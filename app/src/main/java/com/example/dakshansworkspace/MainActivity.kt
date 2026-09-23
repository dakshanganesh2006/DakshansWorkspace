package com.example.dakshansworkspace

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.dakshansworkspace.model.Task
import com.example.dakshansworkspace.ui.LoginActivity
import com.example.dakshansworkspace.ui.TaskAdapter
import com.google.firebase.auth.FirebaseAuth
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.dakshansworkspace.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.app.DatePickerDialog
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        if (auth.currentUser == null) {
            navigateToLogin()
            return
        }

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            auth.signOut()
            navigateToLogin()
        }

        val addTaskFab = findViewById<FloatingActionButton>(R.id.addTaskFab)
        addTaskFab.setOnClickListener {
            showAddTaskDialog()
        }

        // Setup the list
        val tasksRecyclerView = findViewById<RecyclerView>(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = TaskAdapter(emptyList()) { task, isChecked ->
            viewModel.toggleTaskCompletion(task, isChecked)
        }
        tasksRecyclerView.adapter = adapter

        // OPTION A: Swipe to Delete Logic
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // We are not implementing drag-and-drop up/down
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskToDelete = adapter.getTaskAt(position)
                viewModel.deleteTask(taskToDelete) // Tell ViewModel to delete from Firebase
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)

        // OPTION C: Spotlight & Smart List Sorting
        val focusTaskTextView = findViewById<TextView>(R.id.focusTaskTextView)

        viewModel.tasks.observe(this) { liveTaskList ->

            // SORTING ALGORITHM:
            // 1. Incomplete tasks stay at the top. Completed tasks go to the bottom.
            // 2. If tasks are incomplete, sort them by deadline (closest dates first).
            val sortedTasks = liveTaskList.sortedWith(compareBy<Task> { it.isCompleted }.thenBy {
                if (it.deadlineMillis == 0L) Long.MAX_VALUE else it.deadlineMillis
            })

            adapter.updateTasks(sortedTasks)

            // Current focus always grabs the most urgent incomplete task at the very top
            val urgentTask = sortedTasks.firstOrNull { !it.isCompleted }

            if (urgentTask != null) {
                // Update the dark blue card at the top
                focusTaskTextView.text = "${urgentTask.title}\n[Workspace: ${urgentTask.workspace}]"
            } else {
                focusTaskTextView.text = "No active tasks. Time to plan!"
            }
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.taskTitleInput)
        val workspaceInput = dialogView.findViewById<EditText>(R.id.taskWorkspaceInput)
        val deadlineButton = dialogView.findViewById<Button>(R.id.btnSelectDeadline)

        var selectedDeadlineMillis = 0L // Default to 0 if no date is picked

        // When the button is clicked, show the Calendar
        deadlineButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                // Save the exact millisecond timestamp of the chosen day
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDeadlineMillis = selectedCalendar.timeInMillis

                // Update the button text to show the chosen date
                deadlineButton.text = "$dayOfMonth/${month + 1}/$year"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = titleInput.text.toString().trim()
                val workspace = workspaceInput.text.toString().trim()

                if (title.isNotEmpty()) {
                    // Pass the deadline to the ViewModel
                    viewModel.addNewTask(title, if (workspace.isEmpty()) "General" else workspace, selectedDeadlineMillis)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
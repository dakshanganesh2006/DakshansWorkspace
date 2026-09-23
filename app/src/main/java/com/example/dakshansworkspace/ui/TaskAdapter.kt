package com.example.dakshansworkspace.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dakshansworkspace.R
import com.example.dakshansworkspace.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TaskAdapter(
    private var taskList: List<Task>,
    private val onTaskChecked: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.taskTitleTextView)
        val workspaceTextView: TextView = itemView.findViewById(R.id.taskWorkspaceTextView)
        val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.titleTextView.text = task.title
        // Check if a deadline exists. If so, format it and append it to the workspace text.
        if (task.deadlineMillis > 0L) {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dateString = sdf.format(Date(task.deadlineMillis))
            holder.workspaceTextView.text = "${task.workspace} • Due: $dateString"
        } else {
            holder.workspaceTextView.text = task.workspace
        }


        // Detach listener temporarily to prevent accidental triggers while scrolling
        holder.checkBox.setOnCheckedChangeListener(null)

        // Apply the true state from the database
        holder.checkBox.isChecked = task.isCompleted

        // Re-attach listener to catch your physical clicks
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onTaskChecked(task, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun updateTasks(newTasks: List<Task>) {
        taskList = newTasks
        notifyDataSetChanged()
    }

    // Gets the specific task based on its row position for the swipe gesture
    fun getTaskAt(position: Int): Task {
        return taskList[position]
    }
}
package com.example.dakshansworkspace.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dakshansworkspace.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val taskCollection = userId?.let { db.collection("users").document(it).collection("tasks") }

    // Writes a new task to the cloud
    suspend fun addTask(task: Task): Boolean {
        if (taskCollection == null) return false

        return try {
            val documentRef = taskCollection.document()
            val newTask = task.copy(id = documentRef.id)

            documentRef.set(newTask).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Updates the completion status of an existing task
    suspend fun updateTaskStatus(taskId: String, isCompleted: Boolean) {
        try {
            taskCollection?.document(taskId)?.update("isCompleted", isCompleted)?.await()
        } catch (e: Exception) {
            // Handle error silently for now
        }
    }

    // Permanently deletes a task from Firestore
    suspend fun deleteTask(taskId: String) {
        try {
            taskCollection?.document(taskId)?.delete()?.await()
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    // NEW: Reads tasks in real-time from the cloud
    fun getTasksLiveData(): LiveData<List<Task>> {
        val tasksLiveData = MutableLiveData<List<Task>>()

        taskCollection?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val tasks = mutableListOf<Task>()
                for (document in snapshot.documents) {
                    // Convert the raw cloud data back into your Task blueprint
                    val task = document.toObject(Task::class.java)
                    if (task != null) {
                        tasks.add(task)
                    }
                }
                tasksLiveData.value = tasks
            }
        }
        return tasksLiveData
    }
}
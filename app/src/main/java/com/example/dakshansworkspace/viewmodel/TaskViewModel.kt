package com.example.dakshansworkspace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dakshansworkspace.model.Task
import com.example.dakshansworkspace.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()

    // This holds the live stream of tasks flowing from the repository
    val tasks: LiveData<List<Task>> = repository.getTasksLiveData()

    // Function 1: Adding a new task (Now with a deadline!)
    fun addNewTask(title: String, workspace: String, deadlineMillis: Long) {
        val newTask = Task(
            title = title,
            workspace = workspace,
            deadlineMillis = deadlineMillis // Injecting the time here
        )

        viewModelScope.launch {
            repository.addTask(newTask)
        }
    }

    // Function 2: Updating an existing task (Now properly outside of addNewTask!)
    fun toggleTaskCompletion(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            repository.updateTaskStatus(task.id, isChecked)
        }
    }

    // Passes the swipe-to-delete action to the repository
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task.id)
        }
    }
}
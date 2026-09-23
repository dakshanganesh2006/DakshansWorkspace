package com.example.dakshansworkspace.model

import com.google.firebase.firestore.PropertyName

data class Task(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var workspace: String = "College Work",
    var deadlineMillis: Long = 0L,

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false
)
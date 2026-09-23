# Dakshan's Workspace 📱

A cloud-synchronized Android productivity application designed for streamlined task management and daily planning. Built using modern Android development practices, **MVVM architecture**, 
and **Firebase** backend services.

---

## ✨ Features

- **User Authentication:** Secure email/password login and registration powered by Firebase Authentication.
- **Real-Time Synchronization:** Cloud Firestore integration for real-time task creation, updates, and persistence across sessions.
- **Smart Deadline Sorting:** Automatically sorts pending tasks by their upcoming deadlines while pushing completed tasks to the bottom.
- **Interactive Task Controls:**
  - Integrated calendar-based date picker for scheduling deadlines.
  - Smooth **swipe-to-delete** gesture support using Android's `ItemTouchHelper`.
  - Floating action button (FAB) modal dialogs for fast task entry.
- **Modern UI Design:** Clean, dark blue-themed interface with responsive `RecyclerView` layouts.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **UI Components:** Material Design 3, RecyclerView, ViewBinding, ItemTouchHelper
- **Architecture Components:** ViewModel, LiveData / StateFlow
- **Backend & Cloud:** 
  - Firebase Authentication
  - Google Cloud Firestore
- **Build System:** Gradle (Kotlin DSL)

---

## 📂 Project Structure

```text
app/src/main/java/com/example/dakshansworkspace/
├── model/
│   └── Task.kt                  # Data model representing task entities
├── ui/
│   ├── LoginActivity.kt        # User authentication handling
│   ├── MainActivity.kt         # Main dashboard and task list
│   └── TaskAdapter.kt          # RecyclerView adapter with swipe actions
└── viewmodel/
    └── TaskViewModel.kt        # Business logic and Firestore data streams

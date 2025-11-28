FireNotes

A modern offline-first notes app built with Kotlin + Jetpack Compose, featuring secure auth, realtime sync, clean architecture, and smooth UI.

🚀 Tech Stack

Kotlin
Jetpack Compose (Material3)
Hilt (DI)
Room Database
Firebase Authentication
Firebase Realtime Database
DataStore Preferences
Coroutines & Flow

✨ Features
🔒 Authentication
Login & Register with Firebase Authentication

Secure logout
📱 Offline-First Architecture
Notes saved instantly to Room
App works fully without internet
🔄 Two-Way Sync (Room ⇆ Firebase)
Push Sync: Local changes auto-uploaded to Firebase
Pull Sync: Realtime listeners fetch updates instantly
Pull-to-Refresh support for manual retry
Delete sync across devices

🔐 Secure Storage
Room database is wiped on logout (no data leakage)

🎨 Theming
Light/Dark theme toggle
Persisted using DataStore

🧱 Architecture
Clean architecture with reactive Flow-based state management.
UI (Compose)
   ↓
ViewModel
   ↓
UseCases
   ↓
Repository
   ↓
Room  ⇆  Firebase

▶️ How to Run

Clone the repo
Add your google-services.json
Enable Firebase Auth (Email/Password)
Enable Firebase Realtime Database
Build & run the app in Android Studio

🤝 Contributing
Pull requests are welcome. For major changes, please open an issue first.

📄 License
This project is open-source under the MIT License.

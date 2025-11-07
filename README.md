<div align="center">

# 📘 NeuraTask
*Smart Assignment & Task Manager for Android*

<!-- 🖼 Project Logo -->
<img src="https://raw.githubusercontent.com/TanayShah07/NeuraTask/main/app/src/main/res/drawable/logo.png" alt="NeuraTask Logo" width="140" height="140"/>

---

*📱 Android app that helps students and professionals manage assignments efficiently — with intelligent reminders, real-time Firebase sync, and offline support.*

</div>

---

## 🚀 Overview

*NeuraTask* is an Android application built to help users manage assignments and tasks efficiently.  
It focuses on a *clean interface, **intelligent reminders, and **real-time synchronization* using *Firebase*.

This project was developed as part of an *academic course*, blending productivity, usability, and technical depth.

---

## ✨ Features

### 🗂 Task Management
- Add assignments with *title, **description, **subject, **estimated time, and **due date*.
- Tasks are stored *locally (Room)* and synced with *Firebase Firestore* for backup.

### 🔔 Smart Notifications
- Sends reminders automatically before due dates:
    - ⏰ 24 hours
    - ⏳ 6 hours
    - 🕐 1 hour
    - ⚡ At deadline
- Notifications appear both *locally* and in a *unified notifications screen*.
- Integrated *FCM + Room* system ensures reliability even offline.

### ☁ Firebase Integration
- 🔐 *Authentication:* Secure login & signup with Firebase Auth.
- 🧠 *Firestore:* Stores users, assignments, and notifications.
- 📩 *Messaging:* Uses FCM for real-time push notifications.

### ⚙ Offline Access
- Powered by *Room Database* for offline storage.
- Automatically syncs changes when reconnected.

### 🕒 Background Scheduling
- Uses *WorkManager* to handle periodic checks and reminders — so deadlines are never missed.

### 🎨 User Experience
- Clean *Material Design UI*
- Password visibility toggles on all Auth screens
- Animated logo & smooth transitions
- Optimized layouts for all screen sizes

---

## 🧩 Tech Stack

| Layer | Tools / Libraries |
|:------|:------------------|
| *Frontend* | Android XML, Material Components |
| *Backend* | Firebase Auth, Firestore, FCM |
| *Local Storage* | Room Database |
| *Background Tasks* | WorkManager |
| *Language* | Java |
| *Architecture* | MVVM |

---

## 🧱 Project Modules

### 🔑 Authentication
Handles signup, login, OTP verification, and password reset using *Firebase Authentication*.

### 🏠 Home & Navigation
HomeActivity provides bottom navigation for:
- Pending Assignments
- Add Assignment
- Profile
- Notifications

### 🔔 Notifications
- Displays *local & FCM notifications*.
- Users can mark notifications as read or clear them.
- Real-time Firestore listener keeps everything synced.

### 🧮 Assignment Creation
- Create assignments manually or with *AIHelper* assistance.
- Computes a *priority score* based on estimated time, subject, and deadline.

---

## ⚙ Setup Instructions

### 1️⃣ Clone the Repository
bash
git clone https://github.com/TanayShah07/NeuraTask.git
2️⃣ Open in Android Studio
Open Android Studio

Go to File → Open

Select the cloned folder

Wait for Gradle sync to finish

3️⃣ Connect to Firebase
Go to Tools → Firebase

Connect your project to Firebase

Download the google-services.json file

Place it inside your app/ directory

4️⃣ Build & Run
Select an emulator or physical device

Click Run ▶

Enjoy your smart assignment tracker 🚀

👥 **Team Members**
| Name             | Role                                                           |
| :--------------- | :------------------------------------------------------------- |
| **Tanay Shah**   | Backend Development, AI Module, Firebase Firestore Integration |
| **Neerav Reddy** | FCM Notifications Integration, Firestore Sync, Backend Logic   |



📜 **License**

This project is intended for educational use.
You may reference or modify it for personal learning or academic projects.


🧭 **Conclusion**

NeuraTask simplifies academic and personal task management through a smart yet lightweight Android experience.
It combines Firebase’s real-time capabilities with Room’s local persistence, helping users stay organized and ahead of their deadlines.


<div align="center">
Made with ❤ by Tanay Shah & Neerav Reddy

</div> 

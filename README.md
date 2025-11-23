Humanness Android – Sample Task App

This project is built as part of the Humanness Android Intern Assignment by Josh Talks.
The application replicates all required flows exactly as described in the PDF, including:

✔ Ambient noise test
✔ Task selection
✔ Text reading task
✔ Image description task
✔ Photo capture task
✔ Task history & analytics
✔ Local data storage

## APK Download
# Download the final APK:
Click Here 👇🏻

https://drive.google.com/file/d/1e8su8H4r2QWVdrcSH6tzIVTUBiyANX47/view?usp=sharing

# Features Included (Fully Implemented)
1. Start Screen

Light/Dark mode toggle
“Start Sample Task” button → navigates to Noise Test (as per assignment)

2. Ambient Noise Level Test

Semi-circular gauge (0–60 dB)
Dynamic needle animation
Uses mic amplitude (smoothed & scaled)
Pass condition: Avg dB < 40
If passed → automatically navigates to Task Selection Screen
If failed → shows message “Move to a quieter place”

3. Task Selection Screen

Options:
Text Reading
Image Description
Photo Capture
Task History

# Text Reading Task

User must press-and-hold the mic button
Only accepts recordings between 10–20 seconds
Audio playback
3 pre-task checkboxes
“Record again” & “Submit”
Saves data into history

# Image Description Task

Shows a sample image
Press-and-hold mic to record 10–20 sec
Preview + playback
Store recording & meta info in task history

# Photo Capture Task

CameraX implementation
Capture → Preview → Retake
Optional audio description (10–20 sec)
Submit saves image + audio + timestamp to history

# Task History Screen

Shows:
Total tasks completed
Total time recorded
List of all tasks
Text/image/record previews
Timestamp & type
Uses local storage (in-memory or local DB as required)

# Tech Stack

Jetpack Compose UI
ViewModel + State management
Navigation Compose
CameraX API
MediaRecorder (audio capture)
Coroutines
Kotlin
Minimal dependencies (clean architecture)

# Project Structure
app/
 └── src/main/java/com/humanness/sample/
      ├── AppRoot.kt
      ├── StartScreen.kt
      ├── NoiseTestScreen.kt
      ├── TaskSelectionScreen.kt
      ├── TextReadingScreen.kt
      ├── ImageDescriptionScreen.kt
      ├── PhotoCaptureScreen.kt
      ├── TaskHistoryScreen.kt
      └── models / utils / storage classes

# How to Build APK (Simple Steps)

In Android Studio:
Go to top menu → Build
Select Generate App Bundles / APKs
Click Generate APKs


# APK will be inside:

app/build/outputs/apk/debug/app-debug.apk
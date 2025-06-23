# Employee Attendance Manager

An Android application built with modern Android development tools to help manage employee attendance records efficiently.

## ✨ Features

-   **Admin Authentication**: Secure registration and login for administrators.
-   **Employee Management**: Add new employees, view a list of all employees, and edit their details.
-   **Dashboard**: A central dashboard to get a quick overview.
    -   **Search**: Easily find employees.
    -   **Quick Attendance**: Mark employees as "Present," "Absent," or "On Leave" directly from the dashboard.
-   **Detailed Employee View**: A tabbed interface for each employee, showing:
    -   **Info**: Basic employee details.
    -   **Attendance**: A detailed log of their attendance history with a status filter.
-   **Attendance Overview**: A comprehensive, filterable view of all attendance records.
    -   Display data in a tabular, scrollable format (employees as rows, dates as columns).
    -   Filter records by **Day**, **Week**, or **Month**.
-   **Editable Attendance**: Update attendance records at any time.

## 🛠️ Tech Stack & Architecture

-   **UI**: Jetpack Compose with Material 3 for a modern, declarative UI.
-   **Architecture**: Model-View-ViewModel (MVVM) to separate business logic from the UI.
-   **Database**: Room DB for persistent local data storage.
-   **Dependency Injection**: Hilt for managing dependencies.
-   **Asynchronous Operations**: Kotlin Coroutines and Flow for managing background threads and data streams.
-   **Navigation**: Jetpack Navigation Component for handling in-app navigation.
-   **Font**: Poppins (via Google Fonts).

## 🚀 Setup and Installation

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/employee-attendance-manager.git
    ```
2.  **Open in Android Studio**:
    -   Open Android Studio (Hedgehog or newer is recommended).
    -   Click on `File` > `Open` and select the cloned project directory.
3.  **Build the project**:
    -   Android Studio will automatically sync the Gradle files.
    -   Once synced, click on `Build` > `Make Project` or run the app on an emulator or a physical device.

---

*This project was collaboratively developed with the help of an AI assistant.* 
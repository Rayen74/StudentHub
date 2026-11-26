# StudentHub — Android Application

StudentHub is a native Android application designed to help university students organize their academic life, manage their user accounts, and maintain a clear overview of their profile and schedule.

---

## Features & Requirements Implemented

1. **User Authentication & Session Persistence**:
   - **Login & Registration**: Secure account creation and authentication stored in a local SQLite database.
   - **Persistent Session**: Keeps the user logged in across application restarts using `SessionManager` (SharedPreferences).
   - **Password Visibility Toggle**: Show/hide password feature on login, registration, and password change screens.

2. **User Profile & Account Management**:
   - **User Profile**: View user details (Name, First Name, Email).
   - **Edit Profile**: Update first and last names dynamically with immediate persistence in SQLite.
   - **Change Password**: Securely update the account password.

3. **User Directory & Search**:
   - **User List**: View all registered users on the device from the local SQLite database.
   - **User Search**: Real-time search filtering users by email address.

4. **Appearance & Theming**:
   - **Dark Mode / Light Mode**: Toggle switch in the home space to switch between themes, with preference persistence.
   - **Professional Color Palette**: Consistent use of primary blue (`#2747C7`), secondary teal (`#14B8A6`), clean backgrounds, and accessible high-contrast text.

5. **Local SQLite Architecture**:
   - Managed via `DatabaseHelper` (SQLiteOpenHelper).
   - Parameterized queries to prevent SQL injection.
   - Email uniqueness enforcement.
   - Database versioning and schema upgrade support.
   - Standalone initialization script [`StudentHub.sql`](file:///C:/Users/User/AndroidStudioProjects/StudentHub/StudentHub.sql).

---

## Project Structure

- `app/src/main/java/com/example/studenthub/`
  - `LoginActivity.java`: Authentication screen with auto-login support.
  - `RegisterActivity.java`: Account creation screen with validation.
  - `HomeActivity.java`: Personal dashboard with profile management, dark mode toggle, and navigation.
  - `UserListActivity.java`: Registered users directory and real-time email search.
  - `DatabaseHelper.java`: SQLite database helper and CRUD operations.
  - `SessionManager.java`: SharedPreferences session and theme persistence.
  - `User.java`: User entity model.
- `app/src/main/res/`
  - `layout/`: XML layouts for login, register, home, user list, and dialogs.
  - `values/colors.xml`: Centralized color definitions.
  - `raw/welcome.json`: Lottie animation asset.
- [`StudentHub.sql`](file:///C:/Users/User/AndroidStudioProjects/StudentHub/StudentHub.sql): Standalone SQLite schema initialization script.

---

## How to Run the Application

1. Open the project in **Android Studio**.
2. Sync the project with Gradle (`File > Sync Project with Gradle Files`).
3. Connect an Android device or start an Android Emulator (API 24+).
4. Click **Run** (`Shift + F10`) to build and deploy the app.

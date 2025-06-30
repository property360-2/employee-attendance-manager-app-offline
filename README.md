# Employee Attendance Manager 📱

A modern Android application built with **Jetpack Compose**, **MVVM architecture**, and **Room database** to efficiently manage employee attendance records with enhanced security and user experience.

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Room_Database-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/training/data-storage/room)

## ✨ Features

### 🔐 **Authentication & Security**
- **Secure Admin Authentication**: Registration and login with password hashing
- **Biometric Authentication**: Fingerprint/face unlock support
- **Session Management**: Secure session handling with automatic logout
- **Password Change**: Secure password update functionality
- **Input Validation**: Comprehensive validation for all user inputs
- **Account Recovery**: Password reset via username and email verification

### 📊 **Dashboard & Analytics**
- **Real-time Statistics**: Live attendance overview with present/absent/leave counts
- **Attendance Rate**: Percentage-based attendance tracking
- **Quick Filters**: Today, This Week, This Month views
- **Advanced Search**: Real-time employee search with filters
- **Color-coded Status**: Visual indicators for attendance status
- **Today's Summary**: Quick overview cards with key metrics
- **Employee Avatars**: Visual identification with initials/avatars

### 👥 **Employee Management**
- **Add New Employees**: Comprehensive employee registration with validation
- **Edit Employee Details**: Update employee information seamlessly
- **Employee List**: Enhanced list view with search and filters
- **Employee Details**: Detailed employee profiles with attendance history
- **Quick Actions**: Fast attendance marking directly from the dashboard
- **Bulk Operations**: Mark attendance for multiple employees

### 📈 **Attendance Tracking**
- **Quick Marking**: Mark attendance as "Present," "Absent," or "On Leave"
- **Daily Tracking**: Track attendance with actual timestamps
- **Attendance History**: Detailed attendance records with filtering
- **Editable Records**: Update attendance status at any time
- **Visual Status**: Color-coded attendance status indicators
- **Export Data**: Export attendance data to CSV format

### ⚙️ **Settings & Customization**
- **Theme Management**: System/Light/Dark mode support with persistence
- **Data Import/Export**: Import data from files and export to CSV
- **Cloud Backup**: Google Drive/Dropbox integration
- **Help & Support**: Contact information and FAQ
- **Terms & Privacy**: Terms and Conditions and Privacy Policy pages
- **Data Retention**: Automatic data cleanup policies
- **Notification Preferences**: Configure push notification settings

### 📱 **User Experience**
- **Modern UI**: Material 3 design with beautiful animations
- **Responsive Layout**: Optimized for different screen sizes
- **Loading States**: Skeleton screens and progress indicators
- **Error Handling**: User-friendly error messages and recovery options
- **Navigation**: Intuitive bottom navigation with proper state management
- **Onboarding**: Interactive tutorial for first-time users

## 🛠️ Tech Stack & Architecture

### **Frontend**
- **Jetpack Compose**: Modern declarative UI framework
- **Material 3**: Latest Material Design components and theming
- **Navigation**: Jetpack Navigation Component with bottom navigation
- **State Management**: Reactive UI with StateFlow and Compose state

### **Backend & Data**
- **Room Database**: Local SQLite database with Room ORM
- **DataStore**: Secure preferences management
- **Hilt**: Dependency injection for clean architecture
- **Coroutines & Flow**: Asynchronous programming and reactive streams

### **Architecture**
- **MVVM Pattern**: Model-View-ViewModel architecture
- **Repository Pattern**: Clean data access layer
- **Use Cases**: Business logic separation
- **Clean Architecture**: Separation of concerns

### **Additional Libraries**
- **ThreeTenABP**: Date/time handling
- **WorkManager**: Background task management
- **Biometric**: Biometric authentication support
- **Notifications**: Push notification system

## 🚀 Setup and Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/property360-2/employee-attendance-manager.git
   ```

2. **Open in Android Studio**:
   - Open Android Studio (Hedgehog or newer is recommended)
   - Click on `File` > `Open` and select the cloned project directory

3. **Build the project**:
   - Android Studio will automatically sync the Gradle files
   - Once synced, click on `Build` > `Make Project`
   - Run the app on an emulator or a physical device

## 🔧 Configuration

### **Database Setup**
The app automatically handles database setup and migrations. No manual configuration required.

### **Security Settings**
- Passwords must be at least 8 characters long
- Usernames must be at least 3 characters
- Account lockout protection after failed attempts
- Secure session management

### **Permissions**
- Storage access for data import/export
- Biometric authentication (optional)
- Notification permissions

## 📱 Screenshots

*[Screenshots will be added here]*

## 🔒 Security Features

### **Authentication Security**
- Secure password hashing
- Account lockout protection
- Session timeout management
- Biometric authentication support

### **Data Protection**
- Local database encryption
- Secure preferences storage
- Input validation and sanitization
- Error handling without data exposure

## 🚧 Future Enhancements

### **Planned Features**
- [ ] QR Code Attendance: Scan-based attendance marking
- [ ] Geolocation: Location-based attendance verification
- [ ] Time Tracking: Detailed time in/out tracking
- [ ] Leave Management: Leave request and approval system
- [ ] Payroll Integration: Basic payroll calculations
- [ ] Advanced Analytics: Interactive charts and reports
- [ ] Team Management: Department and team organization
- [ ] Shift Management: Multiple shift support
- [ ] Offline Support: Offline mode with sync
- [ ] API Integration: Connect to external services
- [ ] Widget Support: Home screen widgets

### **Technical Improvements**
- [ ] Unit and integration tests
- [ ] Performance optimization
- [ ] Accessibility improvements
- [ ] UI/UX enhancements
- [ ] Code documentation
- [ ] CI/CD pipeline

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**property360-2**
- GitHub: [@property360-2](https://github.com/property360-2)

## 🙏 Acknowledgments

- **Jetpack Compose** team for the amazing UI framework
- **Material Design** team for the design system
- **Android Developer** community for continuous support
- **AI Assistant** for collaborative development

---

*This project was collaboratively developed with the help of an AI assistant, demonstrating modern Android development best practices and security standards.* 
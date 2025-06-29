# Employee Attendance Manager

An Android application built with modern Android development tools to help manage employee attendance records efficiently.

## ✨ Features

### 🔐 **Enhanced Security**
- **Secure Password Hashing**: Passwords are now hashed using BCrypt for enhanced security
- **Account Lockout Protection**: Automatic account lockout after 5 failed login attempts (15-minute lockout)
- **Password Validation**: Strong password requirements with real-time validation
- **Username Validation**: Secure username format requirements
- **Email Validation**: Proper email format validation
- **Session Management**: Secure session handling with token expiration
- **Forgot Password**: Password reset functionality via email

### 👤 **Authentication & User Management**
- **Admin Authentication**: Secure registration and login for administrators
- **Password Visibility Toggle**: Show/hide password for better UX
- **Loading States**: Visual feedback during authentication processes
- **Error Handling**: Comprehensive error messages and validation feedback
- **Account Recovery**: Password reset via username and email verification

### 📊 **Enhanced Dashboard**
- **Real-time Statistics**: Live attendance overview with present/absent/leave counts
- **Attendance Rate**: Percentage-based attendance tracking
- **Visual Indicators**: Color-coded status buttons and progress indicators
- **Empty States**: Helpful messages when no data is available
- **Search Functionality**: Real-time employee search with improved UX

### 👥 **Employee Management**
- **Add New Employees**: Comprehensive employee registration with validation
- **Edit Employee Details**: Update employee information seamlessly
- **Employee List**: Enhanced list view with additional details
- **Employee Details**: Detailed employee profiles with attendance history
- **Quick Actions**: Fast attendance marking directly from the dashboard

### 📈 **Attendance Tracking**
- **Quick Marking**: Mark attendance as "Present," "Absent," or "On Leave" directly from the dashboard
- **Daily Tracking**: Track attendance for each day with timestamp
- **Attendance History**: Detailed attendance records with filtering options
- **Editable Records**: Update attendance status at any time
- **Visual Status**: Color-coded attendance status indicators

### ⚙️ **Settings & Preferences**
- **Dark Mode Toggle**: Switch between light and dark themes
- **Notification Settings**: Configure push notification preferences
- **Biometric Authentication**: Optional fingerprint/face unlock support
- **Auto Backup**: Automatic data backup configuration
- **Data Export**: Export attendance data to CSV format
- **Data Import**: Import data from external files
- **Manual Backup**: Create on-demand data backups
- **Security Settings**: Configure security preferences
- **App Information**: Version details and support information

### 📱 **User Experience**
- **Modern UI**: Material 3 design with beautiful animations
- **Responsive Layout**: Optimized for different screen sizes
- **Accessibility**: Screen reader support and proper focus management
- **Loading States**: Skeleton screens and progress indicators
- **Error Handling**: User-friendly error messages and recovery options
- **Navigation**: Intuitive bottom navigation with proper state management

## 🛠️ Tech Stack & Architecture

### **Frontend**
- **Jetpack Compose**: Modern declarative UI framework
- **Material 3**: Latest Material Design components and theming
- **Navigation**: Jetpack Navigation Component with bottom navigation
- **State Management**: Reactive UI with StateFlow and Compose state

### **Backend & Data**
- **Room Database**: Local SQLite database with Room ORM
- **DataStore**: Secure preferences management
- **Encryption**: Database encryption with SQLCipher
- **Migration**: Automatic database schema migration

### **Security**
- **BCrypt**: Secure password hashing
- **Account Lockout**: Brute force protection
- **Input Validation**: Comprehensive validation for all user inputs
- **Session Management**: Secure session handling

### **Architecture**
- **MVVM Pattern**: Model-View-ViewModel architecture
- **Dependency Injection**: Hilt for managing dependencies
- **Coroutines & Flow**: Asynchronous programming and reactive streams
- **Repository Pattern**: Clean data access layer

### **Additional Libraries**
- **ThreeTenABP**: Date/time handling
- **WorkManager**: Background task management
- **Biometric**: Biometric authentication support
- **Notifications**: Push notification system

## 🚀 Setup and Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/employee-attendance-manager.git
   ```

2. **Open in Android Studio**:
   - Open Android Studio (Hedgehog or newer is recommended)
   - Click on `File` > `Open` and select the cloned project directory

3. **Build the project**:
   - Android Studio will automatically sync the Gradle files
   - Once synced, click on `Build` > `Make Project`
   - Run the app on an emulator or a physical device

## 🔧 Configuration

### **Database Migration**
The app automatically handles database migrations. If you're upgrading from a previous version, the app will migrate your data automatically.

### **Security Settings**
- Passwords must be at least 8 characters long
- Passwords must contain uppercase, lowercase, number, and special character
- Usernames must be at least 3 characters and contain only letters, numbers, and underscores
- Account lockout occurs after 5 failed login attempts

### **Notifications**
- Create notification channels automatically
- Configure notification preferences in Settings
- Support for attendance reminders and backup notifications

## 📱 Screenshots

*[Screenshots will be added here]*

## 🔒 Security Features

### **Password Security**
- BCrypt hashing with salt
- Strong password requirements
- Account lockout protection
- Secure session management

### **Data Protection**
- Database encryption
- Secure preferences storage
- Input validation and sanitization
- Error handling without data exposure

### **Authentication**
- Multi-factor authentication support
- Biometric authentication
- Session timeout
- Secure logout

## 🚧 Future Enhancements

### **Planned Features**
- [ ] Offline support with sync
- [ ] Cloud backup integration
- [ ] Advanced analytics and reporting
- [ ] Employee photo management
- [ ] Department and role management
- [ ] Holiday calendar integration
- [ ] Advanced filtering and sorting
- [ ] Bulk operations
- [ ] Export to multiple formats (PDF, Excel)
- [ ] Real-time collaboration
- [ ] API integration
- [ ] Multi-language support

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

## 🙏 Acknowledgments

- **Jetpack Compose** team for the amazing UI framework
- **Material Design** team for the design system
- **Android Developer** community for continuous support
- **AI Assistant** for collaborative development

---

*This project was collaboratively developed with the help of an AI assistant, demonstrating modern Android development best practices and security standards.* 
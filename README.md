# ⚡ TaskMatrix — Smart Employee Task & HR Management System

TaskMatrix is a modern, full-stack HR & Task Management Portal built with **Java Spring Boot**, **MySQL**, and **Vanilla Web Technologies (HTML5, CSS3, JavaScript)**. It features a sleek glassmorphic UI, real-time metrics, role-based access control (Admin & Employee), leave management, and 2-step **Gmail SMTP OTP Authentication**.

---

## 🌟 Key Features

- 🔐 **2-Step OTP Authentication**: Secure login flow using 6-digit OTP codes sent directly via Gmail SMTP.
- 👑 **Admin Portal**:
  - Dashboard analytics (Total tasks, pending tasks, in-progress tasks, and employee leave requests).
  - Employee Management (View directory, create employees/admins, delete employees).
  - Task Creation & Assignment (Set priorities, assign tasks to specific employees, set deadlines).
  - Leave Request Approvals (Approve or reject employee leave applications).
- 🧑‍💻 **Employee Portal**:
  - View assigned tasks and update task progress (`PENDING`, `IN PROGRESS`, `COMPLETED`).
  - Submit leave requests with custom start/end dates and reasons.
  - View company announcements and profile details.
- 🎨 **Modern Design**: Built with custom HSL CSS color tokens, glassmorphism, dynamic transitions, micro-animations, and responsive tables.

---

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3, Spring Data JPA, Spring Mail
- **Database**: MySQL Server (or H2 in-memory fallback)
- **Frontend**: HTML5, Vanilla CSS3 (Custom Design System), JavaScript (ES6+), FontAwesome 6, Google Fonts (Plus Jakarta Sans)
- **Build Tool**: Maven

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK 17+)**
- **Maven**
- **MySQL Database Server** (running locally on port `3306`)

### Database Setup
1. Create a MySQL database named `task_management_db`:
   ```sql
   CREATE DATABASE task_management_db;
   ```
2. Verify credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/task_management_db?createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

### Running the Application
1. Clone the repository:
   ```bash
   git clone https://github.com/Yashicasri-SP/TaskMatrix.git
   cd TaskMatrix
   ```
2. Build and run using Maven:
   ```bash
   mvn spring-boot:run
   ```
3. Access the web application in your browser at:
   👉 **`http://localhost:8080`**

---

## 🔑 Default Credentials (Seed Data)

The application automatically seeds initial user profiles on startup (`data.sql`):

| Role | Name | Email / Username | Password |
|---|---|---|---|
| **Admin** | System Administrator | `yashicasp422@gmail.com` | `admin123` |
| **Employee** | Rahul Sharma | `rahul@tcs.com` | `emp123` |
| **Employee** | Priya Patel | `priya@tcs.com` | `emp123` |

> ℹ️ *Note: Logging in requires an active internet connection to deliver the 6-digit OTP code to the registered email address via Gmail SMTP.*

---

## 📂 Project Structure

```
TaskMatrix/
├── src/
│   ├── main/
│   │   ├── java/com/tcs/taskmanagement/
│   │   │   ├── controller/      # Auth, Admin, and Employee REST Controllers
│   │   │   ├── dto/             # Request & Response Data Transfer Objects
│   │   │   ├── exception/       # Global Exception Handler
│   │   │   ├── model/           # JPA Entities (Employee, Task, LeaveRequest)
│   │   │   ├── repository/      # Spring Data JPA Repositories
│   │   │   └── service/         # Business Logic & OtpService (Gmail SMTP)
│   │   └── resources/
│   │       ├── static/          # Single Page Application Frontend (index.html, style.css, app.js)
│   │       ├── application.properties
│   │       └── data.sql         # Seed data script
├── pom.xml
└── README.md
```

---

## 📝 License
This project is created for technical portfolio and interview presentation purposes.

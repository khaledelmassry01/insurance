# 🏥 Insurance Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-brightgreen?style=for-the-badge&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Security-red?style=for-the-badge&logo=jsonwebtokens)

**A comprehensive and modern insurance management system built with cutting-edge technologies**

[Features](#-features) • [Technologies](#️-technologies-used) • [Quick Start](#-quick-start) • [API Documentation](#-api-endpoints) • [Contributing](#-contributing)

</div>

---

## 📋 Overview

A comprehensive insurance management system that provides complete solutions for managing insurance companies, including policyholder management, agents, claims, payments, and detailed reports. The system is built on RESTful API architecture with an interactive frontend interface.

### 🎯 System Objectives

- **Comprehensive Management**: Complete control of all insurance operations from one place
- **High Security**: Advanced authentication system using JWT
- **User Friendly**: Simple and easy-to-use user interface
- **Detailed Reports**: Comprehensive reports on all operations
- **Scalability**: Flexible architecture that allows for development and expansion

---

## ✨ Features

### 👥 User Management

#### 🔐 Authentication & Authorization System
- **Secure Login** for agents and policyholders
- **JWT Authentication** to protect all endpoints
- **Multi-level Permissions** (Admin, Agent, Policyholder)
- **Session Management** with automatic expiration

#### 👨‍💼 Agent Management
- Add, edit, and delete agent accounts
- Assign permissions and roles
- Track agent performance
- View active and inactive agents

#### 👤 Policyholder Management
- Register new policyholders
- Update personal information
- View complete details for each policyholder
- Advanced search and filtering

### 📄 Policy Management

#### 📑 Insurance Policies
- **Create new policies** with different types (Health, Life, Auto, Home)
- **Activate and deactivate policies**
- **Automatic renewal** for policies
- **Calculate premiums** based on coverage type
- **Track policy status** (Active, Expired, Suspended)

#### 🛡️ Insurance Coverages
- Manage different types of coverages
- Define coverage limits and premiums
- Link coverages to policies
- Display coverage details for each policy

### 💰 Claims Management

#### 📝 Claims Processing
- **Submit new claims** by policyholders
- **Track claim status** (Under Review, Approved, Rejected, In Payment)
- **Review claims** by agents
- **Approve or reject claims** with justification
- **Detailed log** of all changes to the claim

#### 📊 Claims Tracking
- Comprehensive dashboard for all claims
- Filter by status and date
- Real-time notifications for claim updates
- Detailed reports on claims

### 💳 Payment Management

#### 💵 Payment Processing
- **Record payments** (Credit Card, Bank Transfer, Cash)
- **Track payment status** (Pending, Completed, Failed, Refunded)
- **Complete payment history** for each policy
- **Payment alerts** for due premiums

#### 📈 Financial Reports
- Daily and monthly payment reports
- Revenue analysis by policy type
- Track overdue payments
- Comprehensive financial statistics

### 📊 Dashboard & Reports

#### 📱 Interactive Dashboard
- **Real-time statistics** on all operations
- **Illustrative charts** and graphs
- **Quick numbers** (Policy count, Claims, Payments)
- **Smart alerts** for important events

#### 📑 Detailed Reports
- Reports on active and expired policies
- Claims reports by status
- Payment and revenue reports
- Agent performance reports
- Policyholder reports
- Export reports capability (PDF, Excel)

### 🔔 Notification System

- **Real-time notifications** for important events
- **Email alerts** (can be activated)
- **Payment notifications** for due premiums
- **Instant claim updates**

---

## 🛠️ Technologies Used

### Backend Technologies

| Technology | Version | Description |
|---------|---------|-------|
| **Java** | 17 | Core programming language |
| **Spring Boot** | 3.2.3 | Main framework |
| **Spring Security** | 3.2.3 | Security and authentication |
| **Spring Data JPA** | 3.2.3 | Database interaction |
| **Hibernate** | 6.x | ORM Framework |
| **MySQL** | 8.0+ | Database |
| **JWT** | 0.11.5 | JSON Web Tokens for authentication |
| **Maven** | 3.8+ | Project and dependency management |
| **Lombok** | Latest | Code simplification |
| **Bean Validation** | 3.x | Data validation |
| **SpringDoc OpenAPI** | 2.x | API documentation |

### Frontend Technologies

| Technology | Description |
|---------|-------|
| **HTML5** | Page structure |
| **CSS3** | Interface styling and design |
| **JavaScript (ES6+)** | Interactive programming |
| **Bootstrap** | CSS framework |
| **Font Awesome** | Icons |
| **Chart.js** | Charts and graphs |

### Development Tools

- **Git** - Version control system
- **Postman** - API testing
- **IntelliJ IDEA** - Development environment
- **MySQL Workbench** - Database management

---

## 🚀 Quick Start

### Prerequisites

Before starting, make sure you have installed:

- ☑️ **Java Development Kit (JDK) 17** or newer
- ☑️ **Maven 3.8+** for project management
- ☑️ **MySQL 8.0+** database
- ☑️ **Git** to get the code

### Installation Steps

#### 1️⃣ Clone the Project

```bash
git clone https://github.com/khaledelmassry01/insurance.git
cd insurance
```

#### 2️⃣ Setup Database

Create a new MySQL database:

```sql
CREATE DATABASE insurance_company_database;
```

Or use the included script:

```bash
mysql -u root -p < database/schema.sql
```

#### 3️⃣ Configure Settings File

Open `src/main/resources/application.properties` and modify database settings:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/insurance_company_database
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=your-secret-key-here-make-it-very-long-and-secure
jwt.expiration=216000

# Server Port
server.port=8080
```

#### 4️⃣ Build the Project

```bash
mvn clean install
```

#### 5️⃣ Run the Application

```bash
mvn spring-boot:run
```

Or after building:

```bash
java -jar target/insurance-0.0.1-SNAPSHOT.jar
```

#### 6️⃣ Access the Application

- **Frontend Interface**: [http://localhost:8080](http://localhost:8080)
- **API Documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Actuator Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 📚 API Endpoints

### 🔐 Authentication APIs

#### Agent Authentication

```http
POST /api/auth/agent/login
Content-Type: application/json

{
  "username": "agent01",
  "password": "password123"
}
```

#### Policyholder Authentication

```http
POST /api/auth/policyholder/login
Content-Type: application/json

{
  "username": "policyholder01",
  "password": "password123"
}
```

### 👥 Policyholder Management APIs

#### Get All Policyholders

```http
GET /api/policyholders
Authorization: Bearer {jwt_token}
```

#### Get Policyholder by ID

```http
GET /api/policyholders/{id}
Authorization: Bearer {jwt_token}
```

#### Create Policyholder

```http
POST /api/policyholders
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "01234567890",
  "address": "Cairo, Egypt",
  "dateOfBirth": "1990-01-01",
  "nationalId": "29001010101010"
}
```

#### Update Policyholder

```http
PUT /api/policyholders/{id}
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

#### Delete Policyholder

```http
DELETE /api/policyholders/{id}
Authorization: Bearer {jwt_token}
```

### 📄 Policy Management APIs

#### Get All Policies

```http
GET /api/policies
Authorization: Bearer {jwt_token}
```

#### Get Policy by ID

```http
GET /api/policies/{id}
Authorization: Bearer {jwt_token}
```

#### Create Policy

```http
POST /api/policies
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "policyholderId": 1,
  "coverageId": 1,
  "policyNumber": "POL-2024-001",
  "startDate": "2024-01-01",
  "endDate": "2025-01-01",
  "premiumAmount": 5000.00,
  "coverageAmount": 100000.00,
  "policyType": "HEALTH"
}
```

#### Update Policy Status

```http
PUT /api/policies/{id}/status
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "status": "ACTIVE"
}
```

### 💰 Claims Management APIs

#### Get All Claims

```http
GET /api/claims
Authorization: Bearer {jwt_token}
```

#### Get Claim by ID

```http
GET /api/claims/{id}
Authorization: Bearer {jwt_token}
```

#### Submit Claim (Policyholder)

```http
POST /api/policyholder/claims
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "policyId": 1,
  "claimAmount": 2000.00,
  "claimDate": "2024-01-15",
  "description": "Car accident",
  "incidentDate": "2024-01-14"
}
```

#### Update Claim Status (Agent)

```http
PUT /api/claims/{id}/status
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "status": "APPROVED",
  "remarks": "Claim approved successfully"
}
```

#### Get Policyholder Claims

```http
GET /api/policyholder/claims
Authorization: Bearer {jwt_token}
```

### 💳 Payment Management APIs

#### Get All Payments

```http
GET /api/payments
Authorization: Bearer {jwt_token}
```

#### Get Payment by ID

```http
GET /api/payments/{id}
Authorization: Bearer {jwt_token}
```

#### Process Payment

```http
POST /api/policyholder/payments
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "policyId": 1,
  "amount": 5000.00,
  "paymentDate": "2024-01-20",
  "paymentMethod": "CREDIT_CARD",
  "transactionReference": "TRX-123456"
}
```

#### Get Policyholder Payments

```http
GET /api/policyholder/payments
Authorization: Bearer {jwt_token}
```

### 🛡️ Insurance Coverage APIs

#### Get All Coverages

```http
GET /api/coverages
Authorization: Bearer {jwt_token}
```

#### Get Coverage by ID

```http
GET /api/coverages/{id}
Authorization: Bearer {jwt_token}
```

#### Create Coverage

```http
POST /api/coverages
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "coverageName": "Comprehensive Health Insurance",
  "description": "Covers all medical expenses",
  "coverageType": "HEALTH",
  "maxCoverageAmount": 500000.00,
  "premiumRate": 0.05
}
```

### 📊 Dashboard & Reports APIs

#### Get Dashboard Statistics

```http
GET /api/dashboard/stats
Authorization: Bearer {jwt_token}
```

#### Get Policies Report

```http
GET /api/reports/policies
Authorization: Bearer {jwt_token}
```

#### Get Claims Report

```http
GET /api/reports/claims
Authorization: Bearer {jwt_token}
```

#### Get Payments Report

```http
GET /api/reports/payments
Authorization: Bearer {jwt_token}
```

#### Get Financial Summary

```http
GET /api/reports/financial-summary
Authorization: Bearer {jwt_token}
```

### 👨‍💼 Agent Management APIs

#### Get All Agents

```http
GET /api/agents
Authorization: Bearer {jwt_token}
```

#### Get Agent by ID

```http
GET /api/agents/{id}
Authorization: Bearer {jwt_token}
```

#### Create Agent

```http
POST /api/agents
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "firstName": "Ahmed",
  "lastName": "Mahmoud",
  "email": "ahmed@insurance.com",
  "phoneNumber": "01012345678",
  "username": "agent01",
  "password": "securePassword123"
}
```

### 👤 Policyholder Profile APIs

#### Get Profile

```http
GET /api/policyholder/profile
Authorization: Bearer {jwt_token}
```

#### Update Profile

```http
PUT /api/policyholder/profile
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "email": "newemail@example.com",
  "phoneNumber": "01098765432",
  "address": "Alexandria, Egypt"
}
```

#### Change Password

```http
POST /api/policyholder/change-password
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "currentPassword": "oldPassword",
  "newPassword": "newSecurePassword123"
}
```

---

## 📂 Project Structure

```
insurance/
│
├── src/
│   ├── main/
│   │   ├── java/com/insurance/
│   │   │   ├── config/               # Application configuration
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   │
│   │   │   ├── controller/           # REST Controllers
│   │   │   │   ├── AgentController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ClaimController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── InsuranceCoverageController.java
│   │   │   │   ├── PaymentInfoController.java
│   │   │   │   ├── PolicyController.java
│   │   │   │   ├── PolicyholderAuthController.java
│   │   │   │   ├── PolicyholderClaimController.java
│   │   │   │   ├── PolicyholderController.java
│   │   │   │   ├── PolicyholderPaymentController.java
│   │   │   │   ├── PolicyholderProfileController.java
│   │   │   │   └── ReportController.java
│   │   │   │
│   │   │   ├── exception/            # Error handling
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── ValidationErrorResponse.java
│   │   │   │
│   │   │   ├── model/                # Data models
│   │   │   │   ├── Agent.java
│   │   │   │   ├── Claim.java
│   │   │   │   ├── ClaimStatus.java
│   │   │   │   ├── ClaimStatusDetails.java
│   │   │   │   ├── InsuranceCoverage.java
│   │   │   │   ├── InsuranceHistory.java
│   │   │   │   ├── PaymentInfo.java
│   │   │   │   ├── PaymentStatus.java
│   │   │   │   ├── PaymentStatusHistory.java
│   │   │   │   ├── Policy.java
│   │   │   │   ├── PolicyStatus.java
│   │   │   │   └── Policyholder.java
│   │   │   │
│   │   │   ├── repository/           # Data Access Layer
│   │   │   │   ├── AgentRepository.java
│   │   │   │   ├── ClaimRepository.java
│   │   │   │   ├── InsuranceCoverageRepository.java
│   │   │   │   ├── PaymentInfoRepository.java
│   │   │   │   ├── PolicyRepository.java
│   │   │   │   └── PolicyholderRepository.java
│   │   │   │
│   │   │   ├── security/             # Security & Authentication
│   │   │   │   ├── AgentUserDetails.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── JwtTokenProvider.java
│   │   │   │
│   │   │   ├── service/              # Business Logic Layer
│   │   │   │   ├── impl/
│   │   │   │   ├── AgentAuthService.java
│   │   │   │   ├── ClaimService.java
│   │   │   │   ├── DashboardService.java
│   │   │   │   ├── InsuranceCoverageService.java
│   │   │   │   ├── PaymentInfoService.java
│   │   │   │   ├── PolicyService.java
│   │   │   │   ├── PolicyholderAuthService.java
│   │   │   │   ├── PolicyholderService.java
│   │   │   │   └── ReportService.java
│   │   │   │
│   │   │   └── InsuranceApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/               # Static files
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   ├── js/
│   │       │   │   ├── api.js
│   │       │   │   ├── auth.js
│   │       │   │   ├── claim-status.js
│   │       │   │   ├── dashboard.js
│   │       │   │   ├── main.js
│   │       │   │   ├── notifications.js
│   │       │   │   ├── policyholder.js
│   │       │   │   └── validation.js
│   │       │   ├── index.html
│   │       │   ├── policies.html
│   │       │   ├── policyholder-claims.html
│   │       │   ├── policyholder-dashboard.html
│   │       │   ├── policyholder-login.html
│   │       │   ├── policyholder-payments.html
│   │       │   ├── policyholders.html
│   │       │   └── reports.html
│   │       │
│   │       └── application.properties
│   │
│   └── test/                         # Tests
│
├── target/                           # Build files
├── pom.xml                          # Maven configuration
├── .gitignore                       # Excluded Git files
└── README.md                        # This file
```

---

## 🗄️ Database

### Data Model (Entity Relationship)

The system contains the following main tables:

#### 📊 Main Tables

1. **agents** - Agent data
   - agent_id (PK)
   - first_name, last_name
   - email, phone_number
   - username, password
   - hire_date, status

2. **policyholders** - Policyholders
   - policyholder_id (PK)
   - first_name, last_name
   - email, phone_number
   - address, date_of_birth
   - national_id
   - username, password

3. **insurance_coverage** - Coverage types
   - coverage_id (PK)
   - coverage_name
   - description
   - coverage_type
   - max_coverage_amount
   - premium_rate

4. **policy** - Insurance policies
   - policy_id (PK)
   - policyholder_id (FK)
   - coverage_id (FK)
   - agent_id (FK)
   - policy_number
   - start_date, end_date
   - premium_amount
   - coverage_amount
   - policy_type

5. **claim** - Claims
   - claim_id (PK)
   - policy_id (FK)
   - claim_number
   - claim_amount
   - claim_date
   - incident_date
   - description
   - status

6. **payment_info** - Payments
   - payment_id (PK)
   - policy_id (FK)
   - amount
   - payment_date
   - payment_method
   - transaction_reference
   - status

7. **policy_status** - Policy statuses
8. **payment_status** - Payment statuses
9. **claim_status** - Claim statuses

### Table Relationships

```
policyholders (1) ----→ (N) policy
insurance_coverage (1) ----→ (N) policy
agents (1) ----→ (N) policy
policy (1) ----→ (N) claim
policy (1) ----→ (N) payment_info
```

---

## 🔒 Security

### Security Strategy

1. **JWT Authentication**
   - All endpoints protected with JWT
   - Token valid for 60 hours by default
   - Refresh token to maintain session

2. **Password Security**
   - Password encryption using BCrypt
   - Strong password policy
   - Ability to change password

3. **Role-Based Access Control**
   - ROLE_ADMIN - Full permissions
   - ROLE_AGENT - Policy and claims management
   - ROLE_POLICYHOLDER - Personal data access only

4. **CORS Configuration**
   - Securely configured
   - Allows only requests from trusted sources

5. **Input Validation**
   - Validation of all inputs
   - Protection from SQL Injection
   - Protection from XSS Attacks

---

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific tests
mvn test -Dtest=PolicyServiceTest

# Run tests with coverage
mvn clean test jacoco:report
```

### Testing API with Postman

1. Import the included Postman collection
2. Set environment variables:
   - `base_url`: http://localhost:8080
   - `jwt_token`: Will be set automatically after login

---

## 📱 Frontend

### Available Pages

#### For Agents

1. **index.html** - Main login page
2. **Dashboard** - Main control panel
3. **policyholders.html** - Policyholder management
4. **policies.html** - Policy management
5. **reports.html** - Reports and statistics

#### For Policyholders

1. **policyholder-login.html** - Login
2. **policyholder-dashboard.html** - Personal dashboard
3. **policyholder-claims.html** - Claims management
4. **policyholder-payments.html** - Payment management

### Interactive Features

- ✅ Single Page Application (SPA) behavior
- ✅ Real-time notifications
- ✅ Responsive design (mobile compatible)
- ✅ Form validation
- ✅ Loading indicators
- ✅ Error handling
- ✅ Chart visualizations

---

## 🌍 Environment Variables

You can customize the application using environment variables:

```bash
# Database Configuration
export DB_URL=jdbc:mysql://localhost:3306/insurance_company_database
export DB_USERNAME=root
export DB_PASSWORD=your_password

# JWT Configuration
export JWT_SECRET=your-very-secure-secret-key
export JWT_EXPIRATION=216000

# Server Configuration
export PORT=8080
```

Or use a `.env` file (not included in Git):

```properties
DB_URL=jdbc:mysql://localhost:3306/insurance_company_database
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your-very-secure-secret-key
JWT_EXPIRATION=216000
PORT=8080
```

---

## 🐛 Troubleshooting

### Common Issues and Solutions

#### 1. Database Connection Error

```
Error: Unable to connect to database
```

**Solution:**
- Make sure MySQL Server is running
- Check connection credentials in application.properties
- Ensure the database exists

```bash
mysql -u root -p
CREATE DATABASE insurance_company_database;
```

#### 2. JWT Token Error

```
Error: Invalid JWT token
```

**Solution:**
- Make sure to include Bearer Token in Header
- Check token validity (may be expired)
- Login again to get a new token

#### 3. Port Already in Use

```
Error: Port 8080 is already in use
```

**Solution:**
- Change the port in application.properties
- Or stop the program using port 8080

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

#### 4. Maven Build Errors

```
Error: Failed to execute goal
```

**Solution:**
```bash
mvn clean install -U
mvn dependency:purge-local-repository
```

---

## 📈 Performance & Optimizations

### Applied Performance Improvements

1. **Database Connection Pooling**
   - HikariCP for database connection management
   - Maximum pool size: 10
   - Minimum idle: 5

2. **JPA Batch Processing**
   - Batch insert/update for multiple operations
   - Batch size: 50

3. **Caching Strategy**
   - Spring Cache can be enabled
   - Cache for static data

4. **Query Optimization**
   - Use Projections to reduce data transfer
   - Lazy Loading for relationships
   - Index on important columns

---

## 🔄 Future Updates

### Upcoming Features

- [ ] Email notification system
- [ ] Integration with electronic payment gateways
- [ ] Mobile application (Android/iOS)
- [ ] Internal messaging system
- [ ] Export reports in different formats (PDF, Excel)
- [ ] Advanced AI-powered dashboard
- [ ] Multi-level approval system
- [ ] Integration with external services (SMS, Push Notifications)
- [ ] Automatic backup system
- [ ] Multi-language Support

---

## 🤝 Contributing

We welcome your contributions! Please follow these steps:

1. **Fork** the project
2. Create a new **Branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to Branch (`git push origin feature/AmazingFeature`)
5. Open a **Pull Request**

### Contribution Guidelines

- Follow existing code style
- Add tests for new features
- Update documentation when needed
- Make sure there are no errors before submitting

---

## 📄 License

This project is licensed under [MIT License](LICENSE).

```
MIT License

Copyright (c) 2024 Insurance Management System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 👥 Team

### Lead Developer

- **Khaled Elmassry** - [GitHub Profile](https://github.com/khaledelmassry01)

### Contributors

Thank you to all contributors to this project! 🙏

---

## 📞 Support & Contact

### Need Help?

- 📧 **Email**: support@insurance-system.com
- 🐛 **Bug Reports**: [GitHub Issues](https://github.com/khaledelmassry01/insurance/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/khaledelmassry01/insurance/discussions)
- 📖 **Documentation**: [Wiki](https://github.com/khaledelmassry01/insurance/wiki)

### Social Media

- 🔗 [LinkedIn](#)
- 🐦 [Twitter](#)
- 📘 [Facebook](#)

---

## ⭐ Support Us

If you like this project, don't forget to give it a ⭐ on GitHub!

---

## 📊 Stats

![GitHub stars](https://img.shields.io/github/stars/khaledelmassry01/insurance?style=social)
![GitHub forks](https://img.shields.io/github/forks/khaledelmassry01/insurance?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/khaledelmassry01/insurance?style=social)

---

## 🙏 Special Thanks

Special thanks to:

- **Spring Boot Team** - For the amazing framework
- **MySQL Team** - For the reliable database
- **All contributors** to the libraries used
- **Open Source Community** for continuous support

---

<div align="center">

### Made with ❤️ in Egypt

**Insurance Management System** © 2024

[⬆ Back to Top](#-insurance-management-system)

</div>

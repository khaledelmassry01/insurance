# 🏥 Insurance Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-brightgreen?style=for-the-badge&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Security-red?style=for-the-badge&logo=jsonwebtokens)

**نظام إدارة تأمين شامل ومتطور مبني بتقنيات حديثة**

[المميزات](#-المميزات) • [التقنيات المستخدمة](#️-التقنيات-المستخدمة) • [البداية السريعة](#-البداية-السريعة) • [API Documentation](#-api-endpoints) • [المساهمة](#-المساهمة)

</div>

---

## 📋 نظرة عامة

نظام إدارة تأمين متكامل يوفر حلول شاملة لإدارة شركات التأمين، يتضمن إدارة حاملي الوثائق، الوكلاء، المطالبات، المدفوعات، والتقارير التفصيلية. النظام مبني على معمارية RESTful API مع واجهة أمامية تفاعلية.

### 🎯 الهدف من النظام

- **إدارة شاملة**: التحكم الكامل في جميع عمليات التأمين من مكان واحد
- **أمان عالي**: نظام مصادقة متقدم باستخدام JWT
- **سهولة الاستخدام**: واجهة مستخدم بسيطة وسهلة الاستخدام
- **تقارير مفصلة**: تقارير شاملة عن جميع العمليات
- **قابلية التوسع**: معمارية مرنة تسمح بالتطوير والتوسع

---

## ✨ المميزات

### 👥 إدارة المستخدمين

#### 🔐 نظام المصادقة والتفويض
- **تسجيل دخول آمن** للوكلاء وحاملي الوثائق
- **مصادقة JWT** لحماية جميع نقاط الوصول
- **صلاحيات متعددة المستويات** (Admin, Agent, Policyholder)
- **إدارة الجلسات** مع انتهاء صلاحية تلقائي

#### 👨‍💼 إدارة الوكلاء
- إضافة وتعديل وحذف حسابات الوكلاء
- تعيين الصلاحيات والأدوار
- متابعة أداء الوكلاء
- عرض الوكلاء النشطين وغير النشطين

#### 👤 إدارة حاملي الوثائق
- تسجيل حاملي وثائق جدد
- تحديث البيانات الشخصية
- عرض تفاصيل كاملة لكل حامل وثيقة
- البحث والفلترة المتقدمة

### 📄 إدارة الوثائق

#### 📑 الوثائق التأمينية
- **إنشاء وثائق جديدة** بأنواع مختلفة (صحي، حياة، سيارات، منزل)
- **تفعيل وإيقاف الوثائق**
- **تجديد تلقائي** للوثائق
- **حساب الأقساط** بناءً على نوع التغطية
- **متابعة حالة الوثائق** (نشطة، منتهية، معلقة)

#### 🛡️ التغطيات التأمينية
- إدارة أنواع التغطيات المختلفة
- تحديد حدود التغطية والأقساط
- ربط التغطيات بالوثائق
- عرض تفاصيل التغطية لكل وثيقة

### 💰 إدارة المطالبات

#### 📝 معالجة المطالبات
- **تقديم مطالبات جديدة** من قبل حاملي الوثائق
- **تتبع حالة المطالبة** (قيد المراجعة، موافق عليها، مرفوضة، قيد الدفع)
- **مراجعة المطالبات** من قبل الوكلاء
- **الموافقة أو رفض المطالبات** مع التبرير
- **سجل تفصيلي** لجميع التغييرات على المطالبة

#### 📊 متابعة المطالبات
- Dashboard شامل لجميع المطالبات
- فلترة حسب الحالة والتاريخ
- إشعارات فورية بتحديثات المطالبات
- تقارير تفصيلية عن المطالبات

### 💳 إدارة المدفوعات

#### 💵 معالجة الدفعات
- **تسجيل المدفوعات** (بطاقة ائتمان، تحويل بنكي، نقدي)
- **متابعة حالة الدفع** (معلق، مكتمل، فاشل، مسترد)
- **سجل كامل للمدفوعات** لكل وثيقة
- **تنبيهات الدفع** للأقساط المستحقة

#### 📈 التقارير المالية
- تقارير المدفوعات اليومية والشهرية
- تحليل الإيرادات حسب نوع الوثيقة
- تتبع المدفوعات المتأخرة
- إحصائيات مالية شاملة

### 📊 لوحة التحكم والتقارير

#### 📱 Dashboard التفاعلي
- **إحصائيات فورية** عن جميع العمليات
- **رسوم بيانية** توضيحية
- **أرقام سريعة** (عدد الوثائق، المطالبات، المدفوعات)
- **تنبيهات ذكية** للأحداث الهامة

#### 📑 التقارير المفصلة
- تقارير الوثائق النشطة والمنتهية
- تقارير المطالبات حسب الحالة
- تقارير المدفوعات والإيرادات
- تقارير أداء الوكلاء
- تقارير حاملي الوثائق
- إمكانية تصدير التقارير (PDF, Excel)

### 🔔 نظام الإشعارات

- **إشعارات فورية** للأحداث الهامة
- **تنبيهات البريد الإلكتروني** (قابلة للتفعيل)
- **إشعارات الدفع** للأقساط المستحقة
- **تحديثات المطالبات** الفورية

---

## 🛠️ التقنيات المستخدمة

### Backend Technologies

| التقنية | الإصدار | الوصف |
|---------|---------|-------|
| **Java** | 17 | لغة البرمجة الأساسية |
| **Spring Boot** | 3.2.3 | إطار العمل الرئيسي |
| **Spring Security** | 3.2.3 | نظام الأمان والمصادقة |
| **Spring Data JPA** | 3.2.3 | التعامل مع قاعدة البيانات |
| **Hibernate** | 6.x | ORM Framework |
| **MySQL** | 8.0+ | قاعدة البيانات |
| **JWT** | 0.11.5 | JSON Web Tokens للمصادقة |
| **Maven** | 3.8+ | إدارة المشروع والاعتماديات |
| **Lombok** | Latest | تبسيط الكود |
| **Bean Validation** | 3.x | التحقق من البيانات |
| **SpringDoc OpenAPI** | 2.x | توثيق API |

### Frontend Technologies

| التقنية | الوصف |
|---------|-------|
| **HTML5** | هيكلة الصفحات |
| **CSS3** | تنسيق وتصميم الواجهة |
| **JavaScript (ES6+)** | البرمجة التفاعلية |
| **Bootstrap** | إطار عمل CSS |
| **Font Awesome** | الأيقونات |
| **Chart.js** | الرسوم البيانية |

### Development Tools

- **Git** - نظام التحكم في الإصدارات
- **Postman** - اختبار API
- **IntelliJ IDEA** - بيئة التطوير
- **MySQL Workbench** - إدارة قاعدة البيانات

---

## 🚀 البداية السريعة

### المتطلبات الأساسية

قبل البدء، تأكد من تثبيت:

- ☑️ **Java Development Kit (JDK) 17** أو أحدث
- ☑️ **Maven 3.8+** لإدارة المشروع
- ☑️ **MySQL 8.0+** قاعدة البيانات
- ☑️ **Git** للحصول على الكود

### خطوات التثبيت

#### 1️⃣ استنساخ المشروع

```bash
git clone https://github.com/khaledelmassry01/insurance.git
cd insurance
```

#### 2️⃣ إعداد قاعدة البيانات

قم بإنشاء قاعدة بيانات MySQL جديدة:

```sql
CREATE DATABASE insurance_company_database;
```

أو استخدم السكريبت المرفق:

```bash
mysql -u root -p < database/schema.sql
```

#### 3️⃣ تكوين ملف الإعدادات

افتح ملف `src/main/resources/application.properties` وقم بتعديل إعدادات قاعدة البيانات:

```properties
# إعدادات قاعدة البيانات
spring.datasource.url=jdbc:mysql://localhost:3306/insurance_company_database
spring.datasource.username=your_username
spring.datasource.password=your_password

# إعدادات JWT
jwt.secret=your-secret-key-here-make-it-very-long-and-secure
jwt.expiration=216000

# منفذ الخادم
server.port=8080
```

#### 4️⃣ بناء المشروع

```bash
mvn clean install
```

#### 5️⃣ تشغيل التطبيق

```bash
mvn spring-boot:run
```

أو بعد البناء:

```bash
java -jar target/insurance-0.0.1-SNAPSHOT.jar
```

#### 6️⃣ الوصول إلى التطبيق

- **الواجهة الأمامية**: [http://localhost:8080](http://localhost:8080)
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
  "firstName": "محمد",
  "lastName": "أحمد",
  "email": "mohamed@example.com",
  "phoneNumber": "01234567890",
  "address": "القاهرة، مصر",
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
  "description": "حادث سيارة",
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
  "remarks": "تمت الموافقة على المطالبة"
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
  "coverageName": "تأمين صحي شامل",
  "description": "يغطي جميع المصاريف الطبية",
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
  "firstName": "أحمد",
  "lastName": "محمود",
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
  "firstName": "محمد",
  "lastName": "أحمد",
  "email": "newemail@example.com",
  "phoneNumber": "01098765432",
  "address": "الإسكندرية، مصر"
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

## 📂 هيكل المشروع

```
insurance/
│
├── src/
│   ├── main/
│   │   ├── java/com/insurance/
│   │   │   ├── config/               # إعدادات التطبيق
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
│   │   │   ├── exception/            # معالجة الأخطاء
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── ValidationErrorResponse.java
│   │   │   │
│   │   │   ├── model/                # نماذج البيانات
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
│   │   │   ├── security/             # الأمان والمصادقة
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
│   │       ├── static/               # الملفات الثابتة
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
│   └── test/                         # الاختبارات
│
├── target/                           # ملفات البناء
├── pom.xml                          # إعدادات Maven
├── .gitignore                       # ملفات Git المستبعدة
└── README.md                        # هذا الملف
```

---

## 🗄️ قاعدة البيانات

### نموذج البيانات (Entity Relationship)

النظام يحتوي على الجداول الرئيسية التالية:

#### 📊 الجداول الرئيسية

1. **agents** - بيانات الوكلاء
   - agent_id (PK)
   - first_name, last_name
   - email, phone_number
   - username, password
   - hire_date, status

2. **policyholders** - حاملي الوثائق
   - policyholder_id (PK)
   - first_name, last_name
   - email, phone_number
   - address, date_of_birth
   - national_id
   - username, password

3. **insurance_coverage** - أنواع التغطية
   - coverage_id (PK)
   - coverage_name
   - description
   - coverage_type
   - max_coverage_amount
   - premium_rate

4. **policy** - الوثائق التأمينية
   - policy_id (PK)
   - policyholder_id (FK)
   - coverage_id (FK)
   - agent_id (FK)
   - policy_number
   - start_date, end_date
   - premium_amount
   - coverage_amount
   - policy_type

5. **claim** - المطالبات
   - claim_id (PK)
   - policy_id (FK)
   - claim_number
   - claim_amount
   - claim_date
   - incident_date
   - description
   - status

6. **payment_info** - المدفوعات
   - payment_id (PK)
   - policy_id (FK)
   - amount
   - payment_date
   - payment_method
   - transaction_reference
   - status

7. **policy_status** - حالات الوثائق
8. **payment_status** - حالات المدفوعات
9. **claim_status** - حالات المطالبات

### العلاقات بين الجداول

```
policyholders (1) ----→ (N) policy
insurance_coverage (1) ----→ (N) policy
agents (1) ----→ (N) policy
policy (1) ----→ (N) claim
policy (1) ----→ (N) payment_info
```

---

## 🔒 الأمان

### استراتيجية الأمان

1. **JWT Authentication**
   - جميع endpoints محمية بـ JWT
   - Token صالح لمدة 60 ساعة افتراضياً
   - Refresh token للحفاظ على الجلسة

2. **Password Security**
   - تشفير كلمات المرور باستخدام BCrypt
   - سياسة كلمات مرور قوية
   - إمكانية تغيير كلمة المرور

3. **Role-Based Access Control**
   - ROLE_ADMIN - صلاحيات كاملة
   - ROLE_AGENT - إدارة الوثائق والمطالبات
   - ROLE_POLICYHOLDER - الوصول للبيانات الشخصية فقط

4. **CORS Configuration**
   - مكون بشكل آمن
   - يسمح فقط بالطلبات من مصادر موثوقة

5. **Input Validation**
   - التحقق من جميع المدخلات
   - حماية من SQL Injection
   - حماية من XSS Attacks

---

## 🧪 الاختبار

### تشغيل الاختبارات

```bash
# تشغيل جميع الاختبارات
mvn test

# تشغيل اختبارات محددة
mvn test -Dtest=PolicyServiceTest

# تشغيل الاختبارات مع التغطية
mvn clean test jacoco:report
```

### اختبار API باستخدام Postman

1. قم باستيراد مجموعة Postman المرفقة
2. قم بتعيين المتغيرات البيئية:
   - `base_url`: http://localhost:8080
   - `jwt_token`: سيتم تعيينه تلقائياً بعد تسجيل الدخول

---

## 📱 الواجهة الأمامية

### الصفحات المتاحة

#### للوكلاء (Agents)

1. **index.html** - صفحة تسجيل الدخول الرئيسية
2. **Dashboard** - لوحة التحكم الرئيسية
3. **policyholders.html** - إدارة حاملي الوثائق
4. **policies.html** - إدارة الوثائق
5. **reports.html** - التقارير والإحصائيات

#### لحاملي الوثائق (Policyholders)

1. **policyholder-login.html** - تسجيل الدخول
2. **policyholder-dashboard.html** - لوحة التحكم الشخصية
3. **policyholder-claims.html** - إدارة المطالبات
4. **policyholder-payments.html** - إدارة المدفوعات

### المميزات التفاعلية

- ✅ Single Page Application (SPA) behavior
- ✅ Real-time notifications
- ✅ Responsive design (متوافق مع الهواتف)
- ✅ Form validation
- ✅ Loading indicators
- ✅ Error handling
- ✅ Chart visualizations

---

## 🌍 المتغيرات البيئية

يمكنك تخصيص التطبيق باستخدام المتغيرات البيئية:

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

أو استخدم ملف `.env` (غير مضمن في Git):

```properties
DB_URL=jdbc:mysql://localhost:3306/insurance_company_database
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your-very-secure-secret-key
JWT_EXPIRATION=216000
PORT=8080
```

---

## 🐛 استكشاف الأخطاء

### مشاكل شائعة وحلولها

#### 1. خطأ الاتصال بقاعدة البيانات

```
Error: Unable to connect to database
```

**الحل:**
- تأكد من تشغيل MySQL Server
- تحقق من بيانات الاتصال في application.properties
- تأكد من وجود قاعدة البيانات

```bash
mysql -u root -p
CREATE DATABASE insurance_company_database;
```

#### 2. خطأ JWT Token

```
Error: Invalid JWT token
```

**الحل:**
- تأكد من تضمين Bearer Token في Header
- تحقق من صلاحية التوكن (قد يكون منتهي)
- قم بتسجيل الدخول مجدداً للحصول على token جديد

#### 3. Port Already in Use

```
Error: Port 8080 is already in use
```

**الحل:**
- غير المنفذ في application.properties
- أو أوقف البرنامج الذي يستخدم المنفذ 8080

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

**الحل:**
```bash
mvn clean install -U
mvn dependency:purge-local-repository
```

---

## 📈 الأداء والتحسينات

### تحسينات الأداء المطبقة

1. **Database Connection Pooling**
   - HikariCP لإدارة اتصالات قاعدة البيانات
   - Maximum pool size: 10
   - Minimum idle: 5

2. **JPA Batch Processing**
   - Batch insert/update للعمليات المتعددة
   - Batch size: 50

3. **Caching Strategy**
   - يمكن تفعيل Spring Cache
   - Cache للبيانات الثابتة

4. **Query Optimization**
   - استخدام Projections لتقليل البيانات المنقولة
   - Lazy Loading للعلاقات
   - Index على الأعمدة المهمة

---

## 🔄 التحديثات المستقبلية

### Features القادمة

- [ ] نظام إشعارات البريد الإلكتروني
- [ ] تكامل مع بوابات الدفع الإلكتروني
- [ ] تطبيق موبايل (Android/iOS)
- [ ] نظام الرسائل الداخلية
- [ ] تصدير التقارير بصيغ مختلفة (PDF, Excel)
- [ ] Dashboard متقدم بالذكاء الاصطناعي
- [ ] نظام الموافقات متعدد المستويات
- [ ] تكامل مع خدمات خارجية (SMS, Push Notifications)
- [ ] نظام النسخ الاحتياطي التلقائي
- [ ] Multi-language Support

---

## 🤝 المساهمة

نرحب بمساهماتكم! يرجى اتباع الخطوات التالية:

1. **Fork** المشروع
2. أنشئ **Branch** جديد (`git checkout -b feature/AmazingFeature`)
3. **Commit** التغييرات (`git commit -m 'Add some AmazingFeature'`)
4. **Push** إلى Branch (`git push origin feature/AmazingFeature`)
5. افتح **Pull Request**

### إرشادات المساهمة

- اتبع نمط الكود الموجود
- أضف اختبارات للميزات الجديدة
- حدّث التوثيق عند الحاجة
- تأكد من عدم وجود أخطاء قبل الإرسال

---

## 📄 الترخيص

هذا المشروع مرخص تحت [MIT License](LICENSE).

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

## 👥 الفريق

### المطور الرئيسي

- **Khaled Elmassry** - [GitHub Profile](https://github.com/khaledelmassry01)

### المساهمون

نشكر جميع المساهمين في هذا المشروع! 🙏

---

## 📞 الدعم والاتصال

### هل تحتاج مساعدة؟

- 📧 **Email**: support@insurance-system.com
- 🐛 **Bug Reports**: [GitHub Issues](https://github.com/khaledelmassry01/insurance/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/khaledelmassry01/insurance/discussions)
- 📖 **Documentation**: [Wiki](https://github.com/khaledelmassry01/insurance/wiki)

### وسائل التواصل الاجتماعي

- 🔗 [LinkedIn](#)
- 🐦 [Twitter](#)
- 📘 [Facebook](#)

---

## ⭐ ادعمنا

إذا أعجبك هذا المشروع، لا تنسَ أن تضع ⭐ على GitHub!

---

## 📊 الإحصائيات

![GitHub stars](https://img.shields.io/github/stars/khaledelmassry01/insurance?style=social)
![GitHub forks](https://img.shields.io/github/forks/khaledelmassry01/insurance?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/khaledelmassry01/insurance?style=social)

---

## 🙏 شكر خاص

نتقدم بالشكر لـ:

- **Spring Boot Team** - للإطار الرائع
- **MySQL Team** - لقاعدة البيانات الموثوقة
- **جميع المساهمين** في المكتبات المستخدمة
- **المجتمع المفتوح المصدر** للدعم المستمر

---

<div align="center">

### صنع بـ ❤️ في مصر

**Insurance Management System** © 2024

[⬆ العودة للأعلى](#-insurance-management-system)

</div>

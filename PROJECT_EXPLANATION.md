# 📚 شرح مشروع نظام إدارة التأمين
## Insurance Management System - Complete Code Explanation

---

## 📑 جدول المحتويات

1. [نظرة عامة على المشروع](#نظرة-عامة-على-المشروع)
2. [معمارية المشروع](#معمارية-المشروع)
3. [شرح الطبقات (Layers)](#شرح-الطبقات)
4. [شرح الكود بالتفصيل](#شرح-الكود-بالتفصيل)
5. [سير العمليات الأساسية](#سير-العمليات-الأساسية)
6. [الأمان والحماية](#الأمان-والحماية)
7. [قاعدة البيانات](#قاعدة-البيانات)

---

## 🎯 نظرة عامة على المشروع

### ما هو المشروع؟
نظام إدارة تأمين شامل مبني باستخدام **Spring Boot** يهدف إلى أتمتة وإدارة جميع عمليات شركة التأمين من خلال واجهة ويب تفاعلية.

### الهدف من المشروع
- إدارة حاملي الوثائق (Policyholders)
- إدارة الوثائق التأمينية (Policies)
- معالجة المطالبات (Claims)
- إدارة المدفوعات (Payments)
- توليد التقارير والإحصائيات
- نظام صلاحيات متعدد المستويات

### التقنيات المستخدمة
- **Backend**: Spring Boot 3.2.3, Spring Security, Spring Data JPA
- **Database**: MySQL 8.0
- **Authentication**: JWT (JSON Web Tokens)
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **ORM**: Hibernate
- **Build Tool**: Maven

---

## 🏗️ معمارية المشروع

### 1. **النمط المعماري**
المشروع يتبع نمط **MVC (Model-View-Controller)** مع **Layered Architecture**:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │  ← Controllers
├─────────────────────────────────────────┤
│         Business Logic Layer            │  ← Services
├─────────────────────────────────────────┤
│         Data Access Layer               │  ← Repositories
├─────────────────────────────────────────┤
│         Database Layer                  │  ← MySQL
└─────────────────────────────────────────┘
```

### 2. **الطبقات الرئيسية**

#### **أ. Controller Layer (طبقة التحكم)**
- تستقبل HTTP Requests من المستخدم
- تتعامل مع REST API endpoints
- تقوم بعملية Validation للبيانات المُدخلة
- تُرجع Response للمستخدم

#### **ب. Service Layer (طبقة الخدمات)**
- تحتوي على Business Logic
- تنسق بين عدة Repositories
- تطبق قواعد العمل (Business Rules)
- تتعامل مع Transactions

#### **ج. Repository Layer (طبقة الوصول للبيانات)**
- تتواصل مباشرة مع قاعدة البيانات
- تستخدم JPA/Hibernate
- توفر CRUD operations
- تحتوي على Custom Queries

#### **د. Model Layer (طبقة النماذج)**
- Entity Classes تمثل جداول قاعدة البيانات
- DTOs لنقل البيانات
- تحتوي على Validation Annotations

---

## 📦 شرح الطبقات

### 1️⃣ **Configuration Layer (com.insurance.config)**

#### **SecurityConfig.java**
```
الوظيفة: إعداد الأمان والحماية للتطبيق

المكونات الرئيسية:
- SecurityFilterChain: يحدد مسارات الحماية
- AuthenticationManager: يدير عملية المصادقة
- PasswordEncoder: يشفر كلمات المرور

كيف يعمل:
1. يحدد المسارات المفتوحة (مثل /api/auth)
2. يحدد المسارات المحمية وصلاحياتها
3. يضيف JWT Filter لفحص التوكنات
4. يستخدم BCrypt لتشفير كلمات المرور
```

**مثال من الكود:**
```java
.requestMatchers("/api/auth/**").permitAll()  // مسارات مفتوحة
.requestMatchers("/api/policyholder/**").hasRole("POLICYHOLDER")  // مسارات محمية
```

#### **OpenApiConfig.java**
```
الوظيفة: إعداد Swagger لتوثيق API

ما يفعله:
- يُنشئ صفحة تفاعلية لاختبار API
- يوثق جميع Endpoints
- يضيف معلومات عن المشروع
```

---

### 2️⃣ **Security Layer (com.insurance.security)**

#### **JwtTokenProvider.java**
```
الوظيفة: إدارة JWT Tokens

الوظائف الرئيسية:

1. generateToken():
   - يُنشئ توكن جديد بعد تسجيل الدخول
   - يضع معلومات المستخدم في التوكن (ID, Roles)
   - يحدد وقت انتهاء صلاحية التوكن

2. validateToken():
   - يتحقق من صحة التوكن
   - يفحص التوقيع الرقمي
   - يتأكد من عدم انتهاء الصلاحية

3. getUserIdFromToken():
   - يستخرج معلومات المستخدم من التوكن
   - يُستخدم لمعرفة هوية المستخدم الحالي
```

**كيف يعمل JWT:**
```
User Login → Generate Token → Send to Client
Client Stores Token → Send with Every Request
Server Validates Token → Allow/Deny Access
```

#### **JwtAuthenticationFilter.java**
```
الوظيفة: فلتر يفحص كل طلب HTTP

خطوات العمل:
1. يستخرج التوكن من Header
2. يتحقق من صحة التوكن
3. يحمل معلومات المستخدم
4. يضع المستخدم في Security Context
5. يسمح بمرور الطلب أو يرفضه
```

#### **AgentUserDetails.java**
```
الوظيفة: يمثل معلومات المستخدم (Agent) في Spring Security

يحتوي على:
- Username
- Password
- Authorities (الصلاحيات)
- حالة الحساب (فعال/غير فعال)
```

---

### 3️⃣ **Model Layer (com.insurance.model)**

#### **Agent.java**
```
الوظيفة: يمثل جدول agents في قاعدة البيانات

الحقول الرئيسية:
- agentId: معرف فريد للوكيل
- firstName, lastName: الاسم
- email: البريد الإلكتروني
- username, password: بيانات الدخول
- phoneNumber: رقم الهاتف
- hireDate: تاريخ التعيين
- status: حالة الوكيل (Active/Inactive)

العلاقات:
- يمتلك العديد من Policies
```

#### **Policyholder.java**
```
الوظيفة: يمثل حامل الوثيقة (العميل)

الحقول الرئيسية:
- policyholderId: معرف فريد
- personalInfo: (الاسم، العنوان، تاريخ الميلاد)
- contactInfo: (إيميل، هاتف)
- nationalId: الرقم القومي
- username, password: بيانات الدخول

العلاقات:
- يمتلك العديد من Policies
- يمتلك العديد من Claims
- يمتلك العديد من Payments
```

#### **Policy.java**
```
الوظيفة: يمثل الوثيقة التأمينية

الحقول الرئيسية:
- policyId: معرف الوثيقة
- policyNumber: رقم الوثيقة
- policyType: نوع التأمين (صحي، حياة، سيارات)
- startDate, endDate: فترة التأمين
- premiumAmount: قيمة القسط
- coverageAmount: حد التغطية
- status: حالة الوثيقة

العلاقات:
- تنتمي لـ Policyholder واحد
- تنتمي لـ Agent واحد
- لها InsuranceCoverage واحدة
- لها عدة Claims
- لها عدة Payments
```

#### **Claim.java**
```
الوظيفة: يمثل مطالبة تأمينية

الحقول:
- claimId: معرف المطالبة
- claimNumber: رقم المطالبة
- claimAmount: المبلغ المطالب به
- claimDate: تاريخ تقديم المطالبة
- incidentDate: تاريخ الحادث
- description: وصف المطالبة
- status: حالة المطالبة (Under Review, Approved, Rejected)

العلاقات:
- تنتمي لـ Policy واحدة
```

#### **PaymentInfo.java**
```
الوظيفة: يمثل عملية دفع

الحقول:
- paymentId: معرف الدفعة
- amount: المبلغ
- paymentDate: تاريخ الدفع
- paymentMethod: طريقة الدفع (بطاقة، تحويل، نقدي)
- transactionReference: رقم العملية
- status: حالة الدفع

العلاقات:
- تنتمي لـ Policy واحدة
```

#### **InsuranceCoverage.java**
```
الوظيفة: يمثل نوع التغطية التأمينية

الحقول:
- coverageId: معرف التغطية
- coverageName: اسم التغطية
- coverageType: نوع التغطية
- maxCoverageAmount: الحد الأقصى للتغطية
- premiumRate: معدل القسط
- description: وصف التغطية
```

---

### 4️⃣ **Repository Layer (com.insurance.repository)**

#### **ما هي Repositories؟**
```
الـ Repository هو interface يوفر methods جاهزة للتعامل مع قاعدة البيانات
بفضل Spring Data JPA، لا نحتاج لكتابة SQL بأنفسنا
```

#### **AgentRepository.java**
```java
public interface AgentRepository extends JpaRepository<Agent, Integer> {
    // البحث عن وكيل بالـ username
    Optional<Agent> findByUsername(String username);
    
    // البحث عن وكيل بالإيميل
    Optional<Agent> findByEmail(String email);
    
    // التحقق من وجود username
    boolean existsByUsername(String username);
}
```

**الشرح:**
- `JpaRepository<Agent, Integer>`: يوفر CRUD operations جاهزة
- `Optional<Agent>`: لتجنب null pointer exceptions
- Spring Data JPA يُنشئ الـ SQL تلقائياً من اسم الـ method

#### **PolicyholderRepository.java**
```java
// استعلام مخصص باستخدام @Query
@Query("SELECT p FROM Policyholder p WHERE p.status = :status")
List<Policyholder> findByStatus(@Param("status") String status);
```

#### **ClaimRepository.java**
```java
// البحث عن المطالبات حسب الوثيقة
List<Claim> findByPolicy_PolicyId(Integer policyId);

// البحث عن المطالبات حسب الحالة
List<Claim> findByStatus(String status);
```

#### **PolicyRepository.java**
```java
// استعلام معقد يربط عدة جداول
@Query("SELECT p FROM Policy p " +
       "JOIN p.policyholder ph " +
       "WHERE ph.policyholderId = :policyholderId")
List<Policy> findByPolicyholderId(@Param("policyholderId") Integer id);
```

---

### 5️⃣ **Service Layer (com.insurance.service)**

#### **PolicyholderService.java**
```
الوظيفة: إدارة العمليات المتعلقة بحاملي الوثائق

الـ Methods الرئيسية:

1. getAllPolicyholders():
   - يجلب جميع حاملي الوثائق
   - يُستخدم للعرض في الجداول

2. getPolicyholderById(id):
   - يجلب حامل وثيقة معين
   - يرمي ResourceNotFoundException إذا لم يوجد

3. createPolicyholder(data):
   - ينشئ حامل وثيقة جديد
   - يتحقق من عدم تكرار البيانات
   - يشفر كلمة المرور

4. updatePolicyholder(id, data):
   - يحدث بيانات حامل الوثيقة
   - يتحقق من وجود السجل أولاً

5. deletePolicyholder(id):
   - يحذف حامل الوثيقة
   - يتحقق من عدم وجود وثائق مرتبطة
```

**مثال من الكود:**
```java
public Policyholder createPolicyholder(Policyholder policyholder) {
    // 1. التحقق من عدم وجود username مكرر
    if (policyholderRepository.existsByUsername(policyholder.getUsername())) {
        throw new RuntimeException("Username already exists");
    }
    
    // 2. تشفير كلمة المرور
    policyholder.setPassword(passwordEncoder.encode(policyholder.getPassword()));
    
    // 3. حفظ في قاعدة البيانات
    return policyholderRepository.save(policyholder);
}
```

#### **PolicyService.java**
```
الوظيفة: إدارة الوثائق التأمينية

الـ Methods:

1. createPolicy():
   - ينشئ وثيقة جديدة
   - يربطها بحامل الوثيقة والوكيل
   - يحسب القسط تلقائياً
   - يُنشئ PolicyStatus

2. updatePolicyStatus():
   - يغير حالة الوثيقة (Active/Inactive/Expired)
   - يسجل التغيير في PolicyStatusHistory

3. renewPolicy():
   - يجدد الوثيقة
   - يحدث تاريخ الانتهاء
   - يُنشئ payment جديد للقسط

4. calculatePremium():
   - يحسب قيمة القسط
   - بناءً على نوع التغطية والمبلغ
```

#### **ClaimService.java**
```
الوظيفة: معالجة المطالبات

الـ Methods:

1. submitClaim():
   - يسمح لحامل الوثيقة بتقديم مطالبة
   - يتحقق من صحة الوثيقة
   - يُنشئ رقم مطالبة فريد
   - يضع الحالة "Under Review"

2. processClaim():
   - يراجع المطالبة (بواسطة Agent)
   - يتحقق من صحة المعلومات
   - يقرر الموافقة أو الرفض

3. approveClaim():
   - يوافق على المطالبة
   - يُنشئ payment للمبلغ
   - يرسل إشعار لحامل الوثيقة

4. rejectClaim():
   - يرفض المطالبة
   - يسجل سبب الرفض
   - يرسل إشعار
```

#### **PaymentInfoService.java**
```
الوظيفة: إدارة المدفوعات

الـ Methods:

1. processPayment():
   - يسجل عملية دفع جديدة
   - يتحقق من البيانات
   - يحدث حالة الوثيقة

2. getPaymentsByPolicy():
   - يجلب سجل المدفوعات لوثيقة معينة

3. calculateTotalPaid():
   - يحسب إجمالي المدفوعات
   - لوثيقة أو لحامل وثيقة
```

#### **DashboardService.java**
```
الوظيفة: توفير بيانات Dashboard

الـ Methods:

1. getDashboardStats():
   - إجمالي عدد الوثائق
   - عدد المطالبات المعلقة
   - إجمالي الإيرادات
   - إحصائيات شهرية

2. getChartData():
   - بيانات للرسوم البيانية
   - توزيع الوثائق حسب النوع
   - المطالبات حسب الشهر
```

#### **ReportService.java**
```
الوظيفة: إنشاء التقارير

الـ Methods:

1. generatePolicyReport():
   - تقرير بجميع الوثائق
   - يمكن الفلترة حسب النوع والحالة

2. generateClaimReport():
   - تقرير بالمطالبات
   - مع إحصائيات الموافقة والرفض

3. generatePaymentReport():
   - تقرير بالمدفوعات
   - حسب الفترة الزمنية
```

---

### 6️⃣ **Controller Layer (com.insurance.controller)**

#### **AuthController.java**
```
الوظيفة: التعامل مع عمليات تسجيل الدخول

Endpoints:

POST /api/auth/agent/login
Body: { username, password }
Response: { token, agentInfo }

كيف يعمل:
1. يستقبل username و password
2. يستخدم AuthenticationManager للتحقق
3. إذا صحيح، يُنشئ JWT Token
4. يُرجع التوكن للمستخدم
```

**مثال من الكود:**
```java
@PostMapping("/agent/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
        // 1. التحقق من بيانات الدخول
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), 
                request.getPassword()
            )
        );
        
        // 2. إنشاء التوكن
        String token = jwtTokenProvider.generateToken(auth);
        
        // 3. إرجاع النتيجة
        return ResponseEntity.ok(new AuthResponse(token));
        
    } catch (AuthenticationException e) {
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}
```

#### **PolicyholderController.java**
```
الوظيفة: إدارة حاملي الوثائق

Endpoints:

GET /api/policyholders
- يجلب جميع حاملي الوثائق
- يتطلب صلاحية AGENT أو ADMIN

GET /api/policyholders/{id}
- يجلب حامل وثيقة معين

POST /api/policyholders
- ينشئ حامل وثيقة جديد
- يتحقق من البيانات باستخدام @Valid

PUT /api/policyholders/{id}
- يحدث بيانات حامل الوثيقة

DELETE /api/policyholders/{id}
- يحذف حامل الوثيقة
```

#### **PolicyController.java**
```
الوظيفة: إدارة الوثائق

Endpoints:

GET /api/policies
- جلب جميع الوثائق

POST /api/policies
- إنشاء وثيقة جديدة

PUT /api/policies/{id}/status
- تحديث حالة الوثيقة

GET /api/policies/policyholder/{id}
- جلب وثائق حامل وثيقة معين
```

#### **ClaimController.java**
```
الوظيفة: إدارة المطالبات

Endpoints:

GET /api/claims
- جلب جميع المطالبات

POST /api/claims
- تقديم مطالبة جديدة

PUT /api/claims/{id}/approve
- الموافقة على مطالبة

PUT /api/claims/{id}/reject
- رفض مطالبة
```

#### **PolicyholderAuthController.java**
```
الوظيفة: تسجيل دخول حاملي الوثائق

POST /api/policyholder-auth/login
- تسجيل دخول خاص بحاملي الوثائق
- يُنشئ JWT Token مع ROLE_POLICYHOLDER
```

#### **PolicyholderProfileController.java**
```
الوظيفة: إدارة ملف حامل الوثيقة الشخصي

GET /api/policyholder/profile
- يجلب بيانات الملف الشخصي
- يستخدم @CurrentAgent للحصول على المستخدم الحالي

PUT /api/policyholder/profile
- تحديث البيانات الشخصية

POST /api/policyholder/change-password
- تغيير كلمة المرور
```

#### **DashboardController.java**
```
الوظيفة: توفير بيانات لوحة التحكم

GET /api/dashboard/stats
- إحصائيات عامة

GET /api/dashboard/charts
- بيانات للرسوم البيانية
```

#### **ReportController.java**
```
الوظيفة: إنشاء وتصدير التقارير

GET /api/reports/policies
- تقرير الوثائق

GET /api/reports/claims
- تقرير المطالبات

GET /api/reports/payments
- تقرير المدفوعات
```

---

### 7️⃣ **Exception Handling (com.insurance.exception)**

#### **GlobalExceptionHandler.java**
```
الوظيفة: معالجة الأخطاء بشكل مركزي

يستخدم @ControllerAdvice لالتقاط جميع الأخطاء

الأخطاء التي يعالجها:

1. ResourceNotFoundException:
   - عندما لا يوجد سجل في قاعدة البيانات
   - يُرجع 404 Not Found

2. MethodArgumentNotValidException:
   - عندما تفشل عملية Validation
   - يُرجع 400 Bad Request مع تفاصيل الأخطاء

3. DataIntegrityViolationException:
   - عند محاولة إدخال بيانات مكررة
   - يُرجع 409 Conflict

4. Exception (عام):
   - أي خطأ آخر غير متوقع
   - يُرجع 500 Internal Server Error
```

**مثال:**
```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    ErrorResponse error = new ErrorResponse(
        404,
        ex.getMessage(),
        LocalDateTime.now()
    );
    return ResponseEntity.status(404).body(error);
}
```

#### **ResourceNotFoundException.java**
```
الوظيفة: Exception مخصص عندما لا يوجد سجل

مثال الاستخدام:
throw new ResourceNotFoundException("Policyholder not found with id: " + id);
```

---

## 🔄 سير العمليات الأساسية

### 1. **عملية تسجيل الدخول (Agent Login)**

```
خطوة بخطوة:

1. المستخدم يدخل username و password في الصفحة
   ↓
2. Frontend يرسل POST request إلى /api/auth/agent/login
   ↓
3. AuthController يستقبل الطلب
   ↓
4. AuthenticationManager يتحقق من البيانات
   - يبحث عن Agent في قاعدة البيانات
   - يقارن كلمة المرور (المشفرة)
   ↓
5. إذا صحيح:
   - JwtTokenProvider يُنشئ Token جديد
   - يضع فيه: agentId, username, roles, expiry date
   ↓
6. Controller يُرجع التوكن
   ↓
7. Frontend يحفظ التوكن في localStorage
   ↓
8. Frontend يُرسل التوكن مع كل طلب في Header:
   Authorization: Bearer <token>
```

### 2. **عملية إنشاء وثيقة جديدة (Create Policy)**

```
1. Agent يملأ نموذج إنشاء وثيقة
   - يختار حامل الوثيقة
   - يختار نوع التغطية
   - يحدد المبلغ والتواريخ
   ↓
2. Frontend يرسل POST request إلى /api/policies
   مع JWT Token في Header
   ↓
3. JwtAuthenticationFilter يفحص التوكن
   - يتحقق من صحته
   - يستخرج معلومات Agent
   - يتأكد من الصلاحيات
   ↓
4. PolicyController يستقبل الطلب
   - يتحقق من البيانات (@Valid)
   ↓
5. PolicyService يعالج البيانات:
   - يتحقق من وجود Policyholder
   - يتحقق من وجود Coverage
   - يحسب القسط
   - ينشئ Policy جديد
   - ينشئ PolicyStatus
   ↓
6. PolicyRepository يحفظ في قاعدة البيانات
   ↓
7. Controller يُرجع النتيجة (Policy created)
   ↓
8. Frontend يعرض رسالة نجاح
```

### 3. **عملية تقديم مطالبة (Submit Claim)**

```
1. Policyholder يسجل دخول إلى حسابه
   ↓
2. يفتح صفحة المطالبات
   ↓
3. يملأ نموذج تقديم مطالبة:
   - يختار الوثيقة
   - يدخل المبلغ المطلوب
   - يكتب وصف الحادث
   - يحدد تاريخ الحادث
   ↓
4. Frontend يرسل POST إلى /api/policyholder/claims
   مع JWT Token
   ↓
5. JwtAuthenticationFilter يتحقق من Token
   - يتأكد أن ROLE = POLICYHOLDER
   ↓
6. PolicyholderClaimController يستقبل الطلب
   ↓
7. ClaimService يعالج المطالبة:
   - يتحقق من صحة الوثيقة
   - يتحقق من أن الوثيقة نشطة
   - يتحقق من حدود التغطية
   - ينشئ Claim جديد
   - يضع status = "Under Review"
   - ينشئ رقم مطالبة فريد
   ↓
8. ClaimRepository يحفظ المطالبة
   ↓
9. Controller يُرجع النتيجة
   ↓
10. Frontend يعرض رسالة نجاح
    ويظهر رقم المطالبة
```

### 4. **عملية مراجعة مطالبة (Review Claim)**

```
1. Agent يفتح قائمة المطالبات المعلقة
   ↓
2. يفتح تفاصيل مطالبة معينة
   ↓
3. يراجع المعلومات:
   - تفاصيل الحادث
   - المبلغ المطلوب
   - الوثيقة المرتبطة
   - التغطية
   ↓
4. Agent يقرر: موافقة أو رفض
   ↓
5. إذا موافقة:
   Frontend يرسل PUT إلى /api/claims/{id}/approve
   
   إذا رفض:
   Frontend يرسل PUT إلى /api/claims/{id}/reject
   مع سبب الرفض
   ↓
6. ClaimController يستقبل الطلب
   ↓
7. ClaimService يعالج القرار:
   
   إذا موافقة:
   - يحدث status = "Approved"
   - ينشئ PaymentInfo للمبلغ
   - يسجل في ClaimStatusDetails
   
   إذا رفض:
   - يحدث status = "Rejected"
   - يسجل سبب الرفض
   ↓
8. يُرسل إشعار لحامل الوثيقة
   ↓
9. Controller يُرجع النتيجة
```

### 5. **عملية الدفع (Payment Processing)**

```
1. Policyholder يفتح صفحة المدفوعات
   ↓
2. يختار الوثيقة المراد الدفع لها
   ↓
3. يدخل بيانات الدفع:
   - المبلغ
   - طريقة الدفع
   - رقم العملية/البطاقة
   ↓
4. Frontend يرسل POST إلى /api/policyholder/payments
   ↓
5. PolicyholderPaymentController يستقبل الطلب
   ↓
6. PaymentInfoService يعالج الدفع:
   - يتحقق من الوثيقة
   - يتحقق من المبلغ
   - ينشئ PaymentInfo
   - يحدث PaymentStatus
   - يحدث حالة الوثيقة إذا لزم
   ↓
7. PaymentInfoRepository يحفظ الدفعة
   ↓
8. Controller يُرجع تأكيد الدفع
   ↓
9. Frontend يعرض إيصال الدفع
```

---

## 🔒 الأمان والحماية

### 1. **JWT Authentication Flow**

```
Complete Flow:

1. User Login:
   username + password
   ↓
2. Server validates credentials
   ↓
3. Server generates JWT Token:
   {
     "sub": "agentId",
     "username": "agent01",
     "roles": "ROLE_AGENT",
     "iat": issued_at_time,
     "exp": expiration_time
   }
   ↓
4. Client stores token
   ↓
5. Every request includes token:
   Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
   ↓
6. JwtAuthenticationFilter validates:
   - Token signature
   - Token expiration
   - User exists
   ↓
7. If valid:
   - Load user details
   - Set Authentication in SecurityContext
   - Allow request
   
   If invalid:
   - Return 401 Unauthorized
```

### 2. **Password Security**

```
1. عند التسجيل:
   - المستخدم يدخل كلمة مرور نصية
   - BCryptPasswordEncoder يشفرها
   - تُحفظ المشفرة في قاعدة البيانات
   
   مثال:
   Input: "password123"
   Encoded: "$2a$10$eImiTXuWVxfm2hdSxUlwu.rFh..."

2. عند تسجيل الدخول:
   - المستخدم يدخل كلمة المرور
   - BCrypt يقارنها مع المشفرة
   - بدون فك التشفير
   - يستخدم نفس الخوارزمية
```

### 3. **Role-Based Access Control (RBAC)**

```
الأدوار الموجودة:

1. ROLE_ADMIN:
   - صلاحيات كاملة
   - إدارة الوكلاء
   - عرض جميع البيانات
   - تعديل الإعدادات

2. ROLE_AGENT:
   - إدارة حاملي الوثائق
   - إنشاء وإدارة الوثائق
   - مراجعة المطالبات
   - معالجة المدفوعات
   - عرض التقارير

3. ROLE_POLICYHOLDER:
   - عرض وثائقه الخاصة فقط
   - تقديم مطالبات
   - عرض المطالبات الخاصة به
   - إجراء مدفوعات
   - تحديث معلوماته الشخصية
```

**كيف يتم التحقق:**
```java
// في Controller
@PreAuthorize("hasRole('AGENT')")
public ResponseEntity<?> createPolicy(...) {
    // هذا الـ method يمكن للـ AGENT فقط الوصول إليه
}

// في SecurityConfig
.requestMatchers("/api/policyholder/**").hasRole("POLICYHOLDER")
// كل المسارات تحت /api/policyholder محمية
```

### 4. **Input Validation**

```
استخدام Bean Validation:

في Model:
@NotNull(message = "Email is required")
@Email(message = "Invalid email format")
private String email;

@Size(min = 8, message = "Password must be at least 8 characters")
private String password;

في Controller:
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody PolicyholderDTO dto) {
    // Spring يتحقق من البيانات تلقائياً
    // إذا فشل التحقق، يُرجع 400 Bad Request
}
```

### 5. **CORS Configuration**

```java
// في SecurityConfig
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOrigin("http://localhost:8080");
    config.addAllowedMethod("*");
    config.addAllowedHeader("*");
    config.setAllowCredentials(true);
    return source;
}
```

---

## 🗄️ قاعدة البيانات

### **Schema Overview**

```sql
-- الجداول الرئيسية

agents
├── agent_id (PK)
├── username
├── password (encrypted)
├── first_name
├── last_name
├── email
└── phone_number

policyholders
├── policyholder_id (PK)
├── username
├── password (encrypted)
├── first_name
├── last_name
├── email
├── national_id
└── date_of_birth

insurance_coverage
├── coverage_id (PK)
├── coverage_name
├── coverage_type
├── max_coverage_amount
└── premium_rate

policies
├── policy_id (PK)
├── policy_number (UNIQUE)
├── policyholder_id (FK)
├── agent_id (FK)
├── coverage_id (FK)
├── start_date
├── end_date
├── premium_amount
├── coverage_amount
└── policy_type

claims
├── claim_id (PK)
├── policy_id (FK)
├── claim_number (UNIQUE)
├── claim_amount
├── claim_date
├── incident_date
├── description
└── status

payment_info
├── payment_id (PK)
├── policy_id (FK)
├── amount
├── payment_date
├── payment_method
├── transaction_reference
└── status
```

### **العلاقات (Relationships)**

```
1. One-to-Many Relationships:

Policyholder (1) ──→ (N) Policies
"حامل وثيقة واحد يمكن أن يكون له عدة وثائق"

Agent (1) ──→ (N) Policies
"وكيل واحد يمكن أن يدير عدة وثائق"

Policy (1) ──→ (N) Claims
"وثيقة واحدة يمكن أن يكون لها عدة مطالبات"

Policy (1) ──→ (N) Payments
"وثيقة واحدة يمكن أن يكون لها عدة مدفوعات"

2. Many-to-One Relationships:

(N) Policies ──→ (1) Coverage
"عدة وثائق يمكن أن تستخدم نفس نوع التغطية"
```

### **Hibernate/JPA Annotations**

```java
// One-to-Many
@OneToMany(mappedBy = "policyholder")
private List<Policy> policies;

// Many-to-One
@ManyToOne
@JoinColumn(name = "policyholder_id")
private Policyholder policyholder;

// Cascade Types
cascade = CascadeType.ALL  // عند حذف Parent، احذف Children
orphanRemoval = true       // عند إزالة Child من Collection، احذفه

// Fetch Types
fetch = FetchType.LAZY   // اجلب البيانات عند الحاجة
fetch = FetchType.EAGER  // اجلب البيانات مباشرة
```

---

## 📱 Frontend Integration

### **كيف يتواصل Frontend مع Backend**

```javascript
// 1. Login Function
async function login(username, password) {
    const response = await fetch('/api/auth/agent/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    });
    
    const data = await response.json();
    
    if (response.ok) {
        // حفظ التوكن
        localStorage.setItem('token', data.token);
        // الانتقال للداشبورد
        window.location.href = '/dashboard.html';
    } else {
        alert('Invalid credentials');
    }
}

// 2. API Call with Token
async function fetchPolicyholders() {
    const token = localStorage.getItem('token');
    
    const response = await fetch('/api/policyholders', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    
    const policyholders = await response.json();
    displayPolicyholders(policyholders);
}

// 3. Create New Policy
async function createPolicy(policyData) {
    const token = localStorage.getItem('token');
    
    const response = await fetch('/api/policies', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(policyData)
    });
    
    if (response.ok) {
        alert('Policy created successfully');
    }
}
```

### **Error Handling في Frontend**

```javascript
async function apiCall(url, options) {
    try {
        const response = await fetch(url, options);
        
        if (response.status === 401) {
            // Token منتهي أو غير صحيح
            alert('Session expired, please login again');
            localStorage.removeItem('token');
            window.location.href = '/login.html';
            return;
        }
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message);
        }
        
        return await response.json();
        
    } catch (error) {
        console.error('API Error:', error);
        alert('An error occurred: ' + error.message);
    }
}
```

---

## 🎓 مصطلحات مهمة للمناقشة

### **1. Spring Boot Concepts**

- **Dependency Injection**: Spring ينشئ الكائنات ويحقنها تلقائياً
- **Inversion of Control (IoC)**: Spring يدير دورة حياة الكائنات
- **Bean**: كائن يديره Spring Container
- **Component Scanning**: Spring يبحث عن Classes المُشار إليها بـ @Component

### **2. JPA/Hibernate**

- **Entity**: Class يمثل جدول في قاعدة البيانات
- **Repository**: Interface للتعامل مع قاعدة البيانات
- **Lazy Loading**: تحميل البيانات عند الحاجة فقط
- **Eager Loading**: تحميل البيانات مباشرة
- **Transaction**: مجموعة عمليات تنفذ معاً (كلها أو لا شيء)

### **3. REST API**

- **GET**: جلب بيانات
- **POST**: إنشاء جديد
- **PUT**: تحديث كامل
- **PATCH**: تحديث جزئي
- **DELETE**: حذف
- **Status Codes**:
  - 200: نجاح
  - 201: تم الإنشاء
  - 400: خطأ في البيانات
  - 401: غير مصرح
  - 404: غير موجود
  - 500: خطأ في السيرفر

### **4. Security**

- **Authentication**: التحقق من هوية المستخدم
- **Authorization**: التحقق من صلاحيات المستخدم
- **JWT**: توكن يحتوي على معلومات مشفرة
- **BCrypt**: خوارزمية تشفير كلمات المرور
- **CORS**: السماح للمواقع الأخرى بالوصول للـ API

---

## 💡 نقاط القوة في المشروع

### **1. معمارية نظيفة (Clean Architecture)**
- فصل واضح بين الطبقات
- كل طبقة لها مسؤولية واحدة
- سهولة الصيانة والتطوير

### **2. أمان قوي**
- JWT Authentication
- تشفير كلمات المرور
- Role-Based Access Control
- Input Validation

### **3. قابلية التوسع**
- استخدام Interfaces
- Dependency Injection
- RESTful API Design

### **4. معالجة الأخطاء**
- Global Exception Handler
- رسائل خطأ واضحة
- Status Codes صحيحة

### **5. توثيق جيد**
- Swagger/OpenAPI
- Comments في الكود
- README شامل

---

## 📊 إحصائيات المشروع

```
عدد الملفات:
- Controllers: 13
- Services: 10+
- Repositories: 10+
- Models: 15+
- Configuration: 2
- Security: 4

عدد الـ Endpoints: 40+

قاعدة البيانات:
- Tables: 12+
- Relationships: 15+

التقنيات: 10+
```

---

## 🎯 الأسئلة المتوقعة في المناقشة

### **1. لماذا استخدمت Spring Boot؟**
```
الإجابة:
- يوفر إعدادات جاهزة (Auto-configuration)
- سهل وسريع في التطوير
- مجتمع كبير ودعم قوي
- Integration سهل مع تقنيات أخرى
- Production-ready features (Actuator, Security)
```

### **2. كيف تعمل عملية المصادقة (Authentication)؟**
```
الإجابة:
1. المستخدم يدخل username و password
2. Spring Security يتحقق من البيانات
3. نُنشئ JWT Token يحتوي على معلومات المستخدم
4. المستخدم يحفظ التوكن ويرسله مع كل طلب
5. JwtAuthenticationFilter يفحص التوكن في كل طلب
6. إذا صحيح، نسمح بالوصول، وإلا نرفض
```

### **3. ما الفرق بين Service و Repository؟**
```
الإجابة:
Repository:
- يتعامل مباشرة مع قاعدة البيانات
- CRUD operations فقط
- لا يحتوي على Business Logic

Service:
- يحتوي على Business Logic
- ينسق بين عدة Repositories
- يطبق قواعد العمل
- يدير Transactions
```

### **4. لماذا استخدمت JWT بدلاً من Sessions؟**
```
الإجابة:
مميزات JWT:
- Stateless: السيرفر لا يحتاج لحفظ Sessions
- Scalable: يمكن استخدام عدة Servers
- Mobile-friendly: مناسب لتطبيقات الموبايل
- Cross-domain: يعمل مع مواقع مختلفة
```

### **5. كيف تتعامل مع الأخطاء؟**
```
الإجابة:
- GlobalExceptionHandler يلتقط جميع الأخطاء
- نُرجع Status Code مناسب
- نُرجع رسالة خطأ واضحة
- نسجل الأخطاء في Logs
```

### **6. كيف تضمن أمان البيانات؟**
```
الإجابة:
1. تشفير كلمات المرور (BCrypt)
2. JWT Authentication
3. Role-Based Access Control
4. Input Validation
5. SQL Injection Protection (JPA)
6. CORS Configuration
```

---

## 📖 ملخص للمناقشة

### **النقاط الرئيسية التي يجب ذكرها:**

1. **المشروع يستخدم معمارية Layered Architecture**
   - Controller → Service → Repository → Database

2. **الأمان مبني على JWT + Spring Security**
   - مصادقة قوية
   - نظام صلاحيات

3. **قاعدة البيانات منظمة بعلاقات واضحة**
   - One-to-Many
   - Many-to-One

4. **RESTful API Design**
   - Endpoints واضحة ومنطقية
   - Status Codes صحيحة

5. **Error Handling شامل**
   - رسائل واضحة
   - معالجة مركزية

6. **Frontend Integration**
   - AJAX Calls
   - Token-based Authentication

---

## ✅ Checklist قبل المناقشة

- [ ] فهم معمارية المشروع بشكل عام
- [ ] معرفة دور كل طبقة (Layer)
- [ ] فهم كيفية عمل JWT Authentication
- [ ] معرفة الـ Endpoints الأساسية
- [ ] فهم العلاقات بين الجداول
- [ ] معرفة كيفية معالجة الأخطاء
- [ ] القدرة على شرح سير عملية معينة
- [ ] معرفة التقنيات المستخدمة ولماذا

---

## 🎉 نهاية الشرح

هذا الملف يغطي جميع جوانب المشروع بشكل مبسط ومناسب للمناقشة أمام لجنة.
المفتاح هو فهم الصورة الكبيرة أولاً، ثم التفاصيل.

**نصيحة أخيرة للمناقشة:**
- اشرح بثقة
- استخدم أمثلة عملية
- ارسم diagrams إذا أمكن
- كن مستعداً للأسئلة التقنية
- اربط الأجزاء ببعضها

**بالتوفيق! 🚀**

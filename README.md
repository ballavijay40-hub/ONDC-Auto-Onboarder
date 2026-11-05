# AI-Powered Auto-Onboarder for ONDC (Fashion & Apparel Sellers)

An automated onboarding platform designed to fast-track Fashion and Apparel merchants onto the Open Network for Digital Commerce (ONDC). The platform leverages **Tess4J (Tesseract OCR)** for automated document extraction and **Google Gemini** & **Anthropic Claude** for automated product catalog generation and network compliance auditing.

---

## 🌟 Key Features

1. **Seller Registration & Authentication**
   - Secure merchant registration and login with BCrypt password encryption.
   - Stateless JWT Bearer token authentication with configurable expiration.

2. **Automated Document Upload & OCR**
   - Supports GST Registration Certificates (`REG-06`), PAN Cards, and Business Licenses.
   - Integrated **Tess4J OCR** and **Apache PDFBox** to rasterize multi-page PDF documents and extract text.
   - High-precision regex pattern matching for 15-character GSTIN (`\d{2}[A-Z]{5}\d{4}[A-Z]{1}[A-Z\d]{1}[Z]{1}[A-Z\d]{1}`) and 10-character PAN (`[A-Z]{5}\d{4}[A-Z]{1}`).
   - Automatic profile enrichment: extracted legal name, GSTIN, PAN, and address are automatically synchronized into the seller's profile.

3. **Dual AI Integration (Gemini & Claude)**
   - **Google Gemini 2.5 Flash:** Powers high-throughput fashion catalog creation, converting minimal seller prompts into rich, ONDC-compliant product titles, descriptions, categories, and tags.
   - **Anthropic Claude Sonnet 4.5:** Powers regulatory compliance auditing, verifying merchant credentials against Indian Legal Metrology and Consumer Protection rules.
   - **Resilient AI Router:** Built-in failover and offline intelligent template generator ensures the system functions even without external API keys.

4. **100-Point ONDC Compliance Engine**
   - Evaluates seller verification, document completeness, catalog richness, and mandatory fashion attributes (MRP, Size, Color, Material).
   - Generates readiness score and status (`ONDC Ready` for score $\ge 80$, else `Needs Attention`).
   - Produces itemized passed checks, auditor remarks, and downloadable Readiness Certificates.

5. **ONDC Beckn Protocol Export**
   - Exports the verified seller profile and apparel catalog into standard ONDC Beckn Protocol retail schema JSON (`ONDC:RET12`).

---

## 🛠️ Technology Stack

- **Frontend:** React 18, Tailwind CSS, Axios, Lucide Icons, Vite
- **Backend:** Spring Boot 4.0.9, Java 25, Spring Data MongoDB, Spring Security, Lombok
- **Database:** MongoDB
- **OCR Engine:** Tess4J 5.20.0, Apache PDFBox 3.0.8
- **AI Providers:** Google Gemini API (`gemini-2.5-flash`), Anthropic Claude API (`claude-sonnet-4-5`)
- **Build Tools:** Maven 3.9, Node.js 22

---

## 📁 Project Architecture

```
ONDC-Auto-Onboarder/
├── backend/
│   ├── src/
│   │   ├── main/java/com/ondc/
│   │   │   ├── ai/            # GeminiService, ClaudeService, AiServiceRouter, PromptBuilder, CatalogGenerator, ComplianceAI
│   │   │   ├── config/        # SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter, WebConfig, AppProperties
│   │   │   ├── controller/    # AuthController, SellerController, DocumentController, OCRController, ProductController, ComplianceController, DashboardController, ONDCExportController
│   │   │   ├── dto/           # Request & Response DTOs
│   │   │   ├── entity/        # Seller, UploadedDocument, Product, ComplianceRecord
│   │   │   ├── exception/     # GlobalExceptionHandler & custom exceptions
│   │   │   ├── repository/    # Spring Data MongoDB Repositories
│   │   │   ├── service/       # Business services
│   │   │   └── util/          # OCRUtil, FileUploadUtil, RegexUtil
│   │   └── resources/
│   │       └── application.yml
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/        # Navbar, Sidebar, UploadCard, ProductCard, ComplianceCard, StatCard, ProtectedRoute
│   │   ├── context/           # AuthContext (JWT State & Actions)
│   │   ├── pages/             # Login, Register, Dashboard, SellerProfile, UploadDocuments, ProductUpload, Catalog, Compliance
│   │   ├── routes/            # AppRoutes
│   │   ├── services/          # api.js, authService, sellerService, documentService, productService, complianceService, dashboardService
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── index.html
│   ├── vite.config.js
│   ├── tailwind.config.js
│   └── package.json
└── docs/
    ├── ONDC_Auto_Onboarder_SRS.pdf
    └── ondc structure.pdf
```

---

## 🚀 Getting Started

### 1. Prerequisites
- Java 21 or Java 25
- Maven 3.8+
- Node.js 18+ & npm
- MongoDB running locally on `localhost:27017` (or MongoDB Atlas URI)

### 2. Backend Setup
```bash
cd backend

# Compile and run unit tests
mvn clean test

# Run Spring Boot backend
mvn spring-boot:run
```
The backend starts on `http://localhost:8080`.

### 3. Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Start Vite dev server
npm run dev
```
Open `http://localhost:5173` in your browser.

---

## ⚙️ Environment Configuration

| Variable | Description | Default |
|---|---|---|
| `SERVER_PORT` | Backend port | `8080` |
| `MONGODB_URI` | MongoDB Connection URI | `mongodb://localhost:27017/ondc_onboarder` |
| `JWT_SECRET` | 256-bit secret for signing JWTs | Auto-generated default |
| `JWT_EXPIRATION` | Token validity in ms | `86400000` (24 hours) |
| `CORS_ORIGINS` | Allowed frontend origin | `http://localhost:5173` |
| `UPLOAD_DIR` | Upload directory | `uploads` |
| `GEMINI_API_KEY` | Google AI Studio Key | Optional (offline simulation if empty) |
| `ANTHROPIC_API_KEY` | Anthropic Claude API Key | Optional (offline simulation if empty) |

---

## 🧪 Testing

```bash
# Run backend tests
cd backend
mvn test
```
All unit tests for Regex extraction, Catalog generation, and Compliance auditing run automatically with 100% pass rate.

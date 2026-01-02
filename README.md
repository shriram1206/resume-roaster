# 🔥 Resume Roaster

An AI-powered resume analysis tool that provides honest, actionable feedback to help you land your dream job at top product-based companies. Built with Spring Boot, React, and powered by Ollama's local LLM.

## ✨ Features

- 📄 **Multi-format Support**: Upload resumes in PDF, DOCX, or TXT format
- 🤖 **AI-Powered Analysis**: Uses Ollama's Llama 3.2 model for intelligent resume evaluation
- 📊 **Detailed Scoring**: Get an overall score (0-10) and tier placement (Tier 1/2/3)
- 🎯 **Targeted Feedback**: Receive specific issues and actionable improvement items
- 💼 **Job-Specific**: Analyze your resume against specific job descriptions
- ⚡ **Real-time Processing**: Fast resume parsing and analysis
- 🎨 **Beautiful UI**: Modern, responsive React interface with smooth animations

## 🏗️ Architecture

```
resume-roaster/
├── src/                          # Spring Boot Backend
│   ├── main/
│   │   ├── java/com/resumeroaster/
│   │   │   ├── config/          # CORS, WebClient configuration
│   │   │   ├── controller/      # REST API endpoints
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA Entities
│   │   │   ├── exception/       # Custom exceptions & handlers
│   │   │   ├── repository/      # Spring Data JPA repositories
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── frontend/                     # React Frontend
    ├── src/
    │   ├── App.jsx              # Main application component
    │   ├── api.js               # API service layer
    │   ├── App.css              # Component styles
    │   └── index.css            # Global styles
    ├── package.json
    └── vite.config.js
```

## 🛠️ Tech Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2.1** - Application framework
- **Spring Data JPA** - Database abstraction
- **PostgreSQL** - Relational database
- **Apache PDFBox** - PDF parsing
- **Apache POI** - DOCX parsing
- **WebClient** - Reactive HTTP client for Ollama integration
- **Lombok** - Boilerplate code reduction

### Frontend
- **React 18** - UI library
- **Vite** - Build tool and dev server
- **Axios** - HTTP client
- **CSS3** - Styling with animations

### AI/ML
- **Ollama** - Local LLM runtime
- **Llama 3.2** - Language model for analysis

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17+** - [Download](https://adoptium.net/)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **Node.js 18+** - [Download](https://nodejs.org/)
- **PostgreSQL 14+** - [Download](https://www.postgresql.org/download/)
- **Ollama** - [Download](https://ollama.ai/download)

## 🚀 Setup Instructions

### 1. Database Setup

```sql
-- Create database
CREATE DATABASE resume_roaster;

-- Create user (optional)
CREATE USER resume_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE resume_roaster TO resume_user;
```

### 2. Ollama Setup

```bash
# Install Ollama (if not already installed)
# Follow instructions at https://ollama.ai/download

# Pull the Llama 3.2 model
ollama pull llama3.2:latest

# Start Ollama server
ollama serve
```

The Ollama server will run on `http://localhost:11434`

### 3. Backend Setup

```bash
# Clone the repository
git clone https://github.com/shriram1206/resume-roaster.git
cd resume-roaster

# Configure application properties (optional)
# Edit src/main/resources/application.properties if needed

# Build and run the application
./mvnw clean install
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

#### Backend Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/resume_roaster
spring.datasource.username=postgres
spring.datasource.password=postgres

# Ollama
ollama.base-url=http://localhost:11434
ollama.model=llama3.2:latest
ollama.timeout=300

# File Upload
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

### 4. Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will start on `http://localhost:5173`

## 📖 API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Endpoints

#### 1. Health Check
Check if the service is running.

```http
GET /health
```

**Response:**
```json
{
  "status": "ok",
  "timestamp": "2026-01-02T10:30:00Z"
}
```

---

#### 2. Upload Resume
Upload a resume file for analysis.

```http
POST /upload
Content-Type: multipart/form-data
```

**Request Body:**
- `file` (multipart/form-data): Resume file (PDF, DOCX, or TXT)

**Response:**
```json
{
  "uploadId": "550e8400-e29b-41d4-a716-446655440000",
  "filename": "john_doe_resume.pdf",
  "fileSize": 102400,
  "status": "uploaded"
}
```

**Error Responses:**
- `400 Bad Request`: Invalid file type or file too large
- `500 Internal Server Error`: File storage failure

---

#### 3. Analyze Resume
Analyze an uploaded resume against a job description.

```http
POST /analyze
Content-Type: application/json
```

**Request Body:**
```json
{
  "uploadId": "550e8400-e29b-41d4-a716-446655440000",
  "jobDescription": "Looking for a Senior Java Developer with Spring Boot experience..."
}
```

**Response:**
```json
{
  "analysisId": 1,
  "overallScore": 7,
  "tierPlacement": "Tier 2",
  "verdict": "Your resume demonstrates solid technical skills and relevant experience. However, to increase your chances at Tier 1 companies, focus on quantifying achievements and highlighting impact-driven projects.",
  "topIssues": [
    "Lack of quantifiable metrics in project descriptions",
    "Missing leadership and mentorship experience",
    "Limited demonstration of system design skills"
  ],
  "actionItems": [
    "Add specific metrics (e.g., 'Improved API response time by 40%')",
    "Include examples of code reviews or mentoring junior developers",
    "Describe architecture decisions and trade-offs in major projects",
    "Add more details about cloud infrastructure and scalability"
  ]
}
```

**Error Responses:**
- `400 Bad Request`: Missing required fields or invalid uploadId
- `404 Not Found`: Upload ID not found
- `500 Internal Server Error`: Analysis failure or LLM timeout

---

### Error Response Format

All errors follow this structure:

```json
{
  "error": "Error message description",
  "errorCode": "ERR_CODE",
  "timestamp": "2026-01-02T10:30:00Z"
}
```

**Error Codes:**
- `ERR_INVALID_FILE_TYPE`: Unsupported file format
- `ERR_FILE_TOO_LARGE`: File exceeds 5MB limit
- `ERR_PARSING_FAILED`: Unable to extract text from resume
- `ERR_LLM_FAILED`: Ollama/LLM service error
- `ERR_INTERNAL`: Generic server error

## 🎯 Usage Example

### Using cURL

```bash
# 1. Upload resume
curl -X POST -F "file=@resume.pdf" http://localhost:8080/api/v1/upload

# Response: {"uploadId":"abc123...","filename":"resume.pdf",...}

# 2. Analyze resume
curl -X POST http://localhost:8080/api/v1/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "uploadId": "abc123...",
    "jobDescription": "Senior Java Developer with 5+ years experience..."
  }'
```

### Using the Web Interface

1. Open `http://localhost:5173` in your browser
2. Click "Choose File" and select your resume (PDF, DOCX, or TXT)
3. Click "Upload Resume"
4. Enter the job description in the text area
5. Click "Analyze Resume"
6. View your results with score, tier placement, issues, and action items

## 🔧 Configuration

### Changing the LLM Model

Edit `application.properties`:

```properties
# Use a different Ollama model
ollama.model=llama3:70b
# or
ollama.model=codellama:latest
```

Make sure to pull the model first:
```bash
ollama pull llama3:70b
```

### Adjusting Analysis Timeout

```properties
# Increase timeout for slower systems (in seconds)
ollama.timeout=600
```

### Customizing Port Numbers

**Backend:**
```properties
server.port=8081
```

**Frontend:**
Edit `frontend/vite.config.js`:
```javascript
server: {
  port: 3000
}
```

## 🧪 Testing

### Backend Tests
```bash
./mvnw test
```

### Manual Testing with Sample Resume

A test resume is included at `test-resume.txt`:

```bash
curl -X POST -F "file=@test-resume.txt;type=application/pdf" \
  http://localhost:8080/api/v1/upload
```

## 📊 Scoring System

- **0-3**: Needs significant improvement (Tier 3)
- **4-6**: Good foundation, needs refinement (Tier 2-3)
- **7-8**: Strong resume, competitive for most companies (Tier 2)
- **9-10**: Exceptional resume, Tier 1 company ready (Tier 1)

### Tier Classifications

- **Tier 1**: FAANG, Top Tech Giants (Google, Meta, Amazon, etc.)
- **Tier 2**: Established Product Companies (Flipkart, Swiggy, etc.)
- **Tier 3**: Service-based companies and startups

## 🐛 Troubleshooting

### Ollama Connection Issues

```bash
# Check if Ollama is running
curl http://localhost:11434

# Restart Ollama
ollama serve
```

### Database Connection Issues

```sql
-- Verify PostgreSQL is running
psql -U postgres -l

-- Check if database exists
\c resume_roaster
```

### Port Already in Use

```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (Windows)
taskkill /PID <PID> /F

# Kill the process (Linux/Mac)
kill -9 <PID>
```

### File Upload Errors

- Ensure file size is under 5MB
- Supported formats: PDF, DOCX, TXT
- Check if upload directory is writable

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 👨‍💻 Author

**Shriram**
- GitHub: [@shriram1206](https://github.com/shriram1206)
- Repository: [resume-roaster](https://github.com/shriram1206/resume-roaster)

## 🙏 Acknowledgments

- [Ollama](https://ollama.ai/) - Local LLM runtime
- [Meta AI](https://ai.meta.com/) - Llama 3.2 model
- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [React](https://react.dev/) - Frontend library
- [Vite](https://vitejs.dev/) - Build tool

## 📧 Support

If you encounter any issues or have questions:
- Open an [issue](https://github.com/shriram1206/resume-roaster/issues)
- Check existing documentation
- Review troubleshooting section

---

**Built with ❤️ for job seekers everywhere**

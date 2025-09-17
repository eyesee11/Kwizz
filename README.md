# Quiz Master Platform

A comprehensive online quiz platform built with Spring Boot and React, featuring real-time quiz capabilities, AI-powered quiz generation, and microservice architecture.

## 🚀 Quick Start

### Prerequisites
- Java 21
- Node.js 18+
- MySQL 8.0
- Maven 3.6+

### Installation

#### Option 1: Local Mode (Recommended for Development)
1. **Database Setup**: Start MySQL and create database `quiz_db` with password `password`
2. **Start Application**: Double-click `start-local.bat`
3. **Access**: Frontend at http://localhost:5173, Backend at http://localhost:8080

#### Option 2: Microservices Mode (Advanced)
1. **Database Setup**: Start MySQL and create database `quiz_db` with password `password`
2. **Start All Services**: Double-click `start-microservices.bat`
3. **Access**: 
   - Frontend: http://localhost:5173
   - Main Backend: http://localhost:8080
   <!-- - Question Bank Service: http://localhost:8081
   - Result Service: http://localhost:8082 -->

## 📁 Project Structure
```
quiz-master/
├── backend/                    # Main Spring Boot Backend
├── frontend/                   # React Frontend  
├── services/                   # Microservices
│   ├── question-bank-service/  # Question Bank Microservice
│   └── result-service/         # Result Microservice
├── database/                   # Database Schema
├── docs/                       # Documentation
├── start-local.bat            # Local mode startup
├── start-microservices.bat    # Microservices startup
└── README.md                  # This file
```

## 📚 Documentation
- **[Project Overview](docs/PROJECT_OVERVIEW.md)** - Complete project details
- **[API Documentation](docs/API_DOCUMENTATION.md)** - REST API endpoints
- **[Database Schema](docs/DATABASE_SCHEMA.md)** - Database structure
- **[Installation Guide](docs/INSTALLATION_GUIDE.md)** - Detailed setup instructions

## 🔧 Configuration
- **Database**: localhost:3306/quiz_db (configurable via environment variables)
- **Backend Port**: 8080 (configurable via SERVER_PORT)
- **Frontend Port**: 5173
- **JWT Secret**: Configured via APP_JWT_SECRET environment variable
- **Gemini API**: Configured via GEMINI_API_KEY environment variable

## 📋 Features
- ✅ User Authentication (JWT)
- ✅ Role-based Access (Student/Teacher)
- ✅ Quiz Management
- ✅ Real-time Quiz Taking
- ✅ AI Quiz Generation
- ✅ Question Bank Management
- ✅ Results & Analytics
- ✅ Responsive UI
- ✅ Microservices Architecture
- ✅ Enhanced Student Dashboard
- ✅ Enhanced Teacher Dashboard

## 🛠️ Technology Stack
- **Backend**: Spring Boot, Spring Security, Spring Data JPA, Hibernate
- **Frontend**: React, TypeScript, Vite, Axios
- **Database**: MySQL, JDBC
- **AI**: Google Gemini API
- **Build**: Maven, npm

## Usage

1. Open your browser and go to `http://localhost:5173`
2. Sign up for a new account (choose Teacher or Student role)
3. **For Teachers**:
   - Create quizzes manually or use AI generation
   - Manage questions with enhanced editor
   - View detailed analytics and student performance
   - Monitor quiz attempts and scores
4. **For Students**:
   - Browse available quizzes with difficulty indicators
   - Take quizzes with enhanced UI and progress tracking
   - View detailed results with performance feedback
   - See live leaderboard and personal statistics

## API Endpoints

### Authentication
- `POST /api/auth/signup` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /api/auth/me` - Get current user info

### Quizzes
- `GET /api/quiz` - Get all quizzes (teachers)
- `GET /api/quiz/available` - Get available quizzes (students)
- `POST /api/quiz` - Create new quiz
- `GET /api/quiz/{id}/questions` - Get quiz questions
- `PUT /api/quiz/{id}/questions` - Update quiz questions
- `DELETE /api/quiz/{id}` - Delete quiz

### Attempts
- `GET /api/attempt/my` - Get user's attempts
- `POST /api/attempt` - Submit quiz attempt

### Analytics
- `GET /api/analytics/summary` - Get quiz summary statistics
- `GET /api/analytics/students` - Get student performance data
- `GET /api/analytics/attempts` - Get recent attempts

### AI Generation
- `POST /api/ai/generate` - Generate questions using AI

## Configuration

### Environment Variables

You can configure the application using environment variables:

- `APP_JWT_SECRET` - JWT secret key (default: provided in application.properties)
- `APP_JWT_TTL_SECONDS` - JWT token TTL in seconds (default: 86400)
- `GEMINI_API_KEY` - Gemini AI API key for question generation

### Database Configuration

Configure the application using environment variables or update `backend/src/main/resources/application.properties`:

**Environment Variables:**
```bash
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/quiz_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT Configuration
APP_JWT_SECRET=your-super-secret-jwt-key-here
APP_JWT_TTL_SECONDS=86400

# AI Configuration
GEMINI_API_KEY=your-gemini-api-key-here

# Server Configuration
SERVER_PORT=8080
```

**Or update application.properties directly:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quiz_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
gemini.api.key=your-gemini-api-key-here
```

## Troubleshooting

### Common Issues

1. **Database Connection Error**: Ensure MySQL is running and credentials are correct
2. **CORS Issues**: Check that the frontend is running on port 5173
3. **JWT Token Issues**: Verify the JWT secret key is properly configured

### Logs

- Backend logs: Check console output when running `mvn spring-boot:run`
- Frontend logs: Check browser console for any JavaScript errors

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License.

MADE WITH ❤️

# Microservices Setup Guide

## Architecture Overview

Your quiz platform now uses a microservices architecture with Eureka service discovery:

- **Eureka Server** (Port 8761): Service registry and discovery
- **Result Service** (Port 8082): Handles quiz results, statistics, and leaderboards
- **Question Bank Service** (Port 8081): Manages question banks and questions
- **Main Backend** (Port 8080): Main application API that orchestrates other services
- **Frontend** (Port 5173): React application

## Starting the Services

### Option 1: Using the Batch Script (Windows)
```bash
# Run from the project root directory
./start-microservices.bat
```

### Option 2: Manual Startup
1. **Start Eureka Server first:**
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```

2. **Start Result Service:**
   ```bash
   cd result-service
   mvn spring-boot:run
   ```

3. **Start Question Bank Service:**
   ```bash
   cd question-bank-service
   mvn spring-boot:run
   ```

4. **Start Main Backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

5. **Start Frontend:**
   ```bash
   cd frontend
   npm run dev
   ```

## Verification

1. **Eureka Dashboard:** http://localhost:8761
   - Should show all registered services

2. **Service Health Checks:**
   - Result Service: http://localhost:8082/api/results/leaderboard
   - Question Bank Service: http://localhost:8081/api/question-banks
   - Main Backend: http://localhost:8080/api/auth/me

3. **Frontend:** http://localhost:5173

## Configuration Changes Made

### Main Backend (`application.properties`)
- Changed `microservices.mode=eureka`
- Added Eureka client configuration
- Added Spring Cloud dependencies

### New Microservices
- Both services register with Eureka
- Use shared database configuration
- Expose REST APIs for inter-service communication

## Service Communication

The main backend now uses service discovery to communicate with microservices:
- Uses `@LoadBalanced RestTemplate` for client-side load balancing
- Services are referenced by name (e.g., `http://result-service/api/...`)
- Eureka handles service discovery and health monitoring

## Troubleshooting

1. **Services not registering:** Check Eureka server is running first
2. **Database connection issues:** Ensure MySQL is running and credentials are correct
3. **Port conflicts:** Make sure ports 8080, 8081, 8082, 8761 are available
4. **Service discovery fails:** Wait 30-60 seconds for full registration
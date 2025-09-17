@echo off
echo Starting Eureka Server and Microservices Architecture...

echo.
echo =================================================================
echo Starting Eureka Server on port 8761...
echo =================================================================
start "Eureka Server" cmd /k "cd /d eureka-server && mvn spring-boot:run"

timeout /t 15

echo.
echo =================================================================
echo Starting Microservices...
echo =================================================================
echo Starting Result Service on port 8082...
start "Result Service" cmd /k "cd /d result-service && mvn spring-boot:run"

echo.
echo Starting Question Bank Service on port 8081...
start "Question Bank Service" cmd /k "cd /d question-bank-service && mvn spring-boot:run"

timeout /t 15

echo.
echo =================================================================
echo Starting API Gateway on port 8085...
echo =================================================================
start "API Gateway" cmd /k "cd /d api-gateway && mvn spring-boot:run"

timeout /t 10

echo.
echo =================================================================
echo Starting Main Backend on port 8083...
echo =================================================================
start "Main Backend" cmd /k "cd /d backend && mvn spring-boot:run"

timeout /t 10

echo.
echo =================================================================
echo Starting Frontend on port 5173...
echo =================================================================
start "Frontend" cmd /k "cd /d frontend && npm run dev"

echo.
echo =================================================================
echo All services are starting up...
echo =================================================================
echo Wait 60-90 seconds for all services to register with Eureka
echo.
echo Access URLs:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8085
echo - Frontend: http://localhost:5173
echo.
echo All API calls should go through Gateway at port 8085
pause
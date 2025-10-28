#!/bin/bash

echo "🚀 Starting Career Portal Microservices..."
echo "=========================================="

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}Step 1: Starting Eureka Server...${NC}"
cd eureka_server
mvn spring-boot:run > ../logs/eureka.log 2>&1 &
EUREKA_PID=$!
echo -e "${GREEN}✓ Eureka Server starting (PID: $EUREKA_PID)${NC}"
cd ..

echo "Waiting for Eureka Server to be ready..."
sleep 30

echo -e "${BLUE}Step 2: Starting API Gateway...${NC}"
cd getway-server
mvn spring-boot:run > ../logs/gateway.log 2>&1 &
GATEWAY_PID=$!
echo -e "${GREEN}✓ API Gateway starting (PID: $GATEWAY_PID)${NC}"
cd ..
sleep 10

echo -e "${BLUE}Step 3: Starting User Service...${NC}"
cd user_service
mvn spring-boot:run > ../logs/user-service.log 2>&1 &
USER_PID=$!
echo -e "${GREEN}✓ User Service starting (PID: $USER_PID)${NC}"
cd ..
sleep 10

echo -e "${BLUE}Step 4: Starting Question Service...${NC}"
cd question-sheet
mvn spring-boot:run > ../logs/question-service.log 2>&1 &
QUESTION_PID=$!
echo -e "${GREEN}✓ Question Service starting (PID: $QUESTION_PID)${NC}"
cd ..
sleep 10

echo -e "${BLUE}Step 5: Starting Resume Service...${NC}"
cd resume-service
mvn spring-boot:run > ../logs/resume-service.log 2>&1 &
RESUME_PID=$!
echo -e "${GREEN}✓ Resume Service starting (PID: $RESUME_PID)${NC}"
cd ..
sleep 10

echo -e "${BLUE}Step 6: Starting Roadmap Service...${NC}"
cd roadmap-service
mvn spring-boot:run > ../logs/roadmap-service.log 2>&1 &
ROADMAP_PID=$!
echo -e "${GREEN}✓ Roadmap Service starting (PID: $ROADMAP_PID)${NC}"
cd ..
sleep 10

echo -e "${BLUE}Step 7: Starting Gen Service...${NC}"
cd gen-service
mvn spring-boot:run > ../logs/gen-service.log 2>&1 &
GEN_PID=$!
echo -e "${GREEN}✓ Gen Service starting (PID: $GEN_PID)${NC}"
cd ..

echo ""
echo "=========================================="
echo -e "${GREEN}✅ All services started successfully!${NC}"
echo "=========================================="
echo ""
echo "Service URLs:"
echo "  📊 Eureka Dashboard: http://localhost:8761"
echo "  🌐 API Gateway: http://localhost:8080"
echo ""
echo "To stop all services, run: ./stop-all.sh"

echo "$EUREKA_PID" > pids.txt
echo "$GATEWAY_PID" >> pids.txt
echo "$USER_PID" >> pids.txt
echo "$QUESTION_PID" >> pids.txt
echo "$RESUME_PID" >> pids.txt
echo "$ROADMAP_PID" >> pids.txt
echo "$GEN_PID" >> pids.txt

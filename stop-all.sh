#!/bin/bash

echo "🛑 Stopping Career Portal Microservices..."

if [ -f pids.txt ]; then
    while read pid; do
        if ps -p $pid > /dev/null; then
            echo "Stopping process $pid..."
            kill $pid
        fi
    done < pids.txt
    rm pids.txt
    echo "✅ All services stopped!"
else
    echo "Stopping by port..."
    lsof -ti:8761 | xargs kill -9 2>/dev/null
    lsof -ti:8080 | xargs kill -9 2>/dev/null
    lsof -ti:8081 | xargs kill -9 2>/dev/null
    lsof -ti:8082 | xargs kill -9 2>/dev/null
    lsof -ti:8083 | xargs kill -9 2>/dev/null
    lsof -ti:8084 | xargs kill -9 2>/dev/null
    lsof -ti:8085 | xargs kill -9 2>/dev/null
    echo "✅ Ports cleared!"
fi

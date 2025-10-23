#!/bin/bash
# Start script for Render.com

echo "Starting application..."
java -Dserver.port=$PORT -jar target/quan-ly-noi-that-backend-0.0.1-SNAPSHOT.jar

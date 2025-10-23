#!/bin/bash
# Build script for Render.com

echo "Building application..."
./mvnw clean install -DskipTests

echo "Build completed!"

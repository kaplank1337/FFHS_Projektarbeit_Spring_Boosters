#!/bin/bash

set -e

DOCKER_USERNAME="nziswiler"

echo "======================================"
echo "Docker Build & Push Script"
echo "======================================"
echo ""

# Test Docker Hub connectivity
echo "Testing connection to Docker Hub..."
if ! curl -s --max-time 5 https://registry-1.docker.io/v2/ > /dev/null 2>&1; then
    echo "WARNING: Cannot reach Docker Hub!"
    echo ""
    echo "This is likely due to Docker proxy settings."
    echo "To fix this:"
    echo "  1. Open Docker Desktop"
    echo "  2. Go to Settings → Resources → Proxies"
    echo "  3. Disable 'Manual proxy configuration'"
    echo "  4. Click 'Apply & Restart'"
    echo ""
    read -p "Do you want to continue anyway? (y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Aborted. Please fix proxy settings and try again."
        exit 1
    fi
else
    echo "Connection OK!"
fi

echo ""
read -p "Enter Docker Hub username (default: $DOCKER_USERNAME): " input_username
if [ -n "$input_username" ]; then
    DOCKER_USERNAME="$input_username"
fi

read -p "Enter version tag (default: latest): " VERSION
VERSION=${VERSION:-latest}

echo ""
echo "Using Docker Hub username: $DOCKER_USERNAME"
echo "Using version tag: $VERSION"
echo ""
read -p "Continue? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Aborted."
    exit 1
fi

echo ""
echo "Logging into Docker Hub..."
docker login

echo ""
echo "Setting up buildx builder..."
if ! docker buildx inspect multiplatform > /dev/null 2>&1; then
    docker buildx create --name multiplatform --driver docker-container --bootstrap
fi
docker buildx use multiplatform

echo ""
echo "======================================"
echo "Build & Push Strategy"
echo "======================================"
echo "1. Build locally and push separately (standard)"
echo "2. Build and push together with buildx (may work better with proxies)"
echo ""
read -p "Choose strategy (1 or 2, default: 2): " STRATEGY
STRATEGY=${STRATEGY:-2}

if [ "$STRATEGY" == "2" ]; then
    echo ""
    echo "======================================"
    echo "Building and pushing with buildx"
    echo "======================================"

    echo ""
    echo "1/4 Frontend..."
    docker buildx build --platform linux/amd64 \
        --push \
        -t $DOCKER_USERNAME/springboosters-frontend:$VERSION \
        -f ./frontend/docker/Dockerfile \
        ./frontend

    echo ""
    echo "2/4 Authentication Service..."
    docker buildx build --platform linux/amd64 \
        --push \
        -t $DOCKER_USERNAME/springboosters-authentification-service:$VERSION \
        -f ./authentification_service/Dockerfile \
        ./authentification_service

    echo ""
    echo "3/4 Notification Service..."
    docker buildx build --platform linux/amd64 \
        --push \
        -t $DOCKER_USERNAME/springboosters-notification-service:$VERSION \
        -f ./notification_service/Dockerfile \
        ./notification_service

    echo ""
    echo "4/4 Core Backend..."
    docker buildx build --platform linux/amd64 \
        --push \
        -t $DOCKER_USERNAME/springboosters-corebackend:$VERSION \
        -f ./core_backend/Dockerfile \
        ./core_backend

else
    echo ""
    echo "======================================"
    echo "Building images"
    echo "======================================"

    echo ""
    echo "1/4 Frontend..."
    docker build -t $DOCKER_USERNAME/springboosters-frontend:$VERSION \
        -f ./frontend/docker/Dockerfile \
        ./frontend

    echo ""
    echo "2/4 Authentication Service..."
    docker build -t $DOCKER_USERNAME/springboosters-authentification-service:$VERSION \
        -f ./authentification_service/Dockerfile \
        ./authentification_service

    echo ""
    echo "3/4 Notification Service..."
    docker build -t $DOCKER_USERNAME/springboosters-notification-service:$VERSION \
        -f ./notification_service/Dockerfile \
        ./notification_service

    echo ""
    echo "4/4 Core Backend..."
    docker build -t $DOCKER_USERNAME/springboosters-corebackend:$VERSION \
        -f ./core_backend/Dockerfile \
        ./core_backend

    echo ""
    echo "======================================"
    echo "Pushing images"
    echo "======================================"

    echo ""
    echo "1/4 Pushing Frontend..."
    docker push $DOCKER_USERNAME/springboosters-frontend:$VERSION

    echo ""
    echo "2/4 Pushing Authentication Service..."
    docker push $DOCKER_USERNAME/springboosters-authentification-service:$VERSION

    echo ""
    echo "3/4 Pushing Notification Service..."
    docker push $DOCKER_USERNAME/springboosters-notification-service:$VERSION

    echo ""
    echo "4/4 Pushing Core Backend..."
    docker push $DOCKER_USERNAME/springboosters-corebackend:$VERSION
fi

echo ""
echo "======================================"
echo "All images pushed successfully!"
echo "======================================"
echo ""
echo "Pushed images:"
echo "  - $DOCKER_USERNAME/springboosters-frontend:$VERSION"
echo "  - $DOCKER_USERNAME/springboosters-authentification-service:$VERSION"
echo "  - $DOCKER_USERNAME/springboosters-notification-service:$VERSION"
echo "  - $DOCKER_USERNAME/springboosters-corebackend:$VERSION"
echo ""
echo "Docker Hub: https://hub.docker.com/u/$DOCKER_USERNAME"
echo ""

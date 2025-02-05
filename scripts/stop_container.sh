#!/bin/bash

# Kiểm tra nếu container đang chạy hoặc đã dừng
if docker ps -a | grep -q "container_lms_course_service"; then
    echo "Stopping container_lms_course_service..."
    
    # Nếu container đang chạy, dừng nó
    if docker ps | grep -q "container_lms_course_service"; then
        docker stop container_lms_course_service
    fi
    
    # Xóa container
    docker rm container_lms_course_service
else
    echo "Container container_lms_course_service not found, nothing to stop."
fi

#!/bin/bash

# Kiểm tra xem Docker có đang chạy không
if ! systemctl is-active --quiet docker; then
    echo "Docker is not running!"
    exit 1
fi

# Kiểm tra và dừng container nếu có container đang chạy
if [ "$(docker ps -q)" ]; then
    docker stop $(docker ps -q)
fi

# Kiểm tra và xóa container nếu có container tồn tại
if [ "$(docker ps -a -q)" ]; then
    docker rm $(docker ps -a -q)
fi

echo "All containers stopped and removed successfully!"

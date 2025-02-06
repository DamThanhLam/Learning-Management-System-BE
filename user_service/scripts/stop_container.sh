#!/bin/bash

# Dừng tất cả các container đang chạy
docker stop $(docker ps -q) 2>/dev/null

# Xóa tất cả các container
docker rm $(docker ps -a -q) 2>/dev/null

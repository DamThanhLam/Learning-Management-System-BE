
#!/bin/bash

# Kiểm tra nếu có container nào
container_ids=$(docker ps -a -q)

if [ -z "$container_ids" ]; then
  echo "Không có container nào để dừng hoặc xóa."
else
  # Kiểm tra tất cả các container đang chạy
  echo "Danh sách tất cả các container đang chạy:"
  docker ps -a

  # Dừng tất cả các container đang chạy
  echo "Đang dừng tất cả các container..."
  docker stop $container_ids

  # Xóa tất cả các container đã dừng
  echo "Đang xóa tất cả các container..."
  docker rm $container_ids

  echo "Đã dừng và xóa tất cả các container."
fi

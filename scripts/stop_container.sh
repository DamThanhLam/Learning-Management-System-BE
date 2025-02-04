set -e
#docker stop container_lms_course_service
# Lấy danh sách container đang chạy
CONTAINERS=$(docker ps -q)

# Kiểm tra nếu có container thì mới kill
if [ -n "$CONTAINERS" ]; then
    docker kill $CONTAINERS
    echo "Killed containers: $CONTAINERS"
else
    echo "No running containers to kill."
fi
docker ps -a | grep container_lms_course_service && docker rm container_lms_course_service

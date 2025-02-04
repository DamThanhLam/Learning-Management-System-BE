
set -e

docker build -t image_lms_course_service:latest .
docker run -d --name container_lms_course_service container_lms_course_service:latest
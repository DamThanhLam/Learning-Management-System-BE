
set -e
cd "$(dirname "$0")/.."
docker build -t image_lms_course_service:latest course_service/
docker run -d -p 8080:8080 --name container_lms_course_service image_lms_course_service:latest


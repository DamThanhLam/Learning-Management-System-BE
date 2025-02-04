set -e
#docker stop container_lms_course_service
docker kill $(docker ps -q)
docker rm container_lms_course_service
docker build -t image_lms_course_service:latest .
docker run -d --name container_lms_course_service container_lms_course_service:latest
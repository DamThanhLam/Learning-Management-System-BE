set -e
#docker stop container_lms_course_service
docker kill $(docker ps -q)
docker rm -f container_lms_course_service

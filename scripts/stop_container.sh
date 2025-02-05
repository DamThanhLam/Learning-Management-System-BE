set -e
#docker stop container_lms_course_service

docker ps -a -q | xargs -r docker rm -f
docker ps -a | grep container_lms_course_service && docker rm container_lms_course_service

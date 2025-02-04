set -e
#docker stop container_lms_course_service
docker kill $(docker ps -q)
docker ps -a | grep container_lms_course_service && docker rm container_lms_course_service

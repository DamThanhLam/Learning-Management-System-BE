#set -e
#docker stop container_lms_course_service
chmod +x stop_container.sh
docker ps -a -q | xargs -r docker rm -f
docker ps -a | grep container_lms_course_service && docker rm container_lms_course_service

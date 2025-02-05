#set -e
#docker stop container_lms_course_service
#if docker ps -a | grep -q "container_lms_course_service"; then
#    echo "Stopping container_lms_course_service..."
#    docker stop container_lms_course_service
#    docker rm container_lms_course_service
#else
#    echo "Container container_lms_course_service not found, nothing to stop."
#fi
#docker ps -a | grep container_lms_course_service && docker rm container_lms_course_service

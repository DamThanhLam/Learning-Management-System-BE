
set -e
cd "$(dirname "$0")/.."

gradle build -x test

docker build -t image_lms_user_service:latest .
docker run -d -v /home/ubuntu/aws.env:/root/aws.env -p 8080:8080 --name container_lms_user_service image_lms_user_service:latest


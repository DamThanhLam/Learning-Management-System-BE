
set -e
cd "$(dirname "$0")/.."

export PATH=$PATH:/home/ubuntu/.sdkman/candidates/gradle/current/bin
gradle build -x test

sudo docker build -t image_lms_user_service:latest .
sudo docker run -d --env-file /home/ubuntu/aws.env -p 8080:8080 --name container_lms_user_service image_lms_user_service:latest


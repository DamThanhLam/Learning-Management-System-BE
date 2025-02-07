
set -e
cd "$(dirname "$0")/.."

export PATH=$PATH:/home/ubuntu/.sdkman/candidates/gradle/current/bin
gradle build -x test
aws appconfig get-configuration --application "application-config-lms-app" --environment "dev" --configuration "lms-app" --client-id "test2" src/main/resources/application.yaml

sudo docker build -t image_lms_user_service:latest .
sudo docker run -d --env-file /home/ubuntu/aws.env -p 8080:8080 --name container_lms_user_service image_lms_user_service:latest


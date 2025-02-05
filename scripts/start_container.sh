
set -e
cd "$(dirname "$0")/.."
sudo chown -R root:root ~/aws.env
sudo chmod -R 755 ~/aws.env

docker build -t image_lms_course_service:latest course_service/
docker run -d -v ~/aws.env:/root  -p 8080:8080 --name container_lms_course_service image_lms_course_service:latest


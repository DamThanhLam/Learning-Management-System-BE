package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.services.BL.BucketServiceBL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.google.common.io.Files.getFileExtension;

@Service
public class BucketServiceImpl implements BucketServiceBL {
    @Value("${aws.region}")
    private String region;
    @Value("${aws.accessKeyId}")
    private String awsAccessKeyId;

    @Value("${aws.secretAccessKey}")
    private String awsSecretAccessKey;

    @Value("${aws.s3.folder}")
    private String imageFolder;
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient
                .builder()
                .region(Region.of(region))  // Đặt khu vực AWS (region) của bạn
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey) // Cung cấp Access Key và Secret Key
                        )
                )
                .build();
    }
    @Override
    public List<Bucket> getAllBuckets() throws ExecutionException, InterruptedException {
        return s3AsyncClient().listBuckets().get().buckets();
    }


    public URL putObjectToBucket(String bucketName, MultipartFile multipartFile, String ...path ) throws IOException {
        // Kiểm tra nếu tệp là một file trong bộ nhớ
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        Path pathTemp = Files.createTempDirectory("upload");
        String fileName = multipartFile.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);
        long timestamp = System.currentTimeMillis();
        String uniqueFileName = timestamp+"." + fileExtension;

        File fileAvt = new File(pathTemp.toFile(), uniqueFileName);
        multipartFile.transferTo(fileAvt);

        // Tạo một request body từ InputStream
        AsyncRequestBody requestBody = AsyncRequestBody.fromFile(fileAvt);

        StringBuilder fullPath = new StringBuilder();
        for (String folder : path) {
            fullPath.append(folder).append("/");
        }
        // Tạo một yêu cầu PUT object với bucketName và objectName
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(imageFolder+"/"+fullPath+uniqueFileName)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        s3AsyncClient().putObject(putObjectRequest, requestBody);
        // Gửi yêu cầu tải lên đối tượng đến S3
        return genarateUrl(bucketName, uniqueFileName);
    }

    @Override
    public URL genarateUrl(String bucketName, String objectName) {

        GetUrlRequest getUrlRequest = GetUrlRequest.builder().bucket(bucketName).key(objectName).build();
        URL url = s3AsyncClient().utilities().getUrl(getUrlRequest);
        return url;
    }
}

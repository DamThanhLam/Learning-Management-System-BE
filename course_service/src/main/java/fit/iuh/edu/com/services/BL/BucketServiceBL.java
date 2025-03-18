package fit.iuh.edu.com.services.BL;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface BucketServiceBL {
    List<Bucket> getAllBuckets() throws ExecutionException, InterruptedException;

    /**
     * Method put object to S3 with path lms/(folder)
     * this folder and children all can access on the internet with role read
     * @param bucketName
     * @param multipartFile
     * @param path here to save file
     * @return String for the file to access on the internet
     * @throws IOException
     */
    String putObjectToBucket(String bucketName, MultipartFile multipartFile, String ...path) throws ExecutionException, InterruptedException, IOException;
    URL genarateUrl(String bucketName, String objectName);
    void removeObjectFromBucket(String bucketName, String objectName);
}

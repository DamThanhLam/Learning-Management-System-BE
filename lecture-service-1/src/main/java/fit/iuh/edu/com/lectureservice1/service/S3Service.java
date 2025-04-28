package fit.iuh.edu.com.lectureservice1.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
	private final S3Client s3Client;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	public S3Service(S3Client s3Client) {
		super();
		this.s3Client = s3Client;
	}

	// Upload a file to S3 and return the URL
	public String uploadFile(MultipartFile file, String folder) {
		try {
			String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
			PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(folder+filename)
					.contentType(file.getContentType()).build();

			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			return s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucketName).key(folder+filename).build())
					.toString();

		} catch (IOException e) {
			throw new RuntimeException("Error uploading file to S3", e);
		}
	}
	
	// Replace an existing file with a new one. Deletes the old file and uploads the new one.
	public String replaceFile(String existingFileUrl, MultipartFile newFile, String folder) {
		if(existingFileUrl==null||!existingFileUrl.isEmpty())deleteFileFromUrl(existingFileUrl); // Delete the old file
		return uploadFile(newFile, folder); // Upload the new file and return its URL
	}

	// Delete a file from S3 given its URL
	public void deleteFileFromUrl(String fileUrl) {
		try {
			String fileName = extractFileNameFromUrl(fileUrl);
			s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build());
		} catch (Exception e) {
			throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
		}
	}

	// Extract filename from URL (assuming standard S3 structure)
	private String extractFileNameFromUrl(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}

}

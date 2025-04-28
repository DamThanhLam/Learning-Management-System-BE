package fit.iuh.edu.com.lectureservice1.utils;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidationUtil {
    public static void validateFile(MultipartFile file, long maxSize, List<String> allowedContentTypes, String fileRole) {
        if (file == null || file.isEmpty()) return;

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(fileRole + " file size exceeds the limit of " + (maxSize / (1024 * 1024)) + "MB");
        }

        if (!allowedContentTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException(fileRole + " has unsupported content type: " + file.getContentType());
        }
    }
    // Kiểm tra kích thước file (bytes) và loại MIME type
    public boolean isValidFile(MultipartFile file, String[] allowedTypes, long maxSize) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        boolean typeOk = Arrays.stream(allowedTypes).anyMatch(contentType::equalsIgnoreCase);
        boolean sizeOk = file.getSize() <= maxSize;
        return typeOk && sizeOk;
    }

}

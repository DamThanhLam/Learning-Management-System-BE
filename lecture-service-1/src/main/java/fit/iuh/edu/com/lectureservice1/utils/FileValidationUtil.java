package fit.iuh.edu.com.lectureservice1.utils;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
}

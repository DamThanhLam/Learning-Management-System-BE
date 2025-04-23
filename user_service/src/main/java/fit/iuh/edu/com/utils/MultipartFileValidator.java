package fit.iuh.edu.com.utils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class MultipartFileValidator {

    // Giới hạn kích thước (15MB)
    private static final long MAX_SIZE_BYTES = 15L * 1024 * 1024;

    // Các MIME type được phép
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE
    );
    private static final Set<String> ALLOWED_CV_TYPES = Set.of(
            MediaType.TEXT_PLAIN_VALUE,
            MediaType.APPLICATION_PDF_VALUE,
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    /**
     * Validate một MultipartFile theo danh sách MIME type và kích thước tối đa.
     * @param file      MultipartFile cần kiểm tra
     * @param allowed   tập hợp các MIME type được phép
     * @param fieldName tên field (dùng trong thông báo lỗi)
     * @throws IllegalArgumentException nếu không hợp lệ
     */
    public static void validate(MultipartFile file, Set<String> allowed, String fieldName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !allowed.contains(contentType)) {
            throw new IllegalArgumentException(fieldName
                    + " must be one of: " + String.join(", ", allowed));
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException(fieldName
                    + " must be smaller than " + (MAX_SIZE_BYTES / (1024 * 1024)) + "MB");
        }
    }

    /** Validate image avatar (JPEG/PNG ≤ 15MB) */
    public static void validateImageAvatar(MultipartFile imageAvt) {
        validate(imageAvt, ALLOWED_IMAGE_TYPES, "imageAvt");
    }

    /** Validate CV file (TXT/PDF/DOC/DOCX ≤ 15MB) */
    public static void validateCvFile(MultipartFile cvFile) {
        validate(cvFile, ALLOWED_CV_TYPES, "cvFile");
    }
}

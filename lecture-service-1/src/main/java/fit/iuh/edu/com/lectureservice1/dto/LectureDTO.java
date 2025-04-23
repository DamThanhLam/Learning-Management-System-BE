package fit.iuh.edu.com.lectureservice1.dto;

import fit.iuh.edu.com.lectureservice1.model.Lecture;
import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LectureDTO {

    private String id; // UUID, set internally if needed

    @NotBlank(message = "Title is required")
    private String title;

    private String courseId;

    @NotBlank(message = "Chapter is required")
    private int chapter;

    private String description;

    @NotNull(message = "Status is required")
    private String status; // Enum as String (PUBLISHED, DRAFT, DELETED)

    // --- Enum Conversion ---
    public Lecture.Status getStatusAsEnum() {
        try {
            return status != null ? Lecture.Status.valueOf(status) : null;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    public void setStatusFromEnum(Lecture.Status status) {
        this.status = status != null ? status.name() : null;
    }

    // --- Mapper: DTO → Entity ---
    public Lecture toEntity() {
        Lecture lecture = new Lecture();
        lecture.setTitle(this.title);
        lecture.setChapter(chapter);
        lecture.setDescription(this.description);
        lecture.setStatus(this.getStatusAsEnum());
        return lecture;
    }

    // --- Mapper: Entity → DTO ---
    public static LectureDTO fromEntity(Lecture lecture) {
        LectureDTO dto = new LectureDTO();
        dto.setTitle(lecture.getTitle());
        dto.setChapter(lecture.getChapter());
        dto.setDescription(lecture.getDescription());
        dto.setStatusFromEnum(lecture.getStatus());
        dto.setCourseId(lecture.getCourseId());
        return dto;
    }
    public String getDescription() {
        return description != null ? description : "";  // Trả về chuỗi rỗng nếu null
    }
    public String getTitle() {
        return title != null ? title : "";  // Trả về chuỗi rỗng nếu null
    }

}

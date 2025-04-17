package fit.iuh.edu.com.lectureservice1.controller;

import fit.iuh.edu.com.lectureservice1.dto.LectureDTO;
import fit.iuh.edu.com.lectureservice1.dto.PaginatedLecturesDTO;
import fit.iuh.edu.com.lectureservice1.model.Lecture;
import fit.iuh.edu.com.lectureservice1.service.LectureService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/v1/lectures")
public class ServiceController {
    private final LectureService lectureService;

    public ServiceController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    // Get all lectures for a specific course with pagination
    @GetMapping("/courses/{courseId}/lectures")
    public ResponseEntity<PaginatedLecturesDTO> getLecturesByCourse(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(required = false) String lastEvaluatedId,
            @RequestParam(required = false) String lastEvaluatedChapterOrderIndex) {

        PaginatedLecturesDTO result = lectureService.getByCourseId(courseId, pageSize, lastEvaluatedId, lastEvaluatedChapterOrderIndex);
        return result.getLectures().isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }
    

    // Create a new lecture
    @PostMapping(value = "courses/{courseId}/lectures", consumes = "multipart/form-data")
    public ResponseEntity<?> createLecture(
            @PathVariable("courseId") String courseId,  // Extract courseId from the URL path
            @RequestParam("chapter") Integer chapter,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("status") String status,
            @RequestParam(value = "documentFile", required = false) MultipartFile documentFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {
        Map<String, Object> response = new HashMap<>();

        // Create a LectureDTO from the request parameters
        LectureDTO lectureDTO = new LectureDTO();
        lectureDTO.setChapter(chapter);
        lectureDTO.setTitle(title);
        lectureDTO.setDescription(description);
        lectureDTO.setStatus(status);

        // Call the service to create a new lecture
        try{

            Lecture createdLecture = lectureService.createLecture(courseId, lectureDTO, documentFile, videoFile, thumbnailFile);
            response.put("status","success");
            response.put("lecture",createdLecture);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status","error");
            response.put("message",e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }


    // Update an existing lecture
    @PutMapping(value = "/courses/{courseId}/lectures", consumes = "multipart/form-data")
    public ResponseEntity<Lecture> updateLecture(
            @PathVariable String courseId,
            @RequestParam("chapter") Integer chapter,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("status") String status,
            @RequestParam(value = "documentFile", required = false) MultipartFile documentFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {

        LectureDTO lectureDTO = new LectureDTO();
        lectureDTO.setChapter(chapter);
        lectureDTO.setTitle(title);
        lectureDTO.setDescription(description);
        lectureDTO.setStatus(status);

        Lecture updatedLecture = lectureService.updateLecture(courseId, lectureDTO, documentFile, videoFile, thumbnailFile);
        return ResponseEntity.ok(updatedLecture);
    }

//    // Soft delete a lecture
//    @DeleteMapping("/courses/{courseId}/lectures/{id}")
//    public ResponseEntity<Void> deleteLecture(@PathVariable String courseId, @PathVariable String id) {
//        return lectureService.delete(courseId, id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
//    }
}

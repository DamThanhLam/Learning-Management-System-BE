package fit.iuh.edu.com.lectureservice1.controller;

import fit.iuh.edu.com.lectureservice1.dto.LectureDTO;
import fit.iuh.edu.com.lectureservice1.dto.PaginatedLecturesDTO;
import fit.iuh.edu.com.lectureservice1.model.Lecture;
import fit.iuh.edu.com.lectureservice1.service.CourseService;
import fit.iuh.edu.com.lectureservice1.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CourseService courseService;

    public ServiceController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    // Get all lectures for a specific course with pagination
    @GetMapping("/student/courses/{courseId}/lectures")
    public ResponseEntity<?> getLecturesByCourse(
            @PathVariable String courseId) {
        if(courseService.getCourseCourseIdAndUserId(courseId)!=null){
            PaginatedLecturesDTO result = lectureService.getByCourseId(courseId,"PUBLISHED");
            if (result.getLectures() == null || result.getLectures().isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(result);
            }

        }
        return ResponseEntity.badRequest().body(courseService.getCourseCourseIdAndUserId(courseId));

    }
    @GetMapping("/teacher/courses/{courseId}/lectures")
    public ResponseEntity<?> getLecturesByCourseForTeacher(
            @PathVariable String courseId) {
        if(courseService.getCourseCourseIdAndUserId(courseId)!=null){
            PaginatedLecturesDTO result = lectureService.getByCourseId(courseId,"");
            if (result.getLectures() == null || result.getLectures().isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(result);
            }

        }
        return ResponseEntity.badRequest().body(courseService.getCourseCourseIdAndUserId(courseId));

    }
    @GetMapping()
    public ResponseEntity<?> getLectureDetails(
            @RequestParam("id") String id) {
        Lecture lecture = lectureService.getById(id);
        if(lecture != null && courseService.getCourseCourseIdAndUserId(lecture.getCourseId())!=null){
            return ResponseEntity.ok(lectureService.getById(id));
        }
        return ResponseEntity.badRequest().build();

    }

    

    // Create a new lecture
    @PostMapping(value = "courses/{courseId}/lectures", consumes = "multipart/form-data")
    public ResponseEntity<?> createLecture(
            @PathVariable("courseId") String courseId,  // Extract courseId from the URL path
            @RequestParam("chapter") Integer chapter,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("status") String status,
            @RequestParam(value = "documentFile") MultipartFile documentFile,
            @RequestParam(value = "videoFile") MultipartFile videoFile,
            @RequestParam(value = "thumbnailFile") MultipartFile thumbnailFile) {
        Map<String, Object> response = new HashMap<>();

        // Create a LectureDTO from the request parameters
        LectureDTO lectureDTO = new LectureDTO();
        lectureDTO.setChapter(chapter);
        lectureDTO.setCourseId(courseId);
        lectureDTO.setTitle(title);
        lectureDTO.setDescription(description);
        lectureDTO.setStatus(status);
        if(courseService.getCourseCourseIdAndUserId(courseId)!=null){
            // Call the service to create a new lecture
            if(lectureService.getByCourseIdAndChapter(courseId,chapter) == null){
                Lecture createdLecture = lectureService.createLecture(courseId, lectureDTO, documentFile, videoFile, thumbnailFile);
                response.put("status","success");
                response.put("lecture",createdLecture);
                return ResponseEntity.ok(response);
            }else{
                response.put("status","error");
                response.put("message","Chapter already exist");
                return ResponseEntity.badRequest().body(response);
            }
        }

        return ResponseEntity.badRequest().build();

    }


    // Update an existing lecture
    @PutMapping(value = "/lectures/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Lecture> updateLecture(
            @PathVariable String id,
            @RequestParam("chapter") Integer chapter,
            @RequestParam(value="title",required = false) String title,
            @RequestParam(value="description",required = false) String description,
            @RequestParam(value="status",required = false) String status,
            @RequestParam(value = "documentFile", required = false) MultipartFile documentFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {

        LectureDTO lectureDTO = new LectureDTO();
        lectureDTO.setChapter(chapter);
        lectureDTO.setTitle(title);
        lectureDTO.setDescription(description);
        lectureDTO.setStatus(status);
        Lecture lecture = lectureService.getById(id);
        if(lecture != null && courseService.getCourseCourseIdAndUserId(lecture.getCourseId())!=null){
            Lecture updatedLecture = lectureService.updateLecture(id, lectureDTO, documentFile, videoFile, thumbnailFile);
            return ResponseEntity.ok(updatedLecture);
        }
        return ResponseEntity.badRequest().build();

    }

    // Soft delete a lecture
    @GetMapping("/courses/{courseId}/lectures/{chapter}")
    public ResponseEntity<?> deleteLecture(@PathVariable String courseId, @PathVariable Integer chapter) {
        return ResponseEntity.ok(lectureService.getByCourseIdAndChapter(courseId, chapter));
    }
}

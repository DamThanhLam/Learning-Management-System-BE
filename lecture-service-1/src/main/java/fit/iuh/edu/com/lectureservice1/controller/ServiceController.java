package fit.iuh.edu.com.lectureservice1.controller;

import fit.iuh.edu.com.lectureservice1.dto.LectureDTO;
import fit.iuh.edu.com.lectureservice1.dto.PaginatedLecturesDTO;
import fit.iuh.edu.com.lectureservice1.model.Lecture;
import fit.iuh.edu.com.lectureservice1.service.CourseService;
import fit.iuh.edu.com.lectureservice1.service.LectureService;
import fit.iuh.edu.com.lectureservice1.utils.FileValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.Objects;

@Validated
@RestController
@RequestMapping("/api/v1/lectures")
public class ServiceController {
    private final LectureService lectureService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private FileValidationUtil fileValidationUtil;
    public ServiceController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    // Get all lectures for a specific course with pagination
    @GetMapping("/student/courses/{courseId}/lectures")
    public ResponseEntity<?> getLecturesByCourse(
            @PathVariable String courseId) {
//        if(courseService.getCourseByCourseIdAndStudentId(courseId)!=null){
        if(true){
            PaginatedLecturesDTO result = lectureService.getByCourseId(courseId,"PUBLISHED");
            if (result.getLectures() == null || result.getLectures().isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(result);
            }

        }
        return ResponseEntity.badRequest().build();

    }
    @GetMapping("/teacher/courses/{courseId}/lectures")
    public ResponseEntity<?> getLecturesByCourseForTeacher(
            @PathVariable String courseId) {
        if(courseService.getCourseByCourseIdAndTeacherId(courseId)!=null){
            PaginatedLecturesDTO result = lectureService.getByCourseId(courseId,"");
            if (result.getLectures() == null || result.getLectures().isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(result);
            }

        }
        return ResponseEntity.badRequest().build();

    }
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping()
    public ResponseEntity<?> getLectureDetails(
            @RequestParam("id") String id) {
        Lecture lecture = lectureService.getById(id);
        System.out.println(id);

        System.out.println(lecture);
        if(lecture != null && courseService.getCourseByCourseIdAndTeacherId(lecture.getCourseId())!=null){
            return ResponseEntity.ok(lecture);
        }
        return ResponseEntity.badRequest().build();

    }




    @PostMapping(value = "courses/{courseId}/lectures", consumes = "multipart/form-data")
    public ResponseEntity<?> createLecture(
            @PathVariable("courseId") String courseId,
            @RequestParam("chapter") Integer chapter,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("status") String status,
            @RequestParam("documentFile") MultipartFile documentFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {

        Map<String, Object> response = new HashMap<>();

        // Validate document file (bắt buộc)
        if (!fileValidationUtil.isValidFile(documentFile,
                new String[]{"application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
                15 * 1024 * 1024)) { // 15MB
            response.put("status", "error");
            response.put("message", "Invalid document file. Only PDF or DOCX under 15MB allowed.");
            return ResponseEntity.badRequest().body(response);
        }

        // Validate video file (nếu có)
        if (videoFile != null && !videoFile.isEmpty()) {
            if (!fileValidationUtil.isValidFile(videoFile,
                    new String[]{"video/mp4", "video/quicktime"},
                    1024 * 1024 * 1024)) { // 1GB
                response.put("status", "error");
                response.put("message", "Invalid video file. Only MP4 or MOV under 1GB allowed.");
                return ResponseEntity.badRequest().body(response);
            }
        }

        // Validate thumbnail file (nếu có)
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            if (!fileValidationUtil.isValidFile(thumbnailFile,
                    new String[]{"image/jpeg", "image/png"},
                    15 * 1024 * 1024)) { // 15MB
                response.put("status", "error");
                response.put("message", "Invalid thumbnail file. Only JPG or PNG under 15MB allowed.");
                return ResponseEntity.badRequest().body(response);
            }
        }

        // Create a LectureDTO from the request parameters
        LectureDTO lectureDTO = new LectureDTO();
        lectureDTO.setChapter(chapter);
        lectureDTO.setCourseId(courseId);
        lectureDTO.setTitle(title);
        lectureDTO.setDescription(description);
        lectureDTO.setStatus(status);

        if (courseService.getCourseByCourseIdAndTeacherId(courseId) != null) {
            // Check chapter exist
            if (lectureService.getByCourseIdAndChapter(courseId, chapter) == null) {
                Lecture createdLecture = lectureService.createLecture(courseId, lectureDTO, documentFile, videoFile, thumbnailFile);
                response.put("status", "success");
                response.put("lecture", createdLecture);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Chapter already exists");
                return ResponseEntity.badRequest().body(response);
            }
        }

        return ResponseEntity.badRequest().build();
    }



    // Update an existing lecture
    @PutMapping(value = "/courses/{courseId}/lectures/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateLecture(
            @PathVariable("courseId") String courseId,
            @PathVariable("id") String id,
            @RequestParam("chapter") Integer chapter,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "documentFile", required = false) MultipartFile documentFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) {

        Map<String, Object> response = new HashMap<>();

        // 1. Lấy lecture cũ và check quyền
        Lecture existing = lectureService.getById(id);
        if (existing == null
                || courseService.getCourseByCourseIdAndTeacherId(existing.getCourseId()) == null) {
            response.put("status", "error");
            response.put("message", "Lecture not found or no permission");
            return ResponseEntity.badRequest().body(response);
        }

        // 2. Nếu đổi chapter, kiểm tra không trùng với lecture khác
        if (!Objects.equals(existing.getChapter(), chapter)
                && lectureService.getByCourseIdAndChapter(courseId, chapter) != null) {
            response.put("status", "error");
            response.put("message", "Chapter " + chapter + " already exists");
            return ResponseEntity.badRequest().body(response);
        }

        // 3. Validate files nếu có upload mới
        if (documentFile != null && !documentFile.isEmpty()) {
            if (!fileValidationUtil.isValidFile(documentFile,
                    new String[]{
                            "application/pdf",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
                    15 * 1024 * 1024)) {
                response.put("status", "error");
                response.put("message", "Invalid document file. Only PDF/DOCX under 15MB allowed.");
                return ResponseEntity.badRequest().body(response);
            }
        }
        if (videoFile != null && !videoFile.isEmpty()) {
            if (!fileValidationUtil.isValidFile(videoFile,
                    new String[]{"video/mp4", "video/quicktime"},
                    1024 * 1024 * 1024)) {
                response.put("status", "error");
                response.put("message", "Invalid video file. Only MP4/MOV under 1GB allowed.");
                return ResponseEntity.badRequest().body(response);
            }
        }
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            if (!fileValidationUtil.isValidFile(thumbnailFile,
                    new String[]{"image/jpeg", "image/png"},
                    15 * 1024 * 1024)) {
                response.put("status", "error");
                response.put("message", "Invalid thumbnail file. Only JPG/PNG under 15MB allowed.");
                return ResponseEntity.badRequest().body(response);
            }
        }

        // 4. Build DTO và gọi service
        LectureDTO dto = new LectureDTO();
        dto.setChapter(chapter);
        dto.setTitle(title != null ? title : existing.getTitle());
        dto.setDescription(description != null ? description : existing.getDescription());
        dto.setStatus(status != null ? status : String.valueOf(existing.getStatus()));

        Lecture updated = lectureService.updateLecture(
                id, dto, documentFile, videoFile, thumbnailFile);

        response.put("status", "success");
        response.put("lecture", updated);
        return ResponseEntity.ok(response);
    }


//    @GetMapping("/courses/{courseId}/lectures/{chapter}")
//    public ResponseEntity<?> getLecture(@PathVariable String courseId, @PathVariable Integer chapter) {
//        return ResponseEntity.ok(lectureService.getByCourseIdAndChapter(courseId, chapter));
//    }
}

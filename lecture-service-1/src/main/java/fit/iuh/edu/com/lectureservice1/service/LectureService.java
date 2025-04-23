package fit.iuh.edu.com.lectureservice1.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import fit.iuh.edu.com.lectureservice1.dto.LectureDTO;
import fit.iuh.edu.com.lectureservice1.dto.PaginatedLecturesDTO;
import fit.iuh.edu.com.lectureservice1.model.Course;
import fit.iuh.edu.com.lectureservice1.model.Lecture;
import fit.iuh.edu.com.lectureservice1.repository.CourseRepository;
import fit.iuh.edu.com.lectureservice1.repository.LectureRepository;
import fit.iuh.edu.com.lectureservice1.utils.FileValidationUtil;
import fit.iuh.edu.com.lectureservice1.utils.VideoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class LectureService {
	private final LectureRepository lectureRepository;
	private final S3Service s3Service;
	
	private static final long MAX_DOCUMENT_SIZE = 15 * 1024 * 1024; // 15MB
	private static final long MAX_VIDEO_SIZE = 1024L * 1024 * 1024; // 1GB
	private static final long MAX_THUMBNAIL_SIZE = 15 * 1024 * 1024; // 15MB
	private final CourseRepository courseRepository;

	@Value("${aws.s3.folder}")
	private String folderRoot;

	@Value("${aws.s3.folder.images}")
	private String folderImages;

	@Value("${aws.s3.folder.videos}")
	private String folderVideos;
	@Value("${aws.s3.folder.files}")
	private String folderFiles;

	private static final List<String> ALLOWED_DOCUMENT_TYPES = List.of(
	    "application/pdf",
	    "application/msword",
	    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
	    "text/plain"
	);

	private static final List<String> ALLOWED_VIDEO_TYPES = List.of(
	    "video/x-m4v",
	    "video/quicktime",
	    "video/mp4"
	);

	private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
	    "image/jpeg",
	    "image/png"
	);

	public LectureService(LectureRepository lectureRepository, S3Service s3Service, CourseRepository courseRepository) {
		this.lectureRepository = lectureRepository;
		this.s3Service = s3Service;
		this.courseRepository = courseRepository;
	}

//	public List<Lecture> getByCourseId(String courseId) {
//		return lectureRepository.findByCourseId(courseId);
//	}

	public PaginatedLecturesDTO getByCourseId(String courseId,String status) {

		return lectureRepository.findByCourseId(courseId, status);
	}



	public Lecture getById(String id) {
		return lectureRepository.findById(id);
	}

	public Lecture createLecture(String courseId, LectureDTO dto, MultipartFile documentFile, MultipartFile videoFile,
								 MultipartFile thumbnailFile) {
			// Determine type based on provided files
		Lecture.ComputedType type;
		if (videoFile != null && !videoFile.isEmpty() && documentFile != null && !documentFile.isEmpty()) {
			type = Lecture.ComputedType.MIXED;
		} else if (videoFile != null && !videoFile.isEmpty()) {
			type = Lecture.ComputedType.VIDEO;
		} else if (documentFile != null && !documentFile.isEmpty()) {
			type = Lecture.ComputedType.DOCUMENT;
		} else {
			type = Lecture.ComputedType.UNKNOWN;
		}

		// Validation before upload
		FileValidationUtil.validateFile(documentFile, MAX_DOCUMENT_SIZE, ALLOWED_DOCUMENT_TYPES, "Document");
		FileValidationUtil.validateFile(videoFile, MAX_VIDEO_SIZE, ALLOWED_VIDEO_TYPES, "Video");
		FileValidationUtil.validateFile(thumbnailFile, MAX_THUMBNAIL_SIZE, ALLOWED_IMAGE_TYPES, "Thumbnail");

		// Upload files to S3
		String documentUrl = (documentFile != null && !documentFile.isEmpty()) ? s3Service.uploadFile(documentFile,folderFiles)	: null;
		String videoUrl = (videoFile != null && !videoFile.isEmpty()) ? s3Service.uploadFile(videoFile,folderVideos) : null;
		String thumbnailUrl = (thumbnailFile != null && !thumbnailFile.isEmpty()) ? s3Service.uploadFile(thumbnailFile,folderImages) : null;

		// Extract video duration (in seconds) if video is provided
		Integer duration = null;
		if (videoFile != null && !videoFile.isEmpty()) {
			duration = VideoUtil.extractVideoDurationInSeconds(videoFile); // Get the video duration
		}

		// Create and populate a lecture entity
		Lecture lecture = dto.toEntity();
		lecture.setCourseId(courseId);
		lecture.setType(type);
		lecture.setDocumentUrl(documentUrl);
		lecture.setVideoUrl(videoUrl);
		lecture.setThumbnailUrl(thumbnailUrl);
		lecture.setDuration(duration);
		lecture.setCreatedAt(Instant.now());
		lecture.setUpdatedAt(Instant.now());
		lecture.setId(UUID.randomUUID().toString());

		Course course = courseRepository.getId(lecture.getCourseId());
		course.setCountLectures(course.getCountLectures()+1);
		courseRepository.update(course);
		return lectureRepository.save(lecture);

	}

	public Lecture updateLecture(String id, LectureDTO dto, MultipartFile documentFile,
			MultipartFile videoFile, MultipartFile thumbnailFile) {

		Lecture existingLecture = lectureRepository.findById(id);
		if (existingLecture == null) {
			throw new RuntimeException("Lecture not found");
		}

		// Handle the type based on provided files
		Lecture.ComputedType type = existingLecture.getType(); // Default to the existing type
		if (videoFile != null && !videoFile.isEmpty() && documentFile != null && !documentFile.isEmpty()) {
		    type = Lecture.ComputedType.MIXED;
		} else if (videoFile != null && !videoFile.isEmpty()) {
		    type = Lecture.ComputedType.VIDEO;
		} else if (documentFile != null && !documentFile.isEmpty()) {
		    type = Lecture.ComputedType.DOCUMENT;
		} else {
		    type = existingLecture.getType();
		}

		// Extract video duration if a new video is provided
		Integer duration = existingLecture.getDuration();  // Keep the old duration if no new video
		if (videoFile != null && !videoFile.isEmpty()) {
		    duration = VideoUtil.extractVideoDurationInSeconds(videoFile); // Extract duration
		}

		// Validate and replace files if needed
		if (documentFile != null && !documentFile.isEmpty()) {
			// Validate document file
			FileValidationUtil.validateFile(documentFile, MAX_DOCUMENT_SIZE, ALLOWED_DOCUMENT_TYPES, "Document");
			s3Service.replaceFile(existingLecture.getDocumentUrl(), documentFile, folderFiles);
			existingLecture.setDocumentUrl(s3Service.uploadFile(documentFile,folderFiles));
		}

		if (videoFile != null && !videoFile.isEmpty()) {
			// Validate video file
			FileValidationUtil.validateFile(videoFile, MAX_VIDEO_SIZE, ALLOWED_VIDEO_TYPES, "Video");
			s3Service.replaceFile(existingLecture.getVideoUrl(), videoFile, folderVideos);
			existingLecture.setVideoUrl(s3Service.uploadFile(videoFile,folderVideos));
		}

		if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
			// Validate thumbnail file
			FileValidationUtil.validateFile(thumbnailFile, MAX_THUMBNAIL_SIZE, ALLOWED_IMAGE_TYPES, "Thumbnail");
			s3Service.replaceFile(existingLecture.getThumbnailUrl(), thumbnailFile, folderImages);
			existingLecture.setThumbnailUrl(s3Service.uploadFile(thumbnailFile,folderImages));
		}

		// Update basic fields
		if(!dto.getTitle().isEmpty()){
			existingLecture.setTitle(dto.getTitle());
		}
		if(!dto.getDescription().isEmpty()){
			existingLecture.setDescription(dto.getDescription());
		}
		try{
			if(dto.getStatusAsEnum()!=null){
				existingLecture.setStatus(dto.getStatusAsEnum());
			}
		} catch (Exception e) {
//			throw new RuntimeException(e);
		}
		existingLecture.setType(type);  // Update the lecture type
		existingLecture.setDuration(duration);  // Update the video duration if applicable
		existingLecture.setUpdatedAt(Instant.now());

		// Save and return the updated lecture
		return lectureRepository.save(existingLecture);
	}



	public Lecture getByCourseIdAndChapter(String courseId, Integer chapter) {
		return lectureRepository.findByCourseIdAndChapter(courseId, chapter);
	}
}

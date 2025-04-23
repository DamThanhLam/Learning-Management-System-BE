package fit.iuh.edu.com.lectureservice1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@DynamoDbBean
public class Lecture {
	private String id;
	private String courseId; // Partition key
	private int chapter; // Chapter the lecture belongs to
	private String title; // Title of the lecture
	private ComputedType type; // Computed from available content
	private String description; // Optional description text
	private String documentUrl; // PDF/doc link
	private String videoUrl; // Video file link
	private String thumbnailUrl; // Thumbnail image link
	private Integer duration; // video's duration in seconds
	private Instant createdAt; // Creation timestamp
	private Instant updatedAt; // Last update timestamp
	private Status status; // Lecture status (OPENED, DELETED, DRAFT)

	@DynamoDbPartitionKey
	public String getId() {
		return id;
	}

	@DynamoDbSecondaryPartitionKey(indexNames = "courseId-chapter-index")
	public String getCourseId() {
		return courseId;
	}
	@DynamoDbSecondarySortKey(indexNames = "courseId-chapter-index")
	@DynamoDbAttribute("chapter")
	public int getChapter() {
		return chapter;
	}


	@DynamoDbAttribute("title")
	public String getTitle() {
		return title;
	}

	@DynamoDbAttribute("description")
	public String getDescription() {
		return description;
	}

	@DynamoDbAttribute("documentUrl")
	public String getDocumentUrl() {
		return documentUrl;
	}

	@DynamoDbAttribute("videoUrl")
	public String getVideoUrl() {
		return videoUrl;
	}

	@DynamoDbAttribute("thumbnailUrl")
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	
	@DynamoDbAttribute("duration")
	public Integer getDuration() {
	    return duration;
	}

	public ComputedType getType() {
		return type;
	}

	@DynamoDbAttribute("createdAt")
	public Instant getCreatedAt() {
		return createdAt;
	}

	@DynamoDbAttribute("updatedAt")
	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public Status getStatus() {
		return status;
	}

	// --- Enum for Computed Type ---
	public enum ComputedType {
		VIDEO, DOCUMENT, MIXED, UNKNOWN
	}

	public enum Status {
		PUBLISHED, DRAFT, DELETED
	}

	@DynamoDbAttribute("type")
	public String getTypeAsString() {
		return type != null ? type.name() : null;
	}

	public void setTypeFromString(String typeAsString) {
		this.type = typeAsString != null ? ComputedType.valueOf(typeAsString) : null;
	}

	@DynamoDbAttribute("status")
	public String getStatusAsString() {
		return status != null ? status.name() : null;
	}

	public void setStatusFromString(String statusAsString) {
		this.status = statusAsString != null ? Status.valueOf(statusAsString) : null;
	}
}

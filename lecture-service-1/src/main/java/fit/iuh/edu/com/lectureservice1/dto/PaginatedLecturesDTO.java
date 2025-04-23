package fit.iuh.edu.com.lectureservice1.dto;

import fit.iuh.edu.com.lectureservice1.model.Lecture;

import java.util.List;

public class PaginatedLecturesDTO {
    private List<Lecture> lectures;
    private String lastEvaluatedId;
    private String lastEvaluatedChapter;

    public PaginatedLecturesDTO(List<Lecture> lectures, String lastEvaluatedId, String lastEvaluatedChapter) {
        this.lectures = lectures;
        this.lastEvaluatedId = lastEvaluatedId;
        this.lastEvaluatedChapter = lastEvaluatedChapter;
    }

    public List<Lecture> getLectures() {
        return lectures;
    }

    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
    }

    public String getLastEvaluatedId() {
        return lastEvaluatedId;
    }

    public void setLastEvaluatedId(String lastEvaluatedId) {
        this.lastEvaluatedId = lastEvaluatedId;
    }

    public String getLastEvaluatedChapterOrderIndex() {
        return lastEvaluatedChapter;
    }

    public void setLastEvaluatedChapterOrderIndex(String lastEvaluatedChapterOrderIndex) {
        this.lastEvaluatedChapter = lastEvaluatedChapterOrderIndex;
    }
}

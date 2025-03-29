package fit.iuh.edu.com.services.BL;

import fit.iuh.edu.com.models.Teacher;

public interface TeacherServiceBL {
    public void mapReviewToTeacher(String reviewId,String courseId);
    public Teacher getByCognitoId(String cognitoId);

}

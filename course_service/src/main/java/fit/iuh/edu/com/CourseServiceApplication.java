package fit.iuh.edu.com;

import fit.iuh.edu.com.services.BL.CourseServiceBL;
import fit.iuh.edu.com.services.Impl.CourseServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CourseServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        // Tạo 10 nguồn dữ liệu
        CourseServiceBL sourceServiceBL = new CourseServiceImpl();

//        for (int i = 1; i <= 10; i++) {
//            try {
//                System.out.println("add: " + i);
//                Source source = new Source();
//                source.setSourceName("SourceName-" + i);
//                source.setDescription("Description for Source " + i);
//                source.setPrice(19.99 + i); // Giá sẽ tăng dần
//                source.setCategory("Category-" + i);
//                source.setCreateTime(LocalDateTime.of(2025, 1, 1, 10, 0, 0));
//                source.setUpdateTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0));
//                source.setOpenTime(LocalDateTime.of(2025, 1, 1, 9, 0, 0));
//                source.setCloseTime(LocalDateTime.of(2025, 1, 1, 17, 0, 0));
//                source.setStartTime(LocalDateTime.of(2025, 1, 1, 9, 30, 0));
//                source.setCompleteTime(LocalDateTime.of(2025, 1, 1, 16, 30, 0));
//                source.setStatus(SourceStatus.OPEN);
//                source.setUrlAvt("http://example.com/" + i + ".jpg");
//                source.setCreatedBy("admin");
//                source.setUpdatedBy("admin");
//                source.setTeacherId(101);
//                source.setTeacherName(i); // Mỗi nguồn có tên giáo viên khác nhau
//                source.setNumberMinimum(5);
//                source.setNumberMaximum(20);
//                source.setNumberCurrent(15);
//                source.setStudentIds(null);
//
//                // Lưu dữ liệu vào DynamoDB
//                sourceServiceBL.create(source);
//
//                // Thông báo thêm thành công
//                System.out.println("Successfully added source " + i);
//            } catch (Exception e) {
//                System.out.println("Failed to add source " + i + ": " + e.getMessage());
//            }
//        }
//        sourceServiceBL.findBySourceName("SourceName",null,10);
    }
}

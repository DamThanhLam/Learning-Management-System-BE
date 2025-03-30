package email_service_lms.services;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class EmailService {
    @Value("${spring.mail.username}")
    private String fromEmail;

    @PostConstruct
    void check(){
        System.out.println("User email: " + fromEmail);
    }

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        String htmlContent = templateEngine.process(templateName, context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true nghĩa là HTML email
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email HTML", e);
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String templateName, Context context, byte[] excelFileData) throws MessagingException {
        String htmlContent = templateEngine.process(templateName, context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true nghĩa là gửi email dạng HTML

            // Đính kèm file Excel
            helper.addAttachment("account-lock-details.xlsx", new ByteArrayDataSource(excelFileData, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            // Gửi email
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email với tệp đính kèm", e);
        }
    }



    // Tạo file Excel và trả về dưới dạng byte array
    public byte[] createExcelFile() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Tạo dữ liệu mẫu
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Header 1");
        row.createCell(1).setCellValue("Header 2");

        row = sheet.createRow(1);
        row.createCell(0).setCellValue("Data 1");
        row.createCell(1).setCellValue("Data 2");

        // Xuất ra byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}

package fit.iuh.edu.com.export;

import fit.iuh.edu.com.models.Feedback;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExcelExporter {

    public static void exportFeedbackListToExcel(List<Feedback> feedbacks, String filePath) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Feedbacks");

        // Tạo hàng tiêu đề
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "User ID", "Teacher ID", "Title", "Content", "URL Images"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(createHeaderCellStyle(workbook));
        }

        // Ghi dữ liệu vào các hàng
        int rowNum = 1;
        for (Feedback feedback : feedbacks) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(feedback.getId());
            row.createCell(1).setCellValue(feedback.getUserId());
            row.createCell(2).setCellValue(feedback.getTeacherId());
            row.createCell(3).setCellValue(feedback.getTitle());
            row.createCell(4).setCellValue(feedback.getContent());

            // Chuyển danh sách URL Images thành một chuỗi
            String urlImagesString = String.join(", ", feedback.getUrlImages());
            row.createCell(5).setCellValue(urlImagesString);
        }

        // Tự động điều chỉnh kích thước cột
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Ghi workbook vào file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }

    public static byte[] exportAndReadExcel(List<Feedback> feedbacks, Path path) {
        try {
            // Xuất file Excel vào thư mục tạm

            // Gọi phương thức xuất file
            ExcelExporter.exportFeedbackListToExcel(feedbacks, String.valueOf(path));

            // Đọc file ra mảng byte
            byte[] excelFileData = Files.readAllBytes(path);

            // Xóa file sau khi đọc (nếu cần)
            Files.deleteIfExists(path);

            return excelFileData;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


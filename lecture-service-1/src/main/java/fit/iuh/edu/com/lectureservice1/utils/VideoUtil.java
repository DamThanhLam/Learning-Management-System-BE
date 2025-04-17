package fit.iuh.edu.com.lectureservice1.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.PointerPointer;

public class VideoUtil {

    public static Integer extractVideoDurationInSeconds(MultipartFile multipartFile) {
        File tempFile = null;
        AVFormatContext formatContext = null;

        try {
            // Save the uploaded file to a temporary file
            tempFile = File.createTempFile("video", ".mp4");
            try (InputStream is = multipartFile.getInputStream();
                 FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            // Quiet logging
            avutil.av_log_set_level(avutil.AV_LOG_QUIET);

            // Open the video file
            formatContext = avformat.avformat_alloc_context();
            if (avformat.avformat_open_input(formatContext, tempFile.getAbsolutePath(), null, null) != 0) {
                throw new RuntimeException("Couldn't open video file.");
            }

            if (avformat.avformat_find_stream_info(formatContext, (PointerPointer) null) < 0) {
                throw new RuntimeException("Couldn't retrieve stream info.");
            }

            // Get the duration in microseconds and convert to seconds
            long durationMicroseconds = formatContext.duration();
            return (int) (durationMicroseconds / 1_000_000);

        } catch (Exception e) {
            e.printStackTrace(); // log it however you want
            return null;
        } finally {
            if (formatContext != null) {
                avformat.avformat_close_input(formatContext);
            }
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}

package com.nhom_09.productservice.controller;

import com.nhom_09.productservice.repository.storage.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{filename:.+}")
    //@ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        Resource file = fileStorageService.load(filename);

        // Xác định Content-Type của file
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Bỏ qua nếu không xác định được
        }

        // Cung cấp một giá trị mặc định nếu không xác định được
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                // SET CONTENT-TYPE Ở ĐÂY
                .contentType(MediaType.parseMediaType(contentType))
                //Gửi cho client tự động hiển thị ảnh khi tải lên và đường dẫn gợi ý khi tải file ảnh về
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}

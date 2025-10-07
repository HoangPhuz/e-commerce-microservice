package com.nhom_09.productservice.service.storage;

import com.nhom_09.productservice.repository.storage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileSystemStorageService implements FileStorageService {


    private final Path rootLocation;

    public FileSystemStorageService() {
        // Lấy đường dẫn từ file properties hoặc đặt giá trị mặc định
        this.rootLocation = Paths.get("uploads/images");
        init();
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Không thể khởi tạo bộ nhớ", e);
        }
    }

    //Lưu file ảnh vào thư mục
    @Override
    public String save(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Không thể lưu tệp trống.");
        }
        try {
            // Tạo tên file ngẫu nhiên để tránh trùng lặp
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String newFilename = UUID.randomUUID().toString() + "." + extension;

            //Tạo đường dẫn chưá file ảnh trong thư mục
            Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();

            //Kiểm tra đường dẫn chưá file ảnh có nằm ngoài thư mục cho phép
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Không thể lưu tệp ngoài thư mục hiện tại.");
            }

            //Copy file ảnh vào đường dẫn được chứa ảnh
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return newFilename; // Trả về tên file đã lưu
        } catch (IOException e) {
            throw new RuntimeException("Không lưu được tệp.", e);
        }
    }

    //Lấy đường dẫn chứa file ảnh trong thư mục
    @Override
    public Resource load(String filename) {
        try {
            //Nối đường dẫn của thư mục với tên file ảnh
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Không thể đọc tệp:" + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Không thể đọc tệp:" + filename, e);
        }
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }


    //Xoá file ảnh trong thư mục
    @Override
    public void delete(String filename) {
        if (filename == null || filename.isBlank()) {
            return;
        }
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
            log.info("Đã xóa file: {}", filename);
        } catch (IOException e) {
            log.error("Không thể xóa file: {}", filename, e);
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}

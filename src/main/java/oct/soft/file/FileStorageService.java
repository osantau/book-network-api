package oct.soft.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oct.soft.book.Book;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

	@Value("${application.file.upload.photos-output-path}")
	private String fileUploadPath;

	public String saveFile(@NonNull MultipartFile sourceFile, @NonNull Long userId) {
		final String fileUploadSubpath = "users" + File.separator + userId;
		return uploadFile(sourceFile, fileUploadSubpath);
	}

	private String uploadFile(@NonNull MultipartFile sourceFile, @NonNull String fileUploadSubpath) {
		final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubpath;
		File targetFolder = new File(finalUploadPath);
		if (!targetFolder.exists()) {
			boolean folderCreated = targetFolder.mkdirs();
			if (!folderCreated) {
				log.warn("Failed to create target folder");
				return null;
			}
		}

		final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
		String targetFilePath = fileUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension;
		Path targetPath = Paths.get(targetFilePath);
		try {
			Files.write(targetPath, sourceFile.getBytes());
			log.info("File save to ", targetFilePath);
		} catch (Exception ex) {
			log.error("File was not saved ", ex);
		}
		return null;
	}

	private String getFileExtension(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return "";

		}
		int lastDotIndex = fileName.lastIndexOf(".");
		if (lastDotIndex == -1) {
			return "";
		}

		return fileName.substring(lastDotIndex + 1);
	}

}

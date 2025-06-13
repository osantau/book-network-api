package oct.soft.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

	public static byte[] readFileFromLocation(String fileUrl) {

		if (StringUtils.isBlank(fileUrl)) {
			return null;
		}

		try {
			Path filePath = new File(fileUrl).toPath();
			return Files.readAllBytes(filePath);
		} catch (IOException ex) {
			log.error("No file found in the path {}", fileUrl);
		}

		return new byte[0];
	}

}

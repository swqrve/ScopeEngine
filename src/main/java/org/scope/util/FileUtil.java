package org.scope.util;

import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FileUtil {
    @SneakyThrows // TODO: Replace sneaky throws in the future...
    public static String loadResource(String fileName) {
        if (fileName.charAt(0) != '/') fileName = "/" + fileName;

        String result;
        try (InputStream in = FileUtil.class.getResourceAsStream(fileName)) {
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name());
            result = scanner.useDelimiter("\\a").next();
        }

        return result;
    }
}

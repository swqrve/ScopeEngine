package org.scope.util;

import lombok.SneakyThrows;
import org.bson.Document;
import org.scope.logger.Debug;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtil {
    @SneakyThrows
    public static String loadResource(String fileName) {
        if (fileName.charAt(0) != '/') fileName = "/" + fileName;

        String result;
        try (InputStream in = FileUtil.class.getResourceAsStream(fileName)) {
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name());
            result = scanner.useDelimiter("\\a").next();
        }

        return result;
    }

    @SneakyThrows
    public static List<String> loadResourceAsList(String fileName) {
        if (fileName.charAt(0) != '/') fileName = "/" + fileName;

        List<String> result = new ArrayList<>();

        try (InputStream in = FileUtil.class.getResourceAsStream(fileName)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) result.add(line);

        }

        return result;
    }

    @SneakyThrows
    public static Document readDocFromFile(String directory) {
        if (loadResource(directory) == null) {
            Debug.log(Debug.LogLevel.ERROR, "Could not find the directory for the JSON file you're attempting to parse! Directory: " + directory);
            return null;
        }

        return Document.parse(loadResource(directory));
    }

    @SneakyThrows
    public static void write(File file, Document document) {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(document.toJson());
        fileWriter.close();
    }
}

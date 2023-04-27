package org.scope.util;

import lombok.SneakyThrows;
import org.bson.Document;
import org.lwjgl.BufferUtils;
import org.scope.logger.Debug;
import org.scope.render.model.struct.Texture;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public static ByteBuffer fileDirToBuffer(String fileName) {
        InputStream in = FileUtil.class.getResourceAsStream(fileName);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] fileBytes = new byte[1024];

        int length;
        while ((length = in.read(fileBytes)) != -1) out.write(fileBytes, 0, length);

        byte[] fileData = out.toByteArray();

        ByteBuffer buffer = BufferUtils.createByteBuffer(fileData.length);
        buffer.put(fileData);
        ((Buffer) buffer).flip();

        return buffer;
    }

    @SneakyThrows
    public static void write(File file, Document document) {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(document.toJson());
        fileWriter.close();
    }
}

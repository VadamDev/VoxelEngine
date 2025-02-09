package net.vadamdev.voxel.engine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public final class FileUtils {
    private FileUtils() {}

    public static String readFile(String path) throws IOException {
        final StringBuilder result = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.class.getResourceAsStream(path)));

        String line;
        while((line = reader.readLine()) != null)
            result.append(line).append("\n");

        reader.close();

        return result.toString();
    }
}

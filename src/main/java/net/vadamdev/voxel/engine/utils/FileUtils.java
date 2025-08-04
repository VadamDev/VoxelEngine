package net.vadamdev.voxel.engine.utils;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONAware;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author VadamDev
 * @since 03/02/2025
 */
public final class FileUtils {
    private FileUtils() {}

    public static String readFile(String path) throws IOException, NullPointerException {
        final InputStream in = FileUtils.class.getResourceAsStream(path);
        if(in == null)
            throw new NullPointerException("File not found: " + path);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        final String result = reader.lines().collect(Collectors.joining("\n"));

        reader.close();

        return result;
    }

    public static List<String> getResourcesInPath(String path) throws URISyntaxException, IOException {
        final List<String> result = new ArrayList<>();

        final URL url = FileUtils.class.getClassLoader().getResource(path);
        if(url == null)
            return result;

        switch(url.getProtocol()) {
            case "file":
                Files.walk(Paths.get(url.toURI()), 1).filter(Files::isRegularFile).forEach(file -> result.add(path + "/" + file.getFileName()));
                break;
            case "jar":
                final JarFile jar = new JarFile(URLDecoder.decode(url.getPath().substring(5, url.getPath().indexOf("!")), StandardCharsets.UTF_8));

                final Enumeration<JarEntry> entries = jar.entries();
                while(entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    final String name = entry.getName();

                    if(name.startsWith(path + "/") && !entry.isDirectory())
                        result.add(name);
                }

                jar.close();

                break;
            default:
                break;
        }

        return result;
    }

    public static <T extends JSONAware> T parseJson(String path, @NotNull T fallback) throws IOException, NullPointerException, ParseException {
        final Object parsed = new JSONParser().parse(readFile(path));

        if(!parsed.getClass().isAssignableFrom(fallback.getClass()))
            return fallback;

        return (T) parsed;
    }
}

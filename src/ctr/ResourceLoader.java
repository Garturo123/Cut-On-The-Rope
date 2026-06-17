package ctr;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public final class ResourceLoader {
    private ResourceLoader() {
    }

    public static InputStream open(String resource) throws IOException {
        String normalized = normalize(resource);

        InputStream stream = ResourceLoader.class.getResourceAsStream("/" + normalized);
        if (stream == null) {
            ClassLoader classLoader = ResourceLoader.class.getClassLoader();
            if (classLoader != null) {
                stream = classLoader.getResourceAsStream(normalized);
            }
        }
        if (stream != null) {
            return new BufferedInputStream(stream);
        }

        Path[] candidates = new Path[] {
            Paths.get(normalized),
            Paths.get("src", normalized),
            Paths.get("build", "classes", normalized)
        };

        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                return new BufferedInputStream(Files.newInputStream(candidate));
            }
        }

        throw new IOException("Resource not found: " + resource);
    }

    public static BufferedImage loadImage(String resource) throws IOException {
        try (InputStream stream = open(resource)) {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new IOException("Resource is not a readable image: " + resource);
            }
            return image;
        }
    }

    private static String normalize(String resource) throws IOException {
        if (resource == null) {
            throw new IOException("Resource path is null");
        }

        String normalized = resource.replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.isEmpty()) {
            throw new IOException("Resource path is empty");
        }
        return normalized;
    }
}

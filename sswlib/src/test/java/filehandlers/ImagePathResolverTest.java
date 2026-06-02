package filehandlers;

import common.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.prefs.Preferences;

import static org.junit.Assert.assertEquals;

public class ImagePathResolverTest {
    private Preferences prefs;
    private String previousDefaultImagePath;

    @Before
    public void setUp() {
        prefs = Preferences.userRoot().node(Constants.SSWPrefs);
        previousDefaultImagePath = prefs.get("DefaultImagePath", null);
    }

    @After
    public void tearDown() {
        if (previousDefaultImagePath == null) {
            prefs.remove("DefaultImagePath");
        } else {
            prefs.put("DefaultImagePath", previousDefaultImagePath);
        }
    }

    @Test
    public void keepsBlankImagePathBlank() {
        ImagePathResolver resolver = new ImagePathResolver();

        assertEquals("", resolver.resolve(""));
    }

    @Test
    public void resolvesExistingAbsoluteImagePath() throws Exception {
        File image = Files.createTempFile("balgaire", ".png").toFile();
        ImagePathResolver resolver = new ImagePathResolver();

        assertEquals(image.getCanonicalPath(), resolver.resolve(image.getAbsolutePath()));
    }

    @Test
    public void resolvesRelativePathFromDefaultImageDirectory() throws Exception {
        File directory = Files.createTempDirectory("ssw-images").toFile();
        File image = new File(directory, "Balgaire.png");
        Files.write(image.toPath(), new byte[] {1});
        prefs.put("DefaultImagePath", directory.getAbsolutePath());
        ImagePathResolver resolver = new ImagePathResolver();

        assertEquals(image.getCanonicalPath(), resolver.resolve("Balgaire.png"));
    }

    @Test
    public void resolvesMatchingImageFromDefaultImageDirectory() throws Exception {
        File directory = Files.createTempDirectory("ssw-images").toFile();
        File image = new File(directory, "balgaire_line_art.png");
        Files.write(image.toPath(), new byte[] {1});
        prefs.put("DefaultImagePath", directory.getAbsolutePath());
        ImagePathResolver resolver = new ImagePathResolver();

        assertEquals(image.getCanonicalPath(), resolver.resolve("missing.png", "Balgaire", "BGR-2"));
    }

    @Test
    public void preservesStoredPathWhenNoResolutionExists() {
        ImagePathResolver resolver = new ImagePathResolver();

        assertEquals("missing.png", resolver.resolve("missing.png", "Balgaire", "BGR-2"));
    }
}

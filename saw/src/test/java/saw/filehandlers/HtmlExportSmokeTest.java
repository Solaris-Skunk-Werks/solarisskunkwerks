package saw.filehandlers;

import components.CombatVehicle;
import filehandlers.CVReader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HtmlExportSmokeTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void exportsMusketeerHtmlFromDefaultTemplate() throws Exception {
        CombatVehicle vehicle = new CVReader().ReadUnit(resourceFile("vehicles/Musketeer Hover Tank.saw").getAbsolutePath());
        File html = temporaryFolder.newFile("musketeer.html");

        new HTMLWriter(vehicle).WriteHTML("Data/Templates/Vee_HTML.html", html.getAbsolutePath());

        String export = read(html);
        assertTrue(export.contains("Musketeer Hover Tank"));
        assertTrue(export.contains("Technology Base:"));
        assertTrue(export.contains("Rotary AC/5"));
        assertFalse(export.contains("<+-SSW_"));
    }

    private String read(File file) throws Exception {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private File resourceFile(String path) throws Exception {
        File file = Paths.get("sswlib", "src", "test", "resources", path).toFile();
        if (file.isFile()) {
            return file;
        }

        file = Paths.get("..", "sswlib", "src", "test", "resources", path).toFile();
        if (file.isFile()) {
            return file;
        }

        throw new IllegalArgumentException("Missing test resource: " + path);
    }
}

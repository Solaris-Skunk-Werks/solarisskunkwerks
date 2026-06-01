package ssw.filehandlers;

import components.Mech;
import filehandlers.MechReader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HtmlExportSmokeTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void exportsBalgaireHtmlFromDefaultTemplate() throws Exception {
        Mech mech = new MechReader().ReadMech(resourceFile("mechs/Balgaire.ssw").getAbsolutePath());
        File html = temporaryFolder.newFile("balgaire.html");

        new HTMLWriter(mech).WriteHTML("Data/Templates/Mech_HTML.html", html.getAbsolutePath());

        String export = read(html);
        assertTrue(export.contains("Balgaire"));
        assertTrue(export.contains("Technology Base:"));
        assertTrue(export.contains("Gauss Rifle"));
        assertFalse(export.contains("<+-SSW_"));
    }

    private String read(File file) throws Exception {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private File resourceFile(String path) throws Exception {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Missing test resource: " + path);
        }
        return new File(resource.toURI());
    }
}

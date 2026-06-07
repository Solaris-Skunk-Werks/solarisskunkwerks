package filehandlers;

import components.CombatVehicle;
import components.Mech;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertTrue;

public class TextExportSmokeTest {

    @Test
    public void exportsBalgaireTextSummary() throws Exception {
        Mech mech = new MechReader().ReadMech(resourceFile("mechs/Balgaire.ssw").getAbsolutePath());

        String export = new TXTWriter(mech).GetTextExport();

        assertTrue(export.contains("Balgaire"));
        assertTrue(export.contains("Mass: 90 tons"));
        assertTrue(export.contains("Gauss Rifle"));
        assertTrue(export.contains("Large Pulse Laser"));
    }

    @Test
    public void exportsMusketeerTextSummary() throws Exception {
        CombatVehicle vehicle = new CVReader().ReadUnit(resourceFile("vehicles/Musketeer Hover Tank.saw").getAbsolutePath());

        String export = new CVTXTWriter(vehicle).GetTextExport();

        assertTrue(export.contains("Musketeer Hover Tank"));
        assertTrue(export.contains("Mass: 50 tons"));
        assertTrue(export.contains("Rotary AC/5"));
        assertTrue(export.contains("Streak SRM-2"));
    }

    private File resourceFile(String path) throws Exception {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Missing test resource: " + path);
        }
        return new File(resource.toURI());
    }
}

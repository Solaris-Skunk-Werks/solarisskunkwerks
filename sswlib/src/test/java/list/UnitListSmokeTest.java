package list;

import common.CommonTools;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UnitListSmokeTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void scansRealMechDirectoryAndReloadsGeneratedIndex() throws Exception {
        File unitDirectory = temporaryFolder.newFolder("units");
        copyResource("mechs/Balgaire.ssw", new File(unitDirectory, "Balgaire.ssw"));
        copyResource("mechs/Red Hunter.ssw", new File(unitDirectory, "Red Hunter.ssw"));

        UnitList scanned = new UnitList(unitDirectory.getAbsolutePath(), false);

        assertTrue(new File(unitDirectory, "index.ssi").isFile());
        assertTrue(scanned.Size() > 2);
        assertRedHunterPrimary(scanned);

        UnitList indexed = new UnitList(unitDirectory.getAbsolutePath(), true);

        assertEquals(scanned.Size(), indexed.Size());
        assertRedHunterPrimary(indexed);
    }

    private void assertRedHunterPrimary(UnitList unitList) {
        UnitListData primary = find(unitList, "Red Hunter", "Primary");

        assertEquals(CommonTools.BattleMech, primary.getUnitType());
        assertEquals(55, primary.getTonnage());
        assertEquals(3053, primary.getYear());
        assertEquals(1988, primary.getBV());
        assertEquals("Clan", primary.getTech());
        assertEquals("Biped", primary.getMotive());
        assertTrue(primary.isOmni());
        assertTrue(primary.getBattleForceStats().getAbilities().contains("OMNI"));
    }

    private UnitListData find(UnitList unitList, String name, String configuration) {
        for (UnitListData unit : unitList.getList()) {
            if (name.equals(unit.getName()) && configuration.equals(unit.getConfig())) {
                return unit;
            }
        }
        throw new AssertionError("Missing unit configuration: " + name + " " + configuration);
    }

    private void copyResource(String resourcePath, File destination) throws Exception {
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Missing test resource: " + resourcePath);
        }
        Files.copy(new File(resource.toURI()).toPath(), destination.toPath());
    }
}

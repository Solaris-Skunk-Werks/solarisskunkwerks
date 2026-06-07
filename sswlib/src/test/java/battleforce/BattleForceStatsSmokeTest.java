package battleforce;

import filehandlers.CVReader;
import list.UnitListData;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BattleForceStatsSmokeTest {

    @Test
    public void readsMusketeerBattleForceStatsFromVehicleFixture() throws Exception {
        File file = resourceFile("vehicles/Musketeer Hover Tank.saw");

        UnitListData summary = new CVReader().ReadUnitData(file.getAbsolutePath(), file.getParent());
        BattleForceStats stats = summary.getBattleForceStats();

        assertEquals("Musketeer Hover Tank", stats.getElement());
        assertEquals(11, stats.getBasePV());
        assertEquals(2, stats.getWeight());
        assertEquals("7h", stats.getMovement());
        assertEquals(3, stats.getShort());
        assertEquals(3, stats.getMedium());
        assertEquals(0, stats.getLong());
        assertEquals(0, stats.getExtreme());
        assertEquals(0, stats.getOverheat());
        assertEquals(4, stats.getArmor());
        assertEquals(3, stats.getInternal());
        assertTrue(stats.getAbilities().contains("EE"));
        assertTrue(stats.getAbilities().contains("TUR(3/3/0)"));
    }

    private File resourceFile(String path) throws Exception {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Missing test resource: " + path);
        }
        return new File(resource.toURI());
    }
}

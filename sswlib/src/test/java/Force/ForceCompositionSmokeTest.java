package Force;

import filehandlers.CVReader;
import list.UnitListData;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ForceCompositionSmokeTest {

    @Test
    public void addsRealUnitSummariesAndRefreshesForceTotals() throws Exception {
        UnitListData redHunter = new UnitListData(
                resourceFile("mechs/Red Hunter.ssw").getAbsolutePath(),
                resourceFile("mechs").getAbsolutePath());
        UnitListData musketeer = readVehicleSummary("vehicles/Musketeer Hover Tank.saw");

        Force force = new Force();
        force.ForceName = "Clan Fox Smoke Force";
        Unit firstRedHunter = new Unit(redHunter.Configurations.get(0));
        firstRedHunter.setGroup("Star Fox");
        Unit secondRedHunter = new Unit(redHunter.Configurations.get(0));
        secondRedHunter.setGroup("Star Fox");
        Unit hoverTank = new Unit(musketeer);
        hoverTank.setGroup("Armor");

        force.AddUnit(firstRedHunter);
        force.AddUnit(secondRedHunter);
        force.AddUnit(hoverTank);

        assertEquals(3, force.getUnits().size());
        assertEquals(3, force.TotalUnits);
        assertEquals(160.0f, force.TotalTonnage, 0.01f);
        assertEquals(5096.0f, force.TotalBaseBV, 0.01f);
        assertEquals(51.0f, force.TotalBasePV, 0.01f);
        assertTrue(force.Groups.size() >= 2);
    }

    private UnitListData readVehicleSummary(String resourcePath) throws Exception {
        File file = resourceFile(resourcePath);
        return new CVReader().ReadUnitData(file.getAbsolutePath(), file.getParent());
    }

    private File resourceFile(String path) throws Exception {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Missing test resource: " + path);
        }
        return new File(resource.toURI());
    }
}

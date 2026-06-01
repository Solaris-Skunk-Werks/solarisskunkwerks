package Force;

import IO.DataReader;
import IO.ForceWriter;
import filehandlers.CVReader;
import org.junit.Rule;
import list.UnitListData;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ForceCompositionSmokeTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void loadsSavedSswForceRosterWithEmbeddedMechs() throws Exception {
        Force force = loadForce("forces/ClanFox 1st Fox Dragoons roster.force");

        assertEquals(15, force.getUnits().size());
        assertEquals(15, force.TotalUnits);
        assertEquals(1, force.Groups.size());
        assertEquals(1305.0f, force.TotalTonnage, 0.01f);
        assertEquals(43043.0f, force.TotalBaseBV, 0.01f);
        assertTrue(force.TotalAdjustedBV > force.TotalBaseBV);

        Unit commander = force.getUnits().get(0);
        assertEquals("Rabid Fox", commander.Name);
        assertEquals("Primary", commander.Configuration);
        assertEquals(2, commander.getGunnery());
        assertEquals(3, commander.getPiloting());
        assertTrue(commander.IsOmni());
        assertEquals("Rabid Fox Primary", commander.getBFStats().getElement().replace("  ", " "));
    }

    @Test
    public void savedSswForceRosterRoundTripsThroughWriter() throws Exception {
        Force original = loadForce("forces/ClanFox 1st Fox Dragoons roster.force");
        File output = temporaryFolder.newFile("roundtrip.force");

        new ForceWriter().SerializeForce(original, output.getAbsolutePath());
        Force reloaded = new Force(new DataReader().ReadNode(output.getAbsolutePath(), "force"));

        assertEquals(original.getUnits().size(), reloaded.getUnits().size());
        assertEquals(original.TotalUnits, reloaded.TotalUnits);
        assertEquals(original.Groups.size(), reloaded.Groups.size());
        assertEquals(original.TotalTonnage, reloaded.TotalTonnage, 0.01f);
        assertEquals(original.TotalBaseBV, reloaded.TotalBaseBV, 0.01f);
        assertEquals(original.getUnits().get(0).TypeModel, reloaded.getUnits().get(0).TypeModel);
        assertEquals(original.getUnits().get(0).getGunnery(), reloaded.getUnits().get(0).getGunnery());
        assertEquals(original.getUnits().get(0).getPiloting(), reloaded.getUnits().get(0).getPiloting());
    }

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

    private Force loadForce(String resourcePath) throws Exception {
        return new Force(new DataReader().ReadNode(resourceFile(resourcePath).getAbsolutePath(), "force"));
    }

    private File resourceFile(String path) throws Exception {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Missing test resource: " + path);
        }
        return new File(resource.toURI());
    }
}

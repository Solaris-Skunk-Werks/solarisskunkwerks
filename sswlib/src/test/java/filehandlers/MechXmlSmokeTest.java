package filehandlers;

import components.AvailableCode;
import components.Mech;
import list.UnitListData;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MechXmlSmokeTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void writesAndReadsBasicMechXmlSummaryData() throws Exception {
        Mech source = new Mech();
        source.SetName("Synthetic");
        source.SetModel("SMK-17");
        source.SetTechBase(AvailableCode.TECH_INNER_SPHERE);
        source.SetRulesLevel(AvailableCode.RULES_TOURNAMENT);
        source.SetYear(3050, true);
        source.SetTonnage(20);
        source.SetWalkMP(5);

        File file = temporaryFolder.newFile("synthetic.ssw");

        new MechWriter(source).WriteXML(file.getAbsolutePath());
        UnitListData loaded = new MechReader().ReadMechData(file.getAbsolutePath(), temporaryFolder.getRoot().getAbsolutePath());

        assertEquals(source.GetName(), loaded.getName());
        assertEquals(source.GetModel(), loaded.getModel());
        assertEquals(source.GetTonnage(), loaded.getTonnage());
        assertEquals(source.GetBaseYear(), loaded.getYear());
        assertTrue(loaded.getCost() > 0);
    }

    @Test
    public void readsBalgaireFixture() throws Exception {
        File file = resourceFile("mechs/Balgaire.ssw");

        Mech loaded = new MechReader().ReadMech(file.getAbsolutePath());
        UnitListData summary = new MechReader().ReadMechData(file.getAbsolutePath(), file.getParent());

        assertEquals("Balgaire", loaded.GetName());
        assertEquals("", loaded.GetModel());
        assertEquals(90, loaded.GetTonnage());
        assertEquals(3060, loaded.GetBaseYear());
        assertEquals(AvailableCode.RULES_TOURNAMENT, loaded.GetBaseRulesLevel());
        assertEquals(AvailableCode.TECH_CLAN, loaded.GetBaseTechbase());
        assertEquals(3, loaded.GetWalkingMP());
        assertTrue(loaded.IsOmnimech());
        assertTrue(loaded.IsQuad());
        assertEquals(10, loaded.GetLoadouts().size());

        assertEquals("Balgaire", summary.getName());
        assertEquals(90, summary.getTonnage());
        assertEquals(3060, summary.getYear());
        assertTrue(summary.isOmni());
        assertFalse(summary.Configurations.isEmpty());
    }

    private File resourceFile(String path) throws Exception {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Missing test resource: " + path);
        }
        return new File(resource.toURI());
    }
}

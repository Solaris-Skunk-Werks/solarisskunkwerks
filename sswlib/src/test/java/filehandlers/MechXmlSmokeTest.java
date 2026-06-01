package filehandlers;

import components.AvailableCode;
import components.Mech;
import list.UnitListData;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;
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
}

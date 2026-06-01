package filehandlers;

import components.AvailableCode;
import components.CombatVehicle;
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

    @Test
    public void writesAndReadsBasicCombatVehicleXml() throws Exception {
        CombatVehicle source = new CombatVehicle();
        source.setName("Synthetic Carrier");
        source.setModel("CV-17");
        source.SetTechBase(AvailableCode.TECH_INNER_SPHERE);
        source.SetRulesLevel(AvailableCode.RULES_TOURNAMENT);
        source.SetYear(3050, true);
        source.setTonnage(35);
        source.setCruiseMP(4);

        File file = temporaryFolder.newFile("synthetic-vehicle.ssw");

        new CVWriter(source).WriteXML(file.getAbsolutePath());
        CombatVehicle loaded = new CVReader().ReadUnit(file.getAbsolutePath());

        assertEquals(source.GetName(), loaded.GetName());
        assertEquals(source.GetModel(), loaded.GetModel());
        assertEquals(source.GetTonnage(), loaded.GetTonnage());
        assertEquals(source.GetBaseYear(), loaded.GetBaseYear());
        assertEquals(source.GetBaseRulesLevel(), loaded.GetBaseRulesLevel());
        assertEquals(source.GetBaseTechbase(), loaded.GetBaseTechbase());
        assertEquals(source.getCruiseMP(), loaded.getCruiseMP());
    }

    @Test
    public void readsMusketeerHoverTankFixture() throws Exception {
        File file = resourceFile("vehicles/Musketeer Hover Tank.saw");

        CombatVehicle loaded = new CVReader().ReadUnit(file.getAbsolutePath());
        UnitListData summary = new CVReader().ReadUnitData(file.getAbsolutePath(), file.getParent());

        assertEquals("Musketeer Hover Tank", loaded.GetName());
        assertEquals("", loaded.GetModel());
        assertEquals(50, loaded.GetTonnage());
        assertEquals(3070, loaded.GetBaseYear());
        assertEquals(AvailableCode.RULES_EXPERIMENTAL, loaded.GetBaseRulesLevel());
        assertEquals(AvailableCode.TECH_INNER_SPHERE, loaded.GetBaseTechbase());
        assertEquals(7, loaded.getCruiseMP());
        assertEquals(7, loaded.GetLoadout().GetNonCore().size());
        assertTrue(loaded.GetChatInfo().contains("1 RAC5"));
        assertTrue(loaded.GetChatInfo().contains("2 SSRM2"));

        assertEquals("Musketeer Hover Tank", summary.getName());
        assertEquals(50, summary.getTonnage());
        assertEquals(3070, summary.getYear());
        assertEquals(1120, summary.getBV());
        assertEquals("Hovercraft", summary.getMotive());
        assertEquals("Musketeer Hover Tank 50t, 7/11, ICE, 8.5T/62% Armor; 0 SHS; 2 SSRM2, 1 RAC5", summary.getInfo());
    }

    private File resourceFile(String path) throws Exception {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Missing test resource: " + path);
        }
        return new File(resource.toURI());
    }
}

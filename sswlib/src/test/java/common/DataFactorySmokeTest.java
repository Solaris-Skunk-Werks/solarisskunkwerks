package common;

import components.AvailableCode;
import components.Mech;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DataFactorySmokeTest {

    @Test
    public void loadsJsonEquipmentData() throws Exception {
        DataFactory data = new DataFactory();

        assertFalse("quirks should be loaded from JSON", data.GetQuirks().isEmpty());
    }

    @Test
    public void buildsMechEquipmentFactoryFromJsonData() throws Exception {
        Mech mech = new Mech();
        mech.SetName("Smoke");
        mech.SetModel("SSW-17");
        mech.SetTechBase(AvailableCode.TECH_INNER_SPHERE);
        mech.SetRulesLevel(AvailableCode.RULES_TOURNAMENT);
        mech.SetYear(3050, true);

        DataFactory data = new DataFactory(mech);
        EquipmentFactory equipment = data.GetEquipment();

        assertNotNull(equipment);
        assertNotNull(equipment.GetRangedWeaponByName("(IS) Medium Laser", mech));
        assertNotNull(equipment.GetRangedWeaponByName("(IS) LRM-5", mech));
        assertNotNull(equipment.GetEquipmentByName("Searchlight", mech));
    }

    @Test
    public void defaultMechRemainsUsableUnderJava17() throws Exception {
        Mech mech = new Mech();
        mech.SetTonnage(55);
        mech.SetWalkMP(5);

        assertEquals(55, mech.GetTonnage());
        assertEquals(5, mech.GetWalkingMP());
        assertTrue(mech.GetTotalCost() > 0);
    }
}

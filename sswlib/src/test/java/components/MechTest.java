package components;

import filehandlers.MechReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class MechTest {
    private Mech m;

    private void loadMechFile(String name) {
        try {
            URL url = getClass().getResource("/Mech/" + name);
            String path = java.net.URLDecoder.decode(url.toString(), StandardCharsets.UTF_8.name());
            MechReader XMLr = new MechReader();
            m = XMLr.ReadMech(path);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Before
    public void setUpMech() {
        m = null;
    }

    @After
    public void tearDownMech() {
        m = null;
    }

    @Test
    public void getCurrentBVArchangelCANGO() {
        loadMechFile("Archangel C-ANG-O.ssw");
        m.SetCurLoadout("Invictus (Prime)");
        assertEquals("Archangel C-ANG-O BV should be 2237", 2237, m.GetCurrentBV());
    }

    @Test
    public void getCurrentBVWerewolfWERLF005() {
        loadMechFile("Werewolf WER-LF-005.ssw");
        assertEquals("Werewolf WER-LF-005 BV should be 1082", 1082, m.GetCurrentBV());
    }

    @Test
    public void getCurrentBVTurkinaU() {
        loadMechFile("Turkina.ssw");
        m.SetCurLoadout("U");
        assertEquals("Turkina U BV should be 2520", 2520, m.GetCurrentBV());
    }

    @Test
    public void getCurrentBVTurkinaX() {
        loadMechFile("Turkina.ssw");
        m.SetCurLoadout("X");
        assertEquals("Turkina X BV should be 3056", 3056, m.GetCurrentBV());
    }

    @Test
    public void getCurrentBVArcherARC9W() {
        loadMechFile("Archer ARC-9W.ssw");
        assertEquals("Archer ARC-9W BV should be 1774", 1774, m.GetCurrentBV());
    }

    @Test
    public void getCurrentBVRaptorIIRPT2X() {
        loadMechFile("Raptor II RPT-2X.ssw");
        assertEquals("Raptor II RPT-2X BV should be 896", 896, m.GetCurrentBV());
    }
}
package filehandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonReader {
    private Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();

    public ArrayList ReadAllAmmo(Path dir) throws Exception {
        List<Path> files = walkPath(dir);
        ArrayList ammo = new ArrayList();
        for (Path f: files) {
            ammo.add(gson.fromJson(new FileReader(f.toString()), Ammunition.class));
        }
        return ammo;
    }

    public ArrayList ReadAllRangedWeapons(Path dir) throws Exception {
        List<Path> files = walkPath(dir);
        ArrayList weapons = new ArrayList();
        for (Path f: files) {
            weapons.add(gson.fromJson(new FileReader(f.toString()), RangedWeapon.class));
        }
        return weapons;
    }

    public ArrayList ReadAllPhysicalWeapons(Path dir) throws Exception{
        List<Path> files = walkPath(dir);
        ArrayList weapons = new ArrayList();
        for (Path f: files) {
            weapons.add(gson.fromJson(new FileReader(f.toString()), PhysicalWeapon.class));
        }
        return weapons;
    }

    public ArrayList ReadAllEquipment(Path dir) throws Exception {
        List<Path> files = walkPath(dir);
        ArrayList equipment = new ArrayList();
        for (Path f: files) {
            equipment.add(gson.fromJson(new FileReader(f.toString()), Equipment.class));
        }
        return equipment;
    }

    public ArrayList ReadAllQuirks(Path dir) throws Exception {
        List<Path> files = walkPath(dir);
        ArrayList quirks = new ArrayList();
        for (Path f: files) {
            quirks.add(gson.fromJson(new FileReader(f.toString()), Quirk.class));
        }
        return quirks;
    }

    private List<Path> walkPath(Path dir) throws Exception {
        return Files.walk(dir)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
    }
}

package filehandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;
import components.*;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonReader {
    private Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Ammunition.class, new AmmunitionInstanceCreator())
            .registerTypeAdapter(RangedWeapon.class, new RangedWeaponInstanceCreator())
            .registerTypeAdapter(PhysicalWeapon.class, new PhysicalWeaponInstanceCreator())
            .registerTypeAdapter(Equipment.class, new EquipmentInstanceCreator())
            .registerTypeAdapter(Quirk.class, new QuirkInstanceCreator())
            .create();

    public ArrayList ReadAllAmmo(Path f) throws Exception {
        Type collectionType = new TypeToken<ArrayList<Ammunition>>(){}.getType();
        ArrayList<Ammunition> ammo = gson.fromJson(new FileReader(f.toString()), collectionType);
        return ammo;
    }

    public ArrayList ReadAllRangedWeapons(Path f) throws Exception {
        Type collectionType = new TypeToken<ArrayList<RangedWeapon>>(){}.getType();
        ArrayList<RangedWeapon> weapons = gson.fromJson(new FileReader(f.toString()), collectionType);
        return weapons;
    }

    public ArrayList ReadAllPhysicalWeapons(Path f) throws Exception {
        Type collectionType = new TypeToken<ArrayList<PhysicalWeapon>>(){}.getType();
        ArrayList<PhysicalWeapon> weapons = gson.fromJson(new FileReader(f.toString()), collectionType);
        return weapons;
    }

    public ArrayList ReadAllEquipment(Path f) throws Exception {
        Type collectionType = new TypeToken<ArrayList<Equipment>>(){}.getType();
        ArrayList<Equipment> equipment = gson.fromJson(new FileReader(f.toString()), collectionType);
        return equipment;
    }

    public ArrayList ReadAllQuirks(Path f) throws Exception {
        Type collectionType = new TypeToken<ArrayList<Quirk>>(){}.getType();
        ArrayList<Quirk> quirks = gson.fromJson(new FileReader(f.toString()), collectionType);
        return quirks;
    }

    private List<Path> walkPath(Path dir) throws Exception {
        return Files.walk(dir)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
    }

    private class AmmunitionInstanceCreator implements InstanceCreator<Ammunition> {
        public Ammunition createInstance(Type type) {
            return new Ammunition();
        }
    }

    private class RangedWeaponInstanceCreator implements InstanceCreator<RangedWeapon> {
        public RangedWeapon createInstance(Type type) {
            return new RangedWeapon();
        }
    }

    private class PhysicalWeaponInstanceCreator implements InstanceCreator<PhysicalWeapon> {
        public PhysicalWeapon createInstance(Type type) {
            return new PhysicalWeapon();
        }
    }

    private class EquipmentInstanceCreator implements InstanceCreator<Equipment> {
        public Equipment createInstance(Type type) {
            return new Equipment();
        }
    }

    private class QuirkInstanceCreator implements InstanceCreator<Quirk> {
        public Quirk createInstance(Type type) {
            return new Quirk();
        }
    }
}

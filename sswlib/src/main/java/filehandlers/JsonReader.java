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
import java.util.Map;
import java.util.stream.Collectors;

public class JsonReader {
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Ammunition.class, new AmmunitionInstanceCreator())
            .registerTypeAdapter(RangedWeapon.class, new RangedWeaponInstanceCreator())
            .registerTypeAdapter(PhysicalWeapon.class, new PhysicalWeaponInstanceCreator())
            .registerTypeAdapter(Equipment.class, new EquipmentInstanceCreator())
            .registerTypeAdapter(Quirk.class, new QuirkInstanceCreator())
            .create();

    public ArrayList ReadAllAmmo(Path f) throws Exception {
        Type collectionType = new TypeToken<Map<String, Ammunition>>(){}.getType();
        Map<String, Ammunition> map = gson.fromJson(new FileReader(f.toString()), collectionType);
        return new ArrayList<>(map.values());

    }

    public ArrayList ReadAllRangedWeapons(Path f) throws Exception {
        Type collectionType = new TypeToken<Map<String, RangedWeapon>>(){}.getType();
        Map<String, RangedWeapon> map = gson.fromJson(new FileReader(f.toString()), collectionType);
        return new ArrayList<>(map.values());
    }

    public ArrayList ReadAllPhysicalWeapons(Path f) throws Exception {
        Type collectionType = new TypeToken<Map<String, PhysicalWeapon>>(){}.getType();
        Map<String, PhysicalWeapon> map = gson.fromJson(new FileReader(f.toString()), collectionType);
        return new ArrayList<>(map.values());
    }

    public ArrayList ReadAllEquipment(Path f) throws Exception {
        Type collectionType = new TypeToken<Map<String, Equipment>>(){}.getType();
        Map<String, Equipment> map = gson.fromJson(new FileReader(f.toString()), collectionType);
        return new ArrayList<>(map.values());
    }

    public ArrayList ReadAllQuirks(Path f) throws Exception {
        Type collectionType = new TypeToken<Map<String, Quirk>>(){}.getType();
        Map<String, Quirk> map = gson.fromJson(new FileReader(f.toString()), collectionType);
        return new ArrayList<>(map.values());
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

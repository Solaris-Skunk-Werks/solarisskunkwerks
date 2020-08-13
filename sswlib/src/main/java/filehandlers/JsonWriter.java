package filehandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;

import java.io.FileWriter;
import java.nio.file.Path;

public class JsonWriter {
    private Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    public void Write(RangedWeapon weapon, Path outDir) throws Exception {
        String filename = weapon.LookupName().replace("/", "_") + ".json";
        FileWriter fw = new FileWriter(outDir.resolve(filename).toString());
        gson.toJson(weapon, fw);
        fw.flush();
        fw.close();
    }

    public void Write(PhysicalWeapon weapon, Path outDir) throws Exception {
        String filename = weapon.LookupName().replace("/", "_") + ".json";
        FileWriter fw = new FileWriter(outDir.resolve(filename).toString());
        gson.toJson(weapon, fw);
        fw.flush();
        fw.close();
    }

    public void Write(Equipment equipment, Path outDir) throws Exception {

    }

    public void Write(Ammunition ammo, Path outDir) throws Exception {

    }

    public void Write(Quirk quirk, Path outDir) throws Exception {

    }
}

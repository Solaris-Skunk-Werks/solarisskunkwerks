package filehandlers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import components.*;

import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonWriter {
    private Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    // Store data as a LinkedHashMap to preserve sorting and make the JSON items collapsible/easier to navigate
    private Type equipMapToken = new TypeToken<Map<String, abPlaceable>>(){}.getType();
    private Type quirkMapToken = new TypeToken<Map<String, Quirk>>(){}.getType();

    public void Write(abPlaceable equipment, Path outDir) throws Exception {
        String filename = equipment.LookupName().replace("/", "_") + ".json";
        FileWriter fw = new FileWriter(outDir.resolve(filename).toString());
        Map<String, abPlaceable> map = new LinkedHashMap<>();
        map.put(equipment.LookupName(), equipment);
        gson.toJson(map, equipMapToken, fw);
        fw.flush();
        fw.close();
    }

    public void WriteAllEquipment(ArrayList<abPlaceable> equipment, Path outfile) throws Exception {
        FileWriter fw = new FileWriter(outfile.toString());
        Map<String, abPlaceable> map = new LinkedHashMap<>();
        for (abPlaceable w: equipment) {
            map.put(w.LookupName(), w);
        }
        gson.toJson(map, equipMapToken, fw);
        fw.flush();
        fw.close();
    }

    public void Write(Quirk quirk, Path outDir) throws Exception {
        String filename = quirk.getName().replace("/", "_") + ".json";
        FileWriter fw = new FileWriter(outDir.resolve(filename).toString());
        Map<String, Quirk> map = new LinkedHashMap<>();
        map.put(quirk.getName(), quirk);
        gson.toJson(map, quirkMapToken, fw);
        fw.flush();
        fw.close();
    }

    public void WriteAllQuirks(ArrayList<Quirk> quirks, Path outfile) throws Exception {
        FileWriter fw = new FileWriter(outfile.toString());
        Map<String, Quirk> map = new LinkedHashMap<>();
        for (Quirk q: quirks) {
            map.put(q.getName(), q);
        }
        gson.toJson(map, quirkMapToken, fw);
        fw.flush();
        fw.close();
    }
}

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
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    // Store data as a LinkedHashMap to preserve sorting and make the JSON items collapsible/easier to navigate
    private Type equipMapToken = new TypeToken<Map<String, abPlaceable>>(){}.getType();
    private Type quirkMapToken = new TypeToken<Map<String, Quirk>>(){}.getType();

    public void WriteEquipment(ArrayList<abPlaceable> equipment, Path outfile) throws Exception {
        FileWriter fw = new FileWriter(outfile.toString());
        Map<String, abPlaceable> map = new LinkedHashMap<>();
        for (abPlaceable w: equipment) {
            map.put(w.LookupName(), w);
        }
        gson.toJson(map, equipMapToken, fw);
        fw.flush();
        fw.close();
    }

    public void WriteQuirks(ArrayList<Quirk> quirks, Path outfile) throws Exception {
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

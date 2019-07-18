/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Print;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FontLoader {
    private static String[] names = { "Eurosti.ttf" };
    private static Map<String, Font> cache = new ConcurrentHashMap<String, Font>(names.length);

    static {
        for (String name : names) {
            cache.put(name, getFont(name));
        }
    }

    public static Font getFont(String name) {
        Font font = null;
        if (cache != null) {
            if ((font = cache.get(name)) != null) {
                return font;
            }
        }
        String fName = "./Data/Fonts/" + name;
        try {
            File fFile = new File(fName);
            InputStream is = new FileInputStream(fFile);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            is.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(fName + " not loaded.  Using serif font.");
            font = new Font("Arial", Font.PLAIN, 12);
        }
        return font;
    }
}

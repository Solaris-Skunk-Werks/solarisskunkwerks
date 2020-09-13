package IO;

import java.io.File;

public class Utils {
    public static String convertFilePathSeparator(String path) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return path.replace("/", File.separator);
        } else {
            return path.replace("\\", File.separator);
        }
    }
}

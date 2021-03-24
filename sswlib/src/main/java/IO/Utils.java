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

    public static Integer countFilesInDirectory(File directory, String extension) {
        int count = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(extension)) {
                count++;
            }
            if (file.isDirectory()) {
                count += countFilesInDirectory(file, extension);
            }
        }
        return count;
    }
}

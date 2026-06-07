package filehandlers;

import common.Constants;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public class ImagePathResolver {
    private final Preferences sswPrefs;

    public ImagePathResolver() {
        this(Preferences.userRoot().node(Constants.SSWPrefs));
    }

    ImagePathResolver(Preferences sswPrefs) {
        this.sswPrefs = sswPrefs;
    }

    public String resolve(String imagePath, String unitName, String unitModel) {
        String storedPath = imagePath == null ? "" : imagePath.trim();
        if (storedPath.isEmpty()) {
            return "";
        }

        String resolvedPath = canonicalPathIfPresent(storedPath);
        if (!resolvedPath.isEmpty()) {
            return resolvedPath;
        }

        resolvedPath = canonicalPathIfPresent(new File(sswPrefs.get("DefaultImagePath", ""), storedPath).getPath());
        if (!resolvedPath.isEmpty()) {
            return resolvedPath;
        }

        File matchedImage = findMatchingImage(unitName, unitModel, new File(sswPrefs.get("DefaultImagePath", "")));
        if (matchedImage != null) {
            return canonicalPathIfPresent(matchedImage.getPath());
        }

        return storedPath;
    }

    public String resolve(String imagePath) {
        return resolve(imagePath, "", "");
    }

    private String canonicalPathIfPresent(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return "";
        }

        File imageFile = new File(imagePath);
        if (!imageFile.isFile()) {
            return "";
        }

        try {
            return imageFile.getCanonicalPath();
        } catch (IOException ex) {
            return imageFile.getAbsolutePath();
        }
    }

    private File findMatchingImage(String unitName, String unitModel, File directory) {
        ArrayList<File> imageFiles = findImageFiles(directory);
        for (String candidate : imageNameCandidates(unitName, unitModel)) {
            Pattern pattern = Pattern.compile(".*" + imageNameRegex(candidate) + ".*", Pattern.CASE_INSENSITIVE);
            for (File imageFile : imageFiles) {
                if (pattern.matcher(stripExtension(imageFile.getName())).matches()) {
                    return imageFile;
                }
            }
        }
        return null;
    }

    private ArrayList<String> imageNameCandidates(String unitName, String unitModel) {
        ArrayList<String> candidates = new ArrayList<>();
        addCandidate(candidates, unitName + " " + unitModel);
        addCandidate(candidates, unitModel + " " + unitName);
        addCandidate(candidates, unitName);
        return candidates;
    }

    private void addCandidate(ArrayList<String> candidates, String candidate) {
        String cleanCandidate = candidate == null ? "" : candidate.trim();
        if (!cleanCandidate.isEmpty() && !candidates.contains(cleanCandidate)) {
            candidates.add(cleanCandidate);
        }
    }

    private String imageNameRegex(String candidate) {
        String normalized = candidate.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
        if (normalized.isEmpty()) {
            return "";
        }
        return Pattern.quote(normalized).replace(" ", "\\E[^a-z0-9]*\\Q");
    }

    private String stripExtension(String filename) {
        int extensionIndex = filename.lastIndexOf(".");
        return extensionIndex > 0 ? filename.substring(0, extensionIndex) : filename;
    }

    private ArrayList<File> findImageFiles(File directory) {
        ArrayList<File> files = new ArrayList<>();
        File[] directoryFiles = directory.listFiles();
        if (directoryFiles == null) {
            return files;
        }

        for (File file : directoryFiles) {
            if (file.isDirectory()) {
                files.addAll(findImageFiles(file));
            } else if (isImageFile(file)) {
                files.add(file);
            }
        }
        return files;
    }

    private boolean isImageFile(File file) {
        String filename = file.getName().toLowerCase(Locale.ROOT);
        return filename.endsWith(".gif")
                || filename.endsWith(".jpg")
                || filename.endsWith(".jpeg")
                || filename.endsWith(".png")
                || filename.endsWith(".tif")
                || filename.endsWith(".tiff");
    }
}

package common;

import java.awt.*;
import java.io.File;
import javax.swing.*;

/* Utils.java is used by FileChooserDemo2.java. */
public class Utils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static GridBagConstraints gridBag(int x, int y) {
        return new GridBagConstraints(x, y, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, 1, new Insets(0, 0, 0, 0), 0, 0);
    }

    public static GridBagConstraints gridBag(int x, int y, Insets inset) {
        return new GridBagConstraints(x, y, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, 1, inset, 0, 0);
    }

    public static GridBagConstraints gridBag(int x, int y, int width, int height) {
        return new GridBagConstraints(x, y, width, height, 1.0, 1.0, GridBagConstraints.WEST, 1, new Insets(0, 0, 0, 0), 0, 0);
    }

    public static GridBagConstraints gridBag(int x, int y, int width, int anchor, Insets inset) {
        return new GridBagConstraints(x, y, width, 1, 1.0, 1.0, anchor, 1, inset, 0, 0);
    }

    public static JSeparator etchedSeperator() {
        JSeparator sep = new JSeparator();
        sep.setBorder(BorderFactory.createEtchedBorder());
        return sep;
    }
}

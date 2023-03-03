package common;

import java.awt.*;
import java.awt.event.ActionListener;
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

    /* Returns a separator with etched border */
    public static JSeparator etchedSeparator() {
        JSeparator sep = new JSeparator();
        sep.setBorder(BorderFactory.createEtchedBorder());
        return sep;
    }

    public static JSeparator vertSeparator() {
        JSeparator bar = new JSeparator();
        bar.setOrientation(SwingConstants.VERTICAL);
        return bar;
    }

    public static JButton imageButton(String toolTip, ActionListener listener, ImageIcon icon) {
        JButton button = new JButton();
        button.setIcon(icon);
        button.setToolTipText(toolTip);
        button.setFocusable(false);
        button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        button.addActionListener(listener);
        return button;
    }

    public static JLabel alignedLabel(String label, int alignment) {
        JLabel lbl = new JLabel(label);
        lbl.setHorizontalAlignment(alignment);
        return lbl;
    }

    public static JTextField summaryField(String label) {
        JTextField txt = new JTextField(label);
        txt.setEditable(false);
        txt.setHorizontalAlignment(SwingConstants.CENTER);
        return txt;
    }

    public static JMenuItem menuItem(String label, ActionListener listener) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(listener);
        return item;
    }

    public static JMenuItem menuItem(String label, ActionListener listener, KeyStroke keys) {
        JMenuItem item = menuItem(label, listener);
        item.setAccelerator(keys);
        return item;
    }
}

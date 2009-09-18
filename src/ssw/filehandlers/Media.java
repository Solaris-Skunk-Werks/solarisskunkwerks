/*
Copyright (c) 2008~2009, Justin R. Bengtson (poopshotgun@yahoo.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
        this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
        this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
    * Neither the name of Justin R. Bengtson nor the names of contributors may
        be used to endorse or promote products derived from this software
        without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ssw.filehandlers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import ssw.gui.ImageFilter;
import ssw.gui.ImagePreview;

public class Media {
    MediaTracker Tracker = new MediaTracker(new JLabel());
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    JFileChooser fileChooser = new JFileChooser();

    public Media() {
    }

    public File SelectFile(String defaultDirectory, String Extension, String commandName) {
        File tempFile = new File(defaultDirectory);
        fileChooser.addChoosableFileFilter(new ExtensionFilter(Extension));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(tempFile);
        int returnVal = fileChooser.showDialog(null, commandName);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    public File[] SelectFiles(String defaultDirectory, String Extension, String commandName) {
        File[] files = null;
        File tempFile = new File(defaultDirectory);

        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.addChoosableFileFilter(new ExtensionFilter(Extension));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(tempFile);
        int returnVal = fileChooser.showDialog(null, commandName);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            files = fileChooser.getSelectedFiles();
        }
        return files;
    }

    public File SelectImage(String defaultDirectory, String commandName) {
        File tempFile = new File(defaultDirectory);
        fileChooser.addChoosableFileFilter(new ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(tempFile);
        fileChooser.setAccessory(new ImagePreview(fileChooser));
        int returnVal = fileChooser.showDialog(null, commandName);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }
    public Image GetImage(String filename) {
        Image retval = toolkit.getImage( filename );
        Tracker.addImage( retval, 0 );
        try {
            Tracker.waitForID( 0 );
        } catch (InterruptedException ie) {
            // do nothing
        }
        Tracker.removeImage(retval);
        return retval;
    }

    public void RemoveImage(Image image ) {
        Tracker.removeImage(image);
    }
    
    public void setLogo( javax.swing.JLabel lblLogo, File Logo ) {
        if ( Logo != null && ! Logo.getPath().isEmpty() ) {
            try {
               ImageIcon icon = new ImageIcon(Logo.getPath());

                if( icon == null ) { return; }

                // See if we need to scale
                int lblH = lblLogo.getHeight()-lblLogo.getIconTextGap();
                int lblW = lblLogo.getWidth()-lblLogo.getIconTextGap();

                int h = icon.getIconHeight();
                int w = icon.getIconWidth();
                if ( w > lblW || h > lblH ) {
                    if ( h > lblH ) {
                        icon = new ImageIcon(icon.getImage().
                            getScaledInstance(-1, lblH, Image.SCALE_SMOOTH));
                        w = icon.getIconWidth();
                    }
                    if ( w > lblW ) {
                        icon = new ImageIcon(icon.getImage().
                            getScaledInstance(lblW, -1, Image.SCALE_SMOOTH));
                    }
                }

                lblLogo.setIcon(icon);
            } catch ( Exception e ) {

            }
        }
    }

    public Dimension reScale( Dimension d, double resize ) {
        d.height = (int) (d.height * resize);
        d.width = (int) (d.width * resize);
        return d;
    }

    public Dimension reSize(Image image, double MaxWidth, double MaxHeight ) {
        Dimension imageSize = new Dimension(image.getWidth(null), image.getHeight(null) );

        if ( imageSize.width > MaxWidth || imageSize.height > MaxHeight ) {
            if ( imageSize.width > imageSize.height ) {
                imageSize = reScale(imageSize, (double)(MaxWidth / (double) imageSize.width));
                if ( imageSize.height > MaxHeight ) { imageSize = reScale(imageSize, (double)(MaxHeight / (double) imageSize.height)); }
            } else {
                imageSize = reScale(imageSize, (double)(MaxHeight / (double) imageSize.height));
                if ( imageSize.width > MaxWidth ) { imageSize = reScale(imageSize, (double)(MaxWidth / (double) imageSize.width)); }
            }
        }

        return imageSize;
    }

    public String GetDirectorySelection( Component Parent ) {
        return GetDirectorySelection( Parent, "", "Choose directory" );
    }

    public String GetDirectorySelection( Component Parent, String defaultPath ) {
        return GetDirectorySelection( Parent, defaultPath, "Choose directory" );
    }

    public String GetDirectorySelection( Component Parent, String defaultPath, String CommandName ) {
        String path = defaultPath;
        JFileChooser fc = new JFileChooser();

        //Add a custom file filter and disable the default
        //(Accept All) file filter.
        fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        fc.setAcceptAllFileFilterUsed(false);
        fc.setCurrentDirectory(new File(defaultPath));

        //Show it.
        int returnVal = fc.showDialog( Parent, CommandName);

        //Process the results.  If no file is chosen, the default is used.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = fc.getSelectedFile().getPath();
        }
        return path;
    }

    public static void Messager(String message) {
        javax.swing.JOptionPane.showMessageDialog(null, message);
    }

    public static void Messager(Component component, String message) {
        javax.swing.JOptionPane.showMessageDialog(component, message);
    }
    
    private class ExtensionFilter extends javax.swing.filechooser.FileFilter {
        String Extension = "";

        private ExtensionFilter(String Extension) {
            this.Extension = Extension.toLowerCase();
        }

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith("." + Extension);
        }

        @Override
        public String getDescription() {
            return "*." + Extension;
        }
    }


}

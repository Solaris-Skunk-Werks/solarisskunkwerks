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

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import ssw.gui.ImageFilter;
import ssw.gui.ImagePreview;
import ssw.gui.frmMain;

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

    public String GetDirectorySelection( frmMain Parent ) {
        return GetDirectorySelection( Parent, "" );
    }

    public String GetDirectorySelection(frmMain Parent, String defaultPath ) {
        String path = defaultPath;
        JFileChooser fc = new JFileChooser();

        //Add a custom file filter and disable the default
        //(Accept All) file filter.
        fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        fc.setAcceptAllFileFilterUsed(false);
        fc.setCurrentDirectory(new File(defaultPath));

        //Show it.
        int returnVal = fc.showDialog( Parent, "Choose directory");

        //Process the results.  If no file is chosen, the default is used.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = fc.getSelectedFile().getPath();
        }
        return path;
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

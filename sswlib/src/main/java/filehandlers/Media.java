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

package filehandlers;

import common.Constants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import common.ImageFilter;
import common.ImagePreview;
import java.awt.Point;
import java.net.URL;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

import IO.Utils;

public class Media {
    MediaTracker Tracker = new MediaTracker(new JLabel());
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    JFileChooser fileChooser = new JFileChooser();
    ArrayList<File> imageFiles = new ArrayList<File>();
    Integer trackerIndex = 0;

    public static final int OK = JOptionPane.OK_OPTION,
                            CANCEL = JOptionPane.CANCEL_OPTION;

    public Media() {
    }

    public File SelectFile(String defaultDirectory, String Extension, String commandName) {
        File tempFile = new File(defaultDirectory);
        String[] extensions = Extension.split(",");
        for (String extend : extensions) {
            fileChooser.addChoosableFileFilter(new ExtensionFilter(extend));
        }
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(tempFile);
        fileChooser.setSelectedFile(tempFile);
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
        if ( filename.isEmpty() ) return null;
        Image retval = toolkit.getImage( filename );
        trackerIndex++;
        Tracker.addImage( retval, trackerIndex);
        try {
            Tracker.waitForID( trackerIndex );

            //Check to see if the image loaded or not
            if (Tracker.isErrorID(trackerIndex))
                return null;
        } catch (InterruptedException ie) {
            return null;
        }

        return retval;
    }

    public Image LoadImage( byte[] data ) {
        Image retval = toolkit.createImage( data );
        Tracker.addImage(retval, 0);
        try {
            Tracker.waitForID( 0 );
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
        return retval;
    }

    public void RemoveImage(Image image ) {
        Tracker.removeImage(image);
    }

    public void blankLogo( javax.swing.JLabel lblLogo) {
        lblLogo.setIcon(null);
    }

    public void setLogo( javax.swing.JLabel lblLogo, String url ) {
        ImageIcon icon;
        if ( !url.isEmpty() ) {
            try {
                if ( url.startsWith("http") ) {
                    icon = new ImageIcon( new URL(url) );
                } else {
                    icon = new ImageIcon(url);
                }
                setLogo(lblLogo, icon);
            } catch ( Exception e ) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void setLogo( javax.swing.JLabel lblLogo, Image image ) {
        setLogo(lblLogo,  new ImageIcon(image));
    }

    public void setLogo( javax.swing.JLabel lblLogo, ImageIcon icon ) {
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
    }

    public void setLogo( javax.swing.JLabel lblLogo, File Logo ) {
        if ( Logo != null && ! Logo.getPath().isEmpty() ) {
            try {
               ImageIcon icon = new ImageIcon(Logo.getPath());
               setLogo( lblLogo, icon );
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
        if ( image == null ) return new Dimension(0,0);
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

    public Point offsetImageBottom( Dimension spaceDimensions, Dimension currentDimensions ) {
        Point offset = new Point(0, 0);
        offset.y = spaceDimensions.height - currentDimensions.height;
        return offset;
    }

    public Point offsetImageCenter( Dimension spaceDimensions, Dimension currentDimensions ) {
        Point offset = new Point(0, 0);
        offset.x = (spaceDimensions.width/2) - (currentDimensions.width/2);
        offset.y = (spaceDimensions.height/2) - (currentDimensions.height/2);
        return offset;
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
        } else {
            path = "";
        }
        return path;
    }

    public String FindMatchingImage( String Name, String Model, String DirectoryPath ) {
        Preferences Prefs = Preferences.userRoot().node( Constants.SSWPrefs );
        if ( DirectoryPath.isEmpty() ) DirectoryPath = Prefs.get("DefaultImagePath", "");
        if ( DirectoryPath.isEmpty() ) return "";

        //Create a list of the names to check first starting with the most accurate and working down
        Name = Name.replace("\"", "").trim();
        Model = Model.replace("\"", "").trim();
        ArrayList<String> PossibleNames = new ArrayList<String>();
        PossibleNames.add(Name + " " + Model);
        PossibleNames.add(Model + " " + Name);
        PossibleNames.add(Name);
        PossibleNames.add((Name+Model).replace(" ", ""));
        PossibleNames.add(Name.replace(" ", ""));
        PossibleNames.add(Model.replace(" ", ""));
        if ( Name.contains("(") ) {
            String First = "", Second = "";
            First = Name.split("\\(")[0].trim();
            Second = Name.split("\\(")[1].replace("(", "").replace(")", "");
            PossibleNames.add(Second + " (" + First + ") " + Model);
            PossibleNames.add(Model + " " + Second + " (" + First + ")");
            PossibleNames.add(First + " " + Model);
            PossibleNames.add(Second + " " + Model);
            PossibleNames.add(Second + " (" + First + ")");
            PossibleNames.add(First);
            PossibleNames.add(First.replace(" ", ""));
            PossibleNames.add(Second);
            PossibleNames.add(Second.replace(" ", ""));
        }

        if ( DirectoryPath.endsWith(".jpg") ||
             DirectoryPath.endsWith(".png") ||
             DirectoryPath.endsWith(".gif") ||
             DirectoryPath.endsWith(".ssw") ||
             DirectoryPath.endsWith(".saw") ) DirectoryPath = DirectoryPath.substring(0, DirectoryPath.lastIndexOf("\\")+1);
        if ( !DirectoryPath.endsWith("\\") ) DirectoryPath += "\\";

        String path;
        if ( imageFiles.isEmpty() ) { imageFiles = LoadDirectories(DirectoryPath); }

        for ( String nameToCheck : PossibleNames ) {
            path = CheckDirectories( nameToCheck, imageFiles );
            if ( !path.isEmpty() ) return path;
        }

        return "";
    }

    private ArrayList<File> LoadDirectories( String DirectoryPath ) {
        ArrayList<File> fileList = new ArrayList<File>();
        try
        {
            File d = new File(Utils.convertFilePathSeparator(DirectoryPath));
            if ( d.isDirectory() ) {
                for ( File f : d.listFiles() ) {
                    if ( f.isDirectory() ) {
                        fileList.addAll(LoadDirectories(f.getAbsolutePath()));
                    } else {
                        fileList.add(f);
                    }
                }
            }
        } catch ( Exception e ) { return fileList; }

        return fileList;
    }

    private String CheckDirectories( String nameToCheck, ArrayList<File> files ) {
        try
        {
            for( File f : files ) {
                if ( f.getName().substring(0, f.getName().lastIndexOf(".")).toLowerCase().equals( nameToCheck.trim().toLowerCase() ) )
                    return f.getCanonicalPath();
            }
        } catch ( Exception e ) { return ""; }
        return "";
    }

    public String FindMatchingImage( String Name, String Model ) {
        return FindMatchingImage( Name, Model, "");
    }

    public String FindMatchingImage( String Name ) {
        return FindMatchingImage( Name, "" );
    }

    public String DetermineMatchingImage( String Name, String Model, String CurrentImage ) {
        if ( !CurrentImage.isEmpty() )
        {
            Image img = GetImage(CurrentImage);
            if ( img.getHeight(null) > 0 )
                return CurrentImage;
        }
        return FindMatchingImage(Name, Model, "");
    }

    public static void Messager(String message) {
        javax.swing.JOptionPane.showMessageDialog(null, message);
    }

    public static void Messager(Component component, String message) {
        javax.swing.JOptionPane.showMessageDialog(component, message);
    }

    public static int Options(Component component, String Message, String Title) {
        return javax.swing.JOptionPane.showOptionDialog(component, Message, Title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
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

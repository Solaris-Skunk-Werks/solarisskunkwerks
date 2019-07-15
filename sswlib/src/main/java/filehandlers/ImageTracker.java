/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package filehandlers;

import IO.JarResources;
import Print.PrintConsts;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageTracker {
    public Media media = new Media();
    private ArrayList<StoredImage> imageStore = new ArrayList<StoredImage>();
    private HashMap<String, JarResources> resources = new HashMap<String, JarResources>();

    public ImageTracker() {
        addResource( PrintConsts.PATTERNS, false);
    }

    public void addResource( JarResources resource ) {
        if ( !resources.containsValue(resource) ) {
            resources.put(resource.getJarFileName(), resource);
        }
    }

    public void addResource( String zipName ) {
        if ( !resources.containsKey(zipName) ) {
            resources.put(zipName, new JarResources(zipName));
        }
    }

    public final void addResource( String zipName, boolean preLoad ) {
        JarResources resource = new JarResources(zipName);
        addResource(resource);
        
        if ( preLoad ) {
            for ( Map.Entry<String, byte[]> entry : resource.getContents().entrySet() ) {
                imageStore.add(new StoredImage(entry.getKey(), media.LoadImage(entry.getValue())));
            }
        }
    }

    public void preLoadMechImages() {
        try {
            LoadImageFromFile( PrintConsts.RS_TW_BP );
            LoadImageFromFile( PrintConsts.RS_TW_QD );
            LoadImageFromFile( PrintConsts.BP_ChartImage );
            LoadImageFromFile( PrintConsts.QD_ChartImage );
        } catch ( Exception e ) {
            System.out.println(e.getMessage());
        }
    }
    
    public void preLoadBattleForceImages() {
        LoadImageFromFile( PrintConsts.BF_BG );
        LoadImageFromFile( PrintConsts.BF_Card );
        LoadImageFromFile( PrintConsts.BF_Chart );
    }
    
    public void preLoadVehicleImages() {
        LoadImageFromFile( PrintConsts.RS_TW_GROUND );
        LoadImageFromFile( PrintConsts.RS_TW_VTOL );
        LoadImageFromFile( PrintConsts.RS_TW_NAVAL );
    }
    
    public Image getImage( String filename ) {
        for ( StoredImage PreLoadImage : imageStore ) {
            if ( PreLoadImage.filename.equals(filename) ) {
                return PreLoadImage.image;
            }
        }

        Image tempimg = null;
        for ( JarResources resource : resources.values() ) {
            byte[] image = resource.getResource(filename);
            if ( image != null ) { tempimg = media.LoadImage(resource.getResource(filename)); }
            if ( tempimg != null ) {
                //System.out.println(filename + " retrieved from zip file!");
                imageStore.add(new StoredImage(filename, tempimg));
                break;
            }
        }
        
        if ( tempimg == null ) {
            tempimg = media.GetImage(filename);
            if ( tempimg != null ) {
                //System.out.println(filename + " retrieved from file system.");
                imageStore.add(new StoredImage(filename, tempimg));
            }
        }

        return tempimg;
    }

    private void LoadImageFromFile( String filename ) {
        Image tempimg = null;
        tempimg = media.GetImage(filename);
        if ( tempimg != null ) {
            imageStore.add(new StoredImage(filename, tempimg));
        }
    }

    private class StoredImage {
        String filename = "";
        Image image;

        public StoredImage( String filename, Image image ) {
            this.filename = filename;
            this.image = image;
        }
    }
}

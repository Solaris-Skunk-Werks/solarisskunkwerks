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

package ssw.print;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Hashtable;
import ssw.components.*;
import ssw.Constants;
import ssw.filehandlers.Media;

public class ArmorPrinter {
    private Graphics2D graphics = null;
    private boolean useCanon = true;
    private Mech CurMech = null;
    private Hashtable<Integer, ArmorSettings> Locations = new Hashtable<Integer, ArmorSettings>();
    private String filePath = "./rs/patterns/";
    private String Source = "TW";
    private String Chassis = "BP";
    private String fileExtension = ".gif";
    private ifPrintPoints Points = null;

    private Media media = new Media();

    // <editor-fold desc="Constructors">
    /**
     * Creates an ArmorPrinter object with the default settings
     *
     */
    public ArmorPrinter() {
        this(null, null, true);
    }

    /**
     * Creates an ArmorPrinter object with the given graphics object
     *
     * @param  graphics  the graphics object to use when rendering
     */
    public ArmorPrinter(Graphics2D graphics) {
        this(graphics, null, true);
    }
    
    /**
     * Creates an ArmorPrinter object with the given objects
     *
     * @param  graphics  the graphics object to use when rendering
     * @param  curMech   the Mech object to use
     */
    public ArmorPrinter( Graphics2D graphics, Mech curMech ) {
        this(graphics, curMech, true);
    }
    
    /**
     * Creates an ArmorPrinter object with the given objects
     *
     * @param  graphics  the graphics object to use when rendering
     * @param  curMech   the Mech object to use
     * @param  useCanon  boolean determining whether or not to use canon points
     */
    public ArmorPrinter( Graphics2D graphics, Mech curMech, boolean useCanon ) {
        this.graphics = graphics;
        this.CurMech = curMech;
        this.useCanon = useCanon;

        Points = new TWBipedPoints();
        if ( CurMech.IsQuad() ) { Points = new TWQuadPoints(); }
        
        Locations.put(Constants.LOC_HD, new ArmorSettings(Constants.LOC_HD, new Point(463,52), new Point(17,20), "HD_", Points.GetArmorHDPoints()));
        Locations.put(Constants.LOC_CT, new ArmorSettings(Constants.LOC_CT, new Point(457,83), new Point(28,88), "CT_", Points.GetArmorCTPoints()));
        Locations.put(Constants.LOC_LT, new ArmorSettings(Constants.LOC_LT, new Point(0,0), new Point(0,0), "LT_", Points.GetArmorLTPoints()));
        Locations.put(Constants.LOC_RT, new ArmorSettings(Constants.LOC_RT, new Point(0,0), new Point(0,0), "LT_", Points.GetArmorRTPoints()));
        Locations.put(Constants.LOC_LA, new ArmorSettings(Constants.LOC_LA, new Point(387,55), new Point(30,98), "LA_", Points.GetArmorLAPoints()));
        Locations.put(Constants.LOC_RA, new ArmorSettings(Constants.LOC_RA, new Point(556,55), new Point(-30,98), "LA_", Points.GetArmorRAPoints()));
        Locations.put(Constants.LOC_LL, new ArmorSettings(Constants.LOC_LL, new Point(400,161), new Point(51,125), "LL_", Points.GetArmorLLPoints()));
        Locations.put(Constants.LOC_RL, new ArmorSettings(Constants.LOC_RL, new Point(542,161), new Point(-51,125), "LL_", Points.GetArmorRLPoints()));
        Locations.put(Constants.LOC_CTR, new ArmorSettings(Constants.LOC_CTR, new Point(459,283), new Point(23,70), "CTR_", Points.GetArmorCTRPoints()));
        Locations.put(Constants.LOC_LTR, new ArmorSettings(Constants.LOC_LTR, new Point(423,297), new Point(30,38), "LTR_", Points.GetArmorLTRPoints()));
        Locations.put(Constants.LOC_RTR, new ArmorSettings(Constants.LOC_RTR, new Point(520,297), new Point(-30,38), "LTR_", Points.GetArmorRTRPoints()));
    }
    // </editor-fold>

    /**
     * Renders the armor points for the print out based on the inputs received
     *
     * @param  graphics  the 2d graphics object to write to
     * @param  Location  the location to render to
     * @param  Points    the number of points to render in the location
     * @param  useCanon  use Canon placement of points or a more linear format
     * @return void
     */
    public void Render( Graphics2D graphics, Mech CurMech, boolean useCanon ) {
        this.graphics = graphics;
        this.useCanon = useCanon;
        this.CurMech = CurMech;

        Render();
    }

    /**
     * Renders the armor points for the print out based on the inputs received
     * <p>
     * Assumes that you have pre-set the graphics, location, useCanon, and Points
     * fields using the set methods.
     *
     * @return void
     */
    public void Render( ) {
        if ( graphics == null ) { return; }
        if ( CurMech == null ) { return; }

        if ( CurMech.IsQuad() ) { Chassis = "QD"; }

        for ( int key : Locations.keySet() ) {
            ArmorSettings settings = (ArmorSettings) Locations.get(key);
            if ( useCanon && !settings.startingPoint.equals(new Point(0,0)) ) {
                graphics.drawImage(media.GetImage(filePath + Source + "_" + Chassis + "_" + settings.locationPrefix + settings.GetFileNumber() + fileExtension), settings.startingPoint.x, settings.startingPoint.y, settings.imageSize.x, settings.imageSize.y, null);
            } else {
               for( int i = 0; i < settings.GetArmor(); i++ ) {
                    graphics.drawOval( settings.points[i].x, settings.points[i].y, 5, 5 );
                }
            }
        }
    }

// <editor-fold desc="Settor Methods">
    /**
     * Sets the graphics object to use when rendering
     *
     * @param  graphics  the 2d graphics object to write to
     */
    public void setGraphics( Graphics2D graphics ) {
        this.graphics = graphics;
    }

    /**
     * Selects whether to use canon placement or not
     *
     * @param  useCanon true/false
     */
    public void setCanon( boolean useCanon ) {
        this.useCanon = useCanon;
    }

    /**
     * Sets the Mech object to use for printing
     *
     * @param  mech the Mech object to use
     */
    public void setMech( Mech mech ) {
        this.CurMech = mech;
    }
// </editor-fold>

    private class ArmorSettings {
        public int LocationID = 0;
        public Point startingPoint = null;
        public Point imageSize = null;
        public String locationPrefix = "";
        public int max = 0;
        public Point[] points = null;

        public ArmorSettings(int LocationID, Point startingPoint, Point imageSize, String locationPrefix, Point[] Points) {
            this.LocationID = LocationID;
            this.startingPoint = startingPoint;
            this.imageSize = imageSize;
            this.locationPrefix = locationPrefix;
            this.points = Points;
            this.max = Points.length;
        }

        public int GetArmor() {
            return CurMech.GetArmor().GetLocationArmor(LocationID);
        }

        public String GetFileNumber() {
            String FileNumber = GetArmor() + "";
            if ( FileNumber.length() == 1 ) { FileNumber = "0" + FileNumber; }
            return FileNumber;
        }
    }
}

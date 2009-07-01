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
import java.io.File;
import java.util.Hashtable;
import ssw.components.*;
import ssw.Constants;
import ssw.filehandlers.Media;

public class PIPPrinter {
    private Graphics2D graphics = null;
    private boolean useCanon = true;
    private Mech CurMech = null;
    private Hashtable<Integer, PIPSettings> Armor = new Hashtable<Integer, PIPSettings>();
    private Hashtable<Integer, PIPSettings> Internal = new Hashtable<Integer, PIPSettings>();
    private String filePath = "./rs/patterns/";
    private String Source = "TW";
    private String Chassis = "BP";
    private String ExtensionGIF = ".gif";
    private String ExtensionPNG = ".png";
    private ifPrintPoints Points = null;

    private Media media = new Media();

    // <editor-fold desc="Constructors">
    /**
     * Creates an PIPPrinter object with the default settings
     *
     */
    public PIPPrinter() {
        this(null, null, true);
    }

    /**
     * Creates an PIPPrinter object with the given graphics object
     *
     * @param  graphics  the graphics object to use when rendering
     */
    public PIPPrinter(Graphics2D graphics) {
        this(graphics, null, true);
    }
    
    /**
     * Creates an PIPPrinter object with the given objects
     *
     * @param  graphics  the graphics object to use when rendering
     * @param  curMech   the Mech object to use
     */
    public PIPPrinter( Graphics2D graphics, Mech curMech ) {
        this(graphics, curMech, true);
    }
    
    /**
     * Creates an PIPPrinter object with the given objects
     *
     * @param  graphics  the graphics object to use when rendering
     * @param  curMech   the Mech object to use
     * @param  useCanon  boolean determining whether or not to use canon points
     */
    public PIPPrinter( Graphics2D graphics, Mech curMech, boolean useCanon ) {
        this.graphics = graphics;
        this.CurMech = curMech;
        this.useCanon = useCanon;

        Points = new TWBipedPoints();

        Armor.put(Constants.LOC_HD, new PIPSettings(Constants.LOC_HD, false, new Point(463,52), new Point(17,20), "HD_", Points.GetArmorHDPoints()));
        Armor.put(Constants.LOC_CT, new PIPSettings(Constants.LOC_CT, false, new Point(457,84), new Point(28,88), "CT_", Points.GetArmorCTPoints()));
        Armor.put(Constants.LOC_LT, new PIPSettings(Constants.LOC_LT, false, new Point(422,66), new Point(32,86), "LT_", Points.GetArmorLTPoints()));
        Armor.put(Constants.LOC_RT, new PIPSettings(Constants.LOC_RT, false, new Point(520,66), new Point(-32,86), "LT_", Points.GetArmorRTPoints()));
        Armor.put(Constants.LOC_LA, new PIPSettings(Constants.LOC_LA, false, new Point(387,55), new Point(30,98), "LA_", Points.GetArmorLAPoints()));
        Armor.put(Constants.LOC_RA, new PIPSettings(Constants.LOC_RA, false, new Point(556,55), new Point(-30,98), "LA_", Points.GetArmorRAPoints()));
        Armor.put(Constants.LOC_LL, new PIPSettings(Constants.LOC_LL, false, new Point(400,160), new Point(51,125), "LL_", Points.GetArmorLLPoints()));
        Armor.put(Constants.LOC_RL, new PIPSettings(Constants.LOC_RL, false, new Point(542,160), new Point(-51,125), "LL_", Points.GetArmorRLPoints()));
        Armor.put(Constants.LOC_CTR, new PIPSettings(Constants.LOC_CTR, false, new Point(460,283), new Point(23,70), "CTR_", Points.GetArmorCTRPoints()));
        Armor.put(Constants.LOC_LTR, new PIPSettings(Constants.LOC_LTR, false, new Point(423,297), new Point(30,38), "LTR_", Points.GetArmorLTRPoints()));
        Armor.put(Constants.LOC_RTR, new PIPSettings(Constants.LOC_RTR, false, new Point(520,297), new Point(-30,38), "LTR_", Points.GetArmorRTRPoints()));

        Internal.put(Constants.LOC_HD, new PIPSettings(Constants.LOC_HD, true, new Point(452,389), new Point(13,13), "INT_HD_", Points.GetInternalHDPoints()));
        Internal.put(Constants.LOC_CT, new PIPSettings(Constants.LOC_CT, true, new Point(450,410), new Point(17,61), "INT_CT_", Points.GetInternalCTPoints()));
        Internal.put(Constants.LOC_LT, new PIPSettings(Constants.LOC_LT, true, new Point(426,401), new Point(21,59), "INT_LT_", Points.GetInternalLTPoints()));
        Internal.put(Constants.LOC_RT, new PIPSettings(Constants.LOC_RT, true, new Point(490,401), new Point(-21,59), "INT_LT_", Points.GetInternalRTPoints()));
        Internal.put(Constants.LOC_LA, new PIPSettings(Constants.LOC_LA, true, new Point(402,400), new Point(14,75), "INT_LA_", Points.GetInternalLAPoints()));
        Internal.put(Constants.LOC_RA, new PIPSettings(Constants.LOC_RA, true, new Point(514,400), new Point(-14,75), "INT_LA_", Points.GetInternalRAPoints()));
        Internal.put(Constants.LOC_LL, new PIPSettings(Constants.LOC_LL, true, new Point(418,463), new Point(25,89), "INT_LL_", Points.GetInternalLLPoints()));
        Internal.put(Constants.LOC_RL, new PIPSettings(Constants.LOC_RL, true, new Point(498,463), new Point(-25,89), "INT_LL_", Points.GetInternalRLPoints()));

        if ( CurMech.IsQuad() ) {
            Points = new TWQuadPoints();
            Chassis = "QD";

            Armor.get(Constants.LOC_HD).setStartAndSize(new Point(458,60), new Point(27,20), Points.GetArmorHDPoints());
            Armor.get(Constants.LOC_CT).setStartAndSize(new Point(454,85), new Point(35,68), Points.GetArmorCTPoints());
            Armor.get(Constants.LOC_LT).setStartAndSize(new Point(415,44), new Point(28,66), Points.GetArmorLTPoints());
            Armor.get(Constants.LOC_RT).setStartAndSize(new Point(527,44), new Point(-28,66), Points.GetArmorRTPoints());
            Armor.get(Constants.LOC_LA).setStartAndSize(new Point(402,122), new Point(25,140), Points.GetArmorLAPoints());
            Armor.get(Constants.LOC_RA).setStartAndSize(new Point(541,122), new Point(-25,140), Points.GetArmorRAPoints());
            Armor.get(Constants.LOC_LL).setStartAndSize(new Point(430,119), new Point(27,134), Points.GetArmorLLPoints());
            Armor.get(Constants.LOC_RL).setStartAndSize(new Point(513,119), new Point(-27,134), Points.GetArmorRLPoints());
            Armor.get(Constants.LOC_CTR).setStartAndSize(new Point(457,292), new Point(30,53), Points.GetArmorCTRPoints());
            Armor.get(Constants.LOC_LTR).setStartAndSize(new Point(424,294), new Point(30,48), Points.GetArmorLTRPoints());
            Armor.get(Constants.LOC_RTR).setStartAndSize(new Point(519,294), new Point(-30,48), Points.GetArmorRTRPoints());

            Internal.get(Constants.LOC_HD).setStartAndSize(new Point(450,400), new Point(14,13), Points.GetInternalHDPoints());
            Internal.get(Constants.LOC_CT).setStartAndSize(new Point(446,420), new Point(23,41), Points.GetInternalCTPoints());
            Internal.get(Constants.LOC_LT).setStartAndSize(new Point(417,403), new Point(23,36), Points.GetInternalLTPoints());
            Internal.get(Constants.LOC_RT).setStartAndSize(new Point(498,403), new Point(-23,36), Points.GetInternalRTPoints());
            Internal.get(Constants.LOC_LA).setStartAndSize(new Point(411,456), new Point(16,86), Points.GetInternalLAPoints());
            Internal.get(Constants.LOC_RA).setStartAndSize(new Point(504,456), new Point(-16,86), Points.GetInternalRAPoints());
            Internal.get(Constants.LOC_LL).setStartAndSize(new Point(433,453), new Point(10,86), Points.GetInternalLLPoints());
            Internal.get(Constants.LOC_RL).setStartAndSize(new Point(482,453), new Point(-10,86), Points.GetInternalRLPoints());
        }
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
        String filename = "";
        File check = null;

        for ( int key : Armor.keySet() ) {
            PIPSettings settings = (PIPSettings) Armor.get(key);
            if ( useCanon && !settings.startingPoint.equals(new Point(0,0)) ) {
                filename = filePath + Source + "_" + Chassis + "_" + settings.locationPrefix + settings.GetFileNumber() + ExtensionGIF;
                check = new File( filename  );
                if( ! check.exists() ) {
                    filename = filePath + Source + "_" + Chassis + "_" + settings.locationPrefix + settings.GetFileNumber() + ExtensionPNG;
                    check = new File( filename  );
                    if( ! check.exists() ) {
                       for( int i = 0; i < settings.GetArmor(); i++ ) {
                            graphics.drawOval( settings.points[i].x, settings.points[i].y, 5, 5 );
                        }
                    } else {
                        graphics.drawImage(media.GetImage( filename  ), settings.startingPoint.x, settings.startingPoint.y, settings.imageSize.x, settings.imageSize.y, null);
                    }
                } else {
                    graphics.drawImage(media.GetImage( filename  ), settings.startingPoint.x, settings.startingPoint.y, settings.imageSize.x, settings.imageSize.y, null);
                }
            } else {
               for( int i = 0; i < settings.GetArmor(); i++ ) {
                    graphics.drawOval( settings.points[i].x, settings.points[i].y, 5, 5 );
                }
            }
        }
        for ( int key : Internal.keySet() ) {
            PIPSettings settings = (PIPSettings) Internal.get(key);
            if ( useCanon && !settings.startingPoint.equals(new Point(0,0)) ) {
                filename = filePath + Source + "_" + Chassis + "_" + settings.locationPrefix + settings.GetFileNumber() + ExtensionGIF;
                check = new File( filename  );
                if( ! check.exists() ) {
                    filename = filePath + Source + "_" + Chassis + "_" + settings.locationPrefix + settings.GetFileNumber() + ExtensionPNG;
                    check = new File( filename  );
                    if( ! check.exists() ) {
                       for( int i = 0; i < settings.GetInternals(); i++ ) {
                            graphics.drawOval( settings.points[i].x, settings.points[i].y, 5, 5 );
                        }
                    } else {
                        graphics.drawImage(media.GetImage( filename ), settings.startingPoint.x, settings.startingPoint.y, settings.imageSize.x, settings.imageSize.y, null);
                    }
                } else {
                    graphics.drawImage(media.GetImage( filename ), settings.startingPoint.x, settings.startingPoint.y, settings.imageSize.x, settings.imageSize.y, null);
                }
            } else {
               for( int i = 0; i < settings.GetInternals(); i++ ) {
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

    private class PIPSettings {
        public int LocationID = 0;
        public Point startingPoint = null;
        public Point imageSize = null;
        public String locationPrefix = "";
        public int max = 0;
        public Point[] points = null;
        public boolean Internal = false;

        public PIPSettings(int LocationID, boolean internal, Point startingPoint, Point imageSize, String locationPrefix, Point[] Points) {
            this.LocationID = LocationID;
            this.startingPoint = startingPoint;
            this.imageSize = imageSize;
            this.locationPrefix = locationPrefix;
            this.points = Points;
            this.max = Points.length;
            this.Internal = internal;
        }

        public int GetArmor() {
            return CurMech.GetArmor().GetLocationArmor(LocationID);
        }

        public int GetInternals() {
            switch( LocationID ) {
                case Constants.LOC_HD:
                    return CurMech.GetIntStruc().GetHeadPoints();
                case Constants.LOC_CT:
                    return CurMech.GetIntStruc().GetCTPoints();
                case Constants.LOC_LT: case Constants.LOC_RT:
                    return CurMech.GetIntStruc().GetSidePoints();
                case Constants.LOC_LA: case Constants.LOC_RA:
                    return CurMech.GetIntStruc().GetArmPoints();
                case Constants.LOC_LL: case Constants.LOC_RL:
                    return CurMech.GetIntStruc().GetLegPoints();
            }
            return 0;
        }

        public String GetFileNumber() {
            String FileNumber = "";
            if( Internal ) {
                FileNumber = GetInternals() + "";
            } else {
                FileNumber = GetArmor() + "";
            }
            if ( FileNumber.length() == 1 ) { FileNumber = "0" + FileNumber; }
            return FileNumber;
        }

        public void setStartAndSize( Point newStart, Point newSize, Point[] Points ) {
            this.startingPoint = newStart;
            this.imageSize = newSize;
            this.points = Points;
        }
    }
}

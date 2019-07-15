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

package Print;

import Print.Points.PIPRow;
import components.CombatVehicle;
import components.LocationIndex;
import components.Mech;
import filehandlers.ImageTracker;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PIPPrinter {
    private Graphics2D graphics = null;
    private boolean useCanon = true;
    private Mech CurMech = null;
    private CombatVehicle CurVee = null;
    private ImageTracker imageTracker;
    private HashMap<Integer, PIPSettings> Armor = new HashMap<Integer, PIPSettings>();
    private HashMap<Integer, PIPSettings> Internal = new HashMap<Integer, PIPSettings>();
    private String filePath = "/data/";
    private String Source = "TW";
    private String Chassis = "BP";
    private String ExtensionGIF = ".gif";
    private String ExtensionPNG = ".png";
    private ifPrintPoints Points = null;

    // <editor-fold desc="Constructors">
    /**
     * Creates an PIPPrinter object with the default settings
     *
     */
    public PIPPrinter(ImageTracker images) {
        this.imageTracker = images;
    }

    /**
     * Creates an PIPPrinter object with the given graphics object
     *
     * @param  graphics  the graphics object to use when rendering
     */
    public PIPPrinter(Graphics2D graphics, ImageTracker images) {
        this.imageTracker = images;
        this.graphics = graphics;
    }
    
    /**
     * Creates an PIPPrinter object with the given objects
     *
     * @param  graphics  the graphics object to use when rendering
     * @param  curMech   the Mech object to use
     */
    public PIPPrinter( Graphics2D graphics, Mech curMech, ImageTracker images ) {
        this(graphics, curMech, true, images);
    }
    
    /**
     * Creates an PIPPrinter object with the given objects
     *
     * @param  graphics  the graphics object to use when rendering
     * @param  curMech   the Mech object to use
     * @param  useCanon  boolean determining whether or not to use canon points
     */
    public PIPPrinter( Graphics2D graphics, Mech curMech, boolean useCanon, ImageTracker images ) {
        this.graphics = graphics;
        this.CurMech = curMech;
        this.useCanon = useCanon;
        this.imageTracker = images;

        Points = new TWBipedPoints();

        Armor.put(LocationIndex.MECH_LOC_HD, new PIPSettings(LocationIndex.MECH_LOC_HD, false, new Point(463,52), new Point(17,20), "HD_", Points.GetArmorHDPoints()));
        Armor.put(LocationIndex.MECH_LOC_CT, new PIPSettings(LocationIndex.MECH_LOC_CT, false, new Point(457,84), new Point(28,88), "CT_", Points.GetArmorCTPoints()));
        Armor.put(LocationIndex.MECH_LOC_LT, new PIPSettings(LocationIndex.MECH_LOC_LT, false, new Point(422,66), new Point(32,86), "LT_", Points.GetArmorLTPoints()));
        Armor.put(LocationIndex.MECH_LOC_RT, new PIPSettings(LocationIndex.MECH_LOC_RT, false, new Point(520,66), new Point(-32,86), "LT_", Points.GetArmorRTPoints()));
        Armor.put(LocationIndex.MECH_LOC_LA, new PIPSettings(LocationIndex.MECH_LOC_LA, false, new Point(387,55), new Point(30,98), "LA_", Points.GetArmorLAPoints()));
        Armor.put(LocationIndex.MECH_LOC_RA, new PIPSettings(LocationIndex.MECH_LOC_RA, false, new Point(556,55), new Point(-30,98), "LA_", Points.GetArmorRAPoints()));
        Armor.put(LocationIndex.MECH_LOC_LL, new PIPSettings(LocationIndex.MECH_LOC_LL, false, new Point(400,160), new Point(51,125), "LL_", Points.GetArmorLLPoints()));
        Armor.put(LocationIndex.MECH_LOC_RL, new PIPSettings(LocationIndex.MECH_LOC_RL, false, new Point(542,160), new Point(-51,125), "LL_", Points.GetArmorRLPoints()));
        Armor.put(LocationIndex.MECH_LOC_CTR, new PIPSettings(LocationIndex.MECH_LOC_CTR, false, new Point(460,283), new Point(23,70), "CTR_", Points.GetArmorCTRPoints()));
        Armor.put(LocationIndex.MECH_LOC_LTR, new PIPSettings(LocationIndex.MECH_LOC_LTR, false, new Point(423,297), new Point(30,38), "LTR_", Points.GetArmorLTRPoints()));
        Armor.put(LocationIndex.MECH_LOC_RTR, new PIPSettings(LocationIndex.MECH_LOC_RTR, false, new Point(520,297), new Point(-30,38), "LTR_", Points.GetArmorRTRPoints()));

        Internal.put(LocationIndex.MECH_LOC_HD, new PIPSettings(LocationIndex.MECH_LOC_HD, true, new Point(452,389), new Point(13,13), "INT_HD_", Points.GetInternalHDPoints()));
        Internal.put(LocationIndex.MECH_LOC_CT, new PIPSettings(LocationIndex.MECH_LOC_CT, true, new Point(450,410), new Point(17,61), "INT_CT_", Points.GetInternalCTPoints()));
        Internal.put(LocationIndex.MECH_LOC_LT, new PIPSettings(LocationIndex.MECH_LOC_LT, true, new Point(426,401), new Point(21,59), "INT_LT_", Points.GetInternalLTPoints()));
        Internal.put(LocationIndex.MECH_LOC_RT, new PIPSettings(LocationIndex.MECH_LOC_RT, true, new Point(490,401), new Point(-21,59), "INT_LT_", Points.GetInternalRTPoints()));
        Internal.put(LocationIndex.MECH_LOC_LA, new PIPSettings(LocationIndex.MECH_LOC_LA, true, new Point(402,400), new Point(14,75), "INT_LA_", Points.GetInternalLAPoints()));
        Internal.put(LocationIndex.MECH_LOC_RA, new PIPSettings(LocationIndex.MECH_LOC_RA, true, new Point(514,400), new Point(-14,75), "INT_LA_", Points.GetInternalRAPoints()));
        Internal.put(LocationIndex.MECH_LOC_LL, new PIPSettings(LocationIndex.MECH_LOC_LL, true, new Point(418,463), new Point(25,89), "INT_LL_", Points.GetInternalLLPoints()));
        Internal.put(LocationIndex.MECH_LOC_RL, new PIPSettings(LocationIndex.MECH_LOC_RL, true, new Point(498,463), new Point(-25,89), "INT_LL_", Points.GetInternalRLPoints()));

        if ( CurMech.IsQuad() ) {
            Points = new TWQuadPoints();
            Chassis = "QD";

            Armor.get(LocationIndex.MECH_LOC_HD).setStartAndSize(new Point(458,60), new Point(27,20), Points.GetArmorHDPoints());
            Armor.get(LocationIndex.MECH_LOC_CT).setStartAndSize(new Point(454,85), new Point(35,68), Points.GetArmorCTPoints());
            Armor.get(LocationIndex.MECH_LOC_LT).setStartAndSize(new Point(415,44), new Point(28,66), Points.GetArmorLTPoints());
            Armor.get(LocationIndex.MECH_LOC_RT).setStartAndSize(new Point(527,44), new Point(-28,66), Points.GetArmorRTPoints());
            Armor.get(LocationIndex.MECH_LOC_LA).setStartAndSize(new Point(402,122), new Point(25,140), Points.GetArmorLAPoints());
            Armor.get(LocationIndex.MECH_LOC_RA).setStartAndSize(new Point(541,122), new Point(-25,140), Points.GetArmorRAPoints());
            Armor.get(LocationIndex.MECH_LOC_LL).setStartAndSize(new Point(430,119), new Point(27,134), Points.GetArmorLLPoints());
            Armor.get(LocationIndex.MECH_LOC_RL).setStartAndSize(new Point(513,119), new Point(-27,134), Points.GetArmorRLPoints());
            Armor.get(LocationIndex.MECH_LOC_CTR).setStartAndSize(new Point(457,292), new Point(30,53), Points.GetArmorCTRPoints());
            Armor.get(LocationIndex.MECH_LOC_LTR).setStartAndSize(new Point(424,294), new Point(30,48), Points.GetArmorLTRPoints());
            Armor.get(LocationIndex.MECH_LOC_RTR).setStartAndSize(new Point(519,294), new Point(-30,48), Points.GetArmorRTRPoints());

            Internal.get(LocationIndex.MECH_LOC_HD).setStartAndSize(new Point(450,400), new Point(14,13), Points.GetInternalHDPoints());
            Internal.get(LocationIndex.MECH_LOC_CT).setStartAndSize(new Point(446,420), new Point(23,41), Points.GetInternalCTPoints());
            Internal.get(LocationIndex.MECH_LOC_LT).setStartAndSize(new Point(417,403), new Point(23,36), Points.GetInternalLTPoints());
            Internal.get(LocationIndex.MECH_LOC_RT).setStartAndSize(new Point(498,403), new Point(-23,36), Points.GetInternalRTPoints());
            Internal.get(LocationIndex.MECH_LOC_LA).setStartAndSize(new Point(411,456), new Point(16,86), Points.GetInternalLAPoints());
            Internal.get(LocationIndex.MECH_LOC_RA).setStartAndSize(new Point(504,456), new Point(-16,86), Points.GetInternalRAPoints());
            Internal.get(LocationIndex.MECH_LOC_LL).setStartAndSize(new Point(433,453), new Point(10,86), Points.GetInternalLLPoints());
            Internal.get(LocationIndex.MECH_LOC_RL).setStartAndSize(new Point(482,453), new Point(-10,86), Points.GetInternalRLPoints());
        }
    }
    // </editor-fold>

    public PIPPrinter( Graphics2D graphics, CombatVehicle curVee, boolean useCanon, ImageTracker images ) {
        this.graphics = graphics;
        this.CurVee = curVee;
        this.useCanon = useCanon;
        this.imageTracker = images;

        Points = new TWGroundPoints();
        if ( CurVee.isHasTurret2() ) Points = new TWAdvGroundPoints();
        if ( CurVee.IsNaval() ) Points = new TWNavalPoints();
        if ( CurVee.IsVTOL() ) Points = new TWVTOLPoints();
        
        Armor.put(LocationIndex.CV_LOC_FRONT, new PIPSettings(LocationIndex.CV_LOC_FRONT, false, new Point(463,52), new Point(17,20), "FRONT_", Points.GetArmorFrontPoints()));
        Armor.put(LocationIndex.CV_LOC_LEFT, new PIPSettings(LocationIndex.CV_LOC_LEFT, false, new Point(457,84), new Point(28,88), "LEFT_", Points.GetArmorLeftPoints()));
        Armor.put(LocationIndex.CV_LOC_RIGHT, new PIPSettings(LocationIndex.CV_LOC_RIGHT, false, new Point(422,66), new Point(32,86), "RIGHT_", Points.GetArmorRightPoints()));
        Armor.put(LocationIndex.CV_LOC_REAR, new PIPSettings(LocationIndex.CV_LOC_REAR, false, new Point(520,66), new Point(-32,86), "REAR_", Points.GetArmorRearPoints()));
        if ( CurVee.isHasTurret1() )
            Armor.put(LocationIndex.CV_LOC_TURRET1, new PIPSettings(LocationIndex.CV_LOC_TURRET1, false, new Point(387,55), new Point(30,98), "TURRET_", Points.GetArmorTurretPoints()));
        if ( CurVee.isHasTurret2() )
            Armor.put(LocationIndex.CV_LOC_TURRET2, new PIPSettings(LocationIndex.CV_LOC_TURRET2, false, new Point(556,55), new Point(-30,98), "TURRET2_", Points.GetArmorTurret2Points()));
        if ( CurVee.IsVTOL() )
            Armor.put(LocationIndex.CV_LOC_ROTOR, new PIPSettings(LocationIndex.CV_LOC_ROTOR, false, new Point(400,160), new Point(51,125), "ROTOR_", Points.GetArmorRotorPoints()));
        
        Internal.put(LocationIndex.CV_LOC_FRONT, new PIPSettings(LocationIndex.CV_LOC_FRONT, true, new Point(452,389), new Point(13,13), "INT_FRONT_", Points.GetInternalFrontPoints()));
        Internal.put(LocationIndex.CV_LOC_LEFT, new PIPSettings(LocationIndex.CV_LOC_LEFT, true, new Point(450,410), new Point(17,61), "INT_LEFT_", Points.GetInternalLeftPoints()));
        Internal.put(LocationIndex.CV_LOC_RIGHT, new PIPSettings(LocationIndex.CV_LOC_RIGHT, true, new Point(426,401), new Point(21,59), "INT_RIGHT_", Points.GetInternalRightPoints()));
        Internal.put(LocationIndex.CV_LOC_REAR, new PIPSettings(LocationIndex.CV_LOC_REAR, true, new Point(490,401), new Point(-21,59), "INT_REAR_", Points.GetInternalRearPoints()));
        if ( CurVee.isHasTurret1() )
            Internal.put(LocationIndex.CV_LOC_TURRET1, new PIPSettings(LocationIndex.CV_LOC_TURRET1, true, new Point(402,400), new Point(14,75), "INT_TURRET_", Points.GetInternalTurretPoints()));
        if ( CurVee.isHasTurret2() )
            Internal.put(LocationIndex.CV_LOC_TURRET2, new PIPSettings(LocationIndex.CV_LOC_TURRET2, true, new Point(514,400), new Point(-14,75), "INT_TURRET2_", Points.GetInternalTurret2Points()));
        if ( CurVee.IsVTOL() )
            Internal.put(LocationIndex.CV_LOC_ROTOR, new PIPSettings(LocationIndex.CV_LOC_ROTOR, true, new Point(418,463), new Point(25,89), "INT_ROTOR_", Points.GetInternalRotorPoints()));
        
    }
    
    public ifPrintPoints GetPoints()
    {
        return Points;
    }
    
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

    public void Render( Graphics2D graphics, CombatVehicle CurVee, boolean useCanon ) {
        this.graphics = graphics;
        this.useCanon = useCanon;
        
        if ( graphics == null ) { return; }

        graphics.setStroke(new BasicStroke(.75f));
        for ( int key : Armor.keySet() ) {
            PIPSettings settings = (PIPSettings) Armor.get(key);
            if ( useCanon && !settings.startingPoint.equals(new Point(0,0)) ) {
                renderImage(settings);
            } else {
                for( int i = 0; i < settings.GetArmor(CurVee); i++ ) {
                    if ( settings.points.length-1 >= i )
                        graphics.drawOval( settings.points[i].x, settings.points[i].y, 5, 5 );
                }
            }
        }
        for ( int key : Internal.keySet() ) {
            PIPSettings settings = (PIPSettings) Internal.get(key);
            if ( useCanon && !settings.startingPoint.equals(new Point(0,0)) ) {
                renderImage(settings);
            } else {
               for( int i = 0; i < CurVee.GetIntStruc().NumCVSpaces(); i++ ) {
                   if ( settings.points.length >= i ) 
                       PrintConsts.FilledCircle( graphics, Color.BLACK, Color.WHITE, 5, settings.points[i].x, settings.points[i].y);
                    //graphics.drawOval( settings.points[i].x, settings.points[i].y, 5, 5 );
                }
            }
        }
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

        for ( int key : Armor.keySet() ) {
            PIPSettings settings = (PIPSettings) Armor.get(key);
            if ( useCanon && !settings.startingPoint.equals(new Point(0,0)) ) {
                renderImage(settings);
            } else {

                // Check for a shield.  Shields are always generated via an image
                // This prevents a null pointer exception when you try to draw
                // non-existant armor points.  This only occurs when using
                // non-canon armor dots.
                if ( settings.points.length != 0 ) {

                   for( int i = 0; i < settings.GetArmor(); i++ ) {
                       graphics.drawOval( settings.points[i].x, settings.points[i].y, 5, 5 );
                    }
                } else {
                    // This location is a shield and will always be generated
                    // with an image.
                    renderImage(settings);
                }
            }
        }
        for ( int key : Internal.keySet() ) {
            PIPSettings settings = (PIPSettings) Internal.get(key);
            if ( useCanon && !settings.startingPoint.equals(new Point(0,0)) ) {
                renderImage(settings);
            } else {
               for( int i = 0; i < settings.GetInternals(); i++ ) {
                    graphics.drawOval( settings.points[i].x, settings.points[i].y, 5, 5 );
                }
            }
        }
    }

    private void renderImage( PIPSettings pip ) {
        String filename = "";
        Image pattern = null;
        //filename = filePath + Source + "_" + Chassis + "_" + pip.locationPrefix + pip.GetFileNumber();
        filename = Source + "_" + Chassis + "_" + pip.locationPrefix + pip.GetFileNumber();
        pattern = imageTracker.getImage(filename + ExtensionGIF);
        if ( pattern == null ) { pattern = imageTracker.getImage(filename + ExtensionPNG); }
        graphics.drawImage(pattern, pip.startingPoint.x, pip.startingPoint.y, pip.imageSize.x, pip.imageSize.y, null);
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

    public void AddArmor( String Location, Point startingPoint, Point imageSize, int ArmorPoints ) {
            Armor.put(Armor.size()+1, new PIPSettings(0, false, startingPoint, imageSize, Location, new Point[]{}, ArmorPoints));
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
        public Image pattern = null;
        private int PIPPoints = 0;

        public PIPSettings(int LocationID, boolean internal, Point startingPoint, Point imageSize, String locationPrefix, Point[] Points) {
            this.LocationID = LocationID;
            this.startingPoint = startingPoint;
            this.imageSize = imageSize;
            this.locationPrefix = locationPrefix;
            this.points = Points;
            this.max = Points.length;
            this.Internal = internal;
        }

        public PIPSettings(int LocationID, boolean internal, Point startingPoint, Point imageSize, String locationPrefix, Point[] Points, int PIPPoints) {
            this(LocationID, internal, startingPoint, imageSize, locationPrefix, Points);
            this.PIPPoints = PIPPoints;
        }

        public int GetArmor() {
            return CurMech.GetArmor().GetLocationArmor(LocationID);
        }
        
        public int GetArmor(CombatVehicle vee ) {
            return vee.GetArmor().GetLocationArmor(LocationID);
        }

        public int GetInternals() {
            switch( LocationID ) {
                case LocationIndex.MECH_LOC_HD:
                    return CurMech.GetIntStruc().GetHeadPoints();
                case LocationIndex.MECH_LOC_CT:
                    return CurMech.GetIntStruc().GetCTPoints();
                case LocationIndex.MECH_LOC_LT: case LocationIndex.MECH_LOC_RT:
                    return CurMech.GetIntStruc().GetSidePoints();
                case LocationIndex.MECH_LOC_LA: case LocationIndex.MECH_LOC_RA:
                    return CurMech.GetIntStruc().GetArmPoints();
                case LocationIndex.MECH_LOC_LL: case LocationIndex.MECH_LOC_RL:
                    return CurMech.GetIntStruc().GetLegPoints();
            }
            return 0;
        }

        public String GetFileNumber() {
            String FileNumber = "";
            if ( PIPPoints == 0 ) {
                if( Internal ) {
                    FileNumber = GetInternals() + "";
                } else {
                    FileNumber = GetArmor() + "";
                }
            } else {
                FileNumber = PIPPoints + "";
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

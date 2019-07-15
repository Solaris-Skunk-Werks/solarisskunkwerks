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
import java.awt.Point;
import java.util.ArrayList;

public class TWNavalPoints implements ifPrintPoints {
    //<editor-fold desc="Point Arrays">
    private final static Point[] WeaponPoints = {
        new Point( 10, 200 ), //count
        new Point( 28, 200 ), //name
        new Point( 115, 200 ), //loc
        new Point( 110, 200 ), //ht
        new Point( 136, 200 ), //dmg
        new Point( 159, 200 ), //min
        new Point( 174, 200 ), //sht
        new Point( 188, 200 ), //med
        new Point( 206, 200 ) }; //lng
    
    private final static Point[] DataPoints = {
        new Point( 32, 104 ), //MechName
        new Point( 58, 129 ), //WALKMP = 1,
        new Point( 58, 139 ), //RUNMP = 2
        new Point( 58, 149 ), //JUMPMP = 3
        new Point( 165, 119 ), //TONNAGE = 4
        new Point( 199, 131 ), //TECH_CLAN = 5
        new Point( 165, 130 ), //TECH_IS = 6
        new Point( 261, 104 ), //PILOT_NAME = 7
        new Point( 288, 118 ), //PILOT_GUN = 8
        new Point( 358, 118 ), //PILOT_PILOT = 9
        new Point( 35, 348 ), //COST = 10
        new Point( 140, 348 ), //BV2 = 11
        new Point( 497, 592 ), //HEATSINK_NUMBER = 12
        new Point( 511, 592 ), //HEATSINK_DISSIPATION = 13
        new Point( 522, 699 ),
        new Point( 522, 713 ), 
        new Point( 142, 354 ), //MAX_HEAT = 16
        new Point( 438, 26 ), //TOTAL_ARMOR = 17
        new Point( 122, 708 ),  //STATS = 18;
        new Point( 75, 150 ),      //Movement Type = 19
        new Point( 62, 162 )};     // Engine Type = 20
    
    private final static Point[] ArmorInfo = {
        new Point( 468, 40 ), // Front
        new Point( 372, 305 ), // left
        new Point( 565, 360 ), // right
        new Point( 468, 648 ), // rear
        new Point( 466, 382 ), // turret
        new Point( 0, 0 ), // rear turret
        new Point( 0, 0 ), // rotor
        new Point( 0, 0 ), // body
    };
    //</editor-fold>
    
    public Point[] GetArmorFrontPoints() {
        ArrayList<Point> Points = new ArrayList<Point>();
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Rows.add(new PIPRow(new Point(453,56), 6, 8, 8));
        Rows.add(new PIPRow(new Point(445,64), 8, 8, 8));
        Rows.add(new PIPRow(new Point(437,72), 10, 8, 8));
        Rows.add(new PIPRow(new Point(429,80), 12, 8, 8));
        Rows.add(new PIPRow(new Point(421,88), 14, 8, 8));
        Rows.add(new PIPRow(new Point(413,96), 16, 8, 8));
        Rows.add(new PIPRow(new Point(413,104), 16, 8, 8));
        Rows.add(new PIPRow(new Point(405,112), 18, 8, 8));
        Rows.add(new PIPRow(new Point(405,120), 18, 8, 8));
        
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetArmorLeftPoints() {
        ArrayList<Point> Points = new ArrayList<Point>();
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point starting = new Point(394, 156);
        Rows.add(new PIPRow(new Point( 395, 140), 1, 9, 8));
        Rows.add(new PIPRow(new Point( 395, 148), 2, 9, 8));
        Rows.add(new PIPRow(new Point( 395, 156), 3, 9, 8));
        for (int i = 0; i < 37; i++) {
            Rows.add(new PIPRow(new Point( 395, starting.y+8), 4, 9, 8));
            starting = new Point( 394, starting.y+8);
        }
        Rows.add(new PIPRow(new Point( 403, starting.y+8), 3, 9, 8));
        Rows.add(new PIPRow(new Point( 403, starting.y+(8*2)), 3, 9, 8));
        Rows.add(new PIPRow(new Point( 403, starting.y+(8*3)), 3, 9, 8));
        Rows.add(new PIPRow(new Point( 403, starting.y+(8*4)), 3, 9, 8));
        Rows.add(new PIPRow(new Point( 411, starting.y+(8*5)), 2, 9, 8));
        Rows.add(new PIPRow(new Point( 411, starting.y+(8*6)), 2, 9, 8));
        Rows.add(new PIPRow(new Point( 411, starting.y+(8*7)), 2, 9, 8));
        
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetArmorRightPoints() {
        ArrayList<Point> Points = new ArrayList<Point>();
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point starting = new Point(394, 156);
        Rows.add(new PIPRow(new Point( 553, 140), 1, -9, 8));
        Rows.add(new PIPRow(new Point( 553, 148), 2, -9, 8));
        Rows.add(new PIPRow(new Point( 553, 156), 3, -9, 8));
        for (int i = 0; i < 37; i++) {
            Rows.add(new PIPRow(new Point( 553, starting.y+8), 4, -9, 8));
            starting = new Point( 394, starting.y+8);
        }
        Rows.add(new PIPRow(new Point( 545, starting.y+8), 3, -9, 8));
        Rows.add(new PIPRow(new Point( 545, starting.y+(8*2)), 3, -9, 8));
        Rows.add(new PIPRow(new Point( 545, starting.y+(8*3)), 3, -9, 8));
        Rows.add(new PIPRow(new Point( 545, starting.y+(8*4)), 3, -9, 8));
        Rows.add(new PIPRow(new Point( 537, starting.y+(8*5)), 2, -9, 8));
        Rows.add(new PIPRow(new Point( 537, starting.y+(8*6)), 2, -9, 8));
        Rows.add(new PIPRow(new Point( 537, starting.y+(8*7)), 2, -9, 8));
        
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetArmorRearPoints() {
        ArrayList<Point> Points = new ArrayList<Point>();
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point starting = new Point(437, 530);
        for (int i = 0; i < 5; i++) {
            Rows.add(new PIPRow(new Point( starting.x, starting.y+8), 10, 8, 8));
            starting = new Point( starting.x, starting.y+8);
        }
        starting = new Point(starting.x+8, starting.y);
        for (int i = 0; i < 3; i++) {
            Rows.add(new PIPRow(new Point( starting.x, starting.y+8), 8, 8, 8));
            starting = new Point( starting.x, starting.y+8);
        }
        starting = new Point(starting.x+8, starting.y);
        for (int i = 0; i < 3; i++) {
            Rows.add(new PIPRow(new Point( starting.x, starting.y+8), 6, 8, 8));
            starting = new Point( starting.x, starting.y+8);
        }
        
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetArmorTurretPoints() {
        ArrayList<Point> Points = new ArrayList<Point>();
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point starting = new Point(438, 275);
        Point offset = new Point(8, 8);
        for (int i = 0; i < 9; i++) {
            Rows.add(new PIPRow(new Point(starting.x, starting.y), 3, offset.x, offset.y));
            Rows.add(new PIPRow(new Point(starting.x+(7*offset.x)-1, starting.y), 3, offset.x, offset.y));
            starting.setLocation(starting.x, starting.y+offset.y);
        }
        for (int i = 0; i < 3; i++) {
            Rows.add(new PIPRow(new Point(starting.x, starting.y), 10, offset.x, offset.y));
            starting.setLocation(starting.x, starting.y+offset.y);
        }
        
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetArmorTurret2Points() {
        return new Point[0];
    }

    public Point[] GetArmorRotorPoints() {
        return new Point[0];
    }

    public Point[] GetArmorInfoPoints() {
        return ArmorInfo;
    }

    public Point[] GetWeaponChartPoints() {
        return WeaponPoints;
    }

    public Point[] GetDataChartPoints() {
        return DataPoints;
    }

    public Point GetMechImageLoc() {
        return new Point( 231, 248 );
    }

    public Point GetImageBounds() {
        return new Point( 175, 123 );
    }
    
    public Point GetLogoImageLoc() {
        return new Point( 331, 195 );
    }

    public Point[] GetInternalFrontPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Rows.add(new PIPRow(new Point(418,129), 11, 11, 8));
        Rows.add(new PIPRow(new Point(423,136), 11, 9, 8));
        Rows.add(new PIPRow(new Point(428,144), 11, 8, 8));
        Rows.add(new PIPRow(new Point(434,152), 9, 8, 8));
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetInternalLeftPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point(438, 172);
        Point offset = new Point(14, 21);
        for (int i = 0; i < 5; i++) {
            Rows.add(new PIPRow(new Point(start.x,start.y), 3, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        start.setLocation(start.x, start.y+122);
        for (int i = 0; i < 5; i++) {
            Rows.add(new PIPRow(new Point(start.x,start.y), 3, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetInternalRightPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point(481, 172);
        Point offset = new Point(14, 21);
        for (int i = 0; i < 5; i++) {
            Rows.add(new PIPRow(new Point(start.x,start.y), 3, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        start.setLocation(start.x, start.y+122);
        for (int i = 0; i < 5; i++) {
            Rows.add(new PIPRow(new Point(start.x,start.y), 3, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetInternalRearPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Rows.add(new PIPRow(new Point(422, 524), 11, 10, 8));
        Rows.add(new PIPRow(new Point(428, 517), 11, 9, 8));
        Rows.add(new PIPRow(new Point(433, 510), 11, 8, 8));
        Rows.add(new PIPRow(new Point(440, 503), 9, 8, 8));
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetInternalTurretPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point starting = new Point(464, 273);
        Point offset = new Point(7, 8);
        for (int i = 0; i < 7; i++) {
            Rows.add(new PIPRow(new Point(starting.x, starting.y), 4, offset.x, offset.y));
            starting.setLocation(starting.x, starting.y+offset.y);
        }
        Rows.add(new PIPRow(new Point(starting.x+8, starting.y), 2, offset.x, offset.y));
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetInternalTurret2Points() {
        return PIPRow.RenderRows(new ArrayList<PIPRow>());
    }

    public Point[] GetInternalRotorPoints() {
        return PIPRow.RenderRows(new ArrayList<PIPRow>());
    }
    
    //<editor-fold desc="Mech Info">
    public Point[] GetCritHDPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetCritCTPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetCritLTPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetCritRTPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetCritLAPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetCritRAPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetCritLLPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetCritRLPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorHDPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorCTPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorCTRPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorLTPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorLTRPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorRTPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorRTRPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorLAPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorRAPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorLLPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetArmorRLPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalHDPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalCTPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalLTPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalRTPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalLAPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalRAPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalLLPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalRLPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetInternalInfoPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point[] GetHeatSinkPoints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    //</editor-fold>
}

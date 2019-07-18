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

public class TWAdvGroundPoints implements ifPrintPoints {

    //<editor-fold desc="Arrays" default="collapsed">        
    private final static Point[] FrontInternalPoints = {
        new Point( 464, 134 ), new Point( 471, 134 ), new Point( 478, 134 ), new Point( 485, 134 ), new Point( 492, 134 ), 
        new Point( 464, 142 ), new Point( 471, 142 ), new Point( 478, 142 ), new Point( 485, 142 ), new Point( 492, 142 ), 
    };
    
    private final static Point[] TurretArmorPoints = {
                              new Point( 461, 215), new Point( 467, 215), new Point( 473, 215), new Point( 479, 215), new Point( 485, 215),  new Point( 491, 215), new Point( 497, 215),
        new Point( 455, 221), new Point( 461, 221), new Point( 467, 221), new Point( 473, 221), new Point( 479, 221), new Point( 485, 221),  new Point( 491, 221), new Point( 497, 221), new Point( 503, 221), 
        new Point( 455, 227), new Point( 461, 227), new Point( 467, 227), new Point( 473, 227), new Point( 479, 227), new Point( 485, 227),  new Point( 491, 227), new Point( 497, 227), new Point( 503, 227), 
        new Point( 455, 233), new Point( 461, 233), new Point( 467, 233), new Point( 473, 233), new Point( 479, 233), new Point( 485, 233),  new Point( 491, 233), new Point( 497, 233), new Point( 503, 233), 
        new Point( 455, 239), new Point( 461, 239), new Point( 467, 239), new Point( 473, 239), new Point( 479, 239), new Point( 485, 239),  new Point( 491, 239), new Point( 497, 239), new Point( 503, 239), 
        new Point( 455, 245), new Point( 461, 245), new Point( 467, 245), new Point( 473, 245), new Point( 479, 245), new Point( 485, 245),  new Point( 491, 245), new Point( 497, 245), new Point( 503, 245), 
        new Point( 455, 251), new Point( 461, 251), new Point( 467, 251), new Point( 473, 251), new Point( 479, 251), new Point( 485, 251),  new Point( 491, 251), new Point( 497, 251), new Point( 503, 251), 
    };
    
    private final static Point[] TurretInternalPoints = {
        new Point( 467, 200), new Point( 473, 200), new Point( 479, 200), new Point( 485, 200), new Point( 491, 200),
        new Point( 467, 206), new Point( 473, 206), new Point( 479, 206), new Point( 485, 206), new Point( 491, 206),
    };
    
    private final static Point[] Turret2ArmorPoints = {
        new Point( 528, 59 ), new Point( 534, 59 ), new Point( 528, 65 ), new Point( 534, 65 ), 
        new Point( 540, 65 ), new Point( 546, 65 ), new Point( 528, 71 ), new Point( 534, 71 ), 
        new Point( 540, 71 ), new Point( 546, 71 ), new Point( 528, 77 ), new Point( 534, 77 ), 
        new Point( 540, 77 ), new Point( 546, 77 ), new Point( 528, 83 ), new Point( 534, 83 ), 
        new Point( 540, 83 ), new Point( 546, 83 ), new Point( 534, 89 ), new Point( 540, 89 ), 
        new Point( 534, 95 ), new Point( 540, 95 ), new Point( 534, 101 ), new Point( 540, 101 ), 
        new Point( 534, 107 ), new Point( 540, 107 ), new Point( 534, 113 ), new Point( 540, 113 ), 
        new Point( 534, 119 ), new Point( 540, 119 ), new Point( 534, 125 ), new Point( 540, 125 ), 
        new Point( 534, 131 ), new Point( 540, 131 ) 
    };
        
    private final static Point[] Turret2InternalPoints = {
        new Point( 450, 266),
    };
    
    private final static Point[] RotorArmorPoints = {
        new Point( 449, 158 ), new Point( 443, 158 ) 
    };
      
    private final static Point[] RotorInternalPoints = {
        new Point( 450, 266),
    };
//</editor-fold>
    
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
        new Point( 440, 26 ), //TOTAL_ARMOR = 17
        new Point( 122, 708 ),  //STATS = 18;
        new Point( 75, 150 ),      //Movement Type = 19
        new Point( 62, 162 )};     // Engine Type = 20
    
    private final static Point[] ArmorInfo = {
        new Point( 470, 42 ), // Front
        new Point( 386, 172 ), // left
        new Point( 560, 238 ), // right
        new Point( 470, 342 ), // rear
        new Point( 441, 99 ), // turret
        new Point( 458, 185 ), // rear turret
        new Point( 0, 0 ), // rotor
        new Point( 0, 0 ), // body
    };

    public Point[] GetArmorFrontPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point(433,50);
        Point offset = new Point(7,6);
        for (int i = 0; i < 4; i++) {
            Rows.add(new PIPRow(new Point(start.x, start.y), 14, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        for (int i = 0; i < 2; i++) {
            Rows.add(new PIPRow(new Point(start.x+(9*offset.x), start.y), 4, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        for (int i = 0; i < 2; i++) {
            Rows.add(new PIPRow(new Point(start.x+(10*offset.x), start.y), 3, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        Rows.add(new PIPRow(new Point(start.x+(11*offset.x), start.y), 1, offset.x, offset.y));
        start.setLocation(433,50);
        Rows.add(new PIPRow(new Point(start.x-offset.x, start.y-3), 1, offset.x, offset.y));
        Rows.add(new PIPRow(new Point(start.x+(14*offset.x), start.y-3), 1, offset.x, offset.y));
            
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetArmorLeftPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point(421,68);
        Point offset = new Point(7,8);
        //2nd Col from left
        Rows.add(new PIPRow(new Point(start.x, start.y), 31, offset.x, offset.y));
        //3rd Col from left
        Rows.add(new PIPRow(new Point(start.x+offset.x, start.y+(offset.y*3)+1), 17, offset.x, offset.y));
        Rows.add(new PIPRow(new Point(start.x+offset.x, start.y+(offset.y*21)+4), 5, offset.x, offset.y));
        //1st Col
        Rows.add(new PIPRow(new Point(start.x-offset.x, 142), 21, offset.x, offset.y));
        //4th Col
        Rows.add(new PIPRow(new Point(start.x+(offset.x*2), start.y+(offset.y*6)+1), 8, offset.x, offset.y));
        //5th Col
        Rows.add(new PIPRow(new Point(start.x+(offset.x*3), start.y+(offset.y*9)+2), 3, offset.x, offset.y));
        
        return PIPRow.RenderColumns(Rows);
    }

    public Point[] GetArmorRightPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point(538,68);
        Point offset = new Point(7,8);
        //2nd Col from left
        Rows.add(new PIPRow(new Point(start.x, start.y), 31, offset.x, offset.y));
        //3rd Col from left
        Rows.add(new PIPRow(new Point(start.x-offset.x, start.y+(offset.y*3)+1), 17, offset.x, offset.y));
        Rows.add(new PIPRow(new Point(start.x-offset.x, start.y+(offset.y*21)+4), 5, offset.x, offset.y));
        //1st Col
        Rows.add(new PIPRow(new Point(start.x+offset.x, 142), 21, offset.x, offset.y));
        //4th Col
        Rows.add(new PIPRow(new Point(start.x-(offset.x*2), start.y+(offset.y*6)+1), 8, offset.x, offset.y));
        //5th Col
        Rows.add(new PIPRow(new Point(start.x-(offset.x*3), start.y+(offset.y*9)+2), 3, offset.x, offset.y));
        
        return PIPRow.RenderColumns(Rows);
    }

    public Point[] GetArmorRearPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point( 427, 289);
        Point offset = new Point(7,7);
        for (int i = 0; i < 4; i++) {
            Rows.add(new PIPRow(new Point(start.x, start.y), 16, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetArmorTurretPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point( 453, 109);
        Point offset = new Point(6,6);
        Rows.add(new PIPRow(new Point(471, 103), 6, offset.x, offset.y));
        for (int i = 0; i < 4; i++) {
            Rows.add(new PIPRow(new Point(start.x, start.y), 10, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetArmorTurret2Points() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point( 452, 235);
        Point offset = new Point(6,6);
        for (int i = 0; i < 6; i++) {
            Rows.add(new PIPRow(new Point(start.x, start.y), 10, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetArmorRotorPoints() {
        return RotorArmorPoints;
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
        Point start = new Point( 463, 150 );
        Point offset = new Point( 8, 8 );
        Rows.add(new PIPRow(new Point(start.x, start.y), 5, offset.x, offset.y));
        Rows.add(new PIPRow(new Point(start.x, start.y+offset.y), 5, offset.x, offset.y));
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetInternalLeftPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point( 444, 166 );
        Point offset = new Point(-1,7);
        Rows.add(new PIPRow(new Point(start.x, start.y), 10, offset.x, offset.y));
        return PIPRow.Render(Rows);
    }

    public Point[] GetInternalRightPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point( 514, 166 );
        Point offset = new Point(1,7);
        Rows.add(new PIPRow(new Point(start.x, start.y), 10, offset.x, offset.y));
        return PIPRow.Render(Rows);
    }

    public Point[] GetInternalRearPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point( 452, 280 );
        Point offset = new Point( 6, 6 );
        Rows.add(new PIPRow(new Point(start.x, start.y), 10, offset.x, offset.y));
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetInternalTurretPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point( 468, 132);
        Point offset = new Point(6,6);
        for (int i = 0; i < 2; i++) {
            Rows.add(new PIPRow(new Point(start.x, start.y), 5, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetInternalTurret2Points() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point( 467, 209);
        Point offset = new Point(6,6);
        for (int i = 0; i < 2; i++) {
            Rows.add(new PIPRow(new Point(start.x, start.y), 5, offset.x, offset.y));
            start.setLocation(start.x, start.y+offset.y);
        }
        return PIPRow.RenderRows(Rows);
    }

    public Point[] GetInternalRotorPoints() {
        return RotorInternalPoints;
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

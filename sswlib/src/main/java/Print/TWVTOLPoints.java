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

public class TWVTOLPoints implements ifPrintPoints {
    
    private final static Point[] FrontArmorPoints = {
                              new Point( 467, 64 ), new Point( 474, 64 ), new Point( 481, 64 ), new Point( 488, 64 ), 
        new Point( 460, 71 ), new Point( 467, 71 ), new Point( 474, 71 ), new Point( 481, 71 ), new Point( 488, 71 ), new Point( 495, 71 ), 
        new Point( 460, 78 ), new Point( 467, 78 ), new Point( 474, 78 ), new Point( 481, 78 ), new Point( 488, 78 ), new Point( 495, 78 ), 
                              new Point( 467, 85 ), new Point( 474, 85 ), new Point( 481, 85 ), new Point( 488, 85 ), 
                              new Point( 467, 92 ), new Point( 474, 92 ), new Point( 481, 92 ), new Point( 488, 92 ), 
                              new Point( 467, 99 ), new Point( 474, 99 ), new Point( 481, 99 ), new Point( 488, 99 ), 
    };
    
    private final static Point[] FrontInternalPoints = {
        new Point( 477, 107 ), 
        new Point( 477, 115 ), 
        new Point( 477, 123 ), 
    };
            
    private final static Point[] LeftArmorPoints = {
        new Point( 447, 92), //top left set
        new Point( 447, 99), 
        new Point( 447, 106), 
        new Point( 447, 113), 
        new Point( 447, 120),
        new Point( 456, 96), //top right set
        new Point( 456, 102), 
        new Point( 456, 109), 
        new Point( 456, 116), 
        new Point( 456, 123),
        new Point( 458, 147), //bottom set
        new Point( 458, 154), 
        new Point( 458, 161), 
        new Point( 458, 168), 
        new Point( 458, 175), 
        new Point( 458, 182), 
    };
    
    private final static Point[] LeftInternalPoints = {
        new Point( 471, 157), 
        new Point( 471, 169), 
        new Point( 471, 181), 
    };

    private final static Point[] RightArmorPoints = {
        new Point( 507, 92), //top right set
        new Point( 507, 99), 
        new Point( 507, 106), 
        new Point( 507, 113), 
        new Point( 507, 120),         
        new Point( 498, 96), //top left set
        new Point( 498, 102), 
        new Point( 498, 109), 
        new Point( 498, 116), 
        new Point( 498, 123),
        new Point( 496, 147), //bottom set
        new Point( 496, 154), 
        new Point( 496, 161), 
        new Point( 496, 168), 
        new Point( 496, 175), 
        new Point( 496, 182), 
    };
        
    private final static Point[] RightInternalPoints = {
        new Point( 483, 157), 
        new Point( 483, 169), 
        new Point( 483, 181), 
    };
    
    private final static Point[] RearArmorPoints = {
        new Point( 478, 237), 
        new Point( 478, 243), 
        new Point( 478, 249), 
        new Point( 478, 255), 
        new Point( 478, 261), 
        new Point( 478, 267), 
        new Point( 478, 273), 
        new Point( 478, 279), 
        new Point( 478, 285), 
        new Point( 478, 291), 
        new Point( 478, 297), 
    };
    
    private final static Point[] RearInternalPoints = {
        new Point( 478, 207), 
        new Point( 478, 215), 
        new Point( 478, 223), 
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
        new Point( 396, 134 ), new Point( 558, 134 ) 
    };
      
    private final static Point[] RotorInternalPoints = {
        new Point( 477, 134), //center
        new Point( 427, 134), //left
        new Point( 526, 134), //right
    };
    
    private final static Point[] WeaponPoints = {
        new Point( 10, 190 ), //count
        new Point( 28, 190 ), //name
        new Point( 115, 190 ), //loc
        new Point( 110, 190 ), //ht
        new Point( 136, 190 ), //dmg
        new Point( 159, 190 ), //min
        new Point( 174, 190 ), //sht
        new Point( 188, 190 ), //med
        new Point( 206, 190 ) }; //lng
    
    private final static Point[] DataPoints = {
        new Point( 32, 105 ), //MechName = 0
        new Point( 58, 129 ), //WALKMP = 1,
        new Point( 58, 139 ), //RUNMP = 2
        new Point( 58, 149 ), //JUMPMP = 3
        new Point( 165, 119 ), //TONNAGE = 4
        new Point( 199, 131 ), //TECH_CLAN = 5
        new Point( 165, 130 ), //TECH_IS = 6
        new Point( 261, 104 ), //PILOT_NAME = 7
        new Point( 288, 118 ), //PILOT_GUN = 8
        new Point( 358, 118 ), //PILOT_PILOT = 9
        new Point( 35, 351 ), //COST = 10
        new Point( 140, 351 ), //BV2 = 11
        new Point( 497, 592 ), //HEATSINK_NUMBER = 12
        new Point( 511, 592 ), //HEATSINK_DISSIPATION = 13
        new Point( 522, 699 ),
        new Point( 522, 713 ), 
        new Point( 142, 354 ), //MAX_HEAT = 16
        new Point( 440, 28 ), //TOTAL_ARMOR = 17
        new Point( 122, 708 ),  //STATS = 18;
        new Point( 62, 151 ),      //Movement Type = 19
        new Point( 62, 163 )};     // Engine Type = 20
    
    private final static Point[] ArmorInfo = {
        new Point( 468, 48 ), // Front
        new Point( 404, 185 ), // left
        new Point( 538, 240 ), // right
        new Point( 470, 328 ), // rear
        new Point( 458, 172 ), // turret
        new Point( 0, 0 ), // rear turret
        new Point( 535, 125 ), // rotor
        new Point( 0, 0 ), // body
    };

    public Point[] GetArmorFrontPoints() {
        return FrontArmorPoints;
    }

    public Point[] GetArmorLeftPoints() {
        return LeftArmorPoints;
    }

    public Point[] GetArmorRightPoints() {
        return RightArmorPoints;
    }

    public Point[] GetArmorRearPoints() {
        ArrayList<PIPRow> Rows = new ArrayList<PIPRow>();
        Point start = new Point( 478, 237);
        Point offset = new Point(6,6);
        Rows.add(new PIPRow(new Point(start.x, start.y), 11, offset.x, offset.y));
        return PIPRow.RenderColumns(Rows);
    }

    public Point[] GetArmorTurretPoints() {
        return TurretArmorPoints;
    }

    public Point[] GetArmorTurret2Points() {
        return Turret2ArmorPoints;
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
        return FrontInternalPoints;
    }

    public Point[] GetInternalLeftPoints() {
        return LeftInternalPoints;
    }

    public Point[] GetInternalRightPoints() {
        return RightInternalPoints;
    }

    public Point[] GetInternalRearPoints() {
        return RearInternalPoints;
    }

    public Point[] GetInternalTurretPoints() {
        return TurretInternalPoints;
    }

    public Point[] GetInternalTurret2Points() {
        return Turret2InternalPoints;
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

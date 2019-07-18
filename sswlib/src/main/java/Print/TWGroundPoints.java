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

import java.awt.Point;

public class TWGroundPoints implements ifPrintPoints {
    
    private final static Point[] FrontArmorPoints = {
        new Point( 431, 68 ), new Point( 439, 71 ), new Point( 446, 68 ), new Point( 453, 68 ), new Point( 460, 68 ), new Point( 467, 68 ), new Point( 474, 68 ), new Point( 482, 68 ), new Point( 489, 68 ), new Point( 496, 68 ), new Point( 503, 68 ), new Point( 510, 68 ), new Point( 517, 71 ), new Point( 524, 68 ),
        new Point( 431, 75 ), new Point( 439, 78 ), new Point( 446, 75 ), new Point( 453, 75 ), new Point( 460, 75 ), new Point( 467, 75 ), new Point( 474, 75 ), new Point( 482, 75 ), new Point( 489, 75 ), new Point( 496, 75 ), new Point( 503, 75 ), new Point( 510, 75 ), new Point( 517, 78 ), new Point( 524, 75 ),
                              new Point( 439, 85 ), new Point( 446, 82 ), new Point( 453, 82 ), new Point( 460, 82 ), new Point( 467, 82 ), new Point( 474, 82 ), new Point( 482, 82 ), new Point( 489, 82 ), new Point( 496, 82 ), new Point( 503, 82 ), new Point( 510, 82 ), new Point( 517, 85 ),
                                                    new Point( 446, 89 ), new Point( 453, 89 ), new Point( 460, 89 ), new Point( 467, 89 ), new Point( 474, 89 ), new Point( 482, 89 ), new Point( 489, 89 ), new Point( 496, 89 ), new Point( 503, 89 ), new Point( 510, 89 ), 
                                                    new Point( 446, 96 ), new Point( 453, 96 ), new Point( 460, 96 ), new Point( 467, 96 ), new Point( 474, 96 ), new Point( 482, 96 ), new Point( 489, 96 ), new Point( 496, 96 ), new Point( 503, 96 ), new Point( 510, 96 ), 
                                                                          new Point( 453, 103 ), new Point( 460, 103 ), new Point( 467, 103 ), new Point( 474, 103 ), new Point( 482, 103 ), new Point( 489, 103 ), new Point( 496, 103 ), new Point( 503, 103 ), 
                                                                                                new Point( 460, 110 ), new Point( 467, 110 ), new Point( 474, 110 ), new Point( 482, 110 ), new Point( 489, 110 ), new Point( 496, 110 ),
                                                                                                new Point( 460, 117 ), new Point( 467, 117 ), new Point( 474, 117 ), new Point( 482, 117 ), new Point( 489, 117 ), new Point( 496, 117 ),
                                                                                                new Point( 460, 124 ), new Point( 467, 124 ), new Point( 474, 124 ), new Point( 482, 124 ), new Point( 489, 124 ), new Point( 496, 124 )
    };
    
    private final static Point[] FrontInternalPoints = {
        new Point( 464, 134 ), new Point( 471, 134 ), new Point( 478, 134 ), new Point( 485, 134 ), new Point( 492, 134 ), 
        new Point( 464, 142 ), new Point( 471, 142 ), new Point( 478, 142 ), new Point( 485, 142 ), new Point( 492, 142 ), 
    };
            
    private final static Point[] LeftArmorPoints = {
        new Point( 421, 80), 
        new Point( 422, 87), 
        new Point( 423, 94), new Point( 430, 94), 
        new Point( 424, 101), new Point( 431, 101), 
        new Point( 424, 108), new Point( 431, 108), new Point( 438, 108), 
        new Point( 423, 115), new Point( 430, 115), new Point( 437, 115), new Point( 444, 115), 
        new Point( 423, 122), new Point( 430, 122), new Point( 437, 122), new Point( 444, 122), 
        new Point( 416, 129), new Point( 423, 129), new Point( 430, 129), new Point( 437, 129), new Point( 444, 129), 
        new Point( 416, 136), new Point( 423, 136), new Point( 430, 136), new Point( 437, 136), new Point( 444, 136), new Point( 451, 133), 
        new Point( 416, 143), new Point( 423, 143), new Point( 430, 143), 
        new Point( 416, 150), new Point( 423, 150), new Point( 430, 150), 
        new Point( 414, 157), new Point( 421, 157), new Point( 428, 157), 
        new Point( 413, 164), new Point( 420, 164), new Point( 427, 164), 
        new Point( 414, 170), new Point( 421, 170), 
        new Point( 414, 177), new Point( 421, 177), 
        new Point( 412, 184), new Point( 419, 184), 
        new Point( 411, 191), new Point( 418, 191), 
        new Point( 411, 198), new Point( 418, 198), 
        new Point( 412, 205), 
        new Point( 412, 212), 
        new Point( 412, 219), 
        new Point( 412, 226), new Point( 419, 226), new Point( 426, 226), 
        new Point( 412, 233), new Point( 419, 233), new Point( 426, 233), 
        new Point( 412, 240), new Point( 419, 240), new Point( 426, 240), 
        new Point( 412, 247), new Point( 419, 247), new Point( 426, 247), 
        new Point( 412, 254), new Point( 419, 254), 
        new Point( 412, 261), 
    };
    
    private final static Point[] LeftInternalPoints = {
        new Point( 444, 155), new Point( 443, 161), 
        new Point( 442, 166), new Point( 440, 172), 
        new Point( 439, 178), new Point( 438, 184), 
        new Point( 436, 190), new Point( 435, 196), 
        new Point( 433, 202), new Point( 432, 208), 
    };

    private final static Point[] RightArmorPoints = {
        new Point( 535, 80), 
        new Point( 535, 87), 
        new Point( 528, 94), new Point( 535, 94), 
        new Point( 527, 101), new Point( 534, 101),
        new Point( 520, 108), new Point( 527, 108), new Point( 534, 108), 
        new Point( 513, 115), new Point( 520, 115), new Point( 527, 115), new Point( 534, 115), 
        new Point( 513, 122), new Point( 520, 122), new Point( 527, 122), new Point( 534, 122), 
        new Point( 513, 129), new Point( 520, 129), new Point( 527, 129), new Point( 534, 129), new Point( 541, 129), 
        new Point( 506, 133), new Point( 513, 136), new Point( 520, 136), new Point( 527, 136), new Point( 534, 136), new Point( 541, 136), 
        new Point( 527, 143), new Point( 534, 143), new Point( 541, 143), 
        new Point( 527, 150), new Point( 534, 150), new Point( 541, 150), 
        new Point( 529, 157), new Point( 536, 157), new Point( 543, 157), 
        new Point( 530, 164), new Point( 537, 164), new Point( 544, 164), 
        new Point( 534, 170), new Point( 541, 170), 
        new Point( 534, 177), new Point( 541, 177), 
        new Point( 536, 184), new Point( 543, 184), 
        new Point( 537, 191), new Point( 544, 191), 
        new Point( 538, 198), new Point( 545, 198), 
        new Point( 544, 205), 
        new Point( 544, 212), 
        new Point( 544, 219), 
        new Point( 530, 226), new Point( 537, 226), new Point( 544, 226),
        new Point( 530, 233), new Point( 537, 233), new Point( 544, 233),
        new Point( 530, 240), new Point( 537, 240), new Point( 544, 240),
        new Point( 530, 247), new Point( 537, 247), new Point( 544, 247),
        new Point( 537, 254), new Point( 544, 254), 
        new Point( 544, 261), 
    };
        
    private final static Point[] RightInternalPoints = {
        new Point( 515, 155), new Point( 516, 161), 
        new Point( 517, 166), new Point( 518, 172), 
        new Point( 520, 178), new Point( 521, 184), 
        new Point( 523, 190), new Point( 524, 196), 
        new Point( 526, 202), new Point( 527, 208), 
    };
    
    private final static Point[] RearArmorPoints = {
        new Point( 425, 276), new Point( 432, 276), new Point( 439, 276), new Point( 446, 276), new Point( 453, 276), new Point( 460, 276), new Point( 467, 276), new Point( 474, 276), new Point( 481, 276), new Point( 488, 276), new Point( 495, 276), new Point( 502, 276), new Point( 509, 276), new Point( 516, 276), new Point( 523, 276), new Point( 530, 276), new Point( 537, 276), 
        new Point( 425, 283), new Point( 432, 283), new Point( 439, 283), new Point( 446, 283), new Point( 453, 283), new Point( 460, 283), new Point( 467, 283), new Point( 474, 283), new Point( 481, 283), new Point( 488, 283), new Point( 495, 283), new Point( 502, 283), new Point( 509, 283), new Point( 516, 283), new Point( 523, 283), new Point( 530, 283), new Point( 537, 283), 
        new Point( 425, 290), new Point( 432, 290), new Point( 439, 290), new Point( 446, 290), new Point( 453, 290), new Point( 460, 290), new Point( 467, 290), new Point( 474, 290), new Point( 481, 290), new Point( 488, 290), new Point( 495, 290), new Point( 502, 290), new Point( 509, 290), new Point( 516, 290), new Point( 523, 290), new Point( 530, 290), new Point( 537, 290), 
        new Point( 418, 278), new Point( 544, 278), 
        new Point( 418, 285), new Point( 544, 285), 
        new Point( 418, 292), new Point( 544, 292), 
    };
    
    private final static Point[] RearInternalPoints = {
        new Point( 452, 265), new Point( 458, 265), new Point( 464, 265), new Point( 470, 265), new Point( 476, 265), 
        new Point( 483, 265), new Point( 489, 265), new Point( 495, 265), new Point( 501, 265), new Point( 506, 265),
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
        new Point( 440, 28 ), //TOTAL_ARMOR = 17
        new Point( 122, 708 ),  //STATS = 18;
        new Point( 75, 150 ),      //Movement Type = 19
        new Point( 62, 162 )};     // Engine Type = 20
    
    private final static Point[] ArmorInfo = {
        new Point( 468, 48 ), // Front
        new Point( 388, 162 ), // left
        new Point( 560, 218 ), // right
        new Point( 470, 334 ), // rear
        new Point( 458, 172 ), // turret
        new Point( 0, 0 ), // rear turret
        new Point( 0, 0 ), // rotor
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
        return RearArmorPoints;
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
        return new Point( 231, 262 );
    }

    public Point GetImageBounds() {
        return new Point( 170, 109 );
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

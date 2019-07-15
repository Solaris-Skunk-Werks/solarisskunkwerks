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

package states;

import common.CommonTools;
import components.AvailableCode;
import components.Mech;
import components.MechModifier;

public class stEngineISCF implements ifEngine, ifState {
    // An Inner Sphere Compact Fusion Engine
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private final static double[] Masses = {1.0,1.0,1.0,1.0,1.5,1.5,1.5,
        1.5,2.5,2.5,2.5,3.0,3.0,3.0,4.0,4.0,4.5,4.5,4.5,5.5,5.5,
        6.0,6.0,6.0,7.0,7.0,7.5,7.5,8.5,8.5,9.0,9.0,9.0,10.5,10.5,
        11.5,11.5,12.0,13.0,13.0,13.5,14.5,15.0,15.0,16.0,16.5,17.5,
        18.0,19.0,19.5,20.5,21.0,22.0,23.5,24.0,25.0,26.5,27.0,28.5,
        29.5,31.0,32.5,34.0,35.5,37.0,38.5,40.5,43.0,44.5,47.5,49.5,
        52.0,55.0,58.0,61.5,65.5,69.0,73.5,79.0};
    private final static int[] BFStructure = {1,2,2,3,3,4,4,5,5,6,7,7,7,8,8,9,10,10,10};

    public stEngineISCF() {
        AC.SetISCodes( 'E', 'X', 'X', 'E' );
        AC.SetISDates( 0, 0, false, 3068, 0, 0, false, false );
        AC.SetISFactions( "", "", "LA", "" );
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_ADVANCED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public double GetTonnage( int Rating, boolean fractional ) {
        if( fractional ) {
            return CommonTools.RoundFractionalTons( stEngineFusion.Masses[GetIndex( Rating )] * 1.5 );
//            return Math.ceil( stEngineFusion.Masses[GetIndex( Rating )] * 1500 ) * 0.001;
        } else {
            return Masses[GetIndex( Rating )];
        }
    }
    
    public int GetCTCrits() {
        return 3;
    }
    
    public int GetSideTorsoCrits() {
        return 0;
    }
    
    public int NumCTBlocks() {
        return 1;
    }
    
    public int NumCVSpaces() {
        return -1;
    }

    public int LargeCVSpaces() {
        return 0;
    }

    public boolean CanSupportRating( int rate, Mech m ) {
        if( rate < 5 || rate > 400 || rate % 5 != 0 ) {
            return false;
        } else {
            return true;
        }
    }

    public String ActualName() {
        return "Compact Fusion Engine";
    }

    public String CritName() {
        return "Compact Fusion Engine";
    }

    public String LookupName() {
        return "Compact Fusion Engine";
    }

    public String ChatName() {
        return "CFE";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Fusion Engine";
    }

    public String BookReference() {
        return "Tech Manual";
    }

    public double GetCost( int MechTonnage, int Rating ) {
        return ( 10000.0f * ((double) MechTonnage) * ((double) Rating )) / 75.0f;
    }
    
    public AvailableCode GetAvailability() {
        return AC;
    }
    
    public int FreeHeatSinks() {
        return 10;
    }

    public double GetBVMult() {
        return 1.0f;
    }
    
    public boolean IsFusion() {
        return true;
    }

    public boolean IsNuclear() {
        return true;
    }

    public int GetFullCrits() {
        return 3;
    }

    private int GetIndex( int Rating ) {
        return Math.round(Rating / 5) - 2;
    }

    private int GetBFIndex( int tonnage ) {
        return (tonnage - 10) / 5;
    }

    public int GetBFStructure( int tonnage ) {
        return BFStructure[GetBFIndex(tonnage)];
    }

    public int MaxMovementHeat() {
        return 2;
    }

    public int MinimumHeat() {
        return 0;
    }

    public int JumpingHeatMultiplier() {
        return 1;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    public boolean IsPrimitive() {
        return false;
    }

    @Override
    public String toString() {
        return "Compact Fusion Engine";
    }
}

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
import components.Engine;
import components.Mech;
import components.MechModifier;

public class stEngineISXL implements ifEngine, ifState {
    // An Inner Sphere XL Fusion Engine
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE ),
                                       LARGE_AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private final static double[] Masses = {0.5,0.5,0.5,0.5,0.5,0.5,0.5,
        0.5,1.0,1.0,1.0,1.0,1.0,1.0,1.5,1.5,1.5,1.5,1.5,2.0,2.0,
        2.0,2.0,2.0,2.5,2.5,2.5,2.5,3.0,3.0,3.0,3.0,3.0,3.5,3.5,
        4.0,4.0,4.0,4.5,4.5,4.5,5.0,5.0,5.0,5.5,5.5,6.0,6.0,6.5,
        6.5,7.0,7.0,7.5,8.0,8.0,8.5,9.0,9.0,9.5,10.0,10.5,11.0,
        11.5,12.0,12.5,13.0,13.5,14.5,15.0,16.0,16.5,17.5,18.5,19.5,
        20.5,22.0,23.0,24.5,26.5,28.5,30.5,33.5,36.5,40.0,44.0,48.5,
        54.0,60.0,67.0,75.0,84.5,95.0,107.5,121.5,138.0,156.5,178.0,
        203.0,231.5 };
    private final static int[] BFStructure = {1,1,1,1,1,2,2,2,2,3,3,3,3,3,4,4,4,4,4};
    private Engine Owner;

    public stEngineISXL( Engine e ) {
        AC.SetISCodes( 'E', 'D', 'F', 'E' );
        AC.SetISDates( 0, 0, false, 2579, 2865, 3035, true, true );
        AC.SetISFactions( "", "", "TH", "LC" );
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_ADVANCED );
        LARGE_AC.SetISCodes( 'E', 'D', 'F', 'E' );
        LARGE_AC.SetISDates( 2579, 2630, true, 2630, 2865, 3035, true, true );
        LARGE_AC.SetISFactions( "TH", "TH", "TH", "LC" );
        LARGE_AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL );
        Owner = e;
    }

    public boolean HasCounterpart() {
        return true;
    }

    public double GetTonnage( int Rating, boolean fractional ) {
        if( fractional ) {
            double retval = CommonTools.RoundFractionalTons( stEngineFusion.Masses[GetIndex( Rating )] * 0.5 );
//            double retval = Math.ceil( stEngineFusion.Masses[GetIndex( Rating )] * 500 ) * 0.001;
            if( retval < 0.25 ) { return 0.25; }
            return retval;
        } else {
            return Masses[GetIndex( Rating )];
        }
    }
    
    public int GetCTCrits() {
        return 3;
    }
    
    public int GetSideTorsoCrits() {
        return 3;
    }
    
    public int NumCTBlocks() {
        return 2;
    }

    public int NumCVSpaces() {
        return 2;
    }

    public int LargeCVSpaces() {
        return 3;
    }

    public boolean CanSupportRating( int rate, Mech m ) {
        if( CommonTools.IsAllowed( LARGE_AC, m ) ) {
            if( rate < 5 || rate > 500 || rate % 5 != 0 ) {
                return false;
            } else {
                return true;
            }
        } else {
            if( rate < 5 || rate > 400 || rate % 5 != 0 ) {
                return false;
            } else {
                return true;
            }
        }
    }

    public String ActualName() {
        return "Extra-Light Fusion Engine";
    }

    public String CritName() {
        return "XL Fusion Engine";
    }

    public String LookupName() {
        return "XL Engine";
    }

    public String ChatName() {
        return "XLFE";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Fusion Engine";
    }

    public String BookReference() {
        return "Tech Manual";
    }

    public double GetCost( int MechTonnage, int Rating ) {
        double baseCost = 20000.0f;
        if ( Rating > 400 ) baseCost *= 2;
        return ( baseCost * ((double) MechTonnage) * ((double) Rating )) / 75.0f;
    }
    
    public AvailableCode GetAvailability() {
        if( Owner.GetRating() > 400 ) { return LARGE_AC; }
        return AC;
    }

    public int FreeHeatSinks() {
        return 10;
    }

    public double GetBVMult() {
        return 0.5f;
    }
    
    public boolean IsFusion() {
        return true;
    }

    public boolean IsNuclear() {
        return true;
    }

    public int GetFullCrits() {
        return 12;
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
        return "Fusion XL Engine";
    }
}

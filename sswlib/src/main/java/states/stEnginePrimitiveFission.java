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

public class stEnginePrimitiveFission implements ifEngine, ifState {
    // An Inner Sphere Fission Engine
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private final static double[] Masses = {5.0,5.0,5.0,5.0,5.0,5.0,5.0,
        5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.5,5.5,5.5,6.5,6.5,
        7.0,7.0,7.0,8.0,8.0,9.0,9.0,10.0,10.0,10.5,10.5,10.5,12.5,
        12.5,13.5,13.5,14.0,15.0,15.0,16.0,17.0,17.5,17.5,18.5,19.5,
        20.5,21.0,22.0,23.0,24.0,24.5,25.5,27.5,28.0,29.0,31.0,31.5,
        33.5,34.5,36.0,38.0,39.5,41.5,43.0,45.0,47.5,50.0,52.0,55.5,
        58.0,60.5,64.0,67.5,72.0,76.5,80.5,86.0,92.0};
    private final static int[] BFStructure = {1,1,2,2,3,3,3,4,4,5,5,5,6,6,6,7,7,8,8};

    public stEnginePrimitiveFission() {
        AC.SetISCodes( 'D', 'E', 'E', 'D' );
        AC.SetISDates( 0, 0, false, 1960, 0, 0, false, false );
        AC.SetISFactions( "", "", "ES", "" );
        AC.SetCLCodes( 'D', 'X', 'E', 'D' );
        AC.SetCLDates( 0, 0, false, 1960, 0, 0, false, false );
        AC.SetCLFactions( "", "", "ES", "" );
        AC.SetPIMAllowed( true );
        AC.SetPrimitiveOnly( true );
        AC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_ADVANCED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public double GetTonnage( int Rating, boolean fractional ) {
        if( fractional ) {
            double retval = CommonTools.RoundFractionalTons( stEngineFusion.Masses[GetIndex( Rating )] * 1.75 );
//            double retval = Math.ceil( stEngineFusion.Masses[GetIndex( Rating )] * 1750 ) * 0.001;
            if( retval < 5.0 ) { return 5.0; }
            return retval;
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
        return 2;
    }

    public int NumCVSpaces() {
        return 0;
    }

    public int LargeCVSpaces() {
        return 0;
    }

    public boolean CanSupportRating( int rate, Mech m ) {
        rate = (int) ( Math.floor( ( ( rate * 1.2f ) + 4.5f ) / 5 ) * 5 );
        if( rate < 5 || rate > 400 || rate % 5 != 0 ) {
            return false;
        } else {
            return true;
        }
    }

    public String ActualName() {
        return "Primitive Fission Engine";
    }

    public String CritName() {
        return "Primitive Fission Engine";
    }

    public String LookupName() {
        return "Primitive Fission Engine";
    }

    public String ChatName() {
        return "Pr FIE";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Primitive Fission Engine";
    }

    public String BookReference() {
        return "Jihad Secrets: The Blake Documents";
    }

    public double GetCost( int MechTonnage, int Rating ) {
        return ( 7500.0f * ((double) MechTonnage) * ((double) Rating )) / 75.0f;
    }
    
    public AvailableCode GetAvailability() {
        return AC;
    }
    
    public int FreeHeatSinks() {
        return 5;
    }

    public double GetBVMult() {
        return 1.0f;
    }
    
    public boolean IsFusion() {
        return false;
    }

    public boolean IsNuclear() {
        return true;
    }

    public int GetFullCrits() {
        return 6;
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
        return true;
    }

    @Override
    public String toString() {
        return "Primitive Fission Engine";
    }
}

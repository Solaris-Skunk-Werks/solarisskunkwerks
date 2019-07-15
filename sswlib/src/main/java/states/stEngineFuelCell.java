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

public class stEngineFuelCell implements ifEngine, ifState {
    // An Inner Sphere Fuel Cell Engine
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private final static double[] Masses = {1.0,1.0,1.0,1.0,1.5,1.5,1.5,
        1.5,2.0,2.0,2.0,2.5,2.5,2.5,3.0,3.0,4.0,4.0,4.0,4.5,4.5,
        5.0,5.0,5.0,5.5,5.5,6.0,6.0,7.0,7.0,7.5,7.5,7.5,8.5,8.5,
        9.0,9.0,10.0,10.5,10.5,11.0,11.5,12.0,12.0,13.0,13.5,14.0,
        14.5,15.0,16.0,16.5,17.0,17.5,19.0,19.5,20.0,21.0,22.0,23.0,
        23.5,25.0,26.0,27.0,28.5,29.5,31.0,32.5,34.5,35.5,38.0,40.0,
        41.5,44.0,46.5,49.5,52.5,55.5,59.0,63.0};
    private final static int[] BFStructure = {1,1,2,2,3,3,3,4,4,5,5,5,6,6,6,7,7,8,8};

    public stEngineFuelCell() {
        AC.SetISCodes( 'D', 'C', 'D', 'D' );
        AC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetISFactions( "", "", "PS", "" );
        AC.SetCLCodes( 'D', 'X', 'C', 'D' );
        AC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetCLFactions( "", "", "PS", "" );
        AC.SetSuperHeavyCompatible( false );
        AC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public double GetTonnage( int Rating, boolean fractional ) {
        if( fractional ) {
            return CommonTools.RoundFractionalTons( stEngineFusion.Masses[GetIndex( Rating )] * 1.2 );
//            return Math.ceil( stEngineFusion.Masses[GetIndex( Rating )] * 1200 ) * 0.001;
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
        if( rate < 5 || rate > 400 || rate % 5 != 0 ) {
            return false;
        } else {
            return true;
        }
    }

    public String ActualName() {
        return "Fuel-Cell Engine";
    }

    public String CritName() {
        return "Fuel-Cell Engine";
    }

    public String LookupName() {
        return "Fuel-Cell Engine";
    }

    public String ChatName() {
        return "FCE";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Fuel-Cell";
    }

    public String BookReference() {
        return "Tech Manual";
    }

    public double GetCost( int MechTonnage, int Rating ) {
        return ( 3500.0f * ((double) MechTonnage) * ((double) Rating )) / 75.0f;
    }
    
    public AvailableCode GetAvailability() {
        return AC;
    }

    public int FreeHeatSinks() {
        return 1;
    }

    public double GetBVMult() {
        return 1.0f;
    }
    
    public boolean IsFusion() {
        return false;
    }

    public boolean IsNuclear() {
        return false;
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
        return 0;
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
        return "Fuel Cell Engine";
    }
}

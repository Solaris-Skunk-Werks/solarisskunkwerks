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

public class stEngineFusion implements ifEngine, ifState {
    // An Inner Sphere Fusion Engine
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH ),
                                       LARGE_AC = new AvailableCode( AvailableCode.TECH_BOTH );
    public final static double[] Masses = { 0.5,0.5,0.5,0.5,1.0,1.0,1.0,
        1.0,1.5,1.5,1.5,2.0,2.0,2.0,2.5,2.5,3.0,3.0,3.0,3.5,3.5,
        4.0,4.0,4.0,4.5,4.5,5.0,5.0,5.5,5.5,6.0,6.0,6.0,7.0,7.0,
        7.5,7.5,8.0,8.5,8.5,9.0,9.5,10.0,10.0,10.5,11.0,11.5,12.0,
        12.5,13.0,13.5,14.0,14.5,15.5,16.0,16.5,17.5,18.0,19.0,19.5,
        20.5,21.5,22.5,23.5,24.5,25.5,27.0,28.5,29.5,31.5,33.0,34.5,
        36.5,38.5,41.0,43.5,46.0,49.0,52.5,56.5,61.0,66.5,72.5,79.5,
        87.5,97.0,107.5,119.5,133.5,150.0,168.5,190.0,214.5,243.0,
        275.5,313.0,356.0,405.5,462.5};
                                           /*|----------- NORMAL WEIGHT -----------|---------SUPER HEAVY WEIGHT ---------|*/
    private final static int[] BFStructure = {1,1,2,2,3,3,3,4,4,5,5,5,6,6,6,7,7,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8};
    private Engine Owner;
    
    public stEngineFusion( Engine e ) {
        AC.SetISCodes( 'D', 'C', 'E', 'D' );
        AC.SetISDates( 0, 0, false, 2021, 0, 0, false, false );
        AC.SetISFactions( "", "", "WA", "" );
        AC.SetCLCodes( 'D', 'X', 'B', 'C' );
        AC.SetCLDates( 0, 0, false, 2021, 0, 0, false, false );
        AC.SetCLFactions( "", "", "WA", "" );
        AC.SetRulesLevels( AvailableCode.RULES_INTRODUCTORY, AvailableCode.RULES_INTRODUCTORY, AvailableCode.RULES_INTRODUCTORY, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT );
        LARGE_AC.SetISCodes( 'D', 'C', 'E', 'D' );
        LARGE_AC.SetISDates( 2550, 2630, true, 2630, 0, 0, false, false );
        LARGE_AC.SetISFactions( "WA", "TH", "", "" );
        LARGE_AC.SetCLCodes( 'D', 'X', 'B', 'C' );
        LARGE_AC.SetCLDates( 2550, 2630, true, 2630, 0, 0, false, false );
        LARGE_AC.SetCLFactions( "WA", "TH", "", "" );
        LARGE_AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL );
        Owner = e;
    }

    public boolean HasCounterpart() {
        return false;
    }

    public double GetTonnage( int Rating, boolean fractional ) {
        return Masses[GetIndex( Rating )];
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
        return 1;
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
        return "Fusion Engine";
    }

    public String CritName() {
        return "Fusion Engine";
    }

    public String LookupName() {
        return "Fusion Engine";
    }

    public String ChatName() {
        return "Std FE";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Fusion Engine";
    }

    public String BookReference() {
        return "Tech Manual";
    }

    public double GetCost( int MechTonnage, int Rating ) {
        double baseCost = 5000.0f;
        if ( Rating > 400 ) baseCost *= 2;
        return ( baseCost * (double) MechTonnage * (double) Rating ) / 75.0;
    }

    public AvailableCode GetAvailability() {
        if( Owner.GetRating() > 400 ) { return LARGE_AC; }
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
        return 6;
    }

    private int GetIndex( int Rating ) {
        return Math.round(Rating / 5) - 2;
    }

    private int GetBFIndex( int tonnage ) {
        return Math.abs((tonnage - 10) / 5);
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
        return "Fusion Engine";
    }
}

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

public class stEngineNone implements ifEngine, ifState {
    // An Inner Sphere I.C.E. Engine
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH ),
                                       LARGE_AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private Engine Owner;

    public stEngineNone( Engine e ) {
        AC.SetISCodes( 'C', 'A', 'A', 'A' );
        AC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetISFactions( "", "", "PS", "" );
        AC.SetCLCodes( 'C', 'X', 'A', 'A' );
        AC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetCLFactions( "", "", "PS", "" );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_INTRODUCTORY, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        LARGE_AC.SetISCodes( 'C', 'A', 'A', 'A' );
        LARGE_AC.SetISDates( 2550, 2630, true, 2630, 0, 0, false, false );
        LARGE_AC.SetISFactions( "PS", "PS", "", "" );
        LARGE_AC.SetCLCodes( 'C', 'X', 'A', 'A' );
        LARGE_AC.SetCLDates( 2550, 2630, true, 2630, 0, 0, false, false );
        LARGE_AC.SetCLFactions( "PS", "PS", "", "" );
        LARGE_AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Owner = e;
    }

    public boolean HasCounterpart() {
        return false;
    }

    public double GetTonnage( int Rating, boolean fractional ) {
        return 0;
    }
    
    public int GetCTCrits() {
        return 0;
    }
    
    public int GetSideTorsoCrits() {
        return 0;
    }
    
    public int NumCTBlocks() {
        return 0;
    }

    public int NumCVSpaces() {
        return 0;
    }

    public int LargeCVSpaces() {
        return 0;
    }

    public boolean CanSupportRating( int rate, Mech m ) {
        return false;
    }

    public String ActualName() {
        return "No Engine";
    }

    public String CritName() {
        return "No Engine";
    }

    public String LookupName() {
        return "No Engine";
    }

    public String ChatName() {
        return "NOE";
    }

    public String MegaMekName( boolean UseRear ) {
        return "";
    }

    public String BookReference() {
        return "Tech Manual";
    }

    public double GetCost( int MechTonnage, int Rating ) {
        return 0;
    }
    
    public AvailableCode GetAvailability() {
        if( Owner.GetRating() > 400 ) { return LARGE_AC; }
        return AC;
    }

    public int FreeHeatSinks() {
        return 0;
    }

    public double GetBVMult() {
        return 0.0f;
    }
    
    public boolean IsFusion() {
        return false;
    }

    public boolean IsNuclear() {
        return false;
    }

    public int GetFullCrits() {
        return 0;
    }

    private int GetIndex( int Rating ) {
        return 0;
    }

    private int GetBFIndex( int tonnage ) {
        return 0;
    }

    public int GetBFStructure( int tonnage ) {
        return 0;
    }
    
    public int MaxMovementHeat() {
        return 0;
    }

    public int MinimumHeat() {
        return 0;
    }

    public int JumpingHeatMultiplier() {
        return 0;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    public boolean IsPrimitive() {
        return false;
    }

    @Override
    public String toString() {
        return "No Engine";
    }
}

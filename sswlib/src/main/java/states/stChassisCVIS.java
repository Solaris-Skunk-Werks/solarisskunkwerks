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
import components.MechModifier;

public class stChassisCVIS implements ifChassis, ifState {
    // Combat Vehicle Internal Structure
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );

    public stChassisCVIS() {
        AC.SetISCodes( 'D', 'C', 'C', 'C' );
        AC.SetISDates( 0, 0, false, 2470, 0, 0, false, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetCLCodes( 'D', 'X', 'B', 'B' );
        AC.SetCLDates( 0, 0, false, 2470, 0, 0, false, false );
        AC.SetCLFactions( "", "", "TH", "" );
        AC.SetRulesLevels( AvailableCode.RULES_INTRODUCTORY, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public int GetCrits() {
        return 0;
    }
    
    public String ActualName() {
        return "Combat Vehicle Structure";
    }

    public String CritName() {
        return "Standard";
    }

    public String LookupName() {
        return "Standard Structure";
    }

    public String ChatName() {
        return "Std";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Standard";
    }

    public String BookReference() {
        return "Tech Manual";
    }

    public double GetStrucTon( int Tonnage, boolean fractional ) {
        if ( !fractional )
            return CommonTools.RoundHalfUp((double)Tonnage / 10.0);
        else
            return (double)Tonnage / 10.0;
    }
    
    public boolean IsQuad() {
        return false;
    }
    
    /**
     * Determines if this Chassis is a Tripod
     * @return True if this chassis is a Tripod
     */
    public boolean IsTripod() {
        return false;
    }
    
    public int GetHeadPoints() {
        // All mech heads have 3 internal structure points
        return 0;
    }

    public int GetCTPoints( int Tonnage ) {
        return 0;
    }

    public int GetSidePoints( int Tonnage ) {
        return 0;
    }
    
    public int GetArmPoints( int Tonnage ) {
        return 0;
    }

    public int GetLegPoints( int Tonnage ) {
        return 0;
    }
    
    public double GetCost( int Tonnage ) {
        return 10000 * GetStrucTon( Tonnage, false );
    }
    
    public double GetBVMult() {
        return 1.0f;
    }
    
    public boolean IncrementPlaced() {
        return false;
    }

    public boolean DecrementPlaced() {
        return false;
    }

    public AvailableCode GetAvailability() {
        return AC;
    }

    private int GetIndex( int Tonnage ) {
        return Tonnage / 5 - 2;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    // toString
    @Override
    public String toString() {
        return "Standard";
    }

    public int GetCVPoints( int Tonnage ) {
        return (int)Math.ceil(Tonnage / 10.0);
    }
}

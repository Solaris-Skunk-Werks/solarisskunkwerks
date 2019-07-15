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

package components;

public class CASEII extends abPlaceable {
    // A simple class for CASE II.
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private boolean Clan;

    public CASEII( boolean clan ) {
        AC.SetISCodes( 'E', 'X', 'X', 'F' );
        AC.SetISDates( 3057, 3064, true, 3064, 0, 0, false, false );
        AC.SetISFactions( "FW", "FW", "", "" );
        AC.SetCLCodes( 'F', 'X', 'X', 'F' );
        AC.SetCLDates( 3059, 3062, true, 3062, 0, 0, false, false );
        AC.SetCLFactions( "CCY", "CCY", "", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Clan = clan;
        SetBattleForceAbilities( new String[]{ "CASEII" } );
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    @Override
    public boolean CanArmor() {
        return false;
    }

    public String ActualName() {
        return "Cellular Ammunition Storage Equipment II";
    }

    public String CritName() {
        return "CASE II";
    }

    public String LookupName() {
        if( Clan ) {
            return "(CL) CASE II";
        } else {
            return "(IS) CASE II";
        }
    }

    public String ChatName() {
        return "CASE2";
    }

    public String MegaMekName( boolean UseRear ) {
        if( Clan ) {
            return "CLCASEII";
        } else {
            return "ISCASEII";
        }
    }

    public String BookReference() {
        return "Tactical Operations";
    }

    public void SetClan( boolean b ) {
        Clan = b;
    }

    public boolean IsClan() {
        return Clan;
    }

    public int NumCrits() {
        return 1;
    }

    @Override
    public int NumCVSpaces() {
        return 0;
    }

    public double GetTonnage() {
        if( Clan ) {
            return 0.5;
        } else {
            return 1.0;
        }
    }

    public double GetOffensiveBV() {
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    public double GetDefensiveBV() {
        return 0.0;
    }

    public double GetCost() {
        return 175000.0;
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public boolean IsCritable() {
        return false;
    }

    // All placeables should be able to return their AvailabileCode
    public AvailableCode GetAvailability() {
        return AC;
    }

    @Override
    public String toString() {
        return "C.A.S.E. II";
    }
}

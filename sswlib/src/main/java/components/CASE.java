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

public class CASE extends abPlaceable {
    // A simple class for Inner Sphere CASE.
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private boolean IsClan = false;

    public CASE() {
        AC.SetISCodes( 'D', 'C', 'F', 'D' );
        AC.SetISDates( 0, 0, false, 2476, 2840, 3036, true, true );
        AC.SetISFactions( "", "", "TH", "DC" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        SetBattleForceAbilities( new String[] { "CASE" } );
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
        return "Cellular Ammunition Storage Equipment";
    }

    public String CritName() {
        return "CASE";
    }

    public String LookupName() {
        return "CASE";
    }

    public String ChatName() {
        return "CASE";
    }

    public String MegaMekName( boolean UseRear ) {
        return "ISCASE";
    }

    public String BookReference() {
        return "Tech Manual";
    }

    public int NumCrits() {
        return 1;
    }

    @Override
    public int NumCVSpaces() {
        if ( IsClan ) return 0;
        return 1;
    }

    public double GetTonnage() {
        if ( IsClan ) return 0;
        return 0.5;
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
        return 50000.0;
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
        return "C.A.S.E.";
    }
    
    public void SetClan(boolean b ) {
        IsClan = b;
    }
}

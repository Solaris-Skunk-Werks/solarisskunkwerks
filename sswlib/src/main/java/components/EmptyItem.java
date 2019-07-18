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

public class EmptyItem extends abPlaceable {
    // this is an empty item.  it takes up space in the loadout
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );

    public EmptyItem() {
        AC.SetISCodes( 'A', 'A', 'A', 'A' );
        AC.SetISDates( 0, 0, false, 1900, 0, 0, false, false );
        AC.SetISFactions( "", "", "", "" );
        AC.SetCLCodes( 'A', 'A', 'A', 'A' );
        AC.SetCLDates( 0, 0, false, 1900, 0, 0, false, false );
        AC.SetCLFactions( "", "", "", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    @Override
    public boolean LocationLocked() {
        return false;
    }

    public String ActualName() {
        return "An empty critical slot.";
    }

    public String CritName() {
        return "Roll Again";
    }

    public String LookupName() {
        return "Roll Again";
    }

    public String ChatName() {
        return "";
    }

    public String MegaMekName( boolean UseRear ) {
        return "-Empty-";
    }

    public String BookReference() {
        return "Tech Manual";
    }

    // returns the number of crits this item takes in the Loadout.
    public int NumCrits() {
        return 1;
    }

    public int NumCVSpaces() {
        return 0;
    }

    @Override
    public boolean CanArmor() {
        // empty items can never be armored
        return false;
    }

    public double GetTonnage() {
        return 0.0;
    }

    public double GetOffensiveBV() {
        // small items such as these do not have battle balues
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        // BV will not change for this item, so just return the normal value
        return 0.0;
    }

    public double GetDefensiveBV() {
        return 0.0;
    }

    public double GetCost() {
        return 0.0;
    }

    // All placeables should be able to return their AvailabileCode
    public AvailableCode GetAvailability() {
        return AC;
    }

    @Override
    public boolean CoreComponent() {
        // simple placeables should be considered core components
        return true;
    }

    @Override
    public boolean IsCritable() {
        return false;
    }

    @Override
    public String toString() {
        return "- roll again";
    }
}

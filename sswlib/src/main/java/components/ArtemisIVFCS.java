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

public class ArtemisIVFCS extends abPlaceable implements ifMissileGuidance {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private RangedWeapon Owner;

    public ArtemisIVFCS( RangedWeapon m ) {
        AC.SetISCodes( 'E', 'E', 'F', 'D' );
        AC.SetISDates( 0, 0, false, 2598, 2855, 3035, true, true );
        AC.SetISFactions( "", "", "TH", "FW" );
        AC.SetCLCodes( 'E', 'X', 'D', 'C' );
        AC.SetCLDates( 0, 0, false, 2598, 0, 0, false, false );
        AC.SetCLFactions( "", "", "TH", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT );
        Owner = m;
    }

    public RangedWeapon GetOwner() {
        return Owner;
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    @Override
    public boolean LocationLinked() {
        return true;
    }

    public String ActualName() {
        return "Artemis IV FCS";
    }

    public String CritName() {
        return "Artemis IV FCS";
    }

    public String LookupName() {
        return "Artemis IV FCS";
    }

    public String ChatName() {
        return "A-IV";
    }

    public String MegaMekName( boolean UseRear ) {
        if( Owner.GetAvailability().GetTechBase() >= AvailableCode.TECH_CLAN ) {
            return "CLArtemisIV";
        } else {
            return "ISArtemisIV";
        }
    }

    public String BookReference() {
        return "Tech Manual";
    }

    public int NumCrits() {
        return 1;
    }

    @Override
    public int NumCVSpaces() {
        return 0;
    }

    public double GetTonnage() {
        if( IsArmored() ) {
            return 1.5;
        } else {
            return 1.0;
        }
    }

    public double GetOffensiveBV() {
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return GetOffensiveBV();
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    public double GetDefensiveBV() {
        return 0.0;
    }

    public double GetCost() {
        if( IsArmored() ) {
            return 250000.0;
        } else {
            return 100000.0;
        }
    }

    public AvailableCode GetAvailability() {
        if( IsArmored() ) {
            return ArmoredAC;
        } else {
            return AC;
        }
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public String toString() {
        return "Artemis IV FCS";
    }

    public int GetAIVClass() {
        return ifMissileGuidance.FCS_ArtemisIV;
    }

    public int GetToHitShort() {
        return 0;
    }

    public int GetToHitMedium() {
        return 0;
    }

    public int GetToHitLong() {
        return 0;
    }

    public int GetClusterTableBonus() {
        return 2;
    }

    public double GetBVMultiplier() {
        return 1.2;
    }

    @Override
    public void ArmorComponent( boolean armor ) {
        Armored = armor;
        if( Owner.IsArmored() != armor ) {
            Owner.ArmorComponent( armor );
        }
    }
}

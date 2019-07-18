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

public class ArtemisVFCS extends abPlaceable implements ifMissileGuidance {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_CLAN );
    private RangedWeapon Owner;

    public ArtemisVFCS( RangedWeapon m ) {
        AC.SetCLCodes( 'F', 'X', 'X', 'F' );
        AC.SetCLDates( 3058, 3061, true, 3061, 0, 0, false, false );
        AC.SetCLFactions( "CGS", "CGS", "", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL );
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
        return "Artemis V FCS";
    }

    public String CritName() {
        return "Artemis V FCS";
    }

    public String LookupName() {
        return "Artemis V FCS";
    }

    public String ChatName() {
        return "A-V";
    }

    public String MegaMekName( boolean UseRear ) {
        return "CLArtemisV";
    }

    public String BookReference() {
        return "Tactical Operations";
    }

    public int NumCrits() {
        return 2;
    }

    @Override
    public int NumCVSpaces() {
        return 0;
    }

    public double GetTonnage() {
        if( IsArmored() ) {
            return 2.5;
        } else {
            return 1.5;
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
            return 550000.0;
        } else {
            return 250000.0;
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
        return "Artemis V FCS";
    }

    public int GetAIVClass() {
        return ifMissileGuidance.FCS_ArtemisV;
    }

    public int GetToHitShort() {
        return -1;
    }

    public int GetToHitMedium() {
        return -1;
    }

    public int GetToHitLong() {
        return -1;
    }

    public int GetClusterTableBonus() {
        return 3;
    }

    public double GetBVMultiplier() {
        return 1.3;
    }

    @Override
    public void ArmorComponent( boolean armor ) {
        Armored = armor;
        if( Owner.IsArmored() != armor ) {
            Owner.ArmorComponent( armor );
        }
    }
}

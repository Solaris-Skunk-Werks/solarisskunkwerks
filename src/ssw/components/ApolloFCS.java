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

package ssw.components;

public class ApolloFCS extends abPlaceable implements ifMissileGuidance {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private RangedWeapon Owner;

    public ApolloFCS( RangedWeapon m ) {
        AC.SetISCodes( 'D', 'X', 'X', 'E' );
        AC.SetISDates( 0, 0, false, 3071, 0, 0, false, false );
        AC.SetISFactions( "", "", "DC", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
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

    public String GetCritName() {
        return "MRM Apollo FCS";
    }

    public String GetLookupName() {
        return GetCritName();
    }

    public String GetMMName( boolean UseRear ) {
        return "ISApollo";
    }

    public int NumCrits() {
        return 1;
    }

    public float GetTonnage() {
        if( IsArmored() ) {
            return 1.5f;
        } else {
            return 1.0f;
        }
    }

    public float GetOffensiveBV() {
        return 0.0f;
    }

    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return GetOffensiveBV();
    }

    public float GetDefensiveBV() {
        if( IsArmored() ) {
            return 5.0f;
        }
        return 0.0f;
    }

    public float GetCost() {
        if( IsArmored() ) {
            return 275000.0f;
        } else {
            return 125000.0f;
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
        return "MRM Apollo FCS";
    }

    public int GetAIVClass() {
        return ifMissileGuidance.FCS_Apollo;
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
        return -1;
    }

    public float GetBVMultiplier() {
        return 1.15f;
    }
}

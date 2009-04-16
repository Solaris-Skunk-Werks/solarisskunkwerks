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

public class PPCCapacitor extends abPlaceable {
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private RangedWeapon Owner;
    private float OffBV = 0.0f;

    public PPCCapacitor( RangedWeapon w ) {
        AC.SetISCodes( 'E', 'X', 'X', 'E' );
        AC.SetISDates( 3057, 3060, true, 3060, 0, 0, false, false );
        AC.SetISFactions( "DC", "DC", "", "" );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Owner = w;
        BuildOffensiveBV();
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

    @Override
    public String GetCritName() {
        return "PPC Capacitor";
    }

    public String GetLookupName() {
        return GetCritName();
    }

    @Override
    public String GetMMName(boolean UseRear) {
        return Owner.GetMMName( UseRear ) + "Capacitor";
    }

    @Override
    public int NumCrits() {
         return 1;
    }

    @Override
    public float GetTonnage() {
        if( IsArmored() ) {
            return 1.5f;
        } else {
            return 1.0f;
        }
    }

    @Override
    public float GetCost() {
        if( IsArmored() ) {
            return 300000.0f;
        } else {
            return 150000.0f;
        }
    }

    @Override
    public float GetOffensiveBV() {
        return OffBV;
    }

    @Override
    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return GetOffensiveBV();
    }

    @Override
    public float GetDefensiveBV() {
        if( IsArmored() ) {
            return 5.0f;
        }
        return 0.0f;
    }

    @Override
    public AvailableCode GetAvailability() {
        if( IsArmored() ) {
            return ArmoredAC;
        } else {
            return AC;
        }
    }

    private void BuildOffensiveBV() {
        // the calculations here ARE NOT canon, but they should work for the
        // normal, canon PPCs, including the Enhanced ER PPC (we hope).  We add
        // 2.5 to the damage because the PPC Cap fires only every other round.
        float basemult = 0.0f;
        if( Owner.GetDamageShort() != Owner.GetDamageMedium() || Owner.GetDamageShort() != Owner.GetDamageMedium() ) {
            float mult1 = ( Owner.GetDamageShort() + 2.5f ) / Owner.GetDamageShort();
            if( Owner.GetDamageShort() < 12 && ( Owner.GetDamageShort() + 5 ) >= 12 ) {
                mult1 *= 1.2f;
            }
            float mult2 = ( Owner.GetDamageMedium() + 2.5f ) / Owner.GetDamageMedium();
            if( Owner.GetDamageMedium() < 12 && ( Owner.GetDamageMedium() + 5 ) >= 12 ) {
                mult1 *= 1.2f;
            }
            float mult3 = ( Owner.GetDamageLong() + 2.5f ) / Owner.GetDamageLong();
            if( Owner.GetDamageLong() < 12 && ( Owner.GetDamageLong() + 5 ) >= 12 ) {
                mult1 *= 1.2f;
            }
            basemult = ( mult1 + mult2 + mult3 ) / 3.0f;
        } else {
            basemult = ( Owner.GetDamageShort() + 2.5f ) / Owner.GetDamageShort();
            if( Owner.GetDamageShort() < 12 && ( Owner.GetDamageShort() + 5 ) >= 12 ) {
                basemult *= 1.2f;
            }
        }
        OffBV = Owner.GetOffensiveBV() * ( basemult - 1.0f );
    }
}

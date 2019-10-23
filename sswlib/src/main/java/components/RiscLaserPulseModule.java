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

public class RiscLaserPulseModule extends abPlaceable {
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private RangedWeapon Owner;
    private double OffBV = 0.0;

    public RiscLaserPulseModule( RangedWeapon w ) {
        AC.SetISCodes( 'F', 'X', 'X', 'X', 'F' );
        AC.SetISDates( 3134, 3137, true, 3137, 3140, 0, true, false );
        AC.SetISFactions( "RS", "RS", "RS", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL );
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
    public String ActualName() {
        return "RISC Laser Pulse Module";
    }

    @Override
    public String CritName() {
        return "RISC Pulse Module";
    }

    @Override
    public String LookupName() {
        return "RISC Laser Pulse Module";
    }

    @Override
    public String ChatName() {
        return "RISC_LPM";
    }

    @Override
    public String MegaMekName( boolean UseRear ) {
        return Owner.MegaMekName( UseRear ) + "RISC Laser Pulse Module";
    }

    @Override
    public String BookReference() {
        return "Interstellar Operations";
    }

    @Override
    public int NumCrits() {
         return 1;
    }

    @Override
    public int NumCVSpaces() {
        return 0;
    }

    @Override
    public double GetTonnage() {
        if( IsArmored() ) {
            return 1.5;
        } else {
            return 1.0;
        }
    }

    @Override
    public double GetCost() {
        if( IsArmored() ) {
            return 350000.0;
        } else {
            return 200000.0;
        }
    }

    @Override
    public double GetOffensiveBV() {
        return OffBV;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return GetOffensiveBV();
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        return GetOffensiveBV();
    }

    @Override
    public double GetDefensiveBV() {
        return 0.0;
    }

    @Override
    public void ArmorComponent( boolean armor ) {
        Armored = armor;
        if( Owner.IsArmored() != armor ) {
            Owner.ArmorComponent( armor );
        }
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
        double basemult = 1.15;
        OffBV = Owner.GetOffensiveBV() * ( basemult - 1.0 );
    }
}

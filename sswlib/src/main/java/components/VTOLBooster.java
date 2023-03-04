/*
Copyright (c) 2023~2024, Justin R. Bengtson (poopshotgun@yahoo.com)
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

public class VTOLBooster extends Equipment {
    private ifLoadout Owner;
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );

    public VTOLBooster(ifLoadout l ) {
        AC.SetISCodes( 'D', 'X', 'F', 'E', 'D' );
        AC.SetISDates( 0, 0, false, 3009, 0, 0, false, false );
        AC.SetISFactions( "", "", "ES", "" );
        AC.SetCLCodes( 'D', 'X', 'F', 'E', 'D' );
        AC.SetCLDates( 0, 0, false, 2839, 0, 0, false, false );
        AC.SetCLFactions( "", "", "ES", "" );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Owner = (ifLoadout)l;
        AddMechModifier(new MechModifier(0, 0, 0, 0.5, 0, 3, 0, 0.0, 0.0, 0.0, 0.0, true, false));
    }

    public String ActualName() {
        return "VTOL Jet Booster";
    }

    public String CritName() {
        return "VTOL Jet Booster";
    }

    public String LookupName() {
        return "VTOL Jet Booster";
    }

    public String ChatName() {
        // ammo isn't included in the chat
        return "VTOL Jet Booster";
    }

    public String MegaMekName( boolean UseRear ) {
        if( Owner.GetTechBase() >= AvailableCode.TECH_CLAN ) {
            return "CLVTOLJetBooster";
        } else {
            return "ISVTOLJetBooster";
        }
    }

    public String BookReference() {
        return "Tactical Operations: Advanced Units Equipment";
    }

    @Override
    public int NumCrits() {
        return 1;
    }

    public int NumCVSpaces() {
        return 1;
    }

    @Override
    public double GetTonnage() {
        double retval = 0.0;
        if( Owner.UsingFractionalAccounting() ) {
            retval = Math.ceil( Owner.GetEngine().GetTonnage() * 100 ) * 0.001;
        } else {
            retval = ((int) ( Math.ceil( Owner.GetEngine().GetTonnage() * 0.1 * 2 ))) * 0.5;
        }
        if( IsArmored() ) {
            retval += 0.5;
        }
        return retval;
    }

    @Override
    public double GetCost() {
        double retval = Owner.GetEngine().GetRating() * 10000.0;
        if( IsArmored() ) {
            retval += 150000.;
        }
        return retval;
    }

    @Override
    public double GetOffensiveBV() {
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    @Override
    public double GetDefensiveBV() {
        if( IsArmored() ) {
            return 5.0;
        } else {
            return 0.0;
        }
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
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public AvailableCode GetAvailability() {
        AvailableCode retval = AC.Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        return retval;
    }

    @Override
    public boolean CanAllocCVFront() { return false; }
    @Override
    public boolean CanAllocCVRear() { return false; }
    @Override
    public boolean CanAllocCVSide() { return false; }
    @Override
    public boolean CanAllocCVTurret() { return false; }

    @Override
    public String GetEquipmentType() {
        return "VTOL Jet Booster";
    }
}

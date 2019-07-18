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

public class ModularArmor extends abPlaceable {
    private final static MechModifier Modifier = new MechModifier( -1, 0, -1, 0.0, 0, 1, 0, 0.0, 0.0, 0.0, 0.0, false, false, true );
    private String Manufacturer = "";
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private boolean Rear = false;

    public ModularArmor() {
        AC.SetISCodes( 'D', 'X', 'X', 'F' );
        AC.SetISDates( 3070, 3072, true, 3072, 0, 0, false, false );
        AC.SetISFactions( "CS", "CS", "", "" );
        AC.SetCLCodes( 'D', 'X', 'X', 'F' );
        AC.SetCLDates( 3073, 3074, true, 3074, 0, 0, false, false );
        AC.SetCLFactions( "CWX", "CWX", "", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public void SetMegaMekName( String n ) {
    }

    public String ActualName() {
        return "Modular Armor";
    }

    public String CritName() {
        if( Rear ) {
            return "(R) Modular Armor";
        } else {
            return "Modular Armor";
        }
    }

    public String LookupName() {
        if( Rear ) {
            return "(R) Modular Armor";
        } else {
            return "Modular Armor";
        }
    }

    public String ChatName() {
        return "ModArmor";
    }

    public String MegaMekName( boolean UseRear ) {
        if( Rear ) {
            return "" + " (R)";
        } else {
            return "";
        }
    }

    public String BookReference() {
        return "Tactical Operations";
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
        if( IsArmored() ) {
            return 1.5;
        } else {
            return 1.0;
        }
    }

    @Override
    public double GetCost() {
        if( IsArmored() ) {
            return 160000.0;
        } else {
            return 10000.0;
        }
    }

    @Override
    public double GetOffensiveBV() {
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return GetOffensiveBV();
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    @Override
    public double GetDefensiveBV() {
        // modular armor is handled by the armor BV code.
        return 0.0;
    }

    @Override
    public boolean CanAllocHD() {
        return false;
    }

    @Override
    public boolean CanAllocCT() {
        return true;
    }

    @Override
    public boolean CanAllocTorso() {
        return true;
    }

    @Override
    public boolean CanAllocArms() {
        return true;
    }

    @Override
    public boolean CanSplit() {
        return false;
    }

    @Override
    public boolean CanAllocLegs() {
        return true;
    }

    @Override
    public boolean CanMountRear() {
        return true;
    }

    @Override
    public void MountRear( boolean rear ) {
        Rear = rear;
    }

    @Override
    public boolean IsMountedRear() {
        return Rear;
    }

    @Override
    public String GetManufacturer() {
        return Manufacturer;
    }

    @Override
    public void SetManufacturer( String n ) {
        Manufacturer = n;
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
    public String toString() {
        return "Modular Armor";
    }

    @Override
    public void AddMechModifier( MechModifier m ) {
        // this does nothing, since modular armor should only have one MechMod
        // no matter how many are added.
    }

    @Override
    public MechModifier GetMechModifier() {
        return Modifier;
    }
}

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

import ssw.Constants;

public class ModularArmor extends abPlaceable {
    private final static MechModifier Modifier = new MechModifier( -1, 0, -1, 0.0f, 0, 1, 0, 0.0f, 0.0f, 0.0f, 0.0f, false );
    private String CritName,
                   LookupName,
                   Manufacturer = "";
    private static final AvailableCode ISAC = new AvailableCode( false, 'D', 'X', 'X', 'F', 3072, 0, 0, "CS", "", false, false, 3070, true, "CS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL ),
                                       CLAC = new AvailableCode( true, 'D', 'X', 'X', 'F', 3074, 0, 0, "CWX", "", false, false, 3073, true, "CWX", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
    private AvailableCode AC;
    private boolean Rear = false;

    public ModularArmor( boolean c ) {
        CritName = "Modular Armor";
        LookupName = "";
        if( c ) {
            AC = CLAC;
        } else {
            AC = ISAC;
        }
    }

   public void SetLookupName( String n ) {
        // provided if it's anything different than the CritName
        LookupName = n;
    }

    @Override
    public String GetCritName() {
        if( Rear ) {
            return "(R) " + CritName;
        } else {
            return CritName;
        }
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
            return 160000.0f;
        } else {
            return 10000.0f;
        }
    }

    @Override
    public float GetOffensiveBV() {
        return 0.0f;
    }

    @Override
    public float GetCurOffensiveBV( boolean UseRear ) {
        return GetOffensiveBV();
    }

    @Override
    public float GetDefensiveBV() {
        // modular armor is handled by the armor BV code.
        return 0.0f;
    }

    public String GetMMName( boolean UseRear ) {
        if( Rear ) {
            return LookupName + " (R)";
        } else {
            return LookupName;
        }
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

    public boolean IsClan() {
        return AC.IsClan();
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
        AvailableCode retval = new AvailableCode( AC.IsClan(), AC.GetTechRating(), AC.GetSLCode(), AC.GetSWCode(), AC.GetCICode(), AC.GetIntroDate(), AC.GetExtinctDate(), AC.GetReIntroDate(), AC.GetIntroFaction(), AC.GetReIntroFaction(), AC.WentExtinct(), AC.WasReIntroduced(), AC.GetRandDStart(), AC.IsPrototype(), AC.GetRandDFaction(), AC.GetRulesLevelBM(), AC.GetRulesLevelIM() );
        if( IsArmored() ) {
            if( AC.IsClan() ) {
                retval.Combine( CLArmoredAC );
            } else {
                retval.Combine( ISArmoredAC );
            }
        }
        return retval;
    }

    @Override
    public String toString() {
        return CritName;
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

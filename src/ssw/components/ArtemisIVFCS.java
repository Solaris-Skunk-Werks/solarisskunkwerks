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

public class ArtemisIVFCS extends abPlaceable {
    public final static AvailableCode ISAC = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
    public final static AvailableCode CLAC = new AvailableCode( true, 'E', 'X', 'D', 'C', 2598, 0, 0, "TH", "", false, false );
    private MissileWeapon Owner;

    public ArtemisIVFCS( MissileWeapon m ) {
        Owner = m;
    }

    public MissileWeapon GetOwner() {
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
        return "Artemis IV FCS";
    }

    public String GetMMName( boolean UseRear ) {
        if( Owner.IsClan() ) {
            return "CLArtemisIV";
        } else {
            return "ISArtemisIV";
        }
    }

    public int NumCrits() {
        return 1;
    }

    public float GetTonnage() {
        // a4 never returns a tonnage, the missile launcher alters itself for BV
        // and cost purposes
        return 0.0f;
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
        // a4 never returns a cost, the missile launcher alters itself for BV
        // and cost purposes
        return 0.0f;
    }

    public AvailableCode GetAvailability() {
        if( Owner.IsClan() ) {
            if( IsArmored() ) {
                return CLArmoredAC;
            } else {
                return CLAC;
            }
        } else {
            if( IsArmored() ) {
                return ISArmoredAC;
            } else {
                return ISAC;
            }
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
}

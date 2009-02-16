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

public class PPCCapacitor extends abPlaceable {

    private final static AvailableCode ISPPCC = new AvailableCode( false, 'E', 'X', 'X', 'E', 3060, 0, 0, "DC", "", false, false, 3057, true, "DC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
    private EnergyWeapon Owner;

    public PPCCapacitor( EnergyWeapon w ) {
        Owner = w;
    }

    public EnergyWeapon GetOwner() {
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
        return 0.0f;
    }

    @Override
    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return 0.0f;
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
            return ISArmoredAC;
        } else {
            return ISPPCC;
        }
    }
}

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

public class Actuator extends abPlaceable {
    // This is a class extending abPlaceable to provide for actuators.
    // Actuators only ever use one crit.
    private boolean LocLocked;
    private float CostMult;
    private String CritName,
                   MMName;
    private AvailableCode AC;
    private Mech Owner;

    public Actuator( String name, String mname, boolean locked, AvailableCode A, float Multiplier, Mech m ) {
        LocLocked = locked;
        CritName = name;
        AC = A;
        CostMult = Multiplier;
        Owner = m;
        MMName = mname;
    }

    @Override
    public boolean LocationLocked() {
        return LocLocked;
    }

    // returns the name of this item in the Loadout.
    public String GetCritName() {
        return CritName;
    }

    public String GetLookupName() {
        return GetCritName();
    }

    public String GetMMName( boolean UseRear ) {
        return MMName;
    }

    // returns the number of crits this item takes in the Loadout.
    public int NumCrits() {
        return 1;
    }

    public float GetTonnage() {
        if( IsArmored() ) {
            return 0.5f;
        } else {
            return 0.0f;
        }
    }

    public float GetOffensiveBV() {
        // an actuator only has a BV if it is armored
        return 0.0f;
    }

    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        // BV will not change for this item, so just return the normal value
        return 0.0f;
    }

    public float GetDefensiveBV() {
        // an actuator only has a BV if it is armored
        if( IsArmored() ) {
            return NumCrits() * 5.0f;
        }
        return 0.0f;
    }

    public float GetCost() {
        if( IsArmored() ) {
            return Owner.GetTonnage() * CostMult + 150000.0f;
        } else {
            return Owner.GetTonnage() * CostMult;
        }
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    // All placeables should be able to return their AvailabileCode
    public AvailableCode GetAvailability() {
        AvailableCode retval = AC.Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        return retval;
    }

    @Override
    public String toString() {
        return CritName;
    }
}

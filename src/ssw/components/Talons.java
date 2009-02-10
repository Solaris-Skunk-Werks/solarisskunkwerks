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


public class Talons extends PhysicalWeapon {

    private final static AvailableCode CLTALONS = new AvailableCode( true, 'E', 'X', 'X', 'F', 3072, 0, 0, "CJF", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
    private int Placed = 0;

    public Talons ( Mech m )
    {
        Owner = m;
    }

    @Override
    public int GetPWClass () {
        return Constants.PW_CLASS_TALON;
    }
    
    @Override
    public boolean RequiresLowerArm() {
        return false;
    }

    @Override
    public String GetCritName() {
        return "Talons";
    }

    @Override
    public String GetMMName(boolean UseRear) {
        return "CLTalons";
    }

    @Override
    public int NumCrits() {
        if ( Owner.IsQuad() )
            return 8;
        else
            return 4;
    }

    @Override
    public boolean Contiguous() {
        return false;
    }

    @Override
    public float GetTonnage() {
        float result = 0.0f;
            result = (int) Math.ceil( Owner.GetTonnage() * 0.0667f );

        if( IsArmored() ) {
            return result + ( NumCrits() * 0.5f );
        } else {
            return result;
        }
    }

    @Override
    public float GetCost() {
        if( IsArmored() ) {
            return ( Owner.GetTonnage() * 300.0f + ( NumCrits() * 150000.0f ) );
        } else {
            return Owner.GetTonnage() * 300.0f;
        }
    }

    @Override
    public float GetOffensiveBV() {
        return GetDamageShort();
    }

    @Override
    public AvailableCode GetAvailability() {
        AvailableCode retval = CLTALONS;
        if( IsArmored() ) {
            retval.Combine( CLArmoredAC );
        }
        return retval;
    }

    @Override
    public String GetName() {
        return "Talons";
    }

    @Override
    public String GetType() {
        return "PA";
    }

    @Override
    public String GetSpecials() {
        return "PB";
    }

    @Override
    public int GetDamageShort() {
        return (int) Math.ceil( Owner.GetTonnage() * 0.2f * 1.5f );
    }

    @Override
    public int GetDamageMedium() {
        return (int) Math.ceil( Owner.GetTonnage() * 0.2f * 1.5f );
    }

    @Override
    public int GetDamageLong() {
        return (int) Math.ceil( Owner.GetTonnage() * 0.2f * 1.5f );
    }

    @Override
    public boolean IsClan() {
        return true;
    }

    @Override
    public boolean CanAllocHD()
    {
        return false;
    }

    @Override
    public boolean CanAllocCT()
    {
        return false;
    }

    @Override
    public boolean CanAllocTorso()
    {
        return false;
    }

    @Override
    public boolean CanAllocArms()
    {
        return false;
    }

    @Override
    public boolean CanAllocLegs()
    {
        return true;
    }

    @Override
    public int NumPlaced() {
        return Placed;
    }

    @Override
    public void IncrementPlaced() {
        Placed++;
    }

    @Override
    public void DecrementPlaced() {
        Placed--;
    }

    @Override
    public void ResetPlaced() {
        Placed = 0;
    }

    @Override
    public String toString() {
        if( NumCrits() > Placed ) {
            return "Talons (" + ( NumCrits() - Placed ) + ")";
        } else {
            return "Talons";
        }
    }
}

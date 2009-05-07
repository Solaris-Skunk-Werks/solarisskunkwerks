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

package ssw.battleforce;

import ssw.Constants;
import ssw.components.*;

public class BattleForceTools {

    public static final float[] BFMinRangeModifiers = {1.00f, 0.92f, 0.83f, 0.75f,
        0.66f, 0.58f, 0.50f};
    public static final float[] BFToHitModifiers = {1.20f, 1.15f, 1.10f, 1.05f,
        1.00f, 0.95f, 0.90f, 0.85f, 0.80f};

    public static String GetMovementString( ifBattleforce b ){

        if (b.GetBFSecondaryMovement() > 0){
            return  b.GetBFPrimeMovement() + "" + b.GetBFPrimeMovementMode() + "/"
                    + b.GetBFSecondaryMovement() + "" + b.GetBFSecondaryMovementMode();
        }else{
            return b.GetBFPrimeMovement() + "" + b.GetBFPrimeMovementMode();
        }

    }

    /**
     * Convert any weapon to Battleforce
     *
     * @param w A weapon to convert
     * @param b The ifBatleForce object that uses the ifWeapon
     * @return Array with short - extreme dmg values and heat as floats
     */
    public static float [] GetDamage( ifWeapon w, ifBattleforce b ){
        float [] retval = {0.0f,0.0f,0.0f,0.0f,0.0f};

        // Ignore rear facing weapons
        if ( ((abPlaceable)w).IsMountedRear() ) {
            return retval;
        }

        // Adjust heat appropriately
        retval[Constants.BF_OV] = w.GetHeat();

        if ( w.IsOneShot() ) {
            retval[Constants.BF_OV] = 0;
        }

        if ( w.IsRotary() ) {
            retval[Constants.BF_OV] *= 6;
        }

        if ( w.IsUltra() ) {
            retval[Constants.BF_OV] *= 2;
        }
        
        // Set base damage by range
        if ( w.GetRangeLong() <= 3 ) {
            if ( w instanceof RangedWeapon )
                retval[Constants.BF_SHORT] = w.GetDamageShort();
        } else if ( w.GetRangeLong() > 3 && w.GetRangeLong() <= 15 ) {
            retval[Constants.BF_SHORT] = w.GetDamageShort();
            retval[Constants.BF_MEDIUM] = w.GetDamageMedium();
        } else if ( w.GetRangeLong() > 15 && w.GetRangeLong() <= 23 )
        {
            retval[Constants.BF_SHORT] = w.GetDamageShort();
            retval[Constants.BF_MEDIUM] = w.GetDamageMedium();
            retval[Constants.BF_LONG] = w.GetDamageLong();
        } else {
            retval[Constants.BF_SHORT] = w.GetDamageShort();
            retval[Constants.BF_MEDIUM] = w.GetDamageMedium();
            retval[Constants.BF_LONG] = w.GetDamageLong();
            retval[Constants.BF_EXTREME] = w.GetDamageLong();
        }

        // Adjust for minimum range
        retval[Constants.BF_SHORT] *= BattleForceTools.BFMinRangeModifiers[w.GetRangeMin()];


        if ( w instanceof RangedWeapon ) {

            // Adjust for capacitors
            if ( ((RangedWeapon)w).IsUsingCapacitor() ) {
                retval[Constants.BF_SHORT] *= 0.5f;
                retval[Constants.BF_MEDIUM] *= 0.5f;
                retval[Constants.BF_LONG] *= 0.5f;
                retval[Constants.BF_EXTREME] *= 0.5f;
            }

            // Adjust for Targeting Computer
            if ( ((Mech)b).UsingTC() ) {
                retval[Constants.BF_SHORT] *= 1.1f;
                retval[Constants.BF_MEDIUM] *= 1.1f;
                retval[Constants.BF_LONG] *= 1.1f;
                retval[Constants.BF_EXTREME] *= 1.1f;
            }

        }

        // Adjust for AES
        // TODO add AES if applicable to the to-hit modifier
        int aes = 0;

        int location = ((Mech)b).GetLoadout().Find((abPlaceable)w);
        if ( location == Constants.LOC_RA && ((Mech)b).HasRAAES() ) {
            aes = -1;
        } else if ( ( location == Constants.LOC_LA && ((Mech)b).HasLAAES() ) ) {
            aes = -1;
        } else if ( ( location == Constants.LOC_LL || location == Constants.LOC_RL ) && ((Mech)b).HasLegAES() ) {
            aes = -1;
        }


        // Adjust for to-hit modifier
        retval[Constants.BF_SHORT] *= BattleForceTools.BFToHitModifiers[w.GetToHitShort() + 4 + aes];
        retval[Constants.BF_MEDIUM] *= BattleForceTools.BFToHitModifiers[w.GetToHitMedium() + 4 + aes];
        retval[Constants.BF_LONG] *= BattleForceTools.BFToHitModifiers[w.GetToHitLong() + 4 + aes];
        retval[Constants.BF_EXTREME] *= BattleForceTools.BFToHitModifiers[w.GetToHitLong() + 4 + aes];
        
        return retval;
    }

}

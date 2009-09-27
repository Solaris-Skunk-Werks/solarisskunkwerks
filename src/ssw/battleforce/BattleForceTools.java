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

    public static final double[] BFMinRangeModifiers = {1.00, 0.92, 0.83, 0.75,
        0.66, 0.58, 0.50};
    public static final double[] BFToHitModifiers = {1.20, 1.15, 1.10, 1.05,
        1.00, 0.95, 0.90, 0.85, 0.80};

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
     * @return Array with short - extreme dmg values and heat as doubles
     */
    public static double [] GetDamage( ifWeapon w, ifBattleforce b ){
        double [] retval = {0.0, 0.0, 0.0, 0.0, 0.0};

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
        // fixed for minimum ranges greater than 6 (ELRMs)
        int minrange = w.GetRangeMin();
        if( minrange > 6 ) { minrange = 6; }
        retval[Constants.BF_SHORT] *= BattleForceTools.BFMinRangeModifiers[minrange];


        if ( w instanceof RangedWeapon ) {

            // Adjust for capacitors
            if ( ((RangedWeapon)w).IsUsingCapacitor() ) {
                retval[Constants.BF_SHORT] *= 0.5;
                retval[Constants.BF_MEDIUM] *= 0.5;
                retval[Constants.BF_LONG] *= 0.5;
                retval[Constants.BF_EXTREME] *= 0.5;
            }

            // Adjust for Targeting Computer
            if ( ((Mech)b).UsingTC() ) {
                retval[Constants.BF_SHORT] *= 1.1;
                retval[Constants.BF_MEDIUM] *= 1.1;
                retval[Constants.BF_LONG] *= 1.1;
                retval[Constants.BF_EXTREME] *= 1.1;
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

    public static boolean isBFAutocannon(ifWeapon w)
    {
        if (((abPlaceable)w).CritName().contains("Autocannon"))
            return true;
        else if (((abPlaceable)w).CritName().contains("Light AC"))
            return true;
        else
            return false;
    }

    public static boolean isBFSRM(ifWeapon w)
    {
        if (((abPlaceable)w).CritName().contains("SRM"))
        {
            if (!w.IsStreak() && !w.IsOneShot())
                return true;
            else
                return false;
        }
        else
            return false;

    }

    public static boolean isBFLRM(ifWeapon w)
    {
        if (((abPlaceable)w).CritName().contains("LRM"))
        {
            if (w.GetFCSType() == ifMissileGuidance.FCS_NONE && !w.IsOneShot())
                return true;
            else
                return false;
        }
        else
            return false;
    }

}

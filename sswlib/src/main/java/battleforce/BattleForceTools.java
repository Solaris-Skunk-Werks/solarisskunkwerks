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

package battleforce;

import components.*;

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
        retval[BFConstants.BF_OV] = w.GetHeat();

        if ( w.IsOneShot() ) {
            retval[BFConstants.BF_OV] = 0;
        }

        if ( w.IsRotary() ) {
            retval[BFConstants.BF_OV] *= 6;
        }

        if ( w.IsUltra() ) {
            retval[BFConstants.BF_OV] *= 2;
        }
        
        // Set base damage by range
        // LRM SRM MML and HAG needs to be handled here too due to cluster damage
        if ( w.IsCluster() )
        {
            int dmgModifier = 1;
            if ( ((abPlaceable)w).CritName().contains("LB") || ((abPlaceable)w).CritName().equals("Silver Bullet Gauss") )
                dmgModifier = w.GetDamageMedium();
            if ( ((abPlaceable)w).CritName().contains("HAG") )
                dmgModifier = w.GetDamageMedium();

            if ( w.GetRangeLong() >= 0 ) {
                if ( w instanceof RangedWeapon || w instanceof MGArray )
                    retval[BFConstants.BF_SHORT] = w.GetDamageShort() * common.CommonTools.GetAverageClusterHits(w,w.ClusterModShort()) / dmgModifier;
            }
            if ( w.GetRangeLong() > 3 ) {
                retval[BFConstants.BF_MEDIUM] = w.GetDamageMedium() * common.CommonTools.GetAverageClusterHits(w,w.ClusterModMedium()) / dmgModifier;
            }
            if ( w.GetRangeLong() > 15 ) {
                retval[BFConstants.BF_LONG] = w.GetDamageLong() * common.CommonTools.GetAverageClusterHits(w,w.ClusterModLong()) / dmgModifier;
            }
            if ( w.GetRangeLong() > 24 ) {
                retval[BFConstants.BF_EXTREME] = w.GetDamageLong() * common.CommonTools.GetAverageClusterHits(w,0) / dmgModifier;
            }

            if ( ((abPlaceable)w).CritName().contains("Ultra") )
            {
                retval[BFConstants.BF_SHORT] *= 1.5;
                retval[BFConstants.BF_MEDIUM] *= 1.5;
                retval[BFConstants.BF_LONG] *= 1.5;
            }

            if (isBFMML(w))
            {
                retval[BFConstants.BF_SHORT] *= 2;
                retval[BFConstants.BF_MEDIUM] *= 1.5;
            }

            if ( isBFATM(w))
            {
                retval[BFConstants.BF_SHORT] = common.CommonTools.GetAverageClusterHits(w,2) * 3;
                retval[BFConstants.BF_MEDIUM] = common.CommonTools.GetAverageClusterHits(w,2) * 2;
                retval[BFConstants.BF_LONG] = common.CommonTools.GetAverageClusterHits(w,2);
            }
        }
        else
        {
            if ( w.GetRangeLong() <= 3 ) {
                if ( w instanceof RangedWeapon )
                    retval[BFConstants.BF_SHORT] = w.GetDamageShort();
            } else if ( w.GetRangeLong() > 3 && w.GetRangeLong() <= 15 ) {
                retval[BFConstants.BF_SHORT] = w.GetDamageShort();
                retval[BFConstants.BF_MEDIUM] = w.GetDamageMedium();
            } else if ( w.GetRangeLong() > 15 && w.GetRangeLong() <= 23 )
            {
                retval[BFConstants.BF_SHORT] = w.GetDamageShort();
                retval[BFConstants.BF_MEDIUM] = w.GetDamageMedium();
                retval[BFConstants.BF_LONG] = w.GetDamageLong();
            } else {
                retval[BFConstants.BF_SHORT] = w.GetDamageShort();
                retval[BFConstants.BF_MEDIUM] = w.GetDamageMedium();
                retval[BFConstants.BF_LONG] = w.GetDamageLong();
                retval[BFConstants.BF_EXTREME] = w.GetDamageLong();
            }
        }

        if ( w.CritName().contains("Plasma Cannon") ) {
            retval[BFConstants.BF_SHORT] = 10.0;
            retval[BFConstants.BF_MEDIUM] = 10.0;
            retval[BFConstants.BF_LONG] = 10.0;
            retval[BFConstants.BF_EXTREME] = 0.0;
        }

        //Adjust for variable damage weapons that do not reach long range
        // added 4/7/10 per conversation with nckestrel
        if ( isBFVariable(w) && w.GetRangeLong() <= 15 ) {
            retval[BFConstants.BF_MEDIUM] = ((w.GetDamageMedium() + w.GetDamageLong()) / 2.0f);
        }

        // Adjust for minimum range
        int minrange = w.GetRangeMin();
        if( minrange > 6 ) { minrange = 6; }
        {
            if (!isBFMML(w) && !isBFATM(w) )
                retval[BFConstants.BF_SHORT] *= BattleForceTools.BFMinRangeModifiers[minrange];
        }

        // Check to see if this weapon has enough ammunition to last
        // at least 10 turns.  If not, reduce the damage by 25%.
        if ( w.HasAmmo() && ! w.IsOneShot() )
        {
            if ( (b.GetAmmoCount( w.GetAmmoIndex() ) / b.GetWeaponCount(w.GetAmmoIndex()) ) < 10 )
            {
                retval[BFConstants.BF_SHORT] *= 0.75;
                retval[BFConstants.BF_MEDIUM] *= 0.75;
                retval[BFConstants.BF_LONG] *= 0.75;
                retval[BFConstants.BF_EXTREME] *= 0.75;
            }
        }

        if ( w instanceof RangedWeapon ) {

            // Adjust for capacitors
            if ( ((RangedWeapon)w).IsUsingCapacitor() ) {
                retval[BFConstants.BF_SHORT] *= 0.5;
                retval[BFConstants.BF_MEDIUM] *= 0.5;
                retval[BFConstants.BF_LONG] *= 0.5;
                retval[BFConstants.BF_EXTREME] *= 0.5;
            }

            // Adjust for Targeting Computer
            if ( ((ifUnit)b).UsingTC() && !(isBFATM(w) || isBFSRM(w) || isBFSRT(w) || isBFLRM(w) || isBFLRT(w) ) ) {
                retval[BFConstants.BF_SHORT] *= 1.1;
                retval[BFConstants.BF_MEDIUM] *= 1.1;
                retval[BFConstants.BF_LONG] *= 1.1;
                retval[BFConstants.BF_EXTREME] *= 1.1;
            }

            //Adjust Damage for One Shot
            if ( w.IsOneShot() ) {
                retval[BFConstants.BF_SHORT] *= 0.10;
                retval[BFConstants.BF_MEDIUM] *= 0.10;
                retval[BFConstants.BF_LONG] *= 0.10;
                retval[BFConstants.BF_EXTREME] *= 0.10;
            }

        }

        // Adjust for AES
        // TODO add AES if applicable to the to-hit modifier
        int aes = 0;

        if ( b instanceof Mech ) {
            int location = ((Mech)b).GetLoadout().Find((abPlaceable)w);
            if ( location == LocationIndex.MECH_LOC_RA && ((Mech)b).HasRAAES() ) {
                aes = -1;
            } else if ( ( location == LocationIndex.MECH_LOC_LA && ((Mech)b).HasLAAES() ) ) {
                aes = -1;
            } else if ( ( location == LocationIndex.MECH_LOC_LL || location == LocationIndex.MECH_LOC_RL ) && ((Mech)b).HasLegAES() ) {
                aes = -1;
            }
        }


        // Adjust for to-hit modifier
        retval[BFConstants.BF_SHORT] *= BattleForceTools.BFToHitModifiers[w.GetToHitShort() + 4 + aes];
        retval[BFConstants.BF_MEDIUM] *= BattleForceTools.BFToHitModifiers[w.GetToHitMedium() + 4 + aes];
        retval[BFConstants.BF_LONG] *= BattleForceTools.BFToHitModifiers[w.GetToHitLong() + 4 + aes];
        retval[BFConstants.BF_EXTREME] *= BattleForceTools.BFToHitModifiers[w.GetToHitLong() + 4 + aes];
        
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
            if ( w.IsStreak() )
                return false;
            else if ( ((RangedWeapon) w).IsUsingFCS() )
                return false;
            else
                return true;
        else
            return false;
    }

    public static boolean isBFSRT(ifWeapon w)
    {
        if (((abPlaceable)w).CritName().contains("SRT"))
            if ( w.IsStreak() )
                return false;
            else if ( ((RangedWeapon) w).IsUsingFCS() )
                return false;
            else
                return true;
        else
            return false;
    }

    public static boolean isBFLRM(ifWeapon w)
    {
        if (((abPlaceable)w).CritName().contains("LRM") )
        {
            if ( ((abPlaceable)w).CritName().contains("Streak") || ((abPlaceable)w).CritName().contains("Extended") )
                return false;

            if ( !((RangedWeapon) w).IsUsingFCS() ) {
                return true;
            } else {
                return false;
            }
        }
        else
            return false;
    }

    public static boolean isBFLRT(ifWeapon w)
    {
        if (((abPlaceable)w).CritName().contains("LRT") )
        {
            if ( ((abPlaceable)w).CritName().contains("Streak") || ((abPlaceable)w).CritName().contains("Extended") )
                return false;

            if ( !((RangedWeapon) w).IsUsingFCS() ) {
                return true;
            } else {
                return false;
            }
        }
        else
            return false;
    }

    public static boolean isBFMML(ifWeapon w)
    {
        if (((abPlaceable)w).CritName().contains("MML"))
            return true;
        else
            return false;
    }

    public static boolean isBFATM(ifWeapon w)
    {
        if (((abPlaceable)w).CritName().contains("ATM"))
            return true;
        else
            return false;
    }

    public static boolean isBFVariable(ifWeapon w)
    {
        if(  w.GetDamageShort() != w.GetDamageMedium() ||
             w.GetDamageShort() != w.GetDamageLong() ||
             w.GetDamageMedium() != w.GetDamageLong() )
            return true;
        else
            return false;
    }

    public static boolean isBFIF(ifWeapon w)
    {
        String[] abilities = ((abPlaceable)w).GetBattleForceAbilities();
        for( int i = 0; i < abilities.length; i++ )
        {
            if ( abilities[i].contains("IF") )
                return true;
        }

        return false;
    }

    public static boolean isBFFLK(ifWeapon w)
    {
        String[] abilities = ((abPlaceable)w).GetBattleForceAbilities();
        for( int i = 0; i < abilities.length; i++ )
        {
            if ( abilities[i].contains("FLK") )
                return true;
        }

        return false;
    }
}

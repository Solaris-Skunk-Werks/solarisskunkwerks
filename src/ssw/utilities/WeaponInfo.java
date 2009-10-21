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

package ssw.utilities;

import java.util.Vector;
import components.ifWeapon;
import components.Ammunition;
import components.RangedWeapon;

public class WeaponInfo {
    // contains a weapon and ammo pairing (may hold multiple ammos)
    // may also contain a single weapon if need be
    private ifWeapon Weapon;
    private Vector Ammos = new Vector();
    private int FiringRate = 1;
    public final static int[][] Clusters = {
        { 1, 1, 1, 1, 2, 2, 3, 3,  3,  4,  4,  4,  5,  5,  5,  5,  6,  6,  6,  7,  7,  7,  8,  8,  9,  9,  9, 10, 10, 12 },
        { 1, 1, 2, 2, 2, 2, 3, 3,  3,  4,  4,  4,  5,  5,  5,  5,  6,  6,  6,  7,  7,  7,  8,  8,  9,  9,  9, 10, 10, 12 },
        { 1, 1, 2, 2, 3, 3, 4, 4,  4,  5,  5,  5,  6,  6,  7,  7,  8,  8,  9,  9,  9, 10, 10, 10, 11, 11, 11, 12, 12, 18 },
        { 1, 2, 2, 3, 3, 4, 4, 5,  6,  7,  8,  8,  9,  9, 10, 10, 11, 11, 12, 13, 14, 15, 16, 16, 17, 17, 17, 18, 18, 24 },
        { 1, 2, 2, 3, 4, 4, 5, 5,  6,  7,  8,  8,  9,  9, 10, 10, 11, 11, 12, 13, 14, 15, 16, 16, 17, 17, 17, 18, 18, 24 },
        { 1, 2, 3, 3, 4, 4, 5, 5,  6,  7,  8,  8,  9,  9, 10, 10, 11, 11, 12, 13, 14, 15, 16, 16, 17, 17, 17, 18, 18, 24 },
        { 2, 2, 3, 3, 4, 4, 5, 5,  6,  7,  8,  8,  9,  9, 10, 10, 11, 11, 12, 13, 14, 15, 16, 16, 17, 17, 17, 18, 18, 24 },
        { 2, 2, 3, 4, 5, 6, 6, 7,  8,  9, 10, 11, 11, 12, 13, 14, 14, 15, 16, 17, 18, 19, 20, 21, 21, 22, 23, 23, 24, 32 },
        { 2, 3, 3, 4, 5, 6, 6, 7,  8,  9, 10, 11, 11, 12, 13, 14, 14, 15, 16, 17, 18, 19, 20, 21, 21, 22, 23, 23, 24, 32 },
        { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 40 },
        { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 40 } };

    public WeaponInfo( ifWeapon w ) {
        Weapon = w;
    }

    public void SetUltraRate( int r ) {
        if( Weapon.IsUltra() &! ( r < 1 || r > 2 ) ) {
            FiringRate = r;
        }
    }

    public void SetRotaryRate( int r ) {
        if( Weapon.IsRotary() &! ( r < 1 || r > 6 ) ) {
            FiringRate = r;
        }
    }

    public boolean HasAmmo() {
        return Weapon.HasAmmo();
    }

    public int GetAmmoIndex() {
        return Weapon.GetAmmoIndex();
    }

    public int GetHeat() {
        return Weapon.GetHeat();
    }

    public int GetFiringRate() {
        return FiringRate;
    }

    public int GetBestDamage( int range ) {
        int retval = 0;

        if( Weapon.HasAmmo() ) {
            
        } else {
            if( Weapon.IsCluster() ) {
                return GetDamageAtRange( range ) * Weapon.ClusterSize();
            } else {
                return GetDamageAtRange( range );
            }
        }

        return retval;
    }

    public int GetBestAverageDamage( int range ) {
        int retval = 0;

        if( Weapon.HasAmmo() ) {
            
        } else {
            if( Weapon.IsCluster() ) {
                return GetDamageAtRange( range ) * GetAverageClusterSize();
            } else {
                return GetDamageAtRange( range );
            }
        }

        return retval;
    }

    public void AddAmmo( Ammunition a ) {
        if( a.GetAmmoIndex() == Weapon.GetAmmoIndex() ) {
            Ammos.add( a );
        }
    }

    public void RemoveAmmo( Ammunition a ) {
        Ammos.remove( a );
    }

    public void ClearAmmo() {
        Ammos.clear();
    }

    private int GetDamageAtRange( int range ) {
        if( range <= Weapon.GetRangeShort() ) {
            return Weapon.GetDamageShort();
        } else if( range <= Weapon.GetRangeMedium() ) {
            return Weapon.GetDamageMedium();
        } else if( range <= Weapon.GetRangeLong() ) {
            return Weapon.GetDamageLong();
        } else {
            return 0;
        }
    }

    private int GetAverageClusterSize() {
        int index = 7; // the average roll
        int size = Weapon.GetDamageShort() * Weapon.ClusterSize();
        if( Weapon instanceof RangedWeapon ) {
            if( ((RangedWeapon) Weapon).IsUsingFCS() ) {
                index += ((RangedWeapon) Weapon).GetFCS().GetClusterTableBonus();
            }
        }
        if( size > 30 ) {
            size = 31;  // safeguard for 40 cluster size weapons.
        }
        try {
            return Clusters[index - 2][size - 2];
        } catch( Exception e ) {
            // safe guard for array out of bounds.
            e.printStackTrace();
            return 1;
        }
    }
}
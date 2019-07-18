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

public interface ifWeapon {
    // an abstract class for weapons of all types.  This provides a basic
    // functionality for most weapon types and can be overriden if needed.
    public final static int W_BALLISTIC = 0,
                            W_ENERGY = 1,
                            W_MISSILE = 2,
                            W_ARTILLERY = 3,
                            W_PHYSICAL = 4,
                            W_EQUIPMENT = 5;

    public String LookupName();
    public String CritName();
    public String GetType();
    public String GetSpecials();
    public int GetWeaponClass();
    public int GetHeat();
    public double GetBVHeat();
    public int GetDamageShort();
    public int GetDamageMedium();
    public int GetDamageLong();
    public int GetRangeMin();
    public int GetRangeShort();
    public int GetRangeMedium();
    public int GetRangeLong();
    public int GetToHitShort();
    public int GetToHitMedium();
    public int GetToHitLong();
    public int ClusterSize();
    public int ClusterGrouping();
    public int ClusterModShort();
    public int ClusterModMedium();
    public int ClusterModLong();
    public int GetAmmoLotSize();
    public int GetAmmoIndex();
    public int GetTechBase();
    public boolean IsCluster();
    public boolean IsOneShot();
    public boolean IsStreak();
    public boolean IsUltra();
    public boolean IsRotary();
    public boolean IsExplosive();
    public boolean IsFCSCapable();
    public int GetFCSType();
    public boolean IsTCCapable();
    public boolean IsArrayCapable();
    public boolean OmniRestrictActuators();
    public boolean HasAmmo();
    public boolean SwitchableAmmo();
    public boolean RequiresFusion();
    public boolean RequiresNuclear();
    public boolean RequiresPowerAmps();
}

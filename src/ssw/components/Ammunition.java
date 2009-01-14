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

public class Ammunition extends abPlaceable {
    private float Tonnage = 1.0f,
                  Cost = 0.0f,
                  OffBV = 0.0f,
                  DefBV = 0.0f;
    private int LotSize = 0,
                AmmoIndex,
                minrng = 0,
                srtrng = 0,
                medrng = 0,
                lngrng = 0,
                damage = 0,
                ToHitShort = 0,
                ToHitMedium = 0,
                ToHitLong = 0,
                group = 1,
                cluster = 1;
    private boolean Explosive = true,
                    Clan;
    private AvailableCode AC;
    private String Name,
                   LookupName;

    public Ammunition( String n, String l, int i, boolean c, AvailableCode a ) {
        // some things of note here:
        // Name is the visible name in the program
        // LookupName is the name used when saved to MegaMek
        AC = a;
        Name = n;
        LookupName = l;
        AmmoIndex = i;
        Clan = c;
    }

    public String GetCritName() {
        return Name;
    }

    @Override
    public String GetPrintName() {
        return "@"+GetCritName().replace("@ ", "") + " (" + GetLotSize() + ")";
    }

    public String GetMMName( boolean UseRear ) {
        return LookupName;
    }

    public int GetAmmoIndex() {
        return AmmoIndex;
    }

    public int NumCrits() {
        // ammunition only ever takes up one critical slot
        return 1;
    }

    public void SetStats( float t, float c, float obv, float dbv ) {
        Tonnage = t;
        Cost = c;
        OffBV = obv;
        DefBV = dbv;
    }

    public float GetTonnage() {
        // Only certain types of ammo comes in less or more than one ton lots
        // but we'll have to set this regardless
        return Tonnage;
    }

    // return the cost of the item
    public float GetCost() {
        return Cost;
    }

    public float GetOffensiveBV() {
        return OffBV;
    }

    public float GetCurOffensiveBV( boolean UseRear ) {
        return GetOffensiveBV();
    }

    public float GetDefensiveBV() {
        return DefBV;
    }

    public void SetLotSize( int s ) {
        // sets the amount of ammunition in this lot
        LotSize = s;
    }

    public int GetLotSize() {
        return LotSize;
    }

    public void SetExplosive( boolean b ) {
        Explosive = b;
    }

    public boolean IsExplosive() {
        // some ammo can explode and CASE is needed
        return Explosive;
    }

    public boolean IsClan() {
        return Clan;
    }

    public void SetToHit( int thsrt, int thmed, int thlng ) {
        ToHitShort = thsrt;
        ToHitMedium = thmed;
        ToHitLong = thlng;
    }

    public int GetToHitShort() {
        return ToHitShort;
    }

    public int GetToHitMedium() {
        return ToHitMedium;
    }

    public int GetToHitLong() {
        return ToHitLong;
    }

    public void SetDamage( int dam, int clstr, int grp ) {
        damage = dam;
        cluster = clstr;
        group = grp;
    }

    public int GetDamage() {
        return damage;
    }

    public int ClusterSize() {
        return cluster;
    }

    public int ClusterGrouping() {
        return group;
    }

    public void SetRange( int min, int srt, int med, int lng ) {
        minrng = min;
        srtrng = srt;
        medrng = med;
        lngrng = lng;
    }

    public int GetMinRange() {
        return minrng;
    }

    public int GetShortRange() {
        return srtrng;
    }

    public int GetMediumRange() {
        return medrng;
    }

    public int GetLongRange() {
        return lngrng;
    }

    @Override
    public boolean CanArmor() {
        // Ammunition can never be armored
        return false;
    }
    public AvailableCode GetAvailability() {
        return AC;
    }

    @Override
    public String toString() {
        return Name;
    }
}

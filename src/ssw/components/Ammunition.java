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
                damsht = 0,
                dammed = 0,
                damlng = 0,
                ToHitShort = 0,
                ToHitMedium = 0,
                ToHitLong = 0,
                group = 1,
                cluster = 1,
                WeaponClass = ifWeapon.W_BALLISTIC,
                FCSType = ifMissileGuidance.FCS_NONE;
    private boolean Explosive = true,
                    IsCluster = false;
    private AvailableCode AC;
    private String Name,
                   PrintName,
                   LookupName,
                   MegaMekName;

    public Ammunition( String name, String lookupname, String mname, int idx, AvailableCode a ) {
        // some things of note here:
        // Name is the visible name in the program
        // LookupName is the name used when saved to MegaMek
        AC = a;
        Name = name;
        // set the print name the same as the critname.  if it needs to be
        // changed a method is provided.
        PrintName = name;
        LookupName = lookupname;
        MegaMekName = mname;
        AmmoIndex = idx;
    }

    public void SetStats( float tons, float cost, float obv, float dbv ) {
        Tonnage = tons;
        Cost = cost;
        OffBV = obv;
        DefBV = dbv;
    }

    public void SetToHit( int thsrt, int thmed, int thlng ) {
        ToHitShort = thsrt;
        ToHitMedium = thmed;
        ToHitLong = thlng;
    }

    public void SetDamage( int dsht, int dmed, int dlng, boolean clustered, int clstr, int grp ) {
        damsht = dsht;
        dammed = dmed;
        damlng = dlng;
        IsCluster = clustered;
        cluster = clstr;
        group = grp;
    }

    public void SetRange( int min, int srt, int med, int lng ) {
        minrng = min;
        srtrng = srt;
        medrng = med;
        lngrng = lng;
    }

    public void SetAmmo( int size, boolean explode, int wclass, int fcsclass ) {
        LotSize = size;
        Explosive = explode;
        WeaponClass = wclass;
        FCSType = fcsclass;
    }

    public void SetPrintName( String p ) {
        // for ammos that have huge names you can override the normal name
        PrintName = p;
    }

    public String GetCritName() {
        return Name;
    }

    public String GetLookupName() {
        return LookupName;
    }

    public String GetBasePrintName() {
        // returns the base printname for cloning purposes
        return PrintName;
    }

    @Override
    public String GetPrintName() {
        //return "@" + PrintName.replace( "@ ", "" ) + " (" + GetLotSize() + ")";
        return PrintName.replace( "@ ", "" );
    }

    public String GetMMName( boolean UseRear ) {
        return MegaMekName;
    }

    public int GetAmmoIndex() {
        return AmmoIndex;
    }

    public int NumCrits() {
        // ammunition only ever takes up one critical slot
        return 1;
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

    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return GetOffensiveBV();
    }

    public float GetDefensiveBV() {
        return DefBV;
    }

    public int GetLotSize() {
        return LotSize;
    }

    public boolean IsExplosive() {
        // some ammo can explode and CASE is needed
        return Explosive;
    }

    public int GetWeaponClass() {
        return WeaponClass;
    }

    public int GetFCSType() {
        return FCSType;
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

    public int GetDamageShort() {
        return damsht;
    }

    public int GetDamageMedium() {
        return dammed;
    }

    public int GetDamageLong() {
        return damlng;
    }

    public boolean IsCluster() {
        return IsCluster;
    }

    public int ClusterSize() {
        return cluster;
    }

    public int ClusterGrouping() {
        return group;
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

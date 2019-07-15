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

import common.CommonTools;

public class Ammunition extends abPlaceable {
    private double Tonnage = 1.0,
                  Cost = 0.0,
                  OffBV = 0.0,
                  DefBV = 0.0;
    private int LotSize = 0,
                CurLotSize = 0,
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
    private String ActualName,
                   CritName,
                   LookupName,
                   MegaMekName,
                   BookRef = "";

    public Ammunition( String actualname, String critname, String lookupname, String mmname, int idx, AvailableCode a ) {
        // some things of note here:
        // Name is the visible name in the program
        // LookupName is the name used when saved to MegaMek
        AC = a;
        ActualName = actualname;
        CritName = critname;
        LookupName = lookupname;
        MegaMekName = mmname;
        AmmoIndex = idx;
    }

    private Ammunition( Ammunition a ) {
        AC = a.AC.Clone();
        ActualName = a.ActualName;
        CritName = a.CritName;
        LookupName = a.LookupName;
        MegaMekName = a.MegaMekName;
        AmmoIndex = a.AmmoIndex;
        Tonnage = a.Tonnage;
        Cost = a.Cost;
        OffBV = a.OffBV;
        DefBV = a.DefBV;
        ToHitShort = a.ToHitShort;
        ToHitMedium = a.ToHitMedium;
        ToHitLong = a.ToHitLong;
        damsht = a.damsht;
        dammed = a.dammed;
        damlng = a.damlng;
        IsCluster = a.IsCluster;
        cluster = a.cluster;
        group = a.group;
        minrng = a.minrng;
        srtrng = a.srtrng;
        medrng = a.medrng;
        lngrng = a.lngrng;
        LotSize = a.LotSize;
        CurLotSize = LotSize;
        Explosive = a.Explosive;
        WeaponClass = a.WeaponClass;
        FCSType = a.FCSType;
        BookRef = a.BookRef;
    }

    public void SetStats( double tons, double cost, double obv, double dbv ) {
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
        CurLotSize = LotSize;
        Explosive = explode;
        WeaponClass = wclass;
        FCSType = fcsclass;
    }

    public void SetBookReference( String b ) {
        BookRef = b;
    }

    public String ActualName() {
        return ActualName;
    }

    public String CritName() {
        return CritName;
    }

    public String LookupName() {
        return LookupName;
    }

    public String ChatName() {
        // ammo isn't included in the chat
        return "";
    }

    public String MegaMekName( boolean UseRear ) {
        return MegaMekName;
    }

    public String BookReference() {
        return BookRef;
    }

    public String GetBaseCritName() {
        // returns the base printname for cloning purposes
        return CritName;
    }

    public int GetAmmoIndex() {
        return AmmoIndex;
    }

    public int NumCrits() {
        // ammunition only ever takes up one critical slot
        return 1;
    }

    @Override
    public int NumCVSpaces() {
        // this returns 0 because ammo is handled by the CV much differently
        return 0;
    }

    public double GetTonnage() {
        // Only certain types of ammo comes in less or more than one ton lots
        // but we'll have to set this regardless
        if( LotSize != CurLotSize ) {
            double TonsPerShot = Tonnage / (double) LotSize;
            return CommonTools.RoundFractionalTons( TonsPerShot * (double) CurLotSize );
        }
        return Tonnage;
    }

    // return the cost of the item
    public double GetCost() {
        if( LotSize != CurLotSize ) {
            double CostPerShot = Cost / (double) LotSize;
            return CommonTools.RoundFractionalTons( CostPerShot * (double) CurLotSize );
        }
        return Cost;
    }

    public double GetOffensiveBV() {
        return OffBV;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return GetOffensiveBV();
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    public double GetDefensiveBV() {
        return DefBV;
    }

    public int GetLotSize() {
        if( CurLotSize != LotSize ) { return CurLotSize; }
        return LotSize;
    }

    public boolean SetLotSize( int size ) {
        if( size > LotSize || size < 1 ) return false;
        CurLotSize = size;
        return true;
    }

    public boolean OddLotSize() {
        if( CurLotSize != LotSize ) { return true; }
        return false;
    }

    public int GetMaxLotSize() {
        return LotSize;
    }

    public void ResetLotSize() {
        CurLotSize = LotSize;
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

    public Ammunition Clone() {
        return new Ammunition( this );
    }

    public AvailableCode GetAvailability() {
        return AC;
    }

    @Override
    public String toString() {
        return CritName;
    }
}

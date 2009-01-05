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

public class BallisticWeapon extends abPlaceable implements ifWeapon {
    private AvailableCode AC;
    private String Name,
                   LookupName,
                   Specials = "",
                   Type,
                   Manufacturer = "";
    private boolean Clan,
                    OmniRestrict = false,
                    Ammo = false,
                    Explosive = false,
                    Rotary = false,
                    Ultra = false,
                    Cluster = false,
                    TCCapable = true,
                    MountedRear = false,
                    SwitchableAmmo = false,
                    Alloc_HD = true,
                    Alloc_CT = true,
                    Alloc_Torso = true,
                    Alloc_Arms = true,
                    Alloc_Legs = true,
                    CanSplit = false,
                    InArray = false,
                    LocationLinked = false,
                    Nuclear = false,
                    Fusion = false;
    private int Heat = 0,
                DamSht = 0,
                DamMed = 0,
                DamLng = 0,
                RngMin = 0,
                RngSht = 0,
                RngMed = 0,
                RngLng = 0,
                ToHitShort = 0,
                ToHitMedium = 0,
                ToHitLong = 0,
                AmmoPerTon = -1,
                NumCrits = 0,
                AmmoIndex = 0;
    private float Tonnage = 0.0f,
                  Cost = 0.0f,
                  OffBV = 0.0f,
                  DefBV = 0.0f;
    private MGArray CurArray;

    public BallisticWeapon( String n, String l, String t, boolean c, AvailableCode a ) {
        Name = n;
        LookupName = l;
        Clan = c;
        Type = t;
        AC = a;
    }

    public void SetRange( int min, int sht, int med, int lng ) {
        RngMin = min;
        RngSht = sht;
        RngMed = med;
        RngLng = lng;
    }

    public void SetDamage( int sht, int med, int lng ) {
        DamSht = sht;
        DamMed = med;
        DamLng = lng;
    }

    public void SetHeat( int h ) {
        Heat = h;
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

    public void SetSpecials( String spec, boolean or, boolean ammo, int apt, int ind, boolean tc, boolean sa ) {
        Specials = spec;
        OmniRestrict = or;
        Ammo = ammo;
        TCCapable = tc;
        AmmoPerTon = apt;
        AmmoIndex = ind;
        SwitchableAmmo = sa;
    }

    public void SetStats( float tons, int crits, float cost, float obv, float dbv ) {
        Tonnage = tons;
        NumCrits = crits;
        Cost = cost;
        OffBV = obv;
        DefBV = dbv;
    }

    public void SetAllocations( boolean hd, boolean ct, boolean torso, boolean arms, boolean legs, boolean split ) {
        Alloc_HD = hd;
        Alloc_CT = ct;
        Alloc_Torso = torso;
        Alloc_Arms = arms;
        Alloc_Legs = legs;
        CanSplit = split;
    }

    public void SetBallistics( boolean e, boolean u, boolean r, boolean c ) {
        Explosive = e;
        Ultra = u;
        Rotary = r;
        Cluster = c;
    }

    public void SetRequiresFusion( boolean b ) {
        Fusion = b;
    }

    public void SetRequiresNuclear( boolean b ) {
        Nuclear = b;
    }

    public boolean RequiresFusion() {
        return Fusion;
    }

    public boolean RequiresNuclear() {
        return Nuclear;
    }

    public String GetName() {
        return Name;
    }

    public String GetMMName( boolean UseRear ) {
        if( UseRear ) {
            return LookupName + " (R)";
        } else {
            return LookupName;
        }
    }

    public String GetType() {
        return Type;
    }

    public String GetSpecials() {
        return Specials;
    }

    public int GetHeat() {
        return Heat;
    }

    public int GetBVHeat() {
        if( Rotary ) {
            return Heat * 6;
        } else if( Ultra ) {
            return Heat * 2;
        } else {
            return Heat;
        }
    }

    public int GetDamageShort() {
        return DamSht;
    }

    public int GetDamageMedium() {
        return DamMed;
    }

    public int GetDamageLong() {
        return DamLng;
    }

    public int GetRangeMin() {
        return RngMin;
    }

    public int GetRangeShort() {
        return RngSht;
    }

    public int GetRangeMedium() {
        return RngMed;
    }

    public int GetRangeLong() {
        return RngLng;
    }

    public int GetAmmo() {
        return AmmoPerTon;
    }

    public int GetAmmoIndex() {
        return AmmoIndex;
    }

    public boolean IsOneShot() {
        // ballistic weapons can never one-shot
        return false;
    }

    public boolean IsStreak() {
        // ballistic weapons are never Streak capable
        return false;
    }

    public boolean IsUltra() {
        return Ultra;
    }

    public boolean IsRotary() {
        return Rotary;
    }

    public boolean IsExplosive() {
        return Explosive;
    }

    public boolean IsClan() {
        return Clan;
    }

    public boolean IsArtemisCapable() {
        return false;
    }

    public boolean IsTCCapable() {
        return TCCapable;
    }

    public boolean IsArrayCapable() {
        return false;
    }

    public boolean OmniRestrictActuators() {
        return OmniRestrict;
    }

    public boolean HasAmmo() {
        return Ammo;
    }

    public String GetCritName() {
        if( MountedRear ) {
            return "(R) " + Name;
        } else {
            return Name;
        }
    }

    public int NumCrits() {
        return NumCrits;
    }

    public float GetTonnage() {
        if( IsArmored() ) {
            return Tonnage + ( NumCrits * 0.5f );
        } else {
            return Tonnage;
        }
    }

    public float GetCost() {
        if( IsArmored() ) {
            return Cost + ( 150000.0f * NumCrits );
        } else {
            return Cost;
        }
    }

    public float GetOffensiveBV() {
        return OffBV;
    }

    public float GetCurOffensiveBV( boolean UseRear ) {
        if( UseRear ) {
            if( MountedRear ) {
                return OffBV;
            } else {
                return OffBV * 0.5f;
            }
        } else {
            if( MountedRear ) {
                return OffBV * 0.5f;
            } else {
                return OffBV;
            }
        }
    }

    public float GetDefensiveBV() {
        if( IsArmored() ) {
            return ( ( DefBV + OffBV ) * 0.05f * NumCrits() ) + DefBV;
        }
        return DefBV;
    }

    @Override
    public boolean CanMountRear() {
        // all weapons can be mounted to the rear.
        return true;
    }

    @Override
    public void MountRear( boolean rear ) {
        // this sets the mounted rear boolean
        MountedRear = rear;
        if( InArray && ( CurArray.IsMountedRear() != rear ) ) {
            CurArray.MountRear( rear );
        }
    }

    @Override
    public boolean IsMountedRear() {
        return MountedRear;
    }

    @Override
    public boolean CanAllocHD() {
        return Alloc_HD;
    }

    @Override
    public boolean CanAllocCT() {
        return Alloc_CT;
    }

    @Override
    public boolean CanAllocTorso() {
        return Alloc_Torso;
    }

    @Override
    public boolean CanAllocArms() {
        return Alloc_Arms;
    }

    @Override
    public boolean CanAllocLegs() {
        return Alloc_Legs;
    }

    @Override
    public boolean CanSplit() {
        return CanSplit;
    }

    @Override
    public String GetManufacturer() {
        return Manufacturer;
    }

    @Override
    public void SetManufacturer( String n ) {
        Manufacturer = n;
    }

    public AvailableCode GetAvailability() {
        AvailableCode retval = new AvailableCode( AC.IsClan(), AC.GetTechRating(), AC.GetSLCode(), AC.GetSWCode(), AC.GetCICode(), AC.GetIntroDate(), AC.GetExtinctDate(), AC.GetReIntroDate(), AC.GetIntroFaction(), AC.GetReIntroFaction(), AC.WentExtinct(), AC.WasReIntroduced(), AC.GetRandDStart(), AC.IsPrototype(), AC.GetRandDFaction(), AC.GetRulesLevelBM(), AC.GetRulesLevelIM() );
        if( IsArmored() ) {
            if( AC.IsClan() ) {
                retval.Combine( CLArmoredAC );
            } else {
                retval.Combine( ISArmoredAC );
            }
        }
        return retval;
    }

    @Override
    public String toString() {
        if( MountedRear ) {
            return "(R) " + Name;
        } else {
            return Name;
        }
    }

    public int ClusterSize() {
        // for Ballistic weapons that can cluster, it's always the damage.
        return DamSht;
    }

    public int ClusterGrouping() {
        // Ballistics that cluster always group by 1
        return 1;
    }

    public boolean IsCluster() {
        return Cluster;
    }

    public boolean SwitchableAmmo() {
        return SwitchableAmmo;
    }

    public void AddToArray( MGArray a ) {
        CurArray = a;
        InArray = true;
    }

    public MGArray GetMyArray() {
        return CurArray;
    }

    public boolean IsInArray() {
        return InArray;
    }

    @Override
    public boolean LocationLinked() {
        return LocationLinked;
    }

    public void SetLocationLinked( boolean b ) {
        LocationLinked = b;
        SetLocked( b );
    }
}

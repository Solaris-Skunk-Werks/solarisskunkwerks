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

public class Artillery extends abPlaceable implements ifWeapon {
    private AvailableCode AC;
    private float Tonnage = 0.0f,
                  Cost = 0.0f,
                  OffBV = 0.0f,
                  DefBV = 0.0f;
    private int NumCrits = 0,
                RangeMin = 0,
                RangeSrt = 0,
                RangeMed = 0,
                RangeLng = 0,
                DamageSrt = 0,
                DamageMed = 0,
                DamageLng = 0,
                ToHitSrt = 0,
                ToHitMed = 0,
                ToHitLng = 0,
                AmmoPerTon = 0,
                AmmoIndex = 0,
                Heat = 0;
    private String CritName,
                   MMName,
                   Type,
                   Specials = "",
                   Manufacturer = "";
    private boolean Explosive = false,
                    Alloc_HD = true,
                    Alloc_CT = true,
                    Alloc_Torso = true,
                    Alloc_Arms = true,
                    Alloc_Legs = true,
                    CanSplit = false,
                    OmniRestrict = false,
                    SwitchableAmmo = false,
                    HasAmmo = false,
                    Fusion = false,
                    Nuclear = false,
                    MountedRear = false;

    public Artillery( String name, String mname, String type, AvailableCode a ) {
        AC = a;
        CritName = name;
        MMName = mname;
        Type = type;
    }

    public void SetRange( int min, int sht, int med, int lng ) {
        RangeMin = min;
        RangeSrt = sht;
        RangeMed = med;
        RangeLng = lng;
    }

    public void SetDamage( int sht, int med, int lng ) {
        DamageSrt = sht;
        DamageMed = med;
        DamageLng = lng;
    }

    public void SetToHit( int sht, int med, int lng ) {
        ToHitSrt = sht;
        ToHitMed = med;
        ToHitLng = lng;
    }

    public void SetSpecials( String spec, boolean or, int heat ) {
        Specials = spec;
        OmniRestrict = or;
        Heat = heat;
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

    public void SetArtillery( boolean e, boolean a, int apt, int ind, boolean sa ) {
        Explosive = e;
        HasAmmo = a;
        AmmoPerTon = apt;
        AmmoIndex = ind;
        SwitchableAmmo = sa;
    }

    public void SetRequiresFusion( boolean b ) {
        Fusion = b;
    }

    public void SetRequiresNuclear( boolean b ) {
        Nuclear = b;
    }

    @Override
    public String GetCritName() {
        if( MountedRear ) {
            return "(R) " + CritName;
        } else {
            return CritName;
        }
    }

    @Override
    public String GetMMName(boolean UseRear) {
        if( UseRear ) {
            return MMName + " (R)";
        } else {
            return MMName;
        }
    }

    @Override
    public int NumCrits() {
        return NumCrits;
    }

    @Override
    public float GetTonnage() {
        if( IsArmored() ) {
            return Tonnage + ( NumCrits * 0.5f );
        } else {
            return Tonnage;
        }
    }

    @Override
    public float GetCost() {
        if( IsArmored() ) {
            return Cost + ( 150000.0f * NumCrits );
        } else {
            return Cost;
        }
    }

    @Override
    public float GetOffensiveBV() {
        return OffBV;
    }

    @Override
    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        // artillery is unaffected by targeting computers, so we'll just ignore
        float retval = OffBV;
        if( UseAES ) {
            retval *= 1.5f;
        }
        if( UseRear ) {
            if( MountedRear ) {
                return retval;
            } else {
                return retval * 0.5f;
            }
        } else {
            if( MountedRear ) {
                return retval * 0.5f;
            } else {
                return retval;
            }
        }
    }

    @Override
    public float GetDefensiveBV() {
        if( IsArmored() ) {
            return ( ( DefBV + OffBV ) * 0.05f * NumCrits() ) + DefBV;
        }
        return DefBV;
    }

    @Override
    public String GetManufacturer() {
        return Manufacturer;
    }

    @Override
    public void SetManufacturer( String n ) {
        Manufacturer = n;
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
    }

    @Override
    public boolean IsMountedRear() {
        return MountedRear;
    }

    @Override
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

    public String GetName() {
        return CritName;
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
        return Heat;
    }

    public int GetDamageShort() {
        return DamageSrt;
    }

    public int GetDamageMedium() {
        return DamageMed;
    }

    public int GetDamageLong() {
        return DamageLng;
    }

    public int GetRangeMin() {
        return RangeMin;
    }

    public int GetRangeShort() {
        return RangeSrt;
    }

    public int GetRangeMedium() {
        return RangeMed;
    }

    public int GetRangeLong() {
        return RangeLng;
    }

    public int GetToHitShort() {
        return ToHitSrt;
    }

    public int GetToHitMedium() {
        return ToHitMed;
    }

    public int GetToHitLong() {
        return ToHitLng;
    }

    public int ClusterSize() {
        return DamageSrt;
    }

    public int ClusterGrouping() {
        return 1;
    }

    public int GetAmmo() {
        return AmmoPerTon;
    }

    public int GetAmmoIndex() {
        return AmmoIndex;
    }

    public boolean IsClan() {
        return AC.IsClan();
    }

    public boolean IsCluster() {
        return false;
    }

    public boolean IsOneShot() {
        return false;
    }

    public boolean IsStreak() {
        return false;
    }

    public boolean IsUltra() {
        return false;
    }

    public boolean IsRotary() {
        return false;
    }

    public boolean IsExplosive() {
        return Explosive;
    }

    public boolean IsArtemisCapable() {
        return false;
    }

    public boolean IsTCCapable() {
        return false;
    }

    public boolean IsArrayCapable() {
        return false;
    }

    public boolean OmniRestrictActuators() {
        return OmniRestrict;
    }

    public boolean HasAmmo() {
        return HasAmmo;
    }

    public boolean SwitchableAmmo() {
        return SwitchableAmmo;
    }

    public boolean RequiresFusion() {
        return Fusion;
    }

    public boolean RequiresNuclear() {
        return Nuclear;
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
    public boolean CanArmor() {
        return true;
    }

    @Override
    public String toString() {
        if( MountedRear ) {
            return "(R) " + CritName;
        } else {
            return CritName;
        }
    }
}

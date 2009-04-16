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

import ssw.battleforce.*;

public class RangedWeapon extends abPlaceable implements ifWeapon {
    private AvailableCode AC;
    private PPCCapacitor Capacitor = null;
    private MGArray CurArray = null;
    private ifMissileGuidance FCS = null;
    private String Name,
                   MegaMekName,
                   LookupName,
                   Specials = "",
                   Type,
                   Manufacturer = "",
                   PrintName = "";
    private boolean HasAmmo = false,
                    SwitchableAmmo = false,
                    RequiresFusion = false,
                    RequiresNuclear = false,
                    RequiresPowerAmps = false,
                    Alloc_HD = true,
                    Alloc_CT = true,
                    Alloc_Torso = true,
                    Alloc_Arms = true,
                    Alloc_Legs = true,
                    CanSplit = false,
                    OmniRestrict = false,
                    LocationLinked = false,
                    MountedRear = false,
                    Rotary = false,
                    Ultra = false,
                    IsCluster = false,
                    Explosive = false,
                    Streak = false,
                    OneShot = false,
                    CanUseFCS = false,
                    TCCapable = true,
                    ArrayCapable = false,
                    CanUseCapacitor = false,
                    UsingCapacitor = false,
                    CanUseInsulator = false,
                    UsingInsulator = false,
                    InArray = false,
                    CanUseCaseless = false,
                    UsingFCS = false;
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
                AmmoLotSize = 0,
                NumCrits = 0,
                AmmoIndex = 0,
                ClusterSize = 1,
                ClusterGroup = 1,
                CaselessAmmoIDX = 0,
                OriginalAmmoIDX = 0,
                FCSType = ifMissileGuidance.FCS_NONE,
                WeaponClass = ifWeapon.W_BALLISTIC;
    private float Tonnage = 0.0f,
                  Cost = 0.0f,
                  OffBV = 0.0f,
                  DefBV = 0.0f;

    public RangedWeapon( String name, String lookupname, String mmname, String type, String spec, AvailableCode a, int wepclass ) {
        Name = name;
        LookupName = lookupname;
        MegaMekName = mmname;
        Type = type;
        AC = a;
        Specials = spec;
        WeaponClass = wepclass;
    }

    public void SetStats( float tons, int crits, float cost, float obv, float dbv ) {
        Tonnage = tons;
        NumCrits = crits;
        Cost = cost;
        OffBV = obv;
        DefBV = dbv;
    }

    public void SetHeat( int h ) {
        Heat = h;
    }

    public void SetToHit( int sht, int med, int lng ) {
        ToHitShort = sht;
        ToHitMedium = med;
        ToHitLong = lng;
    }

    public void SetDamage( int sht, int med, int lng, boolean clust, int size, int group ) {
        DamSht = sht;
        DamMed = med;
        DamLng = lng;
        IsCluster = clust;
        ClusterSize = size;
        ClusterGroup = group;
    }

    public void SetRange( int min, int sht, int med, int lng ) {
        RngMin = min;
        RngSht = sht;
        RngMed = med;
        RngLng = lng;
    }

    public void SetAmmo( boolean has, int size, int idx, boolean Switch ) {
        HasAmmo = has;
        AmmoIndex = idx;
        OriginalAmmoIDX = idx;
        AmmoLotSize = size;
        SwitchableAmmo = Switch;
    }

    public void SetAllocations( boolean hd, boolean ct, boolean torso, boolean arms, boolean legs, boolean split, boolean omniarm ) {
        Alloc_HD = hd;
        Alloc_CT = ct;
        Alloc_Torso = torso;
        Alloc_Arms = arms;
        Alloc_Legs = legs;
        CanSplit = split;
        OmniRestrict = omniarm;
    }

    public void SetRequirements( boolean fus, boolean nuc, boolean needspa ) {
        RequiresFusion = fus;
        RequiresNuclear = nuc;
        RequiresPowerAmps = needspa;
    }

    public void SetWeapon( boolean os, boolean streak, boolean ultra, boolean rotary, boolean explode, boolean tc, boolean array, boolean capacitor, boolean insulator ) {
        OneShot = os;
        Streak = streak;
        Ultra = ultra;
        Rotary = rotary;
        Explosive = explode;
        TCCapable = tc;
        CanUseCapacitor = capacitor;
        ArrayCapable = array;
        CanUseInsulator = insulator;
    }

    public void SetCaselessAmmo( boolean canuse, int idx ) {
        CanUseCaseless = canuse;
        CaselessAmmoIDX = idx;
    }

    public void SetMissileFCS( boolean canuse, int fcstype ) {
        CanUseFCS = canuse;
        FCSType = fcstype;
    }

    public void SetPrintName( String s ) {
        PrintName = s;
    }

    @Override
    public void SetManufacturer( String n ) {
        Manufacturer = n;
    }

    @Override
    public String GetCritName() {
        String retval = Name;
        if( UsingCapacitor ) {
            retval += " + PPC Capacitor";
        }
        if( UsingInsulator ) {
            retval += "(Insulated)";
        }
        if( MountedRear ) {
            return "(R) " + retval;
        } else {
            return retval;
        }
    }

    public String GetLookupName() {
        String retval = LookupName;
        if( UsingCapacitor ) {
            retval += " + PPC Capacitor";
        }
        if( UsingInsulator ) {
            retval += " (Insulated)";
        }
        if( MountedRear ) {
            return "(R) " + retval;
        } else {
            return retval;
        }
    }

    @Override
    public String GetMMName(boolean UseRear) {
        if( UseRear ) {
            return MegaMekName + " (R)";
        } else {
            return MegaMekName;
        }
    }

    public String GetName() {
        return Name;
    }

    @Override
    public String GetPrintName() {
        return PrintName;
    }

    public String GetType() {
        return Type;
    }

    public String GetSpecials() {
        return Specials;
    }

    @Override
    public String GetManufacturer() {
        return Manufacturer;
    }

    public int GetTechBase() {
        return AC.GetTechBase();
    }

    public int GetWeaponClass() {
        return WeaponClass;
    }

    @Override
    public AvailableCode GetAvailability() {
        AvailableCode retval = AC.Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        if( UsingCapacitor ) {
            retval.Combine( Capacitor.GetAvailability() );
        }
        return retval;
    }

    @Override
    public int NumCrits() {
        return NumCrits;
    }

    @Override
    public float GetTonnage() {
        float retval = Tonnage;
        if( IsArmored() ) {
            retval += NumCrits * 0.5f;
        }
        if( UsingCapacitor ) {
            retval += Capacitor.GetTonnage();
        }
        if( UsingFCS ) {
            retval += ((abPlaceable) FCS).GetTonnage();
        }
        return retval;
    }

    public float GetCost() {
        float retval = Cost;
        if( IsArmored() ) {
            retval += ( 150000.0f * NumCrits );
        }
        if( UsingCapacitor ) {
            retval += Capacitor.GetCost();
        }
        if( UsingFCS ) {
            retval += ((abPlaceable) FCS).GetCost();
        }
        return retval;
    }

    @Override
    public float GetOffensiveBV() {
        float retval = OffBV;
        if( UsingFCS ) {
            retval = ( Math.round( OffBV * FCS.GetBVMultiplier() * 100.0f ) ) * 0.01f;
        }
        if( UsingCapacitor ) {
            // round off the capacitor BV total, as this is how Tac Ops does it.
            retval = Math.round( OffBV + Capacitor.GetOffensiveBV() );
        }
        return retval;
    }

    @Override
    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        // artillery is unaffected by targeting computers, so we'll just ignore
        float retval = GetOffensiveBV();
        if( UseAES ) {
            retval *= 1.5f;
        }
        if( UseTC && TCCapable ) {
            retval *= 1.25f;
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

    public float GetDefensiveBV() {
        float retval = 0.0f;
        if( UsingFCS ) {
            retval = ( Math.round( DefBV * FCS.GetBVMultiplier() * 100.0f ) ) * 0.01f;
            retval += ((abPlaceable) FCS).GetDefensiveBV();
        } else {
            retval = DefBV;
        }
        if( UsingCapacitor ) {
            retval += Capacitor.GetDefensiveBV();
        }
        if( IsArmored() ) {
            retval += (( GetOffensiveBV() + retval ) * 0.05f * NumCrits() );
        }
        return retval;
    }

    public int GetHeat() {
        if( UsingCapacitor ) {
            return Heat + 5;
        }
        return Heat;
    }

    public int GetBVHeat() {
        int retval = Heat;
        if( UsingCapacitor ) {
            retval += 5;
        }
        if( Rotary ) { retval *= 6; }
        if( Ultra ) { retval *= 2; }
        if( OneShot ) { retval = Math.round( retval * 0.25f ); }
        if( Streak ) { retval =  Math.round( retval * 0.5f ); }
        return retval;
    }

    public int GetDamageShort() {
        if( UsingCapacitor ) {
            return DamSht + 5;
        }
        return DamSht;
    }

    public int GetDamageMedium() {
        if( UsingCapacitor ) {
            return DamMed + 5;
        }
        return DamMed;
    }

    public int GetDamageLong() {
        if( UsingCapacitor ) {
            return DamLng + 5;
        }
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

    public int GetToHitShort() {
        if( UsingFCS ) {
            return ToHitShort + FCS.GetToHitShort();
        } else {
            return ToHitShort;
        }
    }

    public int GetToHitMedium() {
        if( UsingFCS ) {
            return ToHitMedium + FCS.GetToHitMedium();
        } else {
            return ToHitMedium;
        }
    }

    public int GetToHitLong() {
        if( UsingFCS ) {
            return ToHitLong + FCS.GetToHitLong();
        } else {
            return ToHitLong;
        }
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

    public boolean IsCluster() {
        return IsCluster;
    }

    public int ClusterSize() {
        return ClusterSize;
    }

    public int ClusterGrouping() {
        return ClusterGroup;
    }

    public boolean IsOneShot() {
        return OneShot;
    }

    public boolean IsStreak() {
        return Streak;
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

    public boolean IsTCCapable() {
        return TCCapable;
    }

    public boolean IsArrayCapable() {
        return ArrayCapable;
    }

    public boolean HasAmmo() {
        return HasAmmo;
    }

    public int GetAmmoLotSize() {
        return AmmoLotSize;
    }

    public int GetAmmoIndex() {
        return AmmoIndex;
    }

    public boolean SwitchableAmmo() {
        return SwitchableAmmo;
    }

    public boolean RequiresFusion() {
        return RequiresFusion;
    }

    public boolean RequiresNuclear() {
        return RequiresNuclear;
    }

    public boolean RequiresPowerAmps() {
        return RequiresPowerAmps;
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

    public boolean OmniRestrictActuators() {
        return OmniRestrict;
    }

    @Override
    public boolean CanArmor() {
        return true;
    }

    public boolean CanUseCapacitor() {
        return CanUseCapacitor;
    }

    public void UseCapacitor( boolean c ) {
        if( c ) {
            Capacitor = new PPCCapacitor( this );
            UsingCapacitor = true;
        } else {
            Capacitor = null;
            UsingCapacitor = false;
        }
    }

    public boolean IsUsingCapacitor() {
        return UsingCapacitor;
    }

    public PPCCapacitor GetCapacitor() {
        return Capacitor;
    }

    public boolean CanUseInsulator() {
        return CanUseInsulator;
    }

    public boolean IsUsingInsulator() {
        return UsingInsulator;
    }

    public boolean IsFCSCapable() {
        return CanUseFCS;
    }

    public int GetFCSType() {
        return FCSType;
    }

    public void UseFCS( boolean b, int type ) {
        if( b != UsingFCS ) {
            UsingFCS = b;
            if( UsingFCS ) {
                switch( type ) {
                    case ifMissileGuidance.FCS_ArtemisIV:
                        if( FCSType == ifMissileGuidance.FCS_ArtemisIV || FCSType == ifMissileGuidance.FCS_ArtemisV ) {
                            FCS = new ArtemisIVFCS( this );
                        } else {
                            FCS = null;
                            UsingFCS = false;
                        }
                        break;
                    case ifMissileGuidance.FCS_ArtemisV:
                        if( FCSType == ifMissileGuidance.FCS_ArtemisV ) {
                            FCS = new ArtemisVFCS( this );
                        } else {
                            FCS = null;
                            UsingFCS = false;
                        }
                        break;
                    case ifMissileGuidance.FCS_Apollo:
                        if( FCSType == ifMissileGuidance.FCS_Apollo ) {
                            FCS = new ApolloFCS( this );
                        } else {
                            FCS = null;
                            UsingFCS = false;
                        }
                        break;
                    default:
                        FCS = null;
                        UsingFCS = false;
                        break;
                }
            } else {
                FCS = null;
                UsingFCS = false;
            }
        }
    }

    public boolean IsUsingFCS() {
        return UsingFCS;
    }

    public ifMissileGuidance GetFCS() {
        return FCS;
    }

    public void AddToArray( MGArray a ) {
        CurArray = a;
        InArray = true;
    }

    public void RemoveFromArray() {
        CurArray = null;
        InArray = false;
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

    public float GetBFDamageShort( boolean TC ) {
        float retval = 0;

        // this logic seems wierd.
        if ( GetDamageLong() >= 3 ) {          
            retval = GetDamageShort();
        } else {
            // should it still be 0?
        }

        // Adjust for minimum range
        retval *= BattleForceTools.BFMinRangeModifiers[GetRangeMin()];

        // Adjust for to-hit modifier
        retval *= BattleForceTools.BFToHitModifiers[GetToHitShort() + 4];

        // Adjust for capacitors
        if ( UsingCapacitor ) { retval *= 0.50f; }

        // Adjust for Targeting Computer
        if ( TC ) { retval *= 1.10f; }

        // Adjust for AES
        //TODO: Add code

        return retval;
    }

    public float GetBFDamageMedium( boolean TC ) {
        float retval = 0;

        if ( GetRangeShort() > 3 )
            retval = GetDamageShort();

        return retval;
    }

    public float GetBFDamageLong( boolean TC ) {
        float retval = 0;

        if ( GetRangeLong() > 15 )
            retval = GetDamageLong();

        return retval;
    }

    public float GetBFDamageExtreme( boolean TC ) {
        float retval = 0;

        if ( GetRangeLong() > 23 )
            retval = GetDamageLong();

        return retval;
    }

    public String GetBFDamageString( boolean TC ) {
        String retval = "";

        retval += String.format("%1$,8.2f", GetBFDamageShort(TC) ) + "\t";
        retval += GetBFDamageMedium(TC) + "\t";
        retval += GetBFDamageLong(TC);

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
}

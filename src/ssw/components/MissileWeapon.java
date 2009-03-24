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

public class MissileWeapon extends abPlaceable implements ifWeapon {
    private AvailableCode AC;
    private ifMissileGuidance FCS = null;
    private String Name,
                   LookupName,
                   Specials = "",
                   Type,
                   Manufacturer = "",
                   PrintName = "";
    private boolean Clan,
                    OmniRestrict = false,
                    Ammo = true,
                    MountedRear = false,
                    IsCluster = true,
                    SAmmo = false,
                    Streak = false,
                    OneShot = false,
                    UseFCS = false,
                    Fusion = false,
                    Nuclear = false;
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
                AmmoPerTon = 0,
                NumCrits = 0,
                AmmoIndex = 0,
                Cluster = 1,
                Group = 1,
                FCSType = ifMissileGuidance.FCS_NONE;
    private float Tonnage = 0.0f,
                  Cost = 0.0f,
                  OffBV = 0.0f,
                  DefBV = 0.0f;

    public MissileWeapon( String n, String l, String t, boolean c, AvailableCode a ) {
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
        if( UseFCS ) {
            return ToHitShort + FCS.GetToHitShort();
        } else {
            return ToHitShort;
        }
    }

    public int GetToHitMedium() {
        if( UseFCS ) {
            return ToHitMedium + FCS.GetToHitMedium();
        } else {
            return ToHitMedium;
        }
    }

    public int GetToHitLong() {
        if( UseFCS ) {
            return ToHitLong + FCS.GetToHitLong();
        } else {
            return ToHitLong;
        }
    }

    public void SetSpecials( String spec, boolean or, boolean ammo, int apt, int ind, boolean sa ) {
        Specials = spec;
        OmniRestrict = or;
        Ammo = ammo;
        AmmoPerTon = apt;
        AmmoIndex = ind;
        SAmmo = sa;
    }

    public void SetStats( float tons, int crits, float cost, float obv, float dbv ) {
        Tonnage = tons;
        NumCrits = crits;
        Cost = cost;
        OffBV = obv;
        DefBV = dbv;
    }

    public void SetMissile( int clstr, int grp, boolean strk, int fcs, boolean os ) {
        Cluster = clstr;
        Group = grp;
        Streak = strk;
        FCSType = fcs;
        OneShot = os;
    }

    public int GetFCSType() {
        return FCSType;
    }

    public void UseFCS( boolean b, int type ) {
        if( b != UseFCS ) {
            UseFCS = b;
            if( UseFCS ) {
                switch( type ) {
                    case ifMissileGuidance.FCS_ArtemisIV:
                        if( FCSType == ifMissileGuidance.FCS_ArtemisIV || FCSType == ifMissileGuidance.FCS_ArtemisV ) {
                            FCS = new ArtemisIVFCS( this );
                        } else {
                            FCS = null;
                            UseFCS = false;
                        }
                        break;
                    case ifMissileGuidance.FCS_ArtemisV:
                        if( FCSType == ifMissileGuidance.FCS_ArtemisV ) {
                            FCS = new ArtemisVFCS( this );
                        } else {
                            FCS = null;
                            UseFCS = false;
                        }
                        break;
                    case ifMissileGuidance.FCS_Apollo:
                        if( FCSType == ifMissileGuidance.FCS_Apollo ) {
                            FCS = new ApolloFCS( this );
                        } else {
                            FCS = null;
                            UseFCS = false;
                        }
                        break;
                    default:
                        FCS = null;
                        UseFCS = false;
                        break;
                }
            } else {
                FCS = null;
                UseFCS = false;
            }
        }
    }

    public boolean IsUsingFCS() {
        return UseFCS;
    }

    public ifMissileGuidance GetFCS() {
        return FCS;
    }

    public void SetRequiresFusion( boolean b ) {
        Fusion = b;
    }

    public void SetRequiresNuclear( boolean b ) {
        Nuclear = b;
    }

    public void SetPrintName( String s ) {
        PrintName = s;
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

    public int GetHeat() {
        return Heat;
    }

    public int GetBVHeat() {
        if( OneShot ) { return Math.round( Heat * 0.25f ); }
        if( Streak ) { return Math.round( Heat * 0.5f ); }
        return Heat;
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

    public float GetBFDamageShort( boolean TC ) {
        int retval = 0;

        retval = GetDamageShort();

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

        if ( GetRangeShort() > 15 )
            retval = GetDamageShort();

        return retval;
    }

    public float GetBFDamageExtreme( boolean TC ) {
        float retval = 0;

        if ( GetRangeShort() > 23 )
            retval = GetDamageShort();

        return retval;
    }

    public String GetBFDamageString( boolean TC ) {
        String retval = "";

        retval += GetBFDamageShort(TC);

        return retval;
    }

    public int GetAmmo() {
        return AmmoPerTon;
    }

    public int GetAmmoIndex() {
        return AmmoIndex;
    }

    public boolean IsOneShot() {
        return OneShot;
    }

    public boolean IsStreak() {
        return Streak;
    }

    public boolean IsUltra() {
        // missile weapons are never Ultra capable
        return false;
    }

    public boolean IsRotary() {
        // missile weapons are never Rotary capable
        return false;
    }

    public boolean IsExplosive() {
        // no canon missile weapon is explosive
        return false;
    }

    public boolean IsClan() {
        return Clan;
    }

    public boolean IsFCSCapable() {
        if( FCSType != ifMissileGuidance.FCS_NONE ) {
            return true;
        } else {
            return false;
        }
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
        float result = Tonnage;
        if( IsArmored() ) {
            result += NumCrits * 0.5f;
        }
        if( UseFCS ) {
            result += ((abPlaceable) FCS).GetTonnage();
        }
        return result;
    }

    public float GetCost() {
        float result = Cost;
        if( IsArmored() ) {
            result += 150000.0f * NumCrits;
        }
        if( UseFCS ) {
            result += ((abPlaceable) FCS).GetCost();
        }
        return result;
    }

    public float GetOffensiveBV() {
        if( UseFCS ) {
            return ( Math.round( OffBV * FCS.GetBVMultiplier() * 100.0f ) ) * 0.01f;
        } else {
            return OffBV;
        }
    }

    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        float retval = OffBV;
        if( UseAES ) {
            retval *= 1.5f;
        }
        if( UseTC && IsTCCapable() ) {
            retval *= 1.25f;
        }
        if( UseFCS ) {
            retval = ( Math.round( OffBV * FCS.GetBVMultiplier() * 100.0f ) ) * 0.01f;
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
        float result = 0.0f;
        if( UseFCS ) {
            result = ( Math.round( DefBV * FCS.GetBVMultiplier() * 100.0f ) ) * 0.01f;
            result += ((abPlaceable) FCS).GetDefensiveBV();
        } else {
            result = DefBV;
        }
        if( IsArmored() ) {
            result += (( GetOffensiveBV() + DefBV ) * 0.05f * NumCrits() );
        }
        return result;
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
        return Cluster;
    }

    public int ClusterGrouping() {
        return Group;
    }

    public boolean IsCluster() {
        return IsCluster;
    }

    public boolean SwitchableAmmo() {
        return SAmmo;
    }
}

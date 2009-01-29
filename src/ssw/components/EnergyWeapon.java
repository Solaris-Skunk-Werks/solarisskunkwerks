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

public class EnergyWeapon extends abPlaceable implements ifWeapon {
    private AvailableCode AC;
    private String Name,
                   LookupName,
                   Specials = "",
                   Type,
                   Manufacturer = "";
    private boolean Clan,
                    OmniRestrict = false,
                    Ammo = false,
                    TCCapable = true,
                    MountedRear = false,
                    Fusion = false,
                    Nuclear = false,
                    UseCapacitor = false,
                    Explosive = false;
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
    private PPCCapacitor Capacitor = null;

    public EnergyWeapon( String n, String l, String t, boolean c, AvailableCode a ) {
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

    public void SetSpecials( String spec, boolean or, boolean ammo, int apt, int ind, boolean tc ) {
        Specials = spec;
        OmniRestrict = or;
        Ammo = ammo;
        TCCapable = tc;
        AmmoPerTon = apt;
        AmmoIndex = ind;
    }

    public void SetStats( float tons, int crits, float cost, float obv, float dbv ) {
        Tonnage = tons;
        NumCrits = crits;
        Cost = cost;
        OffBV = obv;
        DefBV = dbv;
    }

    public void UseCapacitor( boolean c ) {
        if( c ) {
            UseCapacitor = true;
            Capacitor = new PPCCapacitor( this );
        } else {
            UseCapacitor = false;
            Capacitor = null;
        }
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
        if( UseCapacitor ) {
            return Heat + 5;
        }
        return Heat;
    }

    public int GetBVHeat() {
        // energy weapons don't need anything special here
        if( UseCapacitor ) {
            return Heat + 5;
        }
        return Heat;
    }

    public int GetDamageShort() {
        if( UseCapacitor ) {
            return DamSht + 5;
        }
        return DamSht;
    }

    public int GetDamageMedium() {
        if( UseCapacitor ) {
            return DamMed + 5;
        }
        return DamMed;
    }

    public int GetDamageLong() {
        if( UseCapacitor ) {
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

    public int GetAmmo() {
        return AmmoPerTon;
    }

    public int GetAmmoIndex() {
        return AmmoIndex;
    }

    public boolean IsOneShot() {
        // energy weapons can never one-shot
        return false;
    }

    public boolean IsStreak() {
        // energy weapons are never Streak capable
        return false;
    }

    public boolean IsUltra() {
        // energy weapons are never Ultra capable
        return false;
    }

    public boolean IsRotary() {
        // energy weapons are never Rotary capable
        return false;
    }

    public boolean IsExplosive() {
        return Explosive;
    }

    public void SetExplosive( boolean b ) {
        Explosive = b;
    }

    public boolean HasCapacitor() {
        return UseCapacitor;
    }

    public PPCCapacitor GetCapacitor() {
        return Capacitor;
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
        float retval = Tonnage;
        if( IsArmored() ) {
            retval += NumCrits * 0.5f;
        }
        if( UseCapacitor ) {
            retval += Capacitor.GetTonnage();
        }
        return retval;
    }

    public float GetCost() {
        float retval = 0.0f;
        if( IsArmored() ) {
            retval += Cost + ( 150000.0f * NumCrits );
        } else {
            retval += Cost;
        }
        if( UseCapacitor ) {
            retval += Capacitor.GetCost();
        }
        return retval;
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
        float retval = DefBV;
        if( IsArmored() ) {
            retval = ( ( DefBV + OffBV ) * 0.05f * NumCrits() ) + DefBV;
        }
        if( UseCapacitor ) {
            retval += Capacitor.GetDefensiveBV();
        }
        return retval;
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
        if( UseCapacitor ) {
            retval.Combine( Capacitor.GetAvailability() );
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
        // an energy weapon never has a cluster size
        return 1;
    }

    public int ClusterGrouping() {
        // this really shouldn't matter for an energy weapon
        return 1;
    }

    public boolean IsCluster() {
        // energy weapons never fire in clusters
        return false;
    }

    public boolean SwitchableAmmo() {
        // no energy weapon has switchable ammo
        return false;
    }
}

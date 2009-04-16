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

public class MGArray extends abPlaceable implements ifWeapon {

    private RangedWeapon MGType;
    private RangedWeapon[] MGs = { null, null, null, null };
    private int NumMGs;
    private boolean Clan,
                    Rear = false,
                    Fusion = false,
                    Nuclear = false;
    private float BaseTons,
                  MGTons;
    private String Manufacturer = "";
    private AvailableCode AC;

    public MGArray( RangedWeapon type, int num, float tons, boolean c, AvailableCode a ) {
        MGType = type;
        NumMGs = num;
        MGTons = tons;
        Clan = c;
        AC = a;
        for( int i = 0; i < NumMGs; i++ ) {
            MGs[i] = Copy( MGType );
            MGs[i].AddToArray( this );
            MGs[i].SetLocationLinked( true );
        }

        if( Clan ) {
            BaseTons = 0.25f;
        } else {
            BaseTons = 0.5f;
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

    @Override
    public String GetCritName() {
        if( Rear ) {
            return "(R) " + GetName();
        } else {
            return GetName();
        }
    }

    public String GetLookupName() {
        if( Rear ) {
            if( Clan ) {
                return "(R) (CL) " + GetCritName();
            } else {
                return "(R) (IS) " + GetCritName();
            }
        } else {
            if( Clan ) {
                return "(CL) " + GetCritName();
            } else {
                return "(IS) " + GetCritName();
            }
        }
    }

    @Override
    public String GetMMName( boolean UseRear ) {
        if( Clan ) {
            if( Rear ) {
                return "CLMGA" + " (R)";
            } else {
                return "CLMGA";
            }
        } else {
            if( Rear ) {
                return "ISMGA" + " (R)";
            } else {
                return "ISMGA";
            }
        }
    }

    @Override
    public int NumCrits() {
        return 1;
    }

    @Override
    public float GetTonnage() {
        float result = 0.0f;
        if( IsArmored() ) {
            result += BaseTons + 0.5f;
        } else {
            result += BaseTons;
        }
        // now for the MGs.
        for( int i = 0; i < MGs.length; i++ ) {
            if( MGs[i] != null ) {
                result += MGs[i].GetTonnage();
            }
        }
        return result;
    }

    public float GetMGTons() {
        return MGTons;
    }

    public float GetBaseTons() {
        return BaseTons;
    }

    @Override
    public float GetCost() {
        float result = 1250.0f;
        if( IsArmored() ) {
            result += 150000.0f;
        }
        // now for the MGs.
        for( int i = 0; i < MGs.length; i++ ) {
            if( MGs[i] != null ) {
                result += MGs[i].GetCost();
            }
        }
        return result;
    }

    public float GetOffensiveBV() {
        return (float) Math.floor( ( 67.0f * NumMGs * MGType.GetOffensiveBV() ) ) * 0.01f + NumMGs * MGType.GetOffensiveBV();
    }

    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return (float) Math.floor( ( 67.0f * NumMGs * MGType.GetOffensiveBV() ) ) * 0.01f + NumMGs * MGType.GetCurOffensiveBV( UseRear, UseTC, UseAES );
    }

    public float GetDefensiveBV() {
        float result = 0.0f;
        if( IsArmored() ) {
            result += ( Math.floor( ( 67.0f * NumMGs * MGType.GetOffensiveBV() ) ) * 0.01f ) * 0.05f;
        }
        // get each MGs defensive BV
        for( int i = 0; i < MGs.length; i++ ) {
            if( MGs[i] != null ) {
                result += MGs[i].GetDefensiveBV();
            }
        }
        return result;
    }

    public RangedWeapon GetMGType() {
        return MGType;
    }

    public int GetNumMGs() {
        return NumMGs;
    }

    public RangedWeapon[] GetMGs() {
        return MGs;
    }

    @Override
    public AvailableCode GetAvailability() {
        AvailableCode retval = AC.Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        return retval;
    }

    public String GetName() {
        return "MG Array (" + NumMGs + " " + MGType.GetCritName() + ")";
    }

    @Override
    public String GetPrintName() {
        return "MG Array (" + NumMGs + " " + MGType.GetPrintName() + ")";
    }

    public String GetType() {
        return "T";
    }

    public String GetSpecials() {
        return "-";
    }

    public int GetWeaponClass() {
        return ifWeapon.W_BALLISTIC;
    }

    public int GetFCSType() {
        return ifMissileGuidance.FCS_NONE;
    }

    public int GetHeat() {
        return 0;
    }

    public int GetBVHeat() {
        return 0;
    }

    public int GetDamageShort() {
        return MGType.GetDamageShort();
    }

    public int GetDamageMedium() {
        return MGType.GetDamageMedium();
    }

    public int GetDamageLong() {
        return MGType.GetDamageLong();
    }

    public int GetRangeMin() {
        return MGType.GetRangeMin();
    }

    public int GetRangeShort() {
        return MGType.GetRangeShort();
    }

    public int GetRangeMedium() {
        return MGType.GetRangeMedium();
    }

    public int GetRangeLong() {
        return MGType.GetRangeLong();
    }

    public int GetToHitShort() {
        return MGType.GetToHitShort();
    }

    public int GetToHitMedium() {
        return MGType.GetToHitMedium();
    }

    public int GetToHitLong() {
        return MGType.GetToHitLong();
    }

    public float GetBFDamageShort( boolean TC ) {
        float retval = 0;

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

    public int ClusterSize() {
        return NumMGs;
    }

    public int ClusterGrouping() {
        return MGType.GetDamageShort();
    }

    public int GetAmmoLotSize() {
        return MGType.GetAmmoLotSize();
    }

    public int GetAmmoIndex() {
        return MGType.GetAmmoIndex();
    }

    public int GetTechBase() {
        return AC.GetTechBase();
    }

    public boolean IsClan() {
        return Clan;
    }

    public boolean IsCluster() {
        return true;
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
        return false;
    }

    public boolean IsFCSCapable() {
        return false;
    }

    public boolean IsTCCapable() {
        return false;
    }

    public boolean IsArrayCapable() {
        return false;
    }

    public boolean OmniRestrictActuators() {
        return false;
    }

    public boolean HasAmmo() {
        return true;
    }

    public boolean SwitchableAmmo() {
        return false;
    }

    @Override
    public boolean CanMountRear() {
        return true;
    }

    @Override
    public void MountRear( boolean rear ) {
        Rear = rear;
        if( MGType.IsMountedRear() != rear ) {
            MGType.MountRear( rear );
        }
    }

    @Override
    public boolean IsMountedRear() {
        return Rear;
    }

    @Override
    public void SetManufacturer( String m ) {
       Manufacturer = m;
    }

    @Override
    public String GetManufacturer() {
        return Manufacturer;
    }

    private RangedWeapon Copy( RangedWeapon b ) {
        RangedWeapon retval = new RangedWeapon( b.GetName(), b.GetLookupName(), b.GetMMName( false ), b.GetType(), b.GetSpecials(), b.GetAvailability().Clone(), b.GetWeaponClass() );
        retval.SetStats( b.GetTonnage(), b.NumCrits(), b.GetCost(), b.GetOffensiveBV(), b.GetDefensiveBV() );
        retval.SetHeat( b.GetHeat() );
        retval.SetToHit( b.GetToHitShort(), b.GetToHitMedium(), b.GetToHitMedium() );
        retval.SetRange( b.GetRangeMin(), b.GetRangeShort(), b.GetRangeMedium(), b.GetRangeLong() );
        retval.SetDamage( b.GetDamageShort(), b.GetDamageMedium(), b.GetDamageLong(), b.IsCluster(), b.ClusterSize(), b.ClusterGrouping() );
        retval.SetAmmo( b.HasAmmo(), b.GetAmmoLotSize(), b.GetAmmoIndex(), b.SwitchableAmmo() );
        retval.SetAllocations(b.CanAllocHD(), b.CanAllocCT(), b.CanAllocTorso(), b.CanAllocArms(), b.CanAllocLegs(), b.CanSplit(), b.OmniRestrictActuators() );
        retval.SetWeapon( b.IsOneShot(), b.IsStreak(), b.IsUltra(), b.IsRotary(), b.IsExplosive(), b.IsTCCapable(), b.IsArrayCapable(), b.CanUseCapacitor(), false );
        retval.SetMissileFCS( b.IsFCSCapable(), b.GetFCSType() );
        retval.SetRequirements( b.RequiresFusion(), b.RequiresNuclear(), b.RequiresPowerAmps() );
        retval.SetPrintName( b.GetPrintName() );
        return retval;
    }

    @Override
    public String toString() {
        return GetName();
    }
}

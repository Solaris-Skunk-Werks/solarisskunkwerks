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

    private BallisticWeapon MGType;
    private BallisticWeapon[] MGs = { null, null, null, null };
    private int NumMGs;
    private boolean Clan,
                    Rear = false,
                    Fusion = false,
                    Nuclear = false;
    private float BaseTons,
                  MGTons;
    private String Manufacturer = "";
    private AvailableCode AC;

    public MGArray( BallisticWeapon type, int num, float tons, boolean c, AvailableCode a ) {
        MGType = type;
        NumMGs = num;
        MGTons = tons;
        Clan = c;
        AC = a;
        for( int i = 0; i < NumMGs; i++ ) {
            MGs[i] = Copy( MGType );
            MGs[i].AddToArray( this );
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
                result += MGs[i].GetTonnage() + MGTons;
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

    public float GetCurOffensiveBV( boolean UseRear ) {
        return (float) Math.floor( ( 67.0f * NumMGs * MGType.GetOffensiveBV() ) ) * 0.01f + NumMGs * MGType.GetCurOffensiveBV( UseRear );
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

    public BallisticWeapon GetMGType() {
        return MGType;
    }

    public int GetNumMGs() {
        return NumMGs;
    }

    public BallisticWeapon[] GetMGs() {
        return MGs;
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

    public int ClusterSize() {
        return NumMGs;
    }

    public int ClusterGrouping() {
        return MGType.GetDamageShort();
    }

    public int GetAmmo() {
        return MGType.GetAmmo();
    }

    public int GetAmmoIndex() {
        return MGType.GetAmmoIndex();
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

    private BallisticWeapon Copy( BallisticWeapon b ) {
        BallisticWeapon retval = new BallisticWeapon( b.GetName(), b.GetMMName( false ), b.GetType(), b.IsClan(), b.GetAvailability() );
        ((BallisticWeapon) retval).SetHeat( b.GetHeat() );
        ((BallisticWeapon) retval).SetDamage( b.GetDamageShort(), b.GetDamageMedium(), b.GetDamageLong() );
        ((BallisticWeapon) retval).SetRange( b.GetRangeMin(), b.GetRangeShort(), b.GetRangeMedium(), b.GetRangeLong() );
        ((BallisticWeapon) retval).SetSpecials( b.GetSpecials(), b.OmniRestrictActuators(), b.HasAmmo(), b.GetAmmo(), b.GetAmmoIndex(), b.IsTCCapable(), b.SwitchableAmmo() );
        ((BallisticWeapon) retval).SetStats( b.GetTonnage(), b.NumCrits(), b.GetCost(), b.GetOffensiveBV(), b.GetDefensiveBV() );
        ((BallisticWeapon) retval).SetBallistics( b.IsExplosive(), b.IsUltra(), b.IsRotary(), b.IsCluster() );
        ((BallisticWeapon) retval).SetAllocations( b.CanAllocHD(), b.CanAllocCT(), b.CanAllocTorso(), b.CanAllocArms(), b.CanAllocLegs(), b.CanSplit() );
        ((BallisticWeapon) retval).SetToHit( b.GetToHitShort(), b.GetToHitMedium(), b.GetToHitLong() );
        ((BallisticWeapon) retval).SetPrintName( b.GetPrintName() );
        if( b.LocationLinked() ) {
            ((BallisticWeapon) retval).SetLocationLinked( true );
        }
        return retval;
    }

    @Override
    public String toString() {
        return GetName();
    }
}

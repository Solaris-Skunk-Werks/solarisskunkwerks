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

public class MGArray extends abPlaceable implements ifWeapon {

    private RangedWeapon MGType;
    private RangedWeapon[] MGs = { null, null, null, null };
    private int NumMGs;
    private boolean Clan,
                    Rear = false,
                    Fusion = false,
                    Nuclear = false;
    private double BaseTons,
                  MGTons;
    private String Manufacturer = "";
    private AvailableCode AC;
    private ifTurret Turret = null;

    public MGArray( RangedWeapon type, int num, double tons, boolean c, AvailableCode a ) {
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
            BaseTons = 0.25;
        } else {
            BaseTons = 0.5;
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

    public boolean RequiresPowerAmps() {
        return false;
    }

    public String ActualName() {
        return "Machine Gun Array";
    }

    // the lookup name is used when we are trying to find the piece of equipment.
    public String LookupName() {
        String retval = GetName();
        if( Rear ) {
            if( Clan ) {
                retval = "(R) (CL) " + retval;
            } else {
                retval = "(R) (IS) " + retval;
            }
        } else {
            if( Clan ) {
                retval = "(CL) " + retval;
            } else {
                retval = "(IS) " + retval;
            }
        }
        if( IsTurreted() ) {
            retval = "(T) " + retval;
        }
        return retval;
    }

    // the crit name is how the item appears in the loadout when allocated.
    public String CritName() {
        String retval = GetShortName();
        if( Rear ) {
            retval = "(R) " + retval;
        }
        if( IsTurreted() ) {
            retval = "(T) " + retval;
        }
        return retval;
    }

    // the name to be used when expoerting this equipment to a chat line.
    public String ChatName() {
        return "MGA+" + NumMGs + MGType.ChatName();
    }

    // the name to be used when exporting to MegaMek
    public String MegaMekName( boolean UseRear ) {
        String retval;
        if( MGType.ActualName().contains( "Light" ) ) {
            retval = "LMGA";
        } else if( MGType.ActualName().contains( "Heavy" ) ) {
            retval = "HMGA";
        } else {
            retval = "MGA";
        }
        if( Clan ) {
            retval = "CL" + retval;
        } else {
            retval = "IS" + retval;
        }
        if( Rear ) {
            retval += " (R)";
        }
        return retval;
    }

    // reference for the book that the equipment comes from
    public String BookReference() {
        return "Tech Manual";
    }

    @Override
    public int NumCrits() {
        return 1;
    }

    public int NumCVSpaces() {
        return 1;
    }

    @Override
    public double GetTonnage() {
        double result = 0.0;
        if( IsArmored() ) {
            result += BaseTons + 0.5;
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

    public double GetMGTons() {
        return MGTons;
    }

    public double GetBaseTons() {
        return BaseTons;
    }

    @Override
    public double GetCost() {
        double result = 1250.0;
        if( IsArmored() ) {
            result += 150000.0;
        }
        // now for the MGs.
        for( int i = 0; i < MGs.length; i++ ) {
            if( MGs[i] != null ) {
                result += MGs[i].GetCost();
            }
        }
        return result;
    }

    public double GetOffensiveBV() {
        return (double) Math.floor( ( 67.0 * NumMGs * MGType.GetOffensiveBV() ) ) * 0.01 + NumMGs * MGType.GetOffensiveBV();
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return (double) Math.floor( ( 67.0 * NumMGs * MGType.GetOffensiveBV() ) ) * 0.01 + NumMGs * MGType.GetCurOffensiveBV( UseRear, UseTC, UseAES );
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    public double GetDefensiveBV() {
        double result = 0.0f;
        if( IsArmored() ) {
            result += ( Math.floor( ( 67.0 * NumMGs * MGType.GetOffensiveBV() ) ) * 0.01 ) * 0.05;
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
        return "MG Array (" + NumMGs + " " + MGType.CritName() + ")";
    }

    public String GetShortName() {
        return "MG Array (" + NumMGs + " " + MGType.ChatName() + ")";
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

    public double GetBVHeat() {
        return 0.0;
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

    public double GetBFDamageShort( boolean TC ) {
        double retval = 0;

        retval = GetDamageShort();

        return retval;
    }

    public double GetBFDamageMedium( boolean TC ) {
        double retval = 0;

        if ( GetRangeShort() > 3 )
            retval = GetDamageShort();

        return retval;
    }

    public double GetBFDamageLong( boolean TC ) {
        double retval = 0;

        if ( GetRangeShort() > 15 )
            retval = GetDamageShort();

        return retval;
    }

    public double GetBFDamageExtreme( boolean TC ) {
        double retval = 0;

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

    public int ClusterModShort() {
        return 0;
    }

    public int ClusterModMedium() {
        return 0;
    }

    public int ClusterModLong() {
        return 0;
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
        if( MGs[0] != null ) { MGs[0].MountRear( rear ); }
        if( MGs[1] != null ) { MGs[1].MountRear( rear ); }
        if( MGs[2] != null ) { MGs[2].MountRear( rear ); }
        if( MGs[3] != null ) { MGs[3].MountRear( rear ); }
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

    public boolean AddToTurret( ifTurret t ) {
        if( t.AddWeapon( this ) ) {
            Turret = t;
            return true;
        } else {
            Turret = null;
            return false;
        }
    }

    public void RemoveFromTurret( ifTurret t ) {
        t.RemoveWeapon( this );
        Turret = null;
    }

    public boolean IsTurreted() {
        if( Turret != null ) { return true; }
        return false;
    }

    public ifTurret GetTurret() {
        return Turret;
    }

    private RangedWeapon Copy( RangedWeapon b ) {
        RangedWeapon retval = new RangedWeapon( b.ActualName(), b.CritName(), b.LookupName(), b.MegaMekName( false ), b.GetType(), b.GetSpecials(), b.GetAvailability().Clone(), b.GetWeaponClass() );
        retval.SetStats( b.GetTonnage(), b.NumCrits(), b.NumCVSpaces(), b.GetCost(), b.GetOffensiveBV(), b.GetDefensiveBV() );
        retval.SetHeat( b.GetHeat() );
        retval.SetToHit( b.GetToHitShort(), b.GetToHitMedium(), b.GetToHitMedium() );
        retval.SetRange( b.GetRangeMin(), b.GetRangeShort(), b.GetRangeMedium(), b.GetRangeLong() );
        retval.SetDamage( b.GetDamageShort(), b.GetDamageMedium(), b.GetDamageLong(), b.IsCluster(), b.ClusterSize(), b.ClusterGrouping() );
        retval.SetAmmo( b.HasAmmo(), b.GetAmmoLotSize(), b.GetAmmoIndex(), b.SwitchableAmmo() );
        retval.SetAllocations(b.CanAllocHD(), b.CanAllocCT(), b.CanAllocTorso(), b.CanAllocArms(), b.CanAllocLegs(), b.CanSplit(), b.OmniRestrictActuators() );
        retval.SetWeapon( b.IsOneShot(), b.IsStreak(), b.IsUltra(), b.IsRotary(), b.IsExplosive(), b.IsTCCapable(), b.IsArrayCapable(), b.CanUseCapacitor(), false );
        retval.SetMissileFCS( b.IsFCSCapable(), b.GetFCSType() );
        retval.SetRequirements( b.RequiresFusion(), b.RequiresNuclear(), b.RequiresPowerAmps() );
        retval.SetChatName( b.ChatName() );
        retval.SetBookReference( b.BookReference() );
        return retval;
    }

    @Override
    public String toString() {
        return GetName();
    }
}

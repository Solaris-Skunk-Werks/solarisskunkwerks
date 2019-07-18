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

public class RangedWeapon extends abPlaceable implements ifWeapon {
    private AvailableCode AC;
    private PPCCapacitor Capacitor = null;
    private LaserInsulator Insulator = null;
    private MGArray CurArray = null;
    private ifMissileGuidance FCS = null;
    private ifTurret Turret = null;
    private String ActualName,
                   CritName,
                   MegaMekName,
                   LookupName,
                   ChatName,
                   Specials = "",
                   Type,
                   Manufacturer = "",
                   BookReference = "";
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
                    alloc_front = true,
                    alloc_sides = true,
                    alloc_rear = true,
                    alloc_turret = true,
                    alloc_body = true,
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
                    UsingCaseless = false,
                    UsingFCS = false,
                    CanOS = false,
                    CanIOS = false;
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
                CVSpace = 0,
                AmmoIndex = 0,
                ClusterSize = 1,
                ClusterGroup = 1,
                ClusterModShort = 0,
                ClusterModMedium = 0,
                ClusterModLong = 0,
                CaselessAmmoIDX = 0,
                FCSType = ifMissileGuidance.FCS_NONE,
                WeaponClass = ifWeapon.W_BALLISTIC;
    private double Tonnage = 0.0,
                  Cost = 0.0,
                  OffBV = 0.0,
                  DefBV = 0.0;

    public RangedWeapon( String actualname, String critname, String lookupname, String mmname, String type, String spec, AvailableCode a, int wepclass ) {
        CritName = critname;
        ActualName = actualname;
        LookupName = lookupname;
        MegaMekName = mmname;
        Type = type;
        AC = a;
        Specials = spec;
        WeaponClass = wepclass;
    }

    private RangedWeapon( RangedWeapon r ) {
        CritName = r.CritName;
        ActualName = r.ActualName;
        LookupName = r.LookupName;
        MegaMekName = r.MegaMekName;
        Type = r.Type;
        AC = r.AC.Clone();
        Specials = r.Specials;
        WeaponClass = r.WeaponClass;
        Tonnage = r.Tonnage;
        NumCrits = r.NumCrits;
        CVSpace = r.CVSpace;
        Cost = r.Cost;
        OffBV = r.OffBV;
        DefBV = r.DefBV;
        Heat = r.Heat;
        ToHitShort = r.ToHitShort;
        ToHitMedium = r.ToHitMedium;
        ToHitLong = r.ToHitLong;
        DamSht = r.DamSht;
        DamMed = r.DamMed;
        DamLng = r.DamLng;
        IsCluster = r.IsCluster;
        ClusterSize = r.ClusterSize;
        ClusterGroup = r.ClusterGroup;
        ClusterModShort = r.ClusterModShort;
        ClusterModMedium = r.ClusterModMedium;
        ClusterModLong = r.ClusterModLong;
        RngMin = r.RngMin;
        RngSht = r.RngSht;
        RngMed = r.RngMed;
        RngLng = r.RngLng;
        HasAmmo = r.HasAmmo;
        AmmoIndex = r.AmmoIndex;
        AmmoLotSize = r.AmmoLotSize;
        SwitchableAmmo = r.SwitchableAmmo;
        Alloc_HD = r.Alloc_HD;
        Alloc_CT = r.Alloc_CT;
        Alloc_Torso = r.Alloc_Torso;
        Alloc_Arms = r.Alloc_Arms;
        Alloc_Legs = r.Alloc_Legs;
        alloc_front = r.alloc_front;
        alloc_sides = r.alloc_sides;
        alloc_rear = r.alloc_rear;
        alloc_turret = r.alloc_turret;
        alloc_body = r.alloc_body;
        CanSplit = r.CanSplit;
        OmniRestrict = r.OmniRestrict;
        RequiresFusion = r.RequiresFusion;
        RequiresNuclear = r.RequiresNuclear;
        RequiresPowerAmps = r.RequiresPowerAmps;
        OneShot = r.OneShot;
        Streak = r.Streak;
        Ultra = r.Ultra;
        Rotary = r.Rotary;
        Explosive = r.Explosive;
        TCCapable = r.TCCapable;
        CanUseCapacitor = r.CanUseCapacitor;
        ArrayCapable = r.ArrayCapable;
        CanUseInsulator = r.CanUseInsulator;
        CanUseCaseless = r.CanUseCaseless;
        CaselessAmmoIDX = r.CaselessAmmoIDX;
        CanUseFCS = r.CanUseFCS;
        FCSType = r.FCSType;
        BookReference = r.BookReference;
        ChatName = r.ChatName;
        SetBattleForceAbilities( r.GetBattleForceAbilities() );
    }

    public void SetStats( double tons, int crits, int vspace, double cost, double obv, double dbv ) {
        Tonnage = tons;
        NumCrits = crits;
        CVSpace = vspace;
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

    public void SetClusterMods( int srt, int med, int lng ) {
        ClusterModShort = srt;
        ClusterModMedium = med;
        ClusterModLong = lng;
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

    public void SetCVAllocs( boolean front, boolean sides, boolean rear, boolean turret, boolean body ) {
        alloc_front = front;
        alloc_sides = sides;
        alloc_rear = rear;
        alloc_turret = turret;
        alloc_body = body;
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

    @Override
    public void SetManufacturer( String n ) {
        Manufacturer = n;
    }

    public void SetBookReference( String s ) {
        BookReference = s;
    }

    public void SetChatName( String s ) {
        ChatName = s;
    }

    public String ActualName() {
        return ActualName;
    }

    public String CritName() {
        String retval = CritName;
        if( UsingCapacitor ) {
            retval += " + PPC Capacitor";
        }
        if( UsingInsulator ) {
            retval += " (Insulated)";
        }
        if( UsingCaseless ) {
            retval += " (Caseless)";
        }
        if( IsTurreted() ) {
            retval = "(T) " + retval;
        } else {
            if( InArray ) {
                if( CurArray.IsTurreted() ) {
                    retval = "(T) " + retval;
                }
            }
        }
        if( MountedRear ) {
            return "(R) " + retval;
        } else {
            return retval;
        }
    }

    public String LookupName() {
        String retval = LookupName;
        if( UsingCapacitor ) {
            retval += " + PPC Capacitor";
        }
        if( UsingInsulator ) {
            retval += " (Insulated)";
        }
        if( UsingCaseless ) {
            retval += " (Caseless)";
        }
        if( IsTurreted() ) {
            retval = "(T) " + retval;
        }
        if( MountedRear ) {
            return "(R) " + retval;
        } else {
            return retval;
        }
    }

    public String ChatName() {
        return ChatName;
    }

    public String MegaMekName( boolean UseRear ) {
        if( UseRear ) {
            return MegaMekName + " (R)";
        } else {
            return MegaMekName;
        }
    }

    public String BookReference() {
        return BookReference;
    }

    public String GetType() {
        return Type;
    }

    public String GetSpecials() {
        if( UsingCaseless ) {
            return "-";
        } else {
            return Specials;
        }
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
        if( UsingInsulator ) {
            retval.Combine( Insulator.GetAvailability() );
        }
        return retval;
    }

    @Override
    public int NumCrits() {
        return NumCrits;
    }

    public int NumCVSpaces() {
        int retval = CVSpace;
        if ( UsingCapacitor ) retval += GetCapacitor().NumCVSpaces();
        if ( UsingInsulator ) retval += GetInsulator().NumCVSpaces();
        if ( UsingFCS ) { retval += ((abPlaceable) FCS).NumCVSpaces(); }
        return retval;
    }

    @Override
    public double GetTonnage() {
        double retval = Tonnage;
        if( IsArmored() ) {
            retval += NumCrits * 0.5;
        }
        if( UsingCapacitor ) {
            retval += Capacitor.GetTonnage();
        }
        if( UsingInsulator ) {
            retval += Insulator.GetTonnage();
        }
        if( UsingFCS ) {
            retval += ((abPlaceable) FCS).GetTonnage();
        }
        return retval;
    }

    public double GetCost() {
        double retval = Cost;
        if( IsArmored() ) {
            retval += ( 150000.0 * NumCrits );
        }
        if( UsingCapacitor ) {
            retval += Capacitor.GetCost();
        }
        if( UsingInsulator ) {
            retval += Insulator.GetCost();
        }
        if( UsingFCS ) {
            retval += ((abPlaceable) FCS).GetCost();
        }
        return retval;
    }

    @Override
    public double GetOffensiveBV() {
        double retval = OffBV;
        if( UsingFCS ) {
            retval = ( Math.round( OffBV * FCS.GetBVMultiplier() * 100.0 ) ) * 0.01;
        }
        if( UsingCapacitor ) {
            // round off the capacitor BV total, as this is how Tac Ops does it.
            retval = Math.round( OffBV + Capacitor.GetOffensiveBV() );
        }
        return retval;
    }

    @Override
    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return GetCurOffensiveBV(UseRear, UseTC, UseAES, false);
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRoboticCockpit ) {
        double retval = GetOffensiveBV();
        if( UseAES ) {
            retval *= 1.25;
        }
        if( UseTC && TCCapable ) {
            retval *= 1.25;
        }
        if ( UseRoboticCockpit ) {
            retval *= 0.8;
        }
        if( IsTurreted() ) {
            return retval;
        } else {
            if( UseRear ) {
                if( MountedRear ) {
                    return retval;
                } else {
                    return retval * 0.5;
                }
            } else {
                if( MountedRear ) {
                    return retval * 0.5;
                } else {
                    return retval;
                }
            }
        }
    }

    public double GetDefensiveBV() {
        double retval = 0.0;
        if( UsingFCS ) {
            retval = ( Math.round( DefBV * FCS.GetBVMultiplier() * 100.0 ) ) * 0.01;
            retval += ((abPlaceable) FCS).GetDefensiveBV();
        } else {
            retval = DefBV;
        }
        if( UsingCapacitor ) {
            retval += Capacitor.GetDefensiveBV();
        }
        int crits = NumCrits();
        if( UsingCapacitor ) { crits += Capacitor.NumCrits(); }
        if( UsingInsulator ) { crits += Insulator.NumCrits(); }
        if( UsingFCS ) { crits += ((abPlaceable) FCS).NumCrits(); }
        if( IsArmored() ) {
            retval += (( GetOffensiveBV() + retval ) * 0.05 * crits );
        }
        return retval;
    }

    public int GetHeat() {
        if( UsingCapacitor ) {
            return Heat + 5;
        }
        if( UsingInsulator ) {
            return Heat - 1;
        }
        return Heat;
    }

    public double GetBVHeat() {
        double retval = Heat;
        if( UsingCapacitor ) {
            retval += 5;
        }
        if( UsingInsulator ) {
            retval -= 1;
        }
        if( Rotary ) { retval *= 6; }
        if( Ultra ) { retval *= 2; }
        if( OneShot ) { retval *= 0.25; }
        if( Streak && this.ChatName.contains( "Streak SRM" ) ) { retval *= 0.5; }
        if( retval < 0 ) { retval = 0; }
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
        // unless they're turreted
        if( IsTurreted() ) { return false; }
        return true;
    }

    @Override
    public void MountRear( boolean rear ) {
        if( IsInArray() ) {
            if( GetMyArray().IsMountedRear() != rear ) {
                GetMyArray().MountRear( rear );
            }
            MountedRear = rear;
        } else {
            MountedRear = rear;
        }
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

    public int ClusterModShort() {
        return ClusterModShort;
    }

    public int ClusterModMedium() {
        return ClusterModMedium;
    }

    public int ClusterModLong() {
        return ClusterModLong;
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
        if( UsingCapacitor ) { return true; }
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
        if( UsingCaseless ) {
            return AmmoLotSize * 2;
        } else {
            return AmmoLotSize;
        }
    }

    public int GetAmmoIndex() {
        if( UsingCaseless ) {
            return CaselessAmmoIDX;
        } else {
            return AmmoIndex;
        }
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
    public boolean CanAllocCVBody() {
        return alloc_body;
    }
    
    @Override
    public boolean CanAllocCVFront() {
        return alloc_front;
    }

    @Override
    public boolean CanAllocCVRear() {
        return alloc_rear;
    }

    @Override
    public boolean CanAllocCVSide() {
        return alloc_sides;
    }

    @Override
    public boolean CanAllocCVTurret() {
        return alloc_turret;
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

    @Override
    public void ArmorComponent( boolean armor ) {
        // armor or unarmor the component
        Armored = armor;
        if( UsingCapacitor ) {
            Capacitor.ArmorComponent( armor );
        }
        if( UsingInsulator ) {
            Insulator.ArmorComponent( armor );
        }
        if( UsingFCS ) {
            ((abPlaceable) FCS).ArmorComponent( armor );
        }
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

    public void UseInsulator( boolean l ) {
        if( l ) {
            Insulator = new LaserInsulator( this );
            UsingInsulator = true;
        } else {
            Insulator = null;
            UsingInsulator = false;
        }
    }

    public boolean IsUsingInsulator() {
        return UsingInsulator;
    }

    public LaserInsulator GetInsulator() {
        return Insulator;
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

    public boolean CanUseCaselessAmmo() {
        return CanUseCaseless;
    }

    public boolean IsCaseless() {
        return UsingCaseless;
    }

    public void SetCaseless( boolean b ) {
        if( ! CanUseCaseless ) { return; }
        UsingCaseless = b;
    }

    public int GetCaselessAmmoIDX() {
        return CaselessAmmoIDX;
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

    public boolean AddToTurret( ifTurret t ) {
        if( InArray ) {
            if( CurArray.AddToTurret( t ) ) {
                return true;
            } else {
                return false;
            }
        }
        if( t.AddWeapon( this ) ) {
            Turret = t;
            return true;
        } else {
            Turret = null;
            return false;
        }
    }

    public void RemoveFromTurret( ifTurret t ) {
        if( InArray ) {
            CurArray.RemoveFromTurret( t );
            return;
        }
        t.RemoveWeapon( this );
        Turret = null;
    }

    public boolean IsTurreted() {
        if( InArray ) {
            return CurArray.IsTurreted();
        }
        if( Turret != null ) { return true; }
        return false;
    }

    public ifTurret GetTurret() {
        return Turret;
    }

    public RangedWeapon Clone() {
        return new RangedWeapon( this );
    }

    @Override
    public String toString() {
        String retval = CritName;
        if( MountedRear )
            retval = "(R) " + retval;
            
        if ( UsingFCS ) 
            retval += " w/" + GetFCS().toString();
            
        return retval;
    }
}

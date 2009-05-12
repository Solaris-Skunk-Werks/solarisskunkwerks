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

import java.util.Vector;
import ssw.*;

public class EquipmentFactory {
    // big class for holding and farming out equipment
    private Vector Equipment = new Vector(),
                   IndustrialEquipment = new Vector(),
                   PhysicalWeapons = new Vector(),
                   Ammo,
                   RangedWeapons;

    public EquipmentFactory( Vector rweapons, Vector ammo, Mech m ) {
        Ammo = ammo;
        RangedWeapons = rweapons;
        BuildMGArrays();
        BuildEquipment();
        BuildIndustrialEquipment();
        BuildPhysicals( m );
    }

    public abPlaceable GetCopy( abPlaceable p, Mech m ) {
        // creates an equipment copy of p
        abPlaceable retval = null;

        if( p instanceof IndustrialEquipment ) {
            IndustrialEquipment e = (IndustrialEquipment) p;
            retval = new IndustrialEquipment( e.GetCritName(), e.GetLookupName(), e.GetType(), e.GetAvailability(), e.getValidator(), e.getValidationFalseMessage() );
            ((Equipment) retval).SetAmmo(e.HasAmmo(), e.GetAmmo(), e.GetAmmoIndex());
            ((Equipment) retval).SetMegaMekName(e.GetMMName(false));
            ((Equipment) retval).SetHeat(e.GetHeat());
            ((Equipment) retval).SetRange(e.GetShortRange(), e.GetMediumRange(), e.GetLongRange());
            ((Equipment) retval).SetStats(e.NumCrits(), e.GetTonnage(), e.GetCost(), e.GetOffensiveBV(), e.GetDefensiveBV(), e.GetSpecials());
            ((Equipment) retval).SetAllocs(e.CanAllocHD(), e.CanAllocCT(), e.CanAllocTorso(), e.CanAllocArms(), e.CanAllocLegs());
            ((Equipment) retval).SetSplitable(e.CanSplit());
            ((Equipment) retval).SetMountableRear(e.CanMountRear());
        } else if( p instanceof Equipment ) {
            Equipment e = (Equipment) p;
            retval = new Equipment( e.GetCritName(), e.GetLookupName(), e.GetType(), e.GetAvailability() );
            ((Equipment) retval).SetAmmo(e.HasAmmo(), e.GetAmmo(), e.GetAmmoIndex());
            ((Equipment) retval).SetMegaMekName(e.GetMMName(false));
            ((Equipment) retval).SetHeat(e.GetHeat());
            ((Equipment) retval).SetRange(e.GetShortRange(), e.GetMediumRange(), e.GetLongRange());
            ((Equipment) retval).SetStats(e.NumCrits(), e.GetTonnage(), e.GetCost(), e.GetOffensiveBV(), e.GetDefensiveBV(), e.GetSpecials());
            ((Equipment) retval).SetAllocs(e.CanAllocHD(), e.CanAllocCT(), e.CanAllocTorso(), e.CanAllocArms(), e.CanAllocLegs());
            ((Equipment) retval).SetSplitable(e.CanSplit());
            ((Equipment) retval).SetMountableRear(e.CanMountRear());
            ((Equipment) retval).SetExplosive(e.IsExplosive());
        } else if( p instanceof ModularArmor ) {
            retval = new ModularArmor();
        } else if( p instanceof Ammunition ) {
            Ammunition c = (Ammunition) p;
            retval = new Ammunition( c.GetCritName(), c.GetLookupName(), c.GetMMName( false ), c.GetAmmoIndex(), c.GetAvailability() );
            ((Ammunition) retval).SetStats( c.GetTonnage(), c.GetCost(), c.GetOffensiveBV(), c.GetDefensiveBV() );
            ((Ammunition) retval).SetToHit( c.GetToHitShort(), c.GetToHitMedium(), c.GetToHitLong() );
            ((Ammunition) retval).SetDamage( c.GetDamageShort(), c.GetDamageMedium(), c.GetDamageLong(), c.IsCluster(), c.ClusterSize(), c.ClusterGrouping() );
            ((Ammunition) retval).SetRange( c.GetMinRange(), c.GetShortRange(), c.GetMediumRange(), c.GetLongRange() );
            ((Ammunition) retval).SetAmmo( c.GetLotSize(), c.IsExplosive(), c.GetWeaponClass(), c.GetFCSType() );
            ((Ammunition) retval).SetPrintName( c.GetBasePrintName() );
        } else if( p instanceof RangedWeapon ) {
            RangedWeapon r = (RangedWeapon) p;
            retval = new RangedWeapon( r.GetCritName(), r.GetLookupName(), r.GetMMName( false ), r.GetType(), r.GetSpecials(), r.GetAvailability(), r.GetWeaponClass() );
            ((RangedWeapon) retval).SetStats( r.GetTonnage(), r.NumCrits(), r.GetCost(), r.GetOffensiveBV(), r.GetDefensiveBV() );
            ((RangedWeapon) retval).SetHeat( r.GetHeat() );
            ((RangedWeapon) retval).SetToHit( r.GetToHitShort(), r.GetToHitMedium(), r.GetToHitLong() );
            ((RangedWeapon) retval).SetDamage( r.GetDamageShort(), r.GetDamageMedium(), r.GetDamageLong(), r.IsCluster(), r.ClusterSize(), r.ClusterGrouping() );
            ((RangedWeapon) retval).SetRange( r.GetRangeMin(), r.GetRangeShort(), r.GetRangeMedium(), r.GetRangeLong() );
            ((RangedWeapon) retval).SetAmmo( r.HasAmmo(), r.GetAmmoLotSize(), r.GetAmmoIndex(), r.SwitchableAmmo() );
            ((RangedWeapon) retval).SetAllocations( r.CanAllocHD(), r.CanAllocCT(), r.CanAllocTorso(), r.CanAllocArms(), r.CanAllocLegs(), r.CanSplit(), r.OmniRestrictActuators() );
            ((RangedWeapon) retval).SetRequirements( r.RequiresFusion(), r.RequiresNuclear(), r.RequiresPowerAmps() );
            ((RangedWeapon) retval).SetWeapon( r.IsOneShot(), r.IsStreak(), r.IsUltra(), r.IsRotary(), r.IsExplosive(), r.IsTCCapable(), r.IsArrayCapable(), r.CanUseCapacitor(), r.CanUseInsulator() );
            ((RangedWeapon) retval).SetMissileFCS( r.IsFCSCapable(), r.GetFCSType() );
            ((RangedWeapon) retval).SetPrintName( r.GetPrintName() );
        } else if( p instanceof IndustrialPhysicalWeapon ) {
            IndustrialPhysicalWeapon w = (IndustrialPhysicalWeapon) p;
            switch( w.GetPWClass() ) {
                case PhysicalWeapon.PW_CLASS_TALON:
                    retval = new Talons ( m );
                    break;
                default:
                    retval = new IndustrialPhysicalWeapon( w.GetName(), w.GetLookupName(), w.GetMMName( false ), m, w.GetAvailability() );
                    ((PhysicalWeapon) retval).SetStats( w.GetTonMult(), w.GetCritMult(), w.GetTonAdd(), w.GetCritAdd() );
                    ((PhysicalWeapon) retval).SetDamage( w.GetDamageMult(), w.GetDamageAdd() );
                    ((PhysicalWeapon) retval).SetSpecials( w.GetType(), w.GetSpecials(), w.GetCostMult(), w.GetCostAdd(), w.GetBVMult(), w.GetBVAdd(), w.GetDefBV(), w.GetRounding() );
                    ((PhysicalWeapon) retval).SetToHit( w.GetToHitShort(), w.GetToHitMedium(), w.GetToHitLong() );
                    (retval).AddMechModifier( w.GetMechModifier() );
                    ((PhysicalWeapon) retval).SetHeat( w.GetHeat() );
                    ((PhysicalWeapon) retval).SetRequiresHand( w.RequiresHand() );
                    ((PhysicalWeapon) retval).SetRequiresLowerArm( w.RequiresLowerArm() );
                    ((PhysicalWeapon) retval).SetReplacesHand( w.ReplacesHand() );
                    ((PhysicalWeapon) retval).SetPWClass( w.GetPWClass() );
                    ((PhysicalWeapon) retval).SetRequirements( w.RequiresNuclear(), w.RequiresFusion(), w.RequiresPowerAmps() );
                    ((PhysicalWeapon) retval).SetAllocations( w.CanAllocHD(), w.CanAllocCT(), w.CanAllocTorso(), w.CanAllocArms(), w.CanAllocLegs(), false );
                    break;
            }
        } else if( p instanceof PhysicalWeapon ) {
            PhysicalWeapon w = (PhysicalWeapon) p;
            switch( w.GetPWClass() ) {
                case PhysicalWeapon.PW_CLASS_TALON:
                    retval = new Talons ( m );
                    break;
                default:
                    retval = new PhysicalWeapon( w.GetName(), w.GetLookupName(), w.GetMMName( false ), m, w.GetAvailability() );
                    ((PhysicalWeapon) retval).SetStats( w.GetTonMult(), w.GetCritMult(), w.GetTonAdd(), w.GetCritAdd() );
                    ((PhysicalWeapon) retval).SetDamage( w.GetDamageMult(), w.GetDamageAdd() );
                    ((PhysicalWeapon) retval).SetSpecials( w.GetType(), w.GetSpecials(), w.GetCostMult(), w.GetCostAdd(), w.GetBVMult(), w.GetBVAdd(), w.GetDefBV(), w.GetRounding() );
                    ((PhysicalWeapon) retval).SetToHit( w.GetToHitShort(), w.GetToHitMedium(), w.GetToHitLong() );
                    (retval).AddMechModifier( w.GetMechModifier() );
                    ((PhysicalWeapon) retval).SetHeat( w.GetHeat() );
                    ((PhysicalWeapon) retval).SetRequiresHand( w.RequiresHand() );
                    ((PhysicalWeapon) retval).SetRequiresLowerArm( w.RequiresLowerArm() );
                    ((PhysicalWeapon) retval).SetReplacesHand( w.ReplacesHand() );
                    ((PhysicalWeapon) retval).SetPWClass( w.GetPWClass() );
                    ((PhysicalWeapon) retval).SetRequirements( w.RequiresNuclear(), w.RequiresFusion(), w.RequiresPowerAmps() );
                    ((PhysicalWeapon) retval).SetAllocations( w.CanAllocHD(), w.CanAllocCT(), w.CanAllocTorso(), w.CanAllocArms(), w.CanAllocLegs(), false );
                    break;
            }
        } else if( p instanceof MGArray ) {
            MGArray a = (MGArray) p;
            retval = new MGArray( ((RangedWeapon) GetCopy( a.GetMGType(), null )), a.GetNumMGs(), a.GetMGTons(), a.IsClan(), a.GetAvailability() );
        }
        if( retval == null ) {
            return null;
        }
        if( p.GetExclusions() != null ) {
            retval.SetExclusions( p.GetExclusions() );
        }
        return retval;
    }

    public Object[] GetAmmo( int key, Mech m ) {
        // returns an array containing all the ammunition for the specified 
        // weapon key.
        Vector v = new Vector(),
                test;

        // find the ammunition
        for( int i = 0; i < Ammo.size(); i++ ) {
            if( key == ((Ammunition) Ammo.get( i )).GetAmmoIndex() ) {
                Ammunition a = (Ammunition) Ammo.get( i );
                if( v.contains( a ) ) { break; }
                AvailableCode AC = a.GetAvailability();
                // discard any ammo that is out of era
                if( CommonTools.IsAllowed( AC, m ) ) {
                    v.add( a );
                }
            }
        }

        return v.toArray();
    }

    public Object[] GetAmmo( int[] key, Mech m ) {
        // returns an array containing all the ammunition for the specified 
        // weapon keys.  The keys are the weapon's lookup names
        Vector v = new Vector(),
                test;
        Object o;

        // find the ammunition
        for( int i = 0; i < Ammo.size(); i++ ) {
            for( int j = 0; j < key.length; j++ ) {
                o = Ammo.get( i );
                if( key[j] == ((Ammunition) o).GetAmmoIndex() ) {
                    Ammunition a = (Ammunition) Ammo.get( i );
                    if( v.contains( a ) ) { break; }
                    AvailableCode AC = a.GetAvailability();
                    // discard any ammo that is out of era
                    if( CommonTools.IsAllowed( AC, m ) ) {
                        v.add( a );
                    }
                }
            }
        }

        return v.toArray();
    }

    public Object[] GetArtillery( Mech m ) {
        // returns an array based on the given specifications of era and year
        Vector RetVal = new Vector();
        abPlaceable p;
        AvailableCode AC;

        for( int i = 0; i < RangedWeapons.size(); i++ ) {
            p = (abPlaceable) RangedWeapons.get( i );
            if( p instanceof RangedWeapon ) {
                if( ((RangedWeapon) p).GetWeaponClass() == RangedWeapon.W_ARTILLERY ) {
                    AC = p.GetAvailability();
                    if( CommonTools.IsAllowed( AC, m ) ) {
                        RetVal.add( p );
                    }
                }
            }
        }

        if( RetVal.size() < 1 ) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Object[] GetBallisticWeapons( Mech m ) {
        Vector RetVal = new Vector();
        abPlaceable p;
        AvailableCode AC;

        for( int i = 0; i < RangedWeapons.size(); i++ ) {
            p = (abPlaceable) RangedWeapons.get( i );
            if( p instanceof RangedWeapon ) {
                if( ((RangedWeapon) p).GetWeaponClass() == RangedWeapon.W_BALLISTIC ) {
                    AC = p.GetAvailability();
                    if( CommonTools.IsAllowed( AC, m ) ) {
                        RetVal.add( p );
                    }
                }
            } else if( p instanceof MGArray ) {
                AC = p.GetAvailability();
                if( CommonTools.IsAllowed( AC, m ) ) {
                    RetVal.add( p );
                }
            }
        }

        if( RetVal.size() < 1 ) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Object[] GetEnergyWeapons( Mech m ) {
        // returns an array based on the given specifications of era and year
        Vector RetVal = new Vector();
        abPlaceable p;
        AvailableCode AC;

        for( int i = 0; i < RangedWeapons.size(); i++ ) {
            p = (abPlaceable) RangedWeapons.get( i );
            if( p instanceof RangedWeapon ) {
                if( ((RangedWeapon) p).GetWeaponClass() == RangedWeapon.W_ENERGY ) {
                        AC = p.GetAvailability();
                    if( CommonTools.IsAllowed( AC, m ) ) {
                        RetVal.add( p );
                    }
                }
            }
        }

        if( RetVal.size() < 1 ) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Object[] GetMissileWeapons( Mech m ) {
        // returns an array based on the given specifications of era and year
        Vector RetVal = new Vector();
        abPlaceable p;
        AvailableCode AC;

        for( int i = 0; i < RangedWeapons.size(); i++ ) {
            p = (abPlaceable) RangedWeapons.get( i );
            if( p instanceof RangedWeapon ) {
                if( ((RangedWeapon) p).GetWeaponClass() == RangedWeapon.W_MISSILE ) {
                    AC = p.GetAvailability();
                    if( CommonTools.IsAllowed( AC, m ) ) {
                        RetVal.add( p );
                    }
                }
            }
        }

        if( RetVal.size() < 1 ) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Object[] GetPhysicalWeapons( Mech m ) {
        // returns an array based on the given specifications of era and year
        Vector RetVal = new Vector();
        abPlaceable hatchet = null;

        // do this a little differently.
        // the Inner Sphere portion of this will be a bit of a hack, but the
        // reasons are evident.  Retractable Blades were never used until
        // 3070 or thereabouts, and hatchets are code F during the
        // succession wars because only one mech used them.

        for( int i = 0; i < PhysicalWeapons.size(); i++ ) {
            RetVal.add( PhysicalWeapons.get( i ) );
        }

        // now weed out things that shouldn't be there
        for( int i = RetVal.size() - 1; i >= 0; i-- ) {
            abPlaceable a = (abPlaceable) RetVal.get( i );
            AvailableCode AC = a.GetAvailability();

            if( a.GetCritName().equals( "Hatchet" ) ) {
                // grab the Hatchet, we'll want to filter it specifically
                hatchet = a;
            }
            if( ! CommonTools.IsAllowed( AC, m )) {
                RetVal.remove( i );
            }
        }

        if( m.GetEra() >= AvailableCode.ERA_SUCCESSION ) {
            if( ! RetVal.contains( hatchet ) ) {
                RetVal.add( hatchet );
            }
        }
        if( RetVal.size() < 1 ) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Object[] GetEquipment( Mech m ) {
        // returns an array based on the given specifications of era and year
        Vector RetVal = new Vector();
        abPlaceable p;
        AvailableCode AC;

        for (int i = 0; i < Equipment.size(); i++) {
            p = (abPlaceable) Equipment.get(i);
            AC = p.GetAvailability();
            if (CommonTools.IsAllowed(AC, m)) {
                RetVal.add(p);
            }
        }

        for (int i = 0; i < IndustrialEquipment.size(); i++) {
            p = (abPlaceable) IndustrialEquipment.get(i);
            AC = p.GetAvailability();
            if (CommonTools.IsAllowed(AC, m)) {
                RetVal.add(p);
            }
        }

        if (RetVal.size() < 1) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Ammunition GetAmmoByName( String name, Mech m ) {
        // returns an ammunition based on the given name
        for( int i = 0; i < Ammo.size(); i++ ) {
            if( ((abPlaceable) Ammo.get( i )).GetLookupName().equals( name ) ) {
                return (Ammunition) GetCopy( (abPlaceable) Ammo.get( i ), m );
            }
        }
        return null;
    }

    public Equipment GetEquipmentByName(String name, Mech m ) {
        for (int i = 0; i < Equipment.size(); i++) {
            if (((abPlaceable) Equipment.get(i)).GetLookupName().equals(name)) {
                return (Equipment) GetCopy((abPlaceable) Equipment.get(i), m);
            }
        }

        for (int i = 0; i < IndustrialEquipment.size(); i++) {
            if (((abPlaceable) IndustrialEquipment.get(i)).GetLookupName().equals(name)) {
                return (Equipment) GetCopy((abPlaceable) IndustrialEquipment.get(i), m);
            }
        }
        return null;
    }

    public PhysicalWeapon GetPhysicalWeaponByName( String name, Mech m ) {
        for( int i = 0; i < PhysicalWeapons.size(); i++ ) {
            if( ((abPlaceable) PhysicalWeapons.get( i )).GetLookupName().equals( name ) ) {
                PhysicalWeapon p = (PhysicalWeapon) GetCopy( (abPlaceable) PhysicalWeapons.get( i ), m );
                p.SetOwner( m );
                if( p instanceof IndustrialPhysicalWeapon ) {
                    ((IndustrialPhysicalWeapon) p).resetAllocations( m );
                }
                return p;
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public abPlaceable GetRangedWeaponByName( String name, Mech m ) {
        // searches the weapon database for the named item and returns it
        for( int i = 0; i < RangedWeapons.size(); i++ ) {
            if( ((abPlaceable) RangedWeapons.get( i )).GetLookupName().equals( name ) ) {
                return (abPlaceable) GetCopy( (abPlaceable) RangedWeapons.get( i ), m );
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public void BuildMGArrays() {
        abPlaceable addBW;
        MGArray addMGA;
        AvailableCode a;

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'F' );
        a.SetISDates( 0, 0, false, 3068, 0, 0, false, false );
        a.SetISFactions( "", "", "FS", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        // MGA light machine gun
        addBW = GetRangedWeaponByName( "(IS) Light Machine Gun", null );

        // LMGA 2
        addMGA = new MGArray( ((RangedWeapon) addBW), 2, 0.5f, false, a);
        RangedWeapons.add(addMGA);

        // LMGA 3
        addMGA = new MGArray( ((RangedWeapon) addBW), 3, 0.5f, false, a);
        RangedWeapons.add(addMGA);

        // LMGA 4
        addMGA = new MGArray( ((RangedWeapon) addBW), 4, 0.5f, false, a);
        RangedWeapons.add(addMGA);

        // machine gun
        addBW = GetRangedWeaponByName( "(IS) Machine Gun", null );

        // MGA 2
        addMGA = new MGArray(((RangedWeapon) addBW), 2, 0.5f, false, a);
        RangedWeapons.add(addMGA);

        // MGA 3
        addMGA = new MGArray(((RangedWeapon) addBW), 3, 0.5f, false, a);
        RangedWeapons.add(addMGA);

        // MGA 4
        addMGA = new MGArray(((RangedWeapon) addBW), 4, 0.5f, false, a);
        RangedWeapons.add(addMGA);

        // heavy machine gun
        addBW = GetRangedWeaponByName( "(IS) Heavy Machine Gun", null );

        // HMGA 2
        addMGA = new MGArray(((RangedWeapon) addBW), 2, 1.0f, false, a);
        RangedWeapons.add(addMGA);

        // HMGA 3
        addMGA = new MGArray(((RangedWeapon) addBW), 3, 1.0f, false, a);
        RangedWeapons.add(addMGA);

        // HMGA 4
        addMGA = new MGArray(((RangedWeapon) addBW), 4, 1.0f, false, a);
        RangedWeapons.add(addMGA);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'E', 'X', 'X', 'E' );
        a.SetCLDates( 0, 0, false, 3069, 0, 0, false, false );
        a.SetCLFactions( "", "", "CDS", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        // LMGA light machine gun
        addBW = GetRangedWeaponByName( "(CL) Light Machine Gun", null );

        // LMGA 2
        addMGA = new MGArray( ((RangedWeapon) addBW), 2, 0.25f, true, a );
        RangedWeapons.add( addMGA );

        // LMGA 3
        addMGA = new MGArray( ((RangedWeapon) addBW), 3, 0.25f, true, a );
        RangedWeapons.add( addMGA );

        // LMGA 4
        addMGA = new MGArray( ((RangedWeapon) addBW), 4, 0.25f, true, a );
        RangedWeapons.add( addMGA );

        // MGA machine gun
        addBW = GetRangedWeaponByName( "(CL) Machine Gun", null );

        // MGA 2
        addMGA = new MGArray( ((RangedWeapon) addBW), 2, 0.25f, true, a );
        RangedWeapons.add( addMGA );

        // MGA 3
        addMGA = new MGArray( ((RangedWeapon) addBW), 3, 0.25f, true, a );
        RangedWeapons.add( addMGA );

        // MGA 4
        addMGA = new MGArray( ((RangedWeapon) addBW), 4, 0.25f, true, a );
        RangedWeapons.add( addMGA );

        // HMGA heavy machine gun
        addBW = GetRangedWeaponByName( "(CL) Heavy Machine Gun", null );

        // HMGA 2
        addMGA = new MGArray( ((RangedWeapon) addBW), 2, 0.5f, true, a );
        RangedWeapons.add( addMGA );

        // HMGA 3
        addMGA = new MGArray( ((RangedWeapon) addBW), 3, 0.5f, true, a );
        RangedWeapons.add( addMGA );

        // HMGA 4
        addMGA = new MGArray( ((RangedWeapon) addBW), 4, 0.5f, true, a );
        RangedWeapons.add( addMGA );
    }

    public void BuildPhysicals( Mech m ) {
        // this method rebuilds the physical weapons if the form's CurMech changes
        AvailableCode a;
        PhysicalWeapon addPW;

        PhysicalWeapons.clear();

        // hatchet
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'X', 'F', 'D' );
        a.SetISDates( 0, 0, false, 3022, 0, 0, false, false );
        a.SetISFactions( "", "", "LC", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Hatchet", "Hatchet", "Hatchet", m, a );
        addPW.SetStats( 0.06666f, 0.06666f, 0.0f, 0 );
        addPW.SetDamage( 0.2f, 0 );
        addPW.SetSpecials( "PA", "-", 5000.0f, 0.0f, 1.5f, 0.0f, 0.0f, false );
        addPW.SetToHit( -1, -1, -1 );
        PhysicalWeapons.add( addPW );

        // sword
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'X', 'X', 'D' );
        a.SetISDates( 0, 0, false, 3058, 0, 0, false, false );
        a.SetISFactions( "", "", "DC", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Sword", "Sword", "Sword", m, a );
        addPW.SetStats( 0.05f, 0.06666f, 0.0f, 0 );
        addPW.SetDamage( 0.1f, 1 );
        addPW.SetSpecials( "PA", "-", 10000.0f, 0.0f, 1.725f, 0.0f, 0.0f, true );
        addPW.SetToHit( -2, -2, -2 );
        PhysicalWeapons.add( addPW );

        // retractable blade
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'F', 'D', 'D' );
        a.SetISDates( 0, 0, false, 2420, 0, 0, false, false );
        a.SetISFactions( "", "", "TH", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Retractable Blade", "Retractable Blade", "Retractable Blade", m, a );
        addPW.SetStats( 0.05f, 0.05f, 0.5f, 1 );
        addPW.SetDamage( 0.1f, 0 );
        addPW.SetSpecials( "PA", "-", 10000.0f, 10000.0f, 1.725f, 0.0f, 0.0f, true );
        addPW.SetToHit( -2, -2, -2 );
        PhysicalWeapons.add( addPW );

        // chain whip
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'C', 'X', 'X', 'F' );
        a.SetISDates( 3069, 3071, true, 3071, 0, 0, false, false );
        a.SetISFactions( "LA", "WB", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Chain Whip", "Chain Whip", "Chain Whip", m, a );
        addPW.SetStats(0.0f, 0.0f, 3.0f, 2);
        addPW.SetDamage(0.0f, 1);
        addPW.SetSpecials("PA", "-", 0.0f, 120000.0f, 1.725f, 0.0f, 0.0f, false);
        addPW.SetToHit(-2, -2, -2);
        PhysicalWeapons.add( addPW );

        // claws
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'X', 'F', 'E' );
        a.SetISDates( 0, 0, false, 3060, 0, 0, false, false );
        a.SetISFactions( "", "", "LA", "" );
        a.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Claws", "Claws", "Claws", m, a );
        addPW.SetStats(0.06666f, 0.06666f, 0.0f, 0);
        addPW.SetDamage(0.1428f, 0);
        addPW.SetSpecials("PA", "-", 2800.0f, 0.0f, 1.275f, 0.0f, 0.0f, false);
        addPW.SetToHit(1, 1, 1);
        addPW.SetReplacesHand(true);
        addPW.SetRequiresLowerArm( true );
        PhysicalWeapons.add( addPW );

        // flail
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'X', 'X', 'E' );
        a.SetISDates( 3054, 3057, true, 3057, 0, 0, false, false );
        a.SetISFactions( "FC", "FC", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Flail", "Flail", "Flail", m, a );
        addPW.SetStats(0.0f, 0.0f, 5.0f, 4);
        addPW.SetDamage(0.0f, 9);
        addPW.SetSpecials("PA", "-", 0.0f, 110000.0f, 0.0f, 11.0f, 0.0f, false);
        addPW.SetToHit(1, 1, 1);
        addPW.SetReplacesHand(true);
        addPW.SetRequiresLowerArm( true );
        PhysicalWeapons.add( addPW );

        // lance
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'C', 'X', 'X', 'F' );
        a.SetISDates( 3061, 3064, true, 3064, 0, 0, false, false );
        a.SetISFactions( "LA", "LA", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Lance", "Lance", "Lance", m, a );
        addPW.SetStats(0.05f, 0.05f, 0.0f, 0);
        addPW.SetDamage(0.2f, 0);
        addPW.SetSpecials("PA", "-", 3000.0f, 0.0f, 1.0f, 0.0f, 0.0f, false);
        addPW.SetToHit(2, 2, 2);
        addPW.SetRequiresHand(false);
        PhysicalWeapons.add( addPW );

        // mace
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'X', 'X', 'D' );
        a.SetISDates( 0, 0, false, 3061, 0, 0, false, false );
        a.SetISFactions( "", "", "LA", "" );
        a.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Mace", "Mace", "ISMace", m, a );
        addPW.SetStats(0.1f, 0.1f, 0.0f, 0);
        addPW.SetDamage(0.25f, 0);
        addPW.SetSpecials("PA", "-", 0.0f, 130000.0f, 1.0f, 0.0f, 0.0f, false);
        addPW.SetToHit(1, 1, 1);
        PhysicalWeapons.add(addPW);

        // vibroblade small
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'E' );
        a.SetISDates( 3059, 3065, true, 3065, 0, 0, false, false );
        a.SetISFactions( "DC", "DC", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Small Vibroblade", "Small Vibroblade", "ISSmallVibroBlade", m, a );
        addPW.SetStats(0.0f, 0.0f, 3.0f, 1);
        addPW.SetDamage(0.0f, 7);
        addPW.SetHeat(3);
        addPW.SetSpecials("PA", "V", 0.0f, 150000.0f, 0.0f, 12.0f, 0.0f, false);
        addPW.SetToHit(-1, -1, -1);
        PhysicalWeapons.add(addPW);

        // vibroblade medium
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'E' );
        a.SetISDates( 3059, 3065, true, 3065, 0, 0, false, false );
        a.SetISFactions( "DC", "DC", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Medium Vibroblade", "Medium Vibroblade", "ISMediumVibroBlade", m, a );
        addPW.SetStats(0.0f, 0.0f, 5.0f, 2);
        addPW.SetDamage(0.0f, 10);
        addPW.SetHeat(5);
        addPW.SetSpecials("PA", "V", 0.0f, 400000.0f, 0.0f, 17.0f, 0.0f, false);
        addPW.SetToHit(-1, -1, -1);
        PhysicalWeapons.add(addPW);

        // vibroblade large
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'E' );
        a.SetISDates( 3059, 3065, true, 3065, 0, 0, false, false );
        a.SetISFactions( "DC", "DC", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Large Vibroblade", "Large Vibroblade", "ISLargeVibroBlade", m, a );
        addPW.SetStats(0.0f, 0.0f, 7.0f, 4);
        addPW.SetDamage(0.0f, 14);
        addPW.SetHeat(7);
        addPW.SetSpecials("PA", "V", 0.0f, 750000.0f, 0.0f, 24.0f, 0.0f, false);
        addPW.SetToHit(-1, -1, -1);
        PhysicalWeapons.add(addPW);

        // spikes
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'C', 'X', 'E', 'E' );
        a.SetISDates( 0, 0, false, 3051, 0, 0, false, false );
        a.SetISFactions( "", "", "FC", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Spikes", "Spikes", "ISSpikes", m, a );
        addPW.SetStats(0.0f, 0.0f, 0.5f, 1);
        addPW.SetDamage(0.0f, 2);
        addPW.SetSpecials("PA", "PB", 50.0f, 0.0f, 0.0f, 0.0f, 4.0f, false);
        addPW.SetToHit(0, 0, 0);
        addPW.SetRequiresLowerArm( false );
        addPW.SetPWClass( PhysicalWeapon.PW_CLASS_SPIKE );
        addPW.SetAllocations(true, true, true, true, true, false);
        PhysicalWeapons.add(addPW);

        // small shield
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'F' );
        a.SetISDates( 3065, 3067, true, 3067, 0, 0, false, false );
        a.SetISFactions( "LA", "LA", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Small Shield", "Small Shield", "ISSmallShield", m, a );
        addPW.SetStats(0.0f, 0.0f, 2.0f, 3);
        addPW.SetDamage(0.0f, 3);
        addPW.SetSpecials("PA", "PB", 0.0f, 50000.0f, 0.0f, 0.0f, 50.0f, false);
        addPW.SetToHit(-2, -2, -2);
        addPW.SetRequiresLowerArm(false);
        addPW.SetPWClass( PhysicalWeapon.PW_CLASS_SHIELD );
        PhysicalWeapons.add(addPW);

        // medium shield
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'F' );
        a.SetISDates( 3065, 3067, true, 3067, 0, 0, false, false );
        a.SetISFactions( "LA", "LA", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Medium Shield", "Medium Shield", "ISMediumShield", m, a );
        addPW.SetStats(0.0f, 0.0f, 4.0f, 5);
        addPW.SetDamage(0.0f, 5);
        addPW.SetSpecials("PA", "PB", 0.0f, 100000.0f, 0.0f, 0.0f, 135.0f, false);
        addPW.SetToHit(-3, -3, -3);
        addPW.SetRequiresLowerArm(false);
        addPW.SetPWClass( PhysicalWeapon.PW_CLASS_SHIELD );
        addPW.AddMechModifier( new MechModifier( -1, 0, 0, 0.0f, 0, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, true ));
        PhysicalWeapons.add(addPW);

        // large shield
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'F' );
        a.SetISDates( 3065, 3067, true, 3067, 0, 0, false, false );
        a.SetISFactions( "LA", "LA", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Large Shield", "Large Shield", "ISLargeShield", m, a );
        addPW.SetStats(0.0f, 0.0f, 6.0f, 7);
        addPW.SetDamage(0.0f, 7);
        addPW.SetSpecials("PA", "PB", 0.0f, 300000.0f, 0.0f, 0.0f, 263.0f, false);
        addPW.SetToHit(-4, -4, -4);
        addPW.SetRequiresLowerArm(false);
        addPW.SetPWClass( PhysicalWeapon.PW_CLASS_SHIELD );
        MechModifier addMod = new MechModifier( -1, 0, 0, 0.0f, 0, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, true );
        addMod.SetCanJump(false);
        addPW.AddMechModifier( addMod );
        PhysicalWeapons.add(addPW);

        // Talons
        addPW = new Talons( m );
        PhysicalWeapons.add( addPW );

        // Backhoe
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'B', 'B', 'B' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'B', 'B' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Backhoe", "Backhoe", "Backhoe", m, a);
        addPW.SetStats(0.0F, 0.0F, 5, 6);
        addPW.SetDamage(0.0f, 6);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(50000, 8, 0);
        PhysicalWeapons.add(addPW);

        // Chainsaw
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'D', 'D', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Chainsaw","Chainsaw", "Chainsaw", m, a);
        addPW.SetStats(0.0F, 0.0F, 5, 5);
        addPW.SetDamage(0.0f, 5);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 7, 0);
        PhysicalWeapons.add(addPW);

        // Combine
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'C', 'C', 'C' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'C', 'C' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Combine","Combine", "Combine", m, a);
        addPW.SetStats(0.0F, 0.0F, 2.5f, 4);
        addPW.SetDamage(0.0f, 3);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(75000, 5, 0);
        PhysicalWeapons.add(addPW);
        
        // Dual Saw
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'D', 'D', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'C', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Dual Saw","Dual Saw", "DualSaw", m, a);
        addPW.SetStats(0.0F, 0.0F, 7, 7);
        addPW.SetDamage(0.0f, 7);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 9, 0);
        PhysicalWeapons.add(addPW);
        
        // Heavy Duty Pile-Driver
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'D', 'D', 'E', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'D', 'X', 'E', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Heavy Duty Pile-Driver","Heavy Duty Pile-Driver", "HeavyDutyPile-Driver", m, a);
        addPW.SetStats(0.0F, 0.0F, 10, 8);
        addPW.SetDamage(0.0f, 9);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 9, 0);
        PhysicalWeapons.add(addPW);
        
        // Mining Drill
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'C', 'D', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Mining Drill","Mining Drill", "MiningDrill", m, a);
        addPW.SetStats(0.0F, 0.0F, 3, 4);
        addPW.SetDamage(0.0f, 4);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 6, 0);
        PhysicalWeapons.add(addPW);
        
        // Rock Cutter
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'D', 'D', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'C', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Rock Cutter","Rock Cutter", "RockCutter", m, a);
        addPW.SetStats(0.0F, 0.0F, 5, 5);
        addPW.SetDamage(0.0f, 5);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 6, 0);
        PhysicalWeapons.add(addPW);

        // Salvage Arm
        if (!m.IsQuad()){
            a = new AvailableCode( AvailableCode.TECH_BOTH );
            a.SetISCodes( 'D', 'E', 'F', 'E' );
            a.SetISDates( 0, 0, false, 2452, 0, 0, false, false );
            a.SetISFactions( "", "", "TH", "" );
            a.SetCLCodes( 'D', 'X', 'F', 'E' );
            a.SetCLDates( 0, 0, false, 2452, 0, 0, false, false );
            a.SetCLFactions( "", "", "TH", "" );
            a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
            addPW = new IndustrialPhysicalWeapon("Salvage Arm","Salvage Arm", "SalvageArm", m, a);
            addPW.SetStats(0.0F, 0.0F, 3, 2);
            addPW.SetDamage(0.0f, 0);
            ((IndustrialPhysicalWeapon)addPW).SetSpecials(50000, 0,0);
            PhysicalWeapons.add(addPW);
        }

        // Spot Welder
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'C', 'D', 'C' );
        a.SetISDates( 0, 0, false, 2320, 0, 0, false, false );
        a.SetISFactions( "", "", "CC", "" );
        a.SetCLCodes( 'C', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 2320, 0, 0, false, false );
        a.SetCLFactions( "", "", "CC", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Spot Welder","Spot Welder", "SpotWelder", m, a);
        addPW.SetStats(0.0F, 0.0F, 2, 1);
        addPW.SetDamage(0.0f, 5);
        addPW.SetHeat( 2 );
        addPW.SetRequirements( false, false, true );
        addPW.SetPWClass( PhysicalWeapon.PW_CLASS_SPOTWELDER );
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(75000, 5, 0);
        PhysicalWeapons.add(addPW);
        
        // Wrecking Ball
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'C', 'C', 'C' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'C', 'C' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Wrecking Ball","Wrecking Ball", "WreckingBall", m, a);
        addPW.SetStats(0.0F, 0.0F, 4, 5);
        addPW.SetDamage(0.0f, 8);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(80000, 8,0);
        PhysicalWeapons.add(addPW);
    }

    private void BuildEquipment() {
        AvailableCode a;
        Equipment addEQ;

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'X', 'X', 'D' );
        a.SetISDates( 0, 0, false, 3055, 0, 0, false, false );
        a.SetISFactions( "", "", "CS", "" );
        a.SetCLCodes( 'B', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 2850, 0, 0, false, false );
        a.SetCLFactions( "", "", "CGB", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("A-Pod","A-Pod", "PD", a);
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("AntiPersonnelPod");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 0.5f, 1500.0f, 0.0f, 1.0f, "OS/AI");
        addEQ.SetAllocs(false, false, false, false, true);
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'E', 'X', 'X', 'E' );
        a.SetISDates( 0, 0, false, 3069, 0, 0, false, false );
        a.SetISFactions( "", "", "LA", "" );
        a.SetCLCodes( 'E', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 3068, 0, 0, false, false );
        a.SetCLFactions( "", "", "CWX", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("B-Pod","B-Pod", "PD", a);
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("BPod");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0f, 2500.0f, 0.0f, 2.0f, "OS/AI");
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'C', 'X', 'X', 'E' );
        a.SetISDates( 0, 0, false, 3064, 0, 0, false, false );
        a.SetISFactions( "", "", "LA", "" );
        a.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("M-Pod","M-Pod", "PD", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISMPod");
        addEQ.SetRange(1, 2, 3);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0f, 6000.0f, 5.0f, 0.0f, "C/V/X/OS");
        addEQ.SetMountableRear(true);
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'F', 'X', 'X', 'F' );
        a.SetISDates( 3053, 3057, true, 3057, 0, 0, false, false );
        a.SetISFactions( "DC", "DC", "", "" );
        a.SetCLCodes( 'F', 'X', 'X', 'F' );
        a.SetCLDates( 3056, 3058, true, 3058, 0, 0, false, false );
        a.SetCLFactions( "CGS", "CGS", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Angel ECM","Angel ECM", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("AngelECM");
        addEQ.SetRange(0, 0, 6);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 2.0f, 750000.0f, 0.0f, 100.0f, "-");
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'E', 'F', 'D' );
        a.SetISDates( 0, 0, false, 2617, 2796, 3040, true, true );
        a.SetISFactions( "", "", "TH", "FC" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Anti-Missile System","(IS) Anti-Missile System", "PD", a );
        addEQ.SetAmmo(true, 12, 501);
        addEQ.SetMegaMekName("ISAntiMissileSystem");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(1);
        addEQ.SetStats(1, 0.5f, 100000.0f, 0.0f, 32.0f, "-");
        addEQ.SetMountableRear(true);
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 2617, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Anti-Missile System","(CL) Anti-Missile System", "PD", a );
        addEQ.SetAmmo(true, 24, 502);
        addEQ.SetMegaMekName("CLAntiMissileSystem");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(1);
        addEQ.SetStats(1, 0.5f, 100000.0f, 0.0f, 32.0f, "-");
        addEQ.SetMountableRear(true);
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'E' );
        a.SetISDates( 3054, 3059, true, 3059, 0, 0, false, false );
        a.SetISFactions( "FC", "FS", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Laser Anti-Missile System","(IS) Laser Anti-Missile System", "PD", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISLaserAntiMissileSystem");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(7);
        addEQ.SetStats(2, 1.5f, 225000.0f, 0.0f, 45.0f, "-");
        addEQ.SetMountableRear(true);
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'X', 'E' );
        a.SetCLDates( 3045, 3048, true, 3048, 0, 0, false, false );
        a.SetCLFactions( "CWF", "CWF", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Laser Anti-Missile System","(CL) Laser Anti-Missile System", "PD", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CLLaserAntiMissileSystem");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(5);
        addEQ.SetStats(1, 1.0f, 225000.0f, 0.0f, 45.0f, "-");
        addEQ.SetMountableRear(true);
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'E', 'F', 'D' );
        a.SetISDates( 0, 0, false, 2576, 2835, 3045, true, true );
        a.SetISFactions( "", "", "TH", "CC" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Beagle Active Probe","Beagle Active Probe", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISBeagleActiveProbe");
        addEQ.SetRange(0, 0, 4);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 1.5f, 200000.0f, 0.0f, 10.0f, "-");
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'E', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 2576, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Active Probe","Active Probe", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CLActiveProbe");
        addEQ.SetRange(0, 0, 5);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0f, 200000.0f, 0.0f, 12.0f, "-");
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'X', 'D' );
        a.SetCLDates( 0, 0, false, 3059, 0, 0, false, false );
        a.SetCLFactions( "", "", "CSJ", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Light Active Probe","Light Active Probe", "Light Active Probe", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("Light Active Probe");
        addEQ.SetRange(0, 0, 3);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 0.5f, 50000.0f, 0.0f, 7.0f, "-");
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'F' );
        a.SetISDates( 3056, 3058, true, 3058, 0, 0, false, false );
        a.SetISFactions( "CS", "CS", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Bloodhound Active Probe","Bloodhound Active Probe", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISBloodhoundActiveProbe");
        addEQ.SetRange(0, 0, 8);
        addEQ.SetHeat(0);
        addEQ.SetStats(3, 2.0f, 500000.0f, 0.0f, 25.0f, "-");
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'E' );
        a.SetISDates( 0, 0, false, 3050, 0, 0, false, false );
        a.SetISFactions( "", "", "DC", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("C3 Computer (Master)","C3 Computer (Master)", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISC3MasterComputer");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(5, 5.0f, 1500000.0f, 0.0f, 0.0f, "-");
        addEQ.SetExclusions(new Exclusion(new String[]{"Improved C3 Computer", "Null Signature System", "Void Signature System"}, "C3 Computer (Master)"));
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'E' );
        a.SetISDates( 0, 0, false, 3050, 0, 0, false, false );
        a.SetISFactions( "", "", "DC", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("C3 Computer (Slave)","C3 Computer (Slave)", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISC3SlaveUnit");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0f, 250000.0f, 0.0f, 0.0f, "-");
        addEQ.SetExclusions(new Exclusion(new String[]{"Improved C3 Computer", "Null Signature System", "Void Signature System"}, "C3 Computer (Slave)"));
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'E' );
        a.SetISDates( 0, 0, false, 3062, 0, 0, false, false );
        a.SetISFactions( "", "", "CS", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Improved C3 Computer", "Improved C3 Computer","E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISImprovedC3CPU");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 2.5f, 750000.0f, 0.0f, 0.0f, "-");
        addEQ.SetExclusions(new Exclusion(new String[]{"C3 Computer (Master)", "C3 Computer (Slave)", "Null Signature System", "Void Signature System"}, "Improved C3 Computer"));
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'F', 'F', 'X', 'F' );
        a.SetISDates( 0, 0, false, 2751, 0, 0, false, false );
        a.SetISFactions( "", "", "TH", "" );
        a.SetCLCodes( 'F', 'X', 'F', 'F' );
        a.SetCLDates( 0, 0, false, 2751, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Ground Mobile HPG","Ground Mobile HPG", "PE", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("GroundMobileHPG");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(20);
        addEQ.SetAllocs(false, true, true, false, false);
        addEQ.SetSplitable(true);
        addEQ.SetStats(12, 12.0f, 4000000000.0f, 0.0f, 0.0f, "-");
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'E', 'F', 'D' );
        a.SetISDates( 0, 0, false, 2597, 2845, 3045, true, true );
        a.SetISFactions( "", "", "TH", "CC" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Guardian ECM Suite","Guardian ECM Suite", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISGuardianECM");
        addEQ.SetRange(0, 0, 6);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 1.5f, 200000.0f, 0.0f, 61.0f, "-");
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 2597, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("ECM Suite","ECM Suite", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CLECMSuite");
        addEQ.SetRange(0, 0, 6);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0f, 200000.0f, 0.0f, 61.0f, "-");
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'E', 'E', 'F', 'D' );
        a.SetISDates( 0, 0, false, 2600, 2835, 3033, true, true );
        a.SetISFactions( "", "", "TH", "FS" );
        a.SetCLCodes( 'E', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 2600, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("TAG","TAG", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("TAG");
        addEQ.SetRange(5, 9, 15);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0f, 50000.0f, 0.0f, 0.0f, "-");
        addEQ.SetMountableRear(true);
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'X', 'E' );
        a.SetCLDates( 0, 0, false, 3054, 0, 0, false, false );
        a.SetCLFactions( "", "", "CWF", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Light TAG","Light TAG", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CLLightTAG");
        addEQ.SetRange(3, 6, 9);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 0.5f, 40000.0f, 0.0f, 0.0f, "-");
        addEQ.SetMountableRear(true);
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'X', 'F' );
        a.SetCLDates( 3057, 3059, true, 3059, 0, 0, false, false );
        a.SetCLFactions( "CSJ", "CSJ", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Watchdog CEWS","Watchdog CEWS", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CLWatchdogECM");
        addEQ.SetRange(0, 0, 4);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 1.5f, 600000.0f, 7.0f, 61.0f, "-");
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'D', 'X', 'X', 'E' );
        a.SetISDates( 3041, 3049, true, 3049, 0, 0, false, false );
        a.SetISFactions( "FC", "FC", "", "" );
        a.SetCLCodes( 'D', 'X', 'X', 'E' );
        a.SetCLDates( 3055, 3056, true, 3056, 0, 0, false, false );
        a.SetCLFactions( "CJF", "CSV", "", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment( "Coolant Pod", "Coolant Pod","PE", a );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetMegaMekName( "CoolantPod" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 1.0f, 50000.0f, 0.0f, 0.0f, "OS, X" );
        addEQ.SetExplosive( true );
        Equipment.add( addEQ );

        Equipment.add( new ModularArmor() );
    }

    public void BuildIndustrialEquipment() {
        IndustrialEquipment.clear();
        AvailableCode a;
        Equipment addEQ;

        // light bridgelayer
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'D', 'E', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Bridgelayer, Light","Bridgelayer, Light", "IE", a, new BridgelayerValidator(), "Bipedal Mechs cannot carry bridgelayers.");
        addEQ.SetMegaMekName( "LightBridgelayer");
        addEQ.SetStats(2, 1, 40000, 0, 5, "-");
        addEQ.SetAllocs(false, false, true, false, false);
        addEQ.SetMountableRear(true);
        IndustrialEquipment.add(addEQ);

        // Medium Bridgelayer
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'D', 'E', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'C', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Bridgelayer, Medium","Bridgelayer, Medium", "IE", a, new BridgelayerValidator(), "Bipedal Mechs cannot carry bridgelayers.");
        addEQ.SetMegaMekName( "MediumBridgelayer");
        addEQ.SetStats(4, 2, 75000, 0, 10, "-");
        addEQ.SetAllocs(false, false, true, false, false);
        addEQ.SetMountableRear(true);
        IndustrialEquipment.add(addEQ);

        // Heavy Bridgelayer
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'D', 'E', 'E', 'E' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'D', 'X', 'E', 'E' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment( "Bridgelayer, Heavy","Bridgelayer, Heavy", "IE", a, new BridgelayerValidator(), "Bipedal Mechs cannot carry bridgelayers.");
        addEQ.SetMegaMekName( "HeavyBridgelayer");
        addEQ.SetStats(12, 6, 100000, 0, 20, "-");
        addEQ.SetAllocs(false, false, true, false, false);
        addEQ.SetMountableRear(true);
        IndustrialEquipment.add(addEQ);

        // Fluid Suction System, Standard
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'B', 'B', 'B' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'C', 'X', 'B', 'B' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Fluid Suction System, Standard","Fluid Suction System, Standard", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("StandardFluidSuctionSystem");
        addEQ.SetStats(1, 1, 25000, 0, 0, "-");
        addEQ.SetAmmo(true, 10, 0);
        addEQ.SetMountableRear(true);
        IndustrialEquipment.add(addEQ);

        // Fluid Suction System, Light
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'B', 'B', 'B' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'B', 'B' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Fluid Suction System, Light","Fluid Suction System, Light", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("LightFluidSuctionSystem");
        addEQ.SetStats(1, 0.5f, 1000, 0, 0, "-");
        addEQ.SetAmmo(true, 10, 0);
        addEQ.SetMountableRear(true);
        IndustrialEquipment.add(addEQ);

        // Lift Hoist
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Lift Hoist","Lift Hoist", "IE", a, new LiftHoistValidator(), "Mech cannot have more than 2 lift hoists.");
        addEQ.SetMegaMekName("LiftHoist");
        addEQ.SetStats(3, 3, 50000, 0, 0, "-");
        addEQ.SetAllocs(false, false, true, true, false);
        addEQ.SetMountableRear(true);
        IndustrialEquipment.add(addEQ);

        // Nail/Rivet Gun
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'C', 'C', 'C' );
        a.SetISDates( 0, 0, false, 2310, 0, 0, false, false );
        a.SetISFactions( "", "", "FWL", "" );
        a.SetCLCodes( 'C', 'X', 'C', 'C' );
        a.SetCLDates( 0, 0, false, 2310, 0, 0, false, false );
        a.SetCLFactions( "", "", "FWL", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Nail Gun","Nail Gun", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("NailGun");
        addEQ.SetStats(1, 0.5f, 7000, 1, 0, "-");
        addEQ.SetAmmo(true, 300, 503);
        addEQ.SetMountableRear(true);
        addEQ.SetRange(1, 0, 0);
        IndustrialEquipment.add(addEQ);

        // Nail/Rivet Gun
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'C', 'C', 'C' );
        a.SetISDates( 0, 0, false, 2310, 0, 0, false, false );
        a.SetISFactions( "", "", "FWL", "" );
        a.SetCLCodes( 'C', 'X', 'C', 'C' );
        a.SetCLDates( 0, 0, false, 2310, 0, 0, false, false );
        a.SetCLFactions( "", "", "FWL", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Rivet Gun","Rivet Gun", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("RivetGun");
        addEQ.SetStats(1, 0.5f, 7000, 1, 0, "-");
        addEQ.SetAmmo(true, 300, 504);
        addEQ.SetMountableRear(true);
        addEQ.SetRange(1, 0, 0);
        IndustrialEquipment.add(addEQ);

        // Remote Sensor Dispenser
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'F', 'F', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'C', 'X', 'F', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Remote Sensor Dispenser", "Remote Sensor Dispenser","IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("RemoteSensorDispenser");
        addEQ.SetStats(1, 0.5f, 30000, 0, 0, "-");
        addEQ.SetAmmo(true, 60, 505);
        addEQ.SetMountableRear(true);
        IndustrialEquipment.add(addEQ);

        // Searchlight
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Searchlight","Searchlight", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("Searchlight");
        addEQ.SetStats(1, 0.5f, 2000, 0, 0, "-");
        addEQ.SetRange(0, 0, 170);
        addEQ.SetMountableRear(true);
        IndustrialEquipment.add(addEQ);

        // Sprayer
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'B', 'B', 'B' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'B', 'B' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Sprayer","Sprayer", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("Sprayer");
        addEQ.SetStats(1, 0.5f, 1000, 0, 0, "-");
        addEQ.SetRange(0, 0, 1);
        addEQ.SetAmmo(true, 10, 506);
        addEQ.SetMountableRear(true);
        IndustrialEquipment.add(addEQ);

        // Cargo Container
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Cargo Container","Cargo Container", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("CargoContainer");
        addEQ.SetStats(1, 10, 0, 0, 0, "-");
        IndustrialEquipment.add(addEQ);

        // Cargo, Standard
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Cargo, Standard","Cargo, Standard", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("CargoStandard");
        addEQ.SetStats(1, 1, 0, 0, 0, "-");
        IndustrialEquipment.add(addEQ);

        // Cargo, Standard
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Cargo, Liquid","Cargo, Liquid", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("CargoLiquid");
        addEQ.SetStats(1, 1, 0, 0, 0, "-");
        IndustrialEquipment.add(addEQ);

        // Cargo, Standard
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Cargo, Insulated","Cargo, Insulated", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("CargoInsulated");
        addEQ.SetStats(1, 1, 0, 0, 0, "-");
        IndustrialEquipment.add(addEQ);
    }

    // Classes to validate equipment
    // This is why java needs closures so I could do this an inline function

    // Alawys allocatable equipment
    private class SimpleValidator implements EquipmentValidationInterface {

        public boolean validate(Mech m) {
            return true;
        }
    }

    // Bridgelayers
    private class BridgelayerValidator implements EquipmentValidationInterface {

        public boolean validate(Mech m) {
            return m.IsQuad();
        }
    }

    // Lift Hoists
    private class LiftHoistValidator implements EquipmentValidationInterface {

        public boolean validate(Mech m) {
            Vector currentEquipment = m.GetLoadout().GetEquipment();
            for (int i = 0, c = 0; i < currentEquipment.size(); ++i) {
                abPlaceable currentItem = (abPlaceable) currentEquipment.get(i);
                if (currentItem.GetCritName().equals("Lift Hoist")) {
                    ++c;
                    if (c == 2) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
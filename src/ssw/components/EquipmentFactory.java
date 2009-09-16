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

import ssw.utilities.CommonTools;
import java.util.Vector;

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
        VehicularGrenadeLauncher VGL = new VehicularGrenadeLauncher();
        RangedWeapons.add( VGL );
        BuildMGArrays();
        BuildEquipment();
        BuildIndustrialEquipment();
        BuildPhysicals( m );
    }

    public abPlaceable GetCopy( abPlaceable p, Mech m ) {
        // creates an equipment copy of p
        abPlaceable retval = null;

        if( p instanceof IndustrialEquipment ) {
            retval = ((IndustrialEquipment) p).Clone();
        } else if( p instanceof Equipment ) {
            retval = ((Equipment) p).Clone();
        } else if( p instanceof ModularArmor ) {
            retval = new ModularArmor();
        } else if( p instanceof Ammunition ) {
            retval = ((Ammunition) p).Clone();
        } else if( p instanceof RangedWeapon ) {
            retval = ((RangedWeapon) p).Clone();
        } else if( p instanceof VehicularGrenadeLauncher ) {
            retval = new VehicularGrenadeLauncher();
        } else if( p instanceof PhysicalWeapon ) {
            PhysicalWeapon w = (PhysicalWeapon) p;
            switch( w.GetPWClass() ) {
                case PhysicalWeapon.PW_CLASS_TALON:
                    retval = new Talons ( m );
                    break;
                case PhysicalWeapon.PW_CLASS_INDUSTRIAL:
                    retval = ((IndustrialPhysicalWeapon) p).Clone();
                    break;
                default:
                    retval = w.Clone();
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

    public Object[] GetAllAmmo( int key, int RulesLevel ) {
        // returns an array containing all the ammunition for the specified
        // weapon key regardless of whether it is available (for printing)
        Vector v = new Vector(),
                test;

        // find the ammunition
        for( int i = 0; i < Ammo.size(); i++ ) {
            if( key == ((Ammunition) Ammo.get( i )).GetAmmoIndex() ) {
                Ammunition a = (Ammunition) Ammo.get( i );
                if( v.contains( a ) ) { break; }
                v.add( a );
            }
        }

        return v.toArray();
    }

    public Object[] GetAmmoByYear( int key, int Year, int RulesLevel, Mech m ) {
        // returns an array containing all the ammunition for the specified
        // weapon key regardless of whether it is available (for printing)
        Vector v = new Vector(),
                test;

        // find the ammunition
        for( int i = 0; i < Ammo.size(); i++ ) {
            if( key == ((Ammunition) Ammo.get( i )).GetAmmoIndex() ) {
                Ammunition a = (Ammunition) Ammo.get( i );
                AvailableCode AC = a.GetAvailability();
                if( v.contains( a ) ) { break; }
                if( CommonTools.IsAllowed(AC, RulesLevel, m.GetLoadout().GetTechBase(), m.IsPrimitive(), m.IsIndustrialmech(), 0, true, Year ) ) { v.add( a ); }
            }
        }

        return v.toArray();
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
            } else if( p instanceof VehicularGrenadeLauncher ) {
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

            if( a.LookupName().equals( "Hatchet" ) ) {
                // grab the Hatchet, we'll want to filter it specifically
                hatchet = a;
            }
            if( ! CommonTools.IsAllowed( AC, m )) {
                RetVal.remove( i );
            }
        }

        if( m.GetEra() >= AvailableCode.ERA_SUCCESSION ) {
            if( m.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH || m.GetLoadout().GetTechBase() == AvailableCode.TECH_INNER_SPHERE ) {
                if( m.IsYearRestricted() ) {
                    if( m.GetYear() > 3021 ) {
                        if( ! RetVal.contains( hatchet ) ) {
                            RetVal.add( hatchet );
                        }
                    }
                } else {
                    if( ! RetVal.contains( hatchet ) ) {
                        RetVal.add( hatchet );
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
            if( ((abPlaceable) Ammo.get( i )).LookupName().equals( name ) ) {
                return (Ammunition) GetCopy( (abPlaceable) Ammo.get( i ), m );
            }
        }
        return null;
    }

    public Equipment GetEquipmentByName(String name, Mech m ) {
        for (int i = 0; i < Equipment.size(); i++) {
            if (((abPlaceable) Equipment.get(i)).LookupName().equals(name)) {
                return (Equipment) GetCopy((abPlaceable) Equipment.get(i), m);
            }
        }

        for (int i = 0; i < IndustrialEquipment.size(); i++) {
            if (((abPlaceable) IndustrialEquipment.get(i)).LookupName().equals(name)) {
                return (Equipment) GetCopy((abPlaceable) IndustrialEquipment.get(i), m);
            }
        }
        return null;
    }

    public PhysicalWeapon GetPhysicalWeaponByName( String name, Mech m ) {
        for( int i = 0; i < PhysicalWeapons.size(); i++ ) {
            if( ((abPlaceable) PhysicalWeapons.get( i )).LookupName().equals( name ) ) {
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
            if( ((abPlaceable) RangedWeapons.get( i )).LookupName().equals( name ) ) {
                return (abPlaceable) GetCopy( (abPlaceable) RangedWeapons.get( i ), m );
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public abPlaceable GetByName( String name, Mech m ) {
        // This is a catch-all for certain parts of the program, especially for
        // things like the HMPReader and MTFReader

        // because the ammo table will inevitably be bigger than anything else
        if( name.contains( "@" ) ) {
            return GetAmmoByName( name, m );
        } else {
            // check the other tables
            abPlaceable retval = null;
            retval = GetRangedWeaponByName( name, m );
            if( retval != null ) { return retval; }
            retval = GetEquipmentByName( name, m );
            if( retval != null ) { return retval; }
            retval = GetPhysicalWeaponByName( name, m );
            return retval;
        }
    }

    public void BuildMGArrays() {
        abPlaceable addBW;
        MGArray addMGA;
        AvailableCode a;

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'F' );
        a.SetISDates( 0, 0, false, 3068, 0, 0, false, false );
        a.SetISFactions( "", "", "FS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        // MGA light machine gun
        addBW = GetRangedWeaponByName( "(IS) Light Machine Gun", null );

        // LMGA 2
        addMGA = new MGArray( ((RangedWeapon) addBW), 2, 0.5, false, a);
        RangedWeapons.add(addMGA);

        // LMGA 3
        addMGA = new MGArray( ((RangedWeapon) addBW), 3, 0.5, false, a);
        RangedWeapons.add(addMGA);

        // LMGA 4
        addMGA = new MGArray( ((RangedWeapon) addBW), 4, 0.5, false, a);
        RangedWeapons.add(addMGA);

        // machine gun
        addBW = GetRangedWeaponByName( "(IS) Machine Gun", null );

        // MGA 2
        addMGA = new MGArray(((RangedWeapon) addBW), 2, 0.5, false, a);
        RangedWeapons.add(addMGA);

        // MGA 3
        addMGA = new MGArray(((RangedWeapon) addBW), 3, 0.5, false, a);
        RangedWeapons.add(addMGA);

        // MGA 4
        addMGA = new MGArray(((RangedWeapon) addBW), 4, 0.5, false, a);
        RangedWeapons.add(addMGA);

        // heavy machine gun
        addBW = GetRangedWeaponByName( "(IS) Heavy Machine Gun", null );

        // HMGA 2
        addMGA = new MGArray(((RangedWeapon) addBW), 2, 1.0, false, a);
        RangedWeapons.add(addMGA);

        // HMGA 3
        addMGA = new MGArray(((RangedWeapon) addBW), 3, 1.0, false, a);
        RangedWeapons.add(addMGA);

        // HMGA 4
        addMGA = new MGArray(((RangedWeapon) addBW), 4, 1.0, false, a);
        RangedWeapons.add(addMGA);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'E', 'X', 'X', 'E' );
        a.SetCLDates( 0, 0, false, 3069, 0, 0, false, false );
        a.SetCLFactions( "", "", "CDS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        // LMGA light machine gun
        addBW = GetRangedWeaponByName( "(CL) Light Machine Gun", null );

        // LMGA 2
        addMGA = new MGArray( ((RangedWeapon) addBW), 2, 0.25, true, a );
        RangedWeapons.add( addMGA );

        // LMGA 3
        addMGA = new MGArray( ((RangedWeapon) addBW), 3, 0.25, true, a );
        RangedWeapons.add( addMGA );

        // LMGA 4
        addMGA = new MGArray( ((RangedWeapon) addBW), 4, 0.25, true, a );
        RangedWeapons.add( addMGA );

        // MGA machine gun
        addBW = GetRangedWeaponByName( "(CL) Machine Gun", null );

        // MGA 2
        addMGA = new MGArray( ((RangedWeapon) addBW), 2, 0.25, true, a );
        RangedWeapons.add( addMGA );

        // MGA 3
        addMGA = new MGArray( ((RangedWeapon) addBW), 3, 0.25, true, a );
        RangedWeapons.add( addMGA );

        // MGA 4
        addMGA = new MGArray( ((RangedWeapon) addBW), 4, 0.25, true, a );
        RangedWeapons.add( addMGA );

        // HMGA heavy machine gun
        addBW = GetRangedWeaponByName( "(CL) Heavy Machine Gun", null );

        // HMGA 2
        addMGA = new MGArray( ((RangedWeapon) addBW), 2, 0.5, true, a );
        RangedWeapons.add( addMGA );

        // HMGA 3
        addMGA = new MGArray( ((RangedWeapon) addBW), 3, 0.5, true, a );
        RangedWeapons.add( addMGA );

        // HMGA 4
        addMGA = new MGArray( ((RangedWeapon) addBW), 4, 0.5, true, a );
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
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Hatchet", "Hatchet", "Hatchet", "Hatchet", m, a );
        addPW.SetStats( 0.06666, 0.06666, 0.0, 0 );
        addPW.SetDamage( 0.2, 0 );
        addPW.SetSpecials( "PA", "-", 5000.0, 0.0, 1.5, 0.0, 0.0, false );
        addPW.SetToHit( -1, -1, -1 );
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "Htcht" );
        PhysicalWeapons.add( addPW );

        // sword
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'X', 'X', 'D' );
        a.SetISDates( 0, 0, false, 3058, 0, 0, false, false );
        a.SetISFactions( "", "", "DC", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Sword", "Sword", "Sword", "Sword", m, a );
        addPW.SetStats( 0.05, 0.06666, 0.0, 0 );
        addPW.SetDamage( 0.1, 1 );
        addPW.SetSpecials( "PA", "-", 10000.0, 0.0, 1.725, 0.0, 0.0, true );
        addPW.SetToHit( -2, -2, -2 );
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "Swrd" );
        PhysicalWeapons.add( addPW );

        // retractable blade
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'F', 'D', 'D' );
        a.SetISDates( 0, 0, false, 2420, 0, 0, false, false );
        a.SetISFactions( "", "", "TH", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Retractable Blade", "Retractable Blade", "Retractable Blade", "Retractable Blade", m, a );
        addPW.SetStats( 0.05, 0.05, 0.5, 1 );
        addPW.SetDamage( 0.1, 0 );
        addPW.SetSpecials( "PA", "-", 10000.0, 10000.0, 1.725, 0.0, 0.0, true );
        addPW.SetToHit( -2, -2, -2 );
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "RBlde" );
        PhysicalWeapons.add( addPW );

        // chain whip
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'C', 'X', 'X', 'F' );
        a.SetISDates( 3069, 3071, true, 3071, 0, 0, false, false );
        a.SetISFactions( "LA", "WB", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Chain Whip", "Chain Whip", "Chain Whip", "Chain Whip", m, a );
        addPW.SetStats(0.0, 0.0, 3.0, 2);
        addPW.SetDamage(0.0, 1);
        addPW.SetSpecials("PA", "-", 0.0, 120000.0, 1.725, 0.0, 0.0, false);
        addPW.SetToHit(-2, -2, -2);
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "ChnWhp" );
        PhysicalWeapons.add( addPW );

        // claws
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'X', 'F', 'E' );
        a.SetISDates( 0, 0, false, 3060, 0, 0, false, false );
        a.SetISFactions( "", "", "LA", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Claws", "Claws", "Claws", "Claw", m, a );
        addPW.SetStats(0.06666, 0.06666, 0.0, 0);
        addPW.SetDamage(0.1428, 0);
        addPW.SetSpecials("PA", "-", 2800.0, 0.0, 1.275, 0.0, 0.0, false);
        addPW.SetToHit(1, 1, 1);
        addPW.SetReplacesHand(true);
        addPW.SetRequiresLowerArm( true );
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "Clw" );
        PhysicalWeapons.add( addPW );

        // flail
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'X', 'X', 'E' );
        a.SetISDates( 3054, 3057, true, 3057, 0, 0, false, false );
        a.SetISFactions( "FC", "FC", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Flail", "Flail", "Flail", "Flail", m, a );
        addPW.SetStats(0.0, 0.0, 5.0, 4);
        addPW.SetDamage(0.0, 9);
        addPW.SetSpecials("PA", "-", 0.0, 110000.0, 0.0, 11.0, 0.0, false);
        addPW.SetToHit(1, 1, 1);
        addPW.SetReplacesHand(true);
        addPW.SetRequiresLowerArm( true );
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "Flail" );
        PhysicalWeapons.add( addPW );

        // lance
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'C', 'X', 'X', 'F' );
        a.SetISDates( 3061, 3064, true, 3064, 0, 0, false, false );
        a.SetISFactions( "LA", "LA", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Lance", "Lance", "Lance", "Lance", m, a );
        addPW.SetStats(0.05, 0.05, 0.0, 0);
        addPW.SetDamage(0.2, 0);
        addPW.SetSpecials("PA", "-", 3000.0, 0.0, 1.0, 0.0, 0.0, false);
        addPW.SetToHit(2, 2, 2);
        addPW.SetRequiresHand(false);
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "Lnc" );
        PhysicalWeapons.add( addPW );

        // mace
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'B', 'X', 'X', 'D' );
        a.SetISDates( 0, 0, false, 3061, 0, 0, false, false );
        a.SetISFactions( "", "", "LA", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Mace", "Mace", "Mace", "Mace", m, a );
        addPW.SetStats(0.1, 0.1, 0.0, 0);
        addPW.SetDamage(0.25, 0);
        addPW.SetSpecials("PA", "-", 0.0, 130000.0, 1.0, 0.0, 0.0, false);
        addPW.SetToHit(1, 1, 1);
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "Mace" );
        PhysicalWeapons.add(addPW);

        // vibroblade small
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'E' );
        a.SetISDates( 3059, 3065, true, 3065, 0, 0, false, false );
        a.SetISFactions( "DC", "DC", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Small Vibroblade", "Small Vibroblade", "Small Vibroblade", "ISSmallVibroBlade", m, a );
        addPW.SetStats(0.0, 0.0, 3.0, 1);
        addPW.SetDamage(0.0, 7);
        addPW.SetHeat(3);
        addPW.SetSpecials("PA", "V", 0.0, 150000.0, 0.0, 12.0, 0.0, false);
        addPW.SetToHit(-1, -1, -1);
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "SmlVbrBld" );
        PhysicalWeapons.add(addPW);

        // vibroblade medium
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'E' );
        a.SetISDates( 3059, 3065, true, 3065, 0, 0, false, false );
        a.SetISFactions( "DC", "DC", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Medium Vibroblade", "Medium Vibroblade", "Medium Vibroblade", "ISMediumVibroBlade", m, a );
        addPW.SetStats(0.0, 0.0, 5.0, 2);
        addPW.SetDamage(0.0, 10);
        addPW.SetHeat(5);
        addPW.SetSpecials("PA", "V", 0.0, 400000.0, 0.0, 17.0, 0.0, false);
        addPW.SetToHit(-1, -1, -1);
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "MedVbrBld" );
        PhysicalWeapons.add(addPW);

        // vibroblade large
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'E' );
        a.SetISDates( 3059, 3065, true, 3065, 0, 0, false, false );
        a.SetISFactions( "DC", "DC", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Large Vibroblade", "Large Vibroblade", "Large Vibroblade", "ISLargeVibroBlade", m, a );
        addPW.SetStats(0.0, 0.0, 7.0, 4);
        addPW.SetDamage(0.0, 14);
        addPW.SetHeat(7);
        addPW.SetSpecials("PA", "V", 0.0, 750000.0, 0.0, 24.0, 0.0, false);
        addPW.SetToHit(-1, -1, -1);
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "LrgVbrBld" );
        PhysicalWeapons.add(addPW);

        // spikes
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'C', 'X', 'E', 'E' );
        a.SetISDates( 0, 0, false, 3051, 0, 0, false, false );
        a.SetISFactions( "", "", "FC", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Spikes", "Spikes", "Spikes", "Spikes", m, a );
        addPW.SetStats(0.0, 0.0, 0.5, 1);
        addPW.SetDamage(0.0, 2);
        addPW.SetSpecials("PA", "PB", 50.0, 0.0, 0.0, 0.0, 4.0, false);
        addPW.SetToHit(0, 0, 0);
        addPW.SetRequiresLowerArm( false );
        addPW.SetPWClass( PhysicalWeapon.PW_CLASS_SPIKE );
        addPW.SetAllocations(true, true, true, true, true, false);
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "Spk" );
        PhysicalWeapons.add(addPW);

        // small shield
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'F' );
        a.SetISDates( 3065, 3067, true, 3067, 0, 0, false, false );
        a.SetISFactions( "LA", "LA", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Small Shield", "Small Shield", "Small Shield", "ISSmallShield", m, a );
        addPW.SetStats(0.0, 0.0, 2.0, 3);
        addPW.SetDamage(0.0, 3);
        addPW.SetSpecials("PA", "PB", 0.0, 50000.0, 0.0, 0.0, 50.0, false);
        addPW.SetToHit(-2, -2, -2);
        addPW.SetRequiresLowerArm(false);
        addPW.SetPWClass( PhysicalWeapon.PW_CLASS_SHIELD );
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "SmlShld" );
        PhysicalWeapons.add(addPW);

        // medium shield
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'F' );
        a.SetISDates( 3065, 3067, true, 3067, 0, 0, false, false );
        a.SetISFactions( "LA", "LA", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Medium Shield", "Medium Shield", "Medium Shield", "ISMediumShield", m, a );
        addPW.SetStats(0.0, 0.0, 4.0, 5);
        addPW.SetDamage(0.0, 5);
        addPW.SetSpecials("PA", "PB", 0.0, 100000.0, 0.0, 0.0, 135.0, false);
        addPW.SetToHit(-3, -3, -3);
        addPW.SetRequiresLowerArm(false);
        addPW.SetPWClass( PhysicalWeapon.PW_CLASS_SHIELD );
        addPW.AddMechModifier( new MechModifier( -1, 0, 0, 0.0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, true, false ));
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "MedShld" );
        PhysicalWeapons.add(addPW);

        // large shield
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'D', 'X', 'X', 'F' );
        a.SetISDates( 3065, 3067, true, 3067, 0, 0, false, false );
        a.SetISFactions( "LA", "LA", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new PhysicalWeapon( "Large Shield", "Large Shield", "Large Shield", "ISLargeShield", m, a );
        addPW.SetStats(0.0, 0.0, 6.0, 7);
        addPW.SetDamage(0.0, 7);
        addPW.SetSpecials("PA", "PB", 0.0, 300000.0, 0.0, 0.0, 263.0, false);
        addPW.SetToHit(-4, -4, -4);
        addPW.SetRequiresLowerArm(false);
        addPW.SetPWClass( PhysicalWeapon.PW_CLASS_SHIELD );
        MechModifier addMod = new MechModifier( -1, 0, 0, 0.0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, true, false );
        addMod.SetCanJump(false);
        addPW.AddMechModifier( addMod );
        addPW.SetBookReference( "Tactical Operations" );
        addPW.SetChatName( "LrgShld" );
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
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon( "Backhoe", "Backhoe", "Backhoe", "Backhoe", m, a );
        addPW.SetStats(0.0, 0.0, 5, 6);
        addPW.SetDamage(0.0, 6);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(50000, 8, 0);
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "BckHo" );
        PhysicalWeapons.add(addPW);

        // Chainsaw
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'D', 'D', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Chainsaw","Chainsaw","Chainsaw", "Chainsaw", m, a);
        addPW.SetStats(0.0, 0.0, 5, 5);
        addPW.SetDamage(0.0, 5);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 7, 0);
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "ChnSw" );
        PhysicalWeapons.add(addPW);

        // Combine
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'C', 'C', 'C' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'C', 'C' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Combine","Combine","Combine", "Combine", m, a);
        addPW.SetStats(0.0, 0.0, 2.5, 4);
        addPW.SetDamage(0.0, 3);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(75000, 5, 0);
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "Cmbn" );
        PhysicalWeapons.add(addPW);
        
        // Dual Saw
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'D', 'D', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'C', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Dual Saw","Dual Saw","Dual Saw", "DualSaw", m, a);
        addPW.SetStats(0.0, 0.0, 7, 7);
        addPW.SetDamage(0.0, 7);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 9, 0);
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "2Saw" );
        PhysicalWeapons.add(addPW);
        
        // Heavy Duty Pile-Driver
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'D', 'D', 'E', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'D', 'X', 'E', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Heavy Duty Pile-Driver","Heavy Duty Pile-Driver","Heavy Duty Pile-Driver", "HeavyDutyPile-Driver", m, a);
        addPW.SetStats(0.0, 0.0, 10, 8);
        addPW.SetDamage(0.0, 9);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 9, 0);
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "HDPD" );
        PhysicalWeapons.add(addPW);
        
        // Mining Drill
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'C', 'D', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Mining Drill","Mining Drill","Mining Drill", "MiningDrill", m, a);
        addPW.SetStats(0.0, 0.0, 3, 4);
        addPW.SetDamage(0.0, 4);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 6, 0);
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "MngDrl" );
        PhysicalWeapons.add(addPW);
        
        // Rock Cutter
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'D', 'D', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'C', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Rock Cutter","Rock Cutter","Rock Cutter", "RockCutter", m, a);
        addPW.SetStats(0.0, 0.0, 5, 5);
        addPW.SetDamage(0.0, 5);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 6, 0);
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "RckCut" );
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
            a.SetPBMAllowed( true );
            a.SetPIMAllowed( true );
            a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
            addPW = new IndustrialPhysicalWeapon("Salvage Arm","Salvage Arm","Salvage Arm", "SalvageArm", m, a);
            addPW.SetStats(0.0, 0.0, 3, 2);
            addPW.SetDamage(0.0, 0);
            ((IndustrialPhysicalWeapon)addPW).SetSpecials(50000, 0,0);
            addPW.SetBookReference( "Tech Manual" );
            addPW.SetChatName( "SlvgArm" );
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
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Spot Welder","Spot Welder","Spot Welder", "SpotWelder", m, a);
        addPW.SetStats(0.0, 0.0, 2, 1);
        addPW.SetDamage(0.0, 5);
        addPW.SetHeat( 2 );
        addPW.SetRequirements( false, false, true );
        addPW.SetPWClass( PhysicalWeapon.PW_CLASS_SPOTWELDER );
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(75000, 5, 0);
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "SptWeld" );
        PhysicalWeapons.add(addPW);
        
        // Wrecking Ball
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'C', 'C', 'C' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'C', 'C' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addPW = new IndustrialPhysicalWeapon("Wrecking Ball","Wrecking Ball","Wrecking Ball", "WreckingBall", m, a);
        addPW.SetStats(0.0, 0.0, 4, 5);
        addPW.SetDamage(0.0, 8);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(80000, 8,0);
        addPW.SetBookReference( "Tech Manual" );
        addPW.SetChatName( "WrkBll" );
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
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment( "Anti-Personnel Pod", "A-Pod","A-Pod", "PD", a);
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("AntiPersonnelPod");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 0.5, 1500.0, 0.0, 1.0, "OS/AI");
        addEQ.SetAllocs(false, false, false, false, true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "APod" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'E', 'X', 'X', 'E' );
        a.SetISDates( 0, 0, false, 3069, 0, 0, false, false );
        a.SetISFactions( "", "", "LA", "" );
        a.SetCLCodes( 'E', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 3068, 0, 0, false, false );
        a.SetCLFactions( "", "", "CWX", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Anti-BattleArmor Pod", "B-Pod","B-Pod", "PD", a);
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("BPod");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0, 2500.0, 0.0, 2.0, "OS/AI");
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "BPod" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'C', 'X', 'X', 'E' );
        a.SetISDates( 0, 0, false, 3064, 0, 0, false, false );
        a.SetISFactions( "", "", "LA", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Anti-Mech Pod", "M-Pod","M-Pod", "PD", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISMPod");
        addEQ.SetRange(1, 2, 3);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0, 6000.0, 5.0, 0.0, "C/V/X/OS");
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "MPod" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'F', 'X', 'X', 'F' );
        a.SetISDates( 3053, 3057, true, 3057, 0, 0, false, false );
        a.SetISFactions( "DC", "DC", "", "" );
        a.SetCLCodes( 'F', 'X', 'X', 'F' );
        a.SetCLDates( 3056, 3058, true, 3058, 0, 0, false, false );
        a.SetCLFactions( "CGS", "CGS", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Angel Electronic Countermeasure Suite", "Angel ECM","Angel ECM", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("AngelECM");
        addEQ.SetRange(0, 0, 6);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 2.0, 750000.0, 0.0, 100.0, "-");
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "AECM" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'E', 'F', 'D' );
        a.SetISDates( 0, 0, false, 2617, 2796, 3040, true, true );
        a.SetISFactions( "", "", "TH", "FC" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Anti-Missile System", "(IS) Anti-Missile System","Anti-Missile System", "PD", a );
        addEQ.SetAmmo(true, 12, 501);
        addEQ.SetMegaMekName("ISAntiMissileSystem");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(1);
        addEQ.SetStats(1, 0.5, 100000.0, 0.0, 32.0, "-");
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "AMS" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 2617, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Anti-Missile System","(CL) Anti-Missile System", "Anti-Missile System", "PD", a );
        addEQ.SetAmmo(true, 24, 502);
        addEQ.SetMegaMekName("CLAntiMissileSystem");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(1);
        addEQ.SetStats(1, 0.5, 100000.0, 0.0, 32.0, "-");
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "AMS" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'E' );
        a.SetISDates( 3054, 3059, true, 3059, 0, 0, false, false );
        a.SetISFactions( "FC", "FS", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Laser Anti-Missile System","(IS) Laser Anti-Missile System", "Laser Anti-Missile System", "PD", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISLaserAntiMissileSystem");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(7);
        addEQ.SetStats(2, 1.5, 225000.0, 0.0, 45.0, "-");
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "LAMS" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'X', 'E' );
        a.SetCLDates( 3045, 3048, true, 3048, 0, 0, false, false );
        a.SetCLFactions( "CWF", "CWF", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Laser Anti-Missile System","(CL) Laser Anti-Missile System", "Laser Anti-Missile System", "PD", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CLLaserAntiMissileSystem");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(5);
        addEQ.SetStats(1, 1.0, 225000.0, 0.0, 45.0, "-");
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "LAMS" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'E', 'F', 'D' );
        a.SetISDates( 0, 0, false, 2576, 2835, 3045, true, true );
        a.SetISFactions( "", "", "TH", "CC" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Beagle Active Probe", "Beagle Active Probe","Beagle Active Probe", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISBeagleActiveProbe");
        addEQ.SetRange(0, 0, 4);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 1.5, 200000.0, 0.0, 10.0, "-");
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "BAP" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'E', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 2576, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Active Probe", "Active Probe","Active Probe", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CLActiveProbe");
        addEQ.SetRange(0, 0, 5);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0, 200000.0, 0.0, 12.0, "-");
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "BAP" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'X', 'D' );
        a.SetCLDates( 0, 0, false, 3059, 0, 0, false, false );
        a.SetCLFactions( "", "", "CSJ", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Light Active Probe","Light Active Probe","Light Active Probe", "Light Active Probe", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("LightActiveProbe");
        addEQ.SetRange(0, 0, 3);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 0.5, 50000.0, 0.0, 7.0, "-");
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "LAP" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'F' );
        a.SetISDates( 3056, 3058, true, 3058, 0, 0, false, false );
        a.SetISFactions( "CS", "CS", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Bloodhound Active Probe","Bloodhound Active Probe","Bloodhound Active Probe", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISBloodhoundActiveProbe");
        addEQ.SetRange(0, 0, 8);
        addEQ.SetHeat(0);
        addEQ.SetStats(3, 2.0, 500000.0, 0.0, 25.0, "-");
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "BlAP" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'E' );
        a.SetISDates( 0, 0, false, 3050, 0, 0, false, false );
        a.SetISFactions( "", "", "DC", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("C3 Computer (Master)","C3 Computer (Master)","C3 Computer (Master)", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISC3MasterComputer");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(5, 5.0, 1500000.0, 0.0, 0.0, "-");
        addEQ.SetExclusions(new Exclusion(new String[]{"Improved C3 Computer", "Null Signature System", "Void Signature System"}, "C3 Computer (Master)"));
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "C3M" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'E' );
        a.SetISDates( 0, 0, false, 3050, 0, 0, false, false );
        a.SetISFactions( "", "", "DC", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("C3 Computer (Slave)","C3 Computer (Slave)","C3 Computer (Slave)", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISC3SlaveUnit");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0, 250000.0, 0.0, 0.0, "-");
        addEQ.SetExclusions(new Exclusion(new String[]{"Improved C3 Computer", "Null Signature System", "Void Signature System"}, "C3 Computer (Slave)"));
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "C3S" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'E' );
        a.SetISDates( 0, 0, false, 3062, 0, 0, false, false );
        a.SetISFactions( "", "", "CS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Improved C3 Computer","Improved C3 Computer", "Improved C3 Computer","E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISImprovedC3CPU");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 2.5, 750000.0, 0.0, 0.0, "-");
        addEQ.SetExclusions(new Exclusion(new String[]{"C3 Computer (Master)", "C3 Computer (Slave)", "Null Signature System", "Void Signature System"}, "Improved C3 Computer"));
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "iC3" );
        Equipment.add(addEQ);

	// Birdie
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'F' );
        a.SetISDates( 3069, 3073, true, 3073, 0, 0, false, false );
        a.SetISFactions( "FS", "FS", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("C3 Boosted Computer (Master)","C3 Boosted Computer (Master)","C3 Boosted Computer (Master)", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISBC3MasterComputer");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(6, 6.0, 3000000.0, 0.0, 0.0, "CE/T");
        addEQ.SetExclusions(new Exclusion(new String[]{"Improved C3 Computer", "Null Signature System", "Void Signature System"}, "C3 Boosted Computer (Master)"));
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "BC3M" );
        Equipment.add(addEQ);

	// Birdie
        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'F' );
        a.SetISDates( 3069, 3073, true, 3073, 0, 0, false, false );
        a.SetISFactions( "FS", "FS", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("C3 Boosted Computer (Slave)","C3 Boosted Computer (Slave)","C3 Boosted Computer (Slave)", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISBC3SlaveUnit");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 3.0, 500000.0, 0.0, 0.0, "CE/T");
        addEQ.SetExclusions(new Exclusion(new String[]{"Improved C3 Computer", "Null Signature System", "Void Signature System"}, "C3 Boosted Computer (Slave)"));
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "BC3S" );
        Equipment.add(addEQ);

	// Birdie
	a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'F' );
        a.SetISDates( 3066, 3071, true, 3071, 0, 0, false, false );
        a.SetISFactions( "DC", "DC", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("C3 Emergency Master","C3 Emergency Master","C3 Emergency Master", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISEC3MasterComputer");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 2.0, 2800000.0, 0.0, 0.0, "T");
        addEQ.SetExclusions(new Exclusion(new String[]{"Improved C3 Computer", "Null Signature System", "Void Signature System"}, "C3 Emergency Master"));
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "EC3M" );
        Equipment.add(addEQ);

	// Birdie
	a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'F' );
        a.SetISDates( 3070, 3072, true, 3072, 0, 0, false, false );
        a.SetISFactions( "DC", "DC", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("C3 Remote Sensor Launcher","C3 Remote Sensor Launcher","C3 Remote Sensor Launcher", "E", a );
        addEQ.SetAmmo(true, 4, 507);
        addEQ.SetMegaMekName("C3RemoteSensorLauncher");
        addEQ.SetRange(3, 6, 9);
        addEQ.SetHeat(0);
        addEQ.SetStats(3, 4.0, 400000.0, 30.0, 0.0, "M");
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "C3RLnch" );
        Equipment.add(addEQ);

	// Birdie
	a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'C', 'X', 'X', 'E' );
        a.SetISDates( 3066, 3069, true, 3069, 0, 0, false, false );
        a.SetISFactions( "LA", "LA", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Chaff Pod","Chaff Pod","Chaff Pod", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ChaffPod");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0, 2000.0, 0.0, 19.0, "OS/PD");
	addEQ.SetExplosive( true );        
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "ChfPd" );
	Equipment.add(addEQ);
	
        // Birdie
	a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'D', 'E', 'F', 'E' );
        a.SetISDates( 0, 0, false, 2710, 0, 0, false, false );
        a.SetISFactions( "", "", "TH", "" );
        a.SetCLCodes( 'D', 'X', 'E', 'E' );
        a.SetCLDates( 0, 0, false, 2710, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Collapsible Command Module (CCM)","Collapsible Command Module (CCM)","Collapsible Command Module (CCM)", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CollapsibleCommandModule");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(12, 16.0, 500000.0, 0.0, 0.0, "-");
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "CCM" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'D', 'C', 'D', 'C' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'D', 'C', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Communications Equipment","Communications Equipment","Communications Equipment", "E", a );
        addEQ.SetMegaMekName("CommunicationsEquipment");
        addEQ.SetStats( 1, 1, 0, 0, 0, "-" );
        addEQ.SetVariableSize( true, 1.0, 12.0, 1.0, 1.0, 10000.0 );
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "CommEqp" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'D', 'X', 'X', 'E' );
        a.SetISDates( 3041, 3049, true, 3049, 0, 0, false, false );
        a.SetISFactions( "FC", "FC", "", "" );
        a.SetCLCodes( 'D', 'X', 'X', 'E' );
        a.SetCLDates( 3055, 3056, true, 3056, 0, 0, false, false );
        a.SetCLFactions( "CJF", "CSV", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment( "Coolant Pod", "Coolant Pod", "Coolant Pod","PE", a );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetMegaMekName( "CoolantPod" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 1.0, 50000.0, 0.0, 0.0, "OS, X" );
        addEQ.SetExplosive( true );
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "CoolPd" );
        Equipment.add( addEQ );

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'F', 'E' );
        a.SetISDates( 0, 0, false, 3025, 0, 0, false, false );
        a.SetISFactions( "", "", "CC", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Electronic Warfare Equipment","Electronic Warfare Equipment","EW Equipment", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("EWEquipment");
        addEQ.SetRange( 0, 0, 3 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 4, 7.5, 500000.0, 8.0, 31.0, "-");
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "EWEqp" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'F', 'F', 'X', 'F' );
        a.SetISDates( 0, 0, false, 2751, 0, 0, false, false );
        a.SetISFactions( "", "", "TH", "" );
        a.SetCLCodes( 'F', 'X', 'F', 'F' );
        a.SetCLDates( 0, 0, false, 2751, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Ground Mobile Hyper Pulse Generator","Ground Mobile HPG","Ground Mobile HPG", "PE", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("GroundMobileHPG");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(20);
        addEQ.SetAllocs(false, true, true, false, false);
        addEQ.SetSplitable(true);
        addEQ.SetStats(12, 12.0, 4000000000.0, 0.0, 0.0, "-");
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "HPG" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'E', 'F', 'D' );
        a.SetISDates( 0, 0, false, 2597, 2845, 3045, true, true );
        a.SetISFactions( "", "", "TH", "CC" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Guardian Electronic Countermeasure Suite","Guardian ECM Suite","Guardian ECM Suite", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("ISGuardianECM");
        addEQ.SetRange(0, 0, 6);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 1.5, 200000.0, 0.0, 61.0, "-");
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "ECM" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 2597, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Electronic Countermeasure Suite", "ECM Suite","ECM Suite", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CLECMSuite");
        addEQ.SetRange(0, 0, 6);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0, 200000.0, 0.0, 61.0, "-");
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "ECM" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        a.SetISCodes( 'E', 'X', 'X', 'E');
        a.SetISDates( 3062, 3067, true, 3067, 0, 0, false, false );
        a.SetISFactions( "LA", "LA", "", "" );
        a.SetCLCodes( 'E', 'X', 'X', 'E');
        a.SetCLDates( 3056, 3059, true, 3059, 0, 0, false, false );
        a.SetCLFactions( "CDS", "CDS", "", "" );
        addEQ = new Equipment("HarJel","HarJel","HarJel", "PE", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("HarJel");
        addEQ.SetRange(0, 0, 0);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0, 120000.0, 0.0, 0.0, "-");
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "HrJl" );
        Equipment.add( addEQ );

        Equipment.add( new ModularArmor() );

        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'E', 'E', 'F', 'D' );
        a.SetISDates( 0, 0, false, 2600, 2835, 3033, true, true );
        a.SetISFactions( "", "", "TH", "FS" );
        a.SetCLCodes( 'E', 'X', 'D', 'C' );
        a.SetCLDates( 0, 0, false, 2600, 0, 0, false, false );
        a.SetCLFactions( "", "", "TH", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Target Acquisition Gear","TAG","TAG", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("TAG");
        addEQ.SetRange(5, 9, 15);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 1.0, 50000.0, 0.0, 0.0, "-");
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "TAG" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'X', 'E' );
        a.SetCLDates( 0, 0, false, 3054, 0, 0, false, false );
        a.SetCLFactions( "", "", "CWF", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Light Target Acquisition Gear","Light TAG","Light TAG", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CLLightTAG");
        addEQ.SetRange(3, 6, 9);
        addEQ.SetHeat(0);
        addEQ.SetStats(1, 0.5, 40000.0, 0.0, 0.0, "-");
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "LTAG" );
        Equipment.add(addEQ);

        a = new AvailableCode( AvailableCode.TECH_CLAN );
        a.SetCLCodes( 'F', 'X', 'X', 'F' );
        a.SetCLDates( 3057, 3059, true, 3059, 0, 0, false, false );
        a.SetCLFactions( "CSJ", "CSJ", "", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Watchdog Composite Electronic Warfare System","Watchdog CEWS","Watchdog CEWS", "E", a );
        addEQ.SetAmmo(false, 0, 0);
        addEQ.SetMegaMekName("CLWatchdogECM");
        addEQ.SetRange(0, 0, 4);
        addEQ.SetHeat(0);
        addEQ.SetStats(2, 1.5, 600000.0, 7.0, 61.0, "-");
        addEQ.SetBookReference( "Tactical Operations" );
        addEQ.SetChatName( "WDCEWS" );
        Equipment.add(addEQ);

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
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Bridgelayer, Light","Bridgelayer, Light","Bridgelayer, Light", "IE", a, new BridgelayerValidator(), "Bipedal Mechs cannot carry bridgelayers.");
        addEQ.SetMegaMekName( "LightBridgelayer");
        addEQ.SetStats(2, 1, 40000, 0, 5, "-");
        addEQ.SetAllocs(false, false, true, false, false);
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "LtBrgLyr" );
        IndustrialEquipment.add(addEQ);

        // Medium Bridgelayer
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'D', 'E', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'C', 'X', 'D', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Bridgelayer, Medium","Bridgelayer, Medium","Bridgelayer, Medium", "IE", a, new BridgelayerValidator(), "Bipedal Mechs cannot carry bridgelayers.");
        addEQ.SetMegaMekName( "MediumBridgelayer");
        addEQ.SetStats(4, 2, 75000, 0, 10, "-");
        addEQ.SetAllocs(false, false, true, false, false);
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "MdBrgLyr" );
        IndustrialEquipment.add(addEQ);

        // Heavy Bridgelayer
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'D', 'E', 'E', 'E' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'D', 'X', 'E', 'E' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment( "Bridgelayer, Heavy","Bridgelayer, Heavy","Bridgelayer, Heavy", "IE", a, new BridgelayerValidator(), "Bipedal Mechs cannot carry bridgelayers.");
        addEQ.SetMegaMekName( "HeavyBridgelayer");
        addEQ.SetStats(12, 6, 100000, 0, 20, "-");
        addEQ.SetAllocs(false, false, true, false, false);
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "HvBrgLyr" );
        IndustrialEquipment.add(addEQ);

        // Fluid Suction System, Standard
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'B', 'B', 'B' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'C', 'X', 'B', 'B' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Fluid Suction System, Standard","Fluid Suction System, Standard","Fluid Suction System, Standard", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("StandardFluidSuctionSystem");
        addEQ.SetStats(1, 1, 25000, 0, 0, "-");
        addEQ.SetAmmo(true, 10, 0);
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "FldSuk" );
        IndustrialEquipment.add(addEQ);

        // Fluid Suction System, Light
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'B', 'B', 'B' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'B', 'B' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Fluid Suction System, Light","Fluid Suction System, Light","Fluid Suction System, Light", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("LightFluidSuctionSystem");
        addEQ.SetStats(1, 0.5, 1000, 0, 0, "-");
        addEQ.SetAmmo(true, 10, 0);
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "LFldSuk" );
        IndustrialEquipment.add(addEQ);

        // Lift Hoist
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Lift Hoist","Lift Hoist","Lift Hoist", "IE", a, new LiftHoistValidator(), "Mech cannot have more than 2 lift hoists.");
        addEQ.SetMegaMekName("LiftHoist");
        addEQ.SetStats(3, 3, 50000, 0, 0, "-");
        addEQ.SetAllocs(false, false, true, true, false);
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "LftHst" );
        IndustrialEquipment.add(addEQ);

        // Nail/Rivet Gun
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'C', 'C', 'C' );
        a.SetISDates( 0, 0, false, 2310, 0, 0, false, false );
        a.SetISFactions( "", "", "FWL", "" );
        a.SetCLCodes( 'C', 'X', 'C', 'C' );
        a.SetCLDates( 0, 0, false, 2310, 0, 0, false, false );
        a.SetCLFactions( "", "", "FWL", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Nail Gun","Nail Gun","Nail Gun", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("NailGun");
        addEQ.SetStats(1, 0.5, 7000, 1, 0, "-");
        addEQ.SetAmmo(true, 300, 503);
        addEQ.SetMountableRear(true);
        addEQ.SetRange(1, 0, 0);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "NlGn" );
        IndustrialEquipment.add(addEQ);

        // Nail/Rivet Gun
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'C', 'C', 'C' );
        a.SetISDates( 0, 0, false, 2310, 0, 0, false, false );
        a.SetISFactions( "", "", "FWL", "" );
        a.SetCLCodes( 'C', 'X', 'C', 'C' );
        a.SetCLDates( 0, 0, false, 2310, 0, 0, false, false );
        a.SetCLFactions( "", "", "FWL", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Rivet Gun","Rivet Gun","Rivet Gun", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("RivetGun");
        addEQ.SetStats(1, 0.5, 7000, 1, 0, "-");
        addEQ.SetAmmo(true, 300, 504);
        addEQ.SetMountableRear(true);
        addEQ.SetRange(1, 0, 0);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "RvtGn" );
        IndustrialEquipment.add(addEQ);

        // Remote Sensor Dispenser
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'C', 'F', 'F', 'D' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'C', 'X', 'F', 'D' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Remote Sensor Dispenser", "Remote Sensor Dispenser", "Remote Sensor Dispenser","IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("RemoteSensorDispenser");
        addEQ.SetStats(1, 0.5, 30000, 0, 0, "-");
        addEQ.SetAmmo(true, 60, 505);
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "RSnsDisp" );
        IndustrialEquipment.add(addEQ);

        // Searchlight
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Searchlight","Searchlight","Searchlight", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("Searchlight");
        addEQ.SetStats(1, 0.5, 2000, 0, 0, "-");
        addEQ.SetRange(0, 0, 170);
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "SrchLt" );
        IndustrialEquipment.add(addEQ);

        // Sprayer
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'B', 'B', 'B', 'B' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'B', 'X', 'B', 'B' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Sprayer","Sprayer","Sprayer", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("Sprayer");
        addEQ.SetStats(1, 0.5, 1000, 0, 0, "-");
        addEQ.SetRange(0, 0, 1);
        addEQ.SetAmmo(true, 10, 506);
        addEQ.SetMountableRear(true);
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "Spryr" );
        IndustrialEquipment.add(addEQ);

        // Cargo Container
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new IndustrialEquipment("Cargo Container","Cargo Container","Cargo Container", "IE", a, new SimpleValidator(), "");
        addEQ.SetMegaMekName("CargoContainer");
        addEQ.SetStats(1, 10, 0, 0, 0, "-");
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "CrgCntnr" );
        IndustrialEquipment.add(addEQ);

        // Cargo, Standard
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Cargo, Standard","Cargo, Standard","Cargo, Standard", "IE", a);
        addEQ.SetMegaMekName("CargoStandard");
        addEQ.SetStats(1, 0.5, 0, 0, 0, "-");
        addEQ.SetVariableSize( true, 0.5, 12.0, 0.5, 1.0, 0.0 );
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "StndrdCrgo" );
        IndustrialEquipment.add(addEQ);

        // Cargo, Standard
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Cargo, Liquid","Cargo, Liquid","Cargo, Liquid", "IE", a);
        addEQ.SetMegaMekName("CargoLiquid");
        addEQ.SetStats(1, 0.5, 0, 0, 0, "-");
        addEQ.SetVariableSize( true, 0.5, 12.0, 0.5, 1.0, 100.0 );
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "LqdCrgo" );
        IndustrialEquipment.add(addEQ);

        // Cargo, Standard
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Cargo, Insulated","Cargo, Insulated","Cargo, Insulated", "IE", a);
        addEQ.SetMegaMekName("CargoInsulated");
        addEQ.SetStats(1, 0.5, 0, 0, 0, "-");
        addEQ.SetVariableSize( true, 0.5, 12.0, 0.5, 1.0, 250.0 );
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "InslCrgo" );
        IndustrialEquipment.add(addEQ);

        // Cargo, Livestock Maelwys
        a = new AvailableCode( AvailableCode.TECH_BOTH );
        a.SetISCodes( 'A', 'A', 'A', 'A' );
        a.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetISFactions( "", "", "PS", "" );
        a.SetCLCodes( 'A', 'X', 'A', 'A' );
        a.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        a.SetCLFactions( "", "", "PS", "" );
        a.SetPBMAllowed( true );
        a.SetPIMAllowed( true );
        a.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        addEQ = new Equipment("Cargo, Livestock","Cargo, Livestock","Cargo, Livestock", "IE", a);
        addEQ.SetMegaMekName("CargoLivestock");
        addEQ.SetStats(1, 0.5, 0, 0, 0, "-");
        addEQ.SetVariableSize( true, 0.5, 12.0, 0.5, 1.0, 2500.0 );
        addEQ.SetBookReference( "Tech Manual" );
        addEQ.SetChatName( "LvStkCrgo" );
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
                if (currentItem.LookupName().equals("Lift Hoist")) {
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
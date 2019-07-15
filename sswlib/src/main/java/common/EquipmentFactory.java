/*
Copyright (c) 2008~2009, Justin R. Beng

            @Override
            public String ActualName() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String LookupName() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String CritName() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String ChatName() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String MegaMekName(boolean UseRear) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String BookReference() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int NumCrits() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int NumCVSpaces() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public double GetTonnage() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public double GetCost() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public double GetOffensiveBV() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public double GetCurOffensiveBV(boolean UseRear, boolean UseTC, boolean UseAES) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public double GetCurOffensiveBV(boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public double GetDefensiveBV() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public AvailableCode GetAvailability() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }son (poopshotgun@yahoo.com)
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
package common;

import components.*;
import java.util.ArrayList;

public class EquipmentFactory {
    // big class for holding and farming out equipment
    private ArrayList Equipment = new ArrayList(),
                   PhysicalWeapons = new ArrayList(),
                   Ammo,
                   RangedWeapons;

    public EquipmentFactory( ArrayList rweapons, ArrayList pweapons, ArrayList equips, ArrayList ammo, ifUnit m ) {
        Ammo = ammo;
        RangedWeapons = rweapons;
        PhysicalWeapons = pweapons;
        Equipment = equips;
        RangedWeapons.add( new VehicularGrenadeLauncher() );
        Equipment.add( new ModularArmor() );
        BuildPhysicals( m );
        if (( m.GetUnitType() == AvailableCode.UNIT_BATTLEMECH ) && ( m instanceof Mech) ) {
            PhysicalWeapons.add( new Talons( (Mech) m ) );
            Equipment.add( new ExtendedFuelTank( (Mech) m ) );
            Equipment.add( new DroneOperatingSystem( (Mech) m ) );
        }
        if ( m instanceof CombatVehicle ) {
            Equipment.add( new Hitch() );
        }
        BuildMGArrays();
    }

    public abPlaceable GetCopy( abPlaceable p, ifUnit m ) {
        // creates an equipment copy of p
        abPlaceable retval = null;
        if( p instanceof ExtendedFuelTank ) {
            retval = new ExtendedFuelTank( (Mech) m );
        } else if( p instanceof DroneOperatingSystem ) {
            retval = new DroneOperatingSystem( (Mech) m );
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
        } else if( p instanceof Hitch ) {
            retval = new Hitch();
        } else if( p instanceof PhysicalWeapon ) {
            PhysicalWeapon w = (PhysicalWeapon) p;
            switch( w.GetPWClass() ) {
                case PhysicalWeapon.PW_CLASS_TALON:
                    retval = new Talons ( (Mech) m );
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
        ArrayList v = new ArrayList(),
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

    public Object[] GetAmmoByYear( int key, int Year, int RulesLevel, ifUnit m ) {
        // returns an array containing all the ammunition for the specified
        // weapon key regardless of whether it is available (for printing)
        ArrayList v = new ArrayList(),
                test;

        // find the ammunition
        for( int i = 0; i < Ammo.size(); i++ ) {
            if( key == ((Ammunition) Ammo.get( i )).GetAmmoIndex() ) {
                Ammunition a = (Ammunition) Ammo.get( i );
                AvailableCode AC = a.GetAvailability();
                if( v.contains( a ) ) { break; }
                if( CommonTools.IsAllowed( AC, m ) ) { v.add( a ); }
            }
        }

        return v.toArray();
    }

    public Object[] GetAmmo( int key, ifUnit m ) {
        // returns an array containing all the ammunition for the specified 
        // weapon key.
        ArrayList v = new ArrayList(),
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

    public Object[] GetAmmo( int[] key, ifUnit m ) {
        // returns an array containing all the ammunition for the specified 
        // weapon keys.  The keys are the weapon's lookup names
        ArrayList v = new ArrayList(),
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

    public Object[] GetArtillery( ifUnit m ) {
        // returns an array based on the given specifications of era and year
        ArrayList RetVal = new ArrayList();
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

    public Object[] GetBallisticWeapons( ifUnit m ) {
        ArrayList RetVal = new ArrayList();
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

    public Object[] GetEnergyWeapons( ifUnit m ) {
        // returns an array based on the given specifications of era and year
        ArrayList RetVal = new ArrayList();
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

    public Object[] GetMissileWeapons( ifUnit m ) {
        // returns an array based on the given specifications of era and year
        ArrayList RetVal = new ArrayList();
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

    public Object[] GetPhysicalWeapons( ifUnit m ) {
        // returns an array based on the given specifications of era and year
        ArrayList RetVal = new ArrayList();
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
            if( m instanceof Mech ) {
                if( ((Mech)m).GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH || ((Mech)m).GetLoadout().GetTechBase() == AvailableCode.TECH_INNER_SPHERE ) {
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
        }
        if( RetVal.size() < 1 ) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Object[] GetEquipment( ifUnit m ) {
        // returns an array based on the given specifications of era and year
        ArrayList RetVal = new ArrayList();
        abPlaceable p;
        AvailableCode AC;

        for (int i = 0; i < Equipment.size(); i++) {
            p = (abPlaceable) Equipment.get(i);
            if ( p instanceof Hitch ) {
                if ( m instanceof CombatVehicle ) {
                    if ( ((CombatVehicle)m).CanBeTrailer() )
                        RetVal.add(p);
                }
            } else {
                AC = p.GetAvailability();
                if (CommonTools.IsAllowed(AC, m)) {
                    RetVal.add(p);
                }
            }
        }
        
        if (RetVal.size() < 1) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Ammunition GetAmmoByName( String name, ifUnit m ) {
        // returns an ammunition based on the given name
        for( int i = 0; i < Ammo.size(); i++ ) {
            if( ((abPlaceable) Ammo.get( i )).LookupName().equals( name ) ) {
                return (Ammunition) GetCopy( (abPlaceable) Ammo.get( i ), m );
            }
        }
        return null;
    }

    public Equipment GetEquipmentByName(String name, ifUnit m ) {
        for (int i = 0; i < Equipment.size(); i++) {
            if (((abPlaceable) Equipment.get(i)).LookupName().equals(name)) {
                return (Equipment) GetCopy((abPlaceable) Equipment.get(i), m);
            }
        }

        return null;
    }

    public PhysicalWeapon GetPhysicalWeaponByName( String name, ifUnit m ) {
        for( int i = 0; i < PhysicalWeapons.size(); i++ ) {
            if( ((abPlaceable) PhysicalWeapons.get( i )).LookupName().equals( name ) ) {
                PhysicalWeapon p = (PhysicalWeapon) GetCopy( (abPlaceable) PhysicalWeapons.get( i ), m );
                p.SetOwner( m );
                return p;
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public abPlaceable GetRangedWeaponByName( String name, ifUnit m ) {
        // searches the weapon database for the named item and returns it
        for( int i = 0; i < RangedWeapons.size(); i++ ) {
            if( ((abPlaceable) RangedWeapons.get( i )).LookupName().equals( name ) ) {
                return (abPlaceable) GetCopy( (abPlaceable) RangedWeapons.get( i ), m );
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public abPlaceable GetByName( String name, ifUnit m ) {
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
            retval = GetPhysicalWeaponByName( name, (Mech) m );
            return retval;
        }
    }

    public abPlaceable SearchForName( String name, ifUnit m ) {
        if( name.contains( "@" ) ) {
            return GetAmmoByName( name, m );
        } else {
            for (int i = 0; i < Equipment.size(); i++) {
                if (((abPlaceable) Equipment.get(i)).LookupName().equals(name)) {
                    return GetCopy((abPlaceable) Equipment.get(i), m);
                }
            }
            for( int i = 0; i < RangedWeapons.size(); i++ ) {
                if( ((abPlaceable) RangedWeapons.get( i )).LookupName().equals( name ) ) {
                    return GetCopy( (abPlaceable) RangedWeapons.get( i ), m );
                }
            }
            for( int i = 0; i < PhysicalWeapons.size(); i++ ) {
                if( ((abPlaceable) PhysicalWeapons.get( i )).LookupName().equals( name ) ) {
                    PhysicalWeapon p = (PhysicalWeapon) GetCopy( (abPlaceable) PhysicalWeapons.get( i ), m );
                    p.SetOwner( (Mech) m );
                    return (abPlaceable) p;
                }
            }
        }
        return null;
    }

    public final void BuildMGArrays() {
        abPlaceable addBW;
        MGArray addMGA;
        AvailableCode a;

        a = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        a.SetISCodes( 'E', 'X', 'X', 'F' );
        a.SetISDates( 0, 0, false, 3068, 0, 0, false, false );
        a.SetISFactions( "--", "--", "FS", "--" );
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
        a.SetCLFactions( "--", "--", "CDS", "--" );
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

    public final void BuildPhysicals( ifUnit m ) {
        // this method rebuilds the physical weapons if the form's CurMech changes
        for( int i = 0; i < PhysicalWeapons.size(); i++ ) {
            ((PhysicalWeapon) PhysicalWeapons.get( i )).SetOwner( m );
        }
        for( int i = 0; i < Equipment.size(); i++ ) {
            if( Equipment.get( i ) instanceof ExtendedFuelTank ) {
                ((ExtendedFuelTank) Equipment.get( i )).SetOwner( m );
            }
        }
    }
}
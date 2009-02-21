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

public class WeaponFactory {
    // huge class for holding, finding, farming out, and sorting weapons
    Vector ISEW = new Vector(),
           CLEW = new Vector(),
           ISMW = new Vector(),
           CLMW = new Vector(),
           ISBW = new Vector(),
           CLBW = new Vector(),
           ISPW = new Vector(),
           CLPW = new Vector(),
           ISAR = new Vector(),
           CLAR = new Vector(),
           INPW = new Vector();
    private Mech Owner;
    private Options MyOptions;

    public WeaponFactory( Mech m, Options o ) {
        Owner = m;
        MyOptions = o;
        BuildWeapons();
    }

    public abPlaceable GetCopy( abPlaceable p ) {
        // creates a weapon copy of p
        abPlaceable retval = null;
        if( p instanceof EnergyWeapon ) {
            boolean Capacitor = false;
            EnergyWeapon e = (EnergyWeapon) p;
            if( e.HasCapacitor() ) {
                Capacitor = true;
                e.UseCapacitor( false );
            }
            retval = new EnergyWeapon( e.GetName(), e.GetMMName( false ), e.GetType(), e.IsClan(), e.GetAvailability() );
            ((EnergyWeapon) retval).SetDamage( e.GetDamageShort(), e.GetDamageMedium(), e.GetDamageLong() );
            ((EnergyWeapon) retval).SetHeat( e.GetHeat() );
            ((EnergyWeapon) retval).SetRange( e.GetRangeMin(), e.GetRangeShort(), e.GetRangeMedium(), e.GetRangeLong() );
            ((EnergyWeapon) retval).SetSpecials( e.GetSpecials(), e.OmniRestrictActuators(), e.HasAmmo(), e.GetAmmo(), e.GetAmmoIndex(), e.IsTCCapable() );
            ((EnergyWeapon) retval).SetStats( e.GetTonnage(), e.NumCrits(), e.GetCost(), e.GetOffensiveBV(), e.GetDefensiveBV() );
            ((EnergyWeapon) retval).SetToHit( e.GetToHitShort(), e.GetToHitMedium(), e.GetToHitLong() );
            ((EnergyWeapon) retval).SetRequiresFusion( e.RequiresFusion() );
            ((EnergyWeapon) retval).SetRequiresNuclear( e.RequiresNuclear());
            ((EnergyWeapon) retval).UseCapacitor( Capacitor );
            ((EnergyWeapon) retval).SetPrintName( e.GetPrintName() );
            ((EnergyWeapon) retval).SetPowerAmp( e.GetPowerAmp() );
            if( Capacitor ) { e.UseCapacitor( true ); }
        } else if( p instanceof MissileWeapon ) {
            MissileWeapon m = (MissileWeapon) p;
            retval = new MissileWeapon( m.GetName(), m.GetMMName( false ), m.GetType(), m.IsClan(), m.GetAvailability() );
            ((MissileWeapon) retval).SetHeat( m.GetHeat() );
            ((MissileWeapon) retval).SetDamage( m.GetDamageShort(), m.GetDamageMedium(), m.GetDamageLong() );
            ((MissileWeapon) retval).SetRange( m.GetRangeMin(), m.GetRangeShort(), m.GetRangeMedium(), m.GetRangeLong() );
            ((MissileWeapon) retval).SetStats( m.GetTonnage(), m.NumCrits(), m.GetCost(), m.GetOffensiveBV(), m.GetDefensiveBV() );
            ((MissileWeapon) retval).SetSpecials( m.GetSpecials(), m.OmniRestrictActuators(), m.HasAmmo(), m.GetAmmo(), m.GetAmmoIndex(), m.SwitchableAmmo() );
            ((MissileWeapon) retval).SetMissile( m.ClusterSize(), m.ClusterGrouping(), m.IsStreak(), m.IsArtemisCapable(), m.IsOneShot() );
            ((MissileWeapon) retval).SetToHit( m.GetToHitShort(), m.GetToHitMedium(), m.GetToHitLong() );
            ((MissileWeapon) retval).SetArtemisType( m.GetArtemisType() );
            ((MissileWeapon) retval).SetRequiresFusion( m.RequiresFusion() );
            ((MissileWeapon) retval).SetRequiresNuclear( m.RequiresNuclear());
            ((MissileWeapon) retval).SetPrintName( m.GetPrintName() );
        } else if( p instanceof BallisticWeapon ) {
            BallisticWeapon b = (BallisticWeapon) p;
            retval = new BallisticWeapon( b.GetName(), b.GetMMName( false ), b.GetType(), b.IsClan(), b.GetAvailability() );
            ((BallisticWeapon) retval).SetHeat( b.GetHeat() );
            ((BallisticWeapon) retval).SetDamage( b.GetDamageShort(), b.GetDamageMedium(), b.GetDamageLong() );
            ((BallisticWeapon) retval).SetRange( b.GetRangeMin(), b.GetRangeShort(), b.GetRangeMedium(), b.GetRangeLong() );
            ((BallisticWeapon) retval).SetSpecials( b.GetSpecials(), b.OmniRestrictActuators(), b.HasAmmo(), b.GetAmmo(), b.GetAmmoIndex(), b.IsTCCapable(), b.SwitchableAmmo() );
            ((BallisticWeapon) retval).SetStats( b.GetTonnage(), b.NumCrits(), b.GetCost(), b.GetOffensiveBV(), b.GetDefensiveBV() );
            ((BallisticWeapon) retval).SetBallistics( b.IsExplosive(), b.IsUltra(), b.IsRotary(), b.IsCluster() );
            ((BallisticWeapon) retval).SetAllocations( b.CanAllocHD(), b.CanAllocCT(), b.CanAllocTorso(), b.CanAllocArms(), b.CanAllocLegs(), b.CanSplit() );
            ((BallisticWeapon) retval).SetToHit( b.GetToHitShort(), b.GetToHitMedium(), b.GetToHitLong() );
            if( b.LocationLinked() ) {
                ((BallisticWeapon) retval).SetLocationLinked( true );
            }
            ((BallisticWeapon) retval).SetRequiresFusion( b.RequiresFusion() );
            ((BallisticWeapon) retval).SetRequiresNuclear( b.RequiresNuclear());
            ((BallisticWeapon) retval).SetPrintName( b.GetPrintName() );
        } else if( p instanceof PhysicalWeapon ) {
            PhysicalWeapon w = (PhysicalWeapon) p;
            if ( w.GetPWClass() == Constants.PW_CLASS_TALON )
            {
                retval = new Talons ( Owner );
            }
            else
            {
                retval = new PhysicalWeapon( w.GetName(), w.GetMMName( false ), Owner, w.GetAvailability() );
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
                ((PhysicalWeapon) retval).SetAllocations( w.CanAllocHD(), w.CanAllocCT(), w.CanAllocTorso(), w.CanAllocArms(), w.CanAllocLegs(), false );
            }
        } else if( p instanceof MGArray ) {
            MGArray m = (MGArray) p;
            retval = new MGArray( (BallisticWeapon) GetCopy( m.GetMGType() ), m.GetNumMGs(), m.GetMGTons(), m.IsClan(), m.GetAvailability() );
        } else if( p instanceof Artillery ) {
            Artillery a = (Artillery) p;
            retval = new Artillery( a.GetName(), a.GetMMName( false ), a.GetType(), a.GetAvailability() );
            ((Artillery) retval).SetRange( a.GetRangeMin(), a.GetRangeShort(), a.GetRangeMedium(), a.GetRangeLong() );
            ((Artillery) retval).SetDamage( a.GetDamageShort(), a.GetDamageMedium(), a.GetDamageLong() );
            ((Artillery) retval).SetToHit( a.GetToHitShort(), a.GetToHitMedium(), a.GetToHitLong() );
            ((Artillery) retval).SetSpecials( a.GetSpecials(), a.OmniRestrictActuators(), a.GetHeat() );
            ((Artillery) retval).SetStats( a.GetTonnage(), a.NumCrits(), a.GetCost(), a.GetOffensiveBV(), a.GetDefensiveBV() );
            ((Artillery) retval).SetAllocations( a.CanAllocHD(), a.CanAllocCT(), a.CanAllocTorso(), a.CanAllocArms(), a.CanAllocLegs(), a.CanSplit() );
            ((Artillery) retval).SetArtillery( a.IsExplosive(), a.HasAmmo(), a.GetAmmo(), a.GetAmmoIndex(), a.SwitchableAmmo() );
            ((Artillery) retval).SetRequiresFusion( a.RequiresFusion() );
            ((Artillery) retval).SetRequiresNuclear( a.RequiresNuclear());
        } else {
            return null;
        }

        if( p.GetExclusions() != null ) {
            retval.SetExclusions( p.GetExclusions() );
        }

        return retval;
    }

    public Object[] GetEnergyWeapons( Mech m ) {
        // returns an array based on the given specifications of era and year
        Vector RetVal = new Vector(),
               test;
        abPlaceable p;
        AvailableCode AC;

        if( m.IsClan() ) {
            test = CLEW;
        } else {
            test = ISEW;
        }

        for( int i = 0; i < test.size(); i++ ) {
            p = (abPlaceable) test.get( i );
            AC = p.GetAvailability();
            if( CommonTools.IsAllowed( AC, m ) ) {
                RetVal.add( p );
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
        Vector RetVal = new Vector(),
               test;
        abPlaceable p;
        AvailableCode AC;

        if( m.IsClan() ) {
            test = CLMW;
        } else {
            test = ISMW;
        }

        for( int i = 0; i < test.size(); i++ ) {
            p = (abPlaceable) test.get( i );
            AC = p.GetAvailability();
            if( CommonTools.IsAllowed( AC, m ) ) {
                RetVal.add( p );
            }
        }

        if( RetVal.size() < 1 ) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Object[] GetBallisticWeapons( Mech m ) {
        Vector RetVal = new Vector(),
               test;
        abPlaceable p;
        AvailableCode AC;

        if( m.IsClan() ) {
            test = CLBW;
        } else {
            test = ISBW;
        }

        for( int i = 0; i < test.size(); i++ ) {
            p = (abPlaceable) test.get( i );
            AC = p.GetAvailability();
            if( CommonTools.IsAllowed( AC, m ) ) {
                RetVal.add( p );
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
        Vector RetVal = new Vector(), test;

        if( m.IsClan() ) {
            test = CLPW;
        } else {
            test = ISPW;
        }

        // do this a little differently.
        // the Inner Sphere portion of this will be a bit of a hack, but the
        // reasons are evident.  Retractable Blades were never used until
        // 3070 or thereabouts, and hatchets are code F during the
        // succession wars because only one mech used them.
        // first. let's get the physical weapons into the new vector
        for( int i = 0; i < test.size(); i++ ) {
            RetVal.add( test.get( i ) );
        }

        // do the same of Industrail Physicals
        test = INPW;
        for( int i = 0; i < test.size(); i++ ) {
            RetVal.add( test.get( i ) );
        }

        // now weed out things that shouldn't be there
        for( int i = RetVal.size() - 1; i >= 0; i-- ) {
            abPlaceable a = (abPlaceable) RetVal.get( i );
            AvailableCode AC = a.GetAvailability();

            // check it using the normal routine, with two exceptions
            if( a.GetCritName().equals( "Retractable Blade") ) {
                if( ! m.IsClan() ) {
                    if( m.GetEra() < Constants.ALL_ERA ) {
                        if( m.IsYearRestricted() ) {
                            if( MyOptions.Equip_AllowRBlade ) {
                                if( m.GetYear() < 2420 ) {
                                    RetVal.remove( i );
                                }
                            } else {
                                if( m.GetYear() < 3068 ) {
                                    RetVal.remove( i );
                                }
                            }
                        } else {
                            if( ! MyOptions.Equip_AllowRBlade ) {
                                if( m.GetEra() != Constants.CLAN_INVASION ) {
                                    RetVal.remove( i );
                                }
                            }
                        }
                    }
                } else {
                    RetVal.remove( i );
                }
            } else if( a.GetCritName().equals( "Hatchet" ) ) {
                if( ! m.IsClan() ) {
                    if( m.GetEra() < Constants.ALL_ERA ) {
                        if( m.IsYearRestricted() ) {
                            if( m.GetYear() < 3022 ) {
                                RetVal.remove( i );
                            }
                        } else {
                            if( m.GetEra() != Constants.SUCCESSION && m.GetEra() != Constants.CLAN_INVASION ) {
                                RetVal.remove( i );
                            }
                        }
                    }
                } else {
                    RetVal.remove( i );
                }
            } else {
                if( ! CommonTools.IsAllowed( AC, m )) {
                    RetVal.remove( i );
                }
            }
        }

        if( RetVal.size() < 1 ) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Object[] GetArtillery( Mech m ) {
        // returns an array based on the given specifications of era and year
        Vector RetVal = new Vector(),
               test;
        abPlaceable p;
        AvailableCode AC;

        if( m.IsClan() ) {
            test = CLAR;
        } else {
            test = ISAR;
        }

        for( int i = 0; i < test.size(); i++ ) {
            p = (abPlaceable) test.get( i );
            AC = p.GetAvailability();
            if( CommonTools.IsAllowed( AC, m ) ) {
                RetVal.add( p );
            }
        }

        if( RetVal.size() < 1 ) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public EnergyWeapon GetEnergyWeaponByName( String name, boolean Clan ) {
        // searches the energy weapon database for the named item and returns it
        Vector Test = new Vector();

        if( Clan ) {
            Test = CLEW;
        } else {
            Test = ISEW;
        }

        for( int i = 0; i < Test.size(); i++ ) {
            if( ((abPlaceable) Test.get( i )).GetCritName().equals( name ) ) {
                return (EnergyWeapon) GetCopy( (abPlaceable) Test.get( i ) );
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public BallisticWeapon GetBallisticWeaponByName( String name, boolean Clan ) {
        // searches the ballistic weapon database for the named item and returns it
        Vector Test = new Vector();

        if( Clan ) {
            Test = CLBW;
        } else {
            Test = ISBW;
        }

        for( int i = 0; i < Test.size(); i++ ) {
            if( ((abPlaceable) Test.get( i )).GetCritName().equals( name ) ) {
                return (BallisticWeapon) GetCopy( (abPlaceable) Test.get( i ) );
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public MGArray GetMGArrayByName( String name, boolean Clan ) {
        // searches the mg weapon database for the named item and returns it
        Vector Test = new Vector();

        if( Clan ) {
            Test = CLBW;
        } else {
            Test = ISBW;
        }

        for( int i = 0; i < Test.size(); i++ ) {
            if( ((abPlaceable) Test.get( i )).GetCritName().equals( name ) && ( Test.get( i ) instanceof MGArray ) ) {
                return (MGArray) GetCopy( (abPlaceable) Test.get( i ) );
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public MissileWeapon GetMissileWeaponByName( String name, boolean Clan ) {
        // searches the missile weapon database for the named item and returns it
        Vector Test = new Vector();

        if( Clan ) {
            Test = CLMW;
        } else {
            Test = ISMW;
        }

        for( int i = 0; i < Test.size(); i++ ) {
            if( ((abPlaceable) Test.get( i )).GetCritName().equals( name ) ) {
                return (MissileWeapon) GetCopy( (abPlaceable) Test.get( i ) );
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public PhysicalWeapon GetPhysicalWeaponByName( String name, Mech m ) {
        Vector Test = new Vector();

        if( m.IsClan() ) {
            Test = CLPW;
        } else {
            Test = ISPW;
        }

        for( int i = 0; i < Test.size(); i++ ) {
            if( ((abPlaceable) Test.get( i )).GetCritName().equals( name ) ) {
                PhysicalWeapon p = (PhysicalWeapon) GetCopy( (abPlaceable) Test.get( i ) );
                p.SetOwner( m );
                return p;
            }
        }

        // now check the industrial Physicals
        Test = INPW;

        for( int i = 0; i < Test.size(); i++ ) {
            if( ((abPlaceable) Test.get( i )).GetCritName().equals( name ) ) {
                PhysicalWeapon p = (PhysicalWeapon) GetCopy( (abPlaceable) Test.get( i ) );
                p.SetOwner( m );
                return p;
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public Artillery GetArtilleryByName( String name, boolean Clan ) {
        // searches the artillery weapon database for the named item and returns it
        Vector Test = new Vector();

        if( Clan ) {
            Test = CLAR;
        } else {
            Test = ISAR;
        }

        for( int i = 0; i < Test.size(); i++ ) {
            if( ((abPlaceable) Test.get( i )).GetCritName().equals( name ) ) {
                return (Artillery) GetCopy( (abPlaceable) Test.get( i ) );
            }
        }

        // couldn't find it?  return null
        return null;
    }

    public void RebuildPhysicals( Mech m ) {
        // this method rebuilds the physical weapons if the form's CurMech changes
        Owner = m;
        AvailableCode a;
        PhysicalWeapon addPW;

        ISPW.clear();
        CLPW.clear();
        INPW.clear();

        // hatchet
        a = new AvailableCode( false, 'B', 'X', 'F', 'D', 3022, 0, 0, "LC", "", false, false );
        addPW = new PhysicalWeapon( "Hatchet", "Hatchet", Owner, a );
        addPW.SetStats( 0.06666f, 0.06666f, 0.0f, 0 );
        addPW.SetDamage( 0.2f, 0 );
        addPW.SetSpecials( "PA", "-", 5000.0f, 0.0f, 1.5f, 0.0f, 0.0f, false );
        addPW.SetToHit( -1, -1, -1 );
        ISPW.add( addPW );

        // sword
        a = new AvailableCode( false, 'B', 'X', 'X', 'D', 3058, 0, 0, "DC", "", false, false );
        addPW = new PhysicalWeapon( "Sword", "Sword", Owner, a );
        addPW.SetStats( 0.05f, 0.06666f, 0.0f, 0 );
        addPW.SetDamage( 0.1f, 1 );
        addPW.SetSpecials( "PA", "-", 10000.0f, 0.0f, 1.725f, 0.0f, 0.0f, true );
        addPW.SetToHit( -2, -2, -2 );
        ISPW.add( addPW );

        // retractable blade
        a = new AvailableCode( false, 'B', 'F', 'D', 'D', 2420, 0, 0, "TH", "", false, false );
        addPW = new PhysicalWeapon( "Retractable Blade", "Retractable Blade", Owner, a );
        addPW.SetStats( 0.05f, 0.05f, 0.5f, 1 );
        addPW.SetDamage( 0.1f, 0 );
        addPW.SetSpecials( "PA", "-", 10000.0f, 10000.0f, 1.725f, 0.0f, 0.0f, true );
        addPW.SetToHit( -2, -2, -2 );
        ISPW.add( addPW );

        // chain whip
        a = new AvailableCode( false, 'C', 'X', 'X', 'F', 3071, 0, 0, "WB", "", false, false, 3069, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addPW = new PhysicalWeapon( "Chain Whip","Chain Whip", Owner, a );
        addPW.SetStats(0.0f, 0.0f, 3.0f, 2);
        addPW.SetDamage(0.0f, 1);
        addPW.SetSpecials("PA", "-", 0.0f, 120000.0f, 1.725f, 0.0f, 0.0f, false);
        addPW.SetToHit(-2, -2, -2);
        ISPW.add( addPW );

        // claws
        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3060, 0, 0, "LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addPW = new PhysicalWeapon( "Claws","Claws", Owner, a );
        addPW.SetStats(0.06666f, 0.06666f, 0.0f, 0);
        addPW.SetDamage(0.1428f, 0);
        addPW.SetSpecials("PA", "-", 2800.0f, 0.0f, 1.275f, 0.0f, 0.0f, false);
        addPW.SetToHit(1, 1, 1);
        addPW.SetReplacesHand(true);
        addPW.SetRequiresLowerArm( true );
        ISPW.add( addPW );

        // flail
        a = new AvailableCode( false, 'B', 'X', 'X', 'E', 3057, 0, 0, "FC", "", false, false, 3054, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addPW = new PhysicalWeapon( "Flail","Flail", Owner, a );
        addPW.SetStats(0.0f, 0.0f, 5.0f, 4);
        addPW.SetDamage(0.0f, 9);
        addPW.SetSpecials("PA", "-", 0.0f, 110000.0f, 0.0f, 11.0f, 0.0f, false);
        addPW.SetToHit(1, 1, 1);
        addPW.SetReplacesHand(true);
        addPW.SetRequiresLowerArm( true );
        ISPW.add( addPW );

        // lance
        a = new AvailableCode( false, 'C', 'X', 'X', 'F', 3064, 0, 0, "LA", "", false, false, 3061, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addPW = new PhysicalWeapon( "Lance","Lance", Owner, a );
        addPW.SetStats(0.05f, 0.05f, 0.0f, 0);
        addPW.SetDamage(0.2f, 0);
        addPW.SetSpecials("PA", "-", 3000.0f, 0.0f, 1.0f, 0.0f, 0.0f, false);
        addPW.SetToHit(2, 2, 2);
        addPW.SetRequiresHand(false);
        ISPW.add( addPW );

        // mace
        a = new AvailableCode( false, 'B', 'X', 'X', 'D', 3061, 0, 0, "LA", "", false, false, 3061, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addPW = new PhysicalWeapon( "Mace","ISMace", Owner, a );
        addPW.SetStats(0.1f, 0.1f, 0.0f, 0);
        addPW.SetDamage(0.25f, 0);
        addPW.SetSpecials("PA", "-", 0.0f, 130000.0f, 1.0f, 0.0f, 0.0f, false);
        addPW.SetToHit(1, 1, 1);
        ISPW.add(addPW);

        // vibroblade small
        a = new AvailableCode( false, 'D', 'X', 'X', 'E', 3065, 0, 0, "DC", "", false, false, 3059, true, "DC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addPW = new PhysicalWeapon( "Small Vibroblade","ISSmallVibroBlade", Owner, a );
        addPW.SetStats(0.0f, 0.0f, 3.0f, 1);
        addPW.SetDamage(0.0f, 7);
        addPW.SetHeat(3);
        addPW.SetSpecials("PA", "V", 0.0f, 150000.0f, 0.0f, 12.0f, 0.0f, false);
        addPW.SetToHit(-1, -1, -1);
        ISPW.add(addPW);

        // vibroblade medium
        a = new AvailableCode( false, 'D', 'X', 'X', 'E', 3065, 0, 0, "DC", "", false, false, 3059, true, "DC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addPW = new PhysicalWeapon( "Medium Vibroblade","ISMediumVibroBlade", Owner, a );
        addPW.SetStats(0.0f, 0.0f, 5.0f, 2);
        addPW.SetDamage(0.0f, 10);
        addPW.SetHeat(5);
        addPW.SetSpecials("PA", "V", 0.0f, 400000.0f, 0.0f, 17.0f, 0.0f, false);
        addPW.SetToHit(-1, -1, -1);
        ISPW.add(addPW);

        // vibroblade large
        a = new AvailableCode( false, 'D', 'X', 'X', 'E', 3065, 0, 0, "DC", "", false, false, 3059, true, "DC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addPW = new PhysicalWeapon( "Large Vibroblade","ISLargeVibroBlade", Owner, a );
        addPW.SetStats(0.0f, 0.0f, 7.0f, 4);
        addPW.SetDamage(0.0f, 14);
        addPW.SetHeat(7);
        addPW.SetSpecials("PA", "V", 0.0f, 750000.0f, 0.0f, 24.0f, 0.0f, false);
        addPW.SetToHit(-1, -1, -1);
        ISPW.add(addPW);

        // spikes
        a = new AvailableCode( false, 'C', 'X', 'E', 'E', 3051, 0, 0, "FC", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addPW = new PhysicalWeapon( "Spikes","ISSpikes", Owner, a );
        addPW.SetStats(0.0f, 0.0f, 0.5f, 1);
        addPW.SetDamage(0.0f, 2);
        addPW.SetSpecials("PA", "PB", 50.0f, 0.0f, 0.0f, 0.0f, 4.0f, false);
        addPW.SetToHit(0, 0, 0);
        addPW.SetRequiresLowerArm( false );
        addPW.SetPWClass( ssw.Constants.PW_CLASS_SPIKE );
        addPW.SetAllocations(true, true, true, true, true, false);
        ISPW.add(addPW);

        // small shield
        a = new AvailableCode( false, 'D', 'X', 'X', 'F', 3067, 0, 0, "LA", "", false, false, 3065, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addPW = new PhysicalWeapon( "Small Shield","ISSmallShield", Owner, a );
        addPW.SetStats(0.0f, 0.0f, 2.0f, 3);
        addPW.SetDamage(0.0f, 3);
        addPW.SetSpecials("PA", "PB", 0.0f, 50000.0f, 0.0f, 0.0f, 50.0f, false);
        addPW.SetToHit(-2, -2, -2);
        addPW.SetRequiresLowerArm(false);
        addPW.SetPWClass( ssw.Constants.PW_CLASS_SHIELD );
        ISPW.add(addPW);

        // medium shield
        a = new AvailableCode( false, 'D', 'X', 'X', 'F', 3067, 0, 0, "LA", "", false, false, 3065, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addPW = new PhysicalWeapon( "Medium Shield","ISMediumShield", Owner, a );
        addPW.SetStats(0.0f, 0.0f, 4.0f, 5);
        addPW.SetDamage(0.0f, 5);
        addPW.SetSpecials("PA", "PB", 0.0f, 100000.0f, 0.0f, 0.0f, 135.0f, false);
        addPW.SetToHit(-3, -3, -3);
        addPW.SetRequiresLowerArm(false);
        addPW.SetPWClass( ssw.Constants.PW_CLASS_SHIELD );
        addPW.AddMechModifier( new MechModifier( -1, 0, 0, 0.0f, 0, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, true ));
        ISPW.add(addPW);

        // large shield
        a = new AvailableCode( false, 'D', 'X', 'X', 'F', 3067, 0, 0, "LA", "", false, false, 3065, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addPW = new PhysicalWeapon( "Large Shield","ISLargeShield", Owner, a );
        addPW.SetStats(0.0f, 0.0f, 6.0f, 7);
        addPW.SetDamage(0.0f, 7);
        addPW.SetSpecials("PA", "PB", 0.0f, 300000.0f, 0.0f, 0.0f, 263.0f, false);
        addPW.SetToHit(-4, -4, -4);
        addPW.SetRequiresLowerArm(false);
        addPW.SetPWClass( ssw.Constants.PW_CLASS_SHIELD );
        MechModifier addMod = new MechModifier( -1, 0, 0, 0.0f, 0, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, true );
        addMod.SetCanJump(false);
        addPW.AddMechModifier( addMod );
        ISPW.add(addPW);

        // Talons
        addPW = new Talons( Owner );
        CLPW.add( addPW );

        boolean isClan = m.IsClan();
        String techBaseCode = "IS";
        if(isClan)
            techBaseCode = "CL";
        
        // Backhoe
        a = new AvailableCode(isClan, 'B', 'B', 'B', 'B', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        addPW = new IndustrialPhysicalWeapon("Backhoe", techBaseCode + "Backhoe", Owner, a);
        addPW.SetStats(0.0F, 0.0F, 5, 6);
        addPW.SetDamage(0.0f, 6);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(50000, 8, 0);
        INPW.add(addPW);

        // Chainsaw
        a = new AvailableCode(isClan, 'B', 'D', 'D', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        addPW = new IndustrialPhysicalWeapon("Chainsaw", techBaseCode + "Chainsaw", Owner, a);
        addPW.SetStats(0.0F, 0.0F, 5, 5);
        addPW.SetDamage(0.0f, 5);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 7, 0);
        INPW.add(addPW);

        // Combine
        a = new AvailableCode(isClan, 'B', 'C', 'C', 'C', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        addPW = new IndustrialPhysicalWeapon("Combine", techBaseCode + "Combine", Owner, a);
        addPW.SetStats(0.0F, 0.0F, 2.5f, 4);
        addPW.SetDamage(0.0f, 3);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(75000, 5, 0);
        INPW.add(addPW);
        
        // Dual Saw
        a = new AvailableCode(isClan, 'C', 'D', 'D', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        addPW = new IndustrialPhysicalWeapon("Dual Saw", techBaseCode + "DualSaw", Owner, a);
        addPW.SetStats(0.0F, 0.0F, 7, 7);
        addPW.SetDamage(0.0f, 7);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 9, 0);
        INPW.add(addPW);
        
        // Heavy Duty Pile-Driver
        a = new AvailableCode(isClan, 'D', 'D', 'E', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        addPW = new IndustrialPhysicalWeapon("Heavy Duty Pile-Driver", techBaseCode + "HeavyDutyPile-Driver", Owner, a);
        addPW.SetStats(0.0F, 0.0F, 10, 8);
        addPW.SetDamage(0.0f, 9);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 9, 0);
        INPW.add(addPW);
        
        // Mining Drill
        a = new AvailableCode(isClan, 'B', 'C', 'D', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        addPW = new IndustrialPhysicalWeapon("Mining Drill", techBaseCode + "MiningDrill", Owner, a);
        addPW.SetStats(0.0F, 0.0F, 3, 4);
        addPW.SetDamage(0.0f, 4);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 6, 0);
        INPW.add(addPW);
        
        // Rock Cutter
        a = new AvailableCode(isClan, 'C', 'D', 'D', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        addPW = new IndustrialPhysicalWeapon("Rock Cutter", techBaseCode + "RockCutter", Owner, a);
        addPW.SetStats(0.0F, 0.0F, 5, 5);
        addPW.SetDamage(0.0f, 5);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(100000, 6, 0);
        INPW.add(addPW);

        // Salvage Arm
        if (!m.IsQuad()){
            a = new AvailableCode(isClan, 'D', 'E', 'F', 'E', 2452, 0, 0, "TH", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
            addPW = new IndustrialPhysicalWeapon("Salvage Arm", techBaseCode + "SalvageArm", Owner, a);
            addPW.SetStats(0.0F, 0.0F, 3, 2);
            addPW.SetDamage(0.0f, 0);
            ((IndustrialPhysicalWeapon)addPW).SetSpecials(50000, 0,0);
            INPW.add(addPW);
        }

        // Spot Welder
        a = new AvailableCode(isClan, 'C', 'C', 'D', 'C', 2320, 0, 0, "CC", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        addPW = new IndustrialPhysicalWeapon("Spot Welder", techBaseCode + "SpotWelder", Owner, a);
        addPW.SetStats(0.0F, 0.0F, 2, 1);
        addPW.SetDamage(0.0f, 5);
        addPW.SetHeat(2);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(75000, 5, 0);
        INPW.add(addPW);
        
        // Wrecking Ball
        a = new AvailableCode(isClan, 'A', 'C', 'C', 'C', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        addPW = new IndustrialPhysicalWeapon("Wrecking Ball", techBaseCode + "WreckingBall", Owner, a);
        addPW.SetStats(0.0F, 0.0F, 4, 5);
        addPW.SetDamage(0.0f, 8);
        ((IndustrialPhysicalWeapon)addPW).SetSpecials(80000, 8,0);
        INPW.add(addPW);
    }

    private void BuildWeapons() {
        // this is a very large routine that builds each weapon and places it
        // into the appropriate vector
        AvailableCode a;
        EnergyWeapon addEW;
        MissileWeapon addMW;
        BallisticWeapon addBW;
        MGArray addMGA;
        PhysicalWeapon addPW;
        Artillery addAR;

/*******************************************************************************
 *      START INNER SPHERE WEAPONS
 ******************************************************************************/

        // small laser
        a = new AvailableCode(false, 'C', 'B', 'B', 'B', 2400, 0, 0, "TH", "", false, false);
        addEW = new EnergyWeapon("Small Laser", "ISSmallLaser", "DE", false, a);
        addEW.SetPrintName( "Small Laser" );
        addEW.SetDamage(3, 3, 3);
        addEW.SetHeat(1);
        addEW.SetRange(0, 1, 2, 3);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(0.5f, 1, 11250.0f, 9.0f, 0.0f);
        ISEW.add(addEW);

        // medium laser
        a = new AvailableCode(false, 'C', 'B', 'B', 'B', 2400, 0, 0, "TH", "", false, false);
        addEW = new EnergyWeapon("Medium Laser", "ISMediumLaser", "DE", false, a);
        addEW.SetPrintName( "Medium Laser" );
        addEW.SetDamage(5, 5, 5);
        addEW.SetHeat(3);
        addEW.SetRange(0, 3, 6, 9);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(1.0f, 1, 40000.0f, 46.0f, 0.0f);
        ISEW.add(addEW);

        // large laser
        a = new AvailableCode(false, 'C', 'C', 'D', 'C', 2430, 0, 0, "TH", "", false, false);
        addEW = new EnergyWeapon("Large Laser", "ISLargeLaser", "DE", false, a);
        addEW.SetPrintName( "Large Laser" );
        addEW.SetDamage(8, 8, 8);
        addEW.SetHeat(8);
        addEW.SetRange(0, 5, 10, 15);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(5.0f, 2, 100000.0f, 123.0f, 0.0f);
        ISEW.add(addEW);

        // er small laser
        a = new AvailableCode(false, 'E', 'X', 'X', 'D', 3058, 0, 0, "FW", "", false, false);
        addEW = new EnergyWeapon("ER Small Laser", "ISERSmallLaser", "DE", false, a);
        addEW.SetPrintName( "ER Small Laser" );
        addEW.SetDamage(3, 3, 3);
        addEW.SetHeat(2);
        addEW.SetRange(0, 2, 4, 5);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(0.5f, 1, 11250.0f, 17.0f, 0.0f);
        ISEW.add(addEW);

        // er medium laser
        a = new AvailableCode(false, 'E', 'X', 'X', 'D', 3058, 0, 0, "FW", "", false, false);
        addEW = new EnergyWeapon("ER Medium Laser", "ISERMediumLaser", "DE", false, a);
        addEW.SetPrintName( "ER Medium Laser" );
        addEW.SetDamage(5, 5, 5);
        addEW.SetHeat(5);
        addEW.SetRange(0, 4, 8, 12);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(1.0f, 1, 80000.0f, 62.0f, 0.0f);
        ISEW.add(addEW);

        // er large laser
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2620, 2950, 3037, "TH", "DC", true, true);
        addEW = new EnergyWeapon("ER Large Laser", "ISERLargeLaser", "DE", false, a);
        addEW.SetPrintName( "ER Large Laser" );
        addEW.SetDamage(8, 8, 8);
        addEW.SetHeat(12);
        addEW.SetRange(0, 7, 14, 19);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(5.0f, 2, 200000.0f, 163.0f, 0.0f);
        ISEW.add(addEW);

        // small pulse laser
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2609, 2950, 3037, "TH", "DC", true, true);
        addEW = new EnergyWeapon("Small Pulse Laser", "ISSmallPulseLaser", "P", false, a);
        addEW.SetPrintName( "Small Pulse Laser" );
        addEW.SetDamage(3, 3, 3);
        addEW.SetHeat(2);
        addEW.SetRange(0, 1, 2, 3);
        addEW.SetSpecials("AI", false, false, 0, 0, true);
        addEW.SetStats(1.0f, 1, 16000.0f, 12.0f, 0.0f);
        addEW.SetToHit(-2, -2, -2);
        ISEW.add(addEW);

        // medium pulse laser
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2609, 2950, 3037, "TH", "DC", true, true);
        addEW = new EnergyWeapon("Medium Pulse Laser", "ISMediumPulseLaser", "P", false, a);
        addEW.SetPrintName( "Medium Pulse Laser" );
        addEW.SetDamage(6, 6, 6);
        addEW.SetHeat(4);
        addEW.SetRange(0, 2, 4, 6);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(2.0f, 1, 60000.0f, 48.0f, 0.0f);
        addEW.SetToHit(-2, -2, -2);
        ISEW.add(addEW);

        // large pulse laser
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2609, 2950, 3037, "TH", "DC", true, true);
        addEW = new EnergyWeapon("Large Pulse Laser", "ISLargePulseLaser", "P", false, a);
        addEW.SetPrintName( "Large Pulse Laser" );
        addEW.SetDamage(9, 9, 9);
        addEW.SetHeat(10);
        addEW.SetRange(0, 3, 7, 10);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(7.0f, 2, 175000.0f, 119.0f, 0.0f);
        addEW.SetToHit(-2, -2, -2);
        ISEW.add(addEW);

        // small variable speed laser
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3070, 0, 0, "FWL/WB", "", false, false, 3066, true, "FW/WB", Constants.ADVANCED, Constants.ADVANCED );
        addEW = new EnergyWeapon("Small Variable Speed Laser", "ISSmallVSPLaser", "P", false, a);
        addEW.SetPrintName( "Small VSPL" );
        addEW.SetDamage(5, 4, 3);
        addEW.SetHeat(3);
        addEW.SetRange(0, 2, 4, 6);
        addEW.SetSpecials("V, AI", false, false, 0, 0, true);
        addEW.SetStats( 2.0f, 1, 60000.0f, 22.0f, 0.0f );
        addEW.SetToHit( -3, -2, -1 );
        ISEW.add(addEW);

        // medium variable speed laser
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3070, 0, 0, "FWL/WB", "", false, false, 3066, true, "FW/WB", Constants.ADVANCED, Constants.ADVANCED );
        addEW = new EnergyWeapon("Medium Variable Speed Laser", "ISMediumVSPLaser", "P", false, a);
        addEW.SetPrintName( "Medium VSPL" );
        addEW.SetDamage(9, 7, 5);
        addEW.SetHeat(7);
        addEW.SetRange(0, 2, 5, 9);
        addEW.SetSpecials("V, AI", false, false, 0, 0, true);
        addEW.SetStats( 4.0f, 2, 200000.0f, 56.0f, 0.0f );
        addEW.SetToHit( -3, -2, -1 );
        ISEW.add(addEW);

        // large variable speed laser
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3070, 0, 0, "FWL/WB", "", false, false, 3066, true, "FW/WB", Constants.ADVANCED, Constants.ADVANCED );
        addEW = new EnergyWeapon("Large Variable Speed Laser", "ISLargeVSPLaser", "P", false, a);
        addEW.SetPrintName( "Large VSPL" );
        addEW.SetDamage(11, 9, 7);
        addEW.SetHeat(10);
        addEW.SetRange(0, 4, 8, 15);
        addEW.SetSpecials("V, AI", false, false, 0, 0, true);
        addEW.SetStats(9.0f, 4, 465000.0f, 123.0f, 0.0f);
        addEW.SetToHit( -3, -2, -1 );
        ISEW.add(addEW);

        // small x-pulse laser
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3057, 0, 0, "FC", "", false, false, 3055, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon("Small X-Pulse Laser", "ISSmallXPulseLaser", "P", false, a);
        addEW.SetPrintName( "Small X-Pulse Laser" );
        addEW.SetDamage(3, 3, 3);
        addEW.SetHeat(3);
        addEW.SetRange(0, 2, 4, 5);
        addEW.SetSpecials("AI", false, false, 0, 0, true);
        addEW.SetStats(1.0f, 1, 31000.0f, 21.0f, 0.0f);
        addEW.SetToHit( -2, -2, -2 );
        ISEW.add(addEW);

        // medium x-pulse laser
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3057, 0, 0, "FC", "", false, false, 3055, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon("Medium X-Pulse Laser", "ISMediumXPulseLaser", "P", false, a);
        addEW.SetPrintName( "Medium X-Pulse Laser" );
        addEW.SetDamage(6, 6, 6);
        addEW.SetHeat(6);
        addEW.SetRange(0, 3, 6, 9);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(2.0f, 1, 110000.0f, 71.0f, 0.0f);
        addEW.SetToHit(-2, -2, -2);
        ISEW.add(addEW);

        // large x-pulse laser
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3057, 0, 0, "FC", "", false, false, 3055, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon("Large X-Pulse Laser", "ISLargeXPulseLaser", "P", false, a);
        addEW.SetPrintName( "Large X-Pulse Laser" );
        addEW.SetDamage(9, 9, 9);
        addEW.SetHeat(14);
        addEW.SetRange(0, 5, 10, 15);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(7.0f, 2, 275000.0f, 178.0f, 0.0f);
        addEW.SetToHit(-2, -2, -2);
        ISEW.add(addEW);

        // binary laser cannon
        a = new AvailableCode(false, 'D', 'X', 'E', 'E', 2812, 0, 0, "FW", "", false, false, 2801, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon("Binary Laser Cannon", "ISBlazer", "DE", false, a);
        addEW.SetPrintName( "Binary Laser Cannon" );
        addEW.SetDamage( 12, 12, 12 );
        addEW.SetHeat( 16 );
        addEW.SetRange(0, 5, 10, 15);
        addEW.SetSpecials("-", false, false, 0, 0, true);
        addEW.SetStats(9.0f, 4, 200000.0f, 222.0f, 0.0f);
        addEW.SetToHit( 0, 0, 0 );
        ISEW.add(addEW);

        // light ppc
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3067, 0, 0, "DC", "", false, false);
        addEW = new EnergyWeapon("Light PPC", "ISLightPPC", "DE", false, a);
        addEW.SetPrintName( "Light PPC" );
        addEW.SetDamage(5, 5, 5);
        addEW.SetHeat(5);
        addEW.SetRange(3, 6, 12, 18);
        addEW.SetSpecials("-", true, false, 0, 0, true);
        addEW.SetStats(3.0f, 2, 150000.0f, 88.0f, 0.0f);
        ISEW.add(addEW);

        // light ppc w/ capacitor
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3067, 0, 0, "DC", "", false, false);
        addEW = new EnergyWeapon("Light PPC w/ Capacitor", "ISLightPPC", "DE", false, a);
        addEW.SetPrintName( "Light PPC" );
        addEW.SetDamage(5, 5, 5);
        addEW.SetHeat(5);
        addEW.SetRange(3, 6, 12, 18);
        addEW.SetSpecials("-", true, false, 0, 0, true);
        addEW.SetStats(3.0f, 2, 150000.0f, 132.0f, 0.0f);
        addEW.UseCapacitor( true );
        ISEW.add(addEW);

        // ppc
        a = new AvailableCode(false, 'D', 'C', 'C', 'C', 2460, 0, 0, "TH", "", false, false);
        addEW = new EnergyWeapon("PPC", "ISPPC", "DE", false, a);
        addEW.SetPrintName( "PPC" );
        addEW.SetDamage(10, 10, 10);
        addEW.SetHeat(10);
        addEW.SetRange(3, 6, 12, 18);
        addEW.SetSpecials("-", true, false, 0, 0, true);
        addEW.SetStats(7.0f, 3, 200000.0f, 176.0f, 0.0f);
        ISEW.add(addEW);

        // ppc w/ capacitor
        a = new AvailableCode(false, 'D', 'C', 'C', 'C', 2460, 0, 0, "TH", "", false, false);
        addEW = new EnergyWeapon("PPC w/ Capacitor", "ISPPC", "DE", false, a);
        addEW.SetPrintName( "PPC" );
        addEW.SetDamage(10, 10, 10);
        addEW.SetHeat(10);
        addEW.SetRange(3, 6, 12, 18);
        addEW.SetSpecials("-", true, false, 0, 0, true);
        addEW.SetStats(7.0f, 3, 200000.0f, 264.0f, 0.0f);
        addEW.UseCapacitor( true );
        ISEW.add(addEW);

        // heavy ppc
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3067, 0, 0, "DC", "", false, false);
        addEW = new EnergyWeapon("Heavy PPC", "ISHeavyPPC", "DE", false, a);
        addEW.SetPrintName( "Heavy PPC" );
        addEW.SetDamage(15, 15, 15);
        addEW.SetHeat(15);
        addEW.SetRange(3, 6, 12, 18);
        addEW.SetSpecials("-", true, false, 0, 0, true);
        addEW.SetStats(10.0f, 4, 250000.0f, 317.0f, 0.0f);
        ISEW.add(addEW);

        // heavy ppc w/ capacitor
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3067, 0, 0, "DC", "", false, false);
        addEW = new EnergyWeapon("Heavy PPC w/ Capacitor", "ISHeavyPPC", "DE", false, a);
        addEW.SetPrintName( "Heavy PPC" );
        addEW.SetDamage(15, 15, 15);
        addEW.SetHeat(15);
        addEW.SetRange(3, 6, 12, 18);
        addEW.SetSpecials("-", true, false, 0, 0, true);
        addEW.SetStats(10.0f, 4, 250000.0f, 370.0f, 0.0f);
        addEW.UseCapacitor( true );
        ISEW.add(addEW);

        // er ppc
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2760, 2860, 3037, "TH", "DC", true, true);
        addEW = new EnergyWeapon("ER PPC", "ISERPPC", "DE", false, a);
        addEW.SetPrintName( "ER PPC" );
        addEW.SetDamage(10, 10, 10);
        addEW.SetHeat(15);
        addEW.SetRange(0, 7, 14, 23);
        addEW.SetSpecials("-", true, false, 0, 0, true);
        addEW.SetStats(7.0f, 3, 300000.0f, 229.0f, 0.0f);
        ISEW.add(addEW);

        // er ppc w/ capacitor
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2760, 2860, 3037, "TH", "DC", true, true);
        addEW = new EnergyWeapon("ER PPC w/ Capacitor", "ISERPPC", "DE", false, a);
        addEW.SetPrintName( "ER PPC" );
        addEW.SetDamage(10, 10, 10);
        addEW.SetHeat(15);
        addEW.SetRange(0, 7, 14, 23);
        addEW.SetSpecials("-", true, false, 0, 0, true);
        addEW.SetStats(7.0f, 3, 300000.0f, 343.0f, 0.0f);
        addEW.UseCapacitor( true );
        ISEW.add(addEW);

        // enhanced er ppc
        a = new AvailableCode( false, 'F', 'X', 'F', 'X', 2801, 2825, 0, "CWV", "", true, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon("Enhanced ER PPC", "ISEnhancedERPPC", "DE", false, a);
        addEW.SetPrintName( "Enhanced ER PPC" );
        addEW.SetDamage(12, 12, 12);
        addEW.SetHeat(15);
        addEW.SetRange(0, 7, 14, 23);
        addEW.SetSpecials("-", true, false, 0, 0, true);
        addEW.SetStats( 7.0f, 3, 300000.0f, 332.0f, 0.0f );
        ISEW.add(addEW);

        // snub-nose ppc
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3067, 0, 0, "DC", "", false, false);
        addEW = new EnergyWeapon("Snub-Nose PPC", "ISSNPPC", "DE", false, a);
        addEW.SetPrintName( "Snub-Nose PPC" );
        addEW.SetDamage(10, 8, 5);
        addEW.SetHeat(10);
        addEW.SetRange(0, 9, 13, 15);
        addEW.SetSpecials("V", true, false, 0, 0, true);
        addEW.SetStats(6.0f, 2, 300000.0f, 165.0f, 0.0f);
        ISEW.add(addEW);

        // snub-nose ppc w/ capacitor
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3067, 0, 0, "DC", "", false, false);
        addEW = new EnergyWeapon("Snub-Nose PPC w/ Capacitor", "ISSNPPC", "DE", false, a);
        addEW.SetPrintName( "Snub-Nose PPC" );
        addEW.SetDamage(10, 8, 5);
        addEW.SetHeat(10);
        addEW.SetRange(0, 9, 13, 15);
        addEW.SetSpecials("V", true, false, 0, 0, true);
        addEW.SetStats(6.0f, 2, 300000.0f, 252.0f, 0.0f);
        addEW.UseCapacitor( true );
        ISEW.add(addEW);

        // plasma rifle
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3068, 0, 0, "CC", "", false, false);
        addEW = new EnergyWeapon("Plasma Rifle", "ISPlasmaRifle", "DE", false, a);
        addEW.SetPrintName( "Plasma Rifle" );
        addEW.SetDamage(10, 10, 10);
        addEW.SetHeat(10);
        addEW.SetRange(0, 5, 10, 15);
        addEW.SetSpecials("H/AI", false, true, 10, 1, true);
        addEW.SetStats(6.0f, 2, 260000.0f, 210.0f, 0.0f);
        ISEW.add(addEW);

        // flamer
        a = new AvailableCode(false, 'C', 'B', 'B', 'B', 2025, 0, 0, "WA", "", false, false);
        addEW = new EnergyWeapon("Flamer", "ISFlamer", "DE", false, a);
        addEW.SetPrintName( "Flamer" );
        addEW.SetDamage(2, 2, 2);
        addEW.SetHeat(3);
        addEW.SetRange(0, 1, 2, 3);
        addEW.SetSpecials("H/AI", false, false, 0, 0, false);
        addEW.SetStats(1.0f, 1, 7500.0f, 6.0f, 0.0f);
        ISEW.add(addEW);

        // vehicle flamer
        a = new AvailableCode(false, 'B', 'A', 'A', 'B', 1950, 0, 0, "PS", "", false, false);
        addEW = new EnergyWeapon("Vehicle Flamer", "ISVehicleFlamer", "DE", false, a);
        addEW.SetPrintName( "Vehicle Flamer" );
        addEW.SetDamage(2, 2, 2);
        addEW.SetHeat(3);
        addEW.SetRange(0, 1, 2, 3);
        addEW.SetSpecials("H/AI", false, true, 20, 2, false);
        addEW.SetStats(0.5f, 1, 7500.0f, 5.0f, 0.0f);
        addEW.SetPowerAmp( false );
        ISEW.add(addEW);

        // er flamer
        a = new AvailableCode(false, 'D', 'X', 'X', 'E', 3070, 0, 0, "FS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addEW = new EnergyWeapon("ER Flamer", "ISERFlamer", "DE", false, a);
        addEW.SetPrintName( "ER Flamer" );
        addEW.SetDamage(2, 2, 2);
        addEW.SetHeat(4);
        addEW.SetRange(0, 3, 5, 7);
        addEW.SetSpecials("H/AI", false, false, 0, 0, false);
        addEW.SetStats(1.0f, 1, 15000.0f, 16.0f, 0.0f);
        addEW.SetRequiresFusion( true );
        ISEW.add(addEW);

        // heavy flamer
        a = new AvailableCode(false, 'C', 'X', 'X', 'E', 3068, 0, 0, "LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addEW = new EnergyWeapon("Heavy Flamer", "ISHeavyFlamer", "DE", false, a);
        addEW.SetPrintName( "Heavy Flamer" );
        addEW.SetDamage(4, 4, 4);
        addEW.SetHeat(5);
        addEW.SetRange(0, 2, 3, 4);
        addEW.SetSpecials("H/AI", false, true, 10, 104, false);
        addEW.SetStats(1.5f, 1, 11250.0f, 15.0f, 0.0f);
        addEW.SetPowerAmp( false );
        ISEW.add(addEW);

        // bombast laser
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3064, 0, 0, "LA", "", false, false, 3061, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon("Bombast Laser", "ISBombastLaser", "DE", false, a);
        addEW.SetPrintName( "Bombast Laser" );
        addEW.SetDamage(7, 7, 7);
        addEW.SetHeat(12);
        addEW.SetRange(0, 5, 10, 15);
        addEW.SetSpecials("V", false, false, 0, 0, true);
        addEW.SetStats(7.0f, 3, 200000.0f, 137.0f, 0.0f);
        ISEW.add(addEW);

        // lrm-5
        a = new AvailableCode(false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false);
        addMW = new MissileWeapon("LRM-5", "ISLRM5", "M", false, a);
        addMW.SetPrintName( "LRM-5" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(2);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/5", false, true, 24, 25, false);
        addMW.SetStats(2.0f, 1, 30000.0f, 45.0f, 0.0f);
        addMW.SetMissile(5, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // lrm-10
        a = new AvailableCode(false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false);
        addMW = new MissileWeapon("LRM-10", "ISLRM10", "M", false, a);
        addMW.SetPrintName( "LRM-10" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(4);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/10", false, true, 12, 26, false);
        addMW.SetStats(5.0f, 2, 100000.0f, 90.0f, 0.0f);
        addMW.SetMissile(10, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // lrm-15
        a = new AvailableCode(false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false);
        addMW = new MissileWeapon("LRM-15", "ISLRM15", "M", false, a);
        addMW.SetPrintName( "LRM-15" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(5);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/15", false, true, 8, 27, false);
        addMW.SetStats(7.0f, 3, 175000.0f, 136.0f, 0.0f);
        addMW.SetMissile(15, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // lrm-20
        a = new AvailableCode(false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false);
        addMW = new MissileWeapon("LRM-20", "ISLRM20", "M", false, a);
        addMW.SetPrintName( "LRM-20" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(6);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/20", false, true, 6, 28, false);
        addMW.SetStats(10.0f, 5, 250000.0f, 181.0f, 0.0f);
        addMW.SetMissile(20, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // srm-2
        a = new AvailableCode(false, 'C', 'C', 'C', 'C', 2370, 0, 0, "TH", "", false, false);
        addMW = new MissileWeapon("SRM-2", "ISSRM2", "M", false, a);
        addMW.SetPrintName( "SRM-2" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(2);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/2", false, true, 50, 5, false);
        addMW.SetStats(1.0f, 1, 10000.0f, 21.0f, 0.0f);
        addMW.SetMissile(2, 2, false, true, false);
        addMW.SetArtemisType(Constants.ART4_SRM);
        ISMW.add(addMW);

        // srm-4
        a = new AvailableCode(false, 'C', 'C', 'C', 'C', 2370, 0, 0, "TH", "", false, false);
        addMW = new MissileWeapon("SRM-4", "ISSRM4", "M", false, a);
        addMW.SetPrintName( "SRM-4" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(3);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/4", false, true, 25, 6, false);
        addMW.SetStats(2.0f, 1, 60000.0f, 39.0f, 0.0f);
        addMW.SetMissile(4, 2, false, true, false);
        addMW.SetArtemisType(Constants.ART4_SRM);
        ISMW.add(addMW);

        // srm-6
        a = new AvailableCode(false, 'C', 'C', 'C', 'C', 2370, 0, 0, "TH", "", false, false);
        addMW = new MissileWeapon("SRM-6", "ISSRM6", "M", false, a);
        addMW.SetPrintName( "SRM-6" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(4);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/6", false, true, 15, 7, false);
        addMW.SetStats(3.0f, 2, 80000.0f, 59.0f, 0.0f);
        addMW.SetMissile(6, 2, false, true, false);
        addMW.SetArtemisType(Constants.ART4_SRM);
        ISMW.add(addMW);

        // streak srm-2
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2647, 2845, 3035, "TH", "FW", true, true);
        addMW = new MissileWeapon("Streak SRM-2", "ISStreakSRM2", "M", false, a);
        addMW.SetPrintName( "Streak SRM-2" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(2);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/2", false, true, 50, 11, false);
        addMW.SetStats(1.5f, 1, 15000.0f, 30.0f, 0.0f);
        addMW.SetMissile(2, 2, true, false, false);
        ISMW.add(addMW);

        // streak srm-4
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("Streak SRM-4", "ISStreakSRM4", "M", false, a);
        addMW.SetPrintName( "Streak SRM-4" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(3);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/4", false, true, 25, 12, false);
        addMW.SetStats(3.0f, 1, 90000.0f, 59.0f, 0.0f);
        addMW.SetMissile(4, 2, true, false, false);
        ISMW.add(addMW);

        // streak srm-6
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("Streak SRM-6", "ISStreakSRM6", "M", false, a);
        addMW.SetPrintName( "Streak SRM-6" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(4);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/6", false, true, 15, 13, false);
        addMW.SetStats(4.5f, 2, 120000.0f, 89.0f, 0.0f);
        addMW.SetMissile(6, 2, true, false, false);
        ISMW.add(addMW);

        // mml-3
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3068, 0, 0, "WB", "", false, false);
        addMW = new MissileWeapon("MML-3", "ISMML3", "M", false, a);
        addMW.SetPrintName( "MML-3" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(2);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C3/3", false, true, 40, 33, false);
        addMW.SetStats(1.5f, 2, 45000.0f, 29.0f, 0.0f);
        addMW.SetMissile(3, 3, false, true, false);
        addMW.SetArtemisType(Constants.ART4_MML);
        ISMW.add(addMW);

        // mml-5
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3068, 0, 0, "WB", "", false, false);
        addMW = new MissileWeapon("MML-5", "ISMML5", "M", false, a);
        addMW.SetPrintName( "MML-5" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(3);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/5", false, true, 24, 34, false);
        addMW.SetStats(3.0f, 3, 75000.0f, 45.0f, 0.0f);
        addMW.SetMissile(5, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_MML);
        ISMW.add(addMW);

        // mml-7
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3068, 0, 0, "WB", "", false, false);
        addMW = new MissileWeapon("MML-7", "ISMML7", "M", false, a);
        addMW.SetPrintName( "MML-7" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(4);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/7", false, true, 17, 35, false);
        addMW.SetStats(4.5f, 4, 105000.0f, 67.0f, 0.0f);
        addMW.SetMissile(7, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_MML);
        ISMW.add(addMW);

        // mml-9
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3068, 0, 0, "WB", "", false, false);
        addMW = new MissileWeapon("MML-9", "ISMML9", "M", false, a);
        addMW.SetPrintName( "MML-9" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(5);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/9", false, true, 13, 36, false);
        addMW.SetStats(6.0f, 5, 125000.0f, 86.0f, 0.0f);
        addMW.SetMissile(9, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_MML);
        ISMW.add(addMW);

        // mrm-10
        a = new AvailableCode(false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("MRM-10", "ISMRM10", "M", false, a);
        addMW.SetPrintName( "MRM-10" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(4);
        addMW.SetRange(0, 3, 8, 15);
        addMW.SetSpecials("C/C5/10", false, true, 24, 17, false);
        addMW.SetStats(3.0f, 2, 50000.0f, 56.0f, 0.0f);
        addMW.SetMissile(10, 5, false, false, false);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // mrm-20
        a = new AvailableCode(false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("MRM-20", "ISMRM20", "M", false, a);
        addMW.SetPrintName( "MRM-20" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(6);
        addMW.SetRange(0, 3, 8, 15);
        addMW.SetSpecials("C/C5/20", false, true, 12, 18, false);
        addMW.SetStats(7.0f, 3, 125000.0f, 112.0f, 0.0f);
        addMW.SetMissile(20, 5, false, false, false);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // mrm-30
        a = new AvailableCode(false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("MRM-30", "ISMRM30", "M", false, a);
        addMW.SetPrintName( "MRM-30" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(10);
        addMW.SetRange(0, 3, 8, 15);
        addMW.SetSpecials("C/C5/30", false, true, 8, 19, false);
        addMW.SetStats(10.0f, 5, 225000.0f, 168.0f, 0.0f);
        addMW.SetMissile(30, 5, false, false, false);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // mrm-40
        a = new AvailableCode(false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("MRM-40", "ISMRM40", "M", false, a);
        addMW.SetPrintName( "MRM-40" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(12);
        addMW.SetRange(0, 3, 8, 15);
        addMW.SetSpecials("C/C5/40", false, true, 6, 20, false);
        addMW.SetStats(12.0f, 7, 350000.0f, 224.0f, 0.0f);
        addMW.SetMissile(40, 5, false, false, false);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // thunderbolt-5
        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addMW = new MissileWeapon("Thunderbolt-5", "ISThunderbolt5", "M", false, a);
        addMW.SetPrintName( "Thunderbolt-5" );
        addMW.SetDamage( 5, 5, 5 );
        addMW.SetHeat( 3 );
        addMW.SetRange( 5, 6, 12, 18 );
        addMW.SetSpecials( "-", false, true, 12, 97, true );
        addMW.SetStats( 3.0f, 1, 50000.0f, 64.0f, 0.0f );
        addMW.SetMissile( 1, 1, false, false, false );
        ISMW.add(addMW);

        // thunderbolt-10
        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addMW = new MissileWeapon("Thunderbolt-10", "ISThunderbolt10", "M", false, a);
        addMW.SetPrintName( "Thunderbolt-10" );
        addMW.SetDamage( 10, 10, 10 );
        addMW.SetHeat( 5 );
        addMW.SetRange( 5, 6, 12, 18 );
        addMW.SetSpecials( "-", false, true, 6, 98, true );
        addMW.SetStats( 7.0f, 2, 175000.0f, 127.0f, 0.0f );
        addMW.SetMissile( 1, 1, false, false, false );
        ISMW.add(addMW);

        // thunderbolt-15
        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addMW = new MissileWeapon("Thunderbolt-15", "ISThunderbolt15", "M", false, a);
        addMW.SetPrintName( "Thunderbolt-15" );
        addMW.SetDamage( 15, 15, 15 );
        addMW.SetHeat( 7 );
        addMW.SetRange( 5, 6, 12, 18 );
        addMW.SetSpecials( "-", false, true, 4, 99, true );
        addMW.SetStats( 11.0f, 3, 325000.0f, 229.0f, 0.0f );
        addMW.SetMissile( 1, 1, false, false, false );
        ISMW.add(addMW);

        // thunderbolt-20
        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addMW = new MissileWeapon("Thunderbolt-20", "ISThunderbolt20", "M", false, a);
        addMW.SetPrintName( "Thunderbolt-20" );
        addMW.SetDamage( 20, 20, 20 );
        addMW.SetHeat( 8 );
        addMW.SetRange( 5, 6, 12, 18 );
        addMW.SetSpecials( "-", false, true, 3, 100, true );
        addMW.SetStats( 15.0f, 5, 450000.0f, 305.0f, 0.0f );
        addMW.SetMissile( 1, 1, false, false, false );
        ISMW.add(addMW);

        // rocket launcher-10
        a = new AvailableCode(false, 'B', 'X', 'X', 'B', 3064, 0, 0, "MH", "", false, false);
        addMW = new MissileWeapon("Rocket Launcher 10", "ISRocketLauncher10", "M", false, a);
        addMW.SetPrintName( "Rocket Launcher 10" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(3);
        addMW.SetRange(0, 5, 11, 18);
        addMW.SetSpecials("C/OS/C5/10", false, false, 0, 0, false);
        addMW.SetStats(0.5f, 1, 15000.0f, 18.0f, 0.0f);
        addMW.SetMissile(10, 5, false, false, true);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // rocket launcher-15
        a = new AvailableCode(false, 'B', 'X', 'X', 'B', 3064, 0, 0, "MH", "", false, false);
        addMW = new MissileWeapon("Rocket Launcher 15", "ISRocketLauncher15", "M", false, a);
        addMW.SetPrintName( "Rocket Launcher 15" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(4);
        addMW.SetRange(0, 4, 9, 15);
        addMW.SetSpecials("C/OS/C5/15", false, false, 0, 0, false);
        addMW.SetStats(1.0f, 2, 30000.0f, 23.0f, 0.0f);
        addMW.SetMissile(15, 5, false, false, true);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // rocket launcher-20
        a = new AvailableCode(false, 'B', 'X', 'X', 'B', 3064, 0, 0, "MH", "", false, false);
        addMW = new MissileWeapon("Rocket Launcher 20", "ISRocketLauncher20", "M", false, a);
        addMW.SetPrintName( "Rocket Launcher 20" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(5);
        addMW.SetRange(0, 3, 7, 12);
        addMW.SetSpecials("C/OS/C5/20", false, false, 0, 0, false);
        addMW.SetStats(1.5f, 3, 45000.0f, 24.0f, 0.0f);
        addMW.SetMissile(15, 5, false, false, true);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // narc missile beacon
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2587, 2795, 3035, "TH", "FW", true, true);
        addMW = new MissileWeapon("Narc Missile Beacon", "ISNarcBeacon", "M", false, a);
        addMW.SetPrintName( "Narc Launcher" );
        addMW.SetDamage(0, 0, 0);
        addMW.SetHeat(0);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("E/S", false, true, 6, 37, false);
        addMW.SetStats(3.0f, 2, 100000.0f, 30.0f, 0.0f);
        addMW.SetMissile(1, 1, false, false, false);
        ISMW.add(addMW);

        // improved narc missile beacon
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3062, 0, 0, "CS", "", false, false);
        addMW = new MissileWeapon("iNarc Launcher", "ISImprovedNarc", "M", false, a);
        addMW.SetPrintName( "iNarc Launcher" );
        addMW.SetDamage(0, 0, 0);
        addMW.SetHeat(0);
        addMW.SetRange(0, 4, 9, 15);
        addMW.SetSpecials("E/S", false, true, 4, 38, false);
        addMW.SetStats(5.0f, 3, 250000.0f, 30.0f, 0.0f);
        addMW.SetMissile(1, 1, false, false, false);
        ISMW.add(addMW);

        // enhanced lrm-5
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3058, 0, 0, "FC", "", false, false, 3055, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon("Enhanced LRM-5", "ISNLRM5", "M", false, a);
        addMW.SetPrintName( "Enhanced LRM-5" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(2);
        addMW.SetRange(3, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/5", false, true, 24, 111, false);
        addMW.SetStats(3.0f, 2, 37500.0f, 67.0f, 0.0f);
        addMW.SetMissile(5, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // enhanced lrm-10
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3058, 0, 0, "FC", "", false, false, 3055, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon("Enhanced LRM-10", "ISNLRM10", "M", false, a);
        addMW.SetPrintName( "Enhanced LRM-10" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(4);
        addMW.SetRange(3, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/10", false, true, 12, 112, false);
        addMW.SetStats(6.0f, 4, 125000.0f, 104.0f, 0.0f);
        addMW.SetMissile(10, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // enhanced lrm-15
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3058, 0, 0, "FC", "", false, false, 3055, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon("Enhanced LRM-15", "ISNLRM15", "M", false, a);
        addMW.SetPrintName( "Enhanced LRM-15" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(5);
        addMW.SetRange(3, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/15", false, true, 8, 113, false);
        addMW.SetStats(9.0f, 6, 157000.0f, 157.0f, 0.0f);
        addMW.SetMissile(15, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // enhanced lrm-20
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3058, 0, 0, "FC", "", false, false, 3055, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon("Enhanced LRM-20", "ISNLRM20", "M", false, a);
        addMW.SetPrintName( "Enhanced LRM-20" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(6);
        addMW.SetRange(3, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/20", false, true, 6, 114, false);
        addMW.SetStats(12.0f, 9, 312500.0f, 210.0f, 0.0f);
        addMW.SetMissile(20, 5, false, true, false);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // extended lrm-5
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3054, 0, 0, "FC", "", false, false, 3052, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon("Extended LRM-5", "ISELRM5", "M", false, a);
        addMW.SetPrintName( "Extended LRM-5" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(3);
        addMW.SetRange(10, 12, 22, 38);
        addMW.SetSpecials("C/S/C5/5", false, true, 18, 115, false);
        addMW.SetStats(6.0f, 1, 60000.0f, 52.0f, 0.0f);
        addMW.SetMissile(5, 5, false, false, false);
        addMW.SetArtemisType(Constants.ART4_NONE);
        ISMW.add(addMW);

        // extended lrm-10
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3054, 0, 0, "FC", "", false, false, 3052, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon("Extended LRM-10", "ISELRM10", "M", false, a);
        addMW.SetPrintName( "Extended LRM-10" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(6);
        addMW.SetRange(10, 12, 22, 38);
        addMW.SetSpecials("C/S/C5/10", false, true, 9, 116, false);
        addMW.SetStats(8.0f, 4, 200000.0f, 104.0f, 0.0f);
        addMW.SetMissile(10, 5, false, false, false);
        addMW.SetArtemisType(Constants.ART4_NONE);
        ISMW.add(addMW);

        // extended lrm-15
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3054, 0, 0, "FC", "", false, false, 3052, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon("Extended LRM-15", "ISELRM15", "M", false, a);
        addMW.SetPrintName( "Extended LRM-15" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(8);
        addMW.SetRange(10, 12, 22, 38);
        addMW.SetSpecials("C/S/C5/15", false, true, 6, 117, false);
        addMW.SetStats(12.0f, 6, 350000.0f, 157.0f, 0.0f);
        addMW.SetMissile(15, 5, false, false, false);
        addMW.SetArtemisType(Constants.ART4_NONE);
        ISMW.add(addMW);

        // extended lrm-20
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3054, 0, 0, "FC", "", false, false, 3052, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon("Extended LRM-20", "ISELRM20", "M", false, a);
        addMW.SetPrintName( "Extended LRM-20" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(12);
        addMW.SetRange(10, 12, 22, 38);
        addMW.SetSpecials("C/S/C5/20", false, true, 4, 118, false);
        addMW.SetStats(18.0f, 8, 500000.0f, 210.0f, 0.0f);
        addMW.SetMissile(20, 5, false, false, false);
        addMW.SetArtemisType(Constants.ART4_NONE);
        ISMW.add(addMW);

        // lrm-5 OS
        a = new AvailableCode(false, 'C', 'C', 'F', 'C', 2676, 2800, 3030, "TH", "FW", true, true);
        addMW = new MissileWeapon("LRM-5 (OS)", "ISLRM5 (OS)", "M", false, a);
        addMW.SetPrintName( "LRM-5 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(2);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/5/OS", false, false, 0, 0, false);
        addMW.SetStats(2.5f, 1, 15000.0f, 9.0f, 0.0f);
        addMW.SetMissile(5, 5, false, true, true);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // lrm-10 OS
        a = new AvailableCode(false, 'C', 'C', 'F', 'C', 2676, 2800, 3030, "TH", "FW", true, true);
        addMW = new MissileWeapon("LRM-10 (OS)", "ISLRM10 (OS)", "M", false, a);
        addMW.SetPrintName( "LRM-10 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(4);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/10/OS", false, false, 0, 0, false);
        addMW.SetStats(5.5f, 2, 50000.0f, 18.0f, 0.0f);
        addMW.SetMissile(10, 5, false, true, true);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // lrm-15 OS
        a = new AvailableCode(false, 'C', 'C', 'F', 'C', 2676, 2800, 3030, "TH", "FW", true, true);
        addMW = new MissileWeapon("LRM-15 (OS)", "ISLRM15 (OS)", "M", false, a);
        addMW.SetPrintName( "LRM-15 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(5);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/15/OS", false, false, 0, 0, false);
        addMW.SetStats(7.5f, 3, 87500.0f, 27.0f, 0.0f);
        addMW.SetMissile(15, 5, false, true, true);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // lrm-20 OS
        a = new AvailableCode(false, 'C', 'C', 'F', 'C', 2676, 2800, 3030, "TH", "FW", true, true);
        addMW = new MissileWeapon("LRM-20 (OS)", "ISLRM20 (OS)", "M", false, a);
        addMW.SetPrintName( "LRM-20 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(6);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/20/OS", false, false, 0, 0, false);
        addMW.SetStats(10.5f, 5, 125000.0f, 36.0f, 0.0f);
        addMW.SetMissile(20, 5, false, true, true);
        addMW.SetArtemisType(Constants.ART4_LRM);
        ISMW.add(addMW);

        // srm-2 OS
        a = new AvailableCode(false, 'C', 'C', 'F', 'C', 2676, 2800, 3030, "TH", "FW", true, true);
        addMW = new MissileWeapon("SRM-2 (OS)", "ISSRM2 (OS)", "M", false, a);
        addMW.SetPrintName( "SRM-2 (OS)" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(2);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/2/OS", false, false, 0, 0, false);
        addMW.SetStats(1.5f, 1, 5000.0f, 4.0f, 0.0f);
        addMW.SetMissile(2, 2, false, true, true);
        addMW.SetArtemisType(Constants.ART4_SRM);
        ISMW.add(addMW);

        // srm-4 OS
        a = new AvailableCode(false, 'C', 'C', 'F', 'C', 2676, 2800, 3030, "TH", "FW", true, true);
        addMW = new MissileWeapon("SRM-4 (OS)", "ISSRM4 (OS)", "M", false, a);
        addMW.SetPrintName( "SRM-4 (OS)" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(3);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/4/OS", false, false, 0, 0, false);
        addMW.SetStats(2.5f, 1, 30000.0f, 8.0f, 0.0f);
        addMW.SetMissile(4, 2, false, true, true);
        addMW.SetArtemisType(Constants.ART4_SRM);
        ISMW.add(addMW);

        // srm-6 OS
        a = new AvailableCode(false, 'C', 'C', 'F', 'C', 2676, 2800, 3030, "TH", "FW", true, true);
        addMW = new MissileWeapon("SRM-6 (OS)", "ISSRM6 (OS)", "M", false, a);
        addMW.SetPrintName( "SRM-6 (OS)" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(4);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/6/OS", false, false, 0, 0, false);
        addMW.SetStats(3.5f, 2, 40000.0f, 12.0f, 0.0f);
        addMW.SetMissile(6, 2, false, true, true);
        addMW.SetArtemisType(Constants.ART4_SRM);
        ISMW.add(addMW);

        // streak srm-2 OS
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2676, 2800, 3035, "TH", "FW", true, true);
        addMW = new MissileWeapon("Streak SRM-2 (OS)", "ISStreakSRM2 (OS)", "M", false, a);
        addMW.SetPrintName( "Streak SRM-2 (OS)" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(2);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/2/OS", false, false, 0, 0, false);
        addMW.SetStats(2.0f, 1, 7500.0f, 6.0f, 0.0f);
        addMW.SetMissile(2, 2, true, false, true);
        ISMW.add(addMW);

        // streak srm-4 OS
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("Streak SRM-4 (OS)", "ISStreakSRM4 (OS)", "M", false, a);
        addMW.SetPrintName( "Streak SRM-4 (OS)" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(3);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/4/OS", false, false, 0, 0, false);
        addMW.SetStats(3.5f, 1, 45000.0f, 12.0f, 0.0f);
        addMW.SetMissile(4, 2, true, false, true);
        ISMW.add(addMW);

        // streak srm-6 OS
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("Streak SRM-6 (OS)", "ISStreakSRM6 (OS)", "M", false, a);
        addMW.SetPrintName( "Streak SRM-6 (OS)" );
        addMW.SetDamage(2, 2, 2);
        addMW.SetHeat(4);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("C/C2/6/OS", false, false, 0, 0, false);
        addMW.SetStats(5.0f, 2, 60000.0f, 18.0f, 0.0f);
        addMW.SetMissile(6, 2, true, false, true);
        ISMW.add(addMW);

        // mml-3 OS
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3068, 0, 0, "WB", "", false, false);
        addMW = new MissileWeapon("MML-3 (OS)", "ISMML3 (OS)", "M", false, a);
        addMW.SetPrintName( "MML-3 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(2);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C3/3/OS", false, false, 0, 0, false);
        addMW.SetStats(2.0f, 2, 22500.0f, 6.0f, 0.0f);
        addMW.SetMissile(3, 3, false, true, true);
        addMW.SetArtemisType(Constants.ART4_MML);
        ISMW.add(addMW);

        // mml-5 OS
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3068, 0, 0, "WB", "", false, false);
        addMW = new MissileWeapon("MML-5 (OS)", "ISMML5 (OS)", "M", false, a);
        addMW.SetPrintName( "MML-5 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(3);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/5/OS", false, false, 0, 0, false);
        addMW.SetStats(3.5f, 3, 37500.0f, 9.0f, 0.0f);
        addMW.SetMissile(5, 5, false, true, true);
        addMW.SetArtemisType(Constants.ART4_MML);
        ISMW.add(addMW);

        // mml-7 OS
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3068, 0, 0, "WB", "", false, false);
        addMW = new MissileWeapon("MML-7 (OS)", "ISMML7 (OS)", "M", false, a);
        addMW.SetPrintName( "MML-7 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(4);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/7/OS", false, false, 0, 0, false);
        addMW.SetStats(5.0f, 4, 52500.0f, 13.0f, 0.0f);
        addMW.SetMissile(7, 5, false, true, true);
        addMW.SetArtemisType(Constants.ART4_MML);
        ISMW.add(addMW);

        // mml-9 OS
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3068, 0, 0, "WB", "", false, false);
        addMW = new MissileWeapon("MML-9 (OS)", "ISMML9 (OS)", "M", false, a);
        addMW.SetPrintName( "MML-9 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(5);
        addMW.SetRange(6, 7, 14, 21);
        addMW.SetSpecials("C/S/C5/9/OS", false, false, 0, 0, false);
        addMW.SetStats(6.5f, 5, 62500.0f, 17.0f, 0.0f);
        addMW.SetMissile(9, 5, false, true, true);
        addMW.SetArtemisType(Constants.ART4_MML);
        ISMW.add(addMW);

        // mrm-10 OS
        a = new AvailableCode(false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("MRM-10 (OS)", "ISMRM10 (OS)", "M", false, a);
        addMW.SetPrintName( "MRM-10 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(4);
        addMW.SetRange(0, 3, 8, 15);
        addMW.SetSpecials("C/C5/10/OS", false, false, 0, 0, false);
        addMW.SetStats(3.5f, 2, 25000.0f, 11.0f, 0.0f);
        addMW.SetMissile(10, 5, false, false, true);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // mrm-20 OS
        a = new AvailableCode(false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("MRM-20 (OS)", "ISMRM20 (OS)", "M", false, a);
        addMW.SetPrintName( "MRM-20 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(6);
        addMW.SetRange(0, 3, 8, 15);
        addMW.SetSpecials("C/C5/20/OS", false, false, 0, 0, false);
        addMW.SetStats(7.5f, 3, 62500.0f, 22.0f, 0.0f);
        addMW.SetMissile(20, 5, false, false, true);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // mrm-30 OS
        a = new AvailableCode(false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("MRM-30 (OS)", "ISMRM30 (OS)", "M", false, a);
        addMW.SetPrintName( "MRM-30 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(10);
        addMW.SetRange(0, 3, 8, 15);
        addMW.SetSpecials("C/C5/30/OS", false, false, 0, 0, false);
        addMW.SetStats(10.5f, 5, 112500.0f, 34.0f, 0.0f);
        addMW.SetMissile(30, 5, false, false, true);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // mrm-40 OS
        a = new AvailableCode(false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false);
        addMW = new MissileWeapon("MRM-40 (OS)", "ISMRM40 (OS)", "M", false, a);
        addMW.SetPrintName( "MRM-40 (OS)" );
        addMW.SetDamage(1, 1, 1);
        addMW.SetHeat(12);
        addMW.SetRange(0, 3, 8, 15);
        addMW.SetSpecials("C/C5/40/OS", false, false, 0, 0, false);
        addMW.SetStats(12.5f, 7, 175000.0f, 49.0f, 0.0f);
        addMW.SetMissile(40, 5, false, false, true);
        addMW.SetToHit( 1, 1, 1 );
        ISMW.add(addMW);

        // thunderbolt-5 OS
        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addMW = new MissileWeapon("Thunderbolt-5 (OS)", "ISThunderbolt5OS", "M", false, a);
        addMW.SetPrintName( "Thunderbolt-5 (OS)" );
        addMW.SetDamage( 5, 5, 5 );
        addMW.SetHeat( 3 );
        addMW.SetRange( 5, 6, 12, 18 );
        addMW.SetSpecials( "-", false, false, 0, 0, false );
        addMW.SetStats( 3.5f, 1, 25000.0f, 13.0f, 0.0f );
        addMW.SetMissile( 1, 1, false, false, true );
        ISMW.add(addMW);

        // thunderbolt-10 OS
        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addMW = new MissileWeapon("Thunderbolt-10 (OS)", "ISThunderbolt10OS", "M", false, a);
        addMW.SetPrintName( "Thunderbolt-10 (OS)" );
        addMW.SetDamage( 10, 10, 10 );
        addMW.SetHeat( 5 );
        addMW.SetRange( 5, 6, 12, 18 );
        addMW.SetSpecials( "-", false, false, 0, 0, false );
        addMW.SetStats( 7.5f, 2, 87500.0f, 25.0f, 0.0f );
        addMW.SetMissile( 1, 1, false, false, true );
        ISMW.add(addMW);

        // thunderbolt-15 OS
        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addMW = new MissileWeapon("Thunderbolt-15 (OS)", "ISThunderbolt15OS", "M", false, a);
        addMW.SetPrintName( "Thunderbolt-15 (OS)" );
        addMW.SetDamage( 15, 15, 15 );
        addMW.SetHeat( 7 );
        addMW.SetRange( 5, 6, 12, 18 );
        addMW.SetSpecials( "-", false, false, 0, 0, false );
        addMW.SetStats( 11.5f, 3, 162500.0f, 46.0f, 0.0f );
        addMW.SetMissile( 1, 1, false, false, true );
        ISMW.add(addMW);

        // thunderbolt-20 OS
        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addMW = new MissileWeapon("Thunderbolt-20 (OS)", "ISThunderbolt20OS", "M", false, a);
        addMW.SetPrintName( "Thunderbolt-20 (OS)" );
        addMW.SetDamage( 20, 20, 20 );
        addMW.SetHeat( 8 );
        addMW.SetRange( 5, 6, 12, 18 );
        addMW.SetSpecials( "-", false, false, 0, 0, false );
        addMW.SetStats( 15.5f, 5, 225000.0f, 61.0f, 0.0f );
        addMW.SetMissile( 1, 1, false, false, true );
        ISMW.add(addMW);

        // narc missile beacon OS
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2676, 2795, 3035, "TH", "FW", true, true);
        addMW = new MissileWeapon("Narc Missile Beacon (OS)", "ISNarcBeacon (OS)", "M", false, a);
        addMW.SetPrintName( "Narc Launcher (OS)" );
        addMW.SetDamage(0, 0, 0);
        addMW.SetHeat(0);
        addMW.SetRange(0, 3, 6, 9);
        addMW.SetSpecials("E/S/OS", false, false, 0, 0, false);
        addMW.SetStats(3.5f, 2, 50000.0f, 6.0f, 0.0f);
        addMW.SetMissile(1, 1, false, false, true);
        ISMW.add(addMW);

        // improved narc missile beacon OS
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3062, 0, 0, "CS", "", false, false);
        addMW = new MissileWeapon("iNarc Launcher (OS)", "ISImprovedNarc (OS)", "M", false, a);
        addMW.SetPrintName( "iNarc Launcher (OS)" );
        addMW.SetDamage(0, 0, 0);
        addMW.SetHeat(0);
        addMW.SetRange(0, 4, 9, 15);
        addMW.SetSpecials("E/S/OS", false, false, 0, 0, false);
        addMW.SetStats(5.5f, 3, 125000.0f, 15.0f, 0.0f);
        addMW.SetMissile(1, 1, false, false, true);
        ISMW.add(addMW);

        // autocannon 2
        a = new AvailableCode(false, 'C', 'C', 'D', 'D', 2300, 0, 0, "TH", "", false, false);
        addBW = new BallisticWeapon("Autocannon/2", "ISAC2", "DB", false, a);
        addBW.SetPrintName( "Autocannon/2" );
        addBW.SetDamage(2, 2, 2);
        addBW.SetHeat(1);
        addBW.SetRange(4, 8, 16, 24);
        addBW.SetSpecials("S", true, true, 45, 40, true, true);
        addBW.SetStats(6.0f, 1, 75000.0f, 37.0f, 0.0f);
        ISBW.add(addBW);

        // autocannon 5
        a = new AvailableCode(false, 'C', 'C', 'C', 'D', 2250, 0, 0, "TH", "", false, false);
        addBW = new BallisticWeapon("Autocannon/5", "ISAC5", "DB", false, a);
        addBW.SetPrintName( "Autocannon/5" );
        addBW.SetDamage(5, 5, 5);
        addBW.SetHeat(1);
        addBW.SetRange(3, 6, 12, 18);
        addBW.SetSpecials("S", true, true, 20, 41, true, true);
        addBW.SetStats(8.0f, 4, 125000.0f, 70.0f, 0.0f);
        ISBW.add(addBW);

        // autocannon 10
        a = new AvailableCode(false, 'C', 'C', 'D', 'D', 2460, 0, 0, "TH", "", false, false);
        addBW = new BallisticWeapon("Autocannon/10", "ISAC10", "DB", false, a);
        addBW.SetPrintName( "Autocannon/10" );
        addBW.SetDamage(10, 10, 10);
        addBW.SetHeat(3);
        addBW.SetRange(0, 5, 10, 15);
        addBW.SetSpecials("S", true, true, 10, 42, true, true);
        addBW.SetStats(12.0f, 7, 200000.0f, 123.0f, 0.0f);
        ISBW.add(addBW);

        // autocannon 20
        a = new AvailableCode(false, 'C', 'D', 'E', 'D', 2500, 0, 0, "LC", "", false, false);
        addBW = new BallisticWeapon("Autocannon/20", "ISAC20", "DB", false, a);
        addBW.SetPrintName( "Autocannon/20" );
        addBW.SetDamage(20, 20, 20);
        addBW.SetHeat(7);
        addBW.SetRange(0, 3, 6, 9);
        addBW.SetSpecials("S", true, true, 5, 43, true, true);
        addBW.SetStats(14.0f, 10, 300000.0f, 178.0f, 0.0f);
        addBW.SetAllocations(true, true, true, true, true, true);
        ISBW.add(addBW);

        // lb 2-x ac
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3058, 0, 0, "FS", "", false, false);
        addBW = new BallisticWeapon("LB 2-X AC", "ISLBXAC2", "DB", false, a);
        addBW.SetPrintName( "LB 2-X AC" );
        addBW.SetDamage(2, 2, 2);
        addBW.SetHeat(1);
        addBW.SetRange(4, 9, 18, 27);
        addBW.SetSpecials("C/S/F", true, true, 45, 50, true, true);
        addBW.SetStats(6.0f, 4, 150000.0f, 42.0f, 0.0f);
        addBW.SetBallistics(false, false, false, true);
        ISBW.add(addBW);

        // lb 5-x ac
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3058, 0, 0, "FS", "", false, false);
        addBW = new BallisticWeapon("LB 5-X AC", "ISLBXAC5", "DB", false, a);
        addBW.SetPrintName( "LB 5-X AC" );
        addBW.SetDamage(5, 5, 5);
        addBW.SetHeat(1);
        addBW.SetRange(3, 7, 14, 21);
        addBW.SetSpecials("C/S/F", true, true, 20, 51, true, true);
        addBW.SetStats(8.0f, 5, 250000.0f, 83.0f, 0.0f);
        addBW.SetBallistics(false, false, false, true);
        ISBW.add(addBW);

        // lb 10-x ac
        a = new AvailableCode(false, 'E', 'E', 'F', 'D', 2595, 2840, 3035, "TH", "FC", true, true);
        addBW = new BallisticWeapon("LB 10-X AC", "ISLBXAC10", "DB", false, a);
        addBW.SetPrintName( "LB 10-X AC" );
        addBW.SetDamage(10, 10, 10);
        addBW.SetHeat(2);
        addBW.SetRange(0, 6, 12, 18);
        addBW.SetSpecials("C/S/F", true, true, 10, 52, true, true);
        addBW.SetStats(11.0f, 6, 400000.0f, 148.0f, 0.0f);
        addBW.SetBallistics(false, false, false, true);
        ISBW.add(addBW);

        // lb 20-x ac
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3058, 0, 0, "FS", "", false, false);
        addBW = new BallisticWeapon("LB 20-X AC", "ISLBXAC20", "DB", false, a);
        addBW.SetPrintName( "LB 20-X AC" );
        addBW.SetDamage(20, 20, 20);
        addBW.SetHeat(6);
        addBW.SetRange(0, 4, 8, 12);
        addBW.SetSpecials("C/S/F", true, true, 5, 53, true, true);
        addBW.SetStats(14.0f, 11, 600000.0f, 237.0f, 0.0f);
        addBW.SetBallistics(false, false, false, true);
        addBW.SetAllocations(true, true, true, true, true, true);
        ISBW.add(addBW);

        // light ac/2
        a = new AvailableCode(false, 'D', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addBW = new BallisticWeapon("Light AC/2", "ISLAC2", "DB", false, a);
        addBW.SetPrintName( "Light AC/2" );
        addBW.SetDamage(2, 2, 2);
        addBW.SetHeat(1);
        addBW.SetRange(0, 6, 12, 18);
        addBW.SetSpecials("S", true, true, 45, 58, true, true);
        addBW.SetStats(4.0f, 1, 100000.0f, 30.0f, 0.0f);
        ISBW.add(addBW);

        // light ac/5
        a = new AvailableCode(false, 'D', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addBW = new BallisticWeapon("Light AC/5", "ISLAC5", "DB", false, a);
        addBW.SetPrintName( "Light AC/5" );
        addBW.SetDamage(5, 5, 5);
        addBW.SetHeat(1);
        addBW.SetRange(0, 5, 10, 15);
        addBW.SetSpecials("S", true, true, 20, 59, true, true);
        addBW.SetStats(5.0f, 2, 150000.0f, 62.0f, 0.0f);
        ISBW.add(addBW);

        // rotary ac/2
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3062, 0, 0, "FS", "", false, false);
        addBW = new BallisticWeapon("Rotary AC/2", "ISRotaryAC2", "DB", false, a);
        addBW.SetPrintName( "Rotary AC/2" );
        addBW.SetDamage(2, 2, 2);
        addBW.SetHeat(1);
        addBW.SetRange(0, 6, 12, 18);
        addBW.SetSpecials("R/C/R6", true, true, 45, 60, true, false);
        addBW.SetStats(8.0f, 3, 175000.0f, 118.0f, 0.0f);
        addBW.SetBallistics(false, false, true, false);
        ISBW.add(addBW);

        // rotary ac/5
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3062, 0, 0, "FS", "", false, false);
        addBW = new BallisticWeapon("Rotary AC/5", "ISRotaryAC5", "DB", false, a);
        addBW.SetPrintName( "Rotary AC/5" );
        addBW.SetDamage(5, 5, 5);
        addBW.SetHeat(1);
        addBW.SetRange(0, 5, 10, 15);
        addBW.SetSpecials("R/C/R6", true, true, 20, 61, true, false);
        addBW.SetStats(10.0f, 6, 275000.0f, 247.0f, 0.0f);
        addBW.SetBallistics(false, false, true, false);
        ISBW.add(addBW);

        // ultra ac/2
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3057, 0, 0, "FW", "", false, false);
        addBW = new BallisticWeapon("Ultra AC/2", "ISUltraAC2", "DB", false, a);
        addBW.SetPrintName( "Ultra AC/2" );
        addBW.SetDamage(2, 2, 2);
        addBW.SetHeat(1);
        addBW.SetRange(3, 8, 17, 25);
        addBW.SetSpecials("R/C/R2", true, true, 45, 62, true, false);
        addBW.SetStats(7.0f, 3, 120000.0f, 56.0f, 0.0f);
        addBW.SetBallistics(false, true, false, false);
        ISBW.add(addBW);

        // ultra ac/5
        a = new AvailableCode(false, 'E', 'D', 'F', 'D', 2640, 2915, 3035, "TH", "FC", true, true);
        addBW = new BallisticWeapon("Ultra AC/5", "ISUltraAC5", "DB", false, a);
        addBW.SetPrintName( "Ultra AC/5" );
        addBW.SetDamage(5, 5, 5);
        addBW.SetHeat(1);
        addBW.SetRange(2, 6, 13, 20);
        addBW.SetSpecials("R/C/R2", true, true, 20, 63, true, false);
        addBW.SetStats(9.0f, 5, 200000.0f, 112.0f, 0.0f);
        addBW.SetBallistics(false, true, false, false);
        ISBW.add(addBW);

        // ultra ac/10
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3057, 0, 0, "FW", "", false, false);
        addBW = new BallisticWeapon("Ultra AC/10", "ISUltraAC10", "DB", false, a);
        addBW.SetPrintName( "Ultra AC/10" );
        addBW.SetDamage(10, 10, 10);
        addBW.SetHeat(4);
        addBW.SetRange(0, 6, 12, 18);
        addBW.SetSpecials("R/C/R2", true, true, 10, 64, true, false);
        addBW.SetStats(13.0f, 7, 320000.0f, 210.0f, 0.0f);
        addBW.SetBallistics(false, true, false, false);
        ISBW.add(addBW);

        // ultra ac/20
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3060, 0, 0, "LA", "", false, false);
        addBW = new BallisticWeapon("Ultra AC/20", "ISUltraAC20", "DB", false, a);
        addBW.SetPrintName( "Ultra AC/20" );
        addBW.SetDamage(20, 20, 20);
        addBW.SetHeat(8);
        addBW.SetRange(0, 3, 7, 10);
        addBW.SetSpecials("R/C/R2", true, true, 5, 65, true, false);
        addBW.SetStats(15.0f, 10, 480000.0f, 281.0f, 0.0f);
        addBW.SetBallistics(false, true, false, false);
        addBW.SetAllocations(true, true, true, true, true, true);
        ISBW.add(addBW);

        // hyper-velocity autocannon 2
        a = new AvailableCode(false, 'D', 'X', 'X', 'F', 3059, 0, 0, "CC", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon("Hyper-Velocity Autocannon/2", "ISHVAC2", "DB", false, a);
        addBW.SetPrintName( "HV AC/2" );
        addBW.SetDamage(2, 2, 2);
        addBW.SetHeat(1);
        addBW.SetRange(3, 10, 20, 35);
        addBW.SetSpecials("X", true, true, 30, 94, true, true);
        addBW.SetBallistics( true, false, false, false );
        addBW.SetStats(8.0f, 2, 100000.0f, 53.0f, 0.0f);
        ISBW.add(addBW);

        // hyper-velocity autocannon 5
        a = new AvailableCode(false, 'D', 'X', 'X', 'F', 3059, 0, 0, "CC", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon("Hyper-Velocity Autocannon/5", "ISHVAC5", "DB", false, a);
        addBW.SetPrintName( "HV AC/5" );
        addBW.SetDamage(5, 5, 5);
        addBW.SetHeat(3);
        addBW.SetRange(3, 8, 16, 28);
        addBW.SetSpecials("X", true, true, 15, 95, true, true);
        addBW.SetBallistics( true, false, false, false );
        addBW.SetStats(12.0f, 4, 160000.0f, 109.0f, 0.0f);
        ISBW.add(addBW);

        // hyper-velocity autocannon 10
        a = new AvailableCode(false, 'D', 'X', 'X', 'F', 3059, 0, 0, "CC", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon("Hyper-Velocity Autocannon/10", "ISHVAC10", "DB", false, a);
        addBW.SetPrintName( "HV AC/10" );
        addBW.SetDamage( 10, 10, 10 );
        addBW.SetHeat(7);
        addBW.SetRange(0, 6, 12, 20);
        addBW.SetSpecials("X", true, true, 8, 96, true, true);
        addBW.SetBallistics( true, false, false, false );
        addBW.SetStats(14.0f, 6, 230000.0f, 158.0f, 0.0f);
        ISBW.add(addBW);

        // light gauss rifle
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3056, 0, 0, "FW", "", false, false);
        addBW = new BallisticWeapon("Light Gauss Rifle", "ISLightGaussRifle", "DB", false, a);
        addBW.SetPrintName( "Light Gauss Rifle" );
        addBW.SetDamage(8, 8, 8);
        addBW.SetHeat(1);
        addBW.SetRange(3, 8, 17, 25);
        addBW.SetSpecials("X", true, true, 16, 70, true, false);
        addBW.SetStats(12.0f, 5, 275000.0f, 159.0f, 0.0f);
        addBW.SetBallistics(true, false, false, false);
        ISBW.add(addBW);

        // gauss rifle
        a = new AvailableCode(false, 'E', 'D', 'F', 'D', 2590, 2865, 3040, "TH", "FW", true, true);
        addBW = new BallisticWeapon("Gauss Rifle", "ISGaussRifle", "DB", false, a);
        addBW.SetPrintName( "Gauss Rifle" );
        addBW.SetDamage(15, 15, 15);
        addBW.SetHeat(1);
        addBW.SetRange(2, 7, 15, 22);
        addBW.SetSpecials("X", true, true, 8, 71, true, false);
        addBW.SetStats(15.0f, 7, 300000.0f, 320.0f, 0.0f);
        addBW.SetBallistics(true, false, false, false);
        ISBW.add(addBW);

        // heavy gauss rifle
        a = new AvailableCode(false, 'E', 'X', 'X', 'E', 3061, 0, 0, "LA", "", false, false);
        addBW = new BallisticWeapon("Heavy Gauss Rifle", "ISHeavyGaussRifle", "DB", false, a);
        addBW.SetPrintName( "Heavy Gauss Rifle" );
        addBW.SetDamage(25, 20, 10);
        addBW.SetHeat(2);
        addBW.SetRange(4, 6, 13, 20);
        addBW.SetSpecials("X", true, true, 4, 72, true, false);
        addBW.SetStats(18.0f, 11, 500000.0f, 346.0f, 0.0f);
        addBW.SetBallistics(true, false, false, false);
        addBW.SetAllocations(false, true, true, false, false, true);
        addBW.SetRequiresNuclear(true);
        ISBW.add(addBW);

        // improved heavy gauss rifle
        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3065, 0, 0, "LA", "", false, false, 3061, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "Improved Heavy Gauss Rifle", "ISImprovedHeavyGaussRifle", "DB", false, a );
        addBW.SetPrintName( "Imp. Heavy Gauss Rifle" );
        addBW.SetDamage( 22, 22, 22 );
        addBW.SetHeat( 2 );
        addBW.SetRange( 3, 6, 12, 19 );
        addBW.SetSpecials( "X", true, true, 4, 84, true, false );
        addBW.SetStats( 20.0f, 11, 700000.0f, 385.0f, 0.0f );
        addBW.SetBallistics( true, false, false, false );
        addBW.SetAllocations( false, true, true, false, false, true );
        addBW.SetRequiresNuclear( true );
        ISBW.add( addBW );

        // magshot gauss
        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3072, 0, 0, "FS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addBW = new BallisticWeapon( "Magshot Gauss Rifle", "ISMagshotGR", "DB", false, a );
        addBW.SetPrintName( "Magshot" );
        addBW.SetDamage( 2, 2, 2 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 0, 3, 6, 9 );
        addBW.SetSpecials( "X", true, true, 50, 107, true, false );
        addBW.SetStats( 0.5f, 2, 8500.0f, 15.0f, 0.0f );
        addBW.SetBallistics( true, false, false, false );
        addBW.SetAllocations( true, true, true, true, true, false );
        addBW.SetRequiresNuclear( false );
        ISBW.add( addBW );

        // silver bullet gauss
        a = new AvailableCode( false, 'D', 'X', 'X', 'F', 3051, 0, 0, "FC", "", false, false, 3050, true, "FS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "Silver Bullet Gauss", "ISSBGR", "DB", false, a );
        addBW.SetPrintName( "Silver Bullet Gauss" );
        addBW.SetDamage( 15, 15, 15 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 2, 7, 15, 22 );
        addBW.SetSpecials( "C, F, X", true, true, 50, 108, false, false );
        addBW.SetStats( 15.0f, 7, 350000.0f, 198.0f, 0.0f );
        addBW.SetBallistics( true, false, false, true );
        addBW.SetAllocations( false, true, true, true, false, false );
        addBW.SetRequiresNuclear( true );
        ISBW.add( addBW );

        // mech taser
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3067, 0, 0, "FS", "", false, false, 3065, true, "FS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "BattleMech Taser", "ISBattleMechTaser", "DB", false, a );
        addBW.SetPrintName( "Mech Taser" );
        addBW.SetDamage( 1, 1, 1 );
        addBW.SetHeat( 6 );
        addBW.SetRange( 0, 1, 2, 4 );
        addBW.SetSpecials( "X", false, true, 5, 106, false, false );
        addBW.SetStats( 4.0f, 3, 200000.0f, 40.0f, 0.0f );
        addBW.SetBallistics( true, false, false, false );
        addBW.SetRequiresFusion( true );
        addBW.SetToHit( 1, 1, 1 );
        ISBW.add(addBW);

        // light rifle
        a = new AvailableCode(false, 'B', 'C', 'F', 'X', 1950, 3050, 0, "PS", "", true, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon("Light Rifle", "ISLightRifle", "DB", false, a);
        addBW.SetPrintName( "Light Rifle" );
        addBW.SetDamage( 3, 3, 3 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 0, 4, 8, 12 );
        addBW.SetSpecials( "-", true, true, 18, 101, true, false );
        addBW.SetStats( 3.0f, 1, 37750.0f, 21.0f, 0.0f );
        ISBW.add(addBW);

        // medium rifle
        a = new AvailableCode(false, 'B', 'C', 'F', 'X', 1950, 3050, 0, "PS", "", true, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon("Medium Rifle", "ISMediumRifle", "DB", false, a);
        addBW.SetPrintName( "Medium Rifle" );
        addBW.SetDamage( 6, 6, 6 );
        addBW.SetHeat( 2 );
        addBW.SetRange( 1, 5, 10, 15 );
        addBW.SetSpecials( "-", true, true, 9, 102, true, false );
        addBW.SetStats( 5.0f, 2, 75500.0f, 51.0f, 0.0f );
        ISBW.add(addBW);

        // heavy rifle
        a = new AvailableCode(false, 'B', 'C', 'F', 'X', 1950, 3050, 0, "PS", "", true, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon("Heavy Rifle", "ISHeavyRifle", "DB", false, a);
        addBW.SetPrintName( "Heavy Rifle" );
        addBW.SetDamage( 9, 9, 9 );
        addBW.SetHeat( 2 );
        addBW.SetRange( 2, 6, 12, 18 );
        addBW.SetSpecials( "-", true, true, 6, 103, true, false );
        addBW.SetStats( 8.0f, 3, 90000.0f, 91.0f, 0.0f );
        ISBW.add(addBW);

        // light machine gun
        a = new AvailableCode(false, 'B', 'X', 'X', 'C', 3068, 0, 0, "CC", "", false, false);
        addBW = new BallisticWeapon("Light Machine Gun", "ISLightMG", "DB", false, a);
        addBW.SetPrintName( "LMG" );
        addBW.SetDamage(1, 1, 1);
        addBW.SetHeat(0);
        addBW.SetRange(0, 2, 4, 6);
        addBW.SetSpecials("AI", false, true, 200, 78, false, false);
        addBW.SetStats(0.5f, 1, 5000.0f, 5.0f, 0.0f);
        ISBW.add(addBW);

        // machine gun
        a = new AvailableCode(false, 'B', 'A', 'A', 'B', 1950, 0, 0, "PS", "", false, false);
        addBW = new BallisticWeapon("Machine Gun", "ISMachine Gun", "DB", false, a);
        addBW.SetPrintName( "MG" );
        addBW.SetDamage(2, 2, 2);
        addBW.SetHeat(0);
        addBW.SetRange(0, 1, 2, 3);
        addBW.SetSpecials("AI", false, true, 200, 48, false, false);
        addBW.SetStats(0.5f, 1, 5000.0f, 5.0f, 0.0f);
        ISBW.add(addBW);

        // heavy machine gun
        a = new AvailableCode(false, 'B', 'X', 'X', 'C', 3068, 0, 0, "TC", "", false, false);
        addBW = new BallisticWeapon("Heavy Machine Gun", "ISHeavyMG", "DB", false, a);
        addBW.SetPrintName( "HMG" );
        addBW.SetDamage(3, 3, 3);
        addBW.SetHeat(0);
        addBW.SetRange(0, 1, 2, 0);
        addBW.SetSpecials("AI", false, true, 100, 79, false, false);
        addBW.SetStats(1.0f, 1, 7500.0f, 6.0f, 0.0f);
        ISBW.add(addBW);

        // MGA light machine gun
        a = new AvailableCode(false, 'B', 'X', 'X', 'C', 3068, 0, 0, "CC", "", false, false);
        addBW = new BallisticWeapon("Light Machine Gun", "ISLightMG", "DB", false, a);
        addBW.SetPrintName( "LMG" );
        addBW.SetDamage(1, 1, 1);
        addBW.SetHeat(0);
        addBW.SetRange(0, 2, 4, 6);
        addBW.SetSpecials("AI", false, true, 200, 78, false, false);
        addBW.SetStats(0.0f, 1, 5000.0f, 5.0f, 0.0f);
        addBW.SetLocationLinked(true);

        // LMGA 2
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addMGA = new MGArray( addBW, 2, 0.5f, false, a);
        ISBW.add(addMGA);

        // LMGA 3
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addMGA = new MGArray( addBW, 3, 0.5f, false, a);
        ISBW.add(addMGA);

        // LMGA 4
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addMGA = new MGArray( addBW, 4, 0.5f, false, a);
        ISBW.add(addMGA);

        // machine gun
        a = new AvailableCode(false, 'B', 'A', 'A', 'B', 1950, 0, 0, "PS", "", false, false);
        addBW = new BallisticWeapon("Machine Gun", "ISMachine Gun", "DB", false, a);
        addBW.SetPrintName( "MG" );
        addBW.SetDamage(2, 2, 2);
        addBW.SetHeat(0);
        addBW.SetRange(0, 1, 2, 3);
        addBW.SetSpecials("AI", false, true, 200, 48, false, false);
        addBW.SetStats(0.0f, 1, 5000.0f, 5.0f, 0.0f);
        addBW.SetLocationLinked(true);

        // MGA 2
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addMGA = new MGArray(addBW, 2, 0.5f, false, a);
        ISBW.add(addMGA);

        // MGA 3
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addMGA = new MGArray(addBW, 3, 0.5f, false, a);
        ISBW.add(addMGA);

        // MGA 4
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addMGA = new MGArray(addBW, 4, 0.5f, false, a);
        ISBW.add(addMGA);

        // heavy machine gun
        a = new AvailableCode(false, 'B', 'X', 'X', 'C', 3068, 0, 0, "TC", "", false, false);
        addBW = new BallisticWeapon("Heavy Machine Gun", "ISHeavyMG", "DB", false, a);
        addBW.SetPrintName( "HMG" );
        addBW.SetDamage(3, 3, 3);
        addBW.SetHeat(0);
        addBW.SetRange(0, 1, 2, 0);
        addBW.SetSpecials("AI", false, true, 100, 79, false, false);
        addBW.SetStats(0.0f, 1, 7500.0f, 6.0f, 0.0f);
        addBW.SetLocationLinked(true);

        // HMGA 2
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addMGA = new MGArray(addBW, 2, 1.0f, false, a);
        ISBW.add(addMGA);

        // HMGA 3
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addMGA = new MGArray(addBW, 3, 1.0f, false, a);
        ISBW.add(addMGA);

        // HMGA 4
        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false);
        addMGA = new MGArray(addBW, 4, 1.0f, false, a);
        ISBW.add(addMGA);

        // Fluid Gun
        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addBW = new BallisticWeapon("Fluid Gun", "ISFluidGun", "DB", false, a);
        addBW.SetPrintName( "Fluid Gun" );
        addBW.SetDamage( 0, 0, 0 );
        addBW.SetHeat( 0 );
        addBW.SetRange( 0, 1, 2, 3 );
        addBW.SetSpecials( "S", false, true, 20, 134, true, true );
        addBW.SetStats( 2.0f, 2, 35000.0f, 6.0f, 0.0f );
        ISBW.add(addBW);

// These are created with rebuild physicals. Don't need to be made here.
//         // hatchet
//        a = new AvailableCode( false, 'B', 'X', 'F', 'D', 3022, 0, 0, "LC", "", false, false );
//        addPW = new PhysicalWeapon( "Hatchet", "Hatchet", Owner, a );
//        addPW.SetStats( 0.06666f, 0.06666f, 0.0f, 0 );
//        addPW.SetDamage( 0.2f, 0 );
//        addPW.SetSpecials( "PA", "-", 5000.0f, 0.0f, 1.5f, 0.0f, 0.0f, false );
//        addPW.SetToHit( -1, -1, -1 );
//        ISPW.add( addPW );
//
//        // sword
//        a = new AvailableCode( false, 'B', 'X', 'X', 'D', 3058, 0, 0, "DC", "", false, false );
//        addPW = new PhysicalWeapon( "Sword", "Sword", Owner, a );
//        addPW.SetStats( 0.05f, 0.06666f, 0.0f, 0 );
//        addPW.SetDamage( 0.1f, 1 );
//        addPW.SetSpecials( "PA", "-", 10000.0f, 0.0f, 1.725f, 0.0f, 0.0f, true );
//        addPW.SetToHit( -2, -2, -2 );
//        ISPW.add( addPW );
//
//        // retractable blade
//        a = new AvailableCode( false, 'B', 'F', 'D', 'D', 2420, 0, 0, "TH", "", false, false );
//        addPW = new PhysicalWeapon( "Retractable Blade", "Retractable Blade", Owner, a );
//        addPW.SetStats( 0.05f, 0.05f, 0.5f, 1 );
//        addPW.SetDamage( 0.1f, 0 );
//        addPW.SetSpecials( "PA", "-", 10000.0f, 10000.0f, 1.725f, 0.0f, 0.0f, true );
//        addPW.SetToHit( -2, -2, -2 );
//        ISPW.add( addPW );
//
//        // chain whip
//        a = new AvailableCode( false, 'C', 'X', 'X', 'F', 3071, 0, 0, "WB", "", false, false, 3069, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
//        addPW = new PhysicalWeapon( "Chain Whip","Chain Whip", Owner, a );
//        addPW.SetStats(0.0f, 0.0f, 3.0f, 2);
//        addPW.SetDamage(0.0f, 1);
//        addPW.SetSpecials("PA", "-", 0.0f, 120000.0f, 1.725f, 0.0f, 0.0f, false);
//        addPW.SetToHit(-2, -2, -2);
//        ISPW.add( addPW );
//
//        // claws
//        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3060, 0, 0, "LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
//        addPW = new PhysicalWeapon( "Claws","Claws", Owner, a );
//        addPW.SetStats(0.06666f, 0.06666f, 0.0f, 0);
//        addPW.SetDamage(0.1428f, 0);
//        addPW.SetSpecials("PA", "-", 2800.0f, 0.0f, 1.275f, 0.0f, 0.0f, false);
//        addPW.SetToHit(1, 1, 1);
//        addPW.SetReplacesHand(true);
//        addPW.SetRequiresLowerArm( true );
//        ISPW.add( addPW );
//
//        // flail
//        a = new AvailableCode( false, 'B', 'X', 'X', 'E', 3057, 0, 0, "FC", "", false, false, 3054, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
//        addPW = new PhysicalWeapon( "Flail","Flail", Owner, a );
//        addPW.SetStats(0.0f, 0.0f, 5.0f, 4);
//        addPW.SetDamage(0.0f, 9);
//        addPW.SetSpecials("PA", "-", 0.0f, 110000.0f, 0.0f, 11.0f, 0.0f, false);
//        addPW.SetToHit(1, 1, 1);
//        addPW.SetReplacesHand(true);
//        addPW.SetRequiresLowerArm( true );
//        ISPW.add( addPW );
//
//        // lance
//        a = new AvailableCode( false, 'C', 'X', 'X', 'F', 3064, 0, 0, "LA", "", false, false, 3061, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
//        addPW = new PhysicalWeapon( "Lance","Lance", Owner, a );
//        addPW.SetStats(0.05f, 0.05f, 0.0f, 0);
//        addPW.SetDamage(0.2f, 0);
//        addPW.SetSpecials("PA", "-", 3000.0f, 0.0f, 1.0f, 0.0f, 0.0f, false);
//        addPW.SetToHit(2, 2, 2);
//        addPW.SetRequiresHand(false);
//        ISPW.add( addPW );
//
//        // mace
//        a = new AvailableCode( false, 'B', 'X', 'X', 'D', 3061, 0, 0, "LA", "", false, false, 3061, false, "", Constants.ADVANCED, Constants.ADVANCED );
//        addPW = new PhysicalWeapon( "Mace","ISMace", Owner, a );
//        addPW.SetStats(0.1f, 0.1f, 0.0f, 0);
//        addPW.SetDamage(0.25f, 0);
//        addPW.SetSpecials("PA", "-", 0.0f, 130000.0f, 1.0f, 0.0f, 0.0f, false);
//        addPW.SetToHit(1, 1, 1);
//        ISPW.add(addPW);
//
//        // vibroblade small
//        a = new AvailableCode( false, 'D', 'X', 'X', 'E', 3065, 0, 0, "DC", "", false, false, 3059, true, "DC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
//        addPW = new PhysicalWeapon( "Small Vibroblade","ISSmallVibroBlade", Owner, a );
//        addPW.SetStats(0.0f, 0.0f, 3.0f, 1);
//        addPW.SetDamage(0.0f, 7);
//        addPW.SetHeat(3);
//        addPW.SetSpecials("PA", "V", 0.0f, 150000.0f, 0.0f, 12.0f, 0.0f, false);
//        addPW.SetToHit(-1, -1, -1);
//        ISPW.add(addPW);
//
//        // vibroblade medium
//        a = new AvailableCode( false, 'D', 'X', 'X', 'E', 3065, 0, 0, "DC", "", false, false, 3059, true, "DC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
//        addPW = new PhysicalWeapon( "Medium Vibroblade","ISMediumVibroBlade", Owner, a );
//        addPW.SetStats(0.0f, 0.0f, 5.0f, 2);
//        addPW.SetDamage(0.0f, 10);
//        addPW.SetHeat(5);
//        addPW.SetSpecials("PA", "V", 0.0f, 400000.0f, 0.0f, 17.0f, 0.0f, false);
//        addPW.SetToHit(-1, -1, -1);
//        ISPW.add(addPW);
//
//        // vibroblade large
//        a = new AvailableCode( false, 'D', 'X', 'X', 'E', 3065, 0, 0, "DC", "", false, false, 3059, true, "DC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
//        addPW = new PhysicalWeapon( "Large Vibroblade","ISLargeVibroBlade", Owner, a );
//        addPW.SetStats(0.0f, 0.0f, 7.0f, 4);
//        addPW.SetDamage(0.0f, 14);
//        addPW.SetHeat(7);
//        addPW.SetSpecials("PA", "V", 0.0f, 750000.0f, 0.0f, 24.0f, 0.0f, false);
//        addPW.SetToHit(-1, -1, -1);
//        ISPW.add(addPW);
//
//        // spikes
//        a = new AvailableCode( false, 'C', 'X', 'E', 'E', 3051, 0, 0, "FC", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
//        addPW = new PhysicalWeapon( "Spikes","ISSpikes", Owner, a );
//        addPW.SetStats(0.0f, 0.0f, 0.5f, 1);
//        addPW.SetDamage(0.0f, 2);
//        addPW.SetSpecials("PA", "PB", 50.0f, 0.0f, 0.0f, 0.0f, 4.0f, false);
//        addPW.SetToHit(0, 0, 0);
//        addPW.SetRequiresLowerArm( false );
//        addPW.SetPWClass( ssw.Constants.PW_CLASS_SPIKE );
//        addPW.SetAllocations(true, true, true, true, true, false);
//        ISPW.add(addPW);
//
//        // small shield
//        a = new AvailableCode( false, 'D', 'X', 'X', 'F', 3067, 0, 0, "LA", "", false, false, 3065, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
//        addPW = new PhysicalWeapon( "Small Shield","ISSmallShield", Owner, a );
//        addPW.SetStats(0.0f, 0.0f, 2.0f, 3);
//        addPW.SetDamage(0.0f, 3);
//        addPW.SetSpecials("PA", "PB", 0.0f, 50000.0f, 0.0f, 0.0f, 50.0f, false);
//        addPW.SetToHit(-2, -2, -2);
//        addPW.SetRequiresLowerArm(false);
//        addPW.SetPWClass( ssw.Constants.PW_CLASS_SHIELD );
//        ISPW.add(addPW);
//
//        // medium shield
//        a = new AvailableCode( false, 'D', 'X', 'X', 'F', 3067, 0, 0, "LA", "", false, false, 3065, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
//        addPW = new PhysicalWeapon( "Medium Shield","ISMediumShield", Owner, a );
//        addPW.SetStats(0.0f, 0.0f, 4.0f, 5);
//        addPW.SetDamage(0.0f, 5);
//        addPW.SetSpecials("PA", "PB", 0.0f, 100000.0f, 0.0f, 0.0f, 135.0f, false);
//        addPW.SetToHit(-3, -3, -3);
//        addPW.SetRequiresLowerArm(false);
//        addPW.SetPWClass( ssw.Constants.PW_CLASS_SHIELD );
//        addPW.AddMechModifier( new MechModifier( -1, 0, 0, 0.0f, 0, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, false ));
//        ISPW.add(addPW);
//
//        // large shield
//        a = new AvailableCode( false, 'D', 'X', 'X', 'F', 3067, 0, 0, "LA", "", false, false, 3065, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
//        addPW = new PhysicalWeapon( "Large Shield","ISLargeShield", Owner, a );
//        addPW.SetStats(0.0f, 0.0f, 6.0f, 7);
//        addPW.SetDamage(0.0f, 7);
//        addPW.SetSpecials("PA", "PB", 0.0f, 300000.0f, 0.0f, 0.0f, 263.0f, false);
//        addPW.SetToHit(-4, -4, -4);
//        addPW.SetRequiresLowerArm(false);
//        addPW.SetPWClass( ssw.Constants.PW_CLASS_SHIELD );
//        MechModifier addMod = new MechModifier( -1, 0, 0, 0.0f, 0, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, false );
//        addMod.SetCanJump(false);
//        addPW.AddMechModifier( addMod );
//        ISPW.add(addPW);

        // arrow iv system
        a = new AvailableCode( false, 'E', 'E', 'F', 'E', 2600, 2830, 3044, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addAR = new Artillery( "Arrow IV Missile", "ISArrowIV", "AE", a );
        addAR.SetDamage( 20, 20, 20 );
        addAR.SetRange( 0, 8, 0, 0 );
        addAR.SetSpecials( "S, F", true, 10 );
        addAR.SetArtillery( false, true, 5, 85, true );
        addAR.SetStats( 15.0f, 15, 450000.0f, 240.0f, 0.0f );
        addAR.SetAllocations( false, true, true, true, true, true );
        ISAR.add( addAR );

        // thumper
        a = new AvailableCode( false, 'B', 'C', 'C', 'C', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addAR = new Artillery( "Thumper", "ISThumper", "AE", a );
        addAR.SetDamage( 15, 15, 15 );
        addAR.SetRange( 0, 21, 0, 0 );
        addAR.SetSpecials( "S, F", true, 6 );
        addAR.SetArtillery( false, true, 20, 87, true );
        addAR.SetStats( 15.0f, 15, 187500.0f, 43.0f, 0.0f );
        addAR.SetAllocations( false, true, true, true, true, true );
        ISAR.add( addAR );

        // sniper
        a = new AvailableCode( false, 'B', 'C', 'C', 'C', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addAR = new Artillery( "Sniper", "ISSniper", "AE", a );
        addAR.SetDamage( 20, 20, 20 );
        addAR.SetRange( 0, 18, 0, 0 );
        addAR.SetSpecials( "S, F", true, 6 );
        addAR.SetArtillery( false, true, 10, 88, true );
        addAR.SetStats( 20.0f, 20, 300000.0f, 85.0f, 0.0f );
        addAR.SetAllocations( false, true, true, true, true, true );
        ISAR.add( addAR );

        // long tom artillery cannon
        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "Long Tom Artillery Cannon", "ISLongTomArtCannon", "DB", false, a );
        addBW.SetPrintName( "Long Tom Art. Cannon" );
        addBW.SetDamage( 25, 25, 25 );
        addBW.SetHeat( 20 );
        addBW.SetRange( 4, 6, 13, 20 );
        addBW.SetSpecials( "AE, S", true, true, 5, 128, false, false );
        addBW.SetStats( 20.0f, 15, 650000.0f, 385.0f, 0.0f );
        addBW.SetBallistics( false, false, false, false );
        addBW.SetAllocations( false, true, true, false, false, true );
        addBW.SetRequiresNuclear( false );
        ISBW.add( addBW );

        // sniper artillery cannon
        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "Sniper Artillery Cannon", "ISSniperArtCannon", "DB", false, a );
        addBW.SetPrintName( "Sniper Art. Cannon" );
        addBW.SetDamage( 20, 20, 20 );
        addBW.SetHeat( 10 );
        addBW.SetRange( 2, 4, 8, 12 );
        addBW.SetSpecials( "AE, S", true, true, 10, 127, false, false );
        addBW.SetStats( 15.0f, 10, 475000.0f, 77.0f, 0.0f );
        addBW.SetBallistics( false, false, false, false );
        addBW.SetAllocations( false, true, true, false, false, true );
        addBW.SetRequiresNuclear( false );
        ISBW.add( addBW );

        // thumper artillery cannon
        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "Thumper Artillery Cannon", "ISThumperArtCannon", "DB", false, a );
        addBW.SetPrintName( "Thumper Art. Cannon" );
        addBW.SetDamage( 15, 15, 15 );
        addBW.SetHeat( 5 );
        addBW.SetRange( 3, 4, 9, 15 );
        addBW.SetSpecials( "AE, S", true, true, 20, 126, false, false );
        addBW.SetStats( 10.0f, 7, 200000.0f, 385.0f, 0.0f );
        addBW.SetBallistics( false, false, false, false );
        addBW.SetAllocations( false, true, true, false, false, true );
        addBW.SetRequiresNuclear( false );
        ISBW.add( addBW );

 /*******************************************************************************
 *      START CLAN WEAPONS
 ******************************************************************************/

        // small laser
        a = new AvailableCode( true, 'C', 'X', 'A', 'X', 2400, 0, 0, "TH", "", false, false );
        addEW = new EnergyWeapon( "Small Laser", "ISSmallLaser", "DE", true, a );
        addEW.SetPrintName( "Small Laser" );
        addEW.SetDamage( 3, 3, 3 );
        addEW.SetHeat( 1 );
        addEW.SetRange( 0, 1, 2, 3 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 0.5f, 1, 11250.0f, 9.0f, 0.0f );
        CLEW.add( addEW  );

        // medium laser
        a = new AvailableCode( true, 'C', 'X', 'D', 'X', 2400, 2820, 0, "TH", "", true, false );
        addEW = new EnergyWeapon( "Medium Laser", "ISMediumLaser", "DE", true, a );
        addEW.SetPrintName( "Medium Laser" );
        addEW.SetDamage( 5, 5, 5 );
        addEW.SetHeat( 3 );
        addEW.SetRange( 0, 3, 6, 9 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 1.0f, 1, 40000.0f, 46.0f, 0.0f );
        CLEW.add( addEW  );

        // large laser
        a = new AvailableCode( true, 'C', 'X', 'E', 'X', 2430, 2820, 0, "TH", "", true, false );
        addEW = new EnergyWeapon( "Large Laser", "ISLargeLaser", "DE", false, a );
        addEW.SetPrintName( "Large Laser" );
        addEW.SetDamage( 8, 8, 8 );
        addEW.SetHeat( 8 );
        addEW.SetRange( 0, 5, 10, 15 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 5.0f, 2, 100000.0f, 123.0f, 0.0f );
        CLEW.add( addEW  );

        // clan er micro laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3060, 0, 0, "CSJ", "", false, false );
        addEW = new EnergyWeapon( "ER Micro Laser", "CLERMicroLaser", "DE", true, a );
        addEW.SetPrintName( "ER Micro Laser" );
        addEW.SetDamage( 2, 2, 2 );
        addEW.SetHeat( 1 );
        addEW.SetRange( 0, 1, 2, 4 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 0.25f, 1, 10000.0f, 7.0f, 0.0f );
        CLEW.add( addEW  );

        // clan er small laser
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2825, 0, 0, "CJF", "", false, false );
        addEW = new EnergyWeapon( "ER Small Laser", "CLERSmallLaser", "DE", true, a );
        addEW.SetPrintName( "ER Small Laser" );
        addEW.SetDamage( 5, 5, 5 );
        addEW.SetHeat( 2 );
        addEW.SetRange( 0, 2, 4, 6 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 0.5f, 1, 11250.0f, 31.0f, 0.0f );
        CLEW.add( addEW  );

        // clan er medium laser
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2824, 0, 0, "CJF", "", false, false );
        addEW = new EnergyWeapon( "ER Medium Laser", "CLERMediumLaser", "DE", true, a );
        addEW.SetPrintName( "ER Medium Laser" );
        addEW.SetDamage( 7, 7, 7 );
        addEW.SetHeat( 5 );
        addEW.SetRange( 0, 5, 10, 15 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 1.0f, 1, 80000.0f, 108.0f, 0.0f );
        CLEW.add( addEW  );

        // clan er large laser
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2620, 0, 0, "TH", "", false, false );
        addEW = new EnergyWeapon( "ER Large Laser", "CLERLargeLaser", "DE", true, a );
        addEW.SetPrintName( "ER Large Laser" );
        addEW.SetDamage( 10, 10, 10 );
        addEW.SetHeat( 12 );
        addEW.SetRange( 0, 8, 15, 25 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 4.0f, 1, 200000.0f, 248.0f, 0.0f );
        CLEW.add( addEW  );

        // clan er small pulse laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3057, 0, 0, "CWF", "", false, false, 3053, true, "CWF", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon( "ER Small Pulse Laser", "CLERSmallLaser", "P", true, a );
        addEW.SetPrintName( "ER Small Pulse Laser" );
        addEW.SetDamage( 5, 5, 5 );
        addEW.SetHeat( 3 );
        addEW.SetRange( 0, 2, 4, 6 );
        addEW.SetSpecials( "AI", false, false, 0, 0, true );
        addEW.SetStats( 1.5f, 1, 30000.0f, 36.0f, 0.0f );
        addEW.SetToHit( -1, -1, -1 );
        CLEW.add( addEW  );

        // clan er medium pulse laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3057, 0, 0, "CWF", "", false, false, 3053, true, "CWF", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon( "ER Medium Pulse Laser", "CLERMediumLaser", "P", true, a );
        addEW.SetPrintName( "ER Medium Pulse Laser" );
        addEW.SetDamage( 7, 7, 7 );
        addEW.SetHeat( 6 );
        addEW.SetRange( 0, 5, 9, 14 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 2.0f, 2, 150000.0f, 117.0f, 0.0f );
        addEW.SetToHit( -1, -1, -1 );
        CLEW.add( addEW  );

        // clan er large pulse laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3057, 0, 0, "CWF", "", false, false, 3053, true, "CWF", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon( "ER Large Pulse Laser", "CLERLargeLaser", "P", true, a );
        addEW.SetPrintName( "ER Large Pulse Laser" );
        addEW.SetDamage( 10, 10, 10 );
        addEW.SetHeat( 13 );
        addEW.SetRange( 0, 7, 15, 23 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 6.0f, 3, 400000.0f, 272.0f, 0.0f );
        addEW.SetToHit( -1, -1, -1 );
        CLEW.add( addEW  );

        // clan micro pulse laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3060, 0, 0, "CSJ", "", false, false );
        addEW = new EnergyWeapon( "Micro Pulse Laser", "CLMicroPulseLaser", "P", true, a );
        addEW.SetPrintName( "Micro Pulse Laser" );
        addEW.SetDamage( 3, 3, 3 );
        addEW.SetHeat( 1 );
        addEW.SetRange( 0, 1, 2, 3 );
        addEW.SetSpecials( "AI", false, false, 0, 0, true );
        addEW.SetStats( 0.5f, 1, 12500.0f, 12.0f, 0.0f );
        addEW.SetToHit( -2, -2, -2 );
        CLEW.add( addEW  );

        // clan small pulse laser
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2609, 0, 0, "TH", "", false, false );
        addEW = new EnergyWeapon( "Small Pulse Laser", "CLSmallPulseLaser", "P", true, a );
        addEW.SetPrintName( "Small Pulse Laser" );
        addEW.SetDamage( 3, 3, 3 );
        addEW.SetHeat( 2 );
        addEW.SetRange( 0, 2, 4, 6 );
        addEW.SetSpecials( "AI", false, false, 0, 0, true );
        addEW.SetStats( 1.0f, 1, 16000.0f, 24.0f, 0.0f );
        addEW.SetToHit( -2, -2, -2 );
        CLEW.add( addEW  );

        // clan medium pulse laser
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2609, 0, 0, "TH", "", false, false );
        addEW = new EnergyWeapon( "Medium Pulse Laser", "CLMediumPulseLaser", "P", true, a );
        addEW.SetPrintName( "Medium Pulse Laser" );
        addEW.SetDamage( 7, 7, 7 );
        addEW.SetHeat( 4 );
        addEW.SetRange( 0, 4, 8, 12 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 2.0f, 1, 60000.0f, 111.0f, 0.0f );
        addEW.SetToHit( -2, -2, -2 );
        CLEW.add( addEW  );

        // clan large pulse laser
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2609, 0, 0, "TH", "", false, false );
        addEW = new EnergyWeapon( "Large Pulse Laser", "CLLargePulseLaser", "P", true, a );
        addEW.SetPrintName( "Large Pulse Laser" );
        addEW.SetDamage( 10, 10, 10 );
        addEW.SetHeat( 10 );
        addEW.SetRange( 0, 6, 14, 20 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 6.0f, 2, 175000.0f, 265.0f, 0.0f );
        addEW.SetToHit( -2, -2, -2 );
        CLEW.add( addEW  );

        // clan heavy small laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3059, 0, 0, "CSA", "", false, false );
        addEW = new EnergyWeapon( "Heavy Small Laser", "CLHeavySmallLaser", "DE", true, a );
        addEW.SetPrintName( "Heavy Small Laser" );
        addEW.SetDamage( 6, 6, 6 );
        addEW.SetHeat( 3 );
        addEW.SetRange( 0, 1, 2, 3 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 0.5f, 1, 20000.0f, 15.0f, 0.0f );
        addEW.SetToHit( 1, 1, 1 );
        CLEW.add( addEW  );

        // clan heavy medium laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3059, 0, 0, "CSA", "", false, false );
        addEW = new EnergyWeapon( "Heavy Medium Laser", "CLHeavyMediumLaser", "DE", true, a );
        addEW.SetPrintName( "Heavy Medium Laser" );
        addEW.SetDamage( 10, 10, 10 );
        addEW.SetHeat( 7 );
        addEW.SetRange( 0, 3, 6, 9 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 1.0f, 2, 100000.0f, 76.0f, 0.0f );
        addEW.SetToHit( 1, 1, 1 );
        CLEW.add( addEW  );

        // clan heavy large laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3059, 0, 0, "CSA", "", false, false );
        addEW = new EnergyWeapon( "Heavy Large Laser", "CLHeavyLargeLaser", "DE", true, a );
        addEW.SetPrintName( "Heavy Large Laser" );
        addEW.SetDamage( 16, 16, 16 );
        addEW.SetHeat( 18 );
        addEW.SetRange( 0, 5, 10, 15 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 4.0f, 3, 250000.0f, 244.0f, 0.0f );
        addEW.SetToHit( 1, 1, 1 );
        CLEW.add( addEW  );

        // improved clan heavy small laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3069, 0, 0, "CGS", "", false, false, 3066, true, "CGS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon( "Improved Heavy Small Laser", "CLImprovedHeavySmallLaser", "DE", true, a );
        addEW.SetPrintName( "Imp. Hvy Small Laser" );
        addEW.SetDamage( 6, 6, 6 );
        addEW.SetHeat( 3 );
        addEW.SetRange( 0, 1, 2, 3 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 0.5f, 1, 30000.0f, 19.0f, 0.0f );
        addEW.SetToHit( 0, 0, 0 );
        addEW.SetExplosive( true );
        CLEW.add( addEW  );

        // improved clan heavy medium laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3069, 0, 0, "CGS", "", false, false, 3066, true, "CGS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon( "Improved Heavy Medium Laser", "CLImprovedHeavyMediumLaser", "DE", true, a );
        addEW.SetPrintName( "Imp. Hvy Medium Laser" );
        addEW.SetDamage( 10, 10, 10 );
        addEW.SetHeat( 7 );
        addEW.SetRange( 0, 3, 6, 9 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 1.0f, 2, 150000.0f, 93.0f, 0.0f );
        addEW.SetToHit( 0, 0, 0 );
        addEW.SetExplosive( true );
        CLEW.add( addEW  );

        // improved clan heavy large laser
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3069, 0, 0, "CGS", "", false, false, 3066, true, "CGS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon( "Improved Heavy Large Laser", "CLImprovedHeavyLargeLaser", "DE", true, a );
        addEW.SetPrintName( "Imp. Hvy Large Laser" );
        addEW.SetDamage( 16, 16, 16 );
        addEW.SetHeat( 18 );
        addEW.SetRange( 0, 5, 10, 15 );
        addEW.SetSpecials( "-", false, false, 0, 0, true );
        addEW.SetStats( 4.0f, 3, 400000.0f, 296.0f, 0.0f );
        addEW.SetToHit( 0, 0, 0 );
        addEW.SetExplosive( true );
        CLEW.add( addEW  );

        // small chem laser
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3059, 0, 0, "CHH", "", false, false, 3057, true, "CHH", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon( "Small Chemical Laser", "CLSmallChemLaser", "DE", true, a );
        addEW.SetPrintName( "Small Chem Laser" );
        addEW.SetDamage( 3, 3, 3 );
        addEW.SetHeat( 1 );
        addEW.SetRange( 0, 1, 2, 3 );
        addEW.SetSpecials( "-", false, true, 60, 91, true );
        addEW.SetStats( 0.5f, 1, 10000.0f, 7.0f, 0.0f );
        CLEW.add( addEW  );

        // medium chem laser
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3059, 0, 0, "CHH", "", false, false, 3057, true, "CHH", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon( "Medium Chemical Laser", "CLMediumChemLaser", "DE", true, a );
        addEW.SetPrintName( "Medium Chem Laser" );
        addEW.SetDamage( 5, 5, 5 );
        addEW.SetHeat( 2 );
        addEW.SetRange( 0, 3, 6, 9 );
        addEW.SetSpecials( "-", false, true, 30, 92, true );
        addEW.SetStats( 1.0f, 1, 30000.0f, 37.0f, 0.0f );
        CLEW.add( addEW  );

        // large chem laser
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3059, 0, 0, "CHH", "", false, false, 3057, true, "CHH", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon( "Large Chemical Laser", "CLLargeChemLaser", "DE", false, a );
        addEW.SetPrintName( "Large Chem Laser" );
        addEW.SetDamage( 8, 8, 8 );
        addEW.SetHeat( 6 );
        addEW.SetRange( 0, 5, 10, 15 );
        addEW.SetSpecials( "-", false, true, 10, 93, true );
        addEW.SetStats( 5.0f, 2, 75000.0f, 99.0f, 0.0f );
        CLEW.add( addEW  );

        // clan plasma cannon
        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        addEW = new EnergyWeapon( "Plasma Cannon", "CLPlasmaCannon", "DE", true, a );
        addEW.SetPrintName( "Plasma Cannon" );
        addEW.SetDamage( 0, 0, 0 );
        addEW.SetHeat( 7 );
        addEW.SetRange( 0, 6, 12, 18 );
        addEW.SetSpecials( "H/AI", false, true, 10, 3, true );
        addEW.SetStats( 3.0f, 1, 320000.0f, 170.0f, 0.0f );
        CLEW.add( addEW  );

        // clan ppc
        a = new AvailableCode( true, 'D', 'X', 'E', 'X', 2460, 2825, 0, "TH", "", true, false );
        addEW = new EnergyWeapon( "PPC", "ISPPC", "DE", true, a );
        addEW.SetPrintName( "PPC" );
        addEW.SetDamage( 10, 10, 10 );
        addEW.SetHeat( 10 );
        addEW.SetRange( 3, 6, 12, 18 );
        addEW.SetSpecials( "-", true, false, 0, 0, true );
        addEW.SetStats( 7.0f, 3, 200000.0f, 176.0f, 0.0f );
        CLEW.add( addEW  );

        // clan er ppc
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2760, 0, 0, "TH", "", false, false );
        addEW = new EnergyWeapon( "ER PPC", "CLERPPC", "DE", true, a );
        addEW.SetPrintName( "ER PPC" );
        addEW.SetDamage( 15, 15, 15 );
        addEW.SetHeat( 15 );
        addEW.SetRange( 0, 7, 14, 23 );
        addEW.SetSpecials( "-", true, false, 0, 0, true );
        addEW.SetStats( 6.0f, 2, 300000.0f, 412.0f, 0.0f );
        CLEW.add( addEW  );

        // enhanced er ppc
        a = new AvailableCode( true, 'F', 'X', 'F', 'X', 2801, 2825, 0, "CWV", "", true, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEW = new EnergyWeapon("Enhanced ER PPC", "CLEnhancedERPPC", "DE", true, a);
        addEW.SetPrintName( "Enhanced ER PPC" );
        addEW.SetDamage(12, 12, 12);
        addEW.SetHeat(15);
        addEW.SetRange(0, 7, 14, 23);
        addEW.SetSpecials("-", true, false, 0, 0, true);
        addEW.SetStats( 7.0f, 3, 300000.0f, 332.0f, 0.0f );
        CLEW.add(addEW);

        // clan flamer
        a = new AvailableCode( true, 'C', 'X', 'A', 'A', 2025, 0, 0, "WA", "", false, false );
        addEW = new EnergyWeapon( "Flamer", "CLFlamer", "DE", true, a );
        addEW.SetPrintName( "Flamer" );
        addEW.SetDamage( 2, 2, 2 );
        addEW.SetHeat( 3 );
        addEW.SetRange( 0, 1, 2, 3 );
        addEW.SetSpecials( "H/AI", false, false, 0, 0, false );
        addEW.SetStats( 0.5f, 1, 7500.0f, 6.0f, 0.0f );
        CLEW.add( addEW  );

        // clan vehicle flamer
        a = new AvailableCode( true, 'B', 'X', 'A', 'A', 1950, 0, 0, "PS", "", true, false );
        addEW = new EnergyWeapon( "Vehicle Flamer", "CLVehicleFlamer", "DE", true, a );
        addEW.SetPrintName( "Vehicle Flamer" );
        addEW.SetDamage( 2, 2, 2 );
        addEW.SetHeat( 3 );
        addEW.SetRange( 0, 1, 2, 3 );
        addEW.SetSpecials( "H/AI", false, true, 20, 4, false );
        addEW.SetStats( 0.5f, 1, 7500.0f, 5.0f, 0.0f );
        addEW.SetPowerAmp( false );
        CLEW.add( addEW  );

        // clan er flamer
        a = new AvailableCode(true, 'D', 'X', 'X', 'E', 3067, 0, 0, "CJF", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addEW = new EnergyWeapon("ER Flamer", "ISERFlamer", "DE", true, a);
        addEW.SetPrintName( "ER Flamer" );
        addEW.SetDamage(2, 2, 2);
        addEW.SetHeat(4);
        addEW.SetRange(0, 3, 5, 7);
        addEW.SetSpecials("H/AI", false, false, 0, 0, false);
        addEW.SetStats(1.0f, 1, 15000.0f, 16.0f, 0.0f);
        addEW.SetRequiresFusion( true );
        CLEW.add(addEW);

        // clan heavy flamer
        a = new AvailableCode(true, 'C', 'X', 'X', 'E', 3067, 0, 0, "CJF", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addEW = new EnergyWeapon("Heavy Flamer", "ISHeavyFlamer", "DE", true, a);
        addEW.SetPrintName( "Heavy Flamer" );
        addEW.SetDamage(4, 4, 4);
        addEW.SetHeat(5);
        addEW.SetRange(0, 2, 3, 4);
        addEW.SetSpecials("H/AI", false, true, 10, 105, false);
        addEW.SetStats(1.5f, 1, 11250.0f, 15.0f, 0.0f);
        addEW.SetPowerAmp( false );
        CLEW.add(addEW);

        // clan atm-3
        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CCY", "", false, false );
        addMW = new MissileWeapon( "ATM-3", "CLATM3", "M", true, a );
        addMW.SetPrintName( "ATM-3" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 2 );
        addMW.SetRange( 4, 5, 10, 15 );
        addMW.SetSpecials( "C/S/C5/3", false, true, 20, 21, true );
        addMW.SetStats( 1.5f, 2, 50000.0f, 53.0f, 0.0f );
        addMW.SetMissile( 3, 5, false, false, false );
        CLMW.add( addMW  );

        // clan atm-6
        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CCY", "", false, false );
        addMW = new MissileWeapon( "ATM-6", "CLATM6", "M", true, a );
        addMW.SetPrintName( "ATM-6" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 4 );
        addMW.SetRange( 4, 5, 10, 15 );
        addMW.SetSpecials( "C/S/C5/6", false, true, 10, 22, true );
        addMW.SetStats( 3.5f, 3, 125000.0f, 105.0f, 0.0f );
        addMW.SetMissile( 6, 5, false, false, false );
        CLMW.add( addMW  );

        // clan atm-9
        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CCY", "", false, false );
        addMW = new MissileWeapon( "ATM-9", "CLATM9", "M", true, a );
        addMW.SetPrintName( "ATM-9" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 6 );
        addMW.SetRange( 4, 5, 10, 15 );
        addMW.SetSpecials( "C/S/C5/9", false, true, 7, 23, true );
        addMW.SetStats( 5.0f, 4, 225000.0f, 147.0f, 0.0f );
        addMW.SetMissile( 9, 5, false, false, false );
        CLMW.add( addMW  );

        // clan atm-12
        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3055, 0, 0, "CCY", "", false, false );
        addMW = new MissileWeapon( "ATM-12", "CLATM12", "M", true, a );
        addMW.SetPrintName( "ATM-12" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 8 );
        addMW.SetRange( 4, 5, 10, 15 );
        addMW.SetSpecials( "C/S/C5/12", false, true, 5, 24, true );
        addMW.SetStats( 7.0f, 5, 350000.0f, 212.0f, 0.0f );
        addMW.SetMissile( 12, 5, false, false, false );
        CLMW.add( addMW  );

        // clan lrm-5
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2400, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "LRM-5", "CLLRM5", "M", true, a );
        addMW.SetPrintName( "LRM-5" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 2 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/5", false, true, 24, 29, false );
        addMW.SetStats( 1.0f, 1, 30000.0f, 55.0f, 0.0f );
        addMW.SetMissile( 5, 5, false, true, false );
        addMW.SetArtemisType( Constants.ART4_LRM );
        CLMW.add( addMW  );

        // clan lrm-10
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2400, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "LRM-10", "CLLRM10", "M", true, a );
        addMW.SetPrintName( "LRM-10" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 4 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/10", false, true, 12, 30, false );
        addMW.SetStats( 2.5f, 1, 100000.0f, 109.0f, 0.0f );
        addMW.SetMissile( 10, 5, false, true, false );
        addMW.SetArtemisType( Constants.ART4_LRM );
        CLMW.add( addMW  );

        // clan lrm-15
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2400, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "LRM-15", "CLLRM15", "M", true, a );
        addMW.SetPrintName( "LRM-15" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 5 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/15", false, true, 8, 31, false );
        addMW.SetStats( 3.5f, 2, 175000.0f, 164.0f, 0.0f );
        addMW.SetMissile( 15, 5, false, true, false );
        addMW.SetArtemisType( Constants.ART4_LRM );
        CLMW.add( addMW  );

        // clan lrm-20
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2400, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "LRM-20", "CLLRM20", "M", true, a );
        addMW.SetPrintName( "LRM-20" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 6 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/20", false, true, 6, 32, false );
        addMW.SetStats( 5.0f, 4, 250000.0f, 220.0f, 0.0f );
        addMW.SetMissile( 20, 5, false, true, false );
        addMW.SetArtemisType( Constants.ART4_LRM );
        CLMW.add( addMW  );

        // clan srm-2
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2370, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "SRM-2", "CLSRM2", "M", true, a );
        addMW.SetPrintName( "SRM-2" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 2 );
        addMW.SetRange( 0, 3, 6, 9 );
        addMW.SetSpecials( "C/C2/2", false, true, 50, 8, false );
        addMW.SetStats( 0.5f, 1, 10000.0f, 21.0f, 0.0f );
        addMW.SetMissile( 2, 2, false, true, false );
        addMW.SetArtemisType( Constants.ART4_SRM );
        CLMW.add( addMW  );

        // clan srm-4
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2370, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "SRM-4", "CLSRM4", "M", true, a );
        addMW.SetPrintName( "SRM-4" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 3 );
        addMW.SetRange( 0, 3, 6, 9 );
        addMW.SetSpecials( "C/C2/4", false, true, 25, 9, false );
        addMW.SetStats( 1.0f, 1, 60000.0f, 39.0f, 0.0f );
        addMW.SetMissile( 4, 2, false, true, false );
        addMW.SetArtemisType( Constants.ART4_SRM );
        CLMW.add( addMW  );

        // clan srm-6
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2370, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "SRM-6", "CLSRM6", "M", true, a );
        addMW.SetPrintName( "SRM-6" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 4 );
        addMW.SetRange( 0, 3, 6, 9 );
        addMW.SetSpecials( "C/C2/6", false, true, 15, 10, false );
        addMW.SetStats( 1.5f, 1, 80000.0f, 59.0f, 0.0f );
        addMW.SetMissile( 6, 2, false, true, false );
        addMW.SetArtemisType( Constants.ART4_SRM );
        CLMW.add( addMW  );

        // clan streak srm-2
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2647, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "Streak SRM-2", "CLStreakSRM2", "M", true, a );
        addMW.SetPrintName( "Streak SRM-2" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 2 );
        addMW.SetRange( 0, 4, 8, 12 );
        addMW.SetSpecials( "C/C2/2", false, true, 50, 14, false );
        addMW.SetStats( 1.0f, 1, 15000.0f, 40.0f, 0.0f );
        addMW.SetMissile( 2, 2, true, false, false );
        CLMW.add( addMW  );

        // clan streak srm-4
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2826, 0, 0, "CSA", "", false, false );
        addMW = new MissileWeapon( "Streak SRM-4", "CLStreakSRM4", "M", true, a );
        addMW.SetPrintName( "Streak SRM-4" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 3 );
        addMW.SetRange( 0, 4, 8, 12 );
        addMW.SetSpecials( "C/C2/4", false, true, 25, 15, false );
        addMW.SetStats( 2.0f, 1, 90000.0f, 79.0f, 0.0f );
        addMW.SetMissile( 4, 2, true, false, false );
        CLMW.add( addMW  );

        // clan streak srm-6
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2826, 0, 0, "CSA", "", false, false );
        addMW = new MissileWeapon( "Streak SRM-6", "CLStreakSRM6", "M", true, a );
        addMW.SetPrintName( "Streak SRM-6" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 4 );
        addMW.SetRange( 0, 4, 8, 12 );
        addMW.SetSpecials( "C/C2/6", false, true, 15, 16, false );
        addMW.SetStats( 3.0f, 2, 120000.0f, 118.0f, 0.0f );
        addMW.SetMissile( 6, 2, true, false, false );
        CLMW.add( addMW  );

        // clan streak lrm-5
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3057, 0, 0, "CCY", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon( "Streak LRM-5", "CLStreakLRM5", "M", true, a );
        addMW.SetPrintName( "Streak LRM-5" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 2 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/5", false, true, 24, 119, false );
        addMW.SetStats( 2.0f, 1, 75000.0f, 86.0f, 0.0f );
        addMW.SetMissile( 5, 5, false, true, false );
        CLMW.add( addMW  );

        // clan streak lrm-10
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3057, 0, 0, "CCY", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon( "Streak LRM-10", "CLStreakLRM10", "M", true, a );
        addMW.SetPrintName( "Streak LRM-10" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 6 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/10", false, true, 12, 120, false );
        addMW.SetStats( 5.0f, 2, 225000.0f, 173.0f, 0.0f );
        addMW.SetMissile( 10, 5, false, true, false );
        CLMW.add( addMW  );

        // clan streak lrm-15
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3057, 0, 0, "CCY", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon( "Streak LRM-15", "CLStreakLRM15", "M", true, a );
        addMW.SetPrintName( "Streak LRM-15" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 5 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/15", false, true, 8, 121, false );
        addMW.SetStats( 7.0f, 3, 400000.0f, 259.0f, 0.0f );
        addMW.SetMissile( 15, 5, false, false, false );
        CLMW.add( addMW  );

        // clan streak lrm-20
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3057, 0, 0, "CCY", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addMW = new MissileWeapon( "Streak LRM-20", "CLStreakLRM20", "M", true, a );
        addMW.SetPrintName( "Streak LRM-20" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 6 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/20", false, true, 6, 122, false );
        addMW.SetStats( 10.0f, 5, 600000.0f, 345.0f, 0.0f );
        addMW.SetMissile( 20, 5, false, false, false );
        CLMW.add( addMW  );

        // narc missile beacon
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2587, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "Narc Missile Beacon", "CLNarcBeacon", "M", true, a );
        addMW.SetPrintName( "Narc Launcher" );
        addMW.SetDamage( 0, 0, 0 );
        addMW.SetHeat( 0 );
        addMW.SetRange( 0, 4, 8, 12 );
        addMW.SetSpecials( "E/S", false, true, 6, 39, false );
        addMW.SetStats( 2.0f, 1, 100000.0f, 30.0f, 0.0f );
        addMW.SetMissile( 1, 1, false, false, false );
        CLMW.add( addMW  );

        // clan lrm-5 OS
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2676, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "LRM-5 (OS)", "CLLRM5 (OS)", "M", true, a );
        addMW.SetPrintName( "LRM-5 (OS)" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 2 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/5/OS", false, false, 0, 0, false );
        addMW.SetStats( 1.5f, 1, 15000.0f, 11.0f, 0.0f );
        addMW.SetMissile( 5, 5, false, true, true );
        addMW.SetArtemisType( Constants.ART4_LRM );
        CLMW.add( addMW  );

        // clan lrm-10 OS
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2676, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "LRM-10 (OS)", "CLLRM10 (OS)", "M", true, a );
        addMW.SetPrintName( "LRM-10 (OS)" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 4 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/10/OS", false, false, 0, 0, false );
        addMW.SetStats( 3.0f, 1, 50000.0f, 22.0f, 0.0f );
        addMW.SetMissile( 10, 5, false, true, true );
        addMW.SetArtemisType( Constants.ART4_LRM );
        CLMW.add( addMW  );

        // clan lrm-15 OS
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2676, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "LRM-15 (OS)", "CLLRM15 (OS)", "M", true, a );
        addMW.SetPrintName( "LRM-15 (OS)" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 5 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/15/OS", false, false, 0, 0, false );
        addMW.SetStats( 4.0f, 2, 87500.0f, 33.0f, 0.0f );
        addMW.SetMissile( 15, 5, false, true, true );
        addMW.SetArtemisType( Constants.ART4_LRM );
        CLMW.add( addMW  );

        // clan lrm-20 OS
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2676, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "LRM-20 (OS)", "CLLRM20 (OS)", "M", true, a );
        addMW.SetPrintName( "LRM-20 (OS)" );
        addMW.SetDamage( 1, 1, 1 );
        addMW.SetHeat( 6 );
        addMW.SetRange( 0, 7, 14, 21 );
        addMW.SetSpecials( "C/S/C5/20/OS", false, false, 0, 0, false );
        addMW.SetStats( 5.5f, 4, 125000.0f, 44.0f, 0.0f );
        addMW.SetMissile( 20, 5, false, true, true );
        addMW.SetArtemisType( Constants.ART4_LRM );
        CLMW.add( addMW  );

        // clan srm-2 OS
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2676, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "SRM-2 (OS)", "CLSRM2 (OS)", "M", true, a );
        addMW.SetPrintName( "SRM-2 (OS)" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 2 );
        addMW.SetRange( 0, 3, 6, 9 );
        addMW.SetSpecials( "C/C2/2/OS", false, false, 0, 0, false );
        addMW.SetStats( 1.0f, 1, 5000.0f, 4.0f, 0.0f );
        addMW.SetMissile( 2, 2, false, true, true );
        addMW.SetArtemisType( Constants.ART4_SRM );
        CLMW.add( addMW  );

        // clan srm-4 OS
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2676, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "SRM-4 (OS)", "CLSRM4 (OS)", "M", true, a );
        addMW.SetPrintName( "SRM-4 (OS)" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 3 );
        addMW.SetRange( 0, 3, 6, 9 );
        addMW.SetSpecials( "C/C2/4/OS", false, false, 0, 0, false );
        addMW.SetStats( 1.5f, 1, 30000.0f, 8.0f, 0.0f );
        addMW.SetMissile( 4, 2, false, true, true );
        addMW.SetArtemisType( Constants.ART4_SRM );
        CLMW.add( addMW  );

        // clan srm-6 OS
        a = new AvailableCode( true, 'F', 'X', 'B', 'B', 2676, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "SRM-6 (OS)", "CLSRM6 (OS)", "M", true, a );
        addMW.SetPrintName( "SRM-6 (OS)" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 4 );
        addMW.SetRange( 0, 3, 6, 9 );
        addMW.SetSpecials( "C/C2/6/OS", false, false, 0, 0, false );
        addMW.SetStats( 2.0f, 1, 40000.0f, 12.0f, 0.0f );
        addMW.SetMissile( 6, 2, false, true, true );
        addMW.SetArtemisType( Constants.ART4_SRM );
        CLMW.add( addMW  );

        // clan streak srm-2 OS
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2676, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "Streak SRM-2 (OS)", "CLStreakSRM2 (OS)", "M", true, a );
        addMW.SetPrintName( "Streak SRM-2 (OS)" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 2 );
        addMW.SetRange( 0, 4, 8, 12 );
        addMW.SetSpecials( "C/C2/2/OS", false, false, 0, 0, false );
        addMW.SetStats( 1.5f, 1, 7500.0f, 8.0f, 0.0f );
        addMW.SetMissile( 2, 2, true, false, true );
        CLMW.add( addMW  );

        // clan streak srm-4 OS
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2826, 0, 0, "CSA", "", false, false );
        addMW = new MissileWeapon( "Streak SRM-4 (OS)", "CLStreakSRM4 (OS)", "M", true, a );
        addMW.SetPrintName( "Streak SRM-4 (OS)" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 3 );
        addMW.SetRange( 0, 4, 8, 12 );
        addMW.SetSpecials( "C/C2/4/OS", false, false, 0, 0, false );
        addMW.SetStats( 2.5f, 1, 45000.0f, 16.0f, 0.0f );
        addMW.SetMissile( 4, 2, true, false, true );
        CLMW.add( addMW  );

        // clan streak srm-6 OS
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2826, 0, 0, "CSA", "", false, false );
        addMW = new MissileWeapon( "Streak SRM-6 (OS)", "CLStreakSRM6 (OS)", "M", true, a );
        addMW.SetPrintName( "Streak SRM-6 (OS)" );
        addMW.SetDamage( 2, 2, 2 );
        addMW.SetHeat( 4 );
        addMW.SetRange( 0, 4, 8, 12 );
        addMW.SetSpecials( "C/C2/6/OS", false, false, 0, 0, false );
        addMW.SetStats( 3.5f, 2, 60000.0f, 24.0f, 0.0f );
        addMW.SetMissile( 6, 2, true, false, true );
        CLMW.add( addMW  );

        // narc missile beacon OS
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2676, 0, 0, "TH", "", false, false );
        addMW = new MissileWeapon( "Narc Missile Beacon (OS)", "CLNarcBeacon (OS)", "M", true, a );
        addMW.SetPrintName( "Narc Launcher (OS)" );
        addMW.SetDamage( 0, 0, 0 );
        addMW.SetHeat( 0 );
        addMW.SetRange( 0, 4, 8, 12 );
        addMW.SetSpecials( "E/S/OS", false, false, 0, 0, false );
        addMW.SetStats( 2.5f, 1, 50000.0f, 6.0f, 0.0f );
        addMW.SetMissile( 1, 1, false, false, true );
        CLMW.add( addMW  );

        // autocannon 2
        a = new AvailableCode( true, 'C', 'X', 'D', 'X', 2300, 2850, 0, "TH", "", true, false );
        addBW = new BallisticWeapon( "Autocannon/2", "ISAC2", "DB", true, a );
        addBW.SetPrintName( "Autocannon/2" );
        addBW.SetDamage( 2, 2, 2 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 4, 8, 16, 24 );
        addBW.SetSpecials( "S", true, true, 45, 44, true, true );
        addBW.SetStats( 6.0f, 1, 75000.0f, 37.0f, 0.0f );
        CLBW.add( addBW  );

        // autocannon 5
        a = new AvailableCode( true, 'C', 'X', 'D', 'X', 2250, 2850, 0, "TH", "", true, false );
        addBW = new BallisticWeapon( "Autocannon/5", "ISAC5", "DB", true, a );
        addBW.SetPrintName( "Autocannon/5" );
        addBW.SetDamage( 5, 5, 5 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 3, 6, 12, 18 );
        addBW.SetSpecials( "S", true, true, 20, 45, true, true );
        addBW.SetStats( 8.0f, 4, 125000.0f, 70.0f, 0.0f );
        CLBW.add( addBW  );

        // autocannon 10
        a = new AvailableCode( true, 'C', 'X', 'D', 'X', 2460, 2850, 0, "TH", "", true, false );
        addBW = new BallisticWeapon( "Autocannon/10", "ISAC10", "DB", true, a );
        addBW.SetPrintName( "Autocannon/10" );
        addBW.SetDamage( 10, 10, 10 );
        addBW.SetHeat( 3 );
        addBW.SetRange( 0, 5, 10, 15 );
        addBW.SetSpecials( "S", true, true, 10, 46, true, true );
        addBW.SetStats( 12.0f, 7, 200000.0f, 123.0f, 0.0f );
        CLBW.add( addBW  );

        // autocannon 20
        a = new AvailableCode( true, 'C', 'X', 'E', 'X', 2500, 2850, 0, "LC", "", true, false );
        addBW = new BallisticWeapon( "Autocannon/20", "ISAC20", "DB", true, a );
        addBW.SetPrintName( "Autocannon/20" );
        addBW.SetDamage( 20, 20, 20 );
        addBW.SetHeat( 7 );
        addBW.SetRange( 0, 3, 6, 9 );
        addBW.SetSpecials( "S", true, true, 5, 47, true, true );
        addBW.SetStats( 14.0f, 10, 300000.0f, 178.0f, 0.0f );
        addBW.SetAllocations( true, true, true, true, true, true );
        CLBW.add( addBW  );

        // lb 2-x ac
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2826, 0, 0, "CGS", "", false, false );
        addBW = new BallisticWeapon( "LB 2-X AC", "CLLBXAC2", "DB", true, a );
        addBW.SetPrintName( "LB 2-X AC" );
        addBW.SetDamage( 2, 2, 2 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 4, 10, 20, 30 );
        addBW.SetSpecials( "C/S/F", true, true, 45, 54, true, true );
        addBW.SetStats( 5.0f, 3, 150000.0f, 47.0f, 0.0f );
        addBW.SetBallistics( false, false, false, true );
        CLBW.add( addBW  );

        // lb 5-x ac
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2825, 0, 0, "CCY", "", false, false );
        addBW = new BallisticWeapon( "LB 5-X AC", "CLLBXAC5", "DB", true, a );
        addBW.SetPrintName( "LB 5-X AC" );
        addBW.SetDamage( 5, 5, 5 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 3, 8, 15, 24 );
        addBW.SetSpecials( "C/S/F", true, true, 20, 55, true, true );
        addBW.SetStats( 7.0f, 4, 250000.0f, 93.0f, 0.0f );
        addBW.SetBallistics( false, false, false, true );
        CLBW.add( addBW  );

        // lb 10-x ac
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2595, 0, 0, "TH", "", false, false );
        addBW = new BallisticWeapon( "LB 10-X AC", "CLLBXAC10", "DB", true, a );
        addBW.SetPrintName( "LB 10-X AC" );
        addBW.SetDamage( 10, 10, 10 );
        addBW.SetHeat( 2 );
        addBW.SetRange( 0, 6, 12, 18 );
        addBW.SetSpecials( "C/S/F", true, true, 10, 56, true, true );
        addBW.SetStats( 10.0f, 5, 400000.0f, 148.0f, 0.0f );
        addBW.SetBallistics( false, false, false, true );
        CLBW.add( addBW  );

        // lb 20-x ac
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2826, 0, 0, "CHH", "", false, false );
        addBW = new BallisticWeapon( "LB 20-X AC", "CLLBXAC20", "DB", true, a );
        addBW.SetPrintName( "LB 20-X AC" );
        addBW.SetDamage( 20, 20, 20 );
        addBW.SetHeat( 6 );
        addBW.SetRange( 0, 4, 8, 12 );
        addBW.SetSpecials( "C/S/F", true, true, 5, 57, true, true );
        addBW.SetStats( 12.0f, 9, 600000.0f, 237.0f, 0.0f );
        addBW.SetBallistics( false, false, false, true );
        addBW.SetAllocations( true, true, true, true, true, true );
        CLBW.add( addBW  );

        // ultra ac/2
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2827, 0, 0, "CGS", "", false, false );
        addBW = new BallisticWeapon( "Ultra AC/2", "CLUltraAC2", "DB", true, a );
        addBW.SetPrintName( "Ultra AC/2" );
        addBW.SetDamage( 2, 2, 2 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 2, 9, 18, 27 );
        addBW.SetSpecials( "R/C/R2", true, true, 45, 66, true, false );
        addBW.SetStats( 5.0f, 2, 120000.0f, 62.0f, 0.0f );
        addBW.SetBallistics( false, true, false, false );
        CLBW.add( addBW  );

        // ultra ac/5
        a = new AvailableCode( true, 'F', 'X', 'C', 'C', 2640, 0, 0, "TH", "", false, false );
        addBW = new BallisticWeapon( "Ultra AC/5", "CLUltraAC5", "DB", true, a );
        addBW.SetPrintName( "Ultra AC/5" );
        addBW.SetDamage( 5, 5, 5 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 0, 7, 14, 21 );
        addBW.SetSpecials( "R/C/R2", true, true, 20, 67, true, false );
        addBW.SetStats( 7.0f, 3, 200000.0f, 122.0f, 0.0f );
        addBW.SetBallistics( false, true, false, false );
        CLBW.add( addBW  );

        // ultra ac/10
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2825, 0, 0, "CDS", "", false, false );
        addBW = new BallisticWeapon( "Ultra AC/10", "CLUltraAC10", "DB", true, a );
        addBW.SetPrintName( "Ultra AC/10" );
        addBW.SetDamage( 10, 10, 10 );
        addBW.SetHeat( 3 );
        addBW.SetRange( 0, 6, 12, 18 );
        addBW.SetSpecials( "R/C/R2", true, true, 10, 68, true, false );
        addBW.SetStats( 10.0f, 4, 320000.0f, 210.0f, 0.0f );
        addBW.SetBallistics( false, true, false, false );
        CLBW.add( addBW  );

        // ultra ac/20
        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2825, 0, 0, "CSV", "", false, false );
        addBW = new BallisticWeapon( "Ultra AC/20", "CLUltraAC20", "DB", true, a );
        addBW.SetPrintName( "Ultra AC/20" );
        addBW.SetDamage( 20, 20, 20 );
        addBW.SetHeat( 7 );
        addBW.SetRange( 0, 4, 8, 12 );
        addBW.SetSpecials( "R/C/R2", true, true, 5, 69, true, false );
        addBW.SetStats( 12.0f, 8, 480000.0f, 335.0f, 0.0f );
        addBW.SetBallistics( false, true, false, false );
        addBW.SetAllocations( true, true, true, true, true, true );
        CLBW.add( addBW  );

        // rotary ac2
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3073, 0, 0, "CDS", "", false, false, 3069, true, "CDS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "Rotary AC/2", "CLRotaryAC2", "DB", true, a );
        addBW.SetPrintName( "Rotary AC/2" );
        addBW.SetDamage( 2, 2, 2 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 0, 8, 17, 25 );
        addBW.SetSpecials( "R/C/R6", true, true, 45, 109, true, false );
        addBW.SetStats( 8.0f, 4, 175000.0f, 161.0f, 0.0f );
        addBW.SetBallistics( false, false, true, false );
        CLBW.add( addBW  );

        // rotary ac5
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3073, 0, 0, "CDS", "", false, false, 3069, true, "CDS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "Rotary AC/5", "CLRotaryAC5", "DB", true, a );
        addBW.SetPrintName( "Rotary AC/5" );
        addBW.SetDamage( 5, 5, 5 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 0, 7, 14, 21 );
        addBW.SetSpecials( "R/C/R6", true, true, 20, 110, true, false );
        addBW.SetStats( 10.0f, 8, 275000.0f, 345.0f, 0.0f );
        addBW.SetBallistics( false, false, true, false );
        CLBW.add( addBW  );

        // protomech ac2
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3073, 0, 0, "CBS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addBW = new BallisticWeapon( "Protomech AC/2", "CLProtoAC2", "DB", true, a );
        addBW.SetPrintName( "Protomech AC/2" );
        addBW.SetDamage( 2, 2, 2 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 0, 7, 14, 20 );
        addBW.SetSpecials( "S", false, true, 40, 123, true, false );
        addBW.SetStats( 3.5f, 2, 95000.0f, 34.0f, 0.0f );
        addBW.SetBallistics( false, false, false, false );
        CLBW.add( addBW  );

        // protomech ac4
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3073, 0, 0, "CBS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addBW = new BallisticWeapon( "Protomech AC/4", "CLProtoAC4", "DB", true, a );
        addBW.SetPrintName( "Protomech AC/4" );
        addBW.SetDamage( 4, 4, 4 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 0, 5, 10, 15 );
        addBW.SetSpecials( "S", false, true, 20, 124, true, false );
        addBW.SetStats( 4.5f, 3, 133000.0f, 49.0f, 0.0f );
        addBW.SetBallistics( false, false, false, false );
        CLBW.add( addBW  );

        // protomech ac8
        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3073, 0, 0, "CBS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addBW = new BallisticWeapon( "Protomech AC/8", "CLProtoAC8", "DB", true, a );
        addBW.SetPrintName( "Protomech AC/8" );
        addBW.SetDamage( 8, 8, 8 );
        addBW.SetHeat( 2 );
        addBW.SetRange( 0, 3, 7, 10 );
        addBW.SetSpecials( "S", false, true, 10, 125, true, false );
        addBW.SetStats( 5.5f, 4, 175000.0f, 66.0f, 0.0f );
        addBW.SetBallistics( false, false, false, false );
        CLBW.add( addBW  );

        // ap gauss rifle
        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3069, 0, 0, "CJF", "", false, false );
        addBW = new BallisticWeapon( "AP Gauss Rifle", "CLAPGaussRifle", "DB", true, a );
        addBW.SetPrintName( "AP Gauss Rifle" );
        addBW.SetDamage( 3, 3, 3 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 0, 3, 6, 9 );
        addBW.SetSpecials( "X/AI", true, true, 40, 73, true, false );
        addBW.SetStats( 0.5f, 1, 10000.0f, 21.0f, 0.0f );
        addBW.SetBallistics( true, false, false, false );
        CLBW.add( addBW  );

        // gauss rifle
        a = new AvailableCode( true, 'F', 'X', 'C', 'C', 2590, 0, 0, "TH", "", false, false );
        addBW = new BallisticWeapon( "Gauss Rifle", "CLGaussRifle", "DB", true, a );
        addBW.SetPrintName( "Gauss Rifle" );
        addBW.SetDamage( 15, 15, 15 );
        addBW.SetHeat( 1 );
        addBW.SetRange( 2, 7, 15, 22 );
        addBW.SetSpecials( "X", true, true, 16, 74, true, false );
        addBW.SetStats( 12.0f, 6, 300000.0f, 320.0f, 0.0f );
        addBW.SetBallistics( true, false, false, false );
        CLBW.add( addBW  );

        // hyper assault gauss 20
        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3068, 0, 0, "CHH", "", false, false );
        addBW = new BallisticWeapon( "Hyper Assault Gauss 20", "CLHAG20", "DB", true, a );
        addBW.SetPrintName( "HAG-20" );
        addBW.SetDamage( 20, 20, 20 );
        addBW.SetHeat( 4 );
        addBW.SetRange( 2, 8, 16, 24 );
        addBW.SetSpecials( "X/C/F/C5/20", true, true, 6, 75, true, false );
        addBW.SetStats( 10.0f, 6, 400000.0f, 267.0f, 0.0f );
        addBW.SetBallistics( true, false, false, false );
        CLBW.add( addBW  );

        // hyper assault gauss 30
        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3068, 0, 0, "CHH", "", false, false );
        addBW = new BallisticWeapon( "Hyper Assault Gauss 30", "CLHAG30", "DB", true, a );
        addBW.SetPrintName( "HAG-30" );
        addBW.SetDamage( 30, 30, 30 );
        addBW.SetHeat( 6 );
        addBW.SetRange( 2, 8, 16, 24 );
        addBW.SetSpecials( "X/C/F/C5/30", true, true, 4, 76, true, false );
        addBW.SetStats( 13.0f, 8, 500000.0f, 401.0f, 0.0f );
        addBW.SetBallistics( true, false, false, false );
        CLBW.add( addBW  );

        // hyper assault gauss 40
        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3069, 0, 0, "CHH", "", false, false );
        addBW = new BallisticWeapon( "Hyper Assault Gauss 40", "CLHAG40", "DB", true, a );
        addBW.SetPrintName( "HAG-40" );
        addBW.SetDamage( 40, 40, 40 );
        addBW.SetHeat( 8 );
        addBW.SetRange( 2, 8, 16, 24 );
        addBW.SetSpecials( "X/C/F/C5/40", true, true, 3, 77, true, false );
        addBW.SetStats( 16.0f, 10, 600000.0f, 535.0f, 0.0f );
        addBW.SetBallistics( true, false, false, false );
        addBW.SetAllocations( true, true, true, true, true, true );
        CLBW.add( addBW  );

        // light machine gun
        a = new AvailableCode( true, 'F', 'X', 'X', 'B', 3060, 0, 0, "CSJ", "", false, false );
        addBW = new BallisticWeapon( "Light Machine Gun", "CLLightMG", "DB", true, a );
        addBW.SetPrintName( "LMG" );
        addBW.SetDamage( 1, 1, 1 );
        addBW.SetHeat( 0 );
        addBW.SetRange( 0, 2, 4, 6 );
        addBW.SetSpecials( "AI", false, true, 200, 80, false, false );
        addBW.SetStats( 0.25f, 1, 5000.0f, 5.0f, 0.0f );
        CLBW.add( addBW  );

        // machine gun
        a = new AvailableCode( true, 'C', 'X', 'A', 'A', 1950, 0, 0, "PS", "", false, false );
        addBW = new BallisticWeapon( "Machine Gun", "CLMG", "DB", true, a );
        addBW.SetPrintName( "MG" );
        addBW.SetDamage( 2, 2, 2 );
        addBW.SetHeat( 0 );
        addBW.SetRange( 0, 1, 2, 3 );
        addBW.SetSpecials( "AI", false, true, 200, 49, false, false );
        addBW.SetStats( 0.25f, 1, 5000.0f, 5.0f, 0.0f );
        CLBW.add( addBW  );

        // heavy machine gun
        a = new AvailableCode( true, 'C', 'X', 'X', 'B', 3059, 0, 0, "CSJ", "", false, false );
        addBW = new BallisticWeapon( "Heavy Machine Gun", "CLHeavyMG", "DB", true, a );
        addBW.SetPrintName( "HMG" );
        addBW.SetDamage( 3, 3, 3 );
        addBW.SetHeat( 0 );
        addBW.SetRange( 0, 1, 2, 0 );
        addBW.SetSpecials( "AI", false, true, 100, 81, false, false );
        addBW.SetStats( 0.5f, 1, 7500.0f, 6.0f, 0.0f );
        CLBW.add( addBW  );

        // LMGA light machine gun
        a = new AvailableCode( true, 'F', 'X', 'X', 'B', 3060, 0, 0, "CSJ", "", false, false );
        addBW = new BallisticWeapon( "Light Machine Gun", "CLLightMG", "DB", true, a );
        addBW.SetPrintName( "LMG" );
        addBW.SetDamage( 1, 1, 1 );
        addBW.SetHeat( 0 );
        addBW.SetRange( 0, 2, 4, 6 );
        addBW.SetSpecials( "AI", false, true, 200, 80, false, false );
        addBW.SetStats( 0.0f, 1, 5000.0f, 5.0f, 0.0f );
        addBW.SetLocationLinked( true );

        // LMGA 2
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        addMGA = new MGArray( addBW, 2, 0.25f, true, a );
        CLBW.add( addMGA );

        // LMGA 3
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        addMGA = new MGArray( addBW, 3, 0.25f, true, a );
        CLBW.add( addMGA );

        // LMGA 4
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        addMGA = new MGArray( addBW, 4, 0.25f, true, a );
        CLBW.add( addMGA );

        // MGA machine gun
        a = new AvailableCode( true, 'C', 'X', 'A', 'A', 1950, 0, 0, "PS", "", false, false );
        addBW = new BallisticWeapon( "Machine Gun", "CLMG", "DB", true, a );
        addBW.SetPrintName( "MG" );
        addBW.SetDamage( 2, 2, 2 );
        addBW.SetHeat( 0 );
        addBW.SetRange( 0, 1, 2, 3 );
        addBW.SetSpecials( "AI", false, true, 200, 49, false, false );
        addBW.SetStats( 0.0f, 1, 5000.0f, 5.0f, 0.0f );
        addBW.SetLocationLinked( true );

        // MGA 2
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        addMGA = new MGArray( addBW, 2, 0.25f, true, a );
        CLBW.add( addMGA );

        // MGA 3
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        addMGA = new MGArray( addBW, 3, 0.25f, true, a );
        CLBW.add( addMGA );

        // MGA 4
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        addMGA = new MGArray( addBW, 4, 0.25f, true, a );
        CLBW.add( addMGA );

        // HMGA heavy machine gun
        a = new AvailableCode( true, 'C', 'X', 'X', 'B', 3059, 0, 0, "CSJ", "", false, false );
        addBW = new BallisticWeapon( "Heavy Machine Gun", "CLHeavyMG", "DB", true, a );
        addBW.SetPrintName( "HMG" );
        addBW.SetDamage( 3, 3, 3 );
        addBW.SetHeat( 0 );
        addBW.SetRange( 0, 1, 2, 0 );
        addBW.SetSpecials( "AI", false, true, 100, 81, false, false );
        addBW.SetStats( 0.0f, 1, 7500.0f, 6.0f, 0.0f );
        addBW.SetLocationLinked( true );

        // HMGA 2
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        addMGA = new MGArray( addBW, 2, 0.5f, true, a );
        CLBW.add( addMGA );

        // HMGA 3
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        addMGA = new MGArray( addBW, 3, 0.5f, true, a );
        CLBW.add( addMGA );

        // HMGA 4
        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        addMGA = new MGArray( addBW, 4, 0.5f, true, a );
        CLBW.add( addMGA );

        // Fluid Gun
        a = new AvailableCode( true, 'B', 'B', 'B', 'B', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addBW = new BallisticWeapon("Fluid Gun", "CLFluidGun", "DB", false, a);
        addBW.SetPrintName( "Fluid Gun" );
        addBW.SetDamage( 0, 0, 0 );
        addBW.SetHeat( 0 );
        addBW.SetRange( 0, 1, 2, 3 );
        addBW.SetSpecials( "S", false, true, 20, 135, true, true );
        addBW.SetStats( 2.0f, 2, 35000.0f, 6.0f, 0.0f );
        CLBW.add(addBW);

        // arrow iv system
        a = new AvailableCode( true, 'E', 'X', 'D', 'D', 2850, 0, 0, "TH", "", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addAR = new Artillery( "Arrow IV Missile", "CLArrowIV", "AE", a );
        addAR.SetDamage( 20, 20, 20 );
        addAR.SetRange( 0, 9, 0, 0 );
        addAR.SetSpecials( "S, F", true, 10 );
        addAR.SetArtillery( false, true, 5, 86, true );
        addAR.SetStats( 12.0f, 12, 450000.0f, 240.0f, 0.0f );
        addAR.SetAllocations( false, true, true, true, true, true );
        CLAR.add( addAR );

        // thumper
        a = new AvailableCode( true, 'B', 'X', 'B', 'B', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addAR = new Artillery( "Thumper", "CLThumper", "AE", a );
        addAR.SetDamage( 15, 15, 15 );
        addAR.SetRange( 0, 21, 0, 0 );
        addAR.SetSpecials( "S, F", true, 6 );
        addAR.SetArtillery( false, true, 20, 89, true );
        addAR.SetStats( 15.0f, 15, 187500.0f, 43.0f, 0.0f );
        addAR.SetAllocations( false, true, true, true, true, true );
        CLAR.add( addAR );

        // sniper
        a = new AvailableCode( true, 'B', 'X', 'B', 'B', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addAR = new Artillery( "Sniper", "CLSniper", "AE", a );
        addAR.SetDamage( 20, 20, 20 );
        addAR.SetRange( 0, 18, 0, 0 );
        addAR.SetSpecials( "S, F", true, 6 );
        addAR.SetArtillery( false, true, 10, 90, true );
        addAR.SetStats( 20.0f, 20, 300000.0f, 85.0f, 0.0f );
        addAR.SetAllocations( false, true, true, true, true, true );
        CLAR.add( addAR );

        // long tom artillery cannon
        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "Long Tom Artillery Cannon", "CLLongTomArtCannon", "DB", false, a );
        addBW.SetPrintName( "Long Tom Art. Cannon" );
        addBW.SetDamage( 25, 25, 25 );
        addBW.SetHeat( 20 );
        addBW.SetRange( 4, 6, 13, 20 );
        addBW.SetSpecials( "AE, S", true, true, 5, 131, false, false );
        addBW.SetStats( 20.0f, 15, 650000.0f, 385.0f, 0.0f );
        addBW.SetBallistics( false, false, false, false );
        addBW.SetAllocations( false, true, true, false, false, true );
        addBW.SetRequiresNuclear( false );
        CLBW.add( addBW );

        // sniper artillery cannon
        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "Sniper Artillery Cannon", "CLSniperArtCannon", "DB", false, a );
        addBW.SetPrintName( "Sniper Art. Cannon" );
        addBW.SetDamage( 20, 20, 20 );
        addBW.SetHeat( 10 );
        addBW.SetRange( 2, 4, 8, 12 );
        addBW.SetSpecials( "AE, S", true, true, 10, 130, false, false );
        addBW.SetStats( 15.0f, 10, 475000.0f, 77.0f, 0.0f );
        addBW.SetBallistics( false, false, false, false );
        addBW.SetAllocations( false, true, true, false, false, true );
        addBW.SetRequiresNuclear( false );
        CLBW.add( addBW );

        // thumper artillery cannon
        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addBW = new BallisticWeapon( "Thumper Artillery Cannon", "CLThumperArtCannon", "DB", false, a );
        addBW.SetPrintName( "Thumper Art. Cannon" );
        addBW.SetDamage( 15, 15, 15 );
        addBW.SetHeat( 5 );
        addBW.SetRange( 3, 4, 9, 15 );
        addBW.SetSpecials( "AE, S", true, true, 20, 129, false, false );
        addBW.SetStats( 10.0f, 7, 200000.0f, 385.0f, 0.0f );
        addBW.SetBallistics( false, false, false, false );
        addBW.SetAllocations( false, true, true, false, false, true );
        addBW.SetRequiresNuclear( false );
        CLBW.add( addBW );

        addPW = new Talons( Owner );
        CLPW.add( addPW );
    }
}

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

// Last used Ammo Index: 134

package ssw.components;

import java.util.Vector;
import ssw.CommonTools;
import ssw.Constants;

public class AmmoFactory {
    private Vector ISAM = new Vector(),
                   CLAM = new Vector();

    public AmmoFactory() {
        BuildAmmunition();
    }

    public abPlaceable GetCopy( abPlaceable p ) {
        // returns a carbon-copy of the specified ammunition type
        if( ! ( p instanceof Ammunition ) ) {
            return null;
        }

        Ammunition c = (Ammunition) p;
        Ammunition result = new Ammunition( c.GetCritName(), c.GetMMName( false ), c.GetAmmoIndex(), c.IsClan(), c.GetAvailability() );
        result.SetPrintName( c.GetBasePrintName() );
        result.SetStats( c.GetTonnage(), c.GetCost(), c.GetOffensiveBV(), c.GetDefensiveBV() );
        result.SetExplosive( c.IsExplosive() );
        result.SetLotSize( c.GetLotSize() );
        result.SetToHit( c.GetToHitShort(), c.GetToHitMedium(), c.GetToHitLong() );
        result.SetDamage( c.GetDamage(), c.ClusterSize(), c.ClusterGrouping() );
        result.SetRange( c.GetMinRange(), c.GetShortRange(), c.GetMediumRange(), c.GetLongRange() );
        return result;
    }

    public Object[] GetAmmo( int key, Mech m ) {
        // returns an array containing all the ammunition for the specified 
        // weapon key.  The key is the weapon's lookup name
        Vector v = new Vector(),
               test;
        if( m.IsClan() ) {
            test = CLAM;
        } else {
            test = ISAM;
        }

        // find the ammunition
        for( int i = 0; i < test.size(); i++ ) {
            if( key == ((Ammunition) test.get( i )).GetAmmoIndex() ) {
                Ammunition a = (Ammunition) test.get( i );
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

        if( m.IsClan() ) {
            test = CLAM;
        } else {
            test = ISAM;
        }

        // find the ammunition
        for( int i = 0; i < test.size(); i++ ) {
            for( int j = 0; j < key.length; j++ ) {
                o = test.get( i );
                if( key[j] == ((Ammunition) o).GetAmmoIndex() ) {
                    Ammunition a = (Ammunition) test.get( i );
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

    public Ammunition GetAmmoByName( String name, boolean Clan ) {
        // returns an ammunition based on the given name
        Vector Test = new Vector();
        if( Clan ) {
            Test = CLAM;
        } else {
            Test = ISAM;
        }

        for( int i = 0; i < Test.size(); i++ ) {
            if( ((abPlaceable) Test.get( i )).GetCritName().equals( name ) ) {
                return (Ammunition) GetCopy( (abPlaceable) Test.get( i ) );
            }
        }
        return null;
    }

    private void BuildAmmunition() {
        // this is a very large routine that builds each ammo and places it
        // into the appropriate vector
        AvailableCode a;
        Ammunition add;

/*******************************************************************************
 *      START INNER SPHERE AMMUNITION
 ******************************************************************************/

	a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3068, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ Plasma Rifle", "ISPlasmaRifle Ammo", 1, false, a );
        add.SetStats( 1.0f, 10000.0f, 26.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( false );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'A', 'A', 'A', 'B', 1950, 0, 0, "PS", "", false, false );
        add = new Ammunition( "@ Vehicle Flamer", "ISVehicleFlamer Ammo", 2, false, a );
        add.SetStats( 1.0f, 1000.0f, 1.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode(false, 'C', 'X', 'X', 'E', 3068, 0, 0, "LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Heavy Flamer", "ISHeavyFlamer Ammo", 104, false, a );
        add.SetStats( 1.0f, 2000.0f, 2.0f, 0.0f );
        add.SetDamage( 4, 1, 1 );
        add.SetRange( 0, 2, 3, 4 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'B', 'B', 'B', 2370, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-2", "ISSRM2 Ammo", 5, false, a );
        add.SetStats( 1.0f, 27000.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'B', 'B', 'B', 2370, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-4", "ISSRM4 Ammo", 6, false, a );
        add.SetStats( 1.0f, 27000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'B', 'B', 'B', 2370, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-6", "ISSRM6 Ammo", 7, false, a );
        add.SetStats( 1.0f, 27000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ SRM-2 (Artemis Capable)", "ISSRM2 Ammo", 5, false, a );
        add.SetPrintName( "@ SRM-2 (Artemis)" );
        add.SetStats( 1.0f, 54000.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ SRM-4 (Artemis Capable)", "ISSRM4 Ammo", 6, false, a );
        add.SetPrintName( "@ SRM-4 (Artemis)" );
        add.SetStats( 1.0f, 54000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ SRM-6 (Artemis Capable)", "ISSRM6 Ammo", 7, false, a );
        add.SetPrintName( "@ SRM-6 (Artemis)" );
        add.SetStats( 1.0f, 54000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ SRM-2 (Fragmentation)", "ISSRM2 Ammo", 5, false, a );
        add.SetPrintName( "@ SRM-2 (Frag)" );
        add.SetStats( 1.0f, 54000.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ SRM-4 (Fragmentation)", "ISSRM4 Ammo", 6, false, a );
        add.SetPrintName( "@ SRM-4 (Frag)" );
        add.SetStats( 1.0f, 54000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ SRM-6 (Fragmentation)", "ISSRM6 Ammo", 7, false, a );
        add.SetPrintName( "@ SRM-6 (Frag)" );
        add.SetStats( 1.0f, 54000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'C', 'C', 'C', 2400, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ SRM-2 (Harpoon)", "ISSRM2 Ammo", 5, false, a );
        add.SetStats( 1.0f, 5400.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'C', 'C', 'C', 2400, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ SRM-4 (Harpoon)", "ISSRM4 Ammo", 6, false, a );
        add.SetStats( 1.0f, 5400.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'C', 'C', 'C', 2400, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ SRM-6 (Harpoon)", "ISSRM6 Ammo", 7, false, a );
        add.SetStats( 1.0f, 5400.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'D', 'D', 'D', 2380, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ SRM-2 (Inferno)", "ISSRM2 Ammo", 5, false, a );
        add.SetStats( 1.0f, 13500.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'D', 'D', 'D', 2380, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ SRM-4 (Inferno)", "ISSRM4 Ammo", 6, false, a );
        add.SetStats( 1.0f, 13500.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'D', 'D', 'D', 2380, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ SRM-6 (Inferno)", "ISSRM6 Ammo", 7, false, a );
        add.SetStats( 1.0f, 13500.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 2375, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-2 (Tear Gas)", "ISSRM2 Ammo", 5, false, a );
        add.SetStats( 1.0f, 40500.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 2375, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-4 (Tear Gas)", "ISSRM4 Ammo", 6, false, a );
        add.SetStats( 1.0f, 40500.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 2375, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-6 (Tear Gas)", "ISSRM6 Ammo", 7, false, a );
        add.SetStats( 1.0f, 40500.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ SRM-2 (Narc Capable)", "ISSRM2 Ammo", 5, false, a );
        add.SetPrintName( "@ SRM-2 (Narc)" );
        add.SetStats( 1.0f, 54000.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ SRM-4 (Narc Capable)", "ISSRM4 Ammo", 6, false, a );
        add.SetPrintName( "@ SRM-4 (Narc)" );
        add.SetStats( 1.0f, 54000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ SRM-6 (Narc Capable)", "ISSRM6 Ammo", 7, false, a );
        add.SetPrintName( "@ SRM-6 (Narc)" );
        add.SetStats( 1.0f, 54000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-2 (Torpedo)", "ISSRM2 Ammo", 5, false, a );
        add.SetStats( 1.0f, 27000.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-4 (Torpedo)", "ISSRM4 Ammo", 6, false, a );
        add.SetStats( 1.0f, 27000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-6 (Torpedo)", "ISSRM6 Ammo", 7, false, a );
        add.SetStats( 1.0f, 27000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2647, 2845, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ Streak SRM-2", "ISStreakSRM2 Ammo", 11, false, a );
        add.SetStats( 1.0f, 54000.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false );
        add = new Ammunition( "@ Streak SRM-4", "ISStreakSRM4 Ammo", 12, false, a );
        add.SetStats( 1.0f, 54000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false );
        add = new Ammunition( "@ Streak SRM-6", "ISStreakSRM6 Ammo", 13, false, a );
        add.SetStats( 1.0f, 54000.0f, 11.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false );
        add = new Ammunition( "@ MRM-10", "ISMRM10 Ammo", 17, false, a );
        add.SetStats( 1.0f, 5000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 0, 3, 8, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        add.SetToHit( 1, 1, 1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false );
        add = new Ammunition( "@ MRM-20", "ISMRM20 Ammo", 18, false, a );
        add.SetStats( 1.0f, 5000.0f, 14.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 0, 3, 8, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        add.SetToHit( 1, 1, 1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false );
        add = new Ammunition( "@ MRM-30", "ISMRM30 Ammo", 19, false, a );
        add.SetStats( 1.0f, 5000.0f, 21.0f, 0.0f );
        add.SetDamage( 1, 30, 5 );
        add.SetRange( 0, 3, 8, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        add.SetToHit( 1, 1, 1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'X', 'X', 'E', 3058, 0, 0, "DC", "", false, false );
        add = new Ammunition( "@ MRM-40", "ISMRM40 Ammo", 20, false, a );
        add.SetStats( 1.0f, 5000.0f, 28.0f, 0.0f );
        add.SetDamage( 1, 40, 5 );
        add.SetRange( 0, 3, 8, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        add.SetToHit( 1, 1, 1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-5", "ISLRM5 Ammo", 25, false, a );
        add.SetStats( 1.0f, 30000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-10", "ISLRM10 Ammo", 26, false, a );
        add.SetStats( 1.0f, 30000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-15", "ISLRM15 Ammo", 27, false, a );
        add.SetStats( 1.0f, 30000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-20", "ISLRM20 Ammo", 28, false, a );
        add.SetStats( 1.0f, 30000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ LRM-5 (Artemis Capable)", "ISLRM5 Ammo", 25, false, a );
        add.SetPrintName( "@ LRM-5 (Artemis)" );
        add.SetStats( 1.0f, 60000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ LRM-10 (Artemis Capable)", "ISLRM10 Ammo", 26, false, a );
        add.SetPrintName( "@ LRM-10 (Artemis)" );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ LRM-15 (Artemis Capable)", "ISLRM15 Ammo", 27, false, a );
        add.SetPrintName( "@ LRM-15 (Artemis)" );
        add.SetStats( 1.0f, 60000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ LRM-20 (Artemis Capable)", "ISLRM20 Ammo", 28, false, a );
        add.SetPrintName( "@ LRM-20 (Artemis)" );
        add.SetStats( 1.0f, 60000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "FS", "FW", true, true );
        add = new Ammunition( "@ LRM-5 (Flare)", "ISLRM5 Ammo", 25, false, a );
        add.SetStats( 1.0f, 30000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "FS", "FW", true, true );
        add = new Ammunition( "@ LRM-10 (Flare)", "ISLRM10 Ammo", 26, false, a );
        add.SetStats( 1.0f, 30000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "FS", "FW", true, true );
        add = new Ammunition( "@ LRM-15 (Flare)", "ISLRM15 Ammo", 27, false, a );
        add.SetStats( 1.0f, 30000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "FS", "FW", true, true );
        add = new Ammunition( "@ LRM-20 (Flare)", "ISLRM20 Ammo", 28, false, a );
        add.SetStats( 1.0f, 30000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ LRM-5 (Fragmentation)", "ISLRM5 Ammo", 25, false, a );
        add.SetPrintName( "@ LRM-5 (Frag)" );
        add.SetStats( 1.0f, 60000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ LRM-10 (Fragmentation)", "ISLRM10 Ammo", 26, false, a );
        add.SetPrintName( "@ LRM-10 (Frag)" );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ LRM-15 (Fragmentation)", "ISLRM15 Ammo", 27, false, a );
        add.SetPrintName( "@ LRM-15 (Frag)" );
        add.SetStats( 1.0f, 60000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ LRM-20 (Fragmentation)", "ISLRM20 Ammo", 28, false, a );
        add.SetPrintName( "@ LRM-20 (Frag)" );
        add.SetStats( 1.0f, 60000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-5 (Incendiary)", "ISLRM5 Ammo", 25, false, a );
        add.SetPrintName( "@ LRM-5 (Incend.)" );
        add.SetStats( 1.0f, 45000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-10 (Incendiary)", "ISLRM10 Ammo", 26, false, a );
        add.SetPrintName( "@ LRM-10 (Incend.)" );
        add.SetStats( 1.0f, 45000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-15 (Incendiary)", "ISLRM15 Ammo", 27, false, a );
        add.SetPrintName( "@ LRM-15 (Incend.)" );
        add.SetStats( 1.0f, 45000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-20 (Incendiary)", "ISLRM20 Ammo", 28, false, a );
        add.SetPrintName( "@ LRM-20 (Incend.)" );
        add.SetStats( 1.0f, 45000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-5 (Semi-Guided)", "ISLRM5 Ammo", 25, false, a );
        add.SetPrintName( "@ LRM-5 (Semi-G)" );
        add.SetStats( 1.0f, 90000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-10 (Semi-Guided)", "ISLRM10 Ammo", 26, false, a );
        add.SetPrintName( "@ LRM-10 (Semi-G)" );
        add.SetStats( 1.0f, 90000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-15 (Semi-Guided)", "ISLRM15 Ammo", 27, false, a );
        add.SetPrintName( "@ LRM-15 (Semi-G)" );
        add.SetStats( 1.0f, 90000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-20 (Semi-Guided)", "ISLRM20 Ammo", 28, false, a );
        add.SetPrintName( "@ LRM-20 (Semi-G)" );
        add.SetStats( 1.0f, 90000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ LRM-5 (Swarm)", "ISLRM5 Ammo", 25, false, a );
        add.SetStats( 1.0f, 60000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ LRM-10 (Swarm)", "ISLRM10 Ammo", 26, false, a );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ LRM-15 (Swarm)", "ISLRM15 Ammo", 27, false, a );
        add.SetStats( 1.0f, 60000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ LRM-20 (Swarm)", "ISLRM20 Ammo", 28, false, a );
        add.SetStats( 1.0f, 60000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-5 (Swarm-I)", "ISLRM5 Ammo", 25, false, a );
        add.SetStats( 1.0f, 90000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-10 (Swarm-I)", "ISLRM10 Ammo", 26, false, a );
        add.SetStats( 1.0f, 90000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-15 (Swarm-I)", "ISLRM15 Ammo", 27, false, a );
        add.SetStats( 1.0f, 90000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ LRM-20 (Swarm-I)", "ISLRM20 Ammo", 28, false, a );
        add.SetStats( 1.0f, 90000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 2840, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ LRM-5 (Thunder)", "ISLRM5 Ammo", 25, false, a );
        add.SetStats( 1.0f, 60000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 2840, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ LRM-10 (Thunder)", "ISLRM10 Ammo", 26, false, a );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 2840, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ LRM-15 (Thunder)", "ISLRM15 Ammo", 27, false, a );
        add.SetStats( 1.0f, 60000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 2840, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ LRM-20 (Thunder)", "ISLRM20 Ammo", 28, false, a );
        add.SetStats( 1.0f, 60000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-5 (Thunder-Augmented)", "ISLRM5 Ammo", 25, false, a );
        add.SetPrintName( "@ LRM-5 (Th-Aug)" );
        add.SetStats( 1.0f, 120000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-10 (Thunder-Augmented)", "ISLRM10 Ammo", 26, false, a );
        add.SetPrintName( "@ LRM-10 (Th-Aug)" );
        add.SetStats( 1.0f, 120000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-15 (Thunder-Augmented)", "ISLRM15 Ammo", 27, false, a );
        add.SetPrintName( "@ LRM-15 (Th-Aug)" );
        add.SetStats( 1.0f, 120000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-20 (Thunder-Augmented)", "ISLRM20 Ammo", 28, false, a );
        add.SetPrintName( "@ LRM-20 (Th-Aug)" );
        add.SetStats( 1.0f, 120000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-5 (Thunder-Inferno)", "ISLRM5 Ammo", 25, false, a );
        add.SetPrintName( "@ LRM-5 (Th-Inf)" );
        add.SetStats( 1.0f, 30000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-10 (Thunder-Inferno)", "ISLRM10 Ammo", 26, false, a );
        add.SetPrintName( "@ LRM-10 (Th-Inf)" );
        add.SetStats( 1.0f, 30000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-15 (Thunder-Inferno)", "ISLRM15 Ammo", 27, false, a );
        add.SetPrintName( "@ LRM-15 (Th-Inf)" );
        add.SetStats( 1.0f, 30000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-20 (Thunder-Inferno)", "ISLRM20 Ammo", 28, false, a );
        add.SetPrintName( "@ LRM-20 (Th-Inf)" );
        add.SetStats( 1.0f, 30000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-5 (Thunder-Vibrabomb)", "ISLRM5 Ammo", 25, false, a );
        add.SetPrintName( "@ LRM-5 (Th-Vib)" );
        add.SetStats( 1.0f, 75000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-10 (Thunder-Vibrabomb)", "ISLRM10 Ammo", 26, false, a );
        add.SetPrintName( "@ LRM-10 (Th-Vib)" );
        add.SetStats( 1.0f, 75000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-15 (Thunder-Vibrabomb)", "ISLRM15 Ammo", 27, false, a );
        add.SetPrintName( "@ LRM-15 (Th-Vib)" );
        add.SetStats( 1.0f, 75000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-20 (Thunder-Vibrabomb)", "ISLRM20 Ammo", 28, false, a );
        add.SetPrintName( "@ LRM-20 (Th-Vib)" );
        add.SetStats( 1.0f, 75000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-5 (Thunder-Active)", "ISLRM5 Ammo", 25, false, a );
        add.SetPrintName( "@ LRM-5 (Th-Act)" );
        add.SetStats( 1.0f, 90000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-10 (Thunder-Active)", "ISLRM10 Ammo", 26, false, a );
        add.SetPrintName( "@ LRM-10 (Th-Act)" );
        add.SetStats( 1.0f, 90000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-15 (Thunder-Active)", "ISLRM15 Ammo", 27, false, a );
        add.SetPrintName( "@ LRM-15 (Th-Act)" );
        add.SetStats( 1.0f, 90000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ LRM-20 (Thunder-Active)", "ISLRM20 Ammo", 28, false, a );
        add.SetPrintName( "@ LRM-20 (Th-Act)" );
        add.SetStats( 1.0f, 90000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", false, false );
        add = new Ammunition( "@ LRM-5 (Narc Capable)", "ISLRM5 Ammo", 25, false, a );
        add.SetPrintName( "@ LRM-5 (Narc)" );
        add.SetStats( 1.0f, 60000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", false, false );
        add = new Ammunition( "@ LRM-10 (Narc Capable)", "ISLRM10 Ammo", 26, false, a );
        add.SetPrintName( "@ LRM-10 (Narc)" );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", false, false );
        add = new Ammunition( "@ LRM-15 (Narc Capable)", "ISLRM15 Ammo", 27, false, a );
        add.SetPrintName( "@ LRM-15 (Narc)" );
        add.SetStats( 1.0f, 60000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", false, false );
        add = new Ammunition( "@ LRM-20 (Narc Capable)", "ISLRM20 Ammo", 28, false, a );
        add.SetPrintName( "@ LRM-20 (Narc)" );
        add.SetStats( 1.0f, 60000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-5 (Torpedo)", "ISLRM5 Ammo", 25, false, a );
        add.SetStats( 1.0f, 30000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-10 (Torpedo)", "ISLRM10 Ammo", 26, false, a );
        add.SetStats( 1.0f, 30000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-15 (Torpedo)", "ISLRM15 Ammo", 27, false, a );
        add.SetStats( 1.0f, 30000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-20 (Torpedo)", "ISLRM20 Ammo", 28, false, a );
        add.SetStats( 1.0f, 30000.0f, 23.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ NLRM-5", "ISNLRM5 Ammo", 111, false, a );
        add.SetStats( 1.0f, 31000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ NLRM-10", "ISNLRM10 Ammo", 112, false, a );
        add.SetStats( 1.0f, 31000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ NLRM-15", "ISNLRM15 Ammo", 113, false, a );
        add.SetStats( 1.0f, 31000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ NLRM-20", "ISNLRM20 Ammo", 114, false, a );
        add.SetStats( 1.0f, 31000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ NLRM-5 (Artemis Capable)", "ISNLRM5 Ammo", 111, false, a );
        add.SetPrintName( "@ NLRM-5 (Artemis)" );
        add.SetStats( 1.0f, 62000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ NLRM-10 (Artemis Capable)", "ISNLRM10 Ammo", 112, false, a );
        add.SetPrintName( "@ NLRM-10 (Artemis)" );
        add.SetStats( 1.0f, 62000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ NLRM-15 (Artemis Capable)", "ISNLRM15 Ammo", 113, false, a );
        add.SetPrintName( "@ NLRM-15 (Artemis)" );
        add.SetStats( 1.0f, 62000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ NLRM-20 (Artemis Capable)", "ISNLRM20 Ammo", 114, false, a );
        add.SetPrintName( "@ NLRM-20 (Artemis)" );
        add.SetStats( 1.0f, 62000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "FS", "FW", true, true );
        add = new Ammunition( "@ NLRM-5 (Flare)", "ISNLRM5 Ammo", 111, false, a );
        add.SetStats( 1.0f, 31000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "FS", "FW", true, true );
        add = new Ammunition( "@ NLRM-10 (Flare)", "ISNLRM10 Ammo", 112, false, a );
        add.SetStats( 1.0f, 31000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "FS", "FW", true, true );
        add = new Ammunition( "@ NLRM-15 (Flare)", "ISNLRM15 Ammo", 113, false, a );
        add.SetStats( 1.0f, 31000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "FS", "FW", true, true );
        add = new Ammunition( "@ NLRM-20 (Flare)", "ISNLRM20 Ammo", 114, false, a );
        add.SetStats( 1.0f, 31000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ NLRM-5 (Fragmentation)", "ISNLRM5 Ammo", 111, false, a );
        add.SetPrintName( "@ NLRM-5 (Frag)" );
        add.SetStats( 1.0f, 62000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ NLRM-10 (Fragmentation)", "ISNLRM10 Ammo", 112, false, a );
        add.SetPrintName( "@ NLRM-10 (Frag)" );
        add.SetStats( 1.0f, 62000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ NLRM-15 (Fragmentation)", "ISNLRM15 Ammo", 113, false, a );
        add.SetPrintName( "@ NLRM-15 (Frag)" );
        add.SetStats( 1.0f, 62000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ NLRM-20 (Fragmentation)", "ISNLRM20 Ammo", 114, false, a );
        add.SetPrintName( "@ NLRM-20 (Frag)" );
        add.SetStats( 1.0f, 62000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-5 (Incendiary)", "ISNLRM5 Ammo", 111, false, a );
        add.SetPrintName( "@ NLRM-5 (Incend.)" );
        add.SetStats( 1.0f, 46500.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-10 (Incendiary)", "ISNLRM10 Ammo", 112, false, a );
        add.SetPrintName( "@ NLRM-10 (Incend.)" );
        add.SetStats( 1.0f, 46500.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-15 (Incendiary)", "ISNLRM15 Ammo", 113, false, a );
        add.SetPrintName( "@ NLRM-15 (Incend.)" );
        add.SetStats( 1.0f, 46500.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-20 (Incendiary)", "ISNLRM20 Ammo", 114, false, a );
        add.SetPrintName( "@ NLRM-20 (Incend.)" );
        add.SetStats( 1.0f, 46500.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-5 (Semi-Guided)", "ISNLRM5 Ammo", 111, false, a );
        add.SetPrintName( "@ NLRM-5 (Semi-G)" );
        add.SetStats( 1.0f, 93000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-10 (Semi-Guided)", "ISNLRM10 Ammo", 112, false, a );
        add.SetPrintName( "@ NLRM-10 (Semi-G)" );
        add.SetStats( 1.0f, 93000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-15 (Semi-Guided)", "ISNLRM15 Ammo", 113, false, a );
        add.SetPrintName( "@ NLRM-15 (Semi-G)" );
        add.SetStats( 1.0f, 93000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-20 (Semi-Guided)", "ISNLRM20 Ammo", 114, false, a );
        add.SetPrintName( "@ NLRM-20 (Semi-G)" );
        add.SetStats( 1.0f, 93000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ NLRM-5 (Swarm)", "ISNLRM5 Ammo", 111, false, a );
        add.SetStats( 1.0f, 62000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ NLRM-10 (Swarm)", "ISNLRM10 Ammo", 112, false, a );
        add.SetStats( 1.0f, 62000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ NLRM-15 (Swarm)", "ISNLRM15 Ammo", 113, false, a );
        add.SetStats( 1.0f, 62000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ NLRM-20 (Swarm)", "ISNLRM20 Ammo", 114, false, a );
        add.SetStats( 1.0f, 62000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-5 (Swarm-I)", "ISNLRM5 Ammo", 111, false, a );
        add.SetStats( 1.0f, 93000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-10 (Swarm-I)", "ISNLRM10 Ammo", 112, false, a );
        add.SetStats( 1.0f, 93000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-15 (Swarm-I)", "ISNLRM15 Ammo", 113, false, a );
        add.SetStats( 1.0f, 93000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ NLRM-20 (Swarm-I)", "ISNLRM20 Ammo", 114, false, a );
        add.SetStats( 1.0f, 93000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 2840, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ NLRM-5 (Thunder)", "ISNLRM5 Ammo", 111, false, a );
        add.SetStats( 1.0f, 62000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 2840, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ NLRM-10 (Thunder)", "ISNLRM10 Ammo", 112, false, a );
        add.SetStats( 1.0f, 62000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 2840, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ NLRM-15 (Thunder)", "ISNLRM15 Ammo", 113, false, a );
        add.SetStats( 1.0f, 62000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 2840, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ NLRM-20 (Thunder)", "ISNLRM20 Ammo", 114, false, a );
        add.SetStats( 1.0f, 62000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-5 (Thunder-Augmented)", "ISNLRM5 Ammo", 111, false, a );
        add.SetPrintName( "@ NLRM-5 (Th-Aug)" );
        add.SetStats( 1.0f, 93000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-10 (Thunder-Augmented)", "ISNLRM10 Ammo", 112, false, a );
        add.SetPrintName( "@ NLRM-10 (Th-Aug)" );
        add.SetStats( 1.0f, 93000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-15 (Thunder-Augmented)", "ISNLRM15 Ammo", 113, false, a );
        add.SetPrintName( "@ NLRM-15 (Th-Aug)" );
        add.SetStats( 1.0f, 93000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-20 (Thunder-Augmented)", "ISNLRM20 Ammo", 114, false, a );
        add.SetPrintName( "@ NLRM-20 (Th-Aug)" );
        add.SetStats( 1.0f, 93000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-5 (Thunder-Inferno)", "ISNLRM5 Ammo", 111, false, a );
        add.SetPrintName( "@ NLRM-5 (Th-Inf)" );
        add.SetStats( 1.0f, 31000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-10 (Thunder-Inferno)", "ISNLRM10 Ammo", 112, false, a );
        add.SetPrintName( "@ NLRM-10 (Th-Inf)" );
        add.SetStats( 1.0f, 31000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-15 (Thunder-Inferno)", "ISNLRM15 Ammo", 113, false, a );
        add.SetPrintName( "@ NLRM-15 (Th-Inf)" );
        add.SetStats( 1.0f, 31000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-20 (Thunder-Inferno)", "ISNLRM20 Ammo", 114, false, a );
        add.SetPrintName( "@ NLRM-20 (Th-Inf)" );
        add.SetStats( 1.0f, 31000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-5 (Thunder-Vibrabomb)", "ISNLRM5 Ammo", 111, false, a );
        add.SetPrintName( "@ NLRM-5 (Th-Vib)" );
        add.SetStats( 1.0f, 77500.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-10 (Thunder-Vibrabomb)", "ISNLRM10 Ammo", 112, false, a );
        add.SetPrintName( "@ NLRM-10 (Th-Vib)" );
        add.SetStats( 1.0f, 77500.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-15 (Thunder-Vibrabomb)", "ISNLRM15 Ammo", 113, false, a );
        add.SetPrintName( "@ NLRM-15 (Th-Vib)" );
        add.SetStats( 1.0f, 77500.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-20 (Thunder-Vibrabomb)", "ISNLRM20 Ammo", 114, false, a );
        add.SetPrintName( "@ NLRM-20 (Th-Vib)" );
        add.SetStats( 1.0f, 77500.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-5 (Thunder-Active)", "ISNLRM5 Ammo", 111, false, a );
        add.SetPrintName( "@ NLRM-5 (Th-Act)" );
        add.SetStats( 1.0f, 93000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-10 (Thunder-Active)", "ISNLRM10 Ammo", 112, false, a );
        add.SetPrintName( "@ NLRM-10 (Th-Act)" );
        add.SetStats( 1.0f, 93000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-15 (Thunder-Active)", "ISNLRM15 Ammo", 113, false, a );
        add.SetPrintName( "@ NLRM-15 (Th-Act)" );
        add.SetStats( 1.0f, 93000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ NLRM-20 (Thunder-Active)", "ISNLRM20 Ammo", 114, false, a );
        add.SetPrintName( "@ NLRM-20 (Th-Act)" );
        add.SetStats( 1.0f, 93000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", false, false );
        add = new Ammunition( "@ NLRM-5 (Narc Capable)", "ISNLRM5 Ammo", 111, false, a );
        add.SetPrintName( "@ NLRM-5 (Narc)" );
        add.SetStats( 1.0f, 62000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", false, false );
        add = new Ammunition( "@ NLRM-10 (Narc Capable)", "ISNLRM10 Ammo", 112, false, a );
        add.SetPrintName( "@ NLRM-10 (Narc)" );
        add.SetStats( 1.0f, 62000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", false, false );
        add = new Ammunition( "@ NLRM-15 (Narc Capable)", "ISNLRM15 Ammo", 113, false, a );
        add.SetPrintName( "@ NLRM-15 (Narc)" );
        add.SetStats( 1.0f, 62000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", false, false );
        add = new Ammunition( "@ NLRM-20 (Narc Capable)", "ISNLRM20 Ammo", 114, false, a );
        add.SetPrintName( "@ NLRM-20 (Narc)" );
        add.SetStats( 1.0f, 62000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ NLRM-5 (Torpedo)", "ISNLRM5 Ammo", 111, false, a );
        add.SetStats( 1.0f, 31000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ NLRM-10 (Torpedo)", "ISNLRM10 Ammo", 112, false, a );
        add.SetStats( 1.0f, 31000.0f, 13.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ NLRM-15 (Torpedo)", "ISNLRM15 Ammo", 113, false, a );
        add.SetStats( 1.0f, 31000.0f, 20.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ NLRM-20 (Torpedo)", "ISNLRM20 Ammo", 114, false, a );
        add.SetStats( 1.0f, 31000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );


        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3054, 0, 0, "FC", "", false, false, 3052, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ ELRM-5", "ISELRM5 Ammo", 115, false, a );
        add.SetStats( 1.0f, 35000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 10, 12, 22, 38 );
        add.SetExplosive( true );
        add.SetLotSize( 18 );
        ISAM.add( add );

        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3054, 0, 0, "FC", "", false, false, 3052, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ ELRM-10", "ISELRM10 Ammo", 116, false, a );
        add.SetStats( 1.0f, 35000.0f, 17.0f, 0.0f );
        add.SetDamage( 1, 10, 5 );
        add.SetRange( 10, 12, 22, 38 );
        add.SetExplosive( true );
        add.SetLotSize( 9 );
        ISAM.add( add );

        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3054, 0, 0, "FC", "", false, false, 3052, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ ELRM-15", "ISELRM15 Ammo", 117, false, a );
        add.SetStats( 1.0f, 35000.0f, 25.0f, 0.0f );
        add.SetDamage( 1, 15, 5 );
        add.SetRange( 10, 12, 22, 38 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3054, 0, 0, "FC", "", false, false, 3052, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ ELRM-20", "ISELRM20 Ammo", 118, false, a );
        add.SetStats( 1.0f, 35000.0f, 34.0f, 0.0f );
        add.SetDamage( 1, 20, 5 );
        add.SetRange( 10, 12, 22, 38 );
        add.SetExplosive( true );
        add.SetLotSize( 4 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-3 (LRM)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetStats( 1.0f, 30000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-5 (LRM)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetStats( 1.0f, 30000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-7 (LRM)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetStats( 1.0f, 30000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'C', 'C', 'C', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-9 (LRM)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetStats( 1.0f, 30000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'C', 'C', 'C', 2370, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-3 (SRM)", "ISMML3 SRM Ammo", 33, false, a );
        add.SetStats( 1.0f, 27000.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 3, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 33 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'C', 'C', 'C', 2370, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-5 (SRM)", "ISMML5 SRM Ammo", 34, false, a );
        add.SetStats( 1.0f, 27000.0f, 6.0f, 0.0f );
        add.SetDamage( 2, 5, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'C', 'C', 'C', 2370, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-7 (SRM)", "ISMML7 SRM Ammo", 35, false, a );
        add.SetStats( 1.0f, 27000.0f, 8.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 14 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'C', 'C', 'C', 2370, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-9 (SRM)", "ISMML9 SRM Ammo", 36, false, a );
        add.SetStats( 1.0f, 27000.0f, 11.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 11 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-3 (LRM Artemis Capable)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Art-IV)" );
        add.SetStats( 1.0f, 60000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-5 (LRM Artemis Capable)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Art-IV)" );
        add.SetStats( 1.0f, 60000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-7 (LRM Artemis Capable)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Art-IV)" );
        add.SetStats( 1.0f, 60000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-9 (LRM Artemis Capable)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Art-IV)" );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-3 (SRM Artemis Capable)", "ISMML3 SRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (SRM Art-IV)" );
        add.SetStats( 1.0f, 54000.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 3, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 33 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-5 (SRM Artemis Capable)", "ISMML5 SRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (SRM Art-IV)" );
        add.SetStats( 1.0f, 54000.0f, 6.0f, 0.0f );
        add.SetDamage( 2, 5, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-7 (SRM Artemis Capable)", "ISMML7 SRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (SRM Art-IV)" );
        add.SetStats( 1.0f, 54000.0f, 8.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 14 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2598, 2855, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-9 (SRM Artemis Capable)", "ISMML9 SRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (SRM Art-IV)" );
        add.SetStats( 1.0f, 54000.0f, 11.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 11 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-3 (LRM Flare)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetStats( 1.0f, 30000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-5 (LRM Flare)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetStats( 1.0f, 30000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-7 (LRM Flare)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetStats( 1.0f, 30000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2377, 2790, 3054, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-9 (LRM Flare)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetStats( 1.0f, 30000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-3 (LRM Fragmentation)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Frag)" );
        add.SetStats( 1.0f, 60000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-5 (LRM Fragmentation)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Frag)" );
        add.SetStats( 1.0f, 60000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-7 (LRM Fragmentation)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Frag)" );
        add.SetStats( 1.0f, 60000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-9 (LRM Fragmentation)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Frag)" );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-3 (SRM Fragmentation)", "ISMML3 SRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (SRM Frag)" );
        add.SetStats( 1.0f, 54000.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 3, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 33 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-5 (SRM Fragmentation)", "ISMML5 SRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (SRM Frag)" );
        add.SetStats( 1.0f, 54000.0f, 6.0f, 0.0f );
        add.SetDamage( 2, 5, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-7 (SRM Fragmentation)", "ISMML7 SRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (SRM Frag)" );
        add.SetStats( 1.0f, 54000.0f, 8.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 14 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'E', 'X', 'D', 2385, 2810, 3050, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-9 (SRM Fragmentation)", "ISMML9 SRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (SRM Frag)" );
        add.SetStats( 1.0f, 54000.0f, 11.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 11 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'C', 'C', 'C', 2400, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ MML-3 (SRM Harpoon)", "ISMML3 SRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (SRM Harp.)" );
        add.SetStats( 1.0f, 5400.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 3, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 33 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'C', 'C', 'C', 2400, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ MML-5 (SRM Harpoon)", "ISMML5 SRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (SRM Harp.)" );
        add.SetStats( 1.0f, 5400.0f, 6.0f, 0.0f );
        add.SetDamage( 2, 5, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'C', 'C', 'C', 2400, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ MML-7 (SRM Harpoon)", "ISMML7 SRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (SRM Harp.)" );
        add.SetStats( 1.0f, 5400.0f, 8.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 14 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'C', 'C', 'C', 2400, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ MML-9 (SRM Harpoon)", "ISMML9 SRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (SRM Harp.)" );
        add.SetStats( 1.0f, 5400.0f, 11.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 11 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-3 (LRM Incendiary)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Incend.)" );
        add.SetStats( 1.0f, 45000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-5 (LRM Incendiary)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Incend.)" );
        add.SetStats( 1.0f, 45000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-7 (LRM Incendiary)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Incend.)" );
        add.SetStats( 1.0f, 45000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3053, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-9 (LRM Incendiary)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Incend.)" );
        add.SetStats( 1.0f, 45000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'D', 'D', 'D', 2380, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ MML-3 (SRM Inferno)", "ISMML3 SRM Ammo", 33, false, a );
        add.SetStats( 1.0f, 13500.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 3, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 33 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'D', 'D', 'D', 2380, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ MML-5 (SRM Inferno)", "ISMML5 SRM Ammo", 34, false, a );
        add.SetStats( 1.0f, 13500.0f, 6.0f, 0.0f );
        add.SetDamage( 2, 5, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'D', 'D', 'D', 2380, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ MML-7 (SRM Inferno)", "ISMML7 SRM Ammo", 35, false, a );
        add.SetStats( 1.0f, 13500.0f, 8.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 14 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'D', 'D', 'D', 2380, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ MML-9 (SRM Inferno)", "ISMML9 SRM Ammo", 36, false, a );
        add.SetStats( 1.0f, 13500.0f, 11.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 11 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-3 (LRM Semi-Guided)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Semi-G)" );
        add.SetStats( 1.0f, 90000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-5 (LRM Semi-Guided)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Semi-G)" );
        add.SetStats( 1.0f, 90000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-7 (LRM Semi-Guided)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Semi-G)" );
        add.SetStats( 1.0f, 90000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-9 (LRM Semi-Guided)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Semi-G)" );
        add.SetStats( 1.0f, 90000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ MML-3 (LRM Swarm)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetStats( 1.0f, 60000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ MML-5 (LRM Swarm)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetStats( 1.0f, 60000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ MML-7 (LRM Swarm)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetStats( 1.0f, 60000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'X', 'D', 2621, 2840, 3053, "TH", "FC", true, true );
        add = new Ammunition( "@ MML-9 (LRM Swarm)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-3 (LRM Swarm-I)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Swrm-I)" );
        add.SetStats( 1.0f, 90000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-5 (LRM Swarm-I)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Swrm-I)" );
        add.SetStats( 1.0f, 90000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-7 (LRM Swarm-I)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Swrm-I)" );
        add.SetStats( 1.0f, 90000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ MML-9 (LRM Swarm-I)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Swrm-I)" );
        add.SetStats( 1.0f, 90000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 2375, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-3 (SRM Tear Gas)", "ISMML3 SRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (SRM Tear)" );
        add.SetStats( 1.0f, 40500.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 3, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 33 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 2375, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-5 (SRM Tear Gas)", "ISMML5 SRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (SRM Tear)" );
        add.SetStats( 1.0f, 40500.0f, 6.0f, 0.0f );
        add.SetDamage( 2, 5, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 2375, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-7 (SRM Tear Gas)", "ISMML7 SRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (SRM Tear)" );
        add.SetStats( 1.0f, 40500.0f, 8.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 14 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 2375, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-9 (SRM Tear Gas)", "ISMML9 SRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (SRM Tear)" );
        add.SetStats( 1.0f, 40500.0f, 11.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 11 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 28400, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ MML-3 (LRM Thunder)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Thund)" );
        add.SetStats( 1.0f, 60000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 28400, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ MML-5 (LRM Thunder)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Thund)" );
        add.SetStats( 1.0f, 60000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 28400, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ MML-7 (LRM Thunder)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Thund)" );
        add.SetStats( 1.0f, 60000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2620, 28400, 3052, "TH", "FC", true, true );
        add = new Ammunition( "@ MML-9 (LRM Thunder)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Thund)" );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-3 (LRM Thunder-Augmented)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Th-Aug)" );
        add.SetStats( 1.0f, 120000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-5 (LRM Thunder-Augmented)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Th-Aug)" );
        add.SetStats( 1.0f, 120000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-7 (LRM Thunder-Augmented)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Th-Aug)" );
        add.SetStats( 1.0f, 120000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-9 (LRM Thunder-Augmented)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Th-Aug)" );
        add.SetStats( 1.0f, 120000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-3 (LRM Thunder-Inferno)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Th-Inf)" );
        add.SetStats( 1.0f, 30000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-5 (LRM Thunder-Inferno)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Th-Inf)" );
        add.SetStats( 1.0f, 30000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-7 (LRM Thunder-Inferno)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Th-Inf)" );
        add.SetStats( 1.0f, 30000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3056, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-9 (LRM Thunder-Inferno)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Th-Inf)" );
        add.SetStats( 1.0f, 30000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-3 (LRM Thunder-Vibrabomb)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Th-Vib)" );
        add.SetStats( 1.0f, 75000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-5 (LRM Thunder-Vibrabomb)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Th-Vib)" );
        add.SetStats( 1.0f, 75000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-7 (LRM Thunder-Vibrabomb)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Th-Vib)" );
        add.SetStats( 1.0f, 75000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-9 (LRM Thunder-Vibrabomb)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Th-Vib)" );
        add.SetStats( 1.0f, 75000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-3 (LRM Thunder-Active)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Th-Act)" );
        add.SetStats( 1.0f, 90000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-5 (LRM Thunder-Active)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Th-Act)" );
        add.SetStats( 1.0f, 90000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-7 (LRM Thunder-Active)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Th-Act)" );
        add.SetStats( 1.0f, 90000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ MML-9 (LRM Thunder-Active)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Th-Act)" );
        add.SetStats( 1.0f, 90000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-3 (LRM Narc Capable)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Narc)" );
        add.SetStats( 1.0f, 60000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-5 (LRM Narc Capable)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Narc)" );
        add.SetStats( 1.0f, 60000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-7 (LRM Narc Capable)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Narc)" );
        add.SetStats( 1.0f, 60000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-9 (LRM Narc Capable)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Narc)" );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-3 (SRM Narc Capable)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (SRM Narc)" );
        add.SetStats( 1.0f, 54000.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 3, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 33 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-5 (SRM Narc Capable)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (SRM Narc)" );
        add.SetStats( 1.0f, 54000.0f, 6.0f, 0.0f );
        add.SetDamage( 2, 5, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-7 (SRM Narc Capable)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (SRM Narc)" );
        add.SetStats( 1.0f, 54000.0f, 8.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 14 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ MML-9 (SRM Narc Capable)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (SRM Narc)" );
        add.SetStats( 1.0f, 54000.0f, 11.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 11 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-3 (LRM Torpedo)", "ISMML3 LRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (LRM Torp)" );
        add.SetStats( 1.0f, 30000.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 3, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-5 (LRM Torpedo)", "ISMML5 LRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (LRM Torp)" );
        add.SetStats( 1.0f, 30000.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-7 (LRM Torpedo)", "ISMML7 LRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (LRM Torp)" );
        add.SetStats( 1.0f, 30000.0f, 8.0f, 0.0f );
        add.SetDamage( 1, 7, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 17 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-9 (LRM Torpedo)", "ISMML9 LRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (LRM Torp)" );
        add.SetStats( 1.0f, 30000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 9, 5 );
        add.SetRange( 6, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 13 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-3 (SRM Torpedo)", "ISMML3 SRM Ammo", 33, false, a );
        add.SetPrintName( "@ MML-3 (SRM Torp)" );
        add.SetStats( 1.0f, 27000.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 3, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 33 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-5 (SRM Torpedo)", "ISMML5 SRM Ammo", 34, false, a );
        add.SetPrintName( "@ MML-5 (SRM Torp)" );
        add.SetStats( 1.0f, 27000.0f, 6.0f, 0.0f );
        add.SetDamage( 2, 5, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-7 (SRM Torpedo)", "ISMML7 SRM Ammo", 35, false, a );
        add.SetPrintName( "@ MML-7 (SRM Torp)" );
        add.SetStats( 1.0f, 27000.0f, 8.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 14 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'C', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ MML-9 (SRM Torpedo)", "ISMML9 SRM Ammo", 36, false, a );
        add.SetPrintName( "@ MML-9 (SRM Torp)" );
        add.SetStats( 1.0f, 27000.0f, 11.0f, 0.0f );
        add.SetDamage( 2, 7, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 11 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2587, 2795, 3035, "TH", "FW", true, true );
        add = new Ammunition( "@ Narc (Homing)", "IS Ammo Narc", 37, false, a );
        add.SetStats( 1.0f, 6000.0f, 0.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3060, 0, 0, "DC", "", false, false );
        add = new Ammunition( "@ Narc (Explosive)", "IS Ammo Narc", 37, false, a );
        add.SetStats( 1.0f, 1500.0f, 0.0f, 0.0f );
        add.SetDamage( 4, 1, 4 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3062, 0, 0, "CS", "", false, false );
        add = new Ammunition( "@ iNarc (Homing)", "IS Ammo iNarc", 38, false, a );
        add.SetStats( 1.0f, 7500.0f, 0.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 4, 9, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 4 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3062, 0, 0, "CS", "", false, false );
        add = new Ammunition( "@ iNarc (ECM)", "iNarc ECM Ammo", 38, false, a );
        add.SetStats( 1.0f, 15000.0f, 0.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 4, 9, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 4 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3062, 0, 0, "CS", "", false, false );
        add = new Ammunition( "@ iNarc (Explosive)", "iNarc Explosive Ammo", 38, false, a );
        add.SetStats( 1.0f, 1500.0f, 0.0f, 0.0f );
        add.SetDamage( 6, 1, 6 );
        add.SetRange( 0, 4, 9, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 4 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3062, 0, 0, "CS", "", false, false );
        add = new Ammunition( "@ iNarc (Haywire)", "iNarc Haywire Ammo", 38, false, a );
        add.SetStats( 1.0f, 20000.0f, 0.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 4, 9, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 4 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3062, 0, 0, "CS", "", false, false );
        add = new Ammunition( "@ iNarc (Nemesis)", "iNarc Nemesis Ammo", 38, false, a );
        add.SetStats( 1.0f, 10000.0f, 0.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 4, 9, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 4 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'D', 'D', 2300, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ AC/2", "ISAC2 Ammo", 40, false, a );
        add.SetStats( 1.0f, 1000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 4, 8, 16, 24 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'C', 'D', 2250, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ AC/5", "ISAC5 Ammo", 41, false, a );
        add.SetStats( 1.0f, 4500.0f, 9.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 3, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'D', 'D', 2460, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ AC/10", "ISAC10 Ammo", 42, false, a );
        add.SetStats( 1.0f, 6000.0f, 15.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'D', 'E', 'D', 2500, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ AC/20", "ISAC20 Ammo", 43, false, a );
        add.SetStats( 1.0f, 10000.0f, 22.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3059, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/2 (Armor-Piercing)", "ISAC2 Ammo", 40, false, a );
        add.SetPrintName( "@ AC/2 (AP)" );
        add.SetStats( 1.0f, 4000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 4, 8, 16, 24 );
        add.SetExplosive( true );
        add.SetLotSize( 22 );
        add.SetToHit( 1, 1, 1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3059, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/5 (Armor-Piercing)", "ISAC5 Ammo", 41, false, a );
        add.SetPrintName( "@ AC/5 (AP)" );
        add.SetStats( 1.0f, 18000.0f, 9.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 3, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        add.SetToHit( 1, 1, 1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3059, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/10 (Armor-Piercing)", "ISAC10 Ammo", 42, false, a );
        add.SetPrintName( "@ AC/10 (AP)" );
        add.SetStats( 1.0f, 24000.0f, 15.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        add.SetToHit( 1, 1, 1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3059, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/20 (Armor-Piercing)", "ISAC20 Ammo", 43, false, a );
        add.SetPrintName( "@ AC/20 (AP)" );
        add.SetStats( 1.0f, 40000.0f, 22.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 2 );
        add.SetToHit( 1, 1, 1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3055, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/2 (Flechette)", "ISAC2 Ammo", 40, false, a );
        add.SetStats( 1.0f, 1500.0f, 5.0f, 0.0f );
        add.SetDamage( 1, 1, 1 );
        add.SetRange( 4, 8, 16, 24 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3055, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/5 (Flechette)", "ISAC5 Ammo", 41, false, a );
        add.SetStats( 1.0f, 6750.0f, 9.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 3, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3055, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/10 (Flechette)", "ISAC10 Ammo", 42, false, a );
        add.SetStats( 1.0f, 9000.0f, 15.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3055, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/20 (Flechette)", "ISAC20 Ammo", 43, false, a );
        add.SetStats( 1.0f, 15000.0f, 22.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3055, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/2 (Precision)", "ISAC2 Ammo", 40, false, a );
        add.SetStats( 1.0f, 6000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 4, 8, 16, 24 );
        add.SetExplosive( true );
        add.SetLotSize( 22 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3055, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/5 (Precision)", "ISAC5 Ammo", 41, false, a );
        add.SetStats( 1.0f, 27000.0f, 9.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 3, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3055, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/10 (Precision)", "ISAC10 Ammo", 42, false, a );
        add.SetStats( 1.0f, 36000.0f, 15.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3055, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ AC/20 (Precision)", "ISAC20 Ammo", 43, false, a );
        add.SetStats( 1.0f, 60000.0f, 22.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 2 );
        ISAM.add( add );

        a = new AvailableCode( false, 'A', 'A', 'A', 'B', 1950, 0, 0, "PS", "", false, false );
        add = new Ammunition( "@ Machine Gun", "ISMG Ammo (200)", 48, false, a );
        add.SetPrintName( "@ MG" );
        add.SetStats( 1.0f, 1000.0f, 1.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 200 );
        ISAM.add( add );

        a = new AvailableCode( false, 'A', 'A', 'A', 'B', 1950, 0, 0, "PS", "", false, false );
        add = new Ammunition( "@ Machine Gun (1/2)", "ISMG Ammo (100)", 48, false, a );
        add.SetPrintName( "@ MG (1/2)" );
        add.SetStats( 0.5f, 500.0f, 0.5f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 100 );
        ISAM.add( add );

        a = new AvailableCode( false, 'A', 'X', 'X', 'C', 3068, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ Light Machine Gun", "ISLightMG Ammo (200)", 78, false, a );
        add.SetPrintName( "@ LMG" );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 1, 1, 1 );
        add.SetRange( 0, 2, 4, 6 );
        add.SetExplosive( true );
        add.SetLotSize( 200 );
        ISAM.add( add );

        a = new AvailableCode( false, 'A', 'X', 'X', 'C', 3068, 0, 0, "CC", "", false, false );
        add = new Ammunition( "@ Light Machine Gun (1/2)", "ISLightMG Ammo (100)", 78, false, a );
        add.SetPrintName( "@ LMG (1/2)" );
        add.SetStats( 0.5f, 250.0f, 0.5f, 0.0f );
        add.SetDamage( 1, 1, 1 );
        add.SetRange( 0, 2, 4, 6 );
        add.SetExplosive( true );
        add.SetLotSize( 100 );
        ISAM.add( add );

        a = new AvailableCode( false, 'A', 'X', 'X', 'C', 3068, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ Heavy Machine Gun", "ISHeavyMG Ammo (100)", 79, false, a );
        add.SetPrintName( "@ HMG" );
        add.SetStats( 1.0f, 1000.0f, 1.0f, 0.0f );
        add.SetDamage( 3, 1, 1 );
        add.SetRange( 0, 1, 2, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 100 );
        ISAM.add( add );

        a = new AvailableCode( false, 'A', 'X', 'X', 'C', 3068, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ Heavy Machine Gun (1/2)", "ISHeavyMG Ammo (50)", 79, false, a );
        add.SetPrintName( "@ HMG (1/2)" );
        add.SetStats( 0.5f, 500.0f, 0.5f, 0.0f );
        add.SetDamage( 3, 1, 1 );
        add.SetRange( 0, 1, 2, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ LB 2-X AC (Slug)", "ISLBXAC2 Ammo", 50, false, a );
        add.SetPrintName( "@ LB 2-X (Slug)" );
        add.SetStats( 1.0f, 2000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 4, 9, 18, 27 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ LB 2-X AC (Cluster)", "ISLBXAC2 CL Ammo", 50, false, a );
        add.SetPrintName( "@ LB 2-X (Cluster)" );
        add.SetStats( 1.0f, 3300.0f, 5.0f, 0.0f );
        add.SetDamage( 1, 2, 1 );
        add.SetRange( 4, 9, 18, 27 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        add.SetToHit( -1, -1, -1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ LB 5-X AC (Slug)", "ISLBXAC5 Ammo", 51, false, a );
        add.SetPrintName( "@ LB 5-X (Slug)" );
        add.SetStats( 1.0f, 9000.0f, 10.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ LB 5-X AC (Cluster)", "ISLBXAC5 CL Ammo", 51, false, a );
        add.SetPrintName( "@ LB 5-X (Cluster)" );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 1, 5, 1 );
        add.SetRange( 3, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        add.SetToHit( -1, -1, -1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2595, 2840, 3035, "TH", "FC", true, true );
        add = new Ammunition( "@ LB 10-X AC (Slug)", "ISLBXAC10 Ammo", 52, false, a );
        add.SetPrintName( "@ LB 10-X (Slug)" );
        add.SetStats( 1.0f, 12000.0f, 19.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2595, 2840, 3035, "TH", "FC", true, true );
        add = new Ammunition( "@ LB 10-X AC (Cluster)", "ISLBXAC10 CL Ammo", 52, false, a );
        add.SetPrintName( "@ LB 10-X (Cluster)" );
        add.SetStats( 1.0f, 20000.0f, 19.0f, 0.0f );
        add.SetDamage( 1, 10, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        add.SetToHit( -1, -1, -1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ LB 20-X AC (Slug)", "ISLBXAC20 Ammo", 53, false, a );
        add.SetPrintName( "@ LB 20-X (Slug)" );
        add.SetStats( 1.0f, 20000.0f, 30.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3058, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ LB 20-X AC (Cluster)", "ISLBXAC20 CL Ammo", 53, false, a );
        add.SetPrintName( "@ LB 20-X (Cluster)" );
        add.SetStats( 1.0f, 34000.0f, 30.0f, 0.0f );
        add.SetDamage( 1, 20, 1 );
        add.SetRange( 0, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        add.SetToHit( -1, -1, -1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ Light AC/2", "ISLAC2 Ammo", 58, false, a );
        add.SetStats( 1.0f, 1000.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3059, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ Light AC/2 (Armor-Piercing)", "ISLAC2 Ammo", 58, false, a );
        add.SetPrintName( "@ LAC/2 (AP)" );
        add.SetStats( 1.0f, 4000.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 22 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3055, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ Light AC/2 (Flechette)", "ISLAC2 Ammo", 58, false, a );
        add.SetPrintName( "@ LAC/2 (Flechette)" );
        add.SetStats( 1.0f, 1500.0f, 4.0f, 0.0f );
        add.SetDamage( 1, 1, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3062, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ Light AC/2 (Precision)", "ISLAC2 Ammo", 58, false, a );
        add.SetPrintName( "@ LAC/2 (Precision)" );
        add.SetStats( 1.0f, 6000.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 22 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'X', 'X', 'F', 3068, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ Light AC/5", "ISLAC5 Ammo", 59, false, a );
        add.SetStats( 1.0f, 4500.0f, 8.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3059, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ Light AC/5 (Armor-Piercing)", "ISLAC5 Ammo", 59, false, a );
        add.SetPrintName( "@ LAC/5 (AP)" );
        add.SetStats( 1.0f, 18000.0f, 8.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3055, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ Light AC/5 (Flechette)", "ISLAC5 Ammo", 59, false, a );
        add.SetPrintName( "@ LAC/5 (Flechette)" );
        add.SetStats( 1.0f, 6750.0f, 8.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3062, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ Light AC/5 (Precision)", "ISLAC5 Ammo", 59, false, a );
        add.SetPrintName( "@ LAC/5 (Precision)" );
        add.SetStats( 1.0f, 27000.0f, 8.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3062, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ Rotary AC/2", "IS Rotary AC/2 Ammo", 60, false, a );
        add.SetStats( 1.0f, 3000.0f, 15.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3062, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ Rotary AC/5", "IS Rotary AC/5 Ammo", 61, false, a );
        add.SetStats( 1.0f, 12000.0f, 31.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ Ultra AC/2", "ISUltraAC2 Ammo", 62, false, a );
        add.SetStats( 1.0f, 1000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 3, 8, 17, 25 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'D', 'F', 'D', 2640, 2915, 3035, "TH", "FC", true, true );
        add = new Ammunition( "@ Ultra AC/5", "ISUltraAC5 Ammo", 63, false, a );
        add.SetStats( 1.0f, 9000.0f, 14.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 2, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3057, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ Ultra AC/10", "ISUltraAC10 Ammo", 64, false, a );
        add.SetStats( 1.0f, 12000.0f, 26.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3060, 0, 0, "LA", "", false, false );
        add = new Ammunition( "@ Ultra AC/20", "ISUltraAC20 Ammo", 65, false, a );
        add.SetStats( 1.0f, 20000.0f, 35.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 3, 7, 10 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3056, 0, 0, "FW", "", false, false );
        add = new Ammunition( "@ Light Gauss Rifle", "ISLightGauss Ammo", 70, false, a );
        add.SetStats( 1.0f, 20000.0f, 20.0f, 0.0f );
        add.SetDamage( 8, 1, 1 );
        add.SetRange( 3, 8, 17, 25 );
        add.SetExplosive( false );
        add.SetLotSize( 16 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'D', 'F', 'D', 2590, 2865, 3040, "TH", "FW", false, false );
        add = new Ammunition( "@ Gauss Rifle", "ISGauss Ammo", 71, false, a );
        add.SetStats( 1.0f, 20000.0f, 40.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 2, 7, 15, 22 );
        add.SetExplosive( false );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3061, 0, 0, "LA", "", false, false );
        add = new Ammunition( "@ Heavy Gauss Rifle", "IS Heavy Gauss Rifle Ammo", 72, false, a );
        add.SetStats( 1.0f, 20000.0f, 43.0f, 0.0f );
        add.SetDamage( 25, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( false );
        add.SetLotSize( 4 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3065, 0, 0, "LA", "", false, false, 3061, true, "LA", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Improved Heavy Gauss Rifle", "IS Improved Heavy Gauss Rifle Ammo", 84, false, a );
        add.SetPrintName( "@ iHGR" );
        add.SetStats( 1.0f, 20000.0f, 48.0f, 0.0f );
        add.SetDamage( 22, 1, 1 );
        add.SetRange( 3, 6, 12, 19 );
        add.SetExplosive( false );
        add.SetLotSize( 4 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'D', 3072, 0, 0, "FS", "", false, false, 0, false, "FS", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Magshot Gauss Rifle", "IS Magshot Gauss Rifle Ammo", 107, false, a );
        add.SetStats( 1.0f, 1000.0f, 2.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( false );
        add.SetLotSize( 50 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'X', 'X', 'F', 3051, 0, 0, "FC", "", false, false, 3050, true, "FS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Silver Bullet Gauss", "IS Silver Bullet Gauss Ammo", 108, false, a );
        add.SetStats( 1.0f, 25000.0f, 25.0f, 0.0f );
        add.SetDamage( 15, 15, 15 );
        add.SetRange( 2, 7, 15, 22 );
        add.SetExplosive( false );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode(false, 'E', 'X', 'X', 'F', 3067, 0, 0, "FS", "", false, false, 3065, true, "FS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ BattleMech Taser", "ISBattleMechTaser", 106, false, a );
        add.SetStats( 1.0f, 2000.0f, 5.0f, 0.0f );
        add.SetDamage( 1, 1, 1 );
        add.SetRange( 0, 1, 2, 4 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        add.SetToHit( 1, 1, 1 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2617, 2796, 3040, "TH", "FC", true, true );
        add = new Ammunition( "@ Anti-Missile System", "IS AMS Ammo", 82, false, a );
        add.SetStats( 1.0f, 2000.0f, 0.0f, 11.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 0, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'E', 2600, 2830, 3044, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Non-Homing)", "ISArrowIVAmmo", 85, false, a );
        add.SetPrintName( "@ Arrow IV (Non-Home)" );
        add.SetStats( 1.0f, 10000.0f, 30.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'E', 2600, 2830, 3045, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Homing)", "ISArrowIVAmmo", 85, false, a );
        add.SetStats( 1.0f, 15000.0f, 30.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'E', 2620, 2830, 3047, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Cluster)", "ISArrowIVAmmo", 85, false, a );
        add.SetStats( 1.0f, 15000.0f, 30.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3068, 0, 0, "CC", "", false, false, 3066, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Arrow IV (ADA)", "ISArrowIVAmmo", 85, false, a );
        add.SetStats( 1.0f, 45000.0f, 30.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'D', 'D', 'D', 2621, 2831, 3047, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Illumination)", "ISArrowIVAmmo", 85, false, a );
        add.SetPrintName( "@ Arrow IV (Illum)" );
        add.SetStats( 1.0f, 5000.0f, 30.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3053, 0, 0, "FC", "", false, false, 3052, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Arrow IV (Laser-Inhibiting)", "ISArrowIVAmmo", 85, false, a );
        add.SetPrintName( "@ Arrow IV (Laser-Inhib)" );
        add.SetStats( 1.0f, 40000.0f, 30.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 2 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'X', 'X', 'D', 3055, 0, 0, "CC", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Inferno)", "ISArrowIVAmmo", 85, false, a );
        add.SetStats( 1.0f, 10000.0f, 30.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'E', 'F', 'E', 2600, 2830, 3044, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Smoke)", "ISArrowIVAmmo", 85, false, a );
        add.SetStats( 1.0f, 5000.0f, 30.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2600, 2833, 3051, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Thunder)", "ISArrowIVAmmo", 85, false, a );
        add.SetStats( 1.0f, 15000.0f, 30.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'X', 'X', 'E', 3065, 0, 0, "CC", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Thunder Active)", "ISArrowIVAmmo", 85, false, a );
        add.SetPrintName( "@ Arrow IV (Th-Act)" );
        add.SetStats( 1.0f, 30000.0f, 30.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'X', 'X', 'E', 3065, 0, 0, "CC", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Thunder Vibrabomb)", "ISArrowIVAmmo", 85, false, a );
        add.SetPrintName( "@ Arrow IV (Th-Vib)" );
        add.SetStats( 1.0f, 20000.0f, 30.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'C', 'C', 'C', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper", "ISThumperAmmo", 87, false, a );
        add.SetStats( 1.0f, 4500.0f, 5.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'E', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Cluster)", "ISThumperAmmo", 87, false, a );
        add.SetStats( 1.0f, 6750.0f, 5.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'F', 2645, 2825, 3051, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Copperhead)", "ISThumperAmmo", 87, false, a );
        add.SetPrintName( "@ Thumper (Cprhead)" );
        add.SetStats( 1.0f, 6750.0f, 5.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'F', 'F', 'E', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Flechette)", "ISThumperAmmo", 87, false, a );
        add.SetStats( 1.0f, 6750.0f, 5.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'D', 'D', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Illumination)", "ISThumperAmmo", 87, false, a );
        add.SetPrintName( "@ Thumper (Illum)" );
        add.SetStats( 1.0f, 2250.0f, 5.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'A', 'A', 'A', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Smoke)", "ISThumperAmmo", 87, false, a );
        add.SetStats( 1.0f, 2250.0f, 5.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2600, 2833, 3051, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Thunder)", "ISThumperAmmo", 87, false, a );
        add.SetStats( 1.0f, 6750.0f, 5.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'C', 'C', 'C', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper", "ISSniperAmmo", 88, false, a );
        add.SetStats( 1.0f, 6000.0f, 11.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'E', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Cluster)", "ISSniperAmmo", 88, false, a );
        add.SetStats( 1.0f, 9000.0f, 11.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'E', 'F', 'F', 2645, 2825, 3051, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Copperhead)", "ISSniperAmmo", 88, false, a );
        add.SetStats( 1.0f, 9000.0f, 11.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'F', 'F', 'E', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Flechette)", "ISSniperAmmo", 88, false, a );
        add.SetStats( 1.0f, 9000.0f, 11.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'D', 'D', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Illumination)", "ISSniperAmmo", 88, false, a );
        add.SetStats( 1.0f, 3000.0f, 11.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'A', 'A', 'A', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Smoke)", "ISSniperAmmo", 88, false, a );
        add.SetStats( 1.0f, 3000.0f, 11.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'E', 'X', 'E', 2600, 2833, 3051, "TH", "CC", true, true, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Thunder)", "ISSniperAmmo", 88, false, a );
        add.SetStats( 1.0f, 9000.0f, 11.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon", "ISLongTomCannonAmmo", 128, false, a );
        add.SetStats( 1.0f, 20000.0f, 41.0f, 0.0f );
        add.SetDamage( 25, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Cluster)", "ISLongTomCannonAmmo", 128, false, a );
        add.SetStats( 1.0f, 30000.0f, 41.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Copperhead)", "ISLongTomCannonAmmo", 128, false, a );
        add.SetStats( 1.0f, 30000.0f, 41.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Flechette)", "ISLongTomCannonAmmo", 128, false, a );
        add.SetStats( 1.0f, 30000.0f, 41.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Illumination)", "ISLongTomCannonAmmo", 128, false, a );
        add.SetStats( 1.0f, 10000.0f, 41.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Smoke)", "ISLongTomCannonAmmo", 128, false, a );
        add.SetStats( 1.0f, 10000.0f, 41.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Thunder)", "ISLongTomCannonAmmo", 128, false, a );
        add.SetStats( 1.0f, 30000.0f, 41.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        ISAM.add( add );
        
        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon", "ISSniperCannonAmmo", 127, false, a );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Cluster)", "ISSniperCannonAmmo", 127, false, a );
        add.SetStats( 1.0f, 22500.0f, 10.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Copperhead)", "ISSniperCannonAmmo", 127, false, a );
        add.SetStats( 1.0f, 22500.0f, 10.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Flechette)", "ISSniperCannonAmmo", 127, false, a );
        add.SetStats( 1.0f, 22500.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Illumination)", "ISSniperCannonAmmo", 127, false, a );
        add.SetStats( 1.0f, 7500.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Smoke)", "ISSniperCannonAmmo", 127, false, a );
        add.SetStats( 1.0f, 7500.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Thunder)", "ISSniperCannonAmmo", 127, false, a );
        add.SetStats( 1.0f, 22500.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon", "ISThumperCannonAmmo", 126, false, a );
        add.SetStats( 1.0f, 10000.0f, 10.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Cluster)", "ISThumperCannonAmmo", 126, false, a );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Copperhead)", "ISThumperCannonAmmo", 126, false, a );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Flechette)", "ISThumperCannonAmmo", 126, false, a );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Illumination)", "ISThumperCannonAmmo", 126, false, a );
        add.SetStats( 1.0f, 5000.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Smoke)", "ISThumperCannonAmmo", 126, false, a );
        add.SetStats( 1.0f, 5000.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'X', 'F', 'E', 3000, 0, 0, "LC", "", false, false, 3012, true, "LC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Thunder)", "ISThumperCannonAmmo", 126, false, a );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode(false, 'D', 'X', 'X', 'F', 3059, 0, 0, "CC", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ HVAC/2", "ISHVAC2 Ammo", 94, false, a );
        add.SetStats( 1.0f, 3000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 3, 10, 20, 35 );
        add.SetExplosive( true );
        add.SetLotSize( 30 );
        ISAM.add( add );

        a = new AvailableCode(false, 'D', 'X', 'X', 'F', 3059, 0, 0, "CC", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ HVAC/5", "ISHVAC5 Ammo", 95, false, a );
        add.SetStats( 1.0f, 10000.0f, 14.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 3, 8, 16, 28 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        ISAM.add( add );

        a = new AvailableCode(false, 'D', 'X', 'X', 'F', 3059, 0, 0, "CC", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ HVAC/10", "ISHVAC10 Ammo", 96, false, a );
        add.SetStats( 1.0f, 20000.0f, 20.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 6, 12, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thunderbolt-5", "ISThunderbolt5 Ammo", 97, false, a );
        add.SetStats( 1.0f, 50000.0f, 8.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 5, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thunderbolt-10", "ISThunderbolt10 Ammo", 98, false, a );
        add.SetStats( 1.0f, 50000.0f, 8.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 5, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thunderbolt-15", "ISThunderbolt15 Ammo", 99, false, a );
        add.SetStats( 1.0f, 50000.0f, 8.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 5, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 4 );
        ISAM.add( add );

        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3072, 0, 0, "FS/LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thunderbolt-20", "ISThunderbolt20 Ammo", 100, false, a );
        add.SetStats( 1.0f, 50000.0f, 8.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 5, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 3 );
        ISAM.add( add );

        a = new AvailableCode(false, 'B', 'C', 'F', 'X', 1950, 3050, 0, "PS", "", true, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Light Rifle", "ISLightRifle Ammo", 101, false, a );
        add.SetStats( 1.0f, 800.0f, 3.0f, 0.0f );
        add.SetDamage( 3, 1, 1 );
        add.SetRange( 0, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 18 );
        ISAM.add( add );

        a = new AvailableCode(false, 'B', 'C', 'F', 'X', 1950, 3050, 0, "PS", "", true, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Medium Rifle", "ISMediumRifle Ammo", 102, false, a );
        add.SetStats( 1.0f, 1000.0f, 6.0f, 0.0f );
        add.SetDamage( 6, 1, 1 );
        add.SetRange( 1, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 9 );
        ISAM.add( add );

        a = new AvailableCode(false, 'B', 'C', 'F', 'X', 1950, 3050, 0, "PS", "", true, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Heavy Rifle", "ISHeavyRifle Ammo", 103, false, a );
        add.SetStats( 1.0f, 3000.0f, 11.0f, 0.0f );
        add.SetDamage( 9, 1, 1 );
        add.SetRange( 2, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        ISAM.add( add );

        a = new AvailableCode(false, 'C', 'C', 'C', 'C', 2310, 0, 0, "FWL", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        add = new Ammunition( "@ Nail/Rivet Gun", "ISNail/RivetGunAmmo", 133, false, a );
        add.SetStats( 1.0f, 300.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 1, 0, 0 );
        add.SetExplosive( false );
        add.SetLotSize( 300 );
        ISAM.add( add );

        a = new AvailableCode(false, 'C', 'C', 'C', 'C', 2310, 0, 0, "FWL", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        add = new Ammunition( "@ Nail/Rivet Gun (1/2)", "ISNail/RivetGunHalfAmmo", 133, false, a );
        add.SetStats( 0.5f, 150.0f, 0.5f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 1, 0, 0 );
        add.SetExplosive( false );
        add.SetLotSize( 150 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'B', 'B', 'B', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Coolant", "ISFluidGunCoolantAmmo", 134, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'C', 'C', 'D', 'D', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Corrosive", "ISFluidGunCorrosiveAmmo", 134, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Flame Retardant Foam", "ISFluidGunFoamAmmo", 134, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'D', 'D', 'E', 'D', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Inferno Fuel", "ISFluidGunInfernoFuelAmmo", 134, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Oil Slick", "ISFluidGunOilSlickAmmo", 134, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'B', 'B', 'B', 'B', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Paint/Obscurant", "ISFluidGunPaintAmmo", 134, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        ISAM.add( add );

        a = new AvailableCode( false, 'A', 'A', 'A', 'A', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Water", "ISFluidGunWaterAmmo", 134, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( false );
        add.SetLotSize( 20 );
        ISAM.add( add );

 /*******************************************************************************
 *      START CLAN AMMUNITION
 ******************************************************************************/
        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3069, 0, 0, "CDS", "", false, false );
        add = new Ammunition( "@ Plasma Cannon", "CLPlasmaCannon Ammo", 3, true, a );
        add.SetStats( 1.0f, 12000.0f, 21.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( false );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'A', 'X', 'A', 'A', 1950, 0, 0, "PS", "", false, false );
        add = new Ammunition( "@ Vehicle Flamer", "CLVehicleFlamer Ammo", 4, true, a );
        add.SetStats( 1.0f, 1000.0f, 1.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode(true, 'C', 'X', 'X', 'E', 3067, 0, 0, "CJF", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Heavy Flamer", "ISHeavyFlamer Ammo", 105, true, a );
        add.SetStats( 1.0f, 2000.0f, 2.0f, 0.0f );
        add.SetDamage( 4, 1, 1 );
        add.SetRange( 0, 2, 3, 4 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3059, 0, 0, "CHH", "", false, false, 3057, true, "CHH", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Small Chemical Laser", "CLSmallChemLaserAmmo", 91, true, a );
        add.SetPrintName( "@ Small Chem Laser" );
        add.SetStats( 1.0f, 30000.0f, 1.0f, 0.0f );
        add.SetDamage( 3, 1, 1 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 60 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3059, 0, 0, "CHH", "", false, false, 3057, true, "CHH", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Medium Chemical Laser", "CLMediumChemLaserAmmo", 92, true, a );
        add.SetPrintName( "@ Medium Chem Laser" );
        add.SetStats( 1.0f, 30000.0f, 5.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 30 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'X', 'E', 3059, 0, 0, "CHH", "", false, false, 3057, true, "CHH", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Large Chemical Laser", "CLLargeChemLaserAmmo", 93, true, a );
        add.SetPrintName( "@ Large Chem Laser" );
        add.SetStats( 1.0f, 30000.0f, 12.0f, 0.0f );
        add.SetDamage( 8, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'A', 'A', 2370, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-2", "CLSRM2 Ammo", 8, true, a );
        add.SetStats( 1.0f, 27000.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'A', 'A', 2370, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-4", "CLSRM4 Ammo", 9, true, a );
        add.SetStats( 1.0f, 27000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'A', 'A', 2370, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-6", "CLSRM6 Ammo", 10, true, a );
        add.SetStats( 1.0f, 27000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2598, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-2 (Artemis Capable)", "CLSRM2 Ammo", 8, true, a );
        add.SetPrintName( "@ SRM-2 (Artemis)" );
        add.SetStats( 1.0f, 54000.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2598, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-4 (Artemis Capable)", "CLSRM4 Ammo", 9, true, a );
        add.SetPrintName( "@ SRM-4 (Artemis)" );
        add.SetStats( 1.0f, 54000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2598, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-6 (Artemis Capable)", "CLSRM6 Ammo", 10, true, a );
        add.SetPrintName( "@ SRM-6 (Artemis)" );
        add.SetStats( 1.0f, 54000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2385, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-2 (Fragmentation)", "CLSRM2 Ammo", 8, true, a );
        add.SetPrintName( "@ SRM-2 (Frag)" );
        add.SetStats( 1.0f, 54000.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2385, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-4 (Fragmentation)", "CLSRM4 Ammo", 9, true, a );
        add.SetPrintName( "@ SRM-4 (Frag)" );
        add.SetStats( 1.0f, 54000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2385, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-6 (Fragmentation)", "CLSRM6 Ammo", 10, true, a );
        add.SetPrintName( "@ SRM-6 (Frag)" );
        add.SetStats( 1.0f, 54000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        CLAM.add( add );

        a = new AvailableCode( true, 'D', 'X', 'C', 'C', 2400, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ SRM-2 (Harpoon)", "CLSRM2 Ammo", 8, true, a );
        add.SetStats( 1.0f, 5400.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        CLAM.add( add );

        a = new AvailableCode( true, 'D', 'X', 'C', 'C', 2400, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ SRM-4 (Harpoon)", "CLSRM4 Ammo", 9, true, a );
        add.SetStats( 1.0f, 5400.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        CLAM.add( add );

        a = new AvailableCode( true, 'D', 'X', 'C', 'C', 2400, 0, 0, "LC", "", false, false );
        add = new Ammunition( "@ SRM-6 (Harpoon)", "CLSRM6 Ammo", 10, true, a );
        add.SetStats( 1.0f, 5400.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'C', 'C', 2380, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ SRM-2 (Inferno)", "CLSRM2 Ammo", 8, true, a );
        add.SetStats( 1.0f, 13500.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'C', 'C', 2380, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ SRM-4 (Inferno)", "CLSRM4 Ammo", 9, true, a );
        add.SetStats( 1.0f, 13500.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'C', 'C', 2380, 0, 0, "TC", "", false, false );
        add = new Ammunition( "@ SRM-6 (Inferno)", "CLSRM6 Ammo", 10, true, a );
        add.SetStats( 1.0f, 13500.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'B', 'B', 2375, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-2 (Tear Gas)", "CLSRM2 Ammo", 8, true, a );
        add.SetStats( 1.0f, 40500.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'B', 'B', 2375, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-4 (Tear Gas)", "CLSRM4 Ammo", 9, true, a );
        add.SetStats( 1.0f, 40500.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'B', 'B', 2375, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-6 (Tear Gas)", "CLSRM6 Ammo", 10, true, a );
        add.SetStats( 1.0f, 40500.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2597, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-2 (Narc Capable)", "CLSRM2 Ammo", 8, true, a );
        add.SetPrintName( "@ SRM-2 (Narc)" );
        add.SetStats( 1.0f, 54000.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2597, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-4 (Narc Capable)", "CLSRM4 Ammo", 9, true, a );
        add.SetPrintName( "@ SRM-4 (Narc)" );
        add.SetStats( 1.0f, 54000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2597, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-6 (Narc Capable)", "CLSRM6 Ammo", 10, true, a );
        add.SetPrintName( "@ SRM-6 (Narc)" );
        add.SetStats( 1.0f, 54000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-2 (Torpedo)", "CLSRM2 Ammo", 8, true, a );
        add.SetStats( 1.0f, 27000.0f, 3.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-4 (Torpedo)", "CLSRM4 Ammo", 9, true, a );
        add.SetStats( 1.0f, 27000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ SRM-6 (Torpedo)", "CLSRM6 Ammo", 10, true, a );
        add.SetStats( 1.0f, 27000.0f, 7.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2647, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ Streak SRM-2", "CLStreakSRM2 Ammo", 14, true, a );
        add.SetStats( 1.0f, 54000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 2, 2 );
        add.SetRange( 0, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2826, 0, 0, "CSA", "", false, false );
        add = new Ammunition( "@ Streak SRM-4", "CLStreakSRM4 Ammo", 15, true, a );
        add.SetStats( 1.0f, 54000.0f, 10.0f, 0.0f );
        add.SetDamage( 2, 4, 2 );
        add.SetRange( 0, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 25 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2826, 0, 0, "CSA", "", false, false );
        add = new Ammunition( "@ Streak SRM-6", "CLStreakSRM6 Ammo", 16, true, a );
        add.SetStats( 1.0f, 54000.0f, 15.0f, 0.0f );
        add.SetDamage( 2, 6, 2 );
        add.SetRange( 0, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 15 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CCY", "", false, false );
        add = new Ammunition( "@ ATM-3", "CLATM3 Ammo", 21, true, a );
        add.SetStats( 1.0f, 75000.0f, 14.0f, 0.0f );
        add.SetDamage( 2, 5, 3 );
        add.SetRange( 4, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CSA", "", false, false );
        add = new Ammunition( "@ ATM-3 (ER)", "CLATM3 ER Ammo", 21, true, a );
        add.SetStats( 1.0f, 75000.0f, 14.0f, 0.0f );
        add.SetDamage( 1, 5, 3 );
        add.SetRange( 4, 9, 18, 27 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CCY", "", false, false );
        add = new Ammunition( "@ ATM-3 (HE)", "CLATM3 HE Ammo", 21, true, a );
        add.SetStats( 1.0f, 75000.0f, 14.0f, 0.0f );
        add.SetDamage( 3, 5, 3 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CCY", "", false, false );
        add = new Ammunition( "@ ATM-6", "CLATM6 Ammo", 22, true, a );
        add.SetStats( 1.0f, 75000.0f, 26.0f, 0.0f );
        add.SetDamage( 2, 5, 6 );
        add.SetRange( 4, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CSA", "", false, false );
        add = new Ammunition( "@ ATM-6 (ER)", "CLATM6 ER Ammo", 22, true, a );
        add.SetStats( 1.0f, 75000.0f, 26.0f, 0.0f );
        add.SetDamage( 1, 5, 6 );
        add.SetRange( 4, 9, 18, 27 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CCY", "", false, false );
        add = new Ammunition( "@ ATM-6 (HE)", "CLATM6 HE Ammo", 22, true, a );
        add.SetStats( 1.0f, 75000.0f, 26.0f, 0.0f );
        add.SetDamage( 3, 5, 6 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CCY", "", false, false );
        add = new Ammunition( "@ ATM-9", "CLATM9 Ammo", 23, true, a );
        add.SetStats( 1.0f, 75000.0f, 36.0f, 0.0f );
        add.SetDamage( 2, 5, 9 );
        add.SetRange( 4, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 7 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CSA", "", false, false );
        add = new Ammunition( "@ ATM-9 (ER)", "CLATM9 ER Ammo", 23, true, a );
        add.SetStats( 1.0f, 75000.0f, 36.0f, 0.0f );
        add.SetDamage( 1, 5, 9 );
        add.SetRange( 4, 9, 18, 27 );
        add.SetExplosive( true );
        add.SetLotSize( 7 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3054, 0, 0, "CCY", "", false, false );
        add = new Ammunition( "@ ATM-9 (HE)", "CLATM9 HE Ammo", 23, true, a );
        add.SetStats( 1.0f, 75000.0f, 36.0f, 0.0f );
        add.SetDamage( 3, 5, 9 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 7 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3055, 0, 0, "CCY", "", false, false );
        add = new Ammunition( "@ ATM-12", "CLATM12 Ammo", 24, true, a );
        add.SetStats( 1.0f, 75000.0f, 52.0f, 0.0f );
        add.SetDamage( 2, 5, 12 );
        add.SetRange( 4, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3055, 0, 0, "CSA", "", false, false );
        add = new Ammunition( "@ ATM-12 (ER)", "CLATM12 ER Ammo", 24, true, a );
        add.SetStats( 1.0f, 75000.0f, 52.0f, 0.0f );
        add.SetDamage( 1, 5, 12 );
        add.SetRange( 4, 9, 18, 27 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3055, 0, 0, "CCY", "", false, false );
        add = new Ammunition( "@ ATM-12 (HE)", "CLATM12 HE Ammo", 24, true, a );
        add.SetStats( 1.0f, 75000.0f, 52.0f, 0.0f );
        add.SetDamage( 3, 5, 12 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-5", "CLLRM5 Ammo", 29, true, a );
        add.SetStats( 1.0f, 30000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-10", "CLLRM10 Ammo", 30, true, a );
        add.SetStats( 1.0f, 30000.0f, 14.0f, 0.0f );
        add.SetDamage( 1, 5, 10 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-15", "CLLRM15 Ammo", 31, true, a );
        add.SetStats( 1.0f, 30000.0f, 21.0f, 0.0f );
        add.SetDamage( 1, 5, 15 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2400, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-20", "CLLRM20 Ammo", 32, true, a );
        add.SetStats( 1.0f, 30000.0f, 27.0f, 0.0f );
        add.SetDamage( 1, 5, 20 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2598, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-5 (Artemis Capable)", "CLLRM5 Ammo", 29, true, a );
        add.SetPrintName( "@ LRM-5 (Artemis)" );
        add.SetStats( 1.0f, 60000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2598, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-10 (Artemis Capable)", "CLLRM10 Ammo", 30, true, a );
        add.SetPrintName( "@ LRM-10 (Artemis)" );
        add.SetStats( 1.0f, 60000.0f, 14.0f, 0.0f );
        add.SetDamage( 1, 5, 10 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2598, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-15 (Artemis Capable)", "CLLRM15 Ammo", 31, true, a );
        add.SetPrintName( "@ LRM-15 (Artemis)" );
        add.SetStats( 1.0f, 60000.0f, 21.0f, 0.0f );
        add.SetDamage( 1, 5, 15 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2598, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-20 (Artemis Capable)", "CLLRM20 Ammo", 32, true, a );
        add.SetPrintName( "@ LRM-20 (Artemis)" );
        add.SetStats( 1.0f, 60000.0f, 27.0f, 0.0f );
        add.SetDamage( 1, 5, 20 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'D', 'D', 2377, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ LRM-5 (Flare)", "CLLRM5 Ammo", 29, true, a );
        add.SetStats( 1.0f, 30000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'D', 'D', 2377, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ LRM-10 (Flare)", "CLLRM10 Ammo", 30, true, a );
        add.SetStats( 1.0f, 30000.0f, 14.0f, 0.0f );
        add.SetDamage( 1, 5, 10 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'D', 'D', 2377, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ LRM-15 (Flare)", "CLLRM15 Ammo", 31, true, a );
        add.SetStats( 1.0f, 30000.0f, 21.0f, 0.0f );
        add.SetDamage( 1, 5, 15 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'D', 'D', 2377, 0, 0, "FS", "", false, false );
        add = new Ammunition( "@ LRM-20 (Flare)", "CLLRM20 Ammo", 32, true, a );
        add.SetStats( 1.0f, 30000.0f, 27.0f, 0.0f );
        add.SetDamage( 1, 5, 20 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'D', 'X', 'D', 'C', 2385, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-5 (Fragmentation)", "CLLRM5 Ammo", 29, true, a );
        add.SetPrintName( "@ LRM-5 (Frag)" );
        add.SetStats( 1.0f, 60000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        CLAM.add( add );

        a = new AvailableCode( true, 'D', 'X', 'D', 'C', 2385, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-10 (Fragmentation)", "CLLRM10 Ammo", 30, true, a );
        add.SetPrintName( "@ LRM-10 (Frag)" );
        add.SetStats( 1.0f, 60000.0f, 14.0f, 0.0f );
        add.SetDamage( 1, 5, 10 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        CLAM.add( add );

        a = new AvailableCode( true, 'D', 'X', 'D', 'C', 2385, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-15 (Fragmentation)", "CLLRM15 Ammo", 31, true, a );
        add.SetPrintName( "@ LRM-15 (Frag)" );
        add.SetStats( 1.0f, 60000.0f, 21.0f, 0.0f );
        add.SetDamage( 1, 5, 15 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        CLAM.add( add );

        a = new AvailableCode( true, 'D', 'X', 'D', 'C', 2385, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-20 (Fragmentation)", "CLLRM20 Ammo", 32, true, a );
        add.SetPrintName( "@ LRM-20 (Frag)" );
        add.SetStats( 1.0f, 60000.0f, 27.0f, 0.0f );
        add.SetDamage( 1, 5, 20 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2621, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-5 (Swarm)", "CLLRM5 Ammo", 29, true, a );
        add.SetStats( 1.0f, 60000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2621, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-10 (Swarm)", "CLLRM10 Ammo", 30, true, a );
        add.SetStats( 1.0f, 60000.0f, 14.0f, 0.0f );
        add.SetDamage( 1, 5, 10 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2621, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-15 (Swarm)", "CLLRM15 Ammo", 31, true, a );
        add.SetStats( 1.0f, 60000.0f, 21.0f, 0.0f );
        add.SetDamage( 1, 5, 15 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2621, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-20 (Swarm)", "CLLRM20 Ammo", 32, true, a );
        add.SetStats( 1.0f, 60000.0f, 27.0f, 0.0f );
        add.SetDamage( 1, 5, 20 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2620, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-5 (Thunder)", "CLLRM5 Ammo", 29, true, a );
        add.SetStats( 1.0f, 60000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2620, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-10 (Thunder)", "CLLRM10 Ammo", 30, true, a );
        add.SetStats( 1.0f, 60000.0f, 14.0f, 0.0f );
        add.SetDamage( 1, 5, 10 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2620, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-15 (Thunder)", "CLLRM15 Ammo", 31, true, a );
        add.SetStats( 1.0f, 60000.0f, 21.0f, 0.0f );
        add.SetDamage( 1, 5, 15 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2620, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-20 (Thunder)", "CLLRM20 Ammo", 32, true, a );
        add.SetStats( 1.0f, 60000.0f, 27.0f, 0.0f );
        add.SetDamage( 1, 5, 20 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2597, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-5 (Narc Capable)", "CLLRM5 Ammo", 29, true, a );
        add.SetPrintName( "@ LRM-5 (Narc)" );
        add.SetStats( 1.0f, 60000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2597, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-10 (Narc Capable)", "CLLRM10 Ammo", 30, true, a );
        add.SetPrintName( "@ LRM-10 (Narc)" );
        add.SetStats( 1.0f, 60000.0f, 14.0f, 0.0f );
        add.SetDamage( 1, 5, 10 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2597, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-15 (Narc Capable)", "CLLRM15 Ammo", 31, true, a );
        add.SetPrintName( "@ LRM-15 (Narc)" );
        add.SetStats( 1.0f, 60000.0f, 21.0f, 0.0f );
        add.SetDamage( 1, 5, 15 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2597, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-20 (Narc Capable)", "CLLRM20 Ammo", 32, true, a );
        add.SetPrintName( "@ LRM-20 (Narc)" );
        add.SetStats( 1.0f, 60000.0f, 27.0f, 0.0f );
        add.SetDamage( 1, 5, 20 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-5 (Torpedo)", "CLLRM5 Ammo", 29, true, a );
        add.SetStats( 1.0f, 30000.0f, 7.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-10 (Torpedo)", "CLLRM10 Ammo", 30, true, a );
        add.SetStats( 1.0f, 30000.0f, 14.0f, 0.0f );
        add.SetDamage( 1, 5, 10 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-15 (Torpedo)", "CLLRM15 Ammo", 31, true, a );
        add.SetStats( 1.0f, 30000.0f, 21.0f, 0.0f );
        add.SetDamage( 1, 5, 15 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'B', 'B', 2380, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LRM-20 (Torpedo)", "CLLRM20 Ammo", 32, true, a );
        add.SetStats( 1.0f, 30000.0f, 27.0f, 0.0f );
        add.SetDamage( 1, 5, 20 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3057, 0, 0, "CCY", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Streak LRM-5", "CLStreakLRM5 Ammo", 119, true, a );
        add.SetStats( 1.0f, 60000.0f, 11.0f, 0.0f );
        add.SetDamage( 1, 5, 5 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3057, 0, 0, "CCY", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Streak LRM-10", "CLStreakLRM10 Ammo", 120, true, a );
        add.SetStats( 1.0f, 60000.0f, 22.0f, 0.0f );
        add.SetDamage( 1, 5, 10 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 12 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3057, 0, 0, "CCY", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Streak LRM-15", "CLStreakLRM15 Ammo", 121, true, a );
        add.SetStats( 1.0f, 60000.0f, 32.0f, 0.0f );
        add.SetDamage( 1, 5, 15 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 8 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3057, 0, 0, "CCY", "", false, false, 3055, true, "CC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Streak LRM-20", "CLStreakLRM20 Ammo", 122, true, a );
        add.SetStats( 1.0f, 60000.0f, 43.0f, 0.0f );
        add.SetDamage( 1, 5, 20 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2587, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ Narc (Homing)", "Clan Ammo Narc", 39, true, a );
        add.SetStats( 1.0f, 6000.0f, 0.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'C', 'D', 'D', 2300, 2850, 0, "TH", "", true, false );
        add = new Ammunition( "@ AC/2", "ISAC2 Ammo", 44, false, a );
        add.SetStats( 1.0f, 1000.0f, 5.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 4, 8, 16, 24 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'C', 'C', 'D', 2250, 2850, 0, "TH", "", true, false );
        add = new Ammunition( "@ AC/5", "ISAC5 Ammo", 45, false, a );
        add.SetStats( 1.0f, 4500.0f, 9.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 3, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'C', 'D', 'D', 2460, 2850, 0, "TH", "", true, false );
        add = new Ammunition( "@ AC/10", "ISAC10 Ammo", 46, false, a );
        add.SetStats( 1.0f, 6000.0f, 15.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'D', 'E', 'D', 2500, 2850, 0, "LC", "", true, false );
        add = new Ammunition( "@ AC/20", "ISAC20 Ammo", 47, false, a );
        add.SetStats( 1.0f, 10000.0f, 22.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2826, 0, 0, "CGS", "", false, false );
        add = new Ammunition( "@ LB 2-X AC (Slug)", "CLLBXAC2 Ammo", 54, true, a );
        add.SetPrintName( "@ LB 2-X (Slug)" );
        add.SetStats( 1.0f, 2000.0f, 6.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 4, 10, 20, 30 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2826, 0, 0, "CGS", "", false, false );
        add = new Ammunition( "@ LB 2-X AC (Cluster)", "CLLBXAC2 CL Ammo", 54, true, a );
        add.SetPrintName( "@ LB 2-X (Cluster)" );
        add.SetStats( 1.0f, 3300.0f, 6.0f, 0.0f );
        add.SetDamage( 1, 2, 1 );
        add.SetRange( 4, 10, 20, 30 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        add.SetToHit( -1, -1, -1 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2825, 0, 0, "CCY", "", false, false );
        add = new Ammunition( "@ LB 5-X AC (Slug)", "CLLBXAC5 Ammo", 55, true, a );
        add.SetPrintName( "@ LB 5-X (Slug)" );
        add.SetStats( 1.0f, 9000.0f, 12.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 3, 8, 15, 24 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2825, 0, 0, "CCY", "", false, false );
        add = new Ammunition( "@ LB 5-X AC (Cluster)", "CLLBXAC5 CL Ammo", 55, true, a );
        add.SetPrintName( "@ LB 5-X (Cluster)" );
        add.SetStats( 1.0f, 15000.0f, 12.0f, 0.0f );
        add.SetDamage( 1, 5, 1 );
        add.SetRange( 3, 8, 15, 24 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        add.SetToHit( -1, -1, -1 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2595, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LB 10-X AC (Slug)", "CLLBXAC10 Ammo", 56, true, a );
        add.SetPrintName( "@ LB 10-X (Slug)" );
        add.SetStats( 1.0f, 12000.0f, 19.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2595, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ LB 10-X AC (Cluster)", "CLLBXAC10 CL Ammo", 56, true, a );
        add.SetPrintName( "@ LB 10-X (Cluster)" );
        add.SetStats( 1.0f, 20000.0f, 19.0f, 0.0f );
        add.SetDamage( 1, 10, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        add.SetToHit( -1, -1, -1 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2826, 0, 0, "CHH", "", false, false );
        add = new Ammunition( "@ LB 20-X AC (Slug)", "CLLBXAC20 Ammo", 57, true, a );
        add.SetPrintName( "@ LB 20-X (Slug)" );
        add.SetStats( 1.0f, 20000.0f, 30.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2826, 0, 0, "CHH", "", false, false );
        add = new Ammunition( "@ LB 20-X AC (Cluster)", "CLLBXAC20 CL Ammo", 57, true, a );
        add.SetPrintName( "@ LB 20-X (Cluster)" );
        add.SetStats( 1.0f, 34000.0f, 30.0f, 0.0f );
        add.SetDamage( 1, 20, 1 );
        add.SetRange( 0, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        add.SetToHit( -1, -1, -1 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2827, 0, 0, "CGS", "", false, false );
        add = new Ammunition( "@ Ultra AC/2", "CLUltraAC2 Ammo", 66, true, a );
        add.SetStats( 1.0f, 1000.0f, 8.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 2, 9, 18, 27 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'C', 'C', 2640, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ Ultra AC/5", "CLUltraAC5 Ammo", 67, true, a );
        add.SetStats( 1.0f, 9000.0f, 15.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2825, 0, 0, "CDS", "", false, false );
        add = new Ammunition( "@ Ultra AC/10", "CLUltraAC10 Ammo", 68, true, a );
        add.SetStats( 1.0f, 12000.0f, 26.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 6, 12, 18 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2825, 0, 0, "CSV", "", false, false );
        add = new Ammunition( "@ Ultra AC/20", "CLUltraAC20 Ammo", 69, true, a );
        add.SetStats( 1.0f, 20000.0f, 42.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3073, 0, 0, "CDS", "", false, false, 3069, true, "CDS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Rotary AC/2", "CLRotaryAC2 Ammo", 109, true, a );
        add.SetStats( 1.0f, 3000.0f, 20.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 8, 17, 25 );
        add.SetExplosive( true );
        add.SetLotSize( 45 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3073, 0, 0, "CDS", "", false, false, 3069, true, "CDS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Rotary AC/5", "CLRotaryAC5 Ammo", 110, true, a );
        add.SetStats( 1.0f, 12000.0f, 43.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 7, 14, 21 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3073, 0, 0, "CBS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Protomech AC/2", "CLProtoAC2 Ammo", 123, true, a );
        add.SetStats( 1.0f, 1200.0f, 4.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 7, 14, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 40 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3073, 0, 0, "CBS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Protomech AC/4", "CLProtoAC4 Ammo", 124, true, a );
        add.SetStats( 1.0f, 4800.0f, 6.0f, 0.0f );
        add.SetDamage( 4, 1, 1 );
        add.SetRange( 0, 5, 10, 15 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3073, 0, 0, "CBS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Protomech AC/8", "CLProtoAC8 Ammo", 125, true, a );
        add.SetStats( 1.0f, 6300.0f, 8.0f, 0.0f );
        add.SetDamage( 8, 1, 1 );
        add.SetRange( 0, 3, 7, 10 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3069, 0, 0, "CJF", "", false, false );
        add = new Ammunition( "@ AP Gauss Rifle", "Clan AP Gauss Rifle Ammo", 73, true, a );
        add.SetStats( 1.0f, 3000.0f, 3.0f, 0.0f );
        add.SetDamage( 3, 1, 1 );
        add.SetRange( 0, 3, 6, 9 );
        add.SetExplosive( false );
        add.SetLotSize( 40 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'C', 'C', 2590, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ Gauss Rifle", "CLGauss Ammo", 74, true, a );
        add.SetStats( 1.0f, 20000.0f, 40.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 2, 7, 15, 22 );
        add.SetExplosive( false );
        add.SetLotSize( 8 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3068, 0, 0, "CHH", "", false, false );
        add = new Ammunition( "@ Hyper Assault Gauss 20", "CLHAG20 Ammo", 75, true, a );
        add.SetPrintName( "@ HAG-20" );
        add.SetStats( 1.0f, 30000.0f, 33.0f, 0.0f );
        add.SetDamage( 1, 5, 20 );
        add.SetRange( 2, 8, 16, 24 );
        add.SetExplosive( false );
        add.SetLotSize( 6 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3068, 0, 0, "CHH", "", false, false );
        add = new Ammunition( "@ Hyper Assault Gauss 30", "CLHAG30 Ammo", 76, true, a );
        add.SetPrintName( "@ HAG-30" );
        add.SetStats( 1.0f, 30000.0f, 50.0f, 0.0f );
        add.SetDamage( 1, 5, 30 );
        add.SetRange( 2, 8, 16, 24 );
        add.SetExplosive( false );
        add.SetLotSize( 4 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3069, 0, 0, "CHH", "", false, false );
        add = new Ammunition( "@ Hyper Assault Gauss 40", "CLHAG40 Ammo", 77, true, a );
        add.SetPrintName( "@ HAG-40" );
        add.SetStats( 1.0f, 30000.0f, 67.0f, 0.0f );
        add.SetDamage( 1, 5, 40 );
        add.SetRange( 2, 8, 16, 24 );
        add.SetExplosive( false );
        add.SetLotSize( 3 );
        CLAM.add( add );

        a = new AvailableCode( true, 'A', 'X', 'A', 'A', 1950, 0, 0, "PS", "", false, false );
        add = new Ammunition( "@ Machine Gun", "CLMG Ammo (200)", 49, true, a );
        add.SetPrintName( "@ MG" );
        add.SetStats( 1.0f, 1000.0f, 1.0f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 200 );
        CLAM.add( add );

        a = new AvailableCode( true, 'A', 'X', 'A', 'A', 1950, 0, 0, "PS", "", false, false );
        add = new Ammunition( "@ Machine Gun (1/2)", "CLMG Ammo (100)", 49, true, a );
        add.SetPrintName( "@ MG (1/2)" );
        add.SetStats( 0.5f, 500.0f, 0.5f, 0.0f );
        add.SetDamage( 2, 1, 1 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 100 );
        CLAM.add( add );

        a = new AvailableCode( true, 'A', 'X', 'X', 'B', 3060, 0, 0, "CSJ", "", false, false );
        add = new Ammunition( "@ Light Machine Gun", "CLLightMG Ammo (200)", 80, true, a );
        add.SetPrintName( "@ LMG" );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 1, 1, 1 );
        add.SetRange( 0, 2, 4, 6 );
        add.SetExplosive( true );
        add.SetLotSize( 200 );
        CLAM.add( add );

        a = new AvailableCode( true, 'A', 'X', 'X', 'B', 3060, 0, 0, "CSJ", "", false, false );
        add = new Ammunition( "@ Light Machine Gun (1/2)", "CLLightMG Ammo (100)", 80, true, a );
        add.SetPrintName( "@ LMG (1/2)" );
        add.SetStats( 0.5f, 250.0f, 0.5f, 0.0f );
        add.SetDamage( 1, 1, 1 );
        add.SetRange( 0, 2, 4, 6 );
        add.SetExplosive( true );
        add.SetLotSize( 100 );
        CLAM.add( add );

        a = new AvailableCode( true, 'A', 'X', 'X', 'B', 3059, 0, 0, "CSJ", "", false, false );
        add = new Ammunition( "@ Heavy Machine Gun", "CLHeavyMG Ammo (100)", 81, true, a );
        add.SetPrintName( "@ HMG" );
        add.SetStats( 1.0f, 1000.0f, 1.0f, 0.0f );
        add.SetDamage( 3, 1, 1 );
        add.SetRange( 0, 1, 2, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 100 );
        CLAM.add( add );

        a = new AvailableCode( true, 'A', 'X', 'X', 'B', 3059, 0, 0, "CSJ", "", false, false );
        add = new Ammunition( "@ Heavy Machine Gun (1/2)", "CLHeavyMG Ammo (50)", 81, true, a );
        add.SetPrintName( "@ HMG (1/2)" );
        add.SetStats( 0.5f, 500.0f, 0.5f, 0.0f );
        add.SetDamage( 3, 1, 1 );
        add.SetRange( 0, 1, 2, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 50 );
        CLAM.add( add );

        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2617, 0, 0, "TH", "", false, false );
        add = new Ammunition( "@ Anti-Missile System", "Clan AMS Ammo", 83, true, a );
        add.SetStats( 1.0f, 2000.0f, 0.0f, 22.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 0, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 24 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'D', 2850, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Non-Homing)", "CLArrowIVAmmo", 86, false, a );
        add.SetPrintName( "@ Arrow IV (Non-Home)" );
        add.SetStats( 1.0f, 10000.0f, 30.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'D', 2850, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Homing)", "CLArrowIVAmmo", 86, false, a );
        add.SetStats( 1.0f, 15000.0f, 30.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'D', 2850, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Cluster)", "CLArrowIVAmmo", 86, false, a );
        add.SetStats( 1.0f, 15000.0f, 30.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'C', 'C', 'C', 2621, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Illumination)", "CLArrowIVAmmo", 86, false, a );
        add.SetStats( 1.0f, 5000.0f, 30.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'D', 'D', 2600, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (Smoke)", "CLArrowIVAmmo", 86, false, a );
        add.SetStats( 1.0f, 5000.0f, 30.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'D', 'D', 2600, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Arrow IV (FASCAM)", "CLArrowIVAmmo", 86, false, a );
        add.SetStats( 1.0f, 15000.0f, 30.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 8, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'B', 'B', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper", "CLThumperAmmo", 89, false, a );
        add.SetStats( 1.0f, 4500.0f, 5.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Cluster)", "CLThumperAmmo", 89, false, a );
        add.SetStats( 1.0f, 6750.0f, 5.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'D', 2645, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Copperhead)", "CLThumperAmmo", 89, false, a );
        add.SetPrintName( "@ Thumper (Cprhead)" );
        add.SetStats( 1.0f, 6750.0f, 5.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'E', 'E', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Flechette)", "CLThumperAmmo", 89, false, a );
        add.SetStats( 1.0f, 6750.0f, 5.0f, 0.0f );
        add.SetDamage( 5, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'C', 'C', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Illumination)", "CLThumperAmmo", 89, false, a );
        add.SetPrintName( "@ Thumper (Illum)" );
        add.SetStats( 1.0f, 2250.0f, 5.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'A', 'A', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (Smoke)", "CLThumperAmmo", 89, false, a );
        add.SetStats( 1.0f, 2250.0f, 5.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'D', 'D', 2600, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Thumper (FASCAM)", "CLThumperAmmo", 89, false, a );
        add.SetStats( 1.0f, 6750.0f, 5.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 21, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'B', 'B', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper", "CLSniperAmmo", 90, false, a );
        add.SetStats( 1.0f, 6000.0f, 11.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Cluster)", "CLSniperAmmo", 90, false, a );
        add.SetStats( 1.0f, 9000.0f, 11.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'D', 'D', 2645, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Copperhead)", "CLSniperAmmo", 90, false, a );
        add.SetPrintName( "@ Sniper (Cprhead)" );
        add.SetStats( 1.0f, 9000.0f, 11.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'E', 'X', 'E', 'E', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Flechette)", "CLSniperAmmo", 90, false, a );
        add.SetStats( 1.0f, 9000.0f, 11.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'C', 'C', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Illumination)", "CLSniperAmmo", 90, false, a );
        add.SetPrintName( "@ Sniper (Illum)" );
        add.SetStats( 1.0f, 3000.0f, 11.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'A', 'A', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (Smoke)", "CLSniperAmmo", 90, false, a );
        add.SetStats( 1.0f, 3000.0f, 11.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'X', 'D', 'D', 2600, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Sniper (FASCAM)", "CLSniperAmmo", 90, false, a );
        add.SetStats( 1.0f, 9000.0f, 11.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 18, 0, 0 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );


        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon", "CLLongTomCannonAmmo", 131, false, a );
        add.SetStats( 1.0f, 20000.0f, 41.0f, 0.0f );
        add.SetDamage( 25, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Cluster)", "CLLongTomCannonAmmo", 131, false, a );
        add.SetStats( 1.0f, 30000.0f, 41.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Copperhead)", "CLLongTomCannonAmmo", 131, false, a );
        add.SetStats( 1.0f, 30000.0f, 41.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Flechette)", "CLLongTomCannonAmmo", 131, false, a );
        add.SetStats( 1.0f, 30000.0f, 41.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Illumination)", "CLLongTomCannonAmmo", 131, false, a );
        add.SetStats( 1.0f, 10000.0f, 41.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Smoke)", "CLLongTomCannonAmmo", 131, false, a );
        add.SetStats( 1.0f, 10000.0f, 41.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Long Tom Cannon (Thunder)", "CLLongTomCannonAmmo", 131, false, a );
        add.SetStats( 1.0f, 30000.0f, 41.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 4, 6, 13, 20 );
        add.SetExplosive( true );
        add.SetLotSize( 5 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon", "CLSniperCannonAmmo", 130, false, a );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Cluster)", "CLSniperCannonAmmo", 130, false, a );
        add.SetStats( 1.0f, 22500.0f, 10.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Copperhead)", "CLSniperCannonAmmo", 130, false, a );
        add.SetStats( 1.0f, 22500.0f, 10.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Flechette)", "CLSniperCannonAmmo", 130, false, a );
        add.SetStats( 1.0f, 22500.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Illumination)", "CLSniperCannonAmmo", 130, false, a );
        add.SetStats( 1.0f, 7500.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Smoke)", "CLSniperCannonAmmo", 130, false, a );
        add.SetStats( 1.0f, 7500.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Sniper Cannon (Thunder)", "CLSniperCannonAmmo", 130, false, a );
        add.SetStats( 1.0f, 22500.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 2, 4, 8, 12 );
        add.SetExplosive( true );
        add.SetLotSize( 10 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon", "CLThumperCannonAmmo", 129, false, a );
        add.SetStats( 1.0f, 10000.0f, 10.0f, 0.0f );
        add.SetDamage( 20, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Cluster)", "CLThumperCannonAmmo", 129, false, a );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 15, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Copperhead)", "CLThumperCannonAmmo", 129, false, a );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 10, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Flechette)", "CLThumperCannonAmmo", 129, false, a );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Illumination)", "CLThumperCannonAmmo", 129, false, a );
        add.SetStats( 1.0f, 5000.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Smoke)", "CLThumperCannonAmmo", 129, false, a );
        add.SetStats( 1.0f, 5000.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'X', 'F', 'E', 3032, 0, 0, "CW", "", false, false, 3030, true, "CW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        add = new Ammunition( "@ Thumper Cannon (Thunder)", "CLThumperCannonAmmo", 129, false, a );
        add.SetStats( 1.0f, 15000.0f, 10.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 3, 4, 9, 14 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode(true, 'C', 'X', 'C', 'C', 2310, 0, 0, "FWL", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        add = new Ammunition( "@ Nail/Rivet Gun", "CLNail/RivetGunAmmo", 132, false, a );
        add.SetStats( 1.0f, 300.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 1, 0, 0 );
        add.SetExplosive( false );
        add.SetLotSize( 300 );
        CLAM.add( add );

        a = new AvailableCode(true, 'C', 'X', 'C', 'C', 2310, 0, 0, "FWL", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT);
        add = new Ammunition( "@ Nail/Rivet Gun (1/2)", "CLNail/RivetGunHalfAmmo", 132, false, a );
        add.SetStats( 0.5f, 150.0f, 0.5f, 0.0f );
        add.SetDamage( 0, 1, 1 );
        add.SetRange( 0, 1, 0, 0 );
        add.SetExplosive( false );
        add.SetLotSize( 150 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'B', 'B', 'B', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Coolant", "CLFluidGunCoolantAmmo", 135, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'C', 'C', 'D', 'D', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Corrosive", "CLFluidGunCorrosiveAmmo", 135, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'B', 'B', 'B', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Flame Retardant Foam", "CLFluidGunFoamAmmo", 135, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'D', 'D', 'E', 'D', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Inferno Fuel", "CLFluidGunInfernoFuelAmmo", 135, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'B', 'B', 'B', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Oil Slick", "CLFluidGunOilSlickAmmo", 135, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'B', 'B', 'B', 'B', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Paint/Obscurant", "CLFluidGunPaintAmmo", 135, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( true );
        add.SetLotSize( 20 );
        CLAM.add( add );

        a = new AvailableCode( true, 'A', 'A', 'A', 'A', 2750, 0, 0, "PS", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        add = new Ammunition( "@ Fluid Gun Water", "CLFluidGunWaterAmmo", 135, false, a );
        add.SetStats( 1.0f, 500.0f, 1.0f, 0.0f );
        add.SetDamage( 0, 0, 0 );
        add.SetRange( 0, 1, 2, 3 );
        add.SetExplosive( false );
        add.SetLotSize( 20 );
        CLAM.add( add );
    }
}

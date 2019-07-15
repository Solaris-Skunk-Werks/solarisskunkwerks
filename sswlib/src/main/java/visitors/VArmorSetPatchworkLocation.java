/*
Copyright (c) 2010, Justin R. Bengtson (poopshotgun@yahoo.com)
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

package visitors;

import components.*;
import java.util.ArrayList;

public class VArmorSetPatchworkLocation implements ifVisitor {
    // sets the mech's armor to industrial
    private Mech CurMech;
    private LocationIndex[] Locs = null;
    private String Type = null;
    private int Location = -1;
    private boolean Clan = false;

    public void SetClan( boolean clan ) {
        Clan = clan;
    }

    public void SetLocation( int i ) {
        Location = i;
    }

    public void LoadLocations( LocationIndex[] locs ) {
        Locs = locs;
    }

    public void SetPatchworkType( String type ) {
        Type = type;
    }

    public void Visit( Mech m ) throws Exception {
        // only the armor changes, so pass us off
        CurMech = m;
        ifMechLoadout l = CurMech.GetLoadout();
        MechArmor a = CurMech.GetArmor();

        // remove old armor from this location, if needed.
        ArrayList v = l.FindIndexes( a );
        LocationIndex[] OldLocs = new LocationIndex[v.size()];
        for( int i = 0; i < v.size(); i++ ) {
            OldLocs[i] = (LocationIndex) v.get( i );
        }
        OldLocs = this.GetLocations( Location, OldLocs );
        if( OldLocs != null ) {
            RemoveByLocation( l, OldLocs );
        }

        // Setting the patchwork type will automatically add it in.
        if( Type == null ) {
            a.SetIndustrial( Location );
        } else {
            if( Locs == null ) {
                SetType( a, Type, Location );
            } else {
                LocationIndex[] foundlocs = GetLocations( Location, Locs );
                SetType( a, Type, Location, foundlocs );
            }
        }

        if( a.GetMechModifier() != null ) {
            CurMech.AddMechModifier( a.GetMechModifier() );
        }
    }

    public void Visit( CombatVehicle v ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( Infantry i ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( SupportVehicle s ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( BattleArmor b ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( Fighter f ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( Spaceship s ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( SpaceStation s ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( ProtoMech p ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( MobileStructure m ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( LargeSupportVehicle l ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( Dropship d ) throws Exception {
        // does nothing at the moment
    }

    private void SetType( MechArmor a, String Type, int loc ) throws Exception {
        if( Type.equals( "Ferro-Fibrous" ) ) {
            if( Clan ) {
                a.SetCLFF( loc );
            } else {
                a.SetISFF( loc );
            }
        } else if( Type.equals( "(CL) Ferro-Fibrous" ) ) {
            a.SetCLFF( loc );
        } else if( Type.equals( "(IS) Ferro-Fibrous" ) ) {
            a.SetISFF( loc );
        } else if( Type.equals( "Stealth Armor" ) ) {
            a.SetISST( loc );
        } else if( Type.equals( "Laser-Reflective" ) ) {
            if( Clan ) {
                a.SetCLLR( loc );
            } else {
                a.SetISLR( loc );
            }
        } else if( Type.equals( "(CL) Laser-Reflective" ) ) {
            a.SetCLLR( loc );
        } else if( Type.equals( "(IS) Laser-Reflective" ) ) {
            a.SetISLR( loc );
        } else if( Type.equals( "Standard Armor" ) ) {
            a.SetStandard( loc );
        } else if( Type.equals( "Light Ferro-Fibrous" ) ) {
            a.SetISLF( loc );
        } else if( Type.equals( "Heavy Ferro-Fibrous" ) ) {
            a.SetISHF( loc );
        } else if( Type.equals( "Ferro-Lamellor" ) ) {
            a.SetCLFL( loc );
        } else if( Type.equals( "Hardened Armor" ) ) {
            a.SetHardened( loc );
        } else if( Type.equals( "Industrial Armor" ) ) {
            a.SetIndustrial( loc );
        } else if( Type.equals( "Commercial Armor" ) ) {
            a.SetCommercial( loc );
        } else if( Type.equals( "Reactive Armor" ) ) {
            if( Clan ) {
                a.SetCLRE( loc );
            } else {
                a.SetISRE( loc );
            }
        } else if( Type.equals( "(IS) Reactive Armor" ) ) {
            a.SetISRE( loc );
        } else if( Type.equals( "(CL) Reactive Armor" ) ) {
            a.SetCLRE( loc );
        }
    }

    private void SetType( MechArmor a, String Type, int loc, LocationIndex[] locs ) throws Exception {
        if( Type.equals( "Ferro-Fibrous" ) ) {
            if( Clan ) {
                a.SetCLFF( loc, locs );
            } else {
                a.SetISFF( loc, locs );
            }
        } else if( Type.equals( "(CL) Ferro-Fibrous" ) ) {
            a.SetCLFF( loc, locs );
        } else if( Type.equals( "(IS) Ferro-Fibrous" ) ) {
            a.SetISFF( loc, locs );
        } else if( Type.equals( "Stealth Armor" ) ) {
            a.SetISST( loc, locs );
        } else if( Type.equals( "Laser-Reflective" ) ) {
            if( Clan ) {
                a.SetCLLR( loc, locs );
            } else {
                a.SetISLR( loc, locs );
            }
        } else if( Type.equals( "(CL) Laser-Reflective" ) ) {
            a.SetCLLR( loc, locs );
        } else if( Type.equals( "(IS) Laser-Reflective" ) ) {
            a.SetISLR( loc, locs );
        } else if( Type.equals( "Standard Armor" ) ) {
            a.SetStandard( loc );
        } else if( Type.equals( "Light Ferro-Fibrous" ) ) {
            a.SetISLF( loc, locs );
        } else if( Type.equals( "Heavy Ferro-Fibrous" ) ) {
            a.SetISHF( loc, locs );
        } else if( Type.equals( "Ferro-Lamellor" ) ) {
            a.SetCLFL( loc, locs );
        } else if( Type.equals( "Hardened Armor" ) ) {
            a.SetHardened( loc );
        } else if( Type.equals( "Industrial Armor" ) ) {
            a.SetIndustrial( loc );
        } else if( Type.equals( "Commercial Armor" ) ) {
            a.SetCommercial( loc );
        } else if( Type.equals( "Reactive Armor" ) ) {
            if( Clan ) {
                a.SetCLRE( loc, locs );
            } else {
                a.SetISRE( loc, locs );
            }
        } else if( Type.equals( "(IS) Reactive Armor" ) ) {
            a.SetISRE( loc, locs );
        } else if( Type.equals( "(CL) Reactive Armor" ) ) {
            a.SetCLRE( loc, locs );
        }
    }

    private LocationIndex[] GetLocations( int loc, LocationIndex[] locs ) {
        LocationIndex[] retval = null;
        ArrayList<LocationIndex> Found = new ArrayList<LocationIndex>();
        for( int i = 0; i < locs.length; i++ ) {
            if( locs[i].Location == loc ) {
                Found.add( locs[i] );
            }
        }
        if( Found.size() > 0 ) {
            retval = new LocationIndex[Found.size()];
            for( int i = 0; i < Found.size(); i++ ) {
                retval[i] = Found.get( i );
            }
        }
        return retval;
    }

    private void RemoveByLocation( ifMechLoadout l, LocationIndex[] Loc ) {
        if( Loc == null ) { return; }
        if( Loc.length <= 0 ) { return; }
        abPlaceable[] a = l.GetCrits( Loc[0].Location );
        for( int i = 0; i < Loc.length; i++ ) {
            l.UnallocateByIndex( Loc[i].Index, a );
        }
    }
}

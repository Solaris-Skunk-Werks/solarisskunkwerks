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

package visitors;

import common.CommonTools;
import components.*;
import java.util.Vector;

public class VArmorSetPatchwork implements ifVisitor {
    // sets the mech's armor to industrial
    private Mech CurMech;
    private LocationIndex[] Locs = null;
    private String[] Types = null;
    private boolean Clan = false;

    public void SetClan( boolean clan ) {
        Clan = clan;
    }

    public void LoadLocations( LocationIndex[] locs ) {
        // does nothing here, but may later.
        Locs = locs;
    }

    public void SetPatchworkTypes( String[] types ) {
        Types = types;
    }

    public void Visit( Mech m ) throws Exception {
        // only the armor changes, so pass us off
        CurMech = m;
        ifMechLoadout l = CurMech.GetLoadout();
        MechArmor a = CurMech.GetArmor();

        // remove the old armor, if needed
        l.Remove( a );

        a.SetPatchwork();
        // set the patchwork types, if any
        if( Types == null ) {
        } else {
            for( int i = 0; i < 8; i++ ) {
                SetType( a, Types[i], i );
            }
        }

        // place the armor
        if( Types == null ) {
            if( CommonTools.IsAllowed( a.GetStandardAC(), m ) ) {
                a.SetStandard( LocationIndex.MECH_LOC_HD );
                a.SetStandard( LocationIndex.MECH_LOC_CT );
                a.SetStandard( LocationIndex.MECH_LOC_LT );
                a.SetStandard( LocationIndex.MECH_LOC_RT );
                a.SetStandard( LocationIndex.MECH_LOC_LA );
                a.SetStandard( LocationIndex.MECH_LOC_RA );
                a.SetStandard( LocationIndex.MECH_LOC_LL );
                a.SetStandard( LocationIndex.MECH_LOC_RL );
            } else {
                a.SetIndustrial( LocationIndex.MECH_LOC_HD );
                a.SetIndustrial( LocationIndex.MECH_LOC_CT );
                a.SetIndustrial( LocationIndex.MECH_LOC_LT );
                a.SetIndustrial( LocationIndex.MECH_LOC_RT );
                a.SetIndustrial( LocationIndex.MECH_LOC_LA );
                a.SetIndustrial( LocationIndex.MECH_LOC_RA );
                a.SetIndustrial( LocationIndex.MECH_LOC_LL );
                a.SetIndustrial( LocationIndex.MECH_LOC_RL );
            }
        } else {
            if( Locs == null ) {
                SetType( a, Types[LocationIndex.MECH_LOC_HD], LocationIndex.MECH_LOC_HD );
                SetType( a, Types[LocationIndex.MECH_LOC_CT], LocationIndex.MECH_LOC_CT );
                SetType( a, Types[LocationIndex.MECH_LOC_LT], LocationIndex.MECH_LOC_LT );
                SetType( a, Types[LocationIndex.MECH_LOC_RT], LocationIndex.MECH_LOC_RT );
                SetType( a, Types[LocationIndex.MECH_LOC_LA], LocationIndex.MECH_LOC_LA );
                SetType( a, Types[LocationIndex.MECH_LOC_RA], LocationIndex.MECH_LOC_RA );
                SetType( a, Types[LocationIndex.MECH_LOC_LL], LocationIndex.MECH_LOC_LL );
                SetType( a, Types[LocationIndex.MECH_LOC_RL], LocationIndex.MECH_LOC_RL );
            } else {
                LocationIndex[] foundlocs = GetLocations( LocationIndex.MECH_LOC_HD, Locs );
                SetType( a, Types[LocationIndex.MECH_LOC_HD], LocationIndex.MECH_LOC_HD, foundlocs );
                foundlocs = GetLocations( LocationIndex.MECH_LOC_CT, Locs );
                SetType( a, Types[LocationIndex.MECH_LOC_CT], LocationIndex.MECH_LOC_CT, foundlocs );
                foundlocs = GetLocations( LocationIndex.MECH_LOC_LT, Locs );
                SetType( a, Types[LocationIndex.MECH_LOC_LT], LocationIndex.MECH_LOC_LT, foundlocs );
                foundlocs = GetLocations( LocationIndex.MECH_LOC_RT, Locs );
                SetType( a, Types[LocationIndex.MECH_LOC_RT], LocationIndex.MECH_LOC_RT, foundlocs );
                foundlocs = GetLocations( LocationIndex.MECH_LOC_LA, Locs );
                SetType( a, Types[LocationIndex.MECH_LOC_LA], LocationIndex.MECH_LOC_LA, foundlocs );
                foundlocs = GetLocations( LocationIndex.MECH_LOC_RA, Locs );
                SetType( a, Types[LocationIndex.MECH_LOC_RA], LocationIndex.MECH_LOC_RA, foundlocs );
                foundlocs = GetLocations( LocationIndex.MECH_LOC_LL, Locs );
                SetType( a, Types[LocationIndex.MECH_LOC_LL], LocationIndex.MECH_LOC_LL, foundlocs );
                foundlocs = GetLocations( LocationIndex.MECH_LOC_RL, Locs );
                SetType( a, Types[LocationIndex.MECH_LOC_RL], LocationIndex.MECH_LOC_RL, foundlocs );
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
        Vector<LocationIndex> Found = new Vector<LocationIndex>();
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
}

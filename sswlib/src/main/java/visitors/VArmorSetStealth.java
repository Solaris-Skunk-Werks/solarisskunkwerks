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

import components.*;

public class VArmorSetStealth implements ifVisitor {
    // sets the mech's armor to stealth
    private Mech CurMech;
    private LocationIndex[] Locs = null;

    public VArmorSetStealth() {
    }

    public void LoadLocations( LocationIndex[] locs ) {
        // loads up any location indexes needed to place this.  mainly for use
        // when loading a 'Mech up.
        Locs = locs;
    }

    public void SetClan( boolean clan ) {
    }

    public void Visit(Mech m) throws Exception {
        // only the armor changes, so pass us off
        CurMech = m;
        ifMechLoadout l = CurMech.GetLoadout();
        MechArmor a = CurMech.GetArmor();

        // remove the old armor, if needed
        l.Remove( a );
        a.ResetPatchworkConfigs();

        a.SetISST();

        if( Locs == null ) {
            // place the armor
            if( ! a.Place( l ) ) {
                // not enough free space.  tell the user
                throw new Exception( "There is no available room for Stealth Armor!" );
            }
        } else {
            // use the location index array given to allocate the armor
            if( ! a.Place( l, Locs ) ) {
                // not enough free space.  tell the user
                throw new Exception( "There is no available room for Stealth Armor!" );
            }
        }
        if( a.GetMechModifier() != null ) {
            CurMech.AddMechModifier( a.GetMechModifier() );
        }
        // reset the locations just in case.  Any time this visitor is used we
        // should load up a new set of locations.
        Locs = null;
    }

    public void Visit( CombatVehicle v ) throws Exception {
        // does nothing
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
}

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

public class VEngineSetFusionXL implements ifVisitor {
    private Mech CurMech;
    LocationIndex[] Locs = null;
    private boolean Clan = false;

    public void SetClan( boolean clan ) {
        Clan = clan;
    }

    public void LoadLocations(LocationIndex[] locs) {
        Locs = locs;
    }

    public void Visit(Mech m) throws Exception {
        // only the engine changes here, so pass us off to the engine
        CurMech = m;
        ifMechLoadout l = CurMech.GetLoadout();
        Engine e = CurMech.GetEngine();
        boolean SChargerInstalled = false;
        int SChargerLoc = -1;

        // see if we have a supercharger installed
        if( l.HasSupercharger() ) {
            SChargerInstalled = true;
            SChargerLoc = l.Find( l.GetSupercharger() );
            try {
                CurMech.GetLoadout().SetSupercharger( false, -1, -1 );
            } catch ( Exception ex ) {
                // wow, a problem removing it.  Log it for later.
                System.err.println( ex.getMessage() );
            }
        }

        // remove the engine
        e.Remove(l);

        // change the engine type
        switch( CurMech.GetBaseTechbase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                e.SetISXLEngine();
                break;
            case AvailableCode.TECH_CLAN:
                e.SetCLXLEngine();
                break;
            case AvailableCode.TECH_BOTH:
                if( Clan ) {
                    e.SetCLXLEngine();
                } else {
                    e.SetISXLEngine();
                }
                break;
        }
        m.SetWalkMP( m.GetWalkingMP() );

        // place the engine
        if( Locs == null ) {
            if( ! e.Place( l ) ) {
                throw new Exception( "Fusion XL engine cannot be allocated!" );
            }
        } else {
            if( ! e.Place( l, Locs ) ) {
                throw new Exception( "Fusion XL engine cannot be allocated!" );
            }
        }

        // flush illegal equipment
        m.GetLoadout().FlushIllegal();

        // try to reinstall the Supercharger
        if( SChargerInstalled ) {
            try {
                // we're not interested in where the suypercharger was since it
                // can only go in the same spot as an engine.
                CurMech.GetLoadout().SetSupercharger( true, LocationIndex.MECH_LOC_CT, SChargerLoc );
            } catch ( Exception ex ) {
                System.err.println( ex.getMessage() );
            }
        }
    }

    public void Visit( CombatVehicle v ) throws Exception {
        Engine e = v.GetEngine();

        // change the engine type
        switch( v.GetBaseTechbase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                e.SetISXLEngine();
                break;
            case AvailableCode.TECH_CLAN:
                e.SetCLXLEngine();
                break;
            case AvailableCode.TECH_BOTH:
                if( Clan ) {
                    e.SetCLXLEngine();
                } else {
                    e.SetISXLEngine();
                }
                break;
        }
        v.SetEngine(e);
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

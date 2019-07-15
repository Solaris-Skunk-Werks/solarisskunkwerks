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
import java.util.ArrayList;

public class VFCSApolloLoader implements ifVisitor {
    // this is normally set to true since we simply unallocate items that can't
    // fit their artemis.  For Omnimechs with locked missile launchers it's
    // another matter entirely.
    boolean result = true;

    public void SetClan( boolean clan ) {
    }

    public void LoadLocations( LocationIndex[] locs ) {
        // does nothing here, but may later.
    }

    public void Visit( Mech m ) {
        ArrayList test = m.GetLoadout().GetNonCore();
        abPlaceable p;
        abPlaceable FCS;
        for( int i = 0; i < test.size(); i++ ) {
            p = (abPlaceable) test.get( i );
            if( p instanceof RangedWeapon ) {
                RangedWeapon MW = (RangedWeapon) p;
                if( MW.GetFCSType() == ifMissileGuidance.FCS_Apollo ) {
                    if( m.UsingApollo() ) {
                        if( MW.IsUsingFCS() ) {
                            if( m.IsOmnimech() ) {
                                if( MW.LocationLocked() ) {
                                    result = false;
                                    return;
                                } else {
                                    if( ! ( MW.GetFCS() instanceof ApolloFCS ) ) {
                                        SwitchSystem( MW, m );
                                    }
                                }
                            } else {
                                if( ! ( MW.GetFCS() instanceof ApolloFCS ) ) {
                                    SwitchSystem( MW, m );
                                }
                            }
                        } else {
                            MW.UseFCS( true, ifMissileGuidance.FCS_Apollo );
                            FCS = (abPlaceable) MW.GetFCS();
                            LocationIndex index = m.GetLoadout().FindIndex( MW );
                            if( index.Location >= 0 ) {
                                try {
                                    m.GetLoadout().AddTo( FCS, index.Location, index.Index + MW.NumCrits() );
                                } catch ( Exception e ) {
                                    m.GetLoadout().UnallocateAll( MW, false );
                                }
                            }
                        }
                    } else {
                        if( MW.GetFCS() instanceof ApolloFCS ) {
                            if( m.IsOmnimech() ) {
                                if( MW.LocationLocked() ) {
                                    result = false;
                                    return;
                                } else {
                                    FCS = (abPlaceable) MW.GetFCS();
                                    m.GetLoadout().UnallocateAll( FCS, true );
                                    MW.UseFCS( false, ifMissileGuidance.FCS_Apollo );
                                }
                            } else {
                                FCS = (abPlaceable) MW.GetFCS();
                                m.GetLoadout().UnallocateAll( FCS, true );
                                MW.UseFCS( false, ifMissileGuidance.FCS_Apollo );
                            }
                        }
                    }
                }
            }
        }

        result = true;
    }

    public boolean GetResult() {
        return result;
    }

    private void SwitchSystem( RangedWeapon MW, Mech m ) {
        abPlaceable FCS = (abPlaceable) MW.GetFCS();
        m.GetLoadout().UnallocateAll( FCS, true );
        MW.UseFCS( false, ifMissileGuidance.FCS_Apollo );
        MW.UseFCS( true, ifMissileGuidance.FCS_Apollo );
        FCS = (abPlaceable) MW.GetFCS();
        LocationIndex index = m.GetLoadout().FindIndex( MW );
        if( index.Location >= 0 ) {
            try {
                m.GetLoadout().AddTo( FCS, index.Location, index.Index + MW.NumCrits() );
            } catch ( Exception e ) {
                m.GetLoadout().UnallocateAll( MW, false );
            }
        }
    }

    public void Visit( CombatVehicle v ) throws Exception {
        ArrayList test = v.GetLoadout().GetNonCore();
        abPlaceable p;
        
        for( int i = 0; i < test.size(); i++ ) {
            p = (abPlaceable) test.get( i );
            //Is it a Ranged Weapon
            if( p instanceof RangedWeapon ) {
                RangedWeapon MW = (RangedWeapon) p;
                
                //if the weapon cannot do FCS let's move on quickly
                if ( !MW.IsFCSCapable() ) continue;
                
                //if the location is locked...stop here!
                if( v.IsOmni() && MW.LocationLocked() ) {
                    result = false;
                    return;
                }
                MW.UseFCS(v.UsingApollo(), ifMissileGuidance.FCS_ArtemisIV);
            }
        }
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

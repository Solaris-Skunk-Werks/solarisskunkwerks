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

package ssw.visitors;

import java.util.Vector;
import ssw.components.*;

public class VFCSArtemisIVLoader implements ifVisitor {
    // this is normally set to true since we simply unallocate items that can't
    // fit their artemis.  For Omnimechs with locked missile launchers it's
    // another matter entirely.
    boolean result = true;

    public void LoadLocations(LocationIndex[] locs) {
        // does nothing here, but may later.
    }

    public void Visit( Mech m ) {
        Vector test = m.GetLoadout().GetNonCore();
        abPlaceable p;
        abPlaceable FCS;
        for( int i = 0; i < test.size(); i++ ) {
            p = (abPlaceable) test.get( i );
            if( p instanceof MissileWeapon ) {
                MissileWeapon MW = (MissileWeapon) p;
                if( MW.GetFCSType() == ifMissileGuidance.FCS_ArtemisIV || MW.GetFCSType() == ifMissileGuidance.FCS_ArtemisV ) {
                    if( m.UsingArtemisIV() ) {
                        if( MW.IsUsingFCS() ) {
                            if( m.IsOmnimech() ) {
                                if( MW.LocationLocked() ) {
                                    result = false;
                                    return;
                                } else {
                                    if( ! ( MW.GetFCS() instanceof ArtemisIVFCS ) ) {
                                        SwitchSystem( MW, m );
                                    }
                                }
                            } else {
                                if( ! ( MW.GetFCS() instanceof ArtemisIVFCS ) ) {
                                    SwitchSystem( MW, m );
                                }
                            }
                        } else {
                            MW.UseFCS( true, ifMissileGuidance.FCS_ArtemisIV );
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
                        if( MW.GetFCS() instanceof ArtemisIVFCS ) {
                            if( m.IsOmnimech() ) {
                                if( MW.LocationLocked() ) {
                                    result = false;
                                    return;
                                } else {
                                    FCS = (abPlaceable) MW.GetFCS();
                                    m.GetLoadout().UnallocateAll( FCS, true );
                                    MW.UseFCS( false, ifMissileGuidance.FCS_ArtemisIV );
                                }
                            } else {
                                FCS = (abPlaceable) MW.GetFCS();
                                m.GetLoadout().UnallocateAll( FCS, true );
                                MW.UseFCS( false, ifMissileGuidance.FCS_ArtemisIV );
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

    private void SwitchSystem( MissileWeapon MW, Mech m ) {
        abPlaceable FCS = (abPlaceable) MW.GetFCS();
        m.GetLoadout().UnallocateAll( FCS, true );
        MW.UseFCS( false, ifMissileGuidance.FCS_ArtemisIV );
        MW.UseFCS( true, ifMissileGuidance.FCS_ArtemisIV );
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
}

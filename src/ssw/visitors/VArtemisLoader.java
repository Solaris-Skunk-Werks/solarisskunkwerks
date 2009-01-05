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
import ssw.*;
import ssw.components.*;

public class VArtemisLoader implements ifVisitor {
    // this is normally set to true since we simply unallocate items that can't
    // fit their artemis.  For Omnimechs with locked missile launchers it's
    // another matter entirely.
    boolean result = true;

    public void LoadLocations(LocationIndex[] locs) {
        // does nothing here, but may later.
    }

    public void Visit(Mech m) {
        // Get the loadout
        ifLoadout l = m.GetLoadout();
        Vector test = l.GetNonCore();
        abPlaceable p;

        // check the non-core list for missile weapons that fit the bill
        for( int i = 0; i < test.size(); i++ ) {
            p = (abPlaceable) test.get( i );
            if( p instanceof MissileWeapon ) {
                MissileWeapon MW = (MissileWeapon) p;
                switch( MW.GetArtemisType() ) {
                case Constants.ART4_NONE:
                    break;
                case Constants.ART4_SRM:
                    if( m.UsingA4SRM() ) {
                        // ensure we're not already using artemis
                        if( ! MW.IsUsingArtemis() ) {
                            // is the launcher already allocated?
                            if( l.IsAllocated( MW ) ) {
                                //  get the artemis and allocate it?
                                int Loc = l.Find( MW );
                                abPlaceable[] crits = GetLoc( Loc, l );
                                int Art_Loc = l.FirstFree( crits );
                                if( Art_Loc == crits.length ) {
                                    if( MW.LocationLocked() ) {
                                        // tell whatever called us that we can't do it.
                                        result = false;
                                        return;
                                    } else {
                                        // can't allocate?  remove the launcher to the queue
                                        l.UnallocateAll( MW, false );
                                    }
                                } else {
                                    // switch on artemis for the launcher
                                    MW.UseArtemis( true );

                                    // manually allocate the artemis
                                    crits[Art_Loc] = MW.GetArtemis();
                                }
                            } else {
                                MW.UseArtemis( true );
                            }
                        }
                    } else {
                        if( MW.IsUsingArtemis() ) {
                            // is the launcher locked in position?
                            if( l.GetBaseLoadout().UsingA4SRM() ) {
                                if( MW.LocationLocked() ) {
                                    result = false;
                                    return;
                                }
                            }

                            // are we already allocated?
                            if( l.IsAllocated( MW ) ) {
                                // if we're already using Artemis, remove it.
                                l.UnallocateAll( MW.GetArtemis(), true );
                            }
                            // turn off A4 usage
                            MW.UseArtemis( false );
                        }
                    }
                    break;
                case Constants.ART4_LRM:
                    if( m.UsingA4LRM() ) {
                        // ensure we're not already using artemis
                        if( ! MW.IsUsingArtemis() ) {
                            // is the launcher already allocated?
                            if( l.IsAllocated( MW ) ) {
                                //  get the artemis and allocate it?
                                int Loc = l.Find( MW );
                                abPlaceable[] crits = GetLoc( Loc, l );
                                int Art_Loc = l.FirstFree( crits );
                                if( Art_Loc == crits.length ) {
                                    if( MW.LocationLocked() ) {
                                        // tell whatever called us that we can't do it.
                                        result = false;
                                        return;
                                    } else {
                                        // can't allocate?  remove the launcher to the queue
                                        l.UnallocateAll( MW, false );
                                    }
                                } else {
                                    // switch on artemis for the launcher
                                    MW.UseArtemis( true );

                                    // manually allocate the artemis
                                    crits[Art_Loc] = MW.GetArtemis();
                                }
                            } else {
                                MW.UseArtemis( true );
                            }
                        }
                    } else {
                        if( MW.IsUsingArtemis() ) {
                            // is the launcher locked in position?
                            if( l.GetBaseLoadout().UsingA4LRM() ) {
                                if( MW.LocationLocked() ) {
                                    result = false;
                                    return;
                                }
                            }

                            // are we already allocated?
                            if( l.IsAllocated( MW ) ) {
                                // if we're already using Artemis, remove it.
                                l.UnallocateAll( MW.GetArtemis(), true );
                            }
                            // turn off A4 usage
                            MW.UseArtemis( false );
                        }
                    }
                    break;
                case Constants.ART4_MML:
                    if( m.UsingA4MML() ) {
                        // ensure we're not already using artemis
                        if( ! MW.IsUsingArtemis() ) {
                            // is the launcher already allocated?
                            if( l.IsAllocated( MW ) ) {
                                //  get the artemis and allocate it?
                                int Loc = l.Find( MW );
                                abPlaceable[] crits = GetLoc( Loc, l );
                                int Art_Loc = l.FirstFree( crits );
                                if( Art_Loc == crits.length ) {
                                    if( MW.LocationLocked() ) {
                                        // tell whatever called us that we can't do it.
                                        result = false;
                                        return;
                                    } else {
                                        // can't allocate?  remove the launcher to the queue
                                        l.UnallocateAll( MW, false );
                                    }
                                } else {
                                    // switch on artemis for the launcher
                                    MW.UseArtemis( true );

                                    // manually allocate the artemis
                                    crits[Art_Loc] = MW.GetArtemis();
                                }
                            } else {
                                MW.UseArtemis( true );
                            }
                        }
                    } else {
                        if( MW.IsUsingArtemis() ) {
                            // is the launcher locked in position?
                            if( l.GetBaseLoadout().UsingA4MML() ) {
                                if( MW.LocationLocked() ) {
                                    result = false;
                                    return;
                                }
                            }

                            // are we already allocated?
                            if( l.IsAllocated( MW ) ) {
                                // if we're already using Artemis, remove it.
                                l.UnallocateAll( MW.GetArtemis(), true );
                            }
                            // turn off A4 usage
                            MW.UseArtemis( false );
                        }
                    }
                    break;
                }
            }
        }
    }

    private abPlaceable[] GetLoc( int loc, ifLoadout l ) {
        switch( loc ) {
        case Constants.LOC_HD:
            return l.GetHDCrits();
        case Constants.LOC_CT:
            return l.GetCTCrits();
        case Constants.LOC_LT:
            return l.GetLTCrits();
        case Constants.LOC_RT:
            return l.GetRTCrits();
        case Constants.LOC_LA:
            return l.GetLACrits();
        case Constants.LOC_RA:
            return l.GetRACrits();
        case Constants.LOC_LL:
            return l.GetLLCrits();
        case Constants.LOC_RL:
            return l.GetRLCrits();
        default:
            return null;
        }
    }

    public boolean GetResult() {
        return result;
    }
}

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

package ssw.gui;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import components.*;

public class thLTTransferHandler extends TransferHandler {
    private ifMechForm Parent;
    private Mech CurMech;

    // the right leg transfer handler only deals with adding to the right leg.
    // other listeners will deal with removing items.
    public thLTTransferHandler( ifMechForm f, Mech m ) {
        Parent = f;
        CurMech = m;
    }

    @Override
    public Transferable createTransferable( JComponent comp ) {
        // all we want to do is transfer the index in the queue
        LocationDragDatagram d = new LocationDragDatagram();
        d.Location = LocationIndex.MECH_LOC_LT;
        d.SourceIndex = ((JList) comp).getSelectedIndex();
        d.Locked = CurMech.GetLoadout().GetLTCrits()[d.SourceIndex].LocationLocked();
        if( CurMech.GetLoadout().GetLTCrits()[d.SourceIndex] instanceof EmptyItem ) {
            d.Empty = true;
        }
        return d;
    }

    @Override
    public boolean canImport( TransferHandler.TransferSupport info ) {
        if( ! info.isDrop() ) {
            // can only drop onto this type of component
            return false;
        }

        if( ! info.isDataFlavorSupported( new DataFlavor( LocationDragDatagram.class, "Location Drag Datagram" ) ) ) {
            // not what we're looking for
            return false;
        }

        LocationDragDatagram DropItem = null;
        try {
            DropItem = (LocationDragDatagram) info.getTransferable().getTransferData( new DataFlavor( LocationDragDatagram.class, "Location Drag Datagram" ) );
        } catch ( Exception e ) {
            return false;
        }

        if( DropItem.Locked ) {
            abPlaceable a = CurMech.GetLoadout().GetCrits( DropItem.Location )[DropItem.SourceIndex];
            if( a instanceof CASE || a instanceof CASEII || a instanceof MultiSlotSystem || a instanceof Supercharger || a instanceof Engine || a instanceof SimplePlaceable || a instanceof PartialWing || a instanceof MechArmor || a instanceof MechTurret ) {
                if( DropItem.Location != LocationIndex.MECH_LOC_LT ) {
                    return false;
                } else {
                    // get the drop location
                    JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
                    int dindex = dl.getIndex();
                    if( ( CurMech.GetLoadout().GetCrits( LocationIndex.MECH_LOC_LT )[dindex].LocationLocked() || CurMech.GetLoadout().GetCrits( LocationIndex.MECH_LOC_LT )[dindex].LocationLinked() ) && a != CurMech.GetLoadout().GetCrits( LocationIndex.MECH_LOC_LT )[dindex] ) {
                        return false;
                    }
                    if( a instanceof CASE ) {
                        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().GetLTCase() == a ) {
                            return false;
                        }
                    } else if(  a instanceof CASEII ) {
                        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().GetLTCaseII() == a ) {
                            return false;
                        }
                    } else if( a instanceof Supercharger ) {
                        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().GetSupercharger() == a ) {
                            return false;
                        }
                    } else if( a instanceof MultiSlotSystem ) {
                        if( CurMech.IsOmnimech() ) {
                            return false;
                        }
                    } else if( a instanceof SimplePlaceable ) {
                        if( CurMech.IsOmnimech() ) {
                            return false;
                        }
                    } else if( a instanceof PartialWing ) {
                        int Size = CurMech.GetPartialWing().NumCrits();
                        abPlaceable[] Loc = CurMech.GetLoadout().GetLTCrits();
                        try {
                            for( int i = 0; i < Size; i++ ) {
                                if( ( Loc[dindex + i].LocationLocked() || Loc[dindex + i].LocationLinked() ) && a != Loc[dindex + i] ) {
                                    return false;
                                }
                            }
                        } catch( Exception e ) {
                            return false;
                        }
                    } else if( a instanceof MechArmor ) {
                        if( CurMech.IsOmnimech() ) {
                            return false;
                        }
                        if( CurMech.GetLoadout().GetLTCrits()[dindex].LocationLocked() || CurMech.GetLoadout().GetLTCrits()[dindex].LocationLocked() ) {
                            return false;
                        }
                    } else if( a instanceof MechTurret ) {
                        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasLTTurret() ) {
                            return false;
                        }
                    } else if( a instanceof Engine ) {
                        if( CurMech.IsOmnimech() ) {
                            return false;
                        }
                        // get the side torso crit size so we can check for non-
                        // moveable items.  This'll probably piss some people off
                        int Size = CurMech.GetEngine().GetSideTorsoCrits();
                        abPlaceable[] Loc = CurMech.GetLoadout().GetCrits( LocationIndex.MECH_LOC_LT );
                        try {
                            for( int i = 0; i < Size; i++ ) {
                                if( ( Loc[dindex + i].LocationLocked() || Loc[dindex + i].LocationLinked() ) && a != Loc[dindex + i] ) {
                                    return false;
                                }
                            }
                        } catch( Exception e ) {
                            return false;
                        }
                    } else {
                        // added for code completeness, but this should never happen
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        if( DropItem.Empty ) { return false; }

        info.setShowDropLocation( true );

        // looks like we can import
        return true;
    }

    @Override
    public int getSourceActions( JComponent c ) {
        return MOVE;
    }

    @Override
    public boolean importData( TransferHandler.TransferSupport info ) {
        if (! canImport(info)) {
            System.out.println( "couldn't import" );
            return false;
        }

        LocationDragDatagram DropItem = null;
        boolean rear = false;
        boolean turreted = false;
        // get the item data
        try {
            DropItem = (LocationDragDatagram) info.getTransferable().getTransferData( new DataFlavor( LocationDragDatagram.class, "Location Drag Datagram" ) );
        } catch ( Exception e ) {
            return false;
        }

        // get the drop location
        JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
        int dindex = dl.getIndex();

        // get the item
        abPlaceable a;
        ArrayList v = new ArrayList();
        if( DropItem.Location == -1 ) {
            // from the queue
            a = CurMech.GetLoadout().GetFromQueueByIndex( DropItem.SourceIndex );
        } else {
            // from another location
            a = CurMech.GetLoadout().GetCrits( DropItem.Location )[DropItem.SourceIndex];
            rear = a.IsMountedRear();
            turreted = a.IsTurreted();
            if( a.CanSplit() && a.Contiguous() ) {
                // find all locations before unallocating
                v = CurMech.GetLoadout().FindSplitIndex( a );
                CurMech.GetLoadout().UnallocateAll( a, false );
            } else {
                CurMech.GetLoadout().UnallocateByIndex( DropItem.SourceIndex, CurMech.GetLoadout().GetCrits( DropItem.Location ) );
            }
        }

        // now put it in where it needs to go
        try {
            if( a.CanSplit() && a.Contiguous() ) {
                if( a instanceof PartialWing ) {
                    // have to do this manually
                    abPlaceable[] loc = CurMech.GetLoadout().GetLTCrits();
                    for( int i = 0; i < loc.length; i++ ) {
                        if( loc[i] == a ) { loc[i] = CurMech.GetLoadout().GetNoItem(); }
                    }
                    CurMech.GetLoadout().AddToLT( a, dindex );
                } else {
                    if( DropItem.Location == LocationIndex.MECH_LOC_LT ) {
                        LocationIndex loc1 = null;
                        LocationIndex loc2 = null;
                        for( int i = 0; i < v.size(); i++ ) {
                            if( ((LocationIndex) v.get( i )).Location == LocationIndex.MECH_LOC_LT ) {
                                loc1 = (LocationIndex) v.get( i );
                            } else {
                                loc2 = (LocationIndex) v.get( i );
                            }
                        }
                        if( loc1 == null ) { return false; }

                        // only allocate as many crits as were originally here.
                        if( loc2 == null ) {
                            if( loc1.Number + dindex > CurMech.GetLoadout().GetCrits( loc1.Location ).length ) {
                                return SplitAllocate( a, dindex );
                            } else {
                                CurMech.GetLoadout().AddTo( CurMech.GetLoadout().GetCrits( loc1.Location ), a, dindex, loc1.Number );
                            }
                        } else {
                            if( loc1.Number + dindex > CurMech.GetLoadout().GetCrits( loc1.Location ).length ) {
                                return SplitAllocate( a, dindex );
                            } else {
                                CurMech.GetLoadout().AddTo( CurMech.GetLoadout().GetCrits( loc1.Location ), a, dindex, loc1.Number );
                                CurMech.GetLoadout().AddTo( CurMech.GetLoadout().GetCrits( loc2.Location ), a, loc2.Index, loc2.Number );
                            }
                        }
                    } else {
                        return SplitAllocate( a, dindex );
                    }
                }
            } else {
                CurMech.GetLoadout().AddToLT( a, dindex );
            }
        } catch( Exception e ) {
            CurMech.GetLoadout().AddToQueue( a );
            javax.swing.JOptionPane.showMessageDialog( (javax.swing.JFrame) Parent, e.getMessage() );
            Parent.RefreshSummary();
            Parent.RefreshInfoPane();
            return false;
        }
        if( a.NumPlaced() <= 0 ) {
            CurMech.GetLoadout().RemoveFromQueue( a );
        }
        a.MountRear( rear );
        if( turreted ) {
            a.MountTurret( CurMech.GetLoadout().GetLTTurret() );
        }
        if( a instanceof VehicularGrenadeLauncher ) {
            // reset the arc as it may not be appropriate
            ((VehicularGrenadeLauncher ) a).SetArcFore();
        }
        Parent.RefreshSummary();
        Parent.RefreshInfoPane();
        return true;
    }

    private boolean SplitAllocate( abPlaceable a, int dindex ) throws Exception {
        int ToPlace = CurMech.GetLoadout().FreeFrom( CurMech.GetLoadout().GetLTCrits(), dindex );
        if( ToPlace < a.NumCrits() ) {
            dlgSplitCrits dlgSplit = new dlgSplitCrits( (javax.swing.JFrame) Parent, true, a, LocationIndex.MECH_LOC_LT, dindex );
            Point p = ((javax.swing.JFrame) Parent).getLocationOnScreen();
            dlgSplit.setLocation( p.x + 100, p.y + 100 );
            dlgSplit.setVisible( true );
            if( dlgSplit.GetResult() ) {
                if( a.NumPlaced() <= 0 ) {
                   CurMech.GetLoadout().RemoveFromQueue( a );
                }
                Parent.RefreshSummary();
                Parent.RefreshInfoPane();
                dlgSplit.dispose();
                return true;
            } else {
                CurMech.GetLoadout().AddToQueue( a );
                Parent.RefreshSummary();
                Parent.RefreshInfoPane();
                dlgSplit.dispose();
                return false;
            }
        } else {
            CurMech.GetLoadout().AddToLT( a, dindex );
            Parent.RefreshSummary();
            Parent.RefreshInfoPane();
            return true;
        }
    }
}

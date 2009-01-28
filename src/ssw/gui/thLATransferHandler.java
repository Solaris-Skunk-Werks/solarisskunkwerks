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
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import ssw.*;
import ssw.components.*;

public class thLATransferHandler extends TransferHandler {
    private frmMain Parent;
    private Mech CurMech;

    // the right arm transfer handler only deals with adding to the right arm.
    // other listeners will deal with removing items.
    public thLATransferHandler( frmMain f, Mech m ) {
        Parent = f;
        CurMech = m;
    }

    @Override
    public Transferable createTransferable( JComponent comp ) {
        // all we want to do is transfer the index in the queue
        LocationDragDatagram d = new LocationDragDatagram();
        d.Location = Constants.LOC_LA;
        d.SourceIndex = ((JList) comp).getSelectedIndex();
        d.Locked = CurMech.GetLoadout().GetLACrits()[d.SourceIndex].LocationLocked();
        if( CurMech.GetLoadout().GetLACrits()[d.SourceIndex] instanceof EmptyItem ) {
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

        if( ! info.isDataFlavorSupported( new DataFlavor( ssw.gui.LocationDragDatagram.class, "Location Drag Datagram" ) ) ) {
            // not what we're looking for
            return false;
        }

        LocationDragDatagram DropItem = null;
        try {
            DropItem = (LocationDragDatagram) info.getTransferable().getTransferData( new DataFlavor( ssw.gui.LocationDragDatagram.class, "Location Drag Datagram" ) );
        } catch ( Exception e ) {
            return false;
        }

        if( DropItem.Locked ) {
            abPlaceable a = CurMech.GetLoadout().GetCrits( DropItem.Location )[DropItem.SourceIndex];
            if( a instanceof CASEII || a instanceof MultiSlotSystem ) {
                if( DropItem.Location != Constants.LOC_LA ) {
                    return false;
                } else {
                    // get the drop location
                    JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
                    int dindex = dl.getIndex();
                    if( CurMech.GetLoadout().GetCrits( Constants.LOC_LA )[dindex].LocationLocked() || CurMech.GetLoadout().GetCrits( Constants.LOC_LA )[dindex].LocationLinked() ) {
                        return false;
                    }
                    if( a instanceof CASEII ) {
                        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().GetLACaseII() == a ) {
                            return false;
                        }
                    } else if( a instanceof MultiSlotSystem ) {
                        if( CurMech.IsOmnimech() ) {
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
        // get the item data
        try {
            DropItem = (LocationDragDatagram) info.getTransferable().getTransferData( new DataFlavor( ssw.gui.LocationDragDatagram.class, "Location Drag Datagram" ) );
        } catch ( Exception e ) {
            return false;
        }

        // get the drop location
        JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
        int dindex = dl.getIndex();

        // get the item
        abPlaceable a;
        if( DropItem.Location == -1 ) {
            // from the queue
            a = CurMech.GetLoadout().GetFromQueueByIndex( DropItem.SourceIndex );
        } else {
            // from another location
            a = CurMech.GetLoadout().GetCrits( DropItem.Location )[DropItem.SourceIndex];
            if( a.CanSplit() && a.Contiguous() ) {
                CurMech.GetLoadout().UnallocateAll( a, false );
            } else {
                CurMech.GetLoadout().UnallocateByIndex( DropItem.SourceIndex, CurMech.GetLoadout().GetCrits( DropItem.Location ) );
            }
        }

        // now put it in where it needs to go
        try {
            if( a.CanSplit() && a.Contiguous() ) {
                // we need to figure out how many crits will be placed here
                int ToPlace = CurMech.GetLoadout().FreeFrom( CurMech.GetLoadout().GetLACrits(), dindex );
                if( ToPlace < a.NumCrits() ) {
                    dlgSplitCrits dlgSplit = new dlgSplitCrits( Parent, true, a, Constants.LOC_LA, dindex );
                    Point p = Parent.getLocationOnScreen();
                    dlgSplit.setLocation( p.x + 100, p.y + 100 );
                    dlgSplit.setVisible( true );
                    if( dlgSplit.GetResult() ) {
                        if( a.NumPlaced() <= 0 ) {
                            CurMech.GetLoadout().RemoveFromQueue( a );
                        }
                        Parent.RefreshInfoPane();
                        dlgSplit.dispose();
                        return true;
                    } else {
                        CurMech.GetLoadout().AddToQueue( a );
                        CurMech.ReCalcBaseCost();
                        Parent.RefreshInfoPane();
                        dlgSplit.dispose();
                        return false;
                    }
                } else {
                    CurMech.GetLoadout().AddToLA( a, dindex );
                }
            } else {
                CurMech.GetLoadout().AddToLA( a, dindex );
            }
        } catch( Exception e ) {
            CurMech.GetLoadout().AddToQueue( a );
            javax.swing.JOptionPane.showMessageDialog( Parent, e.getMessage() );
            Parent.RefreshInfoPane();
            return false;
        }
        if( a.NumPlaced() <= 0 ) {
            CurMech.GetLoadout().RemoveFromQueue( a );
        }
        CurMech.ReCalcBaseCost();
        Parent.RefreshInfoPane();
        return true;
    }
}

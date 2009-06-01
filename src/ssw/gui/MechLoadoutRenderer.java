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

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import ssw.*;
import ssw.components.*;

public class MechLoadoutRenderer  extends DefaultListCellRenderer {
    private Options CurOptions;
    private frmMain Parent;

    public MechLoadoutRenderer( frmMain p, Options o ) {
        Parent = p;
        CurOptions = o;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
        String Text = "";
        abPlaceable[] Loc = null;
        int LocNum = -1;
        abPlaceable a = null;
        Color BorderCol = new Color( 0, 0, 0 );
        // find the location
        LocNum =  Parent.GetLocation( list );
        switch( LocNum ) {
            case Constants.LOC_HD:
                Loc = Parent.CurMech.GetLoadout().GetHDCrits();
                break;
            case Constants.LOC_CT:
                Loc = Parent.CurMech.GetLoadout().GetCTCrits();
                break;
            case Constants.LOC_LT:
                Loc = Parent.CurMech.GetLoadout().GetLTCrits();
                break;
            case Constants.LOC_RT:
                Loc = Parent.CurMech.GetLoadout().GetRTCrits();
                break;
            case Constants.LOC_LA:
                Loc = Parent.CurMech.GetLoadout().GetLACrits();
                break;
            case Constants.LOC_RA:
                Loc = Parent.CurMech.GetLoadout().GetRACrits();
                break;
            case Constants.LOC_LL:
                Loc = Parent.CurMech.GetLoadout().GetLLCrits();
                break;
            case Constants.LOC_RL:
                Loc = Parent.CurMech.GetLoadout().GetRLCrits();
                break;
            default:
                Loc = null;
        }

        if( value instanceof abPlaceable ) {
            a = (abPlaceable) value;
            if( a.IsArmored() ) {
                label.setBackground( CurOptions.bg_ARMORED );
                label.setForeground( CurOptions.fg_ARMORED );
                BorderCol = CurOptions.bg_ARMORED;
            } else if( a.LocationLinked() ) {
                label.setBackground( CurOptions.bg_LINKED );
                label.setForeground( CurOptions.fg_LINKED );
                BorderCol = CurOptions.bg_LINKED;
            } else if( a.LocationLocked() ) {
                label.setBackground( CurOptions.bg_LOCKED );
                label.setForeground( CurOptions.fg_LOCKED );
                BorderCol = CurOptions.bg_LOCKED;
            } else if( a instanceof EmptyItem ) {
                label.setBackground( CurOptions.bg_EMPTY );
                label.setForeground( CurOptions.fg_EMPTY );
                BorderCol = CurOptions.bg_EMPTY;
            } else {
                label.setBackground( CurOptions.bg_NORMAL );
                label.setForeground( CurOptions.fg_NORMAL );
                BorderCol = CurOptions.bg_NORMAL;
            }
            if( Parent.CurMech.GetTechBase() == AvailableCode.TECH_BOTH ) {
                Text = a.GetLookupName();
            } else {
                Text = a.GetCritName();
            }
        }

        label.setText( Text );

        JList.DropLocation dropLocation = list.getDropLocation();
        if ( dropLocation != null && dropLocation.getIndex() == index ) {
            int size = Parent.CurItem.NumCrits();
            if( Parent.CurItem instanceof RangedWeapon ) {
                if( ((RangedWeapon) Parent.CurItem).IsUsingFCS() ) {
                    size += ((abPlaceable) ((RangedWeapon) Parent.CurItem).GetFCS()).NumCrits();
                }
                if( ((RangedWeapon) Parent.CurItem).IsUsingCapacitor() ) {
                    size++;
                }
                if( ((RangedWeapon) Parent.CurItem).IsUsingInsulator() ) {
                    size++;
                }
            }
            if( Parent.CurItem instanceof MGArray ) {
                size += ((MGArray) Parent.CurItem).GetNumMGs();
            }
            if( ! Parent.CurItem.Contiguous() ) {
                size = 1;
            }
            label.setBackground( CurOptions.bg_HILITE );
            label.setForeground( CurOptions.fg_HILITE );
            if( Parent.CurMech.GetTechBase() == AvailableCode.TECH_BOTH ) {
                Text =  Parent.CurItem.GetLookupName();
            } else {
                Text =  Parent.CurItem.GetCritName();
            }
            if( size <= 1 ) {
                label.setText( Text );
            } else {
                label.setText( "(" + size + ")" + Text );
            }
        }

        if( Loc == null ) {
            label.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        } else {
            if( ! ( index + 1 >= Loc.length ) ) {
                if( Loc[index + 1] == a &! ( a instanceof EmptyItem ) && a.Contiguous() ) {
                    //no bottom border.  just skip me
                    label.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, BorderCol));
                } else {
                    label.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
                }
            } else {
                // just add the lower border in because it looks nicer.
                label.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
            }
        }
        return label;
    }
}

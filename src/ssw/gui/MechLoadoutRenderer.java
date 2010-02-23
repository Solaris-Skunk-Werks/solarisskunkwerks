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
import components.*;

public class MechLoadoutRenderer extends DefaultListCellRenderer {
    private frmMain Parent;
    private Color EmptyFG,
                  EmptyBG,
                  NormalFG,
                  NormalBG,
                  ArmoredFG,
                  ArmoredBG,
                  LinkedFG,
                  LinkedBG,
                  LockedFG,
                  LockedBG,
                  HiliteFG,
                  HiliteBG;

    public MechLoadoutRenderer( frmMain p ) {
        Parent = p;
        Reset();
    }

    public void Reset() {
        // resets the colors on prefs change
        EmptyFG = new Color( Parent.Prefs.getInt( "ColorEmptyItemFG", -16777216 ) );
        EmptyBG = new Color( Parent.Prefs.getInt( "ColorEmptyItemBG", -6684775 ) );
        NormalFG = new Color( Parent.Prefs.getInt( "ColorNormalItemFG", -16777216 ) );
        NormalBG = new Color( Parent.Prefs.getInt( "ColorNormalItemBG", -10027009 ) );
        ArmoredFG = new Color( Parent.Prefs.getInt( "ColorArmoredItemFG", -1 ) );
        ArmoredBG = new Color( Parent.Prefs.getInt( "ColorArmoredItemBG", -6710887 ) );
        LinkedFG = new Color( Parent.Prefs.getInt( "ColorLinkedItemFG", -16777216 ) );
        LinkedBG = new Color( Parent.Prefs.getInt( "ColorLinkedItemBG", -3618616 ) );
        LockedFG = new Color( Parent.Prefs.getInt( "ColorLockedItemFG", -3342337 ) );
        LockedBG = new Color( Parent.Prefs.getInt( "ColorLockedItemBG", -16777216 ) );
        HiliteFG = new Color( Parent.Prefs.getInt( "ColorHiLiteItemFG", -16777216 ) );
        HiliteBG = new Color( Parent.Prefs.getInt( "ColorHiLiteItemBG", -52 ) );
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
            case LocationIndex.MECH_LOC_HD:
                Loc = Parent.CurMech.GetLoadout().GetHDCrits();
                break;
            case LocationIndex.MECH_LOC_CT:
                Loc = Parent.CurMech.GetLoadout().GetCTCrits();
                break;
            case LocationIndex.MECH_LOC_LT:
                Loc = Parent.CurMech.GetLoadout().GetLTCrits();
                break;
            case LocationIndex.MECH_LOC_RT:
                Loc = Parent.CurMech.GetLoadout().GetRTCrits();
                break;
            case LocationIndex.MECH_LOC_LA:
                Loc = Parent.CurMech.GetLoadout().GetLACrits();
                break;
            case LocationIndex.MECH_LOC_RA:
                Loc = Parent.CurMech.GetLoadout().GetRACrits();
                break;
            case LocationIndex.MECH_LOC_LL:
                Loc = Parent.CurMech.GetLoadout().GetLLCrits();
                break;
            case LocationIndex.MECH_LOC_RL:
                Loc = Parent.CurMech.GetLoadout().GetRLCrits();
                break;
            default:
                Loc = null;
        }

        if( value instanceof abPlaceable ) {
            a = (abPlaceable) value;
            if( a.IsArmored() ) {
                label.setBackground( ArmoredBG );
                label.setForeground( ArmoredFG );
                BorderCol = ArmoredBG;
            } else if( a.LocationLinked() ) {
                label.setBackground( LinkedBG );
                label.setForeground( LinkedFG );
                BorderCol = LinkedBG;
            } else if( a.LocationLocked() ) {
                label.setBackground( LockedBG );
                label.setForeground( LockedFG );
                BorderCol = LockedBG;
            } else if( a instanceof EmptyItem ) {
                label.setBackground( EmptyBG );
                label.setForeground( EmptyFG );
                BorderCol = EmptyBG;
            } else {
                label.setBackground( NormalBG );
                label.setForeground( NormalFG );
                BorderCol = NormalBG;
            }

            if( Parent.CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                if( a instanceof Equipment ) {
                    if( ((Equipment) a).IsVariableSize() ) {
                        Text = a.CritName();
                    } else {
                        Text = a.LookupName();
                    }
                } else if( a instanceof MechArmor ) {
                    if( ((MechArmor) a).IsPatchwork() ) {
                        Text = Parent.BuildLookupName( ((MechArmor) a).GetLocationType( LocNum ) );
                    } else {
                        Text = a.LookupName();
                    }
                } else {
                    Text = a.LookupName();
                }
            } else {
                if( a instanceof MechArmor ) {
                    if( ((MechArmor) a).IsPatchwork() ) {
                        Text = ((MechArmor) a).CritName( LocNum );
                    } else {
                        Text = a.CritName();
                    }
                } else {
                    Text = a.CritName();
                }
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
            label.setBackground( HiliteBG );
            label.setForeground( HiliteFG );
            if( Parent.CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                if( Parent.CurItem instanceof Equipment ) {
                    if( ((Equipment) Parent.CurItem).IsVariableSize() ) {
                        Text =  Parent.CurItem.CritName();
                    } else {
                        Text =  Parent.CurItem.LookupName();
                    }
                } else if( Parent.CurItem instanceof MechArmor ) {
                    if( ((MechArmor) Parent.CurItem).IsPatchwork() ) {
                        Text = Parent.BuildLookupName( ((MechArmor) Parent.CurItem).GetLocationType( LocNum ) );
                    } else {
                        Text = Parent.BuildLookupName( ((MechArmor) Parent.CurItem).GetCurrentState() );
                    }
                } else {
                    Text = Parent.CurItem.LookupName();
                }
            } else {
                if( Parent.CurItem instanceof MechArmor ) {
                    if( ((MechArmor) Parent.CurItem).IsPatchwork() ) {
                        Text = ((MechArmor) Parent.CurItem).CritName( LocNum );
                    } else {
                        Text =  Parent.CurItem.CritName();
                    }
                } else {
                    Text =  Parent.CurItem.CritName();
                }
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

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
    private ifMechForm Parent;
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

    public MechLoadoutRenderer( ifMechForm p ) {
        Parent = p;
        Reset();
    }

    public void Reset() {
        // resets the colors on prefs change
        EmptyFG = new Color( Parent.GetPrefs().getInt( "ColorEmptyItemFG", -16777216 ) );
        EmptyBG = new Color( Parent.GetPrefs().getInt( "ColorEmptyItemBG", -6684775 ) );
        NormalFG = new Color( Parent.GetPrefs().getInt( "ColorNormalItemFG", -16777216 ) );
        NormalBG = new Color( Parent.GetPrefs().getInt( "ColorNormalItemBG", -10027009 ) );
        ArmoredFG = new Color( Parent.GetPrefs().getInt( "ColorArmoredItemFG", -1 ) );
        ArmoredBG = new Color( Parent.GetPrefs().getInt( "ColorArmoredItemBG", -6710887 ) );
        LinkedFG = new Color( Parent.GetPrefs().getInt( "ColorLinkedItemFG", -16777216 ) );
        LinkedBG = new Color( Parent.GetPrefs().getInt( "ColorLinkedItemBG", -3618616 ) );
        LockedFG = new Color( Parent.GetPrefs().getInt( "ColorLockedItemFG", -3342337 ) );
        LockedBG = new Color( Parent.GetPrefs().getInt( "ColorLockedItemBG", -16777216 ) );
        HiliteFG = new Color( Parent.GetPrefs().getInt( "ColorHiLiteItemFG", -16777216 ) );
        HiliteBG = new Color( Parent.GetPrefs().getInt( "ColorHiLiteItemBG", -52 ) );
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
                Loc = Parent.GetMech().GetLoadout().GetHDCrits();
                break;
            case LocationIndex.MECH_LOC_CT:
                Loc = Parent.GetMech().GetLoadout().GetCTCrits();
                break;
            case LocationIndex.MECH_LOC_LT:
                Loc = Parent.GetMech().GetLoadout().GetLTCrits();
                break;
            case LocationIndex.MECH_LOC_RT:
                Loc = Parent.GetMech().GetLoadout().GetRTCrits();
                break;
            case LocationIndex.MECH_LOC_LA:
                Loc = Parent.GetMech().GetLoadout().GetLACrits();
                break;
            case LocationIndex.MECH_LOC_RA:
                Loc = Parent.GetMech().GetLoadout().GetRACrits();
                break;
            case LocationIndex.MECH_LOC_LL:
                Loc = Parent.GetMech().GetLoadout().GetLLCrits();
                break;
            case LocationIndex.MECH_LOC_RL:
                Loc = Parent.GetMech().GetLoadout().GetRLCrits();
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

            if( Parent.GetMech().GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
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
            int size = Parent.GetCurItem().NumCrits();
            if( Parent.GetCurItem() instanceof RangedWeapon ) {
                if( ((RangedWeapon) Parent.GetCurItem()).IsUsingFCS() ) {
                    size += ((abPlaceable) ((RangedWeapon) Parent.GetCurItem()).GetFCS()).NumCrits();
                }
                if( ((RangedWeapon) Parent.GetCurItem()).IsUsingCapacitor() ) {
                    size++;
                }
                if( ((RangedWeapon) Parent.GetCurItem()).IsUsingInsulator() ) {
                    size++;
                }
            }
            if( Parent.GetCurItem() instanceof MGArray ) {
                size += ((MGArray) Parent.GetCurItem()).GetNumMGs();
            }
            if( ! Parent.GetCurItem().Contiguous() ) {
                size = 1;
            }
            label.setBackground( HiliteBG );
            label.setForeground( HiliteFG );
            if( Parent.GetMech().GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                if( Parent.GetCurItem() instanceof Equipment ) {
                    if( ((Equipment) Parent.GetCurItem()).IsVariableSize() ) {
                        Text =  Parent.GetCurItem().CritName();
                    } else {
                        Text =  Parent.GetCurItem().LookupName();
                    }
                } else if( Parent.GetCurItem() instanceof MechArmor ) {
                    if( ((MechArmor) Parent.GetCurItem()).IsPatchwork() ) {
                        Text = Parent.BuildLookupName( ((MechArmor) Parent.GetCurItem()).GetLocationType( LocNum ) );
                    } else {
                        Text = Parent.BuildLookupName( ((MechArmor) Parent.GetCurItem()).GetCurrentState() );
                    }
                } else {
                    Text = Parent.GetCurItem().LookupName();
                }
            } else {
                if( Parent.GetCurItem() instanceof MechArmor ) {
                    if( ((MechArmor) Parent.GetCurItem()).IsPatchwork() ) {
                        Text = ((MechArmor) Parent.GetCurItem()).CritName( LocNum );
                    } else {
                        Text =  Parent.GetCurItem().CritName();
                    }
                } else {
                    Text =  Parent.GetCurItem().CritName();
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

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

package saw.gui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import components.AvailableCode;
import components.CombatVehicle;
import components.Equipment;
import components.abPlaceable;
import components.EquipmentCollection;

public class EquipmentListRenderer extends DefaultListCellRenderer {
    private ifVeeForm Parent;
    private CombatVehicle CurVee;
    private abPlaceable a = null;

    public EquipmentListRenderer( ifVeeForm v ) {
        Parent = v;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
        String Text = "";
        if( value instanceof abPlaceable ) {
            a = (abPlaceable) value;
            if( a.Contiguous() ) {
                if( Parent.GetVehicle().GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    if( a instanceof Equipment ) {
                        if( ((Equipment) a).IsVariableSize() ) {
                            Text = a.CritName();
                        } else {
                            Text = a.LookupName();
                        }
                    } else {
                        Text = a.LookupName();
                    }
                } else {
                    Text = a.CritName();
                }
            } else {
                Text = a.toString();
            }
        } else if( value instanceof EquipmentCollection ) {
            Text = value.toString();
        }

        label.setText( Text );

        return label;
    }
}

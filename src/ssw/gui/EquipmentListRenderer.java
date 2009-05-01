/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.gui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import ssw.components.AvailableCode;
import ssw.components.abPlaceable;

/**
 *
 * @author justin
 */
public class EquipmentListRenderer extends DefaultListCellRenderer {
    private frmMain Parent;
    private abPlaceable a = null;

    public EquipmentListRenderer( frmMain p ) {
        Parent = p;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
        String Text = "";
        if( value instanceof abPlaceable ) {
            a = (abPlaceable) value;
            if( a.Contiguous() ) {
                if( Parent.CurMech.GetTechBase() == AvailableCode.TECH_BOTH ) {
                    Text = a.GetLookupName();
                } else {
                    Text = a.GetCritName();
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

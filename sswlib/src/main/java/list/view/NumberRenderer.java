/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package list.view;

import java.text.Format;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author olaughlj
 */
public class NumberRenderer extends DefaultTableCellRenderer {

    public NumberRenderer()
    {
    }

    @Override
    public int getHorizontalAlignment()
    {
        return JLabel.RIGHT;
    }

    @Override
    protected void setValue(Object value)
    {
        try
        {
            double d = Double.parseDouble(value.toString());
            Object formattedObj = String.format( "%1$,d", (int)d);
            super.setValue(formattedObj);
        }
        catch (IllegalArgumentException e)
        {
            super.setValue(value);
        }
    }
}

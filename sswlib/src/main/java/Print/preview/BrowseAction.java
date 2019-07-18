/*
 *  Preview Dialog - A Preview Dialog for your Swing Applications
 *
 *  Copyright (C) 2003 Jens Kaiser.
 *
 *  Written by: 2003 Jens Kaiser <jens.kaiser@web.de>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package Print.preview;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

class BrowseAction extends AbstractAction {
    public BrowseAction( String name, String iconName, Preview preview, int pageStep ) {
        super();
        if ( !name.isEmpty() ) {
            this.putValue(Action.NAME, name);
        }

        if ( !iconName.isEmpty() ) {
            ImageIcon icon = null;
            icon = new ImageIcon(getClass().getResource("/images/" + iconName));
            this.putValue(Action.SMALL_ICON, icon);
        }

        this.preview = preview;
        this.pageStep = pageStep;
    }

    public BrowseAction(Preview preview, int pageStep) {
        this("", "", preview, pageStep);
    }

    public void actionPerformed(ActionEvent e) {
        preview.moveIndex(pageStep);
        preview.repaint();
    }

    protected Preview preview;
    protected int pageStep;
}

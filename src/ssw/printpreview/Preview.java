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
package ssw.printpreview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import javax.swing.JComponent;

class Preview extends JComponent {
    private final static int DEFAULT_PREVIEW_SIZE = 300;
    private final static double MINIMUM_ZOOM_FACTOR = 0.1;

    public Preview(Pageable pageable, double zoom) {
        this.pageable = pageable;
        PageFormat format = pageable.getPageFormat(index);
        if (zoom == 0.0) {
            if (format.getOrientation() == PageFormat.PORTRAIT)
                this.zoom = DEFAULT_PREVIEW_SIZE / format.getHeight();
            else 
                this.zoom = DEFAULT_PREVIEW_SIZE / format.getWidth();
        } else
            this.zoom = zoom;
        resize();
    }
    
    protected void paintPaper(Graphics g, PageFormat format) {
        g.setColor(Color.white);
        g.fillRect(0, 0, (int)format.getWidth(), (int)format.getHeight());        
        g.setColor(Color.black);
        g.drawRect(0, 0, (int)format.getWidth() - 1, (int)format.getHeight() - 1);        
    }
    
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.scale(zoom, zoom);
        try {
            PageFormat format = pageable.getPageFormat(index);   
            Printable printable = pageable.getPrintable(index);
            paintPaper(g, format);
            printable.print(g, format, 0);
        } catch (PrinterException e) {
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void moveIndex(int indexStep) {
        int newIndex = index + indexStep;
        try {
            Printable printable = pageable.getPrintable(newIndex);
            resize();
            index = newIndex;
        } catch (IndexOutOfBoundsException ignored) {
        }
    }
    
    public void changeZoom(double zoom) {
        this.zoom = Math.max(MINIMUM_ZOOM_FACTOR, this.zoom + zoom);
        resize();
    }
    
    public void resize() {
        PageFormat format = pageable.getPageFormat(index);
        int size = (int)Math.max(format.getWidth() * zoom, format.getHeight() * zoom);
        setPreferredSize(new Dimension(size, size));
        revalidate();
    }
    
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    protected Pageable pageable;
    protected int index = 0;
    protected double zoom = 0.0;
}

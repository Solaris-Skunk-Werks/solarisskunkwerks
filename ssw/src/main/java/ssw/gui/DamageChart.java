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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Vector;
import javax.swing.JPanel;

public class DamageChart extends JPanel {
    private int GridX = 1;
    private int GridY = 1;
    private Vector charts = new Vector();
    private Vector colors = new Vector();
    private boolean TextView = false;

    public DamageChart() {
        setBackground( Color.WHITE );
    }

/**
 * Sets the grid width and height for this damage chart.
 * X should be one greater than the maximum range the unit can hit at
 * Y should be one greater than the maximum damage the unit can produce at any range.
 * 
 * @param x Grid X Units
 * @param y Grid Y Units
 */
    public void SetGridSize( int x, int y ) {
        // sets the number of units to be used for grid x and y
        GridX = x;
        GridY = y;
        repaint();
    }

/**
 * Adds a Damage At Range chart to the chart.  The DAR chart should consist of
 * an int[], with each array element representing the damage at that range.
 * For instance, chart[9] should return the damage the unit produces at range 9.
 * 
 * @param chart The Damage At Range chart.
 * @param ccolor The color to draw the DAR chart's line in.
 */
    public void AddChart( int[] chart, Color ccolor ) {
        // adds a chart to those needing to be plotted
        charts.add( chart );
        colors.add( ccolor );
    }

/**
 * Simply clears the charts vector.  Should be used when loading a new unit, or
 * when a new weapon is added or a weapon removed.
 */
    public void ClearCharts() {
        charts.clear();
        colors.clear();
    }

    public void SetTextView( boolean b ) {
        TextView = b;
        repaint();
    }

    @Override
    public void paintComponent( Graphics g ) {
        super.paintComponent( g );

        if( TextView ) {
            DrawText( (Graphics2D) g );
        } else {
            DrawGrid( (Graphics2D) g );
            DrawCharts( (Graphics2D) g );
        }
    }

    private void DrawCharts( Graphics2D g ) {
        int dx = getSize().width;
        int dy = getSize().height;
        double offX = (double) dx / (double) GridX;
        double offY = (double) dy / (double) GridY;

        g.setStroke( new BasicStroke( 2 ) );
        for( int i = 0; i < charts.size(); i++ ) {
            int[] chart = (int[]) charts.get( i );
            g.setColor( ((Color) colors.get( i )) );
            for( int j = 1; j < chart.length; j++ ) {
                if( chart[j] == 0 ) {
                    g.draw( new Line2D.Double( (( j - 1 ) * offX), (dy - (chart[j-1] * offY)), (( j - 1 ) * offX), (dy - (chart[j] * offY)) ) );
                } else {
                    if( chart[j] < chart[j-1] ) {
                        g.draw( new Line2D.Double( (( j - 1 ) * offX), (dy - (chart[j-1] * offY)), (( j - 1 ) * offX), (dy - (chart[j] * offY)) ) );
                        g.draw( new Line2D.Double( (( j - 1 ) * offX), (dy - (chart[j] * offY)), (j * offX), (dy - (chart[j] * offY)) ) );
                    } else {
                        g.draw( new Line2D.Double( (( j - 1 ) * offX), (dy - (chart[j-1] * offY)), (j * offX), (dy - (chart[j] * offY)) ) );
                    }
                }
            }
        }
    }

    private void DrawGrid( Graphics2D g ) {
        // draws the grid onto the component
        int dx = getSize().width;
        int dy = getSize().height;
        double offX = (double) dx / (double) GridX;
        double offY = (double) dy / (double) GridY;
        int linemodulo = GridY / 10;
        if( linemodulo <= 0 ) { linemodulo = 1; }

        g.setColor( Color.LIGHT_GRAY );

        for( int i = 1; i < GridX; i++ ) {
            g.draw( new Line2D.Double( offX * i, 0, offX * i, dy ) );
            if( i % 5 == 0 ) {
                g.setColor( Color.BLACK );
                g.drawString( "" + i, (int) offX * i, dy - 4 );
                g.setColor( Color.LIGHT_GRAY );
            }
        }
        for( int i = 1; i < GridY; i++ ) {
            if( i % linemodulo == 0 ) {
                g.draw( new Line2D.Double( 0, (dy - (offY * i)), dx, (dy - (offY * i)) ) );
                g.setColor( Color.BLACK );
                g.drawString( "" + i, 1, (int) (dy - (offY * i) + 5) );
                g.setColor( Color.LIGHT_GRAY );
            }
        }
    }

    private void DrawText( Graphics2D g ) {
        Vector<Group> curgroups = new Vector<Group>();
        int CurX = 10;

        for( int i = 0; i < charts.size(); i++ ) {
            int[] chart = (int[]) charts.get( i );
            g.setColor( (Color) colors.get( i ) );
            Group newGroup = null;
            for( int j = 1; j < chart.length; j++ ) {
                // build the group.
                if( newGroup == null ) {
                    newGroup = new Group();
                    newGroup.StartRng = j;
                    newGroup.EndRng = j;
                    newGroup.Dmg = chart[j];
                    curgroups.add( newGroup );
                } else {
                    Group group = new Group();
                    group.StartRng = j;
                    group.EndRng = j;
                    group.Dmg = chart[j];
                    if( ! newGroup.Combine( group ) ) {
                        curgroups.add( group );
                        newGroup = group;
                    }
                }
            }
            // print the groups
            int CurY = 15;
            for( int j = 0; j < curgroups.size(); j++ ) {
                int dmg = curgroups.get( j ).Dmg;
                int srng = curgroups.get( j ).StartRng;
                int erng = curgroups.get( j ).EndRng;
                if( dmg > 0 ) {
                    if( srng == erng ) {
                        g.drawString( "Range " + srng + ": " + dmg, CurX, CurY );
                    } else {
                        g.drawString( "Ranges " + srng + " to " + erng + ": " + dmg, CurX, CurY );
                    }
                    CurY += 11;
                }
            }
            CurX += 150;
            curgroups.clear();
        }
    }

    private class Group {
        public int StartRng = 0, EndRng = 0, Dmg = 0;
        public boolean Combine( Group g ) {
            if( Dmg == g.Dmg ) {
                // we should combine
                if( StartRng <= g.StartRng ) {
                    // good...
                    if( EndRng <= g.EndRng ) {
                        // awesome, combine them
                        EndRng = g.EndRng;
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                // can't combine
                return false;
            }
        }
    }
}

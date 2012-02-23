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

import java.util.ArrayList;
import javax.swing.table.DefaultTableCellRenderer;
import components.Ammunition;
import components.Mech;
import components.ifWeapon;
import common.CommonTools;

public class dlgBracketCharts extends javax.swing.JDialog {

    private Mech CurMech;
    private String[][] data;

    /** Creates new form dlgBracketCharts */
    public dlgBracketCharts( java.awt.Frame parent, boolean modal, Mech m ) {
        super( parent, modal );
        CurMech = m;
        GetWeaponData();
        initComponents();
        ResetTable();
    }

    private void GetWeaponData() {
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        ArrayList<ifWeapon> wep = new ArrayList<ifWeapon>();
        ArrayList<Ammunition> ammo = new ArrayList<Ammunition>();
        ArrayList<WeaponInfo> temp = new ArrayList<WeaponInfo>();
        int cols = 0;
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof ifWeapon ) {
                ifWeapon w = (ifWeapon) v.get( i );
                // do we already have a weapon of this name in the ArrayList?
                boolean add = true;
                for( int x = 0; x < wep.size(); x++ ) {
                    if( wep.get( x ).LookupName().equals( w.LookupName() ) ) {
                        add = false;
                    }
                }
                if( add ) {
                    wep.add( w );
                    if( w.GetRangeLong() > cols ) { cols = w.GetRangeLong(); }
                }
            } else if( v.get( i ) instanceof Ammunition ) {
                Ammunition a = (Ammunition) v.get( i );
                // do we already have an ammo of this name in the ArrayList?
                boolean add = true;
                for( int x = 0; x < ammo.size(); x++ ) {
                    if( ammo.get( x ).LookupName().equals( a.LookupName() ) ) {
                        add = false;
                    }
                }
                if( add ) {
                    ammo.add( a );
                    if( a.GetLongRange() > cols ) { cols = a.GetLongRange(); }
                }
            }
        }

        // construct the data ArrayList
        for( int i = 0; i < wep.size(); i++ ) {
            ifWeapon w = wep.get( i );
            if( w.HasAmmo() ) {
                boolean added = false;
                // search for other ammunition for this weapon.
                for( int x = ammo.size() - 1; x >= 0; x-- ) {
                    Ammunition a = ammo.get( x );
                    if( w.GetAmmoIndex() == a.GetAmmoIndex() ) {
                        temp.add( new WeaponInfo( w, a ) );
                        ammo.remove( a );
                        added = true;
                    }
                }
                if( ! added ) {
                    // add the weapon in by itself
                    temp.add( new WeaponInfo( w ) );
                }
            } else {
                temp.add( new WeaponInfo( w ) );
            }
        }

        // sort the weapon info by range
        SortWeapons( temp );

        // turn the temporary ArrayList into an array
        cols += 1;
        data = new String[temp.size()*2][cols];
        for( int i = 0; i < temp.size(); i++ ) {
            WeaponInfo w = temp.get( i );
            for( int x = 0; x < cols; x++ ) {
                if( x == 0 ) {
                    data[i*2][x] = w.GetName();
                    data[i*2+1][x] = "";
                } else {
                    int tohit = w.GetToHit( x );
                    if( CurMech.UsingTC() ) {
                        if( tohit != 12 && w.CanUseTC() ) { tohit -= 1; }
                    }
                    if( tohit >= 0 ) {
                        data[i*2][x] = "+" + tohit;
                    } else {
                        data[i*2][x] = "" + tohit;
                    }
                    data[i*2+1][x] = "" + w.GetDamage( x );
                }
            }
        }
    }

    private void ResetTable() {
        tblBrackets.setDefaultRenderer( Object.class, new BracketRenderer() );
        tblBrackets.setModel( new javax.swing.table.AbstractTableModel() {
            @Override
            public String getColumnName( int col ) {
                if( col == 0 ) {
                    return "Weapon Name";
                } else {
                    return "" + col;
                }
            }
            public int getRowCount() { return data.length; }
            public int getColumnCount() { return data[0].length; }
            public Object getValueAt( int row, int col ) {
                if( row % 2 == 1 ) {
                    // damage
                    if( data[row][col].equals( "0" ) ) {
                        return "-";
                    } else {
                        return data[row][col];
                    }
                } else {
                    // to-hits
                    if( data[row][col].equals( "+12" ) ) {
                        return "-";
                    } else {
                        return data[row][col];
                    }
                }
            }
        } );
        for( int i = 1; i < tblBrackets.getColumnCount(); i++ ) {
            tblBrackets.getColumnModel().getColumn( i ).setPreferredWidth( 35 );
            tblBrackets.getColumnModel().getColumn( i ).setMinWidth( 35 );
            tblBrackets.getColumnModel().getColumn( i ).setMaxWidth( 35 );
        }
        tblBrackets.getColumnModel().getColumn( 0 ).setCellRenderer( new DefaultTableCellRenderer() );
        tblBrackets.getColumnModel().getColumn( 0 ).setMinWidth( 125 );
    }

    private void SortWeapons( ArrayList<WeaponInfo> v ) {
        // sort by longest range (using gnomesort for less code.  may have to change
        // this depending on the slowness of the program.  I figure lower overhead
        // will have better results at this time, and mechs typically don't
        // carry more than twelve to fifteen weapons.  I'll have to test this.
        int i = 1, j = 2;
        WeaponInfo swap;
        while( i < v.size() ) {
            // get the two items we'll be comparing
            if( v.get( i - 1 ).GetLongRange() >= v.get( i ).GetLongRange() ) {
                i = j;
                j += 1;
            } else {
                swap = v.get( i - 1 );
                v.set( i - 1, v.get( i ) );
                v.set( i, swap );
                i -= 1;
                if( i == 0 ) {
                    i = 1;
                }
            }
        }
    }

    private class BracketRenderer extends DefaultTableCellRenderer {
        @Override
        public int getHorizontalAlignment() {
            return javax.swing.SwingConstants.CENTER;
        }

        @Override
        public int getVerticalAlignment() {
            return javax.swing.SwingConstants.CENTER;
        }
    }

    private class WeaponInfo {
        private ifWeapon wep;
        private Ammunition ammo;

        public WeaponInfo( ifWeapon w ) { wep = w; }
        public WeaponInfo( ifWeapon w, Ammunition a ) { wep = w; ammo = a; }

        public String GetName() {
            if( HasAmmo() ) { return ammo.CritName().replace( "@", "" ); }
            return wep.CritName();
        }

        public boolean HasAmmo() {
            if( ammo == null ) { return false; }
            return true;
        }

        public int GetToHit( int range ) {
            if( HasAmmo() ) {
                return CommonTools.GetToHitAtRange( ammo, range );
            }
            return CommonTools.GetToHitAtRange( wep, range );
        }

        public int GetDamage( int range ) {
            if( HasAmmo() ) {
                return CommonTools.GetAverageDamageAtRange( wep, ammo, range );
            }
            return CommonTools.GetAverageDamageAtRange( wep, range );
        }

        public int GetLongRange() {
            if( HasAmmo() ) { return ammo.GetLongRange(); }
            return wep.GetRangeLong();
        }

        public boolean CanUseTC() {
            return wep.IsTCCapable();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblBrackets = new javax.swing.JTable();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tblBrackets.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblBrackets.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblBrackets.setAutoscrolls(false);
        jScrollPane1.setViewportView(tblBrackets);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
                    .addComponent(btnClose))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblBrackets;
    // End of variables declaration//GEN-END:variables

}

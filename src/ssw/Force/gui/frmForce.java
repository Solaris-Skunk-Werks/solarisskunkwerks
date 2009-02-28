/*
Copyright (c) 2008, George Blouin Jr. (skyhigh@solaris7.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.
    * Neither the name of George Blouin Jr nor the names of contributors may be
used to endorse or promote products derived from this software without specific prior
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ssw.Force.gui;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;
import ssw.Force.*;
import ssw.Force.IO.ForceReader;
import ssw.Force.IO.ForceWriter;
import ssw.Force.IO.PrintSheet;
import ssw.components.Mech;
import ssw.filehandlers.XMLReader;
import ssw.gui.frmMain;
import ssw.print.Printer;


public class frmForce extends javax.swing.JFrame {
    public Force force = new Force();
    private frmMain parent;

    /** Creates new form frmForce */
    public frmForce(frmMain parent) {
        initComponents();

        this.parent = parent;

        refreshTable();
    }

    private void refreshTable() {
        tblForce.setModel(force);

        //Create a sorting class and apply it to the list
        TableRowSorter Leftsorter = new TableRowSorter<Force>(force);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(5, SortOrder.ASCENDING));
        Leftsorter.setSortKeys(sortKeys);
        tblForce.setRowSorter(Leftsorter);

        tblForce.getColumnModel().getColumn(0).setPreferredWidth(200);
        tblForce.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblForce.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblForce.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblForce.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblForce.getColumnModel().getColumn(5).setPreferredWidth(50);
    }

    private void LoadMech() {
        Unit Data = (Unit) ((Force) tblForce.getModel()).Units.get( tblForce.convertRowIndexToModel( tblForce.getSelectedRow() ) );
        try
        {
            XMLReader read = new XMLReader();
            Mech m = read.ReadMech(Data.Filename);
            //if (m.isOmni()) {
                //m.SetCurLoadout( Data.getConfig() );
            //}
            parent.setMech(m);

            //parent.Prefs.put( "LastOpenDirectory", Data.getFilename().substring( 0, Data.getFilename().lastIndexOf( File.separator ) ) );
            //parent.Prefs.put( "LastOpenFile", Data.getFilename().substring( Data.getFilename().lastIndexOf( File.separator ) ) );

            parent.LoadMechIntoGUI();
            this.setVisible(false);

        } catch ( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this.parent, e.getMessage() );
        }
    }

    public void Add( Mech m ) {
        Unit u = new Unit();
        u.TypeModel = m.GetFullName();
        u.Tonnage = m.GetTonnage();
        u.BaseBV = m.GetCurrentBV();
        u.m = m;
        force.Units.add(u);
    }

    public void updateSkills() {
        if (tblForce.getSelectedRowCount() > 0) {
            int[] rows = tblForce.getSelectedRows();
            for ( int i=0; i < rows.length; i++ ) {
                Unit data = (Unit) force.Units.get(tblForce.convertRowIndexToModel(rows[i]));
                data.Gunnery = Integer.parseInt(txtGunnery.getText());
                data.Piloting = Integer.parseInt(txtPiloting.getText());
                data.Refresh();
            }
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

        tlbActions = new javax.swing.JToolBar();
        btnOpen = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnPrintForce = new javax.swing.JButton();
        btnPrintUnits = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnRemoveUnit = new javax.swing.JButton();
        brnClearForce = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        spnList = new javax.swing.JScrollPane();
        tblForce = new javax.swing.JTable();
        lblUnit = new javax.swing.JLabel();
        txtMechwarrior = new javax.swing.JTextField();
        txtGunnery = new javax.swing.JTextField();
        txtPiloting = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setTitle("Force List");
        setMinimumSize(new java.awt.Dimension(800, 500));
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tlbActions.setFloatable(false);
        tlbActions.setRollover(true);

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/folder.gif"))); // NOI18N
        btnOpen.setToolTipText("Open Force");
        btnOpen.setFocusable(false);
        btnOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        tlbActions.add(btnOpen);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_save.gif"))); // NOI18N
        btnSave.setToolTipText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        tlbActions.add(btnSave);
        tlbActions.add(jSeparator2);

        btnPrintForce.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_print.gif"))); // NOI18N
        btnPrintForce.setToolTipText("Print Force List");
        btnPrintForce.setFocusable(false);
        btnPrintForce.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrintForce.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintForce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintForceActionPerformed(evt);
            }
        });
        tlbActions.add(btnPrintForce);

        btnPrintUnits.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_print_batch.gif"))); // NOI18N
        btnPrintUnits.setToolTipText("Print Selected Units");
        btnPrintUnits.setFocusable(false);
        btnPrintUnits.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrintUnits.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintUnitsActionPerformed(evt);
            }
        });
        tlbActions.add(btnPrintUnits);
        tlbActions.add(jSeparator1);

        btnRemoveUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/mech_delete.gif"))); // NOI18N
        btnRemoveUnit.setToolTipText("Remove Unit");
        btnRemoveUnit.setFocusable(false);
        btnRemoveUnit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemoveUnit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveUnitActionPerformed(evt);
            }
        });
        tlbActions.add(btnRemoveUnit);

        brnClearForce.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_stop.gif"))); // NOI18N
        brnClearForce.setToolTipText("Clear Force List");
        brnClearForce.setFocusable(false);
        brnClearForce.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        brnClearForce.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        brnClearForce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brnClearForceActionPerformed(evt);
            }
        });
        tlbActions.add(brnClearForce);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_refresh_blue.gif"))); // NOI18N
        btnRefresh.setToolTipText("Refresh Force List");
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        tlbActions.add(btnRefresh);

        tblForce.setModel(new javax.swing.table.DefaultTableModel(
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
        tblForce.setGridColor(new java.awt.Color(204, 204, 204));
        tblForce.setRowMargin(2);
        tblForce.setShowVerticalLines(false);
        tblForce.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblForceMouseClicked(evt);
            }
        });
        spnList.setViewportView(tblForce);

        txtGunnery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGunneryActionPerformed(evt);
            }
        });
        txtGunnery.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtGunneryFocusGained(evt);
            }
        });
        txtGunnery.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtGunneryInputMethodTextChanged(evt);
            }
        });
        txtGunnery.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtGunneryKeyTyped(evt);
            }
        });

        txtPiloting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPilotingActionPerformed(evt);
            }
        });
        txtPiloting.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPilotingFocusGained(evt);
            }
        });
        txtPiloting.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtPilotingInputMethodTextChanged(evt);
            }
        });
        txtPiloting.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPilotingKeyTyped(evt);
            }
        });

        jLabel2.setText("Mechwarrior");

        jLabel3.setText("Gunnery");

        jLabel4.setText("Piloting");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tlbActions, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spnList, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(145, 145, 145)
                .addComponent(lblUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtMechwarrior, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, 0, 0, Short.MAX_VALUE)
                    .addComponent(txtPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(284, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tlbActions, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(spnList, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUnit, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(txtMechwarrior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        refreshTable();
    }//GEN-LAST:event_formWindowOpened

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refreshTable();
}//GEN-LAST:event_btnRefreshActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        refreshTable();
    }//GEN-LAST:event_formWindowGainedFocus

    private void btnPrintUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintUnitsActionPerformed
        if ( tblForce.getSelectedRowCount() > 0 ) {
            Printer print = new Printer();

            try
            {
                XMLReader read = new XMLReader();
                int[] rows = tblForce.getSelectedRows();
                for ( int i=0; i < rows.length; i++ ) {
                    Unit data = (Unit) force.Units.get(tblForce.convertRowIndexToModel(rows[i]));
                    Mech m = read.ReadMech(data.Filename);
                    //if (data.isOmni()) {
                    //    m.SetCurLoadout(data.getConfig());
                    //}
                    print.AddMech(m);
                }
                print.Print();
                tblForce.clearSelection();

            } catch ( Exception e ) {

            }
        }
    }//GEN-LAST:event_btnPrintUnitsActionPerformed

    private void tblForceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblForceMouseClicked
        if (evt.getClickCount() >= 2) { LoadMech(); }

        Unit u = (Unit) force.Units.get(tblForce.convertRowIndexToModel(tblForce.getSelectedRow()));
        lblUnit.setText(u.TypeModel);
        txtMechwarrior.setText(u.Mechwarrior);
        txtGunnery.setText(u.Gunnery + "");
        txtPiloting.setText(u.Piloting + "");
    }//GEN-LAST:event_tblForceMouseClicked

    private void brnClearForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brnClearForceActionPerformed
        if (javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you want to clear the entire list?") == javax.swing.JOptionPane.YES_OPTION) {
            force.Clear();
            refreshTable();
        }
    }//GEN-LAST:event_brnClearForceActionPerformed

    private void btnRemoveUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveUnitActionPerformed
        if ( tblForce.getSelectedRowCount() > 0 ) {
            int[] rows = tblForce.getSelectedRows();
            for ( int i=0; i < rows.length; i++ ) {
                Unit data = (Unit) force.Units.get(tblForce.convertRowIndexToModel(rows[i]));
                force.Units.remove(data);
            }
            refreshTable();
        }
    }//GEN-LAST:event_btnRemoveUnitActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        String forceName = javax.swing.JOptionPane.showInputDialog("What would you like to name this force?");
        if (! forceName.isEmpty() ) {
            force.ForceName = forceName;
            ForceWriter writer = new ForceWriter(force);
            try
            {
                writer.SerializeForce(force);
            } catch ( IOException ie ) {
                javax.swing.JOptionPane.showMessageDialog(this, ie.getMessage());
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        ForceReader reader = new ForceReader();
        force = reader.Load();
        refreshTable();
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnPrintForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintForceActionPerformed
       force.RefreshBV();
       PrinterJob job = PrinterJob.getPrinterJob();
       PrintSheet p = new PrintSheet(576, 756, force);
       Paper paper = new Paper();
       paper.setImageableArea(18, 18, 576, 756 );
       PageFormat page = new PageFormat();
       page.setPaper( paper );
       job.setPrintable( p, page );
       boolean DoPrint = job.printDialog();
       if( DoPrint ) {
           try {
               job.print();
           } catch( PrinterException e ) {
               System.err.println( e.getMessage() );
               System.out.println( e.getStackTrace() );
           }
       }
    }//GEN-LAST:event_btnPrintForceActionPerformed

    private void txtGunneryInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtGunneryInputMethodTextChanged
        updateSkills();
    }//GEN-LAST:event_txtGunneryInputMethodTextChanged

    private void txtPilotingInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtPilotingInputMethodTextChanged
        updateSkills();
    }//GEN-LAST:event_txtPilotingInputMethodTextChanged

    private void txtGunneryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGunneryActionPerformed
        updateSkills();
    }//GEN-LAST:event_txtGunneryActionPerformed

    private void txtPilotingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPilotingActionPerformed
        updateSkills();
    }//GEN-LAST:event_txtPilotingActionPerformed

    private void txtGunneryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGunneryFocusGained
        txtGunnery.selectAll();
    }//GEN-LAST:event_txtGunneryFocusGained

    private void txtPilotingFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPilotingFocusGained
        txtPiloting.selectAll();
    }//GEN-LAST:event_txtPilotingFocusGained

    private void txtPilotingKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPilotingKeyTyped
        updateSkills();
    }//GEN-LAST:event_txtPilotingKeyTyped

    private void txtGunneryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGunneryKeyTyped
        updateSkills();
    }//GEN-LAST:event_txtGunneryKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton brnClearForce;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnPrintForce;
    private javax.swing.JButton btnPrintUnits;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRemoveUnit;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JLabel lblUnit;
    private javax.swing.JScrollPane spnList;
    private javax.swing.JTable tblForce;
    private javax.swing.JToolBar tlbActions;
    private javax.swing.JTextField txtGunnery;
    private javax.swing.JTextField txtMechwarrior;
    private javax.swing.JTextField txtPiloting;
    // End of variables declaration//GEN-END:variables

}

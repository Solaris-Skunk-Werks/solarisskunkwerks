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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableRowSorter;
import ssw.Force.*;
import ssw.Force.IO.ForceReader;
import ssw.Force.IO.ForceWriter;
import ssw.Force.IO.PrintSheet;
import ssw.components.Mech;
import ssw.filehandlers.MTFWriter;
import ssw.filehandlers.MULWriter;
import ssw.filehandlers.XMLReader;
import ssw.gui.dlgAmmoChooser;
import ssw.gui.frmMain;
import ssw.print.Printer;


public class frmForce extends javax.swing.JFrame {
    public Force force = new Force();
    private frmMain parent;

    /** Creates new form frmForce */
    public frmForce(frmMain parent) {
        initComponents();

        this.parent = parent;
        force.RefreshBV();
        refreshTable();
        sortTable();

        force.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                lblTotalBV.setText(String.format("%1$,.0f", force.TotalAdjustedBV));
                lblTotalTons.setText(String.format("%1$,.0f", force.TotalTonnage) + " Tons");
                lblTotalUnits.setText(force.Units.size() + " Units");
            }
        });
    }

    private void refreshTable() {
        tblForce.setModel(force);
    }

    private void sortTable() {
        //Create a sorting class and apply it to the list
        TableRowSorter Leftsorter = new TableRowSorter<Force>(force);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        Leftsorter.setSortKeys(sortKeys);
        tblForce.setRowSorter(Leftsorter);

        //tblForce.getColumnModel().getColumn(0).setPreferredWidth(150);
        //tblForce.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblForce.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblForce.getColumnModel().getColumn(3).setPreferredWidth(25);
        tblForce.getColumnModel().getColumn(4).setPreferredWidth(25);
        tblForce.getColumnModel().getColumn(5).setPreferredWidth(50);

        force.RefreshBV();
        lblTotalBV.setText(String.format("%1$,.0f", force.TotalAdjustedBV));
        lblTotalTons.setText(String.format("%1$,.0f", force.TotalTonnage) + " Tons");
        lblTotalUnits.setText(force.Units.size() + " Units");
    }

    private void LoadMech() {
        Unit Data = (Unit) ((Force) tblForce.getModel()).Units.get( tblForce.convertRowIndexToModel( tblForce.getSelectedRow() ) );
        try
        {
            XMLReader read = new XMLReader();
            Mech m = read.ReadMech( Data.Filename, parent.data );
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
        btnExportMUL = new javax.swing.JButton();
        btnExportMTFs = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnAddMech = new javax.swing.JButton();
        btnRemoveUnit = new javax.swing.JButton();
        brnClearForce = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnAmmoChooser = new javax.swing.JButton();
        spnList = new javax.swing.JScrollPane();
        tblForce = new javax.swing.JTable();
        lblTotalUnits = new javax.swing.JLabel();
        lblTotalTons = new javax.swing.JLabel();
        lblTotalBV = new javax.swing.JLabel();
        txtGunnery = new javax.swing.JTextField();
        txtPiloting = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setTitle("Force List");
        setMinimumSize(null);
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

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/folder-open-document.png"))); // NOI18N
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

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/disk-black.png"))); // NOI18N
        btnSave.setToolTipText("Save Force");
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

        btnPrintForce.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/printer.png"))); // NOI18N
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

        btnPrintUnits.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/printer--plus.png"))); // NOI18N
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

        btnExportMUL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/document--arrow.png"))); // NOI18N
        btnExportMUL.setToolTipText("Export MUL");
        btnExportMUL.setFocusable(false);
        btnExportMUL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportMUL.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportMUL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportMULActionPerformed(evt);
            }
        });
        tlbActions.add(btnExportMUL);

        btnExportMTFs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/documents--arrow.png"))); // NOI18N
        btnExportMTFs.setToolTipText("Export All to MTF");
        btnExportMTFs.setFocusable(false);
        btnExportMTFs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportMTFs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportMTFs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportMTFsActionPerformed(evt);
            }
        });
        tlbActions.add(btnExportMTFs);
        tlbActions.add(jSeparator3);

        btnAddMech.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/clipboard--plus.png"))); // NOI18N
        btnAddMech.setToolTipText("Add Unit");
        btnAddMech.setFocusable(false);
        btnAddMech.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddMech.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMechActionPerformed(evt);
            }
        });
        tlbActions.add(btnAddMech);

        btnRemoveUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/clipboard--minus.png"))); // NOI18N
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

        brnClearForce.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/clipboard-empty.png"))); // NOI18N
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

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/arrow-circle-double.png"))); // NOI18N
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
        tlbActions.add(jSeparator4);

        btnAmmoChooser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/ammo.png"))); // NOI18N
        btnAmmoChooser.setFocusable(false);
        btnAmmoChooser.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAmmoChooser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAmmoChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAmmoChooserActionPerformed(evt);
            }
        });
        tlbActions.add(btnAmmoChooser);

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

        lblTotalUnits.setText("0 Units");

        lblTotalTons.setText("0 Tons");

        lblTotalBV.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalBV.setText("0 BV");

        txtGunnery.setText("4");
        txtGunnery.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtGunneryFocusGained(evt);
            }
        });
        txtGunnery.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtGunneryKeyReleased(evt);
            }
        });

        txtPiloting.setText("5");
        txtPiloting.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPilotingFocusGained(evt);
            }
        });
        txtPiloting.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPilotingKeyReleased(evt);
            }
        });

        jLabel1.setText("G");

        jLabel2.setText("P");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tlbActions, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spnList, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblTotalUnits, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
                .addComponent(lblTotalTons, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalBV, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tlbActions, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnList, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalUnits)
                    .addComponent(lblTotalBV)
                    .addComponent(lblTotalTons)
                    .addComponent(txtGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        force.RefreshBV();
        refreshTable();
    }//GEN-LAST:event_formWindowOpened

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        force.RefreshBV();
        refreshTable();
}//GEN-LAST:event_btnRefreshActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        force.RefreshBV();
        refreshTable();
    }//GEN-LAST:event_formWindowGainedFocus

    private void btnPrintUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintUnitsActionPerformed
        if ( tblForce.getSelectedRowCount() == 0 ) {
            tblForce.selectAll();
        }

        if ( tblForce.getSelectedRowCount() > 0 ) {
            Printer print = new Printer( parent );

            try
            {
                //XMLReader read = new XMLReader();
                int[] rows = tblForce.getSelectedRows();
                for ( int i=0; i < rows.length; i++ ) {
                    Unit data = (Unit) force.Units.get(tblForce.convertRowIndexToModel(rows[i]));
                    //Mech m = read.ReadMech( data.Filename, parent.data );
                    Mech m = data.m;
                    if ( data.isOmni() ) {
                        m.SetCurLoadout( data.Configuration );
                    }
                    print.AddMech(m, data.Mechwarrior, data.Gunnery, data.Piloting);
                }
                print.Print();
                tblForce.clearSelection();

            } catch ( Exception e ) {
                System.err.println( e.getMessage() );
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnPrintUnitsActionPerformed

    private void tblForceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblForceMouseClicked
        if (evt.getClickCount() >= 2) { LoadMech(); }
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
        reader.setForce(force);
        force = reader.Load();
        refreshTable();
        sortTable();
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

    private void btnExportMULActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportMULActionPerformed
        MULWriter mw = new MULWriter(force);
        ssw.filehandlers.Media media = new ssw.filehandlers.Media();
        File mulFile = media.SelectFile( parent.Prefs.get( "MTFExportPath", "" ), "mul", "Select MUL file" );
        try {
            String filename = mulFile.getCanonicalPath();
            if ( ! filename.endsWith(".mul") ) { filename += ".mul"; }
            mw.WriteXML(filename);
            javax.swing.JOptionPane.showMessageDialog(this, filename + " saved.");
        } catch (IOException ex) {
            Logger.getLogger(frmForce.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnExportMULActionPerformed

    private void btnAddMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMechActionPerformed
        parent.dOpen.setVisible(true);
}//GEN-LAST:event_btnAddMechActionPerformed

    private void btnExportMTFsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportMTFsActionPerformed
        String error = "",
               filename = "";
        MTFWriter mtf = new MTFWriter();
        ssw.filehandlers.Media media = new ssw.filehandlers.Media();
        String mtfDir = media.GetDirectorySelection(null, parent.Prefs.get( "MTFExportPath", "" ) );
        if (!mtfDir.endsWith(File.separator)) { mtfDir += File.separator; }
        
        for ( int i = 0; i < force.Units.size(); i++ ) {
            Unit u = (Unit) force.Units.get(i);
            u.LoadMech();
            mtf.setMech(u.m);
            try {
                filename = mtfDir + u.m.GetFullName() + ".mtf";
                mtf.WriteMTF(filename);
            } catch (IOException ie) {
                error += "Attempted " + filename + ": " + ie.getMessage() + "\n";
            }
        }
        
        if ( !error.isEmpty() ) {
            javax.swing.JOptionPane.showMessageDialog(this, error);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "All units have been saved to MTF.");
        }
    }//GEN-LAST:event_btnExportMTFsActionPerformed

    private void txtGunneryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGunneryKeyReleased
        if ( !txtGunnery.getText().isEmpty() ) {
            for ( int i = 0; i < force.Units.size(); i++ ) {
                Unit u = (Unit) force.Units.get(i);
                u.Gunnery = Integer.parseInt(txtGunnery.getText());
                u.Refresh();
            }
        }
        sortTable();
    }//GEN-LAST:event_txtGunneryKeyReleased

    private void txtPilotingKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPilotingKeyReleased
        if ( !txtPiloting.getText().isEmpty() ) {
            for ( int i = 0; i < force.Units.size(); i++ ) {
                Unit u = (Unit) force.Units.get(i);
                u.Piloting = Integer.parseInt(txtPiloting.getText());
                u.Refresh();
            }
        }
        sortTable();
    }//GEN-LAST:event_txtPilotingKeyReleased

    private void txtGunneryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGunneryFocusGained
        txtGunnery.selectAll();
    }//GEN-LAST:event_txtGunneryFocusGained

    private void txtPilotingFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPilotingFocusGained
        txtPiloting.selectAll();
    }//GEN-LAST:event_txtPilotingFocusGained

    private void btnAmmoChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAmmoChooserActionPerformed
        if ( tblForce.getSelectedRowCount() > 0 ) {
            int[] rows = tblForce.getSelectedRows();
            for ( int i=0; i < rows.length; i++ ) {
                try {
                    Unit data = (Unit) force.Units.get( tblForce.convertRowIndexToModel( rows[i] ) );
                    dlgAmmoChooser Ammo = new dlgAmmoChooser( this, false, data.m, parent.data );
                    Ammo.setLocationRelativeTo( this );
                    if( Ammo.HasAmmo() ) {
                        Ammo.setVisible( true );
                    } else {
                        javax.swing.JOptionPane.showMessageDialog( this, "This 'Mech has no ammunition to exchange." );
                        Ammo.dispose();
                    }
                } catch( Exception e ) {
                    javax.swing.JOptionPane.showMessageDialog(this, "There was an error altering the ammunition on this 'Mech:\n" + e.getMessage() );
                }
            }
            refreshTable();
        }
    }//GEN-LAST:event_btnAmmoChooserActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton brnClearForce;
    private javax.swing.JButton btnAddMech;
    private javax.swing.JButton btnAmmoChooser;
    private javax.swing.JButton btnExportMTFs;
    private javax.swing.JButton btnExportMUL;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnPrintForce;
    private javax.swing.JButton btnPrintUnits;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRemoveUnit;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JLabel lblTotalBV;
    private javax.swing.JLabel lblTotalTons;
    private javax.swing.JLabel lblTotalUnits;
    private javax.swing.JScrollPane spnList;
    private javax.swing.JTable tblForce;
    private javax.swing.JToolBar tlbActions;
    private javax.swing.JTextField txtGunnery;
    private javax.swing.JTextField txtPiloting;
    // End of variables declaration//GEN-END:variables

}

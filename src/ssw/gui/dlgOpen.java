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

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;
import ssw.Force.Unit;
import ssw.Options;
import ssw.components.Mech;
import ssw.filehandlers.*;
import ssw.print.Printer;

public class dlgOpen extends javax.swing.JFrame {
    private frmMain parent;
    private Options opts = new Options();
    private MechList list;
    private Media media = new Media();
    private String dirPath = "";

    /** Creates new form dlgOpen */
    public dlgOpen(java.awt.Frame parent, boolean modal) {
        initComponents();
        this.parent = (frmMain) parent;

        cmbTech.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Tech", "Clan", "Inner Sphere" }));
        cmbEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Era", "Age of War/Star League", "Succession Wars", "Clan Invasion" }));
    }

    private void LoadMech() {
        MechListData Data = ((MechList) tblMechData.getModel()).Get( tblMechData.convertRowIndexToModel( tblMechData.getSelectedRow() ) );
        try
        {
            XMLReader read = new XMLReader();
            Mech m = read.ReadMech(Data.getFilename());
            if (Data.isOmni()) {
                m.SetCurLoadout( Data.getConfig() );
            }
            parent.setMech(m);

            parent.Prefs.put( "LastOpenDirectory", Data.getFilename().substring( 0, Data.getFilename().lastIndexOf( File.separator ) ) );
            parent.Prefs.put( "LastOpenFile", Data.getFilename().substring( Data.getFilename().lastIndexOf( File.separator ) ) ); 

            this.setVisible(false);

        } catch ( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this.parent, e.getMessage() );
        }
    }

    private void Calculate() {
        txtSelected.setText("0 Units Selected for 0 BV and 0 C-Bills");

        int BV = 0;
        float Cost = 0;

        int[] rows = tblMechData.getSelectedRows();
        for ( int i=0; i < rows.length; i++ ) {
            MechListData data = list.Get(tblMechData.convertRowIndexToModel(rows[i]));
            BV += data.getBV();
            Cost += data.getCost();
        }

        txtSelected.setText(rows.length + " Units Selected for " + String.format("%,d", BV) + " BV and " + String.format("%,.2f", Cost) + " C-Bills");
    }

    public void LoadList() {
        if (dirPath.isEmpty()) {
            dirPath = parent.Prefs.get("ListPath", opts.SaveLoadPath);
        }

        list = new MechList();

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        FileList fl = new FileList(dirPath);
        for ( int i=0; i <= fl.length()-1; i++ ) {
            File f = fl.getFiles()[i];
            try
            {
                list.Add(f);
            } catch (Exception e) {
                
            }
        }

        if (list.Size() > 0) {
            setupList(list);
        }

        String displayPath = dirPath;
        if (! dirPath.isEmpty() ) {
            if (dirPath.contains(File.separator)) {
                displayPath = dirPath.substring(0, 3) + "..." + dirPath.substring(dirPath.lastIndexOf(File.separator)) + "";
            }
        }
        this.lblLoading.setText(list.Size() + " Mechs loaded from " + displayPath);
        this.lblLoading.setToolTipText(dirPath);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void setupList(MechList mechList) {
        tblMechData.setModel(mechList);

        //Create a sorting class and apply it to the list
        TableRowSorter sorter = new TableRowSorter<MechList>(mechList);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        tblMechData.setRowSorter(sorter);

        tblMechData.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblMechData.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblMechData.getColumnModel().getColumn(2).setPreferredWidth(40);
        tblMechData.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblMechData.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblMechData.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblMechData.getColumnModel().getColumn(6).setPreferredWidth(60);
    }

    private void checkSelection() {
        if ( tblMechData.getSelectedRowCount() > 0 ) {
            Calculate();
            btnOpen.setEnabled(true);
            btnPrint.setEnabled(true);
            btnOpenMech.setEnabled(true);
            btnAdd2Force.setEnabled(true);
        } else {
            btnOpen.setEnabled(false);
            btnPrint.setEnabled(false);
            btnOpenMech.setEnabled(false);
            btnAdd2Force.setEnabled(false);
            txtSelected.setText("0 Units Selected for 0 BV and 0 C-Bills");
        }
    }

    private void batchUpdateMechs() {
        int Response = javax.swing.JOptionPane.showConfirmDialog(this, "This will open and re-save each file in the current directory so that all files are updated with current BV and Cost calculations.\nThis process could take a few minutes, are you ready?", "Batch Mech Processing", javax.swing.JOptionPane.YES_NO_OPTION);
        if (Response == javax.swing.JOptionPane.YES_OPTION) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try
            {
                XMLReader read = new XMLReader();
                FileList List = new FileList(dirPath);
                File[] Files = List.getFiles();

                for ( int i=0; i < Files.length; i++ ) {
                    File file = Files[i];
                    if (file.isFile() && file.getCanonicalPath().endsWith(".ssw")) {
                        try
                        {
                            Mech m = read.ReadMech(file.getCanonicalPath());

                            // save the mech to XML in the current location
                            XMLWriter writer = new XMLWriter( m );
                            try {
                                writer.WriteXML( file.getCanonicalPath() );
                            } catch( IOException e ) {
                                //do nothing
                            }
                        } catch ( Exception e ) {
                            //do nothing
                        }
                    }
                }

                LoadList();

            } catch (Exception e) {
                //do nothing
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
        btnPrint = new javax.swing.JButton();
        btnAdd2Force = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnOptions = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnMagic = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        spnMechTable = new javax.swing.JScrollPane();
        tblMechData = new javax.swing.JTable();
        txtSelected = new javax.swing.JLabel();
        btnOpenMech = new javax.swing.JButton();
        lblLoading = new javax.swing.JLabel();
        cmbTech = new javax.swing.JComboBox();
        cmbEra = new javax.swing.JComboBox();
        btnOpenDir = new javax.swing.JButton();
        txtMinBV = new javax.swing.JTextField();
        txtMaxBV = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnFilter = new javax.swing.JButton();
        txtMinCost = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtMaxCost = new javax.swing.JTextField();
        btnClearFilter = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Mech(s)");
        setMinimumSize(new java.awt.Dimension(600, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tlbActions.setFloatable(false);
        tlbActions.setRollover(true);

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/folder.gif"))); // NOI18N
        btnOpen.setToolTipText("Open Mech");
        btnOpen.setEnabled(false);
        btnOpen.setFocusable(false);
        btnOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        tlbActions.add(btnOpen);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_print.gif"))); // NOI18N
        btnPrint.setToolTipText("Print Selected Mechs");
        btnPrint.setEnabled(false);
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        tlbActions.add(btnPrint);

        btnAdd2Force.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/mech_add.gif"))); // NOI18N
        btnAdd2Force.setToolTipText("Add to Force List");
        btnAdd2Force.setEnabled(false);
        btnAdd2Force.setFocusable(false);
        btnAdd2Force.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd2Force.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAdd2Force.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdd2ForceActionPerformed(evt);
            }
        });
        tlbActions.add(btnAdd2Force);
        tlbActions.add(jSeparator1);

        btnOptions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/icon_settings.gif"))); // NOI18N
        btnOptions.setToolTipText("Change Options");
        btnOptions.setFocusable(false);
        btnOptions.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOptions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOptionsActionPerformed(evt);
            }
        });
        tlbActions.add(btnOptions);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_refresh_blue.gif"))); // NOI18N
        btnRefresh.setToolTipText("Refresh Mech List");
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        tlbActions.add(btnRefresh);
        tlbActions.add(jSeparator2);

        btnMagic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/icon_wand.gif"))); // NOI18N
        btnMagic.setToolTipText("Update Mech Files (Long Process!)");
        btnMagic.setFocusable(false);
        btnMagic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMagic.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMagic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMagicActionPerformed(evt);
            }
        });
        tlbActions.add(btnMagic);
        tlbActions.add(jSeparator3);

        tblMechData.setAutoCreateRowSorter(true);
        tblMechData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblMechData.setIntercellSpacing(new java.awt.Dimension(4, 4));
        tblMechData.setShowVerticalLines(false);
        tblMechData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMechDataMouseClicked(evt);
            }
        });
        tblMechData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblMechDataKeyPressed(evt);
            }
        });
        spnMechTable.setViewportView(tblMechData);

        txtSelected.setText("0 Units Selected");

        btnOpenMech.setText("Open Mech");
        btnOpenMech.setEnabled(false);
        btnOpenMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenMechActionPerformed(evt);
            }
        });

        lblLoading.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLoading.setText("Loading Mechs....");
        lblLoading.setMaximumSize(new java.awt.Dimension(400, 14));

        cmbTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        cmbEra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        btnOpenDir.setText("Open Dir");
        btnOpenDir.setToolTipText("Change Directory");
        btnOpenDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenDirActionPerformed(evt);
            }
        });

        txtMaxBV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        jLabel1.setText("Technology");

        jLabel2.setText("Era");

        jLabel3.setText("Battle Value");

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        txtMinCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMinCostFilter(evt);
            }
        });

        jLabel4.setText("C-Bill Cost");

        txtMaxCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaxCostFilter(evt);
            }
        });

        btnClearFilter.setText("Clear");
        btnClearFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFilterFilter(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tlbActions, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSelected, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(lblLoading, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(btnOpenDir, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbTech, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbEra, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtMinBV, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMaxBV, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtMinCost, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMaxCost, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClearFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                        .addComponent(btnOpenMech)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spnMechTable, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tlbActions, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOpenDir)
                    .addComponent(lblLoading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnMechTable, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnOpenMech, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cmbTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbEra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnFilter)
                            .addComponent(txtMaxBV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMinCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMaxCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMinBV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnClearFilter))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblMechDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMechDataMouseClicked
        if ( evt.getClickCount() == 2 ) {
            LoadMech();
        } else {
            checkSelection();
        }
    }//GEN-LAST:event_tblMechDataMouseClicked

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        LoadMech();
}//GEN-LAST:event_btnOpenActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (list.Size() == 0) LoadList();
    }//GEN-LAST:event_formWindowOpened

    private void btnOpenMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenMechActionPerformed
        LoadMech();
    }//GEN-LAST:event_btnOpenMechActionPerformed

    private void btnOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOptionsActionPerformed
        dlgOptions dgOptions = new dlgOptions( parent, true );
        dgOptions.setLocationRelativeTo( this );
        dgOptions.setVisible( true );
        opts = new Options();
        this.setVisible(true);
        LoadList();
    }//GEN-LAST:event_btnOptionsActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        LoadList();
        this.setVisible(true);
}//GEN-LAST:event_btnRefreshActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        if ( tblMechData.getSelectedRowCount() > 0 ) {
            Printer print = new Printer();

            try
            {
                XMLReader read = new XMLReader();
                int[] rows = tblMechData.getSelectedRows();
                for ( int i=0; i < rows.length; i++ ) {
                    MechListData data = list.Get(tblMechData.convertRowIndexToModel(rows[i]));
                    Mech m = read.ReadMech(data.getFilename());
                    if (data.isOmni()) {
                        m.SetCurLoadout(data.getConfig());
                    }
                    print.AddMech(m);
                }
                print.Print();
                tblMechData.clearSelection();
                checkSelection();

            } catch ( Exception e ) {

            }
        }

    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnMagicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMagicActionPerformed
        parent.setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );
        batchUpdateMechs();
        parent.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }//GEN-LAST:event_btnMagicActionPerformed

    private void Filter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Filter
        ListFilter filters = new ListFilter();

        if (cmbTech.getSelectedIndex() > 0) {filters.setTech(cmbTech.getSelectedItem().toString());}
        if (cmbEra.getSelectedIndex() > 0) {filters.setEra(cmbEra.getSelectedItem().toString());}
        if (! txtMinBV.getText().isEmpty() ) {
            if ( txtMaxBV.getText().isEmpty() ) {
                filters.setBV(0, Integer.parseInt(txtMinBV.getText()));
            } else {
                filters.setBV(Integer.parseInt(txtMinBV.getText()), Integer.parseInt(txtMaxBV.getText()));
            }
        }
        if (! txtMinCost.getText().isEmpty() ) {
            if ( txtMaxCost.getText().isEmpty() ) {
                filters.setCost(0, Float.parseFloat(txtMinCost.getText()));
            } else {
                filters.setCost(Float.parseFloat(txtMinCost.getText()), Float.parseFloat(txtMaxCost.getText()));
            }
        }
        MechList filtered = list.Filter(filters);
        setupList(filtered);
    }//GEN-LAST:event_Filter

    private void btnOpenDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenDirActionPerformed
        dirPath = media.GetDirectorySelection( parent, dirPath );
        this.setVisible(true);
        parent.Prefs.put("ListPath", dirPath);
        LoadList();
    }//GEN-LAST:event_btnOpenDirActionPerformed

    private void txtMinCostFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMinCostFilter
        // TODO add your handling code here:
}//GEN-LAST:event_txtMinCostFilter

    private void txtMaxCostFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxCostFilter
        // TODO add your handling code here:
}//GEN-LAST:event_txtMaxCostFilter

    private void btnClearFilterFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFilterFilter
        setupList(list);
}//GEN-LAST:event_btnClearFilterFilter

    private void tblMechDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblMechDataKeyPressed
        //javax.swing.JOptionPane.showMessageDialog(this, "You typed a " + evt.getKeyChar());
    }//GEN-LAST:event_tblMechDataKeyPressed

    private void btnAdd2ForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdd2ForceActionPerformed
        if ( tblMechData.getSelectedRowCount() > 0 ) {
            int[] rows = tblMechData.getSelectedRows();
            for ( int i=0; i < rows.length; i++ ) {
                    MechListData data = list.Get(tblMechData.convertRowIndexToModel(rows[i]));
                    parent.dForce.force.Units.add(data.getUnit());
            }
        }

        parent.dForce.setLocationRelativeTo(null);
        parent.dForce.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btnAdd2ForceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd2Force;
    private javax.swing.JButton btnClearFilter;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnMagic;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnOpenDir;
    private javax.swing.JButton btnOpenMech;
    private javax.swing.JButton btnOptions;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox cmbEra;
    private javax.swing.JComboBox cmbTech;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JLabel lblLoading;
    private javax.swing.JScrollPane spnMechTable;
    private javax.swing.JTable tblMechData;
    private javax.swing.JToolBar tlbActions;
    private javax.swing.JTextField txtMaxBV;
    private javax.swing.JTextField txtMaxCost;
    private javax.swing.JTextField txtMinBV;
    private javax.swing.JTextField txtMinCost;
    private javax.swing.JLabel txtSelected;
    // End of variables declaration//GEN-END:variables

}

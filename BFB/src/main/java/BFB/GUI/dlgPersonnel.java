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

package BFB.GUI;

import Force.Warrior;
import Force.Warriors;
import filehandlers.Media;

import IO.XMLWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
// import javax.swing.event.TableModelEvent;
// import javax.swing.event.TableModelListener;

public class dlgPersonnel extends javax.swing.JDialog {
    private static final long serialVersionUID = -5037491851130149787L;

    public Warriors warriors;
    private frmBase parent;

    /*
     * Not used in class it seems
    private TableModelListener ListChanged = new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
            warriors.setupTable(tblWarriors);
        }
    };
    */

    public dlgPersonnel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.parent = (frmBase) parent;
        warriors = new Warriors(this.parent.Prefs.get("LastPSNFile", "data/WarriorList.psn"));
        txtFileName.setText(warriors.getTitle());
        warriors.setupTable(tblWarriors);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnNewFile = new javax.swing.JButton();
        btnLoadFile = new javax.swing.JButton();
        btnSaveFile = new javax.swing.JButton();
        sprOne = new javax.swing.JToolBar.Separator();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnLoadHMP = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        lblStatus = new javax.swing.JLabel();
        spnTable = new javax.swing.JScrollPane();
        tblWarriors = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtGunnery = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPiloting = new javax.swing.JTextField();
        btnAddWarrior = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtRank = new javax.swing.JTextField();
        txtFaction = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtFileName = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Personnel Management");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnNewFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/document--plus.png"))); // NOI18N
        btnNewFile.setToolTipText("New Personnel File");
        btnNewFile.setFocusable(false);
        btnNewFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNewFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNewFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewFileActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNewFile);

        btnLoadFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/folder-open-document.png"))); // NOI18N
        btnLoadFile.setToolTipText("Load Personnel File");
        btnLoadFile.setFocusable(false);
        btnLoadFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLoadFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLoadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadFileActionPerformed(evt);
            }
        });
        jToolBar1.add(btnLoadFile);

        btnSaveFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/disk-black.png"))); // NOI18N
        btnSaveFile.setToolTipText("Save Personnel File");
        btnSaveFile.setFocusable(false);
        btnSaveFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveFileActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSaveFile);
        jToolBar1.add(sprOne);

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Mechwarrior-plus.png"))); // NOI18N
        btnAdd.setToolTipText("Add Warrior");
        btnAdd.setFocusable(false);
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAdd);

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Mechwarrior-pencil.png"))); // NOI18N
        btnEdit.setToolTipText("Edit Warrior");
        btnEdit.setFocusable(false);
        btnEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnEdit);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Mechwarrior-minus.png"))); // NOI18N
        btnDelete.setToolTipText("Delete Warrior");
        btnDelete.setFocusable(false);
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDelete);
        jToolBar1.add(jSeparator1);

        btnLoadHMP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/document--arrow.png"))); // NOI18N
        btnLoadHMP.setToolTipText("Import HMP Warior File");
        btnLoadHMP.setFocusable(false);
        btnLoadHMP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLoadHMP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLoadHMP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadHMPActionPerformed(evt);
            }
        });
        jToolBar1.add(btnLoadHMP);
        jToolBar1.add(jSeparator2);

        lblStatus.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblStatus.setMinimumSize(new java.awt.Dimension(20, 20));
        lblStatus.setPreferredSize(new java.awt.Dimension(20, 20));
        jToolBar1.add(lblStatus);

        tblWarriors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Name", "Affiliation", "Skills"
            }
        ) {
            private static final long serialVersionUID = 741045032946894662L;

            Class<?>[] types = new Class[] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class<?> getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblWarriors.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblWarriorsMouseClicked(evt);
            }
        });
        spnTable.setViewportView(tblWarriors);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Quick Add Warrior"));

        jLabel1.setText("Warrior Name");

        jLabel2.setText("Gunnery");

        jLabel3.setText("Piloting");

        btnAddWarrior.setText("Add Warrior");
        btnAddWarrior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddWarriorActionPerformed(evt);
            }
        });

        jLabel5.setText("Rank");

        jLabel6.setText("Faction");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(165, 165, 165)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtGunnery, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txtRank, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtFaction, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddWarrior)))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(1, 1, 1)
                        .addComponent(txtFaction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAddWarrior)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(1, 1, 1)
                        .addComponent(txtRank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(1, 1, 1)
                        .addComponent(txtPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(1, 1, 1)
                        .addComponent(txtGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(1, 1, 1)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Personnel File"));

        jLabel4.setText("File Name:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFileName, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel4)
                .addComponent(txtFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spnTable, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnTable, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        dlgWarrior dWar = new dlgWarrior(this, warriors, new Warrior());
        dWar.setLocationRelativeTo(this);
        dWar.setVisible(true);

        if ( dWar.Result ) warriors.Add(dWar.getWarrior());
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnAddWarriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddWarriorActionPerformed
        if ( txtName.getText().isEmpty() || txtGunnery.getText().isEmpty() || txtPiloting.getText().isEmpty() ) { return; }

        Warrior warrior = new Warrior();
        warrior.setName(txtName.getText());
        warrior.setGunnery(Integer.parseInt(txtGunnery.getText()));
        warrior.setPiloting(Integer.parseInt(txtPiloting.getText()));
        warrior.setRank(txtRank.getText());
        warrior.setFaction(txtFaction.getText());
        warriors.Add(warrior);
        warriors.setupTable(tblWarriors);

        txtName.setText("");
        txtGunnery.setText("");
        txtPiloting.setText("");
        txtRank.setText("");
        txtFaction.setText("");
    }//GEN-LAST:event_btnAddWarriorActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
         int[] rows = tblWarriors.getSelectedRows();
         Warrior[] ws = new Warrior[rows.length];
         for (int i=0; i < rows.length; i++ ) {
             Warrior w = (Warrior) warriors.Get(tblWarriors.convertRowIndexToModel(rows[i]));
             ws[i] = w;
         }
         for (int j=0; j < ws.length; j++) {
             warriors.Remove(ws[j]);
         }
         warriors.setupTable(tblWarriors);
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnLoadHMPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadHMPActionPerformed
        Media media = new Media();
        File warFile = media.SelectFile(parent.Prefs.get("LastWarFile", ""), "war", "Load HMP Warrior File");

        int     Name = 0,
                Quirk = 1,
                Gunnery = 2,
                Piloting = 3;

        if ( warFile != null ) {
            try {
                parent.Prefs.put("LastWarFile", warFile.getCanonicalPath());
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(warFile.getCanonicalPath())));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    // Is there a semicolon in the line?  If not ignore it
                    if (line.contains(";")) {
                        if ( line.startsWith(";") ) {
                            //ignore it's a comment line
                        } else {
                            Warrior warrior = new Warrior();
                            String[] parts = line.split(";");
                            //does the first item have a comma?  if so split and recombine in reverse
                            if ( parts[Name].contains(",") ) {
                                String[] name = parts[Name].split(",");
                                if ( name.length == 1 ) {
                                    warrior.setName(name[0]);
                                } else if ( name.length == 2 ) {
                                    warrior.setName(name[0]);
                                    warrior.setRank(name[1]);
                                } else if ( name.length == 3 ) {
                                    warrior.setName(name[1] + " " + name[0]);
                                    warrior.setRank(name[2]);
                                }
                            } else {
                                /*
                                String[] name = parts[Name].split(" ");
                                if ( name.length == 1 ) {
                                    warrior.setName(name[0]);
                                } else if ( name.length == 2 ) {
                                    warrior.setName(name[0] + " " + name[1]);
                                } else if ( name.length == 3 ) {
                                    warrior.setName(name[1] + " " + name[2]);
                                    warrior.setRank(name[0]);
                                }
                                 */
                                warrior.setName(parts[Name]);
                            }

                            //is there an OtherInfo area?
                            if ( parts.length == 4 ) {
                                warrior.setQuirks(parts[Quirk]);
                            } else {
                                Gunnery = 1;
                                Piloting = 2;
                            }

                            warrior.setGunnery(Integer.parseInt(parts[Gunnery]));
                            warrior.setPiloting(Integer.parseInt(parts[Piloting]));

                            warriors.Add(warrior);
                        }
                    }
                }
                lblStatus.setText("HMP Warrior file imported...");
                warriors.setupTable(tblWarriors);
            } catch (IOException ex) {
                Media.Messager(this, "An error occured while loading the request HMP Warrior file.\n" + ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnLoadHMPActionPerformed

    private void btnSaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveFileActionPerformed
        if ( warriors.getPersonnelFile().isEmpty() ) {
            Media media = new Media();
            File pFile = media.SelectFile(parent.Prefs.get("LastPSNFile", "/data"), "psn", "Save Personnel To");
            try {
                warriors.setPersonnelFile(pFile.getCanonicalPath().toString());
            } catch (IOException ex) {
                warriors.setPersonnelFile("data/warriors.psn");
            }
        }

        warriors.setTitle(txtFileName.getText());
        XMLWriter writer = new XMLWriter();
        try {
            writer.SerializeWarriors(warriors, warriors.getPersonnelFile());
            lblStatus.setText("File saved...");
        } catch (IOException ex) {
            Media.Messager("Could not save your file!\n" + ex.getMessage());
        }
    }//GEN-LAST:event_btnSaveFileActionPerformed

    private void tblWarriorsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblWarriorsMouseClicked
        if ( evt.getClickCount() == 2 ) {
            if ( tblWarriors.getSelectedRows().length > 0 ) {
                int[] Rows = tblWarriors.getSelectedRows();
                for (int i=0; i < Rows.length; i++ )
                {
                    Warrior Data = ((Warriors) tblWarriors.getModel()).Get( tblWarriors.convertRowIndexToModel( Rows[i] ) );
                    dlgWarrior DoWarrior = new dlgWarrior(this.parent, warriors, Data);
                    DoWarrior.setLocationRelativeTo(this);
                    DoWarrior.setVisible(true);
                }
            }
        }
    }//GEN-LAST:event_tblWarriorsMouseClicked

    private void btnNewFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewFileActionPerformed
        txtFileName.setText("");
        warriors = new Warriors("");
        warriors.setupTable(tblWarriors);
}//GEN-LAST:event_btnNewFileActionPerformed

    private void btnLoadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadFileActionPerformed
        Media media = new Media();
        File warFile = media.SelectFile(parent.Prefs.get("LastPSNFile", ""), "psn", "Select Personnel File");

        if ( warFile != null ) {
            try {
                parent.Prefs.put("LastPSNFile", warFile.getCanonicalPath());
                warriors = new Warriors(warFile.getCanonicalPath());
                warriors.setupTable(tblWarriors);
            } catch (IOException ex) {
                Media.Messager("Could not open " + warFile.getName() + "\n" + ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnLoadFileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddWarrior;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnLoadFile;
    private javax.swing.JButton btnLoadHMP;
    private javax.swing.JButton btnNewFile;
    private javax.swing.JButton btnSaveFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JScrollPane spnTable;
    private javax.swing.JToolBar.Separator sprOne;
    private javax.swing.JTable tblWarriors;
    private javax.swing.JTextField txtFaction;
    private javax.swing.JTextField txtFileName;
    private javax.swing.JTextField txtGunnery;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPiloting;
    private javax.swing.JTextField txtRank;
    // End of variables declaration//GEN-END:variables

}

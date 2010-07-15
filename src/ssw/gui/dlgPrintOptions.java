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

import java.util.Vector;
import common.CommonTools;
import components.Mech;
import javax.print.*;

public class dlgPrintOptions extends javax.swing.JDialog {
    private Mech CurMech;
    private ifMechForm Parent;
    private boolean Result = false;
    private Vector printers;

    /** Creates new form dlgPrintOptions */
    public dlgPrintOptions( java.awt.Frame parent, boolean modal, Mech m ) {
        super(parent, modal);
        initComponents();

        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printer : services) printers.add(printer);

        for (int i=0; i<=printers.size()-1; i++)
        {
        }
        Parent = (ifMechForm) parent;
        CurMech = m;
        cmbGunnery.setSelectedIndex( 4 );
        cmbPiloting.setSelectedIndex( 5 );

        chkPrintCharts.setSelected(Parent.GetPrefs().getBoolean("UseCharts", false));
        chkMWStats.setSelected(Parent.GetPrefs().getBoolean("NoPilot", false));
        if( chkMWStats.isSelected() ) {
            lblAdjustBV.setText( String.format( "%1$,d", CurMech.GetCurrentBV() ) );
            chkAdjustBV.setSelected( false );
            chkAdjustBV.setEnabled( false );
            cmbGunnery.setEnabled( false );
            cmbPiloting.setEnabled( false );
            txtWarriorName.setEnabled( false );
        } else {
            chkAdjustBV.setSelected(Parent.GetPrefs().getBoolean("AdjustPG", false));
            lblAdjustBV.setText( String.format( "%1$,.0f", CommonTools.GetAdjustedBV( CurMech.GetCurrentBV(), cmbGunnery.getSelectedIndex(), cmbPiloting.getSelectedIndex() ) ) );   
        }
        if (Parent.GetPrefs().getBoolean("UseA4", false)) {
            cmbPaperSize.setSelectedIndex(1);
        }
        chkUseHexConversion.setEnabled( Parent.GetPrefs().getBoolean( "UseMiniConversion", false ) );
        if( chkUseHexConversion.isSelected() ) {
            lblOneHex.setEnabled( true );
            cmbHexConvFactor.setEnabled( true );
            lblInches.setEnabled( true );
            cmbHexConvFactor.setSelectedIndex( Parent.GetPrefs().getInt( "MiniConversionRate", 0 ) );
        }
    }

    public boolean Result() {
        return Result;
    }

    public boolean PrintPilot() {
        return ! chkMWStats.isSelected();
    }

    public String GetWarriorName() {
        return txtWarriorName.getText();
    }

    public int GetGunnery() {
        return cmbGunnery.getSelectedIndex();
    }

    public int GetPiloting() {
        return cmbPiloting.getSelectedIndex();
    }

    public boolean AdjustBV() {
        return chkAdjustBV.isSelected();
    }

    public double GetAdjustedBV() {
        return CommonTools.GetAdjustedBV( CurMech.GetCurrentBV(), cmbGunnery.getSelectedIndex(), cmbPiloting.getSelectedIndex() );
    }

    public boolean PrintCharts() {
        return chkPrintCharts.isSelected();
    }

    public boolean UseA4Paper() {
        if( cmbPaperSize.getSelectedIndex() == 0 ) {
            return false;
        } else {
            return true;
        }
    }

    public boolean UseMiniConversion() {
        return chkUseHexConversion.isSelected();
    }

    public double GetMiniConversionRate() {
        switch( cmbHexConvFactor.getSelectedIndex() ) {
            case 0:
                return 0.5;
            case 1:
                return 1.0;
            case 2:
                return 1.5;
            case 3:
                return 2.0;
            case 4:
                return 3.0;
            case 5:
                return 4.0;
            case 6:
                return 5.0;
            default:
                return 1.0;
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
        java.awt.GridBagConstraints gridBagConstraints;

        txtWarriorName = new javax.swing.JTextField();
        chkAdjustBV = new javax.swing.JCheckBox();
        chkPrintCharts = new javax.swing.JCheckBox();
        lblAdjustBVLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        cmbGunnery = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmbPiloting = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        lblAdjustBV = new javax.swing.JLabel();
        chkMWStats = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cmbPaperSize = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cmbPrinters = new javax.swing.JComboBox();
        chkUseHexConversion = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        lblOneHex = new javax.swing.JLabel();
        cmbHexConvFactor = new javax.swing.JComboBox();
        lblInches = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        txtWarriorName.setMaximumSize(new java.awt.Dimension(150, 20));
        txtWarriorName.setMinimumSize(new java.awt.Dimension(150, 20));
        txtWarriorName.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(txtWarriorName, gridBagConstraints);

        chkAdjustBV.setText("Adjust BV for Gunnery/Piloting");
        chkAdjustBV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAdjustBVActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(chkAdjustBV, gridBagConstraints);

        chkPrintCharts.setText("Print helpful charts on the sheet");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(chkPrintCharts, gridBagConstraints);

        lblAdjustBVLabel.setText("Adjusted BV:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        getContentPane().add(lblAdjustBVLabel, gridBagConstraints);

        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jPanel1.add(btnPrint);

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        cmbGunnery.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8" }));
        cmbGunnery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGunneryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel2.add(cmbGunnery, gridBagConstraints);

        jLabel1.setText("Gunnery");
        jPanel2.add(jLabel1, new java.awt.GridBagConstraints());

        jLabel2.setText("Piloting");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel2.add(jLabel2, gridBagConstraints);

        cmbPiloting.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8" }));
        cmbPiloting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPilotingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel2.add(cmbPiloting, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        getContentPane().add(jPanel2, gridBagConstraints);

        jLabel3.setText("MechWarrior Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabel3, gridBagConstraints);

        lblAdjustBV.setText("00,000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(lblAdjustBV, gridBagConstraints);

        chkMWStats.setText("Do not print MechWarrior stats");
        chkMWStats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMWStatsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(chkMWStats, gridBagConstraints);

        jLabel5.setText("Paper Size:");
        jPanel3.add(jLabel5);

        cmbPaperSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Letter", "A4" }));
        jPanel3.add(cmbPaperSize);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        getContentPane().add(jPanel3, gridBagConstraints);

        jLabel4.setText("Printer:");
        jPanel4.add(jLabel4);

        jPanel4.add(cmbPrinters);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jPanel4, gridBagConstraints);

        chkUseHexConversion.setText("Convert Hexes to miniature scale");
        chkUseHexConversion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseHexConversionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(chkUseHexConversion, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        lblOneHex.setText("One Hex equals");
        lblOneHex.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        jPanel5.add(lblOneHex, gridBagConstraints);

        cmbHexConvFactor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1/2", "1", "1 1/2", "2", "3", "4", "5" }));
        cmbHexConvFactor.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel5.add(cmbHexConvFactor, gridBagConstraints);

        lblInches.setText("Inches");
        lblInches.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel5.add(lblInches, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jPanel5, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void cmbPilotingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPilotingActionPerformed
    if( chkAdjustBV.isSelected() ) {
        lblAdjustBV.setText( String.format( "%1$,.0f", CommonTools.GetAdjustedBV( CurMech.GetCurrentBV(), cmbGunnery.getSelectedIndex(), cmbPiloting.getSelectedIndex() ) ) );
    }
}//GEN-LAST:event_cmbPilotingActionPerformed

private void cmbGunneryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGunneryActionPerformed
    if( chkAdjustBV.isSelected() ) {
        lblAdjustBV.setText( String.format( "%1$,.0f", CommonTools.GetAdjustedBV( CurMech.GetCurrentBV(), cmbGunnery.getSelectedIndex(), cmbPiloting.getSelectedIndex() ) ) );   
    }
}//GEN-LAST:event_cmbGunneryActionPerformed

private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
    Parent.GetPrefs().putBoolean("UseA4", UseA4Paper());
    Parent.GetPrefs().putBoolean("UseCharts", chkPrintCharts.isSelected());
    Parent.GetPrefs().putBoolean("AdjustPG", chkAdjustBV.isSelected());
    Parent.GetPrefs().putBoolean("NoPilot", chkMWStats.isSelected());
    Parent.GetPrefs().putBoolean( "UseMiniConversion", chkUseHexConversion.isSelected() );
    Parent.GetPrefs().putInt( "MiniConversionRate", cmbHexConvFactor.getSelectedIndex() );

    Result = true;
    setVisible( false );
}//GEN-LAST:event_btnPrintActionPerformed

private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
    setVisible( false );
}//GEN-LAST:event_btnCancelActionPerformed

private void chkAdjustBVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAdjustBVActionPerformed
    if( chkAdjustBV.isSelected() ) {
        lblAdjustBV.setText( String.format( "%1$,.0f", CommonTools.GetAdjustedBV( CurMech.GetCurrentBV(), cmbGunnery.getSelectedIndex(), cmbPiloting.getSelectedIndex() ) ) );   
    } else {
        lblAdjustBV.setText( String.format( "%1$,d", CurMech.GetCurrentBV() ) );
    }
}//GEN-LAST:event_chkAdjustBVActionPerformed

private void chkMWStatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMWStatsActionPerformed
    if( chkMWStats.isSelected() ) {
        lblAdjustBV.setText( String.format( "%1$,d", CurMech.GetCurrentBV() ) );
        chkAdjustBV.setSelected( false );
        chkAdjustBV.setEnabled( false );
        cmbGunnery.setEnabled( false );
        cmbPiloting.setEnabled( false );
        txtWarriorName.setEnabled( false );
    } else {
        lblAdjustBV.setText( String.format( "%1$,.0f", CommonTools.GetAdjustedBV( CurMech.GetCurrentBV(), cmbGunnery.getSelectedIndex(), cmbPiloting.getSelectedIndex() ) ) );   
        cmbGunnery.setEnabled( true );
        cmbPiloting.setEnabled( true );
        txtWarriorName.setEnabled( true );
        chkAdjustBV.setEnabled( true );
    }
}//GEN-LAST:event_chkMWStatsActionPerformed

private void chkUseHexConversionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseHexConversionActionPerformed
    if( chkUseHexConversion.isSelected() ) {
        lblOneHex.setEnabled( true );
        cmbHexConvFactor.setEnabled( true );
        lblInches.setEnabled( true );
    } else {
        lblOneHex.setEnabled( false );
        cmbHexConvFactor.setEnabled( false );
        lblInches.setEnabled( false );
    }
}//GEN-LAST:event_chkUseHexConversionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnPrint;
    private javax.swing.JCheckBox chkAdjustBV;
    private javax.swing.JCheckBox chkMWStats;
    private javax.swing.JCheckBox chkPrintCharts;
    private javax.swing.JCheckBox chkUseHexConversion;
    private javax.swing.JComboBox cmbGunnery;
    private javax.swing.JComboBox cmbHexConvFactor;
    private javax.swing.JComboBox cmbPaperSize;
    private javax.swing.JComboBox cmbPiloting;
    private javax.swing.JComboBox cmbPrinters;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblAdjustBV;
    private javax.swing.JLabel lblAdjustBVLabel;
    private javax.swing.JLabel lblInches;
    private javax.swing.JLabel lblOneHex;
    private javax.swing.JTextField txtWarriorName;
    // End of variables declaration//GEN-END:variables

}

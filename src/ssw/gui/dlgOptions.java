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

import java.awt.Color;
import java.io.IOException;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import ssw.*;

public class dlgOptions extends javax.swing.JDialog {
    frmMain Parent;
    Options MyOptions;

    /** Creates new form dlgOptions */
    public dlgOptions(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setResizable( false );
        setTitle( Constants.AppName + " Options" );
        Parent= (frmMain) parent;
        MyOptions = ( (frmMain) parent).GetOptions();
        SetState();
    }

    private void SetState() {
        // sets the initial state from the options given to us.
        chkJumpHeat.setSelected( MyOptions.Heat_RemoveJumps );
        chkStealthArmor.setSelected( MyOptions.Heat_RemoveStealthArmor );
        chkOSWeapons.setSelected( MyOptions.Heat_RemoveOSWeapons );
        chkRearWeapons.setSelected( MyOptions.Heat_RemoveRearWeapons );
        chkUltraRAC.setSelected( MyOptions.Heat_UAC_RAC_FullRate );
        chkEquipmentHeat.setSelected( MyOptions.Heat_RemoveEquipment );

        if( MyOptions.Heat_RemoveJumps ) {
            chkMoveHeat.setEnabled( true );
            chkMoveHeat.setSelected( MyOptions.Heat_RemoveMovement );
        } else {
            chkMoveHeat.setEnabled( false );
            chkMoveHeat.setSelected( false );
        }

        chkCustomPercent.setSelected( MyOptions.Armor_CustomPercentage );
        if( MyOptions.Armor_CustomPercentage ) {
            lblCTPercent.setEnabled( true );
            txtCTPercent.setEnabled( true );
            txtCTPercent.setText( "" + MyOptions.Armor_CTRPercent );
            lblSidePercent.setEnabled( true );
            txtSidePercent.setEnabled( true );
            txtSidePercent.setText( "" + MyOptions.Armor_STRPercent );
        } else {
            lblCTPercent.setEnabled( false );
            txtCTPercent.setEnabled( false );
            txtCTPercent.setText( "" + MyOptions.Armor_CTRPercent );
            lblSidePercent.setEnabled( false );
            txtSidePercent.setEnabled( false );
            txtSidePercent.setText( "" + MyOptions.Armor_STRPercent );
        }

        if( MyOptions.Armor_Head == MyOptions.HEAD_MAX ) {
            rdoMaxHead.setSelected( true );
        } else {
            rdoEqualHead.setSelected( true );
        }

        switch( MyOptions.Armor_Priority ) {
            case 0:
                rdoPriorityTorso.setSelected( true );
                break;
            case 1:
                rdoPriorityArms.setSelected( true );
                break;
            case 2:
                rdoPriorityLegs.setSelected( true );
                break;
            default:
                rdoPriorityTorso.setSelected( true );
                break;
        }

        cmbDefaultRules.setSelectedIndex( MyOptions.DefaultRules );
        cmbDefaultEra.setSelectedIndex( MyOptions.DefaultEra );
        cmbDefaultTechbase.setSelectedIndex( MyOptions.DefaultTechbase );
        chkRetractableBlade.setSelected( MyOptions.Equip_AllowRBlade );

        lblLocked.setBackground( MyOptions.bg_LOCKED );
        lblLocked.setForeground( MyOptions.fg_LOCKED );
        lblEmpty.setBackground( MyOptions.bg_EMPTY );
        lblEmpty.setForeground( MyOptions.fg_EMPTY );
        lblLinked.setBackground( MyOptions.bg_LINKED );
        lblLinked.setForeground( MyOptions.fg_LINKED );
        lblNormal.setBackground( MyOptions.bg_NORMAL );
        lblNormal.setForeground( MyOptions.fg_NORMAL );
        lblArmored.setBackground( MyOptions.bg_ARMORED );
        lblArmored.setForeground( MyOptions.fg_ARMORED );
        lblHiLite.setBackground( MyOptions.bg_HILITE );
        lblHiLite.setForeground( MyOptions.fg_HILITE );

        if( MyOptions.Export_Sort == MyOptions.EXPORT_SORT_OUT ) {
            rdoSortOut.setSelected( true );
        } else {
            rdoSortIn.setSelected( true );
        }
        chkAmmoAtEnd.setSelected( MyOptions.Export_AmmoAtEnd );

        txtSaveLoadPath.setText( MyOptions.SaveLoadPath );
        txtHTMLPath.setText( MyOptions.HTMLPath );
        txtTXTPath.setText( MyOptions.TXTPath );
        txtMegamekPath.setText( MyOptions.MegamekPath );
        if( MyOptions.UseMMCustom ) {
            chkMMCustom.setSelected( true );
            txtMMCustom.setEnabled( true );
            txtMMCustom.setText( MyOptions.MMCustom );
        }
    }

    private void SaveState() {
        MyOptions.Heat_RemoveJumps = chkJumpHeat.isSelected();
        MyOptions.Heat_RemoveMovement = chkMoveHeat.isSelected();
        MyOptions.Heat_RemoveOSWeapons = chkOSWeapons.isSelected();
        MyOptions.Heat_RemoveRearWeapons = chkRearWeapons.isSelected();
        MyOptions.Heat_UAC_RAC_FullRate = chkUltraRAC.isSelected();
        MyOptions.Heat_RemoveStealthArmor = chkStealthArmor.isSelected();
        MyOptions.Heat_RemoveEquipment = chkEquipmentHeat.isSelected();

        MyOptions.Armor_CustomPercentage = chkCustomPercent.isSelected();
        MyOptions.Armor_CTRPercent = Integer.valueOf( txtCTPercent.getText() );
        MyOptions.Armor_STRPercent = Integer.valueOf( txtSidePercent.getText() );
        if( rdoMaxHead.isSelected() ) {
            MyOptions.Armor_Head = MyOptions.HEAD_MAX;
        } else {
            MyOptions.Armor_Head = MyOptions.HEAD_EQUAL;
        }
        if( rdoPriorityTorso.isSelected() ) {
            MyOptions.Armor_Priority = MyOptions.PRIORITY_TORSO;
        } else if( rdoPriorityArms.isSelected() ) {
            MyOptions.Armor_Priority = MyOptions.PRIORITY_ARMS;
        } else {
            MyOptions.Armor_Priority = MyOptions.PRIORITY_LEGS;
        }

        MyOptions.DefaultRules = cmbDefaultRules.getSelectedIndex();
        MyOptions.DefaultEra = cmbDefaultEra.getSelectedIndex();
        MyOptions.DefaultTechbase = cmbDefaultTechbase.getSelectedIndex();
        MyOptions.Equip_AllowRBlade = chkRetractableBlade.isSelected();

        MyOptions.bg_LOCKED = lblLocked.getBackground();
        MyOptions.fg_LOCKED = lblLocked.getForeground();
        MyOptions.bg_LINKED = lblLinked.getBackground();
        MyOptions.fg_LINKED = lblLinked.getForeground();
        MyOptions.bg_NORMAL = lblNormal.getBackground();
        MyOptions.fg_NORMAL = lblNormal.getForeground();
        MyOptions.bg_EMPTY = lblEmpty.getBackground();
        MyOptions.fg_EMPTY = lblEmpty.getForeground();
        MyOptions.bg_ARMORED = lblArmored.getBackground();
        MyOptions.fg_ARMORED = lblArmored.getForeground();
        MyOptions.bg_HILITE = lblHiLite.getBackground();
        MyOptions.fg_HILITE = lblHiLite.getForeground();

        MyOptions.SaveLoadPath = txtSaveLoadPath.getText();
        MyOptions.HTMLPath = txtHTMLPath.getText();
        MyOptions.TXTPath = txtTXTPath.getText();
        MyOptions.MegamekPath = txtMegamekPath.getText();
        if( chkMMCustom.isSelected() ) {
            MyOptions.UseMMCustom = true;
            MyOptions.MMCustom = txtMMCustom.getText();
        } else {
            MyOptions.UseMMCustom = false;
            MyOptions.MMCustom = "";
        }

        if( rdoSortOut.isSelected() ) {
            MyOptions.Export_Sort = MyOptions.EXPORT_SORT_OUT;
        } else {
            MyOptions.Export_Sort = MyOptions.EXPORT_SORT_IN;
        }
        MyOptions.Export_AmmoAtEnd = chkAmmoAtEnd.isSelected();

        try {
            Parent.GetOptionsReader().WriteOptions( Constants.OptionsFileName, MyOptions );
        } catch( IOException e ) {
            javax.swing.JOptionPane.showMessageDialog( Parent, "Could not save the options!\nFile operation problem (save, close)." );
        }
    }

    private boolean ValidateMMCustom() {
        char[] c = txtMMCustom.getText().toCharArray();
        for( int i = 0; i < c.length; i++ ) {
            if( ( c[i] >= 'a' ) && ( c[i] <= 'z' ) ) { continue; }
            if( ( c[i] >= 'A' ) && ( c[i] <= 'Z' ) ) { continue; }
            if( ( c[i] >= '0' ) && ( c[i] <= '9' ) ) { continue; }
            if( c[i] == '-' ) { continue; }
            if( c[i] == '_' ) { continue; }
            return false;
        }
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlHeatOptions = new javax.swing.JPanel();
        chkOSWeapons = new javax.swing.JCheckBox();
        chkRearWeapons = new javax.swing.JCheckBox();
        chkJumpHeat = new javax.swing.JCheckBox();
        chkMoveHeat = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        chkStealthArmor = new javax.swing.JCheckBox();
        chkEquipmentHeat = new javax.swing.JCheckBox();
        chkUltraRAC = new javax.swing.JCheckBox();
        pnlArmorOptions = new javax.swing.JPanel();
        chkCustomPercent = new javax.swing.JCheckBox();
        lblCTPercent = new javax.swing.JLabel();
        txtCTPercent = new javax.swing.JTextField();
        lblSidePercent = new javax.swing.JLabel();
        txtSidePercent = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        rdoEqualHead = new javax.swing.JRadioButton();
        rdoMaxHead = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        rdoPriorityTorso = new javax.swing.JRadioButton();
        rdoPriorityArms = new javax.swing.JRadioButton();
        rdoPriorityLegs = new javax.swing.JRadioButton();
        pnlEquipOptions = new javax.swing.JPanel();
        chkRetractableBlade = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        cmbDefaultEra = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        cmbDefaultTechbase = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        cmbDefaultRules = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        pnlColorOptions = new javax.swing.JPanel();
        lblLocked = new javax.swing.JLabel();
        lblLinked = new javax.swing.JLabel();
        lblNormal = new javax.swing.JLabel();
        lblEmpty = new javax.swing.JLabel();
        btnLockedFore = new javax.swing.JButton();
        btnLockedBack = new javax.swing.JButton();
        btnLinkedFore = new javax.swing.JButton();
        btnLinkedBack = new javax.swing.JButton();
        btnNormalFore = new javax.swing.JButton();
        btnNormalBack = new javax.swing.JButton();
        btnEmptyFore = new javax.swing.JButton();
        btnEmptyBack = new javax.swing.JButton();
        lblHiLite = new javax.swing.JLabel();
        btnHiLiteBack = new javax.swing.JButton();
        btnHiLiteFore = new javax.swing.JButton();
        lblArmored = new javax.swing.JLabel();
        btnArmoredFore = new javax.swing.JButton();
        btnArmoredBack = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        rdoSortOut = new javax.swing.JRadioButton();
        rdoSortIn = new javax.swing.JRadioButton();
        chkAmmoAtEnd = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        pnlPaths = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtSaveLoadPath = new javax.swing.JTextField();
        btnTXTPath = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtHTMLPath = new javax.swing.JTextField();
        btnHTMLPath = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txtTXTPath = new javax.swing.JTextField();
        btnSaveLoadPath = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        txtMegamekPath = new javax.swing.JTextField();
        btnMegamekPath = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        chkMMCustom = new javax.swing.JCheckBox();
        txtMMCustom = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnDefaults = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlHeatOptions.setLayout(new java.awt.GridBagLayout());

        chkOSWeapons.setText("Do not include heat from OS weapons");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHeatOptions.add(chkOSWeapons, gridBagConstraints);

        chkRearWeapons.setText("Do not include heat from rear-facing weapons");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHeatOptions.add(chkRearWeapons, gridBagConstraints);

        chkJumpHeat.setText("Do not include Jumping MP heat");
        chkJumpHeat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkJumpHeatActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHeatOptions.add(chkJumpHeat, gridBagConstraints);

        chkMoveHeat.setText("Do not include any movement heat");
        chkMoveHeat.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        pnlHeatOptions.add(chkMoveHeat, gridBagConstraints);

        jLabel1.setText("Total Heat Calculation Options");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHeatOptions.add(jLabel1, gridBagConstraints);

        chkStealthArmor.setText("Do not include heat from Stealth Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHeatOptions.add(chkStealthArmor, gridBagConstraints);

        chkEquipmentHeat.setText("Do not include heat from equipment (AMS, etc.)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHeatOptions.add(chkEquipmentHeat, gridBagConstraints);

        chkUltraRAC.setText("Ultra ACs and RACs fire at full rate");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHeatOptions.add(chkUltraRAC, gridBagConstraints);

        jTabbedPane1.addTab("Heat", pnlHeatOptions);

        pnlArmorOptions.setLayout(new java.awt.GridBagLayout());

        chkCustomPercent.setText("Customize Armor Distribution Percentages");
        chkCustomPercent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCustomPercentActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlArmorOptions.add(chkCustomPercent, gridBagConstraints);

        lblCTPercent.setText("CT rear to CT front Percentage");
        lblCTPercent.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlArmorOptions.add(lblCTPercent, gridBagConstraints);

        txtCTPercent.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCTPercent.setText("25");
        txtCTPercent.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCTPercent.setEnabled(false);
        txtCTPercent.setMaximumSize(new java.awt.Dimension(45, 20));
        txtCTPercent.setMinimumSize(new java.awt.Dimension(45, 20));
        txtCTPercent.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlArmorOptions.add(txtCTPercent, gridBagConstraints);

        lblSidePercent.setText("Side rear to Side front Percentage");
        lblSidePercent.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlArmorOptions.add(lblSidePercent, gridBagConstraints);

        txtSidePercent.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSidePercent.setText("25");
        txtSidePercent.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSidePercent.setEnabled(false);
        txtSidePercent.setMaximumSize(new java.awt.Dimension(45, 20));
        txtSidePercent.setMinimumSize(new java.awt.Dimension(45, 20));
        txtSidePercent.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlArmorOptions.add(txtSidePercent, gridBagConstraints);

        jLabel4.setText("When allocating by tonnage...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        pnlArmorOptions.add(jLabel4, gridBagConstraints);

        buttonGroup1.add(rdoEqualHead);
        rdoEqualHead.setText("Allocate Head armor equally");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlArmorOptions.add(rdoEqualHead, gridBagConstraints);

        buttonGroup1.add(rdoMaxHead);
        rdoMaxHead.setText("Maximize Head armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlArmorOptions.add(rdoMaxHead, gridBagConstraints);

        jLabel5.setText("and...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlArmorOptions.add(jLabel5, gridBagConstraints);

        buttonGroup2.add(rdoPriorityTorso);
        rdoPriorityTorso.setText("Prioritize Torso armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlArmorOptions.add(rdoPriorityTorso, gridBagConstraints);

        buttonGroup2.add(rdoPriorityArms);
        rdoPriorityArms.setText("Prioritize Arm armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlArmorOptions.add(rdoPriorityArms, gridBagConstraints);

        buttonGroup2.add(rdoPriorityLegs);
        rdoPriorityLegs.setText("Prioritize Leg Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlArmorOptions.add(rdoPriorityLegs, gridBagConstraints);

        jTabbedPane1.addTab("Armor", pnlArmorOptions);

        pnlEquipOptions.setLayout(new java.awt.GridBagLayout());

        chkRetractableBlade.setText("Allow Retractable Blade before canon");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEquipOptions.add(chkRetractableBlade, gridBagConstraints);

        jLabel8.setText("Era:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlEquipOptions.add(jLabel8, gridBagConstraints);

        cmbDefaultEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Age of War/Star League", "Succession Wars", "Clan Invasion" }));
        cmbDefaultEra.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbDefaultEra.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbDefaultEra.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbDefaultEra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDefaultEraActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlEquipOptions.add(cmbDefaultEra, gridBagConstraints);

        jLabel9.setText("Techbase:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlEquipOptions.add(jLabel9, gridBagConstraints);

        cmbDefaultTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
        cmbDefaultTechbase.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbDefaultTechbase.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbDefaultTechbase.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlEquipOptions.add(cmbDefaultTechbase, gridBagConstraints);

        jLabel10.setText("Rules Level:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlEquipOptions.add(jLabel10, gridBagConstraints);

        cmbDefaultRules.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tournament Legal", "Advanced Rules", "Experimental Tech" }));
        cmbDefaultRules.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbDefaultRules.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbDefaultRules.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlEquipOptions.add(cmbDefaultRules, gridBagConstraints);

        jLabel11.setText("Equipment Defaults");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlEquipOptions.add(jLabel11, gridBagConstraints);

        jTabbedPane1.addTab("Equipment", pnlEquipOptions);

        pnlColorOptions.setLayout(new java.awt.GridBagLayout());

        lblLocked.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocked.setText(" Location Locked ");
        lblLocked.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLocked.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        pnlColorOptions.add(lblLocked, gridBagConstraints);

        lblLinked.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLinked.setText(" Linked in Location ");
        lblLinked.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLinked.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        pnlColorOptions.add(lblLinked, gridBagConstraints);

        lblNormal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNormal.setText(" Normal Item ");
        lblNormal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblNormal.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        pnlColorOptions.add(lblNormal, gridBagConstraints);

        lblEmpty.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEmpty.setText(" Empty Location ");
        lblEmpty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblEmpty.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        pnlColorOptions.add(lblEmpty, gridBagConstraints);

        btnLockedFore.setText("Fore");
        btnLockedFore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLockedForeActionPerformed(evt);
            }
        });
        pnlColorOptions.add(btnLockedFore, new java.awt.GridBagConstraints());

        btnLockedBack.setText("Back");
        btnLockedBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLockedBackActionPerformed(evt);
            }
        });
        pnlColorOptions.add(btnLockedBack, new java.awt.GridBagConstraints());

        btnLinkedFore.setText("Fore");
        btnLinkedFore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLinkedForeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlColorOptions.add(btnLinkedFore, gridBagConstraints);

        btnLinkedBack.setText("Back");
        btnLinkedBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLinkedBackActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pnlColorOptions.add(btnLinkedBack, gridBagConstraints);

        btnNormalFore.setText("Fore");
        btnNormalFore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNormalForeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        pnlColorOptions.add(btnNormalFore, gridBagConstraints);

        btnNormalBack.setText("Back");
        btnNormalBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNormalBackActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        pnlColorOptions.add(btnNormalBack, gridBagConstraints);

        btnEmptyFore.setText("Fore");
        btnEmptyFore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmptyForeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        pnlColorOptions.add(btnEmptyFore, gridBagConstraints);

        btnEmptyBack.setText("Back");
        btnEmptyBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmptyBackActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        pnlColorOptions.add(btnEmptyBack, gridBagConstraints);

        lblHiLite.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHiLite.setText("Allocate HiLite");
        lblHiLite.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblHiLite.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        pnlColorOptions.add(lblHiLite, gridBagConstraints);

        btnHiLiteBack.setText("Back");
        btnHiLiteBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHiLiteBackActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        pnlColorOptions.add(btnHiLiteBack, gridBagConstraints);

        btnHiLiteFore.setText("Fore");
        btnHiLiteFore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHiLiteForeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        pnlColorOptions.add(btnHiLiteFore, gridBagConstraints);

        lblArmored.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArmored.setText("Armored Item");
        lblArmored.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblArmored.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        pnlColorOptions.add(lblArmored, gridBagConstraints);

        btnArmoredFore.setText("Fore");
        btnArmoredFore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArmoredForeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        pnlColorOptions.add(btnArmoredFore, gridBagConstraints);

        btnArmoredBack.setText("Back");
        btnArmoredBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArmoredBackActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        pnlColorOptions.add(btnArmoredBack, gridBagConstraints);

        jTabbedPane1.addTab("Colors", pnlColorOptions);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("When sorting the equipment stat blocks during");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel2, gridBagConstraints);

        buttonGroup3.add(rdoSortOut);
        rdoSortOut.setText("Sort from the center of the mech out");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(rdoSortOut, gridBagConstraints);

        buttonGroup3.add(rdoSortIn);
        rdoSortIn.setText("Sort from the arms in, then head and legs");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(rdoSortIn, gridBagConstraints);

        chkAmmoAtEnd.setText("Group Ammo at the bottom of the stat block");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(chkAmmoAtEnd, gridBagConstraints);

        jLabel3.setText("export to TXT or HTML:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel3, gridBagConstraints);

        jLabel6.setText("     (HD, CT, RT, LT, RA, LA, RL, LL)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel6, gridBagConstraints);

        jLabel7.setText("     (RA, LA, RT, LT, CT, HD, RL, LL)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel7, gridBagConstraints);

        jTabbedPane1.addTab("Export", jPanel2);

        pnlPaths.setLayout(new java.awt.GridBagLayout());

        jLabel12.setText("Default 'Mech Save/Load path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(jLabel12, gridBagConstraints);

        txtSaveLoadPath.setText("jTextField1");
        txtSaveLoadPath.setMaximumSize(new java.awt.Dimension(200, 20));
        txtSaveLoadPath.setMinimumSize(new java.awt.Dimension(200, 20));
        txtSaveLoadPath.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(txtSaveLoadPath, gridBagConstraints);

        btnTXTPath.setText("...");
        btnTXTPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTXTPathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(btnTXTPath, gridBagConstraints);

        jLabel13.setText("Default HTML Export Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(jLabel13, gridBagConstraints);

        txtHTMLPath.setText("jTextField2");
        txtHTMLPath.setMaximumSize(new java.awt.Dimension(200, 20));
        txtHTMLPath.setMinimumSize(new java.awt.Dimension(200, 20));
        txtHTMLPath.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(txtHTMLPath, gridBagConstraints);

        btnHTMLPath.setText("...");
        btnHTMLPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHTMLPathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(btnHTMLPath, gridBagConstraints);

        jLabel14.setText("Default Text Export Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(jLabel14, gridBagConstraints);

        txtTXTPath.setText("jTextField3");
        txtTXTPath.setMaximumSize(new java.awt.Dimension(200, 20));
        txtTXTPath.setMinimumSize(new java.awt.Dimension(200, 20));
        txtTXTPath.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(txtTXTPath, gridBagConstraints);

        btnSaveLoadPath.setText("...");
        btnSaveLoadPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveLoadPathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(btnSaveLoadPath, gridBagConstraints);

        jLabel15.setText("Megamek root directory:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(jLabel15, gridBagConstraints);

        txtMegamekPath.setText("jTextField4");
        txtMegamekPath.setMaximumSize(new java.awt.Dimension(200, 20));
        txtMegamekPath.setMinimumSize(new java.awt.Dimension(200, 20));
        txtMegamekPath.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(txtMegamekPath, gridBagConstraints);

        btnMegamekPath.setText("...");
        btnMegamekPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMegamekPathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(btnMegamekPath, gridBagConstraints);

        jLabel16.setText("(i.e. \"C:\\Program Files\\Megamek\")");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(jLabel16, gridBagConstraints);

        chkMMCustom.setText("Use custom folder for MegaMek files");
        chkMMCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMMCustomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(chkMMCustom, gridBagConstraints);

        txtMMCustom.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPaths.add(txtMMCustom, gridBagConstraints);

        jTabbedPane1.addTab("Paths", pnlPaths);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(jTabbedPane1, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel1.add(btnSave, gridBagConstraints);

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel1.add(btnCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        getContentPane().add(jPanel1, gridBagConstraints);

        btnDefaults.setText("Set Defaults");
        btnDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        getContentPane().add(btnDefaults, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkCustomPercentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCustomPercentActionPerformed
        // Depending on state, enables or disables controls on the form
        if( chkCustomPercent.isSelected() ) {
            lblCTPercent.setEnabled( true );
            txtCTPercent.setEnabled( true );
            txtCTPercent.setText( "" + MyOptions.Armor_CTRPercent );
            lblSidePercent.setEnabled( true );
            txtSidePercent.setEnabled( true );
            txtSidePercent.setText( "" + MyOptions.Armor_STRPercent );
        } else {
            // reset to defaults as well.
            txtCTPercent.setText( "" + Constants.DEFAULT_CTR_ARMOR_PERCENT );
            lblCTPercent.setEnabled( false );
            txtCTPercent.setEnabled( false );
            txtSidePercent.setText( "" + Constants.DEFAULT_STR_ARMOR_PERCENT );
            lblSidePercent.setEnabled( false );
            txtSidePercent.setEnabled( false );
        }
    }//GEN-LAST:event_chkCustomPercentActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // first, check to see that our MegaMek custom directory is okay
        if( ! ValidateMMCustom() ) {
            javax.swing.JOptionPane.showMessageDialog( this, "The custom MegaMek directory can only contain letters,\nnumbers, underscores \"_\", and dashes \"-\"" );
            jTabbedPane1.setSelectedComponent( pnlPaths );
            return;
        }
        SaveState();
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void chkJumpHeatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkJumpHeatActionPerformed
        if( chkJumpHeat.isSelected() ) {
            chkMoveHeat.setEnabled( true );
        } else {
            chkMoveHeat.setSelected( false );
            chkMoveHeat.setEnabled( false );
        }
    }//GEN-LAST:event_chkJumpHeatActionPerformed

    private void btnDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultsActionPerformed
        MyOptions.SetDefaults();
        SetState();
    }//GEN-LAST:event_btnDefaultsActionPerformed

    private void btnLockedBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLockedBackActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Background Color", lblLocked.getBackground());
        if ( newColor != null ) { lblLocked.setBackground( newColor ); }
    }//GEN-LAST:event_btnLockedBackActionPerformed

    private void btnLockedForeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLockedForeActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblLocked.getForeground());
        if ( newColor != null ) { lblLocked.setForeground( newColor ); }
    }//GEN-LAST:event_btnLockedForeActionPerformed

    private void btnLinkedForeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLinkedForeActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblLinked.getForeground());
        if ( newColor != null ) { lblLinked.setForeground( newColor ); }
    }//GEN-LAST:event_btnLinkedForeActionPerformed

    private void btnLinkedBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLinkedBackActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Background Color", lblLinked.getBackground());
        if ( newColor != null ) { lblLinked.setBackground( newColor ); }
    }//GEN-LAST:event_btnLinkedBackActionPerformed

    private void btnNormalForeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNormalForeActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblNormal.getForeground());
        if ( newColor != null ) { lblNormal.setForeground( newColor ); }
    }//GEN-LAST:event_btnNormalForeActionPerformed

    private void btnNormalBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNormalBackActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Background Color", lblNormal.getBackground());
        if ( newColor != null ) { lblNormal.setBackground( newColor ); }
    }//GEN-LAST:event_btnNormalBackActionPerformed

    private void btnEmptyForeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmptyForeActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblEmpty.getForeground());
        if ( newColor != null ) { lblEmpty.setForeground( newColor ); }
    }//GEN-LAST:event_btnEmptyForeActionPerformed

    private void btnEmptyBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmptyBackActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Background Color", lblEmpty.getBackground());
        if ( newColor != null ) { lblEmpty.setBackground( newColor ); }
    }//GEN-LAST:event_btnEmptyBackActionPerformed

    private void btnHiLiteForeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHiLiteForeActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblHiLite.getForeground());
        if ( newColor != null ) { lblHiLite.setForeground( newColor ); }
    }//GEN-LAST:event_btnHiLiteForeActionPerformed

    private void btnHiLiteBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHiLiteBackActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Background Color", lblHiLite.getBackground());
        if ( newColor != null ) { lblHiLite.setBackground( newColor ); }
    }//GEN-LAST:event_btnHiLiteBackActionPerformed

    private void cmbDefaultEraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDefaultEraActionPerformed
        switch( cmbDefaultEra.getSelectedIndex() ) {
        case Constants.STAR_LEAGUE:
            cmbDefaultTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere" }));
            break;
        case Constants.SUCCESSION:
            cmbDefaultTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
            break;
        case Constants.CLAN_INVASION:
            cmbDefaultTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
            break;
        }
    }//GEN-LAST:event_cmbDefaultEraActionPerformed

private void btnHTMLPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHTMLPathActionPerformed
    String path = "";
    JFileChooser fc = new JFileChooser();

    //Add a custom file filter and disable the default
    //(Accept All) file filter.
    fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
    fc.setAcceptAllFileFilterUsed(false);

    //Show it.
    int returnVal = fc.showDialog( this, "Choose directory");

    //Process the results.  If no file is chosen, the default is used.
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        path = fc.getSelectedFile().getPath();
        txtHTMLPath.setText( path );
    }
}//GEN-LAST:event_btnHTMLPathActionPerformed

private void btnSaveLoadPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveLoadPathActionPerformed
    String path = "";
    JFileChooser fc = new JFileChooser();

    //Add a custom file filter and disable the default
    //(Accept All) file filter.
    fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
    fc.setAcceptAllFileFilterUsed(false);

    //Show it.
    int returnVal = fc.showDialog( this, "Choose directory");

    //Process the results.  If no file is chosen, the default is used.
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        path = fc.getSelectedFile().getPath();
        txtSaveLoadPath.setText( path );
    }
}//GEN-LAST:event_btnSaveLoadPathActionPerformed

private void btnTXTPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTXTPathActionPerformed
    String path = "";
    JFileChooser fc = new JFileChooser();

    //Add a custom file filter and disable the default
    //(Accept All) file filter.
    fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
    fc.setAcceptAllFileFilterUsed(false);

    //Show it.
    int returnVal = fc.showDialog( this, "Choose directory");

    //Process the results.  If no file is chosen, the default is used.
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        path = fc.getSelectedFile().getPath();
        txtTXTPath.setText( path );
    }
}//GEN-LAST:event_btnTXTPathActionPerformed

private void btnMegamekPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMegamekPathActionPerformed
    String path = "";
    JFileChooser fc = new JFileChooser();

    //Add a custom file filter and disable the default
    //(Accept All) file filter.
    fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
    fc.setAcceptAllFileFilterUsed(false);

    //Show it.
    int returnVal = fc.showDialog( this, "Choose directory");

    //Process the results.  If no file is chosen, the default is used.
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        path = fc.getSelectedFile().getPath();
        txtMegamekPath.setText( path );
    }
}//GEN-LAST:event_btnMegamekPathActionPerformed

private void btnArmoredForeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArmoredForeActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblArmored.getForeground());
        if ( newColor != null ) { lblArmored.setForeground( newColor ); }
}//GEN-LAST:event_btnArmoredForeActionPerformed

private void btnArmoredBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArmoredBackActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Background Color", lblArmored.getBackground());
        if ( newColor != null ) { lblArmored.setBackground( newColor ); }
}//GEN-LAST:event_btnArmoredBackActionPerformed

private void chkMMCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMMCustomActionPerformed
    if( chkMMCustom.isSelected() ) {
        txtMMCustom.setEnabled( true );
    } else {
        txtMMCustom.setEnabled( false );
        txtMMCustom.setText( "" );
    }
}//GEN-LAST:event_chkMMCustomActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnArmoredBack;
    private javax.swing.JButton btnArmoredFore;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDefaults;
    private javax.swing.JButton btnEmptyBack;
    private javax.swing.JButton btnEmptyFore;
    private javax.swing.JButton btnHTMLPath;
    private javax.swing.JButton btnHiLiteBack;
    private javax.swing.JButton btnHiLiteFore;
    private javax.swing.JButton btnLinkedBack;
    private javax.swing.JButton btnLinkedFore;
    private javax.swing.JButton btnLockedBack;
    private javax.swing.JButton btnLockedFore;
    private javax.swing.JButton btnMegamekPath;
    private javax.swing.JButton btnNormalBack;
    private javax.swing.JButton btnNormalFore;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveLoadPath;
    private javax.swing.JButton btnTXTPath;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JCheckBox chkAmmoAtEnd;
    private javax.swing.JCheckBox chkCustomPercent;
    private javax.swing.JCheckBox chkEquipmentHeat;
    private javax.swing.JCheckBox chkJumpHeat;
    private javax.swing.JCheckBox chkMMCustom;
    private javax.swing.JCheckBox chkMoveHeat;
    private javax.swing.JCheckBox chkOSWeapons;
    private javax.swing.JCheckBox chkRearWeapons;
    private javax.swing.JCheckBox chkRetractableBlade;
    private javax.swing.JCheckBox chkStealthArmor;
    private javax.swing.JCheckBox chkUltraRAC;
    private javax.swing.JComboBox cmbDefaultEra;
    private javax.swing.JComboBox cmbDefaultRules;
    private javax.swing.JComboBox cmbDefaultTechbase;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblArmored;
    private javax.swing.JLabel lblCTPercent;
    private javax.swing.JLabel lblEmpty;
    private javax.swing.JLabel lblHiLite;
    private javax.swing.JLabel lblLinked;
    private javax.swing.JLabel lblLocked;
    private javax.swing.JLabel lblNormal;
    private javax.swing.JLabel lblSidePercent;
    private javax.swing.JPanel pnlArmorOptions;
    private javax.swing.JPanel pnlColorOptions;
    private javax.swing.JPanel pnlEquipOptions;
    private javax.swing.JPanel pnlHeatOptions;
    private javax.swing.JPanel pnlPaths;
    private javax.swing.JRadioButton rdoEqualHead;
    private javax.swing.JRadioButton rdoMaxHead;
    private javax.swing.JRadioButton rdoPriorityArms;
    private javax.swing.JRadioButton rdoPriorityLegs;
    private javax.swing.JRadioButton rdoPriorityTorso;
    private javax.swing.JRadioButton rdoSortIn;
    private javax.swing.JRadioButton rdoSortOut;
    private javax.swing.JTextField txtCTPercent;
    private javax.swing.JTextField txtHTMLPath;
    private javax.swing.JTextField txtMMCustom;
    private javax.swing.JTextField txtMegamekPath;
    private javax.swing.JTextField txtSaveLoadPath;
    private javax.swing.JTextField txtSidePercent;
    private javax.swing.JTextField txtTXTPath;
    // End of variables declaration//GEN-END:variables
    
}

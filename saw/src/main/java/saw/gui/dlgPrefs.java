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

package saw.gui;

import common.Constants;
import components.MechArmor;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import filehandlers.FileCommon;
import java.util.prefs.*;
import components.AvailableCode;
import filehandlers.Media;

public class dlgPrefs extends javax.swing.JDialog {

    private Preferences Prefs;

    /** Creates new form dlfPrefs */
    public dlgPrefs( java.awt.Frame parent, boolean modal ) {
        super( parent, modal );
        Prefs = Preferences.userRoot().node( Constants.SAWPrefs );
        initComponents();
        SetState();
    }

    private void SetState() {
        cmbRulesLevel.setSelectedItem( Prefs.get( "NewMech_RulesLevel", "Tournament Legal" ) );
        cmbEra.setSelectedItem( Prefs.get( "NewMech_Era", "Age of War/Star League" ) );
        cmbEraActionPerformed( null );
        cmbTechbase.setSelectedItem( Prefs.get( "NewMech_Techbase", "Inner Sphere" ) );
        cmbTechbaseActionPerformed( null );
        cmbHeatSinks.setSelectedItem( Prefs.get( "NewMech_Heatsinks", "Single Heat Sink" ) );

        txtHTMLPath.setText( Prefs.get( "HTMLExportPath", "none" ) );
        txtTXTPath.setText( Prefs.get( "TXTExportPath", "none" ) );
        txtMTFPath.setText( Prefs.get( "MTFExportPath", "none" ) );
        txtImagePath.setText( Prefs.get( "DefaultImagePath", "") );
        txtAmmoPrintName.setText( Prefs.get( "AmmoNamePrintFormat", "@%P (%L)" ) );
        txtAmmoExportName.setText( Prefs.get( "AmmoNameExportFormat", "@%P (%L)" ) );
        chkHeatOSWeapons.setSelected( Prefs.getBoolean( "HeatExcludeOS", false ) );
        chkHeatRearWeapons.setSelected( Prefs.getBoolean( "HeatExcludeRear", false ) );
        chkHeatEquip.setSelected( Prefs.getBoolean( "HeatExcludeEquips", false ) );
        chkHeatSystems.setSelected( Prefs.getBoolean( "HeatExcludeSystems", false ) );
        chkHeatJumpMP.setSelected( Prefs.getBoolean( "HeatExcludeJumpMP", false ) );
        if( chkHeatJumpMP.isSelected() ) {
            chkHeatAllMP.setSelected( Prefs.getBoolean( "HeatExcludeAllMP", false ) );
            chkHeatAllMP.setEnabled( true );
        }
        chkHeatUAC.setSelected( Prefs.getBoolean( "HeatACFullRate", false ) );
        chkAutoAddECM.setSelected( Prefs.getBoolean( "AutoAddECM", true ) );

        chkMaxNotInt.setSelected( Prefs.getBoolean( "UseMaxArmorInstead", false ) );
        chkCustomPercents.setSelected( Prefs.getBoolean( "ArmorUseCustomPercent", false ) );
        chkCustomPercentsActionPerformed( null );
        if( Prefs.getBoolean( "ArmorMaxHead", true ) ) {
            rdoArmorMaxHead.setSelected( true );
        } else {
            rdoArmorEqualHead.setSelected( true );
        }
        if( Prefs.getBoolean( "ExportSortOut", false ) ) {
            rdoExportSortOut.setSelected( true );
        } else {
            rdoExportSortIn.setSelected( true );
        }
        switch( Prefs.getInt( "ArmorPriority", MechArmor.ARMOR_PRIORITY_TORSO ) ) {
            case MechArmor.ARMOR_PRIORITY_TORSO:
                rdoArmorTorsoPriority.setSelected( true );
                break;
            case MechArmor.ARMOR_PRIORITY_ARMS:
                rdoArmorArmPriority.setSelected( true );
                break;
            case MechArmor.ARMOR_PRIORITY_LEGS:
                rdoArmorLegPriority.setSelected( true );
                break;
        }
        chkGroupAmmoAtBottom.setSelected( Prefs.getBoolean( "AmmoGroupAtBottom", true ) );
        chkUpdateStartup.setSelected( Prefs.getBoolean( "CheckUpdatesAtStartup", false ) );
        chkLoadLastMech.setSelected( Prefs.getBoolean( "LoadLastMech", false ) );

        lblColorEmpty.setForeground( new Color( Prefs.getInt( "ColorEmptyItemFG", -16777216 ) ) );
        lblColorEmpty.setBackground( new Color( Prefs.getInt( "ColorEmptyItemBG", -6684775 ) ) );
        lblColorNormal.setForeground( new Color( Prefs.getInt( "ColorNormalItemFG", -16777216 ) ) );
        lblColorNormal.setBackground( new Color( Prefs.getInt( "ColorNormalItemBG", -10027009 ) ) );
        lblColorArmored.setForeground( new Color( Prefs.getInt( "ColorArmoredItemFG", -1 ) ) );
        lblColorArmored.setBackground( new Color( Prefs.getInt( "ColorArmoredItemBG", -6710887 ) ) );
        lblColorLinked.setForeground( new Color( Prefs.getInt( "ColorLinkedItemFG", -16777216 ) ) );
        lblColorLinked.setBackground( new Color( Prefs.getInt( "ColorLinkedItemBG", -3618616 ) ) );
        lblColorLocked.setForeground( new Color( Prefs.getInt( "ColorLockedItemFG", -3342337 ) ) );
        lblColorLocked.setBackground( new Color( Prefs.getInt( "ColorLockedItemBG", -16777216 ) ) );
        lblColorHilite.setForeground( new Color( Prefs.getInt( "ColorHiLiteItemFG", -16777216 ) ) );
        lblColorHilite.setBackground( new Color( Prefs.getInt( "ColorHiLiteItemBG", -52 ) ) );
        switch( Prefs.getInt( "SSWScreenSize", saw.Constants.SCREEN_SIZE_NORMAL ) ) {
            case saw.Constants.SCREEN_SIZE_WIDE_1280:
                rdoWidescreen.setSelected( true );
                break;
            default:
                rdoNormalSize.setSelected( true );
                break;
        }
    }

    private void SaveState() {
        Prefs.put( "NewMech_RulesLevel", (String) cmbRulesLevel.getSelectedItem() );
        Prefs.put( "NewMech_Era", (String) cmbEra.getSelectedItem() );
        Prefs.put( "NewMech_Techbase", (String) cmbTechbase.getSelectedItem() );
        Prefs.put( "NewMech_Heatsinks", (String) cmbHeatSinks.getSelectedItem() );
        Prefs.put( "HTMLExportPath", txtHTMLPath.getText() );
        Prefs.put( "TXTExportPath", txtTXTPath.getText() );
        Prefs.put( "MTFExportPath", txtMTFPath.getText() );
        Prefs.put( "DefaultImagePath", txtImagePath.getText() );
        Prefs.put( "AmmoNamePrintFormat", txtAmmoPrintName.getText() );
        Prefs.put( "AmmoNameExportFormat", txtAmmoExportName.getText() );

        Prefs.putBoolean( "HeatExcludeOS", chkHeatOSWeapons.isSelected() );
        Prefs.putBoolean( "HeatExcludeRear", chkHeatRearWeapons.isSelected() );
        Prefs.putBoolean( "HeatExcludeEquips", chkHeatEquip.isSelected() );
        Prefs.putBoolean( "HeatExcludeSystems", chkHeatSystems.isSelected() );
        Prefs.putBoolean( "HeatExcludeJumpMP", chkHeatJumpMP.isSelected() );
        Prefs.putBoolean( "HeatExcludeAllMP", chkHeatAllMP.isSelected() );
        Prefs.putBoolean( "HeatACFullRate", chkHeatUAC.isSelected() );
        Prefs.putBoolean( "UseMaxArmorInstead", chkMaxNotInt.isSelected() );
        Prefs.putBoolean( "ArmorUseCustomPercent", chkCustomPercents.isSelected() );
        Prefs.putBoolean( "ArmorMaxHead", rdoArmorMaxHead.isSelected() );
        Prefs.putBoolean( "ExportSortOut", rdoExportSortOut.isSelected() );
        Prefs.putBoolean( "AmmoGroupAtBottom", chkGroupAmmoAtBottom.isSelected() );
        Prefs.putBoolean( "CheckUpdatesAtStartup", chkUpdateStartup.isSelected() );
        Prefs.putBoolean( "LoadLastMech", chkLoadLastMech.isSelected() );
        Prefs.putBoolean( "AutoAddECM", chkAutoAddECM.isSelected() );

        if( chkCustomPercents.isSelected() ) {
            Prefs.putInt( "ArmorCTRPercent", Integer.parseInt( txtCTRArmor.getText() ) );
            Prefs.putInt( "ArmorSTRPercent", Integer.parseInt( txtSTRArmor.getText() ) );
        } else {
            Prefs.putInt( "ArmorCTRPercent", MechArmor.DEFAULT_CTR_ARMOR_PERCENT );
            Prefs.putInt( "ArmorSTRPercent", MechArmor.DEFAULT_STR_ARMOR_PERCENT );
        }
        if( rdoArmorTorsoPriority.isSelected() ) {
            Prefs.putInt( "ArmorPriority", MechArmor.ARMOR_PRIORITY_TORSO );
        } else if( rdoArmorArmPriority.isSelected() ) {
            Prefs.putInt( "ArmorPriority", MechArmor.ARMOR_PRIORITY_ARMS );
        } else {
            Prefs.putInt( "ArmorPriority", MechArmor.ARMOR_PRIORITY_LEGS );
        }

        Prefs.putInt( "ColorEmptyItemFG", lblColorEmpty.getForeground().getRGB() );
        Prefs.putInt( "ColorEmptyItemBG", lblColorEmpty.getBackground().getRGB() );
        Prefs.putInt( "ColorNormalItemFG", lblColorNormal.getForeground().getRGB() );
        Prefs.putInt( "ColorNormalItemBG", lblColorNormal.getBackground().getRGB() );
        Prefs.putInt( "ColorArmoredItemFG", lblColorArmored.getForeground().getRGB() );
        Prefs.putInt( "ColorArmoredItemBG", lblColorArmored.getBackground().getRGB() );
        Prefs.putInt( "ColorLinkedItemFG", lblColorLinked.getForeground().getRGB() );
        Prefs.putInt( "ColorLinkedItemBG", lblColorLinked.getBackground().getRGB() );
        Prefs.putInt( "ColorLockedItemFG", lblColorLocked.getForeground().getRGB() );
        Prefs.putInt( "ColorLockedItemBG", lblColorLocked.getBackground().getRGB() );
        Prefs.putInt( "ColorHiLiteItemFG", lblColorHilite.getForeground().getRGB() );
        Prefs.putInt( "ColorHiLiteItemBG", lblColorHilite.getBackground().getRGB() );

        if( rdoNormalSize.isSelected() ) {
            Prefs.putInt( "SSWScreenSize", saw.Constants.SCREEN_SIZE_NORMAL );
        } else if( rdoWidescreen.isSelected() ) {
            Prefs.putInt( "SSWScreenSize", saw.Constants.SCREEN_SIZE_WIDE_1280 );
        }
    }

    private void SetDefaults() {
        Prefs.put( "NewMech_RulesLevel", "Tournament Legal" );
        Prefs.put( "NewMech_Era", "Age of War/Star League" );
        Prefs.put( "NewMech_Techbase", "Inner Sphere" );
        Prefs.put( "NewMech_Heatsinks", "Single Heat Sink" );
        Prefs.put( "HTMLExportPath", "none" );
        Prefs.put( "TXTExportPath", "none" );
        Prefs.put( "MTFExportPath", "none" );
        Prefs.put( "AmmoNamePrintFormat", "@%P (%L)" );
        Prefs.put( "AmmoNameExportFormat", "@%P (%L)" );

        Prefs.putBoolean( "HeatExcludeOS", false );
        Prefs.putBoolean( "HeatExcludeRear", false );
        Prefs.putBoolean( "HeatExcludeEquips", false );
        Prefs.putBoolean( "HeatExcludeSystems", false );
        Prefs.putBoolean( "HeatExcludeJumpMP", false );
        Prefs.putBoolean( "HeatExcludeAllMP", false );
        Prefs.putBoolean( "HeatACFullRate", false );
        Prefs.putBoolean( "UseMaxArmorInstead", false );
        Prefs.putBoolean( "ArmorUseCustomPercent", false );
        Prefs.putBoolean( "ArmorMaxHead", true );
        Prefs.putBoolean( "ExportSortOut", false );
        Prefs.putBoolean( "AmmoGroupAtBottom", true );
        Prefs.putBoolean( "CheckUpdatesAtStartup", false );
        Prefs.putBoolean( "LoadLastMech", false );
        Prefs.putBoolean( "AutoAddECM", true );

        Prefs.putInt( "ArmorCTRPercent", MechArmor.DEFAULT_CTR_ARMOR_PERCENT );
        Prefs.putInt( "ArmorSTRPercent", MechArmor.DEFAULT_STR_ARMOR_PERCENT );
        Prefs.putInt( "ArmorPriority", MechArmor.ARMOR_PRIORITY_TORSO );

        Prefs.putInt( "ColorEmptyItemFG", -16777216 );
        Prefs.putInt( "ColorEmptyItemBG", -6684775 );
        Prefs.putInt( "ColorNormalItemFG", -16777216 );
        Prefs.putInt( "ColorNormalItemBG", -10027009 );
        Prefs.putInt( "ColorArmoredItemFG", -1 );
        Prefs.putInt( "ColorArmoredItemBG", -6710887 );
        Prefs.putInt( "ColorLinkedItemFG", -16777216 );
        Prefs.putInt( "ColorLinkedItemBG", -3618616 );
        Prefs.putInt( "ColorLockedItemFG", -3342337 );
        Prefs.putInt( "ColorLockedItemBG", -16777216 );
        Prefs.putInt( "ColorHiLiteItemFG", -16777216 );
        Prefs.putInt( "ColorHiLiteItemBG", -52 );

        Prefs.putInt( "SSWScreenSize", saw.Constants.SCREEN_SIZE_NORMAL );

        SetState();
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

        btgHeadArmor = new javax.swing.ButtonGroup();
        btgArmorPriority = new javax.swing.ButtonGroup();
        btgExportSort = new javax.swing.ButtonGroup();
        btgScreenSize = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlConstruction = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cmbRulesLevel = new javax.swing.JComboBox();
        cmbEra = new javax.swing.JComboBox();
        cmbTechbase = new javax.swing.JComboBox();
        cmbHeatSinks = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        chkCustomPercents = new javax.swing.JCheckBox();
        lblCTRArmor = new javax.swing.JLabel();
        lblSTRArmor = new javax.swing.JLabel();
        txtCTRArmor = new javax.swing.JTextField();
        txtSTRArmor = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        rdoArmorTorsoPriority = new javax.swing.JRadioButton();
        rdoArmorMaxHead = new javax.swing.JRadioButton();
        rdoArmorEqualHead = new javax.swing.JRadioButton();
        rdoArmorArmPriority = new javax.swing.JRadioButton();
        rdoArmorLegPriority = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        chkMaxNotInt = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        chkHeatOSWeapons = new javax.swing.JCheckBox();
        chkHeatRearWeapons = new javax.swing.JCheckBox();
        chkHeatEquip = new javax.swing.JCheckBox();
        chkHeatSystems = new javax.swing.JCheckBox();
        chkHeatJumpMP = new javax.swing.JCheckBox();
        chkHeatAllMP = new javax.swing.JCheckBox();
        chkHeatUAC = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        chkAutoAddECM = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        lblColorEmpty = new javax.swing.JLabel();
        lblColorNormal = new javax.swing.JLabel();
        lblColorArmored = new javax.swing.JLabel();
        lblColorLinked = new javax.swing.JLabel();
        lblColorLocked = new javax.swing.JLabel();
        lblColorHilite = new javax.swing.JLabel();
        btnColorEmptyFG = new javax.swing.JButton();
        btnColorEmptyBG = new javax.swing.JButton();
        btnColorNormalFG = new javax.swing.JButton();
        btnColorNormalBG = new javax.swing.JButton();
        btnColorArmoredFG = new javax.swing.JButton();
        btnColorArmoredBG = new javax.swing.JButton();
        btnColorLinkedFG = new javax.swing.JButton();
        btnColorLinkedBG = new javax.swing.JButton();
        btnColorLockedFG = new javax.swing.JButton();
        btnColorLockedBG = new javax.swing.JButton();
        btnColorHiliteFG = new javax.swing.JButton();
        btnColorHiliteBG = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        rdoExportSortOut = new javax.swing.JRadioButton();
        rdoExportSortIn = new javax.swing.JRadioButton();
        chkGroupAmmoAtBottom = new javax.swing.JCheckBox();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnAmmoNameExportInfo = new javax.swing.JButton();
        txtAmmoExportName = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        btnAmmoNameInfo = new javax.swing.JButton();
        txtAmmoPrintName = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        txtHTMLPath = new javax.swing.JTextField();
        txtTXTPath = new javax.swing.JTextField();
        txtMTFPath = new javax.swing.JTextField();
        btnHTMLPath = new javax.swing.JButton();
        btnTXTPath = new javax.swing.JButton();
        btnMTFPath = new javax.swing.JButton();
        chkLoadLastMech = new javax.swing.JCheckBox();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtImagePath = new javax.swing.JTextField();
        btnDefaultImagePath = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        chkUpdateStartup = new javax.swing.JCheckBox();
        jPanel14 = new javax.swing.JPanel();
        rdoNormalSize = new javax.swing.JRadioButton();
        rdoWidescreen = new javax.swing.JRadioButton();
        lblScreenSizeNotice = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnSetDefaults = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        btnExport = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(101, 83));

        pnlConstruction.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "New 'Mech Defaults"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        cmbRulesLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Introductory", "Tournament Legal", "Advanced Rules", "Experimental Tech", "Era Specific" }));
        cmbRulesLevel.setSelectedIndex(1);
        cmbRulesLevel.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbRulesLevel.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbRulesLevel.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbRulesLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbRulesLevelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel1.add(cmbRulesLevel, gridBagConstraints);

        cmbEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Age of War/Star League", "Succession Wars", "Clan Invasion", "Dark Ages", "All Eras (non-canon)" }));
        cmbEra.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbEra.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbEra.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbEra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEraActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel1.add(cmbEra, gridBagConstraints);

        cmbTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan", "Mixed" }));
        cmbTechbase.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbTechbase.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbTechbase.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbTechbase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTechbaseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel1.add(cmbTechbase, gridBagConstraints);

        cmbHeatSinks.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Single Heat Sinks", "Double Heat Sinks", "Laser Heat Sinks", "Compact Heat Sinks" }));
        cmbHeatSinks.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbHeatSinks.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbHeatSinks.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel1.add(cmbHeatSinks, gridBagConstraints);

        jLabel1.setText("Rules Level:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Era:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Techbase:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Heat Sinks:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 4);
        jPanel1.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlConstruction.add(jPanel1, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Armor Options"));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        chkCustomPercents.setText("Use Custom Distribution Percentages");
        chkCustomPercents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCustomPercentsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel2.add(chkCustomPercents, gridBagConstraints);

        lblCTRArmor.setText("Percentage CTR to CT:");
        lblCTRArmor.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel2.add(lblCTRArmor, gridBagConstraints);

        lblSTRArmor.setText("Percentage STR to ST:");
        lblSTRArmor.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(lblSTRArmor, gridBagConstraints);

        txtCTRArmor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCTRArmor.setText("25");
        txtCTRArmor.setEnabled(false);
        txtCTRArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        txtCTRArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        txtCTRArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel2.add(txtCTRArmor, gridBagConstraints);

        txtSTRArmor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSTRArmor.setText("25");
        txtSTRArmor.setEnabled(false);
        txtSTRArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        txtSTRArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        txtSTRArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(txtSTRArmor, gridBagConstraints);

        jLabel7.setText("When automatically allocating armor:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel2.add(jLabel7, gridBagConstraints);

        btgArmorPriority.add(rdoArmorTorsoPriority);
        rdoArmorTorsoPriority.setSelected(true);
        rdoArmorTorsoPriority.setText("Prioritize Torso Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel2.add(rdoArmorTorsoPriority, gridBagConstraints);

        btgHeadArmor.add(rdoArmorMaxHead);
        rdoArmorMaxHead.setSelected(true);
        rdoArmorMaxHead.setText("Maximize Head Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel2.add(rdoArmorMaxHead, gridBagConstraints);

        btgHeadArmor.add(rdoArmorEqualHead);
        rdoArmorEqualHead.setText("Allocate Head armor equally");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel2.add(rdoArmorEqualHead, gridBagConstraints);

        btgArmorPriority.add(rdoArmorArmPriority);
        rdoArmorArmPriority.setText("Prioritize Arm Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel2.add(rdoArmorArmPriority, gridBagConstraints);

        btgArmorPriority.add(rdoArmorLegPriority);
        rdoArmorLegPriority.setText("Prioritize Leg Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel2.add(rdoArmorLegPriority, gridBagConstraints);

        jLabel8.setText("and...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        jPanel2.add(jLabel8, gridBagConstraints);

        chkMaxNotInt.setText("<html>Show maximum armor instead of<br />internal points on Armor tab.</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(chkMaxNotInt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlConstruction.add(jPanel2, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Heat Options"));
        jPanel8.setLayout(new java.awt.GridBagLayout());

        chkHeatOSWeapons.setText("Exclude OS weapon heat");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel8.add(chkHeatOSWeapons, gridBagConstraints);

        chkHeatRearWeapons.setText("Exclude rear-facing weapon heat");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel8.add(chkHeatRearWeapons, gridBagConstraints);

        chkHeatEquip.setText("Exclude equipment heat (AMS, etc...)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel8.add(chkHeatEquip, gridBagConstraints);

        chkHeatSystems.setText("Exclude armor and systems heat");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel8.add(chkHeatSystems, gridBagConstraints);

        chkHeatJumpMP.setText("Exclude jumping MP heat");
        chkHeatJumpMP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHeatJumpMPActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel8.add(chkHeatJumpMP, gridBagConstraints);

        chkHeatAllMP.setText("Exclude ALL movement heat");
        chkHeatAllMP.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        jPanel8.add(chkHeatAllMP, gridBagConstraints);

        chkHeatUAC.setText("Ultra and Rotary ACs fire at full rate");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel8.add(chkHeatUAC, gridBagConstraints);

        jLabel19.setText("Non-BV Heat Calculations:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel8.add(jLabel19, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlConstruction.add(jPanel8, gridBagConstraints);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Usability Options"));
        jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13, javax.swing.BoxLayout.LINE_AXIS));

        chkAutoAddECM.setText("Auto-add ECM suite if needed");
        jPanel13.add(chkAutoAddECM);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlConstruction.add(jPanel13, gridBagConstraints);

        jTabbedPane1.addTab("Construction", pnlConstruction);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Allocation Colors"));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        lblColorEmpty.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblColorEmpty.setText("Empty Location");
        lblColorEmpty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColorEmpty.setMaximumSize(new java.awt.Dimension(135, 19));
        lblColorEmpty.setMinimumSize(new java.awt.Dimension(135, 19));
        lblColorEmpty.setOpaque(true);
        lblColorEmpty.setPreferredSize(new java.awt.Dimension(135, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel5.add(lblColorEmpty, gridBagConstraints);

        lblColorNormal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblColorNormal.setText("Normal Item");
        lblColorNormal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColorNormal.setMaximumSize(new java.awt.Dimension(135, 19));
        lblColorNormal.setMinimumSize(new java.awt.Dimension(135, 19));
        lblColorNormal.setOpaque(true);
        lblColorNormal.setPreferredSize(new java.awt.Dimension(135, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel5.add(lblColorNormal, gridBagConstraints);

        lblColorArmored.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblColorArmored.setText("Armored Item");
        lblColorArmored.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColorArmored.setMaximumSize(new java.awt.Dimension(135, 19));
        lblColorArmored.setMinimumSize(new java.awt.Dimension(135, 19));
        lblColorArmored.setOpaque(true);
        lblColorArmored.setPreferredSize(new java.awt.Dimension(135, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel5.add(lblColorArmored, gridBagConstraints);

        lblColorLinked.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblColorLinked.setText("Linked in Location");
        lblColorLinked.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColorLinked.setMaximumSize(new java.awt.Dimension(135, 19));
        lblColorLinked.setMinimumSize(new java.awt.Dimension(135, 19));
        lblColorLinked.setOpaque(true);
        lblColorLinked.setPreferredSize(new java.awt.Dimension(135, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel5.add(lblColorLinked, gridBagConstraints);

        lblColorLocked.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblColorLocked.setText("Locked in Location");
        lblColorLocked.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColorLocked.setMaximumSize(new java.awt.Dimension(135, 19));
        lblColorLocked.setMinimumSize(new java.awt.Dimension(135, 19));
        lblColorLocked.setOpaque(true);
        lblColorLocked.setPreferredSize(new java.awt.Dimension(135, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel5.add(lblColorLocked, gridBagConstraints);

        lblColorHilite.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblColorHilite.setText("Allocation HiLite");
        lblColorHilite.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColorHilite.setMaximumSize(new java.awt.Dimension(135, 19));
        lblColorHilite.setMinimumSize(new java.awt.Dimension(135, 19));
        lblColorHilite.setOpaque(true);
        lblColorHilite.setPreferredSize(new java.awt.Dimension(135, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel5.add(lblColorHilite, gridBagConstraints);

        btnColorEmptyFG.setText("Fore");
        btnColorEmptyFG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorEmptyFGActionPerformed(evt);
            }
        });
        jPanel5.add(btnColorEmptyFG, new java.awt.GridBagConstraints());

        btnColorEmptyBG.setText("Back");
        btnColorEmptyBG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorEmptyBGActionPerformed(evt);
            }
        });
        jPanel5.add(btnColorEmptyBG, new java.awt.GridBagConstraints());

        btnColorNormalFG.setText("Fore");
        btnColorNormalFG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorNormalFGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel5.add(btnColorNormalFG, gridBagConstraints);

        btnColorNormalBG.setText("Back");
        btnColorNormalBG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorNormalBGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanel5.add(btnColorNormalBG, gridBagConstraints);

        btnColorArmoredFG.setText("Fore");
        btnColorArmoredFG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorArmoredFGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel5.add(btnColorArmoredFG, gridBagConstraints);

        btnColorArmoredBG.setText("Back");
        btnColorArmoredBG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorArmoredBGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel5.add(btnColorArmoredBG, gridBagConstraints);

        btnColorLinkedFG.setText("Fore");
        btnColorLinkedFG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorLinkedFGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanel5.add(btnColorLinkedFG, gridBagConstraints);

        btnColorLinkedBG.setText("Back");
        btnColorLinkedBG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorLinkedBGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        jPanel5.add(btnColorLinkedBG, gridBagConstraints);

        btnColorLockedFG.setText("Fore");
        btnColorLockedFG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorLockedFGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        jPanel5.add(btnColorLockedFG, gridBagConstraints);

        btnColorLockedBG.setText("Back");
        btnColorLockedBG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorLockedBGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        jPanel5.add(btnColorLockedBG, gridBagConstraints);

        btnColorHiliteFG.setText("Fore");
        btnColorHiliteFG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorHiliteFGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        jPanel5.add(btnColorHiliteFG, gridBagConstraints);

        btnColorHiliteBG.setText("Back");
        btnColorHiliteBG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnColorHiliteBGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        jPanel5.add(btnColorHiliteBG, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanel4.add(jPanel5, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Export Options"));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel16.setText("<html>When sorting the equipment stat<br />blocks for export to TXT or HTML:</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        jPanel6.add(jLabel16, gridBagConstraints);

        btgExportSort.add(rdoExportSortOut);
        rdoExportSortOut.setText("Sort from the center of the mech out");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel6.add(rdoExportSortOut, gridBagConstraints);

        btgExportSort.add(rdoExportSortIn);
        rdoExportSortIn.setSelected(true);
        rdoExportSortIn.setText("Sort from the arms in");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel6.add(rdoExportSortIn, gridBagConstraints);

        chkGroupAmmoAtBottom.setSelected(true);
        chkGroupAmmoAtBottom.setText("Group Ammo at the bottom of the stat block");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel6.add(chkGroupAmmoAtBottom, gridBagConstraints);

        jLabel17.setText("(HD, CT, RT, LT, RA, LA, RL, LL)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel6.add(jLabel17, gridBagConstraints);

        jLabel18.setText("(RA, LA, RT, LT, CT, HD, RL, LL)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel6.add(jLabel18, gridBagConstraints);

        jLabel20.setText("Export Ammo Format");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel6.add(jLabel20, gridBagConstraints);

        btnAmmoNameExportInfo.setText("?");
        btnAmmoNameExportInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAmmoNameExportInfoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel6.add(btnAmmoNameExportInfo, gridBagConstraints);

        txtAmmoExportName.setText("jTextField4");
        txtAmmoExportName.setMaximumSize(new java.awt.Dimension(120, 20));
        txtAmmoExportName.setMinimumSize(new java.awt.Dimension(120, 20));
        txtAmmoExportName.setPreferredSize(new java.awt.Dimension(120, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel6.add(txtAmmoExportName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel4.add(jPanel6, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Printing Options"));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel9.setText("Ammo Print Format");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(jLabel9, gridBagConstraints);

        btnAmmoNameInfo.setText("?");
        btnAmmoNameInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAmmoNameInfoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel3.add(btnAmmoNameInfo, gridBagConstraints);

        txtAmmoPrintName.setText("jTextField3");
        txtAmmoPrintName.setMaximumSize(new java.awt.Dimension(120, 20));
        txtAmmoPrintName.setMinimumSize(new java.awt.Dimension(120, 20));
        txtAmmoPrintName.setPreferredSize(new java.awt.Dimension(120, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPanel3.add(txtAmmoPrintName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanel4.add(jPanel3, gridBagConstraints);

        jTabbedPane1.addTab("Functionality", jPanel4);

        jPanel7.setPreferredSize(new java.awt.Dimension(9, 381));
        jPanel7.setLayout(new java.awt.GridBagLayout());

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Program Paths"));
        jPanel10.setLayout(new java.awt.GridBagLayout());

        txtHTMLPath.setText("jTextField6");
        txtHTMLPath.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel10.add(txtHTMLPath, gridBagConstraints);

        txtTXTPath.setText("jTextField7");
        txtTXTPath.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel10.add(txtTXTPath, gridBagConstraints);

        txtMTFPath.setText("jTextField8");
        txtMTFPath.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        jPanel10.add(txtMTFPath, gridBagConstraints);

        btnHTMLPath.setText("...");
        btnHTMLPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHTMLPathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel10.add(btnHTMLPath, gridBagConstraints);

        btnTXTPath.setText("...");
        btnTXTPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTXTPathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        jPanel10.add(btnTXTPath, gridBagConstraints);

        btnMTFPath.setText("...");
        btnMTFPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMTFPathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        jPanel10.add(btnMTFPath, gridBagConstraints);

        chkLoadLastMech.setText("Load last 'Mech on startup");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(chkLoadLastMech, gridBagConstraints);

        jLabel22.setText("Default HTML export path");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel10.add(jLabel22, gridBagConstraints);

        jLabel23.setText("Default TXT export path");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel10.add(jLabel23, gridBagConstraints);

        jLabel24.setText("Default MTF export path");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel10.add(jLabel24, gridBagConstraints);

        jLabel5.setText("Default Image path");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(jLabel5, gridBagConstraints);

        txtImagePath.setText("jTextField1");
        txtImagePath.setPreferredSize(new java.awt.Dimension(200, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(txtImagePath, gridBagConstraints);

        btnDefaultImagePath.setText("...");
        btnDefaultImagePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultImagePathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        jPanel10.add(btnDefaultImagePath, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel7.add(jPanel10, gridBagConstraints);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Updater Options"));
        jPanel11.setLayout(new java.awt.GridBagLayout());

        chkUpdateStartup.setText("Check for updates on startup");
        chkUpdateStartup.setEnabled(false);
        jPanel11.add(chkUpdateStartup, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel7.add(jPanel11, gridBagConstraints);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Program Screen Size"));
        jPanel14.setLayout(new java.awt.GridBagLayout());

        btgScreenSize.add(rdoNormalSize);
        rdoNormalSize.setSelected(true);
        rdoNormalSize.setText("Normal Size (750 wide)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel14.add(rdoNormalSize, gridBagConstraints);

        btgScreenSize.add(rdoWidescreen);
        rdoWidescreen.setText("Widescreen (1280 wide)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel14.add(rdoWidescreen, gridBagConstraints);

        lblScreenSizeNotice.setText("Change requires restart of SSW.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel14.add(lblScreenSizeNotice, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(jPanel14, gridBagConstraints);

        jTabbedPane1.addTab("Program", jPanel7);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(jTabbedPane1, gridBagConstraints);

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel9.add(btnSave);

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel9.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        getContentPane().add(jPanel9, gridBagConstraints);

        btnSetDefaults.setText("Set Defaults");
        btnSetDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetDefaultsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        getContentPane().add(btnSetDefaults, gridBagConstraints);

        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        jPanel12.add(btnExport);

        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });
        jPanel12.add(btnImport);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 6);
        getContentPane().add(jPanel12, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSetDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetDefaultsActionPerformed
        SetDefaults();
    }//GEN-LAST:event_btnSetDefaultsActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        SaveState();
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnAmmoNameInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAmmoNameInfoActionPerformed
        // display allowable field info
        String msg = "Ammo Print Name formatting.\n\n";
        msg += "Fill in the text field with how you would like your\n";
        msg += "ammunition names to be printed on the recordsheet.\n\n";
        msg += "You can use certain variables for information:\n";
        msg += "%F - Full Name of the ammunition\n";
        msg += "%P - Print Name (usually shorter than Full Name)\n";
        msg += "%L - Size of the ammo lot (number of rounds)\n\n";
        msg += "Example: @%P (%L)\n";
        msg += "Returns: @SRM-6 (15)\n";
        msg += "Example: [Ammo]%P (%L)\n";
        msg += "Returns: [Ammo]SRM-6 (15)\n";
        Media.Messager(this, msg);
    }//GEN-LAST:event_btnAmmoNameInfoActionPerformed

    private void btnAmmoNameExportInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAmmoNameExportInfoActionPerformed
        btnAmmoNameInfoActionPerformed( evt );
    }//GEN-LAST:event_btnAmmoNameExportInfoActionPerformed

    private void chkHeatJumpMPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHeatJumpMPActionPerformed
        if( chkHeatJumpMP.isSelected() ) {
            chkHeatAllMP.setEnabled( true );
        } else {
            chkHeatAllMP.setEnabled( false );
            chkHeatAllMP.setSelected( false );
        }
    }//GEN-LAST:event_chkHeatJumpMPActionPerformed

    private void chkCustomPercentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCustomPercentsActionPerformed
        if( chkCustomPercents.isSelected() ) {
            lblCTRArmor.setEnabled( true );
            txtCTRArmor.setEnabled( true );
            txtCTRArmor.setText( "" + Prefs.getInt( "ArmorCTRPercent", MechArmor.DEFAULT_CTR_ARMOR_PERCENT ) );
            lblSTRArmor.setEnabled( true );
            txtSTRArmor.setEnabled( true );
            txtSTRArmor.setText( "" + Prefs.getInt( "ArmorSTRPercent", MechArmor.DEFAULT_STR_ARMOR_PERCENT ) );
        } else {
            // reset to defaults as well.
            txtCTRArmor.setText( "" + MechArmor.DEFAULT_CTR_ARMOR_PERCENT );
            lblCTRArmor.setEnabled( false );
            txtCTRArmor.setEnabled( false );
            txtSTRArmor.setText( "" + MechArmor.DEFAULT_STR_ARMOR_PERCENT );
            lblSTRArmor.setEnabled( false );
            txtSTRArmor.setEnabled( false );
        }
    }//GEN-LAST:event_chkCustomPercentsActionPerformed

    private void btnColorEmptyFGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorEmptyFGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorEmpty.getForeground());
        if ( newColor != null ) { lblColorEmpty.setForeground( newColor ); }
    }//GEN-LAST:event_btnColorEmptyFGActionPerformed

    private void btnColorEmptyBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorEmptyBGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorEmpty.getBackground());
        if ( newColor != null ) { lblColorEmpty.setBackground( newColor ); }
    }//GEN-LAST:event_btnColorEmptyBGActionPerformed

    private void btnColorNormalFGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorNormalFGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorNormal.getForeground());
        if ( newColor != null ) { lblColorNormal.setForeground( newColor ); }
    }//GEN-LAST:event_btnColorNormalFGActionPerformed

    private void btnColorNormalBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorNormalBGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorNormal.getBackground());
        if ( newColor != null ) { lblColorNormal.setBackground( newColor ); }
    }//GEN-LAST:event_btnColorNormalBGActionPerformed

    private void btnColorArmoredFGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorArmoredFGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorArmored.getForeground());
        if ( newColor != null ) { lblColorArmored.setForeground( newColor ); }
    }//GEN-LAST:event_btnColorArmoredFGActionPerformed

    private void btnColorArmoredBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorArmoredBGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorArmored.getBackground());
        if ( newColor != null ) { lblColorArmored.setBackground( newColor ); }
    }//GEN-LAST:event_btnColorArmoredBGActionPerformed

    private void btnColorLinkedFGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorLinkedFGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorLinked.getForeground());
        if ( newColor != null ) { lblColorLinked.setForeground( newColor ); }
    }//GEN-LAST:event_btnColorLinkedFGActionPerformed

    private void btnColorLinkedBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorLinkedBGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorLinked.getBackground());
        if ( newColor != null ) { lblColorLinked.setBackground( newColor ); }
    }//GEN-LAST:event_btnColorLinkedBGActionPerformed

    private void btnColorLockedFGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorLockedFGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorLocked.getForeground());
        if ( newColor != null ) { lblColorLocked.setForeground( newColor ); }
    }//GEN-LAST:event_btnColorLockedFGActionPerformed

    private void btnColorLockedBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorLockedBGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorLocked.getBackground());
        if ( newColor != null ) { lblColorLocked.setBackground( newColor ); }
    }//GEN-LAST:event_btnColorLockedBGActionPerformed

    private void btnColorHiliteFGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorHiliteFGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorHilite.getForeground());
        if ( newColor != null ) { lblColorHilite.setForeground( newColor ); }
    }//GEN-LAST:event_btnColorHiliteFGActionPerformed

    private void btnColorHiliteBGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnColorHiliteBGActionPerformed
        Color newColor = JColorChooser.showDialog( this, "Choose Foreground Color", lblColorHilite.getBackground());
        if ( newColor != null ) { lblColorHilite.setBackground( newColor ); }
    }//GEN-LAST:event_btnColorHiliteBGActionPerformed

    private void btnHTMLPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHTMLPathActionPerformed
        String path = "";
        String oldpath = Prefs.get( "HTMLExportPath", "" );
        JFileChooser fc = new JFileChooser();

        //Add a custom file filter and disable the default
        //(Accept All) file filter.
        fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        fc.setAcceptAllFileFilterUsed(false);
        if( oldpath != null ) {
            fc.setCurrentDirectory( new File( FileCommon.GetSafeFilename( oldpath ) ) );
        }

        //Show it.
        int returnVal = fc.showDialog( this, "Choose directory");

        //Process the results.  If no file is chosen, the default is used.
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            path = fc.getSelectedFile().getPath();
            txtHTMLPath.setText( path );
        }
    }//GEN-LAST:event_btnHTMLPathActionPerformed

    private void btnTXTPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTXTPathActionPerformed
        String path = "";
        String oldpath = Prefs.get( "TXTExportPath", "" );
        JFileChooser fc = new JFileChooser();

        //Add a custom file filter and disable the default
        //(Accept All) file filter.
        fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        fc.setAcceptAllFileFilterUsed(false);
        if( oldpath != null ) {
            fc.setCurrentDirectory( new File( FileCommon.GetSafeFilename( oldpath ) ) );
        }

        //Show it.
        int returnVal = fc.showDialog( this, "Choose directory" );

        //Process the results.  If no file is chosen, the default is used.
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            path = fc.getSelectedFile().getPath();
            txtTXTPath.setText( path );
        }
    }//GEN-LAST:event_btnTXTPathActionPerformed

    private void btnMTFPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMTFPathActionPerformed
        String path = "";
        String oldpath = Prefs.get( "MTFExportPath", "" );
        JFileChooser fc = new JFileChooser();

        //Add a custom file filter and disable the default
        //(Accept All) file filter.
        fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        fc.setAcceptAllFileFilterUsed(false);
        if( oldpath != null ) {
            fc.setCurrentDirectory( new File( FileCommon.GetSafeFilename( oldpath ) ) );
        }

        //Show it.
        int returnVal = fc.showDialog( this, "Choose directory");

        //Process the results.  If no file is chosen, the default is used.
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            path = fc.getSelectedFile().getPath();
            txtMTFPath.setText( path );
        }
    }//GEN-LAST:event_btnMTFPathActionPerformed

    private void cmbEraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEraActionPerformed
        switch( cmbEra.getSelectedIndex() ) {
            case AvailableCode.ERA_STAR_LEAGUE:
                cmbTechbase.setModel(new javax.swing.DefaultComboBoxModel( new String[] { "Inner Sphere" }));
                switch( cmbRulesLevel.getSelectedIndex() ) {
                    case AvailableCode.RULES_INTRODUCTORY:
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink" } ) );
                        break;
                    default:
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink" } ) );
                        break;
                }
                break;
            case AvailableCode.ERA_SUCCESSION:
                switch( cmbRulesLevel.getSelectedIndex() ) {
                    case AvailableCode.RULES_INTRODUCTORY:
                        cmbTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere" }));
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink" } ) );
                        break;
                    case AvailableCode.RULES_TOURNAMENT:
                        cmbTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink" } ) );
                        break;
                    case AvailableCode.RULES_EXPERIMENTAL: case AvailableCode.RULES_ERA_SPECIFIC:
                        cmbTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan", "Mixed" }));
                        if( cmbTechbase.getSelectedIndex() == 3 ) {
                            cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "(IS) Double Heat Sink", "(CL) Double Heat Sink" } ) );
                        } else {
                            cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink" } ) );
                        }
                    default:
                        cmbTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink" } ) );
                        break;
                }
                break;
            case AvailableCode.ERA_CLAN_INVASION: case AvailableCode.ERA_DARK_AGES: case AvailableCode.ERA_ALL:
                switch( cmbRulesLevel.getSelectedIndex() ) {
                    case AvailableCode.RULES_INTRODUCTORY:
                        cmbTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere" }));
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink" } ) );
                        break;
                    case AvailableCode.RULES_TOURNAMENT:
                        cmbTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink" } ) );
                        break;
                    case AvailableCode.RULES_EXPERIMENTAL: case AvailableCode.RULES_ERA_SPECIFIC:
                        cmbTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan", "Mixed" }));
                        if( cmbTechbase.getSelectedIndex() == AvailableCode.TECH_BOTH ) {
                            cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "(IS) Double Heat Sink", "(CL) Double Heat Sink", "Compact Heat Sink", "Laser Heat Sink" } ) );
                        } else {
                            cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink", "Compact Heat Sink", "Laser Heat Sink" } ) );
                        }
                    default:
                        cmbTechbase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink" } ) );
                        break;
                }
                break;
        }
    }//GEN-LAST:event_cmbEraActionPerformed

    private void cmbRulesLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRulesLevelActionPerformed
        cmbEraActionPerformed( evt );
    }//GEN-LAST:event_cmbRulesLevelActionPerformed

    private void cmbTechbaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTechbaseActionPerformed
        switch( cmbTechbase.getSelectedIndex() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                switch( cmbRulesLevel.getSelectedIndex() ) {
                    case AvailableCode.RULES_INTRODUCTORY:
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink" } ) );
                        break;
                    case AvailableCode.RULES_EXPERIMENTAL: case AvailableCode.RULES_ERA_SPECIFIC:
                        if( cmbEra.getSelectedIndex() >= AvailableCode.ERA_CLAN_INVASION ) {
                            cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink", "Compact Heat Sink" } ) );
                        } else {
                            cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink" } ) );
                        }
                        break;
                    default:
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink" } ) );
                        break;
                }
                break;
            case AvailableCode.TECH_CLAN:
                switch( cmbRulesLevel.getSelectedIndex() ) {
                    case AvailableCode.RULES_EXPERIMENTAL: case AvailableCode.RULES_ERA_SPECIFIC:
                        if( cmbEra.getSelectedIndex() >= AvailableCode.ERA_CLAN_INVASION ) {
                            cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink", "Laser Heat Sink" } ) );
                        } else {
                            cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink" } ) );
                        }
                        break;
                    default:
                        cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "Double Heat Sink" } ) );
                        break;
                }
                break;
            case AvailableCode.TECH_BOTH:
                if( cmbEra.getSelectedIndex() >= AvailableCode.ERA_CLAN_INVASION ) {
                    cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "(IS) Double Heat Sink", "(CL) Double Heat Sink", "Compact Heat Sink", "Laser Heat Sink" } ) );
                } else {
                    cmbHeatSinks.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Single Heat Sink", "(IS) Double Heat Sink", "(CL) Double Heat Sink" } ) );
                }
                break;
        }
    }//GEN-LAST:event_cmbTechbaseActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        FileOutputStream fos = null;
        Media media = new Media();
        File prefFile = media.SelectFile("prefs.xml", "xml", "Export Preferences To...");
        SaveState();
        try {
            fos = new FileOutputStream(prefFile.getCanonicalPath());
            Prefs.exportSubtree(fos);
            fos.close();
            Media.Messager(this, "Preferences Exported.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(dlgPrefs.class.getName()).log(Level.SEVERE, null, ex);
        } catch ( Exception e ) {
            Logger.getLogger(dlgPrefs.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(dlgPrefs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnExportActionPerformed

    @SuppressWarnings("static-access")
    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        FileInputStream fis = null;
        Media media = new Media();
        try {
            File prefPath = media.SelectFile("prefs.xml", "xml", "Select Preferences File");
            fis = new FileInputStream(prefPath);
            try {
                Prefs.importPreferences(fis);
                SetState();
                Media.Messager(this, "Preferences Imported.");
            } catch (IOException ex) {
                Logger.getLogger(dlgPrefs.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidPreferencesFormatException ex) {
                Logger.getLogger(dlgPrefs.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(dlgPrefs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImportActionPerformed

    private void btnDefaultImagePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultImagePathActionPerformed
        Media media = new Media();
        txtImagePath.setText(media.GetDirectorySelection(this, txtImagePath.getText()));
}//GEN-LAST:event_btnDefaultImagePathActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btgArmorPriority;
    private javax.swing.ButtonGroup btgExportSort;
    private javax.swing.ButtonGroup btgHeadArmor;
    private javax.swing.ButtonGroup btgScreenSize;
    private javax.swing.JButton btnAmmoNameExportInfo;
    private javax.swing.JButton btnAmmoNameInfo;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnColorArmoredBG;
    private javax.swing.JButton btnColorArmoredFG;
    private javax.swing.JButton btnColorEmptyBG;
    private javax.swing.JButton btnColorEmptyFG;
    private javax.swing.JButton btnColorHiliteBG;
    private javax.swing.JButton btnColorHiliteFG;
    private javax.swing.JButton btnColorLinkedBG;
    private javax.swing.JButton btnColorLinkedFG;
    private javax.swing.JButton btnColorLockedBG;
    private javax.swing.JButton btnColorLockedFG;
    private javax.swing.JButton btnColorNormalBG;
    private javax.swing.JButton btnColorNormalFG;
    private javax.swing.JButton btnDefaultImagePath;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHTMLPath;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnMTFPath;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSetDefaults;
    private javax.swing.JButton btnTXTPath;
    private javax.swing.JCheckBox chkAutoAddECM;
    private javax.swing.JCheckBox chkCustomPercents;
    private javax.swing.JCheckBox chkGroupAmmoAtBottom;
    private javax.swing.JCheckBox chkHeatAllMP;
    private javax.swing.JCheckBox chkHeatEquip;
    private javax.swing.JCheckBox chkHeatJumpMP;
    private javax.swing.JCheckBox chkHeatOSWeapons;
    private javax.swing.JCheckBox chkHeatRearWeapons;
    private javax.swing.JCheckBox chkHeatSystems;
    private javax.swing.JCheckBox chkHeatUAC;
    private javax.swing.JCheckBox chkLoadLastMech;
    private javax.swing.JCheckBox chkMaxNotInt;
    private javax.swing.JCheckBox chkUpdateStartup;
    private javax.swing.JComboBox cmbEra;
    private javax.swing.JComboBox cmbHeatSinks;
    private javax.swing.JComboBox cmbRulesLevel;
    private javax.swing.JComboBox cmbTechbase;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCTRArmor;
    private javax.swing.JLabel lblColorArmored;
    private javax.swing.JLabel lblColorEmpty;
    private javax.swing.JLabel lblColorHilite;
    private javax.swing.JLabel lblColorLinked;
    private javax.swing.JLabel lblColorLocked;
    private javax.swing.JLabel lblColorNormal;
    private javax.swing.JLabel lblSTRArmor;
    private javax.swing.JLabel lblScreenSizeNotice;
    private javax.swing.JPanel pnlConstruction;
    private javax.swing.JRadioButton rdoArmorArmPriority;
    private javax.swing.JRadioButton rdoArmorEqualHead;
    private javax.swing.JRadioButton rdoArmorLegPriority;
    private javax.swing.JRadioButton rdoArmorMaxHead;
    private javax.swing.JRadioButton rdoArmorTorsoPriority;
    private javax.swing.JRadioButton rdoExportSortIn;
    private javax.swing.JRadioButton rdoExportSortOut;
    private javax.swing.JRadioButton rdoNormalSize;
    private javax.swing.JRadioButton rdoWidescreen;
    private javax.swing.JTextField txtAmmoExportName;
    private javax.swing.JTextField txtAmmoPrintName;
    private javax.swing.JTextField txtCTRArmor;
    private javax.swing.JTextField txtHTMLPath;
    private javax.swing.JTextField txtImagePath;
    private javax.swing.JTextField txtMTFPath;
    private javax.swing.JTextField txtSTRArmor;
    private javax.swing.JTextField txtTXTPath;
    // End of variables declaration//GEN-END:variables

}

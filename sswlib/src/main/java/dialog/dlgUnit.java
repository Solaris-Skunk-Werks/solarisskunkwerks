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

package dialog;

import Force.*;
import Print.BFBPrinter;
import Print.PagePrinter;
import Print.PrintMech;
import filehandlers.Media;
import filehandlers.TXTWriter;
import battleforce.BattleForceStats;
import common.CommonTools;
import common.Constants;
import components.ifMechLoadout;

import filehandlers.*;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.SpinnerNumberModel;

public class dlgUnit extends javax.swing.JDialog {
    /**
     *
     */
    private static final long serialVersionUID = -8879297096883466444L;
    private Force force;
    private Unit unit;
    private boolean Default = true;
    public boolean Result = false;
    private Skills skills;
    private Warriors warriors;
    private ImageTracker imageTracker;
    private NameGenerator gen = new NameGenerator();
    private Preferences Prefs = Preferences.userRoot().node( Constants.BFBPrefs );
    private Preferences sswPrefs = Preferences.userRoot().node( Constants.SSWPrefs );

    public dlgUnit(java.awt.Frame parent, boolean modal, Force f, Unit u, ImageTracker imagetracker) {
        super(parent, modal);
        force = f;
        unit = u;
        imageTracker = imagetracker;
        initComponents();

        warriors = new Warriors(Prefs.get("LastPSNFile", "/Data/Personnel/warriorlist.psn"));
        setupFrame();
    }

    private void setupFrame() {
        lblModel.setText(unit.TypeModel);
        lblTonnage.setText(unit.Tonnage + " Tons");
        txtMechwarrior.setText(unit.getMechwarrior());
        cmbGunnery.setSelectedIndex(unit.getGunnery());
        cmbPiloting.setSelectedIndex(unit.getPiloting());
        chkC3Active.setSelected(unit.UsingC3);
        txtMod.setText(unit.MiscMod+"");
        lblFilename.setForeground(new Color(Color.black.getRGB()));
        lblFilename.setText(CommonTools.shortenPath(unit.Filename, 100));
        tpnMechwarriorQuirks.setText(unit.getMechwarriorQuirks());
        tpnBattleMechQuirks.setText(unit.UnitQuirks);
        spnSkillSeperationLimit.setModel(new SpinnerNumberModel(3, 0, 7, 1));
        lstPersonnel.setModel(warriors.getModel());

        skills = new Skills(unit.BaseBV);
        setSkills();

        String BVBreakdown = "";
        BVBreakdown += "Base:   " + unit.BaseBV + "\n";
        BVBreakdown += "Skills: " + unit.SkillsBV + "\n";
        BVBreakdown += "Mods:   " + unit.ModifierBV + "\n";
        BVBreakdown += "C3:     " + unit.getForceC3BV() + "\n";
        BVBreakdown += "Total:  " + unit.TotalBV + "\n";
        txtBVBreakdown.setText(BVBreakdown);

        unit.LoadUnit();
        switch(unit.UnitType) {
            case CommonTools.BattleMech:
                if ( unit.m != null ) {
                    if ( unit.m.IsOmnimech() ) {
                        String curConfig = unit.m.GetLoadout().GetName();
                        int BV = unit.m.GetCurrentBV();
                        for (int i=0; i < unit.m.GetLoadouts().size(); i++) {
                            ifMechLoadout config = (ifMechLoadout) unit.m.GetLoadouts().get(i);
                            unit.m.SetCurLoadout(config.GetName());
                            cmbConfiguration.addItem(config.GetName() + " (" + unit.m.GetCurrentBV() + ")");
                        }
                        unit.m.SetCurLoadout(curConfig);
                        cmbConfiguration.setSelectedItem(curConfig + " (" + BV + ")");
                        Default = false;
                    } else {
                        pnlConfiguration.setVisible(false);
                    }
                    setC3();
                    setTRO();
                    setImage();
                    setBattleForce();
                    btnSelectMech.setVisible(false);
                } else {
                    pnlConfiguration.setVisible(false);
                    lblFilename.setForeground(new Color(Color.red.getRGB()));
                    btnSelectMech.setVisible(true);
                }
                break;
            case CommonTools.Vehicle:
                if ( unit.v != null ) {
                    if ( unit.v.IsOmni() ) {
                        String curConfig = unit.v.GetLoadout().GetName();
                        int BV = unit.v.GetCurrentBV();
                        for (int i=0; i < unit.v.GetLoadouts().size(); i++) {
                            ifMechLoadout config = (ifMechLoadout) unit.v.GetLoadouts().get(i);
                            unit.v.SetCurLoadout(config.GetName());
                            cmbConfiguration.addItem(config.GetName() + " (" + unit.v.GetCurrentBV() + ")");
                        }
                        unit.v.SetCurLoadout(curConfig);
                        cmbConfiguration.setSelectedItem(curConfig + " (" + BV + ")");
                        Default = false;
                    } else {
                        pnlConfiguration.setVisible(false);
                    }
                    setC3();
                    setTRO();
                    setImage();
                    setBattleForce();
                    btnSelectMech.setVisible(false);
                } else {
                    pnlConfiguration.setVisible(false);
                    lblFilename.setForeground(new Color(Color.red.getRGB()));
                    btnSelectMech.setVisible(true);
                }
                break;
            default:
                pnlConfiguration.setVisible(false);
                lblFilename.setForeground(new Color(Color.red.getRGB()));
                btnSelectMech.setVisible(true);
                break;
        }
    }

    private void setBV() {
        lblBaseBV.setText( String.format("%1$,.0f", unit.BaseBV) );
        lblTotalBV.setText( String.format("%1$,.0f", unit.TotalBV) );
    }

    private void setTRO() {
        switch(unit.UnitType) {
            case CommonTools.BattleMech:
                TXTWriter txt = new TXTWriter(unit.m);
                txt.CurrentLoadoutOnly = true;
                tpnTRO.setText(txt.GetMiniTextExport());
                tpnTRO.setCaretPosition(0);
                break;
            case CommonTools.Vehicle:
                CVTXTWriter ctxt = new CVTXTWriter(unit.v);
                ctxt.CurrentLoadoutOnly = true;
                tpnTRO.setText(ctxt.GetMiniTextExport());
                tpnTRO.setCaretPosition(0);
                break;
        }

    }

    private void setC3() {
        if ( ! unit.HasC3() ) {
            chkC3Active.setSelected(false);
            chkC3Active.setVisible(false);
        }
    }

    private void setImage() {
        lblSSWImage.setText(unit.getImage());
        if ( !unit.getImage().isEmpty() ) {
            try {
                ImageIcon icon = new ImageIcon(unit.getImage());

                if( icon == null ) { return; }

                // See if we need to scale
                int h = icon.getIconHeight();
                int w = icon.getIconWidth();
                if ( w > lblMechImage.getWidth() || h > lblMechImage.getHeight() ) {
                    if ( w > h ) { // resize based on width
                        icon = new ImageIcon(icon.getImage().
                            getScaledInstance(lblMechImage.getWidth(), -1, Image.SCALE_DEFAULT));
                    } else { // resize based on height
                        icon = new ImageIcon(icon.getImage().
                            getScaledInstance(-1, lblMechImage.getHeight(), Image.SCALE_DEFAULT));
                    }
                }

                lblMechImage.setIcon(icon);
            } catch (Exception e) {
                //do nothing
            }
        }
    }

    private void setBattleForce() {
        BattleForceStats stats = null;
        switch(unit.UnitType) {
            case CommonTools.BattleMech:
                stats = new BattleForceStats(unit.m);
                break;
            case CommonTools.Vehicle:
                stats = new BattleForceStats(unit.v);
                break;
        }

        lblBFMV.setText(stats.getMovement());

        lblBFShort.setText(stats.getShort()+"");
        lblBFMedium.setText(stats.getMedium()+"");
        lblBFLong.setText(stats.getLong()+"");
        lblBFExtreme.setText(stats.getExtreme()+"");

        lblBFWt.setText(stats.getWeight()+"");
        lblBFOV.setText(stats.getOverheat()+"");

        lblBFArmor.setText(stats.getArmor()+"");
        lblBFStructure.setText(stats.getInternal()+"");
        lblBFPoints.setText(stats.getPointValue()+"");

        lblBFSA.setText(stats.getAbilitiesString());
    }

    private void setSkills() {
        lstSkills.setModel(skills.getListModel());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrpSkill = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        txtMechwarrior = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbPiloting = new javax.swing.JComboBox();
        lblTonnage = new javax.swing.JLabel();
        cmbGunnery = new javax.swing.JComboBox();
        pnlConfiguration = new javax.swing.JPanel();
        cmbConfiguration = new javax.swing.JComboBox();
        lblConfig = new javax.swing.JLabel();
        txtMod = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        lblTotalBV = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblBaseBV = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblModel = new javax.swing.JLabel();
        chkC3Active = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jToolBar2 = new javax.swing.JToolBar();
        btnSelectMech = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPrint = new javax.swing.JButton();
        btnRandomName = new javax.swing.JButton();
        btnMD = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        spnTRO = new javax.swing.JScrollPane();
        tpnTRO = new javax.swing.JTextPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtBVLimit = new javax.swing.JTextField();
        btnFilter = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        spnSkillSeperationLimit = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        rdoGunnery = new javax.swing.JRadioButton();
        rdoPiloting = new javax.swing.JRadioButton();
        rdoNeither = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstSkills = new javax.swing.JList();
        jLabel10 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        cmbSkillLevel = new javax.swing.JComboBox();
        lblRandomSkill = new javax.swing.JLabel();
        btnRandomGen = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        btnApply = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstPersonnel = new javax.swing.JList();
        btnSelectWarrior = new javax.swing.JButton();
        btnLoadFile = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        btnMechImage = new javax.swing.JButton();
        lblMechImage = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblSSWImage = new javax.swing.JLabel();
        btnClearImage = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tpnBattleMechQuirks = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        tpnMechwarriorQuirks = new javax.swing.JTextPane();
        jPanel10 = new javax.swing.JPanel();
        pnlBFStats = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        lblBFMV = new javax.swing.JLabel();
        lblBFWt = new javax.swing.JLabel();
        lblBFOV = new javax.swing.JLabel();
        lblBFExtreme = new javax.swing.JLabel();
        lblBFShort = new javax.swing.JLabel();
        lblBFMedium = new javax.swing.JLabel();
        lblBFLong = new javax.swing.JLabel();
        lblBFArmor = new javax.swing.JLabel();
        lblBFStructure = new javax.swing.JLabel();
        lblBFSA = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        lblBFPoints = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtBVBreakdown = new javax.swing.JTextPane();
        pnlFile = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblFilename = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modify Unit");

        jLabel4.setText("Gun");

        jLabel3.setText("Mechwarrior");

        cmbPiloting.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7" }));
        cmbPiloting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPilotingActionPerformed(evt);
            }
        });

        lblTonnage.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTonnage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTonnage.setText("100 Tons");

        cmbGunnery.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7" }));
        cmbGunnery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGunneryActionPerformed(evt);
            }
        });

        cmbConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbConfigurationActionPerformed(evt);
            }
        });

        lblConfig.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblConfig.setText("Selected Configuration:");

        javax.swing.GroupLayout pnlConfigurationLayout = new javax.swing.GroupLayout(pnlConfiguration);
        pnlConfiguration.setLayout(pnlConfigurationLayout);
        pnlConfigurationLayout.setHorizontalGroup(
            pnlConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlConfigurationLayout.createSequentialGroup()
                .addComponent(lblConfig, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlConfigurationLayout.setVerticalGroup(
            pnlConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cmbConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblConfig))
        );

        txtMod.setText("1.0");
        txtMod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtModActionPerformed(evt);
            }
        });

        jLabel13.setText("Adjusted:");

        lblTotalBV.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotalBV.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalBV.setText("0,000 BV");

        jLabel8.setText("Base:");

        lblBaseBV.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblBaseBV.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBaseBV.setText("0,000 BV");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblBaseBV)
                    .addComponent(lblTotalBV)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBaseBV)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalBV)
                    .addComponent(jLabel13)))
        );

        jLabel6.setText("MD");

        lblModel.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblModel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblModel.setText("Sirocco SRC-3C BattleMech");

        chkC3Active.setText("C3 Active");
        chkC3Active.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkC3ActiveActionPerformed(evt);
            }
        });

        jLabel5.setText("Plt");

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        btnSelectMech.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plug.png"))); // NOI18N
        btnSelectMech.setText("File");
        btnSelectMech.setFocusable(false);
        btnSelectMech.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSelectMech.setMaximumSize(new java.awt.Dimension(29, 41));
        btnSelectMech.setMinimumSize(new java.awt.Dimension(29, 41));
        btnSelectMech.setPreferredSize(new java.awt.Dimension(29, 41));
        btnSelectMech.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSelectMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectMechActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSelectMech);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk-black.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSave);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus-circle.png"))); // NOI18N
        btnClose.setText("Close");
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar2.add(btnClose);
        jToolBar2.add(jSeparator1);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printer.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPrint);

        btnRandomName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/asterisk.png"))); // NOI18N
        btnRandomName.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnRandomName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRandomNameActionPerformed(evt);
            }
        });

        btnMD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calculator.png"))); // NOI18N
        btnMD.setToolTipText("Calculate MD Mods");
        btnMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnlConfiguration, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(243, 243, 243)
                        .addComponent(lblTonnage))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtMechwarrior, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btnRandomName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(39, 39, 39)
                                .addComponent(jLabel6))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(39, 39, 39)
                                        .addComponent(txtMod, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(1, 1, 1)
                                .addComponent(btnMD, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addComponent(lblModel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkC3Active)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblModel)
                            .addComponent(lblTonnage))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(3, 3, 3)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtMechwarrior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnRandomName)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(3, 3, 3)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtMod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkC3Active)
                                    .addComponent(btnMD)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(500, 397));

        spnTRO.setBorder(null);
        spnTRO.setPreferredSize(new java.awt.Dimension(591, 369));

        tpnTRO.setBorder(null);
        tpnTRO.setEditable(false);
        tpnTRO.setFont(new java.awt.Font("Courier New", 0, 11));
        tpnTRO.setText("--------------------------------------------------------------------------------");
        tpnTRO.setMaximumSize(new java.awt.Dimension(400, 550));
        tpnTRO.setPreferredSize(new java.awt.Dimension(591, 369));
        spnTRO.setViewportView(tpnTRO);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spnTRO, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spnTRO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Technical Readout", jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Limiters"));

        jLabel2.setText("Max BV:");

        txtBVLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBVLimitActionPerformed(evt);
            }
        });
        txtBVLimit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBVLimitKeyReleased(evt);
            }
        });

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        jLabel7.setText("Max Skill Seperation:");

        spnSkillSeperationLimit.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnSkillSeperationLimitStateChanged(evt);
            }
        });

        jLabel11.setText("Minimize:");

        rdoGunnery.setBackground(new java.awt.Color(255, 255, 255));
        btnGrpSkill.add(rdoGunnery);
        rdoGunnery.setText("Gunnery");
        rdoGunnery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoGunneryActionPerformed(evt);
            }
        });

        rdoPiloting.setBackground(new java.awt.Color(255, 255, 255));
        btnGrpSkill.add(rdoPiloting);
        rdoPiloting.setText("Piloting");
        rdoPiloting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoPilotingActionPerformed(evt);
            }
        });

        rdoNeither.setBackground(new java.awt.Color(255, 255, 255));
        btnGrpSkill.add(rdoNeither);
        rdoNeither.setSelected(true);
        rdoNeither.setText("Neither");
        rdoNeither.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoNeitherActionPerformed(evt);
            }
        });

        lstSkills.setModel(new javax.swing.AbstractListModel() {
            /**
             *
             */
            private static final long serialVersionUID = 7250619854461540939L;
            String[] strings = { "0 / 0  (1000)", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstSkills.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstSkillsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lstSkills);

        jLabel10.setForeground(new java.awt.Color(204, 204, 204));
        jLabel10.setText("Double click a skill choice to update the unit");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rdoNeither)
                                    .addComponent(rdoPiloting)
                                    .addComponent(rdoGunnery)))
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtBVLimit))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(spnSkillSeperationLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(btnFilter))))
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtBVLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(spnSkillSeperationLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(rdoNeither))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoGunnery)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoPiloting)
                        .addGap(18, 18, 18)
                        .addComponent(btnFilter))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Random Selection"));

        jLabel12.setText("Skill Level:");

        cmbSkillLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Random", "Green", "Regular", "Veteran", "Elite" }));

        lblRandomSkill.setFont(new java.awt.Font("Arial Black", 1, 18));
        lblRandomSkill.setText("0 / 0");

        btnRandomGen.setText("Generate");
        btnRandomGen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRandomGenActionPerformed(evt);
            }
        });

        jLabel15.setText("Generated Skill:");

        btnApply.setText("Apply");
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSkillLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRandomGen))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(lblRandomSkill)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnApply)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cmbSkillLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRandomGen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(lblRandomSkill)
                    .addComponent(btnApply))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Mechwarrior List"));

        lstPersonnel.setModel(new javax.swing.AbstractListModel() {
            /**
             *
             */
            private static final long serialVersionUID = -7184327738128057082L;
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstPersonnel.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstPersonnel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstPersonnelMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lstPersonnel);

        btnSelectWarrior.setText("Select");
        btnSelectWarrior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectWarriorActionPerformed(evt);
            }
        });

        btnLoadFile.setText("Load");
        btnLoadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSelectWarrior)
                    .addComponent(btnLoadFile, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(btnSelectWarrior)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                        .addComponent(btnLoadFile))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Mechwarrior Skills", jPanel5);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        btnMechImage.setText("Select Image");
        btnMechImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMechImageActionPerformed(evt);
            }
        });

        lblMechImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel14.setText("Image:");

        lblSSWImage.setText("jLabel16");

        btnClearImage.setText("Clear Image");
        btnClearImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearImageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnMechImage)
                            .addComponent(btnClearImage))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMechImage, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSSWImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(158, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblMechImage, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(btnMechImage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClearImage)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(lblSSWImage))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Mech Image", jPanel8);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel16.setText("Mechwarrior Quirks");

        jLabel17.setText("BattleMech Quirks");

        jScrollPane3.setViewportView(tpnBattleMechQuirks);

        jScrollPane4.setViewportView(tpnMechwarriorQuirks);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Quirks", jPanel3);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        pnlBFStats.setBackground(new java.awt.Color(255, 255, 255));

        jLabel66.setText("MV");

        jLabel67.setText("S (+0)");

        jLabel68.setText("M (+2)");

        jLabel69.setText("L (+4)");

        jLabel70.setText("E (+6)");

        jLabel71.setText("Wt.");

        jLabel72.setText("OV");

        jLabel73.setText("Armor:");

        jLabel74.setText("Structure:");

        jLabel75.setText("Special Abilities:");

        lblBFMV.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFMV.setText("0");

        lblBFWt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFWt.setText("1");

        lblBFOV.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFOV.setText("0");

        lblBFExtreme.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFExtreme.setText("0");

        lblBFShort.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFShort.setText("0");

        lblBFMedium.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFMedium.setText("0");

        lblBFLong.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFLong.setText("0");

        lblBFArmor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFArmor.setText("0");

        lblBFStructure.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFStructure.setText("0");

        lblBFSA.setText("Placeholder");

        jLabel37.setText("Points:");

        lblBFPoints.setText("0");

        javax.swing.GroupLayout pnlBFStatsLayout = new javax.swing.GroupLayout(pnlBFStats);
        pnlBFStats.setLayout(pnlBFStatsLayout);
        pnlBFStatsLayout.setHorizontalGroup(
            pnlBFStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBFStatsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel66)
                .addGap(26, 26, 26)
                .addComponent(jLabel67)
                .addGap(19, 19, 19)
                .addComponent(jLabel68)
                .addGap(17, 17, 17)
                .addComponent(jLabel69)
                .addGap(20, 20, 20)
                .addComponent(jLabel70)
                .addGap(19, 19, 19)
                .addComponent(jLabel71)
                .addGap(22, 22, 22)
                .addComponent(jLabel72)
                .addGap(36, 36, 36)
                .addComponent(jLabel73)
                .addGap(27, 27, 27)
                .addComponent(lblBFArmor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel37)
                .addGap(17, 17, 17)
                .addComponent(lblBFPoints))
            .addGroup(pnlBFStatsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblBFMV, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblBFShort, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblBFMedium, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblBFLong, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblBFExtreme, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblBFWt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblBFOV, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel74)
                .addGap(11, 11, 11)
                .addComponent(lblBFStructure, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(pnlBFStatsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel75))
            .addGroup(pnlBFStatsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblBFSA, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlBFStatsLayout.setVerticalGroup(
            pnlBFStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBFStatsLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pnlBFStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel66)
                    .addComponent(jLabel67)
                    .addComponent(jLabel68)
                    .addComponent(jLabel69)
                    .addComponent(jLabel70)
                    .addComponent(jLabel71)
                    .addComponent(jLabel72)
                    .addComponent(jLabel73)
                    .addComponent(lblBFArmor)
                    .addComponent(jLabel37)
                    .addComponent(lblBFPoints))
                .addGap(6, 6, 6)
                .addGroup(pnlBFStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBFMV)
                    .addComponent(lblBFShort)
                    .addComponent(lblBFMedium)
                    .addComponent(lblBFLong)
                    .addComponent(lblBFExtreme)
                    .addComponent(lblBFWt)
                    .addComponent(lblBFOV)
                    .addGroup(pnlBFStatsLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel74))
                    .addGroup(pnlBFStatsLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblBFStructure)))
                .addGap(36, 36, 36)
                .addComponent(jLabel75)
                .addGap(6, 6, 6)
                .addComponent(lblBFSA, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlBFStats, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(pnlBFStats, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(148, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("BattleForce", jPanel10);

        jScrollPane5.setViewportView(txtBVBreakdown);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Battle Value Breakdown", jPanel11);

        jLabel1.setText("File:");

        lblFilename.setText("k:\\location");

        javax.swing.GroupLayout pnlFileLayout = new javax.swing.GroupLayout(pnlFile);
        pnlFile.setLayout(pnlFileLayout);
        pnlFileLayout.setHorizontalGroup(
            pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFileLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFilename)
                .addContainerGap(513, Short.MAX_VALUE))
        );
        pnlFileLayout.setVerticalGroup(
            pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(lblFilename))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(pnlFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbGunneryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGunneryActionPerformed
        unit.setGunnery(cmbGunnery.getSelectedIndex());
        unit.Refresh();
        setBV();
    }//GEN-LAST:event_cmbGunneryActionPerformed

    private void cmbPilotingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPilotingActionPerformed
        unit.setPiloting(cmbPiloting.getSelectedIndex());
        unit.Refresh();
        setBV();
    }//GEN-LAST:event_cmbPilotingActionPerformed

    private void txtModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtModActionPerformed
        unit.MiscMod = Float.parseFloat(txtMod.getText());
        unit.warrior.setManeiDomini(unit.MiscMod);
        unit.Refresh();
        setBV();
    }//GEN-LAST:event_txtModActionPerformed

    private void cmbConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbConfigurationActionPerformed
        if ( Default ) { return; }
        unit.SetCurLoadout(cmbConfiguration.getSelectedItem().toString().substring(0, cmbConfiguration.getSelectedItem().toString().indexOf(" ")));
        unit.UpdateByUnit();
        setC3();
        setBV();
        setTRO();
    }//GEN-LAST:event_cmbConfigurationActionPerformed

    private void spnSkillSeperationLimitStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnSkillSeperationLimitStateChanged
        btnFilterActionPerformed(null);
    }//GEN-LAST:event_spnSkillSeperationLimitStateChanged

    private void txtBVLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBVLimitActionPerformed
        btnFilterActionPerformed(evt);
    }//GEN-LAST:event_txtBVLimitActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        skills.setMaxSeperation(Integer.parseInt(spnSkillSeperationLimit.getValue().toString()));
        if (!txtBVLimit.getText().isEmpty()) { skills.setMaxBV(Float.parseFloat(txtBVLimit.getText())); }
        skills.setMaxSkill("");
        if ( rdoGunnery.isSelected() ) { skills.setMaxSkill("Gunnery"); }
        if ( rdoPiloting.isSelected() ) { skills.setMaxSkill("Piloting"); }
        setSkills();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void lstSkillsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstSkillsMouseClicked
        if ( evt.getClickCount() == 2 ) {
            String[] selection = lstSkills.getSelectedValue().toString().substring(0, 5).trim().split("/");
            cmbGunnery.setSelectedIndex(Integer.parseInt(selection[0].trim()));
            cmbPiloting.setSelectedIndex(Integer.parseInt(selection[1].trim()));
        }
    }//GEN-LAST:event_lstSkillsMouseClicked

    private void rdoGunneryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoGunneryActionPerformed
        btnFilterActionPerformed(evt);
    }//GEN-LAST:event_rdoGunneryActionPerformed

    private void rdoPilotingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoPilotingActionPerformed
        btnFilterActionPerformed(evt);
    }//GEN-LAST:event_rdoPilotingActionPerformed

    private void rdoNeitherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoNeitherActionPerformed
        btnFilterActionPerformed(evt);
}//GEN-LAST:event_rdoNeitherActionPerformed

    private void btnRandomGenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRandomGenActionPerformed
        lblRandomSkill.setText("9/9");
        lblRandomSkill.setText(skills.generateRandomSkill(cmbSkillLevel.getSelectedItem().toString()));
    }//GEN-LAST:event_btnRandomGenActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        String[] items = lblRandomSkill.getText().split("/");
        cmbGunnery.setSelectedIndex(Integer.parseInt(items[0]));
        cmbPiloting.setSelectedIndex(Integer.parseInt(items[1]));
    }//GEN-LAST:event_btnApplyActionPerformed

    private void btnMechImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMechImageActionPerformed
        Media media = new Media();
        File imageFile = media.SelectImage(Prefs.get("LastMechImage", ""), "Select Unit Image");
        if ( imageFile != null ) {
            try {
                Prefs.put("LastMechImage", imageFile.getCanonicalPath());
                unit.SetUnitImage(imageFile.getCanonicalPath());
                setImage();
                unit.SaveUnit();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnMechImageActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        unit.setMechwarrior(txtMechwarrior.getText());
        unit.setGunnery(cmbGunnery.getSelectedIndex());
        unit.setPiloting(cmbPiloting.getSelectedIndex());
        unit.MiscMod = Float.parseFloat(txtMod.getText());
        unit.UsingC3 = chkC3Active.isSelected();
        unit.setMechwarriorQuirks(tpnMechwarriorQuirks.getText());
        unit.UnitQuirks = tpnBattleMechQuirks.getText();
        unit.Refresh();
        force.isDirty = true;
        Result = true;
        this.setVisible( false );
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
         this.setVisible( false );
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        imageTracker.preLoadMechImages();
        PagePrinter printer = new PagePrinter();
        unit.LoadUnit();
        PrintMech pm = new PrintMech(unit.m, unit.getMechwarrior(), unit.getGunnery(), unit.getPiloting(), imageTracker);
        pm.setCanon(sswPrefs.getBoolean(Constants.Format_CanonPattern, false));
        pm.setCharts(sswPrefs.getBoolean(Constants.Format_Tables, false));
        printer.Append( BFBPrinter.Letter.toPage(), pm);
        printer.Print();
}//GEN-LAST:event_btnPrintActionPerformed

    private void txtBVLimitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBVLimitKeyReleased
        btnFilterActionPerformed(null);
    }//GEN-LAST:event_txtBVLimitKeyReleased

    private void btnSelectWarriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectWarriorActionPerformed
        if ( lstPersonnel.getSelectedIndex() > -1 ) {
            Warrior w = (Warrior) lstPersonnel.getSelectedValue();
            unit.setGunnery(w.getGunnery());
            unit.setPiloting(w.getPiloting());
            unit.setMechwarrior(w.getName());
            unit.MiscMod = Float.parseFloat(w.getManeiDomini()+"");

            txtMechwarrior.setText((w.getRank() + " " + w.getName()).trim());
            cmbGunnery.setSelectedIndex(w.getGunnery());
            cmbPiloting.setSelectedIndex(w.getPiloting());
            txtMod.setText(w.getManeiDomini()+"");

            unit.Refresh();
        }
    }//GEN-LAST:event_btnSelectWarriorActionPerformed

    private void lstPersonnelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstPersonnelMouseClicked
        if ( evt.getClickCount() == 2 ) {
            btnSelectWarriorActionPerformed(null);
        }
    }//GEN-LAST:event_lstPersonnelMouseClicked

    private void btnLoadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadFileActionPerformed
        Media media = new Media();
        File pFile = media.SelectFile(Prefs.get("LastPSNFile", "data/"), "psn", "Load Personnel File");

        if ( pFile != null ) {
            try {
                warriors.Load(pFile.getCanonicalPath());
                lstPersonnel.setModel(warriors.getModel());
                Prefs.put("LastPSNFile", pFile.getCanonicalPath());
            } catch ( IOException ex ) {
                Media.Messager("Could not load file\n" + ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnLoadFileActionPerformed

    private void btnSelectMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectMechActionPerformed
        Media media = new Media();
        File mech = media.SelectFile(sswPrefs.get("ListPath", ""), "ssw,saw", "Select Unit File");

        if ( mech != null ) {
            try {
                Media.Messager("Selected " + mech.getCanonicalPath());
                unit.Filename = mech.getCanonicalPath();
                unit.LoadUnit();
                if ( unit.m != null ) {
                    unit.BaseBV = unit.m.GetCurrentBV();
                    unit.Refresh();
                    setupFrame();
                }
                Prefs.put("ListPath", mech.getCanonicalPath());
            } catch (IOException ex) {
                Media.Messager("Error loading file\n" + ex.getMessage());
            }
        }
    }//GEN-LAST:event_btnSelectMechActionPerformed

    private void chkC3ActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkC3ActiveActionPerformed
        unit.UsingC3 = chkC3Active.isSelected();
        unit.Refresh();
        setBV();
        force.RefreshBV();
    }//GEN-LAST:event_chkC3ActiveActionPerformed

    private void btnClearImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearImageActionPerformed
        unit.SetUnitImage("../BFB.Images/No_Image.png");
        setImage();
        unit.SaveUnit();
    }//GEN-LAST:event_btnClearImageActionPerformed

    private void btnRandomNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRandomNameActionPerformed
        txtMechwarrior.setText(gen.SimpleGenerate());
    }//GEN-LAST:event_btnRandomNameActionPerformed

    private void btnMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMDActionPerformed
        dlgManeiDomini dMD = new dlgManeiDomini(this, true);
        dMD.setVisible(true);

        unit.MiscMod = (float)dMD.Modifier;
        txtMod.setText(dMD.Modifier+"");
        unit.Refresh();
        setBV();
    }//GEN-LAST:event_btnMDActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnClearImage;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnFilter;
    private javax.swing.ButtonGroup btnGrpSkill;
    private javax.swing.JButton btnLoadFile;
    private javax.swing.JButton btnMD;
    private javax.swing.JButton btnMechImage;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRandomGen;
    private javax.swing.JButton btnRandomName;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSelectMech;
    private javax.swing.JButton btnSelectWarrior;
    private javax.swing.JCheckBox chkC3Active;
    private javax.swing.JComboBox cmbConfiguration;
    private javax.swing.JComboBox cmbGunnery;
    private javax.swing.JComboBox cmbPiloting;
    private javax.swing.JComboBox cmbSkillLevel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblBFArmor;
    private javax.swing.JLabel lblBFExtreme;
    private javax.swing.JLabel lblBFLong;
    private javax.swing.JLabel lblBFMV;
    private javax.swing.JLabel lblBFMedium;
    private javax.swing.JLabel lblBFOV;
    private javax.swing.JLabel lblBFPoints;
    private javax.swing.JLabel lblBFSA;
    private javax.swing.JLabel lblBFShort;
    private javax.swing.JLabel lblBFStructure;
    private javax.swing.JLabel lblBFWt;
    private javax.swing.JLabel lblBaseBV;
    private javax.swing.JLabel lblConfig;
    private javax.swing.JLabel lblFilename;
    private javax.swing.JLabel lblMechImage;
    private javax.swing.JLabel lblModel;
    private javax.swing.JLabel lblRandomSkill;
    private javax.swing.JLabel lblSSWImage;
    private javax.swing.JLabel lblTonnage;
    private javax.swing.JLabel lblTotalBV;
    private javax.swing.JList lstPersonnel;
    private javax.swing.JList lstSkills;
    private javax.swing.JPanel pnlBFStats;
    private javax.swing.JPanel pnlConfiguration;
    private javax.swing.JPanel pnlFile;
    private javax.swing.JRadioButton rdoGunnery;
    private javax.swing.JRadioButton rdoNeither;
    private javax.swing.JRadioButton rdoPiloting;
    private javax.swing.JSpinner spnSkillSeperationLimit;
    private javax.swing.JScrollPane spnTRO;
    private javax.swing.JTextPane tpnBattleMechQuirks;
    private javax.swing.JTextPane tpnMechwarriorQuirks;
    private javax.swing.JTextPane tpnTRO;
    private javax.swing.JTextPane txtBVBreakdown;
    private javax.swing.JTextField txtBVLimit;
    private javax.swing.JTextField txtMechwarrior;
    private javax.swing.JTextField txtMod;
    // End of variables declaration//GEN-END:variables

}

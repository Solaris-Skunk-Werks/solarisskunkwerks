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

import Print.*;
import Force.*;
import Print.preview.dlgPreview;
import common.*;
import battleforce.BattleForce;
import filehandlers.ImageTracker;

import filehandlers.MechWriter;
import filehandlers.Media;
import java.awt.Cursor;
import java.awt.print.PageFormat;
import java.util.Vector;
import java.util.prefs.Preferences;

public class dlgPrint extends javax.swing.JDialog {
    /**
     *
     */
    private static final long serialVersionUID = -4270519652104777795L;
    private ImageTracker imageTracker;
    private Scenario scenario;
    private Preferences bfbPrefs = Preferences.userRoot().node( Constants.BFBPrefs );
    private Preferences sswPrefs = Preferences.userRoot().node( Constants.SSWPrefs );

    /** Creates new form dlgPrint */
    public dlgPrint(java.awt.Frame parent, boolean modal, Scenario scenario, ImageTracker imageTracker) {
        super(parent, modal);
        initComponents();

        this.scenario = scenario;
        this.imageTracker = imageTracker;

        chkCanon.setSelected(sswPrefs.getBoolean(Constants.Format_CanonPattern, false));
        chkTables.setSelected(sswPrefs.getBoolean(Constants.Format_Tables, false));
        chkUseHexConversion.setSelected(sswPrefs.getBoolean(Constants.Format_ConvertTerrain, false));
        cmbHexConvFactor.setSelectedItem(sswPrefs.getInt(Constants.Format_TerrainModifier, 1));

        chkPrintForce.setSelected(bfbPrefs.getBoolean(Constants.Print_ForceList, true));
        chkPrintFireChits.setSelected(bfbPrefs.getBoolean(Constants.Print_FireDeclaration, false));
        chkPrintScenario.setSelected(bfbPrefs.getBoolean(Constants.Print_Scenario, false));
        chkPrintRecordsheets.setSelected(bfbPrefs.getBoolean(Constants.Print_Recordsheet, true));
        chkPrintBattleforce.setSelected(bfbPrefs.getBoolean(Constants.Print_BattleForce, false));
        chkBFOnePerPage.setSelected(bfbPrefs.getBoolean(Constants.Format_OneForcePerPage, false));
        chkBFBacks.setSelected(bfbPrefs.getBoolean("BF_Backs", false));
        chkUseColor.setSelected(bfbPrefs.getBoolean("BF_Color", false));

        cmbBFSheetType.setSelectedIndex(bfbPrefs.getInt(Constants.Format_BattleForceSheetChoice, 0));
        cmbRSType.setSelectedIndex(bfbPrefs.getInt(Constants.Format_RecordsheetChoice, 0));

        Verify();
    }

    private void Verify() {
        setStatus("Verifying...");

        cmbRSType.setEnabled(chkPrintRecordsheets.isSelected());
        chkTables.setEnabled(chkPrintRecordsheets.isSelected());
        chkCanon.setEnabled(chkPrintRecordsheets.isSelected());
        chkImage.setEnabled(chkPrintRecordsheets.isSelected() || chkPrintBattleforce.isSelected());
        chkLogo.setEnabled(chkPrintRecordsheets.isSelected() || chkPrintBattleforce.isSelected() || chkPrintForce.isSelected());

        cmbBFSheetType.setEnabled(chkPrintBattleforce.isSelected());
        chkBFOnePerPage.setEnabled(chkPrintBattleforce.isSelected());

        chkUseHexConversion.setEnabled(chkPrintRecordsheets.isSelected());
        cmbHexConvFactor.setEnabled(chkPrintRecordsheets.isSelected());

        if (cmbRSType.getSelectedIndex() == 1) {
            chkTables.setSelected(false);
            chkTables.setEnabled(false);
            chkCanon.setSelected(true);
            chkCanon.setEnabled(false);

            chkUseHexConversion.setSelected(false);
            chkUseHexConversion.setEnabled(false);
            cmbHexConvFactor.setSelectedItem(1);
            cmbHexConvFactor.setEnabled(false);
        }

        switch ( cmbBFSheetType.getSelectedIndex() ) {
            case 0:
                chkBFOnePerPage.setEnabled(true);
                chkBFBacks.setEnabled(false);
                chkUseColor.setEnabled(false);
                break;
            case 1:
                chkBFOnePerPage.setEnabled(false);
                chkBFBacks.setEnabled(false);
                chkUseColor.setEnabled(false);
                break;
            case 2:
                chkBFOnePerPage.setSelected(true);
                chkBFOnePerPage.setEnabled(false);
                chkBFBacks.setEnabled(false);
                chkUseColor.setEnabled(false);
                break;
            default:
                chkBFOnePerPage.setEnabled(false);
                chkBFBacks.setEnabled(true);
                chkUseColor.setEnabled(true);
        }

        String iconPath = "/images/Recordsheet_BG.png";
        if (chkPrintRecordsheets.isSelected()) {
            if (chkTables.isSelected()) { iconPath = "/images/RecordsheetTables_BG.png"; }
            lblRecordsheetIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource(iconPath)));
        }

        if ( chkPrintBattleforce.isSelected() ) {
            iconPath = "/images/BattleForce_BG.png";
            switch ( cmbBFSheetType.getSelectedIndex() ) {
                case 3:
                    iconPath = "/images/BFVertColor_BG.png";
                    break;
                case 2:
                    iconPath = "/images/BFCard_BG.png";
                    break;
                case 1:
                    iconPath = "/images/QSCard_BG.png";
                    break;
            }
            lblBattleForceIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource(iconPath)));
        }
    }

    private PagePrinter SetupPrinter() {
        PagePrinter printer = new PagePrinter();

        printer.setJobName(scenario.getName());

        if (chkPrintForce.isSelected()) {
            ForceListPrinter sheet = new ForceListPrinter(imageTracker);
            sheet.setPrintLogo(chkLogo.isSelected());
            sheet.setTitle(scenario.getName());
            sheet.AddForces(scenario.getForces());
            printer.Append(BFBPrinter.Letter.toPage(), sheet);
        }

        if (chkPrintScenario.isSelected()) {
            ScenarioPrinter scenarioPrint = new ScenarioPrinter(scenario, imageTracker);
            printer.Append(BFBPrinter.Letter.toPage(), scenarioPrint);
        }

        if (chkPrintFireChits.isSelected()) {
            PrintDeclaration fire = new PrintDeclaration(imageTracker);
            fire.AddForces(scenario.getForces());
            printer.Append(BFBPrinter.Letter.toPage(), fire);
        }

        if (chkPrintBattleforce.isSelected()) {
            imageTracker.preLoadBattleForceImages();
            switch ( cmbBFSheetType.getSelectedIndex() ) {
                case 0:
                    if ( chkBFOnePerPage.isSelected() ) {
                        Vector<BattleForce> forcelist = new Vector<BattleForce>();
                        forcelist.addAll(scenario.getAttackerForce().toBattleForceByGroup( 12 ));
                        if ( scenario.getDefenderForce().getUnits().size() > 0 ) { forcelist.addAll(scenario.getDefenderForce().toBattleForceByGroup( 12 )); }

                        for ( BattleForce f : forcelist ) {
                            BattleforcePrinter bf = new BattleforcePrinter(f, imageTracker);
                            bf.setPrintLogo(chkLogo.isSelected());
                            bf.setPrintMechs(chkImage.isSelected());
                            if ( chkBFTerrainMV.isSelected() ) bf.setTerrain(true);
                            printer.Append( BFBPrinter.Letter.toPage(), bf);
                        }
                    } else {
                        BattleforcePrinter topBF = new BattleforcePrinter(scenario.getAttackerForce().toBattleForce(), imageTracker);
                        topBF.setPrintLogo(chkLogo.isSelected());
                        topBF.setPrintMechs(chkImage.isSelected());
                        if ( chkBFTerrainMV.isSelected() ) topBF.setTerrain(true);
                        printer.Append( BFBPrinter.Letter.toPage(), topBF );

                        if ( scenario.getDefenderForce().getUnits().size() > 0 ) {
                            BattleforcePrinter bottomBF = new BattleforcePrinter(scenario.getDefenderForce().toBattleForce(), imageTracker);
                            bottomBF.setPrintLogo(chkLogo.isSelected());
                            bottomBF.setPrintMechs(chkImage.isSelected());
                            if ( chkBFTerrainMV.isSelected() ) bottomBF.setTerrain(true);
                            printer.Append( BFBPrinter.Letter.toPage(), bottomBF );
                        }
                    }
                    break;

                case 1:
                   for ( BattleForce f : scenario.toBattleForceBySize(8) ) {
                        QuickStrikeCardPrinter qs = new QuickStrikeCardPrinter(f, imageTracker);
                        qs.setPrintLogo(chkLogo.isSelected());
                        qs.setPrintMechs(chkImage.isSelected());
                        if ( chkBFTerrainMV.isSelected() ) qs.setTerrain(true);
                        printer.Append( BFBPrinter.FullLetter.toPage(), qs);
                    }
                    break;

                case 2:
                    Vector<BattleForce> forces = new Vector<BattleForce>();
                    forces.addAll(scenario.getAttackerForce().toBattleForceByGroup( 6 ));
                    if ( scenario.getDefenderForce().getUnits().size() > 0 ) { forces.addAll(scenario.getDefenderForce().toBattleForceByGroup( 6 )); }

                    for ( BattleForce f : forces ) {
                        BattleforceCardPrinter bf = new BattleforceCardPrinter(f, imageTracker);
                        bf.setPrintLogo(chkLogo.isSelected());
                        bf.setPrintMechs(chkImage.isSelected());
                        if ( chkBFTerrainMV.isSelected() ) bf.setTerrain(true);
                        printer.Append( BFBPrinter.Letter.toPage(), bf);
                    }
                    break;

                case 3:
                    int MaxUnits = 8;
                    if ( chkBFBacks.isSelected() ) MaxUnits = 4;
                    for ( BattleForce f : scenario.toBattleForceBySize(MaxUnits) ) {
                        QSVerticalCardPrinter qs = new QSVerticalCardPrinter(f, imageTracker);
                        if ( !chkUseColor.isSelected() ) qs.setBlackAndWhite();
                        qs.setCardBack(chkBFBacks.isSelected());
                        qs.setPrintLogo(chkLogo.isSelected());
                        qs.setPrintMechs(chkImage.isSelected());
                        if ( chkBFTerrainMV.isSelected() ) qs.setTerrain(true);
                        PageFormat letter = BFBPrinter.FullLetter.toPage();
                        letter.setOrientation(PageFormat.LANDSCAPE);
                        printer.Append( letter, qs);
                    }
                    break;

                default:
            }
        }

        if ( chkPrintRecordsheets.isSelected() ) {
            imageTracker.preLoadMechImages();
            for ( Force force : scenario.getForces() ) {
                for ( Group g : force.Groups ) {
                    for ( Unit u : g.getUnits() ) {
                        u.LoadUnit();
                        PrintMech pm = new PrintMech(u.m,u.getMechwarrior(), u.getGunnery(), u.getPiloting(), imageTracker);
                        pm.setCanon(chkCanon.isSelected());
                        pm.setCharts(chkTables.isSelected());
                        pm.setPrintMech(chkImage.isSelected());
                        pm.setPrintLogo(chkLogo.isSelected());
                        if ( chkUseHexConversion.isSelected() ) {
                            pm.SetMiniConversion(cmbHexConvFactor.getSelectedIndex());
                        }
                        if ( chkLogo.isSelected() ) {
                            pm.setLogoImage(imageTracker.getImage(g.getLogo()));
                        }
                        if ( cmbRSType.getSelectedIndex() == 1 ) {
                            pm.setTRO(true);
                        }
                        printer.Append( BFBPrinter.Letter.toPage(), pm);
                    }
                }
            }
        }

        return printer;
    }

    private void setPreferences() {
        sswPrefs.putBoolean(Constants.Format_CanonPattern, chkCanon.isSelected());
        sswPrefs.putBoolean(Constants.Format_Tables, chkTables.isSelected());
        sswPrefs.putBoolean(Constants.Format_ConvertTerrain, chkUseHexConversion.isSelected());
        sswPrefs.putInt(Constants.Format_TerrainModifier, cmbHexConvFactor.getSelectedIndex());

        bfbPrefs.putBoolean(Constants.Print_ForceList, chkPrintForce.isSelected());
        bfbPrefs.putBoolean(Constants.Print_FireDeclaration, chkPrintFireChits.isSelected());
        bfbPrefs.putBoolean(Constants.Print_Scenario, chkPrintScenario.isSelected());
        bfbPrefs.putBoolean(Constants.Print_Recordsheet, chkPrintRecordsheets.isSelected());
        bfbPrefs.putBoolean(Constants.Print_BattleForce, chkPrintBattleforce.isSelected());
        bfbPrefs.putBoolean(Constants.Format_OneForcePerPage, chkBFOnePerPage.isSelected());
        bfbPrefs.putBoolean("BF_Backs", chkBFBacks.isSelected());
        bfbPrefs.putBoolean("BF_Color", chkUseColor.isSelected());

        bfbPrefs.putInt(Constants.Format_BattleForceSheetChoice, cmbBFSheetType.getSelectedIndex());
        bfbPrefs.putInt(Constants.Format_RecordsheetChoice, cmbRSType.getSelectedIndex());
    }

    private void setStatus(String message) {
        lblStatus.setText(message);
        lblStatus.firePropertyChange("Text", 0, 1);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlWhat = new javax.swing.JPanel();
        chkPrintForce = new javax.swing.JCheckBox();
        chkPrintFireChits = new javax.swing.JCheckBox();
        chkPrintRecordsheets = new javax.swing.JCheckBox();
        chkPrintBattleforce = new javax.swing.JCheckBox();
        lblForceIcon = new javax.swing.JLabel();
        lblFireDecIcon = new javax.swing.JLabel();
        lblRecordsheetIcon = new javax.swing.JLabel();
        lblBattleForceIcon = new javax.swing.JLabel();
        chkPrintScenario = new javax.swing.JCheckBox();
        lblRecordsheetIcon1 = new javax.swing.JLabel();
        pnlHow = new javax.swing.JPanel();
        pnlGeneral = new javax.swing.JPanel();
        chkLogo = new javax.swing.JCheckBox();
        chkImage = new javax.swing.JCheckBox();
        btnImageMgr = new javax.swing.JButton();
        btnClearImages = new javax.swing.JButton();
        pnlBattleForce = new javax.swing.JPanel();
        chkBFOnePerPage = new javax.swing.JCheckBox();
        cmbBFSheetType = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        chkBFTerrainMV = new javax.swing.JCheckBox();
        chkUseColor = new javax.swing.JCheckBox();
        chkBFBacks = new javax.swing.JCheckBox();
        pnlRecordsheet = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cmbHexConvFactor = new javax.swing.JComboBox();
        lblInches = new javax.swing.JLabel();
        lblOneHex = new javax.swing.JLabel();
        chkUseHexConversion = new javax.swing.JCheckBox();
        cmbRSType = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        chkCanon = new javax.swing.JCheckBox();
        chkTables = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnPreview = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Printing Options");
        setModal(true);
        setResizable(false);

        pnlWhat.setBorder(javax.swing.BorderFactory.createTitledBorder("What To Print"));

        chkPrintForce.setSelected(true);
        chkPrintForce.setText("Force List");
        chkPrintForce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemChanged(evt);
            }
        });

        chkPrintFireChits.setText("Fire Declaration");
        chkPrintFireChits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemChanged(evt);
            }
        });

        chkPrintRecordsheets.setSelected(true);
        chkPrintRecordsheets.setText("Unit Recordsheets");
        chkPrintRecordsheets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Verify(evt);
            }
        });

        chkPrintBattleforce.setText("BattleForce Sheets");
        chkPrintBattleforce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Verify(evt);
            }
        });

        lblForceIcon.setBackground(new java.awt.Color(255, 255, 255));
        lblForceIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ForceList_BG.png"))); // NOI18N
        lblForceIcon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblFireDecIcon.setBackground(new java.awt.Color(255, 255, 255));
        lblFireDecIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/FireDec_BG.png"))); // NOI18N
        lblFireDecIcon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblRecordsheetIcon.setBackground(new java.awt.Color(255, 255, 255));
        lblRecordsheetIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Recordsheet_BG.png"))); // NOI18N
        lblRecordsheetIcon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblBattleForceIcon.setBackground(new java.awt.Color(255, 255, 255));
        lblBattleForceIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/BattleForce_BG.png"))); // NOI18N
        lblBattleForceIcon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        chkPrintScenario.setText("Scenario Sheet");
        chkPrintScenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemChanged(evt);
            }
        });

        lblRecordsheetIcon1.setBackground(new java.awt.Color(255, 255, 255));
        lblRecordsheetIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ForceList_BG.png"))); // NOI18N
        lblRecordsheetIcon1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout pnlWhatLayout = new javax.swing.GroupLayout(pnlWhat);
        pnlWhat.setLayout(pnlWhatLayout);
        pnlWhatLayout.setHorizontalGroup(
            pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWhatLayout.createSequentialGroup()
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPrintForce)
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lblForceIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPrintScenario)
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lblRecordsheetIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPrintFireChits)
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lblFireDecIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPrintRecordsheets)
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lblRecordsheetIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPrintBattleforce)
                    .addGroup(pnlWhatLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lblBattleForceIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        pnlWhatLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkPrintBattleforce, chkPrintFireChits, chkPrintForce, chkPrintRecordsheets, chkPrintScenario});

        pnlWhatLayout.setVerticalGroup(
            pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWhatLayout.createSequentialGroup()
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPrintForce)
                    .addComponent(chkPrintScenario)
                    .addComponent(chkPrintFireChits)
                    .addComponent(chkPrintRecordsheets)
                    .addComponent(chkPrintBattleforce))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlWhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblForceIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRecordsheetIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFireDecIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(lblRecordsheetIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblBattleForceIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlHow.setBorder(javax.swing.BorderFactory.createTitledBorder("How To Print"));

        pnlGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder("General Options"));

        chkLogo.setSelected(true);
        chkLogo.setText("Print Unit Logo");
        chkLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemChanged(evt);
            }
        });

        chkImage.setSelected(true);
        chkImage.setText("Print Mech BFB.Images ");
        chkImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemChanged(evt);
            }
        });

        btnImageMgr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/images-stack.png"))); // NOI18N
        btnImageMgr.setText("Manage BFB.Images");
        btnImageMgr.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnImageMgr.setOpaque(false);
        btnImageMgr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageMgrActionPerformed(evt);
            }
        });

        btnClearImages.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/eraser.png"))); // NOI18N
        btnClearImages.setText("Clear BFB.Images");
        btnClearImages.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClearImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearImagesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkImage)
                    .addComponent(chkLogo)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnClearImages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnImageMgr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addComponent(chkImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImageMgr)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearImages)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkLogo)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        pnlBattleForce.setBorder(javax.swing.BorderFactory.createTitledBorder("BattleForce Options"));

        chkBFOnePerPage.setText("Print One Unit Per Page");
        chkBFOnePerPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemChanged(evt);
            }
        });

        cmbBFSheetType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "*Strategic Ops", "*QuickStrike Cards", "BattleForce Sheet", "Vertical Cards" }));
        cmbBFSheetType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemChanged(evt);
            }
        });

        jLabel3.setText("Sheet Type:");

        chkBFTerrainMV.setText("Print Miniatures Scale");
        chkBFTerrainMV.setToolTipText("Converts MV to use 2\" hexes");
        chkBFTerrainMV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBFTerrainMVitemChanged(evt);
            }
        });

        chkUseColor.setText("Print Full Color");
        chkUseColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseColorActionPerformed(evt);
            }
        });

        chkBFBacks.setText("Print Card Backs");
        chkBFBacks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBFBacksActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBattleForceLayout = new javax.swing.GroupLayout(pnlBattleForce);
        pnlBattleForce.setLayout(pnlBattleForceLayout);
        pnlBattleForceLayout.setHorizontalGroup(
            pnlBattleForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBattleForceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBattleForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkUseColor)
                    .addComponent(chkBFBacks)
                    .addComponent(chkBFTerrainMV)
                    .addComponent(chkBFOnePerPage)
                    .addGroup(pnlBattleForceLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbBFSheetType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        pnlBattleForceLayout.setVerticalGroup(
            pnlBattleForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBattleForceLayout.createSequentialGroup()
                .addGroup(pnlBattleForceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbBFSheetType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkBFOnePerPage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBFTerrainMV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUseColor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBFBacks))
        );

        pnlRecordsheet.setBorder(javax.swing.BorderFactory.createTitledBorder("Recordsheet Options"));

        cmbHexConvFactor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
        cmbHexConvFactor.setEnabled(false);

        lblInches.setText("Inches");
        lblInches.setEnabled(false);

        lblOneHex.setText("One Hex equals");
        lblOneHex.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblOneHex)
                .addGap(1, 1, 1)
                .addComponent(cmbHexConvFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInches)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cmbHexConvFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblOneHex))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(lblInches))
        );

        chkUseHexConversion.setText("Print Miniatures Scale");
        chkUseHexConversion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseHexConversionActionPerformed(evt);
            }
        });

        cmbRSType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Total Warfare", "Recordsheet", "Tactical Operations" }));
        cmbRSType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemChanged(evt);
            }
        });

        jLabel4.setText("Sheet Type:");

        chkCanon.setSelected(true);
        chkCanon.setText("Print Canon Dot Patterns");
        chkCanon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemChanged(evt);
            }
        });

        chkTables.setSelected(true);
        chkTables.setText("Print Charts and Tables");
        chkTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Verify(evt);
            }
        });

        javax.swing.GroupLayout pnlRecordsheetLayout = new javax.swing.GroupLayout(pnlRecordsheet);
        pnlRecordsheet.setLayout(pnlRecordsheetLayout);
        pnlRecordsheetLayout.setHorizontalGroup(
            pnlRecordsheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecordsheetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRecordsheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkTables)
                    .addComponent(chkCanon)
                    .addComponent(chkUseHexConversion)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlRecordsheetLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbRSType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        pnlRecordsheetLayout.setVerticalGroup(
            pnlRecordsheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecordsheetLayout.createSequentialGroup()
                .addGroup(pnlRecordsheetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbRSType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkTables)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCanon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUseHexConversion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlHowLayout = new javax.swing.GroupLayout(pnlHow);
        pnlHow.setLayout(pnlHowLayout);
        pnlHowLayout.setHorizontalGroup(
            pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHowLayout.createSequentialGroup()
                .addComponent(pnlGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlRecordsheet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBattleForce, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlHowLayout.setVerticalGroup(
            pnlHowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlRecordsheet, 0, 146, Short.MAX_VALUE)
            .addComponent(pnlBattleForce, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnPrint)
                .addGap(1, 1, 1)
                .addComponent(btnCancel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnPrint)
                .addComponent(btnCancel))
        );

        btnPreview.setText("Preview");
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });

        lblStatus.setText("Memory Available: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlHow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPreview)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 435, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                    .addComponent(pnlWhat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlWhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlHow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPreview)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(lblStatus))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkUseHexConversionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseHexConversionActionPerformed
        lblOneHex.setEnabled(chkUseHexConversion.isSelected());
        cmbHexConvFactor.setEnabled(chkUseHexConversion.isSelected());
        lblInches.setEnabled(chkUseHexConversion.isSelected());

        Verify();
}//GEN-LAST:event_chkUseHexConversionActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setPreferences();

        setStatus("Loading Requested Sheets");
        PagePrinter printer = SetupPrinter();

        setStatus("Sending to Printer");
        printer.Print();

        this.setCursor(Cursor.getDefaultCursor());
        this.setVisible(false);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void Verify(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Verify
        Verify();
    }//GEN-LAST:event_Verify

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        setStatus("Loading Requested Sheets");
        PagePrinter printer = SetupPrinter();
        dlgPreview prv = new dlgPreview("Print Preview", this, printer.Preview(), imageTracker);
        prv.setLocationRelativeTo(this);
        prv.setVisible(true);
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void btnImageMgrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageMgrActionPerformed
        dlgImageMgr dlgImg = new dlgImageMgr(null, scenario.getForces(), imageTracker);
        if (dlgImg.hasWork) {
            dlgImg.setLocationRelativeTo(this);
            dlgImg.setVisible(true);
        }
    }//GEN-LAST:event_btnImageMgrActionPerformed

    private void itemChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemChanged
        Verify();
}//GEN-LAST:event_itemChanged

    private void chkBFTerrainMVitemChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBFTerrainMVitemChanged
        // TODO add your handling code here:
}//GEN-LAST:event_chkBFTerrainMVitemChanged

    private void chkUseColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseColorActionPerformed
        Verify();
}//GEN-LAST:event_chkUseColorActionPerformed

    private void chkBFBacksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBFBacksActionPerformed
        Verify();
}//GEN-LAST:event_chkBFBacksActionPerformed

    private void btnClearImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearImagesActionPerformed
        MechWriter writer = new MechWriter();

        if ( Media.Options(this, "Are you sure you want to remove all pre-selected images from the units in this list?", "Confirm Image Removal") == Media.OK) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            for ( Group g : scenario.getGroups() ) {
                for ( Unit u : g.getUnits() ) {
                    try {
                        u.LoadUnit();
                        u.m.SetSSWImage("../BFB.Images/No_Image.png");
                        writer.setMech(u.m);
                        writer.WriteXML(u.Filename);
                    } catch ( Exception e ) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            this.setCursor(Cursor.getDefaultCursor());
            Media.Messager("BFB.Images have been removed from all units in the list.");
        }
}//GEN-LAST:event_btnClearImagesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClearImages;
    private javax.swing.JButton btnImageMgr;
    private javax.swing.JButton btnPreview;
    private javax.swing.JButton btnPrint;
    private javax.swing.JCheckBox chkBFBacks;
    private javax.swing.JCheckBox chkBFOnePerPage;
    private javax.swing.JCheckBox chkBFTerrainMV;
    private javax.swing.JCheckBox chkCanon;
    private javax.swing.JCheckBox chkImage;
    private javax.swing.JCheckBox chkLogo;
    private javax.swing.JCheckBox chkPrintBattleforce;
    private javax.swing.JCheckBox chkPrintFireChits;
    private javax.swing.JCheckBox chkPrintForce;
    private javax.swing.JCheckBox chkPrintRecordsheets;
    private javax.swing.JCheckBox chkPrintScenario;
    private javax.swing.JCheckBox chkTables;
    private javax.swing.JCheckBox chkUseColor;
    private javax.swing.JCheckBox chkUseHexConversion;
    private javax.swing.JComboBox cmbBFSheetType;
    private javax.swing.JComboBox cmbHexConvFactor;
    private javax.swing.JComboBox cmbRSType;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblBattleForceIcon;
    private javax.swing.JLabel lblFireDecIcon;
    private javax.swing.JLabel lblForceIcon;
    private javax.swing.JLabel lblInches;
    private javax.swing.JLabel lblOneHex;
    private javax.swing.JLabel lblRecordsheetIcon;
    private javax.swing.JLabel lblRecordsheetIcon1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel pnlBattleForce;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JPanel pnlHow;
    private javax.swing.JPanel pnlRecordsheet;
    private javax.swing.JPanel pnlWhat;
    // End of variables declaration//GEN-END:variables
}

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

import IO.*;
import Force.*;
import Force.Skills.Skill;
import Force.View.*;
import Print.*;
import Print.preview.*;
import dialog.*;
import filehandlers.Media;
import filehandlers.ImageTracker;
import common.CommonTools;
import battleforce.BattleForce;

import common.Constants;
import filehandlers.FileCommon;
import filehandlers.MechWriter;
import filehandlers.TXTWriter;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.prefs.*;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import list.ListFilter;
import list.UnitList;
import list.UnitListData;
import list.view.*;

public class frmBase extends javax.swing.JFrame implements java.awt.datatransfer.ClipboardOwner {
    public Scenario scenario = new Scenario();
    public Preferences Prefs;
    private dlgOpen dOpen;
    private Media media = new Media();
    private ImageTracker images = new ImageTracker();
    private Force addToForce = new Force();
    
    private UnitList list = new UnitList(),  filtered,  chosen = new UnitList();
    private abView currentView = new tbTotalWarfareView(list);
    private String MechListPath = "", BaseRUSPath = "./Data/Tables/",  RUSDirectory = "",  RUSPath = BaseRUSPath, CurrentFile = "";
    private RUS rus = new RUS();

    private KeyListener KeyTyped = new KeyListener() {
        public void keyTyped(KeyEvent e) {
            scenario.setName(txtScenarioName.getText());
            scenario.setSetup(edtSetup.getText());
            scenario.setSituation(edtSituation.getText());
            scenario.setAttacker(edtAttacker.getText());
            scenario.setDefender(edtDefender.getText());
            scenario.setSpecialRules(edtSpecialRules.getText());
            scenario.setVictoryConditions(edtVictoryConditions.getText());
            scenario.setAftermath(edtAftermath.getText());
        }

        public void keyPressed(KeyEvent e) {
            //do nothing
        }

        public void keyReleased(KeyEvent e) {
            keyTyped(e);
        }
    };

    private TableModelListener ForceChanged = new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
            Refresh();
        }
    };

    JPopupMenu popUtilities = new JPopupMenu();
    JMenuItem popGroup = new JMenuItem( "Set Lance/Star" );
    JMenuItem popSkill = new JMenuItem( "Adjust Skills" );
    JMenuItem popName = new JMenuItem( "Generate Names" );

    public frmBase() {
        initComponents();
        Prefs = Preferences.userRoot().node( Constants.BFBPrefs );
        MechListPath = Prefs.get("ListPath", "");
        
        popUtilities.add(popGroup);
        popUtilities.add(popSkill);
        popUtilities.add(popName);

        //Clear tracking data
        CurrentFile = "";
        //Prefs.put("CurrentBFBFile", "");

        dOpen = new dlgOpen(this, true);
        dOpen.setMechListPath(Prefs.get("ListPath", ""));

        scenario.setModel(new tbTotalWarfare());
        scenario.AddListener(ForceChanged);
        scenario.updateOpFor(chkUseForceModifier.isSelected());
        addToForce = scenario.getAttackerForce();

        txtScenarioName.addKeyListener(KeyTyped);
        edtSituation.addKeyListener(KeyTyped);
        edtSetup.addKeyListener(KeyTyped);
        edtAttacker.addKeyListener(KeyTyped);
        edtDefender.addKeyListener(KeyTyped);
        edtSpecialRules.addKeyListener(KeyTyped);
        edtVictoryConditions.addKeyListener(KeyTyped);
        edtAftermath.addKeyListener(KeyTyped);
        
        Refresh();
        lblStatusUpdate.setText("");
        LoadRUSOptions();

        //if ( !Prefs.get("LastOpenFile", "").isEmpty() ) { loadScenario(Prefs.get("LastOpenFile", "")); }

    }

    public void Refresh() {
        scenario.setupTables(tblTop, tblBottom);

        setLogo( lblUnitLogoTop, new File(scenario.getAttackerForce().LogoPath) );
        setLogo( lblUnitLogoBottom, new File(scenario.getDefenderForce().LogoPath) );

        txtUnitNameTop.setText(scenario.getAttackerForce().ForceName);
        txtUnitNameBottom.setText(scenario.getDefenderForce().ForceName);

        if ( scenario.getAttackerForce().getType().equals(BattleForce.Comstar) ) {
            btnCSTop.setSelected(true);
        } else if ( scenario.getAttackerForce().getType().equals(BattleForce.Clan) ) {
            btnCLTop.setSelected(true);
        } else {
            btnISTop.setSelected(true);
        }
        
        if ( scenario.getDefenderForce().getType().equals(BattleForce.Comstar) ) {
            btnCSBottom.setSelected(true);
        } else if ( scenario.getDefenderForce().getType().equals(BattleForce.Clan) ) {
            btnCLBottom.setSelected(true);
        } else {
            btnISBottom.setSelected(true);
        }

        lblForceMod.setText( String.format( "%1$,.2f", CommonTools.GetForceSizeMultiplier( scenario.getAttackerForce().getUnits().size(), scenario.getDefenderForce().getUnits().size() )) );
        
        updateFields();
    }

    private void LoadRUSOptions() {
        treDirectories.setModel(new DefaultTreeModel(addNodes(null, new File(BaseRUSPath))));
        treDirectories.setRootVisible(false);
        treDirectories.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treDirectories.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treDirectories.getLastSelectedPathComponent();
                DirectoryLeaf f = (DirectoryLeaf) node.getUserObject();
                LoadRUSFiles(f.getPath());
            }
        });
    }

    /** Add nodes from under "dir" into curTop. Highly recursive. */
    DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
        String curPath = dir.getPath();
        String dirName = dir.getName();
        DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(new DirectoryLeaf(dir));
        if (curTop != null) { // should only be null at root
            curTop.add(curDir);
        }
        ArrayList ol = new ArrayList();
        String[] tmp = dir.list();
        ol.addAll(Arrays.asList(tmp));
        Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
        File f;
        ArrayList files = new ArrayList();
        // Make two passes, one for Dirs and one for Files. This is #1.
        for (int i = 0; i < ol.size(); i++) {
            String thisObject = (String) ol.get(i);
            String newPath;
            if (curPath.equals(".")) {
                newPath = thisObject;
            } else {
                newPath = curPath + File.separator + thisObject;
            }
            if ((f = new File(newPath)).isDirectory()) {
                addNodes(curDir, f);
            } else {
                //files.addElement(thisObject);
            }
        }
        // Pass two: for files.
        for (int fnum = 0; fnum < files.size(); fnum++) {
            curDir.add(new DefaultMutableTreeNode(files.get(fnum)));
        }
        return curDir;
    }

    class DirectoryLeaf {
        private String Name = "",
                Path = "";
        public DirectoryLeaf( File f ) {
            Name = f.getName();
            try {
                Path = f.getCanonicalPath();
            } catch (IOException ex) {
                Media.Messager(ex.getMessage());
            }
        }

        public String getPath() {
            return Path;
        }

        @Override
        public String toString() {
            return Name;
        }
    }

    private void LoadRUSFiles(String dirPath) {
        if (dirPath.isEmpty()) {
            return;
        }
        try {
            ArrayList<DirectoryLeaf> v = new ArrayList<DirectoryLeaf>();
            File file = new File(dirPath);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        if (files[i].getName().endsWith(".txt")) {
                            v.add(new DirectoryLeaf(files[i]));
                            //listModel.addElement(new DirectoryLeaf(files[i]));
                        }
                    }
                }
            }
            Collections.sort(v, new Comparator<DirectoryLeaf>() {
                public int compare(DirectoryLeaf o1, DirectoryLeaf o2) {
                    return (String.CASE_INSENSITIVE_ORDER).compare(o1.toString(), o2.toString());
                }
            });
            DefaultListModel listModel = new DefaultListModel();
            for ( DirectoryLeaf leaf : v ) {
                listModel.addElement(leaf);
            }
            lstFiles.setModel(listModel);
        } catch (NullPointerException npe) {
            Media.Messager("Could not load " + dirPath + ".\n" + npe.getMessage());
            System.out.println(npe.getMessage());
        }
    }

    private void updateFields() {
        lblUnitsTop.setText(scenario.getAttackerForce().getUnits().size()+"");
        lblTonnageTop.setText( String.format("%1$,.0f", scenario.getAttackerForce().TotalTonnage) );

        lblUnitsBottom.setText(scenario.getDefenderForce().getUnits().size()+"");
        lblTonnageBottom.setText( String.format("%1$,.0f", scenario.getDefenderForce().TotalTonnage) );

        if ( scenario.getAttackerForce().getCurrentModel() instanceof tbBattleForce ) {
            lblBaseBVTop.setText( String.format("%1$,.0f", scenario.getAttackerForce().TotalBasePV) );
            lblTotalBVTop.setText( String.format("%1$,.0f", scenario.getAttackerForce().TotalForcePV) );

            lblBaseBVBottom.setText( String.format("%1$,.0f", scenario.getDefenderForce().TotalBasePV) );
            lblTotalBVBottom.setText( String.format("%1$,.0f", scenario.getDefenderForce().TotalForcePV) );
        } else {
            lblBaseBVTop.setText( String.format("%1$,.0f", scenario.getAttackerForce().TotalBaseBV) );
            lblTotalBVTop.setText( String.format("%1$,.0f", scenario.getAttackerForce().TotalForceBVAdjusted) );

            lblBaseBVBottom.setText( String.format("%1$,.0f", scenario.getDefenderForce().TotalBaseBV) );
            lblTotalBVBottom.setText( String.format("%1$,.0f", scenario.getDefenderForce().TotalForceBVAdjusted) );
        }
    }

    private void loadScenario( String filename ) {
        if ( filename.isEmpty() ) { return; }
        
        BFBReader reader = new BFBReader();
        //Force[] forces;
        try {
            scenario = reader.ReadScenario(filename);

            scenario.setModel(new tbTotalWarfare());
            scenario.AddListener(ForceChanged);
            chkUseForceModifier.setSelected(scenario.UseForceSizeModifier());
            scenario.updateOpFor(scenario.UseForceSizeModifier());
            scenario.Refresh();

            lstObjectives.setModel(scenario.getWarchest().getObjectiveList());
            lstBonuses.setModel(scenario.getWarchest().getBonusList());

            //Load scenario info into fields
            txtTrackCost.setText(scenario.getWarchest().getTrackCost()+"");
            txtScenarioName.setText(scenario.getName());
            edtSituation.setText(scenario.getSituation());
            edtSetup.setText(scenario.getSetup());
            edtAttacker.setText(scenario.getAttacker());
            edtDefender.setText(scenario.getDefender());
            edtSpecialRules.setText(scenario.getSpecialRules());
            edtVictoryConditions.setText(scenario.getVictoryConditions());
            edtAftermath.setText(scenario.getAftermath());

            Refresh();

            Prefs.put("LastOpenFile", filename);
            CurrentFile = filename;
            //Prefs.put("CurrentBFBFile", filename);

        } catch ( IOException ie ) {
            Media.Messager(ie.getMessage());
            System.out.println(ie.getMessage());
            return;
        } catch ( Exception e ) {
            Media.Messager("Issue loading file:\n " + e.getMessage());
            System.out.println(e.getMessage());
            return;
        }
    }

    private void updateLogo( javax.swing.JLabel lblLogo, Force force ) {
        File Logo = media.SelectImage(Prefs.get("LastOpenLogo", ""), "Select Logo");
        try {
            if ( Logo == null ) {
                if ( !force.LogoPath.isEmpty() ) {
                    if ( javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to remove your current logo?", "Remove Logo", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION ) {
                        setLogo(lblLogo, null);
                        force.LogoPath = "";
                        force.isDirty = true;
                    }
                }
            } else {
                force.LogoPath = Logo.getCanonicalPath();
                force.isDirty = true;
                setLogo(lblLogo, Logo);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void clearPopupActions(JMenuItem popMenu) {
        ActionListener[] actions = popMenu.getActionListeners();
        for ( ActionListener a : actions ) {
            popMenu.removeActionListener(a);
        }
    }

    private void SetGroup(JTable Table, Force force) {
        String group = javax.swing.JOptionPane.showInputDialog("Enter Lance/Star Name");
        int[] rows = Table.getSelectedRows();
        for (int i=0; i < rows.length; i++ ) {
            Unit u = (Unit) force.getUnits().get(Table.convertRowIndexToModel(rows[i]));
            u.setGroup(group);
            force.GroupUnit(u);
        }
        force.RefreshBV();
    }

    private void setLogo( javax.swing.JLabel lblLogo, File Logo ) {
        lblLogo.setIcon(null);
        if ( Logo != null && ! Logo.getPath().isEmpty() ) {
            try {
               Prefs.put("LastOpenLogo", Logo.getPath().toString());
               ImageIcon icon = new ImageIcon(Logo.getPath());

                if( icon == null ) { return; }

                // See if we need to scale
                int lblH = lblLogo.getHeight()-lblLogo.getIconTextGap();
                int lblW = lblLogo.getWidth()-lblLogo.getIconTextGap();

                int h = icon.getIconHeight();
                int w = icon.getIconWidth();
                if ( w > lblW || h > lblH ) {
                    if ( h > lblH ) {
                        icon = new ImageIcon(icon.getImage().
                            getScaledInstance(-1, lblH, Image.SCALE_SMOOTH));
                        w = icon.getIconWidth();
                    }
                    if ( w > lblW ) {
                        icon = new ImageIcon(icon.getImage().
                            getScaledInstance(lblW, -1, Image.SCALE_SMOOTH));
                    }
                }

                lblLogo.setIcon(icon);
            } catch ( Exception e ) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void editUnit( javax.swing.JTable Table, Force force ) {
        if ( Table.getSelectedRowCount() > 0 ) {
            Unit u = (Unit) force.getUnits().get(Table.convertRowIndexToModel(Table.getSelectedRow()));
            dlgUnit dUnit = new dlgUnit(this, true, force, u, images);
            dUnit.setLocationRelativeTo(this);
            dUnit.setVisible(true);
            force.RefreshBV();
        }
    }

    private void removeUnits( javax.swing.JTable Table, Force force ) {
         int[] rows = Table.getSelectedRows();
         ArrayList<Unit> units = new ArrayList<Unit>();
         for ( int i : rows ) {
             Unit u = (Unit) force.getUnits().get(Table.convertRowIndexToModel(i));
             units.add(u);
         }
         for ( Unit u : units ) {
             force.RemoveUnit(u);
         }
    }

    private void switchUnits( javax.swing.JTable Table, Force forceFrom, Force forceTo ) {
        int[] rows = Table.getSelectedRows();
        Unit[] units = new Unit[rows.length];
        for (int i=0; i < rows.length; i++ ) {
            Unit u = (Unit) forceFrom.getUnits().get(Table.convertRowIndexToModel(rows[i]));
            units[i] = u;
        }
        for (int j=0; j < units.length; j++) {
            forceFrom.RemoveUnit(units[j]);
            forceTo.AddUnit(units[j]);
        }
        forceFrom.clearEmptyGroups();
        forceTo.clearEmptyGroups();
    }

    private void SetType( Force force, String type ) {
        force.setType(type);
    }

    private void validateChanges() {
        if ((scenario.getAttackerForce().isDirty) || (scenario.getDefenderForce().isDirty) || scenario.IsDirty()) {
                    switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                        case javax.swing.JOptionPane.YES_OPTION:
                            this.mnuSaveActionPerformed(null);
                        case javax.swing.JOptionPane.CANCEL_OPTION:
                            return;
                    }
        }
    }

    private void OpenDialog( Force force ) {
        addToForce = force;
        jTabbedPane1.setSelectedComponent(pnlSelect);
        txtName.requestFocus();
        //dOpen.setForce(force);
        //dOpen.setLocationRelativeTo(this);
        //dOpen.setSize(1024, 768);
        //dOpen.setVisible(true);
    }

    private void OpenQuickAdd( Force force ) {
        dlgQuickAdd dlgQAdd = new dlgQuickAdd(this, true, force);
        dlgQAdd.setLocationRelativeTo(this);
        dlgQAdd.setVisible(true);
    }

    public void setScenario( String scenario ) {
        txtScenarioName.setText(scenario);
    }

    public String getScenario() {
        return txtScenarioName.getText();
    }

    public void openForce( Force force ) {
        File forceFile = media.SelectFile(Prefs.get("LastOpenUnit", ""), "force", "Load Force");

        WaitCursor();
        if (forceFile != null) {
            XMLReader reader = new XMLReader();
            try {
                Force f = new Force();
                reader.ReadUnit( f, forceFile.getCanonicalPath() );
                force.ForceName = f.ForceName;
                force.LogoPath = f.LogoPath;
                for ( Unit u : f.getUnits() ) {
                    force.AddUnit(u);
                }
                Refresh();

               Prefs.put("LastOpenUnit", forceFile.getCanonicalPath());
            } catch (Exception e) {
                Media.Messager("Issue loading file!\n" + e.getMessage());
                System.out.println(e.getMessage());
            }
        }
        DefaultCursor();
    }

    public void saveForce( Force force ) {
        if ( ! force.isSaveable() ) {
            Media.Messager(this, "Please enter a unit name and at least one unit before saving.");
            return;
        }
        String dirPath = media.GetDirectorySelection(this, Prefs.get("LastOpenUnit", ""));
        if ( dirPath.isEmpty() ) { return;}

        XMLWriter write = new XMLWriter();
        try {
            String filename = dirPath + File.separator + CommonTools.FormatFileName(force.ForceName) + ".force";
            write.SerializeForce(force, filename);
            Media.Messager( this, "Force written to " + filename );
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void toClipboard( Force[] forces ) {
        String data = "";

        data += scenario.SerializeClipboard();

        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( data );
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( export, this );
    }

    public void toClipboardSingle( Force force ) {
        String data = "";

        data += force.SerializeClipboard();

        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( data );
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( export, this );
    }

    private void overrideSkill( Force force, int Gunnery, int Piloting ) {
        for ( int i=0; i < force.getUnits().size(); i++ ) {
            Unit u = (Unit) force.getUnits().get(i);
            u.setGunnery(Gunnery);
            u.setPiloting(Piloting);
            u.Refresh();
        }
        force.RefreshBV();
    }

    private void WaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private void DefaultCursor() {
        setCursor(Cursor.getDefaultCursor());
    }

    private void balanceSkills( JTable table, Force force ) {
        dlgBalance balance = new dlgBalance(this, true, force);
        balance.setLocationRelativeTo(this);
        balance.setVisible(true);

        Skills skills;
        if ( balance.Result != dlgBalance.SK_CANCEL ) {
            if ( table.getSelectedRowCount() == 0 ) {
                table.selectAll();
            }
        }
        switch ( balance.Result ) {
            case dlgBalance.SK_BESTSKILLS:
                skills = balance.skills;
                for ( int i : table.getSelectedRows() ) {
                    Unit u = (Unit) force.getUnits().get(table.convertRowIndexToModel(i));
                    skills.setBV(u.BaseBV);
                    Skill skill = skills.getBestSkills();
                    u.setGunnery(skill.getGunnery());
                    u.setPiloting(skill.getPiloting());
                    u.Refresh();
                }
                force.isDirty = true;
                break;

            case dlgBalance.SK_RANDOMSKILLS:
                skills = balance.skills;
                for ( int i : table.getSelectedRows() ) {
                    Unit u = (Unit) force.getUnits().get(table.convertRowIndexToModel(i));
                    Skill skill = skills.generateRandomSkill();
                    u.setGunnery(skill.getGunnery());
                    u.setPiloting(skill.getPiloting());
                    u.Refresh();
                }
                force.isDirty = true;
                break;
            case dlgBalance.SK_RANDOMNAME:
                NameGenerator gen = new NameGenerator();
                boolean overwrite = balance.overwrite;
                for ( int i : table.getSelectedRows() ) {
                    Unit u = (Unit) force.getUnits().get(table.convertRowIndexToModel(i));
                    if ( u.getMechwarrior().isEmpty() || overwrite ) u.setMechwarrior(gen.SimpleGenerate());
                }
        }
        scenario.Refresh();
    }

    private void ManageGroup() {
        dlgGroup dlggroup = new dlgGroup(this, false, scenario);
        dlggroup.setLocationRelativeTo(this);
        dlggroup.setVisible(true);
    }

    private void ChangeC3( Force force, JComboBox selection ) {
        boolean UseC3 = false;
        if ( selection.getSelectedItem().toString().equals("On") ) { UseC3 = true; }
        for ( Unit u : force.getUnits() ) {
            u.LoadUnit();
            if ( u.m != null ) {
                if ( u.m.HasC3() ) {
                    u.UsingC3 = UseC3;
                }
            }
        }
        force.RefreshBV();
        Refresh();
    }

    private PagePrinter SetupPrinter() {
        PagePrinter printer = new PagePrinter();

        printer.setJobName(scenario.getName());

        //Force List
        ForceListPrinter sheet = new ForceListPrinter(images);
        sheet.setTitle(scenario.getName());
        sheet.AddForces(scenario.getForces());
        printer.Append( BFBPrinter.Letter.toPage(), sheet );
        return printer;
    }











    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrpTop = new javax.swing.ButtonGroup();
        btnGrpBottom = new javax.swing.ButtonGroup();
        btnGrpViews = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnLoad = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPrint = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnMULExport = new javax.swing.JButton();
        btnClipboard = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        btnGroupTop = new javax.swing.JButton();
        btnManageImages = new javax.swing.JButton();
        btnClearImages = new javax.swing.JButton();
        btnPersonnel = new javax.swing.JButton();
        lblScenarioName = new javax.swing.JLabel();
        txtScenarioName = new javax.swing.JTextField();
        chkUseForceModifier = new javax.swing.JCheckBox();
        lblForceMod = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        pnlBottom = new javax.swing.JPanel();
        spnBottom = new javax.swing.JScrollPane();
        tblBottom = new javax.swing.JTable();
        lblUnitNameBottom = new javax.swing.JLabel();
        txtUnitNameBottom = new javax.swing.JTextField();
        lblUnitLogoBottom = new javax.swing.JLabel();
        lblTotalBVBottom = new javax.swing.JLabel();
        lblUnitsBottom = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblTonnageBottom = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblBaseBVBottom = new javax.swing.JLabel();
        tlbBottom = new javax.swing.JToolBar();
        btnAddBottom1 = new javax.swing.JButton();
        btnQuickAddBottom = new javax.swing.JButton();
        btnAddGeneric1 = new javax.swing.JButton();
        btnEditBottom1 = new javax.swing.JButton();
        btnDeleteBottom1 = new javax.swing.JButton();
        btnSwitchBottom = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnBalanceBottom = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        btnOpenBottom = new javax.swing.JButton();
        btnSaveBottom = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        btnClipboardBottom = new javax.swing.JButton();
        btnISBottom = new javax.swing.JRadioButton();
        btnCLBottom = new javax.swing.JRadioButton();
        btnCSBottom = new javax.swing.JRadioButton();
        cmbC3Bottom = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        txtBottomGun = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtBottomPilot = new javax.swing.JTextField();
        pnlTop = new javax.swing.JPanel();
        spnTop = new javax.swing.JScrollPane();
        tblTop = new javax.swing.JTable();
        lblUnitNameTop = new javax.swing.JLabel();
        txtUnitNameTop = new javax.swing.JTextField();
        lblUnitLogoTop = new javax.swing.JLabel();
        lblTotalBVTop = new javax.swing.JLabel();
        lblUnitsTop = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblTonnageTop = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblBaseBVTop = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tlbTop = new javax.swing.JToolBar();
        btnAddTop1 = new javax.swing.JButton();
        btnQuickAdd = new javax.swing.JButton();
        btnAddGeneric = new javax.swing.JButton();
        btnEditTop1 = new javax.swing.JButton();
        btnDeleteTop1 = new javax.swing.JButton();
        btnSwitchTop = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnBalanceTop = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        btnOpenTop = new javax.swing.JButton();
        btnSaveTop = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btnClipboardTop = new javax.swing.JButton();
        btnISTop = new javax.swing.JRadioButton();
        btnCLTop = new javax.swing.JRadioButton();
        btnCSTop = new javax.swing.JRadioButton();
        cmbC3Top = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtTopPilot = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTopGun = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        edtSetup = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        edtVictoryConditions = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        edtAftermath = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        edtSituation = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        btnAddBonus = new javax.swing.JButton();
        txtAmount = new javax.swing.JFormattedTextField();
        txtTrackCost = new javax.swing.JFormattedTextField();
        txtObjective = new javax.swing.JTextField();
        txtBonus = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtReward = new javax.swing.JFormattedTextField();
        btnAddObjective = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstObjectives = new javax.swing.JList();
        jScrollPane6 = new javax.swing.JScrollPane();
        lstBonuses = new javax.swing.JList();
        jScrollPane7 = new javax.swing.JScrollPane();
        edtAttacker = new javax.swing.JTextPane();
        jScrollPane8 = new javax.swing.JScrollPane();
        edtDefender = new javax.swing.JTextPane();
        jScrollPane9 = new javax.swing.JScrollPane();
        edtSpecialRules = new javax.swing.JTextPane();
        jLabel24 = new javax.swing.JLabel();
        pnlSelect = new javax.swing.JPanel();
        pnlFilters = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        btnFilter = new javax.swing.JButton();
        btnClearFilter = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        lblMinMP = new javax.swing.JLabel();
        cmbMinMP = new javax.swing.JComboBox();
        jPanel10 = new javax.swing.JPanel();
        cmbEra = new javax.swing.JComboBox();
        lblEra = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        lblMotive = new javax.swing.JLabel();
        cmbMotive = new javax.swing.JComboBox();
        jPanel12 = new javax.swing.JPanel();
        cmbTech = new javax.swing.JComboBox();
        lblTech = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        lblLevel = new javax.swing.JLabel();
        cmbRulesLevel = new javax.swing.JComboBox();
        jPanel15 = new javax.swing.JPanel();
        lblType = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        jPanel29 = new javax.swing.JPanel();
        lblMotive1 = new javax.swing.JLabel();
        cmbUnitType = new javax.swing.JComboBox();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        txtSource = new javax.swing.JTextField();
        lblSource = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        txtMaxBV = new javax.swing.JTextField();
        lblBV = new javax.swing.JLabel();
        txtMinBV = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        lblTonnage1 = new javax.swing.JLabel();
        txtMinYear = new javax.swing.JTextField();
        txtMaxYear = new javax.swing.JTextField();
        jPanel21 = new javax.swing.JPanel();
        lblTonnage = new javax.swing.JLabel();
        txtMaxTon = new javax.swing.JTextField();
        txtMinTon = new javax.swing.JTextField();
        jPanel22 = new javax.swing.JPanel();
        txtMinCost = new javax.swing.JTextField();
        lblCost = new javax.swing.JLabel();
        txtMaxCost = new javax.swing.JTextField();
        chkOmni = new javax.swing.JCheckBox();
        jScrollPane10 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel23 = new javax.swing.JPanel();
        spnMechTable = new javax.swing.JScrollPane();
        tblMechData = new javax.swing.JTable();
        jPanel24 = new javax.swing.JPanel();
        btnOpenMech = new javax.swing.JButton();
        btnOpenDir = new javax.swing.JButton();
        txtInfo = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        lblStatusUpdate = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        treDirectories = new javax.swing.JTree();
        jPanel25 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        lstFiles = new javax.swing.JList();
        jPanel26 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        btnRoll = new javax.swing.JButton();
        spnSelections = new javax.swing.JSpinner();
        jLabel28 = new javax.swing.JLabel();
        spnAddOn = new javax.swing.JSpinner();
        jLabel29 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        jScrollPane13 = new javax.swing.JScrollPane();
        lstOptions = new javax.swing.JList();
        pnlRandomSelection = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        lstSelected = new javax.swing.JList();
        btnClearSelection = new javax.swing.JButton();
        btnClipboard1 = new javax.swing.JButton();
        lblRndStatus = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuNew = new javax.swing.JMenuItem();
        mnuLoad = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        mnuSave = new javax.swing.JMenuItem();
        mnuSaveAs = new javax.swing.JMenuItem();
        mnuSaveScenario = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuPrint = new javax.swing.JMenu();
        mnuPrintDlg = new javax.swing.JMenuItem();
        mnuPrintForce = new javax.swing.JMenuItem();
        mnuPrintUnits = new javax.swing.JMenuItem();
        mnuPrintRS = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuExit = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        mnuExportMUL = new javax.swing.JMenuItem();
        mnuExportText = new javax.swing.JMenuItem();
        mnuExportClipboard = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        mnuBVList = new javax.swing.JMenuItem();
        mnuFactorList = new javax.swing.JMenuItem();
        mnuCurrentList = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        rmnuTWModel = new javax.swing.JRadioButtonMenuItem();
        rmnuBFModel = new javax.swing.JRadioButtonMenuItem();
        rmnuInformation = new javax.swing.JRadioButtonMenuItem();
        rmnuFCTModel = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuDesignBattleMech = new javax.swing.JMenuItem();
        mnuDesignVehicle = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mnuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Battletech Force Balancer");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/document--plus.png"))); // NOI18N
        btnNew.setToolTipText("New Scenario");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/folder-open-document.png"))); // NOI18N
        btnLoad.setToolTipText("Open Scenario");
        btnLoad.setFocusable(false);
        btnLoad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLoad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });
        jToolBar1.add(btnLoad);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/disk-black.png"))); // NOI18N
        btnSave.setToolTipText("Save Scenario");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);
        jToolBar1.add(jSeparator1);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/printer.png"))); // NOI18N
        btnPrint.setToolTipText("Preview/Print Forces");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);
        jToolBar1.add(jSeparator4);

        btnMULExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/map--arrow.png"))); // NOI18N
        btnMULExport.setToolTipText("Export Forces to MUL");
        btnMULExport.setFocusable(false);
        btnMULExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMULExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMULExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMULExportActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMULExport);

        btnClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/clipboard.png"))); // NOI18N
        btnClipboard.setToolTipText("Export Scenario to Clipboard");
        btnClipboard.setFocusable(false);
        btnClipboard.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClipboard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClipboardActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClipboard);
        jToolBar1.add(jSeparator9);

        btnGroupTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/photo-album.png"))); // NOI18N
        btnGroupTop.setToolTipText("Group Information");
        btnGroupTop.setFocusable(false);
        btnGroupTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGroupTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGroupTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGroupTopActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGroupTop);

        btnManageImages.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/images-stack.png"))); // NOI18N
        btnManageImages.setToolTipText("Manage BFB.Images");
        btnManageImages.setFocusable(false);
        btnManageImages.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnManageImages.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnManageImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageImagesActionPerformed(evt);
            }
        });
        jToolBar1.add(btnManageImages);

        btnClearImages.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/eraser.png"))); // NOI18N
        btnClearImages.setToolTipText("Clear Pre-Selected BFB.Images");
        btnClearImages.setFocusable(false);
        btnClearImages.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClearImages.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClearImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearImagesActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClearImages);

        btnPersonnel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Mechwarrior.png"))); // NOI18N
        btnPersonnel.setToolTipText("Manage Personnel");
        btnPersonnel.setFocusable(false);
        btnPersonnel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPersonnel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPersonnel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPersonnelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPersonnel);

        lblScenarioName.setText("Scenario / Event Name: ");

        txtScenarioName.setToolTipText("Enter the name of the scenario or event");

        chkUseForceModifier.setText("Use Force Size Modifier");
        chkUseForceModifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseForceModifierActionPerformed(evt);
            }
        });

        lblForceMod.setText("0.00");

        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });
        jTabbedPane1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTabbedPane1KeyReleased(evt);
            }
        });

        pnlBottom.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Secondary Force Listing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 1, 12), new java.awt.Color(0, 51, 204))); // NOI18N
        pnlBottom.setPreferredSize(new java.awt.Dimension(1010, 250));

        tblBottom.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBottom.setRowMargin(2);
        tblBottom.setShowVerticalLines(false);
        tblBottom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBottomMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblBottomMouseReleased(evt);
            }
        });
        tblBottom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblBottomKeyReleased(evt);
            }
        });
        spnBottom.setViewportView(tblBottom);

        lblUnitNameBottom.setText("Unit Name:");

        txtUnitNameBottom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUnitNameBottomFocusLost(evt);
            }
        });
        txtUnitNameBottom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtUnitNameBottomKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUnitNameBottomKeyTyped(evt);
            }
        });

        lblUnitLogoBottom.setToolTipText("Logo: Click to change");
        lblUnitLogoBottom.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        lblUnitLogoBottom.setOpaque(true);
        lblUnitLogoBottom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblUnitLogoBottomMouseClicked(evt);
            }
        });

        lblTotalBVBottom.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lblTotalBVBottom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalBVBottom.setText("0,000 BV");

        lblUnitsBottom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUnitsBottom.setText("0");

        jLabel3.setText("Units");

        lblTonnageBottom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTonnageBottom.setText("0");

        jLabel5.setText("Tons");

        jLabel7.setText("BV");

        lblBaseBVBottom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBaseBVBottom.setText("0,000");

        tlbBottom.setFloatable(false);
        tlbBottom.setRollover(true);

        btnAddBottom1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--plus.png"))); // NOI18N
        btnAddBottom1.setToolTipText("Add Unit");
        btnAddBottom1.setFocusable(false);
        btnAddBottom1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddBottom1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddBottom1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBottom1ActionPerformed(evt);
            }
        });
        tlbBottom.add(btnAddBottom1);

        btnQuickAddBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--arrow.png"))); // NOI18N
        btnQuickAddBottom.setToolTipText("Quick Add");
        btnQuickAddBottom.setFocusable(false);
        btnQuickAddBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnQuickAddBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnQuickAddBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickAddBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnQuickAddBottom);

        btnAddGeneric1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--gear.png"))); // NOI18N
        btnAddGeneric1.setToolTipText("Add Generic Unit");
        btnAddGeneric1.setFocusable(false);
        btnAddGeneric1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddGeneric1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddGeneric1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddGeneric1ActionPerformed(evt);
            }
        });
        tlbBottom.add(btnAddGeneric1);

        btnEditBottom1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--pencil.png"))); // NOI18N
        btnEditBottom1.setToolTipText("Edit Unit");
        btnEditBottom1.setFocusable(false);
        btnEditBottom1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditBottom1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditBottom1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditBottom1ActionPerformed(evt);
            }
        });
        tlbBottom.add(btnEditBottom1);

        btnDeleteBottom1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--minus.png"))); // NOI18N
        btnDeleteBottom1.setToolTipText("Delete Unit");
        btnDeleteBottom1.setFocusable(false);
        btnDeleteBottom1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteBottom1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDeleteBottom1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteBottom1ActionPerformed(evt);
            }
        });
        tlbBottom.add(btnDeleteBottom1);

        btnSwitchBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--up.png"))); // NOI18N
        btnSwitchBottom.setToolTipText("Move to Primary Force");
        btnSwitchBottom.setFocusable(false);
        btnSwitchBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSwitchBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSwitchBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSwitchBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnSwitchBottom);
        tlbBottom.add(jSeparator5);

        btnBalanceBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/ruler-triangle.png"))); // NOI18N
        btnBalanceBottom.setToolTipText("Auto-Balance");
        btnBalanceBottom.setFocusable(false);
        btnBalanceBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBalanceBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBalanceBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBalanceBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnBalanceBottom);
        tlbBottom.add(jSeparator11);

        btnOpenBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/folder-open-document.png"))); // NOI18N
        btnOpenBottom.setToolTipText("Open Force");
        btnOpenBottom.setFocusable(false);
        btnOpenBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnOpenBottom);

        btnSaveBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/disk.png"))); // NOI18N
        btnSaveBottom.setToolTipText("Save Force");
        btnSaveBottom.setFocusable(false);
        btnSaveBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnSaveBottom);
        tlbBottom.add(jSeparator8);

        btnClipboardBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/clipboard.png"))); // NOI18N
        btnClipboardBottom.setToolTipText("Export Force to Clipboard");
        btnClipboardBottom.setFocusable(false);
        btnClipboardBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClipboardBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClipboardBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClipboardBottomActionPerformed(evt);
            }
        });
        tlbBottom.add(btnClipboardBottom);

        btnGrpBottom.add(btnISBottom);
        btnISBottom.setSelected(true);
        btnISBottom.setText("Inner Sphere");
        btnISBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnISBottomActionPerformed(evt);
            }
        });

        btnGrpBottom.add(btnCLBottom);
        btnCLBottom.setText("Clan");
        btnCLBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCLBottomActionPerformed(evt);
            }
        });

        btnGrpBottom.add(btnCSBottom);
        btnCSBottom.setText("Comstar");
        btnCSBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCSBottomActionPerformed(evt);
            }
        });

        cmbC3Bottom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Off", "On" }));
        cmbC3Bottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbC3BottomActionPerformed(evt);
            }
        });

        jLabel17.setText("C3");

        txtBottomGun.setText("4");
        txtBottomGun.setPreferredSize(new java.awt.Dimension(15, 20));
        txtBottomGun.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBottomGunFocusGained(evt);
            }
        });
        txtBottomGun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBottomGunKeyReleased(evt);
            }
        });

        jLabel11.setText("P");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Skill Override");

        jLabel25.setText("G");

        txtBottomPilot.setText("5");
        txtBottomPilot.setPreferredSize(new java.awt.Dimension(15, 20));
        txtBottomPilot.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBottomPilotFocusGained(evt);
            }
        });
        txtBottomPilot.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBottomPilotKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBottomGun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addGap(3, 3, 3)
                .addComponent(txtBottomPilot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(1, 1, 1)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBottomGun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBottomPilot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel25)))
        );

        javax.swing.GroupLayout pnlBottomLayout = new javax.swing.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBottomLayout.createSequentialGroup()
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBottomLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlBottomLayout.createSequentialGroup()
                                .addComponent(lblUnitLogoBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7))
                            .addGroup(pnlBottomLayout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlBottomLayout.createSequentialGroup()
                                .addComponent(lblUnitNameBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtUnitNameBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnISBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCLBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCSBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbC3Bottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                                .addComponent(tlbBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spnBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 857, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBottomLayout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(lblUnitsBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(313, 313, 313)
                        .addComponent(lblTonnageBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(lblBaseBVBottom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 280, Short.MAX_VALUE)
                        .addComponent(lblTotalBVBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlBottomLayout.setVerticalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBottomLayout.createSequentialGroup()
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBottomLayout.createSequentialGroup()
                        .addComponent(lblUnitLogoBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlBottomLayout.createSequentialGroup()
                        .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblUnitNameBottom)
                                .addComponent(txtUnitNameBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnISBottom)
                                .addComponent(btnCLBottom)
                                .addComponent(btnCSBottom)
                                .addComponent(cmbC3Bottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel17))
                            .addComponent(tlbBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUnitsBottom)
                    .addComponent(jLabel3)
                    .addComponent(lblTonnageBottom)
                    .addComponent(jLabel5)
                    .addComponent(lblBaseBVBottom)
                    .addComponent(jLabel7)
                    .addComponent(lblTotalBVBottom)))
        );

        pnlTop.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Primary Force Listing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 1, 12), new java.awt.Color(0, 51, 204))); // NOI18N
        pnlTop.setPreferredSize(new java.awt.Dimension(1024, 250));

        tblTop.setModel(new javax.swing.table.DefaultTableModel(
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
        tblTop.setRowMargin(2);
        tblTop.setShowVerticalLines(false);
        tblTop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTopMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblTopMouseReleased(evt);
            }
        });
        tblTop.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblTopKeyReleased(evt);
            }
        });
        spnTop.setViewportView(tblTop);

        lblUnitNameTop.setText("Unit Name:");

        txtUnitNameTop.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUnitNameTopFocusLost(evt);
            }
        });
        txtUnitNameTop.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtUnitNameTopKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUnitNameTopKeyTyped(evt);
            }
        });

        lblUnitLogoTop.setToolTipText("Logo: Click to change");
        lblUnitLogoTop.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        lblUnitLogoTop.setOpaque(true);
        lblUnitLogoTop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblUnitLogoTopMouseClicked(evt);
            }
        });

        lblTotalBVTop.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lblTotalBVTop.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalBVTop.setText("0,000 BV");

        lblUnitsTop.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUnitsTop.setText("0");

        jLabel2.setText("Units");

        lblTonnageTop.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTonnageTop.setText("0");

        jLabel4.setText("Tons");

        lblBaseBVTop.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBaseBVTop.setText("0,000");

        jLabel6.setText("BV");

        tlbTop.setFloatable(false);
        tlbTop.setRollover(true);

        btnAddTop1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--plus.png"))); // NOI18N
        btnAddTop1.setToolTipText("Add Unit");
        btnAddTop1.setFocusable(false);
        btnAddTop1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddTop1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddTop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTop1ActionPerformed(evt);
            }
        });
        tlbTop.add(btnAddTop1);

        btnQuickAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--arrow.png"))); // NOI18N
        btnQuickAdd.setToolTipText("Quick Add");
        btnQuickAdd.setFocusable(false);
        btnQuickAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnQuickAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnQuickAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickAddActionPerformed(evt);
            }
        });
        tlbTop.add(btnQuickAdd);

        btnAddGeneric.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--gear.png"))); // NOI18N
        btnAddGeneric.setToolTipText("Add Generic Unit");
        btnAddGeneric.setFocusable(false);
        btnAddGeneric.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddGeneric.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddGeneric.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddGenericActionPerformed(evt);
            }
        });
        tlbTop.add(btnAddGeneric);

        btnEditTop1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--pencil.png"))); // NOI18N
        btnEditTop1.setToolTipText("Edit Unit");
        btnEditTop1.setFocusable(false);
        btnEditTop1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditTop1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditTop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditTop1ActionPerformed(evt);
            }
        });
        tlbTop.add(btnEditTop1);

        btnDeleteTop1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--minus.png"))); // NOI18N
        btnDeleteTop1.setToolTipText("Delete Unit");
        btnDeleteTop1.setFocusable(false);
        btnDeleteTop1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteTop1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDeleteTop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteTop1ActionPerformed(evt);
            }
        });
        tlbTop.add(btnDeleteTop1);

        btnSwitchTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shield--down.png"))); // NOI18N
        btnSwitchTop.setToolTipText("Move to Secondary Force");
        btnSwitchTop.setFocusable(false);
        btnSwitchTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSwitchTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSwitchTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSwitchTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnSwitchTop);
        tlbTop.add(jSeparator6);

        btnBalanceTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/ruler-triangle.png"))); // NOI18N
        btnBalanceTop.setToolTipText("Auto-Balance");
        btnBalanceTop.setFocusable(false);
        btnBalanceTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBalanceTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBalanceTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBalanceTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnBalanceTop);
        tlbTop.add(jSeparator10);

        btnOpenTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/folder-open-document.png"))); // NOI18N
        btnOpenTop.setToolTipText("Open Force");
        btnOpenTop.setFocusable(false);
        btnOpenTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnOpenTop);

        btnSaveTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/disk.png"))); // NOI18N
        btnSaveTop.setToolTipText("Save Force");
        btnSaveTop.setFocusable(false);
        btnSaveTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnSaveTop);
        tlbTop.add(jSeparator7);

        btnClipboardTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/clipboard.png"))); // NOI18N
        btnClipboardTop.setToolTipText("Export Force to Clipboard");
        btnClipboardTop.setFocusable(false);
        btnClipboardTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClipboardTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClipboardTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClipboardTopActionPerformed(evt);
            }
        });
        tlbTop.add(btnClipboardTop);

        btnGrpTop.add(btnISTop);
        btnISTop.setSelected(true);
        btnISTop.setText("Inner Sphere");
        btnISTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnISTopActionPerformed(evt);
            }
        });

        btnGrpTop.add(btnCLTop);
        btnCLTop.setText("Clan");
        btnCLTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCLTopActionPerformed(evt);
            }
        });

        btnGrpTop.add(btnCSTop);
        btnCSTop.setText("Comstar");
        btnCSTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCSTopActionPerformed(evt);
            }
        });

        cmbC3Top.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Off", "On" }));
        cmbC3Top.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbC3TopActionPerformed(evt);
            }
        });

        jLabel16.setText("C3");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Skill Override");

        jLabel18.setText("G");

        txtTopPilot.setText("5");
        txtTopPilot.setPreferredSize(new java.awt.Dimension(15, 20));
        txtTopPilot.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTopPilotFocusGained(evt);
            }
        });
        txtTopPilot.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTopPilotKeyReleased(evt);
            }
        });

        jLabel9.setText("  P");

        txtTopGun.setText("4");
        txtTopGun.setPreferredSize(new java.awt.Dimension(15, 20));
        txtTopGun.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTopGunFocusGained(evt);
            }
        });
        txtTopGun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTopGunKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTopGun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTopPilot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(4, 4, 4)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTopPilot, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTopGun, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel9))))
        );

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTopLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblUnitLogoTop, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlTopLayout.createSequentialGroup()
                                .addComponent(lblUnitNameTop)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtUnitNameTop, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnISTop)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCLTop)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCSTop)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbC3Top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tlbTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spnTop)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopLayout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(lblUnitsTop, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(307, 307, 307)
                        .addComponent(lblTonnageTop, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(lblBaseBVTop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalBVTop, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlTopLayout.setVerticalGroup(
            pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopLayout.createSequentialGroup()
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlTopLayout.createSequentialGroup()
                        .addComponent(lblUnitLogoTop, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlTopLayout.createSequentialGroup()
                        .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblUnitNameTop)
                                .addComponent(txtUnitNameTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnISTop)
                                .addComponent(btnCLTop)
                                .addComponent(btnCSTop)
                                .addComponent(cmbC3Top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel16))
                            .addComponent(tlbTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnTop, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUnitsTop)
                    .addComponent(jLabel2)
                    .addComponent(lblTonnageTop)
                    .addComponent(jLabel4)
                    .addComponent(lblBaseBVTop)
                    .addComponent(jLabel6)
                    .addComponent(lblTotalBVTop)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlBottom, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1102, Short.MAX_VALUE)
                    .addComponent(pnlTop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1102, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Force Selections", jPanel1);

        jLabel12.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel12.setText("Setup");

        jLabel13.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel13.setText("Victory Conditions");

        jLabel14.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel14.setText("Aftermath");

        jLabel15.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel15.setText("Only used for non-warchest system scenarios");

        jScrollPane2.setViewportView(edtSetup);

        jScrollPane3.setViewportView(edtVictoryConditions);

        jScrollPane5.setViewportView(edtAftermath);

        jScrollPane1.setViewportView(edtSituation);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Situation");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15))
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Scenario Information", jPanel2);

        jLabel19.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel19.setText("Attacker");

        jLabel20.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel20.setText("Defender");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Track Cost:");

        btnAddBonus.setText("Add Bonus");
        btnAddBonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBonusActionPerformed(evt);
            }
        });

        txtAmount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));

        txtTrackCost.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        txtTrackCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTrackCostActionPerformed(evt);
            }
        });
        txtTrackCost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTrackCostKeyReleased(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Optional Bonuses");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Objectives");

        txtReward.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        txtReward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRewardActionPerformed(evt);
            }
        });

        btnAddObjective.setText("Add Objective");
        btnAddObjective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddObjectiveActionPerformed(evt);
            }
        });

        lstObjectives.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstObjectivesValueChanged(evt);
            }
        });
        lstObjectives.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lstObjectivesKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(lstObjectives);

        lstBonuses.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstBonusesValueChanged(evt);
            }
        });
        lstBonuses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lstBonusesKeyReleased(evt);
            }
        });
        jScrollPane6.setViewportView(lstBonuses);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTrackCost, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtBonus)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnAddBonus, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(3, 3, 3)))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(txtReward, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtObjective, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddObjective))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtTrackCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtObjective, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAddObjective)
                        .addComponent(txtReward, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtBonus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAddBonus))))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jScrollPane4, jScrollPane6});

        jScrollPane7.setViewportView(edtAttacker);

        jScrollPane8.setViewportView(edtDefender);

        jScrollPane9.setViewportView(edtSpecialRules);

        jLabel24.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel24.setText("Special Rules");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE)
                            .addComponent(jLabel24))
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(448, 448, 448))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jScrollPane8)
                                .addContainerGap())))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane7)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Warchest Information", jPanel3);

        pnlSelect.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pnlSelectKeyReleased(evt);
            }
        });

        pnlFilters.setBorder(javax.swing.BorderFactory.createTitledBorder("Filters"));

        jLabel26.setText("Class");

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterFilter(evt);
            }
        });

        btnClearFilter.setText("Clear");
        btnClearFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFilterFilter(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnFilter)
            .addComponent(btnClearFilter)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(btnFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearFilter))
        );

        lblMinMP.setText("Min MP");

        cmbMinMP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));
        cmbMinMP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMinMPFilter(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMinMP)
            .addComponent(cmbMinMP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(lblMinMP)
                .addGap(1, 1, 1)
                .addComponent(cmbMinMP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        cmbEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Era", "Age of War/Star League", "Succession Wars", "Clan Invasion", "Dark Ages", "All Eras (non-canon)" }));
        cmbEra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEraFilter(evt);
            }
        });

        lblEra.setText("Era");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbEra, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblEra)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(lblEra)
                .addGap(1, 1, 1)
                .addComponent(cmbEra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lblMotive.setText("Motive Type");

        cmbMotive.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Motive", "Biped", "Quad" }));
        cmbMotive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMotiveFilter(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMotive)
            .addComponent(cmbMotive, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(lblMotive)
                .addGap(1, 1, 1)
                .addComponent(cmbMotive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        cmbTech.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Tech", "Clan", "Inner Sphere", "Mixed" }));
        cmbTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTechFilter(evt);
            }
        });

        lblTech.setText("Technology");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbTech, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblTech)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(lblTech)
                .addGap(0, 0, 0)
                .addComponent(cmbTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblLevel.setText("Rules Level");

        cmbRulesLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Level", "Introductory", "Tournament Legal", "Advanced Rules", "Experimental Tech", "Era Specific" }));
        cmbRulesLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbRulesLevelFilter(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbRulesLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblLevel)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(lblLevel)
                .addGap(1, 1, 1)
                .addComponent(cmbRulesLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lblType.setText("Mech Type");

        cmbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Type", "BattleMech", "IndustrialMech", "Primitive BattleMech", "Primitive IndustrialMech", "Hover", "Naval (Displacement)", "Naval (Hydrofoil)", "Naval (Submarine)", "Tracked", "VTOL", "Wheeled", "WiGE" }));
        cmbType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTypeFilter(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblType)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(lblType)
                .addGap(1, 1, 1)
                .addComponent(cmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lblMotive1.setText("Unit Type");

        cmbUnitType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Type", "BattleMech", "IndustrialMech", "ProtoMech", "Vehicle", "Infantry", "Battle Armor", "Conventional Fighter", "Aerospace Fighter", "Small Craft", "Dropship", "Support Vehicle", "Mobile Structure" }));
        cmbUnitType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbUnitTypeFilter(evt);
            }
        });

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMotive1)
            .addComponent(cmbUnitType, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addComponent(lblMotive1)
                .addGap(1, 1, 1)
                .addComponent(cmbUnitType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        lblName.setText("Name");

        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNameKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblName)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(lblName)
                .addGap(4, 4, 4)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        txtSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSourceActionPerformed(evt);
            }
        });
        txtSource.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSourceKeyReleased(evt);
            }
        });

        lblSource.setText("Source");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtSource, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblSource)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(lblSource)
                .addGap(4, 4, 4)
                .addComponent(txtSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        txtMaxBV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaxBVFilter(evt);
            }
        });
        txtMaxBV.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMaxBVFocusLost(evt);
            }
        });

        lblBV.setText("Battle Value");

        txtMinBV.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMinBVFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(txtMinBV, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaxBV, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblBV)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtMaxBV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtMinBV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addComponent(lblBV)
                .addGap(24, 24, 24))
        );

        lblTonnage1.setText("Year");

        txtMinYear.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMinYearFocusLost(evt);
            }
        });

        txtMaxYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaxYearFilter(evt);
            }
        });
        txtMaxYear.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMaxYearFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTonnage1)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(txtMinYear, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaxYear, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(lblTonnage1)
                .addGap(4, 4, 4)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaxYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMinYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        lblTonnage.setText("Tonnage");

        txtMaxTon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaxTonFilter(evt);
            }
        });
        txtMaxTon.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMaxTonFocusLost(evt);
            }
        });

        txtMinTon.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMinTonFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(txtMinTon, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaxTon, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblTonnage)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtMaxTon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtMinTon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addComponent(lblTonnage)
                .addGap(24, 24, 24))
        );

        txtMinCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMinCostFilter(evt);
            }
        });
        txtMinCost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMinCostFocusLost(evt);
            }
        });

        lblCost.setText("C-Bill Cost");

        txtMaxCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaxCostFilter(evt);
            }
        });
        txtMaxCost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMaxCostFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(txtMinCost, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaxCost, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblCost)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(lblCost)
                .addGap(4, 4, 4)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMinCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaxCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        chkOmni.setText("Omni Only");
        chkOmni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOmniActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkOmni)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkOmni)))
                .addContainerGap())
        );

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Light", "Medium", "Heavy", "Assault" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane10.setViewportView(jList1);

        javax.swing.GroupLayout pnlFiltersLayout = new javax.swing.GroupLayout(pnlFilters);
        pnlFilters.setLayout(pnlFiltersLayout);
        pnlFiltersLayout.setHorizontalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltersLayout.createSequentialGroup()
                .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFiltersLayout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlFiltersLayout.setVerticalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltersLayout.createSequentialGroup()
                .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFiltersLayout.createSequentialGroup()
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 34, Short.MAX_VALUE))
                    .addGroup(pnlFiltersLayout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(0, 0, 0)
                        .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane10, 0, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );

        spnMechTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spnMechTableMouseClicked(evt);
            }
        });
        spnMechTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                spnMechTableKeyReleased(evt);
            }
        });

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
        tblMechData.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                tblMechDataMouseMoved(evt);
            }
        });
        tblMechData.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblMechDataFocusGained(evt);
            }
        });
        tblMechData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblMechDataKeyReleased(evt);
            }
        });
        spnMechTable.setViewportView(tblMechData);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spnMechTable, javax.swing.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spnMechTable, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
        );

        btnOpenMech.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/plus.png"))); // NOI18N
        btnOpenMech.setText("Add To Selected");
        btnOpenMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenMechActionPerformed(evt);
            }
        });

        btnOpenDir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/folders.png"))); // NOI18N
        btnOpenDir.setText("Select Directory");
        btnOpenDir.setToolTipText("Change Directory");
        btnOpenDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenDirActionPerformed(evt);
            }
        });

        txtInfo.setText("Mech Information");

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/arrow-circle-double.png"))); // NOI18N
        btnRefresh.setText("Refresh List");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        lblStatusUpdate.setFont(new java.awt.Font("Trebuchet MS", 1, 11)); // NOI18N
        lblStatusUpdate.setForeground(new java.awt.Color(51, 102, 0));
        lblStatusUpdate.setText("...");

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addComponent(lblStatusUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOpenDir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOpenMech)
                .addContainerGap())
            .addComponent(txtInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addComponent(txtInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnOpenMech, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnOpenDir)
                        .addComponent(btnRefresh)
                        .addComponent(lblStatusUpdate))))
        );

        lblStatus.setText("Loading Mechs....");

        javax.swing.GroupLayout pnlSelectLayout = new javax.swing.GroupLayout(pnlSelect);
        pnlSelect.setLayout(pnlSelectLayout);
        pnlSelectLayout.setHorizontalGroup(
            pnlSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectLayout.createSequentialGroup()
                .addGroup(pnlSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSelectLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSelectLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(pnlSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlFilters, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnlSelectLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlSelectLayout.setVerticalGroup(
            pnlSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectLayout.createSequentialGroup()
                .addComponent(pnlFilters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Select Units", pnlSelect);

        jScrollPane11.setBorder(javax.swing.BorderFactory.createTitledBorder("Directories"));
        jScrollPane11.setViewportView(treDirectories);

        jPanel25.setBorder(javax.swing.BorderFactory.createTitledBorder("Tables"));

        lstFiles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstFiles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstFilesValueChanged(evt);
            }
        });
        jScrollPane12.setViewportView(lstFiles);

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
        );

        jPanel26.setBorder(javax.swing.BorderFactory.createTitledBorder("Selection Criteria"));

        jLabel27.setText("# of Selections");

        btnRoll.setText("Roll");
        btnRoll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRollActionPerformed(evt);
            }
        });

        spnSelections.setValue(1);

        jLabel28.setText("Add");

        jLabel29.setText("to all rolls");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnSelections, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRoll))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnAddOn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(spnSelections, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRoll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(spnAddOn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)))
        );

        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Items"));

        lstOptions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstOptions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstOptionsMouseClicked(evt);
            }
        });
        lstOptions.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstOptionsValueChanged(evt);
            }
        });
        jScrollPane13.setViewportView(lstOptions);

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
        );

        pnlRandomSelection.setBorder(javax.swing.BorderFactory.createTitledBorder("Random Selections"));

        lstSelected.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstSelectedValueChanged(evt);
            }
        });
        lstSelected.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstSelectedKeyPressed(evt);
            }
        });
        jScrollPane14.setViewportView(lstSelected);

        btnClearSelection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/eraser.png"))); // NOI18N
        btnClearSelection.setText("Clear");
        btnClearSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearSelectionActionPerformed(evt);
            }
        });

        btnClipboard1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/clipboard.png"))); // NOI18N
        btnClipboard1.setText("Clipboard");
        btnClipboard1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClipboard1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlRandomSelectionLayout = new javax.swing.GroupLayout(pnlRandomSelection);
        pnlRandomSelection.setLayout(pnlRandomSelectionLayout);
        pnlRandomSelectionLayout.setHorizontalGroup(
            pnlRandomSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRandomSelectionLayout.createSequentialGroup()
                .addGroup(pnlRandomSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                    .addGroup(pnlRandomSelectionLayout.createSequentialGroup()
                        .addComponent(btnClearSelection)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                        .addComponent(btnClipboard1)))
                .addContainerGap())
        );
        pnlRandomSelectionLayout.setVerticalGroup(
            pnlRandomSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRandomSelectionLayout.createSequentialGroup()
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRandomSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClipboard1)
                    .addComponent(btnClearSelection)))
        );

        lblRndStatus.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRndStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                    .addComponent(pnlRandomSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(19, 19, 19))
        );

        jPanel7Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel25, jScrollPane11});

        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(pnlRandomSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRndStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Random Generator", jPanel7);

        jMenu1.setText("File");

        mnuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mnuNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/document--plus.png"))); // NOI18N
        mnuNew.setText("New");
        mnuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewActionPerformed(evt);
            }
        });
        jMenu1.add(mnuNew);

        mnuLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        mnuLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/folder-open-document.png"))); // NOI18N
        mnuLoad.setText("Load");
        mnuLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLoadActionPerformed(evt);
            }
        });
        jMenu1.add(mnuLoad);
        jMenu1.add(jSeparator13);

        mnuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/disk-black.png"))); // NOI18N
        mnuSave.setText("Save");
        mnuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveActionPerformed(evt);
            }
        });
        jMenu1.add(mnuSave);

        mnuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/disk.png"))); // NOI18N
        mnuSaveAs.setText("Save As...");
        mnuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveAsActionPerformed(evt);
            }
        });
        jMenu1.add(mnuSaveAs);

        mnuSaveScenario.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuSaveScenario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/disks-black.png"))); // NOI18N
        mnuSaveScenario.setText("Save As Scenario");
        mnuSaveScenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveScenarioActionPerformed(evt);
            }
        });
        jMenu1.add(mnuSaveScenario);
        jMenu1.add(jSeparator2);

        mnuPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/printer.png"))); // NOI18N
        mnuPrint.setText("Print");
        mnuPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintActionPerformed(evt);
            }
        });

        mnuPrintDlg.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        mnuPrintDlg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/printer.png"))); // NOI18N
        mnuPrintDlg.setText("Print Options");
        mnuPrintDlg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintDlgActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintDlg);

        mnuPrintForce.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintForce.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/printer.png"))); // NOI18N
        mnuPrintForce.setText("Print Force List");
        mnuPrintForce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintForceActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintForce);

        mnuPrintUnits.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintUnits.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/printer--plus.png"))); // NOI18N
        mnuPrintUnits.setText("Print Unit Sheets");
        mnuPrintUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintUnitsActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintUnits);

        mnuPrintRS.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintRS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/printer--plus.png"))); // NOI18N
        mnuPrintRS.setText("Print Record Sheets");
        mnuPrintRS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintRSActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintRS);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/printer--puzzle.png"))); // NOI18N
        jMenuItem1.setText("Print BattleForce");
        mnuPrint.add(jMenuItem1);

        jMenu1.add(mnuPrint);
        jMenu1.add(jSeparator3);

        mnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        mnuExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/burn.png"))); // NOI18N
        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExit);

        jMenuBar1.add(jMenu1);

        jMenu4.setText("Export");

        mnuExportMUL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/map--arrow.png"))); // NOI18N
        mnuExportMUL.setText("MUL");
        mnuExportMUL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportMULActionPerformed(evt);
            }
        });
        jMenu4.add(mnuExportMUL);

        mnuExportText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/document-text.png"))); // NOI18N
        mnuExportText.setText("Text");
        mnuExportText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportTextActionPerformed(evt);
            }
        });
        jMenu4.add(mnuExportText);

        mnuExportClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/clipboard.png"))); // NOI18N
        mnuExportClipboard.setText("Clipboard");
        mnuExportClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportClipboardActionPerformed(evt);
            }
        });
        jMenu4.add(mnuExportClipboard);
        jMenu4.add(jSeparator12);

        mnuBVList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/document-excel-table.png"))); // NOI18N
        mnuBVList.setText("Full Mech List");
        mnuBVList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBVListActionPerformed(evt);
            }
        });
        jMenu4.add(mnuBVList);

        mnuFactorList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/document-excel-table.png"))); // NOI18N
        mnuFactorList.setText("Factors");
        mnuFactorList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFactorListActionPerformed(evt);
            }
        });
        jMenu4.add(mnuFactorList);

        mnuCurrentList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/document-excel.png"))); // NOI18N
        mnuCurrentList.setText("Current List");
        mnuCurrentList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCurrentListActionPerformed(evt);
            }
        });
        jMenu4.add(mnuCurrentList);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("View");

        jMenuItem2.setText("Select Units List");
        jMenuItem2.setVisible(false);
        jMenu5.add(jMenuItem2);

        jMenu6.setText("Force List");

        rmnuTWModel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        btnGrpViews.add(rmnuTWModel);
        rmnuTWModel.setSelected(true);
        rmnuTWModel.setText("Total Warfare");
        rmnuTWModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmnuTWModelActionPerformed(evt);
            }
        });
        jMenu6.add(rmnuTWModel);

        rmnuBFModel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        btnGrpViews.add(rmnuBFModel);
        rmnuBFModel.setText("BattleForce");
        rmnuBFModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmnuBFModelActionPerformed(evt);
            }
        });
        jMenu6.add(rmnuBFModel);

        rmnuInformation.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        btnGrpViews.add(rmnuInformation);
        rmnuInformation.setText("Information Line");
        rmnuInformation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmnuInformationActionPerformed(evt);
            }
        });
        jMenu6.add(rmnuInformation);

        rmnuFCTModel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        rmnuFCTModel.setText("Balance Factors");
        rmnuFCTModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmnuFCTModelActionPerformed(evt);
            }
        });
        jMenu6.add(rmnuFCTModel);

        jMenu5.add(jMenu6);

        jMenuBar1.add(jMenu5);

        jMenu2.setText("Design");

        mnuDesignBattleMech.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuDesignBattleMech.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/madcat-pencil.png"))); // NOI18N
        mnuDesignBattleMech.setText("BattleMech");
        mnuDesignBattleMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDesignBattleMechActionPerformed(evt);
            }
        });
        jMenu2.add(mnuDesignBattleMech);

        mnuDesignVehicle.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuDesignVehicle.setText("Combat Vehicle");
        mnuDesignVehicle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDesignVehicleActionPerformed(evt);
            }
        });
        jMenu2.add(mnuDesignVehicle);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Battle Armor");
        jMenuItem3.setEnabled(false);
        jMenu2.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Aero/Conv Fighter");
        jMenuItem4.setEnabled(false);
        jMenu2.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Warship/Dropship");
        jMenuItem5.setEnabled(false);
        jMenu2.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("Support Vehicle");
        jMenuItem6.setEnabled(false);
        jMenu2.add(jMenuItem6);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem7.setText("Protomech");
        jMenuItem7.setEnabled(false);
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("About");

        mnuAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        mnuAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/projection-screen.png"))); // NOI18N
        mnuAbout.setText("Battletech Force Balancer");
        mnuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutActionPerformed(evt);
            }
        });
        jMenu3.add(mnuAbout);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblScenarioName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtScenarioName, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkUseForceModifier)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblForceMod)
                        .addGap(425, 425, 425))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblScenarioName)
                    .addComponent(txtScenarioName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUseForceModifier)
                    .addComponent(lblForceMod))
                .addGap(6, 6, 6)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtUnitNameTopKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameTopKeyTyped
    }//GEN-LAST:event_txtUnitNameTopKeyTyped

    private void txtUnitNameBottomKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameBottomKeyTyped
    }//GEN-LAST:event_txtUnitNameBottomKeyTyped

    private void mnuLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLoadActionPerformed
        if ((scenario.getAttackerForce().isDirty) || (scenario.getDefenderForce().isDirty)) {
            switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                case javax.swing.JOptionPane.YES_OPTION:
                    this.mnuSaveActionPerformed(null);
                case javax.swing.JOptionPane.CANCEL_OPTION:
                    return;
            }
        }

        File forceFile = media.SelectFile(Prefs.get("LastOpenFile", ""), "bfb", "Load Force List");

        if (forceFile != null) {
            WaitCursor();

            try {
               loadScenario(forceFile.getCanonicalPath());
            } catch (Exception e) {
               Media.Messager("Issue loading file:\n " + e.getMessage() );
               System.out.println(e.getMessage());
               return;
            }
            
            DefaultCursor();
        }
}//GEN-LAST:event_mnuLoadActionPerformed

    private void lblUnitLogoBottomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblUnitLogoBottomMouseClicked
        updateLogo(lblUnitLogoBottom, scenario.getDefenderForce());
    }//GEN-LAST:event_lblUnitLogoBottomMouseClicked

    private void lblUnitLogoTopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblUnitLogoTopMouseClicked
        updateLogo(lblUnitLogoTop, scenario.getAttackerForce());
    }//GEN-LAST:event_lblUnitLogoTopMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if ((scenario.getAttackerForce().isDirty) || (scenario.getDefenderForce().isDirty)) {
            switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                case javax.swing.JOptionPane.YES_OPTION:
                    this.mnuSaveActionPerformed(null);
                case javax.swing.JOptionPane.NO_OPTION:
                    dOpen.dispose();
                    this.dispose();
                case javax.swing.JOptionPane.CANCEL_OPTION:
                    return;
            }
        } else {
            System.err.flush();
            System.out.flush();
            dOpen.dispose();
            this.dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    private void txtUnitNameTopFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnitNameTopFocusLost
        scenario.getAttackerForce().ForceName = txtUnitNameTop.getText();
    }//GEN-LAST:event_txtUnitNameTopFocusLost

    private void txtUnitNameBottomFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnitNameBottomFocusLost
        scenario.getDefenderForce().ForceName = txtUnitNameBottom.getText();
    }//GEN-LAST:event_txtUnitNameBottomFocusLost

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        formWindowClosing(null);
    }//GEN-LAST:event_mnuExitActionPerformed

    private void mnuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewActionPerformed
        if ((scenario.getAttackerForce().isDirty) || (scenario.getDefenderForce().isDirty)) {
                    switch (javax.swing.JOptionPane.showConfirmDialog(this, "Would you like to save your changes?")) {
                        case javax.swing.JOptionPane.YES_OPTION:
                            this.mnuSaveActionPerformed(null);
                        case javax.swing.JOptionPane.CANCEL_OPTION:
                            return;
                    }
        }

        //Prefs.put("CurrentBFBFile", "");
        CurrentFile = "";
        this.scenario = new Scenario();
        edtAftermath.setText("");
        edtAttacker.setText("");
        edtDefender.setText("");
        edtSpecialRules.setText("");
        edtSetup.setText("");
        edtSituation.setText("");
        edtVictoryConditions.setText("");

        txtTrackCost.setText("");
        lstBonuses.setModel(scenario.getWarchest().getBonusList());
        lstObjectives.setModel(scenario.getWarchest().getObjectiveList());

        txtScenarioName.setText("");
        txtUnitNameTop.setText("");
        txtUnitNameBottom.setText("");
        lblUnitLogoTop.setIcon(null);
        lblUnitLogoBottom.setIcon(null);
        chkUseForceModifier.setSelected(false);

        scenario.setModel(new tbTotalWarfare());
        scenario.AddListener(ForceChanged);

        Refresh();
    }//GEN-LAST:event_mnuNewActionPerformed

    private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveActionPerformed
        //Update fields
        KeyTyped.keyTyped(null);

        WaitCursor();
        try {
            File file;
            if ( !CurrentFile.isEmpty() && scenario.isOverwriteable() ) {
            //if ( !Prefs.get("CurrentBFBFile", "").isEmpty() && scenario.isOverwriteable() ) {
                file = new File(CurrentFile);
                //file = new File(Prefs.get("CurrentBFBFile", ""));
            } else {
                file = media.SelectFile(txtScenarioName.getText() + ".bfb", "bfb", "Save");
                if (file == null) {
                    DefaultCursor();
                    return;
                }
            }
            String filename = file.getCanonicalPath();
            if ( ! filename.endsWith(".bfb") ) { filename += ".bfb";}

            //XMLWriter write = new XMLWriter(txtScenarioName.getText(), this.scenario.getAttackerForce(), this.scenario.getDefenderForce());
            //write.WriteXML(filename);

            XMLWriter writer = new XMLWriter();
            writer.WriteScenario(scenario, filename);
            
            Prefs.put("LastOpenBFBFile", filename);
            //Prefs.put("CurrentBFBFile", filename);
            CurrentFile = filename;
            Media.Messager(this, "Forces saved to " + filename);
        } catch (java.io.IOException e) {
            Media.Messager(e.getMessage());
            System.out.println(e.getMessage());
        }
        DefaultCursor();
    }//GEN-LAST:event_mnuSaveActionPerformed

    private void mnuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutActionPerformed
        dlgAbout About = new dlgAbout();
        About.setTitle("About Battletech Force Balancer");
        About.setLocationRelativeTo(this);
        About.setVisible(true);
}//GEN-LAST:event_mnuAboutActionPerformed

    private void mnuPrintForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintForceActionPerformed
       WaitCursor();
       BFBPrinter printer = new BFBPrinter(images);
       printer.setJobName(this.txtScenarioName.getText());
       printer.setTitle(this.txtScenarioName.getText());
       printer.Print();
       DefaultCursor();
}//GEN-LAST:event_mnuPrintForceActionPerformed

    private void mnuPrintUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintUnitsActionPerformed
        WaitCursor();
        images.preLoadMechImages();

        PagePrinter printer = new PagePrinter();
        for ( Force printForce : scenario.getForces() ) {

            //printer.setLogoPath(printForce.LogoPath);
            printer.setJobName(printForce.ForceName);

            for ( Unit u : printForce.getUnits() ) {
                u.LoadUnit();
                PrintMech pm = new PrintMech(u.m, u.getMechwarrior(), u.getGunnery(), u.getPiloting(),images);
                pm.setLogoImage(images.getImage(printForce.LogoPath));
                printer.Append( BFBPrinter.Letter.toPage(), pm);
            }
        }
        printer.Print();
        DefaultCursor();
    }//GEN-LAST:event_mnuPrintUnitsActionPerformed

    private void mnuDesignBattleMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDesignBattleMechActionPerformed
        String[] call = { "java", "-Xmx256m", "-jar", "ssw.jar" };
        try {
            Runtime.getRuntime().exec(call);
        } catch (Exception ex) {
            Media.Messager("Error while trying to open SSW\n" + ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }//GEN-LAST:event_mnuDesignBattleMechActionPerformed

    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        mnuLoadActionPerformed(evt);
}//GEN-LAST:event_btnLoadActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        mnuNewActionPerformed(evt);
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        mnuSaveActionPerformed(evt);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        //mnuPrintAllActionPerformed(evt);
        mnuPrintActionPerformed(evt);
}//GEN-LAST:event_btnPrintActionPerformed

    private void tblTopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTopMouseClicked
        if ( evt.getClickCount() == 2 ) { editUnit(tblTop, scenario.getAttackerForce()); }
    }//GEN-LAST:event_tblTopMouseClicked

    private void tblBottomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBottomMouseClicked
        if ( evt.getClickCount() == 2 ) { editUnit(tblBottom, scenario.getDefenderForce()); }
    }//GEN-LAST:event_tblBottomMouseClicked

    private void btnMULExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMULExportActionPerformed
        WaitCursor();
        MULWriter mw = new MULWriter();
        String dir = "";
        dir = media.GetDirectorySelection(this, Prefs.get("MULDirectory", ""));
        if ( dir.isEmpty() ) { 
            DefaultCursor();
            return;
        }

        Prefs.put("MULDirectory", dir);
        mw.setForce(scenario.getAttackerForce());
        try {
            mw.Write( dir + File.separator + scenario.getAttackerForce().ForceName );
        } catch (IOException ex) {
            Media.Messager("Unable to save " + scenario.getAttackerForce().ForceName + "\n" + ex.getMessage() );
            System.out.println(ex.getMessage());
        }

        mw.setForce(scenario.getDefenderForce());
        try {
            mw.Write( dir + File.separator + scenario.getDefenderForce().ForceName );
        } catch ( IOException ex ) {
            Media.Messager("Unable to save " + scenario.getDefenderForce().ForceName + "\n" + ex.getMessage() );
            System.out.println(ex.getMessage());
        }

        Media.Messager(this, "Your forces have been exported to " + dir);
        DefaultCursor();
}//GEN-LAST:event_btnMULExportActionPerformed

    private void chkUseForceModifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseForceModifierActionPerformed
        //lblForceMod.setVisible( chkUseForceModifier.isSelected() );
        scenario.updateOpFor(chkUseForceModifier.isSelected());
        updateFields();
    }//GEN-LAST:event_chkUseForceModifierActionPerformed

    private void btnOpenBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenBottomActionPerformed
        openForce( scenario.getDefenderForce() );
    }//GEN-LAST:event_btnOpenBottomActionPerformed

    private void btnSaveBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveBottomActionPerformed
        scenario.getDefenderForce().ForceName = txtUnitNameBottom.getText();
        saveForce( scenario.getDefenderForce() );
    }//GEN-LAST:event_btnSaveBottomActionPerformed

    private void btnOpenTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenTopActionPerformed
        openForce( scenario.getAttackerForce() );
}//GEN-LAST:event_btnOpenTopActionPerformed

    private void btnSaveTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveTopActionPerformed
        scenario.getAttackerForce().ForceName = txtUnitNameTop.getText();
        saveForce( scenario.getAttackerForce() );
}//GEN-LAST:event_btnSaveTopActionPerformed

    private void btnAddBottom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBottom1ActionPerformed
        if ( scenario.getDefenderForce().ForceName.isEmpty() )  scenario.getDefenderForce().ForceName = "Secondary";
        OpenDialog(scenario.getDefenderForce());
    }//GEN-LAST:event_btnAddBottom1ActionPerformed

    private void btnAddTop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTop1ActionPerformed
        if ( scenario.getAttackerForce().ForceName.isEmpty() )  scenario.getAttackerForce().ForceName = "Primary";
        OpenDialog(scenario.getAttackerForce());
    }//GEN-LAST:event_btnAddTop1ActionPerformed

    private void btnEditTop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditTop1ActionPerformed
        editUnit(tblTop, scenario.getAttackerForce());
    }//GEN-LAST:event_btnEditTop1ActionPerformed

    private void btnEditBottom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditBottom1ActionPerformed
        editUnit(tblBottom, scenario.getDefenderForce());
    }//GEN-LAST:event_btnEditBottom1ActionPerformed

    private void btnDeleteTop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteTop1ActionPerformed
        removeUnits( tblTop, scenario.getAttackerForce() );
    }//GEN-LAST:event_btnDeleteTop1ActionPerformed

    private void btnDeleteBottom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteBottom1ActionPerformed
        removeUnits( tblBottom, scenario.getDefenderForce() );
    }//GEN-LAST:event_btnDeleteBottom1ActionPerformed

    private void btnClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardActionPerformed
        toClipboard( new Force[]{ scenario.getAttackerForce(), scenario.getDefenderForce() } );
    }//GEN-LAST:event_btnClipboardActionPerformed

    private void btnClipboardTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardTopActionPerformed
        toClipboardSingle( scenario.getAttackerForce() );
    }//GEN-LAST:event_btnClipboardTopActionPerformed

    private void btnClipboardBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboardBottomActionPerformed
        toClipboardSingle( scenario.getDefenderForce() );
    }//GEN-LAST:event_btnClipboardBottomActionPerformed

    private void mnuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveAsActionPerformed
        if ( txtScenarioName.getText().isEmpty() ) {
            Media.Messager("Please enter a scenario name before saving.");
            return;
        }

        if ( !scenario.getAttackerForce().isSaveable() || !scenario.getDefenderForce().isSaveable() ) {
            Media.Messager("Please enter a force name and at least one unit in each list before saving.");
            return;
        }

        WaitCursor();
        try {
            File file;
            file = media.SelectFile("", "bfb", "Save");
            if (file == null) {
                return;
            }

            String filename = file.getCanonicalPath();
            if ( ! filename.endsWith(".bfb") ) { filename += ".bfb";}

            XMLWriter writer = new XMLWriter();
            writer.WriteScenario(scenario, filename);

            //XMLWriter write = new XMLWriter(txtScenarioName.getText(), this.scenario.getAttackerForce(), this.scenario.getDefenderForce());
            //write.WriteXML(filename);

            Prefs.put("LastOpenBFBFile", filename);
            //Prefs.put("CurrentBFBFile", filename);
            CurrentFile = filename;
            Media.Messager(this, "Forces saved to " + filename);
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
            Media.Messager(e.getMessage());
        }
        DefaultCursor();
}//GEN-LAST:event_mnuSaveAsActionPerformed

    private void mnuExportClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportClipboardActionPerformed
        toClipboard( new Force[]{ scenario.getAttackerForce(), scenario.getDefenderForce() } );
    }//GEN-LAST:event_mnuExportClipboardActionPerformed

    private void mnuExportMULActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportMULActionPerformed
        btnMULExportActionPerformed(evt);
    }//GEN-LAST:event_mnuExportMULActionPerformed

    private void mnuExportTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportTextActionPerformed
        WaitCursor();
        TXTWriter txtWrite = new TXTWriter( scenario.getForces() );
        File filename = media.SelectFile(Prefs.get("TXTDirectory", ""), "txt", "Save");
        if ( filename == null ) { return; }

        try {
            txtWrite.Write(filename.getCanonicalPath());

            Prefs.put("TXTDirectory", filename.getCanonicalPath());
            Media.Messager("Your forces have been exported to " + filename.getCanonicalPath());
        } catch (IOException ex) {
            //do nothing
            System.out.println(ex.getMessage());
            Media.Messager("Unable to save \n" + ex.getMessage() );
        }
        DefaultCursor();
    }//GEN-LAST:event_mnuExportTextActionPerformed

    private void txtTopGunKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTopGunKeyReleased
        if ( !txtTopGun.getText().isEmpty() && !txtTopPilot.getText().isEmpty() ) {
            overrideSkill( scenario.getAttackerForce(), Integer.parseInt(txtTopGun.getText()), Integer.parseInt(txtTopPilot.getText()) );
        }
}//GEN-LAST:event_txtTopGunKeyReleased

    private void txtTopGunFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTopGunFocusGained
        txtTopGun.selectAll();
    }//GEN-LAST:event_txtTopGunFocusGained

    private void txtTopPilotKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTopPilotKeyReleased
        if ( !txtTopGun.getText().isEmpty() && !txtTopPilot.getText().isEmpty() ) {
            overrideSkill( scenario.getAttackerForce(), Integer.parseInt(txtTopGun.getText()), Integer.parseInt(txtTopPilot.getText()) );
        }
    }//GEN-LAST:event_txtTopPilotKeyReleased

    private void txtTopPilotFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTopPilotFocusGained
        txtTopPilot.selectAll();
    }//GEN-LAST:event_txtTopPilotFocusGained

    private void txtBottomGunFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBottomGunFocusGained
        txtBottomGun.selectAll();
}//GEN-LAST:event_txtBottomGunFocusGained

    private void txtBottomGunKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBottomGunKeyReleased
        if ( !txtBottomGun.getText().isEmpty() && !txtBottomPilot.getText().isEmpty() ) {
            overrideSkill( scenario.getDefenderForce(), Integer.parseInt(txtBottomGun.getText()), Integer.parseInt(txtBottomPilot.getText()) );
        }
}//GEN-LAST:event_txtBottomGunKeyReleased

    private void txtBottomPilotFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBottomPilotFocusGained
        txtBottomPilot.selectAll();
}//GEN-LAST:event_txtBottomPilotFocusGained

    private void txtBottomPilotKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBottomPilotKeyReleased
        if ( !txtBottomGun.getText().isEmpty() && !txtBottomPilot.getText().isEmpty() ) {
            overrideSkill( scenario.getDefenderForce(), Integer.parseInt(txtBottomGun.getText()), Integer.parseInt(txtBottomPilot.getText()) );
        }
}//GEN-LAST:event_txtBottomPilotKeyReleased

    private void btnManageImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManageImagesActionPerformed
        WaitCursor();
        dlgImageMgr img = new dlgImageMgr(this, scenario.getForces(), images);
        if ( img.hasWork ) {
            img.setLocationRelativeTo(this);
            img.setVisible(true);
        }
        DefaultCursor();
    }//GEN-LAST:event_btnManageImagesActionPerformed

    private void mnuPrintDlgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintDlgActionPerformed
        dlgPrint print = new dlgPrint(this, true, scenario, images);
        print.setLocationRelativeTo(this);
        print.setVisible(true);
}//GEN-LAST:event_mnuPrintDlgActionPerformed

    private void btnPersonnelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPersonnelActionPerformed
        dlgPersonnel ppl = new dlgPersonnel(this, false);
        ppl.setLocationRelativeTo(this);
        ppl.setVisible(true);
    }//GEN-LAST:event_btnPersonnelActionPerformed

    private void mnuPrintRSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintRSActionPerformed
        WaitCursor();
        images.preLoadMechImages();

        PagePrinter printer = new PagePrinter();
        for ( Force printForce : scenario.getForces() ) {
            printer.setJobName(printForce.ForceName);

            for ( Unit u : printForce.getUnits() ) {
                u.LoadUnit();
                PrintMech pm = new PrintMech(u.m, u.getMechwarrior(), u.getGunnery(), u.getPiloting(),images);
                pm.setCanon(true);
                pm.setCharts(false);
                pm.SetMiniConversion(1);
                pm.setPrintPilot(false);
                pm.setTRO(true);

                printer.Append( BFBPrinter.Letter.toPage(), pm);
            }
        }
        printer.Print();
        DefaultCursor();
    }//GEN-LAST:event_mnuPrintRSActionPerformed

    private void txtUnitNameTopKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameTopKeyReleased
        scenario.getAttackerForce().ForceName = txtUnitNameTop.getText();
    }//GEN-LAST:event_txtUnitNameTopKeyReleased

    private void txtUnitNameBottomKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitNameBottomKeyReleased
        scenario.getDefenderForce().ForceName = txtUnitNameBottom.getText();
    }//GEN-LAST:event_txtUnitNameBottomKeyReleased

    private void tblTopKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTopKeyReleased
        if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
            btnDeleteTop1ActionPerformed(null);
        }
    }//GEN-LAST:event_tblTopKeyReleased

    private void tblBottomKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblBottomKeyReleased
        if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
            btnDeleteBottom1ActionPerformed(null);
        }
    }//GEN-LAST:event_tblBottomKeyReleased

    private void btnBalanceTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBalanceTopActionPerformed
        balanceSkills( tblTop, scenario.getAttackerForce() );
    }//GEN-LAST:event_btnBalanceTopActionPerformed

    private void btnBalanceBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBalanceBottomActionPerformed
        balanceSkills( tblBottom, scenario.getDefenderForce() );
    }//GEN-LAST:event_btnBalanceBottomActionPerformed

    private void btnSwitchTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSwitchTopActionPerformed
        switchUnits( tblTop, scenario.getAttackerForce(), scenario.getDefenderForce() );
    }//GEN-LAST:event_btnSwitchTopActionPerformed

    private void btnSwitchBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSwitchBottomActionPerformed
        switchUnits( tblBottom, scenario.getDefenderForce(), scenario.getAttackerForce() );
    }//GEN-LAST:event_btnSwitchBottomActionPerformed

    private void btnISTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnISTopActionPerformed
        scenario.getAttackerForce().setType(BattleForce.InnerSphere);
    }//GEN-LAST:event_btnISTopActionPerformed

    private void btnCLTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCLTopActionPerformed
        scenario.getAttackerForce().setType(BattleForce.Clan);
    }//GEN-LAST:event_btnCLTopActionPerformed

    private void btnCSTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCSTopActionPerformed
        scenario.getAttackerForce().setType(BattleForce.Comstar);
    }//GEN-LAST:event_btnCSTopActionPerformed

    private void btnISBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnISBottomActionPerformed
        scenario.getDefenderForce().setType(BattleForce.InnerSphere);
    }//GEN-LAST:event_btnISBottomActionPerformed

    private void btnCLBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCLBottomActionPerformed
        scenario.getDefenderForce().setType(BattleForce.Clan);
    }//GEN-LAST:event_btnCLBottomActionPerformed

    private void btnCSBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCSBottomActionPerformed
        scenario.getDefenderForce().setType(BattleForce.Comstar);
    }//GEN-LAST:event_btnCSBottomActionPerformed

    private void mnuPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintActionPerformed
        WaitCursor();
        scenario.getAttackerForce().clearEmptyGroups();
        scenario.getDefenderForce().clearEmptyGroups();
        PagePrinter printer = SetupPrinter();
        dlgPreview prv = new dlgPreview("Print Preview", this, printer, scenario, images);
        prv.setLocationRelativeTo(this);
        prv.setVisible(true);
        DefaultCursor();
    }//GEN-LAST:event_mnuPrintActionPerformed

    private void mnuBVListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBVListActionPerformed
        //Media.Messager("This will output a csv list of mechs and also a list of EVERY SINGLE Mech's cost and BV2 calculation!");
        WaitCursor();
        if ( list == null ) { LoadList(true); }
        
        TXTWriter out = new TXTWriter();
        String dir = "";
        dir = media.GetDirectorySelection(this, Prefs.get("ListDirectory", ""));
        if ( dir.isEmpty() ) { 
            DefaultCursor();
            return;
        }

        Prefs.put("ListDirectory", dir);
        try {
            out.WriteList(dir + File.separator + "MechListing.csv", list);
            Media.Messager("Mech List output to " + dir);
        } catch (IOException ex) {
            //do nothing
            System.out.println(ex.getMessage());
            Media.Messager("Unable to output list\n" + ex.getMessage() );
        }
        DefaultCursor();
    }//GEN-LAST:event_mnuBVListActionPerformed

    private void rmnuTWModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmnuTWModelActionPerformed
        WaitCursor();
        scenario.setModel(new tbTotalWarfare());
        scenario.AddListener(ForceChanged);
        Refresh();
        DefaultCursor();
    }//GEN-LAST:event_rmnuTWModelActionPerformed

    private void rmnuBFModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmnuBFModelActionPerformed
        WaitCursor();
        scenario.setModel(new tbBattleForce());
        scenario.AddListener(ForceChanged);
        Refresh();
        DefaultCursor();
    }//GEN-LAST:event_rmnuBFModelActionPerformed

    private void btnAddObjectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddObjectiveActionPerformed
        if ( !txtReward.getText().isEmpty() && !txtObjective.getText().isEmpty() ) {
            scenario.getWarchest().getObjectives().add(new Objective(txtObjective.getText(), Integer.parseInt(txtReward.getText().replace(",", ""))));
            txtReward.setText("");
            txtObjective.setText("");
            lstObjectives.setModel(scenario.getWarchest().getObjectiveList());
            scenario.MakeDirty(true);
        }
    }//GEN-LAST:event_btnAddObjectiveActionPerformed

    private void rmnuInformationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmnuInformationActionPerformed
        WaitCursor();
        scenario.setModel(new tbChatInfo());
        scenario.AddListener(ForceChanged);
        Refresh();
        DefaultCursor();
}//GEN-LAST:event_rmnuInformationActionPerformed

    private void btnAddBonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBonusActionPerformed
        if ( !txtAmount.getText().isEmpty() && !txtBonus.getText().isEmpty() ) {
            scenario.getWarchest().getBonuses().add(new Bonus(txtBonus.getText(), Integer.parseInt(txtAmount.getText())));
            txtAmount.setText("");
            txtBonus.setText("");
            lstBonuses.setModel(scenario.getWarchest().getBonusList());
            scenario.MakeDirty(true);
        }
    }//GEN-LAST:event_btnAddBonusActionPerformed

    private void lstObjectivesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstObjectivesKeyReleased
        if ( lstObjectives.getSelectedValues().length > 0 ) {
            if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
                Object[] rows = lstObjectives.getSelectedValues();
                for ( int i=0; i < rows.length; i++ ) {
                    scenario.getWarchest().getObjectives().remove(rows[i]);
                }
                lstObjectives.setModel(scenario.getWarchest().getObjectiveList());
            }
        }
    }//GEN-LAST:event_lstObjectivesKeyReleased

    private void lstBonusesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstBonusesKeyReleased
        if ( lstBonuses.getSelectedValues().length > 0 ) {
            if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
                Object[] rows = lstBonuses.getSelectedValues();
                for ( int i=0; i < rows.length; i++ ) {
                    scenario.getWarchest().getBonuses().remove(rows[i]);
                }
                lstBonuses.setModel(scenario.getWarchest().getObjectiveList());
            }
        }
    }//GEN-LAST:event_lstBonusesKeyReleased

    private void btnGroupTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGroupTopActionPerformed
        ManageGroup(  );
    }//GEN-LAST:event_btnGroupTopActionPerformed

    private void cmbC3TopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbC3TopActionPerformed
        ChangeC3( scenario.getAttackerForce(), cmbC3Top );
    }//GEN-LAST:event_cmbC3TopActionPerformed

    private void cmbC3BottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbC3BottomActionPerformed
        ChangeC3( scenario.getDefenderForce(), cmbC3Bottom );
    }//GEN-LAST:event_cmbC3BottomActionPerformed

    private void mnuSaveScenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveScenarioActionPerformed
        scenario.setOverwriteable(false);
        mnuSaveAsActionPerformed(evt);
    }//GEN-LAST:event_mnuSaveScenarioActionPerformed

    private void btnClearImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearImagesActionPerformed
        MechWriter writer = new MechWriter();

        if ( Media.Options(this, "Are you sure you want to remove all pre-selected images from the units in this list?", "Confirm Image Removal") == Media.OK) {
            WaitCursor();
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
            DefaultCursor();
            Media.Messager("BFB.Images have been removed from all units in the list.");
        }
    }//GEN-LAST:event_btnClearImagesActionPerformed

    private void txtTrackCostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTrackCostActionPerformed
        try {
            int val = Integer.parseInt(txtTrackCost.getText().replace(",",""));
            scenario.getWarchest().setTrackCost(val);
        } catch ( Exception e ) {
            Media.Messager("You must enter a valid value for the Track Cost");
        }
    }//GEN-LAST:event_txtTrackCostActionPerformed

    private void txtTrackCostKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTrackCostKeyReleased
        txtTrackCostActionPerformed(null);
    }//GEN-LAST:event_txtTrackCostKeyReleased

    private void btnQuickAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickAddActionPerformed
        OpenQuickAdd(scenario.getAttackerForce());
    }//GEN-LAST:event_btnQuickAddActionPerformed

    private void btnQuickAddBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickAddBottomActionPerformed
        OpenQuickAdd(scenario.getDefenderForce());
}//GEN-LAST:event_btnQuickAddBottomActionPerformed

    private void tblTopMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTopMouseReleased
        if( evt.isPopupTrigger() ) {
            clearPopupActions(popGroup);
            clearPopupActions(popSkill);
            clearPopupActions(popName);
            popGroup.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SetGroup( tblTop, scenario.getAttackerForce() );
                }
            });
            popSkill.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    balanceSkills( tblTop, scenario.getAttackerForce() );
                }
            });
            popName.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    NameGenerator gen = new NameGenerator();
                    boolean overwrite = true;
                    for ( int i : tblTop.getSelectedRows() ) {
                        Unit u = (Unit) scenario.getAttackerForce().getUnits().get(tblTop.convertRowIndexToModel(i));
                        if ( u.getMechwarrior().isEmpty() || overwrite ) u.setMechwarrior(gen.SimpleGenerate());
                        u.Refresh();
                    }
                    scenario.getAttackerForce().isDirty = true;
                    scenario.getAttackerForce().RefreshBV();
                }
            });
            popUtilities.show( evt.getComponent(), evt.getX(), evt.getY() );
        }
    }//GEN-LAST:event_tblTopMouseReleased

    private void tblBottomMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBottomMouseReleased
        if( evt.isPopupTrigger() ) {
            clearPopupActions(popGroup);
            clearPopupActions(popSkill);
            clearPopupActions(popName);
            popGroup.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SetGroup( tblBottom, scenario.getDefenderForce() );
                }
            });
            popSkill.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    balanceSkills( tblBottom, scenario.getDefenderForce() );
                }
            });
            popName.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    NameGenerator gen = new NameGenerator();
                    boolean overwrite = true;
                    for ( int i : tblBottom.getSelectedRows() ) {
                        Unit u = (Unit) scenario.getDefenderForce().getUnits().get(tblBottom.convertRowIndexToModel(i));
                        if ( u.getMechwarrior().isEmpty() || overwrite ) u.setMechwarrior(gen.SimpleGenerate());
                        u.Refresh();
                    }
                    scenario.getDefenderForce().isDirty = true;
                    scenario.getDefenderForce().RefreshBV();
                }
            });
            popUtilities.show( evt.getComponent(), evt.getX(), evt.getY() );
        }
    }//GEN-LAST:event_tblBottomMouseReleased

    private void rmnuFCTModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmnuFCTModelActionPerformed
        WaitCursor();
        scenario.setFactors();
        scenario.setModel(new tbFactors());
        scenario.AddListener(ForceChanged);
        Refresh();
        DefaultCursor();
    }//GEN-LAST:event_rmnuFCTModelActionPerformed

    private void mnuFactorListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFactorListActionPerformed
        WaitCursor();
        TXTWriter out = new TXTWriter();
        String dir = "";
        dir = media.GetDirectorySelection(this, Prefs.get("ListDirectory", ""));
        if ( dir.isEmpty() ) {
            DefaultCursor();
            return;
        }

        Prefs.put("ListDirectory", dir);
        try {
            scenario.setFactors();
            out.WriteFactorList(dir + File.separator + "FactorList.csv", scenario);
            Media.Messager("Force Factors output to " + dir);
        } catch (IOException ex) {
            //do nothing
            System.out.println(ex.getMessage());
            Media.Messager("Unable to output factors\n" + ex.getMessage() );
        }
        DefaultCursor();
}//GEN-LAST:event_mnuFactorListActionPerformed

    private void btnAddGenericActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddGenericActionPerformed
        dlgGenericUnit dlgGen = new dlgGenericUnit(this, true, scenario.getAttackerForce());
        dlgGen.setLocationRelativeTo(this);
        dlgGen.setVisible(true);
}//GEN-LAST:event_btnAddGenericActionPerformed

    private void btnAddGeneric1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddGeneric1ActionPerformed
        dlgGenericUnit dlgGen = new dlgGenericUnit(this, true, scenario.getDefenderForce());
        dlgGen.setLocationRelativeTo(this);
        dlgGen.setVisible(true);
    }//GEN-LAST:event_btnAddGeneric1ActionPerformed
    
    private void setTooltip(UnitListData data) {
        spnMechTable.setToolTipText(data.getInfo());
        txtInfo.setText(data.getInfo());
    }

    private void mnuCurrentListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCurrentListActionPerformed
        WaitCursor();
        TXTWriter out = new TXTWriter();
        String dir = "";
        dir = media.GetDirectorySelection(this, Prefs.get("ListDirectory", ""));
        if ( dir.isEmpty() ) {
            DefaultCursor();
            return;
        }

        Prefs.put("ListDirectory", dir);
        try {
            scenario.setFactors();
            out.WriteList(dir + File.separator + FileCommon.GetSafeFilename(scenario.getName()) + ".csv", scenario);
            Media.Messager("Force Factors output to " + dir);
        } catch (IOException ex) {
            //do nothing
            System.out.println(ex.getMessage());
            Media.Messager("Unable to output factors\n" + ex.getMessage() );
        }
        DefaultCursor();
    }//GEN-LAST:event_mnuCurrentListActionPerformed

    private void btnFilterFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterFilter
        ListFilter filters = new ListFilter();

        if (cmbTech.getSelectedIndex() > 0) {filters.setTech(cmbTech.getSelectedItem().toString());}
        if (cmbEra.getSelectedIndex() > 0) {filters.setEra(cmbEra.getSelectedItem().toString());}
        if (cmbType.getSelectedIndex() > 0) {filters.setType(cmbType.getSelectedItem().toString());}
        if (cmbMotive.getSelectedIndex() > 0) {filters.setMotive(cmbMotive.getSelectedItem().toString());}
        if (cmbRulesLevel.getSelectedIndex() > 0) {filters.setLevel(cmbRulesLevel.getSelectedItem().toString());}
        if (cmbMinMP.getSelectedIndex() > 0) {filters.setMinMP(Integer.parseInt(cmbMinMP.getSelectedItem().toString()));}
        if (! txtMinBV.getText().isEmpty() ) {
            if ( txtMaxBV.getText().isEmpty() ) {
                filters.setBV(0, Integer.parseInt(txtMinBV.getText()));
            } else {
                filters.setBV(Integer.parseInt(txtMinBV.getText()), Integer.parseInt(txtMaxBV.getText()));
            }
        }
        if (! txtMinCost.getText().isEmpty() ) {
            if ( txtMaxCost.getText().isEmpty() ) {
                filters.setCost(0, Double.parseDouble(txtMinCost.getText()));
            } else {
                filters.setCost(Double.parseDouble(txtMinCost.getText()), Double.parseDouble(txtMaxCost.getText()));
            }
        }
        if (! txtMinTon.getText().isEmpty() ) {
            if ( txtMaxTon.getText().isEmpty() ) {
                filters.setTonnage(20, Integer.parseInt(txtMinTon.getText() ));
            } else {
                filters.setTonnage(Integer.parseInt( txtMinTon.getText() ), Integer.parseInt( txtMaxTon.getText() ));
            }
        }
        if (! txtMinYear.getText().isEmpty() ) {
            if ( txtMaxYear.getText().isEmpty() ) {
                filters.setYear(Integer.parseInt(txtMinYear.getText()), Integer.parseInt(txtMinYear.getText()));
            } else {
                filters.setYear(Integer.parseInt(txtMinYear.getText()), Integer.parseInt(txtMaxYear.getText()));
            }
        }
        if (! txtName.getText().isEmpty() ) { filters.setName(txtName.getText().trim()); }
        if (! txtSource.getText().isEmpty() ) { filters.setSource(txtSource.getText().trim()); }
        filters.setIsOmni(chkOmni.isSelected());

        UnitList filtered = list.Filter(filters);
        setupList(filtered, false);
}//GEN-LAST:event_btnFilterFilter

    private void setupList(UnitList mechList, boolean forceSort) {
        currentView.list = mechList;
        tblMechData.setModel(currentView);
        currentView.setupTable(tblMechData);

        lblStatus.setText("Showing " + mechList.Size() + " of " + list.Size());
    }

    private void Filter(java.awt.event.ActionEvent evt) {
        ListFilter filters = new ListFilter();

        if (cmbTech.getSelectedIndex() > 0) {filters.setTech(cmbTech.getSelectedItem().toString());}
        if (cmbEra.getSelectedIndex() > 0) {filters.setEra(cmbEra.getSelectedItem().toString());}
        if (cmbType.getSelectedIndex() > 0) {filters.setType(cmbType.getSelectedItem().toString());}
        if (cmbMotive.getSelectedIndex() > 0) {filters.setMotive(cmbMotive.getSelectedItem().toString());}
        if (cmbRulesLevel.getSelectedIndex() > 0) {filters.setLevel(cmbRulesLevel.getSelectedItem().toString());}
        if (cmbMinMP.getSelectedIndex() > 0) {filters.setMinMP(Integer.parseInt(cmbMinMP.getSelectedItem().toString()));}
        if (cmbUnitType.getSelectedIndex() > 0) {filters.setUnitType(cmbUnitType.getSelectedIndex()-1);}
        if (! txtMinBV.getText().isEmpty() ) {
            if ( txtMaxBV.getText().isEmpty() ) {
                filters.setBV(0, Integer.parseInt(txtMinBV.getText()));
            } else {
                filters.setBV(Integer.parseInt(txtMinBV.getText()), Integer.parseInt(txtMaxBV.getText()));
            }
        }
        if (! txtMinCost.getText().isEmpty() ) {
            if ( txtMaxCost.getText().isEmpty() ) {
                filters.setCost(0, Double.parseDouble(txtMinCost.getText()));
            } else {
                filters.setCost(Double.parseDouble(txtMinCost.getText()), Double.parseDouble(txtMaxCost.getText()));
            }
        }
        if (! txtMinTon.getText().isEmpty() ) {
            if ( txtMaxTon.getText().isEmpty() ) {
                filters.setTonnage(20, Integer.parseInt(txtMinTon.getText() ));
            } else {
                filters.setTonnage(Integer.parseInt( txtMinTon.getText() ), Integer.parseInt( txtMaxTon.getText() ));
            }
        }
        if (! txtMinYear.getText().isEmpty() ) {
            if ( txtMaxYear.getText().isEmpty() ) {
                filters.setYear(Integer.parseInt(txtMinYear.getText()), Integer.parseInt(txtMinYear.getText()));
            } else {
                filters.setYear(Integer.parseInt(txtMinYear.getText()), Integer.parseInt(txtMaxYear.getText()));
            }
        }
        if (! txtName.getText().isEmpty() ) { filters.setName(txtName.getText().trim()); }
        if (! txtSource.getText().isEmpty() ) { filters.setSource(txtSource.getText().trim()); }
        filters.setIsOmni(chkOmni.isSelected());

        filtered = list.Filter(filters);
        setupList(filtered, false);
    }

    private void btnClearFilterFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFilterFilter
        setupList(list, false);

        //clear the dropdowns
        cmbEra.setSelectedIndex(0);
        cmbMotive.setSelectedIndex(0);
        cmbRulesLevel.setSelectedIndex(0);
        cmbTech.setSelectedIndex(0);
        cmbType.setSelectedIndex(0);
        cmbMinMP.setSelectedIndex(0);

        //clear text fields
        txtMinBV.setText("");
        txtMaxBV.setText("");
        txtMinCost.setText("");
        txtMaxCost.setText("");
        txtMinTon.setText("");
        txtMaxTon.setText("");
        txtMinYear.setText("");
        txtMaxYear.setText("");
        txtName.setText("");
        chkOmni.setSelected(false);
}//GEN-LAST:event_btnClearFilterFilter

    private void cmbMinMPFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMinMPFilter
        Filter(null);
}//GEN-LAST:event_cmbMinMPFilter

    private void cmbEraFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEraFilter
        ListFilter filters = new ListFilter();

        if (cmbTech.getSelectedIndex() > 0) {filters.setTech(cmbTech.getSelectedItem().toString());}
        if (cmbEra.getSelectedIndex() > 0) {filters.setEra(cmbEra.getSelectedItem().toString());}
        if (cmbType.getSelectedIndex() > 0) {filters.setType(cmbType.getSelectedItem().toString());}
        if (cmbMotive.getSelectedIndex() > 0) {filters.setMotive(cmbMotive.getSelectedItem().toString());}
        if (cmbRulesLevel.getSelectedIndex() > 0) {filters.setLevel(cmbRulesLevel.getSelectedItem().toString());}
        if (cmbMinMP.getSelectedIndex() > 0) {filters.setMinMP(Integer.parseInt(cmbMinMP.getSelectedItem().toString()));}
        if (! txtMinBV.getText().isEmpty() ) {
            if ( txtMaxBV.getText().isEmpty() ) {
                filters.setBV(0, Integer.parseInt(txtMinBV.getText()));
            } else {
                filters.setBV(Integer.parseInt(txtMinBV.getText()), Integer.parseInt(txtMaxBV.getText()));
            }
        }
        if (! txtMinCost.getText().isEmpty() ) {
            if ( txtMaxCost.getText().isEmpty() ) {
                filters.setCost(0, Double.parseDouble(txtMinCost.getText()));
            } else {
                filters.setCost(Double.parseDouble(txtMinCost.getText()), Double.parseDouble(txtMaxCost.getText()));
            }
        }
        if (! txtMinTon.getText().isEmpty() ) {
            if ( txtMaxTon.getText().isEmpty() ) {
                filters.setTonnage(20, Integer.parseInt(txtMinTon.getText() ));
            } else {
                filters.setTonnage(Integer.parseInt( txtMinTon.getText() ), Integer.parseInt( txtMaxTon.getText() ));
            }
        }
        if (! txtMinYear.getText().isEmpty() ) {
            if ( txtMaxYear.getText().isEmpty() ) {
                filters.setYear(Integer.parseInt(txtMinYear.getText()), Integer.parseInt(txtMinYear.getText()));
            } else {
                filters.setYear(Integer.parseInt(txtMinYear.getText()), Integer.parseInt(txtMaxYear.getText()));
            }
        }
        if (! txtName.getText().isEmpty() ) { filters.setName(txtName.getText().trim()); }
        if (! txtSource.getText().isEmpty() ) { filters.setSource(txtSource.getText().trim()); }
        filters.setIsOmni(chkOmni.isSelected());

        UnitList filtered = list.Filter(filters);
        setupList(filtered, false);
}//GEN-LAST:event_cmbEraFilter

    private void cmbMotiveFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMotiveFilter
        Filter(null);
}//GEN-LAST:event_cmbMotiveFilter

    private void cmbTechFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTechFilter
        ListFilter filters = new ListFilter();

        if (cmbTech.getSelectedIndex() > 0) {filters.setTech(cmbTech.getSelectedItem().toString());}
        if (cmbEra.getSelectedIndex() > 0) {filters.setEra(cmbEra.getSelectedItem().toString());}
        if (cmbType.getSelectedIndex() > 0) {filters.setType(cmbType.getSelectedItem().toString());}
        if (cmbMotive.getSelectedIndex() > 0) {filters.setMotive(cmbMotive.getSelectedItem().toString());}
        if (cmbRulesLevel.getSelectedIndex() > 0) {filters.setLevel(cmbRulesLevel.getSelectedItem().toString());}
        if (cmbMinMP.getSelectedIndex() > 0) {filters.setMinMP(Integer.parseInt(cmbMinMP.getSelectedItem().toString()));}
        if (! txtMinBV.getText().isEmpty() ) {
            if ( txtMaxBV.getText().isEmpty() ) {
                filters.setBV(0, Integer.parseInt(txtMinBV.getText()));
            } else {
                filters.setBV(Integer.parseInt(txtMinBV.getText()), Integer.parseInt(txtMaxBV.getText()));
            }
        }
        if (! txtMinCost.getText().isEmpty() ) {
            if ( txtMaxCost.getText().isEmpty() ) {
                filters.setCost(0, Double.parseDouble(txtMinCost.getText()));
            } else {
                filters.setCost(Double.parseDouble(txtMinCost.getText()), Double.parseDouble(txtMaxCost.getText()));
            }
        }
        if (! txtMinTon.getText().isEmpty() ) {
            if ( txtMaxTon.getText().isEmpty() ) {
                filters.setTonnage(20, Integer.parseInt(txtMinTon.getText() ));
            } else {
                filters.setTonnage(Integer.parseInt( txtMinTon.getText() ), Integer.parseInt( txtMaxTon.getText() ));
            }
        }
        if (! txtMinYear.getText().isEmpty() ) {
            if ( txtMaxYear.getText().isEmpty() ) {
                filters.setYear(Integer.parseInt(txtMinYear.getText()), Integer.parseInt(txtMinYear.getText()));
            } else {
                filters.setYear(Integer.parseInt(txtMinYear.getText()), Integer.parseInt(txtMaxYear.getText()));
            }
        }
        if (! txtName.getText().isEmpty() ) { filters.setName(txtName.getText().trim()); }
        if (! txtSource.getText().isEmpty() ) { filters.setSource(txtSource.getText().trim()); }
        filters.setIsOmni(chkOmni.isSelected());

        UnitList filtered = list.Filter(filters);
        setupList(filtered, false);
}//GEN-LAST:event_cmbTechFilter

    private void cmbRulesLevelFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRulesLevelFilter
        Filter(null);
}//GEN-LAST:event_cmbRulesLevelFilter

    private void cmbTypeFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTypeFilter
        Filter(null);
}//GEN-LAST:event_cmbTypeFilter

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        Filter(null);
}//GEN-LAST:event_txtNameActionPerformed

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased
        Filter(null);
}//GEN-LAST:event_txtNameKeyReleased

    private void txtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyTyped
        //Filter(null);
}//GEN-LAST:event_txtNameKeyTyped

    private void txtSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSourceActionPerformed
        Filter(null);
}//GEN-LAST:event_txtSourceActionPerformed

    private void txtSourceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSourceKeyReleased
        Filter(null);
}//GEN-LAST:event_txtSourceKeyReleased

    private void txtMaxBVFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxBVFilter
        ListFilter filters = new ListFilter();

        if (cmbTech.getSelectedIndex() > 0) {filters.setTech(cmbTech.getSelectedItem().toString());}
        if (cmbEra.getSelectedIndex() > 0) {filters.setEra(cmbEra.getSelectedItem().toString());}
        if (cmbType.getSelectedIndex() > 0) {filters.setType(cmbType.getSelectedItem().toString());}
        if (cmbMotive.getSelectedIndex() > 0) {filters.setMotive(cmbMotive.getSelectedItem().toString());}
        if (cmbRulesLevel.getSelectedIndex() > 0) {filters.setLevel(cmbRulesLevel.getSelectedItem().toString());}
        if (cmbMinMP.getSelectedIndex() > 0) {filters.setMinMP(Integer.parseInt(cmbMinMP.getSelectedItem().toString()));}
        if (! txtMinBV.getText().isEmpty() ) {
            if ( txtMaxBV.getText().isEmpty() ) {
                filters.setBV(0, Integer.parseInt(txtMinBV.getText()));
            } else {
                filters.setBV(Integer.parseInt(txtMinBV.getText()), Integer.parseInt(txtMaxBV.getText()));
            }
        }
        if (! txtMinCost.getText().isEmpty() ) {
            if ( txtMaxCost.getText().isEmpty() ) {
                filters.setCost(0, Double.parseDouble(txtMinCost.getText()));
            } else {
                filters.setCost(Double.parseDouble(txtMinCost.getText()), Double.parseDouble(txtMaxCost.getText()));
            }
        }
        if (! txtMinTon.getText().isEmpty() ) {
            if ( txtMaxTon.getText().isEmpty() ) {
                filters.setTonnage(20, Integer.parseInt(txtMinTon.getText() ));
            } else {
                filters.setTonnage(Integer.parseInt( txtMinTon.getText() ), Integer.parseInt( txtMaxTon.getText() ));
            }
        }
        if (! txtMinYear.getText().isEmpty() ) {
            if ( txtMaxYear.getText().isEmpty() ) {
                filters.setYear(Integer.parseInt(txtMinYear.getText()), Integer.parseInt(txtMinYear.getText()));
            } else {
                filters.setYear(Integer.parseInt(txtMinYear.getText()), Integer.parseInt(txtMaxYear.getText()));
            }
        }
        if (! txtName.getText().isEmpty() ) { filters.setName(txtName.getText().trim()); }
        if (! txtSource.getText().isEmpty() ) { filters.setSource(txtSource.getText().trim()); }
        filters.setIsOmni(chkOmni.isSelected());

        UnitList filtered = list.Filter(filters);
        setupList(filtered, false);
}//GEN-LAST:event_txtMaxBVFilter

    private void txtMaxBVFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxBVFocusLost
        Filter(null);
}//GEN-LAST:event_txtMaxBVFocusLost

    private void txtMinBVFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinBVFocusLost
        if ( txtMaxBV.getText().isEmpty() ) { txtMaxBV.setText(txtMinBV.getText()); txtMaxBV.selectAll(); }
        Filter(null);
}//GEN-LAST:event_txtMinBVFocusLost

    private void txtMinYearFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinYearFocusLost
        if ( txtMaxYear.getText().isEmpty() ) { txtMaxYear.setText(txtMinYear.getText()); txtMaxYear.selectAll();}
        Filter(null);
}//GEN-LAST:event_txtMinYearFocusLost

    private void txtMaxYearFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxYearFilter
        // TODO add your handling code here:
}//GEN-LAST:event_txtMaxYearFilter

    private void txtMaxYearFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxYearFocusLost
        Filter(null);
}//GEN-LAST:event_txtMaxYearFocusLost

    private void txtMaxTonFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxTonFilter
        Filter(null);
}//GEN-LAST:event_txtMaxTonFilter

    private void txtMaxTonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxTonFocusLost
        Filter(null);
}//GEN-LAST:event_txtMaxTonFocusLost

    private void txtMinTonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinTonFocusLost
        if ( txtMaxTon.getText().isEmpty() ) { txtMaxTon.setText(txtMinTon.getText()); txtMaxTon.selectAll();}
        Filter(null);
}//GEN-LAST:event_txtMinTonFocusLost

    private void txtMinCostFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMinCostFilter
        // TODO add your handling code here:
}//GEN-LAST:event_txtMinCostFilter

    private void txtMinCostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinCostFocusLost
        if ( txtMaxCost.getText().isEmpty() ) { txtMaxCost.setText(txtMinCost.getText()); txtMaxCost.selectAll();}
        Filter(null);
}//GEN-LAST:event_txtMinCostFocusLost

    private void txtMaxCostFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxCostFilter
        // TODO add your handling code here:
}//GEN-LAST:event_txtMaxCostFilter

    private void txtMaxCostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxCostFocusLost
        Filter(null);
}//GEN-LAST:event_txtMaxCostFocusLost

    private void chkOmniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOmniActionPerformed
        Filter(null);
}//GEN-LAST:event_chkOmniActionPerformed

    private void tblMechDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMechDataMouseClicked
        if (evt.getClickCount() == 2) {
            addChosen();
        }
}//GEN-LAST:event_tblMechDataMouseClicked

    private void addChosen() {
        if ( tblMechData.getSelectedRows().length > 0 ) {
            int[] Rows = tblMechData.getSelectedRows();
            for (int i = 0; i < Rows.length; i++) {
                Unit u = new Unit(((abView) tblMechData.getModel()).list.Get(tblMechData.convertRowIndexToModel(Rows[i])));
                addToForce.AddUnit(u);
                lblStatusUpdate.setText(u.getFullName() + " Added to " + addToForce.ForceName);
                addToForce.RefreshBV();
            }
            Refresh();
        } else if (tblMechData.getRowCount() == 1) {
            Unit u = new Unit( ((abView) tblMechData.getModel()).list.Get(0) );
            addToForce.AddUnit(u);
            lblStatusUpdate.setText(u.getFullName() + " Added to " + addToForce.ForceName);
            addToForce.RefreshBV();
        }
    }
    private void tblMechDataMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMechDataMouseMoved
        setTooltip((UnitListData) ((abView) tblMechData.getModel()).Get(tblMechData.convertRowIndexToModel(tblMechData.rowAtPoint(evt.getPoint()))));
}//GEN-LAST:event_tblMechDataMouseMoved

    private void tblMechDataFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblMechDataFocusGained
        // TODO add your handling code here:
}//GEN-LAST:event_tblMechDataFocusGained

    private void tblMechDataKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblMechDataKeyReleased
        pnlSelectKeyReleased(evt);
}//GEN-LAST:event_tblMechDataKeyReleased

    private void spnMechTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spnMechTableKeyReleased
        pnlSelectKeyReleased(evt);
}//GEN-LAST:event_spnMechTableKeyReleased

    private void btnOpenMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenMechActionPerformed
        addChosen();
        jTabbedPane1.setSelectedComponent(jPanel1);
}//GEN-LAST:event_btnOpenMechActionPerformed

    private void btnOpenDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenDirActionPerformed
        Prefs.put("ListPath", media.GetDirectorySelection(this, MechListPath));
        LoadList(false);
}//GEN-LAST:event_btnOpenDirActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        LoadList(false);
        Filter(evt);
}//GEN-LAST:event_btnRefreshActionPerformed

    private void pnlSelectKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pnlSelectKeyReleased
        String entered = txtName.getText();

        switch (evt.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
                if (entered.trim().length() > 0) {
                    entered = entered.substring(0, entered.length() - 1);
                } else {
                    entered = "";
                }
                break;
            case KeyEvent.VK_DELETE:
                entered = "";
                break;
            case KeyEvent.VK_ENTER:
                if (((UnitList) tblMechData.getModel()).Size() == 1) {
                    tblMechData.selectAll();
                    addChosen();
                }
                break;
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_LEFT:
                return;
            default:
                if ((evt.getKeyCode() == 32) || (evt.getKeyCode() >= 45 && evt.getKeyCode() <= 90)) {
                    entered += evt.getKeyChar();
                }
        }
        txtName.setText(entered);
        txtNameKeyReleased(evt);
            // TODO add your handling code here:
    }//GEN-LAST:event_pnlSelectKeyReleased

    private void spnMechTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spnMechTableMouseClicked
        addChosen();
    }//GEN-LAST:event_spnMechTableMouseClicked

    private void lstFilesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstFilesValueChanged
        DirectoryLeaf f = (DirectoryLeaf) lstFiles.getSelectedValue();
        //String filename = lstFiles.getSelectedValue().toString();
        RUSReader reader = new RUSReader();
        try {
            String Path = f.getPath(); // BaseRUSPath + File.separator + RUSDirectory + File.separator + filename;
            //Media.Messager(this, Path);
            reader.Load(Path, rus);
            lstOptions.setModel(rus.getDisplay());
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }//GEN-LAST:event_lstFilesValueChanged

    private void btnRollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRollActionPerformed
        lstSelected.setModel(rus.Generate(Integer.parseInt(spnSelections.getValue().toString()), Integer.parseInt(spnAddOn.getValue().toString())));
}//GEN-LAST:event_btnRollActionPerformed

    private void lstOptionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstOptionsMouseClicked
        if (evt.getClickCount() == 2) {
            String Name = ((Object) lstOptions.getSelectedValue()).toString();
            if (!Name.isEmpty()) {
                Name = Name.split(",")[0];
            }
            //lstSelected.setModel(rus.Add(Name));
            lstOptions.clearSelection();
        }
}//GEN-LAST:event_lstOptionsMouseClicked

    private void lstOptionsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstOptionsValueChanged

}//GEN-LAST:event_lstOptionsValueChanged

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        switch(jList1.getSelectedIndex()) {
            case 0:
                txtMinTon.setText("20");
                txtMaxTon.setText("35");
                break;
            case 1:
                txtMinTon.setText("40");
                txtMaxTon.setText("55");
                break;
            case 2:
                txtMinTon.setText("60");
                txtMaxTon.setText("75");
                break;
            case 3:
                txtMinTon.setText("80");
                txtMaxTon.setText("100");
                break;
        }
        jList1.clearSelection();
        Filter(null);
    }//GEN-LAST:event_jList1ValueChanged

    private void lstSelectedValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstSelectedValueChanged
        if ((lstSelected.getSelectedValues().length > 0)) {
            String Item = ((Object) lstSelected.getSelectedValues()[0]).toString();
            if (Item.contains(" ")) {
                String Name = RUS.ParseDesignName(Item);
                txtName.setText(Name);
                Filter(null);

                if (filtered.Size() == 0) {
                    txtName.setText(Name.split(" ")[0]);
                    Filter(null);
                }

                if ( filtered.Size() == 1 ) {
                    tblMechData.selectAll();
                    addChosen();
                    tblMechData.clearSelection();
                    lstSelected.clearSelection();
                    lblRndStatus.setText(filtered.Get(0).getFullName() + " Added");
                } else {
                    jTabbedPane1.setSelectedComponent(pnlSelect);
                    //tbpSelections.setSelectedComponent(pnlUnits);
                }
            }
        }
}//GEN-LAST:event_lstSelectedValueChanged

    private void lstSelectedKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstSelectedKeyPressed
        if (lstSelected.getSelectedValues().length > 0) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                DefaultListModel model = (DefaultListModel) lstSelected.getModel();
                for (int i = lstSelected.getSelectedValues().length - 1; i >= 0; i--) {
                    model.removeElement((Object) lstSelected.getSelectedValues()[i]);
                }
                lstSelected.clearSelection();
            }
        }
}//GEN-LAST:event_lstSelectedKeyPressed

    private void btnClearSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearSelectionActionPerformed
        lstSelected.setModel(rus.ClearSelection());
}//GEN-LAST:event_btnClearSelectionActionPerformed

    private void btnClipboard1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipboard1ActionPerformed
        String data = "";

        for (int i = 0; i < lstSelected.getModel().getSize(); i++) {
            if (!data.isEmpty()) {
                data += BFB.Common.Constants.NL;
            }
            data += lstSelected.getModel().getElementAt(i).toString();
        }

        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection(data);
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(export, this);
}//GEN-LAST:event_btnClipboard1ActionPerformed

    private void jTabbedPane1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTabbedPane1KeyReleased
        String entered = txtName.getText();

        switch (evt.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
                if (entered.trim().length() > 0) {
                    entered = entered.substring(0, entered.length() - 1);
                } else {
                    entered = "";
                }
                break;
            case KeyEvent.VK_DELETE:
                entered = "";
                break;
            case KeyEvent.VK_ENTER:
                if (((UnitList) tblMechData.getModel()).Size() == 1) {
                    tblMechData.selectAll();
                    addChosen();
                }
                break;
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_CONTROL:
                return;
            default:
                if ((evt.getKeyCode() == 32) || (evt.getKeyCode() >= 45 && evt.getKeyCode() <= 90)) {
                    entered += evt.getKeyChar();
                }
        }
        txtName.setText(entered);
        txtNameKeyReleased(evt);
    }//GEN-LAST:event_jTabbedPane1KeyReleased

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        pnlSelect.requestFocus();
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void lstObjectivesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstObjectivesValueChanged
        Objective o = (Objective) lstObjectives.getSelectedValue();
        scenario.getWarchest().getObjectives().remove(o);
        txtReward.setText(o.getValue()+"");
        txtObjective.setText(o.getDescription());
    }//GEN-LAST:event_lstObjectivesValueChanged

    private void lstBonusesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstBonusesValueChanged
        Bonus b = (Bonus) lstBonuses.getSelectedValue();
        scenario.getWarchest().getBonuses().remove(b);
        txtAmount.setText(b.getValue()+"");
        txtBonus.setText(b.getDescription());
    }//GEN-LAST:event_lstBonusesValueChanged

    private void txtRewardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRewardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRewardActionPerformed

    private void cmbUnitTypeFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbUnitTypeFilter
        Filter(evt);
    }//GEN-LAST:event_cmbUnitTypeFilter

    private void mnuDesignVehicleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDesignVehicleActionPerformed
        String[] call = { "java", "-jar", "saw.jar" };
        try {
            Runtime.getRuntime().exec(call);
        } catch (Exception ex) {
            Media.Messager("Error while trying to open SAW\n" + ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }//GEN-LAST:event_mnuDesignVehicleActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        LoadList(true);
        setupList(list, true);
    }//GEN-LAST:event_formWindowOpened

    public void LoadList(boolean UseIndex) {
        if (MechListPath.isEmpty()) {
            if (MechListPath.isEmpty() && this.isVisible()) {
                MechListPath = media.GetDirectorySelection(null, "", "Select the location of the Master Files.");
                Prefs.put("ListPath", MechListPath);
            }
        }

        if (!MechListPath.isEmpty()) {

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            list = new UnitList(MechListPath, UseIndex);

            if (list.Size() > 0) {
                setupList(list, false);
            }

            String displayPath = MechListPath;
            if (!MechListPath.isEmpty()) {
                if (MechListPath.contains(File.separator)) {
                    displayPath = MechListPath.substring(0, 3) + "..." + MechListPath.substring(MechListPath.lastIndexOf(File.separator)) + "";
                }
            }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            media.Messager("No path to Mechs selected!");
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBonus;
    private javax.swing.JButton btnAddBottom1;
    private javax.swing.JButton btnAddGeneric;
    private javax.swing.JButton btnAddGeneric1;
    private javax.swing.JButton btnAddObjective;
    private javax.swing.JButton btnAddTop1;
    private javax.swing.JButton btnBalanceBottom;
    private javax.swing.JButton btnBalanceTop;
    private javax.swing.JRadioButton btnCLBottom;
    private javax.swing.JRadioButton btnCLTop;
    private javax.swing.JRadioButton btnCSBottom;
    private javax.swing.JRadioButton btnCSTop;
    private javax.swing.JButton btnClearFilter;
    private javax.swing.JButton btnClearImages;
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JButton btnClipboard;
    private javax.swing.JButton btnClipboard1;
    private javax.swing.JButton btnClipboardBottom;
    private javax.swing.JButton btnClipboardTop;
    private javax.swing.JButton btnDeleteBottom1;
    private javax.swing.JButton btnDeleteTop1;
    private javax.swing.JButton btnEditBottom1;
    private javax.swing.JButton btnEditTop1;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnGroupTop;
    private javax.swing.ButtonGroup btnGrpBottom;
    private javax.swing.ButtonGroup btnGrpTop;
    private javax.swing.ButtonGroup btnGrpViews;
    private javax.swing.JRadioButton btnISBottom;
    private javax.swing.JRadioButton btnISTop;
    private javax.swing.JButton btnLoad;
    private javax.swing.JButton btnMULExport;
    private javax.swing.JButton btnManageImages;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnOpenBottom;
    private javax.swing.JButton btnOpenDir;
    private javax.swing.JButton btnOpenMech;
    private javax.swing.JButton btnOpenTop;
    private javax.swing.JButton btnPersonnel;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnQuickAdd;
    private javax.swing.JButton btnQuickAddBottom;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRoll;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveBottom;
    private javax.swing.JButton btnSaveTop;
    private javax.swing.JButton btnSwitchBottom;
    private javax.swing.JButton btnSwitchTop;
    private javax.swing.JCheckBox chkOmni;
    private javax.swing.JCheckBox chkUseForceModifier;
    private javax.swing.JComboBox cmbC3Bottom;
    private javax.swing.JComboBox cmbC3Top;
    private javax.swing.JComboBox cmbEra;
    private javax.swing.JComboBox cmbMinMP;
    private javax.swing.JComboBox cmbMotive;
    private javax.swing.JComboBox cmbRulesLevel;
    private javax.swing.JComboBox cmbTech;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JComboBox cmbUnitType;
    private javax.swing.JTextPane edtAftermath;
    private javax.swing.JTextPane edtAttacker;
    private javax.swing.JTextPane edtDefender;
    private javax.swing.JTextPane edtSetup;
    private javax.swing.JTextPane edtSituation;
    private javax.swing.JTextPane edtSpecialRules;
    private javax.swing.JTextPane edtVictoryConditions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblBV;
    private javax.swing.JLabel lblBaseBVBottom;
    private javax.swing.JLabel lblBaseBVTop;
    private javax.swing.JLabel lblCost;
    private javax.swing.JLabel lblEra;
    private javax.swing.JLabel lblForceMod;
    private javax.swing.JLabel lblLevel;
    private javax.swing.JLabel lblMinMP;
    private javax.swing.JLabel lblMotive;
    private javax.swing.JLabel lblMotive1;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblRndStatus;
    private javax.swing.JLabel lblScenarioName;
    private javax.swing.JLabel lblSource;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStatusUpdate;
    private javax.swing.JLabel lblTech;
    private javax.swing.JLabel lblTonnage;
    private javax.swing.JLabel lblTonnage1;
    private javax.swing.JLabel lblTonnageBottom;
    private javax.swing.JLabel lblTonnageTop;
    private javax.swing.JLabel lblTotalBVBottom;
    private javax.swing.JLabel lblTotalBVTop;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblUnitLogoBottom;
    private javax.swing.JLabel lblUnitLogoTop;
    private javax.swing.JLabel lblUnitNameBottom;
    private javax.swing.JLabel lblUnitNameTop;
    private javax.swing.JLabel lblUnitsBottom;
    private javax.swing.JLabel lblUnitsTop;
    private javax.swing.JList lstBonuses;
    private javax.swing.JList lstFiles;
    private javax.swing.JList lstObjectives;
    private javax.swing.JList lstOptions;
    private javax.swing.JList lstSelected;
    private javax.swing.JMenuItem mnuAbout;
    private javax.swing.JMenuItem mnuBVList;
    private javax.swing.JMenuItem mnuCurrentList;
    private javax.swing.JMenuItem mnuDesignBattleMech;
    private javax.swing.JMenuItem mnuDesignVehicle;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenuItem mnuExportClipboard;
    private javax.swing.JMenuItem mnuExportMUL;
    private javax.swing.JMenuItem mnuExportText;
    private javax.swing.JMenuItem mnuFactorList;
    private javax.swing.JMenuItem mnuLoad;
    private javax.swing.JMenuItem mnuNew;
    private javax.swing.JMenu mnuPrint;
    private javax.swing.JMenuItem mnuPrintDlg;
    private javax.swing.JMenuItem mnuPrintForce;
    private javax.swing.JMenuItem mnuPrintRS;
    private javax.swing.JMenuItem mnuPrintUnits;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JMenuItem mnuSaveAs;
    private javax.swing.JMenuItem mnuSaveScenario;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JPanel pnlRandomSelection;
    private javax.swing.JPanel pnlSelect;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JRadioButtonMenuItem rmnuBFModel;
    private javax.swing.JMenuItem rmnuFCTModel;
    private javax.swing.JRadioButtonMenuItem rmnuInformation;
    private javax.swing.JRadioButtonMenuItem rmnuTWModel;
    private javax.swing.JSpinner spnAddOn;
    private javax.swing.JScrollPane spnBottom;
    private javax.swing.JScrollPane spnMechTable;
    private javax.swing.JSpinner spnSelections;
    private javax.swing.JScrollPane spnTop;
    private javax.swing.JTable tblBottom;
    private javax.swing.JTable tblMechData;
    private javax.swing.JTable tblTop;
    private javax.swing.JToolBar tlbBottom;
    private javax.swing.JToolBar tlbTop;
    private javax.swing.JTree treDirectories;
    private javax.swing.JFormattedTextField txtAmount;
    private javax.swing.JTextField txtBonus;
    private javax.swing.JTextField txtBottomGun;
    private javax.swing.JTextField txtBottomPilot;
    private javax.swing.JLabel txtInfo;
    private javax.swing.JTextField txtMaxBV;
    private javax.swing.JTextField txtMaxCost;
    private javax.swing.JTextField txtMaxTon;
    private javax.swing.JTextField txtMaxYear;
    private javax.swing.JTextField txtMinBV;
    private javax.swing.JTextField txtMinCost;
    private javax.swing.JTextField txtMinTon;
    private javax.swing.JTextField txtMinYear;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtObjective;
    private javax.swing.JFormattedTextField txtReward;
    private javax.swing.JTextField txtScenarioName;
    private javax.swing.JTextField txtSource;
    private javax.swing.JTextField txtTopGun;
    private javax.swing.JTextField txtTopPilot;
    private javax.swing.JFormattedTextField txtTrackCost;
    private javax.swing.JTextField txtUnitNameBottom;
    private javax.swing.JTextField txtUnitNameTop;
    // End of variables declaration//GEN-END:variables

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //do nothing
    }

    /**
     * @return the images
     */
    public ImageTracker getImageTracker() {
        return images;
    }

}

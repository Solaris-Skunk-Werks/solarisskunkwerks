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

import Force.Unit;
import Print.BFBPrinter;
import Print.PagePrinter;
import Print.PrintVehicle;
import components.CombatVehicle;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingWorker;
import filehandlers.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import list.*;
import list.view.*;

public class dlgOpen extends javax.swing.JFrame implements PropertyChangeListener {
    private common.DesignForm parent;
    private UnitList list = new UnitList();
    private Media media = new Media();
    private String dirPath = "";
    private String NL = "";
    private String msg = "";
    private abView currentView = new tbTotalWarfareView(list);
    private boolean cancelledListDirSelection = false;

    public int Requestor = SSW;
    public static final int SSW = 0,
                            FORCE = 1;

    /** Creates new form dlgOpen */
    public dlgOpen(java.awt.Frame parent, boolean modal) {
        initComponents();
        //ImageIcon icon = new ImageIcon(super.getClass().getResource("/ssw/Images/appicon.png"));
        //super.setIconImage(icon.getImage());
        this.parent = (common.DesignForm) parent;
        
        prgResaving.setVisible(false);
        cmbTech.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Tech", "Clan", "Inner Sphere", "Mixed" }));
        cmbEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Era", "Age of War/Star League", "Succession Wars", "Clan Invasion", "Dark Ages", "All Eras (non-canon)" }));
        cmbRulesLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Level", "Introductory", "Tournament Legal", "Advanced Rules", "Experimental Tech", "Era Specific" }));
        cmbMotive.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Motive", "Hovercraft", "Naval (Displacement)", "Naval (Hydrofoil)", "Naval (Submarine)", "Tracked", "VTOL", "Wheeled", "WiGE" }));
        NL = System.getProperty( "line.separator" );
    }
    
    private void LoadMech() {
        switch ( Requestor ) {
            case SSW:
                LoadMechIntoSSW();
                break;

            case FORCE:
                btnAdd2ForceActionPerformed(null);
                this.setVisible(false);
                parent.GetForceDialogue().setVisible(true);
                break;

            default:
                LoadMechIntoSSW();
        }
    }

    private void LoadMechIntoSSW() {
        UnitListData Data = (UnitListData)((abView) tblMechData.getModel()).Get( tblMechData.convertRowIndexToModel( tblMechData.getSelectedRow() ) );
        try
        {
            CVReader read = new CVReader();
            CombatVehicle m = read.ReadUnit( list.getDirectory() + Data.getFilename(), parent.GetData() );
            if (Data.isOmni()) {
                m.SetCurLoadout( Data.getConfig() );
            }
            ArrayList units = new ArrayList();
            units.add(m);
            parent.setUnit( units );

            parent.GetPrefs().put( "LastOpenCVDirectory", list.getDirectory() + Data.getFilename().substring( 0, Data.getFilename().lastIndexOf( File.separator ) + 1 ) );
            parent.GetPrefs().put( "LastOpenCVFile", Data.getFilename().substring( Data.getFilename().lastIndexOf( File.separator ) + 1 ) );

            tblMechData.clearSelection();
            //setupList(list, false);
            this.setVisible(false);

        } catch ( Exception e ) {
            Media.Messager( (javax.swing.JFrame) parent, e.getMessage() );
        }
    }

    private void Calculate() {
        txtSelected.setText("0 Units Selected for 0 BV and 0 C-Bills");

        int BV = 0;
        double Cost = 0;

        int[] rows = tblMechData.getSelectedRows();
        for ( int i=0; i < rows.length; i++ ) {
            UnitListData data = (UnitListData)((abView) tblMechData.getModel()).Get( tblMechData.convertRowIndexToModel( rows[i] ) );
            BV += data.getBV();
            Cost += data.getCost();
        }

        txtSelected.setText(rows.length + " Units Selected for " + String.format("%,d", BV) + " BV and " + String.format("%,.2f", Cost) + " C-Bills");
    }

    public void LoadList() {
        LoadList(true);
    }

    public void LoadList(boolean useIndex) {
        this.lblStatus.setText("Loading Units...");
        this.txtSelected.setText("0 Units Selected for 0 BV and 0 C-Bills");
        this.tblMechData.setModel(new UnitList());
        
        if (dirPath.isEmpty()) {
            dirPath = parent.GetPrefs().get("ListPath", parent.GetPrefs().get( "LastOpenCVDirectory", "" ) );

            if ( dirPath.isEmpty() && this.isVisible() && !cancelledListDirSelection ) {
                dlgFiles dFiles = new dlgFiles(this, true);
                dFiles.setLocationRelativeTo(this);
                dFiles.setVisible(true);
                if ( dFiles.result ) {
                    dirPath = media.GetDirectorySelection(this, "", "Select SAW File Directory");
                    parent.GetPrefs().put("ListPath", dirPath);
                    if ( dirPath.isEmpty() ) {
                        cancelledListDirSelection = true;
                        this.setVisible( false );
                    }
                } else {
                    cancelledListDirSelection = true;
                    this.setVisible( false );
                }
                dFiles.dispose();
            }
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        list = new UnitList(dirPath, useIndex);

        if (list.Size() > 0) {
            setupList(list, true);
        }

        String displayPath = dirPath;
        this.lblStatus.setText(list.Size() + " Units loaded from " + displayPath);
        this.lblStatus.setToolTipText(dirPath);
        spnMechTable.getVerticalScrollBar().setValue(0);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void setupList(UnitList mechList, boolean forceSort) {
        ListFilter fileFilter = new ListFilter();
        fileFilter.setExtension(".saw");
        currentView.list = mechList.Filter(fileFilter);
        tblMechData.setModel(currentView);
        currentView.setupTable(tblMechData);
        //tblMechData.setModel(mechList);

        lblShowing.setText("Showing " + currentView.list.Size() + " of " + list.Size());

    }

    private void checkSelection() {
        if ( tblMechData.getSelectedRowCount() > 0 ) {
            Calculate();
            btnOpen.setEnabled(true);
            btnPrint.setEnabled(true);
            btnAdd2Force.setEnabled(true);
        } else {
            btnOpen.setEnabled(false);
            btnPrint.setEnabled(false);
            btnAdd2Force.setEnabled(false);
            txtSelected.setText("0 Units Selected for 0 BV and 0 C-Bills");
        }
    }

    private void batchUpdateMechs() {
        //String msg = "";
        prgResaving.setValue(0);
        prgResaving.setVisible(true);
        int Response = javax.swing.JOptionPane.showConfirmDialog(this, "This will open and re-save each file in the current directory so that all files are updated with current BV and Cost calculations.\nThis process could take a few minutes, are you ready?", "Batch Unit Processing", javax.swing.JOptionPane.YES_NO_OPTION);
        if (Response == javax.swing.JOptionPane.YES_OPTION) {
            msg = "";
            setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR ) );
            try {
                Resaver Saving = new Resaver( this );
                Saving.addPropertyChangeListener( this );
                Saving.execute();
            } catch( Exception e ) {
                // fatal error.  let the user know
                Media.Messager( this, "A fatal error occured while processing the Units:\n" + e.getMessage() );
                e.printStackTrace();
            }
        } else {
            prgResaving.setVisible(false);
        }
    }

    private void setTooltip( UnitListData data ) {
        //spnMechTable.setToolTipText( data.getInfo() );
        try {
            String[] dirs = data.getFilename().split("\\\\");
            String shortPath = "";
            if ( dirs.length > 3) {
                for (int i = dirs.length-1; i >= dirs.length-3; i--) {
                    shortPath = "\\" + dirs[i] + shortPath;
                }
            }
            txtSelected.setText(data.getInfo() + " (" + shortPath + ")");
        } catch ( Exception e ) {
            //do nothing
        }
    }
    
    public void propertyChange( PropertyChangeEvent e ) {
       prgResaving.setValue( ((Resaver) e.getSource()).getProgress() );
    }

    private class Resaver extends SwingWorker<Void,Void> {
        dlgOpen Owner;
        int filesUpdated = 0,
            totalFileCount = 0;

        public Resaver( dlgOpen owner ) {
            Owner = owner;
        }

        @Override
        public void done() {
            setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            if( msg.length() > 0 ) {
                dlgTextExport Message = new dlgTextExport( Owner, true, msg );
                Message.setLocationRelativeTo( Owner );
                Message.setVisible( true );
            } else {
                Media.Messager( Owner, filesUpdated + " Files Updated.  Reloading list next." );
            }
            prgResaving.setVisible(false);
            prgResaving.setValue( 0 );
            LoadList(false);
        }

        @Override
        protected Void doInBackground() throws Exception {
            CVReader read = new CVReader();
            CVWriter writer = new CVWriter();

            File FileList = new File(dirPath);
            try {
                processDir( FileList, read, writer );
            } catch ( IOException ie ) {
                System.out.println(ie.getMessage());
                throw new Exception(msg);
            } catch ( Exception e ) {
                throw e;
            }

            return null;
        }

        private void processDir( File directory, CVReader read, CVWriter writer ) throws IOException {
            File[] files = directory.listFiles();
            totalFileCount += files.length;
            for ( int i=0; i < files.length; i++ ) {
                if ( files[i].isFile() && files[i].getCanonicalPath().endsWith(".saw") ) {
                    processFile( files[i], read, writer );
                    int progress = ((int) (( ((double) filesUpdated + 1) / (double) totalFileCount ) * 100.0 ) );
                    setProgress( progress );
                } else if ( files[i].isDirectory() ) {
                    processDir( files[i], read, writer );
                }
            }
        }

        private void processFile( File file, CVReader read, CVWriter writer ) throws IOException {
            try {
                CombatVehicle m = read.ReadUnit( file.getCanonicalPath(), parent.GetData() );

                // save the mech to XML in the current location
                writer.setUnit(m);
                try {
                    writer.WriteXML( file.getCanonicalPath() );
                    filesUpdated += 1;
                } catch( IOException e ) {
                    msg += "Could not load the following file(s):" + NL;
                    msg += file.getCanonicalPath() + NL + NL;
                }
            } catch ( Exception e ) {
                // log the error
                msg += file.getCanonicalPath() + NL;
                if( e.getMessage() == null ) {
                    StackTraceElement[] trace = e.getStackTrace();
                    for( int j = 0; j < trace.length; j++ ) {
                        msg += trace[j].toString() + NL;
                    }
                    msg += NL;
                } else {
                    msg += e.getMessage() + NL + NL;
                }
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

        txtSelected = new javax.swing.JLabel();
        tlbActions = new javax.swing.JToolBar();
        btnOpen = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnChangeDir = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnAdd2Force = new javax.swing.JButton();
        btnViewForce = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnOptions = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnMagic = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        lblForce = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        cmbView = new javax.swing.JComboBox();
        spnMechTable = new javax.swing.JScrollPane();
        tblMechData = new javax.swing.JTable();
        pnlFilters = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnFilter = new javax.swing.JButton();
        btnClearFilter = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        lblMinMP = new javax.swing.JLabel();
        cmbMinMP = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        cmbEra = new javax.swing.JComboBox();
        lblEra = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lblMotive = new javax.swing.JLabel();
        cmbMotive = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        cmbTech = new javax.swing.JComboBox();
        lblTech = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblLevel = new javax.swing.JLabel();
        cmbRulesLevel = new javax.swing.JComboBox();
        jPanel15 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        txtSource = new javax.swing.JTextField();
        lblSource = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        txtMaxBV = new javax.swing.JTextField();
        lblBV = new javax.swing.JLabel();
        txtMinBV = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        lblTonnage1 = new javax.swing.JLabel();
        txtMinYear = new javax.swing.JTextField();
        txtMaxYear = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        lblTonnage = new javax.swing.JLabel();
        txtMaxTon = new javax.swing.JTextField();
        txtMinTon = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        txtMinCost = new javax.swing.JTextField();
        lblCost = new javax.swing.JLabel();
        txtMaxCost = new javax.swing.JTextField();
        chkOmni = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        prgResaving = new javax.swing.JProgressBar();
        lblShowing = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Unit(s)");
        setMinimumSize(new java.awt.Dimension(600, 500));
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        txtSelected.setText("0 Units Selected");

        tlbActions.setFloatable(false);
        tlbActions.setRollover(true);

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/folder-open-document.png"))); // NOI18N
        btnOpen.setToolTipText("Open Vehicle");
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
        tlbActions.add(jSeparator4);

        btnChangeDir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/folders.png"))); // NOI18N
        btnChangeDir.setToolTipText("Change Directory");
        btnChangeDir.setFocusable(false);
        btnChangeDir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnChangeDir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnChangeDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeDirActionPerformed(evt);
            }
        });
        tlbActions.add(btnChangeDir);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/printer.png"))); // NOI18N
        btnPrint.setToolTipText("Print Selected Vehicles");
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

        btnAdd2Force.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/clipboard--plus.png"))); // NOI18N
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

        btnViewForce.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/clipboard.png"))); // NOI18N
        btnViewForce.setToolTipText("View Force");
        btnViewForce.setFocusable(false);
        btnViewForce.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnViewForce.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewForce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewForceActionPerformed(evt);
            }
        });
        tlbActions.add(btnViewForce);

        btnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/document--arrow.png"))); // NOI18N
        btnExport.setToolTipText("Export List to CSV");
        btnExport.setFocusable(false);
        btnExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        tlbActions.add(btnExport);
        tlbActions.add(jSeparator1);

        btnOptions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/gear.png"))); // NOI18N
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

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/arrow-circle-double.png"))); // NOI18N
        btnRefresh.setToolTipText("Refresh List");
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

        btnMagic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/wand.png"))); // NOI18N
        btnMagic.setToolTipText("Update Unit Files (Long Process!)");
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
        tlbActions.add(lblForce);
        tlbActions.add(jSeparator5);

        cmbView.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Total Warfare Standard", "Total Warfare Compact", "BattleForce Information", "Chat Information" }));
        cmbView.setFocusable(false);
        cmbView.setMaximumSize(new java.awt.Dimension(139, 20));
        cmbView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbViewActionPerformed(evt);
            }
        });
        tlbActions.add(cmbView);

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
        tblMechData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblMechDataKeyReleased(evt);
            }
        });
        spnMechTable.setViewportView(tblMechData);

        pnlFilters.setBorder(javax.swing.BorderFactory.createTitledBorder("Filters"));

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        btnClearFilter.setText("Clear");
        btnClearFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFilterFilter(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnFilter)
            .addComponent(btnClearFilter)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
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

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMinMP)
            .addComponent(cmbMinMP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(lblMinMP)
                .addGap(1, 1, 1)
                .addComponent(cmbMinMP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        cmbEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Era", "Age of War/Star League", "Succession Wars", "Clan Invasion", "Dark Ages", "All Eras (non-canon)" }));
        cmbEra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        lblEra.setText("Era");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbEra, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblEra)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lblEra)
                .addGap(1, 1, 1)
                .addComponent(cmbEra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lblMotive.setText("Motive Type");

        cmbMotive.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Motive", "Hover", "Naval (Displacement)", "Naval (Hydrofoil)", "Naval (Submarine)", "Tracked", "Wheeled", "WiGE" }));
        cmbMotive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMotiveFilter(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblMotive)
            .addComponent(cmbMotive, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(lblMotive)
                .addGap(1, 1, 1)
                .addComponent(cmbMotive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        cmbTech.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Any Tech", "Clan", "Inner Sphere", "Mixed" }));
        cmbTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
            }
        });

        lblTech.setText("Technology");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbTech, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblTech)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmbRulesLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblLevel)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(lblLevel)
                .addGap(1, 1, 1)
                .addComponent(cmbRulesLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(134, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                .addGap(21, 21, 21))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblName)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
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

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtSource, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblSource)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(lblSource)
                .addGap(4, 4, 4)
                .addComponent(txtSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        txtMaxBV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Filter(evt);
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

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(txtMinBV, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaxBV, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblBV)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtMaxBV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtMinBV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
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

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTonnage1)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(txtMinYear, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaxYear, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(lblTonnage1)
                .addGap(4, 4, 4)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(txtMinTon, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaxTon, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblTonnage)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtMaxTon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtMinTon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
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

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(txtMinCost, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaxCost, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(lblCost)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(lblCost)
                .addGap(4, 4, 4)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMinCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaxCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        chkOmni.setText("Omni Only");
        chkOmni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOmniActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkOmni)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkOmni)))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlFiltersLayout = new javax.swing.GroupLayout(pnlFilters);
        pnlFilters.setLayout(pnlFiltersLayout);
        pnlFiltersLayout.setHorizontalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltersLayout.createSequentialGroup()
                .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlFiltersLayout.setVerticalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFiltersLayout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFiltersLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        lblStatus.setText("Loading Vehicles....");

        prgResaving.setStringPainted(true);

        lblShowing.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblShowing.setText("Showing 0 of 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tlbActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnMechTable, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 525, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblShowing, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 798, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(prgResaving, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tlbActions, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prgResaving, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnMechTable, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFilters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblShowing)))
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
        //if (list.Size() == 0) { LoadList(); }
    }//GEN-LAST:event_formWindowOpened

    private void btnOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOptionsActionPerformed
        dlgPrefs preferences = new dlgPrefs( (javax.swing.JFrame) parent, true );
        preferences.setLocationRelativeTo( this );
        preferences.setVisible( true );
        this.setVisible(true);
        LoadList();
    }//GEN-LAST:event_btnOptionsActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        LoadList(false);
        //Filter(evt);
        this.setVisible(true);
}//GEN-LAST:event_btnRefreshActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        if ( tblMechData.getSelectedRowCount() > 0 ) {
            PagePrinter print = new PagePrinter();

            try
            {
                CVReader read = new CVReader();
                int[] rows = tblMechData.getSelectedRows();
                for ( int i=0; i < rows.length; i++ ) {
                    UnitListData data = list.Get(tblMechData.convertRowIndexToModel(rows[i]));
                    CombatVehicle m = read.ReadUnit( list.getDirectory() + data.getFilename(), parent.GetData() );
                    if (data.isOmni()) {
                        m.SetCurLoadout(data.getConfig());
                    }
                    PrintVehicle sheet = new PrintVehicle(m, new ImageTracker());
                    print.Append(BFBPrinter.Letter.toPage(), sheet);
                }
                print.Print();
                tblMechData.clearSelection();
                checkSelection();

            } catch ( Exception e ) {

            }
        }

    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnMagicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMagicActionPerformed
        //setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        batchUpdateMechs();
        //setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }//GEN-LAST:event_btnMagicActionPerformed

    private void Filter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Filter
        ListFilter filters = new ListFilter();
        filters.setExtension(".saw");

        if (cmbTech.getSelectedIndex() > 0) {filters.setTech(cmbTech.getSelectedItem().toString());}
        if (cmbEra.getSelectedIndex() > 0) {filters.setEra(cmbEra.getSelectedItem().toString());}
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
    }//GEN-LAST:event_Filter

    private void txtMinCostFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMinCostFilter
        // TODO add your handling code here:
}//GEN-LAST:event_txtMinCostFilter

    private void txtMaxCostFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxCostFilter
        // TODO add your handling code here:
}//GEN-LAST:event_txtMaxCostFilter

    private void btnClearFilterFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFilterFilter
        setupList(list, false);
        
        //clear the dropdowns
        cmbEra.setSelectedIndex(0);
        cmbMotive.setSelectedIndex(0);
        cmbRulesLevel.setSelectedIndex(0);
        cmbTech.setSelectedIndex(0);
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

    private void btnAdd2ForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdd2ForceActionPerformed
        //lblForce.setText("");
        if ( tblMechData.getSelectedRowCount() > 0 ) {
            int[] rows = tblMechData.getSelectedRows();
            for ( int i=0; i < rows.length; i++ ) {
                UnitListData data = (UnitListData)((abView) tblMechData.getModel()).Get(tblMechData.convertRowIndexToModel(rows[i]));
                parent.GetForceDialogue().getForce().AddUnit(new Unit(data));
                parent.GetForceDialogue().getForce().RefreshBV();
                lblForce.setText(lblForce.getText() + " " + data.getFullName() + " added;");
            }
            btnViewForce.setEnabled( true );
            String forceList = parent.GetForceDialogue().getForce().getUnits().size() + " Units Selected: ";
            for ( Unit u : parent.GetForceDialogue().getForce().getUnits() ) {
                forceList += " " + u.TypeModel;
            }
            lblForce.setText(parent.GetForceDialogue().getForce().getUnits().size() + " Units");
            lblForce.setToolTipText(forceList);
            //btnViewForce.setToolTipText(forceList);
        }
        //tblMechData.clearSelection();
    }//GEN-LAST:event_btnAdd2ForceActionPerformed

    private void txtMaxTonFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxTonFilter
        Filter(null);
}//GEN-LAST:event_txtMaxTonFilter

    private void cmbRulesLevelFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRulesLevelFilter
        Filter(null);
}//GEN-LAST:event_cmbRulesLevelFilter

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        Filter(null);
    }//GEN-LAST:event_txtNameActionPerformed

    private void txtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyTyped
        //Filter(null);
    }//GEN-LAST:event_txtNameKeyTyped

    private void cmbMotiveFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMotiveFilter
        Filter(null);
}//GEN-LAST:event_cmbMotiveFilter

    private void btnChangeDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeDirActionPerformed
        dirPath = media.GetDirectorySelection( (javax.swing.JFrame) parent, dirPath );
        this.setVisible(true);
        parent.GetPrefs().put("ListPath", dirPath);
        LoadList(false);
    }//GEN-LAST:event_btnChangeDirActionPerformed

    private void chkOmniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOmniActionPerformed
        Filter(null);
    }//GEN-LAST:event_chkOmniActionPerformed

    private void txtSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSourceActionPerformed
        Filter(null);
}//GEN-LAST:event_txtSourceActionPerformed

    private void txtSourceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSourceKeyReleased
        Filter(null);
    }//GEN-LAST:event_txtSourceKeyReleased

    private void cmbMinMPFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMinMPFilter
        Filter(null);
}//GEN-LAST:event_cmbMinMPFilter

    private void tblMechDataMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMechDataMouseMoved
        setTooltip( (UnitListData) ((abView) tblMechData.getModel()).Get(tblMechData.convertRowIndexToModel(tblMechData.rowAtPoint(evt.getPoint()))) );
    }//GEN-LAST:event_tblMechDataMouseMoved

    private void tblMechDataKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblMechDataKeyReleased
        String entered = txtName.getText();

        switch (evt.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
                if ( !entered.isEmpty() ) { entered = entered.substring(0, entered.length()-1); }
                break;
            case KeyEvent.VK_DELETE:
                entered = "";
                break;
            case KeyEvent.VK_ENTER:
                if ( ((abView) tblMechData.getModel()).list.Size() == 1 ) {
                    tblMechData.selectAll();
                    LoadMech();
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
                if ( (evt.getKeyCode() == 32) || (evt.getKeyCode() >= 45 && evt.getKeyCode() <= 90) ) {
                    entered += evt.getKeyChar();
                }
        }
        txtName.setText(entered);
        txtNameKeyReleased(evt);
    }//GEN-LAST:event_tblMechDataKeyReleased

    private void btnViewForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewForceActionPerformed
        lblForce.setText("");
        
        //if ( tblMechData.getSelectedRowCount() > 0 ) {
        //    btnAdd2ForceActionPerformed(evt);
        //}
        parent.GetForceDialogue().setLocationRelativeTo(this);
        parent.GetForceDialogue().setVisible(true);
    }//GEN-LAST:event_btnViewForceActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        lblForce.setText("");
    }//GEN-LAST:event_formWindowClosed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        //Filter(null);
    }//GEN-LAST:event_formWindowGainedFocus

    private void cmbViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbViewActionPerformed
        switch ( cmbView.getSelectedIndex() ) {
            case 0:
                currentView = new tbTotalWarfareView(list);
                break;
            case 1:
                currentView = new tbTotalWarfareCompact(list);
                break;
            case 2:
                currentView = new tbBattleForceView(list);
                break;
            case 3:
                currentView = new tbChatInformation(list);
                break;
            default:
                currentView = new tbTotalWarfareView(list);
        }
        tblMechData.setModel(currentView);
        currentView.setupTable(tblMechData);
    }//GEN-LAST:event_cmbViewActionPerformed

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased
        Filter(null);
    }//GEN-LAST:event_txtNameKeyReleased

    private void txtMaxYearFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaxYearFilter
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaxYearFilter

    private void txtMinTonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinTonFocusLost
        if ( txtMaxTon.getText().isEmpty() ) { txtMaxTon.setText(txtMinTon.getText()); txtMaxTon.selectAll();}
        Filter(null);
    }//GEN-LAST:event_txtMinTonFocusLost

    private void txtMinBVFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinBVFocusLost
        if ( txtMaxBV.getText().isEmpty() ) { txtMaxBV.setText(txtMinBV.getText()); txtMaxBV.selectAll(); }
        Filter(null);
    }//GEN-LAST:event_txtMinBVFocusLost

    private void txtMaxTonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxTonFocusLost
        Filter(null);
    }//GEN-LAST:event_txtMaxTonFocusLost

    private void txtMaxBVFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxBVFocusLost
        Filter(null);
    }//GEN-LAST:event_txtMaxBVFocusLost

    private void txtMinYearFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinYearFocusLost
        if ( txtMaxYear.getText().isEmpty() ) { txtMaxYear.setText(txtMinYear.getText()); txtMaxYear.selectAll();}
        Filter(null);
    }//GEN-LAST:event_txtMinYearFocusLost

    private void txtMaxYearFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxYearFocusLost
        Filter(null);
    }//GEN-LAST:event_txtMaxYearFocusLost

    private void txtMinCostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinCostFocusLost
        if ( txtMaxCost.getText().isEmpty() ) { txtMaxCost.setText(txtMinCost.getText()); txtMaxCost.selectAll();}
        Filter(null);
    }//GEN-LAST:event_txtMinCostFocusLost

    private void txtMaxCostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxCostFocusLost
        Filter(null);
    }//GEN-LAST:event_txtMaxCostFocusLost

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        TXTWriter out = new TXTWriter();
        String dir = "";
        dir = media.GetDirectorySelection(this, parent.GetPrefs().get("ListDirectory", ""));
        if ( dir.isEmpty() ) {
            return;
        }

        parent.GetPrefs().put("ListDirectory", dir);
        try {
            out.WriteList(dir + File.separator + "VehicleListing.csv", ((abView) tblMechData.getModel()).list);
            Media.Messager(((abView) tblMechData.getModel()).list.Size() + " Vehicles output to " + dir + File.separator + "VehicleListing.csv");
        } catch (IOException ex) {
            //do nothing
            System.out.println(ex.getMessage());
            Media.Messager("Unable to output list\n" + ex.getMessage() );
        }
    }//GEN-LAST:event_btnExportActionPerformed

    @Override
    public void setVisible( boolean b ) {
        super.setVisible(b);
        cancelledListDirSelection = false;
        if ( list.Size() == 0 ) { LoadList(); }
        Filter(null);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd2Force;
    private javax.swing.JButton btnChangeDir;
    private javax.swing.JButton btnClearFilter;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnMagic;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnOptions;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnViewForce;
    private javax.swing.JCheckBox chkOmni;
    private javax.swing.JComboBox cmbEra;
    private javax.swing.JComboBox cmbMinMP;
    private javax.swing.JComboBox cmbMotive;
    private javax.swing.JComboBox cmbRulesLevel;
    private javax.swing.JComboBox cmbTech;
    private javax.swing.JComboBox cmbView;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JLabel lblBV;
    private javax.swing.JLabel lblCost;
    private javax.swing.JLabel lblEra;
    private javax.swing.JLabel lblForce;
    private javax.swing.JLabel lblLevel;
    private javax.swing.JLabel lblMinMP;
    private javax.swing.JLabel lblMotive;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblShowing;
    private javax.swing.JLabel lblSource;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTech;
    private javax.swing.JLabel lblTonnage;
    private javax.swing.JLabel lblTonnage1;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JProgressBar prgResaving;
    private javax.swing.JScrollPane spnMechTable;
    private javax.swing.JTable tblMechData;
    private javax.swing.JToolBar tlbActions;
    private javax.swing.JTextField txtMaxBV;
    private javax.swing.JTextField txtMaxCost;
    private javax.swing.JTextField txtMaxTon;
    private javax.swing.JTextField txtMaxYear;
    private javax.swing.JTextField txtMinBV;
    private javax.swing.JTextField txtMinCost;
    private javax.swing.JTextField txtMinTon;
    private javax.swing.JTextField txtMinYear;
    private javax.swing.JTextField txtName;
    private javax.swing.JLabel txtSelected;
    private javax.swing.JTextField txtSource;
    // End of variables declaration//GEN-END:variables

}

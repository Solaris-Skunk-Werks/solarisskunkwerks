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

import common.CommonTools;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import ssw.*;
import components.*;
import filehandlers.*;
import ssw.filehandlers.*;
import visitors.*;
import ssw.print.*;
import states.ifState;
import java.util.prefs.*;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import battleforce.*;
import common.DataFactory;
import dialog.frmForce;
import components.EquipmentCollection;
import gui.TextPane;
import ssw.printpreview.dlgPreview;
import Print.PrintConsts;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import list.view.tbQuirks;
import states.stCockpitInterface;

public class frmMain extends javax.swing.JFrame implements java.awt.datatransfer.ClipboardOwner, common.DesignForm, ifMechForm {
    FocusAdapter spinners = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if ( e.getSource() instanceof JTextComponent ) {
                final JTextComponent textComponent = ((JTextComponent)e.getSource());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        textComponent.selectAll();
                    }
                });
            }
        }
    };

    String[] Selections = { "", "", "", "", "", "", "", "" };
    Mech CurMech;
    VSetArmorTonnage ArmorTons;
    java.awt.Color RedCol = new java.awt.Color( 200, 0, 0 ),
                   GreenCol = new java.awt.Color( 0, 40, 0 );
    Object[][] Equipment = { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } };
    abPlaceable CurItem;
    // this variable is used for armoring multi-slot systems.
    int CurLocation = -1;
    JPopupMenu mnuUtilities = new JPopupMenu();
    JMenuItem mnuDetails = new JMenuItem( "Details" );
    JMenuItem mnuMountRear = new JMenuItem( "Mount Rear" );
    JMenuItem mnuSetVariable = new JMenuItem( "Set Tonnage" );
    JMenuItem mnuSetLotSize = new JMenuItem( "Set Lot Size" );
    JMenuItem mnuArmorComponent = new JMenuItem( "Armor Component" );
    JMenuItem mnuAddCapacitor = new JMenuItem( "Add Capacitor" );
    JMenuItem mnuAddInsulator = new JMenuItem( "Add Insulator" );
    JMenuItem mnuCaseless = new JMenuItem( "Switch to Caseless" );
    JMenuItem mnuTurret = new JMenuItem( "Add to Turret" );
    JMenuItem mnuSelective = new JMenuItem( "Selective Allocate" );
    JMenuItem mnuAuto = new JMenuItem( "Auto-Allocate" );
    JMenuItem mnuUnallocateAll = new JMenuItem( "Unallocate All" );
    JMenuItem mnuRemoveItem = new JMenuItem( "Remove Item" );
    JMenuItem mnuDumper = new JMenuItem("Add Dumper");
    javax.swing.JMenu mnuVGLArc = new javax.swing.JMenu();
    JMenuItem mnuVGLArcFore = new JMenuItem( "Fore" );
    JMenuItem mnuVGLArcForeSide = new JMenuItem( "Fore-Side" );
    JMenuItem mnuVGLArcRear = new JMenuItem( "Rear" );
    JMenuItem mnuVGLArcRearSide = new JMenuItem( "Rear-Side" );
    javax.swing.JMenu mnuVGLAmmo = new javax.swing.JMenu();
    JMenuItem mnuVGLAmmoFrag = new JMenuItem( "Fragmentation" );
    JMenuItem mnuVGLAmmoChaff = new JMenuItem( "Chaff" );
    JMenuItem mnuVGLAmmoIncen = new JMenuItem( "Incendiary" );
    JMenuItem mnuVGLAmmoSmoke = new JMenuItem( "Smoke" );
    JPopupMenu mnuFluff = new JPopupMenu();
    JMenuItem mnuFluffCut = new JMenuItem( "Cut" );
    JMenuItem mnuFluffCopy = new JMenuItem( "Copy" );
    JMenuItem mnuFluffPaste = new JMenuItem( "Paste" );

    TextPane Overview = new TextPane();
    TextPane Capabilities = new TextPane();
    TextPane Deployment = new TextPane();
    TextPane History = new TextPane();
    TextPane Additional = new TextPane();
    TextPane Variants = new TextPane();
    TextPane Notables = new TextPane();

    MechLoadoutRenderer Mechrender;
    public Preferences Prefs;
    boolean Load = false,
            SetSource = true;
    private Cursor Hourglass = new Cursor( Cursor.WAIT_CURSOR );
    private Cursor NormalCursor = new Cursor( Cursor.DEFAULT_CURSOR );
    // ImageIcon FluffImage = Utils.createImageIcon( SSWConstants.NO_IMAGE );
    public DataFactory data;
    public ArrayList<Quirk> quirks = new ArrayList<Quirk>();

    private dlgPrintBatchMechs BatchWindow = null;
    private ImageTracker imageTracker = new ImageTracker();
    public dlgOpen dOpen = new dlgOpen(this, true);
    public frmForce dForce = new frmForce(this, imageTracker);

    final int BALLISTIC = 0,
              ENERGY = 1,
              MISSILE = 2,
              PHYSICAL = 3, 
              EQUIPMENT = 4,
              AMMUNITION = 6,
              SELECTED = 7,
              ARTILLERY = 5;
    private final AvailableCode PPCCapAC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private final AvailableCode LIAC = new AvailableCode( AvailableCode.TECH_BOTH );
    private final AvailableCode CaselessAmmoAC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private PartialWing wing = new PartialWing(CurMech);
    private final AvailableCode PWAC = wing.GetAvailability();

    /** Creates new form frmMain */
    public frmMain() {
        Prefs = Preferences.userRoot().node( common.Constants.SSWPrefs );
        CurMech = new Mech( Prefs );
        ArmorTons = new VSetArmorTonnage( Prefs );
        Mechrender = new MechLoadoutRenderer( this );

        // added for easy checking
        PPCCapAC.SetISCodes( 'E', 'X', 'X', 'E' );
        PPCCapAC.SetISDates( 3057, 3060, true, 3060, 0, 0, false, false );
        PPCCapAC.SetISFactions( "DC", "DC", "", "" );
        PPCCapAC.SetPBMAllowed( true );
        PPCCapAC.SetPIMAllowed( true );
        PPCCapAC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        LIAC.SetISCodes( 'E', 'F', 'F', 'X' );
        LIAC.SetISDates( 0, 0, false, 2575, 2820, 0, true, false );
        LIAC.SetISFactions( "TH", "", "", "" );
        LIAC.SetCLCodes( 'E', 'X', 'E', 'F' );
        LIAC.SetCLDates( 0, 0, false, 2575, 0, 0, false, false );
        LIAC.SetCLFactions( "TH", "", "", "" );
        LIAC.SetPBMAllowed( true );
        LIAC.SetPIMAllowed( true );
        LIAC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        CaselessAmmoAC.SetISCodes( 'D', 'X', 'X', 'E' );
        CaselessAmmoAC.SetISDates( 3055, 3056, true, 3056, 0, 0, false, false );
        CaselessAmmoAC.SetISFactions( "FC", "FC", "", "" );
        CaselessAmmoAC.SetPBMAllowed( true );
        CaselessAmmoAC.SetPIMAllowed( true );
        CaselessAmmoAC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        // fix for NetBeans stupidity.
        pnlDamageChart = new DamageChart();

        initComponents();
        Overview.SetEditorSize( 310, 380 );
        Capabilities.SetEditorSize( 310, 380 );
        Deployment.SetEditorSize( 310, 380 );
        History.SetEditorSize( 310, 380 );
        Additional.SetEditorSize( 310, 380 );
        Variants.SetEditorSize( 310, 380 );
        Notables.SetEditorSize( 310, 380 );
        pnlOverview.add( Overview );
        pnlCapabilities.add( Capabilities );
        pnlDeployment.add( Deployment );
        pnlHistory.add( History );
        pnlAdditionalFluff.add( Additional );
        pnlVariants.add( Variants );
        pnlNotables.add( Notables );
        setViewToolbar( Prefs.getBoolean( "ViewToolbar", true ) );
        setTitle( SSWConstants.AppDescription + " " + SSWConstants.Version );
        pack();

        mnuDetails.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GetInfoOn();
            }
        });

        mnuMountRear.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MountRear();
            }
        });

        mnuSetVariable.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetVariableSize();
            }
        });

        mnuSetLotSize.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetAmmoLotSize();
            }
        });

        mnuArmorComponent.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( java.awt.event.ActionEvent e) {
                ArmorComponent();
            }
        });

        mnuAddCapacitor.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PPCCapacitor();
            }
        });

        mnuAddInsulator.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LaserInsulator();
            }
        });

        mnuTurret.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TurretMount();
            }
        });

        mnuDumper.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DumperMount();
            }
        });

        mnuCaseless.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwitchCaseless();
            }
        });

        mnuVGLArcFore.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetVGLArcFore();
            }
        });

        mnuVGLArcForeSide.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetVGLArcForeSide();
            }
        });

        mnuVGLArcRear.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetVGLArcRear();
            }
        });

        mnuVGLArcRearSide.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetVGLArcRearSide();
            }
        });

        mnuVGLAmmoFrag.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetVGLAmmoFrag();
            }
        });

        mnuVGLAmmoChaff.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetVGLAmmoChaff();
            }
        });

        mnuVGLAmmoIncen.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetVGLAmmoIncendiary();
            }
        });

        mnuVGLAmmoSmoke.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SetVGLAmmoSmoke();
            }
        });

        mnuSelective.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                SelectiveAllocate();
            }
        });

        mnuAuto.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                AutoAllocate();
            }
        });

        mnuUnallocateAll.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UnallocateAll();
            }
        });

        mnuRemoveItem.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RemoveItemCritTab();
            }
        });

        mnuVGLArc.setText( "Set VGL Arc" );
        mnuVGLArc.add( mnuVGLArcFore );
        mnuVGLArc.add( mnuVGLArcForeSide );
        mnuVGLArc.add( mnuVGLArcRear );
        mnuVGLArc.add( mnuVGLArcRearSide );

        mnuVGLAmmo.setText( "Set VGL Ammo" );
        mnuVGLAmmo.add( mnuVGLAmmoFrag );
        mnuVGLAmmo.add( mnuVGLAmmoChaff );
        mnuVGLAmmo.add( mnuVGLAmmoIncen );
        mnuVGLAmmo.add( mnuVGLAmmoSmoke );

        mnuUtilities.add( mnuDetails );
        mnuUtilities.add( mnuMountRear );
        mnuUtilities.add( mnuSetVariable );
        mnuUtilities.add( mnuSetLotSize );
        mnuUtilities.add( mnuArmorComponent );
        mnuUtilities.add( mnuAddCapacitor );
        mnuUtilities.add( mnuAddInsulator );
        mnuUtilities.add( mnuCaseless );
        mnuUtilities.add( mnuTurret );
        mnuUtilities.add( mnuVGLArc );
        mnuUtilities.add( mnuVGLAmmo );
        mnuUtilities.add( mnuSelective );
        mnuUtilities.add( mnuAuto );
        mnuUtilities.add( mnuDumper );
        mnuUtilities.add( mnuUnallocateAll );
        mnuUtilities.add( mnuRemoveItem );

        mnuSetVariable.setVisible( false );
        mnuArmorComponent.setVisible( false );
        mnuAddCapacitor.setVisible( false );
        mnuAddInsulator.setVisible( false );
        mnuTurret.setVisible( false );
        mnuCaseless.setVisible( false );
        mnuVGLArc.setVisible( false );
        mnuVGLAmmo.setVisible( false );

        mnuFluffCut.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                FluffCut( mnuFluff.getInvoker() );
            }
        });

        mnuFluffCopy.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FluffCopy( mnuFluff.getInvoker() );
            }
        });

        mnuFluffPaste.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FluffPaste( mnuFluff.getInvoker() );
            }
        });

        mnuFluff.add( mnuFluffCut );
        mnuFluff.add( mnuFluffCopy );
        mnuFluff.add( mnuFluffPaste );

        try {
            CurMech.Visit( new VMechFullRecalc() );
        } catch( Exception e ) {
            // this should never throw an exception, but log it anyway
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }

        // get the data factory ready
        try {
            data = new DataFactory( CurMech );
        } catch( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }

        // set the program options
        cmbRulesLevel.setSelectedItem( Prefs.get( "NewMech_RulesLevel", "Tournament Legal" ) );
        cmbMechEra.setSelectedItem( Prefs.get( "NewMech_Era", "Age of War/Star League" ) );
        BuildTechBaseSelector();
        cmbTechBase.setSelectedItem( Prefs.get( "NewMech_Techbase", "Inner Sphere" ) );

        BuildChassisSelector();
        BuildEngineSelector();
        BuildGyroSelector();
        BuildCockpitSelector();
        BuildEnhancementSelector();
        BuildArmorSelector();
        BuildHeatsinkSelector();
        BuildJumpJetSelector();
        FixArmorSpinners();
        SetPatchworkArmor();
        RefreshSummary();
        RefreshInfoPane();
        RefreshInternalPoints();
        SetLoadoutArrays();
        SetWeaponChoosers();
        cmbInternalType.setSelectedItem( SSWConstants.DEFAULT_CHASSIS );
        cmbEngineType.setSelectedItem( SSWConstants.DEFAULT_ENGINE );
        cmbGyroType.setSelectedItem( SSWConstants.DEFAULT_GYRO );
        cmbCockpitType.setSelectedItem( SSWConstants.DEFAULT_COCKPIT );
        cmbPhysEnhance.setSelectedItem( SSWConstants.DEFAULT_ENHANCEMENT );
        cmbHeatSinkType.setSelectedItem( Prefs.get( "NewMech_Heatsinks", "Single Heat Sink" ) );
        cmbJumpJetType.setSelectedItem( SSWConstants.DEFAULT_JUMPJET );
        cmbArmorType.setSelectedItem( SSWConstants.DEFAULT_ARMOR );
        cmbOmniVariant.setModel( new javax.swing.DefaultComboBoxModel( new String[] { CurMech.GetLoadout().GetName() } ) );
        lblSumPAmps.setVisible( false );
        txtSumPAmpsTon.setVisible( false );
        txtSumPAmpsACode.setVisible( false );

        tblWeaponManufacturers.setModel( new javax.swing.table.AbstractTableModel() {
            @Override
            public String getColumnName( int col ) {
                if( col == 1 ) {
                    return "Manufacturer/Model";
                } else {
                    return "Item Name";
                }
            }
            public int getRowCount() { return CurMech.GetLoadout().GetEquipment().size(); }
            public int getColumnCount() { return 2; }
            public Object getValueAt( int row, int col ) {
                Object o = CurMech.GetLoadout().GetEquipment().get( row );
                if( col == 1 ) {
                    return ((abPlaceable) o).GetManufacturer();
                } else {
                    return ((abPlaceable) o).CritName();
                }
            }
            @Override
            public boolean isCellEditable( int row, int col ) {
                if( col == 0 ) {
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public void setValueAt( Object value, int row, int col ) {
                if( col == 0 ) { return; }
                if( ! ( value instanceof String ) ) { return; }
                abPlaceable a = (abPlaceable) CurMech.GetLoadout().GetEquipment().get( row );
                if( chkIndividualWeapons.isSelected() ) {
                    a.SetManufacturer( (String) value );
                    fireTableCellUpdated( row, col );
                } else {
                    ArrayList v = CurMech.GetLoadout().GetEquipment();
                    for( int i = 0; i < v.size(); i++ ) {
                        if( FileCommon.LookupStripArc( ((abPlaceable) v.get( i )).LookupName() ).equals( FileCommon.LookupStripArc( a.LookupName() ) ) ) {
                            ((abPlaceable) v.get( i )).SetManufacturer( (String) value );
                        }
                    }
                    fireTableDataChanged();
                }
            }
        } );

        tblWeaponManufacturers.getInputMap( javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_TAB, 0, false ), "selectNextRow" );

        // if the user wants, load the last mech.
        if( Prefs.getBoolean( "LoadLastMech", false ) ) { LoadMechFromFile(Prefs.get("LastOpenDirectory", "") + Prefs.get("LastOpenFile", "") ); }

        if ( !Prefs.get("FileToOpen", "").isEmpty() ) { LoadMechFromFile( Prefs.get("FileToOpen", "") ); }

        //dOpen.LoadList();
        CurMech.SetChanged( false );
    }

    public Preferences GetPrefs() {
        return Prefs;
    }

    public Mech GetMech() {
        return CurMech;
    }

    public abPlaceable GetCurItem() {
        return CurItem;
    }

    public DataFactory GetData() {
        return data;
    }

    public frmForce GetForceDialogue() {
        return dForce;
    }

    public MechLoadoutRenderer GetLoadoutRenderer() {
        return Mechrender;
    }

    private void SetWeaponChoosers() {
        // sets the weapon choosers up.  first, get the user's choices.

        // get the equipment lists for the choices.
        data.Rebuild( CurMech );
        Equipment[ENERGY] = data.GetEquipment().GetEnergyWeapons( CurMech );
        Equipment[MISSILE] = data.GetEquipment().GetMissileWeapons( CurMech );
        Equipment[BALLISTIC] = data.GetEquipment().GetBallisticWeapons( CurMech );
        Equipment[PHYSICAL] = data.GetEquipment().GetPhysicalWeapons( CurMech );
        Equipment[ARTILLERY] = data.GetEquipment().GetArtillery( CurMech );
        Equipment[EQUIPMENT] = data.GetEquipment().GetEquipment( CurMech );
        Equipment[AMMUNITION] = new Object[] { " " };
        if( CurMech.GetLoadout().GetNonCore().toArray().length <= 0 ) {
            Equipment[SELECTED] = new Object[] { " " };
        } else {
            Equipment[SELECTED] = CurMech.GetLoadout().GetNonCore().toArray();
        }

        for( int i = 0; i < Equipment.length; i++ ) {
            if( Equipment[i] == null ) {
                Equipment[i] = new Object[] { " " };
            }
        }

        // now fill in the list boxes
        lstChooseEnergy.setListData( Equipment[ENERGY] );
        lstChooseMissile.setListData( Equipment[MISSILE] );
        lstChooseBallistic.setListData( Equipment[BALLISTIC] );
        lstChoosePhysical.setListData( Equipment[PHYSICAL] );
        lstChooseEquipment.setListData( Equipment[EQUIPMENT] );
        lstChooseAmmunition.setListData( Equipment[AMMUNITION] );
        lstSelectedEquipment.setListData( Equipment[SELECTED] );
        lstChooseArtillery.setListData( Equipment[ARTILLERY] );
        lstSelectedEquipment.repaint();
    }

    private void SetLoadoutArrays() {
        // adds the loadout arrays to the Criticals screen
        if( CurMech.IsQuad() ) {
            // special handling code one way or the other...
            ((javax.swing.border.TitledBorder) pnlLACrits.getBorder()).setTitle( "Left Front Leg" );
            ((javax.swing.border.TitledBorder) pnlRACrits.getBorder()).setTitle( "Right Front Leg" );
            ((javax.swing.border.TitledBorder) pnlLLCrits.getBorder()).setTitle( "Left Rear Leg" );
            ((javax.swing.border.TitledBorder) pnlRLCrits.getBorder()).setTitle( "Right Rear Leg" );
            lstLACrits.setVisibleRowCount( 6 );
            lstRACrits.setVisibleRowCount( 6 );
        } else {
            ((javax.swing.border.TitledBorder) pnlLACrits.getBorder()).setTitle( "Left Arm" );
            ((javax.swing.border.TitledBorder) pnlRACrits.getBorder()).setTitle( "Right Arm" );
            ((javax.swing.border.TitledBorder) pnlLLCrits.getBorder()).setTitle( "Left Leg" );
            ((javax.swing.border.TitledBorder) pnlRLCrits.getBorder()).setTitle( "Right Leg" );
            lstLACrits.setVisibleRowCount( 12 );
            lstRACrits.setVisibleRowCount( 12 );
        }

        CheckActuators();

        // set the arrays to the lists
        lstHDCrits.setListData( CurMech.GetLoadout().GetHDCrits() );
        lstCTCrits.setListData( CurMech.GetLoadout().GetCTCrits() );
        lstLTCrits.setListData( CurMech.GetLoadout().GetLTCrits() );
        lstRTCrits.setListData( CurMech.GetLoadout().GetRTCrits() );
        lstLACrits.setListData( CurMech.GetLoadout().GetLACrits() );
        lstRACrits.setListData( CurMech.GetLoadout().GetRACrits() );
        lstLLCrits.setListData( CurMech.GetLoadout().GetLLCrits() );
        lstRLCrits.setListData( CurMech.GetLoadout().GetRLCrits() );
        lstCritsToPlace.setListData( CurMech.GetLoadout().GetQueue().toArray() );
    }

    public void setMech( Mech m ) {
        CurMech = m;
        LoadMechIntoGUI();
    }

    public void CheckActuators() {
        if( CurMech.IsQuad() ) {
            chkLALowerArm.setEnabled( false );
            chkRALowerArm.setEnabled( false );
            chkLAHand.setEnabled( false );
            chkRAHand.setEnabled( false );
            chkLALowerArm.setSelected( true );
            chkRALowerArm.setSelected( true );
            chkLAHand.setSelected( true );
            chkRAHand.setSelected( true );
        } else {
            if( CurMech.GetActuators().LockedLeft() ) {
                chkLALowerArm.setEnabled( false );
                chkLAHand.setEnabled( false );
            } else {
                chkLALowerArm.setEnabled( true );
                chkLAHand.setEnabled( true );
            }
            if( CurMech.GetActuators().LockedRight() ) {
                chkRALowerArm.setEnabled( false );
                chkRAHand.setEnabled( false );
            } else {
                chkRALowerArm.setEnabled( true );
                chkRAHand.setEnabled( true );
            }
            if( CurMech.GetActuators().LeftLowerInstalled() ) {
                chkLALowerArm.setSelected( true );
                if( CurMech.GetActuators().LeftHandInstalled() ) {
                    chkLAHand.setSelected( true );
                } else {
                    chkLAHand.setSelected( false );
                }
            } else {
                chkLALowerArm.setSelected( false );
                chkLAHand.setSelected( false );
            }
            if( CurMech.GetActuators().RightLowerInstalled() ) {
                chkRALowerArm.setSelected( true );
                if( CurMech.GetActuators().RightHandInstalled() ) {
                    chkRAHand.setSelected( true );
                } else {
                    chkRAHand.setSelected( false );
                }
            } else {
                chkRALowerArm.setSelected( false );
                chkRAHand.setSelected( false );
            }
        }
    }

    private void FixWalkMPSpinner() {
        // This fixes the walking MP spinner if the mech's tonnage changes.
        int MaxWalk = CurMech.GetMaxWalkMP();
        int CurWalk = CurMech.GetWalkingMP();

        // since this should only ever happen when the tonnage changes, we'll
        // deal with the mech's engine rating here.  Reset the Run MP label too
        if( CurWalk > MaxWalk ) { CurWalk = MaxWalk; }
        //CurMech.GetEngine().SetRating( CurWalk * CurMech.GetTonnage(), CurMech.IsPrimitive() );
        try {
            CurMech.SetWalkMP( CurWalk );
        } catch( Exception e ) {
            Media.Messager( e.getMessage() + "\nSetting Walk MP to 1.  Please reset to desired speed.");
            try {
                CurMech.SetWalkMP( 1 );
            } catch( Exception e1 ) {
                Media.Messager( this, "Fatal error while attempting to set Walk MP to 1:\n" + e1.getMessage() + "\nStarting over with a new 'Mech.  Sorry." );
                GetNewMech();
                return;
            }
        }
        lblRunMP.setText( "" + CurMech.GetRunningMP() );

        // reset the spinner model and we're done.
        spnWalkMP.setModel( new javax.swing.SpinnerNumberModel( CurWalk, 1, MaxWalk, 1) );
        ((JSpinner.DefaultEditor)spnWalkMP.getEditor()).getTextField().addFocusListener(spinners);
    }

    private void BuildChassisSelector() {
        // builds the structure selection box
        ArrayList list = new ArrayList();

        // get the structure states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetIntStruc().GetStates( CurMech.IsQuad() );
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( BuildLookupName( check[i] ) );
            }
        }

        // turn the ArrayList into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the structure chooser
        cmbInternalType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildEngineSelector() {
        // builds the engine selection box
        ArrayList list = new ArrayList();

        // get the engine states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetEngine().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( BuildLookupName( check[i] ) );
            }
        }

        // turn the ArrayList into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the engine chooser
        cmbEngineType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildGyroSelector() {
        // builds the gyro selection box
        ArrayList list = new ArrayList();

        // get the gyro states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetGyro().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( BuildLookupName( check[i] ) );
            }
        }

        // turn the ArrayList into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the gyro chooser
        cmbGyroType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildCockpitSelector() {
        // builds the structure selection box
        ArrayList list = new ArrayList();

        // get the structure states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetCockpit().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( check[i].LookupName() );
            }
        }

        // turn the ArrayList into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the structure chooser
        cmbCockpitType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildEnhancementSelector() {
        // builds the physical enhancement selection box
        ArrayList list = new ArrayList();

        // get the enhancement states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetPhysEnhance().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( BuildLookupName( check[i] ) );
            }
        }

        // turn the ArrayList into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the physical enhancement chooser
        cmbPhysEnhance.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildJumpJetSelector() {
        // ensures that we can enable the Improved Jump Jets checkbox.
        ArrayList list = new ArrayList();

        // get the jump jet states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetJumpJets().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( check[i].LookupName() );
            }
        }

        // turn the ArrayList into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the jump jet chooser
        cmbJumpJetType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
        if( temp.length > 0 ) {
            EnableJumpJets( true );
            cmbJumpJetType.setSelectedItem( CurMech.GetJumpJets().LookupName() );
        } else {
            EnableJumpJets( false );
        }
    }

    private void FixJJSpinnerModel() {
        // since the jump jet spinner model changes every time the walking mp
        // changes, here is a quick little routine to do it without extra fuss.

        int min = 0;
        int max = 0;
        int current = 0;

        if( CurMech.IsOmnimech() ) {
            min = CurMech.GetJumpJets().GetBaseLoadoutNumJJ();
        }

        if( CurMech.GetJumpJets().IsImproved() ) {
            if( CurMech.GetArmor().IsHardened() && !CurMech.GetJumpJets().IsImproved() ) {
                max = CurMech.GetRunningMP() - 1;
            } else {
                max = CurMech.GetRunningMP();
            }
        } else {
            max = CurMech.GetWalkingMP();
        }

        current = CurMech.GetJumpJets().GetNumJJ();

        // is the number of jump jets greater than the maximum allowed?
        if( current > max ) {
            for( ; current > max; current-- ) {
                CurMech.GetJumpJets().DecrementNumJJ();
            }
        }

        // is the number of jump jet less than the minimum?
        if( current < min ) {
            for( ; current < min; current++ ) {
                CurMech.GetJumpJets().IncrementNumJJ();
            }
        }

        // see if we need to enable the jump jet manufacturer field
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            // enable the field
            txtJJModel.setEnabled( true );
        } else {
            // disable it, but don't clear it
            txtJJModel.setEnabled( false );
        }

        spnJumpMP.setModel( new javax.swing.SpinnerNumberModel( current, min, max, 1) );
        ((JSpinner.DefaultEditor)spnJumpMP.getEditor()).getTextField().addFocusListener(spinners);
    }

    private void FixHeatSinkSpinnerModel() {
        // mainly provided for Omnimechs.
        if( CurMech.IsOmnimech() ) {
            spnNumberOfHS.setModel( new javax.swing.SpinnerNumberModel(
                CurMech.GetHeatSinks().GetNumHS(), CurMech.GetHeatSinks().GetBaseLoadoutNumHS(), 65, 1) );
        } else {
            spnNumberOfHS.setModel( new javax.swing.SpinnerNumberModel(
                CurMech.GetHeatSinks().GetNumHS(), CurMech.GetEngine().FreeHeatSinks(), 65, 1) );
        }

        ((JSpinner.DefaultEditor)spnNumberOfHS.getEditor()).getTextField().addFocusListener(spinners);
    }

    private void FixJumpBoosterSpinnerModel() {
        int current = CurMech.GetJumpBoosterMP();

        spnBoosterMP.setModel( new javax.swing.SpinnerNumberModel( current, 0, 20, 1) );
    }

    private void BuildHeatsinkSelector() {
        // builds the heat sink selection box
        ArrayList list = new ArrayList();

        // get the heat sink states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetHeatSinks().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( BuildLookupName( check[i] ) );
            }
        }

        // turn the ArrayList into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get( i );
        }

        // now set the heat sink chooser
        cmbHeatSinkType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildArmorSelector() {
        // builds the armor selection box
        ArrayList list = new ArrayList();

        // get the armor states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetArmor().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( BuildLookupName( check[i] ) );
            }
        }

        // turn the ArrayList into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the armor chooser
        cmbArmorType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildPatchworkChoosers() {
        // builds the armor selection box
        ArrayList list = new ArrayList();

        // get the armor states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetArmor().GetPatchworkStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( BuildLookupName( check[i] ) );
            }
        }

        // turn the ArrayList into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the armor chooser
        cmbPWHDType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
        cmbPWCTType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
        cmbPWLTType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
        cmbPWRTType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
        cmbPWLAType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
        cmbPWRAType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
        cmbPWLLType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
        cmbPWRLType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildTechBaseSelector() {
        switch( CurMech.GetEra() ) {
            case AvailableCode.ERA_STAR_LEAGUE:
                cmbTechBase.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Inner Sphere" } ) );
                break;
            default:
                if( CurMech.GetRulesLevel() >= AvailableCode.RULES_EXPERIMENTAL ) {
                    cmbTechBase.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Inner Sphere", "Clan", "Mixed" } ) );
                } else if( CurMech.GetRulesLevel() == AvailableCode.RULES_INTRODUCTORY ) {
                    cmbTechBase.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Inner Sphere" } ) );
                } else {
                    cmbTechBase.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Inner Sphere", "Clan" } ) );
                }
                break;
        }
        try {
            cmbTechBase.setSelectedIndex( CurMech.GetTechbase() );
        } catch( Exception e ) {
            Media.Messager( "Could not set the Techbase due to changes.\nReverting to Inner Sphere." );
            cmbTechBase.setSelectedIndex( 0 );
        }
    }

    private void BuildMechTypeSelector() {
        switch( CurMech.GetRulesLevel() ) {
            case AvailableCode.RULES_INTRODUCTORY:
                cmbMechType.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "BattleMech" } ) );
                CurMech.SetModern();
                break;
            case AvailableCode.RULES_ERA_SPECIFIC:
                if( CurMech.GetEra() == AvailableCode.ERA_SUCCESSION ) {
                    cmbMechType.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "BattleMech", "IndustrialMech" } ) );
                    CurMech.SetModern();
                } else {
                    cmbMechType.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "BattleMech", "IndustrialMech", "Primitive BattleMech", "Primitive IndustrialMech" } ) );
                }
                break;
            default:
                cmbMechType.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "BattleMech", "IndustrialMech" } ) );
                CurMech.SetModern();
                break;
        }
        try {
            if( CurMech.IsIndustrialmech() ) {
                if( CurMech.IsPrimitive() ) {
                    cmbMechType.setSelectedIndex( 3 );
                } else {
                    cmbMechType.setSelectedIndex( 1 );
                }
            } else {
                if( CurMech.IsPrimitive() ) {
                    cmbMechType.setSelectedIndex( 2 );
                } else {
                    cmbMechType.setSelectedIndex( 0 );
                }
            }
        } catch( Exception e ) {
            Media.Messager( "Could not set the 'Mech type due to changes.\nReverting to a BattleMech." );
            cmbMechType.setSelectedIndex( 0 );
        }
    }

    public String BuildLookupName( ifState s ) {
        String retval = s.LookupName();
        if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
            if( s.HasCounterpart() ) {
                if( s.GetAvailability().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    return "(CL) " + retval;
                } else {
                    return "(IS) " + retval;
                }
            } else {
                return retval;
            }
        } else {
            return retval;
        }
    }

    private void RefreshEquipment() {
        // refreshes the equipment selectors

        // fix Artemis IV controls
        ifMissileGuidance ArtCheck = new ArtemisIVFCS( null );
        if( CommonTools.IsAllowed( ArtCheck.GetAvailability(), CurMech ) ) {
            chkFCSAIV.setEnabled( true );
        } else {
            chkFCSAIV.setSelected( false );
            chkFCSAIV.setEnabled( false );
        }

        // fix Artemis V controls
        ArtCheck = new ArtemisVFCS( null );
        if( CommonTools.IsAllowed( ArtCheck.GetAvailability(), CurMech ) ) {
            chkFCSAV.setEnabled( true );
        } else {
            chkFCSAV.setSelected( false );
            chkFCSAV.setEnabled( false );
        }

        // fix Artemis IV controls
        ArtCheck = new ApolloFCS( null );
        if( CommonTools.IsAllowed( ArtCheck.GetAvailability(), CurMech ) ) {
            chkFCSApollo.setEnabled( true );
        } else {
            chkFCSApollo.setSelected( false );
            chkFCSApollo.setEnabled( false );
        }

        // fix the targeting computer display
        if( CommonTools.IsAllowed( CurMech.GetTC().GetAvailability(), CurMech ) ) {
            chkUseTC.setEnabled( true );
            if( CurMech.UsingTC() ) {
                chkUseTC.setSelected( true );
            } else {
                chkUseTC.setSelected( false );
            }
        } else {
            chkUseTC.setSelected( false );
            chkUseTC.setEnabled( false );
        }

        // fix the CASE controls
        if( CommonTools.IsAllowed( CurMech.GetLoadout().GetCTCase().GetAvailability(), CurMech ) ) {
            chkCTCASE.setEnabled( true );
            chkLTCASE.setEnabled( true );
            chkRTCASE.setEnabled( true );
        } else {
            chkCTCASE.setSelected( false );
            chkCTCASE.setEnabled( false );
            chkLTCASE.setSelected( false );
            chkLTCASE.setEnabled( false );
            chkRTCASE.setSelected( false );
            chkRTCASE.setEnabled( false );
        }

        // check the command console, ejection seat, and FHES
        if( CurMech.GetCockpit().CanUseCommandConsole() && CommonTools.IsAllowed( CurMech.GetCommandConsole().GetAvailability(), CurMech ) &! chkFHES.isSelected() ) {
            chkCommandConsole.setEnabled( true );
        } else {
            chkCommandConsole.setEnabled( false );
            chkCommandConsole.setSelected( false );
        }
        if( CurMech.CanUseFHES() && CommonTools.IsAllowed( CurMech.GetFHESAC(), CurMech ) ) {
            chkFHES.setEnabled( true );
        } else {
            chkFHES.setEnabled( false );
            chkFHES.setSelected( false );
        }
        if( CurMech.GetCockpit().IsTorsoMounted() )
        {
            chkEjectionSeat.setEnabled( false );
            chkEjectionSeat.setSelected( false );
        }
        if( CurMech.IsIndustrialmech() ) {
            chkEjectionSeat.setEnabled( true );
        } else {
            chkEjectionSeat.setEnabled( false );
            chkEjectionSeat.setSelected( false );
        }

        // check all multi-slot systems
        if( CommonTools.IsAllowed( CurMech.GetNullSig().GetAvailability(), CurMech ) ) {
            chkNullSig.setEnabled( true );
        } else {
            chkNullSig.setEnabled( false );
            chkNullSig.setSelected( false );
        }
        if( CommonTools.IsAllowed( CurMech.GetVoidSig().GetAvailability(), CurMech ) ) {
            chkVoidSig.setEnabled( true );
        } else {
            chkVoidSig.setEnabled( false );
            chkVoidSig.setSelected( false );
        }
        if( CommonTools.IsAllowed( CurMech.GetChameleon().GetAvailability(), CurMech ) ) {
            chkCLPS.setEnabled( true );
        } else {
            chkCLPS.setEnabled( false );
            chkCLPS.setSelected( false );
        }
        if( CommonTools.IsAllowed( CurMech.GetBlueShield().GetAvailability(), CurMech ) ) {
            chkBSPFD.setEnabled( true );
        } else {
            chkBSPFD.setEnabled( false );
            chkBSPFD.setSelected( false );
        }
        if( CurMech.IsIndustrialmech() ) {
            chkEnviroSealing.setEnabled( true );
            chkEjectionSeat.setEnabled( true );
        } else {
            chkEnviroSealing.setEnabled( false );
            chkEjectionSeat.setEnabled( false );
            chkEnviroSealing.setSelected( false );
            chkEjectionSeat.setSelected( false );
        }
        if( CommonTools.IsAllowed( CurMech.GetTracks().GetAvailability(), CurMech ) ) {
            chkTracks.setEnabled( true );
        } else {
            chkTracks.setEnabled( false );
            chkTracks.setSelected( false );
        }
        if( CommonTools.IsAllowed( CurMech.GetLoadout().GetSupercharger().GetAvailability(), CurMech ) ) {
            chkSupercharger.setEnabled( true );
            cmbSCLoc.setEnabled( true );
            lblSupercharger.setEnabled( true );
        } else {
            chkSupercharger.setEnabled( false );
            cmbSCLoc.setEnabled( false );
            lblSupercharger.setEnabled( false );
        }
        if( CommonTools.IsAllowed( PWAC, CurMech ) &! CurMech.IsOmnimech() ) {
            chkPartialWing.setEnabled( true );
        } else {
            chkPartialWing.setEnabled( false );
        }
        chkPartialWing.setSelected( CurMech.UsingPartialWing() );
        if( CommonTools.IsAllowed( CurMech.GetJumpBooster().GetAvailability(), CurMech ) &! CurMech.IsOmnimech() ) {
            chkBoosters.setEnabled( true );
            FixJumpBoosterSpinnerModel();
        } else {
            try {
                CurMech.SetJumpBooster( false );
            } catch( Exception e ) {
                // nothing should ever happen here, but log it anyway
                System.err.println( "Could not remove Jump Booster!" );
            }
            chkBoosters.setEnabled( false );
            chkBoosters.setSelected( false );
            FixJumpBoosterSpinnerModel();
        }
        chkBoosters.setSelected( CurMech.UsingJumpBooster() );
        if( CurMech.UsingJumpBooster() ) {
            spnBoosterMP.setEnabled( true );
        } else {
            spnBoosterMP.setEnabled( false );
        }
        if( CommonTools.IsAllowed( CurMech.GetLLAES().GetAvailability(), CurMech ) ) {
            chkRAAES.setEnabled( true );
            chkLAAES.setEnabled( true );
            chkLegAES.setEnabled( true );
        } else {
            chkRAAES.setSelected( false );
            chkLAAES.setSelected( false );
            chkLegAES.setSelected( false );
            chkRAAES.setEnabled( false );
            chkLAAES.setEnabled( false );
            chkLegAES.setEnabled( false );
        }

        // It's a trap!
        chkBoobyTrap.setSelected(false);
        if ( CommonTools.IsAllowed( CurMech.GetLoadout().GetBoobyTrap().GetAvailability(), CurMech ) ) {
            chkBoobyTrap.setEnabled( true );
            if ( CurMech.GetLoadout().HasBoobyTrap() ) { chkBoobyTrap.setSelected(true); }
        } else {
            chkBoobyTrap.setEnabled( false );
        }


        // now check the CASE II systems
        if( CommonTools.IsAllowed( CurMech.GetLoadout().GetCTCaseII().GetAvailability(), CurMech ) ) {
            chkHDCASE2.setEnabled( true );
            chkCTCASE2.setEnabled( true );
            chkLTCASE2.setEnabled( true );
            chkRTCASE2.setEnabled( true );
            chkLACASE2.setEnabled( true );
            chkRACASE2.setEnabled( true );
            chkLLCASE2.setEnabled( true );
            chkRLCASE2.setEnabled( true );
        } else {
            try {
                chkHDCASE2.setEnabled( false );
                chkHDCASE2.setSelected( false );
                CurMech.GetLoadout().SetHDCASEII( false, -1, false );
                chkCTCASE2.setEnabled( false );
                chkCTCASE2.setSelected( false );
                CurMech.GetLoadout().SetCTCASEII( false, -1, false );
                chkLTCASE2.setEnabled( false );
                chkLTCASE2.setSelected( false );
                CurMech.GetLoadout().SetLTCASEII( false, -1, false );
                chkRTCASE2.setEnabled( false );
                chkRTCASE2.setSelected( false );
                CurMech.GetLoadout().SetRTCASEII( false, -1, false );
                chkLACASE2.setEnabled( false );
                chkLACASE2.setSelected( false );
                CurMech.GetLoadout().SetLACASEII( false, -1, false );
                chkRACASE2.setEnabled( false );
                chkRACASE2.setSelected( false );
                CurMech.GetLoadout().SetRACASEII( false, -1, false );
                chkLLCASE2.setEnabled( false );
                chkLLCASE2.setSelected( false );
                CurMech.GetLoadout().SetLLCASEII( false, -1, false );
                chkRLCASE2.setEnabled( false );
                chkRLCASE2.setSelected( false );
                CurMech.GetLoadout().SetRLCASEII( false, -1, false );
            } catch( Exception e ) {
                // no reason we should get exceptions when unallocating CASE.
                System.err.println( e.getMessage() );
            }
        }

        // Clan CASE checkbox
        if( CurMech.GetLoadout().GetTechBase() > AvailableCode.TECH_INNER_SPHERE ) {
            chkClanCASE.setEnabled( true );
        } else {
            CurMech.GetLoadout().SetClanCASE( false );
            chkClanCASE.setSelected( false );
            chkClanCASE.setEnabled( false );
        }

        // turret checkboxes
        if( CommonTools.IsAllowed( CurMech.GetLoadout().GetHDTurret().GetAvailability(), CurMech ) ) {
            if( CurMech.GetLoadout().CanUseHDTurret() ) {
                chkHDTurret.setEnabled( true );
            } else {
                chkHDTurret.setSelected( false );
                chkHDTurret.setEnabled( false );
            }
            if( CurMech.GetLoadout().CanUseLTTurret() ) {
                chkLTTurret.setEnabled( true );
            } else {
                chkLTTurret.setSelected( false );
                chkLTTurret.setEnabled( false );
            }
            if( CurMech.GetLoadout().CanUseRTTurret() ) {
                chkRTTurret.setEnabled( true );
            } else {
                chkRTTurret.setSelected( false );
                chkRTTurret.setEnabled( false );
            }
        } else {
            chkHDTurret.setSelected( false );
            chkHDTurret.setEnabled( false );
            chkLTTurret.setSelected( false );
            chkLTTurret.setEnabled( false );
            chkRTTurret.setSelected( false );
            chkRTTurret.setEnabled( false );
        }

        // now set all the equipment if needed
        if( ! chkFCSAIV.isEnabled() ) {
            try {
                CurMech.SetFCSArtemisIV( false );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
            }
            chkFCSAIV.setSelected( false );
        } else {
            if( CurMech.UsingArtemisIV() ) {
                chkFCSAIV.setSelected( true );
            } else {
                chkFCSAIV.setSelected( false );
            }
        }
        if( ! chkFCSAV.isEnabled() ) {
            try {
                CurMech.SetFCSArtemisV( false );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
            }
            chkFCSAV.setSelected( false );
        } else {
            if( CurMech.UsingArtemisV() ) {
                chkFCSAV.setSelected( true );
            } else {
                chkFCSAV.setSelected( false );
            }
        }
        if( ! chkFCSApollo.isEnabled() ) {
            try {
                CurMech.SetFCSApollo( false );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
            }
            chkFCSApollo.setSelected( false );
        } else {
            if( CurMech.UsingApollo() ) {
                chkFCSApollo.setSelected( true );
            } else {
                chkFCSApollo.setSelected( false );
            }
        }
        if( ! chkSupercharger.isEnabled() ) {
            try {
                CurMech.GetLoadout().SetSupercharger( false, 0, -1 );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
            }
        } else {
            if( CurMech.GetLoadout().HasSupercharger() ) {
                chkSupercharger.setSelected( true );
            } else {
                chkSupercharger.setSelected( false );
            }
        }
        if( ! chkHDTurret.isEnabled() ) {
            try {
                CurMech.GetLoadout().SetHDTurret( false, -1 );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
            }
        } else {
            if( CurMech.GetLoadout().HasHDTurret() ) {
                chkHDTurret.setSelected( true );
            } else {
                chkHDTurret.setSelected( false );
            }
        }
        if( ! chkLTTurret.isEnabled() ) {
            try {
                CurMech.GetLoadout().SetLTTurret( false, -1 );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
            }
        } else {
            if( CurMech.GetLoadout().HasLTTurret() ) {
                chkLTTurret.setSelected( true );
            } else {
                chkLTTurret.setSelected( false );
            }
        }
        if( ! chkRTTurret.isEnabled() ) {
            try {
                CurMech.GetLoadout().SetRTTurret( false, -1 );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
            }
        } else {
            if( CurMech.GetLoadout().HasRTTurret() ) {
                chkRTTurret.setSelected( true );
            } else {
                chkRTTurret.setSelected( false );
            }
        }
        if( ! chkUseTC.isEnabled() ) { CurMech.UseTC( false, false ); }
        if( ! chkCTCASE.isEnabled() ) { CurMech.RemoveCTCase(); }
        if( ! chkLTCASE.isEnabled() ) { CurMech.RemoveLTCase(); }
        if( ! chkRTCASE.isEnabled() ) { CurMech.RemoveRTCase(); }
        chkClanCASE.setSelected( CurMech.GetLoadout().IsUsingClanCASE() );

        if( CurMech.GetRulesLevel() >= AvailableCode.RULES_EXPERIMENTAL ) {
            chkFractional.setEnabled( true );
        } else {
            chkFractional.setEnabled( false );
            CurMech.SetFractionalAccounting( false );
        }
        chkFractional.setSelected( CurMech.UsingFractionalAccounting() );

        if( CurMech.IsOmnimech() ) {
            // these items can only be loaded into the base chassis, so they
            // are always locked for an omnimech (although they may be checked).
            chkNullSig.setEnabled( false );
            chkVoidSig.setEnabled( false );
            chkBSPFD.setEnabled( false );
            chkCLPS.setEnabled( false );
            chkEnviroSealing.setEnabled( false );
            chkEjectionSeat.setEnabled( false );
            chkRAAES.setEnabled( false );
            chkLAAES.setEnabled( false );
            chkLegAES.setEnabled( false );
            chkCommandConsole.setEnabled( false );
            chkFHES.setEnabled( false );
            chkTracks.setEnabled( false );

            // now see if we have a supercharger on the base chassis
            if( CurMech.GetBaseLoadout().HasSupercharger() ) {
                chkSupercharger.setEnabled( false );
                cmbSCLoc.setEnabled( false );
                lblSupercharger.setEnabled( false );
            }

            // do we have any CASE in the baseloadout?
            if( CurMech.GetBaseLoadout().HasCTCASE() ) {
                chkCTCASE.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasLTCASE() ) {
                chkLTCASE.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasRTCASE() ) {
                chkRTCASE.setEnabled( false );
            }

            if( CurMech.GetBaseLoadout().HasHDCASEII() ) {
                chkHDCASE2.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasCTCASEII() ) {
                chkCTCASE2.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasLTCASEII() ) {
                chkLTCASE2.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasRTCASEII() ) {
                chkRTCASE2.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasLACASEII() ) {
                chkLACASE2.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasRACASEII() ) {
                chkRACASE2.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasLLCASEII() ) {
                chkLLCASE2.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasRLCASEII() ) {
                chkRLCASE2.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasHDTurret() ) {
                chkHDTurret.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasLTTurret() ) {
                chkLTTurret.setEnabled( false );
            }
            if( CurMech.GetBaseLoadout().HasRTTurret() ) {
                chkRTTurret.setEnabled( false );
            }
        } else {
            try {
                if( ! chkNullSig.isEnabled() ) { CurMech.SetNullSig( false ); }
                if( ! chkVoidSig.isEnabled() ) { CurMech.SetVoidSig( false ); }
                if( ! chkBSPFD.isEnabled() ) { CurMech.SetBlueShield( false ); }
                if( ! chkCLPS.isEnabled() ) { CurMech.SetChameleon( false ); }
                if( ! chkEnviroSealing.isEnabled() ) { CurMech.SetEnviroSealing( false ); }
                if( ! chkLegAES.isEnabled() ) { CurMech.SetLegAES( false, null ); }
                if( ! chkRAAES.isEnabled() ) { CurMech.SetRAAES( false, -1 ); }
                if( ! chkLAAES.isEnabled() ) { CurMech.SetLAAES( false, -1 ); }
                if( ! chkCommandConsole.isEnabled() ) { CurMech.SetCommandConsole( false ); }
                if( ! chkFHES.isEnabled() ) { CurMech.SetFHES( false ); }
                if( ! chkTracks.isEnabled() ) { CurMech.SetTracks( false ); }
            } catch( Exception e ) {
                // we should never get this, but report it if we do
                Media.Messager( this, e.getMessage() );
            }
        }
    }

    private void RecalcGyro() {
        // changes the armor type.
        String OldVal = BuildLookupName( CurMech.GetGyro().GetCurrentState() );
        String LookupVal = (String) cmbGyroType.getSelectedItem();
        if( OldVal.equals( LookupVal ) ) { return; }
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        try {
            CurMech.Visit( v );
            if ( CurMech.GetCockpit().RequiresGyro() && CurMech.GetGyro().NumCrits() == 0)
                throw new Exception( "The selected cockpit requires a gyro." );
        } catch( Exception e ) {
            v = (ifVisitor) CurMech.Lookup( OldVal );
            try {
                Media.Messager( this, "The new gyro type is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous gyro type." );
                CurMech.Visit( v );
                cmbGyroType.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old gyro type:\n" + e.getMessage() + "\nStarting over with a new 'Mech.  Sorry." );
                GetNewMech();
            }
        }
    }

    private void RecalcCockpit() {
        // recalculates the cockpit on the mech
        // get the current lookup in case we can't fit the new one
        String OldVal = CurMech.GetCockpit().LookupName();
        String LookupVal = (String) cmbCockpitType.getSelectedItem();
        if( OldVal.equals( LookupVal ) ) { return; }
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        
        try {
            CurMech.Visit( v );
        } catch( Exception e ) {
            v = (ifVisitor) CurMech.Lookup( OldVal );
            try {
                Media.Messager( this, "The new cockpit type is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous cockpit type." );
                CurMech.Visit( v );
                cmbCockpitType.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old cockpit type:\n" + e.getMessage() + "\nStarting over with a new 'Mech.  Sorry." );
                GetNewMech();
            }
        }

        if ( !CurMech.GetGyro().LookupName().equals(cmbGyroType.getSelectedItem().toString()) )
            cmbGyroType.setSelectedItem(CurMech.GetGyro().LookupName());
        
        // check the command console and ejection seat
        if( CurMech.GetCockpit().CanUseCommandConsole() && CommonTools.IsAllowed( CurMech.GetCommandConsole().GetAvailability(), CurMech ) ) {
            chkCommandConsole.setEnabled( true );
            chkCommandConsole.setSelected(CurMech.HasCommandConsole());
        } else {
            chkCommandConsole.setEnabled( false );
            chkCommandConsole.setSelected( false );
        }
        if( CurMech.CanUseFHES() && CommonTools.IsAllowed( CurMech.GetFHESAC(), CurMech ) ) {
            chkFHES.setEnabled( true );
        } else {
            chkFHES.setSelected( false );
            chkFHES.setSelected( false );
        }
        if( CurMech.GetCockpit().IsTorsoMounted() )
        {
            chkEjectionSeat.setEnabled( false );
            chkEjectionSeat.setSelected( false );
        }
        if( CurMech.IsIndustrialmech() ) {
            chkEjectionSeat.setEnabled( true );
        } else {
            chkEjectionSeat.setEnabled( false );
            chkEjectionSeat.setSelected( false );
        }
        // remove the head turret if it's there.
        if( CurMech.GetLoadout().HasHDTurret() ) {
            try {
                CurMech.GetLoadout().SetHDTurret( false, -1 );
            } catch( Exception e ) {
                Media.Messager( "Fatal error trying to remove head turret.\nRestarting with new 'Mech.  Sorry." );
                GetNewMech();
                return;
            }
        }
        chkHDTurret.setSelected( false );
        chkHDTurret.setEnabled( false );
    }

    private void RecalcEnhancements() {
        // recalculates the enhancements on the mech
        String OldVal = BuildLookupName( CurMech.GetPhysEnhance().GetCurrentState() );
        String LookupVal = (String) cmbPhysEnhance.getSelectedItem();
        if( OldVal.equals( LookupVal ) ) { return; }
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        try {
            CurMech.Visit( v );
        } catch( Exception e ) {
            v = (ifVisitor) CurMech.Lookup( OldVal );
            try {
                Media.Messager( this, "The new enhancement type is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous enhancement." );
                CurMech.Visit( v );
                cmbPhysEnhance.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old enhancement:\n" + e.getMessage() + "\nStarting over with a new 'Mech.  Sorry." );
                GetNewMech();
            }
        }
    }

    private void RecalcJumpJets() {
        // recalculates the jump jets if things have changed.
        String OldVal = CurMech.GetJumpJets().LookupName();
        String LookupVal = (String) cmbJumpJetType.getSelectedItem();
        if( LookupVal == null ) { return; }
        if( OldVal.equals( LookupVal ) ) { return; }
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        try {
            CurMech.Visit( v );
        } catch( Exception e ) {
            v = (ifVisitor) CurMech.Lookup( OldVal );
            try {
                Media.Messager( this, "The new jump jet type is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous jump jet type." );
                CurMech.Visit( v );
                cmbJumpJetType.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old jump jets:\n" + e.getMessage() + "\nStarting over with a new 'Mech.  Sorry." );
                GetNewMech();
                return;
            }
        }
        FixJJSpinnerModel();
    }

    private void RecalcHeatSinks() {
        // recalculate the heat sinks based on what is selected.
        String OldVal = BuildLookupName( CurMech.GetHeatSinks().GetCurrentState() );
        String LookupVal = (String) cmbHeatSinkType.getSelectedItem();
        if( OldVal.equals( LookupVal ) ) { return; }
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        try {
            CurMech.Visit( v );
        } catch( Exception e ) {
            v = (ifVisitor) CurMech.Lookup( OldVal );
            try {
                Media.Messager( this, "The new heat sink type is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous heat sink type." );
                CurMech.Visit( v );
                cmbHeatSinkType.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old heat sinks:\n" + e.getMessage() + "\nStarting over with a new 'Mech.  Sorry." );
                GetNewMech();
                return;
            }
        }
        FixHeatSinkSpinnerModel();
    }

    private void RecalcIntStruc() {
        // recalculates the internal structure if anything happened.
        String OldVal = BuildLookupName( CurMech.GetIntStruc().GetCurrentState() );
        String LookupVal = (String) cmbInternalType.getSelectedItem();
        if( OldVal.equals( LookupVal ) ) { return; }
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        try {
            CurMech.Visit( v );
        } catch( Exception e ) {
            v = (ifVisitor) CurMech.Lookup( OldVal );
            try {
                Media.Messager( this, "The new internal structure is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous internal structure." );
                CurMech.Visit( v );
                cmbInternalType.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old internal structure:\n" + e.getMessage() + "\nStarting over with a new 'Mech.  Sorry." );
                GetNewMech();
            }
        }
    }

    private void RecalcEngine() {
        // first, get the current number of free heat sinks
        int OldFreeHS = CurMech.GetEngine().FreeHeatSinks();

        // changes the engine type.  Changing the type does not change the rating
        // which makes our job here easier.
        String OldVal = BuildLookupName( CurMech.GetEngine().GetCurrentState() );
        String LookupVal = (String) cmbEngineType.getSelectedItem();
        if( OldVal.equals( LookupVal ) ) { return; }
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        try {
            CurMech.Visit( v );
        } catch( Exception e ) {
            v = (ifVisitor) CurMech.Lookup( OldVal );
            try {
                Media.Messager( this, "The new engine type is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous engine." );
                CurMech.Visit( v );
                cmbEngineType.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old engine:\n" + e.getMessage() + "\nStarting over with a new 'Mech.  Sorry." );
                GetNewMech();
                return;
            }
        }

        // now that the new engine is in, check the number of free sinks and act
        // accordingly
        if( CurMech.GetEngine().FreeHeatSinks() != OldFreeHS ) {
            // set the current number of heat sinks to the new free heat sinks
            CurMech.GetHeatSinks().SetNumHS( CurMech.GetEngine().FreeHeatSinks() );
            // redo the heat sinks because the engine affects them
            CurMech.GetHeatSinks().ReCalculate();
            spnNumberOfHS.setModel( new javax.swing.SpinnerNumberModel(
                CurMech.GetHeatSinks().GetNumHS(), CurMech.GetEngine().FreeHeatSinks(), 65, 1) );

            ((JSpinner.DefaultEditor)spnNumberOfHS.getEditor()).getTextField().addFocusListener(spinners);
        }

        // see if we should enable the Power Amplifier display
        if( CurMech.GetEngine().IsNuclear() ) {
            lblSumPAmps.setVisible( false );
            txtSumPAmpsTon.setVisible( false );
            txtSumPAmpsACode.setVisible( false );
        } else {
            lblSumPAmps.setVisible( true );
            txtSumPAmpsTon.setVisible( true );
            txtSumPAmpsACode.setVisible( true );
        }
    }

    private void RecalcArmor() {
        // changes the armor type.
        String OldVal = BuildLookupName( CurMech.GetArmor().GetCurrentState() );
        String LookupVal = (String) cmbArmorType.getSelectedItem();
        if( OldVal.equals( LookupVal ) ) { return; }
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        try {
            CurMech.Visit( v );
        } catch( Exception e ) {
            v = (ifVisitor) CurMech.Lookup( OldVal );
            try {
                Media.Messager( this, "The new armor type is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous armor." );
                CurMech.Visit( v );
                cmbArmorType.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old armor:\n" + e.getMessage() + "\nStarting over with a new 'Mech.  Sorry." );
                GetNewMech();
            }
        }
        if( CurMech.GetArmor().IsStealth() ) {
            if( ! AddECM() ) {
                v = (ifVisitor) CurMech.Lookup( OldVal );
                try {
                    Media.Messager( this, "No ECM Suite was available for this armor type!\nReverting to the previous armor." );
                    CurMech.Visit( v );
                    cmbArmorType.setSelectedItem( OldVal );
                } catch( Exception e ) {
                    // wow, second one?  Get a new 'Mech.
                    Media.Messager( this, "Fatal error while attempting to revert to the old armor:\n" + e.getMessage() + "\nStarting over with a new 'Mech.  Sorry." );
                    GetNewMech();
                }
            }
        }
        SetPatchworkArmor();
    }

    private void SetPatchworkArmor() {
        if( CurMech.GetArmor().IsPatchwork() ) {
            pnlPatchworkChoosers.setVisible( true );
            BuildPatchworkChoosers();
            if( CurMech.IsQuad() ) {
                lblPWLALoc.setText( "FLL Armor: " );
                lblPWRALoc.setText( "FRL Armor: " );
                lblPWLLLoc.setText( "RLL Armor: " );
                lblPWRLLoc.setText( "RRL Armor: " );
            } else {
                lblPWLALoc.setText( "LA Armor: " );
                lblPWRALoc.setText( "RA Armor: " );
                lblPWLLLoc.setText( "LL Armor: " );
                lblPWRLLoc.setText( "RL Armor: " );
            }
            cmbPWHDType.setSelectedItem( BuildLookupName( (ifState) CurMech.GetArmor().GetHDArmorType() ) );
            cmbPWCTType.setSelectedItem( BuildLookupName( (ifState) CurMech.GetArmor().GetCTArmorType() ) );
            cmbPWLTType.setSelectedItem( BuildLookupName( (ifState) CurMech.GetArmor().GetLTArmorType() ) );
            cmbPWRTType.setSelectedItem( BuildLookupName( (ifState) CurMech.GetArmor().GetRTArmorType() ) );
            cmbPWLAType.setSelectedItem( BuildLookupName( (ifState) CurMech.GetArmor().GetLAArmorType() ) );
            cmbPWRAType.setSelectedItem( BuildLookupName( (ifState) CurMech.GetArmor().GetRAArmorType() ) );
            cmbPWLLType.setSelectedItem( BuildLookupName( (ifState) CurMech.GetArmor().GetLLArmorType() ) );
            cmbPWRLType.setSelectedItem( BuildLookupName( (ifState) CurMech.GetArmor().GetRLArmorType() ) );
        } else {
            pnlPatchworkChoosers.setVisible( false );
        }
    }

    private void RecalcPatchworkArmor( int Loc ) {
        VArmorSetPatchworkLocation LCVis = new VArmorSetPatchworkLocation();
        LCVis.SetLocation( Loc );
        if( CurMech.GetBaseTechbase() == AvailableCode.TECH_CLAN ) {
                LCVis.SetClan( false );
        }
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                LCVis.SetPatchworkType( (String) cmbPWHDType.getSelectedItem() );
                break;
            case LocationIndex.MECH_LOC_CT:
                LCVis.SetPatchworkType( (String) cmbPWCTType.getSelectedItem() );
                break;
            case LocationIndex.MECH_LOC_LT:
                LCVis.SetPatchworkType( (String) cmbPWLTType.getSelectedItem() );
                break;
            case LocationIndex.MECH_LOC_RT:
                LCVis.SetPatchworkType( (String) cmbPWRTType.getSelectedItem() );
                break;
            case LocationIndex.MECH_LOC_LA:
                LCVis.SetPatchworkType( (String) cmbPWLAType.getSelectedItem() );
                break;
            case LocationIndex.MECH_LOC_RA:
                LCVis.SetPatchworkType( (String) cmbPWRAType.getSelectedItem() );
                break;
            case LocationIndex.MECH_LOC_LL:
                LCVis.SetPatchworkType( (String) cmbPWLLType.getSelectedItem() );
                break;
            case LocationIndex.MECH_LOC_RL:
                LCVis.SetPatchworkType( (String) cmbPWRLType.getSelectedItem() );
                break;
        }
        try {
            LCVis.Visit( CurMech );
        } catch( Exception e ) {
            Media.Messager( this, e.getMessage() );
            switch( Loc ) {
                case LocationIndex.MECH_LOC_HD:
                    cmbPWHDType.setSelectedItem( CurMech.GetArmor().GetHDArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_CT:
                    cmbPWCTType.setSelectedItem( CurMech.GetArmor().GetCTArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_LT:
                    cmbPWLTType.setSelectedItem( CurMech.GetArmor().GetLTArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_RT:
                    cmbPWRTType.setSelectedItem( CurMech.GetArmor().GetRTArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_LA:
                    cmbPWLAType.setSelectedItem( CurMech.GetArmor().GetLAArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_RA:
                    cmbPWRAType.setSelectedItem( CurMech.GetArmor().GetRAArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_LL:
                    cmbPWLLType.setSelectedItem( CurMech.GetArmor().GetLLArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_RL:
                    cmbPWRLType.setSelectedItem( CurMech.GetArmor().GetRLArmorType().LookupName() );
                    break;
            }
        }
    }

    private void RecalcEquipment() {
        // recalculates the equipment if anything changes
        boolean clan = false;
        switch( CurMech.GetTechbase() ) {
            case AvailableCode.TECH_CLAN: case AvailableCode.TECH_BOTH:
                // this is the default value to use assuming that during mixed
                // tech operations the user will use the best.
                clan = true;
        }
        if( chkCTCASE.isSelected() ) {
            try {
                CurMech.AddCTCase();
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkCTCASE.setSelected( false );
            }
        }
        if( chkLTCASE.isSelected() ) {
            try {
                CurMech.AddLTCase();
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkLTCASE.setSelected( false );
            }
        }
        if( chkRTCASE.isSelected() ) {
            try {
                CurMech.AddRTCase();
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkRTCASE.setSelected( false );
            }
        }

        if( chkHDCASE2.isSelected() ) {
            try {
                if( ! CurMech.GetLoadout().HasHDCASEII() ) {
                    CurMech.GetLoadout().SetHDCASEII( true, -1, clan );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkHDCASE2.setSelected( false );
            }
        }
        if( chkCTCASE2.isSelected() ) {
            try {
                if( ! CurMech.GetLoadout().HasCTCASEII() ) {
                    CurMech.GetLoadout().SetCTCASEII( true, -1, clan );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkCTCASE2.setSelected( false );
            }
        }
        if( chkLTCASE2.isSelected() ) {
            try {
                if( ! CurMech.GetLoadout().HasLTCASEII() ) {
                    CurMech.GetLoadout().SetLTCASEII( true, -1, clan );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkLTCASE2.setSelected( false );
            }
        }
        if( chkRTCASE2.isSelected() ) {
            try {
                if( ! CurMech.GetLoadout().HasRTCASEII() ) {
                    CurMech.GetLoadout().SetRTCASEII( true, -1, clan );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkRTCASE2.setSelected( false );
            }
        }
        if( chkLACASE2.isSelected() ) {
            try {
                if( ! CurMech.GetLoadout().HasLACASEII() ) {
                    CurMech.GetLoadout().SetLACASEII( true, -1, clan );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkLACASE2.setSelected( false );
            }
        }
        if( chkRACASE2.isSelected() ) {
            try {
                if( ! CurMech.GetLoadout().HasRACASEII() ) {
                    CurMech.GetLoadout().SetRACASEII( true, -1, clan );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkRACASE2.setSelected( false );
            }
        }
        if( chkLLCASE2.isSelected() ) {
            try {
                if( ! CurMech.GetLoadout().HasLLCASEII() ) {
                    CurMech.GetLoadout().SetLLCASEII( true, -1, clan );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkLLCASE2.setSelected( false );
            }
        }
        if( chkRLCASE2.isSelected() ) {
            try {
                if( ! CurMech.GetLoadout().HasRLCASEII() ) {
                    CurMech.GetLoadout().SetRLCASEII( true, -1, clan );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkRLCASE2.setSelected( false );
            }
        }
    }

    private void CheckOmnimech() {
        // deals with the omnimech checkbox if needed
        if( CommonTools.IsAllowed( CurMech.GetOmniMechAvailability(), CurMech ) ) {
            chkOmnimech.setEnabled( true );
        } else {
            chkOmnimech.setEnabled( false );
            chkOmnimech.setSelected( false );
        }

        // now let's ensure that all the omni controls are enabled or disabled
        // as appropriate
        if( chkOmnimech.isEnabled() ) {
            if( chkOmnimech.isSelected() ) {
                btnLockChassis.setEnabled( true );
            } else {
                btnLockChassis.setEnabled( false );
            }
        } else {
            btnLockChassis.setEnabled( false );
        }
    }

    private void SaveOmniFluffInfo() {
        if( SetSource ) {
            CurMech.SetSource( txtSource.getText() );
            CurMech.SetEra( cmbMechEra.getSelectedIndex() );
            CurMech.SetProductionEra( cmbProductionEra.getSelectedIndex() ) ;
            CurMech.SetYearRestricted( chkYearRestrict.isSelected() );
            try {
                CurMech.SetYear( Integer.parseInt( txtProdYear.getText() ), chkYearRestrict.isSelected() );
            } catch( Exception e ) {
                // nothing really to be done, set it to a default.
                switch( cmbMechEra.getSelectedIndex() ) {
                    case AvailableCode.ERA_STAR_LEAGUE:
                        CurMech.SetYear( 2750, false );
                        break;
                    case AvailableCode.ERA_SUCCESSION:
                        CurMech.SetYear( 3025, false );
                        break;
                    case AvailableCode.ERA_CLAN_INVASION:
                        CurMech.SetYear( 3070, false );
                        break;
                    case AvailableCode.ERA_DARK_AGES:
                        CurMech.SetYear( 3132, false );
                        break;
                    case AvailableCode.ERA_ALL:
                        CurMech.SetYear( 0, false );
                        break;
                }
            }
        }
    }

    private void LoadOmniFluffInfo() {
        cmbRulesLevel.setSelectedIndex( CurMech.GetRulesLevel() );
        cmbMechEra.setSelectedIndex( CurMech.GetEra() );
        cmbProductionEra.setSelectedIndex( CurMech.GetProductionEra() );
        txtSource.setText( CurMech.GetSource() );
        txtProdYear.setText( "" + CurMech.GetYear() );
        BuildTechBaseSelector();
    }

    private void RefreshInternalPoints() {
        if( Prefs.getBoolean( "UseMaxArmorInstead", false ) ) {
            lblHDHeader.setText( "Max" );
            lblCTHeader.setText( "Max" );
            lblLTHeader.setText( "Max" );
            lblRTHeader.setText( "Max" );
            lblLAHeader.setText( "Max" );
            lblRAHeader.setText( "Max" );
            lblLLHeader.setText( "Max" );
            lblRLHeader.setText( "Max" );
            lblHDIntPts.setText( "" + CurMech.GetIntStruc().GetHeadPoints() * 3 );
            lblCTIntPts.setText( "" + CurMech.GetIntStruc().GetCTPoints() * 2 );
            lblLTIntPts.setText( "" + CurMech.GetIntStruc().GetSidePoints() * 2 );
            lblRTIntPts.setText( "" + CurMech.GetIntStruc().GetSidePoints() * 2 );
            lblLAIntPts.setText( "" + CurMech.GetIntStruc().GetArmPoints() * 2 );
            lblRAIntPts.setText( "" + CurMech.GetIntStruc().GetArmPoints() * 2 );
            lblLLIntPts.setText( "" + CurMech.GetIntStruc().GetLegPoints() * 2 );
            lblRLIntPts.setText( "" + CurMech.GetIntStruc().GetLegPoints() * 2 );
        } else {
            lblHDHeader.setText( "Internal" );
            lblCTHeader.setText( "Internal" );
            lblLTHeader.setText( "Internal" );
            lblRTHeader.setText( "Internal" );
            lblLAHeader.setText( "Internal" );
            lblRAHeader.setText( "Internal" );
            lblLLHeader.setText( "Internal" );
            lblRLHeader.setText( "Internal" );
            lblHDIntPts.setText( "" + CurMech.GetIntStruc().GetHeadPoints() );
            lblCTIntPts.setText( "" + CurMech.GetIntStruc().GetCTPoints() );
            lblLTIntPts.setText( "" + CurMech.GetIntStruc().GetSidePoints() );
            lblRTIntPts.setText( "" + CurMech.GetIntStruc().GetSidePoints() );
            lblLAIntPts.setText( "" + CurMech.GetIntStruc().GetArmPoints() );
            lblRAIntPts.setText( "" + CurMech.GetIntStruc().GetArmPoints() );
            lblLLIntPts.setText( "" + CurMech.GetIntStruc().GetLegPoints() );
            lblRLIntPts.setText( "" + CurMech.GetIntStruc().GetLegPoints() );
        }
    }

    private void RefreshSummary() {
        // refreshes the display completely using info from the mech.
        txtSumIntTon.setText( "" + CurMech.GetIntStruc().GetTonnage() );
        txtSumEngTon.setText( "" + CurMech.GetEngine().GetTonnage() );
        txtSumGyrTon.setText( "" + CurMech.GetGyro().GetTonnage() );
        txtSumCocTon.setText( "" + CurMech.GetCockpit().GetTonnage() );
        txtSumEnhTon.setText( "" + CurMech.GetPhysEnhance().GetTonnage() );
        txtSumHSTon.setText( "" + CurMech.GetHeatSinks().GetTonnage() );
        txtSumJJTon.setText( "" + CurMech.GetJumpJets().GetTonnage() );
        txtSumArmorTon.setText( "" + CurMech.GetArmor().GetTonnage() );
        txtSumPAmpsTon.setText( "" + CurMech.GetLoadout().GetPowerAmplifier().GetTonnage() );
        txtSumIntCrt.setText( "" + CurMech.GetIntStruc().NumCrits() );
        txtSumEngCrt.setText( "" + CurMech.GetEngine().ReportCrits() );
        txtSumGyrCrt.setText( "" + CurMech.GetGyro().NumCrits() );
        txtSumCocCrt.setText( "" + CurMech.GetCockpit().ReportCrits() );
        txtSumEnhCrt.setText( "" + CurMech.GetPhysEnhance().NumCrits() );
        txtSumHSCrt.setText( "" + CurMech.GetHeatSinks().NumCrits() );
        txtSumJJCrt.setText( "" + CurMech.GetJumpJets().ReportCrits() );
        txtSumArmorCrt.setText( "" + CurMech.GetArmor().NumCrits() );
        txtSumIntACode.setText( CurMech.GetIntStruc().GetAvailability().GetBestCombinedCode() );
        txtSumEngACode.setText( CurMech.GetEngine().GetAvailability().GetBestCombinedCode() );
        txtSumGyrACode.setText( CurMech.GetGyro().GetAvailability().GetBestCombinedCode() );
        txtSumCocACode.setText( CurMech.GetCockpit().GetAvailability().GetBestCombinedCode() );
        txtSumHSACode.setText( CurMech.GetHeatSinks().GetAvailability().GetBestCombinedCode() );
        txtSumEnhACode.setText( CurMech.GetPhysEnhance().GetAvailability().GetBestCombinedCode() );
        txtSumJJACode.setText( CurMech.GetJumpJets().GetAvailability().GetBestCombinedCode() );
        txtSumPAmpsACode.setText( CurMech.GetLoadout().GetPowerAmplifier().GetAvailability().GetBestCombinedCode() );

        // added for the armor pane
        lblArmorPoints.setText( CurMech.GetArmor().GetArmorValue() + " of " + CurMech.GetArmor().GetMaxArmor() + " Armor Points" );
        lblArmorCoverage.setText( CurMech.GetArmor().GetCoverage() + "% Coverage" );
        lblArmorTonsWasted.setText( CurMech.GetArmor().GetWastedTonnage() + " Tons Wasted" );
        lblAVInLot.setText( CurMech.GetArmor().GetWastedAV() + " Points Left In This 1/2 Ton Lot" );

        // added for Battleforce pane
        BattleForceStats bfs = new BattleForceStats(CurMech);

        lblBFMV.setText( bfs.getMovement() );
        lblBFWt.setText( bfs.getWeight() + "" );
        lblBFArmor.setText( bfs.getArmor() + "" );
        lblBFStructure.setText( bfs.getInternal() + "" );
        lblBFPoints.setText("" + bfs.getPointValue() );

        //int [] BFdmg = CurMech.GetBFDamage( bfs );
        lblBFShort.setText("" + bfs.getShort() );
        lblBFMedium.setText("" + bfs.getMedium() );
        lblBFLong.setText("" + bfs.getLong() );
        lblBFExtreme.setText("" + bfs.getExtreme() );
        lblBFOV.setText("" + bfs.getOverheat() );

        lblBFSA.setText( bfs.getAbilitiesString() );

        jTextAreaBFConversion.setText( bfs.getBFConversionData() );
    }

    public void RefreshInfoPane() {
        // refreshes the information pane at the bottom of the screen
        // set the colors
        if( CurMech.GetCurrentTons() > CurMech.GetTonnage() ) {
            txtInfoTonnage.setForeground( RedCol );
            txtInfoFreeTons.setForeground( RedCol );
        } else {
            txtInfoTonnage.setForeground( GreenCol );
            txtInfoFreeTons.setForeground( GreenCol );
        }
        if( CurMech.GetLoadout().FreeCrits() - CurMech.GetLoadout().UnplacedCrits() < 0 ) {
            txtInfoFreeCrits.setForeground( RedCol );
            txtInfoUnplaced.setForeground( RedCol );
        } else {
            txtInfoFreeCrits.setForeground( GreenCol );
            txtInfoUnplaced.setForeground( GreenCol );
        }
        // fill in the info
        if( CurMech.UsingFractionalAccounting() ) {
            txtInfoTonnage.setText( "Tons: " + CommonTools.RoundFractionalTons( CurMech.GetCurrentTons() ) );
            txtInfoFreeTons.setText( "Free Tons: " + CommonTools.RoundFractionalTons( CurMech.GetTonnage() - CurMech.GetCurrentTons() ) );
        } else {
            txtInfoTonnage.setText( "Tons: " + CurMech.GetCurrentTons() );
            txtInfoFreeTons.setText( "Free Tons: " + ( CurMech.GetTonnage() - CurMech.GetCurrentTons() ) );
        }
        txtInfoMaxHeat.setText( "Max Heat: " + CurMech.GetMaxHeat() );
        txtInfoHeatDiss.setText( "Heat Dissipation: " + CurMech.GetHeatSinks().TotalDissipation() );
        txtInfoFreeCrits.setText( "Free Crits: " + CurMech.GetLoadout().FreeCrits() );
        txtInfoUnplaced.setText( "Unplaced Crits: " + CurMech.GetLoadout().UnplacedCrits() );
        txtInfoBattleValue.setText( "BV: " + String.format( "%1$,d", CurMech.GetCurrentBV() ) );
        txtInfoCost.setText( "Cost: " + String.format( "%1$,.0f", Math.floor( CurMech.GetTotalCost() + 0.5f ) ) );
        txtEngineRating.setText( "" + CurMech.GetEngine().GetRating() );

        // fill in the movement summary
        String temp = "Max W/R/J/B: ";
        temp += CurMech.GetAdjustedWalkingMP( false, true ) + "/";
        temp += CurMech.GetAdjustedRunningMP( false, true ) + "/";
        temp += CurMech.GetAdjustedJumpingMP( false ) + "/";
        temp += CurMech.GetAdjustedBoosterMP( false );
        lblMoveSummary.setText( temp );

        // because the ArrayList changes, we'll have to load up the Crits to Place list
        lstCritsToPlace.setListData( CurMech.GetLoadout().GetQueue().toArray() );
        lstCritsToPlace.repaint();

        // repaint the rest of the loadout list
        lstHDCrits.repaint();
        lstCTCrits.repaint();
        lstLTCrits.repaint();
        lstRTCrits.repaint();
        lstLACrits.repaint();
        lstRACrits.repaint();
        lstLLCrits.repaint();
        lstRLCrits.repaint();
        lstSelectedEquipment.repaint();
        javax.swing.table.AbstractTableModel m = (javax.swing.table.AbstractTableModel) tblWeaponManufacturers.getModel();
        m.fireTableDataChanged();

        CheckEquipment();

        UpdateBasicChart();
    }

    public void QuickSave() {
        File saveFile = GetSaveFile( "ssw", Prefs.get( "LastOpenDirectory", "" ), true, false );
        if ( saveFile != null ) {
            // save the mech to XML in the current location
            String curLoadout = CurMech.GetLoadout().GetName();
            MechWriter XMLw = new MechWriter( CurMech );
            try {
                XMLw.WriteXML( saveFile.getCanonicalPath() );
                CurMech.SetCurLoadout(curLoadout);
            } catch( IOException e ) {
                return;
            }
        } else {
            mnuSaveActionPerformed(null);
        }
    }

    private void CheckEquipment() {
        // consolidating some code here.
        if( CurMech.UsingArtemisIV() ) {
            chkFCSAIV.setSelected( true );
        } else {
            chkFCSAIV.setSelected( false );
        }
        if( CurMech.UsingArtemisV() ) {
            chkFCSAV.setSelected( true );
        } else {
            chkFCSAV.setSelected( false );
        }
        if( CurMech.UsingApollo() ) {
            chkFCSApollo.setSelected( true );
        } else {
            chkFCSApollo.setSelected( false );
        }

        if( CurMech.UsingTC() ) {
            chkUseTC.setSelected( true );
        } else {
            chkUseTC.setSelected( false );
        }

        // if anything ditched the CASE systems, we'll want to know.
        if( CurMech.HasCTCase() ) {
            chkCTCASE.setSelected( true );
        } else {
            chkCTCASE.setSelected( false );
        }
        if( CurMech.HasLTCase() ) {
            chkLTCASE.setSelected( true );
        } else {
            chkLTCASE.setSelected( false );
        }
        if( CurMech.HasRTCase() ) {
            chkRTCASE.setSelected( true );
        } else {
            chkRTCASE.setSelected( false );
        }

        if( CurMech.GetLoadout().HasHDCASEII() ) {
            chkHDCASE2.setSelected( true );
        } else {
            chkHDCASE2.setSelected( false );
        }
        if( CurMech.GetLoadout().HasCTCASEII() ) {
            chkCTCASE2.setSelected( true );
        } else {
            chkCTCASE2.setSelected( false );
        }
        if( CurMech.GetLoadout().HasLTCASEII() ) {
            chkLTCASE2.setSelected( true );
        } else {
            chkLTCASE2.setSelected( false );
        }
        if( CurMech.GetLoadout().HasRTCASEII() ) {
            chkRTCASE2.setSelected( true );
        } else {
            chkRTCASE2.setSelected( false );
        }
        if( CurMech.GetLoadout().HasLACASEII() ) {
            chkLACASE2.setSelected( true );
        } else {
            chkLACASE2.setSelected( false );
        }
        if( CurMech.GetLoadout().HasRACASEII() ) {
            chkRACASE2.setSelected( true );
        } else {
            chkRACASE2.setSelected( false );
        }
        if( CurMech.GetLoadout().HasLLCASEII() ) {
            chkLLCASE2.setSelected( true );
        } else {
            chkLLCASE2.setSelected( false );
        }
        if( CurMech.GetLoadout().HasRLCASEII() ) {
            chkRLCASE2.setSelected( true );
        } else {
            chkRLCASE2.setSelected( false );
        }

        if( CurMech.GetLoadout().HasHDTurret() ) {
            chkHDTurret.setSelected( true );
        } else {
            chkHDTurret.setSelected( false );
        }
        if( CurMech.GetLoadout().HasLTTurret() ) {
            chkLTTurret.setSelected( true );
        } else {
            chkLTTurret.setSelected( false );
        }
        if( CurMech.GetLoadout().HasRTTurret() ) {
            chkRTTurret.setSelected( true );
        } else {
            chkRTTurret.setSelected( false );
        }

        if( CurMech.GetLoadout().HasSupercharger() ) {
            chkSupercharger.setSelected( true );
            cmbSCLoc.setSelectedItem( FileCommon.EncodeLocation( CurMech.GetLoadout().Find( CurMech.GetLoadout().GetSupercharger() ), false ) );
        } else {
            chkSupercharger.setSelected( false );
        }
        if( CurMech.GetLoadout().IsUsingClanCASE() ) {
            chkClanCASE.setSelected( true );
        } else {
            chkClanCASE.setSelected( false );
        }

        // check the actuators as well.
        if( ! CurMech.IsQuad() ) {
            if( CurMech.GetActuators().LeftLowerInstalled() ) {
                chkLALowerArm.setSelected( true );
            } else {
                chkLALowerArm.setSelected( false );
            }
            if( CurMech.GetActuators().LeftHandInstalled() ) {
                chkLAHand.setSelected( true );
            } else {
                chkLAHand.setSelected( false );
            }
            if( CurMech.GetActuators().RightLowerInstalled() ) {
                chkRALowerArm.setSelected( true );
            } else {
                chkRALowerArm.setSelected( false );
            }
            if( CurMech.GetActuators().RightHandInstalled() ) {
                chkRAHand.setSelected( true );
            } else {
                chkRAHand.setSelected( false );
            }
        }
    }

    private void CheckAES() {
        // if the tonnage or motive type changes, we need to do this.
        if( chkLegAES.isSelected() ) {
            if( CurMech.GetTonnage() > 55 ) {
                // too big to have leg AES
                chkLegAES.setSelected( false );
                try {
                    CurMech.SetLegAES( false, null );
                } catch( Exception e ) {
                    System.err.println( e.getMessage() );
                }
            } else {
                // remove and add the systems back in
                try {
                    CurMech.SetLegAES( false, null );
                    CurMech.SetLegAES( true, null );
                } catch( Exception e ) {
                    chkLegAES.setSelected( false );
                }
            }
        }
        if( chkRAAES.isSelected() ) {
            if( CurMech.IsQuad() ) {
                chkRAAES.setSelected( false );
            } else {
                int index = CurMech.GetLoadout().FindIndex( CurMech.GetRAAES() ).Index;
                try { 
                    CurMech.SetRAAES( false, -1 );
                    CurMech.SetRAAES( true, index );
                } catch( Exception e ) {
                    chkRAAES.setSelected( false );
                }
            }
        }
        if( chkLAAES.isSelected() ) {
            if( CurMech.IsQuad() ) {
                chkLAAES.setSelected( false );
            } else {
                int index = CurMech.GetLoadout().FindIndex( CurMech.GetLAAES() ).Index;
                try { 
                    CurMech.SetLAAES( false, -1 );
                    CurMech.SetLAAES( true, index );
                } catch( Exception e ) {
                    chkLAAES.setSelected( false );
                }
            }
        }
    }

    private void FixArmorSpinners() {
        // fixes the armor spinners to match the new tonnage / motive type
        MechArmor a = CurMech.GetArmor();
        spnHDArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_HD ), 0, 9, 1) );
        spnCTArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_CT ), 0, a.GetLocationMax( LocationIndex.MECH_LOC_CT ), 1) );
        spnLTArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_LT ), 0, a.GetLocationMax( LocationIndex.MECH_LOC_LT ), 1) );
        spnRTArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_RT ), 0, a.GetLocationMax( LocationIndex.MECH_LOC_RT ), 1) );
        spnLAArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_LA ), 0, a.GetLocationMax( LocationIndex.MECH_LOC_LA ), 1) );
        spnRAArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_RA ), 0, a.GetLocationMax( LocationIndex.MECH_LOC_RA ), 1) );
        spnLLArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_LL ), 0, a.GetLocationMax( LocationIndex.MECH_LOC_LL ), 1) );
        spnRLArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_RL ), 0, a.GetLocationMax( LocationIndex.MECH_LOC_RL ), 1) );
        spnCTRArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_CTR ), 0, a.GetLocationMax( LocationIndex.MECH_LOC_CT ), 1) );
        spnLTRArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_LTR ), 0, a.GetLocationMax( LocationIndex.MECH_LOC_LT ), 1) );
        spnRTRArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.MECH_LOC_RTR ), 0, a.GetLocationMax( LocationIndex.MECH_LOC_RT ), 1) );

        //Setup Spinner focus
        ((JSpinner.DefaultEditor)spnHDArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnCTArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnCTRArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRTArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRTRArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnLTArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnLTRArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRAArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRLArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnLAArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnLLArmor.getEditor()).getTextField().addFocusListener(spinners);
    }

    private void SaveSelections() {
        // saves the current GUI selections
        Selections[0] = BuildLookupName( CurMech.GetIntStruc().GetCurrentState() );
        Selections[1] = BuildLookupName( CurMech.GetEngine().GetCurrentState() );
        Selections[2] = BuildLookupName( CurMech.GetGyro().GetCurrentState() );
        Selections[3] = BuildLookupName( CurMech.GetCockpit().GetCurrentState() );
        Selections[4] = BuildLookupName( CurMech.GetPhysEnhance().GetCurrentState() );
        Selections[5] = BuildLookupName( CurMech.GetHeatSinks().GetCurrentState() );
        Selections[6] = BuildLookupName( CurMech.GetJumpJets().GetCurrentState() );
        Selections[7] = BuildLookupName( CurMech.GetArmor().GetCurrentState() );
/*        Selections[0] = (String) cmbInternalType.getSelectedItem();
        Selections[1] = (String) cmbEngineType.getSelectedItem();
        Selections[2] = (String) cmbGyroType.getSelectedItem();
        Selections[3] = (String) cmbCockpitType.getSelectedItem();
        Selections[4] = (String) cmbPhysEnhance.getSelectedItem();
        Selections[5] = (String) cmbHeatSinkType.getSelectedItem();
        Selections[6] = (String) cmbJumpJetType.getSelectedItem();
        Selections[7] = (String) cmbArmorType.getSelectedItem();*/
    }

    private void LoadSelections() {
        // sets the current selections to the last saved selections or to the
        // default selections.  We'll do some validation here as well.

        cmbInternalType.setSelectedItem( Selections[0] );
        cmbEngineType.setSelectedItem( Selections[1] );
        cmbGyroType.setSelectedItem( Selections[2] );
        cmbCockpitType.setSelectedItem( Selections[3] );
        cmbPhysEnhance.setSelectedItem( Selections[4] );
        cmbHeatSinkType.setSelectedItem( Selections[5] );
        cmbJumpJetType.setSelectedItem( Selections[6] );
        cmbArmorType.setSelectedItem( Selections[7] );
    }

    public void RevertToStandardArmor() {
        // convenience method for the armor visitor if Stealth MechArmor cannot be
        // installed.  This should only ever be called by Stealth MechArmor, so we
        // don't have to check whether we're in the right era or not.
        cmbArmorType.setSelectedItem( "Standard Armor" );
    }

    private void ResetAmmo() {
        // first, get the weapons from the loadout that need ammunition
        ArrayList v = CurMech.GetLoadout().GetNonCore(), wep = new ArrayList();
        Object a;

        for( int i = 0; i < v.size(); i++ ) {
            a = v.get( i );
            if( a instanceof ifWeapon ) {
                if( ((ifWeapon) a).HasAmmo() ) {
                    wep.add( a );
                }
            } else if( a instanceof Equipment ) {
                if( ((Equipment) a).HasAmmo() ) {
                    wep.add( a );
                }
            }
        }

        // see if we need to retrieve any ammunition
        Object[] result = { " " };
        if( wep.size() > 0 ) {
            // get the ammunition for those weapons
            int[] key = new int[wep.size()];
            for( int i = 0; i < wep.size(); i++ ) {
                if( wep.get( i ) instanceof ifWeapon ) {
                    key[i] = ((ifWeapon) wep.get( i )).GetAmmoIndex();
                } else if( wep.get( i ) instanceof Equipment ) {
                    key[i] = ((Equipment) wep.get( i )).GetAmmoIndex();
                }
            }
            result = data.GetEquipment().GetAmmo( key, CurMech );
        }

        // put the results into the chooser
        Equipment[AMMUNITION] = result;
        lstChooseAmmunition.setListData( result );
        lstChooseAmmunition.repaint();
    }

    private void SelectiveAllocate() {
        dlgSelectiveAllocate Selec;
        if( CurItem.Contiguous() ) {
            EquipmentCollection e = CurMech.GetLoadout().GetCollection( CurItem );
            if( e == null ) {
                return;
            } else {
                Selec = new dlgSelectiveAllocate( this, true, e );
            }
        } else {
            Selec = new dlgSelectiveAllocate( this, true, CurItem );
        }
        Selec.setLocationRelativeTo( this );
        Selec.setVisible( true );
        RefreshSummary();
        RefreshInfoPane();
    }

    private void AutoAllocate() {
        if( CurItem.Contiguous() ) {
            EquipmentCollection e = CurMech.GetLoadout().GetCollection( CurItem );
            if( e == null ) {
                return;
            } else {
                CurMech.GetLoadout().AutoAllocate( e );
            }
        } else {
            CurMech.GetLoadout().AutoAllocate( CurItem );
        }
        RefreshSummary();
        RefreshInfoPane();
    }

    private void ResetTonnageSelector() {
        int tons = CurMech.GetTonnage();
        if( tons < 15 ) {
            cmbTonnage.setSelectedIndex( 0 );
            lblMechType.setText( "Ultralight Mech");
        } else if( tons > 10 && tons < 20 ) {
            cmbTonnage.setSelectedIndex( 1 );
            lblMechType.setText( "Ultralight Mech");
        } else if( tons > 15 && tons < 25 ) {
            cmbTonnage.setSelectedIndex( 2 );
            lblMechType.setText( "Light Mech");
        } else if( tons > 20 && tons < 30 ) {
            cmbTonnage.setSelectedIndex( 3 );
            lblMechType.setText( "Light Mech");
        } else if( tons > 25 && tons < 35 ) {
            cmbTonnage.setSelectedIndex( 4 );
            lblMechType.setText( "Light Mech");
        } else if( tons > 30 && tons < 40 ) {
            cmbTonnage.setSelectedIndex( 5 );
            lblMechType.setText( "Light Mech");
        } else if( tons > 35 && tons < 45 ) {
            cmbTonnage.setSelectedIndex( 6 );
            lblMechType.setText( "Medium Mech");
        } else if( tons > 40 && tons < 50 ) {
            cmbTonnage.setSelectedIndex( 7 );
            lblMechType.setText( "Medium Mech");
        } else if( tons > 45 && tons < 55 ) {
            cmbTonnage.setSelectedIndex( 8 );
            lblMechType.setText( "Medium Mech");
        } else if( tons > 50 && tons < 60 ) {
            cmbTonnage.setSelectedIndex( 9 );
            lblMechType.setText( "Medium Mech");
        } else if( tons > 55 && tons < 65 ) {
            cmbTonnage.setSelectedIndex( 10 );
            lblMechType.setText( "Heavy Mech");
        } else if( tons > 60 && tons < 70 ) {
            cmbTonnage.setSelectedIndex( 11 );
            lblMechType.setText( "Heavy Mech");
        } else if( tons > 65 && tons < 75 ) {
            cmbTonnage.setSelectedIndex( 12 );
            lblMechType.setText( "Heavy Mech");
        } else if( tons > 70 && tons < 80 ) {
            cmbTonnage.setSelectedIndex( 13 );
            lblMechType.setText( "Heavy Mech");
        } else if( tons > 75 && tons < 85 ) {
            cmbTonnage.setSelectedIndex( 14 );
            lblMechType.setText( "Assault Mech");
        } else if( tons > 80 && tons < 90 ) {
            cmbTonnage.setSelectedIndex( 15 );
            lblMechType.setText( "Assault Mech");
        } else if( tons > 85 && tons < 95 ) {
            cmbTonnage.setSelectedIndex( 16 );
            lblMechType.setText( "Assault Mech");
        } else if( tons > 90 && tons < 100 ) {
            cmbTonnage.setSelectedIndex( 17 );
            lblMechType.setText( "Assault Mech");
        } else {
            cmbTonnage.setSelectedIndex( 18 );
            lblMechType.setText( "Assault Mech");
        }
    }

    private void GetNewMech() {
        boolean Omni = CurMech.IsOmnimech();
        cmbMotiveType.setSelectedIndex( 0 );
        CurMech = new Mech( Prefs );

        chkYearRestrict.setSelected( false );
        txtProdYear.setText( "" );
        cmbMechEra.setEnabled( true );
        cmbProductionEra.setEnabled( true );
        cmbTechBase.setEnabled( true );
        txtProdYear.setEnabled( true );

        cmbRulesLevel.setSelectedItem( Prefs.get( "NewMech_RulesLevel", "Tournament Legal" ) );
        cmbMechEra.setSelectedItem( Prefs.get( "NewMech_Era", "Age of War/Star League" ) );
        cmbProductionEra.setSelectedIndex( 0 );

        if( Omni ) {
            UnlockGUIFromOmni();
        }

        CurMech.SetEra( cmbMechEra.getSelectedIndex() );
        CurMech.SetProductionEra( cmbProductionEra.getSelectedIndex() );
        CurMech.SetRulesLevel( cmbRulesLevel.getSelectedIndex() );
        switch( CurMech.GetEra() ) {
        case AvailableCode.ERA_STAR_LEAGUE:
            CurMech.SetYear( 2750, false );
            break;
        case AvailableCode.ERA_SUCCESSION:
            CurMech.SetYear( 3025, false );
            break;
        case AvailableCode.ERA_CLAN_INVASION:
            CurMech.SetYear( 3070, false );
            break;
        case AvailableCode.ERA_DARK_AGES:
            CurMech.SetYear( 3130, false );
            break;
        case AvailableCode.ERA_ALL:
            CurMech.SetYear( 0, false );
            break;
        }
        BuildTechBaseSelector();
        cmbTechBase.setSelectedItem( Prefs.get( "NewMech_Techbase", "Inner Sphere" ) );
        switch( cmbTechBase.getSelectedIndex() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                CurMech.SetInnerSphere();
                break;
            case AvailableCode.TECH_CLAN:
                CurMech.SetClan();
                break;
            case AvailableCode.TECH_BOTH:
                CurMech.SetMixed();
                break;
        }
        if( CurMech.IsIndustrialmech() ) {
            cmbMechType.setSelectedIndex( 1 );
        } else {
            cmbMechType.setSelectedIndex( 0 );
        }
        txtMechName.setText( CurMech.GetName() );
        txtMechModel.setText( CurMech.GetModel() );

        FixTransferHandlers();
        try {
            CurMech.Visit( new VMechFullRecalc() );
        } catch( Exception e ) {
             //this should never throw an exception, but log it anyway
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }

        ResetTonnageSelector();
        BuildChassisSelector();
        BuildEngineSelector();
        BuildGyroSelector();
        BuildCockpitSelector();
        BuildEnhancementSelector();
        BuildHeatsinkSelector();
        BuildJumpJetSelector();
        BuildArmorSelector();
        CheckOmnimech();
        cmbInternalType.setSelectedItem( SSWConstants.DEFAULT_CHASSIS );
        cmbEngineType.setSelectedItem( SSWConstants.DEFAULT_ENGINE );
        cmbGyroType.setSelectedItem( SSWConstants.DEFAULT_GYRO );
        cmbCockpitType.setSelectedItem( SSWConstants.DEFAULT_COCKPIT );
        cmbPhysEnhance.setSelectedItem( SSWConstants.DEFAULT_ENHANCEMENT );
        cmbHeatSinkType.setSelectedItem( Prefs.get( "NewMech_Heatsinks", "Single Heat Sink" ) );
        cmbJumpJetType.setSelectedItem( SSWConstants.DEFAULT_JUMPJET );
        cmbArmorType.setSelectedItem( SSWConstants.DEFAULT_ARMOR );
        FixWalkMPSpinner();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        RefreshInternalPoints();
        FixArmorSpinners();
        data.Rebuild( CurMech );
        RefreshEquipment();
        chkCTCASE.setSelected( false );
        chkLTCASE.setSelected( false );
        chkRTCASE.setSelected( false );
        chkHDCASE2.setSelected( false );
        chkCTCASE2.setSelected( false );
        chkLTCASE2.setSelected( false );
        chkRTCASE2.setSelected( false );
        chkLACASE2.setSelected( false );
        chkRACASE2.setSelected( false );
        chkLLCASE2.setSelected( false );
        chkRLCASE2.setSelected( false );
        chkNullSig.setSelected( false );
        chkVoidSig.setSelected( false );
        chkBSPFD.setSelected( false );
        chkCLPS.setSelected( false );
        chkTracks.setSelected( false );
        SetLoadoutArrays();
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();

        Overview.StartNewDocument();
        Capabilities.StartNewDocument();
        History.StartNewDocument();
        Deployment.StartNewDocument();
        Variants.StartNewDocument();
        Notables.StartNewDocument();
        Additional.StartNewDocument();
        txtManufacturer.setText( "" );
        txtManufacturerLocation.setText( "" );
        txtEngineManufacturer.setText( "" );
        txtArmorModel.setText( "" );
        txtChassisModel.setText( "" );
        txtJJModel.setText( "" );
        txtCommSystem.setText( "" );
        txtTNTSystem.setText( "" );
        txtSource.setText( "" );
        lblFluffImage.setIcon( null );

        //Reset Manufacturers
        tblWeaponManufacturers.setModel( new javax.swing.table.AbstractTableModel() {
            @Override
            public String getColumnName( int col ) {
                if( col == 1 ) {
                    return "Manufacturer/Model";
                } else {
                    return "Item Name";
                }
            }
            public int getRowCount() { return CurMech.GetLoadout().GetEquipment().size(); }
            public int getColumnCount() { return 2; }
            public Object getValueAt( int row, int col ) {
                Object o = CurMech.GetLoadout().GetEquipment().get( row );
                if( col == 1 ) {
                    return ((abPlaceable) o).GetManufacturer();
                } else {
                    return ((abPlaceable) o).CritName();
                }
            }
            @Override
            public boolean isCellEditable( int row, int col ) {
                if( col == 0 ) {
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public void setValueAt( Object value, int row, int col ) {
                if( col == 0 ) { return; }
                if( ! ( value instanceof String ) ) { return; }
                abPlaceable a = (abPlaceable) CurMech.GetLoadout().GetEquipment().get( row );
                if( chkIndividualWeapons.isSelected() ) {
                    a.SetManufacturer( (String) value );
                    fireTableCellUpdated( row, col );
                } else {
                    ArrayList v = CurMech.GetLoadout().GetEquipment();
                    for( int i = 0; i < v.size(); i++ ) {
                        if( FileCommon.LookupStripArc( ((abPlaceable) v.get( i )).LookupName() ).equals( FileCommon.LookupStripArc( a.LookupName() ) ) ) {
                            ((abPlaceable) v.get( i )).SetManufacturer( (String) value );
                        }
                    }
                    fireTableDataChanged();
                }
            }
        } );

        tblWeaponManufacturers.getInputMap( javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_TAB, 0, false ), "selectNextRow" );

        if( cmbMechEra.getSelectedIndex() == AvailableCode.ERA_ALL ) {
            chkYearRestrict.setEnabled( false );
        } else {
            chkYearRestrict.setEnabled( true );
        }
        CurMech.SetChanged( false );
        setTitle( SSWConstants.AppDescription + " " + SSWConstants.Version );
    }

    private void GetInfoOn() {
        // throws up a window detailing the current item
        if( CurItem instanceof ifWeapon || CurItem instanceof Ammunition ) {
            dlgWeaponInfo WepInfo = new dlgWeaponInfo( this, true );
            WepInfo.setLocationRelativeTo( this );
            WepInfo.setVisible( true );
        } else {
            dlgPlaceableInfo ItemInfo = new dlgPlaceableInfo( this, true );
            ItemInfo.setLocationRelativeTo( this );
            ItemInfo.setVisible( true );
        }
    }

    private void UnallocateAll() {
        // unallocates all crits for the current item from the loadout
        CurMech.GetLoadout().UnallocateAll( CurItem, false );
        RefreshInfoPane();
    }

    private void MountRear() {
        // mounts the current item to the rear or front as appropriate
        if( CurItem.IsMountedRear() ) {
            CurItem.MountRear( false );
        } else {
            CurItem.MountRear( true );
        }
        RefreshInfoPane();
    }

    private void ShowInfoOn( abPlaceable p ) {
        // this fills in all the information on the Equipment panel for the given
        // item.  Depending on what the item is, more or less info is provided
        AvailableCode AC = p.GetAvailability();

        lblInfoAVSL.setText( AC.GetISSLCode() + " / " + AC.GetCLSLCode() );
        lblInfoAVSW.setText( AC.GetISSWCode() + " / " + AC.GetCLSWCode() );
        lblInfoAVCI.setText( AC.GetISCICode() + " / " + AC.GetCLCICode() );
        switch( AC.GetTechBase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                lblInfoIntro.setText( AC.GetISIntroDate() + " (" + AC.GetISIntroFaction() + ")" );
                if( AC.WentExtinctIS() ) {
                    lblInfoExtinct.setText( "" + AC.GetISExtinctDate() );
                } else {
                    lblInfoExtinct.setText( "--" );
                }
                if( AC.WasReIntrodIS() ) {
                    lblInfoReintro.setText( AC.GetISReIntroDate() + " (" + AC.GetISReIntroFaction() + ")" );
                } else {
                    lblInfoReintro.setText( "--" );
                }
                break;
            case AvailableCode.TECH_CLAN:
                lblInfoIntro.setText( AC.GetCLIntroDate() + " (" + AC.GetCLIntroFaction() + ")" );
                if( AC.WentExtinctCL() ) {
                    lblInfoExtinct.setText( "" + AC.GetCLExtinctDate() );
                } else {
                    lblInfoExtinct.setText( "--" );
                }
                if( AC.WasReIntrodCL() ) {
                    lblInfoReintro.setText( AC.GetCLReIntroDate() + " (" + AC.GetCLReIntroFaction() + ")" );
                } else {
                    lblInfoReintro.setText( "--" );
                }
                break;
            case AvailableCode.TECH_BOTH:
                lblInfoIntro.setText( AC.GetISIntroDate() + " (" + AC.GetISIntroFaction() + ") / " + AC.GetCLIntroDate() + " (" + AC.GetCLIntroFaction() + ")" );
                if( AC.WentExtinctIS() ) {
                    lblInfoExtinct.setText( "" + AC.GetISExtinctDate() );
                } else {
                    lblInfoExtinct.setText( "--" );
                }
                if( AC.WentExtinctCL() ) {
                    lblInfoExtinct.setText( lblInfoExtinct.getText() + " / " + AC.GetCLExtinctDate() );
                } else {
                    lblInfoExtinct.setText( lblInfoExtinct.getText() + " / --" );
                }
                if( AC.WasReIntrodIS() ) {
                    lblInfoReintro.setText( AC.GetISReIntroDate() + " (" + AC.GetISReIntroFaction() + ")" );
                } else {
                    lblInfoReintro.setText( "--" );
                }
                if( AC.WasReIntrodCL() ) {
                    lblInfoReintro.setText( lblInfoReintro.getText() + " / " + AC.GetCLReIntroDate() + " (" + AC.GetCLReIntroFaction() + ")" );
                } else {
                    lblInfoReintro.setText( lblInfoReintro.getText() + " / --" );
                }
                break;
        }
        if( CurMech.IsIndustrialmech() ) {
            switch( AC.GetRulesLevel_IM() ) {
                case AvailableCode.RULES_INTRODUCTORY:
                    lblInfoRulesLevel.setText( "Introductory" );
                    break;
                case AvailableCode.RULES_TOURNAMENT:
                    lblInfoRulesLevel.setText( "Tournament" );
                    break;
                case AvailableCode.RULES_ADVANCED:
                    lblInfoRulesLevel.setText( "Advanced" );
                    break;
                case AvailableCode.RULES_EXPERIMENTAL:
                    lblInfoRulesLevel.setText( "Experimental" );
                    break;
                case AvailableCode.RULES_ERA_SPECIFIC:
                    lblInfoRulesLevel.setText( "Era Specific" );
                    break;
                default:
                    lblInfoRulesLevel.setText( "??" );
            }
        } else {
            switch( AC.GetRulesLevel_BM() ) {
                case AvailableCode.RULES_INTRODUCTORY:
                    lblInfoRulesLevel.setText( "Introductory" );
                    break;
                case AvailableCode.RULES_TOURNAMENT:
                    lblInfoRulesLevel.setText( "Tournament" );
                    break;
                case AvailableCode.RULES_ADVANCED:
                    lblInfoRulesLevel.setText( "Advanced" );
                    break;
                case AvailableCode.RULES_EXPERIMENTAL:
                    lblInfoRulesLevel.setText( "Experimental" );
                    break;
                case AvailableCode.RULES_ERA_SPECIFIC:
                    lblInfoRulesLevel.setText( "Era Specific" );
                    break;
                default:
                    lblInfoRulesLevel.setText( "??" );
            }
        }
        lblInfoName.setText( p.CritName() );
        lblInfoTonnage.setText( "" + p.GetTonnage() );
        lblInfoCrits.setText( "" + p.NumCrits() );
        lblInfoCost.setText( "" + String.format( "%1$,.0f", p.GetCost() ) );
        lblInfoBV.setText( CommonTools.GetAggregateReportBV( p ) );

        // now do all the mounting restrictions
        String restrict = "";
        if( ! p.CanAllocHD() ) {
            restrict += "No Head, ";
        }
        if( ! p.CanAllocCT() ) {
            restrict += "No Center Torso, ";
        }
        if( ! p.CanAllocTorso() ) {
            restrict += "No Side Torsos, ";
        }
        if( ! p.CanAllocArms() ) {
            restrict += "No Arms, ";
        }
        if( ! p.CanAllocLegs() ) {
            restrict += "No Legs, ";
        }
        if( p.CanSplit() ) {
            restrict += "Can Split, ";
        }

        // now for weapon and ammo specific 
        if( p instanceof ifWeapon ) {
            ifWeapon w = (ifWeapon) p;
            lblInfoType.setText( w.GetType() );

            if( w.IsUltra() || w.IsRotary() ) {
                lblInfoHeat.setText( w.GetHeat() + "/shot" );
            } else {
                if( w instanceof RangedWeapon ) {
                    if( ((RangedWeapon) w).IsUsingCapacitor() ) {
                        lblInfoHeat.setText( w.GetHeat() + "*" );
                    } else if( ((RangedWeapon) w).IsUsingInsulator() ) {
                        lblInfoHeat.setText( w.GetHeat() + " (I)" );
                    } else {
                        lblInfoHeat.setText( "" + w.GetHeat() );
                    }
                } else {
                    lblInfoHeat.setText( "" + w.GetHeat() );
                }
            }

            if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                lblInfoDamage.setText( w.GetDamageShort() + "/msl" );
            } else if( w.GetWeaponClass() == ifWeapon.W_ARTILLERY ) {
                lblInfoDamage.setText( w.GetDamageShort() + "A" );
            } else if( w instanceof MGArray ) {
                lblInfoDamage.setText( w.GetDamageShort() + "/gun" );
            } else if( w.GetDamageShort() == w.GetDamageMedium() && w.GetDamageShort() == w.GetDamageLong() ) {
                if( w.IsUltra() || w.IsRotary() ) {
                    lblInfoDamage.setText( w.GetDamageShort() + "/shot" );
                } else {
                    if( w instanceof RangedWeapon ) {
                        if( ((RangedWeapon) w).IsUsingCapacitor() ) {
                            lblInfoDamage.setText( w.GetDamageShort() + "*" );
                        } else {
                            lblInfoDamage.setText( "" + w.GetDamageShort() );
                        }
                    } else {
                        lblInfoDamage.setText( "" + w.GetDamageShort() );
                    }
                }
            } else {
                if( w instanceof RangedWeapon ) {
                    if( ((RangedWeapon) w).IsUsingCapacitor() ) {
                        lblInfoDamage.setText( w.GetDamageShort() + "/" + w.GetDamageMedium() + "/" + w.GetDamageLong() + "*" );
                    } else {
                        lblInfoDamage.setText( w.GetDamageShort() + "/" + w.GetDamageMedium() + "/" + w.GetDamageLong() );
                    }
                } else {
                    lblInfoDamage.setText( w.GetDamageShort() + "/" + w.GetDamageMedium() + "/" + w.GetDamageLong() );
                }
            }

            if( w.GetRangeLong() < 1 ) {
                if( w.GetRangeMedium() < 1 ) {
                    if( w.GetWeaponClass() == ifWeapon.W_ARTILLERY ) {
                        lblInfoRange.setText( w.GetRangeShort() + " boards" );
                    } else {
                        lblInfoRange.setText( w.GetRangeShort() + "" );
                    }
                } else {
                    lblInfoRange.setText( w.GetRangeMin() + "/" + w.GetRangeShort() + "/" + w.GetRangeMedium() + "/-" );
                }
            } else {
                lblInfoRange.setText( w.GetRangeMin() + "/" + w.GetRangeShort() + "/" + w.GetRangeMedium() + "/" + w.GetRangeLong() );
            }

            if( w.HasAmmo() ) {
                lblInfoAmmo.setText( "" + w.GetAmmoLotSize() );
            } else {
                lblInfoAmmo.setText( "--" );
            }
            lblInfoSpecials.setText( w.GetSpecials() );
            if( w.OmniRestrictActuators() ) {
                restrict += "Omni Actuator Restricted";
            }
        } else if ( p instanceof Ammunition ) {
            Ammunition a = (Ammunition) p;
            lblInfoType.setText( "--" );
            lblInfoHeat.setText( "--" );
            if( a.ClusterGrouping() > 1 ) {
                lblInfoDamage.setText( a.GetDamageShort() + "/hit" );
            } else {
                lblInfoDamage.setText( a.GetDamageShort() + "" );
            }
            if( a.GetLongRange() < 1 ) {
                if( a.GetMediumRange() < 1 ) {
                    lblInfoRange.setText( a.GetShortRange() + " boards" );
                } else {
                    lblInfoRange.setText( a.GetMinRange() + "/" + a.GetShortRange() + "/" + a.GetMediumRange() + "/-" );
                }
            } else {
                lblInfoRange.setText( a.GetMinRange() + "/" + a.GetShortRange() + "/" + a.GetMediumRange() + "/" + a.GetLongRange() );
            }
            lblInfoAmmo.setText( "" + a.GetLotSize() );
            if( a.IsExplosive() ) {
                lblInfoSpecials.setText( "Explosive" );
            } else {
                lblInfoSpecials.setText( "--" );
            }
        } else if( p instanceof Equipment ) {
            Equipment e = (Equipment) p;
            lblInfoType.setText( e.GetType() );
            lblInfoHeat.setText( "" + e.GetHeat() );
            lblInfoDamage.setText( "--" );
            if( e.GetShortRange() <= 0 && e.GetMediumRange() <= 0 ) {
                if( e.GetLongRange() > 0 ) {
                    lblInfoRange.setText( "" + e.GetLongRange() );
                } else {
                    lblInfoRange.setText( "--" );
                }
            } else {
                lblInfoRange.setText( "0/" + e.GetShortRange() + "/" + e.GetMediumRange() + "/" + e.GetLongRange() );
            }
            if( e.HasAmmo() ) {
                lblInfoAmmo.setText( "" + e.GetAmmo() );
            } else {
                lblInfoAmmo.setText( "--" );
            }
            lblInfoSpecials.setText( e.GetSpecials() );
        } else {
            lblInfoType.setText( "--" );
            lblInfoHeat.setText( "--" );
            lblInfoDamage.setText( "--" );
            lblInfoRange.setText( "--" );
            lblInfoAmmo.setText( "--" );
            lblInfoSpecials.setText( "--" );
        }

        // set the restrictions label
        if( restrict.length() > 0 ) {
            if( restrict.endsWith( ", ") ) {
                restrict = restrict.substring( 0, restrict.length() - 2 );
            }
            lblInfoMountRestrict.setText( restrict );
        } else {
            lblInfoMountRestrict.setText( "None" );
        }

        lblInfoMountRestrict.setText(lblInfoMountRestrict.getText() + " MM Name " + p.MegaMekName(false));
    }

    private void RemoveItemCritTab() {
        if( ! CurItem.CoreComponent() && CurItem.Contiguous() ) {
            CurMech.GetLoadout().Remove( CurItem );

            // refresh the selected equipment listbox
            if( CurMech.GetLoadout().GetNonCore().toArray().length <= 0 ) {
                Equipment[SELECTED] = new Object[] { " " };
            } else {
                Equipment[SELECTED] = CurMech.GetLoadout().GetNonCore().toArray();
            }
            lstSelectedEquipment.setListData( Equipment[SELECTED] );

            // Check the targeting computer if needed
            if( CurMech.UsingTC() ) {
                CurMech.UnallocateTC();
            }

            // refresh the ammunition display
            ResetAmmo();

            // now refresh the information panes
            RefreshSummary();
            RefreshInfoPane();
        }
    }

    private void SolidifyMech() {
        // sets some of the basic mech information normally kept in the GUI and
        // prepares the mech for saving to file
        int year = 0;
        CurMech.SetName( txtMechName.getText() );
        CurMech.SetModel( txtMechModel.getText() );
        if( txtProdYear.getText().isEmpty() ) {
            switch( cmbMechEra.getSelectedIndex() ) {
            case AvailableCode.ERA_STAR_LEAGUE:
                CurMech.SetYear( 2750, false );
                break;
            case AvailableCode.ERA_SUCCESSION:
                CurMech.SetYear( 3025, false );
                break;
            case AvailableCode.ERA_CLAN_INVASION:
                CurMech.SetYear( 3070, false );
                break;
            case AvailableCode.ERA_DARK_AGES:
                CurMech.SetYear( 3132, false );
                break;
            }
        } else {
            try{
                year = Integer.parseInt( txtProdYear.getText() ) ;
                CurMech.SetYear( year, true );
            } catch( NumberFormatException n ) {
                Media.Messager( this, "The production year is not a number." );
                tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
                return;
            }
        }

        CurMech.SetOverview( Overview.GetText() );
        CurMech.SetCapabilities( Capabilities.GetText() );
        CurMech.SetHistory( History.GetText() );
        CurMech.SetDeployment( Deployment.GetText() );
        CurMech.SetVariants( Variants.GetText() );
        CurMech.SetNotables( Notables.GetText() );
        CurMech.SetAdditional( Additional.GetText() );
        CurMech.SetCompany( txtManufacturer.getText() );
        CurMech.SetLocation( txtManufacturerLocation.getText() );
        CurMech.SetEngineManufacturer( txtEngineManufacturer.getText() );
        CurMech.SetArmorModel( txtArmorModel.getText() );
        CurMech.SetChassisModel( txtChassisModel.getText() );
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            CurMech.SetJJModel( txtJJModel.getText() );
        }
        CurMech.SetCommSystem( txtCommSystem.getText() );
        CurMech.SetTandTSystem( txtTNTSystem.getText() );
        CurMech.SetSource( txtSource.getText() );
    }

    private void EnableJumpJets( boolean enable ) {
        // this enables or disables the jump jet spinner if needed
        if( enable ) {
            spnJumpMP.setEnabled( true );
            if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().GetJumpJets().GetNumJJ() > 0 ) {
                cmbJumpJetType.setEnabled( false );
            } else {
                cmbJumpJetType.setEnabled( true );
            }
        } else {
            CurMech.GetJumpJets().ClearJumpJets();
            spnJumpMP.setEnabled( false );
            cmbJumpJetType.setEnabled( false );
        }
        FixJJSpinnerModel();
    }

    private void FixTransferHandlers() {
        // need to reset the transfer handlers because the mech changed
        lstHDCrits.setTransferHandler( new thHDTransferHandler( this, CurMech ) );
        lstCTCrits.setTransferHandler( new thCTTransferHandler( this, CurMech ) );
        lstLTCrits.setTransferHandler( new thLTTransferHandler( this, CurMech ) );
        lstRTCrits.setTransferHandler( new thRTTransferHandler( this, CurMech ) );
        lstLACrits.setTransferHandler( new thLATransferHandler( this, CurMech ) );
        lstRACrits.setTransferHandler( new thRATransferHandler( this, CurMech ) );
        lstLLCrits.setTransferHandler( new thLLTransferHandler( this, CurMech ) );
        lstRLCrits.setTransferHandler( new thRLTransferHandler( this, CurMech ) );
    }

    private void LockGUIForOmni() {
        // this locks most of the GUI controls.  Used mainly by Omnimechs.
        chkOmnimech.setSelected( true );
        chkOmnimech.setEnabled( false );
        mnuUnlock.setEnabled( true );
        cmbTonnage.setEnabled( false );
        cmbMechType.setEnabled( false );
        cmbMotiveType.setEnabled( false );
        cmbInternalType.setEnabled( false );
        cmbEngineType.setEnabled( false );
        cmbGyroType.setEnabled( false );
        cmbCockpitType.setEnabled( false );
        cmbPhysEnhance.setEnabled( false );
        cmbHeatSinkType.setEnabled( false );
        spnHDArmor.setEnabled( false );
        spnCTArmor.setEnabled( false );
        spnLTArmor.setEnabled( false );
        spnRTArmor.setEnabled( false );
        spnLAArmor.setEnabled( false );
        spnRAArmor.setEnabled( false );
        spnLLArmor.setEnabled( false );
        spnRLArmor.setEnabled( false );
        spnCTRArmor.setEnabled( false );
        spnLTRArmor.setEnabled( false );
        spnRTRArmor.setEnabled( false );
        cmbArmorType.setEnabled( false );
        btnMaxArmor.setEnabled( false );
        btnArmorTons.setEnabled( false );
        btnRemainingArmor.setEnabled( false );
        btnEfficientArmor.setEnabled( false );
        btnBalanceArmor.setEnabled( false );
        btnLockChassis.setEnabled( false );
        chkYearRestrict.setEnabled( false );
        if( CurMech.GetBaseLoadout().GetJumpJets().GetNumJJ() > 0 ) {
            cmbJumpJetType.setEnabled( false );
        }
        spnWalkMP.setEnabled( false );
        if( chkFCSAIV.isSelected() ) {
            chkFCSAIV.setEnabled( false );
        }
        if( chkFCSAV.isSelected() ) {
            chkFCSAV.setEnabled( false );
        }
        if( chkFCSApollo.isSelected() ) {
            chkFCSApollo.setEnabled( false );
        }
        if( chkCTCASE.isSelected() ) {
            chkCTCASE.setEnabled( false );
        }
        if( chkLTCASE.isSelected() ) {
            chkLTCASE.setEnabled( false );
        }
        if( chkRTCASE.isSelected() ) {
            chkRTCASE.setEnabled( false );
        }

        if( chkHDCASE2.isSelected() ) {
            chkHDCASE2.setEnabled( false );
        }
        if( chkCTCASE2.isSelected() ) {
            chkCTCASE2.setEnabled( false );
        }
        if( chkLTCASE2.isSelected() ) {
            chkLTCASE2.setEnabled( false );
        }
        if( chkRTCASE2.isSelected() ) {
            chkRTCASE2.setEnabled( false );
        }
        if( chkLACASE2.isSelected() ) {
            chkLACASE2.setEnabled( false );
        }
        if( chkRACASE2.isSelected() ) {
            chkRACASE2.setEnabled( false );
        }
        if( chkLLCASE2.isSelected() ) {
            chkLLCASE2.setEnabled( false );
        }
        if( chkRLCASE2.isSelected() ) {
            chkRLCASE2.setEnabled( false );
        }
        if( chkHDTurret.isSelected() ) {
            chkHDTurret.setEnabled( false );
        }
        if( chkLTTurret.isSelected() ) {
            chkLTTurret.setEnabled( false );
        }
        if( chkRTTurret.isSelected() ) {
            chkRTTurret.setEnabled( false );
        }

        chkFractional.setEnabled( false );
        chkNullSig.setEnabled( false );
        chkVoidSig.setEnabled( false );
        chkCLPS.setEnabled( false );
        chkBSPFD.setEnabled( false );
        chkEnviroSealing.setEnabled( false );
        chkEjectionSeat.setEnabled( false );
        chkRAAES.setEnabled( false );
        chkLAAES.setEnabled( false );
        chkLegAES.setEnabled( false );
        if( CurMech.GetBaseLoadout().HasSupercharger() ) {
            chkSupercharger.setEnabled( false );
            lblSupercharger.setEnabled( false );
            cmbSCLoc.setEnabled( false );
        }

        CheckActuators();

        // now enable the omnimech controls
        cmbOmniVariant.setEnabled( true );
        btnAddVariant.setEnabled( true );
        btnDeleteVariant.setEnabled( true );
        btnRenameVariant.setEnabled( true );
    }

    private void UnlockGUIFromOmni() {
        // this should be used anytime a new mech is made or when unlocking
        // an omnimech.
        chkOmnimech.setSelected( false );
        chkOmnimech.setEnabled( true );
        mnuUnlock.setEnabled( false );
        cmbTonnage.setEnabled( true );
        cmbMechType.setEnabled( true );
        cmbMotiveType.setEnabled( true );
        cmbInternalType.setEnabled( true );
        cmbEngineType.setEnabled( true );
        cmbGyroType.setEnabled( true );
        cmbCockpitType.setEnabled( true );
        cmbPhysEnhance.setEnabled( true );
        cmbHeatSinkType.setEnabled( true );
        spnHDArmor.setEnabled( true );
        spnCTArmor.setEnabled( true );
        spnLTArmor.setEnabled( true );
        spnRTArmor.setEnabled( true );
        spnLAArmor.setEnabled( true );
        spnRAArmor.setEnabled( true );
        spnLLArmor.setEnabled( true );
        spnRLArmor.setEnabled( true );
        spnCTRArmor.setEnabled( true );
        spnLTRArmor.setEnabled( true );
        spnRTRArmor.setEnabled( true );
        cmbArmorType.setEnabled( true );
        btnMaxArmor.setEnabled( true );
        btnArmorTons.setEnabled( true );
        btnRemainingArmor.setEnabled( true );
        btnEfficientArmor.setEnabled( true );
        btnBalanceArmor.setEnabled( true );
        cmbJumpJetType.setEnabled( true );
        btnLockChassis.setEnabled( true );
        chkFCSAIV.setEnabled( true );
        chkFCSAV.setEnabled( true );
        chkFCSApollo.setEnabled( true );
        chkCTCASE.setEnabled( true );
        chkLTCASE.setEnabled( true );
        chkRTCASE.setEnabled( true );
        chkHDCASE2.setEnabled( true );
        chkCTCASE2.setEnabled( true );
        chkLTCASE2.setEnabled( true );
        chkRTCASE2.setEnabled( true );
        chkLACASE2.setEnabled( true );
        chkRACASE2.setEnabled( true );
        chkLLCASE2.setEnabled( true );
        chkRLCASE2.setEnabled( true );
        chkHDTurret.setEnabled( true );
        chkLTTurret.setEnabled( true );
        chkRTTurret.setEnabled( true );
        chkOmnimech.setSelected( false );
        chkOmnimech.setEnabled( true );
        btnLockChassis.setEnabled( false );
        spnWalkMP.setEnabled( true );
        chkYearRestrict.setEnabled( true );
        chkNullSig.setEnabled( true );
        chkVoidSig.setEnabled( true );
        chkCLPS.setEnabled( true );
        chkBSPFD.setEnabled( true );
        chkSupercharger.setEnabled( true );
        lblSupercharger.setEnabled( true );
        cmbSCLoc.setEnabled( true );
        if( CurMech.IsIndustrialmech() ) {
            chkEnviroSealing.setEnabled( true );
            chkEjectionSeat.setEnabled( true );
        } else {
            chkEnviroSealing.setEnabled( false );
            chkEjectionSeat.setEnabled( false );
        }
        // now enable the omnimech controls
        cmbOmniVariant.setEnabled( false );
        btnAddVariant.setEnabled( false );
        btnDeleteVariant.setEnabled( false );
        btnRenameVariant.setEnabled( false );
    }

    private void RefreshOmniVariants() {
        ArrayList v = CurMech.GetLoadouts();
        String[] variants = new String[v.size()];
        if( v.size() <= 0 ) {
            variants = new String[] { common.Constants.BASELOADOUT_NAME };
        } else {
            for( int i = 0; i < v.size(); i++ ) {
                variants[i] = ((ifMechLoadout) v.get(i)).GetName();
            }
        }

        // set the current loadout source before changing
        txtSource.setText( CurMech.GetSource() );
        cmbOmniVariant.setModel( new javax.swing.DefaultComboBoxModel( variants ) );
        cmbOmniVariant.setSelectedItem( CurMech.GetLoadout().GetName() );
    }

    private void RefreshOmniChoices() {
        // this changes the GUI when a new variant is loaded to reflect the
        // equipment it has loaded.
        CheckActuators();
        CheckEquipment();
    }

    private boolean VerifyMech( ActionEvent evt ) {
        // if we have an omnimech, remember which loadout was selected
        String CurLoadout = "";
        SetSource = false;
        if( CurMech.IsOmnimech() ) {
            CurLoadout = CurMech.GetLoadout().GetName();
        }

        // Ensure the mech has a name
        if( CurMech.GetName().isEmpty() ) {
            Media.Messager( this, "Your mech needs a name first." );
            tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
            txtMechName.requestFocusInWindow();
            SetSource = true;
            return false;
        }

        // if we have any systems that requires ECM and don't have it, let the user know
        if( ! CurMech.ValidateECM() ) {
            Media.Messager( "This 'Mech requires an ECM system of some sort to be valid.\nPlease install an ECM system." );
            tbpMainTabPane.setSelectedComponent( pnlEquipment );
            SetSource = true;
            return false;
        }

        // ensure there are no unplaced crits
        if( CurMech.IsOmnimech() ) {
            ArrayList v = CurMech.GetLoadouts();
            for( int i = 0; i < v.size(); i++ ) {
                CurMech.SetCurLoadout( ((ifMechLoadout) v.get( i )).GetName() );
                if( CurMech.GetLoadout().GetQueue().size() != 0 ) {
                    Media.Messager( this, "You must place all items in the " +
                        ((ifMechLoadout) v.get( i )).GetName() + " loadout first." );
                    cmbOmniVariant.setSelectedItem( ((ifMechLoadout) v.get( i )).GetName() );
                    cmbOmniVariantActionPerformed( evt );
                    tbpMainTabPane.setSelectedComponent( pnlCriticals );
                    SetSource = true;
                    return false;
                }
            }
        } else {
            if( CurMech.GetLoadout().GetQueue().size() != 0 ) {
                Media.Messager( this, "You must place all items first." );
                tbpMainTabPane.setSelectedComponent( pnlCriticals );
                SetSource = true;
                return false;
            }
        }

        // ensure we're not overweight
        if( CurMech.IsOmnimech() ) {
            ArrayList v = CurMech.GetLoadouts();
            for( int i = 0; i < v.size(); i++ ) {
                CurMech.SetCurLoadout( ((ifMechLoadout) v.get( i )).GetName() );
                if( CurMech.GetCurrentTons() > CurMech.GetTonnage() ) {
                    Media.Messager( this, ((ifMechLoadout) v.get( i )).GetName() +
                        " loadout is overweight.  Reduce the weight\nto equal or below the mech's tonnage." );
                    cmbOmniVariant.setSelectedItem( ((ifMechLoadout) v.get( i )).GetName() );
                    cmbOmniVariantActionPerformed( evt );
                    tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
                    SetSource = true;
                    return false;
                }
            }
        } else {
            if( CurMech.GetCurrentTons() > CurMech.GetTonnage() ) {
                Media.Messager( this, "This mech is overweight.  Reduce the weight to\nequal or below the mech's tonnage." );
                tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
                SetSource = true;
                return false;
            }
        }
        if( CurMech.IsOmnimech() ) {
            CurMech.SetCurLoadout( CurLoadout );
        }
        SetSource = true;
        return true;
    }

    private void ConfigureUtilsMenu( java.awt.Component c ) {
        // configures the utilities popup menu
        boolean armor = LegalArmoring( CurItem ) && CommonTools.IsAllowed( abPlaceable.ArmoredAC, CurMech );
        boolean cap = LegalCapacitor( CurItem ) && CommonTools.IsAllowed( PPCCapAC, CurMech );
        boolean insul = LegalInsulator( CurItem ) && CommonTools.IsAllowed( LIAC, CurMech );
        boolean caseless = LegalCaseless( CurItem ) && CommonTools.IsAllowed( CaselessAmmoAC, CurMech );
        boolean lotchange = LegalLotChange( CurItem );
        boolean turreted = LegalTurretMount( CurItem );
        boolean dumper = LegalDumper( CurItem );
        mnuArmorComponent.setEnabled( armor );
        mnuAddCapacitor.setEnabled( cap );
        mnuAddInsulator.setEnabled( insul );
        mnuCaseless.setEnabled( caseless );
        mnuArmorComponent.setVisible( armor );
        mnuAddCapacitor.setVisible( cap );
        mnuAddInsulator.setVisible( insul );
        mnuCaseless.setVisible( caseless );
        mnuSetLotSize.setVisible( lotchange );
        mnuTurret.setVisible( turreted );
        mnuDumper.setVisible( dumper );
        if( armor ) {
            if( CurItem.IsArmored() ) {
                mnuArmorComponent.setText( "Unarmor Component" );
            } else {
                mnuArmorComponent.setText( "Armor Component" );
            }
        }
        if( turreted && ( CurItem instanceof RangedWeapon ) ) {
            if( ((RangedWeapon) CurItem).IsTurreted() ) {
                mnuTurret.setText( "Remove from Turret" );
            } else {
                mnuTurret.setText( "Add to Turret");
            }
        }
        if( cap || insul || caseless ) {
            if( CurItem instanceof RangedWeapon ) {
                if( ((RangedWeapon) CurItem).IsUsingCapacitor() ) {
                    mnuAddCapacitor.setText( "Remove Capacitor" );
                } else {
                    mnuAddCapacitor.setText( "Add Capacitor" );
                }
                if( ((RangedWeapon) CurItem).IsUsingInsulator() ) {
                    mnuAddInsulator.setText( "Remove Insulator" );
                } else {
                    mnuAddInsulator.setText( "Add Insulator" );
                }
                if( ((RangedWeapon) CurItem).IsCaseless() ) {
                    mnuCaseless.setText( "Switch from Caseless" );
                } else {
                    mnuCaseless.setText( "Switch to Caseless" );
                }
            }
        }
        if( c == lstCTCrits || c == lstLTCrits || c == lstRTCrits ) {
            if( CurItem instanceof VehicularGrenadeLauncher ) {
                mnuVGLAmmo.setVisible( true );
                mnuVGLArc.setVisible( true );
                switch( ((VehicularGrenadeLauncher) CurItem).GetAmmoType() ) {
                    case VehicularGrenadeLauncher.AMMO_CHAFF:
                        mnuVGLAmmoFrag.setText( "Fragmentation" );
                        mnuVGLAmmoChaff.setText( "* Chaff" );
                        mnuVGLAmmoIncen.setText( "Incendiary" );
                        mnuVGLAmmoSmoke.setText( "Smoke" );
                        break;
                    case VehicularGrenadeLauncher.AMMO_FRAG:
                        mnuVGLAmmoFrag.setText( "* Fragmentation" );
                        mnuVGLAmmoChaff.setText( "Chaff" );
                        mnuVGLAmmoIncen.setText( "Incendiary" );
                        mnuVGLAmmoSmoke.setText( "Smoke" );
                        break;
                    case VehicularGrenadeLauncher.AMMO_INCEN:
                        mnuVGLAmmoFrag.setText( "Fragmentation" );
                        mnuVGLAmmoChaff.setText( "Chaff" );
                        mnuVGLAmmoIncen.setText( "* Incendiary" );
                        mnuVGLAmmoSmoke.setText( "Smoke" );
                        break;
                    case VehicularGrenadeLauncher.AMMO_SMOKE:
                        mnuVGLAmmoFrag.setText( "Fragmentation" );
                        mnuVGLAmmoChaff.setText( "Chaff" );
                        mnuVGLAmmoIncen.setText( "Incendiary" );
                        mnuVGLAmmoSmoke.setText( "* Smoke" );
                        break;
                }
                switch( ((VehicularGrenadeLauncher) CurItem).GetCurrentArc() ) {
                    case VehicularGrenadeLauncher.ARC_FORE:
                        mnuVGLArcFore.setText( "* Fore" );
                        mnuVGLArcForeSide.setText( "Fore-Side" );
                        mnuVGLArcRear.setText( "Rear" );
                        mnuVGLArcRearSide.setText( "Rear-Side" );
                        break;
                    case VehicularGrenadeLauncher.ARC_FORE_SIDE:
                        mnuVGLArcFore.setText( "Fore" );
                        mnuVGLArcForeSide.setText( "* Fore-Side" );
                        mnuVGLArcRear.setText( "Rear" );
                        mnuVGLArcRearSide.setText( "Rear-Side" );
                        break;
                    case VehicularGrenadeLauncher.ARC_REAR:
                        mnuVGLArcFore.setText( "Fore" );
                        mnuVGLArcForeSide.setText( "Fore-Side" );
                        mnuVGLArcRear.setText( "* Rear" );
                        mnuVGLArcRearSide.setText( "Rear-Side" );
                        break;
                    case VehicularGrenadeLauncher.ARC_REAR_SIDE:
                        mnuVGLArcFore.setText( "Fore" );
                        mnuVGLArcForeSide.setText( "Fore-Side" );
                        mnuVGLArcRear.setText( "Rear" );
                        mnuVGLArcRearSide.setText( "* Rear-Side" );
                        break;
                }
            } else {
                mnuVGLAmmo.setVisible( false );
                mnuVGLArc.setVisible( false );
            }
        } else {
            mnuVGLAmmo.setVisible( false );
            mnuVGLArc.setVisible( false );
        }
        if( CurMech.GetLoadout().Find( CurItem ) < 11 ) {
            if( CurItem instanceof EmptyItem ) {
                mnuUnallocateAll.setText( "Unallocate All" );
                mnuUnallocateAll.setEnabled( false );
            } else if( ! CurItem.LocationLocked() ) {
                if( CurItem.Contiguous() ) {
                    mnuUnallocateAll.setText( "Unallocate " + CurItem.CritName() );
                } else {
                    mnuUnallocateAll.setText( "Unallocate All" );
                }
                mnuUnallocateAll.setEnabled( true );
            } else {
                mnuUnallocateAll.setText( "Unallocate All" );
                mnuUnallocateAll.setEnabled( false );
            }
            if( c == lstHDCrits || c == lstCTCrits || c == lstLTCrits || c == lstRTCrits || c == lstLLCrits || c == lstRLCrits || ( CurMech.IsQuad() && (c == lstRACrits || c == lstLACrits))) {
                if( CurItem.CanMountRear() ) {
                    mnuMountRear.setEnabled( true );
                    if( CurItem.IsMountedRear() ) {
                        mnuMountRear.setText( "Unmount Rear " );
                    } else {
                        mnuMountRear.setText( "Mount Rear " );
                    }
                } else {
                    mnuMountRear.setEnabled( false );
                    mnuMountRear.setText( "Mount Rear " );
                }
            } else {
                mnuMountRear.setEnabled( false );
                mnuMountRear.setText( "Mount Rear " );
            }
            if( CurItem.Contiguous() ) {
                EquipmentCollection C = CurMech.GetLoadout().GetCollection( CurItem );
                if( C == null ) {
                    mnuAuto.setEnabled( false );
                    mnuSelective.setEnabled( false );
                } else {
                    mnuAuto.setEnabled( true );
                    mnuSelective.setEnabled( true );
                }
            } else {
                mnuSelective.setEnabled( true );
                mnuAuto.setEnabled( true );
            }
        } else {
            if( CurItem.Contiguous() ) {
                EquipmentCollection C = CurMech.GetLoadout().GetCollection( CurItem );
                if( C == null ) {
                    mnuAuto.setEnabled( false );
                    mnuSelective.setEnabled( false );
                } else {
                    mnuAuto.setEnabled( true );
                    mnuSelective.setEnabled( true );
                }
            } else {
                mnuSelective.setEnabled( true );
                mnuAuto.setEnabled( true );
            }
            mnuUnallocateAll.setText( "Unallocate All" );
            mnuUnallocateAll.setEnabled( false );
            mnuMountRear.setEnabled( false );
            mnuMountRear.setText( "Mount Rear " );
        }
        if( CurItem instanceof Equipment ) {
            if( ((Equipment) CurItem).IsVariableSize() ) {
                mnuSetVariable.setVisible( true );
            } else {
                mnuSetVariable.setVisible( false );
            }
        } else {
            mnuSetVariable.setVisible( false );
        }
        if( CurItem.CoreComponent() || CurItem.LocationLinked() ) {
            mnuRemoveItem.setEnabled( false );
        } else {
            mnuRemoveItem.setEnabled( true );
        }
    }

    private void SetAmmoLotSize() {
        if( CurItem instanceof Ammunition ) {
            dlgAmmoLotSize ammo = new dlgAmmoLotSize( this, true, (Ammunition) CurItem );
            ammo.setLocationRelativeTo( this );
            ammo.setVisible( true );
        }
        RefreshSummary();
        RefreshInfoPane();
    }

    private void ArmorComponent() {
        // armors the component in question
        if( CurItem.IsArmored() ) {
            CurItem.ArmorComponent( false );
        } else {
            CurItem.ArmorComponent( true );
        }
        RefreshInfoPane();
    }

    private void PPCCapacitor() {
        // if the current item can support a capacitor, adds one on
        if( CurItem instanceof RangedWeapon ) {
            if( ((RangedWeapon) CurItem).IsUsingCapacitor() ) {
                abPlaceable p = ((RangedWeapon) CurItem).GetCapacitor();
                ((RangedWeapon) CurItem).UseCapacitor( false );
                CurMech.GetLoadout().Remove( p );
            } else {
                ((RangedWeapon) CurItem).UseCapacitor( true );
                abPlaceable p = ((RangedWeapon) CurItem).GetCapacitor();
                LocationIndex Loc = CurMech.GetLoadout().FindIndex( CurItem );
                if( Loc.Location != -1 ) {
                    try {
                        CurMech.GetLoadout().AddTo( CurMech.GetLoadout().GetCrits( Loc.Location ), p, Loc.Index + CurItem.NumCrits(), 1 );
                    } catch( Exception e ) {
                        // couldn't allocate the capacitor?  Unallocate the PPC.
                        try {
                            CurMech.GetLoadout().UnallocateAll( CurItem, false );
                            // remove the capacitor if it's in the queue
                            //if( CurMech.GetLoadout().QueueContains( p ) ) {
                            //    CurMech.GetLoadout().GetQueue().remove( p );
                            //}
                        } catch( Exception e1 ) {
                            // failed big.  no problem
                            Media.Messager( this, "Fatal error adding a PPC Capacitor:\n" + e.getMessage() + "\nThe Capacitor will be removed." );
                            ((RangedWeapon) CurItem).UseCapacitor( false );
                        }
                    }
                }
            }
        }
        RefreshInfoPane();
    }

    private void LaserInsulator() {
        // if the current item can support an insulator, adds one on
        if( CurItem instanceof RangedWeapon ) {
            if( ((RangedWeapon) CurItem).IsUsingInsulator() ) {
                abPlaceable p = ((RangedWeapon) CurItem).GetInsulator();
                ((RangedWeapon) CurItem).UseInsulator( false );
                CurMech.GetLoadout().Remove( p );
            } else {
                ((RangedWeapon) CurItem).UseInsulator( true );
                abPlaceable p = ((RangedWeapon) CurItem).GetInsulator();
                LocationIndex Loc = CurMech.GetLoadout().FindIndex( CurItem );
                if( Loc.Location != -1 ) {
                    try {
                        CurMech.GetLoadout().AddTo( CurMech.GetLoadout().GetCrits( Loc.Location ), p, Loc.Index + CurItem.NumCrits(), 1 );
                    } catch( Exception e ) {
                        // couldn't allocate the insulator?  Unallocate the PPC.
                        try {
                            CurMech.GetLoadout().UnallocateAll( CurItem, false );
                            // remove the insulator if it's in the queue
                            //if( CurMech.GetLoadout().QueueContains( p ) ) {
                            //    CurMech.GetLoadout().GetQueue().remove( p );
                            //}
                        } catch( Exception e1 ) {
                            // failed big.  no problem
                            Media.Messager( this, "Fatal error adding a Laser Insulator:\n" + e.getMessage() + "\nThe Insulator will be removed." );
                            ((RangedWeapon) CurItem).UseInsulator( false );
                        }
                    }
                }
            }
        }
        RefreshInfoPane();
    }

    private void TurretMount() {
        if( CurItem instanceof RangedWeapon ) {
            RangedWeapon w = (RangedWeapon) CurItem;
            int location = CurMech.GetLoadout().Find( CurItem );
            if( w.IsTurreted() ) {
                if( location == LocationIndex.MECH_LOC_HD ) {
                    w.RemoveFromTurret( CurMech.GetLoadout().GetHDTurret() );
                } else if( location == LocationIndex.MECH_LOC_LT ) {
                    w.RemoveFromTurret( CurMech.GetLoadout().GetLTTurret() );
                } else if( location == LocationIndex.MECH_LOC_RT ) {
                    w.RemoveFromTurret( CurMech.GetLoadout().GetRTTurret() );
                } else {
                    Media.Messager( this, "Cannot remove from turret!" );
                    return;
                }
            } else {
                if( location == LocationIndex.MECH_LOC_HD ) {
                    w.AddToTurret( CurMech.GetLoadout().GetHDTurret() );
                } else if( location == LocationIndex.MECH_LOC_LT ) {
                    w.AddToTurret( CurMech.GetLoadout().GetLTTurret() );
                } else if( location == LocationIndex.MECH_LOC_RT ) {
                    w.AddToTurret( CurMech.GetLoadout().GetRTTurret() );
                } else {
                    Media.Messager( this, "Cannot add to turret!" );
                    return;
                }
            }
        } else if( CurItem instanceof MGArray ) {
            MGArray w = (MGArray) CurItem;
            int location = CurMech.GetLoadout().Find( CurItem );
            if( w.IsTurreted() ) {
                if( location == LocationIndex.MECH_LOC_HD ) {
                    w.RemoveFromTurret( CurMech.GetLoadout().GetHDTurret() );
                } else if( location == LocationIndex.MECH_LOC_LT ) {
                    w.RemoveFromTurret( CurMech.GetLoadout().GetLTTurret() );
                } else if( location == LocationIndex.MECH_LOC_RT ) {
                    w.RemoveFromTurret( CurMech.GetLoadout().GetRTTurret() );
                } else {
                    Media.Messager( this, "Cannot remove from turret!" );
                    return;
                }
            } else {
                if( location == LocationIndex.MECH_LOC_HD ) {
                    w.AddToTurret( CurMech.GetLoadout().GetHDTurret() );
                } else if( location == LocationIndex.MECH_LOC_LT ) {
                    w.AddToTurret( CurMech.GetLoadout().GetLTTurret() );
                } else if( location == LocationIndex.MECH_LOC_RT ) {
                    w.AddToTurret( CurMech.GetLoadout().GetRTTurret() );
                } else {
                    Media.Messager( this, "Cannot add to turret!" );
                    return;
                }
            }
        }
        RefreshInfoPane();
    }

    private void DumperMount() {
        if ( CurItem instanceof Equipment ) {
           
        }
    }

    private void SwitchCaseless() {
        if( CurItem instanceof RangedWeapon ) {
            RangedWeapon r = (RangedWeapon) CurItem;
            // get the original ammo index
            int origIDX = r.GetAmmoIndex();

            // switch over to caseless
            r.SetCaseless( ! r.IsCaseless() );
            int newIDX = r.GetAmmoIndex();

            // check for other weapons with the original ammo index
            ArrayList check = CurMech.GetLoadout().GetNonCore();
            ArrayList replace = new ArrayList();
            abPlaceable p;
            boolean HasOrig = false;
            for( int i = 0; i < check.size(); i++ ) {
                p = (abPlaceable) check.get( i );
                if( p instanceof RangedWeapon ) {
                    if( ((RangedWeapon) p).GetAmmoIndex() == origIDX ) {
                        HasOrig = true;
                    }
                }
                if( p instanceof Ammunition ) {
                    replace.add( p );
                }
            }

            // replace any ammo with the new stuff if there are no other original weapons
            if( ! HasOrig ) {
                Object[] newammo = data.GetEquipment().GetAmmo( newIDX, CurMech );
                for( int i = 0; i < replace.size(); i++ ) {
                    p = (abPlaceable) replace.get( i );
                    if( ((Ammunition) p).GetAmmoIndex() == origIDX ) {
                        CurMech.GetLoadout().Remove( p );
                        if( newammo.length > 0 ) {
                            p = data.GetEquipment().GetCopy( (abPlaceable) newammo[0], CurMech);
                            CurMech.GetLoadout().AddToQueue( p );
                        }
                    }
                }
            }
        }
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();
    }

    public void SetVGLArcFore() {
        if( CurItem instanceof VehicularGrenadeLauncher ) {
            ((VehicularGrenadeLauncher) CurItem).SetArcFore();
            RefreshInfoPane();
        }
    }

    public void SetVGLArcRear() {
        if( CurItem instanceof VehicularGrenadeLauncher ) {
            ((VehicularGrenadeLauncher) CurItem).SetArcRear();
            RefreshInfoPane();
        }
    }

    public void SetVGLArcForeSide() {
        if( CurItem instanceof VehicularGrenadeLauncher ) {
            ((VehicularGrenadeLauncher) CurItem).SetArcForeSide();
            RefreshInfoPane();
        }
    }

    public void SetVGLArcRearSide() {
        if( CurItem instanceof VehicularGrenadeLauncher ) {
            ((VehicularGrenadeLauncher) CurItem).SetArcRearSide();
            RefreshInfoPane();
        }
    }

    public void SetVGLAmmoFrag() {
        if( CurItem instanceof VehicularGrenadeLauncher ) {
            ((VehicularGrenadeLauncher) CurItem).SetAmmoFrag();
            RefreshInfoPane();
        }
    }

    public void SetVGLAmmoChaff() {
        if( CurItem instanceof VehicularGrenadeLauncher ) {
            ((VehicularGrenadeLauncher) CurItem).SetAmmoChaff();
            RefreshInfoPane();
        }
    }

    public void SetVGLAmmoIncendiary() {
        if( CurItem instanceof VehicularGrenadeLauncher ) {
            ((VehicularGrenadeLauncher) CurItem).SetAmmoIncen();
            RefreshInfoPane();
        }
    }

    public void SetVGLAmmoSmoke() {
        if( CurItem instanceof VehicularGrenadeLauncher ) {
            ((VehicularGrenadeLauncher) CurItem).SetAmmoSmoke();
            RefreshInfoPane();
        }
    }

    public void SetVariableSize() {
        if( CurItem instanceof Equipment ) {
            if( ((Equipment) CurItem).IsVariableSize() ) {
                dlgVariableSize SetTons = new dlgVariableSize( this, true, (Equipment) CurItem );
                SetTons.setLocationRelativeTo( this );
                SetTons.setVisible( true );
                LocationIndex li = CurMech.GetLoadout().FindIndex( CurItem );
                if( li.Location >= 0 ) {
                    try {
                        CurMech.GetLoadout().UnallocateAll( CurItem, false );
                        CurMech.GetLoadout().AddTo( CurItem, li.Location, li.Index );
                    } catch( Exception e ) {
                        CurMech.GetLoadout().UnallocateAll( CurItem, true );
                    }
                }
                RefreshInfoPane();
            }
        }
    }

    public boolean LegalArmoring( abPlaceable p ) {
        // This tells us whether it is legal to armor a particular component
        if( p.CanArmor() ) {
            if( CurMech.GetLoadout().GetName().equals( common.Constants.BASELOADOUT_NAME ) ) {
                return true;
            } else {
                if( p instanceof Engine ) { return false; }
                if( p instanceof Gyro ) { return false; }
                if( p instanceof Cockpit ) { return false; }
                if( p instanceof Actuator ) {
                    if( ! ((Actuator) p).IsOmniArmorable() ) { return false; }
                    LocationIndex Loc = CurMech.GetLoadout().FindIndex( p );
                    if( Loc.Location == LocationIndex.MECH_LOC_LA && Loc.Index == 2 ) {
                        if( CurMech.GetBaseLoadout().GetActuators().LeftLowerInstalled() ) { return false; }
                    }
                    if( Loc.Location == LocationIndex.MECH_LOC_RA && Loc.Index == 2 ) {
                        if( CurMech.GetBaseLoadout().GetActuators().RightLowerInstalled() ) { return false; }
                    }
                    if( Loc.Location == LocationIndex.MECH_LOC_LA && Loc.Index == 3 ) {
                        if( CurMech.GetBaseLoadout().GetActuators().LeftHandInstalled() ) { return false; }
                    }
                    if( Loc.Location == LocationIndex.MECH_LOC_RA && Loc.Index == 3 ) {
                        if( CurMech.GetBaseLoadout().GetActuators().RightHandInstalled() ) { return false; }
                    }
                }
                if( p instanceof SimplePlaceable ) { return false; }
                if( CurMech.GetBaseLoadout().GetNonCore().contains( p ) ) { return false; }
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean LegalCapacitor( abPlaceable p ) {
        if( ! ( p instanceof RangedWeapon ) ) { return false; }
        return ((RangedWeapon) p).CanUseCapacitor();
    }

    public boolean LegalInsulator( abPlaceable p ) {
        if( ! ( p instanceof RangedWeapon ) ) { return false; }
        return ((RangedWeapon) p).CanUseInsulator();
    }

    public boolean LegalCaseless( abPlaceable p ) {
        if( ! ( p instanceof RangedWeapon ) ) { return false; }
        return ((RangedWeapon) p).CanUseCaselessAmmo();
    }

    public boolean LegalTurretMount( abPlaceable p ) {
        if( ! (( p instanceof RangedWeapon ) || ( p instanceof MGArray )) ) { return false; }
        int location = CurMech.GetLoadout().Find( p );
        if( location == LocationIndex.MECH_LOC_HD ) {
            if( CurMech.IsOmnimech() ) {
                if( CurMech.GetBaseLoadout().GetHDTurret() == CurMech.GetLoadout().GetHDTurret() ) {
                    return false;
                }
            }
            return CurMech.GetLoadout().HasHDTurret();
        }
        if( location == LocationIndex.MECH_LOC_LT ) {
            if( CurMech.IsOmnimech() ) {
                if( CurMech.GetBaseLoadout().GetLTTurret() == CurMech.GetLoadout().GetLTTurret() ) {
                    return false;
                }
            }
            return CurMech.GetLoadout().HasLTTurret();
        }
        if( location == LocationIndex.MECH_LOC_RT ) {
            if( CurMech.IsOmnimech() ) {
                if( CurMech.GetBaseLoadout().GetRTTurret() == CurMech.GetLoadout().GetRTTurret() ) {
                    return false;
                }
            }
            return CurMech.GetLoadout().HasRTTurret();
        }
        return false;
    }

    public boolean LegalLotChange( abPlaceable p ) {
        if( ! ( p instanceof Ammunition ) ) { return false; }
        if( CurMech.UsingFractionalAccounting() ) { return true; }
        return false;
    }

    public boolean LegalDumper( abPlaceable p ) {
        if ( ! ( p instanceof Equipment ) ) { return false; }
        if ( ( (Equipment)p).CritName().equals("Cargo Container") ) { return true; }
        return false;
    }

    private void PrintMech( Mech m) {
        Printer printer = new Printer(this);
        printer.Print(m);
    }

    private void UpdateBasicChart() {
        int[] fchart = null, lchart = null, rchart = null, rrchart = null;
        if( chkChartFront.isSelected() ) {
            fchart = GetFrontDamageChart();
        }
        if( chkChartRear.isSelected() ) {
            rrchart = GetRearDamageChart();
        }
        if( chkChartRight.isSelected() ) {
            rchart = GetRightArmDamageChart();
        }
        if( chkChartLeft.isSelected() ) {
            lchart = GetLeftArmDamageChart();
        }
        int gridx = 1;
        int gridy = 1;
        if( fchart != null ) {
            for( int i = 0; i < fchart.length; i++ ) {
                if( fchart[i] > 0 ) {
                    if( fchart[i] > gridy ) { gridy = fchart[i]; }
                    if( i > gridx ) { gridx = i; }
                }
            }
        }
        if( rchart != null ) {
            for( int i = 0; i < rchart.length; i++ ) {
                if( rchart[i] > 0 ) {
                    if( rchart[i] > gridy ) { gridy = rchart[i]; }
                    if( i > gridx ) { gridx = i; }
                }
            }
        }
        if( lchart != null ) {
            for( int i = 0; i < lchart.length; i++ ) {
                if( lchart[i] > 0 ) {
                    if( lchart[i] > gridy ) { gridy = lchart[i]; }
                    if( i > gridx ) { gridx = i; }
                }
            }
        }
        if( rrchart != null ) {
            for( int i = 0; i < rrchart.length; i++ ) {
                if( rrchart[i] > 0 ) {
                    if( rrchart[i] > gridy ) { gridy = rrchart[i]; }
                    if( i > gridx ) { gridx = i; }
                }
            }
        }
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        int TotalDamage = 0;
        double TonsWeapons = 0.0f, TonsEquips = 0.0f;

        for( int i = 0; i < v.size(); i++ ) {
            abPlaceable p = (abPlaceable) v.get( i );

            // if the item is a weapon...
            if( p instanceof ifWeapon ) {
                // add it's tonnage to the total
                TonsWeapons += p.GetTonnage();
                TotalDamage += GetMaxDamage( (ifWeapon) p );
            } else {
                if( p instanceof Ammunition ) {
                    TonsWeapons += p.GetTonnage();
                } else {
                    TonsEquips += p.GetTonnage();
                }
            }
        }
        TonsEquips += CurMech.GetCaseTonnage();
        TonsEquips += CurMech.GetCASEIITonnage();
        TonsEquips += ( CurMech.GetTonnage() - CurMech.GetCurrentTons() );

        ((DamageChart) pnlDamageChart).ClearCharts();
        if( chkShowTextNotGraph.isSelected() ) {
            ((DamageChart) pnlDamageChart).SetTextView( true );
            if( chkChartFront.isSelected() ) {
                ((DamageChart) pnlDamageChart).AddChart( fchart, Color.RED );
            }
            if( chkChartRight.isSelected() ) {
                ((DamageChart) pnlDamageChart).AddChart( rchart, Color.GREEN );
            }
            if( chkChartLeft.isSelected() ) {
                ((DamageChart) pnlDamageChart).AddChart( lchart, Color.ORANGE );
            }
            if( chkChartRear.isSelected() ) {
                ((DamageChart) pnlDamageChart).AddChart( rrchart, Color.PINK );
            }
        } else {
            ((DamageChart) pnlDamageChart).SetTextView( false );
            ((DamageChart) pnlDamageChart).SetGridSize( gridx + 1, gridy + 1 );
            if( chkChartRear.isSelected() ) {
                ((DamageChart) pnlDamageChart).AddChart( rrchart, Color.PINK );
            }
            if( chkChartLeft.isSelected() ) {
                ((DamageChart) pnlDamageChart).AddChart( lchart, Color.ORANGE );
            }
            if( chkChartRight.isSelected() ) {
                ((DamageChart) pnlDamageChart).AddChart( rchart, Color.GREEN );
            }
            if( chkChartFront.isSelected() ) {
                ((DamageChart) pnlDamageChart).AddChart( fchart, Color.RED );
            }
        }
        lblTonPercStructure.setText( String.format( "%1$,.2f", ( CurMech.GetIntStruc().GetTonnage() + CurMech.GetCockpit().GetTonnage() + CurMech.GetGyro().GetTonnage() ) / CurMech.GetTonnage() * 100.0f ) + "%" );
        lblTonPercEngine.setText( String.format( "%1$,.2f", CurMech.GetEngine().GetTonnage() / CurMech.GetTonnage() * 100.0 ) + "%" );
        lblTonPercHeatSinks.setText( String.format( "%1$,.2f", CurMech.GetHeatSinks().GetTonnage() / CurMech.GetTonnage() * 100.0 ) + "%" );
        lblTonPercEnhance.setText( String.format( "%1$,.2f", CurMech.GetPhysEnhance().GetTonnage() / CurMech.GetTonnage() * 100.0 ) + "%" );
        lblTonPercArmor.setText( String.format( "%1$,.2f", CurMech.GetArmor().GetTonnage() / CurMech.GetTonnage() * 100.0 ) + "%" );
        lblTonPercJumpJets.setText( String.format( "%1$,.2f", CurMech.GetJumpJets().GetTonnage() / CurMech.GetTonnage() * 100.0 ) + "%" );
        lblTonPercWeapons.setText( String.format( "%1$,.2f", TonsWeapons / CurMech.GetTonnage() * 100.0 ) + "%" );
        lblTonPercEquips.setText( String.format( "%1$,.2f", TonsEquips / CurMech.GetTonnage() * 100.0 ) + "%" );
        lblDamagePerTon.setText( String.format( "%1$,.2f", (double) TotalDamage / CurMech.GetTonnage() ) );
    }

    private int GetMaxDamage( ifWeapon w ) {
        int mult = 1;
        if( w.IsUltra() ) {
            mult = 2;
        }
        if( w.IsRotary() ) {
            mult = 6;
        }
        if( w.GetDamageLong() >= w.GetDamageMedium() && w.GetDamageLong() >= w.GetDamageShort() ) {
            if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                return w.GetDamageLong() * mult * w.ClusterSize();
            } else {
                return w.GetDamageLong() * mult;
            }
        }
        if( w.GetDamageMedium() >= w.GetDamageLong() && w.GetDamageMedium() >= w.GetDamageShort() ) {
            if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                return w.GetDamageMedium() * mult * w.ClusterSize();
            } else {
                return w.GetDamageMedium() * mult;
            }
        }
        if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
            return w.GetDamageShort() * mult * w.ClusterSize();
        } else {
            return w.GetDamageShort() * mult;
        }
    }

    public void lostOwnership( java.awt.datatransfer.Clipboard aClipboard, java.awt.datatransfer.Transferable aContents ) {
        //do nothing
    }

    // the following method is provided to the MechRenderer for comparison
    // purposes.  Please check MechLoadoutRenderer.java for implementation.
    public int GetLocation( javax.swing.JList list ) {
        if( list == lstHDCrits ) { return LocationIndex.MECH_LOC_HD; }
        if( list == lstCTCrits ) { return LocationIndex.MECH_LOC_CT; }
        if( list == lstLTCrits ) { return LocationIndex.MECH_LOC_LT; }
        if( list == lstRTCrits ) { return LocationIndex.MECH_LOC_RT; }
        if( list == lstLACrits ) { return LocationIndex.MECH_LOC_LA; }
        if( list == lstRACrits ) { return LocationIndex.MECH_LOC_RA; }
        if( list == lstLLCrits ) { return LocationIndex.MECH_LOC_LL; }
        if( list == lstRLCrits ) { return LocationIndex.MECH_LOC_RL; }
        return -1;
    }

    // check the tonnage to see if it's legal and acts accordingly
    public void CheckTonnage( boolean RulesChange ) {
        if( RulesChange ) {
            if( ! CurMech.IsIndustrialmech() ) {
                if( CurMech.GetRulesLevel() < AvailableCode.RULES_EXPERIMENTAL && CurMech.GetTonnage() < 20 ) {
                    cmbTonnage.setSelectedItem( "20" );
                }
            }
        } else {
            // a change in mech type or tonnage
            if( ! CurMech.IsIndustrialmech() ) {
                // this is really the only time tonnage needs to be restricted
                if( CurMech.GetRulesLevel() < AvailableCode.RULES_EXPERIMENTAL && CurMech.GetTonnage() < 20 ) {
                    if( CurMech.GetTonnage() < 20 ) {
                        cmbRulesLevel.setSelectedIndex( AvailableCode.RULES_EXPERIMENTAL );
                    }
                }
            }
        }
    }

    private void CheckFileName( String s ) throws Exception {
        if( s.contains( "\\" ) ) {
            throw new Exception( "The Mech name or model contains a back slash\nwhich should be removed before saving." );
        }
        if( s.contains( "/" ) ) {
            throw new Exception( "The Mech name or model contains a forward slash\nwhich should be removed before saving." );
        }
        if( s.contains( "*" ) ) {
            throw new Exception( "The Mech name or model contains an asterisk\nwhich should be removed before saving." );
        }
    }

    private void SolidifyJJManufacturer() {
        // this method is used mainly for OmniMechs with varying jump jet loads
        if( ! txtJJModel.getText().equals( "" ) || ! CurMech.GetJJModel().equals( "" ) ) {
            if( ! txtJJModel.getText().equals( CurMech.GetJJModel() ) ) { 
                CurMech.SetJJModel( txtJJModel.getText() );
            }
        }
        txtJJModel.setText( CurMech.GetJJModel() );
    }

    private int[] GetFrontDamageChart() {
        // creates a damage chart for the current mech using the current loadout
        int[] chart = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        // we're going to use 40 as the max range
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        ifWeapon w;
        for( int i = 0; i < 40; i++ ) {
            for( int j = 0; j < v.size(); j++ ) {
                if( v.get( j ) instanceof ifWeapon ) {
                    w = (ifWeapon) v.get( j );
                    if( ! ((abPlaceable) w).IsMountedRear() ) {
                        if( chkAverageDamage.isSelected() ) {
                            chart[i] += CommonTools.GetAverageDamageAtRange( w, i );
                        } else {
                            chart[i] += GetDamageAtRange( w, i );
                        }
                    }
                }
            }
        }
        return chart;
    }

    private int[] GetRightArmDamageChart() {
        // creates a damage chart for the current mech using the current loadout
        int[] chart = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        // we're going to use 40 as the max range
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        ifWeapon w;
        for( int i = 0; i < 40; i++ ) {
            for( int j = 0; j < v.size(); j++ ) {
                if( v.get( j ) instanceof ifWeapon ) {
                    w = (ifWeapon) v.get( j );
                    if( CurMech.GetLoadout().Find( (abPlaceable) w ) == LocationIndex.MECH_LOC_RA ) {
                        if( chkAverageDamage.isSelected() ) {
                            chart[i] += CommonTools.GetAverageDamageAtRange( w, i );
                        } else {
                            chart[i] += GetDamageAtRange( w, i );
                        }
                    }
                }
            }
        }
        return chart;
    }

    private int[] GetLeftArmDamageChart() {
        // creates a damage chart for the current mech using the current loadout
        int[] chart = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        // we're going to use 40 as the max range
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        ifWeapon w;
        for( int i = 0; i < 40; i++ ) {
            for( int j = 0; j < v.size(); j++ ) {
                if( v.get( j ) instanceof ifWeapon ) {
                    w = (ifWeapon) v.get( j );
                    if( CurMech.GetLoadout().Find( (abPlaceable) w ) == LocationIndex.MECH_LOC_LA ) {
                        if( chkAverageDamage.isSelected() ) {
                            chart[i] += CommonTools.GetAverageDamageAtRange( w, i );
                        } else {
                            chart[i] += GetDamageAtRange( w, i );
                        }
                    }
                }
            }
        }
        return chart;
    }

    private int[] GetRearDamageChart() {
        // creates a damage chart for the current mech using the current loadout
        int[] chart = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        // we're going to use 40 as the max range
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        boolean flip = ! ( CurMech.GetLoadout().GetActuators().LeftLowerInstalled() && CurMech.GetLoadout().GetActuators().RightLowerInstalled() );
        ifWeapon w;
        for( int i = 0; i < 40; i++ ) {
            for( int j = 0; j < v.size(); j++ ) {
                if( v.get( j ) instanceof ifWeapon ) {
                    w = (ifWeapon) v.get( j );
                    int Loc = CurMech.GetLoadout().Find( (abPlaceable) w );
                    if( ((abPlaceable) w).IsMountedRear() || (( Loc == LocationIndex.MECH_LOC_LA || Loc == LocationIndex.MECH_LOC_RA ) && flip ) ) {
                        if( chkAverageDamage.isSelected() ) {
                            chart[i] += CommonTools.GetAverageDamageAtRange( w, i );
                        } else {
                            chart[i] += GetDamageAtRange( w, i );
                        }
                    }
                }
            }
        }
        return chart;
    }

    private int GetDamageAtRange( ifWeapon w, int range ) {
        int mult = 1;
        if( w.IsUltra() ) {
            mult = 2;
        }
        if( w.IsRotary() ) {
            mult = 6;
        }
        if( w instanceof MGArray ) {
            mult = ((MGArray) w).GetNumMGs();
        }

        if( w.GetRangeLong() <= 0 ) {
            if( w.GetRangeMedium() <= 0 ) {
                if( range <= w.GetRangeShort() ) {
                    if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                        return w.GetDamageShort() * mult * w.ClusterSize();
                    } else {
                        return w.GetDamageShort() * mult;
                    }
                } else {
                    return 0;
                }
            } else {
                if( range <= w.GetRangeShort() ) {
                    if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                        return w.GetDamageShort() * mult * w.ClusterSize();
                    } else {
                        return w.GetDamageShort() * mult;
                    }
                } else if( range <= w.GetRangeMedium() ) {
                    if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                        return w.GetDamageMedium() * mult * w.ClusterSize();
                    } else {
                        return w.GetDamageMedium() * mult;
                    }
                } else {
                    return 0;
                }
            }
        } else {
            if( range <= w.GetRangeShort() ) {
                if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                    return w.GetDamageShort() * mult * w.ClusterSize();
                } else {
                    return w.GetDamageShort() * mult;
                }
            } else if( range <= w.GetRangeMedium() ) {
                if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                    return w.GetDamageMedium() * mult * w.ClusterSize();
                } else {
                    return w.GetDamageMedium() * mult;
                }
            } else if( range <= w.GetRangeLong() ) {
                if( w.GetWeaponClass() == ifWeapon.W_MISSILE ) {
                    return w.GetDamageLong() * mult * w.ClusterSize();
                } else {
                    return w.GetDamageLong() * mult;
                }
            } else {
                return 0;
            }
        }
    }

    private File GetSaveFile( final String extension, String path, boolean autooverwrite, boolean singleloadout ) {
        String filename = "";
        boolean overwrite = false;

        // perform standard actions required before saving
        SolidifyMech();
        if( ! VerifyMech( null ) ) {
            return null;
        }

        // build the filename
        if( CurMech.IsOmnimech() && singleloadout ) {
            if( CurMech.GetModel().isEmpty() ) {
                filename = CurMech.GetName() + " " + CurMech.GetLoadout().GetName() + "." + extension;
            } else {
                filename = CurMech.GetName() + " " + CurMech.GetModel() + " " +
                    CurMech.GetLoadout().GetName() + "." + extension;
            }
        } else {
            if( CurMech.GetModel().isEmpty() ) {
                filename = CurMech.GetName() + "." + extension;
            } else {
                filename = CurMech.GetName() + " " + CurMech.GetModel() + "." + extension;
            }
        }

        filename = CommonTools.FormatFileName( filename );

        // ensure we have a good filename
        try {
            CheckFileName( filename );
        } catch( Exception e ) {
            Media.Messager( this, "There was a problem with the filename:\n" +
                e.getMessage() + "\nSaving will continue but you should change the filename." );
        }

        // check for auto overwrite.  sometimes you don't want to do this
        if( autooverwrite ) {
            File testfile = new File( path + File.separator + filename );
            if( testfile.exists() ) {
                int choice = javax.swing.JOptionPane.showConfirmDialog( this, "A file with the specified " +
                    "name already exists\n" + testfile + "\nDo you want to overwrite it?", "Overwrite file",
                    javax.swing.JOptionPane.YES_NO_OPTION );
                if( choice != 1 ) {
                    overwrite = true;
                }
            }
        }

        File retval = null;
        if( autooverwrite && overwrite ) {
            retval = new File( path + File.separator + filename );
        } else {
            // build the filechooser dialogue
            File directory = new File( path );
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory( directory );
            fc.addChoosableFileFilter( new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept( File f ) {
                    if( f.isDirectory() ) {
                        return true;
                    }

                    String checkext = Utils.getExtension( f );
                    if( checkext != null ) {
                        if( checkext.equals( extension ) ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return false;
                }

                @Override
                public String getDescription() {
                    return "*." + extension;
                }
            } );
            fc.setAcceptAllFileFilterUsed( false );
            fc.setSelectedFile( new File( filename ) );

            // what does the user want to do?
            int returnval = fc.showDialog( this, "Save to " + extension );
            if( returnval != JFileChooser.APPROVE_OPTION ) { return null; }
            retval = fc.getSelectedFile();
            if( retval.exists() ) {
                int choice = javax.swing.JOptionPane.showConfirmDialog( this, "A file with the specified " +
                    "name already exists\n" + retval + "\nDo you want to overwrite it?", "Overwrite file",
                    javax.swing.JOptionPane.YES_NO_OPTION );
                if( choice == 1 ) {
                    Media.Messager( this, "The 'Mech was not saved." );
                    return null;
                }
            }
        }

        return retval;
    }

    private void FluffCut( Component c ) {
        String cut = "";
        if( c instanceof JEditorPane ) {
            JEditorPane j = (JEditorPane) c;
            if( j.getSelectedText() == null ) {
                // get everything and remove it
                cut = j.getText();
                j.setText( "" );
            } else {
                // get the selection
                cut = j.getSelectedText();
                j.setText( j.getText().replace( cut, "" ) );
            }
        }
        if( c instanceof JTextField ) {
            JTextField j = (JTextField) c;
            if( j.getSelectedText() == null ) {
                // get everything and remove it
                cut = j.getText();
                j.setText( "" );
            } else {
                // get the selection
                cut = j.getSelectedText();
                j.setText( j.getText().replace( cut, "" ) );
            }
        }
        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( cut );
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( export, this );
    }

    private void FluffCopy( Component c ) {
        String copy = "";
        if( c instanceof JEditorPane ) {
            JEditorPane j = (JEditorPane) c;
            if( j.getSelectedText() == null ) {
                // get everything and remove it
                copy = j.getText();
            } else {
                // get the selection
                copy = j.getSelectedText();
            }
        }
        if( c instanceof JTextField ) {
            JTextField j = (JTextField) c;
            if( j.getSelectedText() == null ) {
                // get everything and remove it
                copy = j.getText();
            } else {
                // get the selection
                copy = j.getSelectedText();
            }
        }
        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( copy );
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( export, this );
    }

    private void FluffPaste( Component c ) {
        // ensure we have the correct data flavor from the clipboard
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        String txtimport = null;
        try {
            txtimport = (String) clipboard.getData( DataFlavor.stringFlavor );
        } catch( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
            return;
        }
        if( txtimport == null ) { return; }
        if( c instanceof JEditorPane ) {
            JEditorPane j = (JEditorPane) c;
            int insert = j.getCaretPosition();
            String paste = j.getText().substring( 0, insert ) + txtimport + j.getText().substring( insert );
            j.setText( paste );
        }
        if( c instanceof JTextField ) {
            JTextField j = (JTextField) c;
            int insert = j.getCaretPosition();
            String paste = j.getText().substring( 0, insert ) + txtimport + j.getText().substring( insert );
            j.setText( paste );
        }
    }

    private boolean AddECM() {
        // Adds an ECM suite if a certain system needs it
        if( Prefs.getBoolean( "AutoAddECM", true ) ) {
            if( ! CurMech.ValidateECM() ) {
                abPlaceable a = data.GetEquipment().GetEquipmentByName( "Guardian ECM Suite", CurMech );
                if( a == null ) {
                    a = data.GetEquipment().GetEquipmentByName( "Angel ECM", CurMech );
                    if( a == null ) {
                        a = data.GetEquipment().GetEquipmentByName( "ECM Suite", CurMech );
                        if( a == null ) {
                            a = data.GetEquipment().GetEquipmentByName( "Watchdog CEWS", CurMech );
                            if( a == null ) {
                                return false;
                            }
                        }
                    }
                }
                CurMech.GetLoadout().AddToQueue( a );
            }
            return true;
        } else {
            Media.Messager( this, "Please add an appropriate ECM Suite to complement this\n system.  The 'Mech is not valid without an ECM Suite." );
            return true;
        }
    }

     /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tlbIconBar = new javax.swing.JToolBar();
        btnNewIcon = new javax.swing.JButton();
        btnOpen = new javax.swing.JButton();
        btnSaveIcon = new javax.swing.JButton();
        btnPrintPreview = new javax.swing.JButton();
        jSeparator24 = new javax.swing.JToolBar.Separator();
        btnPrintIcon = new javax.swing.JButton();
        jSeparator22 = new javax.swing.JToolBar.Separator();
        btnExportClipboardIcon = new javax.swing.JButton();
        btnExportHTMLIcon = new javax.swing.JButton();
        btnExportTextIcon = new javax.swing.JButton();
        btnExportMTFIcon = new javax.swing.JButton();
        btnChatInfo = new javax.swing.JButton();
        jSeparator23 = new javax.swing.JToolBar.Separator();
        btnPostToS7 = new javax.swing.JButton();
        jSeparator25 = new javax.swing.JToolBar.Separator();
        btnAddToForceList = new javax.swing.JButton();
        btnForceList = new javax.swing.JButton();
        jSeparator26 = new javax.swing.JToolBar.Separator();
        btnOptionsIcon = new javax.swing.JButton();
        jSeparator21 = new javax.swing.JToolBar.Separator();
        lblSelectVariant = new javax.swing.JLabel();
        cmbOmniVariant = new javax.swing.JComboBox();
        tbpMainTabPane = new javax.swing.JTabbedPane();
        pnlBasicSetup = new javax.swing.JPanel();
        pnlBasicInformation = new javax.swing.JPanel();
        lblMechName = new javax.swing.JLabel();
        txtMechName = new javax.swing.JTextField();
        lblModel = new javax.swing.JLabel();
        txtMechModel = new javax.swing.JTextField();
        lblMechEra = new javax.swing.JLabel();
        cmbMechEra = new javax.swing.JComboBox();
        lblEraYears = new javax.swing.JLabel();
        lblProdYear = new javax.swing.JLabel();
        txtProdYear = new javax.swing.JTextField();
        chkYearRestrict = new javax.swing.JCheckBox();
        lblTechBase = new javax.swing.JLabel();
        cmbTechBase = new javax.swing.JComboBox();
        cmbRulesLevel = new javax.swing.JComboBox();
        lblRulesLevel = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        txtSource = new javax.swing.JTextField();
        jSeparator28 = new javax.swing.JSeparator();
        jSeparator29 = new javax.swing.JSeparator();
        cmbProductionEra = new javax.swing.JComboBox();
        pnlChassis = new javax.swing.JPanel();
        lblTonnage = new javax.swing.JLabel();
        cmbTonnage = new javax.swing.JComboBox();
        lblMechType = new javax.swing.JLabel();
        lblMotiveType = new javax.swing.JLabel();
        cmbMotiveType = new javax.swing.JComboBox();
        lblEngineType = new javax.swing.JLabel();
        cmbEngineType = new javax.swing.JComboBox();
        lblInternalType = new javax.swing.JLabel();
        cmbInternalType = new javax.swing.JComboBox();
        lblGyroType = new javax.swing.JLabel();
        cmbGyroType = new javax.swing.JComboBox();
        lblCockpit = new javax.swing.JLabel();
        cmbCockpitType = new javax.swing.JComboBox();
        lblPhysEnhance = new javax.swing.JLabel();
        cmbPhysEnhance = new javax.swing.JComboBox();
        chkOmnimech = new javax.swing.JCheckBox();
        cmbMechType = new javax.swing.JComboBox();
        lblUnitType = new javax.swing.JLabel();
        chkCommandConsole = new javax.swing.JCheckBox();
        pnlHeatSinks = new javax.swing.JPanel();
        lblHeatSinkType = new javax.swing.JLabel();
        cmbHeatSinkType = new javax.swing.JComboBox();
        lblHSNumber = new javax.swing.JLabel();
        spnNumberOfHS = new javax.swing.JSpinner();
        pnlMovement = new javax.swing.JPanel();
        lblWalkMP = new javax.swing.JLabel();
        spnWalkMP = new javax.swing.JSpinner();
        lblRunMPLabel = new javax.swing.JLabel();
        lblRunMP = new javax.swing.JLabel();
        lblJumpMP = new javax.swing.JLabel();
        spnJumpMP = new javax.swing.JSpinner();
        cmbJumpJetType = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        spnBoosterMP = new javax.swing.JSpinner();
        chkBoosters = new javax.swing.JCheckBox();
        lblMoveSummary = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtEngineRating = new javax.swing.JTextField();
        pnlOmniInfo = new javax.swing.JPanel();
        btnLockChassis = new javax.swing.JButton();
        btnAddVariant = new javax.swing.JButton();
        btnDeleteVariant = new javax.swing.JButton();
        btnRenameVariant = new javax.swing.JButton();
        pnlBasicSummary = new javax.swing.JPanel();
        lblSumStructure = new javax.swing.JLabel();
        txtSumIntTon = new javax.swing.JTextField();
        lblSumEngine = new javax.swing.JLabel();
        txtSumEngTon = new javax.swing.JTextField();
        lblSumGyro = new javax.swing.JLabel();
        txtSumGyrTon = new javax.swing.JTextField();
        lblSumHeadItem = new javax.swing.JLabel();
        lblSumHeadTons = new javax.swing.JLabel();
        lblSumHeadCrits = new javax.swing.JLabel();
        txtSumIntCrt = new javax.swing.JTextField();
        txtSumEngCrt = new javax.swing.JTextField();
        txtSumGyrCrt = new javax.swing.JTextField();
        lblSumCockpit = new javax.swing.JLabel();
        txtSumCocTon = new javax.swing.JTextField();
        txtSumCocCrt = new javax.swing.JTextField();
        lblSumEnhance = new javax.swing.JLabel();
        txtSumEnhTon = new javax.swing.JTextField();
        txtSumEnhCrt = new javax.swing.JTextField();
        lblSumHeatSinks = new javax.swing.JLabel();
        txtSumHSTon = new javax.swing.JTextField();
        txtSumHSCrt = new javax.swing.JTextField();
        lblSumJJ = new javax.swing.JLabel();
        txtSumJJTon = new javax.swing.JTextField();
        txtSumJJCrt = new javax.swing.JTextField();
        txtSumIntACode = new javax.swing.JTextField();
        txtSumEngACode = new javax.swing.JTextField();
        txtSumGyrACode = new javax.swing.JTextField();
        txtSumCocACode = new javax.swing.JTextField();
        txtSumHSACode = new javax.swing.JTextField();
        txtSumEnhACode = new javax.swing.JTextField();
        txtSumJJACode = new javax.swing.JTextField();
        lblSumHeadAvailable = new javax.swing.JLabel();
        txtSumPAmpsTon = new javax.swing.JTextField();
        txtSumPAmpsACode = new javax.swing.JTextField();
        lblSumPAmps = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        chkCLPS = new javax.swing.JCheckBox();
        chkNullSig = new javax.swing.JCheckBox();
        chkBSPFD = new javax.swing.JCheckBox();
        chkVoidSig = new javax.swing.JCheckBox();
        chkSupercharger = new javax.swing.JCheckBox();
        cmbSCLoc = new javax.swing.JComboBox();
        chkBoobyTrap = new javax.swing.JCheckBox();
        chkPartialWing = new javax.swing.JCheckBox();
        chkFHES = new javax.swing.JCheckBox();
        lblSupercharger = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        chkEjectionSeat = new javax.swing.JCheckBox();
        chkEnviroSealing = new javax.swing.JCheckBox();
        chkTracks = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        chkFractional = new javax.swing.JCheckBox();
        pnlArmor = new javax.swing.JPanel();
        pnlFrontArmor = new javax.swing.JPanel();
        pnlRLArmorBox = new javax.swing.JPanel();
        lblRLHeader = new javax.swing.JLabel();
        lblRLIntPts = new javax.swing.JLabel();
        lblRLArmorHeader = new javax.swing.JLabel();
        spnRLArmor = new javax.swing.JSpinner();
        pnlLLArmorBox = new javax.swing.JPanel();
        lblLLHeader = new javax.swing.JLabel();
        lblLLIntPts = new javax.swing.JLabel();
        lblLLArmorHeader = new javax.swing.JLabel();
        spnLLArmor = new javax.swing.JSpinner();
        pnlRAArmorBox = new javax.swing.JPanel();
        lblRAHeader = new javax.swing.JLabel();
        lblRAIntPts = new javax.swing.JLabel();
        lblRAArmorHeader = new javax.swing.JLabel();
        spnRAArmor = new javax.swing.JSpinner();
        pnlHDArmorBox = new javax.swing.JPanel();
        lblHDHeader = new javax.swing.JLabel();
        lblHDIntPts = new javax.swing.JLabel();
        lblHDArmorHeader = new javax.swing.JLabel();
        spnHDArmor = new javax.swing.JSpinner();
        pnlCTArmorBox = new javax.swing.JPanel();
        lblCTHeader = new javax.swing.JLabel();
        lblCTIntPts = new javax.swing.JLabel();
        lblCTArmorHeader = new javax.swing.JLabel();
        spnCTArmor = new javax.swing.JSpinner();
        pnlLTArmorBox = new javax.swing.JPanel();
        lblLTHeader = new javax.swing.JLabel();
        lblLTIntPts = new javax.swing.JLabel();
        lblLTArmorHeader = new javax.swing.JLabel();
        spnLTArmor = new javax.swing.JSpinner();
        pnlRTArmorBox = new javax.swing.JPanel();
        lblRTHeader = new javax.swing.JLabel();
        lblRTIntPts = new javax.swing.JLabel();
        lblRTArmorHeader = new javax.swing.JLabel();
        spnRTArmor = new javax.swing.JSpinner();
        pnlLAArmorBox = new javax.swing.JPanel();
        lblLAHeader = new javax.swing.JLabel();
        lblLAIntPts = new javax.swing.JLabel();
        lblLAArmorHeader = new javax.swing.JLabel();
        spnLAArmor = new javax.swing.JSpinner();
        pnlRearArmor = new javax.swing.JPanel();
        pnlRTRArmorBox = new javax.swing.JPanel();
        lblRTRArmorHeader = new javax.swing.JLabel();
        spnRTRArmor = new javax.swing.JSpinner();
        pnlCTRArmorBox = new javax.swing.JPanel();
        lblCTRArmorHeader = new javax.swing.JLabel();
        spnCTRArmor = new javax.swing.JSpinner();
        pnlLTRArmorBox = new javax.swing.JPanel();
        lblLTRArmorHeader = new javax.swing.JLabel();
        spnLTRArmor = new javax.swing.JSpinner();
        pnlArmorInfo = new javax.swing.JPanel();
        lblArmorCoverage = new javax.swing.JLabel();
        lblArmorPoints = new javax.swing.JLabel();
        txtSumArmorTon = new javax.swing.JTextField();
        txtSumArmorCrt = new javax.swing.JTextField();
        lblSumHeadTons1 = new javax.swing.JLabel();
        lblSumHeadCrits1 = new javax.swing.JLabel();
        lblArmorTonsWasted = new javax.swing.JLabel();
        lblAVInLot = new javax.swing.JLabel();
        pnlArmorSetup = new javax.swing.JPanel();
        btnMaxArmor = new javax.swing.JButton();
        btnArmorTons = new javax.swing.JButton();
        cmbArmorType = new javax.swing.JComboBox();
        lblArmorType = new javax.swing.JLabel();
        btnBalanceArmor = new javax.swing.JCheckBox();
        btnEfficientArmor = new javax.swing.JButton();
        btnRemainingArmor = new javax.swing.JButton();
        pnlPatchworkChoosers = new javax.swing.JPanel();
        lblPWHDLoc = new javax.swing.JLabel();
        lblPWCTLoc = new javax.swing.JLabel();
        lblPWLTLoc = new javax.swing.JLabel();
        lblPWRTLoc = new javax.swing.JLabel();
        lblPWLALoc = new javax.swing.JLabel();
        lblPWRALoc = new javax.swing.JLabel();
        lblPWLLLoc = new javax.swing.JLabel();
        cmbPWHDType = new javax.swing.JComboBox();
        cmbPWCTType = new javax.swing.JComboBox();
        cmbPWLTType = new javax.swing.JComboBox();
        cmbPWRTType = new javax.swing.JComboBox();
        cmbPWLAType = new javax.swing.JComboBox();
        cmbPWRAType = new javax.swing.JComboBox();
        cmbPWLLType = new javax.swing.JComboBox();
        cmbPWRLType = new javax.swing.JComboBox();
        lblPWRLLoc = new javax.swing.JLabel();
        pnlEquipment = new javax.swing.JPanel();
        tbpWeaponChooser = new javax.swing.JTabbedPane();
        pnlBallistic = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane8 = new javax.swing.JScrollPane();
        lstChooseBallistic = new javax.swing.JList();
        jSeparator4 = new javax.swing.JSeparator();
        pnlEnergy = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane9 = new javax.swing.JScrollPane();
        lstChooseEnergy = new javax.swing.JList();
        jSeparator1 = new javax.swing.JSeparator();
        pnlMissile = new javax.swing.JPanel();
        jSeparator5 = new javax.swing.JSeparator();
        jScrollPane19 = new javax.swing.JScrollPane();
        lstChooseMissile = new javax.swing.JList();
        jSeparator6 = new javax.swing.JSeparator();
        pnlPhysical = new javax.swing.JPanel();
        jSeparator8 = new javax.swing.JSeparator();
        jScrollPane20 = new javax.swing.JScrollPane();
        lstChoosePhysical = new javax.swing.JList();
        jSeparator7 = new javax.swing.JSeparator();
        pnlEquipmentChooser = new javax.swing.JPanel();
        jSeparator10 = new javax.swing.JSeparator();
        jScrollPane21 = new javax.swing.JScrollPane();
        lstChooseEquipment = new javax.swing.JList();
        jSeparator9 = new javax.swing.JSeparator();
        pnlArtillery = new javax.swing.JPanel();
        jSeparator18 = new javax.swing.JSeparator();
        jScrollPane24 = new javax.swing.JScrollPane();
        lstChooseArtillery = new javax.swing.JList();
        jSeparator19 = new javax.swing.JSeparator();
        pnlAmmunition = new javax.swing.JPanel();
        jSeparator11 = new javax.swing.JSeparator();
        jScrollPane22 = new javax.swing.JScrollPane();
        lstChooseAmmunition = new javax.swing.JList();
        jSeparator12 = new javax.swing.JSeparator();
        pnlSpecials = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        chkUseTC = new javax.swing.JCheckBox();
        chkFCSAIV = new javax.swing.JCheckBox();
        chkFCSAV = new javax.swing.JCheckBox();
        chkFCSApollo = new javax.swing.JCheckBox();
        chkClanCASE = new javax.swing.JCheckBox();
        pnlSelected = new javax.swing.JPanel();
        jScrollPane23 = new javax.swing.JScrollPane();
        lstSelectedEquipment = new javax.swing.JList();
        pnlControls = new javax.swing.JPanel();
        btnRemoveEquip = new javax.swing.JButton();
        btnClearEquip = new javax.swing.JButton();
        btnAddEquip = new javax.swing.JButton();
        cmbNumEquips = new javax.swing.JComboBox();
        pnlEquipInfo = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblInfoAVSL = new javax.swing.JLabel();
        lblInfoAVSW = new javax.swing.JLabel();
        lblInfoAVCI = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lblInfoIntro = new javax.swing.JLabel();
        lblInfoExtinct = new javax.swing.JLabel();
        lblInfoReintro = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        lblInfoName = new javax.swing.JLabel();
        lblInfoType = new javax.swing.JLabel();
        lblInfoHeat = new javax.swing.JLabel();
        lblInfoDamage = new javax.swing.JLabel();
        lblInfoRange = new javax.swing.JLabel();
        jSeparator13 = new javax.swing.JSeparator();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        lblInfoAmmo = new javax.swing.JLabel();
        lblInfoTonnage = new javax.swing.JLabel();
        lblInfoCrits = new javax.swing.JLabel();
        lblInfoSpecials = new javax.swing.JLabel();
        jSeparator14 = new javax.swing.JSeparator();
        jLabel32 = new javax.swing.JLabel();
        lblInfoCost = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        lblInfoBV = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        lblInfoMountRestrict = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        lblInfoRulesLevel = new javax.swing.JLabel();
        pnlCriticals = new javax.swing.JPanel();
        pnlHDCrits = new javax.swing.JPanel();
        chkHDTurret = new javax.swing.JCheckBox();
        chkHDCASE2 = new javax.swing.JCheckBox();
        jScrollPane10 = new javax.swing.JScrollPane();
        lstHDCrits = new javax.swing.JList();
        pnlCTCrits = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        lstCTCrits = new javax.swing.JList();
        chkCTCASE = new javax.swing.JCheckBox();
        chkCTCASE2 = new javax.swing.JCheckBox();
        pnlLTCrits = new javax.swing.JPanel();
        chkLTCASE = new javax.swing.JCheckBox();
        jScrollPane12 = new javax.swing.JScrollPane();
        lstLTCrits = new javax.swing.JList();
        chkLTCASE2 = new javax.swing.JCheckBox();
        chkLTTurret = new javax.swing.JCheckBox();
        pnlRTCrits = new javax.swing.JPanel();
        jScrollPane13 = new javax.swing.JScrollPane();
        lstRTCrits = new javax.swing.JList();
        chkRTCASE = new javax.swing.JCheckBox();
        chkRTCASE2 = new javax.swing.JCheckBox();
        chkRTTurret = new javax.swing.JCheckBox();
        pnlLACrits = new javax.swing.JPanel();
        scrLACrits = new javax.swing.JScrollPane();
        lstLACrits = new javax.swing.JList();
        chkLALowerArm = new javax.swing.JCheckBox();
        chkLAHand = new javax.swing.JCheckBox();
        chkLACASE2 = new javax.swing.JCheckBox();
        chkLAAES = new javax.swing.JCheckBox();
        pnlRACrits = new javax.swing.JPanel();
        scrRACrits = new javax.swing.JScrollPane();
        lstRACrits = new javax.swing.JList();
        chkRALowerArm = new javax.swing.JCheckBox();
        chkRAHand = new javax.swing.JCheckBox();
        chkRACASE2 = new javax.swing.JCheckBox();
        chkRAAES = new javax.swing.JCheckBox();
        pnlLLCrits = new javax.swing.JPanel();
        jScrollPane16 = new javax.swing.JScrollPane();
        lstLLCrits = new javax.swing.JList();
        chkLLCASE2 = new javax.swing.JCheckBox();
        pnlRLCrits = new javax.swing.JPanel();
        jScrollPane17 = new javax.swing.JScrollPane();
        lstRLCrits = new javax.swing.JList();
        chkRLCASE2 = new javax.swing.JCheckBox();
        pnlEquipmentToPlace = new javax.swing.JPanel();
        jScrollPane18 = new javax.swing.JScrollPane();
        lstCritsToPlace = new javax.swing.JList();
        btnRemoveItemCrits = new javax.swing.JButton();
        onlLoadoutControls = new javax.swing.JPanel();
        btnCompactCrits = new javax.swing.JButton();
        btnClearLoadout = new javax.swing.JButton();
        btnAutoAllocate = new javax.swing.JButton();
        btnSelectiveAllocate = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        chkLegAES = new javax.swing.JCheckBox();
        jLabel61 = new javax.swing.JLabel();
        pnlFluff = new javax.swing.JPanel();
        pnlImage = new javax.swing.JPanel();
        lblFluffImage = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnLoadImage = new javax.swing.JButton();
        btnClearImage = new javax.swing.JButton();
        pnlExport = new javax.swing.JPanel();
        btnExportTXT = new javax.swing.JButton();
        btnExportHTML = new javax.swing.JButton();
        btnExportMTF = new javax.swing.JButton();
        tbpFluffEditors = new javax.swing.JTabbedPane();
        pnlOverview = new javax.swing.JPanel();
        pnlCapabilities = new javax.swing.JPanel();
        pnlHistory = new javax.swing.JPanel();
        pnlDeployment = new javax.swing.JPanel();
        pnlVariants = new javax.swing.JPanel();
        pnlNotables = new javax.swing.JPanel();
        pnlAdditionalFluff = new javax.swing.JPanel();
        pnlManufacturers = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtManufacturer = new javax.swing.JTextField();
        txtEngineManufacturer = new javax.swing.JTextField();
        txtArmorModel = new javax.swing.JTextField();
        txtChassisModel = new javax.swing.JTextField();
        txtCommSystem = new javax.swing.JTextField();
        txtTNTSystem = new javax.swing.JTextField();
        pnlWeaponsManufacturers = new javax.swing.JPanel();
        chkIndividualWeapons = new javax.swing.JCheckBox();
        scpWeaponManufacturers = new javax.swing.JScrollPane();
        tblWeaponManufacturers = new javax.swing.JTable();
        txtManufacturerLocation = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        txtJJModel = new javax.swing.JTextField();
        pnlQuirks = new javax.swing.JPanel();
        lblBattleMechQuirks = new javax.swing.JLabel();
        scpQuirkTable = new javax.swing.JScrollPane();
        tblQuirks = new javax.swing.JTable();
        btnAddQuirk = new javax.swing.JButton();
        pnlCharts = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        lblTonPercStructure = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        lblTonPercEngine = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        lblTonPercHeatSinks = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        lblTonPercEnhance = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        lblTonPercArmor = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        lblTonPercJumpJets = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        lblTonPercWeapons = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        lblTonPercEquips = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        lblDamagePerTon = new javax.swing.JLabel();
        pnlDamageChart = new DamageChart();
        lblLegendTitle = new javax.swing.JLabel();
        chkChartFront = new javax.swing.JCheckBox();
        chkChartRear = new javax.swing.JCheckBox();
        chkChartRight = new javax.swing.JCheckBox();
        chkChartLeft = new javax.swing.JCheckBox();
        btnBracketChart = new javax.swing.JButton();
        chkAverageDamage = new javax.swing.JCheckBox();
        chkShowTextNotGraph = new javax.swing.JCheckBox();
        pnlBattleforce = new javax.swing.JPanel();
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
        jPanel7 = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        jTextAreaBFConversion = new javax.swing.JTextArea();
        pnlInfoPanel = new javax.swing.JPanel();
        txtInfoTonnage = new javax.swing.JTextField();
        txtInfoFreeTons = new javax.swing.JTextField();
        txtInfoMaxHeat = new javax.swing.JTextField();
        txtInfoHeatDiss = new javax.swing.JTextField();
        txtInfoFreeCrits = new javax.swing.JTextField();
        txtInfoUnplaced = new javax.swing.JTextField();
        txtInfoBattleValue = new javax.swing.JTextField();
        txtInfoCost = new javax.swing.JTextField();
        mnuMainMenu = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuNewMech = new javax.swing.JMenuItem();
        mnuLoad = new javax.swing.JMenuItem();
        mnuOpen = new javax.swing.JMenuItem();
        mnuImport = new javax.swing.JMenu();
        mnuImportHMP = new javax.swing.JMenuItem();
        mnuBatchHMP = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JSeparator();
        mnuSave = new javax.swing.JMenuItem();
        mnuSaveAs = new javax.swing.JMenuItem();
        mnuExport = new javax.swing.JMenu();
        mnuExportHTML = new javax.swing.JMenuItem();
        mnuExportMTF = new javax.swing.JMenuItem();
        mnuExportTXT = new javax.swing.JMenuItem();
        mnuExportClipboard = new javax.swing.JMenuItem();
        mnuCreateTCGMech = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JSeparator();
        mnuPrint = new javax.swing.JMenu();
        mnuPrintCurrentMech = new javax.swing.JMenuItem();
        mnuPrintSavedMech = new javax.swing.JMenuItem();
        mnuPrintBatch = new javax.swing.JMenuItem();
        mnuPrintPreview = new javax.swing.JMenuItem();
        mnuPostS7 = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JSeparator();
        mnuExit = new javax.swing.JMenuItem();
        mnuClearFluff = new javax.swing.JMenu();
        mnuSummary = new javax.swing.JMenuItem();
        mnuCostBVBreakdown = new javax.swing.JMenuItem();
        mnuTextTRO = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JSeparator();
        mnuBFB = new javax.swing.JMenuItem();
        jSeparator27 = new javax.swing.JSeparator();
        mnuOptions = new javax.swing.JMenuItem();
        mnuViewToolbar = new javax.swing.JCheckBoxMenuItem();
        mnuClearUserData = new javax.swing.JMenuItem();
        jSeparator30 = new javax.swing.JSeparator();
        mnuUnlock = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mnuCredits = new javax.swing.JMenuItem();
        mnuAboutSSW = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(750, 515));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        tlbIconBar.setFloatable(false);
        tlbIconBar.setFocusable(false);
        tlbIconBar.setMaximumSize(new java.awt.Dimension(30, 30));
        tlbIconBar.setMinimumSize(new java.awt.Dimension(30, 30));
        tlbIconBar.setPreferredSize(new java.awt.Dimension(30, 30));
        tlbIconBar.setRequestFocusEnabled(false);
        tlbIconBar.setVerifyInputWhenFocusTarget(false);

        btnNewIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/document--plus.png"))); // NOI18N
        btnNewIcon.setToolTipText("New Mech");
        btnNewIcon.setFocusable(false);
        btnNewIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNewIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNewIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewIconActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnNewIcon);

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/folder-open-document.png"))); // NOI18N
        btnOpen.setToolTipText("Open Mech");
        btnOpen.setFocusable(false);
        btnOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnOpen);

        btnSaveIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/disk-black.png"))); // NOI18N
        btnSaveIcon.setToolTipText("Save Mech");
        btnSaveIcon.setFocusable(false);
        btnSaveIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveIconActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnSaveIcon);

        btnPrintPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/projection-screen.png"))); // NOI18N
        btnPrintPreview.setToolTipText("Print Preview");
        btnPrintPreview.setFocusable(false);
        btnPrintPreview.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrintPreview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintPreviewActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnPrintPreview);
        tlbIconBar.add(jSeparator24);

        btnPrintIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/printer.png"))); // NOI18N
        btnPrintIcon.setToolTipText("Print Current Mech");
        btnPrintIcon.setFocusable(false);
        btnPrintIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrintIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintIconActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnPrintIcon);
        tlbIconBar.add(jSeparator22);

        btnExportClipboardIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/document-clipboard.png"))); // NOI18N
        btnExportClipboardIcon.setToolTipText("Export Text to Clipboard");
        btnExportClipboardIcon.setFocusable(false);
        btnExportClipboardIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportClipboardIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportClipboardIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportClipboardIconActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnExportClipboardIcon);

        btnExportHTMLIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/document-image.png"))); // NOI18N
        btnExportHTMLIcon.setToolTipText("Export HTML");
        btnExportHTMLIcon.setFocusable(false);
        btnExportHTMLIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportHTMLIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportHTMLIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportHTMLIconActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnExportHTMLIcon);

        btnExportTextIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/document-text.png"))); // NOI18N
        btnExportTextIcon.setToolTipText("Export Text");
        btnExportTextIcon.setFocusable(false);
        btnExportTextIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportTextIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportTextIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportTextIconActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnExportTextIcon);

        btnExportMTFIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/document--arrow.png"))); // NOI18N
        btnExportMTFIcon.setToolTipText("Export MTF");
        btnExportMTFIcon.setFocusable(false);
        btnExportMTFIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportMTFIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportMTFIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportMTFIconActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnExportMTFIcon);

        btnChatInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/balloon.png"))); // NOI18N
        btnChatInfo.setToolTipText("Copy Chat Line");
        btnChatInfo.setFocusable(false);
        btnChatInfo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnChatInfo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnChatInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChatInfoActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnChatInfo);
        tlbIconBar.add(jSeparator23);

        btnPostToS7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/server.png"))); // NOI18N
        btnPostToS7.setToolTipText("Upload to Solaris7.com");
        btnPostToS7.setFocusable(false);
        btnPostToS7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPostToS7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPostToS7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPostToS7ActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnPostToS7);
        tlbIconBar.add(jSeparator25);

        btnAddToForceList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/clipboard--plus.png"))); // NOI18N
        btnAddToForceList.setToolTipText("Add  to Force List");
        btnAddToForceList.setFocusable(false);
        btnAddToForceList.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddToForceList.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddToForceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToForceListActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnAddToForceList);

        btnForceList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/clipboard.png"))); // NOI18N
        btnForceList.setToolTipText("Force List");
        btnForceList.setFocusable(false);
        btnForceList.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnForceList.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnForceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForceListActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnForceList);
        tlbIconBar.add(jSeparator26);

        btnOptionsIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/gear.png"))); // NOI18N
        btnOptionsIcon.setToolTipText("View Options");
        btnOptionsIcon.setFocusable(false);
        btnOptionsIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOptionsIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOptionsIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOptionsIconActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnOptionsIcon);
        tlbIconBar.add(jSeparator21);

        lblSelectVariant.setText("Selected Variant: ");
        lblSelectVariant.setEnabled(false);
        tlbIconBar.add(lblSelectVariant);

        cmbOmniVariant.setEnabled(false);
        cmbOmniVariant.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbOmniVariant.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbOmniVariant.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbOmniVariant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbOmniVariantActionPerformed(evt);
            }
        });
        tlbIconBar.add(cmbOmniVariant);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(tlbIconBar, gridBagConstraints);

        pnlBasicSetup.setLayout(new java.awt.GridBagLayout());

        pnlBasicInformation.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Basic Information"));
        pnlBasicInformation.setLayout(new java.awt.GridBagLayout());

        lblMechName.setText("Mech Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicInformation.add(lblMechName, gridBagConstraints);

        txtMechName.setMaximumSize(new java.awt.Dimension(150, 20));
        txtMechName.setMinimumSize(new java.awt.Dimension(150, 20));
        txtMechName.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        pnlBasicInformation.add(txtMechName, gridBagConstraints);
        MouseListener mlMechName = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtMechName.addMouseListener( mlMechName );

        lblModel.setText("Model:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicInformation.add(lblModel, gridBagConstraints);

        txtMechModel.setMaximumSize(new java.awt.Dimension(150, 20));
        txtMechModel.setMinimumSize(new java.awt.Dimension(150, 20));
        txtMechModel.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        pnlBasicInformation.add(txtMechModel, gridBagConstraints);
        MouseListener mlMechModel = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtMechModel.addMouseListener( mlMechModel );

        lblMechEra.setText("Era:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicInformation.add(lblMechEra, gridBagConstraints);

        cmbMechEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Age of War/Star League", "Succession Wars", "Clan Invasion", "Dark Ages", "All Eras (non-canon)" }));
        cmbMechEra.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbMechEra.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbMechEra.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbMechEra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMechEraActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        pnlBasicInformation.add(cmbMechEra, gridBagConstraints);

        lblEraYears.setText("2443~2800");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlBasicInformation.add(lblEraYears, gridBagConstraints);

        lblProdYear.setText("Prod Year/Era:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicInformation.add(lblProdYear, gridBagConstraints);

        txtProdYear.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtProdYear.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtProdYear.setMaximumSize(new java.awt.Dimension(60, 20));
        txtProdYear.setMinimumSize(new java.awt.Dimension(60, 20));
        txtProdYear.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        pnlBasicInformation.add(txtProdYear, gridBagConstraints);
        MouseListener mlProdYear = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtProdYear.addMouseListener( mlProdYear );

        chkYearRestrict.setText("Restrict Availability by Year");
        chkYearRestrict.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkYearRestrictActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlBasicInformation.add(chkYearRestrict, gridBagConstraints);

        lblTechBase.setText("Tech Base:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicInformation.add(lblTechBase, gridBagConstraints);

        cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere" }));
        cmbTechBase.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbTechBase.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbTechBase.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbTechBase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTechBaseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        pnlBasicInformation.add(cmbTechBase, gridBagConstraints);

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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        pnlBasicInformation.add(cmbRulesLevel, gridBagConstraints);

        lblRulesLevel.setText("Rules Level:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicInformation.add(lblRulesLevel, gridBagConstraints);

        jLabel65.setText("Source:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlBasicInformation.add(jLabel65, gridBagConstraints);

        txtSource.setMaximumSize(new java.awt.Dimension(150, 20));
        txtSource.setMinimumSize(new java.awt.Dimension(150, 20));
        txtSource.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        pnlBasicInformation.add(txtSource, gridBagConstraints);
        MouseListener mlSource = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtSource.addMouseListener( mlSource );
        pnlBasicInformation.add(jSeparator28, new java.awt.GridBagConstraints());
        pnlBasicInformation.add(jSeparator29, new java.awt.GridBagConstraints());

        cmbProductionEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Age of War", "Star League", "Early Succession War", "Late Succession War", "Clan Invasion", "Civil War", "Jihad", "Republic", "Dark Ages" }));
        cmbProductionEra.setMaximumSize(new java.awt.Dimension(90, 20));
        cmbProductionEra.setMinimumSize(new java.awt.Dimension(90, 20));
        cmbProductionEra.setPreferredSize(new java.awt.Dimension(90, 20));
        cmbProductionEra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProductionEraActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        pnlBasicInformation.add(cmbProductionEra, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlBasicSetup.add(pnlBasicInformation, gridBagConstraints);

        pnlChassis.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Chassis"));
        pnlChassis.setLayout(new java.awt.GridBagLayout());

        lblTonnage.setText("Tonnage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlChassis.add(lblTonnage, gridBagConstraints);

        cmbTonnage.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95", "100" }));
        cmbTonnage.setSelectedIndex(2);
        cmbTonnage.setMaximumSize(new java.awt.Dimension(60, 20));
        cmbTonnage.setMinimumSize(new java.awt.Dimension(60, 20));
        cmbTonnage.setPreferredSize(new java.awt.Dimension(60, 20));
        cmbTonnage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTonnageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlChassis.add(cmbTonnage, gridBagConstraints);

        lblMechType.setText("Light Mech");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlChassis.add(lblMechType, gridBagConstraints);

        lblMotiveType.setText("Motive Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlChassis.add(lblMotiveType, gridBagConstraints);

        cmbMotiveType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Biped", "Quad" }));
        cmbMotiveType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbMotiveType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbMotiveType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbMotiveType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMotiveTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlChassis.add(cmbMotiveType, gridBagConstraints);

        lblEngineType.setText("Engine Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlChassis.add(lblEngineType, gridBagConstraints);

        cmbEngineType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Fusion", "Fusion XL" }));
        cmbEngineType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbEngineType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbEngineType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbEngineType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEngineTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlChassis.add(cmbEngineType, gridBagConstraints);

        lblInternalType.setText("Internal Structure:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlChassis.add(lblInternalType, gridBagConstraints);

        cmbInternalType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard", "Endo Steel" }));
        cmbInternalType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbInternalType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbInternalType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbInternalType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbInternalTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        pnlChassis.add(cmbInternalType, gridBagConstraints);

        lblGyroType.setText("Gyro Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlChassis.add(lblGyroType, gridBagConstraints);

        cmbGyroType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard" }));
        cmbGyroType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbGyroType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbGyroType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbGyroType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGyroTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        pnlChassis.add(cmbGyroType, gridBagConstraints);

        lblCockpit.setText("Cockpit Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlChassis.add(lblCockpit, gridBagConstraints);

        cmbCockpitType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard" }));
        cmbCockpitType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbCockpitType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbCockpitType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbCockpitType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCockpitTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        pnlChassis.add(cmbCockpitType, gridBagConstraints);

        lblPhysEnhance.setText("Enhancements:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlChassis.add(lblPhysEnhance, gridBagConstraints);

        cmbPhysEnhance.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "MASC" }));
        cmbPhysEnhance.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbPhysEnhance.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbPhysEnhance.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbPhysEnhance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPhysEnhanceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        pnlChassis.add(cmbPhysEnhance, gridBagConstraints);

        chkOmnimech.setText("Omnimech");
        chkOmnimech.setEnabled(false);
        chkOmnimech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOmnimechActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        pnlChassis.add(chkOmnimech, gridBagConstraints);

        cmbMechType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BattleMech", "IndustrialMech" }));
        cmbMechType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbMechType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbMechType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbMechType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMechTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        pnlChassis.add(cmbMechType, gridBagConstraints);

        lblUnitType.setText("Mech Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlChassis.add(lblUnitType, gridBagConstraints);

        chkCommandConsole.setText("Use Command Console");
        chkCommandConsole.setEnabled(false);
        chkCommandConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCommandConsoleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlChassis.add(chkCommandConsole, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlBasicSetup.add(pnlChassis, gridBagConstraints);

        pnlHeatSinks.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Heat Sinks"));
        pnlHeatSinks.setLayout(new java.awt.GridBagLayout());

        lblHeatSinkType.setText("Heat Sink Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlHeatSinks.add(lblHeatSinkType, gridBagConstraints);

        cmbHeatSinkType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Single", "Double" }));
        cmbHeatSinkType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbHeatSinkType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbHeatSinkType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbHeatSinkType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbHeatSinkTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        pnlHeatSinks.add(cmbHeatSinkType, gridBagConstraints);

        lblHSNumber.setText("Number of Heat Sinks:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlHeatSinks.add(lblHSNumber, gridBagConstraints);

        spnNumberOfHS.setModel(new javax.swing.SpinnerNumberModel(10, 10, 65, 1));
        spnNumberOfHS.setMaximumSize(new java.awt.Dimension(45, 20));
        spnNumberOfHS.setMinimumSize(new java.awt.Dimension(45, 20));
        spnNumberOfHS.setPreferredSize(new java.awt.Dimension(45, 20));
        spnNumberOfHS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnNumberOfHSStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlHeatSinks.add(spnNumberOfHS, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlBasicSetup.add(pnlHeatSinks, gridBagConstraints);

        pnlMovement.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Movement"));
        pnlMovement.setLayout(new java.awt.GridBagLayout());

        lblWalkMP.setText("Walking MP:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlMovement.add(lblWalkMP, gridBagConstraints);

        spnWalkMP.setModel(new javax.swing.SpinnerNumberModel(1, 1, 20, 1));
        spnWalkMP.setMaximumSize(new java.awt.Dimension(45, 20));
        spnWalkMP.setMinimumSize(new java.awt.Dimension(45, 20));
        spnWalkMP.setPreferredSize(new java.awt.Dimension(45, 20));
        spnWalkMP.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnWalkMPStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlMovement.add(spnWalkMP, gridBagConstraints);

        lblRunMPLabel.setText("Running MP:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlMovement.add(lblRunMPLabel, gridBagConstraints);

        lblRunMP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRunMP.setText("2");
        lblRunMP.setMaximumSize(new java.awt.Dimension(45, 20));
        lblRunMP.setMinimumSize(new java.awt.Dimension(45, 20));
        lblRunMP.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlMovement.add(lblRunMP, gridBagConstraints);

        lblJumpMP.setText("Jumping MP:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlMovement.add(lblJumpMP, gridBagConstraints);

        spnJumpMP.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1, 1));
        spnJumpMP.setMaximumSize(new java.awt.Dimension(45, 20));
        spnJumpMP.setMinimumSize(new java.awt.Dimension(45, 20));
        spnJumpMP.setPreferredSize(new java.awt.Dimension(45, 20));
        spnJumpMP.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnJumpMPStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlMovement.add(spnJumpMP, gridBagConstraints);

        cmbJumpJetType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard Jump Jet", "Improved Jump Jet" }));
        cmbJumpJetType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbJumpJetType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbJumpJetType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbJumpJetType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbJumpJetTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 0, 0);
        pnlMovement.add(cmbJumpJetType, gridBagConstraints);

        jLabel36.setText("Jump Jet Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pnlMovement.add(jLabel36, gridBagConstraints);

        jLabel53.setText("Booster MP:");
        jLabel53.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlMovement.add(jLabel53, gridBagConstraints);

        spnBoosterMP.setEnabled(false);
        spnBoosterMP.setMaximumSize(new java.awt.Dimension(45, 20));
        spnBoosterMP.setMinimumSize(new java.awt.Dimension(45, 20));
        spnBoosterMP.setPreferredSize(new java.awt.Dimension(45, 20));
        spnBoosterMP.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnBoosterMPStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        pnlMovement.add(spnBoosterMP, gridBagConstraints);

        chkBoosters.setText("'Mech Jump Boosters");
        chkBoosters.setEnabled(false);
        chkBoosters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBoostersActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        pnlMovement.add(chkBoosters, gridBagConstraints);

        lblMoveSummary.setText("W/R/J/B: 12/20/12/12");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        pnlMovement.add(lblMoveSummary, gridBagConstraints);

        jLabel1.setText("Engine Rating: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        pnlMovement.add(jLabel1, gridBagConstraints);

        txtEngineRating.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtEngineRating.setText("100");
        txtEngineRating.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtEngineRating.setEnabled(false);
        txtEngineRating.setMaximumSize(new java.awt.Dimension(65, 20));
        txtEngineRating.setMinimumSize(new java.awt.Dimension(65, 20));
        txtEngineRating.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 0, 0);
        pnlMovement.add(txtEngineRating, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlBasicSetup.add(pnlMovement, gridBagConstraints);

        pnlOmniInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Omnimech Configuration"));
        pnlOmniInfo.setLayout(new java.awt.GridBagLayout());

        btnLockChassis.setText("Lock Chassis");
        btnLockChassis.setEnabled(false);
        btnLockChassis.setMaximumSize(new java.awt.Dimension(200, 23));
        btnLockChassis.setMinimumSize(new java.awt.Dimension(105, 23));
        btnLockChassis.setPreferredSize(new java.awt.Dimension(120, 23));
        btnLockChassis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLockChassisActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlOmniInfo.add(btnLockChassis, gridBagConstraints);

        btnAddVariant.setText("Add Variant");
        btnAddVariant.setEnabled(false);
        btnAddVariant.setMaximumSize(new java.awt.Dimension(200, 23));
        btnAddVariant.setMinimumSize(new java.awt.Dimension(80, 23));
        btnAddVariant.setPreferredSize(new java.awt.Dimension(120, 23));
        btnAddVariant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddVariantActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlOmniInfo.add(btnAddVariant, gridBagConstraints);

        btnDeleteVariant.setText("Delete Variant");
        btnDeleteVariant.setEnabled(false);
        btnDeleteVariant.setMaximumSize(new java.awt.Dimension(200, 23));
        btnDeleteVariant.setMinimumSize(new java.awt.Dimension(80, 23));
        btnDeleteVariant.setPreferredSize(new java.awt.Dimension(120, 23));
        btnDeleteVariant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteVariantActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlOmniInfo.add(btnDeleteVariant, gridBagConstraints);

        btnRenameVariant.setText("Rename Variant");
        btnRenameVariant.setEnabled(false);
        btnRenameVariant.setMinimumSize(new java.awt.Dimension(80, 23));
        btnRenameVariant.setPreferredSize(new java.awt.Dimension(120, 23));
        btnRenameVariant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRenameVariantActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlOmniInfo.add(btnRenameVariant, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlBasicSetup.add(pnlOmniInfo, gridBagConstraints);

        pnlBasicSummary.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Basic Setup Summary"));
        pnlBasicSummary.setLayout(new java.awt.GridBagLayout());

        lblSumStructure.setText("Internal Structure:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicSummary.add(lblSumStructure, gridBagConstraints);

        txtSumIntTon.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumIntTon.setText("000.00");
        txtSumIntTon.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumIntTon.setEnabled(false);
        txtSumIntTon.setMaximumSize(new java.awt.Dimension(50, 20));
        txtSumIntTon.setMinimumSize(new java.awt.Dimension(50, 20));
        txtSumIntTon.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlBasicSummary.add(txtSumIntTon, gridBagConstraints);

        lblSumEngine.setText("Engine:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicSummary.add(lblSumEngine, gridBagConstraints);

        txtSumEngTon.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumEngTon.setText("000.00");
        txtSumEngTon.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumEngTon.setEnabled(false);
        txtSumEngTon.setMaximumSize(new java.awt.Dimension(50, 20));
        txtSumEngTon.setMinimumSize(new java.awt.Dimension(50, 20));
        txtSumEngTon.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        pnlBasicSummary.add(txtSumEngTon, gridBagConstraints);

        lblSumGyro.setText("Gyro:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicSummary.add(lblSumGyro, gridBagConstraints);

        txtSumGyrTon.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumGyrTon.setText("000.00");
        txtSumGyrTon.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumGyrTon.setEnabled(false);
        txtSumGyrTon.setMaximumSize(new java.awt.Dimension(50, 20));
        txtSumGyrTon.setMinimumSize(new java.awt.Dimension(50, 20));
        txtSumGyrTon.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        pnlBasicSummary.add(txtSumGyrTon, gridBagConstraints);

        lblSumHeadItem.setText("Item");
        pnlBasicSummary.add(lblSumHeadItem, new java.awt.GridBagConstraints());

        lblSumHeadTons.setText("Tonnage");
        pnlBasicSummary.add(lblSumHeadTons, new java.awt.GridBagConstraints());

        lblSumHeadCrits.setText("Crits");
        pnlBasicSummary.add(lblSumHeadCrits, new java.awt.GridBagConstraints());

        txtSumIntCrt.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumIntCrt.setText("00");
        txtSumIntCrt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumIntCrt.setEnabled(false);
        txtSumIntCrt.setMaximumSize(new java.awt.Dimension(40, 20));
        txtSumIntCrt.setMinimumSize(new java.awt.Dimension(40, 20));
        txtSumIntCrt.setPreferredSize(new java.awt.Dimension(40, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pnlBasicSummary.add(txtSumIntCrt, gridBagConstraints);

        txtSumEngCrt.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumEngCrt.setText("00");
        txtSumEngCrt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumEngCrt.setEnabled(false);
        txtSumEngCrt.setMaximumSize(new java.awt.Dimension(40, 20));
        txtSumEngCrt.setMinimumSize(new java.awt.Dimension(40, 20));
        txtSumEngCrt.setPreferredSize(new java.awt.Dimension(40, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        pnlBasicSummary.add(txtSumEngCrt, gridBagConstraints);

        txtSumGyrCrt.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumGyrCrt.setText("00");
        txtSumGyrCrt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumGyrCrt.setEnabled(false);
        txtSumGyrCrt.setMaximumSize(new java.awt.Dimension(40, 20));
        txtSumGyrCrt.setMinimumSize(new java.awt.Dimension(40, 20));
        txtSumGyrCrt.setPreferredSize(new java.awt.Dimension(40, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        pnlBasicSummary.add(txtSumGyrCrt, gridBagConstraints);

        lblSumCockpit.setText("Cockpit:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicSummary.add(lblSumCockpit, gridBagConstraints);

        txtSumCocTon.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumCocTon.setText("000.00");
        txtSumCocTon.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumCocTon.setEnabled(false);
        txtSumCocTon.setMaximumSize(new java.awt.Dimension(50, 20));
        txtSumCocTon.setMinimumSize(new java.awt.Dimension(50, 20));
        txtSumCocTon.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        pnlBasicSummary.add(txtSumCocTon, gridBagConstraints);

        txtSumCocCrt.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumCocCrt.setText("00");
        txtSumCocCrt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumCocCrt.setEnabled(false);
        txtSumCocCrt.setMaximumSize(new java.awt.Dimension(40, 20));
        txtSumCocCrt.setMinimumSize(new java.awt.Dimension(40, 20));
        txtSumCocCrt.setPreferredSize(new java.awt.Dimension(40, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        pnlBasicSummary.add(txtSumCocCrt, gridBagConstraints);

        lblSumEnhance.setText("Enhancements:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicSummary.add(lblSumEnhance, gridBagConstraints);

        txtSumEnhTon.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumEnhTon.setText("000.00");
        txtSumEnhTon.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumEnhTon.setEnabled(false);
        txtSumEnhTon.setMaximumSize(new java.awt.Dimension(50, 20));
        txtSumEnhTon.setMinimumSize(new java.awt.Dimension(50, 20));
        txtSumEnhTon.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        pnlBasicSummary.add(txtSumEnhTon, gridBagConstraints);

        txtSumEnhCrt.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumEnhCrt.setText("00");
        txtSumEnhCrt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumEnhCrt.setEnabled(false);
        txtSumEnhCrt.setMaximumSize(new java.awt.Dimension(40, 20));
        txtSumEnhCrt.setMinimumSize(new java.awt.Dimension(40, 20));
        txtSumEnhCrt.setPreferredSize(new java.awt.Dimension(40, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        pnlBasicSummary.add(txtSumEnhCrt, gridBagConstraints);

        lblSumHeatSinks.setText("Heat Sinks:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicSummary.add(lblSumHeatSinks, gridBagConstraints);

        txtSumHSTon.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumHSTon.setText("000.00");
        txtSumHSTon.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumHSTon.setEnabled(false);
        txtSumHSTon.setMaximumSize(new java.awt.Dimension(50, 20));
        txtSumHSTon.setMinimumSize(new java.awt.Dimension(50, 20));
        txtSumHSTon.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        pnlBasicSummary.add(txtSumHSTon, gridBagConstraints);

        txtSumHSCrt.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumHSCrt.setText("00");
        txtSumHSCrt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumHSCrt.setEnabled(false);
        txtSumHSCrt.setMaximumSize(new java.awt.Dimension(40, 20));
        txtSumHSCrt.setMinimumSize(new java.awt.Dimension(40, 20));
        txtSumHSCrt.setPreferredSize(new java.awt.Dimension(40, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        pnlBasicSummary.add(txtSumHSCrt, gridBagConstraints);

        lblSumJJ.setText("Jump Jets:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicSummary.add(lblSumJJ, gridBagConstraints);

        txtSumJJTon.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumJJTon.setText("000.00");
        txtSumJJTon.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumJJTon.setEnabled(false);
        txtSumJJTon.setMaximumSize(new java.awt.Dimension(50, 20));
        txtSumJJTon.setMinimumSize(new java.awt.Dimension(50, 20));
        txtSumJJTon.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        pnlBasicSummary.add(txtSumJJTon, gridBagConstraints);

        txtSumJJCrt.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumJJCrt.setText("00");
        txtSumJJCrt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumJJCrt.setEnabled(false);
        txtSumJJCrt.setMaximumSize(new java.awt.Dimension(40, 20));
        txtSumJJCrt.setMinimumSize(new java.awt.Dimension(40, 20));
        txtSumJJCrt.setPreferredSize(new java.awt.Dimension(40, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        pnlBasicSummary.add(txtSumJJCrt, gridBagConstraints);

        txtSumIntACode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumIntACode.setText("A/C-E-D");
        txtSumIntACode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumIntACode.setEnabled(false);
        txtSumIntACode.setMaximumSize(new java.awt.Dimension(65, 20));
        txtSumIntACode.setMinimumSize(new java.awt.Dimension(65, 20));
        txtSumIntACode.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBasicSummary.add(txtSumIntACode, gridBagConstraints);

        txtSumEngACode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumEngACode.setText("C-E-D");
        txtSumEngACode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumEngACode.setEnabled(false);
        txtSumEngACode.setMaximumSize(new java.awt.Dimension(65, 20));
        txtSumEngACode.setMinimumSize(new java.awt.Dimension(65, 20));
        txtSumEngACode.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBasicSummary.add(txtSumEngACode, gridBagConstraints);

        txtSumGyrACode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumGyrACode.setText("C-E-D");
        txtSumGyrACode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumGyrACode.setEnabled(false);
        txtSumGyrACode.setMaximumSize(new java.awt.Dimension(65, 20));
        txtSumGyrACode.setMinimumSize(new java.awt.Dimension(65, 20));
        txtSumGyrACode.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBasicSummary.add(txtSumGyrACode, gridBagConstraints);

        txtSumCocACode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumCocACode.setText("C-E-D");
        txtSumCocACode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumCocACode.setEnabled(false);
        txtSumCocACode.setMaximumSize(new java.awt.Dimension(65, 20));
        txtSumCocACode.setMinimumSize(new java.awt.Dimension(65, 20));
        txtSumCocACode.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBasicSummary.add(txtSumCocACode, gridBagConstraints);

        txtSumHSACode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumHSACode.setText("C-E-D");
        txtSumHSACode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumHSACode.setEnabled(false);
        txtSumHSACode.setMaximumSize(new java.awt.Dimension(65, 20));
        txtSumHSACode.setMinimumSize(new java.awt.Dimension(65, 20));
        txtSumHSACode.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBasicSummary.add(txtSumHSACode, gridBagConstraints);

        txtSumEnhACode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumEnhACode.setText("C-E-D");
        txtSumEnhACode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumEnhACode.setEnabled(false);
        txtSumEnhACode.setMaximumSize(new java.awt.Dimension(65, 20));
        txtSumEnhACode.setMinimumSize(new java.awt.Dimension(65, 20));
        txtSumEnhACode.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBasicSummary.add(txtSumEnhACode, gridBagConstraints);

        txtSumJJACode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumJJACode.setText("C-E-D");
        txtSumJJACode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumJJACode.setEnabled(false);
        txtSumJJACode.setMaximumSize(new java.awt.Dimension(65, 20));
        txtSumJJACode.setMinimumSize(new java.awt.Dimension(65, 20));
        txtSumJJACode.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBasicSummary.add(txtSumJJACode, gridBagConstraints);

        lblSumHeadAvailable.setText("Availability");
        pnlBasicSummary.add(lblSumHeadAvailable, new java.awt.GridBagConstraints());

        txtSumPAmpsTon.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumPAmpsTon.setText("000.00");
        txtSumPAmpsTon.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumPAmpsTon.setEnabled(false);
        txtSumPAmpsTon.setMaximumSize(new java.awt.Dimension(50, 20));
        txtSumPAmpsTon.setMinimumSize(new java.awt.Dimension(50, 20));
        txtSumPAmpsTon.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        pnlBasicSummary.add(txtSumPAmpsTon, gridBagConstraints);

        txtSumPAmpsACode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumPAmpsACode.setText("C-E-D");
        txtSumPAmpsACode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumPAmpsACode.setEnabled(false);
        txtSumPAmpsACode.setMaximumSize(new java.awt.Dimension(65, 20));
        txtSumPAmpsACode.setMinimumSize(new java.awt.Dimension(65, 20));
        txtSumPAmpsACode.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBasicSummary.add(txtSumPAmpsACode, gridBagConstraints);

        lblSumPAmps.setText("Power Amplifiers:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicSummary.add(lblSumPAmps, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlBasicSetup.add(pnlBasicSummary, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Experimental Equipment"));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        chkCLPS.setText("Chameleon LPS");
        chkCLPS.setEnabled(false);
        chkCLPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCLPSActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(chkCLPS, gridBagConstraints);

        chkNullSig.setText("Null Signature System");
        chkNullSig.setEnabled(false);
        chkNullSig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNullSigActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(chkNullSig, gridBagConstraints);

        chkBSPFD.setText("Blue Shield PFD");
        chkBSPFD.setEnabled(false);
        chkBSPFD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBSPFDActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(chkBSPFD, gridBagConstraints);

        chkVoidSig.setText("Void Signature System");
        chkVoidSig.setEnabled(false);
        chkVoidSig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVoidSigActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(chkVoidSig, gridBagConstraints);

        chkSupercharger.setText("Supercharger");
        chkSupercharger.setEnabled(false);
        chkSupercharger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSuperchargerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(chkSupercharger, gridBagConstraints);

        cmbSCLoc.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CT", "LT", "RT" }));
        cmbSCLoc.setEnabled(false);
        cmbSCLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSCLocActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel4.add(cmbSCLoc, gridBagConstraints);

        chkBoobyTrap.setText("Booby Trap");
        chkBoobyTrap.setEnabled(false);
        chkBoobyTrap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBoobyTrapActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(chkBoobyTrap, gridBagConstraints);

        chkPartialWing.setText("Partial Wing");
        chkPartialWing.setEnabled(false);
        chkPartialWing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPartialWingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(chkPartialWing, gridBagConstraints);

        chkFHES.setText("Full-Head Ejection System");
        chkFHES.setEnabled(false);
        chkFHES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFHESActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(chkFHES, gridBagConstraints);

        lblSupercharger.setText("Install in:");
        lblSupercharger.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel4.add(lblSupercharger, gridBagConstraints);

        jLabel57.setText("        ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        jPanel4.add(jLabel57, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlBasicSetup.add(jPanel4, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Industrial Equipment"));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.PAGE_AXIS));

        chkEjectionSeat.setText("Ejection Seat");
        chkEjectionSeat.setEnabled(false);
        chkEjectionSeat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEjectionSeatActionPerformed(evt);
            }
        });
        jPanel6.add(chkEjectionSeat);

        chkEnviroSealing.setText("Environmental Sealing");
        chkEnviroSealing.setEnabled(false);
        chkEnviroSealing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEnviroSealingActionPerformed(evt);
            }
        });
        jPanel6.add(chkEnviroSealing);

        chkTracks.setText("Tracks");
        chkTracks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTracksActionPerformed(evt);
            }
        });
        jPanel6.add(chkTracks);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBasicSetup.add(jPanel6, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Construction Options"));
        jPanel8.setLayout(new java.awt.GridBagLayout());

        chkFractional.setText("Use Fractional Accounting");
        chkFractional.setEnabled(false);
        chkFractional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFractionalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel8.add(chkFractional, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlBasicSetup.add(jPanel8, gridBagConstraints);

        tbpMainTabPane.addTab("Basic Setup", pnlBasicSetup);

        pnlArmor.setLayout(new java.awt.GridBagLayout());

        pnlFrontArmor.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Forward Armor"));
        pnlFrontArmor.setLayout(new java.awt.GridBagLayout());

        pnlRLArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "RL", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlRLArmorBox.setLayout(new java.awt.GridBagLayout());

        lblRLHeader.setText("Internal");
        pnlRLArmorBox.add(lblRLHeader, new java.awt.GridBagConstraints());

        lblRLIntPts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRLIntPts.setText("00");
        lblRLIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRLIntPts.setMaximumSize(new java.awt.Dimension(45, 20));
        lblRLIntPts.setMinimumSize(new java.awt.Dimension(45, 20));
        lblRLIntPts.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlRLArmorBox.add(lblRLIntPts, gridBagConstraints);

        lblRLArmorHeader.setText("Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlRLArmorBox.add(lblRLArmorHeader, gridBagConstraints);

        spnRLArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnRLArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnRLArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnRLArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnRLArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlRLArmorBox.add(spnRLArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        pnlFrontArmor.add(pnlRLArmorBox, gridBagConstraints);

        pnlLLArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "LL", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlLLArmorBox.setLayout(new java.awt.GridBagLayout());

        lblLLHeader.setText("Internal");
        pnlLLArmorBox.add(lblLLHeader, new java.awt.GridBagConstraints());

        lblLLIntPts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLLIntPts.setText("00");
        lblLLIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLLIntPts.setMaximumSize(new java.awt.Dimension(45, 20));
        lblLLIntPts.setMinimumSize(new java.awt.Dimension(45, 20));
        lblLLIntPts.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlLLArmorBox.add(lblLLIntPts, gridBagConstraints);

        lblLLArmorHeader.setText("Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlLLArmorBox.add(lblLLArmorHeader, gridBagConstraints);

        spnLLArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnLLArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnLLArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnLLArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLLArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlLLArmorBox.add(spnLLArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        pnlFrontArmor.add(pnlLLArmorBox, gridBagConstraints);

        pnlRAArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "RA", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlRAArmorBox.setLayout(new java.awt.GridBagLayout());

        lblRAHeader.setText("Internal");
        pnlRAArmorBox.add(lblRAHeader, new java.awt.GridBagConstraints());

        lblRAIntPts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRAIntPts.setText("00");
        lblRAIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRAIntPts.setMaximumSize(new java.awt.Dimension(45, 20));
        lblRAIntPts.setMinimumSize(new java.awt.Dimension(45, 20));
        lblRAIntPts.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlRAArmorBox.add(lblRAIntPts, gridBagConstraints);

        lblRAArmorHeader.setText("Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlRAArmorBox.add(lblRAArmorHeader, gridBagConstraints);

        spnRAArmor.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        spnRAArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnRAArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnRAArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnRAArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnRAArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlRAArmorBox.add(spnRAArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        pnlFrontArmor.add(pnlRAArmorBox, gridBagConstraints);

        pnlHDArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Head", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlHDArmorBox.setLayout(new java.awt.GridBagLayout());

        lblHDHeader.setText("Internal");
        pnlHDArmorBox.add(lblHDHeader, new java.awt.GridBagConstraints());

        lblHDIntPts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHDIntPts.setText("00");
        lblHDIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblHDIntPts.setMaximumSize(new java.awt.Dimension(45, 20));
        lblHDIntPts.setMinimumSize(new java.awt.Dimension(45, 20));
        lblHDIntPts.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlHDArmorBox.add(lblHDIntPts, gridBagConstraints);

        lblHDArmorHeader.setText("Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlHDArmorBox.add(lblHDArmorHeader, gridBagConstraints);

        spnHDArmor.setModel(new javax.swing.SpinnerNumberModel(0, 0, 9, 1));
        spnHDArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnHDArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnHDArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnHDArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnHDArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlHDArmorBox.add(spnHDArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlFrontArmor.add(pnlHDArmorBox, gridBagConstraints);

        pnlCTArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "CT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlCTArmorBox.setLayout(new java.awt.GridBagLayout());

        lblCTHeader.setText("Internal");
        pnlCTArmorBox.add(lblCTHeader, new java.awt.GridBagConstraints());

        lblCTIntPts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCTIntPts.setText("00");
        lblCTIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblCTIntPts.setMaximumSize(new java.awt.Dimension(45, 20));
        lblCTIntPts.setMinimumSize(new java.awt.Dimension(45, 20));
        lblCTIntPts.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlCTArmorBox.add(lblCTIntPts, gridBagConstraints);

        lblCTArmorHeader.setText("Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlCTArmorBox.add(lblCTArmorHeader, gridBagConstraints);

        spnCTArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnCTArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnCTArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnCTArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCTArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlCTArmorBox.add(spnCTArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlFrontArmor.add(pnlCTArmorBox, gridBagConstraints);

        pnlLTArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "LT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlLTArmorBox.setLayout(new java.awt.GridBagLayout());

        lblLTHeader.setText("Internal");
        pnlLTArmorBox.add(lblLTHeader, new java.awt.GridBagConstraints());

        lblLTIntPts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLTIntPts.setText("00");
        lblLTIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLTIntPts.setMaximumSize(new java.awt.Dimension(45, 20));
        lblLTIntPts.setMinimumSize(new java.awt.Dimension(45, 20));
        lblLTIntPts.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlLTArmorBox.add(lblLTIntPts, gridBagConstraints);

        lblLTArmorHeader.setText("Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlLTArmorBox.add(lblLTArmorHeader, gridBagConstraints);

        spnLTArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnLTArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnLTArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnLTArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLTArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlLTArmorBox.add(spnLTArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        pnlFrontArmor.add(pnlLTArmorBox, gridBagConstraints);

        pnlRTArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "RT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlRTArmorBox.setLayout(new java.awt.GridBagLayout());

        lblRTHeader.setText("Internal");
        pnlRTArmorBox.add(lblRTHeader, new java.awt.GridBagConstraints());

        lblRTIntPts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRTIntPts.setText("00");
        lblRTIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRTIntPts.setMaximumSize(new java.awt.Dimension(45, 20));
        lblRTIntPts.setMinimumSize(new java.awt.Dimension(45, 20));
        lblRTIntPts.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlRTArmorBox.add(lblRTIntPts, gridBagConstraints);

        lblRTArmorHeader.setText("Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlRTArmorBox.add(lblRTArmorHeader, gridBagConstraints);

        spnRTArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnRTArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnRTArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnRTArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnRTArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlRTArmorBox.add(spnRTArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        pnlFrontArmor.add(pnlRTArmorBox, gridBagConstraints);

        pnlLAArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "LA", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlLAArmorBox.setLayout(new java.awt.GridBagLayout());

        lblLAHeader.setText("Internal");
        pnlLAArmorBox.add(lblLAHeader, new java.awt.GridBagConstraints());

        lblLAIntPts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLAIntPts.setText("00");
        lblLAIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLAIntPts.setMaximumSize(new java.awt.Dimension(45, 20));
        lblLAIntPts.setMinimumSize(new java.awt.Dimension(45, 20));
        lblLAIntPts.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlLAArmorBox.add(lblLAIntPts, gridBagConstraints);

        lblLAArmorHeader.setText("Armor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlLAArmorBox.add(lblLAArmorHeader, gridBagConstraints);

        spnLAArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnLAArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnLAArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnLAArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLAArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlLAArmorBox.add(spnLAArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlFrontArmor.add(pnlLAArmorBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 3;
        pnlArmor.add(pnlFrontArmor, gridBagConstraints);

        pnlRearArmor.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Rear Armor"));
        pnlRearArmor.setLayout(new java.awt.GridBagLayout());

        pnlRTRArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "RTR", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlRTRArmorBox.setLayout(new java.awt.GridBagLayout());

        lblRTRArmorHeader.setText("Armor");
        pnlRTRArmorBox.add(lblRTRArmorHeader, new java.awt.GridBagConstraints());

        spnRTRArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnRTRArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnRTRArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnRTRArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnRTRArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlRTRArmorBox.add(spnRTRArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlRearArmor.add(pnlRTRArmorBox, gridBagConstraints);

        pnlCTRArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "CTR", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlCTRArmorBox.setLayout(new java.awt.GridBagLayout());

        lblCTRArmorHeader.setText("Armor");
        pnlCTRArmorBox.add(lblCTRArmorHeader, new java.awt.GridBagConstraints());

        spnCTRArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnCTRArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnCTRArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnCTRArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCTRArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlCTRArmorBox.add(spnCTRArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlRearArmor.add(pnlCTRArmorBox, gridBagConstraints);

        pnlLTRArmorBox.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "LTR", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlLTRArmorBox.setLayout(new java.awt.GridBagLayout());

        lblLTRArmorHeader.setText("Armor");
        pnlLTRArmorBox.add(lblLTRArmorHeader, new java.awt.GridBagConstraints());

        spnLTRArmor.setMaximumSize(new java.awt.Dimension(45, 20));
        spnLTRArmor.setMinimumSize(new java.awt.Dimension(45, 20));
        spnLTRArmor.setPreferredSize(new java.awt.Dimension(45, 20));
        spnLTRArmor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLTRArmorStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlLTRArmorBox.add(spnLTRArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlRearArmor.add(pnlLTRArmorBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlArmor.add(pnlRearArmor, gridBagConstraints);

        pnlArmorInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Armor Information"));
        pnlArmorInfo.setLayout(new java.awt.GridBagLayout());

        lblArmorCoverage.setText("100% Coverage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 2);
        pnlArmorInfo.add(lblArmorCoverage, gridBagConstraints);

        lblArmorPoints.setText("999 of 999 Armor Points");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 2);
        pnlArmorInfo.add(lblArmorPoints, gridBagConstraints);

        txtSumArmorTon.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumArmorTon.setText("000.00");
        txtSumArmorTon.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumArmorTon.setEnabled(false);
        txtSumArmorTon.setMaximumSize(new java.awt.Dimension(50, 20));
        txtSumArmorTon.setMinimumSize(new java.awt.Dimension(50, 20));
        txtSumArmorTon.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 0);
        pnlArmorInfo.add(txtSumArmorTon, gridBagConstraints);

        txtSumArmorCrt.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtSumArmorCrt.setText("00");
        txtSumArmorCrt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSumArmorCrt.setEnabled(false);
        txtSumArmorCrt.setMaximumSize(new java.awt.Dimension(40, 20));
        txtSumArmorCrt.setMinimumSize(new java.awt.Dimension(40, 20));
        txtSumArmorCrt.setPreferredSize(new java.awt.Dimension(40, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlArmorInfo.add(txtSumArmorCrt, gridBagConstraints);

        lblSumHeadTons1.setText("Tonnage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlArmorInfo.add(lblSumHeadTons1, gridBagConstraints);

        lblSumHeadCrits1.setText("Crits");
        pnlArmorInfo.add(lblSumHeadCrits1, new java.awt.GridBagConstraints());

        lblArmorTonsWasted.setText("0.00 Tons Wasted");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 2);
        pnlArmorInfo.add(lblArmorTonsWasted, gridBagConstraints);

        lblAVInLot.setText("99 Points Left In This 1/2 Ton Lot");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 2, 2);
        pnlArmorInfo.add(lblAVInLot, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlArmor.add(pnlArmorInfo, gridBagConstraints);

        pnlArmorSetup.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Armor Setup"));
        pnlArmorSetup.setLayout(new java.awt.GridBagLayout());

        btnMaxArmor.setText("Maximize Armor Tonnage");
        btnMaxArmor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaxArmorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        pnlArmorSetup.add(btnMaxArmor, gridBagConstraints);

        btnArmorTons.setText("Set Armor Tonnage");
        btnArmorTons.setMaximumSize(new java.awt.Dimension(194, 25));
        btnArmorTons.setMinimumSize(new java.awt.Dimension(194, 25));
        btnArmorTons.setPreferredSize(new java.awt.Dimension(194, 25));
        btnArmorTons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArmorTonsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        pnlArmorSetup.add(btnArmorTons, gridBagConstraints);

        cmbArmorType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Industrial", "Standard", "Ferro-Fibrous" }));
        cmbArmorType.setSelectedIndex(1);
        cmbArmorType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbArmorType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbArmorType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbArmorType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbArmorTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlArmorSetup.add(cmbArmorType, gridBagConstraints);

        lblArmorType.setText("Armor Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        pnlArmorSetup.add(lblArmorType, gridBagConstraints);

        btnBalanceArmor.setSelected(true);
        btnBalanceArmor.setText("Balance Armor by Location");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlArmorSetup.add(btnBalanceArmor, gridBagConstraints);

        btnEfficientArmor.setText("Use Efficient Maximum");
        btnEfficientArmor.setMaximumSize(new java.awt.Dimension(194, 25));
        btnEfficientArmor.setMinimumSize(new java.awt.Dimension(194, 25));
        btnEfficientArmor.setPreferredSize(new java.awt.Dimension(194, 25));
        btnEfficientArmor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEfficientArmorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        pnlArmorSetup.add(btnEfficientArmor, gridBagConstraints);

        btnRemainingArmor.setText("Use Remaining Tonnage");
        btnRemainingArmor.setMaximumSize(new java.awt.Dimension(194, 25));
        btnRemainingArmor.setMinimumSize(new java.awt.Dimension(194, 25));
        btnRemainingArmor.setPreferredSize(new java.awt.Dimension(194, 25));
        btnRemainingArmor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemainingArmorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        pnlArmorSetup.add(btnRemainingArmor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlArmor.add(pnlArmorSetup, gridBagConstraints);

        pnlPatchworkChoosers.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Patchwork Armor Types"));
        pnlPatchworkChoosers.setLayout(new java.awt.GridBagLayout());

        lblPWHDLoc.setText("Head Armor: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPatchworkChoosers.add(lblPWHDLoc, gridBagConstraints);

        lblPWCTLoc.setText("CT Armor: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPatchworkChoosers.add(lblPWCTLoc, gridBagConstraints);

        lblPWLTLoc.setText("LT Armor: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPatchworkChoosers.add(lblPWLTLoc, gridBagConstraints);

        lblPWRTLoc.setText("RT Armor: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPatchworkChoosers.add(lblPWRTLoc, gridBagConstraints);

        lblPWLALoc.setText("LA Armor: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPatchworkChoosers.add(lblPWLALoc, gridBagConstraints);

        lblPWRALoc.setText("RA Armor: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPatchworkChoosers.add(lblPWRALoc, gridBagConstraints);

        lblPWLLLoc.setText("LL Armor: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPatchworkChoosers.add(lblPWLLLoc, gridBagConstraints);

        cmbPWHDType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPWHDType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbPWHDType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbPWHDType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbPWHDType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPWHDTypeActionPerformed(evt);
            }
        });
        pnlPatchworkChoosers.add(cmbPWHDType, new java.awt.GridBagConstraints());

        cmbPWCTType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPWCTType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbPWCTType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbPWCTType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbPWCTType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPWCTTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pnlPatchworkChoosers.add(cmbPWCTType, gridBagConstraints);

        cmbPWLTType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPWLTType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbPWLTType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbPWLTType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbPWLTType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPWLTTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        pnlPatchworkChoosers.add(cmbPWLTType, gridBagConstraints);

        cmbPWRTType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPWRTType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbPWRTType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbPWRTType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbPWRTType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPWRTTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        pnlPatchworkChoosers.add(cmbPWRTType, gridBagConstraints);

        cmbPWLAType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPWLAType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbPWLAType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbPWLAType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbPWLAType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPWLATypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        pnlPatchworkChoosers.add(cmbPWLAType, gridBagConstraints);

        cmbPWRAType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPWRAType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbPWRAType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbPWRAType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbPWRAType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPWRATypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        pnlPatchworkChoosers.add(cmbPWRAType, gridBagConstraints);

        cmbPWLLType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPWLLType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbPWLLType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbPWLLType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbPWLLType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPWLLTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        pnlPatchworkChoosers.add(cmbPWLLType, gridBagConstraints);

        cmbPWRLType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPWRLType.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbPWRLType.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbPWRLType.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbPWRLType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPWRLTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        pnlPatchworkChoosers.add(cmbPWRLType, gridBagConstraints);

        lblPWRLLoc.setText("RL Armor: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlPatchworkChoosers.add(lblPWRLLoc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlArmor.add(pnlPatchworkChoosers, gridBagConstraints);

        tbpMainTabPane.addTab("  Armor  ", pnlArmor);

        pnlEquipment.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbpWeaponChooser.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        tbpWeaponChooser.setMaximumSize(new java.awt.Dimension(300, 300));
        tbpWeaponChooser.setMinimumSize(new java.awt.Dimension(300, 300));
        tbpWeaponChooser.setPreferredSize(new java.awt.Dimension(300, 300));

        pnlBallistic.setLayout(new javax.swing.BoxLayout(pnlBallistic, javax.swing.BoxLayout.Y_AXIS));

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setAlignmentX(0.0F);
        jSeparator3.setAlignmentY(0.0F);
        pnlBallistic.add(jSeparator3);

        jScrollPane8.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane8.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane8.setMaximumSize(new java.awt.Dimension(200, 260));
        jScrollPane8.setMinimumSize(new java.awt.Dimension(200, 260));
        jScrollPane8.setPreferredSize(new java.awt.Dimension(200, 260));

        lstChooseBallistic.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Placeholder" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstChooseBallistic.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstChooseBallistic.setMaximumSize(new java.awt.Dimension(180, 10000));
        lstChooseBallistic.setMinimumSize(new java.awt.Dimension(180, 100));
        lstChooseBallistic.setPreferredSize(null);
        lstChooseBallistic.setVisibleRowCount(16);
        lstChooseBallistic.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstChooseBallisticValueChanged(evt);
            }
        });
        MouseListener mlBallistic = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    btnAddEquipActionPerformed( null );
                }
            }
        };
        lstChooseBallistic.addMouseListener( mlBallistic );
        lstChooseBallistic.setCellRenderer( new ssw.gui.EquipmentListRenderer( this ) );
        jScrollPane8.setViewportView(lstChooseBallistic);

        pnlBallistic.add(jScrollPane8);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setAlignmentX(0.0F);
        jSeparator4.setAlignmentY(0.0F);
        pnlBallistic.add(jSeparator4);

        tbpWeaponChooser.addTab("Ballistic", pnlBallistic);

        pnlEnergy.setLayout(new javax.swing.BoxLayout(pnlEnergy, javax.swing.BoxLayout.Y_AXIS));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setAlignmentX(0.0F);
        jSeparator2.setAlignmentY(0.0F);
        pnlEnergy.add(jSeparator2);

        jScrollPane9.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane9.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane9.setMaximumSize(new java.awt.Dimension(200, 260));
        jScrollPane9.setMinimumSize(new java.awt.Dimension(200, 260));
        jScrollPane9.setPreferredSize(new java.awt.Dimension(200, 260));

        lstChooseEnergy.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Placeholder" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstChooseEnergy.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstChooseEnergy.setMaximumSize(new java.awt.Dimension(180, 10000));
        lstChooseEnergy.setMinimumSize(new java.awt.Dimension(180, 100));
        lstChooseEnergy.setPreferredSize(null);
        lstChooseEnergy.setVisibleRowCount(16);
        lstChooseEnergy.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstChooseEnergyValueChanged(evt);
            }
        });
        MouseListener mlEnergy = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    btnAddEquipActionPerformed( null );
                }
            }
        };
        lstChooseEnergy.addMouseListener( mlEnergy );
        lstChooseEnergy.setCellRenderer( new ssw.gui.EquipmentListRenderer( this ) );
        jScrollPane9.setViewportView(lstChooseEnergy);

        pnlEnergy.add(jScrollPane9);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setAlignmentX(0.0F);
        jSeparator1.setAlignmentY(0.0F);
        pnlEnergy.add(jSeparator1);

        tbpWeaponChooser.addTab("Energy", pnlEnergy);

        pnlMissile.setLayout(new javax.swing.BoxLayout(pnlMissile, javax.swing.BoxLayout.Y_AXIS));

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator5.setAlignmentX(0.0F);
        jSeparator5.setAlignmentY(0.0F);
        pnlMissile.add(jSeparator5);

        jScrollPane19.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane19.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane19.setMaximumSize(new java.awt.Dimension(200, 260));
        jScrollPane19.setMinimumSize(new java.awt.Dimension(200, 260));
        jScrollPane19.setPreferredSize(new java.awt.Dimension(200, 260));

        lstChooseMissile.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Placeholder" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstChooseMissile.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstChooseMissile.setMaximumSize(new java.awt.Dimension(180, 10000));
        lstChooseMissile.setMinimumSize(new java.awt.Dimension(180, 100));
        lstChooseMissile.setPreferredSize(null);
        lstChooseMissile.setVisibleRowCount(16);
        lstChooseMissile.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstChooseMissileValueChanged(evt);
            }
        });
        MouseListener mlMissile = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    btnAddEquipActionPerformed( null );
                }
            }
        };
        lstChooseMissile.addMouseListener( mlMissile );
        lstChooseMissile.setCellRenderer( new ssw.gui.EquipmentListRenderer( this ) );
        jScrollPane19.setViewportView(lstChooseMissile);

        pnlMissile.add(jScrollPane19);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setAlignmentX(0.0F);
        jSeparator6.setAlignmentY(0.0F);
        pnlMissile.add(jSeparator6);

        tbpWeaponChooser.addTab("Missile", pnlMissile);

        pnlPhysical.setLayout(new javax.swing.BoxLayout(pnlPhysical, javax.swing.BoxLayout.Y_AXIS));

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator8.setAlignmentX(0.0F);
        jSeparator8.setAlignmentY(0.0F);
        pnlPhysical.add(jSeparator8);

        jScrollPane20.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane20.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane20.setMaximumSize(new java.awt.Dimension(200, 260));
        jScrollPane20.setMinimumSize(new java.awt.Dimension(200, 260));
        jScrollPane20.setPreferredSize(new java.awt.Dimension(200, 260));

        lstChoosePhysical.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Placeholder" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstChoosePhysical.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstChoosePhysical.setMaximumSize(new java.awt.Dimension(180, 10000));
        lstChoosePhysical.setMinimumSize(new java.awt.Dimension(180, 100));
        lstChoosePhysical.setPreferredSize(null);
        lstChoosePhysical.setVisibleRowCount(16);
        lstChoosePhysical.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstChoosePhysicalValueChanged(evt);
            }
        });
        MouseListener mlPhysical = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    btnAddEquipActionPerformed( null );
                }
            }
        };
        lstChoosePhysical.addMouseListener( mlPhysical );
        lstChoosePhysical.setCellRenderer( new ssw.gui.EquipmentListRenderer( this ) );
        jScrollPane20.setViewportView(lstChoosePhysical);

        pnlPhysical.add(jScrollPane20);

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator7.setAlignmentX(0.0F);
        jSeparator7.setAlignmentY(0.0F);
        pnlPhysical.add(jSeparator7);

        tbpWeaponChooser.addTab("Physical", pnlPhysical);

        pnlEquipmentChooser.setLayout(new javax.swing.BoxLayout(pnlEquipmentChooser, javax.swing.BoxLayout.Y_AXIS));

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator10.setAlignmentX(0.0F);
        jSeparator10.setAlignmentY(0.0F);
        pnlEquipmentChooser.add(jSeparator10);

        jScrollPane21.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane21.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane21.setMaximumSize(new java.awt.Dimension(200, 260));
        jScrollPane21.setMinimumSize(new java.awt.Dimension(200, 260));
        jScrollPane21.setPreferredSize(new java.awt.Dimension(200, 260));

        lstChooseEquipment.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Placeholder" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstChooseEquipment.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstChooseEquipment.setMaximumSize(new java.awt.Dimension(180, 10000));
        lstChooseEquipment.setMinimumSize(new java.awt.Dimension(180, 100));
        lstChooseEquipment.setPreferredSize(null);
        lstChooseEquipment.setVisibleRowCount(16);
        lstChooseEquipment.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstChooseEquipmentValueChanged(evt);
            }
        });
        MouseListener mlEquipment = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    btnAddEquipActionPerformed( null );
                }
            }
        };
        lstChooseEquipment.addMouseListener( mlEquipment );
        lstChooseEquipment.setCellRenderer( new ssw.gui.EquipmentListRenderer( this ) );
        jScrollPane21.setViewportView(lstChooseEquipment);

        pnlEquipmentChooser.add(jScrollPane21);

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator9.setAlignmentX(0.0F);
        jSeparator9.setAlignmentY(0.0F);
        pnlEquipmentChooser.add(jSeparator9);

        tbpWeaponChooser.addTab("Equipment", pnlEquipmentChooser);

        pnlArtillery.setLayout(new javax.swing.BoxLayout(pnlArtillery, javax.swing.BoxLayout.Y_AXIS));

        jSeparator18.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator18.setAlignmentX(0.0F);
        jSeparator18.setAlignmentY(0.0F);
        pnlArtillery.add(jSeparator18);

        jScrollPane24.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane24.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane24.setMaximumSize(new java.awt.Dimension(200, 260));
        jScrollPane24.setMinimumSize(new java.awt.Dimension(200, 260));
        jScrollPane24.setPreferredSize(new java.awt.Dimension(200, 260));

        lstChooseArtillery.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Placeholder" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstChooseArtillery.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstChooseArtillery.setMaximumSize(new java.awt.Dimension(180, 10000));
        lstChooseArtillery.setMinimumSize(new java.awt.Dimension(180, 100));
        lstChooseArtillery.setPreferredSize(null);
        lstChooseArtillery.setVisibleRowCount(16);
        lstChooseArtillery.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstChooseArtilleryValueChanged(evt);
            }
        });
        MouseListener mlArtillery = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    btnAddEquipActionPerformed( null );
                }
            }
        };
        lstChooseArtillery.addMouseListener( mlArtillery );
        lstChooseArtillery.setCellRenderer( new ssw.gui.EquipmentListRenderer( this ) );
        jScrollPane24.setViewportView(lstChooseArtillery);

        pnlArtillery.add(jScrollPane24);

        jSeparator19.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator19.setAlignmentX(0.0F);
        jSeparator19.setAlignmentY(0.0F);
        pnlArtillery.add(jSeparator19);

        tbpWeaponChooser.addTab("Artillery", pnlArtillery);

        pnlAmmunition.setLayout(new javax.swing.BoxLayout(pnlAmmunition, javax.swing.BoxLayout.Y_AXIS));

        jSeparator11.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator11.setAlignmentX(0.0F);
        jSeparator11.setAlignmentY(0.0F);
        pnlAmmunition.add(jSeparator11);

        jScrollPane22.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane22.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane22.setMaximumSize(new java.awt.Dimension(200, 260));
        jScrollPane22.setMinimumSize(new java.awt.Dimension(200, 260));
        jScrollPane22.setPreferredSize(new java.awt.Dimension(200, 260));

        lstChooseAmmunition.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Placeholder" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstChooseAmmunition.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstChooseAmmunition.setMaximumSize(new java.awt.Dimension(180, 10000));
        lstChooseAmmunition.setMinimumSize(new java.awt.Dimension(180, 100));
        lstChooseAmmunition.setPreferredSize(null);
        lstChooseAmmunition.setVisibleRowCount(16);
        lstChooseAmmunition.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstChooseAmmunitionValueChanged(evt);
            }
        });
        MouseListener mlAmmo = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    btnAddEquipActionPerformed( null );
                }
            }
        };
        lstChooseAmmunition.addMouseListener( mlAmmo );
        lstChooseAmmunition.setCellRenderer( new ssw.gui.EquipmentListRenderer( this ) );
        jScrollPane22.setViewportView(lstChooseAmmunition);

        pnlAmmunition.add(jScrollPane22);

        jSeparator12.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator12.setAlignmentX(0.0F);
        jSeparator12.setAlignmentY(0.0F);
        pnlAmmunition.add(jSeparator12);

        tbpWeaponChooser.addTab("Ammunition", pnlAmmunition);

        pnlEquipment.add(tbpWeaponChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        pnlSpecials.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Specials"));
        pnlSpecials.setLayout(new java.awt.GridBagLayout());

        jLabel16.setText("Missile Guidance:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlSpecials.add(jLabel16, gridBagConstraints);

        chkUseTC.setText("Targeting Computer");
        chkUseTC.setEnabled(false);
        chkUseTC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseTCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 2, 0, 0);
        pnlSpecials.add(chkUseTC, gridBagConstraints);

        chkFCSAIV.setText("Use Artemis IV");
        chkFCSAIV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFCSAIVActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlSpecials.add(chkFCSAIV, gridBagConstraints);

        chkFCSAV.setText("Use Artemis V");
        chkFCSAV.setEnabled(false);
        chkFCSAV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFCSAVActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlSpecials.add(chkFCSAV, gridBagConstraints);

        chkFCSApollo.setText("Use MRM Apollo");
        chkFCSApollo.setEnabled(false);
        chkFCSApollo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFCSApolloActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlSpecials.add(chkFCSApollo, gridBagConstraints);

        chkClanCASE.setText("Use Clan CASE");
        chkClanCASE.setEnabled(false);
        chkClanCASE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClanCASEActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 2, 0, 0);
        pnlSpecials.add(chkClanCASE, gridBagConstraints);

        pnlEquipment.add(pnlSpecials, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 100, 160, 220));

        pnlSelected.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selected Equipment"));
        pnlSelected.setMaximumSize(new java.awt.Dimension(212, 286));
        pnlSelected.setMinimumSize(new java.awt.Dimension(212, 286));
        pnlSelected.setLayout(new javax.swing.BoxLayout(pnlSelected, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane23.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane23.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        lstSelectedEquipment.setModel( new javax.swing.DefaultListModel()
        );
        lstSelectedEquipment.setMaximumSize(new java.awt.Dimension(180, 225));
        lstSelectedEquipment.setMinimumSize(new java.awt.Dimension(180, 225));
        lstSelectedEquipment.setPreferredSize(null);
        lstSelectedEquipment.setVisibleRowCount(16);
        lstSelectedEquipment.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstSelectedEquipmentValueChanged(evt);
            }
        });
        lstSelectedEquipment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstSelectedEquipmentKeyPressed(evt);
            }
        });
        MouseListener mlSelect = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                int Index = lstSelectedEquipment.locationToIndex( e.getPoint() );
                if( Index < 0 ) { return; }
                CurItem = (abPlaceable) CurMech.GetLoadout().GetNonCore().get( Index );
                if( e.isPopupTrigger() ) {
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                int Index = lstSelectedEquipment.locationToIndex( e.getPoint() );
                if( Index < 0 ) { return; }
                CurItem = (abPlaceable) CurMech.GetLoadout().GetNonCore().get( Index );
                if( e.isPopupTrigger() ) {
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        lstSelectedEquipment.addMouseListener( mlSelect );
        lstSelectedEquipment.setCellRenderer( new ssw.gui.EquipmentSelectedRenderer( this ) );
        jScrollPane23.setViewportView(lstSelectedEquipment);

        pnlSelected.add(jScrollPane23);

        pnlEquipment.add(pnlSelected, new org.netbeans.lib.awtextra.AbsoluteConstraints(492, 20, 230, 300));

        pnlControls.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Controls"));
        pnlControls.setLayout(new java.awt.GridBagLayout());

        btnRemoveEquip.setText("<<");
        btnRemoveEquip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveEquipActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        pnlControls.add(btnRemoveEquip, gridBagConstraints);

        btnClearEquip.setText("Clear");
        btnClearEquip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearEquipActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 8);
        pnlControls.add(btnClearEquip, gridBagConstraints);

        btnAddEquip.setText(">>");
        btnAddEquip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddEquipActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlControls.add(btnAddEquip, gridBagConstraints);

        cmbNumEquips.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 0);
        pnlControls.add(cmbNumEquips, gridBagConstraints);

        pnlEquipment.add(pnlControls, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 20, 160, -1));

        pnlEquipInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Information"));
        pnlEquipInfo.setLayout(new java.awt.GridBagLayout());

        jLabel17.setText("Availability(AoW/SL)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlEquipInfo.add(jLabel17, gridBagConstraints);

        jLabel18.setText("Availability (SW)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlEquipInfo.add(jLabel18, gridBagConstraints);

        jLabel19.setText("Availability (CI)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlEquipInfo.add(jLabel19, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoAVSL, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoAVSW, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoAVCI, gridBagConstraints);

        jLabel20.setText("Introduction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel20, gridBagConstraints);

        jLabel21.setText("Extinction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel21, gridBagConstraints);

        jLabel22.setText("Reintroduction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel22, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoIntro, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoExtinct, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoReintro, gridBagConstraints);

        jLabel23.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        pnlEquipInfo.add(jLabel23, gridBagConstraints);

        jLabel24.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel24, gridBagConstraints);

        jLabel25.setText("Heat");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel25, gridBagConstraints);

        jLabel26.setText("Damage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel26, gridBagConstraints);

        jLabel27.setText("Range");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel27, gridBagConstraints);

        lblInfoName.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlEquipInfo.add(lblInfoName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoType, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoHeat, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoDamage, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoRange, gridBagConstraints);

        jSeparator13.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        pnlEquipInfo.add(jSeparator13, gridBagConstraints);

        jLabel28.setText("Ammo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel28, gridBagConstraints);

        jLabel29.setText("Tonnage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel29, gridBagConstraints);

        jLabel30.setText("Crits");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel30, gridBagConstraints);

        jLabel31.setText("Specials");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 0);
        pnlEquipInfo.add(jLabel31, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoAmmo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoTonnage, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoCrits, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlEquipInfo.add(lblInfoSpecials, gridBagConstraints);

        jSeparator14.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        pnlEquipInfo.add(jSeparator14, gridBagConstraints);

        jLabel32.setText("Cost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel32, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlEquipInfo.add(lblInfoCost, gridBagConstraints);

        jLabel34.setText("BV");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel34, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlEquipInfo.add(lblInfoBV, gridBagConstraints);

        jLabel33.setText("Mounting Restrictions");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 3);
        pnlEquipInfo.add(jLabel33, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 4, 0);
        pnlEquipInfo.add(lblInfoMountRestrict, gridBagConstraints);

        jLabel55.setText("Rules Level");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel55, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlEquipInfo.add(lblInfoRulesLevel, gridBagConstraints);

        pnlEquipment.add(pnlEquipInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 325, 710, -1));

        tbpMainTabPane.addTab("Equipment", pnlEquipment);

        pnlCriticals.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHDCrits.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Head", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlHDCrits.setMaximumSize(new java.awt.Dimension(116, 120));
        pnlHDCrits.setMinimumSize(new java.awt.Dimension(116, 120));
        pnlHDCrits.setLayout(new java.awt.GridBagLayout());

        chkHDTurret.setText("Turret");
        chkHDTurret.setEnabled(false);
        chkHDTurret.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHDTurretActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHDCrits.add(chkHDTurret, gridBagConstraints);

        chkHDCASE2.setText("C.A.S.E. II");
        chkHDCASE2.setEnabled(false);
        chkHDCASE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHDCASE2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHDCrits.add(chkHDCASE2, gridBagConstraints);

        jScrollPane10.setPreferredSize(new java.awt.Dimension(105, 87));

        lstHDCrits.setFont( PrintConsts.BaseCritFont );
        lstHDCrits.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Head", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstHDCrits.setDragEnabled(true);
        lstHDCrits.setMaximumSize(new java.awt.Dimension(98, 50));
        lstHDCrits.setMinimumSize(new java.awt.Dimension(98, 50));
        lstHDCrits.setPreferredSize(new java.awt.Dimension(98, 50));
        lstHDCrits.setVisibleRowCount(6);
        lstHDCrits.setTransferHandler( new thHDTransferHandler( this, CurMech ) );
        lstHDCrits.setDropMode( DropMode.ON );
        MouseListener mlHDCrits = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    int index = lstHDCrits.locationToIndex( e.getPoint() );
                    abPlaceable[] a = CurMech.GetLoadout().GetHDCrits();
                    if( ! a[index].LocationLocked() ) {
                        if( a[index].CanSplit() && a[index].Contiguous() ) {
                            CurMech.GetLoadout().UnallocateAll( a[index], false );
                        } else {
                            CurMech.GetLoadout().UnallocateByIndex( index, a );
                        }
                    }
                    RefreshInfoPane();
                }
            }
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetHDCrits();
                    int index = lstHDCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_HD;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetHDCrits();
                    int index = lstHDCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_HD;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstHDCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetHDCrits()[index];
                    CurLocation = LocationIndex.MECH_LOC_HD;
                }
            }
        };
        lstHDCrits.addMouseListener( mlHDCrits );
        lstHDCrits.setCellRenderer( Mechrender );
        jScrollPane10.setViewportView(lstHDCrits);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlHDCrits.add(jScrollPane10, gridBagConstraints);

        pnlCriticals.add(pnlHDCrits, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 117, 165));

        pnlCTCrits.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Center Torso", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlCTCrits.setMaximumSize(new java.awt.Dimension(114, 233));
        pnlCTCrits.setMinimumSize(new java.awt.Dimension(114, 233));
        pnlCTCrits.setLayout(new java.awt.GridBagLayout());

        jScrollPane11.setPreferredSize(new java.awt.Dimension(105, 170));

        lstCTCrits.setFont( PrintConsts.BaseCritFont );
        lstCTCrits.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Center Torso", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstCTCrits.setDragEnabled(true);
        lstCTCrits.setMaximumSize(new java.awt.Dimension(98, 50));
        lstCTCrits.setMinimumSize(new java.awt.Dimension(98, 50));
        lstCTCrits.setPreferredSize(new java.awt.Dimension(98, 50));
        lstCTCrits.setVisibleRowCount(12);
        lstCTCrits.setTransferHandler( new thCTTransferHandler( this, CurMech ) );
        lstCTCrits.setDropMode( DropMode.ON );
        MouseListener mlCTCrits = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    int index = lstCTCrits.locationToIndex( e.getPoint() );
                    abPlaceable[] a = CurMech.GetLoadout().GetCTCrits();
                    if( ! a[index].LocationLocked() ) {
                        if( a[index].CanSplit() && a[index].Contiguous() ) {
                            CurMech.GetLoadout().UnallocateAll( a[index], false );
                        } else {
                            CurMech.GetLoadout().UnallocateByIndex( index, a );
                        }
                    }
                    RefreshInfoPane();
                }
            }
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetCTCrits();
                    int index = lstCTCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_CT;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetCTCrits();
                    int index = lstCTCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_CT;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstCTCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetCTCrits()[index];
                    CurLocation = LocationIndex.MECH_LOC_CT;
                }
            }
        };
        lstCTCrits.addMouseListener( mlCTCrits );
        lstCTCrits.setCellRenderer( Mechrender );
        jScrollPane11.setViewportView(lstCTCrits);

        pnlCTCrits.add(jScrollPane11, new java.awt.GridBagConstraints());

        chkCTCASE.setText("C.A.S.E.");
        chkCTCASE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCTCASEActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlCTCrits.add(chkCTCASE, gridBagConstraints);

        chkCTCASE2.setText("C.A.S.E. II");
        chkCTCASE2.setEnabled(false);
        chkCTCASE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCTCASE2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlCTCrits.add(chkCTCASE2, gridBagConstraints);

        pnlCriticals.add(pnlCTCrits, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 180, 117, -1));

        pnlLTCrits.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Left Torso", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlLTCrits.setMaximumSize(new java.awt.Dimension(114, 235));
        pnlLTCrits.setMinimumSize(new java.awt.Dimension(114, 235));
        pnlLTCrits.setPreferredSize(new java.awt.Dimension(257, 232));
        pnlLTCrits.setLayout(new java.awt.GridBagLayout());

        chkLTCASE.setText("C.A.S.E.");
        chkLTCASE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLTCASEActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlLTCrits.add(chkLTCASE, gridBagConstraints);

        jScrollPane12.setMinimumSize(new java.awt.Dimension(105, 183));
        jScrollPane12.setPreferredSize(new java.awt.Dimension(105, 170));

        lstLTCrits.setFont( PrintConsts.BaseCritFont );
        lstLTCrits.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Left Torso", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstLTCrits.setDragEnabled(true);
        lstLTCrits.setMaximumSize(new java.awt.Dimension(98, 50));
        lstLTCrits.setMinimumSize(new java.awt.Dimension(98, 50));
        lstLTCrits.setPreferredSize(new java.awt.Dimension(98, 50));
        lstLTCrits.setVisibleRowCount(12);
        lstLTCrits.setTransferHandler( new thLTTransferHandler( this, CurMech ) );
        lstLTCrits.setDropMode( DropMode.ON );
        MouseListener mlLTCrits = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    int index = lstLTCrits.locationToIndex( e.getPoint() );
                    abPlaceable[] a = CurMech.GetLoadout().GetLTCrits();
                    if( ! a[index].LocationLocked() ) {
                        if( a[index].CanSplit() && a[index].Contiguous() ) {
                            CurMech.GetLoadout().UnallocateAll( a[index], false );
                        } else {
                            CurMech.GetLoadout().UnallocateByIndex( index, a );
                        }
                    }
                    RefreshInfoPane();
                }
            }
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetLTCrits();
                    int index = lstLTCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_LT;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetLTCrits();
                    int index = lstLTCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_LT;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstLTCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetLTCrits()[index];
                    CurLocation = LocationIndex.MECH_LOC_LT;
                }
            }
        };
        lstLTCrits.addMouseListener( mlLTCrits );
        lstLTCrits.setCellRenderer( Mechrender );
        jScrollPane12.setViewportView(lstLTCrits);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlLTCrits.add(jScrollPane12, gridBagConstraints);

        chkLTCASE2.setText("C.A.S.E. II");
        chkLTCASE2.setEnabled(false);
        chkLTCASE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLTCASE2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlLTCrits.add(chkLTCASE2, gridBagConstraints);

        chkLTTurret.setText("Turret");
        chkLTTurret.setEnabled(false);
        chkLTTurret.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLTTurretActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlLTCrits.add(chkLTTurret, gridBagConstraints);

        pnlCriticals.add(pnlLTCrits, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 40, 117, 270));

        pnlRTCrits.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Right Torso", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlRTCrits.setMaximumSize(new java.awt.Dimension(114, 233));
        pnlRTCrits.setMinimumSize(new java.awt.Dimension(114, 233));
        pnlRTCrits.setLayout(new java.awt.GridBagLayout());

        jScrollPane13.setMinimumSize(new java.awt.Dimension(105, 183));
        jScrollPane13.setPreferredSize(new java.awt.Dimension(105, 170));

        lstRTCrits.setFont( PrintConsts.BaseCritFont );
        lstRTCrits.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Right Torso", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstRTCrits.setDragEnabled(true);
        lstRTCrits.setMaximumSize(new java.awt.Dimension(98, 50));
        lstRTCrits.setMinimumSize(new java.awt.Dimension(98, 50));
        lstRTCrits.setPreferredSize(new java.awt.Dimension(98, 50));
        lstRTCrits.setVisibleRowCount(12);
        lstRTCrits.setTransferHandler( new thRTTransferHandler( this, CurMech ) );
        lstRTCrits.setDropMode( DropMode.ON );
        MouseListener mlRTCrits = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    int index = lstRTCrits.locationToIndex( e.getPoint() );
                    abPlaceable[] a = CurMech.GetLoadout().GetRTCrits();
                    if( ! a[index].LocationLocked() ) {
                        if( a[index].CanSplit() && a[index].Contiguous() ) {
                            CurMech.GetLoadout().UnallocateAll( a[index], false );
                        } else {
                            CurMech.GetLoadout().UnallocateByIndex( index, a );
                        }
                    }
                    RefreshInfoPane();
                }
            }
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetRTCrits();
                    int index = lstRTCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_RT;
                    ConfigureUtilsMenu( e.getComponent() );
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetRTCrits();
                    int index = lstRTCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_RT;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstRTCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetRTCrits()[index];
                    CurLocation = LocationIndex.MECH_LOC_RT;
                }
            }
        };
        lstRTCrits.addMouseListener( mlRTCrits );
        lstRTCrits.setCellRenderer( Mechrender );
        jScrollPane13.setViewportView(lstRTCrits);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlRTCrits.add(jScrollPane13, gridBagConstraints);

        chkRTCASE.setText("C.A.S.E.");
        chkRTCASE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRTCASEActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlRTCrits.add(chkRTCASE, gridBagConstraints);

        chkRTCASE2.setText("C.A.S.E. II");
        chkRTCASE2.setEnabled(false);
        chkRTCASE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRTCASE2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlRTCrits.add(chkRTCASE2, gridBagConstraints);

        chkRTTurret.setText("Turret");
        chkRTTurret.setEnabled(false);
        chkRTTurret.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRTTurretActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlRTCrits.add(chkRTTurret, gridBagConstraints);

        pnlCriticals.add(pnlRTCrits, new org.netbeans.lib.awtextra.AbsoluteConstraints(355, 40, 117, 270));

        pnlLACrits.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Left Arm", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlLACrits.setMaximumSize(new java.awt.Dimension(114, 256));
        pnlLACrits.setMinimumSize(new java.awt.Dimension(114, 256));
        pnlLACrits.setLayout(new java.awt.GridBagLayout());

        scrLACrits.setMinimumSize(new java.awt.Dimension(105, 87));
        scrLACrits.setPreferredSize(new java.awt.Dimension(105, 170));

        lstLACrits.setFont( PrintConsts.BaseCritFont );
        lstLACrits.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Left Arm", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstLACrits.setDragEnabled(true);
        lstLACrits.setMaximumSize(new java.awt.Dimension(98, 50));
        lstLACrits.setMinimumSize(new java.awt.Dimension(98, 50));
        lstLACrits.setPreferredSize(new java.awt.Dimension(98, 50));
        lstLACrits.setVisibleRowCount(12);
        lstLACrits.setTransferHandler( new thLATransferHandler( this, CurMech ) );
        lstLACrits.setDropMode( DropMode.ON );
        MouseListener mlLACrits = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    int index = lstLACrits.locationToIndex( e.getPoint() );
                    abPlaceable[] a = CurMech.GetLoadout().GetLACrits();
                    if( ! a[index].LocationLocked() ) {
                        if( a[index].CanSplit() && a[index].Contiguous() ) {
                            CurMech.GetLoadout().UnallocateAll( a[index], false );
                        } else {
                            CurMech.GetLoadout().UnallocateByIndex( index, a );
                        }
                    }
                    RefreshInfoPane();
                }
            }
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetLACrits();
                    int index = lstLACrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_LA;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetLACrits();
                    int index = lstLACrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_LA;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstLACrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetLACrits()[index];
                    CurLocation = LocationIndex.MECH_LOC_LA;
                }
            }
        };
        lstLACrits.addMouseListener( mlLACrits );
        lstLACrits.setCellRenderer( Mechrender );
        scrLACrits.setViewportView(lstLACrits);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlLACrits.add(scrLACrits, gridBagConstraints);

        chkLALowerArm.setSelected(true);
        chkLALowerArm.setText("Lower Arm");
        chkLALowerArm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLALowerArmActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlLACrits.add(chkLALowerArm, gridBagConstraints);

        chkLAHand.setSelected(true);
        chkLAHand.setText("Hand");
        chkLAHand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLAHandActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlLACrits.add(chkLAHand, gridBagConstraints);

        chkLACASE2.setText("C.A.S.E. II");
        chkLACASE2.setEnabled(false);
        chkLACASE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLACASE2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlLACrits.add(chkLACASE2, gridBagConstraints);

        chkLAAES.setText("A.E.S.");
        chkLAAES.setEnabled(false);
        chkLAAES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLAAESActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlLACrits.add(chkLAAES, gridBagConstraints);

        pnlCriticals.add(pnlLACrits, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 117, -1));

        pnlRACrits.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Right Arm", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlRACrits.setMaximumSize(new java.awt.Dimension(114, 256));
        pnlRACrits.setMinimumSize(new java.awt.Dimension(114, 256));
        pnlRACrits.setLayout(new java.awt.GridBagLayout());

        scrRACrits.setMinimumSize(new java.awt.Dimension(105, 87));
        scrRACrits.setPreferredSize(new java.awt.Dimension(105, 170));

        lstRACrits.setFont( PrintConsts.BaseCritFont );
        lstRACrits.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Right Arm", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstRACrits.setDragEnabled(true);
        lstRACrits.setMaximumSize(new java.awt.Dimension(98, 50));
        lstRACrits.setMinimumSize(new java.awt.Dimension(98, 50));
        lstRACrits.setPreferredSize(new java.awt.Dimension(98, 50));
        lstRACrits.setVisibleRowCount(12);
        lstRACrits.setTransferHandler( new thRATransferHandler( this, CurMech ) );
        lstRACrits.setDropMode( DropMode.ON );
        MouseListener mlRACrits = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    int index = lstRACrits.locationToIndex( e.getPoint() );
                    abPlaceable[] a = CurMech.GetLoadout().GetRACrits();
                    if( ! a[index].LocationLocked() ) {
                        if( a[index].CanSplit() && a[index].Contiguous() ) {
                            CurMech.GetLoadout().UnallocateAll( a[index], false );
                        } else {
                            CurMech.GetLoadout().UnallocateByIndex( index, a );
                        }
                    }
                    RefreshInfoPane();
                }
            }
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetRACrits();
                    int index = lstRACrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_RA;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetRACrits();
                    int index = lstRACrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_RA;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstRACrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetRACrits()[index];
                    CurLocation = LocationIndex.MECH_LOC_RA;
                }
            }
        };
        lstRACrits.addMouseListener( mlRACrits );
        lstRACrits.setCellRenderer( Mechrender );
        scrRACrits.setViewportView(lstRACrits);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlRACrits.add(scrRACrits, gridBagConstraints);

        chkRALowerArm.setSelected(true);
        chkRALowerArm.setText("Lower Arm");
        chkRALowerArm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRALowerArmActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlRACrits.add(chkRALowerArm, gridBagConstraints);

        chkRAHand.setSelected(true);
        chkRAHand.setText("Hand");
        chkRAHand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRAHandActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlRACrits.add(chkRAHand, gridBagConstraints);

        chkRACASE2.setText("C.A.S.E. II");
        chkRACASE2.setEnabled(false);
        chkRACASE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRACASE2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlRACrits.add(chkRACASE2, gridBagConstraints);

        chkRAAES.setText("A.E.S.");
        chkRAAES.setEnabled(false);
        chkRAAES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRAAESActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlRACrits.add(chkRAAES, gridBagConstraints);

        pnlCriticals.add(pnlRACrits, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 60, 117, -1));

        pnlLLCrits.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Left Leg", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlLLCrits.setMaximumSize(new java.awt.Dimension(116, 120));
        pnlLLCrits.setMinimumSize(new java.awt.Dimension(116, 120));
        pnlLLCrits.setLayout(new java.awt.GridBagLayout());

        jScrollPane16.setMinimumSize(new java.awt.Dimension(105, 87));
        jScrollPane16.setPreferredSize(new java.awt.Dimension(105, 87));

        lstLLCrits.setFont( PrintConsts.BaseCritFont );
        lstLLCrits.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Left Leg", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstLLCrits.setDragEnabled(true);
        lstLLCrits.setMaximumSize(new java.awt.Dimension(98, 50));
        lstLLCrits.setMinimumSize(new java.awt.Dimension(98, 50));
        lstLLCrits.setPreferredSize(new java.awt.Dimension(98, 50));
        lstLLCrits.setVisibleRowCount(6);
        lstLLCrits.setTransferHandler( new thLLTransferHandler( this, CurMech ) );
        lstLLCrits.setDropMode( DropMode.ON );
        MouseListener mlLLCrits = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    int index = lstLLCrits.locationToIndex( e.getPoint() );
                    abPlaceable[] a = CurMech.GetLoadout().GetLLCrits();
                    if( ! a[index].LocationLocked() ) {
                        if( a[index].CanSplit() && a[index].Contiguous() ) {
                            CurMech.GetLoadout().UnallocateAll( a[index], false );
                        } else {
                            CurMech.GetLoadout().UnallocateByIndex( index, a );
                        }
                    }
                    RefreshInfoPane();
                }
            }
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetLLCrits();
                    int index = lstLLCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_LL;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetLLCrits();
                    int index = lstLLCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_LL;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstLLCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetLLCrits()[index];
                    CurLocation = LocationIndex.MECH_LOC_LL;
                }
            }
        };
        lstLLCrits.addMouseListener( mlLLCrits );
        lstLLCrits.setCellRenderer( Mechrender );
        jScrollPane16.setViewportView(lstLLCrits);

        pnlLLCrits.add(jScrollPane16, new java.awt.GridBagConstraints());

        chkLLCASE2.setText("C.A.S.E. II");
        chkLLCASE2.setEnabled(false);
        chkLLCASE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLLCASE2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlLLCrits.add(chkLLCASE2, gridBagConstraints);

        pnlCriticals.add(pnlLLCrits, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 320, 117, -1));

        pnlRLCrits.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Right Leg", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlRLCrits.setMaximumSize(new java.awt.Dimension(116, 120));
        pnlRLCrits.setMinimumSize(new java.awt.Dimension(116, 120));
        pnlRLCrits.setLayout(new java.awt.GridBagLayout());

        jScrollPane17.setMinimumSize(new java.awt.Dimension(105, 87));
        jScrollPane17.setPreferredSize(new java.awt.Dimension(105, 87));

        lstRLCrits.setFont( PrintConsts.BaseCritFont );
        lstRLCrits.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Right Leg", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstRLCrits.setDragEnabled(true);
        lstRLCrits.setMaximumSize(new java.awt.Dimension(98, 50));
        lstRLCrits.setMinimumSize(new java.awt.Dimension(98, 50));
        lstRLCrits.setPreferredSize(new java.awt.Dimension(98, 50));
        lstRLCrits.setVisibleRowCount(6);
        lstRLCrits.setTransferHandler( new thRLTransferHandler( this, CurMech ) );
        lstRLCrits.setDropMode( DropMode.ON );
        MouseListener mlRLCrits = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    int index = lstRLCrits.locationToIndex( e.getPoint() );
                    abPlaceable[] a = CurMech.GetLoadout().GetRLCrits();
                    if( ! a[index].LocationLocked() ) {
                        if( a[index].CanSplit() && a[index].Contiguous() ) {
                            CurMech.GetLoadout().UnallocateAll( a[index], false );
                        } else {
                            CurMech.GetLoadout().UnallocateByIndex( index, a );
                        }
                    }
                    RefreshInfoPane();
                }
            }
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetRLCrits();
                    int index = lstRLCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_RL;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetRLCrits();
                    int index = lstRLCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    CurLocation = LocationIndex.MECH_LOC_RL;
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstRLCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetRLCrits()[index];
                    CurLocation = LocationIndex.MECH_LOC_RL;
                }
            }
        };
        lstRLCrits.addMouseListener( mlRLCrits );
        lstRLCrits.setCellRenderer( Mechrender );
        jScrollPane17.setViewportView(lstRLCrits);

        pnlRLCrits.add(jScrollPane17, new java.awt.GridBagConstraints());

        chkRLCASE2.setText("C.A.S.E. II");
        chkRLCASE2.setEnabled(false);
        chkRLCASE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRLCASE2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlRLCrits.add(chkRLCASE2, gridBagConstraints);

        pnlCriticals.add(pnlRLCrits, new org.netbeans.lib.awtextra.AbsoluteConstraints(355, 320, 117, -1));

        pnlEquipmentToPlace.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Equipment to Place", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        pnlEquipmentToPlace.setMaximumSize(new java.awt.Dimension(146, 330));
        pnlEquipmentToPlace.setMinimumSize(new java.awt.Dimension(146, 330));
        pnlEquipmentToPlace.setLayout(new javax.swing.BoxLayout(pnlEquipmentToPlace, javax.swing.BoxLayout.PAGE_AXIS));

        jScrollPane18.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        lstCritsToPlace.setFont( PrintConsts.BaseCritFont );
        lstCritsToPlace.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Selected", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstCritsToPlace.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstCritsToPlace.setDragEnabled(true);
        lstCritsToPlace.setMaximumSize(new java.awt.Dimension(150, 10000));
        lstCritsToPlace.setMinimumSize(new java.awt.Dimension(150, 80));
        lstCritsToPlace.setName("[150, 80]"); // NOI18N
        lstCritsToPlace.setVisibleRowCount(20);
        MouseListener mlCritsToPlace = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                int Index = lstCritsToPlace.locationToIndex( e.getPoint() );
                if( Index < 0 ) { return; }
                CurItem = CurMech.GetLoadout().GetFromQueueByIndex( Index );
                if( e.isPopupTrigger() ) {
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    if( CurItem.Contiguous() ) {
                        EquipmentCollection C = CurMech.GetLoadout().GetCollection( CurItem );
                        if( C == null ) {
                            btnAutoAllocate.setEnabled( false );
                            btnSelectiveAllocate.setEnabled( false );
                        } else {
                            btnAutoAllocate.setEnabled( true );
                            btnSelectiveAllocate.setEnabled( true );
                        }
                    } else {
                        btnAutoAllocate.setEnabled( true );
                        btnSelectiveAllocate.setEnabled( true );
                    }
                }
            }
            public void mousePressed( MouseEvent e ) {
                int Index = lstCritsToPlace.locationToIndex( e.getPoint() );
                if( Index < 0 ) { return; }
                CurItem = CurMech.GetLoadout().GetFromQueueByIndex( Index );
                if( e.isPopupTrigger() ) {
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    if( CurItem.Contiguous() ) {
                        EquipmentCollection C = CurMech.GetLoadout().GetCollection( CurItem );
                        if( C == null ) {
                            btnAutoAllocate.setEnabled( false );
                            btnSelectiveAllocate.setEnabled( false );
                        } else {
                            btnAutoAllocate.setEnabled( true );
                            btnSelectiveAllocate.setEnabled( true );
                        }
                    } else {
                        btnAutoAllocate.setEnabled( true );
                        btnSelectiveAllocate.setEnabled( true );
                    }
                }
            }
        };
        lstCritsToPlace.addMouseListener( mlCritsToPlace );
        lstCritsToPlace.setTransferHandler( new thQueueTransferHandler() );
        lstCritsToPlace.setCellRenderer( new ssw.gui.EquipmentListRenderer( this ) );
        lstCritsToPlace.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstCritsToPlaceValueChanged(evt);
            }
        });
        jScrollPane18.setViewportView(lstCritsToPlace);

        pnlEquipmentToPlace.add(jScrollPane18);

        btnRemoveItemCrits.setText("Remove Item");
        btnRemoveItemCrits.setEnabled(false);
        btnRemoveItemCrits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveItemCritsActionPerformed(evt);
            }
        });
        pnlEquipmentToPlace.add(btnRemoveItemCrits);

        pnlCriticals.add(pnlEquipmentToPlace, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, 150, 360));

        onlLoadoutControls.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Loadout Controls", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        onlLoadoutControls.setLayout(new java.awt.GridBagLayout());

        btnCompactCrits.setText("Compact");
        btnCompactCrits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompactCritsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 2);
        onlLoadoutControls.add(btnCompactCrits, gridBagConstraints);

        btnClearLoadout.setText("Clear");
        btnClearLoadout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearLoadoutActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 2);
        onlLoadoutControls.add(btnClearLoadout, gridBagConstraints);

        btnAutoAllocate.setText("Auto-Allocate");
        btnAutoAllocate.setEnabled(false);
        btnAutoAllocate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutoAllocateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 0);
        onlLoadoutControls.add(btnAutoAllocate, gridBagConstraints);

        btnSelectiveAllocate.setText("Selective-Allocate");
        btnSelectiveAllocate.setEnabled(false);
        btnSelectiveAllocate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectiveAllocateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        onlLoadoutControls.add(btnSelectiveAllocate, gridBagConstraints);

        pnlCriticals.add(onlLoadoutControls, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 370, 270, 90));

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel59.setText("<--");
        jPanel5.add(jLabel59, new java.awt.GridBagConstraints());

        chkLegAES.setText("A.E.S.");
        chkLegAES.setEnabled(false);
        chkLegAES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLegAESActionPerformed(evt);
            }
        });
        jPanel5.add(chkLegAES, new java.awt.GridBagConstraints());

        jLabel61.setText("-->");
        jPanel5.add(jLabel61, new java.awt.GridBagConstraints());

        pnlCriticals.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 430, 115, 30));

        tbpMainTabPane.addTab("Criticals", pnlCriticals);

        pnlFluff.setLayout(new java.awt.GridBagLayout());

        pnlImage.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Fluff Image", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        pnlImage.setLayout(new java.awt.GridBagLayout());

        lblFluffImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFluffImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblFluffImage.setMaximumSize(new java.awt.Dimension(290, 350));
        lblFluffImage.setMinimumSize(new java.awt.Dimension(290, 350));
        lblFluffImage.setPreferredSize(new java.awt.Dimension(290, 350));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlImage.add(lblFluffImage, gridBagConstraints);

        btnLoadImage.setText("Load Image");
        btnLoadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadImageActionPerformed(evt);
            }
        });
        jPanel1.add(btnLoadImage);

        btnClearImage.setText("Clear Image");
        btnClearImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearImageActionPerformed(evt);
            }
        });
        jPanel1.add(btnClearImage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 4);
        pnlImage.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlFluff.add(pnlImage, gridBagConstraints);

        pnlExport.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Export", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        pnlExport.setLayout(new java.awt.GridBagLayout());

        btnExportTXT.setText("to TXT");
        btnExportTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportTXTActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlExport.add(btnExportTXT, gridBagConstraints);

        btnExportHTML.setText("to HTML");
        btnExportHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportHTMLActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        pnlExport.add(btnExportHTML, gridBagConstraints);

        btnExportMTF.setText("to MegaMek");
        btnExportMTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportMTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlExport.add(btnExportMTF, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlFluff.add(pnlExport, gridBagConstraints);

        tbpFluffEditors.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tbpFluffEditors.setMaximumSize(new java.awt.Dimension(420, 455));
        tbpFluffEditors.setMinimumSize(new java.awt.Dimension(420, 455));
        tbpFluffEditors.setPreferredSize(new java.awt.Dimension(420, 455));

        pnlOverview.setMaximumSize(new java.awt.Dimension(427, 485));
        pnlOverview.setMinimumSize(new java.awt.Dimension(427, 485));
        pnlOverview.setLayout(new javax.swing.BoxLayout(pnlOverview, javax.swing.BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Overview", pnlOverview);

        pnlCapabilities.setMaximumSize(new java.awt.Dimension(427, 485));
        pnlCapabilities.setMinimumSize(new java.awt.Dimension(427, 485));
        pnlCapabilities.setLayout(new javax.swing.BoxLayout(pnlCapabilities, javax.swing.BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Capabilities", pnlCapabilities);

        pnlHistory.setLayout(new javax.swing.BoxLayout(pnlHistory, javax.swing.BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Battle History", pnlHistory);

        pnlDeployment.setLayout(new javax.swing.BoxLayout(pnlDeployment, javax.swing.BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Deployment", pnlDeployment);

        pnlVariants.setLayout(new javax.swing.BoxLayout(pnlVariants, javax.swing.BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Variants", pnlVariants);

        pnlNotables.setLayout(new javax.swing.BoxLayout(pnlNotables, javax.swing.BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Notables", pnlNotables);

        pnlAdditionalFluff.setLayout(new javax.swing.BoxLayout(pnlAdditionalFluff, javax.swing.BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Additional", pnlAdditionalFluff);

        pnlManufacturers.setLayout(new java.awt.GridBagLayout());

        jLabel8.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel8.setText("Manufacturer Information");
        jLabel8.setMaximumSize(new java.awt.Dimension(175, 15));
        jLabel8.setMinimumSize(new java.awt.Dimension(175, 15));
        jLabel8.setPreferredSize(new java.awt.Dimension(175, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        pnlManufacturers.add(jLabel8, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Manufacturing Company:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        pnlManufacturers.add(jLabel9, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Location:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 76;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel10, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Engine Manufacturer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel12, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Armor Model:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 56;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel11, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Chassis Model:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 47;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel13, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Communications System:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel14, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Targeting and Tracking:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel15, gridBagConstraints);

        txtManufacturer.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 0, 11);
        pnlManufacturers.add(txtManufacturer, gridBagConstraints);
        MouseListener mlManufacturer = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtManufacturer.addMouseListener( mlManufacturer );

        txtEngineManufacturer.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtEngineManufacturer, gridBagConstraints);
        MouseListener mlEngineManufacturer = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtEngineManufacturer.addMouseListener( mlEngineManufacturer );

        txtArmorModel.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtArmorModel, gridBagConstraints);
        MouseListener mlArmorModel = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtArmorModel.addMouseListener( mlArmorModel );

        txtChassisModel.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtChassisModel, gridBagConstraints);
        MouseListener mlChassisModel = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtChassisModel.addMouseListener( mlChassisModel );

        txtCommSystem.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtCommSystem, gridBagConstraints);
        MouseListener mlCommSystem = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtCommSystem.addMouseListener( mlCommSystem );

        txtTNTSystem.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtTNTSystem, gridBagConstraints);
        MouseListener mlTNTSystem = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtTNTSystem.addMouseListener( mlTNTSystem );

        pnlWeaponsManufacturers.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Weapons Manufacturers", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        pnlWeaponsManufacturers.setFont(new java.awt.Font("Arial", 0, 11));
        pnlWeaponsManufacturers.setMinimumSize(new java.awt.Dimension(315, 260));
        pnlWeaponsManufacturers.setLayout(new java.awt.GridBagLayout());

        chkIndividualWeapons.setText("Assign manufacturers individually");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 0);
        pnlWeaponsManufacturers.add(chkIndividualWeapons, gridBagConstraints);

        scpWeaponManufacturers.setPreferredSize(new java.awt.Dimension(452, 392));

        tblWeaponManufacturers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Weapon", "Manufacturer"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scpWeaponManufacturers.setViewportView(tblWeaponManufacturers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 280;
        gridBagConstraints.ipady = 180;
        pnlWeaponsManufacturers.add(scpWeaponManufacturers, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        pnlManufacturers.add(pnlWeaponsManufacturers, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtManufacturerLocation, gridBagConstraints);
        MouseListener mlManufacturerLocation = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtManufacturerLocation.addMouseListener( mlManufacturerLocation );

        jLabel35.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("Jump Jet Model:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 44;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel35, gridBagConstraints);

        txtJJModel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtJJModel, gridBagConstraints);
        MouseListener mlJJModel = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    mnuFluff.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        txtJJModel.addMouseListener( mlJJModel );

        tbpFluffEditors.addTab("Manufacturers", pnlManufacturers);

        lblBattleMechQuirks.setFont(new java.awt.Font("Arial", 1, 12));
        lblBattleMechQuirks.setText("BattleMech Quirks");
        lblBattleMechQuirks.setMaximumSize(new java.awt.Dimension(175, 15));
        lblBattleMechQuirks.setMinimumSize(new java.awt.Dimension(175, 15));
        lblBattleMechQuirks.setPreferredSize(new java.awt.Dimension(175, 15));

        tblQuirks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Quirk", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblQuirks.setColumnSelectionAllowed(true);
        tblQuirks.getTableHeader().setReorderingAllowed(false);
        scpQuirkTable.setViewportView(tblQuirks);
        tblQuirks.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblQuirks.getColumnModel().getColumn(0).setResizable(false);
        tblQuirks.getColumnModel().getColumn(1).setResizable(false);
        tblQuirks.getColumnModel().getColumn(1).setPreferredWidth(5);

        btnAddQuirk.setText("Add Quirk");
        btnAddQuirk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddQuirkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlQuirksLayout = new javax.swing.GroupLayout(pnlQuirks);
        pnlQuirks.setLayout(pnlQuirksLayout);
        pnlQuirksLayout.setHorizontalGroup(
            pnlQuirksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblBattleMechQuirks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQuirksLayout.createSequentialGroup()
                .addContainerGap(240, Short.MAX_VALUE)
                .addComponent(btnAddQuirk)
                .addContainerGap())
            .addGroup(pnlQuirksLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scpQuirkTable, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlQuirksLayout.setVerticalGroup(
            pnlQuirksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuirksLayout.createSequentialGroup()
                .addComponent(lblBattleMechQuirks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scpQuirkTable, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddQuirk)
                .addContainerGap())
        );

        tbpFluffEditors.addTab("Quirks", pnlQuirks);
        pnlQuirks.getAccessibleContext().setAccessibleName("Quirks");
        pnlQuirks.getAccessibleContext().setAccessibleParent(tbpFluffEditors);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 6);
        pnlFluff.add(tbpFluffEditors, gridBagConstraints);

        tbpMainTabPane.addTab("   Fluff   ", pnlFluff);

        pnlCharts.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Total Tonnage Percentages"));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel39.setText("Structural Components:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel39, gridBagConstraints);

        lblTonPercStructure.setText("000.00%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(lblTonPercStructure, gridBagConstraints);

        jLabel43.setText("Engine:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel43, gridBagConstraints);

        lblTonPercEngine.setText("000.00%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(lblTonPercEngine, gridBagConstraints);

        jLabel54.setText("Heat Sinks:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel54, gridBagConstraints);

        lblTonPercHeatSinks.setText("000.00%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(lblTonPercHeatSinks, gridBagConstraints);

        jLabel56.setText("Enhancements:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel56, gridBagConstraints);

        lblTonPercEnhance.setText("000.00%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(lblTonPercEnhance, gridBagConstraints);

        jLabel58.setText("Armor:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel58, gridBagConstraints);

        lblTonPercArmor.setText("000.00%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(lblTonPercArmor, gridBagConstraints);

        jLabel60.setText("Jump Jets:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel60, gridBagConstraints);

        lblTonPercJumpJets.setText("000.00%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(lblTonPercJumpJets, gridBagConstraints);

        jLabel62.setText("Weapons and Ammo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel62, gridBagConstraints);

        lblTonPercWeapons.setText("000.00%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(lblTonPercWeapons, gridBagConstraints);

        jLabel64.setText("Equipment/Pod Space:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel64, gridBagConstraints);

        lblTonPercEquips.setText("000.00%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(lblTonPercEquips, gridBagConstraints);

        pnlCharts.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 230, 150));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Other Statistics"));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel41.setText("Damage / 'Mech Tonnage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add(jLabel41, gridBagConstraints);

        lblDamagePerTon.setText("000.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel3.add(lblDamagePerTon, gridBagConstraints);

        pnlCharts.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 230, 50));

        pnlDamageChart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlDamageChart.setLayout(null);
        pnlCharts.add(pnlDamageChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 720, 280));

        lblLegendTitle.setText("Chart Options:");
        pnlCharts.add(lblLegendTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 10, 140, -1));

        chkChartFront.setBackground(java.awt.Color.red);
        chkChartFront.setSelected(true);
        chkChartFront.setText("Show Front Arc Weapons");
        chkChartFront.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkChartFrontActionPerformed(evt);
            }
        });
        pnlCharts.add(chkChartFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 210, -1));

        chkChartRear.setBackground(java.awt.Color.pink);
        chkChartRear.setSelected(true);
        chkChartRear.setText("Show Rear Arc Weapons");
        chkChartRear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkChartRearActionPerformed(evt);
            }
        });
        pnlCharts.add(chkChartRear, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 55, 210, -1));

        chkChartRight.setBackground(java.awt.Color.green);
        chkChartRight.setSelected(true);
        chkChartRight.setText("Show Right Arm Arc Weapons");
        chkChartRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkChartRightActionPerformed(evt);
            }
        });
        pnlCharts.add(chkChartRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 80, 210, -1));

        chkChartLeft.setBackground(java.awt.Color.orange);
        chkChartLeft.setSelected(true);
        chkChartLeft.setText("Show Left Arm Arc Weapons");
        chkChartLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkChartLeftActionPerformed(evt);
            }
        });
        pnlCharts.add(chkChartLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 105, 210, -1));

        btnBracketChart.setText("Show Weapon Bracket Chart");
        btnBracketChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBracketChartActionPerformed(evt);
            }
        });
        pnlCharts.add(btnBracketChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 70, 210, -1));

        chkAverageDamage.setText("Show Average Damage");
        chkAverageDamage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAverageDamageActionPerformed(evt);
            }
        });
        pnlCharts.add(chkAverageDamage, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 110, -1, -1));

        chkShowTextNotGraph.setText("Show Text Instead of Graph");
        chkShowTextNotGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowTextNotGraphActionPerformed(evt);
            }
        });
        pnlCharts.add(chkShowTextNotGraph, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 130, -1, -1));

        tbpMainTabPane.addTab("Charts", pnlCharts);

        pnlBattleforce.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlBFStats.setBorder(javax.swing.BorderFactory.createTitledBorder("BattleForce Stats"));
        pnlBFStats.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel66.setText("MV");
        pnlBFStats.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        jLabel67.setText("S (+0)");
        pnlBFStats.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, -1, -1));

        jLabel68.setText("M (+2)");
        pnlBFStats.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));

        jLabel69.setText("L (+4)");
        pnlBFStats.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, -1, -1));

        jLabel70.setText("E (+6)");
        pnlBFStats.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        jLabel71.setText("Wt.");
        pnlBFStats.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 30, -1, -1));

        jLabel72.setText("OV");
        pnlBFStats.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 30, -1, -1));

        jLabel73.setText("Armor:");
        pnlBFStats.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 30, -1, -1));

        jLabel74.setText("Structure:");
        pnlBFStats.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, -1, -1));

        jLabel75.setText("Special Abilities:");
        pnlBFStats.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, -1, -1));

        lblBFMV.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFMV.setText("0");
        pnlBFStats.add(lblBFMV, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 30, -1));

        lblBFWt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFWt.setText("1");
        pnlBFStats.add(lblBFWt, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, 30, -1));

        lblBFOV.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFOV.setText("0");
        pnlBFStats.add(lblBFOV, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 50, 30, -1));

        lblBFExtreme.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFExtreme.setText("0");
        pnlBFStats.add(lblBFExtreme, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, 30, -1));

        lblBFShort.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFShort.setText("0");
        pnlBFStats.add(lblBFShort, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 30, -1));

        lblBFMedium.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFMedium.setText("0");
        pnlBFStats.add(lblBFMedium, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 30, -1));

        lblBFLong.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFLong.setText("0");
        pnlBFStats.add(lblBFLong, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 30, -1));

        lblBFArmor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFArmor.setText("0");
        pnlBFStats.add(lblBFArmor, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, 30, -1));

        lblBFStructure.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBFStructure.setText("0");
        pnlBFStats.add(lblBFStructure, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 60, 30, -1));

        lblBFSA.setText("Placeholder");
        pnlBFStats.add(lblBFSA, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 430, 20));

        jLabel37.setText("Points:");
        pnlBFStats.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 30, -1, -1));

        lblBFPoints.setText("0");
        pnlBFStats.add(lblBFPoints, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 30, -1, -1));

        pnlBattleforce.add(pnlBFStats, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 690, 200));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Conversion Steps"));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextAreaBFConversion.setColumns(20);
        jTextAreaBFConversion.setEditable(false);
        jTextAreaBFConversion.setRows(5);
        jScrollPane14.setViewportView(jTextAreaBFConversion);

        jPanel7.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 660, 190));

        pnlBattleforce.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 690, 230));

        tbpMainTabPane.addTab("BattleForce", pnlBattleforce);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(tbpMainTabPane, gridBagConstraints);

        pnlInfoPanel.setMaximumSize(new java.awt.Dimension(32767, 26));
        pnlInfoPanel.setMinimumSize(new java.awt.Dimension(730, 26));
        pnlInfoPanel.setLayout(new javax.swing.BoxLayout(pnlInfoPanel, javax.swing.BoxLayout.LINE_AXIS));

        txtInfoTonnage.setEditable(false);
        txtInfoTonnage.setFont(new java.awt.Font("Arial", 0, 11));
        txtInfoTonnage.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoTonnage.setText("Tons: 000.00");
        txtInfoTonnage.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtInfoTonnage.setMaximumSize(new java.awt.Dimension(72, 20));
        txtInfoTonnage.setMinimumSize(new java.awt.Dimension(72, 20));
        txtInfoTonnage.setPreferredSize(new java.awt.Dimension(72, 20));
        pnlInfoPanel.add(txtInfoTonnage);

        txtInfoFreeTons.setEditable(false);
        txtInfoFreeTons.setFont(new java.awt.Font("Arial", 0, 11));
        txtInfoFreeTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoFreeTons.setText("Free Tons: 000.00");
        txtInfoFreeTons.setMaximumSize(new java.awt.Dimension(96, 20));
        txtInfoFreeTons.setMinimumSize(new java.awt.Dimension(96, 20));
        txtInfoFreeTons.setPreferredSize(new java.awt.Dimension(96, 20));
        pnlInfoPanel.add(txtInfoFreeTons);

        txtInfoMaxHeat.setEditable(false);
        txtInfoMaxHeat.setFont(new java.awt.Font("Arial", 0, 11));
        txtInfoMaxHeat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoMaxHeat.setText("Max Heat: 000");
        txtInfoMaxHeat.setMaximumSize(new java.awt.Dimension(77, 20));
        txtInfoMaxHeat.setMinimumSize(new java.awt.Dimension(77, 20));
        txtInfoMaxHeat.setPreferredSize(new java.awt.Dimension(77, 20));
        pnlInfoPanel.add(txtInfoMaxHeat);

        txtInfoHeatDiss.setEditable(false);
        txtInfoHeatDiss.setFont(new java.awt.Font("Arial", 0, 11));
        txtInfoHeatDiss.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoHeatDiss.setText("Heat Dissipation: 000");
        txtInfoHeatDiss.setMaximumSize(new java.awt.Dimension(118, 20));
        txtInfoHeatDiss.setMinimumSize(new java.awt.Dimension(118, 20));
        txtInfoHeatDiss.setPreferredSize(new java.awt.Dimension(118, 20));
        pnlInfoPanel.add(txtInfoHeatDiss);

        txtInfoFreeCrits.setEditable(false);
        txtInfoFreeCrits.setFont(new java.awt.Font("Arial", 0, 11));
        txtInfoFreeCrits.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoFreeCrits.setText("Free Crits: -00");
        txtInfoFreeCrits.setMaximumSize(new java.awt.Dimension(77, 20));
        txtInfoFreeCrits.setMinimumSize(new java.awt.Dimension(77, 20));
        txtInfoFreeCrits.setPreferredSize(new java.awt.Dimension(77, 20));
        pnlInfoPanel.add(txtInfoFreeCrits);

        txtInfoUnplaced.setEditable(false);
        txtInfoUnplaced.setFont(new java.awt.Font("Arial", 0, 11));
        txtInfoUnplaced.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoUnplaced.setText("Unplaced Crits: 00");
        txtInfoUnplaced.setMaximumSize(new java.awt.Dimension(110, 20));
        txtInfoUnplaced.setMinimumSize(new java.awt.Dimension(110, 20));
        txtInfoUnplaced.setPreferredSize(new java.awt.Dimension(110, 20));
        pnlInfoPanel.add(txtInfoUnplaced);

        txtInfoBattleValue.setEditable(false);
        txtInfoBattleValue.setFont(new java.awt.Font("Arial", 0, 11));
        txtInfoBattleValue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoBattleValue.setText("BV: 00,000");
        txtInfoBattleValue.setMaximumSize(new java.awt.Dimension(62, 20));
        txtInfoBattleValue.setMinimumSize(new java.awt.Dimension(62, 20));
        txtInfoBattleValue.setPreferredSize(new java.awt.Dimension(62, 20));
        pnlInfoPanel.add(txtInfoBattleValue);

        txtInfoCost.setEditable(false);
        txtInfoCost.setFont(new java.awt.Font("Arial", 0, 11));
        txtInfoCost.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoCost.setText("Cost: 000,000,000");
        txtInfoCost.setMaximumSize(new java.awt.Dimension(125, 20));
        txtInfoCost.setMinimumSize(new java.awt.Dimension(125, 20));
        txtInfoCost.setPreferredSize(new java.awt.Dimension(125, 20));
        pnlInfoPanel.add(txtInfoCost);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(pnlInfoPanel, gridBagConstraints);

        mnuFile.setText("File");
        mnuFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileActionPerformed(evt);
            }
        });

        mnuNewMech.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK));
        mnuNewMech.setText("New Mech");
        mnuNewMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewMechActionPerformed(evt);
            }
        });
        mnuFile.add(mnuNewMech);

        mnuLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        mnuLoad.setText("Load Mech");
        mnuLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLoadActionPerformed(evt);
            }
        });
        mnuFile.add(mnuLoad);

        mnuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK));
        mnuOpen.setText("Open");
        mnuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenActionPerformed(evt);
            }
        });
        mnuFile.add(mnuOpen);

        mnuImport.setText("Import Mech...");

        mnuImportHMP.setText("from Heavy Metal Pro (HMP)");
        mnuImportHMP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportHMPActionPerformed(evt);
            }
        });
        mnuImport.add(mnuImportHMP);

        mnuBatchHMP.setText("Batch Import HMP Files");
        mnuBatchHMP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBatchHMPActionPerformed(evt);
            }
        });
        mnuImport.add(mnuBatchHMP);

        mnuFile.add(mnuImport);
        mnuFile.add(jSeparator16);

        mnuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        mnuSave.setText("Save Mech");
        mnuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveActionPerformed(evt);
            }
        });
        mnuFile.add(mnuSave);

        mnuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuSaveAs.setText("Save Mech As...");
        mnuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveAsActionPerformed(evt);
            }
        });
        mnuFile.add(mnuSaveAs);

        mnuExport.setText("Export Mech...");

        mnuExportHTML.setText("to HTML (Web)");
        mnuExportHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportHTMLActionPerformed(evt);
            }
        });
        mnuExport.add(mnuExportHTML);

        mnuExportMTF.setText("to MTF (MegaMek)");
        mnuExportMTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportMTFActionPerformed(evt);
            }
        });
        mnuExport.add(mnuExportMTF);

        mnuExportTXT.setText("to TXT (Text)");
        mnuExportTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportTXTActionPerformed(evt);
            }
        });
        mnuExport.add(mnuExportTXT);

        mnuExportClipboard.setText("to Clipboard (Text)");
        mnuExportClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportClipboardActionPerformed(evt);
            }
        });
        mnuExport.add(mnuExportClipboard);

        mnuCreateTCGMech.setText("to TCG Format (Card)");
        mnuCreateTCGMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreateTCGMechActionPerformed(evt);
            }
        });
        mnuExport.add(mnuCreateTCGMech);

        mnuFile.add(mnuExport);
        mnuFile.add(jSeparator20);

        mnuPrint.setText("Print");

        mnuPrintCurrentMech.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintCurrentMech.setText("Current Mech");
        mnuPrintCurrentMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintCurrentMechActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintCurrentMech);

        mnuPrintSavedMech.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintSavedMech.setText("Saved Mech");
        mnuPrintSavedMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintSavedMechActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintSavedMech);

        mnuPrintBatch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintBatch.setText("Batch Print Mechs");
        mnuPrintBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintBatchActionPerformed(evt);
            }
        });
        mnuPrint.add(mnuPrintBatch);

        mnuFile.add(mnuPrint);

        mnuPrintPreview.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintPreview.setText("Print Preview");
        mnuPrintPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintPreviewActionPerformed(evt);
            }
        });
        mnuFile.add(mnuPrintPreview);

        mnuPostS7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        mnuPostS7.setText("Post Mech to Solaris7.com");
        mnuPostS7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPostS7ActionPerformed(evt);
            }
        });
        mnuFile.add(mnuPostS7);
        mnuFile.add(jSeparator17);

        mnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuExit);

        mnuMainMenu.add(mnuFile);

        mnuClearFluff.setText("Tools");

        mnuSummary.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_MASK));
        mnuSummary.setText("Show Summary");
        mnuSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSummaryActionPerformed(evt);
            }
        });
        mnuClearFluff.add(mnuSummary);

        mnuCostBVBreakdown.setText("Cost/BV Breakdown");
        mnuCostBVBreakdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCostBVBreakdownActionPerformed(evt);
            }
        });
        mnuClearFluff.add(mnuCostBVBreakdown);

        mnuTextTRO.setText("Show Text TRO Format");
        mnuTextTRO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTextTROActionPerformed(evt);
            }
        });
        mnuClearFluff.add(mnuTextTRO);
        mnuClearFluff.add(jSeparator15);

        mnuBFB.setText("Load Force Balancer");
        mnuBFB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBFBActionPerformed(evt);
            }
        });
        mnuClearFluff.add(mnuBFB);
        mnuClearFluff.add(jSeparator27);

        mnuOptions.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK));
        mnuOptions.setText("Preferences");
        mnuOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOptionsActionPerformed(evt);
            }
        });
        mnuClearFluff.add(mnuOptions);

        mnuViewToolbar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        mnuViewToolbar.setSelected(true);
        mnuViewToolbar.setText("View Toolbar");
        mnuViewToolbar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuViewToolbarActionPerformed(evt);
            }
        });
        mnuClearFluff.add(mnuViewToolbar);

        mnuClearUserData.setText("Clear User Data");
        mnuClearUserData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuClearUserDataActionPerformed(evt);
            }
        });
        mnuClearFluff.add(mnuClearUserData);
        mnuClearFluff.add(jSeparator30);

        mnuUnlock.setText("Unlock Chassis");
        mnuUnlock.setEnabled(false);
        mnuUnlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUnlockActionPerformed(evt);
            }
        });
        mnuClearFluff.add(mnuUnlock);

        jMenuItem1.setText("Clear All Fluff");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        mnuClearFluff.add(jMenuItem1);

        mnuMainMenu.add(mnuClearFluff);

        mnuHelp.setText("Help");

        mnuCredits.setText("Credits");
        mnuCredits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreditsActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuCredits);

        mnuAboutSSW.setText("About SSW");
        mnuAboutSSW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutSSWActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuAboutSSW);

        mnuMainMenu.add(mnuHelp);

        setJMenuBar(mnuMainMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        if( CurMech.HasChanged() ) {
            int choice = javax.swing.JOptionPane.showConfirmDialog( this,
                "The current 'Mech has changed.\nDo you want to discard those changes?", "Discard Changes?", javax.swing.JOptionPane.YES_NO_OPTION );
            if( choice == 1 ) { return; }
        }
        CloseProgram();
    }//GEN-LAST:event_mnuExitActionPerformed

    private void CloseProgram() {
        try {
            if (BatchWindow != null) BatchWindow.dispose();
            if (dOpen != null) dOpen.dispose();
            if (dForce != null) dForce.dispose();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.flush();
        
        System.exit(0);
    }

    private void btnLoadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadImageActionPerformed
        // Opens a file chooser for the user, then resizes the chosen image to
        // fit in the fluff label and adds it
        JFileChooser fc = new JFileChooser();

        // get the current image in case we cancel
        ImageIcon newFluffImage = (ImageIcon) lblFluffImage.getIcon();

        //Add a custom file filter and disable the default
        //(Accept All) file filter.
        fc.addChoosableFileFilter(new ImageFilter());
        fc.setAcceptAllFileFilterUsed(false);
        if (! Prefs.get("LastImagePath", "").isEmpty() ) {
            fc.setCurrentDirectory(new File(Prefs.get("LastImagePath", "")));
        }

        //Add custom icons for file types.
        //ImageFileView IFV = new ImageFileView();
        //fc.setFileView( IFV );

	//Add the preview pane.
        fc.setAccessory(new ImagePreview(fc));

        //Show it.
        int returnVal = fc.showDialog( this, "Attach");

        //Process the results.  If no file is chosen, the default is used.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try
            {
                Prefs.put("LastImagePath", fc.getSelectedFile().getCanonicalPath().replace(fc.getSelectedFile().getName(), ""));
                Prefs.put("LastImageFile", fc.getSelectedFile().getName());

                newFluffImage = new ImageIcon(fc.getSelectedFile().getPath());

                if( newFluffImage == null ) { return; }
                // See if we need to scale
                int h = newFluffImage.getIconHeight();
                int w = newFluffImage.getIconWidth();
                if ( w > 290 || h > 350 ) {
                    if ( w > h ) { // resize based on width
                        newFluffImage = new ImageIcon(newFluffImage.getImage().
                            getScaledInstance(290, -1, Image.SCALE_DEFAULT));
                    } else { // resize based on height
                        newFluffImage = new ImageIcon(newFluffImage.getImage().
                            getScaledInstance(-1, 350, Image.SCALE_DEFAULT));
                    }
                }
            } catch (Exception e) {
                //break;
            }
        } else {
            //
        }

        // add the image to the fluff image label
        lblFluffImage.setIcon( newFluffImage );
        CurMech.SetSSWImage( fc.getSelectedFile().getPath() );
    }//GEN-LAST:event_btnLoadImageActionPerformed

    private void btnClearImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearImageActionPerformed
        // Set the fluff image to default
        lblFluffImage.setIcon( null );
        CurMech.SetSSWImage("");
    }//GEN-LAST:event_btnClearImageActionPerformed

    private void cmbHeatSinkTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbHeatSinkTypeActionPerformed
        if( BuildLookupName( CurMech.GetHeatSinks().GetCurrentState() ).equals( (String) cmbHeatSinkType.getSelectedItem() ) ) {
            return;
        }
        RecalcHeatSinks();
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbHeatSinkTypeActionPerformed

    private void spnNumberOfHSStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnNumberOfHSStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnNumberOfHS.getModel();
        int NumHS = CurMech.GetHeatSinks().GetNumHS();
        javax.swing.JComponent editor = spnNumberOfHS.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnNumberOfHS.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnNumberOfHS.getValue());
            }
            return;
        }

        if( n.getNumber().intValue() > NumHS ) {
            // The number of sinks went up
            for( int i = NumHS; i < n.getNumber().intValue(); i++ ) {
                CurMech.GetHeatSinks().IncrementNumHS();
            }
        } else {
            // the number went down
            for( int i = NumHS; i > n.getNumber().intValue(); i-- ) {
                CurMech.GetHeatSinks().DecrementNumHS();
            }
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnNumberOfHSStateChanged

    private void cmbMechEraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMechEraActionPerformed
        if( Load ) { return; }
        // whenever the era is changed we basically need to reset the GUI and
        // most of the mech.  Certain things we will transfer.
        if( CurMech.GetEra() == cmbMechEra.getSelectedIndex() ) {
            return;
        }
        if( CurMech.IsOmnimech() ) {
            if( cmbMechEra.getSelectedIndex() < CurMech.GetBaseEra() ) {
                Media.Messager( this, "An OmniMech loadout cannot have an era lower than the main loadout." );
                cmbMechEra.setSelectedIndex( CurMech.GetBaseEra() );
            }
        }

        // first, let's save the tech base selection in case we can still use it
        // prevents Clan mechs reverting to Inner Sphere on era change.
        int tbsave = cmbTechBase.getSelectedIndex();

        // change the year range and tech base options
        switch( cmbMechEra.getSelectedIndex() ) {
            case AvailableCode.ERA_STAR_LEAGUE:
                lblEraYears.setText( "2443 ~ 2800" );
                txtProdYear.setText( "" );
                CurMech.SetEra( AvailableCode.ERA_STAR_LEAGUE );
                CurMech.SetYear( 2750, false );
                if( ! CurMech.IsOmnimech() ) { chkYearRestrict.setEnabled( true ); }
                break;
            case AvailableCode.ERA_SUCCESSION:
                lblEraYears.setText( "2801 ~ 3050" );
                txtProdYear.setText( "" );
                CurMech.SetEra( AvailableCode.ERA_SUCCESSION );
                CurMech.SetYear( 3025, false );
                if( ! CurMech.IsOmnimech() ) { chkYearRestrict.setEnabled( true ); }
                break;
            case AvailableCode.ERA_CLAN_INVASION:
                lblEraYears.setText( "3051 ~ 3131" );
                txtProdYear.setText( "" );
                CurMech.SetEra( AvailableCode.ERA_CLAN_INVASION );
                CurMech.SetYear( 3075, false );
                if( ! CurMech.IsOmnimech() ) { chkYearRestrict.setEnabled( true ); }
                break;
            case AvailableCode.ERA_DARK_AGES:
                lblEraYears.setText( "3132 on" );
                txtProdYear.setText( "" );
                CurMech.SetEra( AvailableCode.ERA_DARK_AGES );
                CurMech.SetYear( 3132, false );
                if( ! CurMech.IsOmnimech() ) { chkYearRestrict.setEnabled( true ); }
                break;
            case AvailableCode.ERA_ALL:
                lblEraYears.setText( "Any" );
                txtProdYear.setText( "" );
                CurMech.SetEra( AvailableCode.ERA_ALL );
                CurMech.SetYear( 0, false );
                chkYearRestrict.setEnabled( false );
                break;
        }

        if( CurMech.IsOmnimech() ) {
            BuildJumpJetSelector();
            RefreshEquipment();
            RefreshSummary();
            RefreshInfoPane();
            SetWeaponChoosers();
            ResetAmmo();
            return;
        }

        BuildTechBaseSelector();
        BuildMechTypeSelector();

        // reset the tech base if it's still allowed
        if( tbsave < cmbTechBase.getItemCount() ) {
            // still valid, use it.  No reconfigure needed
            cmbTechBase.setSelectedIndex( tbsave );
        } else {
            // nope, set it to Inner Sphere.  This means it was Clan and we
            // should reconfigure the mech
            cmbTechBase.setSelectedIndex( 0 );
            CurMech.SetInnerSphere();
        }

        // get the currently chosen selections
        SaveSelections();

        // refresh all the combo boxes.
        BuildChassisSelector();
        BuildEngineSelector();
        BuildGyroSelector();
        BuildCockpitSelector();
        BuildEnhancementSelector();
        BuildHeatsinkSelector();
        BuildJumpJetSelector();
        BuildArmorSelector();
        FixWalkMPSpinner();
        FixJJSpinnerModel();
        RefreshEquipment();
        CheckOmnimech();

        // now reset the combo boxes to the closest choices we previously selected
        LoadSelections();

        // when a new era is selected, we have to recalculate the mech
        RecalcEngine();
        RecalcGyro();
        RecalcIntStruc();
        RecalcCockpit();
        CurMech.GetActuators().PlaceActuators();
        RecalcHeatSinks();
        RecalcJumpJets();
        RecalcEnhancements();
        RecalcArmor();
        RecalcEquipment();

        // since you can only ever change the era when not restricted, we're not
        // doing it here.  Pass in default values.
        CurMech.GetLoadout().FlushIllegal();
        //CurMech.GetLoadout().FlushIllegal( cmbMechEra.getSelectedIndex(), 0, false );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();
    }//GEN-LAST:event_cmbMechEraActionPerformed

    private void spnWalkMPStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnWalkMPStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnWalkMP.getModel();
        javax.swing.JComponent editor = spnWalkMP.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnWalkMP.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnWalkMP.getValue());
            }
            return;
        }
        try {
            // the commitedit worked, so set the engine rating and report the running mp
            CurMech.SetWalkMP( n.getNumber().intValue() );
        } catch( Exception e ) {
            Media.Messager( e.getMessage() );
            spnWalkMP.setValue( spnWalkMP.getPreviousValue() );
        }
        lblRunMP.setText( "" + CurMech.GetRunningMP() );

        // when the walking mp changes, we also have to change the jump mp
        // spinner model and recalculate the heat sinks
        FixJJSpinnerModel();
        CurMech.GetHeatSinks().ReCalculate();
        CurMech.GetLoadout().UnallocateFuelTanks();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnWalkMPStateChanged

    private void spnJumpMPStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnJumpMPStateChanged
        // just change the number of jump jets.
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnJumpMP.getModel();
        javax.swing.JComponent editor = spnJumpMP.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();
        int NumJJ = CurMech.GetJumpJets().GetNumJJ();

        // get the value from the text box, if it's valid.
        try {
            spnWalkMP.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnWalkMP.getValue());
            }
            return;
        }

        if( n.getNumber().intValue() > NumJJ ) {
            // The number of sinks went up
            for( int i = NumJJ; i < n.getNumber().intValue(); i++ ) {
                CurMech.GetJumpJets().IncrementNumJJ();
            }
        } else {
            // the number went down
            for( int i = NumJJ; i > n.getNumber().intValue(); i-- ) {
                CurMech.GetJumpJets().DecrementNumJJ();
            }
        }

        // see if we need to enable the jump jet manufacturer field
        if( n.getNumber().intValue() > 0 ) {
            // enable the field
            txtJJModel.setEnabled( true );
        } else {
            // disable it, but don't clear it
            txtJJModel.setEnabled( false );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnJumpMPStateChanged

    private void cmbTonnageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTonnageActionPerformed
        // We have to decode the selected index to set values.  A bit safer, I
        // think, because we can directly set the values ourselves.
        int Tons = 0;
        switch ( cmbTonnage.getSelectedIndex() ) {
            case 0:
                // 10 ton 'Mech.  Need to check the settings first
                lblMechType.setText( "Ultralight Mech");
                Tons = 10;
                break;
            case 1:
                // 15 ton 'Mech
                lblMechType.setText( "Ultralight Mech");
                Tons = 15;
                break;
            case 2:
                // 20 ton mech
                lblMechType.setText( "Light Mech");
                Tons = 20;
                break;
            case 3:
                // 25 ton mech
                lblMechType.setText( "Light Mech");
                Tons = 25;
                break;
            case 4:
                // 30 ton mech
                lblMechType.setText( "Light Mech");
                Tons = 30;
                break;
            case 5:
                // 35 ton mech
                lblMechType.setText( "Light Mech");
                Tons = 35;
                break;
            case 6:
                // 40 ton mech
                lblMechType.setText( "Medium Mech");
                Tons = 40;
                break;
            case 7:
                // 45 ton mech
                lblMechType.setText( "Medium Mech");
                Tons = 45;
                break;
            case 8:
                // 50 ton mech
                lblMechType.setText( "Medium Mech");
                Tons = 50;
                break;
            case 9:
                // 55 ton mech
                lblMechType.setText( "Medium Mech");
                Tons = 55;
                break;
            case 10:
                // 60 ton mech
                lblMechType.setText( "Heavy Mech");
                Tons = 60;
                break;
            case 11:
                // 65 ton mech
                lblMechType.setText( "Heavy Mech");
                Tons = 65;
                break;
            case 12:
                // 70 ton mech
                lblMechType.setText( "Heavy Mech");
                Tons = 70;
                break;
            case 13:
                // 75 ton mech
                lblMechType.setText( "Heavy Mech");
                Tons = 75;
                break;
            case 14:
                // 80 ton mech
                lblMechType.setText( "Assault Mech");
                Tons = 80;
                break;
            case 15:
                // 85 ton mech
                lblMechType.setText( "Assault Mech");
                Tons = 85;
                break;
            case 16:
                // 90 ton mech
                lblMechType.setText( "Assault Mech");
                Tons = 90;
                break;
            case 17:
                // 95 ton mech
                lblMechType.setText( "Assault Mech");
                Tons = 95;
                break;
            case 18:
                // 100 ton mech
                lblMechType.setText( "Assault Mech");
                Tons = 100;
                break;
        }

        if( CurMech.GetTonnage() == Tons ) {
            return;
        } else {
            CurMech.SetTonnage( Tons );
        }

        // check the tonnage
        CheckTonnage( false );

        // fix the walking and jumping MP spinners
        FixWalkMPSpinner();
        FixJJSpinnerModel();

        // recalculate the heat sinks and armor
        CurMech.GetHeatSinks().ReCalculate();
        CurMech.GetArmor().Recalculate();

        // fix the armor spinners
        FixArmorSpinners();

        // unallocate physical weapons since their size depends on tonnage
        CurMech.CheckPhysicals();

        // Check any AES systems that may have been installed
        CheckAES();

        // now refresh the information panes
        RefreshInternalPoints();
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbTonnageActionPerformed

    private void cmbTechBaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTechBaseActionPerformed
        if( Load ) { return; }
        // do we really need to do this?
        if( CurMech.IsOmnimech() ) {
            if( CurMech.GetLoadout().GetTechBase() == cmbTechBase.getSelectedIndex() ) { return; }
        } else {
            if( CurMech.GetTechbase() == cmbTechBase.getSelectedIndex() ) { return; }
        }

        if( CurMech.IsOmnimech() ) {
            boolean check = CurMech.SetTechBase( cmbTechBase.getSelectedIndex() );
            if( ! check ) {
                Media.Messager( this, "An OmniMech can only use the base chassis' Tech Base\nor Mixed Tech.  Resetting." );
                cmbTechBase.setSelectedIndex( CurMech.GetLoadout().GetTechBase() );
                return;
            }
            RefreshEquipment();
        } else {
            // now change the mech over to the new techbase
            switch( cmbTechBase.getSelectedIndex() ) {
                case AvailableCode.TECH_INNER_SPHERE:
                    CurMech.SetInnerSphere();
                    break;
                case AvailableCode.TECH_CLAN:
                    CurMech.SetClan();
                    break;
                case AvailableCode.TECH_BOTH:
                    CurMech.SetMixed();
                    break;
            }

            // save the current selections.  The 'Mech should have already
            // flushed any illegal equipment in the changeover
            SaveSelections();

            data.Rebuild( CurMech );

            // refresh all the combo boxes.
            BuildChassisSelector();
            BuildEngineSelector();
            BuildGyroSelector();
            BuildCockpitSelector();
            BuildEnhancementSelector();
            BuildHeatsinkSelector();
            BuildJumpJetSelector();
            BuildArmorSelector();
            RefreshEquipment();
            FixWalkMPSpinner();
            FixJJSpinnerModel();
            CheckOmnimech();

            // for Clan machines (only) ensure that Clan CASE is selected by default
            if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                CurMech.GetLoadout().SetClanCASE( true );
            }

            // now reset the combo boxes to the closest choices we previously selected
            LoadSelections();

            // recalculate the mech.
            RecalcEngine();
            RecalcGyro();
            RecalcIntStruc();
            RecalcCockpit();
            CurMech.GetActuators().PlaceActuators();
            RecalcHeatSinks();
            RecalcJumpJets();
            RecalcEnhancements();
            RecalcArmor();
        }

        RecalcEquipment();
        SetWeaponChoosers();
        chkUseTC.setSelected( false );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
    }//GEN-LAST:event_cmbTechBaseActionPerformed

    private void cmbPhysEnhanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPhysEnhanceActionPerformed
        if( BuildLookupName( CurMech.GetPhysEnhance().GetCurrentState() ).equals( (String) cmbPhysEnhance.getSelectedItem() ) ) {
            return;
        }

        RecalcEnhancements();

        // check our exclusions
        try {
            CurMech.GetLoadout().CheckExclusions( CurMech.GetPhysEnhance() );
        } catch( Exception e ) {
            Media.Messager( this, e.getMessage() );
            cmbPhysEnhance.setSelectedItem( "No Enhancement" );
            RecalcEnhancements();
            return;
        }

        lblRunMP.setText( "" + CurMech.GetRunningMP() );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbPhysEnhanceActionPerformed

    private void cmbCockpitTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCockpitTypeActionPerformed
        if( CurMech.GetCockpit().LookupName().equals( (String) cmbCockpitType.getSelectedItem() ) ) {
            return;
        }
        RecalcCockpit();

        // now refresh the information panes
        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbCockpitTypeActionPerformed

    private void cmbGyroTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGyroTypeActionPerformed
        if( BuildLookupName( CurMech.GetGyro().GetCurrentState() ).equals( (String) cmbGyroType.getSelectedItem() ) ) {
            return;
        }
        RecalcGyro();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbGyroTypeActionPerformed

    private void cmbEngineTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEngineTypeActionPerformed
        if( BuildLookupName( CurMech.GetEngine().GetCurrentState() ).equals( (String) cmbEngineType.getSelectedItem() ) ) {
            // only nuclear-powered mechs may use jump jets
            if( CurMech.GetEngine().IsNuclear() ) {
                if( cmbJumpJetType.getSelectedItem() == null ) {
                    EnableJumpJets( false );
                } else {
                    EnableJumpJets( true );
                }
            } else {
                EnableJumpJets( false );
            }
            return;
        }
        RecalcEngine();

        // only nuclear-powered mechs may use jump jets
        if( CurMech.GetEngine().IsNuclear() ) {
            if( cmbJumpJetType.getSelectedItem() == null ) {
                EnableJumpJets( false );
            } else {
                EnableJumpJets( true );
            }
        } else {
            EnableJumpJets( false );
        }

        // refresh the selected equipment listbox
        if( CurMech.GetLoadout().GetNonCore().toArray().length <= 0 ) {
            Equipment[SELECTED] = new Object[] { " " };
        } else {
            Equipment[SELECTED] = CurMech.GetLoadout().GetNonCore().toArray();
        }
        lstSelectedEquipment.setListData( Equipment[SELECTED] );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbEngineTypeActionPerformed

    private void cmbInternalTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbInternalTypeActionPerformed
        if( BuildLookupName( CurMech.GetIntStruc().GetCurrentState() ).equals( (String) cmbInternalType.getSelectedItem() ) ) {
            return;
        }
        RecalcIntStruc();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbInternalTypeActionPerformed

    private void cmbMotiveTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMotiveTypeActionPerformed
        if( cmbMotiveType.getSelectedIndex() == 0 ) {
            // if the mech is already a biped, forget it
            if( ! CurMech.IsQuad() ) { return; }

            //Check for Robotic cockpit which is not allowed on a biped.
            if ( CurMech.GetCockpit().CritName().equals("Robotic Cockpit") ) {
                cmbCockpitType.setSelectedItem("Standard Cockpit");
                RecalcCockpit();
            }
            
            CurMech.SetBiped();
            // internal structure is always reset to standard on changing the
            // motive type.
            cmbInternalType.setSelectedIndex( 0 );
            ((javax.swing.border.TitledBorder) pnlLAArmorBox.getBorder()).setTitle( "LA" );
            ((javax.swing.border.TitledBorder) pnlRAArmorBox.getBorder()).setTitle( "RA" );
            ((javax.swing.border.TitledBorder) pnlLLArmorBox.getBorder()).setTitle( "LL" );
            ((javax.swing.border.TitledBorder) pnlRLArmorBox.getBorder()).setTitle( "RL" );
            scrRACrits.setPreferredSize( new java.awt.Dimension( 105, 170 ) );
            scrLACrits.setPreferredSize( new java.awt.Dimension( 105, 170 ) );
        } else {
            // if the mech is already a quad, forget it.
            if( CurMech.IsQuad() ) { return; }
            CurMech.SetQuad();
            // internal structure is always reset to standard on changing the
            // motive type.
            cmbInternalType.setSelectedIndex( 0 );
            ((javax.swing.border.TitledBorder) pnlLAArmorBox.getBorder()).setTitle( "FLL" );
            ((javax.swing.border.TitledBorder) pnlRAArmorBox.getBorder()).setTitle( "FRL" );
            ((javax.swing.border.TitledBorder) pnlLLArmorBox.getBorder()).setTitle( "RLL" );
            ((javax.swing.border.TitledBorder) pnlRLArmorBox.getBorder()).setTitle( "RRL" );
            scrRACrits.setPreferredSize( new java.awt.Dimension( 105, 87 ) );
            scrLACrits.setPreferredSize( new java.awt.Dimension( 105, 87 ) );
        }

        // set the loadout arrays
        SetLoadoutArrays();

        // fix the armor spinners
        FixArmorSpinners();

        // Check any AES systems that may have been installed
        CheckAES();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        RefreshInternalPoints();
        SetWeaponChoosers();
    }//GEN-LAST:event_cmbMotiveTypeActionPerformed

    private void mnuCreditsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCreditsActionPerformed
        dlgCredits Credits = new dlgCredits( this, true );
        Credits.setLocationRelativeTo( this );
        Credits.setVisible( true );
    }//GEN-LAST:event_mnuCreditsActionPerformed

    private void cmbArmorTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbArmorTypeActionPerformed
        if( BuildLookupName( CurMech.GetArmor().GetCurrentState() ).equals( (String) cmbArmorType.getSelectedItem() ) ) {
            return;
        }
        RecalcArmor();
        // we check for hardened armor, you can only have so many IJJs
        FixJJSpinnerModel();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbArmorTypeActionPerformed

    private void mnuOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOptionsActionPerformed
        dlgPrefs preferences = new dlgPrefs( this, true );
        preferences.setLocationRelativeTo( this );
        preferences.setVisible( true );
        Mechrender.Reset();
        ResetAmmo();
        RefreshInternalPoints();
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_mnuOptionsActionPerformed

    private void spnHDArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnHDArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHDArmor.getModel();
        javax.swing.JComponent editor = spnHDArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnHDArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnHDArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_HD );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_HD );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_HD );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_HD );
            }
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnHDArmorStateChanged

    private void spnRAArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnRAArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRAArmor.getModel();
        javax.swing.JComponent editor = spnRAArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRAArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRAArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_RA );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_RA );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_RA );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_RA );
            }
        }

        // see if we need to change the left arm as well
        if( btnBalanceArmor.isSelected() ) {
            a.SetArmor( LocationIndex.MECH_LOC_LA, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnLAArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_LA ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnRAArmorStateChanged

    private void spnRTArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnRTArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRTArmor.getModel();
        javax.swing.JComponent editor = spnRTArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRTArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRTArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_RT );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_RT );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_RT );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_RT );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnRTRArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_RTR ) );

        // see if we need to change the left torso as well
        if( btnBalanceArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnRTArmor.getModel();
            a.SetArmor( LocationIndex.MECH_LOC_LT, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnLTArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_LT ) );
        }

        // now refresh the information panes
       RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnRTArmorStateChanged

    private void spnRTRArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnRTRArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRTRArmor.getModel();
        javax.swing.JComponent editor = spnRTRArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRTRArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRTRArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_RTR );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_RTR );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_RTR );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_RTR );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnRTArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_RT ) );

        // see if we need to change the left torso as well
        // see if we need to change the left torso as well
        if( btnBalanceArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnRTRArmor.getModel();
            a.SetArmor( LocationIndex.MECH_LOC_LTR, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnLTRArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_LTR ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnRTRArmorStateChanged

    private void spnLAArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLAArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnLAArmor.getModel();
        javax.swing.JComponent editor = spnLAArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnLAArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnLAArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_LA );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_LA );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_LA );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_LA );
            }
        }

        // see if we need to change the right arm as well
        if( btnBalanceArmor.isSelected() ) {
            a.SetArmor( LocationIndex.MECH_LOC_RA, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnRAArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_RA ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnLAArmorStateChanged

    private void spnLTArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLTArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnLTArmor.getModel();
        javax.swing.JComponent editor = spnLTArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnLTArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnLTArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_LT );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_LT );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_LT );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_LT );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnLTRArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_LTR ) );

        // see if we need to change the right torso as well
        if( btnBalanceArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnLTArmor.getModel();
            a.SetArmor( LocationIndex.MECH_LOC_RT, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnRTArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_RT ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnLTArmorStateChanged

    private void spnLTRArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLTRArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnLTRArmor.getModel();
        javax.swing.JComponent editor = spnLTRArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnLTRArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnLTRArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_LTR );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_LTR );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_LTR );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_LTR );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnLTArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_LT ) );

        // see if we need to change the right torso as well
        if( btnBalanceArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnLTRArmor.getModel();
            a.SetArmor( LocationIndex.MECH_LOC_RTR, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnRTRArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_RTR ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnLTRArmorStateChanged

    private void spnCTArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCTArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnCTArmor.getModel();
        javax.swing.JComponent editor = spnCTArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnCTArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnCTArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_CT );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_CT );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_CT );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_CT );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnCTRArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_CTR ) );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnCTArmorStateChanged

    private void spnCTRArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCTRArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnCTRArmor.getModel();
        javax.swing.JComponent editor = spnCTRArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnCTRArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnCTRArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_CTR );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_CTR );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_CTR );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_CTR );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnCTArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_CT ) );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnCTRArmorStateChanged

    private void spnLLArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLLArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnLLArmor.getModel();
        javax.swing.JComponent editor = spnLLArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnLLArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnLLArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_LL );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_LL );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_LL );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_LL );
            }
        }

        // see if we need to change the right arm as well
        if( btnBalanceArmor.isSelected() ) {
            a.SetArmor( LocationIndex.MECH_LOC_RL, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnRLArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_RL ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnLLArmorStateChanged

    private void spnRLArmorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnRLArmorStateChanged
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRLArmor.getModel();
        javax.swing.JComponent editor = spnRLArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRLArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRLArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        MechArmor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_RL );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( LocationIndex.MECH_LOC_RL );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( LocationIndex.MECH_LOC_RL );
                curmech = a.GetLocationArmor( LocationIndex.MECH_LOC_RL );
            }
        }

        // see if we need to change the right arm as well
        if( btnBalanceArmor.isSelected() ) {
            a.SetArmor( LocationIndex.MECH_LOC_LL, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnLLArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_LL ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnRLArmorStateChanged

    private void btnMaxArmorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaxArmorActionPerformed
        // this simply maximizes the mech's armor
        MechArmor a = CurMech.GetArmor();

        // set the simple stuff first.
        a.SetArmor( LocationIndex.MECH_LOC_HD, 9 );
        a.SetArmor( LocationIndex.MECH_LOC_LA, a.GetLocationMax( LocationIndex.MECH_LOC_LA ) );
        a.SetArmor( LocationIndex.MECH_LOC_RA, a.GetLocationMax( LocationIndex.MECH_LOC_RA ) );
        a.SetArmor( LocationIndex.MECH_LOC_LL, a.GetLocationMax( LocationIndex.MECH_LOC_LL ) );
        a.SetArmor( LocationIndex.MECH_LOC_RL, a.GetLocationMax( LocationIndex.MECH_LOC_RL ) );

        // now to set the torsos
        int rear = Math.round( a.GetLocationMax( LocationIndex.MECH_LOC_CT ) * Prefs.getInt( "ArmorCTRPercent", MechArmor.DEFAULT_CTR_ARMOR_PERCENT ) / 100 );
        a.SetArmor( LocationIndex.MECH_LOC_CTR, rear );
        a.SetArmor( LocationIndex.MECH_LOC_CT, a.GetLocationMax( LocationIndex.MECH_LOC_CT ) - rear );
        rear = Math.round( a.GetLocationMax( LocationIndex.MECH_LOC_LT ) * Prefs.getInt( "ArmorSTRPercent", MechArmor.DEFAULT_STR_ARMOR_PERCENT ) / 100 );
        a.SetArmor( LocationIndex.MECH_LOC_LTR, rear );
        a.SetArmor( LocationIndex.MECH_LOC_LT, a.GetLocationMax( LocationIndex.MECH_LOC_LT ) - rear );
        rear = Math.round( a.GetLocationMax( LocationIndex.MECH_LOC_RT ) * Prefs.getInt( "ArmorSTRPercent", MechArmor.DEFAULT_STR_ARMOR_PERCENT ) / 100 );
        a.SetArmor( LocationIndex.MECH_LOC_RTR, rear );
        a.SetArmor( LocationIndex.MECH_LOC_RT, a.GetLocationMax( LocationIndex.MECH_LOC_RT ) - rear );

        // if we fix the spinner models, they should refresh the screen
        FixArmorSpinners();

        // of course, we'll also have to set the head spinner manually.
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHDArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.MECH_LOC_HD ) );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_btnMaxArmorActionPerformed

    private void btnArmorTonsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArmorTonsActionPerformed
        // we'll need a new dialogue to get the tonnage
        dlgArmorTonnage ArmorDialogue = new dlgArmorTonnage( this, true );
        ArmorDialogue.setLocationRelativeTo( this );
        ArmorDialogue.setVisible( true );

        // see if we have a good number
        if( ArmorDialogue.NewTonnage() ) {
            double result = ArmorDialogue.GetResult();
            ArmorTons.SetArmorTonnage( result );
            try {
                CurMech.Visit( ArmorTons );
            } catch( Exception e ) {
                // this should never throw an exception, but log it anyway
                System.err.println( e.getMessage() );
                e.printStackTrace();
            }

            ArmorDialogue.dispose();
        } else {
            ArmorDialogue.dispose();
        }

        // if we fix the spinner models, they should refresh the screen
        FixArmorSpinners();

        // of course, we'll also have to set the head spinner manually.
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHDArmor.getModel();
        n.setValue( (Object) CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_HD ) );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_btnArmorTonsActionPerformed

    private void chkLAHandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLAHandActionPerformed
        if( chkLAHand.isSelected() == CurMech.GetActuators().LeftHandInstalled() ) {
            return;
        }
        if( chkLAHand.isSelected() ) {
            // check each crit and ensure we don't have an item that precludes this
            abPlaceable[] check = CurMech.GetLoadout().GetLACrits();
            for( int i = 0; i < check.length; i++ ) {
                if( check[i] instanceof ifWeapon ) {
                    if( ((ifWeapon) check[i]).OmniRestrictActuators() && CurMech.IsOmnimech() ) {
                        Media.Messager( this, check[i].LookupName() + " prevents the installation of the hand." );
                        chkLAHand.setSelected( false );
                        return;
                    }
                }
                if( check[i] instanceof PhysicalWeapon ) {
                    if( ((PhysicalWeapon) check[i]).ReplacesHand() ) {
                        Media.Messager( this, check[i].LookupName() + " prevents the installation of the hand." );
                        chkLAHand.setSelected( false );
                        return;
                    }
                }
            }
            CurMech.GetActuators().AddLeftHand();
        } else {
            CurMech.GetActuators().RemoveLeftHand();
            // check for the presence of physical weapons and remove them
            ArrayList v = CurMech.GetLoadout().GetNonCore();
            for( int i = 0; i < v.size(); i++ ) {
                abPlaceable p = (abPlaceable) v.get( i );
                if( p instanceof PhysicalWeapon ) {
                    if( ((PhysicalWeapon) p).RequiresHand() ) {
                        if( CurMech.GetLoadout().Find( p ) == LocationIndex.MECH_LOC_LA ) {
                            CurMech.GetLoadout().UnallocateAll( p, false );
                        }
                    }
                }
            }
        }
        CheckActuators();
        RefreshInfoPane();
    }//GEN-LAST:event_chkLAHandActionPerformed

    private void chkLALowerArmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLALowerArmActionPerformed
        if( chkLALowerArm.isSelected() == CurMech.GetActuators().LeftLowerInstalled() ) {
            return;
        }
        if( chkLALowerArm.isSelected() ) {
            // check each crit and ensure we don't have an item that precludes this
            abPlaceable[] check = CurMech.GetLoadout().GetLACrits();
            for( int i = 0; i < check.length; i++ ) {
                if( check[i] instanceof ifWeapon ) {
                    if( ((ifWeapon) check[i]).OmniRestrictActuators() && CurMech.IsOmnimech() ) {
                        Media.Messager( this, check[i].LookupName() + " prevents the installation of the lower arm." );
                        chkLALowerArm.setSelected( false );
                        return;
                    }
                }
                if( check[i] instanceof PhysicalWeapon ) {
                    if( ((PhysicalWeapon) check[i]).ReplacesLowerArm() ) {
                        Media.Messager( this, check[i].LookupName() + " prevents the installation of the lower arm." );
                        chkLALowerArm.setSelected( false );
                        return;
                    }
                }
            }
            CurMech.GetActuators().AddLeftLowerArm();
        } else {
            CurMech.GetActuators().RemoveLeftLowerArm();
            // check for the presence of physical weapons and remove them
            ArrayList v = CurMech.GetLoadout().GetNonCore();
            for( int i = 0; i < v.size(); i++ ) {
                abPlaceable p = (abPlaceable) v.get( i );
                if( p instanceof PhysicalWeapon ) {
                    if( ((PhysicalWeapon) p).RequiresLowerArm() ) {
                        if( CurMech.GetLoadout().Find( p ) == LocationIndex.MECH_LOC_LA ) {
                            CurMech.GetLoadout().UnallocateAll( p, false );
                        }
                    }
                }
            }
        }
        CheckActuators();
        RefreshInfoPane();
    }//GEN-LAST:event_chkLALowerArmActionPerformed

    private void chkRAHandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRAHandActionPerformed
        if( chkRAHand.isSelected() == CurMech.GetActuators().RightHandInstalled() ) {
            return;
        }
        if( chkRAHand.isSelected() ) {
            // check each crit and ensure we don't have an item that precludes this
            abPlaceable[] check = CurMech.GetLoadout().GetRACrits();
            for( int i = 0; i < check.length; i++ ) {
                if( check[i] instanceof ifWeapon ) {
                    if( ((ifWeapon) check[i]).OmniRestrictActuators() && CurMech.IsOmnimech() ) {
                        Media.Messager( this, check[i].LookupName() + " prevents the installation of the hand." );
                        chkRAHand.setSelected( false );
                        return;
                    }
                }
                if( check[i] instanceof PhysicalWeapon ) {
                    if( ((PhysicalWeapon) check[i]).ReplacesHand() ) {
                        Media.Messager( this, check[i].LookupName() + " prevents the installation of the hand." );
                        chkRAHand.setSelected( false );
                        return;
                    }
                }
            }
            CurMech.GetActuators().AddRightHand();
        } else {
            CurMech.GetActuators().RemoveRightHand();
            // check for the presence of physical weapons and remove them
            ArrayList v = CurMech.GetLoadout().GetNonCore();
            for( int i = 0; i < v.size(); i++ ) {
                abPlaceable p = (abPlaceable) v.get( i );
                if( p instanceof PhysicalWeapon ) {
                    if( ((PhysicalWeapon) p).RequiresHand() ) {
                        if( CurMech.GetLoadout().Find( p ) == LocationIndex.MECH_LOC_RA ) {
                            CurMech.GetLoadout().UnallocateAll( p, false );
                        }
                    }
                }
            }
        }
        CheckActuators();
        RefreshInfoPane();
    }//GEN-LAST:event_chkRAHandActionPerformed

    private void chkRALowerArmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRALowerArmActionPerformed
        if( chkRALowerArm.isSelected() == CurMech.GetActuators().RightLowerInstalled() ) {
            return;
        }
        if( chkRALowerArm.isSelected() ) {
            // check each crit and ensure we don't have an item that precludes this
            abPlaceable[] check = CurMech.GetLoadout().GetRACrits();
            for( int i = 0; i < check.length; i++ ) {
                if( check[i] instanceof ifWeapon ) {
                    if( ((ifWeapon) check[i]).OmniRestrictActuators() && CurMech.IsOmnimech() ) {
                        Media.Messager( this, check[i].LookupName() + " prevents the installation of the lower arm." );
                        chkRALowerArm.setSelected( false );
                        return;
                    }
                }
                if( check[i] instanceof PhysicalWeapon ) {
                    if( ((PhysicalWeapon) check[i]).ReplacesLowerArm() ) {
                        Media.Messager( this, check[i].LookupName() + " prevents the installation of the lower arm." );
                        chkRALowerArm.setSelected( false );
                        return;
                    }
                }
            }
            CurMech.GetActuators().AddRightLowerArm();
        } else {
            CurMech.GetActuators().RemoveRightLowerArm();
            // check for the presence of physical weapons and remove them
            ArrayList v = CurMech.GetLoadout().GetNonCore();
            for( int i = 0; i < v.size(); i++ ) {
                abPlaceable p = (abPlaceable) v.get( i );
                if( p instanceof PhysicalWeapon ) {
                    if( ((PhysicalWeapon) p).RequiresLowerArm() ) {
                        if( CurMech.GetLoadout().Find( p ) == LocationIndex.MECH_LOC_RA ) {
                            CurMech.GetLoadout().UnallocateAll( p, false );
                        }
                    }
                }
            }
        }
        CheckActuators();
        RefreshInfoPane();
    }//GEN-LAST:event_chkRALowerArmActionPerformed

    private void chkCTCASEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCTCASEActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasCTCASE() ) {
            chkCTCASE.setSelected( true );
            return;
        }
        if( CurMech.HasCTCase() == chkCTCASE.isSelected() ) { return; }
        if( chkCTCASE.isSelected() ) {
            try {
                CurMech.AddCTCase();
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkCTCASE.setSelected( false );
            }
        } else {
            CurMech.RemoveCTCase();
        }
        RefreshInfoPane();
    }//GEN-LAST:event_chkCTCASEActionPerformed

    private void chkRTCASEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRTCASEActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasRTCASE() ) {
            chkRTCASE.setSelected( true );
            return;
        }
        if( CurMech.HasRTCase() == chkRTCASE.isSelected() ) { return; }
        if( chkRTCASE.isSelected() ) {
            try {
                CurMech.AddRTCase();
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkRTCASE.setSelected( false );
            }
        } else {
            CurMech.RemoveRTCase();
        }
        RefreshInfoPane();
    }//GEN-LAST:event_chkRTCASEActionPerformed

    private void chkLTCASEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLTCASEActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasLTCASE() ) {
            chkLTCASE.setSelected( true );
            return;
        }
        if( CurMech.HasLTCase() == chkLTCASE.isSelected() ) { return; }
        if( chkLTCASE.isSelected() ) {
            try {
                CurMech.AddLTCase();
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkLTCASE.setSelected( false );
            }
        } else {
            CurMech.RemoveLTCase();
        }
        RefreshInfoPane();
    }//GEN-LAST:event_chkLTCASEActionPerformed

    private void btnRemoveEquipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveEquipActionPerformed
        if( lstSelectedEquipment.getSelectedIndex() < 0 ) { return; }
        int[] selected = lstSelectedEquipment.getSelectedIndices();
        if( selected.length == 0 ) { return; }
        // we work in reverse so we can properly manage the items in the queue
        for( int i = selected.length - 1; i >= 0; i-- ) {
            // abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetNonCore().get( lstSelectedEquipment.getSelectedIndex() );
            abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetNonCore().get( selected[i] );
            if( p.LocationLocked() &! ( p instanceof Talons ) ) {
                Media.Messager( this, "You may not remove a locked item from the loadout." );
                return;
            } else {
                CurMech.GetLoadout().Remove( p );
            }
        }
        // refresh the selected equipment listbox
        if( CurMech.GetLoadout().GetNonCore().toArray().length <= 0 ) {
            Equipment[SELECTED] = new Object[] { " " };
        } else {
            Equipment[SELECTED] = CurMech.GetLoadout().GetNonCore().toArray();
        }
        lstSelectedEquipment.setListData( Equipment[SELECTED] );

        // Check the targeting computer if needed
        if( CurMech.UsingTC() ) {
            CurMech.UnallocateTC();
        }

        // refresh the ammunition display
        ResetAmmo();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_btnRemoveEquipActionPerformed

    private void btnAddEquipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddEquipActionPerformed
        abPlaceable a = null;
        int Index = 0;
        ArrayList v;

        // figure out which list box to pull from
        switch( tbpWeaponChooser.getSelectedIndex() ) {
        case BALLISTIC:
            if( lstChooseBallistic.getSelectedIndex() < 0 ) { break; }
            a = (abPlaceable) Equipment[BALLISTIC][lstChooseBallistic.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurMech );
            break;
        case ENERGY:
            if( lstChooseEnergy.getSelectedIndex() < 0 ) { break; }
            a = (abPlaceable) Equipment[ENERGY][lstChooseEnergy.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurMech );
            break;
        case MISSILE:
            if( lstChooseMissile.getSelectedIndex() < 0 ) { break; }
            a = (abPlaceable) Equipment[MISSILE][lstChooseMissile.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurMech );
            if( ((RangedWeapon) a).IsFCSCapable() ) {
                if( CurMech.UsingArtemisIV() ) {
                    if( ((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_ArtemisIV || ((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_ArtemisV ) {
                        ((RangedWeapon) a).UseFCS( true, ifMissileGuidance.FCS_ArtemisIV );
                    }
                }
                if( CurMech.UsingArtemisV() ) {
                    if( ((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_ArtemisV ) {
                        ((RangedWeapon) a).UseFCS( true, ifMissileGuidance.FCS_ArtemisV );
                    }
                }
                if( CurMech.UsingApollo() ) {
                    if( ((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_Apollo ) {
                        ((RangedWeapon) a).UseFCS( true, ifMissileGuidance.FCS_Apollo );
                    }
                }
            }
            break;
        case PHYSICAL:
            if( lstChoosePhysical.getSelectedIndex() < 0 ) { break; }
            if( ! ( Equipment[PHYSICAL][lstChoosePhysical.getSelectedIndex()] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[PHYSICAL][lstChoosePhysical.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurMech );
            break;
        case ARTILLERY:
            if( lstChooseArtillery.getSelectedIndex() < 0 ) { break; }
            if( ! ( Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurMech );
            break;
        case EQUIPMENT:
            if( lstChooseEquipment.getSelectedIndex() < 0 ) { break; }
            if( ! ( Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurMech );
            break;
        case AMMUNITION:
            if( lstChooseAmmunition.getSelectedIndex() < 0 ) { break; }
            Index = lstChooseAmmunition.getSelectedIndex();
            if( ! ( Equipment[AMMUNITION][Index] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[AMMUNITION][Index];
            a = data.GetEquipment().GetCopy( a, CurMech );
            break;
        }

        // check exclusions if needed
        if( a != null ) {
            try {
                CurMech.GetLoadout().CheckExclusions( a );
                if( a instanceof Equipment ) {
                    if ( ! ((Equipment) a).Validate( CurMech ) ) {
                        if( ((Equipment) a).RequiresQuad() ) {
                            throw new Exception( a.CritName() + " may only be mounted on a quad 'Mech." );
                        } else if( ((Equipment) a).MaxAllowed() > 0 ) {
                            throw new Exception( "Only " + ((Equipment) a).MaxAllowed() + " " + a.CritName() + "(s) may be mounted on one 'Mech." );
                        }
                    }
                }
            } catch( Exception e ) {
                Media.Messager( e.getMessage() );
                a = null;
            }
        }

        // now we can add it to the 'Mech
        if( a != null ) {
            boolean result = true;
            if( a instanceof Equipment ) {
                if( ((Equipment) a).IsVariableSize() ) {
                    dlgVariableSize SetTons = new dlgVariableSize( this, true, (Equipment) a );
                    SetTons.setLocationRelativeTo( this );
                    SetTons.setVisible( true );
                    result = SetTons.GetResult();
                }
            }
            if( result ) {
                if( a instanceof Talons ) {
                    if( ! a.Place( CurMech.GetLoadout() ) ) {
                        Media.Messager( "Talons cannot be added because there is not enough space." );
                        return;
                    }
                } else {
                    CurMech.GetLoadout().AddToQueue( a );
                    for( int i = 0; i < cmbNumEquips.getSelectedIndex(); i++ ) {
                        a = data.GetEquipment().GetCopy( a, CurMech );
                        CurMech.GetLoadout().AddToQueue( a );
                    }
                }

                // unallocate the TC if needed (if the size changes)
                if( a instanceof ifWeapon ) {
                    if( ((ifWeapon) a).IsTCCapable() && CurMech.UsingTC() ) {
                        CurMech.UnallocateTC();
                    }
                }

                // see if we need ammunition and add it if applicable
                ResetAmmo();

                if( a instanceof Ammunition ) {
                    // added for support if the user selected ammo.  The ResetAmmo()
                    // method clears the selected index.
                    lstChooseAmmunition.setSelectedIndex(Index);
                }

                // refresh the selected equipment listbox
                if( CurMech.GetLoadout().GetNonCore().toArray().length <= 0 ) {
                    Equipment[SELECTED] = new Object[] { " " };
                } else {
                    Equipment[SELECTED] = CurMech.GetLoadout().GetNonCore().toArray();
                }
                lstSelectedEquipment.setListData( Equipment[SELECTED] );
            }

            // now refresh the information panes
            RefreshSummary();
            RefreshInfoPane();
            cmbNumEquips.setSelectedIndex( 0 );
        }
    }//GEN-LAST:event_btnAddEquipActionPerformed

    private void btnClearEquipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearEquipActionPerformed
        CurMech.GetLoadout().SafeClearLoadout();

        // refresh the selected equipment listbox
        if( CurMech.GetLoadout().GetNonCore().toArray().length <= 0 ) {
            Equipment[SELECTED] = new Object[] { " " };
        } else {
            Equipment[SELECTED] = CurMech.GetLoadout().GetNonCore().toArray();
        }
        lstSelectedEquipment.setListData( Equipment[SELECTED] );

        // Check the targeting computer if needed
        if( CurMech.UsingTC() ) {
            CurMech.CheckTC();
        }

        // refresh the ammunition display
        ResetAmmo();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_btnClearEquipActionPerformed

    private void btnClearLoadoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearLoadoutActionPerformed
        CurMech.GetLoadout().SafeMassUnallocate();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_btnClearLoadoutActionPerformed

    private void lstChooseMissileValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstChooseMissileValueChanged
        if( lstChooseMissile.getSelectedIndex() < 0 ) { return; }
        abPlaceable p = (abPlaceable) Equipment[MISSILE][lstChooseMissile.getSelectedIndex()];
        ShowInfoOn( p );
    }//GEN-LAST:event_lstChooseMissileValueChanged

    private void lstChooseEnergyValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstChooseEnergyValueChanged
        if( lstChooseEnergy.getSelectedIndex() < 0 ) { return; }
        abPlaceable p = (abPlaceable) Equipment[ENERGY][lstChooseEnergy.getSelectedIndex()];
        ShowInfoOn( p );
    }//GEN-LAST:event_lstChooseEnergyValueChanged

    private void lstChooseAmmunitionValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstChooseAmmunitionValueChanged
        if( lstChooseAmmunition.getSelectedIndex() < 0 ) { return; }
        if( ! ( Equipment[AMMUNITION][lstChooseAmmunition.getSelectedIndex()] instanceof Ammunition ) ) { return; }
        abPlaceable p = (abPlaceable) Equipment[AMMUNITION][lstChooseAmmunition.getSelectedIndex()];
        ShowInfoOn( p );
    }//GEN-LAST:event_lstChooseAmmunitionValueChanged

    private void chkArtemisSRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkArtemisSRMActionPerformed
    }//GEN-LAST:event_chkArtemisSRMActionPerformed

    private void chkArtemisLRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkArtemisLRMActionPerformed
    }//GEN-LAST:event_chkArtemisLRMActionPerformed

    private void chkArtemisMMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkArtemisMMLActionPerformed
    }//GEN-LAST:event_chkArtemisMMLActionPerformed

    private void lstChooseBallisticValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstChooseBallisticValueChanged
        if( lstChooseBallistic.getSelectedIndex() < 0 ) { return; }
        abPlaceable p = (abPlaceable) Equipment[BALLISTIC][lstChooseBallistic.getSelectedIndex()];
        ShowInfoOn( p );
    }//GEN-LAST:event_lstChooseBallisticValueChanged

    private void chkUseTCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseTCActionPerformed
        if( CurMech.UsingTC() == chkUseTC.isSelected() ) { return; }
        if( chkUseTC.isSelected() ) {
            try {
                CurMech.GetLoadout().CheckExclusions( CurMech.GetTC() );
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurMech.UseTC( true, tech.IsClan() );
                } else if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    CurMech.UseTC( true, true );
                } else {
                    CurMech.UseTC( true, false );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                CurMech.UseTC( false, false );
            }
        } else {
            CurMech.UseTC( false, false );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_chkUseTCActionPerformed

    private void btnCompactCritsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompactCritsActionPerformed
        CurMech.GetLoadout().Compact();

        // now we have to refresh the loadout displays.
        RefreshInfoPane();
}//GEN-LAST:event_btnCompactCritsActionPerformed

    private void lstChooseEquipmentValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstChooseEquipmentValueChanged
        if( lstChooseEquipment.getSelectedIndex() < 0 ) { return; }
        //if( ! ( Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()] instanceof Equipment ) ) { return; }
        abPlaceable p = (abPlaceable) Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()];
        ShowInfoOn( p );
    }//GEN-LAST:event_lstChooseEquipmentValueChanged

    private void mnuSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSummaryActionPerformed
        SolidifyMech();
        dlgSummaryInfo Summary = new dlgSummaryInfo( this, true );
        Summary.setLocationRelativeTo( this );
        Summary.setVisible( true );
    }//GEN-LAST:event_mnuSummaryActionPerformed

    private void btnExportMTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportMTFActionPerformed
        // exports the mech to MTF format for use in Megamek

        String dir = Prefs.get( "MTFExportPath", "none" );
        if( dir.equals( "none" ) ) {
            dir = Prefs.get( "LastOpenDirectory", "" );
        }
        File savemech = GetSaveFile( "mtf", dir, false, true );
        if( savemech == null ) {
            return;
        }

        String filename = "";
        IO.MTFWriter mtfw = new IO.MTFWriter( CurMech );
        try {
            filename = savemech.getCanonicalPath();
            mtfw.WriteMTF( filename );
        } catch( IOException e ) {
            Media.Messager( this, "There was a problem writing the file:\n" + e.getMessage() );
            return;
        }

        // if there were no problems, let the user know how it went
        Media.Messager( this, "Mech saved successfully to MTF:\n" + filename );
        setTitle( SSWConstants.AppName + " " + SSWConstants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
    }//GEN-LAST:event_btnExportMTFActionPerformed

    private void lstChoosePhysicalValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstChoosePhysicalValueChanged
        if( lstChoosePhysical.getSelectedIndex() < 0 ) { return; }
        if( ! ( Equipment[PHYSICAL][lstChoosePhysical.getSelectedIndex()] instanceof PhysicalWeapon ) ) { return; }
        abPlaceable p = (abPlaceable) Equipment[PHYSICAL][lstChoosePhysical.getSelectedIndex()];
        ShowInfoOn( p );
    }//GEN-LAST:event_lstChoosePhysicalValueChanged

    private void btnExportTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportTXTActionPerformed
        // exports the mech to TXT format
        String CurLoadout = "";
        if( CurMech.IsOmnimech() ) {
            CurLoadout = CurMech.GetLoadout().GetName();
        }

        String dir = Prefs.get( "TXTExportPath", "none" );
        if( dir.equals( "none" ) ) {
            dir = Prefs.get( "LastOpenDirectory", "" );
        }
        File savemech = GetSaveFile( "txt", dir, false, false );
        if( savemech == null ) {
            return;
        }

        String filename = "";
        TXTWriter txtw = new TXTWriter( CurMech );
        try {
            filename = savemech.getCanonicalPath();
            txtw.WriteTXT( filename );
        } catch( IOException e ) {
            Media.Messager( this, "There was a problem writing the file:\n" + e.getMessage() );
            return;
        }

        // if there were no problems, let the user know how it went
        Media.Messager( this, "Mech saved successfully to TXT:\n" + filename );

        // lastly, if this is an omnimech, reset the display to the last loadout
        if( CurMech.IsOmnimech() ) {
            cmbOmniVariant.setSelectedItem( CurLoadout );
            cmbOmniVariantActionPerformed( evt );
        }
        setTitle( SSWConstants.AppName + " " + SSWConstants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
    }//GEN-LAST:event_btnExportTXTActionPerformed

    private void btnExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportHTMLActionPerformed
        // exports the mech to HTML format
        String CurLoadout = "";
        if( CurMech.IsOmnimech() ) {
            CurLoadout = CurMech.GetLoadout().GetName();
        }

        String dir = Prefs.get( "HTMLExportPath", "none" );
        if( dir.equals( "none" ) ) {
            dir = Prefs.get( "LastOpenDirectory", "" );
        }
        File savemech = GetSaveFile( "html", dir, false, false );
        if( savemech == null ) {
            return;
        }

        String filename = "";
        HTMLWriter HTMw = new HTMLWriter( CurMech );
        try {
            filename = savemech.getCanonicalPath();
            HTMw.WriteHTML( SSWConstants.HTMLTemplateName, filename );
        } catch( IOException e ) {
            Media.Messager( this, "There was a problem writing the file:\n" + e.getMessage() );
            return;
        }

        // if there were no problems, let the user know how it went
        Media.Messager( this, "Mech saved successfully to HTML:\n" + filename );

        // lastly, if this is an omnimech, reset the display to the last loadout
        if( CurMech.IsOmnimech() ) {
            cmbOmniVariant.setSelectedItem( CurLoadout );
            cmbOmniVariantActionPerformed( evt );
        }
        setTitle( SSWConstants.AppName + " " + SSWConstants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
    }//GEN-LAST:event_btnExportHTMLActionPerformed

    private void mnuAboutSSWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutSSWActionPerformed
        dlgAboutBox about = new dlgAboutBox( this, true );
        about.setLocationRelativeTo( this );
        about.setVisible( true );
    }//GEN-LAST:event_mnuAboutSSWActionPerformed

    private void mnuExportTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportTXTActionPerformed
        SetSource = false;
        btnExportTXTActionPerformed( evt );
        SetSource = true;
    }//GEN-LAST:event_mnuExportTXTActionPerformed

    private void mnuExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportHTMLActionPerformed
        SetSource = false;
        btnExportHTMLActionPerformed( evt );
        SetSource = true;
    }//GEN-LAST:event_mnuExportHTMLActionPerformed

    private void mnuExportMTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportMTFActionPerformed
        SetSource = false;
        btnExportMTFActionPerformed( evt );
        SetSource = true;
    }//GEN-LAST:event_mnuExportMTFActionPerformed

    private void chkYearRestrictActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkYearRestrictActionPerformed
        // This locks in the mech's production year, era, and tech base.
        int year = 0;
        if( CurMech.IsYearRestricted() == chkYearRestrict.isSelected() ) { return; }

        // if we just unchecked the box, clear all locks and exit.
        if( ! chkYearRestrict.isSelected() ) {
            cmbMechEra.setEnabled( true );
            cmbTechBase.setEnabled( true );
            txtProdYear.setEnabled( true );
            CurMech.SetYearRestricted( false );
            switch( cmbMechEra.getSelectedIndex() ) {
                case AvailableCode.ERA_STAR_LEAGUE:
                    CurMech.SetYear( 2750, false );
                    break;
                case AvailableCode.ERA_SUCCESSION:
                    CurMech.SetYear( 3025, false );
                    break;
                case AvailableCode.ERA_CLAN_INVASION:
                    CurMech.SetYear( 3070, false );
                    break;
                case AvailableCode.ERA_DARK_AGES:
                    CurMech.SetYear( 3132, false );
                    break;
                case AvailableCode.ERA_ALL:
                    CurMech.SetYear( 0, false );
                    break;
            }
        } else {
            // ensure we have a good year.
            try{
                year = Integer.parseInt( txtProdYear.getText() ) ;
            } catch( NumberFormatException n ) {
                Media.Messager( this, "The production year is not a number." );
                txtProdYear.setText( "" );
                chkYearRestrict.setSelected( false );
                return;
            }

            // ensure the year is between the era years.
            switch ( cmbMechEra.getSelectedIndex() ) {
                case AvailableCode.ERA_STAR_LEAGUE:
                    // Star League era
                    if( year < 2443 || year > 2800 ) {
                        Media.Messager( this, "The year does not fall within this era." );
                        txtProdYear.setText( "" );
                        chkYearRestrict.setSelected( false );
                        return;
                    }
                    break;
                case AvailableCode.ERA_SUCCESSION:
                    // Succession Wars era
                    if( year < 2801 || year > 3050 ) {
                        Media.Messager( this, "The year does not fall within this era." );
                        txtProdYear.setText( "" );
                        chkYearRestrict.setSelected( false );
                        return;
                    }
                    break;
                case AvailableCode.ERA_CLAN_INVASION:
                    // Clan Invasion Era
                    if( year < 3051 || year > 3131 ) {
                        Media.Messager( this, "The year does not fall within this era." );
                        txtProdYear.setText( "" );
                        chkYearRestrict.setSelected( false );
                        return;
                    }
                    break;
                case AvailableCode.ERA_DARK_AGES:
                    // Clan Invasion Era
                    if( year < 3132 ) {
                        Media.Messager( this, "The year does not fall within this era." );
                        txtProdYear.setText( "" );
                        chkYearRestrict.setSelected( false );
                        return;
                    }
                    break;
                case AvailableCode.ERA_ALL:
                    // all era
                    chkYearRestrict.setSelected( false );
                    chkYearRestrict.setEnabled( false );
            }

            // we know we have a good year, lock it in.
            cmbMechEra.setEnabled( false );
            cmbTechBase.setEnabled( false );
            txtProdYear.setEnabled( false );
            CurMech.SetYear( year, true );
            CurMech.SetYearRestricted( true );
        }

        // get the currently chosen selections
        SaveSelections();

        // first, refresh all the combo boxes.
        BuildChassisSelector();
        BuildEngineSelector();
        BuildGyroSelector();
        BuildCockpitSelector();
        BuildEnhancementSelector();
        BuildHeatsinkSelector();
        BuildJumpJetSelector();
        BuildArmorSelector();
        RefreshEquipment();
        CheckOmnimech();

        // now reset the combo boxes to the closest previously selected
        LoadSelections();

        // now redo the mech based on what happened.
        RecalcEngine();
        RecalcGyro();
        RecalcIntStruc();
        RecalcCockpit();
        CurMech.GetActuators().PlaceActuators();
        RecalcHeatSinks();
        RecalcJumpJets();
        RecalcEnhancements();
        RecalcArmor();
        RecalcEquipment();
        //CurMech.GetLoadout().FlushIllegal( cmbMechEra.getSelectedIndex(), year, chkYearRestrict.isSelected() );
        CurMech.GetLoadout().FlushIllegal();

        // finally, refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();
    }//GEN-LAST:event_chkYearRestrictActionPerformed

    private void cmbJumpJetTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbJumpJetTypeActionPerformed
        RecalcJumpJets();
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbJumpJetTypeActionPerformed

    private void mnuNewMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewMechActionPerformed
        if( CurMech.HasChanged() ) {
            int choice = javax.swing.JOptionPane.showConfirmDialog( this,
                "The current 'Mech has changed.\nDo you want to discard those changes?", "Discard Changes?", javax.swing.JOptionPane.YES_NO_OPTION );
            if( choice == 1 ) { return; }
        }
        GetNewMech();
        Prefs.put("Currentfile", "");
    }//GEN-LAST:event_mnuNewMechActionPerformed

    private void cmbRulesLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRulesLevelActionPerformed
        if( Load ) { return; }
        int NewLevel = cmbRulesLevel.getSelectedIndex();
        int OldLevel = CurMech.GetLoadout().GetRulesLevel();
        int OldType = cmbMechType.getSelectedIndex();
        int OldTech = CurMech.GetTechbase();

        if( OldLevel == NewLevel ) {
            // we're already at the correct rules level.
            return;
        }

        // do we have an OmniMech?
        if( CurMech.IsOmnimech() ) {
            // see if we can set to the new rules level.
            if( CurMech.GetLoadout().SetRulesLevel( NewLevel ) ) {
                // we can.
                if( OldLevel > NewLevel ) {
                    //CurMech.GetLoadout().FlushIllegal( NewLevel, 0, false );
                    CurMech.GetLoadout().FlushIllegal();
                }
                BuildTechBaseSelector();
                cmbTechBase.setSelectedIndex( CurMech.GetLoadout().GetTechBase() );
                BuildJumpJetSelector();
                RefreshEquipment();
                RecalcEquipment();
            } else {
                // can't.  reset to the default rules level and scold the user
                Media.Messager( this, "You cannot set an OmniMech's loadout to a Rules Level\nlower than it's chassis' Rules Level." );
                cmbRulesLevel.setSelectedIndex( CurMech.GetLoadout().GetRulesLevel() );
                return;
            }
        } else {
            CurMech.SetRulesLevel( NewLevel );
            BuildMechTypeSelector();
            CheckTonnage( true );

            // get the currently chosen selections
            SaveSelections();
            BuildTechBaseSelector();
            if( OldTech >= cmbTechBase.getItemCount() ) {
                // ooooh fun, we can't set it correctly.
                switch( OldTech ) {
                    case AvailableCode.TECH_INNER_SPHERE:
                        // WTF???
                        System.err.println( "Fatal Error when reseting techbase, Inner Sphere not available." );
                        break;
                    default:
                        // set it to Inner Sphere
                        cmbTechBase.setSelectedIndex( 0 );
                        cmbTechBaseActionPerformed( null );
                        break;
                }
            }

            // since you can only ever change the rules level when not restricted,
            // we're not doing it here.  Pass in default values.
            //CurMech.GetLoadout().FlushIllegal( CurMech.GetEra(), 0, false );
            CurMech.GetLoadout().FlushIllegal();

            // refresh all the combo boxes.
            BuildChassisSelector();
            BuildEngineSelector();
            BuildGyroSelector();
            BuildCockpitSelector();
            BuildEnhancementSelector();
            BuildHeatsinkSelector();
            BuildJumpJetSelector();
            BuildArmorSelector();
            FixWalkMPSpinner();
            FixJJSpinnerModel();
            RefreshEquipment();

            // now reset the combo boxes to the closest choices we previously selected
            LoadSelections();

            RecalcEngine();
            RecalcGyro();
            RecalcIntStruc();
            RecalcCockpit();
            CurMech.GetActuators().PlaceActuators();
            RecalcHeatSinks();
            RecalcJumpJets();
            RecalcEnhancements();
            RecalcArmor();
            RecalcEquipment();
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();
    }//GEN-LAST:event_cmbRulesLevelActionPerformed

    private void btnLockChassisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLockChassisActionPerformed
        // currently testing right now.
        SaveOmniFluffInfo();
        String VariantName = "";

        // ensure there are no unplaced crits
        if( !CurMech.GetLoadout().GetQueue().isEmpty() ) {
            Media.Messager( this, "You must place all items first." );
            tbpMainTabPane.setSelectedComponent( pnlCriticals );
            return;
        }

        int choice = javax.swing.JOptionPane.showConfirmDialog( this,
            "Are you sure you want to lock the chassis?\nAll items in the base " +
            "loadout will be locked in location\nand most chassis specifications " +
            "will be locked.", "Lock Chassis?", javax.swing.JOptionPane.YES_NO_OPTION );
        if( choice == 1 ) {
            return;
        } else {
            // ask for a name for the first variant
            dlgOmniBase input = new dlgOmniBase( this, true );
            input.setTitle( "Name the first variant" );
            input.setLocationRelativeTo( this );
            input.setVisible( true );
            if( input.WasCanceled() ) {
                input.dispose();
                return;
            } else {
                VariantName = input.GetInput();
                input.dispose();
            }
        }

        // ensure we're not using the base loadout's name.
        if( common.Constants.BASELOADOUT_NAME.equals( VariantName ) ) {
            Media.Messager( this, "\"" + VariantName + "\" is reserved for the base loadout and cannot be used\nfor a new loadout.  Please choose another name." );
            return;
        }

        // make it an omni
        CurMech.SetOmnimech( VariantName );
        chkOmnimech.setEnabled( false );
        FixTransferHandlers();
        SetLoadoutArrays();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        LockGUIForOmni();
        RefreshOmniVariants();
        RefreshOmniChoices();
        SolidifyJJManufacturer();
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_btnLockChassisActionPerformed

    private void chkOmnimechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOmnimechActionPerformed
        if( chkOmnimech.isSelected() ) {
            btnLockChassis.setEnabled( true );
        } else {
            btnLockChassis.setEnabled( false );
        }
    }//GEN-LAST:event_chkOmnimechActionPerformed

    private void btnAddVariantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddVariantActionPerformed
        SaveOmniFluffInfo();
        String VariantName = "";

        // get the variant name
        dlgOmniBase input = new dlgOmniBase( this, true );
        input.setTitle( "Name this variant" );
        input.setLocationRelativeTo( this );
        input.setVisible( true );
        if( input.WasCanceled() ) {
            input.dispose();
            return;
        } else {
            VariantName = input.GetInput();
            input.dispose();
        }

        // now set the new loadout as the current
        try {
            CurMech.AddLoadout( VariantName );
        } catch( Exception e ) {
            // found an error when adding the loadout
            Media.Messager( this, e.getMessage() );
            return;
        }

        // fix the GUI
        LoadOmniFluffInfo();
        FixTransferHandlers();
        SetLoadoutArrays();
        SetWeaponChoosers();
        BuildJumpJetSelector();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        RefreshOmniVariants();
        RefreshOmniChoices();
        SolidifyJJManufacturer();
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_btnAddVariantActionPerformed

    private void mnuOpenActionPerformed(java.awt.event.ActionEvent evt) {                                        
        //dlgOpen dOpen = new dlgOpen(this, true);
        if( CurMech.HasChanged() ) {
            int choice = javax.swing.JOptionPane.showConfirmDialog( this,
                "The current 'Mech has changed.\nDo you want to discard those changes?", "Discard Changes?", javax.swing.JOptionPane.YES_NO_OPTION );
            if( choice == 1 ) { return; }
        }
        dOpen.Requestor = dlgOpen.SSW;
        dOpen.setLocationRelativeTo(null);

        dOpen.setSize( 1024, 600 );
        dOpen.setVisible(true);
    }

    private void btnDeleteVariantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteVariantActionPerformed
        // see if the user actually wants to delete the variant
        int choice = javax.swing.JOptionPane.showConfirmDialog( this,
            "Are you sure you want to delete this variant?", "Delete Variant?", javax.swing.JOptionPane.YES_NO_OPTION );
        if( choice == 1 ) {
            return;
        } else {
            if( CurMech.GetLoadout().GetName().equals( common.Constants.BASELOADOUT_NAME ) ) {
                Media.Messager( this, "You cannot remove the base chassis." );
                return;
            }
        }

        // delete the variant
        CurMech.RemoveLoadout( CurMech.GetLoadout().GetName() );

        // refresh all the displays
        LoadOmniFluffInfo();
        RefreshOmniVariants();
        FixTransferHandlers();
        SetLoadoutArrays();
        SetWeaponChoosers();
        BuildJumpJetSelector();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        RefreshOmniChoices();
        SolidifyJJManufacturer();
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_btnDeleteVariantActionPerformed

    private void cmbOmniVariantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbOmniVariantActionPerformed
        SaveOmniFluffInfo();
        String variant = (String) cmbOmniVariant.getSelectedItem();
        boolean changed = CurMech.HasChanged();

        CurMech.SetCurLoadout( variant );

        // now fix the GUI
        LoadOmniFluffInfo();
        FixTransferHandlers();
        SetLoadoutArrays();
        SetWeaponChoosers();
        BuildJumpJetSelector();
        cmbJumpJetType.setSelectedItem( CurMech.GetJumpJets().LookupName() );
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        RefreshOmniVariants();
        RefreshEquipment();
        RefreshOmniChoices();
        RefreshSummary();
        RefreshInfoPane();

        // this prevents the program from setting the changed tag if we simply
        // open an omnimech for browsing.
        CurMech.SetChanged( changed );
    }//GEN-LAST:event_cmbOmniVariantActionPerformed

	private void mnuPostS7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPostS7ActionPerformed
	    // attempts to post the mech to Solaris7.com
    	// must do all the normal actions for HTML export, then attempt to post
	    // right now we'll just show the screen so we can see it
        // exports the mech to HTML format

        //Save any changes to the Mech before posting...
        QuickSave();

	    String CurLoadout = "";
	    if( CurMech.IsOmnimech() ) {
	        CurLoadout = CurMech.GetLoadout().GetName();
	    }

	    // Solidify the mech first.
	    SolidifyMech();

	    if( ! VerifyMech( evt ) ) {
	        return;
	    }

	    dlgPostToSolaris7 PostS7 = new dlgPostToSolaris7( this, true );
        PostS7.setLocationRelativeTo( this );
	    PostS7.setVisible( true );

	    // lastly, if this is an omnimech, reset the display to the last loadout
	    cmbOmniVariant.setSelectedItem( CurLoadout );
	    cmbOmniVariantActionPerformed( evt );
	}//GEN-LAST:event_mnuPostS7ActionPerformed

	private void mnuClearUserDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuClearUserDataActionPerformed
        int choice = javax.swing.JOptionPane.showConfirmDialog( this,
            "This will remove all Solaris 7 user data.\nAre you sure you want to continue?", "Clear User Data?", javax.swing.JOptionPane.YES_NO_OPTION );
        if( choice == 1 ) {
            return;
        } else {
            Prefs.put( "S7Callsign", "" );
            Prefs.put( "S7Password", "" );
            Prefs.put( "S7UserID", "" );
        }
	}//GEN-LAST:event_mnuClearUserDataActionPerformed

	private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveActionPerformed
        // Solidify the mech first.
        setCursor( Hourglass );

        File savemech = GetSaveFile( "ssw", Prefs.get( "LastOpenDirectory", "" ), true, false );
        if( savemech == null ) {
            setCursor( NormalCursor );
            return;
        }

        //Since we are saving to a new file update the stored prefs
        try {
            Prefs.put("LastOpenDirectory", savemech.getCanonicalPath().replace(savemech.getName(), ""));
            Prefs.put("LastOpenFile", savemech.getName());
            Prefs.put("Currentfile", savemech.getCanonicalPath());
        } catch (IOException e) {
            Media.Messager( this, "There was a problem with the file:\n" + e.getMessage() );
            setCursor( NormalCursor );
            return;
        }

        // exports the mech to XML format
        String CurLoadout = "";
        if( CurMech.IsOmnimech() ) {
            CurLoadout = CurMech.GetLoadout().GetName();
            SaveOmniFluffInfo();
        }

        // save the mech to XML in the current location
        MechWriter XMLw = new MechWriter( CurMech );
        try {
            String file = savemech.getCanonicalPath();
            String ext = Utils.getExtension( savemech );
            if( ext == null || ext.equals( "" ) ) {
                file += ".ssw";
            } else {
                if( ! ext.equals( "ssw" ) ) {
                    file.replace( "." + ext, ".ssw" );
                }
            }
            XMLw.WriteXML( file );

            // if there were no problems, let the user know how it went
            if (evt != null && evt.getActionCommand().equals("Save Mech")) {
                Media.Messager( this, "Mech saved successfully:\n" + file );
            }
        } catch( IOException e ) {
            Media.Messager( this, "There was a problem writing the file:\n" + e.getMessage() );
            setCursor( NormalCursor );
            return;
        }

        // lastly, if this is an omnimech, reset the display to the last loadout
        if( CurMech.IsOmnimech() ) {
            SetSource = false;
            cmbOmniVariant.setSelectedItem( CurLoadout );
            cmbOmniVariantActionPerformed( evt );
            SetSource = true;
        }

        setCursor( NormalCursor );
        setTitle( SSWConstants.AppName + " " + SSWConstants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
        CurMech.SetChanged( false );
	}//GEN-LAST:event_mnuSaveActionPerformed

private void mnuLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLoadActionPerformed
    if( CurMech.HasChanged() ) {
        int choice = javax.swing.JOptionPane.showConfirmDialog( this,
            "The current 'Mech has changed.\nDo you want to discard those changes?", "Discard Changes?", javax.swing.JOptionPane.YES_NO_OPTION );
        if( choice == 1 ) { return; }
    }
    // Get the mech we're loading
    Mech m = LoadMech();
    if (m == null){
        return;
    }
    CurMech = m;
    LoadMechIntoGUI();
    CurMech.SetChanged( false );
}//GEN-LAST:event_mnuLoadActionPerformed

private void mnuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveAsActionPerformed
    setCursor( Hourglass );
    File savemech = GetSaveFile( "ssw", Prefs.get( "LastOpenDirectory", "" ), false, false );
    if( savemech == null ) {
        setCursor( NormalCursor );
        return;
    }

    // exports the mech to XML format
    String CurLoadout = "";
    if( CurMech.IsOmnimech() ) {
        CurLoadout = CurMech.GetLoadout().GetName();
    }

    // since we're doing a Save As...  we'll clear the S7 ID so that you can
    // post a variant without creating an entirely new 'Mech
    CurMech.SetSolaris7ID( "0" );

    // save the mech to XML in the current location
    MechWriter XMLw = new MechWriter( CurMech );
    try {
        String file = savemech.getCanonicalPath();
        String ext = Utils.getExtension( savemech );
        if( ext == null || ext.equals( "" ) ) {
            file += ".ssw";
        } else {
            if( ! ext.equals( "ssw" ) ) {
                file.replace( "." + ext, ".ssw" );
            }
        }
        XMLw.WriteXML( file );
        // if there were no problems, let the user know how it went
        Media.Messager( this, "Mech saved successfully:\n" + file );
    } catch( IOException e ) {
        Media.Messager( this, "There was a problem writing the file:\n" + e.getMessage() );
        setCursor( NormalCursor );
        return;
    }

    //Since we are saving to a new file update the stored prefs
    try {
        Prefs.put("LastOpenDirectory", savemech.getCanonicalPath().replace(savemech.getName(), ""));
        Prefs.put("LastOpenFile", savemech.getName());
    } catch (IOException e) {
        Media.Messager( this, "There was a problem with the file:\n" + e.getMessage() );
        setCursor( NormalCursor );
        return;
    }

    // lastly, if this is an omnimech, reset the display to the last loadout
    if( CurMech.IsOmnimech() ) {
        cmbOmniVariant.setSelectedItem( CurLoadout );
        cmbOmniVariantActionPerformed( evt );
    }
    setTitle( SSWConstants.AppName + " " + SSWConstants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
    CurMech.SetChanged( false );
    setCursor( NormalCursor );
}//GEN-LAST:event_mnuSaveAsActionPerformed

private void lstSelectedEquipmentValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstSelectedEquipmentValueChanged
        if( lstSelectedEquipment.getSelectedIndex() < 0 ) { return; }
        abPlaceable p;
        try {
            p = (abPlaceable) CurMech.GetLoadout().GetNonCore().get( lstSelectedEquipment.getSelectedIndex() );
        } catch( Exception e ) {
            return;
        }
        ShowInfoOn( p );
}//GEN-LAST:event_lstSelectedEquipmentValueChanged

private void btnSelectiveAllocateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectiveAllocateActionPerformed
        SelectiveAllocate();
}//GEN-LAST:event_btnSelectiveAllocateActionPerformed

private void btnAutoAllocateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutoAllocateActionPerformed
        AutoAllocate();
}//GEN-LAST:event_btnAutoAllocateActionPerformed

private void lstCritsToPlaceValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstCritsToPlaceValueChanged
        int Index = lstCritsToPlace.getSelectedIndex();
        if( Index < 0 ) {
            btnAutoAllocate.setEnabled( false );
            btnSelectiveAllocate.setEnabled( false );
            btnRemoveItemCrits.setEnabled( false );
            return;
        }
        CurItem = CurMech.GetLoadout().GetFromQueueByIndex( Index );
        if( CurItem.Contiguous() ) {
            btnAutoAllocate.setEnabled( false );
            btnSelectiveAllocate.setEnabled( false );
            if( ! CurItem.CoreComponent() ) {
                btnRemoveItemCrits.setEnabled( true );
            } else {
                btnRemoveItemCrits.setEnabled( false );
            }
        } else {
            btnAutoAllocate.setEnabled( true );
            btnSelectiveAllocate.setEnabled( true );
            btnRemoveItemCrits.setEnabled( false );
        }
}//GEN-LAST:event_lstCritsToPlaceValueChanged

private void mnuCostBVBreakdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCostBVBreakdownActionPerformed
    SolidifyMech();
    dlgCostBVBreakdown costbv = new dlgCostBVBreakdown( this, true, CurMech );
    costbv.setLocationRelativeTo( this );
    costbv.setVisible( true );
}//GEN-LAST:event_mnuCostBVBreakdownActionPerformed

private void lstChooseArtilleryValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstChooseArtilleryValueChanged
        if( lstChooseArtillery.getSelectedIndex() < 0 ) { return; }
        if( ( ! ( Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()] instanceof RangedWeapon ) && ( ! ( Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()] instanceof VehicularGrenadeLauncher ) ) ) ) { return; }
        abPlaceable p = (abPlaceable) Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()];
        ShowInfoOn( p );
}//GEN-LAST:event_lstChooseArtilleryValueChanged

private void mnuPrintCurrentMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintCurrentMechActionPerformed
    // Solidify the mech first.
    SolidifyMech();

    if( VerifyMech( new ActionEvent( this, 1234567890, null ) ) ) {
        PrintMech( CurMech );
    }
}//GEN-LAST:event_mnuPrintCurrentMechActionPerformed

private void mnuPrintSavedMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintSavedMechActionPerformed
    Mech m = LoadMech();
    if (!(m==null))
        PrintMech(m);
}//GEN-LAST:event_mnuPrintSavedMechActionPerformed

private void btnEfficientArmorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEfficientArmorActionPerformed
    // this routine does an "efficient" armor allocation.  It will find the max
    // armor, then find the wasted tonnage and remove it if it's enough to
    // warrant that.  If the wasted AV is greater than 3 points, the extra half-
    // ton is removed.
    int MaxArmor = (int) ( CurMech.GetArmor().GetMaxArmor() );
    int MaxLessArmor = (int) ( ( CurMech.GetArmor().GetMaxTonnage() - 0.5f ) * 16 * CurMech.GetArmor().GetAVMult() );

    if( MaxArmor - MaxLessArmor > ( 5 * CurMech.GetArmor().GetAVMult() ) ) {
        // use the full amount
        ArmorTons.SetArmorTonnage( CurMech.GetArmor().GetMaxTonnage() );
    } else {
        // use the lesser amount
        ArmorTons.SetArmorTonnage( CurMech.GetArmor().GetMaxTonnage() - 0.5f );
    }

   // ArmorTons.SetArmorTonnage( result );
    try {
        CurMech.Visit( ArmorTons );
    } catch( Exception e ) {
        // this should never throw an exception, but log it anyway
        System.err.println( e.getMessage() );
        e.printStackTrace();
    }

    // if we fix the spinner models, they should refresh the screen
    FixArmorSpinners();

    // of course, we'll also have to set the head spinner manually.
    javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHDArmor.getModel();
    n.setValue( (Object) CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_HD ) );

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_btnEfficientArmorActionPerformed

private void chkNullSigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNullSigActionPerformed
    // is the system already installed?
    if( chkNullSig.isSelected() == CurMech.HasNullSig() ) { return; }
    try {
        if( chkNullSig.isSelected() ) {
            CurMech.SetNullSig( true );
        } else {
            CurMech.SetNullSig( false );
        }
    } catch( Exception e ) {
        Media.Messager( this, e.getMessage() );
        // ensure it's not checked when it shouldn't be
        chkNullSig.setSelected( CurMech.HasNullSig() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkNullSigActionPerformed

private void chkCLPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCLPSActionPerformed
    // is the system already installed?
    if( chkCLPS.isSelected() == CurMech.HasChameleon() ) { return; }
    try {
        if( chkCLPS.isSelected() ) {
            CurMech.SetChameleon( true );
        } else {
            CurMech.SetChameleon( false );
        }
    } catch( Exception e ) {
        Media.Messager( this, e.getMessage() );
        // ensure it's not checked when it shouldn't be
        chkCLPS.setSelected( CurMech.HasChameleon() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkCLPSActionPerformed

private void chkBSPFDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBSPFDActionPerformed
    // is the system already installed?
    if( chkBSPFD.isSelected() == CurMech.HasBlueShield() ) { return; }
    try {
        if( chkBSPFD.isSelected() ) {
            CurMech.SetBlueShield( true );
        } else {
            CurMech.SetBlueShield( false );
        }
    } catch( Exception e ) {
        Media.Messager( this, e.getMessage() );
        // ensure it's not checked when it shouldn't be
        chkBSPFD.setSelected( CurMech.HasBlueShield() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkBSPFDActionPerformed

private void chkVoidSigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVoidSigActionPerformed
    // is the system already installed?
    if( chkVoidSig.isSelected() == CurMech.HasVoidSig() ) { return; }
    try {
        if( chkVoidSig.isSelected() ) {
            CurMech.SetVoidSig( true );
            if( ! AddECM() ) {
                CurMech.SetVoidSig( false );
                throw new Exception( "No ECM Suite was available to support the Void Signature System!\nUninstalling system." );
            }
        } else {
            CurMech.SetVoidSig( false );
        }
    } catch( Exception e ) {
        Media.Messager( this, e.getMessage() );
        // ensure it's not checked when it shouldn't be
        chkVoidSig.setSelected( CurMech.HasVoidSig() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkVoidSigActionPerformed

private void btnRemainingArmorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemainingArmorActionPerformed
        // see if we have a good number
    double freetons = CurMech.GetTonnage() - CurMech.GetCurrentTons() + CurMech.GetArmor().GetTonnage();

    if( freetons > CurMech.GetArmor().GetMaxTonnage() ) {
        freetons = CurMech.GetArmor().GetMaxTonnage();
    }

    ArmorTons.SetArmorTonnage( freetons );
    try {
        CurMech.Visit( ArmorTons );
    } catch( Exception e ) {
        // this should never throw an exception, but log it anyway
        System.err.println( e.getMessage() );
        e.printStackTrace();
    }

    // if we fix the spinner models, they should refresh the screen
    FixArmorSpinners();

    // of course, we'll also have to set the head spinner manually.
    javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHDArmor.getModel();
    n.setValue( (Object) CurMech.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_HD ) );

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_btnRemainingArmorActionPerformed

private void cmbSCLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSCLocActionPerformed
    int curLoc = CurMech.GetLoadout().Find( CurMech.GetLoadout().GetSupercharger() );
    int DesiredLoc = FileCommon.DecodeLocation( (String) cmbSCLoc.getSelectedItem() );
    if( curLoc == DesiredLoc ) { return; }
    if( CurMech.GetLoadout().HasSupercharger() ) {
        try {
            CurMech.GetLoadout().SetSupercharger( true, DesiredLoc, -1 );
        } catch( Exception e ) {
            Media.Messager( this, e.getMessage() );
            chkSupercharger.setSelected( false );
            // now refresh the information panes
            RefreshSummary();
            RefreshInfoPane();
            return;
        }
    }
    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_cmbSCLocActionPerformed

private void chkSuperchargerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSuperchargerActionPerformed
    if( CurMech.GetLoadout().HasSupercharger() == chkSupercharger.isSelected() ) {
        return;
    }
    try {
        CurMech.GetLoadout().SetSupercharger( chkSupercharger.isSelected(), FileCommon.DecodeLocation( (String) cmbSCLoc.getSelectedItem() ), -1 );
    } catch( Exception e ) {
        Media.Messager( this, e.getMessage() );
        try {
            CurMech.GetLoadout().SetSupercharger( false , 0, -1 );
        } catch( Exception x ) {
            // how the hell did we get an error removing it?
            Media.Messager( this, x.getMessage() );
            // now refresh the information panes
            RefreshSummary();
            RefreshInfoPane();
        }
        chkSupercharger.setSelected( false );
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        return;
    }
    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkSuperchargerActionPerformed

public Mech LoadMech (){
    Mech m = null;

    File tempFile = new File( Prefs.get( "LastOpenDirectory", "" ) );
    JFileChooser fc = new JFileChooser();
    fc.addChoosableFileFilter( new javax.swing.filechooser.FileFilter() {
        public boolean accept( File f ) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = Utils.getExtension( f );
            if ( extension != null ) {
                if ( extension.equals( "ssw" ) ) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        //The description of this filter
        public String getDescription() {
            return "*.ssw";
        }
    } );
    fc.setAcceptAllFileFilterUsed( false );
    fc.setCurrentDirectory( tempFile );
    int returnVal = fc.showDialog( this, "Load Mech" );
    if( returnVal != JFileChooser.APPROVE_OPTION ) { return m; }
    File loadmech = fc.getSelectedFile();
    String filename = "";
    try {
        filename = loadmech.getCanonicalPath();
        Prefs.put("LastOpenDirectory", loadmech.getCanonicalPath().replace(loadmech.getName(), ""));
        Prefs.put("LastOpenFile", loadmech.getName());
        Prefs.put("Currentfile", loadmech.getCanonicalPath());
    } catch( Exception e ) {
        Media.Messager( this, "There was a problem opening the file:\n" + e.getMessage() );
        return m;
    }

    try {
        MechReader XMLr = new MechReader();
        m = XMLr.ReadMech( filename, data );
        if( XMLr.GetMessages().length() > 0 ) {
            dlgTextExport Message = new dlgTextExport( this, true, XMLr.GetMessages() );
            Message.setLocationRelativeTo( this );
            Message.setVisible( true );
        }
    } catch( Exception e ) {
        // had a problem loading the mech.  let the user know.
        if( e.getMessage() == null ) {
            Media.Messager( this, "An unknown error has occured.  The log file has been updated." );
            e.printStackTrace();
        } else {
            Media.Messager( this, e.getMessage() );
            e.printStackTrace();
        }
        return m;
    }

    return m;
}

private void LoadMechFromFile( String filename )
{
    Mech m = null;
    if (! filename.isEmpty() ) {
        try {
            MechReader XMLr = new MechReader();
            m = XMLr.ReadMech( filename, data );
            CurMech = m;
            LoadMechIntoGUI();
            Prefs.put("Currentfile", filename);
        } catch( Exception e ) {
            // had a problem loading the mech.  let the user know.
            Media.Messager( e.getMessage() );
        }
    }
}

public void LoadMechIntoGUI() {
    // added for special situations
    Load = true;

    // Put it in the gui.
    UnlockGUIFromOmni();
    BuildMechTypeSelector();
    if( CurMech.IsQuad() ) {
        cmbMotiveType.setSelectedIndex( 1 );
        ((javax.swing.border.TitledBorder) pnlLAArmorBox.getBorder()).setTitle( "FLL" );
        ((javax.swing.border.TitledBorder) pnlRAArmorBox.getBorder()).setTitle( "FRL" );
        ((javax.swing.border.TitledBorder) pnlLLArmorBox.getBorder()).setTitle( "RLL" );
        ((javax.swing.border.TitledBorder) pnlRLArmorBox.getBorder()).setTitle( "RRL" );
        scrRACrits.setPreferredSize( new java.awt.Dimension( 105, 87 ) );
        scrLACrits.setPreferredSize( new java.awt.Dimension( 105, 87 ) );
    } else {
        cmbMotiveType.setSelectedIndex( 0 );
        ((javax.swing.border.TitledBorder) pnlLAArmorBox.getBorder()).setTitle( "LA" );
        ((javax.swing.border.TitledBorder) pnlRAArmorBox.getBorder()).setTitle( "RA" );
        ((javax.swing.border.TitledBorder) pnlLLArmorBox.getBorder()).setTitle( "LL" );
        ((javax.swing.border.TitledBorder) pnlRLArmorBox.getBorder()).setTitle( "RL" );
        scrRACrits.setPreferredSize( new java.awt.Dimension( 105, 170 ) );
        scrLACrits.setPreferredSize( new java.awt.Dimension( 105, 170 ) );
    }
    if( CurMech.IsIndustrialmech() ) {
        if( CurMech.IsPrimitive() ) {
            cmbMechType.setSelectedIndex( 3 );
        } else {
            cmbMechType.setSelectedIndex( 1 );
        }
    } else {
        if( CurMech.IsPrimitive() ) {
            cmbMechType.setSelectedIndex( 2 );
        } else {
            cmbMechType.setSelectedIndex( 0 );
        }
    }
    chkYearRestrict.setSelected( CurMech.IsYearRestricted() );
    txtProdYear.setText( "" + CurMech.GetYear() );
    cmbMechEra.setEnabled( true );
    cmbTechBase.setEnabled( true );
    txtProdYear.setEnabled( true );
    switch( CurMech.GetEra() ) {
        case AvailableCode.ERA_STAR_LEAGUE:
            lblEraYears.setText( "2443 ~ 2800" );
            break;
        case AvailableCode.ERA_SUCCESSION:
            lblEraYears.setText( "2801 ~ 3050" );
            break;
        case AvailableCode.ERA_CLAN_INVASION:
            lblEraYears.setText( "3051 ~ 3131" );
            break;
        case AvailableCode.ERA_DARK_AGES:
            lblEraYears.setText( "3132 on" );
            break;
        case AvailableCode.ERA_ALL:
            lblEraYears.setText( "Any" );
            break;
    }

    cmbRulesLevel.setSelectedIndex( CurMech.GetRulesLevel() );
    cmbMechEra.setSelectedIndex( CurMech.GetEra() );
    cmbProductionEra.setSelectedIndex( CurMech.GetProductionEra() );

    // now that we're done with the special stuff...
    Load = false;

    if( chkYearRestrict.isSelected() ) {
        cmbMechEra.setEnabled( false );
        cmbTechBase.setEnabled( false );
        txtProdYear.setEnabled( false );
    }
    txtMechName.setText( CurMech.GetName() );
    txtMechModel.setText( CurMech.GetModel() );

    if( CurMech.IsOmnimech() ) {
        LockGUIForOmni();
        RefreshOmniVariants();
        RefreshOmniChoices();
    }

    BuildTechBaseSelector();
    cmbTechBase.setSelectedIndex( CurMech.GetLoadout().GetTechBase() );

    FixTransferHandlers();

    ResetTonnageSelector();
    BuildChassisSelector();
    BuildEngineSelector();
    BuildGyroSelector();
    BuildCockpitSelector();
    BuildEnhancementSelector();
    BuildHeatsinkSelector();
    BuildJumpJetSelector();
    BuildArmorSelector();
    cmbInternalType.setSelectedItem( BuildLookupName( CurMech.GetIntStruc().GetCurrentState() ) );
    cmbEngineType.setSelectedItem( BuildLookupName( CurMech.GetEngine().GetCurrentState() ) );
    cmbGyroType.setSelectedItem( BuildLookupName( CurMech.GetGyro().GetCurrentState() ) );
    cmbCockpitType.setSelectedItem( CurMech.GetCockpit().LookupName() );
    cmbPhysEnhance.setSelectedItem( BuildLookupName( CurMech.GetPhysEnhance().GetCurrentState() ) );
    cmbHeatSinkType.setSelectedItem( BuildLookupName( CurMech.GetHeatSinks().GetCurrentState() ) );
    cmbJumpJetType.setSelectedItem( CurMech.GetJumpJets().LookupName() );
    cmbArmorType.setSelectedItem( BuildLookupName( CurMech.GetArmor().GetCurrentState() ) );
    SetPatchworkArmor();
    FixWalkMPSpinner();
    FixHeatSinkSpinnerModel();
    FixJJSpinnerModel();
    RefreshInternalPoints();
    FixArmorSpinners();
    data.Rebuild( CurMech );
    RefreshEquipment();
    chkCTCASE.setSelected( CurMech.HasCTCase() );
    chkLTCASE.setSelected( CurMech.HasLTCase() );
    chkRTCASE.setSelected( CurMech.HasRTCase() );
    chkUseTC.setSelected( CurMech.UsingTC() );
    chkClanCASE.setSelected( CurMech.GetLoadout().IsUsingClanCASE() );
    chkNullSig.setSelected( CurMech.HasNullSig() );
    chkVoidSig.setSelected( CurMech.HasVoidSig() );
    chkBSPFD.setSelected( CurMech.HasBlueShield() );
    chkCLPS.setSelected( CurMech.HasChameleon() );
    chkEnviroSealing.setSelected( CurMech.HasEnviroSealing() );
    chkEjectionSeat.setSelected( CurMech.HasEjectionSeat() );
    chkCommandConsole.setSelected( CurMech.HasCommandConsole() );
    chkFHES.setSelected( CurMech.HasFHES() );
    chkTracks.setSelected( CurMech.HasTracks() );
    chkRAAES.setSelected( CurMech.HasRAAES() );
    chkLAAES.setSelected( CurMech.HasLAAES() );
    chkLegAES.setSelected( CurMech.HasLegAES() );
    SetLoadoutArrays();
    RefreshSummary();
    RefreshInfoPane();
    SetWeaponChoosers();
    ResetAmmo();

    // load the fluff image.
    Media media = new Media();
    media.blankLogo(lblFluffImage);
    media.setLogo(lblFluffImage, media.DetermineMatchingImage(CurMech.GetName(), CurMech.GetModel(), CurMech.GetSSWImage()));

    Overview.SetText( CurMech.GetOverview() );
    Capabilities.SetText( CurMech.GetCapabilities() );
    History.SetText( CurMech.GetHistory() );
    Deployment.SetText( CurMech.GetDeployment() );
    Variants.SetText( CurMech.GetVariants() );
    Notables.SetText( CurMech.GetNotables() );
    Additional.SetText( CurMech.GetAdditional() );
    txtManufacturer.setText( CurMech.GetCompany() );
    txtManufacturerLocation.setText( CurMech.GetLocation() );
    txtEngineManufacturer.setText( CurMech.GetEngineManufacturer() );
    txtArmorModel.setText( CurMech.GetArmorModel() );
    txtChassisModel.setText( CurMech.GetChassisModel() );
    if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
        txtJJModel.setEnabled( true );
    }
    txtSource.setText( CurMech.GetSource() );

    // omnimechs may have jump jets in one loadout and not another.
    txtJJModel.setText( CurMech.GetJJModel() );
    txtCommSystem.setText( CurMech.GetCommSystem() );
    txtTNTSystem.setText( CurMech.GetTandTSystem() );

    // see if we should enable the Power Amplifier display
    if( CurMech.GetEngine().IsNuclear() ) {
        lblSumPAmps.setVisible( false );
        txtSumPAmpsTon.setVisible( false );
        txtSumPAmpsACode.setVisible( false );
    } else {
        lblSumPAmps.setVisible( true );
        txtSumPAmpsTon.setVisible( true );
        txtSumPAmpsACode.setVisible( true );
    }

    setTitle( SSWConstants.AppName + " " + SSWConstants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
}

private void mnuExportClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportClipboardActionPerformed
    // takes the text export and copies it to thesystem clipboard.
        String CurLoadout = "";
        String output = "";

        if( CurMech.IsOmnimech() ) {
            CurLoadout = CurMech.GetLoadout().GetName();
        }

        // Solidify the mech first.
        SolidifyMech();

        if( ! VerifyMech( evt ) ) {
            return;
        }

        TXTWriter txtw = new TXTWriter( CurMech );
        output = txtw.GetTextExport();
        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( output );

        // lastly, if this is an omnimech, reset the display to the last loadout
        if( CurMech.IsOmnimech() ) {
            cmbOmniVariant.setSelectedItem( CurLoadout );
            cmbOmniVariantActionPerformed( evt );
        }
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( export, this );
}//GEN-LAST:event_mnuExportClipboardActionPerformed

private void btnSaveIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveIconActionPerformed
    mnuSaveActionPerformed(evt);
}//GEN-LAST:event_btnSaveIconActionPerformed

private void btnPrintIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintIconActionPerformed
    mnuPrintCurrentMechActionPerformed(evt);
}//GEN-LAST:event_btnPrintIconActionPerformed

private void btnNewIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewIconActionPerformed
    mnuNewMechActionPerformed(evt);
}//GEN-LAST:event_btnNewIconActionPerformed

private void btnOptionsIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOptionsIconActionPerformed
    mnuOptionsActionPerformed(evt);
}//GEN-LAST:event_btnOptionsIconActionPerformed

private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
    //mnuLoadActionPerformed(evt);
    mnuOpenActionPerformed(evt);
}//GEN-LAST:event_btnOpenActionPerformed

private void mnuViewToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuViewToolbarActionPerformed
    setViewToolbar(mnuViewToolbar.getState());
}//GEN-LAST:event_mnuViewToolbarActionPerformed

private void btnExportHTMLIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportHTMLIconActionPerformed
    mnuExportHTMLActionPerformed(evt);
}//GEN-LAST:event_btnExportHTMLIconActionPerformed

private void btnExportTextIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportTextIconActionPerformed
    mnuExportTXTActionPerformed(evt);
}//GEN-LAST:event_btnExportTextIconActionPerformed

private void btnExportMTFIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportMTFIconActionPerformed
    mnuExportMTFActionPerformed(evt);
}//GEN-LAST:event_btnExportMTFIconActionPerformed

private void btnExportClipboardIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportClipboardIconActionPerformed
    mnuExportClipboardActionPerformed(evt);
}//GEN-LAST:event_btnExportClipboardIconActionPerformed

private void btnPostToS7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPostToS7ActionPerformed
    mnuPostS7ActionPerformed(evt);
}//GEN-LAST:event_btnPostToS7ActionPerformed

private void btnRemoveItemCritsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveItemCritsActionPerformed
    int Index = lstCritsToPlace.getSelectedIndex();
    if( Index < 0 ) {
        btnAutoAllocate.setEnabled( false );
        btnSelectiveAllocate.setEnabled( false );
        btnRemoveItemCrits.setEnabled( false );
        return;
    }
    CurItem = CurMech.GetLoadout().GetFromQueueByIndex( Index );
    RemoveItemCritTab();
}//GEN-LAST:event_btnRemoveItemCritsActionPerformed

private void chkCTCASE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCTCASE2ActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasCTCASEII() ) {
            chkCTCASE2.setSelected( true );
            return;
        }
        if( CurMech.GetLoadout().HasCTCASEII() == chkCTCASE2.isSelected() ) { return; }
        if( chkCTCASE2.isSelected() ) {
            try {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurMech.GetLoadout().SetCTCASEII( true, -1, tech.IsClan() );
                } else if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    CurMech.GetLoadout().SetCTCASEII( true, -1, true );
                } else {
                    CurMech.GetLoadout().SetCTCASEII( true, -1, false );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkCTCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetCTCASEII( false, -1, false );
            } catch( Exception e ) {
                // removing CASE II should never return an exception.  log it.
                System.err.println( "Received an error removing CT CASE II:" );
                System.err.println( e.getStackTrace() );
            }
        }
        RefreshInfoPane();
}//GEN-LAST:event_chkCTCASE2ActionPerformed

private void chkRACASE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRACASE2ActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasRACASEII() ) {
            chkRACASE2.setSelected( true );
            return;
        }
        if( CurMech.GetLoadout().HasRACASEII() == chkRACASE2.isSelected() ) { return; }
        if( chkRACASE2.isSelected() ) {
            try {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurMech.GetLoadout().SetRACASEII( true, -1, tech.IsClan() );
                } else if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    CurMech.GetLoadout().SetRACASEII( true, -1, true );
                } else {
                    CurMech.GetLoadout().SetRACASEII( true, -1, false );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkRACASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetRACASEII( false, -1, false );
            } catch( Exception e ) {
                // removing CASE II should never return an exception.  log it.
                System.err.println( "Received an error removing RA CASE II:" );
                System.err.println( e.getStackTrace() );
            }
        }
        RefreshInfoPane();
}//GEN-LAST:event_chkRACASE2ActionPerformed

private void chkRTCASE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRTCASE2ActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasRTCASEII() ) {
            chkRTCASE2.setSelected( true );
            return;
        }
        if( CurMech.GetLoadout().HasRTCASEII() == chkRTCASE2.isSelected() ) { return; }
        if( chkRTCASE2.isSelected() ) {
            try {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurMech.GetLoadout().SetRTCASEII( true, -1, tech.IsClan() );
                } else if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    CurMech.GetLoadout().SetRTCASEII( true, -1, true );
                } else {
                    CurMech.GetLoadout().SetRTCASEII( true, -1, false );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkRTCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetRTCASEII( false, -1, false );
            } catch( Exception e ) {
                // removing CASE II should never return an exception.  log it.
                System.err.println( "Received an error removing RT CASE II:" );
                System.err.println( e.getStackTrace() );
            }
        }
        RefreshInfoPane();
}//GEN-LAST:event_chkRTCASE2ActionPerformed

private void chkRLCASE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRLCASE2ActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasRLCASEII() ) {
            chkRLCASE2.setSelected( true );
            return;
        }
        if( CurMech.GetLoadout().HasRLCASEII() == chkRLCASE2.isSelected() ) { return; }
        if( chkRLCASE2.isSelected() ) {
            try {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurMech.GetLoadout().SetRLCASEII( true, -1, tech.IsClan() );
                } else if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    CurMech.GetLoadout().SetRLCASEII( true, -1, true );
                } else {
                    CurMech.GetLoadout().SetRLCASEII( true, -1, false );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkRLCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetRLCASEII( false, -1, false );
            } catch( Exception e ) {
                // removing CASE II should never return an exception.  log it.
                System.err.println( "Received an error removing RL CASE II:" );
                System.err.println( e.getStackTrace() );
            }
        }
        RefreshInfoPane();
}//GEN-LAST:event_chkRLCASE2ActionPerformed

private void chkHDCASE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHDCASE2ActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasHDCASEII() ) {
            chkHDCASE2.setSelected( true );
            return;
        }
        if( CurMech.GetLoadout().HasHDCASEII() == chkHDCASE2.isSelected() ) { return; }
        if( chkHDCASE2.isSelected() ) {
            try {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurMech.GetLoadout().SetHDCASEII( true, -1, tech.IsClan() );
                } else if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    CurMech.GetLoadout().SetHDCASEII( true, -1, true );
                } else {
                    CurMech.GetLoadout().SetHDCASEII( true, -1, false );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkHDCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetHDCASEII( false, -1, false );
            } catch( Exception e ) {
                // removing CASE II should never return an exception.  log it.
                System.err.println( "Received an error removing HD CASE II:" );
                System.err.println( e.getStackTrace() );
            }
        }
        RefreshInfoPane();
}//GEN-LAST:event_chkHDCASE2ActionPerformed

private void chkLTCASE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLTCASE2ActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasLTCASEII() ) {
            chkLTCASE2.setSelected( true );
            return;
        }
        if( CurMech.GetLoadout().HasLTCASEII() == chkLTCASE2.isSelected() ) { return; }
        if( chkLTCASE2.isSelected() ) {
            try {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurMech.GetLoadout().SetLTCASEII( true, -1, tech.IsClan() );
                } else if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    CurMech.GetLoadout().SetLTCASEII( true, -1, true );
                } else {
                    CurMech.GetLoadout().SetLTCASEII( true, -1, false );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkLTCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetLTCASEII( false, -1, false );
            } catch( Exception e ) {
                // removing CASE II should never return an exception.  log it.
                System.err.println( "Received an error removing LT CASE II:" );
                System.err.println( e.getStackTrace() );
            }
        }
        RefreshInfoPane();
}//GEN-LAST:event_chkLTCASE2ActionPerformed

private void chkLLCASE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLLCASE2ActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasLLCASEII() ) {
            chkLLCASE2.setSelected( true );
            return;
        }
        if( CurMech.GetLoadout().HasLLCASEII() == chkLLCASE2.isSelected() ) { return; }
        if( chkLLCASE2.isSelected() ) {
            try {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurMech.GetLoadout().SetLLCASEII( true, -1, tech.IsClan() );
                } else if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    CurMech.GetLoadout().SetLLCASEII( true, -1, true );
                } else {
                    CurMech.GetLoadout().SetLLCASEII( true, -1, false );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkLLCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetLLCASEII( false, -1, false );
            } catch( Exception e ) {
                // removing CASE II should never return an exception.  log it.
                System.err.println( "Received an error removing LL CASE II:" );
                System.err.println( e.getStackTrace() );
            }
        }
        RefreshInfoPane();
}//GEN-LAST:event_chkLLCASE2ActionPerformed

private void chkLACASE2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLACASE2ActionPerformed
        if( CurMech.IsOmnimech() && CurMech.GetBaseLoadout().HasLACASEII() ) {
            chkLACASE2.setSelected( true );
            return;
        }
        if( CurMech.GetLoadout().HasLACASEII() == chkLACASE2.isSelected() ) { return; }
        if( chkLACASE2.isSelected() ) {
            try {
                if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurMech.GetLoadout().SetLACASEII( true, -1, tech.IsClan() );
                } else if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    CurMech.GetLoadout().SetLACASEII( true, -1, true );
                } else {
                    CurMech.GetLoadout().SetLACASEII( true, -1, false );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkLACASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetLACASEII( false, -1, false );
            } catch( Exception e ) {
                // removing CASE II should never return an exception.  log it.
                System.err.println( "Received an error removing LA CASE II:" );
                System.err.println( e.getStackTrace() );
            }
        }
        RefreshInfoPane();
}//GEN-LAST:event_chkLACASE2ActionPerformed

private void mnuFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_mnuFileActionPerformed

private void mnuPrintBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintBatchActionPerformed
    if (BatchWindow == null) {BatchWindow = new dlgPrintBatchMechs (this, true);}
    BatchWindow.setLocationRelativeTo( this );
    BatchWindow.setVisible( true );
}//GEN-LAST:event_mnuPrintBatchActionPerformed

private void cmbMechTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMechTypeActionPerformed
    switch( cmbMechType.getSelectedIndex() ) {
        case 0:
            if( ! CurMech.IsIndustrialmech() &! CurMech.IsPrimitive() ) { return; }
            CurMech.SetModern();
            CurMech.SetBattlemech();
            break;
        case 1:
            if( CurMech.IsIndustrialmech() &! CurMech.IsPrimitive() ) { return; }
            CurMech.SetModern();
            CurMech.SetIndustrialmech();
            break;
        case 2:
            if( ! CurMech.IsIndustrialmech() && CurMech.IsPrimitive() ) { return; }
            CurMech.SetPrimitive();
            CurMech.SetBattlemech();
            break;
        case 3:
            if( CurMech.IsIndustrialmech() && CurMech.IsPrimitive() ) { return; }
            CurMech.SetPrimitive();
            CurMech.SetIndustrialmech();
            break;
    }

    // check the tonnage
        CheckTonnage( false );

        // set the loadout arrays
        SetLoadoutArrays();

        // fix the armor spinners
        FixArmorSpinners();

        // refresh all the combo boxes.
        SaveSelections();
        BuildChassisSelector();
        BuildEngineSelector();
        BuildGyroSelector();
        BuildCockpitSelector();
        BuildEnhancementSelector();
        BuildHeatsinkSelector();
        BuildJumpJetSelector();
        BuildArmorSelector();
        RefreshEquipment();
        CheckOmnimech();

        // now reset the combo boxes to the closest choices we previously selected
        LoadSelections();

        RecalcEngine();
        FixWalkMPSpinner();
        FixJJSpinnerModel();
        RecalcGyro();
        RecalcIntStruc();
        RecalcCockpit();
        CurMech.GetActuators().PlaceActuators();
        RecalcHeatSinks();
        RecalcJumpJets();
        RecalcEnhancements();
        RecalcArmor();
        RecalcEquipment();

        // since you can only ever change the era when not restricted, we're not
        // doing it here.  Pass in default values.
        CurMech.GetLoadout().FlushIllegal();
        //CurMech.GetLoadout().FlushIllegal( cmbMechEra.getSelectedIndex(), 0, false );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();
}//GEN-LAST:event_cmbMechTypeActionPerformed

private void chkEnviroSealingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEnviroSealingActionPerformed
    // is the system already installed?
    if( chkEnviroSealing.isSelected() == CurMech.HasEnviroSealing() ) { return; }
    try {
        if( chkEnviroSealing.isSelected() ) {
            CurMech.SetEnviroSealing( true );
        } else {
            CurMech.SetEnviroSealing( false );
        }
    } catch( Exception e ) {
        Media.Messager( this, e.getMessage() );
        // ensure it's not checked when it shouldn't be
        chkEnviroSealing.setSelected( CurMech.HasEnviroSealing() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_chkEnviroSealingActionPerformed

private void chkEjectionSeatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEjectionSeatActionPerformed
    if( chkEjectionSeat.isSelected() == CurMech.HasEjectionSeat() ) { return; }
    try {
        if( chkEjectionSeat.isSelected() ) {
            CurMech.SetEjectionSeat( true );
        } else {
            CurMech.SetEjectionSeat( false );
        }
    } catch( Exception e ) {
        // ensure it's not checked when it shouldn't be
        chkEjectionSeat.setSelected( CurMech.HasEjectionSeat() );
        Media.Messager( this, e.getMessage() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_chkEjectionSeatActionPerformed

private void mnuPrintPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintPreviewActionPerformed
    Printer printer = new Printer(this);
    printer.setCharts(Prefs.getBoolean("UseCharts", false));
    printer.setCanon(Prefs.getBoolean("UseCanonDots", false));
    printer.AddMech(CurMech);

    //PreviewDialog dlgPreview = new PreviewDialog(CurMech.GetFullName(), this, printer.Preview(), 1.0);
    dlgPreview preview = new dlgPreview(CurMech.GetFullName(), this, printer, printer.Preview(), 0.0);
    preview.setSize(1024, 768);
    preview.setLocationRelativeTo(null);
    preview.setVisible(true);
}//GEN-LAST:event_mnuPrintPreviewActionPerformed

private void btnPrintPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintPreviewActionPerformed
    mnuPrintPreviewActionPerformed(evt);
}//GEN-LAST:event_btnPrintPreviewActionPerformed

private void chkLegAESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLegAESActionPerformed
    if( chkLegAES.isSelected() == CurMech.HasLegAES() ) { return; }
    try {
        if( chkLegAES.isSelected() ) {
            CurMech.SetLegAES( true, null );
        } else {
            CurMech.SetLegAES( false, null );
        }
    } catch( Exception e ) {
        // ensure it's not checked when it shouldn't be
        chkLegAES.setSelected( CurMech.HasLegAES() );
        Media.Messager( this, e.getMessage() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkLegAESActionPerformed

private void chkLAAESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLAAESActionPerformed
    if( chkLAAES.isSelected() == CurMech.HasLAAES() ) { return; }
    try {
        if( chkLAAES.isSelected() ) {
            CurMech.SetLAAES( true, -1 );
        } else {
            CurMech.SetLAAES( false, -1 );
        }
    } catch( Exception e ) {
        // ensure it's not checked when it shouldn't be
        chkLAAES.setSelected( CurMech.HasLAAES() );
        Media.Messager( this, e.getMessage() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkLAAESActionPerformed

private void chkRAAESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRAAESActionPerformed
    if( chkRAAES.isSelected() == CurMech.HasRAAES() ) { return; }
    try {
        if( chkRAAES.isSelected() ) {
            CurMech.SetRAAES( true, -1 );
        } else {
            CurMech.SetRAAES( false, -1 );
        }
    } catch( Exception e ) {
        // ensure it's not checked when it shouldn't be
        chkRAAES.setSelected( CurMech.HasRAAES() );
        Media.Messager( this, e.getMessage() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkRAAESActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    CloseProgram();
}//GEN-LAST:event_formWindowClosing

private void chkTracksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTracksActionPerformed
    // is the system already installed?
    if( chkTracks.isSelected() == CurMech.HasTracks() ) { return; }
    try {
        if( chkTracks.isSelected() ) {
            CurMech.SetTracks( true );
        } else {
            CurMech.SetTracks( false );
        }
    } catch( Exception e ) {
        Media.Messager( this, e.getMessage() );
        // ensure it's not checked when it shouldn't be
        chkTracks.setSelected( CurMech.HasTracks() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_chkTracksActionPerformed

private void btnForceListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForceListActionPerformed
    dForce.setLocationRelativeTo(this);
    dForce.setVisible(true);
}//GEN-LAST:event_btnForceListActionPerformed

private void btnAddToForceListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToForceListActionPerformed
    SetSource = false;
    SolidifyMech();
    QuickSave();
    if (VerifyMech(evt)) {
        dForce.Add(CurMech, Prefs.get("Currentfile", ""));
    }
    SetSource = true;
}//GEN-LAST:event_btnAddToForceListActionPerformed

private void mnuCreateTCGMechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCreateTCGMechActionPerformed
    // Create CCG stats for mech
    // TODO: Add handling code to check if a canon card already exists.
    SolidifyMech();
    dlgCCGMech ccgMech = new dlgCCGMech( this, true, CurMech );
    ccgMech.setLocationRelativeTo( this );
    ccgMech.setVisible( true );
}//GEN-LAST:event_mnuCreateTCGMechActionPerformed

private void mnuTextTROActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTextTROActionPerformed
    SetSource = false;
    SolidifyMech();
    dlgTextExport Text = new dlgTextExport( this, true, CurMech );
    Text.setLocationRelativeTo( this );
    Text.setVisible( true );
    CurMech.SetCurLoadout( (String) cmbOmniVariant.getSelectedItem() );
    SetSource = true;
}//GEN-LAST:event_mnuTextTROActionPerformed

private void chkChartFrontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChartFrontActionPerformed
    UpdateBasicChart();
}//GEN-LAST:event_chkChartFrontActionPerformed

private void chkChartRearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChartRearActionPerformed
    UpdateBasicChart();
}//GEN-LAST:event_chkChartRearActionPerformed

private void chkChartRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChartRightActionPerformed
    UpdateBasicChart();
}//GEN-LAST:event_chkChartRightActionPerformed

private void chkChartLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkChartLeftActionPerformed
    UpdateBasicChart();
}//GEN-LAST:event_chkChartLeftActionPerformed

private void chkCommandConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCommandConsoleActionPerformed
    if( chkCommandConsole.isSelected() == CurMech.HasCommandConsole() ) { return; }
    if( chkCommandConsole.isSelected() ) {
        if( ! CurMech.SetCommandConsole( true ) ) {
            Media.Messager( this, "Command Console cannot be allocated." );
            chkCommandConsole.setSelected( false );
        }
    } else {
        CurMech.SetCommandConsole( false );
    }

    // now refresh the information panes
    RefreshEquipment();
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkCommandConsoleActionPerformed

private void chkFCSAIVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFCSAIVActionPerformed
        if( CurMech.UsingArtemisIV() == chkFCSAIV.isSelected() ) { return; }
        if( chkFCSAIV.isSelected() ) {
            try {
                CurMech.SetFCSArtemisIV( true );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSAIV.setSelected( false );
            }
        } else {
            try {
                CurMech.SetFCSArtemisIV( false );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSAIV.setSelected( true );
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
}//GEN-LAST:event_chkFCSAIVActionPerformed

private void chkFCSAVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFCSAVActionPerformed
        if( CurMech.UsingArtemisV() == chkFCSAV.isSelected() ) { return; }
        if( chkFCSAV.isSelected() ) {
            try {
                CurMech.SetFCSArtemisV( true );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSAV.setSelected( false );
            }
        } else {
            try {
                CurMech.SetFCSArtemisV( false );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSAV.setSelected( true );
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
}//GEN-LAST:event_chkFCSAVActionPerformed

private void chkFCSApolloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFCSApolloActionPerformed
        if( CurMech.UsingApollo() == chkFCSApollo.isSelected() ) { return; }
        if( chkFCSApollo.isSelected() ) {
            try {
                CurMech.SetFCSApollo( true );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSApollo.setSelected( false );
            }
        } else {
            try {
                CurMech.SetFCSApollo( false );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSApollo.setSelected( true );
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
}//GEN-LAST:event_chkFCSApolloActionPerformed

private void chkClanCASEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClanCASEActionPerformed
    CurMech.GetLoadout().SetClanCASE( chkClanCASE.isSelected() );
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkClanCASEActionPerformed

private void lstSelectedEquipmentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstSelectedEquipmentKeyPressed
    if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
        btnRemoveEquipActionPerformed(new ActionEvent(evt.getSource(), evt.getID(), null));
    }
}//GEN-LAST:event_lstSelectedEquipmentKeyPressed

private void mnuImportHMPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportHMPActionPerformed
    if( CurMech.HasChanged() ) {
        int choice = javax.swing.JOptionPane.showConfirmDialog( this,
            "The current 'Mech has changed.\nDo you want to discard those changes?", "Discard Changes?", javax.swing.JOptionPane.YES_NO_OPTION );
        if( choice == 1 ) { return; }
    }

    // Get the mech we're loading
    Mech m = null;

    File tempFile = new File( Prefs.get( "LastOpenDirectory", "" ) );
    JFileChooser fc = new JFileChooser();
    fc.addChoosableFileFilter( new javax.swing.filechooser.FileFilter() {
        public boolean accept( File f ) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = Utils.getExtension( f );
            if ( extension != null ) {
                if ( extension.equals( "hmp" ) ) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        //The description of this filter
        public String getDescription() {
            return "*.hmp";
        }
    } );
    fc.setAcceptAllFileFilterUsed( false );
    fc.setCurrentDirectory( tempFile );
    int returnVal = fc.showDialog( this, "Import HMP File" );
    if( returnVal != JFileChooser.APPROVE_OPTION ) { return; }
    File loadmech = fc.getSelectedFile();
    String filename = "";
    try {
        filename = loadmech.getCanonicalPath();
        Prefs.put("LastOpenDirectory", loadmech.getCanonicalPath().replace(loadmech.getName(), ""));
        Prefs.put("LastOpenFile", loadmech.getName());
    } catch( Exception e ) {
        Media.Messager( this, "There was a problem opening the file:\n" + e.getMessage() );
        return;
    }

    String Messages = "";
    try {
        HMPReader HMPr = new HMPReader();
        m = HMPr.GetMech( filename, false );
        Messages = HMPr.GetErrors();
    } catch( Exception e ) {
        // had a problem loading the mech.  let the user know.
        if( e.getMessage() == null ) {
            Media.Messager( this, "An unknown error has occured.  The log file has been updated." );
            e.printStackTrace();
        } else {
            Media.Messager( this, e.getMessage() );
        }
        return;
    }

    if( Messages.length() > 0 ) {
        dlgTextExport msgs = new dlgTextExport( this, false, Messages );
        msgs.setLocationRelativeTo( this );
        msgs.setVisible( true );
    }

    CurMech = m;
    LoadMechIntoGUI();
    CurMech.SetChanged( false );
}//GEN-LAST:event_mnuImportHMPActionPerformed

private void btnChatInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChatInfoActionPerformed
    java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( CurMech.GetChatInfo() );
    java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents( export, this );
}//GEN-LAST:event_btnChatInfoActionPerformed

private void chkFHESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFHESActionPerformed
    if( chkFHES.isSelected() == CurMech.HasFHES() ) { return; }
    if( chkFHES.isSelected() ) {
        CurMech.SetFHES( true );
    } else {
        CurMech.SetFHES( false );
    }

    // now refresh the information panes
    RefreshEquipment();
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkFHESActionPerformed

private void chkPartialWingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPartialWingActionPerformed
    if( chkPartialWing.isSelected() == CurMech.UsingPartialWing() ) { return; }
    if( chkPartialWing.isSelected() ) {
        try {
            if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                tech.setLocationRelativeTo( this );
                tech.setVisible( true );
                CurMech.SetPartialWing( chkPartialWing.isSelected(), tech.IsClan() );
            } else if( CurMech.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                CurMech.SetPartialWing( chkPartialWing.isSelected(), true );
            } else {
                CurMech.SetPartialWing( chkPartialWing.isSelected(), false );
            }
        } catch( Exception e ) {
            Media.Messager( this, e.getMessage() );
        }
    } else {
        try {
            CurMech.SetPartialWing( false );
        } catch ( Exception e ) {
            Media.Messager( this, e.getMessage() );
        }
    }

    // now refresh the information panes
    RefreshEquipment();
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkPartialWingActionPerformed

private void mnuUnlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnlockActionPerformed
        int choice = javax.swing.JOptionPane.showConfirmDialog( this,
            "Are you sure you want to unlock the chassis?\nAll omnimech loadouts" +
            " will be deleted\nand the 'Mech will revert to its base loadout.",
            "Unlock Chassis?", javax.swing.JOptionPane.YES_NO_OPTION );
        if( choice == 1 ) {
            return;
        }

        // make it an omni
        CurMech.UnlockChassis();
        FixTransferHandlers();
        SetLoadoutArrays();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        LoadMechIntoGUI();
}//GEN-LAST:event_mnuUnlockActionPerformed

private void btnBracketChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBracketChartActionPerformed
    dlgBracketCharts charts = new dlgBracketCharts( this, true, CurMech );
    charts.setLocationRelativeTo( this );
    charts.setVisible( true );
}//GEN-LAST:event_btnBracketChartActionPerformed

private void chkFractionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFractionalActionPerformed
    if( chkFractional.isSelected() == CurMech.UsingFractionalAccounting() ) { return; }
    CurMech.SetFractionalAccounting( chkFractional.isSelected() );
    if( ! CurMech.UsingFractionalAccounting() ) {
        ArrayList v = CurMech.GetLoadout().GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof Ammunition ) {
                ((Ammunition) v.get( i )).ResetLotSize();
            }
        }
    }

    RefreshEquipment();
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkFractionalActionPerformed

private void mnuBatchHMPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBatchHMPActionPerformed
    dlgBatchHMP batch = new dlgBatchHMP( this, true );
    batch.setLocationRelativeTo( this );
    batch.setVisible( true );
}//GEN-LAST:event_mnuBatchHMPActionPerformed

private void chkAverageDamageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAverageDamageActionPerformed
    UpdateBasicChart();
}//GEN-LAST:event_chkAverageDamageActionPerformed

private void chkShowTextNotGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowTextNotGraphActionPerformed
    UpdateBasicChart();
}//GEN-LAST:event_chkShowTextNotGraphActionPerformed

private void btnRenameVariantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenameVariantActionPerformed
    SaveOmniFluffInfo();
    String VariantName = "";

    // get the variant name
    dlgOmniBase input = new dlgOmniBase( this, true );
    input.setTitle( "Name this variant" );
    input.setLocationRelativeTo( this );
    input.setVisible( true );
    if( input.WasCanceled() ) {
        input.dispose();
        return;
    } else {
        VariantName = input.GetInput();
        input.dispose();
    }

    if( CurMech.GetBaseLoadout().GetName().equals( VariantName ) ) {
        Media.Messager( this, "\"" + VariantName + "\" is reserved for the base loadout and cannot be used\nto name this loadout.  Please choose another name." );
        return;
    }

    // see if another loadout has the same name
    ArrayList Loadouts = CurMech.GetLoadouts();
    for( int i = 0; i < Loadouts.size(); i++ ) {
        if( ((ifMechLoadout) Loadouts.get( i )).GetName().equals( VariantName ) ) {
            Media.Messager( this, "Could not rename the loadout because\nthe name given matches an existing loadout." );
            return;
        }
    }

    CurMech.GetLoadout().SetName( VariantName );
    RefreshOmniVariants();
}//GEN-LAST:event_btnRenameVariantActionPerformed

private void cmbPWHDTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPWHDTypeActionPerformed
    if( BuildLookupName( (ifState) CurMech.GetArmor().GetHDArmorType() ).equals( (String) cmbPWHDType.getSelectedItem() ) ) {
        return;
    }
    RecalcPatchworkArmor( LocationIndex.MECH_LOC_HD );
    // we check for hardened armor, you can only have so many IJJs
    FixJJSpinnerModel();

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_cmbPWHDTypeActionPerformed

private void cmbPWCTTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPWCTTypeActionPerformed
    if( BuildLookupName( (ifState) CurMech.GetArmor().GetCTArmorType() ).equals( (String) cmbPWCTType.getSelectedItem() ) ) {
        return;
    }
    RecalcPatchworkArmor( LocationIndex.MECH_LOC_CT );
    // we check for hardened armor, you can only have so many IJJs
    FixJJSpinnerModel();

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_cmbPWCTTypeActionPerformed

private void cmbPWLTTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPWLTTypeActionPerformed
    if( BuildLookupName( (ifState) CurMech.GetArmor().GetLTArmorType() ).equals( (String) cmbPWLTType.getSelectedItem() ) ) {
        return;
    }
    RecalcPatchworkArmor( LocationIndex.MECH_LOC_LT );
    // we check for hardened armor, you can only have so many IJJs
    FixJJSpinnerModel();

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_cmbPWLTTypeActionPerformed

private void cmbPWRTTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPWRTTypeActionPerformed
    if( BuildLookupName( (ifState) CurMech.GetArmor().GetRTArmorType() ).equals( (String) cmbPWRTType.getSelectedItem() ) ) {
        return;
    }
    RecalcPatchworkArmor( LocationIndex.MECH_LOC_RT );
    // we check for hardened armor, you can only have so many IJJs
    FixJJSpinnerModel();

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_cmbPWRTTypeActionPerformed

private void cmbPWLATypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPWLATypeActionPerformed
    if( BuildLookupName( (ifState) CurMech.GetArmor().GetLAArmorType() ).equals( (String) cmbPWLAType.getSelectedItem() ) ) {
        return;
    }
    RecalcPatchworkArmor( LocationIndex.MECH_LOC_LA );
    // we check for hardened armor, you can only have so many IJJs
    FixJJSpinnerModel();

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_cmbPWLATypeActionPerformed

private void cmbPWRATypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPWRATypeActionPerformed
    if( BuildLookupName( (ifState) CurMech.GetArmor().GetRAArmorType() ).equals( (String) cmbPWRAType.getSelectedItem() ) ) {
        return;
    }
    RecalcPatchworkArmor( LocationIndex.MECH_LOC_RA );
    // we check for hardened armor, you can only have so many IJJs
    FixJJSpinnerModel();

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_cmbPWRATypeActionPerformed

private void cmbPWLLTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPWLLTypeActionPerformed
    if( BuildLookupName( (ifState) CurMech.GetArmor().GetLLArmorType() ).equals( (String) cmbPWLLType.getSelectedItem() ) ) {
        return;
    }
    RecalcPatchworkArmor( LocationIndex.MECH_LOC_LL );
    // we check for hardened armor, you can only have so many IJJs
    FixJJSpinnerModel();

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_cmbPWLLTypeActionPerformed

private void cmbPWRLTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPWRLTypeActionPerformed
    if( BuildLookupName( (ifState) CurMech.GetArmor().GetRLArmorType() ).equals( (String) cmbPWRLType.getSelectedItem() ) ) {
        return;
    }
    RecalcPatchworkArmor( LocationIndex.MECH_LOC_RL );
    // we check for hardened armor, you can only have so many IJJs
    FixJJSpinnerModel();

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_cmbPWRLTypeActionPerformed

private void chkHDTurretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHDTurretActionPerformed
        if( CurMech.IsOmnimech() ) {
            if( CurMech.GetBaseLoadout().GetHDTurret() == CurMech.GetLoadout().GetHDTurret() ) {
                chkHDTurret.setSelected( true );
                return;
            }
        }
        if( CurMech.GetLoadout().HasHDTurret() == chkHDTurret.isSelected() ) { return; }
        if( chkHDTurret.isSelected() ) {
            try {
                CurMech.GetLoadout().SetHDTurret( true, -1 );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkHDTurret.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetHDTurret( false, -1 );
            } catch( Exception e ) {
                Media.Messager( "Fatal error attempting to remove turret.\nGetting a new 'Mech, sorry..." );
            }
        }
        CheckEquipment();
        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
}//GEN-LAST:event_chkHDTurretActionPerformed

private void chkLTTurretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLTTurretActionPerformed
        if( CurMech.IsOmnimech() ) {
            if( CurMech.GetBaseLoadout().GetLTTurret() == CurMech.GetLoadout().GetLTTurret() ) {
                chkLTTurret.setSelected( true );
                return;
            }
        }
        if( CurMech.GetLoadout().HasLTTurret() == chkLTTurret.isSelected() ) { return; }
        if( chkLTTurret.isSelected() ) {
            try {
                CurMech.GetLoadout().SetLTTurret( true, -1 );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkLTTurret.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetLTTurret( false, -1 );
            } catch( Exception e ) {
                Media.Messager( "Fatal error attempting to remove turret.\nGetting a new 'Mech, sorry..." );
            }
        }
        CheckEquipment();
        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
}//GEN-LAST:event_chkLTTurretActionPerformed

private void chkRTTurretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRTTurretActionPerformed
        if( CurMech.IsOmnimech() ) {
            if( CurMech.GetBaseLoadout().GetRTTurret() == CurMech.GetLoadout().GetRTTurret() ) {
                chkRTTurret.setSelected( true );
                return;
            }
        }
        if( CurMech.GetLoadout().HasRTTurret() == chkRTTurret.isSelected() ) { return; }
        if( chkRTTurret.isSelected() ) {
            try {
                CurMech.GetLoadout().SetRTTurret( true, -1 );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkRTTurret.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetRTTurret( false, -1 );
            } catch( Exception e ) {
                Media.Messager( "Fatal error attempting to remove turret.\nGetting a new 'Mech, sorry..." );
            }
        }
        CheckEquipment();
        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
}//GEN-LAST:event_chkRTTurretActionPerformed

private void mnuBFBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBFBActionPerformed
    String[] call = { "java", "-Xmx256m", "-jar", "bfb.jar" };
    try {
        Runtime.getRuntime().exec(call);
    } catch (Exception ex) {
        Media.Messager("Error while trying to open BFB\n" + ex.getMessage());
        System.out.println(ex.getMessage());
    }
}//GEN-LAST:event_mnuBFBActionPerformed

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    Overview.StartNewDocument();
    Capabilities.StartNewDocument();
    History.StartNewDocument();
    Deployment.StartNewDocument();
    Variants.StartNewDocument();
    Notables.StartNewDocument();
    Additional.StartNewDocument();
    txtManufacturer.setText( "" );
    txtManufacturerLocation.setText( "" );
    txtEngineManufacturer.setText( "" );
    txtArmorModel.setText( "" );
    txtChassisModel.setText( "" );
    txtJJModel.setText( "" );
    txtCommSystem.setText( "" );
    txtTNTSystem.setText( "" );
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void chkBoostersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoostersActionPerformed
    if( chkBoosters.isSelected() == CurMech.UsingJumpBooster() ) { return; }
    try {
        CurMech.SetJumpBooster( chkBoosters.isSelected() );
    } catch( Exception e ) {
        Media.Messager( this, e.getMessage() );
    }
    spnBoosterMP.setEnabled( CurMech.UsingJumpBooster() );
    FixJumpBoosterSpinnerModel();

    // now refresh the information panes
    RefreshEquipment();
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkBoostersActionPerformed

private void spnBoosterMPStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnBoosterMPStateChanged
    // just change the number of jump MP.
    javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnBoosterMP.getModel();
    javax.swing.JComponent editor = spnBoosterMP.getEditor();
    javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

    // get the value from the text box, if it's valid.
    try {
        spnBoosterMP.commitEdit();
    } catch ( java.text.ParseException pe ) {
        // Edited value is invalid, spinner.getValue() will return
        // the last valid value, you could revert the spinner to show that:
        if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
            tf.setValue(spnBoosterMP.getValue());
        }
        return;
    }

    CurMech.GetJumpBooster().SetBoostMP( n.getNumber().intValue() );

    // now refresh the information panes
    FixJumpBoosterSpinnerModel();
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_spnBoosterMPStateChanged

private void chkBoobyTrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoobyTrapActionPerformed
    if( chkBoobyTrap.isSelected() == CurMech.GetLoadout().HasBoobyTrap() ) { return; }
    try {
        CurMech.GetLoadout().SetBoobyTrap( chkBoobyTrap.isSelected() );
    } catch( Exception e ) {
        Media.Messager( this, e.getMessage() );
        chkBoobyTrap.setSelected( false );
    }
    
    // now refresh the information panes
    RefreshEquipment();
    RefreshSummary();
    RefreshInfoPane();
}//GEN-LAST:event_chkBoobyTrapActionPerformed

private void cmbProductionEraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProductionEraActionPerformed
    CurMech.SetProductionEra( cmbProductionEra.getSelectedIndex() );
}//GEN-LAST:event_cmbProductionEraActionPerformed

private void btnAddQuirkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddQuirkActionPerformed
    dlgQuirks qmanage = new dlgQuirks(this, true, data, quirks);
    qmanage.setLocationRelativeTo(this);
    qmanage.setVisible(true);
    tblQuirks.setModel(new tbQuirks(quirks));
}//GEN-LAST:event_btnAddQuirkActionPerformed

private void setViewToolbar(boolean Visible)
{
    tlbIconBar.setVisible(Visible);
    Prefs.putBoolean("ViewToolbar", Visible);
    mnuViewToolbar.setState(Visible);
    if (Visible) {
        if (this.getHeight() != 600) { this.setSize(750, 600); }
    } else {
        if (this.getHeight() != 575) { this.setSize(750, 575); }
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddEquip;
    private javax.swing.JButton btnAddQuirk;
    private javax.swing.JButton btnAddToForceList;
    private javax.swing.JButton btnAddVariant;
    private javax.swing.JButton btnArmorTons;
    private javax.swing.JButton btnAutoAllocate;
    private javax.swing.JCheckBox btnBalanceArmor;
    private javax.swing.JButton btnBracketChart;
    private javax.swing.JButton btnChatInfo;
    private javax.swing.JButton btnClearEquip;
    private javax.swing.JButton btnClearImage;
    private javax.swing.JButton btnClearLoadout;
    private javax.swing.JButton btnCompactCrits;
    private javax.swing.JButton btnDeleteVariant;
    private javax.swing.JButton btnEfficientArmor;
    private javax.swing.JButton btnExportClipboardIcon;
    private javax.swing.JButton btnExportHTML;
    private javax.swing.JButton btnExportHTMLIcon;
    private javax.swing.JButton btnExportMTF;
    private javax.swing.JButton btnExportMTFIcon;
    private javax.swing.JButton btnExportTXT;
    private javax.swing.JButton btnExportTextIcon;
    private javax.swing.JButton btnForceList;
    private javax.swing.JButton btnLoadImage;
    private javax.swing.JButton btnLockChassis;
    private javax.swing.JButton btnMaxArmor;
    private javax.swing.JButton btnNewIcon;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnOptionsIcon;
    private javax.swing.JButton btnPostToS7;
    private javax.swing.JButton btnPrintIcon;
    private javax.swing.JButton btnPrintPreview;
    private javax.swing.JButton btnRemainingArmor;
    private javax.swing.JButton btnRemoveEquip;
    private javax.swing.JButton btnRemoveItemCrits;
    private javax.swing.JButton btnRenameVariant;
    private javax.swing.JButton btnSaveIcon;
    private javax.swing.JButton btnSelectiveAllocate;
    private javax.swing.JCheckBox chkAverageDamage;
    private javax.swing.JCheckBox chkBSPFD;
    private javax.swing.JCheckBox chkBoobyTrap;
    private javax.swing.JCheckBox chkBoosters;
    private javax.swing.JCheckBox chkCLPS;
    private javax.swing.JCheckBox chkCTCASE;
    private javax.swing.JCheckBox chkCTCASE2;
    private javax.swing.JCheckBox chkChartFront;
    private javax.swing.JCheckBox chkChartLeft;
    private javax.swing.JCheckBox chkChartRear;
    private javax.swing.JCheckBox chkChartRight;
    private javax.swing.JCheckBox chkClanCASE;
    private javax.swing.JCheckBox chkCommandConsole;
    private javax.swing.JCheckBox chkEjectionSeat;
    private javax.swing.JCheckBox chkEnviroSealing;
    private javax.swing.JCheckBox chkFCSAIV;
    private javax.swing.JCheckBox chkFCSAV;
    private javax.swing.JCheckBox chkFCSApollo;
    private javax.swing.JCheckBox chkFHES;
    private javax.swing.JCheckBox chkFractional;
    private javax.swing.JCheckBox chkHDCASE2;
    private javax.swing.JCheckBox chkHDTurret;
    private javax.swing.JCheckBox chkIndividualWeapons;
    private javax.swing.JCheckBox chkLAAES;
    private javax.swing.JCheckBox chkLACASE2;
    private javax.swing.JCheckBox chkLAHand;
    private javax.swing.JCheckBox chkLALowerArm;
    private javax.swing.JCheckBox chkLLCASE2;
    private javax.swing.JCheckBox chkLTCASE;
    private javax.swing.JCheckBox chkLTCASE2;
    private javax.swing.JCheckBox chkLTTurret;
    private javax.swing.JCheckBox chkLegAES;
    private javax.swing.JCheckBox chkNullSig;
    private javax.swing.JCheckBox chkOmnimech;
    private javax.swing.JCheckBox chkPartialWing;
    private javax.swing.JCheckBox chkRAAES;
    private javax.swing.JCheckBox chkRACASE2;
    private javax.swing.JCheckBox chkRAHand;
    private javax.swing.JCheckBox chkRALowerArm;
    private javax.swing.JCheckBox chkRLCASE2;
    private javax.swing.JCheckBox chkRTCASE;
    private javax.swing.JCheckBox chkRTCASE2;
    private javax.swing.JCheckBox chkRTTurret;
    private javax.swing.JCheckBox chkShowTextNotGraph;
    private javax.swing.JCheckBox chkSupercharger;
    private javax.swing.JCheckBox chkTracks;
    private javax.swing.JCheckBox chkUseTC;
    private javax.swing.JCheckBox chkVoidSig;
    private javax.swing.JCheckBox chkYearRestrict;
    private javax.swing.JComboBox cmbArmorType;
    private javax.swing.JComboBox cmbCockpitType;
    private javax.swing.JComboBox cmbEngineType;
    private javax.swing.JComboBox cmbGyroType;
    private javax.swing.JComboBox cmbHeatSinkType;
    private javax.swing.JComboBox cmbInternalType;
    private javax.swing.JComboBox cmbJumpJetType;
    private javax.swing.JComboBox cmbMechEra;
    private javax.swing.JComboBox cmbMechType;
    private javax.swing.JComboBox cmbMotiveType;
    private javax.swing.JComboBox cmbNumEquips;
    private javax.swing.JComboBox cmbOmniVariant;
    private javax.swing.JComboBox cmbPWCTType;
    private javax.swing.JComboBox cmbPWHDType;
    private javax.swing.JComboBox cmbPWLAType;
    private javax.swing.JComboBox cmbPWLLType;
    private javax.swing.JComboBox cmbPWLTType;
    private javax.swing.JComboBox cmbPWRAType;
    private javax.swing.JComboBox cmbPWRLType;
    private javax.swing.JComboBox cmbPWRTType;
    private javax.swing.JComboBox cmbPhysEnhance;
    private javax.swing.JComboBox cmbProductionEra;
    private javax.swing.JComboBox cmbRulesLevel;
    private javax.swing.JComboBox cmbSCLoc;
    private javax.swing.JComboBox cmbTechBase;
    private javax.swing.JComboBox cmbTonnage;
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
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator20;
    private javax.swing.JToolBar.Separator jSeparator21;
    private javax.swing.JToolBar.Separator jSeparator22;
    private javax.swing.JToolBar.Separator jSeparator23;
    private javax.swing.JToolBar.Separator jSeparator24;
    private javax.swing.JToolBar.Separator jSeparator25;
    private javax.swing.JToolBar.Separator jSeparator26;
    private javax.swing.JSeparator jSeparator27;
    private javax.swing.JSeparator jSeparator28;
    private javax.swing.JSeparator jSeparator29;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator30;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextArea jTextAreaBFConversion;
    private javax.swing.JLabel lblAVInLot;
    private javax.swing.JLabel lblArmorCoverage;
    private javax.swing.JLabel lblArmorPoints;
    private javax.swing.JLabel lblArmorTonsWasted;
    private javax.swing.JLabel lblArmorType;
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
    private javax.swing.JLabel lblBattleMechQuirks;
    private javax.swing.JLabel lblCTArmorHeader;
    private javax.swing.JLabel lblCTHeader;
    private javax.swing.JLabel lblCTIntPts;
    private javax.swing.JLabel lblCTRArmorHeader;
    private javax.swing.JLabel lblCockpit;
    private javax.swing.JLabel lblDamagePerTon;
    private javax.swing.JLabel lblEngineType;
    private javax.swing.JLabel lblEraYears;
    private javax.swing.JLabel lblFluffImage;
    private javax.swing.JLabel lblGyroType;
    private javax.swing.JLabel lblHDArmorHeader;
    private javax.swing.JLabel lblHDHeader;
    private javax.swing.JLabel lblHDIntPts;
    private javax.swing.JLabel lblHSNumber;
    private javax.swing.JLabel lblHeatSinkType;
    private javax.swing.JLabel lblInfoAVCI;
    private javax.swing.JLabel lblInfoAVSL;
    private javax.swing.JLabel lblInfoAVSW;
    private javax.swing.JLabel lblInfoAmmo;
    private javax.swing.JLabel lblInfoBV;
    private javax.swing.JLabel lblInfoCost;
    private javax.swing.JLabel lblInfoCrits;
    private javax.swing.JLabel lblInfoDamage;
    private javax.swing.JLabel lblInfoExtinct;
    private javax.swing.JLabel lblInfoHeat;
    private javax.swing.JLabel lblInfoIntro;
    private javax.swing.JLabel lblInfoMountRestrict;
    private javax.swing.JLabel lblInfoName;
    private javax.swing.JLabel lblInfoRange;
    private javax.swing.JLabel lblInfoReintro;
    private javax.swing.JLabel lblInfoRulesLevel;
    private javax.swing.JLabel lblInfoSpecials;
    private javax.swing.JLabel lblInfoTonnage;
    private javax.swing.JLabel lblInfoType;
    private javax.swing.JLabel lblInternalType;
    private javax.swing.JLabel lblJumpMP;
    private javax.swing.JLabel lblLAArmorHeader;
    private javax.swing.JLabel lblLAHeader;
    private javax.swing.JLabel lblLAIntPts;
    private javax.swing.JLabel lblLLArmorHeader;
    private javax.swing.JLabel lblLLHeader;
    private javax.swing.JLabel lblLLIntPts;
    private javax.swing.JLabel lblLTArmorHeader;
    private javax.swing.JLabel lblLTHeader;
    private javax.swing.JLabel lblLTIntPts;
    private javax.swing.JLabel lblLTRArmorHeader;
    private javax.swing.JLabel lblLegendTitle;
    private javax.swing.JLabel lblMechEra;
    private javax.swing.JLabel lblMechName;
    private javax.swing.JLabel lblMechType;
    private javax.swing.JLabel lblModel;
    private javax.swing.JLabel lblMotiveType;
    private javax.swing.JLabel lblMoveSummary;
    private javax.swing.JLabel lblPWCTLoc;
    private javax.swing.JLabel lblPWHDLoc;
    private javax.swing.JLabel lblPWLALoc;
    private javax.swing.JLabel lblPWLLLoc;
    private javax.swing.JLabel lblPWLTLoc;
    private javax.swing.JLabel lblPWRALoc;
    private javax.swing.JLabel lblPWRLLoc;
    private javax.swing.JLabel lblPWRTLoc;
    private javax.swing.JLabel lblPhysEnhance;
    private javax.swing.JLabel lblProdYear;
    private javax.swing.JLabel lblRAArmorHeader;
    private javax.swing.JLabel lblRAHeader;
    private javax.swing.JLabel lblRAIntPts;
    private javax.swing.JLabel lblRLArmorHeader;
    private javax.swing.JLabel lblRLHeader;
    private javax.swing.JLabel lblRLIntPts;
    private javax.swing.JLabel lblRTArmorHeader;
    private javax.swing.JLabel lblRTHeader;
    private javax.swing.JLabel lblRTIntPts;
    private javax.swing.JLabel lblRTRArmorHeader;
    private javax.swing.JLabel lblRulesLevel;
    private javax.swing.JLabel lblRunMP;
    private javax.swing.JLabel lblRunMPLabel;
    private javax.swing.JLabel lblSelectVariant;
    private javax.swing.JLabel lblSumCockpit;
    private javax.swing.JLabel lblSumEngine;
    private javax.swing.JLabel lblSumEnhance;
    private javax.swing.JLabel lblSumGyro;
    private javax.swing.JLabel lblSumHeadAvailable;
    private javax.swing.JLabel lblSumHeadCrits;
    private javax.swing.JLabel lblSumHeadCrits1;
    private javax.swing.JLabel lblSumHeadItem;
    private javax.swing.JLabel lblSumHeadTons;
    private javax.swing.JLabel lblSumHeadTons1;
    private javax.swing.JLabel lblSumHeatSinks;
    private javax.swing.JLabel lblSumJJ;
    private javax.swing.JLabel lblSumPAmps;
    private javax.swing.JLabel lblSumStructure;
    private javax.swing.JLabel lblSupercharger;
    private javax.swing.JLabel lblTechBase;
    private javax.swing.JLabel lblTonPercArmor;
    private javax.swing.JLabel lblTonPercEngine;
    private javax.swing.JLabel lblTonPercEnhance;
    private javax.swing.JLabel lblTonPercEquips;
    private javax.swing.JLabel lblTonPercHeatSinks;
    private javax.swing.JLabel lblTonPercJumpJets;
    private javax.swing.JLabel lblTonPercStructure;
    private javax.swing.JLabel lblTonPercWeapons;
    private javax.swing.JLabel lblTonnage;
    private javax.swing.JLabel lblUnitType;
    private javax.swing.JLabel lblWalkMP;
    private javax.swing.JList lstCTCrits;
    private javax.swing.JList lstChooseAmmunition;
    private javax.swing.JList lstChooseArtillery;
    private javax.swing.JList lstChooseBallistic;
    private javax.swing.JList lstChooseEnergy;
    private javax.swing.JList lstChooseEquipment;
    private javax.swing.JList lstChooseMissile;
    private javax.swing.JList lstChoosePhysical;
    private javax.swing.JList lstCritsToPlace;
    private javax.swing.JList lstHDCrits;
    private javax.swing.JList lstLACrits;
    private javax.swing.JList lstLLCrits;
    private javax.swing.JList lstLTCrits;
    private javax.swing.JList lstRACrits;
    private javax.swing.JList lstRLCrits;
    private javax.swing.JList lstRTCrits;
    private javax.swing.JList lstSelectedEquipment;
    private javax.swing.JMenuItem mnuAboutSSW;
    private javax.swing.JMenuItem mnuBFB;
    private javax.swing.JMenuItem mnuBatchHMP;
    private javax.swing.JMenu mnuClearFluff;
    private javax.swing.JMenuItem mnuClearUserData;
    private javax.swing.JMenuItem mnuCostBVBreakdown;
    private javax.swing.JMenuItem mnuCreateTCGMech;
    private javax.swing.JMenuItem mnuCredits;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenu mnuExport;
    private javax.swing.JMenuItem mnuExportClipboard;
    private javax.swing.JMenuItem mnuExportHTML;
    private javax.swing.JMenuItem mnuExportMTF;
    private javax.swing.JMenuItem mnuExportTXT;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenu mnuImport;
    private javax.swing.JMenuItem mnuImportHMP;
    private javax.swing.JMenuItem mnuLoad;
    private javax.swing.JMenuBar mnuMainMenu;
    private javax.swing.JMenuItem mnuNewMech;
    private javax.swing.JMenuItem mnuOpen;
    private javax.swing.JMenuItem mnuOptions;
    private javax.swing.JMenuItem mnuPostS7;
    private javax.swing.JMenu mnuPrint;
    private javax.swing.JMenuItem mnuPrintBatch;
    private javax.swing.JMenuItem mnuPrintCurrentMech;
    private javax.swing.JMenuItem mnuPrintPreview;
    private javax.swing.JMenuItem mnuPrintSavedMech;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JMenuItem mnuSaveAs;
    private javax.swing.JMenuItem mnuSummary;
    private javax.swing.JMenuItem mnuTextTRO;
    private javax.swing.JMenuItem mnuUnlock;
    private javax.swing.JCheckBoxMenuItem mnuViewToolbar;
    private javax.swing.JPanel onlLoadoutControls;
    private javax.swing.JPanel pnlAdditionalFluff;
    private javax.swing.JPanel pnlAmmunition;
    private javax.swing.JPanel pnlArmor;
    private javax.swing.JPanel pnlArmorInfo;
    private javax.swing.JPanel pnlArmorSetup;
    private javax.swing.JPanel pnlArtillery;
    private javax.swing.JPanel pnlBFStats;
    private javax.swing.JPanel pnlBallistic;
    private javax.swing.JPanel pnlBasicInformation;
    private javax.swing.JPanel pnlBasicSetup;
    private javax.swing.JPanel pnlBasicSummary;
    private javax.swing.JPanel pnlBattleforce;
    private javax.swing.JPanel pnlCTArmorBox;
    private javax.swing.JPanel pnlCTCrits;
    private javax.swing.JPanel pnlCTRArmorBox;
    private javax.swing.JPanel pnlCapabilities;
    private javax.swing.JPanel pnlCharts;
    private javax.swing.JPanel pnlChassis;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlCriticals;
    private javax.swing.JPanel pnlDamageChart;
    private javax.swing.JPanel pnlDeployment;
    private javax.swing.JPanel pnlEnergy;
    private javax.swing.JPanel pnlEquipInfo;
    private javax.swing.JPanel pnlEquipment;
    private javax.swing.JPanel pnlEquipmentChooser;
    private javax.swing.JPanel pnlEquipmentToPlace;
    private javax.swing.JPanel pnlExport;
    private javax.swing.JPanel pnlFluff;
    private javax.swing.JPanel pnlFrontArmor;
    private javax.swing.JPanel pnlHDArmorBox;
    private javax.swing.JPanel pnlHDCrits;
    private javax.swing.JPanel pnlHeatSinks;
    private javax.swing.JPanel pnlHistory;
    private javax.swing.JPanel pnlImage;
    private javax.swing.JPanel pnlInfoPanel;
    private javax.swing.JPanel pnlLAArmorBox;
    private javax.swing.JPanel pnlLACrits;
    private javax.swing.JPanel pnlLLArmorBox;
    private javax.swing.JPanel pnlLLCrits;
    private javax.swing.JPanel pnlLTArmorBox;
    private javax.swing.JPanel pnlLTCrits;
    private javax.swing.JPanel pnlLTRArmorBox;
    private javax.swing.JPanel pnlManufacturers;
    private javax.swing.JPanel pnlMissile;
    private javax.swing.JPanel pnlMovement;
    private javax.swing.JPanel pnlNotables;
    private javax.swing.JPanel pnlOmniInfo;
    private javax.swing.JPanel pnlOverview;
    private javax.swing.JPanel pnlPatchworkChoosers;
    private javax.swing.JPanel pnlPhysical;
    private javax.swing.JPanel pnlQuirks;
    private javax.swing.JPanel pnlRAArmorBox;
    private javax.swing.JPanel pnlRACrits;
    private javax.swing.JPanel pnlRLArmorBox;
    private javax.swing.JPanel pnlRLCrits;
    private javax.swing.JPanel pnlRTArmorBox;
    private javax.swing.JPanel pnlRTCrits;
    private javax.swing.JPanel pnlRTRArmorBox;
    private javax.swing.JPanel pnlRearArmor;
    private javax.swing.JPanel pnlSelected;
    private javax.swing.JPanel pnlSpecials;
    private javax.swing.JPanel pnlVariants;
    private javax.swing.JPanel pnlWeaponsManufacturers;
    private javax.swing.JScrollPane scpQuirkTable;
    private javax.swing.JScrollPane scpWeaponManufacturers;
    private javax.swing.JScrollPane scrLACrits;
    private javax.swing.JScrollPane scrRACrits;
    private javax.swing.JSpinner spnBoosterMP;
    private javax.swing.JSpinner spnCTArmor;
    private javax.swing.JSpinner spnCTRArmor;
    private javax.swing.JSpinner spnHDArmor;
    private javax.swing.JSpinner spnJumpMP;
    private javax.swing.JSpinner spnLAArmor;
    private javax.swing.JSpinner spnLLArmor;
    private javax.swing.JSpinner spnLTArmor;
    private javax.swing.JSpinner spnLTRArmor;
    private javax.swing.JSpinner spnNumberOfHS;
    private javax.swing.JSpinner spnRAArmor;
    private javax.swing.JSpinner spnRLArmor;
    private javax.swing.JSpinner spnRTArmor;
    private javax.swing.JSpinner spnRTRArmor;
    private javax.swing.JSpinner spnWalkMP;
    private javax.swing.JTable tblQuirks;
    private javax.swing.JTable tblWeaponManufacturers;
    private javax.swing.JTabbedPane tbpFluffEditors;
    private javax.swing.JTabbedPane tbpMainTabPane;
    private javax.swing.JTabbedPane tbpWeaponChooser;
    private javax.swing.JToolBar tlbIconBar;
    private javax.swing.JTextField txtArmorModel;
    private javax.swing.JTextField txtChassisModel;
    private javax.swing.JTextField txtCommSystem;
    private javax.swing.JTextField txtEngineManufacturer;
    private javax.swing.JTextField txtEngineRating;
    private javax.swing.JTextField txtInfoBattleValue;
    private javax.swing.JTextField txtInfoCost;
    private javax.swing.JTextField txtInfoFreeCrits;
    private javax.swing.JTextField txtInfoFreeTons;
    private javax.swing.JTextField txtInfoHeatDiss;
    private javax.swing.JTextField txtInfoMaxHeat;
    private javax.swing.JTextField txtInfoTonnage;
    private javax.swing.JTextField txtInfoUnplaced;
    private javax.swing.JTextField txtJJModel;
    private javax.swing.JTextField txtManufacturer;
    private javax.swing.JTextField txtManufacturerLocation;
    private javax.swing.JTextField txtMechModel;
    private javax.swing.JTextField txtMechName;
    private javax.swing.JTextField txtProdYear;
    private javax.swing.JTextField txtSource;
    private javax.swing.JTextField txtSumArmorCrt;
    private javax.swing.JTextField txtSumArmorTon;
    private javax.swing.JTextField txtSumCocACode;
    private javax.swing.JTextField txtSumCocCrt;
    private javax.swing.JTextField txtSumCocTon;
    private javax.swing.JTextField txtSumEngACode;
    private javax.swing.JTextField txtSumEngCrt;
    private javax.swing.JTextField txtSumEngTon;
    private javax.swing.JTextField txtSumEnhACode;
    private javax.swing.JTextField txtSumEnhCrt;
    private javax.swing.JTextField txtSumEnhTon;
    private javax.swing.JTextField txtSumGyrACode;
    private javax.swing.JTextField txtSumGyrCrt;
    private javax.swing.JTextField txtSumGyrTon;
    private javax.swing.JTextField txtSumHSACode;
    private javax.swing.JTextField txtSumHSCrt;
    private javax.swing.JTextField txtSumHSTon;
    private javax.swing.JTextField txtSumIntACode;
    private javax.swing.JTextField txtSumIntCrt;
    private javax.swing.JTextField txtSumIntTon;
    private javax.swing.JTextField txtSumJJACode;
    private javax.swing.JTextField txtSumJJCrt;
    private javax.swing.JTextField txtSumJJTon;
    private javax.swing.JTextField txtSumPAmpsACode;
    private javax.swing.JTextField txtSumPAmpsTon;
    private javax.swing.JTextField txtTNTSystem;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the imageTracker
     */
    public ImageTracker getImageTracker() {
        return imageTracker;
    }
    
    public void setUnit( ArrayList v ) {
        this.setMech( (Mech) v.get(0) );
    }

    public void loadUnitIntoGUI() {
        this.LoadMechIntoGUI();
    }

    public void showOpenDialog() {
        this.dOpen.Requestor = dlgOpen.FORCE;
        this.dOpen.setVisible(true);
    }

}

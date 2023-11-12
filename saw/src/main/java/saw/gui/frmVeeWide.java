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

import Force.Scenario;
import Force.Unit;
import Print.BFBPrinter;
import Print.PagePrinter;
import Print.PrintVehicle;
import Print.preview.dlgPreview;
import battleforce.BattleForceStats;
import common.*;
import components.*;
import dialog.dlgQuirks;
import dialog.frmForce;
import filehandlers.*;
import gui.TextPane;
import list.view.tbQuirks;
import saw.filehandlers.HTMLWriter;
import states.ifState;
import visitors.VArmorSetPatchworkLocation;
import visitors.VMechFullRecalc;
import visitors.VSetArmorTonnage;
import visitors.ifVisitor;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public final class frmVeeWide extends javax.swing.JFrame implements java.awt.datatransfer.ClipboardOwner, common.DesignForm, ifVeeForm {
    CombatVehicle CurVee;
    abPlaceable CurItem;
    Preferences Prefs;
    String[] Selections = { "", "" };
    public DataFactory data;
    private final Cursor Hourglass = new Cursor( Cursor.WAIT_CURSOR );
    private final Cursor NormalCursor = new Cursor( Cursor.DEFAULT_CURSOR );
    boolean Load = false,
            isLocked = false,
            SetSource = true;
    Object[][] Equipment = { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } };
    final int BALLISTIC = 0,
              ENERGY = 1,
              MISSILE = 2,
              PHYSICAL = 3,
              EQUIPMENT = 4,
              AMMUNITION = 6,
              SELECTED = 7,
              ARTILLERY = 5;
    VSetArmorTonnage ArmorTons;
    private final AvailableCode PPCCapAC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private final AvailableCode LIAC = new AvailableCode( AvailableCode.TECH_BOTH );
    private final AvailableCode PulseModuleAC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private final AvailableCode CaselessAmmoAC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );

    private final ImageTracker imageTracker = new ImageTracker();
    public dlgOpen dOpen = new dlgOpen(this, true);
    public frmForce dForce = new frmForce(this, imageTracker);
    public ArrayList<Quirk> quirks = new ArrayList<Quirk>();
    TextPane Overview = new TextPane();
    TextPane Capabilities = new TextPane();
    TextPane Deployment = new TextPane();
    TextPane History = new TextPane();
    TextPane Additional = new TextPane();
    TextPane Variants = new TextPane();
    TextPane Notables = new TextPane();

    JPopupMenu mnuUtilities = new JPopupMenu();
    JMenuItem mnuDetails = new JMenuItem( "Details" );
    JMenuItem mnuSetVariable = new JMenuItem( "Set Tonnage" );
    JMenuItem mnuSetLotSize = new JMenuItem( "Set Lot Size" );
    JMenuItem mnuArmorComponent = new JMenuItem( "Armor Component" );
    JMenuItem mnuAddCapacitor = new JMenuItem( "Add Capacitor" );
    JMenuItem mnuAddInsulator = new JMenuItem( "Add Insulator" );
    JMenuItem mnuAddPulseModule = new JMenuItem( "Add RISC Pulse Module" );
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

    FocusAdapter spinners = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if ( e.getSource() instanceof JTextComponent ) {
                final JTextComponent textComponent = ((JTextComponent)e.getSource());
                SwingUtilities.invokeLater(textComponent::selectAll);
            }
        }
    };

    /** Creates new form frmMain2 */
    public frmVeeWide() {
        CurVee = new CombatVehicle( );
        initComponents();

        Prefs = Preferences.userRoot().node( Constants.SAWPrefs );
        ArmorTons = new VSetArmorTonnage( Prefs );
        cmbMotiveTypeActionPerformed(null);
        spnTonnageStateChanged(null);


        setTitle( saw.Constants.AppDescription + " " + saw.Constants.GetVersion() );

        // added for easy checking
        PPCCapAC.SetISCodes( 'E', 'X', 'X', 'E', 'D' );
        PPCCapAC.SetISDates( 3057, 3060, true, 3081, 0, 0, false, false );
        PPCCapAC.SetISFactions( "DC", "DC", "", "" );
        PPCCapAC.SetCLCodes( 'E', 'X', 'X', 'E', 'D' );
        PPCCapAC.SetCLDates( 0, 0, false, 3101, 0, 0, false, false );
        PPCCapAC.SetCLFactions( "", "", "PS", "" );
        PPCCapAC.SetPBMAllowed( true );
        PPCCapAC.SetPIMAllowed( true );
        PPCCapAC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        LIAC.SetISCodes( 'E', 'X', 'E', 'F', 'F' );
        LIAC.SetISDates( 0, 0, false, 2575, 2820, 0, true, false );
        LIAC.SetISFactions( "TH", "", "", "" );
        LIAC.SetCLCodes( 'E', 'X', 'E', 'F', 'F' );
        LIAC.SetCLDates( 0, 0, false, 2575, 0, 0, false, false );
        LIAC.SetCLFactions( "TH", "", "", "" );
        LIAC.SetPBMAllowed( true );
        LIAC.SetPIMAllowed( true );
        LIAC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        PulseModuleAC.SetISCodes( 'F', 'X', 'X', 'X', 'F' );
        PulseModuleAC.SetISDates( 3134, 3137, true, 3137, 3140, 0, true, false );
        PulseModuleAC.SetISFactions( "RS", "RS", "RS", "" );
        PulseModuleAC.SetPBMAllowed( true );
        PulseModuleAC.SetPIMAllowed( true );
        PulseModuleAC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL );
        CaselessAmmoAC.SetISCodes( 'D', 'X', 'X', 'E', 'D' );
        CaselessAmmoAC.SetISDates( 3055, 3056, true, 3079, 0, 0, false, false );
        CaselessAmmoAC.SetISFactions( "FC", "FC", "", "" );
        CaselessAmmoAC.SetCLCodes( 'D', 'X', 'X', 'E', 'D' );
        CaselessAmmoAC.SetCLDates( 3055, 3056, false, 3109, 0, 0, false, false );
        CaselessAmmoAC.SetCLFactions( "TH", "", "CSR", "" );
        CaselessAmmoAC.SetPBMAllowed( true );
        CaselessAmmoAC.SetPIMAllowed( true );
        CaselessAmmoAC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        // get the data factory ready
        try {
            data = new DataFactory( CurVee );
        } catch( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }

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

        quirks = CurVee.GetQuirks();
        pack();

        mnuDetails.addActionListener(evt -> {
            GetInfoOn();
            ShowInfoOn(CurItem);
        });

        mnuSetVariable.addActionListener(e -> SetVariableSize());
        mnuSetLotSize.addActionListener(e -> SetAmmoLotSize());
        mnuAddCapacitor.addActionListener(e -> PPCCapacitor());
        mnuAddInsulator.addActionListener(e -> LaserInsulator());
        mnuAddPulseModule.addActionListener(e -> PulseModule());
        mnuDumper.addActionListener(e -> DumperMount());
        mnuCaseless.addActionListener(e -> SwitchCaseless());
        mnuVGLArcFore.addActionListener(e -> SetVGLArcFore());
        mnuVGLArcForeSide.addActionListener(e -> SetVGLArcForeSide());
        mnuVGLArcRear.addActionListener(e -> SetVGLArcRear());
        mnuVGLArcRearSide.addActionListener(e -> SetVGLArcRearSide());
        mnuVGLAmmoFrag.addActionListener(e -> SetVGLAmmoFrag());
        mnuVGLAmmoChaff.addActionListener(e -> SetVGLAmmoChaff());
        mnuVGLAmmoIncen.addActionListener(e -> SetVGLAmmoIncendiary());
        mnuVGLAmmoSmoke.addActionListener(e -> SetVGLAmmoSmoke());
        mnuRemoveItem.addActionListener(e -> RemoveItemCritTab());

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
        mnuUtilities.add( mnuSetVariable );
        mnuUtilities.add( mnuSetLotSize );
        mnuUtilities.add( mnuAddCapacitor );
        mnuUtilities.add( mnuAddInsulator );
        mnuUtilities.add( mnuAddPulseModule );
        mnuUtilities.add( mnuCaseless );
        mnuUtilities.add( mnuVGLArc );
        mnuUtilities.add( mnuVGLAmmo );
        mnuUtilities.add( mnuDumper );
        mnuUtilities.add( mnuUnallocateAll );
        mnuUtilities.add( mnuRemoveItem );

        mnuSetVariable.setVisible( false );
        mnuArmorComponent.setVisible( false );
        mnuAddCapacitor.setVisible( false );
        mnuAddInsulator.setVisible( false );
        mnuAddPulseModule.setVisible( false );
        mnuTurret.setVisible( false );
        mnuCaseless.setVisible( false );
        mnuVGLArc.setVisible( false );
        mnuVGLAmmo.setVisible( false );

        // set the program options
        cmbRulesLevel.setSelectedItem( Prefs.get( "NewCV_RulesLevel", "Tournament Legal" ) );
        cmbEra.setSelectedItem( Prefs.get( "NewCV_Era", "Age of War/Star League" ) );
        BuildTechBaseSelector();
        cmbTechBase.setSelectedItem( Prefs.get( "NewCV_Techbase", "Inner Sphere" ) );
        BuildEngineSelector();
        BuildArmorSelector();
        BuildTurretSelector();
        FixArmorSpinners();
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        CheckOmni();

        //Makes spinners auto-select-all text for easier entry
        ((JSpinner.DefaultEditor)spnTonnage.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnCruiseMP.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnJumpMP.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnHeatSinks.getEditor()).getTextField().addFocusListener(spinners);

        ((JSpinner.DefaultEditor)spnFrontArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnLeftArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRightArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRearArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnTurretArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRearTurretArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRotorArmor.getEditor()).getTextField().addFocusListener(spinners);

        mnuFluffCut.addActionListener(e -> FluffCut( mnuFluff.getInvoker() ));

        mnuFluffCopy.addActionListener(e -> FluffCopy( mnuFluff.getInvoker() ));

        mnuFluffPaste.addActionListener(e -> FluffPaste( mnuFluff.getInvoker() ));

        mnuFluff.add( mnuFluffCut );
        mnuFluff.add( mnuFluffCopy );
        mnuFluff.add( mnuFluffPaste );


        tblWeaponManufacturers.setModel( new javax.swing.table.AbstractTableModel() {
            @Override
            public String getColumnName( int col ) {
                if( col == 1 ) {
                    return "Manufacturer/Model";
                } else {
                    return "Item Name";
                }
            }
            public int getRowCount() { return CurVee.GetLoadout().GetEquipment().size(); }
            public int getColumnCount() { return 2; }
            public Object getValueAt( int row, int col ) {
                Object o = CurVee.GetLoadout().GetEquipment().get( row );
                if( col == 1 ) {
                    return CommonTools.UnknownToEmpty( ((abPlaceable) o).GetManufacturer() );
                } else {
                    return ((abPlaceable) o).CritName();
                }
            }
            @Override
            public boolean isCellEditable( int row, int col ) {
                return col != 0;
            }
            @Override
            public void setValueAt( Object value, int row, int col ) {
                if( col == 0 ) { return; }
                if( ! ( value instanceof String ) ) { return; }
                abPlaceable a = (abPlaceable) CurVee.GetLoadout().GetEquipment().get( row );
                if( chkIndividualWeapons.isSelected() ) {
                    a.SetManufacturer( (String) value );
                    fireTableCellUpdated( row, col );
                } else {
                    ArrayList v = CurVee.GetLoadout().GetEquipment();
                    for (Object o : v) {
                        if (FileCommon.LookupStripArc(((abPlaceable) o).LookupName()).equals(FileCommon.LookupStripArc(a.LookupName()))) {
                            ((abPlaceable) o).SetManufacturer((String) value);
                        }
                    }
                    fireTableDataChanged();
                }
            }
        } );

        tblWeaponManufacturers.getInputMap( javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_TAB, 0, false ), "selectNextRow" );

        if( Prefs.getBoolean( "LoadLastMech", false ) ) { LoadVehicleFromFile(Prefs.get("LastOpenCVDirectory", "") + Prefs.get("LastOpenCVFile", "") ); }
        //LoadVehicleFromFile(Prefs.get("LastOpenCVDirectory", "") + Prefs.get("LastOpenCVFile", "") );
        CurVee.SetChanged(false);
    }

    private void GetInfoOn() {
        // throws up a window detailing the current item
        if( CurItem instanceof ifWeapon || CurItem instanceof Ammunition ) {
            dlgWeaponInfo WepInfo = new dlgWeaponInfo( this, true, CurItem );
            WepInfo.setLocationRelativeTo( this );
            WepInfo.setVisible( true );
        } else {
            dlgPlaceableInfo ItemInfo = new dlgPlaceableInfo( this, true, CurItem );
            ItemInfo.setLocationRelativeTo( this );
            ItemInfo.setVisible( true );
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

    private void PPCCapacitor() {
        // if the current item can support a capacitor, adds one on
        if( CurItem instanceof RangedWeapon ) {
            if( ((RangedWeapon) CurItem).IsUsingCapacitor() ) {
                abPlaceable p = ((RangedWeapon) CurItem).GetCapacitor();
                ((RangedWeapon) CurItem).UseCapacitor( false );
                CurVee.GetLoadout().Remove( p );
            } else {
                ((RangedWeapon) CurItem).UseCapacitor( true );
                LocationIndex Loc = CurVee.GetLoadout().FindIndex( CurItem );
                if( Loc.Location != -1 ) {
                    try {
                        CurVee.GetLoadout().Remove(CurItem);
                        CurVee.GetLoadout().AddTo( CurItem, Loc.Location );
                    } catch( Exception e ) {
                        // couldn't allocate the capacitor?  Unallocate the PPC.
                        try {
                            CurVee.GetLoadout().UnallocateAll( CurItem, false );
                        } catch( Exception e1 ) {
                            // failed big.  no problem
                            Media.Messager( this, "Fatal error adding a PPC Capacitor:\n" + e.getMessage() + "\nThe Capacitor will be removed." );
                            ((RangedWeapon) CurItem).UseCapacitor( false );
                        }
                    }
                }
            }
        }
        RefreshSummary();
        RefreshInfoPane();
        RefreshSelectedEquipment();
    }

    private void LaserInsulator() {
        // if the current item can support an insulator, adds one on
        if( CurItem instanceof RangedWeapon ) {
            if( ((RangedWeapon) CurItem).IsUsingInsulator() ) {
                abPlaceable p = ((RangedWeapon) CurItem).GetInsulator();
                ((RangedWeapon) CurItem).UseInsulator( false );
                CurVee.GetLoadout().Remove( p );
            } else {
                ((RangedWeapon) CurItem).UseInsulator( true );
                abPlaceable p = ((RangedWeapon) CurItem).GetInsulator();
                LocationIndex Loc = CurVee.GetLoadout().FindIndex( CurItem );
                if( Loc.Location != -1 ) {
                    try {
                        CurVee.GetLoadout().Remove(CurItem);
                        CurVee.GetLoadout().AddTo( CurItem, Loc.Location );
                    } catch( Exception e ) {
                        // couldn't allocate the insulator?  Unallocate the PPC.
                        try {
                            CurVee.GetLoadout().UnallocateAll( CurItem, false );
                        } catch( Exception e1 ) {
                            // failed big.  no problem
                            Media.Messager( this, "Fatal error adding a Laser Insulator:\n" + e.getMessage() + "\nThe Insulator will be removed." );
                            ((RangedWeapon) CurItem).UseInsulator( false );
                        }
                    }
                }
            }
        }
        RefreshSummary();
        RefreshInfoPane();
        RefreshSelectedEquipment();
    }

    private void PulseModule() {
        // if the current item can support a Pulse Module, adds one on
        if( CurItem instanceof RangedWeapon ) {
            if( ((RangedWeapon) CurItem).IsUsingPulseModule()) {
                abPlaceable p = ((RangedWeapon) CurItem).GetPulseModule();
                ((RangedWeapon) CurItem).UsePulseModule(false );
                CurVee.GetLoadout().Remove( p );
            } else {
                ((RangedWeapon) CurItem).UsePulseModule( true );
                abPlaceable p = ((RangedWeapon) CurItem).GetPulseModule();
                LocationIndex Loc = CurVee.GetLoadout().FindIndex( CurItem );
                if( Loc.Location != -1 ) {
                    try {
                        CurVee.GetLoadout().Remove(CurItem);
                        CurVee.GetLoadout().AddTo( CurItem, Loc.Location );
                    } catch( Exception e ) {
                        // couldn't allocate the insulator?  Unallocate the PPC.
                        try {
                            CurVee.GetLoadout().UnallocateAll( CurItem, false );
                        } catch( Exception e1 ) {
                            // failed big.  no problem
                            Media.Messager( this, "Fatal error adding a Laser Insulator:\n" + e.getMessage() + "\nThe Insulator will be removed." );
                            ((RangedWeapon) CurItem).UseInsulator( false );
                        }
                    }
                }
            }
        }
        RefreshSummary();
        RefreshInfoPane();
        RefreshSelectedEquipment();
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
            ArrayList check = CurVee.GetLoadout().GetNonCore();
            ArrayList replace = new ArrayList();
            abPlaceable p;
            boolean HasOrig = false;
            for (Object o : check) {
                p = (abPlaceable) o;
                if (p instanceof RangedWeapon) {
                    if (((RangedWeapon) p).GetAmmoIndex() == origIDX) {
                        HasOrig = true;
                    }
                }
                if (p instanceof Ammunition) {
                    replace.add(p);
                }
            }

            // replace any ammo with the new stuff if there are no other original weapons
            if( ! HasOrig ) {
                Object[] newammo = data.GetEquipment().GetAmmo( newIDX, CurVee );
                for (Object o : replace) {
                    p = (abPlaceable) o;
                    if (((Ammunition) p).GetAmmoIndex() == origIDX) {
                        CurVee.GetLoadout().Remove(p);
                        if (newammo.length > 0) {
                            p = data.GetEquipment().GetCopy((abPlaceable) newammo[0], CurVee);
                            try {
                                CurVee.GetLoadout().AddTo(p, LocationIndex.CV_LOC_BODY);
                            } catch (Exception ex) {
                                Media.Messager(ex.getMessage());
                            }
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
                dlgVariableSize SetTons = new dlgVariableSize( this, true, (Equipment) CurItem, CurVee );
                SetTons.setLocationRelativeTo( this );
                SetTons.setVisible( true );
                CurVee.GetLoadout().Remove(CurItem);
                try {
                    CurVee.GetLoadout().AddTo(CurItem, LocationIndex.CV_LOC_BODY);
                } catch (Exception ex) {
                    Media.Messager(ex.getMessage());
                }
                RefreshInfoPane();
                RefreshSelectedEquipment();
            }
        }
    }

    private void setViewToolbar(boolean Visible) {
        tlbIconBar.setVisible(Visible);
        Prefs.putBoolean("ViewToolbar", Visible);
        //mnuViewToolbar.setState(Visible);
        if (Visible) {
            if (this.getHeight() != 600) { this.setSize(1280, 600); }
        } else {
            if (this.getHeight() != 575) { this.setSize(1280, 575); }
        }
    }

    private void ConfigureUtilsMenu( java.awt.Component c ) {
        // configures the utilities popup menu
        boolean cap = LegalCapacitor( CurItem ) && CommonTools.IsAllowed( PPCCapAC, CurVee );
        boolean insul = LegalInsulator( CurItem ) && CommonTools.IsAllowed( LIAC, CurVee );
        boolean pulseModule = LegalPulseModule(CurItem) && CommonTools.IsAllowed( PulseModuleAC, CurVee );
        boolean caseless = LegalCaseless( CurItem ) && CommonTools.IsAllowed( CaselessAmmoAC, CurVee );
        boolean lotchange = LegalLotChange( CurItem );
        boolean dumper = LegalDumper( CurItem );
        mnuAddCapacitor.setEnabled( cap );
        mnuAddInsulator.setEnabled( insul );
        mnuAddPulseModule.setEnabled(pulseModule);
        mnuCaseless.setEnabled( caseless );
        mnuAddCapacitor.setVisible( cap );
        mnuAddInsulator.setVisible( insul );
        mnuAddPulseModule.setVisible(pulseModule);
        mnuCaseless.setVisible( caseless );
        mnuSetLotSize.setVisible( lotchange );
        mnuDumper.setVisible( dumper );
        if( cap || insul || caseless || pulseModule ) {
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
                if( ((RangedWeapon) CurItem).IsUsingPulseModule()) {
                    mnuAddPulseModule.setText( "Remove RISC Pulse Module" );
                } else {
                    mnuAddPulseModule.setText( "Add RISC Pulse Module" );
                }
                if( ((RangedWeapon) CurItem).IsCaseless() ) {
                    mnuCaseless.setText( "Switch from Caseless" );
                } else {
                    mnuCaseless.setText( "Switch to Caseless" );
                }
            }
        }
        mnuVGLAmmo.setVisible( false );
        mnuVGLArc.setVisible( false );
        if( CurVee.GetLoadout().Find( CurItem ) < 11 ) {
            if( CurItem instanceof EmptyItem ) {
                mnuUnallocateAll.setText( "Unallocate All" );
                mnuUnallocateAll.setEnabled( false );
            } else if( ! CurItem.LocationLocked() ) {
                mnuUnallocateAll.setText( "Unallocate All" );
                mnuUnallocateAll.setEnabled( true );
            } else {
                mnuUnallocateAll.setText( "Unallocate All" );
                mnuUnallocateAll.setEnabled( false );
            }
        }
        if( CurItem instanceof Equipment ) {
            mnuSetVariable.setVisible(((Equipment) CurItem).IsVariableSize());
        } else {
            mnuSetVariable.setVisible( false );
        }
        mnuRemoveItem.setEnabled(!CurItem.CoreComponent() && !CurItem.LocationLinked());
    }

    private void RemoveItemCritTab() {
        if( ! CurItem.CoreComponent() && CurItem.Contiguous() ) {
            CurVee.GetLoadout().Remove( CurItem );

            // refresh the selected equipment listbox
            if(CurVee.GetLoadout().GetNonCore().toArray().length == 0) {
                Equipment[SELECTED] = new Object[] { " " };
            } else {
                Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
            }
            lstSelectedEquipment.setListData( Equipment[SELECTED] );

            // Check the targeting computer if needed
            if( CurVee.UsingTC() ) {
                CurVee.UnallocateTC();
            }

            // refresh the ammunition display
            ResetAmmo();

            // now refresh the information panes
            RefreshSummary();
            RefreshInfoPane();
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

    public boolean LegalPulseModule( abPlaceable p ) {
        if( ! ( p instanceof RangedWeapon ) ) { return false; }
        return ((RangedWeapon) p).CanUsePulseModule();
    }

    public boolean LegalCaseless( abPlaceable p ) {
        if( ! ( p instanceof RangedWeapon ) ) { return false; }
        return ((RangedWeapon) p).CanUseCaselessAmmo();
    }

    public boolean LegalTurretMount( abPlaceable p ) {
        return false;
    }

    public boolean LegalLotChange( abPlaceable p ) {
        if( ! ( p instanceof Ammunition ) ) { return false; }
        return CurVee.UsingFractionalAccounting();
    }

    public boolean LegalDumper( abPlaceable p ) {
        if ( ! ( p instanceof Equipment ) ) { return false; }
        return p.CritName().equals("Cargo Container");
    }

    public void RefreshInfoPane() {
        // refreshes the information pane at the bottom of the screen
        // set the colors
        if( CurVee.GetCurrentTons() > CurVee.GetTonnage() ) {
            txtInfoTonnage.setForeground( Color.RED );
            txtInfoFreeTons.setForeground( Color.RED );
        } else {
            txtInfoTonnage.setForeground( Color.BLACK );
            txtInfoFreeTons.setForeground( Color.BLACK );
        }

        if ( CurVee.GetAvailableSlots() < 0 ) {
            txtInfoFreeCrits.setForeground(Color.red);
        } else {
            txtInfoFreeCrits.setForeground(Color.black);
        }

        // fill in the movement summary
        String temp = "Max C/F: ";
        temp += CurVee.GetAdjustedCruiseMP( false, true ) + "/";
        temp += CurVee.GetAdjustedFlankMP( false, true );
        lblMoveSummary.setText( temp );

        // fill in the info
        if( CurVee.UsingFractionalAccounting() ) {
            txtInfoTonnage.setText( "Tons: " + CommonTools.RoundFractionalTons( CurVee.GetCurrentTons() ) );
            txtInfoFreeTons.setText( "Free Tons: " + CommonTools.RoundFractionalTons( CurVee.GetTonnage() - CurVee.GetCurrentTons() ) );
        } else {
            txtInfoTonnage.setText( "Tons: " + CurVee.GetCurrentTons() );
            txtInfoFreeTons.setText( "Free Tons: " + ( CurVee.GetTonnage() - CurVee.GetCurrentTons() ) );
        }
        txtInfoBattleValue.setText( "BV: " + String.format( "%1$,d", CurVee.GetCurrentBV() ) );
        txtInfoCost.setText( "Cost: " + String.format( "%1$,.0f", Math.floor( CurVee.GetTotalCost() + 0.5f ) ) );

        javax.swing.table.AbstractTableModel m = (javax.swing.table.AbstractTableModel) tblWeaponManufacturers.getModel();
        m.fireTableDataChanged();
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
        clipboard.setContents( export, this);
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
        clipboard.setContents( export, this);
    }

    private void FluffPaste( Component c ) {
        // ensure we have the correct data flavor from the clipboard
        char space = 20;
        char linereturn = 13;
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        String txtimport;
        try {
            txtimport = (String) clipboard.getData( DataFlavor.stringFlavor );
            txtimport.replace(linereturn, space);
        } catch( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
            return;
        }
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

    private void ResetAmmo() {
        // first, get the weapons from the loadout that need ammunition
        ArrayList v = CurVee.GetLoadout().GetNonCore(), wep = new ArrayList();
        Object a;

        for (Object o : v) {
            a = o;
            if (a instanceof ifWeapon) {
                if (((ifWeapon) a).HasAmmo()) {
                    wep.add(a);
                }
            } else if (a instanceof Equipment) {
                if (((Equipment) a).HasAmmo()) {
                    wep.add(a);
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
            result = data.GetEquipment().GetAmmo( key, CurVee );
        }

        // put the results into the chooser
        Equipment[AMMUNITION] = result;
        lstChooseAmmunition.setListData( result );
        lstChooseAmmunition.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        tblQuirks = new JTable();
        JPanel pnlInfoPane = new JPanel();
        txtInfoTonnage = new javax.swing.JTextField();
        txtInfoFreeTons = new javax.swing.JTextField();
        txtInfoFreeCrits = new javax.swing.JTextField();
        txtTurretInfo = new javax.swing.JTextField();
        txtInfoBattleValue = new javax.swing.JTextField();
        txtInfoCost = new javax.swing.JTextField();
        tlbIconBar = new javax.swing.JToolBar();
        JLabel lblSelectVariant = new JLabel("Selected Variant: ");
        cmbOmniVariant = new javax.swing.JComboBox();
        tbpMainTabPane = new javax.swing.JTabbedPane();
        pnlBasicSetup = new javax.swing.JPanel();
        JPanel jPanel5 = new JPanel();
        JLabel jLabel1 = new JLabel("Vehicle Name:");
        txtVehicleName = new javax.swing.JTextField();
        JLabel jLabel4 = new JLabel("Model:");
        txtModel = new javax.swing.JTextField();
        JLabel jLabel2 = new JLabel("Rules Level:");
        cmbRulesLevel = new javax.swing.JComboBox();
        JLabel jLabel5 = new JLabel("Era:");
        cmbEra = new javax.swing.JComboBox();
        JLabel jLabel3 = new JLabel("Tech Base:");
        cmbTechBase = new javax.swing.JComboBox();
        lblEraYears = new javax.swing.JLabel("2443~2800");
        chkYearRestrict = new javax.swing.JCheckBox("Restrict Availability by Year");
        JLabel jLabel81 = new JLabel("Source:");
        txtSource = new javax.swing.JTextField();
        JLabel lblProdYear = new JLabel("Prod Year/Era:");
        txtProdYear = new javax.swing.JTextField();
        cmbProductionEra = new javax.swing.JComboBox();
        JPanel pnlChassis = new JPanel();
        cmbMotiveType = new javax.swing.JComboBox();
        JLabel jLabel7 = new JLabel("Motive Type:");
        JLabel jLabel8 = new JLabel("Tonnage:");
        chkOmniVee = new javax.swing.JCheckBox("OmniVehicle");
        chkTrailer = new javax.swing.JCheckBox("Trailer");
        spnTonnage = new javax.swing.JSpinner();
        JLabel jLabel9 = new JLabel("Engine:");
        cmbEngineType = new javax.swing.JComboBox();
        JLabel jLabel32 = new JLabel("Turret:");
        cmbTurret = new javax.swing.JComboBox();
        lblVeeClass = new javax.swing.JLabel("Assault Vee");
        lblVeeLimits = new javax.swing.JLabel("500t Max");
        JLabel jLabel91 = new JLabel("Heat Sinks:");
        spnHeatSinks = new javax.swing.JSpinner();
        spnTurretTonnage = new javax.swing.JSpinner();
        spnRearTurretTonnage = new javax.swing.JSpinner();
        JPanel pnlMovement = new JPanel();
        JLabel jLabel10 = new JLabel("Cruise MP:");
        spnCruiseMP = new javax.swing.JSpinner();
        JLabel jLabel11 = new JLabel("Flank MP:");
        lblFlankMP = new javax.swing.JLabel("2");
        JLabel jLabel13 = new JLabel("Jump MP:");
        spnJumpMP = new javax.swing.JSpinner();
        lblMoveSummary = new javax.swing.JLabel("Max C/F: 12/20");
        JPanel pnlChassisMods = new JPanel();
        chkFlotationHull = new javax.swing.JCheckBox("Flotation Hull");
        chkLimitedAmph = new javax.swing.JCheckBox("Limited Amphibious");
        chkFullAmph = new javax.swing.JCheckBox("Fully Amphibious");
        chkDuneBuggy = new javax.swing.JCheckBox("Dune Buggy");
        chkEnviroSealing = new javax.swing.JCheckBox("Enviro (Vacuum) Sealing");
        JPanel pnlExperimental = new JPanel();
        chkArmoredMotive = new javax.swing.JCheckBox("Armored Motive System");
        chkCommandConsole = new javax.swing.JCheckBox("Command Console");
        chkEscapePod = new javax.swing.JCheckBox("Combat Vehicle Escape Pod");
        chkMinesweeper = new javax.swing.JCheckBox("Minesweeper");
        chkJetBooster = new javax.swing.JCheckBox("VTOL Jet Booster");
        chkSupercharger = new javax.swing.JCheckBox("Supercharger");
        chkSponsonTurret = new javax.swing.JCheckBox("Sponson Turret");
        JPanel jPanel11 = new JPanel();
        chkFractional = new javax.swing.JCheckBox("Use Fractional Accounting");
        JPanel pnlSummary = new JPanel();
        txtSumIntTons = Utils.summaryField("000.00");;
        txtSumIntAV = Utils.summaryField("X/X-X-X");
        txtSumEngTons = Utils.summaryField("000.00");
        txtSumEngAV = Utils.summaryField("X/X-X-X");
        txtSumLifTons = Utils.summaryField("000.00");
        txtSumLifAV = Utils.summaryField("X/X-X-X");
        txtSumEngSpace = Utils.summaryField("00");
        txtSumConTons = Utils.summaryField("000.00");
        txtSumConAV = Utils.summaryField("X/X-X-X");
        txtSumJJTons = Utils.summaryField("000.00");
        JTextField txtSumJJSpace = Utils.summaryField("00");
        txtSumJJAV = Utils.summaryField("X/X-X-X");
        txtSumHSTons = Utils.summaryField("000.00");
        txtSumHSAV = Utils.summaryField("X/X-X-X");
        txtSumArmTons = Utils.summaryField("000.00");
        txtSumArmSpace = Utils.summaryField("00");
        txtSumArmAV = Utils.summaryField("X/X-X-X");
        txtSumTurTons = Utils.summaryField("000.00");
        txtSumTurAV = Utils.summaryField("X/X-X-X");
        txtSumRTuTons = Utils.summaryField("000.00");
        txtSumRTuAV = Utils.summaryField("X/X-X-X");
        txtSumSpnTons = Utils.summaryField("000.00");
        txtSumSpnAV = Utils.summaryField("X/X-X-X");
        txtSumPATons = Utils.summaryField("000.00");
        txtSumPAAV = Utils.summaryField("X/X-X-X");
        JPanel pnlInformation = new JPanel();
        JLabel titleSuspension = new JLabel("Suspension Factor:");
        lblSupensionFacter = new javax.swing.JLabel("000");
        JLabel titleMinEngTon = new JLabel("Minimum Engine Tonnage:");
        lblMinEngineTons = new javax.swing.JLabel("10");
        JLabel titleBaseEngRate = new JLabel("Base Engine Rating:");
        lblBaseEngineRating = new javax.swing.JLabel("000");
        JLabel titleFinalEngRate = new JLabel("Final Engine Rating:");
        lblFinalEngineRating = new javax.swing.JLabel("000");
        JLabel titleFreeHeatSinks = new JLabel("Free Heat Sinks:");
        lblFreeHeatSinks = new javax.swing.JLabel("000");
        JLabel titleCrew = new JLabel("Crew:");
        lblNumCrew = new javax.swing.JLabel("00");
        JPanel pnlOmniInfo = new JPanel();
        btnLockChassis = new javax.swing.JButton("Lock Chassis");
        btnAddVariant = new javax.swing.JButton("Add Variant");
        btnDeleteVariant = new javax.swing.JButton("Delete Variant");
        btnRenameVariant = new javax.swing.JButton("Rename Variant");
        JPanel pnlArmorLocations = new JPanel();
        JPanel pnlRightArmor = new JPanel();
        lblRightIntPts = new javax.swing.JLabel("00");
        spnRightArmor = new javax.swing.JSpinner();
        JPanel pnlFrontArmor = new JPanel();
        lblFrontIntPts = new javax.swing.JLabel("00");
        spnFrontArmor = new javax.swing.JSpinner();
        JPanel pnlLeftArmor = new JPanel();
        lblLeftIntPts = new javax.swing.JLabel("00");
        spnLeftArmor = new javax.swing.JSpinner();
        JPanel pnlRearArmor = new JPanel();
        lblRearIntPts = new javax.swing.JLabel("00");
        spnRearArmor = new javax.swing.JSpinner();
        pnlTurretArmor = new javax.swing.JPanel();
        lblTurretIntPts = new javax.swing.JLabel("00");
        spnTurretArmor = new javax.swing.JSpinner();
        pnlRearTurretArmor = new javax.swing.JPanel();
        lblRearTurretIntPts = new javax.swing.JLabel("00");
        spnRearTurretArmor = new javax.swing.JSpinner();
        pnlRotorArmor = new javax.swing.JPanel();
        lblRotorIntPts = new javax.swing.JLabel("00");
        spnRotorArmor = new javax.swing.JSpinner();
        JPanel jPanel7 = new JPanel();
        JLabel jLabel52 = new JLabel("Armor Type:");
        cmbArmorType = new javax.swing.JComboBox();
        chkBalanceLRArmor = new javax.swing.JCheckBox("Balance Left/Right Armor");
        chkBalanceFRArmor = new javax.swing.JCheckBox("Balance Front/Rear Armor");
        btnSetArmorTons = new javax.swing.JButton("Set Armor Tonnage");
        btnUseRemaining = new javax.swing.JButton("Use Remaining Tonnage");
        btnMaximize = new javax.swing.JButton("Maximize Armor");
        JPanel jPanel8 = new JPanel();
        JLabel jLabel34 = new JLabel("Tons");
        JLabel jLabel36 = new JLabel("Space");
        lblArmorTotals = new javax.swing.JLabel("999 of 999 Armor Points");
        lblArmorCoverage = new javax.swing.JLabel("100.00% Coverage");
        txtArmorTons = new javax.swing.JTextField("000.00");
        txtArmorSpace = new javax.swing.JTextField("00");
        lblArmorTonsWasted = new javax.swing.JLabel("0.00 Tons Wasted");
        lblArmorLeftInLot = new javax.swing.JLabel("99 Points Left In This 1/2 Ton Lot");
        JPanel pnlEquipment = new JPanel();
        pnlEquipInfo = new javax.swing.JPanel();
        lblInfoAVSL = new javax.swing.JLabel();
        lblInfoAVSW = new javax.swing.JLabel();
        lblInfoAVCI = new javax.swing.JLabel();
        lblInfoIntro = new javax.swing.JLabel();
        lblInfoExtinct = new javax.swing.JLabel();
        lblInfoReintro = new javax.swing.JLabel();
        JLabel lblInfoName = new JLabel();
        lblInfoType = new javax.swing.JLabel();
        lblInfoHeat = new javax.swing.JLabel();
        lblInfoDamage = new javax.swing.JLabel();
        lblInfoRange = new javax.swing.JLabel();
        lblInfoAmmo = new javax.swing.JLabel();
        lblInfoTonnage = new javax.swing.JLabel();
        lblInfoCrits = new javax.swing.JLabel();
        lblInfoSpecials = new javax.swing.JLabel();
        lblInfoCost = new javax.swing.JLabel();
        lblInfoBV = new javax.swing.JLabel();
        lblMMNameInfo = new javax.swing.JLabel();
        lblInfoMountRestrict = new javax.swing.JLabel();
        lblInfoRulesLevel = new javax.swing.JLabel();
        lblInfoAVDA = new javax.swing.JLabel();
        JPanel pnlSpecials = new JPanel();
        chkUseTC = new javax.swing.JCheckBox("Targeting Computer");
        chkFCSAIV = new javax.swing.JCheckBox("Use Artemis IV");
        chkFCSAV = new javax.swing.JCheckBox("Use Artemis V");
        chkFCSApollo = new javax.swing.JCheckBox("Use MRM Apollo");
        chkCASE = new javax.swing.JCheckBox("Use CASE");
        JPanel pnlControls = new JPanel();
        JButton btnRemoveEquip = new JButton("<<");
        JButton btnClearEquip = new JButton("Clear");
        JButton btnAddEquip = new JButton(">>");
        cmbNumEquips = new javax.swing.JComboBox(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" });
        JScrollPane scrLocations = new JScrollPane();
        cmbLocation = new javax.swing.JList();
        JPanel pnlSelected = new JPanel();
        JScrollPane scrSelectedEquip = new JScrollPane();
        lstSelectedEquipment = new javax.swing.JList();
        tbpWeaponChooser = new javax.swing.JTabbedPane();
        lstChooseBallistic = new javax.swing.JList();
        lstChooseEnergy = new javax.swing.JList();
        lstChooseMissile = new javax.swing.JList();
        lstChoosePhysical = new javax.swing.JList();
        lstChooseEquipment = new javax.swing.JList();
        lstChooseArtillery = new javax.swing.JList();
        lstChooseAmmunition = new javax.swing.JList();
        JPanel pnlFluff = new JPanel();
        JPanel pnlExport = new JPanel();
        JButton btnExportTXT = new JButton("to TXT");
        JButton btnExportHTML = new JButton("to HTML");
        JButton btnExportMTF = new JButton("to MegaMek");
        JTabbedPane tbpFluffEditors = new JTabbedPane();
        pnlOverview = new javax.swing.JPanel();
        pnlCapabilities = new javax.swing.JPanel();
        pnlHistory = new javax.swing.JPanel();
        pnlDeployment = new javax.swing.JPanel();
        pnlVariants = new javax.swing.JPanel();
        pnlNotables = new javax.swing.JPanel();
        pnlAdditionalFluff = new javax.swing.JPanel();
        JPanel pnlManufacturers = new JPanel();
        JLabel lblManuInfo = new JLabel();
        txtLog = new javax.swing.JTextArea();
        txtManufacturer = new javax.swing.JTextField();
        txtEngineManufacturer = new javax.swing.JTextField();
        txtArmorModel = new javax.swing.JTextField();
        txtChassisModel = new javax.swing.JTextField();
        txtCommSystem = new javax.swing.JTextField();
        txtTNTSystem = new javax.swing.JTextField();
        JPanel pnlWeaponsManufacturers = new JPanel();
        chkIndividualWeapons = new javax.swing.JCheckBox();
        JScrollPane scpWeaponManufacturers = new JScrollPane();
        tblWeaponManufacturers = new javax.swing.JTable();
        txtManufacturerLocation = new javax.swing.JTextField();
        txtJJModel = new javax.swing.JTextField();
        JPanel pnlQuirks = new JPanel();
        JLabel lblBattleMechQuirks = new JLabel("Quirks");
        JScrollPane scpQuirkTable = new JScrollPane();
        JButton btnAddQuirk = new JButton("Manage Quirks");
        JPanel pnlBFStats = new JPanel();
        JLabel jLabel70 = new JLabel("MV");
        JLabel jLabel71 = new JLabel("S (+0)");
        JLabel jLabel72 = new JLabel("M (+2)");
        JLabel jLabel73 = new JLabel("L (+4)");
        JLabel jLabel74 = new JLabel("E (+6)");
        JLabel jLabel75 = new JLabel("Wt.");
        JLabel jLabel76 = new JLabel("OV");
        JLabel jLabel77 = new JLabel("Armor:");
        JLabel jLabel78 = new JLabel("Structure:");
        JLabel jLabel79 = new JLabel("Special Abilities:");
        lblBFMV = new javax.swing.JLabel("0");
        lblBFWt = new javax.swing.JLabel("1");
        lblBFOV = new javax.swing.JLabel("0");
        lblBFExtreme = new javax.swing.JLabel("0");
        lblBFShort = new javax.swing.JLabel("0");
        lblBFMedium = new javax.swing.JLabel("0");
        lblBFLong = new javax.swing.JLabel("0");
        lblBFArmor = new javax.swing.JLabel("0");
        lblBFStructure = new javax.swing.JLabel("0");
        lblBFSA = new javax.swing.JLabel("Placeholder");
        JLabel jLabel80 = new JLabel("Points:");
        lblBFPoints = new javax.swing.JLabel("0");
        JPanel pnlConversionSteps = new JPanel();
        JScrollPane scpBFConversion = new JScrollPane();
        jTextAreaBFConversion = new javax.swing.JTextArea();
        JPanel pnlImage = new JPanel();
        lblFluffImage = new javax.swing.JLabel();
        JPanel pnlImageButtons = new JPanel();
        JButton btnLoadImage = new JButton("Load Image");
        JButton btnClearImage = new JButton("Clear Image");
        JMenuBar jMenuBar1 = new JMenuBar();
        JMenu mnuFile = new JMenu("File");
        JMenu mnuImport = new JMenu("Import...");
        JMenuItem mnuSaveAs = new JMenuItem("Save As...");
        JMenu mnuExport = new JMenu("Export As...");
        JMenu mnuClearFluff = new JMenu("Tools");
        mnuViewToolbar = new javax.swing.JCheckBoxMenuItem("View Toolbar");
        mnuUnlock = new javax.swing.JMenuItem("Unlock Chassis");
        JMenu mnuHelp = new JMenu("Help");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new Dimension(1600, 900));
        setMinimumSize(new Dimension(1280, 650));
        setResizable(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        //region Status Bar
        txtInfoTonnage.setEditable(false);
        txtInfoTonnage.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoTonnage.setText("Tonnage: 000.00");
        txtInfoTonnage.setMaximumSize(new Dimension(110, 20));
        txtInfoTonnage.setMinimumSize(new Dimension(110, 20));
        txtInfoTonnage.setPreferredSize(new Dimension(110, 20));
        pnlInfoPane.add(txtInfoTonnage);

        txtInfoFreeTons.setEditable(false);
        txtInfoFreeTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoFreeTons.setText("Free Tons: 000.00");
        txtInfoFreeTons.setMaximumSize(new Dimension(115, 20));
        txtInfoFreeTons.setMinimumSize(new Dimension(115, 20));
        txtInfoFreeTons.setPreferredSize(new Dimension(115, 20));
        pnlInfoPane.add(txtInfoFreeTons);

        txtInfoFreeCrits.setEditable(false);
        txtInfoFreeCrits.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoFreeCrits.setText("Space: 00");
        txtInfoFreeCrits.setMaximumSize(new Dimension(65, 20));
        txtInfoFreeCrits.setMinimumSize(new Dimension(65, 20));
        txtInfoFreeCrits.setPreferredSize(new Dimension(65, 20));
        pnlInfoPane.add(txtInfoFreeCrits);

        txtTurretInfo.setEditable(false);
        txtTurretInfo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTurretInfo.setText("Turret: 000.00");
        txtTurretInfo.setMaximumSize(new Dimension(120, 20));
        txtTurretInfo.setMinimumSize(new Dimension(120, 20));
        txtTurretInfo.setPreferredSize(new Dimension(100, 20));
        pnlInfoPane.add(txtTurretInfo);

        txtInfoBattleValue.setEditable(false);
        txtInfoBattleValue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoBattleValue.setText("BV: 00,000");
        txtInfoBattleValue.setMaximumSize(new Dimension(75, 20));
        txtInfoBattleValue.setMinimumSize(new Dimension(75, 20));
        txtInfoBattleValue.setPreferredSize(new Dimension(75, 20));
        pnlInfoPane.add(txtInfoBattleValue);

        txtInfoCost.setEditable(false);
        txtInfoCost.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInfoCost.setText("Cost: 000,000,000,000.00");
        txtInfoCost.setMaximumSize(new Dimension(165, 20));
        txtInfoCost.setMinimumSize(new Dimension(165, 20));
        txtInfoCost.setPreferredSize(new Dimension(165, 20));
        pnlInfoPane.add(txtInfoCost);
        //endregion

        //region Icon Bar
        tlbIconBar.setFloatable(false);
        tlbIconBar.setRollover(true);
        tlbIconBar.add(Utils.imageButton("New Unit", this::btnNewVeeActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/document--plus.png"))));
        tlbIconBar.add(Utils.imageButton("Open Unit", this::btnOpenActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/folder-open-document.png"))));
        tlbIconBar.add(Utils.imageButton("Save Unit", this::btnSaveActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/disk-black.png"))));
        tlbIconBar.add(Utils.vertSeparator());
        tlbIconBar.add(Utils.imageButton("Print", this::btnPrintActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/printer.png"))));
        tlbIconBar.add(Utils.vertSeparator());
        tlbIconBar.add(Utils.imageButton("Export Text to Clipboard", this::btnExportClipboardIconActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/document-clipboard.png"))));
        tlbIconBar.add(Utils.imageButton("Export HTML", this::btnExportHTMLIconActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/document-image.png"))));
        tlbIconBar.add(Utils.imageButton("Export Text", this::btnExportTextIconActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/document-text.png"))));
        tlbIconBar.add(Utils.imageButton("Export MTF", this::btnExportMTFIconActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/document--arrow.png"))));
        tlbIconBar.add(Utils.imageButton("Copy Chat Line", this::btnChatInfoActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/balloon.png"))));
        tlbIconBar.add(Utils.vertSeparator());
        tlbIconBar.add(Utils.imageButton("Add  to Force List", this::btnAddToForceListActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/clipboard--plus.png"))));
        tlbIconBar.add(Utils.imageButton("Force List", this::btnForceListActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/clipboard.png"))));
        tlbIconBar.add(Utils.vertSeparator());
        tlbIconBar.add(Utils.imageButton("Preferences", this::btnOptionsActionPerformed, new javax.swing.ImageIcon(getClass().getResource("/saw/images/gear.png"))));
        tlbIconBar.add(Utils.vertSeparator());
        lblSelectVariant.setEnabled(false);
        tlbIconBar.add(lblSelectVariant);

        cmbOmniVariant.setEnabled(false);
        cmbOmniVariant.setMaximumSize(new Dimension(150, 20));
        cmbOmniVariant.setMinimumSize(new Dimension(150, 20));
        cmbOmniVariant.setPreferredSize(new Dimension(150, 20));
        cmbOmniVariant.addActionListener(this::cmbOmniVariantActionPerformed);
        tlbIconBar.add(cmbOmniVariant);
        //endregion

        //region Basic Setup Tab / Basic Information Panel
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Basic Information"));

        txtVehicleName.setMinimumSize(new Dimension(150, 20));
        txtVehicleName.setPreferredSize(new Dimension(150, 20));

        txtModel.setMinimumSize(new Dimension(150, 20));
        txtModel.setPreferredSize(new Dimension(150, 20));

        cmbRulesLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Introductory", "Tournament Legal", "Advanced", "Experimental" }));
        cmbRulesLevel.setSelectedIndex(1);
        cmbRulesLevel.setMinimumSize(new Dimension(150, 20));
        cmbRulesLevel.setPreferredSize(new Dimension(150, 20));
        cmbRulesLevel.addActionListener(this::cmbRulesLevelActionPerformed);

        cmbEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Age of War/Star League", "Succession Wars", "Clan Invasion", "Dark Ages", "All Eras (non-canon)" }));
        cmbEra.setSelectedIndex(1);
        cmbEra.setMinimumSize(new Dimension(150, 20));
        cmbEra.setPreferredSize(new Dimension(150, 20));
        cmbEra.addActionListener(this::cmbEraActionPerformed);

        cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan", "Mixed Tech" }));
        cmbTechBase.setMinimumSize(new Dimension(150, 20));
        cmbTechBase.setPreferredSize(new Dimension(150, 20));
        cmbTechBase.addActionListener(this::cmbTechBaseActionPerformed);

        chkYearRestrict.addActionListener(this::chkYearRestrictActionPerformed);

        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        txtSource.setMinimumSize(new Dimension(150, 20));
        txtSource.setPreferredSize(new Dimension(150, 20));

        txtProdYear.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtProdYear.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtProdYear.setMaximumSize(new Dimension(60, 20));
        txtProdYear.setMinimumSize(new Dimension(60, 20));
        txtProdYear.setPreferredSize(new Dimension(60, 20));

        cmbProductionEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Age of War", "Star League", "Early Succession War", "LSW - LosTech", "LSW - Renaissance", "Clan Invasion", "Civil War", "Jihad", "Early Republic", "Late Republic", "Dark Ages", "ilClan" }));
        cmbProductionEra.setMaximumSize(new Dimension(90, 20));
        cmbProductionEra.setMinimumSize(new Dimension(90, 20));
        cmbProductionEra.setPreferredSize(new Dimension(90, 20));
        cmbProductionEra.addActionListener(this::cmbProductionEraActionPerformed);

        GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel2)
                        .addGap(2, 2, 2)
                        .addComponent(cmbRulesLevel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel5)
                        .addGap(2, 2, 2)
                        .addComponent(cmbEra, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel3)
                        .addGap(2, 2, 2)
                        .addComponent(cmbTechBase, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(lblEraYears))
                    .addGroup(GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel81, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, GroupLayout.Alignment.TRAILING))
                        .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVehicleName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addComponent(txtSource, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtModel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(24, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lblProdYear)
                        .addGap(2, 2, 2)
                        .addComponent(txtProdYear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(cmbProductionEra, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkYearRestrict))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel1))
                    .addComponent(txtVehicleName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel4))
                    .addComponent(txtModel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(txtSource, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel81)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2))
                    .addComponent(cmbRulesLevel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel5))
                    .addComponent(cmbEra, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addComponent(cmbTechBase, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(lblEraYears)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(lblProdYear))
                    .addComponent(txtProdYear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbProductionEra, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkYearRestrict)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
        //endregion

        //region Basic Setup Tab / Chassis Panel
        pnlChassis.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Chassis"));

        cmbMotiveType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hovercraft", "Naval (Displacement)", "Naval (Hydrofoil)", "Naval (Submarine)", "Tracked", "VTOL", "Wheeled", "WiGE", "Hovercraft (Super Heavy)", "Displacement (Super Heavy)" }));
        cmbMotiveType.setMinimumSize(new Dimension(150, 20));
        cmbMotiveType.setPreferredSize(new Dimension(150, 20));
        cmbMotiveType.addActionListener(this::cmbMotiveTypeActionPerformed);

        chkOmniVee.addActionListener(this::chkOmniVeeActionPerformed);

        chkTrailer.setEnabled(false);
        chkTrailer.addActionListener(this::chkTrailerActionPerformed);

        spnTonnage.setModel(new javax.swing.SpinnerNumberModel(10, 1, null, 1));
        spnTonnage.setMinimumSize(new Dimension(45, 20));
        spnTonnage.setPreferredSize(new Dimension(45, 20));
        spnTonnage.addChangeListener(this::spnTonnageStateChanged);
        spnTonnage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                spnTonnageFocusGained(evt);
            }
        });
        spnTonnage.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                spnTonnageInputMethodTextChanged(evt);
            }
        });

        cmbEngineType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "I.C.E.", "Fusion", "Light Fusion", "XL Fusion", "Compact Fusion" }));
        cmbEngineType.setMinimumSize(new Dimension(150, 20));
        cmbEngineType.setPreferredSize(new Dimension(150, 20));
        cmbEngineType.addActionListener(this::cmbEngineTypeActionPerformed);

        cmbTurret.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Turret", "Single Turret", "Dual Turret", "Sponson Turrets", "Chin Turret", "Mast Turret" }));
        cmbTurret.addActionListener(this::cmbTurretActionPerformed);

        lblVeeClass.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        spnHeatSinks.setModel(new javax.swing.SpinnerNumberModel(10, 1, null, 1));
        spnHeatSinks.setMinimumSize(new Dimension(45, 20));
        spnHeatSinks.setPreferredSize(new Dimension(45, 20));
        spnHeatSinks.addChangeListener(this::spnHeatSinksStateChanged);
        spnHeatSinks.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                spnHeatSinksFocusGained(evt);
            }
        });
        spnHeatSinks.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                spnHeatSinksInputMethodTextChanged(evt);
            }
        });

        spnTurretTonnage.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 50.0d, 0.5d));
        spnTurretTonnage.setEnabled(false);
        spnTurretTonnage.addChangeListener(this::spnTurretTonnageStateChanged);
        ((JSpinner.DefaultEditor)spnTurretTonnage.getEditor()).getTextField().addFocusListener(spinners);

        spnRearTurretTonnage.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 50.0d, 0.5d));
        spnRearTurretTonnage.setEnabled(false);
        spnRearTurretTonnage.addChangeListener(this::spnRearTurretTonnageStateChanged);
        ((JSpinner.DefaultEditor)spnRearTurretTonnage.getEditor()).getTextField().addFocusListener(spinners);

            JLabel lblTurretTonnage = new JLabel("Turret Tonnage: ");

        GroupLayout pnlChassisLayout = new GroupLayout(pnlChassis);
        pnlChassis.setLayout(pnlChassisLayout);
        pnlChassisLayout.setHorizontalGroup(
            pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addGroup(pnlChassisLayout.createSequentialGroup()
                    .addComponent(jLabel7)
                    .addGap(2, 2, 2)
                    .addComponent(cmbMotiveType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlChassisLayout.createSequentialGroup()
                    .addGap(65, 65, 65)
                    .addComponent(chkOmniVee))
                .addGroup(pnlChassisLayout.createSequentialGroup()
                    .addGap(65, 65, 65)
                    .addComponent(chkTrailer))
                .addGroup(pnlChassisLayout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addGroup(pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addGroup(pnlChassisLayout.createSequentialGroup()
                            .addComponent(jLabel32)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbTurret, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))
                        .addGroup(pnlChassisLayout.createSequentialGroup()
                                .addComponent(lblTurretTonnage)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnTurretTonnage)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnRearTurretTonnage))
                        .addGroup(pnlChassisLayout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addGap(2, 2, 2)
                            .addComponent(cmbEngineType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                .addGroup(pnlChassisLayout.createSequentialGroup()
                    .addGap(17, 17, 17)
                    .addComponent(jLabel8)
                    .addGap(2, 2, 2)
                    .addComponent(spnTonnage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblVeeClass)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblVeeLimits)))
            .addGroup(pnlChassisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel91)
                .addGap(2, 2, 2)
                .addComponent(spnHeatSinks, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        pnlChassisLayout.setVerticalGroup(
            pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlChassisLayout.createSequentialGroup()
                .addGroup(pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlChassisLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel7))
                    .addComponent(cmbMotiveType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(spnTonnage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlChassisLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblVeeClass)
                                .addComponent(lblVeeLimits)))))
                .addComponent(chkOmniVee)
                .addGap(0, 0, 0)
                .addComponent(chkTrailer)
                .addGap(2, 2, 2)
                .addGroup(pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlChassisLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel9))
                    .addComponent(cmbEngineType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(cmbTurret, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(lblTurretTonnage)
                        .addComponent(spnTurretTonnage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(spnRearTurretTonnage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlChassisLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlChassisLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel91))
                    .addComponent(spnHeatSinks, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        //endregion

        //region Basic Setup Tab / Movement Panel
        pnlMovement.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Movement"));
        spnCruiseMP.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        spnCruiseMP.setPreferredSize(new Dimension(45, 20));
        spnCruiseMP.addChangeListener(this::spnCruiseMPStateChanged);
        spnCruiseMP.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                spnCruiseMPInputMethodTextChanged(evt);
            }
        });

        spnJumpMP.setEnabled(false);
        spnJumpMP.setPreferredSize(new Dimension(45, 20));

        lblMoveSummary.setHorizontalAlignment(SwingConstants.RIGHT);

        GroupLayout pnlMovementLayout = new GroupLayout(pnlMovement);
        pnlMovement.setLayout(pnlMovementLayout);
        pnlMovementLayout.setHorizontalGroup(
            pnlMovementLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlMovementLayout.createSequentialGroup()
                .addGap(29)
                .addGroup(pnlMovementLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMovementLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(5)
                        .addComponent(spnCruiseMP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlMovementLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(18)
                        .addComponent(lblFlankMP)
                        .addGap(20)
                        .addComponent(lblMoveSummary))
                    .addGroup(pnlMovementLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(10)
                        .addComponent(spnJumpMP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
        );
        pnlMovementLayout.setVerticalGroup(
            pnlMovementLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlMovementLayout.createSequentialGroup()
                .addGroup(pnlMovementLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(spnCruiseMP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(5)
                .addGroup(pnlMovementLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lblFlankMP)
                    .addComponent(lblMoveSummary))
                .addGap(8)
                .addGroup(pnlMovementLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(spnJumpMP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(5))
        );
        //endregion

        //region Basic Setup Tab / Chassis Modifications Panel
        pnlChassisMods.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Chassis Modifications"));
        pnlChassisMods.setLayout(new java.awt.GridBagLayout());

        chkFlotationHull.setEnabled(false);
        chkFlotationHull.addActionListener(this::chkFlotationHullActionPerformed);
        chkLimitedAmph.setEnabled(false);
        chkLimitedAmph.addActionListener(this::chkLimitedAmphActionPerformed);
        chkFullAmph.setEnabled(false);
        chkFullAmph.addActionListener(this::chkFullAmphActionPerformed);
        chkDuneBuggy.setEnabled(false);
        chkDuneBuggy.addActionListener(this::chkDuneBuggyActionPerformed);
        chkEnviroSealing.setEnabled(false);
        chkEnviroSealing.addActionListener(this::chkEnviroSealingActionPerformed);

        pnlChassisMods.add(chkFlotationHull, Utils.gridBag(0, 0));
        pnlChassisMods.add(chkLimitedAmph, Utils.gridBag(0, 1));
        pnlChassisMods.add(chkFullAmph, Utils.gridBag(0, 2));
        pnlChassisMods.add(chkDuneBuggy, Utils.gridBag(0, 3));
        pnlChassisMods.add(chkEnviroSealing, Utils.gridBag(0, 4));
        //endregion

        //region Basic Setup Tab / Experimental Equipment Panel
        pnlExperimental.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Experimental Equipment"));

        chkArmoredMotive.setEnabled(false);
        chkArmoredMotive.addActionListener(this::chkArmoredMotiveActionPerformed);

        chkEscapePod.setText("Combat Vehicle Escape Pod");
        chkEscapePod.setEnabled(false);

        chkCommandConsole.setText("Command Console");
        chkCommandConsole.setEnabled(false);

        chkMinesweeper.setText("Minesweeper");
        chkMinesweeper.setEnabled(false);
        chkJetBooster.setEnabled(false);
        chkJetBooster.addActionListener(this::chkJetBoosterActionPerformed);
        chkSupercharger.setEnabled(false);
        chkSupercharger.addActionListener(this::chkSuperchargerActionPerformed);
        chkSponsonTurret.setEnabled(false);
        chkSponsonTurret.addActionListener(this::chkSponsonTurretActionPerformed);

        GroupLayout pnlExperimentalLayout = new GroupLayout(pnlExperimental);
        pnlExperimental.setLayout(pnlExperimentalLayout);
        pnlExperimentalLayout.setHorizontalGroup(
            pnlExperimentalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlExperimentalLayout.createSequentialGroup()
                .addGroup(pnlExperimentalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(chkArmoredMotive)
                    .addComponent(chkSupercharger)
                    .addComponent(chkJetBooster)
                    .addComponent(chkMinesweeper)
                    .addComponent(chkCommandConsole)
                    .addComponent(chkEscapePod)
                    .addComponent(chkSponsonTurret))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlExperimentalLayout.setVerticalGroup(
            pnlExperimentalLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlExperimentalLayout.createSequentialGroup()
                .addComponent(chkArmoredMotive)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSupercharger)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCommandConsole)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMinesweeper)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkJetBooster)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEscapePod)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSponsonTurret)
                .addContainerGap())
        );
        //endregion

        //region Basic Setup Tab / Construction Options Panel
        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Construction Options"));
        jPanel11.setLayout(new java.awt.GridBagLayout());

        chkFractional.setEnabled(false);
        chkFractional.addActionListener(this::chkFractionalActionPerformed);
        jPanel11.add(chkFractional, Utils.gridBag(0, 0));
        //endregion

        //region Basic Setup Tab / Summary Panel
        pnlSummary.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Summary"));
        pnlSummary.setLayout(new java.awt.GridBagLayout());

        pnlSummary.add(Utils.alignedLabel("Item", SwingConstants.CENTER), Utils.gridBag(0, 0));
        pnlSummary.add(Utils.alignedLabel("Tonnage", SwingConstants.CENTER), Utils.gridBag(1, 0));
        pnlSummary.add(Utils.alignedLabel("Space", SwingConstants.CENTER), Utils.gridBag(2, 0));
        pnlSummary.add(Utils.alignedLabel("Availability", SwingConstants.CENTER), Utils.gridBag(3, 0));
        pnlSummary.add(Utils.alignedLabel("Internal Structure:", SwingConstants.RIGHT), Utils.gridBag(0, 1));
        pnlSummary.add(txtSumIntTons, Utils.gridBag(1, 1));
        pnlSummary.add(txtSumIntAV, Utils.gridBag(3, 1));
        pnlSummary.add(Utils.alignedLabel("Engine:", SwingConstants.RIGHT), Utils.gridBag(0, 2));
        pnlSummary.add(txtSumEngTons, Utils.gridBag(1, 2));
        pnlSummary.add(txtSumEngAV, Utils.gridBag(3, 2));
        pnlSummary.add(Utils.alignedLabel("Lift/Dive/Rotor:", SwingConstants.RIGHT), Utils.gridBag(0, 3));
        pnlSummary.add(txtSumLifTons, Utils.gridBag(1, 3));
        pnlSummary.add(txtSumLifAV, Utils.gridBag(3, 3));
        pnlSummary.add(txtSumEngSpace, Utils.gridBag(2, 2, new Insets(0, 2, 0, 2)));
        pnlSummary.add(Utils.alignedLabel("Controls:", SwingConstants.RIGHT), Utils.gridBag(0, 4));
        pnlSummary.add(txtSumConTons, Utils.gridBag(1, 4));
        pnlSummary.add(txtSumConAV, Utils.gridBag(3, 4));
        pnlSummary.add(Utils.alignedLabel("Jump Jets:", SwingConstants.RIGHT), Utils.gridBag(0, 5));
        pnlSummary.add(txtSumJJTons, Utils.gridBag(1, 5));
        pnlSummary.add(txtSumJJSpace, Utils.gridBag(2, 5, new Insets(0, 2, 0, 2)));
        pnlSummary.add(txtSumJJAV, Utils.gridBag(3, 5));
        pnlSummary.add(Utils.alignedLabel("Heat Sinks:", SwingConstants.RIGHT), Utils.gridBag(0, 6));
        pnlSummary.add(txtSumHSTons, Utils.gridBag(1, 6));
        pnlSummary.add(txtSumHSAV, Utils.gridBag(3, 6));
        pnlSummary.add(Utils.alignedLabel("Armor:", SwingConstants.RIGHT), Utils.gridBag(0, 7));
        pnlSummary.add(txtSumArmTons, Utils.gridBag(1, 7));
        pnlSummary.add(txtSumArmSpace, Utils.gridBag(2, 7, new Insets(0, 2, 0, 2)));
        pnlSummary.add(txtSumArmAV, Utils.gridBag(3, 7));
        pnlSummary.add(Utils.alignedLabel("Turret:", SwingConstants.RIGHT), Utils.gridBag(0, 8));
        pnlSummary.add(txtSumTurTons, Utils.gridBag(1, 8));
        pnlSummary.add(txtSumTurAV, Utils.gridBag(3, 8));
        pnlSummary.add(Utils.alignedLabel("Rear Turret:", SwingConstants.RIGHT), Utils.gridBag(0, 9));
        pnlSummary.add(txtSumRTuTons, Utils.gridBag(1, 9));
        pnlSummary.add(txtSumRTuAV, Utils.gridBag(3, 9));
        pnlSummary.add(Utils.alignedLabel("Sponsons:", SwingConstants.RIGHT), Utils.gridBag(0, 10));
        pnlSummary.add(txtSumSpnTons, Utils.gridBag(1, 10));
        pnlSummary.add(txtSumSpnAV, Utils.gridBag(3, 10));
        pnlSummary.add(Utils.alignedLabel("Power Amplifiers:", SwingConstants.RIGHT), Utils.gridBag(0, 11));
        pnlSummary.add(txtSumPATons, Utils.gridBag(1, 11));
        pnlSummary.add(txtSumPAAV, Utils.gridBag(3, 11));
        //endregion

        //region Basic Setup Tab / Information Panel
        pnlInformation.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Information"));

        GroupLayout pnlInformationLayout = new GroupLayout(pnlInformation);
        pnlInformation.setLayout(pnlInformationLayout);
        pnlInformationLayout.setHorizontalGroup(
            pnlInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(titleCrew)
                        .addGap(4, 4, 4)
                        .addComponent(lblNumCrew))
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(titleSuspension)
                        .addGap(4, 4, 4)
                        .addComponent(lblSupensionFacter))
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addComponent(titleMinEngTon)
                        .addGap(4, 4, 4)
                        .addComponent(lblMinEngineTons))
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(titleBaseEngRate)
                        .addGap(4, 4, 4)
                        .addComponent(lblBaseEngineRating))
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(titleFinalEngRate)
                        .addGap(4, 4, 4)
                        .addComponent(lblFinalEngineRating))
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(titleFreeHeatSinks)
                        .addGap(4, 4, 4)
                        .addComponent(lblFreeHeatSinks)))
                .addContainerGap(95, Short.MAX_VALUE))
        );
        pnlInformationLayout.setVerticalGroup(
            pnlInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlInformationLayout.createSequentialGroup()
                .addGroup(pnlInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleCrew)
                    .addComponent(lblNumCrew))
                .addGap(2, 2, 2)
                .addGroup(pnlInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleSuspension)
                    .addComponent(lblSupensionFacter))
                .addGap(2, 2, 2)
                .addGroup(pnlInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleMinEngTon)
                    .addComponent(lblMinEngineTons))
                .addGap(2, 2, 2)
                .addGroup(pnlInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleBaseEngRate)
                    .addComponent(lblBaseEngineRating))
                .addGap(2, 2, 2)
                .addGroup(pnlInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleFinalEngRate)
                    .addComponent(lblFinalEngineRating))
                .addGap(2, 2, 2)
                .addGroup(pnlInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleFreeHeatSinks)
                    .addComponent(lblFreeHeatSinks)))
        );
        //endregion

        //region Basic Setup Tab / Omni Configuration Panel
        pnlOmniInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Omni Configuration"));
        pnlOmniInfo.setLayout(new java.awt.GridBagLayout());
        pnlOmniInfo.add(OmniButton(btnLockChassis, this::btnLockChassisActionPerformed), Utils.gridBag(0, 0));
        pnlOmniInfo.add(OmniButton(btnAddVariant, this::btnAddVariantActionPerformed), Utils.gridBag(0, 1));
        pnlOmniInfo.add(OmniButton(btnDeleteVariant, this::btnDeleteVariantActionPerformed), Utils.gridBag(1, 0));
        pnlOmniInfo.add(OmniButton(btnRenameVariant, this::btnRenameVariantActionPerformed), Utils.gridBag(1, 1));
        //endregion

        //region Basic Setup Tab / Armor Locations Panel
        pnlArmorLocations.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Armor Locations"));
        pnlRightArmor = ArmorLocation("Right", lblRightIntPts, spnRightArmor, this::spnRightArmorStateChanged);
        pnlFrontArmor = ArmorLocation("Front", lblFrontIntPts, spnFrontArmor, this::spnFrontArmorStateChanged);
        pnlLeftArmor = ArmorLocation("Left", lblLeftIntPts, spnLeftArmor, this::spnLeftArmorStateChanged);
        pnlRearArmor = ArmorLocation("Rear", lblRearIntPts, spnRearArmor, this::spnRearArmorStateChanged);
        pnlTurretArmor = ArmorLocation("Turret", lblTurretIntPts, spnTurretArmor, this::spnTurretArmorStateChanged);
        pnlRearTurretArmor = ArmorLocation("R Turret", lblRearTurretIntPts, spnRearTurretArmor, this::spnRearTurretArmorStateChanged);
        pnlRotorArmor = ArmorLocation("Rotor", lblRotorIntPts, spnRotorArmor, this::spnRotorArmorStateChanged);

        GroupLayout jPanel6Layout = new GroupLayout(pnlArmorLocations);
        pnlArmorLocations.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlLeftArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(pnlFrontArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlRearTurretArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlRearArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlTurretArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlRotorArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlRightArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(pnlLeftArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(pnlFrontArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(pnlTurretArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(pnlRightArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pnlRotorArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 11, GroupLayout.PREFERRED_SIZE)
                .addComponent(pnlRearTurretArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlRearArmor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        //endregion

        //region Basic Setup Tab / Armor Type Panel
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Armor Type"));

        cmbArmorType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard", "Industrial", "Commercial", "Ferro-Fibrous", "Light Ferro-Fibrous", "Heavy Ferro-Fibrous", "Vehicular Stealth" }));
        cmbArmorType.setMinimumSize(new Dimension(150, 20));
        cmbArmorType.setPreferredSize(new Dimension(150, 20));
        cmbArmorType.addActionListener(this::cmbArmorTypeActionPerformed);
        chkBalanceLRArmor.setSelected(true);
        btnSetArmorTons.addActionListener(this::btnSetArmorTonsActionPerformed);
        btnUseRemaining.addActionListener(this::btnUseRemainingActionPerformed);
        btnMaximize.addActionListener(this::btnMaximizeActionPerformed);

        GroupLayout jPanel7Layout = new GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel52)
                        .addGap(2, 2, 2)
                        .addComponent(cmbArmorType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnSetArmorTons, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnUseRemaining, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnMaximize, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(chkBalanceLRArmor)
                            .addComponent(chkBalanceFRArmor))))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel52))
                    .addComponent(cmbArmorType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBalanceLRArmor)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBalanceFRArmor)
                .addGap(0, 0, 0)
                .addComponent(btnSetArmorTons)
                .addGap(0, 0, 0)
                .addComponent(btnUseRemaining)
                .addGap(0, 0, 0)
                .addComponent(btnMaximize))
        );
        //endregion

        //region Basic Setup Tab / Armor Information Panel
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Armor Information"));
        jPanel8.setLayout(new java.awt.GridBagLayout());
        jLabel34.setHorizontalAlignment(SwingConstants.CENTER);
        jPanel8.add(jLabel34, Utils.gridBag(0, 0));
        jLabel36.setHorizontalAlignment(SwingConstants.CENTER);
        jPanel8.add(jLabel36, Utils.gridBag(1, 0));

        lblArmorTotals.setHorizontalAlignment(SwingConstants.RIGHT);
        jPanel8.add(lblArmorTotals, Utils.gridBag(2, 0));
        lblArmorCoverage.setHorizontalAlignment(SwingConstants.RIGHT);
        jPanel8.add(lblArmorCoverage, Utils.gridBag(2, 1));

        txtArmorTons.setEditable(false);
        txtArmorTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtArmorTons.setPreferredSize(new Dimension(50, 20));
        jPanel8.add(txtArmorTons, Utils.gridBag(0, 1));

        txtArmorSpace.setEditable(false);
        txtArmorSpace.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArmorSpace.setPreferredSize(new Dimension(40, 20));
        jPanel8.add(txtArmorSpace, Utils.gridBag(1, 1, new Insets(0, 2, 0, 0)));
        lblArmorTonsWasted.setHorizontalAlignment(SwingConstants.RIGHT);
        jPanel8.add(lblArmorTonsWasted, Utils.gridBag(0, 2, 3, GridBagConstraints.EAST, new Insets(4, 0, 0, 0)));
        lblArmorLeftInLot.setHorizontalAlignment(SwingConstants.RIGHT);
        jPanel8.add(lblArmorLeftInLot, Utils.gridBag(0, 3, 3, GridBagConstraints.EAST, new Insets(4, 0, 0, 0)));
        //endregion

        //region Basic Setup Tab / Layout
        GroupLayout pnlBasicSetupLayout = new GroupLayout(pnlBasicSetup);
        pnlBasicSetup.setLayout(pnlBasicSetupLayout);
        pnlBasicSetupLayout.setHorizontalGroup(
            pnlBasicSetupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlBasicSetupLayout.createSequentialGroup()
                .addGroup(pnlBasicSetupLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlChassisMods, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlMovement, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBasicSetupLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlExperimental, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlChassis, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlBasicSetupLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlSummary, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlOmniInfo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlInformation, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlArmorLocations, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBasicSetupLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlBasicSetupLayout.setVerticalGroup(
            pnlBasicSetupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlBasicSetupLayout.createSequentialGroup()
                .addGroup(pnlBasicSetupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBasicSetupLayout.createSequentialGroup()
                        .addComponent(jPanel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlBasicSetupLayout.createSequentialGroup()
                        .addComponent(pnlChassis, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlExperimental, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlBasicSetupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addGroup(pnlBasicSetupLayout.createSequentialGroup()
                            .addComponent(pnlOmniInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlSummary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlInformation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(GroupLayout.Alignment.LEADING, pnlBasicSetupLayout.createSequentialGroup()
                            .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, 221, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlMovement, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlChassisMods, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE))
                        .addComponent(pnlArmorLocations, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbpMainTabPane.addTab("Basic Setup and Armor", pnlBasicSetup);
        //endregion

        //region Equipment Tab / Highlighted Equipment Information
        pnlEquipInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Information"));
        pnlEquipInfo.setLayout(new java.awt.GridBagLayout());
        pnlEquipInfo.add(new JLabel("Avail. (AoW/SL)"), Utils.gridBag(0, 3, new Insets(0, 0, 0, 3)));
        pnlEquipInfo.add(new JLabel("Avail. (SW)"), Utils.gridBag(0, 4, new Insets(0, 0, 0, 3)));
        pnlEquipInfo.add(new JLabel("Avail. (CI)"), Utils.gridBag(0, 5, new Insets(0, 0, 0, 3)));
        pnlEquipInfo.add(lblInfoAVSL, Utils.gridBag(1, 3, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoAVSW, Utils.gridBag(1, 4, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoAVCI, Utils.gridBag(1, 5, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel("Introduction"), Utils.gridBag(2, 3, 2, GridBagConstraints.EAST, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel("Extinction"), Utils.gridBag(2, 4, 2, GridBagConstraints.EAST, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel("Reintroduction"), Utils.gridBag(2, 5, 2, GridBagConstraints.EAST, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoIntro, Utils.gridBag(4, 3, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoExtinct, Utils.gridBag(4, 4, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoReintro, Utils.gridBag(4, 5, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel(" "), Utils.gridBag(0, 0, new Insets(4, 0, 0, 3)));
        pnlEquipInfo.add(new JLabel("Type"), Utils.gridBag(1, 0, new Insets(4, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel("Heat"), Utils.gridBag(2, 0, new Insets(4, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel("Damage"), Utils.gridBag(3, 0, new Insets(4, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel("Range"), Utils.gridBag(4, 0, new Insets(4, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel("Ammo"), Utils.gridBag(5, 0, new Insets(4, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel("Tonnage"), Utils.gridBag(6, 0, new Insets(4, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel("Crits"), Utils.gridBag(7, 0, new Insets(4, 3, 0, 3)));
        pnlEquipInfo.add(new JLabel("Specials"), Utils.gridBag(8, 0, new Insets(4, 3, 0, 0)));
        pnlEquipInfo.add(lblInfoType, Utils.gridBag(1, 1, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoHeat, Utils.gridBag(2, 1, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoDamage, Utils.gridBag(3, 1, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoRange, Utils.gridBag(4, 1, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(Utils.etchedSeparator(), Utils.gridBag(0, 2, GridBagConstraints.REMAINDER, GridBagConstraints.WEST, new Insets(4, 0, 4, 0)));
        pnlEquipInfo.add(lblInfoAmmo, Utils.gridBag(5, 1, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoTonnage, Utils.gridBag(6, 1, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoCrits, Utils.gridBag(7, 1, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoSpecials, Utils.gridBag(8, 1, new Insets(0, 3, 0, 0)));
        pnlEquipInfo.add(Utils.etchedSeparator(), Utils.gridBag(0, 7, GridBagConstraints.REMAINDER, GridBagConstraints.WEST, new Insets(4, 0, 4, 0)));
        pnlEquipInfo.add(new JLabel("Cost"), Utils.gridBag(5, 4, 2, GridBagConstraints.EAST, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoCost, Utils.gridBag(7, 4, 2, GridBagConstraints.WEST, new Insets(0, 3, 0, 0)));
        pnlEquipInfo.add(new JLabel("BV"), Utils.gridBag(5, 5, 2, GridBagConstraints.WEST, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoBV, Utils.gridBag(7, 5, 2, GridBagConstraints.WEST, new Insets(0, 3, 0, 0)));
        pnlEquipInfo.add(new JLabel("Mounting Restrictions"), Utils.gridBag(0, 8, 2, GridBagConstraints.WEST, new Insets(0, 0, 4, 3)));
        pnlEquipInfo.add(lblInfoMountRestrict, Utils.gridBag(2, 8, 7, GridBagConstraints.WEST, new Insets(0, 3, 4, 0)));
        pnlEquipInfo.add(new JLabel("MM Name"), Utils.gridBag(0, 9, 2, GridBagConstraints.WEST, new Insets(0, 0, 4, 3)));
        pnlEquipInfo.add(lblMMNameInfo, Utils.gridBag(2, 9, 7, GridBagConstraints.WEST, new Insets(0, 3, 4, 0)));
        pnlEquipInfo.add(new JLabel("Rules Level"), Utils.gridBag(5, 3, 2, GridBagConstraints.EAST, new Insets(0, 3, 0, 3)));
        pnlEquipInfo.add(lblInfoRulesLevel, Utils.gridBag(7, 3, 2, GridBagConstraints.WEST, new Insets(0, 3, 0, 0)));
        pnlEquipInfo.add(new JLabel("Avail. (DA)"), Utils.gridBag(0, 6, new Insets(0, 0, 0, 3)));
        pnlEquipInfo.add(lblInfoAVDA, Utils.gridBag(1, 6, new Insets(0, 3, 0, 3)));
        //endregion

        //region Equipment Tab / Specials Panel
        chkFCSAIV.addActionListener(this::chkFCSAIVActionPerformed);
        chkFCSAV.setEnabled(false);
        chkFCSAV.addActionListener(this::chkFCSAVActionPerformed);
        chkFCSApollo.setEnabled(false);
        chkFCSApollo.addActionListener(this::chkFCSApolloActionPerformed);
        chkUseTC.setEnabled(false);
        chkUseTC.addActionListener(this::chkUseTCActionPerformed);
        chkCASE.addActionListener(this::chkCASEActionPerformed);

        pnlSpecials.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Specials"));
        pnlSpecials.setLayout(new java.awt.GridBagLayout());
        pnlSpecials.add(new JLabel("Missile Guidance:"), Utils.gridBag(0, 0));
        pnlSpecials.add(chkFCSAIV, Utils.gridBag(0, 1));
        pnlSpecials.add(chkFCSAV, Utils.gridBag(0, 2));
        pnlSpecials.add(chkFCSApollo, Utils.gridBag(0, 3));
        pnlSpecials.add(chkUseTC, Utils.gridBag(0, 4));
        pnlSpecials.add(chkCASE, Utils.gridBag(0, 5));
        //endregion

        //region Equipment Tab / Controls Panel
        btnRemoveEquip.addActionListener(this::btnRemoveEquipActionPerformed);
        btnClearEquip.addActionListener(this::btnClearEquipActionPerformed);
        btnAddEquip.addActionListener(this::btnAddEquipActionPerformed);

        pnlControls.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Controls"));
        pnlControls.setLayout(new java.awt.GridBagLayout());
        pnlControls.setPreferredSize(new Dimension(135, 200));
        pnlControls.add(btnRemoveEquip, Utils.gridBag(0, 0));
        pnlControls.add(btnClearEquip, Utils.gridBag(0, 1));
        pnlControls.add(btnAddEquip, Utils.gridBag(1, 0));
        pnlControls.add(cmbNumEquips, Utils.gridBag(1, 1));

        cmbLocation.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Front", "Left", "Right", "Rear", "Body", "Turret", "Rear Turret" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        cmbLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        cmbLocation.setSelectedIndex(0);
        cmbLocation.setVisibleRowCount(8);
        cmbLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbLocationMouseClicked(evt);
            }
        });
        scrLocations.setViewportView(cmbLocation);

        pnlControls.add(scrLocations, Utils.gridBag(0, 2, 2, 1));

        pnlSelected.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selected Equipment"));
        pnlSelected.setMaximumSize(new Dimension(250, 1000));
        pnlSelected.setMinimumSize(new Dimension(150, 250));
        pnlSelected.setLayout(new BoxLayout(pnlSelected, BoxLayout.LINE_AXIS));
        //endregion

        //region Equipment Tab / Selected Equipment Panel
        lstSelectedEquipment.setModel( new javax.swing.DefaultListModel());
        lstSelectedEquipment.addListSelectionListener(this::lstSelectedEquipmentValueChanged);
        lstSelectedEquipment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstSelectedEquipmentKeyPressed(evt);
            }
        });
        MouseListener mlSelect = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                int Index = lstSelectedEquipment.locationToIndex( e.getPoint() );
                if( Index < 0 ) { return; }
                CurItem = (abPlaceable) CurVee.GetLoadout().GetNonCore().get( Index );
                if( e.isPopupTrigger() ) {
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                int Index = lstSelectedEquipment.locationToIndex( e.getPoint() );
                if( Index < 0 ) { return; }
                CurItem = (abPlaceable) CurVee.GetLoadout().GetNonCore().get( Index );
                if( e.isPopupTrigger() ) {
                    ConfigureUtilsMenu(e.getComponent());
                    mnuUtilities.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        };
        lstSelectedEquipment.addMouseListener( mlSelect );
        lstSelectedEquipment.setCellRenderer( new saw.gui.EquipmentSelectedRenderer( this ) );
        scrSelectedEquip.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrSelectedEquip.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrSelectedEquip.setViewportView(lstSelectedEquipment);
        pnlSelected.add(scrSelectedEquip);
        //endregion

        //region Equipment Tab / Weapons and Equipment Lists
        AbstractListModel placeholder = new AbstractListModel() {
            String[] strings = { "Placeholder" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        };

        MouseListener mlAddEquip = new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( e.getClickCount() == 2 && e.getButton() == 1 ) {
                    btnAddEquipActionPerformed( null );
                }
            }
        };

        tbpWeaponChooser.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        tbpWeaponChooser.setMinimumSize(new Dimension(150, 250));
        tbpWeaponChooser.setMaximumSize(new Dimension(350, 1000));
        tbpWeaponChooser.addTab("Ballistic", EquipmentLocation(lstChooseBallistic, this::lstChooseBallisticValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Energy", EquipmentLocation(lstChooseEnergy, this::lstChooseEnergyValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Missile", EquipmentLocation(lstChooseMissile, this::lstChooseMissileValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Physical", EquipmentLocation(lstChoosePhysical, this::lstChoosePhysicalValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Equipment", EquipmentLocation(lstChooseEquipment, this::lstChooseEquipmentValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Artillery", EquipmentLocation(lstChooseArtillery, this::lstChooseArtilleryValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Ammunition", EquipmentLocation(lstChooseAmmunition, this::lstChooseAmmunitionValueChanged, mlAddEquip, placeholder));

        //region Log Output
        JPanel pnlLog = new JPanel();
        pnlLog.setLayout(new BoxLayout(pnlLog, BoxLayout.Y_AXIS));
            //pnlLog.add(txtLog);   //Uncomment this when you want to see entries in the Log
        //endregion

        GroupLayout pnlEquipmentLayout = new GroupLayout(pnlEquipment);
        pnlEquipment.setLayout(pnlEquipmentLayout);
        pnlEquipmentLayout.setHorizontalGroup(
            pnlEquipmentLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
            .addGroup(pnlEquipmentLayout.createSequentialGroup()
                .addComponent(tbpWeaponChooser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGroup(pnlEquipmentLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlControls, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlSpecials, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(pnlSelected, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGroup(pnlEquipmentLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
                    .addComponent(pnlEquipInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlLog, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)))
        );
        pnlEquipmentLayout.setVerticalGroup(
            pnlEquipmentLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlEquipmentLayout.createSequentialGroup()
                .addGroup(pnlEquipmentLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
                    .addComponent(pnlSelected, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbpWeaponChooser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlEquipmentLayout.createSequentialGroup()
                        .addComponent(pnlControls, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(pnlSpecials, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(pnlEquipmentLayout.createSequentialGroup()
                .addComponent(pnlEquipInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlLog, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE))
        );

        tbpMainTabPane.addTab("Equipment", pnlEquipment);
        //endregion

        //region Battleforce Tab / Export Panel (hidden)
        pnlExport.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Export", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", Font.PLAIN, 11))); // NOI18N
        pnlExport.setLayout(new java.awt.GridBagLayout());

        btnExportTXT.addActionListener(this::btnExportTXTActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlExport.add(btnExportTXT, gridBagConstraints);

        btnExportHTML.addActionListener(this::btnExportHTMLActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        pnlExport.add(btnExportHTML, gridBagConstraints);

        btnExportMTF.addActionListener(this::btnExportMTFActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlExport.add(btnExportMTF, gridBagConstraints);
        //endregion

        //region Fluff Tab / Fluff Editors
        tbpFluffEditors.setTabPlacement(JTabbedPane.RIGHT);
        tbpFluffEditors.setPreferredSize(new Dimension(400, 455));

        pnlOverview.setLayout(new BoxLayout(pnlOverview, BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Overview", pnlOverview);

        pnlCapabilities.setLayout(new BoxLayout(pnlCapabilities, BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Capabilities", pnlCapabilities);

        pnlHistory.setLayout(new BoxLayout(pnlHistory, BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Battle History", pnlHistory);

        pnlDeployment.setLayout(new BoxLayout(pnlDeployment, BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Deployment", pnlDeployment);

        pnlVariants.setLayout(new BoxLayout(pnlVariants, BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Variants", pnlVariants);

        pnlNotables.setLayout(new BoxLayout(pnlNotables, BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Notables", pnlNotables);

        pnlAdditionalFluff.setLayout(new BoxLayout(pnlAdditionalFluff, BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Additional", pnlAdditionalFluff);

        MouseListener showFluff = new MouseAdapter() {
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
        //endregion

        //region Fluff Tab / Manufacturers
        lblManuInfo.setFont(new java.awt.Font("Arial", Font.BOLD, 12)); // NOI18N
        lblManuInfo.setText("Manufacturer Information");

        pnlManufacturers.setLayout(new BoxLayout(pnlManufacturers, BoxLayout.Y_AXIS));
        pnlManufacturers.setPreferredSize(new Dimension(400, 100));
        pnlManufacturers.add(lblManuInfo);
        pnlManufacturers.add(DataEntry("Manufacturing Company:", txtManufacturer, showFluff));
        pnlManufacturers.add(DataEntry("Location:", txtManufacturerLocation, showFluff));
        pnlManufacturers.add(DataEntry("Chassis Model:", txtChassisModel, showFluff));
        pnlManufacturers.add(DataEntry("Engine Manufacturer:", txtEngineManufacturer, showFluff));
        pnlManufacturers.add(DataEntry("Armor Model:", txtArmorModel, showFluff));
        pnlManufacturers.add(DataEntry("Communications System:", txtCommSystem, showFluff));
        pnlManufacturers.add(DataEntry("Targeting and Tracking:", txtTNTSystem, showFluff));

        pnlWeaponsManufacturers.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Weapons Manufacturers", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", Font.PLAIN, 11))); // NOI18N
        pnlWeaponsManufacturers.setFont(new java.awt.Font("Arial", Font.PLAIN, 11)); // NOI18N
        pnlWeaponsManufacturers.setMinimumSize(new Dimension(200, 200));
        pnlWeaponsManufacturers.setLayout(new BoxLayout(pnlWeaponsManufacturers, BoxLayout.Y_AXIS));

        chkIndividualWeapons.setText("Assign manufacturers individually");
        pnlWeaponsManufacturers.add(chkIndividualWeapons);

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
        pnlWeaponsManufacturers.add(scpWeaponManufacturers);
        pnlManufacturers.add(pnlWeaponsManufacturers);

        //tbpFluffEditors.addTab("Manufacturers", pnlManufacturers);
        //endregion

        //region Fluff Tab / Quirks Editor
        lblBattleMechQuirks.setFont(new java.awt.Font("Arial", Font.BOLD, 12)); // NOI18N

        tblQuirks.setModel(new tbQuirks(new ArrayList<Quirk>()));
        tblQuirks.setColumnSelectionAllowed(true);
        tblQuirks.getTableHeader().setReorderingAllowed(false);
        scpQuirkTable.setViewportView(tblQuirks);

        btnAddQuirk.addActionListener(this::btnAddQuirkActionPerformed);

        GroupLayout pnlQuirksLayout = new GroupLayout(pnlQuirks);
        pnlQuirks.setLayout(pnlQuirksLayout);
        pnlQuirksLayout.setHorizontalGroup(
            pnlQuirksLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(lblBattleMechQuirks, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGroup(pnlQuirksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlQuirksLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, pnlQuirksLayout.createSequentialGroup()
                        .addGap(0, 313, Short.MAX_VALUE)
                        .addComponent(btnAddQuirk))
                    .addComponent(scpQuirkTable, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlQuirksLayout.setVerticalGroup(
            pnlQuirksLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuirksLayout.createSequentialGroup()
                .addComponent(lblBattleMechQuirks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnAddQuirk)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scpQuirkTable, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tbpFluffEditors.addTab("Quirks", pnlQuirks);
        //endregion

        //region Fluff Tab / Fluff Image
        pnlImage.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Fluff Image", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", Font.PLAIN, 11))); // NOI18N
        pnlImage.setPreferredSize(new Dimension(350, 450));
        pnlImageButtons.setLayout(new java.awt.GridBagLayout());

        lblFluffImage.setPreferredSize(new Dimension(300, 350));

        btnLoadImage.addActionListener(this::btnLoadImageActionPerformed);
        pnlImageButtons.add(btnLoadImage, new GridBagConstraints());

        btnClearImage.addActionListener(this::btnClearImageActionPerformed);
        pnlImageButtons.add(btnClearImage, Utils.gridBag(1, 0));

        GroupLayout pnlImageLayout = new GroupLayout(pnlImage);
        pnlImage.setLayout(pnlImageLayout);
        pnlImageLayout.setHorizontalGroup(
                pnlImageLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(pnlImageButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFluffImage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        pnlImageLayout.setVerticalGroup(
                pnlImageLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(pnlImageLayout.createSequentialGroup()
                                .addComponent(pnlImageButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblFluffImage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        //endregion

        //region Fluff Tab / Layout
        GroupLayout pnlFluffLayout = new GroupLayout(pnlFluff);
        pnlFluff.setLayout(pnlFluffLayout);
        pnlFluffLayout.setHorizontalGroup(
                pnlFluffLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
                        .addGroup(pnlFluffLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(pnlManufacturers, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbpFluffEditors, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlImage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addContainerGap())
        );
        pnlFluffLayout.setVerticalGroup(
                pnlFluffLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
                        .addGroup(pnlFluffLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlFluffLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
                                        .addComponent(pnlManufacturers, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(tbpFluffEditors, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(pnlImage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        tbpMainTabPane.addTab("Fluff", pnlFluff);
        //endregion

        //region Battleforce Tab / Battleforce Stats
        pnlBFStats.setBorder(javax.swing.BorderFactory.createTitledBorder("BattleForce Stats"));
        pnlBFStats.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        pnlBFStats.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));
        pnlBFStats.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, -1, -1));
        pnlBFStats.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));
        pnlBFStats.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, -1, -1));
        pnlBFStats.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));
        pnlBFStats.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 30, -1, -1));
        pnlBFStats.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 30, -1, -1));
        pnlBFStats.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 110, -1, -1));
        pnlBFStats.add(jLabel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 140, -1, -1));
        pnlBFStats.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, -1, -1));
        lblBFMV.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlBFStats.add(lblBFMV, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 30, -1));
        lblBFWt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlBFStats.add(lblBFWt, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, 30, -1));
        lblBFOV.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlBFStats.add(lblBFOV, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 50, 30, -1));
        lblBFExtreme.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlBFStats.add(lblBFExtreme, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, 30, -1));
        lblBFShort.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlBFStats.add(lblBFShort, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 30, -1));
        lblBFMedium.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlBFStats.add(lblBFMedium, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 30, -1));
        lblBFLong.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlBFStats.add(lblBFLong, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 30, -1));
        lblBFArmor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlBFStats.add(lblBFArmor, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, 30, -1));
        lblBFStructure.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlBFStats.add(lblBFStructure, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 60, 30, -1));
        pnlBFStats.add(lblBFSA, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 430, 20));
        pnlBFStats.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 30, -1, -1));
        pnlBFStats.add(lblBFPoints, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 30, -1, -1));
        //endregion

        //region Battleforce Tab / Conversion Steps
        pnlConversionSteps.setBorder(javax.swing.BorderFactory.createTitledBorder("Conversion Steps"));

        jTextAreaBFConversion.setColumns(60);
        jTextAreaBFConversion.setEditable(false);
        jTextAreaBFConversion.setRows(5);
        scpBFConversion.setViewportView(jTextAreaBFConversion);

        GroupLayout jPanel10Layout = new GroupLayout(pnlConversionSteps);
        pnlConversionSteps.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scpBFConversion, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
            .addComponent(scpBFConversion)
        );
        //endregion

        //region Battleforce Tab / Layout
        JPanel pnlBF = new JPanel();
        pnlBF.setLayout(new BoxLayout(pnlBF, BoxLayout.X_AXIS));
        pnlBF.add(pnlBFStats);
        pnlBF.add(pnlConversionSteps);
        //pnlBF.add(pnlExport);
        tbpMainTabPane.addTab("BattleForce", pnlBF);
        //endregion

        //region Menus

        //region File
        mnuFile.addActionListener(this::mnuFileActionPerformed);
        mnuFile.add(Utils.menuItem("New", this::mnuNewMechActionPerformed, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_MASK)));
        mnuFile.add(Utils.menuItem("Load", this::mnuLoadActionPerformed, KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_MASK)));
        mnuFile.add(Utils.menuItem("Open", this::mnuOpenActionPerformed, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK)));

        mnuImport.add(Utils.menuItem("from Heavy Metal Pro (HMP)", this::mnuImportHMPActionPerformed));
        mnuImport.add(Utils.menuItem("Batch Import HMP Files", this::mnuBatchHMPActionPerformed));
        mnuFile.add(mnuImport);
        mnuFile.add(Utils.etchedSeparator());
        mnuFile.add(Utils.menuItem("Save", this::mnuSaveActionPerformed, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK)));

        mnuSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK | InputEvent.CTRL_MASK));
        mnuSaveAs.addActionListener(this::mnuSaveAsActionPerformed);
        mnuFile.add(mnuSaveAs);

        mnuExport.add(Utils.menuItem("to HTML (Web)", this::mnuExportHTMLActionPerformed));
        mnuExport.add(Utils.menuItem("to MTF (MegaMek)", this::mnuExportMTFActionPerformed));
        mnuExport.add(Utils.menuItem("to TXT (Text)", this::mnuExportTXTActionPerformed));
        mnuExport.add(Utils.menuItem("to Clipboard (Text)", this::mnuExportClipboardActionPerformed));
        mnuExport.add(Utils.menuItem("to TCG Format (Card)", this::mnuCreateTCGMechActionPerformed));
        mnuFile.add(mnuExport);
        mnuFile.add(Utils.etchedSeparator());
        mnuFile.add(Utils.menuItem("Print Preview", this::mnuPrintPreviewActionPerformed, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK)));
        mnuFile.add(Utils.etchedSeparator());
        mnuFile.add(Utils.menuItem("Exit", this::mnuExitActionPerformed, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK)));
        jMenuBar1.add(mnuFile);
        //endregion

        //region Tools
        mnuClearFluff.add(Utils.menuItem("Show Summary", this::mnuSummaryActionPerformed));
        mnuClearFluff.add(Utils.menuItem("Cost/BV Breakdown", this::mnuCostBVBreakdownActionPerformed));
        mnuClearFluff.add(Utils.menuItem("Show Text TRO Format", this::mnuTextTROActionPerformed));
        mnuClearFluff.add(Utils.etchedSeparator());
        mnuClearFluff.add(Utils.menuItem("Load Force Balancer", this::mnuBFBActionPerformed));
        mnuClearFluff.add(Utils.etchedSeparator());
        mnuClearFluff.add(Utils.menuItem("Preferences", this::mnuOptionsActionPerformed, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK)));

        mnuViewToolbar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        mnuViewToolbar.setSelected(true);
        mnuViewToolbar.addActionListener(this::mnuViewToolbarActionPerformed);
        mnuClearFluff.add(mnuViewToolbar);

        mnuClearFluff.add(Utils.menuItem("Clear User Data", this::mnuClearUserDataActionPerformed));
        mnuClearFluff.add(Utils.etchedSeparator());

        mnuUnlock.setEnabled(false);
        mnuUnlock.addActionListener(this::mnuUnlockActionPerformed);
        mnuClearFluff.add(mnuUnlock);
        mnuClearFluff.add(Utils.menuItem("Clear All Fluff", this::jMenuItem1ActionPerformed));
        mnuClearFluff.add(Utils.menuItem("Reload Equipment", this::mnuReloadEquipmentActionPerformed));
        jMenuBar1.add(mnuClearFluff);
        //endregion

        //region About
        mnuHelp.add(Utils.menuItem("Credits", this::mnuCreditsActionPerformed));
        mnuHelp.add(Utils.menuItem("About SAW", this::mnuAboutSSWActionPerformed));
        jMenuBar1.add(mnuHelp);
        //endregion

        setJMenuBar(jMenuBar1);
        //endregion

        //region Form Layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
            .addComponent(tlbIconBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(pnlInfoPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tbpMainTabPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tlbIconBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(tbpMainTabPane)
                .addComponent(pnlInfoPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        //endregion

        pack();
    }

    private JButton OmniButton(JButton button, ActionListener listener) {
        button.setEnabled(false);
        button.setPreferredSize(new Dimension(120, 23));
        button.addActionListener(listener);
        return button;
    }

    private JPanel ArmorLocation(String title, JLabel label, JSpinner spinner, ChangeListener listener) {
        JPanel pnl = new JPanel();
        pnl.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), title));
        pnl.setLayout(new java.awt.GridBagLayout());
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        label.setPreferredSize(new Dimension(45, 20));
        pnl.add(label, Utils.gridBag(0, 1));
        pnl.add(new JLabel("Internal"), new GridBagConstraints());
        pnl.add(new JLabel("Armor"), Utils.gridBag(0, 2));
        spinner.setPreferredSize(new Dimension(45, 20));
        spinner.addChangeListener(listener);
        pnl.add(spinner, Utils.gridBag(0, 3));
        return pnl;
    }

    private JPanel EquipmentLocation(JList list, ListSelectionListener selection, MouseListener listener, AbstractListModel display) {
        JPanel panel = new JPanel();
        list.setModel(display);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(selection);
        list.addMouseListener( listener );
        list.setCellRenderer( new EquipmentListRenderer( this ) );
        JScrollPane pane = new JScrollPane();
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setViewportView(list);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(pane);
        return panel;
    }

    private JPanel DataEntry(String label, JTextField input, MouseListener listener) {
        JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.X_AXIS));

        JLabel dataLabel = new JLabel();
        dataLabel.setText(label);
        dataLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        dataLabel.setPreferredSize(new Dimension(150, 20));

        input.setFont(new java.awt.Font("Arial", Font.PLAIN, 11));
        input.addMouseListener(listener);

        entry.add(dataLabel);
        entry.add(input);

        return entry;
    }

    private void RefreshSummary() {
        // refreshes the display completely using info from the mech.
        txtSumEngTons.setText( "" + CurVee.GetEngineTonnage() );
        txtSumEngSpace.setText("" + CurVee.GetEngine().NumCVSpaces() );
        txtSumIntTons.setText( "" + CurVee.GetIntStruc().GetTonnage() );
        lblSupensionFacter.setText(""+CurVee.GetSuspensionFactor() );
        lblBaseEngineRating.setText(""+CurVee.GetBaseEngineRating() );
        lblFinalEngineRating.setText(""+CurVee.GetFinalEngineRating() );
        lblMinEngineTons.setText(""+CurVee.GetMinEngineWeight() );
        txtSumLifTons.setText( "" + CurVee.GetLiftEquipmentTonnage() );
        txtSumHSTons.setText( "" + CurVee.GetHeatSinkTonnage() );
        txtSumJJTons.setText( "" + CurVee.GetJumpJets().GetTonnage() );
        txtSumArmTons.setText( "" + CurVee.GetArmor().GetTonnage() );
        txtSumArmSpace.setText( "" + CurVee.GetArmor().NumCVSpaces() );
        txtSumConTons.setText("" + CurVee.GetControls() );
        txtSumTurTons.setText("" + CurVee.GetLoadout().GetTurret().GetTonnage() );
        txtSumTurAV.setText( CurVee.GetLoadout().GetTurret().GetAvailability().GetBestCombinedCode() );
        txtTurretInfo.setText("Turret: " + CurVee.GetLoadout().GetTurret().GetTonnage() );
        txtSumRTuTons.setText("" + CurVee.GetLoadout().GetRearTurret().GetTonnage() );
        txtSumRTuAV.setText( CurVee.GetLoadout().GetRearTurret().GetAvailability().GetBestCombinedCode() );
        txtSumSpnTons.setText("" + CurVee.GetLoadout().GetSponsonTurretTonnage() );
        lblFreeHeatSinks.setText("" + CurVee.GetEngine().FreeHeatSinks() );
        lblNumCrew.setText("" + CurVee.GetCrew() );

        txtSumPATons.setText( "" + CurVee.GetLoadout().GetPowerAmplifier().GetTonnage() );
        txtSumIntAV.setText( CurVee.GetIntStruc().GetAvailability().GetBestCombinedCode() );
        txtSumEngAV.setText( CurVee.GetEngine().GetAvailability().GetBestCombinedCode() );
        txtSumLifAV.setText( "D/B-B-B-B" );
        txtSumConAV.setText( "C/A-A-A-A" );
        txtSumHSAV.setText( CurVee.GetHeatSinks().GetAvailability().GetBestCombinedCode() );
        txtSumArmAV.setText(CurVee.GetArmor().GetAvailability().GetBestCombinedCode() );
        txtSumJJAV.setText( CurVee.GetJumpJets().GetAvailability().GetBestCombinedCode() );
        txtSumSpnAV.setText( "B/F-F-F-D" );
        txtSumPAAV.setText( CurVee.GetLoadout().GetPowerAmplifier().GetAvailability().GetBestCombinedCode() );
        txtInfoFreeCrits.setText( "Space: " + CurVee.GetAvailableSlots() );
        txtSumHSTons.setText("" + CurVee.GetHeatSinks().GetTonnage() );
        txtSumHSAV.setText( CurVee.GetHeatSinks().GetAvailability().GetBestCombinedCode() );

        lblFrontIntPts.setText("" + CurVee.GetIntStruc().NumCVSpaces() );
        lblLeftIntPts.setText("" + CurVee.GetIntStruc().NumCVSpaces() );
        lblRearIntPts.setText("" + CurVee.GetIntStruc().NumCVSpaces() );
        lblRightIntPts.setText("" + CurVee.GetIntStruc().NumCVSpaces() );
        lblTurretIntPts.setText("" + CurVee.GetIntStruc().NumCVSpaces() );
        lblRearTurretIntPts.setText("" + CurVee.GetIntStruc().NumCVSpaces() );
        lblRotorIntPts.setText("" + CurVee.GetIntStruc().NumCVSpaces() );

        // added for the armor pane
        txtArmorTons.setText( "" + CurVee.GetArmor().GetTonnage() );
        txtArmorSpace.setText("" + CurVee.GetArmor().NumCVSpaces() );
        lblArmorTotals.setText( CurVee.GetArmor().GetArmorValue() + " of " + CurVee.GetArmor().GetMaxArmor() + " Armor Points" );
        lblArmorCoverage.setText( CurVee.GetArmor().GetCoverage() + "% Coverage" );
        lblArmorTonsWasted.setText( CurVee.GetArmor().GetWastedTonnage() + " Tons Wasted" );
        lblArmorLeftInLot.setText( CurVee.GetArmor().GetWastedAV() + " Points Left In This 1/2 Ton Lot" );

        // added for Battleforce pane
        BattleForceStats bfs = new BattleForceStats(CurVee);

        lblBFMV.setText( bfs.getMovement() );
        lblBFWt.setText( bfs.getWeight() + "" );
        lblBFArmor.setText( bfs.getArmor() + "" );
        lblBFStructure.setText( bfs.getInternal() + "" );
        lblBFPoints.setText("" + bfs.getPointValue() );

        //int [] BFdmg = locArmor.GetBFDamage( bfs );
        lblBFShort.setText("" + bfs.getShort() );
        lblBFMedium.setText("" + bfs.getMedium() );
        lblBFLong.setText("" + bfs.getLong() );
        lblBFExtreme.setText("" + bfs.getExtreme() );
        lblBFOV.setText("" + bfs.getOverheat() );

        lblBFSA.setText( bfs.getAbilitiesString() );

        jTextAreaBFConversion.setText( bfs.getBFConversionData() );

        FixHeatSinkSpinnerModel();
    }

    private void RecalcArmorPlacement() {
        if ( Load ) return;

        double tonnage = CurVee.GetArmor().GetTonnage();
        ArmorTons.SetArmorTonnage( tonnage );
        try {
            CurVee.Visit( ArmorTons );
        } catch( Exception e ) {
            // this should never throw an exception, but log it anyway
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }
        // if we fix the spinner models, they should refresh the screen
        FixArmorSpinners();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void RecalcArmorLocations() {
        if ( cmbTurret.getSelectedItem().toString().equals("No Turret") ) {
            spnTurretArmor.setValue(0);
            spnRearTurretArmor.setValue(0);
            pnlTurretArmor.setVisible(false);
            pnlRearTurretArmor.setVisible(false);
        } else if ( cmbTurret.getSelectedItem().toString().equals("Single Turret")) {
            pnlTurretArmor.setVisible(true);
            spnRearTurretArmor.setValue(0);
            pnlRearTurretArmor.setVisible(false);
        } else if (cmbTurret.getSelectedItem().toString().equals("Chin Turret")) {
            pnlTurretArmor.setVisible(true);
            spnRearTurretArmor.setValue(0);
            pnlRearTurretArmor.setVisible(false);
        } else if ( cmbTurret.getSelectedItem().toString().equals("Dual Turret") ) {
            pnlTurretArmor.setVisible(true);
            pnlRearTurretArmor.setVisible(true);
        }
        if ( CurVee.IsVTOL() ) {
            pnlRotorArmor.setVisible(true);
        } else {
            pnlRotorArmor.setVisible(false);
            spnRotorArmor.setValue(0);
        }
    }

    private void SolidifyVehicle() {
        // sets some of the basic vehicle information normally kept in the GUI and
        // prepares the vehicle for saving to file
        int year;
        CurVee.setName( txtVehicleName.getText() );
        CurVee.setModel( txtModel.getText() );
        if( txtProdYear.getText().isEmpty() ) {
            switch( cmbEra.getSelectedIndex() ) {
                case AvailableCode.ERA_STAR_LEAGUE:
                    CurVee.setYear( 2750, false );
                    break;
                case AvailableCode.ERA_SUCCESSION:
                    CurVee.setYear( 3025, false );
                    break;
                case AvailableCode.ERA_CLAN_INVASION:
                    CurVee.setYear( 3070, false );
                    break;
                case AvailableCode.ERA_DARK_AGES:
                    CurVee.setYear( 3132, false );
                    break;
            }
            txtProdYear.setText(""+CurVee.getYear());
        } else {
            try{
                year = Integer.parseInt( txtProdYear.getText() ) ;
                CurVee.setYear( year, true );
            } catch( NumberFormatException n ) {
                Media.Messager( this, "The production year is not a number." );
                tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
                return;
            }
        }

        CurVee.setOverview( Overview.GetText() );
        CurVee.setCapabilities( Capabilities.GetText() );
        CurVee.setHistory( History.GetText() );
        CurVee.setDeployment( Deployment.GetText() );
        CurVee.setVariants( Variants.GetText() );
        CurVee.SetNotables( Notables.GetText() );
        CurVee.SetAdditional( Additional.GetText() );
        CurVee.SetCompany( txtManufacturer.getText() );
        CurVee.SetLocation( txtManufacturerLocation.getText() );
        CurVee.SetEngineManufacturer( txtEngineManufacturer.getText() );
        CurVee.SetArmorModel( txtArmorModel.getText() );
        CurVee.SetChassisModel( txtChassisModel.getText() );
        if( CurVee.GetJumpJets().GetNumJJ() > 0 ) {
            CurVee.SetJJModel( txtJJModel.getText() );
        }
        CurVee.SetCommSystem( txtCommSystem.getText() );
        CurVee.SetTandTSystem( txtTNTSystem.getText() );
        CurVee.setSource( txtSource.getText() );
    }

    private void BuildLocationSelector() {
        ArrayList locs = new ArrayList();
        locs.add("Front");
        locs.add("Left");
        if ( CurVee.isHasSponsonTurret() )
            locs.add("Left Sponson Turret");
        locs.add("Right");
        if ( CurVee.isHasSponsonTurret() )
            locs.add("Right Sponson Turret");
        locs.add("Rear");
        locs.add("Body");
        if ( CurVee.isHasTurret1() )
            locs.add("Turret");
        if ( CurVee.isHasTurret2() )
            locs.add("Rear Turret");

        cmbLocation.setModel(new DefaultComboBoxModel(locs.toArray()));
        int curSelection = cmbLocation.getSelectedIndex();
        if ( curSelection < 0 || curSelection >= locs.size()) {
            curSelection = 0; // reset to Front
        }
        cmbLocation.setSelectedIndex(curSelection);
    }

    private void BuildArmorSelector() {
        // builds the armor selection box
        ArrayList list = new ArrayList();

        // get the armor states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurVee.GetArmor().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurVee ) ) {
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

        cmbArmorType.setSelectedItem(CurVee.GetArmor().ActualName());
    }

    private void BuildTechBaseSelector() {
        switch( CurVee.GetEra() ) {
            case AvailableCode.ERA_STAR_LEAGUE:
                cmbTechBase.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Inner Sphere" } ) );
                break;
            default:
                if( CurVee.GetRulesLevel() >= AvailableCode.RULES_EXPERIMENTAL ) {
                    cmbTechBase.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Inner Sphere", "Clan", "Mixed" } ) );
                } else if( CurVee.GetRulesLevel() == AvailableCode.RULES_INTRODUCTORY ) {
                    cmbTechBase.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Inner Sphere" } ) );
                } else {
                    cmbTechBase.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Inner Sphere", "Clan" } ) );
                }
                break;
        }
        try {
            cmbTechBase.setSelectedIndex( CurVee.GetTechbase() );
        } catch( Exception e ) {
            Media.Messager( "Could not set the Techbase due to changes.\nReverting to Inner Sphere." );
            cmbTechBase.setSelectedIndex( 0 );
        }
    }

    private void BuildEngineSelector() {
        // builds the engine selection box
        ArrayList list = new ArrayList();

        // get the engine states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurVee.GetEngine().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurVee ) ) {
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

    private void FixArmorSpinners() {
        // fixes the armor spinners to match the new tonnage / motive type
        CVArmor a = CurVee.GetArmor();
        a.SetMaxArmor(CurVee.GetArmorableLocationCount());

        spnFrontArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.CV_LOC_FRONT ), 0, a.GetLocationMax(LocationIndex.CV_LOC_FRONT), 1) );
        spnLeftArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.CV_LOC_LEFT ), 0, a.GetLocationMax(LocationIndex.CV_LOC_LEFT), 1) );
        spnRightArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.CV_LOC_RIGHT ), 0, a.GetLocationMax(LocationIndex.CV_LOC_RIGHT), 1) );
        spnRearArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.CV_LOC_REAR ), 0, a.GetLocationMax(LocationIndex.CV_LOC_REAR), 1) );
        spnTurretArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.CV_LOC_TURRET1 ), 0, a.GetLocationMax(LocationIndex.CV_LOC_TURRET1), 1) );
        spnRearTurretArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.CV_LOC_TURRET2 ), 0, a.GetLocationMax(LocationIndex.CV_LOC_TURRET2), 1) );
        spnRotorArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( LocationIndex.CV_LOC_ROTOR ), 0, a.GetLocationMax(LocationIndex.CV_LOC_ROTOR), 1) );

        //Makes spinners auto-select-all text for easier entry
        ((JSpinner.DefaultEditor)spnTonnage.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnCruiseMP.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnJumpMP.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnHeatSinks.getEditor()).getTextField().addFocusListener(spinners);

        //Setup Spinner focus
        ((JSpinner.DefaultEditor)spnFrontArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnLeftArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRightArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRearArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnTurretArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRearTurretArmor.getEditor()).getTextField().addFocusListener(spinners);
        ((JSpinner.DefaultEditor)spnRotorArmor.getEditor()).getTextField().addFocusListener(spinners);
    }

    public String BuildLookupName( ifState s ) {
        String retval = s.LookupName();
        if( CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
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

    public void FixTonnageSpinner( int MinTonnage, int MaximumTonnage ) {
        int CurVal = Integer.parseInt(spnTonnage.getValue().toString());

        if ( CurVal < MinTonnage )
            CurVal = MinTonnage;

        if ( CurVal > MaximumTonnage )
            CurVal = MaximumTonnage;

        spnTonnage.setModel( new javax.swing.SpinnerNumberModel(CurVal, MinTonnage, MaximumTonnage, 1) );
        spnTonnageStateChanged(null);
    }

    // check the tonnage to see if it's legal and acts accordingly
    public void CheckTonnage( boolean RulesChange ) {
        if( CurVee.GetTonnage() < 1 ) {
            spnTonnage.setValue(1);
        }

        if ( CurVee.GetTonnage() > CurVee.GetMaxTonnage() ) {
            spnTonnage.setValue(CurVee.GetMaxTonnage());
        }
    }

    private void SaveSelections() {
        // saves the current GUI selections
        Selections[0] = BuildLookupName( CurVee.GetEngine().GetCurrentState() );
        Selections[1] = BuildLookupName( CurVee.GetArmor().GetCurrentState() );
    }

    private void LoadSelections() {
        // sets the current selections to the last saved selections or to the
        // default selections.  We'll do some validation here as well.
        cmbEngineType.setSelectedItem( Selections[0] );
        cmbArmorType.setSelectedItem( Selections[1] );
    }

    private void BuildTurretSelector() {
        ArrayList list = new ArrayList();

        if ( !CurVee.IsOmni()) {
            cmbTurret.setEnabled(true);
        }

        list.add("No Turret");
        if ( CurVee.CanUseTurret() ) {
            if (CurVee.IsVTOL()) {
                list.add("Chin Turret");
            } else{
                list.add("Single Turret");
            }
        }
        if ( CurVee.CanUseDualTurret() ) list.add("Dual Turret");

        if ( list.isEmpty() ) {
            list.add("No Turret Allowed");
            cmbTurret.setEnabled(false);
        }

        // turn the ArrayList into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the turret chooser
        cmbTurret.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
        if (CurVee.isHasTurret2())
            cmbTurret.setSelectedItem("Dual Turret");
        else if (CurVee.isHasTurret1())
            cmbTurret.setSelectedItem("Single Turret");
        else
            cmbTurret.setSelectedItem("No Turret");
    }

    private void BuildChassisSelector() {
        chkFlotationHull.setSelected(false);
        chkLimitedAmph.setSelected(false);
        chkFullAmph.setSelected(false);
        chkDuneBuggy.setSelected(false);
        chkEnviroSealing.setSelected(false);

        if ( cmbRulesLevel.getSelectedIndex() > 1 ) {
            chkFlotationHull.setEnabled(true);
            chkLimitedAmph.setEnabled(true);
            chkFullAmph.setEnabled(true);
            chkDuneBuggy.setEnabled(true);
            chkEnviroSealing.setEnabled(true);

            if ( !CurVee.CanUseEnviroSealing() ) {
                chkEnviroSealing.setEnabled(false);
            }
            if ( !CurVee.CanUseFlotationHull() ) {
                chkFlotationHull.setEnabled(false);
            }
            if ( !CurVee.CanUseAmphibious() ) {
                chkLimitedAmph.setEnabled(false);
                chkFullAmph.setEnabled(false);
            }
            if ( !CurVee.CanBeDuneBuggy() ) {
                chkDuneBuggy.setEnabled(false);
            }

            if (CurVee.HasEnvironmentalSealing()) {
                chkEnviroSealing.setSelected(true);
            }
            if (CurVee.HasFlotationHull()) {
                chkFlotationHull.setSelected(true);
            }
            if (CurVee.HasLimitedAmphibious()) {
                chkLimitedAmph.setSelected(true);
            }
            if (CurVee.HasFullAmphibious()) {
                chkFullAmph.setSelected(true);
            }
            if (CurVee.HasDuneBuggy()) {
                chkDuneBuggy.setSelected(true);
            }
        } else {
            chkFlotationHull.setEnabled(false);
            chkLimitedAmph.setEnabled(false);
            chkFullAmph.setEnabled(false);
            chkDuneBuggy.setEnabled(false);
            chkEnviroSealing.setEnabled(false);
        }
    }

    private void BuildExpEquipmentSelector() {
        JCheckBox[] ExpEquipmentCheckboxes = { chkCommandConsole,
                                               chkMinesweeper,
                                               chkEscapePod,
                                               chkSponsonTurret };
        if (cmbRulesLevel.getSelectedIndex() > 1) {

            if (CurVee.CanUseSponson())
                chkSponsonTurret.setEnabled(true);
        } else
            for (JCheckBox item : ExpEquipmentCheckboxes) {
                item.setSelected(false);
                item.setEnabled(false);
            }
    }

    private void ShowInfoOn( abPlaceable p ) {
        // this fills in all the information on the Equipment panel for the given
        // item.  Depending on what the item is, more or less info is provided
        AvailableCode AC = p.GetAvailability();

        lblInfoAVSL.setText( AC.GetISSLCode() + " / " + AC.GetCLSLCode() );
        lblInfoAVSW.setText( AC.GetISSWCode() + " / " + AC.GetCLSWCode() );
        lblInfoAVCI.setText( AC.GetISCICode() + " / " + AC.GetCLCICode() );
        lblInfoAVDA.setText( AC.GetISDACode() + " / " + AC.GetCLDACode() );
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

        switch( AC.GetRulesLevel_CV() ) {
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

        pnlEquipInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), p.CritName()));
        //lblInfoName.setText( p.CritName() );
        lblInfoTonnage.setText( "" + p.GetTonnage() );
        lblInfoCrits.setText( "" + p.NumCVSpaces() );
        lblInfoCost.setText( "" + String.format( "%1$,.0f", p.GetCost() ) );
        lblInfoBV.setText( CommonTools.GetAggregateReportBV( p ) );

        // now do all the mounting restrictions
        String restrict = "";
        if ( ! p.CanAllocCVFront() )
            restrict += "No Front, ";
        if ( ! p.CanAllocCVSide() )
            restrict += "No Sides, ";
        if ( ! p.CanAllocCVRear() )
            restrict += "No Rear, ";
        if ( ! p.CanAllocCVTurret() )
            restrict += "No Turret, ";
        if ( ! p.CanAllocCVBody() )
            restrict += "No Body, ";

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
                    } else if( ((RangedWeapon) w).IsUsingPulseModule() ) {
                        lblInfoHeat.setText( w.GetHeat() + "*" );
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

        lblMMNameInfo.setText(p.MegaMekName(false));
    }

    private void btnAddToForceListActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void btnForceListActionPerformed(java.awt.event.ActionEvent evt) {
        GetForceDialogue().setLocationRelativeTo(this);
        GetForceDialogue().setVisible(true);
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        setCursor( Hourglass );

        File savevee = GetSaveFile( "saw", Prefs.get( "LastOpenCVDirectory", "" ), true, false );
        if( savevee == null ) {
            setCursor( NormalCursor );
            return;
        }

        //Since we are saving to a new file update the stored prefs
        try {
            Prefs.put("LastOpenCVDirectory", savevee.getCanonicalPath().replace(savevee.getName(), ""));
            Prefs.put("LastOpenCVFile", savevee.getName());
            Prefs.put("Currentfile", savevee.getCanonicalPath());
        } catch (IOException e) {
            Media.Messager( this, "There was a problem with the file:\n" + e.getMessage() );
            setCursor( NormalCursor );
            return;
        }

        // exports the mech to XML format
        String CurLoadout = "";
        if( CurVee.IsOmni() ) {
            CurLoadout = CurVee.GetLoadout().GetName();
            SaveOmniFluffInfo();
        }

        // save the mech to XML in the current location
        CVWriter XMLw = new CVWriter( CurVee );
        try {
            String file = savevee.getCanonicalPath();
            String ext = Utils.getExtension( savevee );
            if( ext == null || ext.equals( "" ) ) {
                file += ".saw";
            } else {
                if( ! ext.equals( "saw" ) ) {
                    file.replace( "." + ext, ".saw" );
                }
            }
            XMLw.WriteXML( file );

            // if there were no problems, let the user know how it went
            if (evt != null && evt.getActionCommand().equals("Save Vehicle")) {
                Media.Messager( this, "Vehicle saved successfully:\n" + file );
            }
        } catch( IOException e ) {
            Media.Messager( this, "There was a problem writing the file:\n" + e.getMessage() );
            setCursor( NormalCursor );
            return;
        }

        setCursor( NormalCursor );
        setTitle( saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel() );
        CurVee.SetChanged( false );
    }

    private void SaveOmniFluffInfo() {
        if( SetSource ) {
            CurVee.setSource( txtSource.getText() );
            CurVee.SetEra( cmbEra.getSelectedIndex() );
            CurVee.SetProductionEra( cmbProductionEra.getSelectedIndex() ) ;
            CurVee.SetYearRestricted( chkYearRestrict.isSelected() );
            try {
                CurVee.SetYear( Integer.parseInt( txtProdYear.getText() ), chkYearRestrict.isSelected() );
            } catch( Exception e ) {
                // nothing really to be done, set it to a default.
                switch( cmbEra.getSelectedIndex() ) {
                    case AvailableCode.ERA_STAR_LEAGUE:
                        CurVee.SetYear( 2750, false );
                        break;
                    case AvailableCode.ERA_SUCCESSION:
                        CurVee.SetYear( 3025, false );
                        break;
                    case AvailableCode.ERA_CLAN_INVASION:
                        CurVee.SetYear( 3070, false );
                        break;
                    case AvailableCode.ERA_DARK_AGES:
                        CurVee.SetYear( 3132, false );
                        break;
                    case AvailableCode.ERA_ALL:
                        CurVee.SetYear( 0, false );
                        break;
                }
            }
        }
    }

    private void LoadOmniFluffInfo() {
        cmbRulesLevel.setSelectedIndex( CurVee.GetRulesLevel() );
        cmbEra.setSelectedIndex( CurVee.GetEra() );
        cmbProductionEra.setSelectedIndex( CurVee.GetProductionEra() );
        txtSource.setText( CurVee.getSource() );
        txtProdYear.setText( "" + CurVee.GetYear() );
        BuildTechBaseSelector();
    }

    private File GetSaveFile( final String extension, String path, boolean autooverwrite, boolean singleloadout ) {
        String filename;
        boolean overwrite = false;

        // perform standard actions required before saving
        SolidifyVehicle();

        if( ! VerifyVehicle( null ) ) {
            return null;
        }

        // build the filename
        if( CurVee.IsOmni() && singleloadout ) {
            if( CurVee.GetModel().isEmpty() ) {
                filename = CurVee.GetName() + " " + CurVee.GetLoadout().GetName() + "." + extension;
            } else {
                filename = CurVee.GetName() + " " + CurVee.GetModel() + " " +
                    CurVee.GetLoadout().GetName() + "." + extension;
            }
        } else {
            if( CurVee.GetModel().isEmpty() ) {
                filename = CurVee.GetName() + "." + extension;
            } else {
                filename = CurVee.GetName() + " " + CurVee.GetModel() + "." + extension;
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

        File retval;
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
                        return checkext.equals(extension);
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
                    Media.Messager( this, "The Vehicle was not saved." );
                    return null;
                }
            }
        }

        return retval;
    }

    private void CheckFileName( String s ) throws Exception {
        if( s.contains( "\\" ) ) {
            throw new Exception( "The Vehicle name or model contains a back slash\nwhich should be removed before saving." );
        }
        if( s.contains( "/" ) ) {
            throw new Exception( "The Vehicle name or model contains a forward slash\nwhich should be removed before saving." );
        }
        if( s.contains( "*" ) ) {
            throw new Exception( "The Vehicle name or model contains an asterisk\nwhich should be removed before saving." );
        }
    }

    private boolean VerifyVehicle( ActionEvent evt ) {
        // if we have an Omni, remember which loadout was selected
        String CurLoadout = "";
        SetSource = false;
        if( CurVee.IsOmni() ) {
            CurLoadout = CurVee.GetLoadout().GetName();
        }

        // Ensure the mech has a name
        if( CurVee.GetName().isEmpty() ) {
            Media.Messager( this, "Your Vehicle needs a name first." );
            tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
            txtVehicleName.requestFocusInWindow();
            SetSource = true;
            return false;
        }

        // if we have any systems that requires ECM and don't have it, let the user know
        //if( ! locArmor.ValidateECM() ) {
        //    Media.Messager( "This 'Mech requires an ECM system of some sort to be valid.\nPlease install an ECM system." );
        //    tbpMainTabPane.setSelectedComponent( pnlEquipment );
        //    SetSource = true;
        //    return false;
        //}

        // ensure we're not overweight
        if( CurVee.IsOmni() ) {
            ArrayList v = CurVee.GetLoadouts();
            for (Object o : v) {
                CurVee.SetCurLoadout(((ifCVLoadout) o).GetName());
                if (CurVee.GetCurrentTons() > CurVee.GetTonnage()) {
                    Media.Messager(this, ((ifCVLoadout) o).GetName() +
                            " loadout is overweight.  Reduce the weight\nto equal or below the Vehicle's tonnage.");
                    //cmbOmniVariant.setSelectedItem( ((ifCVLoadout) v.get( i )).GetName() );
                    //cmbOmniVariantActionPerformed( evt );
                    tbpMainTabPane.setSelectedComponent(pnlBasicSetup);
                    SetSource = true;
                    return false;
                }
            }
        } else {
            if( CurVee.GetCurrentTons() > CurVee.GetTonnage() ) {
                Media.Messager( this, "This Vehicle is overweight.  Reduce the weight to\nequal or below the Vehicle's tonnage." );
                tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
                SetSource = true;
                return false;
            }
        }
        if( CurVee.IsOmni() ) {
            CurVee.SetCurLoadout( CurLoadout );
        }
        SetSource = true;
        return true;
    }

    private void EnableJumpJets( boolean enable ) {
        // this enables or disables the jump jet spinner if needed
        if( enable ) {
            spnJumpMP.setEnabled( true );
            if( CurVee.IsOmni() && CurVee.GetBaseLoadout().GetJumpJets().GetNumJJ() > 0 ) {
                //cmbJumpJetType.setEnabled( false );
            } else {
                //cmbJumpJetType.setEnabled( true );
            }
        } else {
            CurVee.GetJumpJets().ClearJumpJets();
            spnJumpMP.setEnabled( false );
            //cmbJumpJetType.setEnabled( false );
        }
        FixJJSpinnerModel();
    }

    private void RecalcEngine() {
        // first, get the current number of free heat sinks
        int OldFreeHS = CurVee.GetEngine().FreeHeatSinks();

        // changes the engine type.  Changing the type does not change the rating
        // which makes our job here easier.
        String OldVal = BuildLookupName( CurVee.GetEngine().GetCurrentState() );
        String LookupVal = (String) cmbEngineType.getSelectedItem();
        if( OldVal.equals( LookupVal ) ) { return; }
        ifVisitor v = CurVee.Lookup( LookupVal );
        try {
            CurVee.Visit( v );
        } catch( Exception e ) {
            v = CurVee.Lookup( OldVal );
            try {
                Media.Messager( this, "The new engine type is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous engine." );
                CurVee.Visit( v );
                cmbEngineType.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old engine:\n" + e.getMessage() + "\nStarting over with a new Vehicle.  Sorry." );
                GetNewVee();
            }
        }
    }

    private void GetNewVee() {
        boolean Omni = CurVee.IsOmni();

        quirks = new ArrayList<Quirk>();
        CurVee = new CombatVehicle();
        spnTonnage.setModel(new SpinnerNumberModel(CurVee.GetTonnage(), 1, CurVee.GetMaxTonnage(), 1));
        spnCruiseMP.setModel(new SpinnerNumberModel(CurVee.getCruiseMP(), 1, CurVee.getMaxCruiseMP(), 1));
        spnHeatSinks.setModel(new SpinnerNumberModel(CurVee.GetHeatSinks().GetNumHS(), 0, 50, 1));

        cmbMotiveType.setSelectedIndex( 0 );
        chkYearRestrict.setSelected( false );
        txtProdYear.setText( "" );
        cmbEra.setEnabled( true );
        cmbProductionEra.setEnabled( true );
        cmbTechBase.setEnabled( true );
        cmbTurret.setSelectedIndex(0);
        spnTurretTonnage.setModel(new SpinnerNumberModel(0.0, 0.0, 50.0, 0.5));
        spnRearTurretTonnage.setModel(new SpinnerNumberModel(0.0, 0.0, 50.0, 0.5));
        txtProdYear.setEnabled( true );

        cmbRulesLevel.setSelectedItem( Prefs.get( "NewVee_RulesLevel", "Tournament Legal" ) );
        cmbEra.setSelectedItem( Prefs.get( "NewVee_Era", "Age of War/Star League" ) );
        BuildTechBaseSelector();
        cmbProductionEra.setSelectedIndex( 0 );

        chkFlotationHull.setSelected(false);
        chkLimitedAmph.setSelected(false);
        chkFullAmph.setSelected(false);
        chkDuneBuggy.setSelected(false);
        chkEnviroSealing.setSelected(false);
        chkTrailer.setSelected(false);
        chkSupercharger.setSelected(false);
        chkJetBooster.setSelected(false);

        if( Omni ) {
            UnlockGUIFromOmni();
        }

        CurVee.SetEra( cmbEra.getSelectedIndex() );
        CurVee.SetProductionEra( cmbProductionEra.getSelectedIndex() );
        CurVee.SetRulesLevel( cmbRulesLevel.getSelectedIndex() );
        switch( CurVee.GetEra() ) {
        case AvailableCode.ERA_STAR_LEAGUE:
            CurVee.SetYear( 2750, false );
            break;
        case AvailableCode.ERA_SUCCESSION:
            CurVee.SetYear( 3025, false );
            break;
        case AvailableCode.ERA_CLAN_INVASION:
            CurVee.SetYear( 3070, false );
            break;
        case AvailableCode.ERA_DARK_AGES:
            CurVee.SetYear( 3130, false );
            break;
        case AvailableCode.ERA_ALL:
            CurVee.SetYear( 0, false );
            break;
        }

        cmbTechBase.setSelectedItem( Prefs.get( "NewVee_Techbase", "Inner Sphere" ) );
        switch( cmbTechBase.getSelectedIndex() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                CurVee.SetInnerSphere();
                break;
            case AvailableCode.TECH_CLAN:
                CurVee.SetClan();
                break;
            case AvailableCode.TECH_BOTH:
                CurVee.SetMixed();
                break;
        }
        txtVehicleName.setText( CurVee.GetName() );
        txtModel.setText( CurVee.GetModel() );

        try {
            CurVee.Visit( new VMechFullRecalc() );
        } catch( Exception e ) {
            // this should never throw an exception, but log it anyway
            System.err.println( e.getMessage() );
        }

        FixTonnageSpinner( CurVee.GetMinTonnage(), CurVee.GetMaxTonnage() );
        BuildChassisSelector();
        BuildEngineSelector();
        BuildArmorSelector();
        BuildTurretSelector();
        BuildExpEquipmentSelector();
        CheckOmni();
        FixMPSpinner();
        FixJJSpinnerModel();
        FixArmorSpinners();
        data.Rebuild( CurVee );
        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();

        tblQuirks.setModel(new tbQuirks(new ArrayList<Quirk>()));

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
            public int getRowCount() { return CurVee.GetLoadout().GetEquipment().size(); }
            public int getColumnCount() { return 2; }
            public Object getValueAt( int row, int col ) {
                if (CurVee.GetLoadout().GetEquipment().isEmpty()) { return null; }
                if (CurVee.GetLoadout().GetEquipment().size() <= row) { return null; }
                Object o = CurVee.GetLoadout().GetEquipment().get( row );
                if( col == 1 ) {
                    return CommonTools.UnknownToEmpty( ((abPlaceable) o).GetManufacturer() );
                } else {
                    return ((abPlaceable) o).CritName();
                }
            }
            @Override
            public boolean isCellEditable( int row, int col ) {
                return col != 0;
            }
            @Override
            public void setValueAt( Object value, int row, int col ) {
                if( col == 0 ) { return; }
                if( ! ( value instanceof String ) ) { return; }
                abPlaceable a = (abPlaceable) CurVee.GetLoadout().GetEquipment().get( row );
                //if( chkIndividualWeapons.isSelected() ) {
//                    a.SetManufacturer( (String) value );
                //    fireTableCellUpdated( row, col );
                //} else {
                    ArrayList v = CurVee.GetLoadout().GetEquipment();
                for (Object o : v) {
                    if (FileCommon.LookupStripArc(((abPlaceable) o).LookupName()).equals(FileCommon.LookupStripArc(a.LookupName()))) {
                        ((abPlaceable) o).SetManufacturer((String) value);
                    }
                }
                    fireTableDataChanged();
                //}
            }
        } );

        tblWeaponManufacturers.getInputMap( javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_TAB, 0, false ), "selectNextRow" );

        if( cmbEra.getSelectedIndex() == AvailableCode.ERA_ALL ) {
            chkYearRestrict.setEnabled( false );
        } else {
            chkYearRestrict.setEnabled( true );
        }
        CurVee.SetChanged( false );
        setTitle( saw.Constants.AppDescription + " " + saw.Constants.GetVersion() );
    }

    private void CheckOmni() {
        // deals with the Omni checkbox if needed
        if( CommonTools.IsAllowed( CurVee.GetOmniAvailability(), CurVee ) ) {
            chkOmniVee.setEnabled( true );
        } else {
            chkOmniVee.setEnabled( false );
            chkOmniVee.setSelected( false );
        }

        // now let's ensure that all the omni controls are enabled or disabled
        // as appropriate
        if( chkOmniVee.isEnabled() ) {
            if( chkOmniVee.isSelected() ) {
                //btnLockChassis.setEnabled( true );
            } else {
                //btnLockChassis.setEnabled( false );
            }
        } else {
            //btnLockChassis.setEnabled( false );
        }
    }

    private void RefreshEquipment() {
        // refreshes the equipment selectors
        //fix the CASE control
        CASE Case = new CASE();
        setCheckbox(chkCASE, ( CommonTools.IsAllowed( Case.GetAvailability(), CurVee) || CurVee.GetTechBase() == AvailableCode.TECH_CLAN ), CurVee.GetLoadout().HasCase(), CurVee.isOmni());

        // fix Artemis IV controls
        ifMissileGuidance ArtCheck = new ArtemisIVFCS( null );
        setCheckbox(chkFCSAIV, ( CommonTools.IsAllowed( ArtCheck.GetAvailability(), CurVee ) ), CurVee.UsingArtemisIV(), CurVee.isOmni());

        // fix Artemis V controls
        ArtCheck = new ArtemisVFCS( null );
        setCheckbox(chkFCSAV, ( CommonTools.IsAllowed( ArtCheck.GetAvailability(), CurVee ) ), CurVee.UsingArtemisV(), CurVee.isOmni());

        // fix ApolloFCS controls
        ArtCheck = new ApolloFCS( null );
        setCheckbox(chkFCSApollo, ( CommonTools.IsAllowed( ArtCheck.GetAvailability(), CurVee ) ), CurVee.UsingApollo(), CurVee.isOmni());

        // fix the targeting computer display
        setCheckbox(chkUseTC, ( CommonTools.IsAllowed( CurVee.GetTC().GetAvailability(), CurVee ) ), CurVee.UsingTC(), CurVee.isOmni());

        setCheckbox(chkSupercharger, ( CommonTools.IsAllowed( CurVee.GetLoadout().GetSupercharger().GetAvailability(), CurVee ) && !CurVee.IsVTOL() ),  CurVee.GetLoadout().HasSupercharger(), CurVee.isOmni());
        setCheckbox(chkJetBooster, ( CommonTools.IsAllowed( CurVee.GetLoadout().GetVTOLBooster().GetAvailability(), CurVee ) && CurVee.IsVTOL() && !CurVee.IsOmni() ), CurVee.GetLoadout().HasVTOLBooster(), CurVee.isOmni());
        setCheckbox(chkArmoredMotive, ( CommonTools.IsAllowed( CurVee.GetLoadout().GetArmoredMotiveSystem().GetAvailability(), CurVee ) && !CurVee.IsVTOL() ), CurVee.GetLoadout().HasArmoredMotiveSystem(), CurVee.isOmni());

        if( ! chkUseTC.isEnabled() ) { CurVee.UseTC( false, false ); }

        if( CurVee.GetRulesLevel() >= AvailableCode.RULES_EXPERIMENTAL ) {
            chkFractional.setEnabled( true );
        } else {
            chkFractional.setEnabled( false );
            CurVee.SetFractionalAccounting( false );
        }
        chkFractional.setSelected( CurVee.UsingFractionalAccounting() );

        if( CurVee.IsOmni() ) {
            // these items can only be loaded into the base chassis, so they
            // are always locked for an Omni (although they may be checked).
            chkEnviroSealing.setEnabled( false );
            chkCommandConsole.setEnabled( false );
            chkJetBooster.setEnabled(false);

            //These items can be selected on the base or variants but if the base
            //has them disable and mark as selected
            if( CurVee.GetBaseLoadout().HasSupercharger() ) {
                chkSupercharger.setEnabled( false );
                chkSupercharger.setSelected( true );
            }
            if( CurVee.GetBaseLoadout().HasCase() ) {
                chkCASE.setEnabled( false );
                chkCASE.setSelected( true );
            }
        }
    }

    private void Log(String message) {
        txtLog.append(message + "\n");
    }

    private void setCheckbox(JCheckBox element, Boolean isEnabled, Boolean isSelected, Boolean allowSelection) {
        element.setEnabled(isEnabled);
        element.setSelected(false);
        if (isEnabled || allowSelection) {
            element.setSelected(isSelected);
        }
    }
    private void SetWeaponChoosers() {
        // sets the weapon choosers up.  first, get the user's choices.

        // get the equipment lists for the choices.
        data.Rebuild( CurVee );
        Equipment[ENERGY] = data.GetEquipment().GetEnergyWeapons( CurVee );
        Equipment[MISSILE] = data.GetEquipment().GetMissileWeapons( CurVee );
        Equipment[BALLISTIC] = data.GetEquipment().GetBallisticWeapons( CurVee );
        Equipment[PHYSICAL] = data.GetEquipment().GetPhysicalWeapons( CurVee );
        Equipment[ARTILLERY] = data.GetEquipment().GetArtillery( CurVee );
        Equipment[EQUIPMENT] = data.GetEquipment().GetEquipment( CurVee );
        Equipment[AMMUNITION] = new Object[] { " " };
        if(CurVee.GetLoadout().GetNonCore().toArray().length == 0) {
            Equipment[SELECTED] = new Object[] { " " };
        } else {
            Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
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
        lstChooseEquipment.setListData( Equipment[EQUIPMENT] );
        lstChoosePhysical.setListData( Equipment[PHYSICAL] );
        lstChooseAmmunition.setListData( Equipment[AMMUNITION] );
        lstSelectedEquipment.setListData( Equipment[SELECTED] );
        lstChooseArtillery.setListData( Equipment[ARTILLERY] );
        lstSelectedEquipment.repaint();

        ResetAmmo();
    }

    private void UnlockGUIFromOmni() {
        // this should be used anytime a new mech is made or when unlocking
        // an Omni.
        isLocked = false;

        chkOmniVee.setSelected( false );
        chkOmniVee.setEnabled( true );
        mnuUnlock.setEnabled( false );
        cmbMotiveType.setEnabled( true );
        spnTonnage.setEnabled( true );
        cmbEngineType.setEnabled( true );
        cmbTurret.setEnabled( true );
        spnTurretTonnage.setEnabled( true );
        spnRearTurretTonnage.setEnabled(true);
        spnFrontArmor.setEnabled( true );
        spnLeftArmor.setEnabled( true );
        spnRightArmor.setEnabled( true );
        spnRearArmor.setEnabled( true );
        spnRotorArmor.setEnabled( true );
        spnTurretArmor.setEnabled( true );
        spnRearTurretArmor.setEnabled( true );
        cmbArmorType.setEnabled( true );
        btnMaximize.setEnabled( true );
        btnSetArmorTons.setEnabled( true );
        btnUseRemaining.setEnabled( true );
        chkTrailer.setEnabled( true );
        //btnEfficientArmor.setEnabled( true );
        //btnBalanceArmor.setEnabled( true );
        chkFCSAIV.setEnabled( true );
        chkFCSAV.setEnabled( true );
        chkFCSApollo.setEnabled( true );
        btnLockChassis.setEnabled( true );
        spnCruiseMP.setEnabled( true );
        chkYearRestrict.setEnabled( true );
        chkSupercharger.setEnabled( true );
        chkJetBooster.setEnabled(true);
        chkEnviroSealing.setEnabled( false );
        // now enable the Omni controls
        cmbOmniVariant.setEnabled( false );
        btnAddVariant.setEnabled( false );
        btnDeleteVariant.setEnabled( false );
        btnRenameVariant.setEnabled( false );
    }

    private void RecalcArmor() {
        // changes the armor type.
        String OldVal = BuildLookupName( CurVee.GetArmor().GetCurrentState() );
        String LookupVal = (String) cmbArmorType.getSelectedItem();
        if( OldVal.equals( LookupVal ) ) { return; }
        ifVisitor v = (ifVisitor) CurVee.Lookup( LookupVal );
        try {
            CurVee.Visit( v );
        } catch( Exception e ) {
            v = (ifVisitor) CurVee.Lookup( OldVal );
            try {
                Media.Messager( this, "The new armor type is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous armor." );
                CurVee.Visit( v );
                cmbArmorType.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old armor:\n" + e.getMessage() + "\nStarting over with a new Vehicle.  Sorry." );
                GetNewVee();
            }
        }
        if( CurVee.GetArmor().IsStealth() ) {
            if( ! AddECM() ) {
                v = (ifVisitor) CurVee.Lookup( OldVal );
                try {
                    Media.Messager( this, "No ECM Suite was available for this armor type!\nReverting to the previous armor." );
                    CurVee.Visit( v );
                    cmbArmorType.setSelectedItem( OldVal );
                } catch( Exception e ) {
                    // wow, second one?  Get a new 'Mech.
                    Media.Messager( this, "Fatal error while attempting to revert to the old armor:\n" + e.getMessage() + "\nStarting over with a new Vehicle.  Sorry." );
                    GetNewVee();
                }
            }
        }
        SetPatchworkArmor();
    }

    private boolean AddECM() {
        // Adds an ECM suite if a certain system needs it
        if( Prefs.getBoolean( "AutoAddECM", true ) ) {
            if( ! CurVee.ValidateECM() ) {
                abPlaceable a = data.GetEquipment().GetEquipmentByName( "Guardian ECM Suite", CurVee );
                if( a == null ) {
                    a = data.GetEquipment().GetEquipmentByName( "Angel ECM", CurVee );
                    if( a == null ) {
                        a = data.GetEquipment().GetEquipmentByName( "ECM Suite", CurVee );
                        if( a == null ) {
                            a = data.GetEquipment().GetEquipmentByName( "Watchdog CEWS", CurVee );
                            if( a == null ) {
                                return false;
                            }
                        }
                    }
                }
                try {
                    CurVee.GetLoadout().AddTo( a, LocationIndex.CV_LOC_BODY );
                    Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
                    lstSelectedEquipment.setListData( Equipment[SELECTED] );
                } catch (Exception ex) {
                    Media.Messager(ex.getMessage());
                    return false;
                }
            }
        } else {
            Media.Messager( this, "Please add an appropriate ECM Suite to complement this\n system.  The Vehicle is not valid without an ECM Suite." );
        }
        return true;
    }

    private void SetPatchworkArmor() {
        /*
        if( locArmor.GetArmor().IsPatchwork() ) {
            pnlPatchworkChoosers.setVisible( true );
            BuildPatchworkChoosers();
            if( locArmor.IsQuad() ) {
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
            cmbPWHDType.setSelectedItem( BuildLookupName( (ifState) locArmor.GetArmor().GetHDArmorType() ) );
            cmbPWCTType.setSelectedItem( BuildLookupName( (ifState) locArmor.GetArmor().GetCTArmorType() ) );
            cmbPWLTType.setSelectedItem( BuildLookupName( (ifState) locArmor.GetArmor().GetLTArmorType() ) );
            cmbPWRTType.setSelectedItem( BuildLookupName( (ifState) locArmor.GetArmor().GetRTArmorType() ) );
            cmbPWLAType.setSelectedItem( BuildLookupName( (ifState) locArmor.GetArmor().GetLAArmorType() ) );
            cmbPWRAType.setSelectedItem( BuildLookupName( (ifState) locArmor.GetArmor().GetRAArmorType() ) );
            cmbPWLLType.setSelectedItem( BuildLookupName( (ifState) locArmor.GetArmor().GetLLArmorType() ) );
            cmbPWRLType.setSelectedItem( BuildLookupName( (ifState) locArmor.GetArmor().GetRLArmorType() ) );
        } else {
            pnlPatchworkChoosers.setVisible( false );
        }
         */
    }

    private void RecalcPatchworkArmor( int Loc ) {
        VArmorSetPatchworkLocation LCVis = new VArmorSetPatchworkLocation();
        LCVis.SetLocation( Loc );
        if( CurVee.GetBaseTechbase() == AvailableCode.TECH_CLAN ) {
                LCVis.SetClan( false );
        }
        /*
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
            LCVis.Visit( locArmor );
        } catch( Exception e ) {
            Media.Messager( this, e.getMessage() );
            switch( Loc ) {
                case LocationIndex.MECH_LOC_HD:
                    cmbPWHDType.setSelectedItem( locArmor.GetArmor().GetHDArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_CT:
                    cmbPWCTType.setSelectedItem( locArmor.GetArmor().GetCTArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_LT:
                    cmbPWLTType.setSelectedItem( locArmor.GetArmor().GetLTArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_RT:
                    cmbPWRTType.setSelectedItem( locArmor.GetArmor().GetRTArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_LA:
                    cmbPWLAType.setSelectedItem( locArmor.GetArmor().GetLAArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_RA:
                    cmbPWRAType.setSelectedItem( locArmor.GetArmor().GetRAArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_LL:
                    cmbPWLLType.setSelectedItem( locArmor.GetArmor().GetLLArmorType().LookupName() );
                    break;
                case LocationIndex.MECH_LOC_RL:
                    cmbPWRLType.setSelectedItem( locArmor.GetArmor().GetRLArmorType().LookupName() );
                    break;
            }
        }
         */
    }

    private void RecalcEquipment() {
        // recalculates the equipment if anything changes
        boolean clan = false;
        switch( CurVee.GetTechbase() ) {
            case AvailableCode.TECH_CLAN: case AvailableCode.TECH_BOTH:
                // this is the default value to use assuming that during mixed
                // tech operations the user will use the best.
                clan = true;
        }
    }

    private void FixHeatSinkSpinnerModel() {
        // mainly provided for Omnis.
        if( CurVee.IsOmni() ) {
            spnHeatSinks.setModel( new javax.swing.SpinnerNumberModel(
                CurVee.GetHeatSinks().GetNumHS(), CurVee.GetHeatSinks().GetBaseLoadoutNumHS(), 65, 1) );
        } else {
            spnHeatSinks.setModel( new javax.swing.SpinnerNumberModel(
                CurVee.GetHeatSinks().GetNumHS(), ((CVLoadout)CurVee.GetLoadout()).GetTotalHeat(), 65, 1) );
        }

        ((JSpinner.DefaultEditor)spnHeatSinks.getEditor()).getTextField().addFocusListener(spinners);
    }

    private void btnNewVeeActionPerformed(java.awt.event.ActionEvent evt) {
        if( CurVee.HasChanged() ) {
            int choice = javax.swing.JOptionPane.showConfirmDialog( this,
                "The current Vehicle has changed.\nDo you want to discard those changes?", "Discard Changes?", javax.swing.JOptionPane.YES_NO_OPTION );
            if( choice == 1 ) { return; }
        }
        GetNewVee();
        Prefs.put("Currentfile", "");
    }

    private void RefreshOmniVariants() {
        ArrayList v = CurVee.GetLoadouts();
        String[] variants = new String[v.size()];
        if(v.size() == 0) {
            variants = new String[] { common.Constants.BASELOADOUT_NAME };
        } else {
            for( int i = 0; i < v.size(); i++ ) {
                variants[i] = ((ifCVLoadout) v.get(i)).GetName();
            }
        }

        // set the current loadout source before changing
        txtSource.setText( CurVee.getSource() );
        cmbOmniVariant.setModel( new javax.swing.DefaultComboBoxModel( variants ) );
        cmbOmniVariant.setSelectedItem( CurVee.GetLoadout().GetName() );
    }

    private void RefreshOmniChoices() {
        // this changes the GUI when a new variant is loaded to reflect the
        // equipment it has loaded.
        CheckEquipment();
    }

    private void CheckEquipment() {
        // consolidating some code here.
        chkFCSAIV.setSelected( CurVee.UsingArtemisIV() );
        chkFCSAV.setSelected( CurVee.UsingArtemisV() );
        chkFCSApollo.setSelected( CurVee.UsingApollo() );
        chkUseTC.setSelected( CurVee.UsingTC() );
        chkJetBooster.setSelected( CurVee.GetBaseLoadout().HasVTOLBooster() );
        chkSupercharger.setSelected( CurVee.GetBaseLoadout().HasSupercharger() );
        chkCASE.setSelected( CurVee.GetLoadout().HasCase() );
    }

    private void LockGUIForOmni() {
        // this locks most of the GUI controls.  Used mainly by OmniVehichles.
        isLocked = true;

        chkOmniVee.setSelected( true );
        chkOmniVee.setEnabled( false );
        mnuUnlock.setEnabled( true );
        spnTonnage.setEnabled( false );
        cmbMotiveType.setEnabled( false );
        cmbEngineType.setEnabled( false );
        cmbTurret.setEnabled( false );
        spnTurretTonnage.setEnabled( false );
        spnRearTurretTonnage.setEnabled(false);
        spnFrontArmor.setEnabled( false );
        spnLeftArmor.setEnabled( false );
        spnRightArmor.setEnabled( false );
        spnRearArmor.setEnabled( false );
        spnTurretArmor.setEnabled( false );
        spnRearTurretArmor.setEnabled( false );
        spnRotorArmor.setEnabled( false );
        cmbArmorType.setEnabled( false );
        btnMaximize.setEnabled( false );
        btnSetArmorTons.setEnabled( false );
        btnUseRemaining.setEnabled( false );
        btnLockChassis.setEnabled( false );
        chkYearRestrict.setEnabled( false );
        chkTrailer.setEnabled( false );
        if( CurVee.GetBaseLoadout().GetJumpJets().GetNumJJ() > 0 ) {
            //cmbJumpJetType.setEnabled( false );
        }
        spnCruiseMP.setEnabled( false );
        if( chkFCSAIV.isSelected() ) {
            chkFCSAIV.setEnabled( false );
        }
        if( chkFCSAV.isSelected() ) {
            chkFCSAV.setEnabled( false );
        }
        if( chkFCSApollo.isSelected() ) {
            chkFCSApollo.setEnabled( false );
        }

        chkFractional.setEnabled( false );
        chkEnviroSealing.setEnabled( false );
        chkJetBooster.setEnabled(false);
        if( CurVee.GetBaseLoadout().HasSupercharger() ) {
            chkSupercharger.setEnabled( false );
        }
        if ( chkCASE.isSelected() ) {
            chkCASE.setEnabled(false);
        }

        // now enable the omnimech controls
        cmbOmniVariant.setEnabled( true );
        btnAddVariant.setEnabled( true );
        btnDeleteVariant.setEnabled( true );
        btnRenameVariant.setEnabled( true );
    }

    private void cmbOmniVariantActionPerformed(java.awt.event.ActionEvent evt) {
        SaveOmniFluffInfo();
        String variant = (String) cmbOmniVariant.getSelectedItem();
        boolean changed = CurVee.HasChanged();

        CurVee.SetCurLoadout(variant);

        // now fix the GUI
        LoadOmniFluffInfo();
        SetWeaponChoosers();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        RefreshOmniVariants();
        RefreshEquipment();
        RefreshOmniChoices();
        RefreshSummary();
        RefreshInfoPane();

        // this prevents the program from setting the changed tag if we simply
        // open an omnimech for browsing.
        CurVee.SetChanged(changed);
    }

    private void mnuNewMechActionPerformed(java.awt.event.ActionEvent evt) {
        btnNewVeeActionPerformed(evt);
    }

    private void mnuLoadActionPerformed(java.awt.event.ActionEvent evt) {
        if (CurVee.HasChanged()) {
            int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                    "The current Vehicle has changed.\nDo you want to discard those changes?", "Discard Changes?", javax.swing.JOptionPane.YES_NO_OPTION);
            if (choice == 1) {
                return;
            }
        }
        GetNewVee();
        // Get the mech we're loading
        CombatVehicle m = LoadVehicle();
        if (m == null) {
            return;
        }
        CurVee = m;
        LoadVehicleIntoGUI();
        CurVee.SetChanged(false);
    }

    private void mnuOpenActionPerformed(java.awt.event.ActionEvent evt) {
        btnOpenActionPerformed(evt);
    }

    public CombatVehicle LoadVehicle() {
        CombatVehicle m = null;

        File tempFile = new File( Prefs.get( "LastOpenCVDirectory", "" ) );
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter( new javax.swing.filechooser.FileFilter() {
            public boolean accept( File f ) {
                if (f.isDirectory()) {
                    return true;
                }

                String extension = Utils.getExtension( f );
                if ( extension != null ) {
                    if ( extension.equals( "saw" ) ) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }

            //The description of this filter
            public String getDescription() {
                return "*.saw";
            }
        } );
        fc.setAcceptAllFileFilterUsed( false );
        fc.setCurrentDirectory( tempFile );
        int returnVal = fc.showDialog( this, "Load Vehicle" );
        if( returnVal != JFileChooser.APPROVE_OPTION ) { return null; }
        File loadmech = fc.getSelectedFile();
        String filename = "";
        try {
            filename = loadmech.getCanonicalPath();
            Prefs.put("LastOpenCVDirectory", loadmech.getCanonicalPath().replace(loadmech.getName(), ""));
            Prefs.put("LastOpenCVFile", loadmech.getName());
            Prefs.put("CurrentCVfile", loadmech.getCanonicalPath());
        } catch( Exception e ) {
            Media.Messager( this, "There was a problem opening the file:\n" + e.getMessage() );
            return null;
        }

        try {
            CVReader XMLr = new CVReader();
            m = XMLr.ReadUnit( filename, data );
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

    private void LoadVehicleFromFile( String filename ) {
        CombatVehicle m = null;
        if (! filename.isEmpty() ) {
            try {
                CVReader XMLr = new CVReader();
                m = XMLr.ReadUnit( filename, data );
                CurVee = m;
                LoadVehicleIntoGUI();
                Prefs.put("Currentfile", filename);
            } catch( Exception e ) {
                // had a problem loading the mech.  let the user know.
                Media.Messager( e.getMessage() );
            }
        }
    }

    public void LoadVehicleIntoGUI() {
        // added for special situations
        Load = true;

        // Put it in the gui.
        UnlockGUIFromOmni();

        chkYearRestrict.setSelected( CurVee.IsYearRestricted() );
        txtProdYear.setText( "" + CurVee.GetYear() );
        cmbEra.setEnabled( true );
        cmbTechBase.setEnabled( true );
        txtProdYear.setEnabled( true );
        switch( CurVee.GetEra() ) {
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

        cmbRulesLevel.setSelectedIndex( CurVee.GetRulesLevel() );
        cmbEra.setSelectedIndex( CurVee.GetEra() );
        BuildTechBaseSelector();
        cmbProductionEra.setSelectedIndex( CurVee.GetProductionEra() );

        if( chkYearRestrict.isSelected() ) {
            cmbEra.setEnabled( false );
            cmbTechBase.setEnabled( false );
            txtProdYear.setEnabled( false );
        }

        //Set all the inputs
        txtVehicleName.setText( CurVee.GetName() );
        txtModel.setText( CurVee.GetModel() );
        cmbTechBase.setSelectedIndex( CurVee.GetLoadout().GetTechBase() );
        cmbMotiveType.setSelectedItem( CurVee.GetMotiveLookupName() );
        spnTonnage.setModel( new javax.swing.SpinnerNumberModel(CurVee.GetTonnage(), 1, CurVee.GetMaxTonnage(), 1) );
        spnCruiseMP.setModel( new javax.swing.SpinnerNumberModel(CurVee.getCruiseMP(), 1, CurVee.getMaxCruiseMP(), 1) );
        FixArmorSpinners();

        // now that we're done with the special stuff...
        Load = false;

        if( CurVee.IsOmni() ) {
            LockGUIForOmni();
            RefreshOmniVariants();
            RefreshOmniChoices();
        }

        spnTurretTonnage.setValue(0);
        spnRearTurretTonnage.setValue(0);
        if ( CurVee.isHasTurret1() ) {
            cmbTurret.setSelectedItem("Single Turret");
            if (chkOmniVee.isSelected()) {
                spnTurretTonnage.setEnabled(!isLocked);
                spnTurretTonnage.setValue(CurVee.GetLoadout().GetTurret().GetTonnage());
            }
        }
        if ( CurVee.isHasTurret2() ) {
            cmbTurret.setSelectedItem("Dual Turret");
            if (chkOmniVee.isSelected()) {
                spnRearTurretTonnage.setEnabled(!isLocked);
                spnRearTurretTonnage.setValue(CurVee.GetLoadout().GetRearTurret().GetTonnage());
            }
        }

        FixTonnageSpinner( CurVee.GetMinTonnage(), CurVee.GetMaxTonnage() );
        BuildChassisSelector();
        BuildEngineSelector();
        BuildArmorSelector();
        BuildTurretSelector();
        BuildExpEquipmentSelector();
        if (CurVee.isHasSponsonTurret())
            chkSponsonTurret.setSelected(true);
        cmbEngineType.setSelectedItem( BuildLookupName( CurVee.GetEngine().GetCurrentState() ) );
        cmbArmorType.setSelectedItem( BuildLookupName( CurVee.GetArmor().GetCurrentState() ) );
        SetPatchworkArmor();
        FixMPSpinner();
        FixHeatSinkSpinnerModel();
        FixJJSpinnerModel();
        data.Rebuild( CurVee );
        RefreshEquipment();
        chkUseTC.setSelected( CurVee.UsingTC() );
        chkCASE.setSelected( CurVee.GetLoadout().HasCase() );
        chkEnviroSealing.setSelected( CurVee.HasEnvironmentalSealing() );
        //chkCommandConsole.setSelected( CurVee.HasCommandConsole() );
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();
        RefreshQuirks();

        // load the fluff image.
        Media media = new Media();
        media.blankLogo(lblFluffImage);
        media.setLogo(lblFluffImage, media.DetermineMatchingImage(CurVee.GetName(), CurVee.GetModel(), CurVee.GetSSWImage()));

        quirks = CurVee.GetQuirks();
        Overview.SetText( CurVee.getOverview() );
        Capabilities.SetText( CurVee.getCapabilities() );
        History.SetText( CurVee.getHistory() );
        Deployment.SetText( CurVee.getDeployment() );
        Variants.SetText( CurVee.getVariants() );
        Notables.SetText( CurVee.getNotables() );
        Additional.SetText( CurVee.GetAdditional() );
        txtManufacturer.setText( CommonTools.UnknownToEmpty( CurVee.GetCompany() ) );
        txtManufacturerLocation.setText( CommonTools.UnknownToEmpty( CurVee.GetLocation() ) );
        txtEngineManufacturer.setText( CommonTools.UnknownToEmpty( CurVee.GetEngineManufacturer() ) );
        txtArmorModel.setText( CommonTools.UnknownToEmpty( CurVee.GetArmorModel() ) );
        txtChassisModel.setText( CommonTools.UnknownToEmpty( CurVee.GetChassisModel() ) );
        if( CurVee.GetJumpJets().GetNumJJ() > 0 ) {
            txtJJModel.setEnabled( true );
        }
        txtSource.setText( CurVee.getSource() );

        // omnimechs may have jump jets in one loadout and not another.
        txtJJModel.setText( CommonTools.UnknownToEmpty( CurVee.GetJJModel() ) );
        txtCommSystem.setText( CommonTools.UnknownToEmpty( CurVee.GetCommSystem() ) );
        txtTNTSystem.setText( CommonTools.UnknownToEmpty( CurVee.GetTandTSystem() ) );

        setTitle( saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel() );
        CurVee.SetChanged(false);
    }

    private void mnuImportHMPActionPerformed(java.awt.event.ActionEvent evt) {
        if (CurVee.HasChanged()) {
            int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                    "The current Vehicle has changed.\nDo you want to discard those changes?", "Discard Changes?", javax.swing.JOptionPane.YES_NO_OPTION);
            if (choice == 1) {
                return;
            }
        }

        // Get the mech we're loading
        CombatVehicle m = null;

        File tempFile = new File(Prefs.get("LastOpenCVDirectory", ""));
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                String extension = Utils.getExtension(f);
                if (extension != null) {
                    if (extension.equals("hmp")) {
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
        });
        fc.setAcceptAllFileFilterUsed(false);
        fc.setCurrentDirectory(tempFile);
        int returnVal = fc.showDialog(this, "Import HMP File");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File loadmech = fc.getSelectedFile();
        String filename = "";
        try {
            filename = loadmech.getCanonicalPath();
            Prefs.put("LastOpenCVDirectory", loadmech.getCanonicalPath().replace(loadmech.getName(), ""));
            Prefs.put("LastOpenCVFile", loadmech.getName());
        } catch (Exception e) {
            Media.Messager(this, "There was a problem opening the file:\n" + e.getMessage());
            return;
        }

        String Messages = "";
        try {
            HMPReader HMPr = new HMPReader();
            m = HMPr.GetVehicle(filename, false);
            Messages = HMPr.GetErrors();
        } catch (Exception e) {
            // had a problem loading the mech.  let the user know.
            if (e.getMessage() == null) {
                Media.Messager(this, "An unknown error has occured.  The log file has been updated.");
                e.printStackTrace();
            } else {
                Media.Messager(this, e.getMessage());
            }
            return;
        }

        if (Messages.length() > 0) {
            dlgTextExport msgs = new dlgTextExport(this, false, Messages);
            msgs.setLocationRelativeTo(this);
            msgs.setVisible(true);
        }

        CurVee = m;
        LoadVehicleIntoGUI();
        CurVee.SetChanged(false);
    }

    private void mnuBatchHMPActionPerformed(java.awt.event.ActionEvent evt) {
        dlgBatchHMP batch = new dlgBatchHMP(this, true);
        batch.setLocationRelativeTo(this);
        batch.setVisible(true);
    }

    private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {
        // Solidify the mech first.
        setCursor(Hourglass);

        File savemech = GetSaveFile("saw", Prefs.get("LastOpenCVDirectory", ""), true, false);
        if (savemech == null) {
            setCursor(NormalCursor);
            return;
        }

        //Since we are saving to a new file update the stored prefs
        try {
            Prefs.put("LastOpenCVDirectory", savemech.getCanonicalPath().replace(savemech.getName(), ""));
            Prefs.put("LastOpenCVFile", savemech.getName());
            Prefs.put("Currentfile", savemech.getCanonicalPath());
        } catch (IOException e) {
            Media.Messager(this, "There was a problem with the file:\n" + e.getMessage());
            setCursor(NormalCursor);
            return;
        }

        // exports the mech to XML format
        String CurLoadout = "";
        if (CurVee.IsOmni()) {
            CurLoadout = CurVee.GetLoadout().GetName();
            SaveOmniFluffInfo();
        }

        // save the mech to XML in the current location
        CVWriter XMLw = new CVWriter(CurVee);
        try {
            String file = savemech.getCanonicalPath();
            String ext = Utils.getExtension(savemech);
            if (ext == null || ext.equals("")) {
                file += ".saw";
            } else {
                if (!ext.equals("saw")) {
                    file.replace("." + ext, ".saw");
                }
            }
            XMLw.WriteXML(file);

            // if there were no problems, let the user know how it went
            if (evt != null && evt.getActionCommand().equals("Save Vehicle")) {
                Media.Messager(this, "Vehicle saved successfully:\n" + file);
            }
        } catch (IOException e) {
            Media.Messager(this, "There was a problem writing the file:\n" + e.getMessage());
            setCursor(NormalCursor);
            return;
        }

        // lastly, if this is an omnimech, reset the display to the last loadout
        if (CurVee.IsOmni()) {
            SetSource = false;
            cmbOmniVariant.setSelectedItem(CurLoadout);
            cmbOmniVariantActionPerformed(evt);
            SetSource = true;
        }

        setCursor(NormalCursor);
        setTitle(saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel());
        CurVee.SetChanged(false);
    }

    private void mnuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {
        setCursor(Hourglass);
        File savemech = GetSaveFile("saw", Prefs.get("LastOpenCVDirectory", ""), false, false);
        if (savemech == null) {
            setCursor(NormalCursor);
            return;
        }

        // exports the mech to XML format
        String CurLoadout = "";
        if (CurVee.IsOmni()) {
            CurLoadout = CurVee.GetLoadout().GetName();
        }

        // since we're doing a Save As...  we'll clear the S7 ID so that you can
        // post a variant without creating an entirely new 'Mech
        CurVee.SetSolaris7ID("0");

        // save the mech to XML in the current location
        CVWriter XMLw = new CVWriter(CurVee);
        try {
            String file = savemech.getCanonicalPath();
            String ext = Utils.getExtension(savemech);
            if (ext == null || ext.equals("")) {
                file += ".saw";
            } else {
                if (!ext.equals("saw")) {
                    file.replace("." + ext, ".saw");
                }
            }
            XMLw.WriteXML(file);
            // if there were no problems, let the user know how it went
            Media.Messager(this, "Vehicle saved successfully:\n" + file);
        } catch (IOException e) {
            Media.Messager(this, "There was a problem writing the file:\n" + e.getMessage());
            setCursor(NormalCursor);
            return;
        }

        //Since we are saving to a new file update the stored prefs
        try {
            Prefs.put("LastOpenCVDirectory", savemech.getCanonicalPath().replace(savemech.getName(), ""));
            Prefs.put("LastOpenCVFile", savemech.getName());
        } catch (IOException e) {
            Media.Messager(this, "There was a problem with the file:\n" + e.getMessage());
            setCursor(NormalCursor);
            return;
        }

        // lastly, if this is an omnimech, reset the display to the last loadout
        if (CurVee.IsOmni()) {
            cmbOmniVariant.setSelectedItem(CurLoadout);
            cmbOmniVariantActionPerformed(evt);
        }
        setTitle(saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel());
        CurVee.SetChanged(false);
        setCursor(NormalCursor);
    }

    private void mnuExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {
        SetSource = false;
        btnExportHTMLActionPerformed(evt);
        SetSource = true;
    }

    private void mnuExportMTFActionPerformed(java.awt.event.ActionEvent evt) {
        SetSource = false;
        btnExportMTFActionPerformed(evt);
        SetSource = true;
    }

    private void mnuExportTXTActionPerformed(java.awt.event.ActionEvent evt) {
        SetSource = false;
        btnExportTXTActionPerformed(evt);
        SetSource = true;
    }

    private void mnuExportClipboardActionPerformed(java.awt.event.ActionEvent evt) {
        // takes the text export and copies it to thesystem clipboard.
        String CurLoadout = "";
        String output = "";

        if (CurVee.IsOmni()) {
            CurLoadout = CurVee.GetLoadout().GetName();
        }

        // Solidify the mech first.
        SolidifyVehicle();

        if (!VerifyVehicle(evt)) {
            return;
        }

        CVTXTWriter txtw = new CVTXTWriter(CurVee);
        output = txtw.GetTextExport();
        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection(output);

        // lastly, if this is an omnimech, reset the display to the last loadout
        if (CurVee.IsOmni()) {
            cmbOmniVariant.setSelectedItem(CurLoadout);
            cmbOmniVariantActionPerformed(evt);
        }
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(export, this);
    }

    private void mnuCreateTCGMechActionPerformed(java.awt.event.ActionEvent evt) {
        // Create CCG stats for mech
        // TODO: Add handling code to check if a canon card already exists.
        SolidifyVehicle();
        //dlgCCGMech ccgMech = new dlgCCGMech(this, true, CurVee);
        //ccgMech.setLocationRelativeTo(this);
        //ccgMech.setVisible(true);
    }

    private void mnuPrintPreviewActionPerformed(java.awt.event.ActionEvent evt) {
        btnPrintActionPerformed(evt);
    }

    public void QuickSave() {
        File saveFile = GetSaveFile( "saw", Prefs.get( "LastOpenCVDirectory", "" ), true, false );
        if ( saveFile != null ) {
            // save the mech to XML in the current location
            String curLoadout = CurVee.GetLoadout().GetName();
            CVWriter XMLw = new CVWriter( CurVee );
            try {
                XMLw.WriteXML( saveFile.getCanonicalPath() );
                CurVee.SetCurLoadout(curLoadout);
            } catch( IOException e ) {
                return;
            }
        } else {
            mnuSaveActionPerformed(null);
        }
    }

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {
        if (CurVee.HasChanged()) {
            int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                    "The current Vehicle has changed.\nDo you want to discard those changes?", "Discard Changes?", javax.swing.JOptionPane.YES_NO_OPTION);
            if (choice == 1) {
                return;
            }
        }
        CloseProgram();
    }

    private void mnuFileActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void CloseProgram() {
        try {
            if (dOpen != null) dOpen.dispose();
            if (dForce != null) dForce.dispose();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.flush();

        System.exit(0);
    }

    private void mnuSummaryActionPerformed(java.awt.event.ActionEvent evt) {
        SolidifyVehicle();
        dlgSummaryInfo Summary = new dlgSummaryInfo(this, true, CurVee);
        Summary.setLocationRelativeTo(this);
        Summary.setVisible(true);
    }

    private void mnuCostBVBreakdownActionPerformed(java.awt.event.ActionEvent evt) {
        SolidifyVehicle();
        dlgCostBVBreakdown costbv = new dlgCostBVBreakdown(this, true, CurVee);
        costbv.setLocationRelativeTo(this);
        costbv.setVisible(true);
    }

    private void mnuTextTROActionPerformed(java.awt.event.ActionEvent evt) {
        SetSource = false;
        SolidifyVehicle();
        dlgTextExport Text = new dlgTextExport(this, true, CurVee);
        Text.setLocationRelativeTo(this);
        Text.setVisible(true);
        CurVee.SetCurLoadout((String) cmbOmniVariant.getSelectedItem());
        SetSource = true;
    }

    private void mnuBFBActionPerformed(java.awt.event.ActionEvent evt) {
        String[] call = {"java", "-Xmx256m", "-jar", "bfb.jar"};
        try {
            Runtime.getRuntime().exec(call);
        } catch (Exception ex) {
            Media.Messager("Error while trying to open BFB\n" + ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }

    private void mnuOptionsActionPerformed(java.awt.event.ActionEvent evt) {
        dlgPrefs preferences = new dlgPrefs(this, true);
        preferences.setLocationRelativeTo(this);
        preferences.setVisible(true);
        ResetAmmo();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void mnuViewToolbarActionPerformed(java.awt.event.ActionEvent evt) {
        setViewToolbar(mnuViewToolbar.getState());
    }

    private void mnuClearUserDataActionPerformed(java.awt.event.ActionEvent evt) {
        int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                "This will remove all Solaris 7 user data.\nAre you sure you want to continue?", "Clear User Data?", javax.swing.JOptionPane.YES_NO_OPTION);
        if (choice == 1) {
            return;
        } else {
            Prefs.put("S7Callsign", "");
            Prefs.put("S7Password", "");
            Prefs.put("S7UserID", "");
        }
    }

    private void mnuUnlockActionPerformed(java.awt.event.ActionEvent evt) {
        int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure you want to unlock the chassis?\nAll omni loadouts"
                + " will be deleted\nand the Vehicle will revert to its base loadout.",
                "Unlock Chassis?", javax.swing.JOptionPane.YES_NO_OPTION);
        if (choice == 1) {
            return;
        }

        // make it an omni
        CurVee.UnlockChassis();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        LoadVehicleIntoGUI();
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        Overview.StartNewDocument();
        Capabilities.StartNewDocument();
        History.StartNewDocument();
        Deployment.StartNewDocument();
        Variants.StartNewDocument();
        Notables.StartNewDocument();
        Additional.StartNewDocument();
        txtManufacturer.setText("");
        txtManufacturerLocation.setText("");
        txtEngineManufacturer.setText("");
        txtArmorModel.setText("");
        txtChassisModel.setText("");
        txtJJModel.setText("");
        txtCommSystem.setText("");
        txtTNTSystem.setText("");
    }

    private void mnuCreditsActionPerformed(java.awt.event.ActionEvent evt) {
        dlgCredits Credits = new dlgCredits(this, true);
        Credits.setLocationRelativeTo(this);
        Credits.setVisible(true);
    }

    private void mnuAboutSSWActionPerformed(java.awt.event.ActionEvent evt) {
        dlgAboutBox about = new dlgAboutBox(this, true);
        about.setLocationRelativeTo(this);
        about.setVisible(true);
    }

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        PagePrinter printer = SetupPrinter();
        Scenario s = new Scenario();
        s.getAttackerForce().AddUnit(new Unit(CurVee));
        dlgPreview prv = new dlgPreview("Print Preview", this, printer, s, imageTracker);
        prv.setRSOnly();
        prv.setLocationRelativeTo(this);
        prv.setVisible(true);
    }

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {
        if( CurVee.HasChanged() ) {
            int choice = javax.swing.JOptionPane.showConfirmDialog( this,
                "The current Vehicle has changed.\nDo you want to discard those changes?", "Discard Changes?", javax.swing.JOptionPane.YES_NO_OPTION );
            if( choice == 1 ) { return; }
        }
        dOpen.Requestor = dlgOpen.SSW;
        dOpen.setLocationRelativeTo(null);

        dOpen.setSize( 1024, 600 );
        dOpen.setVisible(true);
    }

    private void formWindowClosed(java.awt.event.WindowEvent evt) {
        CloseProgram();
    }

    private void btnOptionsActionPerformed(java.awt.event.ActionEvent evt) {
        dlgPrefs preferences = new dlgPrefs( this, true );
        preferences.setLocationRelativeTo( this );
        preferences.setVisible( true );
        ResetAmmo();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void btnExportClipboardIconActionPerformed(java.awt.event.ActionEvent evt) {
        // takes the text export and copies it to thesystem clipboard.
        String CurLoadout = "";
        String output = "";

        if( CurVee.IsOmni() ) {
            CurLoadout = CurVee.GetLoadout().GetName();
        }

        // Solidify the mech first.
        SolidifyVehicle();

        if( ! VerifyVehicle( evt ) ) {
            return;
        }

        CVTXTWriter txtw = new CVTXTWriter( CurVee );
        output = txtw.GetTextExport();
        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( output );

        // lastly, if this is an omnimech, reset the display to the last loadout
        if( CurVee.IsOmni() ) {
            cmbOmniVariant.setSelectedItem( CurLoadout );
            cmbOmniVariantActionPerformed( evt );
        }
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( export, this );
    }

    private void btnChatInfoActionPerformed(java.awt.event.ActionEvent evt) {
        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection(CurVee.GetChatInfo());
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(export, this);
    }

    private void btnExportHTMLIconActionPerformed(java.awt.event.ActionEvent evt) {
        SetSource = false;
        // exports the mech to HTML format
        String CurLoadout = "";
        if( CurVee.IsOmni() ) {
            CurLoadout = CurVee.GetLoadout().GetName();
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
        HTMLWriter HTMw = new HTMLWriter( CurVee );
        try {
            filename = savemech.getCanonicalPath();
            HTMw.WriteHTML( saw.Constants.HTMLTemplateName, filename );
        } catch( IOException e ) {
            Media.Messager( this, "There was a problem writing the file:\n" + e.getMessage() );
            return;
        }

        // if there were no problems, let the user know how it went
        Media.Messager( this, "Vehicle saved successfully to HTML:\n" + filename );

        // lastly, if this is an omnimech, reset the display to the last loadout
        if( CurVee.IsOmni() ) {
            cmbOmniVariant.setSelectedItem( CurLoadout );
            cmbOmniVariantActionPerformed( evt );
        }
        setTitle( saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel() );
        SetSource = true;
    }

    private void btnExportTextIconActionPerformed(java.awt.event.ActionEvent evt) {
        SetSource = false;
        btnExportTXTActionPerformed( evt );
        SetSource = true;
    }

    private void btnExportMTFIconActionPerformed(java.awt.event.ActionEvent evt) {
        SetSource = false;
        btnExportMTFActionPerformed( evt );
        SetSource = true;
    }

    private void btnAddQuirkActionPerformed(java.awt.event.ActionEvent evt) {
        ArrayList<Quirk> filtered = new ArrayList<Quirk>();
        for (Quirk item : data.GetQuirks()) {
            if (item.isCombatvehicle()) {
                filtered.add(item);
            }
        }
        dlgQuirks qmanage = new dlgQuirks(this, true, CurVee, filtered, quirks);
        qmanage.setLocationRelativeTo(this); qmanage.setVisible(true);
        CurVee.SetQuirks(quirks);
        RefreshQuirks();
    }

    private void RefreshQuirks() {
        tblQuirks.setModel(new tbQuirks(CurVee.GetQuirks()));
    }

    private void btnExportMTFActionPerformed(java.awt.event.ActionEvent evt) {
        // exports the mech to MTF format for use in Megamek

        String dir = Prefs.get("MTFExportPath", "none");
        if (dir.equals("none")) {
            dir = Prefs.get("LastOpenCVDirectory", "");
        }
        File savemech = GetSaveFile("mtf", dir, false, true);
        if (savemech == null) {
            return;
        }

        String filename = "";
        IO.MTFWriter mtfw = new IO.MTFWriter(CurVee, saw.Constants.AppDescription + " " + saw.Constants.GetVersion());
        try {
            filename = savemech.getCanonicalPath();
            mtfw.WriteMTF(filename);
        } catch (IOException e) {
            Media.Messager(this, "There was a problem writing the file:\n" + e.getMessage());
            return;
        }

        // if there were no problems, let the user know how it went
        Media.Messager(this, "Vehicle saved successfully to MTF:\n" + filename);
        setTitle(saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel());
    }

    private void btnExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {
        // exports the mech to HTML format
        String CurLoadout = "";
        if (CurVee.IsOmni()) {
            CurLoadout = CurVee.GetLoadout().GetName();
        }

        String dir = Prefs.get("HTMLExportPath", "none");
        if (dir.equals("none")) {
            dir = Prefs.get("LastOpenCVDirectory", "");
        }
        File savemech = GetSaveFile("html", dir, false, false);
        if (savemech == null) {
            return;
        }

        String filename = "";
        HTMLWriter HTMw = new HTMLWriter(CurVee);
        try {
            filename = savemech.getCanonicalPath();
            HTMw.WriteHTML(saw.Constants.HTMLTemplateName, filename);
        } catch (IOException e) {
            Media.Messager(this, "There was a problem writing the file:\n" + e.getMessage());
            return;
        }

        // if there were no problems, let the user know how it went
        Media.Messager(this, "Vehicle saved successfully to HTML:\n" + filename);

        // lastly, if this is an Omni, reset the display to the last loadout
        if (CurVee.IsOmni()) {
            //cmbOmniVariant.setSelectedItem( CurLoadout );
            //cmbOmniVariantActionPerformed( evt );
        }
        setTitle(saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel());
    }

    private void btnExportTXTActionPerformed(java.awt.event.ActionEvent evt) {
        // exports the mech to TXT format
        String CurLoadout = "";
        if (CurVee.IsOmni()) {
            CurLoadout = CurVee.GetLoadout().GetName();
        }

        String dir = Prefs.get("TXTExportPath", "none");
        if (dir.equals("none")) {
            dir = Prefs.get("LastOpenCVDirectory", "");
        }
        File savemech = GetSaveFile("txt", dir, false, false);
        if (savemech == null) {
            return;
        }

        String filename = "";
        CVTXTWriter txtw = new CVTXTWriter(CurVee);
        try {
            filename = savemech.getCanonicalPath();
            txtw.WriteTXT(filename);
        } catch (IOException e) {
            Media.Messager(this, "There was a problem writing the file:\n" + e.getMessage());
            return;
        }

        // if there were no problems, let the user know how it went
        Media.Messager(this, "Vehicle saved successfully to TXT:\n" + filename);

        // lastly, if this is an Omni, reset the display to the last loadout
        if (CurVee.IsOmni()) {
            //cmbOmniVariant.setSelectedItem( CurLoadout );
            //cmbOmniVariantActionPerformed( evt );
        }
        setTitle(saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel());
    }

    private void btnClearImageActionPerformed(java.awt.event.ActionEvent evt) {
        // Set the fluff image to default
        lblFluffImage.setIcon(null);
        CurVee.SetSSWImage("");
    }

    private void btnLoadImageActionPerformed(java.awt.event.ActionEvent evt) {
        // Opens a file chooser for the user, then resizes the chosen image to
        // fit in the fluff label and adds it
        JFileChooser fc = new JFileChooser();

        // get the current image in case we cancel
        ImageIcon newFluffImage = (ImageIcon) lblFluffImage.getIcon();

        //Add a custom file filter and disable the default
        //(Accept All) file filter.
        fc.addChoosableFileFilter(new ImageFilter());
        fc.setAcceptAllFileFilterUsed(false);
        if (!Prefs.get("LastImagePath", "").isEmpty()) {
            fc.setCurrentDirectory(new File(Prefs.get("LastImagePath", "")));
        }

        //Add custom icons for file types.
        //ImageFileView IFV = new ImageFileView();
        //fc.setFileView( IFV );

        //Add the preview pane.
        fc.setAccessory(new ImagePreview(fc));

        //Show it.
        int returnVal = fc.showDialog(this, "Attach");

        //Process the results.  If no file is chosen, the default is used.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                Prefs.put("LastImagePath", fc.getSelectedFile().getCanonicalPath().replace(fc.getSelectedFile().getName(), ""));
                Prefs.put("LastImageFile", fc.getSelectedFile().getName());

                newFluffImage = new ImageIcon(fc.getSelectedFile().getPath());

                if (newFluffImage == null) {
                    return;
                }
                // See if we need to scale
                int h = newFluffImage.getIconHeight();
                int w = newFluffImage.getIconWidth();
                if (w > 290 || h > 350) {
                    if (w > h) { // resize based on width
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
        lblFluffImage.setIcon(newFluffImage);
        CurVee.SetSSWImage(fc.getSelectedFile().getPath());
    }

    private void btnAddEquipActionPerformed(java.awt.event.ActionEvent evt) {
        abPlaceable a = null;
        int Index = 0;
        ArrayList v;

        // figure out which list box to pull from
        switch (tbpWeaponChooser.getSelectedIndex()) {
            case BALLISTIC:
                if (lstChooseBallistic.getSelectedIndex() < 0) {
                    break;
                }
                a = (abPlaceable) Equipment[BALLISTIC][lstChooseBallistic.getSelectedIndex()];
                a = data.GetEquipment().GetCopy(a, CurVee);
                break;
            case ENERGY:
                if (lstChooseEnergy.getSelectedIndex() < 0) {
                    break;
                }
                a = (abPlaceable) Equipment[ENERGY][lstChooseEnergy.getSelectedIndex()];
                a = data.GetEquipment().GetCopy(a, CurVee);
                break;
            case MISSILE:
                if (lstChooseMissile.getSelectedIndex() < 0) {
                    break;
                }
                a = (abPlaceable) Equipment[MISSILE][lstChooseMissile.getSelectedIndex()];
                a = data.GetEquipment().GetCopy(a, CurVee);
                if (((RangedWeapon) a).IsFCSCapable()) {
                    if (CurVee.UsingArtemisIV()) {
                        if (((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_ArtemisIV || ((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_ArtemisV) {
                            ((RangedWeapon) a).UseFCS(true, ifMissileGuidance.FCS_ArtemisIV);
                        }
                    }
                    if (CurVee.UsingArtemisV()) {
                        if (((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_ArtemisV) {
                            ((RangedWeapon) a).UseFCS(true, ifMissileGuidance.FCS_ArtemisV);
                        }
                    }
                    if (CurVee.UsingApollo()) {
                        if (((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_Apollo) {
                            ((RangedWeapon) a).UseFCS(true, ifMissileGuidance.FCS_Apollo);
                        }
                    }
                }
                break;
            case PHYSICAL:
                if (lstChoosePhysical.getSelectedIndex() < 0) {
                    break;
                }
                if (!(Equipment[PHYSICAL][lstChoosePhysical.getSelectedIndex()] instanceof abPlaceable)) {
                    break;
                }
                a = (abPlaceable) Equipment[PHYSICAL][lstChoosePhysical.getSelectedIndex()];
                a = data.GetEquipment().GetCopy(a, CurVee);
                break;
            case ARTILLERY:
                if (lstChooseArtillery.getSelectedIndex() < 0) {
                    break;
                }
                if (!(Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()] instanceof abPlaceable)) {
                    break;
                }
                a = (abPlaceable) Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()];
                a = data.GetEquipment().GetCopy(a, CurVee);
                break;
            case EQUIPMENT:
                if (lstChooseEquipment.getSelectedIndex() < 0) {
                    break;
                }
                if (!(Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()] instanceof abPlaceable)) {
                    break;
                }
                a = (abPlaceable) Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()];
                a = data.GetEquipment().GetCopy(a, CurVee);
                break;
            case AMMUNITION:
                if (lstChooseAmmunition.getSelectedIndex() < 0) {
                    break;
                }
                Index = lstChooseAmmunition.getSelectedIndex();
                if (!(Equipment[AMMUNITION][Index] instanceof abPlaceable)) {
                    break;
                }
                a = (abPlaceable) Equipment[AMMUNITION][Index];
                a = data.GetEquipment().GetCopy(a, CurVee);
                break;
        }

        // check exclusions if needed
        if (a != null) {
            try {
                CurVee.GetLoadout().CheckExclusions(a);
                if (a instanceof ifEquipment) {
                    ((ifEquipment) a).Validate(CurVee);
                }
            } catch (Exception e) {
                Media.Messager(e.getMessage());
                a = null;
            }
        }

        // now we can add it to the Vehicle
        if (a != null) {
            boolean result = true;
            if (a instanceof Equipment) {
                if (((Equipment) a).IsVariableSize()) {
                    dlgVariableSize SetTons = new dlgVariableSize(this, true, (Equipment) a, CurVee);
                    SetTons.setLocationRelativeTo(this);
                    SetTons.setVisible(true);
                    result = SetTons.GetResult();
                }
            }
            if (result) {
                try {
                    a.Place(CurVee.GetLoadout(), LocationIndex.FindIndex(CurVee, cmbLocation.getSelectedValue().toString()));
                    for (int i = 0; i < cmbNumEquips.getSelectedIndex(); i++) {
                        a = data.GetEquipment().GetCopy(a, CurVee);
                        a.Place(CurVee.GetLoadout(), LocationIndex.FindIndex(CurVee, cmbLocation.getSelectedValue().toString()));
                    }
                } catch (Exception e) {
                    //something happened
                    Media.Messager(e.getMessage());
                }

                // unallocate the TC if needed (if the size changes)
                if (a instanceof ifWeapon) {
                    if (((ifWeapon) a).IsTCCapable() && CurVee.UsingTC()) {
                        CurVee.UnallocateTC();
                    }
                }

                // see if we need ammunition and add it if applicable
                ResetAmmo();

                if (a instanceof Ammunition) {
                    // added for support if the user selected ammo.  The ResetAmmo()
                    // method clears the selected index.
                    lstChooseAmmunition.setSelectedIndex(Index);
                }

                // refresh the selected equipment listbox
                if (CurVee.GetLoadout().GetNonCore().toArray().length <= 0) {
                    Equipment[SELECTED] = new Object[]{" "};
                } else {
                    Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
                }
                lstSelectedEquipment.setListData(Equipment[SELECTED]);
            }
            spnHeatSinks.setModel(new SpinnerNumberModel(CurVee.GetHeatSinks().GetNumHS(), ((CVLoadout)CurVee.GetLoadout()).GetTotalHeat(), 99, 1));

            // now refresh the information panes
            RefreshSummary();
            RefreshInfoPane();
            cmbNumEquips.setSelectedIndex(0);
        }
    }

    private void btnClearEquipActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.GetLoadout().SafeClearLoadout();

        // refresh the selected equipment listbox
        if (CurVee.GetLoadout().GetNonCore().toArray().length <= 0) {
            Equipment[SELECTED] = new Object[]{" "};
        } else {
            Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
        }
        lstSelectedEquipment.setListData(Equipment[SELECTED]);

        // Check the targeting computer if needed
        if (CurVee.UsingTC()) {
            CurVee.CheckTC();
        }

        // refresh the ammunition display
        ResetAmmo();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void btnRemoveEquipActionPerformed(java.awt.event.ActionEvent evt) {
        if (lstSelectedEquipment.getSelectedIndex() < 0) {
            return;
        }
        int[] selected = lstSelectedEquipment.getSelectedIndices();
        if (selected.length == 0) {
            return;
        }
        // we work in reverse so we can properly manage the items in the queue
        for (int i = selected.length - 1; i >= 0; i--) {
            // abPlaceable p = (abPlaceable) locArmor.GetLoadout().GetNonCore().get( lstSelectedEquipment.getSelectedIndex() );
            abPlaceable p = (abPlaceable) CurVee.GetLoadout().GetNonCore().get(selected[i]);
            if (p.LocationLocked()) {
                Media.Messager(this, "You may not remove a locked item from the loadout.");
                return;
            } else {
                CurVee.GetLoadout().Remove(p);
            }
        }
        // refresh the selected equipment listbox
        if (CurVee.GetLoadout().GetNonCore().toArray().length <= 0) {
            Equipment[SELECTED] = new Object[]{" "};
        } else {
            Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
        }
        lstSelectedEquipment.setListData(Equipment[SELECTED]);

        // Check the targeting computer if needed
        if (CurVee.UsingTC()) {
            CurVee.UnallocateTC();
        }

        // refresh the ammunition display
        ResetAmmo();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void lstSelectedEquipmentKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            btnRemoveEquipActionPerformed(new ActionEvent(evt.getSource(), evt.getID(), null));
        }
    }

    private void lstSelectedEquipmentValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (lstSelectedEquipment.getSelectedIndex() < 0) {
            return;
        }
        abPlaceable p = (abPlaceable) Equipment[SELECTED][lstSelectedEquipment.getSelectedIndex()];
        ShowInfoOn(p);
    }

    private void chkCASEActionPerformed(java.awt.event.ActionEvent evt) {
        if (!chkCASE.isSelected()) {
            CurVee.GetLoadout().RemoveCase();
            SetWeaponChoosers();
            RefreshSummary();
            RefreshInfoPane();
            return;
        }

        try {
            switch (CurVee.GetTechBase()) {
                case AvailableCode.TECH_INNER_SPHERE:
                    CurVee.GetLoadout().AddCase(false);
                    break;
                case AvailableCode.TECH_CLAN:
                    CurVee.GetLoadout().AddCase(true);
                    break;
                case AvailableCode.TECH_BOTH:
                    dlgTechBaseChooser tech = new dlgTechBaseChooser(this, true);
                    tech.setLocationRelativeTo(this);
                    tech.setVisible(true);
                    CurVee.GetLoadout().AddCase(tech.IsClan());
                    break;
            }
        } catch (Exception e) {
            Media.Messager(e.getMessage());
        }
        SetWeaponChoosers();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkFCSApolloActionPerformed(java.awt.event.ActionEvent evt) {
        if (CurVee.UsingApollo() == chkFCSApollo.isSelected()) {
            return;
        }
        if (chkFCSApollo.isSelected()) {
            try {
                CurVee.SetFCSApollo(true);
            } catch (Exception e) {
                Media.Messager(this, e.getMessage());
                chkFCSApollo.setSelected(false);
            }
        } else {
            try {
                CurVee.SetFCSApollo(false);
            } catch (Exception e) {
                Media.Messager(this, e.getMessage());
                chkFCSApollo.setSelected(true);
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkFCSAVActionPerformed(java.awt.event.ActionEvent evt) {
        if (CurVee.UsingArtemisV() == chkFCSAV.isSelected()) {
            return;
        }
        if (chkFCSAV.isSelected()) {
            try {
                CurVee.SetFCSArtemisV(true);
            } catch (Exception e) {
                Media.Messager(this, e.getMessage());
                chkFCSAV.setSelected(false);
            }
        } else {
            try {
                CurVee.SetFCSArtemisV(false);
            } catch (Exception e) {
                Media.Messager(this, e.getMessage());
                chkFCSAV.setSelected(true);
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkFCSAIVActionPerformed(java.awt.event.ActionEvent evt) {
        if (CurVee.UsingArtemisIV() == chkFCSAIV.isSelected()) {
            return;
        }
        if (chkFCSAIV.isSelected()) {
            try {
                CurVee.SetFCSArtemisIV(true);
            } catch (Exception e) {
                Media.Messager(this, e.getMessage());
                chkFCSAIV.setSelected(false);
            }
        } else {
            try {
                CurVee.SetFCSArtemisIV(false);
            } catch (Exception e) {
                Media.Messager(this, e.getMessage());
                chkFCSAIV.setSelected(true);
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
   }

    private void chkUseTCActionPerformed(java.awt.event.ActionEvent evt) {
        if (CurVee.UsingTC() == chkUseTC.isSelected()) {
            return;
        }
        if (chkUseTC.isSelected()) {
            try {
                CurVee.GetLoadout().CheckExclusions(CurVee.GetTC());
                if (CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser(this, true);
                    tech.setLocationRelativeTo(this);
                    tech.setVisible(true);
                    CurVee.UseTC(true, tech.IsClan());
                } else if (CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN) {
                    CurVee.UseTC(true, true);
                } else {
                    CurVee.UseTC(true, false);
                }
            } catch (Exception e) {
                Media.Messager(this, e.getMessage());
                CurVee.UseTC(false, false);
            }
        } else {
            CurVee.UseTC(false, false);
        }
        SetWeaponChoosers();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void lstChooseAmmunitionValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (lstChooseAmmunition.getSelectedIndex() < 0) {
            return;
        }
        abPlaceable p = (abPlaceable) Equipment[AMMUNITION][lstChooseAmmunition.getSelectedIndex()];
        ShowInfoOn(p);
    }

    private void lstChooseArtilleryValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (lstChooseArtillery.getSelectedIndex() < 0) {
            return;
        }
        abPlaceable p = (abPlaceable) Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()];
        ShowInfoOn(p);
    }

    private void lstChooseEquipmentValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (lstChooseEquipment.getSelectedIndex() < 0) {
            return;
        }
        abPlaceable p = (abPlaceable) Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()];
        ShowInfoOn(p);
    }

    private void lstChoosePhysicalValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (lstChoosePhysical.getSelectedIndex() < 0) {
            return;
        }
        abPlaceable p = (abPlaceable) Equipment[PHYSICAL][lstChoosePhysical.getSelectedIndex()];
        ShowInfoOn(p);
    }

    private void lstChooseMissileValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (lstChooseMissile.getSelectedIndex() < 0) {
            return;
        }
        abPlaceable p = (abPlaceable) Equipment[MISSILE][lstChooseMissile.getSelectedIndex()];
        ShowInfoOn(p);
    }

    private void lstChooseEnergyValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (lstChooseEnergy.getSelectedIndex() < 0) {
            return;
        }
        abPlaceable p = (abPlaceable) Equipment[ENERGY][lstChooseEnergy.getSelectedIndex()];
        ShowInfoOn(p);
    }

    private void lstChooseBallisticValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (lstChooseBallistic.getSelectedIndex() < 0) {
            return;
        }
        abPlaceable p = (abPlaceable) Equipment[BALLISTIC][lstChooseBallistic.getSelectedIndex()];
        ShowInfoOn(p);
    }

    private void btnMaximizeActionPerformed(java.awt.event.ActionEvent evt) {
        // this simply maximizes the mech's armor
        CVArmor a = CurVee.GetArmor();
        a.Maximize();

        // if we fix the spinner models, they should refresh the screen
        FixArmorSpinners();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void btnUseRemainingActionPerformed(java.awt.event.ActionEvent evt) {
        // see if we have a good number
        double freetons = CurVee.GetTonnage() - CurVee.GetCurrentTons() + CurVee.GetArmor().GetTonnage();

        if (freetons > CurVee.GetArmor().GetMaxTonnage()) {
            freetons = CurVee.GetArmor().GetMaxTonnage();
        }

        ArmorTons.SetArmorTonnage(freetons);
        try {
            CurVee.Visit(ArmorTons);
        } catch (Exception e) {
            // this should never throw an exception, but log it anyway
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        // if we fix the spinner models, they should refresh the screen
        FixArmorSpinners();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void btnSetArmorTonsActionPerformed(java.awt.event.ActionEvent evt) {
        // we'll need a new dialogue to get the tonnage
        dlgArmorTonnage ArmorDialogue = new dlgArmorTonnage(this, true, CurVee);
        ArmorDialogue.setLocationRelativeTo(this);
        ArmorDialogue.setVisible(true);

        // see if we have a good number
        if (ArmorDialogue.NewTonnage()) {
            double result = ArmorDialogue.GetResult();
            ArmorTons.SetArmorTonnage(result);
            try {
                CurVee.Visit(ArmorTons);
            } catch (Exception e) {
                // this should never throw an exception, but log it anyway
                System.err.println(e.getMessage());
                e.printStackTrace();
            }

            ArmorDialogue.dispose();
        } else {
            ArmorDialogue.dispose();
        }

        // if we fix the spinner models, they should refresh the screen
        FixArmorSpinners();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void cmbArmorTypeActionPerformed(java.awt.event.ActionEvent evt) {
        if (Load) {
            return;
        }
        if (BuildLookupName(CurVee.GetArmor().GetCurrentState()).equals((String) cmbArmorType.getSelectedItem())) {
            return;
        }
        RecalcArmor();
        // we check for hardened armor, you can only have so many IJJs
        FixJJSpinnerModel();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnRotorArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if (Load) {
            return;
        }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRotorArmor.getModel();
        javax.swing.JComponent editor = spnRotorArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRotorArmor.commitEdit();
        } catch (java.text.ParseException pe) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRotorArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int CurVee = a.GetLocationArmor(LocationIndex.CV_LOC_ROTOR);
        int curframe = n.getNumber().intValue();
        if (curframe > CurVee) {
            while (curframe > CurVee) {
                a.IncrementArmor(LocationIndex.CV_LOC_ROTOR);
                curframe--;
            }
        } else {
            while (CurVee > curframe) {
                a.DecrementArmor(LocationIndex.CV_LOC_ROTOR);
                CurVee = a.GetLocationArmor(LocationIndex.CV_LOC_ROTOR);
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnRotorArmor.getModel();
        n.setValue((Object) a.GetLocationArmor(LocationIndex.CV_LOC_ROTOR));

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnRearTurretArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if (Load) {
            return;
        }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRearTurretArmor.getModel();
        javax.swing.JComponent editor = spnRearTurretArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRearTurretArmor.commitEdit();
        } catch (java.text.ParseException pe) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRearTurretArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_TURRET2);
        int curframe = n.getNumber().intValue();
        if (curframe > locArmor) {
            while (curframe > locArmor) {
                a.IncrementArmor(LocationIndex.CV_LOC_TURRET2);
                curframe--;
            }
        } else {
            while (locArmor > curframe) {
                a.DecrementArmor(LocationIndex.CV_LOC_TURRET2);
                locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_TURRET2);
            }
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnTurretArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if (Load) {
            return;
        }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnTurretArmor.getModel();
        javax.swing.JComponent editor = spnTurretArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnTurretArmor.commitEdit();
        } catch (java.text.ParseException pe) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnTurretArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_TURRET1);
        int curframe = n.getNumber().intValue();
        if (curframe > locArmor) {
            while (curframe > locArmor) {
                a.IncrementArmor(LocationIndex.CV_LOC_TURRET1);
                curframe--;
            }
        } else {
            while (locArmor > curframe) {
                a.DecrementArmor(LocationIndex.CV_LOC_TURRET1);
                locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_TURRET1);
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnTurretArmor.getModel();
        n.setValue((Object) a.GetLocationArmor(LocationIndex.CV_LOC_TURRET1));

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnRearArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if (Load) {
            return;
        }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRearArmor.getModel();
        javax.swing.JComponent editor = spnRearArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRearArmor.commitEdit();
        } catch (java.text.ParseException pe) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRearArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_REAR);
        int curframe = n.getNumber().intValue();
        if (curframe > locArmor) {
            while (curframe > locArmor) {
                a.IncrementArmor(LocationIndex.CV_LOC_REAR);
                curframe--;
            }
        } else {
            while (locArmor > curframe) {
                a.DecrementArmor(LocationIndex.CV_LOC_REAR);
                locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_REAR);
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnRearArmor.getModel();
        n.setValue((Object) a.GetLocationArmor(LocationIndex.CV_LOC_REAR));

        if (chkBalanceFRArmor.isSelected()) {
            n = (SpinnerNumberModel) spnRearArmor.getModel();
            a.SetArmor(LocationIndex.CV_LOC_FRONT, n.getNumber().intValue());
            n = (SpinnerNumberModel) spnFrontArmor.getModel();
            n.setValue((Object) a.GetLocationArmor(LocationIndex.CV_LOC_REAR));
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnLeftArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if (Load) {
            return;
        }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnLeftArmor.getModel();
        javax.swing.JComponent editor = spnLeftArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnLeftArmor.commitEdit();
        } catch (java.text.ParseException pe) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnLeftArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_LEFT);
        int curframe = n.getNumber().intValue();
        if (curframe > locArmor) {
            while (curframe > locArmor) {
                a.IncrementArmor(LocationIndex.CV_LOC_LEFT);
                curframe--;
            }
        } else {
            while (locArmor > curframe) {
                a.DecrementArmor(LocationIndex.CV_LOC_LEFT);
                locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_LEFT);
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnLeftArmor.getModel();
        n.setValue((Object) a.GetLocationArmor(LocationIndex.CV_LOC_LEFT));

        if (chkBalanceLRArmor.isSelected()) {
            n = (SpinnerNumberModel) spnLeftArmor.getModel();
            a.SetArmor(LocationIndex.CV_LOC_RIGHT, n.getNumber().intValue());
            n = (SpinnerNumberModel) spnRightArmor.getModel();
            n.setValue((Object) a.GetLocationArmor(LocationIndex.CV_LOC_LEFT));
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnFrontArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if (Load) {
            return;
        }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnFrontArmor.getModel();
        javax.swing.JComponent editor = spnFrontArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnFrontArmor.commitEdit();
        } catch (java.text.ParseException pe) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnFrontArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_FRONT);
        int curframe = n.getNumber().intValue();
        if (curframe > locArmor) {
            while (curframe > locArmor) {
                a.IncrementArmor(LocationIndex.CV_LOC_FRONT);
                curframe--;
            }
        } else {
            while (locArmor > curframe) {
                a.DecrementArmor(LocationIndex.CV_LOC_FRONT);
                locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_FRONT);
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnFrontArmor.getModel();
        n.setValue((Object) a.GetLocationArmor(LocationIndex.CV_LOC_FRONT));

        if (chkBalanceFRArmor.isSelected()) {
            n = (SpinnerNumberModel) spnFrontArmor.getModel();
            a.SetArmor(LocationIndex.CV_LOC_REAR, n.getNumber().intValue());
            n = (SpinnerNumberModel) spnRearArmor.getModel();
            n.setValue((Object) a.GetLocationArmor(LocationIndex.CV_LOC_FRONT));
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnRightArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if (Load) {
            return;
        }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRightArmor.getModel();
        javax.swing.JComponent editor = spnRightArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRightArmor.commitEdit();
        } catch (java.text.ParseException pe) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRightArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_RIGHT);
        int curframe = n.getNumber().intValue();
        if (curframe > locArmor) {
            while (curframe > locArmor) {
                a.IncrementArmor(LocationIndex.CV_LOC_RIGHT);
                curframe--;
            }
        } else {
            while (locArmor > curframe) {
                a.DecrementArmor(LocationIndex.CV_LOC_RIGHT);
                locArmor = a.GetLocationArmor(LocationIndex.CV_LOC_RIGHT);
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnRightArmor.getModel();
        n.setValue((Object) a.GetLocationArmor(LocationIndex.CV_LOC_RIGHT));

        if (chkBalanceLRArmor.isSelected()) {
            n = (SpinnerNumberModel) spnRightArmor.getModel();
            a.SetArmor(LocationIndex.CV_LOC_LEFT, n.getNumber().intValue());
            n = (SpinnerNumberModel) spnLeftArmor.getModel();
            n.setValue((Object) a.GetLocationArmor(LocationIndex.CV_LOC_RIGHT));
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void btnRenameVariantActionPerformed(java.awt.event.ActionEvent evt) {
        SaveOmniFluffInfo();
        String VariantName = "";

        // get the variant name
        dlgOmniBase input = new dlgOmniBase(this, true);
        input.setTitle("Name this variant");
        input.setLocationRelativeTo(this);
        input.setVisible(true);
        if (input.WasCanceled()) {
            input.dispose();
            return;
        } else {
            VariantName = input.GetInput();
            input.dispose();
        }

        if (CurVee.GetBaseLoadout().GetName().equals(VariantName)) {
            Media.Messager(this, "\"" + VariantName + "\" is reserved for the base loadout and cannot be used\nto name this loadout.  Please choose another name.");
            return;
        }

        // see if another loadout has the same name
        ArrayList Loadouts = CurVee.GetLoadouts();
        for (int i = 0; i < Loadouts.size(); i++) {
            if (((ifCVLoadout) Loadouts.get(i)).GetName().equals(VariantName)) {
                Media.Messager(this, "Could not rename the loadout because\nthe name given matches an existing loadout.");
                return;
            }
        }

        CurVee.GetLoadout().SetName(VariantName);
        RefreshOmniVariants();
    }

    private void btnDeleteVariantActionPerformed(java.awt.event.ActionEvent evt) {
        // see if the user actually wants to delete the variant
        int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this variant?", "Delete Variant?", javax.swing.JOptionPane.YES_NO_OPTION);
        if (choice == 1) {
            return;
        } else {
            if (CurVee.GetLoadout().GetName().equals(common.Constants.BASELOADOUT_NAME)) {
                Media.Messager(this, "You cannot remove the base chassis.");
                return;
            }
        }

        // delete the variant
        CurVee.RemoveLoadout(CurVee.GetLoadout().GetName());

        // refresh all the displays
        LoadOmniFluffInfo();
        RefreshOmniVariants();
        SetWeaponChoosers();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        RefreshOmniChoices();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void btnAddVariantActionPerformed(java.awt.event.ActionEvent evt) {
        SaveOmniFluffInfo();
        String VariantName = "";

        // get the variant name
        dlgOmniBase input = new dlgOmniBase(this, true);
        input.setTitle("Name this variant");
        input.setLocationRelativeTo(this);
        input.setVisible(true);
        if (input.WasCanceled()) {
            input.dispose();
            return;
        } else {
            VariantName = input.GetInput();
            input.dispose();
        }

        // now set the new loadout as the current
        try {
            CurVee.AddLoadout(VariantName);
        } catch (Exception e) {
            // found an error when adding the loadout
            Media.Messager(this, e.getMessage());
            return;
        }

        // fix the GUI
        LoadOmniFluffInfo();
        SetWeaponChoosers();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        RefreshOmniVariants();
        RefreshOmniChoices();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void btnLockChassisActionPerformed(java.awt.event.ActionEvent evt) {
        // currently testing right now.
        SaveOmniFluffInfo();
        String VariantName = "";

        // 2020-11-13 Omnis can't have Hardened Armor, but we wrote this generic
        // in case later other armor types come along
        if (!CurVee.GetArmor().AllowOmni()){
            Media.Messager( this, "Omnivees are not allowed to have " + CurVee.GetArmor().ActualName());
            return;
        }

        int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure you want to lock the chassis?\nAll items in the base "
                + "loadout will be locked in location\nand most chassis specifications "
                + "will be locked.", "Lock Chassis?", javax.swing.JOptionPane.YES_NO_OPTION);
        if (choice == 1) {
            return;
        } else {
            // ask for a name for the first variant
            dlgOmniBase input = new dlgOmniBase(this, true);
            input.setTitle("Name the first variant");
            input.setLocationRelativeTo(this);
            input.setVisible(true);
            if (input.WasCanceled()) {
                input.dispose();
                return;
            } else {
                VariantName = input.GetInput();
                input.dispose();
            }
        }

        // ensure we're not using the base loadout's name.
        if (common.Constants.BASELOADOUT_NAME.equals(VariantName)) {
            Media.Messager(this, "\"" + VariantName + "\" is reserved for the base loadout and cannot be used\nfor a new loadout.  Please choose another name.");
            return;
        }

        // make it an omni
        CurVee.SetOmni(VariantName);
        chkOmniVee.setEnabled(false);
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        LockGUIForOmni();
        RefreshOmniVariants();
        RefreshOmniChoices();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkFractionalActionPerformed(java.awt.event.ActionEvent evt) {
        if (chkFractional.isSelected() == CurVee.UsingFractionalAccounting()) {
            return;
        }
        CurVee.SetFractionalAccounting(chkFractional.isSelected());
        if (!CurVee.UsingFractionalAccounting()) {
            ArrayList v = CurVee.GetLoadout().GetNonCore();
            for (int i = 0; i < v.size(); i++) {
                if (v.get(i) instanceof Ammunition) {
                    ((Ammunition) v.get(i)).ResetLotSize();
                }
            }
        }

        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkJetBoosterActionPerformed(java.awt.event.ActionEvent evt) {
        if( CurVee.GetLoadout().HasVTOLBooster() == chkJetBooster.isSelected() ) {
            return;
        }
        try {
            CurVee.GetLoadout().SetVTOLBooster( chkJetBooster.isSelected());
        } catch( Exception e ) {
            Media.Messager( this, e.getMessage() );
            try {
                CurVee.GetLoadout().SetVTOLBooster( false );
            } catch( Exception x ) {
                // how the hell did we get an error removing it?
                Media.Messager( this, x.getMessage() );
            }
            chkJetBooster.setSelected( false );
        }
        RefreshSelectedEquipment();
        RefreshSummary();
        RefreshInfoPane();
    }
    private void chkArmoredMotiveActionPerformed(ActionEvent evt) {
        if (CurVee.GetLoadout().HasArmoredMotiveSystem() == chkArmoredMotive.isSelected()) {
            return;
        }
        try {
            switch (CurVee.GetTechBase()) {
                case AvailableCode.TECH_INNER_SPHERE:
                    CurVee.GetLoadout().SetArmoredMotiveSystem(chkArmoredMotive.isSelected(), false);
                    break;
                case AvailableCode.TECH_CLAN:
                    CurVee.GetLoadout().SetArmoredMotiveSystem(chkArmoredMotive.isSelected(), true);
                    break;
                case AvailableCode.TECH_BOTH:
                    dlgTechBaseChooser tech = new dlgTechBaseChooser(this, true);
                    tech.setLocationRelativeTo(this);
                    tech.setVisible(true);
                    CurVee.GetLoadout().SetArmoredMotiveSystem(chkArmoredMotive.isSelected(), tech.IsClan());
                    break;
            }
        } catch( Exception e ) {
            Media.Messager( this, e.getMessage() );
            chkArmoredMotive.setSelected( false );
        }
        RefreshSelectedEquipment();
        RefreshSummary();
        RefreshInfoPane();
    }
    private void chkSuperchargerActionPerformed(java.awt.event.ActionEvent evt) {
        if( CurVee.GetLoadout().HasSupercharger() == chkSupercharger.isSelected() ) {
            return;
        }
        try {
            CurVee.GetLoadout().SetSupercharger( chkSupercharger.isSelected());
        } catch( Exception e ) {
            Media.Messager( this, e.getMessage() );
            try {
                CurVee.GetLoadout().SetSupercharger( false );
            } catch( Exception x ) {
                // how the hell did we get an error removing it?
                Media.Messager( this, x.getMessage() );
            }
            chkSupercharger.setSelected( false );
        }
        RefreshSelectedEquipment();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkEnviroSealingActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetEnvironmentalSealing(chkEnviroSealing.isSelected());
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkDuneBuggyActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetDuneBuggy(chkDuneBuggy.isSelected());
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkFullAmphActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void chkLimitedAmphActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetLimitedAmphibious(chkLimitedAmph.isSelected());
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkFlotationHullActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetFlotationHull(chkFlotationHull.isSelected());
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnCruiseMPInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        spnCruiseMPStateChanged(null);
    }

    private void spnCruiseMPStateChanged(javax.swing.event.ChangeEvent evt) {
        if (Load) {
            return;
        }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnCruiseMP.getModel();
        javax.swing.JComponent editor = spnCruiseMP.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();

        n.setMinimum(1);
        // get the value from the text box, if it's valid.
        try {
            spnCruiseMP.commitEdit();
        } catch (java.text.ParseException pe) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnCruiseMP.getValue());
            }
            return;
        }
        try {
            // the commitedit worked, so set the engine rating and report the running mp
            int walkMP = Math.min(n.getNumber().intValue(), CurVee.getMaxCruiseMP());
            CurVee.setCruiseMP(walkMP);
        } catch (Exception e) {
            Media.Messager(e.getMessage());
            spnCruiseMP.setValue(spnCruiseMP.getPreviousValue());
        }
        lblFlankMP.setText("" + CurVee.getFlankMP());

        // when the walking mp changes, we also have to change the jump mp
        // spinner model and recalculate the heat sinks
        FixMPSpinner();
        FixJJSpinnerModel();
        //CurVee.GetHeatSinks().ReCalculate();
        //CurVee.GetLoadout().UnallocateFuelTanks();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnTurretTonnageStateChanged(javax.swing.event.ChangeEvent evt) {
        double Tons = 0;
        try {
            Tons = Double.parseDouble(spnTurretTonnage.getValue().toString());
            CurVee.GetLoadout().GetTurret().SetTonnage(Tons);
        } catch (Exception e) {
            Media.Messager(e.getMessage());
            return;
        }

        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnRearTurretTonnageStateChanged(javax.swing.event.ChangeEvent evt) {
        double Tons = 0;
        try {
            Tons = Double.parseDouble(spnRearTurretTonnage.getValue().toString());
            CurVee.GetLoadout().GetRearTurret().SetTonnage(Tons);
        } catch (Exception e) {
            Media.Messager(e.getMessage());
            return;
        }

        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnHeatSinksInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
    }

    private void spnHeatSinksFocusGained(java.awt.event.FocusEvent evt) {
    }

    private void spnHeatSinksStateChanged(javax.swing.event.ChangeEvent evt) {
        if (Load) {
            return;
        }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHeatSinks.getModel();
        int NumHS = CurVee.GetHeatSinks().GetNumHS();
        javax.swing.JComponent editor = spnHeatSinks.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnHeatSinks.commitEdit();
        } catch (java.text.ParseException pe) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnHeatSinks.getValue());
            }
            return;
        }

        if (n.getNumber().intValue() > NumHS) {
            // The number of sinks went up
            for (int i = NumHS; i < n.getNumber().intValue(); i++) {
                CurVee.GetHeatSinks().IncrementNumHS();
            }
        } else {
            // the number went down
            for (int i = NumHS; i > n.getNumber().intValue(); i--) {
                CurVee.GetHeatSinks().DecrementNumHS();
            }
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void cmbTurretActionPerformed(java.awt.event.ActionEvent evt) {
        if (Load) {
            return;
        }

        String Turret = cmbTurret.getSelectedItem().toString();
        if (Turret.equals("Single Turret") || Turret.equals("Chin Turret")) {
            CurVee.setHasTurret1(true);
            spnTurretTonnage.setEnabled(chkOmniVee.isSelected() && !isLocked);
            spnRearTurretTonnage.setEnabled(false);
            spnRearTurretTonnage.setValue(0);
        } else if (Turret.equals("Dual Turret")) {
            CurVee.setHasTurret1(true);
            CurVee.setHasTurret2(true);
            spnTurretTonnage.setEnabled(chkOmniVee.isSelected() && !isLocked);
            spnRearTurretTonnage.setEnabled(chkOmniVee.isSelected() && !isLocked);
        } else {
            CurVee.setHasTurret1(false);
            CurVee.setHasTurret2(false);
            spnTurretTonnage.setEnabled(false);
            spnTurretTonnage.setValue(0);
            spnRearTurretTonnage.setEnabled(false);
            spnRearTurretTonnage.setValue(0);
        }

        BuildLocationSelector();
        RecalcArmorLocations();
        RefreshSelectedEquipment();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void RefreshSelectedEquipment() {
        Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
        lstSelectedEquipment.setListData( Equipment[SELECTED] );
        lstSelectedEquipment.repaint();
    }

    private void cmbEngineTypeActionPerformed(java.awt.event.ActionEvent evt) {
        if (Load) {
            return;
        }
        if (BuildLookupName(CurVee.GetEngine().GetCurrentState()).equals((String) cmbEngineType.getSelectedItem())) {
            // only nuclear-powered mechs may use jump jets
            if (CurVee.GetEngine().IsNuclear()) {
                /*
                 * if( cmbJumpJetType.getSelectedItem() == null ) {
                 * EnableJumpJets( false ); } else { EnableJumpJets( true ); }
                 */
            } else {
                EnableJumpJets(false);
            }
            return;
        }
        RecalcEngine();
        FixMPSpinner();

        //When the engine changes we need to re-check the Heat Sinks
        CurVee.ResetHeatSinks();

        // only nuclear-powered mechs may use jump jets
        if (CurVee.GetEngine().IsNuclear()) {
            /*
             * if( cmbJumpJetType.getSelectedItem() == null ) { EnableJumpJets(
             * false ); } else { EnableJumpJets( true ); }
             */
        } else {
            EnableJumpJets(false);
        }

        // refresh the selected equipment listbox
        if (CurVee.GetLoadout().GetNonCore().toArray().length <= 0) {
            Equipment[SELECTED] = new Object[]{" "};
        } else {
            Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
        }
        lstSelectedEquipment.setListData(Equipment[SELECTED]);

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnTonnageInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        spnTonnageStateChanged(null);
    }

    private void spnTonnageFocusGained(java.awt.event.FocusEvent evt) {
    }

    private void spnTonnageStateChanged(javax.swing.event.ChangeEvent evt) {
        if (Load) {
            return;
        }

        // We have to decode the selected index to set values.  A bit safer, I
        // think, because we can directly set the values ourselves.
        int Tons = 0;
        try {
            Tons = Integer.parseInt(spnTonnage.getValue().toString());
        } catch (Exception e) {
            Media.Messager(e.getMessage());
            return;
        }

        if (Tons == 0) {
            Tons = 1;
        }

        if (Tons >= 0 && Tons <= 39) {
            lblVeeClass.setText("Light Vee");
        }
        if (Tons >= 40 && Tons <= 59) {
            lblVeeClass.setText("Medium Vee");
        }
        if (Tons >= 60 && Tons <= 79) {
            lblVeeClass.setText("Heavy Vee");
        }
        if (Tons >= 80) {
            lblVeeClass.setText("Assault Vee");
        }

        if (CurVee.GetTonnage() == Tons) {
            return;
        } else {
            CurVee.setTonnage(Tons);
        }

        // check the tonnage
        if (CurVee.GetTonnage() < 1) {
            spnTonnage.setValue(1);
        }

        if (CurVee.GetTonnage() > CurVee.GetMaxTonnage()) {
            spnTonnage.setValue(CurVee.GetMaxTonnage());
        }

        CurVee.GetArmor().SetMaxArmor(CurVee.GetArmorableLocationCount());

        // fix the walking and jumping MP spinners
        FixMPSpinner();
        FixJJSpinnerModel();

        // recalculate the heat sinks and armor
        CurVee.GetHeatSinks().ReCalculate();
        CurVee.GetArmor().Recalculate();

        // fix the armor spinners
        FixArmorSpinners();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkTrailerActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetTrailer(chkTrailer.isSelected());
        BuildEngineSelector();
        if ( chkTrailer.isSelected() ) {
            try {
                CurVee.setCruiseMP(0);
            } catch (Exception ex) {
                Media.Messager(ex.getMessage());
            }
            cmbEngineType.setSelectedItem("No Engine");
            cmbEngineTypeActionPerformed(evt);
            ((SpinnerNumberModel)spnCruiseMP.getModel()).setMinimum(0);
            spnCruiseMP.setValue(0);
            lblFlankMP.setText( "" + CurVee.getFlankMP() );
        } else {
            try {
                CurVee.setCruiseMP(1);
            } catch (Exception ex) {
                Media.Messager(ex.getMessage());
            }
            if ( cmbEngineType.getSelectedItem().toString() == "No Engine" ) cmbEngineType.setSelectedIndex(0);
            cmbEngineTypeActionPerformed(evt);
            spnCruiseMP.setValue(1);
            ((SpinnerNumberModel)spnCruiseMP.getModel()).setMinimum(1);
            lblFlankMP.setText( "" + CurVee.getFlankMP() );
        }
        SetWeaponChoosers();
        RefreshEquipment();
        RefreshInfoPane();
    }

    private void chkOmniVeeActionPerformed(java.awt.event.ActionEvent evt) {
        btnLockChassis.setEnabled(chkOmniVee.isSelected());
        cmbTurretActionPerformed(evt);
    }

    private void cmbMotiveTypeActionPerformed(java.awt.event.ActionEvent evt) {
        if ( Load ) return;
        boolean wasVtol = CurVee.IsVTOL();

        switch (cmbMotiveType.getSelectedIndex()) {
            case 0:      //Hovercraft
                CurVee.SetHover();
                break;
            case 1:     //Naval (Displacement)
                CurVee.SetDisplacement();
                break;
            case 2:     //Naval (Hydrofoil)
                CurVee.SetHydrofoil();
                break;
            case 3:     //Naval (Submarine)
                CurVee.SetSubmarine();
                break;
            case 4:     //Tracked
                CurVee.SetTracked();
                break;
            case 5:     //VTOL
                CurVee.setVTOL();
                break;
            case 6:     //Wheeled
                CurVee.SetWheeled();
                break;
            case 7:
                CurVee.SetWiGE();
                break;
            case 8:
                CurVee.SetSuperHeavyHover();
                break;
            case 9:
                CurVee.SetSuperHeavyDisplacement();
                break;
            default:
                CurVee.SetHover();
                break;
        }
        if ( !CurVee.getCurConfig().CanBeTrailer() ) {
            chkTrailer.setSelected(false);
            chkTrailer.setEnabled(false);
        } else {
            chkTrailer.setEnabled(true);
        }

        BuildTurretSelector();
        cmbTurretActionPerformed(null);
        BuildChassisSelector();
        lblVeeLimits.setText(CurVee.GetMaxTonnage() + "t Max");
        FixTonnageSpinner( CurVee.GetMinTonnage(), CurVee.GetMaxTonnage() );
        FixArmorSpinners();
        FixMPSpinner();
        if( CurVee.IsVTOL() != wasVtol ) RecalcArmorPlacement();
        RecalcArmorLocations();
        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
        RefreshEquipment();
    }

    private void cmbProductionEraActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetProductionEra(cmbProductionEra.getSelectedIndex());
    }

    private void chkYearRestrictActionPerformed(java.awt.event.ActionEvent evt) {
        // This locks in the mech's production year, era, and tech base.
        int year = 0;
        if (CurVee.IsYearRestricted() == chkYearRestrict.isSelected()) {
            return;
        }

        // if we just unchecked the box, clear all locks and exit.
        if (!chkYearRestrict.isSelected()) {
            cmbEra.setEnabled(true);
            cmbTechBase.setEnabled(true);
            txtProdYear.setEnabled(true);
            CurVee.SetYearRestricted(false);
            switch (cmbEra.getSelectedIndex()) {
                case AvailableCode.ERA_STAR_LEAGUE:
                    CurVee.SetYear(2750, false);
                    break;
                case AvailableCode.ERA_SUCCESSION:
                    CurVee.SetYear(3025, false);
                    break;
                case AvailableCode.ERA_CLAN_INVASION:
                    CurVee.SetYear(3070, false);
                    break;
                case AvailableCode.ERA_DARK_AGES:
                    CurVee.SetYear(3132, false);
                    break;
                case AvailableCode.ERA_ALL:
                    CurVee.SetYear(0, false);
                    break;
            }
        } else {
            // ensure we have a good year.
            try {
                year = Integer.parseInt(txtProdYear.getText());
            } catch (NumberFormatException n) {
                Media.Messager(this, "The production year is not a number.");
                txtProdYear.setText("");
                chkYearRestrict.setSelected(false);
                return;
            }

            // ensure the year is between the era years.
            switch (cmbEra.getSelectedIndex()) {
                case AvailableCode.ERA_STAR_LEAGUE:
                    // Star League era
                    if (year < 2443 || year > 2800) {
                        Media.Messager(this, "The year does not fall within this era.");
                        txtProdYear.setText("");
                        chkYearRestrict.setSelected(false);
                        return;
                    }
                    break;
                case AvailableCode.ERA_SUCCESSION:
                    // Succession Wars era
                    if (year < 2801 || year > 3050) {
                        Media.Messager(this, "The year does not fall within this era.");
                        txtProdYear.setText("");
                        chkYearRestrict.setSelected(false);
                        return;
                    }
                    break;
                case AvailableCode.ERA_CLAN_INVASION:
                    // Clan Invasion Era
                    if (year < 3051 || year > 3131) {
                        Media.Messager(this, "The year does not fall within this era.");
                        txtProdYear.setText("");
                        chkYearRestrict.setSelected(false);
                        return;
                    }
                    break;
                case AvailableCode.ERA_DARK_AGES:
                    // Clan Invasion Era
                    if (year < 3132) {
                        Media.Messager(this, "The year does not fall within this era.");
                        txtProdYear.setText("");
                        chkYearRestrict.setSelected(false);
                        return;
                    }
                    break;
                case AvailableCode.ERA_ALL:
                    // all era
                    chkYearRestrict.setSelected(false);
                    chkYearRestrict.setEnabled(false);
            }

            // we know we have a good year, lock it in.
            cmbEra.setEnabled(false);
            cmbTechBase.setEnabled(false);
            txtProdYear.setEnabled(false);
            CurVee.SetYear(year, true);
            CurVee.SetYearRestricted(true);
        }

        // get the currently chosen selections
        SaveSelections();

        // first, refresh all the combo boxes.
        BuildChassisSelector();
        BuildEngineSelector();
        BuildArmorSelector();
        RefreshEquipment();
        CheckOmni();

        // now reset the combo boxes to the closest previously selected
        LoadSelections();

        // now redo the mech based on what happened.
        RecalcEngine();
        RecalcArmor();
        RecalcEquipment();
        CurVee.GetLoadout().FlushIllegal();

        // finally, refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();
    }

    private void cmbTechBaseActionPerformed(java.awt.event.ActionEvent evt) {
        if (Load) {
            return;
        }
        // do we really need to do this?
        if (CurVee.IsOmni()) {
            if (CurVee.GetLoadout().GetTechBase() == cmbTechBase.getSelectedIndex()) {
                return;
            }
        } else {
            if (CurVee.GetTechbase() == cmbTechBase.getSelectedIndex()) {
                return;
            }
        }

        if (CurVee.IsOmni()) {
            boolean check = CurVee.SetTechBase(cmbTechBase.getSelectedIndex());
            if (!check) {
                Media.Messager(this, "An Omni can only use the base chassis' Tech Base\nor Mixed Tech.  Resetting.");
                cmbTechBase.setSelectedIndex(CurVee.GetLoadout().GetTechBase());
                return;
            }
            RefreshEquipment();
        } else {
            // now change the mech over to the new techbase
            switch (cmbTechBase.getSelectedIndex()) {
                case AvailableCode.TECH_INNER_SPHERE:
                    CurVee.SetInnerSphere();
                    break;
                case AvailableCode.TECH_CLAN:
                    CurVee.SetClan();
                    break;
                case AvailableCode.TECH_BOTH:
                    CurVee.SetMixed();
                    break;
            }

            //Adding these here for now, since the loadout gets completely trashed, these selections need to go away.
            chkSupercharger.setSelected(false);
            chkJetBooster.setSelected(false);

            // save the current selections.  The 'Mech should have already
            // flushed any illegal equipment in the changeover
            SaveSelections();

            data.Rebuild(CurVee);

            // refresh all the combo boxes.
            BuildChassisSelector();
            BuildEngineSelector();
            BuildArmorSelector();
            RefreshEquipment();
            FixMPSpinner();
            FixJJSpinnerModel();
            CheckOmni();

            // now reset the combo boxes to the closest choices we previously selected
            LoadSelections();

            if (CurVee.GetTechBase() == AvailableCode.TECH_CLAN) {
                chkCASE.setSelected(true);
                chkCASEActionPerformed(evt);
            }
            RecalcEngine();
            RecalcArmor();
        }

        RecalcEquipment();
        SetWeaponChoosers();
        chkUseTC.setSelected(false);

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
    }

    private void cmbEraActionPerformed(java.awt.event.ActionEvent evt) {
        if (Load) {
            return;
        }
        // whenever the era is changed we basically need to reset the GUI and
        // most of the mech.  Certain things we will transfer.
        if (CurVee.GetEra() == cmbEra.getSelectedIndex()) {
            return;
        }
        if (CurVee.IsOmni()) {
            if (cmbEra.getSelectedIndex() < CurVee.GetBaseEra()) {
                Media.Messager(this, "An Omni loadout cannot have an era lower than the main loadout.");
                cmbEra.setSelectedIndex(CurVee.GetBaseEra());
            }
        }

        // first, let's save the tech base selection in case we can still use it
        // prevents Clan mechs reverting to Inner Sphere on era change.
        int tbsave = cmbTechBase.getSelectedIndex();

        // change the year range and tech base options
        switch (cmbEra.getSelectedIndex()) {
            case AvailableCode.ERA_STAR_LEAGUE:
                lblEraYears.setText("2443 ~ 2800");
                txtProdYear.setText("");
                CurVee.SetEra(AvailableCode.ERA_STAR_LEAGUE);
                CurVee.SetYear(2750, false);
                if (!CurVee.IsOmni()) {
                    chkYearRestrict.setEnabled(true);
                }
                break;
            case AvailableCode.ERA_SUCCESSION:
                lblEraYears.setText("2801 ~ 3050");
                txtProdYear.setText("");
                CurVee.SetEra(AvailableCode.ERA_SUCCESSION);
                CurVee.SetYear(3025, false);
                if (!CurVee.IsOmni()) {
                    chkYearRestrict.setEnabled(true);
                }
                break;
            case AvailableCode.ERA_CLAN_INVASION:
                lblEraYears.setText("3051 ~ 3131");
                txtProdYear.setText("");
                CurVee.SetEra(AvailableCode.ERA_CLAN_INVASION);
                CurVee.SetYear(3075, false);
                if (!CurVee.IsOmni()) {
                    chkYearRestrict.setEnabled(true);
                }
                break;
            case AvailableCode.ERA_DARK_AGES:
                lblEraYears.setText("3132 on");
                txtProdYear.setText("");
                CurVee.SetEra(AvailableCode.ERA_DARK_AGES);
                CurVee.SetYear(3132, false);
                if (!CurVee.IsOmni()) {
                    chkYearRestrict.setEnabled(true);
                }
                break;
            case AvailableCode.ERA_ALL:
                lblEraYears.setText("Any");
                txtProdYear.setText("");
                CurVee.SetEra(AvailableCode.ERA_ALL);
                CurVee.SetYear(0, false);
                chkYearRestrict.setEnabled(false);
                break;
        }

        if (CurVee.IsOmni()) {
            RefreshEquipment();
            RefreshSummary();
            RefreshInfoPane();
            SetWeaponChoosers();
            ResetAmmo();
            return;
        }

        BuildTechBaseSelector();
        BuildTurretSelector();

        // reset the tech base if it's still allowed
        if (tbsave < cmbTechBase.getItemCount()) {
            // still valid, use it.  No reconfigure needed
            cmbTechBase.setSelectedIndex(tbsave);
        } else {
            // nope, set it to Inner Sphere.  This means it was Clan and we
            // should reconfigure the mech
            cmbTechBase.setSelectedIndex(0);
            CurVee.SetInnerSphere();
        }

        // get the currently chosen selections
        SaveSelections();

        // since you can only ever change the era when not restricted, we're not
        // doing it here.  Pass in default values.
        CurVee.GetLoadout().FlushIllegal();

        // refresh all the combo boxes.
        BuildChassisSelector();
        BuildEngineSelector();
        BuildArmorSelector();
        FixMPSpinner();
        FixJJSpinnerModel();
        RefreshEquipment();
        CheckOmni();

        // now reset the combo boxes to the closest choices we previously selected
        LoadSelections();

        // when a new era is selected, we have to recalculate the mech
        RecalcEngine();
        RecalcArmor();
        RecalcEquipment();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();
    }

    private void cmbRulesLevelActionPerformed(java.awt.event.ActionEvent evt) {
        int NewLevel = cmbRulesLevel.getSelectedIndex();
        int OldLevel = CurVee.GetLoadout().GetRulesLevel();
        int OldType = cmbMotiveType.getSelectedIndex();
        int OldTech = CurVee.GetTechbase();

        if (OldLevel == NewLevel) {
            // we're already at the correct rules level.
            return;
        }

        // do we have an OmniVee?
        if (CurVee.IsOmni()) {
            // see if we can set to the new rules level.
            if (CurVee.GetLoadout().SetRulesLevel(NewLevel)) {
                // we can.
                if (OldLevel > NewLevel) {
                    CurVee.GetLoadout().FlushIllegal();
                }
                BuildTechBaseSelector();
                cmbTechBase.setSelectedIndex(CurVee.GetLoadout().GetTechBase());
                RecalcEquipment();
            } else {
                // can't.  reset to the default rules level and scold the user
                Media.Messager(this, "You cannot set an OmniVee's loadout to a Rules Level\nlower than it's chassis' Rules Level.");
                cmbRulesLevel.setSelectedIndex(CurVee.GetLoadout().GetRulesLevel());
                return;
            }
        } else {
            CurVee.SetRulesLevel(NewLevel);
            CheckTonnage(true);

            // get the currently chosen selections
            SaveSelections();
            BuildTechBaseSelector();
            if (OldTech >= cmbTechBase.getItemCount()) {
                // ooooh fun, we can't set it correctly.
                switch (OldTech) {
                    case AvailableCode.TECH_INNER_SPHERE:
                        // WTF???
                        System.err.println("Fatal Error when reseting techbase, Inner Sphere not available.");
                        break;
                    default:
                        // set it to Inner Sphere
                        cmbTechBase.setSelectedIndex(0);
                        //cmbTechBaseActionPerformed( null );
                        break;
                }
            }

            // since you can only ever change the rules level when not restricted,
            // we're not doing it here.  Pass in default values.
            CurVee.GetLoadout().FlushIllegal();

            // refresh all the combo boxes.
            BuildChassisSelector();
            BuildEngineSelector();
            BuildArmorSelector();
            BuildExpEquipmentSelector();
            FixMPSpinner();
            FixJJSpinnerModel();

            // now reset the combo boxes to the closest choices we previously selected
            LoadSelections();

            RecalcEngine();
            RecalcArmor();
            RecalcEquipment();
        }

        BuildTurretSelector();
        // now refresh the information panes
        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();
    }

    private void cmbLocationMouseClicked(java.awt.event.MouseEvent evt) {
        if ( evt.getClickCount() == 2 )
            btnAddEquipActionPerformed(null);
    }

    private void chkSponsonTurretActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.setHasSponsonTurret(chkSponsonTurret.isSelected());
        RefreshSelectedEquipment();
        BuildLocationSelector();
    }

    private void mnuReloadEquipmentActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            data = new DataFactory( CurVee );
        } catch( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }
        SetWeaponChoosers();
    }

    private PagePrinter SetupPrinter() {
        PagePrinter printer = new PagePrinter();
        Media media = new Media();

        printer.setJobName(CurVee.GetFullName());

        PrintVehicle sheet = new PrintVehicle(CurVee, imageTracker);
        sheet.setPrintMech(true);
        sheet.setUnitImage(media.GetImage(CurVee.GetSSWImage()));
        printer.Append(BFBPrinter.Letter.toPage(), sheet);

        return printer;
    }

    private void FixMPSpinner() {
        // This fixes the walking MP spinner if the mech's tonnage changes.
        int MaxWalk = CurVee.getMaxCruiseMP();
        int CurWalk = CurVee.getCruiseMP();

        if( CurWalk > MaxWalk ) { CurWalk = MaxWalk; }
        if( CurWalk < CurVee.getMinCruiseMP() ) { CurWalk = CurVee.getMinCruiseMP(); }
        try {
            CurVee.setCruiseMP( CurWalk );
        } catch( Exception e ) {
            Media.Messager( e.getMessage() + "\nSetting Walk MP to 1.  Please reset to desired speed.");
            try {
                CurVee.setCruiseMP( 1 );
            } catch( Exception e1 ) {
                Media.Messager( this, "Fatal error while attempting to set Walk MP to 1:\n" + e1.getMessage() + "\nStarting over with a new Vehicle.  Sorry." );
                //GetNewVee();
                return;
            }
        }
        lblFlankMP.setText( "" + CurVee.getFlankMP() );

        // reset the spinner model and we're done.
        spnCruiseMP.setModel( new javax.swing.SpinnerNumberModel( CurVee.getCruiseMP(), CurVee.getMinCruiseMP(), MaxWalk, 1) );
        ((JSpinner.DefaultEditor)spnCruiseMP.getEditor()).getTextField().addFocusListener(spinners);
    }

    private void FixJJSpinnerModel() {
        // since the jump jet spinner model changes every time the walking mp
        // changes, here is a quick little routine to do it without extra fuss.

        int min = 0;
        int max;
        int current = 0;

//        if( locArmor.IsOmni() ) {
//            min = locArmor.GetJumpJets().GetBaseLoadoutNumJJ();
//        }

        max = CurVee.getCruiseMP();

//        current = locArmor.GetJumpJets().GetNumJJ();

        // is the number of jump jets greater than the maximum allowed?
//        if( current > max ) {
//            for( ; current > max; current-- ) {
//                locArmor.GetJumpJets().DecrementNumJJ();
//            }
//        }

        // is the number of jump jet less than the minimum?
//        if( current < min ) {
//            for( ; current < min; current++ ) {
//                locArmor.GetJumpJets().IncrementNumJJ();
//            }
//        }

        // see if we need to enable the jump jet manufacturer field
//        if( locArmor.GetJumpJets().GetNumJJ() > 0 ) {
//            // enable the field
//            txtJJModel.setEnabled( true );
//        } else {
//            // disable it, but don't clear it
//            txtJJModel.setEnabled( false );
//        }

        spnJumpMP.setModel( new javax.swing.SpinnerNumberModel( current, min, max, 1) );
    }

    private javax.swing.JButton btnAddVariant;
    private javax.swing.JButton btnDeleteVariant;
    private javax.swing.JButton btnLockChassis;
    private javax.swing.JButton btnMaximize;
    private javax.swing.JButton btnRenameVariant;
    private javax.swing.JButton btnSetArmorTons;
    private javax.swing.JButton btnUseRemaining;
    private javax.swing.JCheckBox chkArmoredMotive;
    private javax.swing.JCheckBox chkBalanceFRArmor;
    private javax.swing.JCheckBox chkBalanceLRArmor;
    private javax.swing.JCheckBox chkCASE;
    private javax.swing.JCheckBox chkCommandConsole;
    private javax.swing.JCheckBox chkDuneBuggy;
    private javax.swing.JCheckBox chkEnviroSealing;
    private javax.swing.JCheckBox chkEscapePod;
    private javax.swing.JCheckBox chkFCSAIV;
    private javax.swing.JCheckBox chkFCSAV;
    private javax.swing.JCheckBox chkFCSApollo;
    private javax.swing.JCheckBox chkFlotationHull;
    private javax.swing.JCheckBox chkFractional;
    private javax.swing.JCheckBox chkFullAmph;
    private javax.swing.JCheckBox chkIndividualWeapons;
    private javax.swing.JCheckBox chkJetBooster;
    private javax.swing.JCheckBox chkLimitedAmph;
    private javax.swing.JCheckBox chkMinesweeper;
    private javax.swing.JCheckBox chkOmniVee;
    private javax.swing.JCheckBox chkSponsonTurret;
    private javax.swing.JCheckBox chkSupercharger;
    private javax.swing.JCheckBox chkTrailer;
    private javax.swing.JCheckBox chkUseTC;
    private javax.swing.JCheckBox chkYearRestrict;
    private javax.swing.JComboBox cmbArmorType;
    private javax.swing.JComboBox cmbEngineType;
    private javax.swing.JComboBox cmbEra;
    private javax.swing.JList cmbLocation;
    private javax.swing.JComboBox cmbMotiveType;
    private javax.swing.JComboBox cmbNumEquips;
    private javax.swing.JComboBox cmbOmniVariant;
    private javax.swing.JComboBox cmbProductionEra;
    private javax.swing.JComboBox cmbRulesLevel;
    private javax.swing.JComboBox cmbTechBase;
    private javax.swing.JComboBox cmbTurret;
    private javax.swing.JLabel lblMMNameInfo;
    private javax.swing.JTextArea jTextAreaBFConversion;
    private javax.swing.JLabel lblArmorCoverage;
    private javax.swing.JLabel lblArmorLeftInLot;
    private javax.swing.JLabel lblArmorTonsWasted;
    private javax.swing.JLabel lblArmorTotals;
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
    private javax.swing.JLabel lblBaseEngineRating;
    private javax.swing.JLabel lblEraYears;
    private javax.swing.JLabel lblFinalEngineRating;
    private javax.swing.JLabel lblFlankMP;
    private javax.swing.JLabel lblMoveSummary;
    private javax.swing.JLabel lblFluffImage;
    private javax.swing.JLabel lblFreeHeatSinks;
    private javax.swing.JLabel lblFrontIntPts;
    private javax.swing.JLabel lblInfoAVCI;
    private javax.swing.JLabel lblInfoAVDA;
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
    private javax.swing.JLabel lblInfoRange;
    private javax.swing.JLabel lblInfoReintro;
    private javax.swing.JLabel lblInfoRulesLevel;
    private javax.swing.JLabel lblInfoSpecials;
    private javax.swing.JLabel lblInfoTonnage;
    private javax.swing.JLabel lblInfoType;
    private javax.swing.JLabel lblLeftIntPts;
    private javax.swing.JLabel lblMinEngineTons;
    private javax.swing.JLabel lblNumCrew;
    private javax.swing.JLabel lblRearIntPts;
    private javax.swing.JLabel lblRearTurretIntPts;
    private javax.swing.JLabel lblRightIntPts;
    private javax.swing.JLabel lblRotorIntPts;
    private javax.swing.JLabel lblSupensionFacter;
    private javax.swing.JLabel lblTurretIntPts;
    private javax.swing.JLabel lblVeeClass;
    private javax.swing.JLabel lblVeeLimits;
    private javax.swing.JList lstChooseAmmunition;
    private javax.swing.JList lstChooseArtillery;
    private javax.swing.JList lstChooseBallistic;
    private javax.swing.JList lstChooseEnergy;
    private javax.swing.JList lstChooseEquipment;
    private javax.swing.JList lstChooseMissile;
    private javax.swing.JList lstChoosePhysical;
    private javax.swing.JList lstSelectedEquipment;
    private javax.swing.JMenuItem mnuUnlock;
    private javax.swing.JCheckBoxMenuItem mnuViewToolbar;
    private javax.swing.JPanel pnlAdditionalFluff;
    private javax.swing.JPanel pnlBasicSetup;
    private javax.swing.JPanel pnlCapabilities;
    private javax.swing.JPanel pnlDeployment;
    private javax.swing.JPanel pnlEquipInfo;
    private javax.swing.JPanel pnlHistory;
    private javax.swing.JPanel pnlNotables;
    private javax.swing.JPanel pnlOverview;
    private javax.swing.JPanel pnlRearTurretArmor;
    private javax.swing.JPanel pnlRotorArmor;
    private javax.swing.JPanel pnlTurretArmor;
    private javax.swing.JPanel pnlVariants;
    private javax.swing.JSpinner spnCruiseMP;
    private javax.swing.JSpinner spnFrontArmor;
    private javax.swing.JSpinner spnHeatSinks;
    private javax.swing.JSpinner spnJumpMP;
    private javax.swing.JSpinner spnLeftArmor;
    private javax.swing.JSpinner spnRearArmor;
    private javax.swing.JSpinner spnRearTurretArmor;
    private javax.swing.JSpinner spnRightArmor;
    private javax.swing.JSpinner spnRotorArmor;
    private javax.swing.JSpinner spnTonnage;
    private javax.swing.JSpinner spnTurretArmor;
    private javax.swing.JSpinner spnTurretTonnage;
    private javax.swing.JSpinner spnRearTurretTonnage;
    private javax.swing.JTable tblWeaponManufacturers;
    private javax.swing.JTabbedPane tbpMainTabPane;
    private javax.swing.JTabbedPane tbpWeaponChooser;
    private javax.swing.JToolBar tlbIconBar;
    private javax.swing.JTextField txtArmorModel;
    private javax.swing.JTextField txtArmorSpace;
    private javax.swing.JTextField txtArmorTons;
    private javax.swing.JTextField txtChassisModel;
    private javax.swing.JTextField txtCommSystem;
    private javax.swing.JTextField txtEngineManufacturer;
    private javax.swing.JTextField txtInfoBattleValue;
    private javax.swing.JTextField txtInfoCost;
    private javax.swing.JTextField txtInfoFreeCrits;
    private javax.swing.JTextField txtInfoFreeTons;
    private javax.swing.JTextField txtInfoTonnage;
    private javax.swing.JTextField txtJJModel;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextField txtManufacturer;
    private javax.swing.JTextField txtManufacturerLocation;
    private javax.swing.JTextField txtModel;
    private javax.swing.JTextField txtProdYear;
    private javax.swing.JTextField txtSource;
    private javax.swing.JTextField txtSumArmAV;
    private javax.swing.JTextField txtSumArmSpace;
    private javax.swing.JTextField txtSumArmTons;
    private javax.swing.JTextField txtSumConAV;
    private javax.swing.JTextField txtSumConTons;
    private javax.swing.JTextField txtSumEngAV;
    private javax.swing.JTextField txtSumEngSpace;
    private javax.swing.JTextField txtSumEngTons;
    private javax.swing.JTextField txtSumHSAV;
    private javax.swing.JTextField txtSumHSTons;
    private javax.swing.JTextField txtSumIntAV;
    private javax.swing.JTextField txtSumIntTons;
    private javax.swing.JTextField txtSumJJAV;
    private javax.swing.JTextField txtSumJJTons;
    private javax.swing.JTextField txtSumLifAV;
    private javax.swing.JTextField txtSumLifTons;
    private javax.swing.JTextField txtSumPAAV;
    private javax.swing.JTextField txtSumPATons;
    private javax.swing.JTextField txtSumRTuAV;
    private javax.swing.JTextField txtSumRTuTons;
    private javax.swing.JTextField txtSumSpnAV;
    private javax.swing.JTextField txtSumSpnTons;
    private javax.swing.JTextField txtSumTurAV;
    private javax.swing.JTextField txtSumTurTons;
    private javax.swing.JTextField txtTNTSystem;
    private javax.swing.JTextField txtTurretInfo;
    private javax.swing.JTextField txtVehicleName;
    private javax.swing.JTable tblQuirks;
    // End of variables declaration//GEN-END:variables

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //do nothing
    }

    public void setUnit(ArrayList v) {
        this.setVee( (CombatVehicle) v.get(0) );
    }
    
    public void setVee( CombatVehicle v ) {
        GetNewVee();
        CurVee = v;
        LoadVehicleIntoGUI();
    }
    
    public void loadUnitIntoGUI() {
        this.LoadVehicleIntoGUI();
    }

    public void showOpenDialog() {
        this.dOpen.Requestor = dlgOpen.FORCE;
        this.dOpen.setVisible(true);
    }

    public Preferences GetPrefs() {
        return Prefs;
    }

    public int GetLocation(JList list) {
        //do nothing
        return 0;
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

    public CombatVehicle GetVehicle() {
        return CurVee;
    }

}

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

public final class frmVee extends javax.swing.JFrame implements java.awt.datatransfer.ClipboardOwner, common.DesignForm, ifVeeForm {
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
    JMenuItem mnuMountRear = new JMenuItem( "Mount Rear" );
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
    public frmVee() {
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
        cmbRulesLevel.setSelectedItem( Prefs.get( "NewVee_RulesLevel", "Tournament Legal" ) );
        cmbEra.setSelectedItem( Prefs.get( "NewVee_Era", "Age of War/Star League" ) );
        BuildTechBaseSelector();
        cmbTechBase.setSelectedItem( Prefs.get( "NewVee_Techbase", "Inner Sphere" ) );
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

        //if( Prefs.getBoolean( "LoadLastMech", false ) ) { LoadVehicleFromFile(Prefs.get("LastOpenCVDirectory", "") + Prefs.get("LastOpenCVFile", "") ); }
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
                abPlaceable p = ((RangedWeapon) CurItem).GetCapacitor();
                LocationIndex Loc = CurVee.GetLoadout().FindIndex( CurItem );
                if( Loc.Location != -1 ) {
                    try {
                        CurVee.GetLoadout().Remove(CurItem);
                        CurVee.GetLoadout().AddTo( CurItem, Loc.Location );
                    } catch( Exception e ) {
                        // couldn't allocate the capacitor?  Unallocate the PPC.
                        try {
                            CurVee.GetLoadout().UnallocateAll( CurItem, false );
                            // remove the capacitor if it's in the queue
                            //if( CurVee.GetLoadout().QueueContains( p ) ) {
                            //    CurVee.GetLoadout().GetQueue().remove( p );
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
                            // remove the insulator if it's in the queue
                            //if( CurVee.GetLoadout().QueueContains( p ) ) {
                            //    CurVee.GetLoadout().GetQueue().remove( p );
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
                            // remove the insulator if it's in the queue
                            //if( CurVee.GetLoadout().QueueContains( p ) ) {
                            //    CurVee.GetLoadout().GetQueue().remove( p );
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

    private void RefreshSelectedEquipment() {
        Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
        lstSelectedEquipment.setListData( Equipment[SELECTED] );
        lstSelectedEquipment.repaint();
    }

    private void setViewToolbar(boolean Visible)
    {
        tlbIconBar.setVisible(Visible);
        Prefs.putBoolean("ViewToolbar", Visible);
        //mnuViewToolbar.setState(Visible);
        if (Visible) {
            if (this.getHeight() != 600) { this.setSize(750, 625); }
        } else {
            if (this.getHeight() != 575) { this.setSize(750, 600); }
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
            if( CurVee.GetLoadout().GetNonCore().toArray().length <= 0 ) {
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
        return ((Equipment) p).CritName().equals("Cargo Container");
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
//        temp += CurMech.GetAdjustedJumpingMP( false ) + "/";
//        temp += CurMech.GetAdjustedBoosterMP( false );
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        JPanel pnlInfoPane = new JPanel();
        txtInfoTonnage = new javax.swing.JTextField();
        txtInfoFreeTons = new javax.swing.JTextField();
        txtInfoFreeCrits = new javax.swing.JTextField();
        txtTurretInfo = new javax.swing.JTextField();
        txtInfoBattleValue = new javax.swing.JTextField();
        txtInfoCost = new javax.swing.JTextField();
        tlbIconBar = new javax.swing.JToolBar();
        JButton btnNewVee = new JButton();
        JButton btnOpen = new JButton();
        JButton btnSave = new JButton();
        JToolBar.Separator jSeparator1 = new JToolBar.Separator();
        JButton btnPrint = new JButton();
        JToolBar.Separator jSeparator2 = new JToolBar.Separator();
        JButton btnExportClipboardIcon = new JButton();
        JButton btnExportHTMLIcon = new JButton();
        JButton btnExportTextIcon = new JButton();
        JButton btnExportMTFIcon = new JButton();
        JButton btnChatInfo = new JButton();
        JToolBar.Separator jSeparator3 = new JToolBar.Separator();
        JToolBar.Separator jSeparator25 = new JToolBar.Separator();
        JButton btnAddToForceList = new JButton();
        JButton btnForceList = new JButton();
        JToolBar.Separator jSeparator4 = new JToolBar.Separator();
        JButton btnOptions = new JButton();
        JToolBar.Separator jSeparator21 = new JToolBar.Separator();
        JLabel lblSelectVariant = new JLabel();
        lblMoveSummary = new javax.swing.JLabel();
        cmbOmniVariant = new javax.swing.JComboBox();
        tbpMainTabPane = new JTabbedPane();
        pnlBasicSetup = new javax.swing.JPanel();
        JPanel pnlBasicInfo = new JPanel();
        JLabel jLabel1 = new JLabel();
        txtVehicleName = new javax.swing.JTextField();
        JLabel jLabel4 = new JLabel();
        txtModel = new javax.swing.JTextField();
        JLabel jLabel2 = new JLabel();
        cmbRulesLevel = new javax.swing.JComboBox();
        JLabel jLabel5 = new JLabel();
        cmbEra = new javax.swing.JComboBox();
        JLabel jLabel3 = new JLabel();
        cmbTechBase = new javax.swing.JComboBox();
        lblEraYears = new javax.swing.JLabel();
        chkYearRestrict = new javax.swing.JCheckBox();
        JLabel jLabel81 = new JLabel();
        txtSource = new javax.swing.JTextField();
        JLabel lblProdYear = new JLabel();
        txtProdYear = new javax.swing.JTextField();
        cmbProductionEra = new javax.swing.JComboBox();
        JPanel pnlChassis = new JPanel();
        cmbMotiveType = new javax.swing.JComboBox();
        JLabel jLabel7 = new JLabel();
        JLabel jLabel8 = new JLabel();
        chkOmniVee = new javax.swing.JCheckBox();
        chkTrailer = new javax.swing.JCheckBox();
        spnTonnage = new javax.swing.JSpinner();
        JLabel jLabel9 = new JLabel();
        cmbEngineType = new javax.swing.JComboBox();
        JLabel jLabel32 = new JLabel();
        cmbTurret = new javax.swing.JComboBox();
        lblVeeClass = new javax.swing.JLabel();
        lblVeeLimits = new javax.swing.JLabel();
        JLabel jLabel91 = new JLabel();
        spnHeatSinks = new javax.swing.JSpinner();
        spnTurretTonnage = new javax.swing.JSpinner();
        JPanel pnlMovement = new JPanel();
        JLabel jLabel10 = new JLabel();
        spnCruiseMP = new javax.swing.JSpinner();
        JLabel jLabel11 = new JLabel();
        lblFlankMP = new javax.swing.JLabel();
        JLabel jLabel13 = new JLabel();
        spnJumpMP = new javax.swing.JSpinner();
        JPanel pnlChassisMods = new JPanel();
        chkFlotationHull = new javax.swing.JCheckBox();
        chkLimitedAmph = new javax.swing.JCheckBox();
        chkFullAmph = new javax.swing.JCheckBox();
        chkDuneBuggy = new javax.swing.JCheckBox();
        chkEnviroSealing = new javax.swing.JCheckBox();
        JPanel pnlExperimental = new JPanel();
        chkArmoredMotive = new javax.swing.JCheckBox();
        chkCommandConsole = new javax.swing.JCheckBox();
        chkEscapePod = new javax.swing.JCheckBox();
        chkMinesweeper = new javax.swing.JCheckBox();
        chkJetBooster = new javax.swing.JCheckBox();
        chkSupercharger = new javax.swing.JCheckBox();
        chkSponsonTurret = new javax.swing.JCheckBox();
        JPanel jPanel11 = new JPanel();
        chkFractional = new javax.swing.JCheckBox();
        JPanel pnlSummary = new JPanel();
        JLabel jLabel12 = new JLabel();
        JLabel jLabel14 = new JLabel();
        JLabel jLabel15 = new JLabel();
        JLabel jLabel16 = new JLabel();
        JLabel jLabel17 = new JLabel();
        txtSumIntTons = new javax.swing.JTextField();
        txtSumIntAV = new javax.swing.JTextField();
        JLabel jLabel18 = new JLabel();
        txtSumEngTons = new javax.swing.JTextField();
        txtSumEngAV = new javax.swing.JTextField();
        JLabel jLabel19 = new JLabel();
        txtSumLifTons = new javax.swing.JTextField();
        txtSumLifAV = new javax.swing.JTextField();
        txtSumEngSpace = new javax.swing.JTextField();
        JLabel jLabel20 = new JLabel();
        txtSumConTons = new javax.swing.JTextField();
        txtSumConAV = new javax.swing.JTextField();
        JLabel jLabel21 = new JLabel();
        txtSumJJTons = new javax.swing.JTextField();
        JTextField txtSumJJSpace = new JTextField();
        txtSumJJAV = new javax.swing.JTextField();
        JLabel jLabel22 = new JLabel();
        txtSumHSTons = new javax.swing.JTextField();
        txtSumHSAV = new javax.swing.JTextField();
        JLabel jLabel23 = new JLabel();
        txtSumArmTons = new javax.swing.JTextField();
        txtSumArmSpace = new javax.swing.JTextField();
        txtSumArmAV = new javax.swing.JTextField();
        JLabel jLabel24 = new JLabel();
        txtSumTurTons = new javax.swing.JTextField();
        txtSumTurAV = new javax.swing.JTextField();
        JLabel jLabel25 = new JLabel();
        txtSumRTuTons = new javax.swing.JTextField();
        txtSumRTuAV = new javax.swing.JTextField();
        JLabel jLabel26 = new JLabel();
        txtSumSpnTons = new javax.swing.JTextField();
        txtSumSpnAV = new javax.swing.JTextField();
        JLabel jLabel27 = new JLabel();
        txtSumPATons = new javax.swing.JTextField();
        txtSumPAAV = new javax.swing.JTextField();
        JPanel pnlInformation = new JPanel();
        JLabel jLabel29 = new JLabel();
        lblSupensionFacter = new javax.swing.JLabel();
        JLabel jLabel31 = new JLabel();
        lblMinEngineTons = new javax.swing.JLabel();
        JLabel jLabel33 = new JLabel();
        lblBaseEngineRating = new javax.swing.JLabel();
        JLabel jLabel35 = new JLabel();
        lblFinalEngineRating = new javax.swing.JLabel();
        JLabel jLabel28 = new JLabel();
        lblFreeHeatSinks = new javax.swing.JLabel();
        JLabel jLabel30 = new JLabel();
        lblNumCrew = new javax.swing.JLabel();
        JPanel pnlOmniInfo = new JPanel();
        btnLockChassis = new javax.swing.JButton();
        btnAddVariant = new javax.swing.JButton();
        btnDeleteVariant = new javax.swing.JButton();
        btnRenameVariant = new javax.swing.JButton();
        JPanel jPanel2 = new JPanel();
        JPanel jPanel6 = new JPanel();
        JPanel pnlRightArmor = new JPanel();
        lblRightIntPts = new javax.swing.JLabel();
        JLabel jLabel40 = new JLabel();
        JLabel jLabel46 = new JLabel();
        spnRightArmor = new javax.swing.JSpinner();
        JPanel pnlFrontArmor = new JPanel();
        lblFrontIntPts = new javax.swing.JLabel();
        JLabel jLabel45 = new JLabel();
        JLabel jLabel47 = new JLabel();
        spnFrontArmor = new javax.swing.JSpinner();
        JPanel pnlLeftArmor = new JPanel();
        lblLeftIntPts = new javax.swing.JLabel();
        JLabel jLabel41 = new JLabel();
        JLabel jLabel48 = new JLabel();
        spnLeftArmor = new javax.swing.JSpinner();
        JPanel pnlRearArmor = new JPanel();
        lblRearIntPts = new javax.swing.JLabel();
        JLabel jLabel44 = new JLabel();
        JLabel jLabel49 = new JLabel();
        spnRearArmor = new javax.swing.JSpinner();
        pnlTurretArmor = new javax.swing.JPanel();
        lblTurretIntPts = new javax.swing.JLabel();
        JLabel jLabel42 = new JLabel();
        JLabel jLabel50 = new JLabel();
        spnTurretArmor = new javax.swing.JSpinner();
        pnlRearTurretArmor = new javax.swing.JPanel();
        lblRearTurretIntPts = new javax.swing.JLabel();
        JLabel jLabel43 = new JLabel();
        JLabel jLabel51 = new JLabel();
        spnRearTurretArmor = new javax.swing.JSpinner();
        pnlRotorArmor = new javax.swing.JPanel();
        lblRotorIntPts = new javax.swing.JLabel();
        JLabel jLabel92 = new JLabel();
        JLabel jLabel93 = new JLabel();
        spnRotorArmor = new javax.swing.JSpinner();
        JPanel pnlArmorType = new JPanel();
        JLabel jLabel52 = new JLabel();
        cmbArmorType = new javax.swing.JComboBox();
        chkBalanceLRArmor = new javax.swing.JCheckBox();
        chkBalanceFRArmor = new javax.swing.JCheckBox();
        btnSetArmorTons = new javax.swing.JButton();
        btnUseRemaining = new javax.swing.JButton();
        btnMaximize = new javax.swing.JButton();
        JPanel jPanel8 = new JPanel();
        JLabel jLabel34 = new JLabel();
        JLabel jLabel36 = new JLabel();
        lblArmorTotals = new javax.swing.JLabel();
        lblArmorCoverage = new javax.swing.JLabel();
        txtArmorTons = new javax.swing.JTextField();
        txtArmorSpace = new javax.swing.JTextField();
        lblArmorTonsWasted = new javax.swing.JLabel();
        lblArmorLeftInLot = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        tbpWeaponChooser = new JTabbedPane();
        lstChooseBallistic = new javax.swing.JList();
        lstChooseEnergy = new javax.swing.JList();
        lstChooseMissile = new javax.swing.JList();
        lstChoosePhysical = new javax.swing.JList();
        lstChooseEquipment = new javax.swing.JList();
        lstChooseArtillery = new javax.swing.JList();
        lstChooseAmmunition = new javax.swing.JList();
        JPanel pnlSpecials = new JPanel();
        JLabel jLabel37 = new JLabel();
        chkUseTC = new javax.swing.JCheckBox();
        chkFCSAIV = new javax.swing.JCheckBox();
        chkFCSAV = new javax.swing.JCheckBox();
        chkFCSApollo = new javax.swing.JCheckBox();
        chkCASE = new javax.swing.JCheckBox();
        JPanel pnlSelected = new JPanel();
        JScrollPane jScrollPane23 = new JScrollPane();
        lstSelectedEquipment = new javax.swing.JList();
        JPanel pnlEquipInfo = new JPanel();
        JLabel jLabel38 = new JLabel();
        JLabel jLabel39 = new JLabel();
        JLabel jLabel53 = new JLabel();
        lblInfoAVSL = new javax.swing.JLabel();
        lblInfoAVSW = new javax.swing.JLabel();
        lblInfoAVCI = new javax.swing.JLabel();
        JLabel jLabel54 = new JLabel();
        JLabel jLabel55 = new JLabel();
        JLabel jLabel56 = new JLabel();
        lblInfoIntro = new javax.swing.JLabel();
        lblInfoExtinct = new javax.swing.JLabel();
        lblInfoReintro = new javax.swing.JLabel();
        JLabel jLabel57 = new JLabel();
        JLabel jLabel58 = new JLabel();
        JLabel jLabel59 = new JLabel();
        JLabel jLabel60 = new JLabel();
        JLabel jLabel61 = new JLabel();
        lblInfoName = new javax.swing.JLabel();
        lblInfoType = new javax.swing.JLabel();
        lblInfoHeat = new javax.swing.JLabel();
        lblInfoDamage = new javax.swing.JLabel();
        lblInfoRange = new javax.swing.JLabel();
        JSeparator jSeparator17 = new JSeparator();
        JLabel jLabel62 = new JLabel();
        JLabel jLabel63 = new JLabel();
        JLabel jLabel64 = new JLabel();
        JLabel jLabel65 = new JLabel();
        lblInfoAmmo = new javax.swing.JLabel();
        lblInfoTonnage = new javax.swing.JLabel();
        lblInfoCrits = new javax.swing.JLabel();
        lblInfoSpecials = new javax.swing.JLabel();
        JSeparator jSeparator20 = new JSeparator();
        JLabel jLabel66 = new JLabel();
        lblInfoCost = new javax.swing.JLabel();
        JLabel jLabel67 = new JLabel();
        lblInfoBV = new javax.swing.JLabel();
        JLabel jLabel68 = new JLabel();
        lblInfoMountRestrict = new javax.swing.JLabel();
        JLabel jLabel69 = new JLabel();
        lblInfoRulesLevel = new javax.swing.JLabel();
        JLabel jLabel6 = new JLabel();
        lblInfoAVDA = new javax.swing.JLabel();
        JPanel pnlControls = new JPanel();
        JButton btnRemoveEquip = new JButton();
        JButton btnClearEquip = new JButton();
        // Variables declaration - do not modify//GEN-BEGIN:variables
        JButton btnAddEquip = new JButton();
        cmbNumEquips = new javax.swing.JComboBox();
        JScrollPane jScrollPane1 = new JScrollPane();
        cmbLocation = new javax.swing.JList();
        JPanel jPanel4 = new JPanel();
        JPanel pnlFluff = new JPanel();
        JPanel pnlImage = new JPanel();
        lblFluffImage = new javax.swing.JLabel();
        JPanel jPanel1 = new JPanel();
        JButton btnLoadImage = new JButton();
        JButton btnClearImage = new JButton();
        JPanel pnlExport = new JPanel();
        JButton btnExportTXT = new JButton();
        JButton btnExportHTML = new JButton();
        JButton btnExportMTF = new JButton();
        JTabbedPane tbpFluffEditors = new JTabbedPane();
        pnlOverview = new javax.swing.JPanel();
        pnlCapabilities = new javax.swing.JPanel();
        pnlHistory = new javax.swing.JPanel();
        pnlDeployment = new javax.swing.JPanel();
        pnlVariants = new javax.swing.JPanel();
        pnlNotables = new javax.swing.JPanel();
        pnlAdditionalFluff = new javax.swing.JPanel();
        JPanel pnlManufacturers = new JPanel();
        JLabel jLabel82 = new JLabel();
        JLabel jLabel83 = new JLabel();
        JLabel jLabel84 = new JLabel();
        JLabel jLabel85 = new JLabel();
        JLabel jLabel86 = new JLabel();
        JLabel jLabel87 = new JLabel();
        JLabel jLabel88 = new JLabel();
        JLabel jLabel89 = new JLabel();
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
        JLabel jLabel90 = new JLabel();
        txtJJModel = new javax.swing.JTextField();
        JPanel pnlQuirks = new JPanel();
        JLabel lblBattleMechQuirks = new JLabel();
        JScrollPane scpQuirkTable = new JScrollPane();
        tblQuirks = new JTable();
        JButton btnAddQuirk = new JButton();
        JPanel jPanel9 = new JPanel();
        JPanel pnlBFStats = new JPanel();
        JLabel jLabel70 = new JLabel();
        JLabel jLabel71 = new JLabel();
        JLabel jLabel72 = new JLabel();
        JLabel jLabel73 = new JLabel();
        JLabel jLabel74 = new JLabel();
        JLabel jLabel75 = new JLabel();
        JLabel jLabel76 = new JLabel();
        JLabel jLabel77 = new JLabel();
        JLabel jLabel78 = new JLabel();
        JLabel jLabel79 = new JLabel();
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
        JLabel jLabel80 = new JLabel();
        lblBFPoints = new javax.swing.JLabel();
        JPanel jPanel10 = new JPanel();
        JScrollPane jScrollPane14 = new JScrollPane();
        jTextAreaBFConversion = new javax.swing.JTextArea();
        JMenuBar jMenuBar1 = new JMenuBar();
        JMenu mnuFile = new JMenu();
        JMenuItem mnuNewMech = new JMenuItem();
        JMenuItem mnuLoad = new JMenuItem();
        JMenuItem mnuOpen = new JMenuItem();
        JMenu mnuImport = new JMenu();
        JMenuItem mnuImportHMP = new JMenuItem();
        JMenuItem mnuBatchHMP = new JMenuItem();
        JSeparator jSeparator22 = new JSeparator();
        JMenuItem mnuSave = new JMenuItem();
        JMenuItem mnuSaveAs = new JMenuItem();
        JMenu mnuExport = new JMenu();
        JMenuItem mnuExportHTML = new JMenuItem();
        JMenuItem mnuExportMTF = new JMenuItem();
        JMenuItem mnuExportTXT = new JMenuItem();
        JMenuItem mnuExportClipboard = new JMenuItem();
        JMenuItem mnuCreateTCGMech = new JMenuItem();
        JSeparator jSeparator23 = new JSeparator();
        JMenu mnuPrint = new JMenu();
        JMenuItem mnuPrintPreview = new JMenuItem();
        JSeparator jSeparator24 = new JSeparator();
        JMenuItem mnuExit = new JMenuItem();
        JMenu mnuClearFluff = new JMenu();
        JMenuItem mnuSummary = new JMenuItem();
        JMenuItem mnuCostBVBreakdown = new JMenuItem();
        JMenuItem mnuTextTRO = new JMenuItem();
        JSeparator jSeparator26 = new JSeparator();
        JMenuItem mnuBFB = new JMenuItem();
        JSeparator jSeparator27 = new JSeparator();
        JMenuItem mnuOptions = new JMenuItem();
        mnuViewToolbar = new javax.swing.JCheckBoxMenuItem();
        JMenuItem mnuClearUserData = new JMenuItem();
        JSeparator jSeparator30 = new JSeparator();
        mnuUnlock = new javax.swing.JMenuItem();
        JMenuItem jMenuItem1 = new JMenuItem();
        JMenuItem mnuReloadEquipment = new JMenuItem();
        JMenu mnuHelp = new JMenu();
        JMenuItem mnuCredits = new JMenuItem();
        JMenuItem mnuAboutSSW = new JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

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

        tlbIconBar.setFloatable(false);
        tlbIconBar.setRollover(true);

        btnNewVee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/document--plus.png"))); // NOI18N
        btnNewVee.setFocusable(false);
        btnNewVee.setHorizontalTextPosition(SwingConstants.CENTER);
        btnNewVee.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnNewVee.addActionListener(this::btnNewVeeActionPerformed);
        tlbIconBar.add(btnNewVee);

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/folder-open-document.png"))); // NOI18N
        btnOpen.setFocusable(false);
        btnOpen.setHorizontalTextPosition(SwingConstants.CENTER);
        btnOpen.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnOpen.addActionListener(this::btnOpenActionPerformed);
        tlbIconBar.add(btnOpen);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/disk-black.png"))); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnSave.addActionListener(this::btnSaveActionPerformed);
        tlbIconBar.add(btnSave);
        tlbIconBar.add(jSeparator1);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/printer.png"))); // NOI18N
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnPrint.addActionListener(this::btnPrintActionPerformed);
        tlbIconBar.add(btnPrint);
        tlbIconBar.add(jSeparator2);

        btnExportClipboardIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/document-clipboard.png"))); // NOI18N
        btnExportClipboardIcon.setToolTipText("Export Text to Clipboard");
        btnExportClipboardIcon.setFocusable(false);
        btnExportClipboardIcon.setHorizontalTextPosition(SwingConstants.CENTER);
        btnExportClipboardIcon.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnExportClipboardIcon.addActionListener(this::btnExportClipboardIconActionPerformed);
        tlbIconBar.add(btnExportClipboardIcon);

        btnExportHTMLIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/document-image.png"))); // NOI18N
        btnExportHTMLIcon.setToolTipText("Export HTML");
        btnExportHTMLIcon.setFocusable(false);
        btnExportHTMLIcon.setHorizontalTextPosition(SwingConstants.CENTER);
        btnExportHTMLIcon.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnExportHTMLIcon.addActionListener(this::btnExportHTMLIconActionPerformed);
        tlbIconBar.add(btnExportHTMLIcon);

        btnExportTextIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/document-text.png"))); // NOI18N
        btnExportTextIcon.setToolTipText("Export Text");
        btnExportTextIcon.setFocusable(false);
        btnExportTextIcon.setHorizontalTextPosition(SwingConstants.CENTER);
        btnExportTextIcon.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnExportTextIcon.addActionListener(this::btnExportTextIconActionPerformed);
        tlbIconBar.add(btnExportTextIcon);

        btnExportMTFIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/document--arrow.png"))); // NOI18N
        btnExportMTFIcon.setToolTipText("Export MTF");
        btnExportMTFIcon.setFocusable(false);
        btnExportMTFIcon.setHorizontalTextPosition(SwingConstants.CENTER);
        btnExportMTFIcon.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnExportMTFIcon.addActionListener(this::btnExportMTFIconActionPerformed);
        tlbIconBar.add(btnExportMTFIcon);

        btnChatInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/balloon.png"))); // NOI18N
        btnChatInfo.setToolTipText("Copy Chat Line");
        btnChatInfo.setFocusable(false);
        btnChatInfo.setHorizontalTextPosition(SwingConstants.CENTER);
        btnChatInfo.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnChatInfo.addActionListener(this::btnChatInfoActionPerformed);
        tlbIconBar.add(btnChatInfo);
        tlbIconBar.add(jSeparator3);
        tlbIconBar.add(jSeparator25);

        btnAddToForceList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/clipboard--plus.png"))); // NOI18N
        btnAddToForceList.setToolTipText("Add  to Force List");
        btnAddToForceList.setFocusable(false);
        btnAddToForceList.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAddToForceList.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnAddToForceList.addActionListener(this::btnAddToForceListActionPerformed);
        tlbIconBar.add(btnAddToForceList);

        btnForceList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/clipboard.png"))); // NOI18N
        btnForceList.setToolTipText("Force List");
        btnForceList.setFocusable(false);
        btnForceList.setHorizontalTextPosition(SwingConstants.CENTER);
        btnForceList.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnForceList.addActionListener(this::btnForceListActionPerformed);
        tlbIconBar.add(btnForceList);
        tlbIconBar.add(jSeparator4);

        btnOptions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/saw/images/gear.png"))); // NOI18N
        btnOptions.setFocusable(false);
        btnOptions.setHorizontalTextPosition(SwingConstants.CENTER);
        btnOptions.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnOptions.addActionListener(this::btnOptionsActionPerformed);
        tlbIconBar.add(btnOptions);
        tlbIconBar.add(jSeparator21);

        lblSelectVariant.setText("Selected Variant: ");
        lblSelectVariant.setEnabled(false);
        tlbIconBar.add(lblSelectVariant);

        cmbOmniVariant.setEnabled(false);
        cmbOmniVariant.setMaximumSize(new Dimension(150, 20));
        cmbOmniVariant.setMinimumSize(new Dimension(150, 20));
        cmbOmniVariant.setPreferredSize(new Dimension(150, 20));
        cmbOmniVariant.addActionListener(this::cmbOmniVariantActionPerformed);
        tlbIconBar.add(cmbOmniVariant);

        pnlBasicInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Basic Information"));

        jLabel1.setText("Vehicle Name:");

        txtVehicleName.setMinimumSize(new Dimension(150, 20));
        txtVehicleName.setPreferredSize(new Dimension(150, 20));

        jLabel4.setText("Model:");

        txtModel.setMinimumSize(new Dimension(150, 20));
        txtModel.setPreferredSize(new Dimension(150, 20));

        jLabel2.setText("Rules Level:");

        cmbRulesLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Introductory", "Tournament Legal", "Advanced", "Experimental" }));
        cmbRulesLevel.setSelectedIndex(1);
        cmbRulesLevel.setMinimumSize(new Dimension(150, 20));
        cmbRulesLevel.setPreferredSize(new Dimension(150, 20));
        cmbRulesLevel.addActionListener(this::cmbRulesLevelActionPerformed);

        jLabel5.setText("Era:");

        cmbEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Age of War/Star League", "Succession Wars", "Clan Invasion", "Dark Ages", "All Eras (non-canon)" }));
        cmbEra.setSelectedIndex(1);
        cmbEra.setMinimumSize(new Dimension(150, 20));
        cmbEra.setPreferredSize(new Dimension(150, 20));
        cmbEra.addActionListener(this::cmbEraActionPerformed);

        jLabel3.setText("Tech Base:");

        cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan", "Mixed Tech" }));
        cmbTechBase.setMinimumSize(new Dimension(150, 20));
        cmbTechBase.setPreferredSize(new Dimension(150, 20));
        cmbTechBase.addActionListener(this::cmbTechBaseActionPerformed);

        lblEraYears.setText("2443~2800");

        chkYearRestrict.setText("Restrict Availability by Year");
        chkYearRestrict.addActionListener(this::chkYearRestrictActionPerformed);

        jLabel81.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel81.setText("Source:");

        txtSource.setMinimumSize(new Dimension(150, 20));
        txtSource.setPreferredSize(new Dimension(150, 20));

        lblProdYear.setText("Prod Year/Era:");

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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(pnlBasicInfo);
        pnlBasicInfo.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel2)
                        .addGap(2, 2, 2)
                        .addComponent(cmbRulesLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel5)
                        .addGap(2, 2, 2)
                        .addComponent(cmbEra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel3)
                        .addGap(2, 2, 2)
                        .addComponent(cmbTechBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(lblEraYears))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel81, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVehicleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addComponent(txtSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(24, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lblProdYear)
                        .addGap(2, 2, 2)
                        .addComponent(txtProdYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(cmbProductionEra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkYearRestrict))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel1))
                    .addComponent(txtVehicleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel4))
                    .addComponent(txtModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel81)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2))
                    .addComponent(cmbRulesLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel5))
                    .addComponent(cmbEra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addComponent(cmbTechBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(lblEraYears)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(lblProdYear))
                    .addComponent(txtProdYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbProductionEra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkYearRestrict)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        pnlChassis.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Chassis"));
        pnlChassis.setNextFocusableComponent(spnCruiseMP);

        cmbMotiveType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hovercraft", "Naval (Displacement)", "Naval (Hydrofoil)", "Naval (Submarine)", "Tracked", "VTOL", "Wheeled", "WiGE", "Hovercraft (Super Heavy)", "Displacement (Super Heavy)" }));
        cmbMotiveType.setMinimumSize(new Dimension(150, 20));
        cmbMotiveType.setPreferredSize(new Dimension(150, 20));
        cmbMotiveType.addActionListener(this::cmbMotiveTypeActionPerformed);

        jLabel7.setText("Motive Type:");

        jLabel8.setText("Tonnage:");

        chkOmniVee.setText("OmniVehicle");
        chkOmniVee.addActionListener(this::chkOmniVeeActionPerformed);

        chkTrailer.setText("Trailer");
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

        jLabel9.setText("Engine:");

        cmbEngineType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "I.C.E.", "Fusion", "Light Fusion", "XL Fusion", "Compact Fusion" }));
        cmbEngineType.setMinimumSize(new Dimension(150, 20));
        cmbEngineType.setPreferredSize(new Dimension(150, 20));
        cmbEngineType.addActionListener(this::cmbEngineTypeActionPerformed);

        jLabel32.setText("Turret:");

        cmbTurret.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Turret", "Single Turret", "Dual Turret", "Sponson Turrets", "Chin Turret", "Mast Turret" }));
        cmbTurret.addActionListener(this::cmbTurretActionPerformed);

        lblVeeClass.setHorizontalAlignment(SwingConstants.LEFT);
        lblVeeClass.setText("Assault Vee");

        lblVeeLimits.setText("500t Max");

        jLabel91.setText("Heat Sinks:");

        spnHeatSinks.setModel(new javax.swing.SpinnerNumberModel(10, 1, null, 1));
        spnHeatSinks.setMinimumSize(new Dimension(45, 20));
        spnHeatSinks.setNextFocusableComponent(spnCruiseMP);
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

        javax.swing.GroupLayout pnlChassisLayout = new javax.swing.GroupLayout(pnlChassis);
        pnlChassis.setLayout(pnlChassisLayout);
        pnlChassisLayout.setHorizontalGroup(
            pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(pnlChassisLayout.createSequentialGroup()
                    .addComponent(jLabel7)
                    .addGap(2, 2, 2)
                    .addComponent(cmbMotiveType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlChassisLayout.createSequentialGroup()
                    .addGap(65, 65, 65)
                    .addComponent(chkOmniVee))
                .addGroup(pnlChassisLayout.createSequentialGroup()
                    .addGap(65, 65, 65)
                    .addComponent(chkTrailer))
                .addGroup(pnlChassisLayout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addGroup(pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(pnlChassisLayout.createSequentialGroup()
                            .addComponent(jLabel32)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbTurret, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spnTurretTonnage))
                        .addGroup(pnlChassisLayout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addGap(2, 2, 2)
                            .addComponent(cmbEngineType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(pnlChassisLayout.createSequentialGroup()
                    .addGap(17, 17, 17)
                    .addComponent(jLabel8)
                    .addGap(2, 2, 2)
                    .addComponent(spnTonnage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblVeeClass)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblVeeLimits)))
            .addGroup(pnlChassisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel91)
                .addGap(2, 2, 2)
                .addComponent(spnHeatSinks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlChassisLayout.setVerticalGroup(
            pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChassisLayout.createSequentialGroup()
                .addGroup(pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlChassisLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel7))
                    .addComponent(cmbMotiveType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnTonnage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlChassisLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblVeeClass)
                                .addComponent(lblVeeLimits)))))
                .addComponent(chkOmniVee)
                .addGap(0, 0, 0)
                .addComponent(chkTrailer)
                .addGap(2, 2, 2)
                .addGroup(pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlChassisLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel9))
                    .addComponent(cmbEngineType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(cmbTurret, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnTurretTonnage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlChassisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlChassisLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel91))
                    .addComponent(spnHeatSinks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlMovement.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Movement"));
        pnlMovement.setNextFocusableComponent(pnlChassisMods);

        jLabel10.setText("Cruise MP:");

        spnCruiseMP.setModel(new javax.swing.SpinnerNumberModel(1, 0, null, 1));
        spnCruiseMP.setMinimumSize(new Dimension(45, 20));
        spnCruiseMP.setPreferredSize(new Dimension(45, 20));
        spnCruiseMP.addChangeListener(this::spnCruiseMPStateChanged);
        spnCruiseMP.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                spnCruiseMPInputMethodTextChanged(evt);
            }
        });

        jLabel11.setText("Flank MP:");

        lblFlankMP.setText("2");

        jLabel13.setText("Jump MP:");

        spnJumpMP.setEnabled(false);
        spnJumpMP.setMinimumSize(new Dimension(45, 20));
        spnJumpMP.setNextFocusableComponent(chkFlotationHull);
        spnJumpMP.setPreferredSize(new Dimension(45, 20));

        javax.swing.GroupLayout pnlMovementLayout = new javax.swing.GroupLayout(pnlMovement);
        pnlMovement.setLayout(pnlMovementLayout);

        pnlMovementLayout.setHorizontalGroup(
            pnlMovementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMovementLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(pnlMovementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMovementLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(2, 2, 2)
                        .addComponent(spnCruiseMP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addComponent(lblMoveSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlMovementLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(pnlMovementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlMovementLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(lblFlankMP))
                            .addGroup(pnlMovementLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(2, 2, 2)
                                .addComponent(spnJumpMP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        pnlMovementLayout.setVerticalGroup(
            pnlMovementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMovementLayout.createSequentialGroup()
                .addGroup(pnlMovementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMovementLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel10))
                        .addComponent(spnCruiseMP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblMoveSummary, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE))
                .addGap(2, 2, 2)
                .addGroup(pnlMovementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lblFlankMP))
                .addGap(2, 2, 2)
                .addGroup(pnlMovementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMovementLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel13))
                    .addComponent(spnJumpMP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlChassisMods.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Chassis Modifications"));
        pnlChassisMods.setLayout(new java.awt.GridBagLayout());

        chkFlotationHull.setText("Flotation Hull");
        chkFlotationHull.setEnabled(false);
        chkFlotationHull.addActionListener(this::chkFlotationHullActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlChassisMods.add(chkFlotationHull, gridBagConstraints);

        lblMoveSummary.setText("W/R: 12/20");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        pnlMovement.add(lblMoveSummary, gridBagConstraints);

        chkLimitedAmph.setText("Limited Amphibious");
        chkLimitedAmph.setEnabled(false);
        chkLimitedAmph.addActionListener(this::chkLimitedAmphActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlChassisMods.add(chkLimitedAmph, gridBagConstraints);

        chkFullAmph.setText("Fully Amphibious");
        chkFullAmph.setEnabled(false);
        chkFullAmph.addActionListener(this::chkFullAmphActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlChassisMods.add(chkFullAmph, gridBagConstraints);

        chkDuneBuggy.setText("Dune Buggy");
        chkDuneBuggy.setEnabled(false);
        chkDuneBuggy.addActionListener(this::chkDuneBuggyActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlChassisMods.add(chkDuneBuggy, gridBagConstraints);

        chkEnviroSealing.setText("Enviro (Vacuum) Sealing");
        chkEnviroSealing.setEnabled(false);
        chkEnviroSealing.setNextFocusableComponent(chkArmoredMotive);
        chkEnviroSealing.addActionListener(this::chkEnviroSealingActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlChassisMods.add(chkEnviroSealing, gridBagConstraints);

        pnlExperimental.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Experimental Equipment"));

        chkArmoredMotive.setText("Armored Motive System");
        chkArmoredMotive.setEnabled(false);

        chkCommandConsole.setText("Command Console");
        chkCommandConsole.setEnabled(false);

        chkEscapePod.setText("Combat Vehicle Escape Pod");
        chkEscapePod.setEnabled(false);
        chkEscapePod.setNextFocusableComponent(chkFractional);
        chkEscapePod.addActionListener(this::chkEscapePodActionPerformed);

        chkMinesweeper.setText("Minesweeper");
        chkMinesweeper.setEnabled(false);

        chkJetBooster.setText("VTOL Jet Booster");
        chkJetBooster.setEnabled(false);
        chkJetBooster.addActionListener(this::chkJetBoosterActionPerformed);
        chkSupercharger.setText("Supercharger");
        chkSupercharger.setEnabled(false);
        chkSupercharger.addActionListener(this::chkSuperchargerActionPerformed);

        chkSponsonTurret.setText("Sponson Turret");
        chkSponsonTurret.setEnabled(false);
        chkSponsonTurret.setNextFocusableComponent(chkFractional);
        chkSponsonTurret.addActionListener(this::chkSponsonTurretActionPerformed);

        javax.swing.GroupLayout pnlExperimentalLayout = new javax.swing.GroupLayout(pnlExperimental);
        pnlExperimental.setLayout(pnlExperimentalLayout);
        pnlExperimentalLayout.setHorizontalGroup(
            pnlExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlExperimentalLayout.createSequentialGroup()
                .addGroup(pnlExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkArmoredMotive)
                    .addComponent(chkSupercharger)
                    .addComponent(chkJetBooster)
                    .addComponent(chkMinesweeper)
                    .addComponent(chkCommandConsole)
                    .addComponent(chkEscapePod)
                    .addComponent(chkSponsonTurret))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlExperimentalLayout.setVerticalGroup(
            pnlExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlExperimentalLayout.createSequentialGroup()
                .addComponent(chkArmoredMotive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSupercharger)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCommandConsole)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkMinesweeper)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkJetBooster)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEscapePod)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSponsonTurret)
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Construction Options"));
        jPanel11.setLayout(new java.awt.GridBagLayout());

        chkFractional.setText("Use Fractional Accounting");
        chkFractional.setEnabled(false);
        chkFractional.setNextFocusableComponent(txtVehicleName);
        chkFractional.addActionListener(this::chkFractionalActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        jPanel11.add(chkFractional, gridBagConstraints);

        pnlSummary.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Summary"));
        pnlSummary.setLayout(new java.awt.GridBagLayout());

        jLabel12.setText("Item");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel12, gridBagConstraints);

        jLabel14.setText("Tonnage");
        pnlSummary.add(jLabel14, new GridBagConstraints());

        jLabel15.setText("Space");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlSummary.add(jLabel15, gridBagConstraints);

        jLabel16.setText("Availability");
        pnlSummary.add(jLabel16, new GridBagConstraints());

        jLabel17.setText("Internal Structure:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel17, gridBagConstraints);

        txtSumIntTons.setEditable(false);
        txtSumIntTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumIntTons.setText("000.00");
        txtSumIntTons.setMinimumSize(new Dimension(50, 20));
        txtSumIntTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumIntTons, gridBagConstraints);

        txtSumIntAV.setEditable(false);
        txtSumIntAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumIntAV.setText("X/X-X-X");
        txtSumIntAV.setMinimumSize(new Dimension(65, 20));
        txtSumIntAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumIntAV, gridBagConstraints);

        jLabel18.setText("Engine:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel18, gridBagConstraints);

        txtSumEngTons.setEditable(false);
        txtSumEngTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumEngTons.setText("000.00");
        txtSumEngTons.setMinimumSize(new Dimension(50, 20));
        txtSumEngTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumEngTons, gridBagConstraints);

        txtSumEngAV.setEditable(false);
        txtSumEngAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumEngAV.setText("X/X-X-X");
        txtSumEngAV.setMinimumSize(new Dimension(65, 20));
        txtSumEngAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumEngAV, gridBagConstraints);

        jLabel19.setText("Lift/Dive/Rotor:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel19, gridBagConstraints);

        txtSumLifTons.setEditable(false);
        txtSumLifTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumLifTons.setText("000.00");
        txtSumLifTons.setMinimumSize(new Dimension(50, 20));
        txtSumLifTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumLifTons, gridBagConstraints);

        txtSumLifAV.setEditable(false);
        txtSumLifAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumLifAV.setText("X/X-X-X");
        txtSumLifAV.setMinimumSize(new Dimension(65, 20));
        txtSumLifAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumLifAV, gridBagConstraints);

        txtSumEngSpace.setEditable(false);
        txtSumEngSpace.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumEngSpace.setText("00");
        txtSumEngSpace.setMinimumSize(new Dimension(40, 20));
        txtSumEngSpace.setPreferredSize(new Dimension(40, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlSummary.add(txtSumEngSpace, gridBagConstraints);

        jLabel20.setText("Controls:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel20, gridBagConstraints);

        txtSumConTons.setEditable(false);
        txtSumConTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumConTons.setText("000.00");
        txtSumConTons.setMinimumSize(new Dimension(50, 20));
        txtSumConTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumConTons, gridBagConstraints);

        txtSumConAV.setEditable(false);
        txtSumConAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumConAV.setText("X/X-X-X");
        txtSumConAV.setMinimumSize(new Dimension(65, 20));
        txtSumConAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumConAV, gridBagConstraints);

        jLabel21.setText("Jump Jets:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel21, gridBagConstraints);

        txtSumJJTons.setEditable(false);
        txtSumJJTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumJJTons.setText("000.00");
        txtSumJJTons.setMinimumSize(new Dimension(50, 20));
        txtSumJJTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumJJTons, gridBagConstraints);

        txtSumJJSpace.setEditable(false);
        txtSumJJSpace.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumJJSpace.setText("00");
        txtSumJJSpace.setMinimumSize(new Dimension(40, 20));
        txtSumJJSpace.setPreferredSize(new Dimension(40, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlSummary.add(txtSumJJSpace, gridBagConstraints);

        txtSumJJAV.setEditable(false);
        txtSumJJAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumJJAV.setText("X/X-X-X");
        txtSumJJAV.setMinimumSize(new Dimension(65, 20));
        txtSumJJAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumJJAV, gridBagConstraints);

        jLabel22.setText("Heat Sinks:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel22, gridBagConstraints);

        txtSumHSTons.setEditable(false);
        txtSumHSTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumHSTons.setText("000.00");
        txtSumHSTons.setMinimumSize(new Dimension(50, 20));
        txtSumHSTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumHSTons, gridBagConstraints);

        txtSumHSAV.setEditable(false);
        txtSumHSAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumHSAV.setText("X/X-X-X");
        txtSumHSAV.setMinimumSize(new Dimension(65, 20));
        txtSumHSAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumHSAV, gridBagConstraints);

        jLabel23.setText("Armor:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel23, gridBagConstraints);

        txtSumArmTons.setEditable(false);
        txtSumArmTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumArmTons.setText("000.00");
        txtSumArmTons.setMinimumSize(new Dimension(50, 20));
        txtSumArmTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumArmTons, gridBagConstraints);

        txtSumArmSpace.setEditable(false);
        txtSumArmSpace.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumArmSpace.setText("00");
        txtSumArmSpace.setMinimumSize(new Dimension(40, 20));
        txtSumArmSpace.setPreferredSize(new Dimension(40, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlSummary.add(txtSumArmSpace, gridBagConstraints);

        txtSumArmAV.setEditable(false);
        txtSumArmAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumArmAV.setText("X/X-X-X");
        txtSumArmAV.setMinimumSize(new Dimension(65, 20));
        txtSumArmAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumArmAV, gridBagConstraints);

        jLabel24.setText("Turret:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel24, gridBagConstraints);

        txtSumTurTons.setEditable(false);
        txtSumTurTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumTurTons.setText("000.00");
        txtSumTurTons.setMinimumSize(new Dimension(50, 20));
        txtSumTurTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumTurTons, gridBagConstraints);

        txtSumTurAV.setEditable(false);
        txtSumTurAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumTurAV.setText("X/X-X-X");
        txtSumTurAV.setMinimumSize(new Dimension(65, 20));
        txtSumTurAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumTurAV, gridBagConstraints);

        jLabel25.setText("Rear Turret:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel25, gridBagConstraints);

        txtSumRTuTons.setEditable(false);
        txtSumRTuTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumRTuTons.setText("000.00");
        txtSumRTuTons.setMinimumSize(new Dimension(50, 20));
        txtSumRTuTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumRTuTons, gridBagConstraints);

        txtSumRTuAV.setEditable(false);
        txtSumRTuAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumRTuAV.setText("X/X-X-X");
        txtSumRTuAV.setMinimumSize(new Dimension(65, 20));
        txtSumRTuAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumRTuAV, gridBagConstraints);

        jLabel26.setText("Sponsons:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel26, gridBagConstraints);

        txtSumSpnTons.setEditable(false);
        txtSumSpnTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumSpnTons.setText("000.00");
        txtSumSpnTons.setMinimumSize(new Dimension(50, 20));
        txtSumSpnTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumSpnTons, gridBagConstraints);

        txtSumSpnAV.setEditable(false);
        txtSumSpnAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumSpnAV.setText("X/X-X-X");
        txtSumSpnAV.setMinimumSize(new Dimension(65, 20));
        txtSumSpnAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumSpnAV, gridBagConstraints);

        jLabel27.setText("Power Amplifiers:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pnlSummary.add(jLabel27, gridBagConstraints);

        txtSumPATons.setEditable(false);
        txtSumPATons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumPATons.setText("000.00");
        txtSumPATons.setMinimumSize(new Dimension(50, 20));
        txtSumPATons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlSummary.add(txtSumPATons, gridBagConstraints);

        txtSumPAAV.setEditable(false);
        txtSumPAAV.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSumPAAV.setText("X/X-X-X");
        txtSumPAAV.setMinimumSize(new Dimension(65, 20));
        txtSumPAAV.setPreferredSize(new Dimension(65, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlSummary.add(txtSumPAAV, gridBagConstraints);

        pnlInformation.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Information"));

        jLabel29.setText("Suspension Factor:");

        lblSupensionFacter.setText("000");

        jLabel31.setText("Minimum Engine Tonnage:");

        lblMinEngineTons.setText("10");

        jLabel33.setText("Base Engine Rating:");

        lblBaseEngineRating.setText("000");

        jLabel35.setText("Final Engine Rating:");

        lblFinalEngineRating.setText("000");

        jLabel28.setText("Free Heat Sinks:");

        lblFreeHeatSinks.setText("000");

        jLabel30.setText("Crew:");

        lblNumCrew.setText("00");

        javax.swing.GroupLayout pnlInformationLayout = new javax.swing.GroupLayout(pnlInformation);
        pnlInformation.setLayout(pnlInformationLayout);
        pnlInformationLayout.setHorizontalGroup(
            pnlInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
            .addGroup(pnlInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(jLabel30)
                        .addGap(4, 4, 4)
                        .addComponent(lblNumCrew))
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel29)
                        .addGap(4, 4, 4)
                        .addComponent(lblSupensionFacter))
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addGap(4, 4, 4)
                        .addComponent(lblMinEngineTons))
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel33)
                        .addGap(4, 4, 4)
                        .addComponent(lblBaseEngineRating))
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel35)
                        .addGap(4, 4, 4)
                        .addComponent(lblFinalEngineRating))
                    .addGroup(pnlInformationLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel28)
                        .addGap(4, 4, 4)
                        .addComponent(lblFreeHeatSinks)))
                .addContainerGap(95, Short.MAX_VALUE))
        );
        pnlInformationLayout.setVerticalGroup(
            pnlInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInformationLayout.createSequentialGroup()
                .addGroup(pnlInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addComponent(lblNumCrew))
                .addGap(2, 2, 2)
                .addGroup(pnlInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addComponent(lblSupensionFacter))
                .addGap(2, 2, 2)
                .addGroup(pnlInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31)
                    .addComponent(lblMinEngineTons))
                .addGap(2, 2, 2)
                .addGroup(pnlInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33)
                    .addComponent(lblBaseEngineRating))
                .addGap(2, 2, 2)
                .addGroup(pnlInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35)
                    .addComponent(lblFinalEngineRating))
                .addGap(2, 2, 2)
                .addGroup(pnlInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28)
                    .addComponent(lblFreeHeatSinks)))
        );

        pnlOmniInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Omni Configuration"));
        pnlOmniInfo.setLayout(new java.awt.GridBagLayout());

        btnLockChassis.setText("Lock Chassis");
        btnLockChassis.setEnabled(false);
        btnLockChassis.setMaximumSize(new Dimension(200, 23));
        btnLockChassis.setMinimumSize(new Dimension(105, 23));
        btnLockChassis.setPreferredSize(new Dimension(120, 23));
        btnLockChassis.addActionListener(this::btnLockChassisActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        pnlOmniInfo.add(btnLockChassis, gridBagConstraints);

        btnAddVariant.setText("Add Variant");
        btnAddVariant.setEnabled(false);
        btnAddVariant.setMaximumSize(new Dimension(200, 23));
        btnAddVariant.setMinimumSize(new Dimension(80, 23));
        btnAddVariant.setPreferredSize(new Dimension(120, 23));
        btnAddVariant.addActionListener(this::btnAddVariantActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        pnlOmniInfo.add(btnAddVariant, gridBagConstraints);

        btnDeleteVariant.setText("Delete Variant");
        btnDeleteVariant.setEnabled(false);
        btnDeleteVariant.setMaximumSize(new Dimension(200, 23));
        btnDeleteVariant.setMinimumSize(new Dimension(80, 23));
        btnDeleteVariant.setPreferredSize(new Dimension(120, 23));
        btnDeleteVariant.addActionListener(this::btnDeleteVariantActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        pnlOmniInfo.add(btnDeleteVariant, gridBagConstraints);

        btnRenameVariant.setText("Rename Variant");
        btnRenameVariant.setEnabled(false);
        btnRenameVariant.setMinimumSize(new Dimension(80, 23));
        btnRenameVariant.setPreferredSize(new Dimension(120, 23));
        btnRenameVariant.addActionListener(this::btnRenameVariantActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        pnlOmniInfo.add(btnRenameVariant, gridBagConstraints);

        javax.swing.GroupLayout pnlBasicSetupLayout = new javax.swing.GroupLayout(pnlBasicSetup);
        pnlBasicSetup.setLayout(pnlBasicSetupLayout);
        pnlBasicSetupLayout.setHorizontalGroup(
            pnlBasicSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
            .addGroup(pnlBasicSetupLayout.createSequentialGroup()
                .addGroup(pnlBasicSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
                    .addComponent(pnlChassisMods, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlMovement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlBasicInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBasicSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
                    .addComponent(pnlExperimental, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlChassis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBasicSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
                    .addComponent(pnlSummary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlOmniInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlInformation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        pnlBasicSetupLayout.setVerticalGroup(
            pnlBasicSetupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
            .addGroup(pnlBasicSetupLayout.createSequentialGroup()
                .addGroup(pnlBasicSetupLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
                    .addGroup(pnlBasicSetupLayout.createSequentialGroup()
                        .addComponent(pnlChassis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlExperimental, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlBasicSetupLayout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
                        .addGroup(pnlBasicSetupLayout.createSequentialGroup()
                            .addComponent(pnlOmniInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlBasicSetupLayout.createSequentialGroup()
                            .addComponent(pnlBasicInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlMovement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlChassisMods, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        tbpMainTabPane.addTab("Basic Setup", pnlBasicSetup);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Armor Locations"));

        pnlRightArmor.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Right"));
        pnlRightArmor.setLayout(new java.awt.GridBagLayout());

        lblRightIntPts.setHorizontalAlignment(SwingConstants.CENTER);
        lblRightIntPts.setText("00");
        lblRightIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRightIntPts.setMaximumSize(new Dimension(45, 20));
        lblRightIntPts.setMinimumSize(new Dimension(45, 20));
        lblRightIntPts.setPreferredSize(new Dimension(45, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlRightArmor.add(lblRightIntPts, gridBagConstraints);

        jLabel40.setText("Internal");
        pnlRightArmor.add(jLabel40, new GridBagConstraints());

        jLabel46.setText("Armor");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlRightArmor.add(jLabel46, gridBagConstraints);

        spnRightArmor.setMinimumSize(new Dimension(45, 20));
        spnRightArmor.setPreferredSize(new Dimension(45, 20));
        spnRightArmor.addChangeListener(this::spnRightArmorStateChanged);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlRightArmor.add(spnRightArmor, gridBagConstraints);

        pnlFrontArmor.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Front"));
        pnlFrontArmor.setLayout(new java.awt.GridBagLayout());

        lblFrontIntPts.setHorizontalAlignment(SwingConstants.CENTER);
        lblFrontIntPts.setText("00");
        lblFrontIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblFrontIntPts.setMaximumSize(new Dimension(45, 20));
        lblFrontIntPts.setMinimumSize(new Dimension(45, 20));
        lblFrontIntPts.setPreferredSize(new Dimension(45, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlFrontArmor.add(lblFrontIntPts, gridBagConstraints);

        jLabel45.setText("Internal");
        pnlFrontArmor.add(jLabel45, new GridBagConstraints());

        jLabel47.setText("Armor");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlFrontArmor.add(jLabel47, gridBagConstraints);

        spnFrontArmor.setMinimumSize(new Dimension(45, 20));
        spnFrontArmor.setPreferredSize(new Dimension(45, 20));
        spnFrontArmor.addChangeListener(this::spnFrontArmorStateChanged);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlFrontArmor.add(spnFrontArmor, gridBagConstraints);

        pnlLeftArmor.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Left"));
        pnlLeftArmor.setLayout(new java.awt.GridBagLayout());

        lblLeftIntPts.setHorizontalAlignment(SwingConstants.CENTER);
        lblLeftIntPts.setText("00");
        lblLeftIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLeftIntPts.setMaximumSize(new Dimension(45, 20));
        lblLeftIntPts.setMinimumSize(new Dimension(45, 20));
        lblLeftIntPts.setPreferredSize(new Dimension(45, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlLeftArmor.add(lblLeftIntPts, gridBagConstraints);

        jLabel41.setText("Internal");
        pnlLeftArmor.add(jLabel41, new GridBagConstraints());

        jLabel48.setText("Armor");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlLeftArmor.add(jLabel48, gridBagConstraints);

        spnLeftArmor.setMinimumSize(new Dimension(45, 20));
        spnLeftArmor.setPreferredSize(new Dimension(45, 20));
        spnLeftArmor.addChangeListener(this::spnLeftArmorStateChanged);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlLeftArmor.add(spnLeftArmor, gridBagConstraints);

        pnlRearArmor.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Rear"));
        pnlRearArmor.setLayout(new java.awt.GridBagLayout());

        lblRearIntPts.setHorizontalAlignment(SwingConstants.CENTER);
        lblRearIntPts.setText("00");
        lblRearIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRearIntPts.setMaximumSize(new Dimension(45, 20));
        lblRearIntPts.setMinimumSize(new Dimension(45, 20));
        lblRearIntPts.setPreferredSize(new Dimension(45, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlRearArmor.add(lblRearIntPts, gridBagConstraints);

        jLabel44.setText("Internal");
        pnlRearArmor.add(jLabel44, new GridBagConstraints());

        jLabel49.setText("Armor");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlRearArmor.add(jLabel49, gridBagConstraints);

        spnRearArmor.setMinimumSize(new Dimension(45, 20));
        spnRearArmor.setPreferredSize(new Dimension(45, 20));
        spnRearArmor.addChangeListener(this::spnRearArmorStateChanged);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlRearArmor.add(spnRearArmor, gridBagConstraints);

        pnlTurretArmor.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Turret"));
        pnlTurretArmor.setLayout(new java.awt.GridBagLayout());

        lblTurretIntPts.setHorizontalAlignment(SwingConstants.CENTER);
        lblTurretIntPts.setText("00");
        lblTurretIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTurretIntPts.setMaximumSize(new Dimension(45, 20));
        lblTurretIntPts.setMinimumSize(new Dimension(45, 20));
        lblTurretIntPts.setPreferredSize(new Dimension(45, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlTurretArmor.add(lblTurretIntPts, gridBagConstraints);

        jLabel42.setText("Internal");
        pnlTurretArmor.add(jLabel42, new GridBagConstraints());

        jLabel50.setText("Armor");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlTurretArmor.add(jLabel50, gridBagConstraints);

        spnTurretArmor.setMinimumSize(new Dimension(45, 20));
        spnTurretArmor.setPreferredSize(new Dimension(45, 20));
        spnTurretArmor.addChangeListener(this::spnTurretArmorStateChanged);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlTurretArmor.add(spnTurretArmor, gridBagConstraints);

        pnlRearTurretArmor.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "R Turret"));
        pnlRearTurretArmor.setEnabled(false);
        pnlRearTurretArmor.setLayout(new java.awt.GridBagLayout());

        lblRearTurretIntPts.setHorizontalAlignment(SwingConstants.CENTER);
        lblRearTurretIntPts.setText("00");
        lblRearTurretIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRearTurretIntPts.setMaximumSize(new Dimension(45, 20));
        lblRearTurretIntPts.setMinimumSize(new Dimension(45, 20));
        lblRearTurretIntPts.setPreferredSize(new Dimension(45, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlRearTurretArmor.add(lblRearTurretIntPts, gridBagConstraints);

        jLabel43.setText("Internal");
        pnlRearTurretArmor.add(jLabel43, new GridBagConstraints());

        jLabel51.setText("Armor");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlRearTurretArmor.add(jLabel51, gridBagConstraints);

        spnRearTurretArmor.setMinimumSize(new Dimension(45, 20));
        spnRearTurretArmor.setPreferredSize(new Dimension(45, 20));
        spnRearTurretArmor.addChangeListener(this::spnRearTurretArmorStateChanged);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlRearTurretArmor.add(spnRearTurretArmor, gridBagConstraints);

        pnlRotorArmor.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Rotor"));
        pnlRotorArmor.setEnabled(false);
        pnlRotorArmor.setLayout(new java.awt.GridBagLayout());

        lblRotorIntPts.setHorizontalAlignment(SwingConstants.CENTER);
        lblRotorIntPts.setText("00");
        lblRotorIntPts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRotorIntPts.setMaximumSize(new Dimension(45, 20));
        lblRotorIntPts.setMinimumSize(new Dimension(45, 20));
        lblRotorIntPts.setPreferredSize(new Dimension(45, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlRotorArmor.add(lblRotorIntPts, gridBagConstraints);

        jLabel92.setText("Internal");
        pnlRotorArmor.add(jLabel92, new GridBagConstraints());

        jLabel93.setText("Armor");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlRotorArmor.add(jLabel93, gridBagConstraints);

        spnRotorArmor.setMinimumSize(new Dimension(45, 20));
        spnRotorArmor.setPreferredSize(new Dimension(45, 20));
        spnRotorArmor.addChangeListener(this::spnRotorArmorStateChanged);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlRotorArmor.add(spnRotorArmor, gridBagConstraints);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlLeftArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(pnlFrontArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlRearTurretArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlRearArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlTurretArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlRotorArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 54, Short.MAX_VALUE)
                .addComponent(pnlRightArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(pnlFrontArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(pnlTurretArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(pnlRotorArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlLeftArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlRightArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(pnlRearTurretArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlRearArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlArmorType.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Armor Type"));

        jLabel52.setText("Armor Type:");

        cmbArmorType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard", "Industrial", "Commercial", "Ferro-Fibrous", "Light Ferro-Fibrous", "Heavy Ferro-Fibrous", "Vehicular Stealth" }));
        cmbArmorType.setMinimumSize(new Dimension(150, 20));
        cmbArmorType.setPreferredSize(new Dimension(150, 20));
        cmbArmorType.addActionListener(this::cmbArmorTypeActionPerformed);

        chkBalanceLRArmor.setSelected(true);
        chkBalanceLRArmor.setText("Balance Left/Right Armor");
        chkBalanceFRArmor.setText("Balance Front/Rear Armor");

        btnSetArmorTons.setText("Set Armor Tonnage");
        btnSetArmorTons.addActionListener(this::btnSetArmorTonsActionPerformed);

        btnUseRemaining.setText("Use Remaining Tonnage");
        btnUseRemaining.addActionListener(this::btnUseRemainingActionPerformed);

        btnMaximize.setText("Maximize Armor");
        btnMaximize.addActionListener(this::btnMaximizeActionPerformed);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(pnlArmorType);
        pnlArmorType.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel52)
                        .addGap(2, 2, 2)
                        .addComponent(cmbArmorType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnSetArmorTons, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnUseRemaining, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnMaximize, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkBalanceLRArmor)
                            .addComponent(chkBalanceFRArmor))))
                .addGap(10, 10, 10))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel52))
                    .addComponent(cmbArmorType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBalanceLRArmor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkBalanceFRArmor)
                .addGap(0, 0, 0)
                .addComponent(btnSetArmorTons)
                .addGap(0, 0, 0)
                .addComponent(btnUseRemaining)
                .addGap(0, 0, 0)
                .addComponent(btnMaximize))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Armor Information"));
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel34.setText("Tons");
        jPanel8.add(jLabel34, new GridBagConstraints());

        jLabel36.setText("Space");
        jPanel8.add(jLabel36, new GridBagConstraints());

        lblArmorTotals.setText("999 of 999 Armor Points");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel8.add(lblArmorTotals, gridBagConstraints);

        lblArmorCoverage.setText("100.00% Coverage");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel8.add(lblArmorCoverage, gridBagConstraints);

        txtArmorTons.setEditable(false);
        txtArmorTons.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtArmorTons.setText("000.00");
        txtArmorTons.setMinimumSize(new Dimension(50, 20));
        txtArmorTons.setPreferredSize(new Dimension(50, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel8.add(txtArmorTons, gridBagConstraints);

        txtArmorSpace.setEditable(false);
        txtArmorSpace.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtArmorSpace.setText("00");
        txtArmorSpace.setMinimumSize(new Dimension(40, 20));
        txtArmorSpace.setPreferredSize(new Dimension(40, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel8.add(txtArmorSpace, gridBagConstraints);

        lblArmorTonsWasted.setText("0.00 Tons Wasted");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel8.add(lblArmorTonsWasted, gridBagConstraints);

        lblArmorLeftInLot.setText("99 Points Left In This 1/2 Ton Lot");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel8.add(lblArmorLeftInLot, gridBagConstraints);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlArmorType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(214, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(pnlArmorType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(199, 199, 199))))
        );

        tbpMainTabPane.addTab("Armor", jPanel2);

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

        tbpWeaponChooser.setTabPlacement(JTabbedPane.RIGHT);
        tbpWeaponChooser.setMaximumSize(new Dimension(300, 300));
        tbpWeaponChooser.setMinimumSize(new Dimension(300, 300));
        tbpWeaponChooser.addTab("Ballistic", EquipmentLocation(lstChooseBallistic, this::lstChooseBallisticValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Energy", EquipmentLocation(lstChooseEnergy, this::lstChooseEnergyValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Missile", EquipmentLocation(lstChooseMissile, this::lstChooseMissileValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Physical", EquipmentLocation(lstChoosePhysical, this::lstChoosePhysicalValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Equipment", EquipmentLocation(lstChooseEquipment, this::lstChooseEquipmentValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Artillery", EquipmentLocation(lstChooseArtillery, this::lstChooseArtilleryValueChanged, mlAddEquip, placeholder));
        tbpWeaponChooser.addTab("Ammunition", EquipmentLocation(lstChooseAmmunition, this::lstChooseAmmunitionValueChanged, mlAddEquip, placeholder));

        pnlSpecials.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Specials"));
        pnlSpecials.setLayout(new java.awt.GridBagLayout());

        jLabel37.setText("Missile Guidance:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlSpecials.add(jLabel37, gridBagConstraints);

        chkUseTC.setText("Targeting Computer");
        chkUseTC.setEnabled(false);
        chkUseTC.addActionListener(this::chkUseTCActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        pnlSpecials.add(chkUseTC, gridBagConstraints);

        chkFCSAIV.setText("Use Artemis IV");
        chkFCSAIV.addActionListener(this::chkFCSAIVActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlSpecials.add(chkFCSAIV, gridBagConstraints);

        chkFCSAV.setText("Use Artemis V");
        chkFCSAV.setEnabled(false);
        chkFCSAV.addActionListener(this::chkFCSAVActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlSpecials.add(chkFCSAV, gridBagConstraints);

        chkFCSApollo.setText("Use MRM Apollo");
        chkFCSApollo.setEnabled(false);
        chkFCSApollo.addActionListener(this::chkFCSApolloActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlSpecials.add(chkFCSApollo, gridBagConstraints);

        chkCASE.setText("Use CASE");
        chkCASE.addActionListener(this::chkCASEActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        pnlSpecials.add(chkCASE, gridBagConstraints);

        pnlSelected.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selected Equipment"));
        pnlSelected.setMaximumSize(new Dimension(212, 286));
        pnlSelected.setMinimumSize(new Dimension(212, 286));
        pnlSelected.setLayout(new javax.swing.BoxLayout(pnlSelected, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane23.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane23.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        lstSelectedEquipment.setModel( new javax.swing.DefaultListModel()
        );
        lstSelectedEquipment.setMaximumSize(new Dimension(180, 225));
        lstSelectedEquipment.setMinimumSize(new Dimension(180, 225));
        lstSelectedEquipment.setPreferredSize(null);
        lstSelectedEquipment.setVisibleRowCount(16);
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
        jScrollPane23.setViewportView(lstSelectedEquipment);

        pnlSelected.add(jScrollPane23);

        pnlEquipInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Information"));
        pnlEquipInfo.setLayout(new java.awt.GridBagLayout());

        jLabel38.setText("Availability(AoW/SL)");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlEquipInfo.add(jLabel38, gridBagConstraints);

        jLabel39.setText("Availability (SW)");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlEquipInfo.add(jLabel39, gridBagConstraints);

        jLabel53.setText("Availability (CI)");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlEquipInfo.add(jLabel53, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoAVSL, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoAVSW, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoAVCI, gridBagConstraints);

        jLabel54.setText("Introduction");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel54, gridBagConstraints);

        jLabel55.setText("Extinction");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel55, gridBagConstraints);

        jLabel56.setText("Reintroduction");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel56, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoIntro, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoExtinct, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoReintro, gridBagConstraints);

        jLabel57.setText("Name");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        pnlEquipInfo.add(jLabel57, gridBagConstraints);

        jLabel58.setText("Type");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel58, gridBagConstraints);

        jLabel59.setText("Heat");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel59, gridBagConstraints);

        jLabel60.setText("Damage");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel60, gridBagConstraints);

        jLabel61.setText("Range");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel61, gridBagConstraints);

        lblInfoName.setText(" ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlEquipInfo.add(lblInfoName, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoType, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoHeat, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoDamage, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoRange, gridBagConstraints);

        jSeparator17.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        pnlEquipInfo.add(jSeparator17, gridBagConstraints);

        jLabel62.setText("Ammo");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel62, gridBagConstraints);

        jLabel63.setText("Tonnage");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel63, gridBagConstraints);

        jLabel64.setText("Crits");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        pnlEquipInfo.add(jLabel64, gridBagConstraints);

        jLabel65.setText("Specials");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 0);
        pnlEquipInfo.add(jLabel65, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoAmmo, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoTonnage, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoCrits, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlEquipInfo.add(lblInfoSpecials, gridBagConstraints);

        jSeparator20.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        pnlEquipInfo.add(jSeparator20, gridBagConstraints);

        jLabel66.setText("Cost");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel66, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlEquipInfo.add(lblInfoCost, gridBagConstraints);

        jLabel67.setText("BV");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel67, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlEquipInfo.add(lblInfoBV, gridBagConstraints);

        jLabel68.setText("Mounting Restrictions");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 3);
        pnlEquipInfo.add(jLabel68, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 4, 0);
        pnlEquipInfo.add(lblInfoMountRestrict, gridBagConstraints);

        jLabel69.setText("Rules Level");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(jLabel69, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlEquipInfo.add(lblInfoRulesLevel, gridBagConstraints);

        jLabel6.setText("Availability (DA)");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlEquipInfo.add(jLabel6, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlEquipInfo.add(lblInfoAVDA, gridBagConstraints);

        pnlControls.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Controls"));
        pnlControls.setLayout(new java.awt.GridBagLayout());

        btnRemoveEquip.setText("<<");
        btnRemoveEquip.addActionListener(this::btnRemoveEquipActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        pnlControls.add(btnRemoveEquip, gridBagConstraints);

        btnClearEquip.setText("Clear");
        btnClearEquip.addActionListener(this::btnClearEquipActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlControls.add(btnClearEquip, gridBagConstraints);

        btnAddEquip.setText(">>");
        btnAddEquip.addActionListener(this::btnAddEquipActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlControls.add(btnAddEquip, gridBagConstraints);

        cmbNumEquips.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 0);
        pnlControls.add(cmbNumEquips, gridBagConstraints);

        cmbLocation.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Front", "Left", "Right", "Rear", "Body", "Turret", "Rear Turret" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        cmbLocation.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        cmbLocation.setSelectedIndex(0);
        cmbLocation.setVisibleRowCount(4);
        cmbLocation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbLocationMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(cmbLocation);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        pnlControls.add(jScrollPane1, gridBagConstraints);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tbpWeaponChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlControls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlSpecials, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlEquipInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlSpecials, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlSelected, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbpWeaponChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlEquipInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tbpMainTabPane.addTab("Equipment", jPanel3);

        pnlFluff.setLayout(new java.awt.GridBagLayout());

        pnlImage.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Fluff Image", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        pnlImage.setLayout(new java.awt.GridBagLayout());

        lblFluffImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblFluffImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblFluffImage.setMaximumSize(new Dimension(325, 350));
        lblFluffImage.setMinimumSize(new Dimension(325, 350));
        lblFluffImage.setPreferredSize(new Dimension(325, 350));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        pnlImage.add(lblFluffImage, gridBagConstraints);

        btnLoadImage.setText("Load Image");
        btnLoadImage.addActionListener(this::btnLoadImageActionPerformed);
        jPanel1.add(btnLoadImage);

        btnClearImage.setText("Clear Image");
        btnClearImage.addActionListener(this::btnClearImageActionPerformed);
        jPanel1.add(btnClearImage);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 4);
        pnlImage.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        pnlFluff.add(pnlImage, gridBagConstraints);

        pnlExport.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Export", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        pnlExport.setLayout(new java.awt.GridBagLayout());

        btnExportTXT.setText("to TXT");
        btnExportTXT.addActionListener(this::btnExportTXTActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        pnlExport.add(btnExportTXT, gridBagConstraints);

        btnExportHTML.setText("to HTML");
        btnExportHTML.addActionListener(this::btnExportHTMLActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        pnlExport.add(btnExportHTML, gridBagConstraints);

        btnExportMTF.setText("to MegaMek");
        btnExportMTF.addActionListener(this::btnExportMTFActionPerformed);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlExport.add(btnExportMTF, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        pnlFluff.add(pnlExport, gridBagConstraints);

        tbpFluffEditors.setTabPlacement(JTabbedPane.RIGHT);
        tbpFluffEditors.setMaximumSize(new Dimension(420, 455));
        tbpFluffEditors.setMinimumSize(new Dimension(420, 455));

        pnlOverview.setMaximumSize(new Dimension(427, 485));
        pnlOverview.setMinimumSize(new Dimension(427, 485));
        pnlOverview.setLayout(new javax.swing.BoxLayout(pnlOverview, javax.swing.BoxLayout.Y_AXIS));
        tbpFluffEditors.addTab("Overview", pnlOverview);

        pnlCapabilities.setMaximumSize(new Dimension(427, 485));
        pnlCapabilities.setMinimumSize(new Dimension(427, 485));
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

        jLabel82.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel82.setText("Manufacturer Information");
        jLabel82.setMaximumSize(new Dimension(175, 15));
        jLabel82.setMinimumSize(new Dimension(175, 15));
        jLabel82.setPreferredSize(new Dimension(175, 15));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        pnlManufacturers.add(jLabel82, gridBagConstraints);

        jLabel83.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel83.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel83.setText("Manufacturing Company:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        pnlManufacturers.add(jLabel83, gridBagConstraints);

        jLabel84.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel84.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel84.setText("Location:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 76;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel84, gridBagConstraints);

        jLabel85.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel85.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel85.setText("Engine Manufacturer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 17;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel85, gridBagConstraints);

        jLabel86.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel86.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel86.setText("Armor Model:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 56;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel86, gridBagConstraints);

        jLabel87.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel87.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel87.setText("Chassis Model:");
        jLabel87.setEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 47;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel87, gridBagConstraints);

        jLabel88.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel88.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel88.setText("Communications System:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel88, gridBagConstraints);

        jLabel89.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel89.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel89.setText("Targeting and Tracking:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 7;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel89, gridBagConstraints);

        txtManufacturer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
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

        txtEngineManufacturer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
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

        txtArmorModel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
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

        txtChassisModel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtChassisModel.setEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
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

        txtCommSystem.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
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

        txtTNTSystem.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
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
        pnlWeaponsManufacturers.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        pnlWeaponsManufacturers.setMinimumSize(new Dimension(315, 260));
        pnlWeaponsManufacturers.setLayout(new java.awt.GridBagLayout());

        chkIndividualWeapons.setText("Assign manufacturers individually");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 0);
        pnlWeaponsManufacturers.add(chkIndividualWeapons, gridBagConstraints);

        scpWeaponManufacturers.setPreferredSize(new Dimension(452, 392));

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

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.ipadx = 280;
        gridBagConstraints.ipady = 180;
        pnlWeaponsManufacturers.add(scpWeaponManufacturers, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        pnlManufacturers.add(pnlWeaponsManufacturers, gridBagConstraints);

        txtManufacturerLocation.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
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

        jLabel90.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel90.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel90.setText("Jump Jet Model:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 44;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlManufacturers.add(jLabel90, gridBagConstraints);

        txtJJModel.setEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
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

        lblBattleMechQuirks.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblBattleMechQuirks.setText("Quirks");
        lblBattleMechQuirks.setMaximumSize(new Dimension(175, 15));
        lblBattleMechQuirks.setMinimumSize(new Dimension(175, 15));
        lblBattleMechQuirks.setPreferredSize(new Dimension(175, 15));

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

        btnAddQuirk.setText("Manage Quirk");
        btnAddQuirk.addActionListener(this::btnAddQuirkActionPerformed);

        javax.swing.GroupLayout pnlQuirksLayout = new javax.swing.GroupLayout(pnlQuirks);
        pnlQuirks.setLayout(pnlQuirksLayout);
        pnlQuirksLayout.setHorizontalGroup(
            pnlQuirksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblBattleMechQuirks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(pnlQuirksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlQuirksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQuirksLayout.createSequentialGroup()
                        .addGap(0, 230, Short.MAX_VALUE)
                        .addComponent(btnAddQuirk))
                    .addComponent(scpQuirkTable, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE))
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

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 6);
        pnlFluff.add(tbpFluffEditors, gridBagConstraints);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 777, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                    .addComponent(pnlFluff, javax.swing.GroupLayout.PREFERRED_SIZE, 767, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 478, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(2, 2, 2)
                    .addComponent(pnlFluff, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        tbpMainTabPane.addTab("Fluff", jPanel4);

        pnlBFStats.setBorder(javax.swing.BorderFactory.createTitledBorder("BattleForce Stats"));
        pnlBFStats.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel70.setText("MV");
        pnlBFStats.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        jLabel71.setText("S (+0)");
        pnlBFStats.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, -1, -1));

        jLabel72.setText("M (+2)");
        pnlBFStats.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));

        jLabel73.setText("L (+4)");
        pnlBFStats.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, -1, -1));

        jLabel74.setText("E (+6)");
        pnlBFStats.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        jLabel75.setText("Wt.");
        pnlBFStats.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 30, -1, -1));

        jLabel76.setText("OV");
        pnlBFStats.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 30, -1, -1));

        jLabel77.setText("Armor:");
        pnlBFStats.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 30, -1, -1));

        jLabel78.setText("Structure:");
        pnlBFStats.add(jLabel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, -1, -1));

        jLabel79.setText("Special Abilities:");
        pnlBFStats.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, -1, -1));

        lblBFMV.setHorizontalAlignment(SwingConstants.CENTER);
        lblBFMV.setText("0");
        pnlBFStats.add(lblBFMV, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 30, -1));

        lblBFWt.setHorizontalAlignment(SwingConstants.CENTER);
        lblBFWt.setText("1");
        pnlBFStats.add(lblBFWt, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, 30, -1));

        lblBFOV.setHorizontalAlignment(SwingConstants.CENTER);
        lblBFOV.setText("0");
        pnlBFStats.add(lblBFOV, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 50, 30, -1));

        lblBFExtreme.setHorizontalAlignment(SwingConstants.CENTER);
        lblBFExtreme.setText("0");
        pnlBFStats.add(lblBFExtreme, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, 30, -1));

        lblBFShort.setHorizontalAlignment(SwingConstants.CENTER);
        lblBFShort.setText("0");
        pnlBFStats.add(lblBFShort, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 30, -1));

        lblBFMedium.setHorizontalAlignment(SwingConstants.CENTER);
        lblBFMedium.setText("0");
        pnlBFStats.add(lblBFMedium, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 30, -1));

        lblBFLong.setHorizontalAlignment(SwingConstants.CENTER);
        lblBFLong.setText("0");
        pnlBFStats.add(lblBFLong, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 30, -1));

        lblBFArmor.setHorizontalAlignment(SwingConstants.CENTER);
        lblBFArmor.setText("0");
        pnlBFStats.add(lblBFArmor, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, 30, -1));

        lblBFStructure.setHorizontalAlignment(SwingConstants.CENTER);
        lblBFStructure.setText("0");
        pnlBFStats.add(lblBFStructure, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 60, 30, -1));

        lblBFSA.setText("Placeholder");
        pnlBFStats.add(lblBFSA, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 430, 20));

        jLabel80.setText("Points:");
        pnlBFStats.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 30, -1, -1));

        lblBFPoints.setText("0");
        pnlBFStats.add(lblBFPoints, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 30, -1, -1));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Conversion Steps"));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextAreaBFConversion.setColumns(60);
        jTextAreaBFConversion.setEditable(false);
        jTextAreaBFConversion.setRows(5);
        jScrollPane14.setViewportView(jTextAreaBFConversion);

        jPanel10.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 740, 190));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
                    .addComponent(pnlBFStats, javax.swing.GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                    .addContainerGap()
                .addComponent(pnlBFStats, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                    .addContainerGap()
                .addComponent(jPanel10, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE))
        );

        tbpMainTabPane.addTab("BattleForce", jPanel9);

        mnuFile.setText("File");
        mnuFile.addActionListener(this::mnuFileActionPerformed);

        mnuNewMech.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK));
        mnuNewMech.setText("New");
        mnuNewMech.addActionListener(this::mnuNewMechActionPerformed);
        mnuFile.add(mnuNewMech);

        mnuLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        mnuLoad.setText("Load");
        mnuLoad.addActionListener(this::mnuLoadActionPerformed);
        mnuFile.add(mnuLoad);

        mnuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK));
        mnuOpen.setText("Open");
        mnuOpen.addActionListener(this::mnuOpenActionPerformed);
        mnuFile.add(mnuOpen);

        mnuImport.setText("Import...");

        mnuImportHMP.setText("from Heavy Metal Pro (HMP)");
        mnuImportHMP.addActionListener(this::mnuImportHMPActionPerformed);
        mnuImport.add(mnuImportHMP);

        mnuBatchHMP.setText("Batch Import HMP Files");
        mnuBatchHMP.addActionListener(this::mnuBatchHMPActionPerformed);
        mnuImport.add(mnuBatchHMP);

        mnuFile.add(mnuImport);
        mnuFile.add(jSeparator22);

        mnuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        mnuSave.setText("Save");
        mnuSave.addActionListener(this::mnuSaveActionPerformed);
        mnuFile.add(mnuSave);

        mnuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuSaveAs.setText("Save As...");
        mnuSaveAs.addActionListener(this::mnuSaveAsActionPerformed);
        mnuFile.add(mnuSaveAs);

        mnuExport.setText("Export As...");

        mnuExportHTML.setText("to HTML (Web)");
        mnuExportHTML.addActionListener(this::mnuExportHTMLActionPerformed);
        mnuExport.add(mnuExportHTML);

        mnuExportMTF.setText("to MTF (MegaMek)");
        mnuExportMTF.addActionListener(this::mnuExportMTFActionPerformed);
        mnuExport.add(mnuExportMTF);

        mnuExportTXT.setText("to TXT (Text)");
        mnuExportTXT.addActionListener(this::mnuExportTXTActionPerformed);
        mnuExport.add(mnuExportTXT);

        mnuExportClipboard.setText("to Clipboard (Text)");
        mnuExportClipboard.addActionListener(this::mnuExportClipboardActionPerformed);
        mnuExport.add(mnuExportClipboard);

        mnuCreateTCGMech.setText("to TCG Format (Card)");
        mnuCreateTCGMech.addActionListener(this::mnuCreateTCGMechActionPerformed);
        mnuExport.add(mnuCreateTCGMech);

        mnuFile.add(mnuExport);
        mnuFile.add(jSeparator23);

        mnuPrint.setText("Print");
        mnuFile.add(mnuPrint);

        mnuPrintPreview.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintPreview.setText("Print Preview");
        mnuPrintPreview.addActionListener(this::mnuPrintPreviewActionPerformed);
        mnuFile.add(mnuPrintPreview);
        mnuFile.add(jSeparator24);

        mnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        mnuExit.setText("Exit");
        mnuExit.addActionListener(this::mnuExitActionPerformed);
        mnuFile.add(mnuExit);

        jMenuBar1.add(mnuFile);

        mnuClearFluff.setText("Tools");

        mnuSummary.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_MASK));
        mnuSummary.setText("Show Summary");
        mnuSummary.addActionListener(this::mnuSummaryActionPerformed);
        mnuClearFluff.add(mnuSummary);

        mnuCostBVBreakdown.setText("Cost/BV Breakdown");
        mnuCostBVBreakdown.addActionListener(this::mnuCostBVBreakdownActionPerformed);
        mnuClearFluff.add(mnuCostBVBreakdown);

        mnuTextTRO.setText("Show Text TRO Format");
        mnuTextTRO.addActionListener(this::mnuTextTROActionPerformed);
        mnuClearFluff.add(mnuTextTRO);
        mnuClearFluff.add(jSeparator26);

        mnuBFB.setText("Load Force Balancer");
        mnuBFB.addActionListener(this::mnuBFBActionPerformed);
        mnuClearFluff.add(mnuBFB);
        mnuClearFluff.add(jSeparator27);

        mnuOptions.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK));
        mnuOptions.setText("Preferences");
        mnuOptions.addActionListener(this::mnuOptionsActionPerformed);
        mnuClearFluff.add(mnuOptions);

        mnuViewToolbar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        mnuViewToolbar.setSelected(true);
        mnuViewToolbar.setText("View Toolbar");
        mnuViewToolbar.addActionListener(this::mnuViewToolbarActionPerformed);
        mnuClearFluff.add(mnuViewToolbar);

        mnuClearUserData.setText("Clear User Data");
        mnuClearUserData.addActionListener(this::mnuClearUserDataActionPerformed);
        mnuClearFluff.add(mnuClearUserData);
        mnuClearFluff.add(jSeparator30);

        mnuUnlock.setText("Unlock Chassis");
        mnuUnlock.setEnabled(false);
        mnuUnlock.addActionListener(this::mnuUnlockActionPerformed);
        mnuClearFluff.add(mnuUnlock);

        jMenuItem1.setText("Clear All Fluff");
        jMenuItem1.addActionListener(this::jMenuItem1ActionPerformed);
        mnuClearFluff.add(jMenuItem1);

        mnuReloadEquipment.setText("Reload Equipment");
        mnuReloadEquipment.addActionListener(this::mnuReloadEquipmentActionPerformed);
        mnuClearFluff.add(mnuReloadEquipment);

        jMenuBar1.add(mnuClearFluff);

        mnuHelp.setText("Help");

        mnuCredits.setText("Credits");
        mnuCredits.addActionListener(this::mnuCreditsActionPerformed);
        mnuHelp.add(mnuCredits);

        mnuAboutSSW.setText("About SAW");
        mnuAboutSSW.addActionListener(this::mnuAboutSSWActionPerformed);
        mnuHelp.add(mnuAboutSSW);

        jMenuBar1.add(mnuHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
            .addComponent(tlbIconBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlInfoPane, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbpMainTabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tlbIconBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbpMainTabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(pnlInfoPane, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        pnlRotorArmor.setVisible(false);

        if ( cmbTurret.getSelectedItem().toString().equals("No Turret") ) {
                spnTurretArmor.setValue(0);
                spnRearTurretArmor.setValue(0);
                pnlTurretArmor.setVisible(false);
                pnlRearTurretArmor.setVisible(false);
        } else if ( cmbTurret.getSelectedItem().toString().equals("Single Turret") ) {
                pnlTurretArmor.setVisible(true);
                spnRearTurretArmor.setValue(0);
                pnlRearTurretArmor.setVisible(false);
        } else if ( cmbTurret.getSelectedItem().toString().equals("Dual Turret") ) {
                pnlTurretArmor.setVisible(true);
                pnlRearTurretArmor.setVisible(true);
        }
        if ( CurVee.IsVTOL() )
            pnlRotorArmor.setVisible(true);
        else
            spnRotorArmor.setValue(0);
    }
    private void SolidifyVehicle() {
        // sets some of the basic vehicle information normally kept in the GUI and
        // prepares the vehicle for saving to file
        int year = 0;
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
        for (states.ifState ifState : check) {
            if (CommonTools.IsAllowed(ifState.GetAvailability(), CurVee)) {
                if ((ifState instanceof states.stEngineNone)) {
                    if (chkTrailer.isSelected())
                        list.add(BuildLookupName(ifState));
                } else
                    list.add(BuildLookupName(ifState));
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

    public void FixTonnageSpinner( int MinTonnage, int MaxTonnage ) {
        int CurVal = Integer.parseInt(spnTonnage.getValue().toString());
        if ( CurVal < MinTonnage )
            CurVal = MinTonnage;

        if ( CurVal > MaxTonnage )
            CurVal = MaxTonnage;

        spnTonnage.setModel( new javax.swing.SpinnerNumberModel(CurVal, MinTonnage, MaxTonnage, 1) );
        spnTonnageStateChanged(null);
    }

    private void cmbRulesLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRulesLevelActionPerformed
        int NewLevel = cmbRulesLevel.getSelectedIndex();
        int OldLevel = CurVee.GetLoadout().GetRulesLevel();
        int OldType = cmbMotiveType.getSelectedIndex();
        int OldTech = CurVee.GetTechbase();

        if( OldLevel == NewLevel ) {
            // we're already at the correct rules level.
            return;
        }

        // do we have an OmniVee?
        if( CurVee.IsOmni() ) {
            // see if we can set to the new rules level.
            if( CurVee.GetLoadout().SetRulesLevel( NewLevel ) ) {
                // we can.
                if( OldLevel > NewLevel ) {
                    CurVee.GetLoadout().FlushIllegal();
                    CurVee.SetChanged(true);
                }
                BuildTechBaseSelector();
                cmbTechBase.setSelectedIndex( CurVee.GetLoadout().GetTechBase() );
                RefreshEquipment();
                RecalcEquipment();
            } else {
                // can't.  reset to the default rules level and scold the user
                Media.Messager( this, "You cannot set an OmniVee's loadout to a Rules Level\nlower than it's chassis' Rules Level." );
                cmbRulesLevel.setSelectedIndex( CurVee.GetLoadout().GetRulesLevel() );
                return;
            }
        } else {
            CurVee.SetRulesLevel( NewLevel );
            CheckTonnage( true );
            CurVee.SetChanged(true);

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

        if ( !CurVee.IsOmni())
            cmbTurret.setEnabled(true);

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
        JCheckBox[] ExpEquipmentCheckboxes = { chkArmoredMotive,
                                               chkSupercharger,
                                               chkCommandConsole,
                                               chkMinesweeper,
                                               chkJetBooster,
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

    private void cmbMotiveTypeActionPerformed(java.awt.event.ActionEvent evt) {
        if ( Load ) return;
        boolean wasVtol = CurVee.IsVTOL();

        switch ( cmbMotiveType.getSelectedIndex() ) {
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
        FixTonnageSpinner(CurVee.GetMinTonnage(), CurVee.GetMaxTonnage());
        FixMPSpinner();
        if( CurVee.IsVTOL() != wasVtol ) RecalcArmorPlacement();
        RecalcArmorLocations();
        SetWeaponChoosers();
        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
        RefreshEquipment();
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
        lblInfoName.setText( p.CritName() );
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

        lblInfoMountRestrict.setText(lblInfoMountRestrict.getText() + " MM Name " + p.MegaMekName(false));
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

    private void chkUseTCActionPerformed(java.awt.event.ActionEvent evt) {
        if( CurVee.UsingTC() == chkUseTC.isSelected() ) { return; }
        if( chkUseTC.isSelected() ) {
            try {
                CurVee.GetLoadout().CheckExclusions( CurVee.GetTC() );
                if( CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_BOTH ) {
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurVee.UseTC( true, tech.IsClan() );
                } else if( CurVee.GetLoadout().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    CurVee.UseTC( true, true );
                } else {
                    CurVee.UseTC( true, false );
                }
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                CurVee.UseTC( false, false );
            }
        } else {
            CurVee.UseTC( false, false );
        }
        SetWeaponChoosers();
        RefreshSelectedEquipment();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkFCSAIVActionPerformed(java.awt.event.ActionEvent evt) {
        if( CurVee.UsingArtemisIV() == chkFCSAIV.isSelected() ) { return; }
        if( chkFCSAIV.isSelected() ) {
            try {
                CurVee.SetFCSArtemisIV( true );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSAIV.setSelected( false );
            }
        } else {
            try {
                CurVee.SetFCSArtemisIV( false );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSAIV.setSelected( true );
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
    }

    private void chkFCSAVActionPerformed(java.awt.event.ActionEvent evt) {
        if( CurVee.UsingArtemisV() == chkFCSAV.isSelected() ) { return; }
        if( chkFCSAV.isSelected() ) {
            try {
                CurVee.SetFCSArtemisV( true );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSAV.setSelected( false );
            }
        } else {
            try {
                CurVee.SetFCSArtemisV( false );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSAV.setSelected( true );
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
    }

    private void chkFCSApolloActionPerformed(java.awt.event.ActionEvent evt) {
         if( CurVee.UsingApollo() == chkFCSApollo.isSelected() ) { return; }
        if( chkFCSApollo.isSelected() ) {
            try {
                CurVee.SetFCSApollo( true );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSApollo.setSelected( false );
            }
        } else {
            try {
                CurVee.SetFCSApollo( false );
            } catch( Exception e ) {
                Media.Messager( this, e.getMessage() );
                chkFCSApollo.setSelected( true );
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
    }

    private void chkCASEActionPerformed(java.awt.event.ActionEvent evt) {
        if ( !chkCASE.isSelected() ) {
            CurVee.GetLoadout().RemoveCase();
            SetWeaponChoosers();
            RefreshSummary();
            RefreshInfoPane();
            return;
        }

        try {
            switch ( CurVee.GetTechBase() ) {
                case AvailableCode.TECH_INNER_SPHERE:
                    CurVee.GetLoadout().AddCase(false);
                    break;
                case AvailableCode.TECH_CLAN:
                    CurVee.GetLoadout().AddCase(true);
                    break;
                case AvailableCode.TECH_BOTH:
                    dlgTechBaseChooser tech = new dlgTechBaseChooser( this, true );
                    tech.setLocationRelativeTo( this );
                    tech.setVisible( true );
                    CurVee.GetLoadout().AddCase(tech.IsClan());
                    break;
            }
        } catch ( Exception e ) {
            Media.Messager(e.getMessage());
        }
        SetWeaponChoosers();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void lstSelectedEquipmentValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if( lstSelectedEquipment.getSelectedIndex() < 0 ) { return; }
        abPlaceable p = (abPlaceable) Equipment[SELECTED][lstSelectedEquipment.getSelectedIndex()];
        ShowInfoOn( p );
    }

    private void lstSelectedEquipmentKeyPressed(java.awt.event.KeyEvent evt) {
        if ( evt.getKeyCode() == KeyEvent.VK_DELETE ) {
            btnRemoveEquipActionPerformed(new ActionEvent(evt.getSource(), evt.getID(), null));
        }
    }

    private void btnRemoveEquipActionPerformed(java.awt.event.ActionEvent evt) {
        if( lstSelectedEquipment.getSelectedIndex() < 0 ) { return; }
        int[] selected = lstSelectedEquipment.getSelectedIndices();
        if( selected.length == 0 ) { return; }
        // we work in reverse so we can properly manage the items in the queue
        for( int i = selected.length - 1; i >= 0; i-- ) {
            abPlaceable p = (abPlaceable) CurVee.GetLoadout().GetNonCore().get( selected[i] );
            if ( p instanceof TargetingComputer ) {
                CurVee.UseTC(false, CurVee.GetTechBase() == AvailableCode.TECH_CLAN);
            }
            if( p.LocationLocked() ) {
                Media.Messager( this, "You may not remove a locked item (" + p.ActualName() + ") from the loadout." );
                return;
            } else {
                CurVee.GetLoadout().Remove( p );
            }
        }
        // refresh the selected equipment listbox
        if( CurVee.GetLoadout().GetNonCore().toArray().length <= 0 ) {
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

    private void btnClearEquipActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.GetLoadout().SafeClearLoadout();

        // refresh the selected equipment listbox
        if( CurVee.GetLoadout().GetNonCore().toArray().length <= 0 ) {
            Equipment[SELECTED] = new Object[] { " " };
        } else {
            Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
        }
        lstSelectedEquipment.setListData( Equipment[SELECTED] );

        // Check the targeting computer if needed
        if( CurVee.UsingTC() ) {
            CurVee.CheckTC();
        }

        // refresh the ammunition display
        ResetAmmo();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void btnAddEquipActionPerformed(java.awt.event.ActionEvent evt) {
        abPlaceable a = null;
        int Index = 0;
        ArrayList v;

        // figure out which list box to pull from
        switch( tbpWeaponChooser.getSelectedIndex() ) {
        case BALLISTIC:
            if( lstChooseBallistic.getSelectedIndex() < 0 ) { break; }
            a = (abPlaceable) Equipment[BALLISTIC][lstChooseBallistic.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurVee );
            break;
        case ENERGY:
            if( lstChooseEnergy.getSelectedIndex() < 0 ) { break; }
            a = (abPlaceable) Equipment[ENERGY][lstChooseEnergy.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurVee );
            break;
        case MISSILE:
            if( lstChooseMissile.getSelectedIndex() < 0 ) { break; }
            a = (abPlaceable) Equipment[MISSILE][lstChooseMissile.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurVee );
            if( ((RangedWeapon) a).IsFCSCapable() ) {
                if( CurVee.UsingArtemisIV() ) {
                    if( ((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_ArtemisIV || ((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_ArtemisV ) {
                        ((RangedWeapon) a).UseFCS( true, ifMissileGuidance.FCS_ArtemisIV );
                    }
                }
                if( CurVee.UsingArtemisV() ) {
                    if( ((RangedWeapon) a).GetFCSType() == ifMissileGuidance.FCS_ArtemisV ) {
                        ((RangedWeapon) a).UseFCS( true, ifMissileGuidance.FCS_ArtemisV );
                    }
                }
                if( CurVee.UsingApollo() ) {
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
            a = data.GetEquipment().GetCopy( a, CurVee );
            break;
        case ARTILLERY:
            if( lstChooseArtillery.getSelectedIndex() < 0 ) { break; }
            if( ! ( Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurVee );
            break;
        case EQUIPMENT:
            if( lstChooseEquipment.getSelectedIndex() < 0 ) { break; }
            if( ! ( Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()];
            a = data.GetEquipment().GetCopy( a, CurVee );
            break;
        case AMMUNITION:
            if( lstChooseAmmunition.getSelectedIndex() < 0 ) { break; }
            Index = lstChooseAmmunition.getSelectedIndex();
            if( ! ( Equipment[AMMUNITION][Index] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[AMMUNITION][Index];
            a = data.GetEquipment().GetCopy( a, CurVee );
            break;
        }

        // check exclusions if needed
        if( a != null ) {
            try {
                CurVee.GetLoadout().CheckExclusions( a );
                if( a instanceof ifEquipment ) {
                    ((ifEquipment) a).Validate( CurVee );
                }
            } catch( Exception e ) {
                Media.Messager( e.getMessage() );
                a = null;
            }
        }

        // now we can add it to the Vehicle
        if( a != null ) {
            boolean result = true;
            if( a instanceof Equipment ) {
                if( ((Equipment) a).IsVariableSize() ) {
                    dlgVariableSize SetTons = new dlgVariableSize( this, true, (Equipment) a, CurVee );
                    SetTons.setLocationRelativeTo( this );
                    SetTons.setVisible( true );
                    result = SetTons.GetResult();
                }
            }
            if( result ) {
                try
                {
                    a.Place(CurVee.GetLoadout(), LocationIndex.FindIndex(CurVee, cmbLocation.getSelectedValue().toString()));
                    for( int i = 0; i < cmbNumEquips.getSelectedIndex(); i++ ) {
                        a = data.GetEquipment().GetCopy( a, CurVee );
                        a.Place(CurVee.GetLoadout(), LocationIndex.FindIndex(CurVee, cmbLocation.getSelectedValue().toString()));
                    }
                } catch ( Exception e ) {
                    //something happened
                    Media.Messager(e.getMessage());
                }

                // unallocate the TC if needed (if the size changes)
                if( a instanceof ifWeapon ) {
                    if( ((ifWeapon) a).IsTCCapable() && CurVee.UsingTC() ) {
                        CurVee.UnallocateTC();
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
                if( CurVee.GetLoadout().GetNonCore().toArray().length <= 0 ) {
                    Equipment[SELECTED] = new Object[] { " " };
                } else {
                    Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
                }
                lstSelectedEquipment.setListData( Equipment[SELECTED] );
            }
            spnHeatSinks.setModel(new SpinnerNumberModel(CurVee.GetHeatSinks().GetNumHS(), ((CVLoadout)CurVee.GetLoadout()).GetTotalHeat(), 99, 1));

            // now refresh the information panes
            RefreshSummary();
            RefreshInfoPane();
            cmbNumEquips.setSelectedIndex( 0 );
        }
    }

    private void btnAddToForceListActionPerformed(java.awt.event.ActionEvent evt) {

    }

    private void btnForceListActionPerformed(java.awt.event.ActionEvent evt) {
        GetForceDialogue().setLocationRelativeTo(this);
        GetForceDialogue().setVisible(true);
    }

    private void spnCruiseMPStateChanged(javax.swing.event.ChangeEvent evt) {
        if( Load ) { return; }

        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnCruiseMP.getModel();
        javax.swing.JComponent editor = spnCruiseMP.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnCruiseMP.commitEdit();
        } catch ( java.text.ParseException pe ) {
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

            if ( !CurVee.GetEngine().RequiresControls() && walkMP > 0 ) {
                spnCruiseMP.setValue(0);
                //Media.Messager("Please select an engine first");
                return;
            }

            CurVee.setCruiseMP( walkMP );
        } catch( Exception e ) {
            Media.Messager( e.getMessage() );
            spnCruiseMP.setValue( spnCruiseMP.getPreviousValue() );
        }
        lblFlankMP.setText( "" + CurVee.getFlankMP() );

        // when the walking mp changes, we also have to change the jump mp
        // spinner model and recalculate the heat sinks
        FixMPSpinner();
        FixJJSpinnerModel();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnTonnageFocusGained(java.awt.event.FocusEvent evt) {

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
        String filename = "";
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
        if( ! CurVee.ValidateECM() ) {
            Media.Messager( "This 'Mech requires an ECM system of some sort to be valid.\nPlease install an ECM system." );
            tbpMainTabPane.setSelectedComponent( jPanel3 );
            SetSource = true;
            return false;
        }

        // ensure we're not overweight
        if( CurVee.IsOmni() ) {
            ArrayList v = CurVee.GetLoadouts();
            for( int i = 0; i < v.size(); i++ ) {
                CurVee.SetCurLoadout( ((ifCVLoadout) v.get( i )).GetName() );
                if( CurVee.GetCurrentTons() > CurVee.GetTonnage() ) {
                    Media.Messager( this, ((ifCVLoadout) v.get( i )).GetName() +
                        " loadout is overweight.  Reduce the weight\nto equal or below the Vehicle's tonnage." );
                    //cmbOmniVariant.setSelectedItem( ((ifCVLoadout) v.get( i )).GetName() );
                    //cmbOmniVariantActionPerformed( evt );
                    tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
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

    private void spnTonnageStateChanged(javax.swing.event.ChangeEvent evt) {
        if( Load ) { return; }

        // We have to decode the selected index to set values.  A bit safer, I
        // think, because we can directly set the values ourselves.
        int Tons = 0;
        try
        {
           Tons = Integer.parseInt(spnTonnage.getValue().toString());
        } catch ( Exception e ) {
            Media.Messager(e.getMessage());
            return;
        }

        if ( Tons == 0 ) Tons = 1;

        if ( Tons >= 0 && Tons <= 39)
            lblVeeClass.setText("Light Vee");
        if ( Tons >= 40 && Tons <= 59 )
            lblVeeClass.setText("Medium Vee");
        if ( Tons >= 60 && Tons <= 79 )
            lblVeeClass.setText("Heavy Vee");
        if ( Tons >= 80)
            lblVeeClass.setText("Assault Vee");

        if( CurVee.GetTonnage() == Tons ) {
            return;
        } else {
            CurVee.setTonnage( Tons );
        }

        // check the tonnage
        if( CurVee.GetTonnage() < 1 ) {
            spnTonnage.setValue(1);
        }

        if ( CurVee.GetTonnage() > CurVee.GetMaxTonnage() ) {
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
        ifVisitor v = (ifVisitor) CurVee.Lookup( LookupVal );
        try {
            CurVee.Visit( v );
        } catch( Exception e ) {
            v = (ifVisitor) CurVee.Lookup( OldVal );
            try {
                Media.Messager( this, "The new engine type is not valid.  Error:\n" + e.getMessage() + "\nReverting to the previous engine." );
                CurVee.Visit( v );
                cmbEngineType.setSelectedItem( OldVal );
            } catch( Exception e1 ) {
                // wow, second one?  Get a new 'Mech.
                Media.Messager( this, "Fatal error while attempting to revert to the old engine:\n" + e.getMessage() + "\nStarting over with a new Vehicle.  Sorry." );
                GetNewVee();
                return;
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
        txtProdYear.setEnabled( true );
        cmbEra.setEnabled( true );
        cmbProductionEra.setEnabled( true );
        cmbTechBase.setEnabled( true );
        cmbTurret.setSelectedIndex(0);
        spnTurretTonnage.setModel(new SpinnerNumberModel(0.0, 0.0, 50.0, 0.5));

        cmbOmniVariant.setModel( new javax.swing.DefaultComboBoxModel( new String[0] ) );

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

        chkYearRestrict.setEnabled(cmbEra.getSelectedIndex() != AvailableCode.ERA_ALL);
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
            btnLockChassis.setEnabled(chkOmniVee.isSelected());
        } else {
            btnLockChassis.setEnabled( false );
        }
    }

    private void RefreshEquipment() {
        // refreshes the equipment selectors
        //fix the CASE control
        CASE Case = new CASE();
        setCheckbox(chkCASE, ( CommonTools.IsAllowed( Case.GetAvailability(), CurVee) || CurVee.GetTechBase() == AvailableCode.TECH_CLAN ), CurVee.GetLoadout().HasCase());

        // fix Artemis IV controls
        ifMissileGuidance ArtCheck = new ArtemisIVFCS( null );
        setCheckbox(chkFCSAIV, ( CommonTools.IsAllowed( ArtCheck.GetAvailability(), CurVee ) ), CurVee.UsingArtemisIV());

        // fix Artemis V controls
        ArtCheck = new ArtemisVFCS( null );
        setCheckbox(chkFCSAV, ( CommonTools.IsAllowed( ArtCheck.GetAvailability(), CurVee ) ), CurVee.UsingArtemisV());

        // fix ApolloFCS controls
        ArtCheck = new ApolloFCS( null );
        setCheckbox(chkFCSApollo, ( CommonTools.IsAllowed( ArtCheck.GetAvailability(), CurVee ) ), CurVee.UsingApollo());

        // fix the targeting computer display
        setCheckbox(chkUseTC, ( CommonTools.IsAllowed( CurVee.GetTC().GetAvailability(), CurVee ) ), CurVee.UsingTC());

        setCheckbox(chkSupercharger, ( CommonTools.IsAllowed( CurVee.GetLoadout().GetSupercharger().GetAvailability(), CurVee ) && !CurVee.IsVTOL() ), CurVee.GetLoadout().HasSupercharger());
        setCheckbox(chkJetBooster, ( CommonTools.IsAllowed( CurVee.GetLoadout().GetVTOLBooster().GetAvailability(), CurVee ) && CurVee.IsVTOL() && !CurVee.IsOmni() ), CurVee.GetLoadout().HasVTOLBooster());

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

    private void setCheckbox(JCheckBox element, Boolean isEnabled, Boolean isSelected) {
        element.setEnabled(isEnabled);
        element.setSelected(false);
        if (isEnabled) {
            element.setSelected(isSelected);
        }
    }

    private void SetWeaponChoosers() {
        // sets the weapon choosers up.  first, get the user's choices.

        // get the equipment lists for the choices.
        if ( data == null ) return;

        Equipment[ENERGY] = data.GetEquipment().GetEnergyWeapons( CurVee );
        Equipment[MISSILE] = data.GetEquipment().GetMissileWeapons( CurVee );
        Equipment[BALLISTIC] = data.GetEquipment().GetBallisticWeapons( CurVee );
        Equipment[PHYSICAL] = data.GetEquipment().GetPhysicalWeapons( CurVee );
        Equipment[ARTILLERY] = data.GetEquipment().GetArtillery( CurVee );
        Equipment[EQUIPMENT] = data.GetEquipment().GetEquipment( CurVee );
        Equipment[AMMUNITION] = new Object[] { " " };
        if( CurVee.GetLoadout().GetNonCore().toArray().length <= 0 ) {
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
        btnLockChassis.setEnabled( false );
        chkFCSAIV.setEnabled( true );
        chkFCSAV.setEnabled( true );
        chkFCSApollo.setEnabled( true );
        chkCASE.setEnabled( true );
        chkOmniVee.setSelected( false );
        chkOmniVee.setEnabled( true );
        spnCruiseMP.setEnabled( true );
        chkYearRestrict.setEnabled( true );
        chkSupercharger.setEnabled( true );
        chkJetBooster.setEnabled(true);
        chkEnviroSealing.setEnabled( false );
        // now enable the Omni controls
        cmbOmniVariant.setSelectedItem("");
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
                    RefreshSelectedEquipment();
                } catch (Exception ex) {
                    Media.Messager(ex.getMessage());
                    return false;
                }
            }
            return true;
        } else {
            Media.Messager( this, "Please add an appropriate ECM Suite to complement this\n system.  The Vehicle is not valid without an ECM Suite." );
            return true;
        }
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

    private void cmbEngineTypeActionPerformed(java.awt.event.ActionEvent evt) {
        if( Load ) { return; }
        if( BuildLookupName( CurVee.GetEngine().GetCurrentState() ).equals( (String) cmbEngineType.getSelectedItem() ) ) {
            // only nuclear-powered mechs may use jump jets
            /*if( CurVee.GetEngine().IsNuclear() ) {

                if( cmbJumpJetType.getSelectedItem() == null ) {
                    EnableJumpJets( false );
                } else {
                    EnableJumpJets( true );
                }
            } else {
                EnableJumpJets( false );
            }
             */
            return;
        }
        RecalcEngine();
        FixMPSpinner();

        //When the engine changes we need to re-check the Heat Sinks
        CurVee.ResetHeatSinks();

        // refresh the selected equipment listbox
        if( CurVee.GetLoadout().GetNonCore().toArray().length <= 0 ) {
            Equipment[SELECTED] = new Object[] { " " };
        } else {
            Equipment[SELECTED] = CurVee.GetLoadout().GetNonCore().toArray();
        }
        lstSelectedEquipment.setListData( Equipment[SELECTED] );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void cmbEraActionPerformed(java.awt.event.ActionEvent evt) {
        if( Load ) { return; }
        // whenever the era is changed we basically need to reset the GUI and
        // most of the mech.  Certain things we will transfer.
        if( CurVee.GetEra() == cmbEra.getSelectedIndex() ) {
            return;
        }
        if( CurVee.IsOmni() ) {
            if( cmbEra.getSelectedIndex() < CurVee.GetBaseEra() ) {
                Media.Messager( this, "An Omni loadout cannot have an era lower than the main loadout." );
                cmbEra.setSelectedIndex( CurVee.GetBaseEra() );
            }
        }

        // first, let's save the tech base selection in case we can still use it
        // prevents Clan mechs reverting to Inner Sphere on era change.
        int tbsave = cmbTechBase.getSelectedIndex();

        // change the year range and tech base options
        switch( cmbEra.getSelectedIndex() ) {
            case AvailableCode.ERA_STAR_LEAGUE:
                lblEraYears.setText( "2443 ~ 2800" );
                txtProdYear.setText( "" );
                CurVee.SetEra( AvailableCode.ERA_STAR_LEAGUE );
                CurVee.SetYear( 2750, false );
                if( ! CurVee.IsOmni() ) { chkYearRestrict.setEnabled( true ); }
                break;
            case AvailableCode.ERA_SUCCESSION:
                lblEraYears.setText( "2801 ~ 3050" );
                txtProdYear.setText( "" );
                CurVee.SetEra( AvailableCode.ERA_SUCCESSION );
                CurVee.SetYear( 3025, false );
                if( ! CurVee.IsOmni() ) { chkYearRestrict.setEnabled( true ); }
                break;
            case AvailableCode.ERA_CLAN_INVASION:
                lblEraYears.setText( "3051 ~ 3131" );
                txtProdYear.setText( "" );
                CurVee.SetEra( AvailableCode.ERA_CLAN_INVASION );
                CurVee.SetYear( 3075, false );
                if( ! CurVee.IsOmni() ) { chkYearRestrict.setEnabled( true ); }
                break;
            case AvailableCode.ERA_DARK_AGES:
                lblEraYears.setText( "3132 on" );
                txtProdYear.setText( "" );
                CurVee.SetEra( AvailableCode.ERA_DARK_AGES );
                CurVee.SetYear( 3132, false );
                if( ! CurVee.IsOmni() ) { chkYearRestrict.setEnabled( true ); }
                break;
            case AvailableCode.ERA_ALL:
                lblEraYears.setText( "Any" );
                txtProdYear.setText( "" );
                CurVee.SetEra( AvailableCode.ERA_ALL );
                CurVee.SetYear( 0, false );
                chkYearRestrict.setEnabled( false );
                break;
        }
        CurVee.SetChanged(true);

        if( CurVee.IsOmni() ) {
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
        if( tbsave < cmbTechBase.getItemCount() ) {
            // still valid, use it.  No reconfigure needed
            cmbTechBase.setSelectedIndex( tbsave );
        } else {
            // nope, set it to Inner Sphere.  This means it was Clan and we
            // should reconfigure the mech
            cmbTechBase.setSelectedIndex( 0 );
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

    private void chkFractionalActionPerformed(java.awt.event.ActionEvent evt) {
        if( chkFractional.isSelected() == CurVee.UsingFractionalAccounting() ) { return; }
        CurVee.SetFractionalAccounting( chkFractional.isSelected() );
        if( ! CurVee.UsingFractionalAccounting() ) {
            ArrayList v = CurVee.GetLoadout().GetNonCore();
            for( int i = 0; i < v.size(); i++ ) {
                if( v.get( i ) instanceof Ammunition ) {
                    ((Ammunition) v.get( i )).ResetLotSize();
                }
            }
        }

        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnTonnageInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        spnTonnageStateChanged(null);
    }

    private void spnCruiseMPInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        spnCruiseMPStateChanged(null);
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

    private void cmbArmorTypeActionPerformed(java.awt.event.ActionEvent evt) {
        if( Load ) { return; }
        if( BuildLookupName( CurVee.GetArmor().GetCurrentState() ).equals( (String) cmbArmorType.getSelectedItem() ) ) {
            return;
        }
        RecalcArmor();
        // we check for hardened armor, you can only have so many IJJs
        FixJJSpinnerModel();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void btnUseRemainingActionPerformed(java.awt.event.ActionEvent evt) {
        // see if we have a good number
        double freetons = CurVee.GetTonnage() - CurVee.GetCurrentTons() + CurVee.GetArmor().GetTonnage();

        if( freetons > CurVee.GetArmor().GetMaxTonnage() ) {
            freetons = CurVee.GetArmor().GetMaxTonnage();
        }

        ArmorTons.SetArmorTonnage( freetons );
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
            try {
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
        CurVee.SetSSWImage( fc.getSelectedFile().getPath() );
    }

    private void btnClearImageActionPerformed(java.awt.event.ActionEvent evt) {
        // Set the fluff image to default
        lblFluffImage.setIcon( null );
        CurVee.SetSSWImage("");
    }

    private void btnExportTXTActionPerformed(java.awt.event.ActionEvent evt) {
        // exports the mech to TXT format
        String CurLoadout = "";
        if( CurVee.IsOmni() ) {
            CurLoadout = CurVee.GetLoadout().GetName();
        }

        String dir = Prefs.get( "TXTExportPath", "none" );
        if( dir.equals( "none" ) ) {
            dir = Prefs.get( "LastOpenCVDirectory", "" );
        }
        File savemech = GetSaveFile( "txt", dir, false, false );
        if( savemech == null ) {
            return;
        }

        String filename = "";
        CVTXTWriter txtw = new CVTXTWriter( CurVee );
        try {
            filename = savemech.getCanonicalPath();
            txtw.WriteTXT( filename );
        } catch( IOException e ) {
            Media.Messager( this, "There was a problem writing the file:\n" + e.getMessage() );
            return;
        }

        // if there were no problems, let the user know how it went
        Media.Messager( this, "Vehicle saved successfully to TXT:\n" + filename );

        // lastly, if this is an Omni, reset the display to the last loadout
        if( CurVee.IsOmni() ) {
            //cmbOmniVariant.setSelectedItem( CurLoadout );
            //cmbOmniVariantActionPerformed( evt );
        }
        setTitle( saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel() );
    }

    private void btnExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {
        // exports the mech to HTML format
        String CurLoadout = "";
        if( CurVee.IsOmni() ) {
            CurLoadout = CurVee.GetLoadout().GetName();
        }

        String dir = Prefs.get( "HTMLExportPath", "none" );
        if( dir.equals( "none" ) ) {
            dir = Prefs.get( "LastOpenCVDirectory", "" );
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

        // lastly, if this is an Omni, reset the display to the last loadout
        if( CurVee.IsOmni() ) {
            //cmbOmniVariant.setSelectedItem( CurLoadout );
            //cmbOmniVariantActionPerformed( evt );
        }
        setTitle( saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel() );
    }

    private void btnExportMTFActionPerformed(java.awt.event.ActionEvent evt) {
        // exports the mech to MTF format for use in Megamek
        String dir = Prefs.get( "MTFExportPath", "none" );
        if( dir.equals( "none" ) ) {
            dir = Prefs.get( "LastOpenCVDirectory", "" );
        }
        File savemech = GetSaveFile( "mtf", dir, false, true );
        if( savemech == null ) {
            return;
        }

        String filename = "";
        IO.MTFWriter mtfw = new IO.MTFWriter( CurVee, saw.Constants.AppDescription + " " + saw.Constants.GetVersion() );
        try {
            filename = savemech.getCanonicalPath();
            mtfw.WriteMTF( filename );
        } catch( IOException e ) {
            Media.Messager( this, "There was a problem writing the file:\n" + e.getMessage() );
            return;
        }

        // if there were no problems, let the user know how it went
        Media.Messager( this, "Vehicle saved successfully to MTF:\n" + filename );
        setTitle( saw.Constants.AppName + " " + saw.Constants.GetVersion() + " - " + CurVee.GetName() + " " + CurVee.GetModel() );
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
    private void cmbTurretActionPerformed(java.awt.event.ActionEvent evt) {
        if( Load ) { return; }

        String Turret = cmbTurret.getSelectedItem().toString();
        if ( Turret.equals("Single Turret") || Turret.equals("Chin Turret")) {
            CurVee.setHasTurret1(true);
            if (chkOmniVee.isSelected() && !isLocked )
                spnTurretTonnage.setEnabled(true);
        } else if(Turret.equals("Dual Turret")) {
            CurVee.setHasTurret1(true);
            CurVee.setHasTurret2(true);
            if (chkOmniVee.isSelected() && !isLocked )
                spnTurretTonnage.setEnabled(true);
        } else {
            CurVee.setHasTurret1(false);
            CurVee.setHasTurret2(false);
            spnTurretTonnage.setEnabled(false);
        }

        BuildLocationSelector();
        RecalcArmorLocations();
        RefreshSelectedEquipment();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void btnSetArmorTonsActionPerformed(java.awt.event.ActionEvent evt) {
        // we'll need a new dialogue to get the tonnage
        dlgArmorTonnage ArmorDialogue = new dlgArmorTonnage( this, true, CurVee );
        ArmorDialogue.setLocationRelativeTo( this );
        ArmorDialogue.setVisible( true );

        // see if we have a good number
        if( ArmorDialogue.NewTonnage() ) {
            double result = ArmorDialogue.GetResult();
            ArmorTons.SetArmorTonnage( result );
            try {
                CurVee.Visit( ArmorTons );
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

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnHeatSinksStateChanged(javax.swing.event.ChangeEvent evt) {
        if( Load ) { return; }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHeatSinks.getModel();
        int NumHS = CurVee.GetHeatSinks().GetNumHS();
        javax.swing.JComponent editor = spnHeatSinks.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnHeatSinks.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnHeatSinks.getValue());
            }
            return;
        }

        if( n.getNumber().intValue() > NumHS ) {
            // The number of sinks went up
            for( int i = NumHS; i < n.getNumber().intValue(); i++ ) {
                CurVee.GetHeatSinks().IncrementNumHS();
            }
        } else {
            // the number went down
            for( int i = NumHS; i > n.getNumber().intValue(); i-- ) {
                CurVee.GetHeatSinks().DecrementNumHS();
            }
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
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

    private void spnHeatSinksFocusGained(java.awt.event.FocusEvent evt) {

    }

    private void spnHeatSinksInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {

    }

    private void spnFrontArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if( Load ) { return; }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnFrontArmor.getModel();
        javax.swing.JComponent editor = spnFrontArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnFrontArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnFrontArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_FRONT );
        int curframe = n.getNumber().intValue();
        if( curframe > locArmor ) {
            while( curframe > locArmor ) {
                a.IncrementArmor( LocationIndex.CV_LOC_FRONT );
                curframe--;
            }
        } else {
            while( locArmor > curframe ) {
                a.DecrementArmor( LocationIndex.CV_LOC_FRONT );
                locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_FRONT );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnFrontArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.CV_LOC_FRONT ) );

        if( chkBalanceFRArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnFrontArmor.getModel();
            a.SetArmor( LocationIndex.CV_LOC_REAR, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnRearArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.CV_LOC_FRONT ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnLeftArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if( Load ) { return; }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnLeftArmor.getModel();
        javax.swing.JComponent editor = spnLeftArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnLeftArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnLeftArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_LEFT );
        int curframe = n.getNumber().intValue();
        if( curframe > locArmor ) {
            while( curframe > locArmor ) {
                a.IncrementArmor( LocationIndex.CV_LOC_LEFT );
                curframe--;
            }
        } else {
            while( locArmor > curframe ) {
                a.DecrementArmor( LocationIndex.CV_LOC_LEFT );
                locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_LEFT );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnLeftArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.CV_LOC_LEFT ) );

        if( chkBalanceLRArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnLeftArmor.getModel();
            a.SetArmor( LocationIndex.CV_LOC_RIGHT, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnRightArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.CV_LOC_LEFT ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnRightArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if( Load ) { return; }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRightArmor.getModel();
        javax.swing.JComponent editor = spnRightArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRightArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRightArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_RIGHT );
        int curframe = n.getNumber().intValue();
        if( curframe > locArmor ) {
            while( curframe > locArmor ) {
                a.IncrementArmor( LocationIndex.CV_LOC_RIGHT );
                curframe--;
            }
        } else {
            while( locArmor > curframe ) {
                a.DecrementArmor( LocationIndex.CV_LOC_RIGHT );
                locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_RIGHT );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnRightArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.CV_LOC_RIGHT ) );

        if( chkBalanceLRArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnRightArmor.getModel();
            a.SetArmor( LocationIndex.CV_LOC_LEFT, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnLeftArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.CV_LOC_RIGHT ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnRearArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if( Load ) { return; }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRearArmor.getModel();
        javax.swing.JComponent editor = spnRearArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRearArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRearArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_REAR );
        int curframe = n.getNumber().intValue();
        if( curframe > locArmor ) {
            while( curframe > locArmor ) {
                a.IncrementArmor( LocationIndex.CV_LOC_REAR );
                curframe--;
            }
        } else {
            while( locArmor > curframe ) {
                a.DecrementArmor( LocationIndex.CV_LOC_REAR );
                locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_REAR );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnRearArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.CV_LOC_REAR ) );

        if( chkBalanceFRArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnRearArmor.getModel();
            a.SetArmor( LocationIndex.CV_LOC_FRONT, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnFrontArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( LocationIndex.CV_LOC_REAR ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnTurretArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if( Load ) { return; }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnTurretArmor.getModel();
        javax.swing.JComponent editor = spnTurretArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnTurretArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnTurretArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_TURRET1 );
        int curframe = n.getNumber().intValue();
        if( curframe > locArmor ) {
            while( curframe > locArmor ) {
                a.IncrementArmor( LocationIndex.CV_LOC_TURRET1 );
                curframe--;
            }
        } else {
            while( locArmor > curframe ) {
                a.DecrementArmor( LocationIndex.CV_LOC_TURRET1 );
                locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_TURRET1 );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnTurretArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.CV_LOC_TURRET1 ) );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void lstChooseAmmunitionValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if( lstChooseAmmunition.getSelectedIndex() < 0 ) { return; }
        CurItem = (abPlaceable) Equipment[AMMUNITION][lstChooseAmmunition.getSelectedIndex()];
        ShowInfoOn( CurItem );
    }

    private void lstChooseArtilleryValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if( lstChooseArtillery.getSelectedIndex() < 0 ) { return; }
        CurItem = (abPlaceable) Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()];
        ShowInfoOn( CurItem );
    }

    private void lstChooseEquipmentValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if( lstChooseEquipment.getSelectedIndex() < 0 ) { return; }
        CurItem = (abPlaceable) Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()];
        ShowInfoOn( CurItem );
    }

    private void lstChooseMissileValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if( lstChooseMissile.getSelectedIndex() < 0 ) { return; }
        CurItem = (abPlaceable) Equipment[MISSILE][lstChooseMissile.getSelectedIndex()];
        ShowInfoOn( CurItem );
    }

    private void lstChooseEnergyValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if( lstChooseEnergy.getSelectedIndex() < 0 ) { return; }
        CurItem = (abPlaceable) Equipment[ENERGY][lstChooseEnergy.getSelectedIndex()];
        ShowInfoOn( CurItem );
    }

    private void lstChooseBallisticValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if( lstChooseBallistic.getSelectedIndex() < 0 ) { return; }
        CurItem = (abPlaceable) Equipment[BALLISTIC][lstChooseBallistic.getSelectedIndex()];
        ShowInfoOn( CurItem );
    }

    private void lstChoosePhysicalValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if( lstChoosePhysical.getSelectedIndex() < 0 ) { return; }
        CurItem = (abPlaceable) Equipment[PHYSICAL][lstChoosePhysical.getSelectedIndex()];
        ShowInfoOn( CurItem );
    }

    private void cmbTechBaseActionPerformed(java.awt.event.ActionEvent evt) {
        if( Load ) { return; }
        // do we really need to do this?
        if( CurVee.IsOmni() ) {
            if( CurVee.GetLoadout().GetTechBase() == cmbTechBase.getSelectedIndex() ) { return; }
        } else {
            if( CurVee.GetTechbase() == cmbTechBase.getSelectedIndex() ) { return; }
        }

        if( CurVee.IsOmni() ) {
            boolean check = CurVee.SetTechBase( cmbTechBase.getSelectedIndex() );
            if( ! check ) {
                Media.Messager( this, "An Omni can only use the base chassis' Tech Base\nor Mixed Tech.  Resetting." );
                cmbTechBase.setSelectedIndex( CurVee.GetLoadout().GetTechBase() );
                return;
            }
            RefreshEquipment();
        } else {
            // now change the mech over to the new techbase
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

            //Adding these here for now, since the loadout gets completely trashed, these selections need to go away.
            chkSupercharger.setSelected(false);
            chkJetBooster.setSelected(false);

            // save the current selections.  The 'Mech should have already
            // flushed any illegal equipment in the changeover
            SaveSelections();

            data.Rebuild( CurVee );

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

            if ( CurVee.GetTechBase() == AvailableCode.TECH_CLAN ) {
                chkCASE.setSelected(true);
                chkCASEActionPerformed(evt);
            }
            // recalculate the mech.
            RecalcEngine();
            RecalcArmor();
        }

        RecalcEquipment();
        SetWeaponChoosers();
        chkUseTC.setSelected( false );

        // now refresh the information panes
        RefreshEquipment();
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
    }

    private void spnRotorArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if( Load ) { return; }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRotorArmor.getModel();
        javax.swing.JComponent editor = spnRotorArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRotorArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRotorArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int CurVee = a.GetLocationArmor( LocationIndex.CV_LOC_ROTOR );
        int curframe = n.getNumber().intValue();
        if( curframe > CurVee ) {
            while( curframe > CurVee ) {
                a.IncrementArmor( LocationIndex.CV_LOC_ROTOR );
                curframe--;
            }
        } else {
            while( CurVee > curframe ) {
                a.DecrementArmor( LocationIndex.CV_LOC_ROTOR );
                CurVee = a.GetLocationArmor( LocationIndex.CV_LOC_ROTOR );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnRotorArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( LocationIndex.CV_LOC_ROTOR ) );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
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

    private void RefreshOmniVariants() {
        ArrayList v = CurVee.GetLoadouts();
        String[] variants = new String[v.size()];
        if( v.size() <= 0 ) {
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
        // this locks most of the GUI controls.  Used mainly by OmniVehicles.
        isLocked = true;

        chkOmniVee.setSelected( true );
        chkOmniVee.setEnabled( false );
        mnuUnlock.setEnabled( true );
        spnTonnage.setEnabled( false );
        cmbMotiveType.setEnabled( false );
        cmbEngineType.setEnabled( false );
        cmbTurret.setEnabled( false );
        spnTurretTonnage.setEnabled( false );
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
        if ( chkCASE.isSelected() ) {
            chkCASE.setEnabled(false);
        }

        chkFractional.setEnabled( false );
        chkEnviroSealing.setEnabled( false );
        chkJetBooster.setEnabled(false);
        if( CurVee.GetBaseLoadout().HasSupercharger() ) {
            chkSupercharger.setEnabled( false );
        }

        // now enable the omnimech controls
        cmbOmniVariant.setEnabled( true );
        btnAddVariant.setEnabled( true );
        btnDeleteVariant.setEnabled( true );
        btnRenameVariant.setEnabled( true );
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

    private void chkOmniVeeActionPerformed(java.awt.event.ActionEvent evt) {
        if( chkOmniVee.isSelected() ) {
            btnLockChassis.setEnabled( true );
        } else {
            btnLockChassis.setEnabled( false );
        }
        cmbTurretActionPerformed(evt);
    }

    private void cmbProductionEraActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetProductionEra(cmbProductionEra.getSelectedIndex());
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

    public CombatVehicle LoadVehicle (){
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
        if( returnVal != JFileChooser.APPROVE_OPTION ) { return m; }
        File loadmech = fc.getSelectedFile();
        String filename = "";
        try {
            filename = loadmech.getCanonicalPath();
            Prefs.put("LastOpenCVDirectory", loadmech.getCanonicalPath().replace(loadmech.getName(), ""));
            Prefs.put("LastOpenCVFile", loadmech.getName());
            Prefs.put("CurrentCVfile", loadmech.getCanonicalPath());
        } catch( Exception e ) {
            Media.Messager( this, "There was a problem opening the file:\n" + e.getMessage() );
            return m;
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
        chkTrailer.setSelected( CurVee.isTrailer() );

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
        spnCruiseMP.setModel( new javax.swing.SpinnerNumberModel(CurVee.getCruiseMP(), CurVee.getMinCruiseMP(), CurVee.getMaxCruiseMP(), 1) );
        if ( CurVee.isHasTurret1() ) cmbTurret.setSelectedItem("Single Turret");
        if ( CurVee.isHasTurret2() ) cmbTurret.setSelectedItem("Dual Turret");
        FixArmorSpinners();

        // now that we're done with the special stuff...
        Load = false;

        cmbOmniVariant.setModel( new javax.swing.DefaultComboBoxModel( new String[0] ) );
        if( CurVee.IsOmni() ) {
            if ( CurVee.isHasTurret1() )
                spnTurretTonnage.setModel( new SpinnerNumberModel(CurVee.GetBaseLoadout().GetTurret().GetMaxTonnage(), 0, 99.0, 0.5) );
            LockGUIForOmni();
            RefreshOmniVariants();
            RefreshOmniChoices();
        }

        FixTonnageSpinner( CurVee.GetMinTonnage(), CurVee.GetMaxTonnage() );

        // Update Tonnage and vehicle class
        lblVeeLimits.setText(CurVee.GetMaxTonnage() + "t Max");

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
        chkEnviroSealing.setSelected( CurVee.HasEnvironmentalSealing() );
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();

        // load the fluff image.
        Media media = new Media();
        media.blankLogo(lblFluffImage);
        media.setLogo(lblFluffImage, media.DetermineMatchingImage(CurVee.GetName(), CurVee.GetModel(), CurVee.GetSSWImage()));

        RefreshQuirks();

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
        if (cmbOmniVariant.isEnabled()) {
            CurVee.SetCurLoadout((String) cmbOmniVariant.getSelectedItem());
        }
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
        SolidifyVehicle();
        PagePrinter printer = SetupPrinter();
        Scenario s = new Scenario();
        s.getAttackerForce().AddUnit(new Unit(CurVee));
        dlgPreview prv = new dlgPreview("Print Preview", this, printer, s, imageTracker, true);
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

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
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

    private void chkYearRestrictActionPerformed(java.awt.event.ActionEvent evt) {
        // This locks in the mech's production year, era, and tech base.
        int year = 0;
        if( CurVee.IsYearRestricted() == chkYearRestrict.isSelected() ) { return; }

        // if we just unchecked the box, clear all locks and exit.
        if( ! chkYearRestrict.isSelected() ) {
            cmbEra.setEnabled( true );
            cmbTechBase.setEnabled( true );
            txtProdYear.setEnabled( true );
            CurVee.SetYearRestricted( false );
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
            switch ( cmbEra.getSelectedIndex() ) {
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
            cmbEra.setEnabled( false );
            cmbTechBase.setEnabled( false );
            txtProdYear.setEnabled( false );
            CurVee.SetYear( year, true );
            CurVee.SetYearRestricted( true );
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

    private void spnTurretTonnageStateChanged(javax.swing.event.ChangeEvent evt) {
        double Tons = 0;
        try
        {
           Tons = Double.parseDouble(spnTurretTonnage.getValue().toString());
           CurVee.GetLoadout().GetTurret().SetTonnage(Tons);
        } catch ( Exception e ) {
            Media.Messager(e.getMessage());
            return;
        }

        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkTrailerActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetTrailer(chkTrailer.isSelected());
        String curEngine = cmbEngineType.getSelectedItem().toString();
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
                if ( Integer.parseInt(spnCruiseMP.getValue().toString()) == 0 )
                    CurVee.setCruiseMP(1);
            } catch (Exception ex) {
                Media.Messager(ex.getMessage());
            }
            if ( curEngine == "No Engine" )
                cmbEngineType.setSelectedIndex(0);
            else
                cmbEngineType.setSelectedItem(curEngine);
            cmbEngineTypeActionPerformed(evt);
            lblFlankMP.setText( "" + CurVee.getFlankMP() );
        }
        SetWeaponChoosers();
        RefreshEquipment();
        RefreshInfoPane();
    }

    private void chkFlotationHullActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetFlotationHull(chkFlotationHull.isSelected());
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkLimitedAmphActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetLimitedAmphibious(chkLimitedAmph.isSelected());
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkDuneBuggyActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetDuneBuggy(chkDuneBuggy.isSelected());
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkEnviroSealingActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetEnvironmentalSealing(chkEnviroSealing.isSelected());
        RefreshSummary();
        RefreshInfoPane();
    }

    private void spnRearTurretArmorStateChanged(javax.swing.event.ChangeEvent evt) {
        if( Load ) { return; }
        // see what changed and perform the appropriate action
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnRearTurretArmor.getModel();
        javax.swing.JComponent editor = spnRearTurretArmor.getEditor();
        javax.swing.JFormattedTextField tf = ((javax.swing.JSpinner.DefaultEditor)editor).getTextField();

        // get the value from the text box, if it's valid.
        try {
            spnRearTurretArmor.commitEdit();
        } catch ( java.text.ParseException pe ) {
            // Edited value is invalid, spinner.getValue() will return
            // the last valid value, you could revert the spinner to show that:
            if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
                tf.setValue(spnRearTurretArmor.getValue());
            }
            return;
        }

        // the commitedit worked, so set the armor value appropriately
        CVArmor a = CurVee.GetArmor();
        int locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_TURRET2 );
        int curframe = n.getNumber().intValue();
        if( curframe > locArmor ) {
            while( curframe > locArmor ) {
                a.IncrementArmor( LocationIndex.CV_LOC_TURRET2 );
                curframe--;
            }
        } else {
            while( locArmor > curframe ) {
                a.DecrementArmor( LocationIndex.CV_LOC_TURRET2 );
                locArmor = a.GetLocationArmor( LocationIndex.CV_LOC_TURRET2 );
            }
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }

    private void cmbLocationMouseClicked(java.awt.event.MouseEvent evt) {
        if ( evt.getClickCount() == 2 )
            btnAddEquipActionPerformed(null);
    }

    private void chkSponsonTurretActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.setHasSponsonTurret(chkSponsonTurret.isSelected());
        RefreshSelectedEquipment();
        BuildLocationSelector();
        RefreshSummary();
        RefreshInfoPane();
    }

    private void chkEscapePodActionPerformed(java.awt.event.ActionEvent evt) {
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

    private void chkFullAmphActionPerformed(java.awt.event.ActionEvent evt) {
        CurVee.SetFullAmphibious(chkFullAmph.isSelected());
        RefreshSummary();
        RefreshInfoPane();
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
        int max = 0;
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
    private javax.swing.JPanel jPanel3;
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
    private javax.swing.JLabel lblInfoName;
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
    private javax.swing.JLabel lblMoveSummary;
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
    private javax.swing.JTable tblWeaponManufacturers;
    private JTabbedPane tbpMainTabPane;
    private JTabbedPane tbpWeaponChooser;
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

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
import ssw.printpreview.PreviewDialog;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SpinnerNumberModel;
import ssw.*;
import ssw.components.*;
import ssw.filehandlers.*;
import ssw.visitors.*;
import ssw.print.*;
import ssw.states.ifState;
import java.util.prefs.*;
import ssw.Force.gui.frmForce;
import ssw.battleforce.BattleForceTools;

public class frmMain extends javax.swing.JFrame implements java.awt.datatransfer.ClipboardOwner {

    String[] Selections = { "", "", "", "", "", "", "", "", "", "", "" };
    Mech CurMech = new Mech( this );
    OptionsReader OReader = new OptionsReader();
    Options GlobalOptions = new Options();
    VSetArmorTonnage ArmorTons = new VSetArmorTonnage( GlobalOptions );
    java.awt.Color RedCol = new java.awt.Color( 200, 0, 0 ),
                   GreenCol = new java.awt.Color( 0, 40, 0 );
    Object[][] Equipment = { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } };
    WeaponFactory Weapons = new WeaponFactory( CurMech, GlobalOptions );
    EquipmentFactory Equips = new EquipmentFactory(CurMech);
    AmmoFactory Ammo = new AmmoFactory();
    abPlaceable CurItem;
    JPopupMenu mnuCrits = new JPopupMenu();
    JMenuItem mnuUnallocateAll = new JMenuItem( "Unallocate All" );
    JMenuItem mnuMountRear = new JMenuItem( "Mount Rear" );
    JMenuItem mnuInfoItem = new JMenuItem( "Get Info" );
    JMenuItem mnuRemoveItem = new JMenuItem( "Remove Item" );
    JPopupMenu mnuPlacement = new JPopupMenu();
    JMenuItem mnuInfoPlacement = new JMenuItem( "Get Info" );
    JMenuItem mnuSelective = new JMenuItem( "Selective Allocate" );
    JMenuItem mnuAuto = new JMenuItem( "Auto-Allocate" );
    JMenuItem mnuArmorComponent = new JMenuItem( "Armor Component" );
    MechLoadoutRenderer Mechrender = new MechLoadoutRenderer( this, GlobalOptions );
    Preferences Prefs;
    boolean Load = false;
    private Cursor Hourglass = new Cursor( Cursor.WAIT_CURSOR );
    private Cursor NormalCursor = new Cursor( Cursor.DEFAULT_CURSOR );
    ImageIcon FluffImage = Utils.createImageIcon( Constants.NO_IMAGE );
    DataFactory data = new DataFactory(CurMech);

    private dlgPrintBatchMechs BatchWindow = null;
    private dlgOpen dOpen = new dlgOpen(this, true);
    public ssw.Force.gui.frmForce dForce = new frmForce(this);

    final int BALLISTIC = 0,
              ENERGY = 1,
              MISSILE = 2,
              PHYSICAL = 3, 
              EQUIPMENT = 4,
              AMMUNITION = 6,
              SELECTED = 7,
              ARTILLERY = 5;

    /** Creates new form frmMain */
    public frmMain() {
        Prefs = Preferences.userNodeForPackage(this.getClass());

        // fix for NetBeans stupidity.
        pnlDamageChart = new DamageChart();

        initComponents();
        setViewToolbar(Prefs.getBoolean("ViewToolbar", true));
        setTitle( Constants.AppDescription + " " + Constants.Version );

        mnuUnallocateAll.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UnallocateAll();
            }
        });

        mnuMountRear.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MountRear();
            }
        });

        mnuInfoItem.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GetInfoOn();
            }
        });

        mnuArmorComponent.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed( java.awt.event.ActionEvent e) {
                ArmorComponent();
            }
        });

        mnuRemoveItem.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RemoveItemCritTab();
            }
        });

        mnuCrits.add( mnuInfoItem );
        mnuCrits.add( mnuMountRear );
        mnuCrits.add( mnuUnallocateAll );
        // not at all finished implementing this.  Need to add support for Omnis
        // as well as unallocating takes the armoring off.
        mnuCrits.add( mnuArmorComponent );
        mnuCrits.add( mnuRemoveItem );
        mnuArmorComponent.setVisible( false );

        mnuInfoPlacement.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                GetInfoOn();
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

        mnuPlacement.add( mnuInfoPlacement );
        mnuPlacement.add( mnuAuto );
        mnuPlacement.add( mnuSelective );

        try {
            OReader.ReadOptions( Constants.OptionsFileName, GlobalOptions );
        } catch( IOException e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "Could not access or modify the options file!\n" + e );
            dispose();
        }

        CurMech.Visit( new VMechFullRecalc() );

        // set the program options
        cmbRulesLevel.setSelectedIndex( GlobalOptions.DefaultRules );
        cmbMechEra.setSelectedIndex( GlobalOptions.DefaultEra );
        cmbTechBase.setSelectedIndex( GlobalOptions.DefaultTechbase );

        BuildChassisSelector();
        BuildEngineSelector();
        BuildGyroSelector();
        BuildCockpitSelector();
        BuildEnhancementSelector();
        BuildArmorSelector();
        BuildHeatsinkSelector();
        BuildJumpJetSelector();
        FixArmorSpinners();
        RefreshSummary();
        RefreshInfoPane();
        RefreshInternalPoints();
        SetLoadoutArrays();
        SetWeaponChoosers();
        cmbInternalType.setSelectedItem( Constants.DEFAULT_CHASSIS );
        cmbEngineType.setSelectedItem( Constants.DEFAULT_ENGINE );
        cmbGyroType.setSelectedItem( Constants.DEFAULT_GYRO );
        cmbCockpitType.setSelectedItem( Constants.DEFAULT_COCKPIT );
        cmbPhysEnhance.setSelectedItem( Constants.DEFAULT_ENHANCEMENT );
        cmbHeatSinkType.setSelectedIndex( GlobalOptions.DefaultHeatSinks );
        cmbJumpJetType.setSelectedItem( Constants.DEFAULT_JUMPJET );
        cmbArmorType.setSelectedItem( Constants.DEFAULT_ARMOR );
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
                    return ((abPlaceable) o).GetCritName();
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
                        Vector v = CurMech.GetLoadout().GetEquipment();
                        for( int i = 0; i < v.size(); i++ ) {
                            if( ((abPlaceable) v.get( i )).GetCritName().equals( a.GetCritName() ) ) {
                                ((abPlaceable) v.get( i )).SetManufacturer( (String) value );
                            }
                        }
                    fireTableDataChanged();
                }
            }
        } );

        // if the user wants, load the last mech.
        if( GlobalOptions.LoadLastMech ) { LoadMechFromPreferences(); }

        dOpen.LoadList();
        CurMech.SetChanged( false );
    }

    public OptionsReader GetOptionsReader() {
        return OReader;
    }

    public Options GetOptions() {
        return GlobalOptions;
    }

    private void SetWeaponChoosers() {
        // sets the weapon choosers up.  first, get the user's choices.

        // get the equipment lists for the choices.
        Weapons.RebuildPhysicals(CurMech);
        Equipment[ENERGY] = Weapons.GetEnergyWeapons( CurMech );
        Equipment[MISSILE] = Weapons.GetMissileWeapons( CurMech );
        Equipment[BALLISTIC] = Weapons.GetBallisticWeapons( CurMech );
        Equipment[PHYSICAL] = Weapons.GetPhysicalWeapons( CurMech );
        Equipment[ARTILLERY] = Weapons.GetArtillery( CurMech );
        if( Equipment[PHYSICAL] == null ) {
            Equipment[PHYSICAL] = new Object[] { " " };
        }
        Equipment[EQUIPMENT] = Equips.GetEquipment( CurMech );
        if( Equipment[EQUIPMENT] == null ) {
            Equipment[EQUIPMENT] = new Object[] { " " };
        }
        Equipment[AMMUNITION] = new Object[] { " " };
        if( CurMech.GetLoadout().GetNonCore().toArray().length <= 0 ) {
            Equipment[SELECTED] = new Object[] { " " };
        } else {
            Equipment[SELECTED] = CurMech.GetLoadout().GetNonCore().toArray();
        }
        if( Equipment[ARTILLERY] == null ) {
            Equipment[ARTILLERY] = new Object[] { " " };
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
        lstCritsToPlace.setListData( CurMech.GetLoadout().GetQueue() );
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
        int MaxWalk = (int) Math.floor( 400 / CurMech.GetTonnage());
        int CurWalk = CurMech.GetWalkingMP();

        // since this should only ever happen when the tonnage changes, we'll
        // deal with the mech's engine rating here.  Reset the Run MP label too
        if( CurWalk > MaxWalk ) { CurWalk = MaxWalk; }
        CurMech.GetEngine().SetRating( CurWalk * CurMech.GetTonnage() );
        lblRunMP.setText( "" + CurMech.GetRunningMP() );

        // reset the spinner model and we're done.
        spnWalkMP.setModel( new javax.swing.SpinnerNumberModel( CurWalk, 1, MaxWalk, 1) );
    }

    private void BuildChassisSelector() {
        // builds the structure selection box
        Vector list = new Vector();

        // get the structure states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetIntStruc().GetStates( CurMech.IsQuad() );
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( check[i].GetLookupName() );
            }
        }

        // turn the vector into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the structure chooser
        cmbInternalType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildEngineSelector() {
        // builds the engine selection box
        Vector list = new Vector();

        // get the engine states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetEngine().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( check[i].GetLookupName() );
            }
        }

        // turn the vector into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the engine chooser
        cmbEngineType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildGyroSelector() {
        // builds the gyro selection box
        Vector list = new Vector();

        // get the gyro states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetGyro().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( check[i].GetLookupName() );
            }
        }

        // turn the vector into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the gyro chooser
        cmbGyroType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildCockpitSelector() {
        // builds the structure selection box
        Vector list = new Vector();

        // get the structure states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetCockpit().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( check[i].GetLookupName() );
            }
        }

        // turn the vector into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the structure chooser
        cmbCockpitType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildEnhancementSelector() {
        // builds the physical enhancement selection box
        Vector list = new Vector();

        // get the enhancement states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetPhysEnhance().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( check[i].GetLookupName() );
            }
        }

        // turn the vector into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the physical enhancement chooser
        cmbPhysEnhance.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildJumpJetSelector() {
        // ensures that we can enable the Improved Jump Jets checkbox.
        Vector list = new Vector();

        // get the jump jet states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetJumpJets().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( check[i].GetLookupName() );
            }
        }

        // turn the vector into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the jump jet chooser
        cmbJumpJetType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
        if( temp.length > 0 ) {
            EnableJumpJets( true );
            cmbJumpJetType.setSelectedItem( CurMech.GetJumpJets().GetLookupName() );
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
            max = CurMech.GetRunningMP();
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
    }

    private void BuildHeatsinkSelector() {
        // builds the heat sink selection box
        Vector list = new Vector();

        // get the heat sink states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetHeatSinks().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( check[i].GetLookupName() );
            }
        }

        // turn the vector into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the heat sink chooser
        cmbHeatSinkType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void BuildArmorSelector() {
        // builds the armor selection box
        Vector list = new Vector();

        // get the armor states and, for each that matches our criteria, add it
        // to the selector list
        ifState[] check = CurMech.GetArmor().GetStates();
        for( int i = 0; i < check.length; i++ ) {
            if( CommonTools.IsAllowed( check[i].GetAvailability(), CurMech ) ) {
                list.add( check[i].GetLookupName() );
            }
        }

        // turn the vector into a string array
        String[] temp = new String[list.size()];
        for( int i = 0; i < list.size(); i++ ) {
            temp[i] = (String) list.get(i);
        }

        // now set the armor chooser
        cmbArmorType.setModel( new javax.swing.DefaultComboBoxModel( temp ) );
    }

    private void RefreshEquipment() {
        // refreshes the equipment selectors

        // fix Artemis IV controls
        if( CurMech.IsClan() ) {
            if( CommonTools.IsAllowed( ArtemisIVFCS.CLAC, CurMech ) ) {
                chkArtemisSRM.setEnabled( true );
                chkArtemisLRM.setEnabled( true );
                chkArtemisMML.setSelected( false );
                chkArtemisMML.setEnabled( false );
            } else {
                chkArtemisSRM.setSelected( false );
                chkArtemisSRM.setEnabled( false );
                chkArtemisLRM.setSelected( false );
                chkArtemisLRM.setEnabled( false );
                chkArtemisMML.setSelected( false );
                chkArtemisMML.setEnabled( false );
            }
        } else {
            if( CommonTools.IsAllowed( ArtemisIVFCS.ISAC, CurMech ) ) {
                chkArtemisSRM.setEnabled( true );
                chkArtemisLRM.setEnabled( true );
                if( CommonTools.IsAllowed( Weapons.GetMissileWeaponByName( "MML-3", false ).GetAvailability(), CurMech ) ) {
                    chkArtemisMML.setEnabled( true );
                } else {
                    chkArtemisMML.setSelected( false );
                    chkArtemisMML.setEnabled( false );
                }
            } else {
                chkArtemisSRM.setSelected( false );
                chkArtemisSRM.setEnabled( false );
                chkArtemisLRM.setSelected( false );
                chkArtemisLRM.setEnabled( false );
                chkArtemisMML.setSelected( false );
                chkArtemisMML.setEnabled( false );
            }
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

        // lastly, fix the CASE controls
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

        // fix armor component menu item
        if( CurMech.IsClan() ) {
            if( CommonTools.IsAllowed( abPlaceable.CLArmoredAC, CurMech ) )  {
                mnuArmorComponent.setVisible( true );
            } else {
                mnuArmorComponent.setVisible( false );
            }
        } else {
            if( CommonTools.IsAllowed( abPlaceable.ISArmoredAC, CurMech ) )  {
                mnuArmorComponent.setVisible( true );
            } else {
                mnuArmorComponent.setVisible( false );
            }
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
        if( CommonTools.IsAllowed( CurMech.GetLoadout().GetSupercharger().GetAvailability(), CurMech ) ) {
            chkSupercharger.setEnabled( true );
            cmbSCLoc.setEnabled( true );
            lblSupercharger.setEnabled( true );
        } else {
            chkSupercharger.setEnabled( false );
            cmbSCLoc.setEnabled( false );
            lblSupercharger.setEnabled( false );
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
                CurMech.GetLoadout().SetHDCASEII( false, -1 );
            } catch( Exception e ) {
                // no reason we should get exceptions when unallocating CASE.
                System.err.println( e.getMessage() );
            }
            try {
                chkCTCASE2.setEnabled( false );
                chkCTCASE2.setSelected( false );
                CurMech.GetLoadout().SetCTCASEII( false, -1 );
            } catch( Exception e ) {
                // no reason we should get exceptions when unallocating CASE.
                System.err.println( e.getMessage() );
            }
            try {
                chkLTCASE2.setEnabled( false );
                chkLTCASE2.setSelected( false );
                CurMech.GetLoadout().SetLTCASEII( false, -1 );
            } catch( Exception e ) {
                // no reason we should get exceptions when unallocating CASE.
                System.err.println( e.getMessage() );
            }
            try {
                chkRTCASE2.setEnabled( false );
                chkRTCASE2.setSelected( false );
                CurMech.GetLoadout().SetRTCASEII( false, -1 );
            } catch( Exception e ) {
                // no reason we should get exceptions when unallocating CASE.
                System.err.println( e.getMessage() );
            }
            try {
                chkLACASE2.setEnabled( false );
                chkLACASE2.setSelected( false );
                CurMech.GetLoadout().SetLACASEII( false, -1 );
            } catch( Exception e ) {
                // no reason we should get exceptions when unallocating CASE.
                System.err.println( e.getMessage() );
            }
            try {
                chkRACASE2.setEnabled( false );
                chkRACASE2.setSelected( false );
                CurMech.GetLoadout().SetRACASEII( false, -1 );
            } catch( Exception e ) {
                // no reason we should get exceptions when unallocating CASE.
                System.err.println( e.getMessage() );
            }
            try {
                chkLLCASE2.setEnabled( false );
                chkLLCASE2.setSelected( false );
                CurMech.GetLoadout().SetLLCASEII( false, -1 );
            } catch( Exception e ) {
                // no reason we should get exceptions when unallocating CASE.
                System.err.println( e.getMessage() );
            }
            try {
                chkRLCASE2.setEnabled( false );
                chkRLCASE2.setSelected( false );
                CurMech.GetLoadout().SetRLCASEII( false, -1 );
            } catch( Exception e ) {
                // no reason we should get exceptions when unallocating CASE.
                System.err.println( e.getMessage() );
            }
        }

        // now set all the equipment if needed
        if( ! chkArtemisSRM.isEnabled() ) {
            try {
                CurMech.SetA4FCSSRM( false );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
            }
            chkArtemisSRM.setSelected( false );
        } else {
            if( CurMech.UsingA4SRM() ) {
                chkArtemisSRM.setSelected( true );
            } else {
                chkArtemisSRM.setSelected( false );
            }
        }
        if( ! chkArtemisLRM.isEnabled() ) {
            try {
                CurMech.SetA4FCSLRM( false );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
            }
            chkArtemisLRM.setSelected( false );
        } else {
            if( CurMech.UsingA4LRM() ) {
                chkArtemisLRM.setSelected( true );
            } else {
                chkArtemisLRM.setSelected( false );
            }
        }
        if( ! chkArtemisMML.isEnabled() ) {
            try {
                CurMech.SetA4FCSMML( false );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
            }
            chkArtemisMML.setSelected( false );
        } else {
            if( CurMech.UsingA4MML() ) {
                chkArtemisMML.setSelected( true );
            } else {
                chkArtemisMML.setSelected( false );
            }
        }
        if( ! chkSupercharger.isEnabled() ) {
            try {
                CurMech.GetLoadout().SetSupercharger( false, 0, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
            }
        } else {
            if( CurMech.GetLoadout().HasSupercharger() ) {
                chkSupercharger.setSelected( true );
            } else {
                chkSupercharger.setSelected( false );
            }
        }
        if( ! chkUseTC.isEnabled() ) { CurMech.UseTC( false ); }
        if( ! chkCTCASE.isEnabled() ) { CurMech.RemoveCTCase(); }
        if( ! chkLTCASE.isEnabled() ) { CurMech.RemoveLTCase(); }
        if( ! chkRTCASE.isEnabled() ) { CurMech.RemoveRTCase(); }

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
            } catch( Exception e ) {
                // we should never get this, but report it if we do
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
            }
        }
    }

    private void RecalcGyro() {
        // changes the armor type.
        String LookupVal = (String) cmbGyroType.getSelectedItem();
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        CurMech.Visit( v );
    }

    private void RecalcCockpit() {
        // recalculates the cockpit on the mech
        String LookupVal = (String) cmbCockpitType.getSelectedItem();
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        CurMech.Visit( v );
    }

    private void RecalcEnhancements() {
        // recalculates the enhancements on the mech
        String LookupVal = (String) cmbPhysEnhance.getSelectedItem();
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        CurMech.Visit( v );
    }

    private void RecalcJumpJets() {
        // recalculates the jump jets if things have changed.
        String LookupVal = (String) cmbJumpJetType.getSelectedItem();
        if( LookupVal == null ) { return; }
        if( LookupVal.equals( CurMech.GetJumpJets().GetLookupName() ) ) { return; }
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        CurMech.Visit( v );
        FixJJSpinnerModel();
    }

    private void RecalcHeatSinks() {
        // recalculate the heat sinks based on what is selected.
        String LookupVal = (String) cmbHeatSinkType.getSelectedItem();
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        CurMech.Visit( v );
        FixHeatSinkSpinnerModel();
    }

    private void RecalcIntStruc() {
        // recalculates the internal structure if anything happened.
        String LookupVal = (String) cmbInternalType.getSelectedItem();
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        CurMech.Visit( v );
    }

    private void RecalcEngine() {
        // first, get the current number of free heat sinks
        int OldFreeHS = CurMech.GetEngine().FreeHeatSinks();

        // changes the engine type.  Changing the type does not change the rating
        // which makes our job here easier.
        String LookupVal = (String) cmbEngineType.getSelectedItem();
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        CurMech.Visit( v );

        // now that the new engine is in, check the number of free sinks and act
        // accordingly
        if( CurMech.GetEngine().FreeHeatSinks() != OldFreeHS ) {
            // set the current number of heat sinks to the new free heat sinks
            CurMech.GetHeatSinks().SetNumHS( CurMech.GetEngine().FreeHeatSinks() );
        }

        // redo the heat sinks because the engine affects them
        CurMech.GetHeatSinks().ReCalculate();
        spnNumberOfHS.setModel( new javax.swing.SpinnerNumberModel(
            CurMech.GetHeatSinks().GetNumHS(), CurMech.GetEngine().FreeHeatSinks(), 65, 1) );

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
        String LookupVal = (String) cmbArmorType.getSelectedItem();
        ifVisitor v = (ifVisitor) CurMech.Lookup( LookupVal );
        CurMech.Visit( v );
    }

    private void RecalcEquipment() {
        // recalculates the equipment if anything changes
        if( chkCTCASE.isSelected() ) {
            try {
                CurMech.AddCTCase();
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkCTCASE.setSelected( false );
            }
        }
        if( chkLTCASE.isSelected() ) {
            try {
                CurMech.AddLTCase();
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkLTCASE.setSelected( false );
            }
        }
        if( chkRTCASE.isSelected() ) {
            try {
                CurMech.AddRTCase();
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkRTCASE.setSelected( false );
            }
        }

        if( chkHDCASE2.isSelected() ) {
            try {
                CurMech.GetLoadout().SetHDCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkHDCASE2.setSelected( false );
            }
        }
        if( chkCTCASE2.isSelected() ) {
            try {
                CurMech.GetLoadout().SetCTCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkCTCASE2.setSelected( false );
            }
        }
        if( chkLTCASE2.isSelected() ) {
            try {
                CurMech.GetLoadout().SetLTCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkLTCASE2.setSelected( false );
            }
        }
        if( chkRTCASE2.isSelected() ) {
            try {
                CurMech.GetLoadout().SetRTCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkRTCASE2.setSelected( false );
            }
        }
        if( chkLACASE2.isSelected() ) {
            try {
                CurMech.GetLoadout().SetLACASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkLACASE2.setSelected( false );
            }
        }
        if( chkRACASE2.isSelected() ) {
            try {
                CurMech.GetLoadout().SetRACASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkRACASE2.setSelected( false );
            }
        }
        if( chkLLCASE2.isSelected() ) {
            try {
                CurMech.GetLoadout().SetLLCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkLLCASE2.setSelected( false );
            }
        }
        if( chkRLCASE2.isSelected() ) {
            try {
                CurMech.GetLoadout().SetRLCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkRLCASE2.setSelected( false );
            }
        }
    }

    private void CheckOmnimech() {
        // deals with the omnimech checkbox if needed
        int ProdYear = 0;
        if( ! txtProdYear.getText().isEmpty() ) {
            ProdYear = Integer.parseInt( txtProdYear.getText() );
        }

        if( chkYearRestrict.isSelected() ) {
            if( cmbTechBase.getSelectedIndex() == 1 ) {
                if( ProdYear >= 2854 ) {
                    if( CurMech.IsIndustrialmech() ) {
                        chkOmnimech.setEnabled( false );
                        chkOmnimech.setSelected( false );
                    } else {
                        chkOmnimech.setEnabled( true );
                    }
                } else {
                    chkOmnimech.setEnabled( false );
                    chkOmnimech.setSelected( false );
                }
            } else {
                if( ProdYear >= 3052 ) {
                    if( CurMech.IsIndustrialmech() ) {
                        chkOmnimech.setEnabled( false );
                        chkOmnimech.setSelected( false );
                    } else {
                        chkOmnimech.setEnabled( true );
                    }
                } else {
                    chkOmnimech.setEnabled( false );
                    chkOmnimech.setSelected( false );
                }
            }
        } else {
            switch( cmbMechEra.getSelectedIndex() ) {
                case 0:
                    chkOmnimech.setEnabled( false );
                    chkOmnimech.setSelected( false );
                    break;
                case 1:
                    if( cmbTechBase.getSelectedIndex() == 1 ) {
                        if( CurMech.IsIndustrialmech() ) {
                            chkOmnimech.setEnabled( false );
                            chkOmnimech.setSelected( false );
                        } else {
                            chkOmnimech.setEnabled( true );
                        }
                    } else {
                        chkOmnimech.setEnabled( false );
                        chkOmnimech.setSelected( false );
                    }
                    break;
                case 2:
                    if( CurMech.IsIndustrialmech() ) {
                        chkOmnimech.setEnabled( false );
                        chkOmnimech.setSelected( false );
                    } else {
                        chkOmnimech.setEnabled( true );
                    }
                    break;
                case 3:
                    if( CurMech.IsIndustrialmech() ) {
                        chkOmnimech.setEnabled( false );
                        chkOmnimech.setSelected( false );
                    } else {
                        chkOmnimech.setEnabled( true );
                    }
                    break;
            }
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

    private void RefreshInternalPoints() {
        lblHDIntPts.setText( "" + CurMech.GetIntStruc().GetHeadPoints() );
        lblCTIntPts.setText( "" + CurMech.GetIntStruc().GetCTPoints() );
        lblLTIntPts.setText( "" + CurMech.GetIntStruc().GetSidePoints() );
        lblRTIntPts.setText( "" + CurMech.GetIntStruc().GetSidePoints() );
        lblLAIntPts.setText( "" + CurMech.GetIntStruc().GetArmPoints() );
        lblRAIntPts.setText( "" + CurMech.GetIntStruc().GetArmPoints() );
        lblLLIntPts.setText( "" + CurMech.GetIntStruc().GetLegPoints() );
        lblRLIntPts.setText( "" + CurMech.GetIntStruc().GetLegPoints() );
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
        txtSumIntACode.setText( CurMech.GetIntStruc().GetAvailability().GetShortenedCode() );
        txtSumEngACode.setText( CurMech.GetEngine().GetAvailability().GetShortenedCode() );
        txtSumGyrACode.setText( CurMech.GetGyro().GetAvailability().GetShortenedCode() );
        txtSumCocACode.setText( CurMech.GetCockpit().GetAvailability().GetShortenedCode() );
        txtSumHSACode.setText( CurMech.GetHeatSinks().GetAvailability().GetShortenedCode() );
        txtSumEnhACode.setText( CurMech.GetPhysEnhance().GetAvailability().GetShortenedCode() );
        txtSumJJACode.setText( CurMech.GetJumpJets().GetAvailability().GetShortenedCode() );
        txtSumPAmpsACode.setText( CurMech.GetLoadout().GetPowerAmplifier().GetAvailability().GetShortenedCode() );

        // added for the armor pane
        lblArmorPoints.setText( CurMech.GetArmor().GetArmorValue() + " of " + CurMech.GetArmor().GetMaxArmor() + " Armor Points" );
        lblArmorCoverage.setText( CurMech.GetArmor().GetCoverage() + "% Coverage" );
        lblArmorTonsWasted.setText( CurMech.GetArmor().GetWastedTonnage() + " Tons Wasted" );
        lblAVInLot.setText( CurMech.GetArmor().GetWastedAV() + " Points Left In This 1/2 Ton Lot" );

        // added for Battleforce pane
        lblBFMV.setText( BattleForceTools.GetMovementString( CurMech ) );
        lblBFWt.setText( "" + CurMech.GetBFSize() );
        lblBFArmor.setText( "" + CurMech.GetBFArmor() );
        lblBFStructure.setText( "" + CurMech.GetBFStructure() );

        jTextAreaBFConversion.setText( CurMech.GetBFConversionStr( CurMech.UsingTC() ) );
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
        txtInfoTonnage.setText( "Tons: " + CurMech.GetCurrentTons() );
        txtInfoFreeTons.setText( "Free Tons: " + ( CurMech.GetTonnage() - CurMech.GetCurrentTons() ) );
        txtInfoMaxHeat.setText( "Max Heat: " + CurMech.GetMaxHeat() );
        txtInfoHeatDiss.setText( "Heat Dissipation: " + CurMech.GetHeatSinks().TotalDissipation() );
        txtInfoFreeCrits.setText( "Free Crits: " + CurMech.GetLoadout().FreeCrits() );
        txtInfoUnplaced.setText( "Unplaced Crits: " + CurMech.GetLoadout().UnplacedCrits() );
        txtInfoBattleValue.setText( "BV: " + String.format( "%1$,d", CurMech.GetCurrentBV() ) );
        txtInfoCost.setText( "Cost: " + String.format( "%1$,.0f", Math.floor( CurMech.GetTotalCost() + 0.5f ) ) );

        // fill in the movement summary
        String temp = "Max W/R/J/B: ";
        temp += CurMech.GetAdjustedWalkingMP( false, true ) + "/";
        temp += CurMech.GetAdjustedRunningMP( false, true ) + "/";
        temp += CurMech.GetAdjustedJumpingMP( false ) + "/";
        temp += "0";
        lblMoveSummary.setText( temp );

        // because the vector changes, we'll have to load up the Crits to Place list
        lstCritsToPlace.setListData( CurMech.GetLoadout().GetQueue() );
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
        javax.swing.table.AbstractTableModel m = (javax.swing.table.AbstractTableModel) tblWeaponManufacturers.getModel();
        m.fireTableDataChanged();

        CheckEquipment();

        UpdateBasicChart();
    }

    private void CheckEquipment() {
        // consolidating some code here.
        if( CurMech.UsingA4SRM() ) {
            chkArtemisSRM.setSelected( true );
        } else {
            chkArtemisSRM.setSelected( false );
        }
        if( CurMech.UsingA4LRM() ) {
            chkArtemisLRM.setSelected( true );
        } else {
            chkArtemisLRM.setSelected( false );
        }
        if( CurMech.UsingA4MML() ) {
            chkArtemisMML.setSelected( true );
        } else {
            chkArtemisMML.setSelected( false );
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

        if( CurMech.GetLoadout().HasSupercharger() ) {
            chkSupercharger.setSelected( true );
            cmbSCLoc.setSelectedItem( FileCommon.EncodeLocation( CurMech.GetLoadout().Find( CurMech.GetLoadout().GetSupercharger() ), false ) );
        } else {
            chkSupercharger.setSelected( false );
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
        Armor a = CurMech.GetArmor();
        spnHDArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_HD ), 0, 9, 1) );
        spnCTArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_CT ), 0, a.GetLocationMax( Constants.LOC_CT ), 1) );
        spnLTArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_LT ), 0, a.GetLocationMax( Constants.LOC_LT ), 1) );
        spnRTArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_RT ), 0, a.GetLocationMax( Constants.LOC_RT ), 1) );
        spnLAArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_LA ), 0, a.GetLocationMax( Constants.LOC_LA ), 1) );
        spnRAArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_RA ), 0, a.GetLocationMax( Constants.LOC_RA ), 1) );
        spnLLArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_LL ), 0, a.GetLocationMax( Constants.LOC_LL ), 1) );
        spnRLArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_RL ), 0, a.GetLocationMax( Constants.LOC_RL ), 1) );
        spnCTRArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_CTR ), 0, a.GetLocationMax( Constants.LOC_CT ), 1) );
        spnLTRArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_LTR ), 0, a.GetLocationMax( Constants.LOC_LT ), 1) );
        spnRTRArmor.setModel( new javax.swing.SpinnerNumberModel( a.GetLocationArmor( Constants.LOC_RTR ), 0, a.GetLocationMax( Constants.LOC_RT ), 1) );
    }

    private void SaveSelections() {
        // saves the current GUI selections
        Selections[0] = (String) cmbInternalType.getSelectedItem();
        Selections[1] = (String) cmbEngineType.getSelectedItem();
        Selections[2] = (String) cmbGyroType.getSelectedItem();
        Selections[3] = (String) cmbCockpitType.getSelectedItem();
        Selections[4] = (String) cmbPhysEnhance.getSelectedItem();
        Selections[5] = (String) cmbHeatSinkType.getSelectedItem();
        Selections[6] = (String) cmbJumpJetType.getSelectedItem();
        Selections[7] = (String) cmbArmorType.getSelectedItem();
        if( chkCTCASE.isSelected() ) {
            Selections[8] = "y";
        } else {
            Selections[8] = "n";
        }
        if( chkLTCASE.isSelected() ) {
            Selections[9] = "y";
        } else {
            Selections[9] = "n";
        }
        if( chkRTCASE.isSelected() ) {
            Selections[10] = "y";
        } else {
            Selections[10] = "n";
        }
    }

    private void LoadSelections() {
        // sets the current selections to the last saved selections or to the
        // default selections.

        cmbInternalType.setSelectedItem( Selections[0] );
        if( cmbInternalType.getSelectedItem() != Selections[0] ) {
            cmbInternalType.setSelectedItem( Constants.DEFAULT_CHASSIS );
        }
        cmbEngineType.setSelectedItem( Selections[1] );
        if( cmbEngineType.getSelectedItem() != Selections[1] ) {
            cmbEngineType.setSelectedItem( Constants.DEFAULT_ENGINE );
        }
        cmbGyroType.setSelectedItem( Selections[2] );
        if( cmbGyroType.getSelectedItem() != Selections[2] ) {
            cmbGyroType.setSelectedItem( Constants.DEFAULT_GYRO );
        }
        cmbCockpitType.setSelectedItem( Selections[3] );
        if( cmbCockpitType.getSelectedItem() != Selections[3] ) {
            cmbCockpitType.setSelectedItem( Constants.DEFAULT_COCKPIT );
        }
        cmbPhysEnhance.setSelectedItem( Selections[4] );
        if( cmbPhysEnhance.getSelectedItem() != Selections[4] ) {
            cmbPhysEnhance.setSelectedItem( Constants.DEFAULT_ENHANCEMENT );
        }
        cmbHeatSinkType.setSelectedItem( Selections[5] );
        if( cmbHeatSinkType.getSelectedItem() != Selections[5] ) {
            cmbHeatSinkType.setSelectedItem( Constants.DEFAULT_HEATSINK );
        }
        cmbJumpJetType.setSelectedItem( Selections[6] );
        if( cmbJumpJetType.getSelectedItem() != Selections[6] ) {
            cmbJumpJetType.setSelectedItem( Constants.DEFAULT_JUMPJET );
        }
        cmbArmorType.setSelectedItem( Selections[7] );
        if( cmbArmorType.getSelectedItem() != Selections[7] ) {
            cmbArmorType.setSelectedItem( Constants.DEFAULT_ARMOR );
        }
        if( Selections[8].equals( "y" ) ) {
            if( chkCTCASE.isEnabled() ) { chkCTCASE.setSelected( true ); }
        } else {
            chkCTCASE.setSelected( false );
        }
        if( Selections[9].equals( "y" ) ) {
            if( chkLTCASE.isEnabled() ) { chkLTCASE.setSelected( true ); }
        } else {
            chkLTCASE.setSelected( false );
        }
        if( Selections[10].equals( "y" ) ) {
            if( chkRTCASE.isEnabled() ) { chkRTCASE.setSelected( true ); }
        } else {
            chkRTCASE.setSelected( false );
        }
    }

    public void RevertToStandardArmor() {
        // convenience method for the armor visitor if Stealth Armor cannot be
        // installed.  This should only ever be called by Stealth Armor, so we
        // don't have to check whether we're in the right era or not.
        cmbArmorType.setSelectedItem( "Standard Armor" );
    }

    private void ResetAmmo() {
        // first, get the weapons from the loadout that need ammunition
        Vector v = CurMech.GetLoadout().GetNonCore(), wep = new Vector();
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
            result = Ammo.GetAmmo( key, CurMech );
        }

        // put the results into the chooser
        Equipment[AMMUNITION] = result;
        lstChooseAmmunition.setListData( result );
        lstChooseAmmunition.repaint();
    }

    private void SelectiveAllocate() {
        dlgSelectiveAllocate Selec = new dlgSelectiveAllocate( this, true, CurItem );
        Selec.setLocationRelativeTo( this );
        Selec.setVisible( true );
        RefreshSummary();
        RefreshInfoPane();
    }

    private void AutoAllocate() {
        CurMech.GetLoadout().AutoAllocate( CurItem );
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
        CurMech = new Mech( this );

        chkYearRestrict.setSelected( false );
        txtProdYear.setText( "" );
        cmbMechEra.setEnabled( true );
        cmbTechBase.setEnabled( true );
        txtProdYear.setEnabled( true );

        cmbRulesLevel.setSelectedIndex( GlobalOptions.DefaultRules );
        cmbMechEra.setSelectedIndex( GlobalOptions.DefaultEra );
        cmbTechBase.setSelectedIndex( GlobalOptions.DefaultTechbase );

        if( Omni ) {
            UnlockGUIFromOmni();
        }

        CurMech.SetEra( cmbMechEra.getSelectedIndex() );
        CurMech.SetRulesLevel( cmbRulesLevel.getSelectedIndex() );
        switch( CurMech.GetEra() ) {
        case Constants.STAR_LEAGUE:
            CurMech.SetYear( 2750, false );
            break;
        case Constants.SUCCESSION:
            CurMech.SetYear( 3025, false );
            break;
        case Constants.CLAN_INVASION:
            CurMech.SetYear( 3070, false );
            break;
        case Constants.ALL_ERA:
            CurMech.SetYear( 0, false );
            break;
        }
        if( cmbTechBase.getSelectedIndex() == Constants.CLAN ) {
            CurMech.SetClan();
        } else {
            CurMech.SetInnerSphere();
        }
        if( CurMech.IsIndustrialmech() ) {
            cmbMechType.setSelectedIndex( 1 );
        } else {
            cmbMechType.setSelectedIndex( 0 );
        }
        txtMechName.setText( CurMech.GetName() );
        txtMechModel.setText( CurMech.GetModel() );

        FixTransferHandlers();
        CurMech.Visit( new VMechFullRecalc() );

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
        cmbInternalType.setSelectedItem( Constants.DEFAULT_CHASSIS );
        cmbEngineType.setSelectedItem( Constants.DEFAULT_ENGINE );
        cmbGyroType.setSelectedItem( Constants.DEFAULT_GYRO );
        cmbCockpitType.setSelectedItem( Constants.DEFAULT_COCKPIT );
        cmbPhysEnhance.setSelectedItem( Constants.DEFAULT_ENHANCEMENT );
        cmbHeatSinkType.setSelectedIndex( GlobalOptions.DefaultHeatSinks );
        cmbJumpJetType.setSelectedItem( Constants.DEFAULT_JUMPJET );
        cmbArmorType.setSelectedItem( Constants.DEFAULT_ARMOR );
        FixWalkMPSpinner();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        RefreshInternalPoints();
        FixArmorSpinners();
        Weapons.RebuildPhysicals( CurMech );
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
        SetLoadoutArrays();
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
        ResetAmmo();

        edtOverview.setText( "" );
        edtCapabilities.setText( "" );
        edtHistory.setText( "" );
        edtDeployment.setText( "" );
        edtVariants.setText( "" );
        edtNotables.setText( "" );
        edtAdditionalFluff.setText( "" );
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

        if( cmbMechEra.getSelectedIndex() == Constants.ALL_ERA ) {
            chkYearRestrict.setEnabled( false );
        } else {
            chkYearRestrict.setEnabled( true );
        }
        CurMech.SetChanged( false );
        setTitle( Constants.AppDescription + " " + Constants.Version );
    }

    private void GetInfoOn() {
        // throws up a window detailing the current item
        if( CurItem instanceof ifWeapon ) {
            dlgWeaponInfo WepInfo = new dlgWeaponInfo( this, true );
            WepInfo.setLocationRelativeTo( this );
            WepInfo.setVisible( true );
        } else if( CurItem instanceof Ammunition ) {
            dlgAmmoInfo AmmoInfo = new dlgAmmoInfo( this, true );
            AmmoInfo.setLocationRelativeTo( this );
            AmmoInfo.setVisible( true );
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

        lblInfoAVSL.setText( "" + AC.GetSLCode() );
        lblInfoAVSW.setText( "" + AC.GetSWCode() );
        lblInfoAVCI.setText( "" + AC.GetCICode() );
        lblInfoIntro.setText( AC.GetIntroDate() + " (" + AC.GetIntroFaction() + ")" );
        if( AC.WentExtinct() ) {
            lblInfoExtinct.setText( "" + AC.GetExtinctDate() );
        } else {
            lblInfoExtinct.setText( "--" );
        }
        if( AC.WasReIntroduced() ) {
            lblInfoReintro.setText( AC.GetReIntroDate() + " (" + AC.GetReIntroFaction() + ")" );
        } else {
            lblInfoReintro.setText( "--" );
        }
        if( CurMech.IsIndustrialmech() ) {
            switch( AC.GetRulesLevelIM() ) {
                case Constants.TOURNAMENT:
                    lblInfoRulesLevel.setText( "Tournament" );
                    break;
                case Constants.ADVANCED:
                    lblInfoRulesLevel.setText( "Advanced" );
                    break;
                case Constants.EXPERIMENTAL:
                    lblInfoRulesLevel.setText( "Experimental" );
                    break;
                default:
                    lblInfoRulesLevel.setText( "??" );
            }
        } else {
            switch( AC.GetRulesLevelBM() ) {
                case Constants.TOURNAMENT:
                    lblInfoRulesLevel.setText( "Tournament" );
                    break;
                case Constants.ADVANCED:
                    lblInfoRulesLevel.setText( "Advanced" );
                    break;
                case Constants.EXPERIMENTAL:
                    lblInfoRulesLevel.setText( "Experimental" );
                    break;
                default:
                    lblInfoRulesLevel.setText( "??" );
            }
        }
        lblInfoName.setText( p.GetCritName() );
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
            } else if( w instanceof EnergyWeapon ) {
                if( ((EnergyWeapon) w).HasCapacitor() ) {
                    lblInfoHeat.setText( w.GetHeat() + "*" );
                } else {
                    lblInfoHeat.setText( "" + w.GetHeat() );
                }
            } else {
                lblInfoHeat.setText( "" + w.GetHeat() );
            }
            if( w instanceof MissileWeapon ) {
                lblInfoDamage.setText( w.GetDamageShort() + "/msl");
            } else if( w.GetDamageShort() == w.GetDamageMedium() && w.GetDamageShort() == w.GetDamageLong() ) {
                if( w instanceof BallisticWeapon ) {
                    if( w.IsUltra() || w.IsRotary() ) {
                        lblInfoDamage.setText( w.GetDamageShort() + "/shot" );
                    } else {
                        lblInfoDamage.setText( "" + w.GetDamageShort() );
                    }
                } else if( w instanceof EnergyWeapon ) {
                    if( ((EnergyWeapon) w).HasCapacitor() ) {
                        lblInfoDamage.setText( w.GetDamageShort() + "*" );
                    } else {
                        lblInfoDamage.setText( "" + w.GetDamageShort() );
                    }
                } else {
                    lblInfoDamage.setText( "" + w.GetDamageShort() );
                }
            } else {
                if( w instanceof EnergyWeapon ) {
                    if( ((EnergyWeapon) w).HasCapacitor() ) {
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
                    if( w instanceof Artillery ) {
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
                lblInfoAmmo.setText( "" + w.GetAmmo() );
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
            if( a.ClusterSize() > 1 ) {
                lblInfoDamage.setText( a.GetDamage() + "/hit" );
            } else {
                lblInfoDamage.setText( a.GetDamage() + "" );
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
            lblInfoMountRestrict.setText( restrict );
        } else {
            lblInfoMountRestrict.setText( "none" );
        }
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
            case 0:
                CurMech.SetYear( 2750, false );
                break;
            case 1:
                CurMech.SetYear( 3025, false );
                break;
            case 2:
                CurMech.SetYear( 3070, false );
                break;
            }
        } else {
            try{
                year = Integer.parseInt( txtProdYear.getText() ) ;
                CurMech.SetYear( year, true );
            } catch( NumberFormatException n ) {
                javax.swing.JOptionPane.showMessageDialog( this, "The production year is not a number." );
                tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
                return;
            }
        }

        CurMech.SetOverview( edtOverview.getText() );
        CurMech.SetCapabilities( edtCapabilities.getText() );
        CurMech.SetHistory( edtHistory.getText() );
        CurMech.SetDeployment( edtDeployment.getText() );
        CurMech.SetVariants( edtVariants.getText() );
        CurMech.SetNotables( edtNotables.getText() );
        CurMech.SetAdditional( edtAdditionalFluff.getText() );
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
            cmbJumpJetType.setEnabled( true );
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
        //cmbRulesLevel.setEnabled( false );
        cmbMechEra.setEnabled( false );
        cmbTechBase.setEnabled( false );
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
        if( CurMech.GetJumpJets().GetNumJJ() > 0 ) {
            cmbJumpJetType.setEnabled( false );
        }
        spnWalkMP.setEnabled( false );
        if( chkArtemisSRM.isSelected() ) {
            chkArtemisSRM.setEnabled( false );
        }
        if( chkArtemisLRM.isSelected() ) {
            chkArtemisLRM.setEnabled( false );
        }
        if( chkArtemisMML.isSelected() ) {
            chkArtemisMML.setEnabled( false );
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
    }

    private void UnlockGUIFromOmni() {
        // this should be used anytime a new mech is made or when unlocking
        // an omnimech.
        chkOmnimech.setSelected( false );
        chkOmnimech.setEnabled( true );
        //cmbRulesLevel.setEnabled( true );
        cmbMechEra.setEnabled( true );
        cmbTechBase.setEnabled( true );
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
        chkArtemisSRM.setEnabled( true );
        chkArtemisLRM.setEnabled( true );
        chkArtemisMML.setEnabled( true );
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
    }

    private void RefreshOmniVariants() {
        Vector v = CurMech.GetLoadouts();
        String[] variants = new String[v.size()];
        if( v.size() <= 0 ) {
            variants = new String[] { Constants.BASELOADOUT_NAME };
        } else {
            for( int i = 0; i < v.size(); i++ ) {
                variants[i] = ((ifLoadout) v.get(i)).GetName();
            }
        }

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
        if( CurMech.IsOmnimech() ) {
            CurLoadout = CurMech.GetLoadout().GetName();
        }

        // Ensure the mech has a name
        if( CurMech.GetName().isEmpty() ) {
            javax.swing.JOptionPane.showMessageDialog( this, "Your mech needs a name first." );
            tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
            txtMechName.requestFocusInWindow();
            return false;
        }

        // ensure there are no unplaced crits
        if( CurMech.IsOmnimech() ) {
            Vector v = CurMech.GetLoadouts();
            for( int i = 0; i < v.size(); i++ ) {
                CurMech.SetCurLoadout( ((ifLoadout) v.get( i )).GetName() );
                if( CurMech.GetLoadout().GetQueue().size() != 0 ) {
                    javax.swing.JOptionPane.showMessageDialog( this, "You must place all items in the " +
                        ((ifLoadout) v.get( i )).GetName() + " loadout first." );
                    cmbOmniVariant.setSelectedItem( ((ifLoadout) v.get( i )).GetName() );
                    cmbOmniVariantActionPerformed( evt );
                    tbpMainTabPane.setSelectedComponent( pnlCriticals );
                    return false;
                }
            }
        } else {
            if( CurMech.GetLoadout().GetQueue().size() != 0 ) {
                javax.swing.JOptionPane.showMessageDialog( this, "You must place all items first." );
                tbpMainTabPane.setSelectedComponent( pnlCriticals );
                return false;
            }
        }

        // ensure we're not overweight
        if( CurMech.IsOmnimech() ) {
            Vector v = CurMech.GetLoadouts();
            for( int i = 0; i < v.size(); i++ ) {
                CurMech.SetCurLoadout( ((ifLoadout) v.get( i )).GetName() );
                if( CurMech.GetCurrentTons() > CurMech.GetTonnage() ) {
                    javax.swing.JOptionPane.showMessageDialog( this, ((ifLoadout) v.get( i )).GetName() +
                        " loadout is overweight.  Reduce the weight\nto equal or below the mech's tonnage before exporting." );
                    cmbOmniVariant.setSelectedItem( ((ifLoadout) v.get( i )).GetName() );
                    cmbOmniVariantActionPerformed( evt );
                    tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
                    return false;
                }
            }
        } else {
            if( CurMech.GetCurrentTons() > CurMech.GetTonnage() ) {
                javax.swing.JOptionPane.showMessageDialog( this, "This mech is overweight.  Reduce the weight to\nequal or below the mech's tonnage before exporting." );
                tbpMainTabPane.setSelectedComponent( pnlBasicSetup );
                return false;
            }
        }
        if( CurMech.IsOmnimech() ) {
            cmbOmniVariant.setSelectedItem( CurLoadout );
        }
        return true;
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

    public boolean LegalArmoring( abPlaceable p ) {
        // This tells us whether it is legal to armor a particular component
        if( p.CanArmor() ) {
            if( CurMech.GetLoadout().GetName().equals( Constants.BASELOADOUT_NAME ) ) {
                return true;
            } else {
                if( p instanceof Engine ) { return false; }
                if( p instanceof Gyro ) { return false; }
                if( p instanceof Cockpit ) { return false; }
                if( p instanceof Actuator ) { return false; }
                if( p instanceof SimplePlaceable ) { return false; }
                if( CurMech.GetBaseLoadout().GetNonCore().contains( p ) ) { return false; }
                return true;
            }
        } else {
            return false;
        }
    }

    private void PrintMech( Mech m) {
        Printer printer = new Printer(this);
        printer.Print(m);
    }

    private void UpdateBasicChart() {
        int[] fchart = GetFrontDamageChart();
        int[] lchart = GetLeftArmDamageChart();
        int[] rchart = GetRightArmDamageChart();
        int[] rrchart = GetRearDamageChart();
        int gridx = 1;
        int gridy = 1;
        for( int i = 0; i < fchart.length; i++ ) {
            if( fchart[i] > 0 ) {
                if( fchart[i] > gridy ) { gridy = fchart[i]; }
                gridx = i;
            }
            if( rchart[i] > 0 ) {
                if( rchart[i] > gridy ) { gridy = rchart[i]; }
                gridx = i;
            }
            if( lchart[i] > 0 ) {
                if( lchart[i] > gridy ) { gridy = lchart[i]; }
                gridx = i;
            }
            if( rrchart[i] > 0 ) {
                if( rrchart[i] > gridy ) { gridy = rrchart[i]; }
                gridx = i;
            }
        }
        Vector v = CurMech.GetLoadout().GetNonCore();
        int TotalDamage = 0;
        float TonsWeapons = 0.0f, TonsEquips = 0.0f;

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
        ((DamageChart) pnlDamageChart).ClearCharts();
        ((DamageChart) pnlDamageChart).SetGridSize( gridx + 1, gridy + 1 );
        ((DamageChart) pnlDamageChart).AddChart( rrchart, Color.PINK );
        ((DamageChart) pnlDamageChart).AddChart( lchart, Color.ORANGE );
        ((DamageChart) pnlDamageChart).AddChart( rchart, Color.GREEN );
        ((DamageChart) pnlDamageChart).AddChart( fchart, Color.RED );
        lblTonPercStructure.setText( String.format( "%1$,.2f", ( CurMech.GetIntStruc().GetTonnage() + CurMech.GetCockpit().GetTonnage() + CurMech.GetGyro().GetTonnage() ) / CurMech.GetTonnage() * 100.0f ) + "%" );
        lblTonPercEngine.setText( String.format( "%1$,.2f", CurMech.GetEngine().GetTonnage() / CurMech.GetTonnage() * 100.0f ) + "%" );
        lblTonPercHeatSinks.setText( String.format( "%1$,.2f", CurMech.GetHeatSinks().GetTonnage() / CurMech.GetTonnage() * 100.0f ) + "%" );
        lblTonPercEnhance.setText( String.format( "%1$,.2f", CurMech.GetPhysEnhance().GetTonnage() / CurMech.GetTonnage() * 100.0f ) + "%" );
        lblTonPercArmor.setText( String.format( "%1$,.2f", CurMech.GetArmor().GetTonnage() / CurMech.GetTonnage() * 100.0f ) + "%" );
        lblTonPercJumpJets.setText( String.format( "%1$,.2f", CurMech.GetJumpJets().GetTonnage() / CurMech.GetTonnage() * 100.0f ) + "%" );
        lblTonPercWeapons.setText( String.format( "%1$,.2f", TonsWeapons / CurMech.GetTonnage() * 100.0f ) + "%" );
        lblTonPercEquips.setText( String.format( "%1$,.2f", TonsEquips / CurMech.GetTonnage() * 100.0f ) + "%" );
        lblDamagePerTon.setText( String.format( "%1$,.2f", (float) TotalDamage / CurMech.GetTonnage() ) );
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
            if( w instanceof MissileWeapon ) {
                return w.GetDamageLong() * mult * w.ClusterSize();
            } else {
                return w.GetDamageLong() * mult;
            }
        }
        if( w.GetDamageMedium() >= w.GetDamageLong() && w.GetDamageMedium() >= w.GetDamageShort() ) {
            if( w instanceof MissileWeapon ) {
                return w.GetDamageMedium() * mult * w.ClusterSize();
            } else {
                return w.GetDamageMedium() * mult;
            }
        }
        if( w instanceof MissileWeapon ) {
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
        if( list == lstHDCrits ) { return Constants.LOC_HD; }
        if( list == lstCTCrits ) { return Constants.LOC_CT; }
        if( list == lstLTCrits ) { return Constants.LOC_LT; }
        if( list == lstRTCrits ) { return Constants.LOC_RT; }
        if( list == lstLACrits ) { return Constants.LOC_LA; }
        if( list == lstRACrits ) { return Constants.LOC_RA; }
        if( list == lstLLCrits ) { return Constants.LOC_LL; }
        if( list == lstRLCrits ) { return Constants.LOC_RL; }
        return -1;
    }

    // check the tonnage to see if it's legal and acts accordingly
    public void CheckTonnage( boolean RulesChange ) {
        if( RulesChange ) {
            if( ! CurMech.IsIndustrialmech() ) {
                if( CurMech.GetRulesLevel() < Constants.EXPERIMENTAL && CurMech.GetTonnage() < 20 ) {
                    cmbTonnage.setSelectedItem( "20" );
                }
            }
        } else {
            // a change in mech type or tonnage
            if( ! CurMech.IsIndustrialmech() ) {
                // this is really the only time tonnage needs to be restricted
                if( CurMech.GetRulesLevel() < Constants.EXPERIMENTAL && CurMech.GetTonnage() < 20 ) {
                    if( CurMech.GetTonnage() < 20 ) {
                        cmbRulesLevel.setSelectedIndex( Constants.EXPERIMENTAL );
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
        Vector v = CurMech.GetLoadout().GetNonCore();
        ifWeapon w;
        for( int i = 0; i < 40; i++ ) {
            for( int j = 0; j < v.size(); j++ ) {
                if( v.get( j ) instanceof ifWeapon ) {
                    w = (ifWeapon) v.get( j );
                    if( ! ((abPlaceable) w).IsMountedRear() ) {
                        chart[i] += GetDamageAtRange( w, i );
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
        Vector v = CurMech.GetLoadout().GetNonCore();
        ifWeapon w;
        for( int i = 0; i < 40; i++ ) {
            for( int j = 0; j < v.size(); j++ ) {
                if( v.get( j ) instanceof ifWeapon ) {
                    w = (ifWeapon) v.get( j );
                    if( CurMech.GetLoadout().Find( (abPlaceable) w ) == Constants.LOC_RA ) {
                        chart[i] += GetDamageAtRange( w, i );
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
        Vector v = CurMech.GetLoadout().GetNonCore();
        ifWeapon w;
        for( int i = 0; i < 40; i++ ) {
            for( int j = 0; j < v.size(); j++ ) {
                if( v.get( j ) instanceof ifWeapon ) {
                    w = (ifWeapon) v.get( j );
                    if( CurMech.GetLoadout().Find( (abPlaceable) w ) == Constants.LOC_LA ) {
                        chart[i] += GetDamageAtRange( w, i );
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
        Vector v = CurMech.GetLoadout().GetNonCore();
        boolean flip = ! ( CurMech.GetLoadout().GetActuators().LeftLowerInstalled() && CurMech.GetLoadout().GetActuators().RightLowerInstalled() );
        ifWeapon w;
        for( int i = 0; i < 40; i++ ) {
            for( int j = 0; j < v.size(); j++ ) {
                if( v.get( j ) instanceof ifWeapon ) {
                    w = (ifWeapon) v.get( j );
                    int Loc = CurMech.GetLoadout().Find( (abPlaceable) w );
                    if( ((abPlaceable) w).IsMountedRear() || (( Loc == Constants.LOC_LA || Loc == Constants.LOC_RA ) && flip ) ) {
                        chart[i] += GetDamageAtRange( w, i );
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
        if( w.GetRangeLong() <= 0 ) {
            if( w.GetRangeMedium() <= 0 ) {
                if( range <= w.GetRangeShort() ) {
                    if( w instanceof MissileWeapon ) {
                        return w.GetDamageShort() * mult * w.ClusterSize();
                    } else {
                        return w.GetDamageShort() * mult;
                    }
                } else {
                    return 0;
                }
            } else {
                if( range <= w.GetRangeShort() ) {
                    if( w instanceof MissileWeapon ) {
                        return w.GetDamageShort() * mult * w.ClusterSize();
                    } else {
                        return w.GetDamageShort() * mult;
                    }
                } else if( range <= w.GetRangeMedium() ) {
                    if( w instanceof MissileWeapon ) {
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
                if( w instanceof MissileWeapon ) {
                    return w.GetDamageShort() * mult * w.ClusterSize();
                } else {
                    return w.GetDamageShort() * mult;
                }
            } else if( range <= w.GetRangeMedium() ) {
                if( w instanceof MissileWeapon ) {
                    return w.GetDamageMedium() * mult * w.ClusterSize();
                } else {
                    return w.GetDamageMedium() * mult;
                }
            } else if( range <= w.GetRangeLong() ) {
                if( w instanceof MissileWeapon ) {
                    return w.GetDamageLong() * mult * w.ClusterSize();
                } else {
                    return w.GetDamageLong() * mult;
                }
            } else {
                return 0;
            }
        }
    }

     /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane25 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        tlbIconBar = new javax.swing.JToolBar();
        btnNewIcon = new javax.swing.JButton();
        btnOpen = new javax.swing.JButton();
        btnSaveIcon = new javax.swing.JButton();
        btnPrintPreview = new javax.swing.JButton();
        jSeparator24 = new javax.swing.JToolBar.Separator();
        btnPrintIcon = new javax.swing.JButton();
        btnPrintSaved = new javax.swing.JButton();
        btnPrintBatch = new javax.swing.JButton();
        jSeparator22 = new javax.swing.JToolBar.Separator();
        btnExportClipboardIcon = new javax.swing.JButton();
        btnExportHTMLIcon = new javax.swing.JButton();
        btnExportTextIcon = new javax.swing.JButton();
        btnExportMTFIcon = new javax.swing.JButton();
        jSeparator23 = new javax.swing.JToolBar.Separator();
        btnPostToS7 = new javax.swing.JButton();
        jSeparator25 = new javax.swing.JToolBar.Separator();
        btnAddToForceList = new javax.swing.JButton();
        btnForceList = new javax.swing.JButton();
        jSeparator26 = new javax.swing.JToolBar.Separator();
        btnOptionsIcon = new javax.swing.JButton();
        jSeparator21 = new javax.swing.JToolBar.Separator();
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
        pnlOmniInfo = new javax.swing.JPanel();
        btnLockChassis = new javax.swing.JButton();
        btnAddVariant = new javax.swing.JButton();
        btnDeleteVariant = new javax.swing.JButton();
        lblSelectVariant = new javax.swing.JLabel();
        cmbOmniVariant = new javax.swing.JComboBox();
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
        chkArtemisSRM = new javax.swing.JCheckBox();
        jLabel16 = new javax.swing.JLabel();
        chkArtemisLRM = new javax.swing.JCheckBox();
        chkArtemisMML = new javax.swing.JCheckBox();
        chkUseTC = new javax.swing.JCheckBox();
        jLabel63 = new javax.swing.JLabel();
        chkArtemisVSRM = new javax.swing.JCheckBox();
        chkArtemisVLRM = new javax.swing.JCheckBox();
        chkMRMApollo = new javax.swing.JCheckBox();
        pnlSelected = new javax.swing.JPanel();
        jScrollPane23 = new javax.swing.JScrollPane();
        lstSelectedEquipment = new javax.swing.JList();
        pnlControls = new javax.swing.JPanel();
        btnRemoveEquip = new javax.swing.JButton();
        btnClearEquip = new javax.swing.JButton();
        btnAddEquip = new javax.swing.JButton();
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
        tbpFluffEditors = new javax.swing.JTabbedPane();
        pnlOverview = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        edtOverview = new javax.swing.JEditorPane();
        pnlCapabilities = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        edtCapabilities = new javax.swing.JEditorPane();
        pnlHistory = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        edtHistory = new javax.swing.JEditorPane();
        pnlDeployment = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        edtDeployment = new javax.swing.JEditorPane();
        pnlVariants = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        edtVariants = new javax.swing.JEditorPane();
        pnlNotables = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        edtNotables = new javax.swing.JEditorPane();
        pnlAdditionalFluff = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        edtAdditionalFluff = new javax.swing.JEditorPane();
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
        pnlExport = new javax.swing.JPanel();
        btnExportTXT = new javax.swing.JButton();
        btnExportHTML = new javax.swing.JButton();
        btnExportMTF = new javax.swing.JButton();
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
        lblFALegend = new javax.swing.JLabel();
        lblRALegened = new javax.swing.JLabel();
        lblRAALegend = new javax.swing.JLabel();
        lblLAALegend = new javax.swing.JLabel();
        lblLegendNote = new javax.swing.JLabel();
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
        jSeparator16 = new javax.swing.JSeparator();
        mnuSave = new javax.swing.JMenuItem();
        mnuSaveAs = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        mnuExportHTML = new javax.swing.JMenuItem();
        mnuExportMTF = new javax.swing.JMenuItem();
        mnuExportTXT = new javax.swing.JMenuItem();
        mnuExportClipboard = new javax.swing.JMenuItem();
        mnuCreateTCGMech = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JSeparator();
        jMenu2 = new javax.swing.JMenu();
        mnuPrintCurrentMech = new javax.swing.JMenuItem();
        mnuPrintSavedMech = new javax.swing.JMenuItem();
        mnuPrintBatch = new javax.swing.JMenuItem();
        mnuPrintPreview = new javax.swing.JMenuItem();
        mnuPostS7 = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JSeparator();
        mnuExit = new javax.swing.JMenuItem();
        mnuTools = new javax.swing.JMenu();
        mnuSummary = new javax.swing.JMenuItem();
        mnuCostBVBreakdown = new javax.swing.JMenuItem();
        mnuTextTRO = new javax.swing.JMenuItem();
        mnuUnlock = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JSeparator();
        mnuOptions = new javax.swing.JMenuItem();
        mnuViewToolbar = new javax.swing.JCheckBoxMenuItem();
        mnuClearUserData = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mnuCredits = new javax.swing.JMenuItem();
        mnuAboutSSW = new javax.swing.JMenuItem();

        jScrollPane25.setViewportView(jEditorPane1);

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

        btnNewIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/page_new.gif"))); // NOI18N
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

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/folder.gif"))); // NOI18N
        btnOpen.setToolTipText("Load Mech");
        btnOpen.setFocusable(false);
        btnOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnOpen);

        btnSaveIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_save.gif"))); // NOI18N
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

        btnPrintPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/image.gif"))); // NOI18N
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

        btnPrintIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_print.gif"))); // NOI18N
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

        btnPrintSaved.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_print_saved.gif"))); // NOI18N
        btnPrintSaved.setToolTipText("Print Saved Mech");
        btnPrintSaved.setFocusable(false);
        btnPrintSaved.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrintSaved.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintSaved.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintSavedActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnPrintSaved);

        btnPrintBatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/action_print_batch.gif"))); // NOI18N
        btnPrintBatch.setToolTipText("Print Batch");
        btnPrintBatch.setFocusable(false);
        btnPrintBatch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrintBatch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintBatchActionPerformed(evt);
            }
        });
        tlbIconBar.add(btnPrintBatch);
        tlbIconBar.add(jSeparator22);

        btnExportClipboardIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/page_tag_blue.gif"))); // NOI18N
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

        btnExportHTMLIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/page_code.gif"))); // NOI18N
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

        btnExportTextIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/page_edit.gif"))); // NOI18N
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

        btnExportMTFIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/page_favourites.gif"))); // NOI18N
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
        tlbIconBar.add(jSeparator23);

        btnPostToS7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/icon_world.gif"))); // NOI18N
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

        btnAddToForceList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/mech_add.gif"))); // NOI18N
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

        btnForceList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/mech.gif"))); // NOI18N
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

        btnOptionsIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/icon_settings.gif"))); // NOI18N
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
        pnlBasicInformation.add(txtMechName, new java.awt.GridBagConstraints());

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
        pnlBasicInformation.add(txtMechModel, gridBagConstraints);

        lblMechEra.setText("Era:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicInformation.add(lblMechEra, gridBagConstraints);

        cmbMechEra.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Age of War/Star League", "Succession Wars", "Clan Invasion", "All Eras (non-canon)" }));
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
        pnlBasicInformation.add(cmbMechEra, gridBagConstraints);

        lblEraYears.setText("2443~2800");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        pnlBasicInformation.add(lblEraYears, gridBagConstraints);

        lblProdYear.setText("Production Year:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlBasicInformation.add(lblProdYear, gridBagConstraints);

        txtProdYear.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtProdYear.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtProdYear.setMaximumSize(new java.awt.Dimension(150, 20));
        txtProdYear.setMinimumSize(new java.awt.Dimension(150, 20));
        txtProdYear.setPreferredSize(new java.awt.Dimension(150, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        pnlBasicInformation.add(txtProdYear, gridBagConstraints);

        chkYearRestrict.setText("Restrict Availability by Year");
        chkYearRestrict.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkYearRestrictActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
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
        pnlBasicInformation.add(cmbTechBase, gridBagConstraints);

        cmbRulesLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tournament Legal", "Advanced Rules", "Experimental Tech" }));
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
        pnlBasicInformation.add(txtSource, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
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
        gridBagConstraints.gridy = 8;
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
        gridBagConstraints.gridy = 8;
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

        cmbMechType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Battlemech", "IndustrialMech" }));
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
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
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        pnlMovement.add(spnBoosterMP, gridBagConstraints);

        chkBoosters.setText("'Mech Jump Boosters");
        chkBoosters.setEnabled(false);
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlBasicSetup.add(pnlMovement, gridBagConstraints);

        pnlOmniInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Omnimech Configuration"));
        pnlOmniInfo.setLayout(new java.awt.GridBagLayout());

        btnLockChassis.setText("Lock Chassis");
        btnLockChassis.setEnabled(false);
        btnLockChassis.setMaximumSize(new java.awt.Dimension(105, 23));
        btnLockChassis.setMinimumSize(new java.awt.Dimension(105, 23));
        btnLockChassis.setPreferredSize(new java.awt.Dimension(105, 23));
        btnLockChassis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLockChassisActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        pnlOmniInfo.add(btnLockChassis, gridBagConstraints);

        btnAddVariant.setText("Add");
        btnAddVariant.setEnabled(false);
        btnAddVariant.setMaximumSize(new java.awt.Dimension(80, 23));
        btnAddVariant.setMinimumSize(new java.awt.Dimension(80, 23));
        btnAddVariant.setPreferredSize(new java.awt.Dimension(80, 23));
        btnAddVariant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddVariantActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlOmniInfo.add(btnAddVariant, gridBagConstraints);

        btnDeleteVariant.setText("Delete");
        btnDeleteVariant.setEnabled(false);
        btnDeleteVariant.setMaximumSize(new java.awt.Dimension(80, 23));
        btnDeleteVariant.setMinimumSize(new java.awt.Dimension(80, 23));
        btnDeleteVariant.setPreferredSize(new java.awt.Dimension(80, 23));
        btnDeleteVariant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteVariantActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        pnlOmniInfo.add(btnDeleteVariant, gridBagConstraints);

        lblSelectVariant.setText("Selected Variant:");
        lblSelectVariant.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        pnlOmniInfo.add(lblSelectVariant, gridBagConstraints);

        cmbOmniVariant.setEnabled(false);
        cmbOmniVariant.setMaximumSize(new java.awt.Dimension(150, 20));
        cmbOmniVariant.setMinimumSize(new java.awt.Dimension(150, 20));
        cmbOmniVariant.setPreferredSize(new java.awt.Dimension(150, 20));
        cmbOmniVariant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbOmniVariantActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        pnlOmniInfo.add(cmbOmniVariant, gridBagConstraints);

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
        gridBagConstraints.gridy = 2;
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(chkBoobyTrap, gridBagConstraints);

        chkPartialWing.setText("Partial Wing");
        chkPartialWing.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(chkPartialWing, gridBagConstraints);

        chkFHES.setText("Full-Head Ejection System");
        chkFHES.setEnabled(false);
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
        gridBagConstraints.gridy = 2;
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlBasicSetup.add(jPanel6, gridBagConstraints);

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
        gridBagConstraints.gridheight = 2;
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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlArmor.add(pnlArmorSetup, gridBagConstraints);

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
                    int index = lstChooseBallistic.locationToIndex( e.getPoint() );
                    if( index < 0 ) { return; }
                    abPlaceable a = (abPlaceable) Equipment[BALLISTIC][index];
                    if( ((ifWeapon) a).RequiresNuclear() &! CurMech.GetEngine().IsNuclear() ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChooseBallistic.getTopLevelAncestor(), a.GetCritName() + " may not be mounted because the mech\ndoes not use a nuclear engine (fission or fusion)." );
                        return;
                    }
                    if( ((ifWeapon) a).RequiresFusion() &! CurMech.GetEngine().IsFusion() ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChooseBallistic.getTopLevelAncestor(), a.GetCritName() + " may not be mounted\nbecause the mech does not use a fusion engine." );
                        return;
                    }
                    a = Weapons.GetCopy( a );

                    // add it to the loadout
                    CurMech.GetLoadout().AddToQueue( a );

                    // unallocate the TC if needed (if the size changes)
                    if( a instanceof ifWeapon ) {
                        if( ((ifWeapon) a).IsTCCapable() && CurMech.UsingTC() ) {
                            CurMech.UnallocateTC();
                        }
                    }

                    // see if we need ammunition and add it if applicable
                    ResetAmmo();

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
                }
            }
        };
        lstChooseBallistic.addMouseListener( mlBallistic );
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
                    int index = lstChooseEnergy.locationToIndex( e.getPoint() );
                    if( index < 0 ) { return; }
                    abPlaceable a = (abPlaceable) Equipment[ENERGY][index];
                    if( ((ifWeapon) a).RequiresNuclear() &! CurMech.GetEngine().IsNuclear() ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChooseEnergy.getTopLevelAncestor(), a.GetCritName() + " may not be mounted because the mech\ndoes not use a nuclear engine (fission or fusion)." );
                        return;
                    }
                    if( ((ifWeapon) a).RequiresFusion() &! CurMech.GetEngine().IsFusion() ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChooseEnergy.getTopLevelAncestor(), a.GetCritName() + " may not be mounted\nbecause the mech does not use a fusion engine." );
                        return;
                    }
                    a = Weapons.GetCopy( a );

                    // add it to the loadout
                    CurMech.GetLoadout().AddToQueue( a );

                    // unallocate the TC if needed (if the size changes)
                    if( a instanceof ifWeapon ) {
                        if( ((ifWeapon) a).IsTCCapable() && CurMech.UsingTC() ) {
                            CurMech.UnallocateTC();
                        }
                    }

                    // see if we need ammunition and add it if applicable
                    ResetAmmo();

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
                }
            }
        };
        lstChooseEnergy.addMouseListener( mlEnergy );
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
                    int index = lstChooseMissile.locationToIndex( e.getPoint() );
                    if( index < 0 ) { return; }
                    abPlaceable a = (abPlaceable) Equipment[MISSILE][index];
                    if( ((ifWeapon) a).RequiresNuclear() &! CurMech.GetEngine().IsNuclear() ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChooseMissile.getTopLevelAncestor(), a.GetCritName() + " may not be mounted because the mech\ndoes not use a nuclear engine (fission or fusion)." );
                        return;
                    }
                    if( ((ifWeapon) a).RequiresFusion() &! CurMech.GetEngine().IsFusion() ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChooseMissile.getTopLevelAncestor(), a.GetCritName() + " may not be mounted\nbecause the mech does not use a fusion engine." );
                        return;
                    }
                    a = Weapons.GetCopy( a );
                    if( ((MissileWeapon) a).IsArtemisCapable() ) {
                        switch( ((MissileWeapon) a).GetArtemisType() ) {
                            case Constants.ART4_SRM:
                            if( CurMech.UsingA4SRM() ) {
                                ((MissileWeapon) a).UseArtemis( true );
                            }
                            break;
                            case Constants.ART4_LRM:
                            if( CurMech.UsingA4LRM() ) {
                                ((MissileWeapon) a).UseArtemis( true );
                            }
                            break;
                            case Constants.ART4_MML:
                            if( CurMech.UsingA4MML() ) {
                                ((MissileWeapon) a).UseArtemis( true );
                            }
                            break;
                        }
                    }

                    // add it to the loadout
                    CurMech.GetLoadout().AddToQueue( a );

                    // see if we need ammunition and add it if applicable
                    ResetAmmo();

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
                }
            }
        };
        lstChooseMissile.addMouseListener( mlMissile );
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
                    int index = lstChoosePhysical.locationToIndex( e.getPoint() );
                    if( index < 0 ) { return; }
                    abPlaceable a = (abPlaceable) Equipment[PHYSICAL][index];
                    a = Weapons.GetCopy( a );

                    // check to ensure that no more than two physical weapons are in the mech
                    Vector v = CurMech.GetLoadout().GetNonCore();
                    int pcheck = 0;
                    for( int i = 0; i < v.size(); i++ ) {
                        if( v.get( i ) instanceof PhysicalWeapon ) {
                            pcheck++;
                        }
                    }
                    if( pcheck >= 2 ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChoosePhysical.getTopLevelAncestor(), "A mech may mount no more than two physical weapons." );
                        return;
                    }
                    if( ((ifWeapon) a).RequiresNuclear() &! CurMech.GetEngine().IsNuclear() ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChoosePhysical.getTopLevelAncestor(), a.GetCritName() + " may not be mounted because the mech\ndoes not use a nuclear engine (fission or fusion)." );
                        return;
                    }
                    if( ((ifWeapon) a).RequiresFusion() &! CurMech.GetEngine().IsFusion() ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChoosePhysical.getTopLevelAncestor(), a.GetCritName() + " may not be mounted\nbecause the mech does not use a fusion engine." );
                        return;
                    }

                    // add it to the loadout
                    CurMech.GetLoadout().AddToQueue( a );

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
                }
            }
        };
        lstChoosePhysical.addMouseListener( mlPhysical );
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
                    int index = lstChooseEquipment.locationToIndex( e.getPoint() );
                    if( index < 0 ) { return; }
                    abPlaceable a = (abPlaceable) Equipment[EQUIPMENT][index];
                    Vector v;
                    // see if this is a C3 computer and whether we need to limit choices
                    if( a.GetCritName().equals( "C3 Computer (Master)" ) || a.GetCritName().equals( "C3 Computer (Slave)" ) ) {
                        // check the loadout for a C3i computer
                        v = CurMech.GetLoadout().GetNonCore();
                        for( int i = 0; i < v.size(); i++ ) {
                            if( v.get( i ) instanceof Equipment ) {
                                if( ((abPlaceable) v.get( i )).GetCritName().equals( "Improved C3 Computer" ) ) {
                                    javax.swing.JOptionPane.showMessageDialog( lstChooseEquipment.getTopLevelAncestor(), "A mech may not mount a C3 Master or Slave if it\nalready mounts an Improved C3 Computer." );
                                    return;
                                }
                            }
                        }
                    }
                    if( a.GetCritName().equals( "Improved C3 Computer" ) ) {
                        // check the loadout for a C3 Master or Slave
                        // check the loadout for a C3i computer
                        v = CurMech.GetLoadout().GetNonCore();
                        for( int i = 0; i < v.size(); i++ ) {
                            if( v.get( i ) instanceof Equipment ) {
                                if( ((abPlaceable) v.get( i )).GetCritName().equals( "C3 Computer (Master)" ) || ((abPlaceable) v.get( i )).GetCritName().equals( "C3 Computer (Slave)" ) ) {
                                    javax.swing.JOptionPane.showMessageDialog( lstChooseEquipment.getTopLevelAncestor(), "A mech may not mount an improved C3 computer if it\nalready mounts a C3 Master or Slave." );
                                    return;
                                }
                            }
                        }
                    }
                    a = Equips.GetCopy( a );

                    // add it to the loadout
                    CurMech.GetLoadout().AddToQueue( a );

                    // see if we need ammunition and add it if applicable
                    ResetAmmo();

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
                }
            }
        };
        lstChooseEquipment.addMouseListener( mlEquipment );
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
                    int index = lstChooseArtillery.locationToIndex( e.getPoint() );
                    if( index < 0 ) { return; }
                    abPlaceable a = (abPlaceable) Equipment[ARTILLERY][index];
                    a = Weapons.GetCopy( a );

                    if( ((ifWeapon) a).RequiresNuclear() &! CurMech.GetEngine().IsNuclear() ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChooseArtillery.getTopLevelAncestor(), a.GetCritName() + " may not be mounted because the mech\ndoes not use a nuclear engine (fission or fusion)." );
                        return;
                    }
                    if( ((ifWeapon) a).RequiresFusion() &! CurMech.GetEngine().IsFusion() ) {
                        javax.swing.JOptionPane.showMessageDialog( lstChooseArtillery.getTopLevelAncestor(), a.GetCritName() + " may not be mounted\nbecause the mech does not use a fusion engine." );
                        return;
                    }

                    // add it to the loadout
                    CurMech.GetLoadout().AddToQueue( a );

                    // see if we need ammunition and add it if applicable
                    ResetAmmo();

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
                }
            }
        };
        lstChooseArtillery.addMouseListener( mlArtillery );
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
                    int index = lstChooseAmmunition.locationToIndex( e.getPoint() );
                    if( index < 0 ) { return; }
                    abPlaceable a = (abPlaceable) Equipment[AMMUNITION][index];
                    a = Ammo.GetCopy( a );

                    // add it to the loadout
                    CurMech.GetLoadout().AddToQueue( a );

                    // added for support if the user selected ammo.  The ResetAmmo()
                    // method clears the selected index.
                    // lstChooseAmmunition.setSelectedIndex(Index);

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
                }
            }
        };
        lstChooseAmmunition.addMouseListener( mlAmmo );
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

        chkArtemisSRM.setText("SRMs");
        chkArtemisSRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkArtemisSRMActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlSpecials.add(chkArtemisSRM, gridBagConstraints);

        jLabel16.setText("Use Artemis IV on:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlSpecials.add(jLabel16, gridBagConstraints);

        chkArtemisLRM.setText("LRMs");
        chkArtemisLRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkArtemisLRMActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        pnlSpecials.add(chkArtemisLRM, gridBagConstraints);

        chkArtemisMML.setText("MMLs");
        chkArtemisMML.setEnabled(false);
        chkArtemisMML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkArtemisMMLActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnlSpecials.add(chkArtemisMML, gridBagConstraints);

        chkUseTC.setText("Targeting Computer");
        chkUseTC.setEnabled(false);
        chkUseTC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseTCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlSpecials.add(chkUseTC, gridBagConstraints);

        jLabel63.setText("Use Artemis V on:");
        jLabel63.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        pnlSpecials.add(jLabel63, gridBagConstraints);

        chkArtemisVSRM.setText("SRMs");
        chkArtemisVSRM.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        pnlSpecials.add(chkArtemisVSRM, gridBagConstraints);

        chkArtemisVLRM.setText("LRMs");
        chkArtemisVLRM.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        pnlSpecials.add(chkArtemisVLRM, gridBagConstraints);

        chkMRMApollo.setText("MRM Apollo");
        chkMRMApollo.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlSpecials.add(chkMRMApollo, gridBagConstraints);

        pnlEquipment.add(pnlSpecials, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 100, 160, 220));

        pnlSelected.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selected Equipment"));
        pnlSelected.setMaximumSize(new java.awt.Dimension(212, 286));
        pnlSelected.setMinimumSize(new java.awt.Dimension(212, 286));
        pnlSelected.setLayout(new javax.swing.BoxLayout(pnlSelected, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane23.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane23.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        lstSelectedEquipment.setModel( new javax.swing.DefaultListModel()
            //    new javax.swing.AbstractListModel() {
                //    String[] strings = { "Placeholder" };
                //    public int getSize() { return strings.length; }
                //    public Object getElementAt(int i) { return strings[i]; }
                // }
        );
        lstSelectedEquipment.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstSelectedEquipment.setMaximumSize(new java.awt.Dimension(180, 225));
        lstSelectedEquipment.setMinimumSize(new java.awt.Dimension(180, 225));
        lstSelectedEquipment.setPreferredSize(null);
        lstSelectedEquipment.setVisibleRowCount(16);
        lstSelectedEquipment.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstSelectedEquipmentValueChanged(evt);
            }
        });
        lstSelectedEquipment.setCellRenderer( new EquipmentListRenderer( this ) );
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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlControls.add(btnClearEquip, gridBagConstraints);

        btnAddEquip.setText(">>");
        btnAddEquip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddEquipActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlControls.add(btnAddEquip, gridBagConstraints);

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

        lstHDCrits.setFont(new java.awt.Font("Tahoma", 0, 10));
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
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetHDCrits();
                    int index = lstHDCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstHDCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetHDCrits()[index];
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

        lstCTCrits.setFont(new java.awt.Font("Tahoma", 0, 10));
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
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetCTCrits();
                    int index = lstCTCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstCTCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetCTCrits()[index];
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

        lstLTCrits.setFont(new java.awt.Font("Tahoma", 0, 10));
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
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetLTCrits();
                    int index = lstLTCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstLTCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetLTCrits()[index];
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

        lstRTCrits.setFont(new java.awt.Font("Tahoma", 0, 10));
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
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetRTCrits();
                    int index = lstRTCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstRTCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetRTCrits()[index];
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

        lstLACrits.setFont(new java.awt.Font("Tahoma", 0, 10));
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
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    mnuMountRear.setEnabled( false );
                    mnuMountRear.setText( "Mount Rear " );
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetLACrits();
                    int index = lstLACrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    mnuMountRear.setEnabled( false );
                    mnuMountRear.setText( "Mount Rear " );
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstLACrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetLACrits()[index];
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

        lstRACrits.setFont(new java.awt.Font("Tahoma", 0, 10));
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
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    mnuMountRear.setEnabled( false );
                    mnuMountRear.setText( "Mount Rear " );
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetRACrits();
                    int index = lstRACrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    mnuMountRear.setEnabled( false );
                    mnuMountRear.setText( "Mount Rear " );
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstRACrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetRACrits()[index];
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

        lstLLCrits.setFont(new java.awt.Font("Tahoma", 0, 10));
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
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetLLCrits();
                    int index = lstLLCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstLLCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetLLCrits()[index];
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

        lstRLCrits.setFont(new java.awt.Font("Tahoma", 0, 10));
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
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
            public void mousePressed( MouseEvent e ) {
                if( e.isPopupTrigger() ) {
                    abPlaceable[] a = CurMech.GetLoadout().GetRLCrits();
                    int index = lstRLCrits.locationToIndex( e.getPoint() );
                    CurItem = a[index];
                    // now, based on what was clicked, show the menu
                    if( CurItem instanceof EmptyItem ) {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    } else if( ! CurItem.LocationLocked() ) {
                        if( CurItem.Contiguous() ) {
                            mnuUnallocateAll.setText( "Unallocate " + CurItem.GetCritName() );
                        } else {
                            mnuUnallocateAll.setText( "Unallocate All" );
                        }
                        mnuUnallocateAll.setEnabled( true );
                    } else {
                        mnuUnallocateAll.setText( "Unallocate All" );
                        mnuUnallocateAll.setEnabled( false );
                    }
                    if( CurItem.CanMountRear() ) {
                        mnuMountRear.setEnabled( true );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    } else {
                        mnuMountRear.setEnabled( false );
                        if( CurItem.IsMountedRear() ) {
                            mnuMountRear.setText( "Unmount Rear " );
                        } else {
                            mnuMountRear.setText( "Mount Rear " );
                        }
                    }
                    if( CurItem.IsArmored() ) {
                        mnuArmorComponent.setText( "Unarmor Component" );
                    } else {
                        mnuArmorComponent.setText( "Armor Component" );
                    }
                    mnuArmorComponent.setEnabled( LegalArmoring( CurItem ) );
                    mnuCrits.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    int index = lstRLCrits.locationToIndex( e.getPoint() );
                    CurItem = CurMech.GetLoadout().GetRLCrits()[index];
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

        lstCritsToPlace.setFont(new java.awt.Font("Tahoma", 0, 10));
        lstCritsToPlace.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Selected", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstCritsToPlace.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstCritsToPlace.setDragEnabled(true);
        lstCritsToPlace.setMaximumSize(new java.awt.Dimension(150, 10000));
        lstCritsToPlace.setMinimumSize(new java.awt.Dimension(150, 80));
        lstCritsToPlace.setPreferredSize(null);
        lstCritsToPlace.setVisibleRowCount(20);
        MouseListener mlCritsToPlace = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                Vector v = CurMech.GetLoadout().GetQueue();
                int Index = lstCritsToPlace.locationToIndex( e.getPoint() );
                if( Index < 0 ) { return; }
                CurItem = (abPlaceable) v.get( Index );
                if( e.isPopupTrigger() ) {
                    if( CurItem.Contiguous() ) {
                        mnuSelective.setEnabled( false );
                        mnuAuto.setEnabled( false );
                    } else {
                        mnuSelective.setEnabled( true );
                        mnuAuto.setEnabled( true );
                    }
                    mnuPlacement.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    if( CurItem.Contiguous() ) {
                        btnAutoAllocate.setEnabled( false );
                        btnSelectiveAllocate.setEnabled( false );
                    } else {
                        btnAutoAllocate.setEnabled( true );
                        btnSelectiveAllocate.setEnabled( true );
                    }
                }
            }
            public void mousePressed( MouseEvent e ) {
                Vector v = CurMech.GetLoadout().GetQueue();
                int Index = lstCritsToPlace.locationToIndex( e.getPoint() );
                if( Index < 0 ) { return; }
                CurItem = (abPlaceable) v.get( Index );
                if( e.isPopupTrigger() ) {
                    if( CurItem.Contiguous() ) {
                        mnuSelective.setEnabled( false );
                        mnuAuto.setEnabled( false );
                    } else {
                        mnuSelective.setEnabled( true );
                        mnuAuto.setEnabled( true );
                    }
                    mnuPlacement.show( e.getComponent(), e.getX(), e.getY() );
                } else {
                    if( CurItem.Contiguous() ) {
                        btnAutoAllocate.setEnabled( false );
                        btnSelectiveAllocate.setEnabled( false );
                    } else {
                        btnAutoAllocate.setEnabled( true );
                        btnSelectiveAllocate.setEnabled( true );
                    }
                }
            }
        };
        lstCritsToPlace.addMouseListener( mlCritsToPlace );
        lstCritsToPlace.setTransferHandler( new thQueueTransferHandler() );
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlFluff.add(pnlImage, gridBagConstraints);

        tbpFluffEditors.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tbpFluffEditors.setMaximumSize(new java.awt.Dimension(420, 455));
        tbpFluffEditors.setMinimumSize(new java.awt.Dimension(420, 455));
        tbpFluffEditors.setPreferredSize(new java.awt.Dimension(420, 455));

        pnlOverview.setMaximumSize(new java.awt.Dimension(427, 485));
        pnlOverview.setMinimumSize(new java.awt.Dimension(427, 485));
        pnlOverview.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel1.setText("Overview");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlOverview.add(jLabel1, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(310, 420));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(310, 420));
        jScrollPane1.setViewportView(edtOverview);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlOverview.add(jScrollPane1, gridBagConstraints);

        tbpFluffEditors.addTab("Overview", pnlOverview);

        pnlCapabilities.setMaximumSize(new java.awt.Dimension(427, 485));
        pnlCapabilities.setMinimumSize(new java.awt.Dimension(427, 485));
        pnlCapabilities.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel2.setText("Capabilities");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlCapabilities.add(jLabel2, gridBagConstraints);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(310, 420));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(310, 420));

        edtCapabilities.setMaximumSize(new java.awt.Dimension(106, 20));
        jScrollPane2.setViewportView(edtCapabilities);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlCapabilities.add(jScrollPane2, gridBagConstraints);

        tbpFluffEditors.addTab("Capabilities", pnlCapabilities);

        pnlHistory.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel3.setText("Battle History");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlHistory.add(jLabel3, gridBagConstraints);

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setMinimumSize(new java.awt.Dimension(310, 420));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(310, 420));
        jScrollPane3.setViewportView(edtHistory);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlHistory.add(jScrollPane3, gridBagConstraints);

        tbpFluffEditors.addTab("Battle History", pnlHistory);

        pnlDeployment.setLayout(new java.awt.GridBagLayout());

        jLabel4.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel4.setText("Deployment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlDeployment.add(jLabel4, gridBagConstraints);

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setMinimumSize(new java.awt.Dimension(310, 420));
        jScrollPane4.setPreferredSize(new java.awt.Dimension(310, 420));
        jScrollPane4.setViewportView(edtDeployment);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlDeployment.add(jScrollPane4, gridBagConstraints);

        tbpFluffEditors.addTab("Deployment", pnlDeployment);

        pnlVariants.setLayout(new java.awt.GridBagLayout());

        jLabel5.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel5.setText("Variants");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlVariants.add(jLabel5, gridBagConstraints);

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane5.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane5.setMinimumSize(new java.awt.Dimension(310, 420));
        jScrollPane5.setPreferredSize(new java.awt.Dimension(310, 420));
        jScrollPane5.setViewportView(edtVariants);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlVariants.add(jScrollPane5, gridBagConstraints);

        tbpFluffEditors.addTab("Variants", pnlVariants);

        pnlNotables.setLayout(new java.awt.GridBagLayout());

        jLabel6.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel6.setText("Notable 'Mechs and Mechwarriors");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlNotables.add(jLabel6, gridBagConstraints);

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane6.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane6.setMinimumSize(new java.awt.Dimension(310, 420));
        jScrollPane6.setPreferredSize(new java.awt.Dimension(310, 420));
        jScrollPane6.setViewportView(edtNotables);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlNotables.add(jScrollPane6, gridBagConstraints);

        tbpFluffEditors.addTab("Notables", pnlNotables);

        pnlAdditionalFluff.setLayout(new java.awt.GridBagLayout());

        jLabel7.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel7.setText("Additional Fluff (story, etc...)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlAdditionalFluff.add(jLabel7, gridBagConstraints);

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane7.setMinimumSize(new java.awt.Dimension(310, 420));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(310, 420));
        jScrollPane7.setViewportView(edtAdditionalFluff);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlAdditionalFluff.add(jScrollPane7, gridBagConstraints);

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

        jLabel11.setFont(new java.awt.Font("Arial", 0, 11));
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

        txtEngineManufacturer.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtEngineManufacturer, gridBagConstraints);

        txtArmorModel.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtArmorModel, gridBagConstraints);

        txtChassisModel.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtChassisModel, gridBagConstraints);

        txtCommSystem.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtCommSystem, gridBagConstraints);

        txtTNTSystem.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 184;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 11);
        pnlManufacturers.add(txtTNTSystem, gridBagConstraints);

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

        tbpFluffEditors.addTab("Manufacturers", pnlManufacturers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 6);
        pnlFluff.add(tbpFluffEditors, gridBagConstraints);

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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlFluff.add(pnlExport, gridBagConstraints);

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

        lblLegendTitle.setText("Chart Legend:");
        pnlCharts.add(lblLegendTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 10, 90, -1));

        lblFALegend.setBackground(java.awt.Color.red);
        lblFALegend.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFALegend.setText("Forward Arc Weapons");
        lblFALegend.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblFALegend.setMaximumSize(new java.awt.Dimension(140, 18));
        lblFALegend.setMinimumSize(new java.awt.Dimension(140, 18));
        lblFALegend.setOpaque(true);
        lblFALegend.setPreferredSize(new java.awt.Dimension(140, 18));
        pnlCharts.add(lblFALegend, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 150, -1));

        lblRALegened.setBackground(java.awt.Color.pink);
        lblRALegened.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRALegened.setText("Rear Arc Weapons");
        lblRALegened.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRALegened.setMaximumSize(new java.awt.Dimension(140, 18));
        lblRALegened.setMinimumSize(new java.awt.Dimension(140, 18));
        lblRALegened.setOpaque(true);
        lblRALegened.setPreferredSize(new java.awt.Dimension(140, 18));
        pnlCharts.add(lblRALegened, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 30, 150, -1));

        lblRAALegend.setBackground(java.awt.Color.green);
        lblRAALegend.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRAALegend.setText("Right Arm Arc Weapons");
        lblRAALegend.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRAALegend.setMaximumSize(new java.awt.Dimension(140, 18));
        lblRAALegend.setMinimumSize(new java.awt.Dimension(140, 18));
        lblRAALegend.setOpaque(true);
        lblRAALegend.setPreferredSize(new java.awt.Dimension(140, 18));
        pnlCharts.add(lblRAALegend, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 50, 150, -1));

        lblLAALegend.setBackground(java.awt.Color.orange);
        lblLAALegend.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLAALegend.setText("Left Arm Arc Weapons");
        lblLAALegend.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLAALegend.setMaximumSize(new java.awt.Dimension(140, 18));
        lblLAALegend.setMinimumSize(new java.awt.Dimension(140, 18));
        lblLAALegend.setOpaque(true);
        lblLAALegend.setPreferredSize(new java.awt.Dimension(140, 18));
        pnlCharts.add(lblLAALegend, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 70, 150, -1));

        lblLegendNote.setText("<html>NOTE:<br>Depnding on the damage at a given<br>range, some lines may be hidden.</html>");
        pnlCharts.add(lblLegendNote, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 100, 240, -1));

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

        mnuOpen.setText("Open");
        mnuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenActionPerformed(evt);
            }
        });
        mnuFile.add(mnuOpen);
        mnuFile.add(jSeparator16);

        mnuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        mnuSave.setText("Save Mech");
        mnuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveActionPerformed(evt);
            }
        });
        mnuFile.add(mnuSave);

        mnuSaveAs.setText("Save Mech As...");
        mnuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveAsActionPerformed(evt);
            }
        });
        mnuFile.add(mnuSaveAs);

        jMenu1.setText("Export Mech...");

        mnuExportHTML.setText("to HTML (Web)");
        mnuExportHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportHTMLActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExportHTML);

        mnuExportMTF.setText("to MTF (MegaMek)");
        mnuExportMTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportMTFActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExportMTF);

        mnuExportTXT.setText("to TXT (Text)");
        mnuExportTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportTXTActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExportTXT);

        mnuExportClipboard.setText("to Clipboard (Text)");
        mnuExportClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportClipboardActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExportClipboard);

        mnuCreateTCGMech.setText("to TCG Format (Card)");
        mnuCreateTCGMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreateTCGMechActionPerformed(evt);
            }
        });
        jMenu1.add(mnuCreateTCGMech);

        mnuFile.add(jMenu1);
        mnuFile.add(jSeparator20);

        jMenu2.setText("Print");

        mnuPrintCurrentMech.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintCurrentMech.setText("Current Mech");
        mnuPrintCurrentMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintCurrentMechActionPerformed(evt);
            }
        });
        jMenu2.add(mnuPrintCurrentMech);

        mnuPrintSavedMech.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintSavedMech.setText("Saved Mech");
        mnuPrintSavedMech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintSavedMechActionPerformed(evt);
            }
        });
        jMenu2.add(mnuPrintSavedMech);

        mnuPrintBatch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuPrintBatch.setText("Batch Print Mechs");
        mnuPrintBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrintBatchActionPerformed(evt);
            }
        });
        jMenu2.add(mnuPrintBatch);

        mnuFile.add(jMenu2);

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

        mnuTools.setText("Tools");

        mnuSummary.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_MASK));
        mnuSummary.setText("Show Summary");
        mnuSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSummaryActionPerformed(evt);
            }
        });
        mnuTools.add(mnuSummary);

        mnuCostBVBreakdown.setText("Cost/BV Breakdown");
        mnuCostBVBreakdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCostBVBreakdownActionPerformed(evt);
            }
        });
        mnuTools.add(mnuCostBVBreakdown);

        mnuTextTRO.setText("Show Text TRO Format");
        mnuTextTRO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTextTROActionPerformed(evt);
            }
        });
        mnuTools.add(mnuTextTRO);

        mnuUnlock.setText("Unlock Chassis");
        mnuUnlock.setEnabled(false);
        mnuTools.add(mnuUnlock);
        mnuTools.add(jSeparator15);

        mnuOptions.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK));
        mnuOptions.setText("Options");
        mnuOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOptionsActionPerformed(evt);
            }
        });
        mnuTools.add(mnuOptions);

        mnuViewToolbar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        mnuViewToolbar.setSelected(true);
        mnuViewToolbar.setText("View Toolbar");
        mnuViewToolbar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuViewToolbarActionPerformed(evt);
            }
        });
        mnuTools.add(mnuViewToolbar);

        mnuClearUserData.setText("Clear User Data");
        mnuClearUserData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuClearUserDataActionPerformed(evt);
            }
        });
        mnuTools.add(mnuClearUserData);

        mnuMainMenu.add(mnuTools);

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
            GlobalOptions.Save();
        } catch( IOException e ) {
            //do nothing
            //javax.swing.JOptionPane.showMessageDialog( this, "Could not save the options!  File operation problem (save, close):\n" + e );
        }

        try {
            if (BatchWindow != null) BatchWindow.dispose();
            if (dOpen != null) dOpen.dispose();
            if (dForce != null) dForce.dispose();
        } catch (Exception e) {
            //do nothing
        }

        dispose();
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
        if( CurMech.GetHeatSinks().GetLookupName().equals( (String) cmbHeatSinkType.getSelectedItem() ) ) {
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

        // first, let's save the tech base selection in case we can still use it
        // prevents Clan mechs reverting to Inner Sphere on era change.
        int tbsave = cmbTechBase.getSelectedIndex();

        // change the year range and tech base options
        switch( cmbMechEra.getSelectedIndex() ) {
            case 0:
                lblEraYears.setText( "2443 ~ 2800" );
                cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere" }));
                txtProdYear.setText( "" );
                CurMech.SetEra( Constants.STAR_LEAGUE );
                CurMech.SetYear( 2750, false );
                break;
            case 1:
                lblEraYears.setText( "2801 ~ 3050" );
                cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
                txtProdYear.setText( "" );
                CurMech.SetEra( Constants.SUCCESSION );
                CurMech.SetYear( 3025, false );
                break;
            case 2:
                lblEraYears.setText( "3051 on" );
                cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
                txtProdYear.setText( "" );
                CurMech.SetEra( Constants.CLAN_INVASION );
                CurMech.SetYear( 3070, false );
                break;
            case 3:
                lblEraYears.setText( "Any" );
                cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
                txtProdYear.setText( "" );
                CurMech.SetEra( Constants.ALL_ERA );
                CurMech.SetYear( 0, false );
                chkYearRestrict.setEnabled( false );
                break;
        }

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
        RefreshEquipment();
        CheckOmnimech();

        // now reset the combo boxes to the closest choices we previously selected
        LoadSelections();

        // when a new era is selected, we have to recalculate the mech
        /*
         * We're testing this for the next few builds to see what happens.
        CurMech.GetLoadout().FullUnallocate();
        */
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

        // the commitedit worked, so set the engine rating and report the running mp
        CurMech.SetWalkMP( n.getNumber().intValue() );
        lblRunMP.setText( "" + CurMech.GetRunningMP() );

        // when the walking mp changes, we also have to change the jump mp
        // spinner model and recalculate the heat sinks
        FixJJSpinnerModel();
        CurMech.GetHeatSinks().ReCalculate();

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
        int CurTons = CurMech.GetTonnage();
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
        if( cmbTechBase.getSelectedIndex() == 0 ) {
            if( ! CurMech.IsClan() ) { return; }
        } else {
            if( CurMech.IsClan() ) { return; }
        }

        // save the current selections
        SaveSelections();

        // now change the mech over to the new techbase
        if( cmbTechBase.getSelectedIndex() == 0 ) {
            CurMech.SetInnerSphere();
        } else {
            CurMech.SetClan();
        }

        data.GetEquipment().rebuildIndustrialEquipment( CurMech );

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
        CheckOmnimech();

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
        RecalcEquipment();
        SetWeaponChoosers();
        chkUseTC.setSelected( false );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
        SetWeaponChoosers();
    }//GEN-LAST:event_cmbTechBaseActionPerformed

    private void cmbPhysEnhanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPhysEnhanceActionPerformed
        if( CurMech.GetPhysEnhance().GetLookupName().equals( (String) cmbPhysEnhance.getSelectedItem() ) ) {
            return;
        }

        RecalcEnhancements();

        // check our exclusions
        try {
            CurMech.GetLoadout().CheckExclusions( CurMech.GetPhysEnhance() );
        } catch( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
        if( CurMech.GetCockpit().GetLookupName().equals( (String) cmbCockpitType.getSelectedItem() ) ) {
            return;
        }
        RecalcCockpit();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbCockpitTypeActionPerformed

    private void cmbGyroTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGyroTypeActionPerformed
        if( CurMech.GetGyro().GetLookupName().equals( (String) cmbGyroType.getSelectedItem() ) ) {
            return;
        }
        RecalcGyro();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbGyroTypeActionPerformed

    private void cmbEngineTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEngineTypeActionPerformed
        if( CurMech.GetEngine().GetLookupName().equals( (String) cmbEngineType.getSelectedItem() ) ) {
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

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbEngineTypeActionPerformed

    private void cmbInternalTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbInternalTypeActionPerformed
        if( CurMech.GetIntStruc().GetLookupName().equals( (String) cmbInternalType.getSelectedItem() ) ) {
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
        if( CurMech.GetArmor().GetLookupName().equals( (String) cmbArmorType.getSelectedItem() ) ) {
            return;
        }
        RecalcArmor();

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_cmbArmorTypeActionPerformed

    private void mnuOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOptionsActionPerformed
        dlgOptions Options = new dlgOptions( this, true );
        Options.setLocationRelativeTo( this );
        Options.setVisible( true );
        SetWeaponChoosers();
        ResetAmmo();
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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_HD );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_HD );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_HD );
                curmech = a.GetLocationArmor( Constants.LOC_HD );
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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_RA );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_RA );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_RA );
                curmech = a.GetLocationArmor( Constants.LOC_RA );
            }
        }

        // see if we need to change the left arm as well
        if( btnBalanceArmor.isSelected() ) {
            a.SetArmor( Constants.LOC_LA, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnLAArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( Constants.LOC_LA ) );
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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_RT );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_RT );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_RT );
                curmech = a.GetLocationArmor( Constants.LOC_RT );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnRTRArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( Constants.LOC_RTR ) );

        // see if we need to change the left torso as well
        if( btnBalanceArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnRTArmor.getModel();
            a.SetArmor( Constants.LOC_LT, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnLTArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( Constants.LOC_LT ) );
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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_RTR );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_RTR );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_RTR );
                curmech = a.GetLocationArmor( Constants.LOC_RTR );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnRTArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( Constants.LOC_RT ) );

        // see if we need to change the left torso as well
        // see if we need to change the left torso as well
        if( btnBalanceArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnRTRArmor.getModel();
            a.SetArmor( Constants.LOC_LTR, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnLTRArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( Constants.LOC_LTR ) );
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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_LA );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_LA );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_LA );
                curmech = a.GetLocationArmor( Constants.LOC_LA );
            }
        }

        // see if we need to change the right arm as well
        if( btnBalanceArmor.isSelected() ) {
            a.SetArmor( Constants.LOC_RA, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnRAArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( Constants.LOC_RA ) );
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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_LT );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_LT );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_LT );
                curmech = a.GetLocationArmor( Constants.LOC_LT );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnLTRArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( Constants.LOC_LTR ) );

        // see if we need to change the right torso as well
        if( btnBalanceArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnLTArmor.getModel();
            a.SetArmor( Constants.LOC_RT, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnRTArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( Constants.LOC_RT ) );
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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_LTR );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_LTR );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_LTR );
                curmech = a.GetLocationArmor( Constants.LOC_LTR );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnLTArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( Constants.LOC_LT ) );

        // see if we need to change the right torso as well
        if( btnBalanceArmor.isSelected() ) {
            n = (SpinnerNumberModel) spnLTRArmor.getModel();
            a.SetArmor( Constants.LOC_RTR, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnRTRArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( Constants.LOC_RTR ) );
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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_CT );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_CT );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_CT );
                curmech = a.GetLocationArmor( Constants.LOC_CT );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnCTRArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( Constants.LOC_CTR ) );

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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_CTR );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_CTR );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_CTR );
                curmech = a.GetLocationArmor( Constants.LOC_CTR );
            }
        }

        // now we need to set the rear armor spinner correctly and update
        n = (SpinnerNumberModel) spnCTArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( Constants.LOC_CT ) );

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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_LL );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_LL );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_LL );
                curmech = a.GetLocationArmor( Constants.LOC_LL );
            }
        }

        // see if we need to change the right arm as well
        if( btnBalanceArmor.isSelected() ) {
            a.SetArmor( Constants.LOC_RL, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnRLArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( Constants.LOC_RL ) );
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
        Armor a = CurMech.GetArmor();
        int curmech = a.GetLocationArmor( Constants.LOC_RL );
        int curframe = n.getNumber().intValue();
        if( curframe > curmech ) {
            while( curframe > curmech ) {
                a.IncrementArmor( Constants.LOC_RL );
                curframe--;
            }
        } else {
            while( curmech > curframe ) {
                a.DecrementArmor( Constants.LOC_RL );
                curmech = a.GetLocationArmor( Constants.LOC_RL );
            }
        }

        // see if we need to change the right arm as well
        if( btnBalanceArmor.isSelected() ) {
            a.SetArmor( Constants.LOC_LL, n.getNumber().intValue() );
            n = (SpinnerNumberModel) spnLLArmor.getModel();
            n.setValue( (Object) a.GetLocationArmor( Constants.LOC_LL ) );
        }

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_spnRLArmorStateChanged

    private void btnMaxArmorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaxArmorActionPerformed
        // this simply maximizes the mech's armor
        Armor a = CurMech.GetArmor();

        // set the simple stuff first.
        a.SetArmor( Constants.LOC_HD, 9 );
        a.SetArmor( Constants.LOC_LA, a.GetLocationMax( Constants.LOC_LA ) );
        a.SetArmor( Constants.LOC_RA, a.GetLocationMax( Constants.LOC_RA ) );
        a.SetArmor( Constants.LOC_LL, a.GetLocationMax( Constants.LOC_LL ) );
        a.SetArmor( Constants.LOC_RL, a.GetLocationMax( Constants.LOC_RL ) );

        // now to set the torsos
        int rear = Math.round( a.GetLocationMax( Constants.LOC_CT ) * GlobalOptions.Armor_CTRPercent / 100 );
        a.SetArmor( Constants.LOC_CTR, rear );
        a.SetArmor( Constants.LOC_CT, a.GetLocationMax( Constants.LOC_CT ) - rear );
        rear = Math.round( a.GetLocationMax( Constants.LOC_LT ) * GlobalOptions.Armor_STRPercent / 100 );
        a.SetArmor( Constants.LOC_LTR, rear );
        a.SetArmor( Constants.LOC_LT, a.GetLocationMax( Constants.LOC_LT ) - rear );
        rear = Math.round( a.GetLocationMax( Constants.LOC_RT ) * GlobalOptions.Armor_STRPercent / 100 );
        a.SetArmor( Constants.LOC_RTR, rear );
        a.SetArmor( Constants.LOC_RT, a.GetLocationMax( Constants.LOC_RT ) - rear );

        // if we fix the spinner models, they should refresh the screen
        FixArmorSpinners();

        // of course, we'll also have to set the head spinner manually.
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHDArmor.getModel();
        n.setValue( (Object) a.GetLocationArmor( Constants.LOC_HD ) );

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
            float result = ArmorDialogue.GetResult();
            ArmorTons.SetArmorTonnage( result );
            CurMech.Visit( ArmorTons );
            ArmorDialogue.dispose();
        } else {
            ArmorDialogue.dispose();
        }

        // if we fix the spinner models, they should refresh the screen
        FixArmorSpinners();

        // of course, we'll also have to set the head spinner manually.
        javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHDArmor.getModel();
        n.setValue( (Object) CurMech.GetArmor().GetLocationArmor( Constants.LOC_HD ) );

        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_btnArmorTonsActionPerformed

    private void chkLAHandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLAHandActionPerformed
        if( chkLAHand.isSelected() == CurMech.GetActuators().LeftHandInstalled() ) {
            return;
        }
        if( chkLAHand.isSelected() ) {
            CurMech.GetActuators().AddLeftHand();
        } else {
            CurMech.GetActuators().RemoveLeftHand();
            // check for the presence of physical weapons and remove them
            Vector v = CurMech.GetLoadout().GetNonCore();
            for( int i = 0; i < v.size(); i++ ) {
                abPlaceable p = (abPlaceable) v.get( i );
                if( p instanceof PhysicalWeapon ) {
                    if( CurMech.GetLoadout().Find( p ) == Constants.LOC_LA ) {
                        CurMech.GetLoadout().UnallocateAll( p, false );
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
            CurMech.GetActuators().AddLeftLowerArm();
        } else {
            CurMech.GetActuators().RemoveLeftLowerArm();
            // check for the presence of physical weapons and remove them
            Vector v = CurMech.GetLoadout().GetNonCore();
            for( int i = 0; i < v.size(); i++ ) {
                abPlaceable p = (abPlaceable) v.get( i );
                if( p instanceof PhysicalWeapon ) {
                    if( CurMech.GetLoadout().Find( p ) == Constants.LOC_LA ) {
                        CurMech.GetLoadout().UnallocateAll( p, false );
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
            CurMech.GetActuators().AddRightHand();
        } else {
            CurMech.GetActuators().RemoveRightHand();
            // check for the presence of physical weapons and remove them
            Vector v = CurMech.GetLoadout().GetNonCore();
            for( int i = 0; i < v.size(); i++ ) {
                abPlaceable p = (abPlaceable) v.get( i );
                if( p instanceof PhysicalWeapon ) {
                    if( CurMech.GetLoadout().Find( p ) == Constants.LOC_RA ) {
                        CurMech.GetLoadout().UnallocateAll( p, false );
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
            CurMech.GetActuators().AddRightLowerArm();
        } else {
            CurMech.GetActuators().RemoveRightLowerArm();
            // check for the presence of physical weapons and remove them
            Vector v = CurMech.GetLoadout().GetNonCore();
            for( int i = 0; i < v.size(); i++ ) {
                abPlaceable p = (abPlaceable) v.get( i );
                if( p instanceof PhysicalWeapon ) {
                    if( CurMech.GetLoadout().Find( p ) == Constants.LOC_RA ) {
                        CurMech.GetLoadout().UnallocateAll( p, false );
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
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkLTCASE.setSelected( false );
            }
        } else {
            CurMech.RemoveLTCase();
        }
        RefreshInfoPane();
    }//GEN-LAST:event_chkLTCASEActionPerformed

    private void btnRemoveEquipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveEquipActionPerformed
        if( lstSelectedEquipment.getSelectedIndex() < 0 ) { return; }
        abPlaceable p = (abPlaceable) CurMech.GetLoadout().GetNonCore().get( lstSelectedEquipment.getSelectedIndex() );
        if( p.LocationLocked() ) {
            javax.swing.JOptionPane.showMessageDialog( this, "You may not remove a locked item from the loadout." );
            return;
        } else {
            CurMech.GetLoadout().Remove( p );
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
        Vector v;

        // figure out which list box to pull from
        switch( tbpWeaponChooser.getSelectedIndex() ) {
        case BALLISTIC:
            if( lstChooseBallistic.getSelectedIndex() < 0 ) { break; }
            a = (abPlaceable) Equipment[BALLISTIC][lstChooseBallistic.getSelectedIndex()];
            a = Weapons.GetCopy( a );
            break;
        case ENERGY:
            if( lstChooseEnergy.getSelectedIndex() < 0 ) { break; }
            a = (abPlaceable) Equipment[ENERGY][lstChooseEnergy.getSelectedIndex()];
            a = Weapons.GetCopy( a );
            break;
        case MISSILE:
            if( lstChooseMissile.getSelectedIndex() < 0 ) { break; }
            a = (abPlaceable) Equipment[MISSILE][lstChooseMissile.getSelectedIndex()];
            a = Weapons.GetCopy( a );
            if( ((MissileWeapon) a).IsArtemisCapable() ) {
                switch( ((MissileWeapon) a).GetArtemisType() ) {
                case Constants.ART4_SRM:
                    if( CurMech.UsingA4SRM() ) {
                        ((MissileWeapon) a).UseArtemis( true );
                    }
                    break;
                case Constants.ART4_LRM:
                    if( CurMech.UsingA4LRM() ) {
                        ((MissileWeapon) a).UseArtemis( true );
                    }
                    break;
                case Constants.ART4_MML:
                    if( CurMech.UsingA4MML() ) {
                        ((MissileWeapon) a).UseArtemis( true );
                    }
                    break;
                }
            }
            break;
        case PHYSICAL:
            if( lstChoosePhysical.getSelectedIndex() < 0 ) { break; }
            if( ! ( Equipment[PHYSICAL][lstChoosePhysical.getSelectedIndex()] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[PHYSICAL][lstChoosePhysical.getSelectedIndex()];
            a = Weapons.GetCopy( a );
            break;
        case ARTILLERY:
            if( lstChooseArtillery.getSelectedIndex() < 0 ) { break; }
            if( ! ( Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()];
            a = Weapons.GetCopy( a );
            break;
        case EQUIPMENT:
            if( lstChooseEquipment.getSelectedIndex() < 0 ) { break; }
            if( ! ( Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()];
            a = Equips.GetCopy( a );
            break;
        case AMMUNITION:
            if( lstChooseAmmunition.getSelectedIndex() < 0 ) { break; }
            Index = lstChooseAmmunition.getSelectedIndex();
            if( ! ( Equipment[AMMUNITION][Index] instanceof abPlaceable ) ) {
                break;
            }
            a = (abPlaceable) Equipment[AMMUNITION][Index];
            a = Ammo.GetCopy( a );
            break;
        }

        // check exclusions if needed
        try {
            CurMech.GetLoadout().CheckExclusions( a );
            if(a instanceof IndustrialEquipment){
                if (!((IndustrialEquipment) a).validate(CurMech)){
                    throw new Exception (((IndustrialEquipment) a).getValidationFalseMessage());
                }
            }
        } catch( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
            a = null;
        }

        // now we can add it to the 'Mech
        if( a != null ) {
            CurMech.GetLoadout().AddToQueue( a );

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

            // now refresh the information panes
            RefreshSummary();
            RefreshInfoPane();
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
        if( CurMech.UsingA4SRM() == chkArtemisSRM.isSelected() ) { return; }
        if( chkArtemisSRM.isSelected() ) {
            try {
                CurMech.SetA4FCSSRM( true );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkArtemisSRM.setSelected( false );
            }
        } else {
            try {
                CurMech.SetA4FCSSRM( false );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkArtemisSRM.setSelected( true );
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_chkArtemisSRMActionPerformed

    private void chkArtemisLRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkArtemisLRMActionPerformed
        if( CurMech.UsingA4LRM() == chkArtemisLRM.isSelected() ) { return; }
        if( chkArtemisLRM.isSelected() ) {
            try {
                CurMech.SetA4FCSLRM( true );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkArtemisLRM.setSelected( false );
            }
        } else {
            try {
                CurMech.SetA4FCSLRM( false );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkArtemisLRM.setSelected( true );
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_chkArtemisLRMActionPerformed

    private void chkArtemisMMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkArtemisMMLActionPerformed
        if( CurMech.UsingA4MML() == chkArtemisMML.isSelected() ) { return; }
        if( chkArtemisMML.isSelected() ) {
            try {
                CurMech.SetA4FCSMML( true );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkArtemisMML.setSelected( false );
            }
        } else {
            try {
                CurMech.SetA4FCSMML( false );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkArtemisMML.setSelected( true );
            }
        }
        // now refresh the information panes
        RefreshSummary();
        RefreshInfoPane();
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
                CurMech.UseTC( true );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                CurMech.UseTC( false );
            }
        } else {
            CurMech.UseTC( false );
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
        if( ! ( Equipment[EQUIPMENT][lstChooseEquipment.getSelectedIndex()] instanceof Equipment ) ) { return; }
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

        // Solidify the mech first.
        SolidifyMech();

        if( ! VerifyMech( evt ) ) {
            return;
        }

        // save the mech to MTF in the current location
        String file = "";
        if( CurMech.IsOmnimech() ) {
            if( CurMech.GetModel().isEmpty() ) {
                file = CurMech.GetName() + " " + CurMech.GetLoadout().GetName() + ".mtf";
            } else {
                file = CurMech.GetName() + " " + CurMech.GetModel() + " " + CurMech.GetLoadout().GetName() + ".mtf";
            }
        } else {
            if( CurMech.GetModel().isEmpty() ) {
                file = CurMech.GetName() + ".mtf";
            } else {
                file = CurMech.GetName() + " " + CurMech.GetModel() + ".mtf";
            }
        }
        // need to double-check the filename and warn the user if there are 
        // special character
        try {
            CheckFileName( file );
        } catch( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "There was a problem with the file name:\n" + e.getMessage() );
            return;
        }

        if( ! GlobalOptions.MegamekPath.equals( "none" ) ) {
            file = GlobalOptions.MegamekPath + File.separator + file;
        }
        MTFWriter mtfw = new MTFWriter( CurMech );
        try {
            mtfw.WriteMTF( file );
        } catch( IOException e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "There was a problem writing the file:\n" + e.getMessage() );
            return;
        }

        // if there were no problems, let the user know how it went
        javax.swing.JOptionPane.showMessageDialog( this, "Mech saved successfully to MTF:\n" + file );
        setTitle( Constants.AppName + " " + Constants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
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

        // Solidify the mech first.
        SolidifyMech();

        if( ! VerifyMech( evt ) ) {
            return;
        }

        // save the mech to TXT in the current location
        String file = "";
        if( CurMech.GetModel().isEmpty() ) {
            file = CurMech.GetName() + ".txt";
        } else {
            file = CurMech.GetName() + " " + CurMech.GetModel() + ".txt";
        }
        // need to double-check the filename and warn the user if there are 
        // special character
        try {
            CheckFileName( file );
        } catch( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "There was a problem with the file name:\n" + e.getMessage() );
            return;
        }

        if( ! GlobalOptions.TXTPath.equals( "none" ) ) {
            file = GlobalOptions.TXTPath + File.separator + file;
        }
        TXTWriter txtw = new TXTWriter( CurMech, GlobalOptions );
        try {
            txtw.WriteTXT( file );
        } catch( IOException e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "There was a problem writing the file:\n" + e.getMessage() );
            return;
        }

        // if there were no problems, let the user know how it went
        javax.swing.JOptionPane.showMessageDialog( this, "Mech saved successfully to TXT:\n" + file );

        // lastly, if this is an omnimech, reset the display to the last loadout
        cmbOmniVariant.setSelectedItem( CurLoadout );
        cmbOmniVariantActionPerformed( evt );
        setTitle( Constants.AppName + " " + Constants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
    }//GEN-LAST:event_btnExportTXTActionPerformed

    private void btnExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportHTMLActionPerformed
        // exports the mech to HTML format
        String CurLoadout = "";
        if( CurMech.IsOmnimech() ) {
            CurLoadout = CurMech.GetLoadout().GetName();
        }

        // Solidify the mech first.
        SolidifyMech();

        if( ! VerifyMech( evt ) ) {
            return;
        }

        // save the mech to HTML in the current location
        String file = "";
        if( CurMech.GetModel().isEmpty() ) {
            file = CurMech.GetName() + ".html";
        } else {
            file = CurMech.GetName() + " " + CurMech.GetModel() + ".html";
        }

        // need to double-check the filename and warn the user if there are 
        // special character
        try {
            CheckFileName( file );
        } catch( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "There was a problem with the file name:\n" + e.getMessage() );
            return;
        }

        if( ! GlobalOptions.HTMLPath.equals( "none" ) ) {
            file = GlobalOptions.HTMLPath + File.separator + file;
        }
        HTMLWriter HTMw = new HTMLWriter( CurMech, GlobalOptions );
        try {
            HTMw.WriteHTML( Constants.HTMLTemplateName, file );
        } catch( IOException e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "There was a problem writing the file:\n" + e.getMessage() );
            return;
        }

        // if there were no problems, let the user know how it went
        javax.swing.JOptionPane.showMessageDialog( this, "Mech saved successfully to HTML:\n" + file );

        // lastly, if this is an omnimech, reset the display to the last loadout
        cmbOmniVariant.setSelectedItem( CurLoadout );
        cmbOmniVariantActionPerformed( evt );
        setTitle( Constants.AppName + " " + Constants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
    }//GEN-LAST:event_btnExportHTMLActionPerformed

    private void mnuAboutSSWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutSSWActionPerformed
        dlgAboutBox about = new dlgAboutBox( this, true );
        about.setLocationRelativeTo( this );
        about.setVisible( true );
    }//GEN-LAST:event_mnuAboutSSWActionPerformed

    private void mnuExportTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportTXTActionPerformed
        btnExportTXTActionPerformed( evt );
    }//GEN-LAST:event_mnuExportTXTActionPerformed

    private void mnuExportHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportHTMLActionPerformed
        btnExportHTMLActionPerformed( evt );
    }//GEN-LAST:event_mnuExportHTMLActionPerformed

    private void mnuExportMTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportMTFActionPerformed
        btnExportMTFActionPerformed( evt );
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
            case Constants.STAR_LEAGUE:
                CurMech.SetYear( 2750, false );
                break;
            case Constants.SUCCESSION:
                CurMech.SetYear( 3025, false );
                break;
            case Constants.CLAN_INVASION:
                CurMech.SetYear( 3070, false );
                break;
            }
        } else {
            // ensure we have a good year.
            try{
                year = Integer.parseInt( txtProdYear.getText() ) ;
            } catch( NumberFormatException n ) {
                javax.swing.JOptionPane.showMessageDialog( this, "The production year is not a number." );
                txtProdYear.setText( "" );
                chkYearRestrict.setSelected( false );
                return;
            }

            // ensure the year is between the era years.
            switch ( cmbMechEra.getSelectedIndex() ) {
                case 0:
                    // Star League era
                    if( year < 2443 || year > 2800 ) {
                        javax.swing.JOptionPane.showMessageDialog( this, "The year does not fall within this era." );
                        txtProdYear.setText( "" );
                        chkYearRestrict.setSelected( false );
                        return;
                    }
                    break;
                case 1:
                    // Succession Wars era
                    if( year < 2801 || year > 3050 ) {
                        javax.swing.JOptionPane.showMessageDialog( this, "The year does not fall within this era." );
                        txtProdYear.setText( "" );
                        chkYearRestrict.setSelected( false );
                        return;
                    }
                    break;
                case 2:
                    // Clan Invasion Era
                    if( year < 3051 ) {
                        javax.swing.JOptionPane.showMessageDialog( this, "The year does not fall within this era." );
                        txtProdYear.setText( "" );
                        chkYearRestrict.setSelected( false );
                        return;
                    }
                    break;
                case 3:
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
    }//GEN-LAST:event_mnuNewMechActionPerformed

    private void cmbRulesLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRulesLevelActionPerformed
        if( Load ) { return; }
        int NewLevel = cmbRulesLevel.getSelectedIndex();
        int OldLevel = CurMech.GetLoadout().GetRulesLevel();

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
                RefreshEquipment();
                RecalcEquipment();
            } else {
                // can't.  reset to the default rules level and scold the user
                javax.swing.JOptionPane.showMessageDialog( this, "You cannot set an OmniMech's loadout to a Rules Level\nlower than it's chassis' Rules Level." );
                cmbRulesLevel.setSelectedIndex( CurMech.GetLoadout().GetRulesLevel() );
                return;
            }
        } else {
            CurMech.SetRulesLevel( NewLevel );
            CheckTonnage( true );

            // get the currently chosen selections
            SaveSelections();

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
            RefreshEquipment();

            // now reset the combo boxes to the closest choices we previously selected
            LoadSelections();

            // when a new rules level is selected, we have to recalculate the mech
            /*
             * We're testing this for the next few builds to see what happens.
            CurMech.GetLoadout().FullUnallocate();
            */
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
        String VariantName = "";

        // ensure there are no unplaced crits
        if( CurMech.GetLoadout().GetQueue().size() != 0 ) {
            javax.swing.JOptionPane.showMessageDialog( this, "You must place all items first." );
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
        if( Constants.BASELOADOUT_NAME.equals( VariantName ) ) {
            javax.swing.JOptionPane.showMessageDialog( this, "\"" + VariantName + "\" is reserved for the base loadout and cannot be used\nfor a new loadout.  Please choose another name." );
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
            javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
            return;
        }

        // fix the GUI
        FixTransferHandlers();
        SetLoadoutArrays();
        SetWeaponChoosers();
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
        dOpen.setLocationRelativeTo(this);
        dOpen.setSize( 750, 600 );
        dOpen.setVisible(true);
    }

    private void btnDeleteVariantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteVariantActionPerformed
        // see if the user actually wants to delete the variant
        int choice = javax.swing.JOptionPane.showConfirmDialog( this,
            "Are you sure you want to delete this variant?", "Delete Variant?", javax.swing.JOptionPane.YES_NO_OPTION );
        if( choice == 1 ) {
            return;
        } else {
            if( CurMech.GetLoadout().GetName().equals( Constants.BASELOADOUT_NAME ) ) {
                javax.swing.JOptionPane.showMessageDialog( this, "You cannot remove the base chassis." );
                return;
            }
        }

        // delete the variant
        CurMech.RemoveLoadout( CurMech.GetLoadout().GetName() );

        // refresh all the displays
        RefreshOmniVariants();
        FixTransferHandlers();
        SetLoadoutArrays();
        SetWeaponChoosers();
        FixJJSpinnerModel();
        FixHeatSinkSpinnerModel();
        RefreshOmniChoices();
        SolidifyJJManufacturer();
        RefreshSummary();
        RefreshInfoPane();
    }//GEN-LAST:event_btnDeleteVariantActionPerformed

    private void cmbOmniVariantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbOmniVariantActionPerformed
        String variant = (String) cmbOmniVariant.getSelectedItem();
        boolean changed = CurMech.HasChanged();

        CurMech.SetCurLoadout( variant );

        // now fix the GUI
        cmbRulesLevel.setSelectedIndex( CurMech.GetLoadout().GetRulesLevel() );
        FixTransferHandlers();
        SetLoadoutArrays();
        SetWeaponChoosers();
        cmbJumpJetType.setSelectedItem( CurMech.GetJumpJets().GetLookupName() );
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
        mnuSaveActionPerformed(evt);

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

        // Save the mech after the post is completed
        mnuSaveActionPerformed(evt);

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
            GlobalOptions.ClearUserInfo();
        }
	}//GEN-LAST:event_mnuClearUserDataActionPerformed

	private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveActionPerformed
        // Solidify the mech first.
        setCursor( Hourglass );
        
        SolidifyMech();

        if( ! VerifyMech( evt ) ) {
            setCursor( NormalCursor );
            return;
        }

        String filename = "";
        if( CurMech.GetModel().isEmpty() ) {
            filename = CurMech.GetName() + ".ssw";
        } else {
            filename = CurMech.GetName() + " " + CurMech.GetModel() + ".ssw";
        }

        // need to double-check the filename and warn the user if there are 
        // special character
        try {
            CheckFileName( filename );
        } catch( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "There was a problem with the file name:\n" + e.getMessage() );
            setCursor( NormalCursor );
            return;
        }

        // save the 'Mech
        File savemech;
        String test = GlobalOptions.SaveLoadPath + File.separator + filename;
        if (filename.equals(Prefs.get("LastOpenFile", ""))) {
            test = Prefs.get("LastOpenDirectory", "") + File.separator + Prefs.get("LastOpenFile", "");
        }
        File testfile = new File( test );
        if( testfile.exists() ) {
            // this is just a quick save on an existing mech.
            savemech = testfile;
        } else {
            System.err.println( "couldn't find the file: " + test );
            // a new save.  we'll show a dialogue and such.
            // get the filename we're going to save to
            JFileChooser fc = new JFileChooser( GlobalOptions.SaveLoadPath );
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
            fc.setSelectedFile( new File( filename ) );
            int returnVal = fc.showDialog( this, "Save Mech" );
            if( returnVal != JFileChooser.APPROVE_OPTION ) {
                setCursor( NormalCursor );
                return;
            }
            savemech = fc.getSelectedFile();
            
            //Since we are saving to a new file update the stored prefs
            try {
                Prefs.put("LastOpenDirectory", savemech.getCanonicalPath().replace(savemech.getName(), ""));
                Prefs.put("LastOpenFile", savemech.getName());
            } catch (IOException e) {
                javax.swing.JOptionPane.showMessageDialog( this, "There was a problem with the file:\n" + e.getMessage() );
                setCursor( NormalCursor );
                return;
            }
        }

        // exports the mech to XML format
        String CurLoadout = "";
        if( CurMech.IsOmnimech() ) {
            CurLoadout = CurMech.GetLoadout().GetName();
        }

        // save the mech to XML in the current location
        XMLWriter XMLw = new XMLWriter( CurMech );
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
            if (evt.getActionCommand().equals("Save Mech")) {
                javax.swing.JOptionPane.showMessageDialog( this, "Mech saved successfully:\n" + file );
            }
        } catch( IOException e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "There was a problem writing the file:\n" + e.getMessage() );
            setCursor( NormalCursor );
            return;
        }

        // lastly, if this is an omnimech, reset the display to the last loadout
        cmbOmniVariant.setSelectedItem( CurLoadout );
        cmbOmniVariantActionPerformed( evt );

        setCursor( NormalCursor );
        setTitle( Constants.AppName + " " + Constants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
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
        // Solidify the mech first.
        SolidifyMech();

        if( ! VerifyMech( evt ) ) {
            return;
        }

        String filename = "";
        if( CurMech.GetModel().isEmpty() ) {
            filename = CurMech.GetName() + ".ssw";
        } else {
            filename = CurMech.GetName() + " " + CurMech.GetModel() + ".ssw";
        }

        // need to double-check the filename and warn the user if there are 
        // special character
        try {
            CheckFileName( filename );
        } catch( Exception e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "There was a problem with the file name:\n" + e.getMessage() + "\nSaving will continue, but you should cahnge the filename." );
        }

        // get the filename we're going to save to
        JFileChooser fc = new JFileChooser( GlobalOptions.SaveLoadPath );
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
        fc.setSelectedFile( new File( filename ) );
        int returnVal = fc.showDialog( this, "Save Mech" );
        if( returnVal != JFileChooser.APPROVE_OPTION ) { return; }
        File savemech = fc.getSelectedFile();

        // exports the mech to XML format
        String CurLoadout = "";
        if( CurMech.IsOmnimech() ) {
            CurLoadout = CurMech.GetLoadout().GetName();
        }

        // since we're doing a Save As...  we'll clear the S7 ID so that you can
        // post a variant without creating an entirely new 'Mech
        CurMech.SetSolaris7ID( "0" );

        // save the mech to XML in the current location
        XMLWriter XMLw = new XMLWriter( CurMech );
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
            javax.swing.JOptionPane.showMessageDialog( this, "Mech saved successfully:\n" + file );
        } catch( IOException e ) {
            javax.swing.JOptionPane.showMessageDialog( this, "There was a problem writing the file:\n" + e.getMessage() );
            return;
        }

        // lastly, if this is an omnimech, reset the display to the last loadout
        cmbOmniVariant.setSelectedItem( CurLoadout );
        cmbOmniVariantActionPerformed( evt );
        setTitle( Constants.AppName + " " + Constants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
        CurMech.SetChanged( false );
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
        Vector v = CurMech.GetLoadout().GetQueue();
        int Index = lstCritsToPlace.getSelectedIndex();
        if( Index < 0 ) {
            btnAutoAllocate.setEnabled( false );
            btnSelectiveAllocate.setEnabled( false );
            btnRemoveItemCrits.setEnabled( false );
            return;
        }
        CurItem = (abPlaceable) v.get( Index );
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
        if( ! ( Equipment[ARTILLERY][lstChooseArtillery.getSelectedIndex()] instanceof Artillery ) ) { return; }
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
    CurMech.Visit( ArmorTons );

    // if we fix the spinner models, they should refresh the screen
    FixArmorSpinners();

    // of course, we'll also have to set the head spinner manually.
    javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHDArmor.getModel();
    n.setValue( (Object) CurMech.GetArmor().GetLocationArmor( Constants.LOC_HD ) );

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
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
        } else {
            CurMech.SetVoidSig( false );
        }
    } catch( Exception e ) {
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
    float freetons = CurMech.GetTonnage() - CurMech.GetCurrentTons() + CurMech.GetArmor().GetTonnage();

    if( freetons > CurMech.GetArmor().GetMaxTonnage() ) {
        freetons = CurMech.GetArmor().GetMaxTonnage();
    }

    ArmorTons.SetArmorTonnage( freetons );
    CurMech.Visit( ArmorTons );

    // if we fix the spinner models, they should refresh the screen
    FixArmorSpinners();

    // of course, we'll also have to set the head spinner manually.
    javax.swing.SpinnerNumberModel n = (SpinnerNumberModel) spnHDArmor.getModel();
    n.setValue( (Object) CurMech.GetArmor().GetLocationArmor( Constants.LOC_HD ) );

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
            javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
        try {
            CurMech.GetLoadout().SetSupercharger( false , 0, -1 );
        } catch( Exception x ) {
            // how the hell did we get an error removing it?
            javax.swing.JOptionPane.showMessageDialog( this, x.getMessage() );
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
    
    File tempFile = new File(Prefs.get("LastOpenDirectory", ""));
    JFileChooser fc = new JFileChooser( GlobalOptions.SaveLoadPath );
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
    fc.setCurrentDirectory(tempFile);
    int returnVal = fc.showDialog( this, "Load Mech" );
    if( returnVal != JFileChooser.APPROVE_OPTION ) { return m; }
    File loadmech = fc.getSelectedFile();
    String filename = "";
    try {
        filename = loadmech.getCanonicalPath();
        Prefs.put("LastOpenDirectory", loadmech.getCanonicalPath().replace(loadmech.getName(), ""));
        Prefs.put("LastOpenFile", loadmech.getName());
    } catch( Exception e ) {
        javax.swing.JOptionPane.showMessageDialog( this, "There was a problem opening the file:\n" + e.getMessage() );
        return m;
    }

    try {
        XMLReader XMLr = new XMLReader();
        m = XMLr.ReadMech( this, filename );
    } catch( Exception e ) {
        // had a problem loading the mech.  let the user know.
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
        return m;
    }

    return m;
}

private void LoadMechFromPreferences()
{
    Mech m = null;
    String filename = Prefs.get("LastOpenDirectory", "") + Prefs.get("LastOpenFile", "");
    if (! filename.isEmpty() ) {
        try {
            XMLReader XMLr = new XMLReader();
            m = XMLr.ReadMech( this, filename );
            CurMech = m;
            LoadMechIntoGUI();
        } catch( Exception e ) {
            // had a problem loading the mech.  let the user know.
            javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
        }
        CurMech = m;
        LoadMechIntoGUI();
    }
}

public void LoadMechIntoGUI() {
    // added for special situations
    Load = true;

    // Put it in the gui.
    UnlockGUIFromOmni();
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
        cmbMechType.setSelectedIndex( 1 );
    } else {
        cmbMechType.setSelectedIndex( 0 );
    }
    chkYearRestrict.setSelected( CurMech.IsYearRestricted() );
    txtProdYear.setText( "" + CurMech.GetYear() );
    cmbMechEra.setEnabled( true );
    cmbTechBase.setEnabled( true );
    txtProdYear.setEnabled( true );
    switch( CurMech.GetEra() ) {
        case Constants.STAR_LEAGUE:
            lblEraYears.setText( "2443 ~ 2800" );
            cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere" }));
            break;
        case Constants.SUCCESSION:
            lblEraYears.setText( "2801 ~ 3050" );
            cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
            break;
        case Constants.CLAN_INVASION:
            lblEraYears.setText( "3051 on" );
            cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
            break;
        case Constants.ALL_ERA:
            lblEraYears.setText( "Any" );
            cmbTechBase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inner Sphere", "Clan" }));
            break;
    }

    cmbRulesLevel.setSelectedIndex( CurMech.GetRulesLevel() );
    cmbMechEra.setSelectedIndex( CurMech.GetEra() );
    cmbTechBase.setSelectedIndex( CurMech.GetTechBase() );

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
    cmbInternalType.setSelectedItem( CurMech.GetIntStruc().GetLookupName() );
    cmbEngineType.setSelectedItem( CurMech.GetEngine().GetLookupName() );
    cmbGyroType.setSelectedItem( CurMech.GetGyro().GetLookupName() );
    cmbCockpitType.setSelectedItem( CurMech.GetCockpit().GetLookupName() );
    cmbPhysEnhance.setSelectedItem( CurMech.GetPhysEnhance().GetLookupName() );
    cmbHeatSinkType.setSelectedItem( CurMech.GetHeatSinks().GetLookupName() );
    cmbJumpJetType.setSelectedItem( CurMech.GetJumpJets().GetLookupName() );
    cmbArmorType.setSelectedItem( CurMech.GetArmor().GetLookupName() );
    FixWalkMPSpinner();
    FixHeatSinkSpinnerModel();
    FixJJSpinnerModel();
    RefreshInternalPoints();
    FixArmorSpinners();
    Weapons.RebuildPhysicals( CurMech );
    RefreshEquipment();
    chkCTCASE.setSelected( CurMech.HasCTCase() );
    chkLTCASE.setSelected( CurMech.HasLTCase() );
    chkRTCASE.setSelected( CurMech.HasRTCase() );
    chkUseTC.setSelected( CurMech.UsingTC() );
    chkNullSig.setSelected( CurMech.HasNullSig() );
    chkVoidSig.setSelected( CurMech.HasVoidSig() );
    chkBSPFD.setSelected( CurMech.HasBlueShield() );
    chkCLPS.setSelected( CurMech.HasChameleon() );
    chkEnviroSealing.setSelected( CurMech.HasEnviroSealing() );
    chkEjectionSeat.setSelected( CurMech.HasEjectionSeat() );
    SetLoadoutArrays();
    RefreshSummary();
    RefreshInfoPane();
    SetWeaponChoosers();
    ResetAmmo();

    // load the fluff image.
    ImageIcon newFluffImage = null;
    newFluffImage = new ImageIcon( CurMech.GetSSWImage() );

    if( newFluffImage != null ) {
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
    }
    lblFluffImage.setIcon( newFluffImage );

    edtOverview.setText( CurMech.GetOverview() );
    edtCapabilities.setText( CurMech.GetCapabilities() );
    edtHistory.setText( CurMech.GetHistory() );
    edtDeployment.setText( CurMech.GetDeployment() );
    edtVariants.setText( CurMech.GetVariants() );
    edtNotables.setText( CurMech.GetNotables() );
    edtAdditionalFluff.setText( CurMech.GetAdditional() );
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

    setTitle( Constants.AppName + " " + Constants.Version + " - " + CurMech.GetName() + " " + CurMech.GetModel() );
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

        TXTWriter txtw = new TXTWriter( CurMech, GlobalOptions );
        output = txtw.GetTextExport();
        java.awt.datatransfer.StringSelection export = new java.awt.datatransfer.StringSelection( output );

        // lastly, if this is an omnimech, reset the display to the last loadout
        cmbOmniVariant.setSelectedItem( CurLoadout );
        cmbOmniVariantActionPerformed( evt );

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
    Vector v = CurMech.GetLoadout().GetQueue();
    int Index = lstCritsToPlace.getSelectedIndex();
    if( Index < 0 ) {
        btnAutoAllocate.setEnabled( false );
        btnSelectiveAllocate.setEnabled( false );
        btnRemoveItemCrits.setEnabled( false );
        return;
    }
    CurItem = (abPlaceable) v.get( Index );
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
                CurMech.GetLoadout().SetCTCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkCTCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetCTCASEII( false, -1 );
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
                CurMech.GetLoadout().SetRACASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkRACASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetRACASEII( false, -1 );
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
                CurMech.GetLoadout().SetRTCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkRTCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetRTCASEII( false, -1 );
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
                CurMech.GetLoadout().SetRLCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkRLCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetRLCASEII( false, -1 );
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
                CurMech.GetLoadout().SetHDCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkHDCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetHDCASEII( false, -1 );
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
                CurMech.GetLoadout().SetLTCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkLTCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetLTCASEII( false, -1 );
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
                CurMech.GetLoadout().SetLLCASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkLLCASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetLLCASEII( false, -1 );
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
                CurMech.GetLoadout().SetLACASEII( true, -1 );
            } catch( Exception e ) {
                javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
                chkLACASE2.setSelected( false );
            }
        } else {
            try {
                CurMech.GetLoadout().SetLACASEII( false, -1 );
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

private void btnPrintSavedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintSavedActionPerformed
    mnuPrintSavedMechActionPerformed(evt);
}//GEN-LAST:event_btnPrintSavedActionPerformed

private void btnPrintBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintBatchActionPerformed
    mnuPrintBatchActionPerformed(evt);
}//GEN-LAST:event_btnPrintBatchActionPerformed

private void cmbMechTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMechTypeActionPerformed
        if( cmbMechType.getSelectedIndex() == 1 ) {
            if( CurMech.IsIndustrialmech() ) { return; }
        } else {
            if( ! CurMech.IsIndustrialmech() ) { return; }
        }
        if( cmbMechType.getSelectedIndex() == 0 ) {
            CurMech.SetBattlemech();
        } else {
            CurMech.SetIndustrialmech();
        }

        // check the tonnage
        CheckTonnage( false );

        // set the loadout arrays
        SetLoadoutArrays();

        // fix the armor spinners
        FixArmorSpinners();

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
        CheckOmnimech();

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
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
        return;
    }

    // now refresh the information panes
    RefreshSummary();
    RefreshInfoPane();

}//GEN-LAST:event_chkEjectionSeatActionPerformed

private void mnuPrintPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrintPreviewActionPerformed
    PrintMech p = new PrintMech( this, CurMech, null, false, false);
    p.SetPilotData( "", 4, 5);
    p.SetOptions( Prefs.getBoolean("UseCharts", false), false, CurMech.GetCurrentBV());

    Printer printer = new Printer();
    printer.AddMech(CurMech);

    PreviewDialog dlgPreview = new PreviewDialog(CurMech.GetFullName(), this, printer.Preview(), 1.25);
    dlgPreview.setSize(1024, 768);
    dlgPreview.setLocationRelativeTo(null);
    dlgPreview.setModal(true);
    dlgPreview.setResizable(true);
    dlgPreview.setVisible(true);
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
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
        javax.swing.JOptionPane.showMessageDialog( this, e.getMessage() );
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
    SolidifyMech();
    if (VerifyMech(evt)) {
        dForce.Add(CurMech);
    }
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
    SolidifyMech();
    dlgTextExport Text = new dlgTextExport( this, true, CurMech );
    Text.setLocationRelativeTo( this );
    Text.setVisible( true );
}//GEN-LAST:event_mnuTextTROActionPerformed

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
    private javax.swing.JButton btnAddToForceList;
    private javax.swing.JButton btnAddVariant;
    private javax.swing.JButton btnArmorTons;
    private javax.swing.JButton btnAutoAllocate;
    private javax.swing.JCheckBox btnBalanceArmor;
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
    private javax.swing.JButton btnPrintBatch;
    private javax.swing.JButton btnPrintIcon;
    private javax.swing.JButton btnPrintPreview;
    private javax.swing.JButton btnPrintSaved;
    private javax.swing.JButton btnRemainingArmor;
    private javax.swing.JButton btnRemoveEquip;
    private javax.swing.JButton btnRemoveItemCrits;
    private javax.swing.JButton btnSaveIcon;
    private javax.swing.JButton btnSelectiveAllocate;
    private javax.swing.JCheckBox chkArtemisLRM;
    private javax.swing.JCheckBox chkArtemisMML;
    private javax.swing.JCheckBox chkArtemisSRM;
    private javax.swing.JCheckBox chkArtemisVLRM;
    private javax.swing.JCheckBox chkArtemisVSRM;
    private javax.swing.JCheckBox chkBSPFD;
    private javax.swing.JCheckBox chkBoobyTrap;
    private javax.swing.JCheckBox chkBoosters;
    private javax.swing.JCheckBox chkCLPS;
    private javax.swing.JCheckBox chkCTCASE;
    private javax.swing.JCheckBox chkCTCASE2;
    private javax.swing.JCheckBox chkEjectionSeat;
    private javax.swing.JCheckBox chkEnviroSealing;
    private javax.swing.JCheckBox chkFHES;
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
    private javax.swing.JCheckBox chkMRMApollo;
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
    private javax.swing.JComboBox cmbOmniVariant;
    private javax.swing.JComboBox cmbPhysEnhance;
    private javax.swing.JComboBox cmbRulesLevel;
    private javax.swing.JComboBox cmbSCLoc;
    private javax.swing.JComboBox cmbTechBase;
    private javax.swing.JComboBox cmbTonnage;
    private javax.swing.JEditorPane edtAdditionalFluff;
    private javax.swing.JEditorPane edtCapabilities;
    private javax.swing.JEditorPane edtDeployment;
    private javax.swing.JEditorPane edtHistory;
    private javax.swing.JEditorPane edtNotables;
    private javax.swing.JEditorPane edtOverview;
    private javax.swing.JEditorPane edtVariants;
    private javax.swing.JEditorPane jEditorPane1;
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
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane25;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
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
    private javax.swing.JSeparator jSeparator3;
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
    private javax.swing.JLabel lblBFSA;
    private javax.swing.JLabel lblBFShort;
    private javax.swing.JLabel lblBFStructure;
    private javax.swing.JLabel lblBFWt;
    private javax.swing.JLabel lblCTArmorHeader;
    private javax.swing.JLabel lblCTHeader;
    private javax.swing.JLabel lblCTIntPts;
    private javax.swing.JLabel lblCTRArmorHeader;
    private javax.swing.JLabel lblCockpit;
    private javax.swing.JLabel lblDamagePerTon;
    private javax.swing.JLabel lblEngineType;
    private javax.swing.JLabel lblEraYears;
    private javax.swing.JLabel lblFALegend;
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
    private javax.swing.JLabel lblLAALegend;
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
    private javax.swing.JLabel lblLegendNote;
    private javax.swing.JLabel lblLegendTitle;
    private javax.swing.JLabel lblMechEra;
    private javax.swing.JLabel lblMechName;
    private javax.swing.JLabel lblMechType;
    private javax.swing.JLabel lblModel;
    private javax.swing.JLabel lblMotiveType;
    private javax.swing.JLabel lblMoveSummary;
    private javax.swing.JLabel lblPhysEnhance;
    private javax.swing.JLabel lblProdYear;
    private javax.swing.JLabel lblRAALegend;
    private javax.swing.JLabel lblRAArmorHeader;
    private javax.swing.JLabel lblRAHeader;
    private javax.swing.JLabel lblRAIntPts;
    private javax.swing.JLabel lblRALegened;
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
    private javax.swing.JMenuItem mnuClearUserData;
    private javax.swing.JMenuItem mnuCostBVBreakdown;
    private javax.swing.JMenuItem mnuCreateTCGMech;
    private javax.swing.JMenuItem mnuCredits;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenuItem mnuExportClipboard;
    private javax.swing.JMenuItem mnuExportHTML;
    private javax.swing.JMenuItem mnuExportMTF;
    private javax.swing.JMenuItem mnuExportTXT;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenuItem mnuLoad;
    private javax.swing.JMenuBar mnuMainMenu;
    private javax.swing.JMenuItem mnuNewMech;
    private javax.swing.JMenuItem mnuOpen;
    private javax.swing.JMenuItem mnuOptions;
    private javax.swing.JMenuItem mnuPostS7;
    private javax.swing.JMenuItem mnuPrintBatch;
    private javax.swing.JMenuItem mnuPrintCurrentMech;
    private javax.swing.JMenuItem mnuPrintPreview;
    private javax.swing.JMenuItem mnuPrintSavedMech;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JMenuItem mnuSaveAs;
    private javax.swing.JMenuItem mnuSummary;
    private javax.swing.JMenuItem mnuTextTRO;
    private javax.swing.JMenu mnuTools;
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
    private javax.swing.JPanel pnlPhysical;
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
    private javax.swing.JTable tblWeaponManufacturers;
    private javax.swing.JTabbedPane tbpFluffEditors;
    private javax.swing.JTabbedPane tbpMainTabPane;
    private javax.swing.JTabbedPane tbpWeaponChooser;
    private javax.swing.JToolBar tlbIconBar;
    private javax.swing.JTextField txtArmorModel;
    private javax.swing.JTextField txtChassisModel;
    private javax.swing.JTextField txtCommSystem;
    private javax.swing.JTextField txtEngineManufacturer;
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
    
}

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

package components;

import java.util.ArrayList;
import common.CommonTools;
import visitors.VFCSApolloLoader;
import visitors.VFCSArtemisIVLoader;
import visitors.VFCSArtemisVLoader;

public class BipedLoadout implements ifMechLoadout, ifLoadout {
    // Loadouts provide critical locations for all of a mech's equipment.
    // Each mech will have a basic loadout for it's chassis, equipment, and
    // extras.  Omnimechs may have several loadouts.  We're using arrays because
    // space inside a mech is fixed.

    // Declares
    private String Name,
                   Source = "";
    private final static EmptyItem NoItem = new EmptyItem();
    private ArrayList Queue = new ArrayList(),
                   NonCore = new ArrayList(),
                   Equipment = new ArrayList(),
                   TCList = new ArrayList(),
                   MechMods = new ArrayList();
    private Mech Owner;
    private JumpJetFactory Jumps;
    private HeatSinkFactory HeatSinks;
    private ActuatorSet Actuators;
    private CASE CTCase = new CASE(),
                   LTCase = new CASE(),
                   RTCase = new CASE();
    private CASEII HDCaseII = new CASEII( false ),
                   CTCaseII = new CASEII( false ),
                   LTCaseII = new CASEII( false ),
                   RTCaseII = new CASEII( false ),
                   LACaseII = new CASEII( false ),
                   RACaseII = new CASEII( false ),
                   LLCaseII = new CASEII( false ),
                   RLCaseII = new CASEII( false );
    private MechTurret HDTurret = new MechTurret( this ),
                       LTTurret = new MechTurret( this ),
                       RTTurret = new MechTurret( this );
    private boolean UseAIVFCS = false,
                    UseAVFCS = false,
                    UseApollo = false,
                    Use_TC = false,
                    UsingClanCASE = false,
                    YearSpecified = false,
                    YearRestricted = false,
                    Use_Dumper = false;
    private TargetingComputer CurTC = new TargetingComputer( this, false );
    private Dumper CurDumper = new Dumper(this);
    private ifMechLoadout BaseLoadout = null;
    private PowerAmplifier PowerAmp = new PowerAmplifier( this );
    private Supercharger SCharger = new Supercharger( this );
    private BoobyTrap BTrap = new BoobyTrap( this );
    private int RulesLevel = AvailableCode.RULES_TOURNAMENT,
                TechBase = AvailableCode.TECH_INNER_SPHERE,
                Era = AvailableCode.ERA_STAR_LEAGUE,
                ProductionEra = AvailableCode.PRODUCTION_ERA_AGE_OF_WAR,
                Year = 2750;

    // Fill up and initialize the critical space arrays.  This is where all the
    // stuff in the loadout will get placed.
    private abPlaceable HDCrits[] = { NoItem, NoItem, NoItem, NoItem, NoItem,
        NoItem };
    private abPlaceable CTCrits[] = { NoItem, NoItem, NoItem, NoItem, NoItem,
        NoItem, NoItem, NoItem, NoItem, NoItem, NoItem, NoItem };
    private abPlaceable RTCrits[] = { NoItem, NoItem, NoItem, NoItem, NoItem,
        NoItem, NoItem, NoItem, NoItem, NoItem, NoItem, NoItem };
    private abPlaceable LTCrits[] = { NoItem, NoItem, NoItem, NoItem, NoItem,
        NoItem, NoItem, NoItem, NoItem, NoItem, NoItem, NoItem };
    private abPlaceable RACrits[] = { NoItem, NoItem, NoItem, NoItem, NoItem,
        NoItem, NoItem, NoItem, NoItem, NoItem, NoItem, NoItem };
    private abPlaceable LACrits[] = { NoItem, NoItem, NoItem, NoItem, NoItem,
        NoItem, NoItem, NoItem, NoItem, NoItem, NoItem, NoItem };
    private abPlaceable RLCrits[] = { NoItem, NoItem, NoItem, NoItem, NoItem,
        NoItem };
    private abPlaceable LLCrits[] = { NoItem, NoItem, NoItem, NoItem, NoItem,
        NoItem };

    // Constructor
    public BipedLoadout( String name, Mech m ) {
        // provided to avoid confusion
        Name = name;
        Owner = m;
        Jumps = new JumpJetFactory( this );
        HeatSinks = new HeatSinkFactory( this );
        Actuators = new ActuatorSet( this, Owner );
    }

    public BipedLoadout( String name, Mech m, int BaseNumHS, HeatSinkFactory hs, JumpJetFactory jump ) {
        // provided for cloning purposes
        Name = name;
        Owner = m;
        Jumps = new JumpJetFactory( this, jump );
        HeatSinks = new HeatSinkFactory( this, BaseNumHS, hs.CurrentConfig(), hs.GetPlacedHeatSinks() );
        Actuators = new ActuatorSet( this, Owner );
    }

    // Public Methods
    public void SetName( String s ) {
        Name = s;

        Owner.SetChanged( true );
    }

    public String GetName() {
        return Name;
    }

    public void SetSource( String s ) {
        Source = s;
    }

    public String GetSource() {
        return Source;
    }

    public Mech GetMech() {
        return Owner;
    }

    public boolean IsQuad() {
        return false;
    }

public boolean IsTripod(){
        return false;
    }
    
    public abPlaceable GetNoItem() {
        return NoItem;
    }

    public HeatSinkFactory GetHeatSinks() {
        return HeatSinks;
    }

    public JumpJetFactory GetJumpJets() {
        return Jumps;
    }

    public ActuatorSet GetActuators() {
        return Actuators;
    }

    public int GetRulesLevel() {
        return RulesLevel;
    }

    public boolean SetRulesLevel( int NewLevel ) {
        if( Owner.IsOmnimech() ) {
            if( NewLevel < Owner.GetBaseRulesLevel() ) {
                return false;
            } else {
                RulesLevel = NewLevel;
                return true;
            }
        } else {
            RulesLevel = NewLevel;
            return true;
        }
    }

    public int GetTechBase() {
        return TechBase;
    }

    public void SetTechBase( int NewLevel ) {
        TechBase = NewLevel;
    }

    public int GetEra() {
        return Era;
    }

    public boolean SetEra( int era ) {
        if( Owner.IsOmnimech() ) {
            if( era < Owner.GetBaseEra() ) {
                return false;
            } else {
                Era = era;
                return true;
            }
        } else {
            Era = era;
            return true;
        }
    }

    public int GetProductionEra() {
        return ProductionEra;
    }

    public boolean SetProductionEra( int era ) {
        ProductionEra = era;
        return true;
    }

    public int GetYear() {
        return Year;
    }

    public void SetYear( int year, boolean specified ) {
        Year = year;
        YearSpecified = specified;
    }

    public boolean YearWasSpecified() {
        return YearSpecified;
    }

    public void SetYearRestricted( boolean b ) {
        YearRestricted = b;
    }

    public boolean IsYearRestricted() {
        return YearRestricted;
    }

    public void AddToQueue( abPlaceable p ) {
        // Adds the specified placeable to the general queue for manual placement

        // if this component has 0 total crits, we'll simply ignore it
        if( p.NumCrits() <= 0 ) {
            return;
        }

        // ensure the item isn't already in the queue
        boolean add = false;
        for( int i = 0; i < Queue.size(); i++ ) {
            if( Queue.get( i ) instanceof EquipmentCollection ) {
                if( ((EquipmentCollection) Queue.get( i )).Add( p ) ) {
                    add = true;
                    break;
                }
            } else {
                if( p == Queue.get( i ) ) { return; }
                if( p.LookupName().equals( ((abPlaceable) Queue.get( i )).LookupName() ) ) {
                    // create a new equipment collection for these items.
                    EquipmentCollection e = new EquipmentCollection( this );
                    e.Add( p );
                    abPlaceable exist = (abPlaceable) Queue.get( i );
                    e.Add( exist );
                    Queue.remove( exist );
                    Queue.add( e );
                    add = true;
                    break;
                }
            }
        }

        if( ! add ) { Queue.add( p ); }

        // check to see if this is a core component
        if( ! p.CoreComponent() ) {
            // add it to the non core list
            if( ! NonCore.contains( p ) ) {
                NonCore.add( p );
            }

            // add it to the equipment list if appropriate
            if( ! ( p instanceof Ammunition ) ) {
                if( ! Equipment.contains( p ) ) {
                    Equipment.add( p );
                }
            }

            // add it to the TC list if appropriate.
            if( p instanceof ifWeapon ) {
                if( ! TCList.contains( p ) ) {
                    if( ((ifWeapon) p).IsTCCapable() ) {
                        TCList.add( p );
                    }
                }
            }

            // if a VGL, reset the arc
            if( p instanceof VehicularGrenadeLauncher ) {
                ((VehicularGrenadeLauncher) p).SetArcFore();
            }
        }

        // if the component has a modifier, add it to the 'Mech
        if( p.GetMechModifier() != null ) {
            if (!p.GetMechModifier().ModifyConfigOnly())
                Owner.AddMechModifier( p.GetMechModifier() );
            else
                AddMechModifier(p.GetMechModifier());
        }

        Owner.SetChanged( true );
    }

    public void RemoveFromQueue( abPlaceable p ) {
        // removes the selected item from the queue.  This makes the object
        // completely disappear from the loadout so should only be used after
        // placing it or when actually removing it from the loadout.
        if( Queue.contains( p ) ) {
            Queue.remove( p );
        } else {
            for( int i = 0; i < Queue.size(); i++ ) {
                if( Queue.get( i ) instanceof EquipmentCollection ) {
                    EquipmentCollection e = (EquipmentCollection) Queue.get( i );
                    if( e.SameType( p ) ) {
                        e.Remove( p );
                        if( e.IsEmpty() ) {
                            Queue.remove( e );
                        }
                        break;
                    }
                }
            }
        }

        Owner.SetChanged( true );
    }

    public abPlaceable GetFromQueueByIndex( int Index ) {
        if( Queue.get( Index ) instanceof EquipmentCollection ) {
            return ((EquipmentCollection) Queue.get( Index )).GetType();
        } else {
            return (abPlaceable) Queue.get( Index );
        }
    }

    public boolean QueueContains( abPlaceable p ) {
        if( Queue.contains( p ) ) { return true; }
        for( int i = 0; i < Queue.size(); i++ ) {
            if( Queue.get( i ) instanceof EquipmentCollection ) {
                if( ((EquipmentCollection) Queue.get( i )).Contains( p ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public EquipmentCollection GetCollection( abPlaceable p ) {
        for( int i = 0; i < Queue.size(); i++ ) {
            if( Queue.get( i ) instanceof EquipmentCollection ) {
                if( ((EquipmentCollection) Queue.get( i )).Contains( p ) ) {
                    return (EquipmentCollection) Queue.get( i );
                }
            }
        }
        return null;
    }

    public ArrayList GetQueue() {
        // returns the queue
        return Queue;
    }

    public ArrayList GetNonCore() {
        return NonCore;
    }

    public ArrayList GetEquipment() {
        return Equipment;
    }

    public ArrayList GetTCList() {
        return TCList;
    }

    public void ClearQueue() {
        // This method completely clears the queue, so be careful when using it.
        Queue.clear();
        NonCore.clear();
        Equipment.clear();
        TCList.clear();
        MechMods.clear();

        Owner.SetChanged( true );
    }

    public void FullUnallocate() {
        // this removes all items from the loadout.

        // Check for jump jets and send them to the queue
        abPlaceable[] a = Jumps.GetPlacedJumps();
        for( int i = 0; i < a.length; i++ ) {
            UnallocateAll( a[i], true );
        }

        for( int i = 0; i < 6; i++ ) {
            HDCrits[i] = NoItem;
            CTCrits[i] = NoItem;
            RTCrits[i] = NoItem;
            LTCrits[i] = NoItem;
            RACrits[i] = NoItem;
            LACrits[i] = NoItem;
            RLCrits[i] = NoItem;
            LLCrits[i] = NoItem;
        }

        for( int i = 6; i < 12; i++ ) {
            CTCrits[i] = NoItem;
            RTCrits[i] = NoItem;
            LTCrits[i] = NoItem;
            RACrits[i] = NoItem;
            LACrits[i] = NoItem;
        }

        for( int i = NonCore.size() - 1; i >= 0; i-- ) {
            if( ! Queue.contains( NonCore.get( i ) ) ) {
                Queue.add( NonCore.get(i) );
            }
        }

        Owner.SetChanged( true );
    }

    public void ClearLoadout() {
        // Completely clears the loadout and queue.  IRREVERSIBLE!
        for( int i = 0; i < 6; i++ ) {
            HDCrits[i] = NoItem;
            CTCrits[i] = NoItem;
            RTCrits[i] = NoItem;
            LTCrits[i] = NoItem;
            RACrits[i] = NoItem;
            LACrits[i] = NoItem;
            RLCrits[i] = NoItem;
            LLCrits[i] = NoItem;
        }

        for( int i = 6; i < 12; i++ ) {
            CTCrits[i] = NoItem;
            RTCrits[i] = NoItem;
            LTCrits[i] = NoItem;
            RACrits[i] = NoItem;
            LACrits[i] = NoItem;
        }

        Queue.clear();
        NonCore.clear();
        Equipment.clear();
        TCList.clear();
        MechMods.clear();

        Owner.SetChanged( true );
    }

    public void SafeClearLoadout() {
        // this routine clears the loadout of any items that are non-core
        for( int i = NonCore.size() - 1; i >= 0; i-- ) {
            abPlaceable p = (abPlaceable) NonCore.get( i );
            if( ! p.LocationLocked() ) {
                Remove( (abPlaceable) NonCore.get( i ) );
            } else if( p instanceof Talons ) {
                Remove( (abPlaceable) NonCore.get( i ) );
            }
        }

        Owner.SetChanged( true );
    }

    public void SafeUnallocateHD() {
        for ( int i = 0; i < 6; i++ ) {
            UnallocateAll( HDCrits[i], false );
        }
    }
    public void SafeMassUnallocate() {
        // this unallocates all non-core and movable items from the loadout
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] != NoItem &! HDCrits[i].LocationLocked() ) {
                UnallocateAll( HDCrits[i], false );
            }
            if( CTCrits[i] != NoItem &! CTCrits[i].LocationLocked() ) {
                UnallocateAll( CTCrits[i], false );
            }
            if( RTCrits[i] != NoItem &! RTCrits[i].LocationLocked() ) {
                UnallocateAll( RTCrits[i], false );
            }
            if( LTCrits[i] != NoItem &! LTCrits[i].LocationLocked() ) {
                UnallocateAll( LTCrits[i], false );
            }
            if( RACrits[i] != NoItem &! RACrits[i].LocationLocked() ) {
                UnallocateAll( RACrits[i], false );
            }
            if( LACrits[i] != NoItem &! LACrits[i].LocationLocked() ) {
                UnallocateAll( LACrits[i], false );
            }
            if( RLCrits[i] != NoItem &! RLCrits[i].LocationLocked() ) {
                UnallocateAll( RLCrits[i], false );
            }
            if( LLCrits[i] != NoItem &! LLCrits[i].LocationLocked() ) {
                UnallocateAll( LLCrits[i], false );
            }
        }

        // now for the last six slots in locations that have them
        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] != NoItem &! CTCrits[i].LocationLocked() ) {
                UnallocateAll( CTCrits[i], false );
            }
            if( RTCrits[i] != NoItem &! RTCrits[i].LocationLocked() ) {
                UnallocateAll( RTCrits[i], false );
            }
            if( LTCrits[i] != NoItem &! LTCrits[i].LocationLocked() ) {
                UnallocateAll( LTCrits[i], false );
            }
            if( RACrits[i] != NoItem &! RACrits[i].LocationLocked() ) {
                UnallocateAll( RACrits[i], false );
            }
            if( LACrits[i] != NoItem &! LACrits[i].LocationLocked() ) {
                UnallocateAll( LACrits[i], false );
            }
        }

        Owner.SetChanged( true );
    }

    public void Transfer( ifMechLoadout l ) {
        // any time we get a new loadout, we must transfer the non-core components
        // to the new loadout.
        abPlaceable p;
        for( int i = NonCore.size(); i > 0; i-- ) {
            p = (abPlaceable) NonCore.get(NonCore.size()-1);
            l.AddToQueue( p );
            NonCore.remove( p );
        }
    }

    public void AddTo( abPlaceable p, int Loc, int SIndex ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                AddToHD( p, SIndex );
                break;
            case LocationIndex.MECH_LOC_CT:
                AddToCT( p, SIndex );
                break;
            case LocationIndex.MECH_LOC_LT:
                AddToLT( p, SIndex );
                break;
            case LocationIndex.MECH_LOC_RT:
                AddToRT( p, SIndex );
                break;
            case LocationIndex.MECH_LOC_LA:
                AddToLA( p, SIndex );
                break;
            case LocationIndex.MECH_LOC_RA:
                AddToRA( p, SIndex );
                break;
            case LocationIndex.MECH_LOC_LL:
                AddToLL( p, SIndex );
                break;
            case LocationIndex.MECH_LOC_RL:
                AddToRL( p, SIndex );
                break;
            default:
                throw new Exception( "Location not recognized or not an integer\nwhile placing " + p.CritName() );
        }
    }

    public void AddToHD( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the head
        for( int i = 0; i < HDCrits.length; i++ ) {
            if ( HDCrits[i] == NoItem) {
                AddToHD( p, i );
                return;
            }
        }
        throw new Exception("Unable to add item to Head");
        /*
        for( int i = HDCrits.length - 1; i >= 0; i-- ) {
            if( HDCrits[i] == NoItem ) {
                AddToHD( p, i );
                break;
            }
        }
        */
    }

    public void AddToCT( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the center torso
        if( p.Contiguous() ) {
            for( int i = 0; i < CTCrits.length; i++ ) {
                if( CTCrits[i] == NoItem ) {
                    AddToCT( p, i );
                    return;
                }
            }
        } else {
            for( int i = CTCrits.length - 1; i >= 0; i-- ) {
                if( CTCrits[i] == NoItem ) {
                    AddToCT( p, i );
                    return;
                }
            }
        }
        throw new Exception("Unable to add item.");
    }

    public void AddToLT( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the left torso
        if( p.Contiguous() ) {
            for( int i = 0; i < LTCrits.length; i++ ) {
                if( LTCrits[i] == NoItem ) {
                    AddToLT( p, i );
                    break;
                }
            }
        } else {
            for( int i = LTCrits.length - 1; i >= 0; i-- ) {
                if( LTCrits[i] == NoItem ) {
                    AddToLT( p, i );
                    break;
                }
            }
        }
    }

    public void AddToRT( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the right torso
        if( p.Contiguous() ) {
            for( int i = 0; i < RTCrits.length; i++ ) {
                if( RTCrits[i] == NoItem ) {
                    AddToRT( p, i );
                    break;
                }
            }
        } else {
            for( int i = RTCrits.length - 1; i >= 0; i-- ) {
                if( RTCrits[i] == NoItem ) {
                    AddToRT( p, i );
                    break;
                }
            }
        }
    }

    public void AddToLA( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the left arm
        if( p.Contiguous() ) {
            for( int i = 0; i < LACrits.length; i++ ) {
                if( LACrits[i] == NoItem ) {
                    AddToLA( p, i );
                    break;
                }
            }
        } else {
            for( int i = LACrits.length - 1; i >= 0; i-- ) {
                if( LACrits[i] == NoItem ) {
                    AddToLA( p, i );
                    break;
                }
            }
        }
    }

    public void AddToRA( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the right arm
        if( p.Contiguous() ) {
            for( int i = 0; i < RACrits.length; i++ ) {
                if( RACrits[i] == NoItem ) {
                    AddToRA( p, i );
                    break;
                }
            }
        } else {
            for( int i = RACrits.length - 1; i >= 0; i-- ) {
                if( RACrits[i] == NoItem ) {
                    AddToRA( p, i );
                    break;
                }
            }
        }
    }

    public void AddToLL( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the left leg
        if( p.Contiguous() ) {
            for( int i = 0; i < LLCrits.length; i++ ) {
                if( LLCrits[i] == NoItem ) {
                    AddToLL( p, i );
                    break;
                }
            }
        } else {
            for( int i = LLCrits.length - 1; i >= 0; i-- ) {
                if( LLCrits[i] == NoItem ) {
                    AddToLL( p, i );
                    break;
                }
            }
        }
    }

    public void AddToRL( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the right leg
        if( p.Contiguous() ) {
            for( int i = 0; i < RLCrits.length; i++ ) {
                if( RLCrits[i] == NoItem ) {
                    AddToRL( p, i );
                    break;
                }
            }
        } else {
            for( int i = RLCrits.length - 1; i >= 0; i-- ) {
                if( RLCrits[i] == NoItem ) {
                    AddToRL( p, i );
                    break;
                }
            }
        }
    }

    public void AddToHD( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocHD() ) {
            throw new Exception( p.CritName() +
                " cannot be allocated to the head." );
        } else if( p.LookupName().equals( "HarJel" ) ) {
            if( Find( Owner.GetCockpit() ) == LocationIndex.MECH_LOC_HD ) {
                throw new Exception( p.CritName() + " cannot be allocated to the head\nbecause the head contains the cockpit." );
            }
        } else if( p.LookupName().equals( "MW Aquatic Survival System" ) ) {
            if( Find( Owner.GetCockpit() ) != LocationIndex.MECH_LOC_HD ) {
                throw new Exception( p.CritName() + " cannot be allocated to the head\nbecause the head does not contain the cockpit." );
            }
        } else if( p instanceof ModularArmor ) {
            for( int i = 0; i < NonCore.size(); i++ ) {
                if ( NonCore.get( i ) instanceof ModularArmor ) {
                    if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_HD ) {
                        throw new Exception( p.CritName() + " cannot be allocated to the head\nbecause only one may be mounted in any location." );
                    }
                }
            }
        } else {
           if( p instanceof PhysicalWeapon ) {
           // Ensure that no other physical weapons are mounted in this location
                for( int i = 0; i < NonCore.size(); i++ ){
                    if ( NonCore.get( i ) instanceof PhysicalWeapon && Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_HD)
                        throw new Exception( p.CritName() +
                            " cannot be allocated to the head because\nthe head already mounts a physical weapon." );
                }
           }
        }
        Allocate( p, SIndex, HDCrits );
    }

    public void AddToCT( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocCT() ) {
            throw new Exception( p.CritName() +
                " cannot be allocated to the center torso." );
        } else if( p.LookupName().equals( "HarJel" ) ) {
            if( Find( Owner.GetCockpit() ) == LocationIndex.MECH_LOC_CT ) {
                throw new Exception( p.CritName() + " cannot be allocated to the center torso\nbecause the center torso contains the cockpit." );
            }
        } else if( p.LookupName().equals( "MW Aquatic Survival System" ) ) {
            if( Find( Owner.GetCockpit() ) != LocationIndex.MECH_LOC_CT ) {
                throw new Exception( p.CritName() + " cannot be allocated to the center torso\nbecause the center torso does not contain the cockpit." );
            }
        } else if( p instanceof ModularArmor ) {
            for( int i = 0; i < NonCore.size(); i++ ) {
                if ( NonCore.get( i ) instanceof ModularArmor ) {
                    if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_CT ) {
                        throw new Exception( p.CritName() + " cannot be allocated to the center torso\nbecause only one may be mounted in any location." );
                    }
                }
            }
        } else {
           if( p instanceof PhysicalWeapon ) {
           // Ensure that no other physical weapons are mounted in this location
                for( int i = 0; i < NonCore.size(); i++ ){
                    if ( NonCore.get( i ) instanceof PhysicalWeapon && Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_CT)
                        throw new Exception( p.CritName() +
                            " cannot be allocated to the center torso because\nthe torso already mounts a physical weapon." );
                }
           }
        }
        Allocate( p, SIndex, CTCrits );
    }

    public void AddToRT( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocTorso() ) {
            throw new Exception( p.CritName() +
                " cannot be allocated to the right torso." );
        } else if( p.LookupName().equals( "MW Aquatic Survival System" ) ) {
            throw new Exception( p.CritName() + " cannot be allocated to the right torso\nbecause the right torso does not contain the cockpit." );
        } else if( p instanceof ModularArmor ) {
            for( int i = 0; i < NonCore.size(); i++ ) {
                if ( NonCore.get( i ) instanceof ModularArmor ) {
                    if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_RT ) {
                        throw new Exception( p.CritName() + " cannot be allocated to the right torso\nbecause only one may be mounted in any location." );
                    }
                }
            }
        } else if( p instanceof PhysicalWeapon ) {
           // Ensure that no other physical weapons are mounted in this location
                for( int i = 0; i < NonCore.size(); i++ ){
                    if ( NonCore.get( i ) instanceof PhysicalWeapon && Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_RT)
                        throw new Exception( p.CritName() +
                            " cannot be allocated to the right torso because\nthe torso already mounts a physical weapon." );
            }
        }
        Allocate( p, SIndex, RTCrits );
    }

    public void AddToLT( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocTorso() ) {
            throw new Exception( p.CritName() +
                " cannot be allocated to the left torso." );
        } else if( p.LookupName().equals( "MW Aquatic Survival System" ) ) {
            throw new Exception( p.CritName() + " cannot be allocated to the left torso\nbecause the left torso does not contain the cockpit." );
        } else if( p instanceof ModularArmor ) {
            for( int i = 0; i < NonCore.size(); i++ ) {
                if ( NonCore.get( i ) instanceof ModularArmor ) {
                    if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_LT ) {
                        throw new Exception( p.CritName() + " cannot be allocated to the left torso\nbecause only one may be mounted in any location." );
                    }
                }
            }
        } else if( p instanceof PhysicalWeapon ) {
            // Ensure that no other physical weapons are mounted in this location
            for( int i = 0; i < NonCore.size(); i++ ){
                if ( NonCore.get( i ) instanceof PhysicalWeapon && Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_LT)
                    throw new Exception( p.CritName() +
                        " cannot be allocated to the left torso because\nthe torso already mounts a physical weapon." );
            }
        }
        Allocate( p, SIndex, LTCrits );
    }

    public void AddToRA( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocArms() ) {
            throw new Exception( p.CritName() +
                " cannot be allocated to the right arm." );
        } else if( p.LookupName().equals( "MW Aquatic Survival System" ) ) {
            throw new Exception( p.CritName() + " cannot be allocated to the right arm\nbecause the right arm does not contain the cockpit." );
        } else if( p instanceof ModularArmor ) {
            for( int i = 0; i < NonCore.size(); i++ ) {
                if ( NonCore.get( i ) instanceof ModularArmor ) {
                    if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_RA ) {
                        throw new Exception( p.CritName() + " cannot be allocated to the right arm\nbecause only one may be mounted in any location." );
                    }
                }
            }
        } else if( p instanceof PhysicalWeapon ) {
            // Ensure that no other physical weapons of the same class are mounted in this location,
            for( int i = 0; i < NonCore.size(); i++ ){
                if ( NonCore.get( i ) instanceof PhysicalWeapon && Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_RA) {
                    if ( ((PhysicalWeapon)p).GetPWClass() == ((PhysicalWeapon)NonCore.get( i )).GetPWClass() )
                        throw new Exception( p.CritName() +
                            " cannot be allocated to the right arm because\nthe arm already mounts a physical weapon of the same class." );
                }
            }

            // Check for proper actuators
            if ( ((PhysicalWeapon)p).RequiresHand() && ! ( RACrits[3] instanceof Actuator ) ) {
                throw new Exception( p.CritName() +
                    " cannot be allocated to the right arm because\nthe arm does not have a hand actuator." );
            }
            if ( ((PhysicalWeapon)p).RequiresLowerArm() && ! ( RACrits[2] instanceof Actuator ) ) {
                throw new Exception( p.CritName() +
                    " cannot be allocated to the right arm because\nthe arm does not have a lower arm actuator." );
            }
            if ( ((PhysicalWeapon)p).ReplacesHand() && ( RACrits[3] instanceof Actuator ) ) {
                throw new Exception( p.CritName() +
                    " cannot be allocated to the right arm because\nthe arm contains a hand actuator." );
            }
            if ( ((PhysicalWeapon)p).ReplacesLowerArm() && ( RACrits[2] instanceof Actuator ) ) {
                throw new Exception( p.CritName() +
                    " cannot be allocated to the right arm because\nthe arm contains a lower arm actuator." );
            }
        } else if( p instanceof ifWeapon ) {
            if( ((ifWeapon) p).OmniRestrictActuators() && Owner.IsOmnimech() ) {
                if( ( RACrits[2] instanceof Actuator ) || ( RACrits[3] instanceof Actuator ) ) {
                    // check for physical weapons before removing the actuators
                    // otherwise throw an exception
                    for( int i = 0; i < NonCore.size(); i++ ) {
                        if( NonCore.get( i ) instanceof PhysicalWeapon ) {
                            if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_RA ) {
                                throw new Exception( p.CritName() +
                                    " cannot be allocated to the right arm\n" +
                                    "because the arm contains lower arm or hand actuators." );
                            }
                        }
                    }
                    Actuators.RemoveRightLowerArm();
                }
            }
        }
        Allocate( p, SIndex, RACrits );
    }

    public void AddToLA( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocArms() ) {
            throw new Exception( p.CritName() +
                " cannot be allocated to the left arm." );
        } else if( p.LookupName().equals( "MW Aquatic Survival System" ) ) {
            throw new Exception( p.CritName() + " cannot be allocated to the left arm\nbecause the left arm does not contain the cockpit." );
        } else if( p instanceof ModularArmor ) {
            for( int i = 0; i < NonCore.size(); i++ ) {
                if ( NonCore.get( i ) instanceof ModularArmor ) {
                    if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_LA ) {
                        throw new Exception( p.CritName() + " cannot be allocated to the left arm\nbecause only one may be mounted in any location." );
                    }
                }
            }
        } else if( p instanceof PhysicalWeapon ) {
            // Ensure that no other physical weapons of the same class are mounted in this location,
            for( int i = 0; i < NonCore.size(); i++ ){
                if ( NonCore.get( i ) instanceof PhysicalWeapon && Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_LA) {
                    if ( ((PhysicalWeapon)p).GetPWClass() == ((PhysicalWeapon)NonCore.get( i )).GetPWClass() )
                        throw new Exception( p.CritName() +
                            " cannot be allocated to the left arm because\nthe arm already mounts a physical weapon of the same class." );
                }
            }

            // Check for proper actuators
            if ( ((PhysicalWeapon)p).RequiresHand() && ! ( LACrits[3] instanceof Actuator ) ) {
                throw new Exception( p.CritName() +
                    " cannot be allocated to the left arm because\nthe arm does not have a hand actuator." );
            }
            if ( ((PhysicalWeapon)p).RequiresLowerArm() && ! ( LACrits[2] instanceof Actuator ) ) {
                throw new Exception( p.CritName() +
                    " cannot be allocated to the left arm because\nthe arm does not have a lower arm actuator." );
            }
            if ( ((PhysicalWeapon)p).ReplacesHand() && ( LACrits[3] instanceof Actuator ) ) {
                throw new Exception( p.CritName() +
                    " cannot be allocated to the left arm because\nthe arm contains a hand actuator." );
            }
            if ( ((PhysicalWeapon)p).ReplacesLowerArm() && ( LACrits[2] instanceof Actuator ) ) {
                throw new Exception( p.CritName() +
                    " cannot be allocated to the left arm because\nthe arm contains a lower arm actuator." );
            }
        } else if( p instanceof ifWeapon ) {
            if( ((ifWeapon) p).OmniRestrictActuators() && Owner.IsOmnimech() ) {
                if( ( LACrits[2] instanceof Actuator ) || ( LACrits[3] instanceof Actuator ) ) {
                    // check for physical weapons before removing the actuators
                    // otherwise throw an exception
                    for( int i = 0; i < NonCore.size(); i++ ) {
                        if( NonCore.get( i ) instanceof PhysicalWeapon ) {
                            if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_LA ) {
                                throw new Exception( p.CritName() +
                                    " cannot be allocated to the right arm\n" +
                                    "because the arm contains lower arm or hand actuators." );
                            }
                        }
                    }
                    Actuators.RemoveLeftLowerArm();
                }
            }
        }
        Allocate( p, SIndex, LACrits );
    }

    public void AddToRL( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocLegs() ) {
            throw new Exception( p.CritName() +
                " cannot be allocated to the right leg." );
        } else if( p.LookupName().equals( "MW Aquatic Survival System" ) ) {
            throw new Exception( p.CritName() + " cannot be allocated to the right leg\nbecause the right leg does not contain the cockpit." );
        } else if( p instanceof ModularArmor ) {
            for( int i = 0; i < NonCore.size(); i++ ) {
                if ( NonCore.get( i ) instanceof ModularArmor ) {
                    if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_RL ) {
                        throw new Exception( p.CritName() + " cannot be allocated to the right leg\nbecause only one may be mounted in any location." );
                    }
                }
            }
        } else if( p instanceof PhysicalWeapon ) {
             // Ensure that no other physical weapons are mounted in this location
             for( int i = 0; i < NonCore.size(); i++ ){
                 if ( NonCore.get( i ) instanceof PhysicalWeapon && Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_RL)
                     if ( ((PhysicalWeapon)p).GetPWClass() != PhysicalWeapon.PW_CLASS_TALON )
                         throw new Exception( p.CritName() +
                             " cannot be allocated to the right leg because\nthe leg already mounts a physical weapon." );
             }
        }
        Allocate( p, SIndex, RLCrits );
    }

    public void AddToLL( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocLegs() ) {
            throw new Exception( p.CritName() +
                " cannot be allocated to the left leg." );
        } else if( p.LookupName().equals( "MW Aquatic Survival System" ) ) {
            throw new Exception( p.CritName() + " cannot be allocated to the left leg\nbecause the left leg does not contain the cockpit." );
        } else if( p instanceof ModularArmor ) {
                for( int i = 0; i < NonCore.size(); i++ ) {
                    if ( NonCore.get( i ) instanceof ModularArmor ) {
                        if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_LL ) {
                            throw new Exception( p.CritName() + " cannot be allocated to the left leg\nbecause only one may be mounted in any location." );
                        }
                    }
                }
        } else if( p instanceof PhysicalWeapon ) {
             // Ensure that no other physical weapons are mounted in this location
             for( int i = 0; i < NonCore.size(); i++ ) {
                 if ( NonCore.get( i ) instanceof PhysicalWeapon && Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_LL)
                     if ( ((PhysicalWeapon)p).GetPWClass() != PhysicalWeapon.PW_CLASS_TALON )
                         throw new Exception( p.CritName() +
                             " cannot be allocated to the left leg because\nthe leg already mounts a physical weapon." );
             }
        }
        Allocate( p, SIndex, LLCrits );
    }

    public abPlaceable[] GetHDCrits() {
        // get the head criticals locations for reporting
        return HDCrits;
    }

    public abPlaceable[] GetCTCrits() {
        // get the center torso criticals locations for reporting
        return CTCrits;
    }

    public abPlaceable[] GetRTCrits() {
        // get the right torso criticals locations for reporting
        return RTCrits;
    }

    public abPlaceable[] GetLTCrits() {
        // get the left torso criticals locations for reporting
        return LTCrits;
    }

    public abPlaceable[] GetRACrits() {
        // get the right arm criticals locations for reporting
        return RACrits;
    }

    public abPlaceable[] GetLACrits() {
        // get the left arm criticals locations for reporting
        return LACrits;
    }

    public abPlaceable[] GetRLCrits() {
        // get the right leg criticals locations for reporting
        return RLCrits;
    }

    public abPlaceable[] GetLLCrits() {
        // get the left leg criticals locations for reporting
        return LLCrits;
    }

    public abPlaceable[] GetCrits( int Loc ) {
        switch( Loc ) {
        case LocationIndex.MECH_LOC_HD:
            return HDCrits;
        case LocationIndex.MECH_LOC_CT:
            return CTCrits;
        case LocationIndex.MECH_LOC_LT:
            return LTCrits;
        case LocationIndex.MECH_LOC_RT:
            return RTCrits;
        case LocationIndex.MECH_LOC_LA:
            return LACrits;
        case LocationIndex.MECH_LOC_RA:
            return RACrits;
        case LocationIndex.MECH_LOC_LL:
            return LLCrits;
        case LocationIndex.MECH_LOC_RL:
            return RLCrits;
        default:
            return null;
        }
    }

    public int Find( abPlaceable p ) {
        // find the item in the loadout and let us know where it is.  For larger
        // item that may be split, it returns the innermost location on the
        // transfer diagram
        int innermost = 11;
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] == p ) {
                innermost = LocationIndex.MECH_LOC_HD;
            }
            if( CTCrits[i] == p ) {
                if( innermost > LocationIndex.MECH_LOC_CT ) {
                    innermost = LocationIndex.MECH_LOC_CT;
                }
            }
            if( LTCrits[i] == p ) {
                if( innermost > LocationIndex.MECH_LOC_LT ) {
                    innermost = LocationIndex.MECH_LOC_LT;
                }
            }
            if( RTCrits[i] == p ) {
                if( innermost > LocationIndex.MECH_LOC_RT ) {
                    innermost = LocationIndex.MECH_LOC_RT;
                }
            }
            if( RLCrits[i] == p ) {
                if( innermost > LocationIndex.MECH_LOC_RT ) {
                    innermost = LocationIndex.MECH_LOC_RL;
                }
            }
            if( LLCrits[i] == p ) {
                if( innermost > LocationIndex.MECH_LOC_RT ) {
                    innermost = LocationIndex.MECH_LOC_LL;
                }
            }
            if( RACrits[i] == p ) {
                if( innermost >= LocationIndex.MECH_LOC_LA && innermost <= LocationIndex.MECH_LOC_RA || innermost == 11 ) {
                    innermost = LocationIndex.MECH_LOC_RA;
                }
            }
            if( LACrits[i] == p ) {
                if( innermost >= LocationIndex.MECH_LOC_LA && innermost <= LocationIndex.MECH_LOC_RA || innermost == 11 ) {
                    innermost = LocationIndex.MECH_LOC_LA;
                }
            }
        }

        // now for the last six slots in locations that have them
        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == p ) {
                if( innermost > LocationIndex.MECH_LOC_CT ) {
                    innermost = LocationIndex.MECH_LOC_CT;
                }
            }
            if( LTCrits[i] == p ) {
                if( innermost > LocationIndex.MECH_LOC_LT ) {
                    innermost = LocationIndex.MECH_LOC_LT;
                }
            }
            if( RTCrits[i] == p ) {
                if( innermost > LocationIndex.MECH_LOC_RT ) {
                    innermost = LocationIndex.MECH_LOC_RT;
                }
            }
            if( RACrits[i] == p ) {
                if( innermost >= LocationIndex.MECH_LOC_LA && innermost <= LocationIndex.MECH_LOC_RA || innermost == 11 ) {
                    innermost = LocationIndex.MECH_LOC_RA;
                }
            }
            if( LACrits[i] == p ) {
                if( innermost >= LocationIndex.MECH_LOC_LA && innermost <= LocationIndex.MECH_LOC_RA || innermost == 11 ) {
                    innermost = LocationIndex.MECH_LOC_LA;
                }
            }
        }

        return innermost;
    }

    public LocationIndex FindIndex( abPlaceable p ) {
        // finds the first location index of the given item.  Should not be used
        // for split items as there is a routine for that.
        LocationIndex l = new LocationIndex();
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_HD;
                return l;
            }
            if( CTCrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_CT;
                return l;
            }
            if( LTCrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_LT;
                return l;
            }
            if( RTCrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_RT;
                return l;
            }
            if( RLCrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_RL;
                return l;
            }
            if( LLCrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_LL;
                return l;
            }
            if( RACrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_RA;
                return l;
            }
            if( LACrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_LA;
                return l;
            }
        }

        // now for the last six slots in locations that have them
        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_CT;
                return l;
            }
            if( LTCrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_LT;
                return l;
            }
            if( RTCrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_RT;
                return l;
            }
            if( RACrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_RA;
                return l;
            }
            if( LACrits[i] == p ) {
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_LA;
                return l;
            }
        }

        return l;
    }

    public ArrayList FindSplitIndex( abPlaceable p ) {
        // finds the split location indexes of a contiguous item that can split.
        // the ArrayList should never be larger than two in size.
        ArrayList v = new ArrayList();
        LocationIndex l;
        int NumHere = 0;
        boolean[] searched = { false, false, false, false, false, false, false, false };
        for( int i = 0; i < 6; i++ ) {
            if( ! searched[LocationIndex.MECH_LOC_HD] ) {
                if( HDCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_HD;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < HDCrits.length; j++ ) {
                        if( HDCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_HD] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_CT] ) {
                if( CTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_CT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < CTCrits.length; j++ ) {
                        if( CTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_CT] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_LT] ) {
                if( LTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_LT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < LTCrits.length; j++ ) {
                        if( LTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_LT] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_RT] ) {
                if( RTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_RT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < RTCrits.length; j++ ) {
                        if( RTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_RT] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_LA] ) {
                if( LACrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_LA;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < LACrits.length; j++ ) {
                        if( LACrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_LA] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_RA] ) {
                if( RACrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_RA;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < RACrits.length; j++ ) {
                        if( RACrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_RA] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_LL] ) {
                if( LLCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_LL;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < LLCrits.length; j++ ) {
                        if( LLCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_LL] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_RL] ) {
                if( RLCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_RL;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < RLCrits.length; j++ ) {
                        if( RLCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_RL] = true;
                }
            }
        }

        // now for the last six slots in locations that have them
        for( int i = 6; i < 12; i++ ) {
            if( ! searched[LocationIndex.MECH_LOC_CT] ) {
                if( CTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_CT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < CTCrits.length; j++ ) {
                        if( CTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_CT] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_LT] ) {
                if( LTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_LT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < LTCrits.length; j++ ) {
                        if( LTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_LT] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_RT] ) {
                if( RTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_RT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < RTCrits.length; j++ ) {
                        if( RTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_RT] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_LA] ) {
                if( LACrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_LA;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < LACrits.length; j++ ) {
                        if( LACrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_LA] = true;
                }
            }
            if( ! searched[LocationIndex.MECH_LOC_RA] ) {
                if( RACrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = LocationIndex.MECH_LOC_RA;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < RACrits.length; j++ ) {
                        if( RACrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[LocationIndex.MECH_LOC_RA] = true;
                }
            }
        }

        return v;
    }

    public int[] FindInstances( abPlaceable p ) {
        // this routine is used mainly by the text and html writers to find the
        // locations of certain items.  It can be used for endo-steel,
        // ferro-fibrous, split weapons, etc...  returns an int[] with the
        // number in each location
        int[] retval = { 0, 0, 0, 0, 0, 0, 0, 0 };
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_HD]++;
            }
            if( CTCrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_CT]++;
            }
            if( LTCrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_LT]++;
            }
            if( RTCrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_RT]++;
            }
            if( LACrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_LA]++;
            }
            if( RACrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_RA]++;
            }
            if( LLCrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_LL]++;
            }
            if( RLCrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_RL]++;
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_CT]++;
            }
            if( LTCrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_LT]++;
            }
            if( RTCrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_RT]++;
            }
            if( LACrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_LA]++;
            }
            if( RACrits[i] == p ) {
                retval[LocationIndex.MECH_LOC_RA]++;
            }
        }
        return retval;
    }

    public ArrayList FindIndexes( abPlaceable p ) {
        // returns a ArrayList full of location indexes for the XML writer
        // this should only be used for non-contiguous items
        ArrayList v = new ArrayList();
        LocationIndex l;
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_HD;
                v.add( l );
            }
            if( CTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_CT;
                v.add( l );
            }
            if( LTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_LT;
                v.add( l );
            }
            if( RTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_RT;
                v.add( l );
            }
            if( LACrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_LA;
                v.add( l );
            }
            if( RACrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_RA;
                v.add( l );
            }
            if( LLCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_LL;
                v.add( l );
            }
            if( RLCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_RL;
                v.add( l );
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_CT;
                v.add( l );
            }
            if( LTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_LT;
                v.add( l );
            }
            if( RTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_RT;
                v.add( l );
            }
            if( LACrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_LA;
                v.add( l );
            }
            if( RACrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = LocationIndex.MECH_LOC_RA;
                v.add( l );
            }
        }
        return v;
    }

    public int[] FindHeatSinks() {
        // this routine is used mainly by the text and html writers to find the
        // locations of heat sinks specifically.  returns an int[] with the
        // number in each location
        int[] retval = { 0, 0, 0, 0, 0, 0, 0, 0 };
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_HD]++;
            }
            if( CTCrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_CT]++;
            }
            if( LTCrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_LT]++;
            }
            if( RTCrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_RT]++;
            }
            if( LACrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_LA]++;
            }
            if( RACrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_RA]++;
            }
            if( LLCrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_LL]++;
            }
            if( RLCrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_RL]++;
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_CT]++;
            }
            if( LTCrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_LT]++;
            }
            if( RTCrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_RT]++;
            }
            if( LACrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_LA]++;
            }
            if( RACrits[i] instanceof HeatSink ) {
                retval[LocationIndex.MECH_LOC_RA]++;
            }
        }

        if( HeatSinks.IsDouble() ) {
            if( HeatSinks.GetTechBase() == AvailableCode.TECH_CLAN ) {
                for( int i = 0; i < 8; i++ ) {
                    retval[i] /= 2;
                }
            } else {
                for( int i = 0; i < 8; i++ ) {
                    retval[i] /= 3;
                }
            }
        } else if( HeatSinks.IsLaser() ) {
            // same as Clan DHS
            for( int i = 0; i < 8; i++ ) {
                retval[i] /= 2;
            }
        } else if( HeatSinks.IsCompact() ) {
            // crazy, have to find if we have a free compact, otherwise it's easy
            HeatSink test = HeatSinks.FindOpenCompact();
            int cloc = 11;
            if( test != null ) {
                cloc = Find( test );
            }
            for( int i = 0; i < 8; i++ ) {
                if( cloc == i ) {
                    retval[i] *= 2;
                    retval[i] -= 1;
                } else {
                    retval[i] *= 2;
                }
            }
        }

        return retval;
    }

    public int[] FindJumpJets( boolean IJJ ) {
        // this routine is used mainly by the text and html writers to find the
        // locations of jump jets specifically.  returns an int[] with the
        // number in each location
        int[] retval = { 0, 0, 0, 0, 0, 0, 0, 0 };
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_HD]++;
            }
            if( CTCrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_CT]++;
            }
            if( LTCrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_LT]++;
            }
            if( RTCrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_RT]++;
            }
            if( LACrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_LA]++;
            }
            if( RACrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_RA]++;
            }
            if( LLCrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_LL]++;
            }
            if( RLCrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_RL]++;
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_CT]++;
            }
            if( LTCrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_LT]++;
            }
            if( RTCrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_RT]++;
            }
            if( LACrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_LA]++;
            }
            if( RACrits[i] instanceof JumpJet ) {
                retval[LocationIndex.MECH_LOC_RA]++;
            }
        }

        if( IJJ ) {
            retval[LocationIndex.MECH_LOC_HD] /= 2;
            retval[LocationIndex.MECH_LOC_CT] /= 2;
            retval[LocationIndex.MECH_LOC_LT] /= 2;
            retval[LocationIndex.MECH_LOC_RT] /= 2;
            retval[LocationIndex.MECH_LOC_LA] /= 2;
            retval[LocationIndex.MECH_LOC_RA] /= 2;
            retval[LocationIndex.MECH_LOC_LL] /= 2;
            retval[LocationIndex.MECH_LOC_RL] /= 2;
        }

        return retval;
    }

    public int[] FindModularArmor() {
        // this routine is used to find where modular armor is located for
        // reporting and BV purposes
        int[] retval = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] instanceof ModularArmor ) {
                retval[LocationIndex.MECH_LOC_HD]++;
            }
            if( CTCrits[i] instanceof ModularArmor ) {
                if( CTCrits[i].IsMountedRear() ) {
                    retval[LocationIndex.MECH_LOC_CTR]++;
                } else {
                    retval[LocationIndex.MECH_LOC_CT]++;
                }
            }
            if( LTCrits[i] instanceof ModularArmor ) {
                if( LTCrits[i].IsMountedRear() ) {
                    retval[LocationIndex.MECH_LOC_LTR]++;
                } else {
                    retval[LocationIndex.MECH_LOC_LT]++;
                }
            }
            if( RTCrits[i] instanceof ModularArmor ) {
                if( RTCrits[i].IsMountedRear() ) {
                    retval[LocationIndex.MECH_LOC_RTR]++;
                } else {
                    retval[LocationIndex.MECH_LOC_RT]++;
                }
            }
            if( LACrits[i] instanceof ModularArmor ) {
                retval[LocationIndex.MECH_LOC_LA]++;
            }
            if( RACrits[i] instanceof ModularArmor ) {
                retval[LocationIndex.MECH_LOC_RA]++;
            }
            if( LLCrits[i] instanceof ModularArmor ) {
                retval[LocationIndex.MECH_LOC_LL]++;
            }
            if( RLCrits[i] instanceof ModularArmor ) {
                retval[LocationIndex.MECH_LOC_RL]++;
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] instanceof ModularArmor ) {
                if( CTCrits[i].IsMountedRear() ) {
                    retval[LocationIndex.MECH_LOC_CTR]++;
                } else {
                    retval[LocationIndex.MECH_LOC_CT]++;
                }
            }
            if( LTCrits[i] instanceof ModularArmor ) {
                if( LTCrits[i].IsMountedRear() ) {
                    retval[LocationIndex.MECH_LOC_LTR]++;
                } else {
                    retval[LocationIndex.MECH_LOC_LT]++;
                }
            }
            if( RTCrits[i] instanceof ModularArmor ) {
                if( RTCrits[i].IsMountedRear() ) {
                    retval[LocationIndex.MECH_LOC_RTR]++;
                } else {
                    retval[LocationIndex.MECH_LOC_RT]++;
                }
            }
            if( LACrits[i] instanceof ModularArmor ) {
                retval[LocationIndex.MECH_LOC_LA]++;
            }
            if( RACrits[i] instanceof ModularArmor ) {
                retval[LocationIndex.MECH_LOC_RA]++;
            }
        }

        return retval;
    }

    public int[] FindExplosiveInstances() {
        // this routine is used to find where modular armor is located for
        // reporting and BV purposes
        int[] retval = { 0, 0, 0, 0, 0, 0, 0, 0 };
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) HDCrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_HD]++;
                }
            } else if( HDCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) HDCrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_HD]++;
                }
            }
            if( CTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) CTCrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_CT]++;
                }
            } else if( CTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) CTCrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_CT]++;
                }
            }
            if( LTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) LTCrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_LT]++;
                }
            } else if( LTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) LTCrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_LT]++;
                }
            }
            if( RTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) RTCrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_RT]++;
                }
            } else if( RTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) RTCrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_RT]++;
                }
            }
            if( LACrits[i] instanceof Ammunition ) {
                if( ((Ammunition) LACrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_LA]++;
                }
            } else if( LACrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) LACrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_LA]++;
                }
            }
            if( RACrits[i] instanceof Ammunition ) {
                if( ((Ammunition) RACrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_RA]++;
                }
            } else if( RACrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) RACrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_RA]++;
                }
            }
            if( LLCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) LLCrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_LL]++;
                }
            } else if( LLCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) LLCrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_LL]++;
                }
            }
            if( RLCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) RLCrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_RL]++;
                }
            } else if( RLCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) RLCrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_RL]++;
                }
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) CTCrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_CT]++;
                }
            } else if( CTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) CTCrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_CT]++;
                }
            }
            if( LTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) LTCrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_LT]++;
                }
            } else if( LTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) LTCrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_LT]++;
                }
            }
            if( RTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) RTCrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_RT]++;
                }
            } else if( RTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) RTCrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_RT]++;
                }
            }
            if( LACrits[i] instanceof Ammunition ) {
                if( ((Ammunition) LACrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_LA]++;
                }
            } else if( LACrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) LACrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_LA]++;
                }
            }
            if( RACrits[i] instanceof Ammunition ) {
                if( ((Ammunition) RACrits[i]).IsExplosive() ) {
                    retval[LocationIndex.MECH_LOC_RA]++;
                }
            } else if( RACrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) RACrits[i]).IsExplosive() ){
                    retval[LocationIndex.MECH_LOC_RA]++;
                }
            }
        }

        return retval;
    }

//    public void FlushIllegal( int Era, int Year, boolean Restrict ) {
    public void FlushIllegal() {
        // since most everything else is taken care of during mech recalculates,
        // this method is provided for non-core equipment
        AvailableCode AC;
        abPlaceable p;
        int Rules = Owner.GetRulesLevel();

        Owner.CheckArmoredComponents();

        // see if there's anything to flush out
        if( NonCore.size() <= 0 ) { return; }

        for( int i = NonCore.size() - 1; i >= 0; i-- ) {
            p = (abPlaceable) NonCore.get( i );
            AC = p.GetAvailability();
            try { 
                CheckExclusions( p );
                if( ! CommonTools.IsAllowed( AC, Owner ) ) {
                    Remove( p );
                }
            } catch( Exception e ) {
                Remove( p );
            }
            if( NonCore.contains( p ) ) {
                if( Rules < AvailableCode.RULES_EXPERIMENTAL ) {
                    p.ArmorComponent( false );
                }
            }
        }
    }

    public boolean UnallocateAll( abPlaceable p, boolean override ) {
        // removes the specified Placeable from it's crit locations and adds it
        // back to the queue.

        // if the item is NoItem, reject
        if( p == NoItem || p == null ) {
            return false;
        }

        // if the item is location locked, we can't unallocate it.
        if( p.LocationLocked() &! override ) {
            return false;
        }

        // Now find the item, remove it from any location it's in, then put
        // it into the queue.  Check the first six slots in each location.
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] == p ) {
                // found it, now remove it
                HDCrits[i] = NoItem;
            }
            if( CTCrits[i] == p ) {
                // found it, now remove it
                CTCrits[i] = NoItem;
            }
            if( RTCrits[i] == p ) {
                // found it, now remove it
                RTCrits[i] = NoItem;
            }
            if( LTCrits[i] == p ) {
                // found it, now remove it
                LTCrits[i] = NoItem;
            }
            if( RACrits[i] == p ) {
                // found it, now remove it
                RACrits[i] = NoItem;
            }
            if( LACrits[i] == p ) {
                // found it, now remove it
                LACrits[i] = NoItem;
            }
            if( RLCrits[i] == p ) {
                // found it, now remove it
                RLCrits[i] = NoItem;
            }
            if( LLCrits[i] == p ) {
                // found it, now remove it
                LLCrits[i] = NoItem;
            }
        }

        // now for the last six slots in locations that have them
        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == p ) {
                // found it, now remove it
                CTCrits[i] = NoItem;
            }
            if( RTCrits[i] == p ) {
                // found it, now remove it
                RTCrits[i] = NoItem;
            }
            if( LTCrits[i] == p ) {
                // found it, now remove it
                LTCrits[i] = NoItem;
            }
            if( RACrits[i] == p ) {
                // found it, now remove it
                RACrits[i] = NoItem;
            }
            if( LACrits[i] == p ) {
                // found it, now remove it
                LACrits[i] = NoItem;
            }
        }

        // if the item is non-contiguous, we have to reset it's placed counter.
        if( ! p.Contiguous() ) {
            p.ResetPlaced();
        }

        // add the item back into the queue unless is already exists there
        // unless it's an Artemis IV FCS system.
        if( ! QueueContains(p) &! ( p instanceof ifMissileGuidance ) &! ( p instanceof PPCCapacitor ) &! ( p instanceof LaserInsulator ) ) {
            if( p instanceof RangedWeapon ) {
                if( ! ((RangedWeapon) p).IsInArray() ) {
                    AddToQueue( p );
                }
            } else {
                AddToQueue( p );
            }
        }

        // is the item was mounted rear, set it to normal
        p.MountRear( false );

        // handler for FCS and Capacitors
        if( p instanceof RangedWeapon ) {
            if( ((RangedWeapon) p).IsUsingFCS() ) {
                UnallocateAll( (abPlaceable) ((RangedWeapon) p).GetFCS(), true );
            }
            if( ((RangedWeapon) p).IsUsingCapacitor() ) {
                UnallocateAll( ((RangedWeapon) p).GetCapacitor(), true );
            }
            if( ((RangedWeapon) p).IsUsingInsulator() ) {
                UnallocateAll( ((RangedWeapon) p).GetInsulator(), true );
            }
            if( ((RangedWeapon) p).IsTurreted() ) {
                if( ((RangedWeapon) p).GetTurret() == HDTurret ) {
                    ((RangedWeapon) p).RemoveFromTurret( HDTurret );
                }
                if( ((RangedWeapon) p).GetTurret() == LTTurret ) {
                    ((RangedWeapon) p).RemoveFromTurret( LTTurret );
                }
                if( ((RangedWeapon) p).GetTurret() == RTTurret ) {
                    ((RangedWeapon) p).RemoveFromTurret( RTTurret );
                }
            }
        }

        // if the item is an MG Array, check for it's MGs and unallocate
        if( p instanceof MGArray ) {
            for( int i = 0; i < ((MGArray) p).GetMGs().length; i++ ) {
                UnallocateAll( ((MGArray) p).GetMGs()[i], true );
            }
        }

        Owner.SetChanged( true );

        // everything worked out fine
        return true;
    }

    public void Remove( abPlaceable p ) {
        // removes the item completely from the loadout.
        // first, unallocate it.
        UnallocateAll( p, true );

        // Now remove it from the queue
        RemoveFromQueue( p );

        // check to see if this is a core component
        if( ! p.CoreComponent() ) {
            // remove it to the non core list
            if( NonCore.contains( p ) ) {
                NonCore.remove( p );
            }

            if( Equipment.contains( p ) ) {
                Equipment.remove( p );
            }

            if( TCList.contains( p ) ) {
                TCList.remove( p );
            }
            if( p.GetMechModifier() != null ) {
                RemoveMechMod( p.GetMechModifier() );
                Owner.RemoveMechMod( p.GetMechModifier() );
            }
        } else {
            if( p.GetMechModifier() != null ) {
                RemoveMechMod( p.GetMechModifier() );
                Owner.RemoveMechMod( p.GetMechModifier() );
            }
        }

        Owner.SetChanged( true );
    }

    public void UnallocateByIndex( int SIndex, abPlaceable[] a ) {
        // removes the specified item index from this location.  If the item is
        // contiguous, it removes the entire item, otherwise only the specified
        // index.  This routine does not care about location locked items as
        // that should have already been checked.
        abPlaceable p = a[SIndex];

        // if the item is NoItem, reject
        if( p == NoItem || p == null ) {
            return;
        }

        if( p.Contiguous() ) {
            // remove all crits from the head and then add the item back into
            // the queue.
            for( int i = 0; i < a.length; i++ ) {
                if( a[i] == p ) {
                    a[i] = NoItem;
                }
            }

            // now add the item back into the Queue
            AddToQueue( p );
        } else {
            // remove it from the head
            a[SIndex] = NoItem;

            // the item is not contiguous, so we have to decrement it's placed
            // number.
            p.DecrementPlaced();

            // now see if it's still in the Queue.  If not, add it back in.
            if( ! QueueContains( p ) ) {
                AddToQueue( p );
            }
        }

        // is the item was mounted rear, set it to normal
        if( p.IsMountedRear() ) {
            p.MountRear( false );
        }

        // handler for FCS and Capacitors
        if( p instanceof RangedWeapon ) {
            if( ((RangedWeapon) p).IsUsingFCS() ) {
                UnallocateAll( (abPlaceable) ((RangedWeapon) p).GetFCS(), true );
            }
            if( ((RangedWeapon) p).IsUsingCapacitor() ) {
                UnallocateAll( ((RangedWeapon) p).GetCapacitor(), true );
            }
            if( ((RangedWeapon) p).IsUsingInsulator() ) {
                UnallocateAll( ((RangedWeapon) p).GetInsulator(), true );
            }
            if( ((RangedWeapon) p).IsTurreted() ) {
                if( ((RangedWeapon) p).GetTurret() == HDTurret ) {
                    ((RangedWeapon) p).RemoveFromTurret( HDTurret );
                }
                if( ((RangedWeapon) p).GetTurret() == LTTurret ) {
                    ((RangedWeapon) p).RemoveFromTurret( LTTurret );
                }
                if( ((RangedWeapon) p).GetTurret() == RTTurret ) {
                    ((RangedWeapon) p).RemoveFromTurret( RTTurret );
                }
            }
        }

        // if the item is an MG Array, check for it's MGs and unallocate
        if( p instanceof MGArray ) {
            for( int i = 0; i < ((MGArray) p).GetMGs().length; i++ ) {
                UnallocateAll( ((MGArray) p).GetMGs()[i], true );
            }
        }

        Owner.SetChanged( true );
    }

    public void AutoAllocate( abPlaceable p ) {
        // automagically allocates the specified item in a round-robin manner
        if( p.Contiguous() ) {
            // we don't auto-allocate contiguous items
            return;
        }

        // start the round-robin.  LA, LT, CT, RT, RA, LL, RL, HD in order
        while( p.NumPlaced() < p.NumCrits() ) {
            try {
                if ( p.CanAllocArms() && HasSpace(LocationIndex.MECH_LOC_LA) )
                    AddToLA( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                if ( p.CanAllocTorso() && HasSpace(LocationIndex.MECH_LOC_LT) )
                    AddToLT( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                if ( p.CanAllocCT() && HasSpace(LocationIndex.MECH_LOC_CT) )
                    AddToCT( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                if ( p.CanAllocTorso() && HasSpace(LocationIndex.MECH_LOC_RT) )
                    AddToRT( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                if ( p.CanAllocArms() && HasSpace(LocationIndex.MECH_LOC_RA) )
                    AddToRA( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                if ( p.CanAllocLegs() && HasSpace(LocationIndex.MECH_LOC_RL) )
                    AddToRL( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                if ( p.CanAllocLegs() && HasSpace(LocationIndex.MECH_LOC_LL) )
                    AddToLL( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                if ( p.CanAllocHD() && HasSpace(LocationIndex.MECH_LOC_HD) )
                    AddToHD( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
            } catch ( Exception e ) {
                // just move on.  we probably couldn't allocate it to that location.
            }
        }
    }

    public void AutoAllocate( EquipmentCollection e ) {
        int Loc = LocationIndex.MECH_LOC_CT, NumItems = e.GetSize(), DefCount;
        abPlaceable p;
        boolean Placed;
        for( int i = 0; i < NumItems; i++ ) {
            p = e.GetType();
            Placed = false;
            DefCount = 0;
            // we'll go round-robin like before, but in a different direction.
            while( ! Placed ) {
                try {
                    switch( Loc ) {
                        case LocationIndex.MECH_LOC_CT:
                            AddToCT( p, FirstFree( CTCrits ) );
                            Loc++;
                            Placed = true;
                            break;
                        case LocationIndex.MECH_LOC_LT:
                            AddToLT( p, FirstFree( LTCrits ) );
                            Loc++;
                            Placed = true;
                            break;
                        case LocationIndex.MECH_LOC_RT:
                            AddToRT( p, FirstFree( RTCrits ) );
                            Loc++;
                            Placed = true;
                            break;
                        case LocationIndex.MECH_LOC_LA:
                            AddToLA( p, FirstFree( LACrits ) );
                            Loc++;
                            Placed = true;
                            break;
                        case LocationIndex.MECH_LOC_RA:
                            AddToRA( p, FirstFree( RACrits ) );
                            Loc++;
                            Placed = true;
                            break;
                        case LocationIndex.MECH_LOC_LL:
                            AddToLL( p, FirstFree( LLCrits ) );
                            Loc++;
                            Placed = true;
                            break;
                        case LocationIndex.MECH_LOC_RL:
                            AddToRL( p, FirstFree( RLCrits ) );
                            Loc++;
                            Placed = true;
                            break;
                        default:
                            Loc = LocationIndex.MECH_LOC_CT;
                            DefCount++;
                    }
                } catch( Exception x ) {
                    Loc++;
                }
                if( DefCount > 1 ) {
                    return;
                }
            }
        }
    }

    public void SplitAllocate( abPlaceable p, int FirstLoc, int FirstIndex, int SecondLoc ) throws Exception {
        // supports splitting locations for large items.
        int NumSecond = 0, SecondIndex = 0;

        switch( FirstLoc ) {
        case LocationIndex.MECH_LOC_CT:
            // add the item into the first location at the specified index and
            // using the correct number of crits.
            try {
                NumSecond = AddTo( CTCrits, p, FirstIndex );
            } catch( Exception e ) {
                throw e;
            }

            // if it fit, add the item into the second location at the first
            // available index.
            try {
                switch( SecondLoc ) {
                case LocationIndex.MECH_LOC_LT:
                    SecondIndex = FirstFree( LTCrits );
                    AddTo( LTCrits, p, SecondIndex, NumSecond );
                    break;
                case LocationIndex.MECH_LOC_RT:
                    SecondIndex = FirstFree( RTCrits );
                    AddTo( RTCrits, p, SecondIndex, NumSecond );
                    break;
                }
            } catch( ArrayIndexOutOfBoundsException a ) {
                UnallocateAll( p, true );
                throw new Exception( p.CritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case LocationIndex.MECH_LOC_LT:
            // add the item into the first location at the specified index and
            // using the correct number of crits.
            try {
                NumSecond = AddTo( LTCrits, p, FirstIndex );
            } catch( Exception e ) {
                throw e;
            }

            // if it fit, add the item into the second location at the first
            // available index.
            try {
                switch( SecondLoc ) {
                case LocationIndex.MECH_LOC_CT:
                    SecondIndex = FirstFree( CTCrits );
                    AddTo( CTCrits, p, SecondIndex, NumSecond );
                    break;
                case LocationIndex.MECH_LOC_LA:
                    SecondIndex = FirstFree( LACrits );
                    AddTo( LACrits, p, SecondIndex, NumSecond );
                    break;
                case LocationIndex.MECH_LOC_LL:
                    SecondIndex = FirstFree( LLCrits );
                    AddTo( LLCrits, p, SecondIndex, NumSecond );
                    break;
                }
            } catch( ArrayIndexOutOfBoundsException a ) {
                UnallocateAll( p, true );
                throw new Exception( p.CritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case LocationIndex.MECH_LOC_RT:
            // add the item into the first location at the specified index and
            // using the correct number of crits.
            try {
                NumSecond = AddTo( RTCrits, p, FirstIndex );
            } catch( Exception e ) {
                throw e;
            }

            // if it fit, add the item into the second location at the first
            // available index.
            try {
                switch( SecondLoc ) {
                case LocationIndex.MECH_LOC_CT:
                    SecondIndex = FirstFree( CTCrits );
                    AddTo( CTCrits, p, SecondIndex, NumSecond );
                    break;
                case LocationIndex.MECH_LOC_RA:
                    SecondIndex = FirstFree( RACrits );
                    AddTo( RACrits, p, SecondIndex, NumSecond );
                    break;
                case LocationIndex.MECH_LOC_RL:
                    SecondIndex = FirstFree( RLCrits );
                    AddTo( RLCrits, p, SecondIndex, NumSecond );
                    break;
                }
            } catch( ArrayIndexOutOfBoundsException a ) {
                UnallocateAll( p, true );
                throw new Exception( p.CritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case LocationIndex.MECH_LOC_LA:
            // there's only ever one location that can be split to.

            // add the item into the first location at the specified index and
            // using the correct number of crits.
            try {
                NumSecond = AddTo( LACrits, p, FirstIndex );
            } catch( Exception e ) {
                throw e;
            }

            // if it fit, add the item into the second location at the first
            // available index.
            SecondIndex = FirstFree( LTCrits );
            try {
                AddTo( LTCrits, p, SecondIndex, NumSecond );
            } catch( ArrayIndexOutOfBoundsException a ) {
                UnallocateAll( p, true );
                throw new Exception( p.CritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case LocationIndex.MECH_LOC_RA:
            // there's only ever one location that can be split to.

            // add the item into the first location at the specified index and
            // using the correct number of crits.
            try {
                NumSecond = AddTo( RACrits, p, FirstIndex );
            } catch( Exception e ) {
                throw e;
            }

            // if it fit, add the item into the second location at the first
            // available index.
            SecondIndex = FirstFree( RTCrits );
            try {
                AddTo( RTCrits, p, SecondIndex, NumSecond );
            } catch( ArrayIndexOutOfBoundsException a ) {
                UnallocateAll( p, true );
                throw new Exception( p.CritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case LocationIndex.MECH_LOC_LL:
            // there's only ever one location that can be split to.

            // add the item into the first location at the specified index and
            // using the correct number of crits.
            try {
                NumSecond = AddTo( LLCrits, p, FirstIndex );
            } catch( Exception e ) {
                throw e;
            }

            // if it fit, add the item into the second location at the first
            // available index.
            SecondIndex = FirstFree( LTCrits );
            try {
                AddTo( LTCrits, p, SecondIndex, NumSecond );
            } catch( ArrayIndexOutOfBoundsException a ) {
                UnallocateAll( p, true );
                throw new Exception( p.CritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case LocationIndex.MECH_LOC_RL:
            // there's only ever one location that can be split to.

            // add the item into the first location at the specified index and
            // using the correct number of crits.
            try {
                NumSecond = AddTo( RLCrits, p, FirstIndex );
            } catch( Exception e ) {
                throw e;
            }

            // if it fit, add the item into the second location at the first
            // available index.
            SecondIndex = FirstFree( RTCrits );
            try {
                AddTo( RTCrits, p, SecondIndex, NumSecond );
            } catch( ArrayIndexOutOfBoundsException a ) {
                UnallocateAll( p, true );
                throw new Exception( p.CritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        }

        // all went well, remove the item from the queue
        RemoveFromQueue( p );
    }

    private int AddTo( abPlaceable[] Loc, abPlaceable p, int index ) throws Exception {
        // support for splittable crits.  Adds the item into the specified loc
        // at the requested index and returns the number of crits left to place.
        int temp = FreeFrom( Loc, index );
/* testing
        // check for space to fill the item into.
        for( int i = index; i < Loc.length; i++ ) {
            if( Loc[i].LocationLocked() || Loc[i].LocationLinked() ) {
                break;
            } else {
                temp ++;
            }
        }

        // now we have the size of a contiguous block from the start, allocate
        // the item, removing those items that we can along the way.
        for( int i = index; i < temp + index; i++ ) {
            // is there a non-location locked item there?
            if( Loc[i] != NoItem ) {
                // we've already ensured that it is not location locked
                // above, so put the item back into the queue.
                UnallocateByIndex( i, Loc );
            }
            // finally, allocate the item slot
            Loc[i] = p;
        }
*/
        for( int i = index; i < temp + index; i++ ) {
            // allocate the item slot
            Loc[i] = p;
        }

        Owner.SetChanged( true );

        // return the number of crits left to place
        return p.NumCrits() - temp;
    }

    public void AddTo( abPlaceable[] Loc, abPlaceable p, int index, int numcrits ) throws Exception {
        // this method attempts to add to a location a certain amount of crits for
        // the given item.
        int temp = 0;

        // see how large the contiguous space from the requested index is.  Count
        // items that can be removed as empty for this purpose.
        for( int i = index; i < ( numcrits + index ); i++ ) {
            if( Loc[i].LocationLocked() || Loc[i].LocationLinked() ) {
                // if the space is unmovable, stop the loop and continue
                break;
            } else {
                // if the space is empty, count it.
                temp++;
            }
        }

        // do we have enough contiguous space?
        if( temp < numcrits ) {
            // nope.
            throw new Exception( p.CritName() + " cannot be allocated because there is not enough space." );
        }

        // allocate the item in question
        for( int i = index; i < temp + index; i++ ) {
            // is there a non-location locked item there?
            if( Loc[i] != NoItem ) {
                // we've already ensured that it is not location locked
                // above, so put the item back into the queue.
                UnallocateByIndex( i, Loc );
            }
            // finally, allocate the item slot
            Loc[i] = p;
        }

        Owner.SetChanged( true );
    }

    public int FirstFree( abPlaceable[] Loc ) {
        // finds the first free index in a location.  returns a location that is
        // out-of-bounds if none found
        int result = 0;
        for( int i = 0; i < Loc.length; i++ ) {
            if( Loc[i] == NoItem ) {
                result = i;
                return result;
            }
        }

        // we haven't found a free index, return the out-of-bound result
        return Loc.length;
    }

    public int FreeFrom( abPlaceable[] Loc, int index ) {
        // returns the number of free crits from the specified index.  Items that
        // can be removed are treated as free for this purpose.
        int result = 0;
// testing
        for( int i = index; i < Loc.length; i++ ) {
            if( Loc[i] == NoItem ) {
                result ++;
            } else {
                return result;
            }
        }

/*        for( int i = index; i < Loc.length; i++ ) {
            if( Loc[i].LocationLinked() || Loc[i].LocationLocked() ) {
                // found the last in this contiguous block
                return result;
            } else {
                result ++;
            }
        }
*/
        return result;
    }

    public void Compact() {
        // Compact the loadout so that it frees up loose space.
        abPlaceable p;

        // compact the head.
        for( int i = 0; i < HDCrits.length; i++ ) {
            // check each space in order to see if it's empty
            if( HDCrits[i] == NoItem ) {
                // it's empty, find the next item and move it up
                for( int j = i + 1; j < HDCrits.length; j++ ) {
                    if( HDCrits[j] != NoItem &! HDCrits[j].LocationLocked() ) {
                        // move it up to i
                        p = HDCrits[j];
                        UnallocateByIndex( j, HDCrits );
                        try {
                            AddToHD( p, i );
                        } catch ( Exception e ) {
                            // wow!  How in the hell did that happen?  Throw the
                            // item back into the queue.
                            AddToQueue( p );
                        }
                        break;
                    } else {
                        if( ! Owner.IsOmnimech() && ( HDCrits[j] instanceof CASEII || HDCrits[j] instanceof MultiSlotSystem ) ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            HDCrits[i] = HDCrits[j];
                            HDCrits[j] = NoItem;
                            break;
                        }
                    }
                }
            }
        }

        // compact the center torso.
        for( int i = 0; i < CTCrits.length; i++ ) {
            // check each space in order to see if it's empty
            if( CTCrits[i] == NoItem ) {
                // it's empty, find the next item and move it up
                for( int j = i + 1; j < CTCrits.length; j++ ) {
                    if( CTCrits[j] != NoItem &! CTCrits[j].LocationLocked() ) {
                        // is the item splittable?  If so, we have some work
                        p = CTCrits[j];
                        if( p.CanSplit() ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            CTCrits[j] = NoItem;
                            CTCrits[i] = p;
                        } else {
                            // move it up to i
                            UnallocateByIndex( j, CTCrits );
                            try {
                                AddToCT( p, i );
                            } catch ( Exception e ) {
                                // wow!  How in the hell did that happen?  Throw the
                                // item back into the queue.
                                AddToQueue( p );
                            }
                        }
                        break;
                    } else {
                        if( ! Owner.IsOmnimech() && ( CTCrits[j] instanceof CASE || CTCrits[j] instanceof CASEII || CTCrits[j] instanceof MultiSlotSystem || CTCrits[j] instanceof Supercharger || CTCrits[j] instanceof SimplePlaceable || CTCrits[j] instanceof Cockpit ) ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            CTCrits[i] = CTCrits[j];
                            CTCrits[j] = NoItem;
                            break;
                        }
                    }
                }
            }
        }

        // compact the left torso.
        for( int i = 0; i < LTCrits.length; i++ ) {
            // check each space in order to see if it's empty
            if( LTCrits[i] == NoItem ) {
                // it's empty, find the next item and move it up
                for( int j = i + 1; j < LTCrits.length; j++ ) {
                    if( LTCrits[j] != NoItem &! LTCrits[j].LocationLocked() ) {
                        // is the item splittable?  If so, we have some work
                        p = LTCrits[j];
                        if( p.CanSplit() ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            LTCrits[j] = NoItem;
                            LTCrits[i] = p;
                        } else {
                            // move it up to i
                            UnallocateByIndex( j, LTCrits );
                            try {
                                AddToLT( p, i );
                            } catch ( Exception e ) {
                                // wow!  How in the hell did that happen?  Throw the
                                // item back into the queue.
                                AddToQueue( p );
                            }
                        }
                        break;
                    } else {
                        if( ! Owner.IsOmnimech() && ( LTCrits[j] instanceof CASE || LTCrits[j] instanceof CASEII || LTCrits[j] instanceof MultiSlotSystem || LTCrits[j] instanceof Supercharger || LTCrits[j] instanceof Engine || LTCrits[j] instanceof SimplePlaceable ) ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            LTCrits[i] = LTCrits[j];
                            LTCrits[j] = NoItem;
                            break;
                        }
                    }
                }
            }
        }

        // compact the right torso.
        for( int i = 0; i < RTCrits.length; i++ ) {
            // check each space in order to see if it's empty
            if( RTCrits[i] == NoItem ) {
                // it's empty, find the next item and move it up
                for( int j = i + 1; j < RTCrits.length; j++ ) {
                    if( RTCrits[j] != NoItem &! RTCrits[j].LocationLocked() ) {
                        // is the item splittable?  If so, we have some work
                        p = RTCrits[j];
                        if( p.CanSplit() ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            RTCrits[j] = NoItem;
                            RTCrits[i] = p;
                        } else {
                            // move it up to i
                            UnallocateByIndex( j, RTCrits );
                            try {
                                AddToRT( p, i );
                            } catch ( Exception e ) {
                                // wow!  How in the hell did that happen?  Throw the
                                // item back into the queue.
                                AddToQueue( p );
                            }
                        }
                        break;
                    } else {
                        if( ! Owner.IsOmnimech() && ( RTCrits[j] instanceof CASE || RTCrits[j] instanceof CASEII || RTCrits[j] instanceof MultiSlotSystem || RTCrits[j] instanceof Supercharger || RTCrits[j] instanceof Engine || RTCrits[j] instanceof SimplePlaceable ) ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            RTCrits[i] = RTCrits[j];
                            RTCrits[j] = NoItem;
                            break;
                        }
                    }
                }
            }
        }

        // compact the left arm.
        for( int i = 0; i < LACrits.length; i++ ) {
            // check each space in order to see if it's empty
            if( LACrits[i] == NoItem ) {
                // it's empty, find the next item and move it up
                for( int j = i + 1; j < LACrits.length; j++ ) {
                    if( LACrits[j] != NoItem &! LACrits[j].LocationLocked() ) {
                        // is the item splittable?  If so, we have some work
                        p = LACrits[j];
                        if( p.CanSplit() ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            LACrits[j] = NoItem;
                            LACrits[i] = p;
                        } else {
                            // move it up to i
                            UnallocateByIndex( j, LACrits );
                            try {
                                AddToLA( p, i );
                            } catch ( Exception e ) {
                                // wow!  How in the hell did that happen?  Throw the
                                // item back into the queue.
                                AddToQueue( p );
                            }
                        }
                        break;
                    } else {
                        if( ! Owner.IsOmnimech() && ( LACrits[j] instanceof CASEII || LACrits[j] instanceof MultiSlotSystem ) ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            LACrits[i] = LACrits[j];
                            LACrits[j] = NoItem;
                            break;
                        }
                    }
                }
            }
        }

        // compact the right arm.
        for( int i = 0; i < RACrits.length; i++ ) {
            // check each space in order to see if it's empty
            if( RACrits[i] == NoItem ) {
                // it's empty, find the next item and move it up
                for( int j = i + 1; j < RACrits.length; j++ ) {
                    if( RACrits[j] != NoItem &! RACrits[j].LocationLocked() ) {
                        // is the item splittable?  If so, we have some work
                        p = RACrits[j];
                        if( p.CanSplit() ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            RACrits[j] = NoItem;
                            RACrits[i] = p;
                        } else {
                            // move it up to i
                            UnallocateByIndex( j, RACrits );
                            try {
                                AddToRA( p, i );
                            } catch ( Exception e ) {
                                // wow!  How in the hell did that happen?  Throw the
                                // item back into the queue.
                                AddToQueue( p );
                            }
                        }
                        break;
                    } else {
                        if( ! Owner.IsOmnimech() && ( RACrits[j] instanceof CASEII || RACrits[j] instanceof MultiSlotSystem ) ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            RACrits[i] = RACrits[j];
                            RACrits[j] = NoItem;
                            break;
                        }
                    }
                }
            }
        }

        // compact the left leg.
        for( int i = 0; i < LLCrits.length; i++ ) {
            // check each space in order to see if it's empty
            if( LLCrits[i] == NoItem ) {
                // it's empty, find the next item and move it up
                for( int j = i + 1; j < LLCrits.length; j++ ) {
                    if( LLCrits[j] != NoItem &! LLCrits[j].LocationLocked() ) {
                        // is the item splittable?  If so, we have some work
                        p = LLCrits[j];
                        if( p.CanSplit() ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            LLCrits[j] = NoItem;
                            LLCrits[i] = p;
                        } else {
                            // move it up to i
                            UnallocateByIndex( j, LLCrits );
                            try {
                                AddToLL( p, i );
                            } catch ( Exception e ) {
                                // wow!  How in the hell did that happen?  Throw the
                                // item back into the queue.
                                AddToQueue( p );
                            }
                        }
                        break;
                    } else {
                        if( ! Owner.IsOmnimech() && ( LLCrits[j] instanceof CASEII || LLCrits[j] instanceof MultiSlotSystem ) ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            LLCrits[i] = LLCrits[j];
                            LLCrits[j] = NoItem;
                            break;
                        }
                    }
                }
            }
        }

        // compact the right leg.
        for( int i = 0; i < RLCrits.length; i++ ) {
            // check each space in order to see if it's empty
            if( RLCrits[i] == NoItem ) {
                // it's empty, find the next item and move it up
                for( int j = i + 1; j < RLCrits.length; j++ ) {
                    if( RLCrits[j] != NoItem &! RLCrits[j].LocationLocked() ) {
                        // is the item splittable?  If so, we have some work
                        p = RLCrits[j];
                        if( p.CanSplit() ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            RLCrits[j] = NoItem;
                            RLCrits[i] = p;
                        } else {
                            // move it up to i
                            UnallocateByIndex( j, RLCrits );
                            try {
                                AddToRL( p, i );
                            } catch ( Exception e ) {
                                // wow!  How in the hell did that happen?  Throw the
                                // item back into the queue.
                                AddToQueue( p );
                            }
                        }
                        break;
                    } else {
                        if( ! Owner.IsOmnimech() && ( RLCrits[j] instanceof CASEII || RLCrits[j] instanceof MultiSlotSystem ) ) {
                            // we're not going to unallocate it.  Instead, just
                            // move the reference up.
                            RLCrits[i] = RLCrits[j];
                            RLCrits[j] = NoItem;
                            break;
                        }
                    }
                }
            }
        }

        Owner.SetChanged( true );
    }

    public boolean IsAllocated( abPlaceable p ) {
        // checks to see if the specified item is allocated in the loadout

        // Find the item.  Check the first six slots in each location.
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] == p ) {
                // found it.
                return true;
            }
            if( CTCrits[i] == p ) {
                // found it.
                return true;
            }
            if( RTCrits[i] == p ) {
                // found it.
                return true;
            }
            if( LTCrits[i] == p ) {
                // found it.
                return true;
            }
            if( RACrits[i] == p ) {
                // found it.
                return true;
            }
            if( LACrits[i] == p ) {
                // found it.
                return true;
            }
            if( RLCrits[i] == p ) {
                // found it.
                return true;
            }
            if( LLCrits[i] == p ) {
                // found it.
                return true;
            }
        }

        // now for the last six slots in locations that have them
        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == p ) {
                // found it.
                return true;
            }
            if( RTCrits[i] == p ) {
                // found it.
                return true;
            }
            if( LTCrits[i] == p ) {
                // found it.
                return true;
            }
            if( RACrits[i] == p ) {
                // found it.
                return true;
            }
            if( LACrits[i] == p ) {
                // found it.
                return true;
            }
        }

        // couldn't find it
        return false;
    }

    // Private Methods
    private void Allocate( abPlaceable p, int SIndex, abPlaceable[] Loc ) throws Exception {
        // Adds the specified placeable to the given location at the specified
        // stating index.  Throws Exceptions with error messages if things went
        // wrong.

        // Let's get a snapshot of the location so we can reset it if we have to.
        abPlaceable SnapShot[] = Loc.clone();

        // we have to accomodate for Artemis IV systems
        boolean AddIn = false;
        boolean ArrayGood = false;
        int AddInSize = 1;
        ArrayList removed = new ArrayList(), rears = new ArrayList();

        // check for generic placement
        if( SIndex == -1 ) {
            SIndex = FirstFree( Loc );
        }

        // do we have space at the requested index?
        try {
            // is the item contiguous?  Note, this will be used for some single
            // location items as well.
            if( p.Contiguous() ) {
                int i;
                int AddInLoc = 0;
                int NumMG = 0;
                int[] MGLocs = { 0, 0, 0, 0 };

                // check for space to fill the item into.
                for( i = SIndex; i < ( p.NumCrits() + SIndex ); i++ ) {
                    // if the item in the location is location locked, there is
                    // no space.
                    if( Loc[i].LocationLocked() ) {
                        throw new Exception( p.CritName() + " cannot be" +
                            " allocated to that location because a non-movable" +
                            " item already exists there." );
                    }
                }

                // check to see if we have space for add-ins
                if( p instanceof RangedWeapon ) {
                    if( ((RangedWeapon) p).IsUsingFCS() ) {
                        // we have a preference for right underneath the launcher
                        AddInSize = ((abPlaceable) ((RangedWeapon) p).GetFCS()).NumCrits();
                        while( ! AddIn ) {
                            if( Loc[i].LocationLocked() ) {
                                i++;
                            } else {
                                if( AddInSize == 2 ) {
                                    if( Loc[i+1].LocationLocked() ) {
                                        i++;
                                    } else {
                                        AddInLoc = i;
                                        AddIn = true;
                                    }
                                } else {
                                    AddInLoc = i;
                                    AddIn = true;
                                }
                            }
                        }
                    }
                    if( ((RangedWeapon) p).IsUsingCapacitor() ) {
                        // we have a preference for right underneath the launcher
                        while( ! AddIn ) {
                            if( Loc[i].LocationLocked() ) {
                                i++;
                            } else {
                                AddInLoc = i;
                                AddIn = true;
                            }
                        }
                    }
                    if( ((RangedWeapon) p).IsUsingInsulator() ) {
                        // we have a preference for right underneath the launcher
                        while( ! AddIn ) {
                            if( Loc[i].LocationLocked() ) {
                                i++;
                            } else {
                                AddInLoc = i;
                                AddIn = true;
                            }
                        }
                    }
                } else {
                    AddIn = true;
                }

                // ensure we have space for the machine guns in an array
                if( p instanceof MGArray ) {
                    int MGid = 0;
                    while( ! ArrayGood ) {
                        if( Loc[i].LocationLocked() ) {
                            i++;
                        } else {
                            MGLocs[MGid] = i;
                            MGid++;
                            NumMG++;
                            if( NumMG >= ((MGArray) p).GetNumMGs() ) {
                                ArrayGood = true;
                            } else {
                                i++;
                            }
                        }
                    }
                } else {
                    ArrayGood = true;
                }

                // now that we know we have space, let's add the item.
                for( i = SIndex; i < ( p.NumCrits() + SIndex ); i++ ) {
                    // is there a non-location locked item there?
                    if( Loc[i] != NoItem ) {
                        // we've already ensured that it is not location locked
                        // above, so put the item back into the queue.
                        if( Loc[i].CanSplit() && Loc[i].Contiguous() ) {
                            removed.add( Loc[i] );
                            rears.add( new Boolean( Loc[i].IsMountedRear() ) );
                            UnallocateAll( Loc[i], false );
                        } else {
                            removed.add( Loc[i] );
                            rears.add( new Boolean( Loc[i].IsMountedRear() ) );
                            UnallocateByIndex( i, Loc );
                        }
                    }
                    // finally, allocate the item slot
                    Loc[i] = p;
                }

                // add in the artemis system if this is a missile weapon and is
                // usiung it.  we've already checked for a good location.
                if( p instanceof RangedWeapon ) {
                    if( ((RangedWeapon) p).IsUsingFCS() ) {
                        if( Loc[AddInLoc] != NoItem ) {
                            // we've already ensured that it is not location locked
                            // above, so put the item back into the queue.
                            if( Loc[i].CanSplit() && Loc[i].Contiguous() ) {
                                removed.add( Loc[i] );
                                rears.add( new Boolean( Loc[i].IsMountedRear() ) );
                                UnallocateAll( Loc[i], false );
                            } else {
                                removed.add( Loc[i] );
                                rears.add( new Boolean( Loc[i].IsMountedRear() ) );
                                UnallocateByIndex( AddInLoc, Loc  );
                            }
                        }
                        for( int j = AddInLoc; j < AddInSize + AddInLoc; j++ ) {
                            Loc[j] = (abPlaceable) ((RangedWeapon) p).GetFCS();
                        }
                    }
                    if( ((RangedWeapon) p).IsUsingCapacitor() ) {
                        if( Loc[AddInLoc] != NoItem ) {
                            // we've already ensured that it is not location locked
                            // above, so put the item back into the queue.
                            if( Loc[i].CanSplit() && Loc[i].Contiguous() ) {
                                removed.add( Loc[i] );
                                rears.add( new Boolean( Loc[i].IsMountedRear() ) );
                                UnallocateAll( Loc[i], false );
                            } else {
                                removed.add( Loc[i] );
                                rears.add( new Boolean( Loc[i].IsMountedRear() ) );
                                UnallocateByIndex( AddInLoc, Loc  );
                            }
                        }
                        Loc[AddInLoc] = ((RangedWeapon) p).GetCapacitor();
                    }
                    if( ((RangedWeapon) p).IsUsingInsulator() ) {
                        if( Loc[AddInLoc] != NoItem ) {
                            // we've already ensured that it is not location locked
                            // above, so put the item back into the queue.
                            if( Loc[i].CanSplit() && Loc[i].Contiguous() ) {
                                removed.add( Loc[i] );
                                rears.add( new Boolean( Loc[i].IsMountedRear() ) );
                                UnallocateAll( Loc[i], false );
                            } else {
                                removed.add( Loc[i] );
                                rears.add( new Boolean( Loc[i].IsMountedRear() ) );
                                UnallocateByIndex( AddInLoc, Loc  );
                            }
                        }
                        Loc[AddInLoc] = ((RangedWeapon) p).GetInsulator();
                    }
                }

                // add in the machine guns if this is an array
                if( p instanceof MGArray ) {
                    for( i = 0; i < ((MGArray) p).GetNumMGs(); i++ ) {
                        if( Loc[MGLocs[i]] != NoItem ) {
                            // we know it's not location locked, so kick it out
                            if( Loc[MGLocs[i]].CanSplit() && Loc[MGLocs[i]].Contiguous() ) {
                                removed.add( Loc[i] );
                                rears.add( new Boolean( Loc[i].IsMountedRear() ) );
                                UnallocateAll( Loc[MGLocs[i]], false );
                            } else {
                                removed.add( Loc[i] );
                                rears.add( new Boolean( Loc[i].IsMountedRear() ) );
                                UnallocateByIndex( MGLocs[i], Loc );
                            }
                        }
                        Loc[MGLocs[i]] = ((MGArray) p).GetMGs()[i];
                    }
                }

                // now remove the item from the Queue
                RemoveFromQueue( p );
            } else {
                    // is there a non-location locked item there?
                    if( Loc[SIndex].LocationLocked() ) {
                        throw new Exception( p.CritName() + " cannot be" +
                            " allocated to that location because a non-movable" +
                            " item already exists there." );
                    }

                    // if there is an item there, put it back in the queue
                    if( Loc[SIndex] != NoItem ) {
                        // put the item back into the queue
                        if( Loc[SIndex].CanSplit() && Loc[SIndex].Contiguous() ) {
                            removed.add( Loc[SIndex] );
                            rears.add( new Boolean( Loc[SIndex].IsMountedRear() ) );
                            UnallocateAll( Loc[SIndex], false );
                        } else {
                            removed.add( Loc[SIndex] );
                            rears.add( new Boolean( Loc[SIndex].IsMountedRear() ) );
                            UnallocateByIndex( SIndex, Loc );
                        }
                    }

                    // now allocate the item.
                    Loc[SIndex] = p;

                    // once the item is allocated, we need to update it's placed
                    // counter, check the counter, and possibly remove it from the queue
                    p.IncrementPlaced();
                    if( p.NumCrits() - p.NumPlaced() <= 0 ) {
                        RemoveFromQueue( p );
                    }
            }

            // now that allocation is finished, add in the removed items if possible
            for( int i = 0; i < removed.size(); i++ ) {
                // no error handling here since the items are already in the queue
                try {
                    Allocate( (abPlaceable) removed.get( i ), -1, Loc );
                    ((abPlaceable) removed.get( i )).MountRear( ((Boolean) rears.get( i )));
                } catch( Exception e1 ) { }
            }
        } catch ( ArrayIndexOutOfBoundsException e ) {
            // reset the location
            Loc = SnapShot;

            // tell the user what happened.
            if( p instanceof RangedWeapon ) {
                if( AddIn ) {
                    if( ArrayGood ) {
                        throw new Exception( p.CritName() + " cannot be allocated because\nthere is no room for its machine guns." );
                    } else {
                        throw new Exception( p.CritName() + " cannot be allocated because there is not enough space." );
                    }
                } else {
                    throw new Exception( p.CritName() + " cannot be allocated because\nthere is no room for its additional equipment." );
                }
            } else {
                throw new Exception( p.CritName() + " cannot be allocated because there is not enough space." );
            }
        }

        Owner.SetChanged( true );
    }

    public int UnplacedCrits() {
        // returns the number of unplaced criticals in the loadout
        int Result = 0;
        int NumThisType = 1;
        abPlaceable p;
        for( int i = Queue.size() - 1; i >= 0; i-- ) {
            if( Queue.get( i ) instanceof EquipmentCollection ) {
                p = ((EquipmentCollection) Queue.get( i )).GetType();
                NumThisType = ((EquipmentCollection) Queue.get( i )).GetSize();
                Result += (( p.NumCrits() - p.NumPlaced() ) * NumThisType );
            } else {
                p = (abPlaceable) Queue.get( i );
                NumThisType = 1;
                Result += ( p.NumCrits() - p.NumPlaced() );
            }

            // special handler for FCS and PPC Capacitors
            if( p instanceof RangedWeapon ) {
                if( ((RangedWeapon) p).IsUsingFCS() ) {
                    Result += ((abPlaceable) ((RangedWeapon) p).GetFCS()).NumCrits() * NumThisType;
                }
                if( ((RangedWeapon) p).IsUsingCapacitor() ) {
                    Result += NumThisType;
                }
                if( ((RangedWeapon) p).IsUsingInsulator() ) {
                    Result += NumThisType;
                }
            }

            // special handler for MG Arrays
            if( p instanceof MGArray ) {
                Result += ((MGArray) p).GetNumMGs() * NumThisType;
            }
        }
        return Result;
    }

    public int FreeCrits() {
        // returns the number of free crits in the loadout
        int Result = 0;

        // every instance of NoItem is an unplaced crit
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] == NoItem ) {
                Result++;
            }
            if( CTCrits[i] == NoItem ) {
                Result++;
            }
            if( RTCrits[i] == NoItem ) {
                Result++;
            }
            if( LTCrits[i] == NoItem ) {
                Result++;
            }
            if( RACrits[i] == NoItem ) {
                Result++;
            }
            if( LACrits[i] == NoItem ) {
                Result++;
            }
            if( RLCrits[i] == NoItem ) {
                Result++;
            }
            if( LLCrits[i] == NoItem ) {
                Result++;
            }
        }

        // now for the last six slots in locations that have them
        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == NoItem ) {
                Result++;
            }
            if( RTCrits[i] == NoItem ) {
                Result++;
            }
            if( LTCrits[i] == NoItem ) {
                Result++;
            }
            if( RACrits[i] == NoItem ) {
                Result++;
            }
            if( LACrits[i] == NoItem ) {
                Result++;
            }
        }

        return Result;
    }

    public int FreeCrits( abPlaceable[] Loc ) {
        // returns the number of free critical slots in the given location
        int result = 0;
        for( int i = 0; i < Loc.length; i++ ) {
            if( Loc[i] == NoItem ) {
                result++;
            }
        }

        return result;
    }

    public void LockChassis() {
        // this goes through the loadout and locks all the items that need it.
        for( int i = 0; i < 6; i++ ) {
            if( ! ( HDCrits[i] instanceof EmptyItem ) ) {
                HDCrits[i].SetLocked( true );
            }
            if( ! ( CTCrits[i] instanceof EmptyItem ) ) {
                CTCrits[i].SetLocked( true );
            }
            if( ! ( LTCrits[i] instanceof EmptyItem ) ) {
                LTCrits[i].SetLocked( true );
            }
            if( ! ( RTCrits[i] instanceof EmptyItem ) ) {
                RTCrits[i].SetLocked( true );
            }
            if( ! ( LACrits[i] instanceof EmptyItem ) ) {
                LACrits[i].SetLocked( true );
            }
            if( ! ( RACrits[i] instanceof EmptyItem ) ) {
                RACrits[i].SetLocked( true );
            }
            if( ! ( LLCrits[i] instanceof EmptyItem ) ) {
                LLCrits[i].SetLocked( true );
            }
            if( ! ( RLCrits[i] instanceof EmptyItem ) ) {
                RLCrits[i].SetLocked( true );
            }
        }
        for( int i = 6; i < 12; i++ ) {
            if( ! ( CTCrits[i] instanceof EmptyItem ) ) {
                CTCrits[i].SetLocked( true );
            }
            if( ! ( LTCrits[i] instanceof EmptyItem ) ) {
                LTCrits[i].SetLocked( true );
            }
            if( ! ( RTCrits[i] instanceof EmptyItem ) ) {
                RTCrits[i].SetLocked( true );
            }
            if( ! ( LACrits[i] instanceof EmptyItem ) ) {
                LACrits[i].SetLocked( true );
            }
            if( ! ( RACrits[i] instanceof EmptyItem ) ) {
                RACrits[i].SetLocked( true );
            }
        }

        Owner.SetChanged( true );
    }

    public void UnlockChassis() {
        // this goes through the loadout and unlocks all the items that need it.
        for( int i = 0; i < 6; i++ ) {
            if( ! ( HDCrits[i] instanceof EmptyItem ) ) {
                HDCrits[i].SetLocked( false );
            }
            if( ! ( CTCrits[i] instanceof EmptyItem ) ) {
                CTCrits[i].SetLocked( false );
            }
            if( ! ( LTCrits[i] instanceof EmptyItem ) ) {
                LTCrits[i].SetLocked( false );
            }
            if( ! ( RTCrits[i] instanceof EmptyItem ) ) {
                RTCrits[i].SetLocked( false );
            }
            if( ! ( LACrits[i] instanceof EmptyItem ) ) {
                LACrits[i].SetLocked( false );
            }
            if( ! ( RACrits[i] instanceof EmptyItem ) ) {
                RACrits[i].SetLocked( false );
            }
            if( ! ( LLCrits[i] instanceof EmptyItem ) ) {
                LLCrits[i].SetLocked( false );
            }
            if( ! ( RLCrits[i] instanceof EmptyItem ) ) {
                RLCrits[i].SetLocked( false );
            }
        }
        for( int i = 6; i < 12; i++ ) {
            if( ! ( CTCrits[i] instanceof EmptyItem ) ) {
                CTCrits[i].SetLocked( false );
            }
            if( ! ( LTCrits[i] instanceof EmptyItem ) ) {
                LTCrits[i].SetLocked( false );
            }
            if( ! ( RTCrits[i] instanceof EmptyItem ) ) {
                RTCrits[i].SetLocked( false );
            }
            if( ! ( LACrits[i] instanceof EmptyItem ) ) {
                LACrits[i].SetLocked( false );
            }
            if( ! ( RACrits[i] instanceof EmptyItem ) ) {
                RACrits[i].SetLocked( false );
            }
        }

        Owner.SetChanged( true );
    }

    public ifMechLoadout Clone() {
        // Returns a clone of this loadout.  Normally used for omnimechs.
        ifMechLoadout clone = new BipedLoadout( "", Owner, HeatSinks.GetNumHS(),
            HeatSinks, Jumps );
        clone.SetRulesLevel( RulesLevel );
        clone.SetTechBase( TechBase );
        clone.SetEra( Era );
        clone.SetYear( Year, false );
        clone.SetHDCrits( HDCrits.clone() );
        clone.SetCTCrits( CTCrits.clone() );
        clone.SetLTCrits( LTCrits.clone() );
        clone.SetRTCrits( RTCrits.clone() );
        clone.SetLACrits( LACrits.clone() );
        clone.SetRACrits( RACrits.clone() );
        clone.SetRLCrits( RLCrits.clone() );
        clone.SetLLCrits( LLCrits.clone() );
        // set the new actuators
        Actuators.Transfer( clone.GetActuators() );
        if( HasHDTurret() ) {
            clone.SetHDTurret( HDTurret );
        }
        if( HasLTTurret() ) {
            clone.SetLTTurret( LTTurret );
        }
        if( HasRTTurret() ) {
            clone.SetRTTurret( RTTurret );
        }
        clone.SetClanCASE( UsingClanCASE );
        try {
            clone.SetFCSArtemisIV( UseAIVFCS );
            clone.SetFCSArtemisV( UseAVFCS );
            clone.SetFCSApollo( UseApollo );
        } catch( Exception e ) {
            // there shouldn't get any exceptions here since the non-core list
            // will be empty.  Write an event to stderr
            System.err.println( "Could not set Artemis IV for an empty loadout." );
        }
        try {
            if( IsAllocated( CTCase ) ) {
                clone.SetCTCASE( CTCase );
                clone.SetCTCASE( true, -1 );
            }
            if( IsAllocated( LTCase ) ) {
                clone.SetLTCASE( LTCase );
                clone.SetLTCASE( true, -1 );
            }
            if( IsAllocated( RTCase ) ) {
                clone.SetRTCASE( RTCase );
                clone.SetRTCASE( true, -1 );
            }
            if( IsAllocated( HDCaseII ) )
            {
                clone.SetHDCASEII(HDCaseII);
                clone.SetHDCASEII(true, -1, HDCaseII.IsClan());
            }
            if( IsAllocated( CTCaseII ) )
            {
                clone.SetCTCASEII(CTCaseII);
                clone.SetCTCASEII(true, -1, CTCaseII.IsClan());
            }
            if( IsAllocated( LTCaseII ) )
            {
                clone.SetLTCASEII(LTCaseII);
                clone.SetLTCASEII(true, -1, LTCaseII.IsClan());
            }
            if( IsAllocated( LACaseII ) )
            {
                clone.SetLACASEII(LACaseII);
                clone.SetLACASEII(true, -1, LACaseII.IsClan());
            }
            if( IsAllocated( LLCaseII ) )
            {
                clone.SetLLCASEII(LLCaseII);
                clone.SetLLCASEII(true, -1, LLCaseII.IsClan());
            }
            if( IsAllocated( RTCaseII ) )
            {
                clone.SetRTCASEII(RTCaseII);
                clone.SetRTCASEII(true, -1, RTCaseII.IsClan());
            }
            if( IsAllocated( RACaseII ) )
            {
                clone.SetRACASEII(RACaseII);
                clone.SetRACASEII(true, -1, RACaseII.IsClan());
            }
            if( IsAllocated( RLCaseII ) )
            {
                clone.SetRLCASEII(RLCaseII);
                clone.SetRLCASEII(true, -1, RLCaseII.IsClan());
            }
        } catch( Exception e ) {
            // unhandled, print a message to the error log
            System.err.println( "CASE system not reinstalled:\n" + e.getMessage() );
        }
        if( NonCore.size() > 0 ) {
            // have to move the none-core items
            clone.SetNonCore( (ArrayList) NonCore.clone() );
        }
        if( TCList.size() > 0 ) {
            clone.SetTCList( (ArrayList) TCList.clone() );
        }
        if( Equipment.size() > 0 ) {
            clone.SetEquipment( (ArrayList) Equipment.clone() );
        }
        if( HasSupercharger() ) {
            clone.SetSupercharger( SCharger );
        }
        if( Owner.IsOmnimech() ) {
            // before we unallocate the actuators, we need to make sure that
            // there are no physical weapons located there.
            for( int i = 0; i < NonCore.size(); i++ ) {
                if( NonCore.get( i ) instanceof PhysicalWeapon ) {
                    if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_RA ) {
                        clone.GetActuators().SetLockedRight( true );
                    }
                    if( Find( (abPlaceable) NonCore.get( i ) ) == LocationIndex.MECH_LOC_LA ) {
                        clone.GetActuators().SetLockedLeft( true );
                    }
                }
            }
            clone.GetActuators().RemoveLeftLowerArm();
            clone.GetActuators().RemoveRightLowerArm();
            clone.SetBaseLoadout( this );
        }
        return clone;
    }

    public void SetBaseLoadout( ifMechLoadout l ) {
        BaseLoadout = l;
    }

    public ifMechLoadout GetBaseLoadout() {
        return BaseLoadout;
    }

    public void SetHDCrits( abPlaceable[] c ) {
        // this method sets the head crits to the specified array.
        // SHOULD ONLY BE USED WHEN CLONING THE LOADOUT
        HDCrits = c;
    }

    public void SetCTCrits( abPlaceable[] c ) {
        // this method sets the center torso crits to the specified array.
        // SHOULD ONLY BE USED WHEN CLONING THE LOADOUT
        CTCrits = c;
    }

    public void SetLTCrits( abPlaceable[] c ) {
        // this method sets the left torso crits to the specified array.
        // SHOULD ONLY BE USED WHEN CLONING THE LOADOUT
        LTCrits = c;
    }

    public void SetRTCrits( abPlaceable[] c ) {
        // this method sets the right torso crits to the specified array.
        // SHOULD ONLY BE USED WHEN CLONING THE LOADOUT
        RTCrits = c;
    }

    public void SetLACrits( abPlaceable[] c ) {
        // this method sets the left arm crits to the specified array.
        // SHOULD ONLY BE USED WHEN CLONING THE LOADOUT
        LACrits = c;
    }

    public void SetRACrits( abPlaceable[] c ) {
        // this method sets the right arm crits to the specified array.
        // SHOULD ONLY BE USED WHEN CLONING THE LOADOUT
        RACrits = c;
    }

    public void SetLLCrits( abPlaceable[] c ) {
        // this method sets the left leg crits to the specified array.
        // SHOULD ONLY BE USED WHEN CLONING THE LOADOUT
        LLCrits = c;
    }

    public void SetRLCrits( abPlaceable[] c ) {
        // this method sets the right leg crits to the specified array.
        // SHOULD ONLY BE USED WHEN CLONING THE LOADOUT
        RLCrits = c;
    }

    public void SetCLCrits( abPlaceable[] c ) {
        // this method sets the center leg crits to the specified array.
        // NO CENTER LEG ON BIPEDS  -- IGNORE
        // SHOULD ONLY BE USED WHEN CLONING THE LOADOUT
    }
    
    public void SetNonCore( ArrayList v ) {
        NonCore = v;
    }

    public void SetTCList( ArrayList v ) {
        TCList = v;
    }

    public void SetEquipment( ArrayList v ) {
        Equipment = v;
    }

    public boolean CanUseClanCASE() {
        if( TechBase == AvailableCode.TECH_INNER_SPHERE ) {
            return false;
        } else {
            return true;
        }
    }

    public boolean IsUsingClanCASE() {
        return UsingClanCASE;
    }

    public void SetClanCASE( boolean b ) {
         UsingClanCASE = b;
    }

    public void SetCTCASE( boolean Add, int index ) throws Exception {
        // adds CASE equipment to the CT
        if( ! Add ) {
            Remove( CTCase );
            return;
        }
        if( Add && HasCTCASE() ) {
            return;
        }
        if( Add && TechBase == AvailableCode.TECH_CLAN ) {
            throw new Exception( "A Clan 'Mech may not mount Inner Sphere CASE equipment." );
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the CT for CASE." );
                }
                try {
                    AddToCT( CTCase, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToCT( CTCase, index );
            } catch( Exception e ) {
                throw new Exception( "CASE system could not be allocated to slot " + index + ".\nCASE system was not installed." );
            }
        }

        Owner.SetChanged( true );
    }

    public void SetLTCASE( boolean Add, int index ) throws Exception {
        // adds CASE equipment to the LT
        if( ! Add ) {
            Remove( LTCase );
            return;
        }
        if( Add && HasLTCASE() ) {
            return;
        }
        if( Add && TechBase == AvailableCode.TECH_CLAN ) {
            throw new Exception( "A Clan 'Mech may not mount Inner Sphere CASE equipment." );
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the LT for CASE." );
                }
                try {
                    AddToLT( LTCase, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToLT( LTCase, index );
            } catch( Exception e ) {
                throw new Exception( "CASE system could not be allocated to slot " + index + ".\nCASE system was not installed." );
            }
        }

        Owner.SetChanged( true );
    }

    public void SetRTCASE( boolean Add, int index ) throws Exception {
        // adds CASE equipment to the RT
        if( ! Add ) {
            Remove( RTCase );
            return;
        }
        if( Add && HasRTCASE() ) {
            return;
        }
        if( Add && TechBase == AvailableCode.TECH_CLAN ) {
            throw new Exception( "A Clan 'Mech may not mount Inner Sphere CASE equipment." );
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the RT for CASE." );
                }
                try {
                    AddToRT( RTCase, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToRT( RTCase, index );
            } catch( Exception e ) {
                throw new Exception( "CASE system could not be allocated to slot " + index + ".\nCASE system was not installed." );
            }
        }

        Owner.SetChanged( true );
    }

    public void SetCTCASE( CASE c ) {
        CTCase = c;
    }

    public void SetLTCASE( CASE c ) {
        LTCase = c;
    }

    public void SetRTCASE( CASE c ) {
        RTCase = c;
    }

    public boolean HasCTCASE() {
        return IsAllocated( CTCase );
    }

    public boolean HasLTCASE() {
        return IsAllocated( LTCase );
    }

    public boolean HasRTCASE() {
        return IsAllocated( RTCase );
    }

    public CASE GetCTCase() {
        return CTCase;
    }

    public CASE GetLTCase() {
        return LTCase;
    }

    public CASE GetRTCase() {
        return RTCase;
    }

    public void SetHDCASEII( boolean Add, int index, boolean clan ) throws Exception {
        // adds CASE II equipment to the HD
        if( ! Add ) {
            Remove( HDCaseII );
            return;
        }
        if( Add && HasHDCASEII() ) {
            return;
        }

        boolean placed = false;
        int increment = 5;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the HD for CASE II." );
                }
                try {
                    AddToHD( HDCaseII, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToHD( HDCaseII, index );
            } catch( Exception e ) {
                throw new Exception( "CASE II system could not be allocated to slot " + index + ".\nCASE II system was not installed in the HD." );
            }
        }

        HDCaseII.SetClan( clan );
        Owner.SetChanged( true );
    }

    public void SetHDCASEII( CASEII c ) {
        HDCaseII = c;
    }

    public boolean HasHDCASEII() {
        return IsAllocated( HDCaseII );
    }

    public CASEII GetHDCaseII() {
        return HDCaseII;
    }

    public void SetCTCASEII( boolean Add, int index, boolean clan ) throws Exception {
        // adds CASE II equipment to the CT
        if( ! Add ) {
            Remove( CTCaseII );
            return;
        }
        if( Add && HasCTCASEII() ) {
            return;
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the CT for CASE II." );
                }
                try {
                    AddToCT( CTCaseII, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToCT( CTCaseII, index );
            } catch( Exception e ) {
                throw new Exception( "CASE II system could not be allocated to slot " + index + ".\nCASE II system was not installed in the CT." );
            }
        }

        CTCaseII.SetClan( clan );
        Owner.SetChanged( true );
    }

    public void SetCTCASEII( CASEII c ) {
        CTCaseII = c;
    }

    public boolean HasCTCASEII() {
        return IsAllocated( CTCaseII );
    }

    public CASEII GetCTCaseII() {
        return CTCaseII;
    }

    public void SetLTCASEII( boolean Add, int index, boolean clan ) throws Exception {
        // adds CASE II equipment to the LT
        if( ! Add ) {
            Remove( LTCaseII );
            return;
        }
        if( Add && HasLTCASEII() ) {
            return;
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the LT for CASE II." );
                }
                try {
                    AddToLT( LTCaseII, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToLT( LTCaseII, index );
            } catch( Exception e ) {
                throw new Exception( "CASE II system could not be allocated to slot " + index + ".\nCASE II system was not installed in the LT." );
            }
        }

        LTCaseII.SetClan( clan );
        Owner.SetChanged( true );
    }

    public void SetLTCASEII( CASEII c ) {
        LTCaseII = c;
    }

    public boolean HasLTCASEII() {
        return IsAllocated( LTCaseII );
    }

    public CASEII GetLTCaseII() {
        return LTCaseII;
    }

    public void SetRTCASEII( boolean Add, int index, boolean clan ) throws Exception {
        // adds CASE II equipment to the RT
        if( ! Add ) {
            Remove( RTCaseII );
            return;
        }
        if( Add && HasRTCASEII() ) {
            return;
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the RT for CASE II." );
                }
                try {
                    AddToRT( RTCaseII, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToRT( RTCaseII, index );
            } catch( Exception e ) {
                throw new Exception( "CASE II system could not be allocated to slot " + index + ".\nCASE II system was not installed in the RT." );
            }
        }

        RTCaseII.SetClan( clan );
        Owner.SetChanged( true );
    }

    public void SetRTCASEII( CASEII c ) {
        RTCaseII = c;
    }

    public boolean HasRTCASEII() {
        return IsAllocated( RTCaseII );
    }

    public CASEII GetRTCaseII() {
        return RTCaseII;
    }

    public void SetLACASEII( boolean Add, int index, boolean clan ) throws Exception {
        // adds CASE II equipment to the LA
        if( ! Add ) {
            Remove( LACaseII );
            return;
        }
        if( Add && HasLACASEII() ) {
            return;
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the LT for CASE II." );
                }
                try {
                    AddToLA( LACaseII, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToLA( LACaseII, index );
            } catch( Exception e ) {
                throw new Exception( "CASE II system could not be allocated to slot " + index + ".\nCASE II system was not installed in the LA." );
            }
        }

        LACaseII.SetClan( clan );
        Owner.SetChanged( true );
    }

    public void SetLACASEII( CASEII c ) {
        LACaseII = c;
    }

    public boolean HasLACASEII() {
        return IsAllocated( LACaseII );
    }

    public CASEII GetLACaseII() {
        return LACaseII;
    }

    public void SetRACASEII( boolean Add, int index, boolean clan ) throws Exception {
        // adds CASE II equipment to the RA
        if( ! Add ) {
            Remove( RACaseII );
            return;
        }
        if( Add && HasRACASEII() ) {
            return;
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the RA for CASE II." );
                }
                try {
                    AddToRA( RACaseII, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToRA( RACaseII, index );
            } catch( Exception e ) {
                throw new Exception( "CASE II system could not be allocated to slot " + index + ".\nCASE II system was not installed in the RA." );
            }
        }

        RACaseII.SetClan( clan );
        Owner.SetChanged( true );
    }

    public void SetRACASEII( CASEII c ) {
        RACaseII = c;
    }

    public boolean HasRACASEII() {
        return IsAllocated( RACaseII );
    }

    public CASEII GetRACaseII() {
        return RACaseII;
    }

    public void SetLLCASEII( boolean Add, int index, boolean clan ) throws Exception {
        // adds CASE II equipment to the LL
        if( ! Add ) {
            Remove( LLCaseII );
            return;
        }
        if( Add && HasLLCASEII() ) {
            return;
        }

        boolean placed = false;
        int increment = 5;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the LL for CASE II." );
                }
                try {
                    AddToLL( LLCaseII, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToLL( LLCaseII, index );
            } catch( Exception e ) {
                throw new Exception( "CASE II system could not be allocated to slot " + index + ".\nCASE II system was not installed in the LL." );
            }
        }

        LLCaseII.SetClan( clan );
        Owner.SetChanged( true );
    }

    public void SetLLCASEII( CASEII c ) {
        LLCaseII = c;
    }

    public boolean HasLLCASEII() {
        return IsAllocated( LLCaseII );
    }

    public CASEII GetLLCaseII() {
        return LLCaseII;
    }

    public void SetRLCASEII( boolean Add, int index, boolean clan ) throws Exception {
        // adds CASE II equipment to the RL
        if( ! Add ) {
            Remove( RLCaseII );
            return;
        }
        if( Add && HasRLCASEII() ) {
            return;
        }

        boolean placed = false;
        int increment = 5;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the RL for CASE II." );
                }
                try {
                    AddToRL( RLCaseII, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToRL( RLCaseII, index );
            } catch( Exception e ) {
                throw new Exception( "CASE II system could not be allocated to slot " + index + ".\nCASE II system was not installed in the RL." );
            }
        }

        RLCaseII.SetClan( clan );
        Owner.SetChanged( true );
    }

    public void SetRLCASEII( CASEII c ) {
        RLCaseII = c;
    }

    public boolean HasRLCASEII() {
        return IsAllocated( RLCaseII );
    }

    public CASEII GetRLCaseII() {
        return RLCaseII;
    }

    // handlers for Artemis IV operations.
    public void SetFCSArtemisIV( boolean b ) throws Exception {
        if( b != UseAIVFCS ) {
            if( UseAVFCS && b ) {
                throw new Exception( "Artemis IV is not compatible with Artemis V.\nDisable Artemis V before enabling Artemis IV." );
            }
            UseAIVFCS = b;
            VFCSArtemisIVLoader k = new VFCSArtemisIVLoader();
            Owner.Visit( k );
            if( k.GetResult() == false ) {
                UseAIVFCS = ( ! b );
                if( UseAIVFCS ) {
                    throw new Exception( "Could not disable Artemis IV because a\nlocked launcher is using an Artemis system." );
                } else {
                    throw new Exception( "Could not enable Artemis IV because a locked\nlauncher did not have space for it's Artemis system." );
                }
            }
        }

        Owner.SetChanged( true );
    }

    public void SetFCSArtemisV( boolean b ) throws Exception {
        if( b != UseAVFCS ) {
            if( UseAIVFCS && b ) {
                throw new Exception( "Artemis V is not compatible with Artemis IV.\nDisable Artemis IV before enabling Artemis V." );
            }
            UseAVFCS = b;
            VFCSArtemisVLoader k = new VFCSArtemisVLoader();
            Owner.Visit( k );
            if( k.GetResult() == false ) {
                UseAVFCS = ( ! b );
                if( UseAVFCS ) {
                    throw new Exception( "Could not disable Artemis V because a\nlocked launcher is using an Artemis V system." );
                } else {
                    throw new Exception( "Could not enable Artemis V because a locked\nlauncher did not have space for it's Artemis V system." );
                }
            }
        }

        Owner.SetChanged( true );
    }

    public void SetFCSApollo( boolean b ) throws Exception {
        if( b != UseApollo ) {
            UseApollo = b;
            VFCSApolloLoader k = new VFCSApolloLoader();
            Owner.Visit( k );
            if( k.GetResult() == false ) {
                UseApollo = ( ! b );
                if( UseApollo ) {
                    throw new Exception( "Could not disable Apollo FCS because a\nlocked launcher is using an Apollo system." );
                } else {
                    throw new Exception( "Could not enable Apollo because a locked\nlauncher did not have space for it's Apollo system." );
                }
            }
        }

        Owner.SetChanged( true );
    }

    public boolean UsingArtemisIV() {
        return UseAIVFCS;
    }

    public boolean UsingArtemisV() {
        return UseAVFCS;
    }

    public boolean UsingApollo() {
        return UseApollo;
    }

    public boolean UsingTC() {
        return Use_TC;
    }

    public TargetingComputer GetTC() {
        return CurTC;
    }

    public boolean UsingDumper(){
        return Use_Dumper;
    }

    public Dumper GetDumper(){
        return CurDumper;
    }

    public void UseDumper (boolean use, String dumpDirection)
    {
        if (use == Use_Dumper){
            return;
        }
        else {
            Use_Dumper = use;
        }
        CurDumper.SetDumpDirection(dumpDirection);
        CheckDumper();
        Owner.SetChanged(true);
    }

    public void CheckDumper(){
        if( ! Use_Dumper ) {
            // remove the TC from the loadout
            Remove( CurDumper );
            return;
        }

        if( ! QueueContains( CurDumper ) ) {
            if( ! IsAllocated( CurDumper ) ) {
                // dumper not allocated or in the queue, let's see if we can add it
                if( CurDumper.NumCrits() > 0 ) {
                    AddToQueue( CurDumper );
                } else {
                    Remove( CurDumper );
                }
            }
        }
    }

    public void UseTC( boolean use, boolean clan ) {
        if( use == Use_TC ) {
            return;
        } else {
            Use_TC = use;
        }

        CurTC.SetClan( clan );
        CheckTC();
        Owner.SetChanged( true );
    }

    public void CheckTC() {
        // this routine checks to see if the targeting computer can be allocated
        // and does so if needed.  It will also remove the TC if it has to.
        if( ! Use_TC ) {
            // remove the TC from the loadout
            Remove( CurTC );
            return;
        }

        if( ! QueueContains( CurTC ) ) {
            if( ! IsAllocated( CurTC ) ) {
                // TC not allocated or in the queue, let's see if we can add it
                if( CurTC.NumCrits() > 0 ) {
                    AddToQueue( CurTC );
                } else {
                    Remove( CurTC );
                }
            }
        }

        // lastly, see if we need to remove it altogether
        if( CurTC.NumCrits() <= 0 ) { Remove( CurTC ); }

        Owner.SetChanged( true );
    }

    public void UnallocateTC() {
        // unallocates the TC from the loadout and then performs a TC check
        Remove( CurTC );
        CheckTC();
    }

    public void SetSupercharger( boolean b, int Loc, int index ) throws Exception {
        // see if the user wants to change its location
        if( HasSupercharger() == b ) {
            if( b == false ) { return; }
            if( Find( SCharger ) == Loc ) {
                // no move, ignore it
                return;
            } else {
                // remove it and continue
                Remove( SCharger );
            }
        }
        if( b == false ) {
            Remove( SCharger );
            return;
        }

        // ensure we have engine slots in the location given
        if( index < 0 ) {
            boolean placed = false;
            int increment = 0;
            switch( Loc ) {
                case LocationIndex.MECH_LOC_CT:
                    while( placed == false ) {
                        if ( increment > 11 ) {
                            throw new Exception( "No room was available in the CT for the Superchager.\nIt has been removed." );
                        }
                        try {
                            AddToCT( SCharger, increment );
                            placed = true;
                        } catch ( Exception e ) {
                            increment++;
                        }
                    }
                    break;
                case LocationIndex.MECH_LOC_LT:
                    if( Owner.GetEngine().GetSideTorsoCrits() < 1 ) {
                        throw new Exception( "Supercharger can only be placed in a location with Engine criticals." );
                    }
                    while( placed == false ) {
                        if ( increment > 11 ) {
                            throw new Exception( "No room was available in the LT for the Superchager.\nIt has been removed." );
                        }
                        try {
                            AddToLT( SCharger, increment );
                            placed = true;
                        } catch ( Exception e ) {
                            increment++;
                        }
                    }
                    break;
                case LocationIndex.MECH_LOC_RT:
                    if( Owner.GetEngine().GetSideTorsoCrits() < 1 ) {
                        throw new Exception( "Supercharger can only be placed in a location with Engine criticals." );
                    }
                    while( placed == false ) {
                        if ( increment > 11 ) {
                            throw new Exception( "No room was available in the RT for the Superchager.\nIt has been removed." );
                        }
                        try {
                            AddToRT( SCharger, increment );
                            placed = true;
                        } catch ( Exception e ) {
                            increment++;
                        }
                    }
                    break;
                default:
                    throw new Exception( "Supercharger can only be placed in a location with Engine criticals." );
            }
        } else {
            switch( Loc ) {
                case LocationIndex.MECH_LOC_CT:
                    try {
                        AddToCT( SCharger, index );
                    } catch( Exception e ) {
                        throw new Exception( "Supercharger cannot be allocated to slot " + index + "\nSupercharger not installed." );
                    }
                    break;
                case LocationIndex.MECH_LOC_LT:
                    try {
                        AddToLT( SCharger, index );
                    } catch( Exception e ) {
                        throw new Exception( "Supercharger cannot be allocated to slot " + index + "\nSupercharger not installed." );
                    }
                    break;
                case LocationIndex.MECH_LOC_RT:
                    try {
                        AddToRT( SCharger, index );
                    } catch( Exception e ) {
                        throw new Exception( "Supercharger cannot be allocated to slot " + index + "\nSupercharger not installed." );
                    }
                    break;
                default:
                    throw new Exception( "Supercharger can only be placed in a location with Engine criticals." );
            }
        }

        AddMechModifier( SCharger.GetMechModifier() );

        Owner.SetChanged( true );
    }

    public void SetSupercharger( Supercharger s ) {
        // this sets the loadout's supercharger to a different one.
        // Used for cloning purposes only!
        SCharger = s;
        AddMechModifier( SCharger.GetMechModifier() );
    }

    public boolean HasSupercharger() {
        if( IsAllocated( SCharger ) ) {
            return true;
        } else {
            return false;
        }
    }

    public Supercharger GetSupercharger() {
        return SCharger;
    }

    public PowerAmplifier GetPowerAmplifier() {
        return PowerAmp;
    }

    public MechTurret GetHDTurret() {
        return HDTurret;
    }

    public void SetHDTurret( boolean Add, int index ) throws Exception {
        if( ! Add ) {
            Remove( HDTurret );
            ClearTurretWeapons( HDTurret );
            return;
        }
        if( Add && HasHDTurret() ) {
            return;
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the CT for a Head Turret." );
                }
                try {
                    AddToCT( HDTurret, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToCT( HDTurret, index );
            } catch( Exception e ) {
                throw new Exception( "Head Turret could not be allocated to slot " + index + ".\nHead Turret system was not installed in the CT." );
            }
        }

        Owner.SetChanged( true );
    }

    public boolean HasHDTurret() {
        return IsAllocated( HDTurret );
    }

    public boolean CanUseHDTurret() {
        if( Owner.GetCockpit().IsTorsoMounted() ) {
            return true;
        }
        return false;
    }

    public void SetHDTurret( MechTurret t ) {
        HDTurret = t;
    }

    public MechTurret GetLTTurret() {
        return LTTurret;
    }

    public void SetLTTurret( boolean Add, int index ) throws Exception {
        if( ! Add ) {
            Remove( LTTurret );
            ClearTurretWeapons( LTTurret );
            return;
        }
        if( Add && HasLTTurret() ) {
            return;
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the LT for a Turret." );
                }
                try {
                    AddToLT( LTTurret, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToLT( LTTurret, index );
            } catch( Exception e ) {
                throw new Exception( "Turret could not be allocated to slot " + index + ".\nTurret system was not installed in the LT." );
            }
        }

        Owner.SetChanged( true );
    }

    public boolean HasLTTurret() {
        return IsAllocated( LTTurret );
    }

    public void SetLTTurret( MechTurret t ) {
        LTTurret = t;
    }

    public boolean CanUseLTTurret() {
        return true;
    }

    public MechTurret GetRTTurret() {
        return RTTurret;
    }

    public void SetRTTurret( boolean Add, int index ) throws Exception {
        if( ! Add ) {
            Remove( RTTurret );
            ClearTurretWeapons( RTTurret );
            return;
        }
        if( Add && HasRTTurret() ) {
            return;
        }

        boolean placed = false;
        int increment = 11;
        if( index < 0 ) {
            // general placement routine
            while( placed == false ) {
                if ( increment < 0 ) {
                    throw new Exception( "There is not enough space in the RT for a Turret." );
                }
                try {
                    AddToRT( RTTurret, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
        } else {
            // specific placement routine
            try {
                AddToRT( RTTurret, index );
            } catch( Exception e ) {
                throw new Exception( "Turret could not be allocated to slot " + index + ".\nTurret system was not installed in the RT." );
            }
        }

        Owner.SetChanged( true );
    }

    public boolean HasRTTurret() {
        return IsAllocated( RTTurret );
    }

    public void SetRTTurret( MechTurret t ) {
        RTTurret = t;
    }

    public boolean CanUseRTTurret() {
        return true;
    }

    private void ClearTurretWeapons( MechTurret t ) {
        for( int i = 0; i < NonCore.size(); i++ ) {
            if( NonCore.get( i ) instanceof RangedWeapon ) {
                RangedWeapon w = (RangedWeapon) NonCore.get( i );
                if( w.IsTurreted() ) {
                    if( w.GetTurret() == t ) {
                        w.RemoveFromTurret( t );
                    }
                }
            }
        }
    }

    public void UnallocateFuelTanks() {
        // removes the fuel tanks from the loadout (if needed) when the engine
        // changes (the crit size may change).
        for( int i = 0; i < NonCore.size(); i++ ) {
            if( NonCore.get( i ) instanceof ExtendedFuelTank ) {
                UnallocateAll( (abPlaceable) NonCore.get( i ), true );
            }
        }
    }

    @Override
    public void SetBoobyTrap( boolean b ) throws Exception{
        if ( b == false && this.HasBoobyTrap() )
        {
            this.Remove(BTrap);
            return;
        }

        if ( b == true && !this.HasBoobyTrap() )
        {
            // Booby Traps are always located in the center torso and take up
            // a single crit location.
            //
            // ...
            // Interesting note.  TacOps states that Booby Traps must be in the same location
            // as a unit's engine.  However the example states this must be the CT.
            // I wonder if booby traps may be placed in RT/LT if the unit has a
            // XL engine?

            boolean placed = false;
            int increment = 0;

            while( placed == false ) {
                if ( increment > 11 ) {
                    throw new Exception( "No room was available in the CT for the Booby Trap.\nIt has been removed." );
                }
                try {
                    AddToCT( BTrap, increment );
                    placed = true;
                } catch ( Exception e ) {
                    increment++;
                }

            }
        }
    }

    @Override
    public boolean HasBoobyTrap() {
        if ( IsAllocated (BTrap) )
            return true;
        else
            return false;
    }

    @Override
    public BoobyTrap GetBoobyTrap() {
        return BTrap;
    }

    public void CheckExclusions( abPlaceable p ) throws Exception {
        // this checks all the items in the loadout vs. the placeable's exclusions
        // not worried about a return value since we're tossing exceptions

        // check basic requirements first
        if( p instanceof RangedWeapon ) {
            if( ((RangedWeapon) p).RequiresNuclear() &! Owner.GetEngine().IsNuclear() ) {
                throw new Exception( p.CritName() + " may not be mounted as it requires a nuclear engine." );
            }
            if( ((RangedWeapon) p).RequiresFusion() &! Owner.GetEngine().IsFusion() ) {
                throw new Exception( p.CritName() + " may not be mounted as it requires a fusion engine." );
            }
        }
        if( p instanceof Talons ) {
            for( int i = 0; i < NonCore.size(); i++ ) {
                if( NonCore.get( i ) instanceof Talons ) {
                    throw new Exception( "A 'Mech cannot mount two sets of talons." );
                }
            }
        }
        if( p instanceof ExtendedFuelTank ) {
            if( ! Owner.GetEngine().IsICE() &! Owner.GetEngine().isFuelCell() ) {
                throw new Exception( p.CritName() + " may not be mounted on this 'Mech because the engine is incompatible." );
            }
        }
        
        //HarJel requires Standard, Heavy Industrial, Light Ferro Fibrous, Standard Ferro Fibrous, or Heavy Ferro Fibrous armor
        if ( p.ActualName().contains("HarJel"))
            if ( !Owner.GetArmor().AllowHarJel() )
                throw new Exception( p.CritName() + " may not be mounted on this 'Mech with " + Owner.GetArmor().ActualName());
        
        if( p.GetExclusions() == null ) { return; }
        String[] exclude = p.GetExclusions().GetExclusions();

        for( int i = 0; i < exclude.length; i++ ) {
            // queue first
            abPlaceable test;
            for( int j = 0; j < Queue.size(); j++ ) {
                if( Queue.get( j ) instanceof EquipmentCollection ) {
                    test = ((EquipmentCollection) Queue.get( j )).GetType();
                } else {
                    test = (abPlaceable) Queue.get( j );
                }
                if( test.CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + ((abPlaceable) Queue.get( j )).CritName() );
                }
            }
            // check the loadout proper
            for( int j = 0; j < 6; j++ ) {
                if( HDCrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + HDCrits[j].CritName() );
                }
                if( CTCrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + CTCrits[j].CritName() );
                }
                if( LTCrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + LTCrits[j].CritName() );
                }
                if( RTCrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + RTCrits[j].CritName() );
                }
                if( LACrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + LACrits[j].CritName() );
                }
                if( RACrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + RACrits[j].CritName() );
                }
                if( LLCrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + LLCrits[j].CritName() );
                }
                if( RLCrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + RLCrits[j].CritName() );
                }
            }
            for( int j = 6; j < 12; j++ ) {
                if( CTCrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + CTCrits[j].CritName() );
                }
                if( LTCrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + LTCrits[j].CritName() );
                }
                if( RTCrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + RTCrits[j].CritName() );
                }
                if( LACrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + LACrits[j].CritName() );
                }
                if( RACrits[j].CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + RACrits[j].CritName() );
                }
            }
            // special addition for a targeting computer that is not in the loadout yet
            if( Use_TC ) {
                if( CurTC.CritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.CritName() + " if it\nalready mounts an " + CurTC.CritName() );
                }
            }
        }
    }

    public void AddMechModifier( MechModifier m ) {
        if( m == null ) { return; }
        if( ! MechMods.contains( m ) &! Owner.GetMechMods().contains( m ) ) {
            MechMods.add( m );
        }
    }

    public void RemoveMechMod( MechModifier m ) {
        if( m == null ) { return; }
        MechMods.remove( m );
    }

    public ArrayList GetMechMods() {
        return MechMods;
    }

    @Override
    public String toString() {
        return "Loadout: " + Name;
    }

    public Engine GetEngine() {
        return Owner.GetEngine();
    }

    public boolean UsingFractionalAccounting() {
        return Owner.UsingFractionalAccounting();
    }

    public ifUnit GetUnit() {
        return Owner;
    }
    
    /**
     * Checks the location given for instances of EmptyItem
     * @param Location What area of crits to check
     * @return True if EmptyItem found, False if not.
     */
    public boolean HasSpace( int Location ) {
        abPlaceable[] Section = new abPlaceable[0];
        switch (Location) {
            case LocationIndex.MECH_LOC_CT:
                Section = CTCrits;
                break;
            case LocationIndex.MECH_LOC_HD:
                Section = HDCrits;
                break;
            case LocationIndex.MECH_LOC_LA:
                Section = LACrits;
                break;
            case LocationIndex.MECH_LOC_LL:
                Section = LLCrits;
                break;
            case LocationIndex.MECH_LOC_LT:
                Section = LTCrits;
                break;
            case LocationIndex.MECH_LOC_RA:
                Section = RACrits;
                break;
            case LocationIndex.MECH_LOC_RL:
                Section = RLCrits;
                break;
            case LocationIndex.MECH_LOC_RT:
                Section = RTCrits;
                break;
        }
        
        for ( abPlaceable a : Section ) {
             if ( a instanceof EmptyItem )
                 return true;
        }
        return false;
    }

    public void AddToCL(abPlaceable p) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void AddToCL(abPlaceable p, int SIndex) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public abPlaceable[] GetCLCrits() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void SetCLCASEII(boolean Add, int index, boolean clan) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void SetCLCASEII(CASEII c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean HasCLCASEII() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public CASEII GetCLCaseII() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

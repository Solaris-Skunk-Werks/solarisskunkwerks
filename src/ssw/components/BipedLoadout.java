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

package ssw.components;

import java.util.Vector;
import ssw.*;
import ssw.visitors.VArtemisLoader;

public class BipedLoadout implements ifLoadout {
    // Loadouts provide critical locations for all of a mech's equipment.
    // Each mech will have a basic loadout for it's chassis, equipment, and
    // extras.  Omnimechs may have several loadouts.  We're using arrays because
    // space inside a mech is fixed.

    // Declares
    private String Name;
    private final static EmptyItem NoItem = new EmptyItem();
    private Vector Queue = new Vector(),
                   NonCore = new Vector(),
                   Equipment = new Vector(),
                   TCList = new Vector(),
                   MechMods = new Vector();
    private Mech Owner;
    private JumpJetFactory Jumps;
    private HeatSinkFactory HeatSinks;
    private ActuatorSet Actuators;
    private ISCASE CTCase = new ISCASE(),
                   LTCase = new ISCASE(),
                   RTCase = new ISCASE();
    private boolean A4FCS_SRM = false,
                    A4FCS_LRM = false,
                    A4FCS_MML = false,
                    Use_TC = false;
    private TargetingComputer CurTC = new TargetingComputer( this );
    private ifLoadout BaseLoadout = null;
    private PowerAmplifier PowerAmp = new PowerAmplifier( this );
    private int RulesLevel = Constants.TOURNAMENT;

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
    }

    public String GetName() {
        return Name;
    }

    public Mech GetMech() {
        return Owner;
    }

    public boolean IsQuad() {
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
        if( NewLevel < Owner.GetBaseRulesLevel() ) {
            return false;
        } else {
            RulesLevel = NewLevel;
            return true;
        }
    }

    public void AddToQueue( abPlaceable p ) {
        // Adds the specified placeable to the general queue for manual placement
        // ensure the item isn't already in the queue
        if( Queue.contains( p ) ) { return ; }

        // if this component has 0 total crits, we'll simply ignore it
        if( p.NumCrits() > 0 ) {
            Queue.add( p );
        }

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

            // if the component has a modifier, add it to the loadout
            if( p.GetMechModifier() != null ) {
                AddMechModifier( p.GetMechModifier() );
            }
        } else {
            // if the component has a modifier, add it to the 'Mech
            if( p.GetMechModifier() != null ) {
                Owner.AddMechModifier( p.GetMechModifier() );
            }
        }
    }

    public void RemoveFromQueue( abPlaceable p ) {
        // removes the selected item from the queue.  This makes the object
        // completely disappear from the loadout so should only be used after
        // placing it or when actually removing it from the loadout.
        if( Queue.contains(p) ) {
            Queue.remove(p);
        } else {
            return;
        }
    }

    public abPlaceable GetFromQueueByIndex( int Index ) {
        return (abPlaceable) Queue.get( Index );
    }

    public Vector GetQueue() {
        // returns the queue
        return Queue;
    }

    public Vector GetNonCore() {
        return NonCore;
    }

    public Vector GetEquipment() {
        return Equipment;
    }

    public Vector GetTCList() {
        return TCList;
    }

    public void ClearQueue() {
        // This method completely clears the queue, so be careful when using it.
        Queue.clear();
        NonCore.clear();
        Equipment.clear();
        TCList.clear();
        MechMods.clear();
    }

    public void FullUnallocate() {
        // this removes all items from the loadout.
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
    }

    public void SafeClearLoadout() {
        // this routine clears the loadout of any items that are non-core
        for( int i = NonCore.size() - 1; i >= 0; i-- ) {
            abPlaceable p = (abPlaceable) NonCore.get( i );
            if( ! p.LocationLocked() ) {
                Remove( (abPlaceable) NonCore.get( i ) );
            }
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
    }

    public void Transfer( ifLoadout l ) {
        // any time we get a new loadout, we must transfer the non-core components
        // to the new loadout.
        abPlaceable p;
        for( int i = NonCore.size(); i > 0; i-- ) {
            p = (abPlaceable) NonCore.lastElement();
            l.AddToQueue( p );
            NonCore.remove( p );
        }
    }

    public void AddTo( abPlaceable p, int Loc, int SIndex ) throws Exception {
        switch( Loc ) {
            case Constants.LOC_HD:
                AddToHD( p, SIndex );
                break;
            case Constants.LOC_CT:
                AddToCT( p, SIndex );
                break;
            case Constants.LOC_LT:
                AddToLT( p, SIndex );
                break;
            case Constants.LOC_RT:
                AddToRT( p, SIndex );
                break;
            case Constants.LOC_LA:
                AddToLA( p, SIndex );
                break;
            case Constants.LOC_RA:
                AddToRA( p, SIndex );
                break;
            case Constants.LOC_LL:
                AddToLL( p, SIndex );
                break;
            case Constants.LOC_RL:
                AddToRL( p, SIndex );
                break;
            default:
                throw new Exception( "Location not recognized or not an integer\nwhile placing " + p.GetCritName() );
        }
    }

    public void AddToHD( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the head
        for( int i = HDCrits.length - 1; i >= 0; i-- ) {
            if( HDCrits[i] == NoItem ) {
                AddToHD( p, i );
                break;
            }
        }
    }

    public void AddToCT( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the center torso
        for( int i = CTCrits.length - 1; i >= 0; i-- ) {
            if( CTCrits[i] == NoItem ) {
                AddToCT( p, i );
                break;
            }
        }
    }

    public void AddToLT( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the left torso
        for( int i = LTCrits.length - 1; i >= 0; i-- ) {
            if( LTCrits[i] == NoItem ) {
                AddToLT( p, i );
                break;
            }
        }
    }

    public void AddToRT( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the right torso
        for( int i = RTCrits.length - 1; i >= 0; i-- ) {
            if( RTCrits[i] == NoItem ) {
                AddToRT( p, i );
                break;
            }
        }
    }

    public void AddToLA( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the left arm
        for( int i = LACrits.length - 1; i >= 0; i-- ) {
            if( LACrits[i] == NoItem ) {
                AddToLA( p, i );
                break;
            }
        }
    }

    public void AddToRA( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the right arm
        for( int i = RACrits.length - 1; i >= 0; i-- ) {
            if( RACrits[i] == NoItem ) {
                AddToRA( p, i );
                break;
            }
        }
    }

    public void AddToLL( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the left leg
        for( int i = LLCrits.length - 1; i >= 0; i-- ) {
            if( LLCrits[i] == NoItem ) {
                AddToLL( p, i );
                break;
            }
        }
    }

    public void AddToRL( abPlaceable p ) throws Exception {
        // adds the specified item into the next available slot in the right leg
        for( int i = RLCrits.length - 1; i >= 0; i-- ) {
            if( RLCrits[i] == NoItem ) {
                AddToRL( p, i );
                break;
            }
        }
    }

    public void AddToHD( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocHD() ) {
            throw new Exception( p.GetCritName() +
                " cannot be allocated to the head." );
        } else {
            try {
                Allocate( p, SIndex, HDCrits );
            } catch( Exception e ) {
                throw e;
            }
        }
    }

    public void AddToCT( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocCT() ) {
            throw new Exception( p.GetCritName() +
                " cannot be allocated to the center torso." );
        } else {
            try {
                Allocate( p, SIndex, CTCrits );
            } catch( Exception e ) {
                throw e;
            }
        }
    }

    public void AddToRT( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocTorso() ) {
            throw new Exception( p.GetCritName() +
                " cannot be allocated to the right torso." );
        } else {
            try {
                Allocate( p, SIndex, RTCrits );
            } catch( Exception e ) {
                throw e;
            }
        }
    }

    public void AddToLT( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocTorso() ) {
            throw new Exception( p.GetCritName() +
                " cannot be allocated to the left torso." );
        } else {
            try {
                Allocate( p, SIndex, LTCrits );
            } catch( Exception e ) {
                throw e;
            }
        }
    }

    public void AddToRA( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocArms() ) {
            throw new Exception( p.GetCritName() +
                " cannot be allocated to the right arm." );
        } else {
            if( p instanceof PhysicalWeapon ) {
                if( ! ( RACrits[2] instanceof Actuator ) || ! ( RACrits[3] instanceof Actuator ) ) {
                    throw new Exception( p.GetCritName() +
                        " cannot be allocated to the right arm because\nthe arm does not have a hand or lower arm actuator." );
                }
            }
            if( p instanceof ifWeapon ) {
                if( ((ifWeapon) p).OmniRestrictActuators() && Owner.IsOmnimech() ) {
                    if( ( RACrits[2] instanceof Actuator ) || ( RACrits[3] instanceof Actuator ) ) {
                        // check for physical weapons before removing the actuators
                        // otherwise throw an exception
                        for( int i = 0; i < NonCore.size(); i++ ) {
                            if( NonCore.get( i ) instanceof PhysicalWeapon ) {
                                if( Find( (abPlaceable) NonCore.get( i ) ) == Constants.LOC_RA ) {
                                    throw new Exception( p.GetCritName() +
                                        " cannot be allocated to the right arm\n" +
                                        "because the arm contains lower arm or hand actuators." );
                                }
                            }
                        }
                        Actuators.RemoveRightLowerArm();
                    }
                }
            }
            try {
                Allocate( p, SIndex, RACrits );
            } catch( Exception e ) {
                throw e;
            }
        }
    }

    public void AddToLA( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocArms() ) {
            throw new Exception( p.GetCritName() +
                " cannot be allocated to the left arm." );
        } else {
            if( p instanceof PhysicalWeapon ) {
                if( ! ( LACrits[2] instanceof Actuator ) || ! ( LACrits[3] instanceof Actuator ) ) {
                    throw new Exception( p.GetCritName() +
                        " cannot be allocated to the left arm because\nthe arm does not have a hand or lower arm actuator." );
                }
            }
            if( p instanceof ifWeapon ) {
                if( ((ifWeapon) p).OmniRestrictActuators() && Owner.IsOmnimech() ) {
                    if( ( LACrits[2] instanceof Actuator ) || ( LACrits[3] instanceof Actuator ) ) {
                        // check for physical weapons before removing the actuators
                        // otherwise throw an exception
                        for( int i = 0; i < NonCore.size(); i++ ) {
                            if( NonCore.get( i ) instanceof PhysicalWeapon ) {
                                if( Find( (abPlaceable) NonCore.get( i ) ) == Constants.LOC_LA ) {
                                    throw new Exception( p.GetCritName() +
                                        " cannot be allocated to the right arm\n" +
                                        "because the arm contains lower arm or hand actuators." );
                                }
                            }
                        }
                        Actuators.RemoveLeftLowerArm();
                    }
                }
            }
            try {
                Allocate( p, SIndex, LACrits );
            } catch( Exception e ) {
                throw e;
            }
        }
    }

    public void AddToRL( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocLegs() ) {
            throw new Exception( p.GetCritName() +
                " cannot be allocated to the right leg." );
        } else {
            try {
                Allocate( p, SIndex, RLCrits );
            } catch( Exception e ) {
                throw e;
            }
        }
    }

    public void AddToLL( abPlaceable p, int SIndex ) throws Exception {
        // Can we allocate the item here?
        if( ! p.CanAllocLegs() ) {
            throw new Exception( p.GetCritName() +
                " cannot be allocated to the left leg." );
        } else {
            try {
                Allocate( p, SIndex, LLCrits );
            } catch( Exception e ) {
                throw e;
            }
        }
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
        case Constants.LOC_HD:
            return HDCrits;
        case Constants.LOC_CT:
            return CTCrits;
        case Constants.LOC_LT:
            return LTCrits;
        case Constants.LOC_RT:
            return RTCrits;
        case Constants.LOC_LA:
            return LACrits;
        case Constants.LOC_RA:
            return RACrits;
        case Constants.LOC_LL:
            return LLCrits;
        case Constants.LOC_RL:
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
                innermost = Constants.LOC_HD;
            }
            if( CTCrits[i] == p ) {
                if( innermost > Constants.LOC_CT ) {
                    innermost = Constants.LOC_CT;
                }
            }
            if( LTCrits[i] == p ) {
                if( innermost > Constants.LOC_LT ) {
                    innermost = Constants.LOC_LT;
                }
            }
            if( RTCrits[i] == p ) {
                if( innermost > Constants.LOC_RT ) {
                    innermost = Constants.LOC_RT;
                }
            }
            if( RLCrits[i] == p ) {
                if( innermost > Constants.LOC_RT ) {
                    innermost = Constants.LOC_RL;
                }
            }
            if( LLCrits[i] == p ) {
                if( innermost > Constants.LOC_RT ) {
                    innermost = Constants.LOC_LL;
                }
            }
            if( RACrits[i] == p ) {
                if( innermost >= Constants.LOC_LA && innermost <= Constants.LOC_RA || innermost == 11 ) {
                    innermost = Constants.LOC_RA;
                }
            }
            if( LACrits[i] == p ) {
                if( innermost >= Constants.LOC_LA && innermost <= Constants.LOC_RA || innermost == 11 ) {
                    innermost = Constants.LOC_LA;
                }
            }
        }

        // now for the last six slots in locations that have them
        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == p ) {
                if( innermost > Constants.LOC_CT ) {
                    innermost = Constants.LOC_CT;
                }
            }
            if( LTCrits[i] == p ) {
                if( innermost > Constants.LOC_LT ) {
                    innermost = Constants.LOC_LT;
                }
            }
            if( RTCrits[i] == p ) {
                if( innermost > Constants.LOC_RT ) {
                    innermost = Constants.LOC_RT;
                }
            }
            if( RACrits[i] == p ) {
                if( innermost >= Constants.LOC_LA && innermost <= Constants.LOC_RA || innermost == 11 ) {
                    innermost = Constants.LOC_RA;
                }
            }
            if( LACrits[i] == p ) {
                if( innermost >= Constants.LOC_LA && innermost <= Constants.LOC_RA || innermost == 11 ) {
                    innermost = Constants.LOC_LA;
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
                l.Location = Constants.LOC_HD;
                return l;
            }
            if( CTCrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_CT;
                return l;
            }
            if( LTCrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_LT;
                return l;
            }
            if( RTCrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_RT;
                return l;
            }
            if( RLCrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_RL;
                return l;
            }
            if( LLCrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_LL;
                return l;
            }
            if( RACrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_RA;
                return l;
            }
            if( LACrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_LA;
                return l;
            }
        }

        // now for the last six slots in locations that have them
        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_CT;
                return l;
            }
            if( LTCrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_LT;
                return l;
            }
            if( RTCrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_RT;
                return l;
            }
            if( RACrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_RA;
                return l;
            }
            if( LACrits[i] == p ) {
                l.Index = i;
                l.Location = Constants.LOC_LA;
                return l;
            }
        }

        return l;
    }

    public Vector FindSplitIndex( abPlaceable p ) {
        // finds the split location indexes of a contiguous item that can split.
        // the vector should never be larger than two in size.
        Vector v = new Vector();
        LocationIndex l;
        int NumHere = 0;
        boolean[] searched = { false, false, false, false, false, false, false, false };
        for( int i = 0; i < 6; i++ ) {
            if( ! searched[Constants.LOC_HD] ) {
                if( HDCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_HD;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < HDCrits.length; j++ ) {
                        if( HDCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_HD] = true;
                }
            }
            if( ! searched[Constants.LOC_CT] ) {
                if( CTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_CT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < CTCrits.length; j++ ) {
                        if( CTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_CT] = true;
                }
            }
            if( ! searched[Constants.LOC_LT] ) {
                if( LTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_LT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < LTCrits.length; j++ ) {
                        if( LTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_LT] = true;
                }
            }
            if( ! searched[Constants.LOC_RT] ) {
                if( RTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_RT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < RTCrits.length; j++ ) {
                        if( RTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_RT] = true;
                }
            }
            if( ! searched[Constants.LOC_LA] ) {
                if( LACrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_LA;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < LACrits.length; j++ ) {
                        if( LACrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_LA] = true;
                }
            }
            if( ! searched[Constants.LOC_RA] ) {
                if( RACrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_RA;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < RACrits.length; j++ ) {
                        if( RACrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_RA] = true;
                }
            }
            if( ! searched[Constants.LOC_LL] ) {
                if( LLCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_LL;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < LLCrits.length; j++ ) {
                        if( LLCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_LL] = true;
                }
            }
            if( ! searched[Constants.LOC_RL] ) {
                if( RLCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_RL;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < RLCrits.length; j++ ) {
                        if( RLCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_RL] = true;
                }
            }
        }

        // now for the last six slots in locations that have them
        for( int i = 6; i < 12; i++ ) {
            if( ! searched[Constants.LOC_CT] ) {
                if( CTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_CT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < CTCrits.length; j++ ) {
                        if( CTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_CT] = true;
                }
            }
            if( ! searched[Constants.LOC_LT] ) {
                if( LTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_LT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < LTCrits.length; j++ ) {
                        if( LTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_LT] = true;
                }
            }
            if( ! searched[Constants.LOC_RT] ) {
                if( RTCrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_RT;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < RTCrits.length; j++ ) {
                        if( RTCrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_RT] = true;
                }
            }
            if( ! searched[Constants.LOC_LA] ) {
                if( LACrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_LA;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < LACrits.length; j++ ) {
                        if( LACrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_LA] = true;
                }
            }
            if( ! searched[Constants.LOC_RA] ) {
                if( RACrits[i] == p ) {
                    l = new LocationIndex();
                    l.Index = i;
                    l.Location = Constants.LOC_RA;
                    NumHere = 1;
                    // find how many are here
                    for( int j = i + 1; j < RACrits.length; j++ ) {
                        if( RACrits[j] == p ) {
                            NumHere++;
                        }
                    }
                    l.Number = NumHere;
                    v.add( l );
                    searched[Constants.LOC_RA] = true;
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
                retval[Constants.LOC_HD]++;
            }
            if( CTCrits[i] == p ) {
                retval[Constants.LOC_CT]++;
            }
            if( LTCrits[i] == p ) {
                retval[Constants.LOC_LT]++;
            }
            if( RTCrits[i] == p ) {
                retval[Constants.LOC_RT]++;
            }
            if( LACrits[i] == p ) {
                retval[Constants.LOC_LA]++;
            }
            if( RACrits[i] == p ) {
                retval[Constants.LOC_RA]++;
            }
            if( LLCrits[i] == p ) {
                retval[Constants.LOC_LL]++;
            }
            if( RLCrits[i] == p ) {
                retval[Constants.LOC_RL]++;
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == p ) {
                retval[Constants.LOC_CT]++;
            }
            if( LTCrits[i] == p ) {
                retval[Constants.LOC_LT]++;
            }
            if( RTCrits[i] == p ) {
                retval[Constants.LOC_RT]++;
            }
            if( LACrits[i] == p ) {
                retval[Constants.LOC_LA]++;
            }
            if( RACrits[i] == p ) {
                retval[Constants.LOC_RA]++;
            }
        }
        return retval;
    }

    public Vector FindIndexes( abPlaceable p ) {
        // returns a vector full of location indexes for the XML writer
        // this should only be used for non-contiguous items
        Vector v = new Vector();
        LocationIndex l;
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_HD;
                v.add( l );
            }
            if( CTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_CT;
                v.add( l );
            }
            if( LTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_LT;
                v.add( l );
            }
            if( RTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_RT;
                v.add( l );
            }
            if( LACrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_LA;
                v.add( l );
            }
            if( RACrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_RA;
                v.add( l );
            }
            if( LLCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_LL;
                v.add( l );
            }
            if( RLCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_RL;
                v.add( l );
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_CT;
                v.add( l );
            }
            if( LTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_LT;
                v.add( l );
            }
            if( RTCrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_RT;
                v.add( l );
            }
            if( LACrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_LA;
                v.add( l );
            }
            if( RACrits[i] == p ) {
                l = new LocationIndex();
                l.Index = i;
                l.Location = Constants.LOC_RA;
                v.add( l );
            }
        }
        return v;
    }

    public int[] FindHeatSinks( boolean DHS, boolean Clan ) {
        // this routine is used mainly by the text and html writers to find the
        // locations of heat sinks specifically.  returns an int[] with the
        // number in each location
        int[] retval = { 0, 0, 0, 0, 0, 0, 0, 0 };
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_HD]++;
            }
            if( CTCrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_CT]++;
            }
            if( LTCrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_LT]++;
            }
            if( RTCrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_RT]++;
            }
            if( LACrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_LA]++;
            }
            if( RACrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_RA]++;
            }
            if( LLCrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_LL]++;
            }
            if( RLCrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_RL]++;
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_CT]++;
            }
            if( LTCrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_LT]++;
            }
            if( RTCrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_RT]++;
            }
            if( LACrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_LA]++;
            }
            if( RACrits[i] instanceof HeatSink ) {
                retval[Constants.LOC_RA]++;
            }
        }

        if( DHS ) {
            if( Clan ) {
                retval[Constants.LOC_HD] /= 2;
                retval[Constants.LOC_CT] /= 2;
                retval[Constants.LOC_LT] /= 2;
                retval[Constants.LOC_RT] /= 2;
                retval[Constants.LOC_LA] /= 2;
                retval[Constants.LOC_RA] /= 2;
                retval[Constants.LOC_LL] /= 2;
                retval[Constants.LOC_RL] /= 2;
            } else {
                retval[Constants.LOC_HD] /= 3;
                retval[Constants.LOC_CT] /= 3;
                retval[Constants.LOC_LT] /= 3;
                retval[Constants.LOC_RT] /= 3;
                retval[Constants.LOC_LA] /= 3;
                retval[Constants.LOC_RA] /= 3;
                retval[Constants.LOC_LL] /= 3;
                retval[Constants.LOC_RL] /= 3;
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
                retval[Constants.LOC_HD]++;
            }
            if( CTCrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_CT]++;
            }
            if( LTCrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_LT]++;
            }
            if( RTCrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_RT]++;
            }
            if( LACrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_LA]++;
            }
            if( RACrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_RA]++;
            }
            if( LLCrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_LL]++;
            }
            if( RLCrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_RL]++;
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_CT]++;
            }
            if( LTCrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_LT]++;
            }
            if( RTCrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_RT]++;
            }
            if( LACrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_LA]++;
            }
            if( RACrits[i] instanceof JumpJet ) {
                retval[Constants.LOC_RA]++;
            }
        }

        if( IJJ ) {
            retval[Constants.LOC_HD] /= 2;
            retval[Constants.LOC_CT] /= 2;
            retval[Constants.LOC_LT] /= 2;
            retval[Constants.LOC_RT] /= 2;
            retval[Constants.LOC_LA] /= 2;
            retval[Constants.LOC_RA] /= 2;
            retval[Constants.LOC_LL] /= 2;
            retval[Constants.LOC_RL] /= 2;
        }

        return retval;
    }

    public int[] FindModularArmor() {
        // this routine is used to find where modular armor is located for
        // reporting and BV purposes
        int[] retval = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        for( int i = 0; i < 6; i++ ) {
            if( HDCrits[i] instanceof ModularArmor ) {
                retval[Constants.LOC_HD]++;
            }
            if( CTCrits[i] instanceof ModularArmor ) {
                if( CTCrits[i].IsMountedRear() ) {
                    retval[Constants.LOC_CTR]++;
                } else {
                    retval[Constants.LOC_CT]++;
                }
            }
            if( LTCrits[i] instanceof ModularArmor ) {
                if( LTCrits[i].IsMountedRear() ) {
                    retval[Constants.LOC_LTR]++;
                } else {
                    retval[Constants.LOC_LT]++;
                }
            }
            if( RTCrits[i] instanceof ModularArmor ) {
                if( RTCrits[i].IsMountedRear() ) {
                    retval[Constants.LOC_RTR]++;
                } else {
                    retval[Constants.LOC_RT]++;
                }
            }
            if( LACrits[i] instanceof ModularArmor ) {
                retval[Constants.LOC_LA]++;
            }
            if( RACrits[i] instanceof ModularArmor ) {
                retval[Constants.LOC_RA]++;
            }
            if( LLCrits[i] instanceof ModularArmor ) {
                retval[Constants.LOC_LL]++;
            }
            if( RLCrits[i] instanceof ModularArmor ) {
                retval[Constants.LOC_RL]++;
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] instanceof ModularArmor ) {
                if( CTCrits[i].IsMountedRear() ) {
                    retval[Constants.LOC_CTR]++;
                } else {
                    retval[Constants.LOC_CT]++;
                }
            }
            if( LTCrits[i] instanceof ModularArmor ) {
                if( LTCrits[i].IsMountedRear() ) {
                    retval[Constants.LOC_LTR]++;
                } else {
                    retval[Constants.LOC_LT]++;
                }
            }
            if( RTCrits[i] instanceof ModularArmor ) {
                if( RTCrits[i].IsMountedRear() ) {
                    retval[Constants.LOC_RTR]++;
                } else {
                    retval[Constants.LOC_RT]++;
                }
            }
            if( LACrits[i] instanceof ModularArmor ) {
                retval[Constants.LOC_LA]++;
            }
            if( RACrits[i] instanceof ModularArmor ) {
                retval[Constants.LOC_RA]++;
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
                    retval[Constants.LOC_HD]++;
                }
            } else if( HDCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) HDCrits[i]).IsExplosive() ){
                    retval[Constants.LOC_HD]++;
                }
            }
            if( CTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) CTCrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_CT]++;
                }
            } else if( CTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) CTCrits[i]).IsExplosive() ){
                    retval[Constants.LOC_CT]++;
                }
            }
            if( LTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) LTCrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_LT]++;
                }
            } else if( LTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) LTCrits[i]).IsExplosive() ){
                    retval[Constants.LOC_LT]++;
                }
            }
            if( RTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) RTCrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_RT]++;
                }
            } else if( RTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) RTCrits[i]).IsExplosive() ){
                    retval[Constants.LOC_RT]++;
                }
            }
            if( LACrits[i] instanceof Ammunition ) {
                if( ((Ammunition) LACrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_LA]++;
                }
            } else if( LACrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) LACrits[i]).IsExplosive() ){
                    retval[Constants.LOC_LA]++;
                }
            }
            if( RACrits[i] instanceof Ammunition ) {
                if( ((Ammunition) RACrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_RA]++;
                }
            } else if( RACrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) RACrits[i]).IsExplosive() ){
                    retval[Constants.LOC_RA]++;
                }
            }
            if( LLCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) LLCrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_LL]++;
                }
            } else if( LLCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) LLCrits[i]).IsExplosive() ){
                    retval[Constants.LOC_LL]++;
                }
            }
            if( RLCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) RLCrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_RL]++;
                }
            } else if( RLCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) RLCrits[i]).IsExplosive() ){
                    retval[Constants.LOC_RL]++;
                }
            }
        }

        for( int i = 6; i < 12; i++ ) {
            if( CTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) CTCrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_CT]++;
                }
            } else if( CTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) CTCrits[i]).IsExplosive() ){
                    retval[Constants.LOC_CT]++;
                }
            }
            if( LTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) LTCrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_LT]++;
                }
            } else if( LTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) LTCrits[i]).IsExplosive() ){
                    retval[Constants.LOC_LT]++;
                }
            }
            if( RTCrits[i] instanceof Ammunition ) {
                if( ((Ammunition) RTCrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_RT]++;
                }
            } else if( RTCrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) RTCrits[i]).IsExplosive() ){
                    retval[Constants.LOC_RT]++;
                }
            }
            if( LACrits[i] instanceof Ammunition ) {
                if( ((Ammunition) LACrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_LA]++;
                }
            } else if( LACrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) LACrits[i]).IsExplosive() ){
                    retval[Constants.LOC_LA]++;
                }
            }
            if( RACrits[i] instanceof Ammunition ) {
                if( ((Ammunition) RACrits[i]).IsExplosive() ) {
                    retval[Constants.LOC_RA]++;
                }
            } else if( RACrits[i] instanceof ifWeapon ) {
                if( ((ifWeapon) RACrits[i]).IsExplosive() ){
                    retval[Constants.LOC_RA]++;
                }
            }
        }

        return retval;
    }

    public void FlushIllegal( int Era, int Year, boolean Restrict ) {
        // since most everything else is taken care of during mech recalculates,
        // this method is provided for non-core equipment
        AvailableCode AC;
        abPlaceable p;

        // see if there's anything to flush out
        if( NonCore.size() <= 0 ) { return; }

        for( int i = NonCore.size() - 1; i >= 0; i-- ) {
            p = (abPlaceable) NonCore.get( i );
            AC = p.GetAvailability();

            if( Restrict ) {
                if( AC.WentExtinct() ) {
                    if( AC.WasReIntroduced() ) {
                        if( Year < AC.GetIntroDate() || ( Year >= AC.GetExtinctDate() && Year < AC.GetReIntroDate() ) ) {
                            Remove( p );
                        }
                    } else {
                        if( Year < AC.GetIntroDate() || Year >= AC.GetExtinctDate() ) {
                            Remove( p );
                        }
                    }
                } else {
                    if( Year < AC.GetIntroDate() ) {
                        Remove( p );
                    }
                }
            } else {
                switch( Era ) {
                case Constants.STAR_LEAGUE:
                    if( AC.GetSLCode() == 'X' ) {
                        Remove( p );
                    }
                    break;
                case Constants.SUCCESSION:
                    if( AC.GetSWCode() >= 'F' ) {
                        Remove( p );
                    }
                    break;
                case Constants.CLAN_INVASION:
                    if( AC.GetCICode() == 'X' ) {
                        Remove( p );
                    }
                    break;
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
        if( ! Queue.contains(p) &! ( p instanceof ArtemisIVFCS ) &! ( p instanceof PPCCapacitor ) ) {
            if( p instanceof BallisticWeapon ) {
                if( ! ((BallisticWeapon) p).IsInArray() ) {
                    AddToQueue( p );
                }
            } else {
                AddToQueue( p );
            }
        }

        // is the item was mounted rear, set it to normal
        if( p.IsMountedRear() ) {
            p.MountRear( false );
        }

        // if the item is a Missile Weapon, check for artemis and unallocate
        if( p instanceof MissileWeapon ) {
            if( ((MissileWeapon) p).IsUsingArtemis() ) {
                UnallocateAll( ((MissileWeapon) p).GetArtemis(), true );
            }
        }

        // handler for PPC Capacitors
        if( p instanceof EnergyWeapon ) {
            if( ((EnergyWeapon) p).HasCapacitor() ) {
                UnallocateAll( ((EnergyWeapon) p).GetCapacitor(), true );
            }
        }

        // if the item is an MG Array, check for it's MGs and unallocate
        if( p instanceof MGArray ) {
            for( int i = 0; i < ((MGArray) p).GetMGs().length; i++ ) {
                UnallocateAll( ((MGArray) p).GetMGs()[i], true );
            }
        }

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
            if( ! Queue.contains( p ) ) {
                AddToQueue( p );
            }
        }

        // is the item was mounted rear, set it to normal
        if( p.IsMountedRear() ) {
            p.MountRear( false );
        }

        // if the item is a Missile Weapon, check for artemis and unallocate
        if( p instanceof MissileWeapon ) {
            if( ((MissileWeapon) p).IsUsingArtemis() ) {
                UnallocateAll( ((MissileWeapon) p).GetArtemis(), true );
            }
        }

        // handler for PPC Capacitors
        if( p instanceof EnergyWeapon ) {
            if( ((EnergyWeapon) p).HasCapacitor() ) {
                UnallocateAll( ((EnergyWeapon) p).GetCapacitor(), true );
            }
        }

        // if the item is an MG Array, check for it's MGs and unallocate
        if( p instanceof MGArray ) {
            for( int i = 0; i < ((MGArray) p).GetMGs().length; i++ ) {
                UnallocateAll( ((MGArray) p).GetMGs()[i], true );
            }
        }
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
                AddToLA( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                AddToLT( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                AddToCT( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                AddToRT( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                AddToRA( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                AddToRL( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                AddToLL( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
                AddToHD( p );
                if( p.NumPlaced() >= p.NumCrits() ) { break; }
                if( FreeCrits() <= 0 ) { break; }
            } catch ( Exception e ) {
                // just move on.  we probably couldn't allocate it to that location.
            }
        }
    }

    public void SplitAllocate( abPlaceable p, int FirstLoc, int FirstIndex, int SecondLoc ) throws Exception {
        // supports splitting locations for large items.
        int NumSecond = 0, SecondIndex = 0;

        switch( FirstLoc ) {
        case Constants.LOC_CT:
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
                case Constants.LOC_LT:
                    SecondIndex = FirstFree( LTCrits );
                    AddTo( LTCrits, p, SecondIndex, NumSecond );
                    break;
                case Constants.LOC_RT:
                    SecondIndex = FirstFree( RTCrits );
                    AddTo( RTCrits, p, SecondIndex, NumSecond );
                    break;
                }
            } catch( ArrayIndexOutOfBoundsException a ) {
                UnallocateAll( p, true );
                throw new Exception( p.GetCritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case Constants.LOC_LT:
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
                case Constants.LOC_CT:
                    SecondIndex = FirstFree( CTCrits );
                    AddTo( CTCrits, p, SecondIndex, NumSecond );
                    break;
                case Constants.LOC_LA:
                    SecondIndex = FirstFree( LACrits );
                    AddTo( LACrits, p, SecondIndex, NumSecond );
                    break;
                case Constants.LOC_LL:
                    SecondIndex = FirstFree( LLCrits );
                    AddTo( LLCrits, p, SecondIndex, NumSecond );
                    break;
                }
            } catch( ArrayIndexOutOfBoundsException a ) {
                UnallocateAll( p, true );
                throw new Exception( p.GetCritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case Constants.LOC_RT:
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
                case Constants.LOC_CT:
                    SecondIndex = FirstFree( CTCrits );
                    AddTo( CTCrits, p, SecondIndex, NumSecond );
                    break;
                case Constants.LOC_RA:
                    SecondIndex = FirstFree( RACrits );
                    AddTo( RACrits, p, SecondIndex, NumSecond );
                    break;
                case Constants.LOC_RL:
                    SecondIndex = FirstFree( RLCrits );
                    AddTo( RLCrits, p, SecondIndex, NumSecond );
                    break;
                }
            } catch( ArrayIndexOutOfBoundsException a ) {
                UnallocateAll( p, true );
                throw new Exception( p.GetCritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case Constants.LOC_LA:
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
                throw new Exception( p.GetCritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case Constants.LOC_RA:
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
                throw new Exception( p.GetCritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case Constants.LOC_LL:
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
                throw new Exception( p.GetCritName() + " cannot be allocated because there is not enough space." );
            } catch( Exception e ) {
                UnallocateAll( p, true );
                throw e;
            }
            break;
        case Constants.LOC_RL:
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
                throw new Exception( p.GetCritName() + " cannot be allocated because there is not enough space." );
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
        int temp = 0;

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
            throw new Exception( p.GetCritName() + " cannot be allocated because there is not enough space." );
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

        for( int i = index; i < Loc.length; i++ ) {
            if( Loc[i].LocationLinked() || Loc[i].LocationLocked() ) {
                // found the last in this contiguous block
                return result;
            } else {
                result ++;
            }
        }

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
                    }
                }
            }
        }
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

        // Let's get a snapshot of the CT so we can reset it if we have to.
        abPlaceable SnapShot[] = Loc.clone();

        // we have to accomodate for Artemis IV systems
        boolean AddIn = false;
        boolean ArrayGood = false;

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
                        throw new Exception( p.GetCritName() + " cannot be" +
                            " allocated to that location because a non-movable" +
                            " item already exists there." );
                    }
                }

                // check to see if we have space for the artemis system
                if( p instanceof MissileWeapon ) {
                    if( ((MissileWeapon) p).IsUsingArtemis() ) {
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
                } else if( p instanceof EnergyWeapon ) {
                    if( ((EnergyWeapon) p).HasCapacitor() ) {
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
                            UnallocateAll( Loc[i], false );
                        } else {
                            UnallocateByIndex( i, Loc );
                        }
                    }
                    // finally, allocate the item slot
                    Loc[i] = p;
                }

                // add in the artemis system if this is a missile weapon and is
                // usiung it.  we've already checked for a good location.
                if( p instanceof MissileWeapon ) {
                    if( ((MissileWeapon) p).IsUsingArtemis() ) {
                        if( Loc[AddInLoc] != NoItem ) {
                            // we've already ensured that it is not location locked
                            // above, so put the item back into the queue.
                            if( Loc[i].CanSplit() && Loc[i].Contiguous() ) {
                                UnallocateAll( Loc[i], false );
                            } else {
                                UnallocateByIndex( AddInLoc, Loc  );
                            }
                        }
                        Loc[AddInLoc] = ((MissileWeapon) p).GetArtemis();
                    }
                }
                // add in any PPC capacitor
                if( p instanceof EnergyWeapon ) {
                    if( ((EnergyWeapon) p).HasCapacitor() ) {
                        if( Loc[AddInLoc] != NoItem ) {
                            // we've already ensured that it is not location locked
                            // above, so put the item back into the queue.
                            if( Loc[i].CanSplit() && Loc[i].Contiguous() ) {
                                UnallocateAll( Loc[i], false );
                            } else {
                                UnallocateByIndex( AddInLoc, Loc  );
                            }
                        }
                        Loc[AddInLoc] = ((EnergyWeapon) p).GetCapacitor();
                    }
                }

                // add in the machine guns if this is an array
                if( p instanceof MGArray ) {
                    for( i = 0; i < ((MGArray) p).GetNumMGs(); i++ ) {
                        if( Loc[MGLocs[i]] != NoItem ) {
                            // we know it's not location locked, so kick it out
                            if( Loc[MGLocs[i]].CanSplit() && Loc[MGLocs[i]].Contiguous() ) {
                                UnallocateAll( Loc[MGLocs[i]], false );
                            } else {
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
                        throw new Exception( p.GetCritName() + " cannot be" +
                            " allocated to that location because a non-movable" +
                            " item already exists there." );
                    }

                    // if there is an item there, put it back in the queue
                    if( Loc[SIndex] != NoItem ) {
                        // put the item back into the queue
                        if( Loc[SIndex].CanSplit() && Loc[SIndex].Contiguous() ) {
                            UnallocateAll( Loc[SIndex], false );
                        } else {
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
        } catch ( ArrayIndexOutOfBoundsException e ) {
            // reset the location
            Loc = SnapShot;

            // tell the user what happened.
            if( p instanceof MissileWeapon ) {
                if( AddIn ) {
                    if( ArrayGood ) {
                        throw new Exception( p.GetCritName() + " cannot be allocated because\nthere is no room for its machine guns." );
                    } else {
                        throw new Exception( p.GetCritName() + " cannot be allocated because there is not enough space." );
                    }
                } else {
                    throw new Exception( p.GetCritName() + " cannot be allocated because\nthere is no room for its Artemis IV FCS." );
                }
            } else {
                throw new Exception( p.GetCritName() + " cannot be allocated because there is not enough space." );
            }
        }
    }

    public int UnplacedCrits() {
        // returns the number of unplaced criticals in the loadout
        int Result = 0;
        abPlaceable p;
        for( int i = Queue.size() - 1; i >= 0; i-- ) {
            p = (abPlaceable) Queue.get( i );
            Result += ( p.NumCrits() - p.NumPlaced() );

            // special handler for Artemis IV FCS
            if( p instanceof MissileWeapon ) {
                if( ((MissileWeapon) p).IsUsingArtemis() ) {
                    Result++;
                }
            }

            // special handler for PPC Capacitors
            if( p instanceof EnergyWeapon ) {
                if( ((EnergyWeapon) p).HasCapacitor() ) {
                    Result++;
                }
            }

            // special handler for MG Arrays
            if( p instanceof MGArray ) {
                Result += ((MGArray) p).GetNumMGs();
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
    }

    public ifLoadout Clone() {
        // Returns a clone of this loadout.  Normally used for omnimechs.
        ifLoadout clone = new BipedLoadout( "", Owner, HeatSinks.GetNumHS(),
            HeatSinks, Jumps );
        clone.SetRulesLevel( RulesLevel );
        clone.SetHDCrits( HDCrits.clone() );
        clone.SetCTCrits( CTCrits.clone() );
        clone.SetLTCrits( LTCrits.clone() );
        clone.SetRTCrits( RTCrits.clone() );
        clone.SetLACrits( LACrits.clone() );
        clone.SetRACrits( RACrits.clone() );
        clone.SetRLCrits( RLCrits.clone() );
        clone.SetLLCrits( LLCrits.clone() );
        try {
            // we'll need to check each missile launcher
            for( int i = 0; i < Equipment.size(); i++ ) {
                abPlaceable p = (abPlaceable) Equipment.get( i );
                if( p instanceof MissileWeapon ) {
                    if( ((MissileWeapon) p).IsUsingArtemis() ) {
                        switch( ((MissileWeapon) p).GetArtemisType() ) {
                        case Constants.ART4_SRM:
                            clone.SetA4FCSSRM( A4FCS_SRM );
                            break;
                        case Constants.ART4_LRM:
                            clone.SetA4FCSLRM( A4FCS_LRM );
                            break;
                        case Constants.ART4_MML:
                            clone.SetA4FCSMML( A4FCS_MML );
                            break;
                        }
                    }
                }
            }
        } catch( Exception e ) {
            // there shouldn't get any exceptions here since the non-core list
            // will be empty.  Write an event to stderr
            System.err.println( "Could not set Artemis IV for an empty loadout." );
        }
        if( IsAllocated( CTCase ) ) {
            clone.SetCTCASE( CTCase );
            clone.AddCTCASE();
        }
        if( IsAllocated( LTCase ) ) {
            clone.SetLTCASE( LTCase );
            clone.AddLTCASE();
        }
        if( IsAllocated( RTCase ) ) {
            clone.SetRTCASE( RTCase );
            clone.AddRTCASE();
        }
        if( NonCore.size() > 0 ) {
            // have to move the none-core items
            clone.SetNonCore( (Vector) NonCore.clone() );
        }
        if( TCList.size() > 0 ) {
            clone.SetTCList( (Vector) TCList.clone() );
        }
        if( Equipment.size() > 0 ) {
            clone.SetEquipment( (Vector) Equipment.clone() );
        }
        if( Owner.IsOmnimech() ) {
            // before we unallocate the actuators, we need to make sure that
            // there are no physical weapons located there.
            for( int i = 0; i < NonCore.size(); i++ ) {
                if( NonCore.get( i ) instanceof PhysicalWeapon ) {
                    if( Find( (abPlaceable) NonCore.get( i ) ) == Constants.LOC_RA ) {
                        clone.GetActuators().SetLockedRight( true );
                    }
                    if( Find( (abPlaceable) NonCore.get( i ) ) == Constants.LOC_LA ) {
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

    public void SetBaseLoadout( ifLoadout l ) {
        BaseLoadout = l;
    }

    public ifLoadout GetBaseLoadout() {
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

    public void SetNonCore( Vector v ) {
        NonCore = v;
    }

    public void SetTCList( Vector v ) {
        TCList = v;
    }

    public void SetEquipment( Vector v ) {
        Equipment = v;
    }

    public boolean AddCTCASE() {
        // adds CASE equipment to the CT
        if( Owner.IsClan() ) { return false; }
        if( HasCTCASE() ) { return true; }
        boolean placed = false;
        int increment = 11;
        while( placed == false ) {
            if ( increment < 0 ) { return false; }
            try {
                AddToCT( CTCase, increment );
                increment--;
                placed = true;
            } catch ( Exception e ) {
                increment--;
            }
        }
        // must have worked
        return true;
    }

    public boolean AddLTCASE() {
        // adds CASE equipment to the LT
        if( Owner.IsClan() ) { return false; }
        if( HasLTCASE() ) { return true; }
        boolean placed = false;
        int increment = 11;
        while( placed == false ) {
            if ( increment < 0 ) { return false; }
            try {
                AddToLT( LTCase, increment );
                increment--;
                placed = true;
            } catch ( Exception e ) {
                increment--;
            }
        }
        // must have worked
        return true;
    }

    public boolean AddRTCASE() {
        // adds CASE equipment to the RT
        if( Owner.IsClan() ) { return false; }
        if( HasRTCASE() ) { return true; }
        boolean placed = false;
        int increment = 11;
        while( placed == false ) {
            if ( increment < 0 ) { return false; }
            try {
                AddToRT( RTCase, increment );
                increment--;
                placed = true;
            } catch ( Exception e ) {
                increment--;
            }
        }
        // must have worked
        return true;
    }

    public void SetCTCASE( ISCASE c ) {
        CTCase = c;
    }

    public void SetLTCASE( ISCASE c ) {
        LTCase = c;
    }

    public void SetRTCASE( ISCASE c ) {
        RTCase = c;
    }

    public void RemoveCTCASE() {
        Remove( CTCase );
    }

    public void RemoveLTCASE() {
        Remove( LTCase );
    }

    public void RemoveRTCASE() {
        Remove( RTCase );
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

    public ISCASE GetCTCase() {
        return CTCase;
    }

    public ISCASE GetLTCase() {
        return LTCase;
    }

    public ISCASE GetRTCase() {
        return RTCase;
    }

    // handlers for Artemis IV operations.
    public void SetA4FCSSRM( boolean b ) throws Exception {
        if( b != A4FCS_SRM ) {
            A4FCS_SRM = b;
            VArtemisLoader k = new VArtemisLoader();
            Owner.Visit( k );
            if( k.GetResult() == false ) {
                A4FCS_SRM = ( ! b );
                if( A4FCS_SRM ) {
                    throw new Exception( "Could not disable Artemis IV for SRMs because a\nlocked launcher is using an Artemis system." );
                } else {
                    throw new Exception( "Could not enable Artemis IV for LRMs because a locked\nlauncher did not have space for it's Artemis system." );
                }
            }
        }
    }

    public void SetA4FCSLRM( boolean b ) throws Exception {
        if( b != A4FCS_LRM ) {
            A4FCS_LRM = b;
            VArtemisLoader k = new VArtemisLoader();
            Owner.Visit( k );
            if( k.GetResult() == false ) {
                A4FCS_LRM = ( ! b );
                if( A4FCS_LRM ) {
                    throw new Exception( "Could not disable Artemis IV for LRMs because a\nlocked launcher is using an Artemis system." );
                } else {
                    throw new Exception( "Could not enable Artemis IV for LRMs because a locked\nlauncher did not have space for it's Artemis system." );
                }
            }
        }
    }

    public void SetA4FCSMML( boolean b ) throws Exception {
        if( b != A4FCS_MML ) {
            A4FCS_MML = b;
            VArtemisLoader k = new VArtemisLoader();
            Owner.Visit( k );
            if( k.GetResult() == false ) {
                A4FCS_MML = ( ! b );
                if( A4FCS_MML ) {
                    throw new Exception( "Could not disable Artemis IV for MMLs because a\nlocked launcher is using an Artemis system." );
                } else {
                    throw new Exception( "Could not enable Artemis IV for MMLs because a locked\nlauncher did not have space for it's Artemis system." );
                }
            }
        }
    }

    public boolean UsingA4SRM() {
        return A4FCS_SRM;
    }

    public boolean UsingA4LRM() {
        return A4FCS_LRM;
    }

    public boolean UsingA4MML() {
        return A4FCS_MML;
    }

    public boolean UsingTC() {
        return Use_TC;
    }

    public TargetingComputer GetTC() {
        return CurTC;
    }

    public void UseTC( boolean use ) {
        if( use == Use_TC ) {
            return;
        } else {
            Use_TC = use;
        }

        CheckTC();
    }

    public void CheckTC() {
        // this routine checks to see if the targeting computer can be allocated
        // and does so if needed.  It will also remove the TC if it has to.
        if( ! Use_TC ) {
            // remove the TC from the loadout
            Remove( CurTC );
            return;
        }

        if( ! GetQueue().contains( CurTC ) ) {
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
    }

    public void UnallocateTC() {
        // unallocates the TC from the loadout and then performs a TC check
        Remove( CurTC );
        CheckTC();
    }

    public PowerAmplifier GetPowerAmplifier() {
        return PowerAmp;
    }

    public void CheckExclusions( abPlaceable p ) throws Exception {
        // this checks all the items in the loadout vs. the placeable's exclusions
        // not worried about a return value since we're tossing exceptions
        if( p.GetExclusions() == null ) { return; }
        String[] exclude = p.GetExclusions().GetExclusions();

        for( int i = 0; i < exclude.length; i++ ) {
            // queue first
            for( int j = 0; j < Queue.size(); j++ ) {
                if( ((abPlaceable) Queue.get( j )).GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + ((abPlaceable) Queue.get( j )).GetCritName() );
                }
            }
            // check the loadout proper
            for( int j = 0; j < 6; j++ ) {
                if( HDCrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + HDCrits[j].GetCritName() );
                }
                if( CTCrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + CTCrits[j].GetCritName() );
                }
                if( LTCrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + LTCrits[j].GetCritName() );
                }
                if( RTCrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + RTCrits[j].GetCritName() );
                }
                if( LACrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + LACrits[j].GetCritName() );
                }
                if( RACrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + RACrits[j].GetCritName() );
                }
                if( LLCrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + LLCrits[j].GetCritName() );
                }
                if( RLCrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + RLCrits[j].GetCritName() );
                }
            }
            for( int j = 6; j < 11; j++ ) {
                if( CTCrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + CTCrits[j].GetCritName() );
                }
                if( LTCrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + LTCrits[j].GetCritName() );
                }
                if( RTCrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + RTCrits[j].GetCritName() );
                }
                if( LACrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + LACrits[j].GetCritName() );
                }
                if( RACrits[j].GetCritName().contains( exclude[i] ) ) {
                    throw new Exception( "A mech may not mount an " + p.GetCritName() + " if it\nalready mounts an " + RACrits[j].GetCritName() );
                }
            }
        }
    }

    public void AddMechModifier( MechModifier m ) {
        if( ! MechMods.contains( m ) &! Owner.GetMechMods().contains( m ) ) {
            MechMods.add( m );
        }
    }

    public void RemoveMechMod( MechModifier m ) {
        MechMods.remove( m );
    }

    public Vector GetMechMods() {
        return MechMods;
    }

    @Override
    public String toString() {
        return "Loadout: " + Name;
    }
}

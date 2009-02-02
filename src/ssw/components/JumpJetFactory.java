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

import java.util.LinkedList;
import ssw.states.*;

public class JumpJetFactory {
    // provides a means of getting jump jets into the loadout easily.
    private int NumJJ = 0,
                BaseLoadoutNumJJ = 0;
    private ifLoadout Owner;
    private LinkedList CurrentJumps = new LinkedList();
    private static ifJumpJetFactory ISNJJ = new stJumpJetISNJJ(),
                                    ISIJJ = new stJumpJetISIJJ(),
                                    ISUMU = new stJumpJetISUMU(),
                                    CLNJJ = new stJumpJetCLNJJ(),
                                    CLIJJ = new stJumpJetCLIJJ(),
                                    CLUMU = new stJumpJetCLUMU();
    private ifJumpJetFactory CurConfig = ISNJJ;

    public JumpJetFactory( ifLoadout l ) {
        // the basic constructor
        Owner = l;
        ReCalculate();
    }

    public JumpJetFactory( ifLoadout l, JumpJetFactory PlacedJumps ) {
        // this constructor is used for cloning purposes.  since we're only
        // going to do this with Omnimechs, there is no need to recalculate.
        Owner = l;
        BaseLoadoutNumJJ = PlacedJumps.NumJJ;
        NumJJ = BaseLoadoutNumJJ;

        if( PlacedJumps.IsImproved() ) {
            SetImproved();
        } else if( PlacedJumps.IsUMU() ) {
            SetUMU();
        } else {
            SetNormal();
        }

        if( BaseLoadoutNumJJ > 0 ) {
            JumpJet[] j = PlacedJumps.GetPlacedJumps();
            for( int i = 0; i < j.length; i++ ) {
                CurrentJumps.add( j[i] );
            }
        }
    }

    public void SetInnerSphere() {
        if( CurConfig.IsImproved() ) {
            CurConfig = ISIJJ;
        } else if( CurConfig.IsUMU() ) {
            CurConfig = ISUMU;
        } else {
            CurConfig = ISNJJ;
        }
    }

    public void SetClan() {
        if( CurConfig.IsImproved() ) {
            CurConfig = CLIJJ;
        } else if( CurConfig.IsUMU() ) {
            CurConfig = CLUMU;
        } else {
            CurConfig = CLNJJ;
        }
    }

    public void SetNormal() {
        if( Owner.GetMech().IsClan() ) {
            CurConfig = CLNJJ;
        } else {
            CurConfig = ISNJJ;
        }
    }

    public void SetImproved() {
        if( Owner.GetMech().IsClan() ) {
            CurConfig = CLIJJ;
        } else {
            CurConfig = ISIJJ;
        }
    }

    public void SetUMU() {
        if( Owner.GetMech().IsClan() ) {
            CurConfig = CLUMU;
        } else {
            CurConfig = ISUMU;
        }
    }

    public void SetISNormal() {
        CurConfig = ISNJJ;
    }

    public void SetISImproved() {
        CurConfig = ISIJJ;
    }

    public void SetISUMU() {
        CurConfig = ISUMU;
    }

    public void SetCLNormal() {
        CurConfig = CLNJJ;
    }

    public void SetCLImproved() {
        CurConfig = CLIJJ;
    }

    public void SetCLUMU() {
        CurConfig = CLUMU;
    }

    public boolean IsImproved() {
        return CurConfig.IsImproved();
    }

    public boolean IsUMU() {
        return CurConfig.IsUMU();
    }

    public int GetNumJJ() {
        return NumJJ;
    }

    public int GetBaseLoadoutNumJJ() {
        return BaseLoadoutNumJJ;
    }

    public void SetBaseLoadoutNumJJ( int j ) {
        BaseLoadoutNumJJ = j;
    }

    public String GetLookupName() {
        return ((ifState) CurConfig).GetLookupName();
    }

    public void IncrementNumJJ() {
        // can we add a new jump jet?
        if( CanAddJJ() ) {
            JumpJet j = (JumpJet) GetJumpJet();
            j.Place( Owner );
            NumJJ++;
        }
    }

    public boolean DecrementNumJJ() {
        JumpJet j = (JumpJet) CurrentJumps.getLast();
        return RemoveJJ( j );
    }

    public float GetTonnage() {
        // returns the total tonnage of jump jets we have installed
        float result = ( NumJJ * Owner.GetMech().GetJJMult() * CurConfig.GetTonnage() );
        for( int i = 0; i < CurrentJumps.size(); i++ ) {
            result += ((JumpJet) CurrentJumps.get( i )).GetTonnage();
        }
        return result;
    }

    public float GetOmniTonnage() {
        // returns the total tonnage of jump jets we have installed
        return ( ( NumJJ - BaseLoadoutNumJJ ) * Owner.GetMech().GetJJMult() * CurConfig.GetTonnage() );
    }

    public float GetCost() {
        // returns the cost of these jump jets
        if( NumJJ <= 0 ) { return 0.0f; }
        float result = ( CurConfig.GetCost() * ( NumJJ * NumJJ ) * Owner.GetMech().GetTonnage() );
        for( int i = 0; i < CurrentJumps.size(); i++ ) {
            result += ((JumpJet) CurrentJumps.get( i )).GetCost();
        }
        return result;
    }

    public abPlaceable GetJumpJet() {
        JumpJet j = CurConfig.GetJumpJet();
        CurrentJumps.add(j);
        return j;
    }

    public String ReportCrits() {
        return ( NumJJ * CurConfig.GetNumCrits() ) + "";
    }

    private boolean CanAddJJ() {
        if( CurConfig.IsImproved() ) {
            if( NumJJ < Owner.GetMech().GetRunningMP() ) {
                return true;
            } else {
                return false;
            }
        } else {
            if( NumJJ < Owner.GetMech().GetWalkingMP() ) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean RemoveJJ( JumpJet j ) {
        // can we actually remove Jump Jets?
        if( NumJJ <= 0 ) {
            return false;
        }

        // remove the Jump Jet
        j.Remove( Owner );
        CurrentJumps.remove( j );
        NumJJ--;
        return true;
    }

    public void ClearJumpJets() {
        // this routine removes all jump jets and sets the number to 0.
        JumpJet j;
        for( int i = CurrentJumps.size() - 1; i >= 0; i-- ) {
            j = (JumpJet) CurrentJumps.get(i);
            CurrentJumps.remove(j);
            j.Remove( Owner );
        }
        NumJJ = 0;
    }

    public void ReCalculate() {
        // clear the loadout of JumpJets
        JumpJet j;
        for( int i = CurrentJumps.size() - 1; i >= 0; i-- ) {
            j = (JumpJet) CurrentJumps.get(i);
            CurrentJumps.remove(j);
            j.Remove( Owner );
        }

        // see if we can still support the number of jumps we had.
        if( NumJJ > MaxJumps() ) {
            // we can't.
            NumJJ = MaxJumps();
        }

        // Now add the jump jets we need back into the loadout
        for( int i = 0; i < NumJJ; i++ ) {
            j = (JumpJet) GetJumpJet();
            j.Place( Owner );
        }
    }

    public int MaxJumps() {
        // calculates the maximum number of jumps we can have on this mech
        if( CurConfig.IsImproved() ) {
            return Owner.GetMech().GetRunningMP();
        } else {
            return Owner.GetMech().GetWalkingMP();
        }
    }

    public ifState[] GetStates() {
        ifState[] retval = { (ifState) ISNJJ, (ifState) ISIJJ, (ifState) ISUMU, 
                             (ifState) CLNJJ, (ifState) CLIJJ, (ifState) CLUMU };
        return retval;
    }

    public JumpJet[] GetPlacedJumps() {
        // returns the currently placed jumpjets in array form
        JumpJet[] retval = new JumpJet[CurrentJumps.size()];
        for( int i = CurrentJumps.size() - 1; i >= 0; i-- ) {
            retval[i] = (JumpJet) CurrentJumps.get( i );
        }
        return retval;
    }

    public float GetOffensiveBV() {
        float result = 0.0f;
        for( int i = 0; i < CurrentJumps.size(); i++ ) {
            result += ((JumpJet) CurrentJumps.get( i )).GetOffensiveBV();
        }
        return result;
    }

    public float GetDefensiveBV() {
        float result = 0.0f;
        for( int i = 0; i < CurrentJumps.size(); i++ ) {
            result += ((JumpJet) CurrentJumps.get( i )).GetDefensiveBV();
        }
        return result;
    }

    public AvailableCode GetAvailability() {
        return CurConfig.GetAvailability();
    }

    public MechModifier GetMechModifier() {
        return CurConfig.GetMechModifier();
    }

    @Override
    public String toString() {
        return "Jump Jet Factory";
    }
}

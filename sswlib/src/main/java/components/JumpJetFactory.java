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

import java.util.LinkedList;
import states.*;

public class JumpJetFactory {
    // provides a means of getting jump jets into the loadout easily.
    private int NumJJ = 0,
                BaseLoadoutNumJJ = 0;
    private ifMechLoadout Owner;
    private LinkedList CurrentJumps = new LinkedList();
    private static ifJumpJetFactory NJJ = new stJumpJetStandard(),
                                    IJJ = new stJumpJetImproved(),
                                    UMU = new stJumpJetUMU();
    private ifJumpJetFactory CurConfig = NJJ;

    public JumpJetFactory( ifMechLoadout l ) {
        // the basic constructor
        Owner = l;
        ReCalculate();
    }

    public JumpJetFactory( ifMechLoadout l, JumpJetFactory PlacedJumps ) {
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

    public void SetNormal() {
        CurConfig = NJJ;
        Owner.GetMech().SetChanged( true );
    }

    public void SetImproved() {
        CurConfig = IJJ;
        Owner.GetMech().SetChanged( true );
    }

    public void SetUMU() {
        CurConfig = UMU;
        Owner.GetMech().SetChanged( true );
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

    public ifState GetCurrentState() {
        return (ifState) CurConfig;
    }

    public String LookupName() {
        return ((ifState) CurConfig).LookupName();
    }

    public String ChatName() {
        return CurConfig.ChatName();
    }

    public void IncrementNumJJ() {
        // can we add a new jump jet?
        if( CanAddJJ() ) {
            JumpJet j = (JumpJet) GetJumpJet();
            j.Place( Owner );
            NumJJ++;
        }
        Owner.GetMech().SetChanged( true );
    }

    public boolean DecrementNumJJ() {
        JumpJet j = (JumpJet) CurrentJumps.getLast();
        Owner.GetMech().SetChanged( true );
        return RemoveJJ( j );
    }

    public double GetTonnage() {
        // returns the total tonnage of jump jets we have installed
        double result = ( NumJJ * Owner.GetMech().GetJJMult() * CurConfig.GetTonnage() );
        for( int i = 0; i < CurrentJumps.size(); i++ ) {
            result += ((JumpJet) CurrentJumps.get( i )).GetTonnage();
        }
        return result;
    }

    public double GetOmniTonnage() {
        // returns the total tonnage of jump jets we have installed
        return ( ( NumJJ - BaseLoadoutNumJJ ) * Owner.GetMech().GetJJMult() * CurConfig.GetTonnage() );
    }

    public double GetCost() {
        // returns the cost of these jump jets
        if( NumJJ <= 0 ) { return 0.0; }
        double result = ( CurConfig.GetCost() * ( NumJJ * NumJJ ) * Owner.GetMech().GetTonnage() );
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
        Owner.GetMech().SetChanged( true );
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
        Owner.GetMech().SetChanged( true );
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
        ifState[] retval = { (ifState) NJJ, (ifState) IJJ, (ifState) UMU };
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

    public double GetOffensiveBV() {
        double result = 0.0;
        for( int i = 0; i < CurrentJumps.size(); i++ ) {
            result += ((JumpJet) CurrentJumps.get( i )).GetOffensiveBV();
        }
        return result;
    }

    public double GetDefensiveBV() {
        double result = 0.0;
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

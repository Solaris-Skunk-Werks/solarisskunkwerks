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

import ssw.Constants;
import ssw.states.*;

public class Engine extends abPlaceable {
    // An engine for CBT.  Has all engine types for cross-unit building.

    // Declares
    private int EngineRating;
    private boolean FoolLoadoutCT = true;
    private ifEngine CurConfig;
    private final static ifEngine ISICEngine = new stEngineISIC(),
                                      ISFCEngine = new stEngineISFC(),
                                      ISFIEngine = new stEngineISFI(),
                                      ISCFEngine = new stEngineISCF(),
                                      ISFUEngine = new stEngineISFU(),
                                      ISLFEngine = new stEngineISLF(),
                                      ISXLEngine = new stEngineISXL(),
                                      ISXXLEngine = new stEngineISXXL(),
                                      CLICEngine = new stEngineCLIC(),
                                      CLFCEngine = new stEngineCLFC(),
                                      CLFIEngine = new stEngineCLFI(),
                                      CLFUEngine = new stEngineCLFU(),
                                      CLXLEngine = new stEngineCLXL(),
                                      CLXXLEngine = new stEngineCLXXL();
    private Mech Owner;

    // Constructor
    public Engine( Mech m ) {
        // Set it to a 20 rated standard fusion to start.
        EngineRating = 20;
        CurConfig = ISFUEngine;
        Owner = m;
    }

    // Public Methods
    public void SetRating( int rate ) {
        // Set the current values
        EngineRating = rate;
    }

    public void SetISICEngine() {
        CurConfig = ISICEngine;
    }

    public void SetCLICEngine() {
        CurConfig = CLICEngine;
    }

    public void SetISFCEngine() {
        CurConfig = ISFCEngine;
    }

    public void SetCLFCEngine() {
        CurConfig = CLFCEngine;
    }

    public void SetISFIEngine() {
        CurConfig = ISFIEngine;
    }

    public void SetCLFIEngine() {
        CurConfig = CLFIEngine;
    }

    public void SetISFUEngine() {
        CurConfig = ISFUEngine;
    }

    public void SetCLFUEngine() {
        CurConfig = CLFUEngine;
    }

    public void SetISXLEngine() {
        CurConfig = ISXLEngine;
    }

    public void SetCLXLEngine() {
        CurConfig = CLXLEngine;
    }

    public void SetISXXLEngine() {
        CurConfig = ISXXLEngine;
    }

    public void SetCLXXLEngine() {
        CurConfig = CLXXLEngine;
    }

    public void SetISLFEngine() {
        CurConfig = ISLFEngine;
    }

    public void SetISCFEngine() {
        CurConfig = ISCFEngine;
    }

    public boolean IsClan() {
        return CurConfig.IsClan();
    }

    public boolean IsISXL() {
        if( CurConfig == ISXLEngine || CurConfig == ISXXLEngine || CurConfig == CLXXLEngine ) {
            return true;
        } else {
            return false;
        }
    }

    public float GetTonnage() {
        if( IsArmored() ) {
            return CurConfig.GetTonnage( EngineRating ) + ReportCrits() * 0.5f;
        } else {
            return CurConfig.GetTonnage( EngineRating );
        }
    }

    public int GetCTCrits() {
        return CurConfig.GetCTCrits();
    }

    public int GetSideTorsoCrits() {
        return CurConfig.GetSideTorsoCrits();
    }

    public boolean CanSupportRating( int rate ) {
        return CurConfig.CanSupportRating( rate );
    }

    public int NumCrits() {
        // This method fools the loadout, since sometimes we're placing the
        // engine four times in three different locations.  No other item has
        // to act this way.
        if( FoolLoadoutCT ) {
            return GetCTCrits();
        } else {
            return GetSideTorsoCrits();
        }
    }

    public String GetCritName() {
        return CurConfig.GetCritName();
    }

    public String GetLookupName() {
        return ((ifState) CurConfig).GetLookupName();
    }

    public String GetMMName( boolean UseRear ) {
        return CurConfig.GetMMName();
    }

    public float GetCost() {
        if( IsArmored() ) {
            return CurConfig.GetCost( Owner.GetTonnage(), EngineRating ) + ReportCrits() * 150000.0f;
        } else {
            return CurConfig.GetCost( Owner.GetTonnage(), EngineRating );
        }
    }

    public int FreeHeatSinks() {
        return CurConfig.FreeHeatSinks();
    }

    public int InternalHeatSinks() {
        int result = (int) Math.floor( EngineRating / 25 );
        if( result < 0 ) { result = 0; }
        return result;
    }

    public int GetRating() {
        // this is provided mainly for the Gyro, but can also do some reporting
        return EngineRating;
    }

    public int ReportCrits() {
        // returns the reporting crits of the engine/
        return CurConfig.GetFullCrits();
    }

    public AvailableCode GetAvailability() {
        AvailableCode AC = CurConfig.GetAvailability();
        AvailableCode retval = new AvailableCode( AC.IsClan(), AC.GetTechRating(), AC.GetSLCode(), AC.GetSWCode(), AC.GetCICode(), AC.GetIntroDate(), AC.GetExtinctDate(), AC.GetReIntroDate(), AC.GetIntroFaction(), AC.GetReIntroFaction(), AC.WentExtinct(), AC.WasReIntroduced(), AC.GetRandDStart(), AC.IsPrototype(), AC.GetRandDFaction(), AC.GetRulesLevelBM(), AC.GetRulesLevelIM() );
        if( IsArmored() ) {
            if( AC.IsClan() ) {
                retval.Combine( CLArmoredAC );
            } else {
                retval.Combine( ISArmoredAC );
            }
        }
        return retval;
    }

    public ifState[] GetStates() {
        ifState[] retval = { (ifState) ISICEngine, (ifState) ISFCEngine,
            (ifState) ISFIEngine, (ifState) ISCFEngine, (ifState) ISFUEngine,
            (ifState) ISLFEngine, (ifState) ISXLEngine, (ifState) ISXXLEngine, 
            (ifState) CLICEngine, (ifState) CLFCEngine, (ifState) CLFIEngine,
            (ifState) CLFUEngine, (ifState) CLXLEngine, (ifState) CLXXLEngine };
        return retval;
    }

    @Override
    public boolean Place( ifLoadout l ) {
        // Override the placement method for the superclass.
        // First, ensure we're fooling the loadout for CT placement.
        FoolLoadoutCT = true;

        // Place the first block.  This is the easy one.
        try {
            l.AddToCT( this, 0 );
        } catch ( Exception e ) {
            // something is taking the engine's spot.  Blow out whatever's there
            abPlaceable[] a = l.GetCTCrits();
            for( int i = 0; i < 2; i++ ) {
                l.UnallocateAll( a[i], true );
            }
            try {
                l.AddToCT( this, 0 );
            } catch ( Exception f ) {
                // what?  well, report it.
                return false;
            }
        }

        // Figure out how many times to place this engine in the CT and do it.
        if( CurConfig.NumCTBlocks() == 2 ) {
            // try the first block, used when we have a compact gyro
            try {
                l.AddToCT( this, 5 );
            } catch ( Exception e1 ) {
                // Move on to the next index, which is for standard gyros
                if( ! ( l.GetCTCrits()[5] instanceof Gyro ) ) {
                    // not a gyro there.  blow it out and reinstall
                    abPlaceable[] a = l.GetCTCrits();
                    for( int i = 5; i < NumCrits() + 5; i++ ) {
                        l.UnallocateAll( a[i], true );
                    }
                    try {
                        l.AddToCT( this, 5 );
                    } catch ( Exception f ) {
                        return false;
                    }
                } else {
                    try {
                        l.AddToCT( this, 7 );
                    } catch ( Exception e2 ) {
                        if( ! ( l.GetCTCrits()[7] instanceof Gyro ) ) {
                            // not a gyro there.  blow it out and reinstall
                            abPlaceable[] a = l.GetCTCrits();
                            for( int i = 7; i < NumCrits() + 7; i++ ) {
                                l.UnallocateAll( a[i], true );
                            }
                            try {
                                l.AddToCT( this, 7 );
                            } catch ( Exception f ) {
                                return false;
                            }
                        } else {
                            // Move on to the next index, which is for extra-light gyros
                            try {
                                l.AddToCT( this, 9 );
                            } catch ( Exception e3 ) {
                                // something is taking the engine's spot. Blow it out
                                abPlaceable[] a = l.GetCTCrits();
                                for( int i = 9; i < 12; i++ ) {
                                    l.UnallocateAll( a[i], true );
                                }
                                try {
                                    l.AddToCT( this, 9 );
                                } catch ( Exception f ) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        // CT slots have been placed, time to do the side torsos, if any.
        if( CurConfig.GetSideTorsoCrits() > 0 ) {
            // Fool the loadout
            FoolLoadoutCT = false;
            
            // Allocate the RT engine crits.
            try {
                l.AddToRT( this, 0 );
            } catch ( Exception e ) {
                // no matter what the exception says, this is bad.  Let the mech
                // handle this (probably by completely clearing the loadout).
                abPlaceable[] a = l.GetRTCrits();
                for( int i = 0; i < CurConfig.GetSideTorsoCrits(); i++ ) {
                    l.UnallocateAll( a[i], true );
                }
                try {
                    l.AddToRT( this, 0 );
                } catch ( Exception f ) {
                    return false;
                }
            }

            // Now allocate the LT engine crits
            try {
                l.AddToLT( this, 0 );
            } catch ( Exception e ) {
                // no matter what the exception says, this is bad.  Let the mech
                // handle this (probably by completely clearing the loadout).
                // no matter what the exception says, this is bad.  Let the mech
                // handle this (probably by completely clearing the loadout).
                abPlaceable[] a = l.GetLTCrits();
                for( int i = 0; i < CurConfig.GetSideTorsoCrits(); i++ ) {
                    l.UnallocateAll( a[i], true );
                }
                try {
                    l.AddToLT( this, 0 );
                } catch ( Exception f ) {
                    return false;
                }
            }
        }

        // looks like everything worked out fine
        return true;
    }

    @Override
    public boolean Place( ifLoadout l, LocationIndex[] Locs ) {
        // Override the placement method for the superclass.
        // First, ensure we're fooling the loadout for CT placement.
        FoolLoadoutCT = true;

        // Place the first block.  This is the easy one.
        try {
            l.AddToCT( this, 0 );
        } catch ( Exception e ) {
            // something is taking the engine's spot.  Blow out whatever's there
            abPlaceable[] a = l.GetCTCrits();
            for( int i = 0; i < 2; i++ ) {
                l.UnallocateAll( a[i], true );
            }
            try {
                l.AddToCT( this, 0 );
            } catch ( Exception f ) {
                // what?  well, report it.
                return false;
            }
        }

        // Figure out how many times to place this engine in the CT and do it.
        if( CurConfig.NumCTBlocks() == 2 ) {
            // try the first block, used when we have a compact gyro
            try {
                l.AddToCT( this, 5 );
            } catch ( Exception e1 ) {
                // Move on to the next index, which is for standard gyros
                if( ! ( l.GetCTCrits()[5] instanceof Gyro ) ) {
                    // not a gyro there.  blow it out and reinstall
                    abPlaceable[] a = l.GetCTCrits();
                    for( int i = 5; i < NumCrits() + 5; i++ ) {
                        l.UnallocateAll( a[i], true );
                    }
                    try {
                        l.AddToCT( this, 5 );
                    } catch ( Exception f ) {
                        return false;
                    }
                } else {
                    try {
                        l.AddToCT( this, 7 );
                    } catch ( Exception e2 ) {
                        if( ! ( l.GetCTCrits()[7] instanceof Gyro ) ) {
                            // not a gyro there.  blow it out and reinstall
                            abPlaceable[] a = l.GetCTCrits();
                            for( int i = 7; i < NumCrits() + 7; i++ ) {
                                l.UnallocateAll( a[i], true );
                            }
                            try {
                                l.AddToCT( this, 7 );
                            } catch ( Exception f ) {
                                return false;
                            }
                        } else {
                            // Move on to the next index, which is for extra-light gyros
                            try {
                                l.AddToCT( this, 9 );
                            } catch ( Exception e3 ) {
                                // something is taking the engine's spot. Blow it out
                                abPlaceable[] a = l.GetCTCrits();
                                for( int i = 9; i < 12; i++ ) {
                                    l.UnallocateAll( a[i], true );
                                }
                                try {
                                    l.AddToCT( this, 9 );
                                } catch ( Exception f ) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        // CT slots have been placed, time to do the side torsos, if any.
        if( CurConfig.GetSideTorsoCrits() > 0 ) {
            // Fool the loadout
            FoolLoadoutCT = false;

            // do we have a RT or LT Index?  If not, use the normal method
            int ltindex = -1;
            int rtindex = -1;

            for( int i = 0; i < Locs.length; i++ ) {
                if( Locs[i] != null ) {
                    if( Locs[i].Location == Constants.LOC_LT ) {
                        if( Locs[i].Index >= 0 ) {
                            ltindex = Locs[i].Index;
                        }
                    }
                    if( Locs[i].Location == Constants.LOC_RT ) {
                        if( Locs[i].Index >= 0 ) {
                            rtindex = Locs[i].Index;
                        }
                    }
                }
            }

            if( rtindex == -1 ) { rtindex = 0; }

            // Allocate the RT engine crits.
            try {
                l.AddToRT( this, rtindex );
            } catch ( Exception e ) {
                // no matter what the exception says, this is bad.  Let the mech
                // handle this (probably by completely clearing the loadout).
                abPlaceable[] a = l.GetRTCrits();
                for( int i = 0; i < CurConfig.GetSideTorsoCrits(); i++ ) {
                    l.UnallocateAll( a[i], true );
                }
                try {
                    l.AddToRT( this, 0 );
                } catch ( Exception f ) {
                    return false;
                }
            }

            if( ltindex == -1 ) { ltindex = 0; }

            // Now allocate the LT engine crits
            try {
                l.AddToLT( this, ltindex );
            } catch ( Exception e ) {
                // no matter what the exception says, this is bad.  Let the mech
                // handle this (probably by completely clearing the loadout).
                // no matter what the exception says, this is bad.  Let the mech
                // handle this (probably by completely clearing the loadout).
                abPlaceable[] a = l.GetLTCrits();
                for( int i = 0; i < CurConfig.GetSideTorsoCrits(); i++ ) {
                    l.UnallocateAll( a[i], true );
                }
                try {
                    l.AddToLT( this, 0 );
                } catch ( Exception f ) {
                    return false;
                }
            }
        }

        // looks like everything worked out fine
        return true;
    }

    // Need to override the superclass on these because engines are always
    // location locked once placed.
    @Override
    public boolean LocationLocked() {
        return true;
    }

    public float GetOffensiveBV() {
        return 0.0f;
    }

    public float GetCurOffensiveBV( boolean UseRear ) {
        return 0.0f;
    }

    public float GetDefensiveBV() {
        if( IsArmored() ) {
            return 5.0f * NumCrits();
        }
        return 0.0f;
    }

    // but an engine does have a type multiplier which the internal structure
    // will use to calculate it's own battle value.
    public float GetBVMult() {
        return CurConfig.GetBVMult();
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    public boolean IsFusion() {
        return CurConfig.IsFusion();
    }

    public boolean IsNuclear() {
        return CurConfig.IsNuclear();
    }

    public int MaxMovementHeat() {
        return CurConfig.MaxMovementHeat();
    }

    public int MinimumHeat() {
        return CurConfig.MinimumHeat();
    }

    public int JumpingHeatMultiplier() {
        return CurConfig.JumpingHeatMultiplier();
    }

    @Override
    public MechModifier GetMechModifier() {
        return CurConfig.GetMechModifier();
    }

    @Override
    public String toString() {
        return CurConfig.toString();
    }
}

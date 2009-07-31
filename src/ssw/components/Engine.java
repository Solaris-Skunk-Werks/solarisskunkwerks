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
    private final static ifEngine ICEngine = new stEngineICE(),
                                  PICEngine = new stEnginePrimitiveICE(),
                                  FCEngine = new stEngineFuelCell(),
                                  PFCEngine = new stEnginePrimitiveFuelCell(),
                                  FIEngine = new stEngineFission(),
                                  PFIEngine = new stEnginePrimitiveFission(),
                                  ISCFEngine = new stEngineISCF(),
                                  FUEngine = new stEngineFusion(),
                                  PFUEngine = new stEnginePrimitiveFusion(),
                                  ISLFEngine = new stEngineISLF(),
                                  ISXLEngine = new stEngineISXL(),
                                  ISXXLEngine = new stEngineISXXL(),
                                  CLXLEngine = new stEngineCLXL(),
                                  CLXXLEngine = new stEngineCLXXL();
    private Mech Owner;

    // Constructor
    public Engine( Mech m ) {
        // Set it to a 20 rated standard fusion to start.
        EngineRating = 20;
        CurConfig = FUEngine;
        Owner = m;
    }

    // Public Methods
    public void SetRating( int rate ) {
        // Set the current values
        if( CurConfig.IsPrimitive() ) {
            EngineRating = (int) ( Math.floor( ( ( rate * 1.2f ) + 4.5f ) / 5 ) * 5 );
        } else {
            EngineRating = rate;
        }
    }

    public void SetICEngine() {
        CurConfig = ICEngine;
    }

    public void SetFCEngine() {
        CurConfig = FCEngine;
    }

    public void SetFIEngine() {
        CurConfig = FIEngine;
    }

    public void SetFUEngine() {
        CurConfig = FUEngine;
    }

    public void SetPrimitiveICEngine() {
        CurConfig = PICEngine;
    }

    public void SetPrimitiveFCEngine() {
        CurConfig = PFCEngine;
    }

    public void SetPrimitiveFIEngine() {
        CurConfig = PFIEngine;
    }

    public void SetPrimitiveFUEngine() {
        CurConfig = PFUEngine;
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

    public int GetTechBase() {
        return CurConfig.GetAvailability().GetTechBase();
    }

    public ifState GetCurrentState() {
        return (ifState) CurConfig;
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
            return CurConfig.GetCost( Owner.GetTonnage(), EngineRating ) + (float) ReportCrits() * 150000.0f;
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
        AvailableCode retval = CurConfig.GetAvailability().Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        return retval;
    }

    public ifState[] GetStates() {
        ifState[] retval = { 
            (ifState) ICEngine,
            (ifState) PICEngine,
            (ifState) FCEngine,
            (ifState) PFCEngine,
            (ifState) FIEngine,
            (ifState) PFIEngine,
            (ifState) ISCFEngine,
            (ifState) FUEngine,
            (ifState) PFUEngine,
            (ifState) ISLFEngine,
            (ifState) ISXLEngine,
            (ifState) ISXXLEngine,
            (ifState) CLXLEngine,
            (ifState) CLXXLEngine };
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
            // something is taking the engine's spot.
            return false;
        }

        // Figure out how many times to place this engine in the CT and do it.
        if( CurConfig.NumCTBlocks() == 2 ) {
            // try the first block, used when we have a compact gyro
            try {
                l.AddToCT( this, 5 );
            } catch ( Exception e1 ) {
                // Move on to the next index, which is for standard gyros
                if( ! ( l.GetCTCrits()[5] instanceof Gyro ) ) {
                    return false;
                } else {
                    try {
                        l.AddToCT( this, 7 );
                    } catch ( Exception e2 ) {
                        if( ! ( l.GetCTCrits()[7] instanceof Gyro ) ) {
                            return false;
                        } else {
                            // Move on to the next index, which is for extra-light gyros
                            try {
                                l.AddToCT( this, 9 );
                            } catch ( Exception e3 ) {
                                return false;
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
                l.AddTo( this, Constants.LOC_RT, -1 );
            } catch ( Exception e ) {
                return false;
            }

            // Now allocate the LT engine crits
            try {
                l.AddTo( this, Constants.LOC_LT, -1 );
            } catch ( Exception e ) {
                return false;
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
            return false;
        }

        // Figure out how many times to place this engine in the CT and do it.
        if( CurConfig.NumCTBlocks() == 2 ) {
            // try the first block, used when we have a compact gyro
            try {
                l.AddToCT( this, 5 );
            } catch ( Exception e1 ) {
                // Move on to the next index, which is for standard gyros
                if( ! ( l.GetCTCrits()[5] instanceof Gyro ) ) {
                    return false;
                } else {
                    try {
                        l.AddToCT( this, 7 );
                    } catch ( Exception e2 ) {
                        if( ! ( l.GetCTCrits()[7] instanceof Gyro ) ) {
                            return false;
                        } else {
                            // Move on to the next index, which is for extra-light gyros
                            try {
                                l.AddToCT( this, 9 );
                            } catch ( Exception e3 ) {
                                return false;
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

            // Allocate the RT engine crits.
            try {
                l.AddToRT( this, rtindex );
            } catch ( Exception e ) {
                return false;
            }

            // Now allocate the LT engine crits
            try {
                l.AddToLT( this, ltindex );
            } catch ( Exception e ) {
                return false;
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

    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
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

    public int GetBFStructure( int tonnage ) {
        return CurConfig.GetBFStructure( tonnage );
    }

    @Override
    public MechModifier GetMechModifier() {
        return CurConfig.GetMechModifier();
    }

    public boolean IsPrimitive() {
        return CurConfig.IsPrimitive();
    }

    @Override
    public String toString() {
        return CurConfig.toString();
    }
}

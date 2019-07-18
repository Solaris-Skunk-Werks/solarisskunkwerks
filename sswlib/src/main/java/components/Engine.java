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

import common.CommonTools;
import states.*;

public class Engine extends abPlaceable {
    // An engine for CBT.  Has all engine types for cross-unit building.

    // Declares
    private int EngineRating;
    private boolean FoolLoadoutCT = true,
                    UseLargePlacement = false;
    private ifEngine CurConfig;
    private final ifEngine ICEngine = new stEngineICE( this ),
                           PICEngine = new stEnginePrimitiveICE(),
                           FCEngine = new stEngineFuelCell(),
                           PFCEngine = new stEnginePrimitiveFuelCell(),
                           FIEngine = new stEngineFission(),
                           PFIEngine = new stEnginePrimitiveFission(),
                           ISCFEngine = new stEngineISCF(),
                           FUEngine = new stEngineFusion( this ),
                           PFUEngine = new stEnginePrimitiveFusion(),
                           ISLFEngine = new stEngineISLF( this ),
                           ISXLEngine = new stEngineISXL( this ),
                           ISXXLEngine = new stEngineISXXL( this ),
                           CLXLEngine = new stEngineCLXL( this ),
                           CLXXLEngine = new stEngineCLXXL( this ),
                           NAEngine = new stEngineNone( this );
    private ifUnit Owner;

    // Constructor
    public Engine( ifUnit u ) {
        // Set it to a 20 rated standard fusion to start.
        EngineRating = 20;
        if( u.GetUnitType() == AvailableCode.UNIT_BATTLEMECH || u.GetUnitType() == AvailableCode.UNIT_INDUSTRIALMECH ) {
            CurConfig = FUEngine;
        } else if( u.GetUnitType() == AvailableCode.UNIT_COMBATVEHICLE ) {
            CurConfig = ICEngine;
        } else {
            CurConfig = FUEngine;
        }
        Owner = u;
    }

    // Public Methods
    public void SetRating( int rate ) {
        // Set the current values
        if( CurConfig.IsPrimitive() ) {
            EngineRating = (int) ( Math.floor( ( ( rate * 1.2 ) + 4.5 ) / 5 ) * 5 );
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
    
    public void SetNoneEngine() {
        CurConfig = NAEngine;
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

    public double GetTonnage() {
        if( IsArmored() ) {
            return CurConfig.GetTonnage( EngineRating, Owner.UsingFractionalAccounting() ) + ReportCrits() * 0.5;
        } else {
            if( Owner.GetUnitType() == AvailableCode.UNIT_COMBATVEHICLE && IsFusion() ) {
                double retval = CurConfig.GetTonnage( EngineRating, Owner.UsingFractionalAccounting() );
                if( Owner.UsingFractionalAccounting() ) {
                    return retval * 1.5;
                } else {
                    return CommonTools.RoundHalfUp(retval * 1.5);
                }
            } else {
                return CurConfig.GetTonnage( EngineRating, Owner.UsingFractionalAccounting() );
            }
        }
    }

    public int GetCTCrits() {
        return CurConfig.GetCTCrits();
    }

    public int GetSideTorsoCrits() {
        return CurConfig.GetSideTorsoCrits();
    }

    public boolean CanSupportRating( int rate, Mech m ) {
        return CurConfig.CanSupportRating( rate, m );
    }

    public int NumCrits() {
        // This method fools the loadout, since sometimes we're placing the
        // engine four times in three different locations.  No other item has
        // to act this way.
        if( FoolLoadoutCT ) {
            if( UseLargePlacement ) {
                return GetCTCrits() + 2;
            }
            return GetCTCrits();
        } else {
            return GetSideTorsoCrits();
        }
    }

    public int NumCVSpaces() {
        if( EngineRating > 400 ) {
            return CurConfig.LargeCVSpaces();
        } else {
            return CurConfig.NumCVSpaces();
        }
    }

    public String ActualName() {
        return CurConfig.ActualName();
    }

    public String CritName() {
        return CurConfig.CritName();
    }

    public String LookupName() {
        return CurConfig.LookupName();
    }

    public String ChatName() {
        return CurConfig.ChatName();
    }

    public String MegaMekName( boolean UseRear ) {
        return CurConfig.MegaMekName( UseRear );
    }

    public String BookReference() {
        return CurConfig.BookReference();
    }

    public double GetCost() {
        if( IsArmored() ) {
            return CurConfig.GetCost( Owner.GetTonnage(), EngineRating ) + (double) ReportCrits() * 150000.0;
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
        if( EngineRating > 500 ) {
            return CurConfig.GetFullCrits() + 2;
        }
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
            (ifState) CLXXLEngine,
            (ifState) NAEngine};
        return retval;
    }

    @Override
    public boolean Place( ifMechLoadout l ) {
        LocationIndex[] lengine = { null, null };
        return Place( l, lengine );
    }

    @Override
    public boolean Place( ifMechLoadout l, LocationIndex[] Locs ) {
        // Override the placement method for the superclass.
        // First, ensure we're fooling the loadout for CT placement.
        FoolLoadoutCT = true;

        // Place the first block.  This is the easy one.
        try {
            l.AddToCT( this, 0 );
        } catch ( Exception e ) {
            return false;
        }

        // if we are large, tell the loadout so.
        if( EngineRating > 400 ) {
            UseLargePlacement = true;
        }

        // Figure out how many times to place this engine in the CT and do it.
        if( CurConfig.NumCTBlocks() == 2 ) {
            // Let's try the first place we THINK there shouldn't be a Gyro
            try {
                l.AddToCT( this, CurConfig.GetCTCrits() + l.GetMech().GetGyro().NumCrits() );
            } catch ( Exception e99 ) {
                // try the next block, used when we have a compact gyro
                if( ! ( l.GetCTCrits()[3] instanceof Gyro ) ) {
                    return false;
                } else {
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
            }
        }

        // reset the large placement anyway.
        UseLargePlacement = false;

        // CT slots have been placed, time to do the side torsos, if any.
        if( CurConfig.GetSideTorsoCrits() > 0 ) {
            // Fool the loadout
            FoolLoadoutCT = false;

            // do we have a RT or LT Index?  If not, use the normal method
            int ltindex = -1;
            int rtindex = -1;

            for( int i = 0; i < Locs.length; i++ ) {
                if( Locs[i] != null ) {
                    if( Locs[i].Location == LocationIndex.MECH_LOC_LT ) {
                        if( Locs[i].Index >= 0 ) {
                            ltindex = Locs[i].Index;
                        }
                    }
                    if( Locs[i].Location == LocationIndex.MECH_LOC_RT ) {
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

    public double GetOffensiveBV() {
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    public double GetDefensiveBV() {
        if( IsArmored() ) {
            //needs to include ALL crits, not just a single location.
            return 5.0 * ReportCrits();
            //return 5.0 * ( (GetCTCrits()*2) + (GetSideTorsoCrits()*2) );
        }
        return 0.0;
    }

    // but an engine does have a type multiplier which the internal structure
    // will use to calculate it's own battle value.
    public double GetBVMult() {
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

    public boolean IsICE() {
        return ( CurConfig instanceof stEngineICE || CurConfig instanceof stEnginePrimitiveICE );
    }

    public boolean isFuelCell() {
        return ( CurConfig instanceof stEngineFuelCell || CurConfig instanceof stEnginePrimitiveFuelCell );
    }
    
    public boolean isNone() {
        return ( CurConfig instanceof stEngineNone );
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
   
    public boolean IsLarge() {
        if ( EngineRating > 400  ) return true;
        return false;
    }
    
    public boolean RequiresControls() {
        if ( CurConfig instanceof stEngineNone ) return false;
        return true;
    }
}

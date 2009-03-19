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

import ssw.states.*;

public class Gyro extends abPlaceable {
    private Mech Owner;
    private final static ifGyro ClanStandard = new stGyroCLStandard(),
                                ISStandard = new stGyroISStandard(),
                                ISCompact = new stGyroISCompact(),
                                ISHeavy = new stGyroISHeavy(),
                                ISXL = new stGyroISXL();
    private ifGyro CurConfig = ISStandard;

    public Gyro( Mech m ) {
        // Gyros are intimately tied to the engine in the mech, so we need a
        // reference whenever a new Gyro is created.
        Owner = m;
    }
    
    public void SetISStandard() {
        CurConfig = ISStandard;
    }
    
    public void SetCLStandard() {
        CurConfig = ClanStandard;
    }
    
    public void SetISCompact() {
        CurConfig = ISCompact;
    }
    
    public void SetISHeavy() {
        CurConfig = ISHeavy;
    }
    
    public void SetISXL() {
        CurConfig = ISXL;
    }
    
    public boolean IsClan() {
        return CurConfig.IsClan();
    }
    
    @Override
    public boolean Place( ifLoadout l ) {
        // Gyros always start at index 3, right after the first (or only)
        // engine block.  Gyros should be placed before engines.
        try {
            l.AddToCT( this, 3 );
        } catch ( Exception e ) {
            return false;
        }

        return true;
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    public float GetTonnage() {
        if( IsArmored() ) {
            return CurConfig.GetTonnage( Owner.GetEngine().GetRating() ) + ( NumCrits() * 0.5f );
        } else {
            return CurConfig.GetTonnage( Owner.GetEngine().GetRating() );
        }
    }

    public int NumCrits() {
        return CurConfig.GetCrits();
    }
    
    public String GetCritName() {
        return CurConfig.GetCritName();
    }

    public String GetLookupName() {
        return ((ifState) CurConfig).GetLookupName();
    }

    public String GetReportName() {
        return CurConfig.GetReportName();
    }

    public String GetMMName( boolean UseRear ) {
        return CurConfig.GetMMName();
    }

    public float GetCost() {
        if( IsArmored() ) {
            return ( CurConfig.GetTonnage( Owner.GetEngine().GetRating() ) * CurConfig.GetCostMult() ) + ( NumCrits() * 150000.0f );
        } else {
            return GetTonnage() * CurConfig.GetCostMult();
        }
    }

    public float GetOffensiveBV() {
        return 0.0f;
    }

    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return 0.0f;
    }

    public float GetDefensiveBV() {
        if( IsArmored() ) {
            float result = Owner.GetTonnage() * CurConfig.GetBVMult();
            result += result * NumCrits() * 0.05f;
            return result;
        } else {
            return Owner.GetTonnage() * CurConfig.GetBVMult();
        }
    }

    public float GetBVTypeMult() {
        return CurConfig.GetBVMult();
    }

    public ifState[] GetStates() {
        ifState[] retval = { (ifState) ClanStandard, (ifState) ISStandard,
            (ifState) ISCompact, (ifState) ISHeavy, (ifState) ISXL };
        return retval;
    }

    @Override
    public boolean CoreComponent() {
        return true;
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

    @Override
    public MechModifier GetMechModifier() {
        return CurConfig.GetMechModifier();
    }

    @Override
    public String toString() {
        return CurConfig.GetCritName();
    }
}

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

public class InternalStructure extends abPlaceable {
    // The internal structure for a mech.  Uses states to indicate it's current
    // configuration.
    
    // Declares
    private Mech Owner;
    private ifChassis Config;
    private static ifChassis MSBP = new stChassisMSBP(),
                             MSQD = new stChassisMSQD(),
                             ISESBP = new stChassisISESBP(),
                             ISESQD = new stChassisISESQD(),
                             ISCOBP = new stChassisISCOBP(),
                             ISCOQD = new stChassisISCOQD(),
                             ISECBP = new stChassisISECBP(),
                             ISECQD = new stChassisISECQD(),
                             ISPRBP = new stChassisISPRBP(),
                             ISPRQD = new stChassisISPRQD(),
                             REBP = new stChassisREBP(),
                             REQD = new stChassisREQD(),
                             IMBP = new stChassisIMBP(),
                             IMQD = new stChassisIMQD(),
                             CLESBP = new stChassisCLESBP(),
                             CLESQD = new stChassisCLESQD(),
                             CLECBP = new stChassisCLECBP(),
                             CLECQD = new stChassisCLECQD();
    private int Placed = 0;
    
    // Constructor
    public InternalStructure( Mech m ) {
        // We'll assume an Inner Sphere standard military bipedal chassis to start.
        Owner = m;
        Config = MSBP;
    }

    // Public Methods
    public void SetMSBP() {
        // Set this chassis to an Inner Sphere Standard Biped
        Config = MSBP;
    }

    public void SetMSQD() {
        // Set this chassis to an Inner Sphere Standard Quad
        Config = MSQD;
    }

    public void SetISESBP() {
        // Set this chassis to an Inner Sphere Endo Steel Biped
        Config = ISESBP;
    }

    public void SetISESQD() {
        // Set this chassis to an Inner Sphere Endo Steel Quad
        Config = ISESQD;
    }

    public void SetISCOBP() {
        // Set this chassis to an Inner Sphere Composite Biped
        Config = ISCOBP;
    }

    public void SetISCOQD() {
        // Set this chassis to an Inner Sphere Composite Quad
        Config = ISCOQD;
    }

    public void SetISECBP() {
        // Set this chassis to an Inner Sphere Endo-Composite Biped
        Config = ISECBP;
    }

    public void SetISECQD() {
        // Set this chassis to an Inner Sphere Endo-Composite Quad
        Config = ISECQD;
    }

    public void SetISPRBP() {
        // Set this chassis to an Inner Sphere Primitive Biped
        Config = ISPRBP;
    }

    public void SetISPRQD() {
        // Set this chassis to an Inner Sphere Primitive Quad
        Config = ISPRQD;
    }

    public void SetREBP() {
        // Set this chassis to an Inner Sphere Reinforced Biped
        Config = REBP;
    }

    public void SetREQD() {
        // Set this chassis to an Inner Sphere Reinforced Quad
        Config = REQD;
    }

    public void SetIMBP() {
        // Set this chassis to an Inner Sphere Reinforced Biped
        Config = IMBP;
    }

    public void SetIMQD() {
        // Set this chassis to an Inner Sphere Reinforced Quad
        Config = IMQD;
    }

    public void SetCLESBP() {
        // Set this chassis to a Clan Endo Steel Biped
        Config = CLESBP;
    }

    public void SetCLESQD() {
        // Set this chassis to a Clan Endo Steel Quad
        Config = CLESQD;
    }

    public void SetCLECBP() {
        // Set this chassis to a Clan Endo-Composite Biped
        Config = CLECBP;
    }

    public void SetCLECQD() {
        // Set this chassis to a Clan Endo-Composite Quad
        Config = CLECQD;
    }

    public int GetTechBase() {
        return Config.GetAvailability().GetTechBase();
    }

    public ifState GetCurrentState() {
        return (ifState) Config;
    }

    public int NumCrits() {
        return Config.GetCrits();
    }
    
    public String GetCritName() {
        return Config.GetCritName();
    }

    public String GetMMName( boolean UseRear ) {
        return Config.GetMMName();
    }

    public String GetLookupName() {
        return ((ifState) Config).GetLookupName();
    }

    public float GetTonnage() {
        return Config.GetStrucTon( Owner.GetTonnage() );
    }

    public boolean IsQuad() {
        return Config.IsQuad();
    }

    @Override
    public boolean CanArmor() {
        // internal structure is always roll again, so no armoring
        return false;
    }

    public int GetTotalPoints() {
        // returns the total number of internal structure points in the mech
        int retval = 0;

        retval += GetHeadPoints();
        retval += GetCTPoints();
        retval += GetSidePoints() + GetSidePoints();
        retval += GetArmPoints() + GetArmPoints();
        retval += GetLegPoints() + GetLegPoints();

        return retval;
    }

    public int GetHeadPoints() {
        return Config.GetHeadPoints();
    }
    
    public int GetCTPoints() {
        return Config.GetCTPoints( Owner.GetTonnage() );
    }
    
    public int GetSidePoints() {
        return Config.GetSidePoints( Owner.GetTonnage() );
    }
    
    public int GetArmPoints() {
        return Config.GetArmPoints( Owner.GetTonnage() );
    }
    
    public int GetLegPoints() {
        return Config.GetLegPoints( Owner.GetTonnage() );
    }

    public float GetCost() {
        return Config.GetCost( Owner.GetTonnage() );
    }

    public float GetOffensiveBV() {
        // internal strucutre only has a defensive BV
        return 0.0f;
    }

    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        // BV will not change for this item, so just return the normal value
        return 0.0f;
    }

    public float GetDefensiveBV() {
        float result = GetHeadPoints() + GetCTPoints() + ( GetSidePoints() * 2.0f )
                + ( GetArmPoints() * 2.0f ) + ( GetLegPoints() * 2.0f );
        result *= 1.5f;
        result *= GetBVTypeMult();
        result *= Owner.GetEngine().GetBVMult();
        return result;
    }

    public float GetBVTypeMult() {
        return Config.GetBVMult() + Owner.GetTotalModifiers( false, true ).InternalMultiplier();
    }

    public ifState[] GetStates( boolean biped ) {
        ifState[] retval = { null, null, null, null, null, null, null, null };
        if( biped ) {
            retval[0] = (ifState) MSBP;
            retval[1] = (ifState) ISESBP;
            retval[2] = (ifState) CLESBP;
            retval[3] = (ifState) ISCOBP;
            retval[4] = (ifState) ISECBP;
            retval[5] = (ifState) CLECBP;
            retval[6] = (ifState) REBP;
            retval[7] = (ifState) IMBP;
            //retval[11] = (ifState) ISPRBP;
        } else {
            retval[0] = (ifState) MSQD;
            retval[1] = (ifState) ISESQD;
            retval[2] = (ifState) CLESQD;
            retval[3] = (ifState) ISCOQD;
            retval[4] = (ifState) ISECQD;
            retval[5] = (ifState) CLECQD;
            retval[6] = (ifState) REQD;
            retval[7] = (ifState) IMQD;
            //retval[11] = (ifState) ISPRQD;
        }
        return retval;
    }

    public AvailableCode GetAvailability() {
        return Config.GetAvailability();
    }

    @Override
    public int NumPlaced() {
        return Placed;
    }

    @Override
    public void IncrementPlaced() {
        if ( Config.IncrementPlaced() ) {
            Placed++;
        }
    }

    @Override
    public void DecrementPlaced() {
        if( Config.DecrementPlaced() ) {
            Placed--;
        }
    }

    @Override
    public void ResetPlaced() {
        Placed = 0;
    }

    @Override
    public boolean CanSplit() {
        return true;
    }

    @Override
    public boolean Contiguous() {
        return false;
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public boolean IsCritable() {
        return false;
    }

    @Override
    public MechModifier GetMechModifier() {
        return Config.GetMechModifier();
    }

    // toString
    @Override
    public String toString() {
        if( Config.GetCrits() > 0 ) {
            if( Config.GetCrits() > Placed ) {
                return Config.toString() + " (" + ( Config.GetCrits() - Placed ) + ")";
            } else {
                return Config.toString();
            }
        }
        return Config.toString();
    }
}

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
    private static ifChassis ISMSBP = new stChassisISMSBP(),
                             ISMSQD = new stChassisISMSQD(),
                             ISESBP = new stChassisISESBP(),
                             ISESQD = new stChassisISESQD(),
                             ISCOBP = new stChassisISCOBP(),
                             ISCOQD = new stChassisISCOQD(),
                             ISECBP = new stChassisISECBP(),
                             ISECQD = new stChassisISECQD(),
                             ISPRBP = new stChassisISPRBP(),
                             ISPRQD = new stChassisISPRQD(),
                             ISREBP = new stChassisISREBP(),
                             ISREQD = new stChassisISREQD(),
                             ISIMBP = new stChassisISIMBP(),
                             ISIMQD = new stChassisISIMQD(),
                             CLMSBP = new stChassisCLMSBP(),
                             CLMSQD = new stChassisCLMSQD(),
                             CLESBP = new stChassisCLESBP(),
                             CLESQD = new stChassisCLESQD(),
                             CLECBP = new stChassisCLECBP(),
                             CLECQD = new stChassisCLECQD(),
                             CLREBP = new stChassisCLREBP(),
                             CLREQD = new stChassisCLREQD(),
                             CLIMBP = new stChassisCLIMBP(),
                             CLIMQD = new stChassisCLIMQD();
    private int Placed = 0;
    
    // Constructor
    public InternalStructure( Mech m ) {
        // We'll assume an Inner Sphere standard military bipedal chassis to start.
        Owner = m;
        Config = ISMSBP;
    }

    // Public Methods
    public void SetISMSBP() {
        // Set this chassis to an Inner Sphere Standard Biped
        Config = ISMSBP;
    }

    public void SetISMSQD() {
        // Set this chassis to an Inner Sphere Standard Quad
        Config = ISMSQD;
    }

    public void SetISESBP() {
        // Set this chassis to an Inner Sphere Endo Steel Biped
        Config = ISESBP;
    }

    public void SetISESQD() {
        // Set this chassis to an Inner Sphere Endo Steel Quad
        Config = ISESQD;
    }

    public void SetCLMSBP() {
        // Set this chassis to a Clan Standard Biped
        Config = CLMSBP;
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

    public void SetISREBP() {
        // Set this chassis to an Inner Sphere Reinforced Biped
        Config = ISREBP;
    }

    public void SetISREQD() {
        // Set this chassis to an Inner Sphere Reinforced Quad
        Config = ISREQD;
    }

    public void SetISIMBP() {
        // Set this chassis to an Inner Sphere Reinforced Biped
        Config = ISIMBP;
    }

    public void SetISIMQD() {
        // Set this chassis to an Inner Sphere Reinforced Quad
        Config = ISIMQD;
    }

    public void SetCLMSQD() {
        // Set this chassis to a Clan Standard Quad
        Config = CLMSQD;
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

    public void SetCLREBP() {
        // Set this chassis to a Clan Reinforced Biped
        Config = CLREBP;
    }

    public void SetCLREQD() {
        // Set this chassis to a Clan Reinforced Quad
        Config = CLREQD;
    }

    public void SetCLIMBP() {
        // Set this chassis to an Inner Sphere Reinforced Biped
        Config = CLIMBP;
    }

    public void SetCLIMQD() {
        // Set this chassis to an Inner Sphere Reinforced Quad
        Config = CLIMQD;
    }


    public boolean IsClan() {
        return Config.IsClan();
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

    public float GetCurOffensiveBV( boolean UseRear ) {
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
        ifState[] retval = { null, null, null, null, null, null, null, null, null, null, null };
        if( biped ) {
            retval[0] = (ifState) ISMSBP;
            retval[1] = (ifState) ISESBP;
            retval[2] = (ifState) CLMSBP;
            retval[3] = (ifState) CLESBP;
            retval[4] = (ifState) ISCOBP;
            retval[5] = (ifState) ISECBP;
            retval[6] = (ifState) CLECBP;
            retval[7] = (ifState) ISREBP;
            retval[8] = (ifState) CLREBP;
            retval[9] = (ifState) ISIMBP;
            retval[10] = (ifState) CLIMBP;
            //retval[11] = (ifState) ISPRBP;
        } else {
            retval[0] = (ifState) ISMSQD;
            retval[1] = (ifState) ISESQD;
            retval[2] = (ifState) CLMSQD;
            retval[3] = (ifState) CLESQD;
            retval[4] = (ifState) ISCOQD;
            retval[5] = (ifState) ISECQD;
            retval[6] = (ifState) CLECQD;
            retval[7] = (ifState) ISREQD;
            retval[8] = (ifState) CLREQD;
            retval[9] = (ifState) ISIMQD;
            retval[10] = (ifState) CLIMQD;
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

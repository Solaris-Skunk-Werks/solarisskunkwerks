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

public class PhysicalEnhancement extends abPlaceable {
    private final static ifPhysEnhance ISNone = new stPEISNone(),
                                       CLNone = new stPECLNone(),
                                       ISMASC = new stPEISMASC(),
                                       ISTSM = new stPEISTSM(),
                                       ISITSM = new stPEISITSM(),
                                       CLMASC = new stPECLMASC();
    private ifPhysEnhance CurConfig = ISNone;
    private Mech Owner;
    private int Placed = 0;

    public PhysicalEnhancement( Mech m ) {
        // We pass in the owning mech because calculations are done from tonnage
        Owner = m;
    }

    public void SetISNone() {
        CurConfig = ISNone;
    }
    
    public void SetCLNone() {
        CurConfig = CLNone;
    }
    
    public void SetISMASC() {
        CurConfig = ISMASC;
    }
    
    public void SetISTSM() {
        CurConfig = ISTSM;
    }

    public void SetISITSM() {
        CurConfig = ISITSM;
    }

    public void SetCLMASC() {
        CurConfig = CLMASC;
    }
    
    public boolean IsClan() {
        return CurConfig.IsClan();
    }

    public void Recalculate() {
        // recalculates the physical enhancement if something changed
        ifLoadout l = Owner.GetLoadout();

        // if it needs to be placed, do it.
        if( IsMASC() || IsTSM() ) {
            Place( l );
        }
    }

    @Override
    public boolean Contiguous() {
        return CurConfig.Contiguous();
    }

    public float GetTonnage() {
        if( IsArmored() ) {
            return CurConfig.GetTonnage( Owner.GetTonnage() ) + NumCrits() * 0.5f;
        } else {
            return CurConfig.GetTonnage( Owner.GetTonnage() );
        }
    }

    public int NumCrits() {
        return CurConfig.GetCrits( Owner.GetTonnage() );
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

    public float GetOffensiveBV() {
        return CurConfig.GetOffensiveBV( Owner.GetTonnage() );
    }

    public float GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    public float GetDefensiveBV() {
        if( IsArmored() ) {
            if( CurConfig.GetDefensiveBV( Owner.GetTonnage() ) > 0.0f ) {
                return ( ( CurConfig.GetDefensiveBV( Owner.GetTonnage() ) + CurConfig.GetOffensiveBV( Owner.GetTonnage() ) ) * 0.05f * NumCrits() ) + CurConfig.GetDefensiveBV( Owner.GetTonnage() );
            } else {
                return 5.0f * NumCrits();
            }
        }
        return CurConfig.GetDefensiveBV( Owner.GetTonnage() );
    }

    @Override
    public boolean CanArmor() {
        return CurConfig.CanArmor();
    }

    public boolean IsTSM() {
        if( CurConfig == ISTSM || CurConfig == ISITSM ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean IsMASC() {
        if( CurConfig == ISMASC || CurConfig == CLMASC ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int NumPlaced() {
        return Placed;
    }

    @Override
    public void IncrementPlaced() {
        if( CurConfig.IncrementPlaced() ) {
            Placed++;
        }
    }

    @Override
    public void DecrementPlaced() {
        if( CurConfig.DecrementPlaced() ) {
            Placed--;
        }
    }

    @Override
    public void ResetPlaced() {
        Placed = 0;
    }

    public float GetCost() {
        if( CurConfig == ISMASC || CurConfig == CLMASC ) {
            if( IsArmored() ) {
                return CurConfig.GetCost( Owner.GetTonnage(), Owner.GetEngine().GetRating() ) + NumCrits() * 150000.0f;
            } else {
                return CurConfig.GetCost( Owner.GetTonnage(), Owner.GetEngine().GetRating() );
            }
        } else {
            if( IsArmored() ) {
                return CurConfig.GetCost( Owner.GetTonnage(), Owner.GetEngine().GetTonnage() ) + NumCrits() * 150000.0f;
            } else {
                return CurConfig.GetCost( Owner.GetTonnage(), Owner.GetEngine().GetTonnage() );
            }
        }
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

    public ifState[] GetStates() {
        ifState[] retval = { (ifState) ISNone, (ifState) CLNone,
            (ifState) ISMASC, (ifState) ISTSM, (ifState) CLMASC, (ifState) ISITSM };
        return retval;
    }

    @Override
    public MechModifier GetMechModifier() {
        return CurConfig.GetMechModifier();
    }

    @Override
    public Exclusion GetExclusions() {
        return CurConfig.GetExclusions();
    }

    @Override
    public boolean IsCritable() {
        return CurConfig.IsCritable();
    }

    @Override
    public String toString() {
        if( CurConfig == ISTSM || CurConfig == ISITSM ) {
            if( CurConfig.GetCrits( Owner.GetTonnage() ) > Placed ) {
                return CurConfig.GetCritName() + " (" + ( CurConfig.GetCrits( Owner.GetTonnage() ) - Placed ) + ")";
            } else {
                return CurConfig.GetCritName();
            }
        } else {
            return CurConfig.GetCritName();
        }
    }
}

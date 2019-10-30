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

package states;

import components.AvailableCode;
import components.JumpJet;
import components.MechModifier;

public class stJumpJetPrototypeImproved implements ifJumpJetFactory, ifState {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );

    public stJumpJetPrototypeImproved() {
        AC.SetISCodes( 'E', 'X', 'F', 'F', 'X' );
        AC.SetISDates( 3020, 3020, true, 3020, 3070, 0, true, false );
        AC.SetISFactions( "FS", "FS", "FS", "" );
        AC.SetSuperHeavyCompatible( false );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public boolean IsImproved() {
        return true;
    }

    public boolean IsUMU() {
        return false;
    }

    public boolean IsProto() {
        return true;
    }

    public JumpJet GetJumpJet() {
        return  new JumpJet( "Prototype Improved Jump Jet", 
                "Prototype Improved Jump Jet", 
                "Prototype Imp. Jump Jet",
                "ISPrototypeImprovedJumpJet",
                "XTRO:Succesion Wars", 1, AC, true );
    }

    public double GetCost() {
        return 500.0f;
    }

    public double GetTonnage() {
        return 1.0f;
    }

    public int GetNumCrits() {
        return 1;
    }

    public AvailableCode GetAvailability() {
        return AC;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    public String LookupName() {
        return "Prototype Improved Jump Jet";
    }

    public String ChatName() {
        return "PIJJ";
    }
}

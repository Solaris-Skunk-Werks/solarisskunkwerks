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
import components.MechModifier;

public class stGyroISSuperHeavy implements ifGyro, ifState {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );

    public stGyroISSuperHeavy() {
        AC.SetISCodes( 'X', 'F', 'F', 'F' );
        AC.SetISDates( 0, 0, false, 2940, 0, 0, false, false );
        AC.SetISFactions( "", "", "", "" );
        AC.SetSuperHeavyCompatible( true );
        AC.SetSuperHeavyOnly( true );
        AC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public double GetTonnage( int rating ) {
        return (double) (((int) ( rating * 0.01f + 0.99f )) * 2);
    }
    
    public int GetCrits() {
        return 2;
    }
    
    public String ActualName() {
        return "Super Heavy Gyro";
    }

    public String CritName() {
        return "Super Heavy Gyro";
    }

    public String LookupName() {
        return "Super Heavy Gyro";
    }

    public String ChatName() {
        return "SH Gyro";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Gyro";
    }

    public String BookReference() {
        return "Interstellar Operations";
    }

    public String GetReportName() {
        return "Super Heavy";
    }

    public double GetBVMult() {
        return 1.0f;
    }
    
    public double GetCostMult() {
        return 500000.0f;
    }
    
    public AvailableCode GetAvailability() {
        return AC;
    }
    
    public MechModifier GetMechModifier() {
        return null;
    }

    @Override
    public String toString() {
        return "Super Heavy Gyro";
    }
}

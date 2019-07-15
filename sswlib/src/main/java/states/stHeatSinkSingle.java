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
import components.HeatSink;
import components.MechModifier;

public class stHeatSinkSingle implements ifHeatSinkFactory, ifState {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );

    public stHeatSinkSingle() {
        AC.SetISCodes( 'C', 'B', 'B', 'B' );
        AC.SetISDates( 0, 0, false, 2022, 0, 0, false, false );
        AC.SetISFactions( "", "", "WA", "" );
        AC.SetCLCodes( 'C', 'X', 'A', 'A' );
        AC.SetCLDates( 0, 0, false, 2022, 0, 0, false, false );
        AC.SetCLFactions( "", "", "WA", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_INTRODUCTORY, AvailableCode.RULES_INTRODUCTORY, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public boolean IsDouble() {
        return false;
    }

    public boolean IsCompact() {
        return false;
    }

    public boolean IsLaser() {
        return false;
    }

    public double GetTonnage() {
        return 1.0f;
    }

    public int GetCost() {
        return 2000;
    }

    public int GetDissipation() {
        return 1;
    }

    public HeatSink GetHeatSink() {
        return new HeatSink( "Single Heat Sink", "Heat Sink", "Heat Sink", "Heat Sink", "Tech Manual", 1, false, AC );
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
        return "Single Heat Sink";
    }

    public String ChatName() {
        return "SHS";
    }
}

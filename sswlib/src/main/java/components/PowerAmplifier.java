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

import java.util.ArrayList;

public class PowerAmplifier {

    private ifLoadout CurLoadout;
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );

    public PowerAmplifier( ifLoadout l ) {
        AC.SetISCodes( 'D', 'B', 'C', 'B' );
        AC.SetISDates( 0, 0, false, 2300, 0, 0, false, false );
        AC.SetISFactions( "", "", "PS", "" );
        AC.SetCLCodes( 'D', 'X', 'C', 'B' );
        AC.SetCLDates( 0, 0, false, 2300, 0, 0, false, false );
        AC.SetCLFactions( "", "", "PS", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        CurLoadout = l;
    }

    public AvailableCode GetAvailability() {
        return AC;
    }

    public double GetTonnage() {
        double tons = 0.0;
        ArrayList v = CurLoadout.GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof ifWeapon ) {
                if ( ((ifWeapon)v.get( i ) ).RequiresPowerAmps() )
                    tons += ((abPlaceable) v.get( i )).GetTonnage();
            }
        }
        if( tons <= 0.0 ) {
            return 0.0;
        } else {
            double result = 0.0;
            if( CurLoadout.GetUnit().UsingFractionalAccounting() ) {
                result = Math.ceil( tons * 200 ) * 0.001;
            } else {
                result = (double) Math.ceil( tons * 0.2 ) * 0.5;
            }
            return result;
        }
    }

    public double GetCost() {
        return GetTonnage() * 20000.0;
    }

    @Override
    public String toString() {
        return "Power Amplifiers";
    }
}

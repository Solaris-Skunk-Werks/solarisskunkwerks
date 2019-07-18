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

import components.*;

public class stArmorISVST implements ifArmor, ifState {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private final static MechModifier MechMod = new MechModifier( 0, 0, 0, 0.0f, 0, 0, 10, 0.2f, 0.0f, 0.0f, 0.0f, true, false );

    public stArmorISVST() {
        AC.SetISCodes( 'E', 'X', 'X', 'F' );
        AC.SetISDates( 0, 0, false, 3067, 0, 0, false, false );
        AC.SetISFactions( "", "", "CC", "" );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL );
    }

    public String ActualName() {
        return "Vehicle Stealth Armor";
    }

    public String CritName() {
        return "Vehicle Stealth Armor";
    }

    public String LookupName() {
        return "Vehicle Stealth Armor";
    }

    public String ChatName() {
        return "V Stlth Armor";
    }

    public String AbbrevName() {
        return "VSA";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Vehicle Stealth Armor";
    }

    public String BookReference() {
        return "Tactical Operations";
    }

    public boolean HasCounterpart() {
        return false;
    }

    public boolean Place( MechArmor a, ifMechLoadout l ) {
        // Place the armor in the mech
        boolean placed = false;
        int increment = 11;
        try {
            if( l.IsQuad() ) {
                // these crits can only ever go here, no need to check.
                l.AddToLA( a, 4 );
                l.AddToLA( a, 5 );
                l.AddToRA( a, 4 );
                l.AddToRA( a, 5 );
            } else {
                // check each available space from the bottom.  If we cannot
                // allocate then we need to revert to normal armor
                while( placed == false ) {
                    if ( increment < 0 ) { return false; }
                    try {
                        l.AddToLA( a, increment );
                        increment--;
                        placed = true;
                    } catch ( Exception e ) {
                        increment--;
                    }
                }
                placed = false;
                while( placed == false ) {
                    if ( increment < 0 ) { return false; }
                    try {
                        l.AddToLA( a, increment );
                        increment--;
                        placed = true;
                    } catch ( Exception e ) {
                        increment--;
                    }
                }
                placed = false;
                increment = 11;
                while( placed == false ) {
                    if ( increment < 0 ) { return false; }
                    try {
                        l.AddToRA( a, increment );
                        increment--;
                        placed = true;
                    } catch ( Exception e ) {
                        increment--;
                    }
                }
                placed = false;
                while( placed == false ) {
                    if ( increment < 0 ) { return false; }
                    try {
                        l.AddToRA( a, increment );
                        increment--;
                        placed = true;
                    } catch ( Exception e ) {
                        increment--;
                    }
                }
            }

            // check each available space from the bottom.  If we cannot allocate
            // then we need to revert to normal armor
            placed = false;
            increment = 11;
            while( placed == false ) {
                if ( increment < 0 ) { return false; }
                try {
                    l.AddToLT( a, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
            placed = false;
            while( placed == false ) {
                if ( increment < 0 ) { return false; }
                try {
                    l.AddToLT( a, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
            placed = false;
            increment = 11;
            while( placed == false ) {
                if ( increment < 0 ) { return false; }
                try {
                    l.AddToRT( a, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }
            placed = false;
            while( placed == false ) {
                if ( increment < 0 ) { return false; }
                try {
                    l.AddToRT( a, increment );
                    increment--;
                    placed = true;
                } catch ( Exception e ) {
                    increment--;
                }
            }

            // leg crits can only ever go here, so no need to check.
            l.AddToLL( a, 4 );
            l.AddToLL( a, 5 );
            l.AddToRL( a, 4 );
            l.AddToRL( a, 5 );
        } catch ( Exception e ) {
            // something else was probably in the way.  Tell the placer
            return false;
        }

        // all went well
        return true;
    }

    public boolean Place( MechArmor a, ifMechLoadout l, LocationIndex[] Locs ) {
        LocationIndex li;
        try {
            for( int i = 0; i < Locs.length; i++ ) {
                li = (LocationIndex) Locs[i];
                l.AddTo( a, li.Location, li.Index );
            }
        } catch( Exception e ) {
            return false;
        }
        return true;
    }

    public int NumCrits() {
        return 12;
    }

    public int NumCVSpaces() {
        return 2;
    }

    public int PatchworkCrits() {
        return 2;
    }

    public int PatchworkSpaces() {
        return 1;
    }

    public double GetAVMult() {
        return 1.0;
    }

    public double GetPointsPerTon() {
        return 0.0625;
    }

    public boolean IsStealth() {
        return true;
    }

    public double GetCostMult() {
        return 50000.0;
    }

    public double GetBVTypeMult() {
        return 1.0;
    }

    public int GetBAR() {
        return 10;
    }

    public boolean LocationLocked() {
        // stealth armor is always locked.
        return true;
    }

    public void SetLocked( boolean l ) {
        // stealth armor is always locked.
    }

    public MechModifier GetMechModifier() {
        return MechMod;
    }

    public AvailableCode GetAvailability() {
        return AC;
    }

    public boolean AllowHarJel(){
        return false;
    }
}

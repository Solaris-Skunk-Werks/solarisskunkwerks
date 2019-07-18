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

import components.MechArmor;
import components.AvailableCode;
import components.LocationIndex;
import components.MechModifier;
import components.ifMechLoadout;

public class stArmorISRE implements ifArmor, ifState {
    boolean locked = false;
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );

    public stArmorISRE() {
        AC.SetISCodes( 'E', 'X', 'X', 'F' );
        AC.SetISDates( 3058, 3063, true, 3063, 0, 0, false, false );
        AC.SetISFactions( "DC", "DC", "", "" );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL );
    }

    public String ActualName() {
        return "Reactive Armor";
    }

    public String CritName() {
        return "Reactive";
    }

    public String LookupName() {
        return "Reactive Armor";
    }

    public String ChatName() {
        return "Rctv Armor";
    }

    public String AbbrevName() {
        return "RE";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Reactive";
    }

    public String BookReference() {
        return "Tactical Operations";
    }

    public boolean HasCounterpart() {
        return true;
    }

    public boolean Place( MechArmor a, ifMechLoadout l ) {
        // Simply place the armor into the loadout queue
        l.AddToQueue( a );
        return true;
    }

    public boolean Place( MechArmor a, ifMechLoadout l, LocationIndex[] Locs ) {
        LocationIndex li;
        try {
            for( int i = 0; i < Locs.length; i++ ) {
                li = (LocationIndex) Locs[i];
                for( int x = 0; x < li.Number; x++ ) {
                    int index = li.Index;
                    if( index < 0 ) { index = l.FirstFree( l.GetCrits( li.Location ) ); }
                    if( index >= l.GetCrits( li.Location ).length ) { return false; }
                    l.AddTo( a, li.Location, index );
                }
            }
        } catch( Exception e ) {
            return false;
        }
        return true;
    }

    public int NumCrits() {
        return 14;
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
        return false;
    }

    public double GetCostMult() {
        return 30000.0;
    }

    public double GetBVTypeMult() {
        return 1.5;
    }

    public int GetBAR() {
        return 10;
    }

    public boolean LocationLocked() {
        return locked;
    }

    public void SetLocked( boolean l ) {
        locked = l;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    public AvailableCode GetAvailability() {
        return AC;
    }

    public boolean AllowHarJel(){
        return false;
    }
}

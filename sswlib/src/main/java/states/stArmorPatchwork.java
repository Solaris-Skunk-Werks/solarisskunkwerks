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

public class stArmorPatchwork implements ifArmor, ifState {
    boolean locked = false;
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );

    public stArmorPatchwork() {
        AC.SetISCodes( 'A', 'A', 'A', 'A' );
        AC.SetISDates( 0, 0, false, 2400, 0, 0, false, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetCLCodes( 'A', 'X', 'A', 'A' );
        AC.SetISDates( 0, 0, false, 2400, 0, 0, false, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL );
    }

    public String ActualName() {
        return "Patchwork Armor";
    }

    public String CritName() {
        return "Patchwork";
    }

    public String LookupName() {
        return "Patchwork Armor";
    }

    public String ChatName() {
        return "Armor";
    }

    public String AbbrevName() {
        return "PA";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Standard Armor";
    }

    public String BookReference() {
        return "Tactical Operations";
    }

    public boolean HasCounterpart() {
        return false;
    }

    public boolean Place( MechArmor a, ifMechLoadout l ) {
        // Place all patchwork components.
        try {
            int idx = l.FirstFree( l.GetHDCrits() );
            for( int i = 0; i < a.GetHDArmorType().PatchworkCrits(); i++ ) {
                l.AddToHD( a, idx );
                idx = l.FirstFree( l.GetHDCrits() );
            }
            idx = l.FirstFree( l.GetCTCrits() );
            for( int i = 0; i < a.GetCTArmorType().PatchworkCrits(); i++ ) {
                l.AddToCT( a, idx );
                idx = l.FirstFree( l.GetCTCrits() );
            }
            idx = l.FirstFree( l.GetLTCrits() );
            for( int i = 0; i < a.GetLTArmorType().PatchworkCrits(); i++ ) {
                l.AddToLT( a, idx );
                idx = l.FirstFree( l.GetLTCrits() );
            }
            idx = l.FirstFree( l.GetRTCrits() );
            for( int i = 0; i < a.GetRTArmorType().PatchworkCrits(); i++ ) {
                l.AddToRT( a, idx );
                idx = l.FirstFree( l.GetRTCrits() );
            }
            idx = l.FirstFree( l.GetLACrits() );
            for( int i = 0; i < a.GetLAArmorType().PatchworkCrits(); i++ ) {
                l.AddToLA( a, idx );
                idx = l.FirstFree( l.GetLACrits() );
            }
            idx = l.FirstFree( l.GetRACrits() );
            for( int i = 0; i < a.GetRAArmorType().PatchworkCrits(); i++ ) {
                l.AddToRA( a, idx );
                idx = l.FirstFree( l.GetRACrits() );
            }
            idx = l.FirstFree( l.GetLLCrits() );
            for( int i = 0; i < a.GetLLArmorType().PatchworkCrits(); i++ ) {
                l.AddToLL( a, idx );
                idx = l.FirstFree( l.GetLLCrits() );
            }
            idx = l.FirstFree( l.GetRLCrits() );
            for( int i = 0; i < a.GetRLArmorType().PatchworkCrits(); i++ ) {
                l.AddToRL( a, idx );
                idx = l.FirstFree( l.GetRLCrits() );
            }
        } catch( Exception e ) {
            return false;
        }
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
        return 0;
    }

    public int NumCVSpaces() {
        return 0;
    }

    public int PatchworkCrits() {
        return 0;
    }

    public int PatchworkSpaces() {
        return 0;
    }

    public double GetAVMult() {
        return 1.0f;
    }

    public double GetPointsPerTon() {
        return 0.0625;
    }

    public boolean IsStealth() {
        return false;
    }

    public double GetCostMult() {
        return 10000.0f;
    }

    public double GetBVTypeMult() {
        return 1.0f;
    }

    public int GetBAR() {
        return 10;
    }

    public boolean LocationLocked() {
        return true;
    }

    public void SetLocked( boolean l ) {
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

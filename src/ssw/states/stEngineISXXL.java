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

package ssw.states;

import ssw.components.AvailableCode;
import ssw.components.MechModifier;

public class stEngineISXXL implements ifEngine, ifState {
    // An Inner Sphere XXL Fusion Engine
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private final static float[] Masses = {0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,
        0.5f,0.5f,0.5f,0.5f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.5f,1.5f,
        1.5f,1.5f,1.5f,1.5f,1.5f,2.0f,2.0f,2.0f,2.0f,2.0f,2.0f,2.0f,2.5f,2.5f,
        2.5f,2.5f,3.0f,3.0f,3.0f,3.0f,3.5f,3.5f,3.5f,3.5f,4.0f,4.0f,4.0f,
        4.5f,4.5f,4.5f,5.0f,5.0f,5.5f,5.5f,5.5f,6.0f,6.0f,6.5f,6.5f,
        7.0f,7.5f,7.5f,8.0f,8.5f,8.5f,9.0f,9.5f,10.0f,10.5f,11.0f,11.5f,
        12.5f,13.0f,14.0f,14.5f,15.5f,16.5f,17.5f};
    private final static int[] BFStructure = {1,1,1,1,2,2,2,2,3,3,3,4,4,4,4,5,5,5,5};

    public stEngineISXXL() {
        AC.SetISCodes( 'F', 'X', 'X', 'F' );
        AC.SetISDates( 2582, 0, true, 3055, 0, 0, false, false );
        AC.SetISFactions( "TH", "FC", "", "" );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL );
    }

    public boolean HasCounterpart() {
        return true;
    }

    public float GetTonnage( int Rating ) {
        return Masses[GetIndex( Rating )];
    }
    
    public int GetCTCrits() {
        return 3;
    }
    
    public int GetSideTorsoCrits() {
        return 6;
    }
    
    public int NumCTBlocks() {
        return 2;
    }
    
    public boolean CanSupportRating( int rate ) {
        if( rate < 5 || rate > 400 || rate % 5 != 0 ) {
            return false;
        } else {
            return true;
        }
    }

    public String GetLookupName() {
        return "XXL Engine";
    }

    public String GetCritName() {
        return "XXL Engine";
    }

    public String GetMMName() {
        return "Fusion Engine";
    }

    public float GetCost( int MechTonnage, int Rating ) {
        return ( 100000.0f * (float) MechTonnage * (float) Rating ) / 75.0f;
    }
    
    public AvailableCode GetAvailability() {
        return AC;
    }
    
    public int FreeHeatSinks() {
        return 10;
    }

    public float GetBVMult() {
        return 0.5f;
    }
    
    public boolean IsFusion() {
        return true;
    }

    public boolean IsNuclear() {
        return true;
    }

    public int GetFullCrits() {
        return 18;
    }

    private int GetIndex( int Rating ) {
        return Rating / 5 - 2;
    }

    private int GetBFIndex( int tonnage ) {
        return (tonnage - 10) / 5;
    }

    public int GetBFStructure( int tonnage ) {
        return BFStructure[GetBFIndex(tonnage)];
    }
    
    public int MaxMovementHeat() {
        return 6;
    }

    public int MinimumHeat() {
        return 2;
    }

    public int JumpingHeatMultiplier() {
        return 2;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    public boolean IsPrimitive() {
        return false;
    }

    @Override
    public String toString() {
        return "XXL Fusion Engine";
    }
}

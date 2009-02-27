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

import ssw.Constants;
import ssw.components.*;

public class stEngineCLIC implements ifEngine, ifState {
    // A Clan I.C.E. Engine
    private final static AvailableCode AC = new AvailableCode( true, 'C', 'X', 'A', 'A',
        1950, 0, 0, "PS", "", false, false );
    private final static float[] Masses = { 1.0f,1.0f,1.0f,1.0f,2.0f,2.0f,2.0f,
        2.0f,3.0f,3.0f,3.0f,4.0f,4.0f,4.0f,5.0f,5.0f,6.0f,6.0f,6.0f,7.0f,7.0f,
        8.0f,8.0f,8.0f,9.0f,9.0f,10.0f,10.0f,11.0f,11.0f,12.0f,12.0f,12.0f,
        14.0f,14.0f,15.0f,15.0f,16.0f,17.0f,17.0f,18.0f,19.0f,20.0f,20.0f,21.0f,
        22.0f,23.0f,24.0f,25.0f,26.0f,27.0f,28.0f,29.0f,31.0f,32.0f,33.0f,35.0f,
        36.0f,38.0f,39.0f,41.0f,43.0f,45.0f,47.0f,49.0f,51.0f,54.0f,57.0f,59.0f,
        63.0f,66.0f,69.0f,73.0f,77.0f,82.0f,87.0f,92.0f,98.0f,105.0f };

    public stEngineCLIC() {
        AC.SetRulesLevelBM( Constants.ADVANCED );
    }

    public boolean IsClan() {
        return true;
    }
    
    public float GetTonnage( int Rating ) {
        return Masses[GetIndex( Rating )];
    }
    
    public int GetCTCrits() {
        return 3;
    }
    
    public int GetSideTorsoCrits() {
        return 0;
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
        return "I.C.E. Engine";
    }

    public String GetCritName() {
        return "I.C.E. Engine";
    }
    
    public String GetMMName() {
        return "I.C.E.";
    }

    public float GetCost( int MechTonnage, int Rating ) {
        return ( 1250 * MechTonnage * Rating ) / 75;
    }
    
    public AvailableCode GetAvailability() {
        return AC;
    }
    
    public int FreeHeatSinks() {
        return 0;
    }

    public float GetBVMult() {
        return 1.0f;
    }
    
    public boolean IsFusion() {
        return false;
    }

    public boolean IsNuclear() {
        return false;
    }

    public int GetFullCrits() {
        return 6;
    }

    private int GetIndex( int Rating ) {
        return Rating / 5 - 2;
    }

    public int MaxMovementHeat() {
        return 0;
    }

    public int MinimumHeat() {
        return 0;
    }

    public int JumpingHeatMultiplier() {
        return 1;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    @Override
    public String toString() {
        return "I.C.E. Engine";
    }
}

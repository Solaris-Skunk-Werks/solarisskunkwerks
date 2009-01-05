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

public class HeatSink extends abPlaceable {
    private int Crits;
    private String CritName,
                   MMName;
    private AvailableCode AC;

    public HeatSink( String name, String mname, int numCrits, AvailableCode A ) {
        Crits = numCrits;
        CritName = name;
        AC = A;
        MMName = mname;
    }

    // returns the name of this item in the Loadout.
    public String GetCritName() {
        return CritName;
    }

    public String GetMMName( boolean UseRear ) {
        return MMName;
    }

    // returns the number of crits this item takes in the Loadout.
    public int NumCrits() {
        return Crits;
    }

    public float GetTonnage() {
        // Heat sinks are calculated from the HeatSinkFactory, not from the
        // individual heat sink
        if( IsArmored() ) {
            return Crits * 0.5f;
        } else {
            return 0.0f;
        }
    }

    // Heat sinks are calculated from the HeatSinkFactory, not from the
    // individual heat sink
    public float GetOffensiveBV() {
        return 0.0f;
    }

    public float GetCurOffensiveBV( boolean UseRear ) {
        return 0.0f;
    }

    public float GetDefensiveBV() {
        if( IsArmored() ) {
            return 5.0f * Crits;
        }
        return 0.0f;
    }

    public float GetCost() {
        // Heat sinks are calculated from the HeatSinkFactory, not from the
        // individual heat sink
        if( IsArmored() ) {
            return Crits * 150000.0f;
        } else {
            return 0.0f;
        }
    }
    
    @Override
    public boolean CoreComponent() {
        return true;
    }

    // All placeables should be able to return their AvailabileCode
    public AvailableCode GetAvailability() {
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

    @Override
    public String toString() {
        return CritName;
    }
}

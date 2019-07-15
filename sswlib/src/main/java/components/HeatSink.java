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

public class HeatSink extends abPlaceable {
    private boolean Compact;
    private int Crits,
                NumHS = 1;
    private String CritName,
                   ActualName,
                   LookupName,
                   BookReference,
                   MMName;
    private AvailableCode AC;

    public HeatSink( String actualname, String lookupname, String critname, String mname, String bookref, int numCrits, boolean compact, AvailableCode A ) {
        ActualName = actualname;
        Compact = compact;
        Crits = numCrits;
        CritName = critname;
        LookupName = lookupname;
        BookReference = bookref;
        AC = A;
        MMName = mname;
    }

    public String ActualName() {
        if( Compact ) {
            return "[" + NumHS + "] " + ActualName;
        }
        return ActualName;
    }

    public String CritName() {
        if( Compact ) {
            return "[" + NumHS + "] " + CritName;
        }
        return CritName;
    }

    public String LookupName() {
        return LookupName;
    }

    public String ChatName() {
        // individual heatsinks aren't included in the chat
        return "";
    }

    public String MegaMekName( boolean UseRear ) {
        if( Compact ) {
            return "IS" + NumHS + " " + MMName;
        }
        return MMName;
    }

    public String BookReference() {
        return BookReference;
    }

    // returns the number of crits this item takes in the Loadout.
    public int NumCrits() {
        return Crits;
    }

    public int NumCVSpaces() {
        return 0;
    }

    public int NumHeatSinks() {
        // provided for Compact heat sinks
        return NumHS;
    }

    public void SetNumHS( int i ) {
        // sets the number of heat sinks in this slot (compact HS)
        NumHS = i;
    }

    public boolean IsCompact() {
        return Compact;
    }

    public double GetTonnage() {
        // Heat sinks are calculated from the HeatSinkFactory, not from the
        // individual heat sink
        if( IsArmored() ) {
            return Crits * 0.5;
        } else {
            return 0.0;
        }
    }

    // Heat sinks are calculated from the HeatSinkFactory, not from the
    // individual heat sink
    public double GetOffensiveBV() {
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    public double GetDefensiveBV() {
        if( IsArmored() ) {
            return 5.0 * Crits;
        }
        return 0.0;
    }

    public double GetCost() {
        // Heat sinks are calculated from the HeatSinkFactory, not from the
        // individual heat sink
        if( IsArmored() ) {
            return Crits * 150000.0;
        } else {
            return 0.0;
        }
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    // All placeables should be able to return their AvailabileCode
    public AvailableCode GetAvailability() {
        AvailableCode retval = AC.Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        return retval;
    }

    @Override
    public String toString() {
        return CritName();
    }
}

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

public class SimplePlaceable extends abPlaceable {
    // This is a class extending abPlaceable to provide for simple placeables
    // such as sensors that don't do much for us in the editor
    // other than take up space.  These are almost always location locked, but
    // you can set that if you wish.  These items return 0 for tonnage, unless
    // they are armored components
    private boolean LocLocked;
    private int Crits;
    private double Tonnage = 0.0,
                  Cost = 0.0,
                  ArmoredTonnage = 0.5;
    private String ActualName,
                   LookupName,
                   CritName,
                   ChatName,
                   BookReference,
                   MegaMekName;
    private AvailableCode AC;

    public SimplePlaceable( String actualname, String critname, String lookupname, String mname, String bookref, int numCrits, boolean locked, AvailableCode A ) {
        LocLocked = locked;
        Crits = numCrits;
        ActualName = actualname;
        CritName = critname;
        LookupName = lookupname;
        MegaMekName = mname;
        BookReference = bookref;
        AC = A;
    }

    @Override
    public boolean LocationLocked() {
        return LocLocked;
    }

    public String ActualName() {
        return ActualName;
    }

    public String CritName() {
        return CritName;
    }

    public String LookupName() {
        return LookupName;
    }

    public String ChatName() {
        // simpleplaceables aren't included in the chat
        return "";
    }

    public String MegaMekName( boolean UseRear ) {
        return MegaMekName;
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

    public void SetTonnage( double tons ) {
        Tonnage = tons;
    }

    public void SetCost( double cost ) {
        Cost = cost;
    }

    public double GetTonnage() {
        if( IsArmored() ) {
            return Crits * ArmoredTonnage + Tonnage;
        } else {
            return Tonnage;
        }
    }
    
    public void SetArmoredTonnage(double tonnage){
        ArmoredTonnage = tonnage;
    }

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
            return 5.0 * NumCrits();
        }
        return 0.0;
    }

    public double GetCost() {
        if( IsArmored() ) {
            return 150000.0 * Crits + Cost;
        } else {
            return Cost;
        }
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
    public boolean CoreComponent() {
        // simple placeables should be considered core components
        return true;
    }

    @Override
    public String toString() {
        return CritName;
    }
}

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
import common.CommonTools;

/**
 *
 * @author olaughlj
 */
public class Dumper extends abPlaceable {
    private Equipment Owner;
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private String DumpDirection;
    private LocationIndex DumperLocation;
    
    public Dumper( Equipment eq)
    {
        AC.SetISCodes( 'A', 'A', 'A', 'A', 'A' );
        AC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetISFactions( "", "", "", "" );
        AC.SetCLCodes( 'A', 'X', 'A', 'A', 'A' );
        AC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetCLFactions( "", "", "", "" );
        AC.SetPBMAllowed( false );
        AC.SetPIMAllowed( true );
        AC.SetPrimitiveOnly(false);
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Owner = eq;
        DumpDirection = "None";
    }

    public void SetDumpDirection(String dumpDir){
        DumpDirection = dumpDir;
    }

    public String GetDumpDirection(){
        return DumpDirection;
    }


    public String ActualName() {
        return "Dumper";
    }

    public String CritName() {
        return String.format(("%s (%s)"), ActualName(), GetDumpDirection());
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    @Override
    public boolean LocationLinked() {
        return true;
    }

    public String LookupName() {
            return CritName();
    }

    public String ChatName() {
        return "Dmper";
    }

    public String MegaMekName( boolean UseRear ) {
        return CritName();
    }

    public String BookReference() {
        return "Tech Manual";
    }

    @Override
    public int NumCrits() {
        return 1;
    }

    public int NumCVSpaces() {
        return 1;
    }

    @Override
    public double GetTonnage() {
        //TODO: Add dumper calculation based on location
        double tonnage = 0.0;
        
        tonnage = Owner.GetTonnage() * 0.05;
        
        int nWholeTonnage = (int)tonnage;
        double difference = tonnage - nWholeTonnage;
        if (difference > 0 && difference < .5) {
            tonnage = tonnage + .5;
        }
        else if (difference  > .5) {
            tonnage = nWholeTonnage + 1.0;
        }
        return tonnage;
    }

    @Override
    public double GetCost() {
       return 5000;
    }

    public double GetOffensiveBV() {
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV(boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic) {
        return 0.0;
    }
    
    public double GetDefensiveBV() {
        return 0.0;
    }

    @Override
    public AvailableCode GetAvailability() {
        AvailableCode retval = AC.Clone();
        return retval;
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public String toString() {
        return CritName();
    }

//    @Override
//    public boolean Place( ifMechLoadout l, LocationIndex[] locs ) {
//        boolean ret = super.Place( l );
//        Owner.
//        return ret;
//    }
}

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

import components.AvailableCode;
import components.LocationIndex;
import components.MechModifier;
import components.SimplePlaceable;

public class stCockpitInterface implements ifCockpit, ifState {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private SimplePlaceable Sensors = new SimplePlaceable( "Sensors", "Sensors", "Sensors", "Sensors", "Tech Manual", 1, true, AC );
    private SimplePlaceable LifeSupport = new SimplePlaceable( "Life Support", "Life Support", "Life Support", "Life Support", "Tech Manual", 1, true, AC );
    private SimplePlaceable SecondSensors = new SimplePlaceable( "Sensors", "Sensors", "Sensors", "Sensors", "Tech Manual", 1, true, AC );
    private SimplePlaceable SecondLifeSupport = new SimplePlaceable( "Life Support", "Life Support", "Life Support", "Life Support", "Tech Manual", 1, true, AC );

    public stCockpitInterface() {
        AC.SetISCodes( 'E', 'X', 'X', 'F', 'X' );
        AC.SetISDates( 3068, 3074, false, 3078, 0, 0, false, false );
        AC.SetISFactions( "WB", "WB", "WB", "" );
        AC.SetSuperHeavyCompatible(false);
        AC.SetCLCodes( 'F', 'X', 'X', 'F', 'F' );
        AC.SetCLDates( 3082, 3083, false, 3083, 0, 0, false, false );
        AC.SetCLFactions( "CHH", "CHH", "CHH", "" );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public double GetTonnage(int MechTonnage) {
        double result = 4.0f;
        result += Sensors.GetTonnage();
        result += SecondSensors.GetTonnage();
        result += LifeSupport.GetTonnage();
        result += SecondLifeSupport.GetTonnage();
        return result;
    }

    public boolean HasSecondLSLoc() {
        return true;
    }

    public SimplePlaceable GetLifeSupport() {
        return LifeSupport;
    }

    public SimplePlaceable GetSensors() {
        return Sensors;
    }

    public SimplePlaceable GetSecondLifeSupport() {
        return SecondLifeSupport;
    }

    public SimplePlaceable GetSecondSensors() {
        return SecondSensors;
    }

    public String ActualName() {
        return "BattleMech Interface";
    }

    public String CritName() {
        return "Interface Cockpit";
    }

    public String LookupName() {
        return "Interface Cockpit";
    }

    public String ChatName() {
        return "";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Interface";
    }

    public String BookReference() {
        return "Interstellar Operations";
    }

    public String GetReportName() {
        return "Interface";
    }

    public double GetCost( int Tonnage, int year ) {
        double result = 1500000.0f;
        return result;
    }

    public boolean HasFireControl() {
        return true;
    }

    public double BVMod() {
        return 1.3f;
    }

    public AvailableCode GetAvailability() {
        return AC;
    }

    public int ReportCrits() {
        return 6;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    @Override
    public String toString() {
        return "Interface Cockpit";
    }

    public LocationIndex GetCockpitLoc() {
        return new LocationIndex( 2, LocationIndex.MECH_LOC_HD, 2 );
    }

    public LocationIndex GetFirstSensorLoc() {
        return new LocationIndex( 1, LocationIndex.MECH_LOC_HD, -1 );
    }

    public LocationIndex GetSecondSensorLoc() {
        return new LocationIndex( 4, LocationIndex.MECH_LOC_HD, -1 );
    }

    public LocationIndex GetFirstLSLoc() {
        return new LocationIndex( 0, LocationIndex.MECH_LOC_HD, -1 );
    }

    public LocationIndex GetSecondLSLoc() {
        return new LocationIndex( 5, LocationIndex.MECH_LOC_HD, -1 );
    }

    public boolean CanUseCommandConsole() {
        return false;
    }

    public boolean HasThirdSensors() {
        return false;
    }

    public LocationIndex GetThirdSensorLoc() {
        return null;
    }

    public SimplePlaceable GetThirdSensors() {
        return null;
    }

    public boolean HasThirdLifeSupport() {
        return false;
    }

    public LocationIndex GetThirdLSLoc() {
        return null;
    }

    public SimplePlaceable GetThirdLifeSupport() {
        return null;
    }
    
    public boolean IsTorsoMounted() {
        return false;
    }

    public boolean RequiresGyro() {
        return false;
    }

    public boolean CanArmor() {
        return false;
    }
}

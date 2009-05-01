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
import ssw.components.AvailableCode;
import ssw.components.LocationIndex;
import ssw.components.MechModifier;
import ssw.components.SimplePlaceable;

public class stCockpitIndustrialAFC implements ifCockpit, ifState {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private final static SimplePlaceable Sensors = new SimplePlaceable( "Sensors", "Sensors", 1, true, AC );
    private final static SimplePlaceable LifeSupport = new SimplePlaceable( "Life Support", "Life Support", 1, true, AC );
    private final static SimplePlaceable SecondSensors = new SimplePlaceable( "Sensors", "Sensors", 1, true, AC );
    private final static SimplePlaceable SecondLifeSupport = new SimplePlaceable( "Life Support", "Life Support", 1, true, AC );

    public stCockpitIndustrialAFC() {
        AC.SetISCodes( 'D', 'D', 'E', 'E' );
        AC.SetISDates( 0, 0, false, 2300, 0, 0, false, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetCLCodes( 'D', 'X', 'D', 'D' );
        AC.SetCLDates( 0, 0, false, 2300, 0, 0, false, false );
        AC.SetCLFactions( "", "", "TH", "" );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public float GetTonnage() {
        float result = 3.0f;
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

    public String GetLookupName() {
        return "Industrial w/ Adv. FC";
    }

    public String GetCritName() {
        return "Cockpit";
    }
    
    public String GetMMName() {
        return "Cockpit";
    }

    public String GetReportName() {
        return "Industrial w/ Adv. Fire Control";
    }

    public float GetCost( int Tonnage ) {
        float result = 250000.0f + ( 2000.0f * Tonnage );
        result += Sensors.GetCost();
        result += LifeSupport.GetCost();
        result += SecondSensors.GetCost();
        result += SecondLifeSupport.GetCost();
        return result;
    }

    public boolean HasFireControl() {
        return true;
    }

    public float BVMod() {
        return 1.0f;
    }

    public AvailableCode GetAvailability() {
        return AC;
    }
    
    public int ReportCrits() {
        return 5;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    @Override
    public String toString() {
        return "Industrial Cockpit (AFC)";
    }

    public LocationIndex GetCockpitLoc() {
        return new LocationIndex( 2, Constants.LOC_HD, -1 );
    }

    public LocationIndex GetFirstSensorLoc() {
        return new LocationIndex( 1, Constants.LOC_HD, -1 );
    }

    public LocationIndex GetSecondSensorLoc() {
        return new LocationIndex( 4, Constants.LOC_HD, -1 );
    }

    public LocationIndex GetFirstLSLoc() {
        return new LocationIndex( 0, Constants.LOC_HD, -1 );
    }

    public LocationIndex GetSecondLSLoc() {
        return new LocationIndex( 5, Constants.LOC_HD, -1 );
    }

    public boolean CanUseCommandConsole() {
        return true;
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

    public boolean IsTorsoMounted() {
        return false;
    }
}
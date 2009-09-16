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

public class stCockpitTorsoMount implements ifCockpit, ifState {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
    private final static SimplePlaceable Sensors = new SimplePlaceable( "Sensors", "Sensors", "Sensors", "Sensors", "Tech Manual", 1, true, AC );
    private final static SimplePlaceable LifeSupport = new SimplePlaceable( "Life Support", "Life Support", "Life Support", "Life Support", "Tech Manual", 1, true, AC );
    private final static SimplePlaceable SecondSensors = new SimplePlaceable( "Sensors", "Sensors", "Sensors", "Sensors", "Tech Manual", 1, true, AC );
    private final static SimplePlaceable SecondLifeSupport = new SimplePlaceable( "Life Support", "Life Support", "Life Support", "Life Support", "Tech Manual", 1, true, AC );
    private final static SimplePlaceable ThirdSensors = new SimplePlaceable( "Sensors", "Sensors", "Sensors", "Sensors", "Tech Manual", 1, true, AC );

    public stCockpitTorsoMount() {
        AC.SetISCodes( 'E', 'X', 'X', 'F' );
        AC.SetISDates( 3044, 3053, true, 3053, 0, 0, false, false );
        AC.SetISFactions( "FC", "FC", "", "" );
        AC.SetCLCodes( 'E', 'X', 'X', 'F' );
        AC.SetCLDates( 3044, 3055, true, 3055, 0, 0, false, false );
        AC.SetCLFactions( "FC", "FC", "", "" );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public double GetTonnage() {
        double result = 4.0f;
        result += Sensors.GetTonnage();
        result += SecondSensors.GetTonnage();
        result += LifeSupport.GetTonnage();
        result += SecondLifeSupport.GetTonnage();
        result += ThirdSensors.GetTonnage();
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
        return "Torso-Mounted Cockpit";
    }

    public String CritName() {
        return "Torso-Mounted Cockpit";
    }

    public String LookupName() {
        return "Torso-Mounted Cockpit";
    }

    public String ChatName() {
        return "Torso Cockpit";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Cockpit";
    }

    public String BookReference() {
        return "Tactical Operations";
    }

    public String GetReportName() {
        return "Torso-Mounted";
    }

    public double GetCost( int Tonnage ) {
        double result = 750000.0f + ( 2000.0f * Tonnage );
        result += Sensors.GetCost();
        result += LifeSupport.GetCost();
        result += SecondSensors.GetCost();
        result += SecondLifeSupport.GetCost();
        result += ThirdSensors.GetCost();
        return result;
    }
    
    public boolean HasFireControl() {
        return true;
    }

    public double BVMod() {
        return 0.95f;
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
        return "Torso-Mounted Cockpit";
    }

    public LocationIndex GetCockpitLoc() {
        return new LocationIndex( -1, Constants.LOC_CT, -1 );
    }

    public LocationIndex GetFirstSensorLoc() {
        return new LocationIndex( 0, Constants.LOC_HD, -1 );
    }

    public LocationIndex GetSecondSensorLoc() {
        return new LocationIndex( 1, Constants.LOC_HD, -1 );
    }

    public LocationIndex GetFirstLSLoc() {
        return new LocationIndex( -1, Constants.LOC_LT, -1 );
    }

    public LocationIndex GetSecondLSLoc() {
        return new LocationIndex( -1, Constants.LOC_RT, -1 );
    }

    public boolean CanUseCommandConsole() {
        return false;
    }

    public boolean HasThirdSensors() {
        return true;
    }

    public LocationIndex GetThirdSensorLoc() {
        return new LocationIndex( -1, Constants.LOC_CT, -1 );
    }

    public SimplePlaceable GetThirdSensors() {
        return ThirdSensors;
    }

    public boolean IsTorsoMounted() {
        return true;
    }
}

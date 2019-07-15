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

import common.CommonTools;
import components.AvailableCode;
import components.Exclusion;
import components.LocationIndex;
import components.MechModifier;
import components.SimplePlaceable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class stCockpitRobotic implements ifCockpit, ifState {
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private SimplePlaceable Sensors = new SimplePlaceable( "Sensors", "Sensors", "Sensors", "Sensors", "Tech Manual", 1, true, AC );
    private SimplePlaceable LifeSupport = new SimplePlaceable( "Life Support", "Life Support", "Life Support", "Life Support", "Tech Manual", 1, true, AC );
    private SimplePlaceable SecondSensors = new SimplePlaceable( "Sensors", "Sensors", "Sensors", "Sensors", "Tech Manual", 1, true, AC );
    private SimplePlaceable SecondLifeSupport = new SimplePlaceable( "Life Support", "Life Support", "Life Support", "Life Support", "Tech Manual", 1, true, AC );

    public stCockpitRobotic() {
        AC.SetISCodes( 'C', 'E', 'X', 'F' );
        AC.SetISDates( 0, 0, false, 2300, 0, 0, false, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetCLCodes( 'C', 'X', 'D', 'E' );
        AC.SetCLDates( 0, 0, false, 2300, 0, 0, false, false );
        AC.SetCLFactions( "", "", "TH", "" );
        AC.SetSuperHeavyCompatible(false);
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public double GetTonnage(int MechTonnage) {
        double result = 3.0f;
        if ( MechTonnage > 10 )
            result += CommonTools.RoundHalfUp(MechTonnage * .05f);
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
        return "Robotic Cockpit";
    }

    public String CritName() {
        return "Robotic Cockpit";
    }

    public String LookupName() {
        return "Robotic Cockpit";
    }

    public String ChatName() {
        return "Robo";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Robotic Cockpit";
    }

    public String BookReference() {
        return "Jihad Final Reckoning";
    }

    public String GetReportName() {
        return "Robotic";
    }

    public double GetCost( int Tonnage, int year ) {
        double result = 5000.0f + ( 10000.0f * GetTonnage(Tonnage) );
        result += Sensors.GetCost();
        result += LifeSupport.GetCost();
        result += SecondSensors.GetCost();
        result += SecondLifeSupport.GetCost();
        return result;
    }
    
    public boolean HasFireControl() {
        return true;
    }

    public double BVMod() {
        return .95f;
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
        return "Robotic Cockpit";
    }

    public LocationIndex GetCockpitLoc() {
        return new LocationIndex( 2, LocationIndex.MECH_LOC_HD, -1 );
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

    public boolean IsTorsoMounted() {
        return false;
    }

    public boolean RequiresGyro() {
        return true;
    }

    public boolean CanArmor() {
        return true;
    }
}

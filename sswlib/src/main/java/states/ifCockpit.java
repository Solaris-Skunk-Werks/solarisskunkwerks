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

public interface ifCockpit {
    public String ActualName();
    public String CritName();
    public String LookupName();
    public String ChatName();
    public String MegaMekName( boolean UseRear );
    public String BookReference();
    public String GetReportName();
    public double GetTonnage( int MechTonnage );
    public LocationIndex GetCockpitLoc();
    public LocationIndex GetFirstSensorLoc();
    public LocationIndex GetSecondSensorLoc();
    public LocationIndex GetThirdSensorLoc();
    public LocationIndex GetFirstLSLoc();
    public LocationIndex GetSecondLSLoc();
    public boolean HasSecondLSLoc();
    public boolean HasThirdSensors();
    public boolean CanUseCommandConsole();
    public SimplePlaceable GetLifeSupport();
    public SimplePlaceable GetSensors();
    public SimplePlaceable GetSecondLifeSupport();
    public SimplePlaceable GetSecondSensors();
    public SimplePlaceable GetThirdSensors();
    public double GetCost( int Tonnage, int year );
    public double BVMod();
    public boolean HasFireControl();
    public boolean IsTorsoMounted();
    public AvailableCode GetAvailability();
    public MechModifier GetMechModifier();
    public int ReportCrits();
    public boolean RequiresGyro();
    public boolean CanArmor();
}

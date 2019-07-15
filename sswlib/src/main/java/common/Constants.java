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

package common;

public class Constants {
    public final static String LibVersion = "0.0.1",
                               BASELOADOUT_NAME = "Base Loadout",
                               WEAPONSFILE = "Data/Equipment/weapons.dat",
                               PHYSICALSFILE = "Data/Equipment/physicals.dat",
                               EQUIPMENTFILE = "Data/Equipment/equipment.dat",
                               QUIRKSFILE = "Data/Equipment/quirks.dat",
                               AMMOFILE = "Data/Equipment/ammunition.dat",
                               CUSTOMWEAPONSFILE = "Data/Equipment/customweapons.dat",
                               CUSTOMPHYSICALSFILE = "Data/Equipment/customphysicals.dat",
                               CUSTOMEQUIPMENTFILE = "Data/Equipment/customequipment.dat",
                               CUSTOMAMMOFILE = "Data/Equipment/customammunition.dat",
                               CUSTOMQUIRKSFILE = "Data/Equipment/customquirks.dat";

    public final static String[] Locs = { "Head", "Center Torso", "Left Torso",
        "Right Torso", "Left Arm", "Right Arm", "Left Leg", "Right Leg" };

    public final static String Print_ForceList = "Print.ForceList",
                        Print_FireDeclaration = "Print.FireDeclaration",
                        Print_Scenario = "Print.Scenario",
                        Print_Recordsheet = "Print.Recordsheets",
                        Print_BattleForce = "Print.BattleForce",
                        Format_Recordsheet = "UseRS",
                        Format_Tables = "UseCharts",
                        Format_CanonPattern = "UseCanonDots",
                        Format_ConvertTerrain = "UseMiniConversion",
                        Format_TerrainModifier = "MiniConversionRate",
                        Format_OneForcePerPage = "OneForcePerPage",
                        Format_BattleForceSheetChoice = "BattleForceSheetIndex",
                        Format_RecordsheetChoice = "RecordsheetIndex",
                        SSWPrefs = "/com/sswsuite/ssw",
                        BFBPrefs = "/com/sswsuite/bfb",
                        SGTPrefs = "/com/sswsuite/sgt",
                        SAWPrefs = "/com/sswsuite/saw",
                        SSVPrefs = "/com/sswsuite/ssv";
    public final static int BattleMech = 0,
                            IndustrialMech = 1,
                            ProtoMech = 2,
                            Vehicle = 3,
                            Infantry = 4,
                            BattleArmor = 5,
                            ConvFighter = 6,
                            AeroFighter = 7,
                            SmallCraft = 8,
                            Dropship = 9,
                            SupportVehicle = 10,
                            MobileStructure = 11;
}

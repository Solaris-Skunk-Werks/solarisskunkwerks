/*
Copyright (c) 2008, George Blouin Jr. (skyhigh@solaris7.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.
    * Neither the name of George Blouin Jr nor the names of contributors may be
used to endorse or promote products derived from this software without specific prior
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package BFB.Common;

public class Constants {
    public final static String AppName = "Battletech Force Balancer",
                        AppDescription = "Battletech Force Balancer",
                        Version = "0.0.3",
                        AppRelease = "Experimental",
                        Author = "George Blouin",
                        EMail = "george.blouin@gmail.com",
                        Print_ForceList = "Print.ForceList",
                        Print_FireDeclaration = "Print.FireDeclaration",
                        Print_Recordsheet = "Print.Recordsheets",
                        Print_BattleForce = "Print.BattleForce",
                        Format_Recordsheet = "UseRS",
                        Format_Tables = "UseCharts",
                        Format_CanonPattern = "UseCanonDots",
                        Format_ConvertTerrain = "UseMiniConversion",
                        Format_TerrainModifier = "MiniConversionRate",
                        Format_OneForcePerPage = "OneForcePerPage";
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
    public final static String[] UnitTypes = { "BattleMech", "IndustrialMech",
                                 "ProtoMech", "Combat Vehicle", "Infantry",
                                 "Battle Armor", "Conventional Fighter",
                                 "Aerospace Fighter", "Small Craft", "Dropship",
                                 "Support Vehicle", "Mobile Structure" };
    public final static String NL = System.getProperty( "line.separator" );
    public final static String Tab = "\t";
}

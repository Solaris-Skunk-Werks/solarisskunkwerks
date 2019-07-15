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
import components.MechModifier;

/**
 * Inner Sphere Super Heavy Endo Steel Biped Chassis
 */
public class stChassisSHESBP implements ifChassis, ifState {
    public final static double[] Masses = { 10.5, 11.0, 11.5, 12.0, 12.5, 13.0, 13.5, 14.0, 14.5, 15.0, 15.5, 16.0, 16.5, 17, 17.5, 18.0, 18.5, 19.0, 19.5, 20.0 };
    private final static int[][] IntPoints = {
        { 32, 22, 17, 22 },
        { 33, 23, 18, 23 },
        { 35, 24, 19, 24 },
        { 36, 25, 20, 25 },
        { 38, 26, 21, 26 },
        { 39, 27, 21, 27 },
        { 41, 28, 22, 28 },
        { 42, 29, 23, 29 },
        { 44, 31, 24, 31 },
        { 45, 32, 25, 32 },
        { 47, 33, 26, 33 },
        { 48, 34, 26, 34 },
        { 50, 35, 27, 35 },
        { 51, 36, 28, 36 },
        { 53, 37, 29, 37 },
        { 54, 38, 30, 38 },
        { 56, 39, 31, 39 },
        { 57, 40, 31, 40 },
        { 59, 41, 32, 41 },
        { 60, 42, 33, 42 } };
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );

    public stChassisSHESBP() {
        AC.SetISCodes( 'E', 'X', 'X', 'F', 'F' );
        AC.SetISDates( 0, 0, false, 3076, 0, 0, false, false );
        AC.SetISFactions( "", "", "WoB", "" );
        AC.SetSuperHeavyOnly(true);
        AC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public boolean HasCounterpart() {
        return false;
    }

    public int GetCrits() {
        return 7;
    }
    
    public String ActualName() {
        return "Super Heavy Endo Steel Structure (Biped)";
    }

    public String CritName() {
        return "Super Heavy Endo Steel";
    }

    public String LookupName() {
        return "Super Heavy Endo Steel Structure";
    }

    public String ChatName() {
        return "Std Super Hvy Endo Steel";
    }

    public String MegaMekName( boolean UseRear ) {
        return "Standard Super Heavy Endo Steel";
    }

    public String BookReference() {
        return "Interstellar Operations";
    }

    public double GetStrucTon( int Tonnage, boolean fractional ) {
        return Masses[GetIndex(Tonnage)];
    }
    
    public boolean IsQuad() {
        return false;
    }
    
    /**
     * Determines if this Chassis is a Tripod
     * @return True if this chassis is a Tripod
     */
    public boolean IsTripod() {
        return false;
    }
    
    public int GetHeadPoints() {
        // All super heavy mech heads have 4 internal structure points
        return 4;
    }

    public int GetCTPoints( int Tonnage ) {
        return IntPoints[GetIndex(Tonnage)][0];
    }

    public int GetSidePoints( int Tonnage ) {
        return IntPoints[GetIndex(Tonnage)][1];
    }
    
    public int GetArmPoints( int Tonnage ) {
        return IntPoints[GetIndex(Tonnage)][2];
    }

    public int GetLegPoints( int Tonnage ) {
        return IntPoints[GetIndex(Tonnage)][3];
    }
    
    public double GetCost( int Tonnage ) {
        return 400 * Tonnage;
    }
    
    public double GetBVMult() {
        return 1.0f;
    }
    
    public boolean IncrementPlaced() {
        return true;
    }

    public boolean DecrementPlaced() {
        return true;
    }

    public AvailableCode GetAvailability() {
        return AC;
    }

    private int GetIndex( int Tonnage ) {
        return (Tonnage - 100) / 5 - 1;
    }

    public MechModifier GetMechModifier() {
        return null;
    }

    // toString
    @Override
    public String toString() {
        return "Endo Steel Super Heavy";
    }

    public int GetCVPoints(int Tonnage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

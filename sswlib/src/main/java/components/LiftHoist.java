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

/**
 *
 * @author gblouin
 */
public class LiftHoist extends Equipment implements ifEquipment {
    private final ifUnit Owner;
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    public LiftHoist(ifUnit l)
    {
        AC.SetISCodes( 'A', 'A', 'A', 'A', 'A' );
        AC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetISFactions( "", "", "", "" );
        AC.SetCLCodes( 'A', 'X', 'A', 'A', 'A' );
        AC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetCLFactions( "", "", "", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetPrimitiveOnly(false);
        AC.SetSuperHeavyCompatible(true);
        AC.SetSuperHeavyOnly(false);
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Owner = l;

        SetMountableRear(true);
    }

    public String ActualName() {
        return "Lift Hoist";
    }

    public String CritName() {
        return NameModifier() + "Lift Hoist";
    }

    @Override
    public boolean IsVariableSize() {
        return false;
    }

    @Override
    public boolean HasAmmo() {
        return false;
    }

    @Override
    public int GetAmmoIndex() {
        return 0;
    }

    public String LookupName() {
            return CritName();
    }

    public String ChatName() {
        return "LftHst";
    }

    public String MegaMekName( boolean UseRear ) {
        return CritName();
    }

    public String BookReference() {
        return "Tech Manual";
    }

    @Override
    public int NumCrits() {
        return 3;
    }

    public int NumCVSpaces() {
        return 1;
    }

    @Override
    public double GetTonnage() {
        return 3.0;
    }

    @Override
    public double GetCost() {
       return 5000;
    }

    public double GetOffensiveBV() {
        return 0.0;
    }

    @Override
    public double GetCurOffensiveBV(boolean UseRear, boolean UseTC, boolean UseAES) {
        return 0;
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
        return AC.Clone();
    }

    @Override
    public boolean CoreComponent() {
        return false;
    }

    @Override
    public String toString() {
        return CritName();
    }

    @Override
    public int MaxAllowed() {
        if (Owner instanceof CombatVehicle) {
            return 4;
        }
        return 2;
    }

    @Override
    public void Validate(Mech m) throws Exception {
        if (MaxAllowed() == 0) {
            return;
        }

        int count = 0;
        for (Object item : m.GetLoadout().GetEquipment()) {
            abPlaceable currentItem = (abPlaceable) item;
            if (currentItem.LookupName().equals(LookupName())) {
                ++count;
                if (count == MaxAllowed()) {
                    throw new Exception("Only " + MaxAllowed() + " " + CritName() + "(s) may be mounted on one 'Mech.");
                }
            }
        }
    }

    @Override
    public void Validate(CombatVehicle v) throws Exception {
        if (MaxAllowed() == 0) {
            return;
        }

        int count = 0;
        for(Object item : v.GetLoadout().GetEquipment()) {
            abPlaceable currentItem = (abPlaceable) item;
            if( currentItem.ActualName().equals( ActualName() ) ) {
                ++count;
                if( count == MaxAllowed() ) {
                    throw new Exception("Only " + MaxAllowed() + " " + CritName() + "(s) may be mounted.");
                }
            }
        }
    }

    @Override
    public boolean CanAllocCVBody() {
        if (Owner instanceof CombatVehicle){
            return ((CombatVehicle) Owner).IsVTOL();
        }
        return false;
    }
}

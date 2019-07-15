/*
Copyright (c) 2010, Justin R. Bengtson (poopshotgun@yahoo.com)
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

public class DroneOperatingSystem extends Equipment {
    private Mech Owner;
    private AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );

    public DroneOperatingSystem( Mech m ) {
        Owner = m;
        AC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED );
        AC.SetISCodes( 'C', 'E', 'F', 'F' );
        AC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetISFactions( "", "", "ES", "" );
        AC.SetCLCodes( 'C', 'X', 'D', 'E' );
        AC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetCLFactions( "", "", "ES", "" );
    }

    public void SetOwner( Mech m ) {
        Owner = m;
    }

    @Override
    public String ActualName() {
        return "Drone Operating System";
    }

    @Override
    public String CritName() {
        return "Drone OS";
    }

    @Override
    public String LookupName() {
        return "Drone Operating System";
    }

    @Override
    public String ChatName() {
        return "DroneOS";
    }

    @Override
    public String MegaMekName( boolean UseRear ) {
        return "Drone Operating System";
    }

    @Override
    public String BookReference() {
        return "Tactical Operations";
    }

    @Override
    public double GetTonnage() {
        return (Owner.GetTonnage() * .1) + .5;
    }

    @Override
    public int NumCrits() {
        return 1;
    }

    @Override
    public double GetCost() {
        return Math.ceil(5000f + ( 10000 * GetTonnage() ));
    }

    @Override
    public AvailableCode GetAvailability() {
        return AC;
    }

    @Override
    public boolean RequiresQuad(){
        return true;
    }
}

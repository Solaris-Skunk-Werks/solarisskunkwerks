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
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package components;

import java.util.ArrayList;

abstract class AbstractPowerAmplifier {

    protected final ifLoadout CurLoadout;
    protected final AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );

    protected AbstractPowerAmplifier( ifLoadout l ) {
        CurLoadout = l;
    }

    public AvailableCode GetAvailability() {
        return AC;
    }

    public double GetTonnage() {
        double tons = GetPoweredEquipmentTonnage();
        if( tons <= 0.0 ) {
            return 0.0;
        }

        if( CurLoadout.GetUnit().UsingFractionalAccounting() ) {
            return Math.ceil( tons * 100 ) * 0.001;
        }
        return Math.ceil( tons * 0.2 ) * 0.5;
    }

    private double GetPoweredEquipmentTonnage() {
        double tons = 0.0;
        ArrayList<abPlaceable> v = CurLoadout.GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            Object item = v.get( i );
            if( item instanceof ifWeapon ) {
                if( ( (ifWeapon) item ).RequiresPowerAmps() ) {
                    tons += ( (abPlaceable) item ).GetTonnage();
                }
            } else if( item instanceof Equipment ) {
                if( ( (Equipment) item ).RequiresPowerAmps() ) {
                    tons += ( (Equipment) item ).GetTonnage();
                }
            }
        }
        return tons;
    }

    public double GetCost() {
        return GetTonnage() * 20000.0;
    }

    @Override
    public String toString() {
        return "Power Amplifiers";
    }
}

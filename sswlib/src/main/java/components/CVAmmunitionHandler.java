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

import java.util.Vector;

public class CVAmmunitionHandler {
    private Vector<Ammunition> Ammo = new Vector<Ammunition>();
    private CVLoadout Owner;

    public CVAmmunitionHandler( CVLoadout c ) {
        Owner = c;
    }

    public void AddAmmo( Ammunition a ) {
        Ammo.add( a );
    }

    public void RemoveAmmo( Ammunition a ) {
        Ammo.remove( a );
    }

    public Vector<Ammunition> GetAmmo() {
        return Ammo;
    }

    public double GetTonnage() {
        double retval = 0.0;
        for( int i = 0; i < Ammo.size(); i++ ) {
            retval += Ammo.get( i ).GetTonnage();
        }
        return retval;
    }

    public int AmmoSpace() {
        Vector<Ammunition> check = (Vector<Ammunition>) Ammo.clone();
        int curIDX = 0, retval = 0;
        while( check.size() > 0 ) {
            curIDX = check.lastElement().GetAmmoIndex();
            check.remove( check.lastElement() );
            retval++;
            for( int i = check.size() - 1; i > -1; i-- ) {
                if( check.get( i ).GetAmmoIndex() == curIDX ) {
                    check.removeElementAt( i );
                }
            }
        }
        return retval;
    }

    @Override
    public String toString() {
        return "Total Ammo (" + String.format( "%1$3.1f", GetTonnage() ) + " tons, " + AmmoSpace() + " spaces)";
    }
}
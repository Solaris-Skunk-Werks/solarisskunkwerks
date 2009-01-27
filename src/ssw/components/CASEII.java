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

package ssw.components;

import ssw.Constants;

/**
 *
 * @author justin
 */
public class CASEII extends abPlaceable {
    // A simple class for CASE II.
    private AvailableCode ISAC = new AvailableCode( false, 'E', 'X', 'X', 'F', 3064, 0, 0, "FW", "", false, false, 3057, true, "FW", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL ),
                          CLAC = new AvailableCode( true, 'F', 'X', 'X', 'F', 3062, 0, 0, "CCY", "", false, false, 3059, true, "CCY", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
    private ifLoadout Owner;

    public CASEII( ifLoadout l ) {
        Owner = l;
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    @Override
    public boolean CanArmor() {
        return false;
    }

    public String GetCritName() {
        return "C.A.S.E. II";
    }

    public String GetMMName( boolean UseRear ) {
        if( Owner.GetMech().IsClan() ) {
            return "CLCASEII";
        } else {
            return "ISCASEII";
        }
    }

    public int NumCrits() {
        return 1;
    }

    public float GetTonnage() {
        if( Owner.GetMech().IsClan() ) {
            return 0.5f;
        } else {
            return 1.0f;
        }
    }

    public float GetOffensiveBV() {
        return 0.0f;
    }

    public float GetCurOffensiveBV( boolean UseRear ) {
        return 0.0f;
    }

    public float GetDefensiveBV() {
        return 0.0f;
    }

    public float GetCost() {
        return 175000.0f;
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public boolean IsCritable() {
        return false;
    }

    // All placeables should be able to return their AvailabileCode
    public AvailableCode GetAvailability() {
        if( Owner.GetMech().IsClan() ) {
            return CLAC;
        } else {
            return ISAC;
        }
    }

    @Override
    public String toString() {
        return "C.A.S.E. II";
    }
}

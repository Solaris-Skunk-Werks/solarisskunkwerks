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

public class EmptyItem extends abPlaceable {
    // this is an empty item.  it takes up space in the loadout
    private final static AvailableCode AC = new AvailableCode( false, 'A', 'A', 'A', 'A',
        1900, 0, 0, "", "", false, false );

    @Override
    public boolean LocationLocked() {
        return false;
    }

    // returns the name of this item in the Loadout.
    public String GetCritName() {
        return "Roll Again";
    }

    public String GetMMName( boolean UseRear ) {
        return "-Empty-";
    }

    // returns the number of crits this item takes in the Loadout.
    public int NumCrits() {
        return 1;
    }

    @Override
    public boolean CanArmor() {
        // empty items can never be armored
        return false;
    }

    public float GetTonnage() {
        return 0.0f;
    }

    public float GetOffensiveBV() {
        // small items such as these do not have battle balues
        return 0.0f;
    }

    public float GetCurOffensiveBV( boolean UseRear ) {
        // BV will not change for this item, so just return the normal value
        return 0.0f;
    }

    public float GetDefensiveBV() {
        return 0.0f;
    }

    public float GetCost() {
        return 0.0f;
    }

    // All placeables should be able to return their AvailabileCode
    public AvailableCode GetAvailability() {
        return AC;
    }

    @Override
    public boolean CoreComponent() {
        // simple placeables should be considered core components
        return true;
    }

    @Override
    public boolean IsCritable() {
        return false;
    }

    @Override
    public String toString() {
        return "- roll again";
    }
}

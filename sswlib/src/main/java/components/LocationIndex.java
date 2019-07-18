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

import java.util.ArrayList;

public class LocationIndex {
    // this is a convenience method for the XML saving and loading routine
    // it provides a location index for any given item.
    public int Index = -1;
    public int Location = -1;
    public int Number = 1; // used for contiguous split items.  otherwise
                            // should be ignored

    public static final int MECH_LOC_HD = 0,
                            MECH_LOC_CT = 1,
                            MECH_LOC_LT = 2,
                            MECH_LOC_RT = 3,
                            MECH_LOC_LA = 4,
                            MECH_LOC_RA = 5,
                            MECH_LOC_LL = 6,
                            MECH_LOC_RL = 7,
                            MECH_LOC_CL = 11,
                            MECH_LOC_CTR = 8,
                            MECH_LOC_LTR = 9,
                            MECH_LOC_RTR = 10,
                            CV_LOC_FRONT = 0,
                            CV_LOC_LEFT = 1,
                            CV_LOC_RIGHT = 2,
                            CV_LOC_REAR = 3,
                            CV_LOC_TURRET1 = 4,
                            CV_LOC_TURRET2 = 5,
                            CV_LOC_ROTOR = 6,
                            CV_LOC_BODY = 7;
    public final static String[] MechLocs = { "Head", "Center Torso", "Left Torso",
        "Right Torso", "Left Arm", "Right Arm", "Left Leg", "Right Leg", "Center Leg" },
                                 CVLocs = { "Front", "Left", "Right", "Rear",
        "Turret", "Rear Turret", "Rotor", "Body" };

    public LocationIndex() {}

    public LocationIndex( int idx, int loc, int num ) {
        Index = idx;
        Location = loc;
        Number = num;
    }

    public void SetFirst( LocationIndex l ) {
        // this method determines if the given index is before the one given
        // and then sets the index accordingly.  If the locations do not match,
        // the given index is discarded.
        if( l.Location == Location ) {
            if( l.Index < Index &! ( l.Index == -1 ) ) {
                Index = l.Index;
            }
        }
    }

    public static int FindIndex( CombatVehicle c, String location ) {
        if ( location.equals("Front")) return CV_LOC_FRONT;
        if ( location.equals("Left")) return CV_LOC_LEFT;
        if ( location.equals("Right")) return CV_LOC_RIGHT;
        if ( location.equals("Rear")) return CV_LOC_REAR;
        if ( location.equals("Turret")) return CV_LOC_TURRET1;
        if ( location.equals("Rear Turret")) return CV_LOC_TURRET2;
        if ( location.equals("Body")) return CV_LOC_BODY;
        return 11;
    }
}

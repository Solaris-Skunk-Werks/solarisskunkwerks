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

/**
 *
 * @author Michael Mills
 */
public class IndustrialPhysicalWeapon extends PhysicalWeapon {
    public IndustrialPhysicalWeapon (String name, String lookup, String mname, Mech m, AvailableCode a){
        super(name, lookup, mname, m, a);
        this.resetAllocations(m);
        this.SetReplacesHand(true);
        this.SetRequiresLowerArm(true);
        this.SetPWClass( PhysicalWeapon.PW_CLASS_INDUSTRIAL );
    }

    public void resetAllocations(Mech m)
    {
        if(m.IsQuad()){
            this.SetAllocations(false, false, true, false, false, false);
            this.SetReplacesHand(false);
            this.SetRequiresLowerArm(false);
        }
        else{
            this.SetAllocations(false, false, false, true, false, false);
            this.SetReplacesHand(true);
            this.SetRequiresLowerArm(true);
        }
    }

    public void SetSpecials( int cost, int obv, int dbv ) {
        this.SetSpecials( "PA", "-", 0, (double) cost, 0, (double) obv, (double) dbv, false );
    }
}

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
import components.Mech;
import components.MechModifier;

public interface ifEngine {
    public String ActualName();
    public String CritName();
    public String LookupName();
    public String ChatName();
    public String MegaMekName( boolean UseRear );
    public String BookReference();
    public int GetFullCrits();
    public boolean IsFusion();
    public boolean IsNuclear();
    public double GetTonnage( int Rating, boolean fractional );
    public int GetBFStructure( int tonnage );
    public int GetCTCrits();
    public int GetSideTorsoCrits();
    public int NumCTBlocks();
    public int NumCVSpaces();
    public int LargeCVSpaces();
    public boolean CanSupportRating( int rate, Mech m );
    public double GetCost( int MechTonnage, int Rating );
    public double GetBVMult();
    public AvailableCode GetAvailability();
    public int FreeHeatSinks();
    public int MaxMovementHeat();
    public int MinimumHeat();
    public int JumpingHeatMultiplier();
    public boolean IsPrimitive();
    public MechModifier GetMechModifier();
}

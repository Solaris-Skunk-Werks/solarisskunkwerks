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

package Print;

import java.awt.Point;

public interface ifPrintPoints {
    // provides common printing points on a record sheet
    public Point[] GetCritHDPoints();
    public Point[] GetCritCTPoints();
    public Point[] GetCritLTPoints();
    public Point[] GetCritRTPoints();
    public Point[] GetCritLAPoints();
    public Point[] GetCritRAPoints();
    public Point[] GetCritLLPoints();
    public Point[] GetCritRLPoints();
    public Point[] GetArmorHDPoints();
    public Point[] GetArmorCTPoints();
    public Point[] GetArmorCTRPoints();
    public Point[] GetArmorLTPoints();
    public Point[] GetArmorLTRPoints();
    public Point[] GetArmorRTPoints();
    public Point[] GetArmorRTRPoints();
    public Point[] GetArmorLAPoints();
    public Point[] GetArmorRAPoints();
    public Point[] GetArmorLLPoints();
    public Point[] GetArmorRLPoints();
    public Point[] GetInternalHDPoints();
    public Point[] GetInternalCTPoints();
    public Point[] GetInternalLTPoints();
    public Point[] GetInternalRTPoints();
    public Point[] GetInternalLAPoints();
    public Point[] GetInternalRAPoints();
    public Point[] GetInternalLLPoints();
    public Point[] GetInternalRLPoints();
    public Point[] GetInternalInfoPoints();
    public Point[] GetArmorInfoPoints();
    public Point[] GetWeaponChartPoints();
    public Point[] GetDataChartPoints();
    public Point[] GetHeatSinkPoints();
    public Point GetMechImageLoc();
    public Point GetImageBounds();
    public Point GetLogoImageLoc();
    
    public Point[] GetArmorFrontPoints();
    public Point[] GetArmorLeftPoints();
    public Point[] GetArmorRightPoints();
    public Point[] GetArmorRearPoints();
    public Point[] GetArmorTurretPoints();
    public Point[] GetArmorTurret2Points();
    public Point[] GetArmorRotorPoints();
    public Point[] GetInternalFrontPoints();
    public Point[] GetInternalLeftPoints();
    public Point[] GetInternalRightPoints();
    public Point[] GetInternalRearPoints();
    public Point[] GetInternalTurretPoints();
    public Point[] GetInternalTurret2Points();
    public Point[] GetInternalRotorPoints();
}

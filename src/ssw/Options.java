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

package ssw;

import java.awt.Color;

public class Options {
    // This is just a holder for a bunch of public variables that everybody
    // should be able to get to.  Individual routines should never change the
    // contained variables, only the Options Dialog should do that.
    public final int HEAD_MAX = 0,
                     HEAD_EQUAL = 1,
                     PRIORITY_TORSO = 0,
                     PRIORITY_ARMS = 1,
                     PRIORITY_LEGS = 2,
                     EXPORT_SORT_OUT = 0,
                     EXPORT_SORT_IN = 1;
    public boolean Armor_CustomPercentage = false,
                   Heat_RemoveOSWeapons = false,
                   Heat_RemoveRearWeapons = false,
                   Heat_UAC_RAC_FullRate = false,
                   Heat_RemoveStealthArmor = false,
                   Heat_RemoveJumps = false,
                   Heat_RemoveMovement = false,
                   Heat_RemoveEquipment = false,
                   Equip_AllowRBlade = false,
                   Export_AmmoAtEnd = true,
                   UseMMCustom = false,
                   LoadLastMech = false;
    public int Armor_CTRPercent = 25,
               Armor_STRPercent = 25,
               Armor_Priority = PRIORITY_TORSO,
               Armor_Head = HEAD_MAX,
               Export_Sort = EXPORT_SORT_OUT,
               DefaultRules = Constants.TOURNAMENT,
               DefaultEra = Constants.STAR_LEAGUE,
               DefaultTechbase = Constants.INNER_SPHERE,
               DefaultHeatSinks = Constants.SINGLE_HEATSINK,
               S7UserID = -1;
    public Color bg_LOCKED = new Color( 0, 0, 0 ),
                 fg_LOCKED = new Color( 204, 255, 255 ),
                 bg_ARMORED = new Color( 153, 153, 153 ),
                 fg_ARMORED = new Color( 255, 255, 255 ),
                 bg_LINKED = new Color( 200, 200, 200 ),
                 fg_LINKED = new Color( 0, 0, 0 ),
                 bg_NORMAL = new Color( 102, 255, 255 ),
                 fg_NORMAL = new Color( 0, 0, 0 ),
                 bg_EMPTY = new Color( 153, 255, 153 ),
                 fg_EMPTY = new Color( 0, 0, 0 ),
                 bg_HILITE = new Color( 255, 255, 204 ),
                 fg_HILITE = new Color( 0, 0, 0 );
    public String S7Callsign = "null",
                  S7Password = "null", // needs to be encrypted
                  SaveLoadPath = "none",
                  HTMLPath = "none",
                  TXTPath = "none",
                  MegamekPath = "none",
                  AmmoNameFormat = "@%P (%L)";

    public void SetDefaults() {
        // choose to keep this here because it made sense.
        Armor_CustomPercentage = false;
        Heat_RemoveOSWeapons = false;
        Heat_RemoveRearWeapons = false;
        Heat_UAC_RAC_FullRate = false;
        Heat_RemoveEquipment = false;
        Heat_RemoveStealthArmor = false;
        Heat_RemoveJumps = false;
        Heat_RemoveMovement = false;
        Armor_CTRPercent = 25;
        Armor_STRPercent = 25;
        Armor_Priority = PRIORITY_TORSO;
        Armor_Head = HEAD_MAX;
        DefaultRules = Constants.TOURNAMENT;
        DefaultEra = Constants.STAR_LEAGUE;
        DefaultTechbase = Constants.INNER_SPHERE;
        DefaultHeatSinks = Constants.SINGLE_HEATSINK;
        Equip_AllowRBlade = false;
        Export_Sort = EXPORT_SORT_OUT;
        Export_AmmoAtEnd = true;
        bg_LOCKED = new Color( 0, 0, 0 );
        fg_LOCKED = new Color( 204, 255, 255 );
        bg_LINKED = new Color( 200, 200, 200 );
        fg_LINKED = new Color( 0, 0, 0 );
        bg_ARMORED = new Color( 153, 153, 153 );
        fg_ARMORED = new Color( 255, 255, 255 );
        bg_NORMAL = new Color( 102, 255, 255 );
        fg_NORMAL = new Color( 0, 0, 0 );
        bg_EMPTY = new Color( 153, 255, 153 );
        fg_EMPTY = new Color( 0, 0, 0 );
        bg_HILITE = new Color( 255, 255, 204 );
        fg_HILITE = new Color( 0, 0, 0 );
        SaveLoadPath = System.getProperty("user.dir");
        HTMLPath = System.getProperty("user.dir");
        TXTPath = System.getProperty("user.dir");
        MegamekPath = System.getProperty("user.dir");
        AmmoNameFormat = "@%P (%L)";
        UseMMCustom = false;
        LoadLastMech = false;
    }

    public void ClearUserInfo() {
        S7Callsign = "null";
        S7Password = "null";
        S7UserID = -1;
    }
}

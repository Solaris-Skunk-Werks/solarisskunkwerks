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

package saw;

public class Constants {
    // Constants for the program

    // here is the versioning and program name
    public final static String AppName = "SAW",
                        AppDescription = "Solaris Armor Werks",
                        Version = "0.7",
                        AppRelease = "Experimental",
                        ImageListFileName = "S7Images",
                        LogFileName = "Logs/SAW_Log.txt",
                        HTMLTemplateName = "Data/Templates/Vee_HTML.html",
                        DEFAULT_ENGINE = "Fusion Engine",
                        DEFAULT_ARMOR = "Standard Armor",
                        Solaris7URL = "http://www.solaris7.com/service/index.asp",
                        BASELOADOUT_NAME = "Base Loadout";
    public final static int ART4_NONE = 0,
                     ART4_ART_4 = 1,
                     ART4_ART_5 = 2,
                     ART4_APOLLO = 3;
    public final static int SCREEN_SIZE_NORMAL = 0,
                            SCREEN_SIZE_WIDE_1280 = 1;
}

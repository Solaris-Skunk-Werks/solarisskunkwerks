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

package ssw.constants;

import java.util.Properties;

public class SSWConstants {
    // SSWConstants for the program

    // here is the versioning and program name
    public final static String AppName = "SSW",
                        AppDescription = "Solaris Skunk Werks",
                        ImageListFileName = "Data/Solaris7/S7Images",
                        LogDirectoryName = "Logs",
                        LogFileName = "SSW_Log.txt",
                        HTMLTemplateName = "Data/Templates/Mech_HTML.html",
                        DEFAULT_CHASSIS = "Standard Structure",
                        DEFAULT_ENGINE = "Fusion Engine",
                        DEFAULT_GYRO = "Standard Gyro",
                        DEFAULT_COCKPIT = "Standard Cockpit",
                        DEFAULT_ENHANCEMENT = "No Enhancement",
                        DEFAULT_HEATSINK = "Single Heat Sink",
                        DEFAULT_JUMPJET = "Standard Jump Jet",
                        DEFAULT_ARMOR = "Standard Armor",
                        Solaris7URL = "http://www.solaris7.com/service/index.asp",
                        PrefsNodeName = "/com/sswsuite/ssw";
    public final static int SCREEN_SIZE_NORMAL = 0,
                            SCREEN_SIZE_WIDE_1280 = 1;

    public static String GetVersion() {
        Properties props = new Properties();
        try {
            props.load(SSWConstants.class.getResourceAsStream("/ssw/build.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String ver = props.getProperty("version");
        if (!props.getProperty("release").equals("Stable")) {
            ver += "-" + props.getProperty("rev");
        }
        return ver;
    }

    public static String GetRelease() {
        Properties props = new Properties();
        try {
            props.load(SSWConstants.class.getResourceAsStream("/ssw/build.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props.getProperty("release");
    }
}

/*
Copyright (c) 2008, George Blouin Jr. (skyhigh@solaris7.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.
    * Neither the name of George Blouin Jr nor the names of contributors may be
used to endorse or promote products derived from this software without specific prior
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ssw.Force.Common;

import java.awt.Font;
import java.io.File;

public class CommonTools {
    private final static double[][] BVMods = {
        { 2.8, 2.63, 2.45, 2.28, 2.01, 1.82, 1.75, 1.67, 1.59 },
        { 2.56, 2.4, 2.24, 2.08, 1.84, 1.6, 1.58, 1.51, 1.44 },
        { 2.24, 2.1, 1.96, 1.82, 1.61, 1.4, 1.33, 1.31, 1.25 },
        { 1.92, 1.8, 1.68, 1.56, 1.38, 1.2, 1.14, 1.08, 1.06 },
        { 1.6, 1.5, 1.4, 1.3, 1.15, 1.0, 0.95, 0.9, 0.85 },
        { 1.5, 1.35, 1.26, 1.17, 1.04, 0.9, 0.86, 0.81, 0.77 },
        { 1.43, 1.33, 1.19, 1.11, 0.98, 0.85, 0.81, 0.77, 0.72 },
        { 1.36, 1.26, 1.16, 1.04, 0.92, 0.8, 0.76, 0.72, 0.68 },
        { 1.28, 1.19, 1.1, 1.01, 0.86, 0.75, 0.71, 0.68, 0.64 }
    };

    public final static String tab = "    ";
    public static Font BoldFont = new Font( "Arial", Font.BOLD, 8 );
    public static Font PlainFont = new Font( "Arial", Font.PLAIN, 8 );
    public static Font ItalicFont = new Font( "Arial", Font.ITALIC, 8 );
    public static Font SmallFont = new Font( "Arial", Font.PLAIN, 7 );
    public static Font SmallItalicFont = new Font( "Arial", Font.ITALIC, 7 );
    public static Font TitleFont = new Font("Arial", Font.BOLD, 14);
    public static Font SectionHeaderFont = new Font("Arial", Font.BOLD, 12);

    public static double GetSkillBV( double BV, int Gunnery, int Piloting ) {
        return BV * BVMods[Gunnery][Piloting];
    }

    public static double GetModifierBV( double SkillBV, double Modifier) {
        return SkillBV * Modifier;
    }

    public static double GetFullAdjustedBV( double BV, int Gunnery, int Piloting, double Modifier ) {
        return BV * BVMods[Gunnery][Piloting] * Modifier;
    }

    public static double GetForceSizeMultiplier( int Force1Size, int Force2Size )
    {
        if( Force1Size <= 0 || Force2Size <= 0 ) {
            return 0;
        }

        return ( ((double) Force2Size) / ((double) Force1Size) ) + ( ((double) Force1Size) / ((double) Force2Size) ) - 1.0f;
    }

    public static String SafeFileName(String filename) {
        return filename.replace(" ", "%20").replace("'", "");
    }

    public static String GetSafeFilename(String s) {
        s = s.replaceAll("%", "%25");
        s = s.replaceAll(" ", "%20");
        s = s.replaceAll("!", "%21");
        s = s.replaceAll("[{(}]", "%28");
        s = s.replaceAll("[{)}]", "%29");
        s = s.replaceAll("[{;}]", "%3B");
        s = s.replaceAll("[{:}]", "%3A");
        s = s.replaceAll("[{@}]", "%40");
        s = s.replaceAll("[{&}]", "%26");
        s = s.replaceAll("[{=}]", "%3D");
        s = s.replaceAll("[{+}]", "%2B");
        s = s.replaceAll("[{$}]", "%24");
        s = s.replaceAll("[{?}]", "%3F");
        s = s.replaceAll("[{,}]", "%2C");
        s = s.replaceAll("[{#}]", "%23");
        s = s.replaceAll("[{\\[}]", "%5B");
        s = s.replaceAll("[{\\]}]", "%5D");
        s = s.replaceAll("[{*}]", "%2A");
        return s;
    }

    public static String FormatFileName(String filename) {
        return filename.replace("'", "").replace(" ", "_");
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}

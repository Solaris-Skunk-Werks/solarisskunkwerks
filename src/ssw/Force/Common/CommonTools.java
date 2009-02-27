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
    private final static float[][] BVMods = {
        { 2.8f, 2.63f, 2.45f, 2.28f, 2.01f, 1.82f, 1.75f, 1.67f, 1.59f },
        { 2.56f, 2.4f, 2.24f, 2.08f, 1.84f, 1.6f, 1.58f, 1.51f, 1.44f },
        { 2.24f, 2.1f, 1.96f, 1.82f, 1.61f, 1.4f, 1.33f, 1.31f, 1.25f },
        { 1.92f, 1.8f, 1.68f, 1.56f, 1.38f, 1.2f, 1.14f, 1.08f, 1.06f },
        { 1.6f, 1.5f, 1.4f, 1.3f, 1.15f, 1.0f, 0.95f, 0.9f, 0.85f },
        { 1.5f, 1.35f, 1.26f, 1.17f, 1.04f, 0.9f, 0.86f, 0.81f, 0.77f },
        { 1.43f, 1.33f, 1.19f, 1.11f, 0.98f, 0.85f, 0.81f, 0.77f, 0.72f },
        { 1.36f, 1.26f, 1.16f, 1.04f, 0.92f, 0.8f, 0.76f, 0.72f, 0.68f },
        { 1.28f, 1.19f, 1.1f, 1.01f, 0.86f, 0.75f, 0.71f, 0.68f, 0.64f }
    };

    public final static String tab = "    ";
    public static Font BoldFont = new Font( "Arial", Font.BOLD, 8 );
    public static Font PlainFont = new Font( "Arial", Font.PLAIN, 8 );
    public static Font ItalicFont = new Font( "Arial", Font.ITALIC, 8 );
    public static Font SmallFont = new Font( "Arial", Font.PLAIN, 7 );
    public static Font SmallItalicFont = new Font( "Arial", Font.ITALIC, 7 );
    public static Font TitleFont = new Font("Arial", Font.BOLD, 14);
    public static Font SectionHeaderFont = new Font("Arial", Font.BOLD, 12);

    public static float GetSkillBV( float BV, int Gunnery, int Piloting ) {
        return BV * BVMods[Gunnery][Piloting];
    }

    public static float GetModifierBV( float SkillBV, float Modifier) {
        return SkillBV * Modifier;
    }

    public static float GetFullAdjustedBV( float BV, int Gunnery, int Piloting, float Modifier ) {
        return BV * BVMods[Gunnery][Piloting] * Modifier;
    }

    public static float GetForceSizeMultiplier( int Force1Size, int Force2Size )
    {
        if( Force1Size <= 0 || Force2Size <= 0 ) {
            return 0;
        }

        return ( ((float) Force2Size) / ((float) Force1Size) ) + ( ((float) Force1Size) / ((float) Force2Size) ) - 1.0f;
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

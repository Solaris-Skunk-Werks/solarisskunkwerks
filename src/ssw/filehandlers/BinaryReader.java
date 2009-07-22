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

package ssw.filehandlers;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.util.Vector;
import ssw.components.Ammunition;
import ssw.components.AvailableCode;
import ssw.components.RangedWeapon;

public class BinaryReader {
    public Vector ReadWeapons( String inputfile ) throws Exception {
        Vector finished = new Vector();
        DataInputStream FR;
        try {
            FR = new DataInputStream( new FileInputStream( inputfile ) );
            String name = "";
            String lname = "";
            String mname = "";
            String type = "";
            String special = "";
            int wclass = 0;
            //AvailableCode AC;
            while( true ) {
                try {
                    name = FR.readUTF();
                    lname = FR.readUTF();
                    mname = FR.readUTF();
                    type = FR.readUTF();
                    special = FR.readUTF();
                    wclass = FR.readInt();
                    AvailableCode AC = GetAvailability( FR );
                    RangedWeapon rw = new RangedWeapon( name, lname, mname, type, special, AC, wclass );
                    rw.SetStats( FR.readFloat(), FR.readInt(), FR.readFloat(), FR.readFloat(), FR.readFloat() );
                    rw.SetHeat( FR.readInt() );
                    rw.SetToHit( FR.readInt(), FR.readInt(), FR.readInt() );
                    rw.SetDamage( FR.readInt(), FR.readInt(), FR.readInt(), FR.readBoolean(), FR.readInt(), FR.readInt() );
                    rw.SetRange( FR.readInt(), FR.readInt(), FR.readInt(), FR.readInt() );
                    rw.SetAmmo( FR.readBoolean(), FR.readInt(), FR.readInt(), FR.readBoolean() );
                    rw.SetAllocations( FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean() );
                    rw.SetRequirements( FR.readBoolean(), FR.readBoolean(), FR.readBoolean() );
                    rw.SetWeapon( FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean() );
                    rw.SetCaselessAmmo( FR.readBoolean(), FR.readInt() );
                    rw.SetMissileFCS( FR.readBoolean(), FR.readInt() );
                    rw.SetPrintName( FR.readUTF() );
                    finished.add( rw );
                } catch( EOFException e1 ) {
                    break;
                }
            }
            FR.close();
        } catch( Exception e ) {
            e.printStackTrace();
            throw e;
        }
        return finished;
    }

    public Vector ReadAmmo( String inputfile ) throws Exception {
        Vector finished = new Vector();
        DataInputStream FR;
        try {
            FR = new DataInputStream( new FileInputStream( inputfile ) );
            String name = "";
            String lname = "";
            String mname = "";
            int idx = 0;
            AvailableCode AC;
            while( true ) {
                try {
                    name = FR.readUTF();
                    lname = FR.readUTF();
                    mname = FR.readUTF();
                    idx = FR.readInt();
                    AC = GetAvailability( FR );
                    Ammunition a = new Ammunition( name, lname, mname, idx, AC );
                    a.SetStats( FR.readFloat(), FR.readFloat(), FR.readFloat(), FR.readFloat() );
                    a.SetToHit( FR.readInt(), FR.readInt(), FR.readInt() );
                    a.SetDamage( FR.readInt(), FR.readInt(), FR.readInt(), FR.readBoolean(), FR.readInt(), FR.readInt() );
                    a.SetRange( FR.readInt(), FR.readInt(), FR.readInt(), FR.readInt() );
                    a.SetAmmo( FR.readInt(), FR.readBoolean(), FR.readInt(), FR.readInt() );
                    a.SetPrintName( FR.readUTF() );
                    finished.add( a );
                } catch( EOFException e1 ) {
                    break;
                }
            }
            FR.close();
        } catch( Exception e ) {
            e.printStackTrace();
            throw e;
        }
        return finished;
    }

    private AvailableCode GetAvailability( DataInputStream FR ) throws Exception {
        String[] strings = { "", "", "", "" };
        int[] ints = { 0, 0, 0, 0, 0 };
        boolean[] bools = { false, false, false };
        AvailableCode AC = new AvailableCode( FR.readInt() );
        ints[0] = FR.readInt();
        ints[1] = FR.readInt();
        ints[2] = FR.readInt();
        ints[3] = FR.readInt();
        ints[4] = FR.readInt();
        AC.SetRulesLevels( ints[0], ints[1], ints[2], ints[3], ints[4] );
        // Inner Sphere availability
        AC.SetISCodes( FR.readChar(), FR.readChar(), FR.readChar(), FR.readChar() );
        ints[0] = FR.readInt();
        strings[0] = FR.readUTF();
        bools[0] = FR.readBoolean();
        ints[1] = FR.readInt();
        bools[1] = FR.readBoolean();
        ints[2] = FR.readInt();
        strings[1] = FR.readUTF();
        bools[2] = FR.readBoolean();
        ints[3] = FR.readInt();
        strings[2] = FR.readUTF();
        ints[4] = FR.readInt();
        strings[3] = FR.readUTF();
        AC.SetISDates( ints[3], ints[4], bools[2], ints[0], ints[1], ints[2], bools[0], bools[1] );
        AC.SetISFactions( strings[2], strings[3], strings[0], strings[1] );
        // Clan availability
        AC.SetCLCodes( FR.readChar(), FR.readChar(), FR.readChar(), FR.readChar() );
        ints[0] = FR.readInt();
        strings[0] = FR.readUTF();
        bools[0] = FR.readBoolean();
        ints[1] = FR.readInt();
        bools[1] = FR.readBoolean();
        ints[2] = FR.readInt();
        strings[1] = FR.readUTF();
        bools[2] = FR.readBoolean();
        ints[3] = FR.readInt();
        strings[2] = FR.readUTF();
        ints[4] = FR.readInt();
        strings[3] = FR.readUTF();
        AC.SetCLDates( ints[3], ints[4], bools[2], ints[0], ints[1], ints[2], bools[0], bools[1] );
        AC.SetCLFactions( strings[2], strings[3], strings[0], strings[1] );
        // hack for primitive 'Mechs.  We're assuming everything can be used by them
        AC.SetPIMAllowed( true );
        AC.SetPBMAllowed( true );
        return AC;
    }
}

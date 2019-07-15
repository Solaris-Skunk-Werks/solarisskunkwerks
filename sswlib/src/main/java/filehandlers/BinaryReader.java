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

package filehandlers;

import components.*;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.util.ArrayList;

public class BinaryReader {
    public ArrayList ReadWeapons( String inputfile ) throws Exception {
        ArrayList finished = new ArrayList();
        DataInputStream FR;
        try {
            FR = new DataInputStream( new FileInputStream( inputfile ) );
            String aname = "";
            String cname = "";
            String lname = "";
            String mname = "";
            String type = "";
            String special = "";
            int wclass = 0;
            //AvailableCode AC;
            while( true ) {
                try {
                    aname = FR.readUTF();
                    cname = FR.readUTF();
                    lname = FR.readUTF();
                    mname = FR.readUTF();
                    type = FR.readUTF();
                    special = FR.readUTF();
                    wclass = FR.readInt();
                    AvailableCode AC = GetAvailability( FR );
                    RangedWeapon rw = new RangedWeapon( aname, cname, lname, mname, type, special, AC, wclass );
                    rw.SetStats( FR.readDouble(), FR.readInt(), FR.readInt(), FR.readDouble(), FR.readDouble(), FR.readDouble() );
                    rw.SetHeat( FR.readInt() );
                    rw.SetToHit( FR.readInt(), FR.readInt(), FR.readInt() );
                    rw.SetDamage( FR.readInt(), FR.readInt(), FR.readInt(), FR.readBoolean(), FR.readInt(), FR.readInt() );
                    rw.SetClusterMods( FR.readInt(), FR.readInt(), FR.readInt() );
                    rw.SetRange( FR.readInt(), FR.readInt(), FR.readInt(), FR.readInt() );
                    rw.SetAmmo( FR.readBoolean(), FR.readInt(), FR.readInt(), FR.readBoolean() );
                    rw.SetAllocations( FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean() );
                    rw.SetCVAllocs( FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean() );
                    rw.SetRequirements( FR.readBoolean(), FR.readBoolean(), FR.readBoolean() );
                    rw.SetWeapon( FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean() );
                    rw.SetCaselessAmmo( FR.readBoolean(), FR.readInt() );
                    rw.SetMissileFCS( FR.readBoolean(), FR.readInt() );
                    rw.SetChatName( FR.readUTF() );
                    rw.SetBookReference( FR.readUTF() );
                    rw.SetBattleForceAbilities( FR.readUTF().split(",") );
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

    public ArrayList ReadPhysicals( String inputfile ) throws Exception {
        ArrayList finished = new ArrayList();
        DataInputStream FR;
        try {
            FR = new DataInputStream( new FileInputStream( inputfile ) );
            String lname = "";
            String aname = "";
            String cname = "";
            String chat = "";
            String mname = "";
            String type = "";
            String special = "";
            while( true ) {
                try {
                    lname = FR.readUTF();
                    aname = FR.readUTF();
                    cname = FR.readUTF();
                    chat = FR.readUTF();
                    mname = FR.readUTF();
                    type = FR.readUTF();
                    special = FR.readUTF();
                    AvailableCode AC = GetAvailability( FR );
                    PhysicalWeapon pw = new PhysicalWeapon( aname, lname, cname, mname, chat, AC );
                    pw.SetType( type, special );
                    pw.SetTonnage( FR.readDouble(), FR.readDouble(), FR.readBoolean() );
                    pw.SetCost( FR.readDouble(), FR.readDouble() );
                    pw.SetBV( FR.readDouble(), FR.readDouble(), FR.readDouble() );
                    pw.SetCrits( FR.readDouble(), FR.readInt() );
                    pw.SetHeat( FR.readInt() );
                    pw.SetToHit( FR.readInt(), FR.readInt(), FR.readInt() );
                    pw.SetDamage( FR.readDouble(), FR.readInt() );
                    pw.SetAllocations( FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean() );
                    pw.SetRequiresLowerArm( FR.readBoolean() );
                    pw.SetRequiresHand( FR.readBoolean() );
                    pw.SetReplacesLowerArm( FR.readBoolean() );
                    pw.SetReplacesHand( FR.readBoolean() );
                    pw.SetRequirements( FR.readBoolean(), FR.readBoolean(), FR.readBoolean() );
                    pw.SetPWClass( FR.readInt() );
                    pw.SetBookReference( FR.readUTF() );
                    pw.SetBattleForceAbilities( FR.readUTF().split(",") );
                    if( FR.readBoolean() ) {
                        MechModifier mm = new MechModifier( FR.readInt(), FR.readInt(), FR.readInt(), FR.readDouble(), FR.readInt(), FR.readInt(), FR.readInt(), FR.readDouble(), FR.readDouble(), FR.readDouble(), FR.readDouble(), FR.readBoolean(), FR.readBoolean() );
                        pw.AddMechModifier( mm );
                    }
                    finished.add( pw );
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

    public ArrayList ReadEquipment( String inputfile ) throws Exception {
        ArrayList finished = new ArrayList();
        DataInputStream FR;
        try {
            FR = new DataInputStream( new FileInputStream( inputfile ) );
            String lname = "";
            String aname = "";
            String cname = "";
            String chat = "";
            String mname = "";
            String type = "";
            String special = "";
            while( true ) {
                try {
                    lname = FR.readUTF();
                    aname = FR.readUTF();
                    cname = FR.readUTF();
                    chat = FR.readUTF();
                    mname = FR.readUTF();
                    type = FR.readUTF();
                    special = FR.readUTF();
                    AvailableCode AC = GetAvailability( FR );
                    Equipment e = new Equipment( aname, lname, cname, mname, chat, type, AC );
                    e.SetSpecials( special );
                    e.SetTonnage( FR.readDouble(), FR.readBoolean(), FR.readDouble(), FR.readDouble(), FR.readDouble() );
                    e.SetCost( FR.readDouble(), FR.readDouble() );
                    e.SetBV( FR.readDouble(), FR.readDouble() );
                    e.SetCrits( FR.readInt(), FR.readDouble(), FR.readInt() );
                    e.SetHeat( FR.readInt() );
                    e.SetRange( FR.readInt(), FR.readInt(), FR.readInt() );
                    e.SetAmmo( FR.readBoolean(), FR.readInt(), FR.readInt() );
                    e.SetAllocs( FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readInt() );
                    e.SetCVAllocs( FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean(), FR.readBoolean() );
                    e.SetMountableRear( FR.readBoolean() );
                    e.SetExplosive( FR.readBoolean() );
                    e.SetBookReference( FR.readUTF() );
                    e.SetBattleForceAbilities( FR.readUTF().split(",") );
                    int numexceptions = FR.readInt();
                    if( numexceptions > 0 ) {
                        ArrayList<String> excep = new ArrayList<String>();
                        for( int i = 0; i < numexceptions; i++ ) {
                            excep.add( FR.readUTF() );
                        }
                        Exclusion ex = new Exclusion( excep.toArray( new String[] { null } ), e.CritName() );
                        e.SetExclusions( ex );
                    }
                    finished.add( e );
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

    public ArrayList ReadAmmo( String inputfile ) throws Exception {
        ArrayList finished = new ArrayList();
        DataInputStream FR;
        try {
            FR = new DataInputStream( new FileInputStream( inputfile ) );
            String aname = "";
            String cname = "";
            String lname = "";
            String mname = "";
            int idx = 0;
            AvailableCode AC;
            while( true ) {
                try {
                    aname = FR.readUTF();
                    cname = FR.readUTF();
                    lname = FR.readUTF();
                    mname = FR.readUTF();
                    idx = FR.readInt();
                    AC = GetAvailability( FR );
                    Ammunition a = new Ammunition( aname, cname, lname, mname, idx, AC );
                    a.SetStats( FR.readDouble(), FR.readDouble(), FR.readDouble(), FR.readDouble() );
                    a.SetToHit( FR.readInt(), FR.readInt(), FR.readInt() );
                    a.SetDamage( FR.readInt(), FR.readInt(), FR.readInt(), FR.readBoolean(), FR.readInt(), FR.readInt() );
                    a.SetRange( FR.readInt(), FR.readInt(), FR.readInt(), FR.readInt() );
                    a.SetAmmo( FR.readInt(), FR.readBoolean(), FR.readInt(), FR.readInt() );
                    a.SetBookReference( FR.readUTF() );
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

    public ArrayList<Quirk> ReadQuirks( String inputfile ) throws Exception {
        ArrayList<Quirk> finished = new ArrayList<Quirk>();
        DataInputStream FR;
        try {
            FR = new DataInputStream( new FileInputStream( inputfile ) );
            AvailableCode AC;
            while( true ) {
                try {
                    Quirk q = new Quirk( FR.readUTF(), 
                                         FR.readBoolean(),
                                         FR.readInt(),
                                         FR.readBoolean(), 
                                         FR.readBoolean(), 
                                         FR.readBoolean(), 
                                         FR.readBoolean(), 
                                         FR.readBoolean(), 
                                         FR.readBoolean(), 
                                         FR.readBoolean(), 
                                         FR.readBoolean(), 
                                         FR.readBoolean(), 
                                         FR.readBoolean(), 
                                         FR.readBoolean(), 
                                         FR.readBoolean(),
                                         FR.readUTF());
                    finished.add( q );
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
        AC.SetISCodes( FR.readChar(), FR.readChar(), FR.readChar(), FR.readChar(), FR.readChar() );
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
        AC.SetCLCodes( FR.readChar(), FR.readChar(), FR.readChar(), FR.readChar(), FR.readChar() );
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

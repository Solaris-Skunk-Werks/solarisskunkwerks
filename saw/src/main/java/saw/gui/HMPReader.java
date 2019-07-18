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

/**
 * Based on MegaMek/src/megamek/common/loaders/HMPFile.java
 */

package saw.gui;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Vector;
import components.AvailableCode;
import common.DataFactory;
import components.*;
import visitors.ifVisitor;

public class HMPReader {
    private Hashtable<Long, String> Common = new Hashtable<Long, String>();
    private Hashtable<Long, String> Sphere = new Hashtable<Long, String>();
    private Hashtable<Long, String> Clan = new Hashtable<Long, String>();
    private Hashtable<Long, String> Unused = new Hashtable<Long, String>();
    private Vector Errors = new Vector();

    public HMPReader() {
        BuildHash();
    }

    public String GetErrors() {
        String retval = "";
        for( int i = 0; i < Errors.size(); i++ ) {
            retval += ((ErrorReport) Errors.get( i )).GetErrorReport() + "\n\n";
        }
        return retval;
    }

    public Mech GetMech( String filename, boolean SuppressOmniNotification ) throws Exception {
        Errors.clear();
        byte[] buffer = new byte[5];
        int[] Armor = new int[11];

        DataInputStream FR;
        FR = new DataInputStream( new FileInputStream( filename ) );

        Mech m = new Mech();

        // From the MegaMek code, this seems to be "padding".
        FR.read(buffer);
        readUnsignedByte( FR );
        FR.skipBytes(11);

        m.SetTonnage( readUnsignedShort(FR) );

        buffer = new byte[readUnsignedShort(FR)];
        FR.read(buffer);
        m.SetName( new String( buffer ) );

        buffer = new byte[readUnsignedShort(FR)];
        FR.read(buffer);
        m.SetModel( new String(buffer) );

        // we're never going to restrict HMP 'Mechs since HMP didn't have that functionality
        m.SetYear( readUnsignedShort(FR), false );

        int Rules = readUnsignedShort(FR);
        if( Rules < 0 || Rules > 3 ) {
            throw new Exception( "Invalid Rules Level: " + Rules );
        } else {
            m.SetRulesLevel( DecodeRulesLevel( Rules ) );
        }

        // more padding
        readUnsignedInt(FR);
        FR.skipBytes(22);
        int bf2Length = readUnsignedShort(FR);
        FR.skipBytes( bf2Length );

        int TechBase = readUnsignedShort(FR);
        if( TechBase < 0 || TechBase > 2 ) {
            throw new Exception( "Invalid Tech Base: " + TechBase );
        }

        // Per the MegaMek code, this gets hairy.  I'm keeping their comments
        // Fortunately the TechBase from HMP lines up with SSW's internal
        boolean OmniMech = false;
        int IntStrucTechBase = TechBase;
        int EngineTechBase = TechBase;
        int HSTechBase = TechBase;
        int PhysicalWeaponTechBase = TechBase;
        int EnhanceTechBase = TechBase;
        int TCTechBase = TechBase;
        int ArmorTechBase = TechBase;
        if ( TechBase == AvailableCode.TECH_BOTH ) {
            // We've got a total of 7 shorts here.
            // The first one is the mech's "base" chassis technology type.
            // It also doubles as the internal structure type.
            // SSW - we'll have to decode this
            IntStrucTechBase = readUnsignedShort(FR);
            // Next we have engine, heat sinks, physical attack weapon,
            // myomer, targeting computer, and finally armor. Note that
            // these 14 bytes are always present in mixed-tech designs,
            // whether the specific equipment exists on the mech or not.
            EngineTechBase = readUnsignedShort(FR);
            HSTechBase = readUnsignedShort(FR);
            PhysicalWeaponTechBase = readUnsignedShort(FR);
            EnhanceTechBase = readUnsignedShort(FR);
            TCTechBase = readUnsignedShort(FR);
            ArmorTechBase = readUnsignedShort(FR);
        }

        int MotiveType = readUnsignedShort(FR);
        switch( MotiveType ) {
            case 0:
                // biped
                m.SetBiped();
                break;
            case 1:
                // quad
                m.SetQuad();
                break;
            case 2:
                // LAM
                throw new Exception( "SSW does not support and cannot load LAMs." );
            case 3:
                // armless
                throw new Exception( "SSW does not support and cannot load armless 'Mechs." );
            case 10:
                // biped omnimech
                if( ! SuppressOmniNotification ) {
                    Errors.add( new ErrorReport( "This 'Mech is flagged as an OmniMech but only one loadout will be created." ) );
                }
                OmniMech = true;
                m.SetBiped();
                break;
            case 11:
                // quad omnimech
                if( ! SuppressOmniNotification ) {
                    Errors.add( new ErrorReport( "This 'Mech is flagged as an OmniMech but only one loadout will be created." ) );
                }
                OmniMech = true;
                m.SetQuad();
                break;
            default:
                throw new Exception( "Invalid Motive Type: " + MotiveType );
        }

        switch( TechBase ) {
            case AvailableCode.TECH_INNER_SPHERE:
                m.SetInnerSphere();
                break;
            case AvailableCode.TECH_CLAN:
                m.SetClan();
                break;
            case AvailableCode.TECH_BOTH:
                m.SetMixed();
                break;
            default:
                throw new Exception( "Could not find a suitable Tech Base." );
        }

        // we'll have to jury-rig the era since it isn't in the HMP files.
        // this is important for the GUI
        if( m.GetYear() < 2443 ) {
            m.SetEra( AvailableCode.ERA_STAR_LEAGUE );
        } else if( m.GetYear() >= 2443 && m.GetYear() < 2801 ) {
            m.SetEra( AvailableCode.ERA_STAR_LEAGUE );
        } else if( m.GetYear() >= 2801 && m.GetYear() < 3051 ) {
            m.SetEra( AvailableCode.ERA_SUCCESSION );
        } else if( m.GetYear() >= 3051 && m.GetYear() < 3132 ) {
            m.SetEra( AvailableCode.ERA_CLAN_INVASION );
        } else {
            m.SetEra( AvailableCode.ERA_DARK_AGES );
        }
        if( m.GetTechbase() == AvailableCode.TECH_CLAN ) {
            if( m.GetEra() < AvailableCode.ERA_SUCCESSION ) {
                m.SetEra( AvailableCode.ERA_SUCCESSION );
            } else {
                m.SetEra( AvailableCode.ERA_CLAN_INVASION );
            }
        }
        // although we can still have a mixed chassis earlier, most designs will
        // be Clan Invasion era
        if( m.GetTechbase() == AvailableCode.TECH_BOTH ) {
            if( m.GetEra() < AvailableCode.ERA_CLAN_INVASION ) {
                m.SetEra( AvailableCode.ERA_CLAN_INVASION );
            }
        }

        int IntStrucType = readUnsignedShort(FR);
        String IntStrucLookup = "";
        switch( IntStrucType ) {
            case 0:
                IntStrucLookup = "Standard Structure";
                break;
            case 1:
                IntStrucLookup = BuildLookupName( "Endo-Steel", m.GetTechbase(), IntStrucTechBase );
                break;
            case 2:
                IntStrucLookup = "Composite Structure";
                break;
            case 3:
                IntStrucLookup = "Reinforced Structure";
                break;
            case 4:
                IntStrucLookup = "Industrial Structure";
                break;
            default:
                throw new Exception( "Invalid Internal Structure Type: " + IntStrucType );
        }

        int EngineRating = readUnsignedShort(FR);
        int EngineType = readUnsignedShort(FR);
        String EngineLookup = "";
        switch( EngineType ) {
            case 0:
                EngineLookup = "Fusion Engine";
                break;
            case 1:
                EngineLookup = BuildLookupName( "XL Engine", m.GetTechbase(), EngineTechBase );
                break;
            case 2:
                EngineLookup = BuildLookupName( "XXL Engine", m.GetTechbase(), EngineTechBase );
                break;
            case 3:
                EngineLookup = "Compact Fusion Engine";
                break;
            case 4:
                EngineLookup = "I.C.E. Engine";
                break;
            case 5:
                EngineLookup = "Light Fusion Engine";
                break;
            default:
                throw new Exception( "Invalid Engine Type: " + EngineType );
        }

        // this used to get the walking MP but we already have the engine rating
        readUnsignedShort(FR);
        int NumJJ = readUnsignedShort(FR);

        int NumHS = readUnsignedShort(FR);
        int HSType = readUnsignedShort(FR);
        String HSLookup = "";
        switch( HSType ) {
            case 0:
                HSLookup = "Single Heat Sink";
                break;
            case 1:
                HSLookup = BuildLookupName( "Double Heat Sink", m.GetTechbase(), HSTechBase );
                break;
            case 2:
                throw new Exception( "SSW does not yet support Compact Heat Sinks." );
            case 3:
                throw new Exception( "SSW does not yet support Laser Heat Sinks." );
            default:
                throw new Exception( "Invalid Heat Sink Type: " + HSType );
        }

        String ArmorLookup = "";
        int ArmorType = readUnsignedShort(FR);
        switch( ArmorType ) {
            case 0:
                ArmorLookup = "Standard Armor";
                break;
            case 1:
                ArmorLookup = BuildLookupName( "Ferro-Fibrous", m.GetTechbase(), ArmorTechBase );
                break;
            case 2:
                ArmorLookup = BuildLookupName( "Reactive Armor", m.GetTechbase(), ArmorTechBase );
                break;
            case 3:
                ArmorLookup = BuildLookupName( "Laser-Reflective", m.GetTechbase(), ArmorTechBase );
                break;
            case 4:
                ArmorLookup = "Hardened Armor";
                break;
            case 5:
                ArmorLookup = "Light Ferro-Fibrous";
                break;
            case 6:
                ArmorLookup = "Heavy Ferro-Fibrous";
                break;
            case 7:
                throw new Exception( "SSW does not support pre-Tactical Operations Patchwork Armor." );
            case 8:
                ArmorLookup = "Stealth Armor";
                break;
            default:
                throw new Exception( "Invalid Armor Type: " + ArmorType );
        }

        FR.skipBytes(2); // ??
        Armor[LocationIndex.MECH_LOC_LA] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[LocationIndex.MECH_LOC_LT] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[LocationIndex.MECH_LOC_LL] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[LocationIndex.MECH_LOC_RA] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[LocationIndex.MECH_LOC_RT] = readUnsignedShort(FR);
        FR.skipBytes(2); // ??

        // WTF is this doing here???
        int JJType = readUnsignedShort(FR);
        String JJLookup = "";
        switch( JJType ) {
            case 0:
                JJLookup = "Standard Jump Jet";
                break;
            case 1:
                JJLookup = "Improved Jump Jet";
                break;
            default:
                throw new Exception( "Invalid Jump Jet Type: " + JJType );
        }

        // back to armor I guess...
        Armor[LocationIndex.MECH_LOC_RL] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[LocationIndex.MECH_LOC_HD] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[LocationIndex.MECH_LOC_CT] = readUnsignedShort(FR);
        FR.skipBytes(2); // ??
        Armor[LocationIndex.MECH_LOC_LTR] = readUnsignedShort(FR);
        Armor[LocationIndex.MECH_LOC_RTR] = readUnsignedShort(FR);
        Armor[LocationIndex.MECH_LOC_CTR] = readUnsignedShort(FR);

        int EnhanceType = readUnsignedShort(FR);
        String EnhanceLookup = "";
        switch( EnhanceType ) {
            case 0:
                EnhanceLookup = "No Enhancement";
                break;
            case 1:
                EnhanceLookup = "TSM";
                break;
            case 2:
                EnhanceLookup = BuildLookupName( "MASC", m.GetTechbase(), EnhanceTechBase );
                break;
            case 3:
                EnhanceLookup = "Industrial TSM";
                break;
            default:
                throw new Exception( "Invalid Physical Enhancement Type: " + EnhanceType );
        }

        int WeaponCount = readUnsignedShort(FR);
        int[][] WeaponArray = new int[WeaponCount][4];
        for (int i = 0; i < WeaponCount; i++) {
            WeaponArray[i][0] = readUnsignedShort(FR); // weapon count
            WeaponArray[i][1] = readUnsignedShort(FR); // weapon type
            WeaponArray[i][2] = readUnsignedShort(FR); // weapon location
            WeaponArray[i][3] = readUnsignedShort(FR); // ammo

            FR.skipBytes(2); // ??

            // manufacturer name.  Shame it's in a number format instead of a String
            FR.skipBytes(readUnsignedShort(FR));
        }

        // now get the criticals
        long[][] Criticals = new long[8][12];
        for( int i = 0; i < 12; i++ ) {
            Criticals[LocationIndex.MECH_LOC_LA][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[LocationIndex.MECH_LOC_LT][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[LocationIndex.MECH_LOC_LL][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[LocationIndex.MECH_LOC_RA][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[LocationIndex.MECH_LOC_RT][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[LocationIndex.MECH_LOC_RL][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[LocationIndex.MECH_LOC_HD][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[LocationIndex.MECH_LOC_CT][i] = readUnsignedInt(FR);
        }

        // more padding I'm assuming
        FR.skipBytes(4);

        // Fluff is fun
        String Fluff = "";

        buffer = new byte[readUnsignedShort(FR)];
        FR.read(buffer);
        Fluff = new String(buffer);
        m.SetOverview( Fluff );

        buffer = new byte[readUnsignedShort(FR)];
        FR.read(buffer);
        Fluff = new String(buffer);
        m.SetCapabilities( Fluff );

        buffer = new byte[readUnsignedShort(FR)];
        FR.read(buffer);
        Fluff = new String(buffer);
        m.SetHistory( Fluff );

        buffer = new byte[readUnsignedShort(FR)];
        FR.read(buffer);
        Fluff = new String(buffer);
        m.SetVariants( Fluff );

        buffer = new byte[readUnsignedShort(FR)];
        FR.read(buffer);
        Fluff = new String(buffer);
        m.SetNotables( Fluff );

        buffer = new byte[readUnsignedShort(FR)];
        FR.read(buffer);
        Fluff = new String(buffer);
        m.SetDeployment( Fluff );

        // non printing notes
        // could probably put these in Additional, but I'm not sure of the file format.
        buffer = new byte[readUnsignedShort(FR)];
        FR.read(buffer);
        Fluff = new String(buffer);

        buffer = new byte[readUnsignedShort(FR)];
        FR.read(buffer);
        Fluff += "\n" + new String(buffer);
        m.SetAdditional( Fluff );

        FR.skipBytes(8); // mechs with supercharger have an 01 in here, but we can identify from the criticals  // Sounds good, MM dudes.

        String GyroLookup = "Standard Gyro";
        String CockpitLookup = "Standard Cockpit";
        boolean CommandConsole = false;

        // Get cockpit and gyro type, if any.
        if( m.GetRulesLevel() > AvailableCode.RULES_ADVANCED ) {
            int GyroType = readUnsignedShort(FR);
            int CockpitType = readUnsignedShort(FR);

            switch( GyroType ) {
                case 0:
                    GyroLookup = "Standard Gyro";
                    break;
                case 1:
                    if( m.GetTechbase() == AvailableCode.TECH_CLAN ) {
                        // this is not allowed under current rules.
                        GyroLookup = "Standard Gyro";
                        Errors.add( new ErrorReport( "An XL Gyro was specified for a Clan TechBase, which is not allowed under current rules.\nUse either Mixed or Inner Sphere Tech.  A Standard Gyro was used instead." ) );
                    } else {
                        GyroLookup = "Extra-Light Gyro";
                    }
                    break;
                case 2:
                    if( m.GetTechbase() == AvailableCode.TECH_CLAN ) {
                        // this is not allowed under current rules.
                        GyroLookup = "Standard Gyro";
                        Errors.add( new ErrorReport( "A Compact Gyro was specified for a Clan TechBase, which is not allowed under current rules.\nUse either Mixed or Inner Sphere Tech.  A Standard Gyro was used instead." ) );
                    } else {
                        GyroLookup = "Compact Gyro";
                    }
                    break;
                case 3:
                    if( m.GetTechbase() == AvailableCode.TECH_CLAN ) {
                        // this is not allowed under current rules.
                        GyroLookup = "Standard Gyro";
                        Errors.add( new ErrorReport( "A Heavy-Duty Gyro was specified for a Clan TechBase, which is not allowed under current rules.\nUse either Mixed or Inner Sphere Tech.  A Standard Gyro was used instead." ) );
                    } else {
                        GyroLookup = "Heavy-Duty Gyro";
                    }
                    break;
                default:
                    throw new Exception( "A non-standard Gyro type was specified: " + GyroType );
            }

            switch( CockpitType ) {
                case 0:
                    CockpitLookup = "Standard Cockpit";
                    break;
                case 1:
                    CockpitLookup = "Torso-Mounted Cockpit";
                    break;
                case 2:
                    if( m.GetTechbase() == AvailableCode.TECH_CLAN ) {
                        // this is not allowed under current rules.
                        CockpitLookup = "Standard Cockpit";
                        Errors.add( new ErrorReport( "A Small Cockpit was specified for a Clan TechBase, which is not allowed under Tactical Operations.\nUse either Mixed or Inner Sphere Tech.  A Standard Cockpit was used instead." ) );
                    } else {
                        CockpitLookup = "Small Cockpit";
                    }
                    break;
                case 3:
                    CockpitLookup = "Standard Cockpit";
                    CommandConsole = true;
                    break;
                case 4:
                    CockpitLookup = "Standard Cockpit";
                    CommandConsole = true;
                    Errors.add( new ErrorReport( "A Dual Cockpit was specified, which is not supported in SSW.\nThe 'Mech has been set to use a Command Console instead." ) );
                case 5:
                    CockpitLookup = "Industrial Cockpit";
                    break;
                default:
                    throw new Exception( "A non-standard Cockpit type was specified: " + CockpitType );
            }
        }

        // done with the file.
        FR.close();

        // check for advanced stuff on the 'Mech
        if( Rules == 2 ) {
            for( int i = 0; i < 8; i++ ) {
                for( int j = 0; j < 12; j++ ) {
                    if( Criticals[i][j] == 0x55 || Criticals[i][j] == 0x71 || Criticals[i][j] == 0xC9 ) {
                        m.SetRulesLevel( AvailableCode.RULES_ADVANCED );
                    }
                }
            }
        }

        // before we go all crazy, figure out our arm actuator situation
        if( Criticals[LocationIndex.MECH_LOC_LA][3] != 0x04 ) {
            m.GetActuators().RemoveLeftHand();
        }
        if( Criticals[LocationIndex.MECH_LOC_LA][2] != 0x03 ) {
            m.GetActuators().RemoveLeftLowerArm();
        }
        if( Criticals[LocationIndex.MECH_LOC_RA][3] != 0x04 ) {
            m.GetActuators().RemoveRightHand();
        }
        if( Criticals[LocationIndex.MECH_LOC_RA][2] != 0x03 ) {
            m.GetActuators().RemoveRightLowerArm();
        }

        // we need a special check here for Stealth armor, since you can move the
        // crits around
        LocationIndex[] Stealth = null;
        if( ArmorLookup.equals( "Stealth Armor" ) ) {
            long test = 0x23;
            Vector STLocs = new Vector();
            for( int i = 0; i < 8; i++ ) {
                for( int j = 0; j < 12; j++ ) {
                    // for every instance of Stealth Armor, mark the location
                    if( Criticals[i][j] == test ) {
                        STLocs.add( new LocationIndex( j, i, 1 ) );
                        Criticals[i][j] = 0x00;
                    }
                }
            }
            // Convert to an array so we can load it into the visitor later
            Stealth = new LocationIndex[STLocs.size()];
            for( int i = 0; i < STLocs.size(); i++ ) {
                Stealth[i] = (LocationIndex) STLocs.get( i );
            }
        }

        // let's check for engine criticals in the side torsos as well
        int LTEngineStart = 0, RTEngineStart = 0;
        for( int i = 0; i < 12; i++ ) {
            if( Criticals[LocationIndex.MECH_LOC_LT][i] == 0x0F ) {
                LTEngineStart = i;
                break;
            }
        }
        for( int i = 0; i < 12; i++ ) {
            if( Criticals[LocationIndex.MECH_LOC_RT][i] == 0x0F ) {
                RTEngineStart = i;
                break;
            }
        }
        LocationIndex[] EngineLocs = { new LocationIndex( LTEngineStart, LocationIndex.MECH_LOC_LT, 1 ), new LocationIndex( RTEngineStart, LocationIndex.MECH_LOC_RT, 1 ) };

        // build the 'Mech based on what we've come up with
        ifVisitor v = m.Lookup( IntStrucLookup );
        m.Visit( v );
        v = m.Lookup( EngineLookup );
        v.LoadLocations( EngineLocs );
        m.Visit( v );
        m.SetEngineRating( EngineRating );
        v = m.Lookup( ArmorLookup );
        if( ArmorLookup.equals( "Stealth Armor" ) ) {
            v.LoadLocations( Stealth );
        }
        m.Visit( v );
        v = m.Lookup( GyroLookup );
        m.Visit( v );
        v = m.Lookup( CockpitLookup );
        m.Visit( v );
        m.SetCommandConsole( CommandConsole );
        v = m.Lookup( HSLookup );
        m.Visit( v );
        m.GetLoadout().GetHeatSinks().SetNumHS( NumHS );
        v = m.Lookup( EnhanceLookup );
        m.Visit( v );
        v = m.Lookup( JJLookup );
        m.Visit( v );
        for( int i = 0; i < NumJJ; i++ ) {
            m.GetLoadout().GetJumpJets().IncrementNumJJ();
        }

        // set the armor
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_HD, Armor[LocationIndex.MECH_LOC_HD] );
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_CT, Armor[LocationIndex.MECH_LOC_CT] );
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_LT, Armor[LocationIndex.MECH_LOC_LT] );
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_RT, Armor[LocationIndex.MECH_LOC_RT] );
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_LA, Armor[LocationIndex.MECH_LOC_LA] );
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_RA, Armor[LocationIndex.MECH_LOC_RA] );
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_LL, Armor[LocationIndex.MECH_LOC_LL] );
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_RL, Armor[LocationIndex.MECH_LOC_RL] );
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_CTR, Armor[LocationIndex.MECH_LOC_CTR] );
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_LTR, Armor[LocationIndex.MECH_LOC_LTR] );
        m.GetArmor().SetArmor( LocationIndex.MECH_LOC_RTR, Armor[LocationIndex.MECH_LOC_RTR] );

        // now check for excess armor due to rounding.  This is the newer rules,
        // we'll simply remove the excess from the CT front.
        double RoundAV = ( m.GetArmor().GetArmorValue() - 1 ) / ( 8 * m.GetArmor().GetAVMult() );
        int mid = (int) Math.floor( RoundAV + 0.9999 );
        RoundAV = mid * 0.5;
        if( m.GetArmor().GetTonnage() > RoundAV ) {
            m.GetArmor().SetArmor( LocationIndex.MECH_LOC_CT, m.GetArmor().GetLocationArmor( LocationIndex.MECH_LOC_CT ) - 1 );
        }

        // first, let's find all the instances of base chassis items.  Structure first
        if( m.GetIntStruc().NumCrits() > 0 ) {
            // since the only structure that HMP has with more than 1 crit is
            // Endo-Steel, we'll look for it directly.
            long test = 0x14;
            Vector ISLocs = new Vector();
            for( int i = 0; i < 8; i++ ) {
                for( int j = 0; j < 12; j++ ) {
                    // for every instance of Endo-Steel, mark the location
                    if( Criticals[i][j] == test ) {
                        ISLocs.add( new LocationIndex( j, i, 1 ) );
                        Criticals[i][j] = 0x00;
                    }
                }
            }
            // now allocate the endo-steel
            for( int i = 0; i < ISLocs.size(); i++ ) {
                LocationIndex l = (LocationIndex) ISLocs.get( i );
                m.GetLoadout().AddTo( m.GetIntStruc(), l.Location, l.Index );
            }
        }

        // armor next
        if( m.GetArmor().NumCrits() > 0 &! m.GetArmor().IsStealth() ) {
            // find out what kind of armor we have.
            long ArmorNum = 0;
            String test = m.GetArmor().LookupName();
            if( test.equals( "Ferro-Fibrous" ) ) {
                ArmorNum = 0x15;
            } else if( test.equals( "Reactive Armor" ) ) {
                ArmorNum = 0x1c;
            } else if( test.equals( "Laser-Reflective" ) ) {
                ArmorNum = 0x1d;
            } else if( test.equals( "Light Ferro-Fibrous" ) ) {
                ArmorNum = 0x21;
            } else if( test.equals( "Heavy Ferro-Fibrous" ) ) {
                ArmorNum = 0x23;
            }
            if( ArmorNum == 0 ) { throw new Exception( "An armor type with critical spaces was specified but we can't find where to put them.\nLoading aborted." ); }
            Vector ARLocs = new Vector();
            for( int i = 0; i < 8; i++ ) {
                for( int j = 0; j < 12; j++ ) {
                    // for every instance of Endo-Steel, mark the location
                    if( Criticals[i][j] == ArmorNum ) {
                        ARLocs.add( new LocationIndex( j, i, 1 ) );
                        Criticals[i][j] = 0x00;
                    }
                }
            }
            // now allocate the armor
            for( int i = 0; i < ARLocs.size(); i++ ) {
                LocationIndex l = (LocationIndex) ARLocs.get( i );
                m.GetLoadout().AddTo( m.GetArmor(), l.Location, l.Index );
            }
        }

        // on to jump jets
        if( m.GetJumpJets().GetNumJJ() > 0 ) {
            long test = 0x0B;
            Vector JJLocs = new Vector();
            boolean improved = m.GetJumpJets().IsImproved();
            for( int i = 0; i < 8; i++ ) {
                for( int j = 0; j < 12; j++ ) {
                    if( Criticals[i][j] == test ) {
                        JJLocs.add( new LocationIndex( j, i, 1 ) );
                        Criticals[i][j] = 0x00;
                        if( improved ) { j++; Criticals[i][j] = 0x00; }
                    }
                }
            }
            JumpJet[] jjList = m.GetJumpJets().GetPlacedJumps();
            if( JJLocs.size() != jjList.length ) {
                throw new Exception( "The number of jump jets specified does not match the number allocated.\nLoading aborted." );
            }
            // now allocate the jump jets
            for( int i = 0; i < JJLocs.size(); i++ ) {
                    // place each jump jet
                LocationIndex l = (LocationIndex) JJLocs.get( i );
                m.GetLoadout().AddTo( jjList[i], l.Location, l.Index );
            }
        }

        // heat sinks next
        if( m.GetHeatSinks().GetPlacedHeatSinks().length > 0 ) {
            long test = 0x00;
            int size = 0;
            if( m.GetHeatSinks().IsDouble() ) {
                test = 0x0A;
                if( m.GetHeatSinks().GetTechBase() == AvailableCode.TECH_CLAN ) {
                    size = 1;
                } else {
                    size = 2;
                }
            } else {
                test = 0x09;
            }
            Vector HSLocs = new Vector();
            for( int i = 0; i < 8; i++ ) {
                for( int j = 0; j < 12; j++ ) {
                    if( Criticals[i][j] == test ) {
                        HSLocs.add( new LocationIndex( j, i, 1 ) );
                        Criticals[i][j] = 0x00;
                        if( size > 0 ) {
                            Criticals[i][j+1] = 0x00;
                            if( size > 1 ) {
                                Criticals[i][j+2] = 0x00;
                            }
                        }
                        j += size;
                    }
                }
            }
            HeatSink[] HSList = m.GetHeatSinks().GetPlacedHeatSinks();
            if( HSLocs.size() != HSList.length ) {
                if( OmniMech ) {
                    throw new Exception( "The number of heat sinks outside the engine does not match the number allocated.\nThis is most likely an issue with fixed heat sinks in an OmniMech loadout.\nSSW does not know how many heat sinks are fixed in the base loadout.\nLoading aborted." );
                } else {
                    throw new Exception( "The number of heat sinks outside the engine does not match the number allocated.\nLoading aborted." );
                }
            }
            // now allocate them
            for( int i = 0; i < HSLocs.size(); i++ ) {
                LocationIndex l = (LocationIndex) HSLocs.get( i );
                m.GetLoadout().AddTo( HSList[i], l.Location, l.Index );
            }
        }

        // see if we have a physical enhancement
        if( m.GetPhysEnhance().NumCrits() > 0 ) {
            long test = 0x00;
            if( m.GetPhysEnhance().Contiguous() ) {
                test = 0x17; // MASC
                boolean found = false;
                for( int i = 0; i < 8; i ++ ) {
                    for( int j = 0; j < 12; j++ ) {
                        if( Criticals[i][j] == test ) {
                            m.GetLoadout().AddTo( m.GetPhysEnhance(), i, j );
                            for( int x = j; x < m.GetPhysEnhance().NumCrits() + j; x++ ) {
                                Criticals[i][x] = 0x00;
                            }
                            found = true;
                            break;
                        }
                    }
                    if( found ) { break; }
                }
            } else {
                test = 0x16; // TSM (of either sort, apparently)
                Vector TSMLocs = new Vector();
                for( int i = 0; i < 8; i++ ) {
                    for( int j = 0; j < 12; j++ ) {
                        if( Criticals[i][j] == test ) {
                            TSMLocs.add( new LocationIndex( j, i, 1 ) );
                            Criticals[i][j] = 0x00;
                        }
                    }
                }
                if( TSMLocs.size() != m.GetPhysEnhance().NumCrits() ) {
                    throw new Exception( "The number of TSM crits specified does not match the number allocated.\nLoading aborted." );
                }
                for( int i = 0; i < TSMLocs.size(); i++ ) {
                    LocationIndex l = (LocationIndex) TSMLocs.get( i );
                    m.GetLoadout().AddTo( m.GetPhysEnhance(), l.Location, l.Index );
                }
            }
        }

        // figure out if we have any CASE crits to handle
        for( int i = 0; i < 8; i++ ) {
            for( int j = 0; j < 12; j++ ) {
                if( Criticals[i][j] == 0x19 ) {
                    // IS CASE
                    switch( i ) {
                        case LocationIndex.MECH_LOC_CT:
                            m.GetLoadout().SetCTCASE( true, j );
                            break;
                        case LocationIndex.MECH_LOC_LT:
                            m.GetLoadout().SetLTCASE( true, j );
                            break;
                        case LocationIndex.MECH_LOC_RT:
                            m.GetLoadout().SetRTCASE( true, j );
                            break;
                        default:
                            Errors.add( new ErrorReport( "Inner Sphere CASE was specified for the " + LocationIndex.MechLocs[i] + "\nThis is not allowed and the item will not be added." ) );
                            break;
                    }
                }
                if( Criticals[i][j] == 0x26 ) {
                    // CASE II
                    switch( i ) {
                        case LocationIndex.MECH_LOC_HD:
                            m.GetLoadout().SetHDCASEII( true, j, false );
                            break;
                        case LocationIndex.MECH_LOC_CT:
                            m.GetLoadout().SetCTCASEII( true, j, false );
                            break;
                        case LocationIndex.MECH_LOC_LT:
                            m.GetLoadout().SetLTCASEII( true, j, false );
                            break;
                        case LocationIndex.MECH_LOC_RT:
                            m.GetLoadout().SetRTCASEII( true, j, false );
                            break;
                        case LocationIndex.MECH_LOC_LA:
                            m.GetLoadout().SetLACASEII( true, j, false );
                            break;
                        case LocationIndex.MECH_LOC_RA:
                            m.GetLoadout().SetRACASEII( true, j, false );
                            break;
                        case LocationIndex.MECH_LOC_LL:
                            m.GetLoadout().SetLLCASEII( true, j, false );
                            break;
                        case LocationIndex.MECH_LOC_RL:
                            m.GetLoadout().SetRLCASEII( true, j, false );
                            break;
                        default:
                            Errors.add( new ErrorReport( "CASE II was specified for an invalid location.\nThe item will not be added." ) );
                            break;
                    }
                }
            }
        }

        // see if we have a targeting computer
        int TCLoc = -1;
        int TCIdx = -1;
        boolean HasTC = false;
        for( int i = 0; i < 8; i++ ) {
            for( int j = 0; j < 8; j++ ) {
                if( Criticals[i][j] == 0x12 ) {
                    // found the start location.
                    TCLoc = i;
                    TCIdx = j;
                    HasTC = true;
                    break;
                }
            }
            if( HasTC ) { break; }
        }

        // do we have Missile FCS
        boolean HasFCS = false;
        for( int i = 0; i < 8; i++ ) {
            for( int j = 0; j < 8; j++ ) {
                if( Criticals[i][j] == 0x18 ) {
                    m.SetFCSArtemisIV( true );
                    HasFCS = true;
                    break;
                }
            }
            if( HasFCS ) { break; }
        }

        // clear out all of the engine, gyro, cockpit, and actuator locations
        // needed for later lookups since we don't load each critical slot
        // independently, we do it per item
        for( int i = 0; i < 8; i++ ) {
            for( int j = 0; j < 12; j++ ) {
                if( Criticals[i][j] > 0x00 && Criticals[i][j] < 0x11 || Criticals[i][j] == 0x12 || Criticals[i][j] == 0x18 || Criticals[i][j] == 0x19 || Criticals[i][j] == 0x26 ) {
                    Criticals[i][j] = 0x00;
                }
            }
        }

        // now for equipment.
        // get the secondary hash table for lookups
        Hashtable Other = null;
        if( IntStrucTechBase == AvailableCode.TECH_CLAN ) {
            Other = Clan;
        } else {
            Other = Sphere;
        }
        // now we get to fish through the loadout trying to figure out what
        // equipment this 'Mech has.
        DataFactory df = new DataFactory( m );
        for( int i = 0; i < 8; i++ ) {
            for( int j = 0; j < 12; j++ ) {
                if( Criticals[i][j] != 0x00 ) {
                    // transform the lookup number.  we'll also find out if it was rear-mounted
                    Long lookup = GetLookupNum( Criticals[i][j] );
                    boolean rear = IsRearMounted( Criticals[i][j] );
                    boolean found = true;

                    // for each critical, we'll need to find the appropriate item
                    // if we can't find it, create an error to let the user know
                    String Name = Common.get( lookup );
                    if( Name == null ) {
                        Name = (String) Other.get( lookup );
                        if( Name == null ) {
                            found = false;
                        }
                    }

                    if( found ) {
                        // fetch the item from the database
                        if( m.UsingArtemisIV() ) {
                            if( Name.contains( "@ LRM" ) || Name.contains( "@ SRM" ) ) { Name += " (Artemis IV Capable)"; }
                        }
                        abPlaceable neweq = df.GetEquipment().GetByName( Name, m );
                        if( neweq != null ) {
                            // is the item splittable?
                            if( neweq.CanSplit() ) {
                                // if it is splittable, figure out how many criticals are here
                                int S1Index = j;
                                int S1Num = 0;
                                for( int k = j; k < 12; k++ ) {
                                    if( ( Criticals[i][k] & 0x0000FFFF ) == ( 0x0000FFFF & lookup ) ) { S1Num++; }
                                }
                                // do we need to check adjacent locations?
                                if( S1Num == neweq.NumCrits() ) {
                                    // nope just allocate it
                                    m.GetLoadout().AddToQueue( neweq );
                                    m.GetLoadout().AddTo( neweq, i, S1Index );
                                    Criticals[i][j] = 0x00;
                                    if( neweq.NumCrits() > 1 ) {
                                        // we'll need to clear out the rest of the
                                        // criticals so we don't "find" this again
                                        for( int k = 1; k < neweq.NumCrits(); k++ ) {
                                            Criticals[i][j + k] = 0x00;
                                        }
                                    }
                                } else {
                                    // find the other criticals in an adjacent location
                                    int SecondLoc = -1;
                                    int ThirdLoc = -1;
                                    int FourthLoc = -1;
                                    switch( i ) {
                                        case LocationIndex.MECH_LOC_CT:
                                            SecondLoc = LocationIndex.MECH_LOC_LT;
                                            ThirdLoc = LocationIndex.MECH_LOC_RT;
                                            break;
                                        case LocationIndex.MECH_LOC_LT:
                                            SecondLoc = LocationIndex.MECH_LOC_LA;
                                            ThirdLoc = LocationIndex.MECH_LOC_CT;
                                            FourthLoc = LocationIndex.MECH_LOC_LL;
                                            break;
                                        case LocationIndex.MECH_LOC_RT:
                                            SecondLoc = LocationIndex.MECH_LOC_RA;
                                            ThirdLoc = LocationIndex.MECH_LOC_CT;
                                            FourthLoc = LocationIndex.MECH_LOC_RL;
                                            break;
                                        case LocationIndex.MECH_LOC_LA:
                                            SecondLoc = LocationIndex.MECH_LOC_LT;
                                            break;
                                        case LocationIndex.MECH_LOC_RA:
                                            SecondLoc = LocationIndex.MECH_LOC_RT;
                                            break;
                                        case LocationIndex.MECH_LOC_LL:
                                            SecondLoc = LocationIndex.MECH_LOC_LT;
                                            break;
                                        case LocationIndex.MECH_LOC_RL:
                                            SecondLoc = LocationIndex.MECH_LOC_RT;
                                            break;
                                    }
                                    // how many locations do we have to check?
                                    // this whole process is retarded.  Keeping the criticals in a series of arrays
                                    // is so backwards...  How do you differentiate between two items of the same type?
                                    // Suppose we'll just have to keep count before we annihalate the item in the Criticals...
                                    int S2Num = 0;
                                    int S2Index = -1;
                                    int S2Loc = -1;
                                    int NumLeft = neweq.NumCrits() - S1Num;
                                    if( SecondLoc > 0 ) {
                                        if( ThirdLoc > 0 ) {
                                            if( FourthLoc > 0 ) {
                                                // Check all three locations
                                                for( int k = 0; k < 12; k++ ) {
                                                    if( S2Index < 0 ) {
                                                        if( ( Criticals[SecondLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                            S2Num++;
                                                            S2Index = k;
                                                            S2Loc = SecondLoc;
                                                        }
                                                    } else {
                                                        if( ( Criticals[SecondLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                            S2Num++;
                                                        }
                                                    }
                                                }
                                                for( int k = 0; k < 12; k++ ) {
                                                    if( S2Index < 0 ) {
                                                        if( ( Criticals[ThirdLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                            S2Num++;
                                                            S2Index = k;
                                                            S2Loc = ThirdLoc;
                                                        }
                                                    } else {
                                                        if( ( Criticals[ThirdLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                            S2Num++;
                                                        }
                                                    }
                                                }
                                                for( int k = 0; k < 12; k++ ) {
                                                    if( S2Index < 0 ) {
                                                        if( ( Criticals[FourthLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                            S2Num++;
                                                            S2Index = k;
                                                            S2Loc = FourthLoc;
                                                        }
                                                    } else {
                                                        if( ( Criticals[FourthLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                            S2Num++;
                                                        }
                                                    }
                                                }
                                            } else {
                                                // Check both locations
                                                for( int k = 0; k < 12; k++ ) {
                                                    if( S2Index < 0 ) {
                                                        if( ( Criticals[SecondLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                            S2Num++;
                                                            S2Index = k;
                                                            S2Loc = SecondLoc;
                                                        }
                                                    } else {
                                                        if( ( Criticals[SecondLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                            S2Num++;
                                                        }
                                                    }
                                                }
                                                for( int k = 0; k < 12; k++ ) {
                                                    if( S2Index < 0 ) {
                                                        if( ( Criticals[ThirdLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                            S2Num++;
                                                            S2Index = k;
                                                            S2Loc = ThirdLoc;
                                                        }
                                                    } else {
                                                        if( ( Criticals[ThirdLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                            S2Num++;
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            // check the other location
                                            for( int k = 0; k < 12; k++ ) {
                                                if( S2Index < 0 ) {
                                                    if( ( Criticals[SecondLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                        S2Num++;
                                                        S2Index = k;
                                                        S2Loc = SecondLoc;
                                                    }
                                                } else {
                                                    if( ( Criticals[SecondLoc][k] & 0x0000FFFF ) == ( lookup & 0x0000FFFF ) ) {
                                                        S2Num++;
                                                    }
                                                }
                                            }
                                        }
                                        if( S2Num < NumLeft ) {
                                            // Specified but not enough crits to fit
                                            Errors.add( new ErrorReport( Name + "\nHas too many crits to fit in the " + LocationIndex.MechLocs[i] + "\n and we could not find another location for it.\nAdd and place the item normally." ) );
                                        } else {
                                            if( S2Num > NumLeft ) {
                                                // we have another item.  This one goes on top, I guess.
                                                S2Num = NumLeft;
                                            }
                                            // now we can allocate the stupid item
                                            m.GetLoadout().AddToQueue( neweq );
                                            m.GetLoadout().RemoveFromQueue( neweq );
                                            m.GetLoadout().AddTo( m.GetLoadout().GetCrits( i ), neweq, S1Index, S1Num );
                                            m.GetLoadout().AddTo( m.GetLoadout().GetCrits( S2Loc ), neweq, S2Index, S2Num );
                                            if( rear ) {
                                                neweq.MountRear( rear );
                                            }
                                            // Clear out the old slots in the retarded criticals array
                                            for( int k = S1Index; k < S1Index + S1Num; k++ ) {
                                                Criticals[i][k] = 0x00;
                                            }
                                            for( int k = S2Index; k < S2Index + S2Num; k++ ) {
                                                Criticals[S2Loc][k] = 0x00;
                                            }
                                        }
                                    }
                                }
                            } else {
                                // easier.
                                int size = neweq.NumCrits();
                                if( m.UsingArtemisIV() && neweq instanceof RangedWeapon ) {
                                    if( m.UsingArtemisIV() ) { ((RangedWeapon) neweq).UseFCS( true, ifMissileGuidance.FCS_ArtemisIV ); }
                                }
                                m.GetLoadout().AddToQueue( neweq );
                                m.GetLoadout().AddTo( neweq, i, j );
                                if( rear ) {
                                    neweq.MountRear( rear );
                                }
                                Criticals[i][j] = 0x00;
                                if( size > 1 ) {
                                    // we'll need to clear out the rest of the
                                    // criticals so we don't "find" this again
                                    for( int k = 1; k < size; k++ ) {
                                        Criticals[i][j + k] = 0x00;
                                    }
                                }
                            }
                        } else {
                            Errors.add( new ErrorReport( lookup, Name ) );
                        }
                    } else {
                        String name = Unused.get( lookup );
                        if( name == null ) {
                            Errors.add( new ErrorReport( "The equipment specified by HMP_REF: " + lookup + " could not be found.\nSkipping that item." ) );
                        } else {
                            Errors.add( new ErrorReport( name + "\nis unused by SSW or is not a valid piece of equipment.\nSkipping that item." ) );
                        }
                    }
                }
            }
        }

        // do we have a targeting computer
        if( HasTC ) {
            boolean TCTB = false;
            if( TCTechBase == AvailableCode.TECH_CLAN ) { TCTB = true; }
            m.GetLoadout().UseTC( true, TCTB );
            m.GetLoadout().AddTo( m.GetLoadout().GetTC(), TCLoc, TCIdx );
        }

        return m;
    }

    public CombatVehicle GetVehicle( String filename, boolean SupressOmniNotification) throws Exception {
        return new CombatVehicle();
    }
    private void BuildHash() {
        Unused.put(new Long(0x25), "IS2 Compact Heat Sinks");

        // cockpit
//        Common.put(new Long(0x0C), "Life Support");
//        Common.put(new Long(0x0D), "Sensors");
//        Common.put(new Long(0x0E), "Cockpit");

        // targeting computers and FCS
//        Common.put(new Long(0x12), "Targeting Computer");
//        Common.put(new Long(0x18), "Artemis IV");

        // specialty equipment
        Common.put(new Long(0x20), "Supercharger");
        Common.put(new Long(0x27), "Null Signature System");
        Common.put(new Long(0x28), "Coolant Pod");

        // industrial equipment
        Common.put(new Long(0xF8), "Combine");
        Common.put(new Long(0xF9), "Lift Hoist");
        Common.put(new Long(0xFA), "Chainsaw");
        Common.put(new Long(0x130), "Backhoe");
        Common.put(new Long(0x131), "Drill");
        Common.put(new Long(0x132), "Rock Cutter");

        // physical weapons
        Sphere.put(new Long(0x11), "Hatchet");
        Sphere.put(new Long(0x1F), "Sword");

        // generic equipment
        Sphere.put(new Long(0x36), "(IS) Laser Anti-Missile System");
        Sphere.put(new Long(0xAF), "(CL) Laser Anti-Missile System");
        Sphere.put(new Long(0x42), "(IS) Anti-Missile System");
        Sphere.put(new Long(0xB4), "(CL) Anti-Missile System");
        Sphere.put(new Long(0x5C), "A-Pod");
        Sphere.put(new Long(0xCA), "A-Pod");
        Sphere.put(new Long(0x64), "Light Active Probe");
        Sphere.put(new Long(0x73), "Beagle Active Probe");
        Sphere.put(new Long(0x74), "Bloodhound Active Probe");
        Sphere.put(new Long(0xCB), "Active Probe");
        Sphere.put(new Long(0x72), "Angel ECM");
        Sphere.put(new Long(0xB3), "Angel ECM");
        Sphere.put(new Long(0x78), "Guardian ECM Suite");
        Sphere.put(new Long(0xCC), "ECM Suite");
        Sphere.put(new Long(0x7A), "TAG");
        Sphere.put(new Long(0xCE), "TAG");
        Sphere.put(new Long(0x65), "Light TAG");
        Sphere.put(new Long(0x75), "C3 Computer (Master)");
        Sphere.put(new Long(0x76), "C3 Computer (Slave)");
        Sphere.put(new Long(0x77), "Improved C3 Computer");
        Clan.put(new Long(0x86), "(IS) Laser Anti-Missile System");
        Clan.put(new Long(0x3B), "(CL) Laser Anti-Missile System");
        Clan.put(new Long(0x92), "(IS) Anti-Missile System");
        Clan.put(new Long(0x40), "(CL) Anti-Missile System");
        Clan.put(new Long(0xAC), "A-Pod");
        Clan.put(new Long(0x56), "A-Pod");
        Clan.put(new Long(0x57), "Active Probe");
        Clan.put(new Long(0xAF), "Light Active Probe");
        Clan.put(new Long(0xC3), "Beagle Active Probe");
        Clan.put(new Long(0xC4), "Bloodhound Active Probe");
        Clan.put(new Long(0xC2), "Angel ECM");
        Clan.put(new Long(0x3F), "Angel ECM");
        Clan.put(new Long(0xC8), "Guardian ECM Suite");
        Clan.put(new Long(0x58), "ECM Suite");
        Clan.put(new Long(0xCA), "TAG");
        Clan.put(new Long(0x5A), "TAG");
        Clan.put(new Long(0xB4), "Light TAG");
        Clan.put(new Long(0xC5), "C3 Computer (Master)");
        Clan.put(new Long(0xC6), "C3 Computer (Slave)");
        Clan.put(new Long(0xC7), "Improved C3 Computer");

        // Energy Weapons
        Unused.put(new Long(0x128), "Clan Plasma Rifle");
        Sphere.put(new Long(0x33), "(IS) ER Large Laser");
        Sphere.put(new Long(0x34), "(IS) ER PPC");
        Sphere.put(new Long(0x35), "(IS) Flamer");
        Sphere.put(new Long(0x37), "(IS) Large Laser");
        Sphere.put(new Long(0x38), "(IS) Medium Laser");
        Sphere.put(new Long(0x39), "(IS) Small Laser");
        Sphere.put(new Long(0x3A), "(IS) PPC");
        Sphere.put(new Long(0x3B), "(IS) Large Pulse Laser");
        Sphere.put(new Long(0x3C), "(IS) Medium Pulse Laser");
        Sphere.put(new Long(0x3D), "(IS) Small Pulse Laser");
        Sphere.put(new Long(0x48), "(IS) Large X-Pulse Laser");
        Sphere.put(new Long(0x49), "(IS) Medium X-Pulse Laser");
        Sphere.put(new Long(0x4A), "(IS) Small X-Pulse Laser");
        Sphere.put(new Long(0x52), "Heavy Flamer");
        //Sphere.put(new Long(0x53), "ISPPCCapacitor"); // HMP uses this code for ERPPC
        Sphere.put(new Long(0x58), "(CL) ER Micro Laser");
        //Sphere.put(new Long(0x59), "ISPPCCapacitor"); // HMP uses this code for standard PPC
        Sphere.put(new Long(0x5A), "(IS) ER Medium Laser");
        Sphere.put(new Long(0x5B), "(IS) ER Small Laser");
        Sphere.put(new Long(0x85), "Vehicle Flamer");
        Sphere.put(new Long(0xA7), "(CL) ER Large Laser");
        Sphere.put(new Long(0xA8), "(CL) ER Medium Laser");
        Sphere.put(new Long(0xA9), "(CL) ER Small Laser");
        Sphere.put(new Long(0xAA), "(CL) ER PPC");
        Sphere.put(new Long(0xAB), "(CL) Flamer");
        Sphere.put(new Long(0xB0), "(CL) Large Pulse Laser");
        Sphere.put(new Long(0xB1), "(CL) Medium Pulse Laser");
        Sphere.put(new Long(0xB2), "(CL) Small Pulse Laser");
        Sphere.put(new Long(0xF4), "(CL) Heavy Large Laser");
        Sphere.put(new Long(0xF5), "(CL) Heavy Medium Laser");
        Sphere.put(new Long(0xF6), "(CL) Heavy Small Laser");
        Sphere.put(new Long(0xDA), "Vehicle Flamer");
        Clan.put(new Long(0x33), "(CL) ER Large Laser");
        Clan.put(new Long(0x34), "(CL) ER Medium Laser");
        Clan.put(new Long(0x35), "(CL) ER Small Laser");
        Clan.put(new Long(0x36), "(CL) ER PPC");
        Clan.put(new Long(0x37), "(CL) Flamer");
        Clan.put(new Long(0x38), "(CL) ER Large Pulse Laser");
        Clan.put(new Long(0x39), "(CL) ER Medium Pulse Laser");
        Clan.put(new Long(0x3A), "(CL) ER Small Pulse Laser");
        Clan.put(new Long(0x3C), "(CL) Large Pulse Laser");
        Clan.put(new Long(0x3D), "(CL) Medium Pulse Laser");
        Clan.put(new Long(0x3E), "(CL) Small Pulse Laser");
        Clan.put(new Long(0x5B), "(CL) ER Micro Laser");
        Clan.put(new Long(0x66), "Vehicle Flamer");
        Clan.put(new Long(0x80), "(CL) Heavy Large Laser");
        Clan.put(new Long(0x81), "(CL) Heavy Medium Laser");
        Clan.put(new Long(0x82), "(CL) Heavy Small Laser");
        Clan.put(new Long(0x83), "(IS) ER Large Laser");
        Clan.put(new Long(0x84), "(IS) ER PPC");
        Clan.put(new Long(0x85), "(IS) Flamer");
        Clan.put(new Long(0x87), "(IS) Large Laser");
        Clan.put(new Long(0x88), "(IS) Medium Laser");
        Clan.put(new Long(0x89), "(IS) Small Laser");
        Clan.put(new Long(0x8A), "(IS) PPC");
        Clan.put(new Long(0x8B), "(IS) Large Pulse Laser");
        Clan.put(new Long(0x8C), "(IS) Medium Pulse Laser");
        Clan.put(new Long(0x8D), "(IS) Small Pulse Laser");
        Clan.put(new Long(0x98), "(IS) Large X-Pulse Laser");
        Clan.put(new Long(0x99), "(IS) Medium X-Pulse Laser");
        Clan.put(new Long(0x9A), "(IS) Small X-Pulse Laser");
        //Clan.put(new Long(0xA3), "ISPPCCapacitor"); // HMP uses this code for ERPPC
        Clan.put(new Long(0xA8), "(CL) Micro Pulse Laser");
        //Clan.put(new Long(0xA9), "ISPPCCapacitor"); // HMP uses this code for PPC
        Clan.put(new Long(0xAA), "(IS) ER Medium Laser");
        Clan.put(new Long(0xAB), "(IS) ER Small Laser");
        Clan.put(new Long(0xD5), "Vehicle Flamer");

        // Ballistics
        Common.put(new Long(0x121), "(IS) Rotary AC/2");
        Common.put(new Long(0x122), "(IS) Rotary AC/5");
        Common.put(new Long(0x124), "(CL) Rotary AC/2");
        Common.put(new Long(0x125), "(CL) Rotary AC/5");
        Sphere.put(new Long(0x3E), "(IS) Autocannon/2");
        Sphere.put(new Long(0x3F), "(IS) Autocannon/5");
        Sphere.put(new Long(0x40), "(IS) Autocannon/10");
        Sphere.put(new Long(0x41), "(IS) Autocannon/20");
        Sphere.put(new Long(0x43), "Long Tom Artillery Cannon");
        Sphere.put(new Long(0x44), "Sniper Artillery Cannon");
        Sphere.put(new Long(0x45), "Thumper Artillery Cannon");
        Sphere.put(new Long(0x46), "(IS) Light Gauss Rifle");
        Sphere.put(new Long(0x47), "(IS) Gauss Rifle");
        Sphere.put(new Long(0x4B), "(IS) LB 2-X AC");
        Sphere.put(new Long(0x4C), "(IS) LB 5-X AC");
        Sphere.put(new Long(0x4D), "(IS) LB 10-X AC");
        Sphere.put(new Long(0x4E), "(IS) LB 20-X AC");
        Sphere.put(new Long(0x4F), "(IS) Machine Gun");
        Sphere.put(new Long(0x50), "(IS) Light AC/2");
        Sphere.put(new Long(0x51), "(IS) Light AC/5");
        Sphere.put(new Long(0x54), "(IS) Ultra AC/2");
        Sphere.put(new Long(0x55), "(IS) Ultra AC/5");
        Sphere.put(new Long(0x56), "(IS) Ultra AC/10");
        Sphere.put(new Long(0x57), "(IS) Ultra AC/20");
        Sphere.put(new Long(0x5E), "(CL) Light Machine Gun");
        Sphere.put(new Long(0x5F), "(CL) Heavy Machine Gun");
        Sphere.put(new Long(0xB5), "(CL) Gauss Rifle");
        Sphere.put(new Long(0xB6), "(CL) LB 2-X AC");
        Sphere.put(new Long(0xB7), "(CL) LB 5-X AC");
        Sphere.put(new Long(0xB8), "(CL) LB 10-X AC");
        Sphere.put(new Long(0xB9), "(CL) LB 20-X AC");
        Sphere.put(new Long(0xBA), "(CL) Machine Gun");
        Sphere.put(new Long(0xBB), "(CL) Ultra AC/2");
        Sphere.put(new Long(0xBC), "(CL) Ultra AC/5");
        Sphere.put(new Long(0xBD), "(CL) Ultra AC/10");
        Sphere.put(new Long(0xBE), "(CL) Ultra AC/20");
        Sphere.put(new Long(0x123), "(IS) Heavy Gauss Rifle");
        Clan.put(new Long(0x41), "(CL) Gauss Rifle");
        Clan.put(new Long(0x42), "(CL) LB 2-X AC");
        Clan.put(new Long(0x43), "(CL) LB 5-X AC");
        Clan.put(new Long(0x44), "(CL) LB 10-X AC");
        Clan.put(new Long(0x45), "(CL) LB 20-X AC");
        Clan.put(new Long(0x46), "(CL) Machine Gun");
        Clan.put(new Long(0x47), "(CL) Ultra AC/2");
        Clan.put(new Long(0x48), "(CL) Ultra AC/5");
        Clan.put(new Long(0x49), "(CL) Ultra AC/10");
        Clan.put(new Long(0x4A), "(CL) Ultra AC/20");
        Clan.put(new Long(0x8E), "(IS) Autocannon/2");
        Clan.put(new Long(0x8F), "(IS) Autocannon/5");
        Clan.put(new Long(0x90), "(IS) Autocannon/10");
        Clan.put(new Long(0x91), "(IS) Autocannon/20");
        Clan.put(new Long(0x96), "(IS) Light Gauss Rifle");
        Clan.put(new Long(0x97), "(IS) Gauss Rifle");
        Clan.put(new Long(0x9B), "(IS) LB 2-X AC");
        Clan.put(new Long(0x9C), "(IS) LB 5-X AC");
        Clan.put(new Long(0x9D), "(IS) LB 10-X AC");
        Clan.put(new Long(0x9E), "(IS) LB 20-X AC");
        Clan.put(new Long(0x9F), "(IS) Machine Gun");
        Clan.put(new Long(0xA0), "(IS) Light AC/2");
        Clan.put(new Long(0xA1), "(IS) Light AC/5");
        Clan.put(new Long(0xA4), "(IS) Ultra AC/2");
        Clan.put(new Long(0xA5), "(IS) Ultra AC/5");
        Clan.put(new Long(0xA6), "(IS) Ultra AC/10");
        Clan.put(new Long(0xA7), "(IS) Ultra AC/20");
        Clan.put(new Long(0xAD), "(CL) Light Machine Gun");
        Clan.put(new Long(0xAE), "(CL) Heavy Machine Gun");
        Clan.put(new Long(0x93), "Long Tom Artillery Cannon");
        Clan.put(new Long(0x94), "Sniper Artillery Cannon");
        Clan.put(new Long(0x95), "Thumper Artillery Cannon");

        // missile weapons
        Common.put(new Long(0xFC), "(CL) ATM-3");
        Common.put(new Long(0xFD), "(CL) ATM-6");
        Common.put(new Long(0xFE), "(CL) ATM-9");
        Common.put(new Long(0xFF), "(CL) ATM-12");
        Common.put(new Long(0x129), "(IS) Rocket Launcher 10");
        Common.put(new Long(0x12A), "(IS) Rocket Launcher 15");
        Common.put(new Long(0x12B), "(IS) Rocket Launcher 20");
        Sphere.put(new Long(0x60), "(IS) LRM-5");
        Sphere.put(new Long(0x61), "(IS) LRM-10");
        Sphere.put(new Long(0x62), "(IS) LRM-15");
        Sphere.put(new Long(0x63), "(IS) LRM-20");
        Sphere.put(new Long(0x67), "(IS) SRM-2");
        Sphere.put(new Long(0x68), "(IS) SRM-4");
        Sphere.put(new Long(0x69), "(IS) SRM-6");
        Sphere.put(new Long(0x6A), "(IS) Streak SRM-2");
        Sphere.put(new Long(0x6B), "(IS) Streak SRM-4");
        Sphere.put(new Long(0x6C), "(IS) Streak SRM-6");
        Sphere.put(new Long(0x89), "(IS) MRM-10");
        Sphere.put(new Long(0x8A), "(IS) MRM-20");
        Sphere.put(new Long(0x8B), "(IS) MRM-30");
        Sphere.put(new Long(0x8C), "(IS) MRM-40");
        Sphere.put(new Long(0x6D), "(IS) Thunderbolt-5");
        Sphere.put(new Long(0x6E), "(IS) Thunderbolt-10");
        Sphere.put(new Long(0x6F), "(IS) Thunderbolt-15");
        Sphere.put(new Long(0x70), "(IS) Thunderbolt-20");
        Sphere.put(new Long(0x66), "(IS) iNarc Launcher");
        Sphere.put(new Long(0x79), "(IS) Narc Missile Beacon");
        Sphere.put(new Long(0x92), "(IS) LRT-5");
        Sphere.put(new Long(0x93), "(IS) LRT-10");
        Sphere.put(new Long(0x94), "(IS) LRT-15");
        Sphere.put(new Long(0x95), "(IS) LRT-20");
        Sphere.put(new Long(0x96), "(IS) SRT-2");
        Sphere.put(new Long(0x97), "(IS) SRT-4");
        Sphere.put(new Long(0x98), "(IS) SRT-6");
        Sphere.put(new Long(0x7B), "(IS) LRM-5 (OS)");
        Sphere.put(new Long(0x7C), "(IS) LRM-10 (OS)");
        Sphere.put(new Long(0x7D), "(IS) LRM-15 (OS)");
        Sphere.put(new Long(0x7E), "(IS) LRM-20 (OS)");
        Sphere.put(new Long(0x7F), "(IS) SRM-2 (OS)");
        Sphere.put(new Long(0x80), "(IS) SRM-4 (OS)");
        Sphere.put(new Long(0x81), "(IS) SRM-6 (OS)");
        Sphere.put(new Long(0x82), "(IS) Streak SRM-2 (OS)");
        Sphere.put(new Long(0x83), "(IS) Streak SRM-4 (OS)");
        Sphere.put(new Long(0x84), "(IS) Streak SRM-6 (OS)");
        Sphere.put(new Long(0x8E), "(IS) MRM-10 (OS)");
        Sphere.put(new Long(0x8F), "(IS) MRM-20 (OS)");
        Sphere.put(new Long(0x90), "(IS) MRM-30 (OS)");
        Sphere.put(new Long(0x91), "(IS) MRM-40 (OS)");
        Sphere.put(new Long(0x99), "(IS) LRM-5 (iOS)");
        Sphere.put(new Long(0x9A), "(IS) LRM-10 (iOS)");
        Sphere.put(new Long(0x9B), "(IS) LRM-15 (iOS)");
        Sphere.put(new Long(0x9C), "(IS) LRM-20 (iOS)");
        Sphere.put(new Long(0x9D), "(IS) SRM-2 (iOS)");
        Sphere.put(new Long(0x9E), "(IS) SRM-4 (iOS)");
        Sphere.put(new Long(0x9f), "(IS) SRM-6 (iOS)");
        Sphere.put(new Long(0xA0), "(IS) Streak SRM-2 (iOS)");
        Sphere.put(new Long(0xA1), "(IS) Streak SRM-4 (iOS)");
        Sphere.put(new Long(0xA2), "(IS) Streak SRM-6 (iOS)");
        Sphere.put(new Long(0xA3), "(IS) MRM-10 (iOS)");
        Sphere.put(new Long(0xA4), "(IS) MRM-20 (iOS)");
        Sphere.put(new Long(0xA5), "(IS) MRM-30 (iOS)");
        Sphere.put(new Long(0xA6), "(IS) MRM-40 (iOS)");
        Sphere.put(new Long(0xBF), "(CL) LRM-5");
        Sphere.put(new Long(0xC0), "(CL) LRM-10");
        Sphere.put(new Long(0xC1), "(CL) LRM-15");
        Sphere.put(new Long(0xC2), "(CL) LRM-20");
        Sphere.put(new Long(0xC3), "(CL) SRM-2");
        Sphere.put(new Long(0xC4), "(CL) SRM-4");
        Sphere.put(new Long(0xC5), "(CL) SRM-6");
        Sphere.put(new Long(0xC6), "(CL) Streak SRM-2");
        Sphere.put(new Long(0xC7), "(CL) Streak SRM-4");
        Sphere.put(new Long(0xC8), "(CL) Streak SRM-6");
        Sphere.put(new Long(0xCD), "(CL) Narc Missile Beacon");
        Sphere.put(new Long(0xD0), "(CL) LRM-5 (OS)");
        Sphere.put(new Long(0xD1), "(CL) LRM-10 (OS)");
        Sphere.put(new Long(0xD2), "(CL) LRM-15 (OS)");
        Sphere.put(new Long(0xD3), "(CL) LRM-20 (OS)");
        Sphere.put(new Long(0xD4), "(CL) SRM-2 (OS)");
        Sphere.put(new Long(0xD5), "(CL) SRM-4 (OS)");
        Sphere.put(new Long(0xD6), "(CL) SRM-6 (OS)");
        Sphere.put(new Long(0xD7), "(CL) Streak SRM-2 (OS)");
        Sphere.put(new Long(0xD8), "(CL) Streak SRM-4 (OS)");
        Sphere.put(new Long(0xD9), "(CL) Streak SRM-6 (OS)");
        Sphere.put(new Long(0xDE), "(CL) LRT-5");
        Sphere.put(new Long(0xDF), "(CL) LRT-10");
        Sphere.put(new Long(0xE0), "(CL) LRT-15");
        Sphere.put(new Long(0xE1), "(CL) LRT-20");
        Sphere.put(new Long(0xE2), "(CL) SRT-2");
        Sphere.put(new Long(0xE3), "(CL) SRT-4");
        Sphere.put(new Long(0xE4), "(CL) SRT-6");
        Sphere.put(new Long(0xE5), "(CL) Streak LRM-5");
        Sphere.put(new Long(0xE6), "(CL) Streak LRM-10");
        Sphere.put(new Long(0xE7), "(CL) Streak LRM-15");
        Sphere.put(new Long(0xE8), "(CL) Streak LRM-20");
        Sphere.put(new Long(0xEA), "(CL) LRM-5 (iOS)");
        Sphere.put(new Long(0xEB), "(CL) LRM-10 (iOS)");
        Sphere.put(new Long(0xEC), "(CL) LRM-15 (iOS)");
        Sphere.put(new Long(0xED), "(CL) LRM-20 (iOS)");
        Sphere.put(new Long(0xEE), "(CL) SRM-2 (iOS)");
        Sphere.put(new Long(0xEF), "(CL) SRM-4 (iOS)");
        Sphere.put(new Long(0xF0), "(CL) SRM-6 (iOS)");
        Sphere.put(new Long(0xF1), "(CL) Streak SRM-2 (iOS)");
        Sphere.put(new Long(0xF2), "(CL) Streak SRM-4 (iOS)");
        Sphere.put(new Long(0xF3), "(CL) Streak SRM-6 (iOS)");
        Clan.put(new Long(0x4B), "(CL) LRM-5");
        Clan.put(new Long(0x4C), "(CL) LRM-10");
        Clan.put(new Long(0x4D), "(CL) LRM-15");
        Clan.put(new Long(0x4E), "(CL) LRM-20");
        Clan.put(new Long(0x4F), "(CL) SRM-2");
        Clan.put(new Long(0x50), "(CL) SRM-4");
        Clan.put(new Long(0x51), "(CL) SRM-6");
        Clan.put(new Long(0x52), "(CL) Streak SRM-2");
        Clan.put(new Long(0x53), "(CL) Streak SRM-4");
        Clan.put(new Long(0x54), "(CL) Streak SRM-6");
        Clan.put(new Long(0x59), "(CL) Narc Missile Beacon");
        Clan.put(new Long(0x5C), "(CL) LRM-5 (OS)");
        Clan.put(new Long(0x5D), "(CL) LRM-10 (OS)");
        Clan.put(new Long(0x5E), "(CL) LRM-15 (OS)");
        Clan.put(new Long(0x5F), "(CL) LRM-20 (OS)");
        Clan.put(new Long(0x60), "(CL) SRM-2 (OS)");
        Clan.put(new Long(0x61), "(CL) SRM-4 (OS)");
        Clan.put(new Long(0x62), "(CL) SRM-6 (OS)");
        Clan.put(new Long(0x63), "(CL) Streak SRM-2 (OS)");
        Clan.put(new Long(0x64), "(CL) Streak SRM-4 (OS)");
        Clan.put(new Long(0x65), "(CL) Streak SRM-6 (OS)");
        Clan.put(new Long(0x6A), "(CL) LRT-5");
        Clan.put(new Long(0x6B), "(CL) LRT-10");
        Clan.put(new Long(0x6C), "(CL) LRT-15");
        Clan.put(new Long(0x6D), "(CL) LRT-20");
        Clan.put(new Long(0x6E), "(CL) SRT-2");
        Clan.put(new Long(0x6F), "(CL) SRT-4");
        Clan.put(new Long(0x70), "(CL) SRT-6");
        Clan.put(new Long(0x71), "(CL) Streak LRM-5");
        Clan.put(new Long(0x72), "(CL) Streak LRM-10");
        Clan.put(new Long(0x73), "(CL) Streak LRM-15");
        Clan.put(new Long(0x74), "(CL) Streak LRM-20");
        Clan.put(new Long(0x76), "(CL) LRM-5 (iOS)");
        Clan.put(new Long(0x77), "(CL) LRM-10 (iOS)");
        Clan.put(new Long(0x78), "(CL) LRM-15 (iOS)");
        Clan.put(new Long(0x79), "(CL) LRM-20 (iOS)");
        Clan.put(new Long(0x7a), "(CL) SRM-2 (iOS)");
        Clan.put(new Long(0x7b), "(CL) SRM-4 (iOS)");
        Clan.put(new Long(0x7c), "(CL) SRM-6 (iOS)");
        Clan.put(new Long(0x7d), "(CL) Streak SRM-2 (iOS)");
        Clan.put(new Long(0x7e), "(CL) Streak SRM-4 (iOS)");
        Clan.put(new Long(0x7f), "(CL) Streak SRM-6 (iOS)");
        Clan.put(new Long(0xB0), "(IS) LRM-5");
        Clan.put(new Long(0xB1), "(IS) LRM-10");
        Clan.put(new Long(0xB2), "(IS) LRM-15");
        Clan.put(new Long(0xB3), "(IS) LRM-20");
        Clan.put(new Long(0xB6), "(IS) iNarc Launcher");
        Clan.put(new Long(0xB7), "(IS) SRM-2");
        Clan.put(new Long(0xB8), "(IS) SRM-4");
        Clan.put(new Long(0xB9), "(IS) SRM-6");
        Clan.put(new Long(0xBA), "(IS) Streak SRM-2");
        Clan.put(new Long(0xBB), "(IS) Streak SRM-4");
        Clan.put(new Long(0xBC), "(IS) Streak SRM-6");
        Clan.put(new Long(0xBD), "(IS) Thunderbolt-5");
        Clan.put(new Long(0xBE), "(IS) Thunderbolt-10");
        Clan.put(new Long(0xBF), "(IS) Thunderbolt-15");
        Clan.put(new Long(0xC0), "(IS) Thunderbolt-20");
        Clan.put(new Long(0xC9), "(IS) Narc Missile Beacon");
        Clan.put(new Long(0xCB), "(IS) LRM-5 (OS)");
        Clan.put(new Long(0xCC), "(IS) LRM-10 (OS)");
        Clan.put(new Long(0xCD), "(IS) LRM-15 (OS)");
        Clan.put(new Long(0xCE), "(IS) LRM-20 (OS)");
        Clan.put(new Long(0xCF), "(IS) SRM-2 (OS)");
        Clan.put(new Long(0xD0), "(IS) SRM-4 (OS)");
        Clan.put(new Long(0xD1), "(IS) SRM-6 (OS)");
        Clan.put(new Long(0xD2), "(IS) Streak SRM-2 (OS)");
        Clan.put(new Long(0xD3), "(IS) Streak SRM-4 (OS)");
        Clan.put(new Long(0xD4), "(IS) Streak SRM-6 (OS)");
        Clan.put(new Long(0xD9), "(IS) MRM-10");
        Clan.put(new Long(0xDA), "(IS) MRM-20");
        Clan.put(new Long(0xDB), "(IS) MRM-30");
        Clan.put(new Long(0xDC), "(IS) MRM-40");
        Clan.put(new Long(0xDE), "(IS) MRM-10 (OS)");
        Clan.put(new Long(0xDF), "(IS) MRM-20 (OS)");
        Clan.put(new Long(0xE0), "(IS) MRM-30 (OS)");
        Clan.put(new Long(0xE1), "(IS) MRM-40 (OS)");
        Clan.put(new Long(0xE2), "(IS) LRT-5");
        Clan.put(new Long(0xE3), "(IS) LRT-10");
        Clan.put(new Long(0xE4), "(IS) LRT-15");
        Clan.put(new Long(0xE5), "(IS) LRT-20");
        Clan.put(new Long(0xE6), "(IS) SRT-2");
        Clan.put(new Long(0xE7), "(IS) SRT-4");
        Clan.put(new Long(0xE8), "(IS) SRT-6");
        Clan.put(new Long(0xE9), "(IS) LRM-5 (iOS)");
        Clan.put(new Long(0xEA), "(IS) LRM-10 (iOS)");
        Clan.put(new Long(0xEB), "(IS) LRM-15 (iOS)");
        Clan.put(new Long(0xEC), "(IS) LRM-20 (iOS)");
        Clan.put(new Long(0xED), "(IS) SRM-2 (iOS)");
        Clan.put(new Long(0xEE), "(IS) SRM-4 (iOS)");
        Clan.put(new Long(0xEf), "(IS) SRM-6 (iOS)");
        Clan.put(new Long(0xF0), "(IS) Streak SRM-2 (iOS)");
        Clan.put(new Long(0xF1), "(IS) Streak SRM-4 (iOS)");
        Clan.put(new Long(0xF2), "(IS) Streak SRM-6 (iOS)");
        Clan.put(new Long(0xF3), "(IS) MRM-10 (iOS)");
        Clan.put(new Long(0xF4), "(IS) MRM-20 (iOS)");
        Clan.put(new Long(0xF5), "(IS) MRM-30 (iOS)");
        Clan.put(new Long(0xF6), "(IS) MRM-40 (iOS)");

        // Artillery
        Clan.put(new Long(0xD7), "(IS) Sniper");
        Clan.put(new Long(0xD8), "(IS) Thumper");
        Clan.put(new Long(0x55), "(CL) Arrow IV Missile");
        Clan.put(new Long(0x68), "(IS) Sniper");
        Clan.put(new Long(0x69), "(IS) Thumper");
        Sphere.put(new Long(0x87), "(IS) Sniper");
        Sphere.put(new Long(0x88), "(IS) Thumper");
        Sphere.put(new Long(0x71), "(IS) Arrow IV Missile");
        Sphere.put(new Long(0xC9), "(CL) Arrow IV Missile");
        Sphere.put(new Long(0xDC), "(IS) Sniper");
        Sphere.put(new Long(0xDD), "(IS) Thumper");

        // ammunition
        Common.put(new Long(0x10000028cL), "(CL) @ ATM-3 (ER)");
        Common.put(new Long(0x20000028cL), "(CL) @ ATM-3 (HE)");
        Common.put(new Long(0x10000028dL), "(CL) @ ATM-6 (ER)");
        Common.put(new Long(0x20000028dL), "(CL) @ ATM-6 (HE)");
        Common.put(new Long(0x10000028eL), "(CL) @ ATM-9 (ER)");
        Common.put(new Long(0x20000028eL), "(CL) @ ATM-9 (HE)");
        Common.put(new Long(0x10000028fL), "(CL) @ ATM-12 (ER)");
        Common.put(new Long(0x20000028fL), "(CL) @ ATM-12 (HE)");
        Common.put(new Long(0x28c), "(CL) @ ATM-3");
        Common.put(new Long(0x28d), "(CL) @ ATM-6");
        Common.put(new Long(0x28e), "(CL) @ ATM-9");
        Common.put(new Long(0x28f), "(CL) @ ATM-12");
        Common.put(new Long(0x2B1), "(IS) @ Rotary AC/2");
        Common.put(new Long(0x2B2), "(IS) @ Rotary AC/5");
        Common.put(new Long(0x2b4), "(CL) @ Rotary AC/2");
        Common.put(new Long(0x2b5), "(CL) @ Rotary AC/5");
        // special for ammo mutator
        // 28c-28f = atm
        //Common.put(new Long(0x100000298L), "ISLBXAC2 Ammo (THB)");
        //Common.put(new Long(0x100000299L), "ISLBXAC5 Ammo (THB)");
        //Common.put(new Long(0x10000029AL), "ISLBXAC20 Ammo (THB)");
        Sphere.put(new Long(0x01CE), "(IS) @ AC/2");
        Sphere.put(new Long(0x01CF), "(IS) @ AC/5");
        Sphere.put(new Long(0x01D0), "(IS) @ AC/10");
        Sphere.put(new Long(0x01d1), "(IS) @ AC/20");
        Sphere.put(new Long(0x01d2), "(IS) @ Anti-Missile System");
        Sphere.put(new Long(0x01d3), "@ Long Tom Cannon");
        Sphere.put(new Long(0x01d4), "@ Sniper Cannon");
        Sphere.put(new Long(0x01d5), "@ Thumper Cannon");
        Sphere.put(new Long(0x01d6), "(IS) @ Light Gauss Rifle");
        Sphere.put(new Long(0x01d7), "@ Gauss Rifle");
        Sphere.put(new Long(0x01db), "(IS) @ LB 2-X AC (Slug)");
        Sphere.put(new Long(0x01dc), "(IS) @ LB 5-X AC (Slug)");
        Sphere.put(new Long(0x01dd), "(IS) @ LB 10-X AC (Slug)");
        Sphere.put(new Long(0x01de), "(IS) @ LB 20-X AC (Slug)");
        Sphere.put(new Long(0x01df), "@ Machine Gun");
        Sphere.put(new Long(0x1e0), "@ Light AC/2");
        Sphere.put(new Long(0x1e1), "@ Light AC/5");
        Sphere.put(new Long(0x1e2), "@ Heavy Flamer");
        Sphere.put(new Long(0x01e4), "(IS) @ Ultra AC/2");
        Sphere.put(new Long(0x01e5), "(IS) @ Ultra AC/5");
        Sphere.put(new Long(0x01e6), "(IS) @ Ultra AC/10");
        Sphere.put(new Long(0x01e7), "(IS) @ Ultra AC/20");
        Sphere.put(new Long(0x01EE), "@ Light Machine Gun");
        Sphere.put(new Long(0x01EF), "@ Heavy Machine Gun");
        Sphere.put(new Long(0x01f0), "(IS) @ LRM-5");
        Sphere.put(new Long(0x01f1), "(IS) @ LRM-10");
        Sphere.put(new Long(0x01f2), "(IS) @ LRM-15");
        Sphere.put(new Long(0x01f3), "(IS) @ LRM-20");
        Sphere.put(new Long(0x01f6), "@ iNarc (Homing)");
        Sphere.put(new Long(0x01f7), "@ SRM-2");
        Sphere.put(new Long(0x01f8), "@ SRM-4");
        Sphere.put(new Long(0x01f9), "@ SRM-6");
        Sphere.put(new Long(0x01fa), "(IS) @ Streak SRM-2");
        Sphere.put(new Long(0x01fb), "(IS) @ Streak SRM-4");
        Sphere.put(new Long(0x01FC), "(IS) @ Streak SRM-6");
        Sphere.put(new Long(0x01FD), "@ Thunderbolt-5");
        Sphere.put(new Long(0x01FE), "@ Thunderbolt-10");
        Sphere.put(new Long(0x01FF), "@ Thunderbolt-15");
        Sphere.put(new Long(0x0200), "@ Thunderbolt-20");
        Sphere.put(new Long(0x0201), "(IS) @ Arrow IV (Homing)");
        Sphere.put(new Long(0x0209), "(IS) @ Narc (Homing)");
        Sphere.put(new Long(0x0215), "@ Vehicle Flamer");
        Sphere.put(new Long(0x0217), "(IS) @ Sniper");
        Sphere.put(new Long(0x0218), "(IS) @ Thumper");
        Sphere.put(new Long(0x0219), "(IS) @ MRM-10");
        Sphere.put(new Long(0x021a), "(IS) @ MRM-20");
        Sphere.put(new Long(0x021b), "(IS) @ MRM-30");
        Sphere.put(new Long(0x021c), "(IS) @ MRM-40");
        Sphere.put(new Long(0x0222), "(IS) @ LRT-5 (Torpedo)");
        Sphere.put(new Long(0x0223), "(IS) @ LRT-10 (Torpedo)");
        Sphere.put(new Long(0x0224), "(IS) @ LRT-15 (Torpedo)");
        Sphere.put(new Long(0x0225), "(IS) @ LRT-20 (Torpedo)");
        Sphere.put(new Long(0x0226), "@ SRT-2 (Torpedo)");
        Sphere.put(new Long(0x0227), "@ SRT-4 (Torpedo)");
        Sphere.put(new Long(0x0228), "@ SRT-6 (Torpedo)");
        Sphere.put(new Long(0x0244), "(CL) @ Anti-Missile System");
        Sphere.put(new Long(0x0245), "@ Gauss Rifle");
        Sphere.put(new Long(0x0246), "(CL) @ LB 2-X AC (Slug)");
        Sphere.put(new Long(0x0247), "(CL) @ LB 5-X AC (Slug)");
        Sphere.put(new Long(0x0248), "(CL) @ LB 10-X AC (Slug)");
        Sphere.put(new Long(0x0249), "(CL) @ LB 20-X AC (Slug)");
        Sphere.put(new Long(0x024A), "@ Machine Gun");
        Sphere.put(new Long(0x024B), "(CL) @ Ultra AC/2");
        Sphere.put(new Long(0x024C), "(CL) @ Ultra AC/5");
        Sphere.put(new Long(0x024D), "(CL) @ Ultra AC/10");
        Sphere.put(new Long(0x024E), "(CL) @ Ultra AC/20");
        Sphere.put(new Long(0x024F), "(CL) @ LRM-5");
        Sphere.put(new Long(0x0250), "(CL) @ LRM-10");
        Sphere.put(new Long(0x0251), "(CL) @ LRM-15");
        Sphere.put(new Long(0x0252), "(CL) @ LRM-20");
        Sphere.put(new Long(0x0253), "@ SRM-2");
        Sphere.put(new Long(0x0254), "@ SRM-4");
        Sphere.put(new Long(0x0255), "@ SRM-6");
        Sphere.put(new Long(0x0256), "(CL) @ Streak SRM-2");
        Sphere.put(new Long(0x0257), "(CL) @ Streak SRM-4");
        Sphere.put(new Long(0x0258), "(CL) @ Streak SRM-6");
        Sphere.put(new Long(0x0259), "(CL) @ Arrow IV (Homing)");
        Sphere.put(new Long(0x025D), "(CL) @ Narc (Homing)");
        Sphere.put(new Long(0x026A), "@ Vehicle Flamer");
        Sphere.put(new Long(0x026C), "@ Sniper");
        Sphere.put(new Long(0x026D), "@ Thumper");
        Sphere.put(new Long(0x026E), "(CL) @ LRT-5 (Torpedo)");
        Sphere.put(new Long(0x026F), "(CL) @ LRT-10 (Torpedo)");
        Sphere.put(new Long(0x0270), "(CL) @ LRT-15 (Torpedo)");
        Sphere.put(new Long(0x0271), "(CL) @ LRT-20 (Torpedo)");
        Sphere.put(new Long(0x0272), "@ SRT-2");
        Sphere.put(new Long(0x0273), "@ SRT-4");
        Sphere.put(new Long(0x0274), "@ SRT-6");
        Sphere.put(new Long(0x0275), "@ Streak LRM-5");
        Sphere.put(new Long(0x0276), "@ Streak LRM-10");
        Sphere.put(new Long(0x0277), "@ Streak LRM-15");
        Sphere.put(new Long(0x0278), "@ Streak LRM-20");
        Sphere.put(new Long(0x02b3), "(IS) @ Heavy Gauss Rifle");
        // 1db-1de = is
        // 1d2-1d5 = cl
        // 298-299 = thb
        // 22B-22E = IS on clan
        // 246-249 = clan on IS
        Sphere.put(new Long(0x1000001dbL), "(IS) @ LB 2-X AC (Cluster)");
        Sphere.put(new Long(0x1000001dcL), "(IS) @ LB 5-X AC (Cluster)");
        Sphere.put(new Long(0x1000001ddL), "(IS) @ LB 10-X AC (Cluster)");
        Sphere.put(new Long(0x1000001deL), "(IS) @ LB 20-X AC (Cluster)");
        Sphere.put(new Long(0x100000246L), "(CL) @ LB 2-X AC (Cluster)");
        Sphere.put(new Long(0x100000247L), "(CL) @ LB 5-X AC (Cluster)");
        Sphere.put(new Long(0x100000248L), "(CL) @ LB 10-X AC (Cluster)");
        Sphere.put(new Long(0x100000249L), "(CL) @ LB 20-X AC (Cluster)");
        // Clan.put(new Long(0x01ce), "CLAC2 Ammo");
        Clan.put(new Long(0x01d0), "(CL) @ Anti-Missile System");
        // Clan.put(new Long(0x01cf), "CLAC5 Ammo");
        Clan.put(new Long(0x01d1), "@ Gauss Rifle");
        Clan.put(new Long(0x01d2), "(CL) @ LB 2-X AC (Slug)");
        Clan.put(new Long(0x01d3), "(CL) @ LB 5-X AC (Slug)");
        Clan.put(new Long(0x01d4), "(CL) @ LB 10-X AC (Slug)");
        Clan.put(new Long(0x01d5), "(CL) @ LB 20-X AC (Slug)");
        Clan.put(new Long(0x01d6), "@ Machine Gun");
        Clan.put(new Long(0x01d7), "(CL) @ Ultra AC/2");
        Clan.put(new Long(0x01d8), "(CL) @ Ultra AC/5");
        Clan.put(new Long(0x01d9), "(CL) @ Ultra AC/10");
        Clan.put(new Long(0x01da), "(CL) @ Ultra AC/20");
        Clan.put(new Long(0x01db), "(CL) @ LRM-5");
        Clan.put(new Long(0x01dc), "(CL) @ LRM-10");
        Clan.put(new Long(0x01dd), "(CL) @ LRM-15");
        Clan.put(new Long(0x01de), "(CL) @ LRM-20");
        Clan.put(new Long(0x01df), "@ SRM-2");
        Clan.put(new Long(0x01e0), "@ SRM-4");
        Clan.put(new Long(0x01e1), "@ SRM-6");
        Clan.put(new Long(0x01e2), "(CL) @ Streak SRM-2");
        Clan.put(new Long(0x01e3), "(CL) @ Streak SRM-4");
        Clan.put(new Long(0x01e4), "(CL) @ Streak SRM-6");
        Clan.put(new Long(0x01e5), "(CL) @ Arrow IV (Homing)");
        Clan.put(new Long(0x01e9), "(CL) @ Narc (Homing)");
        // Clan.put(new Long(0x0215), "CLFlamer Ammo");
        Clan.put(new Long(0x01f0), "(CL) @ LRM-5");
        Clan.put(new Long(0x01f1), "(CL) @ LRM-10");
        Clan.put(new Long(0x01f2), "(CL) @ LRM-15");
        Clan.put(new Long(0x01f3), "(CL) @ LRM-20");
        Clan.put(new Long(0x01f6), "@ Vehicle Flamer");
        Clan.put(new Long(0x01f8), "@ Sniper");
        Clan.put(new Long(0x01f9), "@ Thumper");
        Clan.put(new Long(0x01fa), "(CL) @ LRT-5 (Torpedo)");
        Clan.put(new Long(0x01fb), "(CL) @ LRT-10 (Torpedo)");
        Clan.put(new Long(0x01fc), "(CL) @ LRT-15 (Torpedo)");
        Clan.put(new Long(0x01fd), "(CL) @ LRT-20 (Torpedo)");
        Clan.put(new Long(0x01fe), "@ SRT-2");
        Clan.put(new Long(0x01ff), "@ SRT-4");
        Clan.put(new Long(0x0200), "@ SRT-6");
        Clan.put(new Long(0x0201), "@ Streak LRM-5");
        Clan.put(new Long(0x0202), "@ Streak LRM-10");
        Clan.put(new Long(0x0203), "@ Streak LRM-15");
        Clan.put(new Long(0x0204), "@ Streak LRM-20");
        Clan.put(new Long(0x021E), "@ AC/2");
        Clan.put(new Long(0x021F), "@ AC/5");
        Clan.put(new Long(0x0220), "@ AC/10");
        Clan.put(new Long(0x0221), "@ AC/20");
        Clan.put(new Long(0x0222), "(IS) @ Anti-Missile System");
        Clan.put(new Long(0x0223), "@ Long Tom Cannon");
        Clan.put(new Long(0x0224), "@ Sniper Cannon");
        Clan.put(new Long(0x0225), "@ Thumper Cannon");
        Clan.put(new Long(0x0226), "(IS) @ Light Gauss Rifle");
        Clan.put(new Long(0x0227), "@ Gauss Rifle");
        // Clan.put(new Long(0x0228), "CLSRTorpedo6 Ammo");
        Clan.put(new Long(0x022B), "(IS) @ LB 2-X AC (Slug)");
        Clan.put(new Long(0x022C), "(IS) @ LB 5-X AC (Slug)");
        Clan.put(new Long(0x022D), "(IS) @ LB 10-X AC (Slug)");
        Clan.put(new Long(0x022E), "(IS) @ LB 20-X AC (Slug)");
        Clan.put(new Long(0x022F), "@ Machine Gun");
        Clan.put(new Long(0x0230), "@ Light AC/2");
        Clan.put(new Long(0x0231), "@ Light AC/5");
        Clan.put(new Long(0x0234), "(IS) @ Ultra AC/2");
        Clan.put(new Long(0x0235), "(IS) @ Ultra AC/5");
        Clan.put(new Long(0x0236), "(IS) @ Ultra AC/10");
        Clan.put(new Long(0x0237), "(IS) @ Ultra AC/20");
        Clan.put(new Long(0x023d), "@ Light Machine Gun");
        Clan.put(new Long(0x023e), "@ Heavy Machine Gun");
        Clan.put(new Long(0x0240), "(IS) @ LRM-5");
        Clan.put(new Long(0x0241), "(IS) @ LRM-10");
        Clan.put(new Long(0x0242), "(IS) @ LRM-15");
        Clan.put(new Long(0x0243), "(IS) @ LRM-20");
        Clan.put(new Long(0x0246), "@ iNarc (Homing)");
        Clan.put(new Long(0x0247), "@ SRM-2");
        Clan.put(new Long(0x0248), "@ SRM-4");
        Clan.put(new Long(0x0249), "@ SRM-6");
        Clan.put(new Long(0x024A), "(IS) @ Streak SRM-2");
        Clan.put(new Long(0x024B), "(IS) @ Streak SRM-4");
        Clan.put(new Long(0x024C), "(IS) @ Streak SRM-6");
        Clan.put(new Long(0x024D), "@ Thunderbolt-5");
        Clan.put(new Long(0x024E), "@ Thunderbolt-10");
        Clan.put(new Long(0x024F), "@ Thunderbolt-15");
        Clan.put(new Long(0x0250), "@ Thunderbolt-20");
        Clan.put(new Long(0x0259), "(IS) @ Narc (Homing)");
        Clan.put(new Long(0x0265), "@ Vehicle Flamer");
        Clan.put(new Long(0x0267), "@ Sniper");
        Clan.put(new Long(0x0268), "@ Thumper");
        Clan.put(new Long(0x0269), "(IS) @ MRM-10");
        Clan.put(new Long(0x026A), "(IS) @ MRM-20");
        Clan.put(new Long(0x026B), "(IS) @ MRM-30");
        Clan.put(new Long(0x026C), "(IS) @ MRM-40");
        Clan.put(new Long(0x0272), "(IS) @ LRT-15 (Torpedo)");
        Clan.put(new Long(0x0273), "(IS) @ LRT-20 (Torpedo)");
        Clan.put(new Long(0x0274), "(IS) @ LRT-5 (Torpedo)");
        Clan.put(new Long(0x0275), "(IS) @ LRT-10 (Torpedo)");
        Clan.put(new Long(0x0276), "@ SRT-4");
        Clan.put(new Long(0x0277), "@ SRT-2");
        Clan.put(new Long(0x0278), "@ SRT-6");
        Clan.put(new Long(0x10000022bL), "(IS) @ LB 2-X AC (Slug)");
        Clan.put(new Long(0x10000022cL), "(IS) @ LB 5-X AC (Slug)");
        Clan.put(new Long(0x10000022dL), "(IS) @ LB 10-X AC (Slug)");
        Clan.put(new Long(0x10000022eL), "(IS) @ LB 20-X AC (Slug)");
        Clan.put(new Long(0x1000001d2L), "(CL) @ LB 2-X AC (Cluster)");
        Clan.put(new Long(0x1000001d3L), "(CL) @ LB 5-X AC (Cluster)");
        Clan.put(new Long(0x1000001d4L), "(CL) @ LB 10-X AC (Cluster)");
        Clan.put(new Long(0x1000001d5L), "(CL) @ LB 20-X AC (Cluster)");

        // unused items, from older books usually
        Unused.put(new Long(0x11D), "ISTHBAngelECMSuite");
        Unused.put(new Long(0x11E), "ISTHBBloodhoundActiveProbe");
        Unused.put(new Long(0x0216), "ISLongTom Ammo");
        Unused.put(new Long(0x026B), "CLLongTom Ammo");
        Unused.put(new Long(0x67), "CLLongTomArtillery");
        Unused.put(new Long(0x75), "CLGrenadeLauncher");
        Unused.put(new Long(0xCF), "Thunderbolt (OS)");
        Unused.put(new Long(0xD6), "ISLongTomArtillery");
        Unused.put(new Long(0xDD), "Grenade Launcher");
        Unused.put(new Long(0x01f7), "CLLongTom Ammo");
        Unused.put(new Long(0x0266), "Long Tom Ammo");
        Unused.put(new Long(0x2b6), "CLRotaryAC10 Ammo");
        Unused.put(new Long(0x2b7), "CLRotaryAC20 Ammo");
        Unused.put(new Long(0x2BC), "Mortar/1 Ammo (THB)");
        Unused.put(new Long(0x2BD), "Mortar/2 Ammo (THB)");
        Unused.put(new Long(0x2BE), "Mortar/4 Ammo (THB)");
        Unused.put(new Long(0x2BF), "Mortar/8 Ammo (THB)");
        Unused.put(new Long(0x29E), "ELRM-5 Ammo (THB)");
        Unused.put(new Long(0x29F), "ELRM-10 Ammo (THB)");
        Unused.put(new Long(0x2A0), "ELRM-15 Ammo (THB)");
        Unused.put(new Long(0x2A1), "ELRM-20 Ammo (THB)");
        Unused.put(new Long(0x2A2), "LR DFM-5 Ammo (THB)");
        Unused.put(new Long(0x2A3), "LR DFM-10 Ammo (THB)");
        Unused.put(new Long(0x2A4), "LR DFM-15 Ammo (THB)");
        Unused.put(new Long(0x2A5), "LR DFM-20 Ammo (THB)");
        Unused.put(new Long(0x2A6), "SR DFM-2 Ammo (THB)");
        Unused.put(new Long(0x2A7), "SR DFM-4 Ammo (THB)");
        Unused.put(new Long(0x2A8), "SR DFM-6 Ammo (THB)");
        Unused.put(new Long(0x2A9), "Thunderbolt-5 Ammo (THB)");
        Unused.put(new Long(0x2AA), "Thunderbolt-10 Ammo (THB)");
        Unused.put(new Long(0x2AB), "Thunderbolt-15 Ammo (THB)");
        Unused.put(new Long(0x2AC), "Thunderbolt-20 Ammo (THB)");
        Unused.put(new Long(0x24), "Blue Shield (UB)");
        Unused.put(new Long(0x86), "ISLongTomArtillery");
        Unused.put(new Long(0x8D), "Grenade Launcher");
        Unused.put(new Long(0xCF), "Thunderbolt (OS)");
        Unused.put(new Long(0xDB), "CLLongTomArtillery");
        Unused.put(new Long(0xE9), "CLGrenadeLauncher");
        Unused.put(new Long(0x290), "SB Gauss Rifle Ammo (UB)");
        Unused.put(new Long(0x291), "Caseless AC/2 Ammo (THB)");
        Unused.put(new Long(0x292), "Caseless AC/5 Ammo (THB)");
        Unused.put(new Long(0x293), "Caseless AC/10 Ammo (THB)");
        Unused.put(new Long(0x294), "Caseless AC/20 Ammo (THB)");
        Unused.put(new Long(0x295), "Heavy AC/2 Ammo (THB)");
        Unused.put(new Long(0x296), "Heavy AC/5 Ammo (THB)");
        Unused.put(new Long(0x297), "Heavy AC/10 Ammo (THB)");
        Unused.put(new Long(0x298), "ISLBXAC2 Ammo (THB)");
        Unused.put(new Long(0x299), "ISLBXAC5 Ammo (THB)");
        Unused.put(new Long(0x29A), "ISLBXAC20 Ammo (THB)");
        Unused.put(new Long(0x29B), "IS Ultra AC/2 Ammo (THB)");
        Unused.put(new Long(0x29C), "IS Ultra AC/10 Ammo (THB)");
        Unused.put(new Long(0x29D), "IS Ultra AC/20 Ammo (THB)");
        Unused.put(new Long(0x133), "CLStreakLRM5 (OS)"); // ?
        Unused.put(new Long(0x134), "CLStreakLRM10 (OS)"); // ?
        Unused.put(new Long(0x135), "CLStreakLRM15 (OS)");// ?
        Unused.put(new Long(0x136), "CLStreakLRM20 (OS)");// ?
        Unused.put(new Long(0x12C), "Mortar/1 (THB)");
        Unused.put(new Long(0x12D), "Mortar/2 (THB)");
        Unused.put(new Long(0x12E), "Mortar/4 (THB)");
        Unused.put(new Long(0x12F), "Mortar/8 (THB)");
        Unused.put(new Long(0x100), "SB Gauss Rifle (UB)");
        Unused.put(new Long(0x101), "Caseless AC/2 (THB)");
        Unused.put(new Long(0x102), "Caseless AC/5 (THB)");
        Unused.put(new Long(0x103), "Caseless AC/10 (THB)");
        Unused.put(new Long(0x104), "Caseless AC/20 (THB)");
        Unused.put(new Long(0x105), "Heavy AC/2 (THB)");
        Unused.put(new Long(0x106), "Heavy AC/5 (THB)");
        Unused.put(new Long(0x107), "Heavy AC/10 (THB)");
        Unused.put(new Long(0x108), "ISTHBLBXAC2");
        Unused.put(new Long(0x109), "ISTHBLBXAC5");
        Unused.put(new Long(0x10A), "ISTHBLBXAC20");
        Unused.put(new Long(0x10B), "ISUltraAC2 (THB)");
        Unused.put(new Long(0x10C), "ISUltraAC10 (THB)");
        Unused.put(new Long(0x10D), "ISUltraAC20 (THB)");
        Unused.put(new Long(0x10E), "ELRM-5 (THB)");
        Unused.put(new Long(0x10F), "ELRM-10 (THB)");
        Unused.put(new Long(0x110), "ELRM-15 (THB)");
        Unused.put(new Long(0x111), "ELRM-20 (THB)");
        Unused.put(new Long(0x112), "LR DFM-5 (THB)");
        Unused.put(new Long(0x113), "LR DFM-10 (THB)");
        Unused.put(new Long(0x114), "LR DFM-15 (THB)");
        Unused.put(new Long(0x115), "LR DFM-20 (THB)");
        Unused.put(new Long(0x116), "SR DFM-2 (THB)");
        Unused.put(new Long(0x117), "SR DFM-4 (THB)");
        Unused.put(new Long(0x118), "SR DFM-6 (THB)");
        Unused.put(new Long(0x119), "Thunderbolt-5 (THB)");
        Unused.put(new Long(0x11A), "Thunderbolt-10 (THB)");
        Unused.put(new Long(0x11B), "Thunderbolt-15 (THB)");
        Unused.put(new Long(0x11C), "Thunderbolt-20 (THB)");
        Unused.put(new Long(0x11F), "Watchdog ECM (THB)");
        Unused.put(new Long(0x120), "IS Laser AMS (THB)");
        Unused.put(new Long(0x2B), "Claw (THB)");
        Unused.put(new Long(0x2C), "Mace (THB)");
        Unused.put(new Long(0x2d), "Armored Cowl");
        Unused.put(new Long(0x2e), "Buzzsaw (UB)");
        Unused.put(new Long(0x13), "Turret");
        Unused.put(new Long(0x1a), "Variable Range TargSys");
        Unused.put(new Long(0x1b), "Multi-Trac II");
        Unused.put(new Long(0x1e), "Jump Booster");
        Unused.put(new Long(0x126), "CLRotaryAC10");
        Unused.put(new Long(0x127), "CLRotaryAC20");
    }

    private int DecodeRulesLevel( int OldRules ) {
        switch( OldRules ) {
            case 1:
                return AvailableCode.RULES_INTRODUCTORY;
            case 2:
                // this will be checked for advanced rules anyway
                return AvailableCode.RULES_TOURNAMENT;
            case 3:
                return AvailableCode.RULES_EXPERIMENTAL;
            default:
                return -1;
        }
    }

    private short readUnsignedByte(DataInputStream dis) throws Exception {
        short b = dis.readByte();
        b += b < 0 ? 256 : 0;
        return b;
    }

    private int readUnsignedShort(DataInputStream dis) throws Exception {
        int b2 = readUnsignedByte(dis);

        int b1 = readUnsignedByte(dis);
        b1 <<= 8;

        return b1 + b2;
    }

    private long readUnsignedInt(DataInputStream dis) throws Exception {
        long b4 = readUnsignedByte(dis);

        long b3 = readUnsignedByte(dis);
        b3 <<= 8;

        long b2 = readUnsignedByte(dis);
        b2 <<= 16;

        long b1 = readUnsignedByte(dis);
        b1 <<= 32;

        return b1 + b2 + b3 + b4;
    }

    private boolean IsRearMounted( long critical ) {
        return ( critical & 0xFFFF0000 ) != 0;
    }

    private Long GetLookupNum( long l ) {
        // According to HMPFile.java, this will return the actual lookup number.
        // the first two bytes of the critical location number are the type.
        Long retval = new Long( l & 0xFFFF );
        return retval;
    }

    private String BuildLookupName( String name, int mechbase, int techbase ) {
        if( mechbase == AvailableCode.TECH_BOTH ) {
            if( techbase == 0 ) {
                return "(IS) " + name;
            } else {
                return "(CL) " + name;
            }
        }
        return name;
    }

    private class ErrorReport {
        private long HMPNumber;
        private String EQName;
        private String CustomError = null;
        private boolean HasCustom = false;

        public ErrorReport( Long num, String name ) {
            HMPNumber = num;
            EQName = name;
        }

        public ErrorReport( String custom ) {
            CustomError = custom;
            HasCustom = true;
        }

        public String GetErrorReport() {
            if( HasCustom ) {
                return CustomError;
            } else {
                return "The following item could not be loaded:\nHMP_REF: " + HMPNumber + ", NAME: " + EQName;
            }
        }
    }
}

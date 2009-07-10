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

package ssw.filehandlers;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Vector;
import ssw.Constants;
import ssw.components.AvailableCode;
import ssw.components.DataFactory;
import ssw.components.HeatSink;
import ssw.components.JumpJet;
import ssw.components.LocationIndex;
import ssw.components.Mech;
import ssw.components.abPlaceable;
import ssw.visitors.ifVisitor;

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

    public Mech GetMech( String filename ) throws Exception {
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
        if( m.GetTechBase() == AvailableCode.TECH_CLAN ) {
            if( m.GetEra() < AvailableCode.ERA_SUCCESSION ) {
                m.SetEra( AvailableCode.ERA_SUCCESSION );
            }
        }
        // although we can still have a mixed chassis earlier, most designs will
        // be Clan Invasion era
        if( m.GetTechBase() == AvailableCode.TECH_BOTH ) {
            if( m.GetEra() < AvailableCode.ERA_CLAN_INVASION ) {
                m.SetEra( AvailableCode.ERA_CLAN_INVASION );
            }
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
                Errors.add( new ErrorReport( "This 'Mech is flagged as an OmniMech but only one loadout will be created." ) );
                m.SetBiped();
                break;
            case 11:
                // quad omnimech
                Errors.add( new ErrorReport( "This 'Mech is flagged as an OmniMech but only one loadout will be created." ) );
                m.SetQuad();
                break;
            default:
                throw new Exception( "Invalid Motive Type: " + MotiveType );
        }

        int IntStrucType = readUnsignedShort(FR);
        String IntStrucLookup = "";
        switch( IntStrucType ) {
            case 0:
                IntStrucLookup = "Standard Structure";
                break;
            case 1:
                IntStrucLookup = BuildLookupName( "Endo-Steel", m.GetTechBase(), IntStrucTechBase );
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
                EngineLookup = BuildLookupName( "XL Engine", m.GetTechBase(), EngineTechBase );
                break;
            case 2:
                EngineLookup = BuildLookupName( "XXL Engine", m.GetTechBase(), EngineTechBase );
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
                HSLookup = BuildLookupName( "Double Heat Sink", m.GetTechBase(), HSTechBase );
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
                ArmorLookup = BuildLookupName( "Ferro-Fibrous", m.GetTechBase(), ArmorTechBase );
                break;
            case 2:
                ArmorLookup = BuildLookupName( "Reactive Armor", m.GetTechBase(), ArmorTechBase );
                break;
            case 3:
                ArmorLookup = BuildLookupName( "Laser-Reflective", m.GetTechBase(), ArmorTechBase );
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
        Armor[Constants.LOC_LA] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[Constants.LOC_LT] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[Constants.LOC_LL] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[Constants.LOC_RA] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[Constants.LOC_RT] = readUnsignedShort(FR);
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
        Armor[Constants.LOC_RL] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[Constants.LOC_HD] = readUnsignedShort(FR);
        FR.skipBytes(4); // ??
        Armor[Constants.LOC_CT] = readUnsignedShort(FR);
        FR.skipBytes(2); // ??
        Armor[Constants.LOC_LTR] = readUnsignedShort(FR);
        Armor[Constants.LOC_RTR] = readUnsignedShort(FR);
        Armor[Constants.LOC_CTR] = readUnsignedShort(FR);

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
                EnhanceLookup = BuildLookupName( "MASC", m.GetTechBase(), EnhanceTechBase );
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
            Criticals[Constants.LOC_LA][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[Constants.LOC_LT][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[Constants.LOC_LL][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[Constants.LOC_RA][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[Constants.LOC_RT][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[Constants.LOC_RL][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[Constants.LOC_HD][i] = readUnsignedInt(FR);
        }
        for( int i = 0; i < 12; i++ ) {
            Criticals[Constants.LOC_CT][i] = readUnsignedInt(FR);
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
                    if( m.GetTechBase() == AvailableCode.TECH_CLAN ) {
                        // this is not allowed under current rules.
                        GyroLookup = "Standard Gyro";
                        Errors.add( new ErrorReport( "An XL Gyro was specified for a Clan TechBase, which is not allowed under current rules.\nUse either Mixed or Inner Sphere Tech.  A Standard Gyro was used instead." ) );
                    } else {
                        GyroLookup = "Extra-Light Gyro";
                    }
                    break;
                case 2:
                    if( m.GetTechBase() == AvailableCode.TECH_CLAN ) {
                        // this is not allowed under current rules.
                        GyroLookup = "Standard Gyro";
                        Errors.add( new ErrorReport( "A Compact Gyro was specified for a Clan TechBase, which is not allowed under current rules.\nUse either Mixed or Inner Sphere Tech.  A Standard Gyro was used instead." ) );
                    } else {
                        GyroLookup = "Compact Gyro";
                    }
                    break;
                case 3:
                    if( m.GetTechBase() == AvailableCode.TECH_CLAN ) {
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
                    if( m.GetTechBase() == AvailableCode.TECH_CLAN ) {
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

        // before we go all crazy, figure out our arm actuator situation
        if( Criticals[Constants.LOC_LA][3] != 0x04 ) {
            m.GetActuators().RemoveLeftHand();
        }
        if( Criticals[Constants.LOC_LA][2] != 0x03 ) {
            m.GetActuators().RemoveLeftLowerArm();
        }
        if( Criticals[Constants.LOC_RA][3] != 0x04 ) {
            m.GetActuators().RemoveRightHand();
        }
        if( Criticals[Constants.LOC_RA][2] != 0x03 ) {
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
            if( Criticals[Constants.LOC_LT][i] == 0x0F ) {
                LTEngineStart = i;
                break;
            }
        }
        for( int i = 0; i < 12; i++ ) {
            if( Criticals[Constants.LOC_RT][i] == 0x0F ) {
                RTEngineStart = i;
                break;
            }
        }
        LocationIndex[] EngineLocs = { new LocationIndex( LTEngineStart, Constants.LOC_LT, 1 ), new LocationIndex( RTEngineStart, Constants.LOC_RT, 1 ) };

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
        m.GetArmor().SetArmor( Constants.LOC_HD, Armor[Constants.LOC_HD] );
        m.GetArmor().SetArmor( Constants.LOC_CT, Armor[Constants.LOC_CT] );
        m.GetArmor().SetArmor( Constants.LOC_LT, Armor[Constants.LOC_LT] );
        m.GetArmor().SetArmor( Constants.LOC_RT, Armor[Constants.LOC_RT] );
        m.GetArmor().SetArmor( Constants.LOC_LA, Armor[Constants.LOC_LA] );
        m.GetArmor().SetArmor( Constants.LOC_RA, Armor[Constants.LOC_RA] );
        m.GetArmor().SetArmor( Constants.LOC_LL, Armor[Constants.LOC_LL] );
        m.GetArmor().SetArmor( Constants.LOC_RL, Armor[Constants.LOC_RL] );
        m.GetArmor().SetArmor( Constants.LOC_CTR, Armor[Constants.LOC_CTR] );
        m.GetArmor().SetArmor( Constants.LOC_LTR, Armor[Constants.LOC_LTR] );
        m.GetArmor().SetArmor( Constants.LOC_RTR, Armor[Constants.LOC_RTR] );

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
            String test = m.GetArmor().GetLookupName();
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
                throw new Exception( "The number of heat sinks outside the engine does not match the number allocated.\nLoading aborted." );
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
                        case Constants.LOC_CT:
                            m.GetLoadout().SetCTCASE( true, j );
                            break;
                        case Constants.LOC_LT:
                            m.GetLoadout().SetLTCASE( true, j );
                            break;
                        case Constants.LOC_RT:
                            m.GetLoadout().SetRTCASE( true, j );
                            break;
                        default:
                            Errors.add( new ErrorReport( "Inner Sphere CASE was specified for the " + Constants.Locs[i] + "\nThis is not allowed and the item will not be added." ) );
                            break;
                    }
                }
                if( Criticals[i][j] == 0x26 ) {
                    // CASE II
                    switch( i ) {
                        case Constants.LOC_HD:
                            m.GetLoadout().SetHDCASEII( true, j, false );
                            break;
                        case Constants.LOC_CT:
                            m.GetLoadout().SetCTCASEII( true, j, false );
                            break;
                        case Constants.LOC_LT:
                            m.GetLoadout().SetLTCASEII( true, j, false );
                            break;
                        case Constants.LOC_RT:
                            m.GetLoadout().SetRTCASEII( true, j, false );
                            break;
                        case Constants.LOC_LA:
                            m.GetLoadout().SetLACASEII( true, j, false );
                            break;
                        case Constants.LOC_RA:
                            m.GetLoadout().SetRACASEII( true, j, false );
                            break;
                        case Constants.LOC_LL:
                            m.GetLoadout().SetLLCASEII( true, j, false );
                            break;
                        case Constants.LOC_RL:
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
                    m.GetLoadout().SetFCSArtemisIV( true );
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
                        abPlaceable neweq = df.GetEquipment().GetByName( Name, m );
                        if( neweq != null ) {
                            // is the item splittable?
                            if( neweq.CanSplit() ) {
                                // if it is splittable, figure out how many criticals are here

                                // next, figure out where the other criticals start and allocate
                            } else {
                                // easier.
                                int size = neweq.NumCrits();
                                m.GetLoadout().AddToQueue( neweq );
                                m.GetLoadout().AddTo( neweq, i, j );
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
        Sphere.put(new Long(0x36), "ISLaserAntiMissileSystem");
        Sphere.put(new Long(0xAF), "CLLaserAntiMissileSystem");
        Sphere.put(new Long(0x42), "ISAntiMissileSystem");
        Sphere.put(new Long(0xB4), "CLAntiMissileSystem");
        Sphere.put(new Long(0x5C), "ISAntiPersonnelPod");
        Sphere.put(new Long(0xCA), "CLAntiPersonnelPod");
        Sphere.put(new Long(0x64), "CLLightActiveProbe");
        Sphere.put(new Long(0x73), "ISBeagleActiveProbe");
        Sphere.put(new Long(0x74), "ISBloodhoundActiveProbe");
        Sphere.put(new Long(0xCB), "CLActiveProbe");
        Sphere.put(new Long(0x72), "ISAngelECMSuite");
        Sphere.put(new Long(0xB3), "CLAngelECMSuite");
        Sphere.put(new Long(0x78), "Guardian ECM Suite");
        Sphere.put(new Long(0xCC), "CLECMSuite");
        Sphere.put(new Long(0x7A), "ISTAG");
        Sphere.put(new Long(0xCE), "CLTAG");
        Sphere.put(new Long(0x65), "CLLightTAG");
        Sphere.put(new Long(0x75), "ISC3MasterComputer");
        Sphere.put(new Long(0x76), "ISC3SlaveUnit");
        Sphere.put(new Long(0x77), "ISImprovedC3CPU");
        Clan.put(new Long(0x86), "ISLaserAMS");
        Clan.put(new Long(0x3B), "CLLaserAMS");
        Clan.put(new Long(0x92), "ISAntiMissileSystem");
        Clan.put(new Long(0x40), "CLAntiMissileSystem");
        Clan.put(new Long(0xAC), "ISAntiPersonnelPod");
        Clan.put(new Long(0x56), "CLAntiPersonnelPod");
        Clan.put(new Long(0x57), "CLActiveProbe");
        Clan.put(new Long(0xAF), "CLLightActiveProbe");
        Clan.put(new Long(0xC3), "ISBeagleActiveProbe");
        Clan.put(new Long(0xC4), "ISBloodhoundActiveProbe");
        Clan.put(new Long(0xC2), "ISAngelECMSuite");
        Clan.put(new Long(0x3F), "CLAngelECMSuite");
        Clan.put(new Long(0xC8), "ISGuardianECM");
        Clan.put(new Long(0x58), "CLECMSuite");
        Clan.put(new Long(0xCA), "ISTAG");
        Clan.put(new Long(0x5A), "CLTAG");
        Clan.put(new Long(0xB4), "CLLightTAG");
        Clan.put(new Long(0xC5), "ISC3MasterComputer");
        Clan.put(new Long(0xC6), "ISC3SlaveUnit");
        Clan.put(new Long(0xC7), "ISImprovedC3CPU");

        // Energy Weapons
        Unused.put(new Long(0x128), "CLPlasmaRifle");
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
        Sphere.put(new Long(0x48), "ISLargeXPulseLaser");
        Sphere.put(new Long(0x49), "ISMediumXPulseLaser");
        Sphere.put(new Long(0x4A), "ISSmallXPulseLaser");
        Sphere.put(new Long(0x52), "ISHeavyFlamer");
        Sphere.put(new Long(0x53), "ISPPCCapacitor"); // HMP uses this code for ERPPC
        Sphere.put(new Long(0x58), "CLERMicroLaser");
        Sphere.put(new Long(0x59), "ISPPCCapacitor"); // HMP uses this code for standard PPC
        Sphere.put(new Long(0x5A), "(IS) ER Medium Laser");
        Sphere.put(new Long(0x5B), "(IS) ER Small Laser");
        Sphere.put(new Long(0x85), "ISVehicleFlamer");
        Sphere.put(new Long(0xA7), "(CL) ER Large Laser");
        Sphere.put(new Long(0xA8), "(CL) ER Medium Laser");
        Sphere.put(new Long(0xA9), "(CL) ER Small Laser");
        Sphere.put(new Long(0xAA), "CLERPPC");
        Sphere.put(new Long(0xAB), "CLFlamer");
        Sphere.put(new Long(0xB0), "CLLargePulseLaser");
        Sphere.put(new Long(0xB1), "CLMediumPulseLaser");
        Sphere.put(new Long(0xB2), "CLSmallPulseLaser");
        Sphere.put(new Long(0xF4), "CLHeavyLargeLaser");
        Sphere.put(new Long(0xF5), "CLHeavyMediumLaser");
        Sphere.put(new Long(0xF6), "CLHeavySmallLaser");
        Sphere.put(new Long(0xDA), "CLVehicleFlamer");
        Clan.put(new Long(0x33), "CLERLargeLaser");
        Clan.put(new Long(0x34), "CLERMediumLaser");
        Clan.put(new Long(0x35), "CLERSmallLaser");
        Clan.put(new Long(0x36), "(CL) ER PPC");
        Clan.put(new Long(0x37), "(CL) Flamer");
        Clan.put(new Long(0x38), "CLERLargePulseLaser");
        Clan.put(new Long(0x39), "CLERMediumPulseLaser");
        Clan.put(new Long(0x3A), "CLERSmallPulseLaser");
        Clan.put(new Long(0x3C), "CLLargePulseLaser");
        Clan.put(new Long(0x3D), "CLMediumPulseLaser");
        Clan.put(new Long(0x3E), "CLSmallPulseLaser");
        Clan.put(new Long(0x5B), "CLERMicroLaser");
        Clan.put(new Long(0x66), "CLVehicleFlamer");
        Clan.put(new Long(0x80), "CLHeavyLargeLaser");
        Clan.put(new Long(0x81), "CLHeavyMediumLaser");
        Clan.put(new Long(0x82), "CLHeavySmallLaser");
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
        Clan.put(new Long(0x98), "ISLargeXPulseLaser");
        Clan.put(new Long(0x99), "ISMediumXPulseLaser");
        Clan.put(new Long(0x9A), "ISSmallXPulseLaser");
        Clan.put(new Long(0xA3), "ISPPCCapacitor"); // HMP uses this code for ERPPC
        Clan.put(new Long(0xA8), "CLMicroPulseLaser");
        Clan.put(new Long(0xA9), "ISPPCCapacitor"); // HMP uses this code for PPC
        Clan.put(new Long(0xAA), "(IS) ER Medium Laser");
        Clan.put(new Long(0xAB), "(IS) ER Small Laser");
        Clan.put(new Long(0xD5), "ISVehicleFlamer");

        // Ballistics
        Common.put(new Long(0x121), "ISRotaryAC2");
        Common.put(new Long(0x122), "ISRotaryAC5");
        Common.put(new Long(0x124), "CLRotaryAC2");
        Common.put(new Long(0x125), "CLRotaryAC5");
        Sphere.put(new Long(0x3E), "(IS) Autocannon/2");
        Sphere.put(new Long(0x3F), "(IS) Autocannon/5");
        Sphere.put(new Long(0x40), "(IS) Autocannon/10");
        Sphere.put(new Long(0x41), "(IS) Autocannon/20");
        Sphere.put(new Long(0x43), "Long Tom Cannon");
        Sphere.put(new Long(0x44), "Sniper Cannon");
        Sphere.put(new Long(0x45), "Thumper Cannon");
        Sphere.put(new Long(0x46), "ISLightGaussRifle");
        Sphere.put(new Long(0x47), "ISGaussRifle");
        Sphere.put(new Long(0x4B), "ISLBXAC2");
        Sphere.put(new Long(0x4C), "ISLBXAC5");
        Sphere.put(new Long(0x4D), "ISLBXAC10");
        Sphere.put(new Long(0x4E), "ISLBXAC20");
        Sphere.put(new Long(0x4F), "ISMachine Gun");
        Sphere.put(new Long(0x50), "ISLAC2");
        Sphere.put(new Long(0x51), "ISLAC5");
        Sphere.put(new Long(0x54), "ISUltraAC2");
        Sphere.put(new Long(0x55), "ISUltraAC5");
        Sphere.put(new Long(0x56), "ISUltraAC10");
        Sphere.put(new Long(0x57), "ISUltraAC20");
        Sphere.put(new Long(0x5E), "CLLightMG");
        Sphere.put(new Long(0x5F), "CLHeavyMG");
        Sphere.put(new Long(0xB5), "CLGaussRifle");
        Sphere.put(new Long(0xB6), "CLLBXAC2");
        Sphere.put(new Long(0xB7), "CLLBXAC5");
        Sphere.put(new Long(0xB8), "CLLBXAC10");
        Sphere.put(new Long(0xB9), "CLLBXAC20");
        Sphere.put(new Long(0xBA), "CLMG");
        Sphere.put(new Long(0xBB), "CLUltraAC2");
        Sphere.put(new Long(0xBC), "CLUltraAC5");
        Sphere.put(new Long(0xBD), "CLUltraAC10");
        Sphere.put(new Long(0xBE), "CLUltraAC20");
        Sphere.put(new Long(0x123), "ISHeavyGaussRifle");
        Clan.put(new Long(0x41), "CLGaussRifle");
        Clan.put(new Long(0x42), "CLLBXAC2");
        Clan.put(new Long(0x43), "CLLBXAC5");
        Clan.put(new Long(0x44), "CLLBXAC10");
        Clan.put(new Long(0x45), "CLLBXAC20");
        Clan.put(new Long(0x46), "CLMG");
        Clan.put(new Long(0x47), "CLUltraAC2");
        Clan.put(new Long(0x48), "CLUltraAC5");
        Clan.put(new Long(0x49), "CLUltraAC10");
        Clan.put(new Long(0x4A), "CLUltraAC20");
        Clan.put(new Long(0x8E), "ISAC2");
        Clan.put(new Long(0x8F), "ISAC5");
        Clan.put(new Long(0x90), "ISAC10");
        Clan.put(new Long(0x91), "ISAC20");
        Clan.put(new Long(0x96), "ISLightGaussRifle");
        Clan.put(new Long(0x97), "ISGaussRifle");
        Clan.put(new Long(0x9B), "ISLBXAC2");
        Clan.put(new Long(0x9C), "ISLBXAC5");
        Clan.put(new Long(0x9D), "ISLBXAC10");
        Clan.put(new Long(0x9E), "ISLBXAC20");
        Clan.put(new Long(0x9F), "ISMachine Gun");
        Clan.put(new Long(0xA0), "ISLAC2");
        Clan.put(new Long(0xA1), "ISLAC5");
        Clan.put(new Long(0xA4), "ISUltraAC2");
        Clan.put(new Long(0xA5), "ISUltraAC5");
        Clan.put(new Long(0xA6), "ISUltraAC10");
        Clan.put(new Long(0xA7), "ISUltraAC20");
        Clan.put(new Long(0xAD), "CLLightMG");
        Clan.put(new Long(0xAE), "CLHeavyMG");
        Clan.put(new Long(0x93), "Long Tom Cannon");
        Clan.put(new Long(0x94), "Sniper Cannon");
        Clan.put(new Long(0x95), "Thumper Cannon");

        // missile weapons
        Common.put(new Long(0xFC), "CLATM3");
        Common.put(new Long(0xFD), "CLATM6");
        Common.put(new Long(0xFE), "CLATM9");
        Common.put(new Long(0xFF), "CLATM12");
        Common.put(new Long(0x129), "ISRocketLauncher10");
        Common.put(new Long(0x12A), "ISRocketLauncher15");
        Common.put(new Long(0x12B), "ISRocketLauncher20");
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
        Sphere.put(new Long(0x89), "ISMRM10");
        Sphere.put(new Long(0x8A), "ISMRM20");
        Sphere.put(new Long(0x8B), "ISMRM30");
        Sphere.put(new Long(0x8C), "ISMRM40");
        Sphere.put(new Long(0x6D), "Thunderbolt-5");
        Sphere.put(new Long(0x6E), "Thunderbolt-10");
        Sphere.put(new Long(0x6F), "Thunderbolt-15");
        Sphere.put(new Long(0x70), "Thunderbolt-20");
        Sphere.put(new Long(0x66), "ISImprovedNarc");
        Sphere.put(new Long(0x79), "ISNarcBeacon");
        Sphere.put(new Long(0x92), "ISLRTorpedo5");
        Sphere.put(new Long(0x93), "ISLRTorpedo10");
        Sphere.put(new Long(0x94), "ISLRTorpedo15");
        Sphere.put(new Long(0x95), "ISLRTorpedo20");
        Sphere.put(new Long(0x96), "ISSRT2");
        Sphere.put(new Long(0x97), "ISSRT4");
        Sphere.put(new Long(0x98), "ISSRT6");
        Sphere.put(new Long(0x7B), "ISLRM5 (OS)");
        Sphere.put(new Long(0x7C), "ISLRM10 (OS)");
        Sphere.put(new Long(0x7D), "ISLRM15 (OS)");
        Sphere.put(new Long(0x7E), "ISLRM20 (OS)");
        Sphere.put(new Long(0x7F), "ISSRM2 (OS)");
        Sphere.put(new Long(0x80), "ISSRM4 (OS)");
        Sphere.put(new Long(0x81), "ISSRM6 (OS)");
        Sphere.put(new Long(0x82), "ISStreakSRM2 (OS)");
        Sphere.put(new Long(0x83), "ISStreakSRM4 (OS)");
        Sphere.put(new Long(0x84), "ISStreakSRM6 (OS)");
        Sphere.put(new Long(0x8E), "ISMRM10 (OS)");
        Sphere.put(new Long(0x8F), "ISMRM20 (OS)");
        Sphere.put(new Long(0x90), "ISMRM30 (OS)");
        Sphere.put(new Long(0x91), "ISMRM40 (OS)");
        Sphere.put(new Long(0x99), "ISLRM5 (I-OS)");
        Sphere.put(new Long(0x9A), "ISLRM10 (I-OS)");
        Sphere.put(new Long(0x9B), "ISLRM15 (I-OS)");
        Sphere.put(new Long(0x9C), "ISLRM20 (I-OS)");
        Sphere.put(new Long(0x9D), "ISSRM2 (I-OS)");
        Sphere.put(new Long(0x9E), "ISSRM4 (I-OS)");
        Sphere.put(new Long(0x9f), "ISSRM6 (I-OS)");
        Sphere.put(new Long(0xA0), "ISStreakSRM2 (I-OS)");
        Sphere.put(new Long(0xA1), "ISStreakSRM4 (I-OS)");
        Sphere.put(new Long(0xA2), "ISStreakSRM6 (I-OS)");
        Sphere.put(new Long(0xA3), "ISMRM10 (I-OS)");
        Sphere.put(new Long(0xA4), "ISMRM20 (I-OS)");
        Sphere.put(new Long(0xA5), "ISMRM30 (I-OS)");
        Sphere.put(new Long(0xA6), "ISMRM40 (I-OS)");
        Sphere.put(new Long(0xBF), "CLLRM5");
        Sphere.put(new Long(0xC0), "CLLRM10");
        Sphere.put(new Long(0xC1), "CLLRM15");
        Sphere.put(new Long(0xC2), "CLLRM20");
        Sphere.put(new Long(0xC3), "CLSRM2");
        Sphere.put(new Long(0xC4), "CLSRM4");
        Sphere.put(new Long(0xC5), "CLSRM6");
        Sphere.put(new Long(0xC6), "CLStreakSRM2");
        Sphere.put(new Long(0xC7), "CLStreakSRM4");
        Sphere.put(new Long(0xC8), "CLStreakSRM6");
        Sphere.put(new Long(0xCD), "CLNarcBeacon");
        Sphere.put(new Long(0xD0), "CLLRM5 (OS)");
        Sphere.put(new Long(0xD1), "CLLRM10 (OS)");
        Sphere.put(new Long(0xD2), "CLLRM15 (OS)");
        Sphere.put(new Long(0xD3), "CLLRM20 (OS)");
        Sphere.put(new Long(0xD4), "CLSRM2 (OS)");
        Sphere.put(new Long(0xD5), "CLSRM2 (OS)");
        Sphere.put(new Long(0xD6), "CLSRM2 (OS)");
        Sphere.put(new Long(0xD7), "CLStreakSRM2 (OS)");
        Sphere.put(new Long(0xD8), "CLStreakSRM4 (OS)");
        Sphere.put(new Long(0xD9), "CLStreakSRM6 (OS)");
        Sphere.put(new Long(0xDE), "CLLRTorpedo5");
        Sphere.put(new Long(0xDF), "CLLRTorpedo10");
        Sphere.put(new Long(0xE0), "CLLRTorpedo15");
        Sphere.put(new Long(0xE1), "CLLRTorpedo20");
        Sphere.put(new Long(0xE2), "CLSRT2");
        Sphere.put(new Long(0xE3), "CLSRT4");
        Sphere.put(new Long(0xE4), "CLSRT6");
        Sphere.put(new Long(0xE5), "CLStreakLRM5");
        Sphere.put(new Long(0xE6), "CLStreakLRM10");
        Sphere.put(new Long(0xE7), "CLStreakLRM15");
        Sphere.put(new Long(0xE8), "CLStreakLRM20");
        Sphere.put(new Long(0xEA), "CLLRM5 (I-OS)");
        Sphere.put(new Long(0xEB), "CLLRM10 (I-OS)");
        Sphere.put(new Long(0xEC), "CLLRM15 (I-OS)");
        Sphere.put(new Long(0xED), "CLLRM20 (I-OS)");
        Sphere.put(new Long(0xEE), "CLSRM2 (I-OS)");
        Sphere.put(new Long(0xEF), "CLSRM4 (I-OS)");
        Sphere.put(new Long(0xF0), "CLSRM6 (I=OS)");
        Sphere.put(new Long(0xF1), "CLStreakSRM2 (I-OS)");
        Sphere.put(new Long(0xF2), "CLStreakSRM4 (I-OS)");
        Sphere.put(new Long(0xF3), "CLStreakSRM6 (I=OS)");
        Clan.put(new Long(0x4B), "CLLRM5");
        Clan.put(new Long(0x4C), "CLLRM10");
        Clan.put(new Long(0x4D), "CLLRM15");
        Clan.put(new Long(0x4E), "CLLRM20");
        Clan.put(new Long(0x4F), "CLSRM2");
        Clan.put(new Long(0x50), "CLSRM4");
        Clan.put(new Long(0x51), "CLSRM6");
        Clan.put(new Long(0x52), "CLStreakSRM2");
        Clan.put(new Long(0x53), "CLStreakSRM4");
        Clan.put(new Long(0x54), "CLStreakSRM6");
        Clan.put(new Long(0x59), "CLNarcBeacon");
        Clan.put(new Long(0x5C), "CLLRM5 (OS)");
        Clan.put(new Long(0x5D), "CLLRM10 (OS)");
        Clan.put(new Long(0x5E), "CLLRM15 (OS)");
        Clan.put(new Long(0x5F), "CLLRM20 (OS)");
        Clan.put(new Long(0x60), "CLSRM2 (OS)");
        Clan.put(new Long(0x61), "CLSRM4 (OS)");
        Clan.put(new Long(0x62), "CLSRM6 (OS)");
        Clan.put(new Long(0x63), "CLStreakSRM2 (OS)");
        Clan.put(new Long(0x64), "CLStreakSRM4 (OS)");
        Clan.put(new Long(0x65), "CLStreakSRM6 (OS)");
        Clan.put(new Long(0x6A), "CLLRTorpedo5");
        Clan.put(new Long(0x6B), "CLLRTorpedo10");
        Clan.put(new Long(0x6C), "CLLRTorpedo15");
        Clan.put(new Long(0x6D), "CLLRTorpedo20");
        Clan.put(new Long(0x6E), "CLSRT2");
        Clan.put(new Long(0x6F), "CLSRT4");
        Clan.put(new Long(0x70), "CLSRT6");
        Clan.put(new Long(0x71), "CLStreakLRM5");
        Clan.put(new Long(0x72), "CLStreakLRM10");
        Clan.put(new Long(0x73), "CLStreakLRM15");
        Clan.put(new Long(0x74), "CLStreakLRM20");
        Clan.put(new Long(0x76), "CLLRM5 (I-OS)");
        Clan.put(new Long(0x77), "CLLRM10 (I-OS)");
        Clan.put(new Long(0x78), "CLLRM15 (I-OS)");
        Clan.put(new Long(0x79), "CLLRM20 (I-OS)");
        Clan.put(new Long(0x7a), "CLSRM2 (I-OS)");
        Clan.put(new Long(0x7b), "CLSRM4 (I-OS)");
        Clan.put(new Long(0x7c), "CLSRM6 (I=OS)");
        Clan.put(new Long(0x7d), "CLStreakSRM2 (I-OS)");
        Clan.put(new Long(0x7e), "CLStreakSRM4 (I-OS)");
        Clan.put(new Long(0x7f), "CLStreakSRM6 (I=OS)");
        Clan.put(new Long(0xB0), "ISLRM5");
        Clan.put(new Long(0xB1), "ISLRM10");
        Clan.put(new Long(0xB2), "ISLRM15");
        Clan.put(new Long(0xB3), "ISLRM20");
        Clan.put(new Long(0xB6), "ISImprovedNarc");
        Clan.put(new Long(0xB7), "ISSRM2");
        Clan.put(new Long(0xB8), "ISSRM4");
        Clan.put(new Long(0xB9), "ISSRM6");
        Clan.put(new Long(0xBA), "ISStreakSRM2");
        Clan.put(new Long(0xBB), "ISStreakSRM4");
        Clan.put(new Long(0xBC), "ISStreakSRM6");
        Clan.put(new Long(0xBD), "ISThunderbolt5");
        Clan.put(new Long(0xBE), "ISThunderbolt10");
        Clan.put(new Long(0xBF), "ISThunderbolt15");
        Clan.put(new Long(0xC0), "ISThunderbolt20");
        Clan.put(new Long(0xC9), "ISNarcBeacon");
        Clan.put(new Long(0xCB), "ISLRM5 (OS)");
        Clan.put(new Long(0xCC), "ISLRM10 (OS)");
        Clan.put(new Long(0xCD), "ISLRM15 (OS)");
        Clan.put(new Long(0xCE), "ISLRM20 (OS)");
        Clan.put(new Long(0xCF), "ISSRM2 (OS)");
        Clan.put(new Long(0xD0), "ISSRM4 (OS)");
        Clan.put(new Long(0xD1), "ISSRM6 (OS)");
        Clan.put(new Long(0xD2), "ISStreakSRM2 (OS)");
        Clan.put(new Long(0xD3), "ISStreakSRM4 (OS)");
        Clan.put(new Long(0xD4), "ISStreakSRM6 (OS)");
        Clan.put(new Long(0xD9), "ISMRM10");
        Clan.put(new Long(0xDA), "ISMRM20");
        Clan.put(new Long(0xDB), "ISMRM30");
        Clan.put(new Long(0xDC), "ISMRM40");
        Clan.put(new Long(0xDE), "ISMRM10 (OS)");
        Clan.put(new Long(0xDF), "ISMRM20 (OS)");
        Clan.put(new Long(0xE0), "ISMRM30 (OS)");
        Clan.put(new Long(0xE1), "ISMRM40 (OS)");
        Clan.put(new Long(0xE2), "ISLRTorpedo5");
        Clan.put(new Long(0xE3), "ISLRTorpedo10");
        Clan.put(new Long(0xE4), "ISLRTorpedo15");
        Clan.put(new Long(0xE5), "ISLRTorpedo20");
        Clan.put(new Long(0xE6), "ISSRT2");
        Clan.put(new Long(0xE7), "ISSRT4");
        Clan.put(new Long(0xE8), "ISSRT6");
        Clan.put(new Long(0xE9), "ISLRM5 (I-OS)");
        Clan.put(new Long(0xEA), "ISLRM10 (I-OS)");
        Clan.put(new Long(0xEB), "ISLRM15 (I-OS)");
        Clan.put(new Long(0xEC), "ISLRM20 (I-OS)");
        Clan.put(new Long(0xED), "ISSRM2 (I-OS)");
        Clan.put(new Long(0xEE), "ISSRM4 (I-OS)");
        Clan.put(new Long(0xEf), "ISSRM6 (I-OS)");
        Clan.put(new Long(0xF0), "ISStreakSRM2 (I-OS)");
        Clan.put(new Long(0xF1), "ISStreakSRM4 (I-OS)");
        Clan.put(new Long(0xF2), "ISStreakSRM6 (I-OS)");
        Clan.put(new Long(0xF3), "ISMRM10 (I-OS)");
        Clan.put(new Long(0xF4), "ISMRM20 (I-OS)");
        Clan.put(new Long(0xF5), "ISMRM30 (I-OS)");
        Clan.put(new Long(0xF6), "ISMRM40 (I-OS)");

        // Artillery
        Clan.put(new Long(0xD7), "ISSniperArtillery");
        Clan.put(new Long(0xD8), "ISThumperArtillery");
        Clan.put(new Long(0x55), "CLArrowIVSystem");
        Clan.put(new Long(0x68), "CLSniperArtillery");
        Clan.put(new Long(0x69), "CLThumperArtillery");
        Sphere.put(new Long(0x87), "ISSniperArtillery");
        Sphere.put(new Long(0x88), "ISThumperArtillery");
        Sphere.put(new Long(0x71), "ISArrowIVSystem");
        Sphere.put(new Long(0xC9), "CLArrowIVSystem");
        Sphere.put(new Long(0xDC), "CLSniperArtillery");
        Sphere.put(new Long(0xDD), "CLThumperArtillery");

        // ammunition
        Common.put(new Long(0x28c), "CLATM3 Ammo");
        Common.put(new Long(0x28d), "CLATM6 Ammo");
        Common.put(new Long(0x28e), "CLATM9 Ammo");
        Common.put(new Long(0x28f), "CLATM12 Ammo");
        Common.put(new Long(0x2B1), "ISRotaryAC2 Ammo");
        Common.put(new Long(0x2B2), "ISRotaryAC5 Ammo");
        Common.put(new Long(0x2b4), "CLRotaryAC2 Ammo");
        Common.put(new Long(0x2b5), "CLRotaryAC5 Ammo");
        // special for ammo mutator
        // 28c-28f = atm
        Common.put(new Long(0x10000028cL), "CLATM3 ER Ammo");
        Common.put(new Long(0x20000028cL), "CLATM3 HE Ammo");
        Common.put(new Long(0x10000028dL), "CLATM6 ER Ammo");
        Common.put(new Long(0x20000028dL), "CLATM6 HE Ammo");
        Common.put(new Long(0x10000028eL), "CLATM9 ER Ammo");
        Common.put(new Long(0x20000028eL), "CLATM9 HE Ammo");
        Common.put(new Long(0x10000028fL), "CLATM12 ER Ammo");
        Common.put(new Long(0x20000028fL), "CLATM12 HE Ammo");
        Common.put(new Long(0x100000298L), "ISLBXAC2 Ammo (THB)");
        Common.put(new Long(0x100000299L), "ISLBXAC5 Ammo (THB)");
        Common.put(new Long(0x10000029AL), "ISLBXAC20 Ammo (THB)");
        Sphere.put(new Long(0x01CE), "(IS) @ AC/2");
        Sphere.put(new Long(0x01CF), "(IS) @ AC/5");
        Sphere.put(new Long(0x01D0), "(IS) @ AC/10");
        Sphere.put(new Long(0x01d1), "(IS) @ AC/20");
        Sphere.put(new Long(0x01d2), "ISAMS Ammo");
        Sphere.put(new Long(0x01d3), "Long Tom Cannon Ammo");
        Sphere.put(new Long(0x01d4), "Sniper Cannon Ammo");
        Sphere.put(new Long(0x01d5), "Thumper Cannon Ammo");
        Sphere.put(new Long(0x01d6), "ISLightGauss Ammo");
        Sphere.put(new Long(0x01d7), "ISGauss Ammo");
        Sphere.put(new Long(0x01db), "ISLBXAC2 Ammo");
        Sphere.put(new Long(0x01dc), "ISLBXAC5 Ammo");
        Sphere.put(new Long(0x01dd), "ISLBXAC10 Ammo");
        Sphere.put(new Long(0x01de), "ISLBXAC20 Ammo");
        Sphere.put(new Long(0x01df), "ISMG Ammo");
        Sphere.put(new Long(0x1e0), "ISLAC2 Ammo");
        Sphere.put(new Long(0x1e1), "ISLAC5 Ammo");
        Sphere.put(new Long(0x1e2), "ISHeavyFlamer Ammo");
        Sphere.put(new Long(0x01e4), "ISUltraAC2 Ammo");
        Sphere.put(new Long(0x01e5), "ISUltraAC5 Ammo");
        Sphere.put(new Long(0x01e6), "ISUltraAC10 Ammo");
        Sphere.put(new Long(0x01e7), "ISUltraAC20 Ammo");
        Sphere.put(new Long(0x01EE), "CLLightMG Ammo");
        Sphere.put(new Long(0x01EF), "CLHeavyMG Ammo");
        Sphere.put(new Long(0x01f0), "(IS) @ LRM-5");
        Sphere.put(new Long(0x01f1), "(IS) @ LRM-10");
        Sphere.put(new Long(0x01f2), "(IS) @ LRM-15");
        Sphere.put(new Long(0x01f3), "(IS) @ LRM-20");
        Sphere.put(new Long(0x01f6), "ISiNarc Pods");
        Sphere.put(new Long(0x01f7), "@ SRM-2");
        Sphere.put(new Long(0x01f8), "@ SRM-4");
        Sphere.put(new Long(0x01f9), "@ SRM-6");
        Sphere.put(new Long(0x01fa), "(IS) @ Streak SRM-2");
        Sphere.put(new Long(0x01fb), "(IS) @ Streak SRM-4");
        Sphere.put(new Long(0x01FC), "(IS) @ Streak SRM-6");
        Sphere.put(new Long(0x01FD), "Thunderbolt-5 Ammo");
        Sphere.put(new Long(0x01FE), "Thunderbolt-10 Ammo");
        Sphere.put(new Long(0x01FF), "Thunderbolt-15 Ammo");
        Sphere.put(new Long(0x0200), "Thunderbolt-20 Ammo");
        Sphere.put(new Long(0x0201), "ISArrowIV Ammo");
        Sphere.put(new Long(0x0209), "ISNarc Pods");
        Sphere.put(new Long(0x0215), "ISVehicleFlamer Ammo");
        Sphere.put(new Long(0x0217), "ISSniper Ammo");
        Sphere.put(new Long(0x0218), "ISThumper Ammo");
        Sphere.put(new Long(0x0219), "ISMRM10 Ammo");
        Sphere.put(new Long(0x021a), "ISMRM20 Ammo");
        Sphere.put(new Long(0x021b), "ISMRM30 Ammo");
        Sphere.put(new Long(0x021c), "ISMRM40 Ammo");
        Sphere.put(new Long(0x0222), "ISLRTorpedo5 Ammo");
        Sphere.put(new Long(0x0223), "ISLRTorpedo10 Ammo");
        Sphere.put(new Long(0x0224), "ISLRTorpedo15 Ammo");
        Sphere.put(new Long(0x0225), "ISLRTorpedo20 Ammo");
        Sphere.put(new Long(0x0226), "ISSRT2 Ammo");
        Sphere.put(new Long(0x0227), "ISSRT4 Ammo");
        Sphere.put(new Long(0x0228), "ISSRT6 Ammo");
        Sphere.put(new Long(0x0244), "CLAMS Ammo");
        Sphere.put(new Long(0x0245), "CLGauss Ammo");
        Sphere.put(new Long(0x0246), "CLLBXAC2 Ammo");
        Sphere.put(new Long(0x0247), "CLLBXAC5 Ammo");
        Sphere.put(new Long(0x0248), "CLLBXAC10 Ammo");
        Sphere.put(new Long(0x0249), "CLLBXAC20 Ammo");
        Sphere.put(new Long(0x024A), "CLMG Ammo");
        Sphere.put(new Long(0x024B), "CLUltraAC2 Ammo");
        Sphere.put(new Long(0x024C), "CLUltraAC5 Ammo");
        Sphere.put(new Long(0x024D), "CLUltraAC10 Ammo");
        Sphere.put(new Long(0x024E), "CLUltraAC20 Ammo");
        Sphere.put(new Long(0x024F), "CLLRM5 Ammo");
        Sphere.put(new Long(0x0250), "CLLRM10 Ammo");
        Sphere.put(new Long(0x0251), "CLLRM15 Ammo");
        Sphere.put(new Long(0x0252), "CLLRM20 Ammo");
        Sphere.put(new Long(0x0253), "CLSRM2 Ammo");
        Sphere.put(new Long(0x0254), "CLSRM4 Ammo");
        Sphere.put(new Long(0x0255), "CLSRM6 Ammo");
        Sphere.put(new Long(0x0256), "CLStreakSRM2 Ammo");
        Sphere.put(new Long(0x0257), "CLStreakSRM4 Ammo");
        Sphere.put(new Long(0x0258), "CLStreakSRM6 Ammo");
        Sphere.put(new Long(0x0259), "CLArrowIV Ammo");
        Sphere.put(new Long(0x025D), "CLNarc Pods");
        Sphere.put(new Long(0x026A), "CLVehicleFlamer Ammo");
        Sphere.put(new Long(0x026C), "CLSniper Ammo");
        Sphere.put(new Long(0x026D), "CLThumper Ammo");
        Sphere.put(new Long(0x026E), "CLLRTorpedo5 Ammo");
        Sphere.put(new Long(0x026F), "CLLRTorpedo10 Ammo");
        Sphere.put(new Long(0x0270), "CLLRTorpedo15 Ammo");
        Sphere.put(new Long(0x0271), "CLLRTorpedo20 Ammo");
        Sphere.put(new Long(0x0272), "CLSRT2 Ammo");
        Sphere.put(new Long(0x0273), "CLSRT4 Ammo");
        Sphere.put(new Long(0x0274), "CLSRT6 Ammo");
        Sphere.put(new Long(0x0275), "CLStreakLRM5 Ammo");
        Sphere.put(new Long(0x0276), "CLStreakLRM10 Ammo");
        Sphere.put(new Long(0x0277), "CLStreakLRM15 Ammo");
        Sphere.put(new Long(0x0278), "CLStreakLRM20 Ammo");
        Sphere.put(new Long(0x02b3), "ISHeavyGauss Ammo");
        // 1db-1de = is
        // 1d2-1d5 = cl
        // 298-299 = thb
        // 22B-22E = IS on clan
        // 246-249 = clan on IS
        Sphere.put(new Long(0x1000001dbL), "ISLBXAC2 CL Ammo");
        Sphere.put(new Long(0x1000001dcL), "ISLBXAC5 CL Ammo");
        Sphere.put(new Long(0x1000001ddL), "ISLBXAC10 CL Ammo");
        Sphere.put(new Long(0x1000001deL), "ISLBXAC20 CL Ammo");
        Sphere.put(new Long(0x100000246L), "CLLBXAC2 CL Ammo");
        Sphere.put(new Long(0x100000247L), "CLLBXAC5 CL Ammo");
        Sphere.put(new Long(0x100000248L), "CLLBXAC10 CL Ammo");
        Sphere.put(new Long(0x100000249L), "CLLBXAC20 CL Ammo");
        // Clan.put(new Long(0x01ce), "CLAC2 Ammo");
        Clan.put(new Long(0x01d0), "CLAMS Ammo");
        // Clan.put(new Long(0x01cf), "CLAC5 Ammo");
        Clan.put(new Long(0x01d1), "CLGauss Ammo");
        Clan.put(new Long(0x01d2), "CLLBXAC2 Ammo");
        Clan.put(new Long(0x01d3), "CLLBXAC5 Ammo");
        Clan.put(new Long(0x01d4), "CLLBXAC10 Ammo");
        Clan.put(new Long(0x01d5), "CLLBXAC20 Ammo");
        Clan.put(new Long(0x01d6), "CLMG Ammo");
        Clan.put(new Long(0x01d7), "CLUltraAC2 Ammo");
        Clan.put(new Long(0x01d8), "CLUltraAC5 Ammo");
        Clan.put(new Long(0x01d9), "CLUltraAC10 Ammo");
        Clan.put(new Long(0x01da), "CLUltraAC20 Ammo");
        Clan.put(new Long(0x01db), "CLLRM5 Ammo");
        Clan.put(new Long(0x01dc), "CLLRM10 Ammo");
        Clan.put(new Long(0x01dd), "CLLRM15 Ammo");
        Clan.put(new Long(0x01de), "CLLRM20 Ammo");
        Clan.put(new Long(0x01df), "CLSRM2 Ammo");
        Clan.put(new Long(0x01e0), "CLSRM4 Ammo");
        Clan.put(new Long(0x01e1), "CLSRM6 Ammo");
        Clan.put(new Long(0x01e2), "CLStreakSRM2 Ammo");
        Clan.put(new Long(0x01e3), "CLStreakSRM4 Ammo");
        Clan.put(new Long(0x01e4), "CLStreakSRM6 Ammo");
        Clan.put(new Long(0x01e5), "CLArrowIV Ammo");
        Clan.put(new Long(0x01e9), "CLNarc Pods");
        // Clan.put(new Long(0x0215), "CLFlamer Ammo");
        Clan.put(new Long(0x01f0), "CLLRM5 Ammo");
        Clan.put(new Long(0x01f1), "CLLRM10 Ammo");
        Clan.put(new Long(0x01f2), "CLLRM15 Ammo");
        Clan.put(new Long(0x01f3), "CLLRM20 Ammo");
        Clan.put(new Long(0x01f6), "CLVehicleFlamer Ammo");
        Clan.put(new Long(0x01f8), "CLSniper Ammo");
        Clan.put(new Long(0x01f9), "CLThumper Ammo");
        Clan.put(new Long(0x01fa), "CLLRTorpedo5 Ammo");
        Clan.put(new Long(0x01fb), "CLLRTorpedo10 Ammo");
        Clan.put(new Long(0x01fc), "CLLRTorpedo15 Ammo");
        Clan.put(new Long(0x01fd), "CLLRTorpedo20 Ammo");
        Clan.put(new Long(0x01fe), "CLSRT2 Ammo");
        Clan.put(new Long(0x01ff), "CLSRT4 Ammo");
        Clan.put(new Long(0x0200), "CLSRT6 Ammo");
        Clan.put(new Long(0x0201), "CLStreakLRM5 Ammo");
        Clan.put(new Long(0x0202), "CLStreakLRM10 Ammo");
        Clan.put(new Long(0x0203), "CLStreakLRM15 Ammo");
        Clan.put(new Long(0x0204), "CLStreakLRM20 Ammo");
        Clan.put(new Long(0x021E), "ISAC2 Ammo");
        Clan.put(new Long(0x021F), "ISAC5 Ammo");
        Clan.put(new Long(0x0220), "ISAC10 Ammo");
        Clan.put(new Long(0x0221), "ISAC20 Ammo");
        Clan.put(new Long(0x0222), "ISAMS Ammo");
        Clan.put(new Long(0x0223), "Long Tom Cannon Ammo");
        Clan.put(new Long(0x0224), "Sniper Cannon Ammo");
        Clan.put(new Long(0x0225), "Thumper Cannon Ammo");
        Clan.put(new Long(0x0226), "ISLightGauss Ammo");
        Clan.put(new Long(0x0227), "ISGauss Ammo");
        // Clan.put(new Long(0x0228), "CLSRTorpedo6 Ammo");
        Clan.put(new Long(0x022B), "ISLBXAC2 Ammo");
        Clan.put(new Long(0x022C), "ISLBXAC5 Ammo");
        Clan.put(new Long(0x022D), "ISLBXAC10 Ammo");
        Clan.put(new Long(0x022E), "ISLBXAC20 Ammo");
        Clan.put(new Long(0x022F), "ISMG Ammo");
        Clan.put(new Long(0x0230), "ISLAC2 Ammo");
        Clan.put(new Long(0x0231), "ISLAC5 Ammo");
        Clan.put(new Long(0x0234), "ISUltraAC2 Ammo");
        Clan.put(new Long(0x0235), "ISUltraAC5 Ammo");
        Clan.put(new Long(0x0236), "ISUltraAC10 Ammo");
        Clan.put(new Long(0x0237), "ISUltraAC20 Ammo");
        Clan.put(new Long(0x023d), "CLLightMG Ammo");
        Clan.put(new Long(0x023e), "CLHeavyMG Ammo");
        Clan.put(new Long(0x0240), "ISLRM5 Ammo");
        Clan.put(new Long(0x0241), "ISLRM10 Ammo");
        Clan.put(new Long(0x0242), "ISLRM15 Ammo");
        Clan.put(new Long(0x0243), "ISLRM20 Ammo");
        Clan.put(new Long(0x0246), "ISiNarc Pods");
        Clan.put(new Long(0x0247), "ISSRM2 Ammo");
        Clan.put(new Long(0x0248), "ISSRM4 Ammo");
        Clan.put(new Long(0x0249), "ISSRM6 Ammo");
        Clan.put(new Long(0x024A), "ISStreakSRM2 Ammo");
        Clan.put(new Long(0x024B), "ISStreakSRM4 Ammo");
        Clan.put(new Long(0x024C), "ISStreakSRM6 Ammo");
        Clan.put(new Long(0x024D), "ISThunderbolt5 Ammo");
        Clan.put(new Long(0x024E), "ISThunderbolt10 Ammo");
        Clan.put(new Long(0x024F), "ISThunderbolt15 Ammo");
        Clan.put(new Long(0x0250), "ISThunderbolt20 Ammo");
        Clan.put(new Long(0x0259), "ISNarc Pods");
        Clan.put(new Long(0x0265), "ISVehicleFlamer Ammo");
        Clan.put(new Long(0x0267), "ISSniperArtillery Ammo");
        Clan.put(new Long(0x0268), "ISThumperArtillery Ammo");
        Clan.put(new Long(0x0269), "ISMRM10 Ammo");
        Clan.put(new Long(0x026A), "ISMRM20 Ammo");
        Clan.put(new Long(0x026B), "ISMRM30 Ammo");
        Clan.put(new Long(0x026C), "ISMRM40 Ammo");
        Clan.put(new Long(0x0272), "ISLRTorpedo15 Ammo");
        Clan.put(new Long(0x0273), "ISLRTorpedo20 Ammo");
        Clan.put(new Long(0x0274), "ISLRTorpedo5 Ammo");
        Clan.put(new Long(0x0275), "ISLRTorpedo10 Ammo");
        Clan.put(new Long(0x0276), "ISSRT4 Ammo");
        Clan.put(new Long(0x0277), "ISSRT2 Ammo");
        Clan.put(new Long(0x0278), "ISSRT6 Ammo");
        Clan.put(new Long(0x10000022bL), "ISLBXAC2 CL Ammo");
        Clan.put(new Long(0x10000022cL), "ISLBXAC5 CL Ammo");
        Clan.put(new Long(0x10000022dL), "ISLBXAC10 CL Ammo");
        Clan.put(new Long(0x10000022eL), "ISLBXAC20 CL Ammo");
        Clan.put(new Long(0x1000001d2L), "CLLBXAC2 CL Ammo");
        Clan.put(new Long(0x1000001d3L), "CLLBXAC5 CL Ammo");
        Clan.put(new Long(0x1000001d4L), "CLLBXAC10 CL Ammo");
        Clan.put(new Long(0x1000001d5L), "CLLBXAC20 CL Ammo");

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
                return AvailableCode.RULES_TOURNAMENT;
            case 2:
                return AvailableCode.RULES_ADVANCED;
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

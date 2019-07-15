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

package components;

import java.util.Enumeration;
import common.Constants;
import battleforce.*;
import common.CommonTools;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import visitors.*;

public class Mech implements ifUnit, ifBattleforce {
    // A mech for the designer.  This is a large container class that will
    // handle calculations and settings for the design.

    // Declares
    private String Name = "",
                   Model = "",
                   Overview = "",
                   Capabilities = "",
                   History = "",
                   Deployment = "",
                   Variants = "",
                   Notables = "",
                   Additional = "",
                   Company = "",
                   Location = "",
                   EngineManufacturer = "",
                   ArmorModel = "",
                   ChassisModel = "",
                   JJModel = "",
                   CommSystem = "",
                   TandTSystem = "",
                   Solaris7ID = "0",
                   Solaris7ImageID = "0",
                   SSWImage = "";
    private int Tonnage = 20,
                WalkMP;
    private double JJMult,
                  MechMult;
    public final static double[] DefensiveFactor = { 1.0, 1.0, 1.1, 1.1, 1.2, 1.2,
        1.3, 1.3, 1.3, 1.4, 1.4, 1.4, 1.4, 1.4, 1.4, 1.4, 1.4, 1.5,
        1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.6, 1.6, 1.6, 1.6, 1.6, 1.6 };
    private boolean Quad,
                    Tripod,
                    Omnimech,
                    SuperHeavy,
                    Primitive = false,
                    IndustrialMech = false,
                    HasNullSig = false,
                    HasVoidSig = false,
                    HasChameleon = false,
                    HasBlueShield = false,
                    HasEnviroSealing = false,
                    HasEjectionSeat = false,
                    HasTracks = false,
                    HasLAAES = false,
                    HasRAAES = false,
                    HasLegAES = false,
                    HasFHES = false,
                    HasPartialWing = false,
                    HasJumpBooster = false,
                    FractionalAccounting = false,
                    Changed = false;
    private Engine CurEngine = new Engine( this );
    private ifMechLoadout MainLoadout = new BipedLoadout( Constants.BASELOADOUT_NAME, this ),
                    CurLoadout = MainLoadout;
    private ArrayList<ifMechLoadout> Loadouts = new ArrayList<ifMechLoadout>();
    private ArrayList<MechModifier> MechMods = new ArrayList<MechModifier>();
    private Gyro CurGyro = new Gyro( this );
    private InternalStructure CurIntStruc = new InternalStructure( this );
    private Cockpit CurCockpit = new Cockpit( this );
    private PhysicalEnhancement CurPhysEnhance = new PhysicalEnhancement( this );
    private MechArmor CurArmor = new MechArmor( this );
    private MechanicalJumpBooster JumpBooster = new MechanicalJumpBooster( this );
    private MultiSlotSystem NullSig,
                            VoidSig,
                            Chameleon,
                            BlueShield,
                            EnviroSealing,
                            Tracks;
    private SimplePlaceable EjectionSeat,
                            CommandConsole;
    private AESSystem LAAES = new AESSystem( this, false ),
                      RAAES = new AESSystem( this, false ),
                      FLLAES = new AESSystem( this, true ),
                      FRLAES = new AESSystem( this, true ),
                      LLAES = new AESSystem( this, true ),
                      RLAES = new AESSystem( this, true ),
                      CurLAAES,
                      CurRAAES;
    private PartialWing Wing = new PartialWing( this );
    private AvailableCode FHESAC = new AvailableCode( AvailableCode.TECH_BOTH );
    private Hashtable Lookup = new Hashtable();
    private AvailableCode OmniAvailable = new AvailableCode( AvailableCode.TECH_BOTH );
    private BattleForceData BFData;
    private Preferences Prefs;

    // Constructors
    public Mech() {
        // no prefs file, create a default.
        Prefs = Preferences.userRoot().node( Constants.SSWPrefs );
        Load();
    }

    public Mech( Preferences p ) {
        Prefs = p;
        Load();
    }

    private void Load() {
        BuildLookupTable();

        // Set the names and years to blank so the user doesn't have to overtype
        Name = "";
        MainLoadout.SetTechBase( AvailableCode.TECH_INNER_SPHERE );

        // Basic setup for the mech.  This is an arbitrary default chassis
        Quad = false;
        Omnimech = false;
        SuperHeavy = false;
        WalkMP = 1;
        CurEngine.SetRating( 20 );
        CurLoadout.SetBaseLoadout( MainLoadout );

        // Set the AES Systems to the default
        CurLAAES = LAAES;
        CurRAAES = RAAES;

        // finish off the OmniMech availability
        OmniAvailable.SetISCodes( 'E', 'X', 'X', 'E' );
        OmniAvailable.SetISDates( 0, 0, false, 3052, 0, 0, false, false );
        OmniAvailable.SetISFactions( "", "", "", "" );
        OmniAvailable.SetCLCodes( 'E', 'X', 'E', 'E' );
        OmniAvailable.SetCLDates( 0, 0, false, 2854, 0, 0, false, false );
        OmniAvailable.SetCLFactions( "", "", "", "" );
        OmniAvailable.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        // load up some special equipment
        AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        AC.SetISCodes( 'E', 'E', 'X', 'X' );
        AC.SetISDates( 0, 0, false, 2630, 2790, 0, true, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        NullSig = new MultiSlotSystem( this, "Null Signature System", "Null Signature System", "Null Signature System", "NullSignatureSystem", 0.0, false, true, 1400000.0, false, AC );
        NullSig.AddMechModifier( new MechModifier( 0, 0, 0, 0.0, 0, 0, 10, 0.2, 0.0, 0.0, 0.0, true, false ) );
        NullSig.SetExclusions( new Exclusion( new String[] { "Targeting Computer", "Void Signature System", "Stealth Armor", "C3" }, "Null Signature System" ) );
        NullSig.SetBookReference( "Tactical Operations" );
        NullSig.SetChatName( "NullSig" );

        AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        AC.SetISCodes( 'E', 'F', 'X', 'X' );
        AC.SetISDates( 0, 0, false, 2630, 2790, 0, true, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Chameleon = new MultiSlotSystem( this, "Chameleon LPS", "Chameleon LPS", "Chameleon LPS", "ChameleonLightPolarizationField", 0.0, true, true, 600000.0, false, AC );
        Chameleon.AddMechModifier( new MechModifier( 0, 0, 0, 0.0, 0, 0, 6, 0.2, 0.0, 0.0, 0.0, true, false ) );
        Chameleon.SetExclusions( new Exclusion( new String[] { "Void Signature System", "Stealth Armor" }, "Chameleon LPS" ) );
        Chameleon.SetBookReference( "Tactical Operations" );
        Chameleon.SetChatName( "CLPS" );

        AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        AC.SetISCodes( 'E', 'X', 'X', 'F' );
        AC.SetISDates( 3051, 3053, true, 3053, 0, 0, false, false );
        AC.SetISFactions( "FS", "FS", "", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        BlueShield = new MultiSlotSystem( this, "Blue Shield PFD", "Blue Shield PFD", "Blue Shield Project", "Blue Shield Particle Field Damper", 3.0, false, true, 1000000.0, false, AC );
        BlueShield.AddMechModifier( new MechModifier( 0, 0, 0, 0.0, 0, 0, 0, 0.0, 0.0, 0.2, 0.2, true, false ) );
        BlueShield.SetBookReference( "Tactical Operations" );
        BlueShield.SetChatName( "BluShld" );

        AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        AC.SetISCodes( 'E', 'X', 'X', 'E' );
        AC.SetISDates( 3060, 3070, true, 3070, 0, 0, false, false );
        AC.SetISFactions( "WB", "WB", "", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        VoidSig = new MultiSlotSystem( this, "Void Signature System", "Void Signature System", "Void Signature System", "VoidSignatureSystem", 0.0, false, true, 2000000.0, false, AC );
        VoidSig.AddMechModifier( new MechModifier( 0, 0, 0, 0.0, 0, 0, 10, 0.3, 0.0, 0.0, 0.0, true, false ) );
        VoidSig.SetExclusions( new Exclusion( new String[] { "Targeting Computer", "Null Signature System", "Stealth Armor", "C3", "Chameleon LPS" }, "Void Signature System" ) );
        VoidSig.SetBookReference( "Tactical Operations" );
        VoidSig.SetChatName( "VoidSig" );

        AC = new AvailableCode( AvailableCode.TECH_BOTH );
        AC.SetISCodes( 'C', 'C', 'C', 'C' );
        AC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetISFactions( "", "", "PS", "" );
        AC.SetCLCodes( 'C', 'X', 'C', 'C' );
        AC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetCLFactions( "", "", "PS", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        EnviroSealing = new MultiSlotSystem( this, "Environmental Sealing", "Environmental Sealing", "Environmental Sealing", "Environmental Sealing", 0.1, false, false, 225.0, true, AC );
        EnviroSealing.SetWeightBasedOnMechTonnage( true );
        EnviroSealing.SetBookReference( "Tech Manual" );
        EnviroSealing.SetChatName( "EnvSlng" );

        AC = new AvailableCode( AvailableCode.TECH_BOTH );
        AC.SetISCodes( 'B', 'D', 'E', 'F' );
        AC.SetISDates( 0, 0, false, 2445, 0, 0, false, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetCLCodes( 'B', 'X', 'D', 'F' );
        AC.SetCLDates( 0, 0, false, 2445, 0, 0, false, false );
        AC.SetCLFactions( "", "", "TH", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        EjectionSeat = new SimplePlaceable( "Ejection Seat", "Ejection Seat", "Ejection Seat", "EjectionSeat", "Tech Manual", 1, true, AC );
        EjectionSeat.SetTonnage( 0.5 );
        EjectionSeat.SetCost( 25000.0 );

        AC = new AvailableCode( AvailableCode.TECH_BOTH );
        AC.SetISCodes( 'C', 'D', 'E', 'E' );
        AC.SetISDates( 0, 0, false, 2400, 0, 0, false, false );
        AC.SetISFactions( "", "", "DC", "" );
        AC.SetCLCodes( 'C', 'X', 'D', 'E' );
        AC.SetCLDates( 0, 0, false, 2400, 0, 0, false, false );
        AC.SetCLFactions( "", "", "DC", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Tracks = new Tracks( this, AC );

        AC = new AvailableCode( AvailableCode.TECH_BOTH );
        AC.SetISCodes( 'D', 'C', 'F', 'E' );
        AC.SetISDates( 0, 0, false, 2631, 2850, 3030, true, true );
        AC.SetISFactions( "", "", "TH", "??" );
        AC.SetCLCodes( 'D', 'X', 'B', 'B' );
        AC.SetCLDates( 0, 0, false, 2631, 0, 0, false, false );
        AC.SetCLFactions( "", "", "TH", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        CommandConsole = new SimplePlaceable( "Command Console", "Command Console", "Command Console", "CommandConsole", "Tactical Operations", 1, true, AC );
        CommandConsole.SetTonnage( 3.0 );
        CommandConsole.SetCost( 500000.0 );
        CommandConsole.SetArmoredTonnage(1.0);

        FHESAC.SetISCodes( 'D', 'X', 'F', 'E' );
        FHESAC.SetISDates( 0, 0, false, 3023, 0, 0, false, false );
        FHESAC.SetISFactions( "", "", "LC", "" );
        FHESAC.SetCLCodes( 'D', 'X', 'X', 'E' );
        FHESAC.SetCLDates( 0, 0, false, 3052, 0, 0, false, false );
        FHESAC.SetCLFactions( "", "", "CWF", "" );
        FHESAC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
    }

    public void Recalculate() {
        // recalculates the Mech Mult and the Jump Jet Mult
        if( Tonnage < 60 ) {
            JJMult = 0.5;
        } else if( Tonnage > 55 && Tonnage < 90 ) {
            JJMult = 1.0;
        } else  {
            JJMult = 2.0;
        }
        if ( IndustrialMech == false ) {
            MechMult = 1.0 + ( Tonnage * 0.01 );
        } else {
            MechMult = 1.0 + ( Tonnage * 0.0025 );
        }
    }

    public int GetTonnage() {
        return Tonnage;
    }

    public void SetTonnage( int t ) {
        Tonnage = t;
        Recalculate();

        // also need to redo the engine if tonnage changes.
        int MaxWalk = 0;
        if( Primitive ) {
            MaxWalk = (int) Math.floor( 400 / Tonnage / 1.2 );
        } else {
            MaxWalk = (int) Math.floor( 400 / Tonnage );
        }
        if( WalkMP < 1 ) { WalkMP = 1; }
        if( WalkMP > MaxWalk ) { WalkMP = MaxWalk; }
        CurEngine.SetRating( WalkMP * Tonnage );

        SetChanged( true );
    }

    public void SetName( String n ) {
        Name = n;
        SetChanged( true );
    }

    public void SetModel( String m ) {
        Model = m;
        SetChanged( true );
    }

    public void SetEra( int e ) {
        if( Omnimech ) {
            CurLoadout.SetEra( e );
        } else {
            MainLoadout.SetEra( e );
        }
        SetChanged( true );
    }

    public void SetProductionEra( int e ) {
        if( Omnimech ) {
            CurLoadout.SetProductionEra( e );
        } else {
            MainLoadout.SetProductionEra( e );
        }
        SetChanged( true );
    }

    public void SetRulesLevel( int r ) {
        if( Omnimech ) {
            CurLoadout.SetRulesLevel( r );
        } else {
            MainLoadout.SetRulesLevel( r );
        }
        SetChanged( true );
    }

    public void SetYear( int y, boolean specified ) {
        if( Omnimech ) {
            CurLoadout.SetYear( y, specified );
        } else {
            MainLoadout.SetYear( y, specified );
        }
        SetChanged( true );
    }

    public void SetYearRestricted( boolean y ) {
        if( Omnimech ) {
            CurLoadout.SetYearRestricted( y );
        } else {
            MainLoadout.SetYearRestricted( y );
        }
        SetChanged( true );
    }

    public boolean IsYearRestricted() {
        return CurLoadout.IsYearRestricted();
    }

    public int GetTechBase() {
        return MainLoadout.GetTechBase();
    }

    public boolean SetTechBase( int t ) {
        if( Omnimech ) {
            if( t != MainLoadout.GetTechBase() && t != AvailableCode.TECH_BOTH ) {
                return false;
            } else {
                CurLoadout.SetTechBase( t );
            }
        } else {
            MainLoadout.SetTechBase( t );
        }
        SetChanged( true );
        return true;
    }

    public void SetSolaris7ID( String ID ) {
        Solaris7ID = ID;
        SetChanged( true );
    }

    public void SetSolaris7ImageID( String ID ) {
        Solaris7ImageID = ID;
        SetChanged( true );
    }

    public void SetSSWImage( String image ) {
        SSWImage = image;
        SetChanged( true );
    }

    public void SetInnerSphere() {
        // performs all the neccesary actions to switch this to Inner Sphere
        // set the tech base
        SetTechBase( AvailableCode.TECH_INNER_SPHERE );

        // clear out any MechModifiers in the chassis and loadout
        MechMods.clear();
        CurLoadout.GetMechMods().clear();

        // clear the loadout
        CurLoadout.ClearLoadout();

        // switch the engine over to a military Standard
        CurEngine.SetFUEngine();

        // switch the gyro
        CurGyro.SetStandard();

        // switch the internal structure
        if( IsQuad() ) {
            if( IndustrialMech ) {
                CurIntStruc.SetIMQD();
            } else {
                CurIntStruc.SetMSQD();
            }
        } else {
            if( IndustrialMech ) {
                CurIntStruc.SetIMBP();
            } else {
                CurIntStruc.SetMSBP();
            }
        }

        // switch the cockpit
        if( IndustrialMech ) {
            CurCockpit.SetIndustrialCockpit();
        } else {
            CurCockpit.SetStandardCockpit();
        }

        // switch the heat sinks
        GetHeatSinks().SetSingle();

        // switch the jump jets
        GetJumpJets().SetNormal();

        // switch the physical enhancement
        CurPhysEnhance.SetNone();

        // set the armor type
        CurArmor.SetStandard();

        // replace everything in the loadout
        CurGyro.Place( CurLoadout );
        CurEngine.Place( CurLoadout );
        CurIntStruc.Place( CurLoadout );
        CurCockpit.Place( CurLoadout );
        GetActuators().PlaceActuators();
        CurPhysEnhance.Place( CurLoadout );
        GetHeatSinks().ReCalculate();
        GetJumpJets().ReCalculate();
        CurArmor.Recalculate();
        UseTC( false, false );
        try {
            // replace fixed-slot equipment
            if(HasBlueShield){
                SetBlueShield(false);
                SetBlueShield(true);
            }
            if(HasChameleon){
                SetChameleon(false);
                SetChameleon(true);
            }
            if(HasEjectionSeat){
                SetEjectionSeat(false);
                SetEjectionSeat(true);
            }
            if(HasEnviroSealing){
                SetEnviroSealing(false);
                SetEnviroSealing(true);
            }
            if(HasNullSig){
                SetNullSig(false);
                SetNullSig(true);
            }
            if(HasTracks){
                SetTracks(false);
                SetTracks(true);
            }
            if(HasVoidSig){
                SetVoidSig(false);
                SetVoidSig(true);
            }
            if(HasPartialWing) {
                SetPartialWing( false );
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }

        SetChanged( true );
    }

    public void SetClan() {
        // performs all the neccesary actions to switch this to Clan
        // set the tech base
        SetTechBase( AvailableCode.TECH_CLAN );

        // clear out any MechModifiers in the chassis and loadout
        MechMods.clear();
        CurLoadout.GetMechMods().clear();

        // clear the loadout
        CurLoadout.ClearLoadout();

        // switch the engine over to a military Standard Clan
        CurEngine.SetFUEngine();

        // switch the gyro
        CurGyro.SetStandard();

        // switch the internal structure
        if( IsQuad() ) {
            if( IndustrialMech ) {
                CurIntStruc.SetIMQD();
            } else {
                CurIntStruc.SetMSQD();
            }
        } else {
            if( IndustrialMech ) {
                CurIntStruc.SetIMBP();
            } else {
                CurIntStruc.SetMSBP();
            }
        }

        // switch the cockpit
        if( IndustrialMech ) {
            CurCockpit.SetIndustrialCockpit();
        } else {
            CurCockpit.SetStandardCockpit();
        }

        // switch the heat sinks
        GetHeatSinks().SetSingle();

        // switch the jump jets
        GetJumpJets().SetNormal();

        // switch the physical enhancement
        CurPhysEnhance.SetNone();

        // set the armor type
        CurArmor.SetStandard();

        // replace everything iun the loadout
        CurGyro.Place( CurLoadout );
        CurEngine.Place( CurLoadout );
        CurIntStruc.Place( CurLoadout );
        CurCockpit.Place( CurLoadout );
        GetActuators().PlaceActuators();
        CurPhysEnhance.Place( CurLoadout );
        GetHeatSinks().ReCalculate();
        GetJumpJets().ReCalculate();
        CurArmor.Recalculate();
        UseTC( false, false );
        try {
            // replace fixed-slot equipment
            if(HasBlueShield){
                SetBlueShield(false);
            }
            if(HasChameleon){
                SetChameleon(false);
            }
            if(HasEjectionSeat){
                SetEjectionSeat(false);
                SetEjectionSeat(true);
            }
            if(HasEnviroSealing){
                SetEnviroSealing(false);
                SetEnviroSealing(true);
            }
            if(HasNullSig){
                SetNullSig(false);
            }
            if(HasTracks){
                SetTracks(false);
                SetTracks(true);
            }
            if(HasVoidSig){
                SetVoidSig(false);
            }
            if(HasPartialWing) {
                SetPartialWing( false );
                SetPartialWing( true );
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }

        SetChanged( true );
    }

    public void SetMixed() {
        SetTechBase( AvailableCode.TECH_BOTH );

        // clear out any MechModifiers in the chassis and loadout
        MechMods.clear();
        CurLoadout.GetMechMods().clear();

        // although nothing should technically be illegal, we'll do it anyway.
        CurLoadout.FlushIllegal();

        // check each component in turn before reseting to a default
        if( ! CommonTools.IsAllowed( CurGyro.GetAvailability(), this ) ) {
            CurGyro.SetStandard();
            CurGyro.Place( CurLoadout );
        }

        if( ! CommonTools.IsAllowed( CurEngine.GetAvailability(), this ) ) {
            CurEngine.SetFUEngine();
            CurEngine.Place( CurLoadout );
        }

        if( ! CommonTools.IsAllowed( CurIntStruc.GetAvailability(), this ) ) {
            // switch the internal structure
            if( IsQuad() ) {
                if( IndustrialMech ) {
                    CurIntStruc.SetIMQD();
                } else {
                    CurIntStruc.SetMSQD();
                }
            } else {
                if( IndustrialMech ) {
                    CurIntStruc.SetIMBP();
                } else {
                    CurIntStruc.SetMSBP();
                }
            }
            CurIntStruc.Place( CurLoadout );
        }

        if( ! CommonTools.IsAllowed( CurCockpit.GetAvailability(), this ) ) {
            if( IndustrialMech ) {
                CurCockpit.SetIndustrialCockpit();
            } else {
                CurCockpit.SetStandardCockpit();
            }
            CurCockpit.Place( CurLoadout );
        }

        if( ! CommonTools.IsAllowed( CurPhysEnhance.GetAvailability(), this ) ) {
            CurPhysEnhance.SetNone();
            CurPhysEnhance.Place( CurLoadout );
        }

        if( ! CommonTools.IsAllowed( GetHeatSinks().GetAvailability(), this ) ) {
            GetHeatSinks().SetSingle();
            GetHeatSinks().ReCalculate();
        }

        if( ! CommonTools.IsAllowed( GetJumpJets().GetAvailability(), this ) ) {
            GetJumpJets().SetNormal();
            GetJumpJets().ReCalculate();
        }

        if( ! CommonTools.IsAllowed( CurArmor.GetAvailability(), this ) ) {
            CurArmor.SetStandard();
            CurArmor.Recalculate();
        }

        // replace everything iun the loadout
        UseTC( false, false );
        try {
            // replace fixed-slot equipment
            if(HasBlueShield){
                SetBlueShield(false);
                SetBlueShield(true);
            }
            if(HasChameleon){
                SetChameleon(false);
                SetChameleon(true);
            }
            if(HasEjectionSeat){
                SetEjectionSeat(false);
                SetEjectionSeat(true);
            }
            if(HasEnviroSealing){
                SetEnviroSealing(false);
                SetEnviroSealing(true);
            }
            if(HasNullSig){
                SetNullSig(false);
                SetNullSig(true);
            }
            if(HasTracks){
                SetTracks(false);
                SetTracks(true);
            }
            if(HasVoidSig){
                SetVoidSig(false);
                SetVoidSig(true);
            }
            if(HasPartialWing) {
                SetPartialWing( false );
                SetPartialWing( true );
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }

        SetChanged( true );
    }

/*    public void SetMixed() {
        // performs all the neccesary actions to switch this to Mixed Tech
        // set the tech base
        SetTechBase( AvailableCode.TECH_BOTH );

        // clear out any MechModifiers in the chassis and loadout
        MechMods.clear();
        CurLoadout.GetMechMods().clear();

        // clear the loadout
        CurLoadout.ClearLoadout();

        // switch the engine over to a military Standard Clan
        CurEngine.SetFUEngine();

        // switch the gyro
        CurGyro.SetStandard();

        // switch the internal structure
        if( IsQuad() ) {
            if( IndustrialMech ) {
                CurIntStruc.SetIMQD();
            } else {
                CurIntStruc.SetMSQD();
            }
        } else {
            if( IndustrialMech ) {
                CurIntStruc.SetIMBP();
            } else {
                CurIntStruc.SetMSBP();
            }
        }

        // switch the cockpit
        if( IndustrialMech ) {
            CurCockpit.SetIndustrialCockpit();
        } else {
            CurCockpit.SetStandardCockpit();
        }

        // switch the heat sinks
        GetHeatSinks().SetSingle();

        // switch the jump jets
        GetJumpJets().SetNormal();

        // switch the physical enhancement
        CurPhysEnhance.SetNone();

        // set the armor type
        CurArmor.SetStandard();

        // replace everything iun the loadout
        CurGyro.Place( CurLoadout );
        CurEngine.Place( CurLoadout );
        CurIntStruc.Place( CurLoadout );
        CurCockpit.Place( CurLoadout );
        GetActuators().PlaceActuators();
        CurPhysEnhance.Place( CurLoadout );
        GetHeatSinks().ReCalculate();
        GetJumpJets().ReCalculate();
        CurArmor.Recalculate();
        UseTC( false, false );
        try {
            // replace fixed-slot equipment
            if(HasBlueShield){
                SetBlueShield(false);
                SetBlueShield(true);
            }
            if(HasChameleon){
                SetChameleon(false);
                SetChameleon(true);
            }
            if(HasEjectionSeat){
                SetEjectionSeat(false);
                SetEjectionSeat(true);
            }
            if(HasEnviroSealing){
                SetEnviroSealing(false);
                SetEnviroSealing(true);
            }
            if(HasNullSig){
                SetNullSig(false);
                SetNullSig(true);
            }
            if(HasTracks){
                SetTracks(false);
                SetTracks(true);
            }
            if(HasVoidSig){
                SetVoidSig(false);
                SetVoidSig(true);
            }
            if(HasPartialWing) {
                SetPartialWing( false );
                SetPartialWing( true );
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }

        SetChanged( true );
    }*/

    public int GetTechbase() {
        return CurLoadout.GetTechBase();
    }

    public int GetBaseTechbase() {
        return MainLoadout.GetTechBase();
    }

    public int GetUnitType() {
        if( IndustrialMech ) {
            return AvailableCode.UNIT_INDUSTRIALMECH;
        } else {
            return AvailableCode.UNIT_BATTLEMECH;
        }
    }

    public void SetBiped() {
        // this performs all the neccesary actions to change this mech into a biped
        // see if we have any CASE systems installed first.
        boolean ctcase = HasCTCase();
        boolean ltcase = HasLTCase();
        boolean rtcase = HasRTCase();
        boolean hdcase2 = CurLoadout.HasHDCASEII();
        boolean hdcase2clan = CurLoadout.GetHDCaseII().IsClan();
        boolean ctcase2 = CurLoadout.HasCTCASEII();
        boolean ctcase2clan = CurLoadout.GetCTCaseII().IsClan();
        boolean ltcase2 = CurLoadout.HasLTCASEII();
        boolean ltcase2clan = CurLoadout.GetLTCaseII().IsClan();
        boolean rtcase2 = CurLoadout.HasRTCASEII();
        boolean rtcase2clan = CurLoadout.GetRTCaseII().IsClan();
        boolean lacase2 = CurLoadout.HasLACASEII();
        boolean lacase2clan = CurLoadout.GetLACaseII().IsClan();
        boolean racase2 = CurLoadout.HasRACASEII();
        boolean racase2clan = CurLoadout.GetRACaseII().IsClan();
        boolean llcase2 = CurLoadout.HasLLCASEII();
        boolean llcase2clan = CurLoadout.GetLLCaseII().IsClan();
        boolean rlcase2 = CurLoadout.HasRLCASEII();
        boolean rlcase2clan = CurLoadout.GetRLCaseII().IsClan();
        boolean useClanCase = CurLoadout.IsUsingClanCASE();
        String Jumps = GetJumpJets().LookupName();
        String HeatSinks = GetHeatSinks().LookupName();

        // remember how many heat sinks and jump jets we had
        int NumJJ = GetJumpJets().GetNumJJ();
        int NumHS = GetHeatSinks().GetNumHS() - CurEngine.FreeHeatSinks();

        // Get a new Biped Loadout and load up the queue
        ifMechLoadout l = new BipedLoadout( Constants.BASELOADOUT_NAME, this );
        l.SetTechBase( MainLoadout.GetTechBase() );
        l.SetRulesLevel( MainLoadout.GetRulesLevel() );
        l.SetEra( MainLoadout.GetEra() );
        l.SetProductionEra( MainLoadout.GetProductionEra() );
        CurLoadout.Transfer( l );
        CurLoadout.ClearLoadout();

        // Now set the new main loadout and current loadout
        MainLoadout = l;
        CurLoadout = MainLoadout;
        CurLoadout.SetBaseLoadout( MainLoadout );

        // next, change the internal structure to a default standard biped
        if( IndustrialMech ) {
            CurIntStruc.SetIMBP();
        } else {
            CurIntStruc.SetMSBP();
        }

        // set the mech to a biped
        Quad = false;

        // remove the AES system entirely
        CurLAAES = LAAES;
        CurRAAES = RAAES;
        HasRAAES = false;
        HasLAAES = false;
        HasLegAES = false;

        // remove the any existing physical weapons, and industrial equipment
        ArrayList v = CurLoadout.GetNonCore();
        for( int i = v.size() - 1; i >= 0; i-- ) {
            abPlaceable p = (abPlaceable) v.get( i );
            if( p instanceof PhysicalWeapon ) {
                CurLoadout.Remove(p);
            } else if( p instanceof Equipment ) {
                if( ! ((Equipment) p).Validate( this ) ) {
                    CurLoadout.Remove( p );
                }
            }
        }

        // replace everything into the new loadout
        CurGyro.Place( CurLoadout );
        CurEngine.Place( CurLoadout );
        CurIntStruc.Place( CurLoadout );
        CurCockpit.Place( CurLoadout );
        GetActuators().PlaceActuators();
        CurPhysEnhance.Place( CurLoadout );
        // reset the correct number of heat sinks and jump jets
        try {
            ifVisitor ResetV = Lookup( Jumps );
            Visit( ResetV );
            for( int i = 0; i < NumJJ; i++ ) {
                GetJumpJets().IncrementNumJJ();
            }
            ResetV = Lookup( HeatSinks );
            Visit( ResetV );
            for( int i = 0; i < NumHS; i++ ) {
                GetHeatSinks().IncrementNumHS();
            }
        } catch( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }

        // it is safe to recalc here because we have the correct number
        GetHeatSinks().ReCalculate();
        GetJumpJets().ReCalculate();
        CurArmor.Recalculate();
        CurArmor.ResetPlaced();
        CurArmor.Place( CurLoadout );

        // attempt to replace any CASE systems that went missing
        try {
            if( ctcase ) {
                AddCTCase();
            }
            if( ltcase ) {
                AddLTCase();
            }
            if( rtcase ) {
                AddRTCase();
            }
            if( hdcase2 ) {
                CurLoadout.SetHDCASEII( true, -1, hdcase2clan );
            }
            if( ctcase2 ) {
                CurLoadout.SetCTCASEII( true, -1, ctcase2clan );
            }
            if( ltcase2 ) {
                CurLoadout.SetLTCASEII( true, -1, ltcase2clan );
            }
            if( rtcase2 ) {
                CurLoadout.SetRTCASEII( true, -1, rtcase2clan );
            }
            if( lacase2 ) {
                CurLoadout.SetLACASEII( true, -1, lacase2clan );
            }
            if( racase2 ) {
                CurLoadout.SetRACASEII( true, -1, racase2clan );
            }
            if( llcase2 ) {
                CurLoadout.SetLLCASEII( true, -1, llcase2clan );
            }
            if( rlcase2 ) {
                CurLoadout.SetRLCASEII( true, -1, rlcase2clan );
            }

             if (useClanCase){
                CurLoadout.SetClanCASE(false);
                CurLoadout.SetClanCASE(true);
            }

            // replace fixed-slot equipment
            if(HasBlueShield){
                SetBlueShield(false);
                SetBlueShield(true);
            }
            if(HasChameleon){
                SetChameleon(false);
                SetChameleon(true);
            }
            if(HasEjectionSeat){
                SetEjectionSeat(false);
                SetEjectionSeat(true);
            }
            if(HasEnviroSealing){
                SetEnviroSealing(false);
                SetEnviroSealing(true);
            }
            if(HasNullSig){
                SetNullSig(false);
                SetNullSig(true);
            }
            if(HasTracks){
                SetTracks(false);
                SetTracks(true);
            }
            if(HasVoidSig){
                SetVoidSig(false);
                SetVoidSig(true);
            }
            if(HasPartialWing) {
                SetPartialWing( false );
                SetPartialWing( true );
            }
        } catch( Exception e ) {
            // unhandled at this time, print an error out
            System.err.println( "System not reinstalled:\n" + e.getMessage() );
        }

        SetChanged( true );
    }

    public void SetQuad() {
        // this performs all the neccesary actions to change this mech into a quad
        // see if we have any CASE systems installed first.
        boolean ctcase = HasCTCase();
        boolean ltcase = HasLTCase();
        boolean rtcase = HasRTCase();
        boolean hdcase2 = CurLoadout.HasHDCASEII();
        boolean hdcase2clan = CurLoadout.GetHDCaseII().IsClan();
        boolean ctcase2 = CurLoadout.HasCTCASEII();
        boolean ctcase2clan = CurLoadout.GetCTCaseII().IsClan();
        boolean ltcase2 = CurLoadout.HasLTCASEII();
        boolean ltcase2clan = CurLoadout.GetLTCaseII().IsClan();
        boolean rtcase2 = CurLoadout.HasRTCASEII();
        boolean rtcase2clan = CurLoadout.GetRTCaseII().IsClan();
        boolean lacase2 = CurLoadout.HasLACASEII();
        boolean lacase2clan = CurLoadout.GetLACaseII().IsClan();
        boolean racase2 = CurLoadout.HasRACASEII();
        boolean racase2clan = CurLoadout.GetRACaseII().IsClan();
        boolean llcase2 = CurLoadout.HasLLCASEII();
        boolean llcase2clan = CurLoadout.GetLLCaseII().IsClan();
        boolean rlcase2 = CurLoadout.HasRLCASEII();
        boolean rlcase2clan = CurLoadout.GetRLCaseII().IsClan();
        boolean useClanCase = CurLoadout.IsUsingClanCASE();
        String Jumps = GetJumpJets().LookupName();
        String HeatSinks = GetHeatSinks().LookupName();

        // remember how many heat sinks and jump jets we had
        int NumJJ = GetJumpJets().GetNumJJ();
        int NumHS = GetHeatSinks().GetNumHS() - CurEngine.FreeHeatSinks();

        // Get a new Quad Loadout and load up the queue
        ifMechLoadout l = new QuadLoadout( Constants.BASELOADOUT_NAME, this );
        l.SetTechBase( MainLoadout.GetTechBase() );
        l.SetRulesLevel( MainLoadout.GetRulesLevel() );
        l.SetEra( MainLoadout.GetEra() );
        l.SetProductionEra( MainLoadout.GetProductionEra() );
        CurLoadout.Transfer( l );
        CurLoadout.ClearLoadout();

        // Now set the new main loadout and current loadout
        MainLoadout = l;
        CurLoadout = l;
        CurLoadout.SetBaseLoadout( MainLoadout );

        // next, change the internal structure to a default standard quad
        if( IndustrialMech ) {
            CurIntStruc.SetIMQD();
        } else {
            CurIntStruc.SetMSQD();
        }

        // set the mech to a quad
        Quad = true;

        // remove the AES system entirely
        CurLAAES = FLLAES;
        CurRAAES = FRLAES;
        HasRAAES = false;
        HasLAAES = false;
        HasLegAES = false;

        // remove the  any existing  physical weapons and industrial equipment
        ArrayList v = CurLoadout.GetNonCore();
        for( int i = v.size() - 1; i >= 0; i-- ) {
            abPlaceable p = (abPlaceable) v.get( i );
            if( p instanceof PhysicalWeapon ) {
                CurLoadout.Remove(p);
            } else if( p instanceof Equipment ) {
                if( ! ((Equipment) p).Validate( this ) ) {
                    CurLoadout.Remove( p );
                }
            }
        }

        // replace everything into the new loadout
        CurGyro.Place( CurLoadout );
        CurEngine.Place( CurLoadout );
        CurIntStruc.Place( CurLoadout );
        CurCockpit.Place( CurLoadout );
        GetActuators().PlaceActuators();
        CurPhysEnhance.Place( CurLoadout );
        // reset the correct number of heat sinks and jump jets
        try {
            ifVisitor ResetV = Lookup( Jumps );
            Visit( ResetV );
            for( int i = 0; i < NumJJ; i++ ) {
                GetJumpJets().IncrementNumJJ();
            }
            ResetV = Lookup( HeatSinks );
            Visit( ResetV );
            for( int i = 0; i < NumHS; i++ ) {
                GetHeatSinks().IncrementNumHS();
            }
        } catch( Exception e ) {
            // we shouldn't get an error from these visitors, but log it anyway
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }

        // it is safe to recalc here because we have the correct number
        GetHeatSinks().ReCalculate();
        GetJumpJets().ReCalculate();
        CurArmor.Recalculate();
        CurArmor.ResetPlaced();
        CurArmor.Place( CurLoadout );

        // attempt to replace any CASE systems that went missing
        try {
            if( ctcase ) {
                AddCTCase();
            }
            if( ltcase ) {
                AddLTCase();
            }
            if( rtcase ) {
                AddRTCase();
            }
            if( hdcase2 ) {
                CurLoadout.SetHDCASEII( true, -1, hdcase2clan );
            }
            if( ctcase2 ) {
                CurLoadout.SetCTCASEII( true, -1, ctcase2clan );
            }
            if( ltcase2 ) {
                CurLoadout.SetLTCASEII( true, -1, ltcase2clan );
            }
            if( rtcase2 ) {
                CurLoadout.SetRTCASEII( true, -1, rtcase2clan );
            }
            if( lacase2 ) {
                CurLoadout.SetLACASEII( true, -1, lacase2clan );
            }
            if( racase2 ) {
                CurLoadout.SetRACASEII( true, -1, racase2clan );
            }
            if( llcase2 ) {
                CurLoadout.SetLLCASEII( true, -1, llcase2clan );
            }
            if( rlcase2 ) {
                CurLoadout.SetRLCASEII( true, -1, rlcase2clan );
            }

            if (useClanCase){
                CurLoadout.SetClanCASE(false);
                CurLoadout.SetClanCASE(true);
            }
            // replace fixed-slot equipment
            if(HasBlueShield){
                SetBlueShield(false);
                SetBlueShield(true);
            }
            if(HasChameleon){
                SetChameleon(false);
                SetChameleon(true);
            }
            if(HasEjectionSeat){
                SetEjectionSeat(false);
                SetEjectionSeat(true);
            }
            if(HasEnviroSealing){
                SetEnviroSealing(false);
                SetEnviroSealing(true);
            }
            if(HasNullSig){
                SetNullSig(false);
                SetNullSig(true);
            }
            if(HasTracks){
                SetTracks(false);
                SetTracks(true);
            }
            if(HasVoidSig){
                SetVoidSig(false);
                SetVoidSig(true);
            }
            if(HasPartialWing) {
                SetPartialWing( false );
                SetPartialWing( true );
            }
        } catch( Exception e ) {
            // unhandled at this time, print an error out
            System.err.println( "System not reinstalled:\n" + e.getMessage() );
        }

        SetChanged( true );
    }

    public boolean IsQuad() {
        return Quad;
    }

    public boolean IsTripod() {
        return Tripod;
    }
    
    public void SetTripod() {
        // this performs all the neccesary actions to change this mech into a quad
        // see if we have any CASE systems installed first.
        boolean ctcase = HasCTCase();
        boolean ltcase = HasLTCase();
        boolean rtcase = HasRTCase();
        boolean hdcase2 = CurLoadout.HasHDCASEII();
        boolean hdcase2clan = CurLoadout.GetHDCaseII().IsClan();
        boolean ctcase2 = CurLoadout.HasCTCASEII();
        boolean ctcase2clan = CurLoadout.GetCTCaseII().IsClan();
        boolean ltcase2 = CurLoadout.HasLTCASEII();
        boolean ltcase2clan = CurLoadout.GetLTCaseII().IsClan();
        boolean rtcase2 = CurLoadout.HasRTCASEII();
        boolean rtcase2clan = CurLoadout.GetRTCaseII().IsClan();
        boolean lacase2 = CurLoadout.HasLACASEII();
        boolean lacase2clan = CurLoadout.GetLACaseII().IsClan();
        boolean racase2 = CurLoadout.HasRACASEII();
        boolean racase2clan = CurLoadout.GetRACaseII().IsClan();
        boolean llcase2 = CurLoadout.HasLLCASEII();
        boolean llcase2clan = CurLoadout.GetLLCaseII().IsClan();
        boolean rlcase2 = CurLoadout.HasRLCASEII();
        boolean rlcase2clan = CurLoadout.GetRLCaseII().IsClan();
        boolean useClanCase = CurLoadout.IsUsingClanCASE();
        String Jumps = GetJumpJets().LookupName();
        String HeatSinks = GetHeatSinks().LookupName();

        // remember how many heat sinks and jump jets we had
        int NumJJ = GetJumpJets().GetNumJJ();
        int NumHS = GetHeatSinks().GetNumHS() - CurEngine.FreeHeatSinks();

        // Get a new Quad Loadout and load up the queue
        ifMechLoadout l = new TripodLoadout( Constants.BASELOADOUT_NAME, this );
        l.SetTechBase( MainLoadout.GetTechBase() );
        l.SetRulesLevel( MainLoadout.GetRulesLevel() );
        l.SetEra( MainLoadout.GetEra() );
        l.SetProductionEra( MainLoadout.GetProductionEra() );
        CurLoadout.Transfer( l );
        CurLoadout.ClearLoadout();

        // Now set the new main loadout and current loadout
        MainLoadout = l;
        CurLoadout = l;
        CurLoadout.SetBaseLoadout( MainLoadout );

        // next, change the internal structure to a default standard quad
        if( IndustrialMech ) {
            CurIntStruc.SetIMQD();
        } else {
            CurIntStruc.SetMSQD();
        }

        // set the mech to a quad
        Tripod = true;

        // remove the AES system entirely
        CurLAAES = FLLAES;
        CurRAAES = FRLAES;
        HasRAAES = false;
        HasLAAES = false;
        HasLegAES = false;

        // remove the  any existing  physical weapons and industrial equipment
        ArrayList v = CurLoadout.GetNonCore();
        for( int i = v.size() - 1; i >= 0; i-- ) {
            abPlaceable p = (abPlaceable) v.get( i );
            if( p instanceof PhysicalWeapon ) {
                CurLoadout.Remove(p);
            } else if( p instanceof Equipment ) {
                if( ! ((Equipment) p).Validate( this ) ) {
                    CurLoadout.Remove( p );
                }
            }
        }

        // replace everything into the new loadout
        CurGyro.Place( CurLoadout );
        CurEngine.Place( CurLoadout );
        CurIntStruc.Place( CurLoadout );
        CurCockpit.Place( CurLoadout );
        GetActuators().PlaceActuators();
        CurPhysEnhance.Place( CurLoadout );
        // reset the correct number of heat sinks and jump jets
        try {
            ifVisitor ResetV = Lookup( Jumps );
            Visit( ResetV );
            for( int i = 0; i < NumJJ; i++ ) {
                GetJumpJets().IncrementNumJJ();
            }
            ResetV = Lookup( HeatSinks );
            Visit( ResetV );
            for( int i = 0; i < NumHS; i++ ) {
                GetHeatSinks().IncrementNumHS();
            }
        } catch( Exception e ) {
            // we shouldn't get an error from these visitors, but log it anyway
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }

        // it is safe to recalc here because we have the correct number
        GetHeatSinks().ReCalculate();
        GetJumpJets().ReCalculate();
        CurArmor.Recalculate();
        CurArmor.ResetPlaced();
        CurArmor.Place( CurLoadout );

        // attempt to replace any CASE systems that went missing
        try {
            if( ctcase ) {
                AddCTCase();
            }
            if( ltcase ) {
                AddLTCase();
            }
            if( rtcase ) {
                AddRTCase();
            }
            if( hdcase2 ) {
                CurLoadout.SetHDCASEII( true, -1, hdcase2clan );
            }
            if( ctcase2 ) {
                CurLoadout.SetCTCASEII( true, -1, ctcase2clan );
            }
            if( ltcase2 ) {
                CurLoadout.SetLTCASEII( true, -1, ltcase2clan );
            }
            if( rtcase2 ) {
                CurLoadout.SetRTCASEII( true, -1, rtcase2clan );
            }
            if( lacase2 ) {
                CurLoadout.SetLACASEII( true, -1, lacase2clan );
            }
            if( racase2 ) {
                CurLoadout.SetRACASEII( true, -1, racase2clan );
            }
            if( llcase2 ) {
                CurLoadout.SetLLCASEII( true, -1, llcase2clan );
            }
            if( rlcase2 ) {
                CurLoadout.SetRLCASEII( true, -1, rlcase2clan );
            }

            if (useClanCase){
                CurLoadout.SetClanCASE(false);
                CurLoadout.SetClanCASE(true);
            }
            // replace fixed-slot equipment
            if(HasBlueShield){
                SetBlueShield(false);
                SetBlueShield(true);
            }
            if(HasChameleon){
                SetChameleon(false);
                SetChameleon(true);
            }
            if(HasEjectionSeat){
                SetEjectionSeat(false);
                SetEjectionSeat(true);
            }
            if(HasEnviroSealing){
                SetEnviroSealing(false);
                SetEnviroSealing(true);
            }
            if(HasNullSig){
                SetNullSig(false);
                SetNullSig(true);
            }
            if(HasTracks){
                SetTracks(false);
                SetTracks(true);
            }
            if(HasVoidSig){
                SetVoidSig(false);
                SetVoidSig(true);
            }
            if(HasPartialWing) {
                SetPartialWing( false );
                SetPartialWing( true );
            }
        } catch( Exception e ) {
            // unhandled at this time, print an error out
            System.err.println( "System not reinstalled:\n" + e.getMessage() );
        }

        SetChanged( true );
    }
    
    /**
     * Determines if this mech is a Super Heavy 'Mech (over 100 tons)
     * @return True if this Mech is SuperHeavy 
     */
    public boolean IsSuperheavy() {
        return SuperHeavy;
    }
    
    /**
     * This performs all the necessary steps to transform this Mech into a 
     * SuperHeavy chassis
     */
    public void SetSuperHeavy() {
        
        SuperHeavy = true;
        
        // Update chassis
        CurIntStruc.SetSHBP();
        
        // switch the engine over to a military Standard
        CurEngine.SetFUEngine();
        
        // Update cockpit
        CurCockpit.SetSuperHeavy();
        
        // Update gyro
        CurGyro.SetSuperHeavy();
        
        // Update physical enhancements, Super Heavy Mechs may not use them
        CurPhysEnhance.SetNone();
        
        // Clear all Mech Modifications
        MechMods.clear();
    }
    
    /**
     * This performs all the necessary steps to transform this Mech into a 
     * standard chassis
     */
    public void UnsetSuperHeavy() {
        
        SuperHeavy = false;
        
        // Update chassis
        CurIntStruc.SetMSBP();
        
        // switch the engine over to a military Standard
        CurEngine.SetFUEngine();
        
        // Update cockpit
        CurCockpit.SetStandardCockpit();
        
        // Update gyro
        CurGyro.SetStandard();
        
        // Update physical enhancements, Super Heavy Mechs may not use them
        CurPhysEnhance.SetNone();
        
        // Clear all Mech Modifications
        MechMods.clear();
    }
    
    public void SetIndustrialmech() {
        // do all the neccesary things to change over to an IndustrialMech
        IndustrialMech = true;
        switch( MainLoadout.GetTechBase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                SetInnerSphere();
                break;
            case AvailableCode.TECH_CLAN:
                SetClan();
                break;
            case AvailableCode.TECH_BOTH:
                SetMixed();
                break;
        }

        if( Quad ) {
            SetQuad();
        } else {
            SetBiped();
        }
        Recalculate();

        SetChanged( true );
    }

    public void SetBattlemech() {
        // do all the neccesary things to change over to a BattleMech
        IndustrialMech = false;
        switch( MainLoadout.GetTechBase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                SetInnerSphere();
                break;
            case AvailableCode.TECH_CLAN:
                SetClan();
                break;
            case AvailableCode.TECH_BOTH:
                SetMixed();
                break;
        }

        if( Quad ) {
            SetQuad();
        } else {
            SetBiped();
        }
        Recalculate();

        SetChanged( true );
    }

    public void SetPrimitive() {
        Primitive = true;

        SetChanged( true );
    }

    public void SetModern() {
        Primitive = false;

        SetChanged( true );
    }

    public void SetOmnimech( String name ) {
        // this performs everything needed to turn the mech into an omni
        Omnimech = true;

        // remove any targeting computers from the base chassis.  they vary too
        // much to be fixed equipment
        UseTC( false, false );

        // before we unallocate the actuators, we need to make sure that
        // there are no physical weapons located there.
        boolean left = true;
        boolean right = true;
        ArrayList v = MainLoadout.GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof PhysicalWeapon ) {
                if( MainLoadout.Find( (abPlaceable) v.get( i ) ) == LocationIndex.MECH_LOC_LA ) {
                    left = false;
                }
                if( MainLoadout.Find( (abPlaceable) v.get( i ) ) == LocationIndex.MECH_LOC_RA ) {
                    right = false;
                }
            }
        }

        if( left ) {
            MainLoadout.GetActuators().RemoveLeftLowerArm();
        }
        if( right ) {
            MainLoadout.GetActuators().RemoveRightLowerArm();
        }

        // set the minimums on heat sinks and jump jets
        if( GetJumpJets().GetNumJJ() > 0 ) {
            GetJumpJets().SetBaseLoadoutNumJJ( GetJumpJets().GetNumJJ() );
        }
        if( GetHeatSinks().GetNumHS() > CurEngine.FreeHeatSinks() ) {
            GetHeatSinks().SetBaseLoadoutNumHS( GetHeatSinks().GetNumHS() );
        }

        // lock the main chassis
        MainLoadout.LockChassis();

        // now get a new loadout and set it to the current.
        ifMechLoadout l = MainLoadout.Clone();
        l.SetName( name );
        Loadouts.add( l );
        CurLoadout = l;

        SetChanged( true );
    }

    public void UnlockChassis() {
        // before we unlock, clear out all the loadouts except for Main
        Loadouts.clear();
        Omnimech = false;
        MainLoadout.UnlockChassis();
        CurLoadout = MainLoadout;
    }

    public void AddLoadout( String Name ) throws Exception {
        // Adds a new loadout with the given name to the ArrayList, cloned from the
        // base loadout

        // does the name match the Base Loadout's name?
        if( MainLoadout.GetName().equals( Name ) ) {
            throw new Exception( "\"" + Name + "\" is reserved for the base loadout and cannot be used\nfor a new loadout.  Please choose another name." );
        }

        // see if another loadout has the same name
        for( int i = 0; i < Loadouts.size(); i++ ) {
            if( ((ifMechLoadout) Loadouts.get( i )).GetName().equals( Name ) ) {
                throw new Exception( "Could not add the new loadout because\nthe name given matches an existing loadout." );
            }
        }

        ifMechLoadout l = MainLoadout.Clone();
        l.SetName( Name );
        Loadouts.add( l );
        CurLoadout = l;

        SetChanged( true );
    }

    public void RemoveLoadout( String Name ) {
        // removes the given loadout from the loadout ArrayList.  if the ArrayList is
        // empty (non-omnimech) nothing is done.
        for( int i = 0; i < Loadouts.size(); i++ ) {
            if( ((ifMechLoadout) Loadouts.get( i )).GetName().equals( Name ) ) {
                // remove it
                Loadouts.remove( i );
                break;
            }
        }

        // now set the current loadout to the first
        if( Loadouts.size() > 0 ) {
            CurLoadout = (ifMechLoadout) Loadouts.get(0);
        } else {
            CurLoadout = MainLoadout;
        }

        SetChanged( true );
    }

    public void SetCurLoadout( String Name ) {
        // sets the current loadout to the named loadout.
        if( Name.equals( Constants.BASELOADOUT_NAME ) ) {
            CurLoadout = MainLoadout;
            return;
        }

        for( int i = 0; i < Loadouts.size(); i++ ) {
            if( ((ifMechLoadout) Loadouts.get( i )).GetName().equals( Name ) ) {
                CurLoadout = (ifMechLoadout) Loadouts.get( i );
                return;
            }
        }

        // if we got here, there was a problem.  set the loadout to the base
        if( Loadouts.size() > 0 ) {
            CurLoadout = (ifMechLoadout) Loadouts.get(0);
        } else {
            CurLoadout = MainLoadout;
        }
    }

    public ArrayList GetLoadouts() {
        return Loadouts;
    }

    public ifMechLoadout GetBaseLoadout() {
        return MainLoadout;
    }

    public boolean IsOmnimech() {
        return Omnimech;
    }

    public boolean IsIndustrialmech() {
        return IndustrialMech;
    }

    public boolean IsPrimitive() {
        return Primitive;
    }

    public boolean UsingTC() {
        return CurLoadout.UsingTC();
    }

    public boolean UsingRoboticCockpit()
    {
        return GetCockpit().IsRobotic();
    }

    public String GetName() {
        return Name;
    }

    public String GetModel() {
        return Model;
    }

    public int GetEra() {
        return CurLoadout.GetEra();
    }

    public int GetBaseEra() {
        return MainLoadout.GetEra();
    }

    public int GetProductionEra() {
        return CurLoadout.GetProductionEra();
    }

    public int GetBaseProductionEra() {
        return MainLoadout.GetProductionEra();
    }

    public int GetRulesLevel() {
        if( Omnimech ) {
            return CurLoadout.GetRulesLevel();
        } else {
            return MainLoadout.GetRulesLevel();
        }
    }

    public int GetBaseRulesLevel() {
        return MainLoadout.GetRulesLevel();
    }

    public String GetFullName() {
        return String.format("%1$s %2$s %3$s", GetName(), GetModel(), GetLoadout().GetName()).replace(" " + Constants.BASELOADOUT_NAME, "").trim();
    }

    public String GetChatInfo() {
        String info = GetFullName() + " ";
        info += GetTonnage() + "t, ";
        // MP
        info += GetWalkingMP();
        if( GetWalkingMP() != GetAdjustedWalkingMP( false, true ) ) {
            info += "[" + GetAdjustedWalkingMP( false, true ) + "]";
        }
        info += "/";
        info += GetRunningMP();
        if( GetRunningMP() != GetAdjustedRunningMP( false, true ) ) {
            info += "[" + GetAdjustedRunningMP( false, true ) + "]";
        }
        info += "/" + CurLoadout.GetJumpJets().GetNumJJ();
        if( CurLoadout.GetJumpJets().GetNumJJ() != this.GetAdjustedJumpingMP( false ) ) {
            info += "[" + GetAdjustedJumpingMP( false ) + "]";
        }
        if( HasJumpBooster ) {
            info += "/" + JumpBooster.ChatName();
        }
        if( CurPhysEnhance.IsMASC() || CurPhysEnhance.IsTSM() ) {
            info += " " + CurPhysEnhance.ChatName();
        }
        info += ", ";

        // Engine
        info += GetEngine().ChatName() + ", ";

        // Internal Stucture
        info += GetIntStruc().ChatName();

        // Gyro
        if ( ! GetGyro().LookupName().equals( "Standard Gyro" ) ) {
            info += ", " + GetGyro().ChatName();
        }
        info +=  "; ";

        // MechArmor
        info += GetArmor().GetTonnage() + "T/" + Math.round(GetArmor().GetCoverage()) + "% " + GetArmor().ChatName() + "; ";

        // heat sinks
        info += GetHeatSinks().GetNumHS() + " " + GetHeatSinks().ChatName() + "; ";

        //Weapons and Equip
        Hashtable<String, Integer> list = new Hashtable<String, Integer>();
        abPlaceable item;
        for( int i = 0; i < CurLoadout.GetNonCore().size(); i++ ) {
            item = (abPlaceable) CurLoadout.GetNonCore().get( i );
            if( ! ( item instanceof Ammunition ) ) {
                if ( list.containsKey( item.ChatName() ) ) {
                    int curVal = (Integer) list.get( item.ChatName() ).intValue();
                    curVal++;
                    list.remove( item.ChatName() );
                    list.put( item.ChatName(), curVal );
                } else {
                    list.put( item.ChatName(), new Integer( 1 ) );
                }
            }
        }

        Enumeration e = list.keys();
        while( e.hasMoreElements() ) {
            String name = (String) e.nextElement();
            int count = (Integer) list.get(name).intValue();
            info += count + " " + name + ", ";
        }

        if( UsingTC() ) {
            info += GetTC().ChatName() + ", ";
        }
        if( HasNullSig() ) {
            info += NullSig.ChatName() + ", ";
        }
        if( HasChameleon() ) {
            info += Chameleon.ChatName() + ", ";
        }
        if( HasBlueShield() ) {
            info += BlueShield.ChatName() + ", ";
        }
        if( HasVoidSig() ) {
            info += VoidSig.ChatName() + ", ";
        }
        if( HasPartialWing ) {
            info += "PartWing, ";
        }

        return info.trim().substring( 0, info.length() - 2 );
    }

    public int GetDeprecatedLevel() {
        // returns the mech's "level" according to the older rules
        // this is used by Solaris7.com
        if( GetRulesLevel() >= AvailableCode.RULES_ADVANCED ) {
            return 3;
        } else {
            if( MainLoadout.GetTechBase() == AvailableCode.TECH_CLAN ) {
                return 2;
            }
            if( GetAvailability().GetISSWCode() < 'F' ) {
                if( GetHeatSinks().IsDouble() ) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                return 2;
            }
        }
    }

    public int GetMegaMekLevel() {
        // returns the mech's tech level according to MegaMek
        switch( GetRulesLevel() ) {
            case AvailableCode.RULES_TOURNAMENT:
                if( CurLoadout.GetTechBase() == AvailableCode.TECH_CLAN ) {
                    return 2;
                }
                if( GetAvailability().GetISSWCode() < 'F' ) {
                    if( GetHeatSinks().IsDouble() ) {
                        return 2;
                    } else {
                        return 1;
                    }
                } else {
                    return 2;
                }
            case AvailableCode.RULES_ADVANCED:
                return 3;
            case AvailableCode.RULES_EXPERIMENTAL:
                return 4;
            default:
                // only added for code completeness, we should never reach this
                return 5;
        }
    }

    public int GetYear() {
        return CurLoadout.GetYear();
    }

    public int GetBaseYear() {
        return MainLoadout.GetYear();
    }

    public boolean YearWasSpecified() {
        return CurLoadout.YearWasSpecified();
    }

    public String GetSolaris7ID() {
        return Solaris7ID;
    }

    public String GetSolaris7ImageID() {
        return Solaris7ImageID;
    }

    public String GetSSWImage() {
        return SSWImage;
    }

    public void SetEngineRating( int rate ) throws Exception {
        int oldrate = CurEngine.GetRating();
        if( CurEngine.CanSupportRating( rate, this ) ) {
            if( ( CurEngine.GetRating() < 405 && rate > 400 ) || ( CurEngine.GetRating() > 400 && rate < 405 ) ) {
                MainLoadout.Remove( CurEngine );
                CurEngine.SetRating( rate );
                if( ! CurEngine.Place( MainLoadout ) ) {
                    MainLoadout.Remove( CurEngine );
                    CurEngine.SetRating( oldrate );
                    WalkMP = (int) oldrate / Tonnage;
                    CurEngine.Place( MainLoadout );
                    throw new Exception( "The engine cannot support the new rating of " + rate + "\nbecause there is no room for the engine!" );
                }
                WalkMP = (int) rate / Tonnage;
            } else {
                CurEngine.SetRating( rate );
                WalkMP = (int) rate / Tonnage;
            }
        } else {
            SetWalkMP( 1 );
        }

        SetChanged( true );
    }

    public int GetWalkingMP() {
        return WalkMP;
    }

    public int GetMaxWalkMP() {
        if( CurEngine.IsPrimitive() ) {
            return (int) Math.floor( 400 / Tonnage / 1.2 );
        } else if( CurEngine.CanSupportRating( 500, this ) ) {
            return (int) Math.floor( 500 / Tonnage );
        } else {
            return (int) Math.floor( 400 / Tonnage );
        }
    }

    public int GetAdjustedWalkingMP( boolean BV, boolean MASCTSM ) {
        int retval = WalkMP;
        retval += GetTotalModifiers( BV, MASCTSM ).WalkingAdder();
        if( retval < 0 ) { return 0; }
        return retval;
    }

    public void SetWalkMP( int mp ) throws Exception {
        int MaxWalk = GetMaxWalkMP();
        int oldrate = CurEngine.GetRating();
        if( mp > MaxWalk ) { mp = MaxWalk; }
        if( mp < 1 ) { mp = 1; }
        int rate = mp * Tonnage;
        if( ( oldrate < 405 && rate > 400 ) || ( oldrate > 400 && rate < 405 ) ) {
            MainLoadout.Remove( CurEngine );
            CurEngine.SetRating( rate );
            if( ! CurEngine.Place( MainLoadout ) ) {
                MainLoadout.Remove( CurEngine );
                CurEngine.SetRating( oldrate );
                WalkMP = (int) oldrate / Tonnage;
                throw new Exception( "The engine cannot support the new rating of " + rate + "\nbecause there is no room for the engine!\nEngine reset to the previous rating." );
            } else {
                WalkMP = mp;
            }
        } else {
            CurEngine.SetRating( rate );
            WalkMP = mp;
        }

        SetChanged( true );
    }

    public int GetRunningMP() {
        return (int) Math.floor( GetWalkingMP() * 1.5 + 0.5 );
    }

    public int GetRunningMP( int MiniMult ) {
        return (int) Math.floor( ( GetWalkingMP() * MiniMult ) * 1.5 + 0.5 );
    }

    public int GetAdjustedRunningMP( boolean BV, boolean MASCTSM ) {
        // this had to become more complicated because of the peculiar
        // idiosyncracies of the BV system.  Stupid.
        MechModifier m = GetTotalModifiers( BV, MASCTSM );
        int WalkValue = GetAdjustedWalkingMP( BV, MASCTSM );
        double Multiplier = 1.5 + m.RunningMultiplier();
        int retval = (int) Math.floor( WalkValue * Multiplier + 0.5 ) + m.RunningAdder();
        if( retval < 0 ) { return 0; }
        return retval;
    }

    public int GetAdjustedRunningMP( boolean BV, boolean MASCTSM, int MiniMult ) {
        // this had to become more complicated because of the peculiar
        // idiosyncracies of the BV system.  Stupid.
        // this method provided for miniatures-scale printing
        MechModifier m = GetTotalModifiers( BV, MASCTSM );
        int WalkValue = GetAdjustedWalkingMP( BV, MASCTSM ) * MiniMult;
        double Multiplier = 1.5 + m.RunningMultiplier();
        int retval = (int) Math.floor( WalkValue * Multiplier + 0.5 ) + m.RunningAdder();
        if( retval < 0 ) { return 0; }
        return retval;
    }

    public int GetAdjustedJumpingMP( boolean BV ) {
        // Large Shields restrict jumping ability but do affect BV movement modifiers
        if ( ! BV && ! GetTotalModifiers( BV, true ).CanJump() ) {
            return 0;
        } else {
            int retval = CurLoadout.GetJumpJets().GetNumJJ();
            if( HasPartialWing && retval > 0 &! CurLoadout.GetJumpJets().IsUMU() ) { retval += Wing.GetJumpBonus(); }
            retval += GetTotalModifiers( BV, true ).JumpingAdder();
            if( retval < 0 ) { return 0; }
            return retval;
        }
    }

    public double GetJJMult() {
        return JJMult;
    }

    public int GetJumpBoosterMP() {
        return JumpBooster.GetMP();
    }

    public int GetAdjustedBoosterMP( boolean BV ) {
        // Large Shields restrict jumping ability but do affect BV movement modifiers
        if ( ! BV && ! GetTotalModifiers( BV, true ).CanJump() ) {
            return 0;
        } else {
            int retval = JumpBooster.GetMP();
            retval += GetTotalModifiers( BV, true ).JumpingAdder();
            if( retval < 0 ) { return 0; }
            return retval;
        }
    }

    public double GetCurrentTons() {
        // returns the current total tonnage of the mech
        double result = 0.0;
        result += CurIntStruc.GetTonnage();
        result += CurEngine.GetTonnage();
        result += CurGyro.GetTonnage();
        result += CurCockpit.GetTonnage();
        if( HasCommandConsole() ) {
            result += CommandConsole.GetTonnage();
        }
        result += GetHeatSinks().GetTonnage();
        result += CurPhysEnhance.GetTonnage();
        result += GetJumpJets().GetTonnage();
        result += CurArmor.GetTonnage();
        result += GetActuators().GetTonnage();
        if( HasCTCase() ) { result += CurLoadout.GetCTCase().GetTonnage(); }
        if( HasLTCase() ) { result += CurLoadout.GetCTCase().GetTonnage(); }
        if( HasRTCase() ) { result += CurLoadout.GetCTCase().GetTonnage(); }
        if( CurLoadout.HasHDCASEII() ) { result += CurLoadout.GetHDCaseII().GetTonnage(); }
        if( CurLoadout.HasCTCASEII() ) { result += CurLoadout.GetCTCaseII().GetTonnage(); }
        if( CurLoadout.HasLTCASEII() ) { result += CurLoadout.GetLTCaseII().GetTonnage(); }
        if( CurLoadout.HasRTCASEII() ) { result += CurLoadout.GetRTCaseII().GetTonnage(); }
        if( CurLoadout.HasLACASEII() ) { result += CurLoadout.GetLACaseII().GetTonnage(); }
        if( CurLoadout.HasRACASEII() ) { result += CurLoadout.GetRACaseII().GetTonnage(); }
        if( CurLoadout.HasLLCASEII() ) { result += CurLoadout.GetLLCaseII().GetTonnage(); }
        if( CurLoadout.HasRLCASEII() ) { result += CurLoadout.GetRLCaseII().GetTonnage(); }
        if( CurLoadout.HasHDTurret() ) { result += CurLoadout.GetHDTurret().GetTonnage(); }
        if( CurLoadout.HasLTTurret() ) { result += CurLoadout.GetLTTurret().GetTonnage(); }
        if( CurLoadout.HasRTTurret() ) { result += CurLoadout.GetRTTurret().GetTonnage(); }
        if( CurLoadout.UsingTC() ) { result += GetTC().GetTonnage(); }
        if( CurLoadout.UsingDumper() ) {result += GetDumper().GetTonnage(); }
        if( ! CurEngine.IsNuclear() ) { result += CurLoadout.GetPowerAmplifier().GetTonnage(); }
        if( HasBlueShield ) { result += BlueShield.GetTonnage(); }
        if( HasVoidSig ) { result += VoidSig.GetTonnage(); }
        if( HasNullSig ) { result += NullSig.GetTonnage(); }
        if( HasChameleon ) { result += Chameleon.GetTonnage(); }
        if( HasPartialWing ) { result += Wing.GetTonnage(); }
        if( HasJumpBooster ) { result += JumpBooster.GetTonnage(); }
        if( CurLoadout.HasSupercharger() ) { result += CurLoadout.GetSupercharger().GetTonnage(); }
        if( HasEnviroSealing ) { result += EnviroSealing.GetTonnage(); }
        if( HasEjectionSeat ) { result += EjectionSeat.GetTonnage(); }
        if( HasTracks) { result += Tracks.GetTonnage(); }
        if( Quad ) {
            if( HasLegAES ) { result += RLAES.GetTonnage() * 4.0; }
        } else {
            if( HasRAAES ) { result += CurRAAES.GetTonnage(); }
            if( HasLAAES ) { result += CurLAAES.GetTonnage(); }
            if( HasLegAES ) { result += RLAES.GetTonnage() * 2.0; }
        }
        if ( CurLoadout.HasBoobyTrap() ) { result += CurLoadout.GetBoobyTrap().GetTonnage(); }

        ArrayList v = CurLoadout.GetNonCore();
        if( v.size() > 0 ) {
            for( int i = 0; i < v.size(); i++ ) {
                result += ((abPlaceable) v.get(i)).GetTonnage();
            }
        }
        return result;
    }

    public double GetCurrentDryTons() {
        // returns the tonnage without ammunition
        // returns the current total tonnage of the mech
        double result = 0.0;
        result += CurIntStruc.GetTonnage();
        result += CurEngine.GetTonnage();
        result += CurGyro.GetTonnage();
        result += CurCockpit.GetTonnage();
        if( HasCommandConsole() ) {
            result += CommandConsole.GetTonnage();
        }
        result += GetHeatSinks().GetTonnage();
        result += CurPhysEnhance.GetTonnage();
        result += GetJumpJets().GetTonnage();
        result += CurArmor.GetTonnage();
        result += GetActuators().GetTonnage();
        if( HasCTCase() ) { result += CurLoadout.GetCTCase().GetTonnage(); }
        if( HasLTCase() ) { result += CurLoadout.GetCTCase().GetTonnage(); }
        if( HasRTCase() ) { result += CurLoadout.GetCTCase().GetTonnage(); }
        if( CurLoadout.HasHDCASEII() ) { result += CurLoadout.GetHDCaseII().GetTonnage(); }
        if( CurLoadout.HasCTCASEII() ) { result += CurLoadout.GetCTCaseII().GetTonnage(); }
        if( CurLoadout.HasLTCASEII() ) { result += CurLoadout.GetLTCaseII().GetTonnage(); }
        if( CurLoadout.HasRTCASEII() ) { result += CurLoadout.GetRTCaseII().GetTonnage(); }
        if( CurLoadout.HasLACASEII() ) { result += CurLoadout.GetLACaseII().GetTonnage(); }
        if( CurLoadout.HasRACASEII() ) { result += CurLoadout.GetRACaseII().GetTonnage(); }
        if( CurLoadout.HasLLCASEII() ) { result += CurLoadout.GetLLCaseII().GetTonnage(); }
        if( CurLoadout.HasRLCASEII() ) { result += CurLoadout.GetRLCaseII().GetTonnage(); }
        if( CurLoadout.HasHDTurret() ) { result += CurLoadout.GetHDTurret().GetTonnage(); }
        if( CurLoadout.HasLTTurret() ) { result += CurLoadout.GetLTTurret().GetTonnage(); }
        if( CurLoadout.HasRTTurret() ) { result += CurLoadout.GetRTTurret().GetTonnage(); }
        if( CurLoadout.UsingTC() ) { result += GetTC().GetTonnage(); }
        if( ! CurEngine.IsNuclear() ) { result += CurLoadout.GetPowerAmplifier().GetTonnage(); }
        if( HasBlueShield ) { result += BlueShield.GetTonnage(); }
        if( HasVoidSig ) { result += VoidSig.GetTonnage(); }
        if( HasNullSig ) { result += NullSig.GetTonnage(); }
        if( HasChameleon ) { result += Chameleon.GetTonnage(); }
        if( HasPartialWing ) { result += Wing.GetTonnage(); }
        if( HasJumpBooster ) { result += JumpBooster.GetTonnage(); }
        if( CurLoadout.HasSupercharger() ) { result += CurLoadout.GetSupercharger().GetTonnage(); }
        if( HasEnviroSealing ) { result += EnviroSealing.GetTonnage(); }
        if( HasEjectionSeat ) { result += EjectionSeat.GetTonnage(); }
        if( Quad ) {
            if( HasLegAES ) { result += RLAES.GetTonnage() * 4.0; }
        } else {
            if( HasRAAES ) { result += CurRAAES.GetTonnage(); }
            if( HasLAAES ) { result += CurLAAES.GetTonnage(); }
            if( HasLegAES ) { result += RLAES.GetTonnage() * 2.0; }
        }

        ArrayList v = CurLoadout.GetNonCore();
        if( v.size() > 0 ) {
            for( int i = 0; i < v.size(); i++ ) {
                if( ! ( v.get( i ) instanceof Ammunition ) ) {
                    result += ((abPlaceable) v.get(i)).GetTonnage();
                }
            }
        }
        return result;
    }

    public int GetMaxHeat() {
        // returns the maximum heat of the mech
        int result = 0;
        result += GetMovementHeat();
        result += GetWeaponHeat();
        if( ! Prefs.getBoolean( "HeatExcludeSystems", false ) ) {
            result += GetTotalModifiers( false, true ).HeatAdder();
        }
        return result;
    }

    public int GetJumpingHeat() {
        int walk = CurEngine.MaxMovementHeat();
        int jump = 0;
        int minjumpheat = 3 * CurEngine.JumpingHeatMultiplier();
        double heatperjj = 0.0;

        if( GetJumpJets().IsImproved() ) {
            heatperjj = 0.5 * CurEngine.JumpingHeatMultiplier();
        } else {
            heatperjj = 1.0 * CurEngine.JumpingHeatMultiplier();
        }

        if( GetJumpJets().GetNumJJ() > 0 ) {
            jump = (int) ( GetJumpJets().GetNumJJ() * heatperjj + 0.51f );
            if( jump < minjumpheat ) { jump = minjumpheat; }

            if ( GetJumpJets().IsUMU() ) { return 1 * CurEngine.JumpingHeatMultiplier(); }
        }

        if( Prefs.getBoolean( "HeatExcludeJumpMP", false ) ) {
            if( Prefs.getBoolean( "HeatExcludeAllMP", false ) ) {
                walk = CurEngine.MinimumHeat();
                jump = 0;
            } else {
                jump = 0;
            }
        }

        if( jump > walk ) {
            return jump;
        } else {
            return 0;
        }
    }
    public int GetMovementHeat() {
        int walk = CurEngine.MaxMovementHeat();
        int jump = 0;
        int minjumpheat = 3 * CurEngine.JumpingHeatMultiplier();
        double heatperjj = 0.0;

        if( GetJumpJets().IsImproved() ) {
            heatperjj = 0.5 * CurEngine.JumpingHeatMultiplier();
        } else {
            heatperjj = 1.0 * CurEngine.JumpingHeatMultiplier();
        }

        if( GetJumpJets().GetNumJJ() > 0 ) {
            jump = (int) ( GetJumpJets().GetNumJJ() * heatperjj + 0.51f );
            if( jump < minjumpheat ) { jump = minjumpheat; }

            if ( GetJumpJets().IsUMU() ) { jump = 1 * CurEngine.JumpingHeatMultiplier(); }
        }

        if( Prefs.getBoolean( "HeatExcludeJumpMP", false ) ) {
            if( Prefs.getBoolean( "HeatExcludeAllMP", false ) ) {
                walk = CurEngine.MinimumHeat();
                jump = 0;
            } else {
                jump = 0;
            }
        }

        if( jump > walk ) {
            return jump;
        } else {
            return walk;
        }
    }

    public int GetBVMovementHeat() {
        // provided for BV calculations
        int walk = CurEngine.MaxMovementHeat();
        int jump = 0;
        int minjumpheat = 3 * CurEngine.JumpingHeatMultiplier();
        double heatperjj = 0.0;

        if( GetJumpJets().IsImproved() ) {
            heatperjj = 0.5 * CurEngine.JumpingHeatMultiplier();
        } else {
            heatperjj = 1.0 * CurEngine.JumpingHeatMultiplier();
        }

        if( GetJumpJets().GetNumJJ() > 0 ) {
            jump = (int) ( GetJumpJets().GetNumJJ() * heatperjj + 0.51f );
            if( jump < minjumpheat ) { jump = minjumpheat; }

            if ( GetJumpJets().IsUMU() ) { jump = 1 * CurEngine.JumpingHeatMultiplier(); }
        }

        if( jump > walk ) {
            return jump;
        } else {
            return walk;
        }
    }

    public int GetWeaponHeat()
    {
        return GetWeaponHeat(Prefs.getBoolean("HeatExcludeOS", false), 
                             Prefs.getBoolean( "HeatExcludeRear", false),
                             Prefs.getBoolean( "HeatACFullRate", true),
                             Prefs.getBoolean( "HeatExcludeEquips", false));
    }

    /**
     *
     * @param ExcludeOS
     * @param ExcludeRear
     * @param FullRate
     * @param ExcludeEquip
     * @return int representing the total weapon heat limited by the exclusions provided
     */
    public int GetWeaponHeat(boolean ExcludeOS,
                             boolean ExcludeRear,
                             boolean FullRate,
                             boolean ExcludeEquip) {
        // returns the heat generated by weaponry and equipment that are not 
        // core components
        int result = 0;
        ArrayList v = CurLoadout.GetNonCore();
        if( v.size() <= 0 ) {
            return result;
        }

        abPlaceable a;
        for( int i = 0; i < v.size(); i++ ) {
            a = (abPlaceable) v.get( i );
            if( a instanceof ifWeapon ) {
                boolean OS = ((ifWeapon) a).IsOneShot();
                boolean Rear = a.IsMountedRear();
                int rate = 1;
                if( ( a instanceof RangedWeapon ) && FullRate ) {
                    if( ((RangedWeapon) a).IsUltra() ) {
                        rate = 2;
                    } else if( ((RangedWeapon) a).IsRotary() ) {
                        rate = 6;
                    }
                }
                if( ExcludeOS || ExcludeRear ) {
                    if( ExcludeOS ) {
                        if( ExcludeRear ) {
                            if( ! Rear &! OS ) {
                                result += ((ifWeapon) a).GetHeat() * rate;
                            }
                        } else {
                            if( ! OS ) {
                                result += ((ifWeapon) a).GetHeat() * rate;
                            }
                        }
                    } else {
                        if( ExcludeRear ) {
                            if( ! Rear ) {
                                result += ((ifWeapon) a).GetHeat() * rate;
                            }
                        } else {
                            result += ((ifWeapon) a).GetHeat() * rate;
                        }
                    }
                } else {
                    result += ((ifWeapon) a).GetHeat() * rate;
                }
            } else if( a instanceof Equipment ) {
                if( ! ExcludeEquip ) {
                    result += ((Equipment) a).GetHeat();
                }
            }
        }

        return result;
    }

    public double GetBVWeaponHeat() {
        // this returns the heat generated by weapons for BV purposes as the
        // normal method is governed by user preferences
        double result = 0;
        ArrayList v = CurLoadout.GetNonCore();
        if( v.size() <= 0 ) {
            return result;
        }

        abPlaceable a;
        for( int i = 0; i < v.size(); i++ ) {
            a = (abPlaceable) v.get( i );
            if( a instanceof ifWeapon ) {
                result += ((ifWeapon) a).GetBVHeat();
            }
            if( a instanceof MGArray ) {
                result += ((MGArray) a).GetBVHeat();
            }
        }

        return result;
    }

    public int GetCurrentBV() {
        // returns the final battle value of the mech
        return (int) Math.floor( CurCockpit.BVMod() * ( GetDefensiveBV() + GetOffensiveBV() ) + 0.5 );
    }

    public double GetDefensiveBV() {
        // modify the result by the defensive factor and send it out
        return GetUnmodifiedDefensiveBV() * GetDefensiveFactor();
    }

    public double GetUnmodifiedDefensiveBV() {
        // returns the defensive battle value of the mech
        double defresult = 0.0;

        // defensive battle value calculations start here
        defresult += CurIntStruc.GetDefensiveBV();
        defresult += CurGyro.GetDefensiveBV();
        defresult += CurArmor.GetDefensiveBV();
        defresult += GetDefensiveEquipBV();
        defresult += GetDefensiveExcessiveAmmoPenalty();
        defresult += GetExplosiveAmmoPenalty();
        defresult += GetExplosiveWeaponPenalty();
        if( defresult < 1.0 ) {
            defresult = 1.0;
        }

        return defresult;
    }

    public double GetDefensiveEquipBV() {
        // return the BV of all defensive equipment
        double result = 0.0;
        ArrayList v = CurLoadout.GetNonCore();

        for( int i = 0; i < v.size(); i++ ) {
            result += ((abPlaceable) v.get( i )).GetDefensiveBV();
        }
        if( UsingTC() ) {
            result += GetTC().GetDefensiveBV();
        }
        // now get the defensive BV for any armored components that weren't
        // already covered.
        if( CurLoadout.GetRulesLevel() >= AvailableCode.RULES_EXPERIMENTAL && CurLoadout.GetEra() >= AvailableCode.ERA_CLAN_INVASION ) {
            result += CurEngine.GetDefensiveBV();
            result += CurCockpit.GetDefensiveBV();
            if (HasCommandConsole()) result += CommandConsole.GetDefensiveBV();
            result += CurLoadout.GetActuators().GetDefensiveBV();
            if( HasNullSig() ) {
                result += NullSig.GetDefensiveBV();
            }
            if( HasChameleon() ) {
                result += Chameleon.GetDefensiveBV();
            }
            if( HasBlueShield() ) {
                result += BlueShield.GetDefensiveBV();
            }
            if( HasVoidSig() ) {
                result += VoidSig.GetDefensiveBV();
            }
            if( UsingPartialWing() ) {
                result += Wing.GetDefensiveBV();
            }
            if( UsingJumpBooster() ) {
                result += JumpBooster.GetDefensiveBV();
            }
            if( HasEnviroSealing() ) {
                result += EnviroSealing.GetDefensiveBV();
            }
            if( HasTracks() ) {
                result += Tracks.GetDefensiveBV();
            }
            if( CurLoadout.HasSupercharger() ) {
                result += CurLoadout.GetSupercharger().GetDefensiveBV();
            }
        }
        return result;
    }

    public double GetDefensiveExcessiveAmmoPenalty() {
        double result = 0.0;
        ArrayList v = CurLoadout.GetNonCore();
        ArrayList Ammo = new ArrayList(),
               Wep = new ArrayList();

        // do we even need to do this?
        if( v.size() <= 0 ) { return result; }

        // seperate out the ammo-using weapons and their ammunition
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof Ammunition ) {
                Ammo.add( v.get( i ) );
            }
            if( v.get(i) instanceof Equipment ) {
                if( ((Equipment) v.get( i )).HasAmmo() ) {
                    Wep.add( v.get( i ) );
                }
            }
        }

        // do we need to continue?
        if( Ammo.size() <= 0 ) { return result; }

        // for each weapon that uses ammo, total it's ammo BV and ensure it's
        // not excessive.  Add the BV to the running total.
        while( Wep.size() > 0 ) {
            // get the first item and check if anything else uses the same ammo
            Equipment test = (Equipment) Wep.get( 0 );
            Wep.remove( test );
            Ammunition ammo = null;
            int NumWeps = 1;
            int NumAmmos = 0;
            for( int i = Wep.size() - 1; i >= 0; i-- ) {
                if( ((Equipment) Wep.get( i )).GetAmmoIndex() == test.GetAmmoIndex() ) {
                    NumWeps++;
                    Wep.remove( i );
                }
            }

            // now check the number of ammunitions that this weapon uses
            for( int i = 0; i < Ammo.size(); i++ ) {
                if( ((Ammunition) Ammo.get(i)).GetAmmoIndex() == test.GetAmmoIndex() ) {
                    ammo = (Ammunition) Ammo.get( i );
                    NumAmmos++;
                }
            }

            // now find out if the ammo is excessive
            if( NumAmmos != 0 && ammo != null ) {
                double ammoBV = ( NumAmmos * ammo.GetDefensiveBV() );
                if( ammoBV <= 0.0 ) {
                    ammoBV = ( NumAmmos * ammo.GetDefensiveBV() );
                }
                double wepBV = ( NumWeps * ((abPlaceable) test).GetDefensiveBV() );
                if( ammoBV > wepBV ) {
                    result -= ammoBV - wepBV;
                }
            }
        }

        return result;
    }

    public double GetExplosiveAmmoPenalty() {
        double result = 0.0;
        ArrayList v = CurLoadout.GetNonCore();
        abPlaceable p;

        for( int i = 0; i < v.size(); i++ ) {
            p = (abPlaceable) v.get( i );
            if( p instanceof Ammunition ) {
                if( ((Ammunition) p).IsExplosive() ) {
                    if( CurEngine.IsISXL() ) {
                        switch( CurLoadout.Find( p ) ) {
                            case 0:
                                if( ! CurLoadout.HasHDCASEII() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 1:
                                if( ! CurLoadout.HasCTCASEII() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 2:
                                if( ! CurLoadout.HasLTCASEII() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 3:
                                if( ! CurLoadout.HasRTCASEII() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 4:
                                if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 5:
                                if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 6:
                                if( ! CurLoadout.HasLLCASEII() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 7:
                                if( ! CurLoadout.HasRLCASEII() ) {
                                    result -= 15.0;
                                }
                                break;
                        }
                    } else {
                        switch( CurLoadout.Find( p ) ) {
                            case 0:
                                if( ! CurLoadout.HasHDCASEII() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 1:
                                if( ! CurLoadout.HasCTCASEII() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 2:
                                if( ! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 3:
                                if( ! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 4:
                                if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 5:
                                if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 6:
                                if( ! CurLoadout.HasLLCASEII() ) {
                                    result -= 15.0;
                                }
                                break;
                            case 7:
                                if( ! CurLoadout.HasRLCASEII() ) {
                                    result -= 15.0;
                                }
                                break;
                        }
                    }
                }
            }
        }

        return result;
    }

    public double GetExplosiveWeaponPenalty() {
        double result = 0.0;
        ArrayList v = CurLoadout.GetNonCore();
        abPlaceable p;
        boolean Explode;

        for( int i = 0; i < v.size(); i++ ) {
            p = (abPlaceable) v.get( i );
            Explode = false;
            int mod = 0;
            if( p instanceof ifWeapon ) {
                Explode = ((ifWeapon) p).IsExplosive();
                if( p instanceof RangedWeapon ) {
                    if( ((RangedWeapon) p).IsUsingCapacitor() ) {
                        mod = 1;
                    }
                }
            }
            if( p instanceof Equipment ) { Explode = ((Equipment) p).IsExplosive(); }
            if( Explode ) {
                if( CurEngine.IsISXL() ) {
                    switch( CurLoadout.Find( p ) ) {
                        case 0:
                            if( ! CurLoadout.HasHDCASEII() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 1:
                            if( ! CurLoadout.HasCTCASEII() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 2:
                            if( ! CurLoadout.HasLTCASEII() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 3:
                            if( ! CurLoadout.HasRTCASEII() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 4:
                            if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 5:
                            if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 6:
                            if( ! CurLoadout.HasLLCASEII() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 7:
                            if( ! CurLoadout.HasRLCASEII() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                    }
                } else {
                    switch( CurLoadout.Find( p ) ) {
                        case 0:
                            if( ! CurLoadout.HasHDCASEII() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 1:
                            if( ! CurLoadout.HasCTCASEII() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 2:
                            if( ! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 3:
                            if( ! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 4:
                            if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 5:
                            if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 6:
                            if( ! CurLoadout.HasLLCASEII() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                        case 7:
                            if( ! CurLoadout.HasRLCASEII() ) {
                                result -= p.NumCrits() + mod;
                            }
                            break;
                    }
                }
            }
        }

        // check for Blue Shield system.
        if( HasBlueShield ) {
            if( CurEngine.IsISXL() ) {
                if( ! CurLoadout.HasLACASEII() &! CurLoadout.IsUsingClanCASE() ) { result -= 1; }
                if( ! CurLoadout.HasRACASEII() &! CurLoadout.IsUsingClanCASE() ) { result -= 1; }
                if( ! CurLoadout.HasLLCASEII() &! CurLoadout.IsUsingClanCASE() ) { result -= 1; }
                if( ! CurLoadout.HasRLCASEII() &! CurLoadout.IsUsingClanCASE() ) { result -= 1; }
                result -= 3.0;
            } else {
                if( ! CurLoadout.HasCTCASEII() ) { result -= 1; }
                if( ! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() &! CurLoadout.HasLACASEII() &! CurLoadout.IsUsingClanCASE() ) { result -= 1; }
                if( ! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() &! CurLoadout.IsUsingClanCASE() ) { result -= 1; }
                if( ! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() &! CurLoadout.HasRACASEII() &! CurLoadout.IsUsingClanCASE() ) { result -= 1; }
                if( ! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() &! CurLoadout.IsUsingClanCASE() ) { result -= 1; }
                if( ! CurLoadout.HasLLCASEII() &! CurLoadout.IsUsingClanCASE() ) { result -= 1; }
                if( ! CurLoadout.HasRLCASEII() &! CurLoadout.IsUsingClanCASE() ) { result -= 1; }
            }
        }
        return result;
    }

    public double GetDefensiveFactor() {
        // returns the defensive factor for this mech based on it's highest
        // target number for speed.

        // subtract one since we're indexing an array
        int RunMP = GetAdjustedRunningMP( true, true ) - 1;
        int JumpMP = 0;

        // this is a safeguard for using MASC on an incredibly speedy chassis
        // there is currently no way to get a bonus higher anyway.
        if( RunMP > 29 ) { RunMP = 29; }
        // safeguard for low walk mp (Modular MechArmor, for instance)
        if( RunMP < 0 ) { RunMP = 0; }

        // Get the defensive factors for jumping and running movement
        double ground = DefensiveFactor[RunMP];
        double jump = 0.0;
        if( GetJumpJets().GetNumJJ() > 0 ) {
            JumpMP = GetAdjustedJumpingMP( true ) - 1;
            jump = DefensiveFactor[JumpMP] + 0.1;
        }
        if( UsingJumpBooster() ) {
            int boostMP = GetAdjustedBoosterMP( true ) - 1;
            if( boostMP > JumpMP ) {
                JumpMP = boostMP;
                jump = Mech.DefensiveFactor[JumpMP] + 0.1f;
            }
        }

        double retval = 0.0;
        // return the best one.
        if( jump > ground ) {
            retval = jump;
        } else {
            retval = ground;
        }

        MechModifier m = GetTotalModifiers( true, true );
        retval += m.DefensiveBonus();
        if( retval < m.MinimumDefensiveBonus() ) {
            retval = m.MinimumDefensiveBonus();
        }

        return retval;
    }

    public double GetOffensiveBV() {
        // returns the offensive battle value of the mech
        return GetUnmodifiedOffensiveBV() * GetOffensiveFactor();
    }

    public double GetUnmodifiedOffensiveBV() {
        double offresult = 0.0;

        offresult += GetHeatAdjustedWeaponBV();
        offresult += GetNonHeatEquipBV();
        offresult += GetExcessiveAmmoPenalty();
        offresult += GetTonnageBV();
        return offresult;
    }

    public double GetHeatAdjustedWeaponBV() {
        ArrayList v = CurLoadout.GetNonCore(), wep = new ArrayList();
        double result = 0.0, foreBV = 0.0, rearBV = 0.0;
        boolean UseRear = false, TC = UsingTC(), UseAESMod = false, UseRobotic = CurCockpit.IsRobotic();
        abPlaceable a;

        // is it even worth performing all this?
        if( v.size() <= 0 ) {
            // nope
            return result;
        }

        // trim out the other equipment and get a list of offensive weapons only.
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof ifWeapon ) {
                wep.add( v.get( i ) );
            }
        }

        // just to save us a headache if there are no weapons
        if( wep.size() <= 0 ) { return result; }

        // now get the mech's heat efficiency and the total heat from weapons
        double heff = 6 + GetHeatSinks().TotalDissipation() - GetBVMovementHeat();
        //Subtract another 10 for the stealth armor
        if ( CurArmor.IsStealth() )
            heff -= 10;
        
        double wheat = GetBVWeaponHeat();

        if( GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
            // check for coolant pods
            int NumHS = GetHeatSinks().GetNumHS(), MaxHSBonus = NumHS * 2, NumPods = 0;
            for( int i = 0; i < v.size(); i++ ) {
                a = (abPlaceable) v.get( i );
                if( a instanceof Equipment ) {
                    if( ((Equipment) a).LookupName().equals( "Coolant Pod" ) ) {
                        NumPods++;
                    }
                }
            }
            // get the heat sink bonus
            int Bonus = (int) Math.ceil( (double) NumHS * ( (double) NumPods * 0.2 ) );
            if( Bonus > MaxHSBonus ) { Bonus = MaxHSBonus; }
            heff += Bonus;
        }

        // find out the total BV of rear and forward firing weapons
        for( int i = 0; i < wep.size(); i++ ) {
            a = ((abPlaceable) wep.get( i ));
            // arm mounted weapons always count their full BV, so ignore them.
            int loc = CurLoadout.Find( a );
            if( loc != LocationIndex.MECH_LOC_LA && loc != LocationIndex.MECH_LOC_RA ) {
                UseAESMod = UseAESModifier( a );
                if( a.IsMountedRear() ) {
                    rearBV += a.GetCurOffensiveBV( true, TC, UseAESMod, UseRobotic );
                } else {
                    foreBV += a.GetCurOffensiveBV( false, TC, UseAESMod, UseRobotic );
                }
            }
        }
        if( rearBV > foreBV ) { UseRear = true; }

        // see if we need to run heat calculations
        if( heff - wheat >= 0 ) {
            // no need for extensive calculations, return the weapon BV
            for( int i = 0; i < wep.size(); i++ ) {
                a = ((abPlaceable) wep.get( i ));
                int loc = CurLoadout.Find( a );
                UseAESMod = UseAESModifier( a );
                if( loc != LocationIndex.MECH_LOC_LA && loc != LocationIndex.MECH_LOC_RA ) {
                    result += a.GetCurOffensiveBV( UseRear, TC, UseAESMod,UseRobotic );
                } else {
                    result += a.GetCurOffensiveBV( false, TC, UseAESMod, UseRobotic );
                }
            }
            return result;
        }

        // Sort the weapon list
        abPlaceable[] sorted = SortWeapons( wep, UseRear );

        // calculate the BV of the weapons based on heat
        double curheat = 0;
        for( int i = 0; i < sorted.length; i++ ) {
            int loc = CurLoadout.Find( sorted[i] );
            UseAESMod = UseAESModifier( sorted[i] );
            if( curheat < heff ) {
                if( loc != LocationIndex.MECH_LOC_LA && loc != LocationIndex.MECH_LOC_RA ) {
                    result += sorted[i].GetCurOffensiveBV( UseRear, UsingTC(), UseAESMod, UseRobotic );
                } else {
                    result += sorted[i].GetCurOffensiveBV( false, UsingTC(), UseAESMod, UseRobotic );
                }
            } else {
                if( ((ifWeapon) sorted[i]).GetBVHeat() <= 0 ) {
                    if( loc != LocationIndex.MECH_LOC_LA && loc != LocationIndex.MECH_LOC_RA ) {
                        result += sorted[i].GetCurOffensiveBV( UseRear, UsingTC(), UseAESMod, UseRobotic );
                    } else {
                        result += sorted[i].GetCurOffensiveBV( false, UsingTC(), UseAESMod, UseRobotic );
                    }
                } else {
                    if( loc != LocationIndex.MECH_LOC_LA && loc != LocationIndex.MECH_LOC_RA ) {
                        result += sorted[i].GetCurOffensiveBV( UseRear, UsingTC(), UseAESMod, UseRobotic ) * 0.5;
                    } else {
                        result += sorted[i].GetCurOffensiveBV( false, UsingTC(), UseAESMod, UseRobotic ) * 0.5;
                    }
                }
            }
            curheat += ((ifWeapon) sorted[i]).GetBVHeat();
        }
        return result;
    }

    public double GetNonHeatEquipBV() {
        // return the BV of all offensive equipment
        double result = 0.0;
        ArrayList v = CurLoadout.GetNonCore();

        for( int i = 0; i < v.size(); i++ ) {
            if( ! ( v.get( i ) instanceof ifWeapon ) ) {
                if ( v.get(i) instanceof Equipment )
                    if ( ((Equipment)v.get(i)).LookupName().equals( "Radical Heat Sink" )) 
                        result += CommonTools.RoundFullUp(GetHeatSinks().GetNumHS() * 1.4);
                result += ((abPlaceable) v.get( i )).GetOffensiveBV();
            }
        }
        return result;
    }

    public double GetExcessiveAmmoPenalty() {
        double result = 0.0;
        ArrayList v = CurLoadout.GetNonCore();
        ArrayList Ammo = new ArrayList(),
               Wep = new ArrayList();

        // do we even need to do this?
        if( v.size() <= 0 ) { return result; }

        // seperate out the ammo-using weapons and their ammunition
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof Ammunition ) {
                Ammo.add( v.get( i ) );
            }
            if( v.get(i) instanceof ifWeapon ) {
                if( ((ifWeapon) v.get( i )).HasAmmo() ) {
                    Wep.add( v.get( i ) );
                }
            }
        }

        // do we need to continue?
        if( Ammo.size() <= 0 ) { return result; }

        // for each weapon that uses ammo, total it's ammo BV and ensure it's
        // not excessive.  Add the BV to the running total.
        while( Wep.size() > 0 ) {
            // get the first item and check if anything else uses the same ammo
            ifWeapon test = (ifWeapon) Wep.get( 0 );
            Wep.remove( test );
            Ammunition ammo = null;
            int NumWeps = 1;
            int NumAmmos = 0;
            for( int i = Wep.size() - 1; i >= 0; i-- ) {
                if( ((ifWeapon) Wep.get( i )).GetAmmoIndex() == test.GetAmmoIndex() ) {
                    NumWeps++;
                    Wep.remove( i );
                }
            }

            // now check the number of ammunitions that this weapon uses
            for( int i = 0; i < Ammo.size(); i++ ) {
                if( ((Ammunition) Ammo.get(i)).GetAmmoIndex() == test.GetAmmoIndex() ) {
                    ammo = (Ammunition) Ammo.get( i );
                    NumAmmos++;
                }
            }

            // now find out if the ammo is excessive
            if( NumAmmos != 0 && ammo != null ) {
                double ammoBV = ( NumAmmos * ammo.GetOffensiveBV() );
                if( ammoBV <= 0.0 ) {
                    ammoBV = ( NumAmmos * ammo.GetDefensiveBV() );
                }
                double wepBV = ( NumWeps * ((abPlaceable) test).GetOffensiveBV() );
                if( wepBV <= 0.0 ) {
                    wepBV = ( NumWeps * ((abPlaceable) test).GetDefensiveBV() );
                }
                if( ammoBV > wepBV ) {
                    result -= ammoBV - wepBV;
                }
            }
        }

        return result;
    }

    public double GetTonnageBV() {
        if( CurPhysEnhance.IsTSM() ) {
            return CurPhysEnhance.GetOffensiveBV();
        } else {
            if( Quad ) {
                if( HasLegAES ) {
                    return Tonnage * 1.4;
                } else {
                    return Tonnage;
                }
            } else {
                double AESMod = 1.0;
                if( HasLAAES ) { AESMod += 0.1; }
                if( HasRAAES ) { AESMod += 0.1; }
                if( HasLegAES ) { AESMod += 0.2; }
                return Tonnage * AESMod;
            }
        }
    }

    public double GetOffensiveFactor() {
        double result = 0.0;
        if( UsingJumpBooster() ) {
            int boost = GetAdjustedBoosterMP( true );
            int jump = GetAdjustedJumpingMP( true );
            if( jump >= boost ) {
                result += (double) (GetAdjustedRunningMP(true, true) + (Math.floor(GetAdjustedJumpingMP(true) * 0.5f + 0.5f)) - 5.0f);
            } else {
                result += (double) (GetAdjustedRunningMP(true, true) + (Math.floor(GetAdjustedBoosterMP(true) * 0.5f + 0.5f)) - 5.0f);
            }
        } else {
            result += (double) (GetAdjustedRunningMP(true, true) + (Math.floor(GetAdjustedJumpingMP(true) * 0.5f + 0.5f)) - 5.0f);
        }
        result = result * 0.1 + 1.0;
        result = (double) Math.pow( result, 1.2 ) ;

        // round off to the nearest two digits
        result = (double) Math.floor( result * 100 + 0.5 ) / 100;

        double cockpitMultiplier = 1.0;
        if( ! CurCockpit.HasFireControl() ) {
            cockpitMultiplier -= 0.1;
        }
        if( CurCockpit.LookupName().contains( "Primitive Industrial" ) ) {
            cockpitMultiplier -= 0.05;
        }

        return result * cockpitMultiplier;
    }

    public ifMechLoadout GetLoadout() {
        return CurLoadout;
    }

    public Engine GetEngine() {
        return CurEngine;
    }

    public InternalStructure GetIntStruc() {
        return CurIntStruc;
    }

    public Gyro GetGyro() {
        return CurGyro;
    }
    
    public Cockpit GetCockpit() {
        return CurCockpit;
    }
    
    public ActuatorSet GetActuators() {
        return CurLoadout.GetActuators();
    }
    
    public PhysicalEnhancement GetPhysEnhance() {
        return CurPhysEnhance;
    }

    public HeatSinkFactory GetHeatSinks() {
        return CurLoadout.GetHeatSinks();
    }

    public JumpJetFactory GetJumpJets() {
        return CurLoadout.GetJumpJets();
    }

    public MechArmor GetArmor() {
        return CurArmor;
    }

    public TargetingComputer GetTC() {
        return CurLoadout.GetTC();
    }

    public double GetChassisCost() {
        // this method sets the cost variable by calculating the base cost.
        // this is usually only done whenever a chassis component changes.
        double result = GetBaseChassisCost();
        result += CurEngine.GetCost();

        if( HasNullSig() ) { result += NullSig.GetCost(); }
        if( HasVoidSig() ) { result += VoidSig.GetCost(); }
        if( HasChameleon() ) { result += Chameleon.GetCost(); }
        if( HasBlueShield() ) { result += BlueShield.GetCost(); }
        if( HasEnviroSealing() ) { result += EnviroSealing.GetCost(); }
        if( HasPartialWing ) { result += Wing.GetCost(); }
        if( HasJumpBooster ) { result += JumpBooster.GetCost(); }
        if( Quad ) {
            if( HasLegAES ) { result += RLAES.GetCost() * 4.0; }
        } else {
            if( HasRAAES ) { result += CurRAAES.GetCost(); }
            if( HasLAAES ) { result += CurLAAES.GetCost(); }
            if( HasLegAES ) { result += RLAES.GetCost() * 2.0; }
        }

        // same goes for the targeting computer and supercharger
        if( CurLoadout.UsingTC() ) {
            result += GetTC().GetCost();
        }
        if( CurLoadout.HasHDTurret() ) { result += CurLoadout.GetHDTurret().GetCost(); }
        if( CurLoadout.HasLTTurret() ) { result += CurLoadout.GetLTTurret().GetCost(); }
        if( CurLoadout.HasRTTurret() ) { result += CurLoadout.GetRTTurret().GetCost(); }
        if( HasTracks) { result += Tracks.GetCost(); }
        if( CurLoadout.HasSupercharger() ) {
            result += CurLoadout.GetSupercharger().GetCost();
        }

        return result;
    }

    public double GetTotalCost() {
        // final cost calculations
        return ( GetEquipCost() + GetChassisCost() ) * GetCostMult();
    }

    public double GetDryCost() {
        // returns the total cost of the mech without ammunition
        return ( GetEquipCost() + GetChassisCost() ) * GetCostMult();
    }

    public double GetCostMult() {
        if( Omnimech ) {
            return 1.25 * MechMult;
        } else {
            return MechMult;
        }
    }

    public double GetBaseChassisCost() {
        // chassis cost in this context is different than the ChassisCost
        // variable.  It includes all components except engine, TC, and 
        // equipment without multiple calculation ("base" cost)

        double result = 0.0;
        if( ! CurPhysEnhance.IsTSM() ) {
            // this is standard musculature.  If we have TSM, it's handled later
            // hack here for very early Primitive 'Mech costs
            if( Primitive && CurLoadout.GetYear() > 2449 ) {
                result += 1000 * Tonnage;
            } else {
                result += 2000 * Tonnage;
            }
        }
        result += CurGyro.GetCost();
        result += CurIntStruc.GetCost();
        result += CurCockpit.GetCost();
        if( HasCommandConsole() ) {
            result += CommandConsole.GetCost();
        }
        if( HasEjectionSeat ) { result += EjectionSeat.GetCost(); }
        result += GetActuators().GetCost();
        result += CurPhysEnhance.GetCost();
        result += GetHeatSinks().GetCost();
        result += GetJumpJets().GetCost();
        result += CurArmor.GetCost();
        if( CurLoadout.IsUsingClanCASE() ) {
            int[] test = CurLoadout.FindExplosiveInstances();
            for( int i = 0; i < test.length; i++ ) {
                if( test[i] > 0 ) {
                    result += CurLoadout.GetCTCase().GetCost();
                }
            }
        }
        if( HasCTCase() ) { result += CurLoadout.GetCTCase().GetCost(); }
        if( HasLTCase() ) { result += CurLoadout.GetCTCase().GetCost(); }
        if( HasRTCase() ) { result += CurLoadout.GetCTCase().GetCost(); }
        if( CurLoadout.HasHDCASEII() ) { result += CurLoadout.GetHDCaseII().GetCost(); }
        if( CurLoadout.HasCTCASEII() ) { result += CurLoadout.GetCTCaseII().GetCost(); }
        if( CurLoadout.HasLTCASEII() ) { result += CurLoadout.GetLTCaseII().GetCost(); }
        if( CurLoadout.HasRTCASEII() ) { result += CurLoadout.GetRTCaseII().GetCost(); }
        if( CurLoadout.HasLACASEII() ) { result += CurLoadout.GetLACaseII().GetCost(); }
        if( CurLoadout.HasRACASEII() ) { result += CurLoadout.GetRACaseII().GetCost(); }
        if( CurLoadout.HasLLCASEII() ) { result += CurLoadout.GetLLCaseII().GetCost(); }
        if( CurLoadout.HasRLCASEII() ) { result += CurLoadout.GetRLCaseII().GetCost(); }

        return result;
    }

    public void Visit( ifVisitor v ) throws Exception {
        v.Visit( this );
    }

    public Preferences GetPrefs() {
        // provided for the components that may be governed by options
        return Prefs;
    }

    public double GetEquipCost() {
        // gets the cost for all non-core items minus ammuntion.
        ArrayList v = CurLoadout.GetNonCore();
        double retval = 0.0;
        if( v.size() > 0 ) {
            for( int i = 0; i < v.size(); i++ ) {
                if( ! (v.get( i ) instanceof Ammunition ) ) {
                    retval += ( (abPlaceable) v.get( i ) ).GetCost();
                }
            }
            if( ! CurEngine.IsNuclear() ) { retval += CurLoadout.GetPowerAmplifier().GetCost(); }
            return retval;
        } else {
            return retval;
        }
    }

    public double GetAmmoCosts() {
        // gets the cost for all non-core items minus ammuntion.
        ArrayList v = CurLoadout.GetNonCore();
        double retval = 0.0;
        if( v.size() > 0 ) {
            for( int i = 0; i < v.size(); i++ ) {
                if( (v.get( i ) instanceof Ammunition ) ) {
                    retval += ( (abPlaceable) v.get( i ) ).GetCost();
                }
            }
            return retval;
        } else {
            return retval;
        }
    }

    @Override
    public int GetAmmoCount( int ammoIndex )
    {
        int retval = 0;
        ArrayList v = CurLoadout.GetNonCore();
        if( v.size() > 0 ) {
            for( int i = 0; i < v.size(); i++ ) {
                if( ( v.get( i ) instanceof Ammunition ) ) {
                    if ( ((Ammunition)v.get(i)).GetAmmoIndex() == ammoIndex )
                        retval += ((Ammunition)v.get(i)).GetLotSize();
                }
            }
            return retval;
        }

        return retval;
    }
    
    /*
     * Gets a count of weapons on a unit that all use the same ammo type
     */
    @Override
    public int GetWeaponCount( int ammoIndex )
    {
        int retval = 0;
        ArrayList v = CurLoadout.GetNonCore();
        if ( v.size() > 0 ) {
            for(Object w : v)
            {
                if (w instanceof RangedWeapon)
                {
                    if (((RangedWeapon)w).GetAmmoIndex() == ammoIndex )
                        retval++;
                }
                if (w instanceof MGArray)
                {
                    if (((MGArray)w).GetAmmoIndex() == ammoIndex )
                        retval++;
                }
            }
        }
        return retval;
    }

    public void AddCTCase() throws Exception {
        // adds CASE equipment to the CT
        CurLoadout.SetCTCASE( true, -1 );
    }

    public void AddLTCase() throws Exception {
        // adds CASE equipment to the LT
        CurLoadout.SetLTCASE( true, -1 );
    }

    public void AddRTCase() throws Exception {
        // adds CASE equipment to the RT
        CurLoadout.SetRTCASE( true, -1 );
    }

    public boolean HasCTCase() {
        // tells us whether the CT has CASE installed
        return CurLoadout.HasCTCASE();
    }

    public boolean HasLTCase() {
        // tells us whether the LT has CASE installed
        return CurLoadout.HasLTCASE();
    }

    public boolean HasRTCase() {
        // tells us whether the RT has CASE installed
        return CurLoadout.HasRTCASE();
    }

    public void RemoveCTCase() {
        // remove the CT CASE equipment
        try {
            CurLoadout.SetCTCASE( false, -1 );
        } catch( Exception e ) {
            // why we have an error while removing CASE is beyond me.  Log it.
            System.err.println( "Exception while removing CT CASE:\n" + e.getMessage() );
        }
    }

    public void RemoveLTCase() {
        // remove the LT CASE equipment
        try {
            CurLoadout.SetLTCASE( false, -1 );
        } catch( Exception e ) {
            // why we have an error while removing CASE is beyond me.  Log it.
            System.err.println( "Exception while removing LT CASE:\n" + e.getMessage() );
        }
    }

    public void RemoveRTCase() {
        // remove the RT CASE equipment
        try {
            CurLoadout.SetRTCASE( false, -1 );
        } catch( Exception e ) {
            // why we have an error while removing CASE is beyond me.  Log it.
            System.err.println( "Exception while removing RT CASE:\n" + e.getMessage() );
        }
    }

    public double GetCaseTonnage() {
        double retval = 0.0;

        if( HasCTCase() ) { retval += 0.5; }
        if( HasLTCase() ) { retval += 0.5; }
        if( HasRTCase() ) { retval += 0.5; }

        return retval;
    }

    public double GetCASEIITonnage() {
        double retval = 0.0;

        if( CurLoadout.HasHDCASEII() ) { retval += CurLoadout.GetHDCaseII().GetTonnage(); }
        if( CurLoadout.HasCTCASEII() ) { retval += CurLoadout.GetCTCaseII().GetTonnage(); }
        if( CurLoadout.HasLTCASEII() ) { retval += CurLoadout.GetLTCaseII().GetTonnage(); }
        if( CurLoadout.HasRTCASEII() ) { retval += CurLoadout.GetRTCaseII().GetTonnage(); }
        if( CurLoadout.HasLACASEII() ) { retval += CurLoadout.GetLACaseII().GetTonnage(); }
        if( CurLoadout.HasRACASEII() ) { retval += CurLoadout.GetRACaseII().GetTonnage(); }
        if( CurLoadout.HasLLCASEII() ) { retval += CurLoadout.GetLLCaseII().GetTonnage(); }
        if( CurLoadout.HasRLCASEII() ) { retval += CurLoadout.GetRLCaseII().GetTonnage(); }

        return retval;
    }

    // handlers for Artemis IV operations.
    public void SetFCSArtemisIV( boolean b ) throws Exception {
        CurLoadout.SetFCSArtemisIV( b );
    }

    public void SetFCSArtemisV( boolean b ) throws Exception {
        CurLoadout.SetFCSArtemisV( b );
    }

    public void SetFCSApollo( boolean b ) throws Exception {
        CurLoadout.SetFCSApollo( b );
    }

    public boolean UsingArtemisIV() {
        return CurLoadout.UsingArtemisIV();
    }

    public boolean UsingArtemisV() {
        return CurLoadout.UsingArtemisV();
    }

    public boolean UsingApollo() {
        return CurLoadout.UsingApollo();
    }

    public boolean UsingDumper(){
        return CurLoadout.UsingDumper();
    }
    
    public void UseDumper (boolean use, String dumpDir){
        CurLoadout.UseDumper(use, dumpDir);
    }

    public Dumper GetDumper() {
        return CurLoadout.GetDumper();
    }

    public void CheckDumper(){
        CurLoadout.CheckDumper();
    }

    public void UseTC( boolean use, boolean clan ) {
        CurLoadout.UseTC( use, clan );
    }

    public void CheckTC() {
        CurLoadout.CheckTC();
    }

    public void UnallocateTC() {
        CurLoadout.UnallocateTC();
    }

    public void SetNullSig( boolean set ) throws Exception {
        if( set == HasNullSig ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( NullSig );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! NullSig.Place( MainLoadout ) ) {
                    MainLoadout.Remove( NullSig );
                    throw new Exception( "There is no available room for the Null Signature System!\nIt will not be allocated." );
                }
                AddMechModifier( NullSig.GetMechModifier() );
                HasNullSig = true;
            } else {
                MainLoadout.Remove( NullSig );
                HasNullSig = false;
            }
        }

        SetChanged( true );
    }

    // the following method is added for when we want to load a 'Mech
    // and have specific locations for the system
    public void SetNullSig( boolean set, LocationIndex[] locs ) throws Exception {
        if( set == HasNullSig ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( NullSig );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! NullSig.Place( MainLoadout, locs ) ) {
                    MainLoadout.Remove( NullSig );
                    throw new Exception( "There is no available room for the Null Signature System!\nIt will not be allocated." );
                }
                AddMechModifier( NullSig.GetMechModifier() );
                HasNullSig = true;
            } else {
                MainLoadout.Remove( NullSig );
                HasNullSig = false;
            }
        }

        SetChanged( true );
    }

    public boolean HasNullSig() {
        return HasNullSig;
    }

    public MultiSlotSystem GetNullSig() {
        return NullSig;
    }

    public void SetChameleon( boolean set ) throws Exception {
        if( set == HasChameleon ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( Chameleon );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! Chameleon.Place( MainLoadout ) ) {
                    MainLoadout.Remove( Chameleon );
                    throw new Exception( "There is no available room for the Chameleon LPS!\nIt will not be allocated." );
                }
                AddMechModifier( Chameleon.GetMechModifier() );
                HasChameleon = true;
            } else {
                MainLoadout.Remove( Chameleon );
                HasChameleon = false;
            }
        }

        SetChanged( true );
    }

    // the following method is added for when we want to load a 'Mech
    // and have specific locations for the system
    public void SetChameleon( boolean set, LocationIndex[] locs ) throws Exception {
        if( set == HasChameleon ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( Chameleon );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! Chameleon.Place( MainLoadout, locs ) ) {
                    MainLoadout.Remove( Chameleon );
                    throw new Exception( "There is no available room for the Chameleon LPS!\nIt will not be allocated." );
                }
                AddMechModifier( Chameleon.GetMechModifier() );
                HasChameleon = true;
            } else {
                MainLoadout.Remove( Chameleon );
                HasChameleon = false;
            }
        }

        SetChanged( true );
    }

    public boolean HasChameleon() {
        return HasChameleon;
    }

    public MultiSlotSystem GetChameleon() {
        return Chameleon;
    }
    
    /**
     * Determines if this 'Mech uses a shield in the right arm
     * Returns true if found and false otherwise.
     * Used to determine the proper location of the "Armor Pts" 
     * text on a record sheet
     */
    public boolean HasRAShield()
    {
        abPlaceable items [] = CurLoadout.GetRACrits();
        for (abPlaceable item : items) {
            if( item instanceof PhysicalWeapon ) {
                if ( ((PhysicalWeapon) item).GetPWClass() == PhysicalWeapon.PW_CLASS_SHIELD )
                return true;
            }
        }
        return false;
    }

    public void SetVoidSig( boolean set ) throws Exception {
        if( set == HasVoidSig ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( VoidSig );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! VoidSig.Place( MainLoadout ) ) {
                    MainLoadout.Remove( VoidSig );
                    throw new Exception( "There is no available room for the Void Signature System!\nIt will not be allocated." );
                }
                AddMechModifier( VoidSig.GetMechModifier() );
                HasVoidSig = true;
            } else {
                MainLoadout.Remove( VoidSig );
                HasVoidSig = false;
            }
        }

        SetChanged( true );
    }

    // the following method is added for when we want to load a 'Mech
    // and have specific locations for the system
    public void SetVoidSig( boolean set, LocationIndex[] locs ) throws Exception {
        if( set == HasVoidSig ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( VoidSig );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! VoidSig.Place( MainLoadout, locs ) ) {
                    MainLoadout.Remove( VoidSig );
                    throw new Exception( "There is no available room for the Void Signature System!\nIt will not be allocated." );
                }
                AddMechModifier( VoidSig.GetMechModifier() );
                HasVoidSig = true;
            } else {
                MainLoadout.Remove( VoidSig );
                HasVoidSig = false;
            }
        }

        SetChanged( true );
    }

    public boolean HasVoidSig() {
        return HasVoidSig;
    }

    public MultiSlotSystem GetVoidSig() {
        return VoidSig;
    }

    public void SetBlueShield( boolean set ) throws Exception {
        if( set == HasBlueShield ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( BlueShield );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! BlueShield.Place( MainLoadout ) ) {
                    MainLoadout.Remove( BlueShield );
                    throw new Exception( "There is no available room for the Blue Shield PFD!\nIt will not be allocated." );
                }
                AddMechModifier( BlueShield.GetMechModifier() );
                HasBlueShield = true;
            } else {
                MainLoadout.Remove( BlueShield );
                HasBlueShield = false;
            }
        }

        SetChanged( true );
    }

    // the following method is added for when we want to load a 'Mech
    // and have specific locations for the system
    public void SetBlueShield( boolean set, LocationIndex[] locs ) throws Exception {
        if( set == HasBlueShield ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( BlueShield );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! BlueShield.Place( MainLoadout, locs ) ) {
                    MainLoadout.Remove( BlueShield );
                    throw new Exception( "There is no available room for the Blue Shield PFD!\nIt will not be allocated." );
                }
                AddMechModifier( BlueShield.GetMechModifier() );
                HasBlueShield = true;
            } else {
                MainLoadout.Remove( BlueShield );
                HasBlueShield = false;
            }
        }

        SetChanged( true );
    }

    public boolean HasBlueShield() {
        return HasBlueShield;
    }

    public MultiSlotSystem GetBlueShield() {
        return BlueShield;
    }

    public void SetEnviroSealing( boolean set ) throws Exception {
        if( set == HasEnviroSealing ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( EnviroSealing );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! EnviroSealing.Place( MainLoadout ) ) {
                    MainLoadout.Remove( EnviroSealing );
                    throw new Exception( "There is no available room for the Environmental Sealing!\nIt will not be allocated." );
                }
                AddMechModifier( EnviroSealing.GetMechModifier() );
                HasEnviroSealing = true;
            } else {
                MainLoadout.Remove( EnviroSealing );
                HasEnviroSealing = false;
            }
        }

        SetChanged( true );
    }

    // the following method is added for when we want to load a 'Mech
    // and have specific locations for the system
    public void SetEnviroSealing( boolean set, LocationIndex[] locs ) throws Exception {
        if( set == HasEnviroSealing ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( EnviroSealing );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! EnviroSealing.Place( MainLoadout, locs ) ) {
                    MainLoadout.Remove( EnviroSealing );
                    throw new Exception( "There is no available room for the Environmental Sealing!\nIt will not be allocated." );
                }
                AddMechModifier( EnviroSealing.GetMechModifier() );
                HasEnviroSealing = true;
            } else {
                MainLoadout.Remove( EnviroSealing );
                HasEnviroSealing = false;
            }
        }

        SetChanged( true );
    }

    public boolean HasEnviroSealing() {
        return HasEnviroSealing;
    }

    public MultiSlotSystem GetEnviroSealing() {
        return EnviroSealing;
    }


    public void SetTracks( boolean set ) throws Exception {
        if( set == HasTracks ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( Tracks );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! Tracks.Place( MainLoadout ) ) {
                    MainLoadout.Remove( Tracks );
                    throw new Exception( "There is no available room for the Tracks!\nIt will not be allocated." );
                }
                AddMechModifier( Tracks.GetMechModifier() );
                HasTracks = true;
            } else {
                MainLoadout.Remove( Tracks );
                HasTracks = false;
            }
        }

        SetChanged( true );
    }

    // the following method is added for when we want to load a 'Mech
    // and have specific locations for the system
    public void SetTracks( boolean set, LocationIndex[] locs ) throws Exception {
        if( set == HasTracks ) {
            return;
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( Tracks );
                } catch( Exception e ) {
                    throw e;
                }
                if( ! Tracks.Place( MainLoadout, locs ) ) {
                    MainLoadout.Remove( Tracks );
                    throw new Exception( "There is no available room for the Tracks!\nIt will not be allocated." );
                }
                AddMechModifier( Tracks.GetMechModifier() );
                HasTracks = true;
            } else {
                MainLoadout.Remove( Tracks );
                HasTracks = false;
            }
        }

        SetChanged( true );
    }

    public boolean HasTracks() {
        return HasTracks;
    }

    public MultiSlotSystem GetTracks() {
        return Tracks;
    }

    public void SetEjectionSeat( boolean set ) throws Exception {
        if( set == HasEjectionSeat ) {
            return;
        } else {
            if( set ) {
                // Ejection seat can only go in the main loadout
                MainLoadout.AddTo( EjectionSeat, LocationIndex.MECH_LOC_HD, 3 );
                HasEjectionSeat = true;
            } else {
                MainLoadout.Remove( EjectionSeat );
                HasEjectionSeat = false;
            }
        }

        SetChanged( true );
    }

    public boolean HasEjectionSeat() {
        return HasEjectionSeat;
    }

    public SimplePlaceable GetEjectionSeat() {
        return EjectionSeat;
    }

    public void SetPartialWing( boolean b ) throws Exception {
        if( Omnimech ) { return; }
        if( HasJumpBooster ) { throw new Exception( "Partial Wing is incompatible with Mechanical Jump Boosters." ); }
        if( b ) {
            Wing.SetClan( (GetTechBase() == AvailableCode.TECH_CLAN) || (GetTechBase() == AvailableCode.TECH_BOTH) );
            if( ! Wing.Place( MainLoadout ) ) {
                throw new Exception( "There is no available room for the Partial Wing!\nIt will not be allocated." );
            }
        } else {
            Wing.Remove( MainLoadout );
        }
        HasPartialWing = b;
    }

    public void SetPartialWing( boolean b, boolean useClan ) throws Exception {
        if( Omnimech ) { return; }
        if( HasJumpBooster ) { throw new Exception( "Partial Wing is incompatible with Mechanical Jump Boosters." ); }
        if( b ) {
            Wing.SetClan( useClan );
            if( ! Wing.Place( MainLoadout ) ) {
                HasPartialWing = false;
                throw new Exception( "There is no available room for the Partial Wing!\nIt will not be allocated." );
            }
        } else {
            Wing.Remove( MainLoadout );
        }
        HasPartialWing = b;
    }

    public void SetPartialWing( boolean b, boolean useClan, LocationIndex[] lpw ) throws Exception {
        if( Omnimech ) { return; }
        if( HasJumpBooster ) { throw new Exception( "Partial Wing is incompatible with Mechanical Jump Boosters." ); }
        if( b ) {
            Wing.SetClan( useClan );
            if( ! Wing.Place( MainLoadout, lpw ) ) {
                HasPartialWing = false;
                throw new Exception( "There is no available room for the Partial Wing!\nIt will not be allocated." );
            }
        } else {
            Wing.Remove( MainLoadout );
        }
        HasPartialWing = b;
    }

    public void SetPartialWing( boolean b, LocationIndex[] lpw ) throws Exception {
        if( Omnimech ) { return; }
        if( HasJumpBooster ) { throw new Exception( "Partial Wing is incompatible with Mechanical Jump Boosters." ); }
        if( b ) {
            Wing.SetClan( (GetTechBase() == AvailableCode.TECH_CLAN) || (GetTechBase() == AvailableCode.TECH_BOTH) );
            if( ! Wing.Place( MainLoadout, lpw ) ) {
                HasPartialWing = false;
                throw new Exception( "There is no available room for the Partial Wing!\nIt will not be allocated." );
            }
        } else {
            Wing.Remove( MainLoadout );
        }
        HasPartialWing = b;
    }

    public boolean UsingPartialWing() {
        return HasPartialWing;
    }

    public PartialWing GetPartialWing() {
        return Wing;
    }

    public void SetJumpBooster( boolean b ) throws Exception {
        if( Omnimech ) { return; }
        if( HasPartialWing ) { throw new Exception( "Mechanical Jump Booster is incompatible with Partial Wing." ); }
        if( b ) {
            if( ! JumpBooster.Place( MainLoadout ) ) {
                throw new Exception( "There is no available room for the Jump Boosters!\nThey will not be allocated." );
            }
        } else {
            JumpBooster.Remove( MainLoadout );
            JumpBooster.SetBoostMP( 0 );
        }
        HasJumpBooster = b;
    }

    public boolean UsingJumpBooster() {
        return HasJumpBooster;
    }

    public MechanicalJumpBooster GetJumpBooster() {
        return JumpBooster;
    }

    public void SetLAAES( boolean set, int index ) throws Exception {
        if( set == HasLAAES ) { return; }
        if( IsQuad() ) {
            throw new Exception( "A quad 'Mech may not mount arm-based A.E.S. systems." );
        }
        if( set ) {
            try {
                MainLoadout.CheckExclusions( LAAES );
            } catch( Exception e ) {
                throw e;
            }
            if( index < 0 ) {
                // plop it in the arm where it'll fit.
                CurLoadout.AddToLA( CurLAAES );
                HasLAAES = true;
            } else {
                // we have a specific location in mind here
                CurLoadout.AddToLA( CurLAAES, index );
                HasLAAES = true;
            }
        } else {
            CurLoadout.Remove( CurLAAES );
            HasLAAES = false;
        }

        SetChanged( true );
    }

    public boolean HasLAAES() {
        return HasLAAES;
    }

    public AESSystem GetLAAES() {
        return CurLAAES;
    }

    public void SetRAAES( boolean set, int index ) throws Exception {
        if( set == HasRAAES ) { return; }
        if( IsQuad() ) {
            throw new Exception( "A quad 'Mech may not mount arm-based A.E.S. systems." );
        }
        if( set ) {
            try {
                MainLoadout.CheckExclusions( RAAES );
            } catch( Exception e ) {
                throw e;
            }
            if( index < 0 ) {
                // plop it in the arm where it'll fit.
                CurLoadout.AddToRA( CurRAAES );
                HasRAAES = true;
            } else {
                // we have a specific location in mind here
                CurLoadout.AddToRA( CurRAAES, index );
                HasRAAES = true;
            }
        } else {
            CurLoadout.Remove( CurRAAES );
            HasRAAES = false;
        }

        SetChanged( true );
    }

    public boolean HasRAAES() {
        return HasRAAES;
    }

    public AESSystem GetRAAES() {
        return CurRAAES;
    }

    public void SetLegAES( boolean set, LocationIndex[] Loc ) throws Exception {
        if( set == HasLegAES ) { return; }
        boolean FRL = false, FLL = false, RL = false, LL = false;
        if( IsQuad() ) {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( LAAES );
                } catch( Exception e ) {
                    throw e;
                }
                if( Loc != null ) {
                    try {
                        // we've got some locations, go through them and adjust for
                        // any missing locations
                        for( int i = 0; i < Loc.length; i++ ) {
                            switch( Loc[i].Location ) {
                                case LocationIndex.MECH_LOC_RA:
                                    if( FRL ) {
                                        throw new Exception( "The 'Mech already has an AES system in the FRL.\nAES System will not be installed." );
                                    } else {
                                        CurLoadout.AddToRA( CurRAAES, Loc[i].Index );
                                        FRL = true;
                                    }
                                    break;
                                case LocationIndex.MECH_LOC_LA:
                                    if( FLL ) {
                                        throw new Exception( "The 'Mech already has an AES system in the FLL.\nAES System will not be installed." );
                                    } else {
                                        CurLoadout.AddToLA( CurLAAES, Loc[i].Index );
                                        FLL = true;
                                    }
                                    break;
                                case LocationIndex.MECH_LOC_RL:
                                    if( RL ) {
                                        throw new Exception( "The 'Mech already has an AES system in the RL.\nAES System will not be installed." );
                                    } else {
                                        CurLoadout.AddToRL( RLAES, Loc[i].Index );
                                        RL = true;
                                    }
                                    break;
                                case LocationIndex.MECH_LOC_LL:
                                    if( LL ) {
                                        throw new Exception( "The 'Mech already has an AES system in the LL.\nAES System will not be installed." );
                                    } else {
                                        CurLoadout.AddToLL( LLAES, Loc[i].Index );
                                        LL = true;
                                    }
                                    break;
                            }
                        }

                        // now handle any locations we don't have
                        if( ! FRL ) {
                            CurLoadout.AddToRA( CurRAAES );
                        }   
                        if( ! FLL ) {
                            CurLoadout.AddToLA( CurLAAES );
                        }
                        if( ! RL ) {
                            CurLoadout.AddToRL( RLAES );
                        }
                        if( ! LL ) {
                            CurLoadout.AddToLL( LLAES );
                        }
                    } catch( Exception e ) {
                        // remove all the leg AES systems in the 'Mech
                        CurLoadout.Remove( FRLAES );
                        CurLoadout.Remove( FLLAES );
                        CurLoadout.Remove( RLAES );
                        CurLoadout.Remove( LLAES );
                        HasLegAES = false;
                        throw e;
                    }

                    // looks like everything worked out well
                    HasLegAES = true;
                } else {
                    // no locations so just plop them in
                    try {
                        CurLoadout.AddToRA( CurRAAES );
                        CurLoadout.AddToLA( CurLAAES );
                        CurLoadout.AddToRL( RLAES );
                        CurLoadout.AddToLL( LLAES );
                        HasLegAES = true;
                    } catch( Exception e ) {
                        // remove all the leg AES systems in the 'Mech
                        CurLoadout.Remove( FRLAES );
                        CurLoadout.Remove( FLLAES );
                        CurLoadout.Remove( RLAES );
                        CurLoadout.Remove( LLAES );
                        HasLegAES = false;
                        throw e;
                    }
                }
            } else {
                // remove all the leg AES systems in the 'Mech
                CurLoadout.Remove( FRLAES );
                CurLoadout.Remove( FLLAES );
                CurLoadout.Remove( RLAES );
                CurLoadout.Remove( LLAES );
                HasLegAES = false;
            }
        } else {
            if( set ) {
                try {
                    MainLoadout.CheckExclusions( LAAES );
                } catch( Exception e ) {
                    throw e;
                }
                if( Loc != null ) {
                    try {
                        // we've got some locations, go through them and adjust for
                        // any missing locations
                        for( int i = 0; i < Loc.length; i++ ) {
                            switch( Loc[i].Location ) {
                                case LocationIndex.MECH_LOC_RL:
                                    if( RL ) {
                                        throw new Exception( "The 'Mech already has an AES system in the RL.\nAES System will not be installed." );
                                    } else {
                                        CurLoadout.AddToRL( RLAES, Loc[i].Index );
                                        RL = true;
                                    }
                                    break;
                                case LocationIndex.MECH_LOC_LL:
                                    if( LL ) {
                                        throw new Exception( "The 'Mech already has an AES system in the LL.\nAES System will not be installed." );
                                    } else {
                                        CurLoadout.AddToLL( LLAES, Loc[i].Index );
                                        LL = true;
                                    }
                                    break;
                            }
                        }

                        // now handle any locations we don't have
                        if( ! RL ) {
                            CurLoadout.AddToRL( RLAES );
                        }
                        if( ! LL ) {
                            CurLoadout.AddToLL( LLAES );
                        }
                    } catch( Exception e ) {
                        // remove all the leg AES systems in the 'Mech
                        CurLoadout.Remove( RLAES );
                        CurLoadout.Remove( LLAES );
                        HasLegAES = false;
                        throw e;
                    }

                    // looks like everything worked out well
                    HasLegAES = true;
                } else {
                    // no locations so just plop them in
                    try {
                        CurLoadout.AddToRL( RLAES );
                        CurLoadout.AddToLL( LLAES );
                        HasLegAES = true;
                    } catch( Exception e ) {
                        // remove all the leg AES systems in the 'Mech
                        CurLoadout.Remove( RLAES );
                        CurLoadout.Remove( LLAES );
                        HasLegAES = false;
                        throw e;
                    }
                }
            } else {
                // remove all the leg AES systems in the 'Mech
                CurLoadout.Remove( RLAES );
                CurLoadout.Remove( LLAES );
                HasLegAES = false;
            }
        }

        SetChanged( true );
    }

    public boolean HasLegAES() {
        return HasLegAES;
    }

    public AESSystem GetFRLAES() {
        return FRLAES;
    }

    public AESSystem GetFLLAES() {
        return FLLAES;
    }

    public AESSystem GetRLAES() {
        return RLAES;
    }

    public AESSystem GetLLAES() {
        return LLAES;
    }

    public boolean CanUseFHES() {
        if( HasCommandConsole() || CurCockpit.IsTorsoMounted() ) {
            return false;
        } else {
            return true;
        }
    }

    public void SetFHES( boolean b ) {
        HasFHES = b;
    }

    public boolean HasFHES() {
        return HasFHES;
    }

    public AvailableCode GetFHESAC() {
        return FHESAC;
    }

    public void CheckPhysicals() {
        // unallocates physical weapons, especially if the tonnage changes
        // we'll also check to see if the mech is a quad and remove the weapons
        ArrayList v = CurLoadout.GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            abPlaceable p = (abPlaceable) v.get( i );
            if( p instanceof PhysicalWeapon ) {
                CurLoadout.UnallocateAll( p, false );
            }
        }
    }

    public AvailableCode GetOmniMechAvailability() {
        return OmniAvailable;
    }

    public AvailableCode GetAvailability() {
        // returns the availability code for this mech based on all components
        AvailableCode Base = new AvailableCode( CurLoadout.GetTechBase() );
        Base.SetCodes( 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A' );
        Base.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        Base.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        Base.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        if( Omnimech ) {
            Base.Combine( OmniAvailable );
        }

        // combine the availability codes from all equipment
        Base.Combine( CurEngine.GetAvailability() );
        Base.Combine( CurGyro.GetAvailability() );
        Base.Combine( CurIntStruc.GetAvailability() );
        Base.Combine( CurCockpit.GetAvailability() );
        if( HasCommandConsole() ) {
            Base.Combine( CommandConsole.GetAvailability() );
        }
        Base.Combine( GetActuators().GetAvailability() );
        Base.Combine( CurPhysEnhance.GetAvailability() );
        Base.Combine( GetHeatSinks().GetAvailability() );
        if( GetJumpJets().GetNumJJ() > 0 ) {
            Base.Combine( GetJumpJets().GetAvailability() );
        }
        Base.Combine( CurArmor.GetAvailability() );
        if( CurLoadout.UsingTC() ) {
            Base.Combine( GetTC().GetAvailability() );
        }
        if( HasCTCase() || HasRTCase() || HasLTCase() ) {
            Base.Combine( CurLoadout.GetCTCase().GetAvailability() );
        }
        if( CurLoadout.HasHDCASEII() || CurLoadout.HasCTCASEII() || CurLoadout.HasLTCASEII() || CurLoadout.HasRTCASEII() ||
            CurLoadout.HasLACASEII() || CurLoadout.HasRACASEII() || CurLoadout.HasLLCASEII() || CurLoadout.HasRLCASEII() ) {
            Base.Combine( CurLoadout.GetCTCaseII().GetAvailability() );
        }
        if( ! CurEngine.IsNuclear() ) { Base.Combine( CurLoadout.GetPowerAmplifier().GetAvailability() ); }
        ArrayList v = CurLoadout.GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            Base.Combine( ((abPlaceable) v.get( i )).GetAvailability() );
        }
        if( CurLoadout.HasSupercharger() ) {
            Base.Combine( CurLoadout.GetSupercharger().GetAvailability() );
        }

        if( HasBlueShield() ) {
            Base.Combine( BlueShield.GetAvailability() );
        }
        if( HasNullSig() ) {
            Base.Combine( NullSig.GetAvailability() );
        }
        if( HasVoidSig() ) {
            Base.Combine( VoidSig.GetAvailability() );
        }
        if( HasChameleon() ) {
            Base.Combine( Chameleon.GetAvailability() );
        }
        if( HasEnviroSealing() ) {
            Base.Combine( EnviroSealing.GetAvailability() );
        }
        if( HasEjectionSeat() ) {
            Base.Combine( EjectionSeat.GetAvailability() );
        }
        if( HasFHES() ) {
            Base.Combine( FHESAC );
        }

        // now adjust for the era.
        if( CurLoadout.GetEra() == AvailableCode.ERA_SUCCESSION ) {
            // cut out the Star League stuff.
            AvailableCode SW = new AvailableCode( Base.GetTechBase() );
            SW.SetCodes( 'A', 'X', 'A', 'A', 'A', 'X', 'A', 'A' );
            SW.SetISDates( 0, 0, false, 2801, 10000, 0, false, false );
            SW.SetCLDates( 0, 0, false, 2801, 10000, 0, false, false );
            SW.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
            Base.Combine( SW );
        }
        if( CurLoadout.GetEra() == AvailableCode.ERA_CLAN_INVASION ) {
            // cut out the Star League and Succession Wars stuff.
            AvailableCode CI = new AvailableCode( Base.GetTechBase() );
            CI.SetCodes( 'A', 'X', 'X', 'A', 'A', 'X', 'X', 'A' );
            CI.SetISDates( 0, 0, false, 3051, 10000, 0, false, false );
            CI.SetCLDates( 0, 0, false, 3051, 10000, 0, false, false );
            CI.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
            Base.Combine( CI );
        }

        return Base;
    }

    public void CheckArmoredComponents() {
        if( GetRulesLevel() < AvailableCode.RULES_EXPERIMENTAL ) {
            CurEngine.ArmorComponent( false );
            CurGyro.ArmorComponent( false );
            CurCockpit.ArmorComponent( false );
            CurCockpit.GetFirstSensors().ArmorComponent( false );
            CurCockpit.GetSecondSensors().ArmorComponent( false );
            CurCockpit.GetFirstLS().ArmorComponent( false );
            CurCockpit.GetSecondLS().ArmorComponent( false );
            if( CurCockpit.GetThirdSensors() != null ) {
                CurCockpit.GetThirdSensors().ArmorComponent( false );
            }
            CurPhysEnhance.ArmorComponent( false );
            GetActuators().LeftFoot.ArmorComponent( false );
            GetActuators().RightFoot.ArmorComponent( false );
            GetActuators().LeftLowerLeg.ArmorComponent( false );
            GetActuators().RightLowerLeg.ArmorComponent( false );
            GetActuators().LeftUpperLeg.ArmorComponent( false );
            GetActuators().RightUpperLeg.ArmorComponent( false );
            GetActuators().LeftHip.ArmorComponent( false );
            GetActuators().RightHip.ArmorComponent( false );

            GetActuators().LeftShoulder.ArmorComponent( false );
            GetActuators().RightShoulder.ArmorComponent( false );
            GetActuators().LeftUpperArm.ArmorComponent( false );
            GetActuators().RightUpperArm.ArmorComponent( false );
            GetActuators().LeftLowerArm.ArmorComponent( false );
            GetActuators().RightLowerArm.ArmorComponent( false );
            GetActuators().LeftHand.ArmorComponent( false );
            GetActuators().RightHand.ArmorComponent( false );

            GetActuators().LeftFrontFoot.ArmorComponent( false );
            GetActuators().RightFrontFoot.ArmorComponent( false );
            GetActuators().LeftFrontLowerLeg.ArmorComponent( false );
            GetActuators().RightFrontLowerLeg.ArmorComponent( false );
            GetActuators().LeftFrontUpperLeg.ArmorComponent( false );
            GetActuators().RightFrontUpperLeg.ArmorComponent( false );
            GetActuators().LeftFrontHip.ArmorComponent( false );
            GetActuators().RightFrontHip.ArmorComponent( false );

            if( GetHeatSinks().GetPlacedHeatSinks().length > 0 ) {
                HeatSink[] placed = GetHeatSinks().GetPlacedHeatSinks();
                for( int i = 0; i < placed.length; i++ ) {
                    placed[i].ArmorComponent( false );
                }
            }
            if( GetJumpJets().GetNumJJ() > 0 ) {
                JumpJet[] placed = GetJumpJets().GetPlacedJumps();
                for( int i = 0; i < placed.length; i++ ) {
                    placed[i].ArmorComponent( false );
                }
            }
            if( HasCommandConsole() ) {
                CommandConsole.ArmorComponent( false );
            }
            if( CurLoadout.UsingTC() ) {
                GetTC().ArmorComponent( false );
            }
            if( CurLoadout.HasSupercharger() ) {
                CurLoadout.GetSupercharger().ArmorComponent( false );
            }
        }
    }

    public ArrayList SortLoadout( ArrayList v ) {
        return SortWeapons(v, false, false);
    }

    public abPlaceable[] SortWeapons( ArrayList v, boolean rear ) {
        // convert the results
        ArrayList r = SortWeapons(v, rear, true);
        abPlaceable[] result = new abPlaceable[r.size()];
        for( int i = 0; i < r.size(); i++ ) {
            result[i] = (abPlaceable) r.get( i );
        }
        return result;
    }

    // sorting routine for weapon BV calculation. this is undoubtedly slow
    public ArrayList SortWeapons( ArrayList v, boolean rear, boolean DoRearHeatCheck ) {
        // sort by BV first (using gnomesort for less code.  may have to change 
        // this depending on the slowness of the program.  I figure lower overhead
        // will have better results at this time, and mechs typically don't
        // carry more than twelve to fifteen weapons.  I'll have to test this.
        int i = 1, j = 2;
        boolean TC = UsingTC();
        Object swap;
        while( i < v.size() ) {
            // get the two items we'll be comparing
            boolean AES1 = UseAESModifier( ((abPlaceable) v.get( i - 1 )) );
            boolean AES2 = UseAESModifier( ((abPlaceable) v.get( i )) );
            if( ((abPlaceable) v.get( i - 1 )).GetCurOffensiveBV( rear, TC, AES1 ) >= ((abPlaceable) v.get( i )).GetCurOffensiveBV( rear, TC, AES2 ) ) {
                i = j;
                j += 1;
            } else {
                swap = v.get( i - 1 );
                v.set( i - 1, v.get( i ) );
                v.set( i, swap );
                i -= 1;
                if( i == 0 ) {
                    i = 1;
                }
            }
        }

        // check our values, ensuring that rear-firing weapons, then lower heat
        // weapons take precedence
        if ( DoRearHeatCheck ) {
            i = 1;
            while( i < v.size() ) {
                boolean AES1 = UseAESModifier( ((abPlaceable) v.get( i - 1 )) );
                boolean AES2 = UseAESModifier( ((abPlaceable) v.get( i )) );
                if( ((abPlaceable) v.get( i - 1 )).GetCurOffensiveBV( rear, TC, AES1 ) == ((abPlaceable) v.get( i )).GetCurOffensiveBV( rear, TC, AES2 ) ) {
                    if( rear ) {
                        if( ((abPlaceable) v.get( i - 1 )).IsMountedRear() &! ((abPlaceable) v.get( i )).IsMountedRear() ) {
                            swap = v.get( i - 1 );
                            v.set( i - 1, v.get( i ) );
                            v.set( i, swap );
                        } else if( ((ifWeapon) v.get( i - 1)).GetHeat() > ((ifWeapon) v.get( i )).GetHeat() ) {
                            swap = v.get( i - 1 );
                            v.set( i - 1, v.get( i ) );
                            v.set( i, swap );
                        }
                    } else {
                        if( ! ((abPlaceable) v.get( i - 1 )).IsMountedRear() && ((abPlaceable) v.get( i )).IsMountedRear() ) {
                            swap = v.get( i - 1 );
                            v.set( i - 1, v.get( i ) );
                            v.set( i, swap );
                        } else if( ((ifWeapon) v.get( i - 1)).GetHeat() > ((ifWeapon) v.get( i )).GetHeat() ) {
                            swap = v.get( i - 1 );
                            v.set( i - 1, v.get( i ) );
                            v.set( i, swap );
                        }
                    }
                }
                i++;
            }
        }

        return v;
    }

    public boolean UseAESModifier( abPlaceable a ) {
        if( ! ( a instanceof ifWeapon ) ) { return false; }
        if( HasLegAES || HasRAAES || HasLAAES ) {
            if( a.CanSplit() ) {
                ArrayList v = CurLoadout.FindSplitIndex( a );
                if( v.size() > 1 || v.size() < 1 ) { return false; }
                int test = ((LocationIndex) v.get( 0 )).Location;
                if( Quad ) {
                    if( HasLegAES && ( test == LocationIndex.MECH_LOC_RL || test == LocationIndex.MECH_LOC_LL || test == LocationIndex.MECH_LOC_RA || test == LocationIndex.MECH_LOC_LA ) ) {
                        if( a instanceof PhysicalWeapon ) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    if( HasLegAES && ( test == LocationIndex.MECH_LOC_RL || test == LocationIndex.MECH_LOC_LL ) ) {
                        if( a instanceof PhysicalWeapon ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    if( HasRAAES && test == LocationIndex.MECH_LOC_RA ) {
                        return true;
                    }
                    if( HasLAAES && test == LocationIndex.MECH_LOC_LA ) {
                        return true;
                    }
                }
            } else {
                int test = CurLoadout.Find( a );
                if( Quad ) {
                    if( HasLegAES && ( test == LocationIndex.MECH_LOC_RL || test == LocationIndex.MECH_LOC_LL || test == LocationIndex.MECH_LOC_RA || test == LocationIndex.MECH_LOC_LA ) ) {
                        if( a instanceof PhysicalWeapon ) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    if( HasLegAES && ( test == LocationIndex.MECH_LOC_RL || test == LocationIndex.MECH_LOC_LL ) ) {
                        if( a instanceof PhysicalWeapon ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    if( HasRAAES && test == LocationIndex.MECH_LOC_RA ) {
                        return true;
                    }
                    if( HasLAAES && test == LocationIndex.MECH_LOC_LA ) {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    public SimplePlaceable GetCommandConsole() {
        return CommandConsole;
    }

    public boolean SetCommandConsole( boolean set ) {
        if( HasCommandConsole() == set ) { return true; }
        if( set ) {
            try {
                if ( !CurCockpit.IsTorsoMounted() ) {
                    //CurLoadout.SafeUnallocateHD();
                    CurLoadout.AddToHD( CommandConsole );
                } else {
                    CurLoadout.AddToCT( CommandConsole );
                }
            } catch( Exception e ) {
                return false;
            }
        } else {
            CurLoadout.Remove( CommandConsole );
        }

        return true;
    }

    public boolean HasCommandConsole() {
        if( CurLoadout.IsAllocated( CommandConsole ) ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean HasC3() {
        // checks for C3 systems.
        SimplePlaceable p = new SimplePlaceable( "C3Test", "C3Test", "C3Test", "C3Test", "none", 0, false, null );
        p.SetExclusions( new Exclusion( new String[] { "C3" }, "C3Test" ) );
        try {
            CurLoadout.CheckExclusions( p );
        } catch( Exception e ) {
            return true;
        }
        return false;
    }

    public boolean HasECM() {
        // ensures that, if the 'Mech needs ECM, it has it.
        SimplePlaceable p = new SimplePlaceable( "ECMTest", "ECMTest", "ECMTest", "ECMTest", "none", 0, false, null );
        p.SetExclusions( new Exclusion( new String[] { "ECM", "Watchdog" }, "ECMTest" ) );
        try {
            CurLoadout.CheckExclusions( p );
        } catch( Exception e ) {
            return true;
        }
        return false;
    }

    public boolean HasProbe() {
        // ensures that, if the 'Mech needs Probe, it has it.
        SimplePlaceable p = new SimplePlaceable( "ProbeTest", "ProbeTest", "ProbeTest", "ProbeTest", "none", 0, false, null );
        p.SetExclusions( new Exclusion( new String[] { "Probe" }, "ProbeTest" ) );
        try {
            CurLoadout.CheckExclusions( p );
        } catch( Exception e ) {
            return true;
        }
        return false;
    }

    public boolean ValidateECM() {
        if( CurArmor.IsStealth() || HasVoidSig() ) {
            return HasECM();
        }
        return true;
    }

    public ArrayList GetMechMods() {
        return MechMods;
    }

    public void AddMechModifier( MechModifier m ) {
        if( m == null ) { return; }
        if( ! MechMods.contains( m ) &! CurLoadout.GetMechMods().contains( m ) ) {
            MechMods.add( m );
        }
    }

    public void RemoveMechMod( MechModifier m ) {
        if( m == null ) { return; }
        MechMods.remove( m );
    }

    public MechModifier GetTotalModifiers( boolean BV, boolean MASCTSM ) {
        MechModifier retval = new MechModifier( 0, 0, 0, 0.0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, true, true );
        if( MechMods.size() > 0 ) {
            for( int i = 0; i < MechMods.size(); i++ ) {
                if( BV ) {
                    if( MASCTSM ) {
                        retval.BVCombine( ((MechModifier) MechMods.get( i )) );
                    } else {
                        if( MechMods.get( i ) != CurPhysEnhance.GetMechModifier() ) {
                            retval.BVCombine( ((MechModifier) MechMods.get( i )) );
                        }
                    }
                } else {
                    if( MASCTSM ) {
                        retval.Combine( ((MechModifier) MechMods.get( i )) );
                    } else {
                        if( MechMods.get( i ) != CurPhysEnhance.GetMechModifier() ) {
                            retval.Combine( ((MechModifier) MechMods.get( i )) );
                        }
                    }
                }
            }
        }
        if( CurLoadout.GetMechMods().size() > 0 ) {
            for( int i = 0; i < CurLoadout.GetMechMods().size(); i++ ) {
                if( BV ) {
                    retval.BVCombine( ((MechModifier) CurLoadout.GetMechMods().get( i )) );
                } else {
                    retval.Combine( ((MechModifier) CurLoadout.GetMechMods().get( i )) );
                }
            }
        }
        return retval;
    }

    public boolean UsingFractionalAccounting() {
        return FractionalAccounting;
    }

    public void SetFractionalAccounting( boolean b ) {
        FractionalAccounting = b;
    }

    public void SetOverview( String n ) {
        Overview = n;

        SetChanged( true );
    }

    public void SetCapabilities( String n ) {
        Capabilities = n;

        SetChanged( true );
    }

    public void SetHistory( String n ) {
        History = n;

        SetChanged( true );
    }

    public void SetDeployment( String n ) {
        Deployment = n;

        SetChanged( true );
    }

    public void SetVariants( String n ) {
        Variants = n;

        SetChanged( true );
    }

    public void SetNotables( String n ) {
        Notables = n;

        SetChanged( true );
    }

    public void SetAdditional( String n ) {
        Additional = n;

        SetChanged( true );
    }

    public void SetCompany( String n ) {
        Company = n;

        SetChanged( true );
    }

    public void SetLocation( String n ) {
        Location = n;

        SetChanged( true );
    }

    public void SetEngineManufacturer( String n ) {
        EngineManufacturer = n;

        SetChanged( true );
    }

    public void SetArmorModel( String n ) {
        ArmorModel = n;

        SetChanged( true );
    }

    public void SetChassisModel( String n ) {
        ChassisModel = n;

        SetChanged( true );
    }

    public void SetJJModel( String n ) {
        JJModel = n;

        SetChanged( true );
    }

    public void SetCommSystem( String n ) {
        CommSystem = n;

        SetChanged( true );
    }

    public void SetTandTSystem( String n ) {
        TandTSystem = n;

        SetChanged( true );
    }

    public void SetSource( String s ) {
        CurLoadout.SetSource( s );
        SetChanged( true );
    }

    public String GetOverview() {
        return Overview;
    }

    public String GetCapabilities() {
        return Capabilities;
    }

    public String GetHistory() {
        return History;
    }

    public String GetDeployment() {
        return Deployment;
    }

    public String GetVariants() {
        return Variants;
    }

    public String GetNotables() {
        return Notables;
    }

    public String GetAdditional() {
        return Additional;
    }

    public String GetCompany() {
        if( Company.matches( "" ) ) {
            return "Unknown";
        }
        return Company;
    }

    public String GetLocation() {
        if( Location.matches( "" ) ) {
            return "Unknown";
        }
        return Location;
    }

    public String GetEngineManufacturer() {
        if( EngineManufacturer.matches( "" ) ) {
            return "Unknown";
        }
        return EngineManufacturer;
    }

    public String GetArmorModel() {
        if( ArmorModel.matches( "" ) ) {
            return "Unknown";
        }
        return ArmorModel;
    }

    public String GetChassisModel() {
        if( ChassisModel.matches( "" ) ) {
            return "Unknown";
        }
        return ChassisModel;
    }

    public String GetJJModel() {
        if( ! Omnimech && GetJumpJets().GetNumJJ() < 1 ) {
            return "None";
        }
        if( JJModel.matches( "" ) ) {
            return "Unknown";
        }
        return JJModel;
    }

    public String GetCommSystem() {
        if( CommSystem.matches( "" ) ) {
            return "Unknown";
        }
        return CommSystem;
    }

    public String GetTandTSystem() {
        if( TandTSystem.matches( "" ) ) {
            return "Unknown";
        }
        return TandTSystem;
    }

    public String GetSource() {
        return CurLoadout.GetSource();
    }

    public void SetChanged( boolean b ) {
        Changed = b;
    }

    public boolean HasChanged() {
        return Changed;
    }

    public int GetBFPoints(){
        return Math.round( GetCurrentBV() / 100.0f );
    }

    public int GetBFSize(){
        int mass = GetTonnage();
        if( mass < 40 ){
            return BFConstants.BF_SIZE_LIGHT;
        }else if( mass < 60 ){
            return BFConstants.BF_SIZE_MEDIUM;
        }else if ( mass < 80 ){
            return BFConstants.BF_SIZE_HEAVY;
        }else{
            return BFConstants.BF_SIZE_ASSAULT;
        }
    }

    public int GetBFPrimeMovement(){
        double retval = GetAdjustedWalkingMP(false, false);

        // Adjust retval for MASC and SC
        if ( CurLoadout.HasSupercharger() && GetPhysEnhance().IsMASC() ){
            retval *= 1.5;
        }else if ( CurLoadout.HasSupercharger() || GetPhysEnhance().IsMASC() ){
            retval *= 1.25f;
        }

        return (int) Math.round(retval);
    }
    
    public String GetBFPrimeMovementMode(){
        int walkMP = GetAdjustedWalkingMP(false, false);
        int jumpMP = GetAdjustedJumpingMP(false);
        if ( GetJumpBoosterMP() > jumpMP ) jumpMP = GetJumpBoosterMP();

        if ( walkMP == jumpMP && GetBFPrimeMovement() == jumpMP ){
            if ( GetBFSecondaryMovementMode().isEmpty() ) {
                return (!GetJumpJets().IsUMU() ? "j":"u");
            } else {
                return "";
            }
        }else{
            return "";
        }
    }
    
    public int GetBFSecondaryMovement(){
        int baseMP = GetAdjustedWalkingMP(false, false);
        int walkMP = GetBFPrimeMovement();
        int jumpMP = GetAdjustedJumpingMP(false);
        if ( GetJumpBoosterMP() > jumpMP ) jumpMP = GetJumpBoosterMP();

        if ( jumpMP > 0 && walkMP != jumpMP ){
            if ( baseMP > jumpMP )
                return (int)(Math.round(jumpMP*0.66));
            else if ( baseMP == jumpMP && walkMP > jumpMP )
                return jumpMP;
            else if ( walkMP < jumpMP )
                return jumpMP;
            else
                return 0;
        }else
            return 0;
    }

    public String GetBFSecondaryMovementMode(){
        int walkMP = GetBFPrimeMovement();
        int jumpMP = GetAdjustedJumpingMP(false);
        if ( GetJumpBoosterMP() > jumpMP ) jumpMP = GetJumpBoosterMP();

        if ( jumpMP > 0 && walkMP != jumpMP )
            return (!GetJumpJets().IsUMU() ? "j":"u");
        else if ( walkMP < jumpMP )
            return (!GetJumpJets().IsUMU() ? "j":"u");
        else
            return "";
    }

    public int GetBFArmor() {

        MechArmor a = GetArmor();
        double armorpoints = a.GetArmorValue();

        if ( a.IsCommercial() ){
            armorpoints = (double) Math.floor(armorpoints / 2.0);
        }else if ( a.IsFerroLamellor() ){
            armorpoints = (double) Math.ceil(armorpoints * 1.2);
        }else if ( a.IsHardened() ){
            armorpoints = (double) Math.ceil(armorpoints * 2.0);
        }else if ( a.IsReactive() || a.IsReflective() ){
            armorpoints = (double) Math.ceil(armorpoints * 0.75f);
        }

        armorpoints += a.GetModularArmorValue();
        
        return (int) Math.round(armorpoints / 30);
    }

    public int GetBFStructure() {
        Engine e = GetEngine();
        int t = GetTonnage();
        int retval = e.GetBFStructure(t);
        if ( GetIntStruc().CritName().contains("Reinforced") )
            retval *=2;
        else if ( GetIntStruc().CritName().contains("Composite") )
            retval /= 2;

        return retval;
    }

    public int[] GetBFDamage( BattleForceStats bfs ) {
        int[] retval = {0,0,0,0,0};
        int CoolantPods = 0;

        // Loop through all weapons in non-core
        // and convert all weapon dmg
        ArrayList nc = GetLoadout().GetNonCore();
        BFData = new BattleForceData();

        BFData.AddNote("Weapon  :: Short / Medium / Long / Extreme / [Heat]" );
        for ( int i = 0; i < nc.size(); i++ ) {
            if ( nc.get(i) instanceof ifWeapon ) {
                double [] temp = BattleForceTools.GetDamage((ifWeapon)nc.get(i), (ifBattleforce)this);
                BFData.AddBase(temp);
                
                if ( BattleForceTools.isBFAutocannon((ifWeapon)nc.get(i)) )
                    BFData.AC.AddBase(temp);
                else if ( BattleForceTools.isBFLRM((ifWeapon)nc.get(i)) )
                    BFData.LRM.AddBase(temp);
                else if ( BattleForceTools.isBFSRM((ifWeapon)nc.get(i)) )
                    BFData.SRM.AddBase(temp);
                else if ( BattleForceTools.isBFSRT((ifWeapon)nc.get(i)) ||
                            BattleForceTools.isBFLRT((ifWeapon)nc.get(i)) )
                    BFData.TOR.AddBase(temp);
                else if ( BattleForceTools.isBFMML((ifWeapon)nc.get(i)) )
                {
                    BFData.SRM.AddBase(new double[]{temp[BFConstants.BF_SHORT], temp[BFConstants.BF_MEDIUM]/2.0, 0.0, 0.0, temp[BFConstants.BF_OV]});
                    BFData.LRM.AddBase(new double[]{0.0, temp[BFConstants.BF_MEDIUM]/2.0, temp[BFConstants.BF_LONG], 0.0, temp[BFConstants.BF_OV]} );
                }
                if ( BattleForceTools.isBFIF((ifWeapon)nc.get(i)) )
                    BFData.IF.AddBase(temp);
                if ( BattleForceTools.isBFFLK((ifWeapon)nc.get(i)) )
                    BFData.FLK.AddBase(temp);
                BFData.AddNote(nc.get(i).toString() + " :: " + temp[BFConstants.BF_SHORT] + "/" + temp[BFConstants.BF_MEDIUM] + "/" + temp[BFConstants.BF_LONG] + "/" + temp[BFConstants.BF_EXTREME] + " [" + temp[BFConstants.BF_OV] + "]" );
            } else if ( nc.get(i) instanceof Equipment ) {
                Equipment equip = ((Equipment) nc.get(i));
                if ( equip.CritName().contains("Coolant Pod")) {
                    CoolantPods++;
                }
            }
        }
        
        // Add in heat for movement
        if ( GetAdjustedJumpingMP(false) > 2 ) {
            BFData.AddHeat(GetJumpingHeat());
        } else {
            BFData.AddHeat(2);
        }

        // Subtract 4 because Joel says so...
        // and besides, Joel is awesome and we should trust him
        BFData.AddHeat(-4);

        // Also include Stealth heat, which is ALWAYS on in BF
        if ( GetArmor().IsStealth() ) {
            BFData.AddHeat(10);
        }

        BFData.SetHeat(this.GetHeatSinks().TotalDissipation() + CoolantPods);
        BFData.CheckSpecials();
        
        // Convert all damage to BF scale
        retval[BFConstants.BF_SHORT] = BFData.AdjBase.getBFShort(); //(int) Math.ceil(dmgShort / 10);
        retval[BFConstants.BF_MEDIUM] = BFData.AdjBase.getBFMedium(); //(int) Math.ceil(dmgMedium / 10);
        retval[BFConstants.BF_LONG] = BFData.AdjBase.getBFLong(); //(int) Math.ceil(dmgLong / 10);
        retval[BFConstants.BF_EXTREME] = 0;   // Mechs dont have extreme range ever

        // Add Special Abilities to BattleForceStats if applicable
        if ( BFData.AC.CheckSpecial() ) bfs.addAbility("AC " + BFData.AC.GetAbility() );
        if ( BFData.SRM.CheckSpecial() ) bfs.addAbility("SRM " + BFData.SRM.GetAbility() );
        if ( BFData.LRM.CheckSpecial() ) bfs.addAbility("LRM " + BFData.LRM.GetAbility() );
        if ( BFData.TOR.CheckSpecial() ) bfs.addAbility("TOR " + BFData.TOR.GetAbility() );
        if ( BFData.IF.getBFLong() > 0 )  bfs.addAbility("IF " + BFData.IF.getBFLong() );
        if ( BFData.FLK.getBaseMedium() > 5 ) bfs.addAbility("FLK " + BFData.FLK.GetAbility() );

        // Determine OverHeat
        BFData.AddNote("OverHeat Calculations");
        BFData.AddNote("Is Base Max Medium > 0 ? " + (BFData.BaseMaxMedium() > 0));
        if ( BFData.BaseMaxMedium() != 0 )
        {
            int DmgMedium = BFData.AdjBase.getBFMedium() + BFData.SRM.getBFMedium() + BFData.LRM.getBFMedium() + BFData.AC.getBFMedium();
            BFData.AddNote("Medium Damage = Adj Base (" + BFData.AdjBase.getBFMedium() + ") + "
                                            + "SRM (" + BFData.SRM.getBFMedium() + ") + "
                                            + "LRM (" + BFData.LRM.getBFMedium() + ") + "
                                            + "AC (" + BFData.AC.getBFMedium() + ") = " + DmgMedium);
            retval[BFConstants.BF_OV] = BFData.BaseMaxMedium() - DmgMedium;
            BFData.AddNote("Medium: " + BFData.BaseMaxMedium() + " - " + DmgMedium + " = " + (BFData.BaseMaxMedium()-DmgMedium));
            //System.out.println( BFData.BaseMaxMedium() + " - " + DmgMedium + " = " + (BFData.BaseMaxMedium()-DmgMedium));
        }
        else
        {
            int DmgShort = BFData.AdjBase.getBFShort() + BFData.SRM.getBFShort() + BFData.LRM.getBFShort() + BFData.AC.getBFShort();
            retval[BFConstants.BF_OV] = BFData.BaseMaxShort() - DmgShort;
            BFData.AddNote("Short: " + BFData.BaseMaxShort() + " - " + DmgShort + " = " + (BFData.BaseMaxShort()-DmgShort));
        }

        // Maximum OV value is 4, minimum is 0
        if (retval[BFConstants.BF_OV] > 4)
            retval[BFConstants.BF_OV] = 4;
        if (retval[BFConstants.BF_OV] < 0)
            retval[BFConstants.BF_OV] = 0;

        //System.out.println(BFData.toString());

        // Return final values
        return retval;
    }

    public ArrayList<String> GetBFAbilities() {
        ArrayList<String> retval = new ArrayList();

        // First search all equipment for BF Abilities
        ArrayList nc = GetLoadout().GetNonCore();
        boolean isENE = true,
                hasExplodable = false;
        int Taser = 0,
            RSD = 0;
        double MHQTons = 0;
        int Heat = 0;

        //TSM Check
        if ( GetPhysEnhance().IsTSM() )
            if ( this.IsIndustrialmech() ) {
                if ( !retval.contains("I-TSM") ) retval.add("I-TSM");
            } else {
                if ( !retval.contains("TSM") ) retval.add("TSM");
            }

        //Mimetic MechArmor System
        if ( HasVoidSig )
            if ( !retval.contains("MAS") ) retval.add("MAS");

        //Underwater Movement
        if ( GetJumpJets().IsUMU() ) {
            if ( !retval.contains("UMU") ) retval.add("UMU");
        }

        //Omni
        if ( IsOmnimech() )
            if ( !retval.contains("OMNI") ) retval.add("OMNI");

        //Stealth (also adds ECM)
        if ( (GetArmor().IsStealth()) || (this.HasChameleon) || this.HasNullSig ) {
            if ( !retval.contains("ECM") ) retval.add("ECM");
            if ( !retval.contains("STL") ) retval.add("STL");
        }

        //Command Console (affect Mobile Headquarters)
        if ( GetCockpit().CritName().contains("Command Console"))
            MHQTons += 1.0d;

        for ( int i = 0; i < nc.size(); i++ ) {
            abPlaceable item = (abPlaceable)nc.get(i);

            // Get the list of abilities from the equipment itself
            // handle special cases like C3
            String[] abilities = item.GetBattleForceAbilities();
            for ( String ability : abilities ) {

                if ( ability.equals("C3M") ) MHQTons += 5.0d;
                if ( ability.equals("C3I") ) MHQTons += 2.5d;
                if ( ability.equals("C3BM") ) MHQTons += 6.0d;
                if ( ability.equals("C3S") ) MHQTons += 1.0d;
                if ( ability.equals("MTAS") ) Taser += 1;
                if ( ability.equals("RSD") ) RSD += 1;
                if ( ability.equals("HT2") ) Heat += 2;
                if ( ability.equals("HT3") ) Heat += 3;
                if ( ability.equals("HT7") ) Heat += 7;

                if ( !retval.contains(ability) ) retval.add(ability);
            }

            // Check equipment for special abilities
            if ( item instanceof ifWeapon ) {
                // ENE for mechs without ammo dependant weapons
                if ( ((ifWeapon)item).GetWeaponClass() != ifWeapon.W_ENERGY &&
                     ((ifWeapon)item).GetWeaponClass() != ifWeapon.W_PHYSICAL) {
                    isENE = false;
                }

                // Does the mech carry an explodable weapon?
                if ( ((ifWeapon)nc.get(i)).IsExplosive() ) {
                    hasExplodable = true;
                }
            }
            if( item instanceof Ammunition ) {
                if( ((Ammunition) item).IsExplosive() ) {
                    hasExplodable = true;
                }
            }
        }

        // Remove heat abilities if present, will add proper values later
        retval.remove("HT2");
        retval.remove("HT3");
        retval.remove("HT7");

        // Now deal with all the funny stuff
        if( CurLoadout.HasCTCASE() || CurLoadout.HasLTCASE() || CurLoadout.HasRTCASE() ) {
            if ( !retval.contains( "CASE" ) ) retval.add( "CASE" );
        }
        if( CurLoadout.HasHDCASEII() || CurLoadout.HasCTCASEII() || CurLoadout.HasLTCASEII() || CurLoadout.HasRTCASEII() || CurLoadout.HasLACASEII() || CurLoadout.HasRACASEII() || CurLoadout.HasLLCASEII() || CurLoadout.HasRLCASEII() ) {
            if ( !retval.contains( "CASEII" ) ) retval.add( "CASEII" );
        }
        if ( isENE ) {
            retval.add("ENE");
        }
            if ( !isENE && ( CurLoadout.IsUsingClanCASE() && CurLoadout.CanUseClanCASE() ) )
            if ( !retval.contains( "CASE" ) ) retval.add("CASE");
        if ( Taser > 0 ) {
            retval.remove("MTAS");
            retval.add("MTAS" + Taser);
        }
        if ( MHQTons > 0 ) {
            retval.add("MHQ" + (int) MHQTons);
        }
        if ( RSD > 0 ) {
            retval.remove("RSD");
            retval.add("RSD" + RSD);
        }
        if ( !hasExplodable ) {
            //They don't have anything that blows up so remove any traces of CASE or CASEII
            retval.remove("CASE");
            retval.remove("CASEII");
        }
        if ( Heat > 10 )
            retval.add("HT2");
        else if ( Heat > 5 )
            retval.add("HT1");

        //Remove a - that is a result of the file needing data
        retval.remove("-");

        // Remove extra base LRM, SRM, TRO, and AC if included
        retval.remove("LRM");
        retval.remove("SRM");
        retval.remove("TOR");
        retval.remove("AC");
        retval.remove("IF");
        retval.remove("FLK");

        //ALL Mechs get SRCH (Industrials?)
        retval.add("SRCH");     //Searchlight
        if ( CurEngine.IsICE() || CurEngine.isFuelCell() ) {
            retval.add("EE");
        }
        if ( IsIndustrialmech() ) {
            if( HasEjectionSeat() ) {
                retval.add("ES");       //Ejection Seat
            }
            if( HasEnviroSealing() ) {
                retval.add("SEAL");     //Environmental Sealing
                if (CurEngine.IsFusion() || CurEngine.IsNuclear() || CurEngine.isFuelCell()) {
                    retval.add("SOA");      //Space Operations Adaptation
                }
            }
        } else {
            retval.add("ES");       //Ejection Seat
            retval.add("SEAL");     //Environmental Sealing
            retval.add("SOA");      //Space Operations Adaptation
        }

        return retval;
    }

    public String GetBFConversionStr( ) {
        String retval = "Weapon\t\t\tShort\tMedium\tLong\n\r";
        //TODO Add in conversion steps if possible
        return retval;
    }

    public BattleForceData getBFData() {
        return BFData;
    }

    public ifVisitor Lookup( String s ) {
        // returns a visitor from the lookup table based on the lookup string
        return (ifVisitor) Lookup.get( s );
    }

    private void BuildLookupTable() {
        // sets up the lookup hashtable with String keys and ifVisitor values
        Lookup.put( "Standard Armor", new VArmorSetStandard() );
        Lookup.put( "Ferro-Fibrous", new VArmorSetFF() );
        Lookup.put( "(IS) Ferro-Fibrous", new VArmorSetFF() );
        Lookup.put( "(CL) Ferro-Fibrous", new VArmorSetFF() );
        Lookup.put( "Stealth Armor", new VArmorSetStealth() );
        Lookup.put( "Light Ferro-Fibrous", new VArmorSetLightFF() );
        Lookup.put( "Heavy Ferro-Fibrous", new VArmorSetHeavyFF() );
        Lookup.put( "Ferro-Lamellor", new VArmorSetFL() );
        Lookup.put( "Hardened Armor", new VArmorSetHA() );
        Lookup.put( "Laser-Reflective", new VArmorSetLR() );
        Lookup.put( "(IS) Laser-Reflective", new VArmorSetLR() );
        Lookup.put( "(CL) Laser-Reflective", new VArmorSetLR() );
        Lookup.put( "Reactive Armor", new VArmorSetRE() );
        Lookup.put( "(IS) Reactive Armor", new VArmorSetRE() );
        Lookup.put( "(CL) Reactive Armor", new VArmorSetRE() );
        Lookup.put( "Industrial Armor", new VArmorSetIndustrial() );
        Lookup.put( "Commercial Armor", new VArmorSetCommercial() );
        Lookup.put( "Ablation Armor", new VArmorSetAB() );
        Lookup.put( "Heat-Dissipating Armor", new VArmorSetHD() );
        Lookup.put( "Impact-Resistant Armor", new VArmorSetIR());
        Lookup.put( "Ballistic-Reinforced Armor", new VArmorSetBR());
        Lookup.put( "Patchwork Armor", new VArmorSetPatchwork() );
        Lookup.put( "Standard Structure", new VChassisSetStandard() );
        Lookup.put( "Composite Structure", new VChassisSetComposite() );
        Lookup.put( "Endo-Steel", new VChassisSetEndoSteel() );
        Lookup.put( "(IS) Endo-Steel", new VChassisSetEndoSteel() );
        Lookup.put( "(CL) Endo-Steel", new VChassisSetEndoSteel() );
        Lookup.put( "Endo-Composite", new VChassisSetEndoComposite() );
        Lookup.put( "(IS) Endo-Composite", new VChassisSetEndoComposite() );
        Lookup.put( "(CL) Endo-Composite", new VChassisSetEndoComposite() );
        Lookup.put( "Reinforced Structure", new VChassisSetReinforced() );
        Lookup.put( "Industrial Structure", new VChassisSetIndustrial() );
        Lookup.put( "Standard Cockpit", new VCockpitSetStandard() );
        Lookup.put( "Industrial Cockpit", new VCockpitSetIndustrial() );
        Lookup.put( "Industrial w/ Adv. FC", new VCockpitSetIndustrialAFC() );
        Lookup.put( "Interface Cockpit", new VCockpitSetInterface() );
        Lookup.put( "Small Cockpit", new VCockpitSetSmall() );
        Lookup.put( "Torso-Mounted Cockpit", new VCockpitSetTorsoMount() );
        Lookup.put( "Robotic Cockpit", new VCockpitSetRobotic() );
        Lookup.put( "Fuel-Cell Engine", new VEngineSetFuelCell() );
        Lookup.put( "Fission Engine", new VEngineSetFission() );
        Lookup.put( "Fusion Engine", new VEngineSetFusion() );
        Lookup.put( "Primitive Fuel-Cell Engine", new VEngineSetPrimitiveFuelCell() );
        Lookup.put( "Primitive Fission Engine", new VEngineSetPrimitiveFission() );
        Lookup.put( "Primitive Fusion Engine", new VEngineSetPrimitiveFusion() );
        Lookup.put( "XL Engine", new VEngineSetFusionXL() );
        Lookup.put( "(IS) XL Engine", new VEngineSetFusionXL() );
        Lookup.put( "(CL) XL Engine", new VEngineSetFusionXL() );
        Lookup.put( "XXL Engine", new VEngineSetFusionXXL() );
        Lookup.put( "(IS) XXL Engine", new VEngineSetFusionXXL() );
        Lookup.put( "(CL) XXL Engine", new VEngineSetFusionXXL() );
        Lookup.put( "I.C.E. Engine", new VEngineSetICE() );
        Lookup.put( "Primitive I.C.E. Engine", new VEngineSetPrimitiveICE() );
        Lookup.put( "Compact Fusion Engine", new VEngineSetCompactFusion() );
        Lookup.put( "Light Fusion Engine", new VEngineSetLightFusion() );
        Lookup.put( "Standard Gyro", new VGyroSetStandard() );
        Lookup.put( "Heavy-Duty Gyro", new VGyroSetHD() );
        Lookup.put( "Extra-Light Gyro", new VGyroSetXL() );
        Lookup.put( "Compact Gyro", new VGyroSetCompact() );
        Lookup.put( "No Gyro", new VGyroSetNone() );
        Lookup.put( "No Enhancement", new VEnhanceSetNone() );
        Lookup.put( "MASC", new VEnhanceSetMASC() );
        Lookup.put( "(IS) MASC", new VEnhanceSetMASC() );
        Lookup.put( "(CL) MASC", new VEnhanceSetMASC() );
        Lookup.put( "TSM", new VEnhanceSetTSM() );
        Lookup.put( "Industrial TSM", new VEnhanceSetITSM() );
        Lookup.put( "Single Heat Sink", new VHeatSinkSetSingle() );
        Lookup.put( "Double Heat Sink", new VHeatSinkSetDouble() );
        Lookup.put( "(IS) Double Heat Sink", new VHeatSinkSetDouble() );
        Lookup.put( "(CL) Double Heat Sink", new VHeatSinkSetDouble() );
        Lookup.put( "Compact Heat Sink", new VHeatSinkSetCompact() );
        Lookup.put( "Laser Heat Sink", new VHeatSinkSetLaser() );
        Lookup.put( "Standard Jump Jet", new VJumpJetSetStandard() );
        Lookup.put( "Improved Jump Jet", new VJumpJetSetImproved() );
        Lookup.put( "Mech UMU", new VJumpJetSetUMU() );
        Lookup.put( "Primitive Armor", new VArmorSetPrimitive() );
        Lookup.put( "Primitive Structure", new VChassisSetPrimitive() );
        Lookup.put( "Primitive Industrial Structure", new VChassisSetPrimitiveIndustrial() );
        Lookup.put( "Primitive Cockpit", new VCockpitSetPrimitive() );
        Lookup.put( "Primitive Industrial Cockpit", new VCockpitSetPrimIndustrial() );
        Lookup.put( "Primitive Industrial w/ Adv. FC", new VCockpitSetPrimIndustrialAFC() );

        // now to fix all the visitors with counterparts to use Clan tech if needed
        ((ifVisitor) Lookup.get( "(CL) Ferro-Fibrous" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) Laser-Reflective" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) Reactive Armor" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) Endo-Steel" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) Endo-Composite" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) XL Engine" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) XXL Engine" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) MASC" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) Double Heat Sink" )).SetClan( true );
    }

    // toString
    @Override
    public String toString() {
        // return Name + " " + Model;
        return "test";
    }
}

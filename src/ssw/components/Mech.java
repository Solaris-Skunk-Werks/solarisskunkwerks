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

package ssw.components;

import ssw.battleforce.*;
import java.util.Hashtable;
import java.util.Vector;
import ssw.*;
import ssw.gui.frmMain;
import ssw.visitors.*;

public class Mech implements ifBattleforce {
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
                   Source = "",
                   Solaris7ID = "0",
                   Solaris7ImageID = "0",
                   SSWImage = Constants.NO_IMAGE;
    private int Era,
                Year,
                TechBase,
                RulesLevel,
                Tonnage = 20,
                WalkMP;
    private float JJMult,
                  MechMult;
    public final static float[] DefensiveFactor = { 1.0f, 1.0f, 1.1f, 1.1f, 1.2f, 1.2f,
        1.3f, 1.3f, 1.3f, 1.4f, 1.4f, 1.4f, 1.4f, 1.4f, 1.4f, 1.4f, 1.4f, 1.5f,
        1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.6f, 1.6f, 1.6f, 1.6f, 1.6f, 1.6f };
    private boolean Quad,
                    Omnimech,
                    Primitive = false,
                    IndustrialMech = false,
                    YearSpecified = false,
                    YearRestricted = false,
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
                    Changed = false;
    private Engine CurEngine = new Engine( this );
    private ifLoadout MainLoadout = new BipedLoadout( Constants.BASELOADOUT_NAME, this ),
                    CurLoadout = MainLoadout;
    private Vector Loadouts = new Vector(),
                   MechMods = new Vector();
    private Gyro CurGyro = new Gyro( this );
    private InternalStructure CurIntStruc = new InternalStructure( this );
    private Cockpit CurCockpit = new Cockpit( this );
    private PhysicalEnhancement CurPhysEnhance = new PhysicalEnhancement( this );
    private Armor CurArmor = new Armor( this );
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
    private Options options = new Options();
    private Hashtable Lookup = new Hashtable();
    private AvailableCode OmniAvailable = new AvailableCode( AvailableCode.TECH_BOTH );

    // Constructors
    public Mech() {
        Load();
    }

    public Mech( frmMain window ) {
        Load();
    }

    private void Load(){
        BuildLookupTable();

        // Set the names and years to blank so the user doesn't have to overtype
        Name = "";
        Era = AvailableCode.ERA_STAR_LEAGUE;
        TechBase = AvailableCode.TECH_INNER_SPHERE;
        RulesLevel = AvailableCode.RULES_TOURNAMENT;
        Year = 2750;
        YearRestricted = false;

        // Basic setup for the mech.  This is an arbitrary default chassis
        Quad = false;
        Omnimech = false;
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
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        NullSig = new MultiSlotSystem( this, "Null Signature System", "Null Signature System", "NullSignatureSystem", 0.0f, false, true, 1400000.0f, false, AC );
        NullSig.AddMechModifier( new MechModifier( 0, 0, 0, 0.0f, 0, 0, 10, 0.2f, 0.0f, 0.0f, 0.0f, true ) );
        NullSig.SetExclusions( new Exclusion( new String[] { "Targeting Computer", "Void Signature System", "Stealth Armor", "C3" }, "Null Signature System" ) );

        AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        AC.SetISCodes( 'E', 'F', 'X', 'X' );
        AC.SetISDates( 0, 0, false, 2630, 2790, 0, true, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Chameleon = new MultiSlotSystem( this, "Chameleon LPS", "Chameleon LPS", "ChameleonLightPolarizationField", 0.0f, true, true, 600000.0f, false, AC );
        Chameleon.AddMechModifier( new MechModifier( 0, 0, 0, 0.0f, 0, 0, 6, 0.2f, 0.0f, 0.0f, 0.0f, true ) );
        Chameleon.SetExclusions( new Exclusion( new String[] { "Void Signature System", "Stealth Armor" }, "Chameleon LPS" ) );

        AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        AC.SetISCodes( 'E', 'X', 'X', 'F' );
        AC.SetISDates( 3051, 3053, true, 3053, 0, 0, false, false );
        AC.SetISFactions( "FS", "FS", "", "" );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        BlueShield = new MultiSlotSystem( this, "Blue Shield PFD", "Blue Shield PFD", "BlueShieldPFD", 3.0f, false, true, 1000000.0f, false, AC );
        BlueShield.AddMechModifier( new MechModifier( 0, 0, 0, 0.0f, 0, 0, 0, 0.0f, 0.0f, 0.2f, 0.2f, true ) );

        AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
        AC.SetISCodes( 'E', 'X', 'X', 'E' );
        AC.SetISDates( 3060, 3070, true, 3070, 0, 0, false, false );
        AC.SetISFactions( "WB", "WB", "", "" );
        AC.SetRulesLevels( AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        VoidSig = new MultiSlotSystem( this, "Void Signature System", "Void Signature System", "VoidSignatureSystem", 0.0f, false, true, 2000000.0f, false, AC );
        VoidSig.AddMechModifier( new MechModifier( 0, 0, 0, 0.0f, 0, 0, 10, 0.3f, 0.0f, 0.0f, 0.0f, true ) );
        VoidSig.SetExclusions( new Exclusion( new String[] { "Targeting Computer", "Null Signature System", "Stealth Armor", "C3", "Chameleon LPS" }, "Void Signature System" ) );

        AC = new AvailableCode( AvailableCode.TECH_BOTH );
        AC.SetISCodes( 'C', 'C', 'C', 'C' );
        AC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetISFactions( "", "", "PS", "" );
        AC.SetCLCodes( 'C', 'X', 'C', 'C' );
        AC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetCLFactions( "", "", "PS", "" );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        EnviroSealing = new MultiSlotSystem( this, "Environmental Sealing", "Environmental Sealing", "Environmental Sealing", 0.1f, false, false, 225.0f, true, AC );
        EnviroSealing.SetWeightBasedOnMechTonnage( true );

        AC = new AvailableCode( AvailableCode.TECH_BOTH );
        AC.SetISCodes( 'B', 'D', 'E', 'F' );
        AC.SetISDates( 0, 0, false, 2445, 0, 0, false, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetCLCodes( 'B', 'X', 'D', 'F' );
        AC.SetCLDates( 0, 0, false, 2445, 0, 0, false, false );
        AC.SetCLFactions( "", "", "TH", "" );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        EjectionSeat = new SimplePlaceable( "Ejection Seat", "EjectionSeat", 1, true, AC );
        EjectionSeat.SetTonnage( 0.5f );
        EjectionSeat.SetCost( 25000.0f );

        AC = new AvailableCode( AvailableCode.TECH_BOTH );
        AC.SetISCodes( 'C', 'D', 'E', 'E' );
        AC.SetISDates( 0, 0, false, 2400, 0, 0, false, false );
        AC.SetISFactions( "", "", "DC", "" );
        AC.SetCLCodes( 'C', 'X', 'D', 'E' );
        AC.SetCLDates( 0, 0, false, 2400, 0, 0, false, false );
        AC.SetCLFactions( "", "", "DC", "" );
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Tracks = new Tracks( this, AC );

        AC = new AvailableCode( AvailableCode.TECH_BOTH );
        AC.SetISCodes( 'D', 'C', 'F', 'E' );
        AC.SetISDates( 0, 0, false, 2631, 2850, 3030, true, true );
        AC.SetISFactions( "", "", "TH", "??" );
        AC.SetCLCodes( 'D', 'X', 'B', 'B' );
        AC.SetCLDates( 0, 0, false, 2631, 0, 0, false, false );
        AC.SetCLFactions( "", "", "TH", "" );
        AC.SetRulesLevels( AvailableCode.RULES_ADVANCED, AvailableCode.RULES_ADVANCED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        CommandConsole = new SimplePlaceable( "Command Console", "CommandConsole", 1, true, AC );
        CommandConsole.SetTonnage( 3.0f );
        CommandConsole.SetCost( 500000.0f );
    }

    public void Recalculate() {
        // recalculates the Mech Mult and the Jump Jet Mult
        if( Tonnage < 60 ) {
            JJMult = 0.5f;
        } else if( Tonnage > 55 && Tonnage < 90 ) {
            JJMult = 1.0f;
        } else  {
            JJMult = 2.0f;
        }
        if (IndustrialMech == false)
            MechMult = 1.0f + ( Tonnage * 0.01f );
        else
            MechMult = 1.0f + ( Tonnage * 0.0025f );
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
            MaxWalk = (int) Math.floor( 400 / Tonnage / 1.2f );
        } else {
            MaxWalk = (int) Math.floor( 400 / Tonnage );
        }
        if( WalkMP < 1 ) { WalkMP = 1; }
        if( WalkMP > MaxWalk ) { WalkMP = MaxWalk; }
        if( Primitive ) {
            // have to round up to the nearest multiple of 5
            CurEngine.SetRating( (int) ( Math.floor( ( ( WalkMP * Tonnage * 1.2f ) + 4.5f ) / 5 ) * 5 ) );
        } else {
            CurEngine.SetRating( WalkMP * Tonnage );
        }

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
        Era = e;
        SetChanged( true );
    }

    public void SetRulesLevel( int r ) {
        if( Omnimech ) {
            CurLoadout.SetRulesLevel( r );
        } else {
            RulesLevel = r;
            MainLoadout.SetRulesLevel( r );
        }
        SetChanged( true );
    }

    public void SetYear( int y, boolean specified ) {
        Year = y;
        YearSpecified = specified;
        SetChanged( true );
    }

    public void SetYearRestricted( boolean y ) {
        YearRestricted = y;
        SetChanged( true );
    }

    public boolean IsYearRestricted() {
        return YearRestricted;
    }

    public void SetTechBase( int t ) {
        if( Omnimech ) {
            CurLoadout.SetTechBase( t );
        } else {
            TechBase = t;
            MainLoadout.SetTechBase( t );
        }
        SetChanged( true );
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

        SetChanged( true );
    }

    public void SetMixed() {
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

        SetChanged( true );
    }

    public int GetTechBase() {
        return TechBase;
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
        String Jumps = GetJumpJets().GetLookupName();
        String HeatSinks = GetHeatSinks().GetLookupName();

        // remember how many heat sinks and jump jets we had
        int NumJJ = GetJumpJets().GetNumJJ();
        int NumHS = GetHeatSinks().GetNumHS() - CurEngine.FreeHeatSinks();

        // Get a new Biped Loadout and load up the queue
        ifLoadout l = new BipedLoadout( Constants.BASELOADOUT_NAME, this );
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

        // remove the any existing  physical weapons, and industrial equipment
        Vector v = CurLoadout.GetNonCore();
        for( int i = v.size() - 1; i >= 0; i-- ) {
            abPlaceable p = (abPlaceable) v.get( i );
            if( p instanceof PhysicalWeapon ) {
                CurLoadout.Remove(p);
            }
            else if (p instanceof IndustrialEquipment){
                CurLoadout.Remove(p);
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
        } catch( Exception e ) {
            // unhandled at this time, print an error out
            System.err.println( "CASE system not reinstalled:\n" + e.getMessage() );
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
        String Jumps = GetJumpJets().GetLookupName();
        String HeatSinks = GetHeatSinks().GetLookupName();

        // remember how many heat sinks and jump jets we had
        int NumJJ = GetJumpJets().GetNumJJ();
        int NumHS = GetHeatSinks().GetNumHS() - CurEngine.FreeHeatSinks();

        // Get a new Quad Loadout and load up the queue
        ifLoadout l = new QuadLoadout( Constants.BASELOADOUT_NAME, this );
        CurLoadout.Transfer(l);
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
        Vector v = CurLoadout.GetNonCore();
        for( int i = v.size() - 1; i >= 0; i-- ) {
            abPlaceable p = (abPlaceable) v.get( i );
            if( p instanceof PhysicalWeapon ) {
                CurLoadout.Remove(p);
            }
            else if (p instanceof IndustrialEquipment)
            {
                CurLoadout.Remove(p);
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
        } catch( Exception e ) {
            // unhandled at this time, print an error out
            System.err.println( "CASE system not reinstalled:\n" + e.getMessage() );
        }

        SetChanged( true );
    }

    public boolean IsQuad() {
        return Quad;
    }

    public void SetIndustrialmech() {
        // do all the neccesary things to change over to an IndustrialMech
        IndustrialMech = true;
        switch( TechBase ) {
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
        switch( TechBase ) {
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
        Vector v = MainLoadout.GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            if( v.get( i ) instanceof PhysicalWeapon ) {
                if( MainLoadout.Find( (abPlaceable) v.get( i ) ) == Constants.LOC_LA ) {
                    left = false;
                }
                if( MainLoadout.Find( (abPlaceable) v.get( i ) ) == Constants.LOC_RA ) {
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
        ifLoadout l = MainLoadout.Clone();
        l.SetName( name );
        Loadouts.add( l );
        CurLoadout = l;

        SetChanged( true );
    }

    public void AddLoadout( String Name ) throws Exception {
        // Adds a new loadout with the given name to the vector, cloned from the
        // base loadout

        // does the name match the Base Loadout's name?
        if( MainLoadout.GetName().equals( Name ) ) {
            throw new Exception( "\"" + Name + "\" is reserved for the base loadout and cannot be used\nfor a new loadout.  Please choose another name." );
        }

        // see if another loadout has the same name
        for( int i = 0; i < Loadouts.size(); i++ ) {
            if( ((ifLoadout) Loadouts.get( i )).GetName().equals( Name ) ) {
                throw new Exception( "Could not add the new loadout because\nthe name given matches an existing loadout." );
            }
        }

        ifLoadout l = MainLoadout.Clone();
        l.SetName( Name );
        Loadouts.add( l );
        CurLoadout = l;

        SetChanged( true );
    }

    public void RemoveLoadout( String Name ) {
        // removes the given loadout from the loadout vector.  if the vector is
        // empty (non-omnimech) nothing is done.
        for( int i = 0; i < Loadouts.size(); i++ ) {
            if( ((ifLoadout) Loadouts.get( i )).GetName().equals( Name ) ) {
                // remove it
                Loadouts.remove( i );
                break;
            }
        }

        // now set the current loadout to the first
        if( Loadouts.size() > 0 ) {
            CurLoadout = (ifLoadout) Loadouts.firstElement();
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
            if( ((ifLoadout) Loadouts.get( i )).GetName().equals( Name ) ) {
                CurLoadout = (ifLoadout) Loadouts.get( i );
                return;
            }
        }

        // if we got here, there was a problem.  set the loadout to the base
        if( Loadouts.size() > 0 ) {
            CurLoadout = (ifLoadout) Loadouts.firstElement();
        } else {
            CurLoadout = MainLoadout;
        }
    }

    public Vector GetLoadouts() {
        return Loadouts;
    }

    public ifLoadout GetBaseLoadout() {
        return MainLoadout;
    }

    public boolean IsOmnimech() {
        return Omnimech;
    }

    public boolean IsIndustrialmech() {
        return IndustrialMech;
    }

    public boolean UsingTC() {
        return CurLoadout.UsingTC();
    }

    public String GetName() {
        return Name;
    }

    public String GetModel() {
        return Model;
    }

    public int GetEra() {
        return Era;
    }

    public int GetRulesLevel() {
        if( Omnimech ) {
            return CurLoadout.GetRulesLevel();
        } else {
            return RulesLevel;
        }
    }

    public int GetBaseRulesLevel() {
        return RulesLevel;
    }

    public String GetFullName() {
        return String.format("%1$s %2$s %3$s", GetName(), GetModel(), GetLoadout().GetName()).replace(" " + Constants.BASELOADOUT_NAME, "").replace("  " , " ");
    }

    public int GetDeprecatedLevel() {
        // returns the mech's "level" according to the older rules
        // this is used by Solaris7.com
        if( GetRulesLevel() >= AvailableCode.RULES_ADVANCED ) {
            return 3;
        } else {
            if( TechBase == AvailableCode.TECH_CLAN ) {
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
                if( TechBase == AvailableCode.TECH_CLAN ) {
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
        return Year;
    }

    public boolean YearWasSpecified() {
        return YearSpecified;
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

    public void SetEngineRating( int rate ) {
        if( CurEngine.CanSupportRating( rate ) ) {
            CurEngine.SetRating( rate );
            WalkMP = (int) rate / Tonnage;
        } else {
            SetWalkMP( 1 );
        }

        SetChanged( true );
    }

    public int GetWalkingMP() {
        return WalkMP;
    }

    public int GetAdjustedWalkingMP( boolean BV, boolean MASCTSM ) {
        int retval = WalkMP;
        retval += GetTotalModifiers( BV, MASCTSM ).WalkingAdder();
        return retval;
    }

    public void SetWalkMP( int mp ) {
        int MaxWalk = (int) Math.floor( 400 / Tonnage );
        if( mp > MaxWalk ) { mp = MaxWalk; }
        if( mp < 1 ) { mp = 1; }
        WalkMP = mp;
        CurEngine.SetRating( WalkMP * Tonnage );

        SetChanged( true );
    }

    public int GetRunningMP() {
        return (int) Math.floor( GetWalkingMP() * 1.5f + 0.5f );
    }

    public int GetAdjustedRunningMP( boolean BV, boolean MASCTSM ) {
        // this had to become more complicated because of the peculiar
        // idiosyncracies of the BV system.  Stupid.
        MechModifier m = GetTotalModifiers( BV, MASCTSM );
        int WalkValue = GetAdjustedWalkingMP( BV, MASCTSM );
        float Multiplier = 1.5f + m.RunningMultiplier();
        return (int) Math.floor( WalkValue * Multiplier + 0.5f ) + m.RunningAdder();
    }

    public int GetAdjustedJumpingMP( boolean BV ) {
        // Large Shields restrict jumping ability but do affect BV movement modifiers
        if ( ! BV && ! GetTotalModifiers( BV, true ).CanJump() ) {
            return 0;
        }
        else {
            int retval = CurLoadout.GetJumpJets().GetNumJJ();
            retval += GetTotalModifiers( BV, true ).JumpingAdder();
            return retval;
        }
    }

    public float GetJJMult() {
        return JJMult;
    }

    public float GetCurrentTons() {
        // returns the current total tonnage of the mech
        float result = 0.0f;
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
        if( CurLoadout.UsingTC() ) { result += GetTC().GetTonnage(); }
        if( ! CurEngine.IsNuclear() ) { result += CurLoadout.GetPowerAmplifier().GetTonnage(); }
        if( HasBlueShield ) { result += BlueShield.GetTonnage(); }
        if( CurLoadout.HasSupercharger() ) { result += CurLoadout.GetSupercharger().GetTonnage(); }
        if( HasEnviroSealing ) { result += EnviroSealing.GetTonnage(); }
        if( HasEjectionSeat ) { result += EjectionSeat.GetTonnage(); }
        if( HasTracks) { result += Tracks.GetTonnage(); }
        if( Quad ) {
            if( HasLegAES ) { result += RLAES.GetTonnage() * 4.0f; }
        } else {
            if( HasRAAES ) { result += CurRAAES.GetTonnage(); }
            if( HasLAAES ) { result += CurLAAES.GetTonnage(); }
            if( HasLegAES ) { result += RLAES.GetTonnage() * 2.0f; }
        }

        Vector v = CurLoadout.GetNonCore();
        if( v.size() > 0 ) {
            for( int i = 0; i < v.size(); i++ ) {
                result += ((abPlaceable) v.get(i)).GetTonnage();
            }
        }
        return result;
    }

    public float GetCurrentDryTons() {
        // returns the tonnage without ammunition
        // returns the current total tonnage of the mech
        float result = 0.0f;
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
        if( CurLoadout.UsingTC() ) { result += GetTC().GetTonnage(); }
        if( ! CurEngine.IsNuclear() ) { result += CurLoadout.GetPowerAmplifier().GetTonnage(); }
        if( HasBlueShield ) { result += BlueShield.GetTonnage(); }
        if( CurLoadout.HasSupercharger() ) { result += CurLoadout.GetSupercharger().GetTonnage(); }
        if( HasEnviroSealing ) { result += EnviroSealing.GetTonnage(); }
        if( HasEjectionSeat ) { result += EjectionSeat.GetTonnage(); }
        if( Quad ) {
            if( HasLegAES ) { result += RLAES.GetTonnage() * 4.0f; }
        } else {
            if( HasRAAES ) { result += CurRAAES.GetTonnage(); }
            if( HasLAAES ) { result += CurLAAES.GetTonnage(); }
            if( HasLegAES ) { result += RLAES.GetTonnage() * 2.0f; }
        }

        Vector v = CurLoadout.GetNonCore();
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
        result += GetTotalModifiers( false, true ).HeatAdder();
        return result;
    }

    public int GetMovementHeat() {
        int walk = CurEngine.MaxMovementHeat();
        int jump = 0;
        int minjumpheat = 3 * CurEngine.JumpingHeatMultiplier();
        float heatperjj = 0.0f;

        if( GetJumpJets().IsImproved() ) {
            heatperjj = 0.5f * CurEngine.JumpingHeatMultiplier();
        } else {
            heatperjj = 1.0f * CurEngine.JumpingHeatMultiplier();
        }

        if( GetJumpJets().GetNumJJ() > 0 ) {
            jump = (int) ( GetJumpJets().GetNumJJ() * heatperjj + 0.51f );
            if( jump < minjumpheat ) { jump = minjumpheat; }
        }

        if( options.Heat_RemoveJumps ) {
            if( options.Heat_RemoveMovement ) {
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
        float heatperjj = 0.0f;

        if( GetJumpJets().IsImproved() ) {
            heatperjj = 0.5f * CurEngine.JumpingHeatMultiplier();
        } else {
            heatperjj = 1.0f * CurEngine.JumpingHeatMultiplier();
        }

        if( GetJumpJets().GetNumJJ() > 0 ) {
            jump = (int) ( GetJumpJets().GetNumJJ() * heatperjj + 0.51f );
            if( jump < minjumpheat ) { jump = minjumpheat; }
        }

        if( jump > walk ) {
            return jump;
        } else {
            return walk;
        }
    }

    public int GetWeaponHeat() {
        // returns the heat generated by weaponry and equipment that are not 
        // core components
        int result = 0;
        Vector v = CurLoadout.GetNonCore();
        if( v.size() <= 0 ) {
            return result;
        }

        abPlaceable a;
        boolean UseOS = options.Heat_RemoveOSWeapons;
        boolean UseRear = options.Heat_RemoveRearWeapons;
        boolean FullRate = options.Heat_UAC_RAC_FullRate;
        for( int i = 0; i < v.size(); i++ ) {
            a = (abPlaceable) v.get( i );
            if( a instanceof ifWeapon ) {
                boolean OS = ((ifWeapon) a).IsOneShot();
                boolean Rear = a.IsMountedRear();
                if( UseOS || UseRear ) {
                    if( UseOS ) {
                        if( UseRear ) {
                            if( ! OS &! Rear ) {
                                result += ((ifWeapon) a).GetHeat();
                            }
                        } else {
                            if( ! OS ) {
                                result += ((ifWeapon) a).GetHeat();
                            }
                        }
                    } else if( UseRear ) {
                        if( UseOS ) {
                            if( ! OS &! Rear ) {
                                result += ((ifWeapon) a).GetHeat();
                            }
                        } else {
                            if( ! Rear ) {
                                result += ((ifWeapon) a).GetHeat();
                            }
                        }
                    }
                } else {
                    if( FullRate ) {
                        if( a instanceof RangedWeapon ) {
                            if( ((RangedWeapon) a).IsUltra() ) {
                                result += ((ifWeapon) a).GetHeat() * 2;
                            } else if( ((RangedWeapon) a).IsRotary() ) {
                                result += ((ifWeapon) a).GetHeat() * 6;
                            } else {
                                result += ((ifWeapon) a).GetHeat();
                            }
                        } else {
                            result += ((ifWeapon) a).GetHeat();
                        }
                    } else {
                        result += ((ifWeapon) a).GetHeat();
                    }
                }
            } else if( a instanceof Equipment ) {
                if( ! options.Heat_RemoveEquipment ) {
                    result += ((Equipment) a).GetHeat();
                }
            }
        }

        return result;
    }

    public int GetBVWeaponHeat() {
        // this returns the heat generated by weapons for BV purposes as the
        // normal method is governed by user preferences
        int result = 0;
        Vector v = CurLoadout.GetNonCore();
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
        float tempmod = 0.0f;
        tempmod += CurCockpit.BVMod() * Math.floor( ( GetDefensiveBV() + GetOffensiveBV() + 0.5f ) ) - Math.floor( ( GetDefensiveBV() + GetOffensiveBV() + 0.5f ) );
        return (int) Math.floor( ( GetDefensiveBV() + GetOffensiveBV() + tempmod + 0.5f ) );
    }

    public float GetDefensiveBV() {
        // modify the result by the defensive factor and send it out
        return GetUnmodifiedDefensiveBV() * GetDefensiveFactor();
    }

    public float GetUnmodifiedDefensiveBV() {
        // returns the defensive battle value of the mech
        float defresult = 0.0f;

        // defensive battle value calculations start here
        defresult += CurIntStruc.GetDefensiveBV();
        defresult += CurGyro.GetDefensiveBV();
        defresult += CurArmor.GetDefensiveBV();
        defresult += GetDefensiveEquipBV();
        defresult += GetExplosiveAmmoPenalty();
        defresult += GetExplosiveWeaponPenalty();
        if( defresult < 1.0f ) {
            defresult = 1.0f;
        }

        // now get the defensive BV for any armored components that weren't
        // already covered.
        if( RulesLevel == AvailableCode.RULES_EXPERIMENTAL && Era == AvailableCode.ERA_CLAN_INVASION ) {
            defresult += CurEngine.GetDefensiveBV();
            defresult += CurCockpit.GetDefensiveBV();
        }
        return defresult;
    }

    public float GetDefensiveEquipBV() {
        // return the BV of all defensive equipment
        float result = 0.0f;
        Vector v = CurLoadout.GetNonCore();

        for( int i = 0; i < v.size(); i++ ) {
            result += ((abPlaceable) v.get( i )).GetDefensiveBV();
        }
        if( UsingTC() ) {
            result += GetTC().GetDefensiveBV();
        }
        return result;
    }

    public float GetExplosiveAmmoPenalty() {
        float result = 0.0f;
        Vector v = CurLoadout.GetNonCore();
        abPlaceable p;

        for( int i = 0; i < v.size(); i++ ) {
            p = (abPlaceable) v.get( i );
            if( p instanceof Ammunition ) {
                if( ((Ammunition) p).IsExplosive() ) {
                    if( CurEngine.IsISXL() ) {
                        switch( CurLoadout.Find( p ) ) {
                            case 0:
                                if( ! CurLoadout.HasHDCASEII() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 1:
                                if( ! CurLoadout.HasCTCASEII() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 2:
                                if( ! CurLoadout.HasLTCASEII() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 3:
                                if( ! CurLoadout.HasRTCASEII() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 4:
                                if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 5:
                                if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 6:
                                if( ! CurLoadout.HasLLCASEII() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 7:
                                if( ! CurLoadout.HasRLCASEII() ) {
                                    result -= 15.0f;
                                }
                                break;
                        }
                    } else {
                        switch( CurLoadout.Find( p ) ) {
                            case 0:
                                if( ! CurLoadout.HasHDCASEII() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 1:
                                if( ! CurLoadout.HasCTCASEII() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 2:
                                if( ! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 3:
                                if( ! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 4:
                                if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 5:
                                if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 6:
                                if( ! CurLoadout.HasLLCASEII() ) {
                                    result -= 15.0f;
                                }
                                break;
                            case 7:
                                if( ! CurLoadout.HasRLCASEII() ) {
                                    result -= 15.0f;
                                }
                                break;
                        }
                    }
                }
            }
        }

/*
        if( CurLoadout.UsingClanCASE() ) {
            for( int i = 0; i < v.size(); i++ ) {
                p = (abPlaceable) v.get( i );
                if( p instanceof Ammunition ) {
                    if( ((Ammunition) p).IsExplosive() ) {
                        if( CurEngine.IsISXL() ) {
                            switch( CurLoadout.Find( p ) ) {
                                case 0:
                                    if( ! CurLoadout.HasHDCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 1:
                                    if( ! CurLoadout.HasCTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 2:
                                    if( ! CurLoadout.HasLTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 3:
                                    if( ! CurLoadout.HasRTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 4:
                                    if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 5:
                                    
                                    if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 6:
                                    if( ! CurLoadout.HasLLCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 7:
                                    if( ! CurLoadout.HasRLCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                            }
                        } else {
                            switch( CurLoadout.Find( p ) ) {
                                case 0:
                                    if( ! CurLoadout.HasHDCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 1:
                                    if( ! CurLoadout.HasCTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 6: 
                                    if( ! CurLoadout.HasLLCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 7:
                                    if( ! CurLoadout.HasRLCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        } else {
            for( int i = 0; i < v.size(); i++ ) {
                p = (abPlaceable) v.get( i );
                if( p instanceof Ammunition ) {
                    if( ((Ammunition) p).IsExplosive() ) {
                        if( CurEngine.IsISXL() ) {
                            switch( CurLoadout.Find( p ) ) {
                                case 0:
                                    if( ! CurLoadout.HasHDCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 1:
                                    if( ! CurLoadout.HasCTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 2:
                                    if( ! CurLoadout.HasLTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 3:
                                    if( ! CurLoadout.HasRTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 4:
                                    if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 5:
                                    if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 6:
                                    if( ! CurLoadout.HasLLCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 7:
                                    if( ! CurLoadout.HasRLCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                            }
                        } else {
                            switch( CurLoadout.Find( p ) ) {
                                case 0:
                                    if( ! CurLoadout.HasHDCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 1:
                                    if( ! CurLoadout.HasCTCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 2:
                                    if( ! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 3:
                                    if( ! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 4:
                                    if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 5:
                                    if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 6:
                                    if( ! CurLoadout.HasLLCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                                case 7:
                                    if( ! CurLoadout.HasRLCASEII() ) {
                                        result -= 15.0f;
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        }
*/
        return result;
    }

    public float GetExplosiveWeaponPenalty() {
        float result = 0.0f;
        Vector v = CurLoadout.GetNonCore();
        abPlaceable p;
        boolean Explode;

        for( int i = 0; i < v.size(); i++ ) {
            p = (abPlaceable) v.get( i );
            Explode = false;
            if( p instanceof ifWeapon ) { Explode = ((ifWeapon) p).IsExplosive(); }
            if( p instanceof Equipment ) { Explode = ((Equipment) p).IsExplosive(); }
            if( Explode ) {
                if( CurEngine.IsISXL() ) {
                    switch( CurLoadout.Find( p ) ) {
                        case 0:
                            if( ! CurLoadout.HasHDCASEII() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 1:
                            if( ! CurLoadout.HasCTCASEII() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 2:
                            if( ! CurLoadout.HasLTCASEII() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 3:
                            if( ! CurLoadout.HasRTCASEII() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 4:
                            if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 5:
                            if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 6:
                            if( ! CurLoadout.HasLLCASEII() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 7:
                            if( ! CurLoadout.HasRLCASEII() ) {
                                result -= p.NumCrits();
                            }
                            break;
                    }
                } else {
                    switch( CurLoadout.Find( p ) ) {
                        case 0:
                            if( ! CurLoadout.HasHDCASEII() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 1:
                            if( ! CurLoadout.HasCTCASEII() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 2:
                            if( ! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 3:
                            if( ! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 4:
                            if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 5:
                            if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() &! CurLoadout.IsUsingClanCASE() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 6:
                            if( ! CurLoadout.HasLLCASEII() ) {
                                result -= p.NumCrits();
                            }
                            break;
                        case 7:
                            if( ! CurLoadout.HasRLCASEII() ) {
                                result -= p.NumCrits();
                            }
                            break;
                    }
                }
            }
        }
/*
        if( CurLoadout.CanUseClanCASE() ) {
            for( int i = 0; i < v.size(); i++ ) {
                p = (abPlaceable) v.get( i );
                Explode = false;
                if( p instanceof ifWeapon ) { Explode = ((ifWeapon) p).IsExplosive(); }
                if( p instanceof Equipment ) { Explode = ((Equipment) p).IsExplosive(); }
                if( Explode ) {
                    if( CurEngine.IsISXL() ) {
                        switch( CurLoadout.Find( p ) ) {
                            case 0:
                                if( ! CurLoadout.HasHDCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 1:
                                if( ! CurLoadout.HasCTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 2:
                                if( ! CurLoadout.HasLTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 3:
                                if( ! CurLoadout.HasRTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 4:
                                if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 5:
                                if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 6:
                                if( ! CurLoadout.HasLLCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 7:
                                if( ! CurLoadout.HasRLCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                        }
                    } else {
                        switch( CurLoadout.Find( p ) ) {
                            case 0:
                                if( ! CurLoadout.HasHDCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 1:
                                if( ! CurLoadout.HasCTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 6: 
                                if( ! CurLoadout.HasLLCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 7:
                                if( ! CurLoadout.HasRLCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                        }
                    }
                }
            }
        } else {
            for( int i = 0; i < v.size(); i++ ) {
                p = (abPlaceable) v.get( i );
                Explode = false;
                if( p instanceof ifWeapon ) { Explode = ((ifWeapon) p).IsExplosive(); }
                if( p instanceof Equipment ) { Explode = ((Equipment) p).IsExplosive(); }
                if( Explode ) {
                    if( CurEngine.IsISXL() ) {
                        switch( CurLoadout.Find( p ) ) {
                            case 0:
                                if( ! CurLoadout.HasHDCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 1:
                                if( ! CurLoadout.HasCTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 2:
                                if( ! CurLoadout.HasLTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 3:
                                if( ! CurLoadout.HasRTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 4:
                                if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 5:
                                if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 6:
                                if( ! CurLoadout.HasLLCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 7:
                                if( ! CurLoadout.HasRLCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                        }
                    } else {
                        switch( CurLoadout.Find( p ) ) {
                            case 0:
                                if( ! CurLoadout.HasHDCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 1:
                                if( ! CurLoadout.HasCTCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 2:
                                if( ! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 3:
                                if( ! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 4:
                                if( ! CurLoadout.HasLACASEII() &! CurLoadout.HasLTCASEII() &! CurLoadout.HasLTCASE() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 5:
                                if( ! CurLoadout.HasRACASEII() &! CurLoadout.HasRTCASEII() &! CurLoadout.HasRTCASE() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 6:
                                if( ! CurLoadout.HasLLCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                            case 7:
                                if( ! CurLoadout.HasRLCASEII() ) {
                                    result -= p.NumCrits();
                                }
                                break;
                        }
                    }
                }
            }
        }
*/
        return result;
    }

    public float GetDefensiveFactor() {
        // returns the defensive factor for this mech based on it's highest
        // target number for speed.

        // subtract one since we're indexing an array
        int RunMP = GetAdjustedRunningMP( true, true ) - 1;
        int JumpMP = 0;

        // this is a safeguard for using MASC on an incredibly speedy chassis
        // there is currently no way to get a bonus higher anyway.
        if( RunMP > 29 ) { RunMP = 29; }
        // safeguard for low walk mp (Modular Armor, for instance)
        if( RunMP < 0 ) { RunMP = 0; }

        // Get the defensive factors for jumping and running movement
        float ground = DefensiveFactor[RunMP];
        float jump = 0.0f;
        if( GetJumpJets().GetNumJJ() > 0 ) {
            JumpMP = GetAdjustedJumpingMP( true ) - 1;
                jump = DefensiveFactor[JumpMP] + 0.1f;
        }

        float retval = 0.0f;
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

    public float GetOffensiveBV() {
        // returns the offensive battle value of the mech
        return GetUnmodifiedOffensiveBV() * GetOffensiveFactor();
    }

    public float GetUnmodifiedOffensiveBV() {
        float offresult = 0.0f;

        offresult += GetHeatAdjustedWeaponBV();
        offresult += GetNonHeatEquipBV();
        offresult += GetExcessiveAmmoPenalty();
        offresult += GetTonnageBV();
        return offresult;
    }

    public float GetHeatAdjustedWeaponBV() {
        Vector v = CurLoadout.GetNonCore(), wep = new Vector();
        float result = 0.0f, foreBV = 0.0f, rearBV = 0.0f;
        boolean UseRear = false, TC = UsingTC(), UseAESMod = false;
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
        int heff = 6 + GetHeatSinks().TotalDissipation() - GetBVMovementHeat();
        int wheat = GetBVWeaponHeat();

        if( GetRulesLevel() == AvailableCode.RULES_EXPERIMENTAL ) {
            // check for coolant pods
            int NumHS = GetHeatSinks().GetNumHS(), MaxHSBonus = NumHS * 2, NumPods = 0;
            for( int i = 0; i < v.size(); i++ ) {
                a = (abPlaceable) v.get( i );
                if( a instanceof Equipment ) {
                    if( ((Equipment) a).GetCritName().equals( "Coolant Pod" ) ) {
                        NumPods++;
                    }
                }
            }
            // get the heat sink bonus
            int Bonus = (int) Math.ceil( (float) NumHS * ( (float) NumPods * 0.2f ) );
            if( Bonus > MaxHSBonus ) { Bonus = MaxHSBonus; }
            heff += Bonus;
        }

        // find out the total BV of rear and forward firing weapons
        for( int i = 0; i < wep.size(); i++ ) {
            a = ((abPlaceable) wep.get( i ));
            UseAESMod = UseAESModifier( a );
            if( a.IsMountedRear() ) {
                rearBV += a.GetCurOffensiveBV( false, TC, UseAESMod );
            } else {
                foreBV += a.GetCurOffensiveBV( false, TC, UseAESMod );
            }
        }
        if( rearBV > foreBV ) { UseRear = true; }

        // see if we need to run heat calculations
        if( heff - wheat >= 0 ) {
            // no need for extensive calculations, return the weapon BV
            for( int i = 0; i < wep.size(); i++ ) {
                a = ((abPlaceable) wep.get( i ));
                UseAESMod = UseAESModifier( a );
                result += a.GetCurOffensiveBV( UseRear, TC, UseAESMod );
            }
            return result;
        }

        // Sort the weapon list
        abPlaceable[] sorted = SortWeapons( wep, UseRear );

        // calculate the BV of the weapons based on heat
        int curheat = 0;
        for( int i = 0; i < sorted.length; i++ ) {
            UseAESMod = UseAESModifier( sorted[i] );
            if( curheat < heff ) {
                result += sorted[i].GetCurOffensiveBV( UseRear, UsingTC(), UseAESMod );
            } else {
                if( ((ifWeapon) sorted[i]).GetBVHeat() <= 0 ) {
                    result += sorted[i].GetCurOffensiveBV( UseRear, UsingTC(), UseAESMod );
                } else {
                    result += sorted[i].GetCurOffensiveBV( UseRear, UsingTC(), UseAESMod ) * 0.5f;
                }
            }
            curheat += ((ifWeapon) sorted[i]).GetBVHeat();
        }
        return result;
    }

    public float GetNonHeatEquipBV() {
        // return the BV of all offensive equipment
        float result = 0.0f;
        Vector v = CurLoadout.GetNonCore();

        for( int i = 0; i < v.size(); i++ ) {
            if( ! ( v.get( i ) instanceof ifWeapon ) ) {
                result += ((abPlaceable) v.get( i )).GetOffensiveBV();
            }
        }
        return result;
    }

    public float GetExcessiveAmmoPenalty() {
        float result = 0.0f;
        Vector v = CurLoadout.GetNonCore();
        Vector Ammo = new Vector(),
               Wep = new Vector();

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
                float ammoBV = ( NumAmmos * ammo.GetOffensiveBV() );
                if( ammoBV <= 0.0f ) {
                    ammoBV = ( NumAmmos * ammo.GetDefensiveBV() );
                }
                float wepBV = ( NumWeps * ((abPlaceable) test).GetOffensiveBV() );
                if( wepBV <= 0.0f ) {
                    wepBV = ( NumWeps * ((abPlaceable) test).GetDefensiveBV() );
                }
                if( ammoBV > wepBV ) {
                    result -= ammoBV - wepBV;
                }
            }
        }

        return result;
    }

    public float GetTonnageBV() {
        if( CurPhysEnhance.IsTSM() ) {
            return CurPhysEnhance.GetOffensiveBV();
        } else {
            if( Quad ) {
                if( HasLegAES ) {
                    return Tonnage * 1.4f;
                } else {
                    return Tonnage;
                }
            } else {
                float AESMod = 1.0f;
                if( HasLAAES ) { AESMod += 0.1f; }
                if( HasRAAES ) { AESMod += 0.1f; }
                if( HasLegAES ) { AESMod += 0.2f; }
                return Tonnage * AESMod;
            }
        }
    }

    public float GetOffensiveFactor() {
        float result = 0.0f;
        result += GetAdjustedRunningMP( true, true ) + ( Math.floor( GetAdjustedJumpingMP( true ) * 0.5f + 0.5f )  ) - 5.0f;
        result = result * 0.1f + 1.0f;
        result = (float) Math.pow( result, 1.2f ) ;

        // round off to the nearest two digits
        result = (float) Math.floor( result * 100 + 0.5f ) / 100;

        return result;
    }

    public ifLoadout GetLoadout() {
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

    public Armor GetArmor() {
        return CurArmor;
    }

    public TargetingComputer GetTC() {
        return CurLoadout.GetTC();
    }

    public float GetChassisCost() {
        // this method sets the cost variable by calculating the base cost.
        // this is usually only done whenever a chassis component changes.
        float ChassisCost = 0.0f;
        if( ! CurPhysEnhance.IsTSM() ) {
            // this is standard musculature.  If we have TSM, it's handled later
            ChassisCost += 2000 * Tonnage;
        }
        ChassisCost += CurEngine.GetCost();
        ChassisCost += CurGyro.GetCost();
        ChassisCost += CurIntStruc.GetCost();
        ChassisCost += CurCockpit.GetCost();
        if( HasCommandConsole() ) {
            ChassisCost += CommandConsole.GetCost();
        }
        ChassisCost += GetActuators().GetCost();
        ChassisCost += CurPhysEnhance.GetCost();
        ChassisCost += GetHeatSinks().GetCost();
        ChassisCost += GetJumpJets().GetCost();
        ChassisCost += CurArmor.GetCost();

        // check for Misc equipment.  We're going to add it to chassis cost
        // instead of equipment costs.  It all evens out in the end
        if( CurLoadout.IsUsingClanCASE() ) {
            int[] test = CurLoadout.FindExplosiveInstances();
            for( int i = 0; i < test.length; i++ ) {
                if( test[i] > 0 ) {
                    ChassisCost += CurLoadout.GetCTCase().GetCost();
                }
            }
        }
        if( HasCTCase() ) { ChassisCost += CurLoadout.GetCTCase().GetCost(); }
        if( HasLTCase() ) { ChassisCost += CurLoadout.GetCTCase().GetCost(); }
        if( HasRTCase() ) { ChassisCost += CurLoadout.GetCTCase().GetCost(); }
        if( CurLoadout.HasHDCASEII() ) { ChassisCost += CurLoadout.GetHDCaseII().GetCost(); }
        if( CurLoadout.HasCTCASEII() ) { ChassisCost += CurLoadout.GetCTCaseII().GetCost(); }
        if( CurLoadout.HasLTCASEII() ) { ChassisCost += CurLoadout.GetLTCaseII().GetCost(); }
        if( CurLoadout.HasRTCASEII() ) { ChassisCost += CurLoadout.GetRTCaseII().GetCost(); }
        if( CurLoadout.HasLACASEII() ) { ChassisCost += CurLoadout.GetLACaseII().GetCost(); }
        if( CurLoadout.HasRACASEII() ) { ChassisCost += CurLoadout.GetRACaseII().GetCost(); }
        if( CurLoadout.HasLLCASEII() ) { ChassisCost += CurLoadout.GetLLCaseII().GetCost(); }
        if( CurLoadout.HasRLCASEII() ) { ChassisCost += CurLoadout.GetRLCaseII().GetCost(); }
        if( HasNullSig() ) { ChassisCost += NullSig.GetCost(); }
        if( HasVoidSig() ) { ChassisCost += VoidSig.GetCost(); }
        if( HasChameleon() ) { ChassisCost += Chameleon.GetCost(); }
        if( HasBlueShield() ) { ChassisCost += BlueShield.GetCost(); }
        if( HasEnviroSealing() ) { ChassisCost += EnviroSealing.GetCost(); }
        if( HasEjectionSeat ) { ChassisCost += EjectionSeat.GetCost(); }
        if( Quad ) {
            if( HasLegAES ) { ChassisCost += RLAES.GetCost() * 4.0f; }
        } else {
            if( HasRAAES ) { ChassisCost += CurRAAES.GetCost(); }
            if( HasLAAES ) { ChassisCost += CurLAAES.GetCost(); }
            if( HasLegAES ) { ChassisCost += RLAES.GetCost() * 2.0f; }
        }

        // same goes for the targeting computer and supercharger
        if( CurLoadout.UsingTC() ) {
            ChassisCost += GetTC().GetCost();
        }
        if( CurLoadout.HasSupercharger() ) {
            ChassisCost += CurLoadout.GetSupercharger().GetCost();
        }

        return ChassisCost;
    }

    public float GetTotalCost() {
        // final cost calculations
        if( Omnimech ) {
            return ( GetEquipCost() + GetChassisCost() ) * 1.25f * MechMult;
        } else {
            return ( ( GetEquipCost() + GetChassisCost() ) * MechMult );
        }
    }

    public float GetDryCost() {
        // returns the total cost of the mech without ammunition
        if( Omnimech ) {
            return ( GetDryEquipCost() + GetChassisCost() ) * 1.25f * MechMult;
        } else {
            return ( ( GetDryEquipCost() + GetChassisCost() ) * MechMult );
        }
    }

    public float GetCostMult() {
        if( Omnimech ) {
            return 1.25f * MechMult;
        } else {
            return MechMult;
        }
    }

    public float GetBaseChassisCost() {
        // chassis cost in this context is different than the ChassisCost
        // variable.  It includes all components except engine, TC, and 
        // equipment without multiple calculation ("base" cost)

        float result = 0.0f;
        if( ! CurPhysEnhance.IsTSM() ) {
            // this is standard musculature.  If we have TSM, it's handled later
            result += 2000 * Tonnage;
        }
        result += CurGyro.GetCost();
        result += CurIntStruc.GetCost();
        result += CurCockpit.GetCost();
        if( HasCommandConsole() ) {
            result += CommandConsole.GetCost();
        }
        result += GetActuators().GetCost();
        result += CurPhysEnhance.GetCost();
        result += GetHeatSinks().GetCost();
        result += GetJumpJets().GetCost();
        result += CurArmor.GetCost();
        if( HasEjectionSeat ) {
            result += EjectionSeat.GetCost();
        }
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

    public Options GetOptions() {
        // provided for the components that may be governed by options
        return options;
    }

    public float GetEquipCost() {
        // gets the cost for all non-core items.  Anything that's not intrinsically
        // part of the chassis is kept in a seperate vector for this purpose.
        Vector v = CurLoadout.GetNonCore();
        float retval = 0.0f;
        if( v.size() > 0 ) {
            for( int i = 0; i < v.size(); i++ ) {
                retval += ( (abPlaceable) v.get( i ) ).GetCost();
            }
            if( ! CurEngine.IsNuclear() ) { retval += CurLoadout.GetPowerAmplifier().GetCost(); }
            return retval;
        } else {
            return retval;
        }
    }

    public float GetDryEquipCost() {
        // gets the cost for all non-core items minus ammuntion.
        Vector v = CurLoadout.GetNonCore();
        float retval = 0.0f;
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

    public float GetCaseTonnage() {
        float retval = 0.0f;

        if( HasCTCase() ) { retval += 0.5f; }
        if( HasLTCase() ) { retval += 0.5f; }
        if( HasRTCase() ) { retval += 0.5f; }

        return retval;
    }

    public float GetCASEIITonnage() {
        float retval = 0.0f;

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
                MainLoadout.AddTo( EjectionSeat, Constants.LOC_HD, 3 );
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
                                case Constants.LOC_RA:
                                    if( FRL ) {
                                        throw new Exception( "The 'Mech already has an AES system in the FRL.\nAES System will not be installed." );
                                    } else {
                                        CurLoadout.AddToRA( CurRAAES, Loc[i].Index );
                                        FRL = true;
                                    }
                                    break;
                                case Constants.LOC_LA:
                                    if( FLL ) {
                                        throw new Exception( "The 'Mech already has an AES system in the FLL.\nAES System will not be installed." );
                                    } else {
                                        CurLoadout.AddToLA( CurLAAES, Loc[i].Index );
                                        FLL = true;
                                    }
                                    break;
                                case Constants.LOC_RL:
                                    if( RL ) {
                                        throw new Exception( "The 'Mech already has an AES system in the RL.\nAES System will not be installed." );
                                    } else {
                                        CurLoadout.AddToRL( RLAES, Loc[i].Index );
                                        RL = true;
                                    }
                                    break;
                                case Constants.LOC_LL:
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
                                case Constants.LOC_RL:
                                    if( RL ) {
                                        throw new Exception( "The 'Mech already has an AES system in the RL.\nAES System will not be installed." );
                                    } else {
                                        CurLoadout.AddToRL( RLAES, Loc[i].Index );
                                        RL = true;
                                    }
                                    break;
                                case Constants.LOC_LL:
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

    public void CheckPhysicals() {
        // unallocates physical weapons, especially if the tonnage changes
        // we'll also check to see if the mech is a quad and remove the weapons
        Vector v = CurLoadout.GetNonCore();
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
        AvailableCode Base = new AvailableCode( TechBase );
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
        Vector v = CurLoadout.GetNonCore();
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

        // now adjust for the era.
        if( Era == AvailableCode.ERA_SUCCESSION ) {
            // cut out the Star League stuff.
            AvailableCode SW = new AvailableCode( Base.GetTechBase() );
            SW.SetCodes( 'A', 'X', 'A', 'A', 'A', 'X', 'A', 'A' );
            SW.SetISDates( 0, 0, false, 2801, 10000, 0, false, false );
            SW.SetCLDates( 0, 0, false, 2801, 10000, 0, false, false );
            SW.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
            Base.Combine( SW );
        }
        if( Era == AvailableCode.ERA_CLAN_INVASION ) {
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

    // sorting routine for weapon BV calculation. this is undoubtedly slow
    public abPlaceable[] SortWeapons( Vector v, boolean rear ) {
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
                v.setElementAt( v.get( i ), i - 1 );
                v.setElementAt( swap, i );
                i -= 1;
                if( i == 0 ) {
                    i = 1;
                }
            }
        }

        // check our values, ensuring that rear-firing weapons, then lower heat
        // weapons take precedence
        i = 1;
        while( i < v.size() ) {
            boolean AES1 = UseAESModifier( ((abPlaceable) v.get( i - 1 )) );
            boolean AES2 = UseAESModifier( ((abPlaceable) v.get( i )) );
            if( ((abPlaceable) v.get( i - 1 )).GetCurOffensiveBV( rear, TC, AES1 ) == ((abPlaceable) v.get( i )).GetCurOffensiveBV( rear, TC, AES2 ) ) {
                if( rear ) {
                    if( ((abPlaceable) v.get( i - 1 )).IsMountedRear() &! ((abPlaceable) v.get( i )).IsMountedRear() ) {
                        swap = v.get( i - 1 );
                        v.setElementAt( v.get( i ), i - 1 );
                        v.setElementAt( swap, i );
                    } else if( ((ifWeapon) v.get( i - 1)).GetHeat() > ((ifWeapon) v.get( i )).GetHeat() ) {
                        swap = v.get( i - 1 );
                        v.setElementAt( v.get( i ), i - 1 );
                        v.setElementAt( swap, i );
                    }
                } else {
                    if( ! ((abPlaceable) v.get( i - 1 )).IsMountedRear() && ((abPlaceable) v.get( i )).IsMountedRear() ) {
                        swap = v.get( i - 1 );
                        v.setElementAt( v.get( i ), i - 1 );
                        v.setElementAt( swap, i );
                    } else if( ((ifWeapon) v.get( i - 1)).GetHeat() > ((ifWeapon) v.get( i )).GetHeat() ) {
                        swap = v.get( i - 1 );
                        v.setElementAt( v.get( i ), i - 1 );
                        v.setElementAt( swap, i );
                    }
                }
            }
            i++;
        }

        // convert the results
        abPlaceable[] result = new abPlaceable[v.size()];
        for( i = 0; i < v.size(); i++ ) {
            result[i] = (abPlaceable) v.get( i );
        }

        return result;
    }

    public boolean UseAESModifier( abPlaceable a ) {
        if( ! ( a instanceof ifWeapon ) ) { return false; }
        if( HasLegAES || HasRAAES || HasLAAES ) {
            if( a.CanSplit() ) {
                Vector v = CurLoadout.FindSplitIndex( a );
                if( v.size() > 1 || v.size() < 1 ) { return false; }
                int test = ((LocationIndex) v.get( 0 )).Location;
                if( Quad ) {
                    if( HasLegAES && ( test == Constants.LOC_RL || test == Constants.LOC_LL || test == Constants.LOC_RA || test == Constants.LOC_LA ) ) {
                        if( a instanceof PhysicalWeapon ) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    if( HasLegAES && ( test == Constants.LOC_RL || test == Constants.LOC_LL ) ) {
                        if( a instanceof PhysicalWeapon ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    if( HasRAAES && test == Constants.LOC_RA ) {
                        return true;
                    }
                    if( HasLAAES && test == Constants.LOC_LA ) {
                        return true;
                    }
                }
            } else {
                int test = CurLoadout.Find( a );
                if( Quad ) {
                    if( HasLegAES && ( test == Constants.LOC_RL || test == Constants.LOC_LL || test == Constants.LOC_RA || test == Constants.LOC_LA ) ) {
                        if( a instanceof PhysicalWeapon ) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    if( HasLegAES && ( test == Constants.LOC_RL || test == Constants.LOC_LL ) ) {
                        if( a instanceof PhysicalWeapon ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    if( HasRAAES && test == Constants.LOC_RA ) {
                        return true;
                    }
                    if( HasLAAES && test == Constants.LOC_LA ) {
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
                CurLoadout.AddToHD( CommandConsole );
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
        SimplePlaceable p = new SimplePlaceable( "C3Test", "C3Test", 0, false, null );
        p.SetExclusions( new Exclusion( new String[] { "C3" }, "C3Test" ) );
        try {
            CurLoadout.CheckExclusions( p );
        } catch( Exception e ) {
            return true;
        }
        return false;
    }

    public Vector GetMechMods() {
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

    public MechModifier GetTotalModifiers( boolean BVMovement, boolean MASCTSM ) {
        MechModifier retval = new MechModifier( 0, 0, 0, 0.0f, 0, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, true );
        if( MechMods.size() > 0 ) {
            for( int i = 0; i < MechMods.size(); i++ ) {
                if( BVMovement ) {
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
                if( BVMovement ) {
                    retval.BVCombine( ((MechModifier) CurLoadout.GetMechMods().get( i )) );
                } else {
                    retval.Combine( ((MechModifier) CurLoadout.GetMechMods().get( i )) );
                }
            }
        }
        return retval;
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
        Source = s;
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
        return Source;
    }

    public void SetChanged( boolean b ) {
        Changed = b;
    }

    public boolean HasChanged() {
        return Changed;
    }

    public int GetBFPoints(){
        return GetCurrentBV() / 100;
    }

    public int GetBFSize(){
        int mass = GetTonnage();
        if( mass < 40 ){
            return Constants.BF_SIZE_LIGHT;
        }else if( mass < 70 ){
            return Constants.BF_SIZE_MEDIUM;
        }else if ( mass < 80 ){
            return Constants.BF_SIZE_HEAVY;
        }else{
            return Constants.BF_SIZE_ASSAULT;
        }
    }

    public int GetBFPrimeMovement(){
        float retval = GetWalkingMP();

        // Adjust retval for MASC and SC
        if ( CurLoadout.HasSupercharger() && GetPhysEnhance().IsMASC() ){
            retval *= 1.5f;
        }else if ( CurLoadout.HasSupercharger() || GetPhysEnhance().IsMASC() ){
            retval *= 1.25f;
        }

        return (int) Math.round(retval);
    }
    
    public String GetBFPrimeMovementMode(){
        int walkMP = GetWalkingMP();
        int jumpMP = GetAdjustedJumpingMP(false);

        if (walkMP == jumpMP){
            return "j";
        }else{
            return "";
        }
    }
    
    public int GetBFSecondaryMovement(){
        int walkMP = GetBFPrimeMovement();
        int jumpMP = GetAdjustedJumpingMP(false);

        if ( jumpMP > 0 && walkMP != jumpMP ){
            if ( jumpMP < walkMP && jumpMP*0.66 >= 1 )
                return jumpMP;
            else if ( jumpMP > walkMP )
                return jumpMP;
            else
                return 0;
        }else
            return 0;
    }

    public String GetBFSecondaryMovementMode(){
        int walkMP = GetBFPrimeMovement();
        int jumpMP = GetAdjustedJumpingMP(false);

        if ( jumpMP > 0 && walkMP != jumpMP )
            return "j";
        else
            return "";
    }

    public int GetBFArmor() {

        Armor a = GetArmor();
        float armorpoints = a.GetArmorValue();

        if ( a.IsCommercial() ){
            armorpoints = (float) Math.floor(armorpoints / 2.0f);
        }else if ( a.IsFerroLamellor() ){
            armorpoints = (float) Math.ceil(armorpoints * 1.2f);
        }else if ( a.IsHardened() ){
            armorpoints = (float) Math.ceil(armorpoints * 1.5f);
        }else if ( a.IsReactive() || a.IsReflective() ){
            armorpoints = (float) Math.ceil(armorpoints * 0.75f);
        }

        armorpoints += a.GetModularArmorValue();
        
        return (int) Math.round(armorpoints / 30);
    }

    public int GetBFStructure() {
        Engine e = GetEngine();
        int t = GetTonnage();
        return e.GetBFStructure(t);
    }

    public int [] GetBFDamage() {
        int [] retval = {0,0,0,0,0};

        // TODO Loop through all weapons in non-core
        // and convert all weapon dmg
        Vector nc = GetLoadout().GetNonCore();

        float dmgShort = 0.0f;
        float dmgMedium = 0.0f;
        float dmgLong = 0.0f;
        float dmgExtreme = 0.0f;
        int heatShort = 0;
        int heatMedium = 0;
        int heatLong = 0;
        int heatExtreme = 0;
        int totalHeat = 0;

        for ( int i = 0; i < nc.size(); i++ ) {
            float [] temp = BattleForceTools.GetDamage((ifWeapon)nc.get(i), (ifBattleforce)this);
            
            dmgShort += temp[Constants.BF_SHORT];
            dmgMedium += temp[Constants.BF_MEDIUM];
            dmgLong += temp[Constants.BF_LONG];
            dmgExtreme += temp[Constants.BF_EXTREME];
            
            totalHeat += (int) temp[Constants.BF_OV];

            if ( dmgMedium == 0 ) {
                heatShort += (int) temp[Constants.BF_OV];
            } else if ( dmgLong == 0 ) {
                heatShort += (int) temp[Constants.BF_OV];
                heatMedium += (int) temp[Constants.BF_OV];
            } else if ( dmgExtreme == 0 ) {
                heatShort += (int) temp[Constants.BF_OV];
                heatMedium += (int) temp[Constants.BF_OV];
                heatLong += (int) temp[Constants.BF_OV];
            } else {
                heatShort += (int) temp[Constants.BF_OV];
                heatMedium += (int) temp[Constants.BF_OV];
                heatLong += (int) temp[Constants.BF_OV];
                heatExtreme += (int) temp[Constants.BF_OV];
            }

        }

        // Add in heat for movement
        if ( GetAdjustedJumpingMP(false) > 2 ) {
            totalHeat += GetAdjustedJumpingMP(false);
        } else {
            totalHeat += 2;
        }

        // Subtract 4 because Joel says so...
        // and besides, Joel is awesome and we should trust him
        totalHeat -= 4;

        // What is the max damage?
        int maxShort = (int) Math.ceil(dmgShort / 10);
        int maxMedium = (int) Math.ceil(dmgMedium / 10);

        // Will this ifBattleForce overheat?
        int heatcap = this.GetHeatSinks().TotalDissipation();
        System.out.println("" + heatcap + " " + totalHeat);
        
        if ( totalHeat - heatcap > 0) {
            dmgShort = (dmgShort * heatcap) / totalHeat;
            dmgMedium = (dmgMedium * heatcap) / totalHeat;
            dmgLong = (dmgLong * heatcap) / totalHeat;
            dmgExtreme = (dmgExtreme * heatcap) / totalHeat;
        }

        // Convert to BF scale
        retval[Constants.BF_SHORT] = (int) Math.ceil(dmgShort / 10);
        retval[Constants.BF_MEDIUM] = (int) Math.ceil(dmgMedium / 10);
        retval[Constants.BF_LONG] = (int) Math.ceil(dmgLong / 10);
        retval[Constants.BF_EXTREME] = (int) Math.ceil(dmgExtreme / 10);

        // Determine OverHeat
        if ( maxMedium != 0 ) {
            retval[Constants.BF_OV] = maxMedium - retval[Constants.BF_MEDIUM];
        } else {
            retval[Constants.BF_OV] = maxShort - retval[Constants.BF_SHORT];
        }

        return retval;
    }

    public String GetBFConversionStr( ) {
        String retval = "Weapon\t\t\tShort\tMedium\tLong\n\r";
        //TODO Add in conversion steps if possible
        retval += "________________________________________________________________________________" + System.getProperty( "line.separator" );
        retval += "Base Damage\n\rHeat\n\rHeat Adjusted";
        return retval;
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
        Lookup.put( "Small Cockpit", new VCockpitSetSmall() );
        Lookup.put( "Torso-Mounted Cockpit", new VCockpitSetTorsoMount() );
        Lookup.put( "Fuel-Cell Engine", new VEngineSetFuelCell() );
        Lookup.put( "Fission Engine", new VEngineSetFission() );
        Lookup.put( "Fusion Engine", new VEngineSetFusion() );
        Lookup.put( "XL Engine", new VEngineSetFusionXL() );
        Lookup.put( "(IS) XL Engine", new VEngineSetFusionXL() );
        Lookup.put( "(CL) XL Engine", new VEngineSetFusionXL() );
        Lookup.put( "XXL Engine", new VEngineSetFusionXXL() );
        Lookup.put( "(IS) XXL Engine", new VEngineSetFusionXXL() );
        Lookup.put( "(CL) XXL Engine", new VEngineSetFusionXXL() );
        Lookup.put( "I.C.E. Engine", new VEngineSetICE() );
        Lookup.put( "Compact Fusion Engine", new VEngineSetCompactFusion() );
        Lookup.put( "Light Fusion Engine", new VEngineSetLightFusion() );
        Lookup.put( "Standard Gyro", new VGyroSetStandard() );
        Lookup.put( "Heavy-Duty Gyro", new VGyroSetHD() );
        Lookup.put( "Extra-Light Gyro", new VGyroSetXL() );
        Lookup.put( "Compact Gyro", new VGyroSetCompact() );
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
        Lookup.put( "Standard Jump Jet", new VJumpJetSetStandard() );
        Lookup.put( "Improved Jump Jet", new VJumpJetSetImproved() );
        Lookup.put( "Mech UMU", new VJumpJetSetUMU() );

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

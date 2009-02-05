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

import java.util.Vector;
import ssw.*;
import ssw.gui.frmMain;
import ssw.visitors.*;

public class Mech {
    // A mech for the designer.  This is a large container class that will
    // handle calculations and settings for the design.

    // Declares
    private frmMain Parent;
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
                   SSWImage = Constants.NO_IMAGE;
    private int Era,
                Year,
                TechBase,
                RulesLevel,
                Tonnage = 20,
                WalkMP;
    private float JJMult,
                  ChassisCost,
                  MechMult;
    private final static float[] DefensiveFactor = { 1.0f, 1.0f, 1.1f, 1.1f, 1.2f, 1.2f,
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
                    HasEnviroSealing = false;
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
                            EnviroSealing;

    // Constructor
    public Mech( frmMain window ) {
        Parent = window;
        // Set the names and years to blank so the user doesn't have to overtype
        Name = "";
        Era = Constants.STAR_LEAGUE;
        TechBase = Constants.INNER_SPHERE;
        RulesLevel = Constants.TOURNAMENT;
        Year = 2750;
        YearRestricted = false;

        // Basic setup for the mech.  This is an arbitrary default chassis
        Quad = false;
        Omnimech = false;
        WalkMP = 1;
        CurEngine.SetRating( 20 );
        CurLoadout.SetBaseLoadout( MainLoadout );

        // load up some special equipment
        AvailableCode AC = new AvailableCode( false, 'E', 'E', 'X', 'X', 2630, 2790, 0, "TH", "", true, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        NullSig = new MultiSlotSystem( this, "Null Signature System", "NullSignatureSystem", 0.0f, false, true, 1400000.0f, false, AC );
        NullSig.AddMechModifier( new MechModifier( 0, 0, 0, 0.0f, 0, 0, 10, 0.2f, 0.0f, 0.0f, 0.0f, true ) );
        NullSig.SetExclusions( new Exclusion( new String[] { "Targeting Computer", "Void Signature System", "Stealth Armor", "C3" }, "Null Signature System" ) );
        AC = new AvailableCode( false, 'E', 'F', 'X', 'X', 2630, 2790, 0, "TH", "", true, false, 0, false, "", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        Chameleon = new MultiSlotSystem( this, "Chameleon LPS", "ChameleonLightPolarizationField", 0.0f, true, true, 600000.0f, false, AC );
        Chameleon.AddMechModifier( new MechModifier( 0, 0, 0, 0.0f, 0, 0, 6, 0.2f, 0.0f, 0.0f, 0.0f, true ) );
        Chameleon.SetExclusions( new Exclusion( new String[] { "Void Signature System", "Stealth Armor" }, "Chameleon LPS" ) );
        AC = new AvailableCode( false, 'E', 'X', 'X', 'F', 3053, 0, 0, "FS", "", false, false, 3051, true, "FS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        BlueShield = new MultiSlotSystem( this, "Blue Shield PFD", "BlueShieldPFD", 3.0f, false, true, 1000000.0f, false, AC );
        BlueShield.AddMechModifier( new MechModifier( 0, 0, 0, 0.0f, 0, 0, 0, 0.0f, 0.0f, 0.2f, 0.2f, true ) );
        AC = new AvailableCode( false, 'E', 'X', 'X', 'E', 3070, 0, 0, "WB", "", false, false, 3060, true, "WB", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        VoidSig = new MultiSlotSystem( this, "Void Signature System", "VoidSignatureSystem", 0.0f, false, true, 2000000.0f, false, AC );
        VoidSig.AddMechModifier( new MechModifier( 0, 0, 0, 0.0f, 0, 0, 10, 0.0f, 1.3f, 0.0f, 0.0f, true ) );
        VoidSig.SetExclusions( new Exclusion( new String[] { "Targeting Computer", "Null Signature System", "Stealth Armor", "C3", "Chameleon LPS" }, "Void Signature System" ) );
        AC = new AvailableCode( false, 'C', 'C', 'C', 'C', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.UNALLOWED, Constants.TOURNAMENT );
        EnviroSealing = new MultiSlotSystem( this, "Environmental Sealing", "Environmental Sealing", 0.0f, false, false, 225.0f, true, AC );
        //EnviroSealing.AddMechModifier( new MechModifier( 0, 0, 0, 0.0f, 0, 0, 10, 0.0f, 1.3f, 0.0f, 0.0f, true ) );
        //EnviroSealing.SetExclusions( new Exclusion( new String[] { "Targeting Computer", "Null Signature System", "Stealth Armor", "C3", "Chameleon LPS" }, "Void Signature System" ) );
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
        MechMult = 1.0f + ( Tonnage * 0.01f );
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
    }

    public void SetName( String n ) {
        Name = n;
    }

    public void SetModel( String m ) {
        Model = m;
    }

    public void SetEra( int e ) {
        Era = e;
    }

    public void SetRulesLevel( int r ) {
        if( Omnimech ) {
            CurLoadout.SetRulesLevel( r );
        } else {
            RulesLevel = r;
            MainLoadout.SetRulesLevel( r );
        }
    }

    public void SetYear( int y, boolean specified ) {
        Year = y;
        YearSpecified = specified;
    }

    public void SetYearRestricted( boolean y ) {
        YearRestricted = y;
    }

    public boolean IsYearRestricted() {
        return YearRestricted;
    }

    public void SetTechBase( int t ) {
        TechBase = t;
    }

    public void SetSolaris7ID( String ID ) {
        Solaris7ID = ID;
    }

    public void SetSolaris7ImageID( String ID ) {
        Solaris7ImageID = ID;
    }

    public void SetSSWImage( String image ) {
        SSWImage = image;
    }

    public void SetInnerSphere() {
        // performs all the neccesary actions to switch this to Inner Sphere
        // set the tech base
        TechBase = Constants.INNER_SPHERE;

        // clear out any MechModifiers in the chassis and loadout
        MechMods.clear();
        CurLoadout.GetMechMods().clear();

        // clear the loadout
        CurLoadout.ClearLoadout();

        // switch the engine over to a military Standard
        CurEngine.SetISFUEngine();

        // switch the gyro
        CurGyro.SetISStandard();

        // switch the internal structure
        if( IsQuad() ) {
            if( IndustrialMech ) {
                CurIntStruc.SetISIMQD();
            } else {
                CurIntStruc.SetISMSQD();
            }
        } else {
            if( IndustrialMech ) {
                CurIntStruc.SetISIMBP();
            } else {
                CurIntStruc.SetISMSBP();
            }
        }

        // switch the cockpit
        if( IndustrialMech ) {
            CurCockpit.SetISIndustrialCockpit();
        } else {
            CurCockpit.SetISCockpit();
        }

        // switch the heat sinks
        GetHeatSinks().SetInnerSphere();
        GetHeatSinks().SetSingle();

        // switch the jump jets
        GetJumpJets().SetInnerSphere();
        GetJumpJets().SetNormal();

        // switch the physical enhancement
        CurPhysEnhance.SetISNone();

        // set the armor type
        CurArmor.SetISMS();

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
        UseTC( false );
    }

    public void SetClan() {
        // performs all the neccesary actions to switch this to Clan
        // set the tech base
        TechBase = Constants.CLAN;

        // clear out any MechModifiers in the chassis and loadout
        MechMods.clear();
        CurLoadout.GetMechMods().clear();

        // clear the loadout
        CurLoadout.ClearLoadout();

        // switch the engine over to a military Standard Clan
        CurEngine.SetCLFUEngine();

        // switch the gyro
        CurGyro.SetCLStandard();

        // switch the internal structure
        if( IsQuad() ) {
            if( IndustrialMech ) {
                CurIntStruc.SetCLIMQD();
            } else {
                CurIntStruc.SetCLMSQD();
            }
        } else {
            if( IndustrialMech ) {
                CurIntStruc.SetCLIMBP();
            } else {
                CurIntStruc.SetCLMSBP();
            }
        }

        // switch the cockpit
        if( IndustrialMech ) {
            CurCockpit.SetCLIndustrialCockpit();
        } else {
            CurCockpit.SetClanCockpit();
        }

        // switch the heat sinks
        GetHeatSinks().SetClan();
        GetHeatSinks().SetSingle();

        // switch the jump jets
        GetJumpJets().SetClan();
        GetJumpJets().SetNormal();

        // switch the physical enhancement
        CurPhysEnhance.SetCLNone();

        // set the armor type
        CurArmor.SetCLMS();

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
        UseTC( false );
    }

/*    public void SetPrimitive() {
        // performs all the neccesary actions to switch the Tech Base over to Primitive.
        // set the tech base
        TechBase = Constants.INNER_SPHERE;

        // clear out any MechModifiers in the chassis and loadout
        MechMods.clear();
        CurLoadout.GetMechMods().clear();

        // clear the loadout
        CurLoadout.ClearLoadout();

        // switch the engine over to a military Standard Clan
        CurEngine.SetISFUEngine();

        // switch the gyro
        CurGyro.SetISStandard();

        // switch the internal structure
        if( IsQuad() ) {
            CurIntStruc.SetISPRQD();
        } else {
            CurIntStruc.SetISPRBP();
        }

        // switch the cockpit
        CurCockpit.SetPrimitiveCockpit();

        // switch the heat sinks
        GetHeatSinks().SetInnerSphere();
        GetHeatSinks().SetSingle();

        // switch the jump jets
        GetJumpJets().SetInnerSphere();
        GetJumpJets().SetNormal();

        // switch the physical enhancement
        CurPhysEnhance.SetISNone();

        // set the armor type
        CurArmor.SetISIN();

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
        UseTC( false );

        Primitive = true;

        // recalculate to get the correct engine tonnage and rating
        Recalculate();
    }*/

    public boolean IsClan() {
        if( TechBase == Constants.CLAN ) {
            return true;
        } else {
            return false;
        }
    }

    public void SetBiped() {
        // this performs all the neccesary actions to change this mech into a biped
        // see if we have any CASE systems installed first.
        boolean ctcase = HasCTCase();
        boolean ltcase = HasLTCase();
        boolean rtcase = HasRTCase();
        boolean hdcase2 = CurLoadout.HasHDCASEII();
        boolean ctcase2 = CurLoadout.HasCTCASEII();
        boolean ltcase2 = CurLoadout.HasLTCASEII();
        boolean rtcase2 = CurLoadout.HasRTCASEII();
        boolean lacase2 = CurLoadout.HasLACASEII();
        boolean racase2 = CurLoadout.HasRACASEII();
        boolean llcase2 = CurLoadout.HasLLCASEII();
        boolean rlcase2 = CurLoadout.HasRLCASEII();

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
        if( IsClan() ) {
            if( IndustrialMech ) {
                CurIntStruc.SetCLIMBP();
            } else {
                CurIntStruc.SetCLMSBP();
            }
        } else {
            if( IndustrialMech ) {
                CurIntStruc.SetISIMBP();
            } else {
                CurIntStruc.SetISMSBP();
            }
        }

        // set the mech to a biped
        Quad = false;

        // replace everything into the new loadout
        CurGyro.Place( CurLoadout );
        CurEngine.Place( CurLoadout );
        CurIntStruc.Place( CurLoadout );
        CurCockpit.Place( CurLoadout );
        GetActuators().PlaceActuators();
        CurPhysEnhance.Place( CurLoadout );
        // reset the correct number of heat sinks and jump jets
        for( int i = 0; i < NumJJ; i++ ) {
            GetJumpJets().IncrementNumJJ();
        }
        for( int i = 0; i < NumHS; i++ ) {
            GetHeatSinks().IncrementNumHS();
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
                CurLoadout.SetHDCASEII( true, -1 );
            }
            if( ctcase2 ) {
                CurLoadout.SetCTCASEII( true, -1 );
            }
            if( ltcase2 ) {
                CurLoadout.SetLTCASEII( true, -1 );
            }
            if( rtcase2 ) {
                CurLoadout.SetRTCASEII( true, -1 );
            }
            if( lacase2 ) {
                CurLoadout.SetLACASEII( true, -1 );
            }
            if( racase2 ) {
                CurLoadout.SetRACASEII( true, -1 );
            }
            if( llcase2 ) {
                CurLoadout.SetLLCASEII( true, -1 );
            }
            if( rlcase2 ) {
                CurLoadout.SetRLCASEII( true, -1 );
            }
        } catch( Exception e ) {
            // unhandled at this time, print an error out
            System.err.println( "CASE system not reinstalled:\n" + e.getMessage() );
        }
    }

    public void SetQuad() {
        // this performs all the neccesary actions to change this mech into a quad
        // see if we have any CASE systems installed first.
        boolean ctcase = HasCTCase();
        boolean ltcase = HasLTCase();
        boolean rtcase = HasRTCase();
        boolean hdcase2 = CurLoadout.HasHDCASEII();
        boolean ctcase2 = CurLoadout.HasCTCASEII();
        boolean ltcase2 = CurLoadout.HasLTCASEII();
        boolean rtcase2 = CurLoadout.HasRTCASEII();
        boolean lacase2 = CurLoadout.HasLACASEII();
        boolean racase2 = CurLoadout.HasRACASEII();
        boolean llcase2 = CurLoadout.HasLLCASEII();
        boolean rlcase2 = CurLoadout.HasRLCASEII();

        // remember how many heat sinks and jump jets we had
        int NumJJ = GetJumpJets().GetNumJJ();
        int NumHS = GetHeatSinks().GetNumHS() - CurEngine.FreeHeatSinks();

        // remove any existing physical weapons, since quads can't mount them
        Vector v = CurLoadout.GetNonCore();
        for( int i = v.size() - 1; i >= 0; i-- ) {
            abPlaceable p = (abPlaceable) v.get( i );
            if( p instanceof PhysicalWeapon ) {
                CurLoadout.Remove( p );
            }
        }

        // Get a new Quad Loadout and load up the queue
        ifLoadout l = new QuadLoadout( Constants.BASELOADOUT_NAME, this );
        CurLoadout.Transfer(l);
        CurLoadout.ClearLoadout();

        // Now set the new main loadout and current loadout
        MainLoadout = l;
        CurLoadout = l;
        CurLoadout.SetBaseLoadout( MainLoadout );

        // next, change the internal structure to a default standard quad
        if( IsClan() ) {
            if( IndustrialMech ) {
                CurIntStruc.SetCLIMQD();
            } else {
                CurIntStruc.SetCLMSQD();
            }
        } else {
            if( IndustrialMech ) {
                CurIntStruc.SetISIMQD();
            } else {
                CurIntStruc.SetISMSQD();
            }
        }

        // set the mech to a quad
        Quad = true;

        // replace everything into the new loadout
        CurGyro.Place( CurLoadout );
        CurEngine.Place( CurLoadout );
        CurIntStruc.Place( CurLoadout );
        CurCockpit.Place( CurLoadout );
        GetActuators().PlaceActuators();
        CurPhysEnhance.Place( CurLoadout );
        // reset the correct number of heat sinks and jump jets
        for( int i = 0; i < NumJJ; i++ ) {
            GetJumpJets().IncrementNumJJ();
        }
        for( int i = 0; i < NumHS; i++ ) {
            GetHeatSinks().IncrementNumHS();
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
                CurLoadout.SetHDCASEII( true, -1 );
            }
            if( ctcase2 ) {
                CurLoadout.SetCTCASEII( true, -1 );
            }
            if( ltcase2 ) {
                CurLoadout.SetLTCASEII( true, -1 );
            }
            if( rtcase2 ) {
                CurLoadout.SetRTCASEII( true, -1 );
            }
            if( lacase2 ) {
                CurLoadout.SetLACASEII( true, -1 );
            }
            if( racase2 ) {
                CurLoadout.SetRACASEII( true, -1 );
            }
            if( llcase2 ) {
                CurLoadout.SetLLCASEII( true, -1 );
            }
            if( rlcase2 ) {
                CurLoadout.SetRLCASEII( true, -1 );
            }
        } catch( Exception e ) {
            // unhandled at this time, print an error out
            System.err.println( "CASE system not reinstalled:\n" + e.getMessage() );
        }
    }

    public boolean IsQuad() {
        return Quad;
    }

    public void SetIndustrialmech() {
        // do all the neccesary things to change over to an IndustrialMech
        IndustrialMech = true;
        if( IsClan() ) {
            SetClan();
        } else {
            SetInnerSphere();
        }
        if( Quad ) {
            SetQuad();
        } else {
            SetBiped();
        }
    }

    public void SetBattlemech() {
        // do all the neccesary things to change over to a BattleMech
        IndustrialMech = false;
        if( IsClan() ) {
            SetClan();
        } else {
            SetInnerSphere();
        }
        if( Quad ) {
            SetQuad();
        } else {
            SetBiped();
        }
    }

    public void SetOmnimech( String name ) {
        // this performs everything needed to turn the mech into an omni
        Omnimech = true;

        // remove any targeting computers from the base chassis.  they vary too
        // much to be fixed equipment
        UseTC( false );

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
        if( GetRulesLevel() >= Constants.ADVANCED ) {
            return 3;
        } else {
            if( IsClan() ) {
                return 2;
            }
            if( GetAvailability().GetSWCode() < 'F' ) {
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
            case Constants.TOURNAMENT:
                if( IsClan() ) {
                    return 2;
                }
                if( GetAvailability().GetSWCode() < 'F' ) {
                    if( GetHeatSinks().IsDouble() ) {
                        return 2;
                    } else {
                        return 1;
                    }
                } else {
                    return 2;
                }
            case Constants.ADVANCED:
                return 3;
            case Constants.EXPERIMENTAL:
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

    public int GetTechBase() {
        return TechBase;
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

        if( Parent.GetOptions().Heat_RemoveJumps ) {
            if( Parent.GetOptions().Heat_RemoveMovement ) {
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
        boolean UseOS = Parent.GetOptions().Heat_RemoveOSWeapons;
        boolean UseRear = Parent.GetOptions().Heat_RemoveRearWeapons;
        boolean FullRate = Parent.GetOptions().Heat_UAC_RAC_FullRate;
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
                    if( a instanceof BallisticWeapon ) {
                        if( FullRate ) {
                            if( ((BallisticWeapon) a).IsUltra() ) {
                                result += ((ifWeapon) a).GetHeat() * 2;
                            } else if( ((BallisticWeapon) a).IsRotary() ) {
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
                if( ! Parent.GetOptions().Heat_RemoveEquipment ) {
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
        if( RulesLevel == Constants.EXPERIMENTAL && Era == Constants.CLAN_INVASION ) {
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

        if( IsClan() ) {
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

        return result;
    }

    public float GetExplosiveWeaponPenalty() {
        float result = 0.0f;
        Vector v = CurLoadout.GetNonCore();
        abPlaceable p;

        if( IsClan() ) {
            for( int i = 0; i < v.size(); i++ ) {
                p = (abPlaceable) v.get( i );
                if( p instanceof ifWeapon ) {
                    if( ((ifWeapon) p).IsExplosive() ) {
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
            }
        } else {
            for( int i = 0; i < v.size(); i++ ) {
                p = (abPlaceable) v.get( i );
                if( p instanceof ifWeapon ) {
                    if( ((ifWeapon) p).IsExplosive() ) {
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
        }

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
        boolean UseRear = false;

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
        float TCTotal = 0.0f;

        // find out the total BV of rear and forward firing weapons
        for( int i = 0; i < wep.size(); i++ ) {
            if( ((abPlaceable) wep.get( i )).IsMountedRear() ) {
                rearBV += ((abPlaceable) wep.get( i )).GetOffensiveBV();
            } else {
                foreBV += ((abPlaceable) wep.get( i )).GetOffensiveBV();
            }
        }
        if( rearBV > foreBV ) { UseRear = true; }

        // see if we need to run heat calculations
        if( heff - wheat >= 0 ) {
            // no need for extensive calculations, return the weapon BV
            for( int i = 0; i < wep.size(); i++ ) {
                result += ((abPlaceable) wep.get( i )).GetCurOffensiveBV( UseRear );
                if( ((ifWeapon) wep.get( i )).IsTCCapable() ) {
                    TCTotal += ((abPlaceable) wep.get( i )).GetCurOffensiveBV( UseRear );
                }
            }
            if( CurLoadout.UsingTC() ) {
                TCTotal = TCTotal * 0.25f;
                result += TCTotal;
            }
            return result;
        }

        // Sort the weapon list
        abPlaceable[] sorted = SortWeapons( wep, UseRear );

        // calculate the BV of the weapons based on heat
        int curheat = 0;
        for( int i = 0; i < sorted.length; i++ ) {
            if( curheat < heff ) {
                result += sorted[i].GetCurOffensiveBV( UseRear );
                if( ((ifWeapon) sorted[i]).IsTCCapable() ) {
                    TCTotal += sorted[i].GetCurOffensiveBV( UseRear );
                }
            } else {
                if( ((ifWeapon) sorted[i]).GetBVHeat() <= 0 ) {
                    result += sorted[i].GetCurOffensiveBV( UseRear );
                    if( ((ifWeapon) sorted[i]).IsTCCapable() ) {
                        TCTotal += sorted[i].GetCurOffensiveBV( UseRear );
                    }
                } else {
                    result += sorted[i].GetCurOffensiveBV( UseRear ) * 0.5f;
                    if( ((ifWeapon) sorted[i]).IsTCCapable() ) {
                        TCTotal += sorted[i].GetCurOffensiveBV( UseRear ) * 0.5f;
                    }
                }
            }
            curheat += ((ifWeapon) sorted[i]).GetBVHeat();
        }
        if( CurLoadout.UsingTC() ) {
            TCTotal = TCTotal * 0.25f;
            result += TCTotal;
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
            return Tonnage;
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

    public void ReCalcBaseCost() {
        // this method sets the cost variable by calculating the base cost.
        // this is usually only done whenever a chassis component changes.
        ChassisCost = 0.0f;
        if( ! CurPhysEnhance.IsTSM() ) {
            // this is standard musculature.  If we have TSM, it's handled later
            ChassisCost += 2000 * Tonnage;
        }
        ChassisCost += CurEngine.GetCost();
        ChassisCost += CurGyro.GetCost();
        ChassisCost += CurIntStruc.GetCost();
        ChassisCost += CurCockpit.GetCost();
        ChassisCost += GetActuators().GetCost();
        ChassisCost += CurPhysEnhance.GetCost();
        ChassisCost += GetHeatSinks().GetCost();
        ChassisCost += GetJumpJets().GetCost();
        ChassisCost += CurArmor.GetCost();

        // check for Misc equipment.  We're going to add it to chassis cost
        // instead of equipment costs.  It all evens out in the end
        if( IsClan() ) {
            int[] test = CurLoadout.FindExplosiveInstances();
            for( int i = 0; i < test.length; i++ ) {
                if( test[i] > 0 ) {
                    ChassisCost += CurLoadout.GetCTCase().GetCost();
                }
            }
        } else {
            if( HasCTCase() ) { ChassisCost += CurLoadout.GetCTCase().GetCost(); }
            if( HasLTCase() ) { ChassisCost += CurLoadout.GetCTCase().GetCost(); }
            if( HasRTCase() ) { ChassisCost += CurLoadout.GetCTCase().GetCost(); }
        }
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

        // same goes for the targeting computer and supercharger
        if( CurLoadout.UsingTC() ) {
            ChassisCost += GetTC().GetCost();
        }
        if( CurLoadout.HasSupercharger() ) {
            ChassisCost += CurLoadout.GetSupercharger().GetCost();
        }
    }

    public float GetTotalCost() {
        // final cost calculations
        if( Omnimech ) {
            return ( GetEquipCost() + ChassisCost ) * 1.25f * MechMult;
        } else {
            return ( ( GetEquipCost() + ChassisCost ) * MechMult );
        }
    }

    public float GetDryCost() {
        // returns the total cost of the mech without ammunition
        if( Omnimech ) {
            return ( GetDryEquipCost() + ChassisCost ) * 1.25f * MechMult;
        } else {
            return ( ( GetDryEquipCost() + ChassisCost ) * MechMult );
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
        result += GetActuators().GetCost();
        result += CurPhysEnhance.GetCost();
        result += GetHeatSinks().GetCost();
        result += GetJumpJets().GetCost();
        result += CurArmor.GetCost();
        if( IsClan() ) {
            int[] test = CurLoadout.FindExplosiveInstances();
            for( int i = 0; i < test.length; i++ ) {
                if( test[i] > 0 ) {
                    result += CurLoadout.GetCTCase().GetCost();
                }
            }
        } else {
            if( HasCTCase() ) { result += CurLoadout.GetCTCase().GetCost(); }
            if( HasLTCase() ) { result += CurLoadout.GetCTCase().GetCost(); }
            if( HasRTCase() ) { result += CurLoadout.GetCTCase().GetCost(); }
        }
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

    public void Visit( ifVisitor v ) {
        v.Visit( this );
    }

    public Options GetOptions() {
        // provided for the components that may be governed by options
        return Parent.GetOptions();
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

        if( CurLoadout.HasHDCASEII() ) { retval += CurLoadout.GetHDCaseII().GetCost(); }
        if( CurLoadout.HasCTCASEII() ) { retval += CurLoadout.GetCTCaseII().GetCost(); }
        if( CurLoadout.HasLTCASEII() ) { retval += CurLoadout.GetLTCaseII().GetCost(); }
        if( CurLoadout.HasRTCASEII() ) { retval += CurLoadout.GetRTCaseII().GetCost(); }
        if( CurLoadout.HasLACASEII() ) { retval += CurLoadout.GetLACaseII().GetCost(); }
        if( CurLoadout.HasRACASEII() ) { retval += CurLoadout.GetRACaseII().GetCost(); }
        if( CurLoadout.HasLLCASEII() ) { retval += CurLoadout.GetLLCaseII().GetCost(); }
        if( CurLoadout.HasRLCASEII() ) { retval += CurLoadout.GetRLCaseII().GetCost(); }

        return retval;
    }

    // handlers for Artemis IV operations.
    public void SetA4FCSSRM( boolean b ) throws Exception {
        CurLoadout.SetA4FCSSRM( b );
    }

    public void SetA4FCSLRM( boolean b ) throws Exception {
        CurLoadout.SetA4FCSLRM( b );
    }

    public void SetA4FCSMML( boolean b ) throws Exception {
        CurLoadout.SetA4FCSMML( b );
    }

    public boolean UsingA4SRM() {
        return CurLoadout.UsingA4SRM();
    }

    public boolean UsingA4LRM() {
        return CurLoadout.UsingA4LRM();
    }

    public boolean UsingA4MML() {
        return CurLoadout.UsingA4MML();
    }

    public void UseTC( boolean use ) {
        CurLoadout.UseTC( use );
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
    }

    public boolean HasEnviroSealing() {
        return HasEnviroSealing;
    }

    public MultiSlotSystem GetEnviroSealing() {
        return EnviroSealing;
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

    public AvailableCode GetAvailability() {
        // returns the availability code for this mech based on all components
        AvailableCode AC;
        if( Omnimech ) {
            if( IsClan() ) {
                AC = new AvailableCode( IsClan(), 'E', 'X', 'E', 'E', 2854, 10000, 0, "NA", "NA", false, false );
            } else {
                AC = new AvailableCode( IsClan(), 'E', 'X', 'X', 'E', 3052, 10000, 0, "NA", "NA", false, false );
            }
        } else {
            AC = new AvailableCode( IsClan(), 'A', 'A', 'A', 'A', 0, 10000, 0, "NA", "NA", false, false );
        }

        // combine the availability codes from all equipment
        AC.Combine( CurEngine.GetAvailability() );
        AC.Combine( CurGyro.GetAvailability() );
        AC.Combine( CurIntStruc.GetAvailability() );
        AC.Combine( CurCockpit.GetAvailability() );
        AC.Combine( GetActuators().GetAvailability() );
        AC.Combine( CurPhysEnhance.GetAvailability() );
        AC.Combine( GetHeatSinks().GetAvailability() );
        if( GetJumpJets().GetNumJJ() > 0 ) {
            AC.Combine( GetJumpJets().GetAvailability() );
        }
        AC.Combine( CurArmor.GetAvailability() );
        if( CurLoadout.UsingTC() ) {
            AC.Combine( GetTC().GetAvailability() );
        }
        if( HasCTCase() || HasRTCase() || HasLTCase() ) {
            AC.Combine( CurLoadout.GetCTCase().GetAvailability() );
        }
        if( CurLoadout.HasHDCASEII() || CurLoadout.HasCTCASEII() || CurLoadout.HasLTCASEII() || CurLoadout.HasRTCASEII() ||
            CurLoadout.HasLACASEII() || CurLoadout.HasRACASEII() || CurLoadout.HasLLCASEII() || CurLoadout.HasRLCASEII() ) {
            AC.Combine( CurLoadout.GetCTCaseII().GetAvailability() );
        }
        if( ! CurEngine.IsNuclear() ) { AC.Combine( CurLoadout.GetPowerAmplifier().GetAvailability() ); }
        Vector v = CurLoadout.GetNonCore();
        for( int i = 0; i < v.size(); i++ ) {
            AC.Combine( ((abPlaceable) v.get( i )).GetAvailability() );
        }
        if( CurLoadout.HasSupercharger() ) {
            AC.Combine( CurLoadout.GetSupercharger().GetAvailability() );
        }

        if( HasBlueShield() ) {
            AC.Combine( BlueShield.GetAvailability() );
        }
        if( HasNullSig() ) {
            AC.Combine( NullSig.GetAvailability() );
        }
        if( HasVoidSig() ) {
            AC.Combine( VoidSig.GetAvailability() );
        }
        if( HasChameleon() ) {
            AC.Combine( Chameleon.GetAvailability() );
        }
        if( HasEnviroSealing() ) {
            AC.Combine( EnviroSealing.GetAvailability() );
        }

        // now adjust for the era.
        if( Era == Constants.SUCCESSION ) {
            // cut out the Star League stuff.
            AC.Combine( new AvailableCode( IsClan(), 'A', 'X', 'A', 'A', 2801, 10000, 0, "NA", "NA", false, false ) );
        }
        if( Era == Constants.CLAN_INVASION ) {
            // cut out the Star League and Succession Wars stuff.
            AC.Combine( new AvailableCode( IsClan(), 'A', 'X', 'X', 'A', 3051, 10000, 0, "NA", "NA", false, false ) );
        }

        return AC;
    }

    // sorting routine for weapon BV calculation. this is undoubtedly slow
    public abPlaceable[] SortWeapons( Vector v, boolean rear ) {
        // sort by BV first (using gnomesort for less code.  may have to change 
        // this depending on the slowness of the program.  I figure lower overhead
        // will have better results at this time, and mechs typically don't
        // carry more than twelve to fifteen weapons.  I'll have to test this.
        int i = 1, j = 2;
        Object swap;
        while( i < v.size() ) {
            // get the two items we'll be comparing
            if( ((abPlaceable) v.get( i - 1 )).GetCurOffensiveBV( rear ) >= ((abPlaceable) v.get( i )).GetCurOffensiveBV( rear ) ) {
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
            if( ((abPlaceable) v.get( i - 1 )).GetCurOffensiveBV( rear ) == ((abPlaceable) v.get( i )).GetCurOffensiveBV( rear ) ) {
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
    }

    public void SetCapabilities( String n ) {
        Capabilities = n;
    }

    public void SetHistory( String n ) {
        History = n;
    }

    public void SetDeployment( String n ) {
        Deployment = n;
    }

    public void SetVariants( String n ) {
        Variants = n;
    }

    public void SetNotables( String n ) {
        Notables = n;
    }

    public void SetAdditional( String n ) {
        Additional = n;
    }

    public void SetCompany( String n ) {
        Company = n;
    }

    public void SetLocation( String n ) {
        Location = n;
    }

    public void SetEngineManufacturer( String n ) {
        EngineManufacturer = n;
    }

    public void SetArmorModel( String n ) {
        ArmorModel = n;
    }

    public void SetChassisModel( String n ) {
        ChassisModel = n;
    }

    public void SetJJModel( String n ) {
        JJModel = n;
    }

    public void SetCommSystem( String n ) {
        CommSystem = n;
    }

    public void SetTandTSystem( String n ) {
        TandTSystem = n;
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
        if( GetJumpJets().GetNumJJ() < 1 ) {
            return "None";
        } else {
            if( JJModel.matches( "" ) ) {
                return "Unknown";
            }
            return JJModel;
        }
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

    // toString
    @Override
    public String toString() {
        // return Name + " " + Model;
        return "test";
    }
}

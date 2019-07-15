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

import battleforce.*;
import common.CommonTools;
import common.Constants;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.prefs.Preferences;
import states.*;
import visitors.*;

public class CombatVehicle implements ifUnit, ifBattleforce {
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
                   SSWImage = "",
                   Source = "";
    private int HeatSinks = 0,
                Tonnage = 20,
                CruiseMP = 1,
                Crew = 0,
                Year;
    private double JJMult,
                    LiftEquipment = 0,
                    Controls = 0;
    private boolean Omni = false,
                    FractionalAccounting = false,
                    Changed = false,
                    Primitive = false,
                    HasBlueShield = false,
                    HasTurret1 = false,
                    HasTurret2 = false,
                    HasPowerAmplifier = false,
                    UsingFlotationHull = false,
                    UsingLimitedAmphibious = false,
                    UsingFullAmphibious = false,
                    UsingDuneBuggy = false,
                    UsingEnvironmentalSealing = false,
                    IsTrailer = false;
    private static ifCombatVehicle Wheeled = new stCVWheeled(),
                                 Tracked = new stCVTracked(),
                                 Hover = new stCVHover(),
                                 VTOL = new stCVVTOL(),
                                 WiGE = new stCVWiGE(),
                                 Displacement = new stCVDisplacement(),
                                 Hydrofoil = new stCVHydrofoil(),
                                 Submarine = new stCVSubmarine(),
                                 SHHover = new stCVHoverSH(),
                                 SHDisplacement = new stCVDisplacementSH();
    private ifCombatVehicle[] states = new ifCombatVehicle[]{ Wheeled, Tracked, Hover, VTOL, WiGE, Displacement, Hydrofoil, Submarine, SHHover, SHDisplacement};
    private Engine CurEngine = new Engine( this );
    private ifCombatVehicle CurConfig = Tracked;
    private InternalStructure CurStructure = new InternalStructure( this );
    private ArrayList<ifCVLoadout> Loadouts = new ArrayList<ifCVLoadout>();
    private ifCVLoadout MainLoadout,
                        CurLoadout;
    private CVArmor CurArmor = new CVArmor( this );
    private Hashtable Lookup = new Hashtable();
    private ArrayList<MechModifier> MechMods = new ArrayList<MechModifier>();
    private static AvailableCode OmniAvailable = new AvailableCode( AvailableCode.TECH_BOTH ),
                                 DualTurretAC = new AvailableCode( AvailableCode.TECH_BOTH ),
                                 ChinTurretAC = new AvailableCode( AvailableCode.TECH_BOTH ),
                                 SponsoonAC = new AvailableCode( AvailableCode.TECH_BOTH );
    private Preferences Prefs;
    private BattleForceData BFData;
    private MultiSlotSystem BlueShield,
                            EnviroSealing;
    public final static double[] DefensiveFactor = { 1.0, 1.0, 1.1, 1.1, 1.2, 1.2,
        1.3, 1.3, 1.3, 1.4, 1.4, 1.4, 1.4, 1.4, 1.4, 1.4, 1.4, 1.5,
        1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.6, 1.6, 1.6, 1.6, 1.6, 1.6 };

    public CombatVehicle() {
        OmniAvailable.SetCodes( 'E', 'X', 'E', 'E', 'E', 'X', 'E', 'E' );
        OmniAvailable.SetFactions( "", "", "", "", "", "", "", "" );
        OmniAvailable.SetISDates( 0, 0, false, 3010, 0, 0, false, false );
        OmniAvailable.SetCLDates( 0, 0, false, 2854, 0, 0, false, false );
        OmniAvailable.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        DualTurretAC.SetCodes( 'B', 'F', 'X', 'F', 'B', 'X', 'E', 'E' );
        DualTurretAC.SetFactions( "", "", "PS", "", "", "", "PS", "" );
        DualTurretAC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        DualTurretAC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        DualTurretAC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        ChinTurretAC.SetCodes( 'B', 'F', 'F', 'F', 'B', 'X', 'E', 'E' );
        ChinTurretAC.SetFactions( "", "", "PS", "", "", "", "PS", "" );
        ChinTurretAC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        ChinTurretAC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        ChinTurretAC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        SponsoonAC.SetCodes( 'B', 'F', 'X', 'F', 'B', 'X', 'E', 'E' );
        SponsoonAC.SetFactions( "", "", "PS", "", "", "", "PS", "" );
        SponsoonAC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        SponsoonAC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        SponsoonAC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_EXPERIMENTAL, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );

        AvailableCode AC = new AvailableCode( AvailableCode.TECH_INNER_SPHERE );
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

        AC = new AvailableCode( AvailableCode.TECH_BOTH );
        AC.SetISCodes( 'C', 'C', 'C', 'C' );
        AC.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetISFactions( "", "", "PS", "" );
        AC.SetCLCodes( 'C', 'X', 'C', 'C' );
        AC.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        AC.SetCLFactions( "", "", "PS", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_INTRODUCTORY, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        EnviroSealing = new MultiSlotSystem( this, "Environmental Sealing", "Environmental Sealing", "Environmental Sealing", "Environmental Sealing", 0.1, false, false, 225.0, true, AC );
        EnviroSealing.SetWeightBasedOnMechTonnage( true );
        EnviroSealing.SetBookReference( "Tech Manual" );
        EnviroSealing.SetChatName( "EnvSlng" );
        
        HeatSinks = CurEngine.FreeHeatSinks();

        CurLoadout = new CVLoadout(this);
        CurLoadout.GetHeatSinks().SetSingle();
        MainLoadout = CurLoadout;

        BuildLookupTable();
        setTonnage(10);
    }

    public void Visit( ifVisitor v ) throws Exception {
        v.Visit( this );
    }

    public void Validate() {
        if ( getTonnage() > getCurConfig().GetMaxTonnage() ) {
            setTonnage(getCurConfig().GetMaxTonnage());
        }
    }

    public void SetWheeled() {
        setCurConfig(Wheeled);
    }

    public void SetTracked() {
        setCurConfig(Tracked);
    }

    public void SetHover() {
        SetTrailer(false);
        setCurConfig(Hover);
    }

    public void setVTOL() {
        SetTrailer(false);
        setCurConfig(VTOL);
    }

    public void SetWiGE() {
        SetTrailer(false);
        setCurConfig(WiGE);
    }

    public void SetDisplacement() {
        SetTrailer(false);
        setCurConfig(Displacement);
    }

    public void SetHydrofoil() {
        SetTrailer(false);
        setCurConfig(Hydrofoil);
    }

    public void SetSubmarine() {
        SetTrailer(false);
        setCurConfig(Submarine);
    }
    
    public void SetSuperHeavyHover() {
        SetTrailer(false);
        setCurConfig(SHHover);
    }
    
    public void SetSuperHeavyDisplacement() {
        SetTrailer(false);
        setCurConfig(SHDisplacement);
    }

    public String GetMotiveLookupName() {
        return getCurConfig().GetMotiveLookupName();
    }

    public int GetMinTonnage() {
        return getCurConfig().GetMinTonnage();
    }
    
    public int GetMaxTonnage() {
        return getCurConfig().GetMaxTonnage();
    }

    public int GetTonnage() {
        return getTonnage();
    }

    public boolean CanUseJump() {
        return getCurConfig().CanUseJumpMP();
    }

    public boolean CanBeTrailer() {
        return getCurConfig().CanBeTrailer();
    }

    public boolean CanBeDuneBuggy() {
        return getCurConfig().CanBeDuneBuggy();
    }

    public boolean CanUseEnviroSealing() {
        return getCurConfig().CanUseEnviroSealing();
    }
    
    public int GetSuspensionFactor() {
        return getCurConfig().GetSuspensionFactor( Tonnage );
    }

    public float GetMinEngineWeight() {
        return getCurConfig().GetMinEngineWeight( Tonnage );
    }

    public float GetLiftEquipmentCostMultiplier() {
        return getCurConfig().GetLiftEquipmentCostMultiplier();
    }

    public boolean RequiresLiftEquipment() {
        return getCurConfig().RequiresLiftEquipment();
    }

    public double GetLiftEquipmentTonnage()
    {
        return LiftEquipment;
    }
    
    public double GetLiftEquipmentCost() {
        return GetLiftEquipmentCostMultiplier() * LiftEquipment;
    }

    public double GetControls()
    {
        return Controls;
    }
    
    public double GetControlsCost() {
        return 10000 * Controls;
    }
    
    public boolean IsVTOL() {
        return getCurConfig().IsVTOL();
    }
    
    public boolean IsNaval() {
        return ( (CurConfig instanceof stCVDisplacement) || 
                 (CurConfig instanceof stCVHydrofoil) || 
                 (CurConfig instanceof stCVSubmarine) ) ? true : false;
    }
    
    public boolean IsGround() {
        return ( ( CurConfig instanceof stCVHover ) ||
                 ( CurConfig instanceof stCVTracked ) || 
                 ( CurConfig instanceof stCVWheeled ) ||
                 ( CurConfig instanceof stCVWiGE ) ) ? true : false;
    }

    public boolean CanUseTurret() {
        if( IsVTOL() && CommonTools.IsAllowed( ChinTurretAC,this) ) { return true; }
        return getCurConfig().CanUseTurret();
    }

    public boolean CanUseDualTurret() {
        if( CommonTools.IsAllowed( DualTurretAC,this) ) { return true; }
        return false;
    }

    public boolean CanUseSponsoon() {
        if( CommonTools.IsAllowed( SponsoonAC,this) ) { return true; }
        return false;
    }

    public boolean CanUseFlotationHull() {
        return getCurConfig().CanUseFlotationHull();
    }

    public boolean CanUseArmoredMotiveSystem() {
        return getCurConfig().CanUseArmoredMotiveSystem();
    }

    public boolean CanUseAmphibious() {
        return getCurConfig().CanUseAmphibious();
    }

    public boolean CanUseMinesweeper() {
        return getCurConfig().CanUseMinesweeper();
    }

    public CVArmor GetArmor() {
        return getCurArmor();
    }

    public ArrayList GetLoadouts() {
        return Loadouts;
    }
    
    public ifCVLoadout GetMainLoadout() {
        return getMainLoadout();
    }

    public boolean IsOmni() {
        return isOmni();
    }

    public int GetBaseRulesLevel() {
        return MainLoadout.GetRulesLevel();
    }
    
    public Engine GetEngine() {
        return getCurEngine();
    }

    public double GetEngineTonnage()
    {
        return Math.max(CurEngine.GetTonnage(), CurConfig.GetMinEngineWeight( Tonnage ));
    }

    public int GetYear() {
        return getCurLoadout().GetYear();
    }

    public int GetCurrentBV() {
        // returns the final battle value of the combat vehicle
        return (int) Math.round(( GetDefensiveBV() + GetOffensiveBV() ) );
    }

    public double GetTotalCost() {
        // final cost calculations
        // 8/7/2012 Commented out the GetAmmoCosts addition to match the way Mech works - GKB
        return (( GetChassisCost() + GetEquipCost() ) * GetCostMult() * GetConfigMultiplier()); //+ GetAmmoCosts();
    }
    
    public double GetDryCost() {
        // returns the total cost of the mech without ammunition
        return ( GetEquipCost() + GetChassisCost() ) * GetCostMult() * GetConfigMultiplier();
    }
    
    public double GetConfigMultiplier() {
        return (1 + ((double)Tonnage / (double)CurConfig.GetCostMultiplier()));
    }
    
    public double GetCurrentTons() {
        // returns the current total tonnage of the combat vehicle
        double result = 0.0;
        result += GetEngineTonnage();
        result += CurStructure.GetTonnage();
        result += LiftEquipment;
        result += Controls;
        result += GetJumpJets().GetTonnage();
        result += CurArmor.GetTonnage();
        result += GetHeatSinks().GetTonnage();
        result += CurLoadout.GetPowerAmplifier().GetTonnage();
        if ( isHasTurret1() ) result += CurLoadout.GetTurretTonnage();
        if ( isHasTurret2() ) result += CurLoadout.GetRearTurretTonnage();
        result += GetLimitedAmphibiousTonnage();
        result += GetFullAmphibiousTonnage();
        result += GetEnvironmentalSealingTonnage();
        //if( HasBlueShield ) { result += BlueShield.GetTonnage(); }
        if( CurLoadout.HasSupercharger() ) { result += CurLoadout.GetSupercharger().GetTonnage(); }

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
        result += CurStructure.GetTonnage();
        result += CurEngine.GetTonnage();
        result += LiftEquipment;
        result += Controls;
        result += GetHeatSinks().GetTonnage();
        result += GetJumpJets().GetTonnage();
        result += CurArmor.GetTonnage();
        if( CurLoadout.UsingTC() ) { result += GetTC().GetTonnage(); }
        if( ! CurEngine.IsNuclear() ) { result += CurLoadout.GetPowerAmplifier().GetTonnage(); }
        if( HasBlueShield ) { result += BlueShield.GetTonnage(); }
        if( CurLoadout.HasSupercharger() ) { result += CurLoadout.GetSupercharger().GetTonnage(); }
        if( UsingEnvironmentalSealing ) { result += EnviroSealing.GetTonnage(); }

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

    public boolean UsingFractionalAccounting() {
        return isFractionalAccounting();
    }

    public void SetFractionalAccounting( boolean b ) {
        setFractionalAccounting(b);
    }

    public void SetChanged( boolean b ) {
        setChanged(b);
    }

    public boolean HasChanged() {
        return isChanged();
    }

    public void AddHeatSinks( int i ) {

    }

    public void SetHeatSinks( int i ) {

    }

    public void RemoveHeatSinks( int i ) {
        // be sure to check against the engine's free heat sinks!
    }

    public int NumHeatSinks() {
        return 0;
    }

    public double GetHeatSinkTonnage() {
        // remember to not include the engine's free sinks.
        return 0.0;
    }
    
    public void ResetHeatSinks() {
        CurLoadout.ResetHeatSinks();
    }

    public boolean UsingTurret1() {
        return isHasTurret1();
    }

    public boolean UsingTurret2() {
        return isHasTurret2();
    }

    //Battleforce Specific Methods
    /**
     * Determines the size of the unit from pg. 356 of Strategic Operations
     * @return 
     */
    public int GetBFSize() {
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

        /*
        int mass = GetTonnage();
        if ( mass < 5.0 ) 
            return BFConstants.BF_SIZE_SMALL;
        
        //Hover
        if ( "h".equals(CurConfig.GetBFMotiveType())) {
            if ( mass <= 50 )
                return BFConstants.BF_SIZE_MEDIUM;
            return BFConstants.BF_SIZE_LARGE;
        }
        
        //Naval and Submarines
        if ( "n".equals(CurConfig.GetBFMotiveType()) || 
             "s".equals(CurConfig.GetBFMotiveType())) {
            if ( mass <= 300)
                return BFConstants.BF_SIZE_MEDIUM;
            if ( mass <= 6000)
                return BFConstants.BF_SIZE_LARGE;
            if ( mass <= 30000)
                return BFConstants.BF_SIZE_VERYLARGE;
            return BFConstants.BF_SIZE_SUPERLARGE;
        }
        
        //Tracked
        if ( "t".equals(CurConfig.GetBFMotiveType())) {
            if ( mass <= 100 )
                return BFConstants.BF_SIZE_MEDIUM;
            return BFConstants.BF_SIZE_LARGE;
        }
        
        //VTOL
        if ( "v".equals(CurConfig.GetBFMotiveType())) {
            if ( mass <= 30)
                return BFConstants.BF_SIZE_MEDIUM;
            return BFConstants.BF_SIZE_LARGE;
        }
        
        //Wheeled
        if ( "w".equals(CurConfig.GetBFMotiveType())) {
            if ( mass <= 80)
                return BFConstants.BF_SIZE_MEDIUM;
            return BFConstants.BF_SIZE_LARGE;
        }
        
        //WiGE
        if ( "g".equals(CurConfig.GetBFMotiveType())) {
            if ( mass <= 80)
                return BFConstants.BF_SIZE_MEDIUM;
            return BFConstants.BF_SIZE_LARGE;
        }
        
        return BFConstants.BF_SIZE_SMALL;
        */
    }

    public int GetBFPrimeMovement() {
        double retval = getCruiseMP();
        return (int) Math.round(retval);
    }

    public String GetBFPrimeMovementMode() {
        int walkMP = getCruiseMP();
        int jumpMP = GetJumpJets().GetNumJJ();

        if ( walkMP == jumpMP && GetBFPrimeMovement() == jumpMP ){
            if ( GetBFSecondaryMovementMode().isEmpty() ) {
                return "j";
            } else {
                return CurConfig.GetBFMotiveType();
            }
        }else{
            return CurConfig.GetBFMotiveType();
        }
    }

    public int GetBFSecondaryMovement() {
        int baseMP = getCruiseMP();
        int walkMP = GetBFPrimeMovement();
        int jumpMP = GetJumpJets().GetNumJJ();

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

    public String GetBFSecondaryMovementMode() {
        int walkMP = GetBFPrimeMovement();
        int jumpMP = GetJumpJets().GetNumJJ();

        if ( jumpMP > 0 && walkMP != jumpMP )
            return "j";
        else if ( walkMP < jumpMP )
            return "j";
        else
            return "";
    }

    public int GetBFArmor() {
        CVArmor a = GetArmor();
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
        int s = CurStructure.NumCVSpaces();
        int retval = s * 4;
        if ( isHasTurret1() ) retval += s;
        if ( isHasTurret2() ) retval += s;
        if ( IsVTOL() ) retval += s;
        
        return (int)Math.round(retval / 10.0);
    }

    public int[] GetBFDamage(BattleForceStats bfs) {
        int[] retval = {0,0,0,0,0};
        int CoolantPods = 0;

        // Loop through all weapons in non-core
        // and convert all weapon dmg
        ArrayList nc = GetLoadout().GetNonCore();
        BattleForceData baseData = new BattleForceData();
        BattleForceData turret1Data = new BattleForceData();
        BattleForceData turret2Data = new BattleForceData();
        BFData = new BattleForceData();
        
        boolean isTurret1 = false;
        boolean isTurret2 = false;

        BFData.AddNote("Weapon  :: Short / Medium / Long / Extreme / [Heat]" );
        for ( int i = 0; i < nc.size(); i++ ) {
            if ( nc.get(i) instanceof ifWeapon ) {
                ifWeapon w = (ifWeapon)nc.get(i);
                double [] temp = BattleForceTools.GetDamage(w, (ifBattleforce)this);
                BFData.AddBase(temp);
                
                isTurret1 = ( GetLoadout().Find((abPlaceable)nc.get(i)) == LocationIndex.CV_LOC_TURRET1);
                isTurret2 = ( GetLoadout().Find((abPlaceable)nc.get(i)) == LocationIndex.CV_LOC_TURRET2);
                
                if ( isTurret1 ) turret1Data.AddBase(temp);
                if ( isTurret2) turret2Data.AddBase(temp);
                
                if ( BattleForceTools.isBFAutocannon(w) ) {
                    BFData.AC.AddBase(temp);
                    if ( isTurret1 ) turret1Data.AC.AddBase(temp);
                    if ( isTurret2) turret2Data.AC.AddBase(temp);
                } else if ( BattleForceTools.isBFLRM(w) ) {
                    BFData.LRM.AddBase(temp);
                    if ( isTurret1 ) turret1Data.LRM.AddBase(temp);
                    if ( isTurret2) turret2Data.LRM.AddBase(temp);
                } else if ( BattleForceTools.isBFSRM(w) ) {
                    BFData.SRM.AddBase(temp);
                    if ( isTurret1 ) turret1Data.SRM.AddBase(temp);
                    if ( isTurret2) turret2Data.SRM.AddBase(temp);
                } else if ( BattleForceTools.isBFSRT(w) ||
                            BattleForceTools.isBFLRT(w) ) {
                    BFData.TOR.AddBase(temp);
                    if ( isTurret1 ) turret1Data.TOR.AddBase(temp);
                    if ( isTurret2) turret2Data.TOR.AddBase(temp);
                } else if ( BattleForceTools.isBFMML(w) )
                {
                    BFData.SRM.AddBase(new double[]{temp[BFConstants.BF_SHORT], temp[BFConstants.BF_MEDIUM]/2.0, 0.0, 0.0, temp[BFConstants.BF_OV]});
                    BFData.LRM.AddBase(new double[]{0.0, temp[BFConstants.BF_MEDIUM]/2.0, temp[BFConstants.BF_LONG], 0.0, temp[BFConstants.BF_OV]} );
                    if ( isTurret1 ) {
                        turret1Data.SRM.AddBase(temp);
                        turret1Data.LRM.AddBase(temp);
                    }
                    if ( isTurret2) {
                        turret2Data.SRM.AddBase(temp);
                        turret2Data.LRM.AddBase(temp);
                    }
                }
                if ( BattleForceTools.isBFIF(w) ) {
                    BFData.IF.AddBase(temp);
                    if ( isTurret1 ) turret1Data.IF.AddBase(temp);
                    if ( isTurret2) turret2Data.IF.AddBase(temp);
                } if ( BattleForceTools.isBFFLK(w) ) {
                    BFData.FLK.AddBase(temp);
                    if ( isTurret1 ) turret1Data.FLK.AddBase(temp);
                    if ( isTurret2) turret2Data.FLK.AddBase(temp);
                }
                BFData.AddNote(nc.get(i).toString() + " :: " + temp[BFConstants.BF_SHORT] + "/" + temp[BFConstants.BF_MEDIUM] + "/" + temp[BFConstants.BF_LONG] + "/" + temp[BFConstants.BF_EXTREME] + " [" + temp[BFConstants.BF_OV] + "]" );
            } else if ( nc.get(i) instanceof Equipment ) {
                Equipment equip = ((Equipment) nc.get(i));
                if ( equip.CritName().contains("Coolant Pod")) {
                    CoolantPods++;
                }
            }
        }

        // Subtract 4 because Joel says so...
        // and besides, Joel is awesome and we should trust him
        BFData.AddHeat(-4);
        turret1Data.AddHeat(-4);
        turret2Data.AddHeat(-4);

        // Also include Stealth heat, which is ALWAYS on in BF
        if ( GetArmor().IsStealth() ) {
            BFData.AddHeat(10);
            turret1Data.AddHeat(10);
            turret2Data.AddHeat(10);
        }

        BFData.SetHeat(BFData.getTotalHeatGenerated());
        turret1Data.SetHeat(turret1Data.getTotalHeatGenerated());
        turret2Data.SetHeat(turret2Data.getTotalHeatGenerated());
        
        BFData.CheckSpecials();
        turret1Data.CheckSpecials();
        turret2Data.CheckSpecials();

        // Convert all damage to BF scale
        retval[BFConstants.BF_SHORT] = BFData.AdjBase.getBFShort(); //(int) Math.ceil(dmgShort / 10);
        retval[BFConstants.BF_MEDIUM] = BFData.AdjBase.getBFMedium(); //(int) Math.ceil(dmgMedium / 10);
        retval[BFConstants.BF_LONG] = BFData.AdjBase.getBFLong(); //(int) Math.ceil(dmgLong / 10);
        retval[BFConstants.BF_EXTREME] = 0;   // Vehicles dont have extreme range ever

        if ( HasTurret1 ) {
            ArrayList<String> data = new ArrayList<String>();
            if ( turret1Data.AdjBase.getBFShort() > 0 ||
                 turret1Data.AdjBase.getBFMedium()> 0 ||
                 turret1Data.AdjBase.getBFLong()> 0) data.add(turret1Data.AdjBase.GetAbility());
            if ( turret1Data.AC.CheckSpecial() ) data.add("AC " + turret1Data.AC.GetAbility());
            if ( turret1Data.SRM.CheckSpecial() ) data.add("SRM " + turret1Data.SRM.GetAbility());
            if ( turret1Data.LRM.CheckSpecial() ) data.add("LRM " + turret1Data.LRM.GetAbility());
            if ( turret1Data.TOR.CheckSpecial() ) data.add("TOR " + turret1Data.TOR.GetAbility());
            if ( turret1Data.IF.CheckSpecial() ) data.add("IF " + turret1Data.IF.GetAbility());
            if ( turret1Data.FLK.CheckSpecial() ) data.add("FLK " + turret1Data.FLK.GetAbility());
            bfs.addAbility("TUR(" + data.toString().replace("\\[", "").replace("\\]", "") + ")");
        }
        
        // Add Special Abilities to BattleForceStats if applicable
        if ( BFData.AC.CheckSpecial() ) bfs.addAbility("AC " + BFData.AC.GetAbility() );
        if ( BFData.SRM.CheckSpecial() ) bfs.addAbility("SRM " + BFData.SRM.GetAbility() );
        if ( BFData.LRM.CheckSpecial() ) bfs.addAbility("LRM " + BFData.LRM.GetAbility() );
        if ( BFData.TOR.CheckSpecial() ) bfs.addAbility("TOR " + BFData.TOR.GetAbility() );
        if ( BFData.IF.getBFLong() > 0 )  bfs.addAbility("IF " + BFData.IF.getBFLong() );
        if ( BFData.FLK.getBaseMedium() > 5 ) bfs.addAbility("FLK " + BFData.FLK.GetAbility() );
            
        // Determine OverHeat
        if ( BFData.BaseMaxMedium() != 0 )
        {
            int DmgMedium = BFData.AdjBase.getBFMedium() + BFData.SRM.getBFMedium() + BFData.LRM.getBFMedium() + BFData.AC.getBFMedium();
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
        
        //Determine Long Overheat OVL
        if ( BFData.BaseMaxLong() != 0 ) {
            
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

    public ArrayList GetBFAbilities() {
        ArrayList<String> retval = new ArrayList();

        // First search all equipment for BF Abilities
        ArrayList nc = GetLoadout().GetNonCore();
        boolean isENE = true,
                hasExplodable = false;
        int Taser = 0,
            RSD = 0;
        double MHQTons = 0;
        int Heat = 0;

        //Underwater Movement
        if ( GetJumpJets().IsUMU() ) {
            if ( !retval.contains("UMU") ) retval.add("UMU");
        }

        //Omni
        if ( IsOmni() )
            if ( !retval.contains("OMNI") ) retval.add("OMNI");

        //Stealth (also adds ECM)
        if ( (GetArmor().IsStealth()) ) {
            if ( !retval.contains("ECM") ) retval.add("ECM");
            if ( !retval.contains("STL") ) retval.add("STL");
        }

        for ( int i = 0; i < nc.size(); i++ ) {
            abPlaceable item = (abPlaceable)nc.get(i);

            // Get the list of abilities from the equipment itself
            // handle special cases like C3
            String[] abilities = item.GetBattleForceAbilities();
            for ( String ability : abilities ) {

                if ( ability.equals("C3M") ) MHQTons += 5.0d;
                if ( ability.equals("C3I") ) MHQTons += 2.5d;
                if ( ability.equals("C3BM") ) MHQTons += 6.0d;
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

        if ( CurEngine.IsICE() || CurEngine.isFuelCell() ) {
            retval.add("EE");
        }

        return retval;
    }

    public String GetBFConversionStr() {
        String retval = "Weapon\t\t\tShort\tMedium\tLong\n\r";
        //TODO Add in conversion steps if possible
        return retval;
    }

    public int GetBFPoints() {
        return Math.round( GetCurrentBV() / 100.0f );
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
            }
        }
        return retval;
    }
    
    public int GetUnitType() {
        return AvailableCode.UNIT_COMBATVEHICLE;
    }

    public int GetRulesLevel() {
        return getCurLoadout().GetRulesLevel();
    }

    public int GetTechbase() {
        return getCurLoadout().GetTechBase();
    }
 
   public int GetBaseTechbase() {
        return getMainLoadout().GetTechBase();
    }

    public int GetEra() {
        return getCurLoadout().GetEra();
    }

    public void SetEra( int e ) {
        if( Omni ) {
            CurLoadout.SetEra( e );
        } else {
            MainLoadout.SetEra( e );
        }
    }

    public void SetYear( int y, boolean specified ) {
        if( Omni ) {
            CurLoadout.SetYear( y, specified );
        } else {
            MainLoadout.SetYear( y, specified );
        }
    }
    
    public boolean IsYearRestricted() {
        return getCurLoadout().IsYearRestricted();
    }

    public boolean YearWasSpecified() {
        return getCurLoadout().YearWasSpecified();
    }

    public boolean HasFHES() {
        return false;
    }
    
    public String GetName() {
        return Name;
    }
    
    public void setName(String Name) {
        this.Name = Name;
    }

    public String GetModel() {
        return Model;
    }

    public void setModel(String Model) {
        this.Model = Model;
    }

    public String getOverview() {
        return Overview;
    }

    public void setOverview(String Overview) {
        this.Overview = Overview;
    }

    public String getCapabilities() {
        return Capabilities;
    }

    public void setCapabilities(String Capabilities) {
        this.Capabilities = Capabilities;
    }

    public String getHistory() {
        return History;
    }

    public void setHistory(String History) {
        this.History = History;
    }

    public String getDeployment() {
        return Deployment;
    }

    public void setDeployment(String Deployment) {
        this.Deployment = Deployment;
    }

    public String getVariants() {
        return Variants;
    }

    public void setVariants(String Variants) {
        this.Variants = Variants;
    }

    public String getNotables() {
        return Notables;
    }

    public void SetNotables(String Notables) {
        this.Notables = Notables;
    }

    public String GetAdditional() {
        return Additional;
    }

    public void SetAdditional(String Additional) {
        this.Additional = Additional;
    }

    public String GetCompany() {
        return Company;
    }

    public void SetCompany(String Company) {
        this.Company = Company;
    }

    public String GetLocation() {
        return Location;
    }

    public void SetLocation(String Location) {
        this.Location = Location;
    }

    public String GetEngineManufacturer() {
        return EngineManufacturer;
    }

    public void SetEngineManufacturer(String EngineManufacturer) {
        this.EngineManufacturer = EngineManufacturer;
    }

    public String GetArmorModel() {
        return ArmorModel;
    }

    public void SetArmorModel(String ArmorModel) {
        this.ArmorModel = ArmorModel;
    }

    public String GetChassisModel() {
        return ChassisModel;
    }

    public void SetChassisModel(String ChassisModel) {
        this.ChassisModel = ChassisModel;
    }

    public String GetJJModel() {
        return JJModel;
    }

    public void SetJJModel(String JJModel) {
        this.JJModel = JJModel;
    }

    public String GetCommSystem() {
        return CommSystem;
    }

    public void SetCommSystem(String CommSystem) {
        this.CommSystem = CommSystem;
    }

    public String GetTandTSystem() {
        return TandTSystem;
    }

    public void SetTandTSystem(String TandTSystem) {
        this.TandTSystem = TandTSystem;
    }

    public String GetSolaris7ID() {
        return Solaris7ID;
    }

    public void SetSolaris7ID(String Solaris7ID) {
        this.Solaris7ID = Solaris7ID;
    }

    public String GetSolaris7ImageID() {
        return Solaris7ImageID;
    }

    public void SetSolaris7ImageID(String Solaris7ImageID) {
        this.Solaris7ImageID = Solaris7ImageID;
    }

    public String GetSSWImage() {
        return SSWImage;
    }

    public void SetSSWImage(String SSWImage) {
        this.SSWImage = SSWImage;
    }

    public int getTonnage() {
        return Tonnage;
    }

    public final void setTonnage(int Tonnage) {
        this.Tonnage = Tonnage;
        LiftEquipment = 0;
        Controls = 0;
        
        if ( CurConfig.RequiresLiftEquipment() )
            this.LiftEquipment = CommonTools.RoundHalfUp((double)Tonnage / 10.0);
        if( CurEngine.RequiresControls() )
            this.Controls = CommonTools.RoundHalfUp((double)Tonnage * 0.05);
        SetEngine(CurEngine);
    }

    public InternalStructure GetIntStruc() {
        return CurStructure;
    }

    public double getInternalStructure() {
        return CurStructure.GetTonnage();
    }

    public int getInternalStructurePoints() {
        return CurStructure.NumCVSpaces();
    }

    public int getMaxItems()
    {
        return 5 + ( Math.round(Tonnage / 5) );
    }
    
    public int getLocationCount(boolean includeRotor) {
        int retval = 4;
        if ( isHasTurret1() ) retval += 1;
        if ( isHasTurret2() ) retval += 1;
        if ( includeRotor && IsVTOL() ) retval += 1;
        return retval;        
    }
    
    public int getLocationCount() {
        return getLocationCount(false);       
    }

    public int getCruiseMP() {
        return CruiseMP;
    }

    public int getMaxCruiseMP() {
        if( CurEngine.IsPrimitive() ) {
            return (int) Math.floor( ( ( 400.0 + (double)CurConfig.GetSuspensionFactor(Tonnage) ) / (double)Tonnage ) / 1.2 );
        } //else if( CurEngine.CanSupportRating( 500, this ) ) {
            //return (int) Math.floor( 500 / Tonnage );
        //} 
        else {
            return (int) Math.floor( ( 400.0 + (double)CurConfig.GetSuspensionFactor(Tonnage) ) / (double)Tonnage   );
        }
    }
    
    public int getMinCruiseMP() {
        if ( CurEngine.RequiresControls() && !IsTrailer ) return 1;
        return 0;
    }

    public void setCruiseMP(int mp) throws Exception {
        int MaxWalk = getMaxCruiseMP();
        if( mp > MaxWalk ) { mp = MaxWalk; }
        if( mp < 0 ) { mp = 0; }
        CruiseMP = mp;
        CurEngine.SetRating( GetFinalEngineRating() );
    }

    public double getJJMult() {
        return JJMult;
    }

    public void setJJMult(double JJMult) {
        this.JJMult = JJMult;
    }

    public boolean isOmni() {
        return Omni;
    }

    public void SetOmni( String name ) {
        // this performs everything needed to turn the mech into an omni
        Omni = true;

        // remove any targeting computers from the base chassis.  they vary too
        // much to be fixed equipment
        UseTC( false, false );

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
        ifCVLoadout l = MainLoadout.Clone();
        l.SetName( name );
        Loadouts.add( l );
        CurLoadout = l;
    }

    public void UnlockChassis() {
        // before we unlock, clear out all the loadouts except for Main
        Loadouts.clear();
        Omni = false;
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
            if( ((ifCVLoadout) Loadouts.get( i )).GetName().equals( Name ) ) {
                throw new Exception( "Could not add the new loadout because\nthe name given matches an existing loadout." );
            }
        }

        ifCVLoadout l = MainLoadout.Clone();
        l.SetName( Name );
        Loadouts.add( l );
        CurLoadout = l;
    }

    public void RemoveLoadout( String Name ) {
        // removes the given loadout from the loadout ArrayList.  if the ArrayList is
        // empty (non-omnimech) nothing is done.
        for( int i = 0; i < Loadouts.size(); i++ ) {
            if( ((ifCVLoadout) Loadouts.get( i )).GetName().equals( Name ) ) {
                // remove it
                Loadouts.remove( i );
                break;
            }
        }

        // now set the current loadout to the first
        if( Loadouts.size() > 0 ) {
            CurLoadout = (ifCVLoadout) Loadouts.get(0);
        } else {
            CurLoadout = MainLoadout;
        }
    }

    public boolean isFractionalAccounting() {
        return FractionalAccounting;
    }

    public void setFractionalAccounting(boolean FractionalAccounting) {
        this.FractionalAccounting = FractionalAccounting;
    }

    public boolean isChanged() {
        return Changed;
    }

    public void setChanged(boolean Changed) {
        this.Changed = Changed;
    }

    public boolean isHasTurret1() {
        return HasTurret1;
    }

    public void setHasTurret1(boolean HasTurret1) {
        this.HasTurret1 = HasTurret1;
        //Move any weapons/equipment that was in the turret to another location
        if (!HasTurret1) {
            GetLoadout().SetTurret1(new ArrayList<abPlaceable>());
            GetLoadout().RefreshHeatSinks();
            CurArmor.SetArmor(LocationIndex.CV_LOC_TURRET1, 0);
        }
    }

    public boolean isHasTurret2() {
        return HasTurret2;
    }

    public void setHasTurret2(boolean HasTurret2) {
        this.HasTurret2 = HasTurret2;
        //Move any weapons/equipment that was in the turret to another location
        if (!HasTurret2) {
            GetLoadout().SetTurret2(new ArrayList<abPlaceable>());
            GetLoadout().RefreshHeatSinks();
            CurArmor.SetArmor(LocationIndex.CV_LOC_TURRET2, 0);
        }
    }

    public Engine getCurEngine() {
        return CurEngine;
    }

    public void setCurEngine(Engine CurEngine) {
        this.CurEngine = CurEngine;
    }

    public ifCombatVehicle getCurConfig() {
        return CurConfig;
    }

    public void setCurConfig(ifCombatVehicle CurConfig) {
        this.CurConfig = CurConfig;
        if (!CurConfig.IsVTOL())
            CurArmor.SetArmor(LocationIndex.CV_LOC_ROTOR, 0);
        setTonnage(Tonnage);
    }

    public ArrayList<ifCVLoadout> getLoadouts() {
        return Loadouts;
    }

    public void setLoadouts(ArrayList<ifCVLoadout> Loadouts) {
        this.Loadouts = Loadouts;
    }

    public ifCVLoadout getMainLoadout() {
        return MainLoadout;
    }

    public void setMainLoadout(ifCVLoadout MainLoadout) {
        this.MainLoadout = MainLoadout;
    }

    public ifCVLoadout getCurLoadout() {
        return CurLoadout;
    }

    public ifCVLoadout GetBaseLoadout() {
        return MainLoadout;
    }

    public void SetCurLoadout( String Name ) {
        // sets the current loadout to the named loadout.
        if( Name.equals( Constants.BASELOADOUT_NAME ) ) {
            CurLoadout = MainLoadout;
            return;
        }

        for( int i = 0; i < Loadouts.size(); i++ ) {
            if( ((ifCVLoadout) Loadouts.get( i )).GetName().equals( Name ) ) {
                CurLoadout = (ifCVLoadout) Loadouts.get( i );
                return;
            }
        }

        // if we got here, there was a problem.  set the loadout to the base
        if( Loadouts.size() > 0 ) {
            CurLoadout = (ifCVLoadout) Loadouts.get(0);
        } else {
            CurLoadout = MainLoadout;
        }
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

    public void UseTC( boolean use, boolean clan ) {
        CurLoadout.UseTC( use, clan );
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

    public CVArmor getCurArmor() {
        return CurArmor;
    }

    public void setCurArmor(CVArmor CurArmor) {
        this.CurArmor = CurArmor;
    }

    public ifVisitor Lookup( String s ) {
        // returns a visitor from the lookup table based on the lookup string
        return (ifVisitor) Lookup.get( s );
    }

    public Hashtable getLookup() {
        return Lookup;
    }

    public void setLookup(Hashtable Lookup) {
        this.Lookup = Lookup;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int y, boolean specified) {      
        if( Omni ) {
            CurLoadout.SetYear( y, specified );
        } else {
            MainLoadout.SetYear( y, specified );
        }
    }

    public void SetYearRestricted( boolean y ) {
        if( Omni ) {
            CurLoadout.SetYearRestricted( y );
        } else {
            MainLoadout.SetYearRestricted( y );
        }
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public int getFlankMP() {
        return CruiseMP == 0 ? 0 : (int) Math.floor( CruiseMP * 1.5 + 0.5 );
    }

    public int getFlankMP( int MiniMult ) {
        return (int) Math.floor( ( getCruiseMP() * MiniMult ) * 1.5 + 0.5 );
    }
    
    public void SetRulesLevel( int r ) {
        if( Omni ) {
            CurLoadout.SetRulesLevel( r );
        } else {
            MainLoadout.SetRulesLevel( r );
        }
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

    public int GetTechBase() {
        return MainLoadout.GetTechBase();
    }

    public boolean SetTechBase( int t ) {
        if( Omni ) {
            if( t != MainLoadout.GetTechBase() && t != AvailableCode.TECH_BOTH ) {
                return false;
            } else {
                CurLoadout.SetTechBase( t );
            }
        } else {
            MainLoadout.SetTechBase( t );
        }
        return true;
    }

    public int GetBaseYear() {
        return MainLoadout.GetYear();
    }

    public CVHeatSinkFactory GetHeatSinks() {
        return CurLoadout.GetHeatSinks();
    }

    public CVJumpJetFactory GetJumpJets() {
        return CurLoadout.GetJumpJets();
    }

    public double GetJJMult() {
        return JJMult;
    }

    public void SetInnerSphere() {
        // performs all the neccesary actions to switch this to Inner Sphere
        // set the tech base
        SetTechBase( AvailableCode.TECH_INNER_SPHERE );

        // clear the loadout
        CurLoadout.ClearLoadout();

        // switch the engine over to a military Standard
        CurEngine.SetICEngine();

        // switch the heat sinks
        GetHeatSinks().SetSingle();

        // switch the jump jets
        GetJumpJets().SetNormal();

        // set the armor type
        CurArmor.SetStandard();

        // replace everything in the loadout
        //CurEngine.Place( CurLoadout );
        GetHeatSinks().ReCalculate();
        //GetJumpJets().ReCalculate();
        CurArmor.Recalculate();
    }

    public void SetClan() {
        // performs all the neccesary actions to switch this to Clan
        // set the tech base
        SetTechBase( AvailableCode.TECH_CLAN );

        // clear the loadout
        CurLoadout.ClearLoadout();

        // switch the engine over to a military Standard Clan
        CurEngine.SetICEngine();

        // switch the heat sinks
        GetHeatSinks().SetSingle();

        // switch the jump jets
        GetJumpJets().SetNormal();

        // set the armor type
        CurArmor.SetStandard();

        // replace everything iun the loadout
        //CurEngine.Place( CurLoadout );
        GetHeatSinks().ReCalculate();
        //GetJumpJets().ReCalculate();
        CurArmor.Recalculate();

    }
    public void SetMixed() {
        SetTechBase( AvailableCode.TECH_BOTH );

        // although nothing should technically be illegal, we'll do it anyway.
        CurLoadout.FlushIllegal();

        // check each component in turn before reseting to a default
         if( ! CommonTools.IsAllowed( CurEngine.GetAvailability(), this ) ) {
            CurEngine.SetICEngine();
            //CurEngine.Place( CurLoadout );
        }

        if( ! CommonTools.IsAllowed( GetHeatSinks().GetAvailability(), this ) ) {
            GetHeatSinks().SetSingle();
            GetHeatSinks().ReCalculate();
        }

        if( ! CommonTools.IsAllowed( GetJumpJets().GetAvailability(), this ) ) {
            //GetJumpJets().SetNormal();
            //GetJumpJets().ReCalculate();
        }

        if( ! CommonTools.IsAllowed( CurArmor.GetAvailability(), this ) ) {
            CurArmor.SetStandard();
            CurArmor.Recalculate();
        }
    }

    public String GetFullName() {
        return String.format("%1$s %2$s %3$s", GetName(), GetModel(), GetLoadout().GetName()).replace(" " + Constants.BASELOADOUT_NAME, "").trim();
    }

    public String GetChatInfo() {
        String info = GetFullName() + " ";
        info += GetTonnage() + "t, ";
        // MP
        info += getCruiseMP();
        //if( getCruiseMP() != GetAdjustedWalkingMP( false, true ) ) {
        //    info += "[" + GetAdjustedWalkingMP( false, true ) + "]";
        //}
        info += "/";
        info += getFlankMP();
        //if( getFlankMP() != GetAdjustedRunningMP( false, true ) ) {
        //    info += "[" + GetAdjustedRunningMP( false, true ) + "]";
        //}
        if ( CurLoadout.GetJumpJets().GetNumJJ() > 0 ) {
            info += "/" + CurLoadout.GetJumpJets().GetNumJJ();
            //if( CurLoadout.GetJumpJets().GetNumJJ() != this.GetAdjustedJumpingMP( false ) ) {
            //    info += "[" + GetAdjustedJumpingMP( false ) + "]";
            //}
        }
        info += ", ";

        // Engine
        info += GetEngine().ChatName() + ", ";

        // Armor
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

        return info.trim().substring( 0, info.length() - 2 );
    }

    public boolean UsingTC() {
        return CurLoadout.UsingTC();
    }

    public TargetingComputer GetTC()
    {
        return CurLoadout.GetTC();
    }

    public void CheckTC() {
        CurLoadout.CheckTC();
    }

    public void UnallocateTC() {
        CurLoadout.UnallocateTC();
    }

    public BattleForceData getBFData() {
        return BFData;
    }

    public boolean HasBlueShield() {
        return HasBlueShield;
    }

    public MultiSlotSystem GetBlueShield() {
        return BlueShield;
    }

    public AvailableCode GetOmniAvailability() {
        return OmniAvailable;
    }

    private void BuildLookupTable() {
        // sets up the lookup hashtable with String keys and ifVisitor values
        Lookup.put( "Standard Armor", new VArmorSetStandard() );
        Lookup.put( "Ferro-Fibrous", new VArmorSetFF() );
        Lookup.put( "(IS) Ferro-Fibrous", new VArmorSetFF() );
        Lookup.put( "(CL) Ferro-Fibrous", new VArmorSetFF() );
        Lookup.put( "Vehicle Stealth Armor", new VArmorSetVStealth() );
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
        Lookup.put( "Ablation Armor", new VArmorSetAB());
        Lookup.put( "Ballistic-Reinforced Armor", new VArmorSetBR());
        Lookup.put( "Patchwork Armor", new VArmorSetPatchwork() );
        Lookup.put( "Standard Structure", new VChassisSetStandard() );
        Lookup.put( "Fuel-Cell Engine", new VEngineSetFuelCell() );
        Lookup.put( "Fission Engine", new VEngineSetFission() );
        Lookup.put( "Fusion Engine", new VEngineSetFusion() );
        //Lookup.put( "Primitive Fuel-Cell Engine", new VEngineSetPrimitiveFuelCell() );
        //Lookup.put( "Primitive Fission Engine", new VEngineSetPrimitiveFission() );
        //Lookup.put( "Primitive Fusion Engine", new VEngineSetPrimitiveFusion() );
        Lookup.put( "XL Engine", new VEngineSetFusionXL() );
        Lookup.put( "(IS) XL Engine", new VEngineSetFusionXL() );
        Lookup.put( "(CL) XL Engine", new VEngineSetFusionXL() );
        Lookup.put( "XXL Engine", new VEngineSetFusionXXL() );
        Lookup.put( "(IS) XXL Engine", new VEngineSetFusionXXL() );
        Lookup.put( "(CL) XXL Engine", new VEngineSetFusionXXL() );
        Lookup.put( "I.C.E. Engine", new VEngineSetICE() );
        //Lookup.put( "Primitive I.C.E. Engine", new VEngineSetPrimitiveICE() );
        Lookup.put( "Compact Fusion Engine", new VEngineSetCompactFusion() );
        Lookup.put( "Light Fusion Engine", new VEngineSetLightFusion() );
        Lookup.put( "No Engine", new VEngineSetNone() );
        Lookup.put( "No Enhancement", new VEnhanceSetNone() );
        Lookup.put( "Single Heat Sink", new VHeatSinkSetSingle() );
        //Lookup.put( "Double Heat Sink", new VHeatSinkSetDouble() );
        //Lookup.put( "(IS) Double Heat Sink", new VHeatSinkSetDouble() );
        //Lookup.put( "(CL) Double Heat Sink", new VHeatSinkSetDouble() );
        //Lookup.put( "Compact Heat Sink", new VHeatSinkSetCompact() );
        //Lookup.put( "Laser Heat Sink", new VHeatSinkSetLaser() );
        Lookup.put( "Standard Jump Jet", new VJumpJetSetStandard() );
        //Lookup.put( "Improved Jump Jet", new VJumpJetSetImproved() );
        //Lookup.put( "Primitive Armor", new VArmorSetPrimitive() );
        //Lookup.put( "Primitive Structure", new VChassisSetPrimitive() );
        //Lookup.put( "Primitive Industrial Structure", new VChassisSetPrimitiveIndustrial() );
        //Lookup.put( "Primitive Cockpit", new VCockpitSetPrimitive() );
        //Lookup.put( "Primitive Industrial Cockpit", new VCockpitSetPrimIndustrial() );
        //Lookup.put( "Primitive Industrial w/ Adv. FC", new VCockpitSetPrimIndustrialAFC() );

        // now to fix all the visitors with counterparts to use Clan tech if needed
        ((ifVisitor) Lookup.get( "(CL) Ferro-Fibrous" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) Laser-Reflective" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) Reactive Armor" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) XL Engine" )).SetClan( true );
        ((ifVisitor) Lookup.get( "(CL) XXL Engine" )).SetClan( true );
        //((ifVisitor) Lookup.get( "(CL) Double Heat Sink" )).SetClan( true );
    }

    public boolean ValidateECM() {
        if( CurArmor.IsStealth() ) {
            return HasECM();
        }
        return true;
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
                    retval.BVCombine( ((MechModifier) MechMods.get( i )) );
                } else {
                    retval.Combine( ((MechModifier) MechMods.get( i )) );
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

    public ArrayList GetMechMods() {
        return MechMods;
    }

    public double GetDefensiveBV() {
        // modify the result by the defensive factor and send it out
        return GetUnmodifiedDefensiveBV() * GetDefensiveModifier() * GetDefensiveFactor();
    }
    
    public double GetDefensiveModifier() {
        return CurConfig.GetDefensiveMultiplier() + GetChassisModifier();
    }
    public double GetChassisModifier() {
        double retval = 0.0;
        if ( UsingFlotationHull ) retval += 0.1;
        if ( UsingLimitedAmphibious ) retval += 0.1;
        if ( UsingFullAmphibious ) retval += 0.2;
        if ( UsingDuneBuggy ) retval += 0.1;
        if ( UsingEnvironmentalSealing ) retval += 0.1;
        return retval;
    }

    public double GetUnmodifiedDefensiveBV() {
        // returns the defensive battle value of the mech
        double defresult = 0.0;

        // defensive battle value calculations start here
        defresult += CurArmor.GetDefensiveBV();
        defresult += CurStructure.GetDefensiveBV();
        defresult += GetDefensiveEquipBV();
        defresult += GetDefensiveExcessiveAmmoPenalty();
        //defresult += GetExplosiveAmmoPenalty();
        //defresult += GetExplosiveWeaponPenalty();
        if( defresult < 1.0 ) {
            defresult = 1.0;
        }

        return defresult;
    }
    
    public String GetChassisModifierString() {
        StringBuilder b = new StringBuilder();
        if ( UsingFlotationHull ) b.append(", Flotation Hull");
        if ( UsingLimitedAmphibious ) b.append(", Limited Amphibious");
        if ( UsingFullAmphibious ) b.append(", Full Amphibious");
        if ( UsingDuneBuggy ) b.append(", Dune Buggy");
        if ( UsingEnvironmentalSealing && !(CurConfig instanceof stCVSubmarine) ) b.append(", Environmental Sealing");
        if ( b.length() > 0 ) return  "(" + b.toString().substring(2) + ")";
        return "";
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
            if( HasBlueShield() ) {
                result += BlueShield.GetDefensiveBV();
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
        }

        // check for Blue Shield system.
        if( HasBlueShield ) {
            if( CurEngine.IsISXL() ) {
                result -= 3.0;
            }
        }
        return result;
    }

    public double GetDefensiveFactor() {
        // returns the defensive factor for this mech based on it's highest
        // target number for speed.

        // subtract one since we're indexing an array
        int RunMP = getFlankMP() - 1;
        int JumpMP = 0;

        // this is a safeguard for using MASC on an incredibly speedy chassis
        // there is currently no way to get a bonus higher anyway.
        if( RunMP > 29 ) { RunMP = 29; }
        // safeguard for low walk mp (Modular MechArmor, for instance)
        if( RunMP < 0 ) { RunMP = 0; }

        // Get the defensive factors for jumping and running movement
        double ground = DefensiveFactor[RunMP];
        
        //VTOL's get an extra .1
        if ( IsVTOL() ) ground += .1;
        
        //Stealth Armor gets an extra .2
        if ( GetArmor().IsStealth() ) ground += .2;
        
        double jump = 0.0;
        if( GetJumpJets().GetNumJJ() > 0 ) {
            JumpMP = GetJumpJets().GetNumJJ() - 1;
            jump = DefensiveFactor[JumpMP] + 0.1;
        }

        double retval = 0.0;
        // return the best one.
        if( jump > ground ) {
            retval = jump;
        } else {
            retval = ground;
        }

        return retval;
    }

    public double GetOffensiveBV() {
        // returns the offensive battle value of the mech
        return GetUnmodifiedOffensiveBV() * GetOffensiveFactor();
    }

    public double GetUnmodifiedOffensiveBV() {
        double offresult = 0.0;

        offresult += GetWeaponBV();
        offresult += GetNonHeatEquipBV();
        offresult += GetExcessiveAmmoPenalty();
        offresult += GetTonnageBV();
        return offresult;
    }

    public double GetWeaponBV() {
        double result = 0.0, foreBV = 0.0, rearBV = 0.0;
        boolean UseRear = false, TC = CurLoadout.UsingTC();
        
        ArrayList<ArrayList<abPlaceable>> FrontRear = new ArrayList<ArrayList<abPlaceable>>();
        FrontRear.add(CurLoadout.GetFrontItems());
        FrontRear.add(CurLoadout.GetRearItems());
        
        ArrayList<ArrayList<abPlaceable>> Locations = new ArrayList<ArrayList<abPlaceable>>();
        Locations.add(CurLoadout.GetLeftItems());
        Locations.add(CurLoadout.GetRightItems());
        Locations.add(CurLoadout.GetTurret1Items());
        Locations.add(CurLoadout.GetTurret2Items());
        
        // is it even worth performing all this?
        if( CurLoadout.GetNonCore().size() <= 0 ) {
            // nope
            return result;
        }

        // find out the total BV of rear and forward firing weapons
        for ( abPlaceable w : CurLoadout.GetFrontItems() ) {
            if ( w instanceof ifWeapon )
                foreBV += w.GetCurOffensiveBV(false, TC, false);
        }
        for ( abPlaceable w : CurLoadout.GetRearItems() ) {
            if ( w instanceof ifWeapon )
                rearBV += w.GetCurOffensiveBV(true, TC, false);
        }
        if( rearBV > foreBV ) { UseRear = true; }
        
        //Re-calculate values now based on rear adjustment
        for ( ArrayList<abPlaceable> list : FrontRear ) {
            for ( abPlaceable w : list ) {
                if ( w instanceof ifWeapon)
                    result += w.GetCurOffensiveBV(UseRear, TC, false);
            }
        }
        
        //Sides and Turrets are full value no matter what
        for ( ArrayList<abPlaceable> list : Locations ) {
            for ( abPlaceable w : list ) {
                if ( w instanceof ifWeapon)
                    result += w.GetCurOffensiveBV(false, TC, false);
            }
        }
        
        return result;
    }

    public double GetNonHeatEquipBV() {
        // return the BV of all offensive equipment
        double result = 0.0;
        ArrayList v = CurLoadout.GetNonCore();

        for( int i = 0; i < v.size(); i++ ) {
            if( ! ( v.get( i ) instanceof ifWeapon ) ) {
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
        return (double)Tonnage / 2.0;
    }

    public double GetOffensiveFactor() {
        double result = 0.0;
        result += (double) (getFlankMP() - 5.0f);
        result = result * 0.1 + 1.0;
        result = (double) Math.pow( result, 1.2 ) ;

        // round off to the nearest two digits
        result = (double) Math.floor( result * 100 + 0.5 ) / 100;

        double cockpitMultiplier = 1.0;

        return result * cockpitMultiplier;
    }

    public boolean SetEngine( Engine e ) {
        CurEngine = e;

        if ( !e.RequiresControls() ) {
            CruiseMP = 0;
        } else {
            if ( CruiseMP == 0 ) 
                CruiseMP = 1;
        }
        CurEngine.SetRating( GetFinalEngineRating() );

        if ( e.FreeHeatSinks() > GetHeatSinks().GetNumHS() )
            GetHeatSinks().SetNumHS(e.FreeHeatSinks());

        return true;
    }

    public int GetAvailableSlots()
    {
        int retval = getMaxItems();
        retval -= CurEngine.NumCVSpaces();
        retval -= CurArmor.NumCVSpaces();
        for ( abPlaceable a : (ArrayList<abPlaceable>)GetLoadout().GetNonCore() ) {
            retval -= a.NumCVSpaces();
        }

        return retval;
    }

    public int GetBaseEngineRating()
    {
        return Tonnage * CruiseMP;
    }

    public int GetFinalEngineRating()
    {
        return Math.min(Math.max( (int)(CommonTools.RoundHalfUp( (GetBaseEngineRating() - CurConfig.GetSuspensionFactor(Tonnage))*.1 ) * 10) , CurEngine.GetTonnage() == 0 ? 0 : 10), 400);
    }

    public AvailableCode GetAvailability() {
        // returns the availability code for this mech based on all components
        AvailableCode Base = new AvailableCode( CurLoadout.GetTechBase() );
        Base.SetCodes( 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A' );
        Base.SetISDates( 0, 0, false, 1950, 0, 0, false, false );
        Base.SetCLDates( 0, 0, false, 1950, 0, 0, false, false );
        Base.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        if( Omni ) {
            Base.Combine( OmniAvailable );
        }

        // combine the availability codes from all equipment
        Base.Combine( CurEngine.GetAvailability() );
        Base.Combine( new stChassisMSBP().GetAvailability() );
        Base.Combine( GetHeatSinks().GetAvailability() );
        if( GetJumpJets().GetNumJJ() > 0 ) {
            Base.Combine( GetJumpJets().GetAvailability() );
        }
        Base.Combine( CurArmor.GetAvailability() );
        if( CurLoadout.UsingTC() ) {
            Base.Combine( GetTC().GetAvailability() );
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
            boolean AES1 = false;
            boolean AES2 = false;
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
                boolean AES1 = false;
                boolean AES2 = false;
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

    public void SetPrimitive(boolean value) {
        Primitive = value;
    }

    public boolean IsPrimitive() {
        return Primitive;
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

    public double GetBaseChassisCost() {
        // chassis cost in this context is different than the ChassisCost
        // variable.  It includes all components except engine, TC, and
        // equipment without multiple calculation ("base" cost)

        double result = 0.0;
        result += CurStructure.GetCost();
        result += GetControlsCost();
        result += GetLiftEquipmentCost();
        result += GetHeatSinks().GetCost();
        result += GetJumpJets().GetCost();
        result += CurArmor.GetCost();
        if ( isHasTurret1() ) result += getCurLoadout().GetTurret().GetCost();
        if ( isHasTurret2() ) result += getCurLoadout().GetRearTurret().GetCost();
        if ( HasPowerAmplifier ) result += getCurLoadout().GetPowerAmplifier().GetCost();

        return result;
    }
    
    public double GetChassisCost() {
        // this method sets the cost variable by calculating the base cost.
        // this is usually only done whenever a chassis component changes.
        double result = GetBaseChassisCost();
        result += CurEngine.GetCost();

        if( UsingLimitedAmphibious ) { result += 10000.0 * GetLimitedAmphibiousTonnage(); }
        if( UsingFullAmphibious ) { result += 10000.0 * GetFullAmphibiousTonnage(); }
        if( UsingDuneBuggy ) { result += 10 * Tonnage * Tonnage; }
        if( HasBlueShield() ) { result += BlueShield.GetCost(); }

        // same goes for the targeting computer and supercharger
        if( CurLoadout.UsingTC() ) {
            //result += GetTC().GetCost();
        }
        if( CurLoadout.HasSupercharger() ) {
            result += CurLoadout.GetSupercharger().GetCost();
        }

        return result;
    }

    public double GetCostMult() {
        double retval = 1.0;
        if ( Omni ) retval *= 1.25;
        if ( UsingFlotationHull ) retval *= 1.25;
        if ( UsingEnvironmentalSealing && !(CurConfig instanceof stCVSubmarine) ) retval *= 1.25;
        return Math.max(retval, 1);
    }
    
    public boolean IsQuad() {
        return false;
    }

    public boolean IsTripod() {
        return false;
    }
    
    public PhysicalEnhancement GetPhysEnhance() {
        return new PhysicalEnhancement(this);
    }

    public ifCVLoadout GetLoadout() {
        return getCurLoadout();
    }

    public int GetArmorableLocationCount() {
        int Locs = 4;
        if ( isHasTurret1() )
            Locs++;
        if ( isHasTurret2() )
            Locs++;
        if ( IsVTOL() )
            Locs++;
        
        return Locs;
    }
    
    public int GetCrew()
    {
        if ( CurEngine.RequiresControls() )
            return Math.round(Tonnage / 15) + ( (Tonnage % 15 > 0) ? 1 : 0);
        else
            return 0;
    }
    
    public void SetProductionEra( int e ) {
        if( Omni ) {
            CurLoadout.SetProductionEra( e );
        } else {
            MainLoadout.SetProductionEra( e );
        }
    }
    
    public void setMotiveType( String Motive ) {
        for ( ifCombatVehicle state : states) {
            if (state.GetMotiveLookupName().equals(Motive)) {
                CurConfig = state;
                break;
            }
        }
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
    
    public String GetTurretLookupName() {
        String retval = "No Turret";
        if ( isHasTurret1() ) retval = "Single Turret";
        if ( isHasTurret2() ) retval = "Dual Turret";
        return retval;
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
    
    public void SetTrailer(boolean b ) {
        IsTrailer = b;
        if ( b ) {
            CurEngine.SetNoneEngine();
        } else if ( CurEngine.isNone() ) {
            CurEngine.SetICEngine();
        }
        setTonnage(Tonnage);
    }
    
    public boolean isTrailer() {
        return IsTrailer;
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
    
    public boolean HasFlotationHull() {
        return UsingFlotationHull;
    }
    
    public void SetFlotationHull(boolean b) {
        UsingFlotationHull = b;
    }
    
    public boolean HasLimitedAmphibious() {
        return UsingLimitedAmphibious;
    }
    
    public void SetLimitedAmphibious(boolean b) {
        UsingLimitedAmphibious = b;
    }
    
    public double GetLimitedAmphibiousTonnage() {
        if ( UsingLimitedAmphibious ) 
            return CommonTools.RoundHalfUp(Tonnage  / 25.0);
        return 0;
    }
    
    public boolean HasFullAmphibious() {
        return UsingFullAmphibious;
    }
    
    public void SetFullAmphibious(boolean b) {
        UsingFullAmphibious = b;
    }
    
    public double GetFullAmphibiousTonnage() {
        if ( UsingFullAmphibious ) 
            return CommonTools.RoundHalfUp(Tonnage  / 10.0);
        return 0;
    }
    
    public boolean HasEnvironmentalSealing() {
        return UsingEnvironmentalSealing;
    }
    
    public void SetEnvironmentalSealing(boolean b) {
        UsingEnvironmentalSealing = b;
    }
    
    public double GetEnvironmentalSealingTonnage() {
        if ( UsingEnvironmentalSealing && !(CurConfig instanceof stCVSubmarine) ) 
            return CommonTools.RoundHalfUp(Tonnage  / 10.0);
        return 0;
    }
        
    public boolean HasDuneBuggy() {
        return UsingDuneBuggy;
    }
    
    public void SetDuneBuggy(boolean b) {
        UsingDuneBuggy = b;
    }

}



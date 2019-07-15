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

/**
 * Provides a class for availability codes for BT equipment.
 */
public class AvailableCode {

    public static final int ERA_STAR_LEAGUE = 0,
                            ERA_SUCCESSION = 1,
                            ERA_CLAN_INVASION = 2,
                            ERA_DARK_AGES = 3,
                            ERA_ALL = 4,
                            TECH_INNER_SPHERE = 0,
                            TECH_CLAN = 1,
                            TECH_BOTH = 2,
                            RULES_UNALLOWED = -1,
                            RULES_INTRODUCTORY = 0,
                            RULES_TOURNAMENT = 1,
                            RULES_ADVANCED = 2,
                            RULES_EXPERIMENTAL = 3,
                            RULES_ERA_SPECIFIC = 4,
                            UNIT_BATTLEMECH = 0,
                            UNIT_INDUSTRIALMECH = 1,
                            UNIT_COMBATVEHICLE = 2,
                            UNIT_AEROFIGHTER = 3,
                            UNIT_CONVFIGHTER = 4,
                            PRODUCTION_ERA_AGE_OF_WAR = 0,
                            PRODUCTION_ERA_STAR_LEAGUE = 1,
                            PRODUCTION_ERA_EARLY_SUCCESSION_WAR = 2,
                            PRODUCTION_ERA_LATE_SUCCESSION_WAR = 3,
                            PRODUCTION_ERA_CLAN_INVASION = 4,
                            PRODUCTION_ERA_CIVIL_WAR = 5,
                            PRODUCTION_ERA_JIHAD = 6,
                            PRODUCTION_ERA_REPUBLIC = 7,
                            PRODUCTION_ERA_DARK_AGES = 8;
    public static String[] TechBaseSTR = {
        "Inner Sphere", "Clan", "Mixed" };

    // Declares
    private char IS_SL = 'X',
                 IS_SW = 'X',
                 IS_CI = 'X',
                 IS_DA = 'A',
                 IS_TechRating = 'X',
                 CL_SL = 'X',
                 CL_SW = 'X',
                 CL_CI = 'X',
                 CL_DA ='A',
                 CL_TechRating = 'X';
    private int IS_RandDStartDate = 0,
                IS_PrototypeDate = 0,
                IS_IntroDate = 0,
                IS_ExtinctDate = 0,
                IS_ReIntroDate = 0,
                CL_RandDStartDate = 0,
                CL_PrototypeDate = 0,
                CL_IntroDate = 0,
                CL_ExtinctDate = 0,
                CL_ReIntroDate = 0,
                RulesLevelBM = 0,
                RulesLevelIM = 0,
                RulesLevelCV = 0,
                RulesLevelAF = 0,
                RulesLevelCF = 0,
                TechBase = 2;
    private String IS_IntroFaction = "",
                   IS_ReIntroFaction = "",
                   IS_RandDFaction = "",
                   IS_PrototypeFaction = "",
                   CL_IntroFaction = "",
                   CL_ReIntroFaction = "",
                   CL_RandDFaction = "",
                   CL_PrototypeFaction = "";
    private boolean IS_WentExtinct = false,
                    IS_ReIntroduced = false,
                    IS_IsPrototype = false,
                    CL_WentExtinct = false,
                    CL_ReIntroduced = false,
                    CL_IsPrototype = false,
                    PBMAllowed = false,
                    PIMAllowed = false,
                    PrimitiveOnly = false,
                    SuperHeavyCompatible = true,
                    SuperHeavyOnly = false;

/*
 *  Constructor
 */
    public AvailableCode( int tech ) {
        TechBase = tech;
    }

/*
 *  Setters
 */
    public void SetCodes( char istech, char isSL, char isSW, char isCI, char cltech, char clSL, char clSW, char clCI ) {
        IS_TechRating = istech;
        IS_SL = isSL;
        IS_SW = isSW;
        IS_CI = isCI;
        //IS_DA = isDA;
        CL_TechRating = istech;
        CL_SL = isSL;
        CL_SW = isSW;
        CL_CI = isCI;
        //CL_DA = clDA;
    }

    /**
     * Set Inner Sphere availability codes
     * @param tech Technology Rating
     * @param SL Star League Era 
     * @param SW Succession Wars Era
     * @param CI Clan Invasion Era
     */
    public void SetISCodes( char tech, char SL, char SW, char CI ) {
        IS_TechRating = tech;
        IS_SL = SL;
        IS_SW = SW;
        IS_CI = CI;
    }
    
    public void SetISCodes( char tech, char SL, char SW, char CI, char DA) {
        SetISCodes(tech, SL, SW, CI);
        IS_DA = DA;
    }

    public void SetCLCodes( char tech, char SL, char SW, char CI ) {
        CL_TechRating = tech;
        CL_SL = SL;
        CL_SW = SW;
        CL_CI = CI;
    }
    
    public void SetCLCodes( char tech, char SL, char SW, char CI, char DA ) {
        SetCLCodes(tech, SL, SW, CI);
        CL_DA = DA;
    }

    public void SetISDates( int RDStart, int Proto, boolean IsProto, int Intro, int Extinct, int ReIntro, boolean wentExtinct, boolean WasReIntrod ) {
        IS_RandDStartDate = RDStart;
        IS_PrototypeDate = Proto;
        IS_IsPrototype = IsProto;
        IS_IntroDate = Intro;
        IS_ExtinctDate = Extinct;
        IS_ReIntroDate = ReIntro;
        IS_WentExtinct = wentExtinct;
        IS_ReIntroduced = WasReIntrod;
    }

    public void SetCLDates( int RDStart, int Proto, boolean IsProto, int Intro, int Extinct, int ReIntro, boolean wentExtinct, boolean WasReIntrod ) {
        CL_RandDStartDate = RDStart;
        CL_PrototypeDate = Proto;
        CL_IsPrototype = IsProto;
        CL_IntroDate = Intro;
        CL_ExtinctDate = Extinct;
        CL_ReIntroDate = ReIntro;
        CL_WentExtinct = wentExtinct;
        CL_ReIntroduced = WasReIntrod;
    }

    public void SetISFactions( String RandDfac, String protofac, String introfac, String reintrofac ) {
        IS_IntroFaction = introfac;
        IS_ReIntroFaction = reintrofac;
        IS_RandDFaction = RandDfac;
        IS_PrototypeFaction = protofac;
    }

    public void SetCLFactions( String RandDfac, String protofac, String introfac, String reintrofac ) {
        CL_IntroFaction = introfac;
        CL_ReIntroFaction = reintrofac;
        CL_RandDFaction = RandDfac;
        CL_PrototypeFaction = protofac;
    }

    public void SetFactions( String ISRandDfac, String ISprotofac, String ISintrofac, String ISreintrofac, String CLRandDfac, String CLprotofac, String CLintrofac, String CLreintrofac ) {
        IS_IntroFaction = ISintrofac;
        IS_ReIntroFaction = ISreintrofac;
        IS_RandDFaction = ISRandDfac;
        IS_PrototypeFaction = ISprotofac;
        CL_IntroFaction = CLintrofac;
        CL_ReIntroFaction = CLreintrofac;
        CL_RandDFaction = CLRandDfac;
        CL_PrototypeFaction = CLprotofac;
    }

    /**
     * Set the rules levels for each possible unit type
     * @param BM BattleMech rules level
     * @param IM Industrial Mech rules level
     * @param CV Combat Vehicle rules level
     * @param AF Aerospace Fighter rules level
     * @param CF Conventional Fighter rules level
     */
    public void SetRulesLevels( int BM, int IM, int CV, int AF, int CF ) {
        RulesLevelBM = BM;
        RulesLevelIM = IM;
        RulesLevelCV = CV;
        RulesLevelAF = AF;
        RulesLevelCF = CF;
    }

    public void SetPBMAllowed( boolean b ) {
        PBMAllowed = b;
    }

    public void SetPIMAllowed( boolean b ) {
        PIMAllowed = b;
    }

    public void SetPrimitiveOnly ( boolean b ) {
        PrimitiveOnly = b;
    }
    
    /**
     * Set Super Heavy 'Mech compatibility
     * @param b Super Heavy compatibility setting
     */
    public void SetSuperHeavyCompatible( boolean b ) {
        SuperHeavyCompatible = b;
    }
    
    /**
     * Set Super Heavy 'Mech only restriction
     * @param b Super Heavy compatibility setting
     */
    public void SetSuperHeavyOnly( boolean b ) {
        SuperHeavyOnly = b;
    }
/*
 *  Getters
 */
    public char GetISTechRating() {
        return IS_TechRating;
    }

    public char GetISSLCode() {
        return IS_SL;
    }

    public char GetISSWCode() {
        return IS_SW;
    }

    public char GetISCICode() {
        return IS_CI;
    }
    
    public char GetISDACode() {
        return IS_DA;
    }

    public char GetCLTechRating() {
        return CL_TechRating;
    }

    public char GetCLSLCode() {
        return CL_SL;
    }

    public char GetCLSWCode() {
        return CL_SW;
    }

    public char GetCLCICode() {
        return CL_CI;
    }
    
    public char GetCLDACode() {
        return CL_DA;
    }

    public int GetISRandDStartDate() {
        return IS_RandDStartDate;
    }

    public int GetISPrototypeDate() {
        return IS_PrototypeDate;
    }

    public int GetISIntroDate() {
        return IS_IntroDate;
    }

    public int GetISExtinctDate() {
        return IS_ExtinctDate;
    }

    public int GetISReIntroDate() {
        return IS_ReIntroDate;
    }

    public boolean Is_ISPrototype() {
        return IS_IsPrototype;
    }

    public boolean WentExtinctIS() {
        return IS_WentExtinct;
    }

    public boolean WasReIntrodIS() {
        return IS_ReIntroduced;
    }

    public int GetCLRandDStartDate() {
        return CL_RandDStartDate;
    }

    public int GetCLPrototypeDate() {
        return CL_PrototypeDate;
    }

    public int GetCLIntroDate() {
        return CL_IntroDate;
    }

    public int GetCLExtinctDate() {
        return CL_ExtinctDate;
    }

    public int GetCLReIntroDate() {
        return CL_ReIntroDate;
    }

    public boolean Is_CLPrototype() {
        return CL_IsPrototype;
    }

    public boolean WentExtinctCL() {
        return CL_WentExtinct;
    }

    public boolean WasReIntrodCL() {
        return CL_ReIntroduced;
    }

    public String GetISRandDFaction() {
        return IS_RandDFaction;
    }

    public String GetISPrototypeFaction() {
        return IS_PrototypeFaction;
    }

    public String GetISIntroFaction() {
        return IS_IntroFaction;
    }

    public String GetISReIntroFaction() {
        if( IS_ReIntroduced ) {
            return IS_ReIntroFaction;
        } else {
            return "--";
        }
    }

    public String GetCLRandDFaction() {
        return CL_RandDFaction;
    }

    public String GetCLPrototypeFaction() {
        return CL_PrototypeFaction;
    }

    public String GetCLIntroFaction() {
        return CL_IntroFaction;
    }

    public String GetCLReIntroFaction() {
        if( CL_ReIntroduced ) {
            return CL_ReIntroFaction;
        } else {
            return "--";
        }
    }

    public int GetRulesLevel_BM() {
        return RulesLevelBM;
    }

    public int GetRulesLevel_IM() {
        return RulesLevelIM;
    }

    public int GetRulesLevel_CV() {
        return RulesLevelCV;
    }

    public int GetRulesLevel_AF() {
        return RulesLevelAF;
    }

    public int GetRulesLevel_CF() {
        return RulesLevelCF;
    }

    public int GetTechBase() {
        return TechBase;
    }

    public boolean IsPBMAllowed() {
        return PBMAllowed;
    }

    public boolean IsPIMAllowed() {
        return PIMAllowed;
    }

    public boolean IsPrimitiveOnly() {
        return PrimitiveOnly;
    }
    
    /**
     * Determines if this Available Code is compatible with Super Heavy BattleMechs
     * @return True if Super Heavy designs are supported.
     */
    public boolean IsSuperHeavyCompatible() {
        return SuperHeavyCompatible;
    }
    
    /**
     * Determines if this Available Code is only compatible with Super Heavy BattleMechs
     * @return True if only Super Heavy designs are supported.
     */
    public boolean IsSuperHeavyOnly() {
        return SuperHeavyOnly;
    }

/*
 *  Informational
 */
    public String GetISCombinedCode() {
        return IS_TechRating + "/" + IS_SL + "-" + IS_SW + "-" + IS_CI + "-" + IS_DA;
    }

    public String GetCLCombinedCode() {
        return CL_TechRating + "/" + CL_SL + "-" + CL_SW + "-" + CL_CI + "-" + CL_DA;
    }

    public char GetBestTechRating() {
        if( IS_TechRating > CL_TechRating ) {
            if( IS_TechRating == 'X' ) {
                return CL_TechRating;
            } else{
                return IS_TechRating;
            }
        } else {
            if( CL_TechRating == 'X' ) {
                return IS_TechRating;
            } else{
                return CL_TechRating;
            }
        }
    }

    public char GetBestSLCode() {
        // Clan Star League codes should ALWAYS be X
        return IS_SL;
    }

    public char GetBestSWCode() {
        if( IS_SW < CL_SW ) {
            return IS_SW;
        } else {
            return CL_SW;
        }
    }

    public char GetBestCICode() {
        if( IS_CI < CL_CI ) {
            return IS_CI;
        } else {
            return CL_CI;
        }
    }
    
    public char GetBestDACode() {
        if ( IS_DA < CL_DA ) {
            return IS_DA;
        } else {
            return CL_DA;
        }
    }

    public String GetBestCombinedCode() {
        return GetBestTechRating() + "/" + GetBestSLCode() + "-" + GetBestSWCode() + "-" + GetBestCICode() + "-" + GetBestDACode();
    }

/*
 *  Utility
 */
    public void Combine( AvailableCode a ) {
        // Alters this code from the given AvailableCode.  Mainly used for final
        // availability.

        if( a.GetISTechRating() > IS_TechRating ) {
            IS_TechRating = a.GetISTechRating();
        }
        if( a.GetISSLCode() > IS_SL ) {
            IS_SL = a.GetISSLCode();
        }
        if( a.GetISSWCode() > IS_SW ) {
            IS_SW = a.GetISSWCode();
        }
        if( a.GetISCICode() > IS_CI ) {
            IS_CI = a.GetISCICode();
        }
        if( a.GetCLTechRating() > CL_TechRating ) {
            CL_TechRating = a.GetCLTechRating();
        }
        if( a.GetCLSLCode() > CL_SL ) {
            CL_SL = a.GetCLSLCode();
        }
        if( a.GetCLSWCode() > CL_SW ) {
            CL_SW = a.GetCLSWCode();
        }
        if( a.GetCLCICode() > CL_CI ) {
            CL_CI = a.GetCLCICode();
        }

        if( a.GetISIntroDate() > IS_IntroDate ) {
            IS_IntroDate = a.GetISIntroDate();
            IS_IntroFaction = a.GetISIntroFaction();
        }
        if( a.GetCLIntroDate() > CL_IntroDate ) {
            CL_IntroDate = a.GetCLIntroDate();
            CL_IntroFaction = a.GetCLIntroFaction();
        }
        if( a.GetISExtinctDate() > 0 ) {
            if( IS_ExtinctDate > 0 ) {
                if( a.GetISExtinctDate() < IS_ExtinctDate ) {
                    IS_ExtinctDate = a.GetISExtinctDate();
                }
            } else {
                IS_ExtinctDate = a.GetISExtinctDate();
            }
        }
        if( a.GetCLExtinctDate() > 0 ) {
            if( CL_ExtinctDate > 0 ) {
                if( a.GetCLExtinctDate() < CL_ExtinctDate ) {
                    CL_ExtinctDate = a.GetCLExtinctDate();
                }
            } else {
                CL_ExtinctDate = a.GetCLExtinctDate();
            }
        }
        if( a.GetISReIntroDate() > IS_ReIntroDate ) {
            IS_ReIntroDate = a.GetISReIntroDate();
            IS_ReIntroFaction = a.GetISReIntroFaction();
        }
        if( a.GetISRandDStartDate() > IS_RandDStartDate ) {
            IS_RandDStartDate = a.GetISRandDStartDate();
            IS_RandDFaction = a.GetISRandDFaction();
        }
        if( a.GetCLReIntroDate() > CL_ReIntroDate ) {
            CL_ReIntroDate = a.GetCLReIntroDate();
            CL_ReIntroFaction = a.GetCLReIntroFaction();
        }
        if( a.GetCLRandDStartDate() > CL_RandDStartDate ) {
            CL_RandDStartDate = a.GetCLRandDStartDate();
            CL_RandDFaction = a.GetCLRandDFaction();
        }

        if( a.Is_ISPrototype() ) {
            IS_IsPrototype = true;
        }
        if( a.WentExtinctIS() ) {
            IS_WentExtinct = true;
        }
        if( a.WasReIntrodIS() ) {
            IS_ReIntroduced = true;
        }
        if( a.Is_CLPrototype() ) {
            CL_IsPrototype = true;
        }
        if( a.WentExtinctCL() ) {
            CL_WentExtinct = true;
        }
        if( a.WasReIntrodCL() ) {
            CL_ReIntroduced = true;
        }

        if( a.GetRulesLevel_BM() > RulesLevelBM ) {
            RulesLevelBM = a.GetRulesLevel_BM();
        }
        if( a.GetRulesLevel_IM() > RulesLevelIM ) {
            RulesLevelIM = a.GetRulesLevel_IM();
        }
        if( a.GetRulesLevel_CV() > RulesLevelCV ) {
            RulesLevelCV = a.GetRulesLevel_CV();
        }
        if( a.GetRulesLevel_AF() > RulesLevelAF ) {
            RulesLevelAF = a.GetRulesLevel_AF();
        }
        if( a.GetRulesLevel_CF() > RulesLevelCF ) {
            RulesLevelCF = a.GetRulesLevel_CF();
        }

        // double checking routines.
        if( IS_IntroDate > IS_ExtinctDate ) {
            IS_ExtinctDate = 0;
            IS_ReIntroDate = 0;
            IS_WentExtinct = false;
            IS_ReIntroduced = false;
            IS_ReIntroFaction = "";
        }
        if( CL_IntroDate > CL_ExtinctDate ) {
            CL_ExtinctDate = 0;
            CL_ReIntroDate = 0;
            CL_WentExtinct = false;
            CL_ReIntroduced = false;
            CL_ReIntroFaction = "";
        }
    }

    public AvailableCode Clone() {
        AvailableCode retval = new AvailableCode( TechBase );
        retval.SetRulesLevels( RulesLevelBM, RulesLevelIM, RulesLevelCV, RulesLevelAF, RulesLevelCF );
        retval.SetISCodes( IS_TechRating, IS_SL, IS_SW, IS_CI );
        retval.SetISDates( IS_RandDStartDate, IS_PrototypeDate, IS_IsPrototype, IS_IntroDate, IS_ExtinctDate, IS_ReIntroDate, IS_WentExtinct, IS_ReIntroduced );
        retval.SetISFactions( IS_RandDFaction, IS_PrototypeFaction, IS_IntroFaction, IS_ReIntroFaction );
        retval.SetCLCodes( CL_TechRating, CL_SL, CL_SW, CL_CI );
        retval.SetCLDates( CL_RandDStartDate, CL_PrototypeDate, CL_IsPrototype, CL_IntroDate, CL_ExtinctDate, CL_ReIntroDate, CL_WentExtinct, CL_ReIntroduced );
        retval.SetCLFactions( CL_RandDFaction, CL_PrototypeFaction, CL_IntroFaction, CL_ReIntroFaction );
        retval.SetPBMAllowed( PBMAllowed );
        retval.SetPIMAllowed( PIMAllowed );
        retval.SetPrimitiveOnly( PrimitiveOnly );
        return retval;
    }

    @Override
    public String toString() {
        String retval = "";
        switch( TechBase ) {
            case TECH_INNER_SPHERE:
                if( IS_IsPrototype ) {
                    retval = GetISCombinedCode() + ", Intro Date: " + IS_IntroDate + "P (" + IS_IntroFaction + "), R&D Start Date: " + IS_RandDStartDate + " (" + IS_RandDFaction + ")";
                } else {
                    retval = GetISCombinedCode() + ", Intro Date: " + IS_IntroDate + " (" + IS_IntroFaction + ")";
                }
                if( IS_WentExtinct ) {
                    if( IS_ReIntroduced ) {
                        retval += ", Extinct By: " + GetISExtinctDate() + ", Reintroduced By: " + GetISReIntroDate() + " (" + GetISReIntroFaction() + ")";
                    } else {
                        retval += ", Extinct By: " + GetISExtinctDate();
                    }
                }
                return retval;
            case TECH_CLAN:
                if( CL_IsPrototype ) {
                    retval = GetCLCombinedCode() + ", Intro Date: " + CL_IntroDate + "P (" + CL_IntroFaction + "), R&D Start Date: " + CL_RandDStartDate + " (" + CL_RandDFaction + ")";
                } else {
                    retval = GetCLCombinedCode() + ", Intro Date: " + CL_IntroDate + " (" + CL_IntroFaction + ")";
                }
                if( CL_WentExtinct ) {
                    if( CL_ReIntroduced ) {
                        retval += ", Extinct By: " + GetCLExtinctDate() + ", Reintroduced By: " + GetCLReIntroDate() + " (" + GetCLReIntroFaction() + ")";
                    } else {
                        retval += ", Extinct By: " + GetCLExtinctDate();
                    }
                }
                return retval;
            case TECH_BOTH:
                return GetBestCombinedCode();
            default:
                return "??";
        }
    }
}

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

package common;

import components.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CommonTools {
    private final static DecimalFormat dft = new DecimalFormat( "#.###" ),
                                       dfc = new DecimalFormat( "#.####" );
    public final static String tab = "    ";
    public final static String NL = System.getProperty( "line.separator" );
    public final static String Tab = "\t";
    public final static int BattleMech = 0,
                            IndustrialMech = 1,
                            ProtoMech = 2,
                            Vehicle = 3,
                            Infantry = 4,
                            BattleArmor = 5,
                            ConvFighter = 6,
                            AeroFighter = 7,
                            SmallCraft = 8,
                            Dropship = 9,
                            SupportVehicle = 10,
                            MobileStructure = 11;
    
    public final static String[] UnitTypes = { "BattleMech", "IndustrialMech",
                                 "ProtoMech", "Combat Vehicle", "Infantry",
                                 "Battle Armor", "Conventional Fighter",
                                 "Aerospace Fighter", "Small Craft", "Dropship",
                                 "Support Vehicle", "Mobile Structure" };

    private final static float[][] BVMods = {
        { 2.8f,  2.63f, 2.45f, 2.28f, 2.01f, 1.82f, 1.75f, 1.67f, 1.59f },
        { 2.56f, 2.4f,  2.24f, 2.08f, 1.84f, 1.6f,  1.58f, 1.51f, 1.44f },
        { 2.24f, 2.1f,  1.96f, 1.82f, 1.61f, 1.4f,  1.33f, 1.31f, 1.25f },
        { 1.92f, 1.8f,  1.68f, 1.56f, 1.38f, 1.2f,  1.14f, 1.08f, 1.06f },
        { 1.6f,  1.5f,  1.4f,  1.3f,  1.15f, 1.0f,  0.95f, 0.9f,  0.85f },
        { 1.5f,  1.35f, 1.26f, 1.17f, 1.04f, 0.9f,  0.86f, 0.81f, 0.77f },
        { 1.43f, 1.33f, 1.19f, 1.11f, 0.98f, 0.85f, 0.81f, 0.77f, 0.72f },
        { 1.36f, 1.26f, 1.16f, 1.04f, 0.92f, 0.8f,  0.76f, 0.72f, 0.68f },
        { 1.28f, 1.19f, 1.1f,  1.01f, 0.86f, 0.75f, 0.71f, 0.68f, 0.64f }
    };

    private static int[][] ClusterTable = {
        { 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8, 8, 9, 9, 9, 10, 10, 12 },
        { 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8, 8, 9, 9, 9, 10, 10, 12 },
        { 1, 1, 1, 2, 2, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 18 },
        { 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 13, 14, 15, 16, 16, 17, 17, 17, 18, 18, 24 },
        { 1, 1, 2, 2, 3, 4, 4, 5, 5, 6, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 13, 14, 15, 16, 16, 17, 17, 17, 18, 18, 24 },
        { 1, 1, 2, 3, 3, 4, 4, 5, 5, 6, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 13, 14, 15, 16, 16, 17, 17, 17, 18, 18, 24 },
        { 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 13, 14, 15, 16, 16, 17, 17, 17, 18, 18, 24 },
        { 1, 2, 2, 3, 4, 5, 6, 6, 7, 8, 9, 10, 11, 11, 12, 13, 14, 14, 15, 16, 17, 18, 19, 20, 21, 21, 22, 23, 23, 24, 32 },
        { 1, 2, 3, 3, 4, 5, 6, 6, 7, 8, 9, 10, 11, 11, 12, 13, 14, 14, 15, 16, 17, 18, 19, 20, 21, 21, 22, 23, 23, 24, 32 },
        { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 40 },
        { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 40 },
    };

    public static String DecodeEra( int era ) {
        switch( era ) {
            case AvailableCode.ERA_STAR_LEAGUE:
                return "Age of War/Star League";
            case AvailableCode.ERA_SUCCESSION:
                return "Succession Wars";
            case AvailableCode.ERA_CLAN_INVASION:
                return "Clan Invasion";
            case AvailableCode.ERA_DARK_AGES:
                return "Dark Ages";
            case AvailableCode.ERA_ALL:
                return "All Eras (non-canon)";
            default:
                return "Unknown";
        }
    }

    public static String GetRulesLevelString( int level ) {
        switch( level ) {
            case AvailableCode.RULES_INTRODUCTORY:
                return "Introductory";
            case AvailableCode.RULES_TOURNAMENT:
                return "Tournament Legal";
            case AvailableCode.RULES_ADVANCED:
                return "Advanced Rules";
            case AvailableCode.RULES_EXPERIMENTAL:
                return "Experimental Tech";
            case AvailableCode.RULES_ERA_SPECIFIC:
                return "Era Specific";
            case AvailableCode.RULES_UNALLOWED:
                return "Unallowed";
            default:
                return "Unknown";
        }
    }

    public static String GetTechbaseString( int tech ) {
        switch( tech ) {
            case AvailableCode.TECH_INNER_SPHERE:
                return "Inner Sphere";
            case AvailableCode.TECH_CLAN:
                return "Clan";
            case AvailableCode.TECH_BOTH:
                return "Mixed";
            default:
                return "Unknown";
        }
    }

    public static String GetAggregateReportBV( abPlaceable p ) {
        // since an item may have both offensive and defensive BV, this gives us
        // an aggregate battle value string for reporting
        String result = "";
        if( p.GetOffensiveBV() > 0.0 ) {
            result += String.format( "%1$.2f", p.GetOffensiveBV() );
            if( p.GetDefensiveBV() > 0.0 ) {
                result += " / " + String.format( "%1$.2f", p.GetDefensiveBV() ) + "(D)";
            }
        } else if( p.GetDefensiveBV() > 0.0 ) {
            result += String.format( "%1$.2f", p.GetDefensiveBV() ) + "(D)";
        } else {
            result += "0";
        }
        return result;
    }

    public static boolean IsAllowed( AvailableCode AC, ifUnit u ) {
        // check an available code to see if it can be used legally

        // switch by type and filter.
        switch( u.GetUnitType() ) {
            case AvailableCode.UNIT_BATTLEMECH:
                if( ! ( u instanceof Mech ) ) { return false; }
                switch( u.GetRulesLevel() ) {
                    case AvailableCode.RULES_INTRODUCTORY:
                        if( AC.GetRulesLevel_BM() != AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_TOURNAMENT:
                        if( AC.GetRulesLevel_BM() > AvailableCode.RULES_TOURNAMENT ||
                            AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_ADVANCED:
                        if( AC.GetRulesLevel_BM() > AvailableCode.RULES_ADVANCED ||
                            AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_EXPERIMENTAL:
                        if( AC.GetRulesLevel_BM() > AvailableCode.RULES_EXPERIMENTAL ||
                            AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_ERA_SPECIFIC:
                        if( AC.GetRulesLevel_BM() > AvailableCode.RULES_ERA_SPECIFIC ||
                            AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    default:
                        return false;
                }
                // is the 'Mech primitive and is this equipment allowed?
                if( ((Mech) u).IsPrimitive() ) {
                    if( ! AC.IsPBMAllowed() ) { return false; }
                } else {
                    if( AC.IsPrimitiveOnly() ) { return false; }
                }
                
                // If this 'Mech is Super Heavy, the equipment must be compatible
                if ( ((Mech) u).IsSuperheavy() ) {
                    if ( ! AC.IsSuperHeavyCompatible() ) { return false; }
                } else {
                    if ( AC.IsSuperHeavyOnly() ) { return false; }
                }
                
                break;
            case AvailableCode.UNIT_INDUSTRIALMECH:
                if( ! ( u instanceof Mech ) ) { return false; }
                switch( u.GetRulesLevel() ) {
                    case AvailableCode.RULES_INTRODUCTORY:
                        if( AC.GetRulesLevel_IM() != AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_TOURNAMENT:
                        if( AC.GetRulesLevel_IM() > AvailableCode.RULES_TOURNAMENT ||
                            AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_ADVANCED:
                        if( AC.GetRulesLevel_IM() > AvailableCode.RULES_ADVANCED ||
                            AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_EXPERIMENTAL:
                        if( AC.GetRulesLevel_IM() > AvailableCode.RULES_EXPERIMENTAL ||
                            AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_ERA_SPECIFIC:
                        if( AC.GetRulesLevel_IM() > AvailableCode.RULES_ERA_SPECIFIC ||
                            AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    default:
                        return false;
                }
                // is the 'Mech primitive and is this equipment allowed?
                if( ((Mech) u).IsPrimitive() ) {
                    if( ! AC.IsPIMAllowed() ) { return false; }
                } else {
                    if( AC.IsPrimitiveOnly() ) { return false; }
                }
                break;
            case AvailableCode.UNIT_COMBATVEHICLE:
                switch( u.GetRulesLevel() ) {
                    case AvailableCode.RULES_INTRODUCTORY:
                        if( AC.GetRulesLevel_CV() != AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_TOURNAMENT:
                        if( AC.GetRulesLevel_CV() > AvailableCode.RULES_TOURNAMENT ||
                            AC.GetRulesLevel_CV() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_ADVANCED:
                        if( AC.GetRulesLevel_CV() > AvailableCode.RULES_ADVANCED ||
                            AC.GetRulesLevel_CV() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_EXPERIMENTAL:
                        if( AC.GetRulesLevel_CV() > AvailableCode.RULES_EXPERIMENTAL ||
                            AC.GetRulesLevel_CV() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    case AvailableCode.RULES_ERA_SPECIFIC:
                        if( AC.GetRulesLevel_CV() > AvailableCode.RULES_ERA_SPECIFIC ||
                            AC.GetRulesLevel_CV() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                        break;
                    default:
                        return false;
                }
                // is the 'Mech primitive and is this equipment allowed?
                if( ((CombatVehicle) u).IsPrimitive() ) {
                    if( ! AC.IsPIMAllowed() ) { return false; }
                } else {
                    if( AC.IsPrimitiveOnly() ) { return false; }
                }
                break;
            case AvailableCode.UNIT_CONVFIGHTER:
                switch( u.GetRulesLevel() ) {
                    case AvailableCode.RULES_INTRODUCTORY:
                        break;
                    case AvailableCode.RULES_TOURNAMENT:
                        break;
                    case AvailableCode.RULES_ADVANCED:
                        break;
                    case AvailableCode.RULES_EXPERIMENTAL:
                        break;
                    case AvailableCode.RULES_ERA_SPECIFIC:
                        break;
                    default:
                        return false;
                }
                break;
            case AvailableCode.UNIT_AEROFIGHTER:
                switch( u.GetRulesLevel() ) {
                    case AvailableCode.RULES_INTRODUCTORY:
                        break;
                    case AvailableCode.RULES_TOURNAMENT:
                        break;
                    case AvailableCode.RULES_ADVANCED:
                        break;
                    case AvailableCode.RULES_EXPERIMENTAL:
                        break;
                    case AvailableCode.RULES_ERA_SPECIFIC:
                        break;
                    default:
                        return false;
                }
                break;
            default:
                return false;
        }

        // is this within our techbase?
        switch( u.GetTechbase() ) {
            case AvailableCode.TECH_INNER_SPHERE:
                if( AC.GetTechBase() == AvailableCode.TECH_CLAN ) { return false; }
                break;
            case AvailableCode.TECH_CLAN:
                if( AC.GetTechBase() == AvailableCode.TECH_INNER_SPHERE ) { return false; }
                break;
            case AvailableCode.TECH_BOTH:
                // this does nothing, put here to avoid default
                break;
            default:
                return false;
        }

        // are we restricting by year?
        if( u.IsYearRestricted() ) {
            // we are.
            switch( u.GetTechbase() ) {
                case AvailableCode.TECH_INNER_SPHERE:
                    if( AC.WentExtinctIS() ) {
                        if( AC.WasReIntrodIS() ) {
                            if( ( u.GetYear() >= AC.GetISIntroDate() && u.GetYear() < AC.GetISExtinctDate() ) || u.GetYear() >= AC.GetISReIntroDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if( u.GetYear() >= AC.GetISIntroDate() && u.GetYear() < AC.GetISExtinctDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    } else {
                        if( u.GetYear() >= AC.GetISIntroDate() ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                case AvailableCode.TECH_CLAN:
                    if( AC.WentExtinctCL() ) {
                        if( AC.WasReIntrodCL() ) {
                            if( ( u.GetYear() >= AC.GetCLIntroDate() && u.GetYear() < AC.GetCLExtinctDate() ) || u.GetYear() >= AC.GetCLReIntroDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if( u.GetYear() >= AC.GetCLIntroDate() && u.GetYear() < AC.GetCLExtinctDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    } else {
                        if( u.GetYear() >= AC.GetCLIntroDate() ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                case AvailableCode.TECH_BOTH:
                    boolean Okay_IS = false, Okay_CL = false;
                    if( AC.WentExtinctIS() ) {
                        if( AC.WasReIntrodIS() ) {
                            if( ( u.GetYear() >= AC.GetISIntroDate() && u.GetYear() < AC.GetISExtinctDate() ) || u.GetYear() >= AC.GetISReIntroDate() ) {
                                Okay_IS = true;
                            } else {
                                Okay_IS = false;
                            }
                        } else {
                            if( u.GetYear() >= AC.GetISIntroDate() && u.GetYear() < AC.GetISExtinctDate() ) {
                                Okay_IS = true;
                            } else {
                                Okay_IS = false;
                            }
                        }
                    } else {
                        if( u.GetYear() >= AC.GetISIntroDate() ) {
                            Okay_IS = true;
                        } else {
                            Okay_IS = false;
                        }
                    }
                    if( AC.WentExtinctCL() ) {
                        if( AC.WasReIntrodCL() ) {
                            if( ( u.GetYear() >= AC.GetCLIntroDate() && u.GetYear() < AC.GetCLExtinctDate() ) || u.GetYear() >= AC.GetCLReIntroDate() ) {
                                Okay_CL = true;
                            } else {
                                Okay_CL = false;
                            }
                        } else {
                            if( u.GetYear() >= AC.GetCLIntroDate() && u.GetYear() < AC.GetCLExtinctDate() ) {
                                Okay_CL = true;
                            } else {
                                Okay_CL = false;
                            }
                        }
                    } else {
                        if( u.GetYear() >= AC.GetCLIntroDate() ) {
                            Okay_CL = true;
                        } else {
                            Okay_CL = false;
                        }
                    }
                    if( Okay_IS || Okay_CL ) {
                        return true;
                    } else {
                        return false;
                    }
            }
        } else {
            // we aren't, go by the era
            switch( u.GetEra() ) {
            case AvailableCode.ERA_STAR_LEAGUE:
                switch( u.GetTechbase() ) {
                    case AvailableCode.TECH_INNER_SPHERE: case AvailableCode.TECH_BOTH:
                        if( AC.GetISSLCode() < 'X' ) {
                            return true;
                        } else {
                            return false;
                        }
                    case AvailableCode.TECH_CLAN:
                        return false;
                }
            case AvailableCode.ERA_SUCCESSION:
                switch( u.GetTechbase() ) {
                    case AvailableCode.TECH_INNER_SPHERE:
                        if( u.GetRulesLevel() > AvailableCode.RULES_TOURNAMENT ) {
                            if( AC.GetISSWCode() < 'X' ) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if( AC.GetISSWCode() < 'F' ) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    case AvailableCode.TECH_CLAN:
                        if( AC.GetCLSWCode() < 'X' ) {
                            return true;
                        } else {
                            return false;
                        }
                    case AvailableCode.TECH_BOTH:
                        if( AC.GetBestSWCode() < 'X' ) {
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        return false;
                }
            case AvailableCode.ERA_CLAN_INVASION:
                switch( u.GetTechbase() ) {
                    case AvailableCode.TECH_INNER_SPHERE:
                        if( AC.GetISCICode() < 'X' ) {
                            return true;
                        } else {
                            return false;
                        }
                    case AvailableCode.TECH_CLAN:
                        if( AC.GetCLCICode() < 'X' ) {
                            return true;
                        } else {
                            return false;
                        }
                    case AvailableCode.TECH_BOTH:
                        if( AC.GetBestCICode() < 'X' ) {
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        return false;
                }
            case AvailableCode.ERA_DARK_AGES:
                switch( u.GetTechbase() ) {
                    case AvailableCode.TECH_INNER_SPHERE:
                        if( AC.GetISDACode() < 'X' ) {
                            return true;
                        } else {
                            return false;
                        }
                    case AvailableCode.TECH_CLAN:
                        if( AC.GetCLDACode() < 'X' ) {
                            return true;
                        } else {
                            return false;
                        }
                    case AvailableCode.TECH_BOTH:
                        if( AC.GetBestDACode() < 'X' ) {
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        return false;
                }
            case AvailableCode.ERA_ALL:
                // the "All" era.
                return true;
            }
        }

        return false;
    }

    public static double GetAdjustedBV( double BV, int Gunnery, int Piloting ) {
        return BV * BVMods[Gunnery][Piloting];
    }

    public static int GetAverageClusterHits( ifWeapon w, int manualmodifier ) {
        if( ! w.IsCluster() ) { return 1; }
        if( w.IsStreak() ) { return w.ClusterSize(); }
        int size = w.ClusterSize() - 1; // subtract one for array indexing
        int roll = 5 + manualmodifier; // subtracting 2 because we're indexing an array
        if( w instanceof RangedWeapon ) {
            if( ((RangedWeapon) w).IsUsingFCS() ) {
                roll += ((RangedWeapon) w).GetFCS().GetClusterTableBonus();
            }
        }
        if( roll > 10 ) { roll = 10; }
        if( roll < 0 ) { roll = 0; }
        if( size > 30 ) { size = 30; }
        if( size < 0 ) { return 0; }
        return ClusterTable[roll][size];
    }

    /**
     * Returns the average damage of the given weapon at the given range.
     * We say "average" here as some weapons use a clustered system and may only
     * do partial damage, otherwise we return the maximum damage.  If the range
     * specified is less than 0 or greater than the maximum range of the weapon
     * we return -1.
     *
     * @param w The weapon in question.
     * @param range The range that the weapon will be hitting a target at
     * @return The average damage of the weapon at that range.
     */
    public static int GetAverageDamageAtRange( ifWeapon w, int range ) {
        if( w.IsCluster() ) {
            boolean Streak = w.IsStreak();
            boolean Ultra = w.IsUltra();
            boolean Rotary = w.IsRotary();
            boolean Ballistic = false;
            if( w.GetWeaponClass() == ifWeapon.W_BALLISTIC ) { Ballistic = true; }
            if( range > w.GetRangeLong() ) {
                return 0;
            } else if( range > w.GetRangeMedium() ) {
                if( Streak ) {
                    return w.GetDamageLong() * w.ClusterSize();
                } else if( Ultra ) {
                    // don't even bother with the cluster table, it'll always
                    // return 1 in this case.
                    return w.GetDamageLong();
                } else if( Ballistic &! Rotary ) {
                    // this covers LB-X cannons in general
                    return GetAverageClusterHits( w, w.ClusterModLong() );
                } else {
                    return w.GetDamageLong() * GetAverageClusterHits( w, w.ClusterModLong() );
                }
            } else if( range > w.GetRangeShort() ) {
                if( Streak ) {
                    return w.GetDamageMedium() * w.ClusterSize();
                } else if( Ultra ) {
                    return w.GetDamageMedium();
                } else if( Ballistic &! Rotary ) {
                    return GetAverageClusterHits( w, w.ClusterModMedium() );
                } else {
                    return w.GetDamageMedium() * GetAverageClusterHits( w, w.ClusterModMedium() );
                }
            } else {
                if( Streak ) {
                    return w.GetDamageShort() * w.ClusterSize();
                } else if( Ultra ) {
                    return w.GetDamageShort();
                } else if( Ballistic &! Rotary ) {
                    return GetAverageClusterHits( w, w.ClusterModShort() );
                } else {
                    return w.GetDamageShort() * GetAverageClusterHits( w, w.ClusterModShort() );
                }
            }
        } else {
            if( range > w.GetRangeLong() ) {
                return 0;
            } else if( range > w.GetRangeMedium() ) {
                return w.GetDamageLong();
            } else if( range > w.GetRangeShort() ) {
                return w.GetDamageMedium();
            } else {
                return w.GetDamageShort();
            }
        }
    }

    /**
     * Returns the average damage of the given weapon at the given range using
     * the ammunition specified.  This routine is provided to support LB-X ACs,
     * ATMs, and MMLs. We say "average" here as some weapons use a clustered
     * system and may only do partial damage, otherwise we return the maximum
     * damage.  If the range specified is less than 0 or greater than the
     * maximum range of the weapon we return -1.
     *
     * @param w The weapon in question.
     * @param a The ammunition that the weapon will use.
     * @param range The range that the weapon will be hitting a target at.
     * @return The average damage of the weapon at that range.
     */
    public static int GetAverageDamageAtRange( ifWeapon w, Ammunition a, int range ) {
        if( a.IsCluster() ) {
            boolean Streak = w.IsStreak();
            boolean Ultra = w.IsUltra();
            boolean Rotary = w.IsRotary();
            boolean Ballistic = false;
            if( w.GetWeaponClass() == ifWeapon.W_BALLISTIC && a.IsCluster() ) { Ballistic = true; }
            if( range > a.GetLongRange() ) {
                return 0;
            } else if( range > a.GetMediumRange() ) {
                if( Streak ) {
                    return a.GetDamageLong() * a.ClusterSize();
                } else if( Ultra ) {
                    // don't even bother with the cluster table, it'll always
                    // return 1 in this case.
                    return a.GetDamageLong();
                } else if( Ballistic &! Rotary ) {
                    // this covers LB-X cannons in general
                    return GetAverageClusterHits( w, w.ClusterModLong() );
                } else {
                    return a.GetDamageLong() * GetAverageClusterHits( w, w.ClusterModLong() );
                }
            } else if( range > a.GetShortRange() ) {
                if( Streak ) {
                    return a.GetDamageMedium() * a.ClusterSize();
                } else if( Ultra ) {
                    return a.GetDamageMedium();
                } else if( Ballistic &! Rotary ) {
                    return GetAverageClusterHits( w, w.ClusterModMedium() );
                } else {
                    return a.GetDamageMedium() * GetAverageClusterHits( w, w.ClusterModMedium() );
                }
            } else {
                if( Streak ) {
                    return a.GetDamageShort() * a.ClusterSize();
                } else if( Ultra ) {
                    return a.GetDamageShort();
                } else if( Ballistic &! Rotary ) {
                    return GetAverageClusterHits( w, w.ClusterModShort() );
                } else {
                    return a.GetDamageShort() * GetAverageClusterHits( w, w.ClusterModShort() );
                }
            }
        } else {
            if( range > a.GetLongRange() ) {
                return 0;
            } else if( range > a.GetMediumRange() ) {
                return a.GetDamageLong();
            } else if( range > a.GetShortRange() ) {
                return a.GetDamageMedium();
            } else {
                return a.GetDamageShort();
            }
        }
    }

    /**
     * Returns the to-hit modifier of the weapon at the given range.  This is
     * only weapon-specific, it does not account for target or firer movement,
     * or other modifiers.  If the weapon cannot hit at the given range, we
     * return 12.
     *
     * @param w The weapon in question.
     * @param range The range that the weapon will be hitting a target at.
     * @return The to-hit modifier for the weapon at the given range.
     */
    public static int GetToHitAtRange( ifWeapon w, int range ) {
        if( range > w.GetRangeLong() ) {
            return 12;
        } else if( range > w.GetRangeMedium() ) {
            return 4 + w.GetToHitLong();
        } else if( range > w.GetRangeShort() ) {
            return 2 + w.GetToHitMedium();
        } else {
            if( range <= w.GetRangeMin() && w.GetRangeMin() > 0 ) {
                int min = w.GetRangeMin() - range + 1;
                return 0 + min + w.GetToHitShort();
            } else {
                return 0 + w.GetToHitShort();
            }
        }
    }

    /**
     * Returns the to-hit modifier of the weapon at the given range using the
     * specified ammunition.  Some ammunition may affect the to-hit modifiers
     * for a specific weapon, in general ATMs, MMLs, and LB-X ACs.  This is only
     * weapon-specific, it does not account for target or firer movement, or
     * other modifiers.  If the weapon cannot hit at the given range, we return
     * 12.
     * 
     * @param w The weapon in question.
     * @param a The ammunition that the weapon will be using.
     * @param range The range that the weapon will be hitting a target at.
     * @return The to-hit modifier for the weapon at the given range.
     */
    public static int GetToHitAtRange( Ammunition a, int range ) {
        if( range > a.GetLongRange() ) {
            return 12;
        } else if( range > a.GetMediumRange() ) {
            return 4 + a.GetToHitLong();
        } else if( range > a.GetShortRange() ) {
            return 2 + a.GetToHitMedium();
        } else {
            if( range <= a.GetMinRange() && a.GetMinRange() > 0 ) {
                int min = a.GetMinRange() - range + 1;
                return 0 + min + a.GetToHitShort();
            } else {
                return 0 + a.GetToHitShort();
            }
        }
    }

    public static String Tabs(int Num) {
        String tabs = "";
        for (int i=0; i < Num; i++) {
            tabs += tab;
        }
        return tabs;
    }


    public static float GetSkillBV( float BV, int Gunnery, int Piloting ) {
        return BV * BVMods[Gunnery][Piloting];
    }

    public static float GetModifierBV( float SkillBV, float Modifier) {
        return SkillBV * Modifier;
    }

    public static float GetFullAdjustedBV( float BV, int Gunnery, int Piloting, float Modifier ) {
        return (BV * BVMods[Gunnery][Piloting]) * Modifier;
    }

    public static float GetForceSizeMultiplier( int Force1Size, int Force2Size ) {
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
        s = s.replaceAll("\"", "");
        s = s.replaceAll("'", "");
        s = s.replaceAll("[{(}]", "%28");
        s = s.replaceAll("[{)}]", "%29");
        s = s.replaceAll("[{;}]", "%3B");
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
        return filename.replace("'", "").replace( "\"", "");
    }

    public static String spaceRight(String value, int length) {
        return String.format("%1$-" + length + "s", value);
    }

    public static String spaceLeft(String value, int length) {
        return String.format("%1$#" + length + "s", value);
    }

    public static String padRight(String value, int length, String character) {
                if ( value.length() < length ) {

            for ( int i=value.length(); i < length; i++) {
                value += character;
            }
        }
        return value;
    }

    public static double RoundFractionalTons( double d ) {
        return Double.valueOf( dft.format( d ) );
    }

    public static double RoundFractionalCost( double d ) {
        return Double.valueOf( dfc.format( d ) );
    }

    public static double RoundHalfDown( double d ) {
        BigDecimal value = new BigDecimal(d);
        value.setScale(1, RoundingMode.HALF_DOWN);
        return value.doubleValue();
    }

    public static double RoundHalfUp( double d ) {
        return .5 * Math.ceil(d/.5);

        //BigDecimal value = new BigDecimal(d);
        //value.setScale(1, RoundingMode.HALF_UP);
        //return value.doubleValue();
    }

    /***
     * Adds .5 and rounds the result...this should round up to the full integer anything <.4 
     * @param d Value to round up to the next full integer
     * @return rounded integer
     */
    public static double RoundFullUp( double d ) {
        return Math.round(.4 + d);
    }

    public static String FormatSpeed( double d ) {
        return String.format( "%1$.1f", d );
    }

    public static String shortenPath( String path, int maxlength ) {
        String newPath = path;
        if ( path.length() > maxlength ) {
            String[] parts = path.split("\\" + File.separator.toString());
            newPath = parts[0] + File.separator;
            for ( int i=1; i <= parts.length-2; i++ ) {
                if ( (newPath + parts[i] + File.separator + parts[parts.length-1]).length() <= (maxlength-3) ) {
                    newPath += parts[i] + File.separator;
                } else {
                    newPath += "...";
                    break;
                }
            }
            newPath += parts[parts.length-1];
        }
        return newPath;
    }
}

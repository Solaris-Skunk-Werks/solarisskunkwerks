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

package ssw.utilities;

import ssw.components.*;

public class CommonTools {
    private final static double[][] BVMods = {
        { 2.8, 2.63, 2.45, 2.28, 2.01, 1.82, 1.75, 1.67, 1.59 },
        { 2.56, 2.4, 2.24, 2.08, 1.84, 1.6, 1.58, 1.51, 1.44 },
        { 2.24, 2.1, 1.96, 1.82, 1.61, 1.4, 1.33, 1.31, 1.25 },
        { 1.92, 1.8, 1.68, 1.56, 1.38, 1.2, 1.14, 1.08, 1.06 },
        { 1.6, 1.5, 1.4, 1.3, 1.15, 1.0, 0.95, 0.9, 0.85 },
        { 1.5, 1.35, 1.26, 1.17, 1.04, 0.9, 0.86, 0.81, 0.77 },
        { 1.43, 1.33, 1.19, 1.11, 0.98, 0.85, 0.81, 0.77, 0.72 },
        { 1.36, 1.26, 1.16, 1.04, 0.92, 0.8, 0.76, 0.72, 0.68 },
        { 1.28, 1.19, 1.1, 1.01, 0.86, 0.75, 0.71, 0.68, 0.64 }
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
            result += p.GetOffensiveBV() + "";
            if( p.GetDefensiveBV() > 0.0 ) {
                result += " / " + p.GetDefensiveBV() + "(D)";
            }
        } else if( p.GetDefensiveBV() > 0.0 ) {
            result += p.GetDefensiveBV() + "(D)";
        } else {
            result += "0";
        }
        return result;
    }

    public static boolean IsAllowed( AvailableCode AC, Mech m ) {
        // check an available code to see if it can be used legally

        // ensure it's within our rules-level first
        switch( m.GetRulesLevel() ) {
            case AvailableCode.RULES_INTRODUCTORY:
                // tournament legal
                if( m.IsIndustrialmech() ) {
                    if( AC.GetRulesLevel_IM() != AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() != AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
            case AvailableCode.RULES_TOURNAMENT:
                // tournament legal
                if( m.IsIndustrialmech() ) {
                    if( AC.GetRulesLevel_IM() > AvailableCode.RULES_TOURNAMENT || AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() > AvailableCode.RULES_TOURNAMENT || AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
            case AvailableCode.RULES_ADVANCED:
                // advanced rules
                if( m.IsIndustrialmech() ) {
                    if( AC.GetRulesLevel_IM() > AvailableCode.RULES_ADVANCED || AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() > AvailableCode.RULES_ADVANCED || AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
            case AvailableCode.RULES_EXPERIMENTAL:
                // experimental rules.  everything allowed.
                if( m.IsIndustrialmech() ) {
                    if( AC.GetRulesLevel_IM() > AvailableCode.RULES_EXPERIMENTAL || AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() > AvailableCode.RULES_EXPERIMENTAL || AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
            case AvailableCode.RULES_ERA_SPECIFIC:
                if( m.IsIndustrialmech() ) {
                    if( AC.GetRulesLevel_IM() > AvailableCode.RULES_ERA_SPECIFIC || AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() > AvailableCode.RULES_ERA_SPECIFIC || AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
            default:
                // Unallowed or Era Specific.  everything allowed until we know better.
                if( m.IsIndustrialmech() ) {
                    if( AC.GetRulesLevel_IM() > AvailableCode.RULES_EXPERIMENTAL || AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() > AvailableCode.RULES_EXPERIMENTAL || AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
        }

        // is this within our techbase?
        switch( m.GetLoadout().GetTechBase() ) {
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

        // is the 'Mech primitive and is this equipment allowed?
        if( m.IsPrimitive() ) {
            if( m.IsIndustrialmech() ) {
                if( ! AC.IsPIMAllowed() ) { return false; }
            } else {
                if( ! AC.IsPBMAllowed() ) { return false; }
            }
        } else {
            if( AC.IsPrimitiveOnly() ) { return false; }
        }

        // are we restricting by year?
        if( m.IsYearRestricted() ) {
            // we are.
            switch( m.GetLoadout().GetTechBase() ) {
                case AvailableCode.TECH_INNER_SPHERE:
                    if( AC.WentExtinctIS() ) {
                        if( AC.WasReIntrodIS() ) {
                            if( ( m.GetYear() >= AC.GetISIntroDate() && m.GetYear() < AC.GetISExtinctDate() ) || m.GetYear() >= AC.GetISReIntroDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if( m.GetYear() >= AC.GetISIntroDate() && m.GetYear() < AC.GetISExtinctDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    } else {
                        if( m.GetYear() >= AC.GetISIntroDate() ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                case AvailableCode.TECH_CLAN:
                    if( AC.WentExtinctCL() ) {
                        if( AC.WasReIntrodCL() ) {
                            if( ( m.GetYear() >= AC.GetCLIntroDate() && m.GetYear() < AC.GetCLExtinctDate() ) || m.GetYear() >= AC.GetCLReIntroDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if( m.GetYear() >= AC.GetCLIntroDate() && m.GetYear() < AC.GetCLExtinctDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    } else {
                        if( m.GetYear() >= AC.GetCLIntroDate() ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                case AvailableCode.TECH_BOTH:
                    boolean Okay_IS = false, Okay_CL = false;
                    if( AC.WentExtinctIS() ) {
                        if( AC.WasReIntrodIS() ) {
                            if( ( m.GetYear() >= AC.GetISIntroDate() && m.GetYear() < AC.GetISExtinctDate() ) || m.GetYear() >= AC.GetISReIntroDate() ) {
                                Okay_IS = true;
                            } else {
                                Okay_IS = false;
                            }
                        } else {
                            if( m.GetYear() >= AC.GetISIntroDate() && m.GetYear() < AC.GetISExtinctDate() ) {
                                Okay_IS = true;
                            } else {
                                Okay_IS = false;
                            }
                        }
                    } else {
                        if( m.GetYear() >= AC.GetISIntroDate() ) {
                            Okay_IS = true;
                        } else {
                            Okay_IS = false;
                        }
                    }
                    if( AC.WentExtinctCL() ) {
                        if( AC.WasReIntrodCL() ) {
                            if( ( m.GetYear() >= AC.GetCLIntroDate() && m.GetYear() < AC.GetCLExtinctDate() ) || m.GetYear() >= AC.GetCLReIntroDate() ) {
                                Okay_CL = true;
                            } else {
                                Okay_CL = false;
                            }
                        } else {
                            if( m.GetYear() >= AC.GetCLIntroDate() && m.GetYear() < AC.GetCLExtinctDate() ) {
                                Okay_CL = true;
                            } else {
                                Okay_CL = false;
                            }
                        }
                    } else {
                        if( m.GetYear() >= AC.GetCLIntroDate() ) {
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
            switch( m.GetEra() ) {
            case AvailableCode.ERA_STAR_LEAGUE:
                switch( m.GetLoadout().GetTechBase() ) {
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
                switch( m.GetLoadout().GetTechBase() ) {
                    case AvailableCode.TECH_INNER_SPHERE:
                        if( m.GetRulesLevel() > AvailableCode.RULES_TOURNAMENT ) {
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
                switch( m.GetLoadout().GetTechBase() ) {
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
                switch( m.GetLoadout().GetTechBase() ) {
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
            case AvailableCode.ERA_ALL:
                // the "All" era.
                return true;
            }
        }

        return false;
    }

    public static boolean IsAllowed( AvailableCode AC, int RulesLevel, int TechBase, boolean Primitive, boolean Industrial, int Era, boolean Restrict, int Year ) {
        // check an available code to see if it can be used legally

        // ensure it's within our rules-level first
        switch( RulesLevel ) {
            case AvailableCode.RULES_INTRODUCTORY:
                // tournament legal
                if( Industrial ) {
                    if( AC.GetRulesLevel_IM() != AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() != AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
            case AvailableCode.RULES_TOURNAMENT:
                // tournament legal
                if( Industrial ) {
                    if( AC.GetRulesLevel_IM() > AvailableCode.RULES_TOURNAMENT || AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() > AvailableCode.RULES_TOURNAMENT || AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
            case AvailableCode.RULES_ADVANCED:
                // advanced rules
                if( Industrial ) {
                    if( AC.GetRulesLevel_IM() > AvailableCode.RULES_ADVANCED || AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() > AvailableCode.RULES_ADVANCED || AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
            case AvailableCode.RULES_EXPERIMENTAL:
                // experimental rules.  everything allowed.
                if( Industrial ) {
                    if( AC.GetRulesLevel_IM() > AvailableCode.RULES_EXPERIMENTAL || AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() > AvailableCode.RULES_EXPERIMENTAL || AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
            case AvailableCode.RULES_ERA_SPECIFIC:
                if( Industrial ) {
                    if( AC.GetRulesLevel_IM() > AvailableCode.RULES_ERA_SPECIFIC || AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() > AvailableCode.RULES_ERA_SPECIFIC || AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
            default:
                // Unallowed or Era Specific.  everything allowed until we know better.
                if( Industrial ) {
                    if( AC.GetRulesLevel_IM() > AvailableCode.RULES_EXPERIMENTAL || AC.GetRulesLevel_IM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                } else {
                    if( AC.GetRulesLevel_BM() > AvailableCode.RULES_EXPERIMENTAL || AC.GetRulesLevel_BM() < AvailableCode.RULES_INTRODUCTORY ) { return false; }
                }
                break;
        }

        // is this within our techbase?
        switch( TechBase ) {
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

        // is the 'Mech primitive and is this equipment allowed?
        if( Primitive ) {
            if( Industrial ) {
                if( ! AC.IsPIMAllowed() ) { return false; }
            } else {
                if( ! AC.IsPBMAllowed() ) { return false; }
            }
        } else {
            if( AC.IsPrimitiveOnly() ) { return false; }
        }

        // are we restricting by year?
        if( Restrict ) {
            // we are.
            switch( TechBase ) {
                case AvailableCode.TECH_INNER_SPHERE:
                    if( AC.WentExtinctIS() ) {
                        if( AC.WasReIntrodIS() ) {
                            if( ( Year >= AC.GetISIntroDate() && Year < AC.GetISExtinctDate() ) || Year >= AC.GetISReIntroDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if( Year >= AC.GetISIntroDate() && Year < AC.GetISExtinctDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    } else {
                        if( Year >= AC.GetISIntroDate() ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                case AvailableCode.TECH_CLAN:
                    if( AC.WentExtinctCL() ) {
                        if( AC.WasReIntrodCL() ) {
                            if( ( Year >= AC.GetCLIntroDate() && Year < AC.GetCLExtinctDate() ) || Year >= AC.GetCLReIntroDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if( Year >= AC.GetCLIntroDate() && Year < AC.GetCLExtinctDate() ) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    } else {
                        if( Year >= AC.GetCLIntroDate() ) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                case AvailableCode.TECH_BOTH:
                    boolean Okay_IS = false, Okay_CL = false;
                    if( AC.WentExtinctIS() ) {
                        if( AC.WasReIntrodIS() ) {
                            if( ( Year >= AC.GetISIntroDate() && Year < AC.GetISExtinctDate() ) || Year >= AC.GetISReIntroDate() ) {
                                Okay_IS = true;
                            } else {
                                Okay_IS = false;
                            }
                        } else {
                            if( Year >= AC.GetISIntroDate() && Year < AC.GetISExtinctDate() ) {
                                Okay_IS = true;
                            } else {
                                Okay_IS = false;
                            }
                        }
                    } else {
                        if( Year >= AC.GetISIntroDate() ) {
                            Okay_IS = true;
                        } else {
                            Okay_IS = false;
                        }
                    }
                    if( AC.WentExtinctCL() ) {
                        if( AC.WasReIntrodCL() ) {
                            if( ( Year >= AC.GetCLIntroDate() && Year < AC.GetCLExtinctDate() ) || Year >= AC.GetCLReIntroDate() ) {
                                Okay_CL = true;
                            } else {
                                Okay_CL = false;
                            }
                        } else {
                            if( Year >= AC.GetCLIntroDate() && Year < AC.GetCLExtinctDate() ) {
                                Okay_CL = true;
                            } else {
                                Okay_CL = false;
                            }
                        }
                    } else {
                        if( Year >= AC.GetCLIntroDate() ) {
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
            switch( Era ) {
            case AvailableCode.ERA_STAR_LEAGUE:
                switch( TechBase ) {
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
                switch( TechBase ) {
                    case AvailableCode.TECH_INNER_SPHERE:
                        if( RulesLevel > AvailableCode.RULES_TOURNAMENT ) {
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
                switch( TechBase ) {
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
                switch( TechBase ) {
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
        if( w.IsStreak() ) { roll = 10; }
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
            boolean HAG = w.CritName().contains( "HAG" );
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
                } else if( HAG ) {
                    return GetAverageClusterHits( w, -2 );
                } else if( Ballistic &! Rotary ) {
                    // this covers LB-X cannons in general
                    return GetAverageClusterHits( w, 0 );
                } else {
                    return w.GetDamageLong() * GetAverageClusterHits( w, 0 );
                }
            } else if( range > w.GetRangeShort() ) {
                if( Streak ) {
                    return w.GetDamageMedium() * w.ClusterSize();
                } else if( Ultra ) {
                    return w.GetDamageMedium();
                } else if( HAG || ( Ballistic &! Rotary ) ) {
                    return GetAverageClusterHits( w, 0 );
                } else {
                    return w.GetDamageMedium() * GetAverageClusterHits( w, 0 );
                }
            } else {
                if( Streak ) {
                    return w.GetDamageShort() * w.ClusterSize();
                } else if( Ultra ) {
                    return w.GetDamageShort();
                } else if( HAG ) {
                    return GetAverageClusterHits( w, 2 );
                } else if( Ballistic &! Rotary ) {
                    return GetAverageClusterHits( w, 0 );
                } else {
                    return w.GetDamageShort() * GetAverageClusterHits( w, 0 );
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
            boolean HAG = w.CritName().contains( "HAG" );
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
                } else if( HAG ) {
                    return GetAverageClusterHits( w, -2 );
                } else if( Ballistic &! Rotary ) {
                    // this covers LB-X cannons in general
                    return GetAverageClusterHits( w, 0 );
                } else {
                    return a.GetDamageLong() * GetAverageClusterHits( w, 0 );
                }
            } else if( range > a.GetShortRange() ) {
                if( Streak ) {
                    return a.GetDamageMedium() * a.ClusterSize();
                } else if( Ultra ) {
                    return a.GetDamageMedium();
                } else if( HAG || ( Ballistic &! Rotary ) ) {
                    return GetAverageClusterHits( w, 0 );
                } else {
                    return a.GetDamageMedium() * GetAverageClusterHits( w, 0 );
                }
            } else {
                if( Streak ) {
                    return a.GetDamageShort() * a.ClusterSize();
                } else if( Ultra ) {
                    return a.GetDamageShort();
                } else if( HAG ) {
                    return GetAverageClusterHits( w, 2 );
                } else if( Ballistic &! Rotary ) {
                    return GetAverageClusterHits( w, 0 );
                } else {
                    return a.GetDamageShort() * GetAverageClusterHits( w, 0 );
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
}

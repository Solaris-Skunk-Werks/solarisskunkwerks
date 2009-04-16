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

package ssw;

import ssw.components.*;

public class CommonTools {
    private final static float[][] BVMods = {
        { 2.8f, 2.63f, 2.45f, 2.28f, 2.01f, 1.82f, 1.75f, 1.67f, 1.59f },
        { 2.56f, 2.4f, 2.24f, 2.08f, 1.84f, 1.6f, 1.58f, 1.51f, 1.44f },
        { 2.24f, 2.1f, 1.96f, 1.82f, 1.61f, 1.4f, 1.33f, 1.31f, 1.25f },
        { 1.92f, 1.8f, 1.68f, 1.56f, 1.38f, 1.2f, 1.14f, 1.08f, 1.06f },
        { 1.6f, 1.5f, 1.4f, 1.3f, 1.15f, 1.0f, 0.95f, 0.9f, 0.85f },
        { 1.5f, 1.35f, 1.26f, 1.17f, 1.04f, 0.9f, 0.86f, 0.81f, 0.77f },
        { 1.43f, 1.33f, 1.19f, 1.11f, 0.98f, 0.85f, 0.81f, 0.77f, 0.72f },
        { 1.36f, 1.26f, 1.16f, 1.04f, 0.92f, 0.8f, 0.76f, 0.72f, 0.68f },
        { 1.28f, 1.19f, 1.1f, 1.01f, 0.86f, 0.75f, 0.71f, 0.68f, 0.64f }
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
/*
    public static String GetCorrectLookupName( Mech m, ifState s ) {
        switch( m.GetTechBase() ) {
            case AvailableCode.TECH_INNER_SPHERE: case AvailableCode.TECH_CLAN:
                return s.GetLookupName();
            case AvailableCode.TECH_BOTH:
                if( s.HasCounterpart() ) {
                    if( s.GetAvailability().GetTechBase() == AvailableCode.TECH_INNER_SPHERE ) {
                        return "(IS) " + s.GetLookupName();
                    } else {
                        return "(CL) " + s.GetLookupName();
                    }
                } else {
                    return s.GetLookupName();
                }
        }
        return "";
    }
*/
    public static String GetAggregateReportBV( abPlaceable p ) {
        // since an item may have both offensive and defensive BV, this gives us
        // an aggregate battle value string for reporting
        String result = "";
        if( p.GetOffensiveBV() > 0.0f ) {
            result += p.GetOffensiveBV() + "";
            if( p.GetDefensiveBV() > 0.0f ) {
                result += " / " + p.GetDefensiveBV() + "(D)";
            }
        } else if( p.GetDefensiveBV() > 0.0f ) {
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
        switch( m.GetTechBase() ) {
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
        if( m.IsYearRestricted() ) {
            // we are.
            switch( m.GetTechBase() ) {
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
                switch( m.GetTechBase() ) {
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
                switch( m.GetTechBase() ) {
                    case AvailableCode.TECH_INNER_SPHERE:
                        if( AC.GetISSWCode() < 'F' ) {
                            return true;
                        } else {
                            return false;
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
                switch( m.GetTechBase() ) {
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
                switch( m.GetTechBase() ) {
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

    public static float GetAdjustedBV( float BV, int Gunnery, int Piloting ) {
        return BV * BVMods[Gunnery][Piloting];
    }
}

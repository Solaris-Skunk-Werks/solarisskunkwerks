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

package visitors;

import components.*;
import java.util.prefs.Preferences;

public class VSetArmorTonnage implements ifVisitor {
    private double ArmorTons;
    private int ArmorPoints[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private int CTRPerc = MechArmor.DEFAULT_CTR_ARMOR_PERCENT,
                STRPerc = MechArmor.DEFAULT_STR_ARMOR_PERCENT,
                ArmorPriority = MechArmor.ARMOR_PRIORITY_TORSO;
    private Preferences Prefs;

    public VSetArmorTonnage( Preferences p ) {
        Prefs = p;
    }

    public void Clear() {
        for (int i = 0; i < ArmorPoints.length; i++) {
            ArmorPoints[i] = 0;
        }
    }
    
    public void SetClan( boolean clan ) {
    }

    public void LoadLocations(LocationIndex[] locs) {
        // does nothing here, but may later.
    }

    public void SetArmorTonnage( double tons ) {
        ArmorTons = tons;
    }

    public void Visit(Mech m) {
        // only the armor changes, so pass us off
        CTRPerc = Prefs.getInt( "ArmorCTRPercent", MechArmor.DEFAULT_CTR_ARMOR_PERCENT );
        STRPerc = Prefs.getInt( "ArmorSTRPercent", MechArmor.DEFAULT_STR_ARMOR_PERCENT );
        ArmorPriority = Prefs.getInt( "ArmorPriority", MechArmor.ARMOR_PRIORITY_TORSO );
        MechArmor a = m.GetArmor();

        // set the armor tonnage
        // zero out the armor to begin with.
        a.SetArmor( LocationIndex.MECH_LOC_HD, 0 );
        a.SetArmor( LocationIndex.MECH_LOC_CT, 0 );
        a.SetArmor( LocationIndex.MECH_LOC_CTR, 0 );
        a.SetArmor( LocationIndex.MECH_LOC_LT, 0 );
        a.SetArmor( LocationIndex.MECH_LOC_LTR, 0 );
        a.SetArmor( LocationIndex.MECH_LOC_RT, 0 );
        a.SetArmor( LocationIndex.MECH_LOC_RTR, 0 );
        a.SetArmor( LocationIndex.MECH_LOC_LA, 0 );
        a.SetArmor( LocationIndex.MECH_LOC_RA, 0 );
        a.SetArmor( LocationIndex.MECH_LOC_LL, 0 );
        a.SetArmor( LocationIndex.MECH_LOC_RL, 0 );

        if( ArmorTons >= a.GetMaxTonnage() ) {
            // assume the user simply wanted to maximize the armor
            a.SetArmor( LocationIndex.MECH_LOC_HD, 9 );
            a.SetArmor( LocationIndex.MECH_LOC_LA, a.GetLocationMax( LocationIndex.MECH_LOC_LA ) );
            a.SetArmor( LocationIndex.MECH_LOC_RA, a.GetLocationMax( LocationIndex.MECH_LOC_RA ) );
            a.SetArmor( LocationIndex.MECH_LOC_LL, a.GetLocationMax( LocationIndex.MECH_LOC_LL ) );
            a.SetArmor( LocationIndex.MECH_LOC_RL, a.GetLocationMax( LocationIndex.MECH_LOC_RL ) );
            int rear = Math.round( a.GetLocationMax( LocationIndex.MECH_LOC_CT ) * CTRPerc / 100 );
            a.SetArmor( LocationIndex.MECH_LOC_CTR, rear );
            a.SetArmor( LocationIndex.MECH_LOC_CT, a.GetLocationMax( LocationIndex.MECH_LOC_CT ) - rear );
            rear = Math.round( a.GetLocationMax( LocationIndex.MECH_LOC_LT ) * STRPerc / 100 );
            a.SetArmor( LocationIndex.MECH_LOC_LTR, rear );
            a.SetArmor( LocationIndex.MECH_LOC_LT, a.GetLocationMax( LocationIndex.MECH_LOC_LT ) - rear );
            rear = Math.round( a.GetLocationMax( LocationIndex.MECH_LOC_RT ) * STRPerc / 100 );
            a.SetArmor( LocationIndex.MECH_LOC_RTR, rear );
            a.SetArmor( LocationIndex.MECH_LOC_RT, a.GetLocationMax( LocationIndex.MECH_LOC_RT ) - rear );
        } else if( ArmorTons <= 0 ) {
            // assume the user wants to zero the armor
            // since we've already zero'd the armor, just return
            return;
        } else {
            // zero the armor array
            for( int i = 0; i < 11; i++ ) {
                ArmorPoints[i] = 0;
            }

            // allocate the armor
            AllocateArmor( a );

            // fix the armor
            FixArmor( a );
        }
    }

    public void Visit(CombatVehicle v) {
        Clear();

        // only the armor changes, so pass us off
        CTRPerc = Prefs.getInt( "ArmorFrontPercent", CVArmor.DEFAULT_FRONT_ARMOR_PERCENT );
        STRPerc = Prefs.getInt( "ArmorTurretPercent", CVArmor.DEFAULT_TURRET_ARMOR_PERCENT );
        ArmorPriority = Prefs.getInt( "ArmorPriority", CVArmor.ARMOR_PRIORITY_FRONT );
        CVArmor a = v.GetArmor();

        // remove all existing amounts so we can reset
        a.ClearArmorValues();
        
        if( ArmorTons >= a.GetMaxTonnage() ) {
            a.Maximize();
        } else if( ArmorTons <= 0 ) {
            // assume the user wants to zero the armor
            // since we've already zero'd the armor, just return
            return;
        } else {
            // allocate the armor
            AllocateArmor( a );
            
            // fix the armor
            FixArmor( a );
            
            if ( a.GetArmorValue() < a.GetArmorPoints(ArmorTons) ) {
                AllocateExtra(a, (a.GetArmorPoints(ArmorTons)-a.GetArmorValue()));
                FixArmor( a );
            }            
        }
    }

    private void AllocateArmor( MechArmor a ) {
        // testing out a new allocation routine
        // round the armor tonnage up to the nearest half ton
        double MidTons = ( (double) Math.floor( ArmorTons * 2.0f ) ) * 0.5f;

        // find the AV we get from this tonnage amount
        int AV = (int) ( Math.floor( MidTons * 16 * a.GetAVMult() ) );

        ArmorPoints[LocationIndex.MECH_LOC_HD] = (int) Math.floor( AV * 0.06f );
        ArmorPoints[LocationIndex.MECH_LOC_CT] = (int) Math.floor( AV * 0.15f );
        ArmorPoints[LocationIndex.MECH_LOC_CTR] = (int) Math.round( ArmorPoints[LocationIndex.MECH_LOC_CT] * CTRPerc / 100 );
        switch( Prefs.getInt( "ArmorPriority", MechArmor.ARMOR_PRIORITY_TORSO ) ) {
        case 0:
            // torsos
            ArmorPoints[LocationIndex.MECH_LOC_LT] = (int) Math.floor( AV * 0.13f );
            ArmorPoints[LocationIndex.MECH_LOC_LTR] = (int) Math.round( ArmorPoints[LocationIndex.MECH_LOC_LT] * STRPerc / 100 );
            ArmorPoints[LocationIndex.MECH_LOC_RT] = (int) Math.floor( AV * 0.13f );
            ArmorPoints[LocationIndex.MECH_LOC_RTR] = (int) Math.round( ArmorPoints[LocationIndex.MECH_LOC_RT] * STRPerc / 100 );
            ArmorPoints[LocationIndex.MECH_LOC_RA] = (int) Math.floor( AV * 0.09f );
            ArmorPoints[LocationIndex.MECH_LOC_LA] = (int) Math.floor( AV * 0.09f );
            ArmorPoints[LocationIndex.MECH_LOC_LL] = (int) Math.floor( AV * 0.12f );
            ArmorPoints[LocationIndex.MECH_LOC_RL] = (int) Math.floor( AV * 0.12f );
            break;
        case 1:
            // arms
            ArmorPoints[LocationIndex.MECH_LOC_LT] = (int) Math.floor( AV * 0.13f );
            ArmorPoints[LocationIndex.MECH_LOC_LTR] = (int) Math.round( ArmorPoints[LocationIndex.MECH_LOC_LT] * STRPerc / 100 );
            ArmorPoints[LocationIndex.MECH_LOC_RT] = (int) Math.floor( AV * 0.13f );
            ArmorPoints[LocationIndex.MECH_LOC_RTR] = (int) Math.round( ArmorPoints[LocationIndex.MECH_LOC_RT] * STRPerc / 100 );
            ArmorPoints[LocationIndex.MECH_LOC_RA] = (int) Math.floor( AV * 0.12f );
            ArmorPoints[LocationIndex.MECH_LOC_LA] = (int) Math.floor( AV * 0.12f );
            ArmorPoints[LocationIndex.MECH_LOC_LL] = (int) Math.floor( AV * 0.09f );
            ArmorPoints[LocationIndex.MECH_LOC_RL] = (int) Math.floor( AV * 0.09f );
            break;
        case 2:
            // legs
            ArmorPoints[LocationIndex.MECH_LOC_LT] = (int) Math.floor( AV * 0.12f );
            ArmorPoints[LocationIndex.MECH_LOC_LTR] = (int) Math.round( ArmorPoints[LocationIndex.MECH_LOC_LT] * STRPerc / 100 );
            ArmorPoints[LocationIndex.MECH_LOC_RT] = (int) Math.floor( AV * 0.12f );
            ArmorPoints[LocationIndex.MECH_LOC_RTR] = (int) Math.round( ArmorPoints[LocationIndex.MECH_LOC_RT] * STRPerc / 100 );
            ArmorPoints[LocationIndex.MECH_LOC_RA] = (int) Math.floor( AV * 0.09f );
            ArmorPoints[LocationIndex.MECH_LOC_LA] = (int) Math.floor( AV * 0.09f );
            ArmorPoints[LocationIndex.MECH_LOC_LL] = (int) Math.floor( AV * 0.13f );
            ArmorPoints[LocationIndex.MECH_LOC_RL] = (int) Math.floor( AV * 0.13f );
            break;
        }
        AV -= CurrentArmorAV();

        // check for maximums and return a new AV for round-robin allocation
        AV = CheckMaximums( a, AV );

        // allocate the extra round-robin fashion
        AllocateExtra( a, AV );

        // finish up with a bit of symmetry
        Symmetrize( a );
    }

    private void AllocateArmor( CVArmor a ) {
        // testing out a new allocation routine
        // round the armor tonnage up to the nearest half ton
        double MidTons = ( (double) Math.floor( ArmorTons * 2.0f ) ) * 0.5f;

        // find the AV we get from this tonnage amount
        int AV = a.GetArmorPoints(MidTons); //(int) ( Math.floor( MidTons * 16 * a.GetAVMult() ) );

        // remove all existing amounts so we can reset
        a.ClearArmorValues();
        
        if ( a.GetOwner().IsVTOL() ) {
            ArmorPoints[LocationIndex.CV_LOC_ROTOR] = 2;
            AV -= 2;
        }
        int split = AV / a.GetOwner().getLocationCount();
        
        if ( a.GetOwner().isHasTurret1() ) ArmorPoints[LocationIndex.CV_LOC_TURRET1] = split;
        if ( a.GetOwner().isHasTurret2() ) ArmorPoints[LocationIndex.CV_LOC_TURRET2] = split;
        
        ArmorPoints[LocationIndex.CV_LOC_LEFT] = split;
        ArmorPoints[LocationIndex.CV_LOC_RIGHT] = split;
        
        ArmorPoints[LocationIndex.CV_LOC_FRONT] = (int)Math.ceil((split * 2) * .6);
        ArmorPoints[LocationIndex.CV_LOC_REAR] = (split * 2)-ArmorPoints[LocationIndex.CV_LOC_FRONT];
    }

    private int CheckMaximums( MechArmor a, int AV ) {
        int result = AV;

        // head check
        if( ArmorPoints[LocationIndex.MECH_LOC_HD] > a.GetLocationMax( LocationIndex.MECH_LOC_HD ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.MECH_LOC_HD] - a.GetLocationMax( LocationIndex.MECH_LOC_HD );
            ArmorPoints[LocationIndex.MECH_LOC_HD] = a.GetLocationMax( LocationIndex.MECH_LOC_HD );
            result += mid;
        }

        // CT check
        if( ArmorPoints[LocationIndex.MECH_LOC_CT] + ArmorPoints[LocationIndex.MECH_LOC_CTR] > a.GetLocationMax( LocationIndex.MECH_LOC_CT ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.MECH_LOC_CT] + ArmorPoints[LocationIndex.MECH_LOC_CTR] - a.GetLocationMax( LocationIndex.MECH_LOC_CT );
            int rear = Math.round( a.GetLocationMax( LocationIndex.MECH_LOC_CT ) * CTRPerc * 0.01f + 0.49f );
            ArmorPoints[LocationIndex.MECH_LOC_CTR] = rear;
            ArmorPoints[LocationIndex.MECH_LOC_CT] = a.GetLocationMax( LocationIndex.MECH_LOC_CT ) - rear;
            result += mid;
        }

        // LT check
        if( ArmorPoints[LocationIndex.MECH_LOC_LT] + ArmorPoints[LocationIndex.MECH_LOC_LTR] > a.GetLocationMax( LocationIndex.MECH_LOC_LT ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.MECH_LOC_LT] + ArmorPoints[LocationIndex.MECH_LOC_LTR] - a.GetLocationMax( LocationIndex.MECH_LOC_LT );
            int rear = Math.round( a.GetLocationMax( LocationIndex.MECH_LOC_LT ) * STRPerc * 0.01f + 0.49f );
            ArmorPoints[LocationIndex.MECH_LOC_LTR] = rear;
            ArmorPoints[LocationIndex.MECH_LOC_LT] = a.GetLocationMax( LocationIndex.MECH_LOC_LT ) - rear;
            result += mid;
        }

        // RT check
        if( ArmorPoints[LocationIndex.MECH_LOC_RT] + ArmorPoints[LocationIndex.MECH_LOC_RTR] > a.GetLocationMax( LocationIndex.MECH_LOC_RT ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.MECH_LOC_RT] + ArmorPoints[LocationIndex.MECH_LOC_RTR] - a.GetLocationMax( LocationIndex.MECH_LOC_RT );
            int rear = Math.round( a.GetLocationMax( LocationIndex.MECH_LOC_RT ) * STRPerc * 0.01f + 0.49f );
            ArmorPoints[LocationIndex.MECH_LOC_RTR] = rear;
            ArmorPoints[LocationIndex.MECH_LOC_RT] = a.GetLocationMax( LocationIndex.MECH_LOC_RT ) - rear;
            result += mid;
        }

        // LA check
        if( ArmorPoints[LocationIndex.MECH_LOC_LA] > a.GetLocationMax( LocationIndex.MECH_LOC_LA ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.MECH_LOC_LA] - a.GetLocationMax( LocationIndex.MECH_LOC_LA );
            ArmorPoints[LocationIndex.MECH_LOC_LA] = a.GetLocationMax( LocationIndex.MECH_LOC_LA );
            result += mid;
        }

        // RA check
        if( ArmorPoints[LocationIndex.MECH_LOC_RA] > a.GetLocationMax( LocationIndex.MECH_LOC_RA ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.MECH_LOC_RA] - a.GetLocationMax( LocationIndex.MECH_LOC_RA );
            ArmorPoints[LocationIndex.MECH_LOC_RA] = a.GetLocationMax( LocationIndex.MECH_LOC_RA );
            result += mid;
        }

        // LL check
        if( ArmorPoints[LocationIndex.MECH_LOC_LL] > a.GetLocationMax( LocationIndex.MECH_LOC_LL ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.MECH_LOC_LL] - a.GetLocationMax( LocationIndex.MECH_LOC_LL );
            ArmorPoints[LocationIndex.MECH_LOC_LL] = a.GetLocationMax( LocationIndex.MECH_LOC_LL );
            result += mid;
        }

        // RL check
        if( ArmorPoints[LocationIndex.MECH_LOC_RL] > a.GetLocationMax( LocationIndex.MECH_LOC_RL ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.MECH_LOC_RL] - a.GetLocationMax( LocationIndex.MECH_LOC_RL );
            ArmorPoints[LocationIndex.MECH_LOC_RL] = a.GetLocationMax( LocationIndex.MECH_LOC_RL );
            result += mid;
        }

        return result;
    }

    private int CheckMaximums( CVArmor a, int AV ) {
        int result = AV;

        // head check
        if( ArmorPoints[LocationIndex.CV_LOC_FRONT] > a.GetLocationMax( LocationIndex.CV_LOC_FRONT ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.CV_LOC_FRONT] - a.GetLocationMax( LocationIndex.CV_LOC_FRONT );
            ArmorPoints[LocationIndex.CV_LOC_FRONT] = a.GetLocationMax( LocationIndex.CV_LOC_FRONT );
            result += mid;
        }

        if( ArmorPoints[LocationIndex.CV_LOC_LEFT] > a.GetLocationMax( LocationIndex.CV_LOC_LEFT ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.CV_LOC_LEFT] - a.GetLocationMax( LocationIndex.CV_LOC_LEFT );
            ArmorPoints[LocationIndex.CV_LOC_LEFT] = a.GetLocationMax( LocationIndex.CV_LOC_LEFT );
            result += mid;
        }

        if( ArmorPoints[LocationIndex.CV_LOC_RIGHT] > a.GetLocationMax( LocationIndex.CV_LOC_RIGHT ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.CV_LOC_RIGHT] - a.GetLocationMax( LocationIndex.CV_LOC_RIGHT );
            ArmorPoints[LocationIndex.CV_LOC_RIGHT] = a.GetLocationMax( LocationIndex.CV_LOC_RIGHT );
            result += mid;
        }

        if( ArmorPoints[LocationIndex.CV_LOC_REAR] > a.GetLocationMax( LocationIndex.CV_LOC_REAR ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.CV_LOC_REAR] - a.GetLocationMax( LocationIndex.CV_LOC_REAR );
            ArmorPoints[LocationIndex.CV_LOC_REAR] = a.GetLocationMax( LocationIndex.CV_LOC_REAR );
            result += mid;
        }

        if( a.GetOwner().isHasTurret1() && ArmorPoints[LocationIndex.CV_LOC_TURRET1] > a.GetLocationMax( LocationIndex.CV_LOC_TURRET1 ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.CV_LOC_TURRET1] - a.GetLocationMax( LocationIndex.CV_LOC_TURRET1 );
            ArmorPoints[LocationIndex.CV_LOC_TURRET1] = a.GetLocationMax( LocationIndex.CV_LOC_TURRET1 );
            result += mid;
        }

        if( a.GetOwner().isHasTurret2() && ArmorPoints[LocationIndex.CV_LOC_TURRET2] > a.GetLocationMax( LocationIndex.CV_LOC_TURRET2 ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = ArmorPoints[LocationIndex.CV_LOC_TURRET2] - a.GetLocationMax( LocationIndex.CV_LOC_TURRET2 );
            ArmorPoints[LocationIndex.CV_LOC_TURRET2] = a.GetLocationMax( LocationIndex.CV_LOC_TURRET2 );
            result += mid;
        }

        return result;
    }

    private void AllocateExtra( MechArmor a, int AV ) {
        // recursive routine for allocating the armor.  Pass in the AV we have
        // to distribute.   We'll do it round-robin style by priority
        boolean HeadMax = Prefs.getBoolean( "ArmorMaxHead", true );

        // head first
        if( ArmorPoints[LocationIndex.MECH_LOC_HD] < a.GetLocationMax( LocationIndex.MECH_LOC_HD ) ) {
            if( AV > 0 ) {
                if( HeadMax ) {
                    // head maximum
                    if( AV > 9 - ArmorPoints[LocationIndex.MECH_LOC_HD] ) {
                        // enough to do the job
                        AV -= ( 9 - ArmorPoints[LocationIndex.MECH_LOC_HD] );
                        ArmorPoints[LocationIndex.MECH_LOC_HD] = 9;
                    } else {
                        // armor only goes to the head, then we're done
                        ArmorPoints[LocationIndex.MECH_LOC_HD] += AV;
                        AV = 0;
                        return;
                    }
                } else {
                    // head equal distribution
                    ArmorPoints[LocationIndex.MECH_LOC_HD]++;
                    AV--;
                }
            } else {
                // all done
                return;
            }
        }

        // center torso and center rear next
        if( ArmorPoints[LocationIndex.MECH_LOC_CT] + ArmorPoints[LocationIndex.MECH_LOC_CTR] < a.GetLocationMax( LocationIndex.MECH_LOC_CT ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                if( ( Math.round( ArmorPoints[LocationIndex.MECH_LOC_CT] * CTRPerc * 0.01f + 0.49f ) ) > ArmorPoints[LocationIndex.MECH_LOC_CTR] ) {
                    // allocate to the rear torso
                    ArmorPoints[LocationIndex.MECH_LOC_CTR]++;
                    AV--;
                } else {
                    // front torso
                    ArmorPoints[LocationIndex.MECH_LOC_CT]++;
                    AV--;
                }
            } else {
                // all done
                return;
            }
        }

        // Left torso and left rear first
        if( ArmorPoints[LocationIndex.MECH_LOC_LT] + ArmorPoints[LocationIndex.MECH_LOC_LTR] < a.GetLocationMax( LocationIndex.MECH_LOC_LT ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                if( ( Math.round( ArmorPoints[LocationIndex.MECH_LOC_LT] * STRPerc * 0.01f + 0.49f ) ) > ArmorPoints[LocationIndex.MECH_LOC_LTR] ) {
                    // allocate to the rear torso
                    ArmorPoints[LocationIndex.MECH_LOC_LTR]++;
                    AV--;
                } else {
                    // front torso
                    ArmorPoints[LocationIndex.MECH_LOC_LT]++;
                    AV--;
                }
            } else {
                // all done
                return;
            }
        }

        // now the right torso and right rear
        if( ArmorPoints[LocationIndex.MECH_LOC_RT] + ArmorPoints[LocationIndex.MECH_LOC_RTR] < a.GetLocationMax( LocationIndex.MECH_LOC_RT ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                if( ( Math.round( ArmorPoints[LocationIndex.MECH_LOC_RT] * STRPerc * 0.01f + 0.49f ) ) > ArmorPoints[LocationIndex.MECH_LOC_RTR] ) {
                    // allocate to the rear torso
                    ArmorPoints[LocationIndex.MECH_LOC_RTR]++;
                    AV--;
                } else {
                    // front torso
                    ArmorPoints[LocationIndex.MECH_LOC_RT]++;
                    AV--;
                }
            } else {
                // all done
                return;
            }
        }

        // left arm next
        if( ArmorPoints[LocationIndex.MECH_LOC_LA] < a.GetLocationMax( LocationIndex.MECH_LOC_LA ) ) {
            // haven't reached maximum yet
            if( AV > 0 ) {
                ArmorPoints[LocationIndex.MECH_LOC_LA]++;
                AV--;
            } else {
                // all done
                return;
            }
        }

        // right arm
        if( ArmorPoints[LocationIndex.MECH_LOC_RA] < a.GetLocationMax( LocationIndex.MECH_LOC_RA ) ) {
            // haven't reached maximum yet
            if( AV > 0 ) {
                ArmorPoints[LocationIndex.MECH_LOC_RA]++;
                AV--;
            } else {
                // all done
                return;
            }
        }

        // left leg
        if( ArmorPoints[LocationIndex.MECH_LOC_LL] < a.GetLocationMax( LocationIndex.MECH_LOC_LL ) ) {
            // haven't reached maximum yet
            if( AV > 0 ) {
                ArmorPoints[LocationIndex.MECH_LOC_LL]++;
                AV--;
            } else {
                // all done
                return;
            }
        }

        // right leg
        if( ArmorPoints[LocationIndex.MECH_LOC_RL] < a.GetLocationMax( LocationIndex.MECH_LOC_RL ) ) {
            // haven't reached maximum yet
            if( AV > 0 ) {
                ArmorPoints[LocationIndex.MECH_LOC_RL]++;
                AV--;
            } else {
                // all done
                return;
            }
        }

        // if there's any AV left, call this method again
        if( AV > 0 && CurrentArmorAV() < a.GetMaxArmor() ) {
            AllocateExtra( a, AV );
        }
    }

    private void AllocateExtra( CVArmor a, int AV ) {
        if( ArmorPoints[LocationIndex.CV_LOC_FRONT] < a.GetLocationMax( LocationIndex.CV_LOC_FRONT ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                ArmorPoints[LocationIndex.CV_LOC_FRONT]++;
                AV--;
            } else {
                // all done
                return;
            }
        }
        if( ArmorPoints[LocationIndex.CV_LOC_REAR] < a.GetLocationMax( LocationIndex.CV_LOC_REAR ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                ArmorPoints[LocationIndex.CV_LOC_REAR]++;
                AV--;
            } else {
                // all done
                return;
            }
        }
        if( a.GetOwner().isHasTurret1() && ArmorPoints[LocationIndex.CV_LOC_TURRET1] < a.GetLocationMax( LocationIndex.CV_LOC_TURRET1 ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                ArmorPoints[LocationIndex.CV_LOC_TURRET1]++;
                AV--;
            } else {
                // all done
                return;
            }
        }
        if( a.GetOwner().isHasTurret2() && ArmorPoints[LocationIndex.CV_LOC_TURRET2] < a.GetLocationMax( LocationIndex.CV_LOC_TURRET2 ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                ArmorPoints[LocationIndex.CV_LOC_TURRET2]++;
                AV--;
            } else {
                // all done
                return;
            }
        }
        /*
        if( ArmorPoints[LocationIndex.CV_LOC_LEFT] < a.GetLocationMax( LocationIndex.CV_LOC_LEFT ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.
                ArmorPoints[LocationIndex.CV_LOC_LEFT]++;
                AV--;
            } else {
                // all done
                return;
            }
        }
        if( ArmorPoints[LocationIndex.CV_LOC_RIGHT] < a.GetLocationMax( LocationIndex.CV_LOC_RIGHT ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                ArmorPoints[LocationIndex.CV_LOC_RIGHT]++;
                AV--;
            } else {
                // all done
                return;
            }
        }
        */
        // if there's any AV left, call this method again
        if( AV > 0 && CurrentArmorAV() < a.GetMaxArmor() ) {
            AllocateExtra( a, AV );
        }
    }

    private void Symmetrize( MechArmor a ) {
        // this method attempts to fix any non-symmetrical allocations in a
        // graceful manner.  To simplify, we're taking a point or two from the CT.
        // Since there should never be more than one or two pairs out of alignment
        // the CT shouldn't suffer much.  This will take some watching.

        // LT and RT first
        if( ArmorPoints[LocationIndex.MECH_LOC_LT] > ArmorPoints[LocationIndex.MECH_LOC_RT] || ArmorPoints[LocationIndex.MECH_LOC_LT] < ArmorPoints[LocationIndex.MECH_LOC_RT] ) {
            // torsos are out of alignment
            if( ArmorPoints[LocationIndex.MECH_LOC_LT] > ArmorPoints[LocationIndex.MECH_LOC_RT] ) {
                // left is bigger than right
                ArmorPoints[LocationIndex.MECH_LOC_CT]--;
                ArmorPoints[LocationIndex.MECH_LOC_RT]++;
            } else {
                // right is bigger than left
                ArmorPoints[LocationIndex.MECH_LOC_CT]--;
                ArmorPoints[LocationIndex.MECH_LOC_LT]++;
            }
        }

        // LA and RA next
        if( ArmorPoints[LocationIndex.MECH_LOC_LA] > ArmorPoints[LocationIndex.MECH_LOC_RA] || ArmorPoints[LocationIndex.MECH_LOC_LA] < ArmorPoints[LocationIndex.MECH_LOC_RA] ) {
            // arms are out of alignment
            if( ArmorPoints[LocationIndex.MECH_LOC_LA] > ArmorPoints[LocationIndex.MECH_LOC_RA] ) {
                // left is bigger than right
                ArmorPoints[LocationIndex.MECH_LOC_CT]--;
                ArmorPoints[LocationIndex.MECH_LOC_RA]++;
            } else {
                // right is bigger than left
                ArmorPoints[LocationIndex.MECH_LOC_CT]--;
                ArmorPoints[LocationIndex.MECH_LOC_LA]++;
            }
        }

        // LL and RL next
        if( ArmorPoints[LocationIndex.MECH_LOC_LL] > ArmorPoints[LocationIndex.MECH_LOC_RL] || ArmorPoints[LocationIndex.MECH_LOC_LL] < ArmorPoints[LocationIndex.MECH_LOC_RL] ) {
            // legs are out of alignment
            if( ArmorPoints[LocationIndex.MECH_LOC_LL] > ArmorPoints[LocationIndex.MECH_LOC_RL] ) {
                // left is bigger than right
                ArmorPoints[LocationIndex.MECH_LOC_CT]--;
                ArmorPoints[LocationIndex.MECH_LOC_RL]++;
            } else {
                // right is bigger than left
                ArmorPoints[LocationIndex.MECH_LOC_CT]--;
                ArmorPoints[LocationIndex.MECH_LOC_LL]++;
            }
        }

        // LTR and RTR last
        if( ArmorPoints[LocationIndex.MECH_LOC_LTR] > ArmorPoints[LocationIndex.MECH_LOC_RTR] || ArmorPoints[LocationIndex.MECH_LOC_LTR] < ArmorPoints[LocationIndex.MECH_LOC_RTR] ) {
            // rear torsos are out of alignment
            if( ArmorPoints[LocationIndex.MECH_LOC_LTR] > ArmorPoints[LocationIndex.MECH_LOC_RTR] ) {
                // left is bigger than right
                ArmorPoints[LocationIndex.MECH_LOC_CT]--;
                ArmorPoints[LocationIndex.MECH_LOC_RTR]++;
            } else {
                // right is bigger than left
                ArmorPoints[LocationIndex.MECH_LOC_CT]--;
                ArmorPoints[LocationIndex.MECH_LOC_LTR]++;
            }
        }
    }

    private void Symmetrize( CVArmor a ) {
        if( ArmorPoints[LocationIndex.CV_LOC_LEFT] > ArmorPoints[LocationIndex.CV_LOC_RIGHT] || ArmorPoints[LocationIndex.CV_LOC_LEFT] < ArmorPoints[LocationIndex.CV_LOC_RIGHT] ) {
            if( ArmorPoints[LocationIndex.CV_LOC_LEFT] > ArmorPoints[LocationIndex.CV_LOC_RIGHT] ) {
                // left is bigger than right
                ArmorPoints[LocationIndex.CV_LOC_TURRET1]--;
                ArmorPoints[LocationIndex.CV_LOC_RIGHT]++;
            } else {
                // right is bigger than left
                ArmorPoints[LocationIndex.CV_LOC_TURRET1]--;
                ArmorPoints[LocationIndex.CV_LOC_LEFT]++;
            }
        }

        if( ArmorPoints[LocationIndex.CV_LOC_FRONT] > ArmorPoints[LocationIndex.CV_LOC_REAR] || ArmorPoints[LocationIndex.CV_LOC_FRONT] < ArmorPoints[LocationIndex.CV_LOC_REAR] ) {
            // legs are out of alignment
            if( ArmorPoints[LocationIndex.CV_LOC_FRONT] > ArmorPoints[LocationIndex.CV_LOC_REAR] ) {
                // left is bigger than right
                ArmorPoints[LocationIndex.CV_LOC_TURRET1]--;
                ArmorPoints[LocationIndex.CV_LOC_FRONT]++;
            } else {
                // right is bigger than left
                ArmorPoints[LocationIndex.CV_LOC_TURRET1]--;
                ArmorPoints[LocationIndex.CV_LOC_REAR]++;
            }
        }

    }

    private void FixArmor( MechArmor a ) {
        // fixes the armor values to the mech
        a.SetArmor( LocationIndex.MECH_LOC_HD, ArmorPoints[LocationIndex.MECH_LOC_HD] );
        a.SetArmor( LocationIndex.MECH_LOC_CT, ArmorPoints[LocationIndex.MECH_LOC_CT] );
        a.SetArmor( LocationIndex.MECH_LOC_LT, ArmorPoints[LocationIndex.MECH_LOC_LT] );
        a.SetArmor( LocationIndex.MECH_LOC_RT, ArmorPoints[LocationIndex.MECH_LOC_RT] );
        a.SetArmor( LocationIndex.MECH_LOC_LA, ArmorPoints[LocationIndex.MECH_LOC_LA] );
        a.SetArmor( LocationIndex.MECH_LOC_RA, ArmorPoints[LocationIndex.MECH_LOC_RA] );
        a.SetArmor( LocationIndex.MECH_LOC_LL, ArmorPoints[LocationIndex.MECH_LOC_LL] );
        a.SetArmor( LocationIndex.MECH_LOC_RL, ArmorPoints[LocationIndex.MECH_LOC_RL] );
        a.SetArmor( LocationIndex.MECH_LOC_CTR, ArmorPoints[LocationIndex.MECH_LOC_CTR] );
        a.SetArmor( LocationIndex.MECH_LOC_LTR, ArmorPoints[LocationIndex.MECH_LOC_LTR] );
        a.SetArmor( LocationIndex.MECH_LOC_RTR, ArmorPoints[LocationIndex.MECH_LOC_RTR] );
    }

    private void FixArmor( CVArmor a ) {
        a.SetArmor( LocationIndex.CV_LOC_FRONT, ArmorPoints[LocationIndex.CV_LOC_FRONT] );
        a.SetArmor( LocationIndex.CV_LOC_LEFT, ArmorPoints[LocationIndex.CV_LOC_LEFT] );
        a.SetArmor( LocationIndex.CV_LOC_RIGHT, ArmorPoints[LocationIndex.CV_LOC_RIGHT] );
        a.SetArmor( LocationIndex.CV_LOC_REAR, ArmorPoints[LocationIndex.CV_LOC_REAR] );
        a.SetArmor( LocationIndex.CV_LOC_ROTOR, ArmorPoints[LocationIndex.CV_LOC_ROTOR]);
        a.SetArmor( LocationIndex.CV_LOC_TURRET1, ArmorPoints[LocationIndex.CV_LOC_TURRET1] );
        a.SetArmor( LocationIndex.CV_LOC_TURRET2, ArmorPoints[LocationIndex.CV_LOC_TURRET2] );
    }

    private int CurrentArmorAV() {
        // totals the current armor AV
        int result = 0;
        for( int i = 0; i < 11; i++ ) {
            result += ArmorPoints[i];
        }
        return result;
    }

    public void Visit( Infantry i ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( SupportVehicle s ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( BattleArmor b ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( Fighter f ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( Spaceship s ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( SpaceStation s ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( ProtoMech p ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( MobileStructure m ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( LargeSupportVehicle l ) throws Exception {
        // does nothing at the moment
    }

    public void Visit( Dropship d ) throws Exception {
        // does nothing at the moment
    }
}

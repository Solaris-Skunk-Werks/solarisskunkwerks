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

package ssw.visitors;

import ssw.*;
import ssw.components.*;

public class VSetArmorTonnage implements ifVisitor {
    private float ArmorTons;
    private int Armor[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private Options CurOpts;

    public VSetArmorTonnage( Options o ) {
        CurOpts = o;
    }

    public void SetClan( boolean clan ) {
    }

    public void LoadLocations(LocationIndex[] locs) {
        // does nothing here, but may later.
    }

    public void SetArmorTonnage( float tons ) {
        ArmorTons = tons;
    }

    public void Visit(Mech m) {
        // only the armor changes, so pass us off
        Armor a = m.GetArmor();

        // set the armor tonnage
        // zero out the armor to begin with.
        a.SetArmor( Constants.LOC_HD, 0 );
        a.SetArmor( Constants.LOC_CT, 0 );
        a.SetArmor( Constants.LOC_CTR, 0 );
        a.SetArmor( Constants.LOC_LT, 0 );
        a.SetArmor( Constants.LOC_LTR, 0 );
        a.SetArmor( Constants.LOC_RT, 0 );
        a.SetArmor( Constants.LOC_RTR, 0 );
        a.SetArmor( Constants.LOC_LA, 0 );
        a.SetArmor( Constants.LOC_RA, 0 );
        a.SetArmor( Constants.LOC_LL, 0 );
        a.SetArmor( Constants.LOC_RL, 0 );

        if( ArmorTons >= a.GetMaxTonnage() ) {
            // assume the user simply wanted to maximize the armor
            a.SetArmor( Constants.LOC_HD, 9 );
            a.SetArmor( Constants.LOC_LA, a.GetLocationMax( Constants.LOC_LA ) );
            a.SetArmor( Constants.LOC_RA, a.GetLocationMax( Constants.LOC_RA ) );
            a.SetArmor( Constants.LOC_LL, a.GetLocationMax( Constants.LOC_LL ) );
            a.SetArmor( Constants.LOC_RL, a.GetLocationMax( Constants.LOC_RL ) );
            int rear = Math.round( a.GetLocationMax( Constants.LOC_CT ) * CurOpts.Armor_CTRPercent / 100 );
            a.SetArmor( Constants.LOC_CTR, rear );
            a.SetArmor( Constants.LOC_CT, a.GetLocationMax( Constants.LOC_CT ) - rear );
            rear = Math.round( a.GetLocationMax( Constants.LOC_LT ) * CurOpts.Armor_STRPercent / 100 );
            a.SetArmor( Constants.LOC_LTR, rear );
            a.SetArmor( Constants.LOC_LT, a.GetLocationMax( Constants.LOC_LT ) - rear );
            rear = Math.round( a.GetLocationMax( Constants.LOC_RT ) * CurOpts.Armor_STRPercent / 100 );
            a.SetArmor( Constants.LOC_RTR, rear );
            a.SetArmor( Constants.LOC_RT, a.GetLocationMax( Constants.LOC_RT ) - rear );
        } else if( ArmorTons <= 0 ) {
            // assume the user wants to zero the armor
            // since we've already zero'd the armor, just return
            return;
        } else {
            // zero the armor array
            for( int i = 0; i < 11; i++ ) {
                Armor[i] = 0;
            }

            // allocate the armor
            AllocateArmor( a );

            // fix the armor
            FixArmor( a );
        }
    }

    private void AllocateArmor( Armor a ) {
        // testing out a new allocation routine
        // round the armor tonnage up to the nearest half ton
        float MidTons = ( (float) Math.floor( ArmorTons * 2.0f ) ) * 0.5f;

        // find the AV we get from this tonnage amount
        int AV = (int) ( Math.floor( MidTons * 16 * a.GetAVMult() ) );

        Armor[Constants.LOC_HD] = (int) Math.floor( AV * 0.06f );
        Armor[Constants.LOC_CT] = (int) Math.floor( AV * 0.15f );
        Armor[Constants.LOC_CTR] = (int) Math.round( Armor[Constants.LOC_CT] * CurOpts.Armor_CTRPercent / 100 );
        switch( CurOpts.Armor_Priority ) {
        case 0:
            // torsos
            Armor[Constants.LOC_LT] = (int) Math.floor( AV * 0.13f );
            Armor[Constants.LOC_LTR] = (int) Math.round( Armor[Constants.LOC_LT] * CurOpts.Armor_STRPercent / 100 );
            Armor[Constants.LOC_RT] = (int) Math.floor( AV * 0.13f );
            Armor[Constants.LOC_RTR] = (int) Math.round( Armor[Constants.LOC_RT] * CurOpts.Armor_STRPercent / 100 );
            Armor[Constants.LOC_RA] = (int) Math.floor( AV * 0.09f );
            Armor[Constants.LOC_LA] = (int) Math.floor( AV * 0.09f );
            Armor[Constants.LOC_LL] = (int) Math.floor( AV * 0.12f );
            Armor[Constants.LOC_RL] = (int) Math.floor( AV * 0.12f );
            break;
        case 1:
            // arms
            Armor[Constants.LOC_LT] = (int) Math.floor( AV * 0.13f );
            Armor[Constants.LOC_LTR] = (int) Math.round( Armor[Constants.LOC_LT] * CurOpts.Armor_STRPercent / 100 );
            Armor[Constants.LOC_RT] = (int) Math.floor( AV * 0.13f );
            Armor[Constants.LOC_RTR] = (int) Math.round( Armor[Constants.LOC_RT] * CurOpts.Armor_STRPercent / 100 );
            Armor[Constants.LOC_RA] = (int) Math.floor( AV * 0.12f );
            Armor[Constants.LOC_LA] = (int) Math.floor( AV * 0.12f );
            Armor[Constants.LOC_LL] = (int) Math.floor( AV * 0.09f );
            Armor[Constants.LOC_RL] = (int) Math.floor( AV * 0.09f );
            break;
        case 2:
            // legs
            Armor[Constants.LOC_LT] = (int) Math.floor( AV * 0.12f );
            Armor[Constants.LOC_LTR] = (int) Math.round( Armor[Constants.LOC_LT] * CurOpts.Armor_STRPercent / 100 );
            Armor[Constants.LOC_RT] = (int) Math.floor( AV * 0.12f );
            Armor[Constants.LOC_RTR] = (int) Math.round( Armor[Constants.LOC_RT] * CurOpts.Armor_STRPercent / 100 );
            Armor[Constants.LOC_RA] = (int) Math.floor( AV * 0.09f );
            Armor[Constants.LOC_LA] = (int) Math.floor( AV * 0.09f );
            Armor[Constants.LOC_LL] = (int) Math.floor( AV * 0.13f );
            Armor[Constants.LOC_RL] = (int) Math.floor( AV * 0.13f );
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

    private int CheckMaximums( Armor a, int AV ) {
        int result = AV;

        // head check
        if( Armor[Constants.LOC_HD] > a.GetLocationMax( Constants.LOC_HD ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = Armor[Constants.LOC_HD] - a.GetLocationMax( Constants.LOC_HD );
            Armor[Constants.LOC_HD] = a.GetLocationMax( Constants.LOC_HD );
            result += mid;
        }

        // CT check
        if( Armor[Constants.LOC_CT] + Armor[Constants.LOC_CTR] > a.GetLocationMax( Constants.LOC_CT ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = Armor[Constants.LOC_CT] + Armor[Constants.LOC_CTR] - a.GetLocationMax( Constants.LOC_CT );
            int rear = Math.round( a.GetLocationMax( Constants.LOC_CT ) * CurOpts.Armor_CTRPercent * 0.01f + 0.49f );
            Armor[Constants.LOC_CTR] = rear;
            Armor[Constants.LOC_CT] = a.GetLocationMax( Constants.LOC_CT ) - rear;
            result += mid;
        }

        // LT check
        if( Armor[Constants.LOC_LT] + Armor[Constants.LOC_LTR] > a.GetLocationMax( Constants.LOC_LT ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = Armor[Constants.LOC_LT] + Armor[Constants.LOC_LTR] - a.GetLocationMax( Constants.LOC_LT );
            int rear = Math.round( a.GetLocationMax( Constants.LOC_LT ) * CurOpts.Armor_STRPercent * 0.01f + 0.49f );
            Armor[Constants.LOC_LTR] = rear;
            Armor[Constants.LOC_LT] = a.GetLocationMax( Constants.LOC_LT ) - rear;
            result += mid;
        }

        // RT check
        if( Armor[Constants.LOC_RT] + Armor[Constants.LOC_RTR] > a.GetLocationMax( Constants.LOC_RT ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = Armor[Constants.LOC_RT] + Armor[Constants.LOC_RTR] - a.GetLocationMax( Constants.LOC_RT );
            int rear = Math.round( a.GetLocationMax( Constants.LOC_RT ) * CurOpts.Armor_STRPercent * 0.01f + 0.49f );
            Armor[Constants.LOC_RTR] = rear;
            Armor[Constants.LOC_RT] = a.GetLocationMax( Constants.LOC_RT ) - rear;
            result += mid;
        }

        // LA check
        if( Armor[Constants.LOC_LA] > a.GetLocationMax( Constants.LOC_LA ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = Armor[Constants.LOC_LA] - a.GetLocationMax( Constants.LOC_LA );
            Armor[Constants.LOC_LA] = a.GetLocationMax( Constants.LOC_LA );
            result += mid;
        }

        // RA check
        if( Armor[Constants.LOC_RA] > a.GetLocationMax( Constants.LOC_RA ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = Armor[Constants.LOC_RA] - a.GetLocationMax( Constants.LOC_RA );
            Armor[Constants.LOC_RA] = a.GetLocationMax( Constants.LOC_RA );
            result += mid;
        }

        // LL check
        if( Armor[Constants.LOC_LL] > a.GetLocationMax( Constants.LOC_LL ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = Armor[Constants.LOC_LL] - a.GetLocationMax( Constants.LOC_LL );
            Armor[Constants.LOC_LL] = a.GetLocationMax( Constants.LOC_LL );
            result += mid;
        }

        // RL check
        if( Armor[Constants.LOC_RL] > a.GetLocationMax( Constants.LOC_RL ) ) {
            // find out how much we are over, correct, and then add the excess to the AV
            int mid = Armor[Constants.LOC_RL] - a.GetLocationMax( Constants.LOC_RL );
            Armor[Constants.LOC_RL] = a.GetLocationMax( Constants.LOC_RL );
            result += mid;
        }

        return result;
    }

    private void AllocateExtra( Armor a, int AV ) {
        // recursive routine for allocating the armor.  Pass in the AV we have
        // to distribute.   We'll do it round-robin style by priority

        // head first
        if( Armor[Constants.LOC_HD] < a.GetLocationMax( Constants.LOC_HD ) ) {
            if( AV > 0 ) {
                if( CurOpts.Armor_Head == CurOpts.HEAD_MAX ) {
                    // head maximum
                    if( AV > 9 - Armor[Constants.LOC_HD] ) {
                        // enough to do the job
                        AV -= ( 9 - Armor[Constants.LOC_HD] );
                        Armor[Constants.LOC_HD] = 9;
                    } else {
                        // armor only goes to the head, then we're done
                        Armor[Constants.LOC_HD] += AV;
                        AV = 0;
                        return;
                    }
                } else {
                    // head equal distribution
                    Armor[Constants.LOC_HD]++;
                    AV--;
                }
            } else {
                // all done
                return;
            }
        }

        // center torso and center rear next
        if( Armor[Constants.LOC_CT] + Armor[Constants.LOC_CTR] < a.GetLocationMax( Constants.LOC_CT ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                if( ( Math.round( Armor[Constants.LOC_CT] * CurOpts.Armor_CTRPercent * 0.01f + 0.49f ) ) > Armor[Constants.LOC_CTR] ) {
                    // allocate to the rear torso
                    Armor[Constants.LOC_CTR]++;
                    AV--;
                } else {
                    // front torso
                    Armor[Constants.LOC_CT]++;
                    AV--;
                }
            } else {
                // all done
                return;
            }
        }

        // Left torso and left rear first
        if( Armor[Constants.LOC_LT] + Armor[Constants.LOC_LTR] < a.GetLocationMax( Constants.LOC_LT ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                if( ( Math.round( Armor[Constants.LOC_LT] * CurOpts.Armor_STRPercent * 0.01f + 0.49f ) ) > Armor[Constants.LOC_LTR] ) {
                    // allocate to the rear torso
                    Armor[Constants.LOC_LTR]++;
                    AV--;
                } else {
                    // front torso
                    Armor[Constants.LOC_LT]++;
                    AV--;
                }
            } else {
                // all done
                return;
            }
        }

        // now the right torso and right rear
        if( Armor[Constants.LOC_RT] + Armor[Constants.LOC_RTR] < a.GetLocationMax( Constants.LOC_RT ) ) {
            // haven't exceeded the maximum yet
            if( AV > 0 ) {
                // some AV to distribute.  See who deserves it on this round
                if( ( Math.round( Armor[Constants.LOC_RT] * CurOpts.Armor_STRPercent * 0.01f + 0.49f ) ) > Armor[Constants.LOC_RTR] ) {
                    // allocate to the rear torso
                    Armor[Constants.LOC_RTR]++;
                    AV--;
                } else {
                    // front torso
                    Armor[Constants.LOC_RT]++;
                    AV--;
                }
            } else {
                // all done
                return;
            }
        }

        // left arm next
        if( Armor[Constants.LOC_LA] < a.GetLocationMax( Constants.LOC_LA ) ) {
            // haven't reached maximum yet
            if( AV > 0 ) {
                Armor[Constants.LOC_LA]++;
                AV--;
            } else {
                // all done
                return;
            }
        }

        // right arm
        if( Armor[Constants.LOC_RA] < a.GetLocationMax( Constants.LOC_RA ) ) {
            // haven't reached maximum yet
            if( AV > 0 ) {
                Armor[Constants.LOC_RA]++;
                AV--;
            } else {
                // all done
                return;
            }
        }

        // left leg
        if( Armor[Constants.LOC_LL] < a.GetLocationMax( Constants.LOC_LL ) ) {
            // haven't reached maximum yet
            if( AV > 0 ) {
                Armor[Constants.LOC_LL]++;
                AV--;
            } else {
                // all done
                return;
            }
        }

        // right leg
        if( Armor[Constants.LOC_RL] < a.GetLocationMax( Constants.LOC_RL ) ) {
            // haven't reached maximum yet
            if( AV > 0 ) {
                Armor[Constants.LOC_RL]++;
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

    private void Symmetrize( Armor a ) {
        // this method attempts to fix any non-symmetrical allocations in a
        // graceful manner.  To simplify, we're taking a point or two from the CT.
        // Since there should never be more than one or two pairs out of alignment
        // the CT shouldn't suffer much.  This will take some watching.

        // LT and RT first
        if( Armor[Constants.LOC_LT] > Armor[Constants.LOC_RT] || Armor[Constants.LOC_LT] < Armor[Constants.LOC_RT] ) {
            // torsos are out of alignment
            if( Armor[Constants.LOC_LT] > Armor[Constants.LOC_RT] ) {
                // left is bigger than right
                Armor[Constants.LOC_CT]--;
                Armor[Constants.LOC_RT]++;
            } else {
                // right is bigger than left
                Armor[Constants.LOC_CT]--;
                Armor[Constants.LOC_LT]++;
            }
        }

        // LA and RA next
        if( Armor[Constants.LOC_LA] > Armor[Constants.LOC_RA] || Armor[Constants.LOC_LA] < Armor[Constants.LOC_RA] ) {
            // arms are out of alignment
            if( Armor[Constants.LOC_LA] > Armor[Constants.LOC_RA] ) {
                // left is bigger than right
                Armor[Constants.LOC_CT]--;
                Armor[Constants.LOC_RA]++;
            } else {
                // right is bigger than left
                Armor[Constants.LOC_CT]--;
                Armor[Constants.LOC_LA]++;
            }
        }

        // LL and RL next
        if( Armor[Constants.LOC_LL] > Armor[Constants.LOC_RL] || Armor[Constants.LOC_LL] < Armor[Constants.LOC_RL] ) {
            // legs are out of alignment
            if( Armor[Constants.LOC_LL] > Armor[Constants.LOC_RL] ) {
                // left is bigger than right
                Armor[Constants.LOC_CT]--;
                Armor[Constants.LOC_RL]++;
            } else {
                // right is bigger than left
                Armor[Constants.LOC_CT]--;
                Armor[Constants.LOC_LL]++;
            }
        }

        // LTR and RTR last
        if( Armor[Constants.LOC_LTR] > Armor[Constants.LOC_RTR] || Armor[Constants.LOC_LTR] < Armor[Constants.LOC_RTR] ) {
            // rear torsos are out of alignment
            if( Armor[Constants.LOC_LTR] > Armor[Constants.LOC_RTR] ) {
                // left is bigger than right
                Armor[Constants.LOC_CT]--;
                Armor[Constants.LOC_RTR]++;
            } else {
                // right is bigger than left
                Armor[Constants.LOC_CT]--;
                Armor[Constants.LOC_LTR]++;
            }
        }
    }

    private void FixArmor( Armor a ) {
        // fixes the armor values to the mech
        a.SetArmor( Constants.LOC_HD, Armor[Constants.LOC_HD] );
        a.SetArmor( Constants.LOC_CT, Armor[Constants.LOC_CT] );
        a.SetArmor( Constants.LOC_LT, Armor[Constants.LOC_LT] );
        a.SetArmor( Constants.LOC_RT, Armor[Constants.LOC_RT] );
        a.SetArmor( Constants.LOC_LA, Armor[Constants.LOC_LA] );
        a.SetArmor( Constants.LOC_RA, Armor[Constants.LOC_RA] );
        a.SetArmor( Constants.LOC_LL, Armor[Constants.LOC_LL] );
        a.SetArmor( Constants.LOC_RL, Armor[Constants.LOC_RL] );
        a.SetArmor( Constants.LOC_CTR, Armor[Constants.LOC_CTR] );
        a.SetArmor( Constants.LOC_LTR, Armor[Constants.LOC_LTR] );
        a.SetArmor( Constants.LOC_RTR, Armor[Constants.LOC_RTR] );
    }

    private int CurrentArmorAV() {
        // totals the current armor AV
        int result = 0;
        for( int i = 0; i < 11; i++ ) {
            result += Armor[i];
        }
        return result;
    }
}

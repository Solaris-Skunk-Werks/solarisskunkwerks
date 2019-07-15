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

public class ActuatorSet {
    // this is a little class to encapsulate all the actuators on a mech.  It's
    // here to keep things clean within the mech.
    private final static AvailableCode AC = new AvailableCode( AvailableCode.TECH_BOTH );
    private boolean LHInstalled = true,
                    RHInstalled = true,
                    LLAInstalled = true,
                    RLAInstalled = true,
                    LockedLeft = false,
                    LockedRight = false;
    public Actuator LeftHip,
                            RightHip,
                            LeftFrontHip,
                            RightFrontHip,
                            LeftLowerLeg,
                            RightLowerLeg,
                            LeftFrontLowerLeg,
                            RightFrontLowerLeg,
                            LeftUpperLeg,
                            RightUpperLeg,
                            LeftFrontUpperLeg,
                            RightFrontUpperLeg,
                            LeftFoot,
                            RightFoot,
                            LeftFrontFoot,
                            RightFrontFoot,
                            LeftShoulder,
                            RightShoulder,
                            LeftUpperArm,
                            RightUpperArm,
                            LeftLowerArm,
                            LeftHand,
                            RightLowerArm,
                            RightHand;
    private ifMechLoadout Owner;

    public ActuatorSet( ifMechLoadout l, Mech m ) {
        AC.SetISCodes( 'C', 'C', 'C', 'C' );
        AC.SetISDates( 0, 0, false, 2300, 0, 0, false, false );
        AC.SetISFactions( "", "", "TH", "" );
        AC.SetCLCodes( 'C', 'X', 'C', 'C' );
        AC.SetCLDates( 0, 0, false, 2300, 0, 0, false, false );
        AC.SetCLFactions( "", "", "TH", "" );
        AC.SetPBMAllowed( true );
        AC.SetPIMAllowed( true );
        AC.SetRulesLevels( AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_TOURNAMENT, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED, AvailableCode.RULES_UNALLOWED );
        Owner = l;
        LeftHip = new Actuator( "Hip Actuator", "Hip", "Hip", true, false, AC, 0.0, m );
        RightHip = new Actuator( "Hip Actuator", "Hip", "Hip", true, false, AC, 0.0, m );
        LeftFrontHip = new Actuator( "Hip Actuator", "Hip", "Hip", true, false, AC, 0.0, m );
        RightFrontHip = new Actuator( "Hip Actuator", "Hip", "Hip", true, false, AC, 0.0, m );
        LeftLowerLeg = new Actuator( "Lower Leg Actuator", "Lower Leg Actuator", "Lower Leg Actuator", true, false, AC, 80.0, m );
        RightLowerLeg = new Actuator( "Lower Leg Actuator", "Lower Leg Actuator", "Lower Leg Actuator", true, false, AC, 80.0, m );
        LeftFrontLowerLeg = new Actuator( "Lower Leg Actuator", "Lower Leg Actuator", "Lower Leg Actuator", true, false, AC, 80.0, m );
        RightFrontLowerLeg = new Actuator( "Lower Leg Actuator", "Lower Leg Actuator", "Lower Leg Actuator", true, false, AC, 80.0, m );
        LeftUpperLeg = new Actuator( "Upper Leg Actuator", "Upper Leg Actuator", "Upper Leg Actuator", true, false, AC, 150.0, m );
        RightUpperLeg = new Actuator( "Upper Leg Actuator", "Upper Leg Actuator", "Upper Leg Actuator", true, false, AC, 150.0, m );
        LeftFrontUpperLeg = new Actuator( "Upper Leg Actuator", "Upper Leg Actuator", "Upper Leg Actuator", true, false, AC, 150.0, m );
        RightFrontUpperLeg = new Actuator( "Upper Leg Actuator", "Upper Leg Actuator", "Upper Leg Actuator", true, false, AC, 150.0, m );
        LeftFoot = new Actuator( "Foot Actuator", "Foot Actuator", "Foot Actuator", true, false, AC, 120.0, m );
        RightFoot = new Actuator( "Foot Actuator", "Foot Actuator", "Foot Actuator", true, false, AC, 120.0, m );
        LeftFrontFoot = new Actuator( "Foot Actuator", "Foot Actuator", "Foot Actuator", true, false, AC, 120.0, m );
        RightFrontFoot = new Actuator( "Foot Actuator", "Foot Actuator", "Foot Actuator", true, false, AC, 120.0, m );
        LeftShoulder = new Actuator( "Shoulder Actuator", "Shoulder", "Shoulder", true, false, AC, 0.0, m );
        RightShoulder = new Actuator( "Shoulder Actuator", "Shoulder", "Shoulder", true, false, AC, 0.0, m );
        LeftUpperArm = new Actuator( "Upper Arm Actuator", "Upper Arm Actuator", "Upper Arm Actuator", true, false, AC, 100.0, m );
        RightUpperArm = new Actuator( "Upper Arm Actuator", "Upper Arm Actuator", "Upper Arm Actuator", true, false, AC, 100.0, m );
        LeftLowerArm = new Actuator( "Lower Arm Actuator", "Lower Arm Actuator", "Lower Arm Actuator", true, true, AC, 50.0, m );
        LeftHand = new Actuator( "Hand Actuator", "Hand Actuator", "Hand Actuator", true, true, AC, 80.0, m );
        RightLowerArm = new Actuator( "Lower Arm Actuator", "Lower Arm Actuator", "Lower Arm Actuator", true, true, AC, 50.0, m );
        RightHand = new Actuator( "Hand Actuator", "Hand Actuator", "Hand Actuator", true, true, AC, 80.0, m );
    }

    public void RemoveAll() {
        Owner.Remove( LeftHip );
        Owner.Remove( RightHip );
        Owner.Remove( LeftLowerLeg );
        Owner.Remove( RightLowerLeg );
        Owner.Remove( LeftUpperLeg );
        Owner.Remove( RightUpperLeg );
        Owner.Remove( LeftFoot );
        Owner.Remove( RightFoot );
        if(  Owner.IsQuad() ) {
            Owner.Remove( LeftFrontHip );
            Owner.Remove( RightFrontHip );
            Owner.Remove( LeftFrontLowerLeg );
            Owner.Remove( RightFrontLowerLeg );
            Owner.Remove( LeftFrontUpperLeg );
            Owner.Remove( RightFrontUpperLeg );
            Owner.Remove( LeftFrontFoot );
            Owner.Remove( RightFrontFoot );
        } else {
            Owner.Remove( LeftShoulder );
            Owner.Remove( RightShoulder );
            Owner.Remove( LeftUpperArm );
            Owner.Remove( RightUpperArm );
            Owner.Remove(RightLowerArm);
            Owner.Remove(LeftLowerArm);
            Owner.Remove(RightHand);
            Owner.Remove(LeftHand);
        }
    }

    public boolean LockedLeft() {
        return LockedLeft;
    }

    public boolean LockedRight() {
        return LockedRight;
    }

    public void SetLockedLeft( boolean b ) {
        LockedLeft = b;
    }

    public void SetLockedRight( boolean b ) {
        LockedRight = b;
    }

    public void RemoveRightHand() {
        // removes the right hand actuator
        if( Owner.IsQuad() ) { return; }
        if( Owner.GetMech().IsOmnimech() ) {
            if( LockedRight ) {
                RHInstalled = true;
                RLAInstalled = true;
                return;
            }
        }
        Owner.Remove( RightHand );
        RHInstalled = false;
    }

    public void RemoveLeftHand() {
        // removes the left hand actuator
        if( Owner.IsQuad() ) { return; }
        if( Owner.GetMech().IsOmnimech() ) {
            if( LockedLeft ) {
                LHInstalled = true;
                LLAInstalled = true;
                return;
            }
        }
        Owner.Remove( LeftHand );
        LHInstalled = false;
    }

    public void RemoveRightLowerArm() {
        // removes the right lower arm actuator
        // ensure the right hand actuator is removed as well
        if( Owner.IsQuad() ) { return; }
        if( Owner.GetMech().IsOmnimech() ) {
            if( LockedRight ) {
                RHInstalled = true;
                RLAInstalled = true;
                return;
            }
        }
        Owner.Remove( RightHand );
        Owner.Remove( RightLowerArm );
        RHInstalled = false;
        RLAInstalled = false;
    }

    public void RemoveLeftLowerArm() {
        // removes the left lower arm actuator
        // ensure the left hand actuator is removed as well
        if( Owner.IsQuad() ) { return; }
        if( Owner.GetMech().IsOmnimech() ) {
            if( LockedLeft ) {
                LHInstalled = true;
                LLAInstalled = true;
                return;
            }
        }
        Owner.Remove( LeftHand );
        Owner.Remove( LeftLowerArm );
        LHInstalled = false;
        LLAInstalled = false;
    }

    public boolean AddRightHand() {
        // add the right hand back in.
        if( Owner.IsQuad() ) { return true; }

        // check and see if the lower arm actuator is there.  if not, add it in.
        if( ! Owner.IsAllocated( RightLowerArm ) ) {
            try {
                Owner.AddToRA( RightLowerArm, 2 );
            } catch ( Exception e ) {
                // Actuators always take precedence.  Let the mech know.
                return false;
            }
        }

        RLAInstalled = true;

        // now add the hand back in
        try {
            Owner.AddToRA( RightHand, 3 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        RHInstalled = true;

        // everything worked fine.
        return true;
    }

    public boolean AddLeftHand() {
        // add the right hand back in.
        if( Owner.IsQuad() ) { return true; }

        // check and see if the lower arm actuator is there.  if not, add it in.
        if( ! Owner.IsAllocated( LeftLowerArm ) ) {
            try {
                Owner.AddToLA( LeftLowerArm, 2 );
            } catch ( Exception e ) {
                // Actuators always take precedence.  Let the mech know.
                return false;
            }
        }

        LLAInstalled = true;

        // now add the hand back in
        try {
            Owner.AddToLA( LeftHand, 3 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        LHInstalled = true;

        // everything worked fine.
        return true;
    }

    public boolean AddRightLowerArm() {
        // add the right lower arm back in.
        if( Owner.IsQuad() ) { return true; }

        try {
            Owner.AddToRA( RightLowerArm, 2 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        RLAInstalled = true;

        // everything worked fine.
        return true;
    }

    public boolean AddLeftLowerArm() {
        // add the right lower arm back in.
        if( Owner.IsQuad() ) { return true; }

        try {
            Owner.AddToLA( LeftLowerArm, 2 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        LLAInstalled = true;

        // everything worked fine.
        return true;
    }

    public boolean PlaceActuators() {
        // we're ignoring the normal methods so we don't have to create eight
        // seperate actuator classes.  This is a manual placement.
        try {
            Owner.AddToLL( LeftHip, 0 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            Owner.AddToLL( LeftUpperLeg, 1 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            Owner.AddToLL( LeftLowerLeg, 2 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            Owner.AddToLL( LeftFoot, 3 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            Owner.AddToRL( RightHip, 0 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            Owner.AddToRL( RightUpperLeg, 1 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            Owner.AddToRL( RightLowerLeg, 2 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            Owner.AddToRL( RightFoot, 3 );
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            if( Owner.IsQuad() ) {
                Owner.AddToLA( LeftFrontHip, 0 );
            } else {
                Owner.AddToLA( LeftShoulder, 0 );
            }
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            if( Owner.IsQuad() ) {
                Owner.AddToLA( LeftFrontUpperLeg, 1 );
            } else {
                Owner.AddToLA( LeftUpperArm, 1 );
            }
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            if( Owner.IsQuad() ) {
                LLAInstalled = true;
                Owner.AddToLA( LeftFrontLowerLeg, 2 );
            } else {
                if( LLAInstalled ) {
                    Owner.AddToLA( LeftLowerArm, 2 );
                }
            }
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            if( Owner.IsQuad() ) {
                LHInstalled = true;
                Owner.AddToLA( LeftFrontFoot, 3 );
            } else {
                if( LHInstalled ) {
                    Owner.AddToLA( LeftHand, 3 );
                }
            }
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            if( Owner.IsQuad() ) {
                Owner.AddToRA( RightFrontHip, 0 );
            } else {
                Owner.AddToRA( RightShoulder, 0 );
            }
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            if( Owner.IsQuad() ) {
                Owner.AddToRA( RightFrontUpperLeg, 1 );
            } else {
                Owner.AddToRA( RightUpperArm, 1 );
            }
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            if( Owner.IsQuad() ) {
                RLAInstalled = true;
                Owner.AddToRA( RightFrontLowerLeg, 2 );
            } else {
                if( RLAInstalled ) {
                    Owner.AddToRA( RightLowerArm, 2 );
                }
            }
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        try {
            if( Owner.IsQuad() ) {
                RHInstalled = true;
                Owner.AddToRA( RightFrontFoot, 3 );
            } else {
                if( RHInstalled ) {
                    Owner.AddToRA( RightHand, 3 );
                }
            }
        } catch ( Exception e ) {
            // Actuators always take precedence.  Let the mech know.
            return false;
        }

        // everything worked out fine.
        return true;
    }

    public double GetCost() {
        // NOTE: Hips and shoulders do not have a cost, so we skip them
        double result = 0.0;
        if( Owner.IsQuad() ) {
            // This is easy since no actuators will ever be removed
            result += LeftHip.GetCost();
            result += RightHip.GetCost();
            result += LeftUpperLeg.GetCost();
            result += LeftLowerLeg.GetCost();
            result += LeftFoot.GetCost();
            result += RightUpperLeg.GetCost();
            result += RightLowerLeg.GetCost();
            result += RightFoot.GetCost();
            result += LeftFrontHip.GetCost();
            result += RightFrontHip.GetCost();
            result += LeftFrontUpperLeg.GetCost();
            result += LeftFrontLowerLeg.GetCost();
            result += LeftFrontFoot.GetCost();
            result += RightFrontUpperLeg.GetCost();
            result += RightFrontLowerLeg.GetCost();
            result += RightFrontFoot.GetCost();
        } else {
            // get the shoulders and leg actuators first
            result += LeftShoulder.GetCost();
            result += RightShoulder.GetCost();
            result += LeftUpperArm.GetCost();
            result += RightUpperArm.GetCost();
            result += LeftHip.GetCost();
            result += RightHip.GetCost();
            result += LeftUpperLeg.GetCost();
            result += RightUpperLeg.GetCost();
            result += LeftLowerLeg.GetCost();
            result += RightLowerLeg.GetCost();
            result += LeftFoot.GetCost();
            result += RightFoot.GetCost();

            // now, for each actuator that may be missing
            if( RLAInstalled ) {
                result += RightLowerArm.GetCost();
            }
            if( LLAInstalled ) {
                result += LeftLowerArm.GetCost();
            }
            if( RHInstalled ) {
                result += RightHand.GetCost();
            }
            if( LHInstalled ) {
                result += LeftHand.GetCost();
            }
        }
        return result;
    }

    public double GetTonnage() {
        // gets the tonnage of the actuators.  Mostly added for armored support
        double result = 0.0;
        if( Owner.IsQuad() ) {
            // This is easy since no actuators will ever be removed
            result += LeftHip.GetTonnage();
            result += RightHip.GetTonnage();
            result += LeftUpperLeg.GetTonnage();
            result += LeftLowerLeg.GetTonnage();
            result += LeftFoot.GetTonnage();
            result += RightUpperLeg.GetTonnage();
            result += RightLowerLeg.GetTonnage();
            result += RightFoot.GetTonnage();
            result += LeftFrontHip.GetTonnage();
            result += RightFrontHip.GetTonnage();
            result += LeftFrontUpperLeg.GetTonnage();
            result += LeftFrontLowerLeg.GetTonnage();
            result += LeftFrontFoot.GetTonnage();
            result += RightFrontUpperLeg.GetTonnage();
            result += RightFrontLowerLeg.GetTonnage();
            result += RightFrontFoot.GetTonnage();
        } else {
            // get the shoulders and leg actuators first
            result += LeftShoulder.GetTonnage();
            result += RightShoulder.GetTonnage();
            result += LeftUpperArm.GetTonnage();
            result += RightUpperArm.GetTonnage();
            result += LeftHip.GetTonnage();
            result += RightHip.GetTonnage();
            result += LeftUpperLeg.GetTonnage();
            result += RightUpperLeg.GetTonnage();
            result += LeftLowerLeg.GetTonnage();
            result += RightLowerLeg.GetTonnage();
            result += LeftFoot.GetTonnage();
            result += RightFoot.GetTonnage();

            // now, for each actuator that may be missing
            if( RLAInstalled ) {
                result += RightLowerArm.GetTonnage();
            }
            if( LLAInstalled ) {
                result += LeftLowerArm.GetTonnage();
            }
            if( RHInstalled ) {
                result += RightHand.GetTonnage();
            }
            if( LHInstalled ) {
                result += LeftHand.GetTonnage();
            }
        }
        return result;
    }

    public double GetOffensiveBV() {
        return 0.0;
    }

    public double GetCurOffensiveBV( boolean UseRear ) {
        return 0.0;
    }

    public double GetDefensiveBV() {
        // gets the BV of the actuators.  Mostly added for armored support
        double result = 0.0;
        if( Owner.IsQuad() ) {
            // This is easy since no actuators will ever be removed
            result += LeftHip.GetDefensiveBV();
            result += RightHip.GetDefensiveBV();
            result += LeftUpperLeg.GetDefensiveBV();
            result += LeftLowerLeg.GetDefensiveBV();
            result += LeftFoot.GetDefensiveBV();
            result += RightUpperLeg.GetDefensiveBV();
            result += RightLowerLeg.GetDefensiveBV();
            result += RightFoot.GetDefensiveBV();
            result += LeftFrontHip.GetDefensiveBV();
            result += RightFrontHip.GetDefensiveBV();
            result += LeftFrontUpperLeg.GetDefensiveBV();
            result += LeftFrontLowerLeg.GetDefensiveBV();
            result += LeftFrontFoot.GetDefensiveBV();
            result += RightFrontUpperLeg.GetDefensiveBV();
            result += RightFrontLowerLeg.GetDefensiveBV();
            result += RightFrontFoot.GetDefensiveBV();
        } else {
            // get the shoulders and leg actuators first
            result += LeftShoulder.GetDefensiveBV();
            result += RightShoulder.GetDefensiveBV();
            result += LeftUpperArm.GetDefensiveBV();
            result += RightUpperArm.GetDefensiveBV();
            result += LeftHip.GetDefensiveBV();
            result += RightHip.GetDefensiveBV();
            result += LeftUpperLeg.GetDefensiveBV();
            result += RightUpperLeg.GetDefensiveBV();
            result += LeftLowerLeg.GetDefensiveBV();
            result += RightLowerLeg.GetDefensiveBV();
            result += LeftFoot.GetDefensiveBV();
            result += RightFoot.GetDefensiveBV();

            // now, for each actuator that may be missing
            if( RLAInstalled ) {
                result += RightLowerArm.GetDefensiveBV();
            }
            if( LLAInstalled ) {
                result += LeftLowerArm.GetDefensiveBV();
            }
            if( RHInstalled ) {
                result += RightHand.GetDefensiveBV();
            }
            if( LHInstalled ) {
                result += LeftHand.GetDefensiveBV();
            }
        }
        return result;
    }

    public boolean LeftHandInstalled() {
        return LHInstalled;
    }

    public boolean RightHandInstalled() {
        return RHInstalled;
    }

    public boolean LeftLowerInstalled() {
        return LLAInstalled;
    }

    public boolean RightLowerInstalled() {
        return RLAInstalled;
    }

    public AvailableCode GetAvailability() {
        // returns an availablecode for all actuators
        AvailableCode retval = AC.Clone();
        if( Owner.IsQuad() ) {
            // This is easy since no actuators will ever be removed
            retval.Combine( LeftHip.GetAvailability() );
            retval.Combine( RightHip.GetAvailability() );
            retval.Combine( LeftUpperLeg.GetAvailability() );
            retval.Combine( LeftLowerLeg.GetAvailability() );
            retval.Combine( LeftFoot.GetAvailability() );
            retval.Combine( RightUpperLeg.GetAvailability() );
            retval.Combine( RightLowerLeg.GetAvailability() );
            retval.Combine( RightFoot.GetAvailability() );
            retval.Combine( LeftFrontHip.GetAvailability() );
            retval.Combine( RightFrontHip.GetAvailability() );
            retval.Combine( LeftFrontUpperLeg.GetAvailability() );
            retval.Combine( LeftFrontLowerLeg.GetAvailability() );
            retval.Combine( LeftFrontFoot.GetAvailability() );
            retval.Combine( RightFrontUpperLeg.GetAvailability() );
            retval.Combine( RightFrontLowerLeg.GetAvailability() );
            retval.Combine( RightFrontFoot.GetAvailability() );
        } else {
            // get the shoulders and leg actuators first
            retval.Combine( LeftShoulder.GetAvailability() );
            retval.Combine( RightShoulder.GetAvailability() );
            retval.Combine( LeftUpperArm.GetAvailability() );
            retval.Combine( RightUpperArm.GetAvailability() );
            retval.Combine( LeftHip.GetAvailability() );
            retval.Combine( RightHip.GetAvailability() );
            retval.Combine( LeftUpperLeg.GetAvailability() );
            retval.Combine( RightUpperLeg.GetAvailability() );
            retval.Combine( LeftLowerLeg.GetAvailability() );
            retval.Combine( RightLowerLeg.GetAvailability() );
            retval.Combine( LeftFoot.GetAvailability() );
            retval.Combine( RightFoot.GetAvailability() );

            // now, for each actuator that may be missing
            if( RLAInstalled ) {
                retval.Combine( RightLowerArm.GetAvailability() );
            }
            if( LLAInstalled ) {
                retval.Combine( LeftLowerArm.GetAvailability() );
            }
            if( RHInstalled ) {
                retval.Combine( RightHand.GetAvailability() );
            }
            if( LHInstalled ) {
                retval.Combine( LeftHand.GetAvailability() );
            }
        }
        return retval;
    }

    public void Transfer( ActuatorSet a ) {
        // this method is provided for OmniMechs and ensures that certain
        // actuators are included in new loadouts.  This should normally only be
        // used from the main loadout
        a.LeftHip = LeftHip;
        a.RightHip = RightHip;
        a.LeftFrontHip = LeftFrontHip;
        a.RightFrontHip = RightFrontHip;
        a.LeftLowerLeg = LeftLowerLeg;
        a.RightLowerLeg = RightLowerLeg;
        a.LeftFrontLowerLeg = LeftFrontLowerLeg;
        a.RightFrontLowerLeg = RightFrontLowerLeg;
        a.LeftUpperLeg = LeftUpperLeg;
        a.RightUpperLeg = RightUpperLeg;
        a.LeftFrontUpperLeg = LeftFrontUpperLeg;
        a.RightFrontUpperLeg = RightFrontUpperLeg;
        a.LeftFoot = LeftFoot;
        a.RightFoot = RightFoot;
        a.LeftFrontFoot = LeftFrontFoot;
        a.RightFrontFoot = RightFrontFoot;
        a.LeftShoulder = LeftShoulder;
        a.RightShoulder = RightShoulder;
        a.LeftUpperArm = LeftUpperArm;
        a.RightUpperArm = RightUpperArm;
        if( LockedLeft ) {
            a.LeftLowerArm = LeftLowerArm;
            a.LeftHand = LeftHand;
        }
        if( LockedRight ) {
            a.RightLowerArm = RightLowerArm;
            a.RightHand = RightHand;
        }
    }

    @Override
    public String toString() {
        return "Actuator Set";
    }
}

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

import ssw.states.*;

public class Cockpit extends abPlaceable {
    private final static ifCockpit StandardCockpit = new stCockpitStandard(),
                                   SmallCockpit = new stCockpitISSmall(),
                                   TorsoCockpit = new stCockpitTorsoMount(),
                                   Primitive = new stCockpitISPrimitive(),
                                   IndustrialCockpit = new stCockpitIndustrial(),
                                   IndusAFCCockpit = new stCockpitIndustrialAFC(),
                                   PrimIndustrial = new stCockpitPrimIndustrial(),
                                   PrimIndusAFC = new stCockpitPrimIndustrialAFC();
    private ifCockpit CurConfig = StandardCockpit;
    private Mech Owner;

    public Cockpit( Mech m ) {
        Owner = m;
    }

    public int GetTechBase() {
        return CurConfig.GetAvailability().GetTechBase();
    }

    public void SetStandardCockpit() {
        CurConfig = StandardCockpit;
    }

    public void SetSmallCockpit() {
        CurConfig = SmallCockpit;
    }

    public void SetPrimitiveCockpit() {
        CurConfig = Primitive;
    }

    public void SetPrimIndustrialCockpit() {
        CurConfig = PrimIndustrial;
    }

    public void SetPrimIndustrialAFCCockpit() {
        CurConfig = PrimIndusAFC;
    }

    public void SetIndustrialCockpit() {
        CurConfig = IndustrialCockpit;
    }

    public void SetIndustrialAFCCockpit() {
        CurConfig = IndusAFCCockpit;
    }

    public void SetTorsoMount() {
        CurConfig = TorsoCockpit;
    }

    public double GetTonnage() {
        if( IsArmored() ) {
            return CurConfig.GetTonnage() + 0.5;
        } else {
            return CurConfig.GetTonnage();
        }
    }

    @Override
    public boolean Place( ifLoadout l ) {
        // Add in the cockpit, life support, and sensors to the head.
        // Cockpit itself first
        LocationIndex loc = CurConfig.GetCockpitLoc();
        try {
            l.AddTo( this, loc.Location, loc.Index );
        } catch ( Exception e ) {
            // Something bad happened and we can't alloc the cockpit.
            return false;
        }

        // Now add in the two Sensor locations
        try {
            loc = CurConfig.GetFirstSensorLoc();
            l.AddTo( CurConfig.GetSensors(), loc.Location, loc.Index );
        } catch ( Exception e ) {
            // Something bad happened and we can't alloc the first sensor.
            return false;
        }

        try {
            loc = CurConfig.GetSecondSensorLoc();
            l.AddTo( CurConfig.GetSecondSensors(), loc.Location, loc.Index );
        } catch ( Exception e ) {
            // Something bad happened and we can't alloc the second sensor.
            return false;
        }

        // first life support unit
        try {
            loc = CurConfig.GetFirstLSLoc();
            l.AddTo( CurConfig.GetLifeSupport(), loc.Location, loc.Index );
        } catch ( Exception e ) {
            // Something bad happened and we can't alloc the second sensor.
            return false;
        }

        // do we have a second life support unit?
        if( CurConfig.HasSecondLSLoc() ) {
            try {
                loc = CurConfig.GetSecondLSLoc();
                l.AddTo( CurConfig.GetSecondLifeSupport(), loc.Location, loc.Index );
            } catch ( Exception e ) {
                // Something bad happened and we can't alloc the second sensor.
                return false;
            }
        }

        // do we have a third sensor location?
        if( CurConfig.HasThirdSensors() ) {
            try {
                loc = CurConfig.GetThirdSensorLoc();
                l.AddTo( CurConfig.GetThirdSensors(), loc.Location, loc.Index );
            } catch( Exception e ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean Place( ifLoadout l, LocationIndex[] locs ) {
        // Add in the cockpit, life support, and sensors.
        // Cockpit itself first
        LocationIndex loc = null;
        try {
            if( locs[0] == null ) {
                l.AddTo( this, loc.Location, loc.Index );
            } else {
                l.AddTo( this, locs[0].Location, locs[0].Index);
            }
        } catch ( Exception e ) {
            return false;
        }

        // Now add in the two Sensor locations
        try {
            loc = CurConfig.GetFirstSensorLoc();
            l.AddTo( CurConfig.GetSensors(), loc.Location, loc.Index );
        } catch ( Exception e ) {
            return false;
        }

        try {
            loc = CurConfig.GetSecondSensorLoc();
            l.AddTo( CurConfig.GetSecondSensors(), loc.Location, loc.Index );
        } catch ( Exception e ) {
            return false;
        }

        // first life support unit
        try {
            if( locs[2] == null ) {
                loc = CurConfig.GetFirstLSLoc();
                l.AddTo( CurConfig.GetLifeSupport(), loc.Location, loc.Index );
            } else {
                l.AddTo( CurConfig.GetLifeSupport(), locs[2].Location, locs[2].Index );
            }
        } catch ( Exception e ) {
            return false;
        }

        // do we have a second life support unit?
        if( CurConfig.HasSecondLSLoc() ) {
            try {
                if( locs[3] == null ) {
                    loc = CurConfig.GetSecondLSLoc();
                    l.AddTo( CurConfig.GetSecondLifeSupport(), loc.Location, loc.Index );
                } else {
                    l.AddTo( CurConfig.GetSecondLifeSupport(), locs[3].Location, locs[3].Index );
                }
            } catch ( Exception e ) {
                return false;
            }
        }

        // do we have a third sensor location?
        if( CurConfig.HasThirdSensors() ) {
            try {
                if( locs[1] == null ) {
                    loc = CurConfig.GetThirdSensorLoc();
                    l.AddTo( CurConfig.GetThirdSensors(), loc.Location, loc.Index );
                } else {
                    l.AddTo( CurConfig.GetThirdSensors(), locs[1].Location, locs[1].Index );
                }
            } catch ( Exception e ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void Remove( ifLoadout l ) {
        // removes the cockpit, life support, and sensors.
        l.Remove( CurConfig.GetLifeSupport() );
        l.Remove( CurConfig.GetSecondLifeSupport() );
        l.Remove( CurConfig.GetSensors() );
        l.Remove( CurConfig.GetSecondSensors() );
        l.Remove( this );
        if( CurConfig.HasThirdSensors() ) {
            l.Remove( CurConfig.GetThirdSensors() );
        }
    }

    public String GetCritName() {
        return CurConfig.GetCritName();
    }

    public String GetMMName( boolean UseRear ) {
        return CurConfig.GetMMName();
    }

    public String GetLookupName() {
        return ((ifState) CurConfig).GetLookupName();
    }

    public String GetReportName() {
        if( Owner.HasFHES() ) {
            return CurConfig.GetReportName() + " w/ Full Head Ejection System";
        } else {
            return CurConfig.GetReportName();
        }
    }

    public int NumCrits() {
        // A cockpit is only ever one crit in size.  Granted, it also encompasses
        // life support and sensors, but the actual cockpit is only one crit.
        return 1;
    }

    public double GetCost() {
        if( IsArmored() ) {
            return 150000.0 + CurConfig.GetCost( Owner.GetTonnage() );
        } else {
            return CurConfig.GetCost( Owner.GetTonnage() );
        }
    }

    public boolean HasFireControl() {
        return CurConfig.HasFireControl();
    }

    public double BVMod() {
        return CurConfig.BVMod();
    }

    public double GetOffensiveBV() {
        return 0.0f;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return 0.0f;
    }

    // return the defensive battle value of the item
    public double GetDefensiveBV() {
        // a cockpit has no battle value unless it is armored
        double result = 0.0;
        if( IsArmored() ) {
            result += 5.0;
        }
        if( CurConfig.GetSensors().IsArmored() ) {
            result += 5.0;
        }
        if( CurConfig.GetSecondSensors().IsArmored() ) {
            result += 5.0;
        }
        if( CurConfig.GetLifeSupport().IsArmored() ) {
            result += 5.0;
        }
        if( CurConfig.HasSecondLSLoc() ) {
            if( CurConfig.GetSecondLifeSupport().IsArmored() ) {
                    result += 5.0;
            }
        }
        if( CurConfig.HasThirdSensors() ) {
            if( CurConfig.GetThirdSensors().IsArmored() ) {
                    result += 5.0;
            }
        }
        return result;
    }

    @Override
    public boolean LocationLocked() {
        return true;
    }

    public int ReportCrits() {
        return CurConfig.ReportCrits();
    }

    public boolean CanUseCommandConsole() {
        return CurConfig.CanUseCommandConsole() &! Owner.HasFHES();
    }

    public ifState[] GetStates() {
        ifState[] retval = { (ifState) StandardCockpit, (ifState) SmallCockpit,
            (ifState) TorsoCockpit, (ifState) IndustrialCockpit, (ifState) IndusAFCCockpit,
            (ifState) Primitive, (ifState) PrimIndustrial, (ifState) PrimIndusAFC };
        return retval;
    }

    public boolean IsTorsoMounted() {
        return CurConfig.IsTorsoMounted();
    }

    // the following 3 methods provided for the torso-mounted cockpit for saving
    public SimplePlaceable GetThirdSensors() {
        return CurConfig.GetThirdSensors();
    }

    public SimplePlaceable GetFirstLS() {
        return CurConfig.GetLifeSupport();
    }

    public SimplePlaceable GetSecondLS() {
        return CurConfig.GetSecondLifeSupport();
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public MechModifier GetMechModifier() {
        return CurConfig.GetMechModifier();
    }

    public AvailableCode GetAvailability() {
        AvailableCode retval = CurConfig.GetAvailability().Clone();
        if( IsArmored() ) {
            retval.Combine( ArmoredAC );
        }
        return retval;
    }

    @Override
    public String toString() {
        return CurConfig.GetCritName();
    }
}

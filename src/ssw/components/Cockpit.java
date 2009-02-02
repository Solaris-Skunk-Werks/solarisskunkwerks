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
    private final static ifCockpit ClanCockpit = new stCockpitCL(),
                                   ISCockpit = new stCockpitIS(),
                                   SmallCockpit = new stCockpitISSmall(),
                                   Primitive = new stCockpitISPrimitive(),
                                   ISIndusCockpit = new stCockpitISIndustrial(),
                                   ISIndusAFCCockpit = new stCockpitISIndustrialAFC(),
                                   CLIndusCockpit = new stCockpitCLIndustrial(),
                                   CLIndusAFCCockpit = new stCockpitCLIndustrialAFC();
    private ifCockpit CurConfig = ISCockpit;
    private Mech Owner;

    public Cockpit( Mech m ) {
        Owner = m;
    }

    public void SetISCockpit() {
        CurConfig = ISCockpit;
    }
    
    public void SetClanCockpit() {
        CurConfig = ClanCockpit;
    }
    
    public void SetSmallCockpit() {
        CurConfig = SmallCockpit;
    }

    public void SetPrimitiveCockpit() {
        CurConfig = Primitive;
    }

    public void SetISIndustrialCockpit() {
        CurConfig = ISIndusCockpit;
    }

    public void SetISIndustrialAFCCockpit() {
        CurConfig = ISIndusAFCCockpit;
    }

    public void SetCLIndustrialCockpit() {
        CurConfig = CLIndusCockpit;
    }

    public void SetCLIndustrialAFCCockpit() {
        CurConfig = CLIndusAFCCockpit;
    }

    public boolean IsClan() {
        return CurConfig.IsClan();
    }

    public float GetTonnage() {
        if( IsArmored() ) {
            return CurConfig.GetTonnage() + 0.5f;
        } else {
            return CurConfig.GetTonnage();
        }
    }

    @Override
    public boolean Place( ifLoadout l ) {
        // Add in the cockpit, life support, and sensors to the head.
        // Cockpit itself first
        try {
            l.AddToHD( this, 2 );
        } catch ( Exception e ) {
            // Something bad happened and we can't alloc the cockpit.
            return false;
        }

        // Now add in the two Sensor locations
        try {
            l.AddToHD( CurConfig.GetSensors(), 1 );
        } catch ( Exception e ) {
            // Something bad happened and we can't alloc the first sensor.
            return false;
        }

        try {
            l.AddToHD( CurConfig.GetSecondSensors(), CurConfig.GetSecondSensorLoc() );
        } catch ( Exception e ) {
            // Something bad happened and we can't alloc the second sensor.
            return false;
        }

        // first life support unit
        try {
            l.AddToHD( CurConfig.GetLifeSupport(), 0 );
        } catch ( Exception e ) {
            // Something bad happened and we can't alloc the second sensor.
            return false;
        }

        // do we have a second life support unit?
        if( CurConfig.HasSecondLSLoc() ) {
            try {
                l.AddToHD( CurConfig.GetSecondLifeSupport(), 5 );
            } catch ( Exception e ) {
                // Something bad happened and we can't alloc the second sensor.
                return false;
            }
        }

        // looks like everything worked out fine.
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
        return CurConfig.GetReportName();
    }

    public int NumCrits() {
        // A cockpit is only ever one crit in size.  Granted, it also encompasses
        // life support and sensors, but the actual cockpit is only one crit.
        return 1;
    }

    public float GetCost() {
        if( IsArmored() ) {
            return 150000.0f + CurConfig.GetCost( Owner.GetTonnage() );
        } else {
            return CurConfig.GetCost( Owner.GetTonnage() );
        }
    }

    public boolean HasFireControl() {
        return CurConfig.HasFireControl();
    }

    public float BVMod() {
        return CurConfig.BVMod();
    }

    public float GetOffensiveBV() {
        return 0.0f;
    }

    public float GetCurOffensiveBV( boolean UseRear ) {
        return 0.0f;
    }

    // return the defensive battle value of the item
    public float GetDefensiveBV() {
        // a cockpit has no battle value unless it is armored
        float result = 0.0f;
        if( IsArmored() ) {
            result += 5.0f;
        }
        if( CurConfig.GetSensors().IsArmored() ) {
            result += 5.0f;
        }
        if( CurConfig.GetSecondSensors().IsArmored() ) {
            result += 5.0f;
        }
        if( CurConfig.GetLifeSupport().IsArmored() ) {
            result += 5.0f;
        }
        if( CurConfig.HasSecondLSLoc() ) {
            if( CurConfig.GetSecondLifeSupport().IsArmored() ) {
                    result += 5.0f;
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

    public ifState[] GetStates() {
        ifState[] retval = { (ifState) ClanCockpit, (ifState) ISCockpit,
            (ifState) SmallCockpit, (ifState) ISIndusCockpit, (ifState) CLIndusCockpit,
            (ifState) ISIndusAFCCockpit, (ifState) CLIndusAFCCockpit };
        return retval;
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
        AvailableCode AC = CurConfig.GetAvailability();
        AvailableCode retval = new AvailableCode( AC.IsClan(), AC.GetTechRating(), AC.GetSLCode(), AC.GetSWCode(), AC.GetCICode(), AC.GetIntroDate(), AC.GetExtinctDate(), AC.GetReIntroDate(), AC.GetIntroFaction(), AC.GetReIntroFaction(), AC.WentExtinct(), AC.WasReIntroduced(), AC.GetRandDStart(), AC.IsPrototype(), AC.GetRandDFaction(), AC.GetRulesLevelBM(), AC.GetRulesLevelIM() );
        if( IsArmored() ) {
            if( AC.IsClan() ) {
                retval.Combine( CLArmoredAC );
            } else {
                retval.Combine( ISArmoredAC );
            }
        }
        return retval;
    }

    @Override
    public String toString() {
        return CurConfig.GetCritName();
    }
}

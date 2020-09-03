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

import common.CommonTools;
import states.*;

public class CVArmor extends abPlaceable {
    // the armor of the mech
    public final static int DEFAULT_FRONT_ARMOR_PERCENT = 25,
                            DEFAULT_TURRET_ARMOR_PERCENT = 25,
                            ARMOR_PRIORITY_FRONT = 0,
                            ARMOR_PRIORITY_TURRET = 1,
                            ARMOR_PRIORITY_EVEN = 2;

    // Declares
    private CombatVehicle Owner;
    private int[] ArmorPoints = { 0, 0, 0, 0, 0, 0, 0, 0 };
    private int[] MaxArmor = { 390, 390, 390, 390, 390, 390, 2, 390 };
    private ifArmor Industrial = new stArmorIN(),
                    Standard = new stArmorMS(),
                    ISFF = new stArmorISFF(),
                    ISVST = new stArmorISVST(),
                    ISLF = new stArmorISLF(),
                    ISHF = new stArmorISHF(),
                    Hardened = new stArmorHA(),
                    ISLR = new stArmorISLR(),
                    ISRE = new stArmorISRE(),
                    PBM = new stArmorPBM(),
                    Commercial = new stArmorCM(),
                    CLFF = new stArmorCLFF(),
                    CLFL = new stArmorCLFL(),
                    CLLR = new stArmorCLLR(),
                    CLRE = new stArmorCLRE(),
                    ISAB = new stArmorISAB(),
                    HeatDiss = new stArmorHD(),
                    ISIR = new stArmorISIR(),
                    ISBR = new stArmorISBR(),
                    Patchwork = new stArmorPatchwork();
    private ifArmor Config = Standard,
                    FrontConfig = Standard,
                    LeftConfig = Standard,
                    RightConfig = Standard,
                    RearConfig = Standard,
                    Turret1Config = Standard,
                    RotorConfig = Standard,
                    Turret2Config = Standard;

    public CVArmor( CombatVehicle c ) {
        Owner = c;

        SetMaxArmor(4);
    }

    public final void SetMaxArmor(int locations) {
        // this sets the maximum array when tonnage changes.
        int Percentage = ((!Owner.IsVTOL()) ? GetMaxArmor() : GetMaxArmor()-2) / locations;

        MaxArmor[LocationIndex.CV_LOC_BODY] = 0;
        MaxArmor[LocationIndex.CV_LOC_FRONT] = GetMaxArmor();
        MaxArmor[LocationIndex.CV_LOC_LEFT] = GetMaxArmor();
        MaxArmor[LocationIndex.CV_LOC_RIGHT] = GetMaxArmor();
        MaxArmor[LocationIndex.CV_LOC_REAR] = GetMaxArmor();
        
        if ( Owner.isHasTurret1() )
            MaxArmor[LocationIndex.CV_LOC_TURRET1] = GetMaxArmor();
        
        if ( Owner.isHasTurret2() )
            MaxArmor[LocationIndex.CV_LOC_TURRET2] = GetMaxArmor();
        
        if ( Owner.IsVTOL() )
            MaxArmor[LocationIndex.CV_LOC_ROTOR] = 2;
    }

    public ifState GetCurrentState() {
        return (ifState) Config;
    }

    public void ResetPatchworkConfigs() {
        if( CommonTools.IsAllowed( Standard.GetAvailability(), Owner ) ) {
            Config = Standard;
            FrontConfig = Standard;
            LeftConfig = Standard;
            RightConfig = Standard;
            RearConfig = Standard;
            Turret1Config = Standard;
            RotorConfig = Standard;
            Turret2Config = Standard;
        } else {
            Config = Industrial;
            FrontConfig = Industrial;
            LeftConfig = Industrial;
            RightConfig = Industrial;
            RearConfig = Industrial;
            Turret1Config = Industrial;
            RotorConfig = Industrial;
            Turret2Config = Industrial;
        }
    }

    public void SetPatchwork() {
        Config = Patchwork;
    }

    //<editor-fold defaultstate="collapsed" desc="Armor Type Setters">
    public void SetIndustrial() {
        // set the armor to Inner Sphere Industrial
        Config = Industrial;
    }

    public void SetIndustrial( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = Industrial;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = Industrial;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = Industrial;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = Industrial;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = Industrial;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = Industrial;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = Industrial;
                return;
        }
    }

    public void SetStandard() {
        // set the armor to Inner Sphere Military Standard
        Config = Standard;
    }

    public void SetStandard( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = Standard;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = Standard;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = Standard;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = Standard;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = Standard;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = Standard;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = Standard;
                return;
        }
    }

    public void SetISFF() {
        // set the armor to Inner Sphere Ferro-Fibrous
        Config = ISFF;
    }

    public void SetISFF( int Loc ) throws Exception {
        CheckPatchworkSpace( ISFF, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = ISFF;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = ISFF;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = ISFF;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = ISFF;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = ISFF;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = ISFF;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = ISFF;
                return;
        }
    }

    public void SetISVST() {
        // set the armor to Vehicle Stealth
        Config = ISVST;
    }

    public void SetISVST( int Loc ) throws Exception {
        CheckPatchworkSpace( ISVST, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = ISVST;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = ISVST;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = ISVST;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = ISVST;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = ISVST;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = ISVST;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = ISVST;
                return;
        }
    }

    public void SetISLF() {
        // set the armor to Inner Sphere Light Ferro-Fibrous
        Config = ISLF;
    }

    public void SetISLF( int Loc ) throws Exception {
        CheckPatchworkSpace( ISLF, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = ISLF;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = ISLF;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = ISLF;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = ISLF;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = ISLF;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = ISLF;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = ISLF;
                return;
        }
    }

    public void SetISHF() {
        // set the armor to Inner Sphere Heavy Ferro-Fibrous
        Config = ISHF;
    }

    public void SetISHF( int Loc ) throws Exception {
        CheckPatchworkSpace( ISHF, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = ISHF;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = ISHF;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = ISHF;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = ISHF;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = ISHF;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = ISHF;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = ISHF;
                return;
        }
    }

    public void SetHardened() {
        // set the armor to Inner Sphere Hardened
        Config = Hardened;
    }

    public void SetHardened( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = Hardened;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = Hardened;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = Hardened;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = Hardened;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = Hardened;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = Hardened;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = Hardened;
                return;
        }
    }

    public void SetISLR() {
        // set the armor to Inner Sphere Laser-Reflective
        Config = ISLR;
    }

    public void SetISLR( int Loc ) throws Exception {
        CheckPatchworkSpace( ISLR, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = ISLR;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = ISLR;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = ISLR;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = ISLR;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = ISLR;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = ISLR;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = ISLR;
                return;
        }
    }

    public void SetISRE() {
        // set the armor to Inner Sphere Reactive
        Config = ISRE;
    }

    public void SetISRE( int Loc ) throws Exception {
        CheckPatchworkSpace( ISRE, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = ISRE;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = ISRE;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = ISRE;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = ISRE;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = ISRE;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = ISRE;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = ISRE;
                return;
        }
    }

    public void SetCommercial() {
        Config = Commercial;
    }

    public void SetCommercial( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = Commercial;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = Commercial;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = Commercial;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = Commercial;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = Commercial;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = Commercial;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = Commercial;
                return;
        }
    }

    public void SetPrimitive() {
        Config = PBM;
    }

    public void SetPrimitive( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = PBM;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = PBM;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = PBM;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = PBM;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = PBM;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = PBM;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = PBM;
                return;
        }
    }

    public void SetCLFF() {
        // set the armor to Clan Ferro-Fibrous
        Config = CLFF;
    }

    public void SetCLFF( int Loc ) throws Exception {
        CheckPatchworkSpace( CLFF, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = CLFF;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = CLFF;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = CLFF;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = CLFF;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = CLFF;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = CLFF;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = CLFF;
                return;
        }
    }

    public void SetCLFL() {
        // set the armor to Clan Ferro-Lamellor
        Config = CLFL;
    }

    public void SetCLFL( int Loc ) throws Exception {
        CheckPatchworkSpace( CLFL, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = CLFL;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = CLFL;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = CLFL;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = CLFL;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = CLFL;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = CLFL;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = CLFL;
                return;
        }
    }

    public void SetCLLR() {
        // set the armor to Inner Sphere Laser-Reflective
        Config = CLLR;
    }

    public void SetCLLR( int Loc ) throws Exception {
        CheckPatchworkSpace( CLLR, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = CLLR;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = CLLR;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = CLLR;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = CLLR;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = CLLR;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = CLLR;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = CLLR;
                return;
        }
    }

    public void SetCLRE() {
        // set the armor to Inner Sphere Reactive
        Config = CLRE;
    }

    public void SetCLRE( int Loc ) throws Exception {
        CheckPatchworkSpace( CLRE, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = CLRE;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = CLRE;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = CLRE;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = CLRE;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = CLRE;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = CLRE;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = CLRE;
                return;
        }
    }
    
    
    public void SetISAB() {
        Config = ISAB;
    }

    public void SetISAB( int Loc ) throws Exception {
        CheckPatchworkSpace( ISAB, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = ISAB;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = ISAB;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = ISAB;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = ISAB;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = ISAB;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = ISAB;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = ISAB;
                return;
        }
    }
    
    
    
    public void SetISBR() {
        Config = ISBR;
    }

    public void SetISBR( int Loc ) throws Exception {
        CheckPatchworkSpace( ISBR, Loc );
        switch( Loc ) {
            case LocationIndex.CV_LOC_FRONT:
                FrontConfig = ISBR;
                return;
            case LocationIndex.CV_LOC_LEFT:
                LeftConfig = ISBR;
                return;
            case LocationIndex.CV_LOC_RIGHT:
                RightConfig = ISBR;
                return;
            case LocationIndex.CV_LOC_REAR:
                RearConfig = ISBR;
                return;
            case LocationIndex.CV_LOC_TURRET1:
                Turret1Config = ISBR;
                return;
            case LocationIndex.CV_LOC_ROTOR:
                RotorConfig = ISBR;
                return;
            case LocationIndex.CV_LOC_TURRET2:
                Turret2Config = ISBR;
                return;
        }
    }
    //</editor-fold>
    
    private void CheckPatchworkSpace( ifArmor test, int loc ) throws Exception {
        if( test.PatchworkSpaces() > Owner.GetLoadout().FreeItems() ) {
            throw new Exception( "Cannot change " + LocationIndex.CVLocs[loc] + " armor to " + test.CritName() + "\nbecause there is not enough space." );
        }
    }

    public void Recalculate() {
        // now that we've set the maximums, make sure we're not exceeding them
        if( GetArmorValue() > GetMaxArmor() ) { RebalanceArmor(); }
        Owner.SetChanged( true );
    }

    public void RebalanceArmor() {
        // never a need to decrement the rotor.
        DecrementArmor( LocationIndex.CV_LOC_FRONT );
        DecrementArmor( LocationIndex.CV_LOC_LEFT );
        DecrementArmor( LocationIndex.CV_LOC_RIGHT );
        DecrementArmor( LocationIndex.CV_LOC_REAR );
        DecrementArmor( LocationIndex.CV_LOC_TURRET1 );
        DecrementArmor( LocationIndex.CV_LOC_TURRET2 );
        if( GetArmorValue() > GetMaxArmor() ) { RebalanceArmor(); }
    }

    public void IncrementArmor( int Loc ) {
        // Check the location and see what we have to do
        IncrementSingle( Loc );
    }

    private void IncrementSingle( int Loc ) {
        // Make sure we're not exceeding the max
        if( GetArmorValue() >= GetMaxArmor() ) { return; }
        ArmorPoints[Loc]++;
        Owner.SetChanged( true );
    }

    public void DecrementArmor( int Loc ) {
        // Make sure we're not going below 0
        if( ArmorPoints[Loc] <= 0 ) {
            ArmorPoints[Loc] = 0;
        } else {
            ArmorPoints[Loc]--;
        }
        Owner.SetChanged( true );
    }

    public void SetArmor( int Loc, int av ) {
        // Check the location and see what we have to do
        SetSingle( Loc, av );
        Owner.SetChanged( true );
    }

    private void SetSingle( int Loc, int av ) {
        // make sure we're within bounds
        if( (GetArmorValue()-GetLocationArmor(Loc)) + av > GetMaxArmor() ) {
            av = Math.abs(GetMaxArmor() - GetArmorValue() - GetLocationArmor(Loc));
            ArmorPoints[Loc] = av;
        } else if( av < 0 ) {
            ArmorPoints[Loc] = 0;
        } else {
            ArmorPoints[Loc] = av;
        }
        Owner.SetChanged( true );
    }

    public int GetLocationArmor( int Loc ) {
        return ArmorPoints[Loc];
    }

    public int GetLocationMax( int Loc ) {
        return MaxArmor[Loc];
    }

    public int GetMaxArmor() {
        // returns the maximum amount of armor allowed.\
        return (int) ( Owner.GetTonnage() * 3.5 ) + 40;
    }
    
    public int GetArmorPoints(double Tonnage) {
        return (int) ( Math.floor( Tonnage * 16 * GetAVMult() ) );
    }

    public int GetArmorValue() {
        int result = 0;
        result += ArmorPoints[LocationIndex.CV_LOC_FRONT];
        result += ArmorPoints[LocationIndex.CV_LOC_LEFT];
        result += ArmorPoints[LocationIndex.CV_LOC_RIGHT];
        result += ArmorPoints[LocationIndex.CV_LOC_REAR];
        result += ArmorPoints[LocationIndex.CV_LOC_TURRET1];
        result += ArmorPoints[LocationIndex.CV_LOC_ROTOR];
        result += ArmorPoints[LocationIndex.CV_LOC_TURRET2];
        return result;
    }
    
    public void ClearArmorValues() {
        for (int i = 0; i < ArmorPoints.length; i++) {
            ArmorPoints[i] = 0;
        }
    }

    public int GetModularArmorValue() {
        int result = 0;
        return result;
    }

    //<editor-fold desc="Return Types">
    public boolean IsCommercial() {
        if ( Config == Commercial )
            return true;
        else
            return false;
    }

    public boolean IsFerroLamellor() {
        if ( Config == CLFL )
            return true;
        else
            return false;
    }

    public boolean IsHardened() {
        if ( Config == Hardened )
            return true;
        else
            return false;
    }

    public boolean IsReflective() {
        if ( Config == ISLR || Config == CLLR )
            return true;
        else
            return false;
    }

    public boolean IsReactive() {
        if ( Config == ISRE || Config == CLRE )
            return true;
        else
            return false;
    }

    public boolean IsPatchwork() {
        if( Config == Patchwork ) { return true; }
        return false;
    }
    
    public boolean IsStealth() {
        return Config.IsStealth();
    }

//</editor-fold>
    
    public boolean RequiresExtraRules() {
        if ( IsHardened() || IsReactive() || IsReflective() || IsStealth() ) {
            return true;
        } else {
            return false;
        }
    }

    public int GetTechBase() {
        return Config.GetAvailability().GetTechBase();
    }

    @Override
    public boolean Place( ifCVLoadout l ) {
        return true;
    }

    @Override
    public boolean Place( ifCVLoadout l, LocationIndex[] a ) {
        return true;
    }

    @Override
    public boolean CanArmor() {
        // armor is always roll again, so no armoring
        return false;
    }

    public String ActualName() {
        return Config.ActualName();
    }

    public String CritName() {
        return Config.CritName();
    }

    public String LookupName() {
        return Config.LookupName();
    }

    public String ChatName() {
        return Config.ChatName();
    }

    public String MegaMekName( boolean UseRear ) {
        return Config.MegaMekName( UseRear );
    }

    public String BookReference() {
        return Config.BookReference();
    }

    @Override
    public int NumCrits() {
        return Config.NumCrits();
    }

    @Override
    public int NumCVSpaces() {
        if( IsPatchwork() ) {
            return PatchworkSpaces();
        }
        return Config.NumCVSpaces();
    }

    public int PatchworkSpaces() {
        int retval = FrontConfig.PatchworkSpaces();
        retval += LeftConfig.PatchworkSpaces();
        retval += RightConfig.PatchworkSpaces();
        retval += RearConfig.PatchworkSpaces();
        retval += Turret1Config.PatchworkSpaces();
        retval += RotorConfig.PatchworkSpaces();
        retval += Turret2Config.PatchworkSpaces();
        return retval;
    }

    @Override
    public double GetTonnage() {
        // this has to return the nearest half-ton.
        if( Owner.UsingFractionalAccounting() ) {
            return CommonTools.RoundFractionalTons( GetArmorValue() * Config.GetPointsPerTon() );
//            return Math.ceil( GetArmorValue() * Config.GetPointsPerTon() * 1000 ) * 0.001;
        }
        double result = GetArmorValue() / ( 8 * Config.GetAVMult() );
        int mid = (int) Math.floor( result + 0.9999 );
        result = mid * 0.5;
        return result;
    }

    public double GetWastedTonnage() {
        // returns the amount of tonnage wasted due to unspent armor points
        if( Owner.UsingFractionalAccounting() ) { return 0.0; }
        double result = GetTonnage() - GetArmorValue() / ( 16 * Config.GetAVMult() );
        if( result < 0.0 ) { result = 0.0; }
        return (double) Math.floor( result * 100 ) / 100;
    }

    public int GetWastedAV() {
        // returns the amount of armor points left in the current half-ton lot
        // get the amount of wasted tonnage
        if( Owner.UsingFractionalAccounting() ) { return 0; }
        double Waste = 0.5 - ( GetTonnage() - GetArmorValue() / ( 16 * Config.GetAVMult() ) );
        int result = (int) Math.floor( ( 8 * Config.GetAVMult() ) - ( Waste * 16 * Config.GetAVMult() ) );
        if( result < 0 ) { result = 0; }
        return result;
    }

    public double GetCoverage() {
        // returns the amount of max armor coverage on this mech as a percentage
        double result = (double) GetArmorValue() / (double) GetMaxArmor();
        return (double) Math.floor( result * 10000 ) / 100;
    }

    public double GetMaxTonnage() {
        // returns the maximum armor tonnage supported by this vehicle.
        if( Owner.UsingFractionalAccounting() ) {
            return CommonTools.RoundFractionalTons( GetMaxArmor() * Config.GetPointsPerTon() );
//            return Math.ceil( GetMaxArmor() * Config.GetPointsPerTon() * 1000 ) * 0.001;
        }
        double result = GetMaxArmor() / ( 8 * Config.GetAVMult() );
        int mid = (int) Math.round( result + 0.4999 );
        result = mid * 0.5;
        return result;
    }
    
    public void Maximize() {
        int AV = GetMaxArmor();
        
        // remove all existing amounts so we can reset
        ClearArmorValues();
        
        if ( Owner.IsVTOL() ) {
            SetArmor(LocationIndex.CV_LOC_ROTOR, 2);
            AV -= 2;
        }
        int split = AV / Owner.getLocationCount();
        
        if ( Owner.isHasTurret1() ) SetArmor( LocationIndex.CV_LOC_TURRET1, split);
        if ( Owner.isHasTurret2() ) SetArmor( LocationIndex.CV_LOC_TURRET2, split);
        
        SetArmor( LocationIndex.CV_LOC_LEFT, split);
        SetArmor( LocationIndex.CV_LOC_RIGHT, split);
        
        SetArmor( LocationIndex.CV_LOC_FRONT, (int)Math.ceil((split * 2) * .6) );
        SetArmor( LocationIndex.CV_LOC_REAR, (split * 2)-GetLocationArmor(LocationIndex.CV_LOC_FRONT));
        
        if ( GetArmorValue() < GetMaxArmor() ) {
            int val = GetMaxArmor()-GetArmorValue();
            for (int i = 0; i < val; i++) {
                IncrementArmor(LocationIndex.CV_LOC_FRONT);
            }
        }
    }

    public double GetAVMult() {
        // convenience method for armor placement
        return Config.GetAVMult();
    }

    public double GetPointsPerTon() {
        return Config.GetPointsPerTon();
    }

    public double GetBVTypeMult() {
        return Config.GetBVTypeMult();
    }

    public ifState[] GetStates() {
        ifState[] retval = { (ifState) Industrial, (ifState) Commercial, (ifState) PBM, (ifState) Standard, (ifState) ISFF, (ifState) CLFF,
            (ifState) ISLF, (ifState) ISHF, (ifState) ISVST, (ifState) Hardened, (ifState) ISLR, (ifState) CLLR, (ifState) ISRE, (ifState) CLRE,
             (ifState) CLFL, (ifState) ISAB, (ifState) ISBR, (ifState) Patchwork };
        return retval;
    }

    @Override
    public double GetCost() {
        if( Owner.GetYear() < 2450 ) {
            return GetTonnage() * Config.GetCostMult() * 2.0;
        } else {
            return GetTonnage() * Config.GetCostMult();
        }
    }

    public double GetOffensiveBV() {
        return 0.0f;
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES ) {
        return GetOffensiveBV();
    }

    public double GetCurOffensiveBV( boolean UseRear, boolean UseTC, boolean UseAES, boolean UseRobotic ) {
        // BV will not change for this item, so just return the normal value
        return GetOffensiveBV();
    }

    public double GetDefensiveBV() {
        return ( GetArmorValue() + GetModularArmorValue() ) * 2.5;
    }

    public int GetBAR() {
        return Config.GetBAR();
    }

    @Override
    public void ResetPlaced() { return; }

    @Override
    public boolean Contiguous() {
        return false;
    }

    @Override
    public boolean LocationLocked() {
        return Config.LocationLocked();
    }

    @Override
    public void SetLocked( boolean l ) {
        Config.SetLocked( l );
    }

    @Override
    public boolean CoreComponent() {
        return true;
    }

    @Override
    public AvailableCode GetAvailability() {
        return Config.GetAvailability();
    }

    @Override
    public boolean IsCritable() {
        return false;
    }

    @Override
    public MechModifier GetMechModifier() {
        return Config.GetMechModifier();
    }

    @Override
    public String toString() {
        return Config.CritName();
    }

    public ifArmor GetFrontArmorType()
    {
        return FrontConfig;
    }

    public ifArmor GetRearArmorType()
    {
        return RearConfig;
    }

    public ifArmor GetLeftArmorType()
    {
        return LeftConfig;
    }

    public ifArmor GetRightArmorType()
    {
        return RightConfig;
    }

    public ifArmor GetTurret1ArmorType()
    {
        return Turret1Config;
    }
    public ifArmor GetTurret2ArmorType()
    {
        return Turret2Config;
    }
    public ifArmor GetRotorArmorType()
    {
        return RotorConfig;
    }

    public CombatVehicle GetOwner() {
        return Owner;
    }
}
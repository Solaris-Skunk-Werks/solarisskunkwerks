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

public class MechArmor  extends abPlaceable {
    // the armor of the mech
    public final static int DEFAULT_CTR_ARMOR_PERCENT = 25,
                            DEFAULT_STR_ARMOR_PERCENT = 25,
                            ARMOR_PRIORITY_TORSO = 0,
                            ARMOR_PRIORITY_ARMS = 1,
                            ARMOR_PRIORITY_LEGS = 2;

    // Declares
    private Mech Owner;
    private int Placed = 0;
    private int[] ArmorPoints = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private int[] MaxArmor = { 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 0 };
    private ifArmor Industrial = new stArmorIN(),
                    Standard = new stArmorMS(),
                    ISFF = new stArmorISFF(),
                    ISST = new stArmorISST(),
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
                    HDConfig = Standard,
                    CTConfig = Standard,
                    LTConfig = Standard,
                    RTConfig = Standard,
                    LAConfig = Standard,
                    RAConfig = Standard,
                    LLConfig = Standard,
                    RLConfig = Standard;

    public MechArmor( Mech m ) {
        Owner = m;
        SetMaxArmor();
    }

    public ifState GetCurrentState() {
        return (ifState) Config;
    }

    public AvailableCode GetStandardAC() {
        return Standard.GetAvailability();
    }

    public void ResetPatchworkConfigs() {
        if( CommonTools.IsAllowed( Standard.GetAvailability(), Owner ) ) {
            Config = Standard;
            HDConfig = Standard;
            CTConfig = Standard;
            LTConfig = Standard;
            RTConfig = Standard;
            LAConfig = Standard;
            RAConfig = Standard;
            LLConfig = Standard;
            RLConfig = Standard;
        } else {
            Config = Industrial;
            HDConfig = Industrial;
            CTConfig = Industrial;
            LTConfig = Industrial;
            RTConfig = Industrial;
            LAConfig = Industrial;
            RAConfig = Industrial;
            LLConfig = Industrial;
            RLConfig = Industrial;
        }
    }

    public void SetPatchwork() {
        Config = Patchwork;
    }

    public void SetIndustrial() {
        // set the armor to Inner Sphere Industrial
        Config = Industrial;
    }

    public void SetIndustrial( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = Industrial;
                HDConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_HD, HDConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = Industrial;
                CTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_CT, CTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = Industrial;
                LTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LT, LTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = Industrial;
                RTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RT, RTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = Industrial;
                LAConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LA, LAConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = Industrial;
                RAConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RA, RAConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = Industrial;
                LLConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LL, LLConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = Industrial;
                RLConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RL, RLConfig.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetStandard() {
        // set the armor to Inner Sphere Military Standard
        Config = Standard;
    }

    public void SetStandard( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = Standard;
                HDConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_HD, HDConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = Standard;
                CTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_CT, CTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = Standard;
                LTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LT, LTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = Standard;
                RTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RT, RTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = Standard;
                LAConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LA, LAConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = Standard;
                RAConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RA, RAConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = Standard;
                LLConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LL, LLConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = Standard;
                RLConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RL, RLConfig.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISFF() {
        // set the armor to Inner Sphere Ferro-Fibrous
        Config = ISFF;
    }

    public void SetISFF( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetISFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetISFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetISFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetISFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetISFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetISFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetISFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetISFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISFF.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISFF( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = ISFF;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Industrial;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Head.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = ISFF;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Industrial;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Center Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = ISFF;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Industrial;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Left Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = ISFF;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Industrial;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Right Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = ISFF;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Industrial;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Left Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = ISFF;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Industrial;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Right Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = ISFF;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Industrial;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Left Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = ISFF;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Industrial;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Right Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
        }
    }

    public void SetISST() {
        // set the armor to Inner Sphere Stealth
        Config = ISST;
    }

    public void SetISST( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetISST( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISST.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetISST( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISST.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetISST( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISST.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetISST( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISST.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetISST( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISST.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetISST( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISST.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetISST( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISST.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetISST( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISST.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISST( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = ISST;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Industrial;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Stealth Armor as patchwork in the Head.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = ISST;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Industrial;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Stealth Armor as patchwork in the Center Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = ISST;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Industrial;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Stealth Armor as patchwork in the Left Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = ISST;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Industrial;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Stealth Armor as patchwork in the Right Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = ISST;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Industrial;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Stealth Armor as patchwork in the Left Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = ISST;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Industrial;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Stealth Armor as patchwork in the Right Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = ISST;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Industrial;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Stealth Armor as patchwork in the Left Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = ISST;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Industrial;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Stealth Armor as patchwork in the Right Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
        }
    }

    public void SetISLF() {
        // set the armor to Inner Sphere Light Ferro-Fibrous
        Config = ISLF;
    }

    public void SetISLF( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetISLF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetISLF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetISLF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetISLF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetISLF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetISLF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetISLF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetISLF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLF.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISLF( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = ISLF;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Industrial;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Light Ferro-Fibrous Armor as patchwork in the Head.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = ISLF;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Industrial;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Light Ferro-Fibrous Armor as patchwork in the Center Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = ISLF;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Industrial;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Light Ferro-Fibrous Armor as patchwork in the Left Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = ISLF;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Industrial;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Light Ferro-Fibrous Armor as patchwork in the Right Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = ISLF;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Industrial;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Light Ferro-Fibrous Armor as patchwork in the Left Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = ISLF;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Industrial;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Light Ferro-Fibrous Armor as patchwork in the Right Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = ISLF;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Industrial;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Light Ferro-Fibrous Armor as patchwork in the Left Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = ISLF;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Industrial;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Light Ferro-Fibrous Armor as patchwork in the Right Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
        }
    }

    public void SetISHF() {
        // set the armor to Inner Sphere Heavy Ferro-Fibrous
        Config = ISHF;
    }

    public void SetISHF( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetISHF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISHF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetISHF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISHF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetISHF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISHF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetISHF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISHF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetISHF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISHF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetISHF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISHF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetISHF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISHF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetISHF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISHF.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISHF( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = ISHF;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Industrial;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heavy Ferro-Fibrous Armor as patchwork in the Head.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = ISHF;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Industrial;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heavy Ferro-Fibrous Armor as patchwork in the Center Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = ISHF;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Industrial;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heavy Ferro-Fibrous Armor as patchwork in the Left Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = ISHF;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Industrial;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heavy Ferro-Fibrous Armor as patchwork in the Right Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = ISHF;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Industrial;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heavy Ferro-Fibrous Armor as patchwork in the Left Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = ISHF;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Industrial;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heavy Ferro-Fibrous Armor as patchwork in the Right Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = ISHF;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Industrial;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heavy Ferro-Fibrous Armor as patchwork in the Left Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = ISHF;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Industrial;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heavy Ferro-Fibrous Armor as patchwork in the Right Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
        }
    }

    public void SetHardened() {
        // set the armor to Inner Sphere Hardened
        Config = Hardened;
    }

    public void SetHardened( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = Hardened;
                HDConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_HD, HDConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = Hardened;
                CTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_CT, CTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = Hardened;
                LTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LT, LTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = Hardened;
                RTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RT, RTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = Hardened;
                LAConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LA, LAConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = Hardened;
                RAConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RA, RAConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = Hardened;
                LLConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LL, LLConfig.PatchworkCrits() ) } );;
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = Hardened;
                RLConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RL, RLConfig.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISLR() {
        // set the armor to Inner Sphere Laser-Reflective
        Config = ISLR;
    }

    public void SetISLR( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetISLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetISLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetISLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetISLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetISLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetISLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetISLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetISLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISLR.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISLR( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = ISLR;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Industrial;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Head.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = ISLR;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Industrial;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Center Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = ISLR;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Industrial;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Left Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = ISLR;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Industrial;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Right Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = ISLR;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Industrial;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Left Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = ISLR;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Industrial;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Right Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = ISLR;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Industrial;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Left Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = ISLR;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Industrial;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Right Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
        }
    }

    public void SetISRE() {
        // set the armor to Inner Sphere Reactive
        Config = ISRE;
    }

    public void SetISRE( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetISRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetISRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetISRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetISRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetISRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetISRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetISRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetISRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISRE.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISRE( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = ISRE;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Industrial;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Head.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = ISRE;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Industrial;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Center Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = ISRE;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Industrial;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Left Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = ISRE;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Industrial;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Right Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = ISRE;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Industrial;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Left Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = ISRE;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Industrial;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Right Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = ISRE;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Industrial;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Left Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = ISRE;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Industrial;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Right Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
        }
    }

    public void SetCommercial() {
        Config = Commercial;
    }

    public void SetCommercial( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = Commercial;
                HDConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_HD, HDConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = Commercial;
                CTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_CT, CTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = Commercial;
                LTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LT, LTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = Commercial;
                RTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RT, RTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = Commercial;
                LAConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LA, LAConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = Commercial;
                RAConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RA, RAConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = Commercial;
                LLConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LL, LLConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = Commercial;
                RLConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RL, RLConfig.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetPrimitive() {
        Config = PBM;
    }

    public void SetPrimitive( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = PBM;
                HDConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_HD, HDConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = PBM;
                CTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_CT, CTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = PBM;
                LTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LT, LTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = PBM;
                RTConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RT, RTConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = PBM;
                LAConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LA, LAConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = PBM;
                RAConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RA, RAConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = PBM;
                LLConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_LL, LLConfig.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = PBM;
                RLConfig.Place( this, Owner.GetLoadout(), new LocationIndex[] { new LocationIndex( -1, LocationIndex.MECH_LOC_RL, RLConfig.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetCLFF() {
        // set the armor to Clan Ferro-Fibrous
        Config = CLFF;
    }

    public void SetCLFF( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetCLFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetCLFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetCLFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetCLFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetCLFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetCLFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetCLFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFF.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetCLFF( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFF.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetCLFF( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = CLFF;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Industrial;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Head.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = CLFF;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Industrial;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Center Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = CLFF;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Industrial;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Left Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = CLFF;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Industrial;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Right Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = CLFF;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Industrial;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Left Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = CLFF;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Industrial;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Right Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = CLFF;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Industrial;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Left Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = CLFF;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Industrial;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Fibrous Armor as patchwork in the Right Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
        }
    }

    public void SetCLFL() {
        // set the armor to Clan Ferro-Lamellor
        Config = CLFL;
    }

    public void SetCLFL( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetCLFL( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFL.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetCLFL( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFL.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetCLFL( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFL.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetCLFL( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFL.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetCLFL( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFL.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetCLFL( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFL.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetCLFL( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFL.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetCLFL( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLFL.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetCLFL( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = CLFL;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Industrial;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Lamellor Armor as patchwork in the Head.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = CLFL;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Industrial;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Lamellor Armor as patchwork in the Center Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = CLFL;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Industrial;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Lamellor Armor as patchwork in the Left Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = CLFL;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Industrial;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Lamellor Armor as patchwork in the Right Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = CLFL;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Industrial;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Lamellor Armor as patchwork in the Left Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = CLFL;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Industrial;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Lamellor Armor as patchwork in the Right Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = CLFL;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Industrial;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Lamellor Armor as patchwork in the Left Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = CLFL;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Industrial;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ferro-Lamellor Armor as patchwork in the Right Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
        }
    }

    public void SetCLLR() {
        // set the armor to Inner Sphere Laser-Reflective
        Config = CLLR;
    }

    public void SetCLLR( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetCLLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetCLLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetCLLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetCLLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetCLLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetCLLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetCLLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLLR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetCLLR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLLR.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetCLLR( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = CLLR;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Industrial;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Head.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = CLLR;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Industrial;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Center Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = CLLR;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Industrial;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Left Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = CLLR;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Industrial;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Right Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = CLLR;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Industrial;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Left Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = CLLR;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Industrial;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Right Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = CLLR;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Industrial;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Left Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = CLLR;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Industrial;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Laser-Reflective Armor as patchwork in the Right Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
        }
    }

    public void SetCLRE() {
        // set the armor to Inner Sphere Reactive
        Config = CLRE;
    }

    public void SetCLRE( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLRE.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, CLRE.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetCLRE( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = CLRE;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Industrial;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Head.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = CLRE;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Industrial;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Center Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = CLRE;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Industrial;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Left Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = CLRE;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Industrial;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Right Torso.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = CLRE;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Industrial;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Left Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = CLRE;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Industrial;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Right Arm.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = CLRE;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Industrial;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Left Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = CLRE;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Industrial;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Reactive Armor as patchwork in the Right Leg.\nReverting to Heavy Industrial Armor." );
                }
                return;
        }
    }
    
    public void SetISAB() {
        Config = ISAB;
    }

    public void SetISAB( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISAB.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISAB.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISAB.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISAB.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISAB.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISAB.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISAB.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISAB.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISAB( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = ISAB;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Standard;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ablative Armor as patchwork in the Head.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = ISAB;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Standard;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ablative Armor as patchwork in the Center Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = ISAB;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Standard;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ablative Armor as patchwork in the Left Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = ISAB;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Standard;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ablative Armor as patchwork in the Right Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = ISAB;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Standard;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ablative Armor as patchwork in the Left Arm.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = ISAB;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Standard;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ablative Armor as patchwork in the Right Arm.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = ISAB;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Standard;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ablative Armor as patchwork in the Left Leg.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = ISAB;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Standard;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Ablative Armor as patchwork in the Right Leg.\nReverting to Standard Armor." );
                }
                return;
        }
    }
    
    
    public void SetHD() {
        Config = HeatDiss;
    }

    public void SetHD( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, HeatDiss.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, HeatDiss.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, HeatDiss.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, HeatDiss.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, HeatDiss.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, HeatDiss.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, HeatDiss.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetCLRE( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, HeatDiss.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetHD( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = HeatDiss;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Standard;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heat-Dissipating Armor as patchwork in the Head.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = HeatDiss;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Standard;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heat-Dissipating Armor as patchwork in the Center Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = HeatDiss;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Standard;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heat-Dissipating Armor as patchwork in the Left Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = HeatDiss;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Standard;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heat-Dissipating Armor as patchwork in the Right Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = HeatDiss;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Standard;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heat-Dissipating Armor as patchwork in the Left Arm.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = HeatDiss;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Standard;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heat-Dissipating Armor as patchwork in the Right Arm.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = HeatDiss;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Standard;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heat-Dissipating Armor as patchwork in the Left Leg.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = HeatDiss;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Standard;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit Heat-Dissipating Armor as patchwork in the Right Leg.\nReverting to Standard Armor." );
                }
                return;
        }
    }
    
    
    public void SetISIR() {
        Config = ISIR;
    }

    
    public void SetISIR( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetISIR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISIR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetISIR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISIR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetISIR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISIR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetISIR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISIR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetISIR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISIR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetISIR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISIR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetISIR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISIR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetISIR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISIR.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISIR( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = ISIR;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Standard;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISIR.ActualName() + " as patchwork in the Head.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = ISIR;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Standard;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISIR.ActualName() + " as patchwork in the Center Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = ISIR;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Standard;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISIR.ActualName() + " as patchwork in the Left Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = ISIR;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Standard;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISIR.ActualName() + " as patchwork in the Right Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = ISIR;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Standard;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISIR.ActualName() + " as patchwork in the Left Arm.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = ISIR;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Standard;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISIR.ActualName() + " as patchwork in the Right Arm.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = ISIR;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Standard;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISIR.ActualName() + " as patchwork in the Left Leg.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = ISIR;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Standard;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISIR.ActualName() + " as patchwork in the Right Leg.\nReverting to Standard Armor." );
                }
                return;
        }
    }
    
    
    public void SetISBR() {
        Config = ISBR;
    }

    
    public void SetISBR( int Loc ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                SetISBR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISBR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_CT:
                SetISBR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISBR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LT:
                SetISBR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISBR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RT:
                SetISBR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISBR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LA:
                SetISBR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISBR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RA:
                SetISBR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISBR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_LL:
                SetISBR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISBR.PatchworkCrits() ) } );
                return;
            case LocationIndex.MECH_LOC_RL:
                SetISBR( Loc, new LocationIndex[] { new LocationIndex( -1, Loc, ISBR.PatchworkCrits() ) } );
                return;
        }
    }

    public void SetISBR( int Loc, LocationIndex[] locs ) throws Exception {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                HDConfig = ISBR;
                if( ! HDConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    HDConfig = Standard;
                    HDConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISBR.ActualName() + " as patchwork in the Head.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_CT:
                CTConfig = ISBR;
                if( ! CTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    CTConfig = Standard;
                    CTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISBR.ActualName() + " as patchwork in the Center Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LT:
                LTConfig = ISBR;
                if( ! LTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LTConfig = Standard;
                    LTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISBR.ActualName() + " as patchwork in the Left Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RT:
                RTConfig = ISBR;
                if( ! RTConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RTConfig = Standard;
                    RTConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISBR.ActualName() + " as patchwork in the Right Torso.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LA:
                LAConfig = ISBR;
                if( ! LAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LAConfig = Standard;
                    LAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISBR.ActualName() + " as patchwork in the Left Arm.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RA:
                RAConfig = ISBR;
                if( ! RAConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RAConfig = Standard;
                    RAConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISBR.ActualName() + " as patchwork in the Right Arm.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_LL:
                LLConfig = ISBR;
                if( ! LLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    LLConfig = Standard;
                    LLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISBR.ActualName() + " as patchwork in the Left Leg.\nReverting to Standard Armor." );
                }
                return;
            case LocationIndex.MECH_LOC_RL:
                RLConfig = ISBR;
                if( ! RLConfig.Place( this, Owner.GetLoadout(), locs ) ) {
                    RLConfig = Standard;
                    RLConfig.Place( this, Owner.GetLoadout(), locs );
                    throw new Exception( "Could not fit " + ISBR.ActualName() + " as patchwork in the Right Leg.\nReverting to Standard Armor." );
                }
                return;
        }
    }
    
    public void Recalculate() {
        // recalculates the armor if mech tonnage or motive type changes
        SetMaxArmor();

        // now that we've set the maximums, make sure we're not exceeding them
        // the head max never changes during a recalc so we'll ignore it
        if( ArmorPoints[LocationIndex.MECH_LOC_CT] + ArmorPoints[LocationIndex.MECH_LOC_CTR] > MaxArmor[LocationIndex.MECH_LOC_CT] ) {
            int rear = Math.round( MaxArmor[LocationIndex.MECH_LOC_CT] * Owner.GetPrefs().getInt( "ArmorCTRPercent", DEFAULT_CTR_ARMOR_PERCENT ) / 100 );
            SetArmor( LocationIndex.MECH_LOC_CTR, rear );
            SetArmor( LocationIndex.MECH_LOC_CT, MaxArmor[LocationIndex.MECH_LOC_CT] - rear );
        }
        if( ArmorPoints[LocationIndex.MECH_LOC_LT] + ArmorPoints[LocationIndex.MECH_LOC_LTR] > MaxArmor[LocationIndex.MECH_LOC_LT] ) {
            int rear = Math.round( MaxArmor[LocationIndex.MECH_LOC_LT] * Owner.GetPrefs().getInt( "ArmorSTRPercent", DEFAULT_CTR_ARMOR_PERCENT ) / 100 );
            SetArmor( LocationIndex.MECH_LOC_LTR, rear );
            SetArmor( LocationIndex.MECH_LOC_LT, MaxArmor[LocationIndex.MECH_LOC_LT] - rear );
        }
        if( ArmorPoints[LocationIndex.MECH_LOC_RT] + ArmorPoints[LocationIndex.MECH_LOC_RTR] > MaxArmor[LocationIndex.MECH_LOC_RT] ) {
            int rear = Math.round( MaxArmor[LocationIndex.MECH_LOC_RT] * Owner.GetPrefs().getInt( "ArmorSTRPercent", DEFAULT_CTR_ARMOR_PERCENT ) / 100 );
            SetArmor( LocationIndex.MECH_LOC_RTR, rear );
            SetArmor( LocationIndex.MECH_LOC_RT, ( MaxArmor[LocationIndex.MECH_LOC_RT] - rear ) );
        }
        if( ArmorPoints[LocationIndex.MECH_LOC_LA] > MaxArmor[LocationIndex.MECH_LOC_LA] ) {
            SetArmor( LocationIndex.MECH_LOC_LA, MaxArmor[LocationIndex.MECH_LOC_LA] );
        }
        if( ArmorPoints[LocationIndex.MECH_LOC_RA] > MaxArmor[LocationIndex.MECH_LOC_RA] ) {
            SetArmor( LocationIndex.MECH_LOC_RA, MaxArmor[LocationIndex.MECH_LOC_RA] );
        }
        if( ArmorPoints[LocationIndex.MECH_LOC_LL] > MaxArmor[LocationIndex.MECH_LOC_LL] ) {
            SetArmor( LocationIndex.MECH_LOC_LL, MaxArmor[LocationIndex.MECH_LOC_LL] );
        }
        if( ArmorPoints[LocationIndex.MECH_LOC_RL] > MaxArmor[LocationIndex.MECH_LOC_RL] ) {
            SetArmor( LocationIndex.MECH_LOC_RL, MaxArmor[LocationIndex.MECH_LOC_RL] );
        }
        Owner.SetChanged( true );
    }

    public void SetMaxArmor() {
        // this sets the maximum array when tonnage changes.
        InternalStructure IntStruc = Owner.GetIntStruc();

        MaxArmor[LocationIndex.MECH_LOC_HD] = IntStruc.GetHeadPoints() * 3;
        MaxArmor[LocationIndex.MECH_LOC_CT] = IntStruc.GetCTPoints() * 2;
        MaxArmor[LocationIndex.MECH_LOC_LT] = IntStruc.GetSidePoints() * 2;
        MaxArmor[LocationIndex.MECH_LOC_RT] = IntStruc.GetSidePoints() * 2;
        MaxArmor[LocationIndex.MECH_LOC_LA] = IntStruc.GetArmPoints() * 2;
        MaxArmor[LocationIndex.MECH_LOC_RA] = IntStruc.GetArmPoints() * 2;
        MaxArmor[LocationIndex.MECH_LOC_LL] = IntStruc.GetLegPoints() * 2;
        MaxArmor[LocationIndex.MECH_LOC_RL] = IntStruc.GetLegPoints() * 2;
    }

    public void IncrementArmor( int Loc ) {
        // Check the location and see what we have to do
        switch( Loc ) {
            case 0: case 4: case 5: case 6: case 7:
                IncrementSingle( Loc );
                break;
            case 1: case 2: case 3: case 8: case 9: case 10:
                // now we have to figure out the opposite side.
                int rear = Loc;
                if( Loc < 8 ) {
                    rear += 7;
                } else {
                    rear -= 7;
                }
                IncrementDouble( Loc, rear );
                break;
        }
    }

    private void IncrementSingle( int Loc ) {
        // Make sure we're not exceeding the max
        if( ArmorPoints[Loc] < MaxArmor[Loc] ) {
            ArmorPoints[Loc]++;
        }
        Owner.SetChanged( true );
    }

    private void IncrementDouble( int LocFront, int LocRear ) {
        int MaxLoc = 0;
        // figure out which one is the actual front, since they're nebulously passed in.
        if( LocFront > LocRear ) {
            // They're reversed
            MaxLoc = LocRear;
        } else {
            // not reversed.
            MaxLoc = LocFront;
        }

        // Make sure we're not exceeding the max
        if( ArmorPoints[LocFront] < MaxArmor[MaxLoc] ) {
            ArmorPoints[LocFront]++;
            // now check to see if the rear needs to be decremented
            if( ArmorPoints[LocFront] + ArmorPoints[LocRear] > MaxArmor[MaxLoc] ) {
                if( MaxArmor[MaxLoc] == ArmorPoints[LocFront] ) {
                    ArmorPoints[LocRear] = 0;
                } else {
                    // Should check and see how far above we are, just in case
                    int dif = ArmorPoints[LocFront] + ArmorPoints[LocRear] - MaxArmor[MaxLoc];
                    while( dif > 0 ) {
                        // makes sure we're not decrementing it below 0 without extra code
                        DecrementArmor( LocRear );
                        dif--;
                    }
                }
            }
        }
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
        switch( Loc ) {
            case 0: case 4: case 5: case 6: case 7:
                SetSingle( Loc, av );
                break;
            case 1: case 2: case 3: case 8: case 9: case 10:
                // now we have to figure out the opposite side.
                int rear = Loc;
                if( Loc < 8 ) {
                    rear += 7;
                } else {
                    rear -= 7;
                }
                SetDouble( Loc, rear, av );
                break;
        }
        Owner.SetChanged( true );
    }

    private void SetSingle( int Loc, int av ) {
        // make sure we're within bounds
        if( av > MaxArmor[Loc] ) {
            ArmorPoints[Loc] = MaxArmor[Loc];
        } else if( av < 0 ) {
            ArmorPoints[Loc] = 0;
        } else {
            ArmorPoints[Loc] = av;
        }
        Owner.SetChanged( true );
    }

    private void SetDouble( int LocFront, int LocRear, int av ) {
        int MaxLoc = 0;
        // figure out which one is the actual front, since they're nebulously
        // passed in and we could get some array out of bounds errors
        if( LocFront > LocRear ) {
            // They're reversed
            MaxLoc = LocRear;
        } else {
            // not reversed.
            MaxLoc = LocFront;
        }
        
        // make sure we're within bounds
        if( av > MaxArmor[MaxLoc] ) {
            ArmorPoints[LocFront] = MaxArmor[MaxLoc];
            // also need to fix the rear to the least
            ArmorPoints[LocRear] = 0;
        } else if( av < 0 ) {
            ArmorPoints[LocFront] = 0;
        } else {
            // make sure we decrement the rear a similar amount if needed.
            if( av + ArmorPoints[LocRear] > MaxArmor[MaxLoc] ) {
                if( MaxArmor[MaxLoc] == av ) {
                    ArmorPoints[LocRear] = 0;
                } else {
                    // find the difference
                    int dif = av + ArmorPoints[LocRear] - MaxArmor[MaxLoc];
                    while( dif > 0 ) {
                        // makes sure we're not decrementing it below 0 without extra code
                        DecrementArmor( LocRear );
                        dif--;
                    }
                }
            }
            // finally, set the armor
            ArmorPoints[LocFront] = av;
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
        int result = 0;
        result += MaxArmor[0];
        result += MaxArmor[1];
        result += MaxArmor[2];
        result += MaxArmor[3];
        result += MaxArmor[4];
        result += MaxArmor[5];
        result += MaxArmor[6];
        result += MaxArmor[7];
        return result;
    }

    public int GetArmorValue() {
        int result = 0;
        result += ArmorPoints[0];
        result += ArmorPoints[1];
        result += ArmorPoints[2];
        result += ArmorPoints[3];
        result += ArmorPoints[4];
        result += ArmorPoints[5];
        result += ArmorPoints[6];
        result += ArmorPoints[7];
        result += ArmorPoints[8];
        result += ArmorPoints[9];
        result += ArmorPoints[10];
        return result;
    }

    public int GetModularArmorValue() {
        int result = 0;
        int[] ModArmor = Owner.GetLoadout().FindModularArmor();
        result += ModArmor[0] * 10;
        result += ModArmor[1] * 10;
        result += ModArmor[2] * 10;
        result += ModArmor[3] * 10;
        result += ModArmor[4] * 10;
        result += ModArmor[5] * 10;
        result += ModArmor[6] * 10;
        result += ModArmor[7] * 10;
        result += ModArmor[8] * 10;
        result += ModArmor[9] * 10;
        result += ModArmor[10] * 10;
        return result;
    }

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

    public boolean RequiresExtraRules() {
        if (IsHardened() || IsReactive() || IsReflective() || IsStealth() || IsPatchwork() ) {
            return true;
        } else {
            return false;
        }
    }

    public int GetTechBase() {
        return Config.GetAvailability().GetTechBase();
    }

    public boolean IsStealth() {
        return Config.IsStealth();
    }

    @Override
    public boolean Place( ifMechLoadout l ) {
        return Config.Place( this, Owner.GetLoadout() );
    }

    @Override
    public boolean Place( ifMechLoadout l, LocationIndex[] a ) {
        return Config.Place( this, l, a );
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

    @Override
    public String CritName( int Loc ) {
        if ( Config != Patchwork ) return Config.CritName();
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                return HDConfig.CritName();
            case LocationIndex.MECH_LOC_CT:
                return CTConfig.CritName();
            case LocationIndex.MECH_LOC_LT:
                return LTConfig.CritName();
            case LocationIndex.MECH_LOC_RT:
                return RTConfig.CritName();
            case LocationIndex.MECH_LOC_LA:
                return LAConfig.CritName();
            case LocationIndex.MECH_LOC_RA:
                return RAConfig.CritName();
            case LocationIndex.MECH_LOC_LL:
                return LLConfig.CritName();
            case LocationIndex.MECH_LOC_RL:
                return RLConfig.CritName();
            default:
                return "Unknown Item!";
        }
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

    public ifArmor GetHDArmorType() {
        return HDConfig;
    }

    public ifArmor GetCTArmorType() {
        return CTConfig;
    }

    public ifArmor GetLTArmorType() {
        return LTConfig;
    }

    public ifArmor GetRTArmorType() {
        return RTConfig;
    }

    public ifArmor GetLAArmorType() {
        return LAConfig;
    }

    public ifArmor GetRAArmorType() {
        return RAConfig;
    }

    public ifArmor GetLLArmorType() {
        return LLConfig;
    }

    public ifArmor GetRLArmorType() {
        return RLConfig;
    }

    public ifState GetLocationType( int Loc ) {
        switch( Loc ) {
            case LocationIndex.MECH_LOC_HD:
                return (ifState) HDConfig;
            case LocationIndex.MECH_LOC_CT:
                return (ifState) CTConfig;
            case LocationIndex.MECH_LOC_LT:
                return (ifState) LTConfig;
            case LocationIndex.MECH_LOC_RT:
                return (ifState) RTConfig;
            case LocationIndex.MECH_LOC_LA:
                return (ifState) LAConfig;
            case LocationIndex.MECH_LOC_RA:
                return (ifState) RAConfig;
            case LocationIndex.MECH_LOC_LL:
                return (ifState) LLConfig;
            case LocationIndex.MECH_LOC_RL:
                return (ifState) RLConfig;
            default:
                return null;
        }
    }

    @Override
    public int NumCrits() {
        return Config.NumCrits();
    }

    public int NumCVSpaces() {
        return 0;
    }

    @Override
    public int NumPlaced() {
        return Placed;
    }

    @Override
    public void IncrementPlaced() {
        Placed++;
    }

    @Override
    public void DecrementPlaced() {
        Placed--;
    }

    @Override
    public double GetTonnage() {
        if( IsPatchwork() ) {
            double result = 0.0;
            result += GetHDTonnage();
            result += GetCTTonnage();
            result += GetLTTonnage();
            result += GetRTTonnage();
            result += GetLATonnage();
            result += GetRATonnage();
            result += GetLLTonnage();
            result += GetRLTonnage();
            return result;
        } else {
            if( Owner.UsingFractionalAccounting() ) {
                return CommonTools.RoundFractionalTons( GetArmorValue() * Config.GetPointsPerTon() );
            }
            double result = GetArmorValue() / ( 8 * Config.GetAVMult() );
            int mid = (int) Math.floor( result + 0.9999 );
            result = mid * 0.5;
            return result;
        }
    }

    public double GetHDTonnage() {
        if( Owner.UsingFractionalAccounting() ) {
            double result = ArmorPoints[LocationIndex.MECH_LOC_HD] * HDConfig.GetPointsPerTon();
            return CommonTools.RoundFractionalTons( result );
        } else {
            int mid = (int) Math.floor( ArmorPoints[LocationIndex.MECH_LOC_HD] / ( 8 * HDConfig.GetAVMult() ) + 0.9999 );
            return mid * 0.5;
        }
    }

    public double GetCTTonnage() {
        if( Owner.UsingFractionalAccounting() ) {
            double result = ArmorPoints[LocationIndex.MECH_LOC_CT] * CTConfig.GetPointsPerTon();
            result += ArmorPoints[LocationIndex.MECH_LOC_CTR] * CTConfig.GetPointsPerTon();
            return CommonTools.RoundFractionalTons( result );
        } else {
            int mid = (int) Math.floor( ( ArmorPoints[LocationIndex.MECH_LOC_CT] + ArmorPoints[LocationIndex.MECH_LOC_CTR] ) / ( 8 * CTConfig.GetAVMult() ) + 0.9999 );
            return mid * 0.5;
        }
    }

    public double GetLTTonnage() {
        if( Owner.UsingFractionalAccounting() ) {
            double result = ArmorPoints[LocationIndex.MECH_LOC_LT] * LTConfig.GetPointsPerTon();
            result += ArmorPoints[LocationIndex.MECH_LOC_LTR] * LTConfig.GetPointsPerTon();
            return CommonTools.RoundFractionalTons( result );
        } else {
            int mid = (int) Math.floor( ( ArmorPoints[LocationIndex.MECH_LOC_LT] + ArmorPoints[LocationIndex.MECH_LOC_LTR] ) / ( 8 * LTConfig.GetAVMult() ) + 0.9999 );
            return mid * 0.5;
        }
    }

    public double GetRTTonnage() {
        if( Owner.UsingFractionalAccounting() ) {
            double result = ArmorPoints[LocationIndex.MECH_LOC_RT] * RTConfig.GetPointsPerTon();
            result += ArmorPoints[LocationIndex.MECH_LOC_RTR] * RTConfig.GetPointsPerTon();
            return CommonTools.RoundFractionalTons( result );
        } else {
            int mid = (int) Math.floor( ( ArmorPoints[LocationIndex.MECH_LOC_RT] + ArmorPoints[LocationIndex.MECH_LOC_RTR] ) / ( 8 * RTConfig.GetAVMult() ) + 0.9999 );
            return mid * 0.5;
        }
    }

    public double GetLATonnage() {
        if( Owner.UsingFractionalAccounting() ) {
            double result = ArmorPoints[LocationIndex.MECH_LOC_LA] * LAConfig.GetPointsPerTon();
            return CommonTools.RoundFractionalTons( result );
        } else {
            int mid = (int) Math.floor( ArmorPoints[LocationIndex.MECH_LOC_LA] / ( 8 * LAConfig.GetAVMult() ) + 0.9999 );
            return mid * 0.5;
        }
    }

    public double GetRATonnage() {
        if( Owner.UsingFractionalAccounting() ) {
            double result = ArmorPoints[LocationIndex.MECH_LOC_RA] * RAConfig.GetPointsPerTon();
            return CommonTools.RoundFractionalTons( result );
        } else {
            int mid = (int) Math.floor( ArmorPoints[LocationIndex.MECH_LOC_RA] / ( 8 * RAConfig.GetAVMult() ) + 0.9999 );
            return mid * 0.5;
        }
    }

    public double GetLLTonnage() {
        if( Owner.UsingFractionalAccounting() ) {
            double result = ArmorPoints[LocationIndex.MECH_LOC_LL] * LLConfig.GetPointsPerTon();
            return CommonTools.RoundFractionalTons( result );
        } else {
            int mid = (int) Math.floor( ArmorPoints[LocationIndex.MECH_LOC_LL] / ( 8 * LLConfig.GetAVMult() ) + 0.9999 );
            return mid * 0.5;
        }
    }

    public double GetRLTonnage() {
        if( Owner.UsingFractionalAccounting() ) {
            double result = ArmorPoints[LocationIndex.MECH_LOC_RL] * RLConfig.GetPointsPerTon();
            return CommonTools.RoundFractionalTons( result );
        } else {
            int mid = (int) Math.floor( ArmorPoints[LocationIndex.MECH_LOC_RL] / ( 8 * RLConfig.GetAVMult() ) + 0.9999 );
            return mid * 0.5;
        }
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
        if( Owner.UsingFractionalAccounting() || IsPatchwork() ) { return 0; }
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
        // returns the maximum armor tonnage supported by this mech.
        if( IsPatchwork() ) {
            if( Owner.UsingFractionalAccounting() ) {
                double result = 0.0;
                result += MaxArmor[LocationIndex.MECH_LOC_HD] * HDConfig.GetPointsPerTon();
                result += MaxArmor[LocationIndex.MECH_LOC_CT] * CTConfig.GetPointsPerTon();
                result += MaxArmor[LocationIndex.MECH_LOC_LT] * LTConfig.GetPointsPerTon();
                result += MaxArmor[LocationIndex.MECH_LOC_RT] * RTConfig.GetPointsPerTon();
                result += MaxArmor[LocationIndex.MECH_LOC_LA] * LAConfig.GetPointsPerTon();
                result += MaxArmor[LocationIndex.MECH_LOC_RA] * RAConfig.GetPointsPerTon();
                result += MaxArmor[LocationIndex.MECH_LOC_LL] * LLConfig.GetPointsPerTon();
                result += MaxArmor[LocationIndex.MECH_LOC_RL] * RLConfig.GetPointsPerTon();
                return CommonTools.RoundFractionalTons( result );
            } else {
                int HDmid = (int) Math.floor( MaxArmor[LocationIndex.MECH_LOC_HD] / ( 8 * HDConfig.GetAVMult() ) + 0.9999 );
                int CTmid = (int) Math.floor( MaxArmor[LocationIndex.MECH_LOC_CT] / ( 8 * CTConfig.GetAVMult() ) + 0.9999 );
                int LTmid = (int) Math.floor( MaxArmor[LocationIndex.MECH_LOC_LT] / ( 8 * LTConfig.GetAVMult() ) + 0.9999 );
                int RTmid = (int) Math.floor( MaxArmor[LocationIndex.MECH_LOC_RT] / ( 8 * RTConfig.GetAVMult() ) + 0.9999 );
                int LAmid = (int) Math.floor( MaxArmor[LocationIndex.MECH_LOC_LA] / ( 8 * LAConfig.GetAVMult() ) + 0.9999 );
                int RAmid = (int) Math.floor( MaxArmor[LocationIndex.MECH_LOC_RA] / ( 8 * RAConfig.GetAVMult() ) + 0.9999 );
                int LLmid = (int) Math.floor( MaxArmor[LocationIndex.MECH_LOC_LL] / ( 8 * LLConfig.GetAVMult() ) + 0.9999 );
                int RLmid = (int) Math.floor( MaxArmor[LocationIndex.MECH_LOC_RL] / ( 8 * RLConfig.GetAVMult() ) + 0.9999 );
                return ( HDmid * 0.5 ) + ( CTmid * 0.5 ) + ( LTmid * 0.5 ) + ( RTmid * 0.5 ) + ( LAmid * 0.5 ) + ( RAmid * 0.5 ) + ( LLmid * 0.5 ) + ( RLmid * 0.5 );
            }
        } else {
            if( Owner.UsingFractionalAccounting() ) {
                return CommonTools.RoundFractionalTons( GetMaxArmor() * Config.GetPointsPerTon() );
            }
            double result = GetMaxArmor() / ( 8 * Config.GetAVMult() );
            int mid = (int) Math.round( result + 0.4999 );
            result = mid * 0.5;
            return result;
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
        return Config.GetBVTypeMult() + Owner.GetTotalModifiers( true, true ).ArmorMultiplier();
    }

    public ifState[] GetStates() {
        ifState[] retval = { (ifState) Industrial, (ifState) Commercial, (ifState) PBM, (ifState) Standard, (ifState) ISFF, (ifState) CLFF,
            (ifState) ISLF, (ifState) ISHF, (ifState) ISST, (ifState) Hardened, (ifState) ISLR, (ifState) CLLR, (ifState) ISRE, (ifState) CLRE,
             (ifState) CLFL, (ifState) ISAB, (ifState) HeatDiss, (ifState) ISIR, (ifState) ISBR, (ifState) Patchwork };
        return retval;
    }

    public ifState[] GetPatchworkStates() {
        ifState[] retval = { (ifState) Industrial, (ifState) Commercial, (ifState) PBM, (ifState) Standard, (ifState) ISFF, (ifState) CLFF,
            (ifState) ISLF, (ifState) ISHF, (ifState) ISST, (ifState) Hardened, (ifState) ISLR, (ifState) CLLR, (ifState) ISRE, (ifState) CLRE,
             (ifState) CLFL, (ifState) ISAB, (ifState) HeatDiss, (ifState) ISIR, (ifState) ISBR };
        return retval;
    }

    @Override
    public double GetCost() {
        double result = 0.0;
        if( IsPatchwork() ) {
            result += GetHDCost();
            result += GetCTCost();
            result += GetLTCost();
            result += GetRTCost();
            result += GetLACost();
            result += GetRACost();
            result += GetLLCost();
            result += GetRLCost();
        } else {
            result = GetTonnage() * Config.GetCostMult();
        }
        if( Owner.IsPrimitive() && Owner.GetYear() < 2450 ) { result *= 2.0; }
        return result;
    }

    public double GetHDCost() {
        return GetHDTonnage() * HDConfig.GetCostMult();
    }

    public double GetCTCost() {
        return GetCTTonnage() * CTConfig.GetCostMult();
    }

    public double GetLTCost() {
        return GetLTTonnage() * LTConfig.GetCostMult();
    }

    public double GetRTCost() {
        return GetRTTonnage() * RTConfig.GetCostMult();
    }

    public double GetLACost() {
        return GetLATonnage() * LAConfig.GetCostMult();
    }

    public double GetRACost() {
        return GetRATonnage() * RAConfig.GetCostMult();
    }

    public double GetLLCost() {
        return GetLLTonnage() * LLConfig.GetCostMult();
    }

    public double GetRLCost() {
        return GetRLTonnage() * RLConfig.GetCostMult();
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
        if( IsPatchwork() ) {
            double result = 0.0;
            int[] ModArmor = Owner.GetLoadout().FindModularArmor();
            result += GetHDDefensiveBV( ModArmor );
            result += GetCTDefensiveBV( ModArmor );
            result += GetLTDefensiveBV( ModArmor );
            result += GetRTDefensiveBV( ModArmor );
            result += GetLADefensiveBV( ModArmor );
            result += GetRADefensiveBV( ModArmor );
            result += GetLLDefensiveBV( ModArmor );
            result += GetRLDefensiveBV( ModArmor );
            return result * 2.5;
        } else {
            if( Owner.GetCockpit().IsTorsoMounted() ) {
                return ( GetArmorValue() + ArmorPoints[LocationIndex.MECH_LOC_CT] + ArmorPoints[LocationIndex.MECH_LOC_CTR] + GetModularArmorValue() ) * GetBVTypeMult() * 2.5;
            } else {
                return ( GetArmorValue() + GetModularArmorValue() ) * GetBVTypeMult() * 2.5;
            }
        }
    }

    public double GetHDDefensiveBV( int[] ModArmor ) {
        return ( ArmorPoints[LocationIndex.MECH_LOC_HD] + ModArmor[LocationIndex.MECH_LOC_HD] * 10 ) * HDConfig.GetBVTypeMult();
    }

    public double GetCTDefensiveBV( int[] ModArmor ) {
        if( Owner.GetCockpit().IsTorsoMounted() ) {
            return ( ArmorPoints[LocationIndex.MECH_LOC_CT] + ArmorPoints[LocationIndex.MECH_LOC_CTR] + ModArmor[LocationIndex.MECH_LOC_CT] * 10 + ModArmor[LocationIndex.MECH_LOC_CTR] * 10 ) * CTConfig.GetBVTypeMult() * 2.0;
        } else {
            return ( ArmorPoints[LocationIndex.MECH_LOC_CT] + ArmorPoints[LocationIndex.MECH_LOC_CTR] + ModArmor[LocationIndex.MECH_LOC_CT] * 10 + ModArmor[LocationIndex.MECH_LOC_CTR] * 10 ) * CTConfig.GetBVTypeMult();
        }
    }

    public double GetLTDefensiveBV( int[] ModArmor ) {
        return ( ArmorPoints[LocationIndex.MECH_LOC_LT] + ArmorPoints[LocationIndex.MECH_LOC_LTR] + ModArmor[LocationIndex.MECH_LOC_RT] * 10 + ModArmor[LocationIndex.MECH_LOC_LTR] * 10 ) * LTConfig.GetBVTypeMult();
    }

    public double GetRTDefensiveBV( int[] ModArmor ) {
        return ( ArmorPoints[LocationIndex.MECH_LOC_RT] + ArmorPoints[LocationIndex.MECH_LOC_RTR] + ModArmor[LocationIndex.MECH_LOC_LT] * 10 + ModArmor[LocationIndex.MECH_LOC_RTR] * 10 ) * RTConfig.GetBVTypeMult();
    }

    public double GetLADefensiveBV( int[] ModArmor ) {
        return ( ArmorPoints[LocationIndex.MECH_LOC_LA] + ModArmor[LocationIndex.MECH_LOC_LA] * 10 ) * LAConfig.GetBVTypeMult();
    }

    public double GetRADefensiveBV( int[] ModArmor ) {
        return ( ArmorPoints[LocationIndex.MECH_LOC_RA] + ModArmor[LocationIndex.MECH_LOC_RA] * 10 ) * RAConfig.GetBVTypeMult();
    }

    public double GetLLDefensiveBV( int[] ModArmor ) {
        return ( ArmorPoints[LocationIndex.MECH_LOC_LL] + ModArmor[LocationIndex.MECH_LOC_LL] * 10 ) * LLConfig.GetBVTypeMult();
    }

    public double GetRLDefensiveBV( int[] ModArmor ) {
        return ( ArmorPoints[LocationIndex.MECH_LOC_RL] + ModArmor[LocationIndex.MECH_LOC_RL] * 10 ) * RLConfig.GetBVTypeMult();
    }

    public int GetBAR() {
        return Config.GetBAR();
    }

    public int GetHDBAR() {
        return HDConfig.GetBAR();
    }

    public int GetCTBAR() {
        return CTConfig.GetBAR();
    }

    public int GetLTBAR() {
        return LTConfig.GetBAR();
    }

    public int GetRTBAR() {
        return RTConfig.GetBAR();
    }

    public int GetLABAR() {
        return LAConfig.GetBAR();
    }

    public int GetRABAR() {
        return RAConfig.GetBAR();
    }

    public int GetLLBAR() {
        return LLConfig.GetBAR();
    }

    public int GetRLBAR() {
        return RLConfig.GetBAR();
    }

    public boolean AllowHarJel()
    {
        return Config.AllowHarJel();
    }

    @Override
    public void ResetPlaced() {
        Placed = 0;
    }

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
        if( IsPatchwork() ) {
            if( LLConfig == Hardened || RLConfig == Hardened ) {
                System.out.println( "Added Hardened Modifier, LLConfig = " + LLConfig.ActualName() + ", RLConfig = " + RLConfig.ActualName() );
                return Hardened.GetMechModifier();
            } else {
                return Config.GetMechModifier();
            }
        } else {
            return Config.GetMechModifier();
        }
    }

    @Override
    public String toString() {
        if( Config.NumCrits() > 0 ) {
            if( Config.NumCrits() > Placed ) {
                if( Config.IsStealth() ) {
                    return Config.CritName();
                } else {
                    return Config.CritName() + " (" + ( Config.NumCrits() - Placed ) + ")";
                }
            } else {
                return Config.CritName();
            }
        }
        return Config.CritName();
    }
}

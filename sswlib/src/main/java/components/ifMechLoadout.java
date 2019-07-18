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

import java.util.ArrayList;

public interface ifMechLoadout {
    public void SetName( String s );
    public String GetName();
    public void SetSource( String s );
    public String GetSource();
    public Mech GetMech();
    public boolean IsQuad();
    public boolean IsTripod();
    public abPlaceable GetNoItem();
    public JumpJetFactory GetJumpJets();
    public HeatSinkFactory GetHeatSinks();
    public ActuatorSet GetActuators();
    public int GetRulesLevel();
    public boolean SetRulesLevel( int NewLevel );
    public int GetTechBase();
    public void SetTechBase( int NewLevel );
    public int GetEra();
    public boolean SetEra( int era );
    public int GetProductionEra();
    public boolean SetProductionEra( int era );
    public int GetYear();
    public void SetYear( int year, boolean specified );
    public boolean YearWasSpecified();
    public void SetYearRestricted( boolean b );
    public boolean IsYearRestricted();
    public void AddToQueue( abPlaceable p );
    public void RemoveFromQueue( abPlaceable p );
    public abPlaceable GetFromQueueByIndex( int Index );
    public boolean QueueContains( abPlaceable p );
    public EquipmentCollection GetCollection( abPlaceable p );
    public ArrayList GetQueue();
    public ArrayList GetNonCore();
    public ArrayList GetEquipment();
    public void ClearQueue(); // unused
    public void FullUnallocate();
    public void ClearLoadout();
    public void SafeClearLoadout();
    public void SafeUnallocateHD();
    public void SafeMassUnallocate();
    public void Transfer( ifMechLoadout l );
    public void AddTo( abPlaceable p, int Loc, int SIndex ) throws Exception;
    public void AddTo( abPlaceable[] Loc, abPlaceable p, int index, int numcrits ) throws Exception;
    public void AddToHD( abPlaceable p ) throws Exception;
    public void AddToCT( abPlaceable p ) throws Exception;
    public void AddToLT( abPlaceable p ) throws Exception;
    public void AddToRT( abPlaceable p ) throws Exception;
    public void AddToLA( abPlaceable p ) throws Exception;
    public void AddToRA( abPlaceable p ) throws Exception;
    public void AddToLL( abPlaceable p ) throws Exception;
    public void AddToRL( abPlaceable p ) throws Exception;
    public void AddToCL( abPlaceable p ) throws Exception;
    public void AddToHD( abPlaceable p, int SIndex ) throws Exception;
    public void AddToCT( abPlaceable p, int SIndex ) throws Exception;
    public void AddToRT( abPlaceable p, int SIndex ) throws Exception;
    public void AddToLT( abPlaceable p, int SIndex ) throws Exception;
    public void AddToRA( abPlaceable p, int SIndex ) throws Exception;
    public void AddToLA( abPlaceable p, int SIndex ) throws Exception;
    public void AddToRL( abPlaceable p, int SIndex ) throws Exception;
    public void AddToLL( abPlaceable p, int SIndex ) throws Exception;
    public void AddToCL( abPlaceable p, int SIndex ) throws Exception;
    public abPlaceable[] GetHDCrits();
    public abPlaceable[] GetCTCrits();
    public abPlaceable[] GetRTCrits();
    public abPlaceable[] GetLTCrits();
    public abPlaceable[] GetRACrits();
    public abPlaceable[] GetLACrits();
    public abPlaceable[] GetRLCrits();
    public abPlaceable[] GetLLCrits();
    public abPlaceable[] GetCLCrits();
    public abPlaceable[] GetCrits( int Loc );
    public int Find( abPlaceable p );
    public LocationIndex FindIndex( abPlaceable p );
    public ArrayList FindSplitIndex( abPlaceable p );
    public int[] FindInstances( abPlaceable p );
    public ArrayList FindIndexes( abPlaceable p );
    public int[] FindHeatSinks();
    public int[] FindJumpJets( boolean IJJ );
    public int[] FindModularArmor();
    public int[] FindExplosiveInstances();
//    public void FlushIllegal( int Era, int Year, boolean Restrict );
    public void FlushIllegal();
    public boolean UnallocateAll( abPlaceable p, boolean override );
    public void Remove( abPlaceable p );
    public void UnallocateByIndex( int SIndex, abPlaceable[] a );
    public void AutoAllocate( abPlaceable p );
    public void AutoAllocate( EquipmentCollection e );
    public void SplitAllocate( abPlaceable p, int FirstLoc, int FirstIndex, int SecondLoc ) throws Exception;
    public int FreeFrom( abPlaceable [] Loc, int index );
    public void Compact();
    public boolean IsAllocated( abPlaceable p );
    public int UnplacedCrits();
    public int FreeCrits();
    public int FreeCrits( abPlaceable[] Loc );
    public int FirstFree( abPlaceable[] Loc );
    public void LockChassis();
    public void UnlockChassis();
    public ifMechLoadout Clone();
    public void SetBaseLoadout( ifMechLoadout l );
    public ifMechLoadout GetBaseLoadout();
    public void SetHDCrits( abPlaceable[] c );
    public void SetCTCrits( abPlaceable[] c );
    public void SetLTCrits( abPlaceable[] c );
    public void SetRTCrits( abPlaceable[] c );
    public void SetLACrits( abPlaceable[] c );
    public void SetRACrits( abPlaceable[] c );
    public void SetLLCrits( abPlaceable[] c );
    public void SetRLCrits( abPlaceable[] c );
    public void SetCLCrits( abPlaceable[] c );
    public void SetNonCore( ArrayList v );
    public void SetTCList( ArrayList v );
    public void SetEquipment( ArrayList v );
    public boolean CanUseClanCASE();
    public boolean IsUsingClanCASE();
    public void SetClanCASE( boolean b );
    public void SetCTCASE( boolean Add, int index ) throws Exception;
    public void SetLTCASE( boolean Add, int index ) throws Exception;
    public void SetRTCASE( boolean Add, int index ) throws Exception;
    public void SetCTCASE( CASE c );
    public void SetLTCASE( CASE c );
    public void SetRTCASE( CASE c );
    public boolean HasCTCASE();
    public boolean HasLTCASE();
    public boolean HasRTCASE();
    public CASE GetCTCase();
    public CASE GetRTCase();
    public CASE GetLTCase();
    public void SetHDCASEII( boolean Add, int index, boolean clan ) throws Exception;
    public void SetHDCASEII( CASEII c );
    public boolean HasHDCASEII();
    public CASEII GetHDCaseII();
    public void SetCTCASEII( boolean Add, int index, boolean clan ) throws Exception;
    public void SetCTCASEII( CASEII c );
    public boolean HasCTCASEII();
    public CASEII GetCTCaseII();
    public void SetLTCASEII( boolean Add, int index, boolean clan ) throws Exception;
    public void SetLTCASEII( CASEII c );
    public boolean HasLTCASEII();
    public CASEII GetLTCaseII();
    public void SetRTCASEII( boolean Add, int index, boolean clan ) throws Exception;
    public void SetRTCASEII( CASEII c );
    public boolean HasRTCASEII();
    public CASEII GetRTCaseII();
    public void SetLACASEII( boolean Add, int index, boolean clan ) throws Exception;
    public void SetLACASEII( CASEII c );
    public boolean HasLACASEII();
    public CASEII GetLACaseII();
    public void SetRACASEII( boolean Add, int index, boolean clan ) throws Exception;
    public void SetRACASEII( CASEII c );
    public boolean HasRACASEII();
    public CASEII GetRACaseII();
    public void SetLLCASEII( boolean Add, int index, boolean clan ) throws Exception;
    public void SetLLCASEII( CASEII c );
    public boolean HasLLCASEII();
    public CASEII GetLLCaseII();
    public void SetRLCASEII( boolean Add, int index, boolean clan ) throws Exception;
    public void SetRLCASEII( CASEII c );
    public boolean HasRLCASEII();
    public CASEII GetRLCaseII();
    public void SetCLCASEII( boolean Add, int index, boolean clan ) throws Exception;
    public void SetCLCASEII( CASEII c );
    public boolean HasCLCASEII();
    public CASEII GetCLCaseII();
    public void SetFCSArtemisIV( boolean b ) throws Exception;
    public void SetFCSArtemisV( boolean b ) throws Exception;
    public void SetFCSApollo( boolean b ) throws Exception;
    public boolean UsingArtemisIV();
    public boolean UsingArtemisV();
    public boolean UsingApollo();
    public boolean UsingDumper();
    public Dumper GetDumper();
    public void UseDumper( boolean use, String dumpDirection);
    public void CheckDumper();
    public boolean UsingTC();
    public TargetingComputer GetTC();
    public void UseTC( boolean use, boolean clan );
    public void CheckTC();
    public void UnallocateTC();
    public void SetSupercharger( boolean b, int Loc, int index ) throws Exception;
    public void SetSupercharger( Supercharger s );
    public boolean HasSupercharger();
    public Supercharger GetSupercharger();
    public PowerAmplifier GetPowerAmplifier();
    public MechTurret GetHDTurret();
    public void SetHDTurret( boolean Add, int index ) throws Exception;
    public boolean HasHDTurret();
    public boolean CanUseHDTurret();
    public void SetHDTurret( MechTurret t );
    public MechTurret GetLTTurret();
    public void SetLTTurret( boolean Add, int index ) throws Exception;
    public boolean HasLTTurret();
    public boolean CanUseLTTurret();
    public void SetLTTurret( MechTurret t );
    public MechTurret GetRTTurret();
    public void SetRTTurret( boolean Add, int index ) throws Exception;
    public boolean HasRTTurret();
    public boolean CanUseRTTurret();
    public void SetRTTurret( MechTurret t );
    public void UnallocateFuelTanks();
    public void CheckExclusions( abPlaceable a ) throws Exception;
    public void AddMechModifier( MechModifier m );
    public void RemoveMechMod( MechModifier m );
    public ArrayList GetMechMods();
    public void SetBoobyTrap( boolean b ) throws Exception;
    public boolean HasBoobyTrap();
    public BoobyTrap GetBoobyTrap();
}

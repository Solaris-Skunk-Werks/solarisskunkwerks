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

import java.util.Vector;

public interface ifLoadout {
    public void SetName( String s );
    public String GetName();
    public Mech GetMech();
    public boolean IsQuad();
    public abPlaceable GetNoItem();
    public JumpJetFactory GetJumpJets();
    public HeatSinkFactory GetHeatSinks();
    public ActuatorSet GetActuators();
    public int GetRulesLevel();
    public boolean SetRulesLevel( int NewLevel );
    public void AddToQueue( abPlaceable p );
    public void RemoveFromQueue( abPlaceable p );
    public abPlaceable GetFromQueueByIndex( int Index );
    public Vector GetQueue();
    public Vector GetNonCore();
    public Vector GetEquipment();
    public Vector GetTCList();
    public void ClearQueue(); // unused
    public void FullUnallocate();
    public void ClearLoadout();
    public void SafeClearLoadout();
    public void SafeMassUnallocate();
    public void Transfer( ifLoadout l );
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
    public void AddToHD( abPlaceable p, int SIndex ) throws Exception;
    public void AddToCT( abPlaceable p, int SIndex ) throws Exception;
    public void AddToRT( abPlaceable p, int SIndex ) throws Exception;
    public void AddToLT( abPlaceable p, int SIndex ) throws Exception;
    public void AddToRA( abPlaceable p, int SIndex ) throws Exception;
    public void AddToLA( abPlaceable p, int SIndex ) throws Exception;
    public void AddToRL( abPlaceable p, int SIndex ) throws Exception;
    public void AddToLL( abPlaceable p, int SIndex ) throws Exception;
    public abPlaceable[] GetHDCrits();
    public abPlaceable[] GetCTCrits();
    public abPlaceable[] GetRTCrits();
    public abPlaceable[] GetLTCrits();
    public abPlaceable[] GetRACrits();
    public abPlaceable[] GetLACrits();
    public abPlaceable[] GetRLCrits();
    public abPlaceable[] GetLLCrits();
    public abPlaceable[] GetCrits( int Loc );
    public int Find( abPlaceable p );
    public LocationIndex FindIndex( abPlaceable p );
    public Vector FindSplitIndex( abPlaceable p );
    public int[] FindInstances( abPlaceable p );
    public Vector FindIndexes( abPlaceable p );
    public int[] FindHeatSinks( boolean DHS, boolean Clan );
    public int[] FindJumpJets( boolean IJJ );
    public int[] FindModularArmor();
    public int[] FindExplosiveInstances();
    public void FlushIllegal( int Era, int Year, boolean Restrict );
    public boolean UnallocateAll( abPlaceable p, boolean override );
    public void Remove( abPlaceable p );
    public void UnallocateByIndex( int SIndex, abPlaceable[] a );
    public void AutoAllocate( abPlaceable p );
    public void SplitAllocate( abPlaceable p, int FirstLoc, int FirstIndex, int SecondLoc ) throws Exception;
    public int FreeFrom( abPlaceable [] Loc, int index );
    public void Compact();
    public boolean IsAllocated( abPlaceable p );
    public int UnplacedCrits();
    public int FreeCrits();
    public int FreeCrits( abPlaceable[] Loc );
    public int FirstFree( abPlaceable[] Loc );
    public void LockChassis();
    public ifLoadout Clone();
    public void SetBaseLoadout( ifLoadout l );
    public ifLoadout GetBaseLoadout();
    public void SetHDCrits( abPlaceable[] c );
    public void SetCTCrits( abPlaceable[] c );
    public void SetLTCrits( abPlaceable[] c );
    public void SetRTCrits( abPlaceable[] c );
    public void SetLACrits( abPlaceable[] c );
    public void SetRACrits( abPlaceable[] c );
    public void SetLLCrits( abPlaceable[] c );
    public void SetRLCrits( abPlaceable[] c );
    public void SetNonCore( Vector v );
    public void SetTCList( Vector v );
    public void SetEquipment( Vector v );
    public boolean AddCTCASE();
    public boolean AddLTCASE();
    public boolean AddRTCASE();
    public void SetCTCASE( ISCASE c );
    public void SetLTCASE( ISCASE c );
    public void SetRTCASE( ISCASE c );
    public void RemoveCTCASE();
    public void RemoveLTCASE();
    public void RemoveRTCASE();
    public boolean HasCTCASE();
    public boolean HasLTCASE();
    public boolean HasRTCASE();
    public ISCASE GetCTCase();
    public ISCASE GetRTCase();
    public ISCASE GetLTCase();
    public void SetA4FCSSRM( boolean b ) throws Exception;
    public void SetA4FCSLRM( boolean b ) throws Exception;
    public void SetA4FCSMML( boolean b ) throws Exception;
    public boolean UsingA4SRM();
    public boolean UsingA4LRM();
    public boolean UsingA4MML();
    public boolean UsingTC();
    public TargetingComputer GetTC();
    public void UseTC( boolean use );
    public void CheckTC();
    public void UnallocateTC();
    public void SetSupercharger( boolean b, int Loc ) throws Exception;
    public boolean HasSupercharger();
    public Supercharger GetSupercharger();
    public PowerAmplifier GetPowerAmplifier();
    public void CheckExclusions( abPlaceable a ) throws Exception;
    public void AddMechModifier( MechModifier m );
    public void RemoveMechMod( MechModifier m );
    public Vector GetMechMods();
}

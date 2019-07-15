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

import java.util.Vector;

public class EquipmentCollection {
    // provides a class for consolidating multiple pieces of equipment in a JList

    private Vector equips = new Vector( 10, 5 );
    private ifMechLoadout Owner;

    public EquipmentCollection( ifMechLoadout l ) {
        Owner = l;
    }

    public abPlaceable GetType() {
        return (abPlaceable) equips.get( 0 );
    }

    public boolean SameType( abPlaceable p ) {
        if( ((abPlaceable) equips.get( 0 )).LookupName().equals( p.LookupName() ) ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean Add( abPlaceable p ) {
        // ensure that the lookupnames match.
        if( equips.size() < 1 ) {
            equips.add( p );
            return true;
        } else {
            if( SameType( p ) ) {
                if( ! equips.contains( p ) ) {
                    equips.add( p );
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public void Remove( abPlaceable p ) {
        equips.remove( p );
    }

    public boolean Contains( abPlaceable p ) {
        return equips.contains( p );
    }

    public int GetSize() {
        return equips.size();
    }

    public boolean IsEmpty() {
        if( equips.size() < 1 ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if( equips.size() < 1 ) {
            return "EquipmentCollection - 0 elements";
        } else {
            if( Owner.GetTechBase() == AvailableCode.TECH_BOTH ) {
                if( equips.get( 0 ) instanceof Equipment ) {
                    if( ((Equipment) equips.get( 0 )).IsVariableSize() ) {
                        return "(" + equips.size() + ") " + ((abPlaceable) equips.get( 0 )).CritName();
                    } else {
                        return "(" + equips.size() + ") " + ((abPlaceable) equips.get( 0 )).LookupName();
                    }
                } else {
                    return "(" + equips.size() + ") " + ((abPlaceable) equips.get( 0 )).LookupName();
                }
            } else {
                return "(" + equips.size() + ") " + ((abPlaceable) equips.get( 0 )).CritName();
            }
        }
    }
}

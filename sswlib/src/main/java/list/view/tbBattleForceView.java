/*
Copyright (c) 2008, George Blouin Jr. (skyhigh@solaris7.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.
    * Neither the name of George Blouin Jr nor the names of contributors may be
used to endorse or promote products derived from this software without specific prior
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package list.view;

import javax.swing.SortOrder;
import list.UnitList;
import list.UnitListData;

public class tbBattleForceView extends abView {
    public tbBattleForceView( UnitList list ) {
        this.list = list;

        Columns.add(new Column( 0, "Unit", "TypeModel", 200 ));
        Columns.add(new Column( 1, "MV", "MV", 20, Integer.class ));
        Columns.add(new Column( 2, "S", "S", 20, Integer.class ));
        Columns.add(new Column( 3, "M", "M", 20, Integer.class ));
        Columns.add(new Column( 4, "L", "L", 20, Integer.class ));
        Columns.add(new Column( 5, "E", "E", 20, Integer.class ));
        Columns.add(new Column( 6, "Wt", "Wt", 20, Integer.class ));
        Columns.add(new Column( 7, "OV", "OV", 20, Integer.class ));
        Columns.add(new Column( 8, "Arm", "Armor", 20, Integer.class ));
        Columns.add(new Column( 9, "Str", "Structure", 20, Integer.class ));
        Columns.add(new Column( 10, "Base PV", "BaseBV", 30, Integer.class ));
        Columns.add(new Column( 11, "Adj PV", "TotalBV", false, 40, Integer.class, true, SortOrder.ASCENDING ));
    
        Columns.get(0).sortOrder = SortOrder.ASCENDING;
        Columns.get(6).sortOrder = SortOrder.ASCENDING;

        SortFields.add(Columns.get(6));
        SortFields.add(Columns.get(0));
    }

    public Object getValueAt( int row, int col ) {
        UnitListData m = (UnitListData) list.Get( row );
        switch( col ) {
            case 0:
                return m.getBattleForceStats().getElement();
            case 1:
                return m.getBattleForceStats().getMovement();
            case 2:
                return m.getBattleForceStats().getCombinedShort();
            case 3:
                return m.getBattleForceStats().getCombinedMedium();
            case 4:
                return m.getBattleForceStats().getCombinedLong();
            case 5:
                return m.getBattleForceStats().getCombinedExtreme();
            case 6:
                return m.getBattleForceStats().getWeight();
            case 7:
                return m.getBattleForceStats().getOverheat();
            case 8:
                return m.getBattleForceStats().getArmor();
            case 9:
                return m.getBattleForceStats().getInternal();
            case 10:
                return m.getBattleForceStats().getBasePV();
            case 11:
                return m.getBattleForceStats().getPointValue();
        }
        return null;
    }
}

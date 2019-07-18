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

package Force.View;

import Force.*;
import common.*;

import javax.swing.SortOrder;
import list.view.Column;

public class tbTotalWarfareSmall extends abTable {
    public tbTotalWarfareSmall() {
        Columns.add(new Column( 0, "Unit", "TypeModel", 150 ));
        Columns.add(new Column( 1, "Type", "UnitType", 50 ));
        Columns.add(new Column( 2, "Lance/Star", "Group", true, 0, String.class, true, SortOrder.ASCENDING));
        Columns.add(new Column( 3, "Tons", "Tonnage", false, 40, Integer.class, true, SortOrder.ASCENDING ));
        Columns.add(new Column( 4, "Base BV", "BaseBV", 20, Integer.class ));
        Columns.add(new Column( 5, "G", "Gunnery", 20, true, Integer.class ));
        Columns.add(new Column( 6, "P", "Piloting", 20, true, Integer.class ));
        Columns.add(new Column( 7, "Adj BV", "TotalBV", false, 50, Integer.class, true, SortOrder.ASCENDING ));
    }

    public tbTotalWarfareSmall( Force f ) {
        this();
        force = f;
    }

    public void setForce( Force f ) {
        this.force = f;
    }

    public tbTotalWarfareSmall Create() {
        return new tbTotalWarfareSmall();
    }

    public tbTotalWarfareSmall Create( Force f ) {
        return new tbTotalWarfareSmall(f);
    }

    public Object getValueAt( int row, int col ) {
        Unit u = force.getUnits().get( row );
        switch( col ) {
            case 0:
                return u.TypeModel;
            case 1:
                return CommonTools.UnitTypes[u.UnitType];
            case 2:
                return u.getGroup();
            case 3:
                return u.Tonnage;
            case 4:
                return String.format( "%1$,.0f", u.BaseBV );
            case 5:
                return u.getGunnery();
            case 6:
                return u.getPiloting();
            case 7:
                return String.format( "%1$,.0f", u.TotalBV );
        }
        return null;
    }

    @Override
    public void setValueAt( Object value, int row, int col ) {
        Unit u = force.getUnits().get( row );
        switch( col ) {
            case 2:
                u.setGroup(value.toString());
                force.GroupUnit(u);
                break;
            case 5:
                u.setGunnery(Integer.parseInt(value.toString()));
                break;
            case 6:
                u.setPiloting(Integer.parseInt(value.toString()));
                break;
        }
        force.isDirty = true;
        u.Refresh();
        force.RefreshBV();
    }
}

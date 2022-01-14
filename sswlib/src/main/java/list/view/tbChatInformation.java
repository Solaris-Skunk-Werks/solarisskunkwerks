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

public class tbChatInformation extends abView {
    public tbChatInformation() {
        Columns.add(new Column( 0, "Type/Model", "Name", false, 125, String.class, true, SortOrder.ASCENDING ));
        Columns.add(new Column( 1, "Information", "ChatInfo", false, 325, String.class, true, SortOrder.ASCENDING ));
        Columns.add(new Column( 2, "BV", "BV", false, 20, Integer.class, true, SortOrder.ASCENDING ));
        Column cost = new Column( 3, "Cost", "Cost", false, 20, Integer.class, true, SortOrder.ASCENDING );
        cost.SetRenderer(new NumberRenderer());
        Columns.add(cost);
        Columns.add(new Column( 4, "Level", "Level", 40 ));
        Columns.add(new Column( 5, "Era", "Era", 20 ));
        Columns.add(new Column( 6, "Tech", "Tech", 20 ));

        SortFields.add(Columns.get(0));
        SortFields.add(Columns.get(2));
        SortFields.add(Columns.get(3));
    }

    public Object getValueAt( int row, int col ) {
        UnitListData m = list.Get( row );
        switch( col ) {
            case 0:
                return m.getFullName();
            case 1:
                return m.getInfo();
            case 2:
                return m.getBV();
            case 3:
                return String.format("%10.0f", m.getCost());
            case 4:
                return m.getLevel();
            case 5:
                return m.getEra();
            case 6:
                return m.getTech();
            default:
                return null;
        }
    }
}

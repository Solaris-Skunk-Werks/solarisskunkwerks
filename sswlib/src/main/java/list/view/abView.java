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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import list.UnitList;

public abstract class abView extends AbstractTableModel {
    public Vector<Column> Columns = new Vector<Column>();
    public Vector<Column> SortFields = new Vector<Column>();
    public UnitList list;

    public void setupTable( JTable tbl ) {
        tbl.setModel(this);
        //Create a sorting class and apply it to the list
        TableRowSorter Leftsorter = new TableRowSorter<abView>(this);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();

        if ( SortFields.size() > 0 ) {
            for ( Column col : SortFields ) {
                sortKeys.add(new RowSorter.SortKey(col.Index, col.sortOrder));
            }
        } else {
            for ( Column col : Columns ) {
                if ( col.isSortable ) {
                    sortKeys.add(new RowSorter.SortKey(col.Index, col.sortOrder));
                }
            }
        }
        Leftsorter.setSortKeys(sortKeys);
        tbl.setRowSorter(Leftsorter);

        for ( Column col : Columns ) {
            if ( col.preferredWidth > 0 && list.Size() > 0 ) {
                tbl.getColumnModel().getColumn(col.Index).setPreferredWidth(col.preferredWidth);
            }
            if ( col.Renderer != null ) {
                tbl.getColumnModel().getColumn(col.Index).setCellRenderer(col.Renderer);
            }
        }
    }
    
    public Object Get( int index ) {
        return list.Get(index);
    }

    @Override
    public String getColumnName( int col ) { return Columns.get(col).Title; }
    public int getColumnCount() { return Columns.size(); }
    public int getRowCount() { return list.Size(); }
    @Override
    public Class getColumnClass(int c) { return Columns.get(c).classType; }
    public Object getClassOf( int row, int col ) { return Columns.get(col).classType; }
    @Override
    public boolean isCellEditable( int row, int col ) { return Columns.get(col).isEditable; }
}

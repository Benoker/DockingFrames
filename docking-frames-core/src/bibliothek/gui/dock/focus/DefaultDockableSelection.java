/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.focus;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bibliothek.gui.Dockable;

/**
 * A {@link DockableSelection} that uses a {@link JList} to display all the
 * available {@link Dockable}s. Subclasses can rearrange the layout of this
 * selection using {@link Container#removeAll()} and later {@link #getList()}
 * to get access to the component which represents the list.
 * @author Benjamin Sigg
 */
public class DefaultDockableSelection extends AbstractDockableSelection{
    private Model model = new Model();
    private JScrollPane listPane;
    private JList list;
    
    /**
     * Creates a new selection
     */
    public DefaultDockableSelection(){
        list = new JList( model );
        list.setCellRenderer( new Renderer() );
        
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        list.addListSelectionListener( new ListSelectionListener(){
            public void valueChanged( ListSelectionEvent e ) {
                setSelection( (Dockable)list.getSelectedValue() );
            }
        });
        
        setLayout( new BorderLayout() );
        listPane = new JScrollPane( list );
        add( listPane, BorderLayout.CENTER );
        
        list.addMouseListener( new MouseAdapter(){
            @Override
            public void mouseClicked( MouseEvent e ) {
                select();
            }
        });
        
        list.addMouseMotionListener( new MouseMotionAdapter(){ 
            @Override
            public void mouseMoved( MouseEvent e ) {
                int current = list.getSelectedIndex();
                int index = list.locationToIndex( e.getPoint() );
                
                if( index != current && index >= 0 && index < model.getSize() ){
                    list.setSelectedIndex( index );
                }
            }
        });
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        if( list != null )
            list.setCellRenderer( new Renderer() );
    }
    
    /**
     * Gets the component which represents the list.
     * @return the list
     */
    protected JComponent getList(){
        return listPane;
    }
    
    @Override
    protected void up() {
        int index = list.getSelectedIndex();
        if( index == -1 ){
            index = 0;
        }
        else{
            index--;
            if( index < 0 )
                index = model.getSize()-1;
        }
        list.setSelectedIndex( index );
        list.ensureIndexIsVisible( index );
    }

    @Override
    protected void right() {
        down();
    }

    
    @Override
    protected void down() {
        int index = list.getSelectedIndex();
        index++;
        if( index >= model.getSize() )
            index = 0;
        list.setSelectedIndex( index );
        list.ensureIndexIsVisible( index );
    }

    @Override
    protected void left() {
        up();
    }


    @Override
    protected void iconChanged( int index, Dockable dockable ) {
        model.change( index );
    }

    @Override
    protected void insert( int index, Dockable dockable ) {
        model.insertElementAt( dockable, index );
    }


    @Override
    protected void remove( int index, Dockable dockable ) {
        model.remove( index );
    }

    @Override
    protected void select( Dockable dockable ) {
        list.setSelectedValue( dockable, true );
        int index = list.getSelectedIndex();
        if( index >= 0 )
            list.ensureIndexIsVisible( index );
    }
    
    @Override
    protected void titleChanged( int index, Dockable dockable ) {
        model.change( index );
    }

    
    /**
     * The model used to collect {@link Dockable}s.
     * @author Benjamin Sigg
     */
    private static class Model extends DefaultListModel{
        public void change( int index ){
            fireContentsChanged( this, index, index );
        }
    }
    
    /**
     * The renderer which draws {@link Dockable}s.
     * @author Benjamin Sigg
     */
    private static class Renderer extends DefaultListCellRenderer{
        @Override
        public Component getListCellRendererComponent( JList list,
                Object value, int index, boolean isSelected,
                boolean cellHasFocus ) {

            if( value instanceof Dockable ){
                Dockable dockable = (Dockable)value;
                
                super.getListCellRendererComponent( list, "", index, isSelected, cellHasFocus );
                
                setText( dockable.getTitleText() );
                setIcon( dockable.getTitleIcon() );
                
                return this;
            }
            
            return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
        }
    }
}

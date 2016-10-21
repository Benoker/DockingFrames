/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableListener;

/**
 * This {@link DockableSelection} is also a {@link JPanel}. It implements the
 * methods needed to interact with {@link DockableSelectionListener}. It uses 
 * the {@link #getInputMap() input map} to register actions for when an arrow 
 * key or a controlling key is pressed. This selection also observes the set
 * of available {@link Dockable}s and their title-text/icon. Subclasses get
 * automatically informed about changes.
 * @author Benjamin Sigg
 */
public abstract class AbstractDockableSelection extends JPanel implements DockableSelection{
    private List<DockableSelectionListener> listeners = new ArrayList<DockableSelectionListener>();

    /**
     * Action called when the up arrow key was pressed.
     */
    protected final Action UP = new AbstractAction(){
        public void actionPerformed( ActionEvent e ) {
            up();
        }
    };
    
    /**
     * Action called when the down arrow key was pressed.
     */
    protected final Action DOWN = new AbstractAction(){
        public void actionPerformed( ActionEvent e ) {
            down();
        }
    };
    
    /**
     * Action called when the left arrow key was pressed.
     */
    protected final Action LEFT = new AbstractAction(){
        public void actionPerformed( ActionEvent e ) {
            left();
        }
    };
    
    /**
     * Action called when the right arrow key was pressed.
     */
    protected final Action RIGHT = new AbstractAction(){
        public void actionPerformed( ActionEvent e ) {
            right();
        }
    };
    
    /**
     * Action called when the escape or return key was pressed.
     */
    protected final Action CANCEL = new AbstractAction(){
        public void actionPerformed( ActionEvent e ) {
            cancel();
        }
    };
    
    /**
     * Action called when the space or enter key was pressed.
     */
    protected final Action SELECT = new AbstractAction(){
        public void actionPerformed( ActionEvent e ) {
            select();
        }
    };
    
    /** the controller this selection currently works for */
    private DockController controller;
    
    /** the current selection */
    private Dockable selection;
    
    /** the current list of selectable dockables */
    private List<Dockable> dockables = new LinkedList<Dockable>();
    
    /** a listener informing this selection when icon or title of a {@link Dockable} changes */
    private DockableListener dockableListener = new DockableAdapter(){
        @Override
        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            int index = dockables.indexOf( dockable );
            titleChanged( index, dockable );
        }
        @Override
        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            int index = dockables.indexOf( dockable );
            iconChanged( index, dockable );
        }
    };
    
    /** a listener to {@link #controller} informing when dockables are added or removed */
    private DockRegisterListener registerListener = new DockRegisterAdapter(){
        @Override
        public void dockableRegistered( DockController controller, Dockable dockable ) {
            if( selectable( dockable )){
                add( dockable );
            }
        }
        @Override
        public void dockableUnregistered( DockController controller, Dockable dockable ) {
            remove( dockable );
        }
    };
    
    /**
     * Creates a new selection
     */
    public AbstractDockableSelection(){
        setFocusable( true );
        
        InputMap input = getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_UP, 0 ), "up" );
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, 0 ), "down" );
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, 0 ), "left" );
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, 0 ), "right" );
        
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_KP_UP, 0 ), "up" );
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_KP_DOWN, 0 ), "down" );
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_KP_LEFT, 0 ), "left" );
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_KP_RIGHT, 0 ), "right" );
        
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "cancel" );
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_BACK_SPACE, 0 ), "cancel" );
        
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "select" );
        input.put( KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0 ), "select" );
        
        ActionMap action = getActionMap();
        action.put( "up", UP );
        action.put( "down", DOWN );
        action.put( "left", LEFT );
        action.put( "right", RIGHT );
        action.put( "cancel", CANCEL );
        action.put( "select", SELECT );
    }
    
    public void addDockableSelectionListener( DockableSelectionListener listener ) {
        listeners.add( listener );
    }

    public void removeDockableSelectionListener( DockableSelectionListener listener ) {
        listeners.remove( listener );
    }
    
    public boolean hasChoices( DockController controller ) {
        DockRegister register = controller.getRegister();
        int count = 0;
        
        for( int i = 0, n = register.getDockableCount(); i<n; i++ ){
            if( selectable( register.getDockable( i ) )){
                count++;
                
                if( count >= 2 )
                    return true;
            }
        }
        
        return false;
    }
    
    public Component getComponent() {
        return this;
    }
    
    public void open( DockController controller ) {
        if( this.controller != null )
            throw new IllegalStateException( "selection already open" );
        
        DockRegister register = controller.getRegister();
        for( int i = 0, n = register.getDockableCount(); i<n; i++ ){
            Dockable dockable = register.getDockable( i );
            if( selectable( dockable )){
                add( dockable );
            }
        }
        
        this.controller = controller;
        register.addDockRegisterListener( registerListener );
        
        Dockable focus = controller.getFocusedDockable();
        if( focus != null && !selectable( focus ))
            focus = null;
        
        select( focus );
    }
    
    private void add( Dockable dockable ){
        dockables.add( dockable );
        dockable.addDockableListener( dockableListener );
        insert( dockables.size()-1, dockable );
    }
    
    public void close() {
        if( controller != null ){
            controller.getRegister().removeDockRegisterListener( registerListener );
            controller = null;
        }
        
        for( int i = dockables.size()-1; i >= 0; i-- ){
            Dockable dockable = dockables.remove( i );
            dockable.removeDockableListener( dockableListener );
            remove( i, dockable );
        }
        
        selection = null;
    }
    
    private void remove( Dockable dockable ){
        int index = dockables.indexOf( dockable );
        if( index >= 0 ){
             dockables.remove( index );
            dockable.removeDockableListener( dockableListener );
            remove( index, dockable );
        }
    }
    
    /**
     * Gets the currently used controller.
     * @return the controller for which this selection shows content
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Decides whether <code>dockable</code> fits the requirements for
     * an element that can be selected.
     * @param dockable the element to check
     * @return <code>true</code> if <code>dockable</code> should be shown
     * on this selection
     */
    protected boolean selectable( Dockable dockable ){
        return dockable.asDockStation() == null && dockable.getDockParent() != null;
    }
    
    /**
     * Called when an up arrow key was pressed.
     */
    protected abstract void up();
    
    /**
     * Called when a down arrow key was pressed.
     */
    protected abstract void down();
    
    /**
     * Called when a left arrow key was pressed.
     */
    protected abstract void left();
    
    /**
     * Called when a right arrow key was pressed.
     */
    protected abstract void right();
    
    /**
     * Called when a new dockable can be selected.
     * @param index the location of the dockable in the list of all known dockables.
     * @param dockable the new element
     */
    protected abstract void insert( int index, Dockable dockable );
    
    /**
     * Called when a dockable is no longer selectable.
     * @param index the index of the removed element
     * @param dockable the removed element
     */
    protected abstract void remove( int index, Dockable dockable );
    
    /**
     * Called when the title text of <code>dockable</code> changed.
     * @param index the location of <code>dockable</code>
     * @param dockable the element whose title changed
     */
    protected abstract void titleChanged( int index, Dockable dockable );
    
    /**
     * Called when the icon of <code>dockable</code> changed.
     * @param index the location of <code>dockable</code>
     * @param dockable the element whose icon changed
     */
    protected abstract void iconChanged( int index, Dockable dockable );
    
    /**
     * Called when this selection is forced to select a specific dockable.
     * @param dockable the element to select, can be <code>null</code>
     */
    protected abstract void select( Dockable dockable );
    
    /**
     * Informs this selection which dockable is currently selected.
     * @param dockable the currently selected dockable
     */
    protected void setSelection( Dockable dockable ){
        selection = dockable;
        for( DockableSelectionListener listener : listeners.toArray( new DockableSelectionListener[ listeners.size() ] ))
            listener.considering( dockable );
    }
    
    /**
     * Cancels this selection
     */
    protected void cancel(){
        for( DockableSelectionListener listener : listeners.toArray( new DockableSelectionListener[ listeners.size() ] ))
            listener.canceled();
    }
    
    /**
     * Selects the currently selected dockable.
     * @see #setSelection(Dockable)
     */
    protected void select(){
        if( selection != null ){
            Dockable dockable = this.selection;
        
            for( DockableSelectionListener listener : listeners.toArray( new DockableSelectionListener[ listeners.size() ] ))
                listener.selected( dockable );
        }
    }
}

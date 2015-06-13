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
package bibliothek.gui.dock.control;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.focus.DefaultFocusRequest;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.focus.DockableSelectionListener;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * A {@link DockableSelector} is able to open a popup when the user hits a special
 * combination of keys. The popup uses a {@link DockableSelection} to present
 * the user different {@link Dockable}s from which he can choose one to become focused.
 * @author Benjamin Sigg
 */
public class DockableSelector {
    /** key for the {@link DockProperties}, telling which {@link KeyStroke} activates the selection */
    public static final PropertyKey<KeyStroke> INIT_SELECTION = 
        new PropertyKey<KeyStroke>( "dockable selector init selection keystroke" );
    
    /** the currently active keystroke */
    private PropertyValue<KeyStroke> initSelection = new PropertyValue<KeyStroke>( INIT_SELECTION ){
        @Override
        protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
             // ignore   
        }
    };
    
    /** listens to keyboard and to {@link DockableSelection} */
    private Listener listener = new Listener();
    
    /** The currently observed controller */
    private DockController controller;
    
    /** the selection which is currently used */
    private DockableSelection selection;
    
    /** the dialog which shows the selection */
    private JDialog dialog;
    
    /**
     * Sets the controller which should be observed.
     * @param controller the currently observed controller
     */
    public void setController( DockController controller ){
        cancel();
        
        if( this.controller != null ){
            this.controller.getKeyboardController().removeGlobalListener( listener );
        }
        
        this.controller = controller;
        initSelection.setProperties( controller );
        
        if( this.controller != null ){
            this.controller.getKeyboardController().addGlobalListener( listener );
        }
    }
    
    /**
     * Opens the popup window if possible and allows the user the choice between different 
     * {@link Dockable}s.
     */
    public void select(){
    	if( !isOpen() ){
	        cancel();
	        open();
    	}
    }
    
    /**
     * Tells whether the window is shown to the user right now.
     * @return whether the window is visible
     */
    public boolean isOpen(){
    	return selection != null && dialog != null && dialog.isVisible();
    }
    
    /**
     * Closes the currently open popup window without changing the focused {@link Dockable}
     */
    public void cancel(){
        close();
    }
    
    /**
     * Close the currently open popup window and changes the focused
     * {@link Dockable} to <code>dockable</code>.
     * @param dockable the element that will own the focus
     */
    public void stop( Dockable dockable ){
        close();
        controller.setFocusedDockable( new DefaultFocusRequest( dockable, null, false ));
    }
    
    private void open(){
        selection = controller.getProperties().get( DockTheme.DOCKABLE_SELECTION );
        if( selection != null && selection.hasChoices( controller )){
            Window root = controller.findRootWindow();
            if( dialog == null || dialog.getOwner() != root ){
                if( dialog != null )
                    dialog.dispose();
                
                if( root instanceof Frame )
                    dialog = new JDialog( (Frame)root, false );
                else if( root instanceof Dialog )
                    dialog = new JDialog( (Dialog)root, false );
                else{
                    dialog = new JDialog();
                    dialog.setModal( false );
                }
                
                dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
                dialog.setUndecorated( true );
                dialog.getRootPane().setWindowDecorationStyle( JRootPane.NONE );
                dialog.setLayout( new BorderLayout() );
            }
            
            dialog.addWindowFocusListener( listener );
            selection.open( controller );
            final Component base = selection.getComponent();
            dialog.add( base, BorderLayout.CENTER );
            
            dialog.pack();
            dialog.setLocationRelativeTo( root );
            
            selection.addDockableSelectionListener( listener );
            
            base.requestFocusInWindow();
            dialog.setVisible( true );
        }
        else
            selection = null;
    }
    
    private void close(){
        if( dialog != null ){
            if( selection != null ){
                dialog.removeWindowFocusListener( listener );
                selection.removeDockableSelectionListener( listener );
                selection.close();
                selection = null;
            }
            
            dialog.setVisible( false );
            dialog.getContentPane().removeAll();
        }   
    }
    
    /**
     * A listener that triggers the {@link DockableSelector} and that observes
     * the active {@link DockableSelection}.
     * @author Benjamin Sigg
     */
    private class Listener implements KeyListener, DockableSelectionListener, WindowFocusListener{
        public void keyPressed( KeyEvent event ) {
            if( event.isConsumed() )
                return;
            
            if( KeyStroke.getKeyStrokeForEvent( event ).equals( initSelection.getValue() )){
                select();
                event.consume();
            }
        }
        
        public void keyReleased( KeyEvent event ) {
            if( event.isConsumed() )
                return;
            
            if( KeyStroke.getKeyStrokeForEvent( event ).equals( initSelection.getValue() )){
                select();
                event.consume();
            }
        }

        public void keyTyped( KeyEvent event ) {
            if( event.isConsumed() )
                return;
            
            if( KeyStroke.getKeyStrokeForEvent( event ).equals( initSelection.getValue() )){
                select();
                event.consume();
            }
        }
        
        public void windowGainedFocus( WindowEvent e ) {
            // ignore
        }
        
        public void windowLostFocus( WindowEvent e ) {
            if( selection != null )
                cancel();
        }

        public void canceled() {
            cancel();
        }

        public void considering( Dockable dockable ) {
            // ignore
        }

        public void selected( Dockable dockable ) {
            stop( dockable );
        }
        
    }
}

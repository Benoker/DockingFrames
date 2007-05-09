/**
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
package bibliothek.gui.dock.action.actions;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import bibliothek.gui.DockUI;
import bibliothek.gui.DockUtilities;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.util.container.Tuple;

/**
 * This {@link DockAction} can change the {@link Dockable#getTitleText() title-text}
 * of a {@link Dockable}. When this action is triggered, a popupmenu will appear,
 * where the user can enter the new title of the {@link Dockable}.
 * @author Benjamin Sigg
 */
public abstract class RenameAction extends SimpleButtonAction {
	/** button that is pressed if the new name should be applied */
    private JButton okButton = new JButton();
    /** button to cancel the event */
    private JButton cancelButton = new JButton();
    /** the field containing the new title */
    private JTextField titleField = new JTextField();
    /** the menu on which the items will be shown */
    private JPopupMenu menu = new JPopupMenu();
    
    /** the dockable whose title is currently changed */
    private Dockable current;
    
    /**
     * A set of RenameActions for known types.
     */
    private static final List<Tuple<Class<?>, RenameAction>> SPECIAL = 
    	new ArrayList<Tuple<Class<?>, RenameAction>>();
    
    static{
    	SPECIAL.add( new Tuple<Class<?>, RenameAction>( DefaultDockable.class, new RenameDefaultDockable() ));
    	SPECIAL.add( new Tuple<Class<?>, RenameAction>( SplitDockStation.class, new RenameSplitDockStation() ));
    	SPECIAL.add( new Tuple<Class<?>, RenameAction>( StackDockStation.class, new RenameStackDockStation() ));
    	SPECIAL.add( new Tuple<Class<?>, RenameAction>( FlapDockStation.class, new RenameFlapDockStation() ));
    }
    
    /**
     * Constructs a new action
     */
    public RenameAction(){
        Icon icon = createIcon();
        setIcon( icon );
        setText( DockUI.getDefaultDockUI().getString( "rename" ) );
        setTooltipText( DockUI.getDefaultDockUI().getString( "rename.tooltip" ) );
        
        menu.setLayout( new GridBagLayout() );
        JPanel panel = new JPanel( new GridLayout( 1, 2 ));
        panel.add( okButton );
        panel.add( cancelButton );
        menu.add( titleField, new GridBagConstraints( 0, 0, 1, 1, 100.0, 1.0, 
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
                new Insets( 1, 1, 1, 1 ), 0, 0 ));
        menu.add( panel, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, 
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 
                new Insets( 1, 1, 1, 1 ), 0, 0 ));
        
        titleField.setColumns( 10 );
        
        okButton.setText( DockUI.getDefaultDockUI().getString( "rename.ok" ) );
        cancelButton.setText( DockUI.getDefaultDockUI().getString( "rename.cancel" ) );
        
        menu.addPopupMenuListener( new PopupMenuListener(){
            public void popupMenuCanceled( PopupMenuEvent e ) {
                current = null;
            }
            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
            	// do nothing
            }
            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
            	// do nothing
            }
        });
        
        okButton.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ){
                rename();
            }
        });
        
        cancelButton.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ){
                menu.setVisible( false );
            }
        });
        
        titleField.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ){
                rename();
            }
        });
    }
    
    @Override
    public void action( Dockable dockable ) {
        titleField.setText( "" );
        menu.setSize( menu.getPreferredSize() );
        titleField.setText( dockable.getTitleText() );
        
        Component component = DockUtilities.getShowingComponent( dockable );
        
        if( component != null ){
            current = dockable;
            menu.show( component, 0, 0 );
            titleField.requestFocus();
        }   
    }
    
    /**
     * Changes the name of the current Dockable to the text of
     * the {@link #titleField}.
     */
    private void rename(){
        rename( current, titleField.getText() );
        menu.setVisible( false );
    }
    
    /**
     * Creates an icon that is shown for this action. This method
     * is invoked directly by the constructor.
     * @return The new icon
     */
    protected Icon createIcon(){
        return DockUI.getDefaultDockUI().getIcon( "rename" );
    }
    
    /**
     * Invoked when the action was triggered, and the user tipped in
     * the new title for <code>dockable</code>.
     * @param dockable The {@link Dockable} whose title should be changed
     * @param text The new title
     */
    protected abstract void rename( Dockable dockable, String text );

    /**
     * Searches for a subtype of {@link RenameAction} who can handle
     * the <code>dockable</code>.
     * @param dockable The {@link Dockable} for whom an action is searched
     * @return The instance of a RenameAction, or <code>null</code> if no
     * fitting sub type was found.
     */
    public static RenameAction actionFor( Dockable dockable ){
    	Class<?> clazz = dockable.getClass();
    	
    	for( Tuple<Class<?>, RenameAction> action : SPECIAL ){
    		if( action.getA().isAssignableFrom( clazz ))
    			return action.getB();
    	}
    	
    	return null;
    }
    
    /**
     * An implementation of {@link RenameAction} that can handle
     * {@link StackDockStation StackDockStations}.
     * @author Benjamin Sigg
     */
    public static class RenameStackDockStation extends RenameAction{
        @Override
        protected void rename( Dockable dock, String text ){ 
            ((StackDockStation)dock).setTitleText( text );
        }
    }
    
    /**
     * An implementation of {@link RenameAction} that can handle
     * {@link SplitDockStation SplitDockStations}.
     * @author Benjamin Sigg
     */
    public static class RenameSplitDockStation extends RenameAction{
        @Override
        protected void rename( Dockable dock, String text ){
            ((SplitDockStation)dock).setTitleText( text );
        }
    }

    /**
     * An implementation of {@link RenameAction} that can handle
     * {@link FlapDockStation FlapDockStations}.
     * @author Benjamin Sigg
     */
    public static class RenameFlapDockStation extends RenameAction{
        @Override
        protected void rename( Dockable dock, String text ){
            ((FlapDockStation)dock).setTitleText( text );
        }
    }
    
    /**
     * An implementation of {@link RenameAction} that can handle
     * {@link DefaultDockable DefaultDockables}.
     * @author Benjamin Sigg
     */
    public static class RenameDefaultDockable extends RenameAction{
        @Override
        protected void rename( Dockable dock, String text ){
            ((DefaultDockable)dock).setTitleText( text );
        }
    }
}

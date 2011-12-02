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
package bibliothek.gui.dock.facile.menu;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;

import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelList;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelListener;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelList.Info;
import bibliothek.gui.dock.support.menu.BaseMenuPiece;
import bibliothek.util.ClientOnly;

/**
 * A menu that contains an item for each available {@link LookAndFeel}. The
 * set of <code>LookAndFeel</code>s is determined through a {@link LookAndFeelList}.
 * @author Benjamin Sigg
 */
@ClientOnly
public class LookAndFeelMenuPiece extends BaseMenuPiece{
    /** the item for the default-<code>LookAndFeel</code> */
    private JRadioButtonMenuItem defaultButton;
    /** the item for the systems imitating <code>LookAndFeel</code> */
    private JRadioButtonMenuItem systemButton;
    /** a list of buttons, one for each {@link LookAndFeel} */
    private Map<Info, JRadioButtonMenuItem> buttons = new HashMap<Info, JRadioButtonMenuItem>();
    
    /** the list of available <code>LookAndFeel</code>s */
    private LookAndFeelList list;
    /** a listener to {@link #list} */
    private ListListener listListener = new ListListener();
    /** whether the <code>LookAndFeel</code> is currently changing or not */
    private boolean onChange = false;
    
    /** a collector monitoring the {@link JFrame} that is given to the constructor of this menu */
    private ComponentCollector frameCollector;
    
    /**
     * Creates a new menu.
     */
    public LookAndFeelMenuPiece(){
    	this( null, null );
    }

    /**
     * Creates a new menu.
     * @param frame the main frame of the application. This piece will add
     * a listener to <code>frame</code> and free resources the moment
     * <code>frame</code> is closed. This menu will also ensure that <code>frame</code>s
     * user interface is updated when the {@link javax.swing.LookAndFeel} changes.
     * Can be <code>null</code>.
     * @param list the set of available {@link LookAndFeel}s. Can be <code>null</code>.
     */
    public LookAndFeelMenuPiece( final JFrame frame, LookAndFeelList list ){
        if( list == null )
        	list = LookAndFeelList.getDefaultList();
    	this.list = list;
        
        defaultButton = new JRadioButtonMenuItem( "Default: " + list.getDefault().getName() );
        defaultButton.addItemListener( new SetListener( defaultButton, list.getDefault() ));
        add( defaultButton );
        
        systemButton = new JRadioButtonMenuItem( list.getSystem().getName() );
        systemButton.addItemListener( new SetListener( systemButton, list.getSystem() ));
        add( systemButton );
        
        addSeparator();
        
        for( int i = 0, n = list.size(); i < n; i++ ){
            Info info = list.get( i );
            JRadioButtonMenuItem item = new JRadioButtonMenuItem( info.getName() );
            buttons.put( info, item );
            SetListener listener = new SetListener( item, info );
            item.addItemListener( listener );
            add( item );
        }
        
        if( frame != null ){
        	frameCollector = new ComponentCollector(){
        		public Collection<Component> listComponents(){
        			List<Component> result = new ArrayList<Component>();
        			result.add( frame );
        			return result;
        		}
        	};
        }
        
        changed();
    }
    
    @Override
    public void bind(){
    	if( !isBound() ){
    		super.bind();
    		install();
    	}
    }
    
    @Override
    public void unbind(){
    	if( isBound() ){
    		super.unbind();
    		uninstall();
    	}
    }
    
    private void install(){
    	if( frameCollector != null ){
    		list.addComponentCollector( frameCollector );
    	}
    	list.addLookAndFeelListener( listListener );
    	changed();
    }
    
    private void uninstall(){
    	if( frameCollector != null ){
    		list.removeComponentCollector( frameCollector );
    	}
    	list.removeLookAndFeelListener( listListener );
    }
    
    /**
     * Frees resources and cuts connections to other objects such that this
     * piece can be removed by the garbage collector.
     * @deprecated the method {@link #unbind()} is automatically called if this menu is no
     * longer visible, that method will also uninstall resources
     */
    @Deprecated
    public void destroy(){
        list.removeLookAndFeelListener( listListener );
    }
    
    /**
     * Gets the list of {@link LookAndFeel}s.
     * @return the list
     */
    public LookAndFeelList getList() {
        return list;
    }
    
    /**
     * Called when the {@link LookAndFeel} has been changed. Ensures
     * that the correct items in this menu are selected.
     */
    private void changed(){
        onChange = true;
        
        Info current = list.getLookAndFeel();
        defaultButton.setSelected( list.getDefault() == current );
        systemButton.setSelected( list.getSystem() == current );
        
        for( Map.Entry<Info, JRadioButtonMenuItem> entry : buttons.entrySet() ){
            entry.getValue().setSelected( current == entry.getKey() );
        }
        
        onChange = false;
    }
    
    /**
     * A listener to the {@link LookAndFeelList}, informing this piece
     * when the LookAndFeel changes.
     * @author Benjamin Sigg
     *
     */
    private class ListListener implements LookAndFeelListener{
        public void lookAndFeelChanged( LookAndFeelList list, Info lookAndFeel ) {
            changed();
        }
        public void defaultLookAndFeelChanged( LookAndFeelList list, Info lookAndFeel ) {
            defaultButton.setText( lookAndFeel.getName() );
        }
        public void systemLookAndFeelChanged( LookAndFeelList list, Info lookAndFeel ) {
            systemButton.setText( lookAndFeel.getName() );
        }
        public void lookAndFeelAdded( LookAndFeelList list, Info info ) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem( info.getName() );
            buttons.put( info, item );
            SetListener listener = new SetListener( item, info );
            item.addItemListener( listener );
            add( item );   
        }
        public void lookAndFeelRemoved( LookAndFeelList list, Info lookAndFeel ) {
            JRadioButtonMenuItem item = buttons.remove( lookAndFeel );
            if( item != null )
                remove( item );
        }
    }
    
    /**
     * A listener to one item of a {@link LookAndFeelMenuPiece}, this listener
     * exchanges the {@link LookAndFeel} when the observed item is clicked.
     * @author Benjamin Sigg
     *
     */
    private class SetListener implements ItemListener{
        /** the observed item */
        private JRadioButtonMenuItem item;
        /** the {@link LookAndFeel} */
        private Info info;
        
        /**
         * Creates a new listener. The listener is not added to
         * item, that's the responsibility of the client.
         * @param item the item that will be observed
         * @param info the {@link LookAndFeel} that will be used
         * when <code>item</code> is clicked
         */
        public SetListener( JRadioButtonMenuItem item, Info info ){
            this.info = info;
            this.item = item;
        }
        
        public void itemStateChanged( ItemEvent e ) {
            if( !onChange ){
                if( item.isSelected() )
                    list.setLookAndFeel( info );
                else
                    item.setSelected( true );
            }
        }
    }
}

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

package bibliothek.gui.dock.themes.basic.action.buttons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.view.ViewItem;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A panel showing the actions of a {@link bibliothek.gui.dock.action.DockActionSource} 
 * using {@link BasicTitleViewItem}. Items for actions are created as soon as
 * the DockActionSource is set.
 * @author Benjamin Sigg
 */
public class ButtonPanel extends JPanel{
	/** how to layout the panel */
	private Orientation orientation = Orientation.FREE_HORIZONTAL;
	
	/** the Dockable for which the actions are shown */
	private Dockable dockable;
	/** the list of actions to show */
	private DockActionSource source;
	
	/** a listener to {@link #source} */
	private Listener listener = new Listener();
	
	/** The list of actions which are currently known */
	private List<DockAction> actions = new ArrayList<DockAction>();
	/** The buttons of this title, each of them represents one action */
    private Map<DockAction, BasicTitleViewItem<JComponent>> items = new HashMap<DockAction, BasicTitleViewItem<JComponent>>();
	
	/**
	 * Creates a new panel
	 */
	public ButtonPanel(){
		setLayout( null );
	}
	
    /**
     * Creates a new item for <code>action</code> which will be shown on this title.
     * @param action The action which will be triggered by the button
     * @param dockable The {@link Dockable} which will be affected by the action
     * @return the new graphical representation of the action 
     */
    protected BasicTitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
    	return dockable.getController().getActionViewConverter().createView( 
    			action, ViewTarget.TITLE, dockable );
    }
	
    /**
     * Gets the number of items which are shown on this button.
     * @return the number of items
     */
    public int getItemCount(){
    	return items.size();
    }
    
    /**
     * Changes the orientation of this panel.
     * @param orientation the new orientation
     */
    public void setOrientation( Orientation orientation ){
    	if( this.orientation != orientation ){
    		this.orientation = orientation;
    		for( BasicTitleViewItem<?> item : items.values() )
    			item.setOrientation( orientation );
    		
    		revalidate();
    	}
	}
    
    /**
     * Gets the orientation of this panel. The orientation tells whether the
     * buttons have to be layed out horizontally or vertically.
     * @return the orientation
     */
    public Orientation getOrientation(){
		return orientation;
	}
    
    /**
     * Changes the content which is shown.
     * @param dockable the Dockable for which the actions are shown, can be <code>null</code>
     */
    public void set( Dockable dockable ){
    	if( dockable == null )
    		set( null, null );
    	else
    		set( dockable, dockable.getController().listOffers( dockable ));
    }
    
    /**
     * Changes the content which is shown.
     * @param dockable the Dockable for which the actions are shown, can be <code>null</code>
     * @param source the list of actions, can be <code>null</code>
     * @throws IllegalArgumentException if <code>dockable</code> or <code>source</code>
     * is <code>null</code> while the other is not <code>null</code>.
     */
    public void set( Dockable dockable, DockActionSource source ){
    	if( (source == null) != (dockable == null) )
    		throw new IllegalArgumentException( "Either both arguments are null, or none" );
    	
    	if( this.source != source || this.dockable != dockable ){
    		if( this.source != null ){
    			// remove old items
    			removeAll();
    			
    			for( Map.Entry<DockAction, BasicTitleViewItem<JComponent>> entry : items.entrySet() ){
    				entry.getValue().unbind();
    				entry.getKey().unbind( this.dockable );
    			}
    			
    			
    			actions.clear();
    			items.clear();
    			this.source.removeDockActionSourceListener( listener );
    		}
    		
    		this.source = source;
    		this.dockable = dockable;
    		
    		if( source != null ){
    			source.addDockActionSourceListener( listener );
    			for( DockAction action : source ){
    				actions.add( action );
    				BasicTitleViewItem<JComponent> item = createItemFor( action, dockable );
    				if( item != null ){
    					action.bind( dockable );
    					item.bind();
    					
    					items.put( action, item );
    					
    					item.setOrientation( orientation );
    					item.getItem().setForeground( getForeground() );
    					item.getItem().setBackground( getBackground() );
    					add( item.getItem() );
    				}
    			}
    		}
    	}
	}
    
    
    @Override
    public void setForeground( Color fg ) {
        super.setForeground( fg );
        
        if( items != null )
            for( ViewItem<JComponent> item : items.values() )
                item.getItem().setForeground( fg );
    }
    
    @Override
    public void setBackground( Color fg ) {
        super.setBackground( fg );
        
        if( items != null )
            for( ViewItem<JComponent> item : items.values() )
                item.getItem().setBackground( fg );
    }
    
	@Override
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	
	@Override
	public Dimension getPreferredSize(){
		int width = 0;
		int height = 0;
		
		if( orientation.isHorizontal() ){
			for( int i = 0, n = getComponentCount(); i<n; i++ ){
				Dimension preferred = getComponent( i ).getPreferredSize();
				width += preferred.width;
				height = Math.max( height, preferred.height );
			}
		}
		else{
			for( int i = 0, n = getComponentCount(); i<n; i++ ){
				Dimension preferred = getComponent( i ).getPreferredSize();
				width = Math.max( width, preferred.width );
				height += preferred.height;
			}
		}
		
		return new Dimension( width, height );
	}
	
	@Override
	public void doLayout(){
		Dimension current = getPreferredSize();
		
		if( orientation.isHorizontal() ){
			if( current.width <= 0 )
				return;
			
			int x = 0;
			int height = getHeight();
			int width = getWidth();
			
			if( width > current.width ){
				x += width - current.width;
				width = current.width;
			}
			
			for( int i = 0, n = getComponentCount(); i<n; i++ ){
				Component c = getComponent( i );
				Dimension preferred = c.getPreferredSize();
				if( current.width <= 0 ){
					if( width <= 0 )
						width = 1;
					
					current.width = width;
				}
				
				if( width == current.width ){
					c.setBounds( x, 0, preferred.width, height );
				}
				else{
					double factor = width / (double)current.width;
					c.setBounds( x, 0, (int)(factor * preferred.width), height );
				}
				
				current.width -= preferred.width;
				width -= c.getWidth();
				x += c.getWidth();
				
			}
		}
		else{
			if( current.width <= 0 )
				return;
			
			int y = 0;
			int height = getHeight();
			int width = getWidth();
			
			if( height > current.height ){
				y += height - current.height;
				height = current.height;
			}
			
			for( int i = 0, n = getComponentCount(); i<n; i++ ){
				Component c = getComponent( i );
				Dimension preferred = c.getPreferredSize();
				if( current.height <= 0 ){
					if( height <= 0 )
						height = 1;
					current.height = height;
				}
				
				if( height == current.height ){
					c.setBounds( 0, y, width, preferred.height );
				}
				else{
					double factor = height / (double)current.height;
					c.setBounds( 0, y, width, (int)(factor * preferred.height) );
				}
				
				current.height -= preferred.height;
				height -= c.getHeight();
				y += c.getHeight();
			}
		}
	}
	
	private class Listener implements DockActionSourceListener{
        public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = firstIndex; i <= lastIndex; i++ ){
                DockAction action = source.getDockAction( i );
                
                BasicTitleViewItem<JComponent> item = createItemFor( action, dockable );
                
                if( item != null ){
                	action.bind( dockable );
                    item.bind();
                    item.setOrientation( orientation );
                    item.getItem().setForeground( getForeground() );
	                item.getItem().setBackground( getBackground() );
                    items.put( action, item );
                }
            
                actions.add( i, action );
            }
            update();
        }
        public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = lastIndex; i >= firstIndex; i-- ){
            	DockAction action = actions.remove( i );
                
            	BasicTitleViewItem<JComponent> item = items.remove( action );
                if( item != null ){
                	item.unbind();
                	action.unbind( dockable );
                }
            }
            
            update();
        }
        
        /**
         * Ensures that all mini buttons are visible, and the layout
         * is up to date.
         */
        private void update(){
            removeAll();
                
            for( DockAction action : actions ){
            	BasicTitleViewItem<JComponent> item = items.get( action );
            	if( item != null ){
            		add( item.getItem() );
            	}
            }
                        
            revalidate();
        }
	}
}

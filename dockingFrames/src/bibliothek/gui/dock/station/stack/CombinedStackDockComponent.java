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

package bibliothek.gui.dock.station.stack;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.AbstractTabPane;
import bibliothek.gui.dock.station.stack.tab.AbstractTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.LonelyTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.TabLayoutManager;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.TabPaneListener;

/**
 * A {@link StackDockComponent} which is a combination of other components.<br>
 * This class also implements {@link TabPane} and thus supports the 
 * {@link TabLayoutManager}.
 * @author Benjamin Sigg
 *
 * @param <T> the type of the tabs
 * @param <M> the type of the menus
 */
public abstract class CombinedStackDockComponent<T extends CombinedTab, M extends CombinedMenu, I extends LonelyTabPaneComponent> extends AbstractTabPane<T, M, I> implements StackDockComponent{
    /** The panel which shows the children */
    private JPanel panel;
    
    /** A list of all {@link Component Components} which are shown on this {@link #componentPanel}  */
    private Map<Dockable, Meta> components = new HashMap<Dockable, Meta>();
    
    /** The panel which displays one of the children of this FlatTab */
    private JPanel componentPanel = new JPanel( null ){
    	@Override
    	public void doLayout(){
    		int w = getWidth();
    		int h = getHeight();
    		
    		for( int i = 0, n = getComponentCount(); i<n; i++ ){
    			getComponent(i).setBounds( 0, 0, w, h );
    		}
    	}
    	
    	@Override
    	public Dimension getPreferredSize() {
    		Dimension base = new Dimension( 0, 0 );
    		for( int i = 0, n = getComponentCount(); i<n; i++ ){
    			Dimension next = getComponent(i).getPreferredSize();
    			base.width = Math.max( base.width, next.width );
    			base.height = Math.max( base.height, next.height );
    		}
    		return base;
    	}
    	
    	@Override
    	public Dimension getMinimumSize() {
    		Dimension base = new Dimension( 0, 0 );
    		for( int i = 0, n = getComponentCount(); i<n; i++ ){
    			Dimension next = getComponent(i).getMinimumSize();
    			base.width = Math.max( base.width, next.width );
    			base.height = Math.max( base.height, next.height );
    		}
    		return base;
    	}
    };
    
    /** listeners to be informed when the selection changes */
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    /** Handles visibility of tabs */
    private CombinedVisibility<CombinedTab> tabVisibilityHandler = new CombinedVisibility<CombinedTab>(){
    	public void setVisible( CombinedTab tab, boolean visible ){
        	DockController controller = getController();
        	
        	if( visible ){
        		panel.add( tab.getComponent() );
        		if( controller != null ){
        			controller.addRepresentative( tab );
        		}
        	}
        	else{
        		panel.remove( tab.getComponent() );
        		if( controller != null ){
        			controller.removeRepresentative( tab );
        		}
        	}
    	}
    	
    	public boolean isVisible( CombinedTab item ){
	    	return item.getComponent() != null && item.getComponent().getParent() == panel;
    	}
    };
    
    /** Handles visibility of menus. */
    private CombinedVisibility<CombinedMenu> menuVisibilityHandler = new CombinedVisibility<CombinedMenu>(){
    	public void setVisible( CombinedMenu menu, boolean visible ){
    		if( visible ){
        		panel.add( menu.getComponent() );
        	}
        	else{
        		panel.remove( menu.getComponent() );
        	}
        }
    	
    	public boolean isVisible( CombinedMenu item ){
	    	return item.getComponent() != null && item.getComponent().getParent() == panel;
    	}
    };
    
    /** Handles visibility of info components */
    private CombinedVisibility<AbstractTabPaneComponent> infoVisibilityHandler = new CombinedVisibility<AbstractTabPaneComponent>(){
    	public void setVisible( AbstractTabPaneComponent item, boolean visible ){
    		if( visible ){
        		panel.add( item.getComponent() );
        	}
        	else{
        		panel.remove( item.getComponent() );
        	}
    	}
    	
    	public boolean isVisible( AbstractTabPaneComponent item ){
    		return item.getComponent() != null && item.getComponent().getParent() == panel;
    	}
    };
    
    /**
     * Constructs a new component.
     */
    public CombinedStackDockComponent(){
        panel = new JPanel( null ){
            @Override
            public void doLayout() {
                CombinedStackDockComponent.this.doLayout();
            }
            
            @Override
            public Dimension getPreferredSize(){
            	return CombinedStackDockComponent.this.getPreferredSize();
            }
            
            @Override
            public Dimension getMinimumSize() {
            	return CombinedStackDockComponent.this.getMinimumSize();
            }
        };
        panel.add( componentPanel );
        
        addTabPaneListener( new TabPaneListener(){
        	public void added( TabPane pane, Dockable dockable ){
        		// ignore
        	}
        	public void removed( TabPane pane, Dockable dockable ){
        		// ignore
        	}
        	public void selectionChanged( TabPane pane ){
        		ChangeEvent event = new ChangeEvent( CombinedStackDockComponent.this );
        		for( ChangeListener listener : listeners.toArray( new ChangeListener[ listeners.size() ] )){
        			listener.stateChanged( event );
        		}
        	}
        	public void infoComponentChanged( TabPane pane, LonelyTabPaneComponent oldInfo, LonelyTabPaneComponent newInfo ){
        		// ignore
        	}
        });
    }
    
    @Override
    public void revalidate(){
    	panel.revalidate();
    }
    
    public void addChangeListener( ChangeListener listener ){
	    listeners.add( listener );	
    }
    
    public void removeChangeListener( ChangeListener listener ){
    	listeners.remove( listener );
    }
    
    public void setController( DockController controller ){
    	DockController old = getController();
    	
        if( old != controller ){
        	List<T> tabs = getTabsList(); 
        	
        	if( old != null ){
                for( T tab : tabs ){
                    old.removeRepresentative( tab );
                }
            }

            if( controller != null ){
                for( T tab : tabs ){
                    controller.addRepresentative( tab );
                }
            }
            
            super.setController( controller );
        }
    }
    
    /**
     * Gets a visibility handler for tabs. This visibility handler adds or 
     * removes {@link CombinedTab}s from this component in order to change
     * their visibility.
     * @return the handler
     */
    public CombinedVisibility<CombinedTab> getTabVisibilityHandler(){
		return tabVisibilityHandler;
	}
    
    /**
     * Gets a visibility handler for menus. This visibility handler adds or 
     * removes {@link CombinedMenu}s from this component in order to change
     * their visibility.
     * @return the handler
     */
    public CombinedVisibility<CombinedMenu> getMenuVisibilityHandler(){
		return menuVisibilityHandler;
	}
    
    /**
     * Gets a visibility handler for info components. This visibility handler adds or 
     * removes {@link AbstractTabPaneComponent}s from this component in order to change
     * their visibility.
     * @return the handler
     */
    public CombinedVisibility<AbstractTabPaneComponent> getInfoVisibilityHandler(){
		return infoVisibilityHandler;
	}
    
    public Rectangle getAvailableArea(){
    	Insets insets = panel.getInsets();
    	if( insets == null ){
    		insets = new Insets( 0, 0, 0, 0 );
    	}
    	else{
    		insets = new Insets( insets.top, insets.left, insets.bottom, insets.right );
    	}
    	
    	Border border = panel.getBorder();
    	if( border != null ){
    		Insets borderInsets = border.getBorderInsets( panel );
    		insets.left += borderInsets.left;
    		insets.top += borderInsets.top;
    		insets.right += borderInsets.right;
    		insets.bottom += borderInsets.bottom;
    	}
    	
    	return new Rectangle( 
    			insets.left,
    			insets.top,
    			Math.max( 1, panel.getWidth() - insets.left - insets.right ),
    			Math.max( 1, panel.getHeight() - insets.top - insets.bottom ));
    }
    
    public Rectangle getSelectedBounds(){
    	return componentPanel.getBounds();
    }
    
    public void setSelectedBounds( Rectangle bounds ){
    	componentPanel.setBounds( bounds );
    }
    
    public int getSelectedIndex() {
        return indexOf( getSelectedDockable() );
    }

    public void setSelectedIndex( int index ){
    	if( index < 0 || index >= getDockableCount() )
    		setSelectedDockable( null );
    	else
    		setSelectedDockable( getDockable( index ) );
    }
    
    @Override
    public void setSelectedDockable( Dockable dockable ){
    	if( getSelectedDockable() != dockable ){
    		super.setSelectedDockable( dockable );
    	
    		for( Meta entry : components.values() ){
    			entry.component.setVisible( entry.dockable == dockable );
    		}
    	}
    }

    public Rectangle getBoundsAt( int index ) {
    	T tab = getTab( getDockable( index ) );
    	if( tab == null )
    		return null;
    	
    	return tab.getBounds();
    }
    
    public int getTabCount(){
    	return getDockableCount();
    }
    
    public void addTab( String title, Icon icon, Component comp, Dockable dockable ) {
        insertTab( title, icon, comp, dockable, getTabCount() );
    }

    public void insertTab( String title, Icon icon, Component comp, Dockable dockable, int index ) {
        Component between = createLayerAt( comp, dockable );
        Meta meta = new Meta( dockable, between, title, icon, null );
        components.put( dockable, meta );
        componentPanel.add( between );
        
        insert( index, dockable );
        
        meta.forward();
        
        setSelectedDockable( getSelectedDockable() );
    }
    
    @Override
    public void remove( int index ){
    	Dockable dockable = getDockable( index );
    	super.remove( index );
    	Meta meta = components.remove( dockable );
    	componentPanel.remove( meta.component );
    }
    
    @Override
    public void removeAll(){
    	super.removeAll();
    	for( Meta meta : components.values() ){
    		componentPanel.remove( meta.component );
    	}
    	components.clear();
    }
    
    @Override
    public T putOnTab( Dockable dockable ){
    	T tab = super.putOnTab( dockable );
    	Meta meta = components.get( dockable );
    	
    	tab.setIcon( meta.icon );
    	tab.setText( meta.text );
    	tab.setTooltip( meta.tooltip );
    	
    	return tab;
    }
    
    protected void addToMenu( M menu, Dockable dockable ){
    	int index = menu.getDockableCount();
    	menu.insert( index, dockable );
    	
    	Meta meta = components.get( dockable );
    	menu.setIcon( index, meta.icon );
    	menu.setText( index, meta.text );
    	menu.setTooltip( index, meta.tooltip );
    }
    
    protected void removeFromMenu( M menu, Dockable dockable ){
    	menu.remove( dockable );
    }
    
    /**
     * Creates a layer between <code>component</code> and this panel. The
     * object <code>component</code> is a representation of <code>dockable</code>
     * but not necessarily <code>dockable</code> itself. The default
     * behavior of this method is to return <code>component</code>.
     * @param component the representation of <code>dockable</code>
     * @param dockable the element for which a new layer is created
     * @return the new layer which must be a parent of <code>component</code>
     * or <code>component</code>
     */
    protected Component createLayerAt( Component component, Dockable dockable ){
    	return component;
    }
    
    /**
     * Gets the index'th {@link Component} on this tab. This <code>Component</code>
     * is not a {@link Dockable} but a layer between dockable and this panel.
     * @param index the index of a tab.
     * @return the layer between tab and dockable
     * @see #createComponentAt(Dockable)
     */
    protected Component getLayerAt( int index ){
    	return components.get( getDockable( index ) ).component;
    }
    
    public void setTitleAt( int index, String newTitle ) {
    	Meta meta = components.get( getDockable( index ) );
    	meta.text = newTitle;
    	meta.forward();
    }
    
    public void setTooltipAt( int index, String newTooltip ) {
    	Meta meta = components.get( getDockable( index ) );
    	meta.tooltip = newTooltip;
    	meta.forward();
    }
    
    public void setIconAt( int index, Icon newIcon ) {
    	Meta meta = components.get( getDockable( index ) );
    	meta.icon = newIcon;
    	meta.forward();
    }

    public JComponent getComponent() {
        return panel;
    }

    /**
     * Meta information about a {@link Dockable} that is shown on this 
     * {@link CombinedStackDockComponent}.
     * @author Benjamin Sigg
     */
    private class Meta{
    	/** the element that is shown */
    	public Dockable dockable;
    	/** visual representation of {@link #dockable} */
    	public Component component; 
    	/** text to be displayed on the tab of {@link #dockable} */
    	public String text;
    	/** icon to be displayed on the tab of {@link #dockable} */
    	public Icon icon;
    	/** tooltib to be displayed on the tab of {@link #dockable} */
    	public String tooltip;

    	/**
    	 * Creates new meta information.
    	 * @param dockable the element for which the meta information is required
    	 * @param component graphical representation of <code>dockable</code>
    	 * @param text text to be shown in the tab
    	 * @param icon icon to be shown in the tab
    	 * @param tooltip tooltip to be shown on the tab
    	 */
    	public Meta( Dockable dockable, Component component, String text, Icon icon, String tooltip ){
    		this.dockable = dockable;
    		this.component = component;
    		this.text = text;
    		this.icon = icon;
    		this.tooltip = tooltip;
    	}
    	
    	/**
    	 * Searches {@link CombinedTab} or {@link CombinedMenu} of {@link #dockable}
    	 * and updates {@link #text}, {@link #icon} and {@link #tooltip}.
    	 */
    	public void forward(){
    		CombinedTab tab = getTab( dockable );
    		if( tab != null ){
    			tab.setIcon( icon );
    			tab.setText( text );
    			tab.setTooltip( tooltip );
    		}
    		else{
    			CombinedMenu menu = getMenu( dockable );
    			if( menu != null ){
    				Dockable[] dockables = menu.getDockables();
    				for( int i = 0; i < dockables.length; i++ ){
    					if( dockables[i] == dockable ){
    						menu.setIcon( i, icon );
    						menu.setText( i, text );
    						menu.setTooltip( i, tooltip );
    						break;
    					}
    				}
    			}
    		}
    	}
    }
}

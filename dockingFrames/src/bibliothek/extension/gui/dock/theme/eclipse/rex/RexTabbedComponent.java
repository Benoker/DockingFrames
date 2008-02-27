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
package bibliothek.extension.gui.dock.theme.eclipse.rex;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.ShapedGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabComponent;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabListener;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabPainter;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.title.DockTitle;


/**
 * @author Janni Kovacs
 */
public class RexTabbedComponent extends JComponent {

	public static final String X_OVERLAY_PROPERTY = "RexTabbedComponent.xOverlay";

	public static final int TOP = 0;
	public static final int BOTTOM = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;

	protected JComponent tabStrip;
	private Dockable selectedTab;
	private Dockable focusedTab;
	private TabPainter tabPainter;
	private List<TabListener> listeners = new LinkedList<TabListener>();
	private List<TabEntry> tabs = new ArrayList<TabEntry>();
	private JComponent contentArea;
	
	private boolean paintIconsWhenInactive = false;
	private DockController controller;
	
	private EclipseTheme theme;
	private StackDockStation stack;
	private DockStation station;
	
	/**
	 * Creates a new tabbed component.
	 * @param theme the theme currently used
	 * @param station the owner of this component, can be <code>null</code>
	 */
	public RexTabbedComponent( EclipseTheme theme, DockStation station ) {
		this.theme = theme;
		this.station = station;
		if( station instanceof StackDockStation )
		    stack = (StackDockStation)station;
		initComponent();
	}

	private void initComponent() {
		setTabStrip( new RexTabStrip( this ));
		contentArea = new JPanel(new BorderLayout());
		
		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, SystemColor.controlShadow));
		setLayout(new BorderLayout());
		
		add(contentArea, BorderLayout.CENTER);
		setTabPainter( ShapedGradientPainter.FACTORY );
	}

	protected void bind( TabEntry tab ){
	    if( tab.tab != null && !tab.tabBound ){
	        tab.tab.bind();
	        tab.tabBound = true;
	        if( controller != null )
	        	controller.putRepresentative( tab.tab.getComponent(), tab.dockable );
	    }
	}
	
	protected void unbind( TabEntry tab ){
        if( tab.tab != null && tab.tabBound ){
            tab.tab.unbind();
            tab.tabBound = false;
            if( controller != null )
            	controller.putRepresentative( tab.tab.getComponent(), null );
        }
	}
	
	public void setTabStrip( JComponent strip ){
		if( tabStrip != null ){
			remove( tabStrip );
			tabStrip.removeAll();
		}
		
		String constraints = BorderLayout.NORTH;
		tabStrip = strip;
		add(strip, constraints);
		
		for( TabEntry entry : tabs ){
		    if( entry.tab != null )
		        tabStrip.add( entry.tab.getComponent() );
		}
	}

	public void setController( DockController controller ) {
	    if( this.controller != controller ){
	    	if( this.controller != null ){
	    		for( TabEntry tab : tabs ){
	    			this.controller.putRepresentative( tab.tab.getComponent(), null );
	    		}
	    	}
	    	
            this.controller = controller;
            reinitializeTabs();
        }
	}
	
	public DockController getController() {
        return controller;
    }
	
	public EclipseTheme getTheme() {
        return theme;
    }
	
	public DockStation getStation() {
        return station;
    }
	
	public void setTabPainter(TabPainter painter) {
	    if( this.tabPainter != painter ){
	        this.tabPainter = painter;
		    reinitializeTabs();
	    }
	}
	
	protected void reinitializeTabs(){
	    // remove old tabs
	    tabStrip.removeAll();
	    for( TabEntry entry : tabs ){
	        TabComponent tab = entry.tab;
	        if( tab != null ){
	            tab.removeMouseListener( entry.tabMouseListener );
	            unbind( entry );
	        }
	    }
	    
	    // add new tabs
	    if( controller != null && tabPainter != null ){
	        int index = 0;
	        for( TabEntry entry : tabs ){
                entry.tab = tabPainter.createTabComponent( controller, this, stack, entry.dockable, index++ );
                entry.tab.addMouseListener( entry.tabMouseListener );
                entry.tab.setPaintIconWhenInactive( paintIconsWhenInactive );
                entry.tab.setSelected( entry.dockable == selectedTab );
                entry.tab.setFocused( entry.dockable == focusedTab );
                bind( entry );
                
                tabStrip.add( entry.tab.getComponent() );
            }
	        tabStrip.revalidate();
	    }
	}
	
	public TabPainter getTabPainter(){
		return tabPainter;
	}
	
	public void setPaintIconsWhenInactive( boolean paint ){
		this.paintIconsWhenInactive = paint;
		for( TabEntry entry : tabs ){
		    if( entry.tab != null )
		        entry.tab.setPaintIconWhenInactive( paint );
		}
	}

	public void addTab( Dockable dockable ) {
		insertTab( dockable , tabs.size());
		setSelectedTab( dockable );
	}

	public void insertTab( Dockable dockable, int index) {
		TabEntry entry = new TabEntry();
		entry.dockable = dockable;
		entry.tabMouseListener = new TabMouseListener( dockable );
		tabs.add( index, entry );
        
		if( controller != null ){
    		entry.tab = tabPainter.createTabComponent( controller, this, stack, dockable, index );
    		
    		entry.tab.addMouseListener( entry.tabMouseListener );
    		entry.tab.setPaintIconWhenInactive( paintIconsWhenInactive );
    		bind( entry );
    		
    		tabStrip.removeAll();
    		
    		for( int i = 0, n = tabs.size(); i<n; i++ ){
    		    TabComponent tabComponent = tabs.get( i ).tab;
    			tabComponent.setIndex( i );
    			tabStrip.add( tabComponent.getComponent() );
    		}
		}
	}

	public int getTabCount() {
		return tabs.size();
	}

	public Dockable getSelectedTab() {
		return selectedTab;
	}

	public TabComponent getTabComponent( int index ){
		return tabs.get( index ).tab;
	}
	
	public int indexOf( Dockable dockable ) {
		for( int i = 0, n = tabs.size(); i<n; i++ ){
			if( tabs.get( i ).dockable == dockable )
				return i;
		}
		
		return -1;
	}

	public void removeTab( Dockable dockable ) {
		for (TabListener listener : listeners) {
			listener.tabRemoved( dockable );
		}
		int index = indexOf( dockable );
		TabEntry entry = tabs.get( index );
		tabs.remove( index );
		if( entry.tab != null ){
		    tabStrip.remove( entry.tab.getComponent() );
		    unbind( entry );
		    entry.tab.removeMouseListener( entry.tabMouseListener );
		}
		
		for( int i = index, n = tabs.size(); i<n; i++ ){
		    TabComponent tab = tabs.get( i ).tab;
		    if( tab != null ){
		        tab.setIndex( i );
		    }
		}
		
		if (dockable == selectedTab) {
			if (index == tabs.size()) {
				index = tabs.size() - 1;
			}
			if (index >= 0 && index < tabs.size()) {
				setSelectedTab(index);
			} else {
				setSelectedTab(null);
			}
		}
		
	}

	public void removeAllTabs() {
		for (TabEntry tab : tabs) {			
			for (TabListener listener : listeners) {
				listener.tabRemoved( tab.dockable );
				if( tab.tab != null ){
				    unbind( tab );
				    tab.tab.removeMouseListener( tab.tabMouseListener );
				}
			}
		}
		
		tabStrip.removeAll();
		tabs.clear();
		setSelectedTab(null);
	}

	public JComponent getContentArea() {
		return contentArea;
	}

	/**
	 * Gets an estimate of the insets around the elements that are shown on the content-area.
	 * @return an estimate of the insets between one element and this component.
	 */
	public Insets getContentInsets(){
	    Insets insets = new Insets( 0,0,0,0 );
	    Dimension size = tabStrip.getPreferredSize();
	    if( size != null ){
	        insets.top += size.height;
	    }
	    
	    Insets temp = contentArea.getInsets();
	    if( temp != null ){
	        insets.left += temp.left;
	        insets.right += temp.right;
	        insets.top += temp.top;
	        insets.bottom += temp.bottom;
	    }
	    
	    temp = getInsets();
	    if( temp != null ){
            insets.left += temp.left;
            insets.right += temp.right;
            insets.top += temp.top;
            insets.bottom += temp.bottom;
        }
        
	    return insets;
	}
	
	public void addTabListener(TabListener listener) {
		listeners.add(listener);
	}

	public void removeTabListener(TabListener listener) {
		listeners.remove(listener);
	}

	public int getSelectedIndex() {
		return indexOf(getSelectedTab());
	}

	public void setSelectedTab(int i) {
		setSelectedTab(tabs.get(i).dockable);
	}
	
	public void setFocusedTab( Dockable dockable ){
		focusedTab = dockable;
		for( TabEntry entry : tabs ){
		    if( entry.tab != null )
		        entry.tab.setFocused( entry.dockable == dockable );
		}
	}

	public void setSelectedTab( Dockable dockable ) {
		if (dockable != selectedTab) {
		    int newIndex = -1;
		    if( dockable != null )
		        newIndex = indexOf( dockable );
		    
		    if( dockable == null || newIndex >= 0 ){
    			if( selectedTab != null ){
    				int index = indexOf( selectedTab );
    				if( index >= 0 ){
    				    TabComponent tab = tabs.get( index ).tab;
    				    if( tab != null ){
    				        tab.setSelected( false );
    				    }
    				}
    			}
    			
    			selectedTab = dockable;
    			
    			if( selectedTab != null ){
                    if( newIndex >= 0 ){
                        TabComponent tab = tabs.get( newIndex ).tab;
                        if( tab != null )
                            tab.setSelected( true );
                    }
                }
                
    			
    			tabChanged(dockable);
    			for (TabListener listener : listeners) {
    				listener.tabChanged(dockable);
    			}
		    }
		}
	}

	public Dockable getTabAt(int index) {
		return tabs.get(index).dockable;
	}

	public Rectangle getBoundsAt(int index) {
		return getBounds(getTabAt(index));
	}

	public Rectangle getBounds(Dockable dockable) {
		TabComponent component = tabs.get( indexOf( dockable ) ).tab;
		Point location = new Point( 0, 0 );
		location = SwingUtilities.convertPoint( component.getComponent(), location, this );
		
		return new Rectangle( location, component.getComponent().getSize() );
	}
	
	public void updateContentBorder(){
		if( selectedTab != null ){
			TabEntry entry = tabs.get( indexOf( selectedTab ) );
			if( entry.tab == null ){
			    contentArea.setBorder( null );
			}
			else{
			    contentArea.setBorder( entry.tab.getContentBorder() );
			}
		}
		else
			contentArea.setBorder( null );
	}
	
	protected void popup( Dockable dockable, MouseEvent e ){
		if( !e.isConsumed() && e.isPopupTrigger() ){
			Component comp = dockable.getComponent();
			if( comp instanceof JComponent ){
				JComponent jcomp = (JComponent)comp;

				JPopupMenu popup = jcomp.getComponentPopupMenu();
				if (popup != null) {
					Point location = jcomp.getPopupLocation(e);
					if (location == null) {
						location = e.getPoint();
					}
					popup.show(this, location.x, location.y);
				}
			}
		}
	}
	
	protected void tabChanged(Dockable dockable) {
		contentArea.removeAll();
		if (dockable == null) {
			contentArea.removeAll();
		} else {
			contentArea.add( dockable.getComponent(), BorderLayout.CENTER);
		}
		contentArea.revalidate();
		contentArea.repaint();
	}

	protected static class TabEntry{
		public Dockable dockable;
		public DockTitle title;
		public TabComponent tab;
		public boolean tabBound = false;
		public TabMouseListener tabMouseListener;
	}
	
	private class TabMouseListener extends MouseAdapter{
		private Dockable dockable;
		
		public TabMouseListener( Dockable dockable ){
			this.dockable = dockable;
		}
		
		@Override
		public void mouseClicked( MouseEvent e ){
			setSelectedTab( dockable );
		}
		
		@Override
		public void mousePressed( MouseEvent e ){
			popup( dockable, e );
		}
		
		@Override
		public void mouseReleased( MouseEvent e ){
			popup( dockable, e );
		}
	}
}

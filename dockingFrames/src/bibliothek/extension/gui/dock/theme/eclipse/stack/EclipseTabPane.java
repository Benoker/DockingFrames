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
package bibliothek.extension.gui.dock.theme.eclipse.stack;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabComponent;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabPainter;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.tab.LonelyTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.TabPaneListener;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * The {@link EclipseTabPane} uses a generic {@link TabPainter} to create its
 * tabs and to change its border.
 * @author Benjamin Sigg
 */
public class EclipseTabPane extends CombinedStackDockComponent<EclipseTab, EclipseMenu, EclipseTabInfo>{
	private PropertyValue<TabPainter> tabPainter = new PropertyValue<TabPainter>( EclipseTheme.TAB_PAINTER ){
		@Override
		protected void valueChanged( TabPainter oldValue, TabPainter newValue ){
			updateTabPainter();
		}
	};

	private DockStation station;
	private EclipseTheme theme;
	
	/**
	 * Creates a new pane.
	 * @param theme the theme which created this pane
	 * @param station the owner of this pane, may be <code>null</code>
	 */
	public EclipseTabPane( EclipseTheme theme, DockStation station ){
		this.theme = theme;
		this.station = station;
		
		setInfoComponent( new EclipseTabInfo( this ) );
		
		addTabPaneListener( new TabPaneListener(){
			public void added( TabPane pane, Dockable dockable ){
				// ignore
			}
			public void removed( TabPane pane, Dockable dockable ){
				// ignore
			}
			public void selectionChanged( TabPane pane ){
				updateFullBorder();
				EclipseTabInfo info = getInfoComponent();
				if( info != null ){
					info.setSelection( getSelectedDockable() );
				}
			}
			public void infoComponentChanged( TabPane pane, LonelyTabPaneComponent oldInfo, LonelyTabPaneComponent newInfo ){
				// ignore
			}
		});
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		tabPainter.setProperties( controller );
		
		for( EclipseTab tab : getTabsList() ){
			tab.setController( controller );
		}
		
		updateTabPainter();
	}
	
	@Override
	public void setInfoComponent( EclipseTabInfo info ){
		EclipseTabInfo old = getInfoComponent();
		if( old != null ){
			old.setSelection( null );
		}
		
		if( info != null ){
			info.setSelection( getSelectedDockable() );
		}
		
		super.setInfoComponent( info );
	}
	
	/**
	 * Gets the theme which created this pane.
	 * @return the theme, not <code>null</code>
	 */
	public EclipseTheme getTheme(){
		return theme;
	}
	
	/**
	 * Gets the station on which this pane lies.
	 * @return the station
	 */
	public DockStation getStation(){
		return station;
	}
	
	/**
	 * Gets the {@link TabPainter} that is currently responsible for creating
	 * new tabs for this pane.
	 * @return the tab painter
	 */
	public TabPainter getTabPainter(){
		return tabPainter.getValue();
	}
	
	/**
	 * Called when the {@link TabPainter} has been exchanged or to initialize
	 * this {@link EclipseTabPane}.
	 */
	protected void updateTabPainter(){
		updateFullBorder();
	}
	
	/**
	 * Updates the border that is around this whole pane.
	 */
	protected void updateFullBorder(){
		TabPainter painter = getTabPainter();
		DockController controller = getController();
		Dockable selection = getSelectedDockable();
		
		Border border = null;
		if( painter != null && controller != null && selection != null ){
			border = painter.getFullBorder( controller, selection );
		}
		
		getComponent().setBorder( border );
	}
	
	@Override
	protected EclipseTab newTab( Dockable dockable ){
		TabComponent component = getTabPainter().createTabComponent( this, dockable, indexOf( dockable ) );
		EclipseTab tab = new EclipseTab( this, dockable, component );
		tab.setController( getController() );
		tab.bind();
		return tab;
	}

	@Override
	protected void tabRemoved( EclipseTab tab ){
		tab.unbind();
	}
	
	@Override
	public EclipseMenu newMenu(){
		EclipseMenu menu = new EclipseMenu( this );
		return menu;
	}
	
	@Override
	protected void menuRemoved( EclipseMenu menu ){
		// nothing to do
	}
	
	/**
	 * Used by {@link TabComponent}s and by {@link TabPainter}s this method
	 * advises the {@link EclipseTabPane} to put <code>border</code> 
	 * around the contents of tab <code>index</code>.
	 * @param index the index of the tab
	 * @param border the new border, may be <code>null</code>
	 */
	public void setContentBorderAt( int index, Border border ){
		JComponent layer = getLayerAt( index );
		layer.setBorder( border );
	}
	
	@Override
	protected Component createLayerAt( Component component, Dockable dockable ){
		JPanel panel = new JPanel( new GridLayout( 1, 1 ));
		panel.add( component );
		return panel;
	}
	
	@Override
	protected JComponent getLayerAt( int index ){
		return (JComponent)super.getLayerAt( index );
	}
	
	/**
	 * Gets an estimate of the {@link Insets} between the selected
	 * {@link Dockable} and this whole component.
	 * @return the insets
	 */
	public Insets getContentInsets(){
		Rectangle selectionBounds = getSelectedBounds();
		Dimension size = getComponent().getSize();
		
		Insets result = new Insets( 
				selectionBounds.y, 
				selectionBounds.x,
				size.height - selectionBounds.y - selectionBounds.height,
				size.width - selectionBounds.x - selectionBounds.width );
		
		int index = getSelectedIndex();
		if( index < 0 )
			return result;
		
		JComponent layer = getLayerAt( index );
		Border border = layer.getBorder();
		if( border != null ){
			Insets add = border.getBorderInsets( layer );
			result.left += add.left;
			result.right += add.right;
			result.top += add.top;
			result.bottom += add.bottom;
		}
		
		return result;
	}
	
	public boolean hasBorder(){
		return true;
	}
}

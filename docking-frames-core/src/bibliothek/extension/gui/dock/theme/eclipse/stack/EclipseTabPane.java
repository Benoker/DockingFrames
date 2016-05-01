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
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.BaseTabComponent;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.BorderedComponent;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabComponent;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPainter;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPanePainter;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.CombinedStackDockContentPane;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentBorder;
import bibliothek.gui.dock.station.stack.StackDockComponentContentBorder;
import bibliothek.gui.dock.station.stack.tab.LonelyTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.TabPaneListener;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.border.BorderForwarder;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * The {@link EclipseTabPane} uses a generic {@link TabPainter} to create its
 * tabs and to change its border.
 * @author Benjamin Sigg
 */
public class EclipseTabPane extends CombinedStackDockComponent<EclipseTab, EclipseMenu, EclipseTabInfo> implements BorderedComponent{
   /**
    * Listens to the window ancestor of this {@link TabComponent} and updates
    * color when the focus is lost.
    * @author Benjamin Sigg, Thomas Hilbert
    */
    private class WindowActiveObserver extends WindowAdapter implements HierarchyListener {
       private Window window;

       public void hierarchyChanged (HierarchyEvent e) {
          Window newWindow = SwingUtilities.getWindowAncestor(getComponent());

          long lFlags = e.getChangeFlags();
          // update current found window only if parent has changed
          if (window != newWindow && (lFlags & HierarchyEvent.PARENT_CHANGED) != 0) {
             if (window != null) {
                window.removeWindowListener(this);
             }
             if (newWindow != null) {
                newWindow.addWindowListener(this);
                if (getSelectedIndex() != -1) {
                   EclipseTab tab = getTab(getSelectedDockable());
                   if (tab != null && tab.getComponent() instanceof BaseTabComponent) {
                      ((BaseTabComponent)tab.getComponent()).updateBorder();
                      ((BaseTabComponent)tab.getComponent()).updateFocus();
                   }
                }
             }
             window = newWindow;
          }
       }

       @Override
       public void windowActivated (WindowEvent e) {
          if (getSelectedIndex() != -1) {
             EclipseTab tab = getTab(getSelectedDockable());
             if (tab != null && tab.getComponent() instanceof BaseTabComponent) {
                ((BaseTabComponent)tab.getComponent()).updateBorder();
                ((BaseTabComponent)tab.getComponent()).updateFocus();
             }
          }
       }
 
       @Override
       public void windowDeactivated (WindowEvent e) {
          if (getSelectedIndex() != -1) {
             EclipseTab tab = getTab(getSelectedDockable());
             if (tab != null && tab.getComponent() instanceof BaseTabComponent) {
                ((BaseTabComponent)tab.getComponent()).updateBorder();
                ((BaseTabComponent)tab.getComponent()).updateFocus();
             }
          }
       }
    }

	private PropertyValue<TabPainter> tabPainter = new PropertyValue<TabPainter>( EclipseTheme.TAB_PAINTER ){
		@Override
		protected void valueChanged( TabPainter oldValue, TabPainter newValue ){
			updateTabPainter();
		}
	};

	private PropertyValue<Boolean> paintIcons = new PropertyValue<Boolean>( EclipseTheme.PAINT_ICONS_WHEN_DESELECTED ){
		@Override
		protected void valueChanged( Boolean oldValue, Boolean newValue ){
			updatePaintIcons();	
		}
	};
	
	private DockStation station;
	private EclipseTheme theme;
	private TabPanePainter painter;
	private BorderForwarder border;
	
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
			}
			public void infoComponentChanged( TabPane pane, LonelyTabPaneComponent oldInfo, LonelyTabPaneComponent newInfo ){
				// ignore
			}
			public void controllerChanged( TabPane pane, DockController controller ){
				// ignore
			}
		});
		
		getComponent().addHierarchyListener(new WindowActiveObserver());
	}
	
	@Override
	protected CombinedStackDockContentPane createContentPane( CombinedStackDockComponent<EclipseTab, EclipseMenu, EclipseTabInfo> self ){
		return new EclipseTabPaneContent( (EclipseTabPane)self );
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		tabPainter.setProperties( controller );
		paintIcons.setProperties( controller );
		getBorderForwarder().setController( controller );
		
		if( painter != null )
			painter.setController( controller );
		
		for( EclipseTab tab : getTabsList() ){
			tab.setController( controller );
		}
		for( EclipseMenu menu : getMenuList() ){
			menu.setController( controller );
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
		
		TabPainter painter = getTabPainter();
		setPainter( painter == null ? null : painter.createDecorationPainter( this ) );
		
		discardComponentsAndRebuild();
	}
	
	/**
	 * Sets the {@link TabPanePainter} which will paint decorations for
	 * this panel.
	 * @param painter the new painter, can be <code>null</code>
	 */
	public void setPainter( TabPanePainter painter ){
		if( this.painter != null ){
			this.painter.setController( null );
		}
		
		this.painter = painter;
		
		if( this.painter != null ){
			this.painter.setController( getController() );
		}
		
		repaint();
	}
	
	/**
	 * Gets the {@link TabPanePainter} which paints decorations for
	 * this panel.
	 * @return the painter, may be <code>null</code>
	 */
	public TabPanePainter getPainter(){
		return painter;
	}
	
	/**
	 * Updates the border that is around this whole pane.
	 */
	public void updateFullBorder(){
		TabPainter painter = getTabPainter();
		DockController controller = getController();
		Dockable selection = getSelectedDockable();
		
		Border border = null;
		if( painter != null && controller != null && selection != null ){
			border = painter.getFullBorder( this, controller, selection );
		}
		
		getBorderForwarder().setBorder( border );
		repaint();
	}
	
	private BorderForwarder getBorderForwarder(){
		if( border == null ){
			border = createBorderModifier( getComponent() );
			border.setController( getController() );
		}
		return border;
	}
	
	/**
	 * Creates the {@link BorderForwarder} that is used to set the border of this
	 * panel.
	 * @param target the target component, not <code>null</code>
	 * @return the new forwarder, not <code>null</code>
	 */
	protected BorderForwarder createBorderModifier( JComponent target ){
		return new TabPaneBorder( target );
	}
	
	@Override
	protected BorderForwarder createContentBorderModifier( Dockable dockable, JComponent component ){
		return new TabContentBorder( dockable, component );
	}
		
	private void updatePaintIcons(){
		boolean paintIconsWhenInactive = paintIcons.getValue();
		
		for( EclipseTab tab : getTabsList() ){
			tab.setPaintIconWhenInactive( paintIconsWhenInactive );
		}
		
		revalidate();
		repaint();
	}
	
	@Override
	protected EclipseTab newTab( Dockable dockable ){
		TabComponent component = getTabPainter().createTabComponent( this, dockable );
		EclipseTab tab = new EclipseTab( this, dockable, component );
		tab.setPaintIconWhenInactive( paintIcons.getValue() );
		tab.setController( getController() );
		tab.bind();
		return tab;
	}
	
	@Override
	protected void tabRemoved( EclipseTab tab ){
		tab.unbind();
	}
	
	/**
	 * Gets the index of <code>tab</code> in respect to the {@link Dockable}s 
	 * of this pane, ignores any invisible tab.
	 * @param tab the tab to search
	 * @return its index or -1 if not found or invisible
	 */
	public int indexOfVisible( TabComponent tab ){
		for( EclipseTab eclipse : getTabsList() ){
			if( eclipse.getTabComponent() == tab ){
				return indexOfVisible( eclipse );
			}
		}
		return -1;
	}
	
	@Override
	public EclipseMenu newMenu(){
		EclipseMenu menu = new EclipseMenu( this );
		menu.setController( getController() );
		return menu;
	}
	
	@Override
	protected void menuRemoved( EclipseMenu menu ){
		menu.setController( null );
	}
	
	/**
	 * Used by {@link TabComponent}s and by {@link TabPainter}s this method
	 * advises the {@link EclipseTabPane} to put <code>border</code> 
	 * around the contents of tab <code>index</code>.
	 * @param index the index of the tab
	 * @param border the new border, may be <code>null</code>
	 */
	public void setContentBorderAt( int index, Border border ){
		getContentAt( index ).setBorder( border );
	}
	
	@Override
	protected Component createLayerAt( Component component, Dockable dockable ){
		JPanel panel = new JPanel( new GridLayout( 1, 1 ));
		panel.add( component );
		panel.setOpaque( false );
		return panel;
	}
	
	@Override
	public JComponent getLayerAt( int index ){
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
	
	public boolean isSingleTabComponent(){
		return true;
	}
	
	/**
	 * Used by a {@link EclipseTabPane} to modify its border.
	 * @author Benjamin Sigg
	 */
	private class TabPaneBorder extends BorderForwarder implements StackDockComponentBorder{
		public TabPaneBorder( JComponent target ){
			super( StackDockComponentBorder.KIND, ThemeManager.BORDER_MODIFIER + ".stack.eclipse", target );
		}

		public StackDockComponent getStackComponent(){
			return EclipseTabPane.this;
		}
	}
	
	private class TabContentBorder extends BorderForwarder implements StackDockComponentContentBorder{
		private Dockable dockable;
		
		public TabContentBorder( Dockable dockable, JComponent target ){
			super( StackDockComponentContentBorder.KIND, ThemeManager.BORDER_MODIFIER + ".stack.eclipse.content", target );
			this.dockable = dockable;
		}
		
		public StackDockComponent getStackComponent(){
			return EclipseTabPane.this;
		}
		
		public Dockable getDockable(){
			return dockable;
		}
	}
}

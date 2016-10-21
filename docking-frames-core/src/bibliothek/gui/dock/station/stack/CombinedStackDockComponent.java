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
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.border.Border;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.disable.TabDisablingStrategyObserver;
import bibliothek.gui.dock.station.stack.tab.AbstractTabPane;
import bibliothek.gui.dock.station.stack.tab.AbstractTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.LonelyTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.TabConfiguration;
import bibliothek.gui.dock.station.stack.tab.TabConfigurations;
import bibliothek.gui.dock.station.stack.tab.TabLayoutManager;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.TabPaneBackgroundComponent;
import bibliothek.gui.dock.station.stack.tab.TabPaneListener;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.border.BorderForwarder;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPanel;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.SimpleDockElementRepresentative;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link StackDockComponent} which is a combination of other components.<br>
 * This class also implements {@link TabPane} and thus supports the 
 * {@link TabLayoutManager}.
 * @author Benjamin Sigg
 *
 * @param <T> the type of the tabs
 * @param <M> the type of the menus
 * @param <I> the type of the additional info panel
 */
public abstract class CombinedStackDockComponent<T extends CombinedTab, M extends CombinedMenu, I extends CombinedInfoComponent> extends AbstractTabPane<T, M, I> implements StackDockComponent {
	/** The panel which shows the children */
	private CombinedStackDockContentPane panel;

	/** A list of all {@link Component Components} which are shown on this {@link #componentPanel}  */
	private Map<Dockable, Meta> components = new HashMap<Dockable, Meta>();

	/** The current configuration of the tabs */
	private PropertyValue<TabConfigurations> tabConfiguration = new PropertyValue<TabConfigurations>( StackDockStation.TAB_CONFIGURATIONS ){
		@Override
		protected void valueChanged( TabConfigurations oldValue, TabConfigurations newValue ){
			for( T tab : getTabsList() ){
				tab.setConfiguration( newValue.getConfiguration( tab.getDockable() ));
			}
		}
	};
	
	/** the background of this component */
	private BackgroundAlgorithm background;

	/** The panel which displays one of the children of this pane */
	private BackgroundPanel componentPanel = new ConfiguredBackgroundPanel( null, Transparency.DEFAULT ){
		@Override
		public void doLayout(){
			int w = getWidth();
			int h = getHeight();

			for( int i = 0, n = getComponentCount(); i < n; i++ ) {
				getComponent( i ).setBounds( 0, 0, w, h );
			}
		}

		@Override
		public Dimension getPreferredSize(){
			Dimension base = new Dimension( 0, 0 );
			for( int i = 0, n = getComponentCount(); i < n; i++ ) {
				Dimension next = getComponent( i ).getPreferredSize();
				base.width = Math.max( base.width, next.width );
				base.height = Math.max( base.height, next.height );
			}
			return base;
		}

		@Override
		public Dimension getMinimumSize(){
			Dimension base = new Dimension( 0, 0 );
			for( int i = 0, n = getComponentCount(); i < n; i++ ) {
				Dimension next = getComponent( i ).getMinimumSize();
				base.width = Math.max( base.width, next.width );
				base.height = Math.max( base.height, next.height );
			}
			return base;
		}
	};

	/** listeners to be informed when the selection changes */
	private List<StackDockComponentListener> listeners = new ArrayList<StackDockComponentListener>();

	/** Handles visibility of tabs */
	private CombinedHandler<CombinedTab> tabHandler = new CombinedHandler<CombinedTab>(){
		public void setVisible( CombinedTab tab, boolean visible ){
			DockController controller = getController();

			if( visible ) {
				panel.add( tab.getComponent() );
				if( controller != null ) {
					controller.addRepresentative( tab );
				}
			}
			else {
				panel.remove( tab.getComponent() );
				if( controller != null ) {
					controller.removeRepresentative( tab );
				}
			}
		}

		public boolean isVisible( CombinedTab item ){
			return item.getComponent() != null && item.getComponent().getParent() == panel;
		}

		public void setZOrder( CombinedTab item, int order ){
			CombinedStackDockComponent.this.setZOrder( item.getComponent(), order );
		}

		public int getZOrder( CombinedTab item ){
			return CombinedStackDockComponent.this.getZOrder( item.getComponent() );
		}
	};

	/** Handles visibility of menus. */
	private CombinedHandler<CombinedMenu> menuHandler = new CombinedHandler<CombinedMenu>(){
		public void setVisible( CombinedMenu menu, boolean visible ){
			if( visible ) {
				panel.add( menu.getComponent() );
			}
			else {
				panel.remove( menu.getComponent() );
			}
		}

		public boolean isVisible( CombinedMenu item ){
			return item.getComponent() != null && item.getComponent().getParent() == panel;
		}

		public void setZOrder( CombinedMenu item, int order ){
			CombinedStackDockComponent.this.setZOrder( item.getComponent(), order );
		}

		public int getZOrder( CombinedMenu item ){
			return CombinedStackDockComponent.this.getZOrder( item.getComponent() );
		}
	};

	/** Handles visibility of info components */
	private CombinedHandler<AbstractTabPaneComponent> infoHandler = new CombinedHandler<AbstractTabPaneComponent>(){
		public void setVisible( AbstractTabPaneComponent item, boolean visible ){
			if( visible ) {
				panel.add( item.getComponent() );
			}
			else {
				panel.remove( item.getComponent() );
			}
		}

		public boolean isVisible( AbstractTabPaneComponent item ){
			return item.getComponent() != null && item.getComponent().getParent() == panel;
		}

		public void setZOrder( AbstractTabPaneComponent item, int order ){
			CombinedStackDockComponent.this.setZOrder( item.getComponent(), order );
		}

		public int getZOrder( AbstractTabPaneComponent item ){
			return CombinedStackDockComponent.this.getZOrder( item.getComponent() );
		}
	};

	/** keeps track of which tabs and menu items must be disabled */
	private TabDisablingStrategyObserver tabDisabling = new TabDisablingStrategyObserver(){
		@Override
		public void setDisabled( Dockable dockable, boolean disabled ){
			setEnabledAt( dockable, !disabled );
		}
	};
	
	/**
	 * Constructs a new component.
	 */
	public CombinedStackDockComponent(){
		background = createBackground( this );
		panel = createContentPane( this );
		panel.add( componentPanel );
		panel.setBackground( background );
		componentPanel.setBackground( background );

		addTabPaneListener( new TabPaneListener(){
			public void added( TabPane pane, Dockable dockable ){
				// ignore
			}

			public void removed( TabPane pane, Dockable dockable ){
				// ignore
			}

			public void selectionChanged( TabPane pane ){
				for( StackDockComponentListener listener : listeners.toArray( new StackDockComponentListener[listeners.size()] ) ) {
					listener.selectionChanged( CombinedStackDockComponent.this );
				}
			}

			public void infoComponentChanged( TabPane pane, LonelyTabPaneComponent oldInfo, LonelyTabPaneComponent newInfo ){
				// ignore
			}
			
			public void controllerChanged( TabPane pane, DockController controller ){
				// ignore
			}
		} );
	}

	/**
	 * Creates the content pane for <code>this</code> component. This method
	 * may be called by the constructor.
	 * @param self <code>this</code>
	 * @return a new panel, not <code>null</code>
	 */
	protected CombinedStackDockContentPane createContentPane( CombinedStackDockComponent<T, M, I> self ){
		return new CombinedStackDockContentPane( self );
	}

	/**
	 * Creates the background algorithm that is used for <code>this</code> component. This method
	 * may be called by the constructor.
	 * @param self <code>this</code>
	 * @return the new algorithm, not <code>null</code>
	 */
	protected BackgroundAlgorithm createBackground( CombinedStackDockComponent<T, M, I> self ){
		return new Background();
	}

	@Override
	public void revalidate(){
		panel.revalidate();
		repaint();
	}

	/**
	 * Repaints the contents of this component.
	 */
	public void repaint(){
		panel.repaint();
	}

	public void addStackDockComponentListener( StackDockComponentListener listener ){
		listeners.add( listener );
	}

	public void removeStackDockComponentListener( StackDockComponentListener listener ){
		listeners.remove( listener );
	}

	/**
	 * Calls {@link StackDockComponentListener#tabChanged(StackDockComponent, Dockable)} on all listeners that
	 * are currently registered.
	 * @param dockable the element whose tab changed
	 */
	protected void fireTabChanged( Dockable dockable ){
		for( StackDockComponentListener listener : listeners ){
			listener.tabChanged( this, dockable );
		}
	}
	
	public void setController( DockController controller ){
		DockController old = getController();

		if( old != controller ) {
			List<T> tabs = getTabsList();

			if( old != null ) {
				for( T tab : tabs ) {
					old.removeRepresentative( tab );
				}
			}

			if( controller != null ) {
				for( T tab : tabs ) {
					controller.addRepresentative( tab );
				}
			}
			
			for( Meta meta : components.values() ){
				meta.setController( controller );
			}

			
			background.setController( controller );
			tabDisabling.setController( controller );
			tabConfiguration.setProperties( controller );
			super.setController( controller );
		}
	}
	
	/**
	 * Forwards <code>child</code> to the current {@link TabConfigurations}, in order to 
	 * get the matching {@link TabConfiguration}.
	 * @param child some child of this {@link StackDockComponent}
	 * @return the {@link TabConfiguration} that should be used for its tab
	 */
	public TabConfiguration getConfiguration( Dockable child ){
		return tabConfiguration.getValue().getConfiguration( child );
	}

	/**
	 * Gets a handler for tabs. This handler adds or 
	 * removes {@link CombinedTab}s from this component in order to change
	 * their visibility.
	 * @return the handler
	 */
	public CombinedHandler<CombinedTab> getTabHandler(){
		return tabHandler;
	}

	/**
	 * Gets a handler for menus. This handler adds or 
	 * removes {@link CombinedMenu}s from this component in order to change
	 * their visibility.
	 * @return the handler
	 */
	public CombinedHandler<CombinedMenu> getMenuHandler(){
		return menuHandler;
	}

	/**
	 * Gets a handler for info components. This handler adds or 
	 * removes {@link AbstractTabPaneComponent}s from this component in order to change
	 * their visibility.
	 * @return the handler
	 */
	public CombinedHandler<AbstractTabPaneComponent> getInfoHandler(){
		return infoHandler;
	}

	public Rectangle getAvailableArea(){
		Insets insets = panel.getInsets();
		if( insets == null ) {
			insets = new Insets( 0, 0, 0, 0 );
		}
		else {
			insets = new Insets( insets.top, insets.left, insets.bottom, insets.right );
		}
		return new Rectangle( insets.left, insets.top, Math.max( 1, panel.getWidth() - insets.left - insets.right ), Math.max( 1, panel.getHeight()
				- insets.top - insets.bottom ) );
	}
	
	@Override
	public Dimension getMinimumSize(){
		Dimension result = super.getMinimumSize();
		Insets insets = panel.getInsets();
		if( insets != null ){
			result = new Dimension( 
					result.width + insets.left + insets.right,
					result.height + insets.top + insets.bottom );
		}
		return result;
	}
	
	@Override
	public Dimension getPreferredSize(){
		Dimension result = super.getPreferredSize();
		Insets insets = panel.getInsets();
		if( insets != null ){
			result = new Dimension( 
					result.width + insets.left + insets.right,
					result.height + insets.top + insets.bottom );
		}
		return result;
	}

	public Rectangle getSelectedBounds(){
		return componentPanel.getBounds();
	}

	public void setSelectedBounds( Rectangle bounds ){
		componentPanel.setBounds( bounds );
	}

	public int getSelectedIndex(){
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
		if( getSelectedDockable() != dockable ) {
			super.setSelectedDockable( dockable );

			for( Meta entry : components.values() ) {
				entry.component.setVisible( entry.dockable == dockable );
			}
		}
	}

	public Rectangle getBoundsAt( int index ){
		T tab = getTab( getDockable( index ) );
		if( tab == null )
			return null;

		return tab.getBounds();
	}

	public int getIndexOfTabAt( Point mouseLocation ){
		return getLayoutManager().getIndexOfTabAt( this, mouseLocation );
	}
	
	public int getTabCount(){
		return getDockableCount();
	}

	public void addTab( String title, Icon icon, Component comp, Dockable dockable ){
		insertTab( title, icon, comp, dockable, getTabCount() );
	}

	public void insertTab( String title, Icon icon, Component comp, Dockable dockable, int index ){
		Component between = createLayerAt( comp, dockable );
		Meta meta = new Meta( dockable, between, title, icon, null, !tabDisabling.isDisabled( dockable ) );
		components.put( dockable, meta );
		componentPanel.add( between );

		insert( index, dockable );

		meta.forward();

		meta.component.setVisible( getSelectedDockable() == dockable );
		
		tabDisabling.add( dockable );
	}

	public Dimension getMinimumSize( Dockable dockable ){
		return components.get( dockable ).getComponent().getMinimumSize();
	}
	
	public Dimension getPreferredSize( Dockable dockable ){
		return components.get( dockable ).getComponent().getPreferredSize();
	}
	
	public Dockable getDockableAt( int index ){
		return getDockable( index );
	}
	
	public DockElementRepresentative getTabAt( int index ){
		return getTab( getDockableAt( index ) );
	}

	public void moveTab( int source, int destination ){
		if( source == destination ) {
			return;
		}
		if( destination < 0 || destination >= getTabCount() ) {
			throw new ArrayIndexOutOfBoundsException();
		}
		int selected = getSelectedIndex();

		move( source, destination );

		if( selected == source ) {
			selected = destination;
		}
		else if( selected > source && selected <= destination ) {
			selected++;
		}

		setSelectedIndex( selected );
	}

	@Override
	public void remove( int index ){
		Dockable dockable = getDockable( index );
		tabDisabling.remove( dockable );
		super.remove( index );
		Meta meta = components.remove( dockable );
		meta.setController( null );
		componentPanel.remove( meta.component );
		meta.component.setVisible( true );
	}

	@Override
	public void removeAll(){
		super.removeAll();
		for( Meta meta : components.values() ) {
			componentPanel.remove( meta.component );
			meta.setController( null );
			meta.component.setVisible( true );
			tabDisabling.remove( meta.getDockable() );
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
		tab.setEnabled( meta.enabled );

		return tab;
	}
	
	@Override
	public T getOnTab( Dockable dockable ){
		T tab = super.getOnTab( dockable );
		Meta meta = components.get( dockable );

		tab.setIcon( meta.icon );
		tab.setText( meta.text );
		tab.setTooltip( meta.tooltip );
		tab.setEnabled( meta.enabled );

		return tab;
	}
	
	@Override
	protected T putTab( Dockable dockable, T tab ){
		T result = super.putTab( dockable, tab );
		fireTabChanged( dockable );
		return result;
	}
	
	@Override
	protected T removeTab( Dockable dockable ){
		T result = super.removeTab( dockable );
		if( result != null ){
			fireTabChanged( dockable );
		}
		return result;
	}
	
	@Override
	protected void clearTabs(){
		super.clearTabs();
		for( int i = 0, n = getDockableCount(); i<n; i++ ){
			fireTabChanged( getDockable( i ) );
		}
	}

	protected void addToMenu( M menu, Dockable dockable ){
		int index = menu.getDockableCount();
		menu.insert( index, dockable );

		Meta meta = components.get( dockable );
		menu.setIcon( index, meta.icon );
		menu.setText( index, meta.text );
		menu.setTooltip( index, meta.tooltip );
		menu.setEnabled( index, meta.enabled );
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
	 */
	public Component getLayerAt( int index ){
		return getContentAt( index ).component;
	}

	/**
	 * Gets the meta information about the components at location <code>index</code>
	 * @param index the index of a tab
	 * @return information about that tab
	 */
	protected Meta getContentAt( int index ){
		return components.get( getDockable( index ) );
	}

	public void setTitleAt( int index, String newTitle ){
		Meta meta = components.get( getDockable( index ) );
		if( newTitle == null ) {
			meta.text = "";
		}
		else {
			meta.text = newTitle;
		}
		meta.forward();
	}

	public void setTooltipAt( int index, String newTooltip ){
		Meta meta = components.get( getDockable( index ) );
		meta.tooltip = newTooltip;
		meta.forward();
	}

	public void setIconAt( int index, Icon newIcon ){
		Meta meta = components.get( getDockable( index ) );
		meta.icon = newIcon;
		meta.forward();
	}

	public void setComponentAt( int index, Component component ){
		Meta meta = components.get( getDockable( index ) );

		componentPanel.remove( meta.component );
		meta.component = createLayerAt( component, meta.dockable );
		componentPanel.add( meta.component );

		meta.component.setVisible( getSelectedDockable() == meta.dockable );
		revalidate();
	}
	
	/**
	 * Changes the enabled state of the item <code>dockable</code>. 
	 * @param dockable the tab whose state is to be changed
	 * @param enabled the new enabled state
	 */
	protected void setEnabledAt( Dockable dockable, boolean enabled ){
		Meta meta = components.get( dockable );
		if( meta != null && meta.enabled != enabled ){
			meta.enabled = enabled;
			meta.forward();
		}
	}

	public JComponent getComponent(){
		return panel;
	}

	/**
	 * Sets the z order of <code>component</code>, as higher the z order
	 * as later the component is painted, as more components it can overlap.
	 * @param component some child of this pane
	 * @param order the order
	 */
	protected void setZOrder( Component component, int order ){
		panel.setComponentZOrder( component, order );
	}

	/**
	 * Gets the z order of <code>component</code>.
	 * @param component some child of this pane
	 * @return the order
	 */
	protected int getZOrder( Component component ){
		return panel.getComponentZOrder( component );
	}

	public DockElementRepresentative createDefaultRepresentation( final DockElement target ){
		return new SimpleDockElementRepresentative( target, panel );
	}

	/**
	 * Creates a {@link BorderForwarder} for the content component for the tab of <code>dockable</code>
	 * @param dockable the item that is shown
	 * @param component the component which is influenced
	 * @return the forwarder or null
	 */
	protected BorderForwarder createContentBorderModifier( Dockable dockable, JComponent component ){
		return null;
	}

	/**
	 * Meta information about a {@link Dockable} that is shown on this 
	 * {@link CombinedStackDockComponent}.
	 * @author Benjamin Sigg
	 */
	protected class Meta {
		/** the element that is shown */
		private Dockable dockable;
		/** visual representation of {@link #dockable} */
		private Component component;
		/** text to be displayed on the tab of {@link #dockable} */
		private String text;
		/** icon to be displayed on the tab of {@link #dockable} */
		private Icon icon;
		/** tooltip to be displayed on the tab of {@link #dockable} */
		private String tooltip;
		/** sets the border of {@link #component} */
		private BorderForwarder border;
		/** whether this tab is enabled */
		private boolean enabled = true;
		
		/**
		 * Creates new meta information.
		 * @param dockable the element for which the meta information is required
		 * @param component graphical representation of <code>dockable</code>
		 * @param text text to be shown in the tab
		 * @param icon icon to be shown in the tab
		 * @param tooltip tooltip to be shown on the tab
		 * @param enabled whether this tab is enabled or not
		 */
		public Meta( Dockable dockable, Component component, String text, Icon icon, String tooltip, boolean enabled ){
			this.dockable = dockable;
			this.component = component;
			this.text = text;
			this.icon = icon;
			this.tooltip = tooltip;
			this.enabled = enabled;

			if( component instanceof JComponent ) {
				border = createContentBorderModifier( dockable, (JComponent) component );
			}
			
			setController( getController() );
		}
		
		/**
		 * Sets the controller which is used to read values.
		 * @param controller the new controller
		 */
		@FrameworkOnly
		public void setController( DockController controller ){
			if( border != null ){
				border.setController( controller );
			}
		}

		/**
		 * Gets the element which is represented by this object
		 * @return the dockable
		 */
		public Dockable getDockable(){
			return dockable;
		}

		/**
		 * Gets the component that is shown.
		 * @return the component to show
		 */
		public Component getComponent(){
			return component;
		}

		/**
		 * Sets the border of the component. This method assumes that
		 * {@link CombinedStackDockComponent#createContentBorderModifier(Dockable, JComponent)} returns
		 * a non-<code>null</code> value.
		 * @param border the new border, can be <code>null</code>
		 */
		public void setBorder( Border border ){
			if( this.border == null ) {
				throw new IllegalStateException( "there was no border-modifier created" );
			}
			this.border.setBorder( border );
		}

		/**
		 * Searches {@link CombinedTab} or {@link CombinedMenu} of {@link #dockable}
		 * and updates {@link #text}, {@link #icon} and {@link #tooltip}.
		 */
		public void forward(){
			CombinedTab tab = getTab( dockable );
			if( tab != null ) {
				tab.setIcon( icon );
				tab.setText( text );
				tab.setTooltip( tooltip );
				tab.setEnabled( enabled );
			}
			CombinedMenu menu = getMenu( dockable );
			if( menu != null ) {
				Dockable[] dockables = menu.getDockables();
				for( int i = 0; i < dockables.length; i++ ) {
					if( dockables[i] == dockable ) {
						menu.setIcon( i, icon );
						menu.setText( i, text );
						menu.setTooltip( i, tooltip );
						menu.setEnabled( i, enabled );
						break;
					}
				}
			}
		}
	}

	/**
	 * Represents the background of this component.
	 * @author Benjamin Sigg
	 */
	private class Background extends BackgroundAlgorithm implements TabPaneBackgroundComponent {
		/**
		 * Creates a new object
		 */
		public Background(){
			super( TabPaneBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".tabPane" );
		}

		public TabPane getPane(){
			return CombinedStackDockComponent.this;
		}

		public Component getComponent(){
			return panel;
		}
	}
}

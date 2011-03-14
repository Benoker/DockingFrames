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
package bibliothek.extension.gui.dock.theme.eclipse.displayer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseMenu;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTab;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabInfo;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.displayer.DisplayerBackgroundComponent;
import bibliothek.gui.dock.displayer.DisplayerDockBorder;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentRepresentative;
import bibliothek.gui.dock.station.stack.TabContent;
import bibliothek.gui.dock.station.stack.TabContentFilterListener;
import bibliothek.gui.dock.station.stack.tab.TabContentFilter;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.border.BorderForwarder;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.UIValue;

/**
 * This displayer paints a tab instead of a {@link DockTitle} (if the framework sets a title, then this
 * title is ignored). 
 * @author Janni Kovacs
 */
public class EclipseDockableDisplayer extends EclipseTabPane implements DockableDisplayer {
	private DockStation station;
	
	private Dockable dockable;
	private DockTitle title;
	private Location location;
	
	private List<DockableDisplayerListener> listeners = new ArrayList<DockableDisplayerListener>();
	private TitleBarObserver observer;
	private StackDockComponentRepresentative representative;
	
	private PropertyValue<TabPlacement> tabPlacement = new PropertyValue<TabPlacement>( StackDockStation.TAB_PLACEMENT ){
		@Override
		protected void valueChanged( TabPlacement oldValue, TabPlacement newValue ){
			setTabPlacement( newValue );
		}
	};
	
	private PropertyValue<TabContentFilter> filter = new PropertyValue<TabContentFilter>( StackDockStation.TAB_CONTENT_FILTER ){
		@Override
		protected void valueChanged( TabContentFilter oldValue, TabContentFilter newValue ){
			if( oldValue != null ){
				oldValue.uninstall( EclipseDockableDisplayer.this );
				oldValue.removeListener( filterListener );
			}
			if( newValue != null ){
				newValue.install( EclipseDockableDisplayer.this );
				newValue.addListener( filterListener );
			}
			
			updateTabContent();
		}
	};
	
	private DockableListener dockableListener = new DockableAdapter() {
		public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ){			
			updateTabContent();
		}
		
		public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ){
			updateTabContent();
		}
		
		public void titleToolTipChanged( Dockable dockable, String oldTooltip, String newTooltip ){
			updateTabContent();
		}
	};
	
	private TabContentFilterListener filterListener = new TabContentFilterListener() {
		public void contentChanged(){
			updateTabContent();
		}
		
		public void contentChanged( StackDockComponent component ){
			if( component == EclipseDockableDisplayer.this ){
				updateTabContent();
			}
		}
		
		public void contentChanged( StackDockStation station ){
			// ignore
		}
		
		public void contentChanged( Dockable dockable ){
			if( dockable == EclipseDockableDisplayer.this.dockable ){
				updateTabContent();
			}
		}
	};
	
	/**
	 * Creates a new {@link DockableDisplayer}.
	 * @param theme the theme which creates this displayer, not <code>null</code>
	 * @param station the parent of this displayer, not <code>null</code>
	 * @param dockable the element shown on this displayer, may be <code>null</code>
	 */
	public EclipseDockableDisplayer( EclipseTheme theme, DockStation station, Dockable dockable ){
		super(theme, station);
		
		observer = new TitleBarObserver( station, dockable, TitleBar.ECLIPSE ){
			@Override
			protected void invalidated(){
				for( DockableDisplayerListener listener : displayerListeners() ){
					listener.discard( EclipseDockableDisplayer.this );
				}
			}
		};
		
		this.station = station;
		representative = new StackDockComponentRepresentative();
		representative.setComponent( this );
		setDockable(dockable);
		
		getComponent().setFocusCycleRoot( true );
	}

	@Override
	public Dimension getMinimumSize(){
		if( dockable == null )
			return new Dimension( 10, 10 );
		else
			return dockable.getComponent().getMinimumSize();
	}
	
	@Override
	public Dimension getPreferredSize(){
		if( dockable == null )
			return new Dimension( 10, 10 );
		else
			return dockable.getComponent().getMinimumSize();
	}
	
	public void addDockableDisplayerListener( DockableDisplayerListener listener ){
		listeners.add( listener );	
	}
	
	public void removeDockableDisplayerListener( DockableDisplayerListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets all {@link DockableDisplayerListener} known to this displayer.
	 * @return the list of listeners
	 */
	protected DockableDisplayerListener[] displayerListeners(){
		return listeners.toArray( new DockableDisplayerListener[ listeners.size() ] );
	}
	
	public void setDockable( Dockable dockable ){
		if( this.dockable != null ){
			removeAll();
			this.dockable.removeDockableListener( dockableListener );
		}
		this.dockable = dockable;
		if( dockable != null ){
			TabContent content = new TabContent( dockable.getTitleIcon(), dockable.getTitleText(), dockable.getTitleToolTip() );
			TabContentFilter contentFilter = filter.getValue();
			if( contentFilter != null ){
				content = contentFilter.filter( content, this, dockable );
			}
			
			if( content == null ){
				addTab( null, null, dockable.getComponent(), dockable );
				setTooltipAt( 0, null );
			}
			else{
				addTab( content.getTitle(), content.getIcon(), dockable.getComponent(), dockable );
				setTooltipAt( 0, content.getTooltip() );
			}
			dockable.addDockableListener( dockableListener );
		}
		if( observer != null ){
			observer.setDockable( dockable );
		}
		representative.setTarget( dockable );
		revalidate();
	}
	
	private void updateTabContent(){
		if( dockable != null && getTabCount() == 1 ){
			TabContent content = new TabContent( dockable.getTitleIcon(), dockable.getTitleText(), dockable.getTitleToolTip() );
			TabContentFilter contentFilter = filter.getValue();
			if( contentFilter != null ){
				content = contentFilter.filter( content, this, dockable );
			}
			if( content == null ){
				setTitleAt( 0, null );
				setIconAt( 0, null );
				setTooltipAt( 0, null );
			}
			else{
				setTitleAt( 0, content.getTitle() );
				setIconAt( 0, content.getIcon() );
				setTooltipAt( 0, content.getTooltip() );
			}
		}
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		if( observer != null ){
			observer.setController( controller );
		}
		tabPlacement.setProperties( controller );
		filter.setProperties( controller );
		representative.setController( controller );
	}

	public boolean titleContains( int x, int y ){
		Point point = new Point( x, y );
		for( int i = 0, n = getTabCount(); i<n; i++ ){
			Rectangle bounds = getBoundsAt( i );
			if( bounds.contains( point ))
				return true;
		}
		return false;
	}
	
	@Override
	protected BackgroundAlgorithm createBackground( CombinedStackDockComponent<EclipseTab, EclipseMenu, EclipseTabInfo> self ){
		return new Background( (EclipseDockableDisplayer)self );
	}
	
	public Insets getDockableInsets() {
	    return getContentInsets();
	}

	public Dockable getDockable(){
		return dockable;
	}

	public DockStation getStation(){
		return station;
	}

	public DockTitle getTitle(){
		return title;
	}

	public Location getTitleLocation(){
		return location;
	}

	public void setStation( DockStation station ){
		this.station = station;
	}

	public void setTitle( DockTitle title ){
		this.title = title;
	}

	public void setTitleLocation( Location location ){
		this.location = location;
	}
	
	@Override
	protected BorderForwarder createBorderModifier( JComponent target ){
		return new DisplayerBorder( target, "eclipse" );
	}
	
	@Override
	protected BorderForwarder createContentBorderModifier( Dockable dockable, JComponent target ){
		return new DisplayerBorder( target, "eclipse.content" );
	}
	
	/**
	 * The {@link UIValue} responsible for painting the background of this displayer.
	 * @author Benjamin Sigg
	 */
	private static class Background extends BackgroundAlgorithm implements DisplayerBackgroundComponent{
		/** the displayer whose background is painted by this {@link UIValue} */
		private EclipseDockableDisplayer displayer;
		
		/**
		 * Creates a new object
		 * @param displayer the displayer which will be managed by this object 
		 */
		public Background( EclipseDockableDisplayer displayer ){
			super( DisplayerBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".displayer" );
			this.displayer = displayer;
		}
		
		public DockableDisplayer getDisplayer(){
			return displayer;
		}

		public Component getComponent(){
			return getDisplayer().getComponent();
		}
	}
	
	/**
	 * Used by this {@link EclipseDockableDisplayer} to set the border.
	 * @author Benjamin Sigg
	 */
	private class DisplayerBorder extends BorderForwarder implements DisplayerDockBorder{
		public DisplayerBorder( JComponent target, String suffix ){
			super( DisplayerDockBorder.KIND, ThemeManager.BORDER_MODIFIER + ".displayer." + suffix, target );
		}
		
		public DockableDisplayer getDisplayer(){
			return EclipseDockableDisplayer.this;
		}
	}
}

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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.BorderedComponent;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.InvisibleTab;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.InvisibleTabPane;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPainter;
import bibliothek.extension.gui.dock.util.ReverseCompoundBorder;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.displayer.DisplayerBackgroundComponent;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.displayer.DisplayerDockBorder;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * A {@link DockableDisplayer} which is not able to show the {@link DockTitle} of
 * its {@link Dockable}. This displayer exchanges automatically its border
 * using the global {@link TabPainter} delivered through the {@link DockProperties}
 * and the key {@link EclipseTheme#TAB_PAINTER}.
 * @author Janni Kovacs
 */
public class NoTitleDisplayer extends ConfiguredBackgroundPanel implements DockableDisplayer, InvisibleTabPane, BorderedComponent {
	private Dockable dockable;
	private DockController controller;
	private DockStation station;
	private DockTitle title;
	private Location location;
	
	private PropertyValue<TabPainter> painter;
	
	private boolean defaultBorderHint;
	private Boolean borderHint;
	private DockableDisplayerHints hints;
	
	private boolean bordered;
	private boolean respectHints;
	
	private TitleBarObserver observer;
	
	private List<DockableDisplayerListener> listeners = new ArrayList<DockableDisplayerListener>();
	
	private InvisibleTab invisibleTab;
	
	private Border innerBorder;
	private Border outerBorder;
	
	private DisplayerBorder innerBorderModifier = new DisplayerBorder( "in" );
	private DisplayerBorder outerBorderModifier = new DisplayerBorder( "out" );
	
	private Background background = new Background();
	
	public NoTitleDisplayer( DockStation station, Dockable dockable, TitleBar bar ){
		super( false, true );
		setLayout( new GridLayout( 1, 1, 0, 0 ) );
		setBackground( background );
		
		bordered = bar == TitleBar.NONE_BORDERED || bar == TitleBar.NONE_HINTED_BORDERED;
		respectHints = bar == TitleBar.NONE_HINTED || bar == TitleBar.NONE_HINTED_BORDERED;
		
		observer = new TitleBarObserver( station, dockable, bar ){
			@Override
			protected void invalidated(){
				for( DockableDisplayerListener listener : listeners() ){
					listener.discard( NoTitleDisplayer.this );
				}
			}
		};
		
		if( respectHints ){
		    hints = new DockableDisplayerHints(){
		        public void setShowBorderHint( Boolean border ) {
		            borderHint = border;
		            updateFullBorder();
		        }
		    };
		}
		
		setStation( station );
        setDockable( dockable );
        setBorder( null );
        
        defaultBorderHint = bordered;
        
		painter = new PropertyValue<TabPainter>( EclipseTheme.TAB_PAINTER ){
			@Override
			protected void valueChanged( TabPainter oldValue, TabPainter newValue ) {
				updateFullBorder();
				updateInvisibleTab();
			}
		};
		
		updateFullBorder();
		updateInvisibleTab();
		
		setFocusCycleRoot( true );
		setFocusTraversalPolicy( new LayoutFocusTraversalPolicy() );
	}
	
	/**
	 * Gets the current hint whether a border should be shown or not.
	 * @return the current hint
	 */
	protected boolean getBorderHint(){
	    if( borderHint != null )
	        return borderHint.booleanValue();
	    
	    return defaultBorderHint;
	}
	
	/**
	 * Exchanges the border of this component, using the current
	 * {@link EclipseTheme#TAB_PAINTER} to determine the new border.
	 */
	public void updateFullBorder(){
	    if( (bordered || respectHints) && painter != null ){
    	    TabPainter painter = this.painter.getValue();
    	    
            if( controller == null || painter == null || dockable == null ){
                outerBorder = null;
            }
            else{
                if( hints == null || getBorderHint() ){
                    outerBorder = painter.getFullBorder( this, controller, dockable );
                }
                else{
                    outerBorder = null;
                }
            }
            updateBorder();
	    }
	}
	
	public void setBorder( Dockable dockable, Border border ){
		if( dockable != this.dockable )
			throw new IllegalArgumentException( "unknown dockable: " + dockable );
		
		if( bordered || respectHints ){
			if( hints == null || getBorderHint() ){
				innerBorder = border;
				updateBorder();		
			}
		}
	}
	
	private void updateBorder(){
		Border innerBorder = innerBorderModifier.modify( this.innerBorder );
		Border outerBorder = outerBorderModifier.modify( this.outerBorder );
		
		if( innerBorder == null && outerBorder == null )
			setBorder( null );
		else if( innerBorder == null )
			setBorder( outerBorder );
		else if( outerBorder == null )
			setBorder( innerBorder );
		else
			setBorder( new ReverseCompoundBorder( outerBorder, innerBorder ) );
	}

	protected void updateInvisibleTab(){
		if( invisibleTab != null ){
			invisibleTab.setController( null );
			invisibleTab = null;
		}
		
		if( dockable != null && painter != null ){
			TabPainter painter = this.painter.getValue();
			if( painter != null ){
				invisibleTab = painter.createInvisibleTab( this, dockable );
				invisibleTab.setController( getController() );
			}
		}
	}
	
	public TabPlacement getDockTabPlacement(){
		if( controller == null )
			return null;
		return controller.getProperties().get( StackDockStation.TAB_PLACEMENT );
	}
	
	public Insets getDockableInsets() {
	    Insets insets = getInsets();
	    if( insets == null )
	        return new Insets( 0,0,0,0 );
	    return insets;
	}
	
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintBorder(g);
    }
    
	public Component getComponent(){
		return this;
	}

	public DockController getController(){
		return controller;
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
	
	public void addDockableDisplayerListener( DockableDisplayerListener listener ){
		listeners.add( listener );
	}
	
	public void removeDockableDisplayerListener( DockableDisplayerListener listener ){
		listeners.remove( listener );	
	}
	
	/**
	 * Gets all listeners currently known to this displayer.
	 * @return the list of listeners
	 */
	protected DockableDisplayerListener[] listeners(){
		return listeners.toArray( new DockableDisplayerListener[ listeners.size() ] );
	}
	
	public Dockable getSelectedDockable(){
		return dockable;
	}

	public void setController( DockController controller ){
		DockController oldController = this.controller;
		this.controller = controller;
		if( painter != null )
		    painter.setProperties( controller == null ? null : controller.getProperties() );
		
		if( observer != null )
			observer.setController( controller );
		
		if( invisibleTab != null )
			invisibleTab.setController( controller );
		
		background.setController( controller );
		innerBorderModifier.setController( oldController, controller );
		outerBorderModifier.setController( oldController, controller );
		
		updateFullBorder();
	}

	public void setDockable( Dockable dockable ){
	    if( this.dockable != null )
	        this.dockable.configureDisplayerHints( null );
	    
	    if( invisibleTab != null ){
	    	invisibleTab.setController( null );
	    	invisibleTab = null;
	    }
	    
		this.dockable = dockable;
				
		if( observer != null ){
			observer.setDockable( dockable );
		}
		
		removeAll();
		if( dockable != null ){
			add( dockable.getComponent() );
			dockable.configureDisplayerHints( hints );
		}
		
		updateFullBorder();
		updateInvisibleTab();
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

	public boolean titleContains( int x, int y ){
		return false;
	}

	/**
	 * As the {@link NoTitleDisplayer} does not show any decorations, there is no need to
	 * prepare for a combination. Hence this method always returns <code>null</code>.
	 */
	public DisplayerCombinerTarget prepareCombination( CombinerSource source, Enforcement force ){
		return null;
	}
	
	/**
	 * The background algorithm of this displayer.
	 * @author Benjamin Sigg
	 */
	private class Background extends BackgroundAlgorithm implements DisplayerBackgroundComponent{
		/**
		 * Creates a new object.
		 */
		public Background(){
			super( DisplayerBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".displayer" );
		}
		
		public Component getComponent(){
			return getDisplayer().getComponent();
		}
		
		public DockableDisplayer getDisplayer(){
			return NoTitleDisplayer.this;
		}
	}
	
	/**
	 * A wrapper around a {@link BorderModifier} that is used by this {@link NoTitleDisplayer}.
	 * @author Benjamin Sigg
	 */
	private class DisplayerBorder implements DisplayerDockBorder{
		private String suffix;
		private BorderModifier modifier;
		
		/**
		 * Creates a new wrapper.
		 * @param suffix the suffix of the identifier
		 */
		public DisplayerBorder( String suffix ){
			this.suffix = suffix;
		}
		
		public DockableDisplayer getDisplayer(){
			return NoTitleDisplayer.this;
		}

		/**
		 * Switches the controller which is to be monitored for a value.
		 * @param oldController the old controller, can be <code>null</code>
		 * @param newController the new controller, can be <code>null</code>
		 */
		public void setController( DockController oldController, DockController newController ){
			if( oldController != null ){
				oldController.getThemeManager().remove( this );
			}
			if( newController != null ){
				String id = ThemeManager.BORDER_MODIFIER + ".displayer.eclipse.no_title." + suffix;
				newController.getThemeManager().add( id, DisplayerDockBorder.KIND, ThemeManager.BORDER_MODIFIER_TYPE, this );
			}
			else{
				set( null );
			}
		}
		
		public void set( BorderModifier value ){
			if( value != modifier ){
				modifier = value;
				updateBorder();
			}
		}
		
		/**
		 * Modifies <code>border</code> and returns a new border.
		 * @param border the border to modify, can be <code>null</code>
		 * @return the new border, can be <code>null</code>
		 */
		public Border modify( Border border ){
			if( modifier == null ){
				return border;
			}
			else{
				return modifier.modify( border );
			}
		}
	}
}

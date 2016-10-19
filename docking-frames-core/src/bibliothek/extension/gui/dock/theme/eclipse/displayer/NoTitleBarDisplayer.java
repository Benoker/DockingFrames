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

import java.awt.Graphics;

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
import bibliothek.gui.dock.displayer.DisplayerDockBorder;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayerDecorator;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.Transparency;

/**
 * A {@link DockableDisplayer} which is used in situations when usually no {@link DockTitle} nor any tab is shown. This
 * displayer makes use of an {@link InvisibleTab} to change its border. The invisible tab is accessed through the
 * global {@link TabPainter} which in respect is made available by the {@link DockProperties} and the key {@link EclipseTheme#TAB_PAINTER}.
 * @author Janni Kovacs
 * @author Benjamin Sigg
 */
public class NoTitleBarDisplayer extends BasicDockableDisplayer implements DockableDisplayer, InvisibleTabPane, BorderedComponent {
	private PropertyValue<TabPainter> painter;
	
	private TitleBarObserver observer;
	
	private InvisibleTab invisibleTab;
	
	private Border innerBorder;
	private Border outerBorder;
	
	private DisplayerBorder innerBorderModifier = new DisplayerBorder( "in" );
	private DisplayerBorder outerBorderModifier = new DisplayerBorder( "out" );
	
	public NoTitleBarDisplayer( DockStation station, Dockable dockable, DockTitle title, TitleBar bar ){
		super( station );
		setDockable( dockable );
		setTitle( title );
		
		setTransparency( Transparency.TRANSPARENT );
		
		boolean bordered = bar == TitleBar.NONE_BORDERED || bar == TitleBar.NONE_HINTED_BORDERED;
		boolean respectHints = bar == TitleBar.NONE_HINTED || bar == TitleBar.NONE_HINTED_BORDERED;
		
		setRespectBorderHint( respectHints );
		setDefaultBorderHint( bordered );
		
		observer = new TitleBarObserver( station, dockable, bar ){
			@Override
			protected void invalidated(){
				for( DockableDisplayerListener listener : listeners() ){
					listener.discard( NoTitleBarDisplayer.this );
				}
			}
		};
		
        painter = new PropertyValue<TabPainter>( EclipseTheme.TAB_PAINTER ){
			@Override
			protected void valueChanged( TabPainter oldValue, TabPainter newValue ) {
				updateFullBorder();
				updateInvisibleTab();
			}
		};
		
		updateFullBorder();
		updateInvisibleTab();
	}
	
	private boolean getBorderHint(){
		if( isRespectBorderHint() ){
			return getHints().getShowBorderHint();
		}
		else{
			return getDefaultBorderHint();
		}
	}
	
	/**
	 * Exchanges the border of this component, using the current
	 * {@link EclipseTheme#TAB_PAINTER} to determine the new border.
	 */
	public void updateFullBorder(){
	    if( (isRespectBorderHint() ||  getDefaultBorderHint()) && painter != null ){
    	    TabPainter painter = this.painter.getValue();
    	    Dockable dockable = getDockable();
    	    DockController controller = getController();
    	    
            if( controller == null || painter == null || dockable == null ){
                outerBorder = null;
            }
            else{
                if( getBorderHint() ){
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
		if( dockable != getDockable() )
			throw new IllegalArgumentException( "unknown dockable: " + dockable );
		
		if( getDefaultBorderHint() || isRespectBorderHint() ){
			if( getBorderHint() ){
				innerBorder = border;
				updateBorder();		
			}
		}
	}
	
	@Override
	protected Border getDefaultBorder(){
		Border innerBorder = innerBorderModifier.modify( this.innerBorder );
		Border outerBorder = outerBorderModifier.modify( this.outerBorder );
		
		if( innerBorder == null && outerBorder == null )
			return null;
		else if( innerBorder == null )
			return outerBorder;
		else if( outerBorder == null )
			return innerBorder;
		else
			return new ReverseCompoundBorder( outerBorder, innerBorder );
	}

	protected void updateInvisibleTab(){
		if( invisibleTab != null ){
			invisibleTab.setController( null );
			invisibleTab = null;
		}
		
		Dockable dockable = getDockable();
		if( dockable != null && painter != null ){
			TabPainter painter = this.painter.getValue();
			if( painter != null ){
				invisibleTab = painter.createInvisibleTab( this, dockable );
				invisibleTab.setController( getController() );
			}
		}
	}
	
	public TabPlacement getDockTabPlacement(){
		DockController controller = getController();
		if( controller == null )
			return null;
		return controller.getProperties().get( StackDockStation.TAB_PLACEMENT );
	}
	
    @Override
    public void paint( Graphics g ){
    	// make sure the border is painted over the children
        super.paint( g );
        paintBorder( g );
    }
	
	public Dockable getSelectedDockable(){
		return getDockable();
	}

	@Override
	public void setController( DockController controller ){
		DockController oldController = getController();
		super.setController( oldController );
		
		if( painter != null )
		    painter.setProperties( controller == null ? null : controller.getProperties() );
		
		if( observer != null )
			observer.setController( controller );
		
		if( invisibleTab != null )
			invisibleTab.setController( controller );
		
		innerBorderModifier.setController( oldController, controller );
		outerBorderModifier.setController( oldController, controller );
		
		updateFullBorder();
	}

	@Override
	public void setDockable( Dockable dockable ){
	    if( invisibleTab != null ){
	    	invisibleTab.setController( null );
	    	invisibleTab = null;
	    }
	    	
		if( observer != null ){
			observer.setDockable( dockable );
		}
		
		super.setDockable( dockable );
		
		updateInvisibleTab();
	}
	
	@Override
	protected BasicDockableDisplayerDecorator createTabDecorator(){
		if( isStacked() ){
			return createStackedDecorator();
		}
		else{
			return createMinimalDecorator();
		}
	}

	/**
	 * A wrapper around a {@link BorderModifier} that is used by this {@link NoTitleBarDisplayer}.
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
			return NoTitleBarDisplayer.this;
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

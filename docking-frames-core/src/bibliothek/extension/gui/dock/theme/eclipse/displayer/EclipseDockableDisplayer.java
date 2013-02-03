/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayerDecorator;
import bibliothek.gui.dock.title.DockTitle;

/**
 * The {@link EclipseDockableDisplayer} is used to present {@link Dockable}s with the {@link TitleBar#ECLIPSE} 
 * look. This class also supports showing a {@link DockTitle}.
 * @author Benjamin Sigg
 */
public class EclipseDockableDisplayer extends BasicDockableDisplayer{
	private TitleBarObserver observer;
	
	/**
	 * Creates a new {@link DockableDisplayer}.
	 * @param theme the theme which creates this displayer, not <code>null</code>
	 * @param station the parent of this displayer, not <code>null</code>
	 * @param dockable the element shown on this displayer, may be <code>null</code>
	 * @param title the title that is shown on this displayer, usually <code>null</code>
	 */
	public EclipseDockableDisplayer( EclipseTheme theme, DockStation station, Dockable dockable, DockTitle title ){
		super( station, false );
		
		observer = new TitleBarObserver( station, dockable, TitleBar.ECLIPSE ){
			@Override
			protected void invalidated(){
				for( DockableDisplayerListener listener : listeners() ){
					listener.discard( EclipseDockableDisplayer.this );
				}
			}
		};
		
		init( station, dockable, title, Location.TOP );
		
        setDefaultBorderHint( false );
        setRespectBorderHint( true );
        setSingleTabShowInnerBorder( false );
        setSingleTabShowOuterBorder( false );
        
        updateDecorator( true );
	}
	
	@Override
	protected Border getDefaultBorder(){
		return BorderFactory.createEmptyBorder();
	}
	
	@Override
	public void setDockable( Dockable dockable ){
		super.setDockable( dockable );
		observer.setDockable( dockable );
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		observer.setController( controller );
	}
	
	@Override
	protected BasicDockableDisplayerDecorator createMinimalDecorator(){
		return createTabDecorator();
	}
	
	@Override
	protected BasicDockableDisplayerDecorator createStackedDecorator(){
		return createTabDecorator();
	}
}

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
package bibliothek.extension.gui.dock.theme;

import bibliothek.extension.gui.dock.theme.eclipse.*;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.*;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.themes.nostack.NoStackAcceptance;
import bibliothek.gui.dock.title.*;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;

/**
 * A theme imitating the look and feel of the Eclipse-IDE.
 * @author Janni Kovacs
 */
@ThemeProperties(
		authors = {"Janni Kovacs", "Benjamin Sigg"},
		descriptionBundle = "theme.eclipse.description",
		nameBundle = "theme.eclipse",
		webpages = {""})
public class EclipseTheme extends BasicTheme {
	/** Tells whether icons on tabs that are not selected should be painted or not. */
	public static final PropertyKey<Boolean> PAINT_ICONS_WHEN_DESELECTED = 
		new PropertyKey<Boolean>( "EclipseTheme paint icons when deselected", false );
	
	/**
	 *  Tells in which way the tabs should be painted.
	 *  @see ShapedGradientPainter
	 *  @see RectGradientPainter 
	 */
	public static final PropertyKey<TabPainter> TAB_PAINTER =
		new PropertyKey<TabPainter>( "EclipseTheme tab painter", ShapedGradientPainter.FACTORY );
	
	/**
	 * Provides additional dockable-wise information used to layout components
	 * in the EclipseTheme. Note that changing this property will show full effect
	 * only after re-installing the EclipseTheme.
	 * @see DefaultEclipseThemeConnector
	 */
	public static final PropertyKey<EclipseThemeConnector> THEME_CONNECTOR =
		new PropertyKey<EclipseThemeConnector>( 
		        "EclipseTheme theme connector",
		        new DefaultEclipseThemeConnector() );
	
	/**
	 * The id of the {@link DockTitleVersion} that is intended to create
	 * {@link DockTitle}s used as tabs by the {@link DockTitleTab}. Clients
	 * which want to use {@link DockTitle}s as tabs, should exchange the
	 * {@link TabPainter} by executing this code:<br>
	 * <code>controller.getProperties().set( EclipseTheme.TAB_PAINTER, DockTitleTab.FACTORY );</code>
	 */
	public static final String TAB_DOCK_TITLE = "eclipse.tab";

	/** An acceptance that permits combinations of dockables and stations that do not look good */
	private DockAcceptance acceptance = new NoStackAcceptance();
	
	/**
	 * Creates a new theme
	 */
	public EclipseTheme() {
		setStackDockComponentFactory( new StackDockComponentFactory(){
			public StackDockComponent create( StackDockStation station ){
				return new EclipseStackDockComponent( EclipseTheme.this, station );
			}
		});
		setDisplayerFactory( new EclipseDisplayerFactory( this ) );
		setPaint( new EclipseStationPaint() );
		setMovingImageFactory( new DockableMovingImageFactory(){
		    public MovingImage create( DockController controller, Dockable dockable ) {
		        return null;
		    }
		    
		    public MovingImage create( DockController controller, DockTitle snatched ) {
		        return null;
		    }
		});
		setTitleFactory( new BasicDockTitleFactory(){
		    @Override
		    public <D extends Dockable & DockStation> DockTitle createStationTitle(
		            D dockable, DockTitleVersion version ) {
		        
		        return createDockableTitle( dockable, version );
		    }
		});
	}

	@Override
	public void install( DockController controller ){
	    DockTitleManager titleManager = controller.getDockTitleManager();
	    titleManager.registerTheme( EclipseTheme.TAB_DOCK_TITLE, BasicTabDockTitle.createFactory( this ) );
	    
		super.install( controller );
		
		EclipseDockTitleFactory factory = new EclipseDockTitleFactory( this, new ControllerTitleFactory() );
		
		titleManager.registerTheme( SplitDockStation.TITLE_ID, factory );
		titleManager.registerTheme( FlapDockStation.WINDOW_TITLE_ID, factory );
		titleManager.registerTheme( ScreenDockStation.TITLE_ID, factory );
		titleManager.registerTheme( StackDockStation.TITLE_ID, factory );
		
		controller.addAcceptance( acceptance );
	}
	
	@Override
	public void uninstall( DockController controller ){
		super.uninstall( controller );
		controller.getDockTitleManager().clearThemeFactories();
		controller.removeAcceptance( acceptance );
	}

	/**
	 * Gets the connector which is used for decisions which are normally
	 * altered by the client.
	 * @param controller the controller in whose realm the decisions will take
	 * effect.
	 * @return the connector, either the connector that is installed in
	 * the {@link DockProperties} under {@link #THEME_CONNECTOR} or
	 * a default-value.
	 */
	public EclipseThemeConnector getThemeConnector( DockController controller ) {
		EclipseThemeConnector connector = null;
		if( controller != null )
			connector = controller.getProperties().get( THEME_CONNECTOR );
		
		if( connector == null )
		    connector = THEME_CONNECTOR.getDefault();
		
		return connector;
	}
}


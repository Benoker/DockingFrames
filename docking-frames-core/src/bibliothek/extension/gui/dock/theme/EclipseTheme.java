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

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.eclipse.DefaultEclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseButtonTitle;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseColorScheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockTitleFactory;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockableSelection;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseStationPaint;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.RoundRectButton;
import bibliothek.extension.gui.dock.theme.eclipse.RoundRectDropDownButton;
import bibliothek.extension.gui.dock.theme.eclipse.displayer.EclipseDisplayerFactory;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.ArchGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.BasicTabDockTitle;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.DockTitleTab;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.RectGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPainter;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.MenuDockAction;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.station.stack.StackDockComponentParent;
import bibliothek.gui.dock.station.stack.tab.MenuLineLayout;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.themes.basic.BasicDockTitle;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.themes.basic.NoSpanFactory;
import bibliothek.gui.dock.themes.basic.action.BasicButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicMenuHandler;
import bibliothek.gui.dock.themes.basic.action.BasicSelectableHandler;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.nostack.NoStackAcceptance;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.icon.DefaultIconScheme;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;

/**
 * A theme imitating the look and feel of the Eclipse-IDE.
 * @author Janni Kovacs
 * @author Benjamin Sigg
 */
@ThemeProperties(authors = { "Janni Kovacs", "Benjamin Sigg" }, descriptionBundle = "theme.eclipse.description", nameBundle = "theme.eclipse", webpages = { "" })
public class EclipseTheme extends BasicTheme {
	/** Tells whether icons on tabs that are not selected should be painted or not. */
	public static final PropertyKey<Boolean> PAINT_ICONS_WHEN_DESELECTED = new PropertyKey<Boolean>( "EclipseTheme paint icons when deselected", new ConstantPropertyFactory<Boolean>( false ), true );

	/**
	 *  Tells in which way the tabs should be painted.
	 *  @see ArchGradientPainter
	 *  @see RectGradientPainter 
	 */
	public static final PropertyKey<TabPainter> TAB_PAINTER = new PropertyKey<TabPainter>( "EclipseTheme tab painter", new ConstantPropertyFactory<TabPainter>( ArchGradientPainter.FACTORY ), true );

	/**
	 * Provides additional dockable-wise information used to layout components
	 * in the EclipseTheme. Note that changing this property will show full effect
	 * only after re-installing the EclipseTheme.
	 * @see DefaultEclipseThemeConnector
	 */
	public static final PropertyKey<EclipseThemeConnector> THEME_CONNECTOR = new PropertyKey<EclipseThemeConnector>( "EclipseTheme theme connector", new ConstantPropertyFactory<EclipseThemeConnector>( new DefaultEclipseThemeConnector() ), true );

	/** Access to the {@link ColorScheme} used for this theme */
	public static final PropertyKey<ColorScheme> ECLIPSE_COLOR_SCHEME = new PropertyKey<ColorScheme>( "dock.ui.EclipseTheme.ColorScheme", new DynamicPropertyFactory<ColorScheme>(){
		public ColorScheme getDefault( PropertyKey<ColorScheme> key, DockProperties properties ){
			return new EclipseColorScheme();
		}
	}, true );

	/**
	 * The id of the {@link DockTitleVersion} that is intended to create
	 * {@link DockTitle}s used as tabs by the {@link DockTitleTab}. Clients
	 * which want to use {@link DockTitle}s as tabs, should exchange the
	 * {@link TabPainter} by executing this code:<br>
	 * <code>controller.getProperties().set( EclipseTheme.TAB_PAINTER, DockTitleTab.FACTORY );</code>
	 * @deprecated While still possible to use, implementing a custom {@link TabPainter} is the preferred option to
	 * replace tabs
	 */
	@Deprecated
	public static final String TAB_DOCK_TITLE = "eclipse.tab";

	/** An acceptance that permits combinations of dockables and stations that do not look good */
	private DockAcceptance acceptance = new NoStackAcceptance();

	/**
	 * Creates a new theme
	 */
	public EclipseTheme(){
		setColorSchemeKey( ECLIPSE_COLOR_SCHEME );
		setStackDockComponentFactory( new StackDockComponentFactory(){
			public StackDockComponent create( StackDockComponentParent station ){
				return new EclipseTabPane( EclipseTheme.this, station.getStackDockParent() );
			}
		}, Priority.DEFAULT );
		setDisplayerFactory( new EclipseDisplayerFactory( this ), Priority.DEFAULT );
		setPaint( new EclipseStationPaint(), Priority.DEFAULT );
		setMovingImageFactory( new DockableMovingImageFactory(){
			public MovingImage create( DockController controller, Dockable dockable ){
				return null;
			}

			public MovingImage create( DockController controller, DockTitle snatched ){
				return null;
			}
		}, Priority.DEFAULT );
		setTitleFactory( new BasicDockTitleFactory(){
			@Override
			public void request( DockTitleRequest request ){
				request.answer( new BasicDockTitle( request.getTarget(), request.getVersion() ) );
			}
		}, Priority.DEFAULT );
		setDockableSelection( new EclipseDockableSelection(), Priority.DEFAULT );
		setTabPlacement( TabPlacement.TOP_OF_DOCKABLE, Priority.DEFAULT );
		setSpanFactory( new NoSpanFactory() );
	}

	@Override
	public void install( DockController controller ){
		DockTitleManager titleManager = controller.getDockTitleManager();
		titleManager.registerTheme( EclipseTheme.TAB_DOCK_TITLE, BasicTabDockTitle.FACTORY );

		super.install( controller );

		controller.getIcons().setScheme( Priority.THEME, new DefaultIconScheme( "data/bibliothek/gui/dock/core/eclipse/icons.ini", EclipseTheme.class.getClassLoader(), controller ) );

		EclipseDockTitleFactory factory = new EclipseDockTitleFactory( this, new ControllerTitleFactory() );

		titleManager.registerTheme( SplitDockStation.TITLE_ID, factory );
		titleManager.registerTheme( FlapDockStation.WINDOW_TITLE_ID, factory );
		titleManager.registerTheme( ScreenDockStation.TITLE_ID, factory );
		titleManager.registerTheme( StackDockStation.TITLE_ID, factory );

		controller.addAcceptance( acceptance );

		controller.getProperties().set( TabPane.LAYOUT_MANAGER, new MenuLineLayout(), Priority.THEME );

		titleManager.registerTheme( FlapDockStation.BUTTON_TITLE_ID, new DockTitleFactory(){
			public void install( DockTitleRequest request ){
				// ignore	
			}

			public void uninstall( DockTitleRequest request ){
				// ignore	
			}

			public void request( DockTitleRequest request ){
				//	        	request.answer( new FlatButtonTitle( request.getTarget(), request.getVersion() ) );
				request.answer( new EclipseButtonTitle( request.getTarget(), request.getVersion() ) );
			}
		} );

		controller.getActionViewConverter().putTheme( ActionType.BUTTON, ViewTarget.TITLE, new ViewGenerator<ButtonDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ){
				BasicButtonHandler handler = new BasicButtonHandler( action, dockable );
				RoundRectButton button = new RoundRectButton( handler, handler );
				handler.setModel( button.getModel() );
				return handler;
			}
		} );

		controller.getActionViewConverter().putTheme( ActionType.CHECK, ViewTarget.TITLE, new ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
				BasicSelectableHandler.Check handler = new BasicSelectableHandler.Check( action, dockable );
				RoundRectButton button = new RoundRectButton( handler, handler );
				handler.setModel( button.getModel() );
				return handler;
			}
		} );

		controller.getActionViewConverter().putTheme( ActionType.MENU, ViewTarget.TITLE, new ViewGenerator<MenuDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
				BasicMenuHandler handler = new BasicMenuHandler( action, dockable );
				RoundRectButton button = new RoundRectButton( handler, handler );
				handler.setModel( button.getModel() );
				return handler;
			}
		} );

		controller.getActionViewConverter().putTheme( ActionType.RADIO, ViewTarget.TITLE, new ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
				BasicSelectableHandler.Radio handler = new BasicSelectableHandler.Radio( action, dockable );
				RoundRectButton button = new RoundRectButton( handler, handler );
				handler.setModel( button.getModel() );
				return handler;
			}
		} );

		controller.getActionViewConverter().putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE, new ViewGenerator<DropDownAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, DropDownAction action, Dockable dockable ){
				BasicDropDownButtonHandler handler = new BasicDropDownButtonHandler( action, dockable );
				RoundRectDropDownButton button = new RoundRectDropDownButton( handler );
				handler.setModel( button.getModel() );
				return handler;
			}
		} );
	}

	//    @Override
	//    protected void updateColors() {
	//        DockController controller = getController();
	//        if( controller != null && getColorScheme() != null ){
	//            controller.getColors().lockUpdate();
	//
	//            super.updateColors();
	//
	//            updateColor( "stack.tab.border", null );
	//            updateColor( "stack.tab.border.selected", null );
	//            updateColor( "stack.tab.border.selected.focused", null );
	//            updateColor( "stack.tab.border.selected.focuslost", null );
	//
	//            updateColor( "stack.tab.top", null );
	//            updateColor( "stack.tab.top.selected", null );
	//            updateColor( "stack.tab.top.selected.focused", null );
	//            updateColor( "stack.tab.top.selected.focuslost", null );
	//
	//            updateColor( "stack.tab.bottom", null );
	//            updateColor( "stack.tab.bottom.selected", null );
	//            updateColor( "stack.tab.bottom.selected.focused", null );
	//            updateColor( "stack.tab.bottom.selected.focuslost", null );
	//
	//            updateColor( "stack.tab.text", null );
	//            updateColor( "stack.tab.text.selected", null );
	//            updateColor( "stack.tab.text.selected.focused", null );
	//            updateColor( "stack.tab.text.selected.focuslost", null );
	//
	//            updateColor( "stack.border", null );
	//
	//            updateColor( "selection.border", null );
	//
	//            controller.getColors().unlockUpdate();
	//        }
	//        else{
	//            super.updateColors();
	//        }
	//    }

	@Override
	public void uninstall( DockController controller ){
		super.uninstall( controller );
		controller.getIcons().setScheme( Priority.THEME, null );
		controller.getDockTitleManager().clearThemeFactories();
		controller.removeAcceptance( acceptance );
		controller.getProperties().unset( TabPane.LAYOUT_MANAGER, Priority.THEME );
		controller.getActionViewConverter().putTheme( ActionType.BUTTON, ViewTarget.TITLE, null );
		controller.getActionViewConverter().putTheme( ActionType.CHECK, ViewTarget.TITLE, null );
		controller.getActionViewConverter().putTheme( ActionType.MENU, ViewTarget.TITLE, null );
		controller.getActionViewConverter().putTheme( ActionType.RADIO, ViewTarget.TITLE, null );
		controller.getActionViewConverter().putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE, null );
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
	public EclipseThemeConnector getThemeConnector( DockController controller ){
		EclipseThemeConnector connector = null;
		if( controller != null )
			connector = controller.getProperties().get( THEME_CONNECTOR );

		if( connector == null )
			connector = THEME_CONNECTOR.getDefault( null );

		return connector;
	}
}

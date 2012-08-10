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

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.flat.FlatButtonTitle;
import bibliothek.extension.gui.dock.theme.flat.FlatColorScheme;
import bibliothek.extension.gui.dock.theme.flat.FlatDisplayerFactory;
import bibliothek.extension.gui.dock.theme.flat.FlatStationPaint;
import bibliothek.extension.gui.dock.theme.flat.FlatTabPane;
import bibliothek.extension.gui.dock.theme.flat.FlatTitleFactory;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.MenuDockAction;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.dockable.ScreencaptureMovingImageFactory;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.station.stack.StackDockComponentParent;
import bibliothek.gui.dock.station.stack.action.DefaultDockActionDistributor;
import bibliothek.gui.dock.station.stack.action.DockActionDistributor;
import bibliothek.gui.dock.station.stack.tab.MenuLineLayout;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.themes.basic.NoSpanFactory;
import bibliothek.gui.dock.themes.basic.action.BasicButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicMenuHandler;
import bibliothek.gui.dock.themes.basic.action.BasicResourceInitializer;
import bibliothek.gui.dock.themes.basic.action.BasicSelectableHandler;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.gui.dock.themes.basic.action.buttons.BasicMiniButton;
import bibliothek.gui.dock.themes.basic.action.buttons.DropDownMiniButton;
import bibliothek.gui.dock.themes.basic.action.buttons.MiniButton;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;

/**
 * A {@link DockTheme theme} that uses very few borders.
 * @author Benjamin Sigg
 */
@ThemeProperties(nameBundle = "theme.flat", descriptionBundle = "theme.flat.description", authors = { "Benjamin Sigg" }, webpages = {})
public class FlatTheme extends BasicTheme {

	/** A special factory for the {@link SplitDockStation} */
	protected DisplayerFactory splitDisplayFactory = new FlatDisplayerFactory( true );

	/** the key to set the {@link ColorScheme} of this theme */
	public static final PropertyKey<ColorScheme> FLAT_COLOR_SCHEME = new PropertyKey<ColorScheme>( "dock.ui.FlatTheme.ColorScheme", new DynamicPropertyFactory<ColorScheme>(){
		public ColorScheme getDefault( PropertyKey<ColorScheme> key, DockProperties properties ){

			return new FlatColorScheme();
		}
	}, true );

	/**
	 * Key for a property pointing to a {@link DockActionDistributor}. This interface is responsible for distributing
	 * {@link DockAction}s to tabs, titles and info components.
	 */
	public static final PropertyKey<DockActionDistributor> ACTION_DISTRIBUTOR = new PropertyKey<DockActionDistributor>( "flat.DockActionDistributor", new ConstantPropertyFactory<DockActionDistributor>( new DefaultDockActionDistributor() ), true );

	/**
	 * Creates a new theme
	 */
	public FlatTheme(){
		setColorSchemeKey( FLAT_COLOR_SCHEME );
		setPaint( new FlatStationPaint(), Priority.DEFAULT );
		setTitleFactory( new FlatTitleFactory(), Priority.DEFAULT );
		setDisplayerFactory( new FlatDisplayerFactory( false ), Priority.DEFAULT );
		setStackDockComponentFactory( new StackDockComponentFactory(){
			public StackDockComponent create( StackDockComponentParent station ){
				return new FlatTabPane( station );
			}
		}, Priority.DEFAULT );
		setMovingImageFactory( new ScreencaptureMovingImageFactory( new Dimension( 300, 200 ) ), Priority.DEFAULT );
		setTabPlacement( TabPlacement.BOTTOM_OF_DOCKABLE, Priority.DEFAULT );
		setSpanFactory( new NoSpanFactory() );
	}

	@Override
	public void install( DockController controller ){
		super.install( controller );
		controller.getDockTitleManager().registerTheme( FlapDockStation.BUTTON_TITLE_ID, new DockTitleFactory(){
			public void install( DockTitleRequest request ){
				// ignore	
			}

			public void uninstall( DockTitleRequest request ){
				// ignore	
			}

			public void request( DockTitleRequest request ){
				request.answer( new FlatButtonTitle( request.getTarget(), request.getVersion() ) );
			}
		} );

		controller.getProperties().set( TabPane.LAYOUT_MANAGER, new MenuLineLayout(), Priority.THEME );

		controller.getActionViewConverter().putTheme( ActionType.BUTTON, ViewTarget.TITLE, new ViewGenerator<ButtonDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ){
				BasicButtonHandler handler = new BasicButtonHandler( action, dockable );
				MiniButton<BasicButtonModel> button = createTitleMiniButton( handler, handler );
				handler.setModel( button.getModel() );
				return handler;
			}
		} );

		controller.getActionViewConverter().putTheme( ActionType.CHECK, ViewTarget.TITLE, new ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
				BasicSelectableHandler.Check handler = new BasicSelectableHandler.Check( action, dockable );
				MiniButton<BasicButtonModel> button = createTitleMiniButton( handler, handler );
				handler.setModel( button.getModel() );
				return handler;
			}
		} );

		controller.getActionViewConverter().putTheme( ActionType.MENU, ViewTarget.TITLE, new ViewGenerator<MenuDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
				BasicMenuHandler handler = new BasicMenuHandler( action, dockable );
				MiniButton<BasicButtonModel> button = createTitleMiniButton( handler, handler );
				handler.setModel( button.getModel() );
				return handler;
			}
		} );

		controller.getActionViewConverter().putTheme( ActionType.RADIO, ViewTarget.TITLE, new ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
				BasicSelectableHandler.Radio handler = new BasicSelectableHandler.Radio( action, dockable );
				MiniButton<BasicButtonModel> button = createTitleMiniButton( handler, handler );
				handler.setModel( button.getModel() );
				return handler;
			}
		} );

		controller.getActionViewConverter().putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE, new ViewGenerator<DropDownAction, BasicTitleViewItem<JComponent>>(){
			public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, DropDownAction action, Dockable dockable ){
				BasicDropDownButtonHandler handler = new BasicDropDownButtonHandler( action, dockable );
				DropDownMiniButton button = new DropDownMiniButton( handler );
				handler.setModel( button.getModel() );
				button.setMouseOverBorder( BorderFactory.createEtchedBorder() );
				return handler;
			}
		} );
	}

	/**
	 * Creates a {@link MiniButton} in a flat look.
	 * @param trigger the trigger to invoke when the button has been clicked
	 * @param initializer a strategy to lazily initialize resources
	 * @return the new button
	 */
	protected MiniButton<BasicButtonModel> createTitleMiniButton( BasicTrigger trigger, BasicResourceInitializer initializer ){
		BasicMiniButton button = new BasicMiniButton( trigger, initializer );
		button.setMouseOverBorder( BorderFactory.createEtchedBorder() );
		button.setNormalSelectedBorder( BorderFactory.createEtchedBorder() );

		return button;
	}

	@Override
	public void uninstall( DockController controller ){
		super.uninstall( controller );
		controller.getDockTitleManager().clearThemeFactories();

		controller.getActionViewConverter().putTheme( ActionType.BUTTON, ViewTarget.TITLE, null );
		controller.getActionViewConverter().putTheme( ActionType.CHECK, ViewTarget.TITLE, null );
		controller.getActionViewConverter().putTheme( ActionType.MENU, ViewTarget.TITLE, null );
		controller.getActionViewConverter().putTheme( ActionType.RADIO, ViewTarget.TITLE, null );
		controller.getActionViewConverter().putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE, null );

		controller.getProperties().unset( TabPane.LAYOUT_MANAGER, Priority.THEME );
	}

	//    @Override
	//    protected void updateColors() {
	//        DockController controller = getController();
	//
	//        if( controller != null && getColorScheme() != null ){
	//            controller.getColors().lockUpdate();
	//
	//            super.updateColors();
	//
	//            updateColor( "title.active.left", null );
	//            updateColor( "title.inactive.left", null );
	//            updateColor( "title.active.right", null );
	//            updateColor( "title.inactive.right", null );
	//            updateColor( "title.active.text", null );
	//            updateColor( "title.inactive.text", null );
	//
	//            updateColor( "paint.line", null );
	//            updateColor( "paint.divider", null );
	//            updateColor( "paint.insertion.area", null );
	//            updateColor( "paint.insertion.border", null );
	//
	//            updateColor( "stack.tab.border.out.selected", null );
	//            updateColor( "stack.tab.border.center.selected", null );
	//            updateColor( "stack.tab.border.out.focused", null );
	//            updateColor( "stack.tab.border.center.focused", null );
	//            updateColor( "stack.tab.border.out", null );
	//            updateColor( "stack.tab.border.center", null );
	//            updateColor( "stack.tab.border", null );
	//
	//            updateColor( "stack.tab.background.top.selected", null ); 
	//            updateColor( "stack.tab.background.bottom.selected", null );
	//            updateColor( "stack.tab.background.top.focused", null ); 
	//            updateColor( "stack.tab.background.bottom.focused", null );
	//            updateColor( "stack.tab.background.top", null );
	//            updateColor( "stack.tab.background.bottom", null );
	//            updateColor( "stack.tab.background", null );
	//
	//            updateColor( "stack.tab.foreground", null );
	//            updateColor( "stack.tab.foreground.selected",  null );
	//
	//            controller.getColors().unlockUpdate();
	//        }
	//        else{
	//            super.updateColors();
	//        }
	//    }

	/**
	 * Sets the {@link DisplayerFactory} that is used for the {@link SplitDockStation}.
	 * Normally all displayers do not have any border, but the displayers on
	 * a SplitDockStation may need a small border.
	 * @param splitDisplayFactory the factory
	 */
	public void setSplitDisplayFactory( DisplayerFactory splitDisplayFactory ){
		this.splitDisplayFactory = splitDisplayFactory;
	}

	/**
	 * Gets the special factory for the {@link SplitDockStation}.
	 * @return the factory
	 * @see #setSplitDisplayFactory(DisplayerFactory)
	 */
	public DisplayerFactory getSplitDisplayFactory(){
		return splitDisplayFactory;
	}

	@Override
	public DisplayerFactory getDisplayFactory( DockStation station ){
		if( station instanceof SplitDockStation )
			return splitDisplayFactory;

		return super.getDisplayFactory( station );
	}
}

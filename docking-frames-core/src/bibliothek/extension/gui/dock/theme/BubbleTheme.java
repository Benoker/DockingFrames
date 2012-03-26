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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.bubble.BubbleButtonDockTitle;
import bibliothek.extension.gui.dock.theme.bubble.BubbleColorScheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleDisplayerFactory;
import bibliothek.extension.gui.dock.theme.bubble.BubbleDockTitleFactory;
import bibliothek.extension.gui.dock.theme.bubble.BubbleMovingImageFactory;
import bibliothek.extension.gui.dock.theme.bubble.BubbleSeparator;
import bibliothek.extension.gui.dock.theme.bubble.BubbleStackDockComponent;
import bibliothek.extension.gui.dock.theme.bubble.BubbleStationPaint;
import bibliothek.extension.gui.dock.theme.bubble.RoundButton;
import bibliothek.extension.gui.dock.theme.bubble.RoundButtonViewItem;
import bibliothek.extension.gui.dock.theme.bubble.RoundDropDownButton;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.MenuDockAction;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
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
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicMenuHandler;
import bibliothek.gui.dock.themes.basic.action.BasicSelectableHandler;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.icon.DefaultIconScheme;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;

/**
 * A theme using a lot of eye-candy.
 * @author Benjamin Sigg
 */
@ThemeProperties(
        authors = { "Ivan Seidl", "Benjamin Sigg" }, 
        descriptionBundle = "theme.bubble.description", 
        nameBundle = "theme.bubble", 
        webpages = { "" }  )
public class BubbleTheme extends BasicTheme {
    /** the key to set the {@link ColorScheme} of this theme */
    public static final PropertyKey<ColorScheme> BUBBLE_COLOR_SCHEME = 
        new PropertyKey<ColorScheme>( "dock.ui.BubbleTheme.ColorScheme",
        	new DynamicPropertyFactory<ColorScheme>(){
        		public ColorScheme getDefault(
	        			PropertyKey<ColorScheme> key,
	        			DockProperties properties ){
	        		return new BubbleColorScheme();
	        	}
        	}, true );
    
	/**
	 * Key for a property pointing to a {@link DockActionDistributor}. This interface is responsible for distributing
	 * {@link DockAction}s to tabs, titles and info components.
	 */
	public static final PropertyKey<DockActionDistributor> ACTION_DISTRIBUTOR = new PropertyKey<DockActionDistributor>( "dock.bubble.DockActionDistributor",
			new ConstantPropertyFactory<DockActionDistributor>( new DefaultDockActionDistributor() ), true);

    /** the {@link DockController}s which currently use this theme */
    private List<DockController> controllers = new ArrayList<DockController>();

    /**
     * Creates a new theme
     */
    public BubbleTheme(){
        setColorSchemeKey( BUBBLE_COLOR_SCHEME );
        setDisplayerFactory( new BubbleDisplayerFactory(), Priority.DEFAULT );
        setTitleFactory( new BubbleDockTitleFactory(), Priority.DEFAULT );
        setPaint( new BubbleStationPaint(), Priority.DEFAULT );
        setMovingImageFactory( new BubbleMovingImageFactory(), Priority.DEFAULT );
        setStackDockComponentFactory( new StackDockComponentFactory(){
            public StackDockComponent create( StackDockComponentParent station ) {
                return new BubbleStackDockComponent( station );
            }
        }, Priority.DEFAULT );
        setTabPlacement( TabPlacement.BOTTOM_OF_DOCKABLE, Priority.DEFAULT );
        setSpanFactory( new NoSpanFactory() );
    }

    @Override
    public void install( DockController controller ){
        super.install( controller );
        controllers.add( controller );

        // set new titles
        controller.getDockTitleManager().registerTheme( FlapDockStation.BUTTON_TITLE_ID, BubbleButtonDockTitle.FACTORY );

        controller.getProperties().set( TabPane.LAYOUT_MANAGER, new MenuLineLayout(), Priority.THEME );
        
        controller.getIcons().setScheme( Priority.THEME, new DefaultIconScheme( "data/bibliothek/gui/dock/core/bubble/icons.ini", BubbleTheme.class.getClassLoader(), controller ) );

        ActionViewConverter converter = controller.getActionViewConverter();

        converter.putTheme( 
                ActionType.BUTTON, 
                ViewTarget.TITLE, 
                new ButtonGenerator() );

        converter.putTheme( 
                ActionType.CHECK, 
                ViewTarget.TITLE, 
                new CheckGenerator() );

        converter.putTheme( 
                ActionType.RADIO, 
                ViewTarget.TITLE, 
                new RadioGenerator() );

        converter.putTheme(
                ActionType.DROP_DOWN,
                ViewTarget.TITLE,
                new DropDownGenerator() );

        converter.putTheme(
                ActionType.MENU,
                ViewTarget.TITLE,
                new MenuGenerator() );

        converter.putTheme(
                ActionType.SEPARATOR,
                ViewTarget.TITLE,
                new SeparatorGenerator() );
    }

    @Override
    public void uninstall( DockController controller ){
        super.uninstall( controller );
        controllers.remove( controller );
        controller.getProperties().unset( TabPane.LAYOUT_MANAGER, Priority.THEME );
        
        controller.getDockTitleManager().clearThemeFactories();

        controller.getIcons().setScheme( Priority.THEME, null );

        ActionViewConverter converter = controller.getActionViewConverter();

        converter.putTheme( ActionType.BUTTON,  ViewTarget.TITLE, null );
        converter.putTheme( ActionType.CHECK,  ViewTarget.TITLE, null );
        converter.putTheme( ActionType.RADIO,  ViewTarget.TITLE, null );
        converter.putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE, null );
        converter.putTheme( ActionType.MENU, ViewTarget.TITLE, null );
        converter.putTheme( ActionType.SEPARATOR, ViewTarget.TITLE, null );
    }

    /**
     * Generator to create views for {@link ButtonDockAction button-actions}.
     * @author Benjamin Sigg
     */
    private static class ButtonGenerator implements ViewGenerator<ButtonDockAction, BasicTitleViewItem<JComponent>>{
        public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ) {
            BasicButtonHandler handler = new BasicButtonHandler( action, dockable );
            RoundButton button = new RoundButton( handler, handler, dockable, action );
            handler.setModel( button.getModel() );
            return new RoundButtonViewItem( dockable, handler, button );
        }
    }

    /**
     * Generator to create views for {@link SelectableDockAction check-actions}.
     * @author Benjamin Sigg
     */
    private static class CheckGenerator implements ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>{
        public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ) {
            BasicSelectableHandler.Check handler = new BasicSelectableHandler.Check( action, dockable );
            RoundButton button = new RoundButton( handler, handler, dockable, action );
            handler.setModel( button.getModel() );
            return new RoundButtonViewItem( dockable, handler, button );
        }
    }

    /**
     * Generator to create views for {@link SelectableDockAction radio-actions}.
     * @author Benjamin Sigg
     */
    private static class RadioGenerator implements ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>{
        public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ) {
            BasicSelectableHandler.Radio handler = new BasicSelectableHandler.Radio( action, dockable );
            RoundButton button = new RoundButton( handler, handler, dockable, action );
            handler.setModel( button.getModel() );
            return new RoundButtonViewItem( dockable, handler, button );
        }
    }

    /**
     * Generator to create views for {@link DropDownAction dropdown-actions}.
     * @author Benjamin Sigg
     */
    private static class DropDownGenerator implements ViewGenerator<DropDownAction, BasicTitleViewItem<JComponent>>{
        public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, DropDownAction action, Dockable dockable ) {
            BasicDropDownButtonHandler handler = new BasicDropDownButtonHandler( action, dockable );
            RoundDropDownButton button = new RoundDropDownButton( handler, dockable, action );
            handler.setModel( button.getModel() );
            return new RoundButtonViewItem( dockable, handler, button );
        }
    }

    /**
     * Generator to create views for {@link MenuDockAction menus}.
     * @author Benjamin Sigg
     */
    private static class MenuGenerator implements ViewGenerator<MenuDockAction, BasicTitleViewItem<JComponent>>{
        public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
            BasicMenuHandler handler = new BasicMenuHandler( action, dockable );
            RoundButton button = new RoundButton( handler, handler, dockable, action );
            handler.setModel( button.getModel() );
            return new RoundButtonViewItem( dockable, handler, button );
        }
    }

    /**
     * Generator to create views for {@link SeparatorAction separators}.
     * @author Benjamin Sigg
     */
    private static class SeparatorGenerator implements ViewGenerator<SeparatorAction, BasicTitleViewItem<JComponent>>{
        public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SeparatorAction action, Dockable dockable ){
            if( action.shouldDisplay( ViewTarget.TITLE ))
                return new BubbleSeparator( action );

            return null;
        }
    }
}

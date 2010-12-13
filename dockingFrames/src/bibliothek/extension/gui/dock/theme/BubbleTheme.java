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
import java.util.Map;

import javax.swing.Icon;
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
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.themes.basic.action.BasicButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicMenuHandler;
import bibliothek.gui.dock.themes.basic.action.BasicSelectableHandler;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
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
    }

    @Override
    public void install( DockController controller ){
        super.install( controller );
        controllers.add( controller );

        // set new titles
        controller.getDockTitleManager().registerTheme( 
                FlapDockStation.BUTTON_TITLE_ID, 
                BubbleButtonDockTitle.FACTORY );

        Map<String,Icon> icons = DockUtilities.loadIcons( "data/bubble/icons.ini", null, BubbleTheme.class.getClassLoader() );
        for( Map.Entry<String, Icon> icon : icons.entrySet() ){
            controller.getIcons().setIconTheme( icon.getKey(), icon.getValue() );
        }

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

        controller.getDockTitleManager().clearThemeFactories();

        controller.getIcons().clearThemeIcons();

        ActionViewConverter converter = controller.getActionViewConverter();

        converter.putTheme( ActionType.BUTTON,  ViewTarget.TITLE, null );
        converter.putTheme( ActionType.CHECK,  ViewTarget.TITLE, null );
        converter.putTheme( ActionType.RADIO,  ViewTarget.TITLE, null );
        converter.putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE, null );
        converter.putTheme( ActionType.MENU, ViewTarget.TITLE, null );
        converter.putTheme( ActionType.SEPARATOR, ViewTarget.TITLE, null );
    }

//    @Override
//    protected void updateColors() {
//        DockController controller = getController();
//        if( controller != null && getColorScheme() != null ){
//
//            controller.getColors().lockUpdate();
//
//            super.updateColors();
//
//            updateColor( "tab.border.active", null );
//
//            // stack
//            updateColor( "stack.tab.background.top.mouse", null );
//            updateColor( "stack.tab.background.bottom.mouse", null );
//            updateColor( "stack.tab.border.mouse", null );
//            updateColor( "stack.tab.foreground.mouse", null );
//
//            updateColor( "stack.tab.background.top", null );
//            updateColor( "stack.tab.background.bottom", null );
//            updateColor( "stack.tab.border", null );
//            updateColor( "stack.tab.foreground", null );
//
//            updateColor( "stack.tab.background.top.selected.mouse", null );
//            updateColor( "stack.tab.background.bottom.selected.mouse", null );
//            updateColor( "stack.tab.border.selected.mouse", null );
//            updateColor( "stack.tab.foreground.selected.mouse", null );
//
//            updateColor( "stack.tab.background.top.selected", null );
//            updateColor( "stack.tab.background.bottom.selected", null );
//            updateColor( "stack.tab.border.selected", null );
//            updateColor( "stack.tab.foreground.selected", null );
//
//            updateColor( "stack.tab.background.top.focused.mouse", null );
//            updateColor( "stack.tab.background.bottom.focused.mouse", null );
//            updateColor( "stack.tab.border.focused.mouse", null );
//            updateColor( "stack.tab.foreground.focused.mouse", null );
//
//            updateColor( "stack.tab.background.top.focused", null );
//            updateColor( "stack.tab.background.bottom.focused", null );
//            updateColor( "stack.tab.border.focused", null );
//            updateColor( "stack.tab.foreground.focused", null );
//
//            updateColor( "stack.menu.background.top.mouse", null );
//            updateColor( "stack.menu.background.bottom.mouse", null );
//            updateColor( "stack.menu.border.mouse", null );
//            
//            updateColor( "stack.menu.background.top", null );
//            updateColor( "stack.menu.background.bottom", null );
//            updateColor( "stack.menu.border", null );
//
//            // title
//            updateColor( "title.background.top.active", null );
//            updateColor( "title.background.top.active.mouse", null );
//            updateColor( "title.background.top.inactive", null );
//            updateColor( "title.background.top.inactive.mouse", null );
//            updateColor( "title.background.bottom.active", null );
//            updateColor( "title.background.bottom.active.mouse", null );
//            updateColor( "title.background.bottom.inactive", null );
//            updateColor( "title.background.bottom.inactive.mouse", null );
//            updateColor( "title.foreground.active", null );
//            updateColor( "title.foreground.active.mouse", null );
//            updateColor( "title.foreground.inactive", null );
//            updateColor( "title.foreground.inactive.mouse", null );
//
//            updateColor( "title.background.top.active.flap", null );
//            updateColor( "title.background.top.active.mouse.flap", null );
//            updateColor( "title.background.top.inactive.flap", null );
//            updateColor( "title.background.top.inactive.mouse.flap", null );
//            updateColor( "title.background.top.selected.flap", null );
//            updateColor( "title.background.top.selected.mouse.flap", null );
//            updateColor( "title.background.bottom.active.flap", null );
//            updateColor( "title.background.bottom.active.mouse.flap", null );
//            updateColor( "title.background.bottom.inactive.flap", null );
//            updateColor( "title.background.bottom.inactive.mouse.flap", null );
//            updateColor( "title.background.bottom.selected.flap", null );
//            updateColor( "title.background.bottom.selected.mouse.flap", null );
//            updateColor( "title.foreground.active.flap", null );
//            updateColor( "title.foreground.active.mouse.flap", null );
//            updateColor( "title.foreground.inactive.flap", null );
//            updateColor( "title.foreground.inactive.mouse.flap", null );
//            updateColor( "title.foreground.selected.flap", null );
//            updateColor( "title.foreground.selected.mouse.flap", null );
//            
//            // display border
//            updateColor( "displayer.border.high.active", null );
//            updateColor( "displayer.border.high.inactive", null );
//            updateColor( "displayer.border.low.active", null );
//            updateColor( "displayer.border.low.inactive", null );
//
//            // RoundButton
//            updateColor( "action.button", null );
//            updateColor( "action.button.enabled", null );
//            updateColor( "action.button.selected", null );
//            updateColor( "action.button.selected.enabled", null );
//            updateColor( "action.button.mouse.enabled", null );
//            updateColor( "action.button.mouse.selected.enabled", null );
//            updateColor( "action.button.pressed.enabled", null );
//            updateColor( "action.button.pressed.selected.enabled", null );
//
//            // Round drop down button
//            updateColor( "action.dropdown", null );
//            updateColor( "action.dropdown.enabled", null );
//            updateColor( "action.dropdown.selected", null );
//            updateColor( "action.dropdown.selected.enabled", null );
//            updateColor( "action.dropdown.mouse.enabled", null );
//            updateColor( "action.dropdown.mouse.selected.enabled", null );
//            updateColor( "action.dropdown.pressed.enabled", null );
//            updateColor( "action.dropdown.pressed.selected.enabled", null );
//
//            updateColor( "action.dropdown.line", null );
//            updateColor( "action.dropdown.line.enabled", null );
//            updateColor( "action.dropdown.line.selected", null );
//            updateColor( "action.dropdown.line.selected.enabled", null );
//            updateColor( "action.dropdown.line.mouse.enabled", null );
//            updateColor( "action.dropdown.line.mouse.selected.enabled", null );
//            updateColor( "action.dropdown.line.pressed.enabled", null );
//            updateColor( "action.dropdown.line.pressed.selected.enabled", null );
//
//            // Paint
//            updateColor( "paint.divider", null );
//            updateColor( "paint.insertion", null );
//            updateColor( "paint.line", null );
//
//            controller.getColors().unlockUpdate();
//        }
//        else{
//            super.updateColors();
//        }
//    }

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

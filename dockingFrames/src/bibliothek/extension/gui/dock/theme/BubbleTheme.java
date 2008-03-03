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

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.bubble.*;
import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.themes.basic.action.*;

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
	/** The colors used by this theme */
    private Map<String, Color> colors = new HashMap<String, Color>();
    
    /** the {@link DockController}s which currently use this theme */
    private List<DockController> controllers = new ArrayList<DockController>();
    
	/**
	 * Creates a new theme
	 */
    public BubbleTheme(){
        setColorScheme( new BubbleColorScheme() );
        setDisplayerFactory( new BubbleDisplayerFactory());
        setTitleFactory( new BubbleDockTitleFactory());
        setPaint( new BubbleStationPaint() );
        setMovingImageFactory( new BubbleMovingImageFactory() );
        setStackDockComponentFactory( new StackDockComponentFactory(){
            public StackDockComponent create( StackDockStation station ) {
                return new BubbleStackDockComponent( station );
            }
        });
    }
    
    /**
     * Gets a color for a specified key.
     * @param key the key of the color
     * @return the color or <code>null</code>
     */
    public Color getColor( String key ){
        return colors.get( key );
    }
    
    /**
     * Stores a color which will be used in the theme. Note that this method
     * takes effect the next time when this theme is installed.
     * @param key the key of the color
     * @param color the color to store
     */
    public void setColor( String key, Color color ){
    	colors.put( key, color );
    }
    
	@Override
	public void install( DockController controller ){
		super.install( controller );
		controllers.add( controller );
		
        // set new titles
        controller.getDockTitleManager().registerTheme( 
                FlapDockStation.BUTTON_TITLE_ID, 
                new ReducedBubbleTitleFactory());
        
        Map<String,Icon> icons = loadIcons();
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

	/**
	 * Reads a set of icons which will replace the ordinary icons.
	 * @return the new set of icons
	 */
	protected Map<String, Icon> loadIcons(){
	    try{
	    	Properties properties = new Properties();
	        InputStream in = DockUI.class.getResourceAsStream( "/data/bubble/icons.ini" );
	        properties.load( in );
	        in.close();
	        ClassLoader loader=BubbleTheme.class.getClassLoader();

	        //Properties properties = ResourceManager.getDefault().ini( "DockUI.mapping", "data/bubble/icons.ini", getClass().getClassLoader() ).get();
	        Map<String, Icon> result = new HashMap<String, Icon>();
	        Enumeration<Object> e = properties.keys();
	        while( e.hasMoreElements() ){
	            String key = (String)e.nextElement();
	            ImageIcon icon = new ImageIcon( ImageIO.read( loader.getResource( properties.getProperty(key)) ));
	            result.put( key, icon);
	        }
	        return result;
	    }
	    catch( IOException ex ){
	        ex.printStackTrace();
	        return new HashMap<String, Icon>();
	    }
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
	
	@Override
	protected void updateColors( DockController[] controllers ) {
	    for( DockController controller : controllers )
	        controller.getColors().lockUpdate();
	    
	    super.updateColors( controllers );
	    
	    updateColor( controllers, "tab.border.active", null );
	    
	    // stack
        updateColor( controllers, "stack.tab.background.top.mouse", null );
        updateColor( controllers, "stack.tab.background.bottom.mouse", null );
        updateColor( controllers, "stack.tab.border.mouse", null );
        updateColor( controllers, "stack.tab.foreground.mouse", null );
        
        updateColor( controllers, "stack.tab.background.top", null );
        updateColor( controllers, "stack.tab.background.bottom", null );
        updateColor( controllers, "stack.tab.border", null );
        updateColor( controllers, "stack.tab.foreground", null );
        
        updateColor( controllers, "stack.tab.background.top.selected.mouse", null );
        updateColor( controllers, "stack.tab.background.bottom.selected.mouse", null );
        updateColor( controllers, "stack.tab.border.selected.mouse", null );
        updateColor( controllers, "stack.tab.foreground.selected.mouse", null );
        
        updateColor( controllers, "stack.tab.background.top.selected", null );
        updateColor( controllers, "stack.tab.background.bottom.selected", null );
        updateColor( controllers, "stack.tab.border.selected", null );
        updateColor( controllers, "stack.tab.foreground.selected", null );
        
        updateColor( controllers, "stack.tab.background.top.focused.mouse", null );
        updateColor( controllers, "stack.tab.background.bottom.focused.mouse", null );
        updateColor( controllers, "stack.tab.border.focused.mouse", null );
        updateColor( controllers, "stack.tab.foreground.focused.mouse", null );
        
        updateColor( controllers, "stack.tab.background.top.focused", null );
        updateColor( controllers, "stack.tab.background.bottom.focused", null );
        updateColor( controllers, "stack.tab.border.focused", null );
        updateColor( controllers, "stack.tab.foreground.focused", null );
        
        
        // title
        updateColor( controllers, "title.background.top.active", null );
        updateColor( controllers, "title.background.top.active.mouse", null );
        updateColor( controllers, "title.background.top.inactive", null );
        updateColor( controllers, "title.background.top.inactive.mouse", null );
        updateColor( controllers, "title.background.bottom.active", null );
        updateColor( controllers, "title.background.bottom.active.mouse", null );
        updateColor( controllers, "title.background.bottom.inactive", null );
        updateColor( controllers, "title.background.bottom.inactive.mouse", null );
        updateColor( controllers, "title.foreground.active", null );
        updateColor( controllers, "title.foreground.active.mouse", null );
        updateColor( controllers, "title.foreground.inactive", null );
        updateColor( controllers, "title.foreground.inactive.mouse", null );
        
        // display border
        updateColor( controllers, "displayer.border.high.active", null );
        updateColor( controllers, "displayer.border.high.inactive", null );
        updateColor( controllers, "displayer.border.low.active", null );
        updateColor( controllers, "displayer.border.low.inactive", null );
        
        // RoundButton
        updateColor( controllers, "action.button", null );
        updateColor( controllers, "action.button.enabled", null );
        updateColor( controllers, "action.button.selected", null );
        updateColor( controllers, "action.button.selected.enabled", null );
        updateColor( controllers, "action.button.mouse.enabled", null );
        updateColor( controllers, "action.button.mouse.selected.enabled", null );
        updateColor( controllers, "action.button.pressed.enabled", null );
        updateColor( controllers, "action.button.pressed.selected.enabled", null );

        // Round drop down button
        updateColor( controllers, "action.dropdown", null );
        updateColor( controllers, "action.dropdown.enabled", null );
        updateColor( controllers, "action.dropdown.selected", null );
        updateColor( controllers, "action.dropdown.selected.enabled", null );
        updateColor( controllers, "action.dropdown.mouse.enabled", null );
        updateColor( controllers, "action.dropdown.mouse.selected.enabled", null );
        updateColor( controllers, "action.dropdown.pressed.enabled", null );
        updateColor( controllers, "action.dropdown.pressed.selected.enabled", null );
        
        updateColor( controllers, "action.dropdown.line", null );
        updateColor( controllers, "action.dropdown.line.enabled", null );
        updateColor( controllers, "action.dropdown.line.selected", null );
        updateColor( controllers, "action.dropdown.line.selected.enabled", null );
        updateColor( controllers, "action.dropdown.line.mouse.enabled", null );
        updateColor( controllers, "action.dropdown.line.mouse.selected.enabled", null );
        updateColor( controllers, "action.dropdown.line.pressed.enabled", null );
        updateColor( controllers, "action.dropdown.line.pressed.selected.enabled", null );
        
        // Paint
        updateColor( controllers, "paint.divider", null );
        updateColor( controllers, "paint.insertion", null );
        updateColor( controllers, "paint.line", null );
	    
	    for( DockController controller : controllers )
	        controller.getColors().unlockUpdate();
	}

    /**
     * Generator to create views for {@link ButtonDockAction button-actions}.
     * @author Benjamin Sigg
     */
    private class ButtonGenerator implements ViewGenerator<ButtonDockAction, BasicTitleViewItem<JComponent>>{
        public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ) {
            BasicButtonHandler handler = new BasicButtonHandler( action, dockable );
            RoundButton button = new RoundButton( handler, dockable, action );
            handler.setModel( button.getModel() );
            return new RoundButtonViewItem( dockable, handler, button );
        }
    }
    
    /**
     * Generator to create views for {@link SelectableDockAction check-actions}.
     * @author Benjamin Sigg
     */
    private class CheckGenerator implements ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>{
        public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ) {
            BasicSelectableHandler.Check handler = new BasicSelectableHandler.Check( action, dockable );
            RoundButton button = new RoundButton( handler, dockable, action );
            handler.setModel( button.getModel() );
            return new RoundButtonViewItem( dockable, handler, button );
        }
    }
    
    /**
     * Generator to create views for {@link SelectableDockAction radio-actions}.
     * @author Benjamin Sigg
     */
    private class RadioGenerator implements ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>{
        public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ) {
            BasicSelectableHandler.Radio handler = new BasicSelectableHandler.Radio( action, dockable );
            RoundButton button = new RoundButton( handler, dockable, action );
            handler.setModel( button.getModel() );
            return new RoundButtonViewItem( dockable, handler, button );
        }
    }
    
    /**
     * Generator to create views for {@link DropDownAction dropdown-actions}.
     * @author Benjamin Sigg
     */
    private class DropDownGenerator implements ViewGenerator<DropDownAction, BasicTitleViewItem<JComponent>>{
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
    private class MenuGenerator implements ViewGenerator<MenuDockAction, BasicTitleViewItem<JComponent>>{
    	public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
            BasicMenuHandler handler = new BasicMenuHandler( action, dockable );
            RoundButton button = new RoundButton( handler, dockable, action );
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

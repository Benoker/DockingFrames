/**
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.bubble.*;
import bibliothek.extension.gui.dock.theme.bubble.view.*;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.views.ActionViewConverter;
import bibliothek.gui.dock.action.views.ViewGenerator;
import bibliothek.gui.dock.action.views.ViewTarget;
import bibliothek.gui.dock.action.views.buttons.TitleViewItem;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.station.stack.DefaultStackDockComponent;
import bibliothek.gui.dock.themes.DefaultTheme;
import bibliothek.gui.dock.themes.ThemeProperties;

/**
 * A theme using a lot of eye-candy.
 * @author Benjamin Sigg
 */
@ThemeProperties(
		authors = { "Ivan Seidl", "Benjamin Sigg" }, 
		descriptionBundle = "theme.bubble.description", 
		nameBundle = "theme.bubble", 
		webpages = { "" }  )
public class BubbleTheme extends DefaultTheme {
	/** The colors used by this theme */
    private Map<String, Color> colors = new HashMap<String, Color>();
    
    /** A listener to the {@link DockController} */
	private Listener listener = new Listener();
	
	/**
	 * Creates a new theme
	 */
    public BubbleTheme(){
        colors.put( "tab.border.active",            new Color( 150, 0, 0 ) );
        colors.put( "tab.border.active.mouse",      new Color( 200, 100, 100 ) );
        colors.put( "tab.border.inactive",          new Color( 100, 100, 100 ) );
        colors.put( "tab.border.inactive.mouse",    new Color( 100, 175, 100 ) );
        colors.put( "tab.top.active",               new Color( 200, 0, 0 ) );
        colors.put( "tab.top.active.mouse",         new Color( 255, 100, 100 ) );
        colors.put( "tab.top.inactive",             new Color( 150, 150, 150 ) );
        colors.put( "tab.top.inactive.mouse",       new Color( 150, 255, 150 ) );
        colors.put( "tab.bottom.active",            new Color( 255, 100, 100 ) );
        colors.put( "tab.bottom.active.mouse",      new Color( 255, 200, 200 ) );
        colors.put( "tab.bottom.inactive",          new Color( 200, 200, 200 ) );
        colors.put( "tab.bottom.inactive.mouse",    new Color( 220, 255, 220 ) );
        colors.put( "tab.text.active",              new Color( 0, 0, 0 ));
        colors.put( "tab.text.active.mouse",        new Color( 0, 0, 0 ));
        colors.put( "tab.text.inactive",            new Color( 100, 100, 100 ));
        colors.put( "tab.text.inactive.mouse",      new Color( 25, 25, 25 ));
        
        colors.put( "title.top.active",               new Color( 200, 0, 0 ) );
        colors.put( "title.top.active.mouse",         new Color( 255, 100, 100 ) );
        colors.put( "title.top.inactive",             new Color( 150, 150, 150 ) );
        colors.put( "title.top.inactive.mouse",       new Color( 150, 255, 150 ) );
        colors.put( "title.bottom.active",            new Color( 255, 100, 100 ) );
        colors.put( "title.bottom.active.mouse",      new Color( 255, 200, 200 ) );
        colors.put( "title.bottom.inactive",          new Color( 200, 200, 200 ) );
        colors.put( "title.bottom.inactive.mouse",    new Color( 220, 255, 220 ) );
        colors.put( "title.text.active",              new Color( 0, 0, 0 ));
        colors.put( "title.text.active.mouse",        new Color( 0, 0, 0 ));
        colors.put( "title.text.inactive",            new Color( 100, 100, 100 ));
        colors.put( "title.text.inactive.mouse",      new Color( 25, 25, 25 ));
        
        colors.put( "border.high.active",           new Color( 255, 100, 100 ));
        colors.put( "border.high.inactive",         new Color( 200, 200, 200 ));
        colors.put( "border.low.active",            new Color( 200, 100, 100 ));
        colors.put( "border.low.inactive",          new Color( 100, 100, 100 ));
        
        // RoundButton
       
        colors.put( "button",                                 new Color( 255, 255, 255 ));
        colors.put( "button.enabled",                         new Color( 255, 200, 200 ));
        colors.put( "button.selected",                        new Color( 150, 150, 175 ));
        colors.put( "button.selected.enabled",                new Color( 200, 200, 255 ));
        colors.put( "button.mouse.enabled",                   new Color( 255, 100, 100 ));
        colors.put( "button.mouse.selected.enabled",          new Color( 100, 100, 255 ));
        colors.put( "button.pressed.enabled",                 new Color( 255, 0, 0 ));
        colors.put( "button.pressed.selected.enabled",        new Color( 0, 0, 255 ));
              
        
        // Round drop down button
        colors.put( "dropdown",                                 new Color( 175, 150, 150 ));
        colors.put( "dropdown.enabled",                         new Color( 255, 200, 200 ));
        colors.put( "dropdown.selected",                        new Color( 150, 150, 175 ));
        colors.put( "dropdown.selected.enabled",                new Color( 200, 200, 255 ));
        colors.put( "dropdown.mouse.enabled",                   new Color( 255, 100, 100 ));
        colors.put( "dropdown.mouse.selected.enabled",          new Color( 100, 100, 255 ));
        colors.put( "dropdown.pressed.enabled",                 new Color( 255, 0, 0 ));
        colors.put( "dropdown.pressed.selected.enabled",        new Color( 0, 0, 255 ));
        colors.put( "dropdown.line",                            new Color( 100, 75, 75 ));
        colors.put( "dropdown.line.enabled",                    new Color( 150, 75, 75 ));
        colors.put( "dropdown.line.selected",                   new Color( 75, 75, 100 ));
        colors.put( "dropdown.line.selected.enabled",           new Color( 75, 75, 75 ));
        colors.put( "dropdown.line.mouse.enabled",              new Color( 150, 50, 50 ));
        colors.put( "dropdown.line.mouse.selected.enabled",     new Color( 50, 50, 150 ));
        colors.put( "dropdown.line.pressed.enabled",            new Color( 150, 0, 0 ));
        colors.put( "dropdown.line.pressed.selected.enabled",   new Color( 0, 0, 150 ));
        
        colors.put( "paint",                        new Color( 0, 0, 0 ));

        setDisplayerFactory( new BubbleDisplayerFactory( this ));
        setTitleFactory( new BubbleDockTitleFactory( this ));
        setPaint( new BubbleStationPaint( this ) );
        setMovingTitleGetter( new BubbleMovingTitleGetter( this ) );
    }
    
    /**
     * Derives all colors needed in this theme.
     * @param active the base color for active titles
     * @param inactive the base color for inactive titles
     * @param text the base color for text
     * @param button the base color for buttons
     * @param buttonSelected the base color for selected buttons
     * @param disabled the base color for disabled elementss
     */
    public void deriveColors( 
            Color active, Color inactive, Color text,
            Color button, Color buttonSelected, Color disabled ){
        
        float dh = 1f / 12f;
        float ds = 1f / 3f;
        float db = 1f / 3f;
        
        colors.put( "tab.border.active",             delta( active, 0, 0, -db ));
        colors.put( "tab.border.active.mouse",       delta( active, 0, ds, -db ));
        colors.put( "tab.border.inactive",           delta( inactive, 0, 0, -db ));
        colors.put( "tab.border.inactive.mouse",     delta( inactive, 0, ds, -db ));
        colors.put( "tab.top.active",                delta( active, dh, 0, 0 ));
        colors.put( "tab.top.active.mouse",          delta( active, dh, ds, 0 ));
        colors.put( "tab.top.inactive",              delta( inactive, dh, 0, 0 ));
        colors.put( "tab.top.inactive.mouse",        delta( inactive, dh, ds, 0 ));
        colors.put( "tab.bottom.active",             delta( active, -dh, 0, 0 ));
        colors.put( "tab.bottom.active.mouse",       delta( active, -dh, ds, 0 ));
        colors.put( "tab.bottom.inactive",           delta( inactive, -dh, 0, 0 ));
        colors.put( "tab.bottom.inactive.mouse",     delta( inactive, -dh, ds, 0 ));
        colors.put( "tab.text.active",               text );
        colors.put( "tab.text.active.mouse",         delta( text, 0, ds, 0 ));
        colors.put( "tab.text.inactive",             delta( text, -dh, 0, db ));
        colors.put( "tab.text.inactive.mouse",       delta( text, -dh, ds, db ));
        
        colors.put( "title.top.active",              delta( active, dh, 0, 0 ));
        colors.put( "title.top.active.mouse",        delta( active, dh, ds, 0 ));
        colors.put( "title.top.inactive",            delta( inactive, dh, 0, 0 ));
        colors.put( "title.top.inactive.mouse",      delta( inactive, dh, ds, 0 ));
        colors.put( "title.bottom.active",           delta( active, -dh, 0, 0 ));
        colors.put( "title.bottom.active.mouse",     delta( active, -dh, ds, 0 ));
        colors.put( "title.bottom.inactive",         delta( inactive, -dh, 0, 0 ));
        colors.put( "title.bottom.inactive.mouse",   delta( inactive, -dh, ds, 0 ));
        colors.put( "title.text.active",             text );
        colors.put( "title.text.active.mouse",       delta( text, 0, ds, 0 ));
        colors.put( "title.text.inactive",           delta( text, 0, 0, db ));
        colors.put( "title.text.inactive.mouse",     delta( text, 0, ds, db ));
        
        colors.put( "border.high.active",            delta( active, -dh, 0, 0 ));
        colors.put( "border.high.inactive",          delta( inactive, -dh, 0, 0 ));
        colors.put( "border.low.active",             delta( active, -dh, -ds, 0 ));
        colors.put( "border.low.inactive",           delta( inactive, -dh, -ds, 0 ));
        
        // RoundButton
        colors.put( "button.mouse",                  delta( button, 0, ds, 0 ));
        colors.put( "button",                        button );
        
        // Round drop down button
        colors.put( "dropdown",                                  disabled );
        colors.put( "dropdown.enabled",                          button );
        colors.put( "dropdown.selected",                         disabled );
        colors.put( "dropdown.selected.enabled",                 buttonSelected );
        colors.put( "dropdown.mouse.enabled",                    delta( button, 0, ds, 0 ));
        colors.put( "dropdown.mouse.selected.enabled",           delta( buttonSelected, 0, ds, 0 ));
        colors.put( "dropdown.pressed.enabled",                  delta( button, 0, -ds, 0 ));
        colors.put( "dropdown.pressed.selected.enabled",         delta( buttonSelected, 0, -ds, 0 ));
        colors.put( "dropdown.line",                             disabled );
        colors.put( "dropdown.line.enabled",                     delta( button, 0, 0, -db ));
        colors.put( "dropdown.line.selected",                    disabled );
        colors.put( "dropdown.line.selected.enabled",            delta( buttonSelected, 0, 0, -db ));
        colors.put( "dropdown.line.mouse.enabled",               delta( button, 0, ds, -db ));
        colors.put( "dropdown.line.mouse.selected.enabled",      delta( buttonSelected, 0, ds, -db ));
        colors.put( "dropdown.line.pressed.enabled",             delta( button, 0, -ds, -db ));
        colors.put( "dropdown.line.pressed.selected.enabled",    delta( buttonSelected, 0, -ds, -db ));
        
        colors.put( "paint",                         text );
    }
    
    /**
     * Moves the hsb-representation of <code>color</code> a little.
     * @param color the color to change
     * @param dh the delta in hue
     * @param ds the delta in saturisation
     * @param db the delta in brightness
     * @return the new color
     */
    private Color delta( Color color, float dh, float ds, float db ){
        float[] hsb = new float[3];
        Color.RGBtoHSB( color.getRed(), color.getGreen(), color.getBlue(), hsb );
        hsb[0] += dh;
        hsb[1] += ds;
        hsb[2] += db;
        
        if( hsb[0] > 1 )
            hsb[0] -= (int)hsb[0];
        if( hsb[0] < 0 )
            hsb[0] += (int)( -hsb[0] ) + 1;
        
        hsb[1] = Math.min( 1, Math.max( hsb[1], 0 ));
        hsb[2] = Math.min( 1, Math.max( hsb[2], 0 ));
        return Color.getHSBColor( hsb[0], hsb[1], hsb[2] );
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
     * Stores a color which will be used in the theme.
     * @param key the key of the color
     * @param color the color to store
     */
    public void setColor( String key, Color color ){
    	colors.put( key, color );
    }
    
	@Override
	public void install( DockController controller ){
		super.install( controller );
		
		       
		// Exchange the DockComponents
		for( int i = 0, n = controller.getStationCount(); i<n; i++ ){
        	DockStation station = controller.getStation(i);
        	if( station instanceof StackDockStation ){
        		StackDockStation stack = (StackDockStation)station;
        		if( !(stack.getStackComponent() instanceof BubbleStackDockComponent) )
        			stack.setStackComponent( new BubbleStackDockComponent( this ) );
        	}
        }
		
        // set new titles
        controller.getDockTitleManager().registerTheme( 
                FlapDockStation.BUTTON_TITLE_ID, 
                new ReducedBubbleTitleFactory( this ));
        
		controller.addDockControllerListener( listener );
        
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
	    	/*
	    	 * @Todo reading all icons? Is there a way to do this using less resources?
	    	 */
	    	
	    	Properties properties = new Properties();
	        InputStream in = DockUI.class.getResourceAsStream( "/data/bubble/icons.ini" );
	        properties.load( in );
	        in.close();
	        ClassLoader loader=BubbleTheme.class.getClassLoader();

	        //Properties properties = ResourceManager.getDefault().ini( "DockUI.mapping", "data/bubble/icons.ini", getClass().getClassLoader() ).get();
	        Map<String, Icon> result = new HashMap<String, Icon>();
	        Enumeration e = properties.keys();
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
		
		controller.removeDockControllerListener( listener );
		
    	// Exchange the DockComponents
        for( int i = 0, n = controller.getStationCount(); i<n; i++ ){
        	DockStation station = controller.getStation(i);
        	if( station instanceof StackDockStation ){
        		StackDockStation stack = (StackDockStation)station;
        		if( stack.getStackComponent() instanceof BubbleStackDockComponent )
        			stack.setStackComponent( new DefaultStackDockComponent() );
        	}
        }
        
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
	
    /**
     * A listener to the Controller
     * @author Benjamin Sigg
     */
    private class Listener extends DockControllerAdapter{
		@Override
		public void dockableRegistered(DockController controller, Dockable dockable) {
			if( dockable instanceof StackDockStation ){
				StackDockStation stack = (StackDockStation)dockable;
				if( !(stack.getStackComponent() instanceof BubbleStackDockComponent) ){
					stack.setStackComponent( new BubbleStackDockComponent( BubbleTheme.this ) );
				}
			}
		}
    }

    /**
     * Generator to create views for {@link ButtonDockAction button-actions}.
     * @author Benjamin Sigg
     */
    private class ButtonGenerator implements ViewGenerator<ButtonDockAction, TitleViewItem<JComponent>>{
        public TitleViewItem<JComponent> create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ) {
            return new BubbleButtonView( BubbleTheme.this, action, dockable );
        }
    }
    
    /**
     * Generator to create views for {@link SelectableDockAction check-actions}.
     * @author Benjamin Sigg
     */
    private class CheckGenerator implements ViewGenerator<SelectableDockAction, TitleViewItem<JComponent>>{
        public TitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ) {
            return new BubbleSelectableView.Check( BubbleTheme.this, action, dockable );
        }
    }
    
    /**
     * Generator to create views for {@link SelectableDockAction radio-actions}.
     * @author Benjamin Sigg
     */
    private class RadioGenerator implements ViewGenerator<SelectableDockAction, TitleViewItem<JComponent>>{
        public TitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ) {
            return new BubbleSelectableView.Radio( BubbleTheme.this, action, dockable );
        }
    }
    
    /**
     * Generator to create views for {@link DropDownAction dropdown-actions}.
     * @author Benjamin Sigg
     */
    private class DropDownGenerator implements ViewGenerator<DropDownAction, TitleViewItem<JComponent>>{
        public TitleViewItem<JComponent> create( ActionViewConverter converter, DropDownAction action, Dockable dockable ) {
            return new BubbleDropDownView( BubbleTheme.this, action, dockable );
        }
    }
    
    /**
     * Generator to create views for {@link MenuDockAction menus}.
     * @author Benjamin Sigg
     */
    private class MenuGenerator implements ViewGenerator<MenuDockAction, TitleViewItem<JComponent>>{
    	public TitleViewItem<JComponent> create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
    		return new BubbleMenuView( BubbleTheme.this, action, dockable );
    	}
    }
 
    /**
     * Generator to create views for {@link SeparatorAction separators}.
     * @author Benjamin Sigg
     */
    private class SeparatorGenerator implements ViewGenerator<SeparatorAction, TitleViewItem<JComponent>>{
    	public TitleViewItem<JComponent> create( ActionViewConverter converter, SeparatorAction action, Dockable dockable ){
    		if( action.shouldDisplay( ViewTarget.TITLE ))
    			return new BubbleSeparator( action );
    		
    		return null;
    	}
    }
}

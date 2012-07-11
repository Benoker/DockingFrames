/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.flap.button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Tells what kind of information should be displayed on the buttons of a {@link FlapDockStation}. What is actually
 * displayed also depends on the {@link DockTitle} that is used, but all the default titles support all the settings
 * this class offers. 
 * @author Benjamin Sigg
 */
public class ButtonContent {
	/**
	 * A {@link ButtonContentCondition} that is always <code>true</code>
	 */
	public static final ButtonContentCondition TRUE = new ButtonContentCondition(){
		public boolean shouldShow( Dockable dockable, boolean themeSuggestion ){
			return true;
		}
		
		public void install( Dockable dockable, ButtonContent content ){
			// ignore	
		}
		
		public void uninstall( Dockable dockable, ButtonContent content ){
			// ignore	
		}
	};
	
	/**
	 * A {@link ButtonContentCondition} that is always <code>false</code>
	 */
	public static final ButtonContentCondition FALSE = new ButtonContentCondition(){
		public boolean shouldShow( Dockable dockable, boolean themeSuggestion ){
			return false;
		}
		
		public void install( Dockable dockable, ButtonContent content ){
			// ignore	
		}
		
		public void uninstall( Dockable dockable, ButtonContent content ){
			// ignore	
		}
	};
	
	/**
	 * A {@link ButtonContentCondition} that always returns the value a {@link DockTheme} would choose
	 */
	public static final ButtonContentCondition THEME = new ButtonContentCondition(){
		public boolean shouldShow( Dockable dockable, boolean themeSuggestion ){
			return themeSuggestion;
		}
		
		public void install( Dockable dockable, ButtonContent content ){
			// ignore	
		}
		
		public void uninstall( Dockable dockable, ButtonContent content ){
			// ignore	
		}
	};
	
	/**
	 * A {@link ButtonContentCondition} that returns <code>true</code> if a {@link Dockable} does not
	 * have an icon.
	 */
	public static final ButtonContentCondition NOT_IF_ICON = new AbstractButtonContentCondition(){
		public boolean shouldShow( Dockable dockable, boolean themeSuggestion ){
			return dockable.getTitleIcon() == null;
		}
		
		public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ){
			fire( dockable );
		}
	};
	
	/**
	 * A {@link ButtonContentCondition} that returns <code>true</code> if a {@link Dockable} does not
	 * have a title text.
	 */
	public static final ButtonContentCondition NOT_IF_TEXT = new AbstractButtonContentCondition(){
		public boolean shouldShow( Dockable dockable, boolean themeSuggestion ){
			String title = dockable.getTitleText();
			return title == null || title.length() == 0;
		}
		
		public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ){
			fire( dockable );
		}
	};
	
	/**
	 * A {@link ButtonContentCondition} that returns <code>true</code> if the element is a {@link DockStation}.
	 */
	public static final ButtonContentCondition IF_STATION = new ButtonContentCondition(){
		public boolean shouldShow( Dockable dockable, boolean themeSuggestion ){
			return dockable.asDockStation() != null;
		}
		
		public void install( Dockable dockable, ButtonContent content ){
			// ignore	
		}
		
		public void uninstall( Dockable dockable, ButtonContent content ){
			// ignore
		}
	};
	

	/**
	 * A {@link ButtonContentCondition} that returns <code>true</code> if the element is not a {@link DockStation}.
	 */
	public static final ButtonContentCondition IF_DOCKABLE = new ButtonContentCondition(){
		public boolean shouldShow( Dockable dockable, boolean themeSuggestion ){
			return dockable.asDockStation() == null;
		}
		
		public void install( Dockable dockable, ButtonContent content ){
			// ignore	
		}
		
		public void uninstall( Dockable dockable, ButtonContent content ){
			// ignore
		}
	};
	
	/** the look and feel completely depends on the current {@link DockTheme}. */
	public static final ButtonContent THEME_DEPENDENT = new ButtonContent( THEME, THEME, THEME, THEME, THEME, THEME );
	/**
	 * Only the icon is painted. Please note that this constant only remains for backwards compatibility,
	 * creating a new {@link ButtonContent} would have the exact same effect. 
	 */
    public static final ButtonContent ICON_ONLY = new ButtonContent( FALSE, TRUE, FALSE, FALSE, FALSE, FALSE );
    /**
	 * Only the title text is painted. Please note that this constant only remains for backwards compatibility,
	 * creating a new {@link ButtonContent} would have the exact same effect. 
	 */
    public static final ButtonContent TEXT_ONLY = new ButtonContent( FALSE, FALSE, TRUE, FALSE, FALSE, FALSE );
    /**
	 * Only the icon and the title text are painted. Please note that this constant only remains for backwards compatibility,
	 * creating a new {@link ButtonContent} would have the exact same effect. 
	 */
    public static final ButtonContent ICON_AND_TEXT_ONLY = new ButtonContent( FALSE, TRUE, TRUE, FALSE, FALSE, FALSE );
    /**
	 * The icon, or if not present the title text, is painted. Please note that this constant only remains for backwards compatibility,
	 * creating a new {@link ButtonContent} would have the exact same effect. 
	 */
    public static final ButtonContent ICON_THEN_TEXT_ONLY = new ButtonContent( FALSE, TRUE, NOT_IF_ICON, FALSE, FALSE, FALSE );
    /**
	 * The title text, or if not present the icon, is painted. Please note that this constant only remains for backwards compatibility,
	 * creating a new {@link ButtonContent} would have the exact same effect. 
	 */
    public static final ButtonContent TEXT_THEN_ICON_ONLY = new ButtonContent( FALSE, NOT_IF_TEXT, TRUE, FALSE, FALSE, FALSE );
    /**
	 * Only the icon and the actions are painted. Please note that this constant only remains for backwards compatibility,
	 * creating a new {@link ButtonContent} would have the exact same effect. 
	 */
    public static final ButtonContent ICON_ACTIONS = new ButtonContent( FALSE, TRUE, FALSE, FALSE, TRUE, FALSE );
    /**
	 * Only the title text and the actions are painted. Please note that this constant only remains for backwards compatibility,
	 * creating a new {@link ButtonContent} would have the exact same effect. 
	 */
    public static final ButtonContent TEXT_ACTIONS = new ButtonContent( FALSE, FALSE, TRUE, FALSE, TRUE, FALSE );
    /**
	 * Icon, title text and actions are painted. Please note that this constant only remains for backwards compatibility,
	 * creating a new {@link ButtonContent} would have the exact same effect. 
	 */
    public static final ButtonContent ICON_AND_TEXT_ACTIONS = new ButtonContent( FALSE, TRUE, TRUE, FALSE, TRUE, FALSE );
    /**
	 * The icon, or if not present the title text, and the actions are painted. Please note that this constant only remains for backwards compatibility,
	 * creating a new {@link ButtonContent} would have the exact same effect. 
	 */
    public static final ButtonContent ICON_THEN_TEXT_ACTIONS = new ButtonContent( FALSE, TRUE, NOT_IF_ICON, FALSE, TRUE, FALSE );
    /**
	 * The title text, or if not present the icon, and the actions are painted. Please note that this constant only remains for backwards compatibility,
	 * creating a new {@link ButtonContent} would have the exact same effect. 
	 */
    public static final ButtonContent TEXT_THEN_ICON_ACTIONS = new ButtonContent( FALSE, NOT_IF_TEXT, TRUE, FALSE, TRUE, FALSE );
    
    private ButtonContentCondition knob;
    private ButtonContentCondition icon;
    private ButtonContentCondition text;
    private ButtonContentCondition actions;
    private ButtonContentCondition filterActions;
    private ButtonContentCondition children;
    
    private Map<Dockable, List<ButtonContentListener>> listeners = new HashMap<Dockable, List<ButtonContentListener>>();
    
    /**
     * Creates a new set of properties. All arguments can have a value of <code>null</code>, in which case they default
     * to {@link #THEME}
     * @param knob whether to paint a "knob" where the user can grab the title and move around. A knob really is only
     * required if neither icon nor text is painted
     * @param icon whether to paint the icon of a <code>Dockable</code>
     * @param text whether to paint the title text of a <code>Dockable</code>
     * @param children whether to add a button for each child of a {@link DockStation}. The button allows user to
     * open the station and focus one of its children with on click. If the represented {@link Dockable} is not a 
     * station, then only one button is painted.
     * @param actions whether to show the normal {@link DockAction}s of a <code>Dockable</code>
     * @param filterActions whether only important {@link DockAction}s, as defined by {@link ButtonContentFilter}, should be shown
     */
    public ButtonContent( ButtonContentCondition knob, ButtonContentCondition icon, ButtonContentCondition text, ButtonContentCondition children, ButtonContentCondition actions, ButtonContentCondition filterActions ){
    	this.knob = get( knob );
    	this.icon = get( icon );
    	this.text = get( text );
    	this.children = get( children );
    	this.actions = get( actions );
    	this.filterActions = get( filterActions );
    }
    
    private ButtonContentCondition get( ButtonContentCondition condition ){
    	if( condition == null ){
    		return THEME;
    	}
    	return condition;
    }
    
    /**
     * Informs this {@link ButtonContent} that any change regarding <code>dockable</code> should
     * be reported to <code>listener</code>.
     * @param dockable the element to observe
     * @param listener the listener that monitors <code>dockable</code>
     */
    public void addListener( Dockable dockable, ButtonContentListener listener ){
    	List<ButtonContentListener> list = listeners.get( dockable );
    	if( list == null ){
    		list = new ArrayList<ButtonContentListener>();
    		listeners.put( dockable, list );
    		install( dockable );
    	}
    	list.add( listener );
    }
    
    /**
     * Informs this {@link ButtonContent} that <code>listener</code> no longer has to be observed.
     * @param dockable the element that was observed
     * @param listener the listener that is no longer required
     */
    public void removeListener( Dockable dockable, ButtonContentListener listener ){
    	List<ButtonContentListener> list = listeners.get( dockable );
    	if( list != null ){
    		list.remove( listener );
    		if( list.isEmpty() ){
    			uninstall( dockable );
    			listeners.remove( dockable );
    		}
    	}
    }
    
    /**
     * Gets all the listeners that are currently monitoring <code>dockable</code>.
     * @param dockable the element which may be monitored
     * @return all the listeners, may be an empty array but not <code>null</code>
     */
    protected ButtonContentListener[] listeners( Dockable dockable ){
    	List<ButtonContentListener> list = listeners.get( dockable );
    	if( list != null ){
    		return list.toArray( new ButtonContentListener[ list.size() ] );
    	}
    	return new ButtonContentListener[]{};
    }
    
    public void handleChange( Dockable dockable ){
    	for( ButtonContentListener listener : listeners( dockable )){
    		listener.changed( this, dockable );
    	}
    }
    
    private void install( Dockable dockable ){
    	knob.install( dockable, this );
    	icon.install( dockable, this );
    	text.install( dockable, this );
    	children.install( dockable, this );
    	actions.install( dockable, this );
    	filterActions.install( dockable, this );
    }
    
    private void uninstall( Dockable dockable ){
    	knob.uninstall( dockable, this );
    	icon.uninstall( dockable, this );
    	text.uninstall( dockable, this );
    	children.uninstall( dockable, this );
    	actions.uninstall( dockable, this );
    	filterActions.uninstall( dockable, this );
    }
        
    /**
     * Tells whether a knob should be shown
     * @param dockable the element for which the property is requested
     * @param theme what the theme would do
     * @return <code>true</code> if the knob should be visible
     */
    public boolean showKnob( Dockable dockable, boolean theme ){
    	return knob.shouldShow( dockable, theme );
    }
    
    /**
     * Gets the condition that decides the property for {@link #showKnob(Dockable, boolean)}
     * @return the condition, not <code>null</code>
     */
    public ButtonContentCondition getKnob(){
		return knob;
	}
    
    /**
     * Tells whether actions should be shown on the button of a {@link FlapDockStation}
     * or not.
     * @param dockable the element for which the property is requested
     * @param theme what the theme would do
     * @return <code>true</code> if the actions should be shown
     */
    public boolean showActions( Dockable dockable, boolean theme ){
    	return actions.shouldShow( dockable, theme );
    }
    
    /**
     * Gets the condition that decides the property for {@link #showActions(Dockable, boolean)}
     * @return the condition, not <code>null</code>
     */
    public ButtonContentCondition getActions(){
		return actions;
	}
    

    /**
     * Tells whether actions should filtered before showing on the button of a {@link FlapDockStation}.
     * If {@link #showActions(Dockable, boolean)} returns <code>false</code> for <code>dockable</code>, then
     * this method is ignored.
     * @param dockable the element for which the property is requested
     * @param theme what the theme would do
     * @return <code>true</code> if the actions should be filtered by the current {@link ButtonContentFilter}
     */
    public boolean filterActions( Dockable dockable, boolean theme ){
    	return actions.shouldShow( dockable, theme ) && filterActions.shouldShow( dockable, theme );
    }
    
    /**
     * Gets the condition that decides the property for {@link #filterActions(Dockable, boolean)}
     * @return the condition, not <code>null</code>
     */
    public ButtonContentCondition getFilterActions(){
		return filterActions;
	}
    
    /**
     * Tells whether an icon should be shown.
     * @param dockable the element for which the property is requested
     * @param theme what the theme would do
     * @return <code>true</code> if icons should be shown
     */
    public boolean showIcon( Dockable dockable, boolean theme ){
        return icon.shouldShow( dockable, theme );
    }
    
    /**
     * Gets the condition that decides the property for {@link #showIcon(Dockable, boolean)}
     * @return the condition, not <code>null</code>
     */
    public ButtonContentCondition getIcon(){
		return icon;
	}
    
    /**
     * Tells whether text should be shown.
     * @param dockable the element for which the property is requested
     * @param theme what the theme would do
     * @return <code>true</code> if text should be shown
     */
    public boolean showText( Dockable dockable, boolean theme ){
        return text.shouldShow( dockable, theme );
    }
    
    /**
     * Gets the condition that decides the property for {@link #showText(Dockable, boolean)}
     * @return the condition, not <code>null</code>
     */
    public ButtonContentCondition getText(){
		return text;
	}
    
    /**
     * Tells whether actions to focus a child of a {@link DockStation} should be shown
     * @param dockable the element for which the property is requested
     * @param theme what the theme would do
     * @return <code>true</code> if the buttons should be shown
     */
    public boolean showChildren( Dockable dockable, boolean theme ){
    	return children.shouldShow( dockable, theme );
    }
    
    /**
     * Gets the condition that decides the property for {@link #showChildren(Dockable, boolean)}
     * @return the condition, not <code>null</code>
     */
    public ButtonContentCondition getChildren(){
		return children;
	}
}

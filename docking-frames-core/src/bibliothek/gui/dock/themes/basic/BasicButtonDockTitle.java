/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

package bibliothek.gui.dock.themes.basic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.FilteredDockActionSource;
import bibliothek.gui.dock.action.MultiDockActionSource;
import bibliothek.gui.dock.action.StationChildrenActionSource;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.gui.dock.station.flap.button.ButtonContentFilter;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonContentValue;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.ActivityDockTitleEvent;
import bibliothek.gui.dock.title.DockTitleEvent;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.util.Condition;

import static bibliothek.gui.dock.station.flap.button.ButtonContent.*;

/**
 * This title changes its border whenever the active-state changes.
 * @author Benjamin Sigg
 */
@ColorCodes({ "title.flap.active", 
	"title.flap.active.text",
	"title.flap.inactive",
	"title.flap.inactive.text",
	"title.flap.selected",
	"title.flap.selected.text",	
	"title.flap.active.knob.highlight",
	"title.flap.active.knob.shadow",
	"title.flap.inactive.knob.highlight",
	"title.flap.inactive.knob.shadow",
	"title.flap.selected.knob.highlight",
	"title.flap.selected.knob.shadow"
})
public class BasicButtonDockTitle extends AbstractDockTitle {
	/** amount of space required to paint the knob */
	protected final int KNOB_SIZE = 10;
	
	/** whether the mouse is currently pressed or not */
	private boolean mousePressed = false;
	
	/** whether this button is selected on its owner or not */
	private boolean selected = false;
	
	/** tells what items to paint */
	protected ButtonContentValue behavior;
	
	/** tells what items to filter */
	private PropertyValue<ButtonContentFilter> connector = new PropertyValue<ButtonContentFilter>( FlapDockStation.BUTTON_CONTENT_FILTER ) {
		protected void valueChanged( ButtonContentFilter oldValue, ButtonContentFilter newValue ){
			if( behavior != null ){
				updateActionSource( true );
			}
		}
	};
	
	/** whether children are currently shown */
	private boolean showChildren = false;
	
	/** wether actions are currently shown */
	private boolean showActions = false;
	
	/** whether all actions should be painted or only a selection */
	private boolean filterActions = false;
	
	/** the color used for the background when active */
	private TitleColor activeColor = new BasicTitleColor( "title.flap.active", null );
	/** the color used for the foreground when active */
	private TitleColor activeTextColor = new BasicTitleColor( "title.flap.active.text", null );
	/** the color used for background when inactive */
	private TitleColor inactiveColor = new BasicTitleColor( "title.flap.inactive", null );
	/** the color used for foreground when inactive */
	private TitleColor inactiveTextColor = new BasicTitleColor( "title.flap.inactive.text", null );
	/** the color used for background when selected */
	private TitleColor selectedColor = new BasicTitleColor( "title.flap.selected", null );
	/** the color used for foreground when selected */
	private TitleColor selectedTextColor = new BasicTitleColor( "title.flap.selected.text", null );
	
	/** the color used for the bright side of the knob if active */
	private TitleColor knobActiveHighlightColor = new BasicTitleColor( "title.flap.active.knob.highlight", null );
	/** the color used for the dark side of the knob if active */
	private TitleColor knobActiveShadowColor = new BasicTitleColor( "title.flap.active.knob.shadow", null );
	/** the color used for the bright side of the knob if inactive */
	private TitleColor knobInactiveHighlightColor = new BasicTitleColor( "title.flap.inactive.knob.highlight", null );
	/** the color used for the dark side of the knob if inactive */
	private TitleColor knobInactiveShadowColor = new BasicTitleColor( "title.flap.inactive.knob.shadow", null );
	/** the color used for the bright side of the knob if selected */
	private TitleColor knobSelectedHighlightColor = new BasicTitleColor( "title.flap.selected.knob.highlight", null );
	/** the color used for the dark side of the knob if selected */
	private TitleColor knobSelectedShadowColor = new BasicTitleColor( "title.flap.selected.knob.shadow", null );
	
	/** keeps all the {@link DockActionSource}s that have to be shown on this title */
	private MultiDockActionSource allActionsSource = new MultiDockActionSource();
	
    /**
     * Constructs a new title
     * @param dockable the {@link Dockable} for which this title is created
     * @param origin the version which was used to create this title
     */
    public BasicButtonDockTitle( Dockable dockable, DockTitleVersion origin ) {
        super();

        behavior = new ButtonContentValue( new ButtonContent( TRUE, TRUE, IF_DOCKABLE, IF_STATION, TRUE, TRUE ) ){
			@Override
			protected void propertyChanged(){
				updateContent();
			}
		};
        
        init( dockable, origin, false );
        changeBorder();
        
        addMouseInputListener( new MouseInputAdapter(){
        	@Override
        	public void mousePressed( MouseEvent e ){
        		mousePressed = (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK ) != 0;
        		changeBorder();
        	}
        	
        	@Override
        	public void mouseReleased( MouseEvent e ){
        		mousePressed = (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK ) != 0;
        		changeBorder();
        	}
        });
        
        addColor( activeColor );
        addColor( activeTextColor );
        addColor( inactiveColor );
        addColor( inactiveTextColor );
        addColor( selectedColor );
        addColor( selectedTextColor );
        addColor( knobActiveHighlightColor );
        addColor( knobActiveShadowColor );
        addColor( knobInactiveHighlightColor );
        addColor( knobInactiveShadowColor );
        addColor( knobSelectedHighlightColor );
        addColor( knobSelectedShadowColor );
        
        addConditionalFont( DockFont.ID_FLAP_BUTTON_ACTIVE, TitleFont.KIND_FLAP_BUTTON_FONT, 
                new Condition(){
            public boolean getState() {
                return isActive();
            }
        }, null );

        addConditionalFont( DockFont.ID_FLAP_BUTTON_SELECTED, TitleFont.KIND_FLAP_BUTTON_FONT, 
                new Condition(){
            public boolean getState() {
                return isSelected();
            }
        }, null );

        addConditionalFont( DockFont.ID_FLAP_BUTTON_INACTIVE, TitleFont.KIND_FLAP_BUTTON_FONT, 
                new Condition(){
            public boolean getState() {
                return !isActive();
            }
        }, null );
        
        allActionsSource.setSeparateSources( true );
        updateContent();
    }
    
    @Override
    public void bind(){
    	DockTitleVersion origin = getOrigin();
    	
    	if( origin != null ){
    		connector.setProperties( origin.getController() );
        	behavior.setProperties( origin.getController() );
        }
    	behavior.setDockable( getDockable() );
        
    	super.bind();
    }
    
    @Override
    public void unbind(){
    	connector.setProperties( (DockController)null );
    	behavior.setProperties( (DockController)null );
    	behavior.setDockable( null );
    	super.unbind();
    }
    
    private void updateContent(){    	
    	updateIcon();
    	updateText();
    	updateActionSource( false );
    	
    	if( behavior.isShowActions() || behavior.isShowChildren() ){
    		setShowMiniButtons( true );
    	}
    	else{
    		setShowMiniButtons( false );
    	}
    	
    	revalidate();
    	repaint();
    }
    
    @Override
    protected Insets getInnerInsets(){
    	Insets base = super.getInnerInsets();
    	
    	if( behavior.isShowKnob() ){
    		if( getOrientation().isHorizontal() ){
    			base = new Insets( base.top, base.left + KNOB_SIZE, base.bottom, base.right );
    		}
    		else{
    			base = new Insets( base.top + KNOB_SIZE, base.left, base.bottom, base.right );
    		}
    	}
    	
    	return base;
    }
    
    @Override
    protected DockActionSource getActionSourceFor( Dockable dockable ){
    	return allActionsSource;
    }
    
    private void updateActionSource( boolean force ){
    	boolean showChildren = behavior.isShowChildren();
    	boolean showActions = behavior.isShowActions();
    	boolean filterActions = behavior.isFilterActions();
    		
    	if( force || this.showChildren != showChildren || this.showActions != showActions || this.filterActions != filterActions ){
    		allActionsSource.removeAll();
    		
	    	if( showChildren ){
	    		allActionsSource.add( getChildrenActionSourceFor( getDockable() ) );
	    	}
    	
	    	if( showActions ){
	    		if( filterActions ){
	    			allActionsSource.add( createFilter( getDefaultActionSourceFor( getDockable() ) ) );
	    		}
	    		else{
	    			allActionsSource.add( getDefaultActionSourceFor( getDockable() ) );
	    		}
	    	}
	    	
	    	this.showChildren = showChildren;
	    	this.showActions = showActions;
    	}
    }
    
    /**
     * Creates a filter around <code>actions</code>, only the actions going through the filter
     * will be shown.
     * @param actions the actions to filter
     * @return the filter
     */
    protected DockActionSource createFilter( DockActionSource actions ){
    	final ButtonContentFilter connector = this.connector.getValue();
    	
    	return new FilteredDockActionSource( actions ){
			protected boolean include( DockAction action ){
				return connector.isButtonAction( action );
			}
		};
    }
    
    /**
     * Gets the "normal" actions for <code>dockable</code>.
     * @param dockable some item for which actions are required
     * @return the normal actions, may be a new {@link DockActionSource}, not <code>null</code>
     */
    protected DockActionSource getDefaultActionSourceFor( Dockable dockable ){
    	return super.getActionSourceFor( dockable );
    }
    
    /**
     * Gets the "special" children actions for <code>dockable</code>
     * @param dockable some item for which actions are required
     * @return the children actions, may be a new {@link DockActionSource}, not <code>null</code>
     */
    protected DockActionSource getChildrenActionSourceFor( Dockable dockable ){
    	return new StationChildrenActionSource( dockable, null );
    }
    
    @Override
    protected void updateIcon() {
        if( behavior.isShowIcon() )
            super.updateIcon();
        else
            setIcon( null );
    }
    
    @Override
    protected void updateText() {
        if( behavior.isShowText() ){
            super.updateText();
        }
        else{
            setText( "" );
        }
    }
    
    @Override
    protected void paintForeground( Graphics g, JComponent component ){
    	// paint icon (if there is any)
    	paintIcon( g, component );
    	
    	// paint knob (if there is any)
    	if( behavior.isShowKnob() ){
    		Insets insets = getInnerInsets();
    		
    		if( getOrientation().isHorizontal() ){
    			int x = insets.left - KNOB_SIZE + 3;
    			int y1 = insets.top + 3;
    			int y2 = getHeight() - insets.bottom - 4;
    			
    			g.setColor( getColor( knobActiveHighlightColor, knobInactiveHighlightColor, knobSelectedHighlightColor ) );
    			g.drawLine( x, y1, x, y2 );
    			g.drawLine( x, y1, x+2, y1 );
    			
    			g.setColor( getColor( knobActiveShadowColor, knobInactiveShadowColor, knobSelectedShadowColor ) );
    			g.drawLine( x, y2, x+2, y2 );
    			g.drawLine( x+2, y1+1, x+2, y2 );
    		}
    		else{
    			int y = insets.top - KNOB_SIZE + 3;
    			int x1 = insets.left + 3;
    			int x2 = getWidth() - insets.right - 4;
    			
    			g.setColor( getColor( knobActiveHighlightColor, knobInactiveHighlightColor, knobSelectedHighlightColor ) );
    			g.drawLine( x1, y, x2, y );
    			g.drawLine( x1, y, x1, y+2 );
    			
    			g.setColor( getColor( knobActiveShadowColor, knobInactiveShadowColor, knobSelectedShadowColor ) );
    			g.drawLine( x1+1, y+2, x2, y+2 );
    			g.drawLine( x2, y, x2, y+2 );
    		}
    	}
    }
    
    @Override
    public void setActive( boolean active ) {
        if( active != isActive() ){
            super.setActive(active);
            selected = active;
            updateLayout();
        }
    }
    
    @Override
    public void changed( DockTitleEvent event ) {
    	if( event instanceof ActivityDockTitleEvent ){
    		ActivityDockTitleEvent activity = (ActivityDockTitleEvent)event;
	        super.setActive( activity.isActive() );
	        selected = activity.isActive() || activity.isPreferred();
	        updateLayout();
    	}
    	else{
    		super.changed( event );
    	}
    }
    
    @Override
    public Point getPopupLocation( Point click, boolean popupTrigger ){
        if( popupTrigger )
            return click;
        
    	return null;
    }
    
    /**
     * Tells whether the mouse is currently pressed or not.
     * @return <code>true</code> if the mouse is pressed
     */
    protected boolean isMousePressed(){
		return mousePressed;
	}
    
    /**
     * Whether this button is selected or not.
     * @return <code>true</code> if selected, <code>false</code> otherwise
     */
    public boolean isSelected() {
		return selected;
	}
    
    /**
     * Updates various elements of this title such that the current state
     * is met.
     */
    protected void updateLayout(){
        changeBorder();
        updateColors();
        updateFonts();
    }
    
    /**
     * Exchanges the current border.
     */
    protected void changeBorder(){
    	if( selected && mousePressed ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.selected.pressed", BorderFactory.createBevelBorder( BevelBorder.RAISED ));
    	}
    	else if( selected ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.selected", BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
    	}
    	else if( mousePressed ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.pressed", BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
    	}
    	else{
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button", BorderFactory.createBevelBorder( BevelBorder.RAISED ));
    	}
    }
    
    /**
     * Updates the colors of this title.
     */
    protected void updateColors(){
    	if( isActive() ){
    		setBackground( activeColor.color() );
    		setForeground( activeTextColor.color() );
    	}
    	else if( selected ){
    		setBackground( selectedColor.color() );
    		setForeground( selectedTextColor.color() );
    	}
    	else{
    		setBackground( inactiveColor.color() );
    		setForeground( inactiveTextColor.color() );
    	}
    }
    
    private Color getColor( TitleColor active, TitleColor inactive, TitleColor selected ){
    	if( isActive() ){
    		return active.color();
    	}
    	else if( this.selected ){
    		return selected.color();
    	}
    	else{
    		return inactive.color();
    	}
    }

    /**
     * Gets the color that is used as foreground if the title is focused.
     * @return the color, might be <code>null</code>
     */
    public Color getActiveTextColor(){
    	return activeTextColor.color();
    }
    
    /**
     * Sets the color that is used as foreground if the title is focused.
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setActiveTextColor( Color color ){
    	activeTextColor.setValue( color );
    }
    

    /**
     * Gets the color that is used as background if the title is focused.
     * @return the color, might be <code>null</code>
     */
    public Color getActiveColor(){
    	return activeColor.color();
    }
    
    /**
     * Sets the color that is used as background if the title is focused.
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setActiveColor( Color color ){
    	activeColor.setValue( color );
    }
    
    /**
     * Gets the color that is used as foreground if the title is selected.
     * @return the color, might be <code>null</code>
     */
    public Color getSelectedTextColor(){
    	return selectedTextColor.color();
    }
    
    /**
     * Sets the color that is used as foreground if the title is selected.
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setSelectedTextColor( Color color ){
    	selectedTextColor.setValue( color );
    }
    

    /**
     * Gets the color that is used as background if the title is selected.
     * @return the color, might be <code>null</code>
     */
    public Color getSelectedColor(){
    	return selectedColor.color();
    }
    
    /**
     * Sets the color that is used as background if the title is selected.
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setSelectedColor( Color color ){
    	selectedColor.setValue( color );
    }
    
    /**
     * Gets the color that is used as foreground
     * @return the color, might be <code>null</code>
     */
    public Color getInactiveTextColor(){
    	return inactiveTextColor.color();
    }
    
    /**
     * Sets the color that is used as foreground
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setInactiveTextColor( Color color ){
    	inactiveTextColor.setValue( color );
    }
    

    /**
     * Gets the color that is used as background
     * @return the color, might be <code>null</code>
     */
    public Color getInactiveColor(){
    	return inactiveColor.color();
    }
    
    /**
     * Sets the color that is used as background
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setInactiveColor( Color color ){
    	inactiveColor.setValue( color );
    }
    
    /**
     * A implementation of {@link TitleColor} that calls <code>repaint</code>
     * when the color changes.
     * @author Benjamin Sigg
     */
    private class BasicTitleColor extends TitleColor{
        /**
         * Creates a new color
         * @param id the id of the color
         * @param backup a backup color
         */
        public BasicTitleColor( String id, Color backup ){
            super( id, TitleColor.KIND_FLAP_BUTTON_COLOR, BasicButtonDockTitle.this, backup );
        }
        
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            updateColors();
        }
    }
}

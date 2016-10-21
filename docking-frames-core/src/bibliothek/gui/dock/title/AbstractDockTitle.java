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

package bibliothek.gui.dock.title;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.util.swing.OrientedLabel;

/**
 * An abstract implementation of {@link DockTitle}. This title can have
 * an icon, a title-text and some small buttons to display {@link DockAction actions}.
 * The icon is at the top or left edge, the text in the middle, and the actions
 * at the lower or the right edge of the title. If the orientation of the
 * title is set to {@link DockTitle.Orientation vertical}, the text will be rotated
 * by 90 degrees.<br>
 * This title has also an {@link ActionPopup} which will appear when the user
 * presses the right mouse-button. The popup shows a list of all actions known
 * to this title.<br>
 * The whole logic a {@link DockTitle} needs is implemented in this class,
 * but subclasses may add graphical features - like a border or another
 * background.<br>
 * Subclasses may override {@link #getInnerInsets()} to add a space between
 * border and contents of this title.
 * 
 * @author Benjamin Sigg
 *
 */
public class AbstractDockTitle extends AbstractMultiDockTitle {
    /** A panel that displays the action-buttons of this title */
    private ButtonPanel itemPanel;
    /** The actions that were suggested to this title */
    private DockActionSource suggestedSource;
    
    /**
     * Constructs a new title
     * @param dockable the Dockable which is the owner of this title
     * @param origin the version which was used to create this title
     */
    public AbstractDockTitle( Dockable dockable, DockTitleVersion origin ){
    	init( dockable, origin, true );
    }
    
    /**
     * Standard constructor
     * @param dockable The Dockable whose title this will be
     * @param origin The version which was used to create this title
     * @param showMiniButtons <code>true</code> if the actions of the Dockable
     * should be shown, <code>false</code> if they should not be visible
     */
    public AbstractDockTitle( Dockable dockable, DockTitleVersion origin, boolean showMiniButtons ){
    	init( dockable, origin, showMiniButtons );
    }
    
    /**
     * Constructor which does not do anything. Subclasses should call
     * {@link #init(Dockable, DockTitleVersion, boolean)} to initialize
     * the title.
     */
    protected AbstractDockTitle(){
    	// ignore 
    }
    
    /**
     * Initializer called by the constructor.
     * @param dockable The Dockable whose title this will be
     * @param origin The version which was used to create this title
     * @param showMiniButtons <code>true</code> if the actions of the Dockable
     * should be shown, <code>false</code> if they should not be visible
     */
    protected void init( Dockable dockable, DockTitleVersion origin, boolean showMiniButtons ){
    	super.init( dockable, origin );
    	setShowMiniButtons( showMiniButtons );
    }

    /**
     * Tells whether this title is able to show any {@link DockAction}.
     * @return <code>true</code> if {@link DockAction}s are enabled
     * @see #setShowMiniButtons(boolean)
     */
    public boolean isShowMiniButtons(){
    	return itemPanel != null;
    }
    
    /**
     * Enables or disables {@link DockAction}s for this title.
     * @param showMiniButtons whether to show actions or not
     */
    public void setShowMiniButtons( boolean showMiniButtons ){
    	if( showMiniButtons ){
    		if( itemPanel == null ){
            	itemPanel = new ButtonPanel( true ){
            		@Override
            		protected BasicTitleViewItem<JComponent> createItemFor( DockAction action, Dockable dockable ){
            			return AbstractDockTitle.this.createItemFor( action, dockable );
            		}
            	};
                itemPanel.setOpaque( false );
                itemPanel.setOrientation( getOrientation() );
                itemPanel.setToolTipText( getToolTipText() );
                add( itemPanel );
                
                if( isBound() ){
                	itemPanel.setController( getDockable().getController() );
                	itemPanel.set( getDockable(), getActionSourceFor( getDockable() ) );
                }
    		}
    	}
    	else{
    		if( itemPanel != null ){
    			itemPanel.set( null );
    			remove( itemPanel );
    		}
    	}
    }
    
    /**
     * Sets the tooltip that will be shown on this title.
     * @param text the new tooltip, can be <code>null</code>
     */
    protected void setTooltip( String text ){
    	super.setToolTipText( text );
        if( itemPanel != null )
            itemPanel.setToolTipText( text );
    }
    
    public void setOrientation( Orientation orientation ) {
        if( itemPanel != null ){
        	itemPanel.setOrientation( orientation );
        }
        super.setOrientation( orientation );
    }
    
    @Override
    protected void doTitleLayout(){
        Insets insets = titleInsets();
        int x = insets.left;
        int y = insets.top;
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;
        
        OrientedLabel label = getLabel();
        Orientation orientation = getOrientation();
        Icon icon = getIcon();
        int iconTextGap = getIconTextGap();
        
        Dimension labelPreferred;
        String text = getText();
        if( text == null || text.length() == 0 ){
        	labelPreferred = new Dimension( 5, 5 );
        }
        else{
        	labelPreferred = label.getPreferredSize();
        }
        
        if( orientation.isHorizontal() ){
            if( icon != null ){
                x += icon.getIconWidth() + iconTextGap;
                width -= icon.getIconWidth() + iconTextGap;
            }
            
            if( itemPanel != null && itemPanel.getItemCount() > 0 ){
            	Dimension[] buttonPreferred = itemPanel.getPreferredSizes();
            	
            	int remaining = width - labelPreferred.width;
            	int count = buttonPreferred.length-1;
            	
            	while( count > 0 && buttonPreferred[count].width > remaining )
            		count--;
            	
            	itemPanel.setVisibleActions( count );
            	
            	int buttonWidth = buttonPreferred[count].width;
            	int buttonX = width - buttonWidth;
            	
                label.setBounds( x, y, buttonX, height );
                itemPanel.setBounds( x + buttonX, y, width - buttonX, height );
            }
            else
                label.setBounds( x, y, width, height );
        }
        else{
            if( icon != null ){
                y += icon.getIconWidth() + iconTextGap;
                height -= icon.getIconWidth() + iconTextGap;
            }
            
            if( itemPanel != null && itemPanel.getItemCount() > 0 ){
            	Dimension[] buttonPreferred = itemPanel.getPreferredSizes();
            	
            	int remaining = height - labelPreferred.height;
            	int count = buttonPreferred.length-1;
            	
            	while( count > 0 && buttonPreferred[count].height > remaining )
            		count--;
            	
            	itemPanel.setVisibleActions( count );
            	
            	int buttonHeight = buttonPreferred[count].height;
            	int buttonY = height - buttonHeight;
            	
                label.setBounds( x, y, width, buttonY );
                itemPanel.setBounds( x, y + buttonY, width, height - buttonY );
            }
            else
                label.setBounds( x, y, width, height );
        }
    }
    
    public Point getPopupLocation( Point click, boolean popupTrigger ){
        if( popupTrigger )
            return click;
        
        boolean restrained = getText() == null || getText().length() == 0;
        
        Rectangle icon = getIconBounds();
        if( icon != null ){
            if( icon.contains( click )){
            	if( restrained ){
            		// icon must not be the whole title
            		int size = getWidth() * getHeight();
            		if( itemPanel != null )
            			size -= itemPanel.getWidth() * itemPanel.getHeight();
            		
            		if( size <= 2 * icon.width * icon.height )
            			return null;
            	}
            	
                if( getOrientation().isHorizontal() )
                    return new Point( icon.x, icon.y + icon.height );
                else
                    return new Point( icon.x + icon.width, icon.y );
            }
        }
        
        return null;
    }
    
    @Override
    public void changed( DockTitleEvent event ){
    	super.changed( event );
    	if( event instanceof ActionsDockTitleEvent ){
    		suggestActions( ((ActionsDockTitleEvent)event).getSuggestions() );
    	}
    }
    
    @Override
    public Dimension getPreferredSize() {
    	Dimension size = super.getPreferredSize();
    	if( itemPanel != null ){
    		Dimension items = itemPanel.getPreferredSize();
    		
    		Insets insets = titleInsets();
    		
    		if( getOrientation().isHorizontal() ){
    			size.width += items.width;
    			size.height = Math.max( size.height, items.height + insets.top + insets.bottom );
    		}
    		else{
    			size.height += items.height;
    			size.width = Math.max( size.width, items.width + insets.left + insets.right );
    		}
    	}
    	
        if( size.width < 10 ){
            size.width = 10;
        }
        
        if( size.height < 10 ){
            size.height = 10;
        }
        
        return size;
    }
    
    /**
     * Gets a list of all actions which will be shown on this title.
     * @param dockable the owner of the actions
     * @return the list of actions
     */
    protected DockActionSource getActionSourceFor( Dockable dockable ){
    	if( suggestedSource != null ){
    		return suggestedSource;
    	}
    	
        return dockable.getGlobalActionOffers();
    }
    
    /**
     * Called if a module using the {@link DockTitle} suggests using a specific set of {@link DockAction}s. It is
     * up to the {@link DockTitle} to follow the suggestions or to ignore them. The default behavior of this
     * {@link AbstractDockTitle} is to set the result of {@link #getActionSourceFor(Dockable)} equal to
     * <code>actions</code> and update the {@link #itemPanel} if necessary.
     * @param actions the set of actions that should be used
     */
    protected void suggestActions( DockActionSource actions ){
    	if( suggestedSource != actions ){
	    	suggestedSource = actions;
	    	if( isShowMiniButtons() ){
	    		Dockable dockable = getDockable();
	    		itemPanel.set( dockable, getActionSourceFor( dockable ) );
	    	}
    	}
    }
    
    /**
     * Gets the {@link DockActionSource} that was {@link #suggestActions(DockActionSource) suggested} to this
     * title.
     * @return the source, can be <code>null</code>
     */
    protected DockActionSource getSuggestedSource(){
		return suggestedSource;
	}
    
    @Override
    public void bind() {        
        DockController controller = getDockable().getController();
        if( itemPanel != null ){
        	Dockable dockable = getDockable();
        	itemPanel.set( dockable, getActionSourceFor( dockable ) );
        	itemPanel.setController( controller );
        }
        
        super.bind();
    }

    @Override
    public void unbind() {
        if( itemPanel != null ){
        	itemPanel.set( null );
        	itemPanel.setController( null );
        }
        
        super.unbind();
    }
}

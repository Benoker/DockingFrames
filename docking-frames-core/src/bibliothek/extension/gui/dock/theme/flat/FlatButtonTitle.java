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

package bibliothek.extension.gui.dock.theme.flat;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.extension.gui.dock.util.MouseOverListener;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicButtonDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This title is used by the {@link FlatTheme} to replace the
 * default-DockTitle of the {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public class FlatButtonTitle extends BasicButtonDockTitle {
    /** 
     * Current state of the mouse, is <code>true</code> when the
     * mouse is over this title.
     */
    private MouseOverListener mouseover;
    
    /**
     * Constructs a new title
     * @param dockable the owner of the title
     * @param origin the version which was used to create this title
     */
    public FlatButtonTitle( Dockable dockable, DockTitleVersion origin ) {
    	super( dockable, origin );
    	
    	mouseover = new MouseOverListener( getComponent() ){
    	    @Override
    	    protected void changed() {
    	        changeBorder();
    	    }
    	};
    	
        changeBorder();
    }
    
    @Override
    protected void setDisabled( boolean disabled ){
    	super.setDisabled( disabled );
    	changeBorder();
    }
    
    /**
     * Tells whether the mouse is currently over this button or not.
     * @return <code>true</code> if the mouse is over this button
     */
    public boolean isMouseover() {
		return !isDisabled() && mouseover == null ? false : mouseover.isMouseOver();
	}
    
    /**
     * Exchanges the border of this title according to the state of
     * <code>selected</code> and of <code>mouseover</code>.
     * over this title
     */
    @Override
    protected void changeBorder(){
    	boolean selected = isSelected();
    	boolean mouseover = isMouseover();
    	boolean mousePressed = isMousePressed();
    	
    	if( selected && mousePressed ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.flat.selected.pressed", BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ));	
    	}
    	else if( selected && mouseover ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.flat.selected.hover", BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
    	}
    	else if( selected ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.flat.selected", BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
    	}
    	else if( mousePressed ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.flat.pressed", BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
    	}
    	else if( mouseover ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.flat.hover", BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ));
    	}
    	else{
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.flat", FlatLineBorder.INSTANCE );
    	}
    }
}

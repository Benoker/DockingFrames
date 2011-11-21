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
package bibliothek.gui.dock.station.screen.window;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.ScreenDockWindowFactory;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A default implementation of {@link ScreenDockWindowFactory}.
 * @author Benjamin Sigg
 */
public class DefaultScreenDockWindowFactory implements ScreenDockWindowFactory {
    /** The kind of window that can be produced */
    public static enum Kind{
        /** Represents {@link JDialog} */
        DIALOG,
        /** Represents {@link JFrame} */
        FRAME
    }
    
    /** what kind of window this factory will create */
    private Kind kind = Kind.DIALOG;
    
    /** whether the newly created window will be undecorated */
    private boolean undecorated = true;
    
    /** whether the {@link DockTitle} is shown */
    private boolean showDockTitle = true;
    
    /** icon for the title */
    private Icon titleIcon = null;
    
    /** text for the title */
    private String titleText = null;
    
    /**
     * Sets the kind of window that this factory will create
     * @param kind the kind of window
     */
    public void setKind( Kind kind ) {
        if( kind == null )
            kind = Kind.DIALOG;
        
        this.kind = kind;
    }
    
    /**
     * Gets the kind of window this factory creates.
     * @return the kind of window
     */
    public Kind getKind() {
        return kind;
    }
    
    /**
     * Sets whether the windows created by this factory should be undecorated
     * or not.
     * @param undecorated <code>true</code> if they should not be decorated,
     * <code>false</code> otherwise
     */
    public void setUndecorated( boolean undecorated ) {
        this.undecorated = undecorated;
    }
    
    /**
     * Tells whether new windows will be decorated or not.
     * @return <code>true</code> if they are not decorated
     */
    public boolean isUndecorated() {
        return undecorated;
    }
    
    /**
     * Sets whether the {@link DockTitle} is normally shown on the window.
     * @param showDockTitle <code>true</code> if the title is shown, <code>false</code>
     * otherwise
     */
    public void setShowDockTitle( boolean showDockTitle ) {
        this.showDockTitle = showDockTitle;
    }
    
    /**
     * Tells whether the {@link DockTitle} is normally shown on the window.
     * @return <code>true</code> if shown, <code>false</code> otherwise
     */
    public boolean isShowDockTitle() {
        return showDockTitle;
    }
    
    /**
     * Sets the icon which should be used in the decorated title.
     * @param titleIcon the icon, <code>null</code> if the icon of the
     * {@link Dockable} should be used.
     */
    public void setTitleIcon( Icon titleIcon ) {
        this.titleIcon = titleIcon;
    }
    
    /**
     * Gets the icon which is used in decorated titles.
     * @return the icon, can be <code>null</code>
     */
    public Icon getTitleIcon() {
        return titleIcon;
    }
    
    /**
     * Sets the text which is used in decorated titles.
     * @param titleText the text, <code>null</code> if the text
     * of the {@link Dockable} should be used
     */
    public void setTitleText( String titleText ) {
        this.titleText = titleText;
    }
    
    /**
     * Gets the text which is used in decorated titles.
     * @return the text, can be <code>null</code>
     */
    public String getTitleText() {
        return titleText;
    }
    
    
    public ScreenDockWindow updateWindow( ScreenDockWindow window, WindowConfiguration configuration, ScreenDockStation station ){
    	return createWindow( station, configuration );
    }
    
    
    public ScreenDockWindow createWindow( ScreenDockStation station, WindowConfiguration configuration ){
        AbstractScreenDockWindow window;
        
        if( kind == Kind.FRAME ){
            window = new ScreenDockFrame( station, configuration, undecorated );
        }
        else{
            Window owner = station.getOwner();
            if( owner instanceof Frame )
                window = new ScreenDockDialog( station, configuration, (Frame)owner, undecorated );
            else if( owner instanceof Dialog )
                window = new ScreenDockDialog( station, configuration, (Dialog)owner, undecorated );
            else
                window = new ScreenDockDialog( station, configuration, undecorated );
        }
        
        window.setShowTitle( showDockTitle );
        window.setTitleIcon( titleIcon );
        window.setTitleText( titleText );
        return window;
    }
}

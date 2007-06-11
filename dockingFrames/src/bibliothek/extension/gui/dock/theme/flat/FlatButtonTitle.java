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

package bibliothek.extension.gui.dock.theme.flat;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MouseInputAdapter;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This title is used by the {@link FlatTheme} to replace the
 * default-DockTitle of the {@link FlapDockStation}. This title
 * does not show any {@link DockAction actions}, and if there is
 * an icon, the text of the title isn't shown either.
 * @author Benjamin Sigg
 *
 */
public class FlatButtonTitle extends AbstractDockTitle {
    /** Text to display if there is no icon for this title */
    private String noIconText = "";
    /** 
     * Current state of the mouse, is <code>true</code> when the
     * mouse is over this title 
     */
    private boolean mouseover = false;
    /**
     * Selected state of this title. Another border will be painted
     * if this title is selected.
     */
    private boolean selected = false;
    
    /**
     * Constructs a new title
     * @param dockable the owner of the title
     * @param origin the version which was used to create this title
     */
    public FlatButtonTitle( Dockable dockable, DockTitleVersion origin ) {
        super(dockable, origin, false);
        Listener listener = new Listener();
        addMouseInputListener( listener );
    }

    @Override
    public void changed( DockTitleEvent event ) {
        super.setActive( event.isActive() );
        changeBorder( event.isActive() || event.isPreferred(), mouseover );
    }
    
    /**
     * Exchanges the border of this title according to the state of
     * <code>selected</code> and of <code>mouseover</code>.
     * @param selected <code>true</code> if this title is selected
     * @param mouseover <code>true</code> if the mouse is currently
     * over this title
     */
    protected void changeBorder( boolean selected, boolean mouseover ){
        this.selected = selected;
        this.mouseover = mouseover;
        
        if( selected ){
            setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
        }
        else{
            if( mouseover )
                setBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ));
            else
                setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ));
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        if( getIcon() == null )
            return super.getPreferredSize();
        else{
            Icon icon = getIcon();
            
            Dimension preferred = new Dimension( icon.getIconWidth(), icon.getIconHeight() );
            
            Border border = getBorder();
            if( border != null ){
                Insets insets = border.getBorderInsets( this );
                preferred.width += insets.left + insets.right;
                preferred.height += insets.top + insets.bottom;
            }
            
            return preferred;
        }
    }
    
    @Override
    protected void setIcon( Icon icon ) {
        super.setIcon(icon);
        setText( noIconText );
    }
    
    @Override
    protected void setText( String text ) {
        if( getIcon() != null )
            super.setText( "" );
        else
            super.setText( text );
        
        noIconText = text;
        setToolTipText( text );
    }
    
    /**
     * A listener added to this title. The listener is triggered
     * when the mouse is moved over this title. This listener will
     * then invoke {@link FlatButtonTitle#changeBorder(boolean, boolean) changeBorder}.
     * @author Benjamin Sigg
     */
    private class Listener extends MouseInputAdapter{
        @Override
        public void mouseEntered( MouseEvent e ) {
            changeBorder( selected, true );
        }
        @Override
        public void mouseExited( MouseEvent e ) {
            changeBorder( selected, false );
        }
    }
}

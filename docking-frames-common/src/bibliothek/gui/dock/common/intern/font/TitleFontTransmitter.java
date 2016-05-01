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
package bibliothek.gui.dock.common.intern.font;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.FontMap;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.gui.dock.util.font.FontManager;

/**
 * A {@link TitleFontTransmitter} updates the fonts for
 * {@link DockFont#ID_TITLE_ACTIVE} and {@link DockFont#ID_TITLE_INACTIVE}
 * using {@link FontMap#FONT_KEY_TITLE} and {@link FontMap#FONT_KEY_TITLE_FOCUSED}.
 * @author Benjamin Sigg
 */
public class TitleFontTransmitter extends ListFontTransmitter{    
    /**
     * Creates a new transmitter
     * @param manager the manager to ask for default values
     */
    public TitleFontTransmitter( FontManager manager ){
        super( manager,
                new String[]{ FontMap.FONT_KEY_TITLE_FOCUSED, FontMap.FONT_KEY_TITLE },
                new String[]{ DockFont.ID_TITLE_ACTIVE, DockFont.ID_TITLE_INACTIVE });
    }
    
    @Override
    protected CDockable getDockable( DockFont observer ) {
        Dockable dockable = ((TitleFont)observer).getTitle().getDockable();
        if( dockable instanceof CommonDockable ){
            return ((CommonDockable)dockable).getDockable();
        }
        return null;
    }
}

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
package bibliothek.gui.dock.themes.font;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.font.AbstractDockFont;
import bibliothek.gui.dock.util.font.FontModifier;

/**
 * Represents a font used by a {@link DockTitle}.
 * @author Benjamin Sigg
 */
public abstract class TitleFont extends AbstractDockFont{
    public static final Path KIND_TITLE_FONT = KIND_DOCK_FONT.append( "title" );
    
    private DockTitle title;

    /**
     * Creates a new title font.
     * @param id the id of the font that is represented by this title font
     * @param title the title for which the font is used
     * @param backup the backup value used when no other value is available
     */
    public TitleFont( String id, DockTitle title, FontModifier backup ) {
        this( id, title, KIND_TITLE_FONT, backup );
    }

    /**
     * Creates a new title font.
     * @param id the id of the font that is represented by this title font
     * @param title the title for which the font is used
     * @param kind what kind of font this is
     * @param backup the backup value used when no other value is available
     */
    public TitleFont( String id, DockTitle title, Path kind, FontModifier backup ) {
        super( id, kind, backup );
        this.title = title;
    }

    /**
     * Creates a new title font.
     * @param id the id of the font that is represented by this title font
     * @param title the title for which the font is used
     * @param kind what kind of font this is
     */
    public TitleFont( String id, DockTitle title, Path kind ) {
        super( id, kind );
        this.title = title;
    }

    /**
     * Creates a new title font.
     * @param id the id of the font that is represented by this title font
     * @param title the title for which the font is used
     */
    public TitleFont( String id, DockTitle title ) {
        this( id, title, KIND_TITLE_FONT );
    }

    /**
     * Gets the title for which this font is used.
     * @return the title
     */
    public DockTitle getTitle() {
        return title;
    }
}

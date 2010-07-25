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
package bibliothek.gui.dock.util.font;

import java.awt.Font;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.AbstractUIValue;
import bibliothek.util.Path;

/**
 * An abstract implementation of {@link DockFont} that can connect itself to
 * a {@link DockController}.
 * @author Benjamin Sigg
 */
public abstract class AbstractDockFont extends AbstractUIValue<FontModifier, DockFont> implements DockFont{
    /**
     * Creates a new {@link DockFont}.
     * @param id the unique identifier of the font which is observed
     * @param backup the backup value used when no font is set
     */
    public AbstractDockFont( String id, FontModifier backup ) {
        super( id, DockFont.KIND_DOCK_FONT, backup );
    }

    /**
     * Creates a new {@link DockFont}.
     * @param id the unique identifier of the font which is observed
     * @param kind what kind of {@link DockFont} this is
     * @param backup the backup value used when no font is set
     */
    public AbstractDockFont( String id, Path kind, FontModifier backup ) {
        super( id, kind, backup );
    }


    /**
     * Creates a new {@link DockFont}.
     * @param id the unique identifier of the font which is observed
     * @param kind what kind of {@link DockFont} this is
     */
    public AbstractDockFont( String id, Path kind ) {
        super( id, kind );
    }


    /**
     * Creates a new {@link DockFont}.
     * @param id the unique identifier of the font which is observed
     */
    public AbstractDockFont( String id ) {
        super( id, DockFont.KIND_DOCK_FONT );
    }

    @Override
    protected DockFont me() {
        return this;
    }
    
    /**
     * Makes sure that this {@link DockFont} reads its values from the
     * {@link FontManager} of <code>controller</code>.<br>
     * Use {@link #setManager(bibliothek.gui.dock.util.UIProperties) setManager(null)} to disconnect.
     * @param controller the new source for fonts, can be <code>null</code>
     */
    public void connect( DockController controller ){
        setManager( controller == null ? null : controller.getFonts() );
    }
    
    /**
     * Gets the {@link FontModifier} which is currently represented by this {@link DockFont}.
     * @return the modifier, can be <code>null</code>
     */
    public FontModifier font(){
        return value();
    }
    
    /**
     * Gets a modified version of <code>font</code>
     * @param font some font to modify
     * @return the font that should be used, can be <code>null</code>
     */
    public Font font( Font font ){
        FontModifier modifier = value();
        if( modifier == null )
            return null;
        return modifier.modify( font );
    }
}

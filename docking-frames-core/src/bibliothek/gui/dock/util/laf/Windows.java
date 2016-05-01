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
package bibliothek.gui.dock.util.laf;

import java.awt.Color;
import java.awt.SystemColor;
import java.util.HashMap;
import java.util.Map;

/**
 * A set of colors derived from {@link SystemColor}s.
 * @author Benjamin Sigg
 */
public class Windows extends AbstractLookAndFeelColors{
    private Map<String, Color> colors = new HashMap<String, Color>();
    
    /**
     * Creates the new {@link LookAndFeelColors}
     */
    public Windows(){
        colors.put( TITLE_BACKGROUND, SystemColor.inactiveCaption );
        colors.put( TITLE_FOREGROUND, SystemColor.inactiveCaptionText );
        colors.put( TITLE_SELECTION_BACKGROUND, SystemColor.activeCaption );
        colors.put( TITLE_SELECTION_FOREGROUND, SystemColor.activeCaptionText );
        colors.put( SELECTION, SystemColor.textHighlight );
        colors.put( PANEL_BACKGROUND, SystemColor.control );
        colors.put( PANEL_FOREGROUND, SystemColor.controlText );
        colors.put( CONTROL_SHADOW, SystemColor.controlShadow );
        colors.put( WINDOW_BORDER, SystemColor.windowBorder );
    }
    
    public void bind() {
        // ignore
    }
    
    public void unbind() {
        // ignore
    }
    
    public Color getColor( String key ) {
        return colors.get( key );
    }
}

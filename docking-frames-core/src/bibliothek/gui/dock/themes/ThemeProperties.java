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
package bibliothek.gui.dock.themes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import bibliothek.gui.DockUI;

/**
 * A small description of a DockTheme, used in
 * {@link DockUI} to create a factory for a theme.
 * @author Benjamin Sigg
 */
@Retention( RetentionPolicy.RUNTIME )
@Target({ ElementType.TYPE })
public @interface ThemeProperties {
    /**
     * The key for the name in the local bundle.
     * @return the name
     */
    public String nameBundle();
    
    /**
     * The key for the description in the local bundle.
     * @return the description
     */
    public String descriptionBundle();
    
    /**
     * The authors of the theme.
     * @return the authors
     */
    public String[] authors();
    
    /**
     * URLs for the webpage of this theme.
     * @return the webpages associated to this theme
     */
    public String[] webpages();
}

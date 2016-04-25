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

package bibliothek.extension.gui.dock.theme;

import bibliothek.extension.gui.dock.theme.smooth.SmoothDefaultButtonTitleFactory;
import bibliothek.extension.gui.dock.theme.smooth.SmoothDefaultTitleFactory;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.util.Priority;

/**
 * This theme uses the {@link SmoothDefaultTitleFactory} to create some
 * titles which smoothly changes their color.
 * @author Benjamin Sigg
 *
 */
@ThemeProperties(
        nameBundle="theme.smooth", 
        descriptionBundle="theme.smooth.description",
        authors={"Benjamin Sigg"},
        webpages={})
public class SmoothTheme extends BasicTheme {
    /**
     * Constructor, sets the special title-factory of this theme
     */
    public SmoothTheme(){
        setTitleFactory( new SmoothDefaultTitleFactory(), Priority.DEFAULT );
    }
    
    @Override
    public void install(DockController controller) {
    	super.install(controller);
    	
    	controller.getDockTitleManager().registerTheme( FlapDockStation.BUTTON_TITLE_ID, new SmoothDefaultButtonTitleFactory());
    }
    
    @Override
    public void uninstall(DockController controller) {
    	super.uninstall(controller);
    	
    	controller.getDockTitleManager().clearThemeFactories();
    }
}

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
package bibliothek.gui.dock.common.intern.theme;

import java.awt.Color;

import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.color.BasicTabTransmitter;
import bibliothek.gui.dock.common.intern.color.BasicTitleTransmitter;
import bibliothek.gui.dock.themes.ColorProviderFactory;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.color.ColorManager;

/**
 * A bridge between a {@link SmoothTheme} and the common-project.
 * @author Benjamin Sigg
 */
public class CSmoothTheme extends CDockTheme<SmoothTheme> {
    /**
     * Creates a new theme.
     * @param control the controller for which this theme will be used
     * @param theme the theme that gets encapsulated
     */
    public CSmoothTheme( CControl control, SmoothTheme theme ){
        super( theme );
        init( control );
    }
    
    /**
     * Creates a new theme. This theme can be used directly with a 
     * {@link CControl}.
     * @param control the controller for which this theme will be used.
     */
    public CSmoothTheme( CControl control ){
        this( new SmoothTheme() );
        init( control );
    }
    
    /**
     * Creates a new theme.
     * @param theme the delegate which will do most of the work
     */
    private CSmoothTheme( SmoothTheme theme ){
        super( theme, new NoStackTheme( theme ) );
    }
    
    /**
     * Initializes the properties of this theme.
     * @param control the controller for which this theme will be used
     */
    private void init( final CControl control ){
        putColorProviderFactory( TabColor.class, new ColorProviderFactory<TabColor, UIBridge<Color, TabColor>>(){
            public UIBridge<Color, TabColor> create( ColorManager manager ) {
                BasicTabTransmitter transmitter = new BasicTabTransmitter( manager );
                transmitter.setControl( control );
                return transmitter;
            }
        });
        putColorProviderFactory( TitleColor.class, new ColorProviderFactory<TitleColor, UIBridge<Color, TitleColor>>(){
            public UIBridge<Color, TitleColor> create( ColorManager manager ) {
                BasicTitleTransmitter transmitter = new BasicTitleTransmitter( manager );
                transmitter.setControl( control );
                return transmitter;
            }
        });
    }
}

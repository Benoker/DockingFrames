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

import java.util.Map;

import javax.swing.Icon;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.color.BubbleDisplayerTransmitter;
import bibliothek.gui.dock.common.intern.color.BubbleTabTransmitter;
import bibliothek.gui.dock.common.intern.color.BubbleTitleTransmitter;
import bibliothek.gui.dock.themes.ColorProviderFactory;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.themes.color.DisplayerColor;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.ColorProvider;

/**
 * A theme wrapping {@link BubbleTheme} and adding additional features to
 * properly work within the common-project.
 * @author Benjamin Sigg
 *
 */
public class CBubbleTheme extends CDockTheme<BubbleTheme>{
    /**
     * Creates a new theme.
     * @param control the controller for which this theme will be used
     * @param theme the theme that gets encapsulated
     */
    public CBubbleTheme( CControl control, BubbleTheme theme ){
        super( theme );
        init( control );
    }
    
    /**
     * Creates a new theme. This theme can be used directly with a 
     * {@link CControl}.
     * @param control the controller for which this theme will be used.
     */
    public CBubbleTheme( CControl control ){
        this( new BubbleTheme() );
        init( control );
    }
    
    /**
     * Creates a new theme.
     * @param theme the delegate which will do most of the work
     */
    private CBubbleTheme( BubbleTheme theme ){
        super( theme, new NoStackTheme( theme ) );
    }
    
    /**
     * Initializes the properties of this theme.
     * @param control the controller for which this theme will be used
     */
    private void init( final CControl control ){
        putColorProviderFactory( TabColor.class, new ColorProviderFactory<TabColor, ColorProvider<TabColor>>(){
            public ColorProvider<TabColor> create( ColorManager manager ) {
                BubbleTabTransmitter transmitter = new BubbleTabTransmitter( manager );
                transmitter.setControl( control );
                return transmitter;
            }
        });
        putColorProviderFactory( TitleColor.class, new ColorProviderFactory<TitleColor, ColorProvider<TitleColor>>(){
            public ColorProvider<TitleColor> create( ColorManager manager ) {
                BubbleTitleTransmitter transmitter = new BubbleTitleTransmitter( manager );
                transmitter.setControl( control );
                return transmitter;
            }
        });
        putColorProviderFactory( DisplayerColor.class, new ColorProviderFactory<DisplayerColor, ColorProvider<DisplayerColor>>(){
            public ColorProvider<DisplayerColor> create( ColorManager manager ) {
                BubbleDisplayerTransmitter transmitter = new BubbleDisplayerTransmitter( manager );
                transmitter.setControl( control );
                return transmitter;
            }
        });
    }
    
    @Override
    public void install( DockController controller ) {
        super.install( controller );
        IconManager manager = controller.getIcons();
        Map<String, Icon> icons = DockUtilities.loadIcons(
                "data/bibliothek/gui/dock/icons/bubble/icons.ini",
                "data/bibliothek/gui/dock/icons/bubble/", CEclipseTheme.class.getClassLoader() );
        for( Map.Entry<String, Icon> entry : icons.entrySet() ){
            manager.setIconTheme( entry.getKey(), entry.getValue() );
        }
    }
    
    @Override
    public void uninstall( DockController controller ) {
        super.uninstall( controller );
        controller.getIcons().clearThemeIcons();
    }
}

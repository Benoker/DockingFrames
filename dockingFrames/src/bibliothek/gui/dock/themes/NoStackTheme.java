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

package bibliothek.gui.dock.themes;

import java.net.URI;
import java.util.ResourceBundle;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.nostack.NoStackAcceptance;
import bibliothek.gui.dock.themes.nostack.NoStackTitleFactory;
import bibliothek.gui.dock.title.DockTitleFactory;

/**
 * A {@link DockTheme} that wraps another theme and ensures that there
 * is no {@link StackDockStation} in another <code>StackDockStation</code>.
 * This theme hides some titles for the {@link StackDockStation}.
 * @author Benjamin Sigg
 */
public class NoStackTheme implements DockTheme {
    /**
     * Creates a {@link ThemeFactory} for this theme encapsulating another
     * theme.
     * @param <T> the type of the internal {@link DockTheme}
     * @param theme the theme to encapsulate
     * @param bundle the bundle to read strings for the other theme, can be <code>null</code> 
     * if the bundle of <code>ui</code> should be used.
     * @param ui the {@link DockUI} from which values should be read, can be <code>null</code>
     * if the default-DockUI should be used.
     * @return the new factory
     */
    public static <T extends DockTheme> ThemeFactory getFactory( final Class<T> theme, final ResourceBundle bundle, final DockUI ui ){
        final ThemeFactory factory = new ThemePropertyFactory<T>( theme, bundle, ui );
        
        return new ThemeFactory(){
            public DockTheme create() {
                return new NoStackTheme( factory.create() );
            }
            
            public URI[] getWebpages() {
                return factory.getWebpages();
            }
            
            public String[] getAuthors() {
                String[] authors = factory.getAuthors();
                final String BENI = "Benjamin Sigg";
                for( String author : authors ){
                    if( author.equals( BENI ))
                        return authors;
                }
                
                String[] result = new String[ authors.length + 1 ];
                System.arraycopy( authors, 0, result, 0, authors.length );
                result[ authors.length ] = BENI;
                return result;
            }
            
            public String getDescription() {
                if( ui == null )
                    return DockUI.getDefaultDockUI().getString( "theme.small.description" );
                else
                    return ui.getString( "theme.small.description" );
            }

            public String getName() {
                String name = factory.getName();
                
                String small = null;
                if( ui != null )
                    small = ui.getString( "theme.small" );
                else
                    small = DockUI.getDefaultDockUI().getString( "theme.small" );
                
                if( name == null )
                    return small;
                else
                    return small + " \"" + name + "\"";
            }
        };
    }
    
    /** The delegate theme to get the basic factories */
    private DockTheme base;
    
    /** {@link DockAcceptance} ensuring no nested stacks */
    private NoStackAcceptance acceptance = new NoStackAcceptance();
    
    /**
     * Creates a new theme
     * @param base the wrapped theme, it is used as a delegate to get
     * some factories.
     */
    public NoStackTheme( DockTheme base ){
        if( base == null )
            throw new IllegalArgumentException( "Base theme must not be null" );
        
        this.base = base;
    }
    
    public Combiner getCombiner( DockStation station ) {
        return base.getCombiner( station );
    }

    public DisplayerFactory getDisplayFactory( DockStation station ) {
        return base.getDisplayFactory( station );
    }

    public StationPaint getPaint( DockStation station ) {
        return base.getPaint( station );
    }

    public DockTitleFactory getTitleFactory( DockController controller ) {
        return new NoStackTitleFactory( base.getTitleFactory(controller));
    }

    public DockableMovingImageFactory getMovingImageFactory( DockController controller ) {
        return base.getMovingImageFactory( controller );
    }
    
    public DockableSelection getDockableSelection( DockController controller ) {
        return base.getDockableSelection( controller );
    }
    
    public void install( DockController controller ) {
        base.install( controller );
        controller.addAcceptance( acceptance );
    }
    
    public void uninstall(DockController controller) {
    	base.uninstall( controller );
        controller.removeAcceptance( acceptance );
    }
}

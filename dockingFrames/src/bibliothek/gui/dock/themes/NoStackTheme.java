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

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.nostack.NoStackAcceptance;
import bibliothek.gui.dock.themes.nostack.NoStackTitleFactory;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.MovingTitleGetter;

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
     * @param theme the theme to encapsulate
     * @param bundle the bundle to read strings for the other theme, can be <code>null</code> 
     * if the bundle of <code>ui</code> should be used.
     * @param ui the {@link DockUI} from which values should be read, can be <code>null</code>
     * if the default-DockUI should be used.
     * @return the new factory
     */
    public static ThemeFactory getFactory( final Class<? extends DockTheme> theme, final ResourceBundle bundle, final DockUI ui ){
        final ThemeProperties properties = theme.getAnnotation( ThemeProperties.class );
        if( properties == null )
            throw new IllegalArgumentException( "Class " + theme.getName() + " must have the annotation ThemeProperties" );
        
        try{
            final Constructor<? extends DockTheme> constructor = theme.getConstructor( new Class[0] );

            return new ThemeFactory(){
                public DockTheme create() {
                    try {
                        return new NoStackTheme( constructor.newInstance( new Object[0] ) );
                    }
                    catch( Exception e ){
                        System.err.println( "Can't create theme due an unknown reason" );
                        e.printStackTrace();
                        return null;
                    }
                }

                public String[] getAuthors() {
                    String[] authors = properties.authors();
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
                    String name = null;
                    if( bundle != null )
                        name = bundle.getString( properties.nameBundle() );
                    else if( ui != null )
                        name = ui.getString( properties.nameBundle() );
                    else
                        name = DockUI.getDefaultDockUI().getString( properties.nameBundle() );
                    
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

                public URL[] getWebpages() {
                    try{
                        String[] urls = properties.webpages();
                        URL[] result = new URL[ urls.length ];
                        for( int i = 0; i < result.length; i++ )
                            result[i] = new URL( urls[i] );
                    
                        return result;
                    }
                    catch( MalformedURLException ex ){
                        System.err.print( "Can't create urls due an unknown reason" );
                        ex.printStackTrace();
                        return null;
                    }
                }
            };
        }
        catch( NoSuchMethodException ex ){
            throw new IllegalArgumentException( "Missing default constructor for theme", ex );
        }        
    }
    
    /** The delegate theme to get the basic factories */
    private DockTheme base;
    
    /** the acceptances which were used before this theme was installed to a controller */
    private Map<DockController, DockAcceptance> acceptances = new HashMap<DockController, DockAcceptance>();
    
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

    public MovingTitleGetter getMovingTitleGetter( DockController controller ) {
        return base.getMovingTitleGetter( controller );
    }
    
    public void install( DockController controller ) {
        base.install( controller );
        DockAcceptance acceptance = controller.getAcceptance();
        acceptances.put( controller, acceptance );
        NoStackAcceptance noStack = new NoStackAcceptance();
        if( acceptance == null )
            controller.setAcceptance( noStack );
        else
            controller.setAcceptance( noStack.andAccept( acceptance ));
    }
    
    public void uninstall(DockController controller) {
    	base.uninstall( controller );
    	controller.setAcceptance( acceptances.remove( controller ));
    }
}

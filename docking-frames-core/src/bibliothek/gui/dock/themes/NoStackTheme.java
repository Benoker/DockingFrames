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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
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
     * @return the new factory
     */
    public static <T extends DockTheme> ThemeFactory getFactory( final Class<T> theme ){
        final ThemeFactory factory = new ThemePropertyFactory<T>( theme );
        
        return new ThemeFactory(){
            public DockTheme create( DockController controller ) {
                return new NoStackTheme( factory.create( controller ) );
            }
            
            public ThemeMeta createMeta( DockController controller ){
            	return new Meta( this, controller, factory.createMeta( controller ));
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
    
    public void install( DockController controller, DockThemeExtension[] extensions ){    	
        base.install( controller, extensions );
        controller.addAcceptance( acceptance );
    }
    
    public void uninstall(DockController controller) {
    	base.uninstall( controller );
        controller.removeAcceptance( acceptance );
    }
    

    private static class Meta extends DefaultThemeMeta implements ThemeMetaListener{
    	private ThemeMeta meta;
    	
		public Meta( ThemeFactory factory, DockController controller, ThemeMeta meta ){
			super( factory, controller, "theme.small", "theme.small.description", meta.getAuthors(), meta.getWebpages() );
			this.meta = meta;
		}
    	
		@Override
		public void addListener( ThemeMetaListener listener ){
			if( !hasListeners() ){
				meta.addListener( this );
			}
			super.addListener( listener );
		}
		
		@Override
		public void removeListener( ThemeMetaListener listener ){
			super.removeListener( listener );
			if( !hasListeners() ){
				meta.removeListener( this );
			}
		}
		
		@Override
		public String getName(){
			String small = super.getName();
			String factory = meta.getName();
			
			return small  + " \"" + factory + "\"";
		}
		
		@Override
		public String[] getAuthors(){
			String[] authors = getAuthors();
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
		
		public void authorsChanged( ThemeMeta meta ){
			setAuthors( meta.getAuthors() );
		}
		
		public void webpagesChanged( ThemeMeta meta ){
			setWebpages( meta.getWebpages() );
		}
		
		public void descriptionChanged( ThemeMeta meta ){
			// ignore
		}
		
		public void nameChanged( ThemeMeta meta ){
			fireNameChanged();
		}
    }
}

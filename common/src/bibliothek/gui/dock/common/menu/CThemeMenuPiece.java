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
package bibliothek.gui.dock.common.menu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.facile.menu.ThemeMenuPiece;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.themes.ThemePropertyFactory;
import bibliothek.util.xml.XElement;

/**
 * A {@link ThemeMenuPiece} that uses the default {@link DockTheme}s of
 * DockingFrames, but only in the {@link NoStackTheme no-stack} version.
 * @author Benjamin Sigg
 */
public class CThemeMenuPiece extends ThemeMenuPiece{
    /**
     * Creates a new piece.
     * @param control the control whose theme might be changed
     */
    public CThemeMenuPiece( CControl control ) {
        super( control.intern().getController(), false );
        init( control );
    }
    
    /**
     * Adds the factories to this piece.
     * @param control the control whose theme might be changed
     */
    private void init( CControl control ){
        ThemeFactory flat = new NoStackFactory( new ThemePropertyFactory<FlatTheme>( FlatTheme.class ) );
        ThemeFactory bubble = new NoStackFactory( new ThemePropertyFactory<BubbleTheme>( BubbleTheme.class ) );
        ThemeFactory eclipse = new ThemePropertyFactory<EclipseTheme>( EclipseTheme.class );
        ThemeFactory smooth = new NoStackFactory( new ThemePropertyFactory<SmoothTheme>( SmoothTheme.class ) );
        ThemeFactory basic = new NoStackFactory( new ThemePropertyFactory<BasicTheme>( BasicTheme.class ) );
        
        add( basic );
        add( smooth );
        add( flat );
        add( bubble );
        add( eclipse );
        
        setSelected( smooth );
        
        try {
            control.getResources().put( "CThemeMenuPiece", new ApplicationResource(){
                public void write( DataOutputStream out ) throws IOException {
                    out.writeInt( 1 );
                    out.writeInt( indexOf( getSelected() ) );
                }
                public void read( DataInputStream in ) throws IOException {
                    if( in.readInt() == 1 ){
                        int index = in.readInt();
                        if( index >= 0 && index < getFactoryCount() )
                            setSelected( getFactory( index ) );
                    }
                }
                public void writeXML( XElement element ) {
                    element.setInt( indexOf( getSelected() ) );
                }
                public void readXML( XElement element ) {
                    int index = element.getInt();
                    if( index >= 0 && index < getFactoryCount() )
                        setSelected( getFactory( index ) );
                }
            });
        }
        catch( IOException e ) {
            System.err.println( "Non-lethal IO-error:" );
            e.printStackTrace();
        }
    }
    
    /**
     * A factory creating new {@link NoStackTheme}s.
     * @author Benjamin Sigg
     */
    private static class NoStackFactory implements ThemeFactory{
        /** the creator of the base theme */
        private ThemeFactory delegate;
        
        /**
         * Creates a new factory
         * @param delegate the creator of the base theme
         */
        public NoStackFactory( ThemeFactory delegate ){
            this.delegate = delegate;
        }
        
        public DockTheme create() {
            return new NoStackTheme( delegate.create() );
        }

        public String[] getAuthors() {
            return delegate.getAuthors();
        }

        public String getDescription() {
            return delegate.getDescription();
        }

        public String getName() {
            return delegate.getName();
        }

        public URI[] getWebpages() {
            return delegate.getWebpages();
        }
    }
}

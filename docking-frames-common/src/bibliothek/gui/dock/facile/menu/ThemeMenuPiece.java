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
package bibliothek.gui.dock.facile.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JRadioButtonMenuItem;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.common.theme.ThemeMapListener;
import bibliothek.gui.dock.support.menu.BaseMenuPiece;
import bibliothek.gui.dock.support.menu.MenuPiece;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.themes.ThemeMeta;
import bibliothek.gui.dock.themes.ThemeMetaListener;
import bibliothek.util.ClientOnly;

/**
 * A {@link MenuPiece} that can change the {@link DockTheme}.
 * @author Benjamin Sigg
 */
@ClientOnly
public class ThemeMenuPiece extends BaseMenuPiece {
    /** the controller whose theme might be changed */
    private DockController controller;
    
    /** the items shown by this piece */
    private List<Item> items = new ArrayList<Item>();
    
    /** the list of available themes */
    private ThemeMap themes;
    
    /**
     * Whether it is the responsibility of this menu to transfer the changes
     * of {@link #themes} to {@link #controller} or not 
     */
    private boolean transferTheme = true;
    
    /** a listener for {@link #themes} */
    private ThemeMapListener listener = new ThemeMapListener(){
        public void changed( ThemeMap map, int index, String key, ThemeFactory oldFactory, ThemeFactory newFactory ) {
            if( oldFactory != null ){
                items.remove( index );
                remove( index );
            }
            
            if( newFactory != null ){
                Item item = new Item( key, newFactory );
                items.add( index, item );
                insert( index, item );
            }
        }
        
        public void selectionChanged( ThemeMap map, String oldKey, String newKey ) {
            for( Item item : items ){
                item.setSelected( item.getKey().equals( newKey ) );
            }
            
            if( controller != null && transferTheme ){
                ThemeFactory factory = themes.getSelectedFactory();
                if( factory != null ){
                    controller.setTheme( factory.create( controller ) );
                }
            }
        }
    };
    
    /**
     * Creates a new piece. The {@link #setTransferTheme(boolean) transfer-flag}
     * will be set to <code>true</code>.
     * @param controller the controller whose theme might be changed, can be <code>null</code>
     * @param defaultThemes whether the piece should be filled up with the
     * factories that can be obtained through the {@link DockUI}
     */
    public ThemeMenuPiece( DockController controller, boolean defaultThemes ) {
        setController( controller );
        setTransferTheme( true );
        
        ThemeMap themes = new ThemeMap();
        
        if( defaultThemes ){
            DockUI ui = DockUI.getDefaultDockUI();
            int index = 0;
            for( ThemeFactory theme : ui.getThemes() ){
                themes.add( String.valueOf( index++ ), theme );
            }
            themes.select( ui.getDefaultTheme() );
        }
        
        setThemes( themes );
    }
    
    /**
     * Creates a new piece using the themes of <code>map</code>. The
     * {@link #setTransferTheme(boolean) transfer-flag} will be set to <code>false</code>.
     * @param controller the controller, will just be stored but not used
     * unless {@link #setTransferTheme(boolean)} is called with the argument
     * <code>true</code>. Can be <code>null</code>
     * @param map the list of themes, can be <code>null</code>
     */
    public ThemeMenuPiece( DockController controller, ThemeMap map ){
        setTransferTheme( false );
        setController( controller );
        setThemes( map );
    }
    
    /**
     * Instructs this piece whether it should transfer the {@link DockTheme}
     * from its {@link #getThemes() map} to the {@link #getController() controller}. 
     * @param transferTheme <code>true</code> if this piece should transfer the theme
     */
    public void setTransferTheme( boolean transferTheme ) {
        this.transferTheme = transferTheme;
    }
    
    /**
     * Tells whether this piece is transfers the {@link DockTheme} from
     * its {@link #getThemes() map} to the {@link #getController() controller}.
     * @return <code>true</code> if this piece transfers the theme
     * @see #setTransferTheme(boolean)
     */
    public boolean isTransferTheme() {
        return transferTheme;
    }

    /**
     * Severs all connections of this {@link ThemeMenuPiece} with other objects,
     * allowing the garbage collector to remove this.
     */
    public void destroy(){
    	setThemes( null );
    	setController( null );
    }
    
    /**
     * Sets the themes which this piece offers
     * @param themes the offered themes, can be <code>null</code>
     */
    public void setThemes( ThemeMap themes ) {
        if( this.themes != themes ){
        	if( isBound() ){
        		uninstall();
        	}
            
            this.themes = themes;
            
            if( isBound() ){
            	install();
            }
        }
    }
    
    @Override
    public void bind(){
    	if( !isBound() ){
    		super.bind();
    		install();
    	}
    }
    
    @Override
    public void unbind(){
    	if( isBound() ){
    		super.unbind();
    		uninstall();
    	}
    }
    
    private void install(){
    	if( themes != null ){
            themes.addThemeMapListener( listener );
            
            String selected = themes.getSelectedKey();
            
            for( int i = 0, n = themes.size(); i<n; i++ ){
                Item item = new Item( themes.getKey( i ), themes.getFactory( i ));
                items.add( item );
                add( item );
                
                item.setSelected( item.getKey().equals( selected ) );
            }
            
            if( transferTheme ){
                if( controller != null ){
                    ThemeFactory factory = themes.getSelectedFactory();
                    if( factory != null ){
                        controller.setTheme( factory.create( controller ) );
                    }
                }
            }
        }
    }
    
    private void uninstall(){
    	if( this.themes != null ){
            this.themes.removeThemeMapListener( listener );
            
            removeAll();
            items.clear();
        }
    }
    
    /**
     * Gets the set of themes used by this piece.
     * @return the set of themes
     */
    public ThemeMap getThemes() {
        return themes;
    }
    
    /**
     * Gets the controller whose theme might be changed by this piece.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Sets the controller whose theme might be changed by this piece. The
     * theme of the controller is changed if there is a selection on this
     * piece.
     * @param controller the new controller, can be <code>null</code>
     */
    public void setController( DockController controller ) {
        this.controller = controller;
        if( controller != null && themes != null && transferTheme ){
            ThemeFactory selection = themes.getSelectedFactory();
            
            if( selection != null )
                controller.setTheme( selection.create( controller ) );
        }
        for( Item item : items ){
        	item.setController( controller );
        }
    }
    
    /**
     * An item that changes the theme when selected.
     * @author Benjamin Sigg
     */
    private class Item extends JRadioButtonMenuItem implements ActionListener, ThemeMetaListener{
        /** the name of this factory */
        private String key;
 
        /** the factory represented by this item */
        private ThemeFactory factory;
        
        /** information about the current factory */
        private ThemeMeta meta;
        
        /**
         * Creates a new item.
         * @param key the name of the factory
         * @param factory the factory used to create a theme
         */
        public Item( String key, ThemeFactory factory ){
            this.key = key;
            this.factory = factory;
            addActionListener( this );
            setController( getController() );
        }
        
        /**
         * Sets the controller in whose realm this piece should work.
         * @param controller the controller
         */
        public void setController( DockController controller ){
        	if( meta != null ){
        		meta.removeListener( this );
        		
        		setText( "" );
        		setToolTipText( "" );
        		
        		meta = null;
        	}
        	if( controller != null ){
        		meta = factory.createMeta( controller );
        		meta.addListener( this );
        		setText( meta.getName() );
        		setToolTipText( meta.getDescription() );
        	}
        }
        
        public void actionPerformed( ActionEvent e ) {
            themes.select( key );
        }
        
        public void authorsChanged( ThemeMeta meta ){
        	// ignore
        }
        
        public void descriptionChanged( ThemeMeta meta ){
	        setToolTipText( meta.getDescription() );	
        }
        
        public void nameChanged( ThemeMeta meta ){
        	setText( meta.getName() );
        }
        
        public void webpagesChanged( ThemeMeta meta ){
        	// ignore
        }
        
        /**
         * Gets the key of the factory.
         * @return the key
         */
        public String getKey() {
            return key;
        }
    }
}

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

package bibliothek.gui.dock.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;

import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A list of methods which can be used for different purposes. Methods
 * related to the {@link DockTheme} can be found in {@link DockUI}.
 * @author Benjamin Sigg
 */
public class DockUtilities {
    /**
     * A visitor used to visit the nodes of a dock-tree.
     * @author Benjamin Sigg
     */
    public static abstract class DockVisitor{
        /**
         * Invoked to visit <code>dockable</code>.
         * @param dockable the visited element 
         */
        public void handleDockable( Dockable dockable ){ /* do nothing */ }
        
        /**
         * Invoked to visit <code>station</code>.
         * @param station the visited element
         */
        public void handleDockStation( DockStation station ){ /* do nothing */ }
    }
    
    /**
     * Visits <code>dockable</code> and all its children.
     * @param dockable the first element to visit
     * @param visitor a delegate
     */
    public static void visit( Dockable dockable, DockVisitor visitor ){
        visitDockable( dockable, visitor );
    }
    
    /**
     * Visits <code>station</code> and all its children.
     * @param station the first element to visit
     * @param visitor a delegate
     */
    public static void visit( DockStation station, DockVisitor visitor ){
        Dockable dockable = station.asDockable();
        if( dockable != null )
            visitDockable( dockable, visitor );
        else
            visitStation( station, visitor );
    }
    
    /**
     * Visits <code>dockable</code> and all its children.
     * @param dockable the first element to visit
     * @param visitor a delegate
     */
    private static void visitDockable( Dockable dockable, DockVisitor visitor ){
        visitor.handleDockable( dockable );
        DockStation station = dockable.asDockStation();
        if( station != null )
            visitStation( station, visitor );
    }
    
    /**
     * Visits <code>station</code> and all its children.
     * @param station the first element to visit
     * @param visitor a delegate
     */
    private static void visitStation( DockStation station, DockVisitor visitor ){
        visitor.handleDockStation( station );
        for( int i = 0, n = station.getDockableCount(); i<n; i++ )
            visitDockable( station.getDockable( i ), visitor );
    }
    
    
    /**
     * Tells whether <code>child</code> is identical with <code>ancestor</code>
     * or a child of <code>ancestor</code>.
     * @param ancestor an element
     * @param child another element
     * @return <code>true</code> if <code>ancestor</code> is a parent of or
     * identical with <code>child</code>. 
     */
    public static boolean isAncestor( DockElement ancestor, DockElement child ){
        if( ancestor == null )
            throw new NullPointerException( "ancestor must not be null" );
        
        if( child == null )
            throw new NullPointerException( "child must not be null" );
        
        Dockable dockable = child.asDockable();
        DockStation station = null;
        
        while( dockable != null ){
            if( ancestor == dockable )
                return true;
            
            station = dockable.getDockParent();
            dockable = station == null ? null : station.asDockable();
        }
        
        return station == ancestor;
    }
    
    /**
     * Searches the station which is an ancestor of <code>element</code>
     * and has no parent.
     * @param element the element whose oldest parent is searched
     * @return the root, may be <code>null</code> if element has no parent
     */
    public static DockStation getRoot( DockElement element ){
        Dockable dockable = element.asDockable();
        if( dockable == null )
            return element.asDockStation();
        
        DockStation parent = dockable.getDockParent();
        if( parent == null )
        	return element.asDockStation();
        
        while( true ){
            dockable = parent.asDockable();
            if( dockable == null || dockable.getDockParent() == null )
                return parent;
            parent = dockable.getDockParent();
        }
    }
    
    /**
     * Gets a {@link DockableProperty} which describes the path from the
     * {@link #getRoot(DockElement) root} to <code>dockable</code>.
     * @param dockable a Dockable whose location is searched
     * @return the properties or <code>null</code> if <code>dockable</code> 
     * has no parent
     */
    public static DockableProperty getPropertyChain( Dockable dockable ){
        DockStation station = getRoot( dockable );
        if( station == null || station == dockable )
            return null;
        
        return getPropertyChain( station, dockable );
    }
    
    /**
     * Creates a {@link DockableProperty} describing the path from
     * <code>ground</code> to <code>dockable</code>.
     * @param ground the base of the property
     * @param dockable an indirect child of <code>ground</code>
     * @return a property for the path <code>ground</code> to <code>dockable</code>.
     * @throws IllegalArgumentException if <code>ground</code> is not an
     * ancestor of <code>dockable</code>
     */
    public static DockableProperty getPropertyChain( DockStation ground, Dockable dockable ){
        if( ground == dockable )
            throw new IllegalArgumentException( "ground and dockable are the same" );
        
        DockStation parent = dockable.getDockParent();
        DockableProperty property = parent.getDockableProperty( dockable );
        
        while( true ){
            if( parent == ground )
                return property;
            
            dockable = parent.asDockable();
            if( dockable == null )
                throw new IllegalArgumentException( "The chain is not complete" );
            
            parent = dockable.getDockParent();
            if( parent == null )
                throw new IllegalArgumentException( "The chain is not complete" );
            
            DockableProperty temp = parent.getDockableProperty( dockable );
            temp.setSuccessor( property );
            property = temp;
        }
    }
    
    /**
     * Searches a {@link Component} which is {@link Component#isShowing() showing}
     * and has something to do with <code>dockable</code>.
     * @param dockable a Dockable for which a Component has to be found
     * @return a showing component or <code>null</code>
     */
    public static Component getShowingComponent( Dockable dockable ){
        Component component = dockable.getComponent();
        if( !component.isShowing() ){
            
            for( DockTitle title : dockable.listBoundTitles() ){
                component = title.getComponent();
                if( component.isShowing() )
                    break;
            }
        }
        
        if( component.isShowing() )
            return component;
        else
            return null;
    }
    
    /**
     * Ensures that <code>newChild</code> has no parent, and that there will
     * be no cycle when <code>newChild</code> is added to <code>newParent</code>
     * @param newParent the element that becomes parent of <code>newChild</code>
     * @param newChild the element that becomes child of <code>newParent</code>
     * @throws NullPointerException if either <code>newParent</code> or <code>newChild</code> is <code>null</code>
     * @throws IllegalArgumentException if there would be a cycle introduced
     * @throws IllegalStateException if the old parent of <code>newChild</code> does not
     * allow to remove its child
     */
    public static void ensureTreeValidity( DockStation newParent, Dockable newChild ){
        if( newParent == null )
            throw new NullPointerException( "parent must not be null" );
        
        if( newChild == null )
            throw new NullPointerException( "child must not be null" );
        
        DockStation oldParent = newChild.getDockParent();
            
        // check no self reference
        if( newChild == newParent )
            throw new IllegalArgumentException( "child and parent are the same" );
        
        // check no cycles
        if( isAncestor( newChild, newParent )){
            if( newChild.getDockParent() == newParent )
                newParent.drag( newChild );
            else
                throw new IllegalArgumentException( "can't create a cycle" );
        }
        
        // remove old parent
        if( oldParent != null ){
            if( oldParent != newParent && !oldParent.canDrag( newChild ))
                throw new IllegalStateException( "old parent of child does not want do release the child" );
            
            oldParent.drag( newChild );
        }
    }
    
    /**
     * Gets a "disabled" icon according to the current look and feel.
     * @param parent the component on which the icon will be painted, can be <code>null</code>
     * @param icon an icon or <code>null</code>
     * @return a disabled version of <code>icon</code> or <code>null</code>
     */
    public static Icon disabledIcon( JComponent parent, Icon icon ){
    	if( icon == null )
    		return null;
    	
        Icon result = UIManager.getLookAndFeel().getDisabledIcon( parent, icon );
        if( result != null )
        	return result;
        
        if( parent != null ){
        	BufferedImage image = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
        	Graphics g = image.createGraphics();
        	icon.paintIcon( parent, g, 0, 0 );
        	g.dispose();
        	icon = new ImageIcon( image );
        	result = UIManager.getLookAndFeel().getDisabledIcon( parent, icon );
        }
        
        if( result != null )
        	return result;
        
        return icon;
    }
    
    /**
     * Transforms <code>icon</code> into an image.
     * @param icon some icon
     * @return the image of the icon or <code>null</code>
     */
    public static Image iconImage( Icon icon ){
        if( icon instanceof ImageIcon )
            return ((ImageIcon)icon).getImage();
        
        return null;
    }
    
    /**
     * Loads a map of icons.
     * @param list a path to a property-file containing key-path-pairs.
     * @param path the base path to the icons, will be added before any
     * path of the property file, can be <code>null</code>
     * @param loader used to transform paths into urls.
     * @return the map of {@link Icon}s, the map can be empty if no icons were found
     * @see Properties#load(InputStream)
     */
    public static Map<String, Icon> loadIcons( String list, String path, ClassLoader loader ){
        try{
            InputStream in = loader.getResourceAsStream( list );
            if( in == null )
                return new HashMap<String, Icon>();
            
            Properties properties = new Properties();
            properties.load( in );
            in.close();
            
            Map<String, Icon> result = new HashMap<String, Icon>();
            for( Map.Entry<Object, Object> entry : properties.entrySet() ){
                String key = (String)entry.getKey();
                String file = (String)entry.getValue();
                if( path != null )
                    file = path + file;
                
                URL url = loader.getResource( file );
                if( url == null ){
                    System.err.println( "Missing file: " + file );
                }
                else{
                    ImageIcon icon = new ImageIcon( url );
                    result.put( key, icon );
                }
            }
            
            return result;
        }
        catch( IOException ex ){
            ex.printStackTrace();
            return new HashMap<String, Icon>();
        }
    }
    
    /**
     * Removes the current title of <code>displayer</code> and sets a new
     * title. The {@link Dockable} of <code>displayer</code> is used to create
     * the new title.
     * @param displayer some displayer
     * @param version the kind of title that should be created, can be <code>null</code>
     * to indicate that no title should be created.
     */
    public static void exchangeTitle( DockableDisplayer displayer, DockTitleVersion version ){
        Dockable dockable = displayer.getDockable();
        if( dockable == null )
            throw new IllegalArgumentException( "Missing dockable for displayer" );
        
        DockTitle title = displayer.getTitle();
        if( title != null ){
            dockable.unbind( title );
        }
        
        if( version != null ){
            title = dockable.getDockTitle( version );
            if( title != null ){
                dockable.bind( title );
            }
        }
        else{
            title = null;
        }
        
        displayer.setTitle( title );
    }
}

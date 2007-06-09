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

package bibliothek.gui.dock.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockableProperty;
import bibliothek.gui.dock.title.DockTitle;

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
     * Tells whether <code>child</code> is identical with <code>anchestor</code>
     * or a child of <code>anchestor</code>.
     * @param anchestor an element
     * @param child another element
     * @return <code>true</code> if <code>anchestor</code> is a parent of or
     * identical with <code>child</code>. 
     */
    public static boolean isAnchestor( DockElement anchestor, DockElement child ){
        Dockable dockable = child.asDockable();
        while( dockable != null ){
            if( anchestor == dockable )
                return true;
            
            DockStation station = dockable.getDockParent();
            dockable = station == null ? null : station.asDockable();
        }
        
        return false;
    }
    
    /**
     * Searches the station which is an anchestor of <code>element</code>
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
     * anchestor of <code>dockable</code>
     */
    public static DockableProperty getPropertyChain( DockStation ground, Dockable dockable ){
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
            DockStation station = dockable.getDockParent();
            if( station == null )
                return null;
            
            for( DockTitle title : station.getDockTitles( dockable )){
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
}

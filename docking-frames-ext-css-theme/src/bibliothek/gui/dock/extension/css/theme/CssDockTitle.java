/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.theme;

import java.awt.Graphics;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.extension.css.CssPath;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.DefaultCssItem;
import bibliothek.gui.dock.extension.css.doc.CssDocKey;
import bibliothek.gui.dock.extension.css.doc.CssDocPath;
import bibliothek.gui.dock.extension.css.doc.CssDocPathNode;
import bibliothek.gui.dock.extension.css.doc.CssDocProperty;
import bibliothek.gui.dock.extension.css.doc.CssDocSeeAlso;
import bibliothek.gui.dock.extension.css.doc.CssDocText;
import bibliothek.gui.dock.extension.css.path.DefaultCssNode;
import bibliothek.gui.dock.extension.css.path.DefaultCssPath;
import bibliothek.gui.dock.extension.css.path.MultiCssPath;
import bibliothek.gui.dock.extension.css.property.paint.CssPaint;
import bibliothek.gui.dock.extension.css.property.shape.CssShape;
import bibliothek.gui.dock.extension.css.property.shape.ShapeCssProperty;
import bibliothek.gui.dock.extension.css.theme.title.TitleFontModifierProperty;
import bibliothek.gui.dock.extension.css.theme.title.TitleIconTextGapProperty;
import bibliothek.gui.dock.extension.css.transition.CssPaintTransitionProperty;
import bibliothek.gui.dock.extension.css.tree.CssTree;
import bibliothek.gui.dock.extension.css.util.CssMouseAdapter;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This title makes use of a {@link CssScheme} to set up its look.
 * @author Benjamin Sigg
 */
@CssDocSeeAlso({
	TitleFontModifierProperty.class,
	TitleIconTextGapProperty.class})
public class CssDockTitle extends AbstractDockTitle{
	private CssScheme css;
	
	private DefaultCssNode self;
	private CssPath selfPath;
	
	private DefaultCssItem item;

	@CssDocProperty(
			path=@CssDocPath(referenceId="self"),
			property=@CssDocKey(key="background"))
	private CssPaint background;
	
	@CssDocProperty(
			path=@CssDocPath(referenceId="self"),
			property=@CssDocKey(key="shape"))
	private CssShape shape;
	
	/**
	 * Creates a new title
	 * @param css access to all the css data
	 * @param dockable the dockable whose title this is
	 * @param origin the version which was used to create this title
	 */
	public CssDockTitle( CssScheme css, Dockable dockable, DockTitleVersion origin ){
		super( dockable, origin, true );
		this.css = css;
		self = new DefaultCssNode( "title" );
		updateSelf();
		CssMouseAdapter adapter = new CssMouseAdapter(){
			@Override
			protected void added( String pseudoClass ){
				self.addPseudoClass( pseudoClass );
			}
			@Override
			protected void removed( String pseudoClass ){
				self.removePseudoClass( pseudoClass );
			}
		};
		addMouseInputListener( adapter );
	}
	
	@Override
	public void bind(){
		super.bind();
		CssPath elementPath = css.getTree().getPathFor( getElement() );
		selfPath = new MultiCssPath( elementPath, new DefaultCssPath( self ) );
		
		item = new DefaultCssItem( selfPath );
		
		item.putProperty( "background", new CssPaintTransitionProperty( css, item ){
			@Override
			protected void propertyChanged( CssPaint value ){
				background = value;
				repaint();
			}
		});
		
		item.putProperty( "shape", new ShapeCssProperty(){
			@Override
			protected void propertyChanged( CssShape value ){
				shape = value;
				repaint();
			}
		} );
		
		item.putProperty( "fontmodifier", new TitleFontModifierProperty( this, css, item ));
		
		item.putProperty( "icontextgap", new TitleIconTextGapProperty( this ));
		
		css.add( item );
	}
    
	@Override
	public void unbind(){
		super.unbind();
		css.remove( item );
		item = null;
	}
	
    @Override
    protected void paintBackground( Graphics g, JComponent component ) {
    	if( background != null ){
    		background.paintArea( g, component, shape );
    	}
    }
    
    @Override
    public boolean contains( int x, int y ){
    	if( !super.contains( x, y )){
    		return false;
    	}
		if( shape != null ){
			shape.setSize( getWidth(), getHeight() );
			return shape.contains( x, y );
		}
    	return true;
    }
    
	@Override
	public void setActive( boolean active ){
		super.setActive( active );
		updateSelf();
	}
	
	@Override
	public void setOrientation( Orientation orientation ){
		super.setOrientation( orientation );
		updateSelf();
	}
	
	@CssDocPath(id="self",
			parentId="getPathFor",
			parentClass = CssTree.class,
			description=@CssDocText(format="Path to a %s.", arguments={"CssDockTitle"}),
			nodes={@CssDocPathNode(
					name=@CssDocKey(key="title"),
					pseudoClasses={
							@CssDocKey(key="selected", description=@CssDocText(text="Applied if the title is selected"))},
					properties={
							@CssDocKey(key="side", description=@CssDocText(text="Depends on the orientation of the title, one of 'east', 'west', 'south', 'north' or 'free'")),
							@CssDocKey(key="horizontal", description=@CssDocText(text="'true' if the title is horizontal")),
							@CssDocKey(key="vertical", description=@CssDocText(text="'true' if the title is vertical")),
							@CssDocKey(reference=CssMouseAdapter.class)}
					)})
	private void updateSelf(){
		if( self != null ){
			if( isActive() ){
				self.addPseudoClass( "selected" );
			}
			else{
				self.removePseudoClass( "selected" );
			}
			
			String side = null;
			boolean horizontal = false;
			
			switch( getOrientation() ){
				case EAST_SIDED:
					side = "east";
					horizontal = false;
					break;
				case FREE_HORIZONTAL:
					side = "free";
					horizontal = true;
					break;
				case FREE_VERTICAL:
					side = "free";
					horizontal = false;
					break;
				case NORTH_SIDED:
					side = "north";
					horizontal = true;
					break;
				case SOUTH_SIDED:
					side = "south";
					horizontal = true;
					break;
				case WEST_SIDED:
					side = "west";
					horizontal = false;
					break;
			}
			
			self.putProperty( "side", side );
			if( horizontal ){
				self.putProperty( "horizontal", "true" );
				self.putProperty( "vertical", null );
			}
			else{
				self.putProperty( "horizontal", null );
				self.putProperty( "vertical", "true" );
			}
		}
	}	
}

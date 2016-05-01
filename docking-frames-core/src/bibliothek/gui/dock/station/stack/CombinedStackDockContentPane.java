/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack;

import java.awt.Dimension;

import javax.swing.JPanel;

import bibliothek.gui.dock.focus.DockFocusTraversalPolicy;
import bibliothek.gui.dock.station.stack.tab.TabLayoutManager;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.Transparency;

/**
 * This panel paints the contents of a {@link CombinedStackDockComponent}. It is just a {@link JPanel}. The layout has to be 
 * managed by a {@link TabLayoutManager}. This panel is also a {@link #setFocusTraversalPolicyProvider(boolean)
 * focus traversal policy provider}.
 * @author Benjamin Sigg
 */
public class CombinedStackDockContentPane extends ConfiguredBackgroundPanel{
	private CombinedStackDockComponent<?, ?, ?> parent;
	
	private boolean paintBackground = true;
	
	/**
	 * Creates a new content pane
	 * @param parent the owner of this pane, not <code>null</code>
	 */
	public CombinedStackDockContentPane( CombinedStackDockComponent<?, ?, ?> parent ){
		super( null, Transparency.TRANSPARENT );
		if( parent == null )
			throw new IllegalArgumentException( "parent must not be null" );
		this.parent = parent;
		setFocusTraversalPolicyProvider( true );
		setFocusTraversalPolicy( new DockFocusTraversalPolicy( new CombinedStackDockFocusTraversalPolicy( this ), true ) );
	}
	
	/**
	 * Tells this panel whether the background should be painted or not.
	 * @param paintBackground whether to paint a background
	 */
	public void setPaintBackground( boolean paintBackground ){
		this.paintBackground = paintBackground;
		if( paintBackground ){
			setTransparency( Transparency.DEFAULT );
		}
		else{
			setTransparency( Transparency.TRANSPARENT );
		}
	}
	
	/**
	 * Tells whether a background should be painted or not
	 * @return whether a background should be painted
	 */
	public boolean isPaintBackground(){
		return paintBackground;
	}
		
	/**
	 * Gets the owner of this pane.
	 * @return the owner, not <code>null</code>
	 */
	public CombinedStackDockComponent<?, ?, ?> getParentPane(){
		return parent;
	}
	
	@Override
	public void updateUI(){
		super.updateUI();
		if( parent != null ){
			parent.discardComponentsAndRebuild();
		}
	}
	
    @Override
    public void doLayout() {
    	super.doLayout();
        parent.doLayout();
    }
    
    @Override
    public Dimension getPreferredSize(){
    	return parent.getPreferredSize();
    }
    
    @Override
    public Dimension getMinimumSize() {
    	return parent.getMinimumSize();
    }
}

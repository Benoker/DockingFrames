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

/**
 * This panel paints the contents of a {@link CombinedStackDockComponent}.
 * This panel is also a {@link #setFocusTraversalPolicyProvider(boolean) focus traversal policy provider}.
 * @author Benjamin Sigg
 */
public class CombinedStackDockContentPane extends JPanel{
	private CombinedStackDockComponent<?, ?, ?> parent;
	
	/**
	 * Creates a new content pane
	 * @param parent the owner of this pane, not <code>null</code>
	 */
	public CombinedStackDockContentPane( CombinedStackDockComponent<?, ?, ?> parent ){
		super( null );
		if( parent == null )
			throw new IllegalArgumentException( "parent must not be null" );
		this.parent = parent;
		setOpaque( false );
		setFocusTraversalPolicyProvider( true );
		setFocusTraversalPolicy( new DockFocusTraversalPolicy( new CombinedStackDockFocusTraversalPolicy( this ), true ) );
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

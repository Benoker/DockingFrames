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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.LayoutFocusTraversalPolicy;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPainter;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * A {@link DockableDisplayer} which is not able to show the {@link DockTitle} of
 * its {@link Dockable}. This displayer exchanges automatically its border
 * using the global {@link TabPainter} delivered through the {@link DockProperties}
 * and the key {@link EclipseTheme#TAB_PAINTER}.
 * @author Janni Kovacs
 */
public class NoTitleDisplayer extends JPanel implements DockableDisplayer {
	private Dockable dockable;
	private DockController controller;
	private DockStation station;
	private DockTitle title;
	private Location location;
	
	private PropertyValue<TabPainter> painter;
	
	private boolean defaultBorderHint;
	private Boolean borderHint;
	private DockableDisplayerHints hints;
	
	public NoTitleDisplayer( DockStation station, Dockable dockable, boolean bordered, boolean respectHints ){
		setLayout( new GridLayout( 1, 1, 0, 0 ) );
		setOpaque( false );
		
		if( respectHints ){
		    hints = new DockableDisplayerHints(){
		        public void setShowBorderHint( Boolean border ) {
		            borderHint = border;
		            updateFullBorder();
		        }
		    };
		}
		
		setStation( station );
        setDockable( dockable );
        setBorder( null );
        
        defaultBorderHint = bordered;
        
		if( bordered || respectHints ){
    		painter = new PropertyValue<TabPainter>( EclipseTheme.TAB_PAINTER ){
    	        @Override
    	        protected void valueChanged( TabPainter oldValue, TabPainter newValue ) {
    	            updateFullBorder();
    	        }
    	    };
    	    updateFullBorder();
		}
		
		setFocusCycleRoot( true );
		setFocusTraversalPolicy( new LayoutFocusTraversalPolicy() );
	}
	
	/**
	 * Gets the current hint whether a border should be shown or not.
	 * @return the current hint
	 */
	protected boolean getBorderHint(){
	    if( borderHint != null )
	        return borderHint.booleanValue();
	    
	    return defaultBorderHint;
	}
	
	/**
	 * Exchanges the border of this component, using the current
	 * {@link EclipseTheme#TAB_PAINTER} to determine the new border.
	 */
	protected void updateFullBorder(){
	    if( painter != null ){
    	    TabPainter painter = this.painter.getValue();
    	    
            if( controller == null || painter == null || dockable == null ){
                setBorder( null );
            }
            else{
                if( hints == null || getBorderHint() ){
                    setBorder( painter.getFullBorder( controller, dockable ) );
                }
                else{
                    setBorder( null );
                }
            }
	    }
	}

	public Insets getDockableInsets() {
	    Insets insets = getInsets();
	    if( insets == null )
	        return new Insets( 0,0,0,0 );
	    return insets;
	}
	
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintBorder(g);
    }
	
	public Component getComponent(){
		return this;
	}

	public DockController getController(){
		return controller;
	}

	public Dockable getDockable(){
		return dockable;
	}

	public DockStation getStation(){
		return station;
	}

	public DockTitle getTitle(){
		return title;
	}

	public Location getTitleLocation(){
		return location;
	}

	public void setController( DockController controller ){
		this.controller = controller;
		if( painter != null )
		    painter.setProperties( controller == null ? null : controller.getProperties() );
		
		updateFullBorder();
	}

	public void setDockable( Dockable dockable ){
	    if( this.dockable != null )
	        this.dockable.configureDisplayerHints( null );
	    
		this.dockable = dockable;
		
		removeAll();
		if( dockable != null ){
			add( dockable.getComponent() );
			dockable.configureDisplayerHints( hints );
		}
		updateFullBorder();
	}

	public void setStation( DockStation station ){
		this.station = station;
	}

	public void setTitle( DockTitle title ){
		this.title = title;
	}

	public void setTitleLocation( Location location ){
		this.location = location;
	}

	public boolean titleContains( int x, int y ){
		return false;
	}
}

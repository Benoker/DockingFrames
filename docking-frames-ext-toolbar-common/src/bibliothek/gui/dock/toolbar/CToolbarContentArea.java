/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;

/**
 * Adds four {@link CToolbarArea}s around the existing {@link CStation}s of this
 * {@link CContentArea}.
 * @author Benjamin Sigg
 */
public class CToolbarContentArea extends CContentArea{
	private CToolbarArea eastToolbar;
	private CToolbarArea westToolbar;
	private CToolbarArea southToolbar;
	private CToolbarArea northToolbar;
	
	/**
	 * Creates a new content area
	 * @param control the control for which this area will be used
	 * @param uniqueId a unique identifier for this area
	 */
	public CToolbarContentArea( CControl control, String uniqueId ){
		super( control, uniqueId );
		
		removeAll();
		
		JPanel center = new JPanel( new BorderLayout() );
		center.add( getCenter(), BorderLayout.CENTER );
		center.add( getEastArea(), BorderLayout.EAST );
		center.add( getWestArea(), BorderLayout.WEST );
		center.add( getNorthArea(), BorderLayout.NORTH );
		center.add( getSouthArea(), BorderLayout.SOUTH );
		add( center, BorderLayout.CENTER );
		
		eastToolbar = new CToolbarArea( getEastToolbarIdentifier(), Orientation.VERTICAL );
		westToolbar = new CToolbarArea( getWestToolbarIdentifier(), Orientation.VERTICAL );
		southToolbar = new CToolbarArea( getSouthToolbarIdentifier(), Orientation.HORIZONTAL );
		northToolbar = new CToolbarArea( getNorthToolbarIdentifier(), Orientation.HORIZONTAL );
		
		add( eastToolbar.getStation().getComponent(), BorderLayout.EAST );
		add( westToolbar.getStation().getComponent(), BorderLayout.WEST );
		add( southToolbar.getStation().getComponent(), BorderLayout.SOUTH );
		add( northToolbar.getStation().getComponent(), BorderLayout.NORTH );
		
		addStations( eastToolbar, westToolbar, southToolbar, northToolbar );
	}
	
	/**
	 * Gets the unique identifier that is used for the {@link CToolbarArea} at the east side.
	 * @return the unique identifier
	 */
	public String getEastToolbarIdentifier(){
		return getEastToolbarIdentifier( getUniqueId() );
	}

	/**
	 * Gets the unique identifier that is used for the {@link CToolbarArea} at the east side.
	 * @param uniqueId the unique identifier of the {@link CToolbarContentArea}
	 * @return the unique identifier
	 */
	public static String getEastToolbarIdentifier( String uniqueId ){
		return uniqueId + " toolbar east";
	}
	
	/**
	 * Gets the toolbar which is shown at the east side.
	 * @return the toolbar, not <code>null</code>
	 */
	public CToolbarArea getEastToolbar(){
		return eastToolbar;
	}

	/**
	 * Gets the unique identifier that is used for the {@link CToolbarArea} at the west side.
	 * @return the unique identifier
	 */
	public String getWestToolbarIdentifier(){
		return getWestToolbarIdentifier( getUniqueId() );
	}
	
	/**
	 * Gets the unique identifier that is used for the {@link CToolbarArea} at the west side.
	 * @param uniqueId the unique identifier of the {@link CToolbarContentArea}
	 * @return the unique identifier
	 */
	public static String getWestToolbarIdentifier( String uniqueId ){
		return uniqueId + " toolbar west";
	}

	/**
	 * Gets the toolbar which is shown at the west side.
	 * @return the toolbar, not <code>null</code>
	 */
	public CToolbarArea getWestToolbar(){
		return westToolbar;
	}
	
	/**
	 * Gets the unique identifier that is used for the {@link CToolbarArea} at the south side.
	 * @return the unique identifier
	 */
	public String getSouthToolbarIdentifier(){
		return getSouthToolbarIdentifier( getUniqueId() );
	}
	
	/**
	 * Gets the unique identifier that is used for the {@link CToolbarArea} at the south side.
	 * @param uniqueId the unique identifier of the {@link CToolbarContentArea}
	 * @return the unique identifier
	 */
	public static String getSouthToolbarIdentifier( String uniqueId ){
		return uniqueId + " toolbar south";
	}
	
	/**
	 * Gets the toolbar which is shown at the south side.
	 * @return the toolbar, not <code>null</code>
	 */
	public CToolbarArea getSouthToolbar(){
		return southToolbar;
	}
	
	/**
	 * Gets the unique identifier that is used for the {@link CToolbarArea} at the north side.
	 * @return the unique identifier
	 */
	public String getNorthToolbarIdentifier(){
		return getNorthToolbarIdentifier( getUniqueId() );
	}
	
	/**
	 * Gets the unique identifier that is used for the {@link CToolbarArea} at the north side.
	 * @param uniqueId the unique identifier of the {@link CToolbarContentArea}
	 * @return the unique identifier
	 */
	public static String getNorthToolbarIdentifier( String uniqueId ){
		return uniqueId + " toolbar north";
	}
	
	/**
	 * Gets the toolbar which is shown at the north side.
	 * @return the toolbar, not <code>null</code>
	 */
	public CToolbarArea getNorthToolbar(){
		return northToolbar;
	}
}

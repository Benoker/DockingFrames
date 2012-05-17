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

package bibliothek.gui.dock.wizard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockStationFactory;
import bibliothek.gui.dock.station.split.SplitDockStationLayout;
import bibliothek.gui.dock.station.split.SplitDockStationLayout.Entry;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.wizard.WizardSplitDockStationLayout.Column;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * This factory is intended to read and write the layout of a {@link WizardSplitDockStation}.
 * @author Benjamin Sigg
 */
public class WizardSplitDockStationFactory extends SplitDockStationFactory{
	public static final String ID = "WizardSplitDockStationFactory";
	
	@Override
	protected SplitDockStationLayout createLayout( Entry root, int fullscreen, boolean hasFullscreenAction ){
		return new WizardSplitDockStationLayout( root, fullscreen, hasFullscreenAction );
	}
	
	@Override
	public String getID(){
		return ID;
	}
	
	public void setLayout( SplitDockStation station, SplitDockStationLayout splitLayout, Map<Integer,Dockable> children, PlaceholderStrategy placeholders ){
		super.setLayout( station, splitLayout, children, placeholders );
		
		WizardSplitDockStation wizard = (WizardSplitDockStation)station;
		WizardSplitDockStationLayout layout = (WizardSplitDockStationLayout)splitLayout;
		
		Column[] columns = layout.getColumns();
		Dockable[][] columnsAndCells = new Dockable[ columns.length ][];
		int[][] cellSizes = new int[ columnsAndCells.length ][];
		int[] columnSizes = new int[ columnsAndCells.length ];
		for( int i = 0; i < columns.length; i++ ){
			columnsAndCells[i] = new Dockable[ columns[i].getCellKeys().length ];
			cellSizes[i] = new int[ columnsAndCells[i].length ];
			columnSizes[i] = columns[i].getSize();
			for( int j = 0; j < cellSizes[i].length; j++ ){
				columnsAndCells[i][j] = children.get( columns[i].getCellKeys()[j] );
				cellSizes[i][j] = columns[i].getCellSizes()[j];
			}
		}
		
		wizard.setPersistentColumns( columnsAndCells, cellSizes, columnSizes );
	}
	
	@Override
	public SplitDockStationLayout getLayout( SplitDockStation station, Map<Dockable, Integer> children ){
		WizardSplitDockStation wizard = (WizardSplitDockStation)station;
		WizardSplitDockStationLayout layout = (WizardSplitDockStationLayout)super.getLayout( station, children );
		
		PersistentColumn[] persistentColumns = wizard.getPersistentColumns();
		Column[] columns = new Column[ persistentColumns.length ];
		for( int i = 0; i < persistentColumns.length; i++ ){
			int size = persistentColumns[i].getSize();
			Map<Dockable, PersistentCell> cells = persistentColumns[i].getCells();
			int[] keys = new int[ cells.size() ];
			int[] sizes = new int[ cells.size() ];
			int index = 0;
			for( Map.Entry<Dockable, PersistentCell> entry : cells.entrySet() ){
				keys[ index ] = children.get( entry.getKey() );
				sizes[ index ] = entry.getValue().getSize();
				index++;
			}
			columns[i] = new Column( size, keys, sizes );
		}
		layout.setColumns( columns );
		return layout;
	}
	
	@Override
	public void write( SplitDockStationLayout layout, DataOutputStream out ) throws IOException{
		super.write( layout, out );
		Version.write( out, Version.VERSION_1_1_1 );
		Column[] columns = ((WizardSplitDockStationLayout)layout).getColumns();
		out.writeInt( columns.length );
		for( Column column : columns ){
			out.writeInt( column.getSize() );
			int[] keys = column.getCellKeys();
			int[] sizes = column.getCellSizes();
			out.writeInt( keys.length );
			for( int i = 0; i < keys.length; i++ ){
				out.writeInt( keys[i] );
				out.writeInt( sizes[i] );
			}
		}
	}
	
	@Override
	public void write( SplitDockStationLayout layout, XElement element ){
		super.write( layout, element.addElement( "split" ) );
		element = element.addElement( "wizard" );
		
		Column[] columns = ((WizardSplitDockStationLayout)layout).getColumns();
		for( Column column : columns ){
			XElement xcolumn = element.addElement( "column" );
			xcolumn.addInt( "size", column.getSize() );
			int[] keys = column.getCellKeys();
			int[] sizes = column.getCellSizes();
			for( int i = 0; i < keys.length; i++ ){
				XElement xcell = xcolumn.addElement( "cell" );
				xcell.addInt( "key", keys[i] );
				xcell.addInt( "size", sizes[i] );
			}
		}
	}
	
	@Override
	public SplitDockStationLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
		SplitDockStationLayout layout = super.read( in, placeholders );
		Version version = Version.read( in );
		if( !version.equals( Version.VERSION_1_1_1 )){
			throw new IOException( "trying to read a format from the future: " + version );
		}
		int count = in.readInt();
		Column[] columns = new WizardSplitDockStationLayout.Column[ count ];
		for( int i = 0; i < count; i++ ){
			int size = in.readInt();
			int length = in.readInt();
			int[] keys = new int[ length ];
			int[] sizes = new int[ length ];
			for( int j = 0; j < length; j++ ){
				keys[j] = in.readInt();
				sizes[j] = in.readInt();
			}
			columns[i] = new Column( size, keys, sizes );
		}
		((WizardSplitDockStationLayout)layout).setColumns( columns );
		return layout;
	}
	
	public SplitDockStationLayout read( XElement element, PlaceholderStrategy placeholders ){
		SplitDockStationLayout layout = super.read( element.getElement( "split" ), placeholders );
		element = element.getElement( "wizard" );
		
		XElement[] xcolumns = element.getElements( "column" );
		Column[] columns = new Column[ xcolumns.length ];
		for( int i = 0; i < columns.length; i++ ){
			XElement xcolumn = xcolumns[i];
			int size = xcolumn.getInt("size");
			XElement[] xcells = xcolumn.getElements( "cell" );
			int[] keys = new int[ xcells.length ];
			int[] sizes = new int[ xcells.length ];
			
			for( int j = 0; j < xcells.length; j++ ){
				keys[j] = xcells[j].getInt( "key" );
				sizes[j] = xcells[j].getInt( "size" );
			}
			
			columns[i] = new Column( size, keys, sizes );
		}
		((WizardSplitDockStationLayout)layout).setColumns( columns );
		return layout;
	}
}

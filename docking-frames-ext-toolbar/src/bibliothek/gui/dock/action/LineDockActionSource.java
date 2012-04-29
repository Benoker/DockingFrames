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

package bibliothek.gui.dock.action;

import java.util.Iterator;

import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.themes.basic.action.DockActionImportance;

/**
 * This {@link DockActionSource} adds a {@link SeparatorAction} at the beginning if its delegate
 * {@link DockActionSource} has any content.
 * @author Benjamin Sigg
 */
public class LineDockActionSource extends AbstractDockActionSource{
	private DockActionSource source;
	
	private SeparatorAction separator = new Separator();
	
	@DockActionImportance( value=0.0 )
	private class Separator extends SeparatorAction{
		public Separator(){
			super( ViewTarget.TITLE );
		}
	}
	
	private DockActionSourceListener listener = new DockActionSourceListener(){
		@Override
		public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ){
			if( source.getDockActionCount() == 0 ){
				fireRemoved( 0, lastIndex+1 );
			}
			else{
				fireRemoved( firstIndex+1, lastIndex+1 );
			}
		}
		
		@Override
		public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ){
			int count = lastIndex - firstIndex + 1;
			if( count == source.getDockActionCount() ){
				fireAdded( 0, lastIndex+1 );
			}
			else{
				fireAdded( firstIndex+1, lastIndex+1 );
			}
		}
	};
	
	public LineDockActionSource( DockActionSource source ){
		this.source = source;
	}
	
	@Override
	public LocationHint getLocationHint(){
		return source.getLocationHint();
	}

	@Override
	public int getDockActionCount(){
		int count = source.getDockActionCount();
		if( count > 0 ){
			count++;
		}
		return count;
	}

	@Override
	public DockAction getDockAction( int index ){
		if( index == 0 ){
			return separator;
		}
		else{
			return source.getDockAction( index-1 );
		}
	}

	@Override
	public void addDockActionSourceListener( DockActionSourceListener listener ){
		if( !hasListeners() ){
			source.addDockActionSourceListener( this.listener );
		}
		super.addDockActionSourceListener( listener );
	}
	
	@Override
	public void removeDockActionSourceListener( DockActionSourceListener listener ){
		super.removeDockActionSourceListener( listener );
		if( !hasListeners() ){
			source.removeDockActionSourceListener( this.listener );
		}
	}
	
	@Override
	public Iterator<DockAction> iterator(){
		return new Iterator<DockAction>(){
			private boolean separatorSent = false;
			private Iterator<DockAction> inner = source.iterator();
			
			@Override
			public DockAction next(){
				if( separatorSent ){
					return inner.next();
				}
				else{
					separatorSent = true;
					return separator;
				}
			}
			
			@Override
			public boolean hasNext(){
				if( separatorSent ){
					return inner.hasNext();
				}
				else{
					return getDockActionCount() > 0;
				}
			}
			
			@Override
			public void remove(){
				if( separatorSent ){
					inner.remove();
				}
				else{
					throw new IllegalStateException( "not supported for the first element" );
				}
			}
		};
	}
	
}

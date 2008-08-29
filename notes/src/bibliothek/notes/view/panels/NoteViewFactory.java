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
package bibliothek.notes.view.panels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.view.NoteViewManager;
import bibliothek.util.xml.XElement;

/**
 * A factory creating new {@link NoteView}s.
 * @author Benjamin Sigg
 */
public class NoteViewFactory implements DockFactory<NoteView, String> {
    /** the unique id for this factory */
	public static final String FACTORY_ID = "note";
	
	/** a manager of all {@link NoteView}s */
	private NoteViewManager manager;
	/** the set of known {@link Note}s */
	private NoteModel model;
	
	/**
	 * Creates a new factory
	 * @param manager used to store newly loaded {@link NoteView}s
	 * @param model used to create new {@link NoteView}s
	 */
	public NoteViewFactory( NoteViewManager manager, NoteModel model ){
		this.manager = manager;
		this.model = model;
	}
	
	public String getID(){
		return "note";
	}
	
	public void estimateLocations( String layout, DockableProperty location,
			Map<Integer, DockLayoutInfo> children ){
		// nothing to do
	}
	
	public String getLayout( NoteView element, Map<Dockable, Integer> children ) {
	    return element.getNote().getId();
	}
	
	public NoteView layout( String layout ) {
	    NoteView view = new NoteView( manager, model );
	    view.setNote( model.getNote( layout ) );
	    manager.putExternal( view );
	    return view;
	}
	
	public NoteView layout( String layout, Map<Integer, Dockable> children ) {
	    NoteView view = new NoteView( manager, model );
        view.setNote( model.getNote( layout ) );
        manager.putExternal( view );
        return view;
	}
	
	public void setLayout( NoteView element, String layout ) {
	    element.setNote( model.getNote( layout ) );
	}
	
	public void setLayout( NoteView element, String layout, Map<Integer, Dockable> children ) {
	    element.setNote( model.getNote( layout ) );
	}
	
	public void write( String layout, DataOutputStream out ) throws IOException {
	    out.writeUTF( layout );
	}
	
	public void write( String layout, XElement element ) {
	    element.addElement( "note" ).setString( layout );
	}
	
	public String read( DataInputStream in ) throws IOException {
	    return in.readUTF();
	}
	
	public String read( XElement element ) {
	    return element.getElement( "note" ).getString();
	}
}

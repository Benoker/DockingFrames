package bibliothek.gui.dock.station.toolbar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * Describes the location of a child of a {@link ToolbarContainerDockStation}.
 * 
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public class ToolbarContainerProperty extends AbstractDockableProperty{
	/** the index of the child */
	private int index;

	/** the placeholder for the child, can be <code>null</code> */
	private Path placeholder;

	/**
	 * Creates a new, empty {@link ToolbarProperty}.
	 */
	public ToolbarContainerProperty(){
		// nothing
	}

	/**
	 * Creates a new property.
	 * 
	 * @param index
	 *            the index of a child of a {@link DockStation}
	 * @param placeholder
	 *            the name of the child, can be <code>null</code>
	 */
	public ToolbarContainerProperty( int index, Path placeholder ){
		this.index = index;
		this.placeholder = placeholder;
	}

	@Override
	public DockableProperty copy(){
		final ToolbarContainerProperty copy = new ToolbarContainerProperty(
				index, placeholder);
		final DockableProperty successor = getSuccessor();
		if (successor != null){
			copy.setSuccessor(successor.copy());
		}
		return copy;
	}

	@Override
	public String getFactoryID(){
		return ToolbarPropertyFactory.ID;
	}

	/**
	 * Gets the location of the {@link Dockable} in its list.
	 * 
	 * @return the index
	 */
	public int getIndex(){
		return index;
	}

	/**
	 * Gets the name of the {@link Dockable}.
	 * 
	 * @return the name, can be <code>null</code>
	 */
	public Path getPlaceholder(){
		return placeholder;
	}

	@Override
	public void store( DataOutputStream out ) throws IOException{
		Version.write(out, Version.VERSION_1_1_1);
		out.writeInt(index);
		if (placeholder == null){
			out.writeBoolean(false);
		} else{
			out.writeBoolean(true);
			out.writeUTF(placeholder.toString());
		}
	}

	@Override
	public void load( DataInputStream in ) throws IOException{
		final Version version = Version.read(in);
		if (!version.equals(Version.VERSION_1_1_1)){
			throw new IOException("data from an unknown version: " + version);
		}
		index = in.readInt();
		if (in.readBoolean()){
			placeholder = new Path(in.readUTF());
		} else{
			placeholder = null;
		}
	}

	@Override
	public void store( XElement element ){
		element.addElement("index").setInt(index);
		if (placeholder != null){
			element.addElement("placeholder").setString(placeholder.toString());
		}
	}

	@Override
	public void load( XElement element ){
		final XElement xindex = element.getElement("index");
		final XElement xplaceholder = element.getElement("placeholder");

		if (xindex != null){
			index = xindex.getInt();
		}
		if (xplaceholder != null){
			placeholder = new Path(xplaceholder.getString());
		} else{
			placeholder = null;
		}
	}
}

package bibliothek.gui.dock.perspective;


public interface PerspectiveStation<L> extends PerspectiveElement<L>{
	public int getDockableCount();
	
	public PerspectiveDockable<?> getDockable( int index );
}

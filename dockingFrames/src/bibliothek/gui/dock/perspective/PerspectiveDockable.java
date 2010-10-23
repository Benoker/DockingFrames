package bibliothek.gui.dock.perspective;

import bibliothek.util.Path;

public interface PerspectiveDockable<L> extends PerspectiveElement<L>{
	public Path getPlaceholder();

	public PerspectiveStation<?> getParent();
}

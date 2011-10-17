package bibliothek.gui.dock;


import java.awt.Dimension;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A {@link Dockable} and a {@link Dockstation} which stands for a group of
 * {@link ComponentDockable}. As dockable it can be put in {@link DockStation}
 * which implements marker interface {@link ToolbarInterface}. As DockStation it
 * accept a {@link ComponentDockable} or a {@link ToolbarGroupDockStation}
 * 
 * @author Herve Guillaume
 */
public class ToolbarGroupDockStation extends ToolbarDockStation {

	/**
	 * Dropps <code>dockable</code> at location <code>index</code>.
	 * 
	 * @param dockable
	 *            the element to add
	 * @param index
	 *            the location of <code>dockable</code>
	 * @return whether the operation was succesfull or not
	 */
	@Override
	public boolean drop( Dockable dockable, int index ){
		// note: merging of two ToolbarGroupDockStations is done by the
		// ToolbarGroupDockStationMerger
		System.out.println(this.toString()
				+ "## drop(Dockable dockable, int index)##");
		if (this.accept(dockable)){
			this.add(dockable, index);
			return true;
		}
		return false;
	}

	@Override
	public boolean accept( Dockable child ){
		System.out.println(this.toString() + "## accept(Dockable child) ##");
		return getToolbarStrategy().isToolbarGroupPart(child);
	}

	/**
	 * Insert one dockable at the index
	 * 
	 * @param dockable
	 *            Dockable to add
	 * @param index
	 *            Index where add dockable
	 */
	private void add( Dockable dockable, int index ){
		DockUtilities.ensureTreeValidity(this, dockable);
		DockUtilities.checkLayoutLocked();
		DockHierarchyLock.Token token = DockHierarchyLock.acquireLinking(this,
				dockable);
		try{
			listeners.fireDockableAdding(dockable);
			dockable.setDockParent(this);
			getDockables().add(index, dockable);
			mainPanel.getContentPane().add(dockable.getComponent(), index);
//			mainPanel.getContentPane().setBounds(0, 0,
//					mainPanel.getContentPane().getPreferredSize().width,
//					mainPanel.getContentPane().getPreferredSize().height);
//			mainPanel.setPreferredSize(new Dimension(mainPanel.getContentPane()
//					.getPreferredSize().width, mainPanel.getContentPane()
//					.getPreferredSize().height));
//			mainPanel.revalidate();
//			mainPanel.repaint();
			listeners.fireDockableAdded(dockable);
			fireDockablesRepositioned(index + 1);
		} finally{
			token.release();
		}
		this.mainPanel.updateSize();
		mainPanel.getContentPane().revalidate();
		mainPanel.getContentPane().repaint();
	}

}

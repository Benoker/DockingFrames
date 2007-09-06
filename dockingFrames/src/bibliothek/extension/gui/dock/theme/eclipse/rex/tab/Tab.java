package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Component;
import javax.swing.Icon;

/**
 * @author Janni Kovacs
 */
public class Tab {

	private String title, tooltip;
	private Icon icon;
	private boolean closable;
	private Component comp;

	public Tab(String title, Component comp) {
		this(title, null, null, comp, false);
	}

	public Tab(String title, Icon icon, Component comp) {
		this(title, icon, null, comp, false);
	}

	public Tab(String title, Icon icon, Component comp, boolean closable) {
		this(title, icon, null, comp, closable);
	}

	public Tab(String title, Icon icon, String tooltip, Component comp, boolean closable) {
		this.title = title;
		this.comp = comp;
		this.tooltip = tooltip;
		this.icon = icon;
		this.closable = closable;
	}

	public String getTitle() {
		return title;
	}

	public Icon getIcon() {
		return icon;
	}

	public boolean isClosable() {
		return closable;
	}

	public Component getComponent() {
		return comp;
	}

	public void setTitle(String newTitle) {
		this.title = newTitle;
	}

	public void setIcon(Icon newIcon) {
		this.icon = newIcon;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
}

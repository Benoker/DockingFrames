package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Component;
import javax.swing.Icon;

/**
 * @author Janni Kovacs
 */
public class Tab {
	private String title;
	private Icon icon;
	private Component comp;
	private Component tabComponent;
	private TabComponent painter;
	
	public Tab(String title, Component comp) {
		this(title, null, comp, null );
	}

	public Tab(String title, Icon icon, Component comp) {
		this(title, icon, comp, null);
	}

	public Tab(String title, Icon icon, Component comp, Component tabComponent ) {
		this.title = title;
		this.comp = comp;
		this.tabComponent = tabComponent;
		this.icon = icon;
	}
	
	public void setPainter( TabComponent painter ){
		this.painter = painter;
	}
	
	public String getTitle() {
		return title;
	}

	public Icon getIcon() {
		return icon;
	}

	public Component getTabComponent(){
		return tabComponent;
	}
	
	public Component getComponent() {
		return comp;
	}

	public void setTitle(String newTitle) {
		this.title = newTitle;
		if( painter != null )
			painter.update();
	}

	public void setIcon(Icon newIcon) {
		this.icon = newIcon;
		if( painter != null )
			painter.update();
	}
}

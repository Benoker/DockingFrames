package tutorial.support;

import javax.swing.ImageIcon;

import bibliothek.gui.dock.common.action.CButton;

public class CopyCodeAction extends CButton{
	private CodePanel panel;
	
	public CopyCodeAction( CodePanel panel ){
		this.panel = panel;
		setText("Copy all");
		setTooltip("Copy the entire source code");
		
		ImageIcon icon = new ImageIcon( getClass().getResource("/data/tutorial/icons/copy.png") );
		setIcon( icon );
	}
	
	@Override
	protected void action(){
		panel.copy();
	}
}

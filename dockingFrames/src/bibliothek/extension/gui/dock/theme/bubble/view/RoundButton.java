package bibliothek.extension.gui.dock.theme.bubble.view;


import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleColorAnimation;


public class RoundButton extends JComponent{

BubbleColorAnimation animation;
Icon icon;
private List<ActionListener> actions = new ArrayList<ActionListener>();

	public RoundButton(BubbleTheme theme)
	{
		animation=new BubbleColorAnimation(theme);
		animation.putColor("button", "button");
				
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e) {
				animation.putColor("button", "button.mouse");
			}
			@Override
			public void mouseExited(MouseEvent e) {
				animation.putColor("button", "button");
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				
				ActionEvent event=new ActionEvent(RoundButton.this,Event.ACTION_EVENT,null);
				
				for(ActionListener action : actions)
				{
					action.actionPerformed(event);
				}
			}
		});
		
		animation.addTask(new Runnable() {
		
			public void run()
			{
				repaint();	
			}
	
		});
	}

    public void addActionListener(ActionListener action)
    {
    	actions.add(action);
    }
	
	public void setIcon(Icon icon)
	{
		this.icon=icon;
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
	
		if (icon==null){
			return new Dimension(10,10);
		}
		else
		{
			return new Dimension((int)(icon.getIconWidth()*1.5),(int)(icon.getIconHeight()*1.5));
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	   
		Graphics2D g2 = (Graphics2D)g.create();
	    g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
	    g2.setColor(animation.getColor("button"));
		g2.fillOval( 0, 0, getWidth(), getHeight() );
		g2.dispose();
		
		/*
		 * Icon der DockAction (sofern es ein Icon gibt).
		 */
	//	Icon icon = action.getIcon( dockable );
		if( icon != null ){
			icon.paintIcon( this, g, 
					(getWidth() - icon.getIconWidth()) / 2, 
					(getHeight() - icon.getIconHeight()) / 2 );
		}
	
	}
	
	
}

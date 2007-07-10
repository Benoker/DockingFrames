package bibliothek.chess.model;

import javax.swing.Icon;

public abstract class Figure {
	private Icon smallIcon;
	private Icon bigIcon;
	private Player player;
	private String name;
	
	public Figure( Player player, String name, Icon smallIcon, Icon bigIcon ){
		super();
		this.player = player;
		this.name = name;
		this.smallIcon = smallIcon;
		this.bigIcon = bigIcon;
	}

	public String getName(){
		return name;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Icon getSmallIcon(){
		return smallIcon;
	}
	
	public Icon getBigIcon(){
		return bigIcon;
	}
}

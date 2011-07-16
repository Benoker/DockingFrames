package tutorial.support.sets;


public class TutorialSet {
	private Class<?>[] children;
	
	public TutorialSet( Class<?>... children ){
		this.children = children;
	}
	
	public Class<?>[] getChildren(){
		return children;
	}
}

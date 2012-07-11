package tutorial.support.sets;

import tutorial.TutorialExtension;


public class TutorialSet {
	private Class<?>[] children;
	
	public TutorialSet( Class<?>... children ){
		this.children = children;
	}
	
	public void append( TutorialExtension extension ){
		Class<?>[] tutorials = extension.getTutorials( getClass() );
		if( tutorials != null ){
			Class<?>[] temp = new Class[children.length + tutorials.length];
			System.arraycopy( children, 0, temp, 0, children.length );
			System.arraycopy( tutorials, 0, temp, children.length, tutorials.length );
			children = temp;
		}
	}
	
	public Class<?>[] getChildren(){
		return children;
	}
}

package tutorial.support.sets;

import tutorial.support.Tutorial;

@Tutorial( title="Basics", id="Basics")
public class BasicsSet extends TutorialSet{
	public BasicsSet(){
		super( BasicCoreSet.class,
		       BasicDockFrontendSet.class );
	}
}

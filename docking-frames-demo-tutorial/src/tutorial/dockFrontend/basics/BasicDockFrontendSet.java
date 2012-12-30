package tutorial.dockFrontend.basics;

import tutorial.support.Tutorial;
import tutorial.support.sets.TutorialSet;

@Tutorial(title="DockFrontend", id="BasicsDockFrontend")
public class BasicDockFrontendSet extends TutorialSet{
	public BasicDockFrontendSet(){
		super( DockFrontendExample.class,
			   PersistentLayoutExample.class,
			   AsideExample.class );
	}
}

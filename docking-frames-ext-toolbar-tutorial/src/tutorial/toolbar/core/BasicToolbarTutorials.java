package tutorial.toolbar.core;

import tutorial.support.Tutorial;
import tutorial.support.sets.TutorialSet;

@Tutorial( title="Toolbar", id="Toolbar" )
public class BasicToolbarTutorials extends TutorialSet{
	public BasicToolbarTutorials(){
		super( ToolbarHelloWorld.class,
				ToolbarCustomization.class,
				ToolbarExpanding.class );
	}
}

package tutorial.toolbar.core;

import tutorial.support.Tutorial;
import tutorial.support.sets.TutorialSet;

@Tutorial( title="Toolbar", id="CoreToolbar" )
public class CoreToolbarTutorials extends TutorialSet{
	public CoreToolbarTutorials(){
		super( ToolbarHelloWorld.class,
				ToolbarCustomization.class,
				ToolbarExpanding.class,
				WizardSplitDockStationTutorial.class );
	}
}

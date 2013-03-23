package tutorial.support.sets;

import tutorial.core.basics.BasicCoreSet;
import tutorial.core.guide.GuideCoreSet;
import tutorial.dockFrontend.basics.BasicDockFrontendSet;
import tutorial.support.Tutorial;

@Tutorial(title="Core API", id="CoreAPI")
public class CoreSet extends TutorialSet{
	public CoreSet(){
		super(BasicCoreSet.class,
				BasicDockFrontendSet.class,
				GuideCoreSet.class);
	}
}

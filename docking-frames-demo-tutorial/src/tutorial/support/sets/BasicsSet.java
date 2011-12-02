package tutorial.support.sets;

import tutorial.common.basics.BasicCommonSet;
import tutorial.core.basics.BasicCoreSet;
import tutorial.dockFrontend.basics.BasicDockFrontendSet;
import tutorial.support.Tutorial;

@Tutorial( title="Basics", id="Basics")
public class BasicsSet extends TutorialSet{
	public BasicsSet(){
		super( BasicCoreSet.class,
		       BasicDockFrontendSet.class,
		       BasicCommonSet.class );
	}
}

package tutorial.common.guide;

import tutorial.support.Tutorial;
import tutorial.support.sets.TutorialSet;

@Tutorial(title="Examples from the guide", id="GuideCommon")
public class GuideCommonSet extends TutorialSet{
	public GuideCommonSet(){
		super( CommonHelloWorld.class,
				MultipleDockables.class,
				PerspectivesIntroduction.class,
				PerspectivesMulti.class,
				PerspectivesHistory.class );
	}
}

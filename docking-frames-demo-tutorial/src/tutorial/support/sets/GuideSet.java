package tutorial.support.sets;

import tutorial.common.guide.GuideCommonSet;
import tutorial.core.guide.GuideCoreSet;
import tutorial.support.Tutorial;

@Tutorial( title="Guide", id="Guide")
public class GuideSet extends TutorialSet{
	public GuideSet(){
		super( GuideCoreSet.class,
				GuideCommonSet.class );
	}
}

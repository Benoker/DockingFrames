package tutorial.support.sets;

import tutorial.common.guide.GuideCommonSet;

import tutorial.common.basics.BasicCommonSet;
import tutorial.support.Tutorial;

@Tutorial(title="Common API", id="CommonAPI")
public class CommonSet extends TutorialSet{
	public CommonSet(){
		super( BasicCommonSet.class,
				GuideCommonSet.class);
	}
}

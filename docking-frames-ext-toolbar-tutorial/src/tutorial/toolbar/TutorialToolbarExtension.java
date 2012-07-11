package tutorial.toolbar;

import tutorial.TutorialExtension;
import tutorial.common.basics.BasicCommonSet;
import tutorial.core.basics.BasicCoreSet;
import tutorial.support.sets.TutorialSet;
import tutorial.toolbar.common.CommonToolbarTutorials;
import tutorial.toolbar.core.CoreToolbarTutorials;

public class TutorialToolbarExtension implements TutorialExtension{

	@Override
	public Class<?>[] getTutorials( Class<? extends TutorialSet> set ){
		if( set.isAssignableFrom( BasicCoreSet.class )){
			return new Class[]{ CoreToolbarTutorials.class };
		}
		if( set.isAssignableFrom( BasicCommonSet.class )){
			return new Class[]{ CommonToolbarTutorials.class };
		}
		return null;
	}
	
}

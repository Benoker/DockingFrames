package tutorial.toolbar;

import tutorial.TutorialExtension;
import tutorial.core.basics.BasicCoreSet;
import tutorial.support.sets.TutorialSet;
import tutorial.toolbar.core.CoreToolbarTutorials;

public class TutorialToolbarExtension implements TutorialExtension{

	@Override
	public Class<?>[] getTutorials( Class<? extends TutorialSet> set ){
		if( set.isAssignableFrom( BasicCoreSet.class )){
			return new Class[]{ CoreToolbarTutorials.class };
		}
		return null;
	}
	
}

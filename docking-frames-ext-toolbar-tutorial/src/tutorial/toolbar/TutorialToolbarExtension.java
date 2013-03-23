package tutorial.toolbar;

import tutorial.TutorialExtension;
import tutorial.support.sets.CommonSet;
import tutorial.support.sets.CoreSet;
import tutorial.support.sets.TutorialSet;
import tutorial.toolbar.common.CommonToolbarTutorials;
import tutorial.toolbar.core.CoreToolbarTutorials;

public class TutorialToolbarExtension implements TutorialExtension{

	@Override
	public Class<?>[] getTutorials( Class<? extends TutorialSet> set ){
		if( set.isAssignableFrom( CoreSet.class )){
			return new Class[]{ CoreToolbarTutorials.class };
		}
		if( set.isAssignableFrom( CommonSet.class )){
			return new Class[]{ CommonToolbarTutorials.class };
		}
		return null;
	}
	
}

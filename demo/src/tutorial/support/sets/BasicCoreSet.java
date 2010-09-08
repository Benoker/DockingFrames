package tutorial.support.sets;

import tutorial.C1_CoreBasics_01_HelloWorld;
import tutorial.C1_CoreBasics_02_TheStations;
import tutorial.C1_CoreBasics_03_SplitDockStation;
import tutorial.C1_CoreBasics_04_Themes;
import tutorial.C1_CoreBasics_05_PersistentLayout;
import tutorial.support.Tutorial;

@Tutorial(title="Core", id="BasicsCore")
public class BasicCoreSet extends TutorialSet{
	public BasicCoreSet(){
		super(  C1_CoreBasics_01_HelloWorld.class,
                C1_CoreBasics_02_TheStations.class,
                C1_CoreBasics_03_SplitDockStation.class,
                C1_CoreBasics_04_Themes.class,
                C1_CoreBasics_05_PersistentLayout.class );
	}
}

package entityscala.lang.strategies;

import org.strategoxt.lang.JavaInteropRegisterer;
import org.strategoxt.lang.Strategy;

import entityscala.lang.strategies.java_strategy_0_0;

public class InteropRegisterer extends JavaInteropRegisterer {
	public InteropRegisterer() {
		super(new Strategy[] {
				editor_analyze_0_0.instance,
				java_strategy_0_0.instance, });
	}
}

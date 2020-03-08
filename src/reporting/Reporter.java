package reporting;

import soot.SootMethod;
import soot.Unit;

public class Reporter {

	public void report(SootMethod method, Unit source, Unit sink) {
		System.out.println("Found a flow in method " + method);
		System.out.println("\tSource: " + source);
		System.out.println("\tSink: " + sink);
	}
}

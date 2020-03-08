package analysis;

import java.util.Map;

import reporting.Reporter;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Main;
import soot.PackManager;
import soot.Transform;

/**
 * Runner class for starting the dataflow analysis
 * 
 */
public class MainClass {

	public static void main(String[] args) {
		runAnalysis(new Reporter(), 1);
	}

	public static void runAnalysis(final Reporter reporter, final int exercisenumber) {
		G.reset();

		// Register the transform
		Transform transform = new Transform("jtp.analysis", new BodyTransformer() {

			@Override
			protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
				// Create the analysis
				IntraproceduralAnalysis ipa = new IntraproceduralAnalysis(b, reporter, exercisenumber);
				ipa.doAnalyis();
			}

		});
		PackManager.v().getPack("jtp").add(transform);

		// Run Soot
		Main.main(
				new String[] { "-pp", "-process-dir", "./targetsBin", "-src-prec", "class", "-output-format", "none" });
	}

}

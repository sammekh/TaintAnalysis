package analysis;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reporting.Reporter;
import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * Class implementing dataflow analysis
 */
public class IntraproceduralAnalysis extends ForwardFlowAnalysis<Unit, Set<FlowAbstraction>> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public int flowThroughCount = 0;
	private final SootMethod method;
	private final Reporter reporter;

	public IntraproceduralAnalysis(Body b, Reporter reporter, int exercisenumber) {
		super(new ExceptionalUnitGraph(b));
		this.method = b.getMethod();
		this.reporter = reporter;
		logger.info("Analyzing method " + b.getMethod().getSignature() + "\n" + b);
	}

	@Override
	protected void flowThrough(Set<FlowAbstraction> taintsIn, Unit d, Set<FlowAbstraction> taintsOut) {
		Stmt s = (Stmt) d;
		logger.info("Unit " + d);

		if (s instanceof JAssignStmt) {
			JAssignStmt as = (JAssignStmt) s;

			Value rightOp = as.getRightOp();
			Value leftOp = as.getLeftOp();

			if (rightOp instanceof JSpecialInvokeExpr) {
				if(((JSpecialInvokeExpr) rightOp).getMethod().getName().toString().equals("getSecret"))
					taintsOut.add(new FlowAbstraction(d, (Local) leftOp));
			} else if (rightOp instanceof JimpleLocal) {
				for (FlowAbstraction abs : taintsIn) {
					if (rightOp == abs.getLocal()) {
						if (leftOp instanceof JInstanceFieldRef) {
							// Base Class as Tainted
							taintsOut.add(new FlowAbstraction(d, (Local) ((JInstanceFieldRef) leftOp).getBase()));
						} else
							taintsOut.add(new FlowAbstraction(d, (Local) leftOp));
					}
				}

			}
		} else if (s.containsInvokeExpr()) {

			InvokeExpr inv = s.getInvokeExpr();
			if (s instanceof InvokeStmt && inv.getMethod().getName().equals("leak")) {
				for (FlowAbstraction in : taintsIn) {
					if (inv.getArgs().contains(in.getLocal())) {
						reporter.report(method, in.getSource(), d);
					}
				}
			} else {
				for (FlowAbstraction in : taintsIn) {
					if (inv.getArgs().contains(in.getLocal())) {
						System.out.println("Tainted Value Passed As Arguument");
						reporter.report(method, in.getSource(), d);
					}
				}
			}
		} else if (s instanceof ReturnStmt) {
			for (FlowAbstraction in : taintsIn) {
				if (((ReturnStmt) s).getOp() == in.getLocal()) {
					System.out.println("Tainted Value Returned");
					reporter.report(method, in.getSource(), d);
				}
			}
		}

		taintsOut.addAll(taintsIn);

	}

	@Override
	protected Set<FlowAbstraction> newInitialFlow() {
		return new HashSet<FlowAbstraction>();
	}

	@Override
	protected Set<FlowAbstraction> entryInitialFlow() {
		return new HashSet<FlowAbstraction>();
	}

	@Override
	protected void merge(Set<FlowAbstraction> in1, Set<FlowAbstraction> in2, Set<FlowAbstraction> out) {
		out.addAll(in1);
		out.addAll(in2);
	}

	@Override
	protected void copy(Set<FlowAbstraction> source, Set<FlowAbstraction> dest) {
		dest.clear();
		dest.addAll(source);
	}

	public void doAnalyis() {
		super.doAnalysis();
	}

}

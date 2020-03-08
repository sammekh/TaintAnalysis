Taint Analysis Using Soot

Problem Statement:
Implemented the rules for the analysis in the class IntraproceduralAnalysis. 

A taint analysis tracks so-called tainted data from a given source (here, it is getSecret()) to see if it can reach a given sink (here leak()) through chains of assignments. We provide you a template and some test cases with which you can test your implementation.

Base your analysis on the following rules:

1. If the left side of an assignment is a local and the right side is a method call to method which signature contains the String getSecret, the left side is tainted.

2. If the right side of an assignment is a constant, and the left side is a local which was tainted, the taint can be removed.

3. If the right side of an assignment is a local and the left side is a local, the left side is tainted if and only if the right side was tainted before.

4. If the right side of an assignment is a local and the left side is a field, the base object of the field is tainted if the right side is tainted.

If a tainted value potentially leaves the scope of the method due to being passed as a parameter to another method call or is returned by the method, this is considered a leak and must be reported. Reports are done by calling Reporter.report(). The analysis should also report the source from which the respective data item was obtained, i.e., the appropriate call to getSecret().

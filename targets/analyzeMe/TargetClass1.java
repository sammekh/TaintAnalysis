package analyzeMe;


public class TargetClass1 {

	private final String whatsoever = "Test";
	
	public void test(String data, int x)
	{
		
	}

	private void leak(String data) {
		System.out.println("Leak: " + data);
	}
	private void leak(TargetClass2 tc2) {}

	// Test 1
	// No need

	// Test 2
	public void sourceToSink2() {
		String x = getSecret();
		// x = x + "";
		x = "test";
		leak(x);
	}

	// Test 3
	public void sourceToSink3() {
		String x = getSecret();
		String y = x;
		leak(y);
	}

	// Test 4
	public void sourceToSink4() {
		String x = getSecret();
	}
	
	// Test 5
	public void sourceToSink5() {
		String x = getSecret();
		String y = x;
		String z = y;
		z = "notTanted";
		leak(x);
		leak(y);
		leak(z);
	}
	
	// Test 6
	public void sourceToSink6() {
		String x = getSecret();
		String y = x;
		test(y,1);
	}
	
	// Test 7
	public String sourceToSink7() {
		String x = getSecret();
		return x;
	}
	
	// Test 8
	public void sourceToSink8() {
		String x = getSecret();
		TargetClass2 tc = new TargetClass2();
		tc.c = x;
		leak(tc);
	}

	private String getSecret() {
		return "top secret";
	}

}

package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

public class NextPrimeHammer implements Tool{

	@Override
	public String getType() {
		return "Next Prime Hammer";
	}

	@Override
	public long useOn(Product p) {
		long value=0;
		for(Product part : p.getParts()){
			value+=Math.abs(func(part.getFinalId()));

		}
		return value;
	}
	public long func(long id) {

		long v =id + 1;
		while (!isPrime(v)) {
			v++;
		}
		return v;
	}
	private boolean isPrime(long value) {
		if(value < 2) return false;
		if(value == 2) return true;
		long sq = (long) Math.sqrt(value);
		for (long i = 2; i <= sq; i++) {
			if (value % i == 0) {
				return false;
			}
		}

		return true;
	}
}
package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;
import java.util.*;

public class RandomSumPliers implements Tool {

	@Override
	public String getType() {
		return "RandomSumPliers";
	}

	@Override
	public long useOn(Product p){
		long value=0;
		for(Product part : p.getParts()){
			value+=Math.abs(func(part.getFinalId()));

		}
		return value;
	}

	public long func(long id){
		Random r = new Random(id);
		long  sum = 0;
		for (long i = 0; i < id % 10000; i++) {
			sum += r.nextInt();
		}

		return sum;
	}

}

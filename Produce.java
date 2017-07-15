package bgu.spl.a2.sim.tasks;

import java.util.*;


import bgu.spl.a2.*;
import bgu.spl.a2.sim.*;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;

public class Produce extends Task<Product> {
	private Product _createdProduct;


	public Produce(Product p){
		this._createdProduct=p;
	}

	@Override
	protected void start() {
		List<Produce> _tasks = new ArrayList<>();
		ManufactoringPlan _planForProd= Simulator.getWarehouse().getPlan(_createdProduct.getName());
		
		if (_planForProd.getParts().length==0){
			complete(_createdProduct);
		}
		
		else{
			for (String _tempPart:_planForProd.getParts()){
				Product _sunProduct = new Product(_createdProduct.getStartId()+1, _tempPart);
				_createdProduct.addPart(_sunProduct);
				Produce _produceSun = new Produce(_sunProduct);
				_tasks.add(_produceSun);
				spawn(_produceSun);
			}
		}
		whenResolved(_tasks, ()->{
				for (String _tool:_planForProd.getTools()){
					Deferred<Tool> _toolToUse=Simulator.getWarehouse().acquireTool(_tool);
					_toolToUse.whenResolved(()->{
						_createdProduct.sumItUp(_toolToUse.get().useOn(_createdProduct));
						Simulator.getWarehouse().releaseTool(_toolToUse.get());
					});
				}
			complete(_createdProduct);
		});


	}
}






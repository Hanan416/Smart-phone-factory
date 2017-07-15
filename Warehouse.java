package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.GCDScrewdriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.a2.Deferred;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {
	private GCDScrewdriver _screwdriver = new GCDScrewdriver();
	private RandomSumPliers _plaiers =new RandomSumPliers();
	private NextPrimeHammer _hammer =new NextPrimeHammer();

	private AtomicInteger _sdNum,_rspNum,_nphNum;

	private ConcurrentLinkedDeque<Deferred<Tool>> _sdWaitingqueue;
	private ConcurrentLinkedDeque<Deferred<Tool>> _rspWaitingqueue;
	private ConcurrentLinkedDeque<Deferred<Tool>> _nphWaitingqueue;

	private List<ManufactoringPlan> _listOfPlans;
	/**
	 * Constructor
	 */
	public Warehouse(){
		_sdWaitingqueue = new ConcurrentLinkedDeque<Deferred<Tool>>();
		_rspWaitingqueue =  new ConcurrentLinkedDeque<Deferred<Tool>>();
		_nphWaitingqueue =  new ConcurrentLinkedDeque<Deferred<Tool>>();
		_sdNum=new AtomicInteger(0);
		_rspNum=new AtomicInteger(0);
		_nphNum=new AtomicInteger(0);
		_listOfPlans=new ArrayList<ManufactoringPlan>();
	}

	/**
	 * Tool acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 * @param type - string describing the required tool
	 * @return a deferred promise for the  requested tool
	 */
	public synchronized Deferred<Tool> acquireTool(String type){
		Deferred<Tool> _returnVal=new Deferred<Tool>();
		switch(type){
		case("gs-driver"):{
			if (_sdNum.get()>0){
				_returnVal.resolve(_screwdriver);
				_sdNum.decrementAndGet();
			}
			else
				_sdWaitingqueue.add(_returnVal);
		}
		break;
		case("np-hammer"):{
			if (_nphNum.get()>0){
				_returnVal.resolve(_hammer);
				_nphNum.decrementAndGet();
			}
			else
				_nphWaitingqueue.add(_returnVal);
		}
		break;
		case("rs-pliers"): {
			if (_rspNum.get()>0){
				_returnVal.resolve(_plaiers);
				_rspNum.decrementAndGet();
			}
			else
				_rspWaitingqueue.add(_returnVal);
		}
		}
		return _returnVal;

	}

	/**
	 * Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	 * @param tool - The tool to be returned
	 */
	public void releaseTool(Tool tool){
		switch(tool.getType()){
		case("GCD Screwdriver"):{
			if (_sdWaitingqueue.size()>0){
				_sdWaitingqueue.poll().resolve(_screwdriver);

			}
			else
				_sdNum.incrementAndGet();
		}
		break;
		case("Next Prime Hammer"):{
			if (_nphWaitingqueue.size()>0){
				_nphWaitingqueue.poll().resolve(_hammer);

			}
			else
				_nphNum.incrementAndGet();
		}
		break;
		case("RandomSumPliers"): {
			if (_rspWaitingqueue.size()>0){
				_rspWaitingqueue.poll().resolve(_plaiers);

			}
			else
				_rspNum.incrementAndGet();
		}
		}

	}



	/**
	 * Getter for ManufactoringPlans
	 * @param product - a string with the product name for which a ManufactoringPlan is desired
	 * @return A ManufactoringPlan for product
	 */
	public ManufactoringPlan getPlan(String product){
		for (ManufactoringPlan _plan: _listOfPlans){
			if (_plan.getProductName().equals(product))
				return _plan;
		}
		return null;
	}
	/**
	 * Store a ManufactoringPlan in the warehouse for later retrieval
	 * @param plan - a ManufactoringPlan to be stored
	 */
	public void addPlan(ManufactoringPlan plan){
		_listOfPlans.add(plan);
	}

	/**
	 * Store a qty Amount of tools of type tool in the warehouse for later retrieval
	 * @param tool - type of tool to be stored
	 * @param qty - amount of tools of type tool to be stored
	 */
	public void addTool(Tool tool, int qty){
		String _kindOfTool=tool.getType();
		switch(_kindOfTool){
		case("GCD Screwdriver"): _sdNum.addAndGet(qty);

		case("Next Prime Hammer"): _nphNum.addAndGet(qty);

		case("RandomSumPliers"): _rspNum.addAndGet(qty);
		}
	}
	
	public int getNumOfPlan(){
		return _listOfPlans.size();
	}
}






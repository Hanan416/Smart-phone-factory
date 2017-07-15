/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.BreakMyGson;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tasks.Produce;
import bgu.spl.a2.sim.tools.GCDScrewdriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	private static WorkStealingThreadPool _simulate;
	private static Warehouse _ourWareHouse;
	private static BreakMyGson _rawData;

	/**
	 * Begin the simulation
	 * Should not be called before attachWorkStealingThreadPool()
	 */
	public static ConcurrentLinkedQueue<Product> start() throws Exception {
//		        ConcurrentLinkedQueue<Product> ans = new ConcurrentLinkedQueue<>();
//		        _simulate.start();
//		        for (int i = 0; i < _rawData.getWaves().size(); i++) {
//		            AtomicBoolean running = new AtomicBoolean(true);
//		            AtomicBoolean check = new AtomicBoolean(false);
//		            while (running.get()) {
//		                if (!check.get()) {
//		                    check.set(true);
//		                    List<LinkedTreeMap> d = (List<LinkedTreeMap>) _rawData.getWaves().get(i);
//		                    AtomicInteger total = new AtomicInteger(0);
//		                    for (LinkedTreeMap ltm : d) {
//		                        String s = (String) ltm.get("product");
//		                        long id = ((Double) ltm.get("startId")).longValue();
//		                        int q = ((Double) ltm.get("qty")).intValue();
//		                        total.addAndGet(q);
//		                        for (int z = 0; z < q; z++) {
//		                            Product p = new Product(id + z, s);
//		                            ans.add(p);
//		                            Produce newContract = new Produce(p);
//		                            newContract.getResult().whenResolved(() -> {
//		                                if (total.decrementAndGet() == 0) {
//		                                    running.set(false);
//		                                }
//		                            });
//		                            _simulate.submit(newContract);
//		                        }
//		                    }
//		                }
//		            }
//		        }
//		        return ans;
//		    }
		ConcurrentLinkedQueue<Product> ans = new ConcurrentLinkedQueue<>();
		_simulate.start();
		AtomicBoolean startNewWave=new AtomicBoolean(false);
		for(int j=0;j<_rawData.getWaves().size();j++){
			List<LinkedTreeMap>_wave=_rawData.getWaves().get(j);
			AtomicInteger _numOfProductToCreate=new AtomicInteger(sumQty(j));
			startNewWave.set(false);
			for(int k=0;k<_wave.size();k++){
				LinkedTreeMap _productOrder=_wave.get(k);
				String _productName=(String)_productOrder.get("product");
				long _startID=((Double)_productOrder.get("startId")).longValue();
				int _qty=((Double)_productOrder.get("qty")).intValue();
				for (int i=0;i<_qty;i++){
					Product _productToProduce=new Product(_startID+i, _productName);
					ans.add(_productToProduce);
					Produce _startProduce =new Produce(_productToProduce);
					_simulate.submit(_startProduce);
					_startProduce.getResult().whenResolved(()->{
						_numOfProductToCreate.decrementAndGet();
						if (_numOfProductToCreate.get()==0){
							startNewWave.set(true);
						}
					});
					
				}
			}
			while (!startNewWave.get()){
			}
		}


		return ans;
	}


	private static int sumQty(int _numofwave) {
		List<LinkedTreeMap> _wave=_rawData.getWaves().get(_numofwave);
		int _returnSum=0;
		for (LinkedTreeMap _product:_wave){
			_returnSum=_returnSum+((Double)_product.get("qty")).intValue();
		}
		return _returnSum;

	}


	/**
	 * attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	 * @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	 */
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool){
		_simulate=myWorkStealingThreadPool;
	}

	public static void main(String [] args){
		Gson _gsonObj= new Gson();
		BufferedReader _inputStream=null;
		try {
			_inputStream = new BufferedReader(new FileReader(args[0]));
		} catch (FileNotFoundException e) {
			System.out.println("bo ho");

		}

		_rawData = _gsonObj.fromJson(_inputStream,BreakMyGson.class);
		attachWorkStealingThreadPool(new WorkStealingThreadPool(_rawData.get_numOfThread()));
		_ourWareHouse=new Warehouse();
		CreateWareHouse();

		try {
			ConcurrentLinkedQueue<Product> SimulationResult=null;
			try {
				SimulationResult = start();
			} catch (Exception e) {
				System.out.println("Pali");
			}
			FileOutputStream fout;
			fout = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(SimulationResult);

		} catch (IOException e) {
			System.out.println("Et ha-pali");
		}
		try {
			_simulate.shutdown();
		} catch (InterruptedException e) {
			return;
		}


	}

	private static void CreateWareHouse() {
		for (LinkedTreeMap rawToolData:_rawData.getTools()){
			Tool _rawTool=null;
			String _typeOfTool=(String)rawToolData.get("tool");
			switch (_typeOfTool){
			case ("gs-driver"): _rawTool=new GCDScrewdriver(); break;
			case ("np-hammer"):	_rawTool=new NextPrimeHammer(); break;
			case ("rs-pliers"): _rawTool=new RandomSumPliers(); break;
			}
			int qty = ((Double)rawToolData.get("qty")).intValue();
			_ourWareHouse.addTool(_rawTool, qty);
		}
		for (LinkedTreeMap _rawPlanData: _rawData.getPlans()){
			String _prodName=(String)_rawPlanData.get("product");
			ArrayList<String> _rawToolList = (ArrayList<String>)_rawPlanData.get("tools");
			String[] _validTool = new String[_rawToolList.size()];
			for(int i=0;i<_rawToolList.size();i++){
				_validTool[i]=_rawToolList.get(i);
			}

			ArrayList<String> _rawParts=(ArrayList<String>)_rawPlanData.get("parts");
			String[] _validparts = new String[_rawParts.size()];
			for(int i=0;i<_rawParts.size();i++){
				_validparts[i]=_rawParts.get(i);
			}

			ManufactoringPlan _toAdd=new ManufactoringPlan(_prodName, _validparts, _validTool);
			_ourWareHouse.addPlan(_toAdd);
		}

	}


	public static Warehouse getWarehouse(){
		return _ourWareHouse;
	}
}


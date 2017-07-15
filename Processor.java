package bgu.spl.a2;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 */
public class Processor implements Runnable {

	private final WorkStealingThreadPool pool;
	private final int id;
	protected boolean isActive = true;


	/**
	 * constructor for this class
	 *
	 * IMPORTANT:
	 * 1) this method is package protected, i.e., only classes inside
	 * the same package can access it - you should *not* change it to
	 * public/private/protected
	 *
	 * 2) you may not add other constructors to this class
	 * nor you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param id - the processor id (every processor need to have its own unique
	 * id inside its thread pool)
	 * @param pool - the thread pool which owns this processor
	 */
	/*package*/ Processor(int id, WorkStealingThreadPool pool) {
		this.id = id;
		this.pool = pool;
	}


	@Override
	public void run() {
		int i=1; //for testing outputs
		while(isActive){ //If the current processor awake
			if (!pool._pQueues[id].isEmpty()){
				Task<?> _myTask = pool._pQueues[id].pollFirst();
				_myTask.handle(this);
			}
			else{
				boolean didSteal=stealTasks(id);
				if(!didSteal){
					try{
						int _VM=pool._vMonitor.getVersion();
						pool._vMonitor.await(_VM);
					}catch(InterruptedException e){
						isActive = false;
						Thread.currentThread().interrupt();
					}
				}
			}
			i++;
		}
	}

	/*package*/ boolean stealTasks(int id){
		boolean didSteal=false;
		int _nighborID=(id+1)%pool._poolOfThreads.length;
		while (id!=_nighborID){
			if (pool._pQueues[_nighborID].size()>1){
				int _sizeToSteal=pool._pQueues[_nighborID].size()/2;
				while (_sizeToSteal>0 && pool._pQueues[_nighborID].size()>1){
					Task<?> _stolenTask = pool._pQueues[_nighborID].pollLast();
					if (_stolenTask!=null){
						pool._pQueues[id].addLast(_stolenTask);
						_sizeToSteal--;
						didSteal=true;
						_nighborID=id;
					}
				}
			}
			else
				_nighborID=(_nighborID+1)%pool._poolOfThreads.length;					
		}
		return didSteal;

	}

public void assignTask(Task<?> task){
	pool._pQueues[id].addLast(task);
	pool._vMonitor.inc();

}
}



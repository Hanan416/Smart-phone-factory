package bgu.spl.a2.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;

public class SumMatrix extends Task<int[]>{
	
	private int[][] array;
	public SumMatrix(int[][] array) {
		this.array = array;
	}

	protected void start(){
	//	int sum=0;
		ArrayList<Task<Integer>> tasks = new ArrayList<Task<Integer>>();
		int rows = array.length;
		for(int i=0;i<rows;i++){
			SumRow newTask=new SumRow(array,i);
			spawn(newTask);
			tasks.add(newTask);
		}
	//	spawn(tasks.get(0),tasks.get(1),tasks.get(2));
		
	//	spawn(tasks.get(0),tasks.get(1),tasks.get(2));
		whenResolved(tasks,()->{
			int[] res = new int[rows];
			for(int j=0; j< rows; j++){
				res[j] = tasks.get(j).getResult().get();
			}
			complete(res);
			
			
		}
	);
	}
	
	public static void main(String[] args) throws InterruptedException {
        WorkStealingThreadPool pool = new WorkStealingThreadPool(250);
        int[][] array = {{1,2,3},{1,2,3},{3,3,3},{45,65,89},{456,567,894},{456,321,789},{18,178,12}};
        

        SumMatrix task = new SumMatrix(array);

        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {
            //warning - a large print!! - you can remove this line if you wish
        	System.out.println("Lmabda mamba");
            System.out.println(Arrays.toString(task.getResult().get()));
            System.out.println(task.getResult().get());
            l.countDown();
        });
        
        l.await();
        
        pool.shutdown();
        }
    }




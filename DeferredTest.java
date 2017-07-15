package bgu.spl.a2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author user
 *
 */
public class DeferredTest {

	private Deferred<Integer> _OUT;


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		_OUT=createDeferred();
	}

	private Deferred<Integer> createDeferred(){
		return new Deferred<Integer>();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		_OUT=null;
	}


	/*
	 * @pre: isResoulved()
	 * @post: none
	 * @inv: return the result of a resolved problem
	 */
	@Test public void testGet(){
		if(!_OUT.isResolved()){
			try{
				_OUT.get();
				fail("Did not get excpected exeption");
			} catch (IllegalStateException e){
				assertTrue(true);
			}
		}
		else{
			try{
				_OUT.get();
				assertTrue(true);
			} catch (Exception e){
				fail("Did get unexcpected exeption");
			}
		}
	}


	/*
	 * @pre:
	 * @post:
	 * @inv: a function that answers whether the task has finished its calculation 
	 */
	@Test public void testIsResolved(){
		boolean _ans1 = _OUT.isResolved();
		_OUT.resolve(34);
		boolean _ans2 = _OUT.isResolved();

		assertFalse(_ans1);
		assertTrue(_ans2);

	}


	/*
	 *@pre: !isResolved()
	 *@post: isResovled()
	 *@inv: resolves the given task; 
	 */
	@Test public void testResolve(){
		if (!_OUT.isResolved()){
			_OUT.resolve(34);
			int _ans = _OUT.get();
			assertEquals(_ans,34);
		}
		else{
			try{
				_OUT.resolve(34);
				fail("Not alwoed to resolve a solved Dereffer");
			}catch(Exception e){
				assertTrue(true);
			}
		}
	}


	/*
	 *@pre: none
	 *@post: none
	 *@inv: regestring a runnable object to notify when this object is resolved
	 */
	@Test public void testWhenResolved(){
		_OUT.whenResolved(()->{
			assertTrue(true);});
		if(!_OUT.isResolved())
			_OUT.resolve(21);


	}
}

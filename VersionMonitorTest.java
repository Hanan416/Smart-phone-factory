package bgu.spl.a2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VersionMonitorTest {
	private VersionMonitor _OUT;
	@Before
	public void setUp() throws Exception {
		_OUT=createVersionMonitor();
	}

	private VersionMonitor createVersionMonitor(){
		return new VersionMonitor();
	}

	@After
	public void tearDown() throws Exception {
		_OUT=null;
	}


	/*
	 * @pre:
	 * @post:
	 * @inv: returns the current(!!) version
	 */
	@Test
	public void testGetVersion(){
		assertEquals(_OUT.getVersion(),0);
	}


	/*
	 * @pre:
	 * @post: getVersion()= @pre.getVersion()+1
	 * @inv: increments the version to the next version in a linear way
	 */
	@Test
	public void testInc(){
		int _preVersion=_OUT.getVersion();
		_OUT.inc();
		assertEquals(_OUT.getVersion(),_preVersion+1);
		_OUT.inc();
		assertEquals(_OUT.getVersion(),_preVersion+2);
	}


	/*
	 *@pre: none
	 *@post: none
	 *@inv: informes the monitor when version does change
	 */
	@Test
	public void testAwait(){
		int _preVersion=_OUT.getVersion();

		try{
			_OUT.await(_preVersion);
			_OUT.inc();
			assertTrue(false);
		}
		catch (InterruptedException e){
			assertTrue(true);
		}

	}
}




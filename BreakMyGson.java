package bgu.spl.a2.sim.conf;

import java.util.List;

import com.google.gson.internal.LinkedTreeMap;

public class BreakMyGson {
	private int threads;
	private List<LinkedTreeMap> tools;
	private List<LinkedTreeMap> plans;
	private List<List<LinkedTreeMap>> waves;
	public int get_numOfThread() {
		return threads;
	}
	
	
	public void set_numOfThread(int _numOfThread) {
		this.threads = _numOfThread;
	}
	
	public List<LinkedTreeMap> getTools() {
		return tools;
	}
	
	public void setTools(List<LinkedTreeMap> tools) {
		this.tools = tools;
	}
	
	public List<LinkedTreeMap> getPlans() {
		return plans;
	}
	
	public void setPlans(List<LinkedTreeMap> plans) {
		this.plans = plans;
	}
	
	public List<List<LinkedTreeMap>> getWaves() {
		return waves;
	}
	
	public void setWaves(List<List<LinkedTreeMap>> waves) {
		this.waves = waves;
	}
	
}

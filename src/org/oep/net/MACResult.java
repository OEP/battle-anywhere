package org.oep.net;

public class MACResult {
	public MACResult(String mac, int resId) {
		MAC = mac;
		ResId = resId;
	}
	
	public String MAC;
	public int ResId;
	
	public String toString() {
		return MAC;
	}
}

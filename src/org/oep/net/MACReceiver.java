package org.oep.net;

import java.util.List;

public interface MACReceiver {
	public void onReceive(List<MACResult> result);
}

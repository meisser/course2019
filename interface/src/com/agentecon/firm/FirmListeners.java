// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm;

import com.agentecon.util.AbstractListenerList;

public class FirmListeners extends AbstractListenerList<IFirmListener> implements IFirmListener {
	
	public void reportDividend(double amount) {
		reportDividend(null, amount);
	}

	@Override
	public void reportDividend(IFirm inst, double amount) {
		for (IFirmListener l : list) {
			l.reportDividend(inst, amount);
		}
	}

}

/**
 * Created by Luzius Meisser on Jun 13, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.data;

import java.util.Collection;

import com.agentecon.firm.IFirm;

public class CollectiveFirmData extends CollectiveData {
	
	public double averageDividends;
	public double totalDividends;

	public CollectiveFirmData(Collection<? extends IFirm> firms) {
		super(firms);
		for (IFirm f: firms){
			this.totalDividends += f.getShareRegister().getAverageDividend();
		}
		this.averageDividends = this.totalDividends / firms.size();
	}

}

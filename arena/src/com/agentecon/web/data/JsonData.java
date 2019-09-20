/**
 * Created by Luzius Meisser on Jun 13, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.data;

import com.google.gson.Gson;

public class JsonData {

	public String getJson(){
		return new Gson().toJson(this);
	}
	
}

/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.metric.export;

import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.agentecon.metric.NoInterestingTimeSeriesFoundException;
import com.agentecon.metric.SimStats;

public class ExcelWriter {

	private File folder;

	public ExcelWriter(File folder) {
		folder.mkdirs();
		assert folder.exists();
		this.folder = folder;
	}

	public void export(SimStats stats) throws IOException, NoInterestingTimeSeriesFoundException {
		File file = getNewFile(stats.getName());
		try {
			try (PrintStream writer = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)))) {
				stats.print(writer, ", ");
			}
			System.out.println("Opening " + file.getAbsolutePath());
			Desktop.getDesktop().open(file);
		} catch (NoInterestingTimeSeriesFoundException e) {
			file.delete();
			throw e;
		}
	}

	private File getNewFile(String name) {
		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		File f = new File(folder, dateString + " " + name + ".csv");
		int count = 1;
		while (f.exists()) {
			f = new File(folder, dateString + " " + name + " (" + count++ + ").csv");
		}
		return f;
	}

}

package com.rw.trace.stat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.concurrent.Callable;

import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.TableUpdateContainer;

public class PrintTableState implements Callable<Void> {

	@Override
	public Void call() throws Exception {
		Enumeration<TableUpdateContainer<Object, Object>> enumeration = DataAccessFactory.getTableUpdateCollector().getAllContainer();
		Field f = TableUpdateContainer.class.getDeclaredField("running");
		f.setAccessible(true);
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\table_state", true))), true);
		for (; enumeration.hasMoreElements();) {
			TableUpdateContainer<Object, Object> container = enumeration.nextElement();
			writer.append(container.getTableName() + "," + f.getBoolean(container));
			writer.append("\n");
		}
		writer.append("=========================================================");
		writer.append("\n");
		writer.flush();
		writer.close();
		return null;
	}

}

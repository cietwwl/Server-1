package com.rw.fsutil.dao.cache.trace;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

public class RedLogger {

	public static void readLogger(String path) throws IOException {
		File f = new File(path);
		String writePath = path + "_temp";
		File dir = new File(writePath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		Charset charSet = Charset.forName("UTF-8");
		String[] list = f.list();
		for (String text : list) {
			File cur = new File(path + "\\" + text);
			if (cur.isDirectory()) {
				continue;
			}
			FileInputStream input = new FileInputStream(cur);
			DataInputStream dataInput = new DataInputStream(input);
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir + "\\" + text)));
			try {
				for (;;) {
					int len = dataInput.readInt();
					if (len < 0) {
						return;
					}
					byte flag = dataInput.readByte();
					byte[] array = new byte[len];
					dataInput.read(array);
					if (flag == 1) {
						ByteArrayInputStream bain = new ByteArrayInputStream(array);
						GZIPInputStream zis = new GZIPInputStream(bain);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte[] tmpBuffer = new byte[256];
						int n;
						while ((n = zis.read(tmpBuffer, 0, 256)) >= 0) {
							baos.write(tmpBuffer, 0, n);
						}
						zis.close();
						array = baos.toByteArray();
						baos.close();
					}
					String str = new String(array, charSet);
					writer.write(str);
					writer.flush();
				}
			} catch (Exception e) {
				if (!(e instanceof java.io.EOFException)) {
					e.printStackTrace();
				}
			} finally {
				writer.flush();
				writer.close();
				dataInput.close();
				System.out.println("写入完成：" + text);
			}
		}
	}

	public static void readLogger2(String path) throws IOException {
		File f = new File(path);
		String writePath = path + "_temp2";
		File dir = new File(writePath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		Charset charSet = Charset.forName("UTF-8");
		String[] list = f.list();
		for (String text : list) {
			File cur = new File(path + "\\" + text);
			if (cur.isDirectory()) {
				continue;
			}
			FileInputStream input = new FileInputStream(cur);
			DataInputStream dataInput = new DataInputStream(input);
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir + "\\" + text)));
			ByteArrayOutputStream ops = new ByteArrayOutputStream();
			try {

				for (;;) {
					int len = dataInput.readInt();
					if (len < 0) {
						break;
					}
					byte flag = dataInput.readByte();
					if (flag < 0) {
						break;
					}
					byte[] array = new byte[len];
					dataInput.read(array);
					ops.write(array);

				}

			} catch (Exception e) {
				if (!(e instanceof java.io.EOFException)) {
					e.printStackTrace();
				}
			} finally {
				ByteArrayInputStream bain = new ByteArrayInputStream(ops.toByteArray());
				GZIPInputStream zis = new GZIPInputStream(bain);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] tmpBuffer = new byte[1024];
				int n;
				while ((n = zis.read(tmpBuffer, 0, 1024)) >= 0) {
					baos.write(tmpBuffer, 0, n);
				}
				zis.close();
				byte[] array = baos.toByteArray();
				baos.close();

				String str = new String(array, charSet);
				writer.write(str);
				writer.flush();
				writer.flush();
				writer.close();
				dataInput.close();
				System.out.println("写入完成：" + text);
			}

		}
	}

	public static void main(String[] args) throws IOException {
		String path = "E:\\workspace_release\\server\\server\\FSApp\\datalogs\\20160809";
		readLogger(path);
	}
}

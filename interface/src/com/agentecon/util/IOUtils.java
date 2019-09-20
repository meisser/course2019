package com.agentecon.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;

public class IOUtils {

	public static byte[] readData(int size, InputStream source) throws IOException {
		byte[] data = new byte[size];
		int pos = 0;
		while (pos < size) {
			int read = source.read(data, pos, size - pos);
			if (read == -1) {
				throw new RuntimeException("Early end of entry");
			} else {
				pos += read;
			}
		}
		return data;
	}

	public static byte[] readData(InputStream source) throws IOException {
		int ava = source.available();
		int size = ava == 0 ? 1000 : ava;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		byte[] buffer = new byte[size];
		while (true) {
			int read = source.read(buffer, 0, buffer.length);
			if (read > 0) {
				out.write(buffer, 0, read);
			} else {
				assert read == -1;
				break;
			}
		}
		return out.toByteArray();
	}

	public static byte[] readData(URL url) throws SocketTimeoutException, IOException {
		try (InputStream input = url.openStream()) {
			return readData(input);
		}
	}

}

package com.agentecon.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class VisServer extends BasicServer {

	private static final String VIS_DIR = "/vis";
	private static final File WEB_CONTENT = new File("WebContent");

	private File baseFolder;

	public VisServer(int port) {
		super(port);
		this.baseFolder = new File("../frontend" + VIS_DIR);
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		assert method == Method.GET : session.getUri();
		String uri = session.getUri();
		if (uri.startsWith(VIS_DIR)) {
			return serve(session, uri.substring(VIS_DIR.length()));
		} else {
			File webContentFile = new File(WEB_CONTENT, uri);
			if (webContentFile.getName().isEmpty()) {
				webContentFile = new File(webContentFile, "index.html");
			}
			if (webContentFile.isFile() && webContentFile.getAbsolutePath().startsWith(WEB_CONTENT.getAbsolutePath())) {
				return serveFile(webContentFile);
			} else {
				return super.serve(session);
			}
		}
	}

	protected Response serve(IHTTPSession session, String uri) {
		File child = new File(baseFolder, uri);
		if (!child.isFile() || !child.getAbsolutePath().startsWith(baseFolder.getAbsolutePath())) {
			child = new File(baseFolder, "index.html");
			assert child.isFile();
		}
		return serveFile(child);
	}

	private Response serveFile(File child) {
		try {
			return NanoHTTPD.newChunkedResponse(Status.OK, getMimeTypeForFile(child.getName()), new FileInputStream(child));
		} catch (FileNotFoundException e) {
			throw new java.lang.RuntimeException(child.getAbsolutePath());
		}
	}

}

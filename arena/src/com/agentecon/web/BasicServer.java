package com.agentecon.web;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import fi.iki.elonen.NanoHTTPD;

public class BasicServer extends NanoHTTPD {

	public BasicServer(int port) {
		super(port);
		initMime();
	}

	protected void initMime() {
		if (mimeTypes().isEmpty()) {
			 mimeTypes().put("css", "text/css");
			 mimeTypes().put("htm", "text/html");
			 mimeTypes().put("html", "text/html");
			 mimeTypes().put("xml", "text/xml");
			 mimeTypes().put("gif", "image/gif");
			 mimeTypes().put("jpg", "image/jpeg");
			 mimeTypes().put("jpeg", "image/jpeg");
			 mimeTypes().put("png", "image/png");
			 mimeTypes().put("pdf", "application/pdf");
			 mimeTypes().put("json", "application/json");
		}
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		assert method == Method.GET;
		String uri = session.getUri();
		System.out.println(method + " requested  on " + uri);
		return NanoHTTPD.newFixedLengthResponse("You sent the following request to me:\n" + toString(session));
	}

	private String toString(IHTTPSession session) {
		String html = "<html><body>";
		html += "<p>Method: " + session.getMethod() + "</p>";
		html += "<p>Path: " + session.getUri() + "</p>";
		html += "<p>Parameters: " + session.getQueryParameterString() + "</p>";
		html += "<p>Headers: " + session.getHeaders() + "</p>";
		return html + "</body></html>";
	}

	public void run() throws IOException, InterruptedException {
		start();
		try {
			URI uri = new URI("http://" + (getHostname() == null ? "localhost" : getHostname()) + ":" + getListeningPort() + getStartPath());
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(uri);
			} else {
				System.out.println("Listening on " + uri);
			}
			while (true) {
				Thread.sleep(60000);
			}
		} catch (URISyntaxException e) {
			throw new java.lang.RuntimeException(e);
		} finally {
			stop();
		}
	}

	protected String getStartPath() {
		return "/vis";
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		BasicServer server = new BasicServer(8080);
		server.run();
	}

}

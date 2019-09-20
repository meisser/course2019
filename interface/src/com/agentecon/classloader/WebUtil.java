package com.agentecon.classloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import com.agentecon.util.IOUtils;

public class WebUtil {

	/**
	 * In order to access the github API, an authentication code is necessary.
	 * To generate one, login to github.com and visit https://github.com/settings/tokens
	 * There, generate a new access token with "repo" access. The token looks something like
	 * "1c4dd4ab3e197284fbe2010d91a8a34dc71525f7". Then, create a new text file named
	 * "github-secret.txt" with content "?access_token=1c4dd4ab3e197284fbe2010d91a8a34dc71525f7"
	 * (without the "). This file must be placed one directory above your github repo, e.g.
	 * next to the folder "team004" on your local hard drive if you are in team 4.
	 * Note that github scans all uploaded files for access tokens and if it detects one,
	 * it immediately deactivates it for security reasons. Thus, never write it into a file
	 * that is part of your repository. That's also why the example token mentioned here is
	 * no longer valid. 
	 */
	private final static String API_ADDRESS = "https://api.github.com";
	private final static String ACCESS_SECRETS = loadSecrets();

	private static String loadSecrets() {
		Path path = FileSystems.getDefault().getPath("../..", "github-secret.txt");
		try {
			return Files.readAllLines(path, Charset.defaultCharset()).get(0);
		} catch (IOException e) {
			System.out.println("Could not find api secrets file on " + path.toAbsolutePath());
			return "";
		}
	}

	public static boolean hasAuthorizationCode() {
		return ACCESS_SECRETS.length() > 0;
	}

	public static String addSecret(String address) throws IOException {
		if (address.contains(API_ADDRESS)) {
			checkAuthorizationCode();
			if (address.contains("?")) {
				return address + "&" + ACCESS_SECRETS.substring(1);
			} else {
				return address + ACCESS_SECRETS;
			}
		} else {
			return address;
		}
	}

	public static String readHttp(String address) throws FileNotFoundException, IOException {
		address = addSecret(address);
		String content = "";
		String nextPage = null;
		while (address != null) {
			long t0 = System.nanoTime();
			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			InputStream stream = conn.getInputStream();
			try {
				String rateLimit = conn.getHeaderField("X-RateLimit-Remaining");
				if (rateLimit != null){
					int remaining = Integer.parseInt(rateLimit);
					if (remaining < 1000){
						System.out.println("Warning: only " + remaining + " API calls remaining");
					}
				}
				content += new String(IOUtils.readData(stream));
				nextPage = getNextPageUrl(conn);
			} finally {
				stream.close();
			}
			long t1 = System.nanoTime();
			System.out.println((t1 - t0) / 1000000 + "ms spent reading " + address);
			address = nextPage;
		}
		return content;
	}

	protected static String getNextPageUrl(URLConnection conn) {
		String next = conn.getHeaderField("Link");
		if (next != null && next.contains("next")) {
			int open = next.indexOf('<');
			int close = next.indexOf('>');
			return next.substring(open + 1, close);
		} else {
			return null;
		}
	}

	public static String extract(String content, String what, int[] pos) {
		String item = "\"" + what + "\":\"";
		int pos2 = content.indexOf(item, pos[0]);
		if (pos2 >= 0) {
			int urlEnd = content.indexOf('"', pos2 + item.length());
			pos[0] = urlEnd;
			return content.substring(pos2 + item.length(), urlEnd);
		} else {
			return null;
		}
	}

	public static String readGitApi(String owner, String repo, String command, String path, String branch) throws IOException {
		String address = API_ADDRESS + "/repos/" + owner + "/" + repo + "/" + command + "/" + path + "?ref=" + branch;
		return readHttp(address);
	}

	public static void checkAuthorizationCode() throws IOException {
		if (!hasAuthorizationCode()) {
			throw new IOException("In order to dynamically load agents from github, follow the instructions in com.agentecon.classloader.WebUtil.java .");
		}
	}

}

/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

public class GithubeventMethod extends WebApiMethod {

	private transient ListMethod simulations;

	public GithubeventMethod(ListMethod simulations) {
		this.simulations = simulations;
	}

	@Override
	public Response execute(IHTTPSession session, Parameters params) throws IOException {
		if (session.getMethod() == Method.POST) {
			// note: stream should not be closed, otherwise we can't send back an answer
			InputStream inputStream = session.getInputStream();
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			String content = new String(data);
			System.out.println("Received github event with the following content\n" + content);
			JsonObject object = new Gson().fromJson(content, JsonObject.class);
			JsonObject repository = object.getAsJsonObject("repository");
			JsonPrimitive repoName = repository.getAsJsonPrimitive("name");
			JsonArray commits = object.getAsJsonArray("commits");
			JsonElement headcommit = object.get("head_commit");
			if (headcommit instanceof JsonObject && commits.size() == 1) {
				JsonArray changeList = ((JsonObject) headcommit).getAsJsonArray("modified");
				if (hasChangeJavaFiles(changeList)) {
					System.out.println("Updating repository " + repoName.getAsString());
					simulations.notifyRepositoryChanged(repoName.getAsString());
				} else {
					System.out.println("No relevant change found.");
				}
			} else {
				// if the latest commit is not described or if there were
				// multiple commits, just assume the worst. :)
				System.out.println("Triggering update because of multiple changes.");
				simulations.notifyRepositoryChanged(repoName.getAsString());
			}
		} else if (params.getParam("repo") != null) {
			// for local testing
			System.out.println("Received github test event");
			simulations.notifyRepositoryChanged(params.getParam("repo"));
		}

		return NanoHTTPD.newFixedLengthResponse("");
	}

	private boolean hasChangeJavaFiles(JsonArray changeList) {
		boolean[] java = new boolean[1];
		changeList.forEach(new Consumer<JsonElement>() {

			@Override
			public void accept(JsonElement t) {
				java[0] = t.getAsString().endsWith(".java");
			}
		});
		return java[0];
	}

}

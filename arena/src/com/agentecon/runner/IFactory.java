package com.agentecon.runner;

import java.io.IOException;

public interface IFactory<T> {

	public T create() throws IOException;

}

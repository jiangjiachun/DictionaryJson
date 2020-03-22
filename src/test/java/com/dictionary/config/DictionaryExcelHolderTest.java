/**
 * 
 */
package com.dictionary.config;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public final class DictionaryExcelHolderTest {

	@Test
	public void analyze() throws IOException {
		File file = new File(DictionaryExcelHolder.class.getResource("/").getPath() + "../../src/main/resources/" + DictionaryExcelHolder.JS_JSON_PATH);
		new DictionaryExcelHolder().analyze(file);
	}
	
}

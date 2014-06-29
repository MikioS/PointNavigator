package com.project.util;

import java.io.IOException;
import android.util.Log;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseJson {
	
	protected String content;

	// JSON文字列を、JsonNodeオブジェクトに変換する（1）
	protected JsonNode getJsonNode(String str) {
		
		try {
			return new ObjectMapper().readTree(str);
		} 
		catch (IOException e) {
			Log.d(getClass().getName(), e.getMessage());
		}
		return null;
	}

	// JSON文字列を読み込む
	public void loadJson(String str) {
	}

	public String getContent() {
		return this.content;
	}
}

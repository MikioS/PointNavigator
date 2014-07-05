package com.project.pn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.util.ParseJson;


public class ParseStoreInfo extends ParseJson {

	// 駅情報のリスト
	private List<StoreInfo> storeInfoList = new ArrayList<StoreInfo>();

	public List<StoreInfo> getStoreInfo() {
		return storeInfoList;
	}

	@Override
	public void loadJson(String str) {
		JsonNode root = getJsonNode(str);
		if (root != null){

			// 最寄駅のイテレータを取得する（1）
			Iterator<JsonNode> ite = root.path("response").path("store").elements();
			// 要素の取り出し（2）
			while (ite.hasNext()) {
				JsonNode j = ite.next();

				// 駅情報のセット（3）
				StoreInfo e = new StoreInfo();
				
				e.x = j.path("x").asDouble();
				e.y = j.path("y").asDouble();
				
				e.name = j.path("name").asText();
				String pointNames = j.path("pointNames").asText();
				e.pointNames = pointNames;

				storeInfoList.add(e);
			}
		}
	}

	

}

package com.project.pn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.util.ParseJson;


public class ParseMoyori extends ParseJson {

	// 駅情報のリスト
	private List<EkiInfo> ekiinfo = new ArrayList<EkiInfo>();

	public List<EkiInfo> getEkiinfo() {
		return ekiinfo;
	}

	@Override
	public void loadJson(String str) {
		JsonNode root = getJsonNode(str);
		if (root != null){

			// 最寄駅のイテレータを取得する（1）
			Iterator<JsonNode> ite = root.path("response").path("station").elements();
			// 要素の取り出し（2）
			while (ite.hasNext()) {
				JsonNode j = ite.next();

				// 駅情報のセット（3）
				EkiInfo e = new EkiInfo();
				
				e.x = j.path("x").asDouble();
				e.y = j.path("y").asDouble();
				
				e.name = j.path("name").asText();
				e.next = j.path("next").asText();
				e.prev = j.path("prev").asText();
				e.line = j.path("line").asText();

				// 「xxxm」を数値に変換
				e.distance = Integer.parseInt(j.path("distance").asText().split("m")[0]);

				// リストに追加（4）
				ekiinfo.add(e);
			}
		}
	}

	

}

package com.project.pn;

//最寄駅情報クラス
public class EkiInfo {
	public String name;		// 最寄駅名
	public String prev;		// 前の駅名 （始発駅の場合は null）
	public String next;		// 次の駅名 （終着駅の場合は null）
	public Double	x;		// 最寄駅の経度 （世界測地系）
	public Double	y;		// 最寄駅の緯度 （世界測地系）
	public int	distance;	// 指定の場所から最寄駅までの距離 （精度は 10 ｍ）
	public String	line;	// 最寄駅の存在する路線名
}

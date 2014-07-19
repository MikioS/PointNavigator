package com.project.pn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;

import java.util.HashMap;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.util.HttpAsyncLoader;

public class Main extends Activity implements LoaderCallbacks<String> {

	//TODO　１−１メニュー画面より、利用しているポイントを選択できるようにする
	//TODO　１−２選択されたポイントのみを表示するようにする
	//TODO 　２−１　メニュー画面より、ポイントカードの追加。「ポイントカード名（必須）選択式」、「利用可能店舗名（必須）」、「ジャンル（必須）選択式」、「住所（必須）」、「電話番号」、「Webサイト」、
	//TODO   ２−２ トーストから電話起動、ブラウザ起動を行う
	//TODO　　２−３　ポイントカードは、Ponta , nanaco TPoint など有名どころ以外は　「オリジナル」という言葉で
	//TODO AD 表示部分確保
	//TODO 地図検索（最寄り駅、住所）
	
	// マップオブジェクト（1）
	private GoogleMap googleMap = null;

	// マーカーと駅情報のHashMap
	private HashMap<Marker, StoreInfo> pointMarkerMap;

	// 地図の中心位置
	private CameraPosition centerPosition = null;
	
	//ポイントカードの種類
    static  boolean[] point_selected  = null;
    //ポイントカードのダイアログ
    AlertDialog.Builder point_dialog;
    //選択したポイントカード
    static  String[] point_Disp  = null;
    
	//お店の種類
    static  boolean[] store_selected  = null;
    //お店のダイアログ
    AlertDialog.Builder store_dialog;
    //選択したお店
    static  String[] store_Disp  = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pointMarkerMap = new HashMap<Marker, StoreInfo>();

		// MapFragmentを取得する(2)
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);

		try {
			// マップオブジェクトを取得する（3）
			googleMap = mapFragment.getMap();

			// Activityが初めて生成されたとき（4）
			if (savedInstanceState == null) {
				// フラグメントを保存する(5)
				mapFragment.setRetainInstance(true);
				// 地図の初期設定を行う（6）
				mapInit();

				// InfoWindowのクリックリスナー追加
				googleMap.setOnInfoWindowClickListener(

						new OnInfoWindowClickListener() {

							@Override
							public void onInfoWindowClick(Marker marker) {

								// 駅情報の取り出し
								StoreInfo e = pointMarkerMap.get(marker);

								Toast ts = Toast.makeText(getBaseContext(), 
										e.name + "(" + e.pointNames + "m)\n" , Toast.LENGTH_LONG);
								ts.setGravity(Gravity.TOP, 0, 200);
								ts.show();

							}
						}
				);

				googleMap.setOnCameraChangeListener(
						new OnCameraChangeListener() {
							@Override
							public void onCameraChange(CameraPosition cameraPosition) {
								if (centerPosition !=null ) {
									if ( 0.5 < calcDistance(centerPosition, cameraPosition) ) {
										execMoyori();
									}
									Log.d(getClass().getName(), Double.toString(calcDistance(centerPosition, cameraPosition)));

									centerPosition = cameraPosition;
								}
							}
						}
				);
			}
		}
		// GoogleMapが使用できないとき
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//登録されているポイントカードの読み込み
        ArrayList<String> poitDispList = new ArrayList<String>();
        poitDispList.add("Ponta");
        poitDispList.add("Tカード");
        poitDispList.add("nanaco");
        poitDispList.add("楽天カード");
        point_Disp = (String[])poitDispList.toArray(new String[poitDispList.size()]);
        point_selected = new boolean[point_Disp.length];
        for (int j = 0;j < point_selected.length;j ++) {
        	point_selected[j] = false;
        }
        
		//登録されているお店の読み込み
        ArrayList<String> storeDispList = new ArrayList<String>();
        storeDispList.add("コンビニ");
        storeDispList.add("飲食店");
        storeDispList.add("家電");
        storeDispList.add("ガソリンスタンド");
        store_Disp = (String[])storeDispList.toArray(new String[storeDispList.size()]);
        store_selected = new boolean[store_Disp.length];
        for (int j = 0;j < store_selected.length;j ++) {
        	store_selected[j] = false;
        }
		
		
		/*
		 * ボタン関係の処理
		 */
        //お店の種類を登録するボタン
        ((Button)findViewById(R.id.store)).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final boolean[] tmp_store_selected;
                        tmp_store_selected = store_selected.clone();
                        store_dialog = new AlertDialog.Builder(Main.this);
                        store_dialog.setTitle("お店の種類選択");
                        store_dialog.setMultiChoiceItems(
                                store_Disp,
                                store_selected,
                                new DialogInterface.OnMultiChoiceClickListener(){
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            	tmp_store_selected[which] = isChecked;
                            }});
                        store_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            //とりあえず、何もしない
                            }});
                        store_dialog.show();
                    }});
        //ポイントカードを選択するボタン
		  ((Button)findViewById(R.id.point)).setOnClickListener(
	                new OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                        final boolean[] tmp_days_selected;
	                        tmp_days_selected = point_selected.clone();
	                        point_dialog = new AlertDialog.Builder(Main.this);
	                        point_dialog.setTitle("ポイントカード選択");
	                        point_dialog.setMultiChoiceItems(
	                                point_Disp,
	                                point_selected,
	                                new DialogInterface.OnMultiChoiceClickListener(){
	                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
	                                tmp_days_selected[which] = isChecked;
	                                //TODO チェックのついていないものは、マーカーを削除して、チェックのついたものはマーカーを復元する
	                                
	                            }});
	                        point_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                            public void onClick(DialogInterface dialog, int id) {
	                            //とりあえず、何もしない
	                            }});
	                        point_dialog.show();
	                    }});
		
		
		
	}

	// 2点間の距離を求める(km)
	private double calcDistance(CameraPosition a, CameraPosition b) {
	
		double lata = Math.toRadians(a.target.latitude);
		double lnga = Math.toRadians(a.target.longitude);

		double latb = Math.toRadians(b.target.latitude);
		double lngb = Math.toRadians(b.target.longitude);

		double r = 6378.137; // 赤道半径

		return r * Math.acos(Math.sin(lata) * Math.sin(latb) + Math.cos(lata) * Math.cos(latb) * Math.cos(lngb - lnga));
	}

	// 地図の初期設定
	private void mapInit() {

		// 地図タイプ設定（1）
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		// 現在位置ボタンの表示（2）
		googleMap.setMyLocationEnabled(true);

		//渋谷駅の位置、ズーム設定（3）
		CameraPosition camerapos = new CameraPosition.Builder()
				.target(new LatLng(35.659301,139.698087)).zoom(15.5f).build();
		// 地図の中心を変更する（4）
		googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));

		centerPosition = camerapos;
		execMoyori();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	// 地図の中心位置を取得して、APIのURLを準備する
	public void execMoyori() {

		// 地図の中心位置の取得
		CameraPosition cameraPos = googleMap.getCameraPosition();

		Bundle bundle = new Bundle();
		// 緯度
		bundle.putString("y", Double.toString(cameraPos.target.latitude));
		// 経度
		bundle.putString("x", Double.toString(cameraPos.target.longitude));

		bundle.putString("moyori",
				"http://express.heartrails.com/api/json?method=getStations&");

		// LoaderManagerの初期化（1）
		getLoaderManager().restartLoader(0, bundle, this);

	}

	@Override
	public Loader<String> onCreateLoader(int id, Bundle bundle) {
		HttpAsyncLoader loader = null;

		switch (id) {
		case 0:
			// リクエストURLの組み立て
			String url = bundle.getString("moyori")
						+ "x=" + bundle.getString("x") + "&"
						+ "y=" + bundle.getString("y");

			loader = new HttpAsyncLoader(this, url);
			// Web APIにアクセスする(2)
			loader.forceLoad();
			break;

		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<String> loader, String body) {
//		body = "{\"response\":{\"store\":[{\"x\":139.698087,\"y\":35.659301,\"pointNames\":\"Ponta\",\"name\":\"ローソン上野店\"}]}}";
		
		//とりあえずファイルから読込みを行う
		Resources res = this.getResources();  
		InputStream is = res.openRawResource(R.raw.datalist);
        
		try {
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder linebff = new StringBuilder();
			linebff.append("{\"response\":{");
			linebff.append("\"store\":[");
			String line;
	        while ((line = br.readLine()) != null) {
	        	linebff.append(line);
	        }
	        linebff.append("]}}");
	        body = linebff.toString();
		} catch (Exception e) {
			
		}

		
		// APIの取得に失敗の場合
		if (body == null)
			return;

		switch (loader.getId()) {

		case 0:

			// APIの結果を解析する
			ParseStoreInfo parse = new ParseStoreInfo();
			parse.loadJson(body);

			// マーカーをいったん削除しておく
			googleMap.clear();
			pointMarkerMap.clear();

			// APIの結果をマーカーに反映する（2）
			for (StoreInfo e : parse.getStoreInfo()) {

				Marker marker = googleMap.addMarker(new MarkerOptions()
						.position(new LatLng(e.y, e.x))
						.title(e.name)
						.snippet(e.pointNames)
						.icon(BitmapDescriptorFactory
								.fromResource(getPin(e.pointNames)))); //(3)

				// マーカーと駅情報を保管しておく（4）
				pointMarkerMap.put(marker, e);
				//LocalDBにも格納
			}
			break;
		}
	}
	
	@Override
	public void onLoaderReset(Loader<String> arg0) {
		// TODO 自動生成されたメソッド・スタブ
	}

	
	public static int getPin(String pointName) {
		if (pointName.equals("Ponta")) {
			return R.drawable.pin_blue_c;
		} else if (pointName.equals("Tカード")) {
			return R.drawable.pin_green_c;
		} else if (pointName.equals("nanaco")) {
			return R.drawable.pin_magenta_c;
		} else if (pointName.equals("楽天カード")) {
			return R.drawable.pin_red_c;
		} else {
			return R.drawable.pin_yellow_c;
		}
		
	}
}

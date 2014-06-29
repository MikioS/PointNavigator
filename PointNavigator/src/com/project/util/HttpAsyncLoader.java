package com.project.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class HttpAsyncLoader extends AsyncTaskLoader<String> {

	private String url = null; // WebAPIのURL

	public HttpAsyncLoader(Context context, String url) {
		super(context);
		this.url = url;
	}

	@Override
	public String loadInBackground() {
		HttpClient httpClient = new DefaultHttpClient();
		
		try {
			String responseBody = httpClient.execute(new HttpGet(this.url),

				// UTF-8に対応した文字列を返すようにhandleResponseをオーバーライドする
				new ResponseHandler<String>() {

					@Override
					public String handleResponse(HttpResponse response)
						throws ClientProtocolException,	IOException {

						// レスポンスコードが、HttpStatus.SC_OK（HTTP 200）の場合のみ、結果を返す
						if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
							return EntityUtils.toString(response.getEntity(), "UTF-8");
						}
						return null;
					}
				});

			return responseBody;
		}
		catch (Exception e) {
			Log.e(this.getClass().getSimpleName(),e.getMessage());
		}
		finally {
			// 通信終了時は、接続を閉じる
			httpClient.getConnectionManager().shutdown();
		}
		return null;
	}
}

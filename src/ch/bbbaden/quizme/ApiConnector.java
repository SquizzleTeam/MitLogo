package ch.bbbaden.quizme;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

public class ApiConnector {
	public JSONArray setNewThema(String url){
			HttpEntity httpEntity = null;
		
		try{
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			
			httpEntity = httpResponse.getEntity();
		}
		catch(ClientProtocolException e){
			System.out.println(e);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		JSONArray jsonArray = null;
		
		if(httpEntity != null){
			try{
				String entityResponse = EntityUtils.toString(httpEntity);
				jsonArray = new JSONArray(entityResponse);
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		
		return jsonArray;
	}
}

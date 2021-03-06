/* Class for the Bing Search engine API  */

package com.team11.searchAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.team11.CommonConstants;
import com.team11.CommonUtilities.LogUtil;
import com.team11.Parser.ListFinderHTML;
import com.team11.Parser.WebList;
import com.team11.Parser.WebPage;


public class BingAPI implements CommonConstants {

	public static ArrayList<WebPage> bingSearch(ArrayList<String> seedList, int noOfResults, double overlapTolerance, String query)  {
		ArrayList<WebPage> listPages = new ArrayList<WebPage>();
		URL url;
		HttpURLConnection conn = null;
		BufferedReader br = null;
		ListFinderHTML myfinder = new ListFinderHTML();
		try {
			query = query.replaceAll(" ", "%20");
			byte[] accountKeyBytes = Base64.encodeBase64((CommonConstants.ankurBingKey + ":" + CommonConstants.ankurBingKey).getBytes());
			String accountKeyEnc = new String(accountKeyBytes);

			url = new URL("https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Web?Query=%27" + query + "%27&$top="+noOfResults+"&$format=json");

			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			StringBuilder sb = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JSONObject obj;

			obj = new JSONObject(sb.toString());
			JSONArray arr = obj.getJSONObject("d").getJSONArray("results");
			for (int i = 0; i < arr.length(); i++){
				String post_id = arr.getJSONObject(i).getString("Url");
				String description=arr.getJSONObject(i).getString("Description");		// Obtain all the urls for the given search query
				String title=arr.getJSONObject(i).getString("Title");
				myfinder.SetHTML(post_id);
				LogUtil.log.info("aaa "+title+" "+ post_id+" "+description);
				WebPage page = new WebPage(title, post_id,description);
				listPages.add(page);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listPages;
	}
}


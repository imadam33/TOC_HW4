/*
* 姓名：黃奕璁
* 學號：F74002191
*
* 程式執行方式： java -jar TocHw4.jar URL
* 輸出結果： 找出那一路的成交值的最大值和最小值
*/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.ArrayList;
import org.json.JSONArray;

public class TocHw4 {
	private String sentHttpRequest(String url) throws Exception{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String inputline = "";
		StringBuffer result = new StringBuffer();
		while((inputline = in.readLine()) != null)
			result.append(inputline + "\n");
		in.close();
		return result.toString();
	}
	
	public static void main(String args[]){
		TocHw4 test = new TocHw4();
		String url = args[0];
		String result;
		HashMap<String, Integer> roadName_count = new HashMap<String, Integer>();
		HashMap<String, String> roadName_Year = new HashMap<String, String>();
		HashMap<String, String> roadName_index = new HashMap<String, String>();
		int i;
		try{
			result = test.sentHttpRequest(url);
			JSONArray array = new JSONArray(result);
			Pattern pattern1 = Pattern.compile("(.*路|.*大道|.*街).*");
			for(i = 0; i < array.length(); i++){
				Matcher matcher1 = pattern1.matcher(array.getJSONObject(i).getString("土地區段位置或建物區門牌"));
				if(matcher1.matches()){
					if(roadName_count.containsKey(matcher1.group(1)) && !(roadName_Year.get(matcher1.group(1)).contains(String.valueOf(array.getJSONObject(i).getInt("交易年月"))))){
						int temp = roadName_count.get(matcher1.group(1)) + 1;
						String temp_year = String.valueOf(array.getJSONObject(i).getInt("交易年月"));
						roadName_count.put(matcher1.group(1), temp);
						roadName_Year.put(matcher1.group(1), roadName_Year.get(matcher1.group(1)) + temp_year + " ");
					}
					else if(!roadName_count.containsKey(matcher1.group(1))){
						roadName_count.put(matcher1.group(1), 1);
						roadName_Year.put(matcher1.group(1), String.valueOf(array.getJSONObject(i).getInt("交易年月")) + " ");
					}
					if(roadName_index.containsKey(matcher1.group(1)))
						roadName_index.put(matcher1.group(1), roadName_index.get(matcher1.group(1)) + String.valueOf(i) + " ");
					else
						roadName_index.put(matcher1.group(1), String.valueOf(i) + " ");
				}
				else{
					Pattern pattern2 = Pattern.compile("(.*巷).*");
					Matcher matcher2 = pattern2.matcher(array.getJSONObject(i).getString("土地區段位置或建物區門牌"));
					if(matcher2.matches()){
						if(roadName_count.containsKey(matcher2.group(1)) && !(roadName_Year.get(matcher2.group(1)).contains(String.valueOf(array.getJSONObject(i).getInt("交易年月"))))){
							int temp = roadName_count.get(matcher2.group(1)) + 1;
							String temp_year = String.valueOf(array.getJSONObject(i).getInt("交易年月"));
							roadName_count.put(matcher2.group(1), temp);
							roadName_Year.put(matcher2.group(1), roadName_Year.get(matcher2.group(1)) + temp_year + " ");
						}
						else if(!roadName_count.containsKey(matcher2.group(1))){
							roadName_count.put(matcher2.group(1), 1);
							roadName_Year.put(matcher2.group(1), String.valueOf(array.getJSONObject(i).getInt("交易年月")) + " ");
							roadName_index.put(matcher2.group(1), String.valueOf(i));
						}
						if(roadName_index.containsKey(matcher2.group(1)))
							roadName_index.put(matcher2.group(1), roadName_index.get(matcher2.group(1)) + String.valueOf(i) + " ");
						else
							roadName_index.put(matcher2.group(1), String.valueOf(i) + " ");
					}
				}
			}
			int max_count = 1;
			ArrayList<String> find_road = new ArrayList<String>();
			for(String road:roadName_count.keySet()){
				if(roadName_count.get(road) > max_count)
					max_count = roadName_count.get(road);
			}
			for(String road:roadName_count.keySet()){
				if(roadName_count.get(road) == max_count)
					find_road.add(road);
			}
			for(i = 0; i < find_road.size(); i++){
				String index[] = roadName_index.get(find_road.get(i)).split(" ");
				int price_max, price_min;
				price_max = array.getJSONObject(Integer.valueOf(index[0])).getInt("總價元");
				price_min = array.getJSONObject(Integer.valueOf(index[0])).getInt("總價元");
				for(int j = 0; j < index.length; j++){
					if(array.getJSONObject(Integer.valueOf(index[j])).getInt("總價元") > price_max){
						price_max = array.getJSONObject(Integer.valueOf(index[j])).getInt("總價元");
					}
					else if(array.getJSONObject(Integer.valueOf(index[j])).getInt("總價元") < price_min){
						price_min = array.getJSONObject(Integer.valueOf(index[j])).getInt("總價元");
					}
				}
				System.out.println(find_road.get(i) + ", 最高成交價: " + price_max + ", 最低成交價: " + price_min);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}

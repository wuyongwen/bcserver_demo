<%@ page import="java.net.*,java.util.*,java.io.*,java.lang.*"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="org.json.simple.parser.ParseException"%>
<%
	try{

			URL url = new URL("http://localhost:8080/api/notify/list-notify.action?token=a68db487-e72d-4068-820a-28c049347784&type=Friend");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
			StringBuffer msg = new StringBuffer();
			
		    String line;
			while ((line = in.readLine()) != null)
				msg.append(line);
			in.close();
			
			JSONParser parser = new JSONParser();
			JSONObject object = new JSONObject();		
			JSONArray postArray = new JSONArray();
	       	object = (JSONObject) parser.parse(msg.toString());
	       	postArray = (JSONArray) object.get("results");			
			
	       	if  (postArray.size() > 0)
	       		out.print("success");
	       	else
	        	out.print("fail");
       	
	}catch(Exception ex){
		ex.printStackTrace();		
		out.print("Exception");
	}
%>

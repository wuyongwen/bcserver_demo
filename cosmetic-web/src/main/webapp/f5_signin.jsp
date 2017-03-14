<%@ page import="java.net.*,java.util.*,java.io.*,java.lang.*"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="org.json.simple.parser.ParseException"%>
<%
	try{
		
			URL url = new URL("http://localhost:8080/api/user/sign-in-BC.action?email=clipelle917@gmail.com&password=1111111");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
			StringBuffer msg = new StringBuffer();
			
		    String line;
			while ((line = in.readLine()) != null)
				msg.append(line);
			in.close();
			
			JSONParser parser = new JSONParser();
			JSONObject object = new JSONObject();
			String userId = "";
	       	object = (JSONObject) parser.parse(msg.toString());
	       	object = (JSONObject) object.get("userInfo");
	       	userId = object.get("id").toString();
			
	       	if  (userId.equalsIgnoreCase("16001"))
	       		out.print("success");
	       	else {	       		
	       		out.print(msg.toString());
	       	}
       	
	}catch(Exception ex){
		ex.printStackTrace();		
		out.print("fail");
	}
%>

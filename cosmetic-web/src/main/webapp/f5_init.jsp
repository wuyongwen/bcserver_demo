<%@ page import="java.net.*,java.util.*,java.io.*,java.lang.*"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%
	try{
			URL url = new URL("http://localhost:8080/api/init");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer msg = new StringBuffer();				
		    String line;
			while ((line = in.readLine()) != null)
				msg.append(line);
			in.close();
			
			JSONParser parser = new JSONParser();
			JSONObject object = new JSONObject();		
			String domainUrl = "";
	       	object = (JSONObject) parser.parse(msg.toString());
	       	object = (JSONObject) object.get("message");
	       	domainUrl = object.get("domainUrl").toString();
	
	       	if  (!domainUrl.isEmpty())
				out.print("success");
	        else
	        	out.print("fail");
	       	
	}catch(Exception ex){
		out.print("Exception");
	}
%>

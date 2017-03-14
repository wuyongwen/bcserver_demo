<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="java.net.*"%>
<%
	String fileUrl = "http://cdn.beautycircle.com/1/38001/205/7bd93cfd-72f6-427e-a610-120478c9b4aa.jpg";
	String type = request.getParameter("type");
	if (type != null && type.equalsIgnoreCase("sku"))
		fileUrl = "http://cdn.perfectcorp.com/store/sku/MSR/PFA160126-0028/9/content.zip";

	BufferedInputStream in = null;
	FileOutputStream fout = null;
	try {
		long start = System.currentTimeMillis();
		in = new BufferedInputStream(new URL(fileUrl).openStream());
		long end = System.currentTimeMillis();
		long cost = end - start;
		if (cost > 800)
			out.println("Error = " + cost);
		else
			out.println("Success = " + cost);
	} catch (Exception e) {
		out.println("Exception = " + e.getMessage());
	} finally {
		if (in != null)
			in.close();
		if (fout != null)
			fout.close();
	}
%>
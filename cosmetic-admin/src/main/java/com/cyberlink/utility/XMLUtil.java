package com.cyberlink.utility;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class XMLUtil {
	
	private String xmldata;
	private DocumentBuilder dBuilder;
	public Document doc;
	
	public XMLUtil(String data) {
		xmldata = data;
		Parse();
	}

	public void Parse(){		
		try {
			dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();			
			doc = dBuilder.parse(new InputSource(new StringReader(xmldata)));
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	public String getCharacterDataFromElement(Element e) {
	    Node child = e.getFirstChild();
	    if (child instanceof CharacterData) {
	      CharacterData cd = (CharacterData) child;
	      return cd.getData();
	    }
	    return "";
	  }
	
	public String getNodeData(Element e) {
		String sResult = "";
	    Node child = e.getFirstChild();
	    if (child != null) {
	    	sResult = child.getNodeValue();
	    }	    
	    return sResult;
	  }
}

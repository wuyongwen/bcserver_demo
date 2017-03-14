package com.cyberlink.cosmetic.modules.gcm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
	@JsonProperty("TickerText")
	private String tickerText;
	@JsonProperty("Title")
	private String title;
	@JsonProperty("Msg")
	private String msg;
	@JsonProperty("Nid")
	private String nid;
	@JsonProperty("Link")
	private String link;
	@JsonProperty("MsgType")
	private String msgType;
	@JsonProperty("Ntype")
	private String ntype;

	public String getNtype() {
		return ntype;
	}

	public void setNtype(String ntype) {
		this.ntype = ntype;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTickerText() {
		return tickerText;
	}

	public void setTickerText(String tickerText) {
		this.tickerText = tickerText;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}
}
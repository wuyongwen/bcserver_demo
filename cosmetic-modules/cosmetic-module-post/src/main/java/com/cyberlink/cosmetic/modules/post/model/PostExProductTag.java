package com.cyberlink.cosmetic.modules.post.model;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PostExProductTag {

	@JsonView(Views.Public.class)
	public Object tagInfo;

	public PostExProductTag() {
		this.tagInfo = null;
	}

	public PostExProductTag(String tagInfo) {
		tagInfo = StringEscapeUtils.unescapeJava(tagInfo);
		if (tagInfo.startsWith("\""))
			tagInfo = tagInfo.substring(1);
		if (tagInfo.endsWith("\""))
			tagInfo = tagInfo.substring(0, tagInfo.length() - 1);

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readValue(tagInfo, JsonNode.class);
			this.tagInfo = mapper.writer((PrettyPrinter) null).writeValueAsString(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
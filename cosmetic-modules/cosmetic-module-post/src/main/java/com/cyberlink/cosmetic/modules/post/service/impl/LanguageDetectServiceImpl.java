package com.cyberlink.cosmetic.modules.post.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cyberlink.cosmetic.modules.post.service.LanguageDetectService;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

public class LanguageDetectServiceImpl implements LanguageDetectService {
	LanguageProfileReader profileReader;
	LanguageDetector languageDetector;
	TextObjectFactory textObjectFactory;
	
	public LanguageProfileReader getProfileReader() {
		return profileReader;
	}
	
	public void setProfileReader(LanguageProfileReader profileReader) {
		this.profileReader = profileReader;
		try {
			languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
		            .withProfiles(new LanguageProfileReader().readAll())
		            .build();
			textObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<DetectedLanguage> getProbabilities(String text) {
		if (textObjectFactory == null || languageDetector == null)
			return new ArrayList<DetectedLanguage>();
    	TextObject textObject = textObjectFactory.forText(text);
    	return languageDetector.getProbabilities(textObject);
	}

}

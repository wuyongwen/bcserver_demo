package com.cyberlink.cosmetic.modules.post.service;

import java.util.List;

import com.optimaize.langdetect.DetectedLanguage;

public interface LanguageDetectService {
	List<DetectedLanguage> getProbabilities(String text);
}

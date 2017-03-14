package com.cyberlink.cosmetic.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.cyberlink.cosmetic.utils.ahoCorasick.AhoCorasickDoubleArrayTrie;



public class DetectForbiddenWordUtils {

	private static AhoCorasickDoubleArrayTrie<String> acdatForbiddenWord = null;
	
    public static Boolean hasForbiddenWord(String content){
    	try {
    		if(acdatForbiddenWord == null){
    			Set<String> forbiddenWordSet  = loadDictionary("ahoCorasick/forbiddenWord.txt");
    			Map<String, String> map = new TreeMap<String, String>();
		        for (String key : forbiddenWordSet)
		        {
		            map.put(key, key);
		        }
		        // Build an AhoCorasickDoubleArrayTrie
		        acdatForbiddenWord = new AhoCorasickDoubleArrayTrie<String>();
		        acdatForbiddenWord.build(map);
    		}
		} catch (IOException e) {
			return false;
		}
    	
		return acdatForbiddenWord.hasKeyword(content, new AhoCorasickDoubleArrayTrie.IHit<String>()
		{
		    @Override
		    public Boolean isHit(int begin, int end, String value)
		    {
		    	return Boolean.TRUE;
		    }

			@Override
			public void hit(int begin, int end, String value) {}
		});
    }
		
    private static Set<String> loadDictionary(String fileName) throws IOException
    {
    	Set<String> dictionary = new TreeSet<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null)
        {
            dictionary.add(line);
        }
        br.close();
        return dictionary;
    }
}

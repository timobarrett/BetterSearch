package com.ware.fivetwentysix.bettersearch2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/*
  object stores the text from a site
 */
public class Site {
    private ArrayList<String> siteTxt = new ArrayList<>();
    private HashMap<String,Integer>sentenceCount = new HashMap<>();
    private String url;
    private static final Pattern END_SENTENCE = Pattern.compile("\\p{Punct}");
    private static final Pattern END_SENTENCE2 = Pattern.compile("[.!?\\-]");
    private static final Pattern DONT_WANT = Pattern.compile("[^)/(]");

    public Site(String url) {
        this.url = url;
    }

    /**
     * get count of sentences on site
     * @return - count
     */
    public int getSentenceCount(){
        return siteTxt.size();
    }

    /**
     * returns this site's url
     * @return - url
     */
    public String getSiteUrl(){
        return url;
    }

    /**
     * returns count of search word
     * @param searchStr - search word
     * @return - count of occurances
     */
    public int getSearchWordCnt(String searchStr){
//        System.out.println("GetSearchWordCnt - URL = "+url + " SearchStr = "+searchStr);
        if (sentenceCount.containsKey(searchStr)) {
            return sentenceCount.get(searchStr);
        }
        return 0;
    }

    /**
     * Text to be processed for sentences
     * @param text - site text blob
     */
    public void addSiteText(String text) {
        processSentences(text);
//        System.out.println("TEXT2 = " + text);
//        dumpSentences();
    }

    /**
     * break text into sentences and store
     * @param text - swap this for openNlp?
     */
    private void processSentences(String text){
        for (String sentence:END_SENTENCE2.split(text)) {
            siteTxt.add(sentence);
        }
    }

    /**
     *
     * @param searchStr - search string
     * @return - sentences containing search string
     */
    public ArrayList<String> foundStrings(String searchStr){
        ArrayList<String> results = new ArrayList<>();
        for (String sentence:siteTxt){
            if (sentence.toLowerCase().contains(searchStr.toLowerCase())){
                if(sentence.indexOf(" ") != sentence.lastIndexOf(" ") &&
                        !sentence.contains("/")) {
                    results.add(sentence);
                }
            }
        }
        sentenceCount.put(searchStr,results.size());
        removeReportedStrings(results);
        return results;
    }

    /**
     * Remove sentences once reported to avoid dupolication
     * @param text - to remove
     */
    private void removeReportedStrings(ArrayList<String>text){
        for (String str: text){
            siteTxt.remove(str);
        }
    }

    /**
     * TODO - method to evaluate processing
     */
    private void dumpSentences(){
        for (String sentence:siteTxt){
            System.out.println("TEXT = "+sentence);
        }
    }
}

package com.ware.fivetwentysix.bettersearch2;

import opennlp.tools.stemmer.PorterStemmer;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleProcessText {

    private static final Logger Log = Logger.getLogger( com.ware.fivetwentysix.bettersearch2.SimpleProcessText.class);
    //TODO - look for a sklearn like source for these values
    private String[] cwords = {"for", "the", "be", "as", "an", "you", "this", "i", "we", "of", "or", "me", "than", "that", "if", "is", "it", "in", "do", "also", "their", "when", "was", "too", "can", "are", "where", "until", "any", "and",
            "already", "always", "all", "choose", "therefore", "its", "from", "itself", "just", "some", "you", "their", "put", "has", "your", "we", "know", "to", "so", "that", "it", "is", "in", "if", "do", "by", "into", "her", "his",
            "out", "most", "pm", "she", "he", "who", "what", "off", "even", "only", "no", "says", "am", "search", "one", "good", "other", "very", "see", "then", "first", "amazon","facebook","twitter","pinterest",
            "be", "will", "not", "they", "at", "on", "but", "may", "get", "our", "my", "up", "use", "like", "a", "have", "with", "more", "them", "how", "about", "help", "new", "which", "us", "kindle", "prime"};
    private HashSet<String> stopWords = new HashSet<String>(Arrays.asList(cwords));

    private HashMap<String, Integer> mNonCommonStrings = new HashMap<String, Integer>();
    private HashSet<String> mSearchURLs = new HashSet<>();

    private BaseStemmer mBaseStemmer;
    private PorterStemmer pStemmer = new PorterStemmer();

    public SimpleProcessText() {
        mBaseStemmer = new BaseStemmer();
    }

    /**
     * add search words to stopwords to better weight the results
     * @param words - search words
     */
    public void addCommonTerms(ArrayList<String> words) {
        Log.info("SimpleProcessText - addCommonTerms");
        for (String word : words) {
            stopWords.add(word);
        }
    }

    public boolean isStopWord(String word){
        return stopWords.contains(word);
    }


    /**
     * add urls searched to list
     * @param searchUrl - url to add
     * @return success or fail
     */
    public boolean addSearchUrls(String searchUrl) {
        Log.info("SimpleProcessText - addSearchUrls");
        return mSearchURLs.add(searchUrl);
    }

    /**
     * parse the text collecting non stop words
     * @param text - text from website document
     */
    protected void mineTextForWords(String text) {
        Log.info("SimpleProcessText - mineTextForWords");
        String[] words = text.toLowerCase().split(" ");
        for (String word : words) {
//            String stemWord = mBaseStemmer.getStem(word);
            String stemWord = pStemmer.stem(word);
//            System.out.println("PORTERSTEMMER ="+pStemmer.stem(word));
            if (mNonCommonStrings.containsKey(stemWord)) {
                mNonCommonStrings.put(stemWord, mNonCommonStrings.get(stemWord) + 1);
            } else if (stemWord.chars().allMatch(Character::isAlphabetic) &&
                    !stopWords.contains(word) && !stopWords.contains(stemWord)) { //checking if stemmed word is stop words blocks site load in browser
                mNonCommonStrings.put(stemWord, 1);
            }
        }
    }

    /**
     * Makes sure that the text collected is a url
     * @return - set of valid urls
     */
    public HashSet postProcessResults(){
        Log.info("SimpleProcessText - postProcessResults");
        Iterator<String> urlsFound = mSearchURLs.iterator();
        ArrayList<String> mVals = new ArrayList<>();
        while(urlsFound.hasNext()){
            String urlString = urlsFound.next();
            if(urlString.indexOf("http")!=-1){
                urlsFound.remove();
//                System.out.println("STRING = "+url + " index ="+url.indexOf("http"));
                if(urlString.indexOf("http")!=-1) {
                    mVals.add(urlString.substring(urlString.indexOf("http"), urlString.length()));
                }
            }
        }
        mSearchURLs.addAll(mVals);
        return mSearchURLs;
    }

    /**
     * gets set of non common words
     * @return map of non commoin words and their occurance count
     */

    public HashMap<String, Integer>  getSortedNonCommonWords(){
        Log.info("SimpleProcessText - getSortedNonCommonWords");
        return mNonCommonStrings.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public boolean isNonCommonWord(String word){
        return mNonCommonStrings.containsKey(word);
//        boolean status = false;
//        if(mNonCommonStrings.containsKey(word)){
//            status = true;
//        }
//        return status;
    }

}

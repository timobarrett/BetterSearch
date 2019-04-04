package com.ware.fivetwentysix.bettersearch2;

import opennlp.tools.stemmer.PorterStemmer;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleProcessText {

//TODO - look for a sklearn like source for these values
    private String[] cwords = {"for", "the", "be", "as", "an", "you", "this", "i", "we", "of", "or", "me", "than", "that", "if", "is", "it", "in", "do", "also", "their", "when", "was", "too", "can", "are", "where", "until", "any", "and",
            "already", "always", "all", "choose", "therefore", "its", "from", "itself", "just", "some", "you", "their", "put", "has", "your", "we", "know", "to", "so", "that", "it", "is", "in", "if", "do", "by", "into", "her", "his",
            "out", "most", "pm", "she", "he", "who", "what", "off", "even", "only", "no", "says", "am", "search", "one", "good", "other", "very", "see", "then", "first", "amazon",
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
        for (String word : words) {
            stopWords.add(word);
        }
    }

    /**
     * add urls searched to list
     * @param searchUrl - url to add
     * @return success or fail
     */
    public boolean addSearchUrls(String searchUrl) {
        return mSearchURLs.add(searchUrl);
    }

    /**
     * parse the text collecting non stop words
     * @param text - text from website document
     */
    protected void mineTextForWords(String text) {
        String[] words = text.toLowerCase().split(" ");
        for (String word : words) {
//            String stemWord = mBaseStemmer.getStem(word);
            String stemWord = pStemmer.stem(word);
            System.out.println("PORTERSTEMMER ="+pStemmer.stem(word));
            if (mNonCommonStrings.containsKey(stemWord)) {
                mNonCommonStrings.put(stemWord, mNonCommonStrings.get(stemWord) + 1);
            } else if (stemWord.chars().allMatch(Character::isAlphabetic) &&
                    !stopWords.contains(stemWord)) {
                mNonCommonStrings.put(stemWord, 1);
            }
        }
    }

    /**
     * Makes sure that the text collected is a url
     * @return - set of valid urls
     */
    public HashSet postProcessResults(){
        Iterator<String> urlsFound = mSearchURLs.iterator();
        ArrayList<String> mVals = new ArrayList<>();
        while(urlsFound.hasNext()){
            String urlString = urlsFound.next();
            if(urlString.indexOf("http")!=0){
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
        return mNonCommonStrings.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

}

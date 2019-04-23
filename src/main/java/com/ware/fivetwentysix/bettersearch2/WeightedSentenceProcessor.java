package com.ware.fivetwentysix.bettersearch2;

import com.ware.fivetwentysix.bettersearch2.com.ware.fivetwentysix.bettersearch2.nlp.SimilarityProcessor;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.BreakIterator;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

public class WeightedSentenceProcessor {
    private static final Logger Log = Logger.getLogger( com.ware.fivetwentysix.bettersearch2.WeightedSentenceProcessor.class);
    private ReportWriter mReportWriter;
    private SimpleProcessText mSimpleProcessText;
    private HashMap<String,Integer>mSortedNonCommonWords;
    private HashMap<String,Integer>mWeightedContent;
    private HashMap<Float, String>mWeightedSentences;
    private SimilarityProcessor mSimilarityProcessor;

    private InputStream modelIn = null;
    private SentenceModel model = null;
    private SentenceDetectorME sentenceDetectorME = null;
    /**
     * Constructor
     * @param reportWriter - instantiated class - single instance
     * @param processText - instantiated class - single instance
     */
    public WeightedSentenceProcessor(ReportWriter reportWriter, SimpleProcessText processText){
        mReportWriter = reportWriter;
        mSimpleProcessText = processText;
        mSimilarityProcessor = new SimilarityProcessor();
        mWeightedContent = new HashMap<>();
        initializeNLPSentenceDetect();
    }

    /**
     * Entry point for class function - goal is to weight and report content
     * @param sortedWords - collected during searchProcessor functions
     * @param siteList - site information collected
     */
    protected void mlTextCollected(HashMap<String,Integer>sortedWords,ArrayList<Site> siteList){
        Log.info("WeightedSentenceProcessor - mlTextCollected");

        if (null!=sortedWords){
            mSortedNonCommonWords = new HashMap<String,Integer>(sortedWords);
        }
        ArrayList<Site>mSiteList = new ArrayList<>(siteList);
        ArrayList<String>allTextCollected = new ArrayList<>();
        for (Map.Entry<String,Integer> entry : mSortedNonCommonWords.entrySet()){
            HashMap<String, ArrayList<String>> textCollected = new HashMap<>();
            for (Site site:mSiteList) {
                ArrayList<String> siteText = site.foundStrings(entry.getKey());
//                textCollected.put(entry.getKey(),siteText);
//                for (String text : siteText){
//                    allTextCollected.add(text);
//                }
//                List<String>foo = SimilarityProcessor.removeDuplicatesFromQueries(allTextCollected);

                int t=1;
            }
            walkText(textCollected);
        }
        List<String>reducedText = SimilarityProcessor.removeDuplicatesFromQueries(allTextCollected);
        LinkedHashMap<String,Integer> mSortedWeighted = mWeightedContent.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        System.out.println("Doo");
//        mReportWriter.printWeightedResults(mSortedWeighted);

    }

    private void walkText(HashMap<String,ArrayList<String>>allText) {
//        Log.info("SearchProcessor - walkText");

        for (String keyWord : allText.keySet()) {
            for (String sentence : allText.get(keyWord)){
                reduceTextBest(sentence);            }

            System.out.println("A");
        }

    }

    private String reduceTextBest(String sentence){
        Log.debug("WeightedSentenceProcessor - reduceStringBetter2");
        PorterStemmer stemmer = new PorterStemmer();
        StringBuffer resultBuffer = new StringBuffer();
        int nonCommonCount = 0;

        Pattern pattern = Pattern.compile("[a-zA-X0-9]");
        String[] words = sentence.split(" ");
        for (String word:words){
            String pWord = wordClean(word);
            if (mSimpleProcessText.isStopWord(pWord.toLowerCase()) || !pattern.matcher(pWord).find()) {
                continue;
            }else{
                String stemmed = stemmer.stem(pWord);
                if (mSimpleProcessText.isNonCommonWord(stemmed)){nonCommonCount++;}
                resultBuffer.append(stemmed+" ");
            }
        }
        Log.info("Start = "+sentence+" end = "+resultBuffer.toString());
        if (nonCommonCount != 0) {
            mWeightedContent.put(sentence, calculateSentenceWeight(resultBuffer.toString()));
        }
        return resultBuffer.toString();
    }

    /**
     * Needed to clean punctuation from word - TODO more work to clean other artifacts
     * @param word - word to be processed
     * @return - cleaned up word
     */
    private String wordClean(String word){
        String cleaned = null;
        if (word.contains(".")){ cleaned = word.replace(".","");}
        else if (word.contains(",")){cleaned = word.replace(",","");}
        else{ cleaned = word;}
        return cleaned;
    }


    /**
     * TODO - needs review - functions replaced by reduceTextBest Method
      * @param sentence - string to process
     * @return - return non stopword sentence
     */
    private String reduceText(String sentence){
        Log.info("WeightedSentenceProcessor - reduceText");
        PorterStemmer stemmer = new PorterStemmer();
        BreakIterator wrdItr = BreakIterator.getWordInstance(Locale.US);
        int wrdStrt = 0;
        StringBuffer buffer = new StringBuffer();
        wrdItr.setText(sentence);
        for(int wrdEnd = wrdItr.next(); wrdEnd != BreakIterator.DONE;
            wrdStrt = wrdEnd, wrdEnd = wrdItr.next())
        {
//            String word = this.getStringVal().substring(wrdStrt, wrdEnd);//words[i].trim();
            String word = sentence.substring(wrdStrt,wrdEnd);
            word.replaceAll("\"|'","");

            //Skip stop words and stem the word..
            if(mSimpleProcessText.isStopWord(word)) continue;
            stemmer.stem(word);
            buffer.append(stemmer.toString());
            buffer.append(" ");
        }
        mWeightedContent.put(sentence,calculateSentenceWeight(buffer.toString()));
        // TODO Auto-generated method stub
        return buffer.toString();
    }

    /**
     * calculate the weight of the sentence by totaling the non common word frequency
     * TODO - this does not function when long sentences encounted
     * @param sentence - of stopwords only
     * @return sum of common words in corpus
     */
    private int calculateSentenceWeight(String sentence){
        Log.info("WeightedSentenceProcessor - calculateSentenceWeight");
        Log.info("Sentence = "+sentence);
        String[] words = sentence.split(" ");
        int weight = 0;
        for (String word: words){
            try {
                Integer value = mSortedNonCommonWords.get(word.toLowerCase());
                if (value!=null) {
                    weight += value;
                }
            }catch(NullPointerException e){ Log.error("Calculate sentence weight = "+word);}
        }
        Log.debug("Sentence = " + sentence+"Weight = "+weight);
        return weight;
    }
    private void initializeNLPSentenceDetect(){
        Log.info("Site - initializeNLPSentenceDetect");
        try {
            modelIn = new FileInputStream("en-sent.bin");
        }catch(FileNotFoundException e){System.out.println("en-sent.bin not found");}
        try {
            model = new SentenceModel(modelIn);
        } catch(IOException e){System.out.println("Ereror setting sentencemodel");}
        sentenceDetectorME = new SentenceDetectorME(model);
    }

    protected String bestWeightedSentenceByTopic(ArrayList<String>sentences){
        mWeightedSentences = new LinkedHashMap<>();
        int nonCommonCount = 0;
        float sentenceWeight = 0f;
        for (String sentence : sentences){
            String[] tokens = sentenceDetectorME.sentDetect(sentence);
            for(String word:tokens){
                if (!mSimpleProcessText.isStopWord(word)){
                    nonCommonCount++;
                }
                if (nonCommonCount!=0) {
                    sentenceWeight = (float)nonCommonCount / (float)sentence.length();
                }
            }
            if(sentenceWeight!=0) {
                mWeightedSentences.put(sentenceWeight, sentence);
            }
        }
        String resultSentence = null;
        mWeightedSentences = mWeightedSentences.entrySet().stream()
                .sorted(Map.Entry.<Float,String>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1, e2) -> e1, LinkedHashMap::new));
        if (mWeightedSentences.keySet().iterator().hasNext()) {
            Object key = mWeightedSentences.keySet().iterator().next();
            resultSentence =mWeightedSentences.get(key);
        }
        return resultSentence;
    }
}

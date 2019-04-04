package com.ware.fivetwentysix.bettersearch2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;


public class SearchProcessor {
    private Document mUrlDoc;
    private ArrayList<String>mSearchTerms;
    private ArrayList<Site> mSiteList;
    private static final String staticString2 ="https://www.google.com/search?q=tomato+grow&aqs=chrome.0.69i59j0j69i57j0l3.8531j0j7&sourceid=chrome&ie=UTF-8";
    private String mQueryString;
    private HashSet<String>mProcessedUrls;
    private HashMap<String, Integer> mSortedNonCommonWords = new HashMap<>();

    private SimpleProcessText mSimpleProcessText;
    private ReportWriter mReportWriter;
    private HashMap<String, Integer> mBestSite = new HashMap<>();
    private HashMap<String, Integer> mBestSiteSorted = new HashMap<>();

    public SearchProcessor(ArrayList args){
        mSearchTerms = new ArrayList<>(args);
        mSiteList = new ArrayList<>();
        mSimpleProcessText = new SimpleProcessText();
        mSimpleProcessText.addCommonTerms(mSearchTerms);
    }

    public void ProcessQueryRequest(){
        buildQuery();
        if(performInitialQuery()){
            if(collectLinks()!=0){
                processSites();
                processReport();
                calcBestWebsite();
            }
        }
    }
    private void buildQuery(){
        if(mSearchTerms.size() == 0){
        mQueryString = staticString2;
        }else {
            mQueryString = Utility.buildInitialQuery(mSearchTerms);
            System.out.println("Search = " + mQueryString);
        }
    }
//        if(searchProcessor.performInitialQuery(q2String)) {
//            if (searchProcessor.collectLinks(argsStr) > 0) {
//                searchProcessor.processSites();
////                searchProcessor.processReport();
//            }
//            searchProcessor.performWebsiteCalc();
//            urlResults = searchProcessor.reportTop3Sites();
//            popBrowsers(urlResults);
//        }

    /**
     * jsoup the search results and load the document
     * @return true if document loaded, false if error
     *
     */
    private boolean performInitialQuery(){
        try{
            mUrlDoc = Jsoup.connect(mQueryString).timeout(30*1000).get();
        }catch(IOException e){
            System.out.println("Error searching using terms "+mQueryString+ " - "+ e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    /**
     * This populates the hashmap with utls found to be searched
     */
    public int collectLinks(){
       HashSet<String>searchTerms = new HashSet<>();
       searchTerms.addAll(mSearchTerms);

        Elements elements = mUrlDoc.select("a");
        for (Element wl : elements) {
//            System.out.println(wl.absUrl("href"));
            String url = wl.absUrl("href");
            if(!url.toLowerCase().contains("bing")&&
                    !url.toLowerCase().contains("google")) {
                for (String val : searchTerms) {
                    if (url.contains(val)) {
                        if (mSimpleProcessText.addSearchUrls(url)) {
                            System.out.println("MATCH = " + wl.absUrl("href"));
                            break;
                        }
                    }
                }
            }
        }
        mProcessedUrls = new HashSet<String>(mSimpleProcessText.postProcessResults());
        return mProcessedUrls.size();

    }

    /**
     * process urls returned in initial search mining for non stopword text
     */
    public void processSites(){
        Document siteDoc = null;
        Iterator<String> urlsFound = mProcessedUrls.iterator();
        String docString;
        while (urlsFound.hasNext()){
            String url = urlsFound.next();
            try{
                siteDoc = Jsoup.connect(url).timeout(30*10000).get();
            }catch(IOException e){
                System.out.println("Error processing - "+e.getLocalizedMessage());
            }
            try {
                Site site = new Site(url);
                mSiteList.add(site);
                try {
                    docString = Jsoup.parse(siteDoc.toString()).text();
                    site.addSiteText(docString);
                    mSimpleProcessText.mineTextForWords(docString);
                    System.out.println("SITE = "+ site.getSiteUrl()+" Sentence Count = " + site.getSentenceCount());
                } catch (NullPointerException e) {
                    System.out.println("Null Pointer error processing site - " + url);
                }
            }catch(NoSuchElementException e){System.out.println("No such Element Exception");}
        }
    }

    /**
     * Work in progress.  Could a text file be generated that contains all info
     *                    needed eliminating the need for the browser
     */
    private void processReport() {
        mReportWriter = new ReportWriter();
        HashSet<String> foundSentences = new HashSet<>();
        mSortedNonCommonWords = mSimpleProcessText.getSortedNonCommonWords();
        if (mReportWriter.housekeepingOpen("SearchResults.txt") != -1) {
            for (Map.Entry<String, Integer> entry : mSortedNonCommonWords.entrySet()) {
                mReportWriter.writeStatsFile(entry.getKey() + " - " + entry.getValue(), "\r\n");
//                System.out.println("Word = "+entry.getKey() + " Count = "+ entry.getValue());
                if (entry.getValue() > 60) {
                    mReportWriter.writeSectionHdr(entry.getKey());
                    for (Site site : mSiteList) {
                        ArrayList<String> occurances = site.foundStrings(entry.getKey());
//                        if(occurances.size()>0){
                        foundSentences.addAll(occurances);
                        mReportWriter.writeReport(foundSentences);
                        foundSentences.clear();
//                        }
                    }
                }
            }
        }
    }

    /**
     * Calculate sum of word occurance count / size of sorted common words
     * todo - review
     */
    private void calcBestWebsite(){
        int highestCount = 0;
        String hUrl = "";
        int avgValue = mSortedNonCommonWords.values().stream().mapToInt(Integer::intValue).sum()/mSortedNonCommonWords.size();
        System.out.println("AVERAGE = "+avgValue);
        int minVal = (int)(mSortedNonCommonWords.entrySet().stream().findFirst().get().getValue()*.49);
        for(Map.Entry<String, Integer> cword: mSortedNonCommonWords.entrySet()){
            if (cword.getValue()>avgValue) {
                for (Site site : mSiteList) {
                    int cnt = site.getSearchWordCnt(cword.getKey());
                    if (cnt > highestCount) {
                        highestCount = cnt;
                        hUrl = site.getSiteUrl();
                    }
                }
            }
            if (mBestSite.containsKey(hUrl)){
                mBestSite.put(hUrl, mBestSite.get(hUrl)+highestCount);
            }else {
                mBestSite.put(hUrl, highestCount);
            }
            highestCount = 0;
            hUrl = "";
        }
    }

    /**
     * find the top 3 websites
     * @return list of top 3 sites
     */
    public ArrayList<String> reportTop3Sites(){
        int i=0;
        ArrayList<String> results = new ArrayList<String>();
        mBestSiteSorted = mBestSite.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(e1, e2) -> e1, LinkedHashMap::new));
        for (Map.Entry<String,Integer> url:mBestSiteSorted.entrySet()){
            if (url.getValue() != 0) {
//                System.out.println("SITE = " + url.getKey() + " COUNT = " + url.getValue());
                results.add(url.getKey());
                i++;
            }
            if (i>2){break;}
        }
        return results;
    }

}

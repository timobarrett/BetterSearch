package com.ware.fivetwentysix.bettersearch2;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.NoSuchElementException;

public class SiteWorkerThread implements Runnable {
    private static final Logger Log = Logger.getLogger( com.ware.fivetwentysix.bettersearch2.SiteWorkerThread.class);
    private String mUrl;
    private SimpleProcessText mSimpleProcessText;
    private Object mSiteList;
    Site mSite;
    public SiteWorkerThread(String url,Site site, SimpleProcessText simpleProcessText){
        mUrl = url;
        mSite = site;
        mSimpleProcessText = simpleProcessText;
    }

    public void run(){
        Log.info("SiteWorkerThread - run");
        Document siteDoc = null;
        long totalDurJsoup = 0L;
        long totalDuration = 0L;
        long t1 = System.currentTimeMillis();
        try{
            siteDoc = Jsoup.connect(mUrl).timeout(30*5000).get();
            System.out.println("DUR = "+(System.currentTimeMillis()- t1));
            totalDurJsoup += System.currentTimeMillis() - t1;
        }catch(IOException e){
            System.out.println("Error processing - "+e.getLocalizedMessage());
        }
        try {

            try {
                long startTime = System.currentTimeMillis();
                String docString = Jsoup.parse(siteDoc.toString()).text();
                mSite.addSiteText(docString);
//                System.out.println("URL="+mUrl+" Text size = "+docString.length());
                if (docString.length()>1) {
                    mSimpleProcessText.mineTextForWords(docString);
                }
                System.out.println("SITE = "+ mSite.getSiteUrl()+" Sentence Count = " + mSite.getSentenceCount()+"DUR = "+(System.currentTimeMillis() - startTime));
                totalDuration += System.currentTimeMillis() - t1;
                System.out.println("Total JSoup Connect = "+totalDuration + " Total Duration = "+totalDurJsoup);
            } catch (NullPointerException e) {
                System.out.println("Null Pointer error processing site - " + mUrl);
            }
        }catch(NoSuchElementException e){System.out.println("No such Element Exception");}
    }
}

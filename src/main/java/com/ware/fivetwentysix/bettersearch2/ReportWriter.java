package com.ware.fivetwentysix.bettersearch2;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.function.Function;
/*
    Report on the sentences processed from the document
    Provides information on stemmed word frequency in FindStats.txt
 */

public class ReportWriter {
    private Writer mWriter = null;
    private Writer mWriter2 = null;
    private Writer mWriter3 = null;
    private String mFilename;
    private String mWordStats = "FindStats.txt";
   private String mWeighted = "WeightedResults.txt";
   private final int MAX_LINE_WIDTH = 210;


    public ReportWriter(){}
    private static final Logger Log = Logger.getLogger( com.ware.fivetwentysix.bettersearch2.ReportWriter.class);
    /**
     * Opens the files for output
     *  1. is the text file containing sentences containing the "non Common" word
     *  2. is the text file containing frequency of stemmed words collected
     * @param filename - TODO maybe this should just be hardcoded
     * @return - error or success
     */
    public int housekeepingOpen(String filename){
        int status = 1;
        mFilename = filename;
        Log.info("ReportWriter - housekeepingOpen");
        try{
            mWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"utf-8"));
        }catch(UnsupportedEncodingException | FileNotFoundException e){
            status = -1;
            System.out.println("File Exception - report file = "+e.getLocalizedMessage());
        }
        try{
            mWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mWordStats),"utf-8"));
        }catch(UnsupportedEncodingException | FileNotFoundException e){
            status = -1;
            System.out.println("File Exception - stats file = "+e.getLocalizedMessage());
        }
        try{
            mWriter3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mWeighted),"utf-8"));
        }catch(UnsupportedEncodingException | FileNotFoundException e) {
            status = -1;
            Log.error("File Exception - weighted file = " + e.getStackTrace());
        }
        return status;
    }

    public int printWeightedResults(HashSet<String> bestWeighted){
        int status = 1;
        Log.info("ReportWriter - printedWeightedResults ");
        bestWeighted.stream().forEach((s) -> writeWeighted(s));
        return status;
    }

    private int writeWeighted(String sentence){
        int status = 1;
        try{
            if (sentence!=null && sentence.length()>= MAX_LINE_WIDTH){
                String[] sentence2 = multiLine(sentence).split("\r\n");
                for (String text : sentence2){
                    mWriter3.write(text+"\r\n");
                    mWriter.flush();
                }
            }
            else if (sentence != null) {
                mWriter3.write(sentence+"\r\n");
                mWriter3.flush();
            }
        }catch(IOException e){
            Log.error("Error writing weighted = "+e.getStackTrace());
            status = -1;
        }
        return status;
    }
    /* return Arrays.stream(longString.split(splitter))
            .collect(
                ArrayList<String>::new,
                (l, s) -> {
                    Function<ArrayList<String>, Integer> id = list -> list.size() - 1;
                    if(l.size() == 0 || (l.get(id.apply(l)).length() != 0 && l.get(id.apply(l)).length() + s.length() >= maxLength)) l.add("");
                    l.set(id.apply(l), l.get(id.apply(l)) + (l.get(id.apply(l)).length() == 0 ? "" : splitter) + s);
                },
                (l1, l2) -> l1.addAll(l2))
            .stream().reduce((s1, s2) -> s1 + "\n" + s2).get();*/

    private String multiLine(String longString) {
        return Arrays.stream(longString.split(" "))
                .collect(
                        ArrayList<String>::new,
                        (l, s) -> {
                            Function<ArrayList<String>, Integer> id = list -> list.size() - 1;
                            if(l.size() == 0 || (l.get(id.apply(l)).length() != 0 && l.get(id.apply(l)).length() + s.length() >= MAX_LINE_WIDTH)) l.add("");
                            l.set(id.apply(l), l.get(id.apply(l)) + (l.get(id.apply(l)).length() == 0 ? "" : " ") + s);
                        },
                        (l1, l2) -> l1.addAll(l2))
                .stream().reduce((s1, s2) -> s1 + " \r\n" + s2).get();
    }
    /**
     * write the text passed complete with seperator
     * @param text - text to be printed
     * @param seperator - line terminator
     * @return success or fail
     */
    public int writeReportFile(String text, String seperator){
        int status = 1;
//        Log.info("ReportWriter - writeReportFile");
        try{
         //   text.trim();
            if (text.length() > 100){
                return -1;
             //   outputLonLine(text,seperator);
            }else {
                mWriter.write("\t" + text);
                mWriter.write(seperator);
                mWriter.flush();
            }
        }
        catch (IOException io){
            System.out.println(io.getStackTrace());
            status = -1;
        }
        return status;
    }



    /**
     * Write a header to the text file
     * @param header - String of header text
     */
    protected void writeSectionHdr(String header){
        Log.info( "ReportWriter - writeSectionHdr");
        writeReportFile("\r\n"+header,"\r\n");
        writeReportFile("----------------------------","\r\n");
    }

    /**
     * write report
     * @param textFound - set of text
     *                  TODO - is this needed
     */
    protected void writeReport(HashSet<String> textFound){
//        Log.info("ReportWriter - writeReport");
        for (String text:textFound) {
            writeReportFile(text, "\r\n");
        }
    }

    /**
     * Writes the non common words collected and their instance count
     * @param text - non common word
     * @param seperator - line feed
     * @return status
     */
    public int writeStatsFile(String text, String seperator) {
        int status = 1;
//        Log.info("ReportWriter - writeStatsFile");
        try {
            //   text.trim();
            if (text.length() > 100) {
                return -1;
                //   outputLonLine(text,seperator);
            } else {
                mWriter2.write("\t" + text);
                mWriter2.write(seperator);
                mWriter2.flush();
            }
        } catch (IOException io) {
            System.out.println(io.getStackTrace());
            status = -1;
        }
        return status;
    }

    /**
     * WORK in progress - handle long line output
     * @param text - to write to file
     * @param seperator - line feed
     */
    private void outputLongLine(String text,String seperator){
        int begIndx = 0;
        int spaceIndx = 0;
        int endIndx = 100;
        try {
            for (int i = 0; i < text.length(); i += 100) {
                spaceIndx = text.indexOf(" ", endIndx);
                endIndx+=100;
                mWriter.write("\t" + text.substring(i, spaceIndx));
                mWriter.write(seperator);
                mWriter.flush();
            }
        }catch(IOException e){
            System.out.println("Error writing long line " + endIndx );}
    }
}

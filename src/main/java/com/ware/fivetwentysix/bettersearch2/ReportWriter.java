package com.ware.fivetwentysix.bettersearch2;

import java.io.*;
import java.util.HashSet;
/*
    Report on the sentences processed from the document
    Provides information on stemmed word frequency in FindStats.txt
 */

public class ReportWriter {
    private Writer mWriter = null;
    private Writer mWriter2 = null;
    private String mFilename;
    private String mWordStats = "FindStats.txt";

    public ReportWriter(){}

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
        return status;
    }

    /**
     * write the text passed complete with seperator
     * @param text - text to be printed
     * @param seperator - line terminator
     * @return success or fail
     */
    public int writeReportFile(String text, String seperator){
        int status = 1;
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
        writeReportFile("\r\n"+header,"\r\n");
        writeReportFile("----------------------------","\r\n");
    }

    /**
     * write report
     * @param textFound - set of text
     *                  TODO - is this needed
     */
    protected void writeReport(HashSet<String> textFound){
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

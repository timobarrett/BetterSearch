package com.ware.fivetwentysix.bettersearch2;


import javax.print.DocFlavor;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainEntry {
    public static void main(String[] args) {
        long startTime = System.nanoTime();//System.currentTimeMillis();
        SearchProcessor searchProcessor = new SearchProcessor(new ArrayList<String>(Arrays.asList(args)));
        searchProcessor.ProcessQueryRequest();
        ArrayList<String> urlResults = searchProcessor.reportTop3Sites();
        popBrowsers(urlResults);
        System.out.println("Time taken = "+Utility.returnFormattedDuration (System.nanoTime() - startTime));
    }

    /**
     * Open browser windows to the top 3 sites found
     * @param urls - urls to open
     */
    public static void popBrowsers(ArrayList<String>urls) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            for(String url : urls){
                System.out.println("URL = "+url);
                try {
                    desktop.browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    System.out.println("Exception opening browser " + e.getLocalizedMessage());
                }
            }
        }
    }
}

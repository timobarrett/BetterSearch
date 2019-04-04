package com.ware.fivetwentysix.bettersearch2;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Utility {
    /**
     * Returns the seconds and nanoseconds for time to run
     * @param nanos - elapsed tim
     * @return - string of time
     */
    static String returnFormattedDuration(long nanos){
//result.append(Integer.toString(minutes)).append(":").append(String.format("%02d",seconds)).append (" ").append(paceUnit);
        StringBuilder stTime = new StringBuilder();
        Long days= TimeUnit.NANOSECONDS.toDays(nanos);
        nanos -= TimeUnit.DAYS.toNanos(days);
        Long hours=TimeUnit.NANOSECONDS.toHours(nanos);
        nanos -= TimeUnit.HOURS.toNanos(hours);
        long minutes=TimeUnit.NANOSECONDS.toMinutes(nanos);
        nanos -= TimeUnit.MINUTES.toNanos(minutes);
        long seconds= TimeUnit.NANOSECONDS.toSeconds(nanos);
        nanos -= TimeUnit.SECONDS.toNanos(seconds);
        Long millisec = TimeUnit.NANOSECONDS.toMillis(nanos);
        if (days!=0){
            stTime.append(days.toString()).append(":");}
        if (hours!=0 || days !=0){
            stTime.append(hours.toString()).append(":");}
        stTime.append(String.format("%02d",minutes)).append(":").append(String.format("%02d",seconds)).append(".").append(millisec.toString());
        return stTime.toString();
    }

    /**
     * Build web based query
     * @param searchTerms - user input
     * @return compiled search term for browser
     */
    static String buildInitialQuery(ArrayList<String> searchTerms){
        StringBuilder sbUrl = new StringBuilder();

        String queryBing = "https://www.bing.com/search?q=";
        String queryGoogle = "https://www.google.com/search?q=";
        String queryTail2 = "&aqs=chrome..69i57j69i60l2j0l3.5992j0j9&sourceid=chrome&ie=UTF-8";
        String queryTail = "&PC=U316&FORM=CHROMN";
        if (searchTerms.contains("google")){
            sbUrl.append(queryGoogle);
        }
        else{
            sbUrl.append(queryBing);
        }
        for(int i=1;i<searchTerms.size();i++){
            sbUrl.append(searchTerms.get(i));
            if (i < searchTerms.size()-1){
                sbUrl.append("+");
            }
        }
        sbUrl.append(queryTail2);
        //    jsoupProcessor.addCommonTerms(new ArrayList<>(Arrays.asList(args)));
        return sbUrl.toString();
    }
}

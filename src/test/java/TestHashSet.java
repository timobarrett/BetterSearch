import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class TestHashSet {

    private String goooUn =  "https://www.diynetwork.com/how-to/outdoors/gardening/how-to-grow-tomatoes";
    private String goodUn2 = "https://www.thespruce.com/top-tomato-growing-tips-1402587";
    private String goodUn3 = "https://www.scientificamerican.com/article/case-against-heirloom-tomatoes/";
    private String badUn = "/search?q=related:https://bonnieplants.com/library/9-ways-to-grow-tomatoes/+grow+tomatoes&tbo=1&sa=X&ved=0ahUKEwi7uo2enO_bAhUqi1QKHWHQDwoQHwi1ATAa";
    private String badUn2 = "/preferences?hl=en&prev=https://www.google.com/search?q%3Dgrow%2Btomatoes%26oq%3Dgrow%2B%26aqs%3Dchrome.0.69i59j69i57j69i60j69i61l2j0.2540j0j7%26sourceid%3Dchrome%26ie%3DUTF-8#languages";
    private String badUn3 = "/imgres?imgurl=https://i1.wp.com/bonnieplants.com/wp-content/uploads/2011/10/soaker-hose-tomato.jpg?ssl%3D1&imgrefurl=https://bonnieplants";

    private HashSet<String> testSet = new HashSet<>();

    @Before
    public void setupForTest(){
        testSet.add(goooUn);
        testSet.add(badUn);
        testSet.add(goodUn2);
        testSet.add(badUn2);
        testSet.add(goodUn3);
        testSet.add(badUn3);

        System.out.print("Size of hashset = "+testSet.size()+"\n");
    }

    @Test
    public void testHashSet(){
        Iterator<String> urlsFound = testSet.iterator();
        ArrayList<String> mVals = new ArrayList<>();
//        while(urlsFound.hasNext()){
//            String url = urlsFound.next().toString();
//            System.out.print("** "+url +"\n");
//            if (url.indexOf("http")!=0){
//                System.out.println("HTTPS NOT AT BEGINNING - " + url + "\n");
//                System.out.println("Beginning = "+url.substring(0,10)+"\n");
////                testSet.remove(url);
////                testSet.add(url.substring(url.indexOf("http"),url.length()));
//            }
//        }
        while(urlsFound.hasNext()){
            String url = urlsFound.next();
            if(url.indexOf("http")!=0){
                urlsFound.remove();
                mVals.add(url.substring(url.indexOf("http"),url.length()));
            }
        }
        testSet.addAll(mVals);
        for(String str2:testSet){
            System.out.println("** "+str2);
        }
    }

}




import com.ware.fivetwentysix.bettersearch2.Site;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class TestSentenceProc {

    @Test
    public void verifySentenceProc(){
        Site site = new Site("www.anyold.thing",true);
        String testSentence1 = "This is a, test. Will this work? Hope the test tests.";
        String testSentence2 = "This is a test.Will this work?Hope the test tests.";
        site.addSiteText(testSentence1);
        System.out.println("Count = "+site.getSentenceCount());
        assertTrue ("# sentence parsed incorrect",site.getSentenceCount()==3);
        Site site2 = new Site("www.anyold.thing",false);
        site2.addSiteText(testSentence2);
        System.out.println("Count ="+site2.getSentenceCount());
        assertTrue("# sentences parsed incorrect",site2.getSentenceCount()==3);
        ArrayList<String> results = site2.foundStrings("test");
        for(String val:results){
            System.out.println("RESULT = "+val);
        }
    }
}

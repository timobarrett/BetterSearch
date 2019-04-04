import com.ware.fivetwentysix.bettersearch2.BaseStemmer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TestStemmer {
    BaseStemmer mBaseStemmer;

    @Before
    public void setupForTest(){
        mBaseStemmer = new BaseStemmer();
    }

    //happyPath
    @Test
    public void testIngCase(){
        String ingWord = "Testing";
        String result = mBaseStemmer.getStem(ingWord);
        assertTrue("Test not returned ",result.equals("Test"));
    }

    @Test
    public void testPluralCase(){
        String pluralWord = "Tests";
        String pluralWord2 ="Testes";
        String result = mBaseStemmer.getStem(pluralWord);
        assertTrue("Test not returned ",result.equals("Test"));
        String results2 = mBaseStemmer.getStem(pluralWord2);
        assertTrue("Test not returned ",result.equals("Test"));
    }
}

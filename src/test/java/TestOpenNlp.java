import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Sequence;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

public class TestOpenNlp {

    private BufferedReader bufferedReader = null;
    InputStream inputStream = null;

    InputStream inputFileStream = null;
    private static final String simpleSentence = "Add ¼ cup of balanced fertilizer per two plants." ;
    POSModel model = null;
    WhitespaceTokenizer whitespaceTokenizer = null;
    POSTagger tagger;
    private ArrayList<String>nounArray = null;
    private ArrayList<String>allSentences;
    private ArrayList<String>summaryDoc;
    private HashSet<String>nounStrings = null;
    private PorterStemmer pStemmer = null;

    public TestOpenNlp(){
        nounArray = new ArrayList<>();
        nounStrings = new HashSet<String>();
        summaryDoc = new ArrayList<>();
        pStemmer = new PorterStemmer();
    }
    @Before
    public void setup(){
        allSentences = loadText();
        try {
            inputStream = new FileInputStream("en-pos-maxent.bin");
        } catch (FileNotFoundException e) {
            System.out.println("Error opening en_maxent file");
            assert bufferedReader == null;
        }
        try {
            bufferedReader = new BufferedReader(new FileReader("SearchResults.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Error opening file");
            assert bufferedReader == null;
        }
        model = null;
        try {
            model = new POSModel(inputStream);
        }catch(IOException e){System.out.println(e.getStackTrace());}
        whitespaceTokenizer = WhitespaceTokenizer.INSTANCE;
         tagger = new POSTaggerME(model);
    }

    @Test  //simple tokenizer of single sentence
    public void staticInputTest(){

        String[] tokens = whitespaceTokenizer.tokenize(simpleSentence);

        String[] tags = tagger.tag(tokens);
        POSSample sample = new POSSample(tokens,tags);
     //SAMPLE = Add_VB ¼_JJ cup_NN of_IN balanced_JJ fertilizer_NN per_IN two_CD plants._NN
        //VB = verb, base form. JJ=adjective, NN = noun, singualr or mass, IN = preposition, CD=cardinal number
        System.out.println("SAMPLE = "+ sample.toString());
    }
    @Test //tokenize file
    public void fileInputTest(){
        String textLine;
        try {
            while ((textLine = bufferedReader.readLine()) != null) {
                if (textLine.length()>1) {
                    String[] tokens = whitespaceTokenizer.tokenize(textLine);
                    String[] tags = tagger.tag(tokens);
                    POSSample sample = new POSSample(tokens, tags);
                    harvestTheNouns(sample.toString());

                }
            }
        } catch (IOException e) {
            System.out.println("Error reading = " + e.getLocalizedMessage());
        }
        processSummaryDoc();
        System.out.println("BREAK");
    }

    private void harvestTheNouns(String sample){
        String[] splitSample = sample.split(" ");
        for (String value:splitSample){
            if(value.contains("_NN")){
                if (value.contains(".")|| value.contains(",")|| value.contains("!")){
                    nounStrings.add(pStemmer.stem(value.substring(0,value.indexOf("_NN")-1)));
                }else {
                    nounStrings.add(pStemmer.stem(value.substring(0, value.indexOf("_NN"))));
                }
            }
        }
    }
    private ArrayList<String> loadText(){
        StringBuilder contentBuilder = new StringBuilder();
        String[] startText;
        try (Stream<String> stream = Files.lines( Paths.get("SearchResults.txt"), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        { e.printStackTrace(); }
        startText = contentBuilder.toString().split("\n\t");
        return new ArrayList<String>(Arrays.asList(startText));
    }
    private void processSummaryDoc(){
        for (String noun:nounStrings){
            for(int j=0; j<allSentences.size();j++){
                if (allSentences.get(j).contains(noun)&&!summaryDoc.contains(allSentences.get(j))){
                    summaryDoc.add(allSentences.get(j));
                }
            }
            summaryDoc.add(" ");
        }

        Writer mWriter = null;
        try{
            mWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Summary.txt"),"utf-8"));
        }catch(UnsupportedEncodingException | FileNotFoundException e){
            System.out.println("File Exception - report file = "+e.getLocalizedMessage());
        }
        for (String sentence:summaryDoc){
            try {
                mWriter.write(sentence + "\n");
            }catch(IOException e){System.out.println(e.getStackTrace());}
        }
    }
}

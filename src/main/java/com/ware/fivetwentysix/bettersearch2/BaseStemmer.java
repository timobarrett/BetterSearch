package com.ware.fivetwentysix.bettersearch2;

/*
    Start of building a homegrown stemmer - needs work
 */
public class BaseStemmer {

    public BaseStemmer(){

    }

    /**
     * return the ste of the word passed in
     * @param word - parsed from document
     * @return - stemmed word
     */
    public String getStem(String word){
        String result = word;
        String plural = "s";
        String plural2 = "es";
        if (word.contains("ing")){
            return word.replace("ing","");
        }
        if (word.lastIndexOf(plural2)==word.length()-2 && word.length()>4){
            return word.substring(0,word.length()-2);
        }
        if (word.lastIndexOf(plural)==word.length()-1&&word.length()>4){
                return word.substring(0,word.length()-1);
        }

        return result;
    }
}

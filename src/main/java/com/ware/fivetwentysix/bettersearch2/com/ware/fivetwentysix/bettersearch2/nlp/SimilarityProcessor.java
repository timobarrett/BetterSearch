package com.ware.fivetwentysix.bettersearch2.com.ware.fivetwentysix.bettersearch2.nlp;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

//import opennlp.tools.similarity.apps.utils.StringDistanceMeasurer;

public class SimilarityProcessor {
    private static final Logger Log = Logger.getLogger( SimilarityProcessor.class);
    public SimilarityProcessor(){}

    public static List<String> removeDuplicatesFromQueries(List<String> hits) {
        StringDistanceMeasurer meas = new StringDistanceMeasurer();
        double dupeThresh = 0.7; // if more similar, then considered dupes was
        // 0.7
        List<Integer> idsToRemove = new ArrayList<Integer>();
        List<String> hitsDedup = new ArrayList<String>();
        try {
            for (int i = 0; i < hits.size(); i++)
                for (int j = i + 1; j < hits.size(); j++) {
                    String title1 = hits.get(i);
                    String title2 = hits.get(j);
                    if (StringUtils.isEmpty(title1) || StringUtils.isEmpty(title2))
                        continue;
                    if (meas.measureStringDistance(title1, title2) > dupeThresh) {
                        idsToRemove.add(j); // dupes found, later list member to
                        // be deleted

                    }
                }

            for (int i = 0; i < hits.size(); i++)
                if (!idsToRemove.contains(i))
                    hitsDedup.add(hits.get(i));

            if (hitsDedup.size() < hits.size()) {
                Log.info("Removed duplicates from formed query, including "
                        + hits.get(idsToRemove.get(0)));
            }

        } catch (Exception e) {
            Log.error("Problem removing duplicates from query list");
        }
        if (hits.size() > hitsDedup.size()){
            Log.info("Similarity REDUCTION ="+(hits.size()-hitsDedup.size()));
        }
        return hitsDedup;

    }
}

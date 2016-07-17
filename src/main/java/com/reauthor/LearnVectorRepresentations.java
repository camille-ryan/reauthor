package com.reauthor;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.*;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * (heavily) Modified by Ryan Brady from dl4j example of word2vec
 */
public class LearnVectorRepresentations {

    private static Logger log = LoggerFactory.getLogger(LearnVectorRepresentations.class);

    public static void main(String[] args) throws Exception {

        File[] textFiles = new File("/Users/ryanbrady/lanthack/reauthor/target/classes/").listFiles();
        System.out.println(Arrays.toString(textFiles));
        ArrayList<String> text = new ArrayList<>();
        for(File fileLoc : textFiles) {
            if(fileLoc.toString().substring(fileLoc.toString().lastIndexOf('.') + 1).equals("txt"))
                text.addAll(gutenberg(fileLoc));
        }

        log.info("Load & Vectorize Sentences....");

        // Strip white space before and after for each line
        SentenceIterator iter = UimaSentenceIterator.createWithPath("/Users/ryanbrady/lanthack/reauthor/target/classes/");


        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(10)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");

        // Write word vectors
        WordVectorSerializer.writeFullModel(vec, "fittedmodel.txt");

        log.info("Closest Words:");
        Collection<String> lst = vec.wordsNearest("thought", 10);
        System.out.println(lst);

    }

    private static ArrayList gutenberg(File doc) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(doc));
        String line = null;
        ArrayList<String> paragraph = new ArrayList<String>();
        ArrayList<String> items = new ArrayList<String>();

        while ((line = br.readLine()) != null)
        {
            if(line.length() >=25){
                if(line.substring(0,25).equals("*** START OF THIS PROJECT"))
                    break;
            }
        }
        while ((line = br.readLine()) != null) {
            if (line.length() >= 18) {
                if (line.substring(0, 18).equals("End of the Project"))
                    break;
            }
            if (line.length() > 0) {
                paragraph.add(line);
            }
            else {
                StringBuilder builder = new StringBuilder();
                for (String value : paragraph) {
                    // deal with broken words across lines.
                    if(value.substring(value.length()-1) == "-"){
                        builder.append(value.substring(0, value.length()-2));
                    }
                    else {
                        builder.append(value);
                        builder.append(" ");
                    }
                }
                String[] text = builder.toString().split("[.?!-=_]");
                for(String sentence : text) {
                    items.add(sentence);
                }
                paragraph.clear();
            }

        }

        return(items);
    }

}

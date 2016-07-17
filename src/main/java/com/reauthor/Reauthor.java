package com.reauthor;


import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Ryan Brady on 7/16/16.
 */
public class Reauthor {
    public static void main(String[] args) throws Exception {
        Word2Vec vec = WordVectorSerializer.loadFullModel("fittedmodel.txt");
        double[] wordProbs = getWordProbs("dickens", vec);

        File sampleFile = new File("/Users/ryanbrady/lanthack/reauthor/target/classes/lovecraft - the shunned house.txt");
        ArrayList<String> sample = gutenberg(sampleFile);


        for(int i = 0; i <100; i++) {
            StringBuilder sb = new StringBuilder();
            String sampleLine = sample.get(i);
            String[] tokens = sampleLine.split("\\s");
            for (String token : tokens) {
                double prob = 0;
                String replacement = token;

                for(String candidate : vec.wordsNearest(token, 9)) {
                    double thisProb =  vec.similarity(candidate, token);
                    int wordindex = 0;
                    for(String word : vec.getVocab().words()){
                        if(word.equals(candidate)){
                            thisProb = thisProb * wordProbs[wordindex] / (1 + wordProbs[wordindex]);
                        }
                        wordindex++;
                    }
                    thisProb += Math.random();

                    if(thisProb > prob) {
                        prob = thisProb;
                        replacement = candidate;
                    }
                }
                if(1 + Math.random() > prob){
                    replacement = token;
                }

                    sb.append(replacement);
                    sb.append(" ");



            }
            System.out.println("|" + sampleLine + "|" + sb.toString() + "|");
        }



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
                String[] text = builder.toString().split("[\\n]");
                for(String sentence : text) {
                    items.add(sentence);
                }
                paragraph.clear();
            }

        }

        return(items);
    }

    private static double[] getWordProbs(String author, Word2Vec vec) throws Exception {

        File[] textFiles = new File("/Users/ryanbrady/lanthack/reauthor/target/classes/").listFiles();
        ArrayList<String> text = new ArrayList<>();
        for (File fileLoc : textFiles) {
            String fileString = fileLoc.toString();
            if (fileString.indexOf('-') > -1) {
                String pattern = fileString.substring(fileString.lastIndexOf('/') + 1, fileString.indexOf('-') - 1);
                if (pattern.equals(author)) {
                    text.addAll(gutenberg(fileLoc));
                }
            }
        }
        SentenceIterator iter = new CollectionSentenceIterator(text);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        Word2Vec sourceAuthor = new Word2Vec.Builder()
                .minWordFrequency(1)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();
        sourceAuthor.buildVocab();
        VocabCache authorVocab = sourceAuthor.getVocab();
        Collection<String> awords = authorVocab.words();


        VocabCache vocab = vec.getVocab();
        Collection<String> words = vocab.words();

        double[] corpusFreq = new double[words.size()];
        double corpusWordCount = 0;
        int i = 0;
        double[] authorFreq = new double[words.size()];
        double authorWordCount = 0;

        for (String word : words) {
            corpusFreq[i] = vocab.wordFrequency(word);
            corpusWordCount += corpusFreq[i];
            authorFreq[i] = authorVocab.wordFrequency(word);
            authorWordCount += authorFreq[i];
            i++;
        }

        double[] authorRatio = new double[words.size()];
        double vocabRatio = corpusWordCount / authorWordCount;
        for (int j = 0; j < corpusFreq.length; j++) {
            authorRatio[j] = vocabRatio * authorFreq[j] / corpusFreq[j];
        }
        return(authorRatio);
    }


}


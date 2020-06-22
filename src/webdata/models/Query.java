package webdata.models;

import webdata.IndexReader;

import java.lang.reflect.Array;
import java.util.*;

public class Query {
    public static String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves"};
    public static HashSet<String> stopwordsSet = new HashSet<>(Arrays.asList(stopwords));
    private final ArrayList<String> filteredWords;
    private Enumeration<String> query;

    public HashMap<String, Integer> getQueryTermFreq() {
        return queryTermFreq;
    }

    public HashMap<String, Integer> queryTermFreq;

    public Query(Enumeration<String> query){
        this.query = query;
        this.filteredWords = new ArrayList<>();
        this.queryTermFreq = generateQueryTermFrequency();
    }

    private void addToTermMap(String term, HashMap<String, Integer> termMap){
        if(termMap.containsKey(term)){
            termMap.put(term, termMap.get(term) + 1);
        }
        else {
            termMap.put(term, 1);
        }
    }

    public HashMap<String, Integer> generateQueryTermFrequency(){
        HashMap<String, Integer> termMap = new HashMap<>();
        while (query.hasMoreElements()){
            String term = query.nextElement().toLowerCase();
            if(stopwordsSet.contains(term)){
                this.filteredWords.add(term);
                continue;
            }
            addToTermMap(term, termMap);
        }
        if (termMap.size() == 0 && filteredWords.size() > 0){ // we filtered everything
            for (String term: filteredWords) {
                addToTermMap(term, termMap);
            }
        }
        return termMap;
    }


    public HashMap<String, Integer> generateCorpusTermFrequency(IndexReader iReader){
        HashMap<String, Integer> termMap = new HashMap<>();
        for (String term: queryTermFreq.keySet()) {
            termMap.put(term,iReader.getTokenCollectionFrequency(term));
        }
        return termMap;
    }

    /**
     * returns a map of the form:
     * { reviewId1 : { term1 : freq1, term2 : freq2, }, reviewId2 : { term2 : freq2, term3 : freq3, } ... }
     * where term1, term2, term3 are all in the query
     * @param iReader
     */
    public HashMap<Integer, HashMap<String, Double>> getReviewIdToFreqMapForQueryTokens(IndexReader iReader, boolean logFreq){
        HashMap<Integer, HashMap<String, Double>> reviewIdToFreqMap = new HashMap<>();
        for (String term: queryTermFreq.keySet()) {
            Enumeration<Integer> reviewsWithToken = iReader.getReviewsWithToken(term);
            while (reviewsWithToken.hasMoreElements()){
                int reviewId = reviewsWithToken.nextElement();
                int tokenFreq = reviewsWithToken.nextElement();
                if(reviewIdToFreqMap.containsKey(reviewId)){
                    if(logFreq)
                        reviewIdToFreqMap.get(reviewId).put(term, 1 + Math.log10(tokenFreq));
                    else
                        reviewIdToFreqMap.get(reviewId).put(term, (double)tokenFreq);
                }
                else {
                    HashMap<String, Double> termToScoreMap = new HashMap<>();
                    if(logFreq)
                        termToScoreMap.put(term, 1 + Math.log10(tokenFreq));
                    else
                        termToScoreMap.put(term, (double) tokenFreq);

                    reviewIdToFreqMap.put(reviewId, termToScoreMap);
                }
            }
        }
        return reviewIdToFreqMap;
    }

    private int getDf(IndexReader iReader,String term){
        Enumeration<Integer> reviewsWithToken = iReader.getReviewsWithToken(term);
        int counter = 0;
        while (reviewsWithToken.hasMoreElements()) {
            int rev = reviewsWithToken.nextElement();
            reviewsWithToken.nextElement();
            counter++;
        }
        return counter;
    }

    public HashMap<String, Double> getLTCScore(IndexReader iReader, boolean normalize){
        HashMap<String, Double> termMap =  new HashMap<>();
        int N = iReader.getNumberOfReviews();
        for (String term: queryTermFreq.keySet()) {
            int freq = queryTermFreq.get(term);
            if(freq == 0){
                termMap.put(term, 0.0);
            }else {
                int df = getDf(iReader, term);
                if(df != 0)
                    termMap.put(term, (1 + Math.log10(freq)) * Math.log10(N / df));
                else
                    termMap.put(term, 0.0);

            }
        }

        if(normalize){
            double ltcNormSum = 0;
            for(double value : termMap.values()){
                ltcNormSum+= value*value;
            }

            for(String term : termMap.keySet()){
                termMap.put(term, termMap.get(term)/ Math.sqrt(ltcNormSum));
            }
        }

        return termMap;
    }

}

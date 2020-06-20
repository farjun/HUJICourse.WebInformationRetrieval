package webdata;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

public class ReviewStore {

    public static void main(String[] args) throws Exception {
        run();
    }

    static class QueryEnumeration implements Enumeration<String> {
        private ArrayList<String> enumeration;

        QueryEnumeration(String query){

            this.enumeration = new ArrayList<>(Arrays.asList(query.split(" ")));;
        }

        @Override
        public boolean hasMoreElements() {
            return this.enumeration.size() > 0;
        }

        @Override
        public String nextElement() {
            return this.enumeration.remove(0);
        }
    }

    private static void printEnumeration(Enumeration enumeration){
        while (enumeration.hasMoreElements()){
            System.out.print(enumeration.nextElement());
            System.out.println(",");
        }
    }

    private static void run() {
        String indexDir = "./index";
//        String inputFilePath = "./datasets/counted/10000.txt";
        String inputFilePath = "./datasets/1000.txt";
        IndexWriter indexWriter = new IndexWriter();
        indexWriter.write(inputFilePath, indexDir);

        ReviewSearch reviewSearch = new ReviewSearch(new IndexReader(indexDir));
        printEnumeration(reviewSearch.vectorSpaceSearch(new QueryEnumeration("flavored coffee"), 5));
        System.out.println("---------------------------------------------------------------");
        printEnumeration(reviewSearch.languageModelSearch(new QueryEnumeration("flavored coffee"), 0.4, 5));

    }

}

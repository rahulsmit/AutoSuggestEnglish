import java.io.IOException;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        TermDictionary.init();
        TermDictionary.loadData();
        //System.out.println("TermDictionary = " + TermDictionary.readData(1123));
        System.out.println("TermDictionary.nodeCount.get() = " + TermDictionary.nodeCount.get());

        wordsForPrefix("a");
        wordsForPrefix("ac");
        wordsForPrefix("aci");
    }

    private static void wordsForPrefix(String prefix) throws IOException {
        Set<Integer> termIds = TermDictionary.suggestWords(prefix);
        System.out.println("termIds = " + termIds.size());
        System.out.println("Prefix = " + prefix + " ---> \n" + TermDictionary.readMultipleTerms(termIds));
    }
}



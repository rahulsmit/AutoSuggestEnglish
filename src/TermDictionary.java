import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TermDictionary {
    public  static AtomicInteger nodeCount = new AtomicInteger(0);
    public static String SOURCE_FILE = "words.txt";
    public static byte[] blob;
    public static int[] offsets;
    public static int[] lengths;
    public static Map<String, Integer> dictionary;
    public static TrieNode root;

    public static void init(){
        dictionary = new HashMap<>();
        root = new TrieNode();
    }

    public static void loadData() throws IOException {

        int totalBytes = 0;
        int totalWords = 0;


        try (BufferedReader br = new BufferedReader(new FileReader(SOURCE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                totalBytes+= line.getBytes(StandardCharsets.UTF_8).length;
                totalWords++;
            }
        }


        blob = new byte[totalBytes];
        offsets = new int[totalWords];
        lengths = new int[totalWords];
        int wordId = 0;
        int cursor = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(SOURCE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
                offsets[wordId] = cursor;
                lengths[wordId] = bytes.length;
                System.arraycopy(bytes, 0, blob, cursor, bytes.length);
                cursor += bytes.length;
                insertWordToTrie(line.trim(), wordId);
                wordId++;
            }
        }

    }

    public static String readData(int termId) throws IOException {
        int offset = offsets[termId];
        int length = lengths[termId];

        return new String(blob, offset, length, StandardCharsets.UTF_8);
    }

    public static List<String> readMultipleTerms(Set<Integer> termId) throws IOException {
        List<String> result = new ArrayList<>();
        for (Integer integer : termId) {
            result.add(readData(integer));
        }
        return result;
    }

    public static void insertWordToTrie(String word, int termId) {
        TrieNode node = root;
        for(Character ch : word.toCharArray()) {
            node = node.children.computeIfAbsent(ch, k -> new TrieNode());
            node.considerTopK(termId);
        }
        node.isWord = true;
    }

    public static Set<Integer> suggestWords(String prefix) {
        TrieNode node = root;
        for(Character ch : prefix.toCharArray()) {
            node = node.children.get(ch);
            if(node == null) {
                return Set.of();
            }
        }
        return node.topKCache;
    }

}

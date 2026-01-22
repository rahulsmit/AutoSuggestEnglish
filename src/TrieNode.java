import java.util.*;

public class TrieNode {
        boolean isWord;
        Map<Character, TrieNode> children;
        Set<Integer> topKCache ;
        public TrieNode() {
            TermDictionary.nodeCount.incrementAndGet();
            isWord = false;
            children = new HashMap<>();
            topKCache = new HashSet<>();
        }

        public void considerTopK(Integer termId) {
            topKCache.add(termId);
        }
}

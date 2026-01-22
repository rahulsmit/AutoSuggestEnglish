# Autocomplete Demo (Trie + Top-K Cache per Node)

This project is a simple **autocomplete / prefix-suggestion** demo built around a **Trie** where **each node maintains a small Top-K cache of term IDs** for that prefix.

It also demonstrates a memory-efficient **term dictionary** layout using a single byte `blob` + `offsets[]` + `lengths[]` arrays to reconstruct words by ID.

---

## What this demo shows

### 1) Fast prefix lookup with a Trie
- Each character in a term is stored in a Trie node.
- To get suggestions for a prefix, we traverse the Trie to the prefix node in **O(|prefix|)**.

### 2) Top suggestions stored per Trie node (Top-K cache)
- Each node maintains a small `topKCache` (a set of term IDs).
- While inserting each word, we update the cache along the path using `node.considerTopK(termId)`.
- The result: suggestion lookup is **O(|prefix|)** and returns cached IDs immediately (no DFS needed).

> Note: In a real system `topKCache` is usually capped to K and ordered by popularity/score.  
> Here it’s modeled as a set of IDs for simplicity.

### 3) Compact term storage via blob + offsets/lengths
Instead of storing strings everywhere, terms are packed into:
- `blob`: one big byte array containing all words back-to-back
- `offsets[id]`: starting position for term `id` inside the blob
- `lengths[id]`: length of the term bytes

This makes `readData(termId)` efficient and avoids per-string object overhead.

---

## Files

- `TermDictionary.java`  
  Loads words from `words.txt`, stores them in a compact dictionary, and builds the Trie with per-node top suggestions.
- `words.txt`  
  Input terms file: **one word per line**.

---

## How it works (high-level)

### Initialization
1. Count total words and total bytes needed for storage.
2. Allocate:
    - `blob = new byte[totalBytes]`
    - `offsets = new int[totalWords]`
    - `lengths = new int[totalWords]`
3. Second pass: copy each word’s bytes into `blob`, record `offset/length`, insert into Trie.

### Insertion into Trie
For each character:
- create/find the child node
- update that node’s `topKCache` with `termId`

### Suggestion lookup
To suggest for prefix `"app"`:
- traverse nodes: `a -> p -> p`
- return `node.topKCache`

---

## Complexity

### Build time
- **O(total characters across all terms)** for Trie insertion
- Two file passes for sizing + loading (keeps allocations tight)

### Suggest query
- **O(|prefix|)** to traverse Trie
- returning cached suggestions is O(1) (plus iteration over K items)

### Memory notes
- Trie nodes can be large at scale; caching Top-K per node trades memory for speed.
- The blob dictionary avoids storing large numbers of Java `String` objects.

---

## Running the demo

### 1) Prepare input
Create a `words.txt` file in the working directory:

```txt
apple
app
application
apply
banana
band
bandana

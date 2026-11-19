import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AutoCompleteSearchEngine {

    // Trie Node
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEndOfWord;
    }

    // Suggestion class
    static class Suggestion {
        String word;
        int frequency;

        Suggestion(String word, int frequency) {
            this.word = word;
            this.frequency = frequency;
        }
    }

    private TrieNode root = new TrieNode();

    // Word frequency storage
    private HashMap<String, Integer> wordFrequency = new HashMap<>();

    // Insert word into Trie and HashMap
    public void insertWord(String word, int frequency) {

        if (!isValidInput(word) || frequency <= 0) {
            System.out.println("Invalid word or frequency.");
            return;
        }

        TrieNode current = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'a';

            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }

            current = current.children[index];
        }

        current.isEndOfWord = true;

        wordFrequency.put(word, frequency);

        System.out.println("Word inserted successfully.");
    }

    // Validate lowercase letters a-z only
    private boolean isValidInput(String input) {

        if (input == null || input.isEmpty()) {
            return false;
        }

        for (char ch : input.toCharArray()) {
            if (ch < 'a' || ch > 'z') {
                return false;
            }
        }

        return true;
    }

    // Prefix suggestions
    public void suggestPrefix(String prefix) {

        if (!isValidInput(prefix)) {
            System.out.println("Invalid prefix. Only letters a-z are allowed.");
            return;
        }

        TrieNode current = root;

        for (char ch : prefix.toCharArray()) {
            int index = ch - 'a';

            if (current.children[index] == null) {
                System.out.println("No suggestions found.");
                return;
            }

            current = current.children[index];
        }

        ArrayList<Suggestion> suggestions = new ArrayList<>();

        collectWords(current, prefix, suggestions);

        if (suggestions.isEmpty()) {
            System.out.println("No suggestions found.");
            return;
        }

        sortSuggestions(suggestions);

        System.out.println("Suggestions:");

        for (Suggestion s : suggestions) {
            System.out.println(s.word + " (" + s.frequency + ")");
        }
    }

    // Collect all words from Trie
    private void collectWords(
            TrieNode node,
            String prefix,
            ArrayList<Suggestion> suggestions) {

        if (node.isEndOfWord) {
            int frequency = wordFrequency.get(prefix);
            suggestions.add(new Suggestion(prefix, frequency));
        }

        for (int i = 0; i < 26; i++) {

            if (node.children[i] != null) {

                char ch = (char) ('a' + i);

                collectWords(
                    node.children[i],
                    prefix + ch,
                    suggestions
                );
            }
        }
    }

    // Bubble sort by frequency (highest first)
    private void sortSuggestions(ArrayList<Suggestion> suggestions) {

        for (int i = 0; i < suggestions.size() - 1; i++) {

            for (int j = 0; j < suggestions.size() - i - 1; j++) {

                if (suggestions.get(j).frequency
                        < suggestions.get(j + 1).frequency) {

                    Suggestion temp = suggestions.get(j);

                    suggestions.set(j, suggestions.get(j + 1));

                    suggestions.set(j + 1, temp);
                }
            }
        }
    }

    // Naive substring search
    private boolean containsText(String word, String text) {

        if (text.length() > word.length()) {
            return false;
        }

        for (int i = 0; i <= word.length() - text.length(); i++) {

            int j = 0;

            while (j < text.length()
                    && word.charAt(i + j) == text.charAt(j)) {
                j++;
            }

            if (j == text.length()) {
                return true;
            }
        }

        return false;
    }

    // Search words containing given text
    public void searchContains(String text) {

        if (!isValidInput(text)) {
            System.out.println("Invalid text. Only letters a-z are allowed.");
            return;
        }

        ArrayList<Suggestion> matches = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {

            if (containsText(entry.getKey(), text)) {

                matches.add(
                    new Suggestion(entry.getKey(), entry.getValue())
                );
            }
        }

        if (matches.isEmpty()) {
            System.out.println("No matching words found.");
            return;
        }

        sortSuggestions(matches);

        System.out.println("Matching words:");

        for (Suggestion s : matches) {
            System.out.println(s.word + " (" + s.frequency + ")");
        }
    }

    // Display menu
    private void displayMenu() {

        System.out.println("\n===== AUTO-COMPLETE SEARCH ENGINE =====");
        System.out.println("1. Insert Word");
        System.out.println("2. Suggest Prefix");
        System.out.println("3. Search Contains");
        System.out.println("4. Exit");
        System.out.print("Enter choice: ");
    }

    // Main method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        AutoCompleteSearchEngine engine = new AutoCompleteSearchEngine();

        // Initial dictionary
        engine.insertWord("data", 5);
        engine.insertWord("database", 9);
        engine.insertWord("dating", 2);
        engine.insertWord("dog", 7);

        System.out.println("\nAuto-Complete Search Engine Started!");

        while (true) {

            engine.displayMenu();

            String choice = sc.nextLine();

            switch (choice) {

                case "1":
                    System.out.print("Enter word: ");
                    String word = sc.nextLine();

                    System.out.print("Enter frequency: ");
                    String freqInput = sc.nextLine();

                    try {
                        int frequency = Integer.parseInt(freqInput);
                        engine.insertWord(word, frequency);
                    } catch (NumberFormatException e) {
                        System.out.println("Frequency must be a number.");
                    }
                    break;

                case "2":
                    System.out.print("Enter prefix: ");
                    String prefix = sc.nextLine();

                    engine.suggestPrefix(prefix);
                    break;

                case "3":
                    System.out.print("Enter text to search: ");
                    String text = sc.nextLine();

                    engine.searchContains(text);
                    break;

                case "4":
                    System.out.println("Program exited successfully.");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please select 1-4.");
            }
        }
    }
}
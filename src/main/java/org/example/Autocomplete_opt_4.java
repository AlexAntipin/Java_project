package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStreamReader;
import java.io.InputStream;

public class Autocomplete_opt_4 {

    private Map<Character, TrieNode> root;
    private int columnNumber;

    public Autocomplete_opt_4(int columnNumber) {
        root = new HashMap<>();
        this.columnNumber = columnNumber;
    }

    public void insert(String word) {
        Map<Character, TrieNode> node = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!node.containsKey(c)) {
                node.put(c, new TrieNode());
            }
            node = node.get(c).children;
        }
        node.put(null, new TrieNode()); // помечаем конец слова
    }

    public List<String> search(String prefix) {
        Map<Character, TrieNode> node = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (!node.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.get(c).children;
        }
        List<String> words = new ArrayList<>();
        StringBuilder sb = new StringBuilder(prefix);
        getWordsHelper(node, words, sb);
        return words;
    }

    private void getWordsHelper(Map<Character, TrieNode> node, List<String> words, StringBuilder sb) {
        if (node.containsKey(null)) {
            words.add(sb.toString());
        }
        for (Character c : node.keySet()) {
            if (c != null) {
                sb.append(c);
                getWordsHelper(node.get(c).children, words, sb);
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }

    private static class TrieNode {
        private Map<Character, TrieNode> children;
        private boolean isWord;

        public TrieNode() {
            children = new HashMap<>();
            isWord = false;
        }

        public boolean isWord() {
            return isWord;
        }

        public void setWord(boolean isWord) {
            this.isWord = isWord;
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Необходимо передать номер столбца в качестве аргумента");
            System.exit(1);
        }

        int columnNumber = Integer.parseInt(args[0]);

        Autocomplete_opt_4 autocomplete = new Autocomplete_opt_4(columnNumber);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                Autocomplete_opt_4.class.getClassLoader().getResourceAsStream("airports.csv")));

        String line;
        int count = 0;
        long startTime = System.currentTimeMillis();
        char[] buffer = new char[8192]; // создаем буфер размером 8 кб
        int charsRead;
        StringBuilder sb = new StringBuilder();
        while ((charsRead = reader.read(buffer)) != -1) { // читаем файл блоками
            sb.append(buffer, 0, charsRead);
            String[] lines = sb.toString().split("\n"); // разделяем блоки на строки
            for (int i = 0; i < lines.length; i++) {
                String[] fields = lines[i].split(",");
                if (fields.length >= 2) {
                    String name = fields[1].replaceAll("\"", "");
                    autocomplete.insert(name);
                }
                count++;
                if (count == 10000) {
                    count = 0;
                    autocomplete.root = new HashMap<>();
                }
            }
            sb.delete(0, sb.length()); // очищаем StringBuilder после обработки блока
        }
        reader.close();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Время работы программы: " + totalTime + " миллисекунд");


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String prefix;
        do {
            System.out.print("Введите строку (!quit для выхода): ");
            prefix = br.readLine();
            if (!prefix.equals("") && !prefix.equals("!quit")) {
                List<String> results = autocomplete.search(prefix);
                for (String result : results) {
                    System.out.println(result);
                }
            }
        } while (!prefix.equals("!quit"));
    }
}
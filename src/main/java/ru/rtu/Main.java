package ru.rtu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final String RUSSIAN_TEXT_PATH = "src/main/resources/russianText.txt";
    private static final String ENCRYPTED_TEXT_PATH = "src/main/resources/encryptedText.txt";

    public static void main(String[] args) throws IOException {
        File russianText = new File(RUSSIAN_TEXT_PATH);
        SortedSet<Map.Entry<Character, Integer>> russianTextFrequencies = frequencyAnalyse(russianText);
        System.out.println("Russian text analyse:\n" + russianTextFrequencies + "\nsize:" + russianTextFrequencies.size());

        File encryptedFile = new File(ENCRYPTED_TEXT_PATH);
        SortedSet<Map.Entry<Character, Integer>> encryptedTextFrequencies = frequencyAnalyse(encryptedFile);
        System.out.println("Encrypted text analyse:\n" + encryptedTextFrequencies + "\nsize:" + encryptedTextFrequencies.size());

        String decipheredText = decipherText(ENCRYPTED_TEXT_PATH, encryptedTextFrequencies, russianTextFrequencies);
        System.out.println("result: " + decipheredText);
    }

    private static String decipherText(String pathToFile,
                                       SortedSet<Map.Entry<Character, Integer>> encryptedTextFrequencies,
                                       SortedSet<Map.Entry<Character, Integer>> russianTextFrequencies) throws IOException {
        TreeSet<Map.Entry<Character, Integer>> cloneSet = new TreeSet<>(russianTextFrequencies);
        String text = Files.readString(Path.of(pathToFile)).toLowerCase();
        for (Map.Entry<Character, Integer> encryptedTextFrequency : encryptedTextFrequencies) {
            Character encryptedChar = encryptedTextFrequency.getKey();
            Character actualChar = cloneSet.pollFirst().getKey();
            System.out.println("swap " + encryptedChar + " on " + actualChar);
            text = text.replace(encryptedChar, Character.toUpperCase(actualChar));
        }
        return text;
    }


    private static SortedSet<Map.Entry<Character, Integer>> frequencyAnalyse(File file) throws FileNotFoundException {
        List<Character> characters = readFileAsCharArray(file);
        return entriesSortedByValues(getFrequencyMap(characters));
    }

    private static Map<Character, Integer> getFrequencyMap(List<Character> characters) {
        Map<Character, Integer> resultMap = new TreeMap<>();
        for (Character character : characters) {
            if (Character.isLetter(character)) {
                resultMap.merge(character, 1, Integer::sum);
            }
        }
        return resultMap;
    }

    /**
     * @param map Исходная мапа
     * @param <K> ключ
     * @param <V> значение
     * @return обьект SortedSet, отсортированный по value в порядке убывания
     */
    private static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                (e1, e2) -> {
                    int res = e2.getValue().compareTo(e1.getValue());
                    return res != 0 ? res : 1;
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    /**
     * @param file файл с текстом
     * @return массив боксовых чаров всех символов, содержащихся в тексте
     * @throws FileNotFoundException
     */
    private static List<Character> readFileAsCharArray(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder stringBuilder = new StringBuilder(scanner.nextLine().toLowerCase());
        while (scanner.hasNextLine()) {
            stringBuilder.append("\n").append(scanner.nextLine().toLowerCase());
        }
        return stringBuilder.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
    }
}

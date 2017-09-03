package com.devianta.command;

import java.util.ArrayList;
import java.util.List;

class Parser implements ArgParser {
    /**
     * Indicate of string argument
     */
    private static int ARG = 0;
    /**
     * Indicate of argument name
     */
    private static int ARG_NAME = 1;

    public Parser() {
        super();
    }

    /**
     * Parse line
     * 
     * @param line
     * @return
     */
    @Override
    public LineArgs parse(String line) {
        LineArgs args = new LineArgs();
        List<String> subSrting = splitLine(line);

        if (!subSrting.isEmpty() && (argType(subSrting.get(0)) == ARG)) {
            args.setCommand(clearPrefix(subSrting.get(0)));
        }

        for (int i = 1; i < subSrting.size(); i += 1) {

            if (argType(subSrting.get(i)) == ARG_NAME) {
                if (i + 1 >= subSrting.size() || argType(subSrting.get(i + 1)) == ARG_NAME) {
                    args.putNamed(clearPrefix(subSrting.get(i)), "");
                } else {
                    args.putNamed(clearPrefix(subSrting.get(i)), clearPrefix(subSrting.get(i + 1)));
                    i += 1;
                }
            } else {
                args.putFree(clearPrefix(subSrting.get(i)));
            }
        }

        return args;
    }

    /**
     * Return type of word
     * 
     * @param word
     * @return
     */
    private int argType(String word) {
        if (word.indexOf(CLIParam.STR_DELIMITER.toString()) == 0) {
            return ARG;
        }

        try {
            Double.parseDouble(word);
            return ARG;
        } catch (NumberFormatException e) {
        }

        if (word.indexOf(CLIParam.ARG_PREFIX) == 0) {
            return ARG_NAME;
        }

        return ARG;
    }

    /**
     * Clear prefixes from word
     * 
     * @param word
     * @return
     */
    private String clearPrefix(String word) {
        if (argType(word) == ARG) {
            return word.substring(1);
        } else {
            return word.substring(CLIParam.ARG_PREFIX.length());
        }
    }

    /**
     * Split line to word list
     * 
     * @param line
     * @return
     * @throws IllegalArgumentException
     */
    private List<String> splitLine(String line) throws IllegalArgumentException {
        List<String> words = new ArrayList<>();
        line = line.trim();

        if (line.length() == 0) {
            return words;
        }

        if (line.contains(CLIParam.STR_DELIMITER.toString())) {
            return splitLineWithDelimiters(line);
        } else {
            return splitLineWithoutDelimiters(line);
        }
    }

    /**
     * Split line with string delimiters to word list
     * 
     * @param line
     * @return
     */
    private List<String> splitLineWithoutDelimiters(String line) {
        List<String> words = new ArrayList<>();

        for (String w : line.split("\\s+")) {
            if (argType(w) == ARG) {
                words.add(CLIParam.STR_DELIMITER.toString() + w);
            } else {
                words.add(w);
            }
        }

        return words;
    }

    /**
     * Split line with string delimiters to word list
     * 
     * @param line
     * @return
     * @throws IllegalArgumentException
     */
    private List<String> splitLineWithDelimiters(String line) throws IllegalArgumentException {
        List<String> words = new ArrayList<>();

        int count = (int) line.chars().filter(n -> n == CLIParam.STR_DELIMITER).count();
        if (count % 2 != 0) {
            throw new IllegalArgumentException("Unexpected string delimiter");
        }

        String[] subString = line.split(CLIParam.STR_DELIMITER.toString());

        for (int i = 0; i < subString.length; i += 1) {
            if (i % 2 == 0) {
                words.addAll(splitLine(subString[i]));
            } else {
                words.add(CLIParam.STR_DELIMITER.toString() + subString[i]);
            }
        }
        return words;
    }

}

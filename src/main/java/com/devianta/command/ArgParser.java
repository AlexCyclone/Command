package com.devianta.command;

interface ArgParser {

    /**
     * Parse line
     * 
     * @param line
     * @return
     */
    public LineArgs parse(String line);

}

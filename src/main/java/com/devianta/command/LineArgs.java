package com.devianta.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class LineArgs {
    /**
     * Command name
     */
    private String command;
    /**
     * Free arguments
     */
    private List<String> free = new ArrayList<>();
    /**
     * Named arguments
     */
    private Map<String, String> named = new HashMap<>();

    /**
     * New LineArgs
     */
    public LineArgs() {
        super();
        command = "";
    }

    /**
     * New LineArgs with command name
     * 
     * @param command
     */
    public LineArgs(String command) {
        super();
        this.command = command;
    }

    // Setters

    /**
     * Set command name
     * 
     * @param command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Put free argument
     * 
     * @param freeArg
     */
    public void putFree(String freeArg) {
        this.free.add(freeArg);
    }

    /**
     * Put named argument
     * 
     * @param arg
     * @param value
     */
    public void putNamed(String arg, String value) {
        this.named.put(arg, value);
    }

    // Getters

    /**
     * Get command name
     * 
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     * Get names list of named arguments
     * 
     * @return
     */
    public List<String> getArgsNames() {
        List<String> list = new ArrayList<>();
        list.addAll(named.keySet());
        return list;
    }

    /**
     * Get value of free argument use index
     * 
     * @param index
     * @return
     */
    public String getFreeValue(int index) {
        return free.get(index);
    }

    /**
     * Get value of named argument argument name
     * 
     * @param arg
     * @return
     */
    public String getNamedValue(String arg) {
        return named.get(arg);
    }

    // Counters

    /**
     * Get count of arguments
     * 
     * @return
     */
    public int getArgCount() {
        return getFreeArgCount() + getNamedArgCount();
    }

    /**
     * Get count of free arguments
     * 
     * @return
     */
    public int getFreeArgCount() {
        return free.size();
    }

    /**
     * Get count of named arguments
     * 
     * @return
     */
    public int getNamedArgCount() {
        return named.size();
    }

    /**
     * Return true if argument available in line
     * 
     * @param argument
     * @return
     */
    public boolean available(Argument<?> argument) {
        for (String name : getArgsNames()) {
            if (argument.isEqualsName(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detail of LineArgs
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LineParameters").append(System.lineSeparator());
        sb.append("command:         ").append(command).append(System.lineSeparator());
        sb.append("arguments count: ").append(getArgCount()).append(System.lineSeparator());
        sb.append("free arguments:  ").append(getFreeArgCount()).append(" ").append(free)
                .append(System.lineSeparator());
        sb.append("named arguments: ").append(getNamedArgCount()).append(" ").append(named);
        return sb.toString();
    }

}

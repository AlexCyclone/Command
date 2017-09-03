package com.devianta.command;

public interface CommandRun {
    /**
     * Implement command
     * 
     * @param cli
     * @param command
     */
    public void runCommand(CLI cli, Command command);

}

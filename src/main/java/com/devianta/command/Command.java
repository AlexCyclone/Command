package com.devianta.command;

import java.util.ArrayList;
import java.util.List;

public class Command implements Runnable {
    
    private String name;
    private String desc;
    private boolean parallelThread;
    private CommandRun commandRun;
    private List<Argument<?>> argument;
    private LineArgs line;
    private CLI cli;

    /**
     * New Command with default parameters
     * @param name
     * @param desc
     * @param commandRun
     */
    public Command(String name, String desc, CommandRun commandRun) {
        super();
        setName(name);
        setDesc(desc);
        setCommandRun(commandRun);
        parallelThread = false;
        argument = new ArrayList<>();
    }

    // Private setters

    private void setName(String name) {
        this.name = name;
    }

    private void setDesc(String desc) {
        this.desc = desc;
    }

    private void setCommandRun(CommandRun commandRun) {
        this.commandRun = commandRun;
    }

    // Chain setters

    /**
     * If command should run in parallel thread set isParallelThread value in
     * true
     * 
     * @param isParallelThread
     * @return
     */
    public Command inParallelThread(boolean isParallelThread) {
        this.parallelThread = isParallelThread;
        return this;
    }

    // Public setters

    /**
     * Add Argument Object to command
     * 
     * @param argument
     */
    public void addArgument(Argument<?> argument) {
        if (this.argument.contains(argument)) {
            throw new IllegalArgumentException("Argument \"" + argument + "\" already exist");
        }
        this.argument.add(argument);
    }

    // protected setters

    /**
     * Set command line from CLI
     * 
     * @param line
     */
    protected void setLine(LineArgs line) {
        this.line = line;
    }

    /**
     * Set the caller CLI
     * 
     * @param cli
     */
    protected void setCli(CLI cli) {
        this.cli = cli;
    }

    // protected getters

    /**
     * Return true if CommandRun should be execute in parallel thread
     * 
     * @return
     */
    protected boolean isParallelThread() {
        return parallelThread;
    }

    /**
     * Return name of Command
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Return description of Command
     * 
     * @return
     */
    protected String getDesc() {
        return desc;
    }

    /**
     * Return named Argument object use argument name
     * 
     * @param name
     * @return
     */
    public Argument<?> getArgument(String name) {
        for (Argument<?> arg : argument) {
            if (arg.isEqualsName(name)) {
                return arg;
            }
        }
        return null;
    }

    /**
     * Return full list of Argument objects
     * 
     * @return
     */

    protected List<Argument<?>> getArguments() {
        return argument;
    }

    /**
     * Return list of free arguments
     * 
     * @return
     */
    protected List<Argument<?>> getFreeArguments() {
        List<Argument<?>> freeArgs = new ArrayList<>();
        argument.stream().filter(n -> n.isFree()).forEachOrdered(n -> freeArgs.add(n));
        return freeArgs;
    }
    
    public Object getArg(int n) {
        return getFreeArguments().get(n).getValue();
    }
    
    public Object getArg(String name) {
        return getArgument(name).getValue();
    }

    @Override
    public void run() {
        try {
            validateArguments();
            fillArguments();
            commandRun.runCommand(cli, this);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + System.lineSeparator());
        } finally {
            clearArgs();
        }
    }

    /**
     * Validate arguments
     * 
     * @throws IllegalArgumentException
     */
    private void validateArguments() throws IllegalArgumentException {
        try {
            validateFree();
            validateRequired();
            validateLines();
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * Validate free arguments
     * 
     * @throws IllegalArgumentException
     */
    private void validateFree() throws IllegalArgumentException {
        int min = 0;
        int max = 0;
        for (Argument<?> arg : argument) {
            if (arg.isFree()) {
                max += 1;
                min = arg.isRequired() ? min + 1 : min;
            }
        }
        if (line.getFreeArgCount() < min || line.getFreeArgCount() > max) {
            throw new IllegalArgumentException("Unsupported arguments amount");
        }
    }

    /**
     * Check required arguments
     * 
     * @throws IllegalArgumentException
     */
    private void validateRequired() throws IllegalArgumentException {
        for (Argument<?> arg : argument) {
            if (arg.isFree() || !arg.isRequired()) {
                continue;
            }

            if (!line.available(arg)) {
                throw new IllegalArgumentException("Required argument \"" + arg + "\" hasn't finded");
            }
        }
    }

    /**
     * Check named arguments from line
     * 
     * @throws IllegalArgumentException
     */
    private void validateLines() throws IllegalArgumentException {
        for (String name : line.getArgsNames()) {
            if (getArgument(name) == null) {
                throw new IllegalArgumentException("Unexpected argument \"" + name + "\"");
            }
        }
    }

    /**
     * Fill arguments from line
     * 
     * @throws IllegalArgumentException
     */
    private void fillArguments() throws IllegalArgumentException {

        // Fill free arguments

        List<Argument<?>> fa = getFreeArguments();
        for (int i = 0; i < line.getFreeArgCount(); i++) {
            fa.get(i).setValue(line.getFreeValue(i));
        }

        // Fill named arguments

        for (String name : line.getArgsNames()) {
            getArgument(name).setValue(line.getNamedValue(name));
        }
    }

    /**
     * Clear arguments values
     */
    private void clearArgs() {
        for (Argument<?> arg : argument) {
            arg.clearValue();
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Command other = (Command) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}

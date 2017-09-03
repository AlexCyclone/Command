package com.devianta.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class CLI implements Runnable, CommandRun {
    private Scanner sc;
    private String name;
    private String inform;
    private String finSymbol;
    private Map<String, Command> commandPull = new HashMap<>();
    private Map<String, String[]> synonyms = new HashMap<>();
    private List<String> history = new ArrayList<>();
    private boolean quit = false;

    /**
     * Create CLI object with default setting
     */
    public CLI() {
        super();
        setDefaultSetting();
    }

    private final void setDefaultSetting() {
        name = "";
        inform = "";
        finSymbol = ">";
        setQuitCommand();
        setHistoryCommand();
        setHelpCommand();
    }

    private final void setQuitCommand() {
        Command command = new Command("quit", "Exit this interface", this);
        this.addCommand(command, "q");
    }

    private final void setHelpCommand() {
        String desc = "Show help";
        Command command = new Command("help", desc, this);
        desc = "command for detail information";
        command.addArgument(new Argument<>(desc, false));
        this.addCommand(command, "h", "?");
    }

    private final void setHistoryCommand() {
        String desc = "Show command history";
        Command command = new Command("history", desc, this);
        desc = "takes value n - positive integer, show last n parameter";
        command.addArgument(new Argument<Integer>(desc, false).setName("t", "tail").setConverter(new ConvertToInteger())
                .setValidator(new ValidatePositiveInteger()));
        command.addArgument(new Argument<String>("clear history", false).setName("c", "clear")
                .setValidator(new EmptyArgumentValidator()));

        this.addCommand(command);
    }

    // Chain setters

    /**
     * Set name of CLI
     * 
     * @param name
     * @return
     */
    public CLI setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set information in prompt
     * 
     * @param inform
     * @return
     */
    public CLI setInform(String inform) {
        this.inform = inform;
        return this;
    }

    /**
     * Set final symbol in prompt
     * 
     * @param finSymbol
     * @return
     */
    public CLI setFinSymbol(String finSymbol) {
        this.finSymbol = finSymbol;
        return this;
    }

    /**
     * Add new Command object in CLI without synonyms
     * 
     * @param command
     */
    public void addCommand(Command command) {
        String[] zero = new String[0];
        addCommand(command, zero);
    }

    /**
     * Add new Command object in CLI with synonyms
     * 
     * @param command
     * @param synonyms
     */
    public void addCommand(Command command, String... synonyms) {
        command.setCli(this);
        commandPull.put(command.getName(), command);
        for (String synonym : synonyms) {
            commandPull.put(synonym, command);
        }
        this.synonyms.put(command.getName(), synonyms);
    }

    /**
     * Get name of CLI
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get information in prompt
     * 
     * @return
     */
    public String getInform() {
        return inform.isEmpty() ? "" : "(" + inform + ")";
    }

    /**
     * Get final symbol in prompt
     * 
     * @return
     */
    public String getFinSymbol() {
        return finSymbol;
    }

    private String getPrompt() {
        return getName() + getInform() + getFinSymbol() + " ";
    }

    /**
     * Run CLI in new thread
     */
    public void running() {
        Thread th = new Thread(this);
        th.start();
    }

    @Override
    public void run() {
        while (!this.quit && !Thread.currentThread().isInterrupted()) {
            try {
                String str = promptLine();
                if (str.trim().equals("")) {
                    continue;
                }
                LineArgs params = new Parser().parse(str);
                Command command = commandPull.get(params.getCommand());
                if (command == null) {
                    System.out.println(
                            "Command " + (params.getCommand().equals("") ? "" : "\"" + (params.getCommand() + "\" "))
                                    + "not found. Type \"help\" for help.");
                    continue;
                }
                command.setLine(params);
                Thread thread = new Thread(command);
                thread.start();
                if (!command.isParallelThread()) {
                    thread.join();
                }
                history.add(str);
            } catch (IllegalArgumentException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private String promptLine() {
        sc = new Scanner(System.in);
        System.out.print(getPrompt());
        return sc.nextLine();
    }

    @Override
    public void runCommand(CLI cli, Command command) {
        switch (command.getName()) {
        case "quit":
            cli.quitCommand();
            break;
        case "history":
            historyCommand(command);
            break;
        case "help":
            helpCommand(command);
            break;
        default:
            break;
        }
    }

    private void quitCommand() {
        this.quit = true;
    }

    private void historyCommand(Command command) {
        if (command.getArgument("clear").isFilled()) {
            history = new ArrayList<>();
            return;
        }

        if (command.getArgument("tail").isFilled()) {
            Integer count = (Integer) command.getArgument("tail").getValue();
            count = count > history.size() ? history.size() : count;
            for (int i = history.size() - count; i < history.size(); i++) {
                System.out.println(history.get(i));
            }
            return;
        }

        for (String h : history) {
            System.out.println(h);
        }
    }

    private void helpCommand(Command command) {
        if (command.getFreeArguments().get(0).isFilled()) {
            System.out.println("Not ready");
        } else {
            cliHelp();
        }

    }

    private void cliHelp() {
        System.out.println("Supported command:" + System.lineSeparator());
        Set<String> commands = synonyms.keySet();

        for (String com : commands) {
            String comStr = com;

            if (synonyms.get(com).length > 0) {
                comStr += ", ";
                String synArr = Arrays.toString(synonyms.get(com));
                comStr += synArr.substring(1, synArr.length() - 1);
            }

            String desc = commandPull.get(com).getDesc();

            System.out.printf(" %-20s  %s%s", comStr, desc, System.lineSeparator());
        }
        //System.out.println(System.lineSeparator() + "For detail use: help <command>" + System.lineSeparator());
    }

    // private void commandHelp(String command) {}

}

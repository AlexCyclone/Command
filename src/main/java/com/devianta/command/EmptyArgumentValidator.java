package com.devianta.command;

public class EmptyArgumentValidator implements Validator<String> {

    /**
     * Validate named argument. Argument value should be empty. 
     */
    @Override
    public boolean validate(String str) throws IllegalArgumentException {
        if (!str.isEmpty()) {
            throw new IllegalArgumentException("Unexpected value \"" + str + "\" in command");
        }
        return true;
    }

}
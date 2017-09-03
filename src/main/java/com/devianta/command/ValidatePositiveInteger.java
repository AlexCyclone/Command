package com.devianta.command;

public class ValidatePositiveInteger implements Validator<Integer> {
    
    /**
     * Return true if object is positive integer
     */
    @Override
    public boolean validate(Integer object) throws IllegalArgumentException {
        if (object < 0) {
            throw new IllegalArgumentException(
                    "Value \"" + object + "\" didn't pass the validation, positive integer expected");
        }
        return true;
    }

}

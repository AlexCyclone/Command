package com.devianta.command;

public interface Validator<T> {

    /**
     * Implement argument validator
     * 
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    public boolean validate(T object) throws IllegalArgumentException;

}

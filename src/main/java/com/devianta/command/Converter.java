package com.devianta.command;

public interface Converter<T> {

    /**
     * Implement argument converter
     * 
     * @param str
     * @return
     * @throws IllegalArgumentException
     */
    public T convert(String str) throws IllegalArgumentException;

}

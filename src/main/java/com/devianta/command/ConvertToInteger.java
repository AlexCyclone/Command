package com.devianta.command;

public class ConvertToInteger implements Converter<Integer> {

    /**
     * Convert value to Integer
     */
    @Override
    public Integer convert(String str) throws IllegalArgumentException {
        Integer value;
        try {
            value = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Value \"" + str + "\" did not converted, integer value expected");
        }
        return value;
    }

}

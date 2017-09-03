package com.devianta.command;

public class Argument<T> {
    private T value;
    private String description;
    private boolean required;
    private String shortName = "";
    private String fullName = "";
    private Validator<T> validator;
    private Converter<T> converter;
    private boolean filled = false;

    public Argument(String description, boolean required) {
        super();
        setDescription(description);
        setRequired(required);
        setValidator((n) -> true);
    }

    // private setters

    private void setDescription(String description) throws IllegalArgumentException {
        this.description = nullCheck(description);
    }

    private void setRequired(boolean required) {
        this.required = required;
    }

    // chain setters

    public Argument<T> setName(String fullName) {
        this.fullName = nullCheck(fullName);
        return this;
    }

    public Argument<T> setName(String shortName, String fullName) {
        this.shortName = nullCheck(shortName);
        this.fullName = nullCheck(fullName);
        return this;
    }

    public Argument<T> setConverter(Converter<T> converter) {
        this.converter = converter;
        return this;
    }

    public Argument<T> setValidator(Validator<T> validator) {
        this.validator = validator;
        return this;
    }

    // setters

    protected void setValue(String value) throws IllegalArgumentException, NullPointerException, ClassCastException {
        checkConverter();
        this.value = converter.convert(value);

        try {
            if (!validator.validate(this.value)) {
                throw new IllegalArgumentException("Value \"" + value + "\" validation  failed");
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Converter in argument: \"" + getFullName() + "(" + getShortName() + ")" + "\" with value: \"" + value + "\" should be set");
        }

        this.filled = true;
    }

    protected void clearValue() {
        this.value = null;
        this.filled = false;
    }

    // Check fields

    private final <O> O nullCheck(O value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Null pointer in argument");
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private void checkConverter() throws NullPointerException {
        if (converter == null) {
            converter = (n) -> ((T) n);
        }
    }

    // Check parameters

    protected boolean isFree() {
        if (!isShortName() && !isFullName()) {
            return true;
        }
        return false;
    }

    public boolean isFilled() {
        return filled;
    }

    protected boolean isRequired() {
        return required;
    }

    // Check names

    protected boolean isEqualsName(String name) {
        if (isFree()) {
            return false;
        }
        if (isShortName() && getShortName().equals(name)) {
            return true;
        }
        if (isFullName() && getFullName().equals(name)) {
            return true;
        }
        return false;
    }

    private boolean isShortName() {
        return (shortName.length() > 0) ? true : false;
    }

    private boolean isFullName() {
        return (fullName.length() > 0) ? true : false;
    }

    // get parameters

    private String getShortName() {
        return shortName;
    }

    private String getFullName() {
        return fullName;
    }

    private String getDescription() {
        return description;
    }

    // get values

    protected T getValue() {
        return value;
    }

    // Overrides

    @Override
    public String toString() {
        return getFullName() + "(" + getShortName() + ") " + getDescription();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
        result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
        if (isFree()) {
            result = prime * result + ((description == null) ? 0 : description.hashCode());
        }
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
        Argument<?> other = (Argument<?>) obj;
        if (fullName == null) {
            if (other.fullName != null)
                return false;
        } else if (!fullName.equals(other.fullName))
            return false;
        if (shortName == null) {
            if (other.shortName != null)
                return false;
        } else if (!shortName.equals(other.shortName))
            return false;
        if (isFree()) {
            if (description == null) {
                if (other.description != null)
                    return false;
            } else if (!description.equals(other.description))
                return false;
        }
        return true;
    }

}

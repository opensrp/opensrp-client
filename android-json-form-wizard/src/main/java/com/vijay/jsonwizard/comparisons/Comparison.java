package com.vijay.jsonwizard.comparisons;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 09/02/2017
 */
public abstract class Comparison {
    protected static final String TYPE_STRING = "string";
    protected static final String TYPE_NUMERIC = "numeric";
    protected static final String TYPE_DATE = "date";
    protected static final String TYPE_ARRAY = "array";
    protected static final String DEFAULT_STRING = "";
    protected static final String DEFAULT_NUMERIC = "0";
    protected static final String DEFAULT_DATE = "01-01-1900";
    protected static final String DEFAULT_ARRAY = "[]";

    public abstract boolean compare(String a, String type, String b);

    public abstract String getFunctionName();


}

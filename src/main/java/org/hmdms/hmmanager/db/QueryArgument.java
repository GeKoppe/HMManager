package org.hmdms.hmmanager.db;

public class QueryArgument<T> {

    private T value;
    private QueryArgumentTypes type;

    public QueryArgument(T value, QueryArgumentTypes type) {
        this.value = value;
        this.type = type;
    }

    public QueryArgument(T value) {
        this.value = value;
        if (value instanceof String) {
            this.type = QueryArgumentTypes.STRING;
        } else if (value instanceof Integer) {
            this.type = QueryArgumentTypes.INT;
        }
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public QueryArgumentTypes getType() {
        return type;
    }

    public void setType(QueryArgumentTypes type) {
        this.type = type;
    }
}

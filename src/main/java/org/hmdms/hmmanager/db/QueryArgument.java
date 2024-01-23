package org.hmdms.hmmanager.db;

public class QueryArgument {

    private String value;
    private QueryArgumentTypes type;

    private String column;

    public QueryArgument(String value, QueryArgumentTypes type, String column) {
        this.value = value;
        this.type = type;
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public QueryArgumentTypes getType() {
        return type;
    }

    public void setType(QueryArgumentTypes type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "QueryArgument{" +
                "value='" + value + '\'' +
                ", type=" + type +
                ", column='" + column + '\'' +
                '}';
    }
}

package claudiosoft.dbabel.entity;

/**
 *
 * @author claudio.tortorelli
 */
public class Condition {

    private String field;
    private String operator;
    private String value;

    public Condition(String value) {
        this("id", "=", value);
    }

    public Condition(String field, String value) {
        this(field, "=", value);
    }

    public Condition(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

/**
 * Created by NF on 10/2/2016.
 */
public class CircuitControlElement {

    private String tag;

    private Integer flag;

    private Integer number;

    public CircuitControlElement(Integer flag, Integer number) {
        setFlag(flag);
        setNumber(number);
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}

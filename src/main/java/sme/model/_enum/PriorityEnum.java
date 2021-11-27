package sme.model._enum;

public enum PriorityEnum {
    Low(3),Medium(2),High(1);

    PriorityEnum(int i) {
        this.order = i;
    }
    private int order;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

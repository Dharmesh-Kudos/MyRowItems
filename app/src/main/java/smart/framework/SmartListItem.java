package smart.framework;

import java.util.ArrayList;

public class SmartListItem {

    private int itemLayout;
    private int[] itemViews;
    private ArrayList<Object> values = new ArrayList<Object>();
    @SuppressWarnings("rawtypes")
    private ArrayList<Class> clazz = new ArrayList<Class>();

    public SmartListItem() {
    }

    @SuppressWarnings("rawtypes")
    public SmartListItem(int itemLayout, int[] itemViews, ArrayList<Object> values, ArrayList<Class> clazz) {
        this.itemLayout = itemLayout;
        this.itemViews = itemViews;
        this.values = values;
        this.clazz = clazz;
    }

    public int getItemLayout() {
        return this.itemLayout;
    }

    public void setItemLayout(int itemLayout) {
        this.itemLayout = itemLayout;
    }

    public int[] getItemViews() {
        return this.itemViews;
    }

    public void setItemViews(int[] itemViews) {
        this.itemViews = itemViews;
    }

    public ArrayList<Object> getValues() {
        return this.values;
    }

    public void setValues(ArrayList<Object> values) {
        this.values = values;
    }

    @SuppressWarnings("rawtypes")
    public ArrayList<Class> getClasses() {
        return this.clazz;
    }

    @SuppressWarnings("rawtypes")
    public void setClasses(ArrayList<Class> clazz) {
        this.clazz = clazz;
    }

    public int getTotalViews() {
        return itemViews.length;
    }
}

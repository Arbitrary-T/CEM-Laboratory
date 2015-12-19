package cu.models;

/**
 * Created by T on 22/11/2015.
 */
public class Equipment
{
    private int itemID;
    private int itemCount;
    private boolean functional; //working (true)/ not working (false)
    private String itemName;
    private String itemCategory;

    public Equipment(int itemID, int itemCount, String itemName, String itemCategory, boolean functional)
    {
        setItemID(itemID);
        setItemCount(itemCount);
        setItemName(itemName);
        setItemCategory(itemCategory);
        setFunctional(functional);
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void setFunctional(boolean condition) {
        this.functional = condition;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
}

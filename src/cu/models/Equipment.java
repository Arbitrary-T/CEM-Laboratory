package cu.models;

/**
 * Created by T on 22/11/2015.
 */
public class Equipment
{
    public static final int AUTO_INCREMENT = 0;
    private int itemID;
    private boolean functional;
    private String itemName;
    private String itemCategory;
    private String partOfBundle;

    public Equipment(int itemID, String itemName, String itemCategory, boolean functional, String bundle)
    {
        setItemID(itemID);
        setItemName(itemName);
        setItemCategory(itemCategory);
        setFunctional(functional);
        setPartOfBundle(bundle);
    }
    public Equipment()
    {

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

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getPartOfBundle() {
        return partOfBundle;
    }
    public void setPartOfBundle(String partOfBundle) {
        this.partOfBundle = partOfBundle;
    }

    @Override
    public String toString()
    {
        String equipment = itemID + "\t" + itemName + "\t" + itemCategory + "\t" + functional + "\t" + partOfBundle;
        return equipment;
    }
}
package cu.models.equipment;

/**
 * Created by T on 22/11/2015.
 */
public class Equipment
{
    //A generic pojo.

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

    public boolean isFunctional()
    {
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
    public int hashCode()
    {
        return (7 * 31) + getItemID();
    }
    @Override
    public boolean equals(Object object)
    {
        if(object == null || getClass() != object.getClass())
        {
            return false;
        }
        return this.getItemID() == ((Equipment) object).getItemID();
    }

    @Override
    public String toString()
    {
        return "Item ID " + itemID + " - Item: "+ itemName+"\n";
    }

    public String getFunctionalWrapper()
    {
        if(isFunctional())
        {
            return "Functional";
        }
        else
        {
            return "Faulty";
        }
    }


}
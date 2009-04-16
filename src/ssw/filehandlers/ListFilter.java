/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.filehandlers;

/**
 *
 * @author gblouin
 */
public class ListFilter {
    private String Name = "",
                   Model = "",
                   Era = "",
                   Tech = "",
                   Level = "";
    private int MinTonnage = 0,
                MaxTonnage = 0,
                MinYear = 2750,
                MaxYear = 2750,
                MinBV = 0,
                MaxBV = 0;
    private float MinCost = 0.0f,
                  MaxCost = 0.0f;

    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getModel() {
        return Model;
    }
    public void setModel(String Model) {
        this.Model = Model;
    }
    public String getEra() {
        return Era;
    }
    public void setEra(String Era) {
        this.Era = Era;
    }
    public String getLevel() {
        return Level;
    }
    public void setLevel(String Level) {
        this.Level = Level;
    }
    public String getTech() {
        return Tech;
    }
    public void setTech(String Tech) {
        this.Tech = Tech;
    }
    public int getMinTonnage() {
        return MinTonnage;
    }
    public void setMinTonnage(int MinTonnage) {
        this.MinTonnage = MinTonnage;
    }
    public int getMaxTonnage() {
        return MaxTonnage;
    }
    public void setMaxTonnage(int MaxTonnage) {
        this.MaxTonnage = MaxTonnage;
    }
    public int getMinYear() {
        return MinYear;
    }
    public void setMinYear(int MinYear) {
        this.MinYear = MinYear;
    }
    public int getMaxYear() {
        return MaxYear;
    }
    public void setMaxYear(int MaxYear) {
        this.MaxYear = MaxYear;
    }
    public int getMinBV() {
        return MinBV;
    }
    public void setMinBV(int MinBV) {
        this.MinBV = MinBV;
    }
    public int getMaxBV() {
        return MaxBV;
    }
    public void setMaxBV(int MaxBV) {
        this.MaxBV = MaxBV;
    }
    public float getMinCost() {
        return MinCost;
    }
    public void setMinCost(float MinCost) {
        this.MinCost = MinCost;
    }
    public float getMaxCost() {
        return MaxCost;
    }
    public void setMaxCost(float MaxCost) {
        this.MaxCost = MaxCost;
    }

    public void setTonnage(int Min, int Max) {
        this.MinTonnage = Min;
        this.MaxTonnage = Max;
    }

    public void setBV(int Min, int Max) {
        this.MinBV = Min;
        this.MaxBV = Max;
    }

    public void setCost(float Min, float Max) {
        this.MinCost = Min;
        this.MaxCost = Max;
    }

    public void setYear(int Min, int Max ) {
        this.MinYear = Min;
        this.MaxYear = Max;
    }
}

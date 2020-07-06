package pine.android.flowerdict;

import java.util.ArrayList;
import java.util.TreeMap;

import android.os.Parcel;
import android.os.Parcelable;

public class Flower implements Parcelable {
    private String name, def;
    private TreeMap<String, String> alts;
    private ArrayList<String> aliases;

    public Flower(String name, String def, ArrayList<String> aliases) {
        this.name = name;
        this.def = def;
        this.aliases = aliases;
        this.alts = new TreeMap<String, String>();
    }

    public Flower(String name, String def) {
        this.name = name;
        this.def = def;
        this.alts = new TreeMap<String, String>();
        this.aliases = new ArrayList<>();
    }

    public Flower(Parcel in) {
        this.name = in.readString();
        this.def = in.readString();
        this.alts = (TreeMap<String, String>) in.readSerializable();
        this.aliases = (ArrayList<String>) in.readSerializable();
    }

    /*
    Returns whether this Flower is an "alt", aka a variation of a preexisting Flower
     */
    public boolean isAlt() {
        return name.indexOf(",") > -1 || name.indexOf("(") > -1;
    }

    /*
    Adds an alt to this Flower's map of alts
     */
    public void addAlt(String desc, String def) {
        alts.put(desc, def);
    }

    public String getName() {
        return name;
    }

    public String getDef() {
        return def;
    }

    public TreeMap<String, String> getAlts() {
        return alts;
    }

    public ArrayList<String> getAliases() { return aliases; }

    /*
    Returns whether one of this Flower's aliases matches the String alias
     */
    public boolean containsAlias(String alias) {
        for (String s : this.aliases) {
            if (s.equalsIgnoreCase(alias)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(def);
        out.writeSerializable(alts);
        out.writeSerializable(aliases);
    }

    public static final Parcelable.Creator<Flower> CREATOR = new Parcelable.Creator<Flower>() {
        public Flower createFromParcel(Parcel in) {
            return new Flower(in);
        }

        public Flower[] newArray(int size) {
            return new Flower[size];
        }
    };
}

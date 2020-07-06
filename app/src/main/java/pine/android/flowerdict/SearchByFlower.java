package pine.android.flowerdict;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.lang.Character.isWhitespace;

public class SearchByFlower extends AppCompatActivity {

    private static TreeMap<String, Flower> allFlowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button switchSearch = (Button)findViewById(R.id.switch_search);
        switchSearch.setText("Find by definition");
        AutoCompleteTextView box = (AutoCompleteTextView)findViewById(R.id.search_query);
        box.setHint("Search by typing in the name of a flower...");

        if (allFlowers == null) {
            allFlowers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            parseFlowers();
        }
        Set<String> flowerKeys = new TreeSet<>();
        flowerKeys.addAll(allFlowers.keySet());

        for (String flowerName : allFlowers.keySet()) {
            Flower f = allFlowers.get(flowerName);
            flowerKeys.addAll(f.getAliases());
        }

        String[] flowerNames = new String[flowerKeys.size()];
        flowerNames = flowerKeys.toArray(flowerNames);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, flowerNames);
        box.setAdapter(adapter);
    }

    /*
    Reads the file searchbyflower.txt and loads the resources into a TreeMap format.
     */
    public void parseFlowers() {
        try {
            // read the file
            InputStream stuff = getResources().openRawResource(R.raw.searchbyflower);
            Scanner scan = new Scanner(stuff);
            scan.useDelimiter("_*_");

            while (scan.hasNext()) {
                String word = scan.next();
                String def = scan.next();
                // eat up the extra newline left behind by scan.next()
                scan.nextLine();
                String rawAliases = scan.nextLine();

                if (!isWhitespace(word.charAt(0))) {
                    word = word.trim();
                    handleLine(word, def, rawAliases);
                }
                scan.useDelimiter("_*_|\n");
            }
        } catch (Exception e) {
            System.err.print("Exception: " + e.getMessage());
        }
    }

    /*
    Helper for parseFlowers() that interprets information from a given line of input that is meant
    to represent a flower. Modifies the TreeMap allFlowers.
     */
    public void handleLine(String word, String def, String aliases) {
        ArrayList<String> aliasesToAdd = new ArrayList<>();
        if (aliases.length() > 0) {
            aliases = aliases.substring(1, aliases.length() - 1);
            String[] otherNames = aliases.split(", ");
            for (String name : otherNames) {
                aliasesToAdd.add(name);
            }
        }

        if (word.contains(",")) {
            String[] splitWord = word.split(",");
            String commonName = splitWord[0];
            String altDesc = splitWord[1].trim();

            if (splitWord.length > 2) {
                for (int i = 2; i < splitWord.length; i++) {
                    altDesc += ", " + splitWord[i];
                }
            }

            if (allFlowers.get(commonName) == null) {
                allFlowers.put(commonName, new Flower(commonName, "", aliasesToAdd));
            }
            allFlowers.get(commonName).addAlt(altDesc, def);

        } else if (word.contains("(")) {
            String[] splitWord = word.split("\\(");
            String commonName = splitWord[0].trim();
            String altDesc = splitWord[1].substring(0, splitWord[1].length() - 1);

            if (allFlowers.get(commonName) == null) {
                allFlowers.put(commonName, new Flower(commonName, "", aliasesToAdd));
            }

            allFlowers.get(commonName).addAlt(altDesc, def);

        }
        // Even if it's an alt, flower still gets added as a standalone
        Flower currFlower = new Flower(word, def, aliasesToAdd);
        allFlowers.put(word, currFlower);
    }

    /*
    Given a Flower that is a variation of a preexisting Flower, returns the preexisting Flower.
     */
    public Flower findFlowerByAlt(Flower alt) {
        String altName = alt.getName();
        String common = "";

        if (altName.indexOf(",") > -1) {
            common = altName.substring(0, altName.indexOf(",")).trim();
        } else if (altName.indexOf("(") > -1) {
            common = altName.substring(0, altName.indexOf("(")).trim();
        } else {
            return null;
        }

        return allFlowers.get(common);
    }

    /*
    Given an alternate name for a Flower, return the Flower it is used for.
     */
    public Flower findFlowerByAlias(String alias) {
        Set<String> allFlowersKeySet = allFlowers.keySet();

        for (String key : allFlowersKeySet) {
            if (allFlowers.get(key).containsAlias(alias)) {
                return allFlowers.get(key);
            }
        }

        // maybe it's an alt but not written that way?
        String[] pieces = alias.split(" ");
        String altName = "";
        for (int i = pieces.length - 1; i > 0; i--) {
            altName += pieces[i].trim() + ", ";
        }
        altName += pieces[0];

        Flower found = findFlowerByAlt(new Flower(altName, ""));
        if (found == null && pieces.length > 1) {
            altName = pieces[1].trim() + pieces[0].trim();
        }
        found = findFlowerByAlt(new Flower(altName, ""));

        return found;
    }

    /*
    Searches for a match between a flower and the user's query when the user presses the button with id "search_button".
    Then, it redirects the user to the Def activity.
    */
    public void search(View V) {
        AutoCompleteTextView box = (AutoCompleteTextView)findViewById(R.id.search_query);
        String search = box.getText().toString();
        Flower found = null;

        if (allFlowers.containsKey(search)) {
            found = allFlowers.get(search);
        }

        if (found != null && found.isAlt()) {
            found = findFlowerByAlt(found);
        } else if (found == null) {
            found = findFlowerByAlias(search);
        }

        Intent intent = new Intent(this, Def.class);
        intent.putExtra("Flower", found);

        startActivity(intent);
        finish();
    }

    /*
    Switches to the activity SearchByDef when the user presses the button with id "switch_search".
    */
    public void switchSearch(View V) {
        Intent intent = new Intent(this, SearchByDef.class);
        startActivity(intent);
        finish();
    }

}

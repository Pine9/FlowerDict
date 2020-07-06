package pine.android.flowerdict;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Def extends AppCompatActivity {

    private Flower myFlower;
    private String myDefinition;
    private ArrayList<String> myResults;
    private boolean searchByDef = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_def);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // grab the extras
        Intent intent = getIntent();
        myFlower = intent.getParcelableExtra("Flower");
        myDefinition = intent.getStringExtra("Definition");
        myResults = intent.getStringArrayListExtra("FlowerResults");

        if (myDefinition == null) {
            // standard search-by-flower behavior
            createDictEntry();
        } else {
            // searching by definition
            searchByDef = true;
            listFlowers();
        }
    }

    /*
    Under the assumption that the user is searching by flower, constructs the UI
    to display the name, aliases, definition, and image of the found flower.
     */
    public void createDictEntry() {
        // set defaults just in case the flower wasn't found
        String flowerName = "Flower not found";
        String flowerDef = "";
        TreeMap<String, String> flowerAlts = new TreeMap<>();
        ArrayList<String> flowerAliases = new ArrayList<String>();

        if (myFlower != null) {
            flowerName = myFlower.getName();
            flowerDef = myFlower.getDef();
            flowerAlts = myFlower.getAlts();
            flowerAliases = myFlower.getAliases();

            fetchImage(flowerName.toLowerCase());
        }

        ((TextView) findViewById(R.id.word)).setText(flowerName);
        String toAdd = "";
        if (!flowerAliases.isEmpty()) {
            toAdd += "Also known as: ";
            for (int i = 0; i < flowerAliases.size(); i++) {
                if (i < flowerAliases.size() - 1) {
                    toAdd += flowerAliases.get(i) + ", ";
                } else {
                    toAdd += flowerAliases.get(i);
                }
            }
        }
        ((TextView)findViewById(R.id.aliases)).setText(toAdd);
        StringBuffer dictEntry = new StringBuffer();
        dictEntry = addToDef(dictEntry, flowerDef, flowerAlts);
        ((TextView)findViewById(R.id.def)).setText(Html.fromHtml(dictEntry.toString()));
    }

    /*
    Helper method for createDictEntry() that adds to a preexisting StringBuffer, def, and a map
    of alts to return a StringBuffer that represents the Flower definition.
     */
    public StringBuffer addToDef(StringBuffer currentEntry, String def, TreeMap<String, String> alts) {
        currentEntry.append(def);

        if (def.length() > 0) {
            currentEntry.append("<br/><br/>");
        }

        Set<String> altKeys = alts.keySet();
        TreeSet<String> orderedAltKeys = new TreeSet<>();
        orderedAltKeys.addAll(altKeys);

        for (String alt : orderedAltKeys) {
            currentEntry.append("<i>" + alt + "</i><br/>");
            currentEntry.append(alts.get(alt));
            currentEntry.append("<br/><br/>");
        }

        return currentEntry;
    }

    /*
    Under the assumption that the user is searching by definition, lists the flowers that match
    that definition.
     */
    public void listFlowers() {
        ((TextView) findViewById(R.id.word)).setText(myDefinition);
        String output = "No flowers found";

        if (myResults != null && !myResults.isEmpty()) {
            output = "Flowers that mean \"" + myDefinition + "\":\n";
            for (String flowerName : myResults) {
                output += "\n" + flowerName;
            }
        }

        ((TextView)findViewById(R.id.def)).setText(output);
        ((ImageView)findViewById(R.id.flower)).setVisibility(View.GONE);
    }

    /*
    Sets the image to the flower's match in R.drawable
     */
    public void fetchImage(String query) {
        ImageView image = (ImageView)findViewById(R.id.flower);
        image.setVisibility(View.VISIBLE);

        // if there are weird characters or spaces in the name, we need to make it more image-path-friendly
        if (query.indexOf(" ") > -1) {
            query = query.replace(" ", "_");
        }
        if (query.indexOf("'") > -1) {
            query = query.replace("'", "");
        }
        if (query.indexOf("-") > -1) {
            query = query.replace("-", "");
        }

        int id = getResources().getIdentifier(query, "drawable", getPackageName());
        image.setImageResource(id);
        image.setContentDescription(query);
    }

    /*
    Helper for exit(View V) that sends the user back to the previous activity.
     */
    public void stop() {
        Intent intent;
        if (searchByDef) {
            intent = new Intent(this, SearchByDef.class);
        } else {
            intent = new Intent(this, SearchByFlower.class);
        }
        startActivity(intent);
        finish();
    }

    /*
    Exits this activity when the user presses the button with id "exit".
     */
    public void exit(View V) {
        stop();
    }
}

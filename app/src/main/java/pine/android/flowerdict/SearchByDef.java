package pine.android.flowerdict;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import static java.lang.Character.isWhitespace;

public class SearchByDef extends AppCompatActivity {
    private static TreeMap<String, ArrayList<String>> allDefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button switchSearch = (Button)findViewById(R.id.switch_search);
        switchSearch.setText("Find by flower");
        AutoCompleteTextView box = (AutoCompleteTextView)findViewById(R.id.search_query);
        box.setHint("Search by typing in a definition...");

        if (allDefs == null) {
            allDefs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            parseDefs();
        }
        String[] defs = new String[allDefs.keySet().size()];
        defs = allDefs.keySet().toArray(defs);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, defs);
        box.setAdapter(adapter);
    }

    /*
    Reads the file searchbydef.txt and loads the resources into a TreeMap format.
     */
    public void parseDefs() {
        try {
            // read the file
            InputStream stuff = getResources().openRawResource(R.raw.searchbydef);
            Scanner scan = new Scanner(stuff);
            scan.useDelimiter("_*_");

            while (scan.hasNext()) {
                String def = scan.next();
                String word = scan.next();

                if (!isWhitespace(word.charAt(0))) {
                    def = def.trim();
                    ArrayList<String> toAdd = new ArrayList<>();
                    toAdd.add(word);
                    if (allDefs.get(def) == null) {
                        allDefs.put(def, toAdd);
                    } else {
                        allDefs.get(def).addAll(toAdd);
                    }
                }
                scan.useDelimiter("_*_|\n");
            }
        } catch (Exception e) {
            System.err.print("Exception: " + e.getMessage());
        }
    }

    /*
    Searches for a match between a definition and the user's query when the user presses the button with id "search_button".
    Then, it redirects the user to the Def activity.
     */
    public void search(View V) {
        AutoCompleteTextView box = (AutoCompleteTextView)findViewById(R.id.search_query);
        String search = box.getText().toString().toLowerCase();
        ArrayList<String> results = null;

        if (allDefs.containsKey(search)) {
            results = allDefs.get(search);
        }

        Intent intent = new Intent(this, Def.class);
        intent.putExtra("Definition", search);
        intent.putExtra("FlowerResults", results);

        startActivity(intent);
        finish();
    }

    /*
    Switches to the activity SearchByDef when the user presses the button with id "switch_search".
    */
    public void switchSearch(View V) {
        Intent intent = new Intent(this, SearchByFlower.class);
        startActivity(intent);
        finish();
    }
}

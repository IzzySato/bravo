package ca.bcit.bravo;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Tab2Fragment} factory method to
 * create an instance of this fragment.
 */
public class Tab2Fragment extends Fragment {
    Context context;
    ArrayList<String> stuff;
    TableLayout tableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_tab2, null, false);
        tableLayout = RootView.findViewById(R.id.mytable);
        stuff = new ArrayList<>();
        context = getActivity();
        new doIT().execute();
        // Inflate the layout for this fragment
        return RootView;
    }
    public class doIT extends AsyncTask<Void,Void,Void> {
        String words;
        String active;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("https://www2.gov.bc.ca/gov/content/environment/air-land-water/air/air-quality/air-advisories").get();

                // With the document fetched, we use JSoup's title() method to fetch the title
                System.out.printf("Title: %s\n", doc.title());

                words = doc.text();


                Element element = doc.select("tbody").first();
                for(Element tr: element.select("tr")) {

                    for(Element td: tr.select("td")){
                        Log.d("myTag", "DoInBackground " + td.text() + " ");
                        if(td.text().equals("")) {
                            stuff.add("None");
                        } else {
                            stuff.add(td.text());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for(String item: stuff) {
                System.out.println(item);
            }
            tableLayout.setPadding(20,15, 5,0);

            System.out.println("Testing the logic");
            for(int i =0; i < stuff.size(); i = i + 4) {
                //DebugCode
                StringBuilder test = new StringBuilder();
                //Debug code end
                TableRow row = new TableRow(requireContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                for(int j = i; j < i + 4 ; j++) {
                    TextView tv = new TextView(requireContext());
                    tv.setText(stuff.get(j));
                    tv.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.cell_shape));
                    tv.setPadding(15, 15, 15,15);
                    tv.setTextColor(Color.parseColor("#000000"));
                    row.addView(tv);
                    //DebugCode
                    test.append(stuff.get(j)).append(" ");
                    //Debug code end
                }
                tableLayout.addView(row);
            }
        }
    }
}
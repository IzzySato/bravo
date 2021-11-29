package ca.bcit.bravo;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AirQualityFragment} factory method to
 * create an instance of this fragment.
 */
public class AirQualityFragment extends Fragment {
    Context context;
    ArrayList<String> values;
    TableLayout tableLayout;
    ProgressBar _progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_airquality, null, false);
        tableLayout = RootView.findViewById(R.id.mytable);
        _progressBar = RootView.findViewById(R.id.progressBar1);
        values = new ArrayList<>();
        context = getActivity();
        new doIT().execute();
        return RootView;
    }
    public class doIT extends AsyncTask<Void,Void,Void> {
        String words;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                _progressBar.setVisibility(View.VISIBLE);
                Document doc = Jsoup.connect(getResources().getString(R.string.air_quality_url)).get();

                words = doc.text();

                Element element = doc.select("tbody").first();
                for(Element tr: element.select("tr")) {
                    for(Element td: tr.select("td")){
                        if(td.text().equals("")) {
                            values.add("None");
                        } else {
                            values.add(td.text());
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
            _progressBar.setVisibility(View.GONE);
            tableLayout.setPadding(20,15, 5,0);

            for(int i =0; i < values.size(); i = i + 4) {
                TableRow row = new TableRow(requireContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                for(int j = i; j < i + 4 ; j++) {
                    TextView tv = new TextView(requireContext());
                    tv.setText(values.get(j));
                    if(i > 3) {
                        tv.setWidth(50);
                        tv.setHeight(200);
                    }
                    tv.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.cell_shape));
                    tv.setPadding(15, 15, 15,15);
                    tv.setTextColor(Color.parseColor("#000000"));
                    row.addView(tv);
                }
                tableLayout.addView(row);
            }
        }
    }
}
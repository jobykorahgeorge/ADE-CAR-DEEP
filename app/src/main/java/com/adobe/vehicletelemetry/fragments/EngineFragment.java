package com.adobe.vehicletelemetry.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adobe.vehicletelemetry.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EngineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EngineFragment extends Fragment
                implements OnChartValueSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RelativeLayout rpmLayout;
    private RelativeLayout speedLayout;
    private RelativeLayout rpmOvershootLayout;
    private RelativeLayout loadLayout;
    private RelativeLayout temperatureLayout;
    private RelativeLayout fuelCostLayout;

    private LineChart mChart;
    int j,k = 0;

    double costInc = 0.01;

    public EngineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EngineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EngineFragment newInstance(String param1, String param2) {
        EngineFragment fragment = new EngineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_engine, container, false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {

        rpmLayout = root.findViewById(R.id.engine_rpm_layout);
        speedLayout = root.findViewById(R.id.engine_speed_layout);
        rpmOvershootLayout = root.findViewById(R.id.engine_rpm_overshoot_layout);
        loadLayout = root.findViewById(R.id.engine_load_layout);
        temperatureLayout = root.findViewById(R.id.engine_temperature_layout);
        fuelCostLayout = root.findViewById(R.id.fuel_cost_layout);
        mChart = root.findViewById(R.id.engine_chart);
        setupChart();


        displayStat("Avg. RPM", R.drawable.ic_rpm, "0", "rpm", rpmLayout);
        displayStat("Fuel Rate", R.drawable.ic_fuel_rate, "0", "gal/hr", speedLayout);

        displayStat("RPM Overshoot", R.drawable.ic_rpm_overshoot, "0", "Times", rpmOvershootLayout);
        displayStat("Engine Load", R.drawable.ic_engine_load, "0.0", "%", loadLayout);
        displayStat("Coolant Temp", R.drawable.ic_temperature, "132", (char)0x00B0 + "F", temperatureLayout);
        displayStat("Running Cost", R.drawable.ic_dollar, "0", " ", fuelCostLayout);
    }

    private void displayStat(String statName, int iconResId, String statValue, String unit, RelativeLayout statLayout) {

        TextView nameText = statLayout.findViewById(R.id.stat_name_text);
        TextView valueText = statLayout.findViewById(R.id.stat_value_text);
        TextView unitText = statLayout.findViewById(R.id.stat_unit_text);
        ImageView iconView = statLayout.findViewById(R.id.stat_icon_image);

        nameText.setText(statName);
        valueText.setText(statValue);
        unitText.setText(unit);
        iconView.setImageResource(iconResId);
    }

    private void setupChart() {
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);

        // add an empty data object
        mChart.setData(new LineData());
//        mChart.getXAxis().setDrawLabels(false);
//        mChart.getXAxis().setDrawGridLines(false);

        mChart.setBackgroundColor(Color.TRANSPARENT);



        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
//        xAxis.setGridColor(Color.RED);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(120f);
        leftAxis.setAxisMinimum(0f);
//        leftAxis.setGridColor(Color.RED);
        mChart.getAxisRight().setEnabled(false);

        mChart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override   // OnChartValueSelectedListener
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override   // OnChartValueSelectedListener
    public void onNothingSelected() {

    }

    public void showStats(double rpm, double speed, double rpmOvershoot, double load, double coolantTemp) {

        int m = (int) Math.round(rpmOvershoot);

        j = j+1;
        if(j % 10 == 0){
            costInc=costInc + 0.01;
        }
        double saveRPM = rpm;


        if(rpm>=70 && rpm<=90){
            rpm= rpm+2500;
        }
        else if(rpm>90){
            rpm = rpm+3300;
        }
        if(rpm<70){
            rpm =rpm+1200;
        }
        DecimalFormat f = new DecimalFormat("##.00");
        ((TextView) rpmLayout.findViewById(R.id.stat_value_text)).setText(f.format(rpm));



        if(saveRPM==0){
            ((TextView) speedLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.18,0.24)));
        }
        else if(saveRPM>=1 && saveRPM<=20){
            ((TextView) speedLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.18,0.24)));
        }
        else if(saveRPM>20 && saveRPM<=40){
            ((TextView) speedLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.24,0.43)));
        }
        else if(saveRPM>40 && saveRPM<=60){
            ((TextView) speedLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.43,0.65)));
        }
        else if(saveRPM>60 && saveRPM<=80){
            ((TextView) speedLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.65,0.85)));
        }
        else if(saveRPM>80 && saveRPM<=100){
            ((TextView) speedLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.85,0.91)));
        }
        else {
            ((TextView) speedLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.85,0.91)));
        }



//
//        ((TextView) speedLayout.findViewById(R.id.stat_value_text)).setText(f.format(speed));

        ((TextView) rpmOvershootLayout.findViewById(R.id.stat_value_text)).setText(Integer.toString(m));
        ((TextView) loadLayout.findViewById(R.id.stat_value_text)).setText(f.format(saveRPM));
        ((TextView) temperatureLayout.findViewById(R.id.stat_value_text)).setText(f.format(coolantTemp));
        ((TextView) fuelCostLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", costInc));

        addEntry(saveRPM);
    }

        public double getRandom(double min,double max){
        Random rand = new Random();
        double finalValue = rand.nextDouble() * (max-min) + min;
        return finalValue;
    }


    private void addEntry(double rpm) {

        LineData data = mChart.getData();

        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }

        // choose a random dataSet
        int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
        float yValue = (float) (Math.random() * 10) + 50f;

        data.addEntry(new Entry(data.getDataSetByIndex(randomDataSetIndex).getEntryCount(), (float) rpm), randomDataSetIndex);
        data.notifyDataChanged();

        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();

        mChart.setVisibleXRangeMaximum(6);
        //mChart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
        mChart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null,"Acceleration");
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);
        set.setColor(Color.WHITE);
        set.setCircleColor(Color.WHITE);
        set.setValueTextColor(Color.WHITE);
        set.setColor(Color.rgb(240, 99, 99));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10f);

        /*if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
            set.setFillDrawable(drawable);
        }*/

        return set;
    }

    public void loadHistory() {

//        List<Integer> accList = Arrays.asList(26, 90, 36, 13, 50, 80, 100, 15, 45, 90, 10, 100, 100, 50, 70, 40);
        List<Integer> accList = Arrays.asList(20, 90, 36, 13, 50, 80, 100, 15, 45, 90, 10, 100, 100, 50, 70, 40);
//        List<Integer> accList = Arrays.asList(1210, 1235, 1365, 1365, 1470, 1475, 1578, 1279, 1280, 1460, 1548, 1570, 1450, 1455, 1440, 1460);

        for (int acc : accList) {
            addEntry(acc);
        }

    }
}

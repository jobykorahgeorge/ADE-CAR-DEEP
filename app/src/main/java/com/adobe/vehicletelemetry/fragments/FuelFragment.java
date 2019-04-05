package com.adobe.vehicletelemetry.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adobe.vehicletelemetry.R;
import com.adobe.vehicletelemetry.model.Wheel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class FuelFragment extends Fragment
        implements OnChartValueSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RelativeLayout mileageLayout;
    private RelativeLayout fuelLayout;
    private RelativeLayout distanceLayout;
    private RelativeLayout fuelRateLayout;
    private RelativeLayout durationLayout;
    private RelativeLayout costLayout;

    private RelativeLayout rpmLayout;
    private RelativeLayout speedLayout;
    private RelativeLayout rpmOvershootLayout;
    private RelativeLayout loadLayout;
    private RelativeLayout temperatureLayout;

    private LineChart mChart;
    private FuelListener listener;

    int overShootCount = 0;
    double accletarionPre = 0;

    double costInc = 0.01;
    int secondDistance=0;


    public FuelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FuelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FuelFragment newInstance(String param1, String param2) {
        FuelFragment fragment = new FuelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (FuelListener) context;
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
        View root = inflater.inflate(R.layout.fragment_fuel, container, false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {

        mileageLayout = root.findViewById(R.id.fuel_mileage_layout);
        fuelLayout = root.findViewById(R.id.fuel_consumed_layout);
        distanceLayout = root.findViewById(R.id.fuel_distance_layout);
        fuelRateLayout = root.findViewById(R.id.fuel_rate_layout);
        durationLayout = root.findViewById(R.id.fuel_duration_layout);
        costLayout = root.findViewById(R.id.fuel_cost_layout);


        rpmLayout = root.findViewById(R.id.engine_rpm_layout);
        speedLayout = root.findViewById(R.id.engine_speed_layout);
        rpmOvershootLayout = root.findViewById(R.id.engine_rpm_overshoot_layout);
        loadLayout = root.findViewById(R.id.engine_load_layout);
        temperatureLayout = root.findViewById(R.id.engine_temperature_layout);

        mChart = root.findViewById(R.id.fuel_chart);
        setupChart();

        displayStat("Fuel Economy", R.drawable.ic_milage, "9.44", "mpg", mileageLayout);
        displayStat("Fuel Consumed", R.drawable.ic_fuel_consumed, "1.25", "gallons", fuelLayout);
        displayStat("Distance Covered", R.drawable.ic_distance, "11.8", "miles", distanceLayout);

        displayStat("Fuel Rate", R.drawable.ic_fuel_rate, "0.00", "gal/hr", fuelRateLayout);
        displayStat("Trip Duration", R.drawable.ic_duration, "00:37", "min", durationLayout);
        displayStat("Running Cost", R.drawable.ic_dollar, "0.00", " ", costLayout);

        Log.d("METER", " totalDist>>>>>>:11.8");

        displayStat("Avg. RPM", R.drawable.ic_rpm, "0", "rpm", rpmLayout);
        displayStat("RPM Overshoot", R.drawable.ic_rpm_overshoot, "0", "Times", rpmOvershootLayout);
        displayStat("Engine Load", R.drawable.ic_engine_load, "0.0", "%", loadLayout);
        displayStat("Coolant Temp", R.drawable.ic_temperature, "208", (char)0x00B0 + "F", temperatureLayout);

        listener.onFuelInitialized();
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

        if(unitText.getText().toString().equals("mpg")){
            valueText.setTextColor(Color.RED);
            valueText.setTextSize(27);
        }
        if(unitText.getText().toString().equals("per mile")){
            valueText.setTextColor(Color.GREEN);
            valueText.setTextSize(27);
        }
        if(unitText.getText().toString().equals("gph")){
            valueText.setTextSize(27);
        }

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
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        mChart.getAxisRight().setEnabled(false);

        mChart.invalidate();
    }

    @Override   // OnChartValueSelectedListener
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override   // OnChartValueSelectedListener
    public void onNothingSelected() {

    }

    public void updateStats(double totalDist, double milage, double fuel, double time,double acc) {
        Log.d("METERz", " totalDist:" + totalDist);
        if (distanceLayout != null) {
            totalDist = totalDist;
            DecimalFormat f = new DecimalFormat("##.00");

            double accleration = acc;
            double difference = accleration-accletarionPre;

            if(difference<0){
                difference = difference*(-1);
            }

            if(difference>40){
                overShootCount=overShootCount+1;
                accletarionPre=accleration;
            }

            ((TextView) rpmOvershootLayout.findViewById(R.id.stat_value_text)).setText(Integer.toString(Math.round(overShootCount/2)));



            Log.d("FUEL", "Dist:" + totalDist + " Milage:" + milage + " Fuel:" + fuel + " Duration:" + time + " cost: " + (totalDist / fuel * 75) + " FuelRate:" + (fuel / time * 60));

            ((TextView) distanceLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", totalDist));
            ((TextView) mileageLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", milage));

            ((TextView) fuelLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", fuel));
            String duration = String.format("%02d", (int) (time / 60)) + ":" + String.format("%02d", (int) (time % 60));
            ((TextView) durationLayout.findViewById(R.id.stat_value_text)).setText(duration);


            secondDistance = secondDistance +1;
            if(secondDistance % 10 == 0){
                costInc = costInc + 0.01;
            }


            ((TextView) costLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", costInc));
//            int fuelPrice = 75;
//            double costkm = fuel * fuelPrice / totalDist;
//            ((TextView) costLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", costkm));

//            if (time == 0) {
//                ((TextView) fuelRateLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", 0.0));
//            } else {
//                ((TextView) fuelRateLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", (fuel / time * 60)));
//            }

            if(acc==0){
                ((TextView) fuelRateLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", 0.0));
            }
            else if(acc>=1 && acc<=20){
                ((TextView) fuelRateLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.18,0.24)));
            }
            else if(acc>20 && acc<=40){
                ((TextView) fuelRateLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.24,0.43)));
            }
            else if(acc>40 && acc<=60){
                ((TextView) fuelRateLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.43,0.65)));
            }
            else if(acc>60 && acc<=80){
                ((TextView) fuelRateLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.65,0.85)));
            }
            else if(acc>80 && acc<=100){
                ((TextView) fuelRateLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.85,0.91)));
            }
            else {
                ((TextView) fuelRateLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f", getRandom(0.85,0.91)));
            }



            //addEntry(accleration);
            ((TextView) loadLayout.findViewById(R.id.stat_value_text)).setText(f.format(acc));

            if(acc>=70 && acc<=90){
                acc= acc+2500;
            }
            else if(acc>90){
                acc = acc+3300;
            }
            if(acc<70){
                acc =acc+1200;
            }
            ((TextView) rpmLayout.findViewById(R.id.stat_value_text)).setText(String.format("%.2f",acc));





        }
    }

    public double getRandom(double min,double max){
        Random rand = new Random();
        double finalValue = rand.nextDouble() * (max-min) + min;
        return finalValue;
    }

    int counter = 0;

    public void updateWheelStats(Wheel wheel) {
        Log.d("TEST", "TS: " + wheel.getTimestamp());
        addEntry(wheel.getAcc());

        /*counter = 0;

        //Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      if (counter == wheelData.size() - 1) {
                                          listener.onWheelDataConsumed();
                                      }

                                      if(counter < wheelData.size()) {
                                          Log.d("TEST", "ACC: " + wheelData.get(counter).getAcc());
                                          addEntry(wheelData.get(counter).getAcc());
                                      }

                                      counter++;
                                  }

                              },
                //Set how long before to start calling the TimerTask (in milliseconds)
                0,
                //Set the amount of time between each execution (in milliseconds)
                50);*/
    }

    private void addEntry(double value) {

        LineData data = mChart.getData();

        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }

        // choose a random dataSet
        int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
        //double yValue = (double) (Math.random() * 10) + 50f;

        data.addEntry(new Entry(data.getDataSetByIndex(randomDataSetIndex).getEntryCount(), (float) value), randomDataSetIndex);
        data.notifyDataChanged();

        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();

        mChart.setVisibleXRangeMaximum(6);
        //mChart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
        mChart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
        mChart.setBackgroundColor(Color.TRANSPARENT);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        mChart.getAxisRight().setEnabled(false);
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Acceleration");
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

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            /*Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
            set.setFillDrawable(drawable);*/
        }

        return set;
    }

    public void loadHistory() {

        List<Integer> accList = Arrays.asList(10, 35, 65, 65, 70, 75, 78, 79, 80, 60, 48, 70, 50, 55, 40, 60);

        for (int acc : accList) {
            addEntry(acc);
        }
    }

    public interface FuelListener {
        void onFuelInitialized();
        //void onWheelDataConsumed();
    }
}

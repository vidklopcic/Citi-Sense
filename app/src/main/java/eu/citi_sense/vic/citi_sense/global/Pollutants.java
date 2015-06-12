package eu.citi_sense.vic.citi_sense.global;
import android.animation.ArgbEvaluator;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import eu.citi_sense.vic.citi_sense.R;
import eu.citi_sense.vic.citi_sense.global.Databases.Measurement;
import eu.citi_sense.vic.citi_sense.global.Databases.Station;

public class Pollutants {
    public int nOfPollutants = 8;
    public static final int CO = 1;
    public static final int CO2 = 2;
    public static final int H2O = 3;
    public static final int NO = 4;
    public static final int O3 = 5;
    public static final int PM10 = 6;
    public static final int PM2_5 = 7;
    public static final int SO2 = 8;
    private static final HashMap<Integer, String> unicodeNames;
    public static final HashMap<Integer, String> pollutantNames;
    public static final HashMap<String, Integer> pollutantIndexes;
    public ArrayList<Station> stations;
    static {
        pollutantNames = new HashMap<>();
        pollutantNames.put(CO, "CO");
        pollutantNames.put(CO2, "CO2");
        pollutantNames.put(H2O, "H2O");
        pollutantNames.put(NO, "NO");
        pollutantNames.put(O3, "O3");
        pollutantNames.put(PM10, "PM10");
        pollutantNames.put(PM2_5, "PM2_5");
        pollutantNames.put(SO2, "SO2");

        unicodeNames = new HashMap<>();
        unicodeNames.put(CO, "CO");
        unicodeNames.put(CO2, "CO₂");
        unicodeNames.put(H2O, "H₂O");
        unicodeNames.put(NO, "NO");
        unicodeNames.put(O3, "O₃");
        unicodeNames.put(PM10, "PM₁₀");
        unicodeNames.put(PM2_5, "PM₂.₅");
        unicodeNames.put(SO2, "SO₂");

        pollutantIndexes = new HashMap<>();
        pollutantIndexes.put("CO", CO);
        pollutantIndexes.put("CO2", CO2);
        pollutantIndexes.put("H2O", H2O);
        pollutantIndexes.put("NO", NO);
        pollutantIndexes.put("O3", O3);
        pollutantIndexes.put("PM10", PM10);
        pollutantIndexes.put("PM2_5", PM2_5);
        pollutantIndexes.put("SO2", SO2);
    }

    public Integer icon;
    public Integer description;
    public Integer aqi;
    public Integer current;
    public Integer color;
    public Integer color_pressed;

    HashMap<Integer, Integer> pollutantsAqi = new HashMap<>();

    private Context context;
    public Pollutants(Context context) {
        this.context = context;
        initializeAqis();
    }

    public void initializeAqis() {
        pollutantsAqi.put(CO, 80);
        pollutantsAqi.put(CO2, 20);
        pollutantsAqi.put(H2O, 0);
        pollutantsAqi.put(NO, 31);
        pollutantsAqi.put(O3, 20);
        pollutantsAqi.put(PM10, 105);
        pollutantsAqi.put(PM2_5, 150);
        pollutantsAqi.put(SO2, 75);
    }

    public PollutionCallback pollutionCallback;

    public interface PollutionCallback {
        void pollutantChanged();

        void pollutantUpdated(int pollutant);
    }

    public void setPollutionChangeListener(PollutionCallback callback) {
        pollutionCallback = callback;
    }

    public void removePollutionChangeListener() {
        pollutionCallback = null;
    }

    public void setPollutant(String pollutant) {
        setPollutant(pollutantIndexes.get(pollutant));
    }

    public void setPollutant(int pollutant) {
        switch (pollutant) {
            case 1:
                setCO();
                break;
            case 2:
                setCO2();
                break;
            case 3:
                setH2O();
                break;
            case 4:
                setNO();
                break;
            case 5:
                setO3();
                break;
            case 6:
                setPM10();
                break;
            case 7:
                setPM2_5();
                break;
            case 8:
                setSO2();
                break;
        }
    }

    public Pollutants getPollutant(String pollutant) {
        return getPollutant(pollutantIndexes.get(pollutant));
    }

    public Pollutants getPollutant(int pollutant) {
        Pollutants p = new Pollutants(context);
        switch (pollutant) {
            case 1:
                p.setCO();
                return p;
            case 2:
                p.setCO2();
                return p;
            case 3:
                p.setH2O();
                return p;
            case 4:
                p.setNO();
                return p;
            case 5:
                p.setO3();
                return p;
            case 6:
                p.setPM10();
                return p;
            case 7:
                p.setPM2_5();
                return p;
            case 8:
                p.setSO2();
                return p;
            default:
                return p;
        }
    }

    private void setCO() {
        icon = R.drawable.ic_co;
        description = R.string.CO_description;
        aqi = pollutantsAqi.get(CO);
        current = 1;
        stations = new ArrayList<>(
                Station.find(Station.class, "CO = ?", "1")
        );
        afterChange();
    }
    private void setCO2() {
        icon = R.drawable.ic_co2;
        description = R.string.CO2_description;
        aqi = pollutantsAqi.get(CO2);
        current = 2;
        stations = new ArrayList<>(
                Station.find(Station.class, "CO2 = ?", "1")
        );
        afterChange();
    }
    private void setH2O() {
        icon = R.drawable.ic_h2o;
        description = R.string.H2O_description;
        aqi = pollutantsAqi.get(H2O);
        current = 3;
        stations = new ArrayList<>(
                Station.find(Station.class, "H2O = ?", "1")
        );
        afterChange();
    }
    private void setNO() {
        icon = R.drawable.ic_no;
        description = R.string.NO_description;
        aqi = pollutantsAqi.get(NO);
        current = 4;
        stations = new ArrayList<>(
                Station.find(Station.class, "NO = ?", "1")
        );
        afterChange();
    }
    private void setO3() {
        icon = R.drawable.ic_o3;
        description = R.string.O3_description;
        aqi = pollutantsAqi.get(O3);
        current = 5;
        stations = new ArrayList<>(
                Station.find(Station.class, "O3 = ?", "1")
        );
        afterChange();
    }
    private void setPM10() {
        icon = R.drawable.ic_pm10;
        description = R.string.PM10_description;
        aqi = pollutantsAqi.get(PM10);
        current = 6;
        stations = new ArrayList<>(
                Station.find(Station.class, "PM10 = ?", "1")
        );
        afterChange();
    }
    private void setPM2_5() {
        icon = R.drawable.ic_pm2;
        description = R.string.PM2_5_description;
        aqi = pollutantsAqi.get(PM2_5);
        current = 7;
        stations = new ArrayList<>(
                Station.find(Station.class, "PM25 = ?", "1")
        );
        afterChange();
    }
    private void setSO2() {
        icon = R.drawable.ic_so2;
        description = R.string.SO2_description;
        aqi = pollutantsAqi.get(SO2);
        current = 8;
        stations = new ArrayList<>(
                Station.find(Station.class, "SO2 = ?", "1")
        );
        afterChange();
    }

    private void afterChange() {
        int[] colors = getColors(aqi);
        color = colors[0];
        color_pressed = colors[1];
        if (pollutionCallback != null) {
            pollutionCallback.pollutantChanged();
        }
    }

    private int[] getColors(int cAqi) {
        int proportion;
        int start_color;
        int end_color;
        int start_color_pressed;
        int end_color_pressed;
        if (cAqi < 51) {
            proportion = cAqi*2;
            start_color = context.getResources().getColor(R.color.aqi_good);
            end_color = context.getResources().getColor(R.color.aqi_moderate);
            start_color_pressed = context.getResources().getColor(R.color.aqi_good_pressed);
            end_color_pressed = context.getResources().getColor(R.color.aqi_moderate_pressed);
        } else if (cAqi < 101) {
            proportion = (cAqi-50)*2;
            start_color = context.getResources().getColor(R.color.aqi_moderate);
            end_color = context.getResources().getColor(R.color.aqi_unhealthy_for_sensitive);
            start_color_pressed = context.getResources().getColor(R.color.aqi_moderate_pressed);
            end_color_pressed = context.getResources().getColor(R.color.aqi_unhealthy_for_sensitive_pressed);
        } else if (cAqi < 151) {
            proportion = (cAqi-100)*2;
            start_color = context.getResources().getColor(R.color.aqi_unhealthy_for_sensitive);
            end_color = context.getResources().getColor(R.color.aqi_unhealthy);
            start_color_pressed = context.getResources().getColor(R.color.aqi_unhealthy_for_sensitive_pressed);
            end_color_pressed = context.getResources().getColor(R.color.aqi_unhealthy_pressed);
        } else if (cAqi < 201) {
            proportion = (cAqi-150)*2;
            start_color = context.getResources().getColor(R.color.aqi_unhealthy);
            end_color = context.getResources().getColor(R.color.aqi_very_unhealthy);
            start_color_pressed = context.getResources().getColor(R.color.aqi_unhealthy_pressed);
            end_color_pressed = context.getResources().getColor(R.color.aqi_very_unhealthy_pressed);
        } else if (cAqi < 301) {
            proportion = cAqi-200;
            start_color = context.getResources().getColor(R.color.aqi_very_unhealthy);
            end_color = context.getResources().getColor(R.color.aqi_hazardous);
            start_color_pressed = context.getResources().getColor(R.color.aqi_very_unhealthy_pressed);
            end_color_pressed = context.getResources().getColor(R.color.aqi_hazardous_pressed);
        } else {
            return new int[]{
                    context.getResources().getColor(R.color.aqi_hazardous),
                    context.getResources().getColor(R.color.aqi_hazardous_pressed)};
        }

        int color = interpolateColor(
                start_color, end_color, proportion);
        int colorPressed = interpolateColor(
                start_color_pressed, end_color_pressed, proportion);
        return new int[]{color, colorPressed};
    }

    private int interpolateColor(int a, int b, int percentage) {
        float proportion = (float) (percentage / 100.0);
        return (int) new ArgbEvaluator().evaluate(proportion, a, b);
    }

    public void updatePollutant(String pollutant, int aqi) {
        updatePollutant(pollutantIndexes.get(pollutant), aqi);
    }



    public void updatePollutant(int pollutant, int aqi) {
        pollutantsAqi.put(pollutant, aqi);
        if (pollutionCallback != null) {
            pollutionCallback.pollutantUpdated(pollutant);
        }
    }

    public HashMap<Station, ArrayList<Measurement>> getAllMeasurements(Integer startTime, Integer endTime) {
        HashMap<Station, ArrayList<Measurement>> measurements = new HashMap<>();
        for(Station station : stations) {
            measurements.put(station, station.getMeasurements(startTime, endTime));
        }
        return measurements;
    }

    public ArrayList<Measurement> getMeasurements(Station station, Integer startTime, Integer endTime) {
        return station.getMeasurements(startTime, endTime);
    }

    public String getUnicodeName(int pollutant) {
        return unicodeNames.get(pollutant);
    }
}

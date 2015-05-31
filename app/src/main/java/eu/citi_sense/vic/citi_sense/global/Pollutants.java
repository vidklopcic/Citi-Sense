package eu.citi_sense.vic.citi_sense.global;
import java.util.ArrayList;

import eu.citi_sense.vic.citi_sense.R;

public class Pollutants {
    public int nOfPollutants = 8;
    public int CO = 1;
    public int CO2 = 2;
    public int H2O = 3;
    public int NO = 4;
    public int O3 = 5;
    public int PM10 = 6;
    public int PM2_5 = 7;
    public int SO2 = 8;
    
    public int icon;
    public int description;
    public int aqi;
    public int current;
    public int color;
    public int color_pressed;

    public int COAqi = 0;
    public int CO2Aqi = 100;
    public int H2OAqi = 200;
    public int NOAqi = 300;
    public int O3Aqi = 400;
    public int PM10Aqi = 500;
    public int PM2_5Aqi = 50;
    public int SO2Aqi = 10;

    public ArrayList<PollutionCallback> pollutionCallbacks = new ArrayList<>();

    public interface PollutionCallback {
        void onClick();
    }

    public void setPollutionChangeListener(PollutionCallback callback) {
        pollutionCallbacks.add(callback);
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

    public Pollutants getPollutant(int pollutant) {
        Pollutants p = new Pollutants();
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
        aqi = COAqi;
        current = 1;
        afterChange();
    }
    private void setCO2() {
        icon = R.drawable.ic_co2;
        description = R.string.CO2_description;
        aqi = CO2Aqi;
        current = 2;
        afterChange();
    }
    private void setH2O() {
        icon = R.drawable.ic_h2o;
        description = R.string.H2O_description;
        aqi = H2OAqi;
        current = 3;
        afterChange();
    }
    private void setNO() {
        icon = R.drawable.ic_no;
        description = R.string.NO_description;
        aqi = NOAqi;
        current = 4;
        afterChange();
    }
    private void setO3() {
        icon = R.drawable.ic_o3;
        description = R.string.O3_description;
        aqi = O3Aqi;
        current = 5;
        afterChange();
    }
    private void setPM10() {
        icon = R.drawable.ic_pm10;
        description = R.string.PM10_description;
        aqi = PM10Aqi;
        current = 6;
        afterChange();
    }
    private void setPM2_5() {
        icon = R.drawable.ic_pm2;
        description = R.string.PM2_5_description;
        aqi = PM2_5Aqi;
        current = 7;
        afterChange();
    }
    private void setSO2() {
        icon = R.drawable.ic_so2;
        description = R.string.SO2_description;
        aqi = SO2Aqi;
        current = 8;
        afterChange();
    }

    private void afterChange() {
        int[] colors = getColors(aqi);
        color = colors[0];
        color_pressed = colors[1];
        for (PollutionCallback pc: pollutionCallbacks) {
            pc.onClick();
        }
    }

    private int[] getColors(int cAqi) {
        if (cAqi < 51) {
            return new int[]{R.color.aqi_good, R.color.aqi_good_pressed};
        } else if (cAqi < 101) {
            return new int[]{R.color.aqi_moderate, R.color.aqi_moderate_pressed};
        } else if (cAqi < 151) {
            return new int[]{R.color.aqi_unhealthy_for_sensitive, R.color.aqi_unhealthy_for_sensitive_pressed};
        } else if (cAqi < 201) {
            return new int[]{R.color.aqi_unhealthy, R.color.aqi_unhealthy_pressed};
        } else if (cAqi < 301) {
            return new int[]{R.color.aqi_very_unhealthy, R.color.aqi_very_unhealthy_pressed};
        } else {
            return new int[]{R.color.aqi_hazardous, R.color.aqi_hazardous_pressed};
        }
    }
}

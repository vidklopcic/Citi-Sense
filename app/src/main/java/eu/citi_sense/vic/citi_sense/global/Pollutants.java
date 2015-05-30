package eu.citi_sense.vic.citi_sense.global;
import eu.citi_sense.vic.citi_sense.R;

public class Pollutants {
    public int current;
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
    }
    private void setCO2() {
        icon = R.drawable.ic_co2;
    }
    private void setH2O() {
        icon = R.drawable.ic_h2o;
    }
    private void setNO() {
        icon = R.drawable.ic_no;
    }
    private void setO3() {
        icon = R.drawable.ic_o3;
    }
    private void setPM10() {
        icon = R.drawable.ic_pm10;
    }
    private void setPM2_5() {
        icon = R.drawable.ic_pm2;
    }
    private void setSO2() {
        icon = R.drawable.ic_so2;
    }

}

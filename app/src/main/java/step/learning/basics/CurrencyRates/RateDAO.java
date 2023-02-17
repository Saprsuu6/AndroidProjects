package step.learning.basics.CurrencyRates;

import org.json.JSONException;
import org.json.JSONObject;

public class RateDAO {
    private int r030;
    private String txt;
    private double rate;
    private String cc;
    private String exchangeDate;

    public RateDAO(JSONObject obj) throws JSONException {
        setR030(obj.getInt("r030"));
        setTxt(obj.getString("txt"));
        setRate(obj.getDouble("rate"));
        setCc(obj.getString("cc"));
        setExchangeDate(obj.getString("exchangedate"));
    }

    public int getR030() {
        return r030;
    }

    public String getTxt() {
        return txt;
    }

    public double getRate() {
        return rate;
    }

    public String getCc() {
        return cc;
    }

    public String getExchangeDate() {
        return exchangeDate;
    }

    public void setR030(int r030) {
        this.r030 = r030;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public void setExchangeDate(String exchangeDate) {
        this.exchangeDate = exchangeDate;
    }
}

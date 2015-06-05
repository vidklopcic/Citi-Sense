package eu.citi_sense.vic.citi_sense.global.Databases;

import com.orm.SugarRecord;

import java.util.List;
import java.util.Objects;

public class CachedData extends SugarRecord<CachedData> {
    String name;
    String data;
    String date;

    public CachedData() {
    }

    public CachedData(String name, String data) {
        this.name = name;
        this.data = data;
        this.date = Integer.toString((int) System.currentTimeMillis());
    }

    public void put(String key, String value) {
        List<CachedData> matches = CachedData.find(CachedData.class, "name = ?", key);
        for(CachedData match: matches) {
            match.delete();
        }
        CachedData cd = new CachedData(key, value);
        cd.save();
    }

    public String get(String key, String d) {
        List<CachedData> matches = CachedData.find(CachedData.class, "name = ?", key);
        if(matches.size() > 0) {
            return matches.get(0).data;
        } else {
            return d;
        }
    }
}

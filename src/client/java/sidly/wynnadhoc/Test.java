package sidly.wynnadhoc;

import sidly.wynnadhoc.wapi.ApiUtils;
import sidly.wynnadhoc.wapi.item.WynnItem;
import sidly.wynnadhoc.wapi.item.enums.MajorID;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        Set<MajorID> seen = new HashSet<>();
        Map<String, WynnItem> itemDatabase = ApiUtils.getItemDatabase();
        for (Map.Entry<String, WynnItem> dbEntry : itemDatabase.entrySet()) {
            for (Map.Entry<MajorID, String> mIDEntry : dbEntry.getValue().majorIds().entrySet()) {
                if (seen.add(mIDEntry.getKey())) {
                    if (mIDEntry.getValue().toLowerCase().contains("light") || mIDEntry.getValue().toLowerCase().contains("orb")) {
                        System.out.println(dbEntry.getKey() + " has mID: " + mIDEntry);
                    }
                }
            }
        }
    }
}

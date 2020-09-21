package filehandlers;

import list.UnitListData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnitCacheParser {
    public static ArrayList<UnitListData> LoadUnitCache(BufferedReader br) throws IOException {
        ArrayList<UnitListData> units = new ArrayList<>();
        CSVParser parser = CSVParser.parse(br, CSVFormat.DEFAULT);
        for (CSVRecord record : parser) {
            List<String> values = new ArrayList<>();
            for (int i = 0; i < record.size(); i++) {
                values.add(record.get(i));
            }
            units.add(new UnitListData(values));
        }
        return units;
    }

    public static int WriteUnitCache(ArrayList<UnitListData> unitList, BufferedWriter bw) throws IOException {
        int unitsWritten = 0;
        CSVPrinter printer = new CSVPrinter(bw, CSVFormat.DEFAULT);
        for (UnitListData unit : unitList) {
            printer.printRecord(unit.toCsvIndex());
            unitsWritten++;
        }
        return unitsWritten;
    }
}

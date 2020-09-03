package IO;

import battleforce.BattleForceStats;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import list.UnitListData;

public class UnitListDataSerializer extends Serializer<UnitListData> {
    public UnitListData read(Kryo kryo, Input input, Class<UnitListData> type) {
        UnitListData unit = new UnitListData();
        unit.setName(input.readString());
        unit.setModel(input.readString());
        unit.setConfiguration(input.readString());
        unit.setLevel(input.readString());
        unit.setEra(input.readString());
        unit.setTech(input.readString());
        unit.setSource(input.readString());
        unit.setTonnage(input.readInt());
        unit.setYear(input.readInt());
        unit.setBV(input.readInt());
        unit.setCost(input.readDouble());
        unit.setFilename(IO.Utils.convertFilePathSeparator(input.readString()));
        unit.setType(input.readString());
        unit.setMotive(input.readString());
        unit.setInfo(input.readString());
        unit.setConfig(input.readString());
        if (!unit.getConfig().isEmpty()) {
            unit.setOmni(true);
        }
        unit.setTypeModel(unit.getFullName());
        unit.bfstat = kryo.readObject(input, BattleForceStats.class);
        unit.bfstat.setElement(unit.getFullName());
        unit.bfstat.setName(unit.getName());
        unit.bfstat.setModel(unit.getModel());
        return unit;
    }

    public void write(Kryo kryo, Output output, UnitListData unit) {
        output.writeString(unit.getName());
        output.writeString(unit.getModel());
        output.writeString(unit.getConfiguration());
        output.writeString(unit.getLevel());
        output.writeString(unit.getEra());
        output.writeString(unit.getTech());
        output.writeString(unit.getSource());
        output.writeInt(unit.getTonnage());
        output.writeInt(unit.getYear());
        output.writeInt(unit.getBV());
        output.writeDouble(unit.getCost());
        output.writeString(unit.getFilename());
        output.writeString(unit.getType());
        output.writeString(unit.getMotive());
        output.writeString(unit.getInfo());
        output.writeString(unit.getConfig());
        kryo.writeObject(output, unit.bfstat);
    }
}

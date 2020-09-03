package IO;

import battleforce.BattleForceStats;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.ArrayList;
import java.util.Arrays;

public class BattleForceStatsSerializer extends Serializer<BattleForceStats> {
    public BattleForceStats read(Kryo kryo, Input input, Class<BattleForceStats> type) {
        BattleForceStats bfstat = new BattleForceStats();
        bfstat.setBasePV(input.readInt());
        bfstat.setWeight(input.readInt());
        bfstat.setMovement(input.readString());
        bfstat.setTerrain();
        bfstat.setShort(input.readInt());
        bfstat.setMedium(input.readInt());
        bfstat.setLong(input.readInt());
        bfstat.setExtreme(input.readInt());
        bfstat.setOverheat(input.readInt());
        bfstat.setArmor(input.readInt());
        bfstat.setInternal(input.readInt());
        String ab = input.readString();
        bfstat.setAbilities(new ArrayList<>(Arrays.asList(",".split(ab))));
        bfstat.setPointValue(bfstat.getBasePV());

        return bfstat;
    }

    public void write(Kryo kryo, Output output, BattleForceStats bfstat) {
        output.writeInt(bfstat.getPointValue());
        output.writeInt(bfstat.getWeight());
        output.writeString(bfstat.getMovement());
        output.writeInt(bfstat.getShort());
        output.writeInt(bfstat.getMedium());
        output.writeInt(bfstat.getLong());
        output.writeInt(bfstat.getExtreme());
        output.writeInt(bfstat.getArmor());
        output.writeInt(bfstat.getInternal());
        output.writeString(String.join(",", bfstat.getAbilities()));
    }
}

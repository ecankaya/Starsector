package RacingSector.world;

import com.fs.starfarer.api.campaign.SectorAPI;

public class MyModGen {
    public void generate(SectorAPI sector) {
        new MyContact().generate(sector);
    }
}
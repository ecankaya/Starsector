package author.mymod.world;

import com.fs.starfarer.api.campaign.SectorAPI;

public class MyModGen {
    public void generate(SectorAPI sector) {
        new MySystem().generate(sector);
    }
}
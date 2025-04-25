package RacingSector;

import RacingSector.world.MyModGen;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

public class TemplateModPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();

//        initMyMod();
//
//         Test that the .jar is loaded and working, using the most obnoxious way possible.
//        throw new RuntimeException("Template mod loaded! Remove this crash in TemplateModPlugin.");
    }

    private static void initMyMod() {
        new MyModGen().generate(Global.getSector());
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        super.onNewGameAfterEconomyLoad();
        Global.getLogger(this.getClass()).info("Hooray My mod plugin in a jar is loaded!");
        initMyMod();
    }

    @Override
    public void onGameLoad(boolean newGame){
        super.onGameLoad(false);
        initMyMod();
    }
}

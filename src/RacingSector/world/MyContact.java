package RacingSector.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.People;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;


public class MyContact {
    public void generate(SectorAPI sector) {

        FullName name = new FullName("Emre", "Pasha", FullName.Gender.MALE);

        MarketAPI maxios = sector.getEntityById("new_maxios").getMarket();

        PersonAPI pasha = Global.getSector().getFaction(Factions.INDEPENDENT).createRandomPerson(FullName.Gender.MALE);
        pasha.setId("pasha");
        pasha.setName(name);
        People.assignPost(maxios,Ranks.POST_WARLORD,pasha);

        String sprite = Global.getSettings().getSpriteName("contacts", "pasha");
        pasha.setPortraitSprite(sprite);



//        Global.getSoundPlayer().playCustomMusic(1,1,"MyMusic");
//         MagicCampaign.addCustomPerson(
//                 maxios,
//                name.getFirst(),
//                name.getLast(),
//                People.DARDAN_KATO,
//                FullName.Gender.MALE,
//                Factions.INDEPENDENT,
//                Ranks.PILOT,
//                "criminal",
//                false,
//                 0,
//                 5)
//                 .setImportance(PersonImportance.VERY_HIGH
//                 );

//        People.assignPost(maxios,"criminal",rando);

    }

    public void playMusic(){
        Global.getSoundPlayer().playCustomMusic(0,0,"MyMusic",true);
    }

}

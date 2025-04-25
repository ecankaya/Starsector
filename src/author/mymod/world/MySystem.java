package author.mymod.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

import java.awt.*;

public class MySystem {
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Pasha");
        system.getLocation().set(-2000, -4000); //near Askonia

        // create the star and generate the hyperspace anchor for this system
        PlanetAPI PashaStar = system.initStar("Pasha", // unique id for this star
                "star_blue_giant", // id in planets.json
                800f, // radius (in pixels at default zoom)
                150); // corona radius, from star edge
        system.setLightColor(new Color(39, 155, 228)); // light color in entire system, affects all entities

        //add first stable loc
        SectorEntityToken stableLoc1 = system.addCustomEntity("pasha_stableloc_1", "Stable Location", "stable_location", Factions.NEUTRAL);
        stableLoc1.setCircularOrbit(PashaStar, 200, 5200, 520);

        //add gate
        SectorEntityToken gate = system.addCustomEntity("pasha_gate", "Pasha Gate","inactive_gate",Factions.NEUTRAL);
        gate.setFixedLocation(5000,5000);

        //add comm relay
        SectorEntityToken comm = system.addCustomEntity("pasha_comm_relay","comm relay", "comm_relay",Factions.NEUTRAL);
        comm.setCircularOrbit(PashaStar, 180,3500,240);

        //add nav buoy
        SectorEntityToken nav = system.addCustomEntity("pasha_nav_buoy","nav buoy", "nav_buoy",Factions.NEUTRAL);
        nav.setCircularOrbit(PashaStar, 180,4000,100);

        //add sensor array
        SectorEntityToken sarray = system.addCustomEntity("pasha_sensor_array","sensor array", "sensor_array",Factions.NEUTRAL);
        sarray.setCircularOrbit(PashaStar, 180,3000,150);

        //add research station
        SectorEntityToken research = system.addCustomEntity("pasha_research","research station", "station_research",Factions.NEUTRAL);
        research.setCircularOrbit(PashaStar, 180,2500,200);

        //add weapons cache
        SectorEntityToken weapons = system.addCustomEntity("pasha_weapons","weapons cache", "weapons_cache_high",Factions.NEUTRAL);
        weapons.setCircularOrbit(PashaStar, 180,2000,100);

        //barren industry world
        PlanetAPI pBarren = system.addPlanet("pBarren",PashaStar,"Pasha I","barren",360 * (float) Math.random(),190f,5000f,900f);
        pBarren.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
        pBarren.getMarket().addCondition(Conditions.RUINS_VAST);

        //terran habitable world
        PlanetAPI pTerran = system.addPlanet("pTerran",PashaStar,"Pasha II","terran",360 * (float) Math.random(),250f,4000f,600f);
        pTerran.getMarket().addCondition(Conditions.FARMLAND_BOUNTIFUL);
        pTerran.getMarket().addCondition(Conditions.MILD_CLIMATE);
        pTerran.getMarket().addCondition(Conditions.HABITABLE);
        pTerran.getMarket().addCondition(Conditions.ORGANICS_PLENTIFUL);
        pTerran.getMarket().addCondition(Conditions.ORE_ULTRARICH);
        pTerran.getMarket().addCondition(Conditions.SOLAR_ARRAY);
        pTerran.getMarket().addCondition(Conditions.RUINS_VAST);

        //volcanic mining world
        PlanetAPI pVolcanic = system.addPlanet("pVolcanic",PashaStar,"Pasha III","lava",360 * (float) Math.random(),190f,3000f,300f);
        pVolcanic.getMarket().addCondition(Conditions.ORE_ULTRARICH);
        pVolcanic.getMarket().addCondition(Conditions.RARE_ORE_ULTRARICH);
        pVolcanic.getMarket().addCondition(Conditions.VERY_HOT);
        pVolcanic.getMarket().addCondition(Conditions.EXTREME_TECTONIC_ACTIVITY);
        pVolcanic.getMarket().addCondition(Conditions.RUINS_VAST);
        pVolcanic.getMarket().addCondition(Conditions.TOXIC_ATMOSPHERE);

        //gas giant mining world
        PlanetAPI pGas = system.addPlanet("pGas",PashaStar,"Pasha IV","gas_giant",360 * (float) Math.random(),320f,6000f,150f);
        pGas.getMarket().addCondition(Conditions.VOLATILES_PLENTIFUL);
        pGas.getMarket().addCondition(Conditions.HIGH_GRAVITY);

        //center jump point
        JumpPointAPI jump = Global.getFactory().createJumpPoint("inner_jump","Pasha System Jump");
        jump.setCircularOrbit(system.getEntityById("Pasha"),2,2000,4000f);
        jump.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jump);

        system.autogenerateHyperspaceJumpPoints(true, true);
    }
}

package RacingSector.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.BuffManagerAPI;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberViewAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.CRRecoveryBuff;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class boost extends BaseDurationAbility {
    public static float SENSOR_RANGE_MULT = 0.5f;
    public static float DETECTABILITY_PERCENT = 50f;
    public static float MAX_BURN_MOD = 20f;
    public static float CR_COST_MULT = 0.25f;
    public static float FUEL_USE_MULT = 2f;
    public static float ACCELERATION_MULT = 8f;

    public static float angle;

    @Override
    protected void activateImpl() {

        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return;

        float crCostFleetMult = fleet.getStats().getDynamic().getValue(Stats.EMERGENCY_BURN_CR_MULT);
        if (crCostFleetMult > 0) {
            for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
                float crLoss = member.getDeployCost() * CR_COST_MULT * crCostFleetMult;
                member.getRepairTracker().applyCREvent(-crLoss, "boost");
            }
        }

        float cost = computeFuelCost();
        fleet.getCargo().removeFuel(cost);


    }

    @Override
    protected void applyEffect(float amount, float level) {

        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return;

        fleet.getStats().getSensorRangeMod().modifyMult(getModId(), 1f + (SENSOR_RANGE_MULT - 1f) * level, "boost");
        fleet.getStats().getDetectedRangeMod().modifyPercent(getModId(), DETECTABILITY_PERCENT * level, "boost");
        fleet.getStats().getFleetwideMaxBurnMod().modifyFlat(getModId(), (int)(MAX_BURN_MOD * level), "boost");
        fleet.getStats().getAccelerationMult().modifyMult(getModId(), 1f + (ACCELERATION_MULT - 1f) * level);

        angle = fleet.getFacing();
        Vector2f v = fleet.getVelocity();
        Vector2f boost = Misc.getUnitVectorAtDegreeAngle(angle);
        boost.scale(0.5f * MAX_BURN_MOD);

        fleet.setVelocity(v.x + boost.x ,v.y + boost.y);
        //fleet.getCommanderStats().getDynamic().getStat(Stats.NAVIGATION_PENALTY_MULT).modifyMult(getModId(), 0f);//1f - level);

        for (FleetMemberViewAPI view : fleet.getViews()) {
            //view.getContrailColor().shift(getModId(), view.getEngineColor().getBase(), 1f, 1f, 0.25f);
            view.getContrailColor().shift(getModId(), new Color(50,150,100,255), 1f, 1f, .75f);
            //view.getContrailColor().shift(getModId(), new Color(255,100,255), 1f, 1f, 0.5f);
            //view.getContrailDurMult().shift(getModId(), 0.5f, 1f, 1f, 1f);
            //view.getContrailWidthMult().shift(getModId(), 2f, 1f, 1f, 1f);
            view.getEngineGlowSizeMult().shift(getModId(), 2f+1f, 1+3f, 1f, 1f+3f);
            view.getEngineHeightMult().shift(getModId(), 5f, 1f, 1f, 1f);
            view.getEngineWidthMult().shift(getModId(), 3f, 1f, 1f, 1f);
        }

        //member.getStats().getBaseCRRecoveryRatePercentPerDay()for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
        String buffId = getModId();
        float buffDur = 0.1f;
        boolean needsSync = false;
        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            if (level <= 0) {
                member.getBuffManager().removeBuff(buffId);
                needsSync = true;
            } else {
                BuffManagerAPI.Buff test = member.getBuffManager().getBuff(buffId);
                if (test instanceof CRRecoveryBuff) {
                    CRRecoveryBuff buff = (CRRecoveryBuff) test;
                    buff.setDur(buffDur);
                } else {
                    member.getBuffManager().addBuff(new CRRecoveryBuff(buffId, 0f, buffDur));
                    needsSync = true;
                }
            }
        }

        //if (needsSync || fleet.isPlayerFleet()) {
        if (needsSync) {
            fleet.forceSync();
        }


//		if (level > 0) {
//			SlipstreamTerrainPlugin slipstream = SlipstreamTerrainPlugin.getSlipstreamPlugin(fleet.getContainingLocation());
//			if (slipstream != null) {
//				slipstream.disrupt(fleet, 0.1f);
//			}
//		}
    }

    @Override
    protected void deactivateImpl() {
        cleanupImpl();
    }

    @Override
    protected void cleanupImpl() {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return;

        fleet.getStats().getSensorRangeMod().unmodify(getModId());
        fleet.getStats().getDetectedRangeMod().unmodify(getModId());
        fleet.getStats().getFleetwideMaxBurnMod().unmodify(getModId());
        fleet.getStats().getAccelerationMult().unmodify(getModId());
        //fleet.getCommanderStats().getDynamic().getStat(Stats.NAVIGATION_PENALTY_MULT).unmodify(getModId());
    }

//	@Override
//	public float getActivationDays() {
//		return 0.25f;
//	}
//
//	@Override
//	public float getCooldownDays() {
//		return 1f;
//	}
//
//	@Override
//	public float getDeactivationDays() {
//		return 0.25f;
//	}
//
//	@Override
//	public float getDurationDays() {
//		return 1.5f;
//	}


    protected List<FleetMemberAPI> getNonReadyShips() {
        List<FleetMemberAPI> result = new ArrayList<FleetMemberAPI>();
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return result;

        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            //if (member.isMothballed()) continue;
            //float crLoss = member.getDeployCost() * CR_COST_MULT * crCostFleetMult;
            float crLoss = getCRCost(member, fleet);
            if (Math.round(member.getRepairTracker().getCR() * 100) < Math.round(crLoss * 100)) {
                result.add(member);
            }
        }
        return result;
    }


//	public static boolean isReadyForEBurn(FleetMemberAPI member, CampaignFleetAPI fleet) {
//		float crLoss = getCRCost(member, fleet);
//		return Math.round(member.getRepairTracker().getCR() * 100) < Math.round(crLoss * 100);
//	}

    public static float getCRCost(FleetMemberAPI member, CampaignFleetAPI fleet) {
        float crCostFleetMult = fleet.getStats().getDynamic().getValue(Stats.EMERGENCY_BURN_CR_MULT);
        float crLoss = member.getDeployCost() * CR_COST_MULT * crCostFleetMult;
        return Math.round(crLoss * 100f) / 100f;
    }

    protected float computeFuelCost() {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return 0f;

        float cost = fleet.getLogistics().getFuelCostPerLightYear() * FUEL_USE_MULT;
        return cost;
    }

    protected float computeSupplyCost() {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return 0f;

        float crCostFleetMult = fleet.getStats().getDynamic().getValue(Stats.EMERGENCY_BURN_CR_MULT);

        float cost = 0f;
        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            cost += member.getDeploymentCostSupplies() * CR_COST_MULT * crCostFleetMult;
        }
        return cost;
    }


    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return;

        Color gray = Misc.getGrayColor();
        Color highlight = Misc.getHighlightColor();
        Color fuel = Global.getSettings().getColor("progressBarFuelColor");
        Color bad = Misc.getNegativeHighlightColor();

        LabelAPI title = tooltip.addTitle("boost");
//		title.highlightLast(status);
//		title.setHighlightColor(gray);

        float pad = 10f;

        float fuelCost = computeFuelCost();
        float supplyCost = computeSupplyCost();

        tooltip.addPara("Increases the maximum burn level by %s." +
                        " Reduces sensor range by %s and increases the range at" +
                        " which the fleet can be detected by %s.",// +
//				" The fleet will also be unaffected by most terrain " +
//				"movement penalties and hazards while the ability is active.",
                pad,
                highlight,
                "" + (int) MAX_BURN_MOD,
                "" + (int)((1f - SENSOR_RANGE_MULT) * 100f) + "%",
                //"" + (int)((DETECTABILITY_MULT - 1f) * 100f) + "%"
                "" + (int)(DETECTABILITY_PERCENT) + "%"
        );

        if (supplyCost > 0) {
            tooltip.addPara("Consumes %s fuel and reduces the combat readiness" +
                            " of all ships, costing up to %s supplies to recover. Also prevents combat readiness recovery while active.", pad,
                    highlight,
                    Misc.getRoundedValueMaxOneAfterDecimal(fuelCost),
                    Misc.getRoundedValueMaxOneAfterDecimal(supplyCost));
        } else {
            tooltip.addPara("Consumes %s fuel and prevents combat readiness recovery while active.", pad,
                    highlight,
                    Misc.getRoundedValueMaxOneAfterDecimal(fuelCost));
        }

        if (fuelCost > fleet.getCargo().getFuel()) {
            tooltip.addPara("Not enough fuel.", bad, pad);
        }

        List<FleetMemberAPI> nonReady = getNonReadyShips();
        if (!nonReady.isEmpty()) {
            //tooltip.addPara("Not all ships have enough combat readiness to initiate an emergency burn. Ships that require higher CR:", pad);
            tooltip.addPara("Some ships don't have enough combat readiness to safely initiate an boost " +
                            "and may suffer damage if the ability is activated:", pad,
                    Misc.getNegativeHighlightColor(), "may suffer damage");
            //tooltip.beginGridFlipped(getTooltipWidth(), 1, 30, pad);
            //tooltip.setGridLabelColor(bad);
            int j = 0;
            int max = 4;
            float initPad = 5f;
            for (FleetMemberAPI member : nonReady) {
                if (j >= max) {
                    if (nonReady.size() > max + 1) {
                        tooltip.addPara(BaseIntelPlugin.INDENT + "... and several other ships", initPad);
                        break;
                    }
                }

                //float crLoss = member.getDeployCost() * CR_COST_MULT;
                //String cost = "" + Math.round(crLoss * 100) + "%";
                String str = "";
                if (!member.isFighterWing()) {
                    str += member.getShipName() + ", ";
                    str += member.getHullSpec().getHullNameWithDashClass();
                } else {
                    str += member.getVariant().getFullDesignationWithHullName();
                }

                tooltip.addPara(BaseIntelPlugin.INDENT + str, initPad);
                initPad = 0f;
                j++;

                //tooltip.addToGrid(0, j++, str, cost, bad);
            }
            //tooltip.addGrid(3f);
        }

        //tooltip.addPara("Disables \"Go Dark\" when activated.", pad);
        addIncompatibleToTooltip(tooltip, expanded);
    }

    public boolean hasTooltip() {
        return true;
    }


    @Override
    public void fleetLeftBattle(BattleAPI battle, boolean engagedInHostilities) {
        if (engagedInHostilities) {
            deactivate();
        }
    }

    @Override
    public void fleetOpenedMarket(MarketAPI market) {
        deactivate();
    }




//	@Override
//	public boolean showProgressIndicator() {
//		if (true) return true;
//		return !getNonReadyShips().isEmpty();
//	}
//	@Override
//	public float getProgressFraction() {
//		return 1f;
//	}
//	@Override
//	public Color getProgressColor() {
//		Color color = Misc.getNegativeHighlightColor();
//		color = Misc.getHighlightColor();
//		return Misc.scaleAlpha(color, Global.getSector().getCampaignUI().getSharedFader().getBrightness());
//	}


    protected boolean showAlarm() {
        return !getNonReadyShips().isEmpty() && !isOnCooldown() && !isActiveOrInProgress() && isUsable();
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() &&
                getFleet() != null &&
                //getNonReadyShips().isEmpty() &&
                (getFleet().isAIMode() || computeFuelCost() <= getFleet().getCargo().getFuel());
    }
    @Override
    public float getCooldownFraction() {
        if (showAlarm()) {
            return 0f;
        }
        return super.getCooldownFraction();
    }
    @Override
    public boolean showCooldownIndicator() {
        return super.showCooldownIndicator();
    }
    @Override
    public boolean isOnCooldown() {
        return super.getCooldownFraction() < 1f;
    }

    @Override
    public Color getCooldownColor() {
        if (showAlarm()) {
            Color color = Misc.getNegativeHighlightColor();
            return Misc.scaleAlpha(color, Global.getSector().getCampaignUI().getSharedFader().getBrightness() * 0.5f);
        }
        return super.getCooldownColor();
    }

    @Override
    public boolean isCooldownRenderingAdditive() {
        if (showAlarm()) {
            return true;
        }
        return false;
    }
}


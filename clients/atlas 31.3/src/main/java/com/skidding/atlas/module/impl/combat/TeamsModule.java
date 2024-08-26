package com.skidding.atlas.module.impl.combat;

import com.skidding.atlas.event.impl.client.TargetCheckEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.awt.*;

public class TeamsModule extends ModuleFeature {

    public TeamsModule() {
        super(new ModuleBuilder("Teams", "Stops the client from targeting team mates", ModuleCategory.COMBAT));
    }

    @EventHandler
    public void onAllowTarget(TargetCheckEvent targetCheckEvent) {
        if(targetCheckEvent.entityLivingBase instanceof EntityPlayer player) {
            if(mc.thePlayer.getTeam() != null && player.getTeam() != null && (mc.thePlayer.getTeam().isSameTeam(player.getTeam()) || getTeamColor(mc.thePlayer).getRGB() == getTeamColor(player).getRGB()))
                targetCheckEvent.allow = false;
        }
    }

    public Color getTeamColor(EntityPlayer player) {
        ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) player.getTeam();
        int i = 16777215;

        if (scoreplayerteam != null && scoreplayerteam.getColorPrefix() != null) {
            String s = FontRenderer.getFormatFromString(scoreplayerteam.getColorPrefix());
            if (s.length() >= 2) {
                if (mc.getRenderManager().getFontRenderer() != null && mc.getRenderManager().getFontRenderer().getColorCode(s.charAt(1)) != 0)
                    i = mc.getRenderManager().getFontRenderer().getColorCode(s.charAt(1));
            }
        }
        final float f1 = (float) (i >> 16 & 255) / 255.0F;
        final float f2 = (float) (i >> 8 & 255) / 255.0F;
        final float f = (float) (i & 255) / 255.0F;

        return new Color(f1, f2, f);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}

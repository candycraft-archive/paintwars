package de.pauhull.paintwars.util;

// Project: paintwars
// Class created on 22.03.2020 by Paul
// Package de.pauhull.paintwars.util

import net.mcstats2.core.MCSCore;
import net.mcstats2.core.api.MCSEntity.MCSPlayer;
import net.mcstats2.mcmoney.manager.MCMoneyManager;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CoinUtil {

    public static double COINS_AFTER_WIN;
    public static double CREDITS_AFTER_WIN;
    public static double COINS_AFTER_KILL;
    public static double CREDITS_AFTER_KILL;
    public static double COINS_FAIR_PLAY;

    public static String buildSubTitle(double coins, double credits) {

        StringBuilder builder = new StringBuilder();

        if (coins != 0) {

            if (coins > 0) {
                builder.append("§a+§7");
            } else {
                builder.append("§c-§7");
            }

            builder.append(Math.abs(coins)).append(" Coins");
        }

        if (credits != 0) {

            builder.append(", ");

            if (credits > 0) {
                builder.append("§a+§7");
            } else {
                builder.append("§c-§7");
            }

            builder.append(Math.abs(credits)).append(" Credits");
        }

        return builder.toString();
    }

    public static void addBalance(UUID uuid, double coinsDiff, double creditsDiff) {

        MCSPlayer mcsPlayer;

        try {
            mcsPlayer = MCSCore.getInstance().getPlayer(uuid);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }

        if (coinsDiff != 0) {
            double coins = MCMoneyManager.getInstance().getProfile(mcsPlayer).getCoins();
            coins += coinsDiff;
            if (coins < 0) coins = 0;
            MCMoneyManager.getInstance().getProfile(mcsPlayer).setCoins(coins);
        }

        if (creditsDiff != 0) {
            double credits = MCMoneyManager.getInstance().getProfile(mcsPlayer).getCredits();
            credits += creditsDiff;
            if (credits < 0) credits = 0;
            MCMoneyManager.getInstance().getProfile(mcsPlayer).setCredits(credits);
        }
    }

}

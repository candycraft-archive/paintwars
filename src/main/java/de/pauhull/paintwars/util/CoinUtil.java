package de.pauhull.paintwars.util;

// Project: paintwars
// Class created on 22.03.2020 by Paul
// Package de.pauhull.paintwars.util

import net.mcstats2.mcmoney.manager.MCMoneyManager;

import java.util.UUID;

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

        if (coinsDiff != 0) {
            double coins = MCMoneyManager.getInstance().getProfile(uuid).getCoins();
            coins += coinsDiff;
            if (coins < 0) coins = 0;
            MCMoneyManager.getInstance().getProfile(uuid).setCoins(coins);
        }

        if (creditsDiff != 0) {
            double credits = MCMoneyManager.getInstance().getProfile(uuid).getCredits();
            credits += creditsDiff;
            if (credits < 0) credits = 0;
            MCMoneyManager.getInstance().getProfile(uuid).setCredits(credits);
        }
    }

}

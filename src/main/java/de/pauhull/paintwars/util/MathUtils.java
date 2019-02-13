package de.pauhull.paintwars.util;

import org.bukkit.util.Vector;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
public class MathUtils {

    public static double map(double value, double istart, double istop, double ostart, double ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    public static Vector randomizeVector(Vector vector, double randomX, double randomY, double randomZ) {
        double x = vector.getX() + Math.random() * randomX - randomX / 2.0;
        double y = vector.getY() + Math.random() * randomY - randomY / 2.0;
        double z = vector.getZ() + Math.random() * randomZ - randomZ / 2.0;
        return new Vector(x, y, z);
    }

}

package de.pauhull.paintwars.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 08.12.2018
 *
 * @author pauhull
 */
@ToString
@AllArgsConstructor
public class Selection {

    @Getter
    @Setter
    private Location a, b;

    public static int countBlocks(List<Block> blocks, Material material, short data) {
        int amount = 0;
        for (Block block : blocks) {
            if (block.getType() == material && block.getData() == data) {
                amount++;
            }
        }
        return amount;
    }

    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();
        World world = a.getWorld();
        double minX = Math.min(a.getX(), b.getX());
        double minY = Math.min(a.getY(), b.getY());
        double minZ = Math.min(a.getZ(), b.getZ());
        double maxX = Math.max(a.getX(), b.getX());
        double maxY = Math.max(a.getY(), b.getY());
        double maxZ = Math.max(a.getZ(), b.getZ());

        for (double x = minX; x <= maxX; x++) {
            for (double y = minY; y <= maxY; y++) {
                for (double z = minZ; z <= maxZ; z++) {
                    Block block = new Location(world, x, y, z).getBlock();
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

}

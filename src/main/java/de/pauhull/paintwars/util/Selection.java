package de.pauhull.paintwars.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;

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

}

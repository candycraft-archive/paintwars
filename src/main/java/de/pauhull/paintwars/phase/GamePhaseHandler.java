package de.pauhull.paintwars.phase;

import io.sentry.Sentry;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

import static de.pauhull.paintwars.phase.GamePhase.Type;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class GamePhaseHandler {

    @Getter
    private static GamePhaseHandler instance;

    @Getter
    private GamePhase activePhase;

    @Getter
    private Type activePhaseType;

    public GamePhaseHandler() {
        instance = this;
        this.startPhase(LobbyPhase.class);
    }

    public <T extends GamePhase> T startPhase(Class<T> gamePhaseClass) {
        try {
            this.activePhase = gamePhaseClass.getConstructor(GamePhaseHandler.class).newInstance(this);
            this.activePhaseType = this.activePhase.getType();
            this.activePhase.start();

            @SuppressWarnings("unchecked")
            T genericPhase = (T) this.activePhase;
            return genericPhase;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            Sentry.capture(e);
            return null;
        }
    }

}

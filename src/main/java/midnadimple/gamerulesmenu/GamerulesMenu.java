package midnadimple.gamerulesmenu;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.data.gamerule.GameRule;
import net.minecraft.core.data.gamerule.GameRuleCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.ArrayList;
import java.util.List;


public class GamerulesMenu implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "gamerulesmenu";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static GameRuleCollection gameRuleCollection = new GameRuleCollection();
	public static final List<GameRule<?>> gameRuleList = new ArrayList<>();

	@Override
    public void onInitialize() {
        LOGGER.info("GamerulesMenu initialized.");
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {

	}
}

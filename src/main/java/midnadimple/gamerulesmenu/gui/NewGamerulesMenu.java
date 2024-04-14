package midnadimple.gamerulesmenu.gui;

import midnadimple.gamerulesmenu.GamerulesMenu;
import midnadimple.gamerulesmenu.interfaces.IGuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.render.Tessellator;
import net.minecraft.core.data.gamerule.GameRule;
import net.minecraft.core.data.gamerule.GameRuleBoolean;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.sound.SoundCategory;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NewGamerulesMenu extends GuiScreen {
	private static final I18n loc = I18n.getInstance();
	private final GuiScreen prevGuiScreen;
	private boolean cheatsEnabled;

	private GuiButton cheatsButton;
	private final LinkedHashMap<GuiButton, Boolean> gameRuleButtons = new LinkedHashMap<>();

	private static final int BUTTON_BORDER_OFFSET = 40;
	private float scrollFloat = 0.0f;

	public NewGamerulesMenu(GuiScreen guiScreen, boolean cheatsEnabled) {
		this.prevGuiScreen = guiScreen;
		this.cheatsEnabled = cheatsEnabled;
	}

	@Override
	public void init() {
		controlList.add(this.cheatsButton = new GuiButton(0, this.width / 2 - 100, BUTTON_BORDER_OFFSET, 200, 20, ""));

		int i = 2;
		for (GameRule<?> gameRule : Registries.GAME_RULES) {
			GuiButton guiButton = new GuiButton(i, this.width - BUTTON_BORDER_OFFSET - 60 , (int) (2 * BUTTON_BORDER_OFFSET + (i - 2) * BUTTON_BORDER_OFFSET * 0.75 - scrollFloat), 50, 20, "");
			controlList.add(guiButton);

			Boolean value;
			if (GamerulesMenu.GAMERULE_COLLECTION_EMPTY) {
				GamerulesMenu.gameRuleList.add(gameRule);
				value = (Boolean) gameRule.getDefaultValue();
			} else {
				value = (Boolean) GamerulesMenu.gameRuleCollection.getValue(gameRule);
			}
			gameRuleButtons.put(guiButton, value);
			i++;
		}
		if (GamerulesMenu.GAMERULE_COLLECTION_EMPTY) {
			GamerulesMenu.GAMERULE_COLLECTION_EMPTY = false;
		}

		controlList.add(new GuiButton(1, this.width / 2 - 100, this.height - BUTTON_BORDER_OFFSET, 200, 20, loc.translateKey("gamerulesmenu.goback")));

		updateButtons();
	}

	private void updateButtons() {
		cheatsButton.displayString = loc.translateKey("gamerulesmenu.cheats") + " ";
		if (cheatsEnabled) {
			cheatsButton.displayString += loc.translateKey("gamerulesmenu.on");
		} else {
			cheatsButton.displayString += loc.translateKey("gamerulesmenu.off");
		}

		gameRuleButtons.forEach((gameRuleButton, gameRuleButtonValue) -> {
			if (gameRuleButtonValue) {
				gameRuleButton.displayString = loc.translateKey("gamerulesmenu.on");
			} else {
				gameRuleButton.displayString = loc.translateKey("gamerulesmenu.off");
			}
		});
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		GL11.glDisable(2896);
		GL11.glDisable(2912);
		Tessellator tessellator = Tessellator.instance;
		GL11.glBindTexture(3553, this.mc.renderEngine.getTexture("/gui/titlebackground.png"));
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		float f = 32.0f;
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(0x404040);
		tessellator.addVertexWithUV(0.0, this.height, 0.0, 0.0, (float)this.height / f);
		tessellator.addVertexWithUV(this.width, this.height, 0.0, (float)this.width / f, (float)this.height / f);
		tessellator.addVertexWithUV(this.width, 0.0, 0.0, (float)this.width / f, 0.0);
		tessellator.addVertexWithUV(0.0, 0.0, 0.0, 0.0, 0.0);
		tessellator.draw();

		// Title
		drawStringCentered(fontRenderer, loc.translateKey("gamerulesmenu.title"), width / 2, 20, 0xFFFFFF);

		// Gamerules
		scrollFloat = Math.max(scrollFloat + (float) Mouse.getDWheel() / -5.0F, 0);
		scrollFloat = Math.min(scrollFloat, 25 * gameRuleButtons.size());

		AtomicInteger i = new AtomicInteger();
		for (GameRule<?> gameRule : GamerulesMenu.gameRuleList) {
			drawStringCentered(fontRenderer, gameRule.getKey(), BUTTON_BORDER_OFFSET + 60, (int) (2.15 * BUTTON_BORDER_OFFSET + i.get() * BUTTON_BORDER_OFFSET * 0.75 - scrollFloat), 0xFFFFFF);
			i.getAndIncrement();
		}

		i.set(0);
		for (GuiButton gameruleButton : gameRuleButtons.keySet()) {
			gameruleButton.yPosition =((int) (2 * BUTTON_BORDER_OFFSET + i.getAndIncrement() * BUTTON_BORDER_OFFSET * 0.75 - scrollFloat));
		}

		super.drawScreen(mouseX, mouseY, partialTick);
		updateButtons();
	}

	@Override
	protected void buttonReleased(GuiButton button) {
		if (button.id == 1) {
			((IGuiCreateWorld) prevGuiScreen).gamerules_menu$setCheatsEnabled(cheatsEnabled);
			mc.displayGuiScreen(prevGuiScreen);
		} else if (button.id == 0) {
			cheatsEnabled = !cheatsEnabled;
			updateButtons();
		} else {
			for (GuiButton gameruleButton : gameRuleButtons.keySet()) {
				if (button.id == gameruleButton.id) {
					gameRuleButtons.put(gameruleButton, !gameRuleButtons.get(gameruleButton));
					GamerulesMenu.gameRuleCollection.setValue((GameRuleBoolean) GamerulesMenu.gameRuleList.get(button.id-2), gameRuleButtons.get(gameruleButton));
					updateButtons();
				}
			}
		}
		this.mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
	}
}

package midnadimple.gamerulesmenu.mixin;

import midnadimple.gamerulesmenu.GamerulesMenu;
import midnadimple.gamerulesmenu.gui.NewGamerulesMenu;
import midnadimple.gamerulesmenu.interfaces.IGuiCreateWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.core.player.gamemode.Gamemode;
import org.objectweb.asm.Opcodes;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiCreateWorld.class, remap = false)
public class GuiCreateWorldMixin extends GuiScreen implements IGuiCreateWorld {
	@Shadow
	private boolean cheatsEnabled;
	@Shadow
	private Gamemode selectedGamemode;

	@Unique
	protected Minecraft mc = Minecraft.getMinecraft(Minecraft.class);

	@Redirect(method = "buttonPressed", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiCreateWorld;cheatsEnabled:Z", opcode = Opcodes.PUTFIELD))
	private void cheatDisabler(GuiCreateWorld guiCreateWorld, boolean value) {
	}

	@Inject(method = "buttonPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiCreateWorld;updateButtons()V"))
	private void openGamerulesMenu(GuiButton button, CallbackInfo ci) {
		// id 3 = cheats button
		if (button.id == 3) {
			mc.displayGuiScreen(new NewGamerulesMenu(this, cheatsEnabled));
		}
	}

	@Inject(method = "updateButtons", at = @At("HEAD"))
	private void creativeCheatsEnabler(CallbackInfo ci) {
		if (selectedGamemode == Gamemode.creative) {
			cheatsEnabled = true;
		}
	}

	@Inject(method = "buttonPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/save/LevelData;setCheatsEnabled(Z)V"))
	private void saveGamerules(GuiButton button, CallbackInfo ci) {
		mc.theWorld.getLevelData().getGameRules().setValues(GamerulesMenu.gameRuleCollection);
	}

	@Override
	public void gamerules_menu$setCheatsEnabled(boolean cheatsEnabled) {
		this.cheatsEnabled = cheatsEnabled;
	}
}

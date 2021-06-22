package de.cuuky.varo.gui.admin.discordbot.botregister;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.menu.utils.PageAction;
import de.cuuky.varo.Main;
import de.cuuky.varo.bot.discord.register.BotRegister;
import de.cuuky.varo.gui.VaroSuperInventory;
import de.cuuky.varo.gui.admin.discordbot.DiscordBotGUI;

public class BotRegisterListGUI extends VaroSuperInventory {

	public BotRegisterListGUI(Player opener) {
		super("§cBotVerify", opener, 54, false);

		this.setModifier = true;
		Main.getCuukyFrameWork().getInventoryManager().registerInventory(this);
		open();
	}

	@Override
	public boolean onBackClick() {
		new DiscordBotGUI(opener);
		return true;
	}

	@Override
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}

	@Override
	public void onInventoryAction(PageAction action) {}

	@Override
	public boolean onOpen() {
		ArrayList<BotRegister> list = BotRegister.getBotRegister();

		int start = getSize() * (getPage() - 1);
		for (int i = 0; i != getSize(); i++) {
			BotRegister register;
			try {
				register = list.get(start);
			} catch (IndexOutOfBoundsException e) {
				break;
			}

			linkItemTo(i, new ItemBuilder().playername(register.getPlayerName()).lore(new String[] { "§7Player Name: " + Main.getColorCode() + register.getUUID(), "§7Player Name: " + Main.getColorCode() + register.getPlayerName(), "§7Is Bypassing: " + Main.getColorCode() + register.isBypass(), "§7Discord User: " + Main.getColorCode() + register.getMember().getAsMention() }).amount(getFixedSize(list.size())).buildSkull(), new Runnable() {

				@Override
				public void run() {
					new BotRegisterGUI(opener, register);
				}
			});

			start++;
		}

		return calculatePages(list.size(), getSize()) == page;
	}
}

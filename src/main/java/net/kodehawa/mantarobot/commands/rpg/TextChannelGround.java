package net.kodehawa.mantarobot.commands.rpg;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.kodehawa.mantarobot.commands.rpg.item.Item;
import net.kodehawa.mantarobot.commands.rpg.item.ItemStack;
import net.kodehawa.mantarobot.commands.rpg.item.Items;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TextChannelGround {
	private static final Map<String, List<ItemStack>> DROPPED_ITEMS = new HashMap<>();
	private static final Map<String, AtomicInteger> DROPPED_MONEY = new HashMap<>();
	private static TextChannel channel;
	private static Random r = new Random(System.currentTimeMillis());

	public static TextChannelGround of(String id) {
		return new TextChannelGround(
			DROPPED_ITEMS.computeIfAbsent(id, k -> new ArrayList<>()),
			DROPPED_MONEY.computeIfAbsent(id, k -> new AtomicInteger(0))
		);
	}

	public static TextChannelGround of(TextChannel ch) {
		channel = ch;
		return of(ch.getId());
	}

	public static TextChannelGround of(GuildMessageReceivedEvent event) {
		channel = event.getChannel();
		return of(event.getChannel());
	}

	private final AtomicInteger money;
	private final List<ItemStack> stacks;

	private TextChannelGround(List<ItemStack> stacks, AtomicInteger money) {
		this.stacks = stacks;
		this.money = money;
	}

	public List<ItemStack> collectItems() {
		List<ItemStack> finalStacks = new ArrayList<>(stacks);
		stacks.clear();
		return finalStacks;
	}

	public int collectMoney() {
		return money.getAndSet(0);
	}

	public TextChannelGround dropItem(Item item) {
		return dropItems(new ItemStack(item, 1));
	}

	public TextChannelGround dropItem(int item) {
		return dropItem(Items.ALL[item]);
	}

	public boolean dropItemWithChance(Item item, int weight) {
		boolean doDrop = r.nextInt(weight) == 0;
		if (doDrop) dropItem(item);
		return doDrop;
	}

	public boolean dropItemWithChance(int item, int weight) {
		return dropItemWithChance(Items.fromId(item), weight);
	}

	public TextChannelGround dropItems(List<ItemStack> stacks) {
		List<ItemStack> finalStacks = new ArrayList<>(stacks);
		this.stacks.addAll(finalStacks);
		return this;
	}

	public TextChannelGround dropItems(ItemStack... stacks) {
		return dropItems(Arrays.asList(stacks));
	}

	public void dropMoney(int money) {
		this.money.addAndGet(money);
	}

	public boolean dropMoneyWithChance(int money, int weight) {
		boolean doDrop = r.nextInt(weight) == 0;
		if (doDrop) dropMoney(money);
		return doDrop;
	}
}

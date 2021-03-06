package net.kodehawa.mantarobot.commands.moderation;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.kodehawa.mantarobot.modules.SimpleCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static net.kodehawa.mantarobot.utils.StringUtils.SPLIT_PATTERN;

public class CategorizedCommand extends SimpleCommand {
	public static class Builder {
		public static class CategoryBuilder {
			private final Map<String, Callable> options = new HashMap<>();

			private Map<String, Callable> done() {
				return options;
			}

			public CategoryBuilder with(String option, Callable callable) {
				options.put(option, callable);
				return this;
			}
		}

		private final Map<String, Map<String, Callable>> categories = new HashMap<>();

		public CategorizedCommand done() {
			return new CategorizedCommand(new HashMap<>(categories));
		}

		public Builder with(String category, Consumer<CategoryBuilder> builder) {
			CategoryBuilder b = new CategoryBuilder();
			builder.accept(b);
			categories.put(category, b.done());
			return this;
		}
	}

	private final Map<String, Map<String, Callable>> categories;

	public CategorizedCommand(Map<String, Map<String, Callable>> categories) {
		this.categories = categories;
	}

	@Override
	protected void call(String[] args, String content, GuildMessageReceivedEvent event) {
		if (!handle(args, content, event)) onHelp(event);
	}

	@Override
	protected String[] splitArgs(String content) {
		return SPLIT_PATTERN.split(content, 3);
	}

	@Override
	public MessageEmbed help(GuildMessageReceivedEvent event) {
		return null; //TODO
	}

	private boolean handle(String[] args, String content, GuildMessageReceivedEvent event) {
		if (args.length < 3) return false;
		Map<String, Callable> categoryMap = categories.get(args[0]);
		if (categoryMap == null) return false;
		Callable callable = categoryMap.get(args[1]);
		return callable != null && callable.call(event, args[2]);
	}
}

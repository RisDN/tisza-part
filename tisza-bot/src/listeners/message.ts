import { Listener } from "@sapphire/framework";
import { ChannelType, Message } from "discord.js";
import { config } from "../config";
import { sendChatMessage } from "../socket.io/server";

export class MessageListener extends Listener {
  public constructor(
    context: Listener.LoaderContext,
    options: Listener.Options
  ) {
    super(context, {
      ...options,
      once: false,
      event: "messageCreate",
    });
    // Permission to mention staff
  }

  public run(message: Message) {
    if (message.author.bot) return;
    if (message.channel.type == ChannelType.DM) return;
    if (message.channelId != config.crossChatChannelId) {
      return;
    }

    if (message.content.length == 0) return;

    sendChatMessage(message.content, message.author.displayName, message.url);
  }
}

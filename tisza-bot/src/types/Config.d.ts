export type Config = {
  guildId: string;

  commandCooldowns: {
    link: number;
    suggest: number;
  };

  channels: {
    suggestion: string;
  };

  crossChatChannelId: string;
};

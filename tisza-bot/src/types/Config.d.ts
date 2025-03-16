export type Config = {
  guildId: string;

  commandCooldowns: {
    link: number;
    suggest: number;
    ip: number
  };

  channels: {
    suggestion: string;
  };

  crossChatChannelId: string;
};

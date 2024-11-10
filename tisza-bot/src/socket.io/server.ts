// simple socket io server with express

import express from "express";
import { config } from "../config";
import { getClient } from "..";
import { BaseGuildTextChannel } from "discord.js";

const app = express();

const http = require("http").createServer(app);

const io = require("socket.io")(http);

let socketConnection: any;

io.on("connection", (socket: any) => {
  socketConnection = socket;

  socket.on("connection", () => {
    console.log("client disconnected");
  });

  socket.on("chat", (msg: string, sender: string) => {
    const client = getClient();
    if (client == null) {
      console.log("The bot is not running");
      return;
    }
    console.log(`message: ${msg} from ${sender}`);
    const channel = client.channels.cache.get(config.crossChatChannelId) as BaseGuildTextChannel;
    channel.send(`**${sender}**: ${msg}`);
  });
});

http.listen(2211, () => {
  console.log("Socket server listening on *:2211");
});

export const sendChatMessage = (
  message: string,
  sender: string,
  messageUrl: string
) => {
  socketConnection.emit("chat", message, sender, messageUrl);
};

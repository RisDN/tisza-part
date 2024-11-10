// simple socket io server with express

import express from "express";

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
    // TODO: itt kuldje el a chat uzenetet a discordra (az id ott van a configban)
    console.log(`message: ${msg} from ${sender}`);
  });
});

http.listen(2211, () => {
  console.log("Socket server listening on *:2211");
});

export const sendChatMessage = (
  message: string,
  sender: string,
  messageurl: string
) => {
  socketConnection.emit("chat", message, sender, messageurl);
};

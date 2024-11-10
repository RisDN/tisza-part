import { SapphireClient } from "@sapphire/framework";
import { GatewayIntentBits } from "discord.js";
import { config } from "dotenv";
import { sendChatMessage } from "./socket.io/server";

// import { REST, Routes } from "discord.js";
config();

const { env } = process;
const client = new SapphireClient({
  intents: [
    GatewayIntentBits.Guilds,
    GatewayIntentBits.GuildMessages,
    GatewayIntentBits.MessageContent,
    GatewayIntentBits.GuildMembers,
  ],
});

process.on("unhandledRejection", (error: any) => {
  if (error.code == 50013) {
    console.log(
      `Hiányzó jogosultságok egy művelet során! ${JSON.stringify(
        error.requestBody
      )}`
    );
    return;
  }
  if (error.code == 10007) {
    console.log(`Ismeretlen felhasználó!`);
    return;
  }
  if (error.code == 10011) {
    console.log(`Ismeretlen rang! ${error.url.split("roles")[1]}`);
    return;
  }
  if (error.code == "ETIMEDOUT") {
    // console.log(`Időtúllépés! ${error.address}:${error.port}`);
    return;
  }
  if (error.code == "ECONNRESET") {
    console.log(`Kapcsolat megszakadt! ${error.address}:${error.port}`);
    return;
  }
  if (error.code == "ECONNREFUSED") {
    // console.log(`Kapcsolat visszautasítva! ${error.address}:${error.port}`);
    return;
  }
  if (error.code) {
    console.log(`Kezeletlen hiba: ${error}`);
    console.log(`Kezeletlen hibakód: ${error.code}`);
    return;
  }
});

const main = async () => {
  try {
    client.logger.info("Bejelentkezés...");
    await client.login(env.TOKEN);
    client.logger.info("A bot felállt!");
    require("./socket.io/server");
    setTimeout(() => {
      sendChatMessage(
        "Hello, wor",
        "Server",
        "https://discord.com/channels/1195719753944289290/1200153833595207872/1305245723779924088"
      );
    }, 5000);
  } catch (error) {
    client.logger.fatal(error);
    await client.destroy();
    process.exit(1);
  }
};

// const rest = new REST().setToken(env.TOKEN ? env.TOKEN : "");
// rest.put(Routes.applicationCommands('1071745533284204634'), { body: [] }).catch(console.error);
// rest.put(Routes.applicationGuildCommands("1071745533284204634", "868163206911254538"), { body: [] }).catch(console.error);

void main();

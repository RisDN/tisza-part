import { Client, TextChannel } from "discord.js";
import { config } from "../config";

let lastAd = 0;

const shuffle = (array: string[]) => {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
};

const shuffledMessages = shuffle(config.advertisement.messages);

export const AdvertService = {

    async startService(client: Client) {
        this.sendAdvert(client);
        setInterval(() => {
            this.sendAdvert(client);
        }, config.advertisement.interval);
    },

    async sendAdvert(client: Client) {
        const channel = client.guilds.cache.get(config.guildId)?.channels.cache.get(config.advertisement.channel) as TextChannel;
        if (!channel) {
            console.log("Advert channel not found");
            return;
        }

        const messages = await channel.messages.fetch({ limit: 1 });
        const lastMessage = messages.first();
        if (!lastMessage) {
            channel.send(shuffledMessages[lastAd]);
            lastAd++;
            return;
        }

        const date = new Date();
        if (date.getTime() - lastMessage.createdTimestamp > config.advertisement.interval && (date.getHours() > 14 || date.getHours() < 20)) {
            if (lastMessage.content == config.advertisement.messages[lastAd]) {
                lastAd++;
            }
            channel.send(config.advertisement.messages[lastAd]);
            lastAd++;
            if (lastAd > config.advertisement.messages.length - 1) {
                lastAd = 0;
            }
        };
    }
}

// function sendAd() {
//     const channel = client.guilds.cache.get(fivemGuildId).channels.cache.get("1103326067999117403");
//     channel.messages.fetch({ limit: 1 }).then(messages => {
//         const lastMessage = messages.first();
//         const date = new Date();
//         if (date - lastMessage.createdTimestamp > 1209600000 && (date.getHours() > 14 || date.getHours() < 20)) {
//             if (lastMessage.content == adMessages[lastAd]) {
//                 lastAd++;
//             }
//             channel.send(adMessages[lastAd]);
//             lastAd++;
//             if (lastAd > adMessages.length - 1) {
//                 lastAd = 0;
//             }
//         }
//     });
// }
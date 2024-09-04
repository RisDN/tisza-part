import { Client, GuildMember } from 'discord.js';
import { readFileSync } from "fs";
import * as fs from 'fs/promises';
import { config } from '../config';
import { User } from '../types/User';
const https = require('https');

const filePath = 'src/data/users.json';
const users = JSON.parse(readFileSync(filePath, 'utf8'));

const threeDays = 259200000;

// user:
// "765794653156540467": {
//     "roleDate": 1660657497782,
//     "peramently": false,
//     "denies": 1
// },

export default {

    async saveUsers(): Promise<void> {
        await fs.writeFile(filePath, JSON.stringify(users, null, 0));
    },

    async removeDeniedRoles(client: Client): Promise<void> {
        const guild = client.guilds.cache.get(config.guildId);
        if (!guild) return;

        for (const userId in users) {
            const user: User = users[userId];
            if (Date.now() - user.roleDate > threeDays && !user.peramently) {
                const member = guild.members.cache.get(userId);
                if (!member) continue;
                if (member.roles.cache.has(config.roles.denied) && (!member.roles.cache.has(config.roles.peramentlyDenied) || !user.peramently)) {
                    member.roles.remove(config.roles.denied);
                    if (users[userId] && !users[userId].peramently) {
                        users[userId] = undefined;
                    }
                    https.get('https://galaxyrp.hu/reuser/' + userId, () => { }).on("error", () => { });
                }
            }
        }
        this.saveUsers();
    },

    async declineUser(member: GuildMember): Promise<void> {
        const user: User = users[member.id];
        if (!user || user.denies < 2) {
            users[member.id] = {
                roleDate: Date.now(),
                peramently: false,
                denies: user ? user.denies + 1 : 1
            }
            member.roles.add(config.roles.denied);
        } else {
            users[member.id] = {
                roleDate: Date.now(),
                peramently: true,
                denies: 2
            }
            member.roles.add(config.roles.peramentlyDenied);
            member.roles.remove(config.roles.denied);
        }
    },

    getUser(userId: string): User {
        return users[userId];
    },


};

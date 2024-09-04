import { BaseGuildTextChannel, ButtonStyle, CategoryChannel, ChannelType, EmbedBuilder, GuildMember, PermissionFlagsBits } from 'discord.js';
import { buttonBuilder, concatButtons } from '../builders/buttonBuilder';
import { config } from '../config';

export const TicketService = {

    async open(member: GuildMember, categoryKey: string) {
        const guild = member.guild;
        const category = guild.id == config.guildId ? config.ticketCategories[categoryKey] : config.unbanTicketCategories[categoryKey];
        if (!category) {
            return;
        }

        const discordCategory = member.guild.channels.cache.get(category.categoryId) as CategoryChannel;
        if (!discordCategory) {
            console.error('A ticket kategória nem található!');
            return;
        }

        const permissions = [
            {
                id: guild.roles.everyone.id,
                allow: [] as bigint[],
                deny: [PermissionFlagsBits.ViewChannel] as bigint[]
            },
            {
                id: member.id,
                allow: [PermissionFlagsBits.ViewChannel, PermissionFlagsBits.SendMessages, PermissionFlagsBits.AttachFiles] as bigint[],
                deny: [] as bigint[]
            }
        ];
        if (guild.id == config.guildId) {
            for (const roleId in config.ticket.permissions) {
                if (config.ticket.permissions[roleId]) {
                    permissions.push({
                        id: roleId,
                        allow: config.ticket.permissions[roleId].allow,
                        deny: config.ticket.permissions[roleId].deny
                    });
                }
            }
        } else {
            for (const roleId in config.ticket.unbanPermissions) {
                if (config.ticket.unbanPermissions[roleId]) {
                    permissions.push({
                        id: roleId,
                        allow: config.ticket.unbanPermissions[roleId].allow,
                        deny: config.ticket.unbanPermissions[roleId].deny
                    });
                }
            }
        }

        const channel = await guild.channels.create({
            name: `ticket-${member.displayName}`,
            type: ChannelType.GuildText,
            parent: discordCategory,
            permissionOverwrites: permissions,
            topic: member.id
        });

        const welcomeEmbed = new EmbedBuilder()
            .setTitle(category.welcomeEmbed.title)
            .setDescription(category.welcomeEmbed.description.replace('<username>', member.toString()))
            .setColor(category.welcomeEmbed.color);

        const closeButton = buttonBuilder({ customId: 'close-ticket', label: 'Bezárás', style: ButtonStyle.Danger });

        channel.send({
            embeds: [welcomeEmbed],
            components: [concatButtons([closeButton])]
        });
        channel.send(`<@${member.id}>`).then(message => {
            message.delete().catch(() => { });
        });
    },

    async close(closer: GuildMember | undefined, channel: BaseGuildTextChannel) {
        const embed = new EmbedBuilder();
        embed.setTitle('Ticket be lesz zárva 5 másodperc múlva.');
        embed.setDescription(`Bezárta: <@${closer?.id}>`);
        embed.setColor('#F03030');
        channel.send({ embeds: [embed] });

        if (channel.parentId == config.categoryes.closedTicket || channel.parentId == config.categoryes.unbanClosedTicket) {
            channel.delete().catch(() => { console.log(`Nem sikerült törölni a szobát`) });
            return;
        }

        setTimeout(async () => {
            const date = new Date();
            const newChannelName = `closed-${channel.name} ${date.getHours() + 2}:${date.getMinutes()}`;
            const everyoneId = channel.guild.roles.everyone.id;

            const permissions = [
                {
                    id: everyoneId,
                    allow: [] as bigint[],
                    deny: [PermissionFlagsBits.ViewChannel] as bigint[]
                },
            ];

            const categoryId = channel.guildId == config.guildId ? config.categoryes.closedTicket : config.categoryes.unbanClosedTicket;
            const category = await channel.guild.channels.fetch(categoryId) as CategoryChannel;
            const channels = category.children.cache.filter((channel) => channel.type == ChannelType.GuildText).size;

            channel.edit({
                permissionOverwrites: permissions
            });
            if (channels == 50) {
                console.log('Túl sok zárt ticket van!');
                return;
            } else {
                channel.setParent(category);
                channel.edit({
                    name: newChannelName
                });
            }
        }, 5000);
    },


    async hasTicket(member: GuildMember): Promise<boolean> {
        await member.guild.channels.fetch();
        return member.guild.channels.cache.filter((channel) =>
            channel.name == `ticket-${member.displayName}`.toLowerCase()
        ).size > 0;
    }
}
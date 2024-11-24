import { Command } from '@sapphire/framework';
import crypto from "crypto";
import { config } from '../config';
import query from '../database/db';
import { CooldownService } from '../services/CooldownService';


export class UnLinkCommand extends Command {
    private cooldownService = new CooldownService("unlink");

    public constructor(context: Command.LoaderContext, options: Command.Options) {
        super(context, {
            ...options, description: 'Minecraft szétkapcsolása a discordodtól', preconditions: ['GuildOnly']
        });
    }

    public override registerApplicationCommands(registry: Command.Registry) {
        registry.registerChatInputCommand((builder) =>
            builder.setName('unlink')
                .setDescription('Discord felhasználó szétkapcsolása a Minecraft karaktertől.')
        );
    }

    public override async chatInputRun(interaction: Command.ChatInputCommandInteraction) {
        const member = await interaction.guild?.members.fetch(interaction.user.id);
        if (!member) {
            return;
        }

        const cooldown = this.cooldownService;
        if (cooldown && cooldown.hasCooldown(member.id)) {
            interaction.reply({
                content: cooldown.getCooldownMessage(member.id),
                ephemeral: true
            }).then(message => {
                setTimeout(() => {
                    message.delete().catch(() => { });
                }, 5000);
            });
            return
        }

        const links = (await query('SELECT * FROM links WHERE discord_id = ?', [member.id]) as any[]);
        if (links.length == 0) {
            return interaction.reply({ content: 'Ez a Discord fiók egy karakterhez sincs kapcsolva!', ephemeral: true });
        }

        const uuid = this.generateOfflineUUID(links[0].username);
        const bans = (await query('SELECT * FROM litebans_bans WHERE uuid = ? AND active = 1', [uuid]) as any[]);

        if (bans.length > 0) {
            return interaction.reply({ content: 'Ez a karakter **ki van tiltva** a szerveren, ezért nem tudod szétkapcsolni!', ephemeral: true });
        }

        cooldown?.addCooldown(member.id, config.commandCooldowns.link);
        await query('DELETE FROM links WHERE discord_id = ?', [member.id]);
        member.roles.remove('1307294590901944370');
        return interaction.reply({ content: '**Sikeresen** szétkapcsoltad fiókjaid!', ephemeral: true });
    }

    private generateOfflineUUID(username: string): string {
        const hash = crypto.createHash('md5').update(`OfflinePlayer:${username}`).digest('hex');

        const uuid = [
            hash.substr(0, 8),
            hash.substr(8, 4),
            `3${hash.substr(13, 3)}`, // A 13. karakter 3-as
            `${(parseInt(hash.substr(16, 2), 16) & 0x3f | 0x80).toString(16)}${hash.substr(18, 2)}`, // A 17. karakter 4-es
            hash.substr(20, 12)
        ].join('-');

        return uuid;
    }
}
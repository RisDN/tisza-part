import { Command } from '@sapphire/framework';
import { config } from '../config';
import query from '../database/db';
import crypto from "crypto";
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

        const uuid = crypto.createHash('md5').update(`$OfflinePlayer:${links[0].username}`).digest('hex');
        const bans = (await query('SELECT * FROM litebans_bans WHERE uuid = ? AND active = 1', [uuid]) as any[]);
        if (bans.length > 0) {
            return interaction.reply({ content: 'Ez a karakter **bannolva van** a szerveren, nem tudod szétkapcsolni!', ephemeral: true });
        }

        cooldown?.addCooldown(member.id, config.commandCooldowns.link);
        await query('DELETE FROM links WHERE discord_id = ?', [member.id]);
        member.roles.remove('1307294590901944370');
        return interaction.reply({ content: '**Sikeresen** szétkapcsoltad fiókjaid!', ephemeral: true });
    }
}
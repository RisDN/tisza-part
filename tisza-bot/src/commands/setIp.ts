import { Command } from '@sapphire/framework';
import { config } from '../config';
import query from '../database/db';
import { CooldownService } from '../services/CooldownService';


export class IpCommand extends Command {
    private cooldownService = new CooldownService("setip");
    private ipRegex = /^(?:\d{1,3}\.){3}\d{1,3}$/;

    public constructor(context: Command.LoaderContext, options: Command.Options) {
        super(context, {
            ...options, description: 'Minecraft karaktered védelme ip cím (IPV4) segítségével', preconditions: ['GuildOnly']
        });
    }

    public override registerApplicationCommands(registry: Command.Registry) {
        registry.registerChatInputCommand((builder) =>
            builder.setName('setip')
                .setDescription('Minecraft karaktered védelme ip cím (IPV4) segítségével')
                .addStringOption((option) => option.setName('ipcím')
                    .setDescription('IP címed (IPV4) (https://whatismyipaddress.com/)')
                    .setMinLength(8)
                    .setMaxLength(20)
                    .setRequired(true)), { guildIds: [config.guildId] }
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

        const ip = (interaction.options.getString('ipcím') || '').trim();
        if (!ip || !this.ipRegex.test(ip)) {
            return interaction.reply({ content: 'Az ípcím nem volt megfelelő formátumban. pl.: 192.168.0.1', ephemeral: true }); {
            }
        }
        const isLinked = (await query('SELECT * FROM links WHERE discord_id = ?', [interaction.user.id]) as any[]).length > 1;
        if (!isLinked) {
            return interaction.reply({ content: 'A Discord fiókod még nincs összekötve egy Minecraft fiókkal sem. Ezt a **/link** paranccsal teheted meg!', ephemeral: true });
        }
        cooldown?.addCooldown(member.id, config.commandCooldowns.ip);
        await query("UPDATE links SET ip = ? WHERE discord_id = ?", [ip, interaction.user.id]);
        return interaction.reply({ content: `**Sikeresen** frissítetted az IP címedet a következőre: \`${ip}\``, ephemeral: true });
    }
}
import { Command } from '@sapphire/framework';
import { config } from '../config';
import query from '../database/db';
import { CooldownService } from '../services/CooldownService';


export class LinkCommand extends Command {
    private cooldownService = new CooldownService("link");

    public constructor(context: Command.LoaderContext, options: Command.Options) {
        super(context, {
            ...options, description: 'Minecraft csatlakoztatása', preconditions: ['GuildOnly']
        });
    }

    public override registerApplicationCommands(registry: Command.Registry) {
        registry.registerChatInputCommand((builder) =>
            builder.setName('link')
                .setDescription('Discord felhasználó összekapcsolása a Minecraft karakterrel.')
                .addStringOption((option) => option.setName('játékosnév')
                    .setDescription('Minecraft neved (kis és nagybetű számít!)')
                    .setMinLength(3)
                    .setMaxLength(16)
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

        const playerName = (interaction.options.getString('játékosnév') || '').trim();

        if (!playerName || !/^[a-zA-Z0-9_]{3,16}$/.test(playerName)) {
            return interaction.reply({ content: 'A játékosnév **csak kis és nagybetűket, számokat és alulvonást tartalmazhat**!', ephemeral: true }); {
            }
        }
        const isUsernameFree = (await query('SELECT * FROM links WHERE username = ?', [playerName]) as any[]).length === 0;
        if (!isUsernameFree) {
            return interaction.reply({ content: 'Ez a Minecraft karakter **már össze van kapcsolva** egy Discord fiókkal!', ephemeral: true });
        }

        const isAlreadyLinked = (await query('SELECT * FROM links WHERE discord_id = ?', [interaction.user.id]) as any[]).length > 0;

        cooldown?.addCooldown(member.id, config.commandCooldowns.link);
        if (isAlreadyLinked) {
            // await query('UPDATE links SET username = ? WHERE discord_id = ?', [playerName, interaction.user.id]);
            // return interaction.reply({ content: `**Sikeresen** frissítetted a Minecraft karaktered! (${playerName})`, ephemeral: true });
            return interaction.reply({ content: 'Ez a Discord fiók **már össze van kapcsolva** egy Minecraft karakterrel! (**/unlink** a szétkapcsoláshoz)', ephemeral: true });
        }

        await query('INSERT INTO links (discord_id, username) VALUES (?, ?)', [interaction.user.id, playerName]);
        member.roles.add('1307294590901944370');
        return interaction.reply({ content: `**Sikeresen** összekapcsoltad fiókjaid! (${playerName})`, ephemeral: true });
    }
}
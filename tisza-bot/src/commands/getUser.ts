import { Command } from '@sapphire/framework';
import { config } from '../config';
import query from '../database/db';
import { PermissionFlagsBits } from 'discord.js';


export class LinkCommand extends Command {

    public constructor(context: Command.LoaderContext, options: Command.Options) {
        super(context, {
            ...options, description: 'Minecraft csatlakoztatása', preconditions: ['GuildOnly']
        });
    }

    public override registerApplicationCommands(registry: Command.Registry) {
        registry.registerChatInputCommand((builder) =>
            builder.setName('getuser')
                .setDescription('Linkelt fiókok lekérdezése.')
                .addStringOption((option) => option.setName('játékosnév')
                    .setDescription('Minecraft név')
                    .setMinLength(3)
                    .setMaxLength(16))
                .addUserOption((option) => option.setName("felhasználó")
                    .setDescription("Discord felhasználó")), { guildIds: [config.guildId] }
        );
    }

    public override async chatInputRun(interaction: Command.ChatInputCommandInteraction) {
        const member = await interaction.guild?.members.fetch(interaction.user.id);
        if (!member) {
            return;
        }

        if (!member.permissions.has(PermissionFlagsBits.ModerateMembers)) {
            return interaction.reply({ content: 'Nincs jogosultságod a parancs használatához!', ephemeral: true });
        }

        const user = interaction.options.getUser('felhasználó');
        const playerName = (interaction.options.getString('játékosnév') || '').trim();
        if (playerName) {
            const players = (await query('SELECT * FROM links WHERE username = ?', [playerName]) as any[]);
            if (players.length == 0) {
                return interaction.reply({ content: 'A megadott játékosnév nincs egy fiókhoz sem kapcsolva', ephemeral: true });
            }
            return interaction.reply({ content: `A megadott játékosnévhez tartozó fiók: <@${players[0].discord_id}>`, ephemeral: true });
        } else if (user) {
            const players = (await query('SELECT * FROM links WHERE discord_id = ?', [user.id]) as any[]);
            if (players.length == 0) {
                return interaction.reply({ content: 'A megadott felhasználó nincs csatolva egy játékosnévhez sem', ephemeral: true });
            }
            return interaction.reply({ content: `A megadott felhasználóhoz tartozó játékosnév: **${players[0].username}**`, ephemeral: true });
        }

        return interaction.reply({ content: 'Nem adtál meg egy azonosítót sem!', ephemeral: true });
    }
}
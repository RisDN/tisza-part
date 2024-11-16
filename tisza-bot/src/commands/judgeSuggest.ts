import { Command } from '@sapphire/framework';
import { EmbedBuilder, PermissionFlagsBits, TextBasedChannel } from 'discord.js';
import { config } from '../config';

export class SuggestCommand extends Command {

    public constructor(context: Command.LoaderContext, options: Command.Options) {
        super(context, {
            ...options, description: 'Javaslat írása', preconditions: ['GuildOnly']
        });
    }

    public override registerApplicationCommands(registry: Command.Registry) {
        registry.registerChatInputCommand((builder) =>
            builder.setName('judgesuggest')
                .setDescription('Javaslat elbírálása')
                .addStringOption((option) => option.setName('javaslatid')
                    .setDescription('Javaslat azonosítója')
                    .setRequired(true))
                .addBooleanOption((option) => option.setName('elfogadás')
                    .setDescription('Elfogadás')
                    .setRequired(true))
                .addStringOption((option) => option.setName('indok')
                    .setDescription('Döntés indoklása')),
            { guildIds: [config.guildId] }
        );
    }

    public override async chatInputRun(interaction: Command.ChatInputCommandInteraction) {
        const member = await interaction.guild?.members.fetch(interaction.user.id);
        if (!member) {
            return;
        }
        if (!member.permissions.has(PermissionFlagsBits.ManageChannels)) {
            return interaction.reply({ content: 'Nincs jogosultságod a parancs használatához!', ephemeral: true });
        }
        const channel = interaction.guild?.channels.cache.get(config.channels.suggestion) as TextBasedChannel;
        if (!channel) {
            return;
        }
        const message = await channel.messages.fetch(interaction.options.getString('javaslatid', true));
        if (!message) {
            return interaction.reply({ content: 'Nem található a javaslat!', ephemeral: true });
        }

        if (!message.embeds[0]) {
            return interaction.reply({ content: 'Nem található a javaslat!', ephemeral: true });
        }

        const suggestionEmbed = EmbedBuilder.from(message.embeds[0]);
        const accepted = interaction.options.getBoolean('elfogadás', true);
        const reason = interaction.options.getString('indok', false);
        if (reason) {
            suggestionEmbed.addFields({ name: 'Döntés indoklása:', value: reason });
        }
        suggestionEmbed.setColor(accepted ? '#00b000' : '#ff0000');

        await message.edit({ embeds: [suggestionEmbed] });

        return interaction.reply({
            content: 'Javaslat elbírálva!',
            ephemeral: true
        }).then(message => {
            setTimeout(() => {
                message.delete().catch(() => { });
            }, 5000);
        });
    }
}
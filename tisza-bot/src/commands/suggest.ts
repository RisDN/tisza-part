import { Command } from '@sapphire/framework';
import { EmbedBuilder, TextBasedChannel, TextChannel, DMChannel, NewsChannel } from 'discord.js';
import { config } from '../config';
import { CooldownService } from '../services/CooldownService';

function isSendableChannel(channel: TextBasedChannel): channel is TextChannel | DMChannel | NewsChannel {
    return 'send' in channel;
}

export class SuggestCommand extends Command {
    private cooldownService = new CooldownService("suggest");

    public constructor(context: Command.LoaderContext, options: Command.Options) {
        super(context, {
            ...options, description: 'Javaslat írása', preconditions: ['GuildOnly']
        });
    }

    public override registerApplicationCommands(registry: Command.Registry) {
        registry.registerChatInputCommand((builder) =>
            builder.setName('suggest')
                .setDescription('Oszd meg velünk ötleteidet, hogy szerverünk tovább fejlődhessen!')
                .addStringOption((option) => option.setName('javaslat')
                    .setDescription('Javaslat szövege')
                    .setMinLength(10)
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
                content: `Ezt a parancsot csak 10 percenként használhatod!`,
                ephemeral: true
            }).then(message => {
                setTimeout(() => {
                    message.delete().catch(() => { });
                }, 5000);
            });
            return
        } else if (cooldown) {
            cooldown.addCooldown(interaction.user.id, config.commandCooldowns.suggest);
        }

        const suggestEmbed = new EmbedBuilder()
            .addFields(
                { name: 'Felhasználó', value: member.displayName },
                { name: 'Javaslat', value: interaction.options.getString('javaslat') || "" },
            )
            .setThumbnail("https://cdn.discordapp.com/attachments/1301937623987916851/1307031654862422047/server-icon.png?ex=67397c55&is=67382ad5&hm=2d65643b95bcba8c6b09a275c744c0815c9d5f4891658f6efd7782f4bbfeb1ce&")
            .setColor('#fca503')
            .setFooter({ text: 'Új javaslat', iconURL: member.user.avatarURL() || "" })
            .setTimestamp();

        const channel = interaction.guild?.channels.cache.get(config.channels.suggestion) as TextBasedChannel;
        if (!channel) {
            return;
        }
        if (isSendableChannel(channel)) {
            channel.send({ embeds: [suggestEmbed] }).then(message => {
                message.react('1307299981643874304');
                message.react('1307299952271167528');
            });
        }

        interaction.reply({
            content: `Javaslatod sikeresen elküldve!`,
            ephemeral: true
        }).then(message => {
            setTimeout(() => {
                message.delete().catch(() => { });
            }, 5000);
        });
    }
}
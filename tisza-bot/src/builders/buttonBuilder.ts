import { ActionRowBuilder, ButtonBuilder, ButtonStyle } from 'discord.js';

export function buttonBuilder({ customId, label, style }: { customId: string, label: string, style: ButtonStyle }): ButtonBuilder {
    const button = new ButtonBuilder()
        .setCustomId(customId)
        .setLabel(label)
        .setStyle(style);

    return button;
}

export function concatButtons(buttons: ButtonBuilder[]): ActionRowBuilder<ButtonBuilder> {
    const row = new ActionRowBuilder<ButtonBuilder>().addComponents(buttons);
    return row;
}
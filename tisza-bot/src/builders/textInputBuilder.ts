import { TextInputBuilder, TextInputStyle } from 'discord.js';

export type TextInputOptions = {
    customId: string,
    label: string,
    style: TextInputStyle
    placeholder?: string
    minLength?: number
    maxLength?: number
}

export function textInputBuilder({ customId, label, style, placeholder, minLength, maxLength }: TextInputOptions): TextInputBuilder {
    if (!maxLength) {
        switch (style) {
            case TextInputStyle.Paragraph:
                maxLength = 4000;
                break;
            case TextInputStyle.Short:
                maxLength = 45;
                break;
        }
    } else if (style == TextInputStyle.Paragraph && maxLength > 4000) {
        maxLength = 4000;
    } else if (style == TextInputStyle.Short && maxLength > 45) {
        maxLength = 45;
    }

    const textInput = new TextInputBuilder()
        .setCustomId(customId)
        .setLabel(label)
        .setPlaceholder(placeholder || "")
        .setMinLength(minLength || 1)
        .setMaxLength(maxLength)
        .setStyle(style);

    return textInput;
}

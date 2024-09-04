import { ButtonStyle, Client, Collection, EmbedBuilder, Message, TextBasedChannel } from "discord.js";
import { config } from "../config";
import { buttonBuilder, concatButtons } from "../builders/buttonBuilder";

export async function resendApplications(client: Client) {
    const guild = await client.guilds.fetch('868163206911254538');
    for (let i in config.applicationChannels) {
        const channel = await guild.channels.cache.get(config.applicationChannels[i]) as TextBasedChannel;
        let messages: any = [];
        let lastID;
        let whileC = true;
        while (whileC) {

            const fetchedMessages: Collection<string, Message> = await channel.messages.fetch({
                limit: 100, ...(lastID && { before: lastID }),
            });

            if (fetchedMessages.size === 0) {
                messages = messages.filter((msg: Message) => msg.author.bot);
                messages = messages.filter((msg: Message) => msg.embeds[0]);
                messages = messages.filter((msg: Message) => msg.embeds[0].color == 14813165)
                for (let i in messages) {
                    const oldEmbed = messages[i].embeds[0];
                    const newEmbed = new EmbedBuilder(oldEmbed);
                    const memberId = oldEmbed.fields[0].value.replace('<', '').replace('>', '').replace('@', '');

                    const approve = buttonBuilder({ customId: `approve_${memberId}`, label: "Elfogadás", style: ButtonStyle.Success });
                    const reject = buttonBuilder({ customId: `decline_${memberId}`, label: "Elutasítás", style: ButtonStyle.Danger });
                    channel.send({
                        content: `<@${memberId}>`,
                        embeds: [newEmbed],
                        components: [concatButtons([approve, reject])]
                    });
                    messages[i].delete();
                    await sleep(1000);
                }
                if (config.applicationChannels.length == parseInt(i) + 1) {
                    return;
                }
                whileC = false;
            }
            messages = messages.concat(Array.from(fetchedMessages.values()));
            lastID = fetchedMessages.lastKey();
        }
    }
}

let j = 0;
export async function sendMessage(client: Client, data: any) {
    const discordId = !data.discordId ? 'nincs' : data.discordId;
    const name = !data.name ? 'nincs' : data.name;
    const gender = !data.gender ? 'nincs' : data.gender;
    const birthDate = !data.birthDate ? 'nincs' : data.birthDate;
    const link = !data.link ? 'nincs' : data.link;

    const answers = [
        data.answ1,
        data.answ2,
        data.answ3,
        data.answ4,
        data.answ5,
        data.answ6,
    ]

    const guild = client.guilds.cache.get(config.guildId);
    if (!guild) return;
    const member = await guild.members.fetch(discordId);
    let sum = 0
    for (let i = 0; i < answers.length; i++) {
        sum += answers[i].length
    }
    if (sum > 5000) {
        answers[5] = answers[5].substring(0, 300)
    }

    let channel;
    if (j < config.applicationChannels.length) {
        channel = guild.channels.cache.get(config.applicationChannels[j]) as TextBasedChannel;
        j++;
    } else {
        j = 0;
        channel = guild.channels.cache.get(config.applicationChannels[j]) as TextBasedChannel;
    }

    if (channel != null) {
        const applyEmbed = new EmbedBuilder()
            .setTitle('Új jelentkezés')
            .setDescription(`https://galaxyrp.hu/${link}`)
            .addFields(
                { name: 'Felhasználó', value: `<@${member.id}>` || "(hibás adat)", inline: true },
                { name: 'Neve', value: name || "(hibás adat)", inline: true },
                { name: 'Neme', value: gender || "(hibás adat)", inline: true },
                { name: 'Születési dátum', value: birthDate || "(hibás adat)", inline: true },
                // { name: 'Ped', value: ped || "(hibás adat)", inline: true },
                { name: 'Véleményed szerint mik a pozitív és a negatív dolgok a szabályzatban?', value: answers[0].substring(0, 1000) || "(hibás adat)" },
                { name: 'Mi a definíciója a szerepjátéknak?', value: answers[1].substring(0, 1000) || "(hibás adat)" },
                { name: 'Amennyiben játszottál másik szerveren milyen karaktert rp-ztél eddig és mennyi időt töltöttél el ebben a játékstílusban?', value: answers[2].substring(0, 1000) || "(hibás adat)" },
                { name: 'Milyen típusú karaktert tervezel?', value: answers[3].substring(0, 1000) || "(hibás adat)" },
                { name: 'Milyen pozitív és negatív tulajdonságai vannak a karakterednek?', value: answers[4].substring(0, 1000) || "(hibás adat)" },
                { name: 'Mivel tudnál hozzátenni a Galaxy RP közösségéhez?', value: answers[5].substring(0, 1000) || "(hibás adat)" },
            )
            .setThumbnail("https://cdn.discordapp.com/attachments/897135580952019034/897135615437582356/G_Galaxy_transparent.png")
            .setColor('#e207ed')
            .setFooter({ text: member.displayName, iconURL: member.user.avatarURL() || "" })
            .setTimestamp();
        const approve = buttonBuilder({ customId: `approve_${member.id}`, label: "Elfogadás", style: ButtonStyle.Success });
        const reject = buttonBuilder({ customId: `decline_${member.id}`, label: "Elutasítás", style: ButtonStyle.Danger });
        channel.send({
            content: `<@${member.id}>`,
            embeds: [applyEmbed],
            components: [concatButtons([approve, reject])]
        });
    } else {
        console.log('Nem található a jelentkezési csatorna!');
    }

}

function sleep(ms: number) {
    return new Promise((resolve) => {
        setTimeout(resolve, ms);
    });
}


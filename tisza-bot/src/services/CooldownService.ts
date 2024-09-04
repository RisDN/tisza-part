export class CooldownService {

    public static COOLDOWN_SERVICES: CooldownService[] = [];
    private cooldownMap = new Map<string, Date>();
    private command: string;

    constructor(command: string) {
        CooldownService.COOLDOWN_SERVICES.push(this);
        this.command = command;
    }

    public addCooldown(userId: string, cooldwon: number) {
        this.cooldownMap.set(userId, new Date(Date.now() + cooldwon));
    }

    private getCooldown(userId: string): Date {
        const cooldown = this.cooldownMap.get(userId);
        if (!cooldown)
            return new Date(0);
        else
            return cooldown;
    }

    public getCooldownMessage(userId: string): string {
        const cooldown = this.getCooldown(userId);
        if (!cooldown)
            return "";
        else {
            const timeInMinutes = (cooldown.getTime() - Date.now()) / 60000;
            if (timeInMinutes < 1) {
                const formattedTime = Math.ceil(timeInMinutes * 60);
                return `Ezt a parancsot nem használhatod még **${formattedTime}** másodpercig!`;
            }
            const formattedTime = Math.round(timeInMinutes * 10) / 10;
            return `Ezt a parancsot nem használhatod még **${formattedTime}** percig!`;
        }
    }

    public hasCooldown(userId: string): boolean {
        const cooldown = this.getCooldown(userId);
        if (!cooldown) return false;
        if (cooldown > new Date()) return true;
        return false;
    }

    public static getCooldownService(command: string): CooldownService | undefined {
        for (const service of CooldownService.COOLDOWN_SERVICES) {
            if (service.command === command) {
                return service;
            }
        }
        return undefined;
    }

}
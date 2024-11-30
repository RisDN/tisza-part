package hu.ris.tiszartp;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class RandomTP {
    private boolean found = false;
    private final Player p;
    private final Location center;
    private final Double size;
    private int tries = 0;
    private boolean isNether;

    public RandomTP(Player p) {
        World w = Bukkit.getWorld(TiszaRTP.getInstance().getConfig().getString("modules.randomtp.world"));
        this.center = w.getWorldBorder().getCenter();
        this.isNether = TiszaRTP.isNether();
        this.size = TiszaRTP.getInstance().getConfig().getDouble("modules.randomtp.size");
        this.p = p;
        findLocation();
    }

    public void findLocation() {
        Location loc = this.center.clone();
        loc.add(ThreadLocalRandom.current().nextDouble(this.size.doubleValue() / -2.0D, this.size.doubleValue() / 2.0D), 0.0D, ThreadLocalRandom.current().nextDouble(this.size.doubleValue() / -2.0D, this.size.doubleValue() / 2.0D));

        loc.getWorld().getChunkAtAsync(loc, false).thenAccept(chunk -> {
            this.tries++;
            int topY = chunk.getChunkSnapshot().getHighestBlockYAt(8, 8);
            Location floc = chunk.getBlock(8, topY, 8).getLocation();
            Block block = chunk.getBlock(8, topY, 8);
            if (block.isSolid() && !block.getType().isAir()) {
                floc.add(0.0D, 1.0D, 0.0D);
                this.found = true;
                this.p.teleportAsync(floc);
                this.p.sendMessage(TiszaRTP.getMessage("rtp.success"));
            }
            if (this.found)
                return;
            if (this.tries >= TiszaRTP.getInstance().getConfig().getInt("modules.randomtp.max-tries")) {
                this.p.sendMessage(TiszaRTP.getMessage("rtp.failed"));
                return;
            }
            findLocation();
        });
    }
}

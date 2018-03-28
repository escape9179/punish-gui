package logan.punishgui;

import com.earth2me.essentials.User;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Tre Logan
 */
public class MenuLoader {

    public Menu loadBanMenu(Player player, FileConfiguration config) {
        ClickHandler.removeAll();

        Menu menu = new Menu("Ban Options");

        List<String> stringList = config.getStringList("ban");

        int slot = 0;
        for (String string : stringList) {

            String[] parts = string.split("->");
            String reason = parts[0].trim();
            String timeString = parts[1].trim();

            ItemStack itemStack = new ItemStack(Material.STONE_AXE);
            MenuItem menuItem = new MenuItem(reason + " -> " + timeString, itemStack);

            final long banTime = getTime(timeString);
            menuItem.setOnClick((e) -> {
                User user = PunishPlugin.getEssentials().getUser(player);
                LocalDateTime ldt = LocalDateTime.now().plus(banTime, ChronoUnit.MILLIS);
                Bukkit.getBanList(Type.NAME).addBan(player.getName(), reason, Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()), null);
                player.kickPlayer("Banned: " + reason);
                e.getPlayer().sendMessage("Banned " + player.getName() + " for " + banTime);
            });

            menu.addMenuItem(menuItem, slot);

            slot++;

        }
        
        return menu;
    }

    public Menu loadKickMenu(Player player, FileConfiguration config) {
        ClickHandler.removeAll();

        Menu menu = new Menu("Kick Options");

        List<String> stringList = config.getStringList("kick");

        int slot = 0;
        for (String reason : stringList) {

            ItemStack itemStack = new ItemStack(Material.BOW);
            MenuItem menuItem = new MenuItem(reason, itemStack);

            menuItem.setOnClick((e) -> {
                player.kickPlayer(reason);
                e.getPlayer().sendMessage("Kicked " + player.getName() + " for " + reason);
            });

            menu.addMenuItem(menuItem, slot);

            slot++;

        }
        
        return menu;
    }

    public Menu loadMuteMenu(Player player, FileConfiguration config) {
        ClickHandler.removeAll();

        Menu menu = new Menu("Mute Options");

        List<String> stringList = config.getStringList("mute");

        int slot = 0;
        for (String string : stringList) {

            String[] parts = string.split("->");
            String reason = parts[0].trim();
            String timeString = parts[1].trim();

            ItemStack itemStack = new ItemStack(Material.ARROW);
            MenuItem menuItem = new MenuItem(reason + " -> " + timeString, itemStack);

            final long muteTimeout = getTime(timeString);
            menuItem.setOnClick((e) -> {
                User user = PunishPlugin.getEssentials().getUser(player);
                user.setMuteTimeout(muteTimeout);
                e.getPlayer().sendMessage("Muted " + player.getName() + " for " + muteTimeout);
            });

            menu.addMenuItem(menuItem, slot);

            slot++;

        }

        return menu;
    }

    private long getTime(String timeString) {
        Pattern pattern = Pattern.compile("\\d+\\D{1}");
        Matcher matcher = pattern.matcher(timeString);

        long timeout = 0;
        while (matcher.find()) {

            String match = timeString.substring(matcher.start(), matcher.end());

            int time = Integer.parseInt(match.substring(0, match.length() - 1));
            char timeUnit = match.charAt(match.length() - 1);

            System.out.println("Time: " + time + ", Unit: " + timeUnit);

            switch (timeUnit) {
                case 'd':
                    timeout += TimeUnit.DAYS.toMillis(time);
                    break;
                case 'h':
                    timeout += TimeUnit.HOURS.toMillis(time);
                    break;
                case 'm':
                    timeout += TimeUnit.MINUTES.toMillis(time);
                    break;
                case 's':
                    timeout += TimeUnit.SECONDS.toMillis(time);
                    break;
                default:
                    break;
            }

        }

        return timeout;
    }

}

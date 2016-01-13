package fr.xephi.authme.process.email;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.cache.auth.PlayerAuth;
import fr.xephi.authme.cache.auth.PlayerCache;
import fr.xephi.authme.datasource.DataSource;
import fr.xephi.authme.output.MessageKey;
import fr.xephi.authme.output.Messages;
import fr.xephi.authme.settings.Settings;
import fr.xephi.authme.util.StringUtils;
import org.bukkit.entity.Player;

/**
 * Async task to add an email to an account.
 */
public class AsyncAddEmail {

    private AuthMe plugin;
    private Player player;
    private String email;
    private Messages messages;

    public AsyncAddEmail(AuthMe plugin, Player player, String email) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
        this.player = player;
        this.email = email;
    }

    public void process() {
        String playerName = player.getName().toLowerCase();
        PlayerCache playerCache = PlayerCache.getInstance();

        if (playerCache.isAuthenticated(playerName)) {
            PlayerAuth auth = PlayerCache.getInstance().getAuth(playerName);
            String currentEmail = auth.getEmail();

            if (currentEmail == null) {
                messages.send(player, MessageKey.USAGE_CHANGE_EMAIL);
            } else if (StringUtils.isEmpty(email) || "your@email.com".equals(email) || Settings.isEmailCorrect(email)) {
                messages.send(player, MessageKey.INVALID_EMAIL);
            } else {
                auth.setEmail(email);
                playerCache.updatePlayer(auth);
                messages.send(player, MessageKey.EMAIL_ADDED_SUCCESS);
            }
        } else {
            sendUnloggedMessage(plugin.getDataSource());
        }
    }

    private void sendUnloggedMessage(DataSource dataSource) {
        if (dataSource.isAuthAvailable(player.getName())) {
            messages.send(player, MessageKey.LOGIN_MESSAGE);
        } else if (Settings.emailRegistration) {
            messages.send(player, MessageKey.REGISTER_EMAIL_MESSAGE);
        } else {
            messages.send(player, MessageKey.REGISTER_MESSAGE);
        }
    }

}

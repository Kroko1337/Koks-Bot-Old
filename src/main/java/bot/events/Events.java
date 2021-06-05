package bot.events;

import bot.Main;
import bot.manager.Setting;
import bot.manager.command.Command;
import bot.manager.command.CommandManager;
import bot.manager.file.FileManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author kroko
 * @created on 18.01.2021 : 19:35
 */
public class Events extends ListenerAdapter {

    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event) {
        final User user = event.getInvite().getInviter();

        if (!event.getGuild().getName().contains("Public") && user != null && !user.isBot() && event.getInvite().getInviter() != null) {
            final String userName = user.getName();
            System.out.println("Invite created by " + userName);

            user.openPrivateChannel().complete().sendMessage("Please not create Invites!").queue();
            try {
                Main.staffAlert(event.getGuild().getMember(event.getInvite().getInviter()), "created a Invite!", event.getGuild());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            event.getInvite().delete().queue();
        }
        super.onGuildInviteCreate(event);
    }

    @Override
    public void onGuildInviteDelete(@NotNull GuildInviteDeleteEvent event) {
        final Setting setting = Main.getSettings(event.getGuild());
        if (setting != null) {
            setting.INVITES.remove(event.getCode());
        }
        super.onGuildInviteDelete(event);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        if (!Main.SETTINGS.containsKey(event.getGuild().getId())) {
            Main.SETTINGS.put(event.getGuild().getId(), new Setting(event.getGuild()));
            try {
                FileManager.loadGuild(event.getGuild());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Main.ircClient.setIngameName("BedIntruder");
        Main.ircClient.setMcServerIp("gommehd.net");

        for (User user : event.getGuild().getJDA().getUsers()) {
            final Setting setting = Main.getSettings(event.getGuild());
            final Member member = event.getGuild().getMember(user);
            if (setting != null) {
                if (setting.KOKS_USER.containsKey(user.getId()) && !user.getName().endsWith("✓") && member != null) {
                    event.getGuild().modifyNickname(member, user.getName() + " ✓").queue();
                }
            }
        }
        super.onGuildReady(event);
    }

    @Override
    public void onGenericUserPresence(GenericUserPresenceEvent event) {
        for (Activity activity : event.getMember().getActivities()) {
            if (activity.isRich()) {
                final RichPresence presence = activity.asRichPresence();
                assert presence != null;
                if (presence.getApplicationId().equalsIgnoreCase("842470849013743677")) {
                    Role koksRole = null;
                    //TODO: Verify System integration (CL Name Updaten)
                    String detail = "";
                    if (presence.getDetails() != null && presence.getDetails().contains("(") && presence.getDetails().split(" ").length >= 2) {
                        detail = " " + presence.getDetails().split(" ")[2];
                    }

                    for (Role role : event.getGuild().getRoles()) {
                        if (role.getName().equalsIgnoreCase("Koks User"))
                            koksRole = role;
                    }

                    if (koksRole != null) {
                        event.getGuild().addRoleToMember(event.getMember(), koksRole).queue();
                        System.out.println(event.getMember().getUser().getAsTag() + " has verified!" + detail);
                    } else {
                        System.out.println(event.getMember().getUser().getAsTag() + " wanted to verify but the role doesn't exist!");
                    }
                } else if (presence.getName().equalsIgnoreCase("Koks")) {
                    try {
                        Main.staffAlert(event.getMember(), "tried to fake Koks Verification", event.getGuild());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        super.onGenericUserPresence(event);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final Message message = event.getMessage();
        final Setting setting = Main.getSettings(event.getGuild());
        String msg = message.getContentRaw();
        String[] args = msg.split(" ");

        if (setting != null && event.getMember() != null)
            if (setting.KOKS_USER.containsKey(event.getMember().getUser().getId()) && !event.getMember().getUser().getName().endsWith("✓")) {
                event.getGuild().modifyNickname(event.getMember(), event.getMember().getUser().getName() + " ✓").queue();
            }

        if (message.getTextChannel().getName().equalsIgnoreCase("irc") && event.getMember() != null && !event.getMember().getUser().isBot()) {
            if (msg.startsWith("#")) {
                Main.ircClient.executeCommand(msg.substring(1));
            } else {
                Main.ircClient.sendChatMessage(msg);
            }
        }

        if (event.getTextChannel().getParent() != null && event.getMember() != null) {
            if (event.getTextChannel().getParent().getName().equalsIgnoreCase("Support") && event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
                if (msg.contains("<@&")) {
                    String roleId;
                    int beginSub = -1;
                    int endSub = -1;
                    for (int i = 0; i < msg.length(); i++) {
                        String cur = msg.substring(i, i + 1);
                        if (cur.equalsIgnoreCase("&")) {
                            String before = msg.substring(i - 2, i);
                            if (before.equalsIgnoreCase("<@")) {
                                beginSub = i + 1;
                            }
                        }

                        if (beginSub != -1) {
                            if (cur.equalsIgnoreCase(">"))
                                endSub = i;
                        }
                    }

                    roleId = msg.substring(beginSub, endSub);

                    Role role = event.getGuild().getRoleById(roleId);

                    Collection<Permission> collection = new ArrayList<>();
                    collection.add(Permission.VIEW_CHANNEL);
                    collection.add(Permission.MESSAGE_WRITE);

                    assert role != null;

                    PermissionOverride permissionOverride = event.getTextChannel().getPermissionOverride(role);
                    if (permissionOverride == null)
                        permissionOverride = event.getTextChannel().createPermissionOverride(role).complete();

                    permissionOverride.getManager().setAllow(collection).queue();
                }
            }
        }

        if (args.length == 2 && (args[0].substring(1).equalsIgnoreCase("pause") || args[0].substring(1).equalsIgnoreCase("resume")))
            if (args[0].startsWith(CommandManager.PREFIX))
                if (Integer.parseInt(args[1]) == Main.botID)
                    if (args[0].substring(1).equalsIgnoreCase("pause")) {
                        Main.paused = true;
                        Main.sendEmbed(event, "Paused me :c", "", Command.ColorType.ERROR.getColor());
                    } else if (args[0].substring(1).equalsIgnoreCase("resume")) {
                        Main.paused = false;
                        Main.sendEmbed(event, "Resumed me c:", "", Command.ColorType.SUCCESSFULLY.getColor());
                    }

        if (msg.startsWith(CommandManager.PREFIX) && !Main.paused) {
            for (Command command : Main.COMMAND_MANAGER.getCommands()) {
                if (args[0].substring(1).equalsIgnoreCase(command.command())) {
                    if (command.requiredPermission() == null || Objects.requireNonNull(event.getMember()).getPermissions().contains(command.requiredPermission())) {
                        try {
                            command.execute(Arrays.copyOfRange(args, 1, args.length), event);
                            event.getMessage().delete().queueAfter(20, TimeUnit.SECONDS);
                        } catch (Exception ignore) {
                        }
                    } else {
                        Main.sendEmbed(event, "You have no Permissions to do that!", "Needed Permissions: " + command.requiredPermission().getName(), new Color(255, 0, 54));
                    }
                }
            }
        }
        super.onMessageReceived(event);
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        /*if(event.getMember().getUser().getId().equalsIgnoreCase("264720350904844298")) {
            event.getGuild().modifyNickname(event.getMember(), "Durt").queue();
        }*/
        final Setting setting = Main.getSettings(event.getGuild());
        if (setting != null) {
            if (setting.KOKS_USER.containsKey(event.getUser().getId()) && event.getNewNickname() != null && !event.getNewNickname().endsWith("✓")) {
                event.getGuild().modifyNickname(event.getMember(), event.getNewNickname() + " ✓").queue();
            }
        }
        super.onGuildMemberUpdateNickname(event);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Welcome " + event.getUser().getName());
        eb.setDescription("Please read the rules before chatting!");
        eb.setThumbnail(event.getUser().getEffectiveAvatarUrl());
        eb.setColor(new Color(26, 255, 0));
        final List<Invite> invites = event.getGuild().retrieveInvites().complete();
        for (Invite invite : invites) {
            if (invite.getUses() == 1) {
                final Setting setting = Main.getSettings(event.getGuild());
                if (setting != null) {
                    final String clName = setting.INVITES.get(invite.getCode());
                    if (setting.KOKS_USER.containsValue(clName.toLowerCase())) {
                        for (String id : setting.KOKS_USER.keySet()) {
                            final String name = setting.KOKS_USER.get(id);
                            if (name.equalsIgnoreCase(clName) && !id.equals(event.getUser().getId()))
                                event.getMember().ban(0, "Account Sharing").complete();
                        }
                    } else {
                        setting.KOKS_USER.put(event.getUser().getId(), clName.toLowerCase());

                        if (setting.KOKS_USER.containsKey(event.getUser().getId()) && !event.getUser().getName().endsWith("✓")) {
                            event.getGuild().modifyNickname(event.getMember(), event.getUser().getName() + " ✓").queue();
                        }

                        Role koksRole = null;

                        for (Role role : event.getGuild().getRoles()) {
                            if (role.getName().equalsIgnoreCase("Koks User"))
                                koksRole = role;
                        }

                        if (koksRole != null) {
                            event.getGuild().addRoleToMember(event.getMember(), koksRole).queue();
                            System.out.println(event.getMember().getUser().getAsTag() + " has verified!");
                        } else {
                            System.out.println(event.getMember().getUser().getAsTag() + " wanted to verify but the role doesn't exist!");
                        }
                        try {
                            FileManager.saveGuild(event.getGuild());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    invite.delete().complete();
                }
            }
        }
        event.getGuild().getTextChannelsByName("\uD83D\uDC4B・welcome", true).get(0).sendMessage(eb.build()).queue();
        super.onGuildMemberJoin(event);
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Bye " + event.getUser().getName() + " :c");
        eb.setThumbnail(event.getUser().getEffectiveAvatarUrl());
        eb.setDescription("Good bye!");
        eb.setColor(new Color(255, 0, 51));
        event.getGuild().getTextChannelsByName("\uD83D\uDC4B・welcome", true).get(0).sendMessage(eb.build()).queue();
        final Setting setting = Main.getSettings(event.getGuild());
        if (setting != null) {
            setting.KOKS_USER.remove(event.getUser().getId());
        }
        super.onGuildMemberLeave(event);
    }
}

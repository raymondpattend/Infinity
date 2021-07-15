package com.reflexian.discordbot.commands.music;

import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.listeners.CommandListener;
import com.reflexian.discordbot.utilities.UtilStrings;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Music extends Command {


    private static final AudioPlayerManager myManager = new DefaultAudioPlayerManager();
    private static final Map<String, Map.Entry<AudioPlayer, TrackManager>> players = new HashMap<>();

    private static final String CD = "\uD83D\uDCBF";
    private static final String DVD = "\uD83D\uDCC0";
    private static final String MIC = "\uD83C\uDFA4 **|>** ";

    private static final String QUEUE_TITLE = "__%s has added %d new track%s to the Queue__";
    private static final String QUEUE_DESCRIPTION = "%s **|>**  %s\n%s\n%s %s\n%s";
    private static final String QUEUE_INFO = "Info about the Queue: (Size - %d)";
    private static final String ERROR = "Error while loading \"%s\"";

    private final EmbedBuilder help = new EmbedBuilder().setTitle("Help - Music").setDescription("Sub Commands start with <@775250061504413727>").addField("Commands","Play <url> **-** Play a YouTube URL\nPlay <search> **-** Search for a YouTube Title to play\nInfo **-** Get information about the current song\nVolume **-** Set the volume of the bot\nPause **-** Pause the current track\nPlay **-** Play the current track\nSkip **-** Cast a vote to skip the current track\nForceSkip **-** Force a skip on the current track*\nQueue **-** View the song queue\nReset **-** Reset the music player*",false).addField("Description", "The music feature allows you to play content from YouTube through the bot.", false).addField("Permission", "Commands ending with * require ``DJ`` role or ``MANAGE_MESSAGES`` permission.", false).addField("Example","```@CoinMan#3243 music play All Star - Smash Mouth```", false).setColor(new Color(50, 122, 182));

    public Music(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }




    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        Guild guild = event.getGuild();

        switch (args.length) {
            case 2:
                sendMessage(event.getTextChannel(), help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).build(), 25);
                return;
            case 3:
                switch (args[2].toLowerCase()) {
                    case "volume":
                        sendMessage(event.getTextChannel(), "Incorrect Usage.\n``@CoinMan#3243 music volume (1-100)", 15);
                        return;
                    case "queue":
                        if (!hasPlayer(guild) || getTrackManager(guild).getQueuedTracks().isEmpty()) {
                            sendMessage(event.getTextChannel(), "The queue is empty! Load a song with ``@CoinMan#3243 music play``!", 30);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            Set<AudioInfo> queue = getTrackManager(guild).getQueuedTracks();
                            queue.forEach(audioInfo -> sb.append(buildQueueMessage(audioInfo)));
                            String embedTitle = String.format(QUEUE_INFO, queue.size());

                            if (sb.length() <= 1960) {
                                EmbedBuilder em = new EmbedBuilder().setTitle(embedTitle).setDescription("**>** " + sb.toString()).setFooter("WARNING: This feature may become part of a membership.");;
                                sendMessage(event.getTextChannel(),em.build(),30);
                            } else /* if (sb.length() <= 20000) */ {
                                event.getChannel().sendTyping().queue();
                                File qFile = new File("queue.txt");
                                try {
                                    FileUtils.write(qFile, sb.toString(), "UTF-8", false);
                                    event.getChannel().sendFile(qFile, qFile.getName(), null).queue();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                if (!qFile.delete()) { // Delete the queue file after we're done
                                    qFile.deleteOnExit();
                                }
                            }
                        }
                        break;
                    case "skip":
                        if (isIdle(event.getTextChannel(), guild)) return;

                        if (isCurrentDj(event.getMember())) {
                            forceSkipTrack(guild, event.getTextChannel());
                        } else {
                            AudioInfo info = getTrackManager(guild).getTrackInfo(getPlayer(guild).getPlayingTrack());
                            if (info.hasVoted(event.getAuthor())) {
                                sendMessage(event.getTextChannel(), "\u26A0 You've already voted to skip this song!", 30);
                            } else {
                                int votes = info.getSkips();
                                if (votes >= 3) { // Skip on 4th vote
                                    getPlayer(guild).stopTrack();
                                    sendMessage(event.getTextChannel(), "\u23E9 Skipping current track.", 30);
                                } else {
                                    info.addSkip(event.getAuthor());
                                    sendMessage(event.getTextChannel(), "**" + UtilStrings.userDiscrimSet(event.getAuthor()) + "** has voted to skip this track! [" + (votes + 1) + "/4]", 30);
                                }
                            }
                        }
                        break;
                    case "now":
                    case "current":
                    case "nowplaying":
                    case "info": // Display song info
                        if (!hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null) { // No song is playing
                            sendMessage(event.getTextChannel(), "No song is being played at the moment! *It's your time to shine..*", 30);
                        } else {
                            AudioTrack track = getPlayer(guild).getPlayingTrack();
                            EmbedBuilder em = new EmbedBuilder().setTitle("Track Info").setDescription(String.format(QUEUE_DESCRIPTION, CD, getOrNull(track.getInfo().title),
                                    "\n\u23F1 **|>** `[ " + getTimestamp(track.getPosition()) + " / " + getTimestamp(track.getInfo().length) + " ]`",
                                    "\n" + MIC, getOrNull(track.getInfo().author),
                                    "\n\uD83C\uDFA7 **|>**  " + UtilStrings.userDiscrimSet(getTrackManager(guild).getTrackInfo(track).getAuthor().getUser()))).setFooter("WARNING: This feature may become part of a membership.");;
                            sendMessage(event.getTextChannel(), em.build(), 30);

                        }
                        break;
                    case "forceskip":
                        if (isIdle(event.getTextChannel(), guild)) return;

                        if (isCurrentDj(event.getMember()) || isDj(event.getMember())||event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                            forceSkipTrack(guild, event.getTextChannel());
                        } else {
                            sendMessage(event.getTextChannel(), "You don't have permission to do that!\nUse ``@CoinMan#3243 music skip`` to cast a vote!", 30);
                        }
                        break;
                    case "shuffle":
                        if (isIdle(event.getTextChannel(), guild)) return;

                        if (isCurrentDj(event.getMember()) || isDj(event.getMember())||event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                            getTrackManager(guild).shuffleQueue();
                            sendMessage(event.getTextChannel(), "\u2705 Shuffled the queue!", null);
                        } else {
                            sendMessage(event.getTextChannel(), "\u26D4 You don't have the permission to do that!", 30);
                        }
                        break;
                    case "stop":
                    case "pause":
                        if (!hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null) { // No song is playing
                            sendMessage(event.getTextChannel(), "No song is being played at the moment! *It's your time to shine..*", 30);
                            return;
                        }
                        if (isCurrentDj(event.getMember()) || isDj(event.getMember())||event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

                            if (getPlayer(event.getGuild()).isPaused()) {
                                sendMessage(event.getTextChannel(), "The track is already paused! Try unpausing it using ``play``", 30);
                            } else {
                                sendMessage(event.getTextChannel(), "Pausing the current track.", 30);
                                getPlayer(event.getGuild()).setPaused(true);
                            }
                        } else {
                            sendMessage(event.getTextChannel(), "You don't have the required permissions to do that! [DJ role]", 30);
                        }
                        break;
                    case "reconnect":
                        if (!hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null) { // No song is playing
                            sendMessage(event.getTextChannel(), "No song is being played at the moment! *It's your time to shine..*", 30);
                            return;
                        }
                        try {
                            if (event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()&&event.getMember().getVoiceState().inVoiceChannel()) {
                                sendMessage(event.getTextChannel(), "I'm already in a voice channel, " + event.getAuthor().getAsMention()+"!", 15);
                                return;
                            } else if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()&&event.getMember().getVoiceState().inVoiceChannel()) {
                                event.getGuild().getAudioManager().closeAudioConnection();
                                event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
                                sendMessage(event.getTextChannel(), "Reconnecting!", 15);
                                return;
                            }
                        }catch (NullPointerException e) {
                            sendMessage(event.getTextChannel(), "Something went wrong " + e.getLocalizedMessage() + "!", 30);
                        }
                        break;
                    case "play":
                        try {
                            if (isCurrentDj(event.getMember()) || isDj(event.getMember())||event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

                                if (!getPlayer(event.getGuild()).isPaused()) {
                                    sendMessage(event.getTextChannel(), "The track is already playing! Try pausing it using ``pause``", 30);
                                } else {
                                    sendMessage(event.getTextChannel(), "Playing the current track.", 30);
                                    getPlayer(event.getGuild()).setPaused(false);
                                }
                            } else sendMessage(event.getTextChannel(), "You don't have the required permissions to do that! [DJ role]", 30);
                        }catch (NullPointerException e) {
                            sendMessage(event.getTextChannel(), "Something went wrong, try again later :(", 15);
                        }
                        break;
                    case "reset":
                        try {
                            if (isCurrentDj(event.getMember()) || isDj(event.getMember())||event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                                reset(guild);
                                sendMessage(event.getTextChannel(), "\uD83D\uDD04 Resetting the music player..", 30);
                            } else {
                                sendMessage(event.getTextChannel(), "You don't have the required permissions to do that! [DJ role]", 30);
                            }
                        }catch (NullPointerException e) {
                            sendMessage(event.getTextChannel(), "Can't reset the music player because it's not playing anything!", 15);
                        }

                        break;
                    default:
                        sendMessage(event.getTextChannel(), help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).build(), 25);
                        return;
                }
                break;
            default:
                if (args[2].equalsIgnoreCase("play")) {
                    AudioSourceManagers.registerRemoteSources(myManager);
                    String input = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    input = "ytsearch: " + input;
                    loadTrack(input, event.getMember(), event.getMessage(), event.getTextChannel());
                    return;
                } else if (args[2].equalsIgnoreCase("volume")) {
                    if (isCurrentDj(event.getMember()) || isDj(event.getMember())||event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                        try {
                            int i = Integer.parseInt(args[3]);
                            if (i>100||i<1) {
                                sendMessage(event.getTextChannel(), "That is not a valid number! (1-100)", 30);
                                return;
                            }
                            getPlayer(event.getGuild()).setVolume(i);
                            sendMessage(event.getTextChannel(), "Set the volume of the current content to " + i + "%!", 60);
                        }catch (NumberFormatException e) {
                            sendMessage(event.getTextChannel(), "That is not a valid number! (1-100)", 30);
                            return;
                        }
                    } else sendMessage(event.getTextChannel(), "You don't have the required permissions to do that! [DJ role]", 30);
                    return;
                }
                sendMessage(event.getTextChannel(), help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).build(), 25);
                break;
        }
    }


    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (!players.containsKey(event.getGuild().getId()))
            return; //Guild doesn't have a music player

        TrackManager manager = getTrackManager(event.getGuild());
        manager.getQueuedTracks().stream()
                .filter(info -> !info.getTrack().equals(getPlayer(event.getGuild()).getPlayingTrack())
                        && info.getAuthor().getUser().equals(event.getMember().getUser()))
                .forEach(manager::remove);
        CommandListener.unregisterEvent(this);
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        reset(event.getGuild());
        CommandListener.unregisterEvent(this);
    }

    private void loadTrack(String identifier, Member author, Message msg, TextChannel chat) {
        if (author.getVoiceState().getChannel() == null) {
            chat.sendMessage("You are not in a Voice Channel!").queue();
            return;
        }
        Guild guild = author.getGuild();
        getPlayer(guild); // Make sure this guild has a player.

        msg.getTextChannel().sendTyping().queue();
        myManager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                EmbedBuilder em = new EmbedBuilder().setTitle(String.format(QUEUE_TITLE, UtilStrings.userDiscrimSet(author.getUser()), 1, "")).setDescription(String.format(QUEUE_DESCRIPTION, CD, getOrNull(track.getInfo().title), "", MIC, getOrNull(track.getInfo().author), "")).setFooter("WARNING: This feature may become part of a membership.");;
                chat.sendMessage(em.build()).queue();
                getTrackManager(guild).queue(track, author);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getSelectedTrack() != null) {
                    trackLoaded(playlist.getSelectedTrack());
                } else if (playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    EmbedBuilder em = new EmbedBuilder().setTitle(String.format(QUEUE_TITLE, UtilStrings.userDiscrimSet(author.getUser()))).setDescription(String.format(QUEUE_DESCRIPTION, DVD, getOrNull(playlist.getName()), "", "", "", "")).setFooter("WARNING: This feature may become part of a membership.");
                    chat.sendMessage(em.build()).queue();
                    for (int i = 0; i < Math.min(playlist.getTracks().size(), 200); i++) {
                        getTrackManager(guild).queue(playlist.getTracks().get(i), author);
                    }
                }
            }

            @Override
            public void noMatches() {
                chat.sendMessage("\u26A0 No playable tracks were found.").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                chat.sendMessage("\u26D4 " + exception.getLocalizedMessage()).queue();
            }
        });
    }

    @Override
    public void cancel() {

    }


    private boolean hasPlayer(Guild guild) {
        return players.containsKey(guild.getId());
    }

    private AudioPlayer getPlayer(Guild guild) {
        AudioPlayer p;
        if (hasPlayer(guild)) {
            p = players.get(guild.getId()).getKey();
        } else {
            p = createPlayer(guild);
        }
        return p;
    }

    private TrackManager getTrackManager(Guild guild) {
        return players.get(guild.getId()).getValue();
    }

    private AudioPlayer createPlayer(Guild guild) {
        AudioPlayer nPlayer = myManager.createPlayer();
        TrackManager manager = new TrackManager(nPlayer);
        nPlayer.addListener(manager);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(nPlayer));
        players.put(guild.getId(), new AbstractMap.SimpleEntry<>(nPlayer, manager));
        return nPlayer;
    }
    private void reset(Guild guild) {
        if (!players.containsKey(guild.getId())) {
            return;
        }
        players.remove(guild.getId());
        getPlayer(guild).destroy();
        getTrackManager(guild).purgeQueue();
        guild.getAudioManager().closeAudioConnection();
    }

    private String getOrNull(String s) {
        return s.isEmpty() ? "N/A" : s;
    }

    private String getTimestamp(long milis) {
        long seconds = milis / 1000;

        double hoursA = seconds / 3600;
        hoursA = Math.floor(hoursA);
        long hours = (long)hoursA;
        //long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        double minsA = seconds/60;
        minsA = Math.floor(minsA);
        long mins = (long)minsA;
        //long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }


    private String buildQueueMessage(AudioInfo info) {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String title = trackInfo.title;
        long length = trackInfo.length;
        return "`[ " + getTimestamp(length) + " ]` " + title + "\n";
    }

    private boolean isIdle(TextChannel chat, Guild guild) {
        if (!hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null) {
            sendMessage(chat, "No music is being played at the moment! :(", 30);
            return true;
        }
        return false;
    }

    private boolean isDj(Member member) {
        return member.getRoles().stream().anyMatch(r -> r.getName().equals("DJ"));
    }

    private boolean isCurrentDj(Member member) {
        return getTrackManager(member.getGuild()).getTrackInfo(getPlayer(member.getGuild()).getPlayingTrack()).getAuthor().equals(member);
    }

    private void forceSkipTrack(Guild guild, TextChannel chat) {
        getPlayer(guild).stopTrack();
        sendMessage(chat, "\u23E9 Skipping track!", 30);
    }

    private void tryToDelete(Message m) {
        if (m.getGuild().getSelfMember().hasPermission(m.getTextChannel(), Permission.MESSAGE_MANAGE)) {
            m.delete().queue();
        }
    }

}

/*
 * Copyright (c) 2019 Zavarov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package vartas.discord.bot.listener;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import vartas.chart.Interval;
import vartas.chart.line.DelegatingLineChart;
import vartas.discord.bot.entities.DiscordCommunicator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class keeps track of the activity in all guilds in the respective shard.
 */
public class ActivityListener extends ListenerAdapter implements Runnable{
    protected static final String AllChannels = "All Channels";
    protected static final String AllMembers = "All Members";
    protected static final String MembersOnline = "Members Online";
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The chart for all guilds in the current chart.
     */
    protected LoadingCache<Guild, DelegatingLineChart<Long>> charts;
    /**
     * The communicator over this shard.
     */
    protected DiscordCommunicator communicator;
    /**
     * Initializes an empty tracker.
     */
    public ActivityListener(DiscordCommunicator communicator){
        this.communicator = communicator;
        charts = CacheBuilder.newBuilder().build(CacheLoader.from((guild) -> {
            DelegatingLineChart<Long> chart;

            chart = new DelegatingLineChart<>(
                    (values) -> values.iterator().next(),
                    Duration.ofDays(7)
            );

            chart.setGranularity(ChronoUnit.MINUTES);
            chart.setStepSize(communicator.environment().config().getActivityUpdateInterval());
            chart.setInterval(Interval.MINUTE);
            chart.setTitle(String.format("Activity in '%s'", guild.getName()));
            chart.setXAxisLabel("Time");
            chart.setYAxisLabel("Count");

            return chart;
        }));
        log.info("Activity Tracker started.");
    }

    /**
     * Creates an image based on a set of data of the member and channel in this guild.
     * @param guild the guild to get the name.
     * @param channels all channels whose data also has to be plotted.
     * @return the image representing the data.
     */
    public JFreeChart create(Guild guild, Collection<TextChannel> channels){
        List<String> names = new ArrayList<>(channels.size() + 3);

        names.add(AllChannels);
        names.add(AllMembers);
        names.add(MembersOnline);

        channels.stream().map(GuildChannel::getName).forEach(names::add);

        return charts.getUnchecked(guild).create(names);
    }

    /**
     * Updates the total number of guild members and the number of which are online.
     */
    @Override
    public void run(){
        //If we'd use the guilds from the cache we skip servers where nobody has talked
        communicator.jda().getGuilds().forEach(this::update);
    }

    private void update(Guild guild){
        DelegatingLineChart<Long> chart = charts.getUnchecked(guild);

        long allMembers = guild.getMembers()
                .stream()
                .filter(m -> !m.getUser().isBot())
                .count();

        long membersOnline = guild.getMembers()
                .stream()
                .filter(m -> !m.getUser().isBot())
                .filter(m -> m.getOnlineStatus() != OnlineStatus.OFFLINE)
                .count();

        LocalDateTime now = LocalDateTime.now();
        chart.set(AllMembers, now, Collections.singleton(allMembers));
        chart.set(MembersOnline, now, Collections.singleton(membersOnline));


        log.info(String.format("%d total %s in %s", allMembers, English.plural("member", (int)allMembers), guild.getName()));
        log.info(String.format("%d %s online in %s", membersOnline, English.plural("member", (int)membersOnline), guild.getName()));
    }

    /**
     * Updates the counter for the channel the message was received in and the counter
     * for all received messages so far by one.
     * @param event the triggered event.
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot())
            return;

        DelegatingLineChart<Long> chart = charts.getUnchecked(event.getGuild());
        LocalDateTime date = LocalDateTime.now();

        String channelName = event.getMessage().getTextChannel().getName();

        //Can't use 'update' since there might be no values to update
        long allChannels = chart.get(AllChannels, date).stream().mapToLong(l -> l).findAny().orElse(0L);
        long channel = chart.get(channelName, date).stream().mapToLong(l -> l).findAny().orElse(0L);

        chart.set(AllChannels, date, Collections.singleton(allChannels + 1));
        chart.set(channelName, date, Collections.singleton(channel + 1));
    }
}
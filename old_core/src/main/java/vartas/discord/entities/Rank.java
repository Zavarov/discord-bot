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

package vartas.discord.entities;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * This class implements the internal permission system of the Discord bot.<br>
 * We usually distinguish between two types of permissions:
 * {@link net.dv8tion.jda.api.Permission Permissions} granted to an {@link User}
 * via {@link net.dv8tion.jda.api.entities.Role Role} or
 * {@link net.dv8tion.jda.api.entities.PermissionOverride PermissionOverride} in a single
 * {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}.<br>
 * This is relevant for all managing commands that should only be available to moderators. But
 * since those are restricted to {@link net.dv8tion.jda.api.entities.Guild Guilds}, they are not sufficient
 * to deal with commands only the developer should be able to execute.
 */
@Nonnull
public class Rank {
    /**
     * The internal multimap that maps the unique id of an {@link User} to all of its {@link Ranks Ranks}.
     */
    @Nonnull
    private SetMultimap<Long, Ranks> ranks = Multimaps.synchronizedSetMultimap(LinkedHashMultimap.create());

    /**
     * One might think that this method always returns true if the {@code user} has the {@link Ranks#ROOT Root} rank.
     * But this is not the case and has to be checked separately. This method checks if an user has the specific rank,
     * not if it has the permissions a user with this rank has.
     * @param key the {@link User} associated with the {@link Ranks Rank}
     * @param value the {@link Ranks Rank} associated with the {@link User}
     * @return true, if there is a {@link User} with the given {@link Ranks Rank}.
     */
    public boolean resolve(@Nonnull User key, @Nonnull Ranks value){
        return ranks.containsEntry(key.getIdLong(), value);
    }

    /**
     * Associates a {@link User} with a {@link Ranks Rank}.
     * @param key the {@link User} associated with the {@link Ranks Rank}
     * @param value the {@link Ranks Rank} associated with the {@link User}
     */
    public void add(@Nonnull User key, @Nonnull Ranks value){
        add(key.getIdLong(), value);
    }

    /**
     * Associates a {@link User} with a {@link Ranks Rank}.<br>
     * This method is required when loading the rank file, since it only contains the unique ids and not the
     * {@link User} instances.
     * @param key the unique user id associated with the {@link Ranks Rank}
     * @param value the {@link Ranks Rank} associated with the unique user id
     */
    public void add(long key, @Nonnull Ranks value){
        ranks.put(key, value);
    }

    /**
     * Removes the mapping for a key-value pair in the underlying multimap.
     * @param key the {@link User} associated with the {@link Ranks Rank}
     * @param value the {@link Ranks Rank} associated with the unique user id
     */
    public void remove(@Nonnull User key, @Nonnull Ranks value){
        remove(key.getIdLong(), value);
    }

    /**
     * Removes the mapping for a key-value pair in the underlying multimap.
     * This method is necessary when modifying the file via an MPI command.
     * @param key the unique user id associated with the {@link Ranks Rank}
     * @param value the {@link Ranks Rank} associated with the unique user id
     */
    public void remove(long key, @Nonnull Ranks value){
        ranks.remove(key, value);
    }

    /**
     * The returned maps contains all unique user ids and their ranks they are associated with.<br>
     * Note that the returned map only reflect the state of the multimap at the time this method was called. Further
     * modifications on internal map will not be represented. This is necessary to avoid additional synchronization,
     * since we are working in a parallel environment.
     * @return an unmodifiable copy of the underlying multimap.
     */
    @Nonnull
    public Multimap<Long, Ranks> get(){
        return Multimaps.unmodifiableSetMultimap(LinkedHashMultimap.create(ranks));
    }

    /**
     * A collection of all possible ranks.
     */
    @Nonnull
    public enum Ranks {
        /**
         * The root rank overwrites all other permissions. {@link User Users} with this rank should automatically have
         * all other ranks, as well as all Discord {@link net.dv8tion.jda.api.Permission Permissions}.<br>
         * As one can guess, this is a very powerful rank and should only be used when absolutely necessary.
         */
        ROOT("Root"),
        /**
         * The reddit rank should entitle the {@link User} to use commands that communicate with the Reddit API.
         * This restriction is necessary since those commands can be costly and allowing everyone to use them
         * might overstrain the program.
         */
        REDDIT("Reddit"),
        /**
         * The developer rank is directly below the {@link Ranks#ROOT Root} rank. {@link User Users} with this rank
         * should be able to execute all commands that are exclusive to developers, but not those that are restricted
         * to other ranks.
         * Note that they should <b>not</b> automatically have all Discord
         * {@link net.dv8tion.jda.api.Permission Permissions}, to avoid accidentally messing with
         * {@link net.dv8tion.jda.api.entities.Guild guilds}.
         */
        DEVELOPER("Developer");
        /**
         * The name identifying this rank in the rank file
         */
        @Nonnull
        private String name;

        /**
         * @param name the name identifying this rank in the rank file
         * @throws NullPointerException if {@code name} is null
         */
        Ranks(@Nonnull String name){
            this.name = name;
        }
        /**
         * @return the name identifying this rank in the rank file
         */
        @Nonnull
        public String getName(){
            return name;
        }
    }

    /**
     * The hook point for the visitor pattern.
     * @param visitor the visitor traversing through the rank
     */
    public void accept(@Nonnull Visitor visitor){
        visitor.handle(this);
    }

    /**
     * The rank visitor.
     */
    @Nonnull
    public interface Visitor {
        /**
         * The method that is invoked before the sub-nodes are handled.
         * @param rank the corresponding rank
         */
        default void visit(@Nonnull Rank rank){}

        /**
         * The method that is invoked to handle all sub-nodes.
         * @param rank the corresponding rank
         */
        default void traverse(@Nonnull Rank rank){}

        /**
         * The method that is invoked after the sub-nodes have been handled.
         * @param rank the corresponding rank
         */
        default void endVisit(@Nonnull Rank rank){}

        /**
         * The top method of the rank visitor, calling the visitor methods.
         * The order in which the methods are called is
         * <ul>
         *      <li>visit</li>
         *      <li>traverse</li>
         *      <li>endvisit</li>
         * </ul>
         * @param rank the corresponding rank
         */
        default void handle(@Nonnull Rank rank) {
            visit(rank);
            traverse(rank);
            endVisit(rank);
        }
    }

}
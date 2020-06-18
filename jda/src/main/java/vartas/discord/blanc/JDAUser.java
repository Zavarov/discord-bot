/*
 * Copyright (c) 2020 Zavarov
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

package vartas.discord.blanc;

import vartas.discord.blanc.factory.UserFactory;

import javax.annotation.Nonnull;

public class JDAUser extends User{
    @Nonnull
    public static User create(net.dv8tion.jda.api.entities.User user){
        //Users are of rank USER by default, unless they've been loaded from the JSON file
        return UserFactory.create(
                JDAUser::new,
                Rank.USER,
                user.getIdLong(),
                user.getName()
        );
    }
}

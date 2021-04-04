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

package zav.discord.blanc.mock;

import zav.discord.blanc._factory.MessageFactory;
import zav.discord.blanc.Message;
import zav.discord.blanc.User;

import java.time.Instant;

public class  MessageMock extends Message {
    public MessageMock(){}

    public MessageMock(int id, Instant created, User author){
        MessageFactory.create(() -> this,id, created, author);
    }
}
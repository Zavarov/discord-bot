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

package zav.discord.blanc._json;

import org.json.JSONArray;
import org.json.JSONObject;
import zav.discord.blanc.Guild;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.io._json.JSONCredentials;

import java.io.IOException;
import java.nio.file.Path;

public class JSONTextChannel extends JSONTextChannelTOP {
    public static String getFileName(TextChannel source){
        return getFileName(source.getId());
    }

    protected static String getFileName(long id){
        return "t" + Long.toUnsignedString(id) + ".json";
    }

    protected static Path getFileDirectory(Guild guild){
        return JSONCredentials.CREDENTIALS.getJsonDirectory().resolve(Long.toUnsignedString(guild.getId()));
    }

    public static TextChannel fromJson(TextChannel target, Guild guild, long id) throws IOException {
        Path filePath = getFileDirectory(guild).resolve(getFileName(id));
        return fromJson(target, filePath);
    }

    @Override
    protected void $fromSubreddits(JSONObject source, TextChannel target){
        JSONArray subreddits = source.optJSONArray(SUBREDDITS);
        if(subreddits != null)
            for(int i = 0 ; i < subreddits.length() ; ++i)
                target.addSubreddits(subreddits.getString(i));
    }

    @Override
    protected void $toSubreddits(TextChannel source, JSONObject target){
        JSONArray subreddits = new JSONArray();
        for(String subreddit : source.getSubreddits())
            subreddits.put(subreddit);
        target.put(SUBREDDITS, subreddits);
    }
}
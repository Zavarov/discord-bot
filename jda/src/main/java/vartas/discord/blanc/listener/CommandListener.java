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

package vartas.discord.blanc.listener;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import vartas.discord.blanc.MessageChannel;
import vartas.discord.blanc.command.Command;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public abstract class CommandListener extends ListenerAdapter {
    private ExecutorService executor = Executors.newWorkStealingPool();

    protected void submit(@Nonnull MessageChannel messageChannel, @Nonnull Supplier<Optional<? extends Command>> commandSupplier){
        try{
            Optional<? extends Command> commandOpt = commandSupplier.get();
            commandOpt.ifPresent(command -> {
                command.validate();
                executor.submit(command);
            });
        }catch(Exception e){
            messageChannel.send(e);
        }
    }
}

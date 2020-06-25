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

package vartas.discord.blanc.parser;

import vartas.discord.blanc.*;
import vartas.discord.blanc.command.Command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

/**
 * The {@link IntermediateCommand} generated by the parser is only able to store the argument as either a {@link String}
 * or {@link Number}. In order to allow more complex data types as arguments for the {@link Command Commands},
 * this class is able to further transform those {@link Argument Arguments} prior to the execution of the
 * associated {@link Command}.
 * @see StringArgument
 * @see MentionArgument
 * @see ExpressionArgument
 */
@Nonnull
public abstract class AbstractTypeResolver extends AbstractTypeResolverTOP{
    @Nullable
    protected Guild guild;
    @Nullable
    protected TextChannel textChannel;

    public AbstractTypeResolver(@Nullable Guild guild, @Nullable TextChannel textChannel){
        this.guild = guild;
        this.textChannel = textChannel;
    }

    public AbstractTypeResolver(){
        this(null, null);
    }

    /**
     * Attempts to transform the provided {@link Argument} into a {@link String}.<br>
     * This is done by using the interval value of the {@link StringArgument}.
     * @param argument the {@link Argument} associated with the {@link String}.
     * @return the {@link String} associated with the {@link Argument}.
     * @throws NoSuchElementException if the {@link Argument} can't be resolved as a {@link String}.
     */
    @Override
    @Nonnull
    public String resolveString(@Nonnull Argument argument) throws NoSuchElementException {
        return new StringResolver().apply(argument).orElseThrow();
    }
    /**
     * Attempts to transform the provided {@link Argument} into a {@link LocalDate}.<br>
     * This is done by parsing the {@link String} value of the {@link StringArgument} using the default
     * formatter of {@link LocalDate}.
     * @see DateTimeFormatter#ISO_LOCAL_DATE
     * @param argument the {@link Argument} associated with the {@link LocalDate}.
     * @return the {@link LocalDate} associated with the {@link Argument}.
     * @throws NoSuchElementException if the {@link Argument} can't be resolved as a {@link LocalDate}.
     */
    @Override
    @Nonnull
    public LocalDate resolveLocalDate(@Nonnull Argument argument) throws NoSuchElementException{
        return new LocalDateResolver().apply(argument).orElseThrow();
    }
    /**
     * Attempts to transform the provided {@link Argument} into a {@link BigDecimal}.<br>
     * This is done by using the interval value of the {@link ExpressionArgument}.
     * @param argument the {@link Argument} associated with the {@link BigDecimal}.
     * @return the {@link BigDecimal} associated with the {@link Argument}.
     * @throws NoSuchElementException if the {@link Argument} can't be resolved as a {@link BigDecimal}.
     */
    @Override
    @Nonnull
    public BigDecimal resolveBigDecimal(@Nonnull Argument argument) throws NoSuchElementException{
        return new BigDecimalResolver().apply(argument).orElseThrow();
    }
    /**
     * Attempts to transform the provided {@link Argument} into a {@link GuildModule}.<br>
     * This is done by using the interval value of the {@link ExpressionArgument}.
     * @param argument the {@link Argument} associated with the {@link GuildModule}.
     * @return the {@link GuildModule} associated with the {@link Argument}.
     * @throws NoSuchElementException if the {@link Argument} can't be resolved as a {@link GuildModule}.
     */
    @Override
    @Nonnull
    public GuildModule resolveGuildModule(@Nonnull Argument argument) throws NoSuchElementException{
        return new GuildModuleResolver().apply(argument).orElseThrow();
    }
}

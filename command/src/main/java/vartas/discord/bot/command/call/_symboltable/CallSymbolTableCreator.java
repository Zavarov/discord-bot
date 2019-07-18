package vartas.discord.bot.command.call._symboltable;

import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import vartas.discord.bot.command.call._ast.ASTCallArtifact;
import vartas.discord.bot.command.command._symboltable.CommandSymbol;
import vartas.discord.bot.command.entity._ast.*;
import vartas.discord.bot.command.parameter._symboltable.*;

/*
 * Copyright (C) 2019 Zavarov
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
public class CallSymbolTableCreator extends CallSymbolTableCreatorTOP{
    protected int index;
    protected CommandSymbol command;

    public CallSymbolTableCreator(ResolvingConfiguration resolvingConfig, Scope enclosingScope) {
        super(resolvingConfig, enclosingScope);
    }

    public void setCommandSymbol(CommandSymbol command){
        this.command = command;
    }

    @Override
    public void handle(ASTCallArtifact node){
        node.setEnclosingScopeOpt(currentScope());

        for(index = 0 ; index < node.getParameterList().size() ; ++index){
            node.getParameter(index).accept(getRealThis());
        }
    }

    @Override
    public void visit(ASTDateType node){
        DateSymbol symbol = new DateSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTMessageType node){
        MessageSymbol symbol = new MessageSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTGuildType node){
        GuildSymbol symbol = new GuildSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTMemberType node){
        MemberSymbol symbol = new MemberSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTUserType node){
        UserSymbol symbol = new UserSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTTextChannelType node){
        TextChannelSymbol symbol = new TextChannelSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTRoleType node){
        RoleSymbol symbol = new RoleSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTExpressionType node){
        ExpressionSymbol symbol = new ExpressionSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTStringType node){
        StringSymbol symbol = new StringSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTOnlineStatusType node){
        OnlineStatusSymbol symbol = new OnlineStatusSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTIntervalType node){
        IntervalSymbol symbol = new IntervalSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node);
        addToScopeAndLinkWithNode(symbol, node);
    }
}

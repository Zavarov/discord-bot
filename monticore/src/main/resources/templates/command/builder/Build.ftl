${signature("cdMethod")}
<#assign name = cdMethod.getCDParameter(0)>
<#assign arguments = cdMethod.getCDParameter(1)>
        return Optional.ofNullable(commands.getOrDefault(${name.getName()}, null))<#rt>
            .map(_function -> _function.apply(${arguments.getName()}));<#lt>
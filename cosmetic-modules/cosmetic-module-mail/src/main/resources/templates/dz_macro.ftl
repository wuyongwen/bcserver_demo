<#--
 * message
 *
 * Macro to translate a message code into a message
 -->
<#macro message code>${resources.getMessage(code)}</#macro>

<#macro messageArgs code, args>${resources.getMessage(code,args)}</#macro>

package open.v0gdump.recontent.rule

data class RootParser(
    val rootSelector: String,
    val textNodeRule: TextNodeRule,
    val rules: List<Rule>
)
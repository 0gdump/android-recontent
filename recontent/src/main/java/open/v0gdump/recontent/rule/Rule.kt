package open.v0gdump.recontent.rule

data class Rule(
    val selector: String,
    val callback: RuleCallback,
    val tag: String? = null
)
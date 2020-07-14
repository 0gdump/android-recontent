package open.v0gdump.recontent.model

data class SectionRule(
    val selector: String,
    val childRules: List<NodeRule>,
    val specificNodesHandler: SpecificNodesHandler? = null
)
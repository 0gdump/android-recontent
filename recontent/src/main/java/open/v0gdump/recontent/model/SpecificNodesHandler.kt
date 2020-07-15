package open.v0gdump.recontent.model

import org.jsoup.nodes.*

data class SpecificNodesHandler(
    val textNodeHandler: ((textNode: TextNode) -> Unit)? = null,
    val xmlDeclarationHandler: ((xmlDeclaration: XmlDeclaration) -> Unit)? = null,
    val documentTypeHandler: ((document: DocumentType) -> Unit)? = null,
    val dataNodeHandler: ((dataNode: DataNode) -> Unit)? = null,
    val commentHandler: ((comment: Comment) -> Unit)? = null
)
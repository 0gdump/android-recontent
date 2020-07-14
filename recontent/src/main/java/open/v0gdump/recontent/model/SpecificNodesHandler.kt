package open.v0gdump.recontent.model

import org.jsoup.nodes.*

abstract class SpecificNodesHandler {
    abstract fun textNodeHandler(textNode: TextNode)
    abstract fun xmlDeclarationHandler(xmlDeclaration: XmlDeclaration)
    abstract fun documentTypeHandler(document: DocumentType)
    abstract fun dataNodeHandler(dataNode: DataNode)
    abstract fun commentHandler(comment: Comment)
}
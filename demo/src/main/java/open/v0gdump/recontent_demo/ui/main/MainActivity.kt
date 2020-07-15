package open.v0gdump.recontent_demo.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import open.v0gdump.recontent.ReContent
import open.v0gdump.recontent.ReContentEvents
import open.v0gdump.recontent.model.NodeRule
import open.v0gdump.recontent.model.SectionRule
import open.v0gdump.recontent.model.SpecificNodesHandler
import open.v0gdump.recontent_demo.R
import open.v0gdump.recontent_demo.model.Item
import open.v0gdump.recontent_demo.model.ItemImage
import open.v0gdump.recontent_demo.model.ItemText
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

class MainActivity : AppCompatActivity() {

    private val data = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val eventsHandler = ReContentEvents(
            beforeParse = { _, doc ->
                title = doc.title()
            },
            afterParse = {
                recycler.setHasFixedSize(true)
                recycler.setItemViewCacheSize(20)

                recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                recycler.adapter = MainAdapter(data)
            }
        )

        setContentView(R.layout.activity_main)
        ReContent(this, eventsHandler).apply {
            sectionsRules = createRules()
            load("https://varlamov.ru/3685280.html")
        }
    }

    private fun createRules(): List<SectionRule> = listOf(
        SectionRule(
            selector = "div#entrytext.j-e-text",
            childRules = listOf(
                NodeRule(
                    selector = "span.j-imagewrapper",
                    callback = { e, t -> buildImage(e, t) }
                )
            ),
            specificNodesHandler = SpecificNodesHandler(
                textNodeHandler = { node -> buildText(node) }
            )
        )
    )

    //region ReContent Callbacks

    private fun buildImage(element: Element, tag: String?) {
        val source = element.select("img").attr("src")

        data.add(ItemImage(source = source))
    }

    private fun buildText(node: TextNode) {
        data.add(ItemText(content = node.text()))
    }

    //endregion
}

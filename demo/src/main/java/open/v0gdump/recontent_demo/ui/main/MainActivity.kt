package open.v0gdump.recontent_demo.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import open.v0gdump.recontent.ReContent
import open.v0gdump.recontent.callback.ParserCallback
import open.v0gdump.recontent.callback.WebCallback
import open.v0gdump.recontent.rule.RootParser
import open.v0gdump.recontent.rule.Rule
import open.v0gdump.recontent.rule.Rules
import open.v0gdump.recontent.rule.TextNodeRule
import open.v0gdump.recontent_demo.R
import open.v0gdump.recontent_demo.model.Item
import open.v0gdump.recontent_demo.model.ItemImage
import open.v0gdump.recontent_demo.model.ItemText
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

class MainActivity : AppCompatActivity() {

    private val data = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webCallback = object : WebCallback {

            override fun onNavigate(url: String) {

            }

            override fun onDocumentLoadStarted(url: String) {

            }

            override fun onDocumentLoadFinished(url: String) {

            }

            override fun onDocumentReady(url: String, doc: Document) {
                title = doc.title()
            }
        }

        val parserCallback = object : ParserCallback {

            override fun onParserStart() {

            }

            override fun onParserEnd() {
                val adapter = MainAdapter(data)

                recycler.setHasFixedSize(true)
                recycler.setItemViewCacheSize(20)

                recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                recycler.adapter = adapter
            }
        }

        ReContent()
            .init(this, webCallback)
            .setupRules({ setupRules(it) }, parserCallback)
            .load("https://varlamov.ru/3685280.html")
    }

    private fun setupRules(rules: Rules) {
        rules.add(
            RootParser(
                rootSelector = "div#entrytext.j-e-text",
                textNodeRule = TextNodeRule(callback = { tn -> buildText(tn) }),
                rules = listOf(
                    Rule(
                        selector = "span.j-imagewrapper",
                        callback = { e, t -> buildImage(e, t) }
                    )
                )
            )
        )
    }

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
